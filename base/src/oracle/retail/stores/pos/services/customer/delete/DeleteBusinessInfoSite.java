/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/delete/DeleteBusinessInfoSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:26 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.delete;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.customer.common.EnterCustomerInfoSite;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

//--------------------------------------------------------------------------
/**
    Displays the Business Customer Delete screen.
    <p>
**/
//--------------------------------------------------------------------------
public class DeleteBusinessInfoSite extends EnterCustomerInfoSite
{
    /**
        revision number
    **/
    public static final String revisionNumber = "";

    //----------------------------------------------------------------------
    /**
        Displays the Business Customer Delete screen.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
         // model to use for the UI
        CustomerInfoBeanModel model = getCustomerInfoBeanModel(bus);
        NavigationButtonBeanModel globalModel = new NavigationButtonBeanModel();

        // turn off editing
        model.setEditableFields(false);
        // disable Clear and Undo Buttons
        globalModel.setButtonEnabled(CommonActionsIfc.NEXT,false);
        globalModel.setButtonEnabled(CommonActionsIfc.CLEAR,false);
        
        model.setGlobalButtonBeanModel(globalModel);
        model.setBusinessCustomer(true);

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DELETE_BUSINESS, model);
    }
}
