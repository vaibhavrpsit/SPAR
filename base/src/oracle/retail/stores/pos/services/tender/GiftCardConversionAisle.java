/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/GiftCardConversionAisle.java /rgbustores_13.4x_generic_branch/1 2011/06/01 12:21:54 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * Converts the letter to GiftCard for the site or station.
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class GiftCardConversionAisle extends PosLaneActionAdapter
{
    /** Constant for GiftCard letter */
    public static final String GIFT_CARD_LETTER = "GiftCard";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        bus.mail(GIFT_CARD_LETTER, BusIfc.CURRENT);
    }

}
