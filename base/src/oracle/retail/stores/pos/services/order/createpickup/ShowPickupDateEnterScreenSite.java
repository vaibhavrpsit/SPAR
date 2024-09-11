/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/createpickup/ShowPickupDateEnterScreenSite.java /main/8 2013/08/27 14:46:50 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  08/25/13 - Xchannel Inventory lookup enhancement phase I
 *    mkutiana  12/28/12 - Setting a default pickup date
 *    jswan     04/13/12 - Moved to the order package during the cross
 *                         channel project to provide better organization for
 *                         the order create process.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - Pickup date enter screen
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.createpickup;

import oracle.retail.stores.domain.inventoryinquiry.promise.AvailableToPromiseInventoryIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.TagConstantsIfc;
import oracle.retail.stores.pos.services.order.xchannelcreatepickup.XChannelCreatePickupOrderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.GetPickupDateBeanModel;

@SuppressWarnings("serial")
public class ShowPickupDateEnterScreenSite extends PosSiteActionAdapter
{

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/8 $";

    //----------------------------------------------------------------------
    /**
     *   Displays the screen to enter pickup date.
     *   <P>
     *   @param  bus     Service bus.
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        PickupDeliveryOrderCargo pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        if (pickupDeliveryOrderCargo.getTransaction().getCustomer() != null)
        {
            if (pickupDeliveryOrderCargo.getTransaction().getCustomer().getFirstName() != null
                    && pickupDeliveryOrderCargo.getTransaction().getCustomer().getLastName() != null)
            {
                String[] vars = { pickupDeliveryOrderCargo.getTransaction().getCustomer().getFirstName(),
                        pickupDeliveryOrderCargo.getTransaction().getCustomer().getLastName() };
                UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
                String pattern = utility.retrieveText("CustomerAddressSpec", BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                        TagConstantsIfc.CUSTOMER_NAME_TAG, TagConstantsIfc.CUSTOMER_NAME_PATTERN_TAG);
                String name = LocaleUtilities.formatComplexMessage(pattern, vars);
                ui.customerNameChanged(name);
            }
            else
            {
                ui.customerNameChanged(pickupDeliveryOrderCargo.getTransaction().getCustomer().getCustomerID());
            }
        }
        //Pick up Date should default to today's date if item is currently in stock. 
        //If item is not in stock, date should default to expected available date
        GetPickupDateBeanModel beanModel = new GetPickupDateBeanModel();
        if (pickupDeliveryOrderCargo instanceof XChannelCreatePickupOrderCargo)
        {
            EYSDate todaysDate = new EYSDate(EYSDate.TYPE_DATE_ONLY);
            EYSDate defaultPickupDate = todaysDate;
            
            XChannelCreatePickupOrderCargo xchannelCreatePickupOrderCargo = (XChannelCreatePickupOrderCargo)pickupDeliveryOrderCargo;            
            StoreIfc currentStore = xchannelCreatePickupOrderCargo.getStoreForPickupByLineNum().get(
                    xchannelCreatePickupOrderCargo.getLineItemsBucket().get(xchannelCreatePickupOrderCargo.getLineItemIndex()).getItemBucket().get(0).getLineNumber());
            AvailableToPromiseInventoryIfc atpi = xchannelCreatePickupOrderCargo.getStoreItemAvailablity(
                    currentStore.getStoreID(), xchannelCreatePickupOrderCargo.getItemAvailablityList());
            if (atpi != null) {
                EYSDate availableDate =  atpi.getDate();
                availableDate.setType(EYSDate.TYPE_DATE_ONLY);                
                if (availableDate.after(todaysDate))
                {
                    defaultPickupDate = availableDate;
                }            
            }
            beanModel.setSelectedPickupDate(defaultPickupDate);
        }
        
        ui.showScreen(POSUIManagerIfc.GET_PICKUP_DATE_SCREEN, beanModel);
    }

}
