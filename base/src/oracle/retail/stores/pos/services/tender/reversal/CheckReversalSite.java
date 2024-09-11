/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/reversal/CheckReversalSite.java /rgbustores_13.4x_generic_branch/5 2011/07/25 10:22:07 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   07/22/11 - Since cargo now has list of requests, simply check
 *                         list to determine if reversals are present.
 *    blarsen   07/19/11 - Changed to use the reversal service's new cargo
 *                         (ReversalCargo).
 *    blarsen   07/12/11 - Initial version. Now implemented.
 *    blarsen   07/08/11 - Initial version. Placeholder. Does nothing.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.reversal;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;


/**
 * Check cargo for tenders that need reversing.
 */
@SuppressWarnings("serial")
public class CheckReversalSite extends PosSiteActionAdapter
{

    /**
     * Checks for reversals.  If any found, continue on with the reversal process.
     */
    @Override
    public void arrive(BusIfc bus)
    {
        String letter = "Success";

        ReversalCargo cargo = (ReversalCargo)bus.getCargo();

        if (cargo.getRequestList() != null && cargo.getRequestList().size() > 0)
        {
            letter = "Continue";
        }

        if (letter != null)
        {
            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
    }

}
