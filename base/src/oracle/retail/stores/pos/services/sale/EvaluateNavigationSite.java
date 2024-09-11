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
 *    asinton   10/22/14 - Initial checkin.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Evaluates tour navigation if from the Fulfillment main option flow.
 * 
 * @since 14.1
 *
 */
@SuppressWarnings("serial")
public class EvaluateNavigationSite extends PosSiteActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        SaleCargo cargo = (SaleCargo) bus.getCargo();
        String letter = CommonLetterIfc.CONTINUE;
        if(cargo.isFromFulfillment() && !cargo.isExitToFulfillment())
        {
            letter = "Fulfillment";
        }
        // In the case where we're done tendering the transaction we
        // need to know that we can (and should) go back to fulfillment.
        else if(cargo.isExitToFulfillment())
        {
            letter = CommonLetterIfc.SUCCESS;
        }
        
        bus.mail(letter, BusIfc.CURRENT);
    }

}
