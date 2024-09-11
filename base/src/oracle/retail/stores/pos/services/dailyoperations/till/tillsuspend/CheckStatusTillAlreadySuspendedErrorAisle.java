/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillsuspend/CheckStatusTillAlreadySuspendedErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:18 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:13 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:59 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:50:09  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:03  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:38   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jul 14 2003 13:40:24   RSachdeva
 * Till Already Suspended
 * Resolution for POS SCR-3074: Entered Till Not Open message is presented when attempting to suspend a till that is already in suspend.
 * 
 *    Rev 1.0   Apr 29 2002 15:25:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:31:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:20:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:15:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillsuspend;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**


    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class CheckStatusTillAlreadySuspendedErrorAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8319988869493038984L;


    public static final String LANENAME = "CheckStatusTillAlreadySuspendedErrorAisle";

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
        //Till Already Suspended
        model.setResourceID("TillAlreadySuspended");
        model.setType(DialogScreensIfc.ERROR);
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
