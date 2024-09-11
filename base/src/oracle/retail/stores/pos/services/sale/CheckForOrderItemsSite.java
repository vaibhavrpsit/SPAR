/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/CheckForOrderItemsSite.java /main/5 2014/07/21 14:54:18 vineesin Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vineesin  07/21/14 - Add take-with webstore item without showing
 *                         add-option-dialog in case of OrderPickup
 *    abhinavs  06/10/13 - Fix to display item number with description only
 *                         once for multiple quantities of the same item number
 *    yiqzhao   05/10/13 - Avoid PickupShipDialog display again if pick or
 *                         shipping is already selected. It happens when a
 *                         serialized item entered.
 *    yiqzhao   02/28/13 - Handle orderLineItems for cargos.
 *    yiqzhao   02/27/13 - Display the dialog if web store item(s) exist. The
 *                         item can be primary item, auto related item or
 *                         manually added (UPSELL, CROSSSELL, etc).
 *    yiqzhao   02/26/13 - Check for order item after adding related item(s).
 *    jswan     05/14/12 - Added to support the Ship button functionality.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import java.util.List;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Checks of for items that could potentially be order items.
    $Revision: /main/5 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class CheckForOrderItemsSite extends PosSiteActionAdapter
{
    private static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    private static final String XCHANNEL_ENABLED = "XChannelEnabled";
    private static final String PICKUP_SHIP_CHOICE = "PickupShipChoice";
    private static final String PICKUP_DELIVER_CHOICE = "PickupDeliverChoice";

    /**
        Checks of for items that could potentially be order items.
        @param bus Service Bus
     */
    public void arrive(BusIfc bus)
    {
        SaleCargo cargo = (SaleCargo) bus.getCargo();
        int size = cargo.getOrderLineItems().size();

        if (size>0)
        {
            for (SaleReturnLineItemIfc srli : cargo.getOrderLineItems())
            {
                srli.setSelectedForItemModification(true);
            }
            
            boolean isXChannel = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, 
                    XCHANNEL_ENABLED, false);

            if (isXChannel)
            {
                if (cargo.isPickupOrDeliveryExecuted() || (cargo.getRetailTransaction() != null && cargo.getRetailTransaction().isOrderPickupOrCancel()))
                {
                    bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
                }
                else
                {
                    displayPickupShipDialog(bus, cargo.getOrderLineItems());
                }
            }
            else
            {
                displayPickupDeliverDialog(bus, cargo.getOrderLineItems());
            }
        }
        else
        {
            bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
        }
        
    }
    /**
     * Display error dialog
     * @param bus Service bus.
     */
    protected void displayPickupShipDialog(BusIfc bus, List<SaleReturnLineItemIfc> lineItems)
    {
    	DialogBeanModel model = new DialogBeanModel();
    	model.setResourceID(PICKUP_SHIP_CHOICE);
    	model.setType(DialogScreensIfc.PICKUP_SHIP);
    	String[]args = new String[2];
    	args[0] = lineItems.get(0).getItemID();
    	args[1] = lineItems.get(0).getItemDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
    	model.setArgs(args);
    	POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
    	ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    /**
     * Display error dialog
     * @param bus Service bus.
     */
    protected void displayPickupDeliverDialog(BusIfc bus, List<SaleReturnLineItemIfc> lineItems)
    {
    	DialogBeanModel model = new DialogBeanModel();
    	model.setResourceID(PICKUP_DELIVER_CHOICE);
    	model.setType(DialogScreensIfc.PICKUP_DELIVER);
    	String[]args = new String[2];
    	args[0] = lineItems.get(0).getItemID();
    	args[1] = lineItems.get(0).getItemDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
    	model.setArgs(args);
    	POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
    	ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
