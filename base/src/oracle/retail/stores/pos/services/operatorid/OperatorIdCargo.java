/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/OperatorIdCargo.java /main/15 2014/02/17 08:54:00 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   02/12/14 - Fortify Fix: Prevent heap inspection of passwords by
 *                         avoiding the use of Strings for passwords
 *    tzgarba   10/02/12 - Updated to support using a register ID as the
 *                         context for security checking
 *    blarsen   06/12/12 - Changed getContextValue() to use the register's
 *                         context if it's available.
 *    blarsen   03/06/12 - Using new constant for POS appID
 *    mchellap  07/12/11 - Fortify fix: Removed the main method
 *    blarsen   02/04/11 - Added feild to store the mechanism used to obtain
 *                         the login id (e.g., fingprint, barcode, keyboard)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         3/30/2007 5:15:48 AM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         8    .v8x      1.6.1.0     2/18/2007 1:49:23 PM   Rohit Sachdeva
 *         25389:
 *         ManualEntryID being set from Backoffice for POS
 *    7    360Commerce 1.6         11/28/2006 4:07:54 AM  Brett J. Larsen CR
 *         23011 - add "isPasswordEncrypted" field - needed to deal with an
 *         MPOS use-case
 *    6    360Commerce 1.5         10/17/2006 1:51:48 AM  Rohit Sachdeva
 *         21237: Password Policy Flow Updates
 *    5    360Commerce 1.4         10/12/2006 6:47:50 PM  Christian Greene
 *         Adding new functionality for PasswordPolicy.  Employee password
 *         will now be persisted as a byte[] in hexadecimal.  Updates include
 *         UI changes, persistence changes, and AppServer configuration
 *         changes.  A database rebuild with the new SQL scripts will be
 *         required.
 *    4    360Commerce 1.3         10/7/2006 1:37:13 AM   Rohit Sachdeva
 *         21237: Password Policy Updates
 *    3    360Commerce 1.2         4/1/2005 2:59:12 AM    Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 9:53:48 PM   Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 11:42:50 PM  Robert Pearse
 *
 *   Revision 1.6  2004/09/27 22:32:04  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.5  2004/07/28 22:53:23  epd
 *   @scr 6593 fixed training mode issue
 *
 *   Revision 1.4  2004/02/13 16:35:49  jriggins
 *   @scr 3782 Enter New Password functionality
 *
 *   Revision 1.3  2004/02/12 16:51:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:03:12   CSchellenger
 * Initial revision.
 *
 *    Rev 1.8   Jun 05 2003 13:05:02   bwf
 * Set default idType to "Login".
 * Resolution for 1933: Employee Login enhancements
 *
 *    Rev 1.7   May 15 2003 11:13:56   baa
 * set required password setting to true
 * Resolution for 2373: Access granted to POS functions using one any one character for the password field.
 *
 *    Rev 1.6   May 08 2003 11:32:20   bwf
 * Added idType
 * Resolution for 1933: Employee Login enhancements
 *
 *    Rev 1.5   Apr 14 2003 19:05:32   pdd
 * Deprecated old override stuff.
 * Added passwordRequired field.
 * Resolution for 1933: Employee Login enhancements
 *
 *    Rev 1.4   Mar 17 2003 13:15:08   HDyer
 * Added role function ID to facilitate security overrides by the SecurityManager.
 * Resolution for POS SCR-2089: Manager Override maintains manager security level rather than reverting to lower level
 *
 *    Rev 1.3   23 Jan 2003 15:44:22   mrm
 * Implement JAAS support
 * Resolution for POS SCR-1958: Implement JAAS Support
 *
 *    Rev 1.2   Jan 21 2003 13:09:44   RSachdeva
 * Comment Added
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.1   Jan 21 2003 13:06:02   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.0   Apr 29 2002 15:13:44   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:40:28   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:32:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;

import java.io.UnsupportedEncodingException;

import oracle.retail.stores.commerceservices.security.EmployeeStatusEnum;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.utility.LoginIdDeviceIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;

import org.apache.log4j.Logger;

/**
 * This class holds the data for the Operator Identification Package. This class
 * now implements DBErrorCargoIfc
 *
 */
public class OperatorIdCargo extends AbstractFinancialCargo implements DBErrorCargoIfc
{
    private static final long serialVersionUID = -1943453700783179942L;

    /**
     * A list of employees that are already logged into this register.
     */
    protected EmployeeIfc employees[] = null;

    /**
     * The employee that has been logged in.
     */
    protected EmployeeIfc selectedEmployee = null;

    /**
     * Id that user has entered into the UI.
     */
    protected String employeeID = "";

    /**
     * that user has entered into the UI.
     */
    protected byte[] employeePassword;

    /**
     * A value of false indicates that the service should leave error handling
     * to the calling service.
     */
    protected boolean handleError = true;

    /**
     * A value of true indicates that the service should force the user to enter
     * the password even if the user is found in the list.
     */
    protected boolean passwordRequiredWithList = false;

    /**
     * The number of opportunities that the operator has to enter the password.
     */
    protected int maximumAttempts = 1;

    /**
     * The number of times that the operator has entered the password.
     */
    protected int attempts = 0;

    /**
     * When the calling service performs the error handling, the member
     * indicates error type.
     */
    protected int errorType = 0;

    /**
     * The operator ID prompt text for this service
     */
    protected String operatorIdPromptText = null;

    /**
     * The operator ID screen name for this service
     */
    protected String operatorIdScreenName = null;

    /**
     * employee compliance evaluation result
     */
    EmployeeStatusEnum evaluateStatusEnum = EmployeeStatusEnum.ACTIVE;

    /**
     * false if no override is requested, true is override is needed
     */
    protected boolean securityOverrideFlag = false;

    /**
     * This is used to Track Login Validation For Change Password
     */
    protected boolean loginValidationChangePassword = false;

    /**
     * This is used for Lockout due to Number of attempts or password policy
     * creation date expiration checks
     */
    protected boolean lockOut = false;

    /**
     * This is used to track locale preference for different than user interface
     * locale. This is for Change Password Flow.
     */
    protected boolean employeeLocaleChangePassword = false;

    /**
     * data exception error code
     */
    protected int dataExceptionErrorCode = DataException.NONE;

    /**
     * Access Function ID
     */
    protected int functionID = RoleFunctionIfc.FUNCTION_UNDEFINED;

    /**
     * Indicates whether to prompt for a password
     */
    protected boolean passwordRequired = true;

    /**
     * Indicates whether using Login or Employee id
     */
    protected String idType = "Login";

    /**
     * Placeholder for the new employee password before it gets saved to the
     * current EmployeeIfc instance
     */
    protected String newPassword;

    /**
     * Is the cargo's password already encrypted? Normally it is not encrypted.
     * But in an MPOS use-case where a cached employee is automatically logged
     * in, it is encrypted.
     */
    protected boolean passwordEncrypted = false;

    /**
     * Is the password of the current user expired or not? Default value is
     * false and becomes true if the password is expired
     */
    protected boolean passwordExpired = false;

    /**
     * The number of failed login attempts for the particular user ID.
     */
    protected int failedLoginAttempts;

    /**
     * Which device was used to obtain the login id?
     */
    protected LoginIdDeviceIfc.LoginIdDevice loginIdDevice = LoginIdDeviceIfc.LoginIdDevice.UNKNOWN;

    /**
     * Constructs OperatorIdCargo object.
     * <P>
     */
    public OperatorIdCargo()
    {
    }

    /**
     * Retrieves A list of employees that are already logged into this
     * register.
     *
     * @return A list of employees that are already logged into this register.
     */
    public EmployeeIfc[] getEmployees()
    {
        return (employees);
    }

    /**
     * Sets A list of employees that are already logged into this register..
     *
     * @param value A list of employees that are already logged into this
     *            register.
     */
    public void setEmployees(EmployeeIfc value[])
    {
        employees = value;
    }

    /**
     * Retrieves The employee that has been logged in..
     *
     * @return The employee that has been logged in.
     */
    public EmployeeIfc getSelectedEmployee()
    {
        return (selectedEmployee);
    }

    /**
     * Sets The employee that has been logged in..
     *
     * @param value The employee that has been logged in.
     */
    public void setSelectedEmployee(EmployeeIfc value)
    {
        selectedEmployee = value;
    }

    /**
     * Retrieves Id that user has entered into the UI..
     *
     * @return Id that user has entered into the UI.
     */
    public String getEmployeeID()
    {
        return (employeeID);
    }

    /**
     * Sets Id that user has entered into the UI..
     *
     * @param value Id that user has entered into the UI.
     */
    public void setEmployeeID(String value)
    {
        employeeID = value;
    }

    /**
     * Retrieves the password that the user has entered into the UI.
     *
     * @return that the password represented as bytes in the correct char set.
     */
    public byte[] getEmployeePasswordBytes()
    {
        return (employeePassword);
    }

    /**
     * Sets the password that the user has entered into the UI.
     *
     * @param value the password represented as bytes in the correct char set.
     */
    public void setEmployeePasswordBytes(byte[] value)
    {
        employeePassword = value;
    }

    /**
     * Retrieves A value of false indicates that the service should leave error
     * handling to the calling service..
     *
     * @return A value of false indicates that the service should leave error
     *         handling to the calling service.
     */
    public boolean getHandleError()
    {
        return (handleError);
    }

    /**
     * Sets A value of false indicates that the service should leave error
     * handling to the calling service..
     *
     * @param value A value of false indicates that the service should leave
     *            error handling to the calling service.
     */
    public void setHandleError(boolean value)
    {
        handleError = value;
    }

    /**
     * Retrieves A value of true indicates that the service should force the
     * user to enter the password even if the user is found in the list..
     *
     * @return A value of true indicates that the service should force the user
     *         to enter the password even if the user is found in the list.
     */
    public boolean getPasswordRequiredWithList()
    {
        return (passwordRequiredWithList);
    }

    /**
     * Sets A value of true indicates that the service should force the user to
     * enter the password even if the user is found in the list..
     *
     * @param value A value of true indicates that the service should force the
     *            user to enter the password even if the user is found in the
     *            list.
     */
    public void setPasswordRequiredWithList(boolean value)
    {
        passwordRequiredWithList = value;
    }

    /**
     * Retrieves The number of opportunities that the operator has to enter the
     * password..
     *
     * @return The number of opportunities that the operator has to enter the
     *         password.
     */
    public int getMaximumAttempts()
    {
        return (maximumAttempts);
    }

    /**
     * Sets The number of opportunities that the operator has to enter the
     * password..
     *
     * @param value The number of opportunities that the operator has to enter
     *            the password.
     */
    public void setMaximumAttempts(int value)
    {
        maximumAttempts = value;
    }

    /**
     * Retrieves The number of times that the operator has entered the
     * password..
     *
     * @return The number of times that the operator has entered the password.
     */
    public int getAttempts()
    {
        return (attempts);
    }

    /**
     * Sets The number of times that the operator has entered the password..
     *
     * @param value The number of times that the operator has entered the
     *            password.
     */
    public void setAttempts(int value)
    {
        attempts = value;
    }

    /**
     * Retrieves When the calling service performs the error handling, the
     * member indicates error type..
     *
     * @return When the calling service performs the error handling, the member
     *         indicates error type.
     */
    public int getErrorType()
    {
        return (errorType);
    }

    /**
     * Sets When the calling service performs the error handling, the member
     * indicates error type..
     *
     * @param value When the calling service performs the error handling, the
     *            member indicates error type.
     */
    public void setErrorType(int value)
    {
        errorType = value;
    }

    /**
     * Gets the prompt Enter ID prompt text for this service.
     *
     * @return the prompt text.
     */
    public String getOperatorIdPromptText()
    {
        return operatorIdPromptText;
    }

    /**
     * Sets the prompt Enter ID prompt text for this service.
     *
     * @return the prompt text.
     */
    public void setOperatorIdPromptText(String value)
    {
        operatorIdPromptText = value;
    }

    /**
     * Gets the screen name for this service.
     *
     * @return the screen name.
     */
    public String getOperatorIdScreenName()
    {
        return operatorIdScreenName;
    }

    /**
     * Sets the screen name for this service.
     *
     * @return the screen name.
     */
    public void setOperatorIdScreenName(String value)
    {
        operatorIdScreenName = value;
    }

    /**
     * Returns the securityOverrideFlag boolean.
     *
     * @return The securityOverrideFlag boolean.
     */
    public boolean getSecurityOverrideFlag()
    {
        return securityOverrideFlag;
    }

    /**
     * Sets the securityOverrideFlag boolean.
     *
     * @param value The ssecurityOverrideFlag boolean.
     */
    public void setSecurityOverrideFlag(boolean value)
    {
        securityOverrideFlag = value;

    }

    /**
     * Returns the data exception error code.
     *
     * @return The data exception error code.
     */
    public int getDataExceptionErrorCode()
    {
        return dataExceptionErrorCode;
    }

    /**
     * Sets the data exception error code.
     *
     * @param value The data exception error code.
     */
    public void setDataExceptionErrorCode(int value)
    {
        dataExceptionErrorCode = value;
    }

    /**
     * Sets the function ID whose access is to be checked.
     *
     * @param value int
     */
    public void setAccessFunctionID(int value)
    {
        functionID = value;
    }

    /**
     * Returns the function ID whose access is to be checked.
     *
     * @return int function ID
     */
    public int getAccessFunctionID()
    {
        return functionID;
    }

    /**
     * Returns the passwordRequired.
     *
     * @return boolean
     */
    public boolean isPasswordRequired()
    {
        return passwordRequired;
    }

    /**
     * Sets the passwordRequired.
     *
     * @param passwordRequired The passwordRequired to set
     */
    public void setPasswordRequired(boolean passwordRequired)
    {
        this.passwordRequired = passwordRequired;
    }

    /**
     * Sets the id type. Value=Employee implies we should enter id as employee
     * id number such as '20027'. Value=User implies employee login id such as
     * 'pos'.
     *
     * @param type The id type to set
     */
    public void setIDType(String type)
    {
        this.idType = type;
    }

    /**
     * Gets the id type
     *
     * @return String
     */
    public String getIDType()
    {
        return (this.idType);
    }

    /**
     * Sets the new password
     *
     * @param newPassword the new password, typically entered by the user
     */
    public void setEmployeeNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }

    /**
     * Gets the newPassword
     *
     * @param return the new password
     */
    public String getEmployeeNewPassword()
    {
        return (this.newPassword);
    }

    /**
     * This is used to Track Login Validation For Change Password. This is
     * alternate login.
     *
     * @return true if this is to validate login from change password screen
     */
    public boolean isLoginValidationChangePassword()
    {
        return this.loginValidationChangePassword;
    }

    /**
     * This is used to Track Login Validation For Change Password. This is
     * alternate login.
     *
     * @param loginValidationChangePassword login validation for change password
     */
    public void setLoginValidationChangePassword(boolean loginValidationChangePassword)
    {
        this.loginValidationChangePassword = loginValidationChangePassword;
    }

    /**
     * This is used for Lockout due to Number of attempts or password policy
     * creation date expiration checks
     *
     * @return boolean true if locked out, otherwise false
     */
    public boolean isLockOut()
    {
        return lockOut;
    }

    /**
     * This is used for Lockout due to Number of attempts or password policy
     * creation date expiration checks
     *
     * @param lockOut lock out
     */
    public void setLockOut(boolean lockOut)
    {
        this.lockOut = lockOut;
    }

    /**
     * This is used to track locale preference for different than user interface
     * locale. This is for Change Password Flow.
     *
     * @return true when employee locale is different than current user
     *         interface locale
     */
    public boolean isEmployeeLocaleChangePassword()
    {
        return employeeLocaleChangePassword;
    }

    /**
     * This is used to track locale preference for different than user interface
     * locale. This is for Change Password Flow.
     *
     * @param employeeLocaleChangePassword employee locale in change password
     */
    public void setEmployeeLocaleChangePassword(boolean employeeLocaleChangePassword)
    {
        this.employeeLocaleChangePassword = employeeLocaleChangePassword;
    }

    /**
     * This is used to get Employee Compliance Evaluation Result
     *
     * @return EmployeeStatusEnum employee status enum result
     */
    public EmployeeStatusEnum getEvaluateStatusEnum()
    {
        return evaluateStatusEnum;
    }

    /**
     * This is used to set Employee Compliance Evaluation Result
     *
     * @param evaluateStatusEnum employee status enum result
     */
    public void setEvaluateStatusEnum(EmployeeStatusEnum evaluateStatusEnum)
    {
        this.evaluateStatusEnum = evaluateStatusEnum;
    }

    /**
     * This is used to determine if the cargo's password is already encrypted
     *
     * @return Returns the passwordEncrypted.
     */
    public boolean isPasswordEncrypted()
    {
        return passwordEncrypted;
    }

    /**
     * This is used to set if the cargo's password is already encrypted
     *
     * @param passwordEncrypted The passwordEncrypted to set.
     */
    public void setPasswordEncrypted(boolean passwordEncrypted)
    {
        this.passwordEncrypted = passwordEncrypted;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.commerceservices.logging.MappableContextIfc#getContextValue()
     */
    @Override
    public Object getContextValue()
    {
        Object context = null;

        if (register != null)
        {
            context = register.getContextValue();
        }
        else if (employeeID != null)
        {
            StringBuilder builder = new StringBuilder("Employee[id=");
            builder.append(employeeID);
            builder.append("]");
            context = builder.toString();
        }
        else
        {
            context = getClass().getSimpleName() + "@" + hashCode();
        }
        return context;
    }

    /**
     * Returns default display string.
     *
     * @return String representation of object
     */
    public String toString()
    {
        // build result string
        String strResult = new String("Class:  OperatorIdCargo (Revision " + getRevisionNumber() + ") @" + hashCode());
        strResult += "\n";
        // add attributes to string
        if (employees == null)
        {
            strResult += "employees[]:                        [null]";
        }
        else
        {
            for (int i = 0; i < employees.length; i++)
            {
                strResult += employees[i].toString();
            }
        }
        if (selectedEmployee == null)
        {
            strResult += "selectedEmployee:                   [null]";
        }
        else
        {
            strResult += selectedEmployee.toString();
        }
        strResult += "employeeID:                         [" + employeeID + "]";
        strResult += "employeePassword:                   [" + employeePassword + "]";
        strResult += "handleError:                        [" + handleError + "]";
        strResult += "maximumAttempts:                    [" + maximumAttempts + "]";
        strResult += "attempts:                           [" + attempts + "]";
        strResult += "errorType:                          [" + errorType + "]";
        strResult += "securityOverrideFlag:               [" + securityOverrideFlag + "]";
        strResult += "functionID:                         [" + functionID + "]";
        strResult += "isPasswordEncrypted:                [" + passwordEncrypted + "]";

        // pass back result
        return (strResult);
    }

    /**
     * Retrieves the source-code-control system revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /**
     * This is used to determine if the cargo's password is expired
     *
     * @return Returns the expired.
     */
    public boolean isPasswordExpired()
    {
        return passwordExpired;
    }

    /**
     * This is used to set if the user's password is expired
     *
     * @param expired The expired to set.
     */
    public void setPasswordExpired(boolean expired)
    {
        passwordExpired = expired;
    }

    /**
     * This is used to get the number of failed login attempts
     *
     * @return Returns the failedLoginAttempts.
     */
    public int getFailedLoginAttempts()
    {
        return failedLoginAttempts;
    }

    /**
     * This is used to set the number of failed login attempts
     *
     * @param failedLoginAttempts The failedLoginAttempts.
     */
    public void setFailedLoginAttempts(int failedLoginAttempts)
    {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    /**
     * This is used to get the device used to obtain the login id during login.
     *
     * @return Returns the entryMode.
     */
    public LoginIdDeviceIfc.LoginIdDevice getLoginIdDevice()
    {
        return loginIdDevice;
    }

    /**
     * This is used to set the device used to obtain the login id during login.
     *
     * @param entryMode The entryMode.
     */
    public void setLoginDevice(LoginIdDeviceIfc.LoginIdDevice loginIdDevice)
    {
        this.loginIdDevice = loginIdDevice;
    }

    /**
     * Entry mode is considered "automatic" if the login id was obtained from
     * an mag swipe read or barcode scan
     *
     * @return true if user id obtained from MSR card or barcode.
     */
    public boolean isEntryModeAutomatic()
    {
        return LoginIdDeviceIfc.LoginIdDevice.MAG_STRIPE.equals(this.loginIdDevice) ||
            LoginIdDeviceIfc.LoginIdDevice.BARCODE.equals(this.loginIdDevice);

    }
}