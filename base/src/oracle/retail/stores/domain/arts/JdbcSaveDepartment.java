/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveDepartment.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:05 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    cgreene   10/05/09 - XbranchMerge cgreene_bug8931126-storehist3 from
 *                         rgbustores_13.1x_branch
 *    cgreene   10/05/09 - un-override methods used by super-implementations
 *    cgreene   09/25/09 - switched to using straight currency instead of
 *                         bigdecimal
 *    cgreene   09/24/09 - refactor SQL statements up support
 *                         preparedStatements for updates and inserts to
 *                         improve dept hist perf
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         4/1/2008 11:25:11 AM   Jack G. Swan
 *         Modified to include non-taxable restocking fee.
 *    7    360Commerce 1.6         8/15/2007 11:34:49 AM  Alan N. Sinton  CR
 *         28333 Netting out the restocking fees from the department sales
 *         report.
 *    6    360Commerce 1.5         5/14/2007 6:08:34 PM   Sandy Gu
 *         update inclusive information in financial totals and history tables
 *    5    360Commerce 1.4         4/25/2007 10:01:10 AM  Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/25/2006 4:11:21 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:48 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:01 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:09    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:43     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:48     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:01     Robert Pearse
 *
 *   Revision 1.8  2004/05/11 23:03:01  jdeleau
 *   @scr 4218 Backout recent changes to remove TransactionDiscounts,
 *   going to go a different route and remove the newly added
 *   voids and grosses instead.
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:38  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:40   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:39:30   msg
 * Initial revision.
 *
 *    Rev 1.2   16 Apr 2002 13:18:26   sfl
 * Added the gross value columns and value obtain
 * methods so that the gross values will be saved
 * into the POSDepartmentHistory table for later
 * reading. This is to replace using net values to
 * compute the gross values.
 * Resolution for POS SCR-1579: Store gross figures in the DB (financials)
 *
 *    Rev 1.1   Mar 18 2002 22:48:12   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:04   msg
 * Initial revision.
 *
 *    Rev 1.1   06 Feb 2002 18:15:40   sfl
 * To apply safeSQLCast to support Postgresql database.
 * Resolution for Domain SCR-28: Porting POS 5.0 to Postgresql
 *
 *    Rev 1.0   Sep 20 2001 15:57:12   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.util.Calendar;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.commerceservices.common.currency.CurrencyDecimal;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLParameterValue;
import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReportingPeriodIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;

/**
 * This operation modifies the
 * {@link ARTSDatabaseIfc#TABLE_POS_DEPARTMENT_HISTORY} table.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public abstract class JdbcSaveDepartment extends JdbcSaveReportingPeriod
                                         implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 720367413112206604L;

    private static final CurrencyTypeIfc BASE_TYPE = DomainGateway.getFactory().getCurrencyTypeInstance();
    private static final CurrencyIfc ZERO = DomainGateway.getFactory().getCurrencyInstance(BASE_TYPE);

    /**
     * Adds the financial totals to the appropriate record in the POS Department
     * History table.
     * 
     * @param dataConnection connection to the db
     * @param deptID the department ID
     * @param businessDate the identifying business date
     * @param totals the financial totals information to a  dd
     * @exception DataException thrown when an error occurs.
     */
    public void addPOSDepartmentTotals(JdbcDataConnection dataConnection,
                                       String deptID,
                                       EYSDate businessDate,
                                       FinancialTotalsIfc totals)
                                       throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Define the table
        sql.setTable(TABLE_POS_DEPARTMENT_HISTORY);

        // Add columns and their values
        addFinancialTotals(sql, totals);

        // Add Qualifier(s)
        sql.addQualifier(new SQLParameterValue(FIELD_POS_DEPARTMENT_ID, deptID));
        sql.addQualifier(new SQLParameterValue(FIELD_FISCAL_YEAR, getFiscalYearCode(businessDate)));
        sql.addQualifier(new SQLParameterValue(FIELD_REPORTING_PERIOD_TYPE_CODE, getReportingPeriodTypeCode(businessDate)));
        sql.addQualifier(new SQLParameterValue(FIELD_REPORTING_PERIOD_ID, getReportingPeriod(businessDate)));

        // execute sql
        dataConnection.execute(sql.getSQLString(), sql.getParameterValues());

        if (dataConnection.getUpdateCount() <= 0)
        {
            // Throw an exception if there was no record to update
            throw new DataException(DataException.NO_DATA, "addPOSDepartmentTotals");
        }
    }

    /**
     * Updates the POS Department History table with the financial totals.
     * 
     * @param dataConnection connection to the db
     * @param deptID the department ID
     * @param businessDate the identifying business date
     * @param totals the financial totals information to add
     * @exception DataException thrown when an error occurs.
     */
    public void updatePOSDepartmentTotals(JdbcDataConnection dataConnection,
                                          String deptID,
                                          EYSDate businessDate,
                                          FinancialTotalsIfc totals)
                                          throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Define the table
        sql.setTable(TABLE_POS_DEPARTMENT_HISTORY);

        // Add columns and their values
        getFinancialTotals(sql, totals);


        // Add Qualifier(s)
        sql.addQualifier(new SQLParameterValue(FIELD_POS_DEPARTMENT_ID, deptID));
        sql.addQualifier(new SQLParameterValue(FIELD_FISCAL_YEAR, getFiscalYearCode(businessDate)));
        sql.addQualifier(new SQLParameterValue(FIELD_REPORTING_PERIOD_TYPE_CODE, getReportingPeriodTypeCode(businessDate)));
        sql.addQualifier(new SQLParameterValue(FIELD_REPORTING_PERIOD_ID, getReportingPeriod(businessDate)));

        dataConnection.execute(sql.getSQLString(), sql.getParameterValues());

        if (dataConnection.getUpdateCount() <= 0)
        {
            // Throw an exception if there was no record to update
            throw new DataException(DataException.NO_DATA, "updatePOSDepartmentTotals");
        }
    }

    /**
     * Adds a new POS Department History record.
     * 
     * @param dataConnection connection to the db
     * @param deptID the department ID
     * @param businessDate the identifying business date
     * @param totals the financial totals information to add
     * @exception DataException thrown when an error occurs.
     */
    protected void insertPOSDepartmentTotals(JdbcDataConnection dataConnection,
                                             String deptID,
                                             EYSDate businessDate,
                                             FinancialTotalsIfc totals)
                                             throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Define table
        sql.setTable(TABLE_POS_DEPARTMENT_HISTORY);

        // Add columns and their values
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPARTMENT_ID, deptID));
        sql.addColumn(new SQLParameterValue(FIELD_FISCAL_YEAR, getFiscalYearCode(businessDate)));
        sql.addColumn(new SQLParameterValue(FIELD_REPORTING_PERIOD_TYPE_CODE, getReportingPeriodTypeCode(businessDate)));
        sql.addColumn(new SQLParameterValue(FIELD_REPORTING_PERIOD_ID, getReportingPeriod(businessDate)));

        getFinancialTotals(sql, totals);

        dataConnection.execute(sql.getSQLString(), sql.getParameterValues());
    }

    /**
     * Adds the columns and values for the financial totals fields
     * 
     * @param sql The SQL statement to add the column-value pairs to
     * @param totals The financial totals to draw the values from
     */
    protected void getFinancialTotals(SQLUpdatableStatementIfc sql, FinancialTotalsIfc totals)
    {
        /*
         * These two are only here because the exist in ARTS.
         * I don't think that they will ever contain values.
         */
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_REFUND_TOTAL_AMOUNT, getRefundAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_REFUND_COUNT, getRefundCount(totals)));

        // Item Sales/Returns
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_NET_ITEM_SALES_TOTAL_AMOUNT, getNetItemSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_SALE_LINE_ITEM_COUNT, getItemSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT, getItemNontaxableAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_NONTAXABLE_LINE_ITEM_COUNT, getItemNontaxableCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TAX_EXEMPT_TOTAL_AMOUNT, getItemTaxExemptAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_TAX_EXEMPT_TRANSACTION_COUNT, getItemTaxExemptCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_RETURN_TOTAL_AMOUNT, getItemReturnAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_RETURN_COUNT, getItemReturnCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_RETURN_NONTAXABLE_TOTAL_AMOUNT, getReturnNontaxableAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_NONTAXABLE_RETURN_COUNT, getReturnNontaxableCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_RETURN_TAX_EXEMPT_TOTAL_AMOUNT, getReturnTaxExemptAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_TAX_EXEMPT_RETURN_COUNT, getReturnTaxExemptCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_GROSS_TAXABLE_ITEM_SALES_AMOUNT, getTaxableItemSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_GROSS_TAXABLE_ITEM_SALES_COUNT, getTaxableItemSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_GROSS_TAXABLE_ITEM_RETURNS_AMOUNT, getTaxableItemReturnsAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_GROSS_TAXABLE_ITEM_RETURNS_COUNT, getTaxableItemReturnsCount(totals)));
        // Tax
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TAX_TOTAL_AMOUNT, getNetTaxAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_INCLUSIVE_TAX_TOTAL_AMOUNT, getNetInclusiveTaxAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_RETURN_TAX_TOTAL_AMOUNT, getTaxReturnedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_RETURN_INCLUSIVE_TAX_TOTAL_AMOUNT, getInclusiveTaxReturnedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_ITEM_SALES_TAX_AMOUNT, totals.getAmountTaxItemSales()));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_ITEM_SALES_INCLUSIVE_TAX_AMOUNT, totals.getAmountInclusiveTaxItemSales()));
        // Misc
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_MISCELLANEOUS_DISCOUNT_TOTAL_AMOUNT, getTransactionDiscountAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_MISCELLANEOUS_DISCOUNT_COUNT, getTransactionDiscountCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_MARKDOWN_TOTAL_AMOUNT, getMarkdownAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_MARKDOWN_COUNT, getMarkdownCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_POST_TRANSACTION_VOID_TOTAL_AMOUNT, getPostVoidAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_POST_TRANSACTION_VOID_COUNT, getPostVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_LINE_ITEM_VOID_TOTAL_AMOUNT, getLineVoidAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_LINE_ITEM_VOID_COUNT, getLineVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TRANSACTION_VOID_TOTAL_AMOUNT, getVoidAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_TRANSACTION_VOID_COUNT, getVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_RESTOCKING_FEE_TOTAL_AMOUNT, getRestockingFeeAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_RESTOCKING_FEE_COUNT, getRestockingFeeCount(totals)));
    }

    /**
     * Adds the financial totals to the current values.
     * 
     * @param sql The SQL statement to add the column-value pairs to
     * @param totals The financial totals to draw the values from
     */
    protected void addFinancialTotals(SQLUpdatableStatementIfc sql, FinancialTotalsIfc totals)
    {
        /*
         * These two are only here because the exist in ARTS.
         * I don't think that they will ever contain values.
         */
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_REFUND_TOTAL_AMOUNT + "="
                + FIELD_POS_DEPT_REFUND_TOTAL_AMOUNT + "+ ?", getRefundAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_REFUND_COUNT + "=" + FIELD_POS_DEPT_TOTAL_REFUND_COUNT
                + "+ ?", getRefundCount(totals)));

        // Item Sales/Returns
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_NET_ITEM_SALES_TOTAL_AMOUNT + "="
                + FIELD_POS_DEPT_NET_ITEM_SALES_TOTAL_AMOUNT + "+ ?", getNetItemSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_SALE_LINE_ITEM_COUNT + "="
                + FIELD_POS_DEPT_TOTAL_SALE_LINE_ITEM_COUNT + "+ ?", getItemSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT + "="
                + FIELD_POS_DEPT_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT + "+ ?", getItemNontaxableAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_NONTAXABLE_LINE_ITEM_COUNT + "="
                + FIELD_POS_DEPT_TOTAL_NONTAXABLE_LINE_ITEM_COUNT + "+ ?", getItemNontaxableCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TAX_EXEMPT_TOTAL_AMOUNT + "="
                + FIELD_POS_DEPT_TAX_EXEMPT_TOTAL_AMOUNT + "+ ?", getItemTaxExemptAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_TAX_EXEMPT_TRANSACTION_COUNT + "="
                + FIELD_POS_DEPT_TOTAL_TAX_EXEMPT_TRANSACTION_COUNT + "+ ?", getItemTaxExemptCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_RETURN_TOTAL_AMOUNT + "="
                + FIELD_POS_DEPT_RETURN_TOTAL_AMOUNT + "+ ?", getItemReturnAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_RETURN_COUNT + "=" + FIELD_POS_DEPT_TOTAL_RETURN_COUNT
                + "+ ?", getItemReturnCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_RETURN_NONTAXABLE_TOTAL_AMOUNT + "="
                + FIELD_POS_DEPT_RETURN_NONTAXABLE_TOTAL_AMOUNT + "+ ?", getReturnNontaxableAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_NONTAXABLE_RETURN_COUNT + "="
                + FIELD_POS_DEPT_TOTAL_NONTAXABLE_RETURN_COUNT + "+ ?", getReturnNontaxableCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_RETURN_TAX_EXEMPT_TOTAL_AMOUNT + "="
                + FIELD_POS_DEPT_RETURN_TAX_EXEMPT_TOTAL_AMOUNT + "+ ?", getReturnTaxExemptAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_TAX_EXEMPT_RETURN_COUNT + "="
                + FIELD_POS_DEPT_TOTAL_TAX_EXEMPT_RETURN_COUNT + "+ ?", getReturnTaxExemptCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_GROSS_TAXABLE_ITEM_SALES_AMOUNT + "="
                + FIELD_POS_DEPT_GROSS_TAXABLE_ITEM_SALES_AMOUNT + "+ ?", getTaxableItemSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_GROSS_TAXABLE_ITEM_SALES_COUNT + "="
                + FIELD_POS_DEPT_GROSS_TAXABLE_ITEM_SALES_COUNT + "+ ?", getTaxableItemSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_GROSS_TAXABLE_ITEM_RETURNS_AMOUNT + "="
                + FIELD_POS_DEPT_GROSS_TAXABLE_ITEM_RETURNS_AMOUNT + "+ ?", getTaxableItemReturnsAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_GROSS_TAXABLE_ITEM_RETURNS_COUNT + "="
                + FIELD_POS_DEPT_GROSS_TAXABLE_ITEM_RETURNS_COUNT + "+ ?", getTaxableItemReturnsCount(totals)));
        // Tax
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TAX_TOTAL_AMOUNT + "=" + FIELD_POS_DEPT_TAX_TOTAL_AMOUNT
                + "+ ?", getNetTaxAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_INCLUSIVE_TAX_TOTAL_AMOUNT + "="
                + FIELD_POS_DEPT_INCLUSIVE_TAX_TOTAL_AMOUNT + "+ ?", getNetInclusiveTaxAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_RETURN_TAX_TOTAL_AMOUNT + "="
                + FIELD_POS_DEPT_RETURN_TAX_TOTAL_AMOUNT + "+ ?", getTaxReturnedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_RETURN_INCLUSIVE_TAX_TOTAL_AMOUNT + "="
                + FIELD_POS_DEPT_RETURN_INCLUSIVE_TAX_TOTAL_AMOUNT + "+ ?", getInclusiveTaxReturnedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_ITEM_SALES_TAX_AMOUNT + "="
                + FIELD_POS_DEPT_ITEM_SALES_TAX_AMOUNT + "+ ?", totals.getAmountTaxItemSales()));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_ITEM_SALES_INCLUSIVE_TAX_AMOUNT + "="
                + FIELD_POS_DEPT_ITEM_SALES_INCLUSIVE_TAX_AMOUNT + "+ ?", totals.getAmountInclusiveTaxItemSales()));
        // Misc
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_MISCELLANEOUS_DISCOUNT_TOTAL_AMOUNT + "="
                + FIELD_POS_DEPT_MISCELLANEOUS_DISCOUNT_TOTAL_AMOUNT + "+ ?", getTransactionDiscountAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_MISCELLANEOUS_DISCOUNT_COUNT + "="
                + FIELD_POS_DEPT_TOTAL_MISCELLANEOUS_DISCOUNT_COUNT + "+ ?", getTransactionDiscountCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_MARKDOWN_TOTAL_AMOUNT + "="
                + FIELD_POS_DEPT_MARKDOWN_TOTAL_AMOUNT + "+ ?", getMarkdownAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_MARKDOWN_COUNT + "="
                + FIELD_POS_DEPT_TOTAL_MARKDOWN_COUNT + "+ ?", getMarkdownCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_POST_TRANSACTION_VOID_TOTAL_AMOUNT + "="
                + FIELD_POS_DEPT_POST_TRANSACTION_VOID_TOTAL_AMOUNT + "+ ?", getPostVoidAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_POST_TRANSACTION_VOID_COUNT + "="
                + FIELD_POS_DEPT_TOTAL_POST_TRANSACTION_VOID_COUNT + "+ ?", getPostVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_LINE_ITEM_VOID_TOTAL_AMOUNT + "="
                + FIELD_POS_DEPT_LINE_ITEM_VOID_TOTAL_AMOUNT + "+ ?", getLineVoidAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_LINE_ITEM_VOID_COUNT + "="
                + FIELD_POS_DEPT_TOTAL_LINE_ITEM_VOID_COUNT + "+ ?", getLineVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TRANSACTION_VOID_TOTAL_AMOUNT + "="
                + FIELD_POS_DEPT_TRANSACTION_VOID_TOTAL_AMOUNT + "+ ?", getVoidAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_TRANSACTION_VOID_COUNT + "="
                + FIELD_POS_DEPT_TOTAL_TRANSACTION_VOID_COUNT + "+ ?", getVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_RESTOCKING_FEE_TOTAL_AMOUNT + "="
                + FIELD_POS_DEPT_RESTOCKING_FEE_TOTAL_AMOUNT + "+ ?", getRestockingFeeAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_POS_DEPT_TOTAL_RESTOCKING_FEE_COUNT + "="
                + FIELD_POS_DEPT_TOTAL_RESTOCKING_FEE_COUNT + "+ ?", getRestockingFeeCount(totals)));
    }

    /**
     * Returns the department id
     * 
     * @param deptID The department ID
     * @return the department id
     * @deprecated as of 13.1.1 quotes not needed
     */
    protected String getDepartmentID(String deptID)
    {
        return ("'" + deptID + "'");
    }

    /**
     * Returns the fiscal year. Assumes fiscal year and calendar year are the
     * same.
     * <p>
     * Replaces {@link #getFiscalYear(EYSDate)} to avoid quotes around the result.
     * 
     * @param businessDate the business date
     * @return the fiscal year
     */
    protected String getFiscalYearCode(EYSDate businessDate)
    {
        return Integer.toString(businessDate.getYear());
    }

    /**
     * Returns the reporting period type.
     * <p>
     * Replaces {@link #getReportingPeriodType(EYSDate)} to avoid quotes around the result.
     * 
     * @param businessDate the business date
     * @return the reporting period type
     */
    protected String getReportingPeriodTypeCode(EYSDate businessDate)
    {
        int code = ReportingPeriodIfc.TYPE_BUSINESS_DAY;
        return ReportingPeriodIfc.REPORTING_PERIOD_CODES[code];
    }

    /**
     * Returns the reporting period id.
     * <p>
     * Replaces {@link #getReportingPeriodID(EYSDate)}
     * 
     * @param businessDate the business date
     * @return the reporting period id
     */
    protected int getReportingPeriod(EYSDate businessDate)
    {
        Calendar c = businessDate.calendarValue();
        return c.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Returns the monetary amount of line item voids
     * 
     * @param totals The financial totals to extract the information from
     * @return the monetary amount of line item voids
     */
    protected CurrencyIfc getLineVoidAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountLineVoids();
        }
        return value;
    }

    /**
     * Returns the number of line item voids
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of line item voids
     */
    protected BigDecimal getLineVoidCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = CurrencyDecimal.BIG_ZERO_TWO;
        if (totals != null)
        {
            value = totals.getUnitsLineVoids();
        }
        return value;
    }

    /**
     * Returns the monetary amount of post transaction voids
     * 
     * @param totals The financial totals to extract the information from
     * @return the monetary amount of post transaction voids
     */
    protected CurrencyIfc getPostVoidAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountPostVoids();
        }
        return value;
    }

    /**
     * Returns the number of post transaction voids
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of post transaction voids
     */
    protected int getPostVoidCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getNumberPostVoids();
        }
        return value;
    }

    /**
     * Returns the monetary amount of transaction voids
     * 
     * @param totals The financial totals to extract the information from
     * @return the monetary amount of transaction voids
     */
    protected CurrencyIfc getVoidAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountCancelledTransactions();
        }
        return value;
    }

    /**
     * Returns the number of transaction voids
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of transaction voids
     */
    protected int getVoidCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getNumberCancelledTransactions();
        }
        return value;
    }

    /**
     * Returns the monetary amount of transaction voids
     * 
     * @param totals The financial totals to extract the information from
     * @return the monetary amount of restocking fees.
     */
    protected CurrencyIfc getRestockingFeeAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountRestockingFees().add(totals.getAmountRestockingFeesFromNonTaxableItems());
        }
        return value;
    }

    /**
     * Returns the number of restocking fees.
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of restocking fees.
     */
    protected BigDecimal getRestockingFeeCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = CurrencyDecimal.BIG_ZERO_TWO;
        if (totals != null)
        {
            value = totals.getUnitsRestockingFees().add(totals.getUnitsRestockingFeesFromNonTaxableItems());
        }
        return value;
    }

    /**
     * Returns the monetary amount of transaction discounts
     * 
     * @param totals The financial totals to extract the information from
     * @return the monetary amount of transaction discounts
     */
    protected CurrencyIfc getTransactionDiscountAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountTransactionDiscounts();
        }
        return value;
    }

    /**
     * Returns the number of transaction discounts
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of transaction discounts
     */
    protected int getTransactionDiscountCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getNumberTransactionDiscounts();
        }
        return value;
    }

    /**
     * Returns the monetary amount of price markdowns
     * 
     * @param totals The financial totals to extract the information from
     * @return the monetary amount of price markdowns
     */
    protected CurrencyIfc getMarkdownAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountItemDiscounts();
        }
        return value;
    }

    /**
     * Returns the number of price markdowns
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of price markdowns
     */
    protected int getMarkdownCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getNumberItemDiscounts();
        }
        return value;
    }

    /**
     * Returns the refund amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the refund amount
     */
    protected CurrencyIfc getRefundAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableTransactionReturns()
                          .subtract(totals.getAmountGrossTaxableTransactionReturnsVoided())
                        .add(totals.getAmountGrossNonTaxableTransactionReturns()
                                   .subtract(totals.getAmountGrossNonTaxableTransactionReturnsVoided()));
        }
        return value;
    }

    /**
     * Returns the number of refunds
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of refunds
     */
    protected int getRefundCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountGrossTaxableTransactionReturns()
                                   - totals.getCountGrossTaxableTransactionReturnsVoided()
                                   + (totals.getCountGrossNonTaxableTransactionReturns()
                                      - totals.getCountGrossNonTaxableTransactionReturnsVoided());
        }
        return value;
    }

    /**
     * Returns the net item sales amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the net item sales amount
     */
    protected CurrencyIfc getNetItemSalesAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountNetItemSales();
        }
        return value;
    }

    /**
     * Returns the number of line items sold or returned
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of line items sold or returned
     */
    protected BigDecimal getItemSalesCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = CurrencyDecimal.BIG_ZERO_TWO;
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxableItemSales()
                          .subtract(totals.getUnitsGrossTaxableItemSalesVoided())
                          .add(totals.getUnitsGrossNonTaxableItemSales()
                               .subtract(totals.getUnitsGrossNonTaxableItemSalesVoided()));
        }
        return value;
    }

    /**
     * Returns the nontaxable item sales amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the nontaxable item sales amount
     */
    protected CurrencyIfc getItemNontaxableAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossNonTaxableItemSales()
                          .subtract(totals.getAmountGrossNonTaxableItemSalesVoided());
        }
        return(value);
    }

    /**
     * Returns the number of nontaxable line items sold
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of nontaxale line items sold
     */
    protected BigDecimal getItemNontaxableCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = CurrencyDecimal.BIG_ZERO_TWO;
        if (totals != null)
        {
            value = totals.getUnitsGrossNonTaxableItemSales()
                          .subtract(totals.getUnitsGrossNonTaxableItemSalesVoided());
        }
        return(value);
    }

    /**
     * Returns the tax exempt item sales amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the tax exempt item sales amount
     */
    protected CurrencyIfc getItemTaxExemptAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTaxExemptItemSales()
                          .subtract(totals.getAmountGrossTaxExemptItemSalesVoided());
        }
        return(value);
    }

    /**
     * Returns the number of tax exempt line items sold
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of tax exempt line items sold
     */
    protected BigDecimal getItemTaxExemptCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = CurrencyDecimal.BIG_ZERO_TWO;
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxExemptItemSales()
                          .subtract(totals.getUnitsGrossTaxExemptItemSalesVoided());
        }
        return(value);
    }

    /**
     * Returns the return amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the return amount
     */
    protected CurrencyIfc getItemReturnAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableItemReturns()
                          .subtract(totals.getAmountGrossTaxableItemReturnsVoided())
                          .add(totals.getAmountGrossNonTaxableItemReturns()
                               .subtract(totals.getAmountGrossNonTaxableItemReturnsVoided()));
        }
        return(value);
    }

    /**
     * Returns the number of returns
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of returns
     */
    protected BigDecimal getItemReturnCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = CurrencyDecimal.BIG_ZERO_TWO;
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxableItemReturns()
                          .subtract(totals.getUnitsGrossTaxableItemReturnsVoided())
                          .add(totals.getUnitsGrossNonTaxableItemReturns()
                               .subtract(totals.getUnitsGrossNonTaxableItemReturnsVoided()));
        }
        return(value);
    }

    /**
     * Returns the nontaxable item return amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the nontaxable item return amount
     */
    protected CurrencyIfc getReturnNontaxableAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossNonTaxableItemReturns()
                          .subtract(totals.getAmountGrossNonTaxableItemReturnsVoided());
        }
        return(value);
    }

    /**
     * Returns the number of nontaxable line items returned
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of nontaxale line items returned
     */
    protected BigDecimal getReturnNontaxableCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = CurrencyDecimal.BIG_ZERO_TWO;
        if (totals != null)
        {
            value = totals.getUnitsGrossNonTaxableItemReturns()
                          .subtract(totals.getUnitsGrossNonTaxableItemReturnsVoided());
        }
        return(value);
    }

    /**
     * Returns the tax exempt item return amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the tax exempt item return amount
     */
    protected CurrencyIfc getReturnTaxExemptAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTaxExemptItemReturns()
                          .subtract(totals.getAmountGrossTaxExemptItemReturnsVoided());
        }
        return(value);
    }

    /**
     * Returns the number of tax exempt line items returned
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of tax exempt line items returned
     */
    protected BigDecimal getReturnTaxExemptCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = CurrencyDecimal.BIG_ZERO_TWO;
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxExemptItemReturns()
                          .subtract(totals.getUnitsGrossTaxExemptItemReturnsVoided());
        }
        return(value);
    }

    /**
     * Returns the net tax amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the net tax amount
     */
    protected CurrencyIfc getNetTaxAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            CurrencyIfc taxItemReturns = totals.getAmountTaxItemReturns();
            value = totals.getAmountTaxItemSales().subtract(taxItemReturns);
        }
        return(value);
    }
    
    /**
     * Returns the net inclusive tax amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the net inclusive tax amount
     */
    protected CurrencyIfc getNetInclusiveTaxAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            CurrencyIfc taxItemReturns = totals.getAmountInclusiveTaxItemReturns();
            value = totals.getAmountInclusiveTaxItemSales().subtract(taxItemReturns);
        }
        return value;
    }

    /**
     * Returns the tax returned amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the tax returned amount
     */
    protected CurrencyIfc getTaxReturnedAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountTaxItemReturns();
        }
        return value;
    }

    /**
     * Returns the inclusive tax returned amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the inclusive tax returned amount
     */
    protected CurrencyIfc getInclusiveTaxReturnedAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountInclusiveTaxItemReturns();
        }
        return value;
    }

    /**
     * Returns the taxable item sales amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the taxable item sales amount
     */
    protected CurrencyIfc getTaxableItemSalesAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableItemSales()
                          .subtract(totals.getAmountGrossTaxableItemSalesVoided());
        }
        return(value);
    }

    /**
     * Returns the taxable item returns amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the taxable item returns amount
     */
    protected CurrencyIfc getTaxableItemReturnsAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableItemReturns()
                          .subtract(totals.getAmountGrossTaxableItemReturnsVoided());
        }
        return(value);
    }

    /**
     * Returns the number of taxable line items sold
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of taxable line items sold
     */
    protected BigDecimal getTaxableItemSalesCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = CurrencyDecimal.BIG_ZERO_TWO;
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxableItemSales()
                          .subtract(totals.getUnitsGrossTaxableItemSalesVoided());
        }
        return value;
    }

    /**
     * Returns the number of taxable line items returned
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of taxable line items returned
     */
    protected BigDecimal getTaxableItemReturnsCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = CurrencyDecimal.BIG_ZERO_TWO;
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxableItemReturns()
                          .subtract(totals.getUnitsGrossTaxableItemReturnsVoided());
        }
        return value;
    }
}
