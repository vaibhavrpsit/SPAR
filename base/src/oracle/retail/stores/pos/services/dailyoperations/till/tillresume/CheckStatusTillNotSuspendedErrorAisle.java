/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillresume/CheckStatusTillNotSuspendedErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:21 mszekely Exp $
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
 *   Revision 1.5  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/05/12 13:08:25  tmorris
 *   @scr 4686 -Till error message was changed.
 *
 *   Revision 1.3  2004/02/12 16:50:07  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:18  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Apr 17 2003 12:49:16   bwf
 * Use correct verbiage for Till Not Suspended.
 * Resolution for 2087: Till Resume missing dialog screen
 * 
 *    Rev 1.0   Apr 29 2002 15:25:50   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:30:54   msg
 * Initial revision.
 * 
 *    Rev 1.2   Mar 10 2002 18:00:18   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.1   30 Jan 2002 17:32:42   epd
 * Updated to use screen as defined in requirements
 * Resolution for POS SCR-949: Till Options - user attempts to resume a till that is not suspended
 *
 *    Rev 1.0   Sep 21 2001 11:20:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:15:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillresume;

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

public class CheckStatusTillNotSuspendedErrorAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3070174817332612911L;


    public static final String LANENAME = "CheckStatusTillNotSuspendedErrorAisle";
    /**
     * no suspended till prompt tag
     */
    protected static String NO_SUSPENDED_TILL_TAG = "TillNotSuspendedError";
    /**
     * no suspended till prompt
     */
    protected static String NO_SUSPENDED_TILL = "No Till is suspended";

    //--------------------------------------------------------------------------
    /**



       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui =
          (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(NO_SUSPENDED_TILL_TAG);
        model.setType(DialogScreensIfc.ERROR);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

}
