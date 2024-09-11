/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/CancelSelectedRoad.java /rgbustores_13.4x_generic_branch/1 2011/08/15 10:24:52 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   08/12/11 - Set tenderCanceled true upon user cancel
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * This road sets the tenderCanceled flag on the TenderCargo.
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class CancelSelectedRoad extends PosLaneActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        TenderCargo tenderCargo = (TenderCargo)bus.getCargo();
        tenderCargo.setTenderCanceled(true);
    }

}
