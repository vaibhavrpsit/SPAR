/* ===========================================================================
* Copyright (c) 2006, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/utility/tdo/AbstractPasswordPolicyTDO.java /main/4 2012/04/25 10:25:37 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * Abstract class for the password policies.
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  04/24/12 - Fixes for Fortify redundant null check
 *    abondala  04/11/11 - XbranchMerge abondala_bug11827952-salting_passwords
 *                         from main
 *    abondala  03/26/11 - implement salting passwords
 *    abondala  03/23/11 - Implemented salting for the passwords
 *    mkutiana  02/22/11 - Password policies abstracted
 *    mkutiana  02/18/11 - Abstracting password policies
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.utility.tdo;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.security.EmployeeComplianceIfc;
import oracle.retail.stores.commerceservices.security.EmployeeStatusEnum;
import oracle.retail.stores.commerceservices.security.PasswordEvaluationResultEnum;
import oracle.retail.stores.commerceservices.security.PasswordPolicyCriteriaIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeTransaction;
import oracle.retail.stores.domain.arts.EmployeeWriteTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.operatorid.OperatorIdCargo;
import oracle.retail.stores.pos.tdo.TDOAdapter;


public abstract class AbstractPasswordPolicyTDO extends TDOAdapter implements PasswordPolicyTDOIfc
{
    
    /**
    The logger to which log messages will be sent
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ado.utility.tdo.AbstractPasswordPolicyTDO.class);
    
    /**
     * Manual Entry Requires Password
     */
    private static final String MANUAL_ENTRY_REQUIRES_PASSWORD = "ManualEntryRequiresPassword";
    
    /** Failed login attempts code */
    public static final String EMP_LOGIN_LOCKOUT= "EmpLoginLockout"; 
    
    //----------------------------------------------------------------------
    /**
       Checks if Training Model.
       @param bus reference to bus
       @return true if training mode is on, otherwise false
    **/
    //----------------------------------------------------------------------
    public boolean checkTrainingMode(BusIfc bus) 
    {
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        boolean trainingModeOn = false;    
         RegisterIfc register = cargo.getRegister();
         if (register != null && register.getWorkstation() != null)
         {
             trainingModeOn = register.getWorkstation().isTrainingMode();
         }
        return trainingModeOn;
    }
    
   //----------------------------------------------------------------------
    /**
       Checks Manual Entry Requires Password. 
       @param bus reference to bus
       @return true if as per meaning of the parameter password is required
    **/
    //----------------------------------------------------------------------
    public boolean  checkPasswordParameter(BusIfc bus) 
    {
        boolean passwordRequired = true;
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        String manualPasswordParm = MANUAL_ENTRY_REQUIRES_PASSWORD;
        try
        {
            
            if (!"Y".equalsIgnoreCase(pm.getStringValue(manualPasswordParm)))
            {
                passwordRequired = false;   
            }
        }
        catch (ParameterException pe)
        {
            logger.warn("Could not determine whether password is required based on " 
                         + manualPasswordParm + "defaulting to requiring a password.");
        }
        return passwordRequired;
    }
    
    //----------------------------------------------------------------------
    /**
       Checks if employee passes basic checks for applying password policy
       @param employee reference to employee
       @return true if employee passes basic checks, otherwise false
    **/
    //----------------------------------------------------------------------
    public boolean checkEmployeeApplyPolicy(EmployeeIfc employee)
    {
        boolean applyPolicy = false;
        if(employee != null
           && employee.getEmployeeCompliance() != null
           && employee.getPasswordCreationDate() != null
           && employee.getLoginStatus() == EmployeeIfc.LOGIN_STATUS_ACTIVE)
        {
            applyPolicy = true;
        }
        return applyPolicy;
    }
    
    //--------------------------------------------------------------------------
    /**
     * Checks Change Password Required based on first time login
     * after password reset. This uses the Password Policy Service
     * through the Security Manager evaluateEmployeeCompliance
     * @param employee reference to employee
     * @param bus reference to bus
     * @return true if a password change is needed; false otherwise
     */
    //--------------------------------------------------------------------------
    public boolean checkPasswordChangeByFirstTime(BusIfc bus) 
    {
        boolean passwordChangeRequired = false;
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        EmployeeStatusEnum evaluateStatusEnum = cargo.getEvaluateStatusEnum();
        if (evaluateStatusEnum.equals(EmployeeStatusEnum.PWD_RESET))
        {
            passwordChangeRequired = true;
        }
        return passwordChangeRequired;
    }
    

    //----------------------------------------------------------------------
    /**
     * Check for lockouts due to number failed password attempts and password creation date
     * @param status employee status enum
     * @return boolean true if lock out had happened, otherwise false
     */
    //----------------------------------------------------------------------
    private boolean checkLockout(EmployeeStatusEnum status) 
    {
        boolean lockOut = false;
        if (status.equals(EmployeeStatusEnum.LOCKED_OUT))
        {
            lockOut = true;
        }

        return lockOut;
    }
    
    //----------------------------------------------------------------------
    /**
     * Updates Attempts as per numberOfFailedPasswords parameter passed. This is reset to 0
     * for successful attempt and is increased for each unsuccessful attempt.
     * @param employee reference to employee
     * @param securityManager security manager reference
     * @param numberOfFailedPasswords number of failed password attempts till now. Reset to 0 for success.
     * @return boolean updatedAttempts update to database was successful
     */
    //----------------------------------------------------------------------
    private boolean updateAttempts(EmployeeIfc employee, 
                                   SecurityManagerIfc securityManager, 
                                   int numberOfFailedPasswords) 
    {
        boolean updatedAttempts = false;
        if (employee != null)
        {
            employee.setNumberFailedPasswords(numberOfFailedPasswords);
            if (employee.getEmployeeCompliance() != null)
            {
                employee.getEmployeeCompliance().setNumberFailedLoginAttempts(employee.getNumberFailedPasswords());
                updatedAttempts = securityManager.updateEmployeeNumberFailedAttempts(employee);
            }
        }
        return updatedAttempts;
    }

    //----------------------------------------------------------------------
    /**
       Employee Compliance Policy being applied to the Logged in user.
       Also, returns if this user was already locked out, so even if he
       logs in he is still locked out
       @param bus reference to bus
       @param securityManager security manager reference
       @param employee reference to employee       
       @return true if logged in user is locked out, false otherwise
    **/
    //----------------------------------------------------------------------
    private boolean employeeComplianceEvaluationLoggedInUser(BusIfc bus,
                                                             EmployeeIfc employee) 
    {
        boolean lockedAlready = false;
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        SecurityManagerIfc securityManager = (SecurityManagerIfc)bus.getManager(SecurityManagerIfc.TYPE);
        if (checkEmployeeComplianceEvaluationAllowed(bus)
              && checkEmployeeApplyPolicy(employee))
        {
            //process employee compliance status
            EmployeeStatusEnum evaluateStatusEnum = retrieveEmployeeComplianceStatus(employee, 
                                                                                    bus);
            //check for lock out if logged in successful. This is actually to make sure that
            //already locked out emomplyee is not allowed to re-login
            lockedAlready = checkLockout(evaluateStatusEnum);
            //from performance viewpoint no need to update if already number of attempt is 0
            if(!lockedAlready && !(employee.getNumberFailedPasswords() == 0))
            {
                //reset attempts
                int numberOfFailedPasswords = 0;
                boolean updatedAttempts = updateAttempts(employee, securityManager, numberOfFailedPasswords);
                if (updatedAttempts)
                {
                    getLogger().info(" Successful Attempt");
                }
            }
            cargo.setLockOut(lockedAlready);
        }
        return lockedAlready;
    }    
    
    
    
    //----------------------------------------------------------------------
    /**
       Employee Compliance Policy being applied to the NOT Logged in user to check if
       we are in a lockout situation.
       @param bus reference to bus
       @param securityManager security manager reference
       @param employee reference to employee       
       @return true if logged in user is locked out, false otherwise
    **/
    //----------------------------------------------------------------------
    private boolean employeeComplianceEvaluationNotLoggedInUser(BusIfc bus, 
                                                                EmployeeIfc employee) 
    {
        boolean lockOut = false;
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        SecurityManagerIfc securityManager = (SecurityManagerIfc)bus.getManager(SecurityManagerIfc.TYPE);
        //update attempts. 
        if (checkEmployeeComplianceEvaluationAllowed(bus) 
            && checkEmployeeApplyPolicy(employee))
        {
            //for change password attempts are not to be updated
            if(!cargo.isLoginValidationChangePassword())
            {
                //check for lockout, for normal login process, not manager override, logged in process is not OK
                //attempts not counted for login validation in change password
                //If N=6 for Lockout user after N consecutive invalid login attempts, then on 7th attempt user is locked out 
                //if the user makes an unsuccessful attempt
                int numberOfFailedPasswords = employee.getNumberFailedPasswords() + 1;
                boolean updatedAttempts = updateAttempts(employee, securityManager, numberOfFailedPasswords);
                if (updatedAttempts)
                {
                    getLogger().info("Failure Attempt");
                }
            }
            EmployeeStatusEnum evaluateStatusEnum = retrieveEmployeeComplianceStatus(employee, 
                                                                                    bus);
            lockOut = checkLockout(evaluateStatusEnum);
            cargo.setLockOut(lockOut);
        }
        return lockOut;
    }
    
    //----------------------------------------------------------------------
    /**
       Read Employee Compliance Password Policy for the Employee.
       Currently there is one default Password Policy for all employees.
       @param bus reference to bus
       @param employee reference to employee
    **/
    //----------------------------------------------------------------------
    public void readEmployeeCompliance(BusIfc bus, EmployeeIfc employee) 
    {
        //Read Employee Compliance for Password Policy Settings
        //Even if Employee is not logged in this could be required for attempts lock out
        SecurityManagerIfc securityManager = (SecurityManagerIfc)bus.getManager(SecurityManagerIfc.TYPE);
        EmployeeComplianceIfc employeeCompliance = securityManager.readEmployeeCompliancePasswordPolicyForEmployee(employee);
        employee.setEmployeeCompliance(employeeCompliance);
    }
    
    //----------------------------------------------------------------------
    /**
       This is a Convenience method that reads employee compliance 
       and calls employee compliance evaluation and further checks for lockout, 
       since at lock out, the Employee cannot proceed further in the Application. 
       @param bus reference to bus
       @param securityManager security manager reference
       @param employee reference to employee  
       @param loggedInUser true if this is used for a user who could login
              successfully, otherwise false     
       @return true if logged in user is locked out, false otherwise
    **/
    //----------------------------------------------------------------------
    public boolean employeeComplianceEvaluation(BusIfc bus,
                                                EmployeeIfc employee,
                                                boolean loggedInUser)
    {
        if (loggedInUser)
        {
            return employeeComplianceEvaluationLoggedInUser(bus,
                                                            employee); 
        }
        else
        {
            return employeeComplianceEvaluationNotLoggedInUser(bus, 
                                                               employee);   
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Processes to retrieve Employee Compliance Status.
     * @param employee reference to employee
     * @param securityManager security manager reference
     * @param bus reference to bus
     * @return EmployeeStatusEnum employee status enum
     */
    //----------------------------------------------------------------------
    public EmployeeStatusEnum retrieveEmployeeComplianceStatus(EmployeeIfc employee, 
                                                              BusIfc bus) 
    {
        SecurityManagerIfc securityManager = (SecurityManagerIfc)bus.getManager(SecurityManagerIfc.TYPE);
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        EmployeeStatusEnum evaluateStatusEnum = EmployeeStatusEnum.ACTIVE;
        if (checkEmployeeApplyPolicy(employee))
        {
            EmployeeComplianceIfc employeeCompliance = employee.getEmployeeCompliance();
            employeeCompliance.setNumberFailedLoginAttempts(employee.getNumberFailedPasswords());
            employeeCompliance.setPasswordCreationDate(employee.getPasswordCreationDate()); 
            employeeCompliance.setPasswordChangeRequired(employee.isPasswordChangeRequired());
            evaluateStatusEnum = securityManager.evaluateEmployeeCompliance(employeeCompliance);            
            cargo.setEvaluateStatusEnum(evaluateStatusEnum);
            cargo.setPasswordExpired(employeeCompliance.isPasswordExpired());
            PasswordPolicyCriteriaIfc maxLoginAttempts = employeeCompliance.getPasswordPolicy().getCriterion(EMP_LOGIN_LOCKOUT);
            cargo.setMaximumAttempts(maxLoginAttempts.getValue().intValue());
            cargo.setFailedLoginAttempts(employeeCompliance.getNumberFailedLoginAttempts());
        }       
        return evaluateStatusEnum;
    }   
    
    //----------------------------------------------------------------------
    /**
     * Processes to retrieve Password Compliance Status
     * @param newPassword new password
     * @param employee reference to employee
     * @param securityManager security manager reference
     * @param bus reference to bus
     * @return EmployeeStatusEnum employee status enum
     */
    //----------------------------------------------------------------------
    public PasswordEvaluationResultEnum retrievePasswordComplianceStatus(String newPassword,
                                                                         EmployeeIfc employee, 
                                                                         BusIfc bus) 
    {
        PasswordEvaluationResultEnum evaluateStatusEnum  = PasswordEvaluationResultEnum.SUCCESS;
        if (checkEmployeeApplyPolicy(employee))
        {
            SecurityManagerIfc securityManager = (SecurityManagerIfc)bus.getManager(SecurityManagerIfc.TYPE);
            evaluateStatusEnum = securityManager.evaluatePasswordCompliance(employee.getEmployeeCompliance(),
                                                                            newPassword);   
        }
        return evaluateStatusEnum;
    }   
    
    //----------------------------------------------------------------------
    /**
     * Inserts Password History
     * @param employee
     *            reference to employee
     * @throws DataException data exception
     * @return boolean updates were successful
     */
    //----------------------------------------------------------------------
    public boolean updateEmployeeInsertPasswordHistory(EmployeeIfc employee)
                                                          throws DataException
        {
            boolean updateSuccess = false;

            try
            {               
                // Update the employee in the database
                EmployeeWriteTransaction empWriteTrans = null;
                empWriteTrans = (EmployeeWriteTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_WRITE_TRANSACTION);
                empWriteTrans.saveEmployeeAndPasswordHistory(employee);

                // Indicate success
                updateSuccess = true;
            }
            catch (DataException de)
            {
                logger.error(
                    "Unable to update employee or insert password history" + employee.getEmployeeID(),
                    de);
                throw de;
            }

            return updateSuccess;
        }
    
   //----------------------------------------------------------------------
    /**
     * Reads Password History for the employee
     * @param employee reference to employee
     * @return employee with password history
     * @throws DataException data exception
     */
    //----------------------------------------------------------------------
    public EmployeeIfc readPasswordHistory(EmployeeIfc employee)
                         throws DataException
    {
        EmployeeIfc employeeWithPasswordHistory = null;
        if (employee != null)
        {  
                
                EmployeeTransaction empTransaction = null;
                empTransaction = (EmployeeTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_TRANSACTION);                
                try
                {
                    employeeWithPasswordHistory = empTransaction.readPasswordHistory(employee);
                }
                catch (DataException de)
                {
                    logger.error(
                            "Unable to read password policy  for " + employee.getEmployeeID(),
                            de);
                    throw de;
                }
        }
        return employeeWithPasswordHistory;
    }    
}
