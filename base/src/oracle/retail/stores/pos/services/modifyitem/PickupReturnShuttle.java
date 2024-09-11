/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/PickupReturnShuttle.java /main/13 2013/04/16 13:32:44 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    sgu       10/18/12 - set order item status
 *    sgu       10/18/12 - set quantity ordered and picked
 *    yiqzhao   08/31/12 - set order status for kit component but not kit
 *                         header.
 *    sgu       07/16/12 - set pickup info into order line items
 *    jswan     04/13/12 - Modified to support the change in location of the
 *                         pickup and delivery tours.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    nkgautam  03/17/10 - setting item serial to null when item is selected
 *                         for modification
 *    nkgautam  03/02/10 - setting item serial to null when pickup transaction
 *                         is initiated
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
 *    aphulamb  11/13/08 - Pickup launch shuttle
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

public class PickupReturnShuttle extends FinancialCargoShuttle
{

	// This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5891734460808892690L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/13 $";
    /**
        pickup delivery cargo
    **/
    protected PickupDeliveryOrderCargo pickupDeliveryOrderCargo = null;

    //----------------------------------------------------------------------
    /**
        load pickup delivery order cargo.
        <P>
        @param  bus     Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
    	pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
        Copies the pickup delivery order info to the cargo for the Modify Item service.
        <P>
        @param  bus     Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        ItemCargo cargo = (ItemCargo) bus.getCargo();
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
                if(lineItem.getOrderItemStatus().getStatus().getStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_UNDEFINED)
                {
                    lineItem.setItemSerial(null);
                }
            }

        }
    }

    /**
     * Update the line item with the pickup info from the model line item.
     * @param lineItem
     * @param model
     */
    protected void setStatusOnSelectedItem(SaleReturnLineItemIfc lineItem, SaleReturnLineItemIfc model)
    {
        KitHeaderLineItemIfc parentKitItem = null;
        KitComponentLineItemIfc childKit   = null;

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
        else
        {
        	 setStatusOnSelectedOrderItemStatus(lineItem.getOrderItemStatus(), lineItem.getItemQuantityDecimal(),
                     model.getOrderItemStatus());
        }
    }

    //----------------------------------------------------------------------
    /**
     * Update the order item status with pickup info from the model
     * @param orderItemStatus
     * @param model
     */
    //----------------------------------------------------------------------
    protected void setStatusOnSelectedOrderItemStatus(OrderItemStatusIfc orderItemStatus, BigDecimal qtyOrdered,
            OrderItemStatusIfc model)
    {
        orderItemStatus.setQuantityOrdered(qtyOrdered);
        orderItemStatus.setPickupDate(model.getPickupDate());
        orderItemStatus.setPickupFirstName(model.getPickupFirstName());
        orderItemStatus.setPickupLastName(model.getPickupLastName());
        orderItemStatus.setPickupContact(model.getPickupContact());
        orderItemStatus.setPickupStoreID(model.getPickupStoreID());
        orderItemStatus.setItemDispositionCode(OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP);
    }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  InquiryOptionsReturnShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   // end getRevisionNumber()

    //----------------------------------------------------------------------
    /**
        Main to run a test..
        <P>
        @param  args    Command line parameters
    **/
    //----------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        PickupReturnShuttle obj = new PickupReturnShuttle();

        // output toString()
        System.out.println(obj.toString());
    }



}
