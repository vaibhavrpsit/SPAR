/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/priceadjustment/PriceAdjErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:03 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:28 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:24:19 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:13:21 PM  Robert Pearse   
 * $
 * Revision 1.6  2004/09/23 00:07:16  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.5  2004/04/27 21:31:14  jriggins
 * @scr 3979 Code review cleanup
 * Revision 1.4 2004/04/16 22:33:21 jriggins
 * @scr 3979 Added parameter information
 * 
 * Revision 1.3 2004/04/15 15:44:08 jriggins @scr 3979 Changed dialog printing
 * functionality
 * 
 * Revision 1.2 2004/03/30 23:49:17 jriggins @scr 3979 Price Adjustment feature
 * dev
 * 
 * Revision 1.1 2004/03/30 00:04:59 jriggins @scr 3979 Price Adjustment feature
 * dev
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.priceadjustment;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
 * This aisle handles displaying error dialogs for the price adjustment flows
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class PriceAdjErrorAisle extends LaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7095678896873820350L;


    //--------------------------------------------------------------------------
    /**
     * Handles displaying error dialogs for the price adjustment flows
     * 
     * @param bus
     *            the bus traversing this lane
     */
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Show the dialog. If the dialogID is not explicitly set, check the
        // dialog map
        PriceAdjustmentCargo cargo = (PriceAdjustmentCargo) bus.getCargo();

        String dialogID = cargo.getDialogID();
        if (dialogID == null)
        {
            dialogID = PriceAdjustmentUtilities.getDialogID(bus.getCurrentLetter().getName());
        }

        showDialog(bus, dialogID);
    }

    //----------------------------------------------------------------------
    /**
     * Shows an error dialog given a dialog resource ID
     * 
     * @param bus
     *            The bus
     * @param dialogID
     *            Dialog resource ID
     */
    //----------------------------------------------------------------------
    protected void showDialog(BusIfc bus, String dialogID)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(dialogID);
        model.setType(DialogScreensIfc.ERROR);

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

}
