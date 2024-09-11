/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/fill/LocationEnteredRoad.java /main/1 2013/01/10 14:03:53 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       01/04/13 - add new class
 *    sgu       01/03/13 - rename the class for xc only
 *    sgu       01/03/13 - add back order fill flow
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    abondala  02/27/09 - LayawayLocation and OrderLocation parameters are
 *                         changed to ReasonCodes.
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         11/27/2006 5:37:44 PM  Charles D. Baker
 *
 *   Revision 1.8  2004/10/06 02:44:24  mweis
 *   @scr 7012 Special and Web Orders now have Inventory.
 *
 *   Revision 1.7  2004/09/29 20:46:07  mweis
 *   @scr 7012 Elaborate 'Special' as part of the Order params.
 *
 *   Revision 1.6  2004/09/27 18:27:40  mweis
 *   @scr 7012 Special Order restoration of "oder list" (and fixes for SCR 7243).
 *
 *   Revision 1.5  2004/09/23 21:17:59  mweis
 *   @scr 7012 Special Order and Web Order parameters for POS Inventory
 *
 *   Revision 1.4  2004/06/29 22:03:32  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.3.2.2  2004/06/21 14:17:09  jeffp
 *   Removed unused imports
 *
 *   Revision 1.3.2.1  2004/06/14 17:48:08  aachinfiev
 *   Inventory location/state related modifications
 *
 *   Revision 1.3  2004/02/12 16:51:23  mcs
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
 *    Rev 1.0   Aug 29 2003 16:03:38   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:12:38   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:41:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 07 2002 10:53:50   dfh
 * updates to better save the order location, cleanup
 * Resolution for POS SCR-1522: Location for a Filled Special Order does not update correctly
 *
 *    Rev 1.0   Sep 24 2001 13:01:10   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.fill;

//foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LocationBeanModel;

//------------------------------------------------------------------------------
/**
    Retrieves the order location entered in the Edit order location screen.
    <P>
    @version $Revision: /main/1 $
**/
//------------------------------------------------------------------------------

public class LocationEnteredRoad extends LaneActionAdapter
{
    /**
       class name constant
    **/
    public static final String LANENAME = "LocationEnteredRoad";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/1 $";


    //--------------------------------------------------------------------------
    /**
       Retrieves the order location entered in the Edit order location screen.
       Updates the order with the new location.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        OrderCargo cargo = (OrderCargo)bus.getCargo();

        //retrieve the location from the model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        LocationBeanModel model = (LocationBeanModel)ui.getModel(POSUIManagerIfc.EDIT_LOCATION);

        String orderLocationCode = model.getSelectedLocation();

        // Save selected order location
        cargo.getOrder().getStatus().setLocation(orderLocationCode);
    }
}
