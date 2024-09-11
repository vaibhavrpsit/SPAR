/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/DisplayOrderOptionsSite.java /main/12 2013/07/02 16:32:12 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/17/14 - Disable cancel button if calling from fulfillment
 *    yiqzhao   07/02/13 - Disable sliding order buttons for Fulfillment at
 *                         login in main menu.
 *    sgu       01/03/13 - rename the class for xc only
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:20  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:03:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:11:26   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:32   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 24 2001 13:00:04   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order;

//foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the menu screen for selecting order options.

    @version $Revision: /main/12 $
**/
//------------------------------------------------------------------------------

public class DisplayOrderOptionsSite extends PosSiteActionAdapter
{

    /**
       class name constant
    **/
    public static final String SITENAME = "DisplayOrderOptionsSite";
    
    private static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    private static final String XCHANNEL_ENABLED = "XChannelEnabled";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    //--------------------------------------------------------------------------
    /**
       Displays the order options screen.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        POSBaseBeanModel model = new POSBaseBeanModel();
        ui.customerNameChanged("");
        if (isXChannelEnabled())
        {
            CargoIfc cargo = bus.getCargo();
            if ( cargo instanceof OrderCargo && !((OrderCargo)cargo).isPopupMenuNeeded())
            {
                if (((OrderCargo)cargo).isFromFulfillment())
                {
                    NavigationButtonBeanModel globalModel = new NavigationButtonBeanModel();
                    globalModel.setButtonEnabled(CommonActionsIfc.CANCEL, false);
                    model.setGlobalButtonBeanModel(globalModel);
                }
                ui.showScreen(POSUIManagerIfc.XC_ORDER_OPTIONS_NO_POPUPMENU, model); 
            }
            else
            {
                ui.showScreen(POSUIManagerIfc.XC_ORDER_OPTIONS, model);
            }
        }
        else
        {
            ui.showScreen(POSUIManagerIfc.ORDER_OPTIONS, model);
        }
    }
    
    /**
     * @return a flag indicating if cross channel is enabled.
     */
    protected boolean isXChannelEnabled()
    {
        return Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, XCHANNEL_ENABLED, false);
    }
}
