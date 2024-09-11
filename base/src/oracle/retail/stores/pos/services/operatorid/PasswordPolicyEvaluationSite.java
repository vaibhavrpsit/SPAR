/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/PasswordPolicyEvaluationSite.java /rgbustores_13.4x_generic_branch/1 2011/05/23 13:22:51 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  05/23/11 - Modifying Audit Log entry for corner case where
 *                         NoFingerprint(parameter) and FingerPrint reader
 *                         ENTER happen togeather
 *    hyin      02/22/11 - format change
 *    hyin      02/18/11 - update last login column
 *    hyin      02/10/11 - change based on loginDevice
 *    hyin      01/27/11 - use fingerprint event when logged in using
 *                         fingerprint
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *    11   360Commerce 1.10        6/12/2008 4:57:15 AM   Manikandan Chellapan
 *         CR#31924 Fixed missing audit logs for training mode login events
 *    10   360Commerce 1.9         6/7/2008 6:09:16 AM    Manikandan Chellapan
 *         CR#31924 Enabled audit logging for training and reentry login
 *         logout 
 *    9    360Commerce 1.8         6/7/2008 12:35:10 AM   Manikandan Chellapan
 *         CR31968 Logging user login incase of register offline
 *    8    360Commerce 1.7         3/7/2008 4:46:52 AM    Anil Kandru
 *         Lock_Out Event has been logged properly
 *    7    360Commerce 1.6         3/6/2008 5:45:55 AM    Chengegowda Venkatesh
 *          For CR 30275
 *    6    360Commerce 1.5         1/10/2008 7:59:31 AM   Manas Sahu      Event
 *          Originator Changes
 *    5    360Commerce 1.4         1/7/2008 7:55:27 AM    Chengegowda Venkatesh
 *          Audit log changes
 *    4    360Commerce 1.3         11/7/2006 5:56:03 PM   Brett J. Larsen CR
 *         22927 - add password policy to MPOS - changed a private method to
 *         protected so MPOS's subclass has access
 *    3    360Commerce 1.2         10/16/2006 3:35:03 PM  Rohit Sachdeva
 *         21237: Password Policy Flow Updates
 *    2    360Commerce 1.1         10/9/2006 1:31:55 PM   Rohit Sachdeva
 *         21237: Password Policy Updates
 *    1    360Commerce 1.0         10/6/2006 4:09:12 PM   Rohit Sachdeva  
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;

// foundation imports
import java.util.Date;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.audit.AuditLoggerConstants;
import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.UserEvent;
import oracle.retail.stores.commerceservices.security.EmployeeComplianceIfc;
import oracle.retail.stores.commerceservices.security.EmployeeStatusEnum;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeWriteTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.utility.LoginIdDeviceIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

// --------------------------------------------------------------------------
/**
 * This site does the handling for employee compliance checks based on creation
 * date.
 * <p>
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
// --------------------------------------------------------------------------
public class PasswordPolicyEvaluationSite extends PosSiteActionAdapter
{
    /**
     * revision number
     */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * The logger to which log messages will be sent
     */
    protected static Logger logger = Logger
            .getLogger(oracle.retail.stores.pos.services.operatorid.PasswordPolicyEvaluationSite.class);

    /**
     * resource id employee lockout
     */
    private static final String EMPLOYEE_LOCKOUT = "EmployeeLockout";

    /**
     * resource id employee compliance expiration warning
     */
    private static final String EMPLOYEE_COMPLIANCE_EXPIRATION_WARNING = "EmployeeComplianceExpirationWarning";

    /**
     * resource id employee compliance grace period notice
     */
    private static final String EMPLOYEE_COMPLIANCE_GRACE_PERIOD_NOTICE = "EmployeeComplianceGracePeriodNotice";

    /**
     * letter to change password from here
     */
    private static final String CHANGE_PASSWORD_FROM_PASSWORD_POLICY = "ChangePasswordFromPasswordPolicy";

    // ----------------------------------------------------------------------
    /**
     * Password Policy Evaluations
     * <P>
     *
     * @param bus Service Bus
     */
    // ----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        LetterIfc letter = new Letter(CommonLetterIfc.SUCCESS);
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        /**
         * For last login column, as long as user uses right
         * password/biometrics, we log it. The purpose of this
         * column is for quick verification of fingerprint when 
         * in "fingerprintOnly" login mode
         */
        logEmpLastLoginTime(cargo.getSelectedEmployee());
        
        // if manager override/training mode
        // password policy employee compliance is not done
        boolean employeeComplianceAllowed = checkEmployeeComplianceEvaluationAllowed(bus);
        if (employeeComplianceAllowed)
        {
            EmployeeIfc employee = cargo.getSelectedEmployee();
            EmployeeComplianceIfc employeeCompliance = employee.getEmployeeCompliance();
            if (employee != null && employeeCompliance != null && employee.getPasswordCreationDate() != null)
            {
                EmployeeStatusEnum result = cargo.getEvaluateStatusEnum();

                if (result.equals(EmployeeStatusEnum.LOCKED_OUT))
                {
                    // Beyond the grace period, locked out.
                    // USE Case: Alternate Flow: Password Expired Lockout
                    logger.info("Account has been locked due to failed login attemps or expired password");

                    // Audit Logging UserEvent for user lockout
                    /*
                     * if(!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister())) {
                     * AuditLoggerServiceIfc auditService =
                     * AuditLoggingUtils.getAuditLogger(); UserEvent ev =
                     * (UserEvent)AuditLoggingUtils.createLogEvent(UserEvent.class,
                     * AuditLogEventEnum.LOCKOUT);
                     * ev.setStoreId(Gateway.getProperty("application",
                     * "StoreID", "")); RegisterIfc ri = cargo.getRegister();
                     * if(ri!=null) { WorkstationIfc wi = ri.getWorkstation();
                     * if(wi!=null) {
                     * ev.setRegisterNumber(wi.getWorkstationID()); } }
                     * ev.setUserId(employee.getLoginID());
                     * if(EventOriginatorInfoBean.getEventOriginator() != null)
                     * ev.setEventOriginator(EventOriginatorInfoBean.getEventOriginator());
                     * auditService.logStatusSuccess(ev); }
                     */

                    // show dialog message
                    displayErrorDialog(bus, EMPLOYEE_LOCKOUT, CommonLetterIfc.FAILURE);
                    return;

                }
                if (result.equals(EmployeeStatusEnum.WARN))
                {
                    // number of days left
                    // USE Case: Alternate Flow: Password Expiration Warning
                    logger.info("Account is within the password expiration warning period");
                    // show dialog message
                    SecurityManagerIfc securityManager = (SecurityManagerIfc) Gateway.getDispatcher().getManager(
                            SecurityManagerIfc.TYPE);
                    int days = securityManager.getDaysUntilPasswordExpiration(employeeCompliance);
                    String daysUntilPasswordExpired = String.valueOf(days);
                    String[] args = new String[1];
                    args[0] = daysUntilPasswordExpired;
                    displayWarnErrorDialog(bus, args);
                    return;

                }
                if (result.equals(EmployeeStatusEnum.EXPIRED_IN_GRACE))
                {
                    // grace period message
                    // Use Case: Alternate Flow: Password Grace Perod Notice
                    logger.info("Account password has expired but the account is within the expiration grace period");
                    // show dialog message
                    displayErrorDialog(bus, EMPLOYEE_COMPLIANCE_GRACE_PERIOD_NOTICE,
                            CHANGE_PASSWORD_FROM_PASSWORD_POLICY);
                    return;

                }
                if (result.equals(EmployeeStatusEnum.ACTIVE))
                {
                    logger.info("For Normal Login Process , Password Policy Evaluation Success");
                    letter = new Letter(CommonLetterIfc.SUCCESS);
                    // log login audit event
                    logLoginEvent(cargo);
                }
            }
            //CR31968 Log login audit event in case of register offline
            else
            {
                // log login audit event
                logLoginEvent(cargo);
            }
        }
        else
        {
            // For Manager Override, no action needed for now
            logger.info("Password Policy Evaluation Employee Compliance Not Allowed");
            // reset
            cargo.setSecurityOverrideFlag(false);
            cargo.setLoginValidationChangePassword(false);
            // log login audit event
            logLoginEvent(cargo);
        }
        bus.mail(letter, BusIfc.CURRENT);
    }

    // ----------------------------------------------------------------------
    /**
     * Displays Error Dialog
     *
     * @param bus reference to bus
     * @param resourceId resource id
     * @param letterName letter name
     */
    // ----------------------------------------------------------------------
    protected void displayErrorDialog(BusIfc bus, String resourceId, String letterName)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(resourceId);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, letterName);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    // ----------------------------------------------------------------------
    /**
     * Displays Warning Error Dialog
     *
     * @param bus reference to bus
     * @param args arguments passed
     */
    // ----------------------------------------------------------------------
    protected void displayWarnErrorDialog(BusIfc bus, String args[])
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        int[] buttons = new int[] { DialogScreensIfc.BUTTON_YES, DialogScreensIfc.BUTTON_NO };
        // Yes takes you to Change Password Policy from Password Policy
        // No takes you through the SUCCESS route as for ACTIVE
        String[] letters = new String[] { CHANGE_PASSWORD_FROM_PASSWORD_POLICY, CommonLetterIfc.SUCCESS };
        UIUtilities.setDialogModel(ui, DialogScreensIfc.YES_NO, EMPLOYEE_COMPLIANCE_EXPIRATION_WARNING, args, buttons,
                letters);
    }

    // ----------------------------------------------------------------------
    /**
     * Checks if Employee Compliance is Allowed.
     *
     * @param bus reference to bus
     * @return true if employee compliance is allowed, otherwise false
     */
    // ----------------------------------------------------------------------
    protected boolean checkEmployeeComplianceEvaluationAllowed(BusIfc bus)
    {
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        if (cargo.getSecurityOverrideFlag())
        {
            return false;
        }
        boolean trainingModeOn = checkTrainingMode(bus);
        if (trainingModeOn)
        {
            return false;
        }
        return true;
    }

    // ----------------------------------------------------------------------
    /**
     * Checks if Training Model.
     *
     * @param bus reference to bus
     * @return true if training mode is on, otherwise false
     */
    // ----------------------------------------------------------------------
    private boolean checkTrainingMode(BusIfc bus)
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

    // --------------------------------------------------------------------------------
    /** Writes audit log for login event
     * @param cargo The operator id cargo
     */
    // --------------------------------------------------------------------------------
    private void logLoginEvent(OperatorIdCargo cargo)
    {
    	boolean isFingerPrint = false;
    	
    	//There is a corner case/scenario where the parameter(FingerprintLoginOptions) is  'NoFingerprint' but a 
    	//connected fingerprint reader is activated/used during login in addition to the login/pwd hence the below isFingerprintAllowed check
    	if (cargo.getLoginIdDevice().equals(LoginIdDeviceIfc.LoginIdDevice.FINGERPRINT) && Utility.getUtil().isFingerprintAllowed()){
    		isFingerPrint = true;
    	}
    	
    	AuditLogEventEnum alee = null;
    	if (isFingerPrint){
    		alee = AuditLogEventEnum.LOGIN_FINGERPRINT;
    	}else {
    		alee = AuditLogEventEnum.LOGIN;
    	}
    	
        // Audit Logging UserEvent for user active
        AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();
        UserEvent ev = (UserEvent) AuditLoggingUtils.createLogEvent(UserEvent.class, alee);
        ev.setStoreId(Gateway.getProperty("application", "StoreID", ""));
        RegisterIfc ri = cargo.getRegister();
        if (ri != null)
        {
            WorkstationIfc wi = ri.getWorkstation();
            if (wi != null)
            {
                ev.setRegisterNumber(wi.getWorkstationID());
            }
        }
        ev.setUserId(cargo.getEmployeeID());
        ev.setStatus(AuditLoggerConstants.SUCCESS);
        if (EventOriginatorInfoBean.getEventOriginator() != null)
            ev.setEventOriginator(EventOriginatorInfoBean.getEventOriginator());
        auditService.logStatusSuccess(ev);
    }
    
    /**
     * Writes the employee login time to lastLoginTime
     * column.
     * @param employee logged in employee
     */
    private void logEmpLastLoginTime(EmployeeIfc employee)
    {
    	if (employee != null)
    	{
    	    employee.setLastLoginTime(new Date());
            EmployeeWriteTransaction empWriteTrans = null;
            empWriteTrans = (EmployeeWriteTransaction) DataTransactionFactory
                    .create(DataTransactionKeys.EMPLOYEE_WRITE_TRANSACTION);

            try {
				empWriteTrans.updateEmployee(employee);
			} catch (DataException de) {
				logger.error("Unable to update employee login time " + employee.getEmployeeID(), de);
			}
    		
    	}else {
    		logger.info("Cannot log empLastLoginTime, employee is null");
    	}
    	
    }

    // ----------------------------------------------------------------------
    /**
     * Returns a string representation of this object.
     * <P>
     *
     * @return String representation of object
     */
    // ----------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        String strResult = new String("Class:  EnterChangePasswordSite (Revision " + getRevisionNumber() + ")"
                + hashCode());

        // pass back result
        return (strResult);
    } // end toString()

    // ----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * <P>
     *
     * @return String representation of revision number
     */
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    } // end getRevisionNumber()
}
