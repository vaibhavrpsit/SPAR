/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeemain/EmployeeCargo.java /main/14 2011/02/25 15:24:53 hyin Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    hyin      02/25/11 - add check for fingerprint change
 *    blarsen   02/23/11 - Added data field and accessors for
 *                         fingerprintEnrollmentTemplate. The fingerprint is
 *                         stored on the cargo by the enroll aisle for later
 *                         checking by the verify aisle.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *6    360Commerce 1.5         10/16/2006 5:54:12 PM  Christian Greene
 *     re-adding import
 *     oracle.retail.stores.pos.services.common.DBErrorCargoIfc, which is not
 *     the same as _360Commerce
 *5    360Commerce 1.4         10/12/2006 8:17:49 AM  Christian Greene Adding
 *     new functionality for PasswordPolicy.  Employee password will now be
 *     persisted as a byte[] in hexadecimal.  Updates include UI changes,
 *     persistence changes, and AppServer configuration changes.  A database
 *     rebuild with the new SQL scripts will be required.
 *4    360Commerce 1.3         9/26/2006 9:17:52 AM   Christian Greene
 *     implement EmployeeCargoIfc
 *3    360Commerce 1.2         3/31/2005 4:27:56 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:21:17 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:10:48 PM  Robert Pearse   
 *
 Revision 1.6  2004/09/23 00:07:15  kmcbride
 @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 Revision 1.5  2004/07/26 23:47:07  jriggins
 @scr 5759 Added logic to display a dialog in the event that a user is attempting to add a standard employee using an ID that is in the temporary employee range.
 *
 Revision 1.4  2004/04/06 18:34:04  awilliam
 @scr 4042 Security: Add Temp Employee: User with Manager Override and No Access priv does NOT invoke SECURITY_ERROR
 *
 Revision 1.3  2004/02/12 16:50:19  mcs
 Forcing head revision
 *
 Revision 1.2  2004/02/11 21:49:04  rhafernik
 @scr 0 Log4J conversion and code cleanup
 *
 Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Dec 16 2003 15:25:48   jriggins
 * Making use of the EmployeeTypeEnum class.
 * Resolution for 3597: Employee 7.0 Updates
 * 
 *    Rev 1.1   Dec 12 2003 14:34:04   jriggins
 * Updates for Add Options usecase in 7.0
 * Resolution for 3597: Employee 7.0 Updates
 * 
 *    Rev 1.0   Aug 29 2003 15:59:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jul 22 2003 16:55:32   DCobb
 * Added StatusChange letter and getEmployeeStatusFromModel() method.
 * Resolution for POS SCR-3217: Unable to change employee status from active to inactive
 * 
 *    Rev 1.0   Apr 29 2002 15:23:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:32:36   msg
 * Initial revision.
 * 
 *    Rev 1.2   22 Jan 2002 16:02:06   baa
 * split employe/ role  role function
 * Resolution for POS SCR-714: Roles/Security 5.0 Updates
 *
 *    Rev 1.1   21 Jan 2002 17:51:06   baa
 * converting to new security model
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.0   Sep 21 2001 11:23:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeemain;

import java.util.Vector;

import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.pos.services.common.EmployeeCargoIfc;
import oracle.retail.stores.pos.services.common.PasswordCargoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.EmployeeTypeEnum;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;
import oracle.retail.stores.pos.services.common.WriteHardTotalsCargoIfc;
import oracle.retail.stores.pos.ui.beans.EmployeeMasterBeanModel;

/**
 * The EmployeeCargo contains the data that needs to be passed to the
 * EmployeeFind service.
 * 
 * @version $Revision: /main/14 $
 */
public class EmployeeCargo extends UserAccessCargo implements EmployeeCargoIfc,
        PasswordCargoIfc, DBErrorCargoIfc, TourCamIfc,
        java.io.Serializable, WriteHardTotalsCargoIfc
{
    static final long serialVersionUID = -846192416023991395L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    public static final int DEFAULT_ID = 1;

    /**
     * string for "DoSearch" letter
     */
    public static final String DO_SEARCH = "DoSearch";

    /**
     * string for DuplicateID letter
     */
    public static final String DUPLICATE_ID = "DuplicateID";

    /**
     * string for DuplicateName letter
     */
    public static final String DUPLICATE_NAME = "DuplicateName";

    /**
     * string for StatusChange letter
     */
    public static final String STATUS_CHANGE = "StatusChange";

    /**
     * string for an Exit letter
     */
    public static final String EXIT = "Exit";

    /**
     * string for the Match letter
     */
    public static final String MATCH = "Match";

    /**
     * string for the No Match letter
     */
    public static final String NO_MATCH = "NoMatch";

    /**
     * string for the Not Fatal letter
     */
    public static final String NOT_FATAL = "NotFatal";

    /**
     * string for Save letter
     */
    public static final String POSSIBLE_MATCHES = "PossibleMatches";

    public static final String SINGLE_MATCH = "Continue";

    /**
     * string for Save letter
     */
    public static final String SAVE = "Save";

    /**
     * string for the Too Many letter
     */
    public static final String TOO_MANY = "TooMany";

    /**
     * string for the Add letter
     */
    public static final String ADD = "Add";

    /**
     * string for the AddStandard letter
     */
    public static final String ADD_STANDARD = "AddStandard";

    /**
     * string for the AddTemporary letter
     */
    public static final String ADD_TEMPORARY = "AddTemporary";

    /**
     * string for the TempIDForStandard letter
     */
    public static final String TEMP_ID_FOR_STANDARD = "TempIDForStandard";

    /**
     * EmployeeIfc object
     */
    protected EmployeeIfc employee;

    /**
     * Employee ID of the employee operating the Employee service This ID
     * corresponds to EmployeeID in the calling services. It was changed here
     * for clarity.
     */
    protected String operatorID;

    /**
     * EmployeeIfc object for the operator
     */
    protected EmployeeIfc operador;

    /**
     * EmployeeIfc object, this employee is the one selected from the database
     */
    protected EmployeeIfc originalEmployee;

    /**
     * List of Employees found in database
     */
    protected Vector employeeList;

    /**
     * Screen to use when showing list of matching Employee records
     */
    protected int screen;

    /**
     * The result of the an interaction with the data manager
     */
    protected int dataExceptionErrorCode;

    /**
     * The type of error to pass back.
     */
    protected boolean fatalError;

    /**
     * The plain text password.
     */
    protected String plainTextPassword;

    /**
     * Holds all role ids
     */
    protected RoleIfc[] roles = null;

    /**
     * Holds all role titles
     */
    protected String[] roleTitles = null;

    /**
     * The financial data for the store
     */
    protected StoreStatusIfc storeStatus;

    /**
     * The register at which operations are being performed
     */
    protected RegisterIfc register = null;

    /**
     * Holds the Access Function Id For security Purposes
     */
    protected int accessFunctionID = RoleFunctionIfc.EMPLOYEE_ADD_FIND;

    /**
     * The type of employee add we are performing defaults to standard.
     */
    protected EmployeeTypeEnum addEmployeeType = EmployeeTypeEnum.STANDARD;

    /**
     * The fingerprint enrollment template.
     */
    protected byte[] fingerprintEnrollmentTemplate;

    /**
     * if a new fingerprint has been enrolled
     */
    protected boolean enrolledNewFingerprint = false;
    
    /**
     * Constructs EmployeeCargo object.
     * <P>
     * 
     * @param none
     * @return none
     */
    public EmployeeCargo()
    {

        employee = null;

    }

    /**
     * Returns the function ID whose access is to be checked.
     * 
     * @return int Role Function ID
     */
    public int getAccessFunctionID()
    {
        return accessFunctionID;
    }

    /**
     * Sets The Access Function ID
     */
    public void setAccessFunctionID(int value)
    {
        accessFunctionID = value;
    }

    /**
     * Resets employee cargo.
     */
    public void resetCargo()
    {
        // setEmployee(null);
    }

    /**
     * Retrieves employee.
     * 
     * @return employee
     */
    public EmployeeIfc getEmployee()
    {
        return (employee);
    };

    /**
     * Sets employee.
     * 
     * @return void
     */
    public void setEmployee(EmployeeIfc value)
    {
        employee = value;
    };

    /**
     * Sets the operator.
     * 
     * @return void
     */
    public void setOperator(EmployeeIfc value)
    {
        operador = value;
        operatorID = operador.getEmployeeID();
    };

    /**
     * Retrieves operator.
     * 
     * @return operator
     */
    public EmployeeIfc getOperator()
    {
        return (operador);
    };

    /**
     * Get the Employee list in the cargo.
     * 
     * @param none
     * @return Employee reference to the Employee list in the cargo
     * @exception
     */
    public Vector getEmployeeList()
    { // Begin getEmployeeList()

        return employeeList;

    } // End getEmployeeList()

    /**
     * Set the Employee list in the cargo.
     * 
     * @param employeeList reference to Employee list to set the cargo to
     *            reference
     * @return void
     * @exception none
     */
    public void setEmployeeList(Vector employeeList)
    { // Begin setEmployeeList()

        this.employeeList = employeeList;

    } // End setEmployeeList()

    /**
     * Get the screen used to display a list of matching Employee records.
     * 
     * @param none
     * @return int screen ID to display
     * @exception
     */
    public int getScreen()
    { // Begin getScreen()

        return screen;

    } // End getScreen()

    /**
     * Set the screen used to display a list of matching Employee records.
     * 
     * @param screen ID of screen to display a list of matching Employee records
     * @return void
     * @exception none
     */

    public void setScreen(int screen)
    { // Begin setScreen()

        this.screen = screen;

    } // End setScreen()

    /**
     * Returns the error code returned with a DataException.
     * 
     * @return the integer value
     */
    public int getDataExceptionErrorCode()
    {
        return dataExceptionErrorCode;
    }

    /**
     * Sets the error code returned with a DataException.
     * 
     * @param the integer value
     */
    public void setDataExceptionErrorCode(int value)
    {
        dataExceptionErrorCode = value;
    }

    /**
     * Retrieves the current operator identifier.
     * 
     * @return operator identifier
     */
    public String getOperatorID()
    { // begin getOperatorID()
        return (operatorID);
    } // end getOperatorID()

    /**
     * Sets the current operator identifier.
     * 
     * @param value operator ID
     * @return void
     */
    public void setOperatorID(String value)
    { // begin setOperatorID()
        operatorID = value;
    } // end setOperatorID()

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.EmployeeCargoIfc#getEmployeeID()
     */
    public String getEmployeeID()
    {
        if (employee != null)
            return employee.getEmployeeID();
        return null;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.EmployeeCargoIfc#setEmployeeID(java.lang.String)
     */
    public void setEmployeeID(String employeeID)
    {
        if (employee != null)
            employee.setEmployeeID(employeeID);
    }

    /**
     * Set the value of the fatalError variable.
     * 
     * @param boolean value to set
     * @return void
     * @exception none
     */
    public void setFatalError(boolean value)
    { // Begin setFatalError()

        fatalError = value;

    } // End setFatalError()

    /**
     * Get the value of the fatalError variable.
     * 
     * @param none
     * @return value of fatalError
     * @exception
     */
    public boolean getFatalError()
    { // Begin getFatalError()

        return fatalError;

    } // End getFatalError()

    /**
     * Retrieves the original employee.
     * 
     * @param none
     * @return employee
     */
    public EmployeeIfc getOriginalEmployee()
    {
        return (originalEmployee);
    }

    /**
     * Sets employee.
     * 
     * @param EmployeeIfc value of the original employee
     * @return void
     */
    public void setOriginalEmployee(EmployeeIfc value)
    {
        originalEmployee = value;
    };

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.EmployeeCargoIfc#getPlainTextPassword()
     */
    public String getPlainTextPassword()
    {
        return plainTextPassword;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.EmployeeCargoIfc#setPlainTextPassword(java.lang.String)
     */
    public void setPlainTextPassword(String password)
    {
        plainTextPassword = password;
    }

    /**
     * Retrieves the array of roles.
     * 
     * @param none
     * @return RoleIfc[] the array of roles
     */
    public RoleIfc[] getRoles()
    {
        return (roles);
    };

    /**
     * Sets role id array.
     * 
     * @param value the array of roles
     * @return void
     */
    public void setRoles(RoleIfc[] value)
    {
        roles = value;
    };

    /**
     * Retrieves the array of role Titles.
     * 
     * @return the array of role titles
     */
    public String[] getRoleTitles()
    {
        return (roleTitles);
    };

    /**
     * Sets role Titles array.
     * 
     * @param value array of role titles
     * @return void
     */
    public void setRoleTitles(String[] value)
    {
        roleTitles = value;
    };

    /**
     * Returns the store status.
     * 
     * @return The store status.
     */
    public StoreStatusIfc getStoreStatus()
    { // begin getStoreStatus()
        return storeStatus;
    } // end getStoreStatus()

    /**
     * Sets the store status.
     * 
     * @param value The store status.
     */
    public void setStoreStatus(StoreStatusIfc value)
    { // begin setStoreStatus()
        storeStatus = value;
    } // end setStoreStatus()

    /**
     * Returns the register at which operations are being performed.
     * 
     * @return The register where operations are being performed.
     */
    public RegisterIfc getRegister()
    {
        return (register);
    }

    /**
     * Sets the register at which operations are to be performed.
     * 
     * @param register The register where operations are being performed.
     */
    public void setRegister(RegisterIfc register)
    {
        this.register = register;
    }

    /**
     * Returns the selected status.
     * 
     * @param EmployeeMasterBeanModel model
     * @return int EmployeeIfc status constant
     */
    public static int getEmployeeStatusFromModel(UtilityManagerIfc utility, EmployeeMasterBeanModel model)
    {
        int returnValue = EmployeeIfc.LOGIN_STATUS_UNKNOWN;
        String statusValues[] = model.getStatusValues();
        int selected = model.getSelectedStatus();
        String selectedStatus = statusValues[selected];

        String statusActive = utility
                .retrieveCommonText(EmployeeIfc.LOGIN_STATUS_DESCRIPTORS[EmployeeIfc.LOGIN_STATUS_ACTIVE]);
        String statusInactive = utility
                .retrieveCommonText(EmployeeIfc.LOGIN_STATUS_DESCRIPTORS[EmployeeIfc.LOGIN_STATUS_INACTIVE]);

        if (selectedStatus.equals(statusActive))
        {
            returnValue = EmployeeIfc.LOGIN_STATUS_ACTIVE;
        }
        else if (selectedStatus.equals(statusInactive))
        {
            returnValue = EmployeeIfc.LOGIN_STATUS_INACTIVE;
        }

        return returnValue;
    }

    /**
     * Returns the add employee type
     * 
     * @return the add employee type as an int
     * @see oracle.retail.stores.domain.employee.EmployeeIfc
     */
    public EmployeeTypeEnum getAddEmployeeType()
    {
        return addEmployeeType;
    }

    /**
     * Sets the add employee type
     * <P>
     * 
     * @see oracle.retail.stores.domain.employee.EmployeeIfc
     */

    public void setAddEmployeeType(EmployeeTypeEnum addEmployeeType)
    {
        this.addEmployeeType = addEmployeeType;
    }

    /**
     * Returns the fingerprint enrollment template.
     * 
     * @return the afingerprint enrollment template.
     * @see oracle.retail.stores.domain.employee.EmployeeIfc
     */
    public byte[] getFingerprintEnrollmentTemplate()
    {
        return fingerprintEnrollmentTemplate;
    }

    /**
     * Sets the fingerprint enrollment template.
     * <P>
     * 
     * @see oracle.retail.stores.domain.employee.EmployeeIfc
     */
    public void setFingerprintEnrollmentTemplate(byte[] fingerprintEnrollmentTemplate)
    {
        this.fingerprintEnrollmentTemplate = fingerprintEnrollmentTemplate;
    }

    
    /**
     * Returns enrolledNewFingerprint
     * @return the enrolledNewFingerprint
     */
    public boolean isEnrolledNewFingerprint() 
    {
        return enrolledNewFingerprint;
    }

    /**
     * Sets enrolledNewFingerprint
     * @param enrolledNewFingerprint the enrolledNewFingerprint to set
     */
    public void setEnrolledNewFingerprint(boolean enrolledNewFingerprint) 
    {
        this.enrolledNewFingerprint = enrolledNewFingerprint;
    }

    /**
     * Returns a string representation of this object.
     * 
     * @param none
     * @return String representation of object
     */
    public String toString()
    { // begin toString()
        // result string
        String strResult = new String("Class:  EmployeeCargo (Revision " + getRevisionNumber() + ")" + hashCode());
        if (employee != null)
        {
            strResult += "\nemployee = \n";
            strResult += employee + "\n";
        }
        else
        {
            strResult += "\nemployee = null\n";
        }

        if (originalEmployee != null)
        {
            strResult += "\n" + originalEmployee + "\n";
        }
        else
        {
            strResult += "\noriginalEmployee = null\n";
        }

        if (employeeList != null)
        {
            strResult += "\nemployee list = \n";
            for (int i = 0; i < employeeList.size(); i++)
            {
                EmployeeIfc tempEmployee = (EmployeeIfc) employeeList.elementAt(i);
                strResult += tempEmployee.toString() + "\n";
            }

        }
        else
        {
            strResult += "employeeList = null \n";
        }

        strResult += "\nscreen number = " + screen + "\n";

        strResult += "\ndataExceptionErrorCode =" + dataExceptionErrorCode + "\n";

        strResult += "\nfatalError =" + fatalError + "\n";

        if (roles != null)
        {
            strResult += "\nroles = \n";
            for (int i = 0; i < roles.length; i++)
            {
                strResult += roles[i].toString() + "\n";
            }
        }
        else
        {
            strResult += "roles = null \n";
        }

        if (roleTitles != null)
        {
            strResult += "\nroleTitles = \n";
            for (int i = 0; i < roleTitles.length; i++)
            {
                strResult += roleTitles[i] + "\n";
            }
        }
        else
        {
            strResult += "roleTitles = null \n";
        }

        if (register != null)
        {
            strResult += "\nregister = \n" + register.toString() + "\n";
        }
        else
        {
            strResult += "\nregister = null\n";
        }

        // pass back result
        return (strResult);
    } // end toString()

    /**
     * Returns the revision number of the class.
     * 
     * @param none
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    } // end getRevisionNumber()

    /**
     * Create a SnapshotIfc which can subsequently be used to restore the cargo
     * to its current state.
     * 
     * @param none
     * @return an object which stores the current state of the cargo.
     * @see oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc
     */

    public SnapshotIfc makeSnapshot()
    {
        return new TourCamSnapshot(this);

    }

    /**
     * Reset the cargo data using the snapshot passed in.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>The snapshot represents the state of the cargo, possibly relative to
     * the existing state of the cargo.
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>The cargo state has been restored with the contents of the snapshot.
     * </UL>
     * 
     * @param snapshot is the SnapshotIfc which contains the desired state of
     *            the cargo.
     * @exception ObjectRestoreException is thrown when the cargo cannot be
     *                restored with this snapshot
     */
    public void restoreSnapshot(SnapshotIfc s) throws ObjectRestoreException
    {

        EmployeeCargo savedCargo = (EmployeeCargo) s.restoreObject();

        this.dataExceptionErrorCode = savedCargo.getDataExceptionErrorCode();
        this.employee = savedCargo.getEmployee();
        this.employeeList = savedCargo.getEmployeeList();
        this.operador = savedCargo.getOperator();
        this.operatorID = savedCargo.getOperatorID();
        this.register = savedCargo.getRegister();
        this.roles = savedCargo.getRoles();
        this.roleTitles = savedCargo.getRoleTitles();
        this.screen = savedCargo.getScreen();
        this.storeStatus = savedCargo.getStoreStatus();
    }

}// end EmployeeCargo
