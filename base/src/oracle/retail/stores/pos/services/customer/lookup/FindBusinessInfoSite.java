/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/lookup/FindBusinessInfoSite.java /main/11 2012/12/13 10:05:11 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  12/13/12 - customer search criteria fields are all optional.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.lookup;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
//--------------------------------------------------------------------------
/**
     Displays the Business Customer Search screen for input of search
     criteria of a business customer. <p>
**/
//--------------------------------------------------------------------------
public class FindBusinessInfoSite extends PosSiteActionAdapter
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
          CustomerInfoBeanModel model = new CustomerInfoBeanModel();
          
          model.setCountries(utility.getCountriesAndStates(pm));
          model.setBusinessCustomer(true);
          model.setReceiptModes(CustomerUtilities.getReceiptPreferenceTypes(utility));
          POSUIManagerIfc uiManager = 
              (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
          uiManager.showScreen(POSUIManagerIfc.BUSINESS_SEARCH, model);
    }
}
