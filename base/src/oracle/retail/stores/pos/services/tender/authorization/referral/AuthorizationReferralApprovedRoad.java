/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/referral/AuthorizationReferralApprovedRoad.java /main/3 2014/07/01 13:33:27 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   06/05/14 - XbranchMerge
 *                         blarsen_bug18854403-ajb-call-ref-trans-cancel-bad-invoiceid
 *                         from rgbustores_14.0x_generic_branch
 *    blarsen   06/03/14 - Refactor: Moving call referral fields into their new
 *                         class.
 *    icole     04/30/13 - Handle check authorization when offline and convert
 *                         eCheck to deposited check for referrals and floor
 *                         limit per the functional requirements.
 *    cgreene   03/21/12 - refactor referral into separate tour
 *    ohorne    10/14/11 - fix for NPE when over tendering after call referral
 *    jswan     09/28/11 - Modified to force signature capture for manually
 *                         approved credit tenders.
 *    cgreene   09/22/11 - negate call referral amount if the transtype is
 *                         refund or void.
 *    blarsen   08/12/11 - Added support for CallReferralApprovedAmount. This
 *                         is the now editable amount on the call referral
 *                         screen.
 *    asinton   06/27/11 - Added new UI for Call Referral and application flow
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization.referral;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc.AuthorizationMethod;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.tender.authorization.AuthorizationCargo;

/**
 * This road captures the approval code from the UI and stores in into the
 * response. Also, the response's ResponseCode attribute is set to Approved.
 *
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class AuthorizationReferralApprovedRoad extends PosLaneActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        AuthorizationCargo authorizationCargo = (AuthorizationCargo)bus.getCargo();

        // retrieve the call referral approval code
        String approvalCode = authorizationCargo.getCallReferralData().getApprovalCode();
        // retrieve the call referral approval code
        CurrencyIfc approvedAmount = authorizationCargo.getCallReferralData().getApprovedAmount();
        // reset the callReferralApprovalCode
        authorizationCargo.getCallReferralData().setApprovalCode(null);
        // reset the callReferralApprovedAmount
        authorizationCargo.getCallReferralData().setApprovedAmount(null);

        // get the most recently added response
        AuthorizeTransferResponseIfc response = null;
        if (authorizationCargo.getResponseList().size() > 0)
        {
            response = authorizationCargo.getResponseList().get(authorizationCargo.getResponseList().size() - 1);
        }
        if (response != null)
        {
            // set values entered on referral screen
            response.setAuthorizationCode(approvalCode);
            response.setResponseMessage(approvalCode);
            response.setAuthorizationMethod(AuthorizationMethod.Manual);
            // set the amount that was approved onto the response.
            int authTransactionType = authorizationCargo.getCurrentRequest().getAuthorizationTransactionType();
            if (authTransactionType == AuthorizationConstantsIfc.TRANS_VOID ||
                authTransactionType == AuthorizationConstantsIfc.TRANS_CREDIT)
            {
                approvedAmount = approvedAmount.negate();
            }
            response.setBaseAmount(approvedAmount);
            if (response.getTenderType().equals(TenderType.CHECK))
            {
                response.setResponseCode(ResponseCode.ApprovedReferral);
            }
            else
            {
                // set the response approved
                response.setResponseCode(ResponseCode.Approved);
            }
        }
        else
        {
            logger.warn("Response is null");
        }
    }

}
