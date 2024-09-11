/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/LookupEmployeeMatchNotFoundAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   02/04/09 - updated UIManager call to showScreen
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:20 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:28 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:59:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:23:50   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:32:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:23:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeefind;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * The LookupEmployeeMatchNotFound Aisle is traversed when the entered search
 * information does not result in a match. It displays an error message.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class LookupEmployeeMatchNotFoundAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 7581501774527281526L;

    public static final String LANENAME = "LookupEmployeeMatchNotFoundAisle";

    /**
     * The LookupEmployeeMatchNotFound Aisle is traversed when the entered
     * search information does not result in a match. It displays an error
     * message.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // display the error dialog
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("INFO_NOT_FOUND_ERROR");
        model.setType(DialogScreensIfc.ERROR);

        ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
    }
}