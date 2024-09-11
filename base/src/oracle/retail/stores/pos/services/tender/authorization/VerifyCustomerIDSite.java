/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/VerifyCustomerIDSite.java /rgbustores_13.4x_generic_branch/3 2011/05/27 17:23:06 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/11 - move auth response objects into domain
 *    asinton   03/25/11 - Moved APF request and response objects to common
 *                         module.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Displays UI asking operator to verify customer ID 
 */
@SuppressWarnings("serial")
public class VerifyCustomerIDSite extends PosSiteActionAdapter
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        AuthorizationCargo cargo = (AuthorizationCargo) bus.getCargo();
        // Get the response text for this tender
        AuthorizeTransferResponseIfc response = cargo.getCurrentResponse();
        String responseMessage = response.getResponseMessage();
        String[] dialogArgs = {responseMessage};

        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("CreditPosID");
        model.setType(DialogScreensIfc.CONFIRMATION);
        model.setArgs(dialogArgs);
        model.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.YES);
        //get manager for ui
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        // display dialog
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
    
}
