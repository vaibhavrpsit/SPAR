/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/search/DisplayOrderSearchOptionsSite.java /main/3 2013/01/14 11:39:58 mkutiana Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* mkutiana    01/10/13 - implementing Item Age Verification for order pickup
* yiqzhao     07/27/12 - modify order search flow and populate order cargo for
*                        searching
* yiqzhao     07/23/12 - modify order search flow for xchannel order and
*                        special order
* yiqzhao     07/19/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.search;

//foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the ORDER_SEARCH_OPTIONS screen.  This site is used by
    the Pickup and Cancel Order services. Removes the customer name from 
    the screen.
    <P>
    @version $Revision: /main/3 $
**/
//------------------------------------------------------------------------------

public class DisplayOrderSearchOptionsSite extends PosSiteActionAdapter
{
    /**
       class name constant
    **/
    public static final String SITENAME = "DisplayOrderSearchOptionsSite";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/3 $";

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
        
        //Clear the PickupDOB 
        cargo.setPickupDOB(null);

        // clear the customer's name in the status area
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setCustomerName("");
        model.setStatusBeanModel(statusModel);

        ui.showScreen(POSUIManagerIfc.ORDER_SEARCH_OPTIONS, model);
    }
}