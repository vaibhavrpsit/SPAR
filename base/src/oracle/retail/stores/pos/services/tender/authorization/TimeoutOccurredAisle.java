/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/TimeoutOccurredAisle.java /rgbustores_13.4x_generic_branch/3 2011/11/01 15:43:45 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   11/01/11 - added check for no token after a timeout
 *    blarsen   09/13/11 - Preventing option for call referral when the timeout
 *                         occurs during a debit tender.
 *    cgreene   07/27/11 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This aisle shows dialog that says the auth request timed out.
 *
 * @author cgreene
 * @since 13.4
 */
@SuppressWarnings("serial")
public class TimeoutOccurredAisle extends PosLaneActionAdapter
{
    /** Resource ID for the timeout dialog message. */
    public static final String TIMEOUT_RESOURCE_ID = "AuthTimedOut";
    public static final String TIMEOUT_WITH_REFERRAL_RESOURCE_ID = "AuthTimedOutWithReferral";

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        AuthorizationCargo cargo = (AuthorizationCargo)bus.getCargo();
        AuthorizeTransferResponseIfc authResponse = cargo.getCurrentResponse();
        DialogBeanModel dialogModel = new DialogBeanModel();

        if (AuthorizationConstantsIfc.TenderType.DEBIT.equals(authResponse.getTenderType()) ||
                // not inserting ICC card can result in no token. Shouldn't go to Referral screen.
                StringUtils.isEmpty(authResponse.getAccountNumberToken()))
        {
            dialogModel.setResourceID(TIMEOUT_RESOURCE_ID);
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonActionsIfc.FAILURE);
        }
        else
        {
            dialogModel.setResourceID(TIMEOUT_WITH_REFERRAL_RESOURCE_ID);
            dialogModel.setType(DialogScreensIfc.CONFIRMATION);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.RETRY);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.REFERRAL);
        }
        // display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
