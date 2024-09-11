/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/CreatePickupOrderSite.java /main/16 2014/05/01 10:28:08 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   04/30/14 - Make pickup item from the same store as a cross
 *                         channel item.
 *    ohorne    03/05/14 - OrderUtilities.assignNewOrderID no longer calls
 *                         OrderTransaction.setUniqueID(..)
 *    tksharma  11/14/13 - called updateTransactionTotals after creating
 *                         orderTransaction object
 *    yiqzhao   10/01/13 - Make kit header not order item, but display the
 *                         order image and delivery store id on Sale Item
 *                         Screen since kit components are hidden on the
 *                         screen.
 *    abhinavs  08/30/13 - Fix to set xchannel order attributes of the
 *                         kitheaderitem
 *    mkutiana  05/01/13 - moved the journaling to after the order number is
 *                         created
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    sgu       01/11/13 - set xchannel order item flag based on if item is
 *                         avilable in the store inventory
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    sgu       10/18/12 - set order item status
 *    sgu       10/18/12 - set quantity ordered and picked
 *    sgu       10/15/12 - add ordered amount
 *    blarsen   09/11/12 - Merge project Echo (MPOS) into Trunk.
 *    yiqzhao   09/04/12 - use preSplitLineNumber since lineNumber may change
 *                         from transaction after linking a customer. It may
 *                         trigger advanced price rules.
 *    yiqzhao   08/31/12 - add kit components and remove kit header from
 *                         lineItems in cargo.
 *    sgu       06/22/12 - refactor order id assignment
 *    sgu       06/21/12 - rename resetOrderID to resyncOrderID
 *    sgu       06/20/12 - clone the order transaction before making changes
 *    sgu       06/20/12 - refactor get order id
 *    jswan     05/14/12 - Modified to fix issue with split of multi-quantity
 *                         line items.
 *    jswan     04/29/12 - Added to support cross channel create pickup order
 *                         feature.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

//foundation imports
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.order.OrderManagerIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.common.OrderUtilities;

import org.apache.commons.lang3.StringUtils;

//--------------------------------------------------------------------------
/**
    This class updates the transaction with order information.
    <p>
    @version $Revision: /main/16 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class CreatePickupOrderSite extends PosSiteActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/16 $";

    //----------------------------------------------------------------------
    /**
       This method updates the transaction with order information.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo)bus.getCargo();
        SaleReturnTransactionIfc saleTransaction = (SaleReturnTransaction)cargo.getTransaction();

        try
        {
            if (saleTransaction.getTransactionType() != TransactionIfc.TYPE_ORDER_INITIATE)
            {
                createOrder(bus, cargo, saleTransaction);
            }
            else
            {
                OrderTransactionIfc orderTransaction = (OrderTransactionIfc)saleTransaction.clone();
                orderTransaction.updateTransactionTotals();
                cargo.setOrderTransaction(orderTransaction);
                cargo.setTransaction(orderTransaction);
            }

            updateLineItems(cargo);
            setOrderID(bus, cargo);
            bus.mail(CommonLetterIfc.CONTINUE);
        }
        catch (DataException de)
        {
            cargo.setOrderTransaction(null); // by seting it to null, the transaction will not be copied back to the caller cargo
            bus.mail(CommonLetterIfc.OFFLINE);
        }
    }

    /**
     * Creates an order transaction from the sale return transaction.
     * @param bus
     * @param cargo
     * @param saleTransaction
     * @return order transaction
     */
    protected void createOrder(BusIfc bus, XChannelCreatePickupOrderCargo cargo,
            SaleReturnTransactionIfc saleTransaction)
    {
        OrderTransactionIfc orderTransaction =
            DomainGateway.getFactory().getOrderTransactionInstance();
        orderTransaction.setCashier(cargo.getOperator());
        orderTransaction.setSalesAssociate(cargo.getSalesAssociate());

        orderTransaction.initializeOrderFromSaleTransaction((SaleReturnTransaction)saleTransaction);
        orderTransaction.updateTransactionTotals();
        cargo.setOrderTransaction(orderTransaction);
        cargo.setTransaction(orderTransaction);
        cargo.setTransactionType(orderTransaction.getTransactionType());

    }

    /**
     * Update each line item with it's data.
     * @param cargo
     */
    @SuppressWarnings("unchecked")
    protected void updateLineItems(XChannelCreatePickupOrderCargo cargo)
    {
        HashMap<Integer, StoreIfc> stores  = cargo.getStoreForPickupByLineNum();
        LinkedHashMap<Integer, CaptureCustomerIfc> customers = cargo.getCustomerForPickupByLineNum();
        HashMap<Integer, EYSDate> dates = cargo.getDateForPickupByLineNum();

        // Get the list of items from the cargo that the operator selected for modification
        // iterate through the list.
        AbstractTransactionLineItemIfc[] lineItems = cargo.getOrderTransaction().getItemContainerProxy().getLineItems();
        for (AbstractTransactionLineItemIfc lineItem:lineItems)
        {
            if (lineItem instanceof SaleReturnLineItemIfc && ((SaleReturnLineItemIfc)lineItem).isSelectedForItemModification())
            {
                // Get the data from the cargo that the operator has entered to
                // modify each of the items in the list.
                SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)lineItem;
                if (srli.isKitHeader() )
                {
                    KitHeaderLineItemIfc kitHeaderLineItem = (KitHeaderLineItemIfc)srli;
                    for ( int j=0; j<kitHeaderLineItem.getKitComponentLineItemArray().length; j++ )
                    {
                        lineItem = kitHeaderLineItem.getKitComponentLineItemArray()[j];
                        if (lineItem instanceof SaleReturnLineItemIfc)
                        {
                            setOrderInfo(cargo, (SaleReturnLineItemIfc)lineItem, dates, customers, stores);
                        }
                    }
                }
                else
                {
                	setOrderInfo(cargo, srli, dates, customers, stores);
                }
            }
        }
    }

    /**
     * set order line status information
     * @param cargo
     * @param srli
     * @param dates
     * @param customers
     * @param stores
     */
    protected void setOrderInfo(XChannelCreatePickupOrderCargo cargo, SaleReturnLineItemIfc srli,
    		HashMap<Integer, EYSDate> dates,
    		LinkedHashMap<Integer, CaptureCustomerIfc> customers,
    		HashMap<Integer, StoreIfc> stores)
    {
        srli.getOrderItemStatus().setItemDispositionCode(OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP);

       int itemIndex = srli.getPreSplitLineNumber();
       EYSDate date = dates.get(itemIndex);
       CaptureCustomerIfc customer = customers.get(itemIndex);
       StoreIfc store = stores.get(itemIndex);
      
       // The entire list of items from the sale return transaction have been cloned
       // and set on the order transaction.  Get the corresponding line item and
       // update that copy.
       setOrderItemStatusValues(srli.getOrderItemStatus(), date, customer, store,
               srli.getItemPrice().getItemTotal(), srli.getItemQuantityDecimal());
    }

    /**
     * Set the order item status values
     * @param orderItemStatus
     * @param date
     * @param customer
     * @param store
     * @param isCrossChannel
     */
    protected void setOrderItemStatusValues(OrderItemStatusIfc orderItemStatus,
            EYSDate date, CaptureCustomerIfc customer, StoreIfc store, CurrencyIfc amountOrdered,
            BigDecimal quantityOrdered)
    {
        orderItemStatus.setPickupDate(date);
        orderItemStatus.setItemDispositionCode(OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP);
        orderItemStatus.setPickupFirstName(customer.getFirstName());
        orderItemStatus.setPickupLastName(customer.getLastName());
        orderItemStatus.setPickupContact(customer.getPrimaryPhone());
        orderItemStatus.setPickupStoreID(store.getStoreID());
        orderItemStatus.setCrossChannelItem(true);
        orderItemStatus.setOrderedAmount(amountOrdered);
        orderItemStatus.setQuantityOrdered(quantityOrdered);
    }

    /**
     * Set order id for an order transaction
     * @param bus
     * @param cargo
     * @throws DataException
     */
    protected void setOrderID(BusIfc bus, XChannelCreatePickupOrderCargo cargo) throws DataException
    {
        //For a new order transaction or an order transaction that just turns into a cross channel order,
        //A new order id needs to be assigned to the transaction.
        OrderManagerIfc orderMgr = (OrderManagerIfc)bus.getManager(OrderManagerIfc.TYPE);
        OrderTransactionIfc orderTransaction = cargo.getOrderTransaction();

        String orderID = orderTransaction.resyncNewOrderID();
        if (StringUtils.isBlank(orderID))
        {
            OrderUtilities.assignNewOrderID(orderTransaction, orderMgr,cargo.getRegister());
            orderTransaction.setUniqueID(cargo.getRegister().getCurrentUniqueID());
        }
        //Journal Order creation
        OrderUtilities.journalOrderCreation(bus, orderTransaction);
    }

}
