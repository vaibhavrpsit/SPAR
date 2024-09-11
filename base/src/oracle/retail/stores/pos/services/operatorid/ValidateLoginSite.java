/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/ValidateLoginSite.java /main/25 2013/12/16 13:53:43 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/16/13 - refactor orpos login for fewer db access calls
 *    mkutiana  07/03/13 - Handle situation where manager (override) does not
 *                         have permission to access point
 *    mkutiana  07/01/13 - Fixed merge loss from previous checkin
 *    cgreene   06/04/13 - implement manager override as dialogs
 *    mkutiana  05/28/13 - incorrect override message fixed - rollback of
 *                         previous checkins implementing new fix
 *    rabhawsa  04/03/13 - display override failed error only when process is
 *                         not ok
 *    rabhawsa  03/07/13 - if override fails show error message and send back
 *                         to override flow.
 *    mkutiana  03/30/12 - XbranchMerge spurkaya_bug-13243938 from
 *                         rgbustores_13.3x_generic_branch
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    mkutiana  07/28/11 - The password does not need to be nullified over the
 *                         wire now that it is encrypted consequently will not
 *                         need to be reconstituted at the client end
 *    abondala  04/11/11 - XbranchMerge abondala_bug11827952-salting_passwords
 *                         from main
 *    abondala  03/25/11 - implement salting for the passwords
 *    abondala  03/23/11 - Implemented salting for the passwords
 *    mkutiana  02/22/11 - Modified to handle multiple password policies
 *                         (introduction of biometrics)
 *    blarsen   06/09/10 - XbranchMerge blarsen_biometrics-poc from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    blarsen   05/25/10 - Added support for manager override with optional
 *                         password (override via fingerprint).
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    mdecama   02/12/09 - Added LookAndFeel support by Locale
 *    mdecama   11/04/08 - I18N - Eliminate the Loading and Caching of the
 *                         CodeListMap. CodeListMap is deprecated by
 *                         CodeListManager.
 * $Log:
 *  17   360Commerce1.16        12/18/2006 9:08:16 AM  Rohit Sachdeva  23433:
 *       Encryption condition added
 *  16   360Commerce1.15        11/27/2006 4:39:43 PM  Brett J. Larsen CR 23011
 *       - checking cargo's is-password-encrypted flag before encrypting the
 *       password
 *
 *       in one MPOS use-case the password is stored in the cargo encrypted
 *
 *       this prevents the password from being encrypted twice
 *  15   360Commerce1.14        11/8/2006 5:42:00 PM   Brett J. Larsen
 *       CR 22927 - MPOS password policy
 *
 *       reverting previous changes - simple refactor caused
 *       failed-attempt-counter to stop working
 *
 *       avoiding changes to this class
 *  14   360Commerce1.13        11/7/2006 5:57:02 PM   Brett J. Larsen CR 22927
 *       - add password policy to MPOS
 *
 *       changed a few methods to protected so MPOS' subclass has access
 *
 *       refactored basic login code from arrive into "basicLogin" method -
 *       this code is reusable by MPOS' subclass
 *  13   360Commerce1.12        11/2/2006 6:10:46 AM   Rohit Sachdeva  21237:
 *       Activating Password Policy Evaluation and Change Password
 *  12   360Commerce1.11        10/25/2006 3:13:25 PM  Rohit Sachdeva  21237:
 *       Password Policy TDO updates
 *  11   360Commerce1.10        10/24/2006 11:11:16 AM Rohit Sachdeva  21237:
 *       Change Password Login Updates to Handle Impacts of Password Policy
 *  10   360Commerce1.9         10/23/2006 10:34:55 AM Rohit Sachdeva  21237:
 *       Password Policy Login Validation using Password Policy TDO to Handle
 *       Impacts of Password Policy for Logged In and Not Logged In
 *  9    360Commerce1.8         10/17/2006 12:26:27 AM Rohit Sachdeva  21237:
 *       Password Policy Flow Checks Added
 *  8    360Commerce1.7         10/16/2006 11:51:25 PM Rohit Sachdeva  21237:
 *       Password Policy Flow Updates
 *  7    360Commerce1.6         10/16/2006 6:06:24 PM  Christian Greene check
 *       for null on compliance before attempting to update.  It may be null if
 *       offline
 *  6    360Commerce1.5         10/16/2006 3:35:03 PM  Rohit Sachdeva  21237:
 *       Password Policy Flow Updates
 *  5    360Commerce1.4         10/12/2006 8:20:59 AM  Christian Greene Adding
 *       new functionality for PasswordPolicy.  Employee password will now be
 *       persisted as a byte[] in hexadecimal.  Updates include UI changes,
 *       persistence changes, and AppServer configuration changes.  A database
 *       rebuild with the new SQL scripts will be required.
 *  4    360Commerce1.3         10/9/2006 5:10:32 PM   Rohit Sachdeva  21237:
 *       Login Updates to Handle Impacts of Password Policy
 *  3    360Commerce1.2         3/31/2005 3:30:42 PM   Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:26:41 AM  Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:15:29 PM  Robert Pearse
 * $
 *  4    360Commerce1.3         10/9/2006 5:10:32 PM   Rohit Sachdeva  21237:
 *       Login Updates to Handle Impacts of Password Policy
 *  3    360Commerce1.2         3/31/2005 3:30:42 PM   Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:26:41 AM  Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:15:29 PM  Robert Pearse
 * $
 * Revision 1.9  2004/07/21 20:35:24  rsachdeva
 * @scr 6388 Non authorized Manager Override
 *
 * Revision 1.8  2004/05/17 17:22:48  khassen
 * @scr 2679 - removed errant else/if block, fixed bad password logic.
 *
 * Revision 1.7  2004/02/24 17:28:24  jriggins
 * @scr 3782 Added IsNewPasswordNeededSignal in order to test for the need to prompt the user to enter a new password.  Removed this logic from ValidateLoginSite.
 *
 * Revision 1.6  2004/02/19 23:36:45  jriggins
 * @scr 3782 this commit mainly deals with the database modifications needed for Enter New Password feature in Operator ID
 * Revision 1.5 2004/02/18 00:07:03 jriggins
 * @scr 3783 adding validate temp employee code
 *
 * Revision 1.4 2004/02/13 16:35:49 jriggins @scr 3782 Enter New Password
 * functionality
 *
 * Revision 1.3 2004/02/12 16:51:19 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:51:48 rhafernik @scr 0 Log4J conversion and code
 * cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:18 cschellenger updating to pvcs
 * 360store-current
 *
 *
 *
 * Rev 1.0 Aug 29 2003 16:03:14 CSchellenger Initial revision.
 *
 * Rev 1.15 Jul 10 2003 14:30:04 vxs added check if(employee != null)
 *
 * Rev 1.14 May 13 2003 10:51:26 adc Changes for journaling the security
 * override Resolution for 2340: security override for Void is not in
 * E.Journals
 *
 * Rev 1.13 May 08 2003 18:10:12 adc Save the password Resolution for 2302:
 * User with no access can access Administration when the same user name or
 * another user name are entered for Manager Override.
 *
 * Rev 1.12 May 08 2003 11:33:42 bwf Set correct id. Resolution for 1933:
 * Employee Login enhancements
 *
 * Rev 1.11 May 07 2003 14:58:58 adc Made processOk flag equal to loggedIn when
 * override Resolution for 2302: User with no access can access Administration
 * when the same user name or another user name are entered for Manager
 * Override.
 *
 * Rev 1.10 Apr 14 2003 19:06:08 pdd Cleanup. Added nopassword login
 * capability. Resolution for 1933: Employee Login enhancements
 *
 * Rev 1.9 Mar 19 2003 09:52:50 HDyer Fixed bug of not setting toContinue flag
 * after setting locale preferences. Resolution for POS SCR-2089: Manager
 * Override maintains manager security level rather than reverting to lower
 * level
 *
 * Rev 1.8 Mar 17 2003 13:18:50 HDyer Modify the site to use the
 * SecurityManager override method when performing an override. Only set locale
 * preferences on a login, not an override. Resolution for POS SCR-2089:
 * Manager Override maintains manager security level rather than reverting to
 * lower level
 *
 * Rev 1.7 05 Feb 2003 11:10:06 mrm JAAS mods Resolution for POS SCR-1958:
 * Implement JAAS Support
 *
 * Rev 1.6 Jan 31 2003 17:35:10 baa change pole display locale and receipt to
 * match link customer locale preferences Resolution for POS SCR-1843:
 * Multilanguage support
 *
 * Rev 1.5 23 Jan 2003 15:44:22 mrm Implement JAAS support Resolution for POS
 * SCR-1958: Implement JAAS Support
 *
 * Rev 1.4 Jan 21 2003 13:18:30 RSachdeva Database Internationalization
 * Resolution for POS SCR-1866: I18n Database support
 *
 * Rev 1.3 Jan 21 2003 11:12:16 pdd cleanup Resolution for 1933: Employee Login
 * enhancements
 *
 * Rev 1.2 Dec 18 2002 17:40:20 baa add employee preferred locale support
 * Resolution for POS SCR-1843: Multilanguage support
 *
 * Rev 1.1 Nov 27 2002 14:22:48 baa support multilanguage loggins Resolution
 * for POS SCR-1843: Multilanguage support
 *
 * Rev 1.0 Apr 29 2002 15:13:48 msg Initial revision.
 *
 * Rev 1.0 Mar 18 2002 11:40:30 msg Initial revision.
 *
 * Rev 1.1 19 Nov 2001 10:47:26 epd Disallow INACTIVE employees from logging in
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 * Rev 1.0 Sep 21 2001 11:32:08 msg Initial revision.
 *
 * Rev 1.1 Sep 17 2001 13:10:12 msg header update
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.operatorid;

import java.util.Locale;

import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.tdo.PasswordPolicyTDOIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This site validates the employee ID.
 *
 * @version $Revision: /main/25 $
 */
public class ValidateLoginSite extends PosSiteActionAdapter
{
    /**
     * Generated Serial Version UI
     */
    private static final long serialVersionUID = 7566783742744142274L;
    /**
     * The logger to which log messages will be sent
     */
    protected static final Logger logger = Logger.getLogger(ValidateLoginSite.class);
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/25 $";

    /**
     * letter login validation for change password
     */
    public static final String LOGIN_VALIDATION_CHANGE_PASSWORD = "LoginValidationChangePassword";
    /**
     * Default error code
     */
    protected static final int NO_ERROR = 0;

    /**
     * This site checks the user id and password to see if this is a valid user
     * to be logged into the system. Sends a Success letter if found, sends a
     * Failure letter if not found.
     *
     * @param bus Service Bus
     */
    public void arrive(BusIfc bus)
    {
        // set local variables
        boolean loggedIn = false;
        PasswordPolicyTDOIfc tdo = getPasswordPolicyTDO();

        OperatorIdCargo cargo = (OperatorIdCargo)bus.getCargo();
        cargo.setErrorType(NO_ERROR);

        byte[] enteredPassword = cargo.getEmployeePasswordBytes();
        String enteredID = cargo.getEmployeeID();

        // create a holder for employee id/loging id and entered password.
        EmployeeIfc employee = DomainGateway.getFactory().getEmployeeInstance();
        if (cargo.getIDType().equals(ParameterConstantsIfc.OPERATORID_ManualEntryID_EMPLOYEE))
        {
            employee.setEmployeeID(enteredID);
        }
        else
        {
            employee.setLoginID(enteredID);
        }
        employee.setPasswordBytes(enteredPassword);

        // Use SecurityManager to determine if user is logged in
        SecurityManagerIfc securityManager = (SecurityManagerIfc)bus.getManager(SecurityManagerIfc.TYPE);

        try
        {
            // Perform either the login or the override, as indicated in cargo
            if (cargo.getSecurityOverrideFlag())
            {
                loggedIn = securityManager.override(cargo.getAppID(), enteredID, enteredPassword,
                        cargo.getAccessFunctionID(), cargo.isPasswordRequired());
            }
            else
            {
                tdo.readEmployeeCompliance(bus, employee);
                loggedIn = securityManager.loginEmployee(cargo.getAppID(), employee, cargo.isPasswordRequired());
            }

            if (loggedIn)
            {
                cargo.setOperatorIdScreenName(employee.getLoginID());
                cargo.setSelectedEmployee(employee);
            }
        }
        catch (LoginException le)
        {
            logger.warn("Could not validate login: " + le.getMessage());
            int errorCode = -50;
            try
            {
                errorCode = Integer.parseInt(le.getMessage());
            }
            catch (NumberFormatException nfe)
            {
                logger.warn("Could not parse login error as a number: " + nfe.getMessage());
            }

            // prevent inactive employees from logging in
            if (errorCode == DataException.NO_DATA)
            {
                cargo.setErrorType(errorCode);
            }
            else
            {
                logger.error("Unknown exception when logging in." + le.getMessage());
            }
        }

        LetterIfc letter = null;

        // logged in process is OK
        if (loggedIn)
        {
            //for normal login process, not for Manager Override, logged in process is OK
            if (!cargo.getSecurityOverrideFlag())
            {
                letter = getLoggedInLetter(bus, employee, tdo);
            }
            //for Manager Override, logged in process is OK
            else
            {
                letter = getLoggedInManagerOverrideLetter();
            }
        }
        else
        {
            if (!cargo.getSecurityOverrideFlag())
            {
                letter = getNotLoggedInLetter(bus, employee, tdo);
            }
            else
            {
                letter = getNotLoggedInManagerOverrideLetter(cargo.getErrorType());
            }
        }

        bus.mail(letter, BusIfc.CURRENT);
    }

    // ========================================================
    // Methods for retrieving flow. PasswordPolicyTDO is used for
    // Password Policy Employee Compliance evaluation checks and its impacts.
    // ========================================================
    /**
     * Retrieves letter for logged in process ok user. This is not for Manager
     * Override.
     *
     * @param bus reference to bus
     * @param employee employee reference
     * @param tdo password policy tdo
     * @param lockOut true if locked out, otherwise false
     * @return LetterIfc depending on the various conditions appropriate letter
     *         is returned
     */
    protected LetterIfc getLoggedInLetter(BusIfc bus, EmployeeIfc employee, PasswordPolicyTDOIfc tdo)
    {
        LetterIfc letter;

        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        boolean lockOut = tdo.employeeComplianceEvaluation(bus, employee, true);
        //Not for lockout
        if(!lockOut)
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

            //set user interface locale according to the employee locale
            // preferrence.
            setLocalePreference(employee.getPreferredLocale(), ui);

            if (cargo.isLoginValidationChangePassword())
            {
                letter = new Letter(LOGIN_VALIDATION_CHANGE_PASSWORD);
            }
            else
            {
                letter = new Letter(CommonLetterIfc.SUCCESS);
            }
        }
        else
        {
            //logged in user that was previously locked out and password not reset
            letter = new Letter(CommonLetterIfc.NEXT);
        }
        return letter;
    }

    /**
     * Retrieves letter for user who could not log in process not ok. This is
     * not for Manager Override.
     *
     * @param bus reference to bus
     * @param cargo reference to cargo
     * @param employee employee reference
     * @param tdo password policy tdo
     * @return LetterIfc depending on the various conditions appropriate letter
     *         is returned
     */
    protected LetterIfc getNotLoggedInLetter(BusIfc bus, EmployeeIfc employee, PasswordPolicyTDOIfc tdo)
    { 
        LetterIfc letter = new Letter(CommonLetterIfc.SHOW_ERROR);
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        boolean lockOut = tdo.employeeComplianceEvaluation(bus, employee, false);
        if (cargo.getErrorType() != DataException.NO_DATA || lockOut)
        {
            letter = new Letter(CommonLetterIfc.NEXT);
        }
        return letter;
    }

    /**
     * Retrieves letter for logged in Manager Override.
     *
     * @return LetterIfc for logged in Manager Override
     */
    protected LetterIfc getLoggedInManagerOverrideLetter()
    {
        return new Letter(CommonLetterIfc.SUCCESS);
    }

    /**
     * Retrieves letter for Not logged in Manager Override.
     *
     * @return LetterIfc for logged in Manager Override
     **/
    protected LetterIfc getNotLoggedInManagerOverrideLetter(int errorType)
    {
        LetterIfc letter = new Letter(CommonLetterIfc.SHOW_ERROR);
        if (errorType == 0)
        {
            letter = new Letter(CommonLetterIfc.FAILURE);
        }
        return letter;
    }

    /**
     * Sets the locale preference for the user Interface.
     *
     * @param preferredLocale employee locale preference
     * @return boolean true if change locale
     */
    public boolean setLocalePreference(Locale preferredLocale, POSUIManagerIfc ui)
    {
        boolean changedLocale = false;
        if (preferredLocale != null)
        {
            if (!preferredLocale.equals(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)))
            {
                changedLocale = true;
                UIUtilities.setUILocaleForEmployee(preferredLocale, ui);
            }
        }
        else
        {
            UIUtilities.setUILocaleForEmployee(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE), ui);
        }
        return changedLocale;
    }

    /**
     * Creates Instance of Password Policy TDO.
     *
     * @return PasswordPolicyTDOIfc instance of Password Policy TDO
     **/
    private PasswordPolicyTDOIfc getPasswordPolicyTDO()
    {
        return Utility.getUtil().getPasswordPolicyTDO();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#toString()
     */
    @Override
    public String toString()
    {
      // result string
        String strResult = new String("Class:  ValidateLoginSite (Revision " + getRevisionNumber() + ")" + hashCode());

        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
      // return string
        return (revisionNumber);
    }
}
