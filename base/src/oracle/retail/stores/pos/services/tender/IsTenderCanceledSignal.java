/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/IsTenderCanceledSignal.java /rgbustores_13.4x_generic_branch/1 2011/08/15 10:24:52 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   08/12/11 - Initial
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

/**
 * Is tender canceled signal.
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class IsTenderCanceledSignal implements TrafficLightIfc
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc#roadClear(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public boolean roadClear(BusIfc bus)
    {
        return ((TenderCargo)bus.getCargo()).isTenderCanceled();
    }

}
