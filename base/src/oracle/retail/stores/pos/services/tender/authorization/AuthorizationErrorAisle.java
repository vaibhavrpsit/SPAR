/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/AuthorizationErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/08/09 07:01:17 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     08/06/11 - Added to provide a better error message than
 *                         “Unknown Error” for authorization errors that have
 *                         no specific error handling.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This aisle shows a generic authorization error dialog.
 * 
 * @author jswan
 * @since 13.4
 */
@SuppressWarnings("serial")
public class AuthorizationErrorAisle extends PosLaneActionAdapter
{
    /** constant for canceled by customer dialog name */
    public static final String AUTHORIZATION_ERROR_DIALOG = "TenderAuthorizationError";

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        AuthorizationCargo cargo = (AuthorizationCargo)bus.getCargo();
        AuthorizeTransferResponseIfc response = cargo.getCurrentResponse();
        
        String[] dialogArgs = {getResponseCodeText(bus, response.getResponseCode())};
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(AUTHORIZATION_ERROR_DIALOG);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(dialogArgs);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonActionsIfc.FAILURE);
        // display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
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
}
