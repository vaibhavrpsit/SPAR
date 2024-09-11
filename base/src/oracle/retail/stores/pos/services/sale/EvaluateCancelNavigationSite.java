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

import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Evaluates the navigation choices upon Cancel letter.
 * @since 14.1
 *
 */
@SuppressWarnings("serial")
public class EvaluateCancelNavigationSite extends PosSiteActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        String letter = CommonLetterIfc.CONTINUE;
        SaleCargoIfc saleCargo = (SaleCargoIfc)bus.getCargo();
        if(saleCargo.getTransaction() instanceof LayawayTransactionIfc)
        {
            letter = CommonLetterIfc.CANCEL;
        }
        // since we're canceling from ModifyTransaction, the only case where we would have a
        // transaction is for layaway, therefore we don't test if a transaction is present here.
        else if(saleCargo.isFromFulfillment())
        {
            letter = CommonLetterIfc.UNDO;
        }
        bus.mail(letter, BusIfc.CURRENT);
    }

}
