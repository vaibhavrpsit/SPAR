/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/role/roleadd/AddRoleNameSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:06 mszekely Exp $
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
 *    4    360Commerce 1.3         1/10/2008 7:40:02 AM   Manas Sahu      Event
 *          originator changes
 *    3    360Commerce 1.2         3/31/2005 4:27:10 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:33 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:25 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:57  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:37:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jul 03 2003 16:06:28   baa
 * remove role name from cargo on backup
 * Resolution for 2400: Role/Security: undo button has saving role action on Set Access screen
 * 
 *    Rev 1.0   Apr 29 2002 15:38:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:52   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:21:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:12:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:13:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.role.roleadd;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EditRoleBeanModel;

//------------------------------------------------------------------------------
/**
    Displays a text field for adding the role name.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class AddRoleNameSite extends PosSiteActionAdapter
{

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static final String SITENAME = "AddRoleNameSite";

    //--------------------------------------------------------------------------
    /**
       Add the role name in the text field.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Need to change Cargo type to RoleAddCargo
        RoleAddCargo cargo = (RoleAddCargo)bus.getCargo();

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.
            getManager(UIManagerIfc.TYPE);

        /*
         * Setup bean model information for the UI to display
         */
        EditRoleBeanModel beanModel = new EditRoleBeanModel();
        beanModel.setRoleName(cargo.getNewRoleTitle());
        
        // set event originator class
        EventOriginatorInfoBean.setEventOriginator("AddRoleNameSite.arrive");

        // display the Add Role form on the screen
        ui.showScreen(POSUIManagerIfc.ADD_ROLE, beanModel);

    }

}
