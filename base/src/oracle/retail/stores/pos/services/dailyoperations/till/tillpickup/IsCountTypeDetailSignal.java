/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpickup/IsCountTypeDetailSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:18 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *3    360Commerce 1.2         3/31/2005 4:28:26 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:22:15 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:11:31 PM  Robert Pearse   
 *
 Revision 1.3  2004/09/23 00:07:12  kmcbride
 @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 Revision 1.2  2004/04/30 17:13:17  dcobb
 @scr 4098 Open drawer before detail count screens.
 Pickup changed to open drawer before detaion count screens.
 *
 Revision 1.1  2004/04/29 20:06:21  dcobb
 @scr 4098 Open Drawer before detail count screens.
 Pickup changed to open drawer before detail count screens.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpickup;

import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//------------------------------------------------------------------------------
/**
    Determines if the pickup count type is Detail Count.
    
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class IsCountTypeDetailSignal implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -3420109721109599286L;

    /** revision number for this class */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
        Determines if the pickup count type is Detail Count.
        @param bus the bus arriving at this site
        @return true if the count type is Detail Count.
    **/
    //--------------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        TillPickupCargo cargo = (TillPickupCargo)bus.getCargo();
        return (cargo.getPickupCountType() == FinancialCountIfc.COUNT_TYPE_DETAIL);
    }

}
