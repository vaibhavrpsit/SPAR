/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  3/4/2013               Izhar                                       MAX-POS-Customer-FES_v1.2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/


package max.retail.stores.pos.services.customer.lookup;

import max.retail.stores.pos.ui.beans.MAXCustomerInfoBeanModel;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.customer.lookup.FindCustomerInfoSite;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
//--------------------------------------------------------------------------
/**
    Put up Customer Info screen for input of customer name and address
    information.
    $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXFindCustomerInfoSite extends FindCustomerInfoSite
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: 3$";

    //----------------------------------------------------------------------
    /**
        Displays the Customer Info screen for input of search criteria
        of a customer. <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
          // model to use for the UI
   //   MAX Rev 1.0 Change : Start 
    	 CustomerCargo cargo = (CustomerCargo)bus.getCargo();
 
          UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
          ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE); 
          MAXCustomerInfoBeanModel model = new MAXCustomerInfoBeanModel();
		     //   MAX Rev 1.0 Change : end
          model.setPhoneTypes(CustomerUtilities.getPhoneTypes(utility));
          model.setCountries(utility.getCountriesAndStates(pm));
          //State info is not used for customer lookup.
          model.setStateIndex(-1);
          // show the screen
          POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

          uiManager.showScreen(POSUIManagerIfc.FIND_CUSTOMER_INFO, model);
    }


}
