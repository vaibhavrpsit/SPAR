/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/CheckForShippingSite.java /main/1 2012/06/29 11:53:42 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     06/29/12 - Add dialog for deleting ship item, disable change
*                        price for ship item
* yiqzhao     06/22/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Check to see if user is trying to delete items from multiple send 
    packages. If so, show error dialog screen. if not, call delete send
    road.
    $Revision: /main/1 $
**/
//--------------------------------------------------------------------------
public class CheckForShippingSite extends PosSiteActionAdapter
{
    /**
     revision number
     **/
    public static final String revisionNumber = "$Revision: /main/1 $";
    /**
     * This defines the dialog screen to display from the bundles.
     */
    public static String DELETE_SHIPPING_ITEM = "DeleteAllOrderLineItems";
    
    //----------------------------------------------------------------------
    /**
     * Check to see if user is trying to delete items from multiple send 
     * packages. If so, show error dialog screen. if not, call update 
     * shipping charge.
     * <P>
     * 
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        
        // Just show this dialog and exit the service
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(DELETE_SHIPPING_ITEM);
        model.setType(DialogScreensIfc.YES_NO);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);   

    }
}
