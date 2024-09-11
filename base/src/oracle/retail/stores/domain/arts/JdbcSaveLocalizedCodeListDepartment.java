/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveLocalizedCodeListDepartment.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    mdecama   11/04/08 - Added retrieval and saving of the deprecated db
 *                         column for text. It requires a non-null value for
 *                         backwards compatibility
 *    mdecama   11/03/08 - Updating the name column during insert for backwards
 *                         compatibility
 *    mdecama   11/03/08 - Updates to the persistence of CodeLists.
 * =========================================================================== */

package oracle.retail.stores.domain.arts;

import java.sql.SQLException;
import java.util.Locale;

import org.apache.log4j.Logger;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.CodeListSaveCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;

/**
 * This Class saves a localized Reason Code in the appropriate table
 */
public class JdbcSaveLocalizedCodeListDepartment extends JdbcSaveLocalizedCodeList implements ARTSDatabaseIfc
{

    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = 7122629844966967034L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger
            .getLogger(oracle.retail.stores.domain.arts.JdbcSaveLocalizedCodeListDepartment.class);

    /**
     * Saves a Code List Try to update it first. If the update fails, try to add
     * it.
     *
     * @param dataConnection
     * @param criteria
     * @throws DataException
     */
    protected void saveCodeList(JdbcDataConnection dataConnection, CodeListSaveCriteriaIfc criteria)
            throws DataException
    {
        super.saveCodeList(dataConnection, criteria);

        saveRetailStoreDepartmentCodeList(dataConnection, criteria);
    }

    /**
     * Saves a Code List Try to update it first. If the update fails, try to add
     * it.
     *
     * @param dataConnection
     * @param criteria
     * @throws DataException
     */
    protected void saveRetailStoreDepartmentCodeList(JdbcDataConnection dataConnection, CodeListSaveCriteriaIfc criteria)
            throws DataException
    {
        CodeListIfc codeList = criteria.getCodeList();
        boolean result = false;

        for (int i = 0; i < codeList.getEntries().length; i++)
        {
            CodeEntryIfc entry = codeList.getEntries()[i];
            result = updateRetailStoreDepartmentEntry(dataConnection, criteria, entry);
            if (!result)
                insertRetailStoreDepartmentEntry(dataConnection, criteria, entry);
        }
    }

    /**
     * Builds the SQL Statement to Insert a CodeList Entry
     *
     * @param codeList
     * @param entry
     * @return
     */
    protected SQLInsertStatement buildInsertCodeListEntrySQLStatement(CodeListSaveCriteriaIfc criteria, CodeEntryIfc entry)
    {
        SQLInsertStatement sql = new SQLInsertStatement();
        sql.setTable(TABLE_POS_DEPARTMENT);
        sql.addColumn(FIELD_POS_DEPARTMENT_ID, makeSafeString(entry.getCode()));

        // This value is being set for backwards compatibility
        sql.addColumn(FIELD_POS_DEPARTMENT_NAME, makeSafeString(entry.getCodeName()));

        return sql;
    }

    /**
     * Builds the SQL Statement to Insert a CodeList I18N Entry
     *
     * @param codeList
     * @param entry
     * @return
     */
    protected SQLInsertStatement buildInsertCodeListI18EntrySQLStatement(CodeListSaveCriteriaIfc criteria,
            CodeEntryIfc entry, Locale lcl) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        String localizedText = getLocalizedTextString(entry.getLocalizedText(), lcl, criteria.getUserLocale());
        sql.setTable(TABLE_POS_DEPARTMENT_I8);

        sql.addColumn(FIELD_POS_DEPARTMENT_ID, makeSafeString(entry.getCode()));
        sql.addColumn(FIELD_LOCALE, makeSafeString(lcl.toString()));
        sql.addColumn(FIELD_POS_DEPARTMENT_NAME, makeSafeString(localizedText));

        return sql;
    }

    /**
     * Builds the SQL Statement to update a codeList entry
     *
     * @param codeList
     * @param entry
     * @return
     * @throws SQLException
     */
    protected SQLUpdateStatement buildUpdateCodeListEntrySQLStatement(CodeListIfc codeList, CodeEntryIfc entry)
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        sql.setTable(TABLE_POS_DEPARTMENT);
        sql.addQualifier(FIELD_POS_DEPARTMENT_ID, makeSafeString(entry.getCode()));
        // This value is being set for backwards compatibility
        sql.addColumn(FIELD_POS_DEPARTMENT_NAME, makeSafeString(entry.getCodeName()));

        return sql;
    }

    /**
     * Builds the SQL Statement to update a codeList I18N entry
     *
     * @param codeList
     * @param entry
     * @return
     * @throws SQLException
     */
    protected SQLUpdateStatement buildUpdateCodeListI18EntrySQLStatement(CodeListSaveCriteriaIfc criteria,
            CodeEntryIfc entry) throws DataException
    {
        Locale userLocale = criteria.getUserLocale();
        SQLUpdateStatement sql = new SQLUpdateStatement();

        sql.setTable(TABLE_POS_DEPARTMENT_I8);

        sql.addColumn(FIELD_POS_DEPARTMENT_NAME, makeSafeString(entry.getText(userLocale)));

        sql.addQualifier(FIELD_LOCALE, makeSafeString(LocaleMap.getBestMatch(userLocale).toString()));
        sql.addQualifier(FIELD_POS_DEPARTMENT_ID, makeSafeString(entry.getCode()));

        return sql;
    }

    /**
     * Builds the SQL Statement to update a codeList entry
     *
     * @param codeList
     * @param entry
     * @return
     * @throws SQLException
     */
    protected SQLUpdateStatement buildUpdateRetailStoreDepartmentEntrySQLStatement(CodeListIfc codeList,
            CodeEntryIfc entry) throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();
        boolean isDefault = isDefaultEntry(codeList, entry.getCode());

        sql.setTable(TABLE_RETAIL_STORE_POS_DEPARTMENT);

        sql.addColumn(FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG, makeStringFromBoolean(isDefault));
        sql.addColumn(FIELD_POS_DEPARTMENT_ID, makeSafeString(entry.getCode()));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_SORT_INDEX, entry.getSortIndex());
        sql.addColumn(FIELD_CODE_LIST_ENTRY_ENABLED_FLAG, makeStringFromBoolean(entry.isEnabled()));

        sql.addQualifier(FIELD_RETAIL_STORE_ID, makeSafeString(codeList.getStoreID()));
        sql.addQualifier(FIELD_POS_DEPARTMENT_ID, makeSafeString(entry.getCode()));

        return sql;
    }

    /**
     * Builds the SQL Statement to Insert a CodeList Entry
     *
     * @param codeList
     * @param entry
     * @return
     */
    protected SQLInsertStatement buildInsertRetailStoreDepartmentSQLStatement(CodeListIfc codeList, CodeEntryIfc entry)
            throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();
        boolean isDefault = isDefaultEntry(codeList, entry.getCode());

        sql.setTable(TABLE_RETAIL_STORE_POS_DEPARTMENT);

        sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(codeList.getStoreID()));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG, makeStringFromBoolean(isDefault));
        sql.addColumn(FIELD_POS_DEPARTMENT_ID, makeSafeString(entry.getCode()));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_SORT_INDEX, entry.getSortIndex());
        sql.addColumn(FIELD_CODE_LIST_ENTRY_ENABLED_FLAG, makeStringFromBoolean(entry.isEnabled()));

        return sql;
    }

    /**
     * Inserts a CodeEntry in the Database
     *
     * @param dataConnection
     * @param criteria
     * @param entry
     * @throws DataException
     */
    protected void insertRetailStoreDepartmentEntry(JdbcDataConnection dataConnection,
            CodeListSaveCriteriaIfc criteria, CodeEntryIfc entry) throws DataException
    {
        CodeListIfc codeList = criteria.getCodeList();

        SQLInsertStatement sql = buildInsertRetailStoreDepartmentSQLStatement(codeList, entry);

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            logger.error("JdbcSaveLocalizedCodeList.insertCodeList", e);
            throw new DataException(DataException.UNKNOWN, "JdbcSaveLocalizedCodeList.insertRetailStoreDepartmentntry",
                    e);
        }
    }

    /**
     * Updates the CodeList Entry
     *
     * @param dataConnection
     * @param criteria
     * @param entry
     * @throws DataException
     */
    protected boolean updateRetailStoreDepartmentEntry(JdbcDataConnection dataConnection,
            CodeListSaveCriteriaIfc criteria, CodeEntryIfc entry) throws DataException
    {
        SQLUpdateStatement sql = buildUpdateRetailStoreDepartmentEntrySQLStatement(criteria.getCodeList(), entry);

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error("JdbcSaveLocalizedCodeList.updateRetailStoreDepartmentEntry", de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error("JdbcSaveLocalizedCodeList.updateCodeListEntry", e);
            throw new DataException(DataException.UNKNOWN,
                    "JdbcSaveLocalizedCodeList.updateRetailStoreDepartmentEntry", e);
        }

        return (dataConnection.getUpdateCount() > 0);
    }

}
