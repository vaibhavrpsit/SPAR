/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/role/roleadd/RoleAddCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:06 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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
 *    3    360Commerce 1.2         3/31/2005 4:29:47 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:55 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:58 PM  Robert Pearse   
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
 *    Rev 1.0   Apr 29 2002 15:38:04   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:54   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:21:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:12:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:13:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.role.roleadd;

import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;

//------------------------------------------------------------------------------
/**
 * This cargo holds the information necessary for the RoleAdd service.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
// ------------------------------------------------------------------------------
public class RoleAddCargo extends UserAccessCargo implements DBErrorCargoIfc
{
    /**
     * revision number supplied by Team Connection
     **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Role to add to database
     **/
    protected RoleIfc role = null;

    /**
     * list of matching Roles returned from database lookup
     **/
    protected RoleIfc[] roleList = null;

    /**
     * Role name to edit
     **/
    protected String oldRoleTitle = "";

    protected String newRoleTitle = "";

    /**
     * Role name which was edited
     **/
    protected String modifiedRoleTitle = "";

    /**
     * Role index to edit
     **/
    protected int roleIndex = 0;

    /**
     * Role selected for modification
     **/
    protected RoleIfc roleSelected = null;

    /**
     * The financial data for the store
     **/
    protected StoreStatusIfc storeStatus;

    /**
     * The register at which operations are being performed
     **/
    protected RegisterIfc register = null;

    /**
     * The result of the an interaction with the data manager
     **/
    protected int dataExceptionErrorCode = 0;

    /**
     * The user's login ID
     **/
    protected String employeeID = "";

    // ---------------------------------------------------------------------
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
     * 
     * @param none
     * @return none
     * @exception none
     **/
    // ---------------------------------------------------------------------
    public RoleAddCargo()
    {
    }

    // ---------------------------------------------------------------------
    /**
     * Get the Role object in the cargo.
     * <P>
     * 
     * @return RoleIfc reference to the Role object in the cargo
     **/
    // ---------------------------------------------------------------------
    public RoleIfc getRole()
    {
        return role;
    }

    // ---------------------------------------------------------------------
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
    // ---------------------------------------------------------------------
    public void setRole(RoleIfc role)
    {
        this.role = role;
    }

    // ---------------------------------------------------------------------
    /**
     * Get the selected role index.
     * <P>
     * 
     * @return int reference to the Role's index in the cargo
     **/
    // ---------------------------------------------------------------------
    public int getRoleSelectedIndex()
    {
        return roleIndex;
    }

    // ---------------------------------------------------------------------
    /**
     * Set the role selected index.
     * <P>
     * 
     * @param roleIndex reference to the Role's index in the cargo
     **/
    // ---------------------------------------------------------------------
    public void setRoleSelectedIndex(int roleIndex)
    {
        this.roleIndex = roleIndex;
    }

    // ---------------------------------------------------------------------
    /**
     * Get the Role which was selected for modification
     * 
     * @return RoleIfc reference to the Role object in the cargo
     **/
    // ---------------------------------------------------------------------
    public RoleIfc getRoleSelected()
    {
        return roleSelected;
    }

    // ---------------------------------------------------------------------
    /**
     * Set the Role which was selected for modification
     * 
     * @param role reference to Role object to set the cargo to reference
     **/
    // ---------------------------------------------------------------------
    public void setRoleSelected(RoleIfc role)
    {
        roleSelected = role;
    }

    // ---------------------------------------------------------------------
    /**
     * Get the old role name title.
     * <P>
     * 
     * @return String reference to the role title in the cargo
     **/
    // ---------------------------------------------------------------------
    public String getOldRoleTitle()
    {
        return oldRoleTitle;
    }

    // ---------------------------------------------------------------------
    /**
     * Set the old role name title.
     * <P>
     * 
     * @param roleTitle reference to the role title in the cargo
     **/
    // ---------------------------------------------------------------------
    public void setOldRoleTitle(String roleTitle)
    {
        oldRoleTitle = roleTitle;
    }

    // ---------------------------------------------------------------------
    /**
     * Get the modified role name title.
     * <P>
     * 
     * @return String reference to the role title in the cargo
     **/
    // ---------------------------------------------------------------------
    public String getModifiedRoleTitle()
    {
        return modifiedRoleTitle;
    }

    // ---------------------------------------------------------------------
    /**
     * Set the modified role name title.
     * <P>
     * 
     * @param roleTitle reference to the role title in the cargo
     **/
    // ---------------------------------------------------------------------
    public void setModifiedRoleTitle(String roleTitle)
    {
        modifiedRoleTitle = roleTitle;
    }

    // ---------------------------------------------------------------------
    /**
     * Get the Role list in the cargo.
     * <P>
     * 
     * @return RoleIfc[] reference to the Role list in the cargo
     **/
    // ---------------------------------------------------------------------
    public RoleIfc[] getRoleList()
    {
        return roleList;
    }

    // ---------------------------------------------------------------------
    /**
     * Set the Role list in the cargo.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>Cargo is set to reference the specified Role list
     * </UL>
     * 
     * @param roleList reference to Role list to set the cargo to reference
     **/
    // ---------------------------------------------------------------------
    public void setRoleList(RoleIfc[] roleList)
    {
        this.roleList = roleList;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the store status.
     * <P>
     * 
     * @return The store status.
     **/
    // ----------------------------------------------------------------------
    public StoreStatusIfc getStoreStatus()
    { // begin getStoreStatus()
        return storeStatus;
    } // end getStoreStatus()

    // ----------------------------------------------------------------------
    /**
     * Sets the store status.
     * <P>
     * 
     * @param value The store status.
     **/
    // ----------------------------------------------------------------------
    public void setStoreStatus(StoreStatusIfc value)
    { // begin setStoreStatus()
        storeStatus = value;
    } // end setStoreStatus()

    // ----------------------------------------------------------------------
    /**
     * Returns the register at which operations are being performed.
     * <P>
     * 
     * @return RegisterIfc object
     **/
    // ----------------------------------------------------------------------
    public RegisterIfc getRegister()
    {
        return (register);
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the register at which operations are to be performed.
     * <P>
     * 
     * @param register The register where operations are being performed.
     **/
    // ----------------------------------------------------------------------
    public void setRegister(RegisterIfc register)
    {
        this.register = register;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the error code returned with a DataException.
     * <P>
     * 
     * @return int the integer value
     **/
    // ----------------------------------------------------------------------
    public int getDataExceptionErrorCode()
    {
        return dataExceptionErrorCode;
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the error code returned with a DataException.
     * <P>
     * 
     * @param value the integer value
     **/
    // ----------------------------------------------------------------------
    public void setDataExceptionErrorCode(int value)
    {
        dataExceptionErrorCode = value;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the user's login identifier.
     * <P>
     * 
     * @return The user's login identifier.
     **/
    // ----------------------------------------------------------------------
    public String getEmployeeID()
    {
        return employeeID;
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the user's login identifier.
     * <P>
     * 
     * @param value The user's login identifier.
     **/
    // ----------------------------------------------------------------------
    public void setEmployeeID(String value)
    {
        employeeID = value;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns a string representation of this object.
     * <P>
     * 
     * @return String representation of object
     **/
    // ----------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class: RoleAddCargo (Revision " + getRevisionNumber() + ")" + hashCode());

        // pass back result
        return (strResult);
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * <P>
     * 
     * @return String representation of revision number
     **/
    // ----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    // ----------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     **/
    // ----------------------------------------------------------------------
    public static void main(String args[])
    {
        // instantiate class
        RoleAddCargo obj = new RoleAddCargo();

        // output toString()
        System.out.println(obj.toString());
    }

    // ----------------------------------------------------------------------
    /**
     * Return the new role title
     * 
     * @return newRoleTitle is returned
     */
    // ----------------------------------------------------------------------
    public String getNewRoleTitle()
    {
        return newRoleTitle;
    }

    // ----------------------------------------------------------------------
    /**
     * Set the new role title
     * 
     * @param string role name
     */
    // ----------------------------------------------------------------------
    public void setNewRoleTitle(String string)
    {
        newRoleTitle = string;
    }

}
