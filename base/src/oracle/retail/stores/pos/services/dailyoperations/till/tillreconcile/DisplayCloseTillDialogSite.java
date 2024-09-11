/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/DisplayCloseTillDialogSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:20 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *3    360Commerce 1.2         3/31/2005 4:27:47 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:21:02 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:10:38 PM  Robert Pearse   
 *
 Revision 1.1  2004/04/15 18:57:00  dcobb
 @scr 4205 Feature Enhancement: Till Options
 Till reconcile service is now separate from till close.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
 This aisle displays error dialogs in the till resume service. <P>
 @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//------------------------------------------------------------------------------

public class DisplayCloseTillDialogSite extends PosSiteActionAdapter
{
    /** revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** Close Till Confirm dialog spec name */
    public static final String CLOSE_TILL_CONFIRM = "CloseTillConfirm";

    //--------------------------------------------------------------------------
    /**
        Display the Close Till Confirm dialog.
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        // set and display the dialog model
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(CLOSE_TILL_CONFIRM);
        model.setType(DialogScreensIfc.CONFIRMATION);
        
        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
