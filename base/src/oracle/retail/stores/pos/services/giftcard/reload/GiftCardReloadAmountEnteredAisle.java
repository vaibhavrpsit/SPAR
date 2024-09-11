/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcard/reload/GiftCardReloadAmountEnteredAisle.java /rgbustores_13.4x_generic_branch/2 2011/09/07 08:33:37 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     09/06/11 - Fixed issues with gift card balance when
 *                         issuing/reloading multiple gift cards and one card
 *                         fails.
 *    asinton   04/26/11 - Refactor gift card for APF
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
 *       5    360Commerce 1.4         4/25/2007 8:52:26 AM   Anda D. Cadar
 *            I18N merge
 *
 *       4    360Commerce 1.3         1/22/2006 11:45:07 AM  Ron W. Haight
 *            removed references to com.ibm.math.BigDecimal
 *       3    360Commerce 1.2         3/31/2005 4:28:17 PM   Robert Pearse
 *       2    360Commerce 1.1         3/10/2005 10:21:55 AM  Robert Pearse
 *       1    360Commerce 1.0         2/11/2005 12:11:14 PM  Robert Pearse
 *
 *      Revision 1.13  2004/08/20 19:52:40  blj
 *      @scr 6855 - update after code review
 *
 *      Revision 1.12  2004/08/20 19:46:30  blj
 *      @scr 6855 - update after code review
 *
 *      Revision 1.11  2004/08/19 21:55:41  blj
 *      @scr 6855 - Removed old code and fixed some flow issues with gift card credit.
 *
 *      Revision 1.10  2004/06/21 22:19:26  lzhao
 *      @scr 5774, 5447: gift card return/reload.
 *
 *      Revision 1.9  2004/06/11 19:13:15  lzhao
 *      @scr 5396 fix the problem in return exchange
 *
 *      Revision 1.8  2004/05/11 16:05:29  blj
 *      @scr 4603 - fixed for post void of giftcard issue/reload/redeem/credit
 *
 *      Revision 1.7  2004/04/14 20:10:51  lzhao
 *      @scr  3872 Redeem, change gift card request type from String to in.
 *
 *      Revision 1.6  2004/04/07 21:10:09  lzhao
 *      @scr 3872: gift card redeem and revise gift card activation
 *
 *      Revision 1.5  2004/03/30 20:34:12  bwf
 *      @scr 4165 Gift Card Rework
 *
 *      Revision 1.4  2004/03/03 23:15:12  bwf
 *      @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *      Revision 1.3  2004/02/12 16:50:24  mcs
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
 *    Rev 1.6   Feb 04 2004 15:25:58   blj
 * more gift card refund work.
 *
 *    Rev 1.5   Dec 19 2003 15:21:44   lzhao
 * issue code review follow up
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.4   Dec 16 2003 11:15:52   lzhao
 * code review follow up
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.3   Dec 08 2003 09:30:48   lzhao
 * change reload amount to amount.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.2   Nov 26 2003 09:27:00   lzhao
 * cleanup, use the mehods in gift card utilties.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.1   Nov 21 2003 15:05:30   lzhao
 * refactory gift card using sale, completesale, giftoptions services and giftcardutilites.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.0   Oct 30 2003 10:57:44   lzhao
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.giftcard.reload;


import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;

/**
 * This aisle retrieves the reload amount from the UI.
 */
@SuppressWarnings("serial")
public class GiftCardReloadAmountEnteredAisle extends PosLaneActionAdapter
{
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void traverse(BusIfc bus)
    {
        GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
        UtilityManagerIfc utility =
            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
         ParameterManagerIfc pm =
            (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        String letterName = bus.getCurrentLetter().getName();
        LetterIfc letter  = null;

        //create the transaction if it doesn't exist.
        if (cargo.getTransaction() == null)
        {
            cargo.initializeTransaction(bus);
            cargo.getTransaction().setSalesAssociate(cargo.getTransaction().getCashier());
        }

         // get amount from user's input/selection
        CurrencyIfc amount;
        if ( cargo.getTransaction().isTrainingMode() )
        {
            amount = GiftCardUtilities.getCurrency("10.0");
        }
        else if (letterName.equals(CommonLetterIfc.NEXT))
        {
            // get the amount from input text
            String amountString =
                     LocaleUtilities
                        .parseCurrency(
                            ui.getInput(),
                            LocaleMap.getLocale(LocaleMap.DEFAULT))
                        .toString();
            amount = GiftCardUtilities.getCurrency(amountString);
        }
        else
        {
            // get the amount from a amount button click
            amount = GiftCardUtilities.getButtonDenomination(pm, letterName, logger, bus.getServiceName());
        }

        // check amount
        if ( GiftCardUtilities.isMoreThanMax(pm, amount, logger, bus.getServiceName()) )
        {
            DialogBeanModel dModel = GiftCardUtilities.getMoreThanMaxDialogModel(utility);
            if (cargo.getTransaction().getTransactionType() == TransactionIfc.TYPE_RETURN)
            {
                dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.UNDO);
            }
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
                          dModel);
        }
        else if ( GiftCardUtilities.isLessThanMin(pm, amount, logger, bus.getServiceName()) )
        {
            DialogBeanModel dModel = GiftCardUtilities.getLessThanMinDialogModel(utility);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
                          dModel);
        }
        else
        {
            GiftCardIfc giftCard = DomainGateway.getFactory().getGiftCardInstance();
            giftCard.setReqestedAmount(amount);
            giftCard.setRequestType(GiftCardIfc.GIFT_CARD_RELOAD);

            cargo.setGiftCard(giftCard);
            GiftCardBeanModel model = new GiftCardBeanModel();
            if (ui.getModel() instanceof GiftCardBeanModel)
            {
                model = (GiftCardBeanModel) ui.getModel();
            }
            model.setGiftCardAmount(new BigDecimal(amount.getStringValue()));
            ui.setModel(POSUIManagerIfc.SELL_GIFT_CARD, model);
            letter = new Letter(CommonLetterIfc.CONTINUE);
        }

        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

}
