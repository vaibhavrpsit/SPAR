/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/CreateShipOrderSite.java /main/13 2014/03/10 14:11:41 ohorne Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* ohorne      03/05/14 - OrderUtilities.assignNewOrderID no longer calls
*                        OrderTransaction.setUniqueID(..)
* mkutiana    05/01/13 - moved the journaling to after the order number is
*                        created
* vtemker     04/16/13 - Moved constants in OrderLineItemIfc to
*                        OrderConstantsIfc in common project
* tksharma    12/10/12 - commons-lang update 3.1
* sgu         10/15/12 - add ordered amount
* yiqzhao     09/17/12 - fix the issue with multiple order delivery details for
*                        a given item group.
* blarsen     09/11/12 - Merge project Echo (MPOS) into Trunk.
* yiqzhao     08/31/12 - add kit components and remove kit header from
*                        lineItems in cargo.
* sgu         07/03/12 - replace item disposition code to use delivery instead
*                        of ship
* yiqzhao     07/03/12 - refine shipping flow
* yiqzhao     06/29/12 - Add dialog for deleting ship item, disable change
*                        price for ship item
* sgu         06/27/12 - set item disposition code for ship to store item
* sgu         06/27/12 - modify ship to store for xc
* yiqzhao     06/07/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.xchannelcreateshipping;

//foundation imports
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
    @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class CreateShipOrderSite extends PosSiteActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    //----------------------------------------------------------------------
    /**
       This method updates the transaction with order information.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        XChannelShippingCargo cargo = (XChannelShippingCargo)bus.getCargo();
        SaleReturnTransactionIfc saleTransaction = (SaleReturnTransaction)cargo.getTransaction();

        try
        {
            if (saleTransaction.getTransactionType() != TransactionIfc.TYPE_ORDER_INITIATE)
            {
                createOrder(bus, cargo, saleTransaction);
            }
            else
            {
                cargo.setTransaction((OrderTransactionIfc)saleTransaction.clone());
            }

            updateLineItems(cargo);
            setOrderID(bus, cargo);
            bus.mail(CommonLetterIfc.DONE);
        }
        catch (DataException de)
        {
            cargo.setTransaction(null); // by seting it to null, the transaction will not be copied back to the caller cargo
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
    protected void createOrder(BusIfc bus, XChannelShippingCargo cargo,
            SaleReturnTransactionIfc saleTransaction)
    {
        OrderTransactionIfc orderTransaction =
            DomainGateway.getFactory().getOrderTransactionInstance();
        orderTransaction.setCashier(cargo.getOperator());
        orderTransaction.setSalesAssociate(cargo.getSalesAssociate());

        orderTransaction.initializeOrderFromSaleTransaction((SaleReturnTransaction)saleTransaction);
        cargo.setTransaction(orderTransaction);

    }

    /**
     * Update order item status of all selected order line items
     * @param cargo
     */
    protected void updateLineItems(XChannelShippingCargo cargo)
    {
        // Get the list of items from the cargo that the operator selected for modification
        // iterate through the list.
        AbstractTransactionLineItemIfc[] lineItems = cargo.getTransaction().getItemContainerProxy().getLineItems();
        for (AbstractTransactionLineItemIfc lineItem:lineItems)
        {
            if (lineItem instanceof SaleReturnLineItemIfc &&
                ((SaleReturnLineItemIfc)lineItem).isSelectedForItemModification())
            {
                SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)lineItem;
                if ( srli.isKitHeader() )
                {
                	KitHeaderLineItemIfc kitHeaderLineItem = (KitHeaderLineItemIfc)srli;
                	for ( int i=0; i<kitHeaderLineItem.getKitComponentLineItemArray().length; i++ )
                	{
                		SaleReturnLineItemIfc kitComponentLineItem = kitHeaderLineItem.getKitComponentLineItemArray()[i];
                		updateLineItem(cargo, kitComponentLineItem);
                	}
                }
                else
                {
                	updateLineItem(cargo, srli);
                }
            }
        }
    }

    /**
     * Update order item status of a selected order line item
     * @param cargo
     * @param lineItem
     */
    protected void updateLineItem(XChannelShippingCargo cargo, SaleReturnLineItemIfc lineItem)
    {
        OrderItemStatusIfc orderItemStatus = lineItem.getOrderItemStatus();

        orderItemStatus.setCrossChannelItem(true);
        orderItemStatus.setOrderedAmount(lineItem.getItemPrice().getItemTotal());
        orderItemStatus.setQuantityOrdered(lineItem.getItemQuantityDecimal());

        if (cargo.isShipToCustomer())
        {
            orderItemStatus.setItemDispositionCode(OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY);
        }
        else
        {
            orderItemStatus.setItemDispositionCode(OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP);
            orderItemStatus.setShipToStoreForPickup(true);

	        CaptureCustomerIfc customer = cargo.getCaptureCustomer();
	        orderItemStatus.setPickupFirstName(customer.getFirstName());
	        orderItemStatus.setPickupLastName(customer.getLastName());
	        orderItemStatus.setPickupContact(customer.getPrimaryPhone());

	        StoreIfc store = cargo.getStoreToShip();
	        orderItemStatus.setPickupStoreID(store.getStoreID());
        }
    }

    /**
     * Set order id for an order transaction
     * @param bus
     * @param cargo
     * @throws DataException
     */
    protected void setOrderID(BusIfc bus, XChannelShippingCargo cargo) throws DataException
    {
        //For a new order transaction or an order transaction that just turns into a cross channel order,
        //A new order id needs to be assigned to the transaction.
        OrderManagerIfc orderMgr = (OrderManagerIfc)bus.getManager(OrderManagerIfc.TYPE);
        OrderTransactionIfc orderTransaction = (OrderTransactionIfc)cargo.getTransaction();

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
