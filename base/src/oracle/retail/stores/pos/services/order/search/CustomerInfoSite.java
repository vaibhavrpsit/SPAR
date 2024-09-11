/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/search/CustomerInfoSite.java /main/2 2012/07/27 16:14:52 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     07/27/12 - modify order search flow and populate order cargo for
*                        searching
* yiqzhao     07/23/12 - modify order search flow for xchannel order and
*                        special order
* yiqzhao     07/20/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.order.search;

import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.OrderCustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;



public class CustomerInfoSite extends PosSiteActionAdapter
{
    /**
       class name constant
    **/
    public static final String SITENAME = "CustomerInfoSite";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/2 $";

    //--------------------------------------------------------------------------
    /**
       Displays the Order Search Options screen and removes the customer name
       from the status area.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        
        OrderCustomerInfoBeanModel model  = new OrderCustomerInfoBeanModel();
        model.setCountries(utility.getCountriesAndStates(pm));

        // clear the customer's name in the status area
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setCustomerName("");
        model.setStatusBeanModel(statusModel);

        ui.showScreen(POSUIManagerIfc.ORDER_SEARCH_BY_CUSTOMER, model);
    }
}