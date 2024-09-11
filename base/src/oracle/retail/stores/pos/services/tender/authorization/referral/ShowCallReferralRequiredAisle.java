/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/referral/ShowCallReferralRequiredAisle.java /main/3 2014/07/01 13:33:27 blarsen Exp $
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
 *    asinton   11/23/13 - made the CallReferralRequired resource ID into a
 *                         constant
 *    asinton   08/02/12 - Call referral refactor
 *    cgreene   03/21/12 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization.referral;

import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.authorization.AuthorizationCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Display a dialog that indicates that Call Referral is required.
 *
 * @author cgreene
 * @since 13.4.1
 */
@SuppressWarnings("serial")
public class ShowCallReferralRequiredAisle extends PosLaneActionAdapter
{
    /**
     * Constant for Call Referral Required resource ID
     */
    public static final String CALL_REFERRAL_REQUIRED_RESOURCE_ID = "CallReferralRequired";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // get the response and store the token and tender type.
        AuthorizationCargo cargo = (AuthorizationCargo)bus.getCargo();
        AuthorizeTransferResponseIfc response = null;
        if(cargo.getResponseList().size() > 0)
        {
            response = cargo.getResponseList().get(cargo.getResponseList().size() - 1);
        }
        if(response != null)
        {
            cargo.getCallReferralData().setAccountNumberToken(response.getAccountNumberToken());
            cargo.getCallReferralData().setTenderType(response.getTenderType());
            cargo.getCallReferralData().setMaskedAccountNumber(response.getMaskedAccountNumber());
            cargo.getCallReferralData().setTenderSubType(response.getTenderSubType());
            cargo.getCallReferralData().setTraceNumber(response.getTraceNumber());
            cargo.getCallReferralData().setEntryMethod(response.getEntryMethod());
            if(TenderType.GIFT_CARD.equals(response.getTenderType()))
            {
                cargo.getCallReferralData().setGiftcardAccountNumber(response.getAccountNumber());
            }
        }

        DialogBeanModel dialogBean = new DialogBeanModel();
        dialogBean.setType(DialogScreensIfc.ERROR);
        dialogBean.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
        dialogBean.setResourceID(CALL_REFERRAL_REQUIRED_RESOURCE_ID);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogBean);
    }

}
