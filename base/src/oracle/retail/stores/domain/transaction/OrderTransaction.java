/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   09/26/14 - set pickupDeliveryItemDepositAmount for order 
 *                         payment in TR_LTM_PYAN.MO_PYM_AGT_RCV which
 *                         should not include sale amount.
 *    yiqzhao   07/09/14 - Exclude cancelled order items for reprice.
 *    sgu       07/05/14 - set order line reference for in-store priced item
 *    yiqzhao   06/20/14 - OrderAmount in financialTotals should not include
 *                         take with item sale amount.
 *    sgu       06/15/14 - fix transaction total and discount calculation for
 *                         order pickup transaction
 *    sgu       06/11/14 - combine multiple order transactions into one final
 *                         one
 *    yiqzhao   06/12/14 - Introduce maxLineReference for adding take with item
 *                         at pickup or cancel order time.
 *    sgu       05/25/14 - update transaction total, order payment and tender
 *                         to support adding take with items for order pickup
 *                         transaction
 *    yiqzhao   05/09/14 - Add method isOrderPickupOrCancel.
 *    yiqzhao   05/07/14 - Set deposit amount.
 *    yiqzhao   05/02/14 - While picking items, take with item can be added in
 *                         Order Transaction.
 *    yiqzhao   03/06/14 - Fix the issue related to orders contain take with
 *                         items in summary reports.
 *    jswan     07/16/13 - Updated xc and store grand totals in the
 *                         addTransactionDiscountDuringTender() method so that
 *                         the deposit pro ration will calculated correctly.
 *    jswan     07/09/13 - Fixed issues saving the cash adjustment total to the
 *                         history tables for order, layaway, redeem and voided
 *                         transactions.
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    jswan     04/11/13 - Modified to prevent the update of transaction sales
 *                         totals.
 *    sgu       12/12/12 - move xchannel functions to sale return transaction
 *    sgu       10/15/12 - refactor order pickup flow to support partial pickup
 *    sgu       06/22/12 - refactor order id assignment
 *    sgu       06/21/12 - rename resetOrderID to resyncOrderID
 *    sgu       06/20/12 - clone the order transaction before making changes
 *    sgu       06/19/12 - handle xc order id creation failure
 *    sgu       05/29/12 - set up retail transaction technician to handle
 *                         creation of xc customer order
 *    sgu       05/09/12 - separate minimum deposit amount into xchannel part
 *                         and store order part
 *    sgu       05/08/12 - retrieve order totals from order status object
 *    sgu       05/08/12 - prorate store order and xchannel deposit amount
 *                         separatly
 *    sgu       05/07/12 - rename crossChannel to XChannel
 *    sgu       05/07/12 - read/write order status table
 *    sgu       05/04/12 - refactor OrderStatus to support store order and
 *                         xchannel order
 *    sgu       04/26/12 - fix containsCrossChannelOrderLineItem to return the
 *                         correct boolean flag
 *    sgu       04/25/12 - fixed indentation
 *    sgu       04/24/12 - read/save order delivery details
 *    sgu       03/29/12 - move order recipient record be transactional data.
 *    jswan     10/20/10 - Fix an issue in the ARG summary report showing the
 *                         Cancelled Order amount.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    jswan     03/24/10 - Fix an issue with instant credit enrollment discount
 *                         with special orders.
 *    cgreene   02/02/10 - add override method updateTenderTotals
 *    abondala  01/03/10 - update header date
 *    asinton   06/05/09 - Added method to check if canceled order items exist
 *                         in the transaction.
 *    aphulamb  04/14/09 - Fixed issue if Special Order is done by Purchase
 *                         Order
 *    mchellap  04/02/09 - Added null check to
 *                         updateTransactionTotalsWithTransDiscount
 *    cgreene   04/01/09 - implement method getReceiptTenderLineItems which
 *                         returns getCollectedTenderLineItems except for order
 *                         cancels which return all tenders
 *    cgreene   03/31/09 - added method isRefundDue() to to OrderTransactionIfc
 *                         and hasCollectedTnderLineItems to
 *                         TenderTransactionIfc for use in printing order
 *                         receipts
 *    npoola    03/27/09 - removed the unwanted code for SaleItem
 *                         initialization
 *    npoola    03/26/09 - Fix to display line items properly for Gold
 *                         Customers
 *    stallama  03/26/09 - Fixed the recept totals issue and change due issue
 *                         at the time of pickup.
 *    cgreene   03/25/09 - redo getOrderPhoneNumber so that customer's phone
 *                         takes precedence
 *    mahising  03/20/09 - Fixed total amount issue for PDO receipt when
 *                         customer added
 *    stallama  03/20/09 - updating the tenderTransactionTotals along with
 *                         transaction totals.
 *    cgreene   03/11/09 - default to customers phone number for order if there
 *                         are no deliver details
 *    mahising  02/26/09 - Rework for PDO functionality
 *    mahising  02/26/09 - Rework for PDO functionality
 *    jswan     02/22/09 - Merge from refresh to tip
 *    cgreene   02/21/09 - added getOrderCustomerPhone method
 *    jswan     02/21/09 - Added intialization in the constructor for
 *                         orderDeliveryDetailCollection.
 *    jswan     02/20/09 - Added access methods to the interface for the
 *                         orderType data member.
 *    jswan     01/30/09 - Modifications to correctly print the change due on
 *                         Order Reciepts.
 *    cgreene   12/17/08 - prorate the deposit including taxation in case
 *                         customer has paid all amounts in full
 *    aphulamb  12/17/08 - bug fixing of PDO
 *    aphulamb  12/10/08 - version added
 *    aphulamb  12/10/08 - revision number is added back
 *    aphulamb  12/09/08 - Deposite Amount and Cancel Trasaction Fixes.
 *    npoola    12/04/08 - PDO Amrish checkins
 *    aphulamb  11/27/08 - checking files after merging code for receipt
 *                         printing by Amrish
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/17/08 - Pickup Delivery order
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    cgreene   11/18/08 - removed call to deprecated salereturntrans method
 *                         that did nothing
 *    mchellap  11/13/08 - Inventory Reservation Module
 *
 * ===========================================================================
 * $Log:
 *    10   360Commerce 1.9         5/16/2007 7:56:04 PM   Brett J. Larsen
 *         CR 26903 - 8.0.1 merge to trunk
 *
 *         BackOffice <ARG> Summary Report overhaul (many CRs fixed)
 *
 *    9    360Commerce 1.8         5/14/2007 6:08:34 PM   Sandy Gu
 *         update inclusive information in financial totals and history tables
 *    8    360Commerce 1.7         4/25/2007 10:00:20 AM  Anda D. Cadar   I18N
 *         merge
 *    7    360Commerce 1.6         5/12/2006 5:26:37 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    6    360Commerce 1.5         4/27/2006 7:29:49 PM   Brett J. Larsen CR
 *         17307 - remove inventory functionality - stage 2
 *    5    360Commerce 1.4         1/22/2006 11:41:57 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    4    360Commerce 1.3         12/13/2005 4:43:52 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:29:15 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:54 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:54 PM  Robert Pearse
 *
 *   Revision 1.19.2.4  2004/11/16 19:58:08  mweis
 *   @scr 7680 Use proper 'old' status when voiding an order.  Must also use (a different) proper 'old' status during filling, picking up, and canceling orders.
 *
 *   Revision 1.19.2.3  2004/11/12 23:16:57  mweis
 *   @scr 7680 Use 'saved' as the old status, and not 'previous'.
 *
 *   Revision 1.19.2.2  2004/11/03 22:19:42  mweis
 *   @scr 7012 Use proper Inventory constants for default values.
 *
 *   Revision 1.19.2.1  2004/10/15 18:50:25  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.20  2004/10/11 17:48:13  mweis
 *   @scr 7012 Update comments to assist any future debugging.
 *
 *   Revision 1.19  2004/10/06 21:28:09  mweis
 *   @scr 7012 Ensure Post Void of Order works w.r.t. Inventory.  Use getPreviousStatus() to get the old status.  Doh!
 *
 *   Revision 1.18  2004/10/06 02:44:17  mweis
 *   @scr 7012 Special and Web Orders now have Inventory.
 *
 *   Revision 1.17  2004/09/29 16:30:21  mweis
 *   @scr 7012 Special Order and Inventory integration -- canceling the entire order.
 *
 *   Revision 1.16  2004/09/27 19:53:01  kll
 *   @scr 7152: pull out discount calculation of OrderTransaction.getOrderFinancialTotals()
 *
 *   Revision 1.15  2004/09/23 00:30:51  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.14  2004/09/17 15:49:59  jdeleau
 *   @scr 7146 Define a taxable transaction, for reporting purposes.
 *
 *   Revision 1.13  2004/08/23 16:15:46  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.12  2004/08/10 07:19:36  mwright
 *   Merge (3) with top of tree
 *   Resolution for SCR 1578 - read back customer email address and order description
 *
 *   Revision 1.11.2.1  2004/07/29 01:49:39  mwright
 *   Resolution for SCR1578
 *   Read back the customer email address and order description that was stored in the order table, and make them accessable in the object
 *
 *   Revision 1.11  2004/07/01 13:59:46  jeffp
 *   @scr 3837 Added method to removeTenderLineItem. System logic was not updating tenderTransactionTotals.
 *
 *   Revision 1.10  2004/06/30 17:42:01  aachinfiev
 *   Fixing InventoryBuckets for order void case
 *
 *   Revision 1.9  2004/06/30 16:20:32  aachinfiev
 *   Fixing InventoryBuckets for order void case
 *
 *   Revision 1.8  2004/06/29 21:59:00  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.7  2004/06/15 16:05:33  jdeleau
 *   @scr 2775 Add database entry for uniqueID so returns w/
 *   receipt will work, make some fixes to FinancialTotals storage of tax.
 *
 *   Revision 1.6  2004/06/15 00:44:31  jdeleau
 *   @scr 2775 Support register reports and financial totals with the new
 *   tax engine.
 *
 *   Revision 1.5  2004/05/11 23:03:02  jdeleau
 *   @scr 4218 Backout recent changes to remove TransactionDiscounts,
 *   going to go a different route and remove the newly added
 *   voids and grosses instead.
 *
 *   Revision 1.3  2004/02/12 17:14:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:28:50  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Oct 02 2003 10:45:48   baa
 * fix for rss defect 1406
 * Resolution for 3402: RSS  PRF 1406 Tender Information on Void transactions does not looks correct
 *
 *    Rev 1.0   Aug 29 2003 15:40:54   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   24 Jun 2003 20:06:12   mpm
 * Added code to set financials, bypass inventory updates, when in-process-void transactions are saved.
 *
 *    Rev 1.0   Jun 03 2002 17:05:56   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 12:30:18   msg
 * Initial revision.
 *
 *    Rev 1.16   Feb 21 2002 16:34:36   dfh
 * added getorderfinancialtotals - updates for register reports
 * Resolution for POS SCR-1298: Completed Layaway Pickup does not update Total Item Sales count/amount on Summary Report
 *
 *    Rev 1.15   Feb 10 2002 21:53:40   dfh
 * updates to try to include orders in summary reports
 * Resolution for POS SCR-1225: Summary Report requirements for Special Order has changed - vers 9
 *
 *    Rev 1.14   Feb 05 2002 16:36:28   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.13   Feb 03 2002 14:15:56   mpm
 * Removed debugging statements.
 * Resolution for Domain SCR-14: Special Order modifications
 *
 *    Rev 1.11   Jan 23 2002 21:21:38   dfh
 * add order partial to financials (payment)
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.10   Jan 18 2002 16:34:50   dfh
 * clone lineamount for proratedeposit method
 * Resolution for POS SCR-779: Item prices missing from receipt for Special Order
 *
 *    Rev 1.9   03 Jan 2002 18:08:04   cir
 * Change setPayment method
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.8   02 Jan 2002 14:02:06   jbp
 * Update Tender Totals when tenderLineItems are read
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.7   Dec 26 2001 17:26:00   mpm
 * Modified to properly handle order transaction retrieval.
 * Resolution for Domain SCR-14: Special Order modifications
 *
 *    Rev 1.6   Dec 14 2001 15:04:02   dfh
 * remove tender line items added
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.5   09 Dec 2001 10:18:38   mpm
 * Added support for OrderLineItemIfc in order transactions and associated database activity.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.4   Dec 02 2001 21:13:22   dfh
 * updates for accepting tender
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.3   02 Dec 2001 12:48:14   mpm
 * Implemented financials, voids for special order domain objects.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.2   27 Nov 2001 06:23:46   mpm
 * Modifications to support data operations for special order.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   22 Nov 2001 08:02:56   mpm
 * Modified as needed.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   13 Nov 2001 07:21:38   mpm
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.PaymentHistoryInfoIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.order.OrderRecipientIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.utility.EYSStatusIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;

/**
 * This class defines an order transaction. This class is extended from
 * SaleReturnTransaction and includes the order status data.
 *
 * @version $Revision: /main/66 $
 */
public class OrderTransaction extends SaleReturnTransaction
    implements OrderTransactionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -840226668887081901L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/66 $";

    /**
     * order status reference
     */
    protected OrderStatusIfc orderStatus = null;

    /**
     * The new store order id assigned to this transaction
     */
    protected String newStoreOrderIdAssigned = "";

    /**
     * The new cross channel order id assigned to this transaction
     */
    protected String newXChannelOrderIdAssigned = "";

    /**
     * payment reference
     */
    protected PaymentIfc payment = null;

    /**
     * tender transaction totals
     */
    protected TransactionTotalsIfc tenderTransactionTotals = null;

    /**
     * Customer email address
     */
    protected String customerEmailAddress = null;

    /**
     * Inventory description
     */
    protected String description = null;

    /**
     * Order Delivery Detail collection
     */
    protected Collection<OrderDeliveryDetailIfc> orderDeliveryDetailCollection = null;

    /**
     * Order Receipient
     */
    protected OrderRecipientIfc orderRecipient = null;

    /*
     * payment history info collection
     */
    protected List<PaymentHistoryInfoIfc> paymentHistoryInfoCollection = null;
    
    /*
     * The max store line reference number. It is used for creating a new take with item.
     */
    protected int maxStoreOrderLineReference= -1;
    
    /*
     * The max xchannel line reference number. It is used for creating an in-store priced item.
     */
    protected int maxXChannelOrderLineReference = -1;


    /**
     * Constructs OrderTransaction object.
     */
    public OrderTransaction()
    {
        orderStatus = DomainGateway.getFactory().getOrderStatusInstance();
        tenderTransactionTotals = DomainGateway.getFactory().getTransactionTotalsInstance();
        paymentHistoryInfoCollection = new ArrayList<PaymentHistoryInfoIfc>();
        orderDeliveryDetailCollection = new ArrayList<OrderDeliveryDetailIfc>();
    }

    /**
     * Creates clone of this object.
     *
     * @return Object clone of this object
     */
    public Object clone()
    {
        // instantiate new object
        OrderTransaction c = new OrderTransaction();

        // set values
        setCloneAttributes(c);

        // pass back Object
        return c;
    }

    /**
     * Sets attributes in clone of this object.
     *
     * @param newClass new instance of object
     */
    public void setCloneAttributes(OrderTransaction newClass)
    {
        super.setCloneAttributes(newClass);
        if (orderStatus != null)
        {
            newClass.setOrderStatus((OrderStatusIfc)getOrderStatus().clone());
        }
        if (tenderTransactionTotals != null)
        {
            newClass.setTenderTransactionTotals((TransactionTotalsIfc)getTenderTransactionTotals().clone());
        }
        if (payment != null)
        {
            newClass.setPayment((PaymentIfc)getPayment().clone());
        }

        for (int k = 0; k < this.getPaymentHistoryInfoCollection().size(); k++)
        {
            PaymentHistoryInfoIfc paymentHistoryInfo = getPaymentHistoryInfoCollection().get(k);
            newClass.addPaymentHistoryInfo((PaymentHistoryInfoIfc)paymentHistoryInfo.clone());
        }
        if (orderDeliveryDetailCollection != null)
        {
        	newClass.setDeliveryDetails(orderDeliveryDetailCollection);
        }
        if (orderRecipient != null)
        {
        	newClass.setOrderRecipient((OrderRecipientIfc)orderRecipient.clone());
        }
        newClass.setMaxStoreOrderLineReference(maxStoreOrderLineReference);
        newClass.setMaxXChannelOrderLineReference(maxXChannelOrderLineReference);
        newClass.setNewStoreOrderIDAssigned(newStoreOrderIdAssigned);
        newClass.setNewXChannelOrderIDAssigned(newXChannelOrderIdAssigned);
    }                                   // end setCloneAttributes()

    /**
     * Determine if two objects are identical.
     *
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = true;
        // confirm object instanceof this object
        if (obj instanceof OrderTransaction)
        {

            OrderTransaction c = (OrderTransaction)obj; // downcast the input
                                                        // object

            // compare all the attributes of OrderTransaction
            if (super.equals(obj)
                    && Util.isObjectEqual(getOrderStatus(), c.getOrderStatus())
                    && Util.isObjectEqual(getTenderTransactionTotals(), c.getTenderTransactionTotals())
                    && Util.isObjectEqual(getPayment(), c.getPayment())
                    && Util.isObjectEqual(this.paymentHistoryInfoCollection, c.getPaymentHistoryInfoCollection())
                    && Util.isObjectEqual(this.newStoreOrderIdAssigned, c.newStoreOrderIdAssigned)
                    && Util.isObjectEqual(this.newXChannelOrderIdAssigned, c.newXChannelOrderIdAssigned)
                    && (this.maxStoreOrderLineReference==c.maxStoreOrderLineReference)
                    && (this.maxXChannelOrderLineReference==c.maxXChannelOrderLineReference))
            {
                isEqual = true; // set the return code to true
            }
            else
            {
                isEqual = false; // set the return code to false
            }
        }
        else
        {
            isEqual = false;
        }
        return (isEqual);
    }

    /**
     * Determines which sub-status value to use as the "old" status.
     * <ul>
     * <li>If we are in a post void, then use the "previous" (breadcrumb)
     * status. <li>Otherwise use the "saved" status.
     * </ul>
     *
     * @param status The status structure containing the sub-status values.
     * @param transType The transaction type we are a part of.
     * @return The sub-status value to be used as the "old" status.
     */
    protected int determineOldStatus(EYSStatusIfc status, int transType)
    {
        int oldStatus = EYSStatusIfc.STATUS_UNDEFINED;

        if (transType == TYPE_VOID)
        {
            // Use the breadcrumb when we are being post voided.
            oldStatus = status.getPreviousStatus();
        }
        else
        {
            // Use the saved status.
            oldStatus = status.getSavedStatus();
        }

        return oldStatus;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.OrderTransactionIfc#isRefundDue()
     */
    public boolean isRefundDue()
    {
        if ((getTransactionType() == TYPE_ORDER_COMPLETE || getTransactionType() == TYPE_ORDER_CANCEL)
            && (calculateChangeDue().getDoubleValue() < 0))
        {
            return true;
        }
        return false;
    }

    /**
     * calculates the change due.
     *
     * @return changeDue as CurrencyIfc
     */
    public CurrencyIfc calculateChangeDue()
    {
        // For transactions with payments the change due is calculated by
        // subtracting the amount tender from the payment amount.
        CurrencyIfc changeDue = payment.getPaymentAmount().subtract(getCollectedTenderTotalAmount());
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

        if (getTransactionStatus() == TransactionIfc.STATUS_CANCELED)
        {
            financialTotals.setNumberCancelledTransactions(1);
            financialTotals.setAmountCancelledTransactions(totals.getSubtotal().subtract(totals.getDiscountTotal())
                    .abs());
        }
        else
        {
            switch (transactionType)
            {
                case TransactionIfc.TYPE_ORDER_INITIATE:
                    // do not record totals on suspended transaction
                    if (getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED)
                    {
                        if (getOrderType() == OrderConstantsIfc.ORDER_TYPE_SPECIAL)
                        {
                            financialTotals.addAmountSpecialOrderNew(getPayment().getPaymentAmount());
                        }
                        else if (getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND)
                        {   // cross channel order
                            //get total gross, tax and inclusive tax for all take with items
                            CurrencyIfc grossSale = getTotalExtendedDiscountSaleAmount(); //does not include tax
                            CurrencyIfc taxSale = getTotalTaxSaleAmount();
                            CurrencyIfc inclusiveTaxSale = getTotalInclusiveTaxSaleAmount();
                                      
                            //get transaction level financial totals for all take with item
                            financialTotals = getTakeWithItemsFinancialTotals(grossSale, taxSale, inclusiveTaxSale);
                            
                            //add line level financial totals for each take with item
                            financialTotals.add(getLineItemsFinancialTotals());
                            
                            //add order total
                            financialTotals.addAmountSpecialOrderNew(getTransactionTotals().getGrandTotal().subtract(getTotalSaleAmount()));
                        }
                    }
                    break;
                case TransactionIfc.TYPE_ORDER_PARTIAL:
                    financialTotals.addAmountSpecialOrderPartial
                    (getPayment().getPaymentAmount().subtract(getTotalSaleAmount()));
                    // these could be payment or refund
                    financialTotals.add(getLineItemsFinancialTotals());
                    financialTotals.add(getOrderFinancialTotals());
                    break;
                case TransactionIfc.TYPE_ORDER_COMPLETE:
                	// these could be payment or refund
                    financialTotals.addAmountOrderPayments
                    (getPayment().getPaymentAmount().subtract(getTotalSaleAmount()));
                    financialTotals.addCountOrderPayments(1);
                    financialTotals.add(getLineItemsFinancialTotals());
                    financialTotals.add(getOrderFinancialTotals());
                    break;
                case TransactionIfc.TYPE_ORDER_CANCEL:
                    financialTotals.addAmountOrderCancels
                      (getPayment().getPaymentAmount());
                    financialTotals.addCountOrderCancels(1);
                    break;
                default:
                    break;
            }

            // Add the rounded (cash) change amount to the financial totals object.
            if (getTenderTransactionTotals().getCashChangeRoundingAdjustment().signum() == CurrencyIfc.NEGATIVE)
            {
                financialTotals.addAmountChangeRoundedOut(getTenderTransactionTotals().getCashChangeRoundingAdjustment().abs());
            }
            if (getTenderTransactionTotals().getCashChangeRoundingAdjustment().signum() == CurrencyIfc.POSITIVE)
            {
                financialTotals.addAmountChangeRoundedIn(getTenderTransactionTotals().getCashChangeRoundingAdjustment().abs());
            }

            // get totals from tender line items
            financialTotals.add
              (getTenderFinancialTotals(getTenderLineItems(),
                                        getTenderTransactionTotals()));
        }

        setHouseCardEnrollmentCounts(financialTotals);
        
        return (financialTotals);
    }

    /**
     * Get transaction financial totals for all take with items.
     *
     * @return additive financial totals
     */
    public FinancialTotalsIfc getTakeWithItemsFinancialTotals(CurrencyIfc gross, CurrencyIfc tax, CurrencyIfc inclusiveTax)
    {
        //It is similar to getSaleReturnFinancialTotals() in SaleReturnTransaction.
        FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();  
        
        //The following code populate take with items in transaction financial totals.
        Vector<AbstractTransactionLineItemIfc> lineItemsVector = itemProxy.getLineItemsVector();
        Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator();
        SaleReturnLineItemIfc temp = null;
        while (i.hasNext())
        {
            // check for sale items with corresponding kit type
            temp = (SaleReturnLineItemIfc)i.next();
            if (temp.isPriceAdjustmentLineItem() && !temp.isOrderItem())  
            {
                financialTotals.addUnitsPriceAdjustments(new BigDecimal(1));
            }
        }
        // handle transaction values
        if (gross.signum() >=0 )
        {
            // set tax exempt, taxable sales
            // Note: at this time, tax exempt is only managed at the transaction
            // level
            // what if there is an item for return in order transaction?
            if (getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
            {
                financialTotals.addAmountGrossTaxExemptTransactionSales(gross);
                financialTotals.addCountGrossTaxExemptTransactionSales(1);
            }


            // isTaxableTransaction will return false for tax exempt
            if (isTaxableTransaction())
            {               
                financialTotals.addAmountGrossTaxableTransactionSales(gross);
                financialTotals.addCountGrossTaxableTransactionSales(1);
                financialTotals.addAmountTaxTransactionSales(tax);
                financialTotals.addAmountInclusiveTaxTransactionSales(inclusiveTax);
            }
            else
            {
                financialTotals.addAmountGrossNonTaxableTransactionSales(gross);
                financialTotals.addCountGrossNonTaxableTransactionSales(1);
            }
        }
        else
        {
            // set tax exempt, taxable sales
            // Note: at this time, tax exempt is only managed at the transaction
            // level
            if (getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
            {
                financialTotals.addAmountGrossTaxExemptTransactionReturns(gross.abs());
                financialTotals.addCountGrossTaxExemptTransactionReturns(1);
            }

            // isTaxableTransaction will return false for tax exempt
            if (isTaxableTransaction())
            {
                financialTotals.addAmountGrossTaxableTransactionReturns(gross.abs());
                financialTotals.addCountGrossTaxableTransactionReturns(1);
                financialTotals.addAmountTaxTransactionReturns(tax.abs());
                financialTotals.addAmountInclusiveTaxTransactionReturns(inclusiveTax.abs());
            }
            else
            {
                financialTotals.addAmountGrossNonTaxableTransactionReturns(gross.abs());
                financialTotals.addCountGrossNonTaxableTransactionReturns(1);
            }
        }

        // handle discount quantities
        // Transaction Amounts are updated in getLineItemsFinancialTotals() by
        // ItemPrice
        TransactionDiscountStrategyIfc[] transDiscounts = getTransactionDiscounts();
        if (transDiscounts != null)
        {
            for (int x = 0; x < transDiscounts.length; x++)
            {
                if (transDiscounts[x].getAssignmentBasis() == DiscountRuleIfc.ASSIGNMENT_EMPLOYEE)
                {
                    financialTotals.addUnitsGrossTransactionEmployeeDiscount(new BigDecimal(1));
                }
                else
                {
                    financialTotals.addNumberTransactionDiscounts(1);
                }
            }
        }

        // Add the round (cash) change amount to the financial totals object.
        if (getTransactionTotals().getCashChangeRoundingAdjustment().signum() == CurrencyIfc.NEGATIVE)
        {
            financialTotals.addAmountChangeRoundedOut(getTransactionTotals().getCashChangeRoundingAdjustment().abs());
        }
        if (getTransactionTotals().getCashChangeRoundingAdjustment().signum() == CurrencyIfc.POSITIVE)
        {
            financialTotals.addAmountChangeRoundedIn(getTransactionTotals().getCashChangeRoundingAdjustment().abs());
        }

        return (financialTotals);
    }
    
    /**
     * Get financial totals for all the pickup items and shipping charge.
     *
     * @return additive financial totals for line items
     */
    public FinancialTotalsIfc getLineItemsFinancialTotals()
    {
        FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();

        // loop through line items
        for (AbstractTransactionLineItemIfc li : getLineItemsVector())
        {
            // get references to line item, price, tax objects
            if (getTransactionType()==TransactionConstantsIfc.TYPE_ORDER_PARTIAL ||
                getTransactionType()==TransactionConstantsIfc.TYPE_ORDER_COMPLETE)
            {
                 //the transaction should be order pickup
                if ( li instanceof OrderLineItemIfc)
                {
                    OrderLineItemIfc sli = (OrderLineItemIfc)li;
                    if (sli.getItemStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_PICK_UP && !sli.isPriceCancelledDuringPickup())
                    {            
                        financialTotals.add(sli.getFinancialTotals());
                    }
                }
                else if (li instanceof SaleReturnLineItemIfc)
                {
                    //take with item
                    SaleReturnLineItemIfc sli = (SaleReturnLineItemIfc)li;
                    financialTotals.add(sli.getFinancialTotals());
                }
            }
            else if (getTransactionType()==TransactionConstantsIfc.TYPE_ORDER_INITIATE)
            {
                //This is order initiate
               SaleReturnLineItemIfc sli = (SaleReturnLineItemIfc)li;
               if (sli.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE)
               {
                   //take with item
                   financialTotals.add(sli.getFinancialTotals());
               }
            }
        }
        
        return (financialTotals);
    }
    


    /**
     * Retrieves order status reference.
     *
     * @return order status reference
     */
    public OrderStatusIfc getOrderStatus()
    {
        return (orderStatus);
    }

    /**
     * Sets order status reference.
     *
     * @param value order status reference
     */
    public void setOrderStatus(OrderStatusIfc value)
    {
        orderStatus = value;
    }

    /**
     * Retrieves order identifier. This method overrides the corresponding
     * method in SaleReturnTransaction.
     *
     * @return order identifier
     */
    public String getOrderID()
    {
        return (getOrderStatus().getOrderID());
    }

    /**
     * Sets order identifier.
     *
     * @param value order identifier
     */
    public void setOrderID(String value)
    {
        getOrderStatus().setOrderID(value);
    }

    /**
     * Assign a new order id to this order transaction
     * @param orderID the order id
     * @param xchannelOrderID a boolean flag indicating if
     * the order id is a cross channel one or not
     */
    public void assignNewOrderID(String orderID, boolean xchannelOrderID)
    {
        getOrderStatus().setOrderID(orderID);
        if (xchannelOrderID)
        {
            setNewXChannelOrderIDAssigned(orderID);
        }
        else
        {
            setNewStoreOrderIDAssigned(orderID);
        }
    }

    /**
     * Resync new order identifier from the assigned order ids
     *
     * @return the order id after resync
     */
    public String resyncNewOrderID()
    {
        String orderID = null;
        if (containsXChannelOrderLineItem())
        {
            orderID = getNewXChannelOrderIDAssigned();
        }
        else
        {
            orderID = getNewStoreOrderIDAssigned();
        }
        getOrderStatus().setOrderID(orderID);

        return orderID;
    }

    /**
     * Retrieves payment reference.
     *
     * @return payment reference
     */
    public PaymentIfc getPayment()
    {
        return (payment);
    }

    /**
     * Sets payment reference.
     *
     * @param value payment reference
     */
    public void setPayment(PaymentIfc value)
    {
        payment = value;
        if (getTenderLineItemsVector().size() == 0) // totals to tender for
                                                    // deposit
        {
            updateTenderTotals(payment);
        }
        else
        // use totals read from db based upon payment
        {
            tenderTransactionTotals.updateTransactionTotalsForPayment(payment.getPaymentAmount());
        }
    }

    /**
     * Retrieves tender transaction totals.
     *
     * @return tender transaction totals
     */
    public TransactionTotalsIfc getTenderTransactionTotals()
    {
        return (tenderTransactionTotals);
    }

    /**
     * Sets tender transaction totals.
     *
     * @param value tender transaction totals
     */
    public void setTenderTransactionTotals(TransactionTotalsIfc value)
    {
        tenderTransactionTotals = value;
    }

    /**
     * Leaves the transaction type as TYPE_ORDER_INITIATE or TYPE_ORDER_COMPLETE
     * or TYPE_ORDER_CANCEL.
     */
    protected void resetTransactionType()
    {
        // leave current type either TYPE_ORDER_INITIATE or
        // TYPE_ORDER_COMPLETE or TYPE_ORDER_CANCEL
    }

    /**
     * Update totals in the tenderTotals object
     */
    @Override
    public void updateTenderTotals()
    {
        super.updateTenderTotals();
        getTenderTransactionTotals().updateTenderTotals(getTenderLineItems());
    }

    /**
     * Updates TenderTotals when payment is set.
     *
     * @param payment The payment
     */
    protected void updateTenderTotals(PaymentIfc payment)
    {
        // Updates tenderTotals with payment amounts
        TransactionTotalsIfc tenderTotals = getTenderTransactionTotals();

        tenderTotals.setGrandTotal(payment.getPaymentAmount());
        tenderTotals.setSubtotal(payment.getPaymentAmount());
        tenderTotals.setBalanceDue(payment.getPaymentAmount());
    }

    /**
     * Adds tender line item.
     *
     * @param item oracle.retail.stores.domain.tender.TenderLineItemIfc The
     *            tender line item to be added
     * @exception IllegalArgumentException if tender line item cannot be added
     */
    public void addTenderLineItem(TenderLineItemIfc item) throws IllegalArgumentException
    {
        super.addTenderLineItem(item);
        getTenderTransactionTotals().updateTenderTotals(getTenderLineItems());
    }

    /**
     * Adds tender line item.
     *
     * @param item oracle.retail.stores.domain.tender.TenderLineItemIfc The
     *            tender line item to be added
     * @exception IllegalArgumentException if tender line item cannot be added
     */
    public void addTender(TenderLineItemIfc item)
    {
        super.addTender(item);
        getTenderTransactionTotals().updateTenderTotals(getTenderLineItems());
    }

    /**
     * Removes tender line item.
     *
     * @param tenderToRemove The tender line item to be removed
     */
    public void removeTenderLineItem(TenderLineItemIfc tenderToRemove)
    {
        super.removeTenderLineItem(tenderToRemove);
        getTenderTransactionTotals().updateTenderTotals(getTenderLineItems());
    }

    /**
     * Set tender line items array and update totals. Overrides method in
     * abstractTenderableTransaction.
     *
     * @param tli array tender line items
     */
    public void setTenderLineItems(TenderLineItemIfc[] tli)
    {
        super.setTenderLineItems(tli);
        // update totals
        getTenderTransactionTotals().updateTenderTotals(getTenderLineItems());
    }

    /**
     * Remove a tender line from the transaction.
     *
     * @param index tender line item index to remove
     */
    public void removeTenderLineItem(int index)
    {
        super.removeTenderLineItem(index);
        getTenderTransactionTotals().updateTenderTotals(getTenderLineItems());
    }

    /**
     * Returns array of order line items.
     *
     * @return array of order line items
     */
    public OrderLineItemIfc[] getOrderLineItems()
    {
        return (getItemContainerProxy().getOrderLineItems());
    }

    /**
     * Prorates deposit amount across line items based on extended discounted
     * selling price.
     */
    public void prorateDeposit()
    {
        CurrencyIfc storeOrderDepositAmount = getOrderStatus().getStoreOrderDepositAmount();
        CurrencyIfc xChannelDepositAmount = getOrderStatus().getXChannelDepositAmount();
        CurrencyIfc xChannelTotal = getOrderStatus().getXChannelTotal();
        CurrencyIfc storeOrderTotal = getOrderStatus().getStoreOrderTotal();

        prorateDeposit(storeOrderTotal, storeOrderDepositAmount, false);
        prorateDeposit(xChannelTotal, xChannelDepositAmount, true);
    }

    /**
     * Prorates deposit amount across line items based on extended discounted
     * selling price.
     */
    public void prorateDeposit(CurrencyIfc total, CurrencyIfc depositAmount, boolean isXChannel)
    {
        @SuppressWarnings("rawtypes")
        Iterator itemIterator = getItemContainerProxy().getLineItemsIterator();
        AbstractTransactionLineItemIfc lineItem = null;
        SaleReturnLineItemIfc srLineItem = null;
        OrderItemStatusIfc orderItemStatus = null;

        // if deposit is zero, no calculations are necessary.
        // Just set zero on each lineitem
        if (depositAmount.signum() == 0)
        {
            while (itemIterator.hasNext())
            {
                lineItem = (AbstractTransactionLineItemIfc)itemIterator.next();
                if (lineItem instanceof SaleReturnLineItemIfc)
                {
                    srLineItem = (SaleReturnLineItemIfc)lineItem;
                    orderItemStatus = srLineItem.getOrderItemStatus();
                    if ((orderItemStatus != null) && (orderItemStatus.isCrossChannelItem() == isXChannel))
                    {
                        orderItemStatus.setDepositAmount(DomainGateway.getBaseCurrencyInstance());
                    }
                }
            }
        }
        else
        {
            CurrencyIfc itemDeposit = null;
            CurrencyIfc lineAmount = null;
            while (itemIterator.hasNext())
            {
                lineItem = (AbstractTransactionLineItemIfc)itemIterator.next();
                if (lineItem instanceof SaleReturnLineItemIfc)
                {
                    srLineItem = (SaleReturnLineItemIfc)lineItem;
                    orderItemStatus = srLineItem.getOrderItemStatus();
                    if ((orderItemStatus != null) && (orderItemStatus.isCrossChannelItem() == isXChannel))
                    {
                        // get line total and prorate across the entire amount plus tax
                        lineAmount = (CurrencyIfc)srLineItem.getItemPrice().getItemTotal().clone();
                        if ((getOrderStatus().getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND) &&
                            (orderItemStatus.getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE))
                        {
                            // on hand orders that are sold have to be paid in full
                            itemDeposit = lineAmount;
                        }
                        else
                        {
                            itemDeposit = depositAmount.prorate(lineAmount, total);
                            srLineItem.getOrderItemStatus().setDepositAmount(itemDeposit);
                        }
                        total = total.subtract(lineAmount);
                        depositAmount = depositAmount.subtract(itemDeposit);
                        lineAmount.setZero();
                    }
                }
            }
        }
    }

    /**
     * Derives the additive financial totals for an order complete transaction,
     * not including line items and tenders .
     *
     * @return additive financial totals
     */
    protected FinancialTotalsIfc getOrderFinancialTotals()
    {
        FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();

        // gross total is item subtotal with discount applied for picked up
        // items
        CurrencyIfc tax = DomainGateway.getBaseCurrencyInstance("0.0");
        CurrencyIfc inclusiveTax = DomainGateway.getBaseCurrencyInstance("0.0");
        CurrencyIfc gross = DomainGateway.getBaseCurrencyInstance("0.0");

        AbstractTransactionLineItemIfc[] items = getLineItems();
        for (int i = 0; i < items.length; i++)
        {
            if (items[i] instanceof OrderLineItemIfc)
            {
                OrderLineItemIfc item = (OrderLineItemIfc)items[i];
                if (item.getItemStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_PICK_UP && !item.isPriceCancelledDuringPickup())
                {
                    tax = tax.add(item.getItemPrice().getItemTaxAmount());
                    inclusiveTax = inclusiveTax.add(item.getItemInclusiveTaxAmount());
                    gross = gross.add(item.getItemPrice().getExtendedDiscountedSellingPrice());
                }
            }
            else if (items[i] instanceof SaleReturnLineItemIfc)
            {
                SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)items[i];
                tax = tax.add(item.getItemPrice().getItemTaxAmount());
                inclusiveTax = inclusiveTax.add(item.getItemInclusiveTaxAmount());
                gross = gross.add(item.getItemPrice().getExtendedDiscountedSellingPrice());
            }
        }

        // if tax is positive value, add to taxable
        // Note: tax exempt stuff is counted as non-taxable for now
        // TEC need to see if tax is zero to handle returns
        if (isTaxableTransaction())
        {
            financialTotals.addAmountTaxTransactionSales(tax);
            financialTotals.addAmountInclusiveTaxTransactionSales(inclusiveTax);
            financialTotals.addAmountGrossTaxableTransactionSales(gross);
            financialTotals.addCountGrossTaxableTransactionSales(1);
        }
        else
        {
            financialTotals.addAmountGrossNonTaxableTransactionSales(gross);
            financialTotals.addCountGrossNonTaxableTransactionSales(1);
        }

        return (financialTotals);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SaleReturnTransaction#toString()
     */
    @Override
    public String toString()
    {
        // build result string
        StringBuilder strResult =
          Util.classToStringHeader("OrderTransaction",
                                    getRevisionNumber(),
                                    hashCode());
        strResult.append(Util.formatToStringEntry("order status",
                                                  getOrderStatus()))
                 .append(Util.formatToStringEntry("payment",
                                                  getPayment()))
                 .append(Util.formatToStringEntry("tender transaction totals",
                                                  getTenderTransactionTotals()))
                 .append(Util.formatToStringEntry("order delivery detail",
                		                          getDeliveryDetails()))
                 .append(Util.formatToStringEntry("order recipient detail",
                		                          getOrderRecipient()))
                 .append(Util.formatToStringEntry("store order id assigned",
                                                  getNewStoreOrderIDAssigned()))
                 .append(Util.formatToStringEntry("xc order id assigned",
                                                  getNewXChannelOrderIDAssigned()))
                 .append(super.toString());

        // pass back result
        return (strResult.toString());
    }

    /**
     * Retrieves the source-code-control system revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /**
     * Sets customer email address
     *
     * @param email The customer email address
     */
    public void setOrderCustomerEmailAddress(String email)
    {
        customerEmailAddress = email;
    }

    /**
     * Retrieve customer email address
     *
     * @return the email address
     */
    public String getOrderCustomerEmailAddress()
    {
        return customerEmailAddress;
    }

    /**
     * Convenience method to get the phone number from the first order
     * delivery. May return null if {@link #getDeliveryDetails()} is empty.
     *
     * @return the email address
     */
    public PhoneIfc getOrderCustomerPhone()
    {
        PhoneIfc orderPhone = null;
        if (getCustomer() != null)
        {
            orderPhone = getCustomer().getPrimaryPhone();
        }
        if (orderPhone == null)
        {
            for (OrderDeliveryDetailIfc detail : getDeliveryDetails())
            {
                orderPhone = detail.getContactPhone();
                if (orderPhone != null)
                {
                    break;
                }
            }
        }
        return orderPhone;
    }

    /**
     * Retrieve the order description
     *
     * @return the order description
     */
    public String getOrderDescription()
    {
        return description;
    }

    /**
     * Sets the order description.
     *
     * @param desc The order description.
     */
    public void setOrderDescription(String desc)
    {
        description = desc;
    }

    /**
     * Sets order decription from first line item, if present
     */
    public void setOrderDescriptionFromLineItem()
    {
        OrderLineItemIfc[] orderItems = getOrderLineItems();

        String orderDescription = " ";

        if (orderItems.length > 0)
        {
            // The Order is transactional data. Since it's store related, we use
            // the default locale of the item as the description.
            orderDescription = orderItems[0].getItemDescription(LocaleMap.getLocale(LocaleMap.DEFAULT));
        }

        setOrderDescription(orderDescription);
    }

    /**
     * Adds payment history info
     *
     * @param paymentHistoryInfo
     */
    public void addPaymentHistoryInfo(PaymentHistoryInfoIfc paymentHistoryInfo)
    {
        this.paymentHistoryInfoCollection.add(paymentHistoryInfo);
    }

    /**
     * Gets the payment history info
     *
     * @return List collection of payment history info
     */
    public List<PaymentHistoryInfoIfc> getPaymentHistoryInfoCollection()
    {
        return (this.paymentHistoryInfoCollection);
    }

    /**
     * Adds delivery detail
     *
     * @param deliveryDetail
     */
    public void addDeliveryDetail(OrderDeliveryDetailIfc deliveryDetail)
    {
        orderDeliveryDetailCollection.add(deliveryDetail);

    }

    /**
     * Gets the delivery detail
     *
     * @return List collection of delivery detail
     */
    public Collection<OrderDeliveryDetailIfc> getDeliveryDetails()
    {
        return orderDeliveryDetailCollection;
    }

    /**
     * Gets the order recipient
     *
     * @return the order recipient
     */
    public OrderRecipientIfc getOrderRecipient()
    {
        return orderRecipient;
    }

    /**
     * Initializes Pickup Delivery Order with super class attributes.
     *
     * @param transaction
     */
    public void initializeOrderFromSaleTransaction(SaleReturnTransaction transaction)
    {
        transaction.setCloneAttributes(this);
        orderStatus.initializeStatus();
        orderStatus.setInitiatingChannel(OrderConstantsIfc.ORDER_CHANNEL_STORE);
        orderStatus.setTimestampBegin();
        orderStatus.setTimestampCreated();
        orderStatus.setInitialTransactionBusinessDate(this.getBusinessDay());
        orderStatus.setInitialTransactionID(this.getTransactionIdentifier());
        orderStatus.setRecordingTransactionID(this.getTransactionIdentifier());
        orderStatus.setRecordingTransactionBusinessDate(this.getBusinessDay());
        orderStatus.setOrderType(OrderConstantsIfc.ORDER_TYPE_ON_HAND);
        this.setTransactionType(TransactionIfc.TYPE_ORDER_INITIATE);

    }
    /**
     * Set order delivery detail collection.
     *
     * @param deliveryDetails Collection OrderDeliveryDetailIfc
     */
    public void setDeliveryDetails(Collection<OrderDeliveryDetailIfc> deliveryDetails)
    {
        this.orderDeliveryDetailCollection = deliveryDetails;

    }

    /**
     * Set order recipient.
     *
     * @param orderRecipient the OrderRecipientIfc
     */
    public void setOrderRecipient(OrderRecipientIfc orderRecipient)
    {
        this.orderRecipient = orderRecipient;

    }

    /**
     * Gets the OrderType.
     *
     * @return The orderType.
     */
    public int getOrderType()
    {
        return orderStatus.getOrderType();
    }

    /**
     * Sets the OrderType
     *
     * @param orderType
     */
    public void setOrderType(int orderType)
    {
        orderStatus.setOrderType(orderType);
    }

    /**
     * The method calculates total amount for sale items in the Pickup/Delivery
     * Order. Calculated sale amount will be used to calculate balance due for
     * the receipt printing.
     *
     * @return additive sale totals
     */
    public CurrencyIfc getTotalSaleAmount()
    {
        @SuppressWarnings("rawtypes")
        Iterator itemIterator = getItemContainerProxy().getLineItemsIterator();
        AbstractTransactionLineItemIfc lineItem = null;
        SaleReturnLineItemIfc srLineItem = null;
        CurrencyIfc lineAmount = null;
        CurrencyIfc totalSaleAmount = DomainGateway.getBaseCurrencyInstance("0.00");
        while (itemIterator.hasNext())
        {
            lineItem = (AbstractTransactionLineItemIfc)itemIterator.next();
            if (lineItem instanceof SaleReturnLineItemIfc)
            {
                srLineItem = (SaleReturnLineItemIfc)lineItem;
                if (getOrderStatus().getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND
                    && (srLineItem.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE 
                    && (!srLineItem.isKitHeader())))
                {
                    lineAmount = srLineItem.getItemPrice().getItemTotal();
                    totalSaleAmount = totalSaleAmount.add(lineAmount);
                }
            }
        }
        return totalSaleAmount;
    }
    
    /**
     * This method is only used at cross channel order initiate
     * @return sum of take with items discounted sale amount which does not include tax
     */
    protected CurrencyIfc getTotalExtendedDiscountSaleAmount()
    {
        @SuppressWarnings("rawtypes")
        Iterator itemIterator = getItemContainerProxy().getLineItemsIterator();
        AbstractTransactionLineItemIfc lineItem = null;
        SaleReturnLineItemIfc srLineItem = null;
        CurrencyIfc lineAmount = null;
        CurrencyIfc totalSaleAmount = DomainGateway.getBaseCurrencyInstance("0.00");
        while (itemIterator.hasNext())
        {
            lineItem = (AbstractTransactionLineItemIfc)itemIterator.next();
            if (lineItem instanceof SaleReturnLineItemIfc)
            {
                srLineItem = (SaleReturnLineItemIfc)lineItem;
                if (getOrderStatus().getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND
                        && (srLineItem.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE && (!srLineItem.isKitHeader())))
                {
                    lineAmount = srLineItem.getItemPrice().getExtendedDiscountedSellingPrice();
                    totalSaleAmount = totalSaleAmount.add(lineAmount);
                }
            }
        }
        return totalSaleAmount;
    }
    
    /**
     * This method is only used at cross channel order initiate
     * @return sum of take with items tax amount
     */
    public CurrencyIfc getTotalTaxSaleAmount()
    {
        @SuppressWarnings("rawtypes")
        Iterator itemIterator = getItemContainerProxy().getLineItemsIterator();
        AbstractTransactionLineItemIfc lineItem = null;
        SaleReturnLineItemIfc srLineItem = null;
        CurrencyIfc lineTaxAmount = null;
        CurrencyIfc totalTaxAmount = DomainGateway.getBaseCurrencyInstance("0.00");
        while (itemIterator.hasNext())
        {
            lineItem = (AbstractTransactionLineItemIfc)itemIterator.next();
            if (lineItem instanceof SaleReturnLineItemIfc)
            {
                srLineItem = (SaleReturnLineItemIfc)lineItem;
                if (getOrderStatus().getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND
                        && (srLineItem.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE && (!srLineItem.isKitHeader())))
                {
                    lineTaxAmount = srLineItem.getItemPrice().getItemTaxAmount();
                    totalTaxAmount = totalTaxAmount.add(lineTaxAmount);
                }
            }
        }
        return totalTaxAmount;
    }
    
    /**
     * This method is only used at cross channel order initiate
     * @return sum of take with items inclusive tax amount
     */
    public CurrencyIfc getTotalInclusiveTaxSaleAmount()
    {
        @SuppressWarnings("rawtypes")
        Iterator itemIterator = getItemContainerProxy().getLineItemsIterator();
        AbstractTransactionLineItemIfc lineItem = null;
        SaleReturnLineItemIfc srLineItem = null;
        CurrencyIfc itemInclusiveTaxAmount = null;
        CurrencyIfc totalItemInclusiveTaxAmount = DomainGateway.getBaseCurrencyInstance("0.00");
        while (itemIterator.hasNext())
        {
            lineItem = (AbstractTransactionLineItemIfc)itemIterator.next();
            if (lineItem instanceof SaleReturnLineItemIfc)
            {
                srLineItem = (SaleReturnLineItemIfc)lineItem;
                if (getOrderStatus().getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND
                        && (srLineItem.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE && (!srLineItem.isKitHeader())))
                {
                    itemInclusiveTaxAmount = srLineItem.getItemPrice().getItemInclusiveTaxAmount();
                    totalItemInclusiveTaxAmount = totalItemInclusiveTaxAmount.add(itemInclusiveTaxAmount);
                }
            }
        }
        return totalItemInclusiveTaxAmount;
    }


    /**
     * Adds a transaction discount and optionally updates the transaction
     * totals.
     *
     * @param disc TransactionDiscountStrategyIfc
     * @param doUpdate flag indicating transaction totals should be updated
     */
    public void addTransactionDiscount(TransactionDiscountStrategyIfc disc, boolean doUpdate)
    {
        itemProxy.addTransactionDiscount(disc);
        if (doUpdate)
        {
            // update totals
            updateTransactionTotalsWithTransDiscount(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                    .getTransactionTax());
        }
    }

    /**
     * Updates transaction totals and tenderTransactionTotals and resets transaction type.
     *
     * @param lineItems array of line items
     * @param discounts array of transaction discounts
     * @param tax transaction tax object
     */
    protected void updateTransactionTotalsWithTransDiscount(AbstractTransactionLineItemIfc[] lineItems,
            TransactionDiscountStrategyIfc[] discounts, TransactionTaxIfc tax)
    {
        totals.updateTransactionTotals(lineItems, discounts, tax);
        resetTransactionType();
        
        // For an order pickup/cancel transaction, update the order status & payment 
        // to reflect changes in transaction totals due to repricing or adding of
        // take with items
        if (isOrderPickupOrCancel())
        {
            updatePickupCancelOrderPayment(lineItems);
        }
        else
        {
            TransactionTotalsIfc tenderTotals = getTenderTransactionTotals();
            tenderTotals.setBalanceDue(totals.getBalanceDue());
            tenderTotals.setGrandTotal(totals.getGrandTotal());
            CurrencyIfc xcDepositAmount = getXChannelGrandTotal();
            orderStatus.setXChannelDepositAmount(xcDepositAmount);
            orderStatus.setStoreOrderDepositAmount(tenderTotals.getGrandTotal().subtract(xcDepositAmount));
            if(payment != null && orderStatus.getDepositAmount() != null)
            {
                payment.setPickupDeliveryItemDepositAmount(orderStatus.getDepositAmount().subtract(getTotalSaleAmount()));
                payment.setPaymentAmount(orderStatus.getDepositAmount());
            } 
        }
    }
    
    /**
     * Updates transaction totals and resets transaction type.
     *
     * @param lineItems array of line items
     * @param discounts array of transaction discounts
     * @param tax transaction tax object
     */
    protected void updateTransactionTotals(AbstractTransactionLineItemIfc[] lineItems,
            TransactionDiscountStrategyIfc[] discounts, TransactionTaxIfc tax)
    {
        super.updateTransactionTotals(lineItems, discounts, tax);
        
        // For an order pickup/cancel transaction, update the order status & payment 
        // to reflect changes in transaction totals due to repricing or adding of
        // take with items
        if (isOrderPickupOrCancel())
        {
            updatePickupCancelOrderPayment(lineItems);
        }
    }

    /**
     * Adds a transaction discount and updates the transaction totals during
     * the tender process.
     *
     * The order transaction has two sets of totals; one tracks SaleReturnTransaction totals,
     * which this class extends, and the other tracks the Order specific totals.  Both sets
     * of totals must be updated by the change caused by the discount.  However, updating
     * these totals, undoes some of the values that are unique to the Order settings.
     * This method recalcuates values specifically for the set of Order transaction totals.
     *
     * @param disc TransactionDiscountStrategyIfc
     */
    public void addTransactionDiscountDuringTender(TransactionDiscountStrategyIfc disc)
    {
        itemProxy.addTransactionDiscount(disc);
        totals.updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                .getTransactionTax());

        resetTransactionType();

        // For an order pickup/cancel transaction, update the order status & payment 
        // to reflect changes in transaction totals due to repricing or adding of
        // take with items
        if (isOrderPickupOrCancel())
        {
            updatePickupCancelOrderPayment(itemProxy.getLineItems());
        }
        else
        {
            // Somewhere in the call tenderTransactionTotals.updateTransactionTotals(),
            // the orderStatus.depositAmount gets lost;
            // Save a copy for refiguring the other values.
            CurrencyIfc depositAmount = DomainGateway.getBaseCurrencyInstance();
            CurrencyIfc xcDepositAmount = DomainGateway.getBaseCurrencyInstance();
            if (orderStatus.getDepositAmount() != null)
            {
                depositAmount = (CurrencyIfc)orderStatus.getDepositAmount().clone();
                xcDepositAmount = (CurrencyIfc)orderStatus.getXChannelDepositAmount().clone();
            }
            tenderTransactionTotals.updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                    .getTransactionTax());

            // It is possible that the total value of the transaction has been reduce to less
            // than the deposit amount entered. If so, set the deposit amount to the grand total.
            CurrencyIfc xcGrandTotal = getXChannelGrandTotal();
            if (totals.getGrandTotal().compareTo(depositAmount) == CurrencyIfc.LESS_THAN)
            {
                depositAmount = totals.getGrandTotal();
                xcDepositAmount = xcGrandTotal;
            }

            // Set the grand total and balance due back to the deposit amount
            tenderTransactionTotals.setGrandTotal(depositAmount);
            tenderTransactionTotals.setBalanceDue(depositAmount);

            // Recalculate the values for the the order status and payment objects
            CurrencyIfc saleAmount = getTotalSaleAmount();
            CurrencyIfc balanceDue = totals.getGrandTotal().subtract(depositAmount);
            CurrencyIfc xcBalanceDue = xcGrandTotal.subtract(xcDepositAmount);
            orderStatus.setXChannelDepositAmount((CurrencyIfc)xcDepositAmount.clone());
            orderStatus.setStoreOrderDepositAmount((CurrencyIfc)depositAmount.subtract(xcDepositAmount).clone());
            orderStatus.setSaleAmount((CurrencyIfc)saleAmount.clone());
            orderStatus.setXChannelBalanceDue((CurrencyIfc)xcBalanceDue.clone());
            orderStatus.setStoreOrderBalanceDue((CurrencyIfc)balanceDue.subtract(xcBalanceDue).clone());
            orderStatus.setXChannelTotal(xcGrandTotal);
            orderStatus.setStoreOrderTotal(depositAmount.subtract(xcGrandTotal));

            if(payment != null)
            {
                payment.setPickupDeliveryItemDepositAmount(depositAmount.subtract(saleAmount));
                payment.setPaymentAmount((CurrencyIfc)depositAmount.clone());
                payment.setBalanceDue((CurrencyIfc)balanceDue.clone());
            }
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.OrderTransactionIfc#containsCanceledOrderLineItems()
     */
    public boolean containsCanceledOrderLineItems()
    {
        boolean canceledItemsExist = false;
        OrderLineItemIfc[] items = getOrderLineItems();
        if(items != null)
        {
            boolean done = false;
            for(int i = 0; i < items.length && !done; i++)
            {
                if (items[i].getItemStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_CANCEL)
                {
                    canceledItemsExist = true;
                    done = true;
                }
            }
        }
        return canceledItemsExist;
    }

    /**
     * Returns the transaction type description for special orders/PDOs.
     *
     * @return String The order transaction type description.
     */
    public String getTransactionTypeDescription()
    {
        String trnName = "ORDER";
        if (getOrderStatus().getOrderType() == OrderConstantsIfc.ORDER_TYPE_SPECIAL)
        {
            trnName = super.getTransactionTypeDescription();
        }
        else
        {
            switch (getTransactionType())
            {
                case TransactionConstantsIfc.TYPE_ORDER_INITIATE:
                    trnName = "ORDER_INITIATE";
                    break;
                case TransactionConstantsIfc.TYPE_ORDER_PARTIAL:
                    trnName = "ORDER_PARTIAL";
                    break;
                case TransactionConstantsIfc.TYPE_ORDER_COMPLETE:
                    trnName = "ORDER_COMPLETE";
                    break;
                case TransactionConstantsIfc.TYPE_ORDER_CANCEL:
                    trnName = "ORDER_CANCEL";
                    break;
                default:
                    break;
            }
        }

        return trnName;
    }

    /**
     * @return the new order id used for store order
     */
    public String getNewStoreOrderIDAssigned()
    {
        return newStoreOrderIdAssigned;
    }

    /**
     * Sets the new order id used for store order
     * @param storeOrderId
     */
    protected void setNewStoreOrderIDAssigned(String orderId)
    {
        this.newStoreOrderIdAssigned = orderId;
    }

    /**
     * @return the new order id used for cross channel order
     */
    public String getNewXChannelOrderIDAssigned()
    {
        return newXChannelOrderIdAssigned;
    }

    /**
     * Sets the new order id used for cross channel order
     * @param channelOrderId
     */
    protected void setNewXChannelOrderIDAssigned(String orderId)
    {
        this.newXChannelOrderIdAssigned = orderId;
    }
    
    /**
     * return true if the transaction is cross channel order pickup or order cancel. Otherwise, return false
     * @return
     */
    public boolean isOrderPickupOrCancel()
    {
        if (transactionType==TransactionConstantsIfc.TYPE_ORDER_COMPLETE ||
            transactionType==TransactionConstantsIfc.TYPE_ORDER_PARTIAL ||
            transactionType==TransactionConstantsIfc.TYPE_ORDER_CANCEL )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    //---------------------------------------------------------------------
    /**
     * Update the order status and payment of an order pickup/cancel 
     * transaction due to repricing or adding of take with items
     * 
     * @param lineItems the sale line items
     */
    //---------------------------------------------------------------------
    protected void updatePickupCancelOrderPayment(AbstractTransactionLineItemIfc[] lineItems)
    {       
       // calculate order payment
       CurrencyIfc paymentAmount = DomainGateway.getBaseCurrencyInstance(); 
       CurrencyIfc saleAmount = DomainGateway.getBaseCurrencyInstance(); 
       for (AbstractTransactionLineItemIfc lineItem : getLineItems())
       {
           SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)lineItem;
           if (srli.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE)
           {
               // take with items totals 
               paymentAmount = paymentAmount.add(srli.getItemPrice().getItemTotal());  
               saleAmount = saleAmount.add(srli.getItemPrice().getItemTotal());
           }
           else
           {
               OrderLineItemIfc orli = (OrderLineItemIfc) srli;
               if (orli.isPriceCancelledDuringPickup())
               {
                   //For a pickup item that is repriced in store, donot include
                   //its original price in the total payment amount
                   continue;
               }
               else if (orli.isInStorePriceDuringPickup())
               {
                   // An in-store priced pickup item has no deposit.
                   // set item balance due to be the item total
                   orli.setItemBalanceDue(orli.getItemPrice().getItemTotal());
               }
               
               paymentAmount = paymentAmount.add(orli.getItemBalanceDue()); 
           }   
       }
       
       // update order payment
       PaymentIfc payment = getPayment();
       if (!paymentAmount.equals(CurrencyIfc.ZERO) && payment == null)
       {
           payment = DomainGateway.getFactory().getPaymentInstance(LocaleMap.getLocale(LocaleMap.DEFAULT));
           payment.setTransactionID(getTransactionIdentifier());
           payment.setBusinessDate(getBusinessDay());
       }
       if (payment != null)
       {
           payment.setPaymentAmount(paymentAmount);
           payment.setPickupDeliveryItemDepositAmount(paymentAmount.subtract(saleAmount));
           setPayment(payment); // must call this to update transaction tender totals
       }

    }
    
    /** 
     * get max store line reference. When a new take with item is created, its line reference should be maxStoreOrderLineReference plus one.
     * @return
     */
    public int getMaxStoreOrderLineReference() 
    {
        return maxStoreOrderLineReference;
    }

    /**
     * set max store line reference number. This is the largest line reference number in the order transaction
     * @param maxStoreOrderLineReference
     */
    public void setMaxStoreOrderLineReference(int maxStoreOrderLineReference) 
    {
        this.maxStoreOrderLineReference = maxStoreOrderLineReference;
    }

    /** 
     * get max xchannel line reference. When an in-store priced item is created, its line reference should be maxXChannelOrderLineReference plus one.
     * @return
     */
    public int getMaxXChannelOrderLineReference() 
    {
        return maxXChannelOrderLineReference;
    }

   /**
    * set max xchannel line reference number. This is the largest xchannel line reference number in the order transaction
    * @param maxXChannelOrderLineReference
    */
    public void setMaxXChannelOrderLineReference(int maxXChannelOrderLineReference) 
    {
        this.maxXChannelOrderLineReference = maxXChannelOrderLineReference;
    }  
    
}

