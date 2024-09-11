/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcard/issue/GiftCardIssueCardNumEnteredAisle.java /main/19 2012/09/12 11:57:18 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    blarse 08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreen 03/30/12 - get journalmanager from bus
 *    asinto 03/30/12 - implemented giftcard issue
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
      13   360Commerce 1.12        4/7/2008 5:28:45 PM    Alan N. Sinton  CR
           30361: Gift Card Number retrieved from PromptAndResponseModel if
           swiped, from UI otherwise.  Code changes reviewed by Christian
           Greene.
      12   360Commerce 1.11        1/17/2008 5:24:06 PM   Alan N. Sinton  CR
           29954: Refactor of EncipheredCardData to implement interface and be
            instantiated using a factory.
      11   360Commerce 1.10        1/10/2008 1:05:19 PM   Alan N. Sinton  CR
           29761:  Code review changes per Tony Zgarba and Jack Swan.
      10   360Commerce 1.9         12/16/2007 5:57:17 PM  Alan N. Sinton  CR
           29598: Fixes for various areas broke from PABP changes.
      9    360Commerce 1.8         12/14/2007 8:59:59 AM  Alan N. Sinton  CR
           29761: Removed non-PABP compliant methods and modified card RuleIfc
            to take an instance of EncipheredCardData.
      8    360Commerce 1.7         12/12/2007 6:47:38 PM  Alan N. Sinton  CR
           29761: FR 8: Prevent repeated decryption of PAN data.
      7    360Commerce 1.6         5/21/2007 9:39:40 PM   Mathews Kochummen use
            locale format
      6    360Commerce 1.5         4/25/2007 8:52:26 AM   Anda D. Cadar   I18N
           merge

      5    360Commerce 1.4         5/12/2006 5:25:28 PM   Charles D. Baker
           Merging with v1_0_0_53 of Returns Managament
      4    360Commerce 1.3         3/29/2006 12:38:16 AM  Nageshwar Mishra CR
           16238: Updated traverse method for the Sold Date.
      3    360Commerce 1.2         3/31/2005 4:28:16 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:21:54 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:11:13 PM  Robert Pearse
     $
     Revision 1.8  2004/06/23 23:15:46  lzhao
     @scr 5353: add check for avoiding NullPointerException.

     Revision 1.7  2004/05/27 19:31:33  jdeleau
     @scr 2775 Remove unused imports as a result of tax engine rework

     Revision 1.6  2004/05/27 17:12:48  mkp1
     @scr 2775 Checking in first revision of new tax engine.

     Revision 1.5  2004/04/14 20:10:43  lzhao
     @scr  3872 Redeem, change gift card request type from String to in.

     Revision 1.4  2004/03/03 23:15:10  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.3  2004/02/12 16:50:23  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:11  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.6   Jan 30 2004 14:14:08   lzhao
 * update based on req. changes.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.5   Dec 19 2003 16:52:36   lzhao
 * set pluitem to cargo
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.4   Dec 19 2003 15:21:40   lzhao
 * issue code review follow up
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.3   Dec 18 2003 09:41:06   lzhao
 * format
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.2   Dec 16 2003 11:11:52   lzhao
 * issue refactory
 *
 *    Rev 1.1   Dec 12 2003 14:14:54   lzhao
 * remove validAmount
 *
 *    Rev 1.0   Dec 08 2003 09:09:18   lzhao
 * Initial revision.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.giftcard.issue;

import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
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
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
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
public class GiftCardIssueCardNumEnteredAisle extends PosLaneActionAdapter implements GiftCardConstantsIfc
{
    /**
     * class name
     */
    public static final String LANENAME = "GiftCardIssueCardNumEnteredAisle";

    /**
     * Get the data entered on the Gift Card screen or from MSR
     * Validate and save the gift card number. Send the 'Continue' letter
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
         // read the data from the UI
        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        LocaleRequestor localeRequestor = utility.getRequestLocales();

        ParameterManagerIfc pm =
            (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
        GiftCardBeanModel model =
            (GiftCardBeanModel) ui.getModel(POSUIManagerIfc.GET_CARD_NUM_FOR_GIFT_CARD);
        PromptAndResponseModel parModel = model.getPromptAndResponseModel();
        boolean noError = true;
        GiftCardIfc giftCard = cargo.getGiftCard();
        EncipheredCardDataIfc cardData = null;
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

        LetterIfc letter = null;
        if (cardData != null && GiftCardUtilities.isEmpty(model, cardData.getMaskedAcctNumber(), logger, bus.getServiceName()))
        {
            noError = false;
            if ( (parModel != null) && parModel.isSwiped())
            {
                ui.showScreen(
                    POSUIManagerIfc.DIALOG_TEMPLATE,
                    GiftCardUtilities.createBadMSRReadDialogModel(utility));
            }
            else
            {
                ui.showScreen(
                    POSUIManagerIfc.DIALOG_TEMPLATE,
                    GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel());
            }
        }
        if (cardData == null ||
        		(noError
        				&& !GiftCardUtilities.isValidBinRange(
			                pm,
			                utility,
			                cardData,
			                logger,
			                bus.getServiceName())))
        {
            noError = false;
            ui.showScreen(
                POSUIManagerIfc.DIALOG_TEMPLATE,
                GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel());
        }
        if (noError && !cargo.getRegister().getWorkstation().isTrainingMode())
        {
            if (!GiftCardUtilities
                .isValidCheckDigit(
                    utility,
                    cardData,
                    logger,
                    bus.getServiceName()))
            {
                noError = false;
                ui.showScreen(
                    POSUIManagerIfc.DIALOG_TEMPLATE,
                    GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel());
            }
        }
        if (noError)
        {
            GiftCardPLUItemIfc pluItem =
                (GiftCardPLUItemIfc) cargo.getPLUItem();
            if (pluItem == null)
            {
                String itemID = "0";
                try
                {
                    itemID = pm.getStringValue(DEFAULT_GIFT_CARD_ITEM_ID);
                }
                catch (ParameterException e)
                {
                    itemID = DEFAULT_ITEM_ID;
                    if (logger.isInfoEnabled()) logger.info(
                        "GiftCardIssueCardNumEnteredAisle.traverse(), cannot find default giftCard item.");
                }
                pluItem =
                    GiftCardUtilities.getPluItem(ui,
                        cargo,
                        itemID,
                        logger,
                        bus.getServiceName(),
                        localeRequestor);
            }
            if (pluItem != null && cardData != null)
            {
                CurrencyIfc amount = giftCard.getReqestedAmount();
                giftCard.setEncipheredCardData(cardData);
                pluItem.setGiftCard(giftCard);
                pluItem.setPrice(amount);
                cargo.setItemQuantity(BigDecimalConstants.ONE_AMOUNT);
                cargo.setPLUItem(pluItem);
                String soldDate = "N/A";
                if (giftCard.getDateSold() != null)
                {
                	Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
                    soldDate = giftCard.getDateSold().toFormattedString(locale);
                }
                JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
				StringBuilder journalString = new StringBuilder(giftCard
						.toJournalString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
				Object[] dataArgs = new Object[2];
				dataArgs[0] = soldDate;
				journalString.append(Util.EOL
						+ I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
								JournalConstantsIfc.DATE_SOLD_LABEL, dataArgs));
				journalString.append(Util.EOL
						+ I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
								JournalConstantsIfc.ISSUED_LABEL, null));
                jmi.journal(
                    cargo.getTransaction().getCashier().getLoginID(),
                    cargo.getTransaction().getTransactionID(),
                    journalString.toString());
                letter = new Letter(CommonLetterIfc.CONTINUE);
            }
        }
        // Proceed
        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }
}
