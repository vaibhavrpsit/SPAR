/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/EditParamListFromListSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:05 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
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
 *    Rev 1.0   Apr 29 2002 15:39:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:04:24   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:19:16   msg
 * Initial revision.
 * 
 *    Rev 1.2   19 Feb 2002 11:02:34   KAC
 * Removed depart method.  Value storage now occurs in
 * StoreNewListFromListValuesAisle.
 * Resolution for POS SCR-1312: Selecting Escape after selecting Delete on Build a List from a List crashes application
 * 
 *    Rev 1.1   08 Feb 2002 17:36:34   KAC
 * Now sets oldValues on arrival.
 * Resolution for POS SCR-1176: Update "list from list" parameter editing
 * 
 *    Rev 1.0   30 Jan 2002 10:05:10   KAC
 * Initial revision.
 * Resolution for POS SCR-672: Create List Parameter Editor
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;


import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListFromListParameterBeanModel;

//------------------------------------------------------------------------------
/**
    Edit a parameter whose value can be several of a discrete set of values.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class EditParamListFromListSite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";


    //--------------------------------------------------------------------------
    /**
        Sets up the UI to edit a parameter whose value can be several
        of a discrete set of values. <p>
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        ParameterCargo cargo = (ParameterCargo)bus.getCargo();
        ListFromListParameterBeanModel beanModel =
            (ListFromListParameterBeanModel)cargo.getParameter();

        // Save the current values as the "old" values
        beanModel.setOldValues(beanModel.getNewValues());

        // display the UI screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.PARAM_EDIT_LIST_FROM_LIST, beanModel);
    }

}
