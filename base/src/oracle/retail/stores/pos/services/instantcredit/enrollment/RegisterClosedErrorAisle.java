/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/RegisterClosedErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:15 mszekely Exp $
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
 *  3    360Commerce 1.2         3/31/2005 4:29:37 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:24:38 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:13:38 PM  Robert Pearse   
 * $
 * Revision 1.3  2004/02/12 16:50:42  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:51:22  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 * updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 24 2003 19:48:12   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
 *  Displays a register closed error dialog.
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------

public class RegisterClosedErrorAisle extends PosLaneActionAdapter
{
    /** PVCS revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** aisle name constant */
    public static final String LANENAME = "RegisterClosedErrorAisle";

    /** dialog resource id constant */
    public static final String RESOURCE_ID = "TillRegisterClosedError";

    //--------------------------------------------------------------------------
    /**
     *     Displays the register closed error.
     *    @param bus the bus traversing this lane
     */
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the error dialog
        UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, RESOURCE_ID);
    }
}
