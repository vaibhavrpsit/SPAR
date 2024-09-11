/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/EditSelectedEmployeeSite.java /main/25 2013/04/05 16:39:03 subrdey Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    subrdey   04/05/13 - Calculating valid days of temp employee using system
 *                         date and expiration date.
 *    mkutiana  03/21/13 - Restrict Role Display and access
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   10/25/11 - added a renderer to the EmployeeMasterBean for
 *                         renderering Locales instead of dealing with Strings
 *                         directly.
 *    blarsen   02/25/11 - replaced temp fix in fingerprintReaderExists() with
 *                         real code to check reader online status via new
 *                         fingerprint reader device action.
 *    blarsen   02/23/11 - Updating fingerprintReaderExists() method with a
 *                         temporary fix to always return true.
 *    mkutiana  02/22/11 - disable the reset password button if fingerprint
 *                         logins are turned on
 *    blarsen   02/14/11 - Added check to hide set-fingerprint button if a
 *                         fingerprint reader is not configured.
 *    mkutiana  02/10/11 - Disable the Set Fingerprint button if offline from
 *                         the RemoteDT (StoreDB)
 *    mkutiana  02/09/11 - Disable the Set Fingerprint button if offline to DB
 *                         (store)
 *    hyin      02/04/11 - add security check for set fp button
 *    blarsen   06/09/10 - XbranchMerge blarsen_biometrics-poc from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    blarsen   05/25/10 - Added support for new Set-Fingerprint button.
 *    acadar    09/07/10 - externalize supported localesz
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    kkhirema  02/26/09 - Modified arrive method to set the selected language
 *                         based on the locale if the preferred language is
 *                         missing
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         3/5/2008 2:54:53 PM    Anil Bondalapati
 *         updated to fix the display of storeID on the backoffice
 *    8    360Commerce 1.7         1/10/2008 7:57:12 AM   Manas Sahu      Event
 *          Originator Changes
 *    7    360Commerce 1.6         11/2/2006 9:39:47 AM   Christian Greene
 *         Adding ResetPassword as a security access role.
 *    6    360Commerce 1.5         9/27/2006 4:58:08 PM   Christian Greene Add
 *         functionality to display temp password after adding a standard
 *         employee
 *    5    360Commerce 1.4         9/26/2006 9:18:37 AM   Christian Greene
 *         password changing moved to new site
 *    4    360Commerce 1.3         4/2/2006 11:56:16 PM   Dinesh Gautam   Added
 *          code for new fields ???Employee login Id??? & ???Verify Password???
 *    3    360Commerce 1.2         3/31/2005 4:27:54 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:13 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse
 *
 *   Revision 1.8.2.1  2004/10/21 16:16:01  jdeleau
 *   @scr 7436 Fix crash on looking up employee by ID
 *
 *   Revision 1.8  2004/08/23 16:15:58  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.7  2004/07/07 23:33:17  cdb
 *   @scr 6023 Corrected malfunctioning internationalization code.
 *
 *   Revision 1.6  2004/06/25 16:20:45  jeffp
 *   @scr 5738 - Added checks to see when editing a temporary employee
 *
 *   Revision 1.5  2004/05/20 22:54:58  cdb
 *   @scr 4204 Removed tabs from code base again.
 *
 *   Revision 1.4  2004/05/01 16:47:26  tfritz
 *   @scr 3904 Preferred Language now shows the one saved for the specific employee
 *
 *   Revision 1.3  2004/02/12 16:50:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:47  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Jan 15 2004 10:02:24   mrm
 * remove println
 * Resolution for 3713: Change Employee Status Confirm = No, does not return the status to the previous state.
 *
 *    Rev 1.1   Jan 14 2004 14:52:38   mrm
 * restore previous status
 * Resolution for 3713: Change Employee Status Confirm = No, does not return the status to the previous state.
 *
 *    Rev 1.0   Aug 29 2003 15:59:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Jul 15 2003 12:09:14   mrm
 * Remove Unknown from status list
 * Resolution for POS SCR-3150: When editing employee information the status field on the Employee Master page includes 'unknown' as a selection.
 *
 *    Rev 1.3   Apr 16 2003 19:18:18   baa
 * add status field
 * Resolution for POS SCR-2165: System crashes if FIND or ADD is selected from blank MBC Customer screen
 *
 *    Rev 1.2   Jan 30 2003 16:59:50   baa
 * add employe locale preference for offline flow
 * Resolution for POS SCR-1843: Multilanguage support
 *
 *    Rev 1.1   Dec 18 2002 17:40:20   baa
 * add employee preferred locale support
 * Resolution for POS SCR-1843: Multilanguage support
 *
 *    Rev 1.0   Apr 29 2002 15:23:40   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:32:20   msg
 * Initial revision.
 *
 *    Rev 1.1   27 Oct 2001 08:45:34   mpm
 * Merged employee changes from Virginia ABC demonstration.
 *
 *    Rev 1.0   Sep 21 2001 11:23:38   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeefind;

import java.util.ArrayList;
import java.util.Locale;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.EmployeeTypeEnum;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.DataManagerOnlineStatus;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EmployeeMasterBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;

/**
 * The EditSelectedEmployee site displays the Employee Master screen where the
 * use can edit or view the selected employee.
 * 
 * @version $Revision: /main/25 $
 */
public class EditSelectedEmployeeSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -4163316142578381484L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/25 $";

    /**
     * class name constant
     */
    public static final String SITENAME = "EditSelectedEmployeeSite";

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
    
    // Milliseconds in a day = 24*60*60*1000 = 86400000
    public static final long MILLISECONDS_IN_DAY = 86400000;

    /**
     * The EditSelectedEmployee site displays the Employee Master screen where
     * the use can edit or view the selected employee.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Need to change Cargo type to <ServiceName>Cargo
        String screen = POSUIManagerIfc.EMPLOYEE_MASTER;

        EmployeeCargo cargo = (EmployeeCargo) bus.getCargo();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        //setOriginalEmployee so it can be compared after changes.
        //cargo.setOriginalEmployee((EmployeeIfc)cargo.getEmployee().clone());

        // if there have been changes to the Employee
        if (!cargo.getEmployee().equals(cargo.getOriginalEmployee()))
        {
            // Did employee status change?
            if (cargo.getEmployee().getLoginStatus() != cargo.getOriginalEmployee().getLoginStatus())
            {
                // restore original status
                cargo.getEmployee().setLoginStatus(cargo.getOriginalEmployee().getLoginStatus());
            }
        }

        // Need to change screen ID and bean type
        EmployeeMasterBeanModel model = new EmployeeMasterBeanModel();
        
        //Get the filtered list of roles that the operator/logged in user has access to.
        RoleIfc operatorRole = cargo.getOperator().getRole();        
        ArrayList<RoleIfc> filteredRoles = (ArrayList<RoleIfc>)operatorRole.getFilteredRoles(cargo.getRoles());
        
        //While editting an employee add his role to the filtered/Diplayed list
        RoleIfc role = cargo.getEmployee().getRole();
        if (! filteredRoles.contains(role))
        {
            filteredRoles.add(role);
        }
           
        String[] roleTitles = new String[filteredRoles.size()];
        RoleIfc[] roles = filteredRoles.toArray(new RoleIfc[filteredRoles.size()]);
                
        int j=0;
        for (RoleIfc aRole : filteredRoles)
        {
            roleTitles[j++] = aRole.getTitle(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
        }
                
        cargo.setRoleTitles(roleTitles);        
        cargo.setRoles(roles);

        model.setRoles(roleTitles);

        if (cargo.getEmployee() != null )
        {
            String[] statusValues = new String[2];
            statusValues[0] = utility.retrieveCommonText(EmployeeIfc.LOGIN_STATUS_DESCRIPTORS[EmployeeIfc.LOGIN_STATUS_ACTIVE]);
            statusValues[1] = utility.retrieveCommonText(EmployeeIfc.LOGIN_STATUS_DESCRIPTORS[EmployeeIfc.LOGIN_STATUS_INACTIVE]);

            model.setStatusValues(statusValues);
            // adjust selected status to skip over Unknown
            int index = cargo.getEmployee().getLoginStatus() - EmployeeIfc.LOGIN_STATUS_ACTIVE;
            if (index < 0)
            {
                index = 0;
            }
            model.setSelectedStatus(index);
            // if there's an employee in cargo, set the screen to that employee
            model.setIDNumber(cargo.getEmployee().getEmployeeID());

            // this check should not be needed in production,
            // currently the entire employee is not being set.
            if( cargo.getEmployee().getPersonName() != null)
            {
                model.setFirstName(cargo.getEmployee().getPersonName().getFirstName());
                model.setMiddleName(cargo.getEmployee().getPersonName().getMiddleName());
                model.setLastName(cargo.getEmployee().getPersonName().getLastName());
            }
            
            if (cargo.getEmployee().getEmployeeID()!= null)
            {
                model.setIDNumber(cargo.getEmployee().getEmployeeID());
            }
            if (cargo.getEmployee().getLoginID()!= null)
            {
                model.setLoginIDNumber(cargo.getEmployee().getLoginID());
            }

            Locale[] supportedLocales = LocaleMap.getSupportedLocales();
            Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

            model.setSupportedLanguages(supportedLocales);
            // retrieve employee's current preferrence
            Locale employeeLocale = cargo.getEmployee().getPreferredLocale();

            if (employeeLocale != null)
            {
              model.setPreferredLanguage(employeeLocale);
              Locale preferredLocale = cargo.getEmployee().getPreferredLocale();
              for (int i = 0; i < supportedLocales.length; i++)
              {
                  if (preferredLocale.equals(supportedLocales[i]))
                  {
                      model.setSelectedLanguage(i);
                  }
              }
            }
            else
            {
               model.setPreferredLanguage(uiLocale);
               for (int i = 0; i < supportedLocales.length; i++)
               {
            	   if (uiLocale.equals(supportedLocales[i]))
                   {
                       model.setSelectedLanguage(i);
                   }
               }
            }
            
            boolean found = false;
            if(role != null)
            {
                for (int i = 0; i < roles.length && !found; i++)
                {
                    if (role.getRoleID() == roles[i].getRoleID())
                    {
                        found = true;
                        model.setSelectedRole(i);
                    }
                }
            }

            // setup model if editing a temporary employee
            if (EmployeeTypeEnum.TEMPORARY.equals(cargo.getEmployee().getType()))
            {
                EYSDate systemDate = DomainGateway.getFactory().getEYSDateInstance();
                int targetDays = (int)(cargo.getEmployee().getExpirationDate().dateValue().getTime() / MILLISECONDS_IN_DAY);
                int currentDays = (int)(systemDate.dateValue().getTime() / MILLISECONDS_IN_DAY);
                model.setDaysValidValue(targetDays - currentDays + 1);
                model.setEmployeeType(cargo.getEmployee().getType());
                screen = POSUIManagerIfc.EMPLOYEE_MASTER_TEMP;
            }

            model.setEditableIDNumber(false);
        }
        else
        {
            if (logger.isInfoEnabled()) logger.info( "" + SITENAME + "employee not set" );
        }

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        NavigationButtonBeanModel nModel = model.getLocalButtonBeanModel();
        if (nModel == null) {
            nModel = new NavigationButtonBeanModel();
            model.setLocalButtonBeanModel(nModel);
        }

        // Send enable to the reset button on the Local Navigation button bean.
        // Should be disable if in training mode, or the user does not have access
        SecurityManagerIfc securityManager = null;
        securityManager = (SecurityManagerIfc)bus.getManager(SecurityManagerIfc.TYPE);
        boolean enabled = (!isFingerprintAllowed() && !cargo.getRegister().getWorkstation().isTrainingMode()
                           && securityManager.checkAccess(cargo.getAppID(),
                                   RoleFunctionIfc.RESET_EMPLOYEE_PASSWORD));
        nModel.setButtonEnabled("ResetPassword", enabled);        


        boolean fpEnabled = (isFingerprintReaderOnline(bus) && isDBOnline(bus) && 
                !cargo.getRegister().getWorkstation().isTrainingMode()
                && securityManager.checkAccess(cargo.getAppID(), RoleFunctionIfc.SET_EMPLOYEE_FINGERPRINT));
        
        nModel.setButtonEnabled("SetFingerprint", fpEnabled);

        EventOriginatorInfoBean.setEventOriginator("EditSelectedEmployeeSite.arrive");
        ui.showScreen(screen, model);
    }

    /**
     * Is the database online?
     * 
     * @param bus
     * @return true if database is online
     */
    protected boolean isDBOnline(BusIfc bus)
    {
        DataManagerIfc dataManager = (DataManagerIfc)bus.getManager(DataManagerIfc.TYPE);
        boolean isDBOnline = DataManagerOnlineStatus.getStatus(dataManager);
        return isDBOnline;
    }

    /**
     * Is a fingerprint reader online?
     *
     * @param bus
     * @return true if a fingerprint reader is online
     */
    protected boolean isFingerprintReaderOnline(BusIfc bus)
    {
        boolean isFingerprintReaderOnline = false;
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);

        try
        {
            isFingerprintReaderOnline = pda.isFingerprintReaderOnline();
        }
        catch (DeviceException e)
        {
            logger.error("Unable to determine if fingerprint reader is online" + e);
        }
            
        return isFingerprintReaderOnline;
    }

    /**
     * Are fingerprints for logging in turned on (based on paramater
     * FingerprintLoginOptions)
     * 
     * @return boolean
     */
    private boolean isFingerprintAllowed()
    {
        return Utility.getUtil().isFingerprintAllowed();
    }

    /**
     * Get a String array of valid days from the value held in the
     * TempEmployeeDaysValid parameter
     *
     * @param BusIfc bus
     * @return String array containing the valid days
     */
    protected static String[] getDaysValidList(BusIfc bus)
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

        return daysValidArray;
    }
}
