/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/activation/ActivationOfflineAisle.java /main/3 2014/04/18 16:19:11 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   03/21/14 - interrogate request sub type to assign correct
 *                         dialog, activation versus deactivation.
 *    mkutiana  02/06/12 - Using existing parameter
 *                         GiftCardActivationRefPhoneNumber
 *    jswan     10/14/11 - Parameterized the request sub type for all error
 *                         dialog messages in the activation service.
 *    asinton   09/21/11 - Show offline dialog when RequestSubType is Redeem.
 *    jswan     09/16/11 - Fixed display of Gift Card Activation Referral phone
 *                         number.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    asinton   05/04/11 - New activation service for APF.
 *    asinton   05/03/11 - New activation service for APF.
 *    asinton   05/03/11 - New activation service for APF.
 *    asinton   03/22/11 - new tender authorization service
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.activation;

import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc.RequestSubType;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * This aisle shows the authorization offline dialog.
 * 
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class ActivationOfflineAisle extends ActivationErrorAisle
{
    /**
     * The logger to which log messages will be sent
     */
    protected static final Logger logger = Logger.getLogger(ActivationOfflineAisle.class);

    /** constant for activation referral dialog name */
    public static final String ACTIVATION_REFERRAL_DIALOG = "ReferralGiftCardNumberActivation";

    /** constant for deactivation referral dialog name */
    public static final String DEACTIVATION_REFERRAL_DIALOG = "ReferralGiftCardNumberDeactivation";

    /** constant for offline dialog name */
    public static final String ACTIVATION_OFFLINE_DIALOG = "GiftCardActivationError";

    /** Constant for Manual Approval letter */
    public static final String MANUAL_APPROVAL_LETTER = "Manual";
    
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ActivationCargo cargo = (ActivationCargo)bus.getCargo();
        AuthorizeTransferResponseIfc response = cargo.getCurrentResponse();
        AuthorizeTransferRequestIfc request = cargo.getCurrentRequest();
        String cardLastFour = getCardLastFourDigits(response, request);

        if(RequestSubType.Redeem.equals(request.getRequestSubType()))
        {
            String[] dialogArgs = {getRequestSubTypeText(bus, request.getRequestSubType()), 
                    cardLastFour, getResponseCodeText(bus, response.getResponseCode())};
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID(GIFT_CARD_ERROR_DIALOG);
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setArgs(dialogArgs);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonActionsIfc.FAILURE);
            // display dialog
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            String referralNumber = "";
            try
            {
                referralNumber = pm.getStringValue("GiftCardActivationRefPhoneNumber");
            }
            catch (ParameterException e)
            {
                logger.warn("Could not get Giftcard activation referral number", e);
            }
            String[] dialogArgs = {cardLastFour, referralNumber};
            String resourceId = ACTIVATION_REFERRAL_DIALOG;
            if(RequestSubType.Deactivate.equals(request.getRequestSubType()))
            {
                resourceId = DEACTIVATION_REFERRAL_DIALOG;
            }
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID(resourceId);
            dialogModel.setType(DialogScreensIfc.YES_NO);
            dialogModel.setArgs(dialogArgs);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, MANUAL_APPROVAL_LETTER);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonActionsIfc.FAILURE);
            // display dialog
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
    }

    /**
     * Retrieves the last four digits of the card number
     * @param response
     * @param request
     * @return the last four digits of the card number
     */
    protected String getCardLastFourDigits(AuthorizeTransferResponseIfc response, AuthorizeTransferRequestIfc request)
    {
        String cardLastFour = response.getMaskedAccountNumber();
        if(cardLastFour != null && cardLastFour.length() > 4)
        {
            cardLastFour = cardLastFour.substring(cardLastFour.length() - 4);
        }
        else if(request.getAccountNumber() != null)
        {
            KeyStoreEncryptionManagerIfc encryptionManager = (KeyStoreEncryptionManagerIfc)Gateway.getDispatcher().getManager(KeyStoreEncryptionManagerIfc.TYPE);
            try
            {
                cardLastFour = new String(encryptionManager.decrypt(Base64.decodeBase64(request.getAccountNumber().getBytes())));
                if(cardLastFour != null && cardLastFour.length() > 4)
                {
                    cardLastFour = cardLastFour.substring(cardLastFour.length() - 4);
                }
            }
            catch(EncryptionServiceException ese)
            {
                logger.error("Could not decrypt gift card number");
                cardLastFour = "";
            }
        }
        else
        {
            cardLastFour = "";
        }
        return cardLastFour;
    }
}
