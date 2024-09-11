/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/POSErrorSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
 *  Site that displays a register error.
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 *  @deprecated as of release 7.0 The complete pos service was replaced by the sale service under _360commerce
 */
//------------------------------------------------------------------------------

public class POSErrorSite extends PosSiteActionAdapter
{
    /** PVCS revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //--------------------------------------------------------------------------
    /**
     *               
     *  @param bus the bus arriving at this site
     */
    public void arrive(BusIfc bus)
    {
        // get the ui
        POSUIManagerIfc ui = 
            (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // create the dialog model
        DialogBeanModel model = new DialogBeanModel();

        // set the dialog name and show the dialog
        model.setResourceID("StoreCloseRegisterStatusInquiryError");
        model.setType(DialogScreensIfc.ERROR);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);   
    }

}
