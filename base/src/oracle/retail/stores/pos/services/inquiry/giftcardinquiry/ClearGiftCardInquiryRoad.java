/* ===========================================================================
* Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/giftcardinquiry/ClearGiftCardInquiryRoad.java /main/1 2013/02/14 17:10:14 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   02/14/13 - return to the gift card entry screen when error is
 *                         encountered in the activation service which is used
 *                         for inquiries.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.inquiry.giftcardinquiry;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * Clears the gift card information from the cargo upon return to the
 * {@link EnterGiftCardNumberSite}.
 *
 * @since 14.0
 */
@SuppressWarnings("serial")
public class ClearGiftCardInquiryRoad extends PosLaneActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        InquiryCargo cargo = (InquiryCargo)bus.getCargo();
        cargo.setGiftCard(null);
    }

}
