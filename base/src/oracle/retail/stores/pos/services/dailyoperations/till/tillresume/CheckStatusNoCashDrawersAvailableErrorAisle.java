/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillresume/CheckStatusNoCashDrawersAvailableErrorAisle.java /main/12 2012/08/07 16:19:51 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  06/25/12 - wptg - TILL_OPEN_ERROR_TAG_LINE1 and 2 are merged in
 *                         property file
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    sgu       04/14/09 - fix hardcoded english strings
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:12 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:58 PM  Robert Pearse
 *
 *   Revision 1.2  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.1  2004/09/13 19:02:59  kll
 *   @scr 7027: fix furnished by the PepBoys initiative
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillresume;

import oracle.retail.stores.pos.services.common.TillCargoIfc;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**


    @version $Revision: /main/12 $
**/
//------------------------------------------------------------------------------

public class CheckStatusNoCashDrawersAvailableErrorAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -713833872619647163L;


    public static final String LANENAME = "CheckStatusNoCashDrawersAvailableErrorAisle";
    /**
     * no suspended till prompt tag
     */
    protected static String NO_CASH_DRAWERS_AVAILABLE_TAG = "NoCashDrawersError";
    /**
     * no suspended till prompt
     */
    protected static String NO_CASH_DRAWERS_AVAILABLE = "No Cash Drawers are available";

    //--------------------------------------------------------------------------
    /**



       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui =
          (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(NO_CASH_DRAWERS_AVAILABLE_TAG);
        model.setType(DialogScreensIfc.ERROR);
        String[] args = new String[1];
        args[0] = utility.retrieveDialogText(TillCargoIfc.TILL_OPEN_ERROR_TAG_LINE,
                TillCargoIfc.TILL_OPEN_ERROR_LINE);
        model.setArgs(args);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

}
