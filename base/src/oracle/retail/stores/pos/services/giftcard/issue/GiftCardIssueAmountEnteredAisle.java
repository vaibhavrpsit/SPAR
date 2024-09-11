/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcard/issue/GiftCardIssueAmountEnteredAisle.java /main/13 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   03/30/12 - implemented giftcard issue
 *    jswan     09/06/11 - Fixed issues with gift card balance when
 *                         issuing/reloading multiple gift cards and one card
 *                         fails.
 *    cgreene   07/26/11 - moved StatusCode to GiftCardifc
 *    cgreene   05/27/11 - move auth response objects into domain
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
 *       6    360Commerce 1.5         4/25/2007 8:52:26 AM   Anda D. Cadar
 *            I18N merge
 *
 *       5    360Commerce 1.4         5/12/2006 5:25:28 PM   Charles D. Baker
 *            Merging with v1_0_0_53 of Returns Managament
 *       4    360Commerce 1.3         1/22/2006 11:45:06 AM  Ron W. Haight
 *            removed references to com.ibm.math.BigDecimal
 *       3    360Commerce 1.2         3/31/2005 4:28:16 PM   Robert Pearse
 *       2    360Commerce 1.1         3/10/2005 10:21:54 AM  Robert Pearse
 *       1    360Commerce 1.0         2/11/2005 12:11:13 PM  Robert Pearse
 *
 *      Revision 1.14  2004/08/20 19:42:01  blj
 *      @scr 6855 - update from code review
 *
 *      Revision 1.13  2004/08/19 21:55:41  blj
 *      @scr 6855 - Removed old code and fixed some flow issues with gift card credit.
 *
 *      Revision 1.12  2004/06/24 15:31:38  blj
 *      @scr 5185 - Had to update gift card credit to get Amount from the tenderAttributes
 *
 *      Revision 1.11  2004/06/21 22:19:26  lzhao
 *      @scr 5774, 5447: gift card return/reload.
 *
 *      Revision 1.10  2004/06/11 19:13:15  lzhao
 *      @scr 5396 fix the problem in return exchange
 *
 *      Revision 1.9  2004/05/11 16:05:29  blj
 *      @scr 4603 - fixed for post void of giftcard issue/reload/redeem/credit
 *
 *      Revision 1.8  2004/04/22 20:52:17  epd
 *      @scr 4513 FIxes to tender, especially gift card, gift cert, and store credit
 *
 *      Revision 1.7  2004/04/14 20:10:43  lzhao
 *      @scr  3872 Redeem, change gift card request type from String to in.
 *
 *      Revision 1.6  2004/03/30 20:34:12  bwf
 *      @scr 4165 Gift Card Rework
 *
 *      Revision 1.5  2004/03/03 23:15:10  bwf
 *      @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *      Revision 1.4  2004/02/19 04:07:05  blj
 *      @scr 3284 - per code review findings.
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
 *    Rev 1.6   Feb 06 2004 16:43:24   lzhao
 * change display message for different request.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.5   Jan 29 2004 12:00:42   blj
 * added gift card refund issue.
 *
 *    Rev 1.4   Dec 19 2003 15:21:38   lzhao
 * issue code review follow up
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.3   Dec 18 2003 09:41:06   lzhao
 * format
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.2   Dec 16 2003 11:11:50   lzhao
 * issue refactory
 *
 *    Rev 1.1   Dec 12 2003 14:15:28   lzhao
 * format
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.0   Dec 08 2003 09:09:18   lzhao
 * Initial revision.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.giftcard.issue;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
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
 *  This aisle is to get use's input/selection for gift card issue amount
 */
@SuppressWarnings("serial")
public class GiftCardIssueAmountEnteredAisle extends PosLaneActionAdapter
{
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void traverse(BusIfc bus)
    {
        GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
        cargo.setItemQuantity(BigDecimal.ZERO);
        UtilityManagerIfc utility =
            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ParameterManagerIfc pm =
            (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        GiftCardIfc giftCard = cargo.getGiftCard();
        String letterName = bus.getCurrentLetter().getName();
        LetterIfc letter = null;
        // get amount from user's input/selection
        CurrencyIfc amount = null;
        if (cargo.getTransaction().isTrainingMode())
        {
            amount = GiftCardUtilities.getCurrency("10.0");
        }
        else if (letterName.equals(CommonLetterIfc.NEXT))
        {
            if(cargo.getGiftCardAmount() == null)
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
                amount = cargo.getGiftCardAmount();
            }
        }
        else
        {
            // get the amount from a amount button click
            amount =
                GiftCardUtilities.getButtonDenomination(
                    pm,
                    letterName,
                    logger,
                    bus.getServiceName());
        }
        // check amount
        if (GiftCardUtilities
            .isMoreThanMax(pm, amount, logger, bus.getServiceName()))
        {
            DialogBeanModel dModel = GiftCardUtilities.getMoreThanMaxDialogModel(utility);
            if (cargo.getTransaction().getTransactionType() == TransactionIfc.TYPE_RETURN)
            {
                dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.UNDO);
            }
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
                          dModel);
        }
        else if (
            GiftCardUtilities.isLessThanMin(
                pm,
                amount,
                logger,
                bus.getServiceName()))
        {
            DialogBeanModel dModel = GiftCardUtilities.getLessThanMinDialogModel(utility);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
                    dModel);
        }
        else
        {
            giftCard.setReqestedAmount(amount);
            GiftCardBeanModel model = null;
            if (ui.getModel() instanceof GiftCardBeanModel)
            {
                model = (GiftCardBeanModel) ui.getModel();
            }
            else
            {
                model = new GiftCardBeanModel();
            }
            model.setGiftCardAmount(new BigDecimal(amount.toString()));
            model.setGiftCardStatus(StatusCode.Active);
            ui.setModel(POSUIManagerIfc.SELL_GIFT_CARD, model);
            letter = new Letter(CommonLetterIfc.CONTINUE);
        }
        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }
}
