/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  4/6/2013               Izhar                                       MAX-POS-Customer-FES_v1.2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.customer.delete;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.pos.services.customer.common.MAXEnterCustomerInfoSite;
import max.retail.stores.pos.ui.beans.MAXCustomerInfoBeanModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
//--------------------------------------------------------------------------
/**
    Put up Customer Delete screen.
    <p>
    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
//MAX Rev 1.0 Change : Start 
public class MAXDeleteCustomerInfoSite extends MAXEnterCustomerInfoSite
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: 3$";

    //----------------------------------------------------------------------
    /**
        Displays the Customer Delete screen. <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // model to use for the UI
    	CustomerCargo cargo =(CustomerCargo)bus.getCargo();
    	
    	 //MAX Rev 1.0 Change : Start 
        MAXCustomerInfoBeanModel model = (MAXCustomerInfoBeanModel) getCustomerInfoBeanModel(bus);
        //MAX Rev 1.0 Change : end 
        NavigationButtonBeanModel globalModel = new NavigationButtonBeanModel();
        NavigationButtonBeanModel localModel = new NavigationButtonBeanModel();
        // turn off editing
        model.setEditableFields(false);
        // disable Clear and Undo Buttons
        globalModel.setButtonEnabled("Next",false);
        globalModel.setButtonEnabled("Clear",false);
       
        model.setGlobalButtonBeanModel(globalModel);
        //MAX Rev 1.0 Change : Start 
        if(((MAXCustomer)cargo.getCustomer()).getCustomerType().equalsIgnoreCase("T")){
        localModel.setButtonEnabled("Delete",false);
        model.setLocalButtonBeanModel(localModel);
        }
        else
        {
        	localModel.setButtonEnabled("Delete",true);
            model.setLocalButtonBeanModel(localModel);
        	
        }
        //MAX Rev 1.0 Change : end 
        // show the screen
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        uiManager.showScreen(POSUIManagerIfc.CUSTOMER_DELETE, model);
    }
    
}
