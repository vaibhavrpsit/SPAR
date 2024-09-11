/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/activation/ActivationCanceledAisle.java /main/2 2014/02/20 14:40:22 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     02/20/14 - Fixing null pointer exception when cancelling failed
 *                         gift card refund tender.
 *    asinton   02/10/14 - reworked flow for gift card activation error
 *                         scenarios
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
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

import org.apache.commons.codec.binary.Base64;

/**
 * @since 14.0.1
 */
@SuppressWarnings("serial")
public class ActivationCanceledAisle extends PosLaneActionAdapter
{
    public static final String GIFT_CARD_ACTIVATION_CANCEL_DIALOG = "GiftCardActivationCancel";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ActivationCargo cargo = (ActivationCargo)bus.getCargo();
        if (cargo.getCurrentLineNumber() != null)
        {
	        cargo.addFailedLineNumber(cargo.getCurrentLineNumber());
        }
        
        AuthorizeTransferResponseIfc response = cargo.getCurrentResponse();
        AuthorizeTransferRequestIfc request = cargo.getCurrentRequest();
        String[] dialogArgs = getDialogParameters(response, request);
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(GIFT_CARD_ACTIVATION_CANCEL_DIALOG);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(dialogArgs);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonActionsIfc.FAILURE);

        // display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * Retrieves the parameters for the gift card activation canceled dialog in the
     * form of an String array giving the gift cards last four digits and the amount.
     * @param response the <code>AuthorizeTransferResponseIfc</code> instance
     * @param request the <code>AuthorizeTransferRequestIfc</code> instance
     * @return an array containing the gift cards last four digits and the amount
     */
    protected String[] getDialogParameters(AuthorizeTransferResponseIfc response, AuthorizeTransferRequestIfc request)
    {
        String cardLastFour = response.getMaskedAccountNumber();
        String amount = request.getBaseAmount().toFormattedString();
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
        return new String[] { cardLastFour, amount };
    }
}
