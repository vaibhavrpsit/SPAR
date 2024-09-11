/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/pickup/UndoTenderRoad.java /main/11 2012/10/19 12:46:37 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       10/17/12 - no longer need to save order
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:39 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:31 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:22 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:51:26  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:03:54   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:12:08   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:41:50   msg
 * Initial revision.
 *
 *    Rev 1.1   29 Jan 2002 15:13:10   sfl
 * Get the previous saved order to support the ESC in the
 * Tender Options screen during doing the tender
 * for a special order.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   26 Jan 2002 18:49:02   cir
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.pickup;
//foundation imports
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//------------------------------------------------------------------------------
/**
    This road is traversed when the user presses the
    Undo key from the tender screen.
    <p>
    @version $Revision: /main/11 $
**/
//------------------------------------------------------------------------------

public class UndoTenderRoad extends PosLaneActionAdapter
{

    //--------------------------------------------------------------------------
    /**
        Clears the line display device that has left over info from tender
        Resets the order line items
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        PickupOrderCargo cargo = (PickupOrderCargo)bus.getCargo();

        //clear the line display device
        try
        {
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
            pda.clearText();
        }
        catch (DeviceException e)
        {
            logger.warn(
                        "Unable to use Line Display: " + e.getMessage() + "");
        }
    }
}
