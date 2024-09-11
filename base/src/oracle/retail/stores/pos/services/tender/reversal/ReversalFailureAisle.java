/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/reversal/ReversalFailureAisle.java /rgbustores_13.4x_generic_branch/2 2011/08/04 14:22:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   07/13/11 - "Initial version."
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.reversal;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Reversals are fire-and-forget and should never fail at the tour level.
 *
 * Log an error and convert to a success letter.
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
@SuppressWarnings("serial")
public class ReversalFailureAisle extends PosLaneActionAdapter
{

    /**
     * Essentially converts failure letters to success letters
     *
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        logger.error("Reversals failed. Reversals are fire-and-forget.  " +
                        "There may be a problem with PaymentManager CommExt configuration or something may be preventing " +
                        "reversal requests from being written to a file queue.");

        Letter letter = new Letter(CommonLetterIfc.SUCCESS);
        bus.mail(letter, BusIfc.CURRENT);
    }

}
