/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/cancel/CheckAndUpdateOrderRoad.java /main/3 2013/01/15 18:46:22 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         01/14/13 - process pickup or cancel for store order items
* sgu         01/08/13 - update order status
* sgu         01/08/13 - add support for order picklist
* sgu         11/15/12 - correct order status mapping
* sgu         11/15/12 - add missing class
* sgu         11/15/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.cancel;

import java.math.BigDecimal;

import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.order.pickup.PickupOrderCargo;

public class CheckAndUpdateOrderRoad extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 3121109964615124365L;

    @Override
    public void traverse(BusIfc bus)
    {
        PickupOrderCargo cargo = (PickupOrderCargo) bus.getCargo();

        // set all NEW, PENDING, and FILLED item status to CANCEL
        // All checking if each order item can be cancelled is done
        // in CheckOrderCancelableSite
        OrderIfc cancelOrder = (OrderIfc)cargo.getOrder();
        for (OrderLineItemIfc orderLineItem : cancelOrder.getOrderLineItems())
        {
            OrderItemStatusIfc orderItemStatus = orderLineItem.getOrderItemStatus();

            orderItemStatus.setQuantityPickup(BigDecimal.ZERO);
            orderItemStatus.setQuantityCancel(
            		orderItemStatus.getQuantityNew().add(
                    orderItemStatus.getQuantityPending()).add(
                    orderItemStatus.getQuantityPicked()));
            orderItemStatus.setQuantityNew(BigDecimal.ZERO);
            orderItemStatus.setQuantityPending(BigDecimal.ZERO);
            orderItemStatus.setQuantityPicked(BigDecimal.ZERO);

            orderItemStatus.setStatusByQuantity();
        }

        // set order status
        cancelOrder.setOrderStatus();

        // clear the cancel order transaction created based on the order
        // in the cargo since the order may have been looked up again.
        // This will force the transaction to be recreated.
        cargo.setTransaction(null);
    }
}
