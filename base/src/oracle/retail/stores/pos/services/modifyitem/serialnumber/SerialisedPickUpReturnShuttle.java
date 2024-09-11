/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/serialnumber/SerialisedPickUpReturnShuttle.java /main/9 2013/04/16 13:32:47 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    sgu       07/16/12 - set pickup info into order line items
 *    jswan     04/13/12 - Modified to support the change in location of the
 *                         pickup and delivery tours.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/15/09 - new return shuttle for serialised item pickup
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.serialnumber;

import java.util.Iterator;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.order.createpickup.PickupDeliveryOrderCargo;

public class SerialisedPickUpReturnShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6347055382296841937L;

    /**
     * pickup delivery cargo
     */
    protected PickupDeliveryOrderCargo pickupDeliveryOrderCargo = null;

    /**
     * Loads the item cargo.
     * @param bus Service Bus to copy cargo from.
     */
    public void load(BusIfc bus)
    {
        pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo) bus.getCargo();
    }

    /**
      Copies the pickup delivery order info to the cargo for the Modify Item service.
      @param  bus     Service Bus to copy cargo to.
     **/
    public void unload(BusIfc bus)
    {
        SerializedItemCargo cargo = (SerializedItemCargo) bus.getCargo();
        if (pickupDeliveryOrderCargo.getOrderTransaction() != null)
        {
            cargo.setTransaction(pickupDeliveryOrderCargo.getTransaction());
            setStatusOnSelectedItems(pickupDeliveryOrderCargo.getOrderTransaction().getItemContainerProxy().getLineItems(),
                    pickupDeliveryOrderCargo.getLineItems());
        }
        cargo.setPickupOrDeliveryExecuted(true);
    }

    /**
     *  Multi-quantity line items can be expanded to individual line items
     *  when a customer is added to the transaction.  As result the pickup
     *  function must manage the update of line items itself.
     *  This method iterates through the line items from ItemContainerProxy
     *  looking for line items that have been selected for Item Modification
     *  and then calls a method update these items with the pickup info.
     * @param lineItems
     * @param models
     */
    protected void setStatusOnSelectedItems(AbstractTransactionLineItemIfc[] lineItems, SaleReturnLineItemIfc[] models)
    {
        for(int i = 0; i < lineItems.length; i++)
        {
            SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)lineItems[i];
            if (lineItem.isSelectedForItemModification())
            {
                // The "models" are the list of items to changes from sale item screen.
                // Each item in this list gets exactly the same pickup data, so all
                // we need is the first one.
                setStatusOnSelectedItem(lineItem, models[0]);
            }
        }
    }

    /**
     * Update the line item with the pickup data from the model line item.
     * @param lineItem
     * @param model
     */
    protected void setStatusOnSelectedItem(SaleReturnLineItemIfc lineItem, SaleReturnLineItemIfc model)
    {
        KitHeaderLineItemIfc parentKitItem = null;
        KitComponentLineItemIfc childKit   = null;
        setStatusOnSelectedOrderItemStatus(lineItem.getOrderItemStatus(), model.getOrderItemStatus());

        if (lineItem.isKitHeader())
        {
            parentKitItem = (KitHeaderLineItemIfc)lineItem;
            Iterator<KitComponentLineItemIfc> childKitItemIter = parentKitItem.getKitComponentLineItems();
            while (childKitItemIter.hasNext())
            {
                childKit = childKitItemIter.next();
                setStatusOnSelectedOrderItemStatus(childKit.getOrderItemStatus(), model.getOrderItemStatus());
            }
        }
    }

    //----------------------------------------------------------------------
    /**
     * Update the order item status with pickup info from the model
     * @param orderItemStatus
     * @param model
     */
    //----------------------------------------------------------------------
    protected void setStatusOnSelectedOrderItemStatus(OrderItemStatusIfc orderItemStatus, OrderItemStatusIfc model)
    {
        orderItemStatus.setPickupDate(model.getPickupDate());
        orderItemStatus.setPickupFirstName(model.getPickupFirstName());
        orderItemStatus.setPickupLastName(model.getPickupLastName());
        orderItemStatus.setPickupContact(model.getPickupContact());
        orderItemStatus.setPickupStoreID(model.getPickupStoreID());
        orderItemStatus.setItemDispositionCode(OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP);
    }
}
