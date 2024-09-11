/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/SetTimeoutFlagRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    asinton   04/22/10 - Modified redeem tour to fix timeout issue.
 *    asinton   04/22/10 - Road to set the timeout flag on the cargo in order
 *                         to rememeber that a timeout letter carried us out of
 *                         the tender service.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * This road sets the timeout flag to true in the RedeemCargo.
 */
public class SetTimeoutFlagRoad extends PosLaneActionAdapter
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4221203504720298838L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        RedeemCargo cargo = (RedeemCargo)bus.getCargo();
        cargo.setInactiveTimeout(true);
    }
    
}
