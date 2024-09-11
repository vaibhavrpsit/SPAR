/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadStoreTotals.java /main/19 2013/02/27 15:01:17 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     02/20/13 - Modified for Currency Rounding.
 *    nkgautam  07/28/10 - Bill Payment Report changes
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    jswan     08/13/09 - Added the till reconcile amount and count to the
 *                         till, workstation and store tender history tables
 *                         and to code that reads from and writes to them.
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         5/16/2007 7:55:27 PM   Brett J. Larsen
 *         CR 26903 - 8.0.1 merge to trunk
 *
 *         BackOffice <ARG> Summary Report overhaul (many CRs fixed)
 *         
 *    8    360Commerce 1.7         5/14/2007 6:08:34 PM   Sandy Gu
 *         update inclusive information in financial totals and history tables
 *    7    360Commerce 1.6         4/25/2007 10:01:13 AM  Anda D. Cadar   I18N
 *         merge
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
 *    4    .v700     1.2.1.0     11/16/2005 16:26:06    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:45     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:00     Robert Pearse
 *
 *   Revision 1.19  2004/06/15 00:44:30  jdeleau
 *   @scr 2775 Support register reports and financial totals with the new
 *   tax engine.
 *
 *   Revision 1.18  2004/05/12 15:03:57  jdeleau
 *   @scr 4218 Remove GrossTransactionDiscount Amounts, Units, UnitsVoid,
 *   and AmountVoids in favor of the already existing AmountTransactionDiscounts
 *   and NumberTransactionDiscounts, which end up already being NET totals.
 *
 *   Revision 1.17  2004/05/11 23:03:01  jdeleau
 *   @scr 4218 Backout recent changes to remove TransactionDiscounts,
 *   going to go a different route and remove the newly added
 *   voids and grosses instead.
 *
 *   Revision 1.15  2004/04/28 19:41:39  jdeleau
 *   @scr 4218 Add StoreCreditsIssued (count/amount and voided counts and amounts)
 *   to Financial Totals.
 *
 *   Revision 1.14  2004/04/27 20:01:16  jdeleau
 *   @scr 4218 Add in the concrete calls for register reports data, refactor
 *   the houseCardEnrollment methods to be in line with other FinancialTotals
 *   methods.
 *
 *   Revision 1.13  2004/04/26 21:07:22  jdeleau
 *   @scr 4218 Put calls to new Financial Totals data in register reports,
 *   correct error in ArtsDatabaseIfc
 *
 *   Revision 1.12  2004/04/26 18:23:40  jdeleau
 *   @scr 4128 JDBC changes to support new data required for register reports
 *
 *   Revision 1.11  2004/04/08 16:45:59  cdb
 *   @scr 4204 Removing tabs - again.
 *
 *   Revision 1.10  2004/04/07 20:56:49  lzhao
 *   @scr 4218: add gift card info for summary report.
 *
 *   Revision 1.9  2004/04/05 23:03:01  jdeleau
 *   @scr 4218 JavaDoc fixes associated with RegisterReports changes
 *
 *   Revision 1.8  2004/04/02 23:07:33  jdeleau
 *   @scr 4218 Register Reports - House Account and initial changes to
 *   the way SummaryReports are built.
 *
 *   Revision 1.7  2004/03/25 17:12:16  blj
 *   @scr 3872 - fixed summary reports for store credit redeem.
 *
 *   Revision 1.6  2004/02/25 23:02:29  crain
 *   @scr 3814 Issue Gift Certificate
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:12   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Jul 15 2003 11:18:36   sfl
 * Read the stored void counts from StoreHistory table for calculating the Net Trans. count in store report.
 * Resolution for POS SCR-3165: Register Reports -  Store "Net Trans Tax Count " line is not correct.
 *
 *    Rev 1.3   Jul 01 2003 13:27:18   jgs
 * Fixed problem which doubled pickup counts.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 *
 *    Rev 1.2   Feb 15 2003 17:25:50   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.1   11 Jun 2002 16:25:02   jbp
 * changes to report markdowns
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   Jun 03 2002 16:38:20   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;


import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.utility.CountryCodeMap;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
    This data operation gets the current totals for the specified store.
    <P>
    @version $Revision: /main/19 $
**/
public class JdbcReadStoreTotals extends JdbcDataOperation
                                 implements ARTSDatabaseIfc
{
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadStoreTotals.class);
    /**
       revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/19 $";
    // Tender type map
    protected static TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();

    /**
       Class constructor.
     */
    public JdbcReadStoreTotals()
    {
        super();
        setName("JdbcReadStoreTotals");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadStoreTotals.execute()");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ARTSStore artsStore = (ARTSStore)action.getDataObject();
        FinancialTotalsIfc totals = readStoreTotals(connection,
                                                    artsStore.getPosStore(),
                                                    artsStore.getBusinessDate());

        /*
         * Send back the result
         */
        dataTransaction.setResult(totals);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadStoreTotals.execute()");
    }

    /**
       Returns the financial totals for the store on a given business day.
       <P>
       @param  dataConnection  connection to the db
       @param store The store to read
       @param businessDate the date to read for
       @return status of store, or null if no store history records.
       @exception DataException upon error
     */
    public FinancialTotalsIfc readStoreTotals(JdbcDataConnection dataConnection,
                                              StoreIfc store,
                                              EYSDate businessDate)
        throws DataException
    {
        FinancialTotalsIfc totals = selectStoreHistory(dataConnection,
                                                       store,
                                                       businessDate);

        return(totals.add(selectStoreTenderHistory(dataConnection, store, businessDate)));
    }

    /**
       Returns the information from the Store History table.
       <P>
       @param dataConnection  connection to the db
       @param store The store to get the history for
       @param businessDate the date to get history for
       @return status of store, or null if no store history records.
       @exception DataException upon error
     */
    public FinancialTotalsIfc selectStoreHistory(JdbcDataConnection dataConnection,
                                                 StoreIfc store,
                                                 EYSDate businessDate)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_STORE_HISTORY + " " + ALIAS_STORE_HISTORY);
        sql.addTable(TABLE_REPORTING_PERIOD + " " + ALIAS_REPORTING_PERIOD);
        sql.addTable(TABLE_BUSINESS_DAY + " " + ALIAS_BUSINESS_DAY);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_CURRENCY_ID); //I18N

        // These are in the same order as the ARTS document
        sql.addColumn(FIELD_STORE_TOTAL_NO_SALE_TRANSACTION_COUNT);
        sql.addColumn(FIELD_STORE_TOTAL_SALE_LINE_ITEM_COUNT);
        sql.addColumn(FIELD_STORE_TOTAL_TAX_EXEMPT_TRANSACTION_COUNT);
        sql.addColumn(FIELD_STORE_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_INCLUSIVE_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_SALES_EX_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_DISCOUNT_COUNT);
        sql.addColumn(FIELD_STORE_DISCOUNT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_MARKDOWN_COUNT);
        sql.addColumn(FIELD_STORE_MARKDOWN_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_MISCELLANEOUS_DISCOUNT_COUNT);
        sql.addColumn(FIELD_STORE_MISCELLANEOUS_DISCOUNT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_REFUND_COUNT);
        sql.addColumn(FIELD_STORE_REFUND_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_RETURN_COUNT);
        sql.addColumn(FIELD_STORE_RETURN_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TENDER_PICKUP_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TENDER_LOAN_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_TENDER_PICKUP_COUNT);
        sql.addColumn(FIELD_STORE_TOTAL_TRANSACTION_VOID_COUNT);
        sql.addColumn(FIELD_STORE_TOTAL_POST_TRANSACTION_VOID_COUNT);
        sql.addColumn(FIELD_STORE_POST_TRANSACTION_VOID_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_LINE_ITEM_VOID_COUNT);
        sql.addColumn(FIELD_STORE_LINE_ITEM_VOID_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_TRANSACTION_COUNT);
        sql.addColumn(FIELD_STORE_TOTAL_TENDER_LOAN_COUNT);
        sql.addColumn(FIELD_STORE_TRANSACTION_VOID_TOTAL_AMOUNT);
        // Additional fields not defined in ARTS
            // StoreCouponDiscounts
        sql.addColumn(FIELD_STORE_ITEM_DISCOUNT_STORE_COUPON_AMOUNT);
        sql.addColumn(FIELD_STORE_ITEM_DISCOUNT_STORE_COUPON_COUNT);
        sql.addColumn(FIELD_STORE_TRANSACTION_DISCOUNT_STORE_COUPON_AMOUNT);
        sql.addColumn(FIELD_STORE_TRANSACTION_DISCOUNT_STORE_COUPON_COUNT);
            //
        sql.addColumn(FIELD_STORE_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_NONTAXABLE_COUNT);
        sql.addColumn(FIELD_STORE_TOTAL_TAXABLE_COUNT);
        sql.addColumn(FIELD_STORE_REFUND_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_NONTAXABLE_REFUND_COUNT);
        sql.addColumn(FIELD_STORE_REFUND_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_TAX_EXEMPT_REFUND_COUNT);
        sql.addColumn(FIELD_STORE_LINE_ITEM_SALES_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_NONTAXABLE_LINE_ITEM_COUNT);
        sql.addColumn(FIELD_STORE_LINE_ITEM_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_TAX_EXEMPT_LINE_ITEM_COUNT);
        sql.addColumn(FIELD_STORE_RETURN_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_NONTAXABLE_RETURN_COUNT);
        sql.addColumn(FIELD_STORE_RETURN_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_TAX_EXEMPT_RETURN_COUNT);
        sql.addColumn(FIELD_STORE_REFUND_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_REFUND_INCLUSIVE_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_RETURN_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_RETURN_INCLUSIVE_TAX_TOTAL_AMOUNT);

        sql.addColumn(FIELD_STORE_NONMERCH_NONTAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_NONMERCH_NONTAX_COUNT);
        sql.addColumn(FIELD_STORE_RETURN_NONMERCH_NONTAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_NONMERCH_NONTAX_RETURN_COUNT);
        sql.addColumn(FIELD_STORE_NONMERCH_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_NONMERCH_TAX_COUNT);
        sql.addColumn(FIELD_STORE_RETURN_NONMERCH_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_NONMERCH_TAX_RETURN_COUNT);
        sql.addColumn(FIELD_STORE_GIFT_CARD_AMOUNT);
        sql.addColumn(FIELD_STORE_GIFT_CARD_COUNT);
        sql.addColumn(FIELD_STORE_RETURN_GIFT_CARD_AMOUNT);
        sql.addColumn(FIELD_STORE_GIFT_CARD_RETURN_COUNT);
        sql.addColumn(FIELD_STORE_HOUSE_PAYMENT_AMOUNT);
        sql.addColumn(FIELD_STORE_HOUSE_PAYMENT_COUNT);
        sql.addColumn(FIELD_STORE_RESTOCKING_FEE_AMOUNT);
        sql.addColumn(FIELD_STORE_RESTOCKING_FEE_COUNT);
        sql.addColumn(FIELD_STORE_RESTOCKING_FEE_AMOUNT_FROM_NON_TAXABLE);
        sql.addColumn(FIELD_STORE_RESTOCKING_FEE_COUNT_FROM_NON_TAXABLE);
        sql.addColumn(FIELD_STORE_SHIPPING_CHARGE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_SHIPPING_CHARGE_COUNT);
        sql.addColumn(FIELD_STORE_SHIPPING_CHARGE_TAX_AMOUNT);
        sql.addColumn(FIELD_STORE_SHIPPING_CHARGE_INCLUSIVE_TAX_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_LAYAWAY_PAYMENTS_COLLECTED_COUNT);
        sql.addColumn(FIELD_STORE_LAYAWAY_PAYMENTS_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_LAYAWAY_NEW_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_LAYAWAY_PICKUP_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_LAYAWAY_DELETIONS_DISBURSEMENT_COUNT);
        sql.addColumn(FIELD_STORE_LAYAWAY_DELETIONS_DISBURSEMENT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_LAYAWAY_INITIATION_FEES_COLLECTED_COUNT);
        sql.addColumn(FIELD_STORE_LAYAWAY_INITIATION_FEES_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_LAYAWAY_DELETION_FEES_COUNT);
        sql.addColumn(FIELD_STORE_LAYAWAY_DELETION_FEES_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_SPECIAL_ORDER_NEW_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_SPECIAL_ORDER_PARTIAL_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_ORDER_PAYMENTS_COLLECTED_COUNT);
        sql.addColumn(FIELD_STORE_ORDER_PAYMENTS_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_ORDER_CANCELS_DISBURSEMENT_COUNT);
        sql.addColumn(FIELD_STORE_ORDER_CANCELS_DISBURSEMENT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_FUNDS_RECEIVED_IN_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_FUNDS_RECEIVED_OUT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_FUNDS_RECEIVED_IN_UNIT_COUNT);
        sql.addColumn(FIELD_STORE_FUNDS_RECEIVED_OUT_UNIT_COUNT);

        // Gross value related columns

        sql.addColumn(FIELD_STORE_GROSS_TAXABLE_ITEM_SALES_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_TAXABLE_ITEM_SALES_COUNT);
        sql.addColumn(FIELD_STORE_GROSS_TAXABLE_ITEM_RETURNS_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_TAXABLE_ITEM_RETURNS_COUNT);
        sql.addColumn(FIELD_STORE_ITEM_SALES_TAX_AMOUNT);
        sql.addColumn(FIELD_STORE_ITEM_SALES_INCLUSIVE_TAX_AMOUNT);
        sql.addColumn(FIELD_STORE_TRANSACTION_SALES_TAX_AMOUNT);
        sql.addColumn(FIELD_STORE_TRANSACTION_SALES_INCLUSIVE_TAX_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_TAXABLE_TRANSACTION_SALES_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_NON_TAXABLE_TRANSACTION_SALES_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_TAX_EXEMPT_TRANSACTION_SALES_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_NON_TAXABLE_TRANSACTION_SALES_COUNT);
        sql.addColumn(FIELD_STORE_GROSS_TAXABLE_TRANSACTION_SALES_COUNT);
        sql.addColumn(FIELD_STORE_GROSS_TAX_EXEMPT_TRANSACTION_SALES_COUNT);
        sql.addColumn(FIELD_STORE_GROSS_TAXABLE_TRANSACTION_RETURNS_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_TAXABLE_TRANSACTION_RETURNS_COUNT);
        sql.addColumn(FIELD_STORE_GROSS_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_TAXABLE_NON_MERCHANDISE_SALES_COUNT);
        sql.addColumn(FIELD_STORE_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_COUNT);
        sql.addColumn(FIELD_GROSS_TAXABLE_SALES_VOID_COUNT);
        sql.addColumn(FIELD_GROSS_TAXABLE_RETURNS_VOID_COUNT);
        sql.addColumn(FIELD_GROSS_NONTAXABLE_SALES_VOID_COUNT);
        sql.addColumn(FIELD_GROSS_NONTAXABLE_RETURNS_VOID_COUNT);
        sql.addColumn(FIELD_STORE_GIFT_CERTIFICATE_ISSUED_AMOUNT);
        sql.addColumn(FIELD_STORE_GIFT_CERTIFICATE_ISSUED_COUNT);

        sql.addColumn(FIELD_STORE_GIFT_CARD_ISSUED_AMOUNT);
        sql.addColumn(FIELD_STORE_GIFT_CARD_ISSUED_COUNT);
        sql.addColumn(FIELD_STORE_GIFT_CARD_RELOADED_AMOUNT);
        sql.addColumn(FIELD_STORE_GIFT_CARD_RELOADED_COUNT);
        sql.addColumn(FIELD_STORE_GIFT_CARD_REDEEMED_AMOUNT);
        sql.addColumn(FIELD_STORE_GIFT_CARD_REDEEMED_COUNT);
        sql.addColumn(FIELD_STORE_GIFT_CARD_ISSUE_VOIDED_AMOUNT);
        sql.addColumn(FIELD_STORE_GIFT_CARD_ISSUE_VOIDED_COUNT);
        sql.addColumn(FIELD_STORE_GIFT_CARD_RELOAD_VOIDED_AMOUNT);
        sql.addColumn(FIELD_STORE_GIFT_CARD_RELOAD_VOIDED_COUNT);
        sql.addColumn(FIELD_STORE_GIFT_CARD_REDEEM_VOIDED_AMOUNT);
        sql.addColumn(FIELD_STORE_GIFT_CARD_REDEEM_VOIDED_COUNT);

        sql.addColumn(FIELD_STORE_HOUSE_ACCOUNT_ENROLLMENT_APPROVED_COUNT);
        sql.addColumn(FIELD_STORE_HOUSE_ACCOUNT_ENROLLMENT_DECLINED_COUNT);

        sql.addColumn(FIELD_STORE_GROSS_GIFT_CARD_ITEM_CREDIT_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_GIFT_CARD_ITEM_CREDIT_UNITS);
        sql.addColumn(FIELD_STORE_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_UNITS);
        sql.addColumn(FIELD_STORE_GROSS_GIFT_CERTIFICATE_REDEEMED_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_GIFT_CERTIFICATE_REDEEMED_UNITS);
        sql.addColumn(FIELD_STORE_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_VOIDED_UNITS);
        sql.addColumn(FIELD_STORE_GROSS_STORE_CREDITS_REDEEMED_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_STORE_CREDITS_REDEEMED_UNITS);
        sql.addColumn(FIELD_STORE_GROSS_STORE_CREDITS_REDEEMED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_STORE_CREDITS_REDEEMED_VOIDED_UNITS);
        sql.addColumn(FIELD_STORE_GROSS_ITEM_EMPLOYEE_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_ITEM_EMPLOYEE_DISCOUNT_UNITS);
        sql.addColumn(FIELD_STORE_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_UNITS);
        sql.addColumn(FIELD_STORE_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_UNITS);
        sql.addColumn(FIELD_STORE_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_UNITS);
        sql.addColumn(FIELD_STORE_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_COUNT);
        sql.addColumn(FIELD_STORE_GROSS_GIFT_CERTIFICATE_TENDERED_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_GIFT_CERTIFICATE_TENDERED_COUNT);
        sql.addColumn(FIELD_STORE_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_COUNT);
        sql.addColumn(FIELD_STORE_GROSS_EMPLOYEE_DISCOUNTS_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_EMPLOYEE_DISCOUNTS_COUNT);
        sql.addColumn(FIELD_STORE_GROSS_CUSTOMER_DISCOUNTS_AMOUNT);
        sql.addColumn(FIELD_STORE_GROSS_CUSTOMER_DISCOUNTS_COUNT);
        sql.addColumn(FIELD_STORE_PRICE_OVERRIDES_AMOUNT);
        sql.addColumn(FIELD_STORE_PRICE_OVERRIDES_COUNT);
        sql.addColumn(FIELD_STORE_PRICE_ADJUSTMENTS_COUNT);
        sql.addColumn(FIELD_STORE_TRANSACTIONS_WITH_RETURNED_ITEMS_COUNT);
        sql.addColumn(FIELD_STORE_TOTAL_BILLPAYMENT);
        sql.addColumn(FIELD_STORE_TOTAL_BILLPAYMENT_COUNT);
        sql.addColumn(FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_IN);
        sql.addColumn(FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_OUT);
        // Other fields

        sql.addColumn(ALIAS_STORE_HISTORY + "." + FIELD_FISCAL_YEAR);
        sql.addColumn(ALIAS_STORE_HISTORY + "." + FIELD_REPORTING_PERIOD_ID);

        /*
         * Add Qualifier(s)
         */

        // For the specified store only
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(store));

        // Join Store History and Reporting Period
        sql.addQualifier(ALIAS_STORE_HISTORY + "." + FIELD_FISCAL_YEAR
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_YEAR);
        sql.addQualifier(ALIAS_STORE_HISTORY + "." + FIELD_REPORTING_PERIOD_TYPE_CODE
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_TYPE_CODE);
        sql.addQualifier(ALIAS_STORE_HISTORY + "." + FIELD_REPORTING_PERIOD_ID
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
                         + " = " + getBusinessDate(businessDate));

        FinancialTotalsIfc totals = instantiateFinancialTotals();
        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                /*
                 * Grab the fields selected from the database
                 */
                int index = 0;

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
                CurrencyIfc pickupAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc loanAmount = getCurrencyFromDecimal(rs, ++index);
                int pickupCount = rs.getInt(++index);
                int voidTransactionCount = rs.getInt(++index);
                int postVoidCount = rs.getInt(++index);
                CurrencyIfc postVoidAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal itemVoidCount = getBigDecimal(rs, ++index);
                CurrencyIfc itemVoidAmount = getCurrencyFromDecimal(rs, ++index);
                int transactionCount = rs.getInt(++index);
                int loanCount = rs.getInt(++index);
                CurrencyIfc voidTransactionAmount = getCurrencyFromDecimal(rs, ++index);
                // StoreCouponDiscounts
                CurrencyIfc itemDiscStoreCouponAmount = getCurrencyFromDecimal(rs, ++index);
                int itemDiscStoreCouponCount = rs.getInt(++index);
                CurrencyIfc transactionDiscStoreCouponAmount = getCurrencyFromDecimal(rs, ++index);
                int transactionDiscStoreCouponCount = rs.getInt(++index);
                //
                CurrencyIfc netNontaxableAmount = getCurrencyFromDecimal(rs, ++index);
                int nontaxableTransactionCount = rs.getInt(++index);
                int taxableTransactionCount = rs.getInt(++index);
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
                CurrencyIfc housePaymentsAmount         = getCurrencyFromDecimal(rs, ++index);
                int         housePaymentsCount          = rs.getInt(++index);
                CurrencyIfc restockingFeeAmount         = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  restockingFeeCount          = getBigDecimal(rs, ++index);
                CurrencyIfc restockingFeeAmountNonTax   = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  restockingFeeCountNonTax    = getBigDecimal(rs, ++index);
                CurrencyIfc shippingChargeAmount        = getCurrencyFromDecimal(rs, ++index);
                int         shippingChargeCount         = rs.getInt(++index);
                CurrencyIfc shippingChargeTaxAmount     = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc shippingChargeInclusiveTaxAmount        = getCurrencyFromDecimal(rs, ++index);
                int         layawayPaymentsCount        = rs.getInt(++index);
                CurrencyIfc layawayPaymentsAmount       = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc layawayNewAmount            = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc layawayPickupAmount         = getCurrencyFromDecimal(rs, ++index);
                int         layawayDeletionsCount       = rs.getInt(++index);
                CurrencyIfc layawayDeletionsAmount      = getCurrencyFromDecimal(rs, ++index);
                int         layawayInitiationFeesCount  = rs.getInt(++index);
                CurrencyIfc layawayInitiationFeesAmount = getCurrencyFromDecimal(rs, ++index);
                int         layawayDeletionFeesCount    = rs.getInt(++index);
                CurrencyIfc layawayDeletionFeesAmount   = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc specialOrderNewAmount      =  getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc specialOrderPartialAmount      = getCurrencyFromDecimal(rs, ++index);
                int         orderPaymentsCount          = rs.getInt(++index);
                CurrencyIfc orderPaymentsAmount         = getCurrencyFromDecimal(rs, ++index);
                int         orderCancelsCount           = rs.getInt(++index);
                CurrencyIfc orderCancelsAmount          = getCurrencyFromDecimal(rs, ++index);
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
                int taxableSalesVoidCount = rs.getInt(++index);
                int taxableReturnsVoidCount = rs.getInt(++index);
                int nonTaxableSalesVoidCount = rs.getInt(++index);
                int nonTaxableReturnsVoidVount = rs.getInt(++index);
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
                BigDecimal priceAdjustmentsUnits = getBigDecimal(rs, ++index);
                int transactionsWithReturnedItemsCount = rs.getInt(++index);
                
                CurrencyIfc totalBillPaymentAmount = getCurrencyFromDecimal(rs, ++index);
                int totalBillPaymentCount = rs.getInt(++index);
                
                CurrencyIfc amountChangeRoundedIn = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc amountChangeRoundedOut = getCurrencyFromDecimal(rs, ++index);

                /*
                 * FinancialTotals
                 */
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
                //totals.setAmountTaxTransactionSales(netTaxAmount.add(taxRefundAmount));
                totals.setAmountTaxTransactionSales(transactionSalesTaxAmount);
                totals.setAmountInclusiveTaxTransactionSales(transactionSalesInclusiveTaxAmount);
                totals.setAmountTaxTransactionReturns(taxRefundAmount);
                totals.setAmountInclusiveTaxTransactionReturns(inclusiveTaxRefundAmount);
                totals.setAmountTaxItemReturns(taxReturnAmount);
                totals.setAmountInclusiveTaxItemReturns(inclusiveTaxReturnAmount);

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
                    // StoreCouponDiscounts
                totals.setAmountItemDiscStoreCoupons(itemDiscStoreCouponAmount);
                totals.setNumberItemDiscStoreCoupons(itemDiscStoreCouponCount);
                totals.setAmountTransactionDiscStoreCoupons(transactionDiscStoreCouponAmount);
                totals.setNumberTransactionDiscStoreCoupons(transactionDiscStoreCouponCount);

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
                totals.setAmountGrossGiftCardItemSales(giftCardAmount);
                totals.setUnitsGrossGiftCardItemSales(giftCardCount);
                totals.setAmountGrossGiftCardItemReturns(giftCardReturnAmount);
                totals.setUnitsGrossGiftCardItemReturns(giftCardReturnCount);
                totals.setAmountHousePayments(housePaymentsAmount);
                totals.setCountHousePayments(housePaymentsCount);
                totals.setAmountRestockingFees(restockingFeeAmount);
                totals.setUnitsRestockingFees(restockingFeeCount);
                totals.setAmountRestockingFeesFromNonTaxableItems(restockingFeeAmountNonTax);
                totals.setUnitsRestockingFeesFromNonTaxableItems(restockingFeeCountNonTax);
                totals.setAmountShippingCharges(shippingChargeAmount);
                totals.setNumberShippingCharges(shippingChargeCount);
                totals.setAmountTaxShippingCharges(shippingChargeTaxAmount);
                totals.setAmountInclusiveTaxShippingCharges(shippingChargeInclusiveTaxAmount);
                totals.setAmountLayawayPayments(layawayPaymentsAmount);
                totals.setCountLayawayPayments(layawayPaymentsCount);
                totals.setAmountLayawayNew(layawayNewAmount);
                totals.setAmountLayawayPickup(layawayPickupAmount);
                totals.setAmountLayawayDeletions(layawayDeletionsAmount);
                totals.setCountLayawayDeletions(layawayDeletionsCount);
                totals.setAmountSpecialOrderNew(specialOrderNewAmount);
                totals.setAmountSpecialOrderPartial(specialOrderPartialAmount);
                totals.setAmountLayawayInitiationFees(layawayInitiationFeesAmount);
                totals.setCountLayawayInitiationFees(layawayInitiationFeesCount);
                totals.setAmountLayawayDeletionFees(layawayDeletionFeesAmount);
                totals.setCountLayawayDeletionFees(layawayDeletionFeesCount);
                totals.setAmountOrderPayments(orderPaymentsAmount);
                totals.setCountOrderPayments(orderPaymentsCount);
                totals.setAmountOrderCancels(orderCancelsAmount);
                totals.setCountOrderCancels(orderCancelsCount);
                totals.setAmountTillPayIns(tillPayInsAmount);
                totals.setAmountTillPayOuts(tillPayOutsAmount);
                totals.setCountTillPayIns(tillPayInsCount);
                totals.setCountTillPayOuts(tillPayOutsCount);
                totals.setCountTillLoans(loanCount);
                totals.setCountGrossTaxableTransactionSalesVoided(taxableSalesVoidCount);
                totals.setCountGrossTaxableTransactionReturnsVoided(taxableReturnsVoidCount);
                totals.setCountGrossNonTaxableTransactionSalesVoided(nonTaxableSalesVoidCount);
                totals.setCountGrossNonTaxableTransactionReturnsVoided(nonTaxableReturnsVoidVount);
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
                totals.setUnitsPriceAdjustments(priceAdjustmentsUnits);
                totals.setTransactionsWithReturnedItemsCount(transactionsWithReturnedItemsCount);
                totals.setAmountBillPayments(totalBillPaymentAmount);
                totals.setCountBillPayments(totalBillPaymentCount);
                totals.setAmountChangeRoundedIn(amountChangeRoundedIn);
                totals.setAmountChangeRoundedOut(amountChangeRoundedOut);
            }
            else
            {
                String msg = "selectStoreHistory: totals not found.";
                throw new DataException(NO_DATA, msg);
            }

            rs.close();

        }
        catch (SQLException se)
        {
            throw new DataException(SQL_ERROR, "selectStoreHistory", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(UNKNOWN, "selectStoreHistory", e);
        }
        return(totals);
    }

    /**
       Reads the information from the Store Tender History table.
       The information is added to status.
       <P>
       @param dataConnection  connection to the db
       @param store The store to get the history for
       @param businessDate the date to get the history for
       @return Financial totals for the store
       @exception DataException upon error
     */
    public FinancialTotalsIfc selectStoreTenderHistory(JdbcDataConnection dataConnection,
                                                       StoreIfc store,
                                                       EYSDate businessDate)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_STORE_TENDER_HISTORY + " " + ALIAS_STORE_TENDER_HISTORY);
        sql.addTable(TABLE_REPORTING_PERIOD + " " + ALIAS_REPORTING_PERIOD);
        sql.addTable(TABLE_BUSINESS_DAY + " " + ALIAS_BUSINESS_DAY);
        /*
         * Add desired columns
         */
        // These are in the same order as the ARTS document
        sql.addColumn(FIELD_TENDER_TYPE_CODE);
        sql.addColumn(FIELD_TENDER_SUBTYPE);
        sql.addColumn(FIELD_CURRENCY_ISSUING_COUNTRY_CODE);
        sql.addColumn(FIELD_CURRENCY_ID); //I18N
        sql.addColumn(FIELD_STORE_TOTAL_TENDER_MEDIA_COUNT);
        sql.addColumn(FIELD_STORE_TOTAL_TENDER_MEDIA_OVER_COUNT);
        sql.addColumn(FIELD_STORE_TOTAL_TENDER_MEDIA_SHORT_COUNT);
        sql.addColumn(FIELD_STORE_TENDER_SHORT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TENDER_OVER_TOTAL_AMOUNT);
        //sql.addColumn(FIELD_STORE_TOTAL_TENDER_DEPOSIT_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_STORE_TOTAL_TENDER_LOAN_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_STORE_TENDER_LOAN_MEDIA_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_TENDER_PICKUP_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_STORE_TENDER_PICKUP_MEDIA_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT);
        //sql.addColumn(FIELD_STORE_TENDER_DEPOSIT_TOTAL_AMOUNT);
        // Additional fields not defined in ARTS
        sql.addColumn(FIELD_STORE_TOTAL_TENDER_REFUND_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_STORE_TENDER_REFUND_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TENDER_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_TENDER_OPEN_AMOUNT);
        sql.addColumn(FIELD_STORE_TENDER_CLOSE_AMOUNT);
        sql.addColumn(FIELD_STORE_TENDER_MEDIA_CLOSE_COUNT);
        sql.addColumn(FIELD_STORE_FUNDS_RECEIVED_IN_MEDIA_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_FUNDS_RECEIVED_OUT_MEDIA_TOTAL_AMOUNT);
        sql.addColumn(FIELD_STORE_FUNDS_RECEIVED_IN_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_STORE_FUNDS_RECEIVED_OUT_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_RECONCILE_AMOUNT);
        sql.addColumn(FIELD_RECONCILE_MEDIA_UNIT_COUNT);
        /*
         * Add Qualifier(s)
         */
        // For the specified store only
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(store));
        // Join Store History and Reporting Period
        sql.addQualifier(ALIAS_STORE_TENDER_HISTORY + "." + FIELD_FISCAL_YEAR
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_YEAR);
        sql.addQualifier(ALIAS_STORE_TENDER_HISTORY + "." + FIELD_REPORTING_PERIOD_TYPE_CODE
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_TYPE_CODE);
        sql.addQualifier(ALIAS_STORE_TENDER_HISTORY + "." + FIELD_REPORTING_PERIOD_ID
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
                         + " = " + getBusinessDate(businessDate));

        FinancialTotalsIfc totals = instantiateFinancialTotals();

        try
        {
            dataConnection.execute(sql.getSQLString());
            String operatorID = null;
            ResultSet rs = (ResultSet)dataConnection.getResult();

            while (rs.next())
            {
                /*
                 * Grab the fields selected from the database
                 */
                int index = 0;
                String tenderCode = getSafeString(rs, ++index);
                String tenderSubType = spaceStringToEmptyString(getSafeString(rs, ++index));
                String currencyCode = getSafeString(rs, ++index);
                int currencyID = rs.getInt(++index); //I18N
                int tenderCount = rs.getInt(++index);
                int overCount = rs.getInt(++index);
                int shortCount = rs.getInt(++index);
                CurrencyIfc shortAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                CurrencyIfc overAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                //int depositCount = rs.getInt(++index);
                int loanCount = rs.getInt(++index);
                CurrencyIfc loanAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                int pickupCount = rs.getInt(++index);
                CurrencyIfc pickupAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                int openCount = rs.getInt(++index);
                //CurrencyIfc depositAmount = getCurrencyFromDecimal(rs, ++index);
                int refundCount = rs.getInt(++index);
                CurrencyIfc refundAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                CurrencyIfc tenderAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                CurrencyIfc openAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                CurrencyIfc closeAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                int closeCount = rs.getInt(++index);
                CurrencyIfc tillPayInsAmount            = getCurrencyFromDecimal(rs, ++index, currencyCode);
                CurrencyIfc tillPayOutsAmount           = getCurrencyFromDecimal(rs, ++index, currencyCode);
                int         tillPayInsCount             = rs.getInt(++index);
                int         tillPayOutsCount            = rs.getInt(++index);
                CurrencyIfc reconcileAmount             = getCurrencyFromDecimal(rs, ++index, currencyCode);
                int         reconcileCount              = rs.getInt(++index);

                TenderDescriptorIfc descriptor = DomainGateway.getFactory().getTenderDescriptorInstance();
                descriptor.setCountryCode(currencyCode);
                descriptor.setCurrencyID(currencyID); //I18N
                descriptor.setTenderSubType(tenderSubType);
                int tenderType = getTenderType(tenderCode);
                descriptor.setTenderType(tenderType);
                String tenderTypeDesc = tenderTypeMap.getDescriptor(tenderType);
                String summaryDesc = "";

                if (tenderType == TenderLineItemIfc.TENDER_TYPE_CHARGE)
                {
                    summaryDesc = tenderTypeDesc;
                    tenderTypeDesc = tenderSubType;
                }
                else // Assuming Charge is always in the local currency
                if (!currencyCode.equals(DomainGateway.getBaseCurrencyType().getCountryCode()))
                {
                    tenderTypeDesc = CountryCodeMap.getCountryDescriptor(currencyCode) + " " + tenderTypeDesc;
                }

                if (openAmount.signum() != CurrencyIfc.ZERO)
                {
                    FinancialCountIfc startFloat = totals.getStartingFloatCount().getEntered();
                    startFloat.addTenderItemIn(tenderType, currencyCode, openCount, openAmount);
                }

                if (closeAmount.signum() != CurrencyIfc.ZERO)
                {
                    FinancialCountIfc endFloat = totals.getEndingFloatCount().getEntered();
                    endFloat.addTenderItemOut(tenderType, currencyCode, closeCount, closeAmount);
                }

                if (tillPayInsCount != 0)
                {
                    ReconcilableCountIfc payIns = totals.instantiateReconcilableCountIfc();
                    payIns.getEntered().addTenderItemIn(tenderType, currencyCode, tillPayInsCount, tillPayInsAmount);
                    totals.addTillPayIns(payIns);
                }

                if (tillPayOutsCount != 0)
                {
                    ReconcilableCountIfc payOuts = totals.instantiateReconcilableCountIfc();
                    payOuts.getEntered().addTenderItemOut(tenderType, currencyCode, tillPayOutsCount, tillPayOutsAmount);
                    totals.addTillPayOuts(payOuts);
                }

                if (loanCount != 0)
                {
                    ReconcilableCountIfc loans = totals.instantiateReconcilableCountIfc();
                    loans.getEntered().addTenderItemIn(tenderType, currencyCode, loanCount, loanAmount);
                    totals.addTillLoans(loans);
                }

                if (pickupCount != 0)
                {
                    ReconcilableCountIfc pickups = totals.instantiateReconcilableCountIfc();
                    pickups.getEntered().addTenderItemOut(tenderType, currencyCode, pickupCount, pickupAmount);
                    totals.addTillPickups(pickups);
                    totals.addCountTillPickups(currencyCode, pickupCount);
                }

                /*
                 * Sale/Return Transaction information
                 */
                FinancialCountIfc tender = totals.getTenderCount();
                tender.addTenderItem(descriptor,
                                     tenderCount + refundCount,
                                     refundCount,
                                     tenderAmount.add(refundAmount),
                                     refundAmount,
                                     tenderTypeDesc,
                                     summaryDesc,
                                     false);
                /*
                 * Add the reconcile amount and count to the combined count.
                 */
                ReconcilableCountIfc combined = totals.getCombinedCount();
                combined.getEntered().addTenderItemIn(descriptor, reconcileCount, reconcileAmount);
        }

            rs.close();
        }
        catch (SQLException se)
        {
            throw new DataException(SQL_ERROR, "selectStoreTenderHistory", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(UNKNOWN, "selectStoreTenderHistory", e);
        }

        return(totals);
    }

    /**
       Returns a database safe string for the store id.
       <p>
       @param store The store to get the storeId of
       @return the store id
     */
    protected String getStoreID(StoreIfc store)
    {
        return("'" + store.getStoreID() + "'");
    }

    /**
       Returns the tender type
       <p>
       @param  tenderTypeCode  The type of tender
       @return the tender type
     */
    protected String getTenderTypeDesc(String tenderTypeCode)
    {
        return tenderTypeMap.getDescriptor(tenderTypeMap.getTypeFromCode(tenderTypeCode));
    }

    /**
       Returns the tender type
       <p>
       @param tenderTypeCode String {@link TenderLineItemIfc}
       @return int tender type
     */
    protected int getTenderType(String tenderTypeCode)
    {
        int value = tenderTypeMap.getTypeFromCode(tenderTypeCode);

        if (value == -1)
        {
            value = TenderLineItemIfc.TENDER_TYPE_CASH;
        }

        return value;
    }

    /**
       Returns a database safe string for the business date
       <p>
       @param businessDate The date object to get a string value for
       @return the business date
     */
    protected String getBusinessDate(EYSDate businessDate)
    {
        return(dateToSQLDateString(businessDate.dateValue()));
    }

    /**
       Instantiates a financial total object.
       <p>
       @return new FinancialTotalsIfc object
     */
    protected FinancialTotalsIfc instantiateFinancialTotals()
    {
        return(DomainGateway.getFactory().getFinancialTotalsInstance());
    }

    /**
       Retrieves the source-code-control system revision number. <P>
       @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }

}
