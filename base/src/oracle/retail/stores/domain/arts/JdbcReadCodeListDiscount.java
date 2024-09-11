/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadCodeListDiscount.java /main/17 2014/03/19 11:37:18 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  03/14/14 - Throwing checked exceptions for fallback on remoteDT
 *                         to avoid NPE
 *    mjwallac  12/19/13 - fix POS null dereferences (part 1)
 *    abondala  05/01/13 - convert primitive types to objects before setting
 *                         the bind variables.
 *    abondala  05/01/13 - replace dynamic sql with bind variables.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    ohorne    12/23/08 - now uses new Statement object to generate ResultSet
 *                         b/c method accessed while still reading ResultSet
 *                         obtained from JdbcDataConnection's statement
 *    mdecama   11/04/08 - Added retrieval and saving of the deprecated db
 *                         column for text. It requires a non-null value for
 *                         backwards compatibility
 *    ohorne    11/04/08 - removed reliance on scrollable ResultSet
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *    acadar    11/02/08 - changes to read the localized reason codes for
 *                         customer groups and store coupons
 *    acadar    10/31/08 - fixes for retrieving the reason codes for advanced
 *                         pricing rules
 *    acadar    10/31/08 - added check for null LocalizedCodeIfc
 *    acadar    10/31/08 - fixes to distinguish between manual item discounts
 *                         and markdowns
 *    acadar    10/31/08 - fixes
 *    mdecama   10/17/08 - Fixed problem populating the CodeList
 *    mdecama   10/15/08 - Retrieves a CodeList for Discounts
 * =========================================================================== */
package oracle.retail.stores.domain.arts;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import oracle.retail.stores.common.sql.SQLParameter;
import oracle.retail.stores.common.sql.SQLParameterValue;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.CodeListSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CodeSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.util.DBUtils;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This Data Operation retrieves a Code List for Manual Discounts
 */
public class JdbcReadCodeListDiscount extends JdbcDataOperation implements ARTSDatabaseIfc, CodeConstantsIfc,
        DiscountRuleConstantsIfc
{
    /**
     * Generated serialVersionUID
     */
    private static final long serialVersionUID = -8427546114961221352L;

    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcReadCodeListDiscount.class);

    /**
     * Discount Group Name
     */
    private static final String DISCOUNT_GROUP_NAME = "Discount";

    /**
     * Executes the SQL statements against the database.
     * <P>
     *
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCodeListDiscount.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        if (action.getDataObject() instanceof CodeSearchCriteriaIfc)
        {
            CodeSearchCriteriaIfc criteria = (CodeSearchCriteriaIfc)action.getDataObject();
            LocalizedCodeIfc code = readCode(connection, criteria);
            dataTransaction.setResult(code);
        }
        else if (action.getDataObject() instanceof CodeListSearchCriteriaIfc)
        {
            CodeListSearchCriteriaIfc criteria = (CodeListSearchCriteriaIfc)action.getDataObject();
            if (criteria.getSearchType() == CodeSearchCriteriaIfc.SEARCH_CODE_LIST)
            {
                CodeListIfc codeList = readCodeList(connection, criteria);
                dataTransaction.setResult(codeList);
            }

            if (criteria.getSearchType() == CodeSearchCriteriaIfc.SEARCH_CODE_LIST_ID)
            {
                // This can be a chain call. Get the Results
                List<String> chainedCodeListIDs = null;
                if ((dataTransaction.getResult() != null) && (dataTransaction.getResult() instanceof Vector))
                {
                    chainedCodeListIDs = (Vector<String>)dataTransaction.getResult();
                }

                List<String> codeListIDs = readCodeListIDs(connection, criteria);

                // Concatenate Results
                if (chainedCodeListIDs != null)
                {
                    if (codeListIDs != null)
                    {
                        codeListIDs.addAll(chainedCodeListIDs);
                        dataTransaction.setResult((Vector)codeListIDs);
                    }
                    else
                    {
                        dataTransaction.setResult((Vector)chainedCodeListIDs);
                    }
                }
                else
                {
                    dataTransaction.setResult((Vector)codeListIDs);
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCodeListDiscount.execute()");
    }

    /**
     * Reads a CodeList
     *
     * @param connection
     * @param criteria
     * @return
     * @throws DataException
     */
    public CodeListIfc readCodeList(JdbcDataConnection dataConnection, CodeListSearchCriteriaIfc criteria)
            throws DataException
    {
        CodeListIfc codeList = null;

        try
        {
            SQLSelectStatement sql = null;
            if (!criteria.getStoreID().equals(STORE_ID_CORPORATE))
            {
                sql = buildCodeListSQLStatement(criteria);
                dataConnection.execute(sql.getSQLString(), sql.getParameterValues());
                codeList = parseCodeListResultSet(dataConnection, criteria.getStoreID(), criteria.getListID());
            }

            // If there is no CodeList for the Store, read from CORP
            if (codeList == null)
            {
                criteria.setStoreID(STORE_ID_CORPORATE);
                sql = buildCodeListSQLStatement(criteria);
                dataConnection.execute(sql.getSQLString(), sql.getParameterValues());
                codeList = parseCodeListResultSet(dataConnection, criteria.getStoreID(), criteria.getListID());
            }
            
            if(null == codeList)
            {
                throw new DataException(DataException.NO_DATA, "JdbcReadCodeListDiscount.parseCodeListResultSet");
            }
            
        }
        catch (SQLException se)
        {
            logger.error("JdbcReadCodeListDiscount.readCodeList", se);
            throw new DataException(DataException.SQL_ERROR, "JdbcReadCodeListDiscount.readCodeList", se);
        }
        catch (DataException de)
        {
            logger.error("JdbcReadCodeListDiscount.readCodeList", de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error("JdbcReadCodeListDiscount.readCodeList", e);
            throw new DataException(DataException.UNKNOWN, "JdbcReadCodeListShippingMethod.readCodeList", e);
        }

        return codeList;
    }

    /**
     * Builds a SQL Statement for retrieval of a CodeList
     *
     * @param criteria
     * @return
     * @throws SQLException
     */
    protected SQLSelectStatement buildCodeListSQLStatement(CodeListSearchCriteriaIfc criteria) throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_PRICE_DERIVATION_RULE, TABLE_PRICE_DERIVATION_RULE);
        sql.addTable(TABLE_PRICE_DERIVATION_RULE_I8, TABLE_PRICE_DERIVATION_RULE_I8);

        sql.setDistinctFlag(true);

        sql.addColumn(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_ID);
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_NAME); // For backwards compatibility
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_REASON_CODE);
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_STATUS_CODE);
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE, FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG);
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE, FIELD_CODE_LIST_ENTRY_SORT_INDEX);
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE_I8, FIELD_LOCALE);
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE_I8, FIELD_PRICE_DERIVATION_RULE_NAME);

        buildDiscountQualifier(sql, criteria);

        sql.addOrdering(TABLE_PRICE_DERIVATION_RULE, FIELD_CODE_LIST_ENTRY_SORT_INDEX);
        sql.addOrdering(TABLE_PRICE_DERIVATION_RULE_I8, FIELD_PRICE_DERIVATION_RULE_NAME);

        sql.addTable(TABLE_PRICE_DERIVATION_RULE);

        return (sql);
    }

    /**
     * Builds the discount qualifier
     *
     * @param sql
     * @param criteria
     */
    protected void buildDiscountQualifier(SQLSelectStatement sql, CodeListSearchCriteriaIfc criteria)
    { // begin getDiscountCodeListName()
        String listId = criteria.getListID();

        if (listId.equals(CODE_LIST_PREFERRED_CUSTOMER_DISCOUNT))
        {
            sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE,
                    new Integer(ASSIGNMENT_CUSTOMER)));
        }
        else
        {
            sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE,
                    new Integer(ASSIGNMENT_MANUAL)));

            if (listId.equals(CODE_LIST_TRANSACTION_DISCOUNT_BY_PERCENTAGE))
            {
                sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_SCOPE_CODE,
                        new Integer(DISCOUNT_SCOPE_TRANSACTION)));
                sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_METHOD_CODE,
                        new Integer(DISCOUNT_METHOD_PERCENTAGE)));
            }
            else if (listId.equals(CODE_LIST_TRANSACTION_DISCOUNT_BY_AMOUNT))
            {
                sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_SCOPE_CODE,
                        new Integer(DISCOUNT_SCOPE_TRANSACTION)));
                sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_METHOD_CODE,
                        new Integer(DISCOUNT_METHOD_AMOUNT)));
            }
            else if (listId.equals(CODE_LIST_ITEM_DISCOUNT_BY_PERCENTAGE))
            {
                sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_SCOPE_CODE,
                        new Integer(DISCOUNT_SCOPE_ITEM)));
                sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_METHOD_CODE,
                        new Integer(DISCOUNT_METHOD_PERCENTAGE)));
            }
            else if (listId.equals(CODE_LIST_ITEM_DISCOUNT_BY_AMOUNT))
            {
                sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_SCOPE_CODE,
                        new Integer(DISCOUNT_SCOPE_ITEM)));
                sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_METHOD_CODE,
                        new Integer(DISCOUNT_METHOD_AMOUNT)));
            }
        }

        if (!Util.isEmpty(criteria.getStoreID()))
            sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_RETAIL_STORE_ID, criteria.getStoreID()));

        sql.addJoinQualifier(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_ID,
                TABLE_PRICE_DERIVATION_RULE_I8, FIELD_PRICE_DERIVATION_RULE_ID);
        sql.addQualifier(FIELD_LOCALE + " "
                + buildINClauseString(LocaleMap.getBestMatch("", criteria.getLocaleRequestor().getLocales())));

    }

    /**
     * Parses result set and creates a CodeListIfc object.
     *
     * @param dataConnection data connection
     * @exception SQLException thrown if result set cannot be parsed
     * @exception DataException thrown if no records in result set
     */
    protected CodeListIfc parseCodeListResultSet(JdbcDataConnection dataConnection, String storeID, String listID)
            throws SQLException, DataException
    {
        CodeListIfc codeList = null;
        boolean isFirst = true;
        ResultSet rs = (ResultSet)dataConnection.getResult();

        boolean enabled = false;

        if (rs != null)
        {
            CodeEntryIfc codeEntry = null;
            LocalizedTextIfc localizedText = null;

            try
            {
                while (rs.next())
                {
                    int index = 0;
                    if (codeList == null)
                    {
                        // Initialize the CodeList
                        codeList = DomainGateway.getFactory().getCodeListInstance();
                        codeList.setStoreID(storeID);
                        codeList.setListDescription(listID);
                        codeList.setGroupName(DISCOUNT_GROUP_NAME);
                        codeList.setNumericCodes(false);
                    }

                    String ruleID = getSafeString(rs, ++index);
                    String codeName = getSafeString(rs, ++index);
                    String code = getSafeString(rs, ++index);
                    String status = getSafeString(rs, ++index);
                    boolean defaultFlag = getBooleanFromString(rs, ++index);
                    int sortIndex = rs.getInt(++index);
                    String locale = getSafeString(rs, ++index);
                    String text = getSafeString(rs, ++index);

                    // If we have already loaded a specific code entry, we only
                    // update the localized text
                    codeEntry = codeList.findListEntryByCode(code, false);
                    if (codeEntry == null)
                    {
                        if (status.equals(STATUS_DESCRIPTORS[STATUS_ACTIVE]))
                        {
                            enabled = true;
                        }
                        localizedText = DomainGateway.getFactory().getLocalizedText();
                        localizedText.putText(LocaleUtilities.getLocaleFromString(locale), text);
                        codeList.addEntry(localizedText, code, codeName, sortIndex, enabled, ruleID);

                    }
                    else
                    {
                        codeEntry.getLocalizedText().putText(LocaleUtilities.getLocaleFromString(locale), text);
                    }

                    // set default if needed
                    if (defaultFlag)
                    {
                        codeList.setDefaultCodeString(code);
                    }
                }
            }
            finally
            {
                rs.close();
            }
        }
        return codeList;
    }

    /**
     * Retrieves the CodeList IDs for this Table. We are hardcoding them here
     * because these are the only ones handle in this table.
     *
     * @param connection
     * @param criteria
     * @return
     * @throws DataException
     * @throws SQLException
     */
    public List<String> readCodeListIDs(JdbcDataConnection dataConnection, CodeListSearchCriteriaIfc criteria)
    {
        List<String> codeListIDs = new Vector<String>(5);
        codeListIDs.add(CODE_LIST_PREFERRED_CUSTOMER_DISCOUNT);
        codeListIDs.add(CODE_LIST_TRANSACTION_DISCOUNT_BY_PERCENTAGE);
        codeListIDs.add(CODE_LIST_TRANSACTION_DISCOUNT_BY_AMOUNT);
        codeListIDs.add(CODE_LIST_ITEM_DISCOUNT_BY_PERCENTAGE);
        codeListIDs.add(CODE_LIST_ITEM_DISCOUNT_BY_AMOUNT);

        return codeListIDs;
    }

    /**
     * Reads a LocalizedCode
     *
     * @param connection
     * @param criteria
     * @return
     * @throws DataException
     * @throws SQLException
     */
    public LocalizedCodeIfc readCode(JdbcDataConnection dataConnection, CodeSearchCriteriaIfc criteria)
            throws DataException
    {
        LocalizedCodeIfc code = null;
        SQLSelectStatement sql = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            
            // First Read the CodeList from the Store
            if (!criteria.getStoreID().equals(STORE_ID_CORPORATE))
            {
                sql = buildCodeSQLStatement(criteria);            
                
                ps =  dataConnection.getConnection().prepareStatement(sql.getSQLString());
                addParameter(ps, sql.getParameterValues());
                
                rs = ps.executeQuery();
                code = parseCodeResultSet(rs);
                
                DBUtils.getInstance().closeResultSet(rs);
                DBUtils.getInstance().closeStatement(ps);
            }

            // If there is no CodeList for the Store, read from CORP
            if (code == null)
            {
                criteria.setStoreID(STORE_ID_CORPORATE);
                sql = buildCodeSQLStatement(criteria);
                
                ps =  dataConnection.getConnection().prepareStatement(sql.getSQLString());
                addParameter(ps, sql.getParameterValues());
                
                rs = ps.executeQuery();
                code = parseCodeResultSet(rs);
                
                DBUtils.getInstance().closeResultSet(rs);
                DBUtils.getInstance().closeStatement(ps);
            }
            
            if(null == code)
            {
                throw new DataException(DataException.NO_DATA, "JdbcReadCodeListDiscount.parseCodeResultSet");
            }
        }
        catch (SQLException se)
        {
            logger.error("JdbcReadCodeListDiscount.readCode", se);
            throw new DataException(DataException.SQL_ERROR, "JdbcReadCodeListDiscount.readCode", se);
        }
        catch (DataException de)
        {
            logger.error("JdbcReadCodeListDiscount.readCode", de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error("JdbcReadCodeListDiscount.readCode", e);
            throw new DataException(DataException.UNKNOWN, "JdbcReadCodeListShippingMethod.readCode", e);
        }
        finally
        {
            DBUtils.getInstance().closeResultSet(rs);
            DBUtils.getInstance().closeStatement(ps);
        }

        return code;
    }

    /**
     * Builds a SQL Statement to retrieve a Localized Discount
     *
     * @param criteria
     * @return
     * @throws SQLException
     */
    protected SQLSelectStatement buildCodeSQLStatement(CodeSearchCriteriaIfc criteria) throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_PRICE_DERIVATION_RULE, TABLE_PRICE_DERIVATION_RULE);
        sql.addTable(TABLE_PRICE_DERIVATION_RULE_I8, TABLE_PRICE_DERIVATION_RULE_I8);

        sql.setDistinctFlag(true);

        sql.addColumn(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_REASON_CODE);
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE_I8, FIELD_LOCALE);
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE_I8, FIELD_PRICE_DERIVATION_RULE_NAME);

        if (!Util.isEmpty(criteria.getCode()))
            sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_REASON_CODE, criteria.getCode()));
        
        buildDiscountQualifier(sql, criteria);

        return (sql);
    }

    /**
     * Parses result set and returns a LocalizedCodeIfc object.
     *
     * @param rs the ResultSet
     * @return LocalizedCode
     * @exception SQLException thrown if result set cannot be parsed
     * @exception DataException thrown if no records in result set
     */
    // ---------------------------------------------------------------------
    protected LocalizedCodeIfc parseCodeResultSet(ResultSet rs) throws SQLException, DataException
    {
        LocalizedCodeIfc localizedCode = null;
        boolean found = false;
        if (rs != null)
        {
            try
            {

                while (rs.next())
                {

                    int index = 0;
                    if (!found)
                    {
                        found = true;
                        localizedCode = DomainGateway.getFactory().getLocalizedCode();
                    }

                    String code = (getSafeString(rs, ++index));
                    String locale = getSafeString(rs, ++index);
                    String text = getSafeString(rs, ++index);

                    if (localizedCode != null)
                    {
                        localizedCode.setCode(code);
                        localizedCode.putText(LocaleUtilities.getLocaleFromString(locale), text);
                    }
                }
            }
            finally
            {
                rs.close();

            }
        }
        return localizedCode;
    }


    /**
     * Reads a LocalizedCode
     *
     * @param connection
     * @param criteria
     * @return
     * @throws DataException
     * @throws SQLException
     */
    public LocalizedCodeIfc readAdvancedPricingRuleCode(JdbcDataConnection dataConnection, CodeSearchCriteriaIfc criteria)
            throws DataException
    {
        LocalizedCodeIfc code = null;
        SQLSelectStatement sql = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try
        {
            // First Read the CodeList from the Store
            if (!criteria.getStoreID().equals(STORE_ID_CORPORATE))
            {
                sql = buildAdvancedPricingCodeSQLStatement(criteria);
                
                ps =  dataConnection.getConnection().prepareStatement(sql.getSQLString());
                addParameter(ps, sql.getParameterValues());
                
                rs = ps.executeQuery();
                code = parseAdvancedPricingCodeResultSet(rs);
                
                DBUtils.getInstance().closeResultSet(rs);
                DBUtils.getInstance().closeStatement(ps);
            }

            // If there is no CodeList for the Store, read from CORP
            if (code == null)
            {
                criteria.setStoreID(STORE_ID_CORPORATE);
                sql = buildAdvancedPricingCodeSQLStatement(criteria);
                
                ps =  dataConnection.getConnection().prepareStatement(sql.getSQLString());
                addParameter(ps, sql.getParameterValues());
                
                rs = ps.executeQuery();
                code = parseAdvancedPricingCodeResultSet(rs);

                DBUtils.getInstance().closeResultSet(rs);
                DBUtils.getInstance().closeStatement(ps);

            }
            
            if(null == code)
            {
                throw new DataException(DataException.NO_DATA, "JdbcReadCodeListDiscount.parseAdvancedPricingCodeResultSet");
            }
            
        }
        catch (SQLException se)
        {
            logger.error("JdbcReadCodeListDiscount.readCode", se);
            throw new DataException(DataException.SQL_ERROR, "JdbcReadCodeListDiscount.readCode", se);
        }
        catch (DataException de)
        {
            logger.error("JdbcReadCodeListDiscount.readCode", de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error("JdbcReadCodeListDiscount.readCode", e);
            throw new DataException(DataException.UNKNOWN, "JdbcReadCodeListShippingMethod.readCode", e);
        }
        finally
        {
            DBUtils.getInstance().closeResultSet(rs);
            DBUtils.getInstance().closeStatement(ps);
        }
        return code;
    }
    
    private void addParameter(PreparedStatement preparedStatement, List<Object> parameters) throws SQLException
    {
        int index = 0;
        for (Object parameter : parameters)
        {
            index++;
            SQLParameter.setParameter(preparedStatement, index, parameter);    
        }
    }    


    /**
     *
     * @param criteria
     * @return
     */
    private SQLSelectStatement buildAdvancedPricingCodeSQLStatement(CodeSearchCriteriaIfc criteria)
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables

        sql.addTable(TABLE_PRICE_DERIVATION_RULE, TABLE_PRICE_DERIVATION_RULE);
        sql.addTable(TABLE_PRICE_DERIVATION_RULE_I8, TABLE_PRICE_DERIVATION_RULE_I8);

        sql.setDistinctFlag(true);

        sql.addColumn(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_REASON_CODE);
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE_I8, FIELD_LOCALE);
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE_I8, FIELD_PRICE_DERIVATION_RULE_NAME);

        sql.addJoinQualifier(TABLE_PRICE_DERIVATION_RULE_I8, FIELD_PRICE_DERIVATION_RULE_ID,
                TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_ID);

        sql.addJoinQualifier(TABLE_PRICE_DERIVATION_RULE, FIELD_RETAIL_STORE_ID, TABLE_PRICE_DERIVATION_RULE_I8, FIELD_RETAIL_STORE_ID);

        //add qualifier for locale
        Set<Locale> bestMatches = LocaleMap.getBestMatch("", criteria.getLocaleRequestor().getLocales());
        sql.addQualifier(TABLE_PRICE_DERIVATION_RULE_I8 + "." + FIELD_LOCALE + " " +  JdbcDataOperation.buildINClauseString(bestMatches));
        
        if (!Util.isEmpty(criteria.getRuleID()))
            sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_ID, criteria.getRuleID()));
        
        if (!Util.isEmpty(criteria.getStoreID()))
            sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_RETAIL_STORE_ID, criteria.getStoreID()));
        
        if (!Util.isEmpty(criteria.getCode()))
            sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_REASON_CODE, criteria.getCode()));

        return sql;
    }

    /**
     * Parses result set and returns a LocalizedCodeIfc object.
     *
     * @param rs the ResultSet
     * @return LocalizedCode
     * @exception SQLException thrown if result set cannot be parsed
     * @exception DataException thrown if no records in result set
     */
    // ---------------------------------------------------------------------
    protected LocalizedCodeIfc parseAdvancedPricingCodeResultSet(ResultSet rs) throws SQLException, DataException
    {
        LocalizedCodeIfc localizedCode = null;
        boolean found = false;
        if (rs != null)
        {
            try
            {

                while (rs.next())
                {

                    int index = 0;
                    if (!found)
                    {
                        found = true;
                        localizedCode = DomainGateway.getFactory().getLocalizedCode();
                    }

                    String code = (getSafeString(rs, ++index));
                    String locale = getSafeString(rs, ++index);
                    String text = getSafeString(rs, ++index);

                    if (localizedCode != null)
                    {
                        localizedCode.setCode(code);
                        localizedCode.putText(LocaleUtilities.getLocaleFromString(locale), text);
                    }
                }
                
            }
            finally
            {
                rs.close();

            }
        }
        return localizedCode;
    }

}
