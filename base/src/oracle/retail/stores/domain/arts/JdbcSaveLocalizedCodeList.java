/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveLocalizedCodeList.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:05 mszekely Exp $
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
 *    mdecama   11/03/08 - Updates to the CodeList Persistence
 * =========================================================================== */

package oracle.retail.stores.domain.arts;

import java.sql.SQLException;
import java.util.Locale;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.CodeListSaveCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This Class saves a localized Reason Code in the appropriate table
 */
public class JdbcSaveLocalizedCodeList extends JdbcDataOperation implements ARTSDatabaseIfc
{

    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = 7122629844966967034L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveLocalizedCodeList.class);

    /**
     * This method is used to execute specific operations for a specific
     * transaction.
     *
     * @see oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc#execute(oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc,
     *      oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc,
     *      oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc)
     */
    public void execute(DataTransactionIfc dt, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCodeList.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        CodeListSaveCriteriaIfc criteria = (CodeListSaveCriteriaIfc)action.getDataObject();

        saveCodeList(connection, criteria);
    }

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
        CodeListIfc codeList = criteria.getCodeList();
        boolean result = false;

        for (int i = 0; i < codeList.getEntries().length; i++)
        {
            CodeEntryIfc entry = codeList.getEntries()[i];
            result = updateCodeListEntry(dataConnection, criteria, entry);
            if (!result)
                insertCodeListEntry(dataConnection, criteria, entry);
        }
    }

    /**
     * Inserts a CodeEntry in the Database
     *
     * @param dataConnection
     * @param criteria
     * @param entry
     * @throws DataException
     */
    protected void insertCodeListEntry(JdbcDataConnection dataConnection, CodeListSaveCriteriaIfc criteria,
            CodeEntryIfc entry) throws DataException
    {
        SQLInsertStatement sql = buildInsertCodeListEntrySQLStatement(criteria, entry);

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
            throw new DataException(DataException.UNKNOWN, "JdbcSaveLocalizedCodeList.insertCodeListEntry", e);
        }

        insertCodeListI18Entries(dataConnection, criteria, entry);
    }

    /**
     * Update the CodeList I18N Table
     *
     * @param dataConnection
     * @param criteria
     * @param entry
     * @throws DataException
     */
    protected void insertCodeListI18Entries(JdbcDataConnection dataConnection, CodeListSaveCriteriaIfc criteria,
            CodeEntryIfc entry) throws DataException
    {
        Locale[] supportedLocales = LocaleMap.getSupportedLocales();
        for (int i = 0; i < supportedLocales.length; i++)
        {
            Locale lcl = supportedLocales[i];
            insertCodeListI18Entry(dataConnection, criteria, entry, lcl);
        }
    }

    /**
     * Inserts a CodeListEntry to the I18N Table
     *
     * @param dataConnection
     * @param criteria
     * @param entry
     * @throws DataException
     */
    protected void insertCodeListI18Entry(JdbcDataConnection dataConnection, CodeListSaveCriteriaIfc criteria,
            CodeEntryIfc entry, Locale lcl) throws DataException
    {

        SQLInsertStatement sql = buildInsertCodeListI18EntrySQLStatement(criteria, entry, lcl);

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
            logger.error("JdbcSaveLocalizedCodeList.insertCodeListI18TableEntry", e);
            throw new DataException(DataException.UNKNOWN, "JdbcSaveLocalizedCodeList.insertCodeListI18TableEntry", e);
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
    protected boolean updateCodeListEntry(JdbcDataConnection dataConnection, CodeListSaveCriteriaIfc criteria,
            CodeEntryIfc entry) throws DataException
    {
        SQLUpdateStatement sql = buildUpdateCodeListEntrySQLStatement(criteria.getCodeList(), entry);

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error("JdbcSaveLocalizedCodeList.updateCodeListEntry", de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error("JdbcSaveLocalizedCodeList.updateCodeListEntry", e);
            throw new DataException(DataException.UNKNOWN, "JdbcSaveLocalizedCodeList.updateCodeListEntry", e);
        }

        if (dataConnection.getUpdateCount() > 0)
        {
            updateCodeListI18Entries(dataConnection, criteria, entry);
            return true;
        }
        
        return false;
    }

    /**
     * Update the CodeList I18N Table
     *
     * @param dataConnection
     * @param criteria
     * @param entry
     * @throws DataException
     */
    protected void updateCodeListI18Entries(JdbcDataConnection dataConnection, CodeListSaveCriteriaIfc criteria,
            CodeEntryIfc entry) throws DataException
    {
        boolean result = false;
        Locale[] supportedLocales = LocaleMap.getSupportedLocales();
        for (int i = 0; i < supportedLocales.length; i++)
        {
            Locale lcl = supportedLocales[i];
            result = updateCodeListI18Entry(dataConnection, criteria, entry);
            if (!result)
                insertCodeListI18Entry(dataConnection, criteria, entry, lcl);
        }
    }

    /**
     * Update the CodeList I18N Table
     *
     * @param dataConnection
     * @param criteria
     * @param entry
     * @throws DataException
     */
    protected boolean updateCodeListI18Entry(JdbcDataConnection dataConnection, CodeListSaveCriteriaIfc criteria,
            CodeEntryIfc entry) throws DataException
    {
        SQLUpdateStatement sql = buildUpdateCodeListI18EntrySQLStatement(criteria, entry);

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
            logger.error("JdbcSaveLocalizedCodeList.updateCodeListI18Entry", e);
            throw new DataException(DataException.UNKNOWN, "JdbcSaveLocalizedCodeList.updateCodeListI18Entry", e);
        }

        return (dataConnection.getUpdateCount() > 0);
    }

    /**
     * If there is no text set for a locale, replicate with the text set in the
     * userLocale
     *
     * @param text
     * @param locales
     * @param userLocale
     */
    protected String getLocalizedTextString(LocalizedTextIfc text, Locale lcl, Locale userLocale)
    {
        String stringText = text.getText(LocaleMap.getBestMatch(lcl));
        if (Util.isEmpty(stringText))
        {
            Locale bestMatch = LocaleMap.getBestMatch(userLocale);
            stringText = text.getText(bestMatch);
        }
        return stringText;
    }

    /**
     * Builds the SQL Statement to Insert a CodeList Entry
     *
     * @param criteria
     * @param entry
     * @return
     */
    protected SQLInsertStatement buildInsertCodeListEntrySQLStatement(CodeListSaveCriteriaIfc criteria, CodeEntryIfc entry)
            throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        CodeListIfc codeList = criteria.getCodeList();
        boolean isDefault = isDefaultEntry (codeList, entry.getCode());

        sql.setTable(TABLE_CODE_LIST);

        sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(codeList.getStoreID()));
        sql.addColumn(FIELD_CODE_LIST_DESCRIPTION, makeSafeString(codeList.getListDescription()));
        sql.addColumn(FIELD_CODE_LIST_GROUP_NAME, makeSafeString(codeList.getGroupName()));
        sql.addColumn(FIELD_CODE_LIST_NUMERIC_CODES_FLAG, makeStringFromBoolean(codeList.isNumericCodes()));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG, makeStringFromBoolean(isDefault));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_CODE, makeSafeString(entry.getCode()));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_SORT_INDEX, entry.getSortIndex());
        sql.addColumn(FIELD_CODE_LIST_ENTRY_ENABLED_FLAG, makeStringFromBoolean(entry.isEnabled()));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_TEXT, makeSafeString(entry.getCodeName())); // For backwards compatibility
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());

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
        CodeListIfc codeList = criteria.getCodeList();

        String localizedText = getLocalizedTextString(entry.getLocalizedText(), lcl, criteria.getUserLocale());
        sql.setTable(TABLE_CODE_LIST_I8);

        sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(codeList.getStoreID()));
        sql.addColumn(FIELD_CODE_LIST_DESCRIPTION, makeSafeString(codeList.getListDescription()));
        sql.addColumn(FIELD_CODE_LIST_GROUP_NAME, makeSafeString(codeList.getGroupName()));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_CODE, makeSafeString(entry.getCode()));
        sql.addColumn(FIELD_LOCALE, makeSafeString(lcl.toString()));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_TEXT, makeSafeString(localizedText));

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
            throws DataException
    {
        boolean isDefault = isDefaultEntry(codeList, entry.getCode());

        SQLUpdateStatement sql = new SQLUpdateStatement();

        sql.setTable(TABLE_CODE_LIST);

        sql.addColumn(FIELD_CODE_LIST_NUMERIC_CODES_FLAG, makeStringFromBoolean(codeList.isNumericCodes()));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG, makeStringFromBoolean(isDefault));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_CODE, makeSafeString(entry.getCode()));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_SORT_INDEX, entry.getSortIndex());
        sql.addColumn(FIELD_CODE_LIST_ENTRY_ENABLED_FLAG, makeStringFromBoolean(entry.isEnabled()));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());

        sql.addQualifier(FIELD_RETAIL_STORE_ID, makeSafeString(codeList.getStoreID()));
        sql.addQualifier(FIELD_CODE_LIST_DESCRIPTION, makeSafeString(codeList.getListDescription()));
        sql.addQualifier(FIELD_CODE_LIST_GROUP_NAME, makeSafeString(codeList.getGroupName()));
        sql.addQualifier(FIELD_CODE_LIST_ENTRY_CODE, makeSafeString(entry.getCode()));

        return sql;
    }

    /**
     * Builds the SQL Statement to update a codeList I18n entry
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
        CodeListIfc codeList = criteria.getCodeList();

        sql.setTable(TABLE_CODE_LIST_I8);

        sql.addColumn(FIELD_CODE_LIST_ENTRY_TEXT, makeSafeString(entry.getText(userLocale)));

        sql.addQualifier(FIELD_RETAIL_STORE_ID, makeSafeString(codeList.getStoreID()));
        sql.addQualifier(FIELD_CODE_LIST_DESCRIPTION, makeSafeString(codeList.getListDescription()));
        sql.addQualifier(FIELD_CODE_LIST_GROUP_NAME, makeSafeString(codeList.getGroupName()));
        sql.addQualifier(FIELD_CODE_LIST_ENTRY_CODE, makeSafeString(entry.getCode()));
        sql.addQualifier(FIELD_LOCALE, makeSafeString(LocaleMap.getBestMatch(userLocale).toString()));

        return sql;
    }

    /**
     * Checks if the Entry is the default for the codeList
     * @param codeList
     * @param code
     * @return
     */
    boolean isDefaultEntry (CodeListIfc codeList, String code)
    {
        boolean isDefault = false;
        if (codeList.getDefaultCodeString().equals(code))
            isDefault = true;
        return isDefault;
    }

}
