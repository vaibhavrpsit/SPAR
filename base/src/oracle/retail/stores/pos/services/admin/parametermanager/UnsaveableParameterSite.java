/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/UnsaveableParameterSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:05 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:39 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:34 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:25 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:50  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:35:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:40:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:05:40   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:19:26   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:12:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    This site informs the user that the attempted save
    of the parameters failed.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class UnsaveableParameterSite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
        Informs the user that the attempted save of the parameters failed. <p>
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ParameterCargo cargo = (ParameterCargo)bus.getCargo();
        String[] args = { cargo.getParameterName() };

        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("UnsaveableParam");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(args);

        // display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
