/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/AuthorizationCanceledByCustomerAisle.java /rgbustores_13.4x_generic_branch/1 2011/06/07 15:56:41 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   06/06/11 - Initial version.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This aisle shows the authorization canceled by customer dialog.
 * 
 * @author blarsen
 * @since 13.4
 */
@SuppressWarnings("serial")
public class AuthorizationCanceledByCustomerAisle extends PosLaneActionAdapter
{
    /** constant for canceled by customer dialog name */
    public static final String AUTH_CANCELED_BY_CUSTOMER = "AuthorizationCanceledByCustomer";

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        AuthorizationCargo cargo = (AuthorizationCargo)bus.getCargo();
        AuthorizeTransferResponseIfc response = cargo.getCurrentResponse();
        String[] dialogArgs = {response.getResponseMessage()};
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(AUTH_CANCELED_BY_CUSTOMER);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(dialogArgs);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonActionsIfc.FAILURE);
        // display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
