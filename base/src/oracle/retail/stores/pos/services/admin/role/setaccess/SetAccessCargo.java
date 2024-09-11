/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/role/setaccess/SetAccessCargo.java /main/14 2013/03/22 16:31:08 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  03/19/13 - Restricting access point and role access based on
 *                         operators role
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         6/12/2008 11:27:43 AM  Manas Sahu      The
 *         Catch block in SetAccessUpdatesAisle to have register ID and User
 *         login ID for Audit event. But for User Login ID we need to pass the
 *          Operator from SecurityCargo to RoleMainCargo and then to
 *         SetAccessCargo. Code reviewed by Naveen
 *    3    360Commerce 1.2         3/31/2005 4:29:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:14 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:11 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:54  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Apr 23 2003 17:00:02   baa
 * allow for modifying roles
 * Resolution for POS SCR-2194: Secruity/Role not saving changes when Setting Access
 * 
 *    Rev 1.1   Mar 26 2003 17:17:24   bwf
 * Set correct label for No.
 * Resolution for 1866: I18n Database  support
 * 
 *    Rev 1.0   Apr 29 2002 15:37:48   msg
 * Initial revision.
 * 
 *    Rev 1.2   04 Apr 2002 15:22:08   baa
 * Remove references to Rolefunction descriptor array and maximun number of role functions
 * Resolution for POS SCR-1565: Remove references to RoleFunctionIfc.Descriptor Security Service
 *
 *    Rev 1.1   Mar 18 2002 23:07:14   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:21:12   msg
 * Initial revision.
 *
 *    Rev 1.2   05 Mar 2002 16:35:58   baa
 * deprecate function description
 * Resolution for POS SCR-626: Make the list of Role functions extendible.
 *
 *    Rev 1.1   23 Jan 2002 12:44:18   baa
 * security updates
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.0   Sep 21 2001 11:12:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:13:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.role.setaccess;

import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;
import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.pos.services.common.WriteHardTotalsCargoIfc;

//------------------------------------------------------------------------------
/**
 * This is the cargo used for the Set Access service.
 * 
 * @version $Revision: /main/14 $
 **/
// ------------------------------------------------------------------------------
public class SetAccessCargo extends UserAccessCargo implements WriteHardTotalsCargoIfc, DBErrorCargoIfc
{
    /**
     * revision number supplied by Team Connection
     **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * Old role title
     **/
    protected String oldRoleTitle = "";

    /**
     * New or modified role to add to database
     **/
    protected RoleIfc selectedRole = null;

    /**
     * Sets whether a New role has been to add to database
     **/
    protected boolean newRole = false;

    /**
     * Role functios to add to database
     **/
    protected RoleFunctionIfc[] function = null;

    /**
     * Old role function access values to add to database
     **/
    protected String[] oldFunctionAccess = null;

    /**
     * New role function access values to add to database
     **/
    protected String[] newFunctionAccess = null;

    /**
     * The financial data for the store
     **/
    protected StoreStatusIfc storeStatus;

    /**
     * The register at which operations are being performed
     **/
    protected RegisterIfc register = null;

    /**
     * The user's login ID
     **/
    protected String employeeID = "";

    /**
     * The result of the an interaction with the data manager
     **/
    protected int dataExceptionErrorCode = 0;
    
    /**
     * Holds the restricted set of role functions
     */
    protected RoleFunctionIfc[] filteredRoleFunctionsForRole = null;

    /*
     * Labels and specs required for international language support
     */
    public static final String YES_TAG = "YesLabel";

    public static final String NO_TAG = "No";

    public static final String SET_ACCESS_SPEC = "SetAccessSelectSpec";

    public static final String NO_LABEL = "No";

    public static final String YES_LABEL = "Yes";

    /**
     * Constructor for the Cargo class.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     **/
    public SetAccessCargo()
    {
    }

    /**
     * Get the new role true/false value.
     * <P>
     * 
     * @return boolean reference to the boolean value if it is a new role
     **/
    public boolean getNewRole()
    {
        return newRole;
    }

    /**
     * Sets whether the role is a new one.
     * <P>
     * 
     * @param newRole the true/false value of whether it is a new role
     **/
    public void setNewRole(boolean newRole)
    {
        this.newRole = newRole;
    }

    /**
     * Get the original role name title.
     * <P>
     * 
     * @return String reference to the Role title in the cargo
     **/
    public String getOldRoleTitle()
    {
        return oldRoleTitle;
    }

    /**
     * Set the original role name title.
     * <P>
     * 
     * @param roleTitle the role title value
     **/
    public void setOldRoleTitle(String roleTitle)
    {
        oldRoleTitle = roleTitle;
    }

    /**
     * Get the Role object in the cargo.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * 
     * @return RoleIfc reference to the selected RoleIfc object
     **/
    public RoleIfc getRoleSelected()
    {
        return selectedRole;
    }

    /**
     * Set the Role object in the cargo.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>Cargo is set to reference the specified Role object
     * </UL>
     * 
     * @param role reference to Role object to set the cargo to reference
     **/
    public void setRoleSelected(RoleIfc role)
    { // Begin setRole()
        selectedRole = role;
    } // End setRole()

    /**
     * Get the role function access.
     * <P>
     * 
     * @return String[] reference to the old roleFunctionAccess values
     **/
    public String[] getOldFunctionAccess()
    {
        return oldFunctionAccess;
    }

    /**
     * Set the role function access.
     * <P>
     * 
     * @param functionAccess the function access values
     **/
    public void setOldFunctionAccess(String[] functionAccess)
    {
        // copy the String array of function Access values obtained from the
        // site into this cargo's String Access array
        oldFunctionAccess = new String[functionAccess.length];

        System.arraycopy(functionAccess, 0, oldFunctionAccess, 0, functionAccess.length);
    }

    /**
     * Get the new role function access.
     * <P>
     * 
     * @return String[] reference to the new roleFunctionAccess values
     **/
    public String[] getNewFunctionAccess()
    {
        return newFunctionAccess;
    }

    
    
    /**
     * Set the new role function access based on the selected roles funtions
     * <P>
     * 
     * @param utility reference to UtilityManager
     **/
    public void setNewFunctionAccess(String[] functionAccess, UtilityManagerIfc utility)
    {
        // also tie the function Access values for the selected role
        function = selectedRole.getFunctions();
        setNewFunctionAccess(functionAccess, utility, function);
    }
    
    
    /**
     * Set the new role function access based on the provided functions
     * <P>
     * 
     * @param functionAccess the function access values
     * @param utility reference to UtilityManager
     **/
    public void setNewFunctionAccess(String[] functionAccess, UtilityManagerIfc utility, RoleFunctionIfc[] function)
    {
        // copy the String array of function Access values obtained from the
        // site into this cargo's String Access array
        newFunctionAccess = new String[functionAccess.length];

        System.arraycopy(functionAccess, 0, newFunctionAccess, 0, functionAccess.length);
        String yesLabelText = utility.retrieveText(SET_ACCESS_SPEC, BundleConstantsIfc.ROLE_BUNDLE_NAME, YES_TAG, YES_LABEL);

        // also tie the function Access values for the selected role
        for (int i = 0; i < function.length; i++)
        {
            int id = function[i].getFunctionID();
            if (newFunctionAccess[i].equals(yesLabelText))
            {
                selectedRole.setFunctionAccess(id, true);
            }
            else
            {
                selectedRole.setFunctionAccess(id, false);
            }
        }
    }

    /**
     * Returns the store status.
     * <P>
     * 
     * @return The store status.
     **/
    public StoreStatusIfc getStoreStatus()
    { // begin getStoreStatus()
        return storeStatus;
    } // end getStoreStatus()

    /**
     * Sets the store status.
     * <P>
     * 
     * @param value The store status.
     **/
    public void setStoreStatus(StoreStatusIfc value)
    { // begin setStoreStatus()
        storeStatus = value;
    } // end setStoreStatus()

    /**
     * Returns the register at which operations are being performed.
     * <P>
     * 
     * @return RegisterIfc object
     **/
    public RegisterIfc getRegister()
    {
        return (register);
    }

    /**
     * Sets the register at which operations are to be performed.
     * <P>
     * 
     * @param register the register where operations are being performed
     **/
    public void setRegister(RegisterIfc register)
    {
        this.register = register;
    }

    /**
     * Returns the user's login identifier.
     * <P>
     * 
     * @return The user's login identifier.
     **/
    public String getEmployeeID()
    {
        return employeeID;
    }

    /**
     * Sets the user's login identifier.
     * <P>
     * 
     * @param value The user's login identifier.
     **/
    public void setEmployeeID(String value)
    {
        employeeID = value;
    }

    /**
     * Returns the error code returned with a DataException.
     * 
     * @return int the integer value
     **/
    public int getDataExceptionErrorCode()
    {
        return dataExceptionErrorCode;
    }

    /**
     * Sets the error code returned with a DataException.
     * 
     * @param value the integer value
     **/
    public void setDataExceptionErrorCode(int value)
    {
        dataExceptionErrorCode = value;
    }
    
    
    /**
     * Gets the filtered RoleFunctions for the Role being edited.
     * @return the filteredRoleFunctionsForRole
     */
    public RoleFunctionIfc[] getFilteredRoleFunctionsForRole()
    {
        return filteredRoleFunctionsForRole;
    }

    /**
     * Sets the filtered RoleFunctions for the Role being edited.
     * @param filteredRoleFunctionsForRole the filteredRoleFunctionsForRole to set
     */
    public void setFilteredRoleFunctionsForRole(RoleFunctionIfc[] filteredRoleFunctionsForRole)
    {
        this.filteredRoleFunctionsForRole = filteredRoleFunctionsForRole;
    }


    /**
     * Method to default display string function.
     * <P>
     * 
     * @return String representation of object
     **/
    public String toString()
    {
        // result string
        String strResult = new String("Class: SetAccessCargo (Revision " + getRevisionNumber() + ")" + hashCode());
        // pass back result
        return (strResult);
    }

    /**
     * Retrieves the Team Connection revision number.
     * <P>
     * 
     * @return String representation of revision number
     **/
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
    
}


