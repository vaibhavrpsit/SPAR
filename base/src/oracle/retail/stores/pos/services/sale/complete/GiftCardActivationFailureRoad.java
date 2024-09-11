/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/GiftCardActivationFailureRoad.java /main/1 2014/02/10 15:44:37 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   02/10/14 - reworked flow for gift card activation error
 *                         scenarios
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.sale.complete;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

/**
 * Calls {@see SaleCargoIfc#setGiftCardActivationsCanceled(boolean)} to indicate that gift cards
 * have been canceled in the current transaction.
 * @since 14.0.1
 *
 */
@SuppressWarnings("serial")
public class GiftCardActivationFailureRoad extends PosLaneActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        cargo.setGiftCardActivationsCanceled(true);
    }

}
