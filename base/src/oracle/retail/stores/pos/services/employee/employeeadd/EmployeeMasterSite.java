/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeeadd/EmployeeMasterSite.java /main/22 2013/04/05 16:39:02 subrdey Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    subrdey   04/05/13 - Adding valid days for temp employee in textfield
 *    mkutiana  03/21/13 - Restrict Role Display and access
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   10/25/11 - added a renderer to the EmployeeMasterBean for
 *                         renderering Locales instead of dealing with Strings
 *                         directly.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    kelesika  10/21/10 - Text change for addition of an employee
 *    kelesika  10/21/10 - Text change for addition of an employee
 *    blarsen   06/09/10 - XbranchMerge blarsen_biometrics-poc from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    blarsen   05/25/10 - Added support for new SetFingerprint button.
 *    acadar    09/07/10 - externalize supported localesz
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    tzgarba   11/05/08 - Merged with tip
 *
 * ===========================================================================
 * $Log:
 *  8    360Commerce 1.7         3/24/2008 12:35:36 PM  Deepti Sharma   merge
 *       from v12.x to trunk
 *  7    360Commerce 1.6         3/5/2008 2:54:53 PM    Anil Bondalapati
 *       updated to fix the display of storeID on the backoffice
 *  6    360Commerce 1.5         9/27/2006 4:58:08 PM   Christian Greene Add
 *       functionality to display temp password after adding a standard
 *       employee
 *  5    360Commerce 1.4         9/26/2006 9:22:03 AM   Christian Greene Added
 *       Reset Password button to site
 *  4    360Commerce 1.3         4/2/2006 11:54:40 PM   Dinesh Gautam   Added
 *       code for new fields �Employee login Id� & �Verify Password� and added
 *        logic for autogenerating employee id.
 *  3    360Commerce 1.2         3/31/2005 4:27:58 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:21:20 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:10:50 PM  Robert Pearse
 *
 * Revision 1.14  2004/07/16 16:00:40  aschenk
 * @scr 6099 - Fixed the days value field to display the saved value if there is one.  It was always showing 1.
 *
 * Revision 1.13  2004/06/17 13:52:24  dfierling
 * @scr 5320 - corrected StoreID when loading page from previously entered info
 *
 * Revision 1.12  2004/06/15 14:52:50  dfierling
 * @scr 5320 - corrected IDNumber when loading page from previously entered info
 *
 * Revision 1.11  2004/06/03 14:47:45  epd
 * @scr 5368 Update to use of DataTransactionFactory
 *
 * Revision 1.10  2004/05/20 22:54:58  cdb
 * @scr 4204 Removed tabs from code base again.
 *
 * Revision 1.9  2004/05/06 02:41:37  tfritz
 * @scr 4219 Temp employee ID is no longer being updated in training mode
 *
 * Revision 1.8  2004/04/20 13:13:10  tmorris
 * @scr 4332 -Sorted imports
 *
 * Revision 1.7  2004/04/13 02:26:35  pkillick
 * @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 * Revision 1.6  2004/04/08 20:33:03  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * Rev 1.5 Jan 12 2004 13:48:34 baa use lowercase for application groupname
 *
 * Rev 1.4 Jan 09 2004 18:05:14 baa get list from application properties
 *
 * Rev 1.3 Dec 22 2003 17:11:32 jriggins Added logic to generate an ID for a
 * temporary employee in populateModel(). Resolution for 3597: Employee 7.0
 * Updates
 *
 * Rev 1.2 Dec 17 2003 08:28:20 jriggins Refactored arrive(). Added more model
 * logic for controlling how the Employee Master screen should be displayed
 * depending on the type of employee we are dealing with.
 *
 * Rev 1.1 Dec 16 2003 15:29:14 jriggins Added support for the Add Temporary
 * Employee usecase. Resolution for 3597: Employee 7.0 Updates
 *
 * Rev 1.0 Aug 29 2003 15:59:10 CSchellenger Initial revision.
 *
 * Rev 1.3 Jul 07 2003 12:21:22 DCobb Populate status field with Active and
 * Inactive only. Resolution for POS SCR-2117: Employee Master Status field is
 * labeled as Preferred Language.
 *
 * Rev 1.2 Apr 16 2003 19:18:18 baa add status field Resolution for POS
 * SCR-2165: System crashes if FIND or ADD is selected from blank MBC Customer
 * screen
 *
 * Rev 1.1 Jan 28 2003 13:29:40 HDyer Set the languages on the
 * EmployeeMasterBeanModel before showing the screen so the language pulldown
 * is poplulated. Also fixed some deprecation warnings on Employee calls.
 * Resolution for POS SCR-1931: Preferred language pulldown is not populated on
 * add employee screen
 *
 * Rev 1.0 Apr 29 2002 15:24:06 msg Initial revision.
 *
 * Rev 1.0 Mar 18 2002 11:32:10 msg Initial revision.
 *
 * Rev 1.1 27 Oct 2001 08:45:34 mpm Merged employee changes from Virginia ABC
 * demonstration.
 *
 * Rev 1.0 Sep 21 2001 11:23:20 msg Initial revision.
 *
 * Rev 1.1 Sep 17 2001 13:07:46 msg header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeeadd;

import java.util.ArrayList;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeWriteTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.EmployeeTypeEnum;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.EmployeeCargoIfc;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EmployeeMasterBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 *
 * The EmployeeMaster site allows the user to enter the new employee
 * information.
 *
 * @version $Revision: /main/22 $
 */
/**
 * @author mkutiana
 *
 */
/**
 * @author mkutiana
 *
 */
/**
 * @author mkutiana
 *
 */
public class EmployeeMasterSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 2874986860183338502L;
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/22 $";

    /**
     * class name constant
     */
    public static final String SITENAME = "EmployeeMasterSite";

    /**
     * The string used to look up the maximum days valid parameter.
     */
    public static final String TEMP_EMPLOYEE_DAYS_VALID = "TempEmployeeDaysValid";

    /**
     * The string used to list the valid default values"
     */
    public static final String DEFAULT_DAYS_VALID = "1,2,3,4,5,6,7,30";
    /**
     * The string used to look up the property group
     */
    public static final String APPLICATION_PROPERTIES = "application";

    protected static final String EMP_ADD_PROMPT_TAG   = "EmployeeMasterAddPrompt";
    protected static final String EMP_ADD_PROMPT   = "Enter information, then press Next.";

    /**
     * The EmployeeMaster site allows the user to enter the new employee
     * information.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        EmployeeCargoIfc cargo = (EmployeeCargoIfc)bus.getCargo();

        // Populate the model
        EmployeeMasterBeanModel model = null;
		boolean goodToGo = true;
        try
        {
            model = populateModel(bus);
        }
        catch (DataException de)
        {
			goodToGo = false;
            // log the error; set the error code in the cargo for future use.
            cargo.setDataExceptionErrorCode(de.getErrorCode());
            logger.error(
                "Employee generate ID error: " + de.getMessage() + "");
            bus.mail(CommonLetterIfc.DB_ERROR);
        }
        if(goodToGo)
        {
			// Show the Employee Master screen. We use a different overlay based
			// upon the employee type.
			String screen = POSUIManagerIfc.EMPLOYEE_MASTER;
			if (EmployeeTypeEnum.TEMPORARY.equals(model.getEmployeeType()))
				screen = POSUIManagerIfc.EMPLOYEE_MASTER_TEMP;

			POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
			NavigationButtonBeanModel nModel = model.getLocalButtonBeanModel();
			if (nModel == null)
			{
				nModel = new NavigationButtonBeanModel();
				model.setLocalButtonBeanModel(nModel);
			}

			// Send disable to the reset button on the Local Navigation button bean.
            nModel.setButtonEnabled("ResetPassword", false);
            nModel.setButtonEnabled("SetFingerprint", false);

			// show the employee master screen
			ui.showScreen(screen, model);
		}
    }

    /**
     * Popluate the UI model
     *
     * @param bus
     * @return EmployeeMasterBeanModel which serves as the model for the UI
     */
    protected EmployeeMasterBeanModel populateModel(BusIfc bus)
        throws DataException
    {
        EmployeeCargo cargo = (EmployeeCargo)bus.getCargo();
        UtilityManagerIfc utility =
            (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        EmployeeMasterBeanModel model = new EmployeeMasterBeanModel();

        RegisterIfc register = cargo.getRegister();
        boolean trainingModeOn = false;

        if (register != null)
        {
            trainingModeOn = register.getWorkstation().isTrainingMode();
        }

        model.setInTraining(trainingModeOn);
        
        RoleIfc operatorRole = cargo.getOperator().getRole();                
        ArrayList<RoleIfc> filteredRoles = (ArrayList<RoleIfc>)operatorRole.getFilteredRoles(cargo.getRoles());
        
        String[] roleTitles = new String[filteredRoles.size()];        
        RoleIfc[] roles = filteredRoles.toArray(new RoleIfc[filteredRoles.size()]);
        
        int j=0;
        for (RoleIfc aRole : filteredRoles)
        {
            roleTitles[j] = aRole.getTitle(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
            j++;
        }

        cargo.setRoleTitles(roleTitles);        
        cargo.setRoles(roles);

        model.setRoles(roleTitles);

        String[] statusValues = new String[2];
        statusValues[0] =
            utility.retrieveCommonText(
                EmployeeIfc
                    .LOGIN_STATUS_DESCRIPTORS[EmployeeIfc
                    .LOGIN_STATUS_ACTIVE]);
        statusValues[1] =
            utility.retrieveCommonText(
                EmployeeIfc
                    .LOGIN_STATUS_DESCRIPTORS[EmployeeIfc
                    .LOGIN_STATUS_INACTIVE]);
        model.setStatusValues(statusValues);

        Locale[] supportedLocales = LocaleMap.getSupportedLocales();
        model.setSupportedLanguages(supportedLocales);

        // We need to set the some values based on the employee type
        EmployeeTypeEnum addEmployeeType = cargo.getAddEmployeeType();
        if (EmployeeTypeEnum.TEMPORARY.equals(addEmployeeType))
        {
            // IDs aren't editable for temp employees
            model.setEditableIDNumber(false);
        }

        // if there's an employee in cargo, set the screen to that employee
        if (cargo.getEmployee() != null)
        {
            model.setIDNumber(cargo.getEmployee().getEmployeeID());
            model.setFirstName(
                cargo.getEmployee().getPersonName().getFirstName());
            model.setMiddleName(
                cargo.getEmployee().getPersonName().getMiddleName());
            model.setLastName(
                cargo.getEmployee().getPersonName().getLastName());
            model.setIDNumber(cargo.getEmployee().getEmployeeID());
            model.setLoginIDNumber(cargo.getEmployee().getLoginID());
            model.setEmployeeType(cargo.getEmployee().getType());
            Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
            String role = cargo.getEmployee().getRole().getTitle(uiLocale);

            // Find index for current role
            boolean found = false;
            for (int i = 0; i < roleTitles.length && !found; i++)
            {
                if (role.equals(roleTitles[i]))
                {
                    found = true;
                    model.setSelectedRole(i);
                }
            }
        }
        else
        {
            model.setEmployeeType(addEmployeeType);
            EmployeeWriteTransaction trans = null;
            trans = (EmployeeWriteTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_WRITE_TRANSACTION);
            Integer employeeID = Integer.valueOf(1);
            if (trainingModeOn && EmployeeTypeEnum.STANDARD.equals(addEmployeeType))
            {
            	employeeID = Integer.valueOf(10001);
            }
            else
            {
              	employeeID = trans.generateEmployeeID(addEmployeeType);
            }

            model.setIDNumber(employeeID.toString());
            if (cargo.getEmployee() != null)
            {
                model.setDaysValidValue(cargo.getEmployee().getDaysValid());
            }
            else
            {
                model.setDaysValidValue(1);
            }
        }

        PromptAndResponseModel prm = new PromptAndResponseModel();
        String promptTextTag = EMP_ADD_PROMPT_TAG;
        String promptText = EMP_ADD_PROMPT;
        promptText = utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                BundleConstantsIfc.EMPLOYEE_BUNDLE_NAME,
                promptTextTag,
                promptText);
        prm.setPromptText(promptText);
        model.setPromptAndResponseModel(prm);

        return model;
    }


    /**
     * Get a String array of valid days from the value held in the
     * TempEmployeeDaysValid parameter
     *
     * @param BusIfc bus
     * @return String array containing the valid days
     */
    protected static String[] populateDaysValid(BusIfc bus)
    {
        //initialize to default
        String[] daysValidArray =
            new String[] { "1", "2", "3", "4", "5", "6", "7", "30" };

        try
        {
            ArrayList<String> validValues =
                ReturnUtilities.getPropertyValues(
                    APPLICATION_PROPERTIES,
                    TEMP_EMPLOYEE_DAYS_VALID,
                    DEFAULT_DAYS_VALID);
            // parse list of values to make sure they are all numeric

            if (validValues != null && validValues.size() > 0)
            {
                for (String value : validValues)
                {
                    // Convert the string to an integer to varify that it is numeric.
                    Integer.parseInt(value);
                }
                daysValidArray = new String[validValues.size()];
                validValues.toArray(daysValidArray);
            }
        }
        catch (NumberFormatException e)
        {
            //  list of values is not numeric.
            logger.error(
                "TempEmployeeDaysValid  property has invalid data. ["
                    + daysValidArray.toString()
                    + "]. Expected numeric values only");
        }

        return (daysValidArray);
    }

    /**
     * Returns a string representation of this object.
     *
     * @param none
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult =
            new String(
                "Class: "
                    + SITENAME
                    + " (Revision "
                    + getRevisionNumber()
                    + ")"
                    + hashCode());

        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     *
     * @param none
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}
