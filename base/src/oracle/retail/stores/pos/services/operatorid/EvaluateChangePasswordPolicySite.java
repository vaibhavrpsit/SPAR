/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/EvaluateChangePasswordPolicySite.java /main/13 2014/01/28 11:05:40 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   01/24/14 - Fortify: Prevent heap inspection of passwords by
 *                         avoiding using Strings
 *    abhineek  04/03/13 - fix for user is not prompted with any error message
 *                         when a special character is entered in the password
 *                         field
 *    mkutiana  02/22/11 - Modified to handle multiple password policies
 *                         (introduction of biometrics)
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   11/04/08 - I18N - Eliminate the Loading and Caching of the
 *                         CodeListMap. CodeListMap is deprecated by
 *                         CodeListManager.
 *

      $Log:
       15   360Commerce 1.14        3/17/2008 5:23:32 AM   Chengegowda
            Venkatesh For Audit Log
       14   360Commerce 1.13        3/10/2008 5:40:17 AM   Chengegowda
            Venkatesh For Audit logging
       13   360Commerce 1.12        3/6/2008 6:08:28 AM    Chengegowda
            Venkatesh For audit logging
       12   360Commerce 1.11        3/6/2008 3:29:27 AM    Manas Sahu      For
            CR 30482
       11   360Commerce 1.10        11/12/2007 2:14:22 PM  Tony Zgarba
            Deprecated all existing encryption APIs and migrated the code to
            the new encryption API.
       10   360Commerce 1.9         11/2/2006 7:10:46 AM   Rohit Sachdeva
            21237: Activating Password Policy Evaluation and Change Password
       9    360Commerce 1.8         10/25/2006 3:13:50 PM  Rohit Sachdeva
            21237: Password Policy TDO updates
       8    360Commerce 1.7         10/24/2006 11:14:13 AM Rohit Sachdeva
            21237: Change Password Login Updates using Main TDO for Retreiving
             Code List Map to Reuse Code
       7    360Commerce 1.6         10/24/2006 9:40:45 AM  Rohit Sachdeva
            21237: Login Updates to Handle Impacts of Password Policy
       6    360Commerce 1.5         10/16/2006 11:52:46 AM Rohit Sachdeva
            21237: Change Password Flow Updates
       5    360Commerce 1.4         10/13/2006 3:57:58 PM  Rohit Sachdeva
            21237: Change Password Refactoring for checking each
            PasswordEvaluationResult expliticly. Refactoring SUCCESS handling
            to be in one location to be clearer.
       4    360Commerce 1.3         10/13/2006 2:54:20 PM  Rohit Sachdeva
            21237: Re-entering Statement deleted by previous check in
       3    360Commerce 1.2         10/12/2006 8:20:59 AM  Christian Greene
            Adding new functionality for PasswordPolicy.  Employee password
            will now be persisted as a byte[] in hexadecimal.  Updates include
             UI changes, persistence changes, and AppServer configuration
            changes.  A database rebuild with the new SQL scripts will be
            required.
       2    360Commerce 1.1         10/11/2006 10:43:48 AM Rohit Sachdeva
            21237: Change Password Updates
       1    360Commerce 1.0         10/6/2006 4:36:48 PM   Rohit Sachdeva
      $
 */
package oracle.retail.stores.pos.services.operatorid;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.UserEvent;
import oracle.retail.stores.commerceservices.security.PasswordEvaluationResultEnum;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.tdo.PasswordPolicyTDOIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ChangePasswordBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site Handles Password Compliance Checks for New Password. Validate Login
 * check has been done up to this point.
 * 
 * @version $Revision: /main/13 $
 */
public class EvaluateChangePasswordPolicySite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 897876235861041615L;
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/13 $";
    /**
       resource id
    **/
    private static final String CHANGE_PASSWORD_POLICY_COMMON_ERROR = "ChangePasswordPolicyCommonError";
    /**
     * Letter for password compliance check failure
     */
    private static final String CHANGE_PASSWORD_POLICY_FAILURE = "ChangePasswordPolicyFailure";
    /**
     * resource id  for change password successful
     */
    private static final String CHANGE_PASSWORD_SUCCESSFUL = "ChangePasswordSuccessful";
    /**
        database offline resource id
    **/
    public static final String DATABASE_ERROR_RESOURCE_ID = "DatabaseError";

    //----------------------------------------------------------------------
    /**
       This Handles Password Compliance Checks for New Password.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
    	OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();

    	resetCargo(bus);
    	PasswordPolicyTDOIfc tdo = getPasswordPolicyTDO();

        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ChangePasswordBeanModel beanModel =
            (ChangePasswordBeanModel) ui.getModel(POSUIManagerIfc.CHANGE_PASSWORD);
        String newPassword = "";
        try
        {
            newPassword = new String(beanModel.getNewPassword(), EmployeeIfc.PASSWORD_CHARSET);
        }
        catch(UnsupportedEncodingException e)
        {
            logger.error("Unable to use correct password character set", e);
            if (logger.isDebugEnabled())
                logger.debug("Defaulting to system character set: " + ui.getInput());
            newPassword = new String(beanModel.getNewPassword());
        }

        EmployeeIfc employee = cargo.getSelectedEmployee();

        // for audit logging

        UserEvent ev = (UserEvent)AuditLoggingUtils.createLogEvent(UserEvent.class, AuditLogEventEnum.CHANGE_PASSWORD);
        AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();

        ev.setUserId(employee.getLoginID());
        ev.setStoreId(employee.getStoreID());
        ev.setMessageDate(new Date());
        ev.setEventType(AuditLogEventEnum.CHANGE_PASSWORD);
        ev.setEventOriginator("ValidateLoginSite.arrive");
        ev.setRegisterNumber(cargo.getRegister().getWorkstation().getWorkstationID());

        try
        {
            employee = tdo.readPasswordHistory(employee);
        }
        catch(DataException de)
        {
        	//read password history failed
        	displayDatabaseError(bus);
        	return;
        }

        // Use EncryptionManager to get the length of the desired password.
        KeyStoreEncryptionManagerIfc cryptoManager = null;
        cryptoManager = (KeyStoreEncryptionManagerIfc)bus.getManager(KeyStoreEncryptionManagerIfc.TYPE);

        //update employee password and password history with hashed password
        EmployeeUtilities.hashAndSetPassword(cryptoManager, employee, newPassword);

        //password compliance evaluation starts

        PasswordEvaluationResultEnum result = tdo.retrievePasswordComplianceStatus(newPassword,
        		                                                                   employee,
        		                                                                   bus);
        boolean hasSpecialCharacters = false;
        if (newPassword != null)
        {
            char[] buf = newPassword.toCharArray();
            for (int i = 0; (i < buf.length); ++i)
            {
                if (!(buf[i] >= 'A' && buf[i] <= 'Z') && !(buf[i] >= 'a' && buf[i] <= 'z')
                        && !(buf[i] >= '0' && buf[i] <= '9'))
                {
                    hasSpecialCharacters = true;
                    break;
                }
            }
            Util.flushCharArray(buf);
        }
        newPassword = null; // no more references

        if (result.equals(PasswordEvaluationResultEnum.SUCCESS)  && !hasSpecialCharacters)
        {
            employee.setPasswordChangeRequired(false);
            employee.setPasswordCreationDate(new Date());
            try
            {
        	    tdo.updateEmployeeInsertPasswordHistory(employee);
            }
            catch(DataException de)
            {
            	displayDatabaseError(bus);
            	return;
            }

        	//If all password compliance checks passed,from change password use case we return to the point where
        	//system stores login information.
        	String resourceIdChangeSuccessful = CHANGE_PASSWORD_SUCCESSFUL;
        	displayDialog(bus,
        			      resourceIdChangeSuccessful,
    		              null,
    		              CommonLetterIfc.SUCCESS,
    		              DialogScreensIfc.ACKNOWLEDGEMENT);

			if (!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
			{
				auditService.logStatusSuccess(ev);
			}


        }
        else if(result.equals(PasswordEvaluationResultEnum.DUPLICATE) ||
        		result.equals(PasswordEvaluationResultEnum.TOO_FEW_ALPHA) ||
        		result.equals(PasswordEvaluationResultEnum.TOO_FEW_NUM) ||
        		result.equals(PasswordEvaluationResultEnum.TOO_LONG) ||
        		result.equals(PasswordEvaluationResultEnum.TOO_SHORT) || hasSpecialCharacters)
        {
        	String resourceId = CHANGE_PASSWORD_POLICY_COMMON_ERROR;
        	SecurityManagerIfc securityManager = null;
            securityManager = (SecurityManagerIfc)bus.getManager(SecurityManagerIfc.TYPE);
            displayDialog(bus,
            		      resourceId,
            		      securityManager.invalidPasswordCommonMessageArgs(employee.getEmployeeCompliance()),
            		      CHANGE_PASSWORD_POLICY_FAILURE,
            		      DialogScreensIfc.ERROR);

			if (!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
			{
				auditService.logStatusFailure(ev);
			}
        }

//          for auditloggging

        else if (result.equals(PasswordEvaluationResultEnum.FAILURE))
        {
        	logger.info("Unknown Password Compliance Evaluation Result Failure");
			if (!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
			{
				auditService.logStatusFailure(ev);
			}
        }
        //password compliance evaluation ends
    }


   //----------------------------------------------------------------------
    /**
     * Displays Error Dialog
     * @param bus reference to bus
     * @param resourceId resource id
     * @param args arguments
     * @param type type of dialog. Uses ERROR or ACKNOWLEDGEMENT
     * @param buttonLetter button letter
     */
    //----------------------------------------------------------------------
    protected void displayDialog(BusIfc bus, String resourceId, String args[], String buttonLetter, int type)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(resourceId);
        dialogModel.setType(type);
        dialogModel.setArgs(args);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, buttonLetter);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    //----------------------------------------------------------------------
    /**
     * Displays Database Error Dialog
     * @param bus reference to bus
     */
    //----------------------------------------------------------------------
    private void displayDatabaseError(BusIfc bus)
	{
		String args[] = new String[1];
		UtilityManagerIfc utility =
		  (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		args[0] = utility.getErrorCodeString(DataException.UNKNOWN);
		displayDialog(bus,DATABASE_ERROR_RESOURCE_ID, args, CommonLetterIfc.FAILURE, DialogScreensIfc.ERROR);
	}

    //----------------------------------------------------------------------
    /**
     * This resets cargo for settings that will be set again as needed.
     * @param bus reference to bus
     */
    //----------------------------------------------------------------------
    private void resetCargo(BusIfc bus)
	{
		OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
		cargo.setLoginValidationChangePassword(false);
	}

     /**
        Creates Instance of Password Policy TDO.
        @return PasswordPolicyTDOIfc instance of Password Policy TDO
     **/
     private PasswordPolicyTDOIfc getPasswordPolicyTDO()
     {  
         return Utility.getUtil().getPasswordPolicyTDO();         
     }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  EvaluateChangePasswordSite (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()
}
