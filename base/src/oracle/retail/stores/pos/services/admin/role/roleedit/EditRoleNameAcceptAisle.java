/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/role/roleedit/EditRoleNameAcceptAisle.java /main/13 2011/12/05 12:16:17 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:13 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:12  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
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
 *    Rev 1.2   Jan 12 2004 12:59:22   baa
 * check for empty role name
 * 
 *    Rev 1.1   Jan 07 2004 10:35:44   baa
 * Fix flow so that Role name changes are remove when undo is selected
 * Resolution for 3523: Role name changes/edits are saved even when Undo is selected.
 * 
 *    Rev 1.0   Aug 29 2003 15:53:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:37:54   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:58   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:21:04   msg
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

import java.util.Locale;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EditRoleBeanModel;

//------------------------------------------------------------------------------
/**
    This aisle is traversed once the user has edited an existing
    role name. The edited title is checked to look for an existing
    duplicate name. If a duplicate exists, then we mail a "NameError"
    letter. Else, we save the new name and proceed.
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class EditRoleNameAcceptAisle extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
       class name constant
    **/
    public static final String LANENAME = "EditRoleNameAcceptAisle";

    /**
       letter name constant
    **/
    public static final String NAME_ERROR = "NameError";

    //--------------------------------------------------------------------------
    /**
       This aisle is traversed when the user has edited a role
       and pressed the Accept button.

       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        EditRoleBeanModel beanModel = null;
        String roleNameEdited = "";
        

        boolean roleExists = false;
        int roleEditedIndex = -1;
        String oldRoleName = "";


        // change Cargo type to RoleEditCargo
        RoleEditCargo cargo = (RoleEditCargo)bus.getCargo();

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // get the model for the bean
        beanModel = (EditRoleBeanModel) ui.getModel(POSUIManagerIfc.EDIT_ROLE);

        // get the edited role name
        roleNameEdited = beanModel.getRoleName();
        
        // get the user's locale
        Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        // get the roles from the RoleEditCargo
        RoleIfc[] roles = cargo.getRoleList();

        // get the index for the role that was edited
        roleEditedIndex = cargo.getRoleSelectedIndex();
        RoleIfc roleSelected = roles[roleEditedIndex];

        oldRoleName = roles[roleEditedIndex].getTitle(userLocale);
        if (!Util.isEmpty(oldRoleName) && !roleNameEdited.equals(oldRoleName))
        {
            cargo.setOldRoleTitle(oldRoleName);
        }
        
        cargo.setRoleSelected(roleSelected);

        // compare the edited name with existing names in the roles array
        // if we have a list of roles from our database
        int rolesSize = 0;
        if (roles != null)
        {
            rolesSize = roles.length;

            // search for the user entered role title amongst the
            // roles list read in from the database
            for (int i = 0; i < rolesSize; i++)
            {
                if (i == roleEditedIndex)
                {
                    // skip the same role name in the array
                }
                else if (roleNameEdited.equals(roles[i].getTitle(userLocale)))
                {
                    // duplicate name found elsewhere in the array
                    roleExists = true;
                    bus.mail(new Letter(NAME_ERROR), BusIfc.CURRENT);
                }
            } // end of for-loop

            // if the role name entered does not exist amongst the
            // roles list previously read in, then replace the old
            // role title with this new title for the correct index
            // in the array.
            if (!roleExists)
            {
                roles[roleEditedIndex].setTitle(userLocale, roleNameEdited);
                

                bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
            }
        }
    }


}

