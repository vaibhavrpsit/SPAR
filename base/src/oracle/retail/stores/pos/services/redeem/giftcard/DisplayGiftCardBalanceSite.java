/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/giftcard/DisplayGiftCardBalanceSite.java /rgbustores_13.4x_generic_branch/3 2011/07/26 11:52:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/26/11 - removed tenderauth and giftcard.activation tours and
 *                         financialnetwork interfaces.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         3/11/2008 6:08:31 PM   Michael P. Barnett
 *      Display the truncated gift card number rather than the encrypted
 *      value.
 * 3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:21:03 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:10:38 PM  Robert Pearse
 *
 *Revision 1.8  2004/04/22 17:33:20  lzhao
 *@scr 3872: code review. remove unused code.
 *
 *Revision 1.7  2004/04/15 16:18:14  lzhao
 *@scr 3872: gift card redeem
 *
 *Revision 1.6  2004/04/14 20:10:07  lzhao
 *@scr  3872 Redeem, change gift card request type from String to in.
 *
 *Revision 1.5  2004/04/13 19:02:22  lzhao
 *@scr 3872: gift card redeem.
 *
 *Revision 1.4  2004/04/08 20:33:02  cdb
 *@scr 4206 Cleaned up class headers for logs and revisions.
 *
 *Revision 1.3  2004/04/07 21:10:08  lzhao
 *@scr 3872: gift card redeem and revise gift card activation
 *
 *Revision 1.2  2004/03/31 16:17:23  lzhao
 *@scr 3872: gift card redeem service update
 *
 *Revision 1.1  2004/03/25 23:01:23  lzhao
 *@scr #3872 Redeem Gift Card
 *
 *Revision 1.2  2004/03/23 00:30:23  lzhao
 *@scr 3872 - add Next for "ReadyToRedeem".
 *
 *Revision 1.1  2004/03/22 23:59:08  lzhao
 *@scr 3872 - add gift card redeem (initial)
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem.giftcard;

import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.redeem.RedeemCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

/**
 * Displays the gift card information screen which includes gift card number and
 * current balance.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/3 $
 */
public class DisplayGiftCardBalanceSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -6782671880734915291L;
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/3 $";
    /**
     * gift card number field tag contant
     */
    protected static final String GIFT_CARD_NUMBER_FIELD = "giftCardNumberField";
    /**
     * gift card current balance tag contant
     */
    protected static final String GIFT_CARD_CURRENT_BALANCE = "giftCardBalanceField";

    /**
     * The arrive method is called for display the gift card information before
     * doing the redeem. The screen shows the gift card information which
     * includes gift card number and current balance.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        RedeemCargo cargo = (RedeemCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        GiftCardIfc giftCard = cargo.getGiftCard();
        if (giftCard != null)
        {
            giftCard.setRequestType(GiftCardIfc.GIFT_CARD_REDEEM);
            cargo.setCurrentAmount(giftCard.getCurrentBalance());

            DataInputBeanModel dModel = new DataInputBeanModel();
            dModel.setValue(GIFT_CARD_NUMBER_FIELD, giftCard.getEncipheredCardData().getTruncatedAcctNumber());
            dModel.setValue(GIFT_CARD_CURRENT_BALANCE, giftCard.getCurrentBalance().toFormattedString());

            ui.showScreen(POSUIManagerIfc.GIFT_CARD_REDEEM, dModel);
        }
        else if (logger.isInfoEnabled())
        {
            logger.info("DisplayGiftCardBalanceSite.arrive(), giftCard = " + giftCard);
        }
    }
}
