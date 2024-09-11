/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/giftcardinquiry/GiftCardNumberEnteredAisle.java /rgbustores_13.4x_generic_branch/7 2011/09/07 08:33:37 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     09/06/11 - Fixed issues with gift card balance when
 *                         issuing/reloading multiple gift cards and one card
 *                         fails.
 *    cgreene   07/26/11 - moved StatusCode to GiftCardIfc
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    asinton   06/29/11 - Refactored to use EntryMethod and
 *                         AuthorizationMethod enums.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    cgreene   05/20/11 - implemented enums for reponse code and giftcard
 *                         status code
 *    asinton   04/25/11 - Refactored giftcard inquiry for APF
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    npoola    08/12/10 - reverted back the transaction rsnayak_bug-9626720
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    10   360Commerce 1.9         5/2/2008 5:23:58 PM    Christian Greene
 *         31569 Add null-check for parModel and get from other base model if
 *         first one is null
 *    9    360Commerce 1.8         4/15/2008 3:34:07 PM   Alan N. Sinton  CR
 *         30361: Modified so that a swiped card number will be handled.  Code
 *          changes were reviewed by Deepti Sharma.
 *    8    360Commerce 1.7         3/19/2008 11:34:52 PM  Manikandan Chellapan
 *         CR#30959 Modified code to use encrypted card number instead of
 *         clear text number.
 *    7    360Commerce 1.6         1/17/2008 5:24:06 PM   Alan N. Sinton  CR
 *         29954: Refactor of EncipheredCardData to implement interface and be
 *          instantiated using a factory.
 *    6    360Commerce 1.5         1/10/2008 1:05:19 PM   Alan N. Sinton  CR
 *         29761:  Code review changes per Tony Zgarba and Jack Swan.
 *    5    360Commerce 1.4         12/12/2007 6:47:38 PM  Alan N. Sinton  CR
 *         29761: FR 8: Prevent repeated decryption of PAN data.
 *    4    360Commerce 1.3         8/8/2006 1:06:15 PM    Robert Zurga    Merge
 *          from GiftCardNumberEnteredAisle.java, Revision 1.2.1.0
 *    3    360Commerce 1.2         3/31/2005 4:28:17 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:55 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:14 PM  Robert Pearse
 *
 *   Revision 1.4  2004/06/29 19:59:03  lzhao
 *   @scr 5477: add gift card inquiry in training mode.
 *
 *   Revision 1.3  2004/06/23 23:15:46  lzhao
 *   @scr 5353: add check for avoiding NullPointerException.
 *
 *   Revision 1.2  2004/04/14 20:11:01  lzhao
 *   @scr  3872 Redeem, change gift card request type from String to in.
 *
 *   Revision 1.1  2004/04/07 21:10:08  lzhao
 *   @scr 3872: gift card redeem and revise gift card activation
 *
 *   Revision 1.4  2004/03/03 23:15:13  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:22  mcs
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
 *    Rev 1.5   Jan 30 2004 14:14:06   lzhao
 * update based on req. changes.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.4   Dec 08 2003 09:28:40   lzhao
 * remove unused code.
 *
 *    Rev 1.3   Nov 26 2003 09:25:14   lzhao
 * cleanup, use the methods in gift card utility.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.2   Nov 21 2003 15:00:50   lzhao
 * using GiftCardUtilities
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.1   Nov 07 2003 16:50:18   lzhao
 * add new inquiry multple time and print offline features.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.0   Aug 29 2003 15:59:38   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Mar 19 2003 16:38:58   DCobb
 * Code review cleanup.
 * Resolution for POS SCR-1923: POS 6.0 Check Digit For Sale
 * Resolution for POS SCR-1976: POS 6.0 Check Digit For Sale - Gift Card Number
 *
 *    Rev 1.3   Jan 24 2003 15:19:04   DCobb
 * Added check digit support for gift card number.
 * Resolution for POS SCR-1976: POS 6.0 Check Digit For Sale - Gift Card Number
 *
 *    Rev 1.2   Aug 02 2002 14:54:50   jriggins
 * Changed check for "" to whether or not the length == 0
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Jul 31 2002 09:30:24   jriggins
 * Changed call to String.toLowerCase() to use Locale.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:23:04   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:32:56   msg
 * Initial revision.
 *
 *    Rev 1.2   12 Mar 2002 16:52:48   pdd
 * Modified to use the factory.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 *
 *    Rev 1.1   Mar 10 2002 18:00:22   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Sep 21 2001 11:20:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.giftcardinquiry;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This Aisle is traveled when the user enters the gift card number.
 * 
 */
@SuppressWarnings("serial")
public class GiftCardNumberEnteredAisle extends PosLaneActionAdapter
{
    /**
     * class name
     */
    public static final String LANENAME = "GiftCardNumberEnteredAisle";

    /**
     * Gets the gift card number.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc   ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm        = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        POSBaseBeanModel baseModel = (POSBaseBeanModel) ui.getModel(POSUIManagerIfc.GIFT_CARD);
        PromptAndResponseModel parModel = baseModel.getPromptAndResponseModel();
        if (parModel == null)
        {
            baseModel = (POSBaseBeanModel) ui.getModel(POSUIManagerIfc.GIFT_CARD_INQUIRY);
            parModel = baseModel.getPromptAndResponseModel();
        }
        InquiryCargo cargo = (InquiryCargo) bus.getCargo();
        LetterIfc letter = null;

        EncipheredCardDataIfc cardData = null;
        if (parModel != null && parModel.isSwiped())
        {
            cardData = parModel.getMSRModel().getEncipheredCardData();
        }
        else
        {
            try
            {
                if(Util.isEmpty(ui.getInput()) == false)
                {
                    cardData = FoundationObjectFactory.getFactory().createEncipheredCardDataInstance(ui.getInput().getBytes());
                }
                else
                {
                    logger.error("Couldn't get gift card number from UI.");
                }
            }
            catch(EncryptionServiceException ese)
            {
                logger.error("Couldn't decrypt gift card number", ese);
            }
        }
        DialogBeanModel dialogModel = null;
        if ( GiftCardUtilities.isEmpty(baseModel, cardData.getMaskedAcctNumber(), logger, bus.getServiceName()) )
        {
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
        else if (!GiftCardUtilities.isValidBinRange(pm, utility, cardData, logger, bus.getServiceName()))
        {
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
                          GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel());
        }

        else if (!GiftCardUtilities.isValidCheckDigit(utility, cardData, logger, bus.getServiceName()))
        {
            dialogModel = GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel();
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            // no error, create the "continue" letter
            letter = new Letter(CommonLetterIfc.CONTINUE);
        }

        if ( letter != null )
        {
            GiftCardIfc giftCard = DomainGateway.getFactory().getGiftCardInstance();
            giftCard.setCardNumber(cardData.getEncryptedAcctNumber());
            giftCard.setRequestType(GiftCardIfc.GIFT_CARD_INQUIRY);
            if (parModel != null)
            {
                if (parModel.isSwiped())
                {
                    giftCard.setEntryMethod(EntryMethod.Swipe);
                }
                else if (parModel.isScanned())
                {
                    giftCard.setEntryMethod(EntryMethod.Automatic);
                }
                else
                {
                    giftCard.setEntryMethod(EntryMethod.Manual);
                }
            }

            cargo.setGiftCard(giftCard);

            if ( cargo.getRegister().getWorkstation().isTrainingMode() )
            {
                giftCard.setReqestedAmount(DomainGateway.getBaseCurrencyInstance("10"));
                giftCard.setCurrentBalance(DomainGateway.getBaseCurrencyInstance("10"));
                giftCard.setStatus(StatusCode.Active);
                letter = new Letter(CommonLetterIfc.TRAINING);
            }

            if (logger.isInfoEnabled()) logger.info( "GiftCardNumberEnteredAisle.traverse(), giftCard = " + giftCard + "");

            bus.mail(letter, BusIfc.CURRENT);
        }
    }
}
