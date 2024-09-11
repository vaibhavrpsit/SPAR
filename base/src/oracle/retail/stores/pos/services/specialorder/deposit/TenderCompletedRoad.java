/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/deposit/TenderCompletedRoad.java /main/39 2014/07/17 15:09:41 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   07/15/14 - Set original line item number for order line.
 *    sgu       06/22/14 - insert discount and tax status for take with items
 *                         added during pickup
 *    yiqzhao   06/11/14 - Set original transaction id for order line item
 *                         status.
 *    yiqzhao   04/30/14 - Remove a dead branch.
 *    vtemker   06/11/13 - Fixed Unique constraint SQL errors when retrieving
 *                         suspended Delivery orders
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    sgu       01/14/13 - process pickup or cancel for store order items
 *    sgu       01/07/13 - initiate order item status
 *    jswan     12/06/12 - Modified to support JDBC opertions for order tax and
 *                         discount status.
 *    sgu       11/26/12 - set desposition code for special order item
 *    sgu       11/26/12 - set pickup store ID
 *    sgu       11/26/12 - set pickup store ID for sale item
 *    sgu       11/21/12 - set order item discount and tax status during order
 *                         initiate
 *    sgu       11/14/12 - added discount and tax pickup view
 *    sgu       11/07/12 - added captured order line item
 *    sgu       10/25/12 - add filled status for order and order item
 *    sgu       10/18/12 - carry over amounts first
 *    sgu       10/18/12 - set order item status
 *    sgu       10/16/12 - set ordered amount for each order line item
 *    sgu       10/16/12 - clean up order item quantities
 *    sgu       09/19/12 - accumulate completed and cancelled amount
 *    sgu       06/22/12 - refactor order id assignment
 *    sgu       05/24/12 - check storeOrderLineNumber before adding it
 *    sgu       05/24/12 - set xchannel order item reference
 *    sgu       05/15/12 - remove column LN_ITM_REF from order line item tables
 *    sgu       05/08/12 - set order completed status only if the order
 *                         contains non-xc order item
 *    sgu       05/08/12 - prorate store order and xchannel deposit amount
 *                         separatly
 *    sgu       05/04/12 - refactor OrderStatus to support store order and
 *                         xchannel order
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/05/10 - use default locale for currency and date/time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    npoola    03/01/09 - added the condition check for the ON_HAND for
 *                         complete status
 *    npoola    02/26/09 - moved the logic from JDBCSaveTransaction to the
 *                         TenderCompleteRoad
 *    aphulamb  11/24/08 - Checking files after code review by amrish
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *
 * ===========================================================================
 * $Log:
 *   5    360Commerce 1.4         8/20/2007 5:26:44 PM   Charles D. Baker CR
 *        28436 - Updated alignment of balance due for special orders.
 *   4    360Commerce 1.3         4/25/2007 8:51:33 AM   Anda D. Cadar   I18N
 *        merge
 *   3    360Commerce 1.2         3/31/2005 4:30:23 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:25:55 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:14:49 PM  Robert Pearse
 *
 *  Revision 1.3  2004/02/12 16:52:03  mcs
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 21:52:29  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:07:22   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:01:54   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:48:20   msg
 * Initial revision.
 *
 *    Rev 1.5   25 Jan 2002 10:34:50   jbp
 * increment line item reference.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.4   Jan 10 2002 17:33:30   dfh
 * sets line items status to new, pro-rates the deposit across
 * the line items
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.3   Dec 21 2001 16:57:08   dfh
 * set created order status to new
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.2   Dec 21 2001 12:39:04   dfh
 * added status change to active
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   Dec 04 2001 16:08:58   dfh
 * No change.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Dec 04 2001 15:11:20   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder.deposit;

// foundation imports
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemDiscountStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderItemTaxStatusIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.order.OrderManagerIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.order.common.OrderUtilities;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

// ------------------------------------------------------------------------------
/**
 * This class updates the balance due for the special order based upon the
 * deposit amount and sets the cargo.
 * <P>
 *
 */
// ------------------------------------------------------------------------------
@SuppressWarnings("serial")
public class TenderCompletedRoad extends PosLaneActionAdapter
{ // begin class TenderCompletedRoad
    /**
     * lane name constant
     */
    public static final String LANENAME = "TenderCompletedRoad";

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/39 $";

    private static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    private static final String XCHANNEL_ENABLED = "XChannelEnabled";

    // --------------------------------------------------------------------------
    /**
     * Performs the traversal functionality for the aisle. In this case, The
     * special order balance due is calculated based upon the deposit. Sets the
     * order and item status to new and prorates the deposit amount across the
     * line items. Journals the balance due.
     * <P>
     *
     * @param bus the bus traversing this lane
     */
    // --------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    { // begin traverse()
        SpecialOrderCargo specialOrderCargo = (SpecialOrderCargo)bus.getCargo();

        // Gets special order transaction and its components to update
        OrderTransactionIfc orderTransaction = specialOrderCargo.getOrderTransaction();
        CurrencyIfc balanceDue = orderTransaction.getOrderStatus().getBalanceDue();
        String storeID = specialOrderCargo.getStoreStatus().getStore().getStoreID();

        orderTransaction.prorateDeposit();

        OrderStatusIfc orderStatus = orderTransaction.getOrderStatus();
        if (!orderTransaction.containsXChannelOrderLineItemOnly())
        {
            //the order is non-xchannel order or xchannel order which contains take with items
            orderStatus.getStoreOrderStatus().changeStatus(OrderConstantsIfc.ORDER_STATUS_NEW);
            orderStatus.getStoreOrderStatus().setPreviousStatus(OrderConstantsIfc.ORDER_STATUS_NEW);
            orderStatus.getStoreOrderStatus().setPreviousStatusChange(orderStatus.getStoreOrderStatus().getLastStatusChange());
        }
        if (orderTransaction.containsXChannelOrderLineItem())
        {
            orderStatus.getXChannelStatus().changeStatus(OrderConstantsIfc.ORDER_STATUS_NEW);
            orderStatus.getXChannelStatus().setPreviousStatus(OrderConstantsIfc.ORDER_STATUS_NEW);
            orderStatus.getXChannelStatus().setPreviousStatusChange(orderStatus.getStoreOrderStatus().getLastStatusChange());
        }
        // set lineitems status to New
        AbstractTransactionLineItemIfc[] lineitems = orderTransaction.getLineItems();

        // flag used to see if the all the items are pickedup
        boolean storeOrderCompleted = true;
        int storeOrderLineNumber = 0, xchannelOrderLineNumber = 0, capturedOrderLineNumber = 0;

        for (AbstractTransactionLineItemIfc lineitem : lineitems)
        {
            SaleReturnLineItemIfc srln = (SaleReturnLineItemIfc)lineitem;
            boolean isXChannelItem = srln.getOrderItemStatus().isCrossChannelItem();
            int itemDispositionCode = srln.getOrderItemStatus().getItemDispositionCode();

            if (orderTransaction.getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND)
            {
                if (srln.getOrderItemStatus().isCrossChannelItem())
                {
                    // A cross channel item's availability is determined by OMS
                    srln.getOrderItemStatus().getStatus().changeStatus(
                            OrderConstantsIfc.ORDER_ITEM_STATUS_NEW);
                    srln.getOrderItemStatus().setQuantityNew(
                            srln.getItemQuantityDecimal());
                }
                else // store order item
                {
                    srln.getOrderItemStatus().setPickupStoreID(storeID);
                    if (itemDispositionCode == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE)
                    {
                        //xchannel order take with item
                        srln.getOrderItemStatus().getStatus().changeStatus(
                                OrderConstantsIfc.ORDER_ITEM_STATUS_PICKED_UP);
                        srln.getOrderItemStatus().setQuantityPickedUp(
                                srln.getItemQuantityDecimal());
                        srln.getOrderItemStatus().setCompletedAmount(
                                srln.getItemPrice().getItemTotal());
                    }
                    else
                    {
                        //If a store order item is in non-XC ORPOS env, set its status
                        //to NEW since ORPOS has fill order flow enabled to allow
                        //user to change its status to FILLED manually.
                        srln.getOrderItemStatus().getStatus().changeStatus(
                                OrderConstantsIfc.ORDER_ITEM_STATUS_NEW);
                        srln.getOrderItemStatus().setQuantityNew(
                                srln.getItemQuantityDecimal());
                    }
                }
            }
            else // special order item
            {
                // A special order item needs to be ordered from the vendor
                // before it is available for pickup.
                srln.getOrderItemStatus().setItemDispositionCode(
                        OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP);
                srln.getOrderItemStatus().getStatus().changeStatus(
                        OrderConstantsIfc.ORDER_ITEM_STATUS_NEW);
                srln.getOrderItemStatus().setQuantityNew(
                        srln.getItemQuantityDecimal());
                srln.getOrderItemStatus().setPickupStoreID(storeID);
            }

            srln.getOrderItemStatus().setOriginalTransactionId((TransactionIDIfc)orderTransaction.getTransactionIdentifier().clone());
            srln.getOrderItemStatus().setOriginalBusinessDate((EYSDate)orderTransaction.getBusinessDay().clone());
            srln.getOrderItemStatus().setOriginalLineNumber(srln.getLineNumber());
            
            srln.getOrderItemStatus().setQuantityOrdered(
                    srln.getItemQuantityDecimal());
            srln.getOrderItemStatus().setOrderedAmount(
                    srln.getItemPrice().getItemTotal());
            srln.setCapturedOrderLineReference(capturedOrderLineNumber++);
            if (isXChannelItem)
            {
                // For a xchannel transaction line item, ORPOS is not aware of its xchannel
                // order line item number at this point.
                srln.setOrderLineReference(xchannelOrderLineNumber++);
            }
            else
            {
                // Specify the store order line item number for a non-xchannel transaction line item.
                srln.setOrderLineReference(storeOrderLineNumber++);
            }

            // initialize order item discount status
            ItemDiscountStrategyIfc[] itemDiscounts = srln.getItemPrice().getItemDiscounts();
            int orderItemDiscountLineNo = 0;
            srln.getOrderItemStatus().clearDiscountStatus(); // clear all discount status in case there are ones from a resumed transaction
            
            for (ItemDiscountStrategyIfc itemDiscount : itemDiscounts)
            {
                itemDiscount.setOrderItemDiscountLineReference(orderItemDiscountLineNo++);
                OrderItemDiscountStatusIfc itemDiscountStatus = getOrderItemDiscountStatus(srln, itemDiscount);
                srln.getOrderItemStatus().addDiscountStatus(itemDiscountStatus);

            }

            // initialize order item tax line reference id
            TaxInformationIfc[] taxInformations = srln.getItemTax().getTaxInformationContainer().getTaxInformation();
            int orderItemTaxLineNo = 0;
            srln.getOrderItemStatus().clearTaxStatus(); // clear all tax status in case there are ones from a resumed transaction
            
            for (TaxInformationIfc taxInformation : taxInformations)
            {
                taxInformation.setOrderItemTaxLineReference(orderItemTaxLineNo++);
                OrderItemTaxStatusIfc itemTaxStatus = getOrderItemTaxStatus(srln, taxInformation);
                srln.getOrderItemStatus().addTaxStatus(itemTaxStatus);
            }

            // set the flag to false if there are order items to be picked up or delivered
            if (!isXChannelItem && (orderTransaction.getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND )
                    && (itemDispositionCode == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY
                            || itemDispositionCode == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP))
            {
                storeOrderCompleted =false;
            }
        }

        // set the store order status to complete if all the items are picked up
        if ((orderTransaction.getOrderType()
                == OrderConstantsIfc.ORDER_TYPE_ON_HAND) &&
             !orderTransaction.containsXChannelOrderLineItemOnly() && storeOrderCompleted)
        {
            orderTransaction.getOrderStatus().getStoreOrderStatus().setStatus(OrderConstantsIfc.ORDER_STATUS_COMPLETED);
        }

        // cancel unused order id
        OrderManagerIfc orderMgr = (OrderManagerIfc)bus.getManager(OrderManagerIfc.TYPE);
        OrderUtilities.cancelUnusedNewOrderIDs(orderTransaction, orderMgr);

        StringBuffer sb = new StringBuffer();
        String balanceDueString = balanceDue.toGroupFormattedString();

        Object dataObject[] = { balanceDueString };

        String depositAmountPaid = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.SPECIAL_ORDER_BALANCE_DUE, dataObject);

        sb.append(Util.EOL).append(depositAmountPaid).append(Util.EOL);

        JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        jmi.journal(orderTransaction.getCashier().getEmployeeID(), orderTransaction.getTransactionID(), sb.toString());
    } // end traverse()

    // --------------------------------------------------------------------------
    /**
     * Returns an order item discount status object
     * @param discountLineNo discount line number
     * @param srln the sale return line item
     * @param itemDiscount the item discount
     * @return OrderItemDiscountStatusIfc
     */
    // --------------------------------------------------------------------------
    protected OrderItemDiscountStatusIfc getOrderItemDiscountStatus(
            SaleReturnLineItemIfc srln, ItemDiscountStrategyIfc itemDiscount)
    {
        OrderItemDiscountStatusIfc discountStatus = DomainGateway.getFactory().getOrderItemDiscountStatusInstance();
        discountStatus.setLineNumber(itemDiscount.getOrderItemDiscountLineReference());
        discountStatus.setTotalAmount(itemDiscount.getDiscountAmount());
        if (srln.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE)
        {
            discountStatus.setCompletedAmount(itemDiscount.getDiscountAmount());
        }
        return discountStatus;
    }

    // --------------------------------------------------------------------------
    /**
     * Returns an order item tax status object
     * @param taxLineNo tax line number
     * @param srln the sale return line item
     * @param taxInformation the tax information
     * @return OrderItemTaxStatusIfc
     */
    // --------------------------------------------------------------------------
    protected OrderItemTaxStatusIfc getOrderItemTaxStatus(
            SaleReturnLineItemIfc srln, TaxInformationIfc taxInformation)
    {
        OrderItemTaxStatusIfc taxStatus = DomainGateway.getFactory().getOrderItemTaxStatusInstance();
        taxStatus.setAuthorityID(taxInformation.getTaxAuthorityID());
        taxStatus.setTaxGroupID(taxInformation.getTaxGroupID());
        taxStatus.setTypeCode(taxInformation.getTaxTypeCode());
        taxStatus.setTotalAmount(taxInformation.getTaxAmount());
        if (srln.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE)
        {
            taxStatus.setCompletedAmount(taxInformation.getTaxAmount());
        }

        return taxStatus;

    }

    /**
     * Gets the locale used for Journaling
     *
     * @return
     */
    public static Locale getJournalLocale()
    {
        // attempt to get instance
        return LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
    }

    /**
     * @return a flag indicating if cross channel is enabled.
     */
    protected boolean isXChannelEnabled()
    {
        return Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, XCHANNEL_ENABLED, false);
    }

} // end class TenderCompletedRoad
