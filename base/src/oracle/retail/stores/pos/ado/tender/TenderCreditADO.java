/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderCreditADO.java /main/44 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    blarsen   07/17/13 - Restoring audit tracenum. AJB reversals is using it.
 *    cgreene   06/06/13 - removed setting audit trace num since it is not
 *                         requried for credit
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   10/20/11 - correct setting of EntryMethod when getting the
 *                         tender atttributes
 *    sgu       09/16/11 - remove unused expiration date
 *    cgreene   09/12/11 - added support for setting balance left on prepaid
 *                         card
 *    cgreene   09/09/11 - made more sense out of setting EntryMethod, esp when
 *                         its given by the response
 *    blarsen   08/26/11 - Removed obsolete (and inappropriate)
 *                         isSignatureRequired() method.
 *    blarsen   08/10/11 - Removing authorizeResponseCode field since it is
 *                         'read-only' and seems misplaced for this class.
 *                         Fixed isAuthorized() to use the RDO's
 *                         authResponseCode.
 *    blarsen   08/02/11 - Misc cleanup. Renamed token to accountNumberToken.
 *    blarsen   07/28/11 - card-types and limit pamameters were removed as part
 *                         of 13.4's Advance Payment Foundation.
 *    cgreene   07/28/11 - implement credit decline manager override
 *    cgreene   07/26/11 - removed tenderauth and giftcard.activation tours and
 *                         financialnetwork interfaces.
 *    blarsen   07/22/11 - Changed isAuthorized() to use AuthCode since this
 *                         column is more likely to be filled after auth.
 *    cgreene   07/20/11 - added support for requiring signature for icc cards
 *    blarsen   07/15/11 - Fix misspelled word: retrival
 *    blarsen   07/14/11 - Added get/sets for reversal-related tender attribs.
 *    blarsen   07/12/11 - Added support for account number token and Retrieval
 *                         Reference Number.
 *    asinton   07/12/11 - fixed some entry method coding
 *    cgreene   07/12/11 - update generics
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    cgreene   06/29/11 - add token column and remove encrypted/hashed account
 *                         number column in credit-debit tender table.
 *    blarsen   06/16/11 - Saving enciphered card data and payment service
 *                         token onto tenderRDO so it can be persisted.
 *    cgreene   06/02/11 - Tweaks to support Servebase chipnpin
 *    blarsen   05/24/11 - Removed some dependencies on MSR device and expiry
 *                         date.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    asinton   10/01/10 - Changed logic to always require signature for
 *                         returns.
 *    asinton   09/22/10 - Adding Credit Card Accountability Responsibility and
 *                         Disclosure Act of 2009 changes.
 *    asinton   09/20/10 - Changes to incorporate new MinimumSigCapFor+<credit
 *                         type> parameter.
 *    asinton   09/08/10 - modified the journaling of voided credit tender
 *                         authorization since authorization is now done
 *                         asynchronously
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    asinton   05/06/10 - Added Prepaid Remaining Balance to receipt and
 *                         ejournal
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    asinton   04/02/10 - XbranchMerge asinton_bug-9545254 from
 *                         rgbustores_13.2x_generic_branch
 *    asinton   04/02/10 - Modified decodeAuthResponse so that previous data
 *                         does not get overwritten with data from a call
 *                         referral request.
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    asinton   04/01/10 - XbranchMerge asinton_bug-9544980 from main
 *    asinton   04/01/10 - In voidAuth moved the setting of voiding, voided
 *                         flags outside of the voidTenderHasAuthData block.
 *    sgu       03/24/10 - null pointer check for reversal response
 *    asinton   03/19/10 - set reversal to true in the request when voiding
 *                         tender
 *    dwfung    03/10/10 - Handling Training Mode Requests
 *    asinton   03/08/10 - Prevent overwritting of settlement data.
 *    vapartha  02/05/10 - Added code to set the encipheredData in the msrModel
 *                         to avoid a null pointer.
 *    abondala  01/03/10 - update header date
 *    asinton   12/10/09 - Changes per review comments
 *    asinton   12/03/09 - Changes to support credit card authorizations on
 *                         returns and voids.
 *    asinton   06/17/09 - Removing parameter MerchantNumber.
 *    asinton   03/05/09 - Updates to partially approved credit flow.
 *    asinton   03/02/09 - Fix for Partially approved credit.
 *    abondala  02/18/09 - check for layaway payment with HA card
 *    mdecama   01/28/09 - Setting the Authorization Amount in the
 *                         setOfflineApprovalInfo()
 *    vchengeg  11/07/08 - To fix BAT test failure
 *    abondala  11/06/08 - updated files related to reason codes
 *    sswamygo  11/05/08 - Checkin after merges
 *
 * ===========================================================================
 * $Log:
 *     23   360Commerce 1.22        1/18/2008 4:54:06 AM   Alan N. Sinton  CR
 *         29954: Refactor of EncipheredCardData to implement interface and be
 *          instantiated using a factory.
 *     22   360Commerce 1.21        12/20/2007 3:52:46 AM  Alan N. Sinton  CR
 *          29598: getTenderAttributes needs to put EncipheredCardData into the
 *            HashMap.
 *     21   360Commerce 1.20        12/14/2007 8:29:59 PM  Alan N. Sinton  CR
 *           29761: Removed non-PABP compliant methods and modified card RuleIfc
 *            to take an instance of EncipheredCardData.
 *     20   360Commerce 1.19        12/13/2007 6:17:38 AM  Alan N. Sinton  CR
 *          29761: FR 8: Prevent repeated decryption of PAN data.
 *     19   360Commerce 1.18        12/5/2007 7:48:27 AM   Alan N. Sinton  CR
 *          29598: code changes per code review.
 *     18   360Commerce 1.17        11/21/2007 1:29:17 PM  Deepti Sharma   CR
 *          29598: changes for credit/debit PAPB
 *     17   360Commerce 1.16        11/15/2007 9:39:55 PM  Christian Greene
 *          Belize merge - Changed comparision of MinimumAuthAmount to
 *          greater-than-equal-to so minimum amount is inclusive of amount
 *          required to authorize.
 *     16   360Commerce 1.15        8/3/2007 8:31:46 PM    Michael P. Barnett In
 *           setTenderAttributes(), remove the setting of the credit card
 *          expiration date to the ID expiration date.
 *     15   360Commerce 1.14        7/10/2007 4:34:18 AM   Alan N. Sinton  CR
 *          27494 - Removed constaint TenderLimitsIfc.TENDER_NO_LIMIT_AMOUNT in
 *           favor of a lazy init value returned by static method
 *          TenderLimits.getTenderNoLimitAmount().
 *     14   360Commerce 1.13        6/20/2007 12:11:27 AM  Maisa De Camargo
 *          Setting the Expiration Date with the value of the TenderAttribute
 *          ID_EXPIRATION_DATE.
 *          It was causing the House Account Transaction to fail and not be
 *          commited in the Db.
 *     13   360Commerce 1.12        6/14/2007 3:14:17 AM   Anda D. Cadar   Put
 *          back the check for null expiration date; credit card expiration
 *          date is not locale dependant, therefore the fix from 8.0.1 was
 *          rolled back
 *     12   360Commerce 1.11        6/12/2007 11:33:13 PM  Michael Boyd    merge
 *           from 8.0.1
 *     11   360Commerce 1.10        5/30/2007 7:38:09 PM   Anda D. Cadar   code
 *          cleanup
 *     10   360Commerce 1.9         5/22/2007 12:59:46 AM  Mathews Kochummen use
 *           credit card exp. date format for all locales
 *     9    360Commerce 1.8         5/21/2007 7:46:19 PM   Anda D. Cadar   EJ
 *          changes
 *     8    360Commerce 1.7         5/18/2007 7:49:17 PM   Anda D. Cadar
 *          always use decimalValue toString
 *     7    360Commerce 1.6         4/25/2007 7:22:55 PM   Anda D. Cadar   I18N
 *          merge
 *
 *     6    360Commerce 1.5         10/12/2006 6:47:49 PM  Christian Greene
 *          Adding new functionality for PasswordPolicy.  Employee password
 *          will now be persisted as a byte[] in hexadecimal.  Updates include
 *          UI changes, persistence changes, and AppServer configuration
 *          changes.  A database rebuild with the new SQL scripts will be
 *          required.
 *     5    360Commerce 1.4         1/26/2006 3:41:51 AM   Brett J. Larsen merge
 *           7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *     4    360Commerce 1.3         12/14/2005 4:12:32 AM  Barry A. Pape
 *          Base-lining of 7.1_LA
 *     3    360Commerce 1.2         4/1/2005 3:00:23 AM    Robert Pearse
 *     2    360Commerce 1.1         3/10/2005 9:55:55 PM   Robert Pearse
 *     1    360Commerce 1.0         2/11/2005 11:44:50 PM  Robert Pearse
 *    $: TenderCreditADO.java,v $
 *     7    .v710     1.2.2.0     9/21/2005 13:40:22     Brendan W. Farrell
 *          Initial Check in merge 67.
 *     6    .v700     1.2.3.2     12/27/2005 13:55:16    Deepanshu       CR
 *          8132: Date format for es_PR and fr_CA
 *     5    .v700     1.2.3.1     10/28/2005 11:48:45    Rohit Sachdeva  4780:
 *          HouseCardExpDateRequired  Parameter
 *     4    .v700     1.2.3.0     10/18/2005 16:19:18    Jason L. DeLeau Fix
 *          crash caused when expiration date for a house card was null and the
 *          parameter requiring an expiration date was turned on.
 *     3    360Commerce1.2         3/31/2005 15:30:23     Robert Pearse
 *     2    360Commerce1.1         3/10/2005 10:25:55     Robert Pearse
 *     1    360Commerce1.0         2/11/2005 12:14:50     Robert Pearse
 *    $
 *    Revision 1.18.2.1  2004/11/12 14:28:53  kll
 *    @scr 7337: JournalFactory extensibility initiative
 *
 *    Revision 1.18  2004/10/05 19:05:32  bwf
 *    @scr 7305 Add logging of authorization in order to get response time.
 *
 *    Revision 1.17  2004/08/05 21:12:50  blj
 *    @scr 6195 - corrected a problem with MSRModel data being lost for postvoids.
 *
 *    Revision 1.16  2004/07/31 16:09:37  bwf
 *    @scr 6551 Enable credit auth charge confirmation.
 *
 *    Revision 1.15  2004/07/26 22:45:17  blj
 *    @scr 5828 - added a check for training mode before validation.
 *
 *    Revision 1.14  2004/07/23 22:21:02  epd
 *    @scr 5963 (ServicesImpact) Updates to fix training mode and RegisterADO singleton refactoring
 *
 *    Revision 1.13  2004/07/23 20:46:05  bwf
 *    @scr 2141, 3289, 3290, 3291, 3292 Only use sysauthcode for offlines.
 *
 *    Revision 1.12  2004/07/14 22:11:17  kmcbride
 *    @scr 5954 (Services Impact): Adding log4j loggers to these classes to make them easier to debug, also fixed some catch statements that were not logging or re-throwing a new exception w/o nesting the original.
 *
 *    Revision 1.11  2004/07/12 21:42:19  bwf
 *    @scr 6125 Made available expiration validation of debit before pin.
 *
 *    Revision 1.10  2004/06/29 19:52:16  bwf
 *    @scr 5888 Display correct messages during credit time out.
 *
 *    Revision 1.9  2004/04/22 21:03:53  epd
 *    @scr 4513 Changed all toFormattedString() calls to getStringValue() calls
 *
 *    Revision 1.8  2004/04/08 21:12:41  tfritz
 *    @scr 3884 - Do not do check digit validation when in training mode.
 *
 *    Revision 1.7  2004/03/16 18:30:45  cdb
 *    @scr 0 Removed tabs from all java source code.
 *
 *    Revision 1.6  2004/02/25 18:32:52  bwf
 *    @scr 3883 Credit Rework.
 *
 *    Revision 1.5  2004/02/20 21:07:28  rsachdeva
 *    @scr  3820 ISD Integration
 *
 *    Revision 1.4  2004/02/13 18:41:33  rsachdeva
 *    @scr 3820 ISD Integration
 *
 *    Revision 1.3  2004/02/12 16:47:55  mcs
 *    Forcing head revision
 *
 *    Revision 1.2  2004/02/11 21:35:22  rsachdeva
 *    @scr 3820 ISD Integration
 *
 *    Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *    updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.16   Feb 05 2004 13:46:28   rhafernik
 * log4j changes
 *
 *    Rev 1.15   Jan 30 2004 12:49:54   bwf
 * Updated isAuthorized to fix problems when gift card reload fails and tender must be reversed.
 *
 *    Rev 1.14   Jan 19 2004 17:22:44   epd
 * Put back calls to DomainGateway
 *
 *    Rev 1.13   Jan 07 2004 15:25:52   epd
 * Refactoring to add MOD10 to debit
 *
 *    Rev 1.12   Jan 06 2004 16:08:02   epd
 * fixed 3642 - app crashes on bad mag stripe
 *
 *    Rev 1.11   Jan 06 2004 11:23:00   epd
 * refactorings to remove unfriendly references to TenderHelper and DomainGateway
 *
 *    Rev 1.10   Dec 19 2003 09:40:54   epd
 * updates to accommodate unit testing
 *
 *    Rev 1.9   Dec 04 2003 17:46:30   epd
 * Updates for debit auth
 *
 *    Rev 1.8   Dec 01 2003 19:06:26   epd
 * Updates for Credit/Debit
 *
 *    Rev 1.7   Dec 01 2003 17:49:18   epd
 * removed TO DO
 *
 *    Rev 1.6   Dec 01 2003 17:20:30   epd
 * fixed date parsing for swiped cards
 *
 *    Rev 1.5   Nov 13 2003 17:03:08   epd
 * Refactoring: updated to use new method to access context
 *
 *    Rev 1.4   Nov 12 2003 13:23:46   bwf
 * Added hashmap to authorize and reverse.
 *
 *    Rev 1.3   Nov 06 2003 13:57:38   epd
 * updates for OCC approval
 *
 *    Rev 1.2   Nov 05 2003 18:42:58   epd
 * updates for authorization
 *
 *    Rev 1.1   Nov 04 2003 18:23:46   epd
 * updates for auth
 *
 *    Rev 1.0   Nov 04 2003 11:13:12   epd
 * Initial revision.
 *
 *    Rev 1.10   Nov 03 2003 19:21:22   epd
 * updates for authorization
 *
 *    Rev 1.9   Nov 03 2003 11:49:36   epd
 * updated to include new id country
 *
 *    Rev 1.8   Nov 01 2003 15:08:48   epd
 * dev updates
 *
 *    Rev 1.7   Oct 31 2003 16:45:54   epd
 * added attributes to get/set tender attributes methods
 *
 *    Rev 1.6   Oct 30 2003 20:34:54   epd
 * development updates relating to authorization
 *
 *    Rev 1.5   Oct 28 2003 14:16:28   epd
 * Added method to return whether tender is already authorized
 *
 *    Rev 1.4   Oct 27 2003 18:47:14   epd
 * fixed credit logic
 *
 *    Rev 1.3   Oct 27 2003 18:23:34   epd
 * Added code for Credit tender
 *
 *    Rev 1.2   Oct 22 2003 19:16:02   epd
 * Updated to use card type method from TenderHelper
 *
 *    Rev 1.1   Oct 21 2003 10:00:58   epd
 * Refactoring.  Moved RDO tender to abstract class
 *
 *    Rev 1.0   Oct 17 2003 12:33:46   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.domain.tender.IntegratedChipCardDetailsIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;
import oracle.retail.stores.pos.ado.context.ADOContextIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalConstants;
import oracle.retail.stores.pos.ado.journal.JournalData;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.security.OverridableIfc;
import oracle.retail.stores.pos.ado.transaction.PaymentTransactionADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;

import org.apache.log4j.Logger;

/**
 * A credit tender object
 */
public class TenderCreditADO extends AbstractCardTender implements AuthorizableADOIfc, OverridableIfc, ReversibleTenderADOIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -536443449620914000L;

    /** The format of expiration dates as read from UI */
    public static final String CARD_DATE_FORMAT = "MM/yyyy";

    /**
     * Used to indicate OCC approval obtained or not.
     */
    protected boolean occApprovalObtained = false;

    /** Indicates whether this tender was reversed or not */
    protected boolean reversed = false;

    /** Indicates whether this tender was voided or not */
    protected boolean voided = false;

    /** Indicating that this tender is in the process of voiding itself **/
    protected boolean voiding = false;

    /** Flag to indicate that we should call the authorizer for a call referral */
    protected boolean authorizeCallReferral = false;

    /** Flag to indicate that the Referral was because the authorizer was offline. */
    protected boolean offlineReferral = false;

    /** logger */
    protected static final Logger logger = Logger.getLogger(TenderCreditADO.class);

    /**
     * No-arg constructor
     * Note: constructor protected by design for use by tender factory
     */
    protected TenderCreditADO() {}

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.AbstractTenderADO#initializeTenderRDO()
     */
    protected void initializeTenderRDO()
    {
        tenderRDO = DomainGateway.getFactory().getTenderChargeInstance();
    }

    /**
     * @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#getTenderType()
     */
    public TenderTypeEnum getTenderType()
    {
        return TenderTypeEnum.CREDIT;
    }

    /**
     * @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#getTenderType()
     */
    public CreditTypeEnum getCreditType()
    {
        if(logger.isInfoEnabled())
        {
            logger.info("Retrieving credit type...");
        }

        return determineCreditType(((TenderChargeIfc)tenderRDO).getEncipheredCardData());
    }

    /**
     * Convenience method to retrieve Store ID
     * This method was created to facilitate Unit Testing
     * @return
     */
    protected String getStoreID()
    {
        AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc)TourContext.getInstance().getTourBus().getCargo();
        return cargo.getRegister().getWorkstation().getStoreID();
    }

    /**
     * Convenience method to retrieve Register ID
     * This method was created to facilitate Unit Testing
     * @return
     */
    protected String getRegisterID()
    {
        AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc)TourContext.getInstance().getTourBus().getCargo();
        return cargo.getRegister().getWorkstation().getWorkstationID();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.AuthorizableADOIfc#isAuthorized()
     */
    public boolean isAuthorized()
    {
        boolean returnCode = false;

        if(!StringUtils.isBlank(((TenderChargeIfc)tenderRDO).getAuthorizationCode()) ||
                        !StringUtils.isBlank(((TenderChargeIfc)tenderRDO).getAuthResponseCode()) ||
                        !StringUtils.isBlank(((TenderChargeIfc)tenderRDO).getAuthorizationResponse()))
        {
            returnCode = true;
        }
        return returnCode || isVoided();
    }

    /**
     * Convenience method to get current operator's Employee ID
     * Method added to facilitate Unit testing.
     * @return
     */
    protected String getOperatorEmployeeID()
    {
        return ((UserAccessCargo)getContext().getBus().getCargo()).getOperator().getEmployeeID();
    }

    /**
     * Convenience method to get Register Journal
     * Method added to facilitate Unit testing.
     * @return
     */
    protected RegisterJournalIfc getRegisterJournal()
    {
        return getJournalFactory().getRegisterJournal();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.security.OverridableIfc#override(oracle.retail.stores.domain.employee.EmployeeIfc)
     */
    public boolean override(EmployeeIfc overrideEmployee, int roleFunction)
    {
        if(logger.isInfoEnabled())
        {
            logger.info("Attempting override tender limit...");
        }

        // Get the context
        ADOContextIfc context = getContext();
        // Get the security manager
        SecurityManagerIfc sm = (SecurityManagerIfc)getManager(SecurityManagerIfc.TYPE);
        boolean result = false;
        try
        {
            // Attempt Tender Limit override with this employee
            result = sm.override(context.getApplicationID(),
                    overrideEmployee.getLoginID(),
                    overrideEmployee.getPasswordBytes(),
                    roleFunction);

            // journal the override attempt
            JournalData data = new JournalData();
            // add operator, override operator, function, and result to journal data.
            data.putJournalData(JournalConstants.OPERATOR, getOperatorEmployeeID());
            data.putJournalData(JournalConstants.OVERRIDE_OPERATOR, overrideEmployee.getEmployeeID());
            data.putJournalData(JournalConstants.FUNCTION, Integer.toString(RoleFunctionIfc.OVERRIDE_DECLINED_CREDIT));
            data.putJournalData(JournalConstants.BOOLEAN, new Boolean(result).toString());
            RegisterJournalIfc regJournal = getRegisterJournal();
            regJournal.journal(data, JournalFamilyEnum.TENDER, JournalActionEnum.OVERRIDE);
        }
        catch (LoginException e)
        {
            // do nothing.  Obviously the override didn't work

            // KLM: This actually handled below, but let's at least log it
            //
            logger.warn("Caught exception when trying to override tender limit: " + e);
        }

        // If the override succeeded, alter the internal state of this tender
        if (result)
        {

            ((TenderChargeIfc)tenderRDO).setAuthorizationMethod(AuthorizableTenderIfc.AUTHORIZATION_METHOD_MANUAL);
            ((TenderChargeIfc)tenderRDO).setAuthorizationStatus(AuthorizableTenderIfc.AUTHORIZATION_STATUS_APPROVED);
            // only reset auth code if not null (may have been entered manually
            // on call referral screen).
            if (((TenderChargeIfc)tenderRDO).getAuthorizationCode() == null)
            {
                ((TenderChargeIfc)tenderRDO).setAuthorizationCode(((TenderChargeIfc)tenderRDO).getAuthorizationResponse());
            }

            //journal the result using utility method in cargo
            // only journal for successful override.  We could have multiple
            // unsuccessful overrides and only want to journal when we have a resolution
            RegisterJournalIfc journal = getRegisterJournal();
            journal.journal(this, JournalFamilyEnum.TENDER, JournalActionEnum.AUTHORIZATION);
        }
        else
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("Overriding tender limit for employee: " + overrideEmployee.getLoginID() + " failed");
            }

            ((TenderChargeIfc)tenderRDO).setAuthorizationMethod(AuthorizableTenderIfc.AUTHORIZATION_METHOD_MANUAL);
            ((TenderChargeIfc)tenderRDO).setAuthorizationStatus(AuthorizableTenderIfc.AUTHORIZATION_STATUS_DECLINED);
        }

        return result;
    }

    /**
     * determine whether we previously captured customer info
     */
    public boolean capturedCustomerInfo()
    {
        boolean captured = false;
        if ((!((TenderChargeIfc)tenderRDO).getPersonalIDType().getCode().equals(CodeConstantsIfc.CODE_UNDEFINED)) &&
                ((TenderChargeIfc)tenderRDO).getIDState() != null && ((TenderChargeIfc)tenderRDO).getIDState().length() > 0 &&
                ((TenderChargeIfc)tenderRDO).getIDExpirationDate() != null)
        {
            captured = true;
        }
        return captured;
    }

    /**
     * @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#validate()
     */
    public void validate() throws TenderException
    {
        // empty implementation.  As of 13.4/Advanced Payment Foundation, thirdparty payment
        // system will validate bin ranges, check digits, etc.
    }

    /**
     * Validates we can determine the card type
     *
     */
    protected void validateKnownCardType()
    throws TenderException
    {
        // Determine the card type
        CreditTypeEnum creditType = determineCreditType(((TenderChargeIfc)tenderRDO).getEncipheredCardData());

        // If we don't know the type, punt.
        if (creditType == CreditTypeEnum.UNKNOWN)
        {
            throw new TenderException("Unknown Card Type",
                    TenderErrorCodeEnum.UNKNOWN_CARD_TYPE);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.TenderADOIfc#getTenderAttributes()
     */
    public HashMap<String,Object> getTenderAttributes()
    {
        HashMap<String,Object> map = new HashMap<String,Object>(40);
        map.put(TenderConstants.TENDER_TYPE, getTenderType());
        map.put(TenderConstants.AMOUNT,
                getAmount().getStringValue());
        // occ approval code
        map.put(TenderConstants.OCC_APPROVAL_CODE, ((TenderChargeIfc)tenderRDO).getOccApprovalCode());
        if (capturedCustomerInfo())
        {
            if(((TenderChargeIfc)tenderRDO).getPersonalIDType()!= null)
            {
                map.put(TenderConstants.ID_TYPE, ((TenderChargeIfc)tenderRDO).getPersonalIDType().getCodeName());
                map.put(TenderConstants.LOCALIZED_ID_TYPE, ((TenderChargeIfc)tenderRDO).getPersonalIDType());
            }
            map.put(TenderConstants.ID_COUNTRY, ((TenderChargeIfc)tenderRDO).getIDCountry());
            map.put(TenderConstants.ID_STATE, ((TenderChargeIfc)tenderRDO).getIDState());
            String expDateString = ((TenderChargeIfc)tenderRDO).getIDExpirationDate()
            .toFormattedString(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
            map.put(TenderConstants.ID_EXPIRATION_DATE, expDateString);
        }

        map.put(TenderConstants.MSR_MODEL, this.msrModel);
        if (((TenderChargeIfc)tenderRDO).getEntryMethod() == null)
        {
            map.put(TenderConstants.ENTRY_METHOD, (msrModel != null)? EntryMethod.Swipe : EntryMethod.Manual);
        }
        else
        {
            map.put(TenderConstants.ENTRY_METHOD, ((TenderChargeIfc)tenderRDO).getEntryMethod());
        }
        // always put these in regardless of entry method
        map.put(TenderConstants.NUMBER, ((TenderChargeIfc)tenderRDO).getCardNumber());
        map.put(TenderConstants.EXPIRATION_DATE, ((TenderChargeIfc)tenderRDO).getExpirationDateString());
        map.put(TenderConstants.ENCIPHERED_CARD_DATA, ((TenderChargeIfc)tenderRDO).getEncipheredCardData());
        map.put(TenderConstants.SIGNATURE_REQUIRED, ((TenderChargeIfc)tenderRDO).isSignatureRequired());

        if (((TenderChargeIfc)tenderRDO).getAccountNumberToken() != null)
        {
            map.put(TenderConstants.ACCOUNT_NUMBER_TOKEN, ((TenderChargeIfc)tenderRDO).getAccountNumberToken());
        }

        // authorization info
        map.put(TenderConstants.AUTH_STATUS, new Integer(((TenderChargeIfc)tenderRDO).getAuthorizationStatus()));
        // Only put in map if we have authorized (status should be non-zero)
        if (isAuthorized())
        {
            map.put(TenderConstants.AUTH_AMOUNT, ((TenderChargeIfc)tenderRDO).getAuthorizationAmount()
                    .getStringValue());
            map.put(TenderConstants.AUTH_CODE, ((TenderChargeIfc)tenderRDO).getAuthorizationCode());
            map.put(TenderConstants.AUTH_METHOD, ((TenderChargeIfc)tenderRDO).getAuthorizationMethod());
            map.put(TenderConstants.AUTH_RESPONSE, ((TenderChargeIfc)tenderRDO).getAuthorizationResponse());
            map.put(TenderConstants.FINANCIAL_NETWORK_STATUS, ((TenderChargeIfc)tenderRDO).getFinancialNetworkStatus());
            map.put(TenderConstants.AUTH_RESPONSE_CODE, ((TenderChargeIfc)tenderRDO).getAuthResponseCode());
            map.put(TenderConstants.SETTLEMENT_DATA, ((TenderChargeIfc)tenderRDO).getSettlementData());
            map.put(TenderConstants.AUTH_DATE_TIME, ((TenderChargeIfc)tenderRDO).getAuthorizedDateTime());
            map.put(TenderConstants.PREPAID_REMAINING_BALANCE, ((TenderChargeIfc)tenderRDO).getPrepaidRemainingBalance());

            // for reversals
            map.put(TenderConstants.JOURNAL_KEY, ((TenderChargeIfc)tenderRDO).getJournalKey());
            map.put(TenderConstants.AUTH_SEQUENCE_NUMBER, ((TenderChargeIfc)tenderRDO).getRetrievalReferenceNumber());
            map.put(TenderConstants.LOCAL_TIME, ((TenderChargeIfc)tenderRDO).getAuthorizationTime());
            map.put(TenderConstants.LOCAL_DATE, ((TenderChargeIfc)tenderRDO).getAuthorizationDate());
            map.put(TenderConstants.ACCOUNT_DATA_SOURCE, ((TenderChargeIfc)tenderRDO).getAccountDataSource());
            map.put(TenderConstants.PAYMENT_SERVICE_INDICATOR, ((TenderChargeIfc)tenderRDO).getPaymentServiceIndicator());
            map.put(TenderConstants.TRANSACTION_ID, ((TenderChargeIfc)tenderRDO).getTransactionIdentificationNumber());
            map.put(TenderConstants.AUTH_RESPONSE_CODE, ((TenderChargeIfc)tenderRDO).getAuthResponseCode());
            map.put(TenderConstants.VALIDATION_CODE, ((TenderChargeIfc)tenderRDO).getValidationCode());
            map.put(TenderConstants.AUTH_SOURCE, ((TenderChargeIfc)tenderRDO).getAuthorizationSource());
            map.put(TenderConstants.HOST_REFERENCE, ((TenderChargeIfc)tenderRDO).getHostReference());
            map.put(TenderConstants.TRACE_NUMBER, ((TenderChargeIfc)tenderRDO).getTraceNumber());
        }

        return map;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.TenderADOIfc#setTenderAttributes(java.util.HashMap)
     */
    public void setTenderAttributes(HashMap<String,Object> tenderAttributes)
        throws TenderException
    {
        // get the amount
        CurrencyIfc amount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
        ((TenderChargeIfc)tenderRDO).setAmountTender(amount);
        // set the authorization (original) request amount
        ((TenderChargeIfc)tenderRDO).setAuthorizationRequestAmount(amount);
        // occ approval code
        ((TenderChargeIfc)tenderRDO).setOccApprovalCode((String)tenderAttributes.get(TenderConstants.OCC_APPROVAL_CODE));
        // card info
        EncipheredCardDataIfc encipheredCardData = (EncipheredCardDataIfc)tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA);
        ((TenderChargeIfc)tenderRDO).setEncipheredCardData(encipheredCardData);
        EntryMethod entryMethod = (EntryMethod)tenderAttributes.get(TenderConstants.ENTRY_METHOD);
        ((TenderChargeIfc)tenderRDO).setEntryMethod(entryMethod);
        boolean swiped = isCreditCardSwiped(tenderAttributes);
        if (entryMethod == null)
        {
            ((TenderChargeIfc)tenderRDO).setEntryMethod((swiped)? EntryMethod.Swipe : EntryMethod.Manual);
        }

        ((TenderChargeIfc)tenderRDO).setExpirationDateString((String)tenderAttributes.get(TenderConstants.EXPIRATION_DATE));

        // Authorization info
        ((TenderChargeIfc)tenderRDO).setAuthorizationStatus(((Integer)tenderAttributes.get(TenderConstants.AUTH_STATUS)).intValue());
        // Only get from map if authorization occurred
        if (tenderAttributes.get(TenderConstants.AUTH_RESPONSE) != null)
        {
            ((TenderChargeIfc)tenderRDO).setAuthorizationAmount(
                    parseAmount((String)tenderAttributes.get(TenderConstants.AUTH_AMOUNT)));
            ((TenderChargeIfc)tenderRDO).setAuthorizationCode((String)tenderAttributes.get(TenderConstants.AUTH_CODE));
            ((TenderChargeIfc)tenderRDO).setAuthorizationMethod((String)tenderAttributes.get(TenderConstants.AUTH_METHOD));
            ((TenderChargeIfc)tenderRDO).setAuthorizationResponse((String)tenderAttributes.get(TenderConstants.AUTH_RESPONSE));
            ((TenderChargeIfc)tenderRDO).setFinancialNetworkStatus((String)tenderAttributes.get(TenderConstants.FINANCIAL_NETWORK_STATUS));
            ((TenderChargeIfc)tenderRDO).setSettlementData((String)tenderAttributes.get(TenderConstants.SETTLEMENT_DATA));
            ((TenderChargeIfc)tenderRDO).setAuthorizedDateTime((EYSDate)tenderAttributes.get(TenderConstants.AUTH_DATE_TIME));
            ((TenderChargeIfc)tenderRDO).setAccountNumberToken((String)tenderAttributes.get(TenderConstants.ACCOUNT_NUMBER_TOKEN));
            ((TenderChargeIfc)tenderRDO).setPrepaidRemainingBalance((CurrencyIfc)tenderAttributes.get(TenderConstants.PREPAID_REMAINING_BALANCE));

            // for reversals
            ((TenderChargeIfc)tenderRDO).setJournalKey((String)tenderAttributes.get(TenderConstants.JOURNAL_KEY));
            ((TenderChargeIfc)tenderRDO).setRetrievalReferenceNumber((String)tenderAttributes.get(TenderConstants.AUTH_SEQUENCE_NUMBER));
            ((TenderChargeIfc)tenderRDO).setAuthorizationTime((String)tenderAttributes.get(TenderConstants.LOCAL_TIME));
            ((TenderChargeIfc)tenderRDO).setAuthorizationDate((String)tenderAttributes.get(TenderConstants.LOCAL_DATE));
            ((TenderChargeIfc)tenderRDO).setAccountDataSource((String)tenderAttributes.get(TenderConstants.ACCOUNT_DATA_SOURCE));
            ((TenderChargeIfc)tenderRDO).setPaymentServiceIndicator((String)tenderAttributes.get(TenderConstants.PAYMENT_SERVICE_INDICATOR));
            ((TenderChargeIfc)tenderRDO).setTransactionIdentificationNumber((String)tenderAttributes.get(TenderConstants.TRANSACTION_ID));
            ((TenderChargeIfc)tenderRDO).setAuthResponseCode((String)tenderAttributes.get(TenderConstants.AUTH_RESPONSE_CODE));
            ((TenderChargeIfc)tenderRDO).setValidationCode((String)tenderAttributes.get(TenderConstants.VALIDATION_CODE));
            ((TenderChargeIfc)tenderRDO).setAuthorizationSource((String)tenderAttributes.get(TenderConstants.AUTH_SOURCE));
            ((TenderChargeIfc)tenderRDO).setHostReference((String)tenderAttributes.get(TenderConstants.HOST_REFERENCE));
            ((TenderChargeIfc)tenderRDO).setTraceNumber((String)tenderAttributes.get(TenderConstants.TRACE_NUMBER));
            ((TenderChargeIfc)tenderRDO).setAuditTraceNumber((String)tenderAttributes.get(TenderConstants.SYSTEM_TRACE_AUDIT_NUMBER));
        }

        // set the details from the card swiped
        ((TenderChargeIfc)tenderRDO).setICCDetails((IntegratedChipCardDetailsIfc)tenderAttributes.get(TenderConstants.ICC_DETAILS));
        if (tenderAttributes.get(TenderConstants.SIGNATURE_REQUIRED) != null)
        {
            ((TenderChargeIfc)tenderRDO).setSignatureRequired((Boolean)tenderAttributes.get(TenderConstants.SIGNATURE_REQUIRED));
        }

        // ID info (only if captured)
        if (tenderAttributes.get(TenderConstants.ID_TYPE) != null &&
                tenderAttributes.get(TenderConstants.ID_COUNTRY) != null &&
                tenderAttributes.get(TenderConstants.ID_STATE) != null &&
                tenderAttributes.get(TenderConstants.ID_EXPIRATION_DATE) != null)
        {
            ((TenderChargeIfc)tenderRDO).setPersonalIDType((LocalizedCodeIfc)tenderAttributes.get(TenderConstants.LOCALIZED_ID_TYPE));
            ((TenderChargeIfc)tenderRDO).setIDCountry((String)tenderAttributes.get(TenderConstants.ID_COUNTRY));
            ((TenderChargeIfc)tenderRDO).setIDState((String)tenderAttributes.get(TenderConstants.ID_STATE));
            EYSDate idExpDate = parseExpirationDate(CARD_DATE_FORMAT,
                    (String)tenderAttributes.get(TenderConstants.ID_EXPIRATION_DATE));
            ((TenderChargeIfc)tenderRDO).setIDExpirationDate(idExpDate);

        }
        // Perform additional steps
        ((TenderChargeIfc)tenderRDO).setCardType(determineCreditType(encipheredCardData).toString());
    }

    /**
     * Returns a boolean indicating whether or not the
     * card was swiped.  This decision is based on whether
     * we have an MSR model or not and on the entry method of the tender item.
     * @return card swiped boolean flag.
     */
    public boolean isCreditCardSwiped()
    {
        boolean isCardSwiped = false;
        if (((TenderChargeIfc)tenderRDO).getEntryMethod() != null)
        {
            if (((TenderChargeIfc)tenderRDO).getEntryMethod().equals(EntryMethod.Swipe))
            {
                isCardSwiped = true;
            }
        }
        return isCardSwiped;
    }

    /**
     * Returns a boolean indicating whether or not the
     * card was swiped.  This decision is based on whether the
     * tender attributes has the MSR model or not and on the entry method of the tender item.
     * @return card swiped boolean flag.
     */
    public boolean isCreditCardSwiped(HashMap<String,Object> tenderAttributes)
    {
        boolean isSwiped = false;

        if (tenderAttributes.get(TenderConstants.MSR_MODEL) != null)
        {
            isSwiped=true;
        }
        else if (tenderAttributes.get(TenderConstants.ENTRY_METHOD) != null)
        {
            if (tenderAttributes.get(TenderConstants.ENTRY_METHOD).equals(EntryMethod.Swipe))
            {
                isSwiped = true;
            }
            else
            {
                isSwiped = false;
            }
        }
        return isSwiped;
    }

    /**
     * Indicates Credit is a NOT type of PAT Cash
     *
     * @return false
     */
    public boolean isPATCash()
    {
        return false;
    }

    /**
     * This method returns the credit type.
     *
     * @return
     */
    protected CreditTypeEnum determineCreditType(EncipheredCardDataIfc encipheredCardData)
    {
        CreditTypeEnum type = CreditTypeEnum.UNKNOWN;
        if (encipheredCardData != null)
        {
            if (!Util.isEmpty(encipheredCardData.getCardName()))
            {
                type = CreditTypeEnum.makeEnumFromString(encipheredCardData.getCardName());
            }
        }
        return type;
    }

    /**
     * This method takes in a EncipheredCardData instance and returns the credit
     * type.
     *
     * @param cardData
     * @return
     * @see oracle.retail.stores.pos.ado.tender.TenderBaseADO#determineCreditType(java.lang.String)
     */
    protected static CreditTypeEnum getCreditType(EncipheredCardDataIfc cardData)
    {
        BusIfc bus = TourContext.getInstance().getTourBus();
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        return utility.determineCreditType(cardData);
    }

    /**
     * Utility method to determine whether we should be processing with an
     * expiration date or not
     *
     * @return boolean true if Card expiration Date required
     */
    protected boolean isExpirationDateRequired()
    {
        boolean result = true;

        UtilityIfc util = getUtility();
        // if house card and parameter setting is set to not required
        //The ID Expiration Date is different from House Card expiration Date.
        //This controls the House Card Expiration Date.
        if (getCreditType() == CreditTypeEnum.HOUSECARD &&
                util.getParameterValue("HouseCardExpDateRequired", "N").equals("N"))
        {
            result = false;
        }

        return result;
    }

    /**
     * @return
     */
    public boolean isOccApprovalObtained()
    {
        return occApprovalObtained;
    }

    /**
     * @param occApprovalObtained
     */
    public void setOccApprovalObtained(boolean occApprovalObtained)
    {
        this.occApprovalObtained = occApprovalObtained;
    }

    /**
     * This static method is called to see if there is a house account being
     * used for a house account payment.
     *
     * @param cardData
     * @param trans
     * @return
     * @throws TenderException
     */
    public static void checkHouseAcctOnHousePayment(EncipheredCardDataIfc cardData,
            RetailTransactionADOIfc trans)
    throws TenderException
    {
        TransactionIfc transIfc = (TransactionIfc)trans.toLegacy();

        if(getCreditType(cardData) == CreditTypeEnum.HOUSECARD &&
              trans instanceof PaymentTransactionADO && transIfc.getTransactionType() != TransactionIfc.TYPE_LAYAWAY_PAYMENT)
        {
            throw new TenderException("Cannot make House Account payment with House Account card",
                    TenderErrorCodeEnum.INVALID_TENDER_TYPE);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    public Map<String,Object> getJournalMemento()
    {
        // reuse tender attributes for memento
        Map<String,Object> memento = getTenderAttributes();
        TenderChargeIfc tenderChargeRDO = (TenderChargeIfc)tenderRDO;
        memento.put(JournalConstants.DESCRIPTOR, getTenderType().toString());
        memento.put(JournalConstants.CARD_TYPE, tenderChargeRDO.getCardType());
        memento.put(JournalConstants.ENTRY_METHOD, tenderChargeRDO.getEntryMethod());
        memento.put(TenderConstants.REMAINING_BALANCE, tenderChargeRDO.getPrepaidRemainingBalance());
        memento.put(TenderConstants.ACCOUNT_APR, tenderChargeRDO.getAccountAPR());
        memento.put(TenderConstants.ACCOUNT_APR_TYPE, tenderChargeRDO.getAccountAPRType());
        memento.put(TenderConstants.PROMOTION_APR, tenderChargeRDO.getPromotionAPR());
        memento.put(TenderConstants.PROMOTION_APR_TYPE, tenderChargeRDO.getPromotionAPRType());
        memento.put(TenderConstants.PROMOTION_DESCRIPTION, tenderChargeRDO.getPromotionDescription());
        memento.put(TenderConstants.PROMOTION_DURATION, tenderChargeRDO.getPromotionDuration());

        return memento;
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        assert(rdo instanceof TenderChargeIfc);
        super.fromLegacy(rdo);
        tenderRDO = (TenderChargeIfc)rdo;
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        return tenderRDO;
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class<? extends EYSDomainIfc> type)
    {
        return toLegacy();
    }


    // methods to support ReversibleTenderADOIfc

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.tender.ReversibleTenderADOIfc#isReversed()
     */
    public boolean isReversed()
    {
        return reversed;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.tender.ReversibleTenderADOIfc#isVoided()
     */
    public boolean isVoided()
    {
        return voided;
    }

    /**
     * Tests the tender to be voided to see if data from original authorization exists.
     * If no authorization data present, then no need reversing.
     * @param voidTenderCredit
     * @return
     */
    protected boolean voidTenderHasAuthData(TenderChargeIfc voidTenderCredit)
    {
        boolean canVoid = false;
        if(voidTenderCredit != null && !Util.isEmpty(voidTenderCredit.getSettlementData()))
        {
            canVoid = true;
        }
        return canVoid;
    }

    /**
     * Sets the flag to call the authorizer in the case of a call referral.  The offline flag
     * indicates whether the message to the authorizer indicates if the referral authorization
     * was obtained because offline or online.
     * @param authorizeCallReferral
     * @param offlineReferral
     */
    public void setAuthorizeCallReferral(boolean authorizeCallReferral, boolean offlineReferral)
    {
        this.authorizeCallReferral = authorizeCallReferral;
        this.offlineReferral = offlineReferral;
    }

    /**
     * Returns the flag of whether the call referral authorization should occur.
     * @return
     */
    public boolean isAuthorizeCallReferral()
    {
        return this.authorizeCallReferral;
    }

}
