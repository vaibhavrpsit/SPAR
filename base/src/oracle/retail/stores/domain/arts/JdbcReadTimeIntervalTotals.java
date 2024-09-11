/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadTimeIntervalTotals.java /main/12 2013/09/05 10:36:19 abondala Exp $
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
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         1/23/2008 1:51:06 PM   Jack G. Swan    Fixed
 *          issue with Calendar class use of HOUR vs HOUR_OF_DAY constants;
 *         this problem was cause by a difference in Java 1.5 vs 1.4.  This
 *         code change was reviewed by Tony Zgarba
 *    6    360Commerce 1.5         4/25/2007 10:01:12 AM  Anda D. Cadar   I18N
 *         merge
 *    5    360Commerce 1.4         1/25/2006 4:11:19 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         1/22/2006 11:41:21 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:07    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:42     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:46     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:00     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:25  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:32:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:38:28   msg
 * Initial revision.
 * 
 *    Rev 1.2   25 Mar 2002 16:54:56   pdd
 * Added ordering on the interval data.
 * Resolution for POS SCR-1163: Hourly Productivity Report prints multiple entries of the same hour for multiple business days
 * 
 *    Rev 1.1   Mar 18 2002 22:47:50   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:07:46   msg
 * Initial revision.
 * 
 *    Rev 1.4   06 Feb 2002 16:59:20   pdd
 * Added gross figures for clarity.
 * Resolution for POS SCR-140: Hourly Productivity Report: Invalid hourly sales value
 * 
 *    Rev 1.3   Feb 05 2002 16:33:44   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 * 
 *    Rev 1.2   18 Dec 2001 13:53:00   sfl
 * Included shipping charge information in the
 * workstation time interval activity history data
 * reading.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.1   02 Dec 2001 12:48:00   mpm
 * Implemented financials, voids for special order domain objects.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 20 2001 16:00:14   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.TimeIntervalActivityIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * Abstract class that contains the database calls for reading workstation time
 * interval totals.
 * 
 * @version $Revision: /main/12 $
 */
public class JdbcReadTimeIntervalTotals extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 5759901902737836998L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadTimeIntervalTotals.class);

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadTimeIntervalTotals.execute()");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ARTSReportingPeriod period = (ARTSReportingPeriod)action.getDataObject();
        TimeIntervalActivityIfc[] totals;
        totals = readTimeIntervalTotals(connection,
                                        period.getStoreID(),
                                        period.getStartDate(),
                                        period.getEndDate());

        /*
         * Send back the result
         */
        dataTransaction.setResult(totals);

        if (logger.isDebugEnabled()) logger.debug( "ReadTimeIntervalTotals.execute()");
    }

    /**
       Returns a list of workstation time activity totals for the
       time interval.
       <p>
       @param  dataConnection  connection to the db
       @param  storeID         The store ID
       @param  startDate       The beginning of the time interval
       @param  endDate         The end of the time interval
       @return workstation time activity information
       @exception DataException upon error
     */
    public TimeIntervalActivityIfc[] readTimeIntervalTotals(JdbcDataConnection dataConnection,
                                                            String storeID,
                                                            EYSDate startDate,
                                                            EYSDate endDate)
        throws DataException
    {
        Vector vector = selectWorkstationTimeActivityHistory(dataConnection,
                                                             storeID,
                                                             startDate,
                                                             endDate);

        TimeIntervalActivityIfc[] activities = new TimeIntervalActivityIfc[vector.size()];
        vector.copyInto(activities);

        return(activities);
    }

    /**
       Returns a list of workstation time activity records.
       <p>
       beginDate and endDate are used for the days only, not the time.
       <p>
       @param  dataConnection  connection to the db
       @param  storeID         The store ID
       @param  beginDate       The beginning of the time interval
       @param  endDate         The end of the time interval
       @return List of department totals
       @exception DataException upon error
     */
    public Vector selectWorkstationTimeActivityHistory(JdbcDataConnection dataConnection,
                                                       String storeID,
                                                       EYSDate beginDate,
                                                       EYSDate endDate)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_WORKSTATION_TIME_ACTIVITY_HISTORY);
        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_TIME_PERIOD_MINUTE_INTERVAL_COUNT);
        sql.addColumn(FIELD_TIME_PERIOD_HOUR);
        sql.addColumn(FIELD_TIME_PERIOD_INTERVAL_PER_HOUR_COUNT);
        sql.addColumn(FIELD_WORKSTATION_ID);
        sql.addColumn(FIELD_WORKSTATION_DATE);
        // Financial Totals fields
        //sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_SIGN_ON_TIME_MINUTES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TRANSACTION_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_NET_SALES_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_SALE_LINE_ITEM_COUNT);
        // The following fields are not defined in ARTS
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_SALES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_NONTAXABLE_SALES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TAXABLE_SALES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_REFUND_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_NONTAXABLE_REFUND_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TAXABLE_REFUND_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LINE_ITEM_TAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_NONTAXABLE_LINE_ITEM_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TAXABLE_LINE_ITEM_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_RETURN_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_RETURN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_RETURN_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_RETURN_TAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_SALES_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_SALES_TAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_NONTAXABLE_RETURN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_TAXABLE_RETURN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_HOUSE_PAYMENT_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_HOUSE_PAYMENT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_RESTOCKING_FEE_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_RESTOCKING_FEE_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_LAYAWAY_PAYMENTS_COLLECTED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LAYAWAY_PAYMENTS_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_LAYAWAY_DELETIONS_DISBURSEMENT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LAYAWAY_DELETIONS_DISBURSEMENT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_LAYAWAY_INITIATION_FEES_COLLECTED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LAYAWAY_INITIATION_FEES_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_LAYAWAY_DELETION_FEES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_LAYAWAY_DELETION_FEES_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_ORDER_PAYMENTS_COLLECTED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_ORDER_PAYMENTS_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_ORDER_CANCELS_DISBURSEMENT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_ORDER_CANCELS_DISBURSEMENT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_SHIPPING_CHARGE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TIME_PERIOD_TOTAL_SHIPPING_CHARGE_COUNT);
        /*
         * Add Qualifier(s)
         */
        // For the specified store only
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(storeID));
        // During the specified time interval
        sql.addQualifier(FIELD_WORKSTATION_DATE + " >= " + dateToSQLDateString(beginDate.dateValue()));
        sql.addQualifier(FIELD_WORKSTATION_DATE + " <= " + dateToSQLDateString(endDate.dateValue()));
        // Order by business day and hour
        sql.addOrdering(FIELD_WORKSTATION_DATE);
        sql.addOrdering(FIELD_TIME_PERIOD_HOUR);
        Vector activityVector = new Vector(2);

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            while (rs.next())
            {
                /*
                 * Grab the fields selected from the database
                 */
                int index = 0;
                int minute = rs.getInt(++index);
                int hour = rs.getInt(++index);
                int interval = rs.getInt(++index);
                String workstationID = getSafeString(rs, ++index);
                EYSDate date = getEYSDateFromString(rs, ++index);
                int transactionCount = rs.getInt(++index);
                CurrencyIfc netSalesAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal itemSalesCount = getBigDecimal(rs, ++index);
                int salesCount = rs.getInt(++index);
                int nontaxableSalesCount = rs.getInt(++index);
                int taxableSalesCount = rs.getInt(++index);
                int refundCount = rs.getInt(++index);
                int nontaxableRefundCount = rs.getInt(++index);
                int taxableRefundCount = rs.getInt(++index);
                CurrencyIfc itemNontaxableAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc itemTaxableAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal itemNontaxableCount = getBigDecimal(rs, ++index);
                BigDecimal itemTaxableCount = getBigDecimal(rs, ++index);
                CurrencyIfc returnAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal returnCount = getBigDecimal(rs, ++index);
                CurrencyIfc returnNontaxableAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc returnTaxableAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc salesNontaxableAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc salesTaxableAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal returnNontaxableCount = getBigDecimal(rs, ++index);
                BigDecimal returnTaxableCount = getBigDecimal(rs, ++index);
                CurrencyIfc housePaymentsAmount = getCurrencyFromDecimal(rs, ++index);
                int housePaymentsCount = rs.getInt(++index);
                CurrencyIfc restockingFeeAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal restockingFeeCount = getBigDecimal(rs, ++index);
                int layawayPaymentsCount = rs.getInt(++index);
                CurrencyIfc layawayPaymentsAmount = getCurrencyFromDecimal(rs, ++index);
                int layawayDeletionsCount = rs.getInt(++index);
                CurrencyIfc layawayDeletionsAmount = getCurrencyFromDecimal(rs, ++index);
                int layawayInitiationFeesCount = rs.getInt(++index);
                CurrencyIfc layawayInitiationFeesAmount = getCurrencyFromDecimal(rs, ++index);
                int layawayDeletionFeesCount = rs.getInt(++index);
                CurrencyIfc layawayDeletionFeesAmount = getCurrencyFromDecimal(rs, ++index);
                int orderPaymentsCount = rs.getInt(++index);
                CurrencyIfc orderPaymentsAmount = getCurrencyFromDecimal(rs, ++index);
                int orderCancelsCount = rs.getInt(++index);
                CurrencyIfc orderCancelsAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc shippingChargesAmount = getCurrencyFromDecimal(rs, ++index);
                int shippingChargesCount = rs.getInt(++index);
                /*
                 * Setup the bounds of the time interval
                 */
                date.setHour(hour);
                date.setMinute(minute);
                EYSDate startDate = DomainGateway.getFactory().getEYSDateInstance();
                startDate.initialize(date.dateValue());
                Calendar value = date.calendarValue();
                // use time interval of one hour
                value.add(Calendar.HOUR_OF_DAY, interval);
                EYSDate stopDate = DomainGateway.getFactory().getEYSDateInstance();
                stopDate.initialize(value.getTime());
                /*
                 * TimeIntervalActivity
                 */
                TimeIntervalActivityIfc activity = instantiateTimeIntervalActivity();
                activity.setStartTime(startDate);
                activity.setEndTime(stopDate);
                /*
                 * FinancialTotals
                 */
                FinancialTotalsIfc totals = instantiateFinancialTotals();
                // Transaction Sales
                totals.setTransactionCount(transactionCount);
                totals.setCountGrossTaxableTransactionSales(taxableSalesCount);
                totals.setCountGrossNonTaxableTransactionSales(nontaxableSalesCount);
                totals.setCountGrossTaxableTransactionReturns(taxableRefundCount);
                totals.setCountGrossNonTaxableTransactionReturns(nontaxableRefundCount);
                totals.setAmountGrossTaxableTransactionSales(itemTaxableAmount);
                totals.setAmountGrossNonTaxableTransactionSales(itemNontaxableAmount);
                totals.setAmountGrossTaxableTransactionReturns(returnTaxableAmount);
                totals.setAmountGrossNonTaxableTransactionReturns(returnNontaxableAmount);
                // Item Sales
                CurrencyIfc itemSalesAmount = netSalesAmount.add(returnAmount);
                totals.setAmountGrossTaxableItemSales(itemTaxableAmount);
                totals.setUnitsGrossTaxableItemSales(itemTaxableCount);
                totals.setAmountGrossNonTaxableItemSales(itemNontaxableAmount);
                totals.setUnitsGrossNonTaxableItemSales(itemNontaxableCount);
                // Item Returns
                totals.setAmountGrossTaxableItemReturns(returnTaxableAmount);
                totals.setUnitsGrossTaxableItemReturns(returnTaxableCount);
                totals.setAmountGrossNonTaxableItemReturns(returnNontaxableAmount);
                totals.setUnitsGrossNonTaxableItemReturns(returnNontaxableCount);
                totals.setAmountHousePayments(housePaymentsAmount);
                totals.setCountHousePayments(housePaymentsCount);
                totals.setAmountRestockingFees(restockingFeeAmount);
                totals.setUnitsRestockingFees(restockingFeeCount);
                totals.setAmountLayawayPayments(layawayPaymentsAmount);
                totals.setCountLayawayPayments(layawayPaymentsCount);
                totals.setAmountLayawayDeletions(layawayDeletionsAmount);
                totals.setCountLayawayDeletions(layawayDeletionsCount);
                totals.setAmountLayawayInitiationFees(layawayInitiationFeesAmount);
                totals.setCountLayawayInitiationFees(layawayInitiationFeesCount);
                totals.setAmountLayawayDeletionFees(layawayDeletionFeesAmount);
                totals.setCountLayawayDeletionFees(layawayDeletionFeesCount);
                totals.setAmountOrderPayments(orderPaymentsAmount);
                totals.setCountOrderPayments(orderPaymentsCount);
                totals.setAmountOrderCancels(orderCancelsAmount);
                totals.setCountOrderCancels(orderCancelsCount);
                totals.setAmountShippingCharges(shippingChargesAmount);
                totals.setNumberShippingCharges(shippingChargesCount);
                // Tax
                // Misc
                activity.setTotals(totals);
                activityVector.addElement(activity);
            }

            if (activityVector.isEmpty())
            {
                throw new DataException(NO_DATA, "No Activity records");
            }

            rs.close();
        }
        catch (SQLException se)
        {
            throw new DataException(SQL_ERROR, "selectWorkstationTimeActivityHistory", se);
        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(UNKNOWN, "selectWorkstationTimeActivityHistory", e);
        }

        return(activityVector);
    }

    /**
       Returns a database safe string for the store id
       <p>
       @return store id
     */
    protected String getStoreID(String storeID)
    {
        return("'" + storeID + "'");
    }

    /**
       Instantiates a Time Interval Activity object.
       <p>
       @return new Time Interval Activity object
     */
    protected TimeIntervalActivityIfc instantiateTimeIntervalActivity()
    {
        return(DomainGateway.getFactory().getTimeIntervalActivityInstance());
    }

    /**
       Instantiates a Financial Totals object.
       <p>
       @return new Financial Totals object
     */
    protected FinancialTotalsIfc instantiateFinancialTotals()
    {
        return(DomainGateway.getFactory().getFinancialTotalsInstance());
    }
}
