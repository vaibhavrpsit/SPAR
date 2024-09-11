/* ===========================================================================
* Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/activation/ActivationDeclinedAisle.java /main/2 2013/09/09 14:05:49 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   09/06/13 - made the canceling of transaction with deactivation
 *                         of giftcards more robust
 *    jswan     10/14/11 - Parameterized the request sub type for all error
 *                         dialog messages in the activation service.
 *    asinton   08/15/11 - show the appropriate error dialog for redeem of zero
 *                         balance gift card
 *    cgreene   05/27/11 - move auth response objects into domain
 *    asinton   05/04/11 - New activation service for APF.
 *    asinton   05/03/11 - New activation service for APF.
 *    asinton   05/03/11 - New activation service for APF.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.activation;

import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc.RequestSubType;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This aisle shows the authorization declined dialog.
 * 
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class ActivationDeclinedAisle extends PosLaneActionAdapter
{
    /** constant for declined dialog name */
    public static final String GC_ERROR_REENTER = "GiftCardErrorwithOptiontoReenter";

    /** constant for zero balance dialog name */
    public static final String GIFT_CARD_ZERO_BALANCE_DIALOG = "GiftCardZeroBalance";

    /** Constant for Manual Approval letter */
    public static final String CARD_INFO_LETTER = "CardInfo";

    /** Constant for Failure letter */
    public static final String FAILURE_LETTER = "Failure";

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ActivationCargo cargo = (ActivationCargo)bus.getCargo();
        AuthorizeTransferResponseIfc response = cargo.getCurrentResponse();
        String cardLastFour = response.getMaskedAccountNumber();
        if(cardLastFour != null && cardLastFour.length() > 4)
        {
            cardLastFour = cardLastFour.substring(cardLastFour.length() - 4);
        }
        else
        {
            cardLastFour = "";
        }
        RequestSubType requestSubType = cargo.getCurrentRequest().getRequestSubType();
        DialogBeanModel dialogModel = new DialogBeanModel();
        if(RequestSubType.Redeem.equals(requestSubType))
        {
            showGiftCardZeroBalanceDialog(cardLastFour, dialogModel);
        }
        else
        {
            AuthorizeTransferRequestIfc request = cargo.getCurrentRequest();
            showGiftCardErrorWithOptionToReenterDialog(bus, response,
                    cardLastFour, dialogModel, request);
        }
        // display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * Shows the gift card error with option to reenter dialog.
     * @param bus the tour bus instance
     * @param response the response object
     * @param cardLastFour the cards last four digits
     * @param dialogModel the dialog model use use
     * @param request the request object
     */
    protected void showGiftCardErrorWithOptionToReenterDialog(BusIfc bus, AuthorizeTransferResponseIfc response, String cardLastFour, DialogBeanModel dialogModel, AuthorizeTransferRequestIfc request)
    {
        String[] dialogArgs = {getRequestSubTypeText(bus, request.getRequestSubType()), cardLastFour, getResponseCodeText(bus, response.getResponseCode())};
        dialogModel.setResourceID(GC_ERROR_REENTER);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(dialogArgs);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK , CARD_INFO_LETTER);
    }

    /**
     * Shows the gift card zero balance dialog.
     * @param cardLastFour the cards last four digits
     * @param dialogModel the dialog model use use
     */
    protected void showGiftCardZeroBalanceDialog(String cardLastFour, DialogBeanModel dialogModel)
    {
        String[] dialogArgs = {cardLastFour};
        dialogModel.setResourceID(GIFT_CARD_ZERO_BALANCE_DIALOG);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(dialogArgs);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, FAILURE_LETTER);
    }

    /*
     * Get the I18N text associated with the response code for display in the error dialog 
     */
    protected String getResponseCodeText(BusIfc bus, ResponseCode responseCode)
    {
        // Added "Response" to the toString() value of the ResponseCode in order
        // to avoid collisions with other keys with "Common." group name  

        // Get the I18N unknown error to serve as the default text for all other response code
        // text values.
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        String unknowErrorText = utility.retrieveText("ResponseCodeText",
                                                     BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                                                     ResponseCode.Unknown+"Response",
                                                     "<UNKNOWN ERROR>");
        
        // The the actual value if available
        String retValue = utility.retrieveText("ResponseCodeText",
                BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                responseCode.toString()+"Response",
                unknowErrorText);

        return retValue;
    }

    /*
     * Get the I18N text associated with the request sub type for display in the error dialog 
     */
    protected String getRequestSubTypeText(BusIfc bus, RequestSubType requestSubType)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        String retValue = utility.retrieveText("GiftCardRequestSubType",
                BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                requestSubType.toString(), RequestSubType.Activate.toString());

        return retValue;
    }
}
