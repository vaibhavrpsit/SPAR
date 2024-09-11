/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header:$
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/29/14 - Added to valid gift card number for bin range
 *                         and check digit.
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.tender.giftcard;

import java.util.HashMap;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardConstantsIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * Performs Gift Card number validations.
 * @since 14.1
 */
@SuppressWarnings("serial")
public class GetAndValidateGiftCardNumberAisle extends PosLaneActionAdapter
{
    private static final String ACTIVATE = "Activate";
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    @SuppressWarnings("deprecation")
    public void traverse(BusIfc bus)
    {
        
        // Get information from UI
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = (POSBaseBeanModel) ui.getModel();
        PromptAndResponseModel parModel = model.getPromptAndResponseModel();
        HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
        EncipheredCardDataIfc cardData = null;
        
        if (parModel != null) 
        {
        
            if (parModel.isSwiped())
            {
                // Put the model in tenderAttributes
                cardData = parModel.getMSRModel().getEncipheredCardData();
                tenderAttributes.put(TenderConstants.MSR_MODEL, parModel.getMSRModel());
                tenderAttributes.put(TenderConstants.NUMBER, parModel.getMSRModel().getEncipheredCardData().getTruncatedAcctNumber());
                tenderAttributes.put(TenderConstants.ENCIPHERED_CARD_DATA, cardData);
                tenderAttributes.put(TenderConstants.ENTRY_METHOD, EntryMethod.Swipe);
            }
            else if (parModel.isScanned())
            {
                // encrypt the card number
                try
                {
                    cardData = FoundationObjectFactory.getFactory().
                            createEncipheredCardDataInstance(ui.getInput().getBytes());
                    tenderAttributes.put(TenderConstants.NUMBER, cardData.getTruncatedAcctNumber());
                    tenderAttributes.put(TenderConstants.ENCIPHERED_CARD_DATA, cardData);
                    tenderAttributes.put(TenderConstants.ENTRY_METHOD, EntryMethod.Automatic);
                }
                catch(EncryptionServiceException e)
                {
                    String message = "unable to decrypt the text";
                    throw new RuntimeException(message, e);
                }
            }
            else if (cargo.getPreTenderMSRModel() == null)
            {
                // if manually entered, we only have the card number.
                // encrypt the card number
                try
                {
                    cardData = FoundationObjectFactory.getFactory().
                            createEncipheredCardDataInstance(ui.getInput().getBytes());
                    tenderAttributes.put(TenderConstants.NUMBER, cardData.getTruncatedAcctNumber());
                    tenderAttributes.put(TenderConstants.ENCIPHERED_CARD_DATA, cardData);
                    tenderAttributes.put(TenderConstants.ENTRY_METHOD, EntryMethod.Manual);
                    // remove MSR (may have been previously swiped)
                    tenderAttributes.remove(TenderConstants.MSR_MODEL);
                }
                catch(EncryptionServiceException e)
                {
                    String message = "unable to decrypt the text";
                    throw new RuntimeException(message, e);
                }
            }
            else
            {  
                MSRModel msr = (MSRModel)cargo.getTenderAttributes().get(TenderConstants.MSR_MODEL);
                cardData = msr.getEncipheredCardData();
                tenderAttributes.put(TenderConstants.NUMBER, msr.getEncipheredCardData().getTruncatedAcctNumber());
                tenderAttributes.put(TenderConstants.ENCIPHERED_CARD_DATA, cardData);
                
            }
            GiftCardIfc giftCard = DomainGateway.getFactory().getGiftCardInstance();
            giftCard.setEncipheredCardData((EncipheredCardDataIfc)tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA));

            giftCard.setRequestType(GiftCardIfc.GIFT_CARD_INQUIRY);
            cargo.setGiftCard(giftCard);

            // This was added to force processing past the off-line condition when performing
            // an inquiry during tendering.  The current tender amount is being set on the available
            giftCard.setInquireAmountForTender(true);
            String tenderAmount = tenderAttributes.get(TenderConstants.AMOUNT).toString();
            giftCard.setBalanceForInquiryFailure(DomainGateway.getBaseCurrencyInstance(tenderAmount));
        }
        else if(!Util.isBlank(ui.getInput()))
        {
            // if manually entered, we only have the card number.
            // encrypt the card number
            try
            {
                cardData = FoundationObjectFactory.getFactory().
                        createEncipheredCardDataInstance(ui.getInput().getBytes());
                tenderAttributes.put(TenderConstants.NUMBER, cardData.getTruncatedAcctNumber());
                tenderAttributes.put(TenderConstants.ENCIPHERED_CARD_DATA, cardData);
                tenderAttributes.put(TenderConstants.ENTRY_METHOD, EntryMethod.Manual);
                // remove MSR (may have been previously swiped)
                tenderAttributes.remove(TenderConstants.MSR_MODEL);
            }
            catch(EncryptionServiceException e)
            {
                String message = "unable to decrypt the text";
                throw new RuntimeException(message, e);
            }
            
            GiftCardIfc giftCard = DomainGateway.getFactory().getGiftCardInstance();
            giftCard.setEncipheredCardData((EncipheredCardDataIfc)tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA));

            giftCard.setRequestType(GiftCardIfc.GIFT_CARD_INQUIRY);
            cargo.setGiftCard(giftCard);

            // This was added to force processing past the off-line condition when performing
            // an inquiry during tendering.  The current tender amount is being set on the available
            giftCard.setInquireAmountForTender(true);
            String tenderAmount = tenderAttributes.get(TenderConstants.AMOUNT).toString();
            giftCard.setBalanceForInquiryFailure(DomainGateway.getBaseCurrencyInstance(tenderAmount));
        
        }
        
        // set pre tender msr model to null
        // we dont want to come back into credit/debit automatically if we have problems.
        cargo.setPreTenderMSRModel(null);            
        
        if (validGiftCardNumber(cargo, bus, cardData))
        {
            bus.mail(new Letter(ACTIVATE), BusIfc.CURRENT);
        }
    }

    protected boolean validGiftCardNumber(TenderCargo cargo, BusIfc bus,
            EncipheredCardDataIfc cardData)
    {
        POSUIManagerIfc     ui        = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc   utility   = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm        = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        boolean valid = true;

        if (cardData != null && !GiftCardUtilities.isValidBinRange(pm, utility, cardData, logger, bus.getServiceName()) )
        {
            valid = false;
        }

        if (valid && cardData != null && !cargo.getRegister().getWorkstation().isTrainingMode() )
        {
            if (!GiftCardUtilities.isValidCheckDigit(utility, cardData, logger, bus.getServiceName()) )
            {
                valid = false;
            }
        }
        
        if (!valid)
        {
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
                    createInvalidGiftCardNumErrorDialogModel());
        }
        
        return valid;
    }

    /**
     * Build the dialog screen; mails a failure letter when the user presses the OK
     * button.
     * @return
     */
    protected DialogBeanModel createInvalidGiftCardNumErrorDialogModel()
    {
        DialogBeanModel dialogModel = new DialogBeanModel();

        dialogModel.setResourceID(GiftCardConstantsIfc.INVALID_GIFT_CARD_NUMBER_ERROR);
        dialogModel.setType(DialogScreensIfc.ERROR);

        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
        return dialogModel;
    }
}
