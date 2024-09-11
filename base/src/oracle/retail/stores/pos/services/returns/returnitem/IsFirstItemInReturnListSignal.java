/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/IsFirstItemInReturnListSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       09/14/10 - adjust the current item index in non-receipted
 *                         return
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:27 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:17 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:32 PM  Robert Pearse
 *
 *   Revision 1.2  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.1  2004/03/12 16:26:09  baa
 *   @scr 3561 fix bugs with flow esc item size
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

// foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
    This ailse Checks if an item requires Size info.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class IsFirstItemInReturnListSignal implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 8915226081950740393L;

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       roadClear determines whether it is safe for the bus to proceed

       @param bus the bus trying to proceed
       @return true if not cashdrawer; false otherwise
    **/
    //--------------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        boolean isFirstItem = false;
        ReturnItemCargo cargo = (ReturnItemCargo)bus.getCargo();
        if (cargo.getCurrentItem() <= 0 || cargo.isExternalOrder())
        {
            isFirstItem = true;
        }

        return isFirstItem;
    }
}
