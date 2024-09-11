/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/search/SearchByCustomerSite.java /main/2 2012/07/27 16:14:53 yiqzhao Exp $
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

//foundation imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.services.order.common.OrderSearchCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the ORDER_SEARCH_OPTIONS screen.  This site is used by
    the Pickup and Cancel Order services. Removes the customer name from 
    the screen.
    <P>
    @version $Revision: /main/2 $
**/
//------------------------------------------------------------------------------

public class SearchByCustomerSite extends PosSiteActionAdapter
{
    /**
       class name constant
    **/
    public static final String SITENAME = "SearchByCustomerSite";

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
        OrderCargo      cargo   = (OrderCargo) bus.getCargo();
        POSUIManagerIfc ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model  = new POSBaseBeanModel();

        // clear the customer's name in the status area
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setCustomerName("");
        model.setStatusBeanModel(statusModel);

        ui.showScreen(POSUIManagerIfc.ORDER_SEARCH_BY_CUSTOMER_OPT, model);
    }
    
    public void depart(BusIfc bus)
    {
        if ( !bus.getCurrentLetter().getName().equals(CommonLetterIfc.NEXT) )
        	return;
        
        OrderCargo      cargo   = (OrderCargo) bus.getCargo();
        POSUIManagerIfc ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        
        String customerID = ui.getInput();
        if ( customerID != null && customerID.length() > 0 )
        {
            CustomerIfc customer = DomainGateway.getFactory().getCustomerInstance();
            customer.setCustomerID(customerID);
            cargo.setSelectedCustomer(customer);
            cargo.setSearchMethod(OrderSearchCargoIfc.SEARCH_BY_CUSTOMER);
            cargo.setDateRange(false);
        }
    }
}