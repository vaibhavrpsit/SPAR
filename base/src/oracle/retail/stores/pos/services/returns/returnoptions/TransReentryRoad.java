/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/TransReentryRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     05/19/10 - Add transaction reentry classes
 *    jswan     05/19/10 - Add road for returns refactor.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

/**
 * 
 * Since transaction reentry transactions bypass the lookup of
 * return transactions by receipt or gift receipt, In this Road, we set the 
 * appropriate flags in the cargo to simulate a not found scenario 
 * w/o displaying the not found dialog screen.
 */
public class TransReentryRoad extends LaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = 3009152455925934137L;

    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        ReturnOptionsCargo cargo = (ReturnOptionsCargo) bus.getCargo();
        cargo.setHaveReceipt(true);
        cargo.setTransactionFound(false);
    }
}
