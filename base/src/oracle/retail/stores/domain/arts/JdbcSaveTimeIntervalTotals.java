/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveTimeIntervalTotals.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         4/1/2008 11:25:11 AM   Jack G. Swan
 *         Modified to save non-taxable restocking fee.
 *    5    360Commerce 1.4         1/25/2006 4:11:25 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         1/22/2006 11:41:23 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:04 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:52    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:45     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:50     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:04     Robert Pearse
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
 *   Revision 1.3  2004/02/12 17:13:19  mcs
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
 *    Rev 1.0   Aug 29 2003 15:33:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:40:26   msg
 * Initial revision.
 * 
 *    Rev 1.2   25 Mar 2002 16:54:30   pdd
 * Switched the workstation start date from the system date to the business date.
 * Resolution for POS SCR-1163: Hourly Productivity Report prints multiple entries of the same hour for multiple business days
 * 
 *    Rev 1.1   Mar 18 2002 22:49:00   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:08:44   msg
 * Initial revision.
 * 
 *    Rev 1.8   27 Feb 2002 13:56:44   pdd
 * Removed register id qualifier from addTimeIntervalTotals().
 * Resolution for POS SCR-1163: Hourly Productivity Report prints multiple entries of the same hour
 * 
 *    Rev 1.7   Feb 16 2002 17:30:12   dfh
 * backed out changes to version 1.5
 * Resolution for POS SCR-1281: Special Order complete does not update sales tax count/amount on Summary report
 * 
 *    Rev 1.5   07 Feb 2002 11:40:24   sfl
 * To apply safeSQLCast on total amount and total counts
 * to support Postgresql database.
 * Resolution for Domain SCR-8: vabcdomain changes
 * Resolution for Domain SCR-28: Porting POS 5.0 to Postgresql
 *
 *    Rev 1.4   06 Feb 2002 16:59:24   pdd
 * Added gross figures for clarity.
 * Resolution for POS SCR-140: Hourly Productivity Report: Invalid hourly sales value
 *
 *    Rev 1.3   Feb 05 2002 16:33:50   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.2   18 Dec 2001 13:52:08   sfl
 * Included shipping charge information in the
 * workstation time interval activity history data
 * saving.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.1   02 Dec 2001 12:48:04   mpm
 * Implemented financials, voids for special order domain objects.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 20 2001 15:57:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;

/**
    Abstract class that contains the database calls for adding/inserting/
    updating workstation time interval totals.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
public abstract class JdbcSaveTimeIntervalTotals extends JdbcDataOperation
                                                 implements ARTSDatabaseIfc
{
    /**

        The logger to which log messages will be sent.
    *
     */
    protected static final Logger logger = Logger.getLogger(JdbcSaveTimeIntervalTotals.class);

    /**
        Adds the financial totals information to the specified workstation
        time interval history.
        <p>
        @param  dataConnection  connection to the db
        @param  transaction     The transaction information
        @exception DataException upon error
     */
    public void addTimeIntervalTotals(JdbcDataConnection dataConnection,
                                      TenderableTransactionIfc transaction)
                                      throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();
        sql.setTable(TABLE_WORKSTATION_TIME_ACTIVITY_HISTORY);
        addFinancialTotals(sql, transaction.getFinancialTotals());
        /*
         * Add Qualifier(s)
         */
        // For the specified workstation only
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        // This is commented out because we don't currently need to distinguish this data
        // by register. We want one report for all registers.
        //sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        // For the specified time period only
        sql.addQualifier(FIELD_WORKSTATION_DATE + " = " + getWorkstationDate(transaction));
        sql.addQualifier(FIELD_TIME_PERIOD_HOUR + " = " + getTimePeriodHour(transaction));
        sql.addQualifier(FIELD_TIME_PERIOD_MINUTE_INTERVAL_COUNT
                         + " = " + getTimePeriodMinute(transaction));
        sql.addQualifier(FIELD_TIME_PERIOD_INTERVAL_PER_HOUR_COUNT
                         + " = " + getTimePeriodInterval(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.warn( de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "addTimeIntervalTotals", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA,
                                    "Could not update Workstation Time Activity record");
        }
    }

    /**
        Inserts a workstation time activity total for the time interval.
        <p>
        @param  dataConnection  connection to the db
        @param  transaction     The transaction information
        @exception DataException upon error
     */
    public void insertTimeIntervalTotals(JdbcDataConnection dataConnection,
                                         TenderableTransactionIfc transaction)
                                         throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        sql.setTable(TABLE_WORKSTATION_TIME_ACTIVITY_HISTORY);

        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_WORKSTATION_DATE, getWorkstationDate(transaction));
        sql.addColumn(FIELD_TIME_PERIOD_HOUR, getTimePeriodHour(transaction));
        sql.addColumn(FIELD_TIME_PERIOD_MINUTE_INTERVAL_COUNT, getTimePeriodMinute(transaction));
        sql.addColumn(FIELD_TIME_PERIOD_INTERVAL_PER_HOUR_COUNT, getTimePeriodInterval(transaction));

        getFinancialTotals(sql, transaction.getFinancialTotals());

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.warn(
                        de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertTimeIntervalTotals", e);
        }
    }

    /**
        Adds the financial totals to the current values.
        <p>
        @param  sql     The SQL statement to add the column-value pairs to
        @param  totals  The financial totals to draw the values from
     */
    protected void addFinancialTotals(SQLUpdatableStatementIfc sql, FinancialTotalsIfc totals)
    {
        // Transaction Sales/Returns
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_REFUND_COUNT,
                      FIELD_WORKSTATION_TIME_PERIOD_TOTAL_REFUND_COUNT + "+" + safeSQLCast(getRefundCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_NONTAXABLE_REFUND_COUNT,
                      FIELD_WORKSTATION_TIME_PERIOD_TOTAL_NONTAXABLE_REFUND_COUNT + "+" + safeSQLCast(getNontaxableRefundCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TRANSACTION_COUNT,
                      FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TRANSACTION_COUNT + "+" + safeSQLCast(getTransactionCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_SALES_COUNT,
                      FIELD_WORKSTATION_TIME_PERIOD_TOTAL_SALES_COUNT + "+" + safeSQLCast(getSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_NET_SALES_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TIME_PERIOD_NET_SALES_TOTAL_AMOUNT + "+" + safeSQLCast(getNetSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_NONTAXABLE_SALES_COUNT,
                      FIELD_WORKSTATION_TIME_PERIOD_TOTAL_NONTAXABLE_SALES_COUNT + "+" + safeSQLCast(getNontaxableSalesCount(totals)));
        // Item Sales/Returns
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_SALE_LINE_ITEM_COUNT,
                      FIELD_WORKSTATION_TIME_PERIOD_TOTAL_SALE_LINE_ITEM_COUNT + "+" + safeSQLCast(getItemSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TIME_PERIOD_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT + "+" + safeSQLCast(getItemNontaxableAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_NONTAXABLE_LINE_ITEM_COUNT,
                      FIELD_WORKSTATION_TIME_PERIOD_TOTAL_NONTAXABLE_LINE_ITEM_COUNT + "+" + safeSQLCast(getItemNontaxableCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_RETURN_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TIME_PERIOD_RETURN_TOTAL_AMOUNT + "+" + safeSQLCast(getItemReturnAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_RETURN_COUNT,
                      FIELD_WORKSTATION_TIME_PERIOD_TOTAL_RETURN_COUNT + "+" + safeSQLCast(getItemReturnCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_RETURN_NONTAXABLE_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TIME_PERIOD_RETURN_NONTAXABLE_TOTAL_AMOUNT + "+" + safeSQLCast(getReturnNontaxableAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_NONTAXABLE_RETURN_COUNT,
                      FIELD_WORKSTATION_TIME_PERIOD_TOTAL_NONTAXABLE_RETURN_COUNT + "+" + safeSQLCast(getReturnNontaxableCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_HOUSE_PAYMENT_AMOUNT,
                      FIELD_WORKSTATION_TIME_PERIOD_HOUSE_PAYMENT_AMOUNT + "+" + safeSQLCast(getHousePaymentAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_HOUSE_PAYMENT_COUNT,
                      FIELD_WORKSTATION_TIME_PERIOD_HOUSE_PAYMENT_COUNT + "+" + safeSQLCast(getHousePaymentCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_RESTOCKING_FEE_AMOUNT,
                      FIELD_WORKSTATION_TIME_PERIOD_RESTOCKING_FEE_AMOUNT + "+" + safeSQLCast(getRestockingFeeAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_RESTOCKING_FEE_COUNT,
                      FIELD_WORKSTATION_TIME_PERIOD_RESTOCKING_FEE_COUNT + "+" + safeSQLCast(getRestockingFeeCount(totals)));
        if (totals != null)
        {
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TAXABLE_REFUND_COUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TAXABLE_REFUND_COUNT + "+"
                          + String.valueOf(totals.getCountGrossTaxableTransactionReturns()
                                           - totals.getCountGrossTaxableTransactionReturnsVoided()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TAXABLE_SALES_COUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TAXABLE_SALES_COUNT + "+"
                          + String.valueOf(totals.getCountGrossTaxableTransactionSales()
                                           - totals.getCountGrossTaxableTransactionSalesVoided()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LINE_ITEM_TAXABLE_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_LINE_ITEM_TAXABLE_TOTAL_AMOUNT + "+"
                          + safeSQLCast(totals.getAmountGrossTaxableItemSales()
                                  .subtract(totals.getAmountGrossTaxableItemSalesVoided()).getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TAXABLE_LINE_ITEM_COUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TAXABLE_LINE_ITEM_COUNT + "+"
                          + safeSQLCast(totals.getUnitsGrossTaxableItemSales()
                                  .subtract(totals.getUnitsGrossTaxableItemSalesVoided()).toString()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_RETURN_TAXABLE_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_RETURN_TAXABLE_TOTAL_AMOUNT + "+"
                          + safeSQLCast(totals.getAmountGrossTaxableItemReturns()
                                  .subtract(totals.getAmountGrossTaxableItemReturnsVoided()).getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_SALES_TAXABLE_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_SALES_TAXABLE_TOTAL_AMOUNT + "+"
                          + safeSQLCast(totals.getAmountGrossTaxableItemSales()
                                  .subtract(totals.getAmountGrossTaxableItemSalesVoided()).getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_SALES_NONTAXABLE_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_SALES_NONTAXABLE_TOTAL_AMOUNT + "+"
                          + safeSQLCast(totals.getAmountGrossNonTaxableItemReturns()
                                  .subtract(totals.getAmountGrossNonTaxableItemReturnsVoided()).getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TAXABLE_RETURN_COUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TAXABLE_RETURN_COUNT + "+"
                          + safeSQLCast(totals.getUnitsGrossTaxableItemReturns()
                                  .subtract(totals.getUnitsGrossTaxableItemReturnsVoided()).toString()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_LAYAWAY_PAYMENTS_COLLECTED_COUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_TOTAL_LAYAWAY_PAYMENTS_COLLECTED_COUNT + "+" +
                          totals.getCountLayawayPayments());
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LAYAWAY_PAYMENTS_COLLECTED_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_LAYAWAY_PAYMENTS_COLLECTED_TOTAL_AMOUNT + "+" +
                          safeSQLCast(totals.getAmountLayawayPayments().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_LAYAWAY_DELETIONS_DISBURSEMENT_COUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_TOTAL_LAYAWAY_DELETIONS_DISBURSEMENT_COUNT + "+" +
                          totals.getCountLayawayDeletions());
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LAYAWAY_DELETIONS_DISBURSEMENT_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_LAYAWAY_DELETIONS_DISBURSEMENT_TOTAL_AMOUNT + "+" +
                          safeSQLCast(totals.getAmountLayawayDeletions().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LAYAWAY_INITIATION_FEES_COLLECTED_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_LAYAWAY_INITIATION_FEES_COLLECTED_TOTAL_AMOUNT + "+" +
                          safeSQLCast(totals.getAmountLayawayInitiationFees().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_LAYAWAY_INITIATION_FEES_COLLECTED_COUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_TOTAL_LAYAWAY_INITIATION_FEES_COLLECTED_COUNT + "+" +
                          totals.getCountLayawayInitiationFees());
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LAYAWAY_DELETION_FEES_COLLECTED_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_LAYAWAY_DELETION_FEES_COLLECTED_TOTAL_AMOUNT + "+" +
                          safeSQLCast(totals.getAmountLayawayDeletionFees().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_LAYAWAY_DELETION_FEES_COUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_TOTAL_LAYAWAY_DELETION_FEES_COUNT + "+" +
                          totals.getCountLayawayDeletionFees());
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_ORDER_PAYMENTS_COLLECTED_COUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_TOTAL_ORDER_PAYMENTS_COLLECTED_COUNT + " + " +
                            safeSQLCast(Integer.toString(totals.getCountOrderPayments())));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_ORDER_PAYMENTS_COLLECTED_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_ORDER_PAYMENTS_COLLECTED_TOTAL_AMOUNT + " + " +
                            safeSQLCast(totals.getAmountOrderPayments().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_ORDER_CANCELS_DISBURSEMENT_COUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_TOTAL_ORDER_CANCELS_DISBURSEMENT_COUNT + " + " +
                            safeSQLCast(Integer.toString(totals.getCountOrderCancels())));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_ORDER_CANCELS_DISBURSEMENT_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_ORDER_CANCELS_DISBURSEMENT_TOTAL_AMOUNT + " + " +
                            safeSQLCast(totals.getAmountOrderCancels().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_SHIPPING_CHARGE_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_SHIPPING_CHARGE_TOTAL_AMOUNT + " + " +
                            safeSQLCast(totals.getAmountShippingCharges().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_SHIPPING_CHARGE_COUNT,
                          FIELD_WORKSTATION_TIME_PERIOD_TOTAL_SHIPPING_CHARGE_COUNT + " + " +
                            safeSQLCast(Integer.toString(totals.getNumberShippingCharges())));
        }
        // set timestamps
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
    }

    /**
        Adds the financial totals fields and values to the sql statement.
        <p>
        @param  sql     The SQL statement to add the column-value pairs to
        @param  totals  The financial totals to draw the values from
     */
    protected void getFinancialTotals(SQLUpdatableStatementIfc sql, FinancialTotalsIfc totals)
    {
        // Transaction Sales/Returns
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_REFUND_COUNT,
                      safeSQLCast(getRefundCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_NONTAXABLE_REFUND_COUNT,
                      safeSQLCast(getNontaxableRefundCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TRANSACTION_COUNT,
                      safeSQLCast(getTransactionCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_SALES_COUNT,
                      safeSQLCast(getSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_NET_SALES_TOTAL_AMOUNT,
                      safeSQLCast(getNetSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_NONTAXABLE_SALES_COUNT,
                      safeSQLCast(getNontaxableSalesCount(totals)));
        // Item Sales/Returns
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_SALE_LINE_ITEM_COUNT,
                      safeSQLCast(getItemSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT,
                      safeSQLCast(getItemNontaxableAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_NONTAXABLE_LINE_ITEM_COUNT,
                      safeSQLCast(getItemNontaxableCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_RETURN_TOTAL_AMOUNT,
                      safeSQLCast(getItemReturnAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_RETURN_COUNT,
                      safeSQLCast(getItemReturnCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_RETURN_NONTAXABLE_TOTAL_AMOUNT,
                      safeSQLCast(getReturnNontaxableAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_NONTAXABLE_RETURN_COUNT,
                      safeSQLCast(getReturnNontaxableCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_HOUSE_PAYMENT_AMOUNT,
                      safeSQLCast(getHousePaymentAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_HOUSE_PAYMENT_COUNT,
                      safeSQLCast(getHousePaymentCount(totals)));
        if (totals != null)
        {
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TAXABLE_SALES_COUNT,
                          String.valueOf(totals.getCountGrossTaxableTransactionSales()
                                         - totals.getCountGrossTaxableTransactionSalesVoided()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TAXABLE_REFUND_COUNT,
                          String.valueOf(totals.getCountGrossTaxableTransactionReturns()
                                         - totals.getCountGrossTaxableTransactionReturnsVoided()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LINE_ITEM_TAXABLE_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountGrossTaxableItemSales()
                                .subtract(totals.getAmountGrossTaxableItemSalesVoided()).getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TAXABLE_LINE_ITEM_COUNT,
                          safeSQLCast(totals.getUnitsGrossTaxableItemSales()
                                .subtract(totals.getUnitsGrossTaxableItemSalesVoided()).toString()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_SALES_NONTAXABLE_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountGrossNonTaxableItemSales()
                                .subtract(totals.getAmountGrossNonTaxableItemSalesVoided()).getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_SALES_TAXABLE_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountGrossTaxableItemSales()
                                .subtract(totals.getAmountGrossTaxableItemSalesVoided()).getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_RETURN_TAXABLE_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountGrossTaxableItemReturns()
                                .subtract(totals.getAmountGrossTaxableItemReturnsVoided()).getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TAXABLE_RETURN_COUNT,
                          safeSQLCast(totals.getUnitsGrossTaxableItemReturns()
                                .subtract(totals.getUnitsGrossTaxableItemReturnsVoided()).toString()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_LAYAWAY_PAYMENTS_COLLECTED_COUNT,
                          safeSQLCast(Integer.toString(totals.getCountLayawayPayments())));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LAYAWAY_PAYMENTS_COLLECTED_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountLayawayPayments().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_LAYAWAY_DELETIONS_DISBURSEMENT_COUNT,
                          safeSQLCast(Integer.toString(totals.getCountLayawayDeletions())));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LAYAWAY_DELETIONS_DISBURSEMENT_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountLayawayDeletions().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LAYAWAY_INITIATION_FEES_COLLECTED_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountLayawayInitiationFees().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_LAYAWAY_INITIATION_FEES_COLLECTED_COUNT,
                          safeSQLCast(Integer.toString(totals.getCountLayawayInitiationFees())));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LAYAWAY_DELETION_FEES_COLLECTED_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountLayawayDeletionFees().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_LAYAWAY_DELETION_FEES_COUNT,
                          safeSQLCast(Integer.toString(totals.getCountLayawayDeletionFees())));
            sql.addColumn(FIELD_WORKSTATION_TOTAL_ORDER_PAYMENTS_COLLECTED_COUNT,
                          safeSQLCast(Integer.toString(totals.getCountOrderPayments())));
            sql.addColumn(FIELD_WORKSTATION_ORDER_PAYMENTS_COLLECTED_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountOrderPayments().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TOTAL_ORDER_CANCELS_DISBURSEMENT_COUNT,
                          safeSQLCast(Integer.toString(totals.getCountOrderCancels())));
            sql.addColumn(FIELD_WORKSTATION_ORDER_CANCELS_DISBURSEMENT_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountOrderCancels().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_SHIPPING_CHARGE_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountShippingCharges().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_SHIPPING_CHARGE_COUNT,
                          safeSQLCast(Integer.toString(totals.getNumberShippingCharges())));
        }
        // set timestamp
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
    }

    /**
        Returns a database safe string for the store id
        <p>
        @return store id
     */
    protected String getStoreID(TenderableTransactionIfc transaction)
    {
        return("'" + transaction.getWorkstation().getStoreID() + "'");
    }

    /**
        Returns a database safe string for the workstation id
        <p>
        @return workstation id
     */
    protected String getWorkstationID(TenderableTransactionIfc transaction)
    {
        return("'" + transaction.getWorkstation().getWorkstationID() + "'");
    }

    /**
        Returns the workstation date
        <p>
        @return the workstation date
     */
    protected String getWorkstationDate(TenderableTransactionIfc transaction)
    {
        return(dateToSQLDateString(transaction.getBusinessDay()));
    }

    /**
        Returns the hour of the day
        <p>
        @return the hour of the day
     */
    protected String getTimePeriodHour(TenderableTransactionIfc transaction)
    {
        EYSDate eysDate = transaction.getTimestampBegin();
        return(String.valueOf(eysDate.getHour()));
    }

    /**
        Returns the minute interval
        <p>
        @return the minute interval
     */
    protected String getTimePeriodMinute(TenderableTransactionIfc transaction)
    {
        // always 0
        return("0");
    }

    /**
        Returns the intervals per hour
        <p>
        @return the intervals per hour
     */
    protected String getTimePeriodInterval(TenderableTransactionIfc transaction)
    {
        // always 1
        return("1");
    }

    /**
        Returns the number of transactions
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of transactions
     */
    protected String getTransactionCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getTransactionCount());
        }
        return(value);
    }

    /**
        Returns the number of sales transactions
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of transactions
     */
    protected String getSalesCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossTaxableTransactionSales()
                                   - totals.getCountGrossTaxableTransactionSalesVoided()
                                   + (totals.getCountGrossNonTaxableTransactionSales()
                                      - totals.getCountGrossNonTaxableTransactionSalesVoided()));
        }
        return(value);
    }

    /**
        Returns the monetary amount of line item voids
        <p>
        @param  totals  The financial totals to extract the information from
        @return the monetary amount of line item voids
     */
    protected String getLineVoidAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountLineVoids().getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of line item voids
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of line item voids
     */
    protected String getLineVoidCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsLineVoids().toString();
        }
        return(value);
    }

    /**
        Returns the monetary amount of post transaction voids
        <p>
        @param  totals  The financial totals to extract the information from
        @return the monetary amount of post transaction voids
     */
    protected String getPostVoidAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountPostVoids().getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of post transaction voids
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of post transaction voids
     */
    protected String getPostVoidCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getNumberPostVoids());
        }
        return(value);
    }

    /**
        Returns the monetary amount of transaction voids
        <p>
        @param  totals  The financial totals to extract the information from
        @return the monetary amount of transaction voids
     */
    protected String getVoidAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountCancelledTransactions().getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of transaction voids
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of transaction voids
     */
    protected String getVoidCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getNumberCancelledTransactions());
        }
        return(value);
    }

    /**
        Returns the monetary amount of transaction discounts
        <p>
        @param  totals  The financial totals to extract the information from
        @return the monetary amount of transaction discounts
     */
    protected String getTransactionDiscountAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountTransactionDiscounts().getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of transaction discounts
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of transaction discounts
     */
    protected String getTransactionDiscountCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getNumberTransactionDiscounts());
        }
        return(value);
    }

    /**
        Returns the monetary amount of price markdowns
        <p>
        @param  totals  The financial totals to extract the information from
        @return the monetary amount of price markdowns
     */
    protected String getMarkdownAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountItemDiscounts().getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of price markdowns
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of price markdowns
     */
    protected String getMarkdownCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getNumberItemDiscounts());
        }
        return(value);
    }

    /**
        Returns the refund amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the refund amount
     */
    protected String getRefundAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableTransactionReturns()
                          .subtract(totals.getAmountGrossTaxableTransactionReturnsVoided())
                          .add(totals.getAmountGrossNonTaxableTransactionReturns()
                               .subtract(totals.getAmountGrossNonTaxableTransactionReturnsVoided())).getStringValue();
        }
        return value;
    }

    /**
        Returns the number of refunds
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of refunds
     */
    protected String getRefundCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossTaxableTransactionReturns()
                                   - totals.getCountGrossTaxableTransactionReturnsVoided()
                                   + (totals.getCountGrossNonTaxableTransactionReturns()
                                      - totals.getCountGrossNonTaxableTransactionReturnsVoided()));
        }
        return(value);
    }

    /**
        Returns the number of non-taxable refunds
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of non-taxable refunds
     */
    protected String getNontaxableRefundCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossNonTaxableTransactionReturns()
                                   - totals.getCountGrossNonTaxableTransactionReturnsVoided());
        }
        return(value);
    }

    /**
        Returns the net sales amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the net sales amount
     */
    protected String getNetSalesAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountNetTransactionSales().getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of non-taxable sales
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of non-taxable sales
     */
    protected String getNontaxableSalesCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossNonTaxableTransactionSales()
                                   - totals.getCountGrossNonTaxableTransactionSalesVoided());
        }
        return value;
    }

    /**
        Returns the number of line items sold or returned
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of line items sold or returned
     */
    protected String getItemSalesCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            BigDecimal total = totals.getUnitsGrossTaxableItemSales()
                                     .subtract(totals.getUnitsGrossTaxableItemSalesVoided());
            total = total.add(totals.getUnitsGrossNonTaxableItemSales()
                                    .subtract(totals.getUnitsGrossNonTaxableItemSalesVoided()));
            value = total.toString();
        }
        return value;
    }

    /**
        Returns the nontaxable item sales amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the nontaxable item sales amount
     */
    protected String getItemNontaxableAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossNonTaxableItemSales()
                          .subtract(totals.getAmountGrossNonTaxableItemSalesVoided()).getStringValue();
        }
        return value;
    }

    /**
        Returns the number of nontaxable line items sold
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of nontaxale line items sold
     */
    protected String getItemNontaxableCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossNonTaxableItemSales()
                          .subtract(totals.getUnitsGrossNonTaxableItemSalesVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the return amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the return amount
     */
    protected String getItemReturnAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableItemReturns()
                          .subtract(totals.getAmountGrossTaxableItemReturnsVoided())
                        .add(totals.getAmountGrossNonTaxableItemReturns()
                                   .subtract(totals.getAmountGrossNonTaxableItemReturnsVoided())).getStringValue();

        }
        return value;
    }

    /**
        Returns the number of returns
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of returns
     */
    protected String getItemReturnCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxableItemReturns().subtract(totals.getUnitsGrossTaxableItemReturnsVoided())
                        .subtract(totals.getUnitsGrossNonTaxableItemReturns()
                                  .subtract(totals.getUnitsGrossNonTaxableItemReturnsVoided())).toString();
        }
        return value;
    }

    /**
        Returns the nontaxable item return amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the nontaxable item return amount
     */
    protected String getReturnNontaxableAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossNonTaxableItemReturns()
                          .subtract(totals.getAmountGrossNonTaxableItemReturnsVoided()).getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of nontaxable line items returned
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of nontaxale line items returned
     */
    protected String getReturnNontaxableCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossNonTaxableItemReturns()
                          .subtract(totals.getUnitsGrossNonTaxableItemReturnsVoided()).toString();
        }
        return value;
    }

    /**
        Returns the amount of house account payments
        <p>
        @param  totals  The financial totals to extract the information from
        @return the amount of house account payments
     */
    protected String getHousePaymentAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountHousePayments().getStringValue();
        }

        return(value);
    }

    /**
        Returns the number of house account payments
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of house account payments
     */
    protected String getHousePaymentCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountHousePayments());
        }

        return(value);
    }


    /**
        Returns the number of restocking fees
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of restocking fees
     */
    protected String getRestockingFeeCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = (totals.getUnitsRestockingFees().
                    add(totals.getUnitsRestockingFeesFromNonTaxableItems())).toString();
        }

        return(value);
    }

    /**
        Returns the amount of restocking fees
        <p>
        @param  totals  The financial totals to extract the information from
        @return the amount of restocking fees
     */
    protected String getRestockingFeeAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = (totals.getAmountRestockingFees().
                    add(totals.getAmountRestockingFeesFromNonTaxableItems())).getStringValue();
        }

        return(value);
    }

}
