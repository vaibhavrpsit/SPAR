/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillopen/TillAlreadyOpenErrorAisle.java /main/1 2014/06/25 15:23:30 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     06/24/14 - Forward port fix to prevent 2 or more online
 *                         registers from opening the same till when opening
 *                         simultaneously.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillopen;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    @version $Revision: /main/1 $
**/
//------------------------------------------------------------------------------
/**
 * 
 * Handles the case of a till already open which can occur if two or more registers attempt to
 * open the same till ID simultaneously/concurrently.  
 *
 *@since 14.1
 */
public class TillAlreadyOpenErrorAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 2434575715257486457L;

    public static final String LANENAME = "TillAlreadyOpenErrorAisle";

    //--------------------------------------------------------------------------
    /**
      @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("TillAlreadyOpenError");
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.ENTER_TILL);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    //--------------------------------------------------------------------------
    /**
      @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void backup(BusIfc bus)
    {

    }

}
