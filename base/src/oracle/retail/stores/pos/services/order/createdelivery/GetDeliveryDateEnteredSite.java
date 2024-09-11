/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/createdelivery/GetDeliveryDateEnteredSite.java /main/10 2013/04/16 13:32:35 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    sgu       07/03/12 - added xc order ship delivery date, carrier code and
 *                         type code
 *    jswan     04/13/12 - Moved to the ‘order’ package during the cross
 *                         channel project to provide better organization for
 *                         the order create process.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  02/26/09 - Rework for PDO functionality
 *    aphulamb  12/23/08 - Mock padding fix and PDO flow related changes for
 *                         buttons enable/disable
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/18/08 - Pickup Delivery Order
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - get delivery date enter
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.createdelivery;

import java.util.Iterator;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.createpickup.PickupDeliveryOrderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.GetDeliveryDateBeanModel;

public class GetDeliveryDateEnteredSite extends PosSiteActionAdapter
{
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/10 $";

    protected EYSDate enteredDate = null;

    protected EYSDate currentSystemDate = null;

    // ----------------------------------------------------------------------
    /**
     * validation for delivery date and set the delivery date into
     * OrderDeliveryDetail Object
     * <P>
     *
     * @param bus Service bus.
     */
    // ----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        PickupDeliveryOrderCargo pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        GetDeliveryDateBeanModel model = (GetDeliveryDateBeanModel)ui
                .getModel(POSUIManagerIfc.GET_DELIVERY_DATE_SCREEN);
        SaleReturnLineItemIfc[] lineItems = pickupDeliveryOrderCargo.getLineItems();

        // get the delivery date from the model
        enteredDate = model.getSelectedDeliveryDate();
        currentSystemDate = DomainGateway.getFactory().getEYSDateInstance();

        // entered delivery date is valid
        KitHeaderLineItemIfc parentKitItem=null;
        KitComponentLineItemIfc childKit=null;

        if (isEnteredDateCorrect())
        {
            pickupDeliveryOrderCargo.setDeliveryAction(true);
            for (int i = 0; i < lineItems.length; i++)
            {
                lineItems[i].getOrderItemStatus().getDeliveryDetails().setDeliveryDate(enteredDate);
                lineItems[i].getOrderItemStatus().setItemDispositionCode(
                        OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY);
                if (lineItems[i].isKitHeader())
                {
                    parentKitItem = (KitHeaderLineItemIfc)lineItems[i];
                    Iterator<KitComponentLineItemIfc> childKitItemIter = parentKitItem.getKitComponentLineItems();
                    while (childKitItemIter.hasNext())
                    {
                        childKit = childKitItemIter.next();
                        childKit.getOrderItemStatus().getDeliveryDetails().setDeliveryDate(enteredDate);
                        childKit.getOrderItemStatus().setItemDispositionCode(
                                OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY);

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
     * validate delivery entered date
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
