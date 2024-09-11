/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillopen/TillOpenInTrainingModeErrorAisle.java /main/11 2012/08/27 11:22:55 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  08/17/12 - wptg - removed placeholder from key
 *                         StoreRegisterTillTrainingModeOpenError
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:13 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:06 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/04/07 17:50:56  tfritz
 *   @scr 3884 - Training Mode rework
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillopen;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This aisle displays the till cannot open in training mode error dialog
    $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class TillOpenInTrainingModeErrorAisle extends LaneActionAdapter
{
    /**
     revision number of this class
     **/
    public static String revisionNumber = "$Revision: /main/11 $";


    //--------------------------------------------------------------------------
    /**
     Displays error message.<P>
     @param bus the bus traversing this site
     **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // get ui handle
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        // set bean model
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("StoreRegisterTillTrainingModeOpenError");
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Cancel");
        model.setType(DialogScreensIfc.ERROR);

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    //----------------------------------------------------------------------
    /**
     Returns the revision number of the class. <P>
     @return String representation of revision number
     **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
