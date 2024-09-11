/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmployeeLookupRoleBeanModel.java /main/14 2011/12/05 12:16:23 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:19 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:50 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Jan 26 2004 16:03:04   jriggins
 * Initial revision.
 * Resolution for 3597: Employee 7.0 Updates
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Locale;

import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;

//----------------------------------------------------------------------------
/**
    This is model for the Looking up the employee by name.
    @version $Revison:  $
**/
//----------------------------------------------------------------------------
public class EmployeeLookupRoleBeanModel extends POSBaseBeanModel
{
    /**
        Revision number
    */
    public static String revisionNumber = "$Revision: /main/14 $";
    /**
     * List of RoleIfc objects
     */
    private RoleIfc[] roles = null;
    
    private int selectedRoleIndex = 0;
    
    /**
     * List of role titles which correspond to the roles member. These values 
     * will be supplied to the parent model for display in the UI.
     *
     */
    private String[] roleTitles = null;
    
    //--------------------------------------------------------------------------
    /**
     *  Default constructor.
     */
    public EmployeeLookupRoleBeanModel()
    {
        super();
    }
    
    //----------------------------------------------------------------------------
    /**
        Get the list of roles
        @return the list of roles
    **/
    //----------------------------------------------------------------------------
    public RoleIfc[] getRoles()
    {
        return roles;
    }
    //----------------------------------------------------------------------------
    /**
     Get the role from the list at the selected index or null if index 
     is invalid
     @return role from the list at the selected index or null if index is 
     invalid
     **/
    //----------------------------------------------------------------------------
    public RoleIfc getRole(int index)
    {        
        if (roles == null || index < 0 || index >= roles.length)
            return null;
        else
            return roles[index];
    }    
    //----------------------------------------------------------------------------
    /**
        Sets the list of roles as well as the list for the parent ListBeanModel
        @param roles the list of roles
    **/
    //----------------------------------------------------------------------------
    public void setRoles(RoleIfc[] roles)
    {
        // Set both the list of RoleIfc objects and its corresponding list of 
        // role titles
        this.roles = roles;
        
        // get the user's locale
        Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        
        if (this.roles != null)
        {
            roleTitles = new String[this.roles.length];
            
            for (int i = 0; i < this.roles.length; i++)
            {
                if (this.roles[i] == null)
                    roleTitles[i] = "";
                else 
                    roleTitles[i] = this.roles[i].getTitle(userLocale);
            }
        }
    }
    
    public void setSelectedRoleIndex(int index)
    {
        selectedRoleIndex = index;
    }
    
    public int getSelectedRoleIndex()
    {
        return selectedRoleIndex;
    }
    
    public RoleIfc getSelectedRole()
    {
        return getRole(getSelectedRoleIndex());
    }

    public String[] getRoleTitles()
    {
        return roleTitles;
    }
    
//    //----------------------------------------------------------------------------
//    /**
//        Get the role from the list at the selected index or null if index 
//        is invalid
//        @return role from the list at the selected index or null if index is 
//        invalid
//     **/
//    //----------------------------------------------------------------------------
//    public RoleIfc getSelectedRole()
//    {
//        int index = getListModel().getSelectedIndex();
//        if (roles == null || index < 0 || index >= roles.length)
//            return null;
//        else
//            return roles[index];
//    }    
//    //----------------------------------------------------------------------------
//    /**
//        Sets the list of roles as well as the list for the parent ListBeanModel
//        @param roles the list of roles
//    **/
//    //----------------------------------------------------------------------------
//    public void setRoles(RoleIfc[] roles)
//    {
//        // Set both the list of RoleIfc objects and its corresponding list of 
//        // role titles
//        this.roles = roles;
//        
//        if (this.roles != null)
//        {
//            roleTitles = new String[this.roles.length];
//            
//            for (int i = 0; i < this.roles.length; i++)
//            {
//                if (this.roles[i] == null)
//                    roleTitles[i] = "";
//                else 
//                    roleTitles[i] = this.roles[i].getTitle();
//            }
//        }
//        
//        // Set the list of titles into the parent model}
//        setListModel(this.roles);
//    }
    //--------------------------------------------------------------------------
//TODO remove getSelectedValue if not needed
//    /**
//     *  Overloaded from ListBeanModel.  Gets the role title that corresponds to the selected row.
//     *  @return the selected value (role title), or null if nothing is selected
//     */
//    public Object getSelectedValue()
//    {
//        return ((RoleIfc)selectedValue).getTitle();
//    }    
    //----------------------------------------------------------------------------
    /**
        Converts to a string representing the data in this Object
        @returns string representing the data in this Object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: EmployeeLookupRoleBeanModel Revision: " + revisionNumber + "\n");
        buff.append("Roles:\n"); 
        if (roles == null)
            buff.append("no roles");
        else
        {    
            buff.append("[");
            for (int x = 0; x < roles.length; x++)
            {
                buff.append(roles[x]).append("\n");
            }
            buff.append("]");
        }

        return(buff.toString());
    }
}
