/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/role/roleedit/FindRoleToEditSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:06 mszekely Exp $
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
 *    4    360Commerce 1.3         1/10/2008 7:40:28 AM   Manas Sahu      Event
 *          originator changes
 *    3    360Commerce 1.2         3/31/2005 4:28:12 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:43 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:06 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:58  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:53  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:37:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:07:02   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:21:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:12:54   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:13:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.role.roleedit;

import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.FindRoleBeanModel;

//------------------------------------------------------------------------------
/**
    The user selects a Role to edit.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class FindRoleToEditSite extends PosSiteActionAdapter
{

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static final String SITENAME = "FindRoleToEditSite";

    //--------------------------------------------------------------------------
    /**
       Find a role to update.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        RoleIfc[] roleList = null;
        int roleSize = 0;
        String roleTitle = "";


        // Need to change Cargo type to RoleEditCargo
        RoleEditCargo cargo = (RoleEditCargo)bus.getCargo();

        roleList = cargo.getRoleList();
        roleSize = roleList.length;

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.
            getManager(UIManagerIfc.TYPE);

        /*
         * Setup bean model information for the UI to display
         */
        FindRoleBeanModel beanModel = new FindRoleBeanModel();

        // add the List of roles to the beanModel
        beanModel.setRoleTitles(roleList, roleSize);

        // set event originator class
        EventOriginatorInfoBean.setEventOriginator("FindroleToEditSite.arrive");
        
        // set the bean model to the FindRoleBeanModel
        ui.showScreen(POSUIManagerIfc.FIND_ROLE, beanModel);

    }

    //---------------------------------------------------------------------
    /**
       Method to default display string function. <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class: FindRoleToEditSite"
                                      + " (Revision "   + getRevisionNumber() + ")" + hashCode());

        // pass back result
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }
}
