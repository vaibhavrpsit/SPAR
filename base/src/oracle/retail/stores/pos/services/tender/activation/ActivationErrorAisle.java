/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/activation/ActivationErrorAisle.java /rgbustores_13.4x_generic_branch/3 2011/10/29 13:07:05 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/29/11 - fix retrival of last four of card number for error
 *                         dialog
 *    jswan     10/14/11 - Parameterized the request sub type for all error
 *                         dialog messages in the activation service.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.activation;

import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

import org.apache.commons.codec.binary.Base64;

/**
 * This aisle shows the authorization declined dialog.
 * 
 * @author jswan
 * @since 13.4
 */
@SuppressWarnings("serial")
public class ActivationErrorAisle extends ActivationDeclinedAisle
{
    /** constant for declined dialog name */
    public static final String GIFT_CARD_ERROR_DIALOG = "GiftCardError";

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
