/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/AuthorizationSite.java /main/18 2014/07/01 13:33:27 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   06/05/14 - XbranchMerge
 *                         blarsen_bug18854403-ajb-call-ref-trans-cancel-bad-invoiceid
 *                         from rgbustores_14.0x_generic_branch
 *    blarsen   06/04/14 - Added call referral traceNumber & entryMethod to fix
 *                         American Eagle SRs 18854403 & 18854433.
 *    blarsen   06/03/14 - Refactor: Moving call referral fields into their new
 *                         class.
 *    blarsen   02/04/14 - AJB requires original auth response for call
 *                         referrals. Adding this to appropriate
 *                         shuttles/cargos.
 *    asinton   01/23/14 - fixed handling of approvals for gift card tender for
 *                         zero amount and calcalation of remaining balance
 *    asinton   11/14/13 - modified to manage updates to the archived
 *                         transaction for possible reversal of pending
 *                         authorizations in the case of application crash
 *    icole     09/27/13 - Allow transaction to timeout when setting on the
 *                         Authorization Failed dialog, within
 *                         AuthorizationSite. Changed letter from Timeout to
 *                         AuthTimeout.
 *    blarsen   08/12/13 - AttemptCallReferralWhenOffline param only relevant
 *                         when bank is offline. If payment service is offline,
 *                         never do call referral.
 *    asinton   06/12/13 - prevent call referral for credit / gift card refunds
 *    asinton   05/31/13 - reenable call referral for aci gift card and credit
 *                         cards
 *    icole     04/30/13 - Handle check authorization when offline and convert
 *                         eCheck to deposited check for referrals and floor
 *                         limit per the functional requirements.
 *    asinton   08/02/12 - Call referral refactor
 *    cgreene   07/13/12 - Do not convert Offline to Referral message since
 *                         access to the PED is not likely
 *    asinton   07/03/12 - forwarded 13.4.1 fix that allows call referral for
 *                         credit when connection to authorization switch is
 *                         down.
 *    asinton   02/22/12 - allow call referral for gift card when offline
 *    icole     03/06/12 - Refactor to remove CPOIPaymentUtility and attempt to
 *                         have more generic code, rather than heavily Pincomm.
 *    asinton   02/29/12 - allow call referral for credit when offline response
 *                         received.
 *    asinton   02/22/12 - allow call referral for gift card when offline
 *    blarsen   01/30/12 - Change default credit floor limit to $50.00
 *                         blarsen_bug13628130-credit-floor-limit-should-be-less-than-or-equals
 *    blarsen   01/30/12 - Fixed floor limit comparison. If amount == floor
 *                         limit, then floor limit auth is eligible.
 *    asinton   01/12/12 - XbranchMerge asinton_bug-13558615 from
 *                         rgbustores_13.4x_generic_branch
 *    asinton   01/11/12 - Use FloorLimit ResponseCode for improved handling of
 *                         this response.
 *    asinton   01/06/12 - changes to make credit floor limit authorizations
 *                         synchronous
 *    asinton   12/29/11 - XbranchMerge asinton_bug-13526185 from
 *                         rgbustores_13.4x_generic_branch
 *    asinton   12/28/11 - fixed logic to not allow floor limit approvals with
 *                         Debit
 *    asinton   12/22/11 - Get Offline Credit Floor Limit and set in the
 *                         request
 *    cgreene   10/26/11 - set base amount when it is null
 *    sgu       10/20/11 - check floor limit for call referral response
 *    jswan     10/07/11 - Fixed positive ID issue with Check tender.
 *    icole     10/07/11 - Don't allow referral for DEBIT.
 *    jswan     10/04/11 - Fixed offline issues with SAFTOR.
 *    icole     09/29/11 - Treat CPOI timeout as OFFLINE. DefectID 937.
 *    blarsen   09/27/11 - Ending scrolling receipt session for
 *                         AuthorizeCardRefund requests.
 *    jswan     09/26/11 - Modified to force the check tender to execute the
 *                         referral path when the response code is equal to
 *                         Offline.
 *    icole     09/15/11 - Moved ending scrolling receipt from
 *                         CardAuthConnector to AuthorizationSite using
 *                         CPOIUtility.
 *    blarsen   09/13/11 - Debit with cash back always prompting operator with
 *                         partial approval. Old logic did not consider cash
 *                         back in response and assumed if request/response
 *                         auth amounts were not equal, there was a partial
 *                         approval. Fixed this.
 *    asinton   08/24/11 - set the POSGiftCardEntryRequired flag on the request
 *    ohorne    08/09/11 - APF:foreign currency support
 *    cgreene   07/29/11 - check for referral before offline
 *    cgreene   07/14/11 - fix tendering and reload gift cards
 *    blarsen   06/30/11 - Setting ui's financial network status flag based on
 *                         payment manager response. This will update the
 *                         online/offline indicator on POS UI.
 *    blarsen   06/21/11 - Added handling for configuration error and the
 *                         default 'unknown' error case.
 *    blarsen   06/10/11 - Displaying generic 'please wait: authorizing...'
 *                         message when sending request to payment service.
 *    blarsen   06/06/11 - Added cancel-card-inquiry-by-customer flow.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    cgreene   05/20/11 - implemented enums for reponse code and giftcard
 *                         status code
 *    ohorne    05/12/11 - added offline floor limit logic
 *    sgu       05/05/11 - refactor payment technician commext framework
 *    asinton   03/25/11 - Moved APF request and response objects to common
 *                         module.
 *    asinton   03/21/11 - new tender authorization service
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponse;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceRequestIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceRequestIfc.RequestType;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.archive.AuthorizedTendersTransactionArchiveHelperIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This site calls the Payment Manager for authorization.
 *
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class AuthorizationSite extends PosSiteActionAdapter
{
    /** Constant for application property group */
    public static final String APPLICATION_PROPERTY_GROUP = "application";

    /** Constant for manual entry gift card property name */
    public static final String POS_GFCARD_TENDER_ENTRY_REQUIRED = "POSGFCardTenderEntryRequired";

    /** Constant for an Attempt to perform a Call Referral When Offline */
    public static final String ATTEMPT_CALL_REFERRAL_WHEN_OFFLINE = "AttemptCallReferralWhenOffline";

    /** Constant for Offline Check Floor Limit Parameter */
    public static final String OFFLINE_CHECK_FLOOR_LIMIT_PARAMETER = "OfflineCheckFloorLimit";

    /** Constant for Offline Credit Floor Limit Parameter */
    public static final String OFFLINE_CREDIT_FLOOR_LIMIT_PARAMETER = "OfflineCreditFloorLimit";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        AuthorizationCargo cargo = (AuthorizationCargo)bus.getCargo();
        PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);

        if (cargo.getCurrentRequest().getRequestType().equals(PaymentServiceRequestIfc.RequestType.AuthorizeCard) ||
                        cargo.getCurrentRequest().getRequestType().equals(PaymentServiceRequestIfc.RequestType.AuthorizeCardRefund))
        {
            WorkstationIfc workstation = cargo.getRegister().getWorkstation();
            paymentManager.endScrollingReceipt(workstation);
        }
        AuthorizeTransferRequestIfc request = cargo.getCurrentRequest();
        setOfflineFloorLimit(bus, request);
        boolean posGFCardEntryRequired = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP, POS_GFCARD_TENDER_ENTRY_REQUIRED, false);
        request.setPosGFCardEntryRequired(posGFCardEntryRequired);

        AuthorizeTransferResponseIfc response = null;
        String letter = CommonLetterIfc.ERROR;
        if((TenderType.CREDIT.equals(cargo.getCallReferralData().getTenderType()) ||
                TenderType.GIFT_CARD.equals(cargo.getCallReferralData().getTenderType())) &&
                (cargo.getCallReferralData().getAccountNumberToken() != null || cargo.getCallReferralData().getTenderType() != null))
        {
            letter = CommonLetterIfc.REFERRAL;
            response = buildCallReferralResponse(cargo, request);
            cargo.addResponse(response);
        }

        if(response == null)
        {
            displayAuthorizationUI(bus);

            try
            {
                response = (AuthorizeTransferResponseIfc)paymentManager.authorize(request);
            }
            catch(Exception e)
            {
                logger.error("Exception caught while calling the PaymentManagerIfc.authorize()", e);
            }
            if (response != null)
            {
                cargo.addResponse(response);
                // set the base amount if the response wasn't able to
                if (response.getBaseAmount() == null)
                {
                    response.setBaseAmount(request.getBaseAmount());
                }
                ResponseCode responseCode = response.getResponseCode();
                letter = convertResponseToLetter(request, response, responseCode);
            }

            UIUtilities.setFinancialNetworkUIStatus(response, (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE));
        }
        // if approved or partially approved then update to the transaction archive
        // for possible reversing of tenders in the case of application crash
        if(CommonLetterIfc.APPROVED == letter ||
                CommonLetterIfc.PARTIALLY_APPROVED == letter)
        {
            AuthorizedTendersTransactionArchiveHelperIfc helper = (AuthorizedTendersTransactionArchiveHelperIfc)BeanLocator.getServiceBean(AuthorizedTendersTransactionArchiveHelperIfc.AUTHORIZED_TENDERS_TRANSACTION_ARCHIVE_HELPER_BEAN_KEY);
            helper.addPendingAuthorizationToTransactionArchive(bus, request, response);
        }
        bus.mail(letter);
    }

    /**
     * Builds the AuthorizeTransferResponseIfc instance based on the request and data from the cargo
     * @param cargo
     * @param request
     * @return
     */
    protected AuthorizeTransferResponseIfc buildCallReferralResponse(AuthorizationCargo cargo, AuthorizeTransferRequestIfc request)
    {
        AuthorizeTransferResponseIfc response = new AuthorizeTransferResponse();
        // data from cargo
        response.setAccountNumberToken(cargo.getCallReferralData().getAccountNumberToken());
        response.setTenderType(cargo.getCallReferralData().getTenderType());
        response.setTenderSubType(cargo.getCallReferralData().getTenderSubType());
        response.setMaskedAccountNumber(cargo.getCallReferralData().getMaskedAccountNumber());
        response.setAccountNumber(cargo.getCallReferralData().getGiftcardAccountNumber());
        response.setRawResponse(cargo.getCallReferralData().getOriginalAuthResponse());
        // data from request
        response.setBaseAmount(request.getBaseAmount());
        response.setResponseCode(ResponseCode.Referral);
        response.setEntryMethod(cargo.getCallReferralData().getEntryMethod());
        response.setTraceNumber(cargo.getCallReferralData().getTraceNumber());
        return response;
    }

    /**
     * @param request
     * @param response
     * @param letter
     * @param responseCode
     * @return
     */
    protected String convertResponseToLetter(AuthorizeTransferRequestIfc request,
            AuthorizeTransferResponseIfc response,
            ResponseCode responseCode)
    {
        String letter = CommonLetterIfc.ERROR;

        if (ResponseCode.Approved.equals(responseCode))
        {
            // The authorized amount for a refund must come back from the
            // authorization service with a negative amount in order to balance
            // the transaction.  However, the request is always expressed as a
            // positive number; therefore, make this comparison to the absolute
            // value of the response (authorized) amount.

            // Note that the base amount in the response might be more than the base amount in the request
            // for Debit card tenders when cash back is selected by the customer.
            if ( response.getBaseAmount().abs().compareTo(request.getBaseAmount()) == CurrencyIfc.LESS_THAN)
            {
                letter = CommonLetterIfc.PARTIALLY_APPROVED;
            }
            else
            {
                letter = CommonLetterIfc.APPROVED;
            }
        }
        else if (ResponseCode.ApprovedZeroAmount.equals(responseCode))
        {
            letter = "ApprovedZeroAmount";
        }
        else if (ResponseCode.PositiveIDRequired.equals(responseCode))
        {
            // Already have the ID for Check tenders.
            if ((TenderType.CHECK.equals(response.getTenderType())))
            {
                letter = CommonLetterIfc.APPROVED;
            }
            else
            {
                letter = CommonLetterIfc.POSITIVE_ID;
            }
        }
        else if (ResponseCode.Declined.equals(responseCode))
        {
            letter = CommonLetterIfc.DECLINED;
        }
        else if (ResponseCode.ConfigurationError.equals(responseCode))
        {
            letter = CommonLetterIfc.CONFIGURATION_ERROR;
        }
        else if (ResponseCode.InquiryForTenderCanceledByCustomer.equals(responseCode))
        {
            letter = CommonLetterIfc.CANCELED_BY_CUSTOMER;
        }
        else if (ResponseCode.Timeout.equals(responseCode))
        {
            letter = CommonLetterIfc.AUTH_TIMEOUT;
        }
        else if (ResponseCode.DeviceTimeout.equals(responseCode))
        {
        	letter = CommonLetterIfc.OFFLINE;
        }
        else if (ResponseCode.Referral.equals(responseCode))
        {
            // DEBIT is not allowed to do Call Referral
            if (TenderType.DEBIT.equals(response.getTenderType()))
            {
                letter = CommonLetterIfc.DECLINED;
            }
            else
            {
                letter = CommonLetterIfc.REFERRAL;
            }
        }
        else if(ResponseCode.FloorLimit.equals(responseCode))
        {
            letter = CommonLetterIfc.FLOOR_LIMIT;
        }
        else if (ResponseCode.Offline.equals(responseCode) || AuthorizationConstantsIfc.ONLINE != response.getFinancialNetworkStatus())
        {
            if(TenderType.CHECK.equals(response.getTenderType()))
            {
                if (request.getBaseAmount().compareTo(request.getFloorLimit())!= CurrencyIfc.GREATER_THAN)
                {
                    letter = CommonLetterIfc.FLOOR_LIMIT;
                    response.setResponseCode(ResponseCode.FloorLimit);
                }
                else
                {
                    letter = CommonLetterIfc.REFERRAL;
                    response.setResponseCode(ResponseCode.Referral);
                }
            }
            else
            {

                boolean attemptCallReferralWhenBankOffline = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP, ATTEMPT_CALL_REFERRAL_WHEN_OFFLINE, false);

                /*
                 * Do not allow referral for cards other than credit and gift card.
                 * Do not allow referral for refunds.
                 * Only allow referral when bank is offline.
                 * When payment service is down (and CPOIs), do not attempt call referral.
                 */
                if((attemptCallReferralWhenBankOffline && response.getFinancialNetworkStatus() == AuthorizationConstantsIfc.BANK_OFFLINE) &&
                        !(RequestType.AuthorizeCardRefund.equals(request.getRequestType()) ||
                                RequestType.AuthorizeCardRefundWithToken.equals(request.getRequestType())) &&
                        (TenderType.CREDIT.equals(response.getTenderType()) ||
                         TenderType.GIFT_CARD.equals(response.getTenderType())))
                {
                    letter = CommonLetterIfc.REFERRAL;
                }
                else
                {
                    // Either the application has the reference to the tender ID, or it can get
                    // the reference.  Do not do Referrals for DEBIT
                    // 12JUL12 - Can't assume ability to do referral because PC-EFT might be down
                    // thus we never had the token to do a referral.
                    letter = CommonLetterIfc.OFFLINE;
                }
            }
        }
        return letter;
    }

    /**
     * Fetches the offline floor limit from parameters and sets it in the request
     * @param bus
     * @param request
     */
    protected void setOfflineFloorLimit(BusIfc bus, AuthorizeTransferRequestIfc request)
    {
        CurrencyIfc floorLimit = null;
        try
        {
            ParameterManagerIfc parameterManager = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            // if it's a check, we know it's a check, then use the check floor limit parameter
            if(TenderType.CHECK.equals(request.getRequestTenderType()))
            {
                floorLimit = DomainGateway.getBaseCurrencyInstance(parameterManager.getStringValue(OFFLINE_CHECK_FLOOR_LIMIT_PARAMETER));
            }
            // else use the credit floor limit parameter.
            else
            {
                floorLimit = DomainGateway.getBaseCurrencyInstance(parameterManager.getStringValue(OFFLINE_CREDIT_FLOOR_LIMIT_PARAMETER));
            }
        }
        catch(ParameterException pe)
        {
            logger.warn("Could not get parameter", pe);
            floorLimit = DomainGateway.getBaseCurrencyInstance("50.00");
        }
        request.setFloorLimit(floorLimit);
    }

    /**
     * This method displays the authorize screen.
     *
     * This instructs the operator to wait for authorization to complete.
     *
     * @param bus
     */
    protected void displayAuthorizationUI(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        PromptAndResponseModel parModel = new PromptAndResponseModel();

        String promptText = utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                BundleConstantsIfc.COMMON_BUNDLE_NAME, "AuthorizationPrompt", "AuthorizationPrompt");

        parModel.setPromptText(promptText);

        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        baseModel.setPromptAndResponseModel(parModel);
        ui.showScreen(POSUIManagerIfc.AUTHORIZATION, baseModel);
    }

}
