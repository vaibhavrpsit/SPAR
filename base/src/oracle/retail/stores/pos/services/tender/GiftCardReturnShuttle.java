/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/GiftCardReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/06/01 12:21:54 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;

/**
 * This shuttle is used to receive from the gift card service for retrieving the
 * funding selection when refunding to a gift card.
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class GiftCardReturnShuttle implements ShuttleIfc
{
    /**
     * The action code from the gift card service
     */
    private int actionCode;
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        GiftCardCargo giftCardCargo = (GiftCardCargo)bus.getCargo();
        actionCode = giftCardCargo.getActionCode();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        TenderCargo tenderCargo = (TenderCargo)bus.getCargo();
        if(GiftCardIfc.GIFT_CARD_ISSUE == actionCode)
        {
            tenderCargo.setAuthorizationTransactionType(GiftCardIfc.GIFT_CARD_ISSUE);
        }
        else if(GiftCardIfc.GIFT_CARD_RELOAD == actionCode)
        {
            tenderCargo.setAuthorizationTransactionType(GiftCardIfc.GIFT_CARD_RELOAD);
        }
    }

}
