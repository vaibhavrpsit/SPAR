/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeeadd/EmployeeAddInformationEnteredAisle.java /main/14 2013/04/05 16:39:02 subrdey Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    subrdey   04/05/13 - Reading temp employee valid day from user
 *                         input(textbox) earlier it was combobox.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  10   360Commerce 1.9         3/5/2008 2:54:53 PM    Anil Bondalapati
 *       updated to fix the display of storeID on the backoffice
 *  9    360Commerce 1.8         11/12/2007 2:14:22 PM  Tony Zgarba
 *       Deprecated all existing encryption APIs and migrated the code to the
 *       new encryption API.
 *  8    360Commerce 1.7         10/12/2006 8:17:49 AM  Christian Greene Adding
 *        new functionality for PasswordPolicy.  Employee password will now be
 *        persisted as a byte[] in hexadecimal.  Updates include UI changes,
 *       persistence changes, and AppServer configuration changes.  A database
 *        rebuild with the new SQL scripts will be required.
 *  7    360Commerce 1.6         9/29/2006 11:36:14 AM  Christian Greene
 *       determine password length from parameters and pass to secmgr
 *  6    360Commerce 1.5         9/27/2006 4:58:07 PM   Christian Greene Add
 *       functionality to display temp password after adding a standard
 *       employee
 *  5    360Commerce 1.4         9/26/2006 9:23:41 AM   Christian Greene move
 *       setting of password to new aisle
 *  4    360Commerce 1.3         4/2/2006 11:53:41 PM   Dinesh Gautam   Added
 *       code for new fields ‘Employee login Id’ & ‘Verify Password’
 *  3    360Commerce 1.2         3/31/2005 4:27:56 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:21:16 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:10:48 PM  Robert Pearse   
 *
 * Revision 1.7  2004/09/23 00:07:14  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.6  2004/07/26 23:47:07  jriggins
 * @scr 5759 Added logic to display a dialog in the event that a user is attempting to add a standard employee using an ID that is in the temporary employee range.
 *
 * Revision 1.5  2004/05/27 18:39:18  tmorris
 * @scr 3931 -Status index in drop-down box and Employee login status did not match.
 *
 * Revision 1.4  2004/04/08 20:33:03  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * Rev 1.1 Dec 22 2003 17:12:34 jriggins Refactored traverse(). Added logic to
 * support newly-added EmployeeIfc fields. Resolution for 3597: Employee 7.0
 * Updates
 *
 * Rev 1.0 Aug 29 2003 15:59:08 CSchellenger Initial revision.
 *
 * Rev 1.3 Apr 16 2003 19:18:16 baa add status field Resolution for POS
 * SCR-2165: System crashes if FIND or ADD is selected from blank MBC Customer
 * screen
 *
 * Rev 1.2 Dec 19 2002 11:22:56 baa add employee locale support Resolution for
 * POS SCR-1843: Multilanguage support
 *
 * Rev 1.0 Apr 29 2002 15:24:04 msg Initial revision.
 *
 * Rev 1.0 Mar 18 2002 11:32:08 msg Initial revision.
 *
 * Rev 1.2 07 Dec 2001 09:20:02 epd Defaults new employee to active status
 * Resolution for POS SCR-345: Added new emp. Used new emp to logon to POS
 * 'Invalid Assoc'
 *
 * Rev 1.1 27 Oct 2001 08:45:34 mpm Merged employee changes from Virginia ABC
 * demonstration.
 *
 * Rev 1.0 Sep 21 2001 11:23:24 msg Initial revision.
 *
 * Rev 1.1 Sep 17 2001 13:07:46 msg header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeeadd;

import java.util.Calendar;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.EmployeeTypeEnum;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.PasswordCargoIfc;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.EmployeeMasterBeanModel;

/**
 * The EmployeeAddInformationEntered aisle moves the data entered into cargo
 * and mails a DoSearch letter.
 *
 * @version $Revision: /main/14 $
 */
public class EmployeeAddInformationEnteredAisle
    extends PosLaneActionAdapter
    implements LaneActionIfc
{
    /** This id is used to tell the compiler not to generate a new serialVersionUID. */
    static final long serialVersionUID = 5206608836136942150L;

    /** Revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /** Class name constant */
    public static final String LANENAME = "EmployeeAddInformationEnteredAisle";

    /**
     * The EmployeeAddInformationEntered aisle moves the data entered into
     * cargo and mails a DoSearch letter.
     *
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        String letter = EmployeeCargo.DO_SEARCH;

        // Create an EmployeeIfc instance based on the UI's model.
        EmployeeIfc newEmployee = createEmployeeFromModel(bus);

        // Check to see if this is a standard employee that has a login ID
        // in the temporary employee range
        if (EmployeeTypeEnum.STANDARD.equals(newEmployee.getType()))
        {
            // Attempt to convert the id to a number
            String loginID = newEmployee.getLoginID();
            try
            {
                long loginIDAsLong = Long.parseLong(loginID);
                if (loginIDAsLong <= EmployeeIfc.MAXIMUM_TEMP_EMPOYEE_ID)
                {
                    letter = EmployeeCargo.TEMP_ID_FOR_STANDARD;
                }
            }
            catch(NumberFormatException nfe) {}
        }

        // Set the employee in cargo.
        EmployeeCargo cargo = (EmployeeCargo)bus.getCargo();
        cargo.setEmployee(newEmployee);

        // Mail the appropriate letter to continue
        if (EmployeeCargo.TEMP_ID_FOR_STANDARD.equals(letter))
        {
            showTempIDForStandardEmployeeDialog(bus);
        }
        else
        {
            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }

    } // end traverse(BusIfc)

    /**
     * Creates a new EmployeeIfc instance from the data supplied in the
     * EmployeeMasterBeanModel.
     *
     * @param bus
     * @return EmployeeIfc instance from the data supplied in the
     *         EmployeeMasterBeanModel.
     */
    protected static EmployeeIfc createEmployeeFromModel(BusIfc bus)
    {
        EmployeeIfc newEmployee = DomainGateway.getFactory().getEmployeeInstance();
        PersonNameIfc name = DomainGateway.getFactory().getPersonNameInstance();
        EmployeeCargo cargo = (EmployeeCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // get the model for the bean from either the standard or temporary screen
        String screen = POSUIManagerIfc.EMPLOYEE_MASTER;
        EmployeeTypeEnum addEmployeeType = cargo.getAddEmployeeType();
        if (EmployeeTypeEnum.TEMPORARY.equals(addEmployeeType))
        {
            screen = POSUIManagerIfc.EMPLOYEE_MASTER_TEMP;
        }
        EmployeeMasterBeanModel model = (EmployeeMasterBeanModel)ui.getModel(screen);

        // Get name elements
        name.setFirstName(model.getFirstName());
        name.setMiddleName(model.getMiddleName());
        name.setLastName(model.getLastName());

        // Get standard employee elements
        newEmployee.setPersonName(name);
        newEmployee.setLoginID(model.getLoginIDNumber());
        // the alternate ID is currently the same as the login ID.
        newEmployee.setAlternateID(model.getLoginIDNumber());
        newEmployee.setEmployeeID(model.getIDNumber());
        
        int statusCheck = model.getSelectedStatus();
        int setStatus = 0;

        if(statusCheck == 0)
        {
            setStatus = EmployeeIfc.LOGIN_STATUS_ACTIVE;
        }
        else if(statusCheck == 1)
        {
            setStatus = EmployeeIfc.LOGIN_STATUS_INACTIVE;
        }

        newEmployee.setLoginStatus(setStatus);
        newEmployee.setPreferredLocale(
            LocaleMap.getSupportedLocales()[model.getSelectedLanguage()]);
        //get and set the role
        RoleIfc[] roles = cargo.getRoles();
        newEmployee.setRole(roles[model.getSelectedRole()]);
        newEmployee.setType(addEmployeeType);
        
        newEmployee.setStoreID(Gateway.getProperty("application", "StoreID", ""));
        
        // Get the additional temporary employee elements
        if (EmployeeTypeEnum.TEMPORARY.equals(addEmployeeType))
        {
            newEmployee.setEmployeeID(model.getIDNumber());
            

            int selectedDaysValidValue = model.getDaysValidValue();
            newEmployee.setDaysValid(selectedDaysValidValue);

            // Use the days valid setting to calculate the expiration date
            EYSDate expirationDate = new EYSDate();
            expirationDate.add(Calendar.DATE, selectedDaysValidValue);
            newEmployee.setExpirationDate(expirationDate);
        } // end if

        createTemporaryPassword(bus, newEmployee);

        return newEmployee;
    } // end createEmployeeFromModel(BusIfc)

    /**
     * Create and set a hashed password for a new employee.
     *
     * @param bus
     * @param employee
     */
    protected static void createTemporaryPassword (BusIfc bus, EmployeeIfc employee)
    {
        // Use SecurityManager to create a temporary password for this employee
        SecurityManagerIfc securityManager = null;
        securityManager = (SecurityManagerIfc) bus.getManager(SecurityManagerIfc.TYPE);

        // Use ParameterManager to get the length of the desired password.
        ParameterManagerIfc parameterManager = null;
        parameterManager = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        int length = EmployeeUtilities.getTemporaryPasswordLength(parameterManager);

        // get the temporary password
        String tempPassword = securityManager.generateTemporaryPassword(employee, length);

        // User EncryptionManager to hash the temp password
        KeyStoreEncryptionManagerIfc cryptoManager = null;
        cryptoManager = (KeyStoreEncryptionManagerIfc)bus.getManager(KeyStoreEncryptionManagerIfc.TYPE);
        EmployeeUtilities.hashAndSetPassword(cryptoManager, employee, tempPassword);

        // set the plain text password into the cargo
        PasswordCargoIfc cargo = (PasswordCargoIfc)bus.getCargo();
        cargo.setPlainTextPassword(tempPassword);

    } // end createTemporaryPassword(BusIfc, EmployeeIfc)

    /**
     * Show a dialog displaying the employee's temporary id.
     *
     * @param bus
     */
    protected void showTempIDForStandardEmployeeDialog(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the error dialog
        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID("EmployeeTempIDForStandardEmployeeError");
        model.setType(DialogScreensIfc.ERROR);
        model.setArgs(new String[]{Long.toString(EmployeeIfc.MAXIMUM_TEMP_EMPOYEE_ID)});
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, EmployeeCargo.TEMP_ID_FOR_STANDARD);

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    } // end showTempIDForStandardEmployeeDialog(BusIfc)

} // end class EmployeeAddInformationEnteredAisle
