/* ===========================================================================
* Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/GiftCardLaunchShuttle.java /main/2 2013/05/22 10:41:02 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   05/21/13 - added GIFT_CARD_OPTIONS_SCREEN to be used when
 *                         appropriate instead of the POPUPMENU for same
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;

/**
 * This shuttle is used for the call to the gift card service for retrieving the
 * funding selection when refunding to a gift card.
 * @since 13.4
 */
@SuppressWarnings("serial")
public class GiftCardLaunchShuttle implements ShuttleIfc
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        GiftCardCargo giftCardCargo = (GiftCardCargo)bus.getCargo();
        giftCardCargo.setFundingSelectionOnly(true);
        // since coming from tender service we should show
        // the gift card options screen instead of the popup menu. 
        giftCardCargo.setGiftCardOptionScreen(true);
    }

}
