/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/22/14 - Refactor to restore Fulfillment main option flow.
 *    asinton   10/22/14 - Initial checkin
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

/**
 * Signal to indicate that we're in the sale tour from the fulfillment tour.
 * @since 14.1
 *
 */
@SuppressWarnings("serial")
public class IsFromFulfillmentSignal implements TrafficLightIfc
{

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc#roadClear(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public boolean roadClear(BusIfc bus)
    {
        SaleCargoIfc saleCargo = (SaleCargoIfc)bus.getCargo();
        return saleCargo.isFromFulfillment() && saleCargo.getTransaction() == null;
    }

}
