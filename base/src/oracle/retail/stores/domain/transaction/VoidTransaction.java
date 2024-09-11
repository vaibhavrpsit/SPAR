/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/VoidTransaction.java /main/53 2014/07/09 16:20:03 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    sgu    11/19/14 - Use negateItemQuantity for voiding line item
 *    sgu    07/08/14 - add new function to getTotablableLineItems
 *    jswan  06/20/14 - Modified to get the recommended items for all sale
 *                      return line items in the transaction.
 *    yiqzha 05/09/14 - Add method isOrderPickupOrCancel.
 *    cgreen 06/06/14 - set line number when adding a tender
 *    cgreen 10/25/13 - remove currency type deprecations and use currency code
 *                      instead of description
 *    yiqzha 09/13/13 - Reverse amounts and counts when running a post void
 *                      transaction.
 *    yiqzha 08/29/13 - Updated count as well.
 *    yiqzha 08/28/13 - For void transaction, amtIn subtract void amount,
 *                      amtOut is equal to zero. Counts are changed
 *                      accordingly.
 *    jswan  07/09/13 - Fixed issues saving the cash adjustment total to the
 *                      history tables for order, layaway, redeem and voided
 *                      transactions.
 *    subrde 06/27/13 - Modified code to print correct amount for post voided
 *                      special order cancel/pickup transactions
 *    jswan  06/25/13 - Modified to fix totals issues with post voiding a
 *                      return funded with store credit.
 *    jswan  05/07/13 - Modified to support sending voided order returns to the
 *                      cross channel order repository.
 *    vtemke 04/16/13 - Moved constants in OrderLineItemIfc to
 *                      OrderConstantsIfc in common project
 *    rgour  04/01/13 - CBR cleanup
 *    abhine 10/29/12 - fix for post void paidout transactions not updated on
 *                      pos/backoffice summary reports
 *    sgu    08/17/12 - refactor discount audit
 *    sgu    05/04/12 - refactor OrderStatus to support store order and
 *                      xchannel order
 *    yiqzha 04/16/12 - refactor store send from transaction totals
 *    yiqzha 04/03/12 - refactor store send for cross channel
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    sgu    09/22/11 - remove unused function
 *    sgu    09/22/11 - negate return tax in post void case
 *    jswan  09/16/11 - Reworked credited giftcard totals issues.
 *    jswan  08/22/11 - Fixes issues with gift card totals.
 *    kelesi 10/26/10 - Empty TaxInformation when post voiding transaction.
 *    rrkohl 10/12/10 - added comments
 *    rrkohl 10/12/10 - added fix to display correct tax in the reciept
 *    nkgaut 07/28/10 - Bill Payment Report changes
 *    dwfung 07/16/10 - Transaction voided with MailBankCheck should be reversed
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/28/10 - updating deprecated names
 *    acadar 03/11/10 - update the correct buckets when voiding a layaway
 *                      initiate or a pickup
 *    jswan  02/02/10 - Voids of picks put tender back in the drawer; there I
 *                      modified this action to create a TenderItemIn rather
 *                      than a TenderItemOut.
 *    abonda 01/03/10 - update header date
 *    cgreen 05/13/09 - implement getReversedTenders method in order to be able
 *                      to print all reversed tenders
 *    mjwall 03/26/09 - Don't calculate change due on the receipt for a voided
 *                      redeem transaction.
 *    cgreen 03/20/09 - keep kit components off receipts by implementing new
 *                      method getLineItemsExceptExclusions
 *    jswan  03/10/09 - Reimplemented changes for defect 8223906.
 *    jswan  02/16/09 - Fixed issue with Voided Gift Card Tender Totals.
 *    mdecam 11/07/08 - I18N - updated toString()
 *    mdecam 11/07/08 - I18N - Fixed Clone Method
 *    acadar 11/03/08 - transaction tax reason codes updates
 *    acadar 11/03/08 - localization of reason codes for discounts and merging
 *                      to tip
 *    acadar 10/30/08 - use localized reason codes for item and transaction
 *                      discounts
 *    akandr 10/30/08 - EJ changes
 *    acadar 10/27/08 - fix the broken unit tests
 *    acadar 10/24/08 - I18N changes for post void reason codes
 *    acadar 10/24/08 - localization of post void reason codes
 * ===========================================================================

     $Log:
      20   360Commerce 1.19        4/22/2008 4:16:56 PM   Maisa De Camargo CR
           28819 - Added fixes to update the financial totals for
           storeCreditsIssuedVoid only when voiding an StoreCredit Issue
           Transaction.
      19   360Commerce 1.18        9/20/2007 11:29:19 AM  Rohit Sachdeva
           28813: Initial Bulk Migration for Java 5 Source/Binary
           Compatibility of All Products
      18   360Commerce 1.17        5/23/2007 7:10:48 PM   Jack G. Swan    Fixed
            issues with tills and CurrencyID.
      17   360Commerce 1.16        5/22/2007 9:11:26 AM   Sandy Gu
           Enhance financial totals for VAT
      16   360Commerce 1.15        5/17/2007 5:13:07 PM   Owen D. Horne
           CR#8399 - Merged fix from v8.0.1
           13   .v8x       1.11.1.0    4/20/2007 6:08:03 AM   Sujay
           Purkayastha Fix
           for CR 8399

      15   360Commerce 1.14        5/14/2007 6:08:34 PM   Sandy Gu
           update inclusive information in financial totals and history tables
      14   360Commerce 1.13        5/1/2007 12:16:12 PM   Brett J. Larsen CR
           26474 - Tax Engine Enhancements for Shipping Carge Tax (for VAT
           feature)
      13   360Commerce 1.12        4/25/2007 10:00:17 AM  Anda D. Cadar   I18N
           merge
      12   360Commerce 1.11        10/20/2006 12:54:16 PM Charles D. Baker
           Revamped EOL behavior of transaction header for automated testing
           success.
      11   360Commerce 1.10        8/10/2006 11:17:44 AM  Brendan W. Farrell
           16500 - Remove Sales: xxx journaling because it has been moved up
           to base
           transaction class jouraling.
      10   360Commerce 1.9         8/7/2006 3:10:41 PM    Brendan W. Farrell
           Change fix from v7.x to meet coding standards.
      9    360Commerce 1.8         7/25/2006 10:51:29 AM  Tony Zgarba
           Missed a couple of imports when merging code for 18872.
      8    360Commerce 1.7         7/24/2006 8:33:57 PM   Tony Zgarba
           Merged changes from v7x for CR 18872.  Direct merge not possible
           due to inventory removal and UDM work.
      7    360Commerce 1.6         5/19/2006 2:26:28 PM   Brett J. Larsen CR
           17307 - inventory removal
           method getInventoryUpdate() was removed as part of inventory
           removal
           however, it included a call to setupReversal() (which is not
           inventory-specific)
           this change makes setupReversal() visible
      6    360Commerce 1.5         4/27/2006 7:29:50 PM   Brett J. Larsen CR
           17307 - remove inventory functionality - stage 2
      5    360Commerce 1.4         1/25/2006 4:11:55 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      4    360Commerce 1.3         1/22/2006 11:41:59 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:30:46 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:26:47 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:15:33 PM  Robert Pearse
     $:
      4    .v700     1.2.1.0     11/30/2005 17:24:59    Deepanshu       CR
           6261: Added Postvoid transaction reason code
      3    360Commerce1.2         3/31/2005 15:30:46     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:26:47     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:15:33     Robert Pearse
     $
     Revision 1.23.2.2  2004/11/17 16:21:30  jdeleau
     @scr 7739 Correct the way receipts are printing change on a post-void.

     Revision 1.23.2.1  2004/11/16 19:58:08  mweis
     @scr 7680 Use proper 'old' status when voiding an order.  Must also use (a different) proper 'old' status during filling, picking up, and canceling orders.

     Revision 1.23  2004/09/23 00:30:51  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.22  2004/09/17 15:49:59  jdeleau
     @scr 7146 Define a taxable transaction, for reporting purposes.

     Revision 1.21  2004/08/04 19:27:16  jdeleau
     @scr 6029 Make sure tax prints by tax rule for post-void transactions.

     Revision 1.20  2004/07/17 17:41:29  bwf
     @scr 6030 Copy over discount rate.

     Revision 1.19  2004/07/08 23:34:31  jdeleau
     @scr 6086 payroll till pay out on post void was crashing the system.  In fact it
     was not implemented at all.  Now its implemented just as normal till pay out.

     Revision 1.18  2004/06/14 14:39:42  lzhao
     @scr 4670: change the way to determine count.

     Revision 1.17  2004/06/11 23:25:40  cdb
     @scr 5559 Updated to record store credit issues (during returns) and voids to financial totals.

     Revision 1.16  2004/06/11 18:59:53  lzhao
     @scr 4670: Change the way to getCalculatedShippingCharge

     Revision 1.15  2004/06/04 18:57:49  crain
     @scr 5388 Voiding a gift card redeem does not increase the "out" in tender summary in register reports

     Revision 1.14  2004/06/02 00:28:03  crain
     @scr 5333 Void of a gift cert. redeem does not increase gift certificate "out" in tender summary

     Revision 1.13  2004/05/19 23:09:28  cdb
     @scr 5103 Updating to more correctly handle register reports.

     Revision 1.12  2004/05/19 18:33:31  cdb
     @scr 5103 Updating to more correctly handle register reports.

     Revision 1.11  2004/05/11 23:03:02  jdeleau
     @scr 4218 Backout recent changes to remove TransactionDiscounts,
     going to go a different route and remove the newly added
     voids and grosses instead.

     Revision 1.9  2004/05/11 14:08:28  tmorris
     @scr 4119 -Voided layaway payments were voiding entire layaway.

     Revision 1.8  2004/04/29 19:24:58  lzhao
     @scr 4553: Summary Report for redeem and redeem void.

     Revision 1.7  2004/03/22 17:26:41  blj
     @scr 3872 - added redeem security, receipt printing and saving redeem transactions.

     Revision 1.6  2004/03/02 23:14:06  cdb
     @scr 3588 Updated so item discount audits get employee
     ID for employee transaction discounts.

     Revision 1.5  2004/02/27 00:48:30  jdeleau
     @scr 0 The clone and equals methods were failing with NPE on
     voidTransaction objects created with the default constructor.
     This corrects those problems.

     Revision 1.4  2004/02/17 16:18:52  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:14:42  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:28:50  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.4   Dec 01 2003 13:46:16   bwf
 * Updated for echeck declines.
 *
 *    Rev 1.3   25 Nov 2003 22:52:32   baa
 * implement new methods on interface
 *
 *    Rev 1.2   Oct 27 2003 10:50:40   epd
 * Removed/deprecated logic that now resides in ADO layer
 *
 *    Rev 1.1   Oct 02 2003 10:46:02   baa
 * fix for rss defect 1406
 * Resolution for 3402: RSS  PRF 1406 Tender Information on Void transactions does not looks correct
 *
 *    Rev 1.0   Aug 29 2003 15:41:20   CSchellenger
 * Initial revision.
 *
 *    Rev 1.10   Jul 29 2003 17:17:52   bwf
 * Use TransactionTotals instead of TenderTransactionTotals if available.
 * Resolution for 3295: Voiding the pickup of an Order add the tax into the Sub Total line on receipt which incorrectly updates the Summary Reports
 *
 *    Rev 1.9   Jul 23 2003 11:05:54   jgs
 * Modified to defer reversal of item order state so that the inventory balance calculation has the original states to work with.
 * Resolution for 1648: Special Order - Voiding a sp.ord. pickup does not update inventory buckets properly
 *
 *    Rev 1.8   Jul 21 2003 12:03:14   DCobb
 * Convert till pickup foreign currency to base currency for post void. Removed calculation of the unused variable gross.
 * Resolution for POS SCR-3197: Voiding of Canadian currency pickups does not convert tender amount back to local currency
 *
 *    Rev 1.7   Jul 08 2003 14:35:16   DCobb
 * Allow the status of order transactions to revert to 'new'.
 * Resolution for POS SCR-2363: Complete Sp order & void pickup trans, the original sp order trans can't be void
 *
 *    Rev 1.6   Jun 03 2003 11:34:30   RSachdeva
 * Canadian Cash tender for Till Summary
 * Resolution for POS SCR-2619: Void a sale trans with canadian cash tender, the Net amount is not correct on summary report
 *
 *    Rev 1.5   May 15 2003 13:29:18   adc
 * Decrement the layaway delete count when a  layaway delete transaction  is voided
 * Resolution for 2356: delete layaway and then void it. layaway delete count is not correct on till summary
 *
 *    Rev 1.4   May 14 2003 14:55:42   adc
 * Changed the postVoidAmount
 * Resolution for 2353: Void sending transaction, Post Void Trans. amount is not correct on till summary report
 *
 *    Rev 1.3   Sep 03 2002 15:43:42   baa
 * Externalize domain constants
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   13 Jun 2002 19:57:24   vxs
 * Re-checkin with associated SCR, didn't work last time
 * Resolution for POS SCR-1669: Voiding Canadian Trav Check updates wrong bucket on Summary report
 *
 *    Rev 1.1   13 Jun 2002 18:57:38   vxs
 * Revamped getFinancialTotalsFromTender(), more in line with AbstractTenderableTransaction implementation.
 *
 *    Rev 1.0   Jun 03 2002 17:07:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Apr 28 2002 13:32:32   mpm
 * Completed translation of sale transactions.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   Mar 18 2002 12:30:42   msg
 * Initial revision.
 *
 *    Rev 1.23   12 Mar 2002 11:02:54   pjf
 * Use factory to get discount audit instances.
 * Resolution for POS SCR-1550: Use Factory to get new object instances in POS & Domain
 *
 *    Rev 1.22   08 Mar 2002 15:56:00   pdd
 * Removed non-tax sales impact of voiding House Acct Pmt.
 * Resolution for POS SCR-1534: House Account Payments are updating the Net Trans Nontaxable line
 *
 *    Rev 1.21   01 Mar 2002 15:17:40   pdd
 * Converted to use TenderTypeMapIfc for tender codes and descriptors.
 * Resolution for POS SCR-627: Make the Tender type list extendible.
 *
 *    Rev 1.20   28 Feb 2002 14:26:16   vxs
 * for case TYPE_HOUSE_PAYMENT, addCountGrossNonTaxableTransactionSales(1)
 * Resolution for POS SCR-1428: Voiding a House Acct Payment updates count incorrectly for Net Trans Nontaxable
 *
 *    Rev 1.19   21 Feb 2002 19:24:54   jbp
 * removed debugging
 * Resolution for POS SCR-1358: Voided Return with Store Credit as return tender.  Store Credit did not void.
 *
 *    Rev 1.18   21 Feb 2002 18:41:02   jbp
 * void store credit issues only.
 * Resolution for POS SCR-1358: Voided Return with Store Credit as return tender.  Store Credit did not void.
 *
 *    Rev 1.16   Feb 21 2002 16:37:26   dfh
 * added getorderfinancialtotals, updates for register reports
 * Resolution for POS SCR-1298: Completed Layaway Pickup does not update Total Item Sales count/amount on Summary Report
 *
 *    Rev 1.15   Feb 20 2002 21:05:44   dfh
 * don't include kit headers in line item financial totals
 * Resolution for POS SCR-1298: Completed Layaway Pickup does not update Total Item Sales count/amount on Summary Report
 *
 *    Rev 1.14   Feb 19 2002 11:30:50   dfh
 * updates to financial totals for layaway transactions: counters for
 * fees and payments
 * Resolution for POS SCR-1299: Completed Layaway Pickup does not update Net Item Sales count/amount on Summary Report
 *
 *    Rev 1.13   Feb 18 2002 17:50:34   dfh
 * added getlayawayfinancialtotals to reverse the layaway complete totals and counts
 * Resolution for POS SCR-1284: Layaway pickup does not update Sales Tax count/amount on Summary Report
 *
 *    Rev 1.12   Feb 11 2002 13:24:04   dfh
 * added getOrderLineItemsFinancialTotals to only include pick up items when voiding an order partial or complete for summary report
 * Resolution for POS SCR-1225: Summary Report requirements for Special Order has changed - vers 9
 *
 *    Rev 1.11   Feb 10 2002 21:53:42   dfh
 * updates to try to include orders in summary reports
 * Resolution for POS SCR-1225: Summary Report requirements for Special Order has changed - vers 9
 *
 *    Rev 1.10   Feb 05 2002 16:36:36   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.9   Feb 03 2002 14:01:38   mpm
 * Changes to support inventory movement in order transactions.
 * Resolution for Domain SCR-14: Special Order modifications
 *
 *    Rev 1.8   Feb 01 2002 12:02:46   dfh
 * include order partial trans in financial total calcs
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.7   28 Jan 2002 15:47:46   jbp
 * revert statuses for order and order line items.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.6   24 Jan 2002 15:38:56   epd
 * added logic to not add tender items to totals for TillAdjustmentTransactions
 * Resolution for POS SCR-150: Voiding till pickups/loans does not update in Trans Summary
 *
 *    Rev 1.5   15 Jan 2002 16:02:10   sfl
 * Fixed the not necessary shipping charge total number decrement during void transaction.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.4   02 Jan 2002 14:09:24   jbp
 * removed deprecated methods
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.3   17 Dec 2001 10:55:30   sfl
 * Subtract the shipping charge amount and count
 * when a transaction containing send items is being
 * voided.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.2   02 Dec 2001 12:48:14   mpm
 * Implemented financials, voids for special order domain objects.
 * Resolution for POS SCR-228: Merge VABC, Pier 1 changes
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   29 Oct 2001 08:33:22   mpm
 * Added inventory-movement methods.
 *
 *    Rev 1.0   Sep 20 2001 16:06:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:39:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.common.item.ExtendedItemData;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.ReturnItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItem;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tax.TaxInformationContainerIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.tender.ReversibleTenderIfc;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSStatusIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.StoreCreditIfc;

/**
 * A transaction that cancels a prior sales/return transaction.
 *
 * @see oracle.retail.stores.domain.transaction.Transaction
 * @see oracle.retail.stores.domain.transaction.VoidTransactionIfc
 */
public class VoidTransaction extends AbstractTenderableTransaction
    implements VoidTransactionIfc, RetailTransactionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 2269704080516447778L;

    /**
     * Voided Transaction
     */
    protected TenderableTransactionIfc originalTransaction;

    /**
     * Voided Transaction type
     */
    protected int originalTransactionType = TransactionIfc.TYPE_UNKNOWN;

    /**
     * Retail store ID
     */
    protected String originalRetailStoreID;

    /**
     * Workstation ID
     */
    protected String originalWorkstationID;

    /**
     * transaction sequence number
     */
    protected long originalTransactionSequenceNumber;

    /**
     * transaction sequence number
     */
    protected EYSDate originalBusinessDay;

    /**
     * Sale Line Items
     */
    protected Vector<AbstractTransactionLineItemIfc> lineItems = null;

    /**
     * Order ID
     */
    protected String orderID = null;

    /**
     * Reason Code
     *
     * @deprecated as of 13.1. Use {@link localizedReasonCode}
     */
    protected String reasonCode = null;

    /**
     * Reason Code Text
     *
     * @deprecated as of 13.1. Use {@link localizedReasonCode.getText()}
     */
    protected String reasonCodeText = null;

    /**
     * Localized reason code
     */
    protected LocalizedCodeIfc reason = DomainGateway.getFactory().getLocalizedCode();

    /**
     * Send package line item for each send
     */
    protected Vector<SendPackageLineItemIfc> sendPackages = null;    
 
    /**
     * Currency type for Transaction
     */
    protected CurrencyTypeIfc currencyType; 
    
    /**
     * Country Code for the transaction
     */    
    private String transactionCountryCode;

    /**
     * Constructs VoidTransaction object.
     */
    public VoidTransaction()
    {
        initialize();
    }

    /**
     * Constructs a new, usable VoidTransaction object.
     *
     * @param station workstation
     * @deprecated Deprecated in 4.5.0. Use
     *             DomainGateway.getFactory().getVoidTransactionInstance().
     */
    public VoidTransaction(WorkstationIfc station)
    {
        initialize(station);
        initialize();
    }

    /**
     * Initializes object.
     */
    protected void initialize()
    {
        super.initialize();
        transactionType = TransactionIfc.TYPE_VOID;
        originalTransaction = null;
        originalRetailStoreID = null;
        originalWorkstationID = null;
        originalTransactionSequenceNumber = -1;
        originalBusinessDay = null;
    }

    /**
     * Clones VoidTransaction object
     *
     * @return instance of VoidTransaction object
     */
    public Object clone()
    {
        // instantiate new object
        VoidTransaction vt = new VoidTransaction();

        setCloneAttributes(vt);

        // pass back object
        return (vt);
    }

    /**
     * Sets attributes for clone.
     *
     * @param newClass new instance of VoidTransaction
     */
    protected void setCloneAttributes(VoidTransaction newClass)
    {
        super.setCloneAttributes(newClass);

        if (originalTransaction != null)
        {
            newClass.setOriginalTransaction((TenderableTransactionIfc) (originalTransaction.clone()));
        }
        newClass.setOriginalRetailStoreID(originalRetailStoreID);
        newClass.setOriginalWorkstationID(originalWorkstationID);
        newClass.setOriginalTransactionSequenceNumber(originalTransactionSequenceNumber);
        if(originalBusinessDay != null)
            newClass.setOriginalBusinessDay((EYSDate) originalBusinessDay.clone());
        newClass.setOrderID(orderID);
        if (reason != null)
        {
            newClass.setReason((LocalizedCodeIfc) reason.clone());
        }

    }

    /**
     * calculates the change due.
     *
     * @return changeDue as CurrencyIfc
     */
    public CurrencyIfc calculateChangeDue()
    {
        // gets the origional layaway payment transaction
        CurrencyIfc changeDue = DomainGateway.getBaseCurrencyInstance();
        // gets the current transaction type
        int transType = getTransactionType();

        // if we are in a return then the negative cash is not change it is a
        // refund
        // If voiding a redeem transaction, it is also a refund, not change.
        if (transType != TransactionConstantsIfc.TYPE_RETURN
                && transType != TransactionConstantsIfc.TYPE_ORDER_CANCEL
                && transType != TransactionConstantsIfc.TYPE_REDEEM
                && transType != TransactionConstantsIfc.TYPE_LAYAWAY_DELETE
                && !(transType == TransactionConstantsIfc.TYPE_VOID && (getOriginalTransactionType() == TransactionConstantsIfc.TYPE_REDEEM)))
        {
            // calculate sum of negative cash tenders
            TenderLineItemIfc[] cashTenders = getTenderLineItemArray(TenderLineItemIfc.TENDER_TYPE_CASH);
            CurrencyIfc result = DomainGateway.getBaseCurrencyInstance();
            for (int i = 0; cashTenders != null && i < cashTenders.length; i++)
            {
                if (cashTenders[i].getAmountTender().signum() == CurrencyIfc.POSITIVE)
                {
                    result = result.add(cashTenders[i].getAmountTender());
                }
            }
            changeDue = changeDue.add(result);
        }
        return (changeDue);
    }

    /**
     * Calculates FinancialTotals based on current transaction.
     *
     * @return FinancialTotalsIfc object
     */
    public FinancialTotalsIfc getFinancialTotals()
    {
        FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();
        TransactionTotalsIfc tenderTransactionTotals = getTenderTransactionTotals();

        // temporary variables used for calculations
        CurrencyIfc paymentAmountLessFees = null;
        CurrencyIfc paymentAmount = null;
        LayawayTransactionIfc layawayTransaction = null;
        PaymentTransactionIfc housePaymentTransaction = null;
        OrderTransactionIfc orderTransaction = null;
        TillAdjustmentTransactionIfc adjustmentTransaction = null;
        BillPayTransactionIfc billPaymentTransaction = null;
        CurrencyIfc billPaymentAmount = null;
        ReconcilableCountIfc[] rCountArray = null;
        ReconcilableCountIfc rCount = null;
        FinancialCountIfc fCount = null;
        TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
        String tenderType = null;

        // This flag allows us to skip getting the tender items for transaction types that
        // have not gone through the tender service
        boolean getTenderTotals = true;

        switch(originalTransactionType)
        {
            case TYPE_SALE:
            case TYPE_RETURN:
            case TYPE_EXCHANGE:
                getSaleReturnFinancialTotals(financialTotals);
                // get line items financial totals
                getLineItemsFinancialTotals(financialTotals);
                break;
            case TYPE_HOUSE_PAYMENT:
                // reverse the payment and count of house payment
                housePaymentTransaction = (PaymentTransaction)getOriginalTransaction();
                paymentAmount = housePaymentTransaction.getPaymentAmount();
                financialTotals.addAmountHousePayments(paymentAmount.negate());
                financialTotals.addCountHousePayments(-1);
                break;
            case TYPE_LAYAWAY_COMPLETE:
                // reverse the effect of original transaction
                layawayTransaction = (LayawayTransactionIfc) getOriginalTransaction();
                paymentAmount = layawayTransaction.getPayment().getPaymentAmount();
                financialTotals.addAmountLayawayPickup(paymentAmount.negate());
                // get layaway financial totals
                getLayawayFinancialTotals(financialTotals, layawayTransaction);

                break;
            case TYPE_LAYAWAY_DELETE:
                // reverse the effect of original transaction
                layawayTransaction = (LayawayTransactionIfc) getOriginalTransaction();
                // put fees back in (to figure correct payment)
                paymentAmountLessFees = layawayTransaction.getPayment().getPaymentAmount();
                // add the payment, less the fee
                financialTotals.addAmountLayawayDeletions(paymentAmountLessFees.negate());
                financialTotals.addCountLayawayDeletions(-1);
                // add the fee
                financialTotals.addAmountLayawayDeletionFees(layawayTransaction.getLayaway().getDeletionFee().negate());
                financialTotals.addCountLayawayDeletionFees(-1);
                break;
            case TYPE_LAYAWAY_PAYMENT:
                // reverse the effect of the payment transaction
                LayawayPaymentTransactionIfc paymentTransaction =
                    (LayawayPaymentTransactionIfc) getOriginalTransaction();
                paymentAmountLessFees = paymentTransaction.getPayment().getPaymentAmount();
                financialTotals.addAmountLayawayPayments(paymentAmountLessFees.negate());
                financialTotals.addCountLayawayPayments(1);
                break;
            case TYPE_LAYAWAY_INITIATE:
                // reverse the effect of original transaction
                layawayTransaction = (LayawayTransactionIfc) getOriginalTransaction();

                // revert the payment new
                financialTotals.addAmountLayawayNew(layawayTransaction.getPayment().getPaymentAmount().negate());

                // add the fee
                financialTotals.addAmountLayawayInitiationFees(
                    layawayTransaction.getLayaway().getCreationFee().negate());
                financialTotals.addCountLayawayInitiationFees(-1);
                break;
            case TransactionIfc.TYPE_ORDER_INITIATE:
                orderTransaction = (OrderTransactionIfc) getOriginalTransaction();

                // do not record totals on suspended transaction
                if (orderTransaction.getTransactionStatus() !=
                     TransactionIfc.STATUS_SUSPENDED)
                {
                    financialTotals.addAmountOrderPayments
                      (orderTransaction.getPayment().getPaymentAmount().negate());
                    financialTotals.addCountOrderPayments(-1);
                }
                break;
            case TransactionIfc.TYPE_ORDER_COMPLETE:
            case TransactionIfc.TYPE_ORDER_PARTIAL:
                orderTransaction = (OrderTransactionIfc) getOriginalTransaction();
                // get order transaction financial totals
                getOrderFinancialTotals(financialTotals,orderTransaction);
                // get order line items financial totals
                getOrderLineItemsFinancialTotals(financialTotals);
                financialTotals.addAmountSpecialOrderPartial(orderTransaction.getPayment().getPaymentAmount().negate());
                break;
            case TransactionIfc.TYPE_ORDER_CANCEL:
                orderTransaction = (OrderTransactionIfc) getOriginalTransaction();
                financialTotals.addAmountOrderCancels
                  (orderTransaction.getPayment().getPaymentAmount().negate());
                financialTotals.addCountOrderCancels(-1);
                break;
            case TYPE_PAYIN_TILL:
                getTenderTotals = false;
                adjustmentTransaction = (TillAdjustmentTransactionIfc) originalTransaction;
                rCountArray = new ReconcilableCountIfc[adjustmentTransaction.getAdjustmentCount()];
                rCount = DomainGateway.getFactory().getReconcilableCountInstance();
                fCount = rCount.getEntered();
                tenderType = tenderTypeMap.getDescriptor(adjustmentTransaction.getTender().getTenderType());
                fCount.addTenderItemOut(tenderType,
                                        1,
                                        adjustmentTransaction.getAdjustmentAmount().abs());
                rCountArray[0] = rCount;
                financialTotals.addTillPayIns(rCountArray);
                break;
            case TYPE_PAYOUT_TILL:
            case TYPE_PAYROLL_PAYOUT_TILL:
                getTenderTotals = false;
                adjustmentTransaction = (TillAdjustmentTransactionIfc) originalTransaction;
                rCountArray = new ReconcilableCountIfc[adjustmentTransaction.getAdjustmentCount()];
                rCount = DomainGateway.getFactory().getReconcilableCountInstance();
                fCount = rCount.getEntered();
                tenderType = tenderTypeMap.getDescriptor(adjustmentTransaction.getTender().getTenderType());
                fCount.addTenderItemOut(tenderType,
                                       1,
                                       adjustmentTransaction.getAdjustmentAmount().abs());
                rCountArray[0] = rCount.negate();
                financialTotals.addTillPayOuts(rCountArray);
                break;
            case TYPE_PICKUP_TILL:
                getTenderTotals = false;
                adjustmentTransaction = (TillAdjustmentTransactionIfc) originalTransaction;
                rCountArray = new ReconcilableCountIfc[adjustmentTransaction.getAdjustmentCount()];
                rCount = DomainGateway.getFactory().getReconcilableCountInstance();
                fCount = rCount.getEntered();
                tenderType = tenderTypeMap.getDescriptor(adjustmentTransaction.getTender().getTenderType());
                fCount.addTenderItemIn(tenderType,
                                       1,
                                       adjustmentTransaction.getAdjustmentAmount().abs());
                rCountArray[0] = rCount;
                financialTotals.addTillPickups(rCountArray);
                break;
            case TYPE_LOAN_TILL:
                getTenderTotals = false;
                adjustmentTransaction = (TillAdjustmentTransactionIfc) originalTransaction;
                rCountArray = new ReconcilableCountIfc[adjustmentTransaction.getAdjustmentCount()];
                rCount = DomainGateway.getFactory().getReconcilableCountInstance();
                fCount = rCount.getEntered();
                tenderType = tenderTypeMap.getDescriptor(adjustmentTransaction.getTender().getTenderType());
                fCount.addTenderItemOut(tenderType,
                                        1,
                                        adjustmentTransaction.getAdjustmentAmount().abs());
                rCountArray[0] = rCount;
                financialTotals.addTillLoans(rCountArray);
                break;
            case TYPE_BILL_PAY:
                billPaymentTransaction = (BillPayTransactionIfc) originalTransaction;
                billPaymentAmount = billPaymentTransaction.getTenderTransactionTotals().getGrandTotal();
                financialTotals.addAmountBillPayments(billPaymentAmount.negate());
                financialTotals.addCountBillPayments(-1);
                financialTotals.addAmountGrossNonTaxableTransactionSales(billPaymentAmount.negate());
                financialTotals.addCountGrossNonTaxableTransactionSales(-1);
                break;
            default:
                break;
        }

        // the post void amount is the total tender amount
        CurrencyIfc transactionGrandTotal = tenderTransactionTotals.getGrandTotal().abs();
        if (originalTransactionType == TYPE_PICKUP_TILL)
        {
            if (!transactionGrandTotal.getCountryCode().equals(
                    DomainGateway.getBaseCurrencyInstance().getCountryCode()))
            {
                // Convert foreign currency to base currency amount.
                transactionGrandTotal = DomainGateway.convertToBase(transactionGrandTotal);
            }
        }
        financialTotals.addAmountPostVoids(transactionGrandTotal);
        financialTotals.addNumberPostVoids(1);

        // Add the rounded (cash) change amount to the financial totals object.
        TransactionTotalsIfc transactionTotalsCCRA = originalTransaction.getTenderTransactionTotals();
        if (transactionTotalsCCRA.getCashChangeRoundingAdjustment().signum() == CurrencyIfc.NEGATIVE)
        {
            financialTotals.addAmountChangeRoundedOut(transactionTotalsCCRA.getCashChangeRoundingAdjustment());
        }
        if (transactionTotalsCCRA.getCashChangeRoundingAdjustment().signum() == CurrencyIfc.POSITIVE)
        {
            financialTotals.addAmountChangeRoundedIn(transactionTotalsCCRA.getCashChangeRoundingAdjustment().negate());
        }

        // get tender financial totals
        if (getTenderTotals == true)
        {
            financialTotals = getTenderFinancialTotals(financialTotals);
        }

        return(financialTotals);
    }

    /**
     * Adds sale/return transaction financial totals to financial totals object.
     *
     * @param financialTotals FinancialTotalsIfc object
     */
    protected void getSaleReturnFinancialTotals(FinancialTotalsIfc financialTotals)
    {
        // Back out the sales totals and taxes
        CurrencyIfc tax = totals.getTaxTotal();
        CurrencyIfc inclusiveTax = totals.getInclusiveTaxTotal();
        CurrencyIfc gross = totals.getSubtotal().subtract(totals.getDiscountTotal());

        // Back out shipping tax since shipping tax are tracked separately in financial totals.
        tax = tax.subtract(financialTotals.getAmountTaxShippingCharges());
        inclusiveTax = inclusiveTax.subtract(financialTotals.getAmountInclusiveTaxShippingCharges());

        if ((tax != null) && (inclusiveTax != null))
        {
            if (getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
            {
                if (originalTransactionType == TransactionIfc.TYPE_SALE
                    || originalTransactionType == TransactionIfc.TYPE_EXCHANGE)
                {
                    financialTotals.addAmountGrossTaxExemptTransactionSalesVoided(gross.abs());
                    financialTotals.addCountGrossTaxExemptTransactionSalesVoided(1);
                }
                else if (originalTransactionType == TransactionIfc.TYPE_RETURN)
                {
                    financialTotals.addAmountGrossTaxExemptTransactionReturnsVoided(gross.abs());
                    financialTotals.addCountGrossTaxExemptTransactionReturnsVoided(1);
                }
            }
            // Tax exempt is being handled as non-taxable for now
            if (isTaxableTransaction())
            {
                if (originalTransactionType == TransactionIfc.TYPE_SALE
                    || originalTransactionType == TransactionIfc.TYPE_EXCHANGE)
                {
                    financialTotals.addAmountGrossTaxableTransactionSalesVoided(gross.abs());
                    financialTotals.addCountGrossTaxableTransactionSalesVoided(1);
                    financialTotals.addAmountTaxTransactionSales(tax);
                    financialTotals.addAmountInclusiveTaxTransactionSales(inclusiveTax);
                }
                else if (originalTransactionType == TransactionIfc.TYPE_RETURN)
                {
                    financialTotals.addAmountGrossTaxableTransactionReturnsVoided(gross.abs());
                    financialTotals.addCountGrossTaxableTransactionReturnsVoided(1);
                    financialTotals.addAmountTaxTransactionReturns(tax.abs().negate());
                    financialTotals.addAmountInclusiveTaxTransactionReturns(inclusiveTax.abs().negate());
                }
            }
            else
            {
                if (originalTransactionType == TransactionIfc.TYPE_SALE
                    || originalTransactionType == TransactionIfc.TYPE_EXCHANGE)
                {
                    financialTotals.addAmountGrossNonTaxableTransactionSalesVoided(gross.abs());
                    financialTotals.addCountGrossNonTaxableTransactionSalesVoided(1);
                }
                else if (originalTransactionType == TransactionIfc.TYPE_RETURN)
                {
                    financialTotals.addAmountGrossNonTaxableTransactionReturnsVoided(gross.abs());
                    financialTotals.addCountGrossNonTaxableTransactionReturnsVoided(1);
                }
            }
        }

        SaleReturnTransactionIfc srt = (SaleReturnTransactionIfc) originalTransaction;
        TransactionDiscountStrategyIfc[] discounts =
          srt.getTransactionDiscounts();
        if (discounts != null)
        {
            for (int x = 0; x < discounts.length; x++)
            {
                if (discounts[x].getAssignmentBasis() == DiscountRuleIfc.ASSIGNMENT_EMPLOYEE)
                {
                    financialTotals.addUnitsGrossTransactionEmployeeDiscount(new BigDecimal(-1));
                }
                else
                {
                    financialTotals.addNumberTransactionDiscounts(-1);
                }
            }
        }
    }

    /**
     * Derives the additive financial totals for an order complete transaction,
     * not including line items and tenders .
     *
     * @param financialTotals FinancialTotalsIfc object
     * @param OrderTransactionIfc orderTransaction object
     */
    protected void getOrderFinancialTotals(FinancialTotalsIfc financialTotals,
                                                         OrderTransactionIfc orderTransaction)
    {
        CurrencyIfc tax = totals.getTaxTotal();
        CurrencyIfc inclusiveTax = totals.getInclusiveTaxTotal();
        AbstractTransactionLineItemIfc[] items = getLineItems();
        for (int i = 0; i < items.length; i++)
        {
            SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)items[i];

            // already reverted the status to pick up
            if (item.getOrderItemStatus().getStatus().getStatus() ==
                OrderConstantsIfc.ORDER_ITEM_STATUS_PICK_UP)
            {
                tax = tax.add(item.getItemTaxAmount());
                inclusiveTax = inclusiveTax.add(item.getItemInclusiveTaxAmount());
            }
        }

        // gross total is payment minus pickedup item tax amount
        CurrencyIfc gross  = orderTransaction.getPayment().getPaymentAmount().add(tax);

        if ((tax != null) && (inclusiveTax != null))
        {
            if (getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
            {
                financialTotals.addAmountGrossTaxExemptTransactionSalesVoided(gross.abs());
                financialTotals.addCountGrossTaxExemptTransactionSalesVoided(1);
            }
            // if tax is positive value, add to taxable
            // Note:  tax exempt stuff is counted as non-taxable for now
            // TEC need to see if tax is zero to handle returns
            if(isTaxableTransaction())
            {
                financialTotals.addAmountGrossTaxableTransactionSalesVoided(gross.abs());
                financialTotals.addCountGrossTaxableTransactionSalesVoided(1);
                financialTotals.addAmountTaxTransactionSales(tax);
                financialTotals.addAmountInclusiveTaxTransactionSales(inclusiveTax);
            }
            else
            {
                financialTotals.addAmountGrossNonTaxableTransactionSalesVoided(gross.abs());
                financialTotals.addCountGrossNonTaxableTransactionSalesVoided(1);
            }

        }
        SaleReturnTransactionIfc srt = (SaleReturnTransactionIfc) originalTransaction;
        TransactionDiscountStrategyIfc[] discounts =
          srt.getTransactionDiscounts();
        if (discounts != null)
        {
            for (int x = 0; x < discounts.length; x++)
            {
                if (discounts[x].getAssignmentBasis() == DiscountRuleIfc.ASSIGNMENT_EMPLOYEE)
                {
                    financialTotals.addUnitsGrossTransactionEmployeeDiscount(new BigDecimal(-1));
                }
                else
                {
                    financialTotals.addNumberTransactionDiscounts(-1);
                }
            }
        }
    }

    /**
     * Adds line items financial totals to financial totals.
     *
     * @param financialTotals FinancialTotalsIfc object
     */
    protected void getLineItemsFinancialTotals(FinancialTotalsIfc financialTotals)
    {
        // loop through line items
        Enumeration<AbstractTransactionLineItemIfc> enumer = getLineItemsVector().elements();
        ArrayList<String> visited = new ArrayList<String>();
        while (enumer.hasMoreElements())
        {
            // get references to line item, price, tax objects
            AbstractTransactionLineItemIfc li = enumer.nextElement();
            if (li instanceof SaleReturnLineItemIfc &&
                !((SaleReturnLineItemIfc)li).isKitHeader() )
            {
                // isSale is false
                financialTotals.add(li.getFinancialTotals(false));
                financialTotals.add(getFinancialTotalsBestDealDiscounts((SaleReturnLineItemIfc)li, visited));
            }
        }
    }

    /**
     * Derives the additive financial totals for order line items.
     *
     * @return additive financial totals for line items
     */
    protected void getOrderLineItemsFinancialTotals(FinancialTotalsIfc financialTotals)
    {
        // loop through line items
        Enumeration<AbstractTransactionLineItemIfc> enumer = getLineItemsVector().elements();
        ArrayList<String> visited = new ArrayList<String>();

        while (enumer.hasMoreElements())
        {
            // get references to line item, price, tax objects
            AbstractTransactionLineItemIfc li = enumer.nextElement();
            SaleReturnLineItemIfc sli = (SaleReturnLineItemIfc)li;
            // check it order item was a pickup item
            if (sli.getOrderItemStatus().getStatus().getStatus() ==
                    OrderConstantsIfc.ORDER_ITEM_STATUS_PICK_UP)
            {
                financialTotals.add(li.getFinancialTotals(false));
                financialTotals.add(getFinancialTotalsBestDealDiscounts(sli, visited));
            }
        }
    }

    /**
     * Returns FinancialTotals object for the Store Coupons on this void.
     * ASSUMPTION: There is only one instance of a particular StoreCoupon on
     * this transaction. (ie. If a StoreCoupon is applied to more than one
     * LineItem, the StoreCoupon is counted only once.)
     *
     * @param financialTotals FinancialTotalsIfc object
     */
    protected FinancialTotalsIfc getFinancialTotalsBestDealDiscounts(SaleReturnLineItemIfc lineItem,
            ArrayList<String> visited)
    {
        FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();
        ItemDiscountStrategyIfc discount = null;
        discount = lineItem.getItemPrice().getBestDealDiscount();

        if (discount != null)
        {
            switch (discount.getReferenceIDCode())
            {
                case DiscountRuleConstantsIfc.REFERENCE_ID_CODE_STORE_COUPON:
                {
                    switch (discount.getDiscountScope())
                    {
                        case DiscountRuleConstantsIfc.DISCOUNT_SCOPE_TRANSACTION :
                        {
                            // Assumption:  Store Coupon used only once and added only once
                            // if ruleID not in list of visited rules then add 1 to count
                            // and add rule to visited.
                            if (!visited.contains(discount.getRuleID()))
                            {
                                financialTotals.addNumberTransactionDiscStoreCoupons(1);
                                visited.add(discount.getRuleID());
                            }
                            switch ( discount.getDiscountMethod() )
                            {
                                case DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE :
                                {
                                    financialTotals.addAmountTransactionDiscStoreCoupons(((lineItem.getExtendedSellingPrice().negate()).multiply(discount.getDiscountRate())).negate());
                                    break;
                                }
                                case DiscountRuleConstantsIfc.DISCOUNT_METHOD_FIXED_PRICE :
                                case DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT :
                                default:
                                {
                                    financialTotals.addAmountTransactionDiscStoreCoupons(discount.getDiscountAmount().negate());
                                    break;
                                }
                            }
                            break;
                        }
                        case DiscountRuleConstantsIfc.DISCOUNT_SCOPE_ITEM :
                        case DiscountRuleConstantsIfc.DISCOUNT_SCOPE_GROUP :
                        {
                            // Assumption:  Store Coupon used only once and added only once
                            // if ruleID not in list of visited rules then add 1 to count
                            // and add rule to visited.
                            if (!visited.contains(discount.getRuleID()))
                            {
                                financialTotals.addNumberItemDiscStoreCoupons(1);
                                visited.add(discount.getRuleID());
                            }
                            switch ( discount.getDiscountMethod() )
                            {
                                case DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE :
                                {
                                    financialTotals.addAmountItemDiscStoreCoupons(((lineItem.getExtendedSellingPrice().negate()).multiply(discount.getDiscountRate())).negate());
                                    break;
                                }
                                case DiscountRuleConstantsIfc.DISCOUNT_METHOD_FIXED_PRICE :
                                case DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT :
                                default:
                                {
                                    financialTotals.addAmountItemDiscStoreCoupons(discount.getDiscountAmount().negate());
                                    break;
                                }
                            }
                            break;
                        }
                        default:
                        {
                            break;
                        }
                    }
                    break;
                }
                default:
                {
                    break;
                }
            }
        }
        return financialTotals;
    }

    /**
     * Adds tender financial totals to financial totals object.
     *
     * @param financialTotals FinancialTotalsIfc object
     */
    protected FinancialTotalsIfc getTenderFinancialTotals(FinancialTotalsIfc financialTotals)
    {
        TenderLineItemIfc tli;
        // set up enumeration
        Enumeration<TenderLineItemIfc> enumer = originalTransaction.getTenderLineItemsVector().elements();

        // if elements exist, loop through them
        while (enumer.hasMoreElements())
        {
            tli = enumer.nextElement();
            financialTotals = financialTotals.add(getFinancialTotalsFromTender(tli));
        }

        // The customer was given change on the original transaction
        // Note: POSITIVE signum is > 0
        if (totals.getBalanceDue().signum() == CurrencyIfc.POSITIVE)
        {
            CurrencyIfc amtIn = totals.getBalanceDue();
            CurrencyIfc amtOut = DomainGateway.getBaseCurrencyInstance();
            TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
            financialTotals.getTenderCount().addTenderItem(
                tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH),
                1, 0, amtIn, amtOut);
        }

        if ( originalTransaction instanceof RedeemTransactionIfc )
        {
            RedeemTransactionIfc redeemTransaction = (RedeemTransactionIfc)originalTransaction;
            TenderLineItemIfc redeemTender = redeemTransaction.getRedeemTender();
            TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
            CurrencyIfc amount = redeemTender.getAmountTender();
            String countryCode = amount.getCountryCode();
            CurrencyIfc zeroAmount = DomainGateway.getCurrencyInstance(countryCode);
            TenderDescriptorIfc descriptor = DomainGateway.getFactory().getTenderDescriptorInstance();
            descriptor.setTenderType(redeemTender.getTypeCode());
            descriptor.setCountryCode(countryCode);
            descriptor.setCurrencyID(amount.getType().getCurrencyId());
            financialTotals.getTenderCount().addTenderItem(
                descriptor,
                0,
                1,
                zeroAmount,
                amount,
                tenderTypeMap.getDescriptor(redeemTender.getTypeCode()),
                null,
                false);

            if ( redeemTender instanceof TenderStoreCreditIfc )
            {
                TenderStoreCreditIfc tenderStoreCredit = (TenderStoreCreditIfc)redeemTender;
                StoreCreditIfc storeCredit = tenderStoreCredit.getStoreCredit();
                CurrencyIfc storeCreditVoidAmount = storeCredit.getAmount();
                if ( tenderStoreCredit.getState().equals( TenderStoreCreditIfc.ISSUE ) )
                {
                    financialTotals.addAmountGrossStoreCreditsRedeemedVoided(storeCreditVoidAmount);
                    financialTotals.addUnitsGrossStoreCreditsRedeemedVoided(new BigDecimal(1.0));
                }
                else if ( tenderStoreCredit.getState().equals( TenderStoreCreditIfc.REDEEM ) )
                {
                    financialTotals.addAmountGrossStoreCreditsRedeemedVoided(storeCreditVoidAmount);
                    financialTotals.addUnitsGrossStoreCreditsRedeemedVoided(new BigDecimal(1.0));
                }
                voidStoreCredit(redeemTender);
            }
            else if ( redeemTender instanceof TenderGiftCardIfc )
            {
                TenderGiftCardIfc tenderGiftCard = (TenderGiftCardIfc)redeemTender;
                CurrencyIfc giftCardVoidAmount = tenderGiftCard.getAmountTender();
                GiftCardIfc giftCard = tenderGiftCard.getGiftCard();
                if ( giftCard != null )
                {
                    if ( giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_ISSUE )
                    {
                        financialTotals.addAmountGrossGiftCardItemIssueVoided(giftCardVoidAmount);
                        financialTotals.addUnitsGrossGiftCardItemIssueVoided(new BigDecimal(1.0));
                    }
                    else if ( giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_RELOAD )
                    {
                        financialTotals.addAmountGrossGiftCardItemReloadVoided(giftCardVoidAmount);
                        financialTotals.addUnitsGrossGiftCardItemReloadVoided(new BigDecimal(1.0));
                    }
                    else if ( giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_REDEEM )
                    {
                        financialTotals.addAmountGrossGiftCardItemRedeemedVoided(giftCardVoidAmount);
                        financialTotals.addUnitsGrossGiftCardItemRedeemedVoided(new BigDecimal(1.0));
                    }
                }
            }
            else if ( redeemTender instanceof TenderGiftCertificateIfc )
            {
                TenderGiftCertificateIfc tenderGiftCertificate = (TenderGiftCertificateIfc) redeemTender;
                CurrencyIfc giftCertificateAmount = tenderGiftCertificate.getAmountTender();
                if ( tenderGiftCertificate.getState().equals(TenderGiftCertificateIfc.REDEEMED) )
                {
                    financialTotals.addAmountGrossGiftCertificatesRedeemedVoided(giftCertificateAmount);
                    financialTotals.addUnitsGrossGiftCertificatesRedeemedVoided(new BigDecimal(1.0));
                }
                else
                {
                    financialTotals.addAmountGrossGiftCertificateIssuedVoided(giftCertificateAmount);
                    financialTotals.addUnitsGrossGiftCertificateIssuedVoided(new BigDecimal(1.0));
                }
            }
        }
        return financialTotals;
    }

    /**
     * Void the store credit that was issued with the transaction.
     *
     * @param tli TenderLineItemIfc entry
     */
    protected void voidStoreCredit(TenderLineItemIfc tli)
    {
        TenderStoreCreditIfc storeCredit = (TenderStoreCreditIfc)tli;
        if(TenderStoreCreditIfc.ISSUE.equals(storeCredit.getState()))
        {
            storeCredit.getStoreCredit().setStatus(StoreCreditIfc.VOIDED);
        }
    }

    /**
     * Derive the additive financial totals from a given tender line item.
     *
     * @param tli TenderLineItemIfc entry
     * @return financialTotals financial totals to be added to transaction's
     *         totals
     */
    public FinancialTotalsIfc getFinancialTotalsFromTender(TenderLineItemIfc tli)
    {
        FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();

        CurrencyIfc amtIn  = null;
        CurrencyIfc amtOut = null;
        int countIn = 0;
        int countOut = 0;
        TenderDescriptorIfc descriptor = DomainGateway.getFactory().getTenderDescriptorInstance();
        String desc  = tli.getTypeDescriptorString();

        // add individual charge card totals to the financial total
        String sDesc = null;
        if (tli.getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CHARGE)
        {
            desc  = ((TenderChargeIfc) tli).getCardType();
            sDesc = tli.getTypeDescriptorString();
            descriptor.setTenderSubType(desc);
        }

        CurrencyIfc zero = DomainGateway.getBaseCurrencyInstance();
        zero.setZero();
        if ( tli.getAmountTender().compareTo(zero) > 0 )
        {
            //post void the sale item
            amtIn = tli.getAmountTender().negate();
            amtOut = zero;
            countIn = -1;
            countOut = 0;
        }
        else
        {
            //postvoid the return item
            amtIn = zero;
            amtOut = tli.getAmountTender();
            countIn = 0;
            countOut = -1;
        }
        
        // check if alternate tender and override amounts, if necessary
        if (tli instanceof TenderAlternateCurrencyIfc)
        {
            // cast to alternate currency ifc
            TenderAlternateCurrencyIfc alternate = (TenderAlternateCurrencyIfc) tli;
            CurrencyIfc alternateTender = alternate.getAlternateCurrencyTendered();
            // if no alternate currency, handle as base
            if (alternateTender != null)
            {
                // set description to include nationality
                desc = alternateTender.getCurrencyCode() + "_" + desc;
                if ( tli.getAmountTender().compareTo(zero) > 0 )
                {
                    //post void the sale item
                    amtIn = alternateTender.negate();
                }
                else
                {
                    //post void the return item
                    amtOut = alternateTender;
                }
            }
        }

        descriptor.setCountryCode(amtOut.getCountryCode());
        descriptor.setCurrencyID(amtOut.getType().getCurrencyId());
        descriptor.setTenderType(tli.getTypeCode());

        financialTotals.getTenderCount().addTenderItem(descriptor,
                                                       countIn, 
                                                       countOut,
                                                       amtIn,
                                                       amtOut,
                                                       desc,
                                                       sDesc,
                                                       tli.getHasDenominations());

        if(!(originalTransaction instanceof RedeemTransactionIfc))
        {
            if(tli.getTypeCode() == TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT)
            {
                if (tli instanceof TenderStoreCreditIfc)
                {
                    TenderStoreCreditIfc tenderStoreCredit = (TenderStoreCreditIfc) tli;
                    if (tenderStoreCredit.getState().equals(TenderStoreCreditIfc.ISSUE))
                    {
                        financialTotals.addAmountGrossStoreCreditsIssuedVoided(amtOut.abs());
                        financialTotals.addUnitsGrossStoreCreditsIssuedVoided(new BigDecimal(1));
                    }
                }
            }
            else if(tli.getTypeCode() == TenderLineItemIfc.TENDER_TYPE_GIFT_CARD)
            {
                if (tli instanceof TenderGiftCardIfc)
                {
                    TenderGiftCardIfc tenderGiftCard = (TenderGiftCardIfc) tli;
                    CurrencyIfc giftCardRedeemAmount = tenderGiftCard.getAmountTender();
                    if(originalTransaction.getTransactionType()==TransactionConstantsIfc.TYPE_RETURN ||
                            getTransactionType() == TransactionConstantsIfc.TYPE_LAYAWAY_DELETE ||
                            getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_CANCEL)
                    {
                        financialTotals.addAmountGrossGiftCardItemCreditVoided(giftCardRedeemAmount.abs());
                        financialTotals.addUnitsGrossGiftCardItemCreditVoided(new BigDecimal(1.0));
                    }
                    else if (originalTransaction.getTransactionType()==TransactionConstantsIfc.TYPE_SALE &&
                            giftCardRedeemAmount.signum() > 0)
                    {
                        financialTotals.addAmountGrossGiftCardItemCreditVoided(giftCardRedeemAmount.abs());
                        financialTotals.addUnitsGrossGiftCardItemCreditVoided(new BigDecimal(1.0));
                    }
                }
            }
        }
        return(financialTotals);
    }

    /**
     * Determines if we need to mark the original transaction as being part
     * of a post void transaction.
     *
     * Currently, only Web Orders and Special Orders need this information
     * so they can properly calculate their inventory movements.
     *
     * @param originalTransaction The original transaction.
     *
     * @return Whether the original transaction should be informed that it is part of
     *         a post void transaction.
     */
    protected boolean needToMarkTransAsVoid (TenderableTransactionIfc originalTransaction)
    {
        // Assume we do not need to fake out the original transaction.
        boolean answer = false;

        // Determine our final answer.
        int originalTransactionType = originalTransaction.getTransactionType();
        switch(originalTransactionType)
        {
            // For Web and Special Orders, we must fake out the original transaction
            // when voiding it.  This is so inventory movements can be properly calculated.
            case TYPE_ORDER_COMPLETE:
            case TYPE_ORDER_INITIATE:
            case TYPE_ORDER_CANCEL:
            case TYPE_ORDER_PARTIAL:
                answer = true;
                break;
            default:
                break;
        }

        // Return our verdict.
        return answer;
    }

    /**
     * Adds tender line item.
     *
     * @param tender The tender line item to be added
     */
    public void addTenderLineItem(TenderLineItemIfc tender)
    {
        super.addTender(tender);
    }

    /**
     * Retrieves original transaction identifier.
     *
     * @return original transaction identifier
     */
    public String getOriginalTransactionID()
    {
        // build store
        StoreIfc store = DomainGateway.getFactory().getStoreInstance();
        store.setStoreID(originalRetailStoreID);

        // build workstation
        WorkstationIfc workstation = DomainGateway.getFactory().getWorkstationInstance();
        workstation.setStore(store);
        workstation.setWorkstationID(originalWorkstationID);

        // build transaction
        TransactionIfc transaction = DomainGateway.getFactory().getTransactionInstance();
        transaction.setWorkstation(workstation);
        transaction.setTransactionSequenceNumber(originalTransactionSequenceNumber);
        transaction.setBusinessDay(originalBusinessDay);
        transaction.buildTransactionID();

        return (transaction.getTransactionID());
    }

    /**
     * Retrieves original transaction type.
     *
     * @return int original transaction type
     */
    public int getOriginalTransactionType()
    {
        return originalTransactionType;
    }

    /**
     * Sets original transaction type.
     *
     * @param int type
     */
    protected void setOriginalTransactionType(int type)
    {
        originalTransactionType = type;
    }

    /**
     * Returns the original transaction.
     * <p>
     * Returns a transaction only if <code>setOriginalTransaction</code> has
     * been called.
     * <p>
     */
    public TenderableTransactionIfc getOriginalTransaction()
    {
        return originalTransaction;
    }

    /**
     * Initializes attributes from the original transaction.
     *
     * @param transaction original transaction
     */
    public void setOriginalTransaction(TenderableTransactionIfc newTransaction)
    {
        originalTransaction = newTransaction;
        /*
         * Should use initialize() if you want to clear all of these
         * values.  This method won't clear the primary keys or the
         * transaction totals.
         */
        if (newTransaction != null)
        {
            // Set the primary keys also
            WorkstationIfc workstation = newTransaction.getWorkstation();
            setOriginalRetailStoreID(workstation.getStoreID());
            setOriginalWorkstationID(workstation.getWorkstationID());
            setOriginalTransactionSequenceNumber(newTransaction.getTransactionSequenceNumber());
            setOriginalBusinessDay(newTransaction.getBusinessDay());
            setOriginalTransactionType(newTransaction.getTransactionType());

            /*
             * Use the financial information from the original transaction
             * to setup the financials of this transaction, tenderTransactionTotals.
             */
            if(newTransaction instanceof SaleReturnTransactionIfc)
            {
                setVoidTransactionTotals(((SaleReturnTransactionIfc)newTransaction).getTransactionTotals());
            }
            else
            {

                setVoidTransactionTotals(newTransaction.getTenderTransactionTotals());
            }

            if (getOriginalTransactionType() == TYPE_ORDER_INITIATE ||
                getOriginalTransactionType() == TYPE_ORDER_PARTIAL  ||
                getOriginalTransactionType() == TYPE_ORDER_COMPLETE ||
                getOriginalTransactionType() == TYPE_ORDER_CANCEL   ||
                getOriginalTransactionType() == TYPE_LAYAWAY_INITIATE ||
                getOriginalTransactionType() == TYPE_LAYAWAY_COMPLETE  ||
                getOriginalTransactionType() == TYPE_LAYAWAY_PAYMENT ||
                getOriginalTransactionType() == TYPE_LAYAWAY_DELETE)
            {
                // If the original transaction was order, we must delay
                // reversing the state of the items in the transaction
                // until the inventory balance have been recalculated
                // the setupReversal() method is called for orders in
                // this.getInventoryUpdate()
            }
            else
            {
                setupReversal();
            }
        }
    }

    /**
     * Returns voided transaction retail store ID
     *
     * @return voided transaction retail store ID
     */
    public String getOriginalRetailStoreID()
    {
        return (originalRetailStoreID);
    }

    /**
     * Sets voided transaction retail store ID.
     *
     * @param value voided transaction retail store ID.
     */
    public void setOriginalRetailStoreID(String value)
    {
        originalRetailStoreID = value;
    }

    /**
     * Returns voided transaction workstation ID
     *
     * @return voided transaction workstation ID
     */
    public String getOriginalWorkstationID()
    {
        return (originalWorkstationID);
    }

    /**
     * Sets voided transaction workstation ID.
     *
     * @param value voided transaction workstation ID.
     */
    public void setOriginalWorkstationID(String value)
    {
        originalWorkstationID = value;
    }

    /**
     * Returns voided transaction sequence number.
     *
     * @return voided transaction sequence number
     */
    public long getOriginalTransactionSequenceNumber()
    {
        return (originalTransactionSequenceNumber);
    }

    /**
     * Sets voided transaction sequence number.
     *
     * @param value voided transaction sequence number
     */
    public void setOriginalTransactionSequenceNumber(long value)
    {
        originalTransactionSequenceNumber = value;
    }

    /**
     * Returns voided transaction business day.
     *
     * @return voided transaction business day.
     */
    public EYSDate getOriginalBusinessDay()
    {
        return (originalBusinessDay);
    }

    /**
     * Sets voided transaction business day.
     *
     * @param value voided transaction business day.
     */
    public void setOriginalBusinessDay(EYSDate value)
    {
        originalBusinessDay = value;
    }

    /**
     * Sets void transaction totals rather than calculating from line items.
     * Negates each attribute of the TransactionTotals.
     *
     * @param origTotals the TransactionTotals from the original transaction.
     */
    protected void setVoidTransactionTotals(TransactionTotalsIfc origTotals)
    {
        // Add tenderline items from original trans
        totals.setSubtotal(origTotals.getSubtotal().negate());
        totals.setSaleSubtotal(origTotals.getSaleSubtotal().negate());
        totals.setReturnSubtotal(origTotals.getReturnSubtotal().negate());
        totals.setDiscountTotal(origTotals.getDiscountTotal().negate());
        totals.setSaleDiscountTotal(origTotals.getSaleDiscountTotal().negate());
        totals.setReturnDiscountTotal(origTotals.getReturnDiscountTotal().negate());
        totals.setTransactionDiscountTotal(origTotals.getTransactionDiscountTotal().negate());
        totals.setItemDiscountTotal(origTotals.getItemDiscountTotal().negate());
        totals.setTaxTotal(origTotals.getTaxTotal().negate());
        totals.setInclusiveTaxTotal(origTotals.getInclusiveTaxTotal().negate());
        totals.setQuantityTotal(origTotals.getQuantityTotal().negate());
        totals.setGrandTotal(origTotals.getGrandTotal().negate());
        totals.setBalanceDue(origTotals.getGrandTotal().negate());
        totals.setTaxInformationContainer((TaxInformationContainerIfc)origTotals.getTaxInformationContainer().clone());
        totals.getTaxInformationContainer().negate();
        totals.getShippingChargeTotal().negate();

        PaymentIfc payment = null;

        // Adjust balance due and tender amount as required according to the
        // transaction type
        if (getOriginalTransactionType() == TYPE_RETURN)
        {
            // if a store credit was applied, do not show them as part of the
            // amount tender.
            Iterator<TenderLineItemIfc> storeCredits =
                originalTransaction.getTenderLineItemIterator(TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT);
            CurrencyIfc totalStoreCredits = DomainGateway.getBaseCurrencyInstance();
            while (storeCredits.hasNext())
            {
                totalStoreCredits = storeCredits.next().getAmountTender();
            }
            totals.setAmountTender((origTotals.getAmountTender().subtract(totalStoreCredits)).negate());
        }
        else if (originalTransaction instanceof LayawayPaymentTransactionIfc )
        {
            payment = ((LayawayPaymentTransactionIfc)originalTransaction).getPayment();
        }
        else if (originalTransaction instanceof OrderTransactionIfc)
        {
            payment = ((OrderTransactionIfc)originalTransaction).getPayment();
        }
        else
        {
            // if original transaction contains gift card line item
        	if(originalTransaction.containsTenderLineItems(TenderLineItemIfc.TENDER_TYPE_GIFT_CARD)){
        		TenderLineItemIfc temp []= originalTransaction.getTenderLineItems();
        		if(temp != null && temp.length>0){
        			int size = temp.length;
        			for(int counter = 0;counter<size ; counter++){
        				TenderLineItemIfc item = temp[counter];
        				int typeCode = item.getTypeCode();
        				if(typeCode==TenderLineItemIfc.TENDER_TYPE_GIFT_CARD){
        					totals.setAmountTender(item.getAmountTender().negate());
        					break;
        				}
        			}

        		}
            }
            else{
            	totals.setAmountTender(totals.getBalanceDue());
            }
        }

        // if the original trasaction included a payment
        // adjust Balance due and amount tender to reflect that
        if (payment != null)
        {
            totals.setAmountTender(payment.getPaymentAmount().negate());
            totals.setBalanceDue(totals.getAmountTender().negate());
        }

        // set the restocking fee total, if any
        CurrencyIfc restockingFeeTotal = origTotals.getRestockingFeeTotal();
        if (restockingFeeTotal != null)
        {
            totals.setRestockingFeeTotal(restockingFeeTotal.negate());
        }

    }

    /**
     * Sets up any reversal attributes required to undo what was done in the
     * original transaction.
     */
    public void setupReversal()
    {
        switch(originalTransactionType)
        {
            case TYPE_LAYAWAY_INITIATE:
            case TYPE_LAYAWAY_DELETE:
            case TYPE_LAYAWAY_PAYMENT:
            case TYPE_LAYAWAY_COMPLETE:
                resetLayawayStatus();
                offsetLayawayPayment();
                break;
            case TYPE_ORDER_INITIATE:
            case TYPE_ORDER_PARTIAL:
            case TYPE_ORDER_COMPLETE:
            case TYPE_ORDER_CANCEL:
                resetOrderStatus();
                break;
            // Assuming here that the original transaction is not needed in its original state
            // and can be modified and saved to reverse the previous one.
            case TYPE_PAYOUT_TILL:
            case TYPE_PAYIN_TILL:
            case TYPE_PICKUP_TILL:
            case TYPE_LOAN_TILL:
            case TYPE_PAYROLL_PAYOUT_TILL:
                TillAdjustmentTransactionIfc adjustmentTransaction =
                    (TillAdjustmentTransactionIfc) originalTransaction;
                adjustmentTransaction.setAdjustmentAmount(adjustmentTransaction.getAdjustmentAmount().negate());
                adjustmentTransaction.setAdjustmentCount(adjustmentTransaction.getAdjustmentCount());
                break;
            default:
                break;
        }
    }

    /**
     * Resets the layaway status to what it was before the transaction which is
     * being voided.
     */
    protected void resetLayawayStatus()
    {
        LayawayPaymentTransactionIfc layawayTransaction = (LayawayPaymentTransactionIfc) originalTransaction;
        LayawayIfc layaway = layawayTransaction.getLayaway();

        switch (originalTransactionType)
        {
            case TYPE_LAYAWAY_INITIATE:
                layaway.setStatus(LayawayConstantsIfc.STATUS_VOIDED);
                break;
            case TYPE_LAYAWAY_DELETE:
            case TYPE_LAYAWAY_PAYMENT:
            case TYPE_LAYAWAY_COMPLETE:
                layaway.resetStatus();
                break;
            default:
        }
    }

    /**
     * Resets the order status to what it was before the transaction which is
     * being voided.
     */
    protected void resetOrderStatus()
    {
        OrderTransactionIfc orderTransaction =
          (OrderTransactionIfc) originalTransaction;
        EYSStatusIfc status = orderTransaction.getOrderStatus().getStoreOrderStatus();

        // get array of order line items
        OrderLineItemIfc olis[] = orderTransaction.getOrderLineItems();

        switch (originalTransactionType)
        {
            case TYPE_ORDER_INITIATE:
                 // set status of initial order to voided
                status.changeStatus(OrderConstantsIfc.ORDER_STATUS_VOIDED);
                // revert status for each line item
                for (int i=0; i<olis.length; i++)
                {
                    olis[i].getOrderItemStatus().getStatus().setStatus(
                      OrderConstantsIfc.ORDER_ITEM_STATUS_VOIDED);
                }
                break;
            case TYPE_ORDER_CANCEL:
            case TYPE_ORDER_COMPLETE:
            case TYPE_ORDER_PARTIAL:
                // revert status for the order
                status.revertStatus();
                // revert status for each line item
                for (int i=0; i<olis.length; i++)
                {
                    olis[i].getOrderItemStatus().getStatus().revertStatus();
                }
                break;
            default:
        }
    }

    /**
     * Adds a payment to the layaway that offsets the payment that was on the
     * transaction being voided. Updates the payment count accordingly.
     */
    protected void offsetLayawayPayment()
    {
        LayawayPaymentTransactionIfc layawayTransaction = (LayawayPaymentTransactionIfc) originalTransaction;
        LayawayIfc layaway = layawayTransaction.getLayaway();
        CurrencyIfc paymentAmount = layawayTransaction.getPayment().getPaymentAmount();

        // negate effect of layaway payment
       layaway.setBalanceDue(layaway.getTotal().subtract(layaway.getTotalAmountPaid().add(paymentAmount.negate())));
       layaway.setTotalAmountPaid(paymentAmount.negate());

       // adjust count if not a layaway delete
       if (originalTransactionType == TYPE_LAYAWAY_DELETE)
       {
           layaway.setPaymentCount(0); // leave it alone
       }
       else
       {
           layaway.setPaymentCount(-1); // subtract 1
       }
    }

    /**
     * Returns the customer of the original transaction.
     *
     * @return customer or original transaction
     */
    public CustomerIfc getOriginalTransactionCustomer()
    {
        CustomerIfc customer = null;

        if (getOriginalTransaction() != null)
        {
            customer = getOriginalTransaction().getCustomer();
        }

        return (customer);
    }

    /**
     * Sets customer attribute and performs other operations associated with
     * assigning a customer to a transaction, such as setting discount rules. In
     * the case of a VoidTransaction, no other operations are performed.
     *
     * @param value customer
     */
    public void linkCustomer(CustomerIfc value)
    {
        setCustomer(value);
    }

    /**
     * Returns the sales associate.
     *
     * @return sales associate
     */
    public EmployeeIfc getSalesAssociate()
    {
        EmployeeIfc associate = null;

        if (originalTransaction != null && originalTransaction instanceof RetailTransactionIfc)
        {
            associate = ((RetailTransactionIfc) originalTransaction).getSalesAssociate();
        }

        return associate;
    }

    /**
     * Sets sales associate.
     *
     * @param value sales assocaite
     */
    public void setSalesAssociate(EmployeeIfc value)
    {
    }

    /**
     * Returns the line items of the original transaction with the quantity
     * negated.
     *
     * @return line items
     */
    public Vector<AbstractTransactionLineItemIfc> getLineItemsVector()
    {
        // If first call to this method
        if (lineItems == null)
        {
            // If the original transaction has been set
            if (originalTransaction != null && originalTransaction instanceof RetailTransactionIfc)
            {
                RetailTransactionIfc origTrans = (RetailTransactionIfc) originalTransaction;
                lineItems = new Vector<AbstractTransactionLineItemIfc>();
                AbstractTransactionLineItemIfc[] origLineItems = null;
                origLineItems = origTrans.getLineItems();

                int numItems = 0;
                if (origLineItems != null)
                {
                    numItems = origLineItems.length;
                }

                AbstractTransactionLineItemIfc newLineItem, origLineItem = null;

                // Clone each element of the original vector
                // and negate the quantity
                for (int i = 0; i < numItems; i++)
                {
                    // original transaction line items
                    origLineItem = origLineItems[i];
                    newLineItem = (AbstractTransactionLineItemIfc) origLineItem.clone();
                    lineItems.addElement(newLineItem);

                    // negate the quantity and re-calculate item totals
                    newLineItem.negateItemQuantity();
                }
                // original transaction line items
            }
        }
        return (lineItems);
    }

    /**
     * Retrieves iterator for line items array.
     *
     * @return iterator for line items array
     */
    public Iterator<AbstractTransactionLineItemIfc> getLineItemsIterator()
    {
        return(getLineItemsVector().iterator());
    }


    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.VoidTransactionIfc#getReversedTenders()
     */
    public ReversibleTenderIfc[] getReversedTenders()
    {
        List<ReversibleTenderIfc> reservedTenders = new ArrayList<ReversibleTenderIfc>();
        for (TenderLineItemIfc tender : getTenderLineItemsVector())
        {
            if (tender instanceof ReversibleTenderIfc)
            {
                reservedTenders.add((ReversibleTenderIfc)tender);
            }
        }
        return reservedTenders.toArray(new ReversibleTenderIfc[reservedTenders.size()]);
    }

    /**
     * The pro-rated amount of the transaction discount amount must be negated
     * for each transaction discount.
     *
     * @param ip ItemPriceIfc object
     * @deprecated As of release 14.1
     */
    protected void setTransactionDiscounts(ItemPriceIfc ip)
    {
        // pull transaction discounts
        ItemDiscountStrategyIfc[] discounts = ip.getItemDiscounts();
        int len = 0;
        // clear all discounts
        ip.clearItemDiscounts();
        ItemTransactionDiscountAuditIfc itda = null;
        ItemTransactionDiscountAuditIfc nitda = null;
        ReturnItemTransactionDiscountAuditIfc ritda = null;
        ReturnItemTransactionDiscountAuditIfc nritda = null;
        if (discounts != null)
        {
            len = discounts.length;
        }
        for (int i = 0; i < len; i++)
        {
            if (discounts[i] instanceof ItemTransactionDiscountAuditIfc)
            {
                itda = (ItemTransactionDiscountAuditIfc) discounts[i];
                // set new discount with value sign reversed
                nitda =
                      DomainGateway.getFactory().getItemTransactionDiscountAuditInstance();

                nitda.initialize(itda.getDiscountAmount().negate(),
                                 itda.getReason(),
                                 itda.getAssignmentBasis());
                nitda.setOriginalDiscountMethod(itda.getOriginalDiscountMethod());
                nitda.setDiscountEmployee(itda.getDiscountEmployeeID());
                nitda.setDiscountRate(itda.getDiscountRate());

                ip.addItemDiscount(nitda);
            }
            else if (discounts[i] instanceof ReturnItemTransactionDiscountAuditIfc)
            {
                ritda = (ReturnItemTransactionDiscountAuditIfc) discounts[i];
                nritda =
                      DomainGateway.getFactory().getReturnItemTransactionDiscountAuditInstance();
                nritda.initialize(ritda.getDiscountAmount().negate(), ritda.getReason());
                nritda.setAssignmentBasis(ritda.getAssignmentBasis());
                nritda.setDiscountEmployee(ritda.getDiscountEmployeeID());
                nritda.setDiscountRate(ritda.getDiscountRate());
                ip.addItemDiscount(nritda);
            }
            else
            {
                ip.addItemDiscount(discounts[i]);
            }
        }
    }

    /**
     * Retrieves the line items that are part of the group passed as the
     * argument.
     *
     * @param String prodGroupID
     * @return SaleReturnLineItemIfc[]
     */
    public SaleReturnLineItemIfc[] getProductGroupLineItems(String prodGroupID)
    {
        // create a new vector (groupLineItems) that contains just the line items
        // that are part of the product group
        SaleReturnLineItemIfc[] items = null;
        Vector<SaleReturnLineItemIfc> groupLineItems = new Vector<SaleReturnLineItemIfc>();
        // get number of line items from line items vector
        Vector<AbstractTransactionLineItemIfc> lineItemsVector = getLineItemsVector();
        if (lineItemsVector != null)
        {
            for(int i = 0; i < lineItemsVector.size(); i++)
            {
                if (lineItemsVector.elementAt(i) instanceof SaleReturnLineItemIfc)
                {
                   SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)lineItemsVector.elementAt(i);
                   if (item.getPLUItem().getItemClassification().getGroup().getGroupID().equals(
                                                    prodGroupID))
                   {
                       groupLineItems.addElement(item);
                   }
                }
            }
        }

        // create a SaleReturnLineItemIfc[] array and copy the groupLineItems
        // elements into it
        if (groupLineItems.size() > 0)
        {
            items = new SaleReturnLineItemIfc[groupLineItems.size()];
            groupLineItems.copyInto(items);
        }

        return(items);
    }

    /**
     * Returns the line items of the original transaction with the quantity
     * negated.
     *
     * @return array of line items
     */
    public AbstractTransactionLineItemIfc[] getLineItems()
    {
        AbstractTransactionLineItemIfc[] lineItems = null;
        // get number of lines from line items vector
        Vector<AbstractTransactionLineItemIfc> lineItemsVector = getLineItemsVector();
        int numLines = 0;
        if (lineItemsVector != null)
        {
            numLines = lineItemsVector.size();
            lineItems = new AbstractTransactionLineItemIfc[numLines];
            lineItemsVector.copyInto(lineItems);
        }

        return(lineItems);
    }
    
    /**
     * Retrieves lineItems that are included in transaction totals
     * 
     * @return array of totalable line items.
     */
    public AbstractTransactionLineItemIfc[] getTotalableLineItems()
    {
        List<AbstractTransactionLineItemIfc> itemList = new ArrayList<AbstractTransactionLineItemIfc>();
        for (AbstractTransactionLineItemIfc abstractLineItem : getLineItemsVector())
        {
            if (abstractLineItem.isTotalable())
            {
                itemList.add(abstractLineItem);
            }
        }
        return itemList.toArray(new AbstractTransactionLineItemIfc[itemList.size()]);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.RetailTransactionIfc#getLineItemsExceptExclusions()
     */
    public AbstractTransactionLineItemIfc[] getLineItemsExceptExclusions()
    {
        List<AbstractTransactionLineItemIfc> itemList = new ArrayList<AbstractTransactionLineItemIfc>();
        for (AbstractTransactionLineItemIfc abstractLineItem : getLineItemsVector())
        {
            if (abstractLineItem instanceof SaleReturnLineItemIfc)
            {
                SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)abstractLineItem;
                if (lineItem.isPartOfPriceAdjustment() // don't include any price adjustment components
                       || (lineItem.isSaleLineItem() && lineItem.isKitComponent()) // don't include any sale kit comps
                       || (lineItem.isReturnLineItem() && lineItem.isKitHeader()) // don't include any return kit headers
                       || (lineItem.isPriceAdjustmentLineItem() && lineItem.isKitHeader())) // don't include any adj kit headers
                {
                    continue;
                }
                itemList.add(lineItem);
            }
        }
        return itemList.toArray(new AbstractTransactionLineItemIfc[itemList.size()]);
    }


    /**
     * Returns the transaction tax object from the original transaction
     *
     * @return transaction tax object
     */
    public TransactionTaxIfc getTransactionTax()
    {
        TransactionTaxIfc tax = null;

        if (originalTransaction != null && originalTransaction instanceof RetailTransactionIfc)
        {
            tax = ((RetailTransactionIfc) originalTransaction).getTransactionTax();
        }

        return tax;
    }

    /**
     * Returns the transaction discount array from the original transaction
     *
     * @return transaction discount array
     */
    public TransactionDiscountStrategyIfc[] getTransactionDiscounts()
    {
        TransactionDiscountStrategyIfc discounts[] = null;

        if (originalTransaction != null && originalTransaction instanceof RetailTransactionIfc)
        {
            discounts = ((RetailTransactionIfc)originalTransaction).getTransactionDiscounts();
        }

        return discounts;
    }

    /**
     * Retrieves OrderID descriptor string.
     *
     * @return orderID
     */
    public String getOrderID()
    {
        return orderID;
    }

    /**
     * Sets OrderID descriptor string.
     * <P>
     */
    public void setOrderID(String id)
    {
        orderID = id;
    }

    /**
     * Indicates whether this transaction contains one or more order line items.
     *
     * @return true if line items vector contains order line item(s).
     */
    public boolean containsOrderLineItems()
    {
        boolean returnValue = false;

        if (lineItems != null)
        {
            for (Enumeration<AbstractTransactionLineItemIfc> e = lineItems.elements(); e.hasMoreElements(); )
            {
                if (e.nextElement() instanceof OrderLineItemIfc)
                {
                    returnValue = true;
                }
            }
        }

        return returnValue;
    }

    /**
     * Derives the additive financial totals for a layaway complete transaction,
     * not including line items and tenders .
     *
     * @return additive financial totals
     */
    protected void getLayawayFinancialTotals(FinancialTotalsIfc financialTotals,
                                             LayawayTransactionIfc layawayTransaction)
    {
        // gross total is transaction subtotal with discount applied minus payments applied
        TransactionTotalsIfc layawayTotals = layawayTransaction.getTransactionTotals();
        CurrencyIfc tax = layawayTotals.getTaxTotal();
        CurrencyIfc inclusiveTax = layawayTotals.getInclusiveTaxTotal();
        CurrencyIfc gross = layawayTransaction.getPayment().getPaymentAmount().subtract(tax);


        if (getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
        {
            financialTotals.addAmountGrossTaxExemptTransactionSalesVoided(gross.abs());
            financialTotals.addCountGrossTaxExemptTransactionSalesVoided(1);
        }
        // if tax is positive value, add to taxable
        // Note:  tax exempt stuff is counted as non-taxable for now
        // TEC need to see if tax is zero to handle returns
        if (isTaxableTransaction())
        {
            financialTotals.addAmountGrossTaxableTransactionSalesVoided(gross.abs());
            financialTotals.addCountGrossTaxableTransactionSalesVoided(1);
            financialTotals.addAmountTaxTransactionSales(tax.negate());
            financialTotals.addAmountInclusiveTaxTransactionSales(inclusiveTax.negate());
        }
        else
        {
            financialTotals.addAmountGrossNonTaxableTransactionSalesVoided(gross.abs());
            financialTotals.addCountGrossNonTaxableTransactionSalesVoided(1);
        }

        // handle discount amounts
        TransactionDiscountStrategyIfc[] discounts = layawayTransaction.getTransactionDiscounts();
        if (discounts != null)
        {
            for (int x = 0; x < discounts.length; x++)
            {
                if (discounts[x].getAssignmentBasis() == DiscountRuleIfc.ASSIGNMENT_EMPLOYEE)
                {
                    financialTotals.addUnitsGrossTransactionEmployeeDiscount(new BigDecimal(-1));
                }
                else
                {
                    financialTotals.addNumberTransactionDiscounts(-1);
                }
            }
        }
        // get line item financial totals
        getLineItemsFinancialTotals(financialTotals);
    }

    /**
     * Write journal header to specified string buffer.
     *
     * @return journal fragment string
     * @deprecated as of 13.1. new method added to take the client's journal
     *             locale.
     */
    @Override
    public String toJournalString()
    {
        return (toJournalString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
    }

    /**
     * Write journal header to specified string buffer.
     *
     * @param journalLocale locale received from the client
     * @return journal fragment string
     */
    @Override
    public String toJournalString(Locale journalLocale)
    {
        StringBuffer strResult = new StringBuffer();
        strResult.append(super.toJournalString(journalLocale));
        // Must remove trailing EOL for automated testing
        strResult.deleteCharAt(strResult.length() - 1);
        // pass back result
        return (strResult.toString());
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.AbstractTenderableTransaction#toString()
     */
    @Override
    public String toString()
    {
        // build result string
        StringBuilder strResult = new StringBuilder("Class:  VoidTransaction ")
            .append(hashCode())
            .append("\n");

        // add attributes to string
        if (originalTransaction == null)
        {
            strResult.append("originalTransaction:                [null]\n");
        }
        else
        {
            strResult.append(originalTransaction.toString());
        }
        strResult.append("originalRetailStoreID:              [")
            .append(originalRetailStoreID)
            .append("]\n")
            .append("originalWorkstationID:              [")
            .append(originalWorkstationID)
            .append("]\n")
            .append("originalTransactionSequenceNumber:  [")
            .append(originalTransactionSequenceNumber)
            .append("]\n")
            .append("orderID:                            [")
            .append(orderID)
            .append("]\n")
            .append("reason:                             [")
            .append(reason)
            .append("]\n");


        if (originalBusinessDay == null)
        {
            strResult.append("originalBusinessDay:                [null]\n");
        }
        else
        {
            strResult.append("originalBusinessDay:                [")
                .append(originalBusinessDay)
                .append("]\n");
        }
        if (tenderLineItemsVector == null)
        {
            strResult.append("tenderLineItemsVector:                    [null]\n");
        }
        else
        {
            strResult.append(tenderLineItemsVector.toString());
        }
        if (totals == null)
        {
            strResult.append("totals:                             [null]\n");
        }
        else
        {
            strResult.append(totals.toString());
        }
        if (customer == null)
        {
            strResult.append("customer:                           [null]\n");
        }
        else
        {
            strResult.append(customer.toString());
        }
        if (lineItems == null)
        {
            strResult.append("lineItems:                          [null]\n");
        }
        else
        {
            strResult.append(lineItems.toString());
        }
        // pass back result
        return(strResult.toString());
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.AbstractTenderableTransaction#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean isEqual = true;

        // Make sure it's a VoidTransaction
        if (obj instanceof VoidTransaction)
        {
            // downcast the input object
            VoidTransaction c = (VoidTransaction) obj;

            // compare all the attributes of VoidTransaction

            if(super.equals(obj) &&
                Util.isObjectEqual(originalTransaction, c.getOriginalTransaction()) &&
                Util.isObjectEqual(originalRetailStoreID, c.getOriginalRetailStoreID()) &&
                Util.isObjectEqual(originalWorkstationID, c.getOriginalWorkstationID()) &&
                (originalTransactionSequenceNumber == c.getOriginalTransactionSequenceNumber()) &&
                Util.isObjectEqual(originalBusinessDay, c.getOriginalBusinessDay()) &&
                Util.isObjectEqual(getLineItems(), c.getLineItems()) &&
                Util.isObjectEqual(reason, c.getReason()))
            {
                isEqual = true;
            }
            else
            {
                isEqual = false;
            }
        }
        else
        {
            isEqual = false;
        }

        return(isEqual);
    }

    /**
     * Retrieves reasonCode string.
     *
     * @return reasonCode
     * @deprecated as of 13.1. Use {@setLocalizedReasonCode(LocalizedCodeIfc)}
     */
    public void setReasonCode(String reasonCode)
    {
        this.reasonCode = reasonCode;
    }

    /**
     * Sets reasonCode string.
     *
     * @deprecated as of 13.1. Use {@getLocalizedReasonCode()}
     */
    public String getReasonCode()
    {
        return this.reasonCode;
    }

    /**
     * Retrieves the Reason Code text
     *
     * @return The reason code text.
     * @deprecated as of 13.1. Use {@getLocalizedReasonCode()}
     */
    public String getReasonCodeText()
    {
        return this.reasonCodeText;
    }

    /**
     * Sets the Reason Code text
     *
     * @param reasonCodeText
     * @deprecated as of 13.1. Use {@getLocalizedReasonCode()}
     */
    public void setReasonCodeText(String reasonCodeText)
    {
        this.reasonCodeText = reasonCodeText;
    }

    /**
     * Sets a localized reason code
     *
     * @param LocalizedCodeIfc localizedReasonCode
     */
    public LocalizedCodeIfc getReason()
    {
        return reason;
    }

    /**
     * Returns a localized reason code
     *
     * @param LocalizedCodeIfc localizedReasonCode
     */
    public void setReason(LocalizedCodeIfc reason)
    {
        this.reason = reason;
    }

    /**
     * Check to see if the transaction is taxable.  A void transaction
     * is by default non-taxable, unless any item in the transaction
     * is a taxable item.
     *
     *  @return true or false
     *  @since 7.0
     */
    public boolean isTaxableTransaction()
    {
        boolean taxableItemFound = false;
        boolean nonTaxableItemFound = false;
        if(getTransactionTotals() != null && getTransactionTotals().getTaxInformationContainer() != null)
        {
            TaxInformationIfc[] taxInfo = getTransactionTotals().getTaxInformationContainer().getTaxInformation();
            if(taxInfo != null && taxInfo.length > 0)
            {
                for(int i=0; i<taxInfo.length; i++)
                {
                    if(taxInfo[i].getTaxMode() == TaxConstantsIfc.TAX_MODE_NON_TAXABLE ||
                      taxInfo[i].getTaxMode() == TaxConstantsIfc.TAX_MODE_EXEMPT ||
                      taxInfo[i].getTaxMode() == TaxConstantsIfc.TAX_MODE_TOGGLE_OFF)
                    {
                        nonTaxableItemFound = true;
                    }
                    else
                    {
                        taxableItemFound = true;
                    }
                }
            }
            else {
            	return checkTaxableItem();
            }
        }


        boolean taxable = true; // default
        if(!taxableItemFound && nonTaxableItemFound)
        {
            taxable = false;
        }
        return taxable;
    }

	/**
	 * Method checks if any line item is taxable
	 *
	 * @return boolean
	 */
    private boolean checkTaxableItem()
    {
		boolean taxable = true;
		//if items are there in void transaction
		if(this.lineItems != null && this.lineItems.size() > 0){
			int size = this.lineItems.size();
			for(int count = 0 ; count < size; count++){
				taxable = ((SaleReturnLineItemIfc)lineItems.get(count)).getTaxable();
				//if any of items is taxable return true
				if(taxable){
					break;
				}
			}
		}
		return taxable;
	}

    /**
     * For each new send adds a send package line item containing shipping-to
     * customer and shipping method used
     *
     * @param sendPackage send package line item
     */
    public void addSendPackage(SendPackageLineItemIfc sendPackageUsed)
    {
        if (sendPackages == null)
        	sendPackages = getSendPackageVector();

        if (sendPackages != null )
        {
        	sendPackages.add(sendPackageUsed);
        	sendPackageUsed.setPackageNumber(sendPackages.size());
        }
    }
    /**
     * Get send package at the requested index. This is zero-based, which would
     * be the package's number minus one.
     *
     * @return package at the requested index.
     */
    public SendPackageLineItemIfc getSendPackage(int index)
    {
    	//if this is the first time call, copy sendPackages from orignal transaction.
        if (sendPackages == null)
        	sendPackages = getSendPackageVector();

        if (sendPackages != null )
        {
            SendPackageLineItemIfc sendPackage = sendPackages.get(index);
            sendPackage.setPackageNumber(index + 1);
            return sendPackage;
        }
        else
        {
        	return null;
        }
    }


    /**
     * Gets send packages for all the sends
     *
     * @return an array of send packages for all the sends
     */
    public SendPackageLineItemIfc[] getSendPackages()
    {
    	//if this is the first time call, copy sendPackages from orignal transaction.
        if (sendPackages == null)
        	sendPackages = getSendPackageVector();

        if (sendPackages != null )
        {
	        return sendPackages.toArray(new SendPackageLineItem[sendPackages.size()]);
        }
        else
        {
        	return null;
        }
    }

    /**
     * Retrieve sendPackageCount.
     *
     * @return the send package count
     */
    public int getItemSendPackagesCount()
    {
    	int count = 0;
    	if ( originalTransaction instanceof SaleReturnTransaction )
    	{
    		count = ((SaleReturnTransaction)originalTransaction).getSendPackageCount();
    	}
        return count;
    }

    /**
     * Returns the line items of the original transaction with the quantity
     * negated.SendPackageLineItemIfc[]
     *
     * @return line items
     */
    protected Vector<SendPackageLineItemIfc> getSendPackageVector()
    {
        Vector<SendPackageLineItemIfc> sendPackagelineItems = new Vector<SendPackageLineItemIfc>();

            // If the original transaction has been set
            if (originalTransaction != null && originalTransaction instanceof RetailTransactionIfc)
            {
                RetailTransactionIfc origTrans = (RetailTransactionIfc) originalTransaction;
                SendPackageLineItemIfc[] origSendPackageLineItems = origTrans.getSendPackages();

                int numItems = 0;
                if (origSendPackageLineItems != null)
                {
                    numItems = origSendPackageLineItems.length;
                }
                // Clone each element of the original vector
                // and negate the quantity
                for (int i = 0; i < numItems; i++)
                {
                    // original transaction line items
                	sendPackagelineItems.addElement((SendPackageLineItemIfc)origSendPackageLineItems[i].clone());
                }
                // original transaction line items
            }

        return (sendPackagelineItems);
    }
    /** Returns the transaction currency type.
    *
    *  @return CurrencyTypeIfc
    */
    public CurrencyTypeIfc getCurrencyType()
    {
        return currencyType;
    }

    /** Sets the currency type for this transaction.
    *
    *  @param CurrencyTypeIfc currencyType
    */
    public void setCurrencyType(CurrencyTypeIfc currencyType)
    {
        this.currencyType = currencyType;
    }


    /** Returns the transaction country code.
    *
    *  @return String
    */
    public String getTransactionCountryCode()
    {
        return transactionCountryCode;
    }

    /** Sets the ISO country code for this transaction.
    *
    *  @param String transactionCountryCode
    */
    public void setTransactionCountryCode(String transactionCountryCode)
    {
        this.transactionCountryCode = transactionCountryCode;
    }    

    /**
     * @return a boolean flag indicating if the transaction contains cross
     * channel order line item.
     */
    @Override
    public boolean containsXChannelOrderLineItem()
    {
        boolean result = false;
        
        if (getOriginalTransaction() instanceof SaleReturnTransactionIfc)
        {
            for (AbstractTransactionLineItemIfc lineItem : ((SaleReturnTransactionIfc)getOriginalTransaction()).getLineItems())
            {
                boolean isReturn = ((SaleReturnLineItemIfc)lineItem).isReturnLineItem();
                OrderItemStatusIfc orderItemStatus = ((SaleReturnLineItemIfc)lineItem).getOrderItemStatus();
                if ((orderItemStatus != null) && orderItemStatus.isCrossChannelItem() && isReturn)
                {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
    
    /**
     * return true if the transaction is cross channel order pickup or order cancel. Otherwise, return false
     * @return
     */
    public boolean isOrderPickupOrCancel()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.RetailTransactionIfc#getRecommendedItems(int)
     */
    @Override
    public List<ExtendedItemData> getRecommendedItems(int maxRecommendedItemsListSize)
    {
        return null;
    }
}
