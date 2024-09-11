/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/AuthorizationDeclinedAisle.java /rgbustores_13.4x_generic_branch/8 2011/08/23 16:08:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/23/11 - rolled back ability to override declines
 *    mkutiana  08/15/11 - Replaced decline credit override message with
 *                         generic message since used with other tenders
 *    cgreene   05/27/11 - move auth response objects into domain
 *    ohorne    05/09/11 - made decline message more generic
 *    asinton   03/25/11 - Moved APF request and response objects to common
 *                         module.
 *    asinton   03/22/11 - new tender authorization service
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
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
public class AuthorizationDeclinedAisle extends PosLaneActionAdapter
{
    /** constant for declined dialog name */
    public static final String AUTH_DECLINED_DIALOG = "AuthorizationDeclined";

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
        dialogModel.setResourceID(AUTH_DECLINED_DIALOG);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(dialogArgs);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);

        // display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}