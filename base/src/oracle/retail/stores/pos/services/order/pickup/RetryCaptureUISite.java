/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/pickup/RetryCaptureUISite.java /rgbustores_13.4x_generic_branch/1 2011/07/08 13:49:37 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   07/08/11 - Moved from tenderauth service which was deleted as
 *                         part of Advance Payment Foundation project.
 * 
 * 
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.pickup;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
    Display dialog to retry signature capture.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
@SuppressWarnings("serial")
public class RetryCaptureUISite extends PosSiteActionAdapter
{

    public static final String SITENAME = "RetryCaptureSite";

    //--------------------------------------------------------------------------
    /**

       <p>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        //display the dialog
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("SignatureRetry");
        model.setType(DialogScreensIfc.CONFIRMATION);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

    }

    //--------------------------------------------------------------------------
    /**
       Displays the signature capture screen if retry requested.

       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void depart(BusIfc bus)
    {

        if (bus.getCurrentLetter().getName().equals("Yes"))
        {
            //re-display the capture prompt
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.SIGNATURE_CAPTURE, new POSBaseBeanModel());
        }

    }
}

