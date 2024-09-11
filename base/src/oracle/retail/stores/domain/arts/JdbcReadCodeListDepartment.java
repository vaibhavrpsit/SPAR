/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadCodeListDepartment.java /main/13 2014/03/19 11:37:18 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  03/14/14 - Throwing checked exceptions for fallback on remoteDT
 *                         to avoid NPE
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   11/04/08 - Added retrieval and saving of the deprecated db
 *                         column for text. It requires a non-null value for
 *                         backwards compatibility
 *    mdecama   11/03/08 - Updating the name column during insert for backwards
 *                         compatibility
 *    ohorne    11/04/08 - removed reliance on scrollable ResultSet
 *    mdecama   10/17/08 - Fixed problem populating the CodeList
 *    mdecama   10/15/08 - Data Operation to read a Code List for Departments
 * =========================================================================== */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

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

public class JdbcReadCodeListDepartment extends JdbcDataOperation implements ARTSDatabaseIfc, CodeConstantsIfc
{
    /**
     * Generated serialVersionUID
     */
    private static final long serialVersionUID = -6568204440177241754L;

    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcReadCodeListDepartment.class);

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
            logger.debug("JdbcReadCodeListDepartment.execute()");

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
            logger.debug("JdbcReadCodeListDepartment.execute()");
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

            // First Read the CodeList from the Store
            if (!criteria.getStoreID().equals(STORE_ID_CORPORATE))
            {
                sql = buildCodeListSQLStatement(criteria);
                dataConnection.execute(sql.getSQLString());
                codeList = parseCodeListResultSet(dataConnection);
            }

            // If there is no CodeList for the Store, read from CORP
            if (codeList == null)
            {
                criteria.setStoreID(STORE_ID_CORPORATE);
                sql = buildCodeListSQLStatement(criteria);
                dataConnection.execute(sql.getSQLString());
                codeList = parseCodeListResultSet(dataConnection);
            }
            
            if(null == codeList)
            {
               throw new DataException(DataException.NO_DATA, "JdbcReadCodeListDepartment.parseCodeListResultSet");
            }

        }
        catch (SQLException se)
        {
            logger.error("JdbcReadCodeListDepartment.readCodeList", se);
            throw new DataException(DataException.SQL_ERROR, "JdbcReadCodeListDepartment.readCodeList", se);
        }
        catch (DataException de)
        {
            logger.error("JdbcReadCodeListDepartment.readCodeList", de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error("JdbcReadCodeListDepartment.readCodeList", e);
            throw new DataException(DataException.UNKNOWN, "JdbcReadCodeListDepartment.readCodeList", e);
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

        sql.addTable(TABLE_POS_DEPARTMENT, TABLE_POS_DEPARTMENT);
        sql.addTable(TABLE_RETAIL_STORE_POS_DEPARTMENT, TABLE_RETAIL_STORE_POS_DEPARTMENT);
        sql.addTable(TABLE_POS_DEPARTMENT_I8, TABLE_POS_DEPARTMENT_I8);

        sql.setDistinctFlag(true);

        sql.addColumn(TABLE_RETAIL_STORE_POS_DEPARTMENT, FIELD_RETAIL_STORE_ID);
        sql.addColumn(TABLE_POS_DEPARTMENT, FIELD_POS_DEPARTMENT_ID);

        // For backwards compatibility
        sql.addColumn(TABLE_POS_DEPARTMENT,FIELD_POS_DEPARTMENT_NAME);

        sql.addColumn(TABLE_RETAIL_STORE_POS_DEPARTMENT, FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG);
        sql.addColumn(TABLE_RETAIL_STORE_POS_DEPARTMENT, FIELD_CODE_LIST_ENTRY_SORT_INDEX);
        sql.addColumn(TABLE_RETAIL_STORE_POS_DEPARTMENT, FIELD_CODE_LIST_ENTRY_ENABLED_FLAG);
        sql.addColumn(TABLE_POS_DEPARTMENT_I8, FIELD_LOCALE);
        sql.addColumn(TABLE_POS_DEPARTMENT_I8, FIELD_POS_DEPARTMENT_NAME);

        sql.addQualifier(TABLE_RETAIL_STORE_POS_DEPARTMENT, FIELD_RETAIL_STORE_ID,
                makeSafeString(criteria.getStoreID()));
        sql.addJoinQualifier(TABLE_POS_DEPARTMENT, FIELD_POS_DEPARTMENT_ID, TABLE_RETAIL_STORE_POS_DEPARTMENT,
                FIELD_POS_DEPARTMENT_ID);
        sql.addJoinQualifier(TABLE_POS_DEPARTMENT, FIELD_POS_DEPARTMENT_ID, TABLE_POS_DEPARTMENT_I8,
                FIELD_POS_DEPARTMENT_ID);

        sql.addQualifier(FIELD_LOCALE + " "
                + buildINClauseString(LocaleMap.getBestMatch("", criteria.getLocaleRequestor().getLocales())));

        sql.addOrdering(TABLE_RETAIL_STORE_POS_DEPARTMENT, FIELD_CODE_LIST_ENTRY_SORT_INDEX);
        sql.addOrdering(TABLE_POS_DEPARTMENT, FIELD_POS_DEPARTMENT_ID);

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
                    int index = 0;
                    if (codeList == null)
                    {
                        // Initialize the CodeList
                        codeList = DomainGateway.getFactory().getCodeListInstance();
                        codeList.setListDescription(CODE_LIST_DEPARTMENT);
                        codeList.setGroupName(CODE_LIST_DEPARTMENT);
                        codeList.setNumericCodes(false);
                    }

                    codeList.setStoreID(getSafeString(rs, ++index));
                    String code = getSafeString(rs, ++index);
                    String codeName = getSafeString(rs, ++index);
                    boolean defaultFlag = getBooleanFromString(rs, ++index);
                    int sortIndex = rs.getInt(++index);
                    boolean enabledFlag = getBooleanFromString(rs, ++index);
                    String locale = getSafeString(rs, ++index);
                    String text = getSafeString(rs, ++index);

                    // If we have already loaded a specific code entry, we only
                    // update the localized text
                    codeEntry = codeList.findListEntryByCode(code, false);
                    if (codeEntry == null)
                    {
                        localizedText = DomainGateway.getFactory().getLocalizedText();
                        localizedText.putText(LocaleUtilities.getLocaleFromString(locale), text);
                        codeList.addEntry(localizedText, code, codeName, sortIndex, enabledFlag, "");
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
     * Retrieves the CodeList ID for this Table. Since there is only one
     * CodeListID for the Department Table, we are only hardcoding it here.
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
        codeListIDs.add(CODE_LIST_DEPARTMENT);
        return codeListIDs;
    }

    /**
     * Reads a LocalizedCode
     *
     * @param connection
     * @param criteria
     * @return
     * @throws DataException
     */
    public LocalizedCodeIfc readCode(JdbcDataConnection dataConnection, CodeSearchCriteriaIfc criteria)
            throws DataException
    {
        LocalizedCodeIfc code = null;
        SQLSelectStatement sql = null;

        try
        {

            // First Read the CodeList from the Store
            if (!criteria.getStoreID().equals(STORE_ID_CORPORATE))
            {
                sql = buildCodeSQLStatement(criteria);
                dataConnection.execute(sql.getSQLString());
                code = parseCodeResultSet(dataConnection);
            }

            // If there is no CodeList for the Store, read from CORP
            if (code == null)
            {
                criteria.setStoreID(STORE_ID_CORPORATE);
                sql = buildCodeSQLStatement(criteria);
                dataConnection.execute(sql.getSQLString());
                code = parseCodeResultSet(dataConnection);
            }
            
            if(null == code)
            {
               throw new DataException(DataException.NO_DATA, "JdbcReadCodeListDepartment.parseCodeResultSet");
            }
        }
        catch (SQLException se)
        {
            logger.error("JdbcReadCodeListDepartment.readCode", se);
            throw new DataException(DataException.SQL_ERROR, "JdbcReadCodeListDepartment.readCode", se);
        }
        catch (DataException de)
        {
            logger.error("JdbcReadCodeListDepartment.readCode", de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error("JdbcReadCodeListDepartment.readCode", e);
            throw new DataException(DataException.UNKNOWN, "JdbcReadCodeListDepartment.readCode", e);
        }

        return code;
    }

    /**
     * Builds SQL statement for retrieval of a Localized Code
     *
     * @param storeID store identifier
     * @return sql string
     * @exception SQLException thrown if error occurs
     */
    // ---------------------------------------------------------------------
    protected SQLSelectStatement buildCodeSQLStatement(CodeSearchCriteriaIfc criteria) throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_POS_DEPARTMENT, TABLE_POS_DEPARTMENT);
        sql.addTable(TABLE_RETAIL_STORE_POS_DEPARTMENT, TABLE_RETAIL_STORE_POS_DEPARTMENT);
        sql.addTable(TABLE_POS_DEPARTMENT_I8, TABLE_POS_DEPARTMENT_I8);

        sql.setDistinctFlag(true);

        sql.addColumn(TABLE_POS_DEPARTMENT, FIELD_POS_DEPARTMENT_ID);
        sql.addColumn(TABLE_POS_DEPARTMENT_I8, FIELD_LOCALE);
        sql.addColumn(TABLE_POS_DEPARTMENT_I8, FIELD_POS_DEPARTMENT_NAME);

        sql.addQualifier(TABLE_RETAIL_STORE_POS_DEPARTMENT, FIELD_RETAIL_STORE_ID,
                makeSafeString(criteria.getStoreID()));
        sql
                .addQualifier(TABLE_RETAIL_STORE_POS_DEPARTMENT, FIELD_POS_DEPARTMENT_ID, makeSafeString(criteria
                        .getCode()));
        sql.addJoinQualifier(TABLE_POS_DEPARTMENT, FIELD_POS_DEPARTMENT_ID, TABLE_RETAIL_STORE_POS_DEPARTMENT,
                FIELD_POS_DEPARTMENT_ID);
        sql.addJoinQualifier(TABLE_POS_DEPARTMENT, FIELD_POS_DEPARTMENT_ID, TABLE_POS_DEPARTMENT_I8,
                FIELD_POS_DEPARTMENT_ID);

        sql.addQualifier(FIELD_LOCALE + " "
                + buildINClauseString(LocaleMap.getBestMatch("", criteria.getLocaleRequestor().getLocales())));

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
        if (rs != null)
        {
            try
            {
                while (rs.next())
                {
                    int index = 0;
                    if (localizedCode == null)
                    {
                        localizedCode = DomainGateway.getFactory().getLocalizedCode();
                    }

                    String code = (getSafeString(rs, ++index));
                    String locale = getSafeString(rs, ++index);
                    String text = getSafeString(rs, ++index);

                    localizedCode.setCode(code);
                    localizedCode.putText(LocaleUtilities.getLocaleFromString(locale), text);
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
