/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/CreatePickupDeliverOrderReturnShuttle.java /main/5 2013/04/16 13:32:31 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    yiqzhao   02/28/13 - Handle orderLineItems for cargos.
 *    sgu       07/16/12 - set pickup info into order line items
 *    sgu       07/03/12 - added xc order ship delivery date, carrier code and
 *                         type code
 *    jswan     05/14/12 - Added to support the Ship button functionality.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

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
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.order.createpickup.PickupDeliveryOrderCargo;

public class CreatePickupDeliverOrderReturnShuttle implements ShuttleIfc
{

    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -82268067153917575L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/5 $";

    /** Calling Item cargo */
    protected PickupDeliveryOrderCargo orderCargo = null;

    // ----------------------------------------------------------------------
    /**
     * Loads the item cargo.
     * <P>
     *
     * @param bus Service Bus to copy cargo from.
     */
    // ----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        orderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
    }

    // ----------------------------------------------------------------------
    /**
     * Transfers the item cargo to the pickup delivery order cargo for the
     * modify item service.
     * <P>
     *
     * @param bus Service Bus to copy cargo to.
     */
    // ----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        SaleCargo saleCargo = (SaleCargo)bus.getCargo();
        if (orderCargo.getOrderTransaction() != null)
        {
            saleCargo.setTransaction(orderCargo.getOrderTransaction());
            setStatusOnSelectedItems(orderCargo.getOrderTransaction().getItemContainerProxy().getLineItems(),
                    orderCargo.getLineItems());
        }

        // Reset the line items
        AbstractTransactionLineItemIfc[] lineItems = saleCargo.getTransaction().
            getItemContainerProxy().getLineItems();
        for(int i = 0; i < lineItems.length; i++)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)lineItems[i];
            srli.setSelectedForItemModification(false);
        }
        int size = saleCargo.getOrderLineItems().size();
        for ( int i=0; i<size; i++ )
        {
            saleCargo.getOrderLineItems().remove(0);
        }
    }

    /**
     *  Multi-quantity line items can be expanded to individual line items
     *  when a customer is added to the transaction.  As result the delivery/pickup
     *  function must manage the update of line items itself.
     *  This method iterates through the line items from ItemContainerProxy
     *  looking for line items that have been selected for Item Modification
     *  and then calls a method update these items with the delivery info.
     * @param lineItems
     * @param models
     */
    protected void setStatusOnSelectedItems(AbstractTransactionLineItemIfc[] lineItems, SaleReturnLineItemIfc[] models)
    {
        for(int i = 0; i < lineItems.length; i++)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)lineItems[i];
            if (srli.isSelectedForItemModification())
            {
                setStatusOnSelectedItem(srli, models[0]);
                if(srli.getOrderItemStatus().getStatus().getStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_UNDEFINED)
                {
                    srli.setItemSerial(null);
                }
            }
        }
    }

    /**
     * Update the line item with the delivery data from the model line item.
     * @param lineItem
     * @param model
     */
    @SuppressWarnings("unchecked")
    protected void setStatusOnSelectedItem(SaleReturnLineItemIfc srli, SaleReturnLineItemIfc model)
    {
        setStatusOnSelectedOrderItemStatus(srli.getOrderItemStatus(), model.getOrderItemStatus());

        KitHeaderLineItemIfc parentKitItem = null;
        KitComponentLineItemIfc childKit   = null;
        if (srli.isKitHeader())
        {
            parentKitItem = (KitHeaderLineItemIfc)srli;
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
        orderItemStatus.setItemDispositionCode(model.getItemDispositionCode());

        if (model.getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY)
        {
            orderItemStatus.getDeliveryDetails().setDeliveryDetailID(
                    model.getDeliveryDetails().getDeliveryDetailID());
            orderItemStatus.setDeliveryDetails(model.getDeliveryDetails());
        }
        else
        {
            orderItemStatus.setPickupDate(model.getPickupDate());
            orderItemStatus.setPickupFirstName(model.getPickupFirstName());
            orderItemStatus.setPickupLastName(model.getPickupLastName());
            orderItemStatus.setPickupContact(model.getPickupContact());
            orderItemStatus.setPickupStoreID(model.getPickupStoreID());
        }
    }

    // ----------------------------------------------------------------------
    /**
     * Returns a string representation of this object.
     * <P>
     *
     * @return String representation of object
     */
    // ----------------------------------------------------------------------
    public String toString()
    {
        return "Class:  InquiryOptionsLaunchShuttle (Revision " + getRevisionNumber() + ")" + hashCode();
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * <P>
     *
     * @return String representation of revision number
     */
    // ----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

}
