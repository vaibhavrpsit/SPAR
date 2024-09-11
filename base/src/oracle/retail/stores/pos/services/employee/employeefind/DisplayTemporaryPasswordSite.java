/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/DisplayTemporaryPasswordSite.java /main/13 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    hyin      02/25/11 - move ej method to utility class
 *    hyin      02/24/11 - change based on review comment
 *    hyin      02/24/11 - add journal entry for this
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * $ 12   360Commerce 1.11        3/6/2008 5:45:05 AM    Chengegowda Venkatesh
 * $      For CR 30275
 * $ 11   360Commerce 1.10        1/10/2008 7:58:51 AM   Manas Sahu      Event
 * $      Originator Changes
 * $ 10   360Commerce 1.9         1/7/2008 7:57:51 AM    Chengegowda Venkatesh
 * $      Audit log changes
 * $ 9    360Commerce 1.8         11/12/2007 2:14:22 PM  Tony Zgarba
 * $      Deprecated all existing encryption APIs and migrated the code to the
 * $       new encryption API.
 * $ 8    360Commerce 1.7         10/13/2006 5:38:11 PM  Christian Greene
 * $      Commented out adding password to history until clarification on when
 * $       password becomes part of the history.
 * $ 7    360Commerce 1.6         10/12/2006 8:20:59 AM  Christian Greene
 * $      Adding new functionality for PasswordPolicy.  Employee password will
 * $       now be persisted as a byte[] in hexadecimal.  Updates include UI
 * $      changes, persistence changes, and AppServer configuration changes.
 * $      A database rebuild with the new SQL scripts will be required.
 * $ 6    360Commerce 1.5         10/11/2006 9:51:21 AM  Rohit Sachdeva  21237:
 * $       Password Policy Updates
 * $ 5    360Commerce 1.4         9/29/2006 3:16:02 PM   Christian Greene
 * $      password change is required after a reset
 * $ 4    360Commerce 1.3         9/29/2006 11:36:14 AM  Christian Greene
 * $      determine password length from parameters and pass to secmgr
 * $ 3    360Commerce 1.2         9/26/2006 3:57:02 PM   Christian Greene had
 * $      to move method from PosLaneAdapter up into this class
 * $ 2    360Commerce 1.1         9/26/2006 3:37:35 PM   Christian Greene
 * $      update renamed UpdateEmployeePasswordAdaptor
 * $ 1    360Commerce 1.0         9/26/2006 9:43:04 AM   Christian Greene 
 * $$ 7    360Commerce1.6         10/12/2006 8:20:59 AM  Christian Greene Adding
 * $      new functionality for PasswordPolicy.  Employee password will now be
 * $      persisted as a byte[] in hexadecimal.  Updates include UI changes,
 * $      persistence changes, and AppServer configuration changes.  A database
 * $      rebuild with the new SQL scripts will be required.
 * $ 6    360Commerce1.5         10/11/2006 9:51:21 AM  Rohit Sachdeva  21237:
 * $      Password Policy Updates
 * $ 5    360Commerce1.4         9/29/2006 3:16:02 PM   Christian Greene
 * $      password change is required after a reset
 * $ 4    360Commerce1.3         9/29/2006 11:36:14 AM  Christian Greene
 * $      determine password length from parameters and pass to secmgr
 * $ 3    360Commerce1.2         9/26/2006 3:57:02 PM   Christian Greene had to
 * $      move method from PosLaneAdapter up into this class
 * $ 2    360Commerce1.1         9/26/2006 3:37:35 PM   Christian Greene update
 * $      renamed UpdateEmployeePasswordAdaptor
 * $ 1    360Commerce1.0         9/26/2006 9:43:04 AM   Christian Greene
 * $$ 5    360Commerce1.4         9/29/2006 3:16:02 PM   Christian Greene
 * $      password change is required after a reset
 * $ 4    360Commerce1.3         9/29/2006 11:36:14 AM  Christian Greene
 * $      determine password length from parameters and pass to secmgr
 * $ 3    360Commerce1.2         9/26/2006 3:57:02 PM   Christian Greene had to
 * $      move method from PosLaneAdapter up into this class
 * $ 2    360Commerce1.1         9/26/2006 3:37:35 PM   Christian Greene update
 * $      renamed UpdateEmployeePasswordAdaptor
 * $ 1    360Commerce1.0         9/26/2006 9:43:04 AM   Christian Greene
 * $$ 4    360Commerce1.3         9/29/2006 11:36:14 AM  Christian Greene
 * $      determine password length from parameters and pass to secmgr
 * $ 3    360Commerce1.2         9/26/2006 3:57:02 PM   Christian Greene had to
 * $      move method from PosLaneAdapter up into this class
 * $ 2    360Commerce1.1         9/26/2006 3:37:35 PM   Christian Greene update
 * $      renamed UpdateEmployeePasswordAdaptor
 * $ 1    360Commerce1.0         9/26/2006 9:43:04 AM   Christian Greene
 * $$ 3    360Commerce1.2         9/26/2006 3:57:02 PM   Christian Greene had to
 * $      move method from PosLaneAdapter up into this class
 * $ 2    360Commerce1.1         9/26/2006 3:37:35 PM   Christian Greene update
 * $      renamed UpdateEmployeePasswordAdaptor
 * $ 1    360Commerce1.0         9/26/2006 9:43:04 AM   Christian Greene
 * $$ 2    360Commerce1.1         9/26/2006 3:37:35 PM   Christian Greene update
 * $      renamed UpdateEmployeePasswordAdaptor
 * $ 1    360Commerce1.0         9/26/2006 9:43:04 AM   Christian Greene
 * $$ 1    360Commerce1.0         9/26/2006 9:43:04 AM   Christian Greene
 * $$$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeefind;

import java.util.Date;

import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeWriteTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.EmployeeTypeEnum;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.EmployeeCargoIfc;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This message is displayed if an employee?s password has been reset, a
 * temporary employee?s password has been reset, an employee has been added or
 * if a temporary employee has been added.
 * 
 * @version $Revision: /main/13 $
 */
public class DisplayTemporaryPasswordSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 3507646216227119065L;

    public static final String SITENAME = "DisplayTemporaryPasswordSite";

    /**
     * Arrive at the site. Display either the temp password or a db error.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Retrieve the data from cargo
        EmployeeCargoIfc cargo = (EmployeeCargoIfc) bus.getCargo();
        EmployeeIfc employee = cargo.getEmployee();
        // Using "generic dialog bean".
        DialogBeanModel model = new DialogBeanModel();

        // reset the password.
        // Use SecurityManager to determine if user is logged in
        SecurityManagerIfc securityManager = null;
        securityManager = (SecurityManagerIfc) bus.getManager(SecurityManagerIfc.TYPE);

        // Use ParamterManager to get the length of the desired password.
        ParameterManagerIfc parameterManager = null;
        parameterManager = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        int length = EmployeeUtilities.getTemporaryPasswordLength(parameterManager);

        String tempPassword = securityManager.generateTemporaryPassword(employee, length);

        if (!updateEmployeePassword(employee, tempPassword))
        {
            configureModelForDBError(model);
        }
        else
        {
            configureModelForTemporaryPassword(model, employee, tempPassword);
        }

        doAuditLog(AuditLogEventEnum.RESET_EMPLOYEE_PASSWORD, cargo, employee.getEmployeeID());
        logEJEntry(bus, cargo.getEmployee().getLoginID());

        // set and display the model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

	/**
	 * This method writes EJournal entry for Employee Password Reset
	 */
    private void logEJEntry(BusIfc bus, String empID)
    {
        // create the EJournal String
        StringBuffer journalString = new StringBuffer();
        Object[] dataArgs = new Object[2];
        dataArgs[0] = empID;
        journalString.append(I18NHelper.getString(
                I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.FIND_EMPLOYEE_LABEL, dataArgs));

        journalString.append(Util.EOL);
        journalString.append(I18NHelper.getString(
                I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.EMPLOYEE_PASSWORD_CHANGED, dataArgs));

        EmployeeFindUtility.logEJEntry(bus, journalString.toString());
    }
    
    private void doAuditLog(AuditLogEventEnum auditLogEventEnum, EmployeeCargoIfc cargo, String loginID)
    {
        if (cargo instanceof EmployeeCargo)
        {
            EmployeeCargo employeeCargo = (EmployeeCargo) cargo;
            if (!CheckTrainingReentryMode.isTrainingRetryOn(employeeCargo.getRegister()))
            {
                if (EmployeeTypeEnum.TEMPORARY.equals(employeeCargo.getEmployee().getType()))
                {
                    EmployeeFindUtility.logAuditEvent(true, employeeCargo, AuditLogEventEnum.RESET_TEMP_EMPLOYEE_PASSWORD, 
                            EventOriginatorInfoBean.getEventOriginator());
                }
                else
                {
                    EmployeeFindUtility.logAuditEvent(true, employeeCargo, AuditLogEventEnum.RESET_EMPLOYEE_PASSWORD, 
                            EventOriginatorInfoBean.getEventOriginator());
                }
            }
        }
    }

    protected void configureModelForDBError(DialogBeanModel model)
    {
        model.setResourceID("PasswordSaveError");
        model.setType(DialogScreensIfc.ERROR);
    }

    protected void configureModelForTemporaryPassword(DialogBeanModel model, EmployeeIfc employee,
            String plainTextNewPassword)
    {
        String name = employee.getPersonName().getFirstLastName();

        model.setResourceID("DisplayTemporaryPassword");
        model.setArgs(new String[] { name, plainTextNewPassword, name });
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CONTINUE);
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
    }

    /**
     * Updates the new employee password and sets the new password required flag
     * to <code>true</code> in the current EmployeeIfc in cargo as well as in
     * the database
     * 
     * @param employee the EmployeeIfc to update
     * @param newPassword the new password
     * @return boolean flag; <code>true</code> for success, <code>false</code>
     *         for failure
     */
    protected boolean updateEmployeePassword(EmployeeIfc employee, String tempPassword)
    {
        boolean updateSuccess = false;

        try
        {
            // byte[] oldPassword = employee.getPasswordBytes();

            // Use EncryptionManager to get the length of the desired password.
            KeyStoreEncryptionManagerIfc cryptoManager = null;
            cryptoManager = (KeyStoreEncryptionManagerIfc) Gateway.getDispatcher().getManager(
                    KeyStoreEncryptionManagerIfc.TYPE);

            // Update the employee's new password and password required flag
            EmployeeUtilities.hashAndSetPassword(cryptoManager, employee, tempPassword);
            employee.setPasswordChangeRequired(true);
            employee.setPasswordCreationDate(new Date());
            int numberOfFailedPasswords = 0;
            employee.setNumberFailedPasswords(numberOfFailedPasswords);
            // set old password to history of passwords //TODO are current
            // already part of the history? CMG
            // employee.getEmployeeCompliance().getPasswordHistory().add(oldPassword);

            // Update the employee in the database
            EmployeeWriteTransaction empWriteTrans = null;
            empWriteTrans = (EmployeeWriteTransaction) DataTransactionFactory
                    .create(DataTransactionKeys.EMPLOYEE_WRITE_TRANSACTION);

            empWriteTrans.updateEmployee(employee);

            // Indicate success
            updateSuccess = true;
        }
        catch (DataException de)
        {
            logger.error("Unable to update employee " + employee.getEmployeeID(), de);
        }

        return updateSuccess;
    }

} // end class DisplayTemporaryPasswordSite

