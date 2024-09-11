/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/EmployeeInvalidScanAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:51 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:27:57 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:21:19 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:10:49 PM  Robert Pearse   
 *
 *Revision 1.3  2004/02/12 16:49:08  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 20:56:28  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 24 2003 16:34:46   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

// foundation imports
import oracle.retail.stores.domain.arts.DataManagerMsgIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This aisle is traversed when the employee ID entered
    is invalid. It displays the ERROR screen.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class EmployeeInvalidScanAisle extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Displays the Invalid Scan error screen.
       <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        /*
         * Ask the UI Manager to display the error message
         */
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        String msg[]           = new String[3];

        msg[0] = "";
        msg[1] = DataManagerMsgIfc.NO_DATA_VALID_ID;
        msg[2] = "";
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("InvalidAssocId");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(msg);

        // display dialog
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

    }
}
