/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpayin/UpdateStatusErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:17 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:26 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:16  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:50:03  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:48:04  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:27:00   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:30:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 10 2002 18:00:16   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Sep 21 2001 11:19:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpayin;

// Foundation imports
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
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class UpdateStatusErrorAisle extends PosLaneActionAdapter
                                    implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -6913818749093562241L;


    public static final String LANENAME = "UpdateStatusErrorAisle";
    /**
     * pay-in text tag
     */
    protected static String PAY_IN_TAG = "TillUpdateDatabaseError.PayIn";
    /**
     * pay-in text
     */
    protected static String PAY_IN = "pay-in";

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

        String args[] = new String[1];
        args[0] = utility.retrieveDialogText(PAY_IN_TAG, PAY_IN);

        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("TillUpdateDatabaseError");
        model.setType(DialogScreensIfc.ERROR);
        model.setArgs(args);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

}
