/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/createdelivery/ShowDeliveryDateEnterScreenSite.java /main/5 2012/05/02 14:07:48 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     04/13/12 - Moved to the ‘order’ package during the cross
 *                         channel project to provide better organization for
 *                         the order create process.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - show delivery date enter screen
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.createdelivery;

import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.TagConstantsIfc;
import oracle.retail.stores.pos.services.order.createpickup.PickupDeliveryOrderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.GetDeliveryDateBeanModel;

@SuppressWarnings("serial")
public class ShowDeliveryDateEnterScreenSite extends PosSiteActionAdapter
{

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/5 $";

    // ----------------------------------------------------------------------
    /**
     * Displays the screen to enter delivery date.
     * <P>
     *
     * @param bus Service bus.
     */
    // ----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        PickupDeliveryOrderCargo pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

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
        ui.showScreen(POSUIManagerIfc.GET_DELIVERY_DATE_SCREEN, new GetDeliveryDateBeanModel());

    }

}
