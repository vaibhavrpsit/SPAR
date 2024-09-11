/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcard/SetIssueRequestTypeAisle.java /rgbustores_13.4x_generic_branch/1 2011/06/01 12:21:54 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.giftcard;

import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Sets the request type in the Gift Card Cargo to GIFT_CARD_ISSUE.
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class SetIssueRequestTypeAisle extends PosLaneActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        GiftCardCargo giftCardCargo = (GiftCardCargo)bus.getCargo();
        giftCardCargo.setRequestType(GiftCardIfc.GIFT_CARD_ISSUE);
        bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
    }

}
