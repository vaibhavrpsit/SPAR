/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderCheckADO.java /main/32 2013/09/04 09:10:26 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     08/30/13 - Add support for check approval sequence number
 *    icole     08/26/13 - Correct auth called for echeck when check auth
 *                         parameter is set to no.
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    sgu       09/12/11 - set authorization status properly for reentry mode
 *    cgreene   09/12/11 - revert aba number encryption, which is not sensitive
 *    ohorne    08/18/11 - APF: check cleanup
 *    mkutiana  08/15/11 - Typo fixed - Caused EJ to not display the Auth
 *                         Response text
 *    masahu    08/12/11 - Fixes to Junit Failure
 *    cgreene   07/28/11 - implement credit decline manager override
 *    rrkohli   07/27/11 - removing check number encryption/masking
 *    cgreene   07/26/11 - moved StatusCode to GiftCardifc
 *    cgreene   07/26/11 - removed tenderauth and giftcard.activation tours and
 *                         financialnetwork interfaces.
 *    rrkohli   07/19/11 - encryption cr
 *    rrkohli   06/29/11 - encryption CR
 *    ohorne    07/18/11 - eCheck post void
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    ohorne    06/23/11 - apf:check removal of obsolete code
 *    cgreene   05/27/11 - move auth response objects into domain
 *    ohorne    05/09/11 - APF deprecations
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/11/10 - convert Base64 from axis
 *    cgreene   05/11/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    dwfung    03/10/10 - Handling Training Mode Requests
 *    asinton   01/18/10 - Added track 1 data to support swiping driver's
 *                         license and retriving 2 letter state code.
 *    asinton   01/11/10 - Add driver's license state code to authorization
 *                         request.

 *    asinton   01/11/10 - If personal ID used for check tender is driver's
 *                         license, then prepend to personal ID number in
 *                         request.
 *    asinton   01/04/10 - XbranchMerge spurkaya_bug-8557516 from
 *                         rgbustores_13.0x_branch
 *    asinton   01/11/10 - Moved logic for adding Manuel(keyed) versus Swiped
 *                         data entry for personal ID for check tenders.
 *    asinton   12/31/09 - XbranchMerge spurkaya_bug-8673216 from
 *                         rgbustores_13.0x_branch
 *    abondala  01/03/10 - update header date
 *    asinton   06/17/09 - Removing parameter MerchantNumber.
 *    kulu      02/22/09 - Fix the bug that foreign currency in tender info is
 *                         always CAD
 *    ddbaker   01/21/09 - Removed tab characters causing alignment problems.
 *    ddbaker   01/21/09 - Update to use Currency Type during creation of
 *                         alternate (foreign) currency objects.
 *    vchengeg  11/07/08 - To fix BAT test failure
 *    abondala  11/06/08 - updated files related to reason codes
 *    sswamygo  11/05/08 - Checkin after merges
 *
 * ===========================================================================
 * $Log:
 *  10   360Commerce 1.9         11/15/2007 10:09:55 AM Christian Greene Belize
 *        merge - Changed comparision of MinimumAuthAmount to
 *       greater-than-equal-to so minimum amount is inclusive of amount
 *       required to authorize.
 *  9    360Commerce 1.8         7/9/2007 6:04:18 PM    Alan N. Sinton  CR
 *       27494 - Removed constaint TenderLimitsIfc.TENDER_NO_LIMIT_AMOUNT in
 *       favor of a lazy init value returned by static method
 *       TenderLimits.getTenderNoLimitAmount().
 *  8    360Commerce 1.7         5/30/2007 9:01:48 AM   Anda D. Cadar   code
 *       cleanup
 *  7    360Commerce 1.6         5/18/2007 9:19:17 AM   Anda D. Cadar   always
 *       use decimalValue toString
 *  6    360Commerce 1.5         4/25/2007 8:52:55 AM   Anda D. Cadar   I18N
 *       merge
 *
 *  5    360Commerce 1.4         10/12/2006 8:17:48 AM  Christian Greene Adding
 *        new functionality for PasswordPolicy.  Employee password will now be
 *        persisted as a byte[] in hexadecimal.  Updates include UI changes,
 *       persistence changes, and AppServer configuration changes.  A database
 *        rebuild with the new SQL scripts will be required.
 *  4    360Commerce 1.3         12/13/2005 4:42:32 PM  Barry A. Pape
 *       Base-lining of 7.1_LA
 *  3    360Commerce 1.2         3/31/2005 4:30:22 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:25:54 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:14:49 PM  Robert Pearse
 * $
 * Revision 1.25.2.1  2004/11/12 14:28:53  kll
 * @scr 7337: JournalFactory extensibility initiative
 *
 * Revision 1.25  2004/10/05 19:05:32  bwf
 * @scr 7305 Add logging of authorization in order to get response time.
 *
 * Revision 1.24  2004/08/20 21:37:02  bwf
 * @scr 6785 Catch auth exception during reversal and voids and log it.
 *
 * Revision 1.23  2004/08/03 20:44:42  bwf
 * @scr 2539 Fix unit tests.
 *
 * Revision 1.22  2004/07/30 15:18:08  epd
 * @scr 2539 Updated to add timeout to echeck auth request.  Also fixed bug in entry mode setting for checks
 *
 * Revision 1.21  2004/07/26 19:04:20  epd
 * @scr 6487 Fixed null pointer
 *
 * Revision 1.20  2004/07/23 22:21:02  epd
 * @scr 5963 (ServicesImpact) Updates to fix training mode and RegisterADO singleton refactoring
 *
 * Revision 1.19  2004/07/23 20:46:05  bwf
 * @scr 2141, 3289, 3290, 3291, 3292 Only use sysauthcode for offlines.
 *
 * Revision 1.18  2004/07/15 16:13:22  kmcbride
 * @scr 5954 (Services Impact): Adding logging to these ADOs, also fixed some exception handling issues.
 *
 * Revision 1.17  2004/07/14 21:32:00  bwf
 * @scr 6230 Fix check auth handling.
 *
 * Revision 1.16  2004/07/13 23:33:31  jdeleau
 * @scr 6238 Fix crash for a real check post-void that occurs when
 * eCheckAuth is required.
 *
 * Revision 1.15  2004/06/19 17:33:33  bwf
 * @scr 5205 These are the overhaul changes to the Change Due Options
 *                   screen and max change calculations.
 *
 * Revision 1.14  2004/06/11 23:14:51  bwf
 * @scr 5526 Set an authorization response when we dont authorize.
 *
 * Revision 1.13  2004/06/03 15:27:38  bwf
 * @scr 4474 Moved hard coded state codes to application.properties.
 *
 * Revision 1.12  2004/06/02 04:08:36  blj
 * @scr 5334 - fix defects for Pep Boys
 *
 * Revision 1.11  2004/05/06 17:57:26  bwf
 * @scr 3347 Fixed check void authorization.
 *
 * Revision 1.10  2004/04/28 22:06:02  bwf
 * @scr 3377 Debit Reversal Work
 *
 * Revision 1.9  2004/04/22 21:03:53  epd
 * @scr 4513 Changed all toFormattedString() calls to getStringValue() calls
 *
 * Revision 1.8  2004/04/14 17:47:45  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.7  2004/04/09 22:39:57  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.6  2004/03/16 18:30:45  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.5  2004/02/19 20:00:38  rsachdeva
 * @scr  3820  ISD Integration
 *
 * Revision 1.4  2004/02/16 19:03:50  bwf
 * @scr 3429 Check Rework.
 *
 * Revision 1.3  2004/02/12 16:47:55  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:19:47  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 * Revision 1.1.1.1 2004/02/11 01:04:11
 * cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.15 Feb 10 2004 14:38:54 bwf Refactor echeck.
 *
 * Rev 1.14 Feb 05 2004 13:46:24 rhafernik log4j changes
 *
 * Rev 1.13 Jan 30 2004 12:49:52 bwf Updated isAuthorized to fix problems when
 * gift card reload fails and tender must be reversed.
 *
 * Rev 1.12 Jan 19 2004 17:22:40 epd Put back calls to DomainGateway
 *
 * Rev 1.11 Jan 09 2004 10:29:52 bwf Updated for unit tests.
 *
 * Rev 1.10 Jan 06 2004 13:27:46 epd refactoring away references to
 * TenderHelper and DomainGateway
 *
 * Rev 1.9 Jan 02 2004 10:14:10 rsachdeva Alternate Currency Resolution for POS
 * SCR-3551: Tender using Canadian Cash/Canadian Travelers Check/Canadian Check
 *
 * Rev 1.8 Dec 09 2003 11:07:54 bwf Updates per code review.
 *
 * Rev 1.7 Dec 05 2003 14:38:10 bwf Make sure to set to check auth if offline.
 * Resolution for 3549: App crashes when apply discount with e-Check
 * Functionality = No
 *
 * Rev 1.6 Nov 25 2003 14:11:32 bwf More check updates.
 *
 * Rev 1.5 Nov 20 2003 18:22:02 bwf Check franking Resolution for 3429:
 * Check/ECheck Tender
 *
 * Rev 1.4 Nov 19 2003 13:46:56 bwf Made changes for authorization. Resolution
 * for 3429: Check/ECheck Tender
 *
 * Rev 1.3 Nov 12 2003 13:44:14 bwf Update for hashmap auth. Resolution for
 * 3429: Check/ECheck Tender
 *
 * Rev 1.2 Nov 11 2003 16:18:14 epd Updates made to accommodate tender
 * deletion/reversal
 *
 * Rev 1.1 Nov 07 2003 16:26:32 bwf Added check functionality. Resolution for
 * 3429: Check/ECheck Tender
 *
 * Rev 1.0 Nov 04 2003 11:13:12 epd Initial revision.
 *
 * Rev 1.2 Oct 28 2003 14:16:34 epd Added method to return whether tender is
 * already authorized
 *
 * Rev 1.1 Oct 21 2003 10:00:58 epd Refactoring. Moved RDO tender to abstract
 * class
 *
 * Rev 1.0 Oct 17 2003 12:33:44 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.login.LoginException;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderLimits;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.device.MICRModel;
import oracle.retail.stores.foundation.manager.device.MSRModelIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;
import oracle.retail.stores.pos.ado.context.ADOContextIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalConstants;
import oracle.retail.stores.pos.ado.journal.JournalData;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.security.OverridableIfc;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;

import org.apache.log4j.Logger;

/**
 * This class is the Check ADO.
 */
public class TenderCheckADO extends AbstractTenderADO
    implements AuthorizableADOIfc, ReversibleTenderADOIfc, OverridableIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6291477916534136467L;

    /** The MICR model as read from the check device * */
    protected MICRModel micrModel;
    /** The MSR model as read from id swipe * */
    protected MSRModelIfc msrModel;

    // input codes
    public static final int SWIPED = 0;
    public static final int MANUAL = 1;

    public static final String APPROVED = TenderCheckIfc.APPROVED;
    public static final String DECLINED = TenderCheckIfc.DECLINED;
    public static final String DECLINED_REFERRAL = TenderCheckIfc.DECLINED_REFERRAL;

    protected String authorizationResponseCode;

    /** Flag indicating this tender was successfully reversed */
    protected boolean reversed = false;

    /** Flag indicating this tender was successfully voided */
    protected boolean voided = false;

    /** authorization type * */
    protected int authType = 0;
    protected static final int AUTH_TYPE_AUTHORIZE = 0;
    protected static final int AUTH_TYPE_REVERSAL = 1;
    protected static final int AUTH_TYPE_VOID = 2;

    /** Constant for DriversLicense */
    public static final String ID_TYPE_DRIVERS_LICENSE = "DriversLicense";

    /**
     * The logger to which log messages will be sent.
     */
    protected static transient Logger logger = Logger.getLogger(TenderCheckADO.class);

    /** The factory instantiates this class */
    protected TenderCheckADO()
    {
    }

    /**
     * Initialize the rdo.
     *
     * @see oracle.retail.stores.ado.tender.AbstractTenderADO#initializeTenderRDO()
     */
    protected void initializeTenderRDO()
    {
        tenderRDO = DomainGateway.getFactory().getTenderCheckInstance();
        TenderCheckIfc tenderCheckRDO = (TenderCheckIfc)tenderRDO;
        if (eCheckAuthRequired())
        {
            tenderCheckRDO.setTypeCode(TenderLineItemConstantsIfc.TENDER_TYPE_E_CHECK);
        }
    }

    /**
     * Get the tender type.
     *
     * @return TenderTypeEnum Check
     * @see oracle.retail.stores.ado.tender.TenderADOIfc#getTenderType()
     */
    public TenderTypeEnum getTenderType()
    {
        return TenderTypeEnum.CHECK;
    }

    /**
     * This method checks to see if echeck or check auth required.
     *
     * @return result boolean
     */
    public boolean eCheckAuthRequired()
    {
        UtilityIfc util = getUtility();
        boolean transReentry = this.isTransactionReentryMode();
        boolean result = false;
        if (util.getParameterValue("E-CheckFunctionality", "Y").equals("Y") && !transReentry)  
        {
            if ( util.getParameterValue("CheckAuthActive", "Y").equals("Y"))
            {
                result = true;
            }
            else
            {
                logger.info("ECheck tender set to YES and Check Auth Active set to NO, eCheck will be handled as deposited check");
            }
        }

        return result;
    }
    /**
     * This method validates the Tender
     *
     * @throws TenderException
     * @see oracle.retail.stores.ado.tender.TenderADOIfc#validate()
     */
    public void validate() throws TenderException
    {
        // nothing needs to be validate here
    }

    /**
     * Return whether or not tender has been reversed.
     *
     * @return @see oracle.retail.stores.pos.ado.tender.ReversibleTenderADOIfc#isReversed()
     */
    public boolean isReversed()
    {
        return reversed;
    }

    /**
     * Return whether or not tender has been voided.
     *
     * @return @see oracle.retail.stores.pos.ado.tender.ReversibleTenderADOIfc#isReversed()
     */
    public boolean isVoided()
    {
        return voided;
    }

    /**
     * This checks to see if online authorization is required.
     *
     * @return result boolean
     */
    protected boolean onlineAuthorizationRequired()
    {
        boolean result = false;

        UtilityIfc util = getUtility();
        // default param value from requirements
        CurrencyIfc limitAmount =
            DomainGateway.getBaseCurrencyInstance(util.getParameterValue("MinimumCheckAuthorizationAmount", "10.00"));

        // if amount greater than or equals limit value, online auth required
        if (!limitAmount.equals(TenderLimits.getTenderNoLimitAmount())
                && getAmount().compareTo(limitAmount) >= CurrencyIfc.EQUALS)
        {
            result = true;
        }

        return result;
    }

    /**
     * This method determines whether check can be authorized offline.
     *
     * @return
     */
    public boolean offlineAuthorizationOk()
    {
        boolean result = false;

        UtilityIfc util = getUtility();
        CurrencyIfc floorLimit =
            DomainGateway.getBaseCurrencyInstance(util.getParameterValue("OfflineCheckFloorLimit", "50.00"));

        // check limit
        if (!floorLimit.equals(TenderLimits.getTenderNoLimitAmount())
                && !(getAmount().abs().compareTo(floorLimit) == CurrencyIfc.GREATER_THAN))
        {
            result = true;
        }

        return result;
    }

    /**
     * This method gets the store id.
     *
     * @return
     */
    protected String getStoreID()
    {
        AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc)TourContext.getInstance().getTourBus().getCargo();
        return cargo.getRegister().getWorkstation().getStoreID();
    }

    /**
     * This method gets the register id.
     *
     * @return
     */
    protected String getRegisterID()
    {
        AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc)TourContext.getInstance().getTourBus().getCargo();
        return cargo.getRegister().getWorkstation().getWorkstationID();
    }

    /**
     * This checks to see if the check is authorized.
     *
     * @return boolean
     * @see oracle.retail.stores.ado.tender.#isAuthorized()
     */
    public boolean isAuthorized()
    {
        TenderCheckIfc tenderCheckRDO = (TenderCheckIfc)tenderRDO;
        boolean returnCode = false;

        if(!StringUtils.isBlank(tenderCheckRDO.getAuthorizationCode()) ||
                !StringUtils.isBlank(tenderCheckRDO.getAuthorizationResponse()) ||
                !StringUtils.isBlank(authorizationResponseCode))
        {
            returnCode = true;
        }
        return returnCode || isVoided();
    }

    /**
     * This method gets the operator id so it can be overriden for unit tests.
     *
     * @return employee id string
     */
    protected String getOperatorEmployeeID()
    {
        return ((UserAccessCargo) getContext().getBus().getCargo()).getOperator().getEmployeeID();
    }

    /**
     * This method gets the register journal so that it can be overriden for
     * unit testing.
     *
     * @return registerjournalifc
     */
    protected RegisterJournalIfc getRegisterJournal()
    {
        return getJournalFactory().getRegisterJournal();
    }

    /**
     * This method perform the override.
     *
     * @param overrideEmployee
     * @param roleFunction
     * @return boolean
     * @see oracle.retail.stores.pos.ado.security.OverridableIfc#override(oracle.retail.stores.domain.employee.EmployeeIfc,
     *      int)
     */
    public boolean override(EmployeeIfc overrideEmployee, int roleFunction)
    {
        TenderCheckIfc tenderCheckRDO = (TenderCheckIfc)tenderRDO;
        if(logger.isInfoEnabled())
        {
            logger.info("Overriding check tender limit...");
        }

        // Get the context
        ADOContextIfc context = getContext();
        // Get the security manager
        SecurityManagerIfc sm = (SecurityManagerIfc) getManager(SecurityManagerIfc.TYPE);
        boolean result = false;
        try
        {
            // Attempt Tender Limit override with this employee
            result =
                sm.override(
                        context.getApplicationID(),
                        overrideEmployee.getLoginID(),
                        overrideEmployee.getPasswordBytes(),
                        roleFunction);

            // journal the override attempt
            JournalData data = new JournalData();
            // add operator, override operator, function, and result to journal
            // data.
            data.putJournalData(JournalConstants.OPERATOR, getOperatorEmployeeID());
            data.putJournalData(JournalConstants.OVERRIDE_OPERATOR, overrideEmployee.getEmployeeID());
            data.putJournalData(JournalConstants.FUNCTION, Integer.toString(RoleFunctionIfc.OVERRIDE_DECLINED_CREDIT));
            data.putJournalData(JournalConstants.BOOLEAN, new Boolean(result).toString());
            RegisterJournalIfc regJournal = getRegisterJournal();
            regJournal.journal(data, JournalFamilyEnum.TENDER, JournalActionEnum.OVERRIDE);
        }
        catch (LoginException e)
        {
            // do nothing. Obviously the override didn't work

            // KLM: This actually handled below, but let's at least log it
            //
            logger.warn("Caught exception when trying to override tender limit: " + e);
        }

        // If the override succeeded, alter the internal state of this tender
        if (result)
        {
            tenderCheckRDO.setAuthorizationMethod(AuthorizableTenderIfc.AUTHORIZATION_METHOD_MANUAL);
            tenderCheckRDO.setAuthorizationStatus(AuthorizableTenderIfc.AUTHORIZATION_STATUS_APPROVED);
            // only reset auth code if not null (may have been entered manually
            // on call referral screen).
            if (tenderCheckRDO.getAuthorizationCode() == null)
            {
                tenderCheckRDO.setAuthorizationCode(tenderCheckRDO.getAuthorizationResponse());
            }

            //journal the result using utility method in cargo
            // only journal for successful override. We could have multiple
            // unsuccessful overrides and only want to journal when we have a
            // resolution
            RegisterJournalIfc journal = getRegisterJournal();
            journal.journal(this, JournalFamilyEnum.TENDER, JournalActionEnum.AUTHORIZATION);
        }
        else
        {
            tenderCheckRDO.setAuthorizationMethod(AuthorizableTenderIfc.AUTHORIZATION_METHOD_MANUAL);
            tenderCheckRDO.setAuthorizationStatus(AuthorizableTenderIfc.AUTHORIZATION_STATUS_DECLINED);
        }

        return result;
    }

    /**
     * This gets the journal memento.
     *
     * @return @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    public Map<String,Object> getJournalMemento()
    {
        Map<String,Object> memento = getTenderAttributes();

        // add tender descriptor
        memento.put(JournalConstants.DESCRIPTOR, getTenderType().toString());
        return memento;}

    /**
     * This method gets the Locale so that it can be override in the unit
     * tests.
     *
     * @return Locale
     */
    protected Locale getLocale()
    {
        return LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
    }

    /**
     * This method gets the tender attributes.
     *
     * @return tender attributes hashmap
     * @see oracle.retail.stores.ado.tender.TenderADOIfc#getTenderAttributes()
     */
    public HashMap<String,Object> getTenderAttributes()
    {
        HashMap<String,Object> map = new HashMap<String, Object>();
        TenderCheckIfc tenderCheckRDO = (TenderCheckIfc)tenderRDO;

        map.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.CHECK);
        map.put(TenderConstants.AMOUNT, getAmount().getStringValue());
        if (tenderCheckRDO.getPersonalIDType() != null)
        {
            map.put(TenderConstants.ID_NUMBER, tenderCheckRDO.getIDNumber());
            map.put(TenderConstants.ID_TYPE, tenderCheckRDO.getPersonalIDType().getCodeName());
            map.put(TenderConstants.LOCALIZED_ID_TYPE, tenderCheckRDO.getPersonalIDType());
            map.put(TenderConstants.ID_ENTRY_METHOD, tenderCheckRDO.isIDSwiped() ? EntryMethod.Swipe : EntryMethod.Manual);
        }

        if (tenderCheckRDO.getPersonalID() != null)
        {
            map.put(TenderConstants.ENCIPHERED_DATA_ID_NUMBER, tenderCheckRDO.getPersonalID());
        }

        if (tenderCheckRDO.getMICREncipheredData() != null)
        {
            map.put(TenderConstants.ENCIPHERED_DATA_MICR_NUMBER, tenderCheckRDO.getMICREncipheredData());
        }

        map.put(TenderConstants.COUNTRY, new Integer(tenderCheckRDO.getMICRCountryCode()));


        if (tenderCheckRDO.getIDTrack2Data() != null || tenderCheckRDO.getIDTrack1Data() != null)
        {
            map.put(TenderConstants.MSR_MODEL, msrModel);
            map.put(TenderConstants.ID_TRACK_1_DATA, tenderCheckRDO.getIDTrack1Data());
            map.put(TenderConstants.ID_TRACK_2_DATA, tenderCheckRDO.getIDTrack2Data());
        }

        // there will be an issuer only if type dl or state id
        if (tenderCheckRDO.getIDIssuer() != null)
        {
            map.put(TenderConstants.ID_STATE, new String(tenderCheckRDO.getIDIssuer()));
        }
        if (tenderCheckRDO.getPhoneNumber() != null)
        {
            map.put(TenderConstants.PHONE_NUMBER, new String(tenderCheckRDO.getPhoneNumber()));
        }

        map.put(TenderConstants.ENTRY_METHOD, tenderCheckRDO.getEntryMethod());
        map.put(TenderConstants.CHECK_NUMBER, tenderCheckRDO.getCheckNumber());
        map.put(TenderConstants.ABA_NUMBER, tenderCheckRDO.getABANumber());
        map.put(TenderConstants.ENCIPHERED_DATA_ACCOUNT_NUMBER, tenderCheckRDO.getAccountNumberEncipheredData());

        // authorization info
        map.put(TenderConstants.AUTH_STATUS, new Integer(tenderCheckRDO.getAuthorizationStatus()));
        // Only put in map if we have authorized (status should be non-zero)
        if (isAuthorized())
        {
            map.put(TenderConstants.AUTH_AMOUNT, tenderCheckRDO.getAuthorizationAmount().getStringValue());
            map.put(TenderConstants.AUTH_CODE, tenderCheckRDO.getAuthorizationCode());
            map.put(TenderConstants.AUTH_METHOD, tenderCheckRDO.getAuthorizationMethod());
            map.put(TenderConstants.AUTH_RESPONSE, tenderCheckRDO.getAuthorizationResponse());
            map.put(TenderConstants.FINANCIAL_NETWORK_STATUS, tenderCheckRDO.getFinancialNetworkStatus());
            map.put(TenderConstants.AUTH_RESPONSE_CODE, authorizationResponseCode);
            map.put(TenderConstants.RESPONSE_TYPE, tenderCheckRDO.getResponseType());
            map.put(TenderConstants.CHECK_AUTH_SEQUENCE_NUMBER, tenderCheckRDO.getAuthorizationSequenceNumber());
        }
        // get type authorized
        if (tenderCheckRDO.getTypeCode() == TenderLineItemConstantsIfc.TENDER_TYPE_E_CHECK)
        {
            map.put(TenderConstants.CHECK_AUTH_TYPE, "ECheck");
        }
        else
        {
            map.put(TenderConstants.CHECK_AUTH_TYPE, "Check");
        }
        //alternate currency
        if (((TenderAlternateCurrencyIfc) tenderRDO).getAlternateCurrencyTendered() != null)
        {
            TenderAlternateCurrencyIfc tenderAlternateCurrencyRDO = (TenderAlternateCurrencyIfc)tenderRDO;
            map.put(TenderConstants.ALTERNATE_AMOUNT, tenderAlternateCurrencyRDO.getAlternateCurrencyTendered().getStringValue());
            map.put(TenderConstants.FOREIGN_CURRENCY, tenderAlternateCurrencyRDO.getAlternateCurrencyTendered().getType());

        }
        return map;
    }

    /**
     * This method sets the tender attributes.
     *
     * @param tenderAttributes
     *            HashMap
     * @see oracle.retail.stores.ado.tender.AbstractTenderADO#setTenderAttributes(java.util.HashMap)
     */
    public void setTenderAttributes(HashMap<String,Object> tenderAttributes) throws TenderException
    {
        TenderCheckIfc tenderCheckRDO = (TenderCheckIfc)tenderRDO;
        CurrencyIfc amount = parseAmount((String) tenderAttributes.get(TenderConstants.AMOUNT));
        tenderCheckRDO.setAmountTender(amount);
        //alternate currency
        String alternateAmountValue = (String) tenderAttributes.get(TenderConstants.ALTERNATE_AMOUNT);
        if (alternateAmountValue != null)
        {
            CurrencyIfc alternateAmount = parseAlternateAmount(alternateAmountValue, tenderAttributes);
            ((TenderAlternateCurrencyIfc) tenderRDO).setAlternateCurrencyTendered(alternateAmount);
            CurrencyTypeIfc alternateAmountType = (CurrencyTypeIfc)tenderAttributes.get(TenderConstants.FOREIGN_CURRENCY);
            if (alternateAmountType != null)
            {
                alternateAmount.setType(alternateAmountType);
            }
        }

        LocalizedCodeIfc personalIDType = (LocalizedCodeIfc) tenderAttributes.get(TenderConstants.LOCALIZED_ID_TYPE);
        if(personalIDType != null)
        {
            tenderCheckRDO.setPersonalIDType(personalIDType);
        }

        // license swiped
        tenderCheckRDO.setIDTrack1Data((byte[]) tenderAttributes.get(TenderConstants.ID_TRACK_1_DATA));
        tenderCheckRDO.setIDTrack2Data((byte[]) tenderAttributes.get(TenderConstants.ID_TRACK_2_DATA));

        tenderCheckRDO.setIDNumber((String) tenderAttributes.get(TenderConstants.ID_NUMBER));
        tenderCheckRDO.setIDIssuer((String) tenderAttributes.get(TenderConstants.ID_STATE));
        EntryMethod idEntryMethod = (EntryMethod) tenderAttributes.get(TenderConstants.ID_ENTRY_METHOD);
        tenderCheckRDO.setIDSwiped(EntryMethod.Swipe.equals(idEntryMethod));

        tenderCheckRDO.setCheckNumber((String) tenderAttributes.get(TenderConstants.CHECK_NUMBER));
        tenderCheckRDO.setPhoneNumber((String) tenderAttributes.get(TenderConstants.PHONE_NUMBER));

        if (tenderAttributes.get(TenderConstants.ENCIPHERED_DATA_MICR_NUMBER) != null)
        {
            tenderCheckRDO.setMICREncipheredData((EncipheredDataIfc) tenderAttributes.get(TenderConstants.ENCIPHERED_DATA_MICR_NUMBER));
        }

        if (tenderAttributes.get(TenderConstants.ENCIPHERED_DATA_ID_NUMBER) != null)
        {
          tenderCheckRDO.setPersonalID((EncipheredDataIfc) tenderAttributes.get(TenderConstants.ENCIPHERED_DATA_ID_NUMBER));
        }

        if (tenderAttributes.get(TenderConstants.ABA_NUMBER) != null)
        {
          tenderCheckRDO.setABANumber((String) tenderAttributes.get(TenderConstants.ABA_NUMBER));
        }

        if (tenderAttributes.get(TenderConstants.ENCIPHERED_DATA_ACCOUNT_NUMBER) != null)
        {
          tenderCheckRDO.setAccountNumberEncipheredData((EncipheredDataIfc) tenderAttributes.get(TenderConstants.ENCIPHERED_DATA_ACCOUNT_NUMBER));
        }

        tenderCheckRDO.setEntryMethod((EntryMethod) tenderAttributes.get(TenderConstants.ENTRY_METHOD));
        if (tenderAttributes.get(TenderConstants.COUNTRY) != null)
        {
            tenderCheckRDO.setMICRCountryCode(
                    ((Integer) tenderAttributes.get(TenderConstants.COUNTRY)).intValue());
        }

        // Authorization info
        tenderCheckRDO.setAuthorizationStatus(((Integer) tenderAttributes.get(TenderConstants.AUTH_STATUS)).intValue());
        // Only get from map if authorization occurred
        if (tenderAttributes.get(TenderConstants.AUTH_RESPONSE) != null)
        {
            tenderCheckRDO.setAuthorizationAmount(parseAmount((String) tenderAttributes.get(TenderConstants.AUTH_AMOUNT)));
            tenderCheckRDO.setAuthorizationMethod((String) tenderAttributes.get(TenderConstants.AUTH_METHOD));
            tenderCheckRDO.setFinancialNetworkStatus((String) tenderAttributes.get(TenderConstants.FINANCIAL_NETWORK_STATUS));
            tenderCheckRDO.setResponseType(((String) tenderAttributes.get(TenderConstants.RESPONSE_TYPE)));
            tenderCheckRDO.setConversionFlag((String) tenderAttributes.get(TenderConstants.CONVERSION_CODE));
            tenderCheckRDO.setAuthorizationSequenceNumber((String) tenderAttributes.get(TenderConstants.CHECK_AUTH_SEQUENCE_NUMBER));
            //auth code is the authorization indicator for eCheck
            tenderCheckRDO.setAuthorizationCode((String) tenderAttributes.get(TenderConstants.AUTH_CODE));
            tenderCheckRDO.setAuthorizationResponse((String) tenderAttributes.get(TenderConstants.AUTH_RESPONSE));
            authorizationResponseCode = (String) tenderAttributes.get(TenderConstants.AUTH_RESPONSE_CODE);
        }
        String checkType = (String) tenderAttributes.get(TenderConstants.CHECK_AUTH_TYPE);
        // determine if auth is of check or echeck
        if (checkType != null)
        {
            if (checkType.equalsIgnoreCase("Check"))
            {
                tenderCheckRDO.setTypeCode(TenderLineItemConstantsIfc.TENDER_TYPE_CHECK);
            }
            else
            {
                tenderCheckRDO.setTypeCode(TenderLineItemConstantsIfc.TENDER_TYPE_E_CHECK);
            }
        }
    }

    /**
     * Indicates (Personal) Check is a NOT type of PAT Cash
     * @return false
     */
    public boolean isPATCash()
    {
        return false;
    }


    /**
     * This method determines if the auth check is echeck or check.
     */
    protected void setAuthCheckType()
    {
        TenderCheckIfc tenderCheckRDO = (TenderCheckIfc)tenderRDO;
        if (eCheckAuthRequired())
        {
            tenderCheckRDO.setTypeCode(TenderLineItemConstantsIfc.TENDER_TYPE_E_CHECK);
        }
        else
        {
            tenderCheckRDO.setTypeCode(TenderLineItemConstantsIfc.TENDER_TYPE_CHECK);
        }
    }
    /**
     * This methods converts from the legacy to the RDO.
     *
     * @param rdo
     *            EYSDomainIfc
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        assert(rdo instanceof TenderCheckIfc);

        tenderRDO = (TenderCheckIfc) rdo;
    }

    /**
     * This method converts from current to legacy.
     *
     * @return tenderRDO the legacy domain object.
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        return tenderRDO;
    }

    /**
     * This method converts from the current to the legacy.
     *
     * @param type
     * @return rdo
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class<? extends EYSDomainIfc> type)
    {
        return null;
    }
}
