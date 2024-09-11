/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/StoreParamGroupAisle.java /main/11 2012/01/26 14:13:54 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  01/26/12 - XbranchMerge mjwallac_forward_port_bug_13603964 from
 *                         rgbustores_13.4x_generic_branch
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:13 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:30 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:52:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   30 Jun 2003 23:24:54   baa
 * check group access
 * 
 *    Rev 1.0   Apr 29 2002 15:40:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:05:30   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:19:22   msg
 * Initial revision.
 * 
 *    Rev 1.2   30 Jan 2002 09:12:24   KAC
 * Commented out a println().
 * Resolution for POS SCR-372: Modify Parameter UI for register level editing
 * 
 *    Rev 1.1   Jan 19 2002 10:28:08   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:12:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

/**
 * Store the parameter group as entered at the UI.
 * 
 * @version $Revision: /main/11 $
 */
public class StoreParamGroupAisle extends LaneActionAdapter
{
    private static final long serialVersionUID = -3444050138945856737L;
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * Stores the parameter group as entered at the UI.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DataInputBeanModel model = (DataInputBeanModel) ui.getModel(POSUIManagerIfc.PARAM_SELECT_GROUP);

        String val = "";
        if (model != null)
        {
            val = (String)model.getSelectionValue("choiceList");
            ParameterCargo cargo = (ParameterCargo)bus.getCargo();
            cargo.setParameterGroup(val);
            // Check access for parameters group
            cargo.setAccessFunctionID(cargo.getRoleFunctionForGroup());
	        bus.mail(new Letter("CheckAccess"), BusIfc.CURRENT);
        }
        else
        {
            // stay on the same site if none of the
            // parameter group is selected
            bus.mail(new Letter("Cancel"), BusIfc.CURRENT);
        }
    }    
}
