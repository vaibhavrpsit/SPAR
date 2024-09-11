/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcard/issue/IssueGiftCardStartSite.java /rgbustores_13.4x_generic_branch/5 2011/09/07 08:33:37 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     09/06/11 - Fixed issues with gift card balance when
 *                         issuing/reloading multiple gift cards and one card
 *                         fails.
 *    cgreene   07/26/11 - moved StatusCode to GiftCardifc
 *    asinton   05/31/11 - Refactored Gift Card Redeem and Tender for APF
 *    cgreene   05/27/11 - move auth response objects into domain
 *    asinton   04/26/11 - Refactor gift card for APF
 *    sgu       06/08/10 - fix tab
 *    sgu       06/08/10 - add item # & desc to the screen prompt. fix unknow
 *                         item screen to disable price and quantity for
 *                         external item
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         4/25/2007 8:52:26 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    6    360Commerce 1.5         5/12/2006 5:25:29 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    5    360Commerce 1.4         1/25/2006 4:11:04 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         1/22/2006 11:45:06 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:29 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:21 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:35 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     10/31/2005 11:55:30    Deepanshu       CR
 *         6092: Removed setting of Sales Associate as Cashier
 *    3    360Commerce1.2         3/31/2005 15:28:29     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:21     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:35     Robert Pearse
 *
 *   Revision 1.8  2004/06/25 21:38:18  lzhao
 *   @scr 5816: should sale item screen when undo
 *
 *   Revision 1.7  2004/06/21 22:19:26  lzhao
 *   @scr 5774, 5447: gift card return/reload.
 *
 *   Revision 1.6  2004/04/14 20:10:43  lzhao
 *   @scr  3872 Redeem, change gift card request type from String to in.
 *
 *   Revision 1.5  2004/04/07 21:10:09  lzhao
 *   @scr 3872: gift card redeem and revise gift card activation
 *
 *   Revision 1.4  2004/03/30 16:31:06  pkillick
 *   @scr 3947 -Allowed for Issue Entry Type to be set to BY_DENOMINATION. (Line 134)
 *
 *   Revision 1.3  2004/02/12 16:50:23  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:11  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.6   Feb 06 2004 16:43:24   lzhao
 * change display message for different request.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.5   Jan 30 2004 14:14:08   lzhao
 * update based on req. changes.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.4   Jan 09 2004 12:54:26   lzhao
 * set transaction back, remove comments, add date
 * Resolution for 3666: Eltronic Journal for Gift Card Issue  and Reload not Correct
 *
 *    Rev 1.3   Dec 19 2003 15:21:42   lzhao
 * issue code review follow up
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.2   Dec 18 2003 09:41:08   lzhao
 * format
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.1   Dec 16 2003 11:11:54   lzhao
 * issue refactory
 *
 *    Rev 1.0   Dec 12 2003 14:24:44   lzhao
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.giftcard.issue;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;

/**
 *  This class determines which site is the next one.
 */
@SuppressWarnings("serial")
public class IssueGiftCardStartSite extends PosSiteActionAdapter
{
    /** the letter for get gift card amount */
    public static final String GET_GIFT_CARD_AMOUNT_LETTER = "GetCardAmount";

    /** the letter for get gift card number */
    public static final String GET_GIFT_CARD_NUMBER_LETTER = "GetCardNumber";

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ParameterManagerIfc pm =
            (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        UtilityManagerIfc utility =
            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
        cargo.setItemQuantity(BigDecimal.ZERO);
        GiftCardIfc giftCard = DomainGateway.getFactory().getGiftCardInstance();
        giftCard.setOpenAmount(true);
        giftCard.setDateSold( new EYSDate() );
        giftCard.setRequestType(GiftCardIfc.GIFT_CARD_ISSUE);
        cargo.setGiftCard(giftCard);

        //create the transaction if it doesn't exist.
        SaleReturnTransactionIfc transaction = cargo.getTransaction();
        if (transaction == null)
        {
            cargo.initializeTransaction(bus);
        }
        GiftCardBeanModel model = new GiftCardBeanModel();
        model.setGiftCardStatus(StatusCode.Active);

        GiftCardPLUItemIfc giftCardItem = (GiftCardPLUItemIfc) cargo.getPLUItem();

        // if it is called from sale screen, gift card item should not
        // be null. The tour will directly go to get gift card number site if the item
        // is not open amount. Otherwise it will go to get gift card amount site.
        // If it is called from gift card option screen gift card item should be null.
        // The tour will go to get gift card amount site
        if (giftCardItem != null)
        {
            giftCard.setIssueEntryType(GiftCardIfc.BY_ITEM_ID);
            cargo.setDisplayedGetAmountScreen(false);
            ItemClassificationIfc itemClassification = giftCardItem.getItemClassification();
            if ( itemClassification != null )
            {
                CurrencyIfc amount = null;
                if (cargo.getGiftCardAmount() != null)	// if there is already a price, use that price
                {
                    amount = cargo.getGiftCardAmount();
                }
                else if ( !itemClassification.getPriceEntryRequired() )
                {
                    amount = giftCardItem.getPrice();
                }
                if (amount != null)
                {
            		if (GiftCardUtilities
            				.isMoreThanMax(pm, amount, logger, bus.getServiceName()))
            		{
            			DialogBeanModel dModel = GiftCardUtilities.getMoreThanMaxDialogModel(utility);
            			dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.UNDO);
            			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
            					dModel);
            			return;
            		}
            		else if (
            				GiftCardUtilities.isLessThanMin(
            						pm,
            						amount,
            						logger,
            						bus.getServiceName()))
            		{
            			DialogBeanModel dModel = GiftCardUtilities.getLessThanMinDialogModel(utility);
            			dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.UNDO);
            			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
            					dModel);
            			return;
            		}
            		else
            		{
                        giftCard.setReqestedAmount(amount);
            			giftCard.setOpenAmount(false);
            			model.setGiftCardAmount(new BigDecimal(amount.toString()));
            		}
            	}
            }
            else
            {
                logger.warn( "item classification is null.");
            }
        }
        else
        {
            giftCard.setIssueEntryType(GiftCardIfc.BY_DENOMINATION);
        }

        ui.setModel(POSUIManagerIfc.SELL_GIFT_CARD, model);
        if ( giftCard.getOpenAmount() && !cargo.isFundingSelectionOnly())
        {
            bus.mail(GET_GIFT_CARD_AMOUNT_LETTER, BusIfc.CURRENT);
        }
        else
        {
            bus.mail(GET_GIFT_CARD_NUMBER_LETTER, BusIfc.CURRENT);
        }
    }
}
