/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveLocalizedCodeListUOM.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
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

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.CodeListSaveCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;

/**
 * This Class saves a localized Reason Code in the appropriate table
 */
public class JdbcSaveLocalizedCodeListUOM extends JdbcSaveLocalizedCodeList implements ARTSDatabaseIfc
{

    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = 7122629844966967034L;

    /**
     * Builds the SQL Statement to Insert a CodeList Entry
     *
     * @param codeList
     * @param entry
     * @return
     */
    protected SQLInsertStatement buildInsertCodeListEntrySQLStatement(CodeListSaveCriteriaIfc criteria, CodeEntryIfc entry)
    {
        CodeListIfc codeList = criteria.getCodeList();
        SQLInsertStatement sql = new SQLInsertStatement();

        boolean isDefault = isDefaultEntry(codeList, entry.getCode());
        sql.setTable(TABLE_UNIT_OF_MEASURE);

        sql.addColumn(FIELD_UNIT_OF_MEASURE_CODE, makeSafeString(entry.getCode()));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG, makeStringFromBoolean(isDefault));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_SORT_INDEX, entry.getSortIndex());
        sql.addColumn(FIELD_CODE_LIST_ENTRY_ENABLED_FLAG, makeStringFromBoolean(entry.isEnabled()));

        // This value is being set for backwards compatibility
        sql.addColumn(FIELD_UNIT_OF_MEASURE_NAME, makeSafeString(entry.getCodeName()));

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
        sql.setTable(TABLE_UNIT_OF_MEASURE_I8);

        sql.addColumn(FIELD_UNIT_OF_MEASURE_CODE, makeSafeString(entry.getCode()));
        sql.addColumn(FIELD_LOCALE, makeSafeString(lcl.toString()));
        sql.addColumn(FIELD_UNIT_OF_MEASURE_NAME, makeSafeString(localizedText));

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
        boolean isDefault = isDefaultEntry(codeList, entry.getCode());

        SQLUpdateStatement sql = new SQLUpdateStatement();

        sql.setTable(TABLE_UNIT_OF_MEASURE);

        sql.addColumn(FIELD_UNIT_OF_MEASURE_CODE, makeSafeString(entry.getCode()));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG, makeStringFromBoolean(isDefault));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_SORT_INDEX, entry.getSortIndex());
        sql.addColumn(FIELD_CODE_LIST_ENTRY_ENABLED_FLAG, makeStringFromBoolean(entry.isEnabled()));

        sql.addQualifier(FIELD_UNIT_OF_MEASURE_CODE, makeSafeString(entry.getCode()));

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

        sql.setTable(TABLE_UNIT_OF_MEASURE_I8);

        sql.addColumn(FIELD_UNIT_OF_MEASURE_NAME, makeSafeString(entry.getText(userLocale)));

        sql.addQualifier(FIELD_UNIT_OF_MEASURE_CODE, makeSafeString(entry.getCode()));
        sql.addQualifier(FIELD_LOCALE, makeSafeString(LocaleMap.getBestMatch(userLocale).toString()));

        return sql;
    }
}
