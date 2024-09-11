/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveLocalizedCodeListDiscount.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:04 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
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
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.CodeListSaveCriteriaIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;

/**
 * This Class saves a localized Reason Code in the appropriate table
 */
public class JdbcSaveLocalizedCodeListDiscount extends JdbcSaveLocalizedCodeList implements ARTSDatabaseIfc,
        DiscountRuleConstantsIfc
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
            throws DataException
    {
        CodeListIfc codeList = criteria.getCodeList();
        SQLInsertStatement sql = new SQLInsertStatement();

        int scope = getDiscountScope(codeList.getListDescription());
        int method = getDiscountMethod(codeList.getListDescription(), scope);
        int status = getDiscountStatus(entry);
        boolean isDefault = isDefaultEntry(codeList, entry.getCode());

        // Default Begin/End Promotion Dates
        EYSDate DEFAULT_BEGIN_DATE = new EYSDate(1980, 1, 1);
        EYSDate DEFAULT_END_DATE = new EYSDate(2040, 12, 31);

        sql.setTable(TABLE_PRICE_DERIVATION_RULE);

        // Fields
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_SCOPE_CODE, scope);
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE, method);
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_REASON_CODE, entry.getCode());
        sql.addColumn(FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG, makeStringFromBoolean(isDefault));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_STATUS_CODE, makeSafeString(STATUS_DESCRIPTORS[status]));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_SORT_INDEX, Integer.toString(entry.getSortIndex()));
        sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(codeList.getStoreID()));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE, ASSIGNMENT_MANUAL);
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ID, entry.getReferenceKey());
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_TRANSACTION_CONTROL_BREAK_CODE, "'" + APPLIED_CODES[APPLIED_DETAIL]
                + "'");
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_EFFECTIVE_DATE, dateToSQLTimestampString(DEFAULT_BEGIN_DATE));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_EXPIRATION_DATE, dateToSQLTimestampString(DEFAULT_END_DATE));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_INCLUDED_IN_BEST_DEAL_FLAG, makeStringFromBoolean(Boolean.valueOf(
                "false").booleanValue()));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_TYPE_CODE, "'"
                + DISCOUNT_APPLICATION_TYPE_CODE[DISCOUNT_APPLICATION_TYPE_MANUAL] + "'");

        // These values are being set for backwards compatibility
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_DESCRIPTION, makeSafeString(entry.getCodeName()));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_NAME, makeSafeString(entry.getCodeName()));

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

        String localizedText = getLocalizedTextString(entry.getLocalizedText(), lcl, criteria.getUserLocale());

        sql.setTable(TABLE_PRICE_DERIVATION_RULE_I8);

        sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(criteria.getCodeList().getStoreID()));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_NAME, makeSafeString(localizedText));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ID, makeSafeString(entry.getReferenceKey()));
        sql.addColumn(FIELD_LOCALE, makeSafeString(lcl.toString()));

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
    protected SQLUpdateStatement buildUpdateCodeListEntrySQLStatement(CodeListSaveCriteriaIfc criteria, CodeEntryIfc entry)
            throws DataException
    {
        CodeListIfc codeList = criteria.getCodeList();
        int scope = getDiscountScope(codeList.getListDescription());
        int method = getDiscountMethod(codeList.getListDescription(), scope);
        int status = getDiscountStatus(entry);
        boolean isDefault = isDefaultEntry(codeList, entry.getCode());

        SQLUpdateStatement sql = new SQLUpdateStatement();

        sql.setTable(TABLE_PRICE_DERIVATION_RULE);

        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_SCOPE_CODE, scope);
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE, method);
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_REASON_CODE, entry.getCode());
        sql.addColumn(FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG, makeStringFromBoolean(isDefault));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_STATUS_CODE, makeSafeString(STATUS_DESCRIPTORS[status]));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_SORT_INDEX, Integer.toString(entry.getSortIndex()));

        sql.addQualifier(FIELD_RETAIL_STORE_ID, makeSafeString(codeList.getStoreID()));
        sql.addQualifier(FIELD_PRICE_DERIVATION_RULE_ID , makeSafeString(entry.getReferenceKey()));

        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());


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
        CodeListIfc codeList = criteria.getCodeList();

        SQLUpdateStatement sql = new SQLUpdateStatement();

        sql.setTable(TABLE_PRICE_DERIVATION_RULE_I8);

        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_NAME, makeSafeString(entry.getText(userLocale)));

        sql.addQualifier(FIELD_RETAIL_STORE_ID, makeSafeString(codeList.getStoreID()));
        sql.addQualifier(FIELD_PRICE_DERIVATION_RULE_ID , makeSafeString(entry.getReferenceKey()));
        sql.addQualifier(FIELD_LOCALE, makeSafeString(LocaleMap.getBestMatch(userLocale).toString()));

        return sql;
    }

    /**
     * Returns discount scope derived from code list description.
     * <P>
     *
     * @param desc list description
     * @return scope constant
     * @exception DataException thrown if scope cannot be determined
     * @see oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc
     */
    // ---------------------------------------------------------------------
    protected int getDiscountScope(String desc) throws DataException
    { // begin getDiscountScope()
        int scope = -1;
        if (desc.equals(CodeConstantsIfc.CODE_LIST_TRANSACTION_DISCOUNT_BY_PERCENTAGE)
                || desc.equals(CodeConstantsIfc.CODE_LIST_TRANSACTION_DISCOUNT_BY_AMOUNT)
                || desc.equals(CodeConstantsIfc.CODE_LIST_PREFERRED_CUSTOMER_DISCOUNT))
        {
            scope = DISCOUNT_SCOPE_TRANSACTION;
        }
        else if (desc.equals(CodeConstantsIfc.CODE_LIST_ITEM_DISCOUNT_BY_PERCENTAGE)
                || desc.equals(CodeConstantsIfc.CODE_LIST_ITEM_DISCOUNT_BY_AMOUNT))
        {
            scope = DISCOUNT_SCOPE_ITEM;
        }
        else
        {
            // if not found, issue message and not-found exception
            String msg = "JdbcSaveCodeList.updatePriceDerivationRuleTable:  " + "Code list has unknown discount type ["
                    + desc + "].";
            throw new DataException(DataException.NO_DATA, msg);
        }

        return (scope);
    } // end getDiscountScope()

    // ---------------------------------------------------------------------
    /**
     * Returns discount method derived from code list description.
     * <P>
     *
     * @param desc list description
     * @param scope discount scope
     * @return method constant
     * @exception DataException thrown if method cannot be determined
     * @see oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc
     */
    // ---------------------------------------------------------------------
    protected int getDiscountMethod(String desc, int scope) throws DataException
    { // begin getDiscountMethod()
        int method = DISCOUNT_METHOD_NONE;
        switch (scope) {
        case DISCOUNT_SCOPE_TRANSACTION:
            if (desc.equals(CodeConstantsIfc.CODE_LIST_TRANSACTION_DISCOUNT_BY_PERCENTAGE))
            {
                method = DISCOUNT_METHOD_PERCENTAGE;
            }
            else if (desc.equals(CodeConstantsIfc.CODE_LIST_TRANSACTION_DISCOUNT_BY_AMOUNT))
            {
                method = DISCOUNT_METHOD_AMOUNT;
            }
            break;
        case DISCOUNT_SCOPE_ITEM:
            if (desc.equals(CodeConstantsIfc.CODE_LIST_ITEM_DISCOUNT_BY_PERCENTAGE))
            {
                method = DISCOUNT_METHOD_PERCENTAGE;
            }
            else if (desc.equals(CodeConstantsIfc.CODE_LIST_ITEM_DISCOUNT_BY_AMOUNT))
            {
                method = DISCOUNT_METHOD_AMOUNT;
            }
            break;
        }

        // if no method found, throw exception)
        if (method == DISCOUNT_METHOD_NONE)
        {
            // if not found, issue message and not-found exception
            String msg = "JdbcSaveCodeList.updatePriceDerivationRuleTable:  " + "Code list has unknown discount type ["
                    + desc + "].";
            throw new DataException(DataException.NO_DATA, msg);
        }

        return (method);
    }

    protected int getDiscountStatus(CodeEntryIfc entry)
    {
        if (entry.isEnabled())
            return STATUS_ACTIVE;
        else
            return STATUS_INACTIVE;

    }
}
