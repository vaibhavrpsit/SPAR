/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/EditParamMultilineStringSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:06 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:52:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:39:18   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:04:26   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:19:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   15 Feb 2002 08:46:52   KAC
 * Removed depart method.  Value harvesting should not happen
 * here, especially when the user hits esc/undo.
 * Resolution for POS SCR-1291: Selecting Esc or Cancel crashes/hangs application on Multiple Line Editor screen
 * 
 *    Rev 1.0   31 Jan 2002 13:46:20   KAC
 * Initial revision.
 * Resolution for POS SCR-672: Create List Parameter Editor
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;


import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.RetailParameter;

//------------------------------------------------------------------------------
/**
    Edit a parameter whose value can span several lines.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class EditParamMultilineStringSite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";


    //--------------------------------------------------------------------------
    /**
        Sets up the UI to edit a parameter whose value can span several lines.
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        ParameterCargo cargo = (ParameterCargo)bus.getCargo();
        RetailParameter beanModel = cargo.getParameter();

        // display the UI screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.PARAM_EDIT_MULTILINE_STRING, beanModel);
    }


}
