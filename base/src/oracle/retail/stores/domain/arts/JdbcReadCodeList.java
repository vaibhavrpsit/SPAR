/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadCodeList.java /main/19 2014/03/19 11:37:18 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  03/14/14 - Throwing checked exceptions for fallback on remoteDT
 *                         to avoid NPE
 *    mjwallac  12/19/13 - fix POS null dereferences (part 1)
 *    abondala  05/01/13 - replace dynamic sql with bind variables.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   02/05/10 - Throw NO_DATA exception when no code list is found
 *    abondala  01/03/10 - update header date
 *    cgreene   06/18/09 - commented out DISTINCT clause on select to reduce
 *                         performance overhead. Arranged store id and desc to
 *                         match index on table.
 *    sswamygo  02/12/09 - updated to set codeName which will be used for
 *                         POSLog
 *    ohorne    12/23/08 - now uses new Statement object to generate ResultSet
 *                         b/c method accessed while still reading ResultSet
 *                         obtained from JdbcDataConnection's statement
 *    ohorne    11/04/08 - removed reliance on scrollable resultset
 *    abondala  11/03/08 - updated files related to the Patriotic customer ID
 *                         types reason code
 *    acadar    10/24/08 - updates
 *    mdecama   10/17/08 - Fixed problem populating the CodeList
 *    mdecama   10/15/08 - Data Operation to load a CodeList from the ID_LU_CD
 *                         table
 * =========================================================================== */
package oracle.retail.stores.domain.arts;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
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

public class JdbcReadCodeList extends JdbcDataOperation implements ARTSDatabaseIfc, CodeConstantsIfc
{
    /**
     * Generated serialVersionUID
     */
    private static final long serialVersionUID = -32925247595052041L;

    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcReadCodeList.class);

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
            logger.debug("JdbcReadCodeList.execute()");

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
            switch (criteria.getSearchType()) {
            case CodeListSearchCriteriaIfc.SEARCH_CODE_LIST:
                CodeListIfc codeList = readCodeList(connection, criteria);
                dataTransaction.setResult(codeList);
                break;
            case CodeListSearchCriteriaIfc.SEARCH_CODE_LIST_ID:
                List<String> codeListIDs = readCodeListIDs(connection, criteria);
                dataTransaction.setResult((Vector<String>)codeListIDs);
                break;
            }
        }

        if (dataTransaction.getResult() == null)
        {
            throw new DataException(DataException.NO_DATA,
                                    "No code list was found using JdbcReadCodeList.");
        }
    }

    /**
     * Reads a CodeList and its codeEntries
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
            // First Read the CodeList from the Store
            if (!criteria.getStoreID().equals(STORE_ID_CORPORATE))
            {
                sql = buildCodeListSQLStatement(criteria);
                dataConnection.execute(sql.getSQLString(), sql.getParameterValues());
                codeList = parseCodeListResultSet(dataConnection);
            }

            // If there is no CodeList for the Store, read from CORP
            if (codeList == null)
            {
                criteria.setStoreID(STORE_ID_CORPORATE);
                sql = buildCodeListSQLStatement(criteria);
                dataConnection.execute(sql.getSQLString(), sql.getParameterValues());
                codeList = parseCodeListResultSet(dataConnection);
            }
            
            if(null == codeList)
            {
                throw new DataException(DataException.NO_DATA, "JdbcReadCodeList.parseCodeListResultSet");
            }
        }
        catch (SQLException se)
        {
            logger.error("JdbcReadCodeList.readCodeList", se);
            throw new DataException(DataException.SQL_ERROR, "JdbcReadCodeList.readCodeList", se);
        }
        catch (DataException de)
        {
            logger.error("JdbcReadCodeList.readCodeList", de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error("JdbcReadCodeList.readCodeList", e);
            throw new DataException(DataException.UNKNOWN, "JdbcReadCodeList.readCodeList", e);
        }

        return codeList;
    }

    /**
     * Builds SQL statement for retrieval of a CodeList
     *
     * @param criteria Search Criteria
     * @return sql string
     * @exception SQLException thrown if error occurs
     */
    protected SQLSelectStatement buildCodeListSQLStatement(CodeListSearchCriteriaIfc criteria) throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_CODE_LIST, TABLE_CODE_LIST);
        sql.addTable(TABLE_CODE_LIST_I8, TABLE_CODE_LIST_I8);

        sql.addColumn(TABLE_CODE_LIST, FIELD_RETAIL_STORE_ID);
        sql.addColumn(TABLE_CODE_LIST, FIELD_CODE_LIST_DESCRIPTION);
        sql.addColumn(TABLE_CODE_LIST, FIELD_CODE_LIST_GROUP_NAME);
        sql.addColumn(TABLE_CODE_LIST, FIELD_CODE_LIST_NUMERIC_CODES_FLAG);
        sql.addColumn(TABLE_CODE_LIST, FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG);
        sql.addColumn(TABLE_CODE_LIST, FIELD_CODE_LIST_ENTRY_TEXT);
        sql.addColumn(TABLE_CODE_LIST, FIELD_CODE_LIST_ENTRY_CODE);
        sql.addColumn(TABLE_CODE_LIST, FIELD_CODE_LIST_ENTRY_SORT_INDEX);
        sql.addColumn(TABLE_CODE_LIST, FIELD_CODE_LIST_ENTRY_ENABLED_FLAG);
        sql.addColumn(TABLE_CODE_LIST, FIELD_PRICE_DERIVATION_RULE_ID);
        sql.addColumn(TABLE_CODE_LIST_I8, FIELD_LOCALE);
        sql.addColumn(TABLE_CODE_LIST_I8, FIELD_CODE_LIST_ENTRY_TEXT);

        if (!Util.isEmpty(criteria.getStoreID()))
            sql.addQualifier(new SQLParameterValue(TABLE_CODE_LIST, FIELD_RETAIL_STORE_ID, criteria.getStoreID()));
        
        if (!Util.isEmpty(criteria.getListID()))
            sql.addQualifier(new SQLParameterValue(TABLE_CODE_LIST, FIELD_CODE_LIST_DESCRIPTION, criteria.getListID()));

        if (!Util.isEmpty(criteria.getGroupID()))
            sql.addQualifier(new SQLParameterValue(TABLE_CODE_LIST, FIELD_CODE_LIST_GROUP_NAME, criteria.getGroupID()));

        sql.addQualifier(FIELD_LOCALE + " "
                + buildINClauseString(LocaleMap.getBestMatch("", criteria.getLocaleRequestor().getLocales())));

        sql.addJoinQualifier(TABLE_CODE_LIST, FIELD_RETAIL_STORE_ID, TABLE_CODE_LIST_I8, FIELD_RETAIL_STORE_ID);
        sql.addJoinQualifier(TABLE_CODE_LIST, FIELD_CODE_LIST_DESCRIPTION, TABLE_CODE_LIST_I8,
                FIELD_CODE_LIST_DESCRIPTION);
        sql.addJoinQualifier(TABLE_CODE_LIST, FIELD_CODE_LIST_GROUP_NAME, TABLE_CODE_LIST_I8,
                FIELD_CODE_LIST_GROUP_NAME);
        sql.addJoinQualifier(TABLE_CODE_LIST, FIELD_CODE_LIST_ENTRY_CODE, TABLE_CODE_LIST_I8,
                FIELD_CODE_LIST_ENTRY_CODE);

        sql.addOrdering(FIELD_CODE_LIST_GROUP_NAME);
        sql.addOrdering(FIELD_CODE_LIST_DESCRIPTION);
        sql.addOrdering(FIELD_CODE_LIST_ENTRY_SORT_INDEX);
        sql.addOrdering(FIELD_CODE_LIST_ENTRY_CODE);

        return (sql);
    }

    /**
     * Parses result set and creates a CodeListIfc object.
     *
     * @param dataConnection data connection
     * @exception SQLException thrown if result set cannot be parsed
     * @exception DataException thrown if no records in result set
     */
    protected CodeListIfc parseCodeListResultSet(JdbcDataConnection dataConnection) throws SQLException, DataException
    {
        CodeListIfc codeList = null;
        ResultSet rs = (ResultSet)dataConnection.getResult();

        if (rs != null)
        {
            CodeEntryIfc codeEntry = null;
            LocalizedTextIfc localizedText = null;

            try
            {
                while (rs.next())
                {
                    if (codeList == null)
                    {
                        codeList = DomainGateway.getFactory().getCodeListInstance();
                    }

                    int index = 0;
                    codeList.setStoreID(getSafeString(rs, ++index));
                    codeList.setListDescription(getSafeString(rs, ++index));
                    codeList.setGroupName(getSafeString(rs, ++index));
                    codeList.setNumericCodes(getBooleanFromString(rs, ++index));

                    boolean defaultFlag = getBooleanFromString(rs, ++index);
                    String codeName = getSafeString(rs, ++index);

                    String code = getSafeString(rs, ++index);
                    int sortIndex = rs.getInt(++index);
                    boolean enabledFlag = getBooleanFromString(rs, ++index);
                    String referenceKey = getSafeString(rs, ++index);
                    String locale = getSafeString(rs, ++index);
                    String text = getSafeString(rs, ++index);

                    // If we have already loaded a specific code entry, we only
                    // update the localized text
                    codeEntry = codeList.findListEntryByCode(code, false);
                    if (codeEntry == null)
                    {
                        localizedText = DomainGateway.getFactory().getLocalizedText();
                        localizedText.putText(LocaleUtilities.getLocaleFromString(locale), text);
                        codeList.addEntry(localizedText, code, codeName, sortIndex, enabledFlag, referenceKey);
                    }
                    else
                    {
                        codeEntry.getLocalizedText().putText(LocaleUtilities.getLocaleFromString(locale), text);
                    }

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
     * Reads all the CodeList Ids from the CodeList Table
     *
     * @param connection
     * @param criteria
     * @return
     * @throws DataException
     * @throws SQLException
     */
    public List<String> readCodeListIDs(JdbcDataConnection dataConnection, CodeListSearchCriteriaIfc criteria)
            throws DataException
    {
        List<String> codeListIDs = null;
        SQLSelectStatement sql = null;
        try
        {
            sql = buildCodeListIDsSQLStatement(criteria);
            dataConnection.execute(sql.getSQLString());
            codeListIDs = parseCodeListIDsResultSet(dataConnection);
        }
        catch (SQLException se)
        {
            logger.error("JdbcReadCodeList.readCodeListIDs", se);
            throw new DataException(DataException.SQL_ERROR, "JdbcReadCodeList.readCodeListIDs", se);
        }
        catch (DataException de)
        {
            logger.error("JdbcReadCodeList.readCodeListIDs", de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error("JdbcReadCodeList.readCodeListIDs", e);
            throw new DataException(DataException.UNKNOWN, "JdbcReadCodeList.readCodeListIDs", e);
        }

        return codeListIDs;
    }

    /**
     * Builds SQL statement for retrieval of CodeList IDs
     *
     * @param criteria
     * @exception SQLException thrown if error occurs
     */
    protected SQLSelectStatement buildCodeListIDsSQLStatement(CodeListSearchCriteriaIfc criteria) throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_CODE_LIST, TABLE_CODE_LIST);

        sql.setDistinctFlag(true);

        sql.addColumn(TABLE_CODE_LIST, FIELD_CODE_LIST_DESCRIPTION);

        sql.addQualifier(FIELD_RETAIL_STORE_ID + " IN (" + inQuotes(criteria.getStoreID()) + ","
                + inQuotes(STORE_ID_CORPORATE) + ")");
        sql.addOrdering(FIELD_CODE_LIST_DESCRIPTION);

        return (sql);
    }

    /**
     * Parses result set and returns a list of CodeList IDs
     *
     * @param dataConnection data connection
     * @exception SQLException thrown if result set cannot be parsed
     * @exception DataException thrown if no records in result set
     */
    protected List<String> parseCodeListIDsResultSet(JdbcDataConnection dataConnection) throws SQLException,
            DataException
    {
        List<String> codeListIds = null;
        ResultSet rs = (ResultSet)dataConnection.getResult();
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
                        codeListIds = new Vector<String>(4);
                    }
                    if (codeListIds != null)
                    {
                        codeListIds.add(getSafeString(rs, ++index));
                    }
                }
                if(null == codeListIds)
                {
                    throw new DataException(DataException.NO_DATA, "JdbcReadCodeList.parseCodeListIDsResultSet");
                }
            }
            finally
            {
                rs.close();
            }
        }
        return codeListIds;
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
                throw new DataException(DataException.NO_DATA, "JdbcReadCodeList.parseCodeResultSet");
            }
            
        }
        catch (SQLException se)
        {
            logger.error("JdbcReadCodeList.readCode", se);
            throw new DataException(DataException.SQL_ERROR, "JdbcReadCodeList.readCode", se);
        }
        catch (DataException de)
        {
            logger.error("JdbcReadCodeList.readCode", de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error("JdbcReadCodeList.readCode", e);
            throw new DataException(DataException.UNKNOWN, "JdbcReadCodeList.readCode", e);
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
     * Builds SQL statement for retrieval of a localized code
     *
     * @param criteria search criteria
     * @return sql string
     * @exception SQLException thrown if error occurs
     */
    protected SQLSelectStatement buildCodeSQLStatement(CodeSearchCriteriaIfc criteria) throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_CODE_LIST, TABLE_CODE_LIST);
        sql.addTable(TABLE_CODE_LIST_I8, TABLE_CODE_LIST_I8);

        sql.setDistinctFlag(true);

        sql.addColumn(TABLE_CODE_LIST, FIELD_CODE_LIST_ENTRY_CODE);
        sql.addColumn(TABLE_CODE_LIST, FIELD_CODE_LIST_ENTRY_TEXT);
        sql.addColumn(TABLE_CODE_LIST_I8, FIELD_LOCALE);
        sql.addColumn(TABLE_CODE_LIST_I8, FIELD_CODE_LIST_ENTRY_TEXT);

        if (!Util.isEmpty(criteria.getListID()))
            sql.addQualifier(new SQLParameterValue(TABLE_CODE_LIST, FIELD_CODE_LIST_DESCRIPTION, criteria.getListID()));
        
        if (!Util.isEmpty(criteria.getGroupID()))
            sql.addQualifier(new SQLParameterValue(TABLE_CODE_LIST, FIELD_CODE_LIST_GROUP_NAME, criteria.getGroupID()));
        
        if (!Util.isEmpty(criteria.getStoreID()))
            sql.addQualifier(new SQLParameterValue(TABLE_CODE_LIST, FIELD_RETAIL_STORE_ID, criteria.getStoreID()));
        
        if (!Util.isEmpty(criteria.getCode()))
            sql.addQualifier(new SQLParameterValue(TABLE_CODE_LIST, FIELD_CODE_LIST_ENTRY_CODE, criteria.getCode()));

        sql.addQualifier(FIELD_LOCALE + " "
                + buildINClauseString(LocaleMap.getBestMatch("", criteria.getLocaleRequestor().getLocales())));

        sql.addJoinQualifier(TABLE_CODE_LIST, FIELD_RETAIL_STORE_ID, TABLE_CODE_LIST_I8, FIELD_RETAIL_STORE_ID);
        sql.addJoinQualifier(TABLE_CODE_LIST, FIELD_CODE_LIST_DESCRIPTION, TABLE_CODE_LIST_I8,
                FIELD_CODE_LIST_DESCRIPTION);
        sql.addJoinQualifier(TABLE_CODE_LIST, FIELD_CODE_LIST_GROUP_NAME, TABLE_CODE_LIST_I8,
                FIELD_CODE_LIST_GROUP_NAME);
        sql.addJoinQualifier(TABLE_CODE_LIST, FIELD_CODE_LIST_ENTRY_CODE, TABLE_CODE_LIST_I8,
                FIELD_CODE_LIST_ENTRY_CODE);

        return (sql);
    }

    /**
     * Parses result set and creates a LocalizedCodeIfc object.
     *
     * @param rs the ResultSet
     * @exception SQLException thrown if result set cannot be parsed
     * @exception DataException thrown if no records in result set
     */
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

                    String code = getSafeString(rs, ++index);
                    String text = getSafeString(rs, ++index);
                    String locale = getSafeString(rs, ++index);
                    String localeSpecificText = getSafeString(rs, ++index);

                    if (localizedCode != null)
                    {
                        localizedCode.setCode(code);
                        localizedCode.setCodeName(text);
                        localizedCode.putText(LocaleUtilities.getLocaleFromString(locale), localeSpecificText);
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
