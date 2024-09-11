/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/SetTimedOutRoad.java /main/1 2013/07/02 13:09:09 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/02/13 - Added to support non transactional timeouts in
 *                         instant credit.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

/**
 * If the cargo is a TimedCargoIfc, sets timeout to true.
 */
@SuppressWarnings("serial")
public class SetTimedOutRoad extends LaneActionAdapter
{
    /**
     * Stores Tax ID entered by the user.
     * 
     * @param bus
     *            the bus traversing this lane
     */
    public void traverse(BusIfc bus)
    {
        if ( bus.getCargo() instanceof TimedCargoIfc)
        {
            ((TimedCargoIfc) bus.getCargo()).setTimeout(true);
        }
    }

}
