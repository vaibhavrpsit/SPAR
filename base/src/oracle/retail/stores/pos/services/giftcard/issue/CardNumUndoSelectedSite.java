/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcard/issue/CardNumUndoSelectedSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 11:02:37 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   04/26/11 - Refactor gift card for APF
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *       3    360Commerce 1.2         3/31/2005 4:27:21 PM   Robert Pearse   
 *       2    360Commerce 1.1         3/10/2005 10:20:00 AM  Robert Pearse   
 *       1    360Commerce 1.0         2/11/2005 12:09:49 PM  Robert Pearse   
 *
 *      Revision 1.4  2004/04/22 20:52:17  epd
 *      @scr 4513 FIxes to tender, especially gift card, gift cert, and store credit
 *
 *      Revision 1.3  2004/02/12 16:50:23  mcs
 *      Forcing head revision
 *
 *      Revision 1.2  2004/02/11 21:51:11  rhafernik
 *      @scr 0 Log4J conversion and code cleanup
 *
 *      Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *      updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Dec 19 2003 15:29:06   lzhao
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.giftcard.issue;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;

/**
 * The site will be execute when operator clicks on undo button on getGiftCardNum screen
 **/
@SuppressWarnings("serial")
public class CardNumUndoSelectedSite extends PosSiteActionAdapter
{
    /** the letter for get gift card amount */
    public static final String GET_GIFT_CARD_AMOUNT_LETTER = "GetCardAmount";

    /**
     * This method will be called when system is in GetCardNumForGiftCardIssueSite and
     * a user clicks on "Undo" button. The site should detemine which site need to go to
     * based on the gift card issue entry type
     * @param  bus     Service Bus
     */
    public void arrive(BusIfc bus)
    {
        GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
        LetterIfc letter = null;
        if (cargo.isDisplayedGetAmountScreen())
        {
            letter = new Letter(GET_GIFT_CARD_AMOUNT_LETTER);
        }
        else
        {
            letter = new Letter(CommonLetterIfc.UNDO);
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
}
