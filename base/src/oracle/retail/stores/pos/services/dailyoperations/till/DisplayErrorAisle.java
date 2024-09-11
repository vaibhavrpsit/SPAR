/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/DisplayErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:19 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:38 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/05/17 22:36:11  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Add second argument to TillAccountabilityError.
 *
 *   Revision 1.1  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.TillCargo;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
    This aisle displays error dialogs in the till reconcile service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class DisplayErrorAisle extends PosLaneActionAdapter
{
    /** revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** database error offline tag */
    public static final String DATABASE_ERROR_OFFLINE_TAG = "DatabaseError.Offline";
    /** database error offline default text */
    public static final String DATABASE_ERROR_OFFLINE_TEXT = "The database is offline.";

    //--------------------------------------------------------------------------
    /**
       Display the error dialog as specified by the error type set in the cargo.
       Issues Ok letter or Failure letter according to the tillFatalError indicator
       set in the cargo.
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        TillCargo cargo = (TillCargo) bus.getCargo();
        boolean bOk = false;
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);


        String args[] = new String[1];
        args[0] = "";

        String letters[] = new String[1];
        letters[0] = CommonLetterIfc.OK;
        if (cargo.isTillFatalError())
        {
            letters[0] = CommonLetterIfc.FAILURE;
        }

        int buttons[] = new int[1];
        buttons[0] = DialogScreensIfc.BUTTON_OK;

        switch (cargo.getErrorType())
        {
            case TillCargo.TILL_ID_INVALID_ERROR_TYPE:
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR,
                                           "TillIDError", args,
                                           buttons, letters);
                break;
            case TillCargo.TILL_NOT_FOUND_ERROR_TYPE:
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR,
                                          "TillNotFoundError", args,
                                          buttons, letters);
                break;
            case TillCargo.TILL_DATABASE_ERROR_TYPE:
                args[0] = utility.retrieveDialogText(DATABASE_ERROR_OFFLINE_TAG, 
                                                     DATABASE_ERROR_OFFLINE_TEXT);
                letters[0] = CommonLetterIfc.FAILURE;
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR,
                                          "DatabaseError", args,
                                          buttons, letters);
                break;
            case TillCargo.TILL_SUSPENDED_ERROR_TYPE:
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR,
                                           "TillSuspendedError", args,
                                           buttons, letters);
                break;
            case TillCargo.TILL_ALREADY_RECONCILED_TYPE:
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR,
                        "TillAlreadyReconciled", args,
                        buttons, letters);
                break;
            case TillCargo.TILL_ACCOUNTABILITY_ERROR_TYPE:
                args[0] = cargo.getTillID();
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR,
                        "TillAccountabilityError", cargo.getErrorScreenArgs(),
                        buttons, letters);
                break;
            case TillCargo.TILL_CASHIER_ERROR_TYPE:
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR,
                                           "TillNotAssignedtoCashierError", args,
                                           buttons, letters);
                break;
            case TillCargo.TILL_DRAWER_ERROR_TYPE:
                String argForNoCashDrawer[] = cargo.getErrorScreenArgs();
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR,
                                           "NoCashDrawersError", argForNoCashDrawer,
                                           buttons, letters);
                break;
            case TillCargo.TILL_REGISTER_CLOSED_ERROR_TYPE:
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR,
                        "TillRegisterClosedError", args,
                        buttons, letters);
                break;
            case TillCargo.TILL_NOT_SUSPENDED_ERROR_TYPE:
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR,
                        "TillNotSuspendedError", args,
                        buttons, letters);
                break;
            case TillCargo.TILL_NOT_FLOATING_ERROR_TYPE:
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR,
                        "TillNotFloatingError", args,
                        buttons, letters);
                break;
            case TillCargo.TILL_NONE_OPEN_ERROR_TYPE:
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR,
                        "TillNoOpenTillsError", args,
                        buttons, letters);
                break;
            case TillCargo.TILL_CLOSED_ERROR_TYPE:
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR,
                        "TillNotOpenError", args,
                        buttons, letters);
                break;                
        }

    }

}
