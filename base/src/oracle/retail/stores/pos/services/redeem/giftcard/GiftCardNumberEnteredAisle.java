/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/giftcard/GiftCardNumberEnteredAisle.java /main/13 2013/03/07 12:26:25 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     03/07/13 - Forward Port: ACI requires card entry method on
 *                         redeem.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 9    360Commerce 1.8         4/15/2008 3:34:07 PM   Alan N. Sinton  CR
 *      30361: Modified so that a swiped card number will be handled.  Code
 *      changes were reviewed by Deepti Sharma. 
 * 8    360Commerce 1.7         3/27/2008 12:46:40 AM  Manikandan Chellapan
 *      Revoked the changes made for 30967
 * 7    360Commerce 1.6         3/27/2008 12:25:49 AM  Manikandan Chellapan
 *      CR#30967 Fixed Gift Card Inquiry receipt format errors. Code reviewed
 *      by anil kandru.
 * 6    360Commerce 1.5         1/17/2008 5:24:06 PM   Alan N. Sinton  CR
 *      29954: Refactor of EncipheredCardData to implement interface and be
 *      instantiated using a factory.
 * 5    360Commerce 1.4         1/10/2008 1:05:19 PM   Alan N. Sinton  CR
 *      29761:  Code review changes per Tony Zgarba and Jack Swan.
 * 4    360Commerce 1.3         12/12/2007 6:47:38 PM  Alan N. Sinton  CR
 *      29761: FR 8: Prevent repeated decryption of PAN data.
 * 3    360Commerce 1.2         3/31/2005 4:28:17 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:21:55 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:11:14 PM  Robert Pearse   
 *
 *Revision 1.9  2004/07/29 18:33:08  blj
 *@scr 5831 - added a training mode for bin ranges.
 *
 *Revision 1.8  2004/06/23 23:16:19  lzhao
 *@scr 5353: add check for avoiding NullPointerException
 *
 *Revision 1.7  2004/04/14 20:10:07  lzhao
 *@scr  3872 Redeem, change gift card request type from String to in.
 *
 *Revision 1.6  2004/04/13 19:02:22  lzhao
 *@scr 3872: gift card redeem.
 *
 *Revision 1.5  2004/04/08 21:12:41  tfritz
 *@scr 3884 - Do not do check digit validation when in training mode.
 *
 *Revision 1.4  2004/04/08 20:33:02  cdb
 *@scr 4206 Cleaned up class headers for logs and revisions.
 *
 *Revision 1.3  2004/04/07 21:10:08  lzhao
 *@scr 3872: gift card redeem and revise gift card activation
 *
 *Revision 1.2  2004/03/25 23:01:23  lzhao
 *@scr #3872 Redeem Gift Card
 *
 *Revision 1.1  2004/03/22 23:59:08  lzhao
 *@scr 3872 - add gift card redeem (initial)
 *
 *Revision: 9$
 *Mar 19, 2004 lzhao
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem.giftcard;

import oracle.retail.stores.keystoreencryption.EncryptionServiceException;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.services.redeem.RedeemCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
 This aisle is traveled when the user entered the gift card number.
 @version $Revision: /main/13 $
 **/
//--------------------------------------------------------------------------
public class GiftCardNumberEnteredAisle extends PosLaneActionAdapter
{
    /**
     * class name
     **/
    public static final String LANENAME = "GiftCardNumberEnteredAisle";
    /**
     * inquiry balance letter constant 
     */
    public static final String INQUIRY_BALANCE = "InquiryBalance";
    /**
     * revision number
     **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    //----------------------------------------------------------------------
    /**
     Gets the gift card number, check bin range and mod 10.
     If there is no error, create a gift card instance and assign it to the cargo.
     If there is an error, display error dialog.
     <P>
     @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc   ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm    = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        POSBaseBeanModel baseModel = (POSBaseBeanModel) ui.getModel(POSUIManagerIfc.GIFT_CARD_REDEEM_INQUIRY);
        PromptAndResponseModel parModel = baseModel.getPromptAndResponseModel();
        RedeemCargo cargo = (RedeemCargo) bus.getCargo();
        LetterIfc letter = null;

        EncipheredCardDataIfc cardData = null;
        if(parModel.isSwiped())
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
        // if cardData is null, or not training mode and not valid bin range
        else if ( cardData == null ||
        		(!cargo.getRegister().getWorkstation().isTrainingMode() &&
                  !GiftCardUtilities.isValidBinRange(pm, utility, cardData, logger, bus.getServiceName())) )
        {
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, 
                    GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel()); 
        }
        
        else if ( !cargo.getRegister().getWorkstation().isTrainingMode() && 
                  !GiftCardUtilities.isValidCheckDigit(utility, cardData, logger, bus.getServiceName()) )
        {
            dialogModel = GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel();
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            // no error, inquiry gift card balance
            letter = new Letter(INQUIRY_BALANCE);
        }

        if ( letter != null )
        {
            GiftCardIfc giftCard = DomainGateway.getFactory().getGiftCardInstance();
            giftCard.setEncipheredCardData(cardData);
            giftCard.setRequestType(GiftCardIfc.GIFT_CARD_REDEEM_INQUIRY);
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

            if (logger.isInfoEnabled()) 
            {
                logger.info( "GiftCardNumberEnteredAisle.traverse(), giftCard = " + giftCard + "");
            }

            bus.mail(letter, BusIfc.CURRENT);
        }
    }
}
