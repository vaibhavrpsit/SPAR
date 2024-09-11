/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/AuthorizationLaunchShuttle.java /main/5 2014/07/01 13:33:27 blarsen Exp $
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
 *    mchellap  01/28/14 - Add call referral authorization details to cargo
 *                         from authorization response.
 *    asinton   11/14/13 - added code to carry the transaction archive name to
 *                         reversal and authorization services for transaction
 *                         archival to support potential reversal of pending
 *                         authorizations in the case of application crash
 *    asinton   08/02/12 - Call referral refactor
 *    asinton   07/02/12 - carry call referral authorization details from
 *                         Mobile POS to call referral site.
 *    cgreene   03/21/12 - refactor referral into separate tour
 *    cgreene   03/21/12 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;

/**
 * A shuttle that unloads the {@link AuthorizationCargo} directly.
 *
 * @author cgreene
 * @since 13.4.1
 */
public class AuthorizationLaunchShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = 3626531416731775989L;

    protected AuthorizationCargo cargo;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        cargo = (AuthorizationCargo)bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        AuthorizationCargo cargo = (AuthorizationCargo)bus.getCargo();
        cargo.requestList = this.cargo.requestList;
        cargo.responseList = this.cargo.responseList;
        cargo.currentIndex = this.cargo.currentIndex;
        cargo.creditReferralBeanModel = this.cargo.creditReferralBeanModel;
        AuthorizeTransferResponseIfc response = null;

        // MPOS: copy in call referral data captured from previous authorization attempt
        if(cargo.getResponseList().size() > 0)
        {
            response = cargo.getResponseList().get(cargo.getResponseList().size() - 1);
        }

        if(response != null)
        {
            CallReferralData callReferralData = new CallReferralData();
            callReferralData.setAccountNumberToken(response.getAccountNumberToken());
            callReferralData.setTenderType(response.getTenderType());
            callReferralData.setMaskedAccountNumber(response.getMaskedAccountNumber());
            callReferralData.setTenderSubType(response.getTenderSubType());
            callReferralData.setOriginalAuthResponse(response.getRawResponse());
            callReferralData.setTraceNumber(response.getTraceNumber());
            callReferralData.setEntryMethod(response.getEntryMethod());

            if(TenderType.GIFT_CARD.equals(response.getTenderType()))
            {
                callReferralData.setGiftcardAccountNumber(response.getAccountNumber());
            }
            cargo.setCallReferralData(callReferralData);
        }
        else
        {
                cargo.setCallReferralData(this.cargo.getCallReferralData());
        }

        cargo.setTransactionArchiveName(this.cargo.getTransactionArchiveName());
    }

}
