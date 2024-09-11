/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcard/IsFundingSelectionOnlySignal.java /rgbustores_13.4x_generic_branch/1 2011/06/01 12:21:53 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.giftcard;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

/**
 * This signal is used for the navigation in the gift card tour to
 * only show the gift card funding selection, issue vs. reload.
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class IsFundingSelectionOnlySignal implements TrafficLightIfc
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc#roadClear(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public boolean roadClear(BusIfc bus)
    {
        return ((GiftCardCargo)bus.getCargo()).isFundingSelectionOnly();
    }

}
