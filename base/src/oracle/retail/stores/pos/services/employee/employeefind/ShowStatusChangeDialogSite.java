/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/ShowStatusChangeDialogSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:04 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:19 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:14 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:47  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:59:24   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jul 22 2003 17:30:58   DCobb
 * Initial revision.
 * Resolution for POS SCR-3217: Unable to change employee status from active to inactive
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeefind;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**

        The ShowStatusChangeDialogSite site presents a confirmation
        dialog to allow the user to continue or not with a status change.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class ShowStatusChangeDialogSite extends PosSiteActionAdapter
{

    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**

       The ShowStatusChangeDialogSite site presents a confirmation
       dialog to allow the user to continue or not with a status change.

       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        // Using "generic dialog bean".
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("ConfirmStatus");
        model.setType(DialogScreensIfc.CONFIRMATION);

        // set and display the model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
