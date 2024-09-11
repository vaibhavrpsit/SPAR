/*===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/print/CheckAndUpdateOrderRoad.java /main/2 2013/01/15 18:46:22 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         01/14/13 - process pickup or cancel for store order items
* sgu         01/09/13 - update order to printed status in picklist and
*                        servicealert
* sgu         01/08/13 - update order status
* sgu         01/08/13 - set order pending status
* sgu         01/08/13 - add new road
* sgu         01/08/13 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.print;

import java.math.BigDecimal;

import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.order.common.OrderCargoIfc;

/**
 * @author sgu
 *
 * Update the order status to Printed. This road is only invoked
 * if cross channel env is not enabled.
 */
public class CheckAndUpdateOrderRoad extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -1641892459064829547L;

    @Override
    public void traverse(BusIfc bus)
    {
        OrderCargoIfc cargo = (OrderCargoIfc)bus.getCargo();

        // set all items of NEW status to PENDING.
        OrderIfc order = cargo.getOrder();
        for (OrderLineItemIfc lineItem : order.getOrderLineItems())
        {
            OrderItemStatusIfc orderItemStatus = lineItem.getOrderItemStatus();
     
            orderItemStatus.setQuantityPending(orderItemStatus.getQuantityPending().add(
            		orderItemStatus.getQuantityNew()));
            orderItemStatus.setQuantityNew(BigDecimal.ZERO);

            orderItemStatus.setStatusByQuantity();
        }

        // set order status
        order.setOrderStatus();
    }
}


