/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/createpickup/GetPickupDateEnteredSite.java /main/9 2013/04/16 13:32:35 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    sgu       07/16/12 - set pickup info into order line items
 *    jswan     04/13/12 - Moved to the ‘order’ package during the cross
 *                         channel project to provide better organization for
 *                         the order create process.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  02/26/09 - Rework for PDO functionality
 *    aphulamb  11/25/08 - Checking files after code review by Amrish
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/18/08 - Pickup Delivery Order
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - Get pickup date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.createpickup;

import java.util.Iterator;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.GetPickupDateBeanModel;

@SuppressWarnings("serial")
public class GetPickupDateEnteredSite extends PosSiteActionAdapter
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/9 $";

    protected EYSDate enteredDate = null;

    protected EYSDate currentSystemDate = null;

    // ----------------------------------------------------------------------
    /**
     * validation for pickup date and set the pickup date into OrderItemStatus
     * Object
     * <P>
     *
     * @param bus Service bus.
     */
    // ----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        PickupDeliveryOrderCargo pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        SaleReturnLineItemIfc[] lineItems = pickupDeliveryOrderCargo.getLineItems();
        GetPickupDateBeanModel model = (GetPickupDateBeanModel)ui.getModel(POSUIManagerIfc.GET_PICKUP_DATE_SCREEN);

        // get the pickup date from model
        enteredDate = model.getSelectedPickupDate();
        currentSystemDate = new EYSDate();
        // set the pickup date into OrderItemStatus if entered pickup date is
        // valid
        StoreIfc store = pickupDeliveryOrderCargo.getStoreStatus().getStore();
        CustomerIfc customer = pickupDeliveryOrderCargo.getTransaction().getCustomer();
        KitHeaderLineItemIfc parentKitItem=null;
        KitComponentLineItemIfc childKit=null;
        if (isEnteredDateCorrect())
        {
            for (int i = 0; i < lineItems.length; i++)
            {
                setOrderItemStatus(lineItems[i].getOrderItemStatus(), enteredDate, customer, store);
                if (lineItems[i].isKitHeader())
                {
                    parentKitItem = (KitHeaderLineItemIfc)lineItems[i];
                    Iterator<KitComponentLineItemIfc> childKitItemIter = parentKitItem.getKitComponentLineItems();
                    while (childKitItemIter.hasNext())
                    {
                        childKit = childKitItemIter.next();
                        setOrderItemStatus(childKit.getOrderItemStatus(), enteredDate, customer, store);
                    }

                }
            }
            bus.mail(CommonLetterIfc.CONTINUE);
        }
        else
        {
            dialogForIncorrectDate(bus);
        }
    }

    // ----------------------------------------------------------------------
    /**
     * Set pickup info into order item status
     * @param orderItemStatus the order item status
     * @param pickupDate the pickup date
     * @param customer the customer to pickup the item
     * @param store the store to pickup the item
     */
    // ----------------------------------------------------------------------
    protected void setOrderItemStatus(OrderItemStatusIfc orderItemStatus, EYSDate pickupDate,
            CustomerIfc customer, StoreIfc store)
    {
        orderItemStatus.setPickupDate(pickupDate);
        if (customer != null)
        {
            orderItemStatus.setPickupFirstName(customer.getFirstName());
            orderItemStatus.setPickupLastName(customer.getLastName());
            orderItemStatus.setPickupContact(customer.getPrimaryPhone());
        }
        orderItemStatus.setPickupStoreID(store.getStoreID());
        orderItemStatus.setItemDispositionCode(OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP);
    }

    // ----------------------------------------------------------------------
    /**
     * validate pickup entered date
     *
     * @return result boolean
     */
    // ----------------------------------------------------------------------
    public boolean isEnteredDateCorrect()
    {
        boolean result = false;
        if (enteredDate != null)
        {
            if (enteredDate.after(currentSystemDate)
                    || (enteredDate.getDay() == currentSystemDate.getDay()
                            && enteredDate.getMonth() == currentSystemDate.getMonth() && enteredDate.getYear() == currentSystemDate
                            .getYear()))
            {
                result = true;
            }
            else
            {
                result = false;
            }
        }
        return result;
    }

    // ----------------------------------------------------------------------
    /**
     * Display dialogbox for incorrect entered date
     * <P>
     *
     * @param bus Service bus.
     */
    // ----------------------------------------------------------------------
    public void dialogForIncorrectDate(BusIfc bus)
    {
        // Using "generic dialog bean".
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("IncorrectEnteredDate");
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        // set and display the model
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
