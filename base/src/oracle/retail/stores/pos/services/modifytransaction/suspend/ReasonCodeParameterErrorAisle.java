/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/suspend/ReasonCodeParameterErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:31 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:34 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:32 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:34 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:16  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:47  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:46   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:15:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:39:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:31:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.suspend;

// Foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Aisle to traverse if there is a parameter error preventing
    transaction suspension.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ReasonCodeParameterErrorAisle extends PosLaneActionAdapter
{                                       // begin class ReasonCodeParameterErrorAisle
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Display an error message, wait for user acknowlegement. <P>
       @param bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        // get ui handle
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // set bean model
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("SuspendParameterError");
        model.setType(DialogScreensIfc.ERROR);

        // display dialog
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

    }

}                                       // end class ReasonCodeParameterErrorAisle
