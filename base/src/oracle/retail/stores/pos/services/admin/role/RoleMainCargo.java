/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/role/RoleMainCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:06 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:47 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:57 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:58 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:56  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:54:19  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:37:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:07:10   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:56   msg
 * Initial revision.
 * 
 *    Rev 1.2   22 Jan 2002 16:02:18   baa
 * split employe/ role  role function
 * Resolution for POS SCR-714: Roles/Security 5.0 Updates
 *
 *    Rev 1.1   21 Jan 2002 17:50:00   baa
 * converting to new security model
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.0   Sep 21 2001 11:12:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:13:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.role;

import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;

/**
 * This is the cargo for the role main service.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
// ------------------------------------------------------------------------------
public class RoleMainCargo extends UserAccessCargo implements DBErrorCargoIfc, CargoIfc
{
    /**
     * revision number supplied by Team Connection
     **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * list of matching Roles returned from database lookup
     **/
    protected RoleIfc[] roleList = null;

    /**
     * Role name to edit
     **/
    protected String oldRoleTitle = "";

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
     **/
    // ---------------------------------------------------------------------
    public RoleMainCargo()
    {
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the function ID whose access is to be checked.
     * 
     * @return int Role Function ID
     **/
    // ----------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.ROLE_ADD_FIND;
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
     * @param roleIndex the int value of the role selected index
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
     * @return String reference to the old role title
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
     * @param roleTitle the old role title value
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
     * @return String reference to the modified role title
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
     * @param roleTitle the modified role title value
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
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
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

    // ---------------------------------------------------------------------
    /**
     * Method to default display string function.
     * <P>
     * 
     * @return String representation of object
     **/
    // ---------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class: RoleMainCargo (Revision " + getRevisionNumber() + ")" + hashCode());

        // pass back result
        return (strResult);
    }

    // ---------------------------------------------------------------------
    /**
     * Retrieves the Team Connection revision number.
     * <P>
     * 
     * @return String representation of revision number
     **/
    // ---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}
