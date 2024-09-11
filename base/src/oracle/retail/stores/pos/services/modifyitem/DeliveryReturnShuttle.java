/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/DeliveryReturnShuttle.java /main/12 2013/04/16 13:32:33 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    sgu       10/18/12 - set order item status
 *    sgu       10/18/12 - set quantity ordered and picked
 *    sgu       07/03/12 - added xc order ship delivery date, carrier code and
 *                         type code
 *    jswan     05/14/12 - Modified to support Ship button feature.
 *    jswan     04/13/12 - Modified to support the change in location of the
 *                         pickup and delivery tours.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    nkgautam  03/02/10 - setting item serial to null for delivery transaction
 *                         when item scanned through imei
 *    abondala  01/03/10 - update header date
 *    asinton   05/12/09 - Removed calls to ItemCargo.setRetailTransactionIfc
 *                         and put call to ItemCargo.setTransaction inside of
 *                         null check for
 *                         pickupDeliveryOrderCargo.getOrderTransaction().
 *    jswan     04/14/09 - Added comment per code review.
 *    jswan     04/14/09 - Modified to fix conflict between multi quantity
 *                         items and items that have been marked for Pickup or
 *                         Delivery.
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - Delivery return shuttle
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

import java.math.BigDecimal;
import java.util.Iterator;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.order.createpickup.PickupDeliveryOrderCargo;

public class DeliveryReturnShuttle extends FinancialCargoShuttle
{

    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5891734460808892690L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     Child service's cargo
     **/
    protected PickupDeliveryOrderCargo pickupDeliveryOrderCargo = null;

    //----------------------------------------------------------------------
    /**
     Loads the PickupDeliveryOrderCarog.
     <P>
     @param  bus     Service Bus to copy cargo from.
     **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
     Copies the new delivery detail to the cargo for the Modify Item service.
     <P>
     @param  bus     Service Bus to copy cargo to.
     **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        ItemCargo cargo = (ItemCargo)bus.getCargo();
        if (pickupDeliveryOrderCargo.getOrderTransaction() != null)
        {
            cargo.setTransaction(pickupDeliveryOrderCargo.getOrderTransaction());
            setStatusOnSelectedItems(pickupDeliveryOrderCargo.getOrderTransaction().getItemContainerProxy().getLineItems(),
                    pickupDeliveryOrderCargo.getLineItems());
        }
        cargo.setPickupOrDeliveryExecuted(true);
    }

    /**
     *  Multi-quantity line items can be expanded to individual line items
     *  when a customer is added to the transaction.  As result the delivery
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
            SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)lineItems[i];
            if (item.isSelectedForItemModification())
            {
                // The "models" are the list of items to changes from sale item screen.
                // Each item in this list gets exactly the same delivery data, so all
                // we need is the first one.
                setStatusOnSelectedItem(item, models[0]);
            }
        }
    }

    /**
     * Update the line item with the delivery data from the model line item.
     * @param lineItem
     * @param model
     */
    protected void setStatusOnSelectedItem(SaleReturnLineItemIfc lineItem, SaleReturnLineItemIfc model)
    {
        KitHeaderLineItemIfc parentKitItem = null;
        KitComponentLineItemIfc childKit   = null;

        setStatusOnSelectedOrderItemStatus(lineItem.getOrderItemStatus(), lineItem.getItemQuantityDecimal(),
                model.getOrderItemStatus());

        if (lineItem.isKitHeader())
        {
            parentKitItem = (KitHeaderLineItemIfc)lineItem;
            Iterator<KitComponentLineItemIfc> childKitItemIter = parentKitItem.getKitComponentLineItems();
            while (childKitItemIter.hasNext())
            {
                childKit = childKitItemIter.next();
                setStatusOnSelectedOrderItemStatus(childKit.getOrderItemStatus(), childKit.getItemQuantityDecimal(),
                        model.getOrderItemStatus());
            }

        }
        if(lineItem.getOrderItemStatus().getStatus().getStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_UNDEFINED)
        {
            lineItem.setItemSerial(null);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Update the order item status with delivery info from the model
     * @param orderItemStatus
     * @param model
     */
    //----------------------------------------------------------------------
    protected void setStatusOnSelectedOrderItemStatus(OrderItemStatusIfc orderItemStatus,
            BigDecimal qtyOrdered, OrderItemStatusIfc model)
    {
        orderItemStatus.setQuantityOrdered(qtyOrdered);
        orderItemStatus.setItemDispositionCode(OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY);
        orderItemStatus.getDeliveryDetails().setDeliveryDetailID(model.getDeliveryDetails().getDeliveryDetailID());
        orderItemStatus.setDeliveryDetails(model.getDeliveryDetails());
    }

    //----------------------------------------------------------------------
    /**
     Returns a string representation of this object.
     <P>
     @return String representation of object
     **/
    //----------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        String strResult = new String("Class:  InquiryOptionsReturnShuttle (Revision " + getRevisionNumber() + ")"
                + hashCode());

        // pass back result
        return (strResult);
    } // end toString()

    //----------------------------------------------------------------------
    /**
     Returns the revision number of the class.
     <P>
     @return String representation of revision number
     **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    } // end getRevisionNumber()

    //----------------------------------------------------------------------
    /**
     Main to run a test..
     <P>
     @param  args    Command line parameters
     **/
    //----------------------------------------------------------------------
    public static void main(String args[])
    { // begin main()
        // instantiate class
        DeliveryReturnShuttle obj = new DeliveryReturnShuttle();

        // output toString()
        System.out.println(obj.toString());
    }

}
