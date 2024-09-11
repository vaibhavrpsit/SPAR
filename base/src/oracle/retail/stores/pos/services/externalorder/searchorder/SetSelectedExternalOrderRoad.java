/* ===========================================================================
* Copyright (c) 2009, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/searchorder/SetSelectedExternalOrderRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  05/19/10 - Display list of external orders flow
 *    abondala  05/18/10 - Class to set the selected external order on cargo.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.externalorder.searchorder;


import oracle.retail.stores.commerceservices.externalorder.ExternalOrderIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

//--------------------------------------------------------------------------
/**
    Road to set the selected external order entry from the external order list screen
    to search order cargo.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class SetSelectedExternalOrderRoad extends PosLaneActionAdapter
{

	private static final long serialVersionUID = 6497484952451924015L;

	/**
        road name constant
    **/
    public static final String LANENAME = "SetSelectedExternalOrderRoad";

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Sets the selected external order entry from the external orders list screen
       to search order cargo.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
    	SearchOrderCargo cargo  = (SearchOrderCargo) bus.getCargo();

        POSUIManagerIfc  ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        
        ListBeanModel  beanModel =
            (ListBeanModel) ui.getModel(POSUIManagerIfc.EXTERNAL_ORDER_LIST);
        
        ExternalOrderIfc externalOrder =  (ExternalOrderIfc)beanModel.getSelectedValue();

        cargo.setExternalOrder(externalOrder);
    }//end traverse
}
