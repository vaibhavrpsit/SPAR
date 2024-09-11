/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/UpdateEmployeePasswordAdapter.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * $ 5    360Commerce 1.4         11/12/2007 2:14:22 PM  Tony Zgarba
 * $      Deprecated all existing encryption APIs and migrated the code to the
 * $       new encryption API.
 * $ 4    360Commerce 1.3         10/12/2006 8:17:49 AM  Christian Greene
 * $      Adding new functionality for PasswordPolicy.  Employee password will
 * $       now be persisted as a byte[] in hexadecimal.  Updates include UI
 * $      changes, persistence changes, and AppServer configuration changes.
 * $      A database rebuild with the new SQL scripts will be required.
 * $ 3    360Commerce 1.2         9/26/2006 3:37:35 PM   Christian Greene
 * $      update renamed UpdateEmployeePasswordAdaptor
 * $ 2    360Commerce 1.1         9/26/2006 3:33:18 PM   Christian Greene 
 * $ 1    360Commerce 1.0         9/26/2006 12:14:18 PM  Christian Greene 
 * $$ 3    360Commerce1.2         9/26/2006 3:37:35 PM   Christian Greene update
 * $      renamed UpdateEmployeePasswordAdaptor
 * $ 2    360Commerce1.1         9/26/2006 3:33:18 PM   Christian Greene
 * $ 1    360Commerce1.0         9/26/2006 12:14:18 PM  Christian Greene
 * $$ 1    360Commerce1.0         9/26/2006 12:14:18 PM  Christian Greene
 * $$$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeWriteTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeUtilities;

/**
 * New password has been entered. Now we need to confirm this password by
 * prompting again.
 */
public abstract class UpdateEmployeePasswordAdapter extends PosLaneActionAdapter
{
    /**
     * revision number of this class
     */
    public static String revisionNumber = "$Revision";

    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger.getLogger(UpdateEmployeePasswordAdapter.class);

    /**
     * Updates the new employee password and sets the new password required
     * flag to <code>false</code> in the current EmployeeIfc in cargo as well
     * as in the database
     *
     * @param employee
     *          the EmployeeIfc to update
     * @param newPassword
     *          the new password
     * @return boolean flag; <code>true</code> for success, <code>false</code>
     *         for failure
     */
    protected boolean updateEmployeePassword(
        EmployeeIfc employee,
        String newPassword)
    {
        boolean updateSuccess = false;

        try
        {
            // Use EncryptionManager to get the length of the desired password.
            KeyStoreEncryptionManagerIfc cryptoManager = null;
            cryptoManager = (KeyStoreEncryptionManagerIfc)Gateway.getDispatcher().getManager(KeyStoreEncryptionManagerIfc.TYPE);

            // Update the employee's new password and password required flag
            EmployeeUtilities.hashAndSetPassword(cryptoManager, employee, newPassword);
            employee.setPasswordChangeRequired(false);

            // Update the employee in the database
            EmployeeWriteTransaction empWriteTrans = null;
            empWriteTrans = (EmployeeWriteTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_WRITE_TRANSACTION);

            empWriteTrans.updateEmployee(employee);

            // Indicate success
            updateSuccess = true;
        }
        catch (DataException de)
        {
            logger.error(
                "Unable to update employee " + employee.getEmployeeID(),
                de);
        }

        return updateSuccess;
    }

}
