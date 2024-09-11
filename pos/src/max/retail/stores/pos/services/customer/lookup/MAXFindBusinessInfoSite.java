/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  11/April/2013               Izhar                                       MAX-POS-Customer-FES_v1.2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.customer.lookup;

import max.retail.stores.pos.ui.beans.MAXCustomerInfoBeanModel;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.customer.lookup.FindBusinessInfoSite;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
//--------------------------------------------------------------------------
/**
     Displays the Business Customer Search screen for input of search
     criteria of a business customer. <p>
**/
//--------------------------------------------------------------------------
public class MAXFindBusinessInfoSite extends FindBusinessInfoSite
{
    /**
        revision number
    **/
    public static final String revisionNumber = "";

    //----------------------------------------------------------------------
    /**
        Displays the Business Customer Search screen for input of search
        criteria of a business customer. <p>
        @param  bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
 
          UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
          ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE); 
		  //  MAX Rev 1.0 Change : Start 
          MAXCustomerInfoBeanModel model = new MAXCustomerInfoBeanModel();
          //  MAX Rev 1.0 Change : end
          model.setCountries(utility.getCountriesAndStates(pm));
          model.setBusinessCustomer(true);
          POSUIManagerIfc uiManager = 
              (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
          uiManager.showScreen(POSUIManagerIfc.BUSINESS_SEARCH, model);
    }
}
