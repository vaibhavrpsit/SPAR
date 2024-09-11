/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  4/6/2013               Izhar                                      bug 7254
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/


package max.retail.stores.pos.services.customer.delete;

import max.retail.stores.pos.services.customer.common.MAXEnterCustomerInfoSite;
import max.retail.stores.pos.ui.beans.MAXCustomerInfoBeanModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

//--------------------------------------------------------------------------
/**
    Displays the Business Customer Delete screen.
    <p>
**/
//--------------------------------------------------------------------------
public class MAXDeleteBusinessInfoSite extends MAXEnterCustomerInfoSite
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
        MAXCustomerInfoBeanModel model = (MAXCustomerInfoBeanModel) getCustomerInfoBeanModel(bus);
        NavigationButtonBeanModel globalModel = new NavigationButtonBeanModel();

        // turn off editing
        model.setEditableFields(false);
        // disable Clear and Undo Buttons
        globalModel.setButtonEnabled("Next",false);
        globalModel.setButtonEnabled("Clear",false);
        model.setGlobalButtonBeanModel(globalModel);
        model.setBusinessCustomer(true);

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DELETE_BUSINESS, model);
    }
}
