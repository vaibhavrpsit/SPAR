/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/role/roleadd/AddRoleNameAcceptAisle.java /main/13 2011/12/05 12:16:17 cgreene Exp $
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
 *   Revision 1.4  2004/03/03 23:15:15  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
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
 *    Rev 1.2   Jul 30 2003 18:27:56   baa
 * ignore case when checking for duplicate titles
 * Resolution for 3304: Entering duplicate role names (capitalization different) results in the role being saved multiple times
 * 
 *    Rev 1.1   Jul 03 2003 16:06:02   baa
 * remove new role from cargo when backing up from role add service
 * Resolution for 2400: Role/Security: undo button has saving role action on Set Access screen
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

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EditRoleBeanModel;

//------------------------------------------------------------------------------
/**
    This aisle is traversed when the user has entered a role name
    to add.
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class AddRoleNameAcceptAisle extends PosLaneActionAdapter
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



    //--------------------------------------------------------------------------
    /**
       This aisle is traversed after the user has entered a role name/title.
       The role name is checked for an existing duplicate. If there is
       a duplicate name error, then a "NameError" letter is mailed, in
       order for an error dialog to be displayed. If no duplicate name
       is found, then the new role name is added to the roles array.
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        Letter letter = new Letter(CommonLetterIfc.CONTINUE);
        String roleName = "";
        RoleIfc[] roles = null;

        // change Cargo type to RoleAddCargo
        RoleAddCargo cargo = (RoleAddCargo)bus.getCargo();

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // get the model for the bean
        EditRoleBeanModel model = (EditRoleBeanModel) ui.getModel(POSUIManagerIfc.ADD_ROLE);

        // get the user's locale
        Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        // get the role name from the model
        roleName = model.getRoleName();

        // get the roles from the RoleAddCargo
        roles = cargo.getRoleList();
        cargo.setNewRoleTitle(roleName);
        int oldRolesSize = 0;


        // check to see if the user entered role name already exists
        // in the database

        // if we have a list of roles from our database
        if (roles != null)
        {
            oldRolesSize = roles.length;

            // search for the user entered role title among the
            // roles list read in from the database
            for (int i = 0; i < oldRolesSize; i++)
            {
                if (roleName.equalsIgnoreCase(roles[i].getTitle(userLocale)))
                {
                    letter = new Letter(NAME_ERROR);
                    i = oldRolesSize;
                }
            } // end of for-loop
        }
        bus.mail(letter, BusIfc.CURRENT);

    }


}
