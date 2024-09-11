/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/role/roleadd/AddRoleNameRoad.java /main/13 2011/12/05 12:16:17 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    tzgarba   11/05/08 - Merged with tip
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:10 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:32 AM  Robert Pearse   
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
 *    Rev 1.0   07 Jul 2003 12:09:26   baa
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:38:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:50   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:21:00   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:12:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:13:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.role.roleadd;
// foundation imports
import java.util.Locale;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//------------------------------------------------------------------------------
/**
    This aisle is traversed when the user has entered a role name
    to add.
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class AddRoleNameRoad extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
       class name constant
    **/
    public static final String LANENAME = "AddRoleNameAcceptAisle";

    /**
       letter name constant
    **/
    public static final String NAME_ERROR = "NameError";

    /**
       letter
    **/
    private Letter letter = null;

    //--------------------------------------------------------------------------
    /**
       This aisle is traversed after the user has entered a role name/title.
       A new role name is added to the roles array.
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        String roleName = "";
        RoleIfc[] roles = null;
        RoleIfc[] newRoleIfc = null;
        RoleIfc newRole = null;


        // change Cargo type to RoleAddCargo
        RoleAddCargo cargo = (RoleAddCargo)bus.getCargo();

        // get the role name from the model
        roleName = cargo.getNewRoleTitle();
        
        // For new roles, set the given name into all supported locales
        Locale[] supportedLocales = LocaleMap.getSupportedLocales();
        
        // get the roles from the RoleAddCargo
        roles = cargo.getRoleList();
        int oldRolesSize = 0;
        int newRolesSize = 0;

        // if we have a list of roles from our database
        if (roles != null)
        {
            oldRolesSize = roles.length;
            // Add a new Role to the existing list

            // create a new array with one more element than
            // the original array size
            newRolesSize = oldRolesSize + 1;
            
            newRoleIfc = updateRoleList(roles, newRolesSize); 


            // add the new role name to the last position in the new array
            newRole = DomainGateway.getFactory().getRoleInstance();
            newRole.getLocalizedTitles().initialize(supportedLocales, roleName);

            // add the new role name to the last position in the new array
            newRoleIfc[oldRolesSize] = (RoleIfc)newRole;

            // save the new roles list in the cargo
            cargo.setRoleList(newRoleIfc);

            // set the last index of the array
            // as being the one for the newly added role
            cargo.setRoleSelectedIndex(oldRolesSize);

            // save the new role as the one being
            // selected for editing of funtion access values
            cargo.setRoleSelected(newRoleIfc[oldRolesSize]);
        }
        else    // if NO existing roles were read from the database
        {
            // Create a new role title and add it to the existing roles array

            // add the new role name to the last position in the new array
            newRole = DomainGateway.getFactory().getRoleInstance();
            newRole.getLocalizedTitles().initialize(supportedLocales, roleName);

            // create a new RoleIfc array with a single new role
            newRolesSize = 1;
            newRoleIfc = new RoleIfc[newRolesSize];

            // save this single new role into the new roles array
            newRoleIfc[0] = (RoleIfc)newRole;

            // save the new roles list in the cargo
            cargo.setRoleList(newRoleIfc);

            // set the beginning index of the array
            // as being the one for the newly added role
            cargo.setRoleSelectedIndex(0);

            // save the new role as the one being
            // selected for editing of funtion access values
            cargo.setRoleSelected(newRoleIfc[0]);
        }
    }


    /* 
     * This method is executed when escaping from the set access role during role add.
     * The new role is removed from the list of roles.
     * @see oracle.retail.stores.foundation.tour.ifc.LaneActionIfc#backup(oracle.retail.stores.foundation.tour.ifc.BusIfc, oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc)
     */
    public void backup(BusIfc bus, SnapshotIfc snapshot)
    {
        // change Cargo type to RoleAddCargo
        RoleAddCargo cargo = (RoleAddCargo)bus.getCargo();
        
        // Get current role list and remove one
        RoleIfc[] oldRolesList = cargo.getRoleList();
        RoleIfc[] newRolesList = null;
        int newListSize = oldRolesList.length -1;
        
        if (newListSize > 0)
        {
            newRolesList = updateRoleList(oldRolesList, newListSize) ; 
        }
         
        //  update roles list in the cargo
        cargo.setRoleList(newRolesList);
    }
    
    /**
     * Update the list of role names 
     * @param oldList previous list of roles
     * @param newSize size of new list of roles
     * @return RoleIfc[]  returns the new list of roles
     */
    protected RoleIfc[] updateRoleList(RoleIfc[] oldList, int newSize)
    {
        RoleIfc[] newList = new RoleIfc[newSize];
        int oldListSize = oldList.length;
        
        int rolesToCopy = oldListSize;
        if ( oldListSize > newSize)
        {
            //if reducing the list copy all but the last one
            rolesToCopy = oldListSize - 1;
        }
        // copy contents from the old array into the new
        System.arraycopy(oldList, 0, newList, 0, rolesToCopy);
   
        return newList;
    }
}
