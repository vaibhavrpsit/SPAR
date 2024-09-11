/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/SendCallReferralAuthorizationSite.java /main/4 2014/07/01 13:33:27 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   08/13/14 - deprecated because this class moved to referral subpackage
 *    blarsen   06/05/14 - XbranchMerge
 *                         blarsen_bug18854403-ajb-call-ref-trans-cancel-bad-invoiceid
 *                         from rgbustores_14.0x_generic_branch
 *    blarsen   06/03/14 - Refactor: Moving call referral fields into their new
 *                         class.
 *    blarsen   02/12/14 - Some payment systems (AJB EMV) may not requie a
 *                         token, per se, for call referrals. Check the new
 *                         TokenRequiredForCallReferral property. Do not fail a
 *                         tender for a missing token when a token is not
 *                         required.
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    asinton   11/03/11 - added improved handling for call referral failure
 *    asinton   11/02/11 - reverting back to synchronous call referral flow
 *    asinton   09/29/11 - Added Card Type to the Call Referral Screen, made it
 *                         depended upon credit tender only.
 *    blarsen   08/12/11 - Added support for CallReferralApprovedAmount. This
 *                         amount is edited by the operator on the call
 *                         referral screen. This amount should be used in the
 *                         referral request, not the amount from the original
 *                         auth request.
 *    blarsen   08/02/11 - Renamed token to accountNumberToken to be
 *                         consistent.
 *    asinton   07/21/11 - added call referral for checks
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeCallReferralRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceRequestIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceRequestIfc.RequestType;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CreditReferralBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

import org.apache.commons.lang3.StringUtils;

/**
 * This site sends the call referral message to the authorizer.  It is
 * expected to be accepted by the authorizer so no decline is expected.
 *
 * @author asinton
 * @since 13.4
 * @deprecated as of 14.1.  This class moved to {@link oracle.retail.stores.pos.services.tender.authorization.referral.SendCallReferralAuthorizationSite}.
 */
@SuppressWarnings("serial")
public class SendCallReferralAuthorizationSite extends PosSiteActionAdapter
{
    /** Constant for the call referral error dialog */
    public static final String CALL_REFERRAL_ERROR_DIALOG = "CallReferralError";

    /** Is token required for Call Referrals? */
    protected static final String POS_TOKEN_REQUIRED_FOR_CALL_REFERRAL = "TokenRequiredForCallReferral";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        AuthorizationCargo authorizationCargo = (AuthorizationCargo)bus.getCargo();
        // assume success
        String letter = CommonLetterIfc.SUCCESS;

        // get the most recently added response
        AuthorizeTransferResponseIfc response = null;
        if(authorizationCargo.getResponseList().size() > 0)
        {
            response = authorizationCargo.getResponseList().get(authorizationCargo.getResponseList().size() - 1);
        }
        if(response != null)
        {
            // capture information from the response and set it on the request
            AuthorizeTransferRequestIfc request = authorizationCargo.getCurrentRequest();
            AuthorizeCallReferralRequestIfc callReferralRequest = request.newAuthorizeCallReferralRequestInstance();
            // get the approval code
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            CreditReferralBeanModel model = ((CreditReferralBeanModel) ui.getModel(POSUIManagerIfc.CREDIT_REFERRAL));
            String approvalCode = model.getApprovalCode();
            authorizationCargo.getCallReferralData().setApprovalCode(approvalCode);
            callReferralRequest.setApprovalCode(approvalCode);

            // when a call referral is partially approved, the operator changes the amount on the call referral screen
            // (use the edited amount, not the amount contained in the original auth request)
            CurrencyIfc enteredAmount = model.getChargeAmount();
            authorizationCargo.getCallReferralData().setApprovedAmount(enteredAmount);
            callReferralRequest.setBaseAmount(enteredAmount);

            // capture the card type in the response, if it is different
            String callReferralCardType = model.getCardType();
            if(StringUtils.isNotEmpty(callReferralCardType) && !callReferralCardType.equals(response.getTenderSubType()))
            {
                response.setTenderSubType(callReferralCardType);
            }

            boolean callPaymentManager = false;
            RequestType requestType = request.getRequestType();
            // credit, debit
            if(PaymentServiceRequestIfc.RequestType.AuthorizeCard.equals(requestType))
            {
                callReferralRequest.setAccountNumberToken(response.getAccountNumberToken());
                callReferralRequest.setOriginalResponseCode(response.getResponseCode());
                callReferralRequest.setRequestType(RequestType.AuthorizeCallReferral);
                callPaymentManager = true;
            }
            // check, echeck
            else if(PaymentServiceRequestIfc.RequestType.AuthorizeCheck.equals(requestType) ||
            		PaymentServiceRequestIfc.RequestType.AuthorizeECheck.equals(requestType))
            {
                callReferralRequest.setOriginalResponseCode(response.getResponseCode());
                callReferralRequest.setRequestType(RequestType.AuthorizeCheckCallReferral);
                callPaymentManager = true;
            }
            if(callPaymentManager)
            {
                AuthorizeTransferResponseIfc callReferralResponse = null;
                if(PaymentServiceRequestIfc.RequestType.AuthorizeCard.equals(requestType) &&
                        callReferralRequest.isCallReferralWithoutToken())
                {
                    /* When Authorization is for a Card, validate that response already
                     * contains an Account Number token.  If missing, then Prompt for card swipe
                     * to card data for call referral update.
                     */
                    // Set the status on the UI
                    ui.statusChanged(POSUIManagerIfc.FINANCIAL_NETWORK_STATUS,POSUIManagerIfc.ONLINE);
                    ui.showScreen(POSUIManagerIfc.CREDIT_DEBIT_CARD, new POSBaseBeanModel());
                }
                try
                {
                    PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);
                    callReferralResponse = (AuthorizeTransferResponseIfc)paymentManager.authorize(callReferralRequest);
                }
                catch(Exception e)
                {
                    logger.error("Exception caught while calling the PaymentManagerIfc.authorize()", e);
                }

                boolean isTokenRequiredForCallReferral = Gateway.getBooleanProperty(Gateway.APPLICATION_PROPERTIES_GROUP, POS_TOKEN_REQUIRED_FOR_CALL_REFERRAL, true);

                if((isTokenRequiredForCallReferral && callReferralRequest.isCallReferralWithoutToken()) &&
                        callReferralResponse != null &&
                        !ResponseCode.Approved.equals(callReferralResponse.getResponseCode()))
                {
                    // if this happens, then the call referral update failed.  Show the dialog and exit with Failure
                    letter = null;
                    DialogBeanModel dialogModel = new DialogBeanModel();
                    dialogModel.setResourceID(CALL_REFERRAL_ERROR_DIALOG);
                    dialogModel.setType(DialogScreensIfc.ERROR);
                    dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonActionsIfc.FAILURE);
                    // display dialog
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                }
            }
        }

        if(letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }
}
