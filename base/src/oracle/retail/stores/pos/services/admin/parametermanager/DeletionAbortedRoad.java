/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/DeletionAbortedRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:05 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:54 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:33 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:52:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:39:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:04:14   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:19:10   msg
 * Initial revision.
 * 
 *    Rev 1.0   08 Feb 2002 17:30:34   KAC
 * Initial revision.
 * Resolution for POS SCR-1176: Update "list from list" parameter editing
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.beans.ListFromListParameterBeanModel;

//------------------------------------------------------------------------------
/**
    The user has decided not to delete the parameter values.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class DeletionAbortedRoad extends PosLaneActionAdapter
{

    public static final String LANENAME = "DeletionAbortedRoad";

    //--------------------------------------------------------------------------
    /**
       The user has decided not to delete the parameter values.
           Restore the state of the bean model's parameter values.
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        ParameterCargo cargo = (ParameterCargo)bus.getCargo();
        ListFromListParameterBeanModel beanModel =
            (ListFromListParameterBeanModel)cargo.getParameter();
        beanModel.setNewValues(beanModel.getOldValues());
    }
}
