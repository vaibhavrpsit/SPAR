/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadStoreRegisters.java /main/15 2013/09/05 10:36:19 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
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
 *    11   360Commerce 1.10        6/26/2007 11:13:58 AM  Ashok.Mondal    I18N
 *         changes to export and import POSLog.
 *    10   360Commerce 1.9         5/14/2007 6:08:34 PM   Sandy Gu
 *         update inclusive information in financial totals and history tables
 *    9    360Commerce 1.8         4/25/2007 10:01:13 AM  Anda D. Cadar   I18N
 *         merge
 *    8    360Commerce 1.7         7/25/2006 7:06:23 PM   Robert Zurga    .v7x
 *              1.4.1.1     6/22/2006 3:53:39 PM   Michael Wisbauer added
 *         setting flag to retrieve all employees'
 *         
 *    7    360Commerce 1.6         7/25/2006 6:48:01 PM   Robert Zurga    Merge
 *          from JdbcReadStoreRegisters.java, Revision 1.4.1.1 
 *    6    360Commerce 1.5         7/21/2006 2:27:37 PM   Brendan W. Farrell
 *         Merge fixes from v7.x.  These changes let services extend tax.
 *    5    360Commerce 1.4         1/25/2006 4:11:18 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         1/22/2006 11:41:19 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:10    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:45     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:00     Robert Pearse
 *
 *   Revision 1.17  2004/06/15 00:44:31  jdeleau
 *   @scr 2775 Support register reports and financial totals with the new
 *   tax engine.
 *
 *   Revision 1.16  2004/05/12 15:03:57  jdeleau
 *   @scr 4218 Remove GrossTransactionDiscount Amounts, Units, UnitsVoid, 
 *   and AmountVoids in favor of the already existing AmountTransactionDiscounts
 *   and NumberTransactionDiscounts, which end up already being NET totals.
 *
 *   Revision 1.15  2004/05/11 23:03:01  jdeleau
 *   @scr 4218 Backout recent changes to remove TransactionDiscounts,
 *   going to go a different route and remove the newly added
 *   voids and grosses instead.
 *
 *   Revision 1.13  2004/04/28 19:41:40  jdeleau
 *   @scr 4218 Add StoreCreditsIssued (count/amount and voided counts and amounts)
 *   to Financial Totals.
 *
 *   Revision 1.12  2004/04/27 20:01:17  jdeleau
 *   @scr 4218 Add in the concrete calls for register reports data, refactor
 *   the houseCardEnrollment methods to be in line with other FinancialTotals
 *   methods.
 *
 *   Revision 1.11  2004/04/26 21:07:22  jdeleau
 *   @scr 4218 Put calls to new Financial Totals data in register reports, 
 *   correct error in ArtsDatabaseIfc
 *
 *   Revision 1.10  2004/04/26 18:23:40  jdeleau
 *   @scr 4128 JDBC changes to support new data required for register reports
 *
 *   Revision 1.9  2004/04/07 20:56:49  lzhao
 *   @scr 4218: add gift card info for summary report.
 *
 *   Revision 1.8  2004/04/05 23:03:01  jdeleau
 *   @scr 4218 JavaDoc fixes associated with RegisterReports changes
 *
 *   Revision 1.7  2004/04/02 23:07:33  jdeleau
 *   @scr 4218 Register Reports - House Account and initial changes to
 *   the way SummaryReports are built.
 *
 *   Revision 1.6  2004/02/25 23:08:00  crain
 *   @scr 3814 Issue Gift Certificate
 *
 *   Revision 1.5  2004/02/17 17:57:35  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:44  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:21  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:32:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   11 Jun 2002 16:25:02   jbp
 * changes to report markdowns
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   Jun 03 2002 16:38:14   msg
 * Initial revision.
 *
 *    Rev 1.2   23 Apr 2002 16:13:08   sfl
 * Implemented using the newly added columns in the
 * WorkstationHistory table to retrieve the financial totals
 * gross values.
 * Resolution for POS SCR-1579: Store gross figures in the DB (financials)
 *
 *    Rev 1.1   Mar 18 2002 22:46:00   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:06:04   msg
 * Initial revision.
 *
 *    Rev 1.3   Feb 05 2002 16:33:40   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.2   17 Dec 2001 17:11:38   sfl
 * Included the ShippingChargeTotalAmount and
 * TotalShippingChargeCount columns in the
 * database table read on the WorkstationHistory table.
 * This is to support displaying the shipping charge
 * information in the summary report on register.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.1   02 Dec 2001 12:47:58   mpm
 * Implemented financials, voids for special order domain objects.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 20 2001 16:00:00   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
/**
    This operation reads all of the registers for a store for a particular
    business day.
    <P>
    @version $Revision: /main/15 $
**/
public class JdbcReadStoreRegisters extends JdbcDataOperation
                                    implements ARTSDatabaseIfc
{
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadStoreRegisters.class);

    /**
       Class constructor.
     */
    public JdbcReadStoreRegisters()
    {
        super();
        setName("JdbcReadStoreRegisters");
    }

    /**
       Executes the SQL statements against the database.
       <P>
       @param  dataTransaction     The data transaction
       @param  dataConnection      The connection to the data source
       @param  action              The information passed by the valet
       @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadStoreRegisters.execute()");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ARTSStore store = (ARTSStore)action.getDataObject();
        Vector registerVector = readStoreRegisters(connection, store);

        /*
         * Send back the result
         */
        RegisterIfc[] registers = new RegisterIfc[registerVector.size()];
        registerVector.copyInto(registers);
        dataTransaction.setResult(registers);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadStoreRegisters.execute()");
    }

    /**
       Returns the registers for a store's business day.
       <P>
       @param  dataConnection  connection to the db
       @param  store           the store and business day
       @return vector of registers
       @exception DataException upon error
     */
    public Vector readStoreRegisters(JdbcDataConnection dataConnection,
                                     ARTSStore store)
        throws DataException
    {
        Vector registerVector = readWorkstationHistory(dataConnection, store);

        return(registerVector);
    }

    /**
       Returns the workstation history for a store's business day.
       <P>
       @param  dataConnection  connection to the db
       @param  store           the store and business day
       @return vector of workstation history
       @exception DataException upon error
     */
    public Vector readWorkstationHistory(JdbcDataConnection dataConnection,
                                         ARTSStore store)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_WORKSTATION_HISTORY, ALIAS_WORKSTATION_HISTORY);
        sql.addTable(TABLE_REPORTING_PERIOD, ALIAS_REPORTING_PERIOD);
        sql.addTable(TABLE_BUSINESS_DAY, ALIAS_BUSINESS_DAY);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_WORKSTATION_ID);
        sql.addColumn(FIELD_WORKSTATION_START_DATE_TIMESTAMP);
        sql.addColumn(FIELD_WORKSTATION_HISTORY_STATUS_CODE);
        sql.addColumn(FIELD_CURRENCY_ID); //I18N

        // These are in the same order as Store History
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NO_SALE_TRANSACTION_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_SALE_LINE_ITEM_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_TRANSACTION_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_INCLUSIVE_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_SALES_EX_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_DISCOUNT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_DISCOUNT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_MARKDOWN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_MARKDOWN_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_MISCELLANEOUS_DISCOUNT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_MISCELLANEOUS_DISCOUNT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_REFUND_COUNT);
        sql.addColumn(FIELD_WORKSTATION_REFUND_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_RETURN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_RETURN_TOTAL_AMOUNT);
        //sql.addColumn(FIELD_WORKSTATION_TENDER_PICKUP_TOTAL_AMOUNT);
        //sql.addColumn(FIELD_WORKSTATION_TENDER_LOAN_TOTAL_AMOUNT);
        //sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_PICKUP_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TRANSACTION_VOID_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_POST_TRANSACTION_VOID_COUNT);
        sql.addColumn(FIELD_WORKSTATION_POST_TRANSACTION_VOID_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_LINE_ITEM_VOID_COUNT);
        sql.addColumn(FIELD_WORKSTATION_LINE_ITEM_VOID_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TRANSACTION_COUNT);
        //sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_LOAN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TRANSACTION_VOID_TOTAL_AMOUNT);
        // Additional fields not defined in ARTS
            // StoreCouponDiscounts
        sql.addColumn(FIELD_WORKSTATION_ITEM_DISCOUNT_STORE_COUPON_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_ITEM_DISCOUNT_STORE_COUPON_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TRANSACTION_DISCOUNT_STORE_COUPON_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TRANSACTION_DISCOUNT_STORE_COUPON_COUNT);

        sql.addColumn(FIELD_WORKSTATION_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONTAXABLE_COUNT);
        sql.addColumn(FIELD_WORKSTATION_REFUND_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONTAXABLE_REFUND_COUNT);
        sql.addColumn(FIELD_WORKSTATION_REFUND_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_REFUND_COUNT);
        sql.addColumn(FIELD_WORKSTATION_LINE_ITEM_SALES_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONTAXABLE_LINE_ITEM_COUNT);
        sql.addColumn(FIELD_WORKSTATION_LINE_ITEM_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_LINE_ITEM_COUNT);
        sql.addColumn(FIELD_WORKSTATION_RETURN_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONTAXABLE_RETURN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_RETURN_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_RETURN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_REFUND_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_REFUND_INCLUSIVE_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_RETURN_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_RETURN_INCLUSIVE_TAX_TOTAL_AMOUNT);

        sql.addColumn(FIELD_WORKSTATION_NONMERCH_NONTAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONMERCH_NONTAX_COUNT);
        sql.addColumn(FIELD_WORKSTATION_RETURN_NONMERCH_NONTAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONMERCH_NONTAX_RETURN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_NONMERCH_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONMERCH_TAX_COUNT);
        sql.addColumn(FIELD_WORKSTATION_RETURN_NONMERCH_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONMERCH_TAX_RETURN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_COUNT);
        sql.addColumn(FIELD_WORKSTATION_RETURN_GIFT_CARD_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_RETURN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_LAYAWAY_PAYMENTS_COLLECTED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_LAYAWAY_PAYMENTS_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_LAYAWAY_DELETIONS_DISBURSEMENT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_LAYAWAY_DELETIONS_DISBURSEMENT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_LAYAWAY_INITIATION_FEES_COLLECTED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_LAYAWAY_INITIATION_FEES_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_LAYAWAY_DELETION_FEES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_LAYAWAY_DELETION_FEES_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_ORDER_PAYMENTS_COLLECTED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_ORDER_PAYMENTS_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_ORDER_CANCELS_DISBURSEMENT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_ORDER_CANCELS_DISBURSEMENT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_SHIPPING_CHARGE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_SHIPPING_CHARGE_COUNT);
        sql.addColumn(FIELD_WORKSTATION_SHIPPING_CHARGE_TAX_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_SHIPPING_CHARGE_INCLUSIVE_TAX_AMOUNT);

        
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_IN_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_IN_UNIT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_UNIT_COUNT);

        // Gross value related columns
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_SALES_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_SALES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_RETURNS_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_RETURNS_COUNT);
        sql.addColumn(FIELD_WORKSTATION_ITEM_SALES_TAX_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_ITEM_SALES_INCLUSIVE_TAX_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TRANSACTION_SALES_TAX_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TRANSACTION_SALES_INCLUSIVE_TAX_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_SALES_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_NON_TAXABLE_TRANSACTION_SALES_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAX_EXEMPT_TRANSACTION_SALES_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_NON_TAXABLE_TRANSACTION_SALES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_SALES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAX_EXEMPT_TRANSACTION_SALES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_RETURNS_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_RETURNS_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_NON_MERCHANDISE_SALES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CERTIFICATE_ISSUED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CERTIFICATE_ISSUED_COUNT);
        
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_ISSUED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_ISSUED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_RELOADED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_RELOADED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_REDEEMED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_REDEEMED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_ISSUE_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_ISSUE_VOIDED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_RELOAD_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_RELOAD_VOIDED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_REDEEM_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_REDEEM_VOIDED_COUNT);

        sql.addColumn(FIELD_WORKSTATION_HOUSE_ACCOUNT_ENROLLMENT_APPROVED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_HOUSE_ACCOUNT_ENROLLMENT_DECLINED_COUNT);
        
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_VOIDED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_VOIDED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_EMPLOYEE_DISCOUNTS_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_EMPLOYEE_DISCOUNTS_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_CUSTOMER_DISCOUNTS_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_CUSTOMER_DISCOUNTS_COUNT);
        sql.addColumn(FIELD_WORKSTATION_PRICE_OVERRIDES_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_PRICE_OVERRIDES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TRANSACTIONS_WITH_RETURNED_ITEMS_COUNT);
        /*
         * Add Qualifier(s)
         */

        // For the specified workstation only
        sql.addQualifier(ALIAS_WORKSTATION_HISTORY + "." + FIELD_RETAIL_STORE_ID
                         + " = " + getStoreID(store));

        // Join Workstation History and Reporting Period
        sql.addQualifier(ALIAS_WORKSTATION_HISTORY + "." + FIELD_FISCAL_YEAR
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_YEAR);
        sql.addQualifier(ALIAS_WORKSTATION_HISTORY + "." + FIELD_REPORTING_PERIOD_TYPE_CODE
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_TYPE_CODE);
        sql.addQualifier(ALIAS_WORKSTATION_HISTORY + "." + FIELD_REPORTING_PERIOD_ID
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_ID);

        // Join Reporting Period and Business Day
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_YEAR
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_YEAR);
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_WEEK_NUMBER
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_WEEK_NUMBER);
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_DAY_NUMBER
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_DAY_NUMBER);

        // For the specified business day only
        sql.addQualifier(ALIAS_BUSINESS_DAY + "." + FIELD_BUSINESS_DAY_DATE
                         + " = " + getBusinessDate(store.getBusinessDate()));

        Vector workstationVector = new Vector(2);
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
                String workstationID = getSafeString(rs, ++index);
                Timestamp startTime = rs.getTimestamp(++index);
                int statusCode = rs.getInt(++index);
                int currencyID = rs.getInt(++index); //I18N

                int noSaleCount = rs.getInt(++index);
                BigDecimal itemSalesCount = getBigDecimal(rs, ++index);
                int taxExemptTransactionCount = rs.getInt(++index);
                CurrencyIfc netTaxExemptAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc netTaxAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc netInclusiveTaxAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc transactionSalesAmount = getCurrencyFromDecimal(rs, ++index);
                int discountCount = rs.getInt(++index);
                CurrencyIfc discountAmount = getCurrencyFromDecimal(rs, ++index);
                int markdownCount = rs.getInt(++index);
                CurrencyIfc markdownAmount = getCurrencyFromDecimal(rs, ++index);
                int miscDiscountCount = rs.getInt(++index);
                CurrencyIfc miscDiscountAmount = getCurrencyFromDecimal(rs, ++index);
                int refundCount = rs.getInt(++index);
                CurrencyIfc refundAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal returnCount = getBigDecimal(rs, ++index);
                CurrencyIfc returnAmount = getCurrencyFromDecimal(rs, ++index);
                //CurrencyIfc pickupAmount = getCurrencyFromDecimal(rs, ++index);
                //CurrencyIfc loanAmount = getCurrencyFromDecimal(rs, ++index);
                //int pickupCount = rs.getInt(++index);
                int voidTransactionCount = rs.getInt(++index);
                int postVoidCount = rs.getInt(++index);
                CurrencyIfc postVoidAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal itemVoidCount = getBigDecimal(rs, ++index);
                CurrencyIfc itemVoidAmount = getCurrencyFromDecimal(rs, ++index);
                int transactionCount = rs.getInt(++index);
                //int loanCount = rs.getInt(++index);
                CurrencyIfc voidTransactionAmount = getCurrencyFromDecimal(rs, ++index);
                // StoreCouponDiscounts
                CurrencyIfc itemDiscStoreCouponAmount = getCurrencyFromDecimal(rs, ++index);
                int  itemDiscStoreCouponCount = rs.getInt(++index);
                CurrencyIfc transactionDiscStoreCouponAmount = getCurrencyFromDecimal(rs, ++index);
                int  transactionDiscStoreCouponCount = rs.getInt(++index);

                CurrencyIfc netNontaxableAmount = getCurrencyFromDecimal(rs, ++index);
                int nontaxableTransactionCount = rs.getInt(++index);
                CurrencyIfc nontaxableRefundAmount = getCurrencyFromDecimal(rs, ++index);
                int nontaxableRefundCount = rs.getInt(++index);
                CurrencyIfc taxExemptRefundAmount = getCurrencyFromDecimal(rs, ++index);
                int taxExemptRefundCount = rs.getInt(++index);
                CurrencyIfc itemSalesAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc itemNontaxableAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal itemNontaxableCount = getBigDecimal(rs, ++index);
                CurrencyIfc itemTaxExemptAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal itemTaxExemptCount = getBigDecimal(rs, ++index);
                CurrencyIfc itemNontaxableReturnAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal itemNontaxableReturnCount = getBigDecimal(rs, ++index);
                CurrencyIfc itemTaxExemptReturnAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal itemTaxExemptReturnCount = getBigDecimal(rs, ++index);
                CurrencyIfc taxRefundAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc inclusiveTaxRefundAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc taxReturnAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc inclusiveTaxReturnAmount = getCurrencyFromDecimal(rs, ++index);

                CurrencyIfc nonMerchNonTaxAmount        = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  nonMerchNonTaxCount         = getBigDecimal(rs, ++index);
                CurrencyIfc nonMerchNonTaxReturnAmount  = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  nonMerchNonTaxReturnCount   = getBigDecimal(rs, ++index);
                CurrencyIfc nonMerchTaxAmount           = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  nonMerchTaxCount            = getBigDecimal(rs, ++index);
                CurrencyIfc nonMerchTaxReturnAmount     = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  nonMerchTaxReturnCount      = getBigDecimal(rs, ++index);
                CurrencyIfc giftCardAmount              = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  giftCardCount               = getBigDecimal(rs, ++index);
                CurrencyIfc giftCardReturnAmount        = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  giftCardReturnCount         = getBigDecimal(rs, ++index);
                int         layawayPaymentsCount        = rs.getInt(++index);
                CurrencyIfc layawayPaymentsAmount       = getCurrencyFromDecimal(rs, ++index);
                int         layawayDeletionsCount       = rs.getInt(++index);
                CurrencyIfc layawayDeletionsAmount      = getCurrencyFromDecimal(rs, ++index);
                int         layawayInitiationFeesCount  = rs.getInt(++index);
                CurrencyIfc layawayInitiationFeesAmount = getCurrencyFromDecimal(rs, ++index);
                int         layawayDeletionFeesCount    = rs.getInt(++index);
                CurrencyIfc layawayDeletionFeesAmount   = getCurrencyFromDecimal(rs, ++index);
                int         orderPaymentsCount          = rs.getInt(++index);
                CurrencyIfc orderPaymentsAmount         = getCurrencyFromDecimal(rs, ++index);
                int         orderCancelsCount           = rs.getInt(++index);
                CurrencyIfc orderCancelsAmount          = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc shippingChargesAmount       = getCurrencyFromDecimal(rs, ++index);
                int         shippingChargesCount        = rs.getInt(++index);
                CurrencyIfc shippingChargesTaxAmount    = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc shippingChargesInclusiveTaxAmount    = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc tillPayInsAmount            = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc tillPayOutsAmount           = getCurrencyFromDecimal(rs, ++index);
                int         tillPayInsCount             = rs.getInt(++index);
                int         tillPayOutsCount            = rs.getInt(++index);

                CurrencyIfc grossTaxableItemSalesAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossTaxableItemSalesCount          = getBigDecimal(rs, ++index);
                CurrencyIfc grossTaxableItemReturnsAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossTaxableItemReturnsCount        = getBigDecimal(rs, ++index);
                CurrencyIfc itemSalesTaxAmount          = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc itemSalesInclusiveTaxAmount          = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc transactionSalesTaxAmount   = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc transactionSalesInclusiveTaxAmount   = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc grossTaxableTransactionSalesAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc grossNonTaxableTransactionSalesAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc grossTaxExemptTransactionSalesAmount  = getCurrencyFromDecimal(rs, ++index);
                int grossNonTaxableTransactionSalesCount = rs.getInt(++index);
                int grossTaxableTransactionSalesCount   = rs.getInt(++index);
                int grossTaxExemptTransactionSalesCount  = rs.getInt(++index);
                CurrencyIfc grossTaxableTransactionReturnsAmount = getCurrencyFromDecimal(rs, ++index);
                int grossTaxableTransactionReturnsCount  = rs.getInt(++index);
                CurrencyIfc grossTaxableNonMerchandiseSalesAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossTaxableNonMerchandiseSalesCount     = getBigDecimal(rs, ++index);
                CurrencyIfc grossNonTaxableNonMerchandiseSalesAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossNonTaxableNonMerchandiseSalesCount = getBigDecimal(rs, ++index);
                CurrencyIfc giftCertificateIssuedAmount    = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  giftCertificateIssuedCount     = getBigDecimal(rs, ++index);
                
                CurrencyIfc grossGiftCardIssuedAmount  = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  grossGiftCardIssuedCount   = getBigDecimal(rs, ++index);
                CurrencyIfc grossGiftCardReloadedAmount  = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  grossGiftCardReloadedCount   = getBigDecimal(rs, ++index);
                CurrencyIfc grossGiftCardRedeemedAmount  = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  grossGiftCardRedeemedCount   = getBigDecimal(rs, ++index);
                CurrencyIfc grossGiftCardIssueVoidedAmount  = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  grossGiftCardIssueVoidedCount   = getBigDecimal(rs, ++index);
                CurrencyIfc grossGiftCardReloadVoidedAmount  = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  grossGiftCardReloadVoidedCount   = getBigDecimal(rs, ++index);
                CurrencyIfc grossGiftCardRedeemVoidedAmount  = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  grossGiftCardRedeemVoidedCount   = getBigDecimal(rs, ++index);

                int houseAccountsApproved            = rs.getInt(++index);
                int houseAccountsDeclined            = rs.getInt(++index);
                
                CurrencyIfc amountGrossGiftCardItemCredit = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossGiftCardItemCredit = getBigDecimal(rs, ++index);
                CurrencyIfc amountGrossGiftCardItemCreditVoided = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossGiftCardItemCreditVoided = getBigDecimal(rs, ++index);

                CurrencyIfc amountGrossGiftCertificatesRedeemed = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossGiftCertificatesRedeemed = getBigDecimal(rs, ++index);
                CurrencyIfc amountGrossGiftCertificatesRedeemedVoided = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossGiftCertificatesRedeemedVoided = getBigDecimal(rs, ++index);

                CurrencyIfc amountGrossStoreCreditsIssued = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossStoreCreditsIssued = getBigDecimal(rs, ++index);
                CurrencyIfc amountGrossStoreCreditsIssuedVoided = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossStoreCreditsIssuedVoided = getBigDecimal(rs, ++index);
                
                CurrencyIfc amountGrossStoreCreditsRedeemed = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossStoreCreditsRedeemed = getBigDecimal(rs, ++index);
                CurrencyIfc amountGrossStoreCreditsRedeemedVoided = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossStoreCreditsRedeemedVoided = getBigDecimal(rs, ++index);

                CurrencyIfc amountGrossItemEmployeeDiscount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossItemEmployeeDiscount = getBigDecimal(rs, ++index);
                CurrencyIfc amountGrossItemEmployeeDiscountVoided = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossItemEmployeeDiscountVoided = getBigDecimal(rs, ++index);

                CurrencyIfc amountGrossTransactionEmployeeDiscount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossTransactionEmployeeDiscount = getBigDecimal(rs, ++index);
                CurrencyIfc amountGrossTransactionEmployeeDiscountVoided = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossTransactionEmployeeDiscountVoided = getBigDecimal(rs, ++index);

                CurrencyIfc grossGiftCertificateIssuedVoidedAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossGiftCertificateIssuedVoidedUnits = getBigDecimal(rs, ++index);
                CurrencyIfc grossGiftCertificatedTenderedAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossGiftCertificatedTenderedUnits = getBigDecimal(rs, ++index);
                CurrencyIfc grossGiftCertificatedTenderedVoidedAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossGiftCertificatedTenderedVoidedUnits = getBigDecimal(rs, ++index);
                CurrencyIfc grossEmployeeDiscountAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossEmployeeDiscountUnits = getBigDecimal(rs, ++index);
                CurrencyIfc grossCustomerDiscountAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossCustomerDiscountUnits = getBigDecimal(rs, ++index);
                CurrencyIfc priceOverridesAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal priceOverridesUnits = getBigDecimal(rs, ++index);
                int transactionsWithReturnedItemsCount = rs.getInt(++index);
                /*
                 * Timestamps need to be initialized this way,
                 * using getEYSDateFromString() returns a DATE_ONLY EYSDate.
                 */
                EYSDate openTime = new EYSDate(timestampToDate(startTime));

                /*
                 * Workstation
                 */
                WorkstationIfc workstation = instantiateWorkstation();
                workstation.setStore(store.getPosStore()); // cheat
                workstation.setWorkstationID(workstationID);

                /*
                 * AbstractFinancialEntity
                 */
                RegisterIfc register = instantiateRegister();
                register.resetTotals();
                register.setWorkstation(workstation);
                register.setStatus(statusCode);
                register.setOpenTime(openTime);
                register.setBusinessDate(store.getBusinessDate()); // cheat

                /*
                 * FinancialTotals
                 */
                FinancialTotalsIfc totals = instantiateFinancialTotals();
                totals.setTransactionCount(transactionCount);
                totals.setCurrencyID(currencyID); //I18N

                // Item Sales
                //totals.setAmountGrossTaxableItemSales(itemSalesAmount.subtract(itemNontaxableAmount));
                totals.setAmountGrossTaxableItemSales(grossTaxableItemSalesAmount);
                //totals.setUnitsGrossTaxableItemSales(itemSalesCount.subtract(itemNontaxableCount));
                totals.setUnitsGrossTaxableItemSales(grossTaxableItemSalesCount);
                totals.setAmountGrossNonTaxableItemSales(itemNontaxableAmount);
                totals.setUnitsGrossNonTaxableItemSales(itemNontaxableCount);
                totals.setAmountGrossTaxExemptItemSales(itemTaxExemptAmount);
                totals.setUnitsGrossTaxExemptItemSales(itemTaxExemptCount);
                // Item Returns
                //totals.setAmountGrossTaxableItemReturns(returnAmount.subtract(itemNontaxableReturnAmount));
                totals.setAmountGrossTaxableItemReturns(grossTaxableItemReturnsAmount);
                //totals.setUnitsGrossTaxableItemReturns(returnCount.subtract(itemNontaxableReturnCount));
                totals.setUnitsGrossTaxableItemReturns(grossTaxableItemReturnsCount);
                totals.setAmountGrossNonTaxableItemReturns(itemNontaxableReturnAmount);
                totals.setUnitsGrossNonTaxableItemReturns(itemNontaxableReturnCount);
                totals.setAmountGrossTaxExemptItemReturns(itemTaxExemptReturnAmount);
                totals.setUnitsGrossTaxExemptItemReturns(itemTaxExemptReturnCount);
                // Tax
                //totals.setAmountTaxItemSales(netTaxAmount.add(taxReturnAmount));
                totals.setAmountTaxItemSales(itemSalesTaxAmount);
                totals.setAmountInclusiveTaxItemSales(itemSalesInclusiveTaxAmount);
                totals.setAmountTaxItemReturns(taxReturnAmount);
                totals.setAmountInclusiveTaxItemReturns(inclusiveTaxReturnAmount);
                //totals.setAmountTaxTransactionSales(netTaxAmount.add(taxRefundAmount));
                totals.setAmountTaxTransactionSales(transactionSalesTaxAmount);
                totals.setAmountInclusiveTaxTransactionSales(transactionSalesInclusiveTaxAmount);
                totals.setAmountTaxTransactionReturns(taxRefundAmount);
                totals.setAmountInclusiveTaxTransactionReturns(inclusiveTaxRefundAmount);
                // Transaction Sales
                //CurrencyIfc nontaxableTransactionAmount = netNontaxableAmount.add(nontaxableRefundAmount);
                //CurrencyIfc taxExemptTransactionAmount = netTaxExemptAmount.add(taxExemptRefundAmount);
                //totals.setAmountGrossTaxableTransactionSales(transactionSalesAmount.subtract(nontaxableTransactionAmount));
                totals.setAmountGrossTaxableTransactionSales(grossTaxableTransactionSalesAmount);

                //int nontaxableTransactionSalesCount = nontaxableTransactionCount - nontaxableRefundCount;
                //int taxExemptTransactionSalesCount = taxExemptTransactionCount - taxExemptRefundCount;

                // taxable transaction sales = taxable transactions - taxable refund transactions
                //totals.setCountGrossTaxableTransactionSales
                //  (taxableTransactionCount - (refundCount - nontaxableRefundCount));
                totals.setCountGrossTaxableTransactionSales(grossTaxableTransactionSalesCount);
                //totals.setAmountGrossNonTaxableTransactionSales(nontaxableTransactionAmount);
                totals.setAmountGrossNonTaxableTransactionSales(grossNonTaxableTransactionSalesAmount);
                //totals.setCountGrossNonTaxableTransactionSales(nontaxableTransactionSalesCount);
                totals.setCountGrossNonTaxableTransactionSales(grossNonTaxableTransactionSalesCount);
                //totals.setAmountGrossTaxExemptTransactionSales(taxExemptTransactionAmount);
                totals.setAmountGrossTaxExemptTransactionSales(grossTaxExemptTransactionSalesAmount);
                //totals.setCountGrossTaxExemptTransactionSales(taxExemptTransactionSalesCount);
                totals.setCountGrossTaxExemptTransactionSales(grossTaxExemptTransactionSalesCount);

                // Transaction Returns
                //totals.setAmountGrossTaxableTransactionReturns(refundAmount.subtract(nontaxableRefundAmount));
                totals.setAmountGrossTaxableTransactionReturns(grossTaxableTransactionReturnsAmount);
                //totals.setCountGrossTaxableTransactionReturns(refundCount - nontaxableRefundCount);
                totals.setCountGrossTaxableTransactionReturns(grossTaxableTransactionReturnsCount);
                totals.setAmountGrossNonTaxableTransactionReturns(nontaxableRefundAmount);
                totals.setCountGrossNonTaxableTransactionReturns(nontaxableRefundCount);
                totals.setAmountGrossTaxExemptTransactionReturns(taxExemptRefundAmount);
                totals.setCountGrossTaxExemptTransactionReturns(taxExemptRefundCount);
                // Misc
                    //Set StoreCouponDiscounts
                totals.setAmountTransactionDiscStoreCoupons(transactionDiscStoreCouponAmount);
                totals.setNumberTransactionDiscStoreCoupons(transactionDiscStoreCouponCount);
                totals.setAmountItemDiscStoreCoupons(itemDiscStoreCouponAmount);
                totals.setNumberItemDiscStoreCoupons(itemDiscStoreCouponCount);

                totals.setAmountTransactionDiscounts(miscDiscountAmount);
                totals.setNumberTransactionDiscounts(miscDiscountCount);
                totals.setAmountItemDiscounts(discountAmount);
                totals.setNumberItemDiscounts(discountCount);
                totals.setAmountItemMarkdowns(markdownAmount);
                totals.setNumberItemMarkdowns(markdownCount);
                totals.setAmountPostVoids(postVoidAmount);
                totals.setNumberPostVoids(postVoidCount);
                totals.setNumberNoSales(noSaleCount);
                totals.setAmountLineVoids(itemVoidAmount);
                totals.setUnitsLineVoids(itemVoidCount);
                totals.setAmountCancelledTransactions(voidTransactionAmount);
                totals.setNumberCancelledTransactions(voidTransactionCount);

                //totals.setAmountGrossTaxableNonMerchandiseSales(nonMerchTaxAmount.add(nonMerchTaxReturnAmount));
                totals.setAmountGrossTaxableNonMerchandiseSales(grossTaxableNonMerchandiseSalesAmount);
                //totals.setUnitsGrossTaxableNonMerchandiseSales(nonMerchTaxCount.add(nonMerchTaxReturnCount));
                totals.setUnitsGrossTaxableNonMerchandiseSales(grossTaxableNonMerchandiseSalesCount);
                //totals.setAmountGrossNonTaxableNonMerchandiseSales(nonMerchNonTaxAmount.add(nonMerchNonTaxReturnAmount));
                totals.setAmountGrossNonTaxableNonMerchandiseSales(grossNonTaxableNonMerchandiseSalesAmount);
                //totals.setUnitsGrossNonTaxableNonMerchandiseSales(nonMerchNonTaxCount.add(nonMerchNonTaxReturnCount));
                totals.setUnitsGrossNonTaxableNonMerchandiseSales(grossNonTaxableNonMerchandiseSalesCount);
                totals.setAmountGrossTaxableNonMerchandiseReturns(nonMerchTaxReturnAmount);
                totals.setUnitsGrossTaxableNonMerchandiseReturns(nonMerchTaxReturnCount);
                totals.setAmountGrossNonTaxableNonMerchandiseReturns(nonMerchNonTaxReturnAmount);
                totals.setUnitsGrossNonTaxableNonMerchandiseReturns(nonMerchNonTaxReturnCount);
                totals.setAmountGrossGiftCardItemSales(giftCardAmount.add(giftCardReturnAmount));
                totals.setUnitsGrossGiftCardItemSales(giftCardCount.add(giftCardReturnCount));
                totals.setAmountGrossGiftCardItemReturns(giftCardReturnAmount);
                totals.setUnitsGrossGiftCardItemReturns(giftCardReturnCount);
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
                totals.setAmountTaxShippingCharges(shippingChargesTaxAmount);
                totals.setAmountInclusiveTaxShippingCharges(shippingChargesInclusiveTaxAmount);
                totals.setAmountTillPayIns(tillPayInsAmount);
                totals.setAmountTillPayOuts(tillPayOutsAmount);
                totals.setCountTillPayIns(tillPayInsCount);
                totals.setCountTillPayOuts(tillPayOutsCount);
                totals.setAmountGrossGiftCertificateIssued(giftCertificateIssuedAmount);
                totals.setUnitsGrossGiftCertificateIssued(giftCertificateIssuedCount);
                
                totals.setAmountGrossGiftCardItemIssued(grossGiftCardIssuedAmount);
                totals.setUnitsGrossGiftCardItemIssued(grossGiftCardIssuedCount);
                totals.setAmountGrossGiftCardItemReloaded(grossGiftCardReloadedAmount);
                totals.setUnitsGrossGiftCardItemReloaded(grossGiftCardReloadedCount);
                totals.setAmountGrossGiftCardItemRedeemed(grossGiftCardRedeemedAmount);
                totals.setUnitsGrossGiftCardItemRedeemed(grossGiftCardRedeemedCount);
                totals.setAmountGrossGiftCardItemIssueVoided(grossGiftCardIssueVoidedAmount);
                totals.setUnitsGrossGiftCardItemIssueVoided(grossGiftCardIssueVoidedCount);
                totals.setAmountGrossGiftCardItemReloadVoided(grossGiftCardReloadVoidedAmount);
                totals.setUnitsGrossGiftCardItemReloadVoided(grossGiftCardReloadVoidedCount);
                totals.setAmountGrossGiftCardItemRedeemedVoided(grossGiftCardRedeemVoidedAmount);
                totals.setUnitsGrossGiftCardItemRedeemedVoided(grossGiftCardRedeemVoidedCount);

                totals.setHouseCardEnrollmentsApproved(houseAccountsApproved);
                totals.setHouseCardEnrollmentsDeclined(houseAccountsDeclined);
                
                totals.setAmountGrossGiftCardItemCredit(amountGrossGiftCardItemCredit);
                totals.setUnitsGrossGiftCardItemCredit(unitsGrossGiftCardItemCredit);
                totals.setAmountGrossGiftCardItemCreditVoided(amountGrossGiftCardItemCreditVoided);
                totals.setUnitsGrossGiftCardItemCreditVoided(unitsGrossGiftCardItemCreditVoided);
                totals.setAmountGrossGiftCertificatesRedeemed(amountGrossGiftCertificatesRedeemed);
                totals.setUnitsGrossGiftCertificatesRedeemed(unitsGrossGiftCertificatesRedeemed);
                totals.setAmountGrossGiftCertificatesRedeemedVoided(amountGrossGiftCertificatesRedeemedVoided);
                totals.setUnitsGrossGiftCertificatesRedeemedVoided(unitsGrossGiftCertificatesRedeemedVoided);
                totals.setAmountGrossStoreCreditsIssued(amountGrossStoreCreditsIssued);
                totals.setUnitsGrossStoreCreditsIssued(unitsGrossStoreCreditsIssued);
                totals.setAmountGrossStoreCreditsIssuedVoided(amountGrossStoreCreditsIssuedVoided);
                totals.setUnitsGrossStoreCreditsIssuedVoided(unitsGrossStoreCreditsIssuedVoided);
                totals.setAmountGrossStoreCreditsRedeemed(amountGrossStoreCreditsRedeemed);
                totals.setUnitsGrossStoreCreditsRedeemed(unitsGrossStoreCreditsRedeemed);
                totals.setAmountGrossStoreCreditsRedeemedVoided(amountGrossStoreCreditsRedeemedVoided);
                totals.setUnitsGrossStoreCreditsRedeemedVoided(unitsGrossStoreCreditsRedeemedVoided);
                totals.setAmountGrossItemEmployeeDiscount(amountGrossItemEmployeeDiscount);
                totals.setUnitsGrossItemEmployeeDiscount(unitsGrossItemEmployeeDiscount);
                totals.setAmountGrossItemEmployeeDiscountVoided(amountGrossItemEmployeeDiscountVoided);
                totals.setUnitsGrossItemEmployeeDiscountVoided(unitsGrossItemEmployeeDiscountVoided);
                totals.setAmountGrossTransactionEmployeeDiscount(amountGrossTransactionEmployeeDiscount);
                totals.setUnitsGrossTransactionEmployeeDiscount(unitsGrossTransactionEmployeeDiscount);
                totals.setAmountGrossTransactionEmployeeDiscountVoided(amountGrossTransactionEmployeeDiscountVoided);
                totals.setUnitsGrossTransactionEmployeeDiscountVoided(unitsGrossTransactionEmployeeDiscountVoided);
                totals.setAmountGrossGiftCertificateIssuedVoided(grossGiftCertificateIssuedVoidedAmount);
                totals.setUnitsGrossGiftCertificateIssuedVoided(grossGiftCertificateIssuedVoidedUnits);
                totals.setAmountGrossGiftCertificateTendered(grossGiftCertificatedTenderedAmount);
                totals.setUnitsGrossGiftCertificateTendered(grossGiftCertificatedTenderedUnits);
                totals.setAmountGrossGiftCertificateTenderedVoided(grossGiftCertificatedTenderedVoidedAmount);
                totals.setUnitsGrossGiftCertificateTenderedVoided(grossGiftCertificatedTenderedVoidedUnits);
                totals.setAmountEmployeeDiscounts(grossEmployeeDiscountAmount);
                totals.setUnitsEmployeeDiscounts(grossEmployeeDiscountUnits);
                totals.setAmountCustomerDiscounts(grossCustomerDiscountAmount);
                totals.setUnitsCustomerDiscounts(grossCustomerDiscountUnits);
                totals.setAmountPriceOverrides(priceOverridesAmount);
                totals.setUnitsPriceOverrides(priceOverridesUnits);
                totals.setTransactionsWithReturnedItemsCount(transactionsWithReturnedItemsCount);
                
                register.setTotals(totals);
                workstationVector.addElement(register);
                
            }
                


            if (workstationVector.isEmpty())
            {
                throw new DataException(NO_DATA, "readWorkstationHistory");
            }
            
            rs.close();
        }
        catch (SQLException se)
        {
            throw new DataException(SQL_ERROR, "readWorkstationHistory", se);
        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(UNKNOWN, "readWorkstationHistory", e);
        }
        return(workstationVector);
    }

    /**
       Returns the SQL string representation of an EYSDate.
       <p>
       @param  date  An EYSDate
       @return The SQL string representation of an EYSDate.
     */
    protected String getBusinessDate(EYSDate date)
    {
        return(dateToSQLDateString(date.dateValue()));
    }

    /**
       Returns the store ID
       <p>
       @param  store  An ARTSStore
       @return The store ID
     */
    protected String getStoreID(ARTSStore store)
    {
        return("'" + store.getPosStore().getStoreID() + "'");
    }

    /**
       Returns the workstation ID
       <p>
       @param  register  A Register
       @return The workstation ID
     */
    protected String getWorkstationID(RegisterIfc register)
    {
        return("'" + register.getWorkstation().getWorkstationID() + "'");
    }

    /**
       Returns the last transaction sequence number.
       <p>
       @param  register  A register
       @return The last transaction sequence number
     */
    protected int getLastSequenceNumber(RegisterIfc register)
    {
        return(register.getLastTransactionSequenceNumber());
    }

    /**
       Returns the starting timestamp for the register session
       <p>
       @param  register  A register
       @return The starting timestamp for the register session
     */
    protected String getStartTimestamp(RegisterIfc register)
    {
        return(dateToSQLTimestampString(register.getOpenTime().dateValue()));
    }

    /**
       Instantiates a workstation object.
       <p>
       @return new WorkstationIfc object
     */
    protected WorkstationIfc instantiateWorkstation()
    {
        return(DomainGateway.getFactory().getWorkstationInstance());
    }

    /**
       Instantiates a register object.
       <p>
       @return new RegisterIfc object
     */
    protected RegisterIfc instantiateRegister()
    {
        return(DomainGateway.getFactory().getRegisterInstance());
    }

    /**
       Instantiates a financial totals object.
       <p>
       @return new FinancialTotalsIfc object
     */
    protected FinancialTotalsIfc instantiateFinancialTotals()
    {
        return(DomainGateway.getFactory().getFinancialTotalsInstance());
    }

    /**
       Returns the employee for the given employeeID.  Uses the
       <code>JdbcEmployeeLookupOperation</code> data operation to
       read the employee from the data source.
       <p>
       @param  dataConnection  The connection to the data source
       @param  employeeID      The employee id
       @return the employee
       @exception DataException upon error.
     */
    protected EmployeeIfc getEmployee(JdbcDataConnection dataConnection,
                                      String employeeID)
        throws DataException
    {
        /*
         * Design-wise, this is probably not the best way to do this,
         * but it's fast and effective (at least for now).
         */
        JdbcEmployeeLookupOperation dbOp = new JdbcEmployeeLookupOperation();
        dbOp.setRetreiveAllEmployees(true);
        return(dbOp.selectEmployeeByNumber(dataConnection, employeeID));
    }
}
