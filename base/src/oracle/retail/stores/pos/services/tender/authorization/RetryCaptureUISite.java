/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/RetryCaptureUISite.java /rgbustores_13.4x_generic_branch/1 2011/06/30 09:59:24 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/29/11 - Added to support signature capture in APF.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

// Foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc; 
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    Display dialog to retry signature capture.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class RetryCaptureUISite extends PosSiteActionAdapter
{

    /* serialVersionUID */
    private static final long serialVersionUID = 496151351473087774L;
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
}

