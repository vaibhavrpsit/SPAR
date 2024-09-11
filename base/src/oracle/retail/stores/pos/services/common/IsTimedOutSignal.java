/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   11/17/14 - fixed timeout issue where, because of the call to
 *                         CancelTransactionStation, the flow forgets that a
 *                         timeout has been called.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

/**
 * This signal indicates if the <code>TimedCargoIfc.isTimeout()</code> is true.
 * @since 14.1
 *
 */
@SuppressWarnings("serial")
public class IsTimedOutSignal implements TrafficLightIfc
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc#roadClear(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public boolean roadClear(BusIfc bus)
    {
        TimedCargoIfc timedCargo = (TimedCargoIfc)bus.getCargo();
        return timedCargo.isTimeout();
    }

}
