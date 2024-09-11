/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcard/reload/GiftCardReloadCardNumEnteredAisle.java /main/20 2012/09/12 11:57:18 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    blarse 08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreen 03/09/12 - add support for journalling queues by current register
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    jswan  09/06/11 - Fixed issues with gift card balance when
 *                      issuing/reloading multiple gift cards and one card
 *                      fails.
 *    asinto 09/01/11 - capture the entry method for gift card issue and reload
 *    asinto 04/26/11 - Refactor gift card for APF
 *    asinto 12/20/10 - XbranchMerge asinton_bug-10407292 from
 *                      rgbustores_13.3x_generic_branch
 *    asinto 12/17/10 - deprecated hashed account ID.
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    sswamy 11/05/08 - Checkin after merges
 *    sswamy 11/04/08 - Modified to use toJournalString(Locale)
 *    deghos 10/29/08 - EJI18n_changes_ExtendyourStore
 *    abonda 10/17/08 - I18Ning manufacturer name
 *    abonda 10/15/08 - I18Ning manufacturer name
 *
 *
 * ===========================================================================

     $Log:
      9    360Commerce 1.8         4/14/2008 1:41:58 AM   Manas Sahu      The
           following condition checks if postvoid is of last reload then it
           should be allowed. To check that get the ItemTotal price of the
           PLUItem add that to the initial balance and compare that to current
            balance. In case of last reload the initial balance will be the
           amount before the reload, the PLUItem total price will be the last
           reload amount and the current balance will be initial balance
           before reload + the reload amount. e.g. Initial balance 100, first
           reload 25 and second reload of 25 again. Now the last reload
           postvoid should be allowed. In that case when you try to postvoid
           the first reload this will return true as 100 + 25 != 150
           (current). But if you try to postvoid the second reload then 125 +
           25 = 150 and hence this condition will return false.
      8    360Commerce 1.7         1/17/2008 5:24:06 PM   Alan N. Sinton  CR
           29954: Refactor of EncipheredCardData to implement interface and be
            instantiated using a factory.
      7    360Commerce 1.6         12/26/2007 12:28:14 PM Anil Bondalapati
           removed log statements related to security informaton.
      6    360Commerce 1.5         12/14/2007 8:59:59 AM  Alan N. Sinton  CR
           29761: Removed non-PABP compliant methods and modified card RuleIfc
            to take an instance of EncipheredCardData.
      5    360Commerce 1.4         12/12/2007 6:47:38 PM  Alan N. Sinton  CR
           29761: FR 8: Prevent repeated decryption of PAN data.
      4    360Commerce 1.3         4/25/2007 8:52:26 AM   Anda D. Cadar   I18N
           merge

      3    360Commerce 1.2         3/31/2005 4:28:17 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:21:55 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:11:14 PM  Robert Pearse
     $
     Revision 1.15  2004/08/19 21:55:41  blj
     @scr 6855 - Removed old code and fixed some flow issues with gift card credit.

     Revision 1.14  2004/06/23 23:15:46  lzhao
     @scr 5353: add check for avoiding NullPointerException.

     Revision 1.13  2004/06/21 22:19:26  lzhao
     @scr 5774, 5447: gift card return/reload.

     Revision 1.12  2004/05/27 19:31:33  jdeleau
     @scr 2775 Remove unused imports as a result of tax engine rework

     Revision 1.11  2004/05/27 17:12:48  mkp1
     @scr 2775 Checking in first revision of new tax engine.

     Revision 1.10  2004/04/14 20:10:51  lzhao
     @scr  3872 Redeem, change gift card request type from String to in.

     Revision 1.9  2004/04/07 21:10:09  lzhao
     @scr 3872: gift card redeem and revise gift card activation

     Revision 1.8  2004/03/16 18:30:46  cdb
     @scr 0 Removed tabs from all java source code.

     Revision 1.7  2004/03/03 23:15:12  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.6  2004/02/16 17:12:09  blj
     @scr 3824 - removed comments

     Revision 1.5  2004/02/16 16:58:37  blj
     @scr 3824 added new method to handle request type.

     Revision 1.4  2004/02/12 17:27:08  blj
     @scr 3824 - check for request type

     Revision 1.2  2004/02/11 21:51:11  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.9   Jan 30 2004 14:14:08   lzhao
 * update based on req. changes.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.8   Dec 19 2003 16:52:38   lzhao
 * set pluitem to cargo
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.7   Dec 16 2003 11:15:54   lzhao
 * code review follow up
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.6   Dec 12 2003 14:17:10   lzhao
 * remove validAmount()
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.5   Dec 08 2003 09:31:32   lzhao
 * remove unused code.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.4   Nov 26 2003 12:45:14   lzhao
 * remove ui as parameter in isValid() in gift card utility
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.3   Nov 26 2003 09:26:58   lzhao
 * cleanup, use the mehods in gift card utilties.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.2   Nov 21 2003 15:05:22   lzhao
 * refactory gift card using sale, completesale, giftoptions services and giftcardutilites.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.1   Oct 30 2003 13:51:00   lzhao
 * fix the problem when gift card not find in pos db.
 *
 *    Rev 1.0   Oct 30 2003 10:57:46   lzhao
 * Initial revision.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.giftcard.reload;


import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.services.giftcard.GiftCardConstantsIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * Validates the gift card number entered or scanned and adds
 * it to the cargo.
 */
@SuppressWarnings("serial")
public class GiftCardReloadCardNumEnteredAisle extends PosLaneActionAdapter implements GiftCardConstantsIfc
{
    /**
     * class name
     */
    public static final String LANENAME = "GiftCardReloadCardNumEnteredAisle";

    /**
     * Get the data entered on the Gift Card screen or from MSR
     * Validate and save the gift card number.
     * Send the 'Continue' letter
     * @param bus the bus traversing this lane
     */
    public void traverse(BusIfc bus)
    {
        // read the data from the UI
        POSUIManagerIfc     ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ParameterManagerIfc pm      = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        GiftCardCargo         cargo   = (GiftCardCargo)bus.getCargo();

        GiftCardIfc         giftCard   = cargo.getGiftCard();
        EncipheredCardDataIfc  cardData   = null;
        GiftCardBeanModel model = (GiftCardBeanModel) ui.getModel(POSUIManagerIfc.GET_CARD_NUM_FOR_GIFT_CARD);
        PromptAndResponseModel parModel = model.getPromptAndResponseModel();
        if(parModel.isSwiped())
        {
            cardData = parModel.getMSRModel().getEncipheredCardData();
            giftCard.setEntryMethod(EntryMethod.Swipe);
        }
        else
        {
            if(parModel.isScanned())
            {
                giftCard.setEntryMethod(EntryMethod.Scan);
            }
            else
            {
                giftCard.setEntryMethod(EntryMethod.Manual);
            }
            byte[] cardNumber = parModel.getResponseBytes();
            try
            {
                cardData = FoundationObjectFactory.getFactory().createEncipheredCardDataInstance(cardNumber);
            }
            catch(EncryptionServiceException ese)
            {
                logger.error("Couldn't encrypt gift card number", ese);
            }
            finally
            {
                Util.flushByteArray(cardNumber);
            }
        }

        if ( noError(bus, cardData) )
        {
            String itemID="0";
            try
            {
                itemID = pm.getStringValue(DEFAULT_GIFT_CARD_ITEM_ID);
            }
            catch (ParameterException e)
            {
                itemID = DEFAULT_ITEM_ID;
            }

            GiftCardPLUItemIfc pluItem = GiftCardUtilities.getPluItem(ui, cargo, itemID, logger, bus.getServiceName(), utility.getRequestLocales());
            if (cardData != null && pluItem != null)
            {
                CurrencyIfc amount = giftCard.getReqestedAmount();
                giftCard.setCardNumber(cardData.getEncryptedAcctNumber());
                giftCard.setRequestType(GiftCardIfc.GIFT_CARD_RELOAD);
                pluItem.setGiftCard(giftCard);
                pluItem.setPrice(amount);
                cargo.setItemQuantity(BigDecimalConstants.ONE_AMOUNT);
                cargo.setPLUItem(pluItem);

                JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
				jmi.journal(cargo.getTransaction().getCashier().getLoginID(),
						cargo.getTransaction().getTransactionID(), giftCard
								.toJournalString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL))
								+ Util.EOL
								+ I18NHelper.getString(
										I18NConstantsIfc.EJOURNAL_TYPE,
										JournalConstantsIfc.RELOADED_LABEL,
										null));
				Letter letter = new Letter(CommonLetterIfc.CONTINUE);
				bus.mail(letter, BusIfc.CURRENT);
            }
        }
    }

    protected boolean noError(BusIfc bus, EncipheredCardDataIfc cardData)
    {
        POSUIManagerIfc     ui        = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc   utility   = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm        = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        GiftCardCargo       cargo     = (GiftCardCargo)bus.getCargo();
        GiftCardBeanModel   model     = (GiftCardBeanModel) ui.getModel(POSUIManagerIfc.SELL_GIFT_CARD);

        boolean noError = true;

        if ( GiftCardUtilities.isEmpty(model, cardData.getMaskedAcctNumber(), logger, bus.getServiceName()) )
        {
            noError = false;
            PromptAndResponseModel parModel = model.getPromptAndResponseModel();
            if ( (parModel!=null) && parModel.isSwiped() )
            {
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
                              GiftCardUtilities.createBadMSRReadDialogModel(utility));

            }
            else
            {
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
                              GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel());
            }
        }

        if ( cardData != null && noError && !GiftCardUtilities.isValidBinRange(pm, utility, cardData, logger, bus.getServiceName()) )
        {
            noError = false;
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
                          GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel());
        }

        if ( noError && !cargo.getRegister().getWorkstation().isTrainingMode() )
        {
            if (!GiftCardUtilities.isValidCheckDigit(utility, cardData, logger, bus.getServiceName()) )
            {
               noError = false;
               ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
                             GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel());
            }
        }
        return noError;
    }
}
