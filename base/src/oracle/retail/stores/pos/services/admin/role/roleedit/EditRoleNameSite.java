/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/role/roleedit/EditRoleNameSite.java /main/13 2011/12/05 12:16:17 cgreene Exp $
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
 *    Rev 1.2   Jan 12 2004 12:59:18   baa
 * check for empty role name
 * 
 *    Rev 1.1   Jan 07 2004 10:35:48   baa
 * Fix flow so that Role name changes are remove when undo is selected
 * Resolution for 3523: Role name changes/edits are saved even when Undo is selected.
 * 
 *    Rev 1.0   Aug 29 2003 15:53:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:37:54   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:07:00   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:21:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:12:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:13:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.role.roleedit;

import java.util.Locale;

import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EditRoleBeanModel;

//------------------------------------------------------------------------------
/**
    Displays a text field for editing the role name.
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class EditRoleNameSite extends PosSiteActionAdapter
{

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    public static final String SITENAME = "EditRoleNameSite";

    //--------------------------------------------------------------------------
    /**
       Edit the role selected
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        RoleIfc[] roleList = null;
        String roleTitle = "";


        // Need to change Cargo type to RoleEditCargo
        RoleEditCargo cargo = (RoleEditCargo)bus.getCargo();

        // get the RoleIfc array containing all the roles
        roleList = cargo.getRoleList();
        
        // get the user's locale
        Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);        

        // get the title of the role selected by the user for editting
        roleTitle = roleList[cargo.getRoleSelectedIndex()].getTitle(userLocale);

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.
            getManager(UIManagerIfc.TYPE);

        /*
         * Setup bean model information for the UI to display
         */
        EditRoleBeanModel beanModel = new EditRoleBeanModel();

        // add the one selected role to the beanModel
        beanModel.setRoleName(roleTitle);

        // set the bean model to the FindRoleBeanModel
        ui.showScreen(POSUIManagerIfc.EDIT_ROLE, beanModel);

    }

    /* 
       * This method is executed when escaping from the edit role during role find.
       * The modified role is removed from the list of roles.
       */
      public void undo(BusIfc bus)
      {
          // change Cargo type to RoleAddCargo
          RoleEditCargo cargo = (RoleEditCargo)bus.getCargo();
              // get the roles from the RoleEditCargo
             RoleIfc[] roles = cargo.getRoleList();

             // get the user's locale
             Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);        
             
             RoleIfc roleSelected = roles[cargo.getRoleSelectedIndex()];
             String editedRoleName = roleSelected .getTitle(userLocale);
             String oldRoleName = cargo.getOldRoleTitle();
            
             if (!Util.isEmpty(oldRoleName) && !editedRoleName.equals(oldRoleName))
             {
                 cargo.setOldRoleTitle(oldRoleName);
                 roles[cargo.getRoleSelectedIndex()].setTitle(userLocale, oldRoleName);
                 cargo.setRoleList(roles);
             }
             //arrive(bus);
      }
 
}
