/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadCodeListShippingMethod.java /main/14 2014/03/17 10:29:10 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  03/14/14 - Throwing checked exceptions for fallback on remoteDT
 *                         to avoid NPE
 *    mjwallac  12/19/13 - fix POS null dereferences (part 1)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    ohorne    11/04/08 - fixed header
 *    ohorne    11/04/08 - removed reliance on scrollable ResultSet
 *    mdecama   10/17/08 - Fixed problem populating the CodeList
 *    mdecama   10/15/08 - Data Operation to Retrieve Code List from the
 *                         Shipping Method Table
 * =========================================================================== */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
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



/**
 * This Data Operation retrieves Code Lists for Shipping Method
 */
public class JdbcReadCodeListShippingMethod extends JdbcDataOperation implements ARTSDatabaseIfc, CodeConstantsIfc
{
    /**
     * Generated serialVersionUID
     */
    private static final long serialVersionUID = 5438782993653072458L;

    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger
            .getLogger(oracle.retail.stores.domain.arts.JdbcReadCodeListShippingMethod.class);

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
            logger.debug("JdbcReadCodeListShippingMethod.execute()");

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
            logger.debug("JdbcReadCodeListShippingMethod.execute()");
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
            sql = buildCodeListSQLStatement(criteria);
            dataConnection.execute(sql.getSQLString());
            codeList = parseCodeListResultSet(dataConnection);
        }
        catch (SQLException se)
        {
            logger.error("JdbcReadCodeListShippingMethod.readCodeList", se);
            throw new DataException(DataException.SQL_ERROR, "JdbcReadCodeListShippingMethod.readCodeList", se);
        }
        catch (DataException de)
        {
            logger.error("JdbcReadCodeListShippingMethod.readCodeList", de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error("JdbcReadCodeListShippingMethod.readCodeList", e);
            throw new DataException(DataException.UNKNOWN, "JdbcReadCodeListShippingMethod.readCodeList", e);
        }

        return codeList;
    }

    /**
     * Builds a SQL Statement for retrieval a CodeList
     *
     * @param criteria
     * @return
     * @throws SQLException
     */
    protected SQLSelectStatement buildCodeListSQLStatement(CodeListSearchCriteriaIfc criteria) throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_SHIPPING_METHOD, TABLE_SHIPPING_METHOD);
        sql.addTable(TABLE_SHIPPING_METHOD_I8, TABLE_SHIPPING_METHOD_I8);

        sql.setDistinctFlag(true);

        sql.addColumn(TABLE_SHIPPING_METHOD, FIELD_SHIPPING_METHOD_ID);
        sql.addColumn(TABLE_SHIPPING_METHOD_I8, FIELD_LOCALE);
        sql.addColumn(TABLE_SHIPPING_METHOD_I8, FIELD_SHIPPING_TYPE);

        sql.addJoinQualifier(TABLE_SHIPPING_METHOD, FIELD_SHIPPING_METHOD_ID, TABLE_SHIPPING_METHOD_I8,
                FIELD_SHIPPING_METHOD_ID);
        sql.addQualifier(FIELD_LOCALE + " "
                + buildINClauseString(LocaleMap.getBestMatch("", criteria.getLocaleRequestor().getLocales())));

        sql.addOrdering(TABLE_SHIPPING_METHOD_I8, FIELD_SHIPPING_TYPE);

        return (sql);
    }

    /**
     * Parses result set and returns a CodeListIfc object.
     *
     * @param dataConnection data connection
     * @exception SQLException thrown if result set cannot be parsed
     * @exception DataException thrown if no records in result set
     */
    // ---------------------------------------------------------------------
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
                    int index = 0;
                    if (codeList == null)
                    {

                        // Initialize the CodeList
                        codeList = DomainGateway.getFactory().getCodeListInstance();
                        codeList.setStoreID(STORE_ID_CORPORATE);
                        codeList.setListDescription(CODE_LIST_SHIPPING_METHOD);
                        codeList.setGroupName(CODE_LIST_SHIPPING_METHOD);
                        codeList.setNumericCodes(false);
                    }

                    String code = getSafeString(rs, ++index);
                    String locale = getSafeString(rs, ++index);
                    String text = getSafeString(rs, ++index);

                    // If we have already loaded a specific code entry, we only
                    // update the localized text
                    codeEntry = codeList.findListEntryByCode(code, false);
                    if (codeEntry == null)
                    {
                        localizedText = DomainGateway.getFactory().getLocalizedText();
                        localizedText.putText(LocaleUtilities.getLocaleFromString(locale), text);
                        codeList.addEntry(localizedText, code, 0, true, "");
                    }
                    else
                    {
                        codeEntry.getLocalizedText().putText(LocaleUtilities.getLocaleFromString(locale), text);
                    }
                }
                if(null == codeList)
                {
                    throw new DataException(DataException.NO_DATA, "JdbcReadCodeListShippingMethod.parseCodeListResultSet");
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
     * Retrieves the CodeList ID for this Table. Since there is only one
     * CodeListID for the Shipping Method Table, we are hardcoding it here.
     *
     * @param connection
     * @param criteria
     * @return
     * @throws DataException
     * @throws SQLException
     */
    public List<String> readCodeListIDs(JdbcDataConnection dataConnection, CodeListSearchCriteriaIfc criteria)
    {
        List<String> codeListIDs = new Vector<String>(1);
        codeListIDs.add(CODE_LIST_SHIPPING_METHOD);
        return codeListIDs;
    }

    /**
     * Reads a Localized Shipping Method
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

        try
        {
            sql = buildCodeSQLStatement(criteria);
            dataConnection.execute(sql.getSQLString());
            code = parseCodeResultSet(dataConnection);
        }
        catch (SQLException se)
        {
            logger.error("JdbcReadCodeListShippingMethod.readCode", se);
            throw new DataException(DataException.SQL_ERROR, "JdbcReadCodeListShippingMethod.readCode", se);
        }
        catch (DataException de)
        {
            logger.error("JdbcReadCodeListShippingMethod.readCode", de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error("JdbcReadCodeListShippingMethod.readCode", e);
            throw new DataException(DataException.UNKNOWN, "JdbcReadCodeListShippingMethod.readCode", e);
        }

        return code;
    }

    /**
     * Builds a SQL Statement to retrieve a localized shipping method
     *
     * @param criteria
     * @return
     * @throws SQLException
     */
    protected SQLSelectStatement buildCodeSQLStatement(CodeSearchCriteriaIfc criteria) throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_SHIPPING_METHOD, TABLE_SHIPPING_METHOD);
        sql.addTable(TABLE_SHIPPING_METHOD_I8, TABLE_SHIPPING_METHOD_I8);

        sql.setDistinctFlag(true);

        sql.addColumn(TABLE_SHIPPING_METHOD, FIELD_SHIPPING_METHOD_ID);
        sql.addColumn(TABLE_SHIPPING_METHOD_I8, FIELD_LOCALE);
        sql.addColumn(TABLE_SHIPPING_METHOD_I8, FIELD_SHIPPING_TYPE);

        sql.addJoinQualifier(TABLE_SHIPPING_METHOD, FIELD_SHIPPING_METHOD_ID, TABLE_SHIPPING_METHOD_I8,
                FIELD_SHIPPING_METHOD_ID);
        sql.addQualifier(FIELD_LOCALE + " "
                + buildINClauseString(LocaleMap.getBestMatch("", criteria.getLocaleRequestor().getLocales())));
        sql.addQualifier(TABLE_SHIPPING_METHOD, FIELD_SHIPPING_METHOD_ID, criteria.getCode());

        return (sql);
    }

    /**
     * Parses result set and creates a LocalizedCodeIfc object.
     *
     * @param dataConnection data connection
     * @exception SQLException thrown if result set cannot be parsed
     * @exception DataException thrown if no records in result set
     */
    // ---------------------------------------------------------------------
    protected LocalizedCodeIfc parseCodeResultSet(JdbcDataConnection dataConnection) throws SQLException, DataException
    {
        LocalizedCodeIfc localizedCode = null;
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
                if(null == localizedCode)
                {
                    throw new DataException(DataException.NO_DATA, "JdbcReadCodeListShippingMethod.parseCodeResultSet");
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
