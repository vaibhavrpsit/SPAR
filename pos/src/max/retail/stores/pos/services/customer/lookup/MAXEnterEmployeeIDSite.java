/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  9/7/2013	Prateek			Changes done for BUG 6886
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.customer.lookup;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
    Get the employee ID to use in the customer search.
    $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXEnterEmployeeIDSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: 3$";

    //----------------------------------------------------------------------
    /**
        Displays screen for operator to input the employee id.
        This initiates a lookup of customer based on employee id.
        Multiple customers may be returned depending on how many
        customers are associated with the employee.
        <p>
        @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        CustomerCargo cargo = (CustomerCargo) bus.getCargo();

        // Set the screen ID and bean type
        POSBaseBeanModel model = new POSBaseBeanModel();
        PromptAndResponseModel prModel = new PromptAndResponseModel();
        
        
        String employeeID = cargo.getCustomer().getEmployeeID();
       /* 
        NavigationButtonBeanModel globalModel = new NavigationButtonBeanModel();
        
        if (employeeID != null)
        {
            prModel.setResponseText(employeeID);
            globalModel.setButtonEnabled("Next", true);
        }
        else
        	globalModel.setButtonEnabled("Next", false);
        model.setPromptAndResponseModel(prModel);
        model.setGlobalButtonBeanModel(globalModel);*/
        // Display the screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.FIND_CUSTOMER_EMPLOYEE_ID, model);
    }


}
