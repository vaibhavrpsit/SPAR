/* ===========================================================================
* Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/add/CustomerAddedAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:26 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 1    360Commerce 1.0         12/13/2005 4:47:06 PM  Barry A. Pape   
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.add;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This aisle creates a simple confirmation dialog.
 * @author jdeleau
 *
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @since NEP67
 */
public class CustomerAddedAisle extends LaneActionAdapter
{
    /**
     * This shows a confirmation dialog, after a customer has been successfully added.
     * 
     * @since NEP67
     * @see oracle.retail.stores.foundation.tour.ifc.LaneActionIfc#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();

        // Error dialog screen
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("CustomerAdded");
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CONTINUE);
        String args[] = new String[1];
        args[0] = cargo.getCustomer().getCustomerID();
        model.setArgs(args);

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
