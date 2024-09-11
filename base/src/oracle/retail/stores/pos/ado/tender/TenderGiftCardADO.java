/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderGiftCardADO.java /main/37 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    asinton   05/02/13 - set the authroization code on the tenderRDO in
 *                         setTenderAttributes method
 *    icole     02/28/13 - Forward Port Print trace number on receipt for gift
 *                         cards, required by ACI.
 *    rgour     01/30/13 - gift card redeem transaction's approval code is
 *                         stored in database
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    asinton   02/08/12 - XbranchMerge asinton_bug-13636517 from
 *                         rgbustores_13.4x_generic_branch
 *    asinton   08/06/12 - get entry method from tenderRDO object in
 *                         getTenderAttributes method.
 *    blarsen   06/26/12 - Setting the isSignatureRequired from attributes.
 *                         Signature slip required code being returned by MPOS
 *                         API for gift cards..
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    asinton   02/08/12 - set gift card status to Active only when operation
 *                         preformed while online
 *    asinton   02/02/12 - XbranchMerge asinton_bug-13645789 from
 *                         rgbustores_13.4x_generic_branch
 *    asinton   02/02/12 - removed call to deprecated constant
 *                         OVERRIDE_CALL_REFERRAL_ACCEPT_GIFT_CARD
 *    cgreene   09/22/11 - set status to Unknown in Reentry mode
 *    jswan     09/21/11 - Cleaned up TenderGiftCard and TenderGiftCardTest.
 *    jswan     09/19/11 - Remove the MaximumCashChangeForGiftCard parameter
 *                         and references.
 *    jswan     09/12/11 - Modifications for reversals of Gift Cards when
 *                         escaping from the Tender Tour.
 *    cgreene   08/08/11 - Switch giftcard action to requestSubtype to avoid
 *                         clash with requestType.
 *    jswan     08/04/11 - Reversed previous change to status code; all but
 *                         classes except GiftCardActionSite set STATUS_CODE as
 *                         an Integer. The correct fix is to modify
 *                         GiftCardActionSite.
 *    blarsen   08/02/11 - Renamed token to accountNumberToken to be
 *                         consistent.
 *    cgreene   08/01/11 - correct casting of statuscode
 *    blarsen   07/28/11 - Auth timeout parameters delete in 13.4. These were
 *                         moved into the payment technician layer.
 *    cgreene   07/26/11 - moved StatusCode to GiftCardifc
 *    cgreene   07/26/11 - removed tenderauth and giftcard.activation tours and
 *                         financialnetwork interfaces.
 *    blarsen   07/14/11 - added auth journal key to get/set tender attribs
 *    asinton   07/12/11 - fixed some entry method coding
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    asinton   06/29/11 - Refactored to use EntryMethod and
 *                         AuthorizationMethod enums.
 *    cgreene   06/29/11 - add token column and remove encrypted/hashed account
 *                         number column in credit-debit tender table.
 *    blarsen   06/16/11 - Saving payment service token to tenderRDO so it can
 *                         be persisted.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    cgreene   05/20/11 - implemented enums for reponse code and giftcard
 *                         status code
 *    kelesika  12/15/10 - Gift card depletion during return transaction
 *    npoola    10/25/10 - added a null check to the response authorize amount
 *    npoola    10/18/10 - set the correct Authorized amount for Giftcard
 *                         response
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    asinton   03/21/10 - Preventing a ClassCastException
 *    cgreene   03/18/10 - set gift card status when available. This allows
 *                         training mode gift cards to work
 *    asinton   03/12/10 - Added code to send the Scanned (Automatic), Swiped
 *                         (MagSwipe), and Manual entry values in the request
 *                         object for the authorizer.
 *    dwfung    03/10/10 - Handling Training Mode Requests
 *    abondala  01/03/10 - update header date
 *    asinton   06/17/09 - Removing parameter MerchantNumber.
 *    vigopina  03/23/09 - Made changes to save the entry method in the
 *                         giftCard object and also check if the entry method
 *                         is different in case of the authorized gift card. if
 *                         different then to reset the original entry method.
 *    vigopina  03/20/09 - Changes made to the TenderGiftCardADO class to set
 *                         the correct entry method to the gift card object.
 *    nkgautam  02/06/09 - Fix for incorrect gift card remaining balance
 *    vchengeg  11/07/08 - To fix BAT test failure
 *
 * ===========================================================================
 * $Log:
 |    23    360Commerce 1.22        3/28/2008 4:05:39 AM   Sameer Thajudin When
 |          a special order containing multiple items is cancelled, the amount
 |          needs to be refunded back. When refunding the amount to the
 |          GiftCard,
 |          the CurrencyIfc object returned by getAmount() has to be negative.
 |          Only then will the amount be added to the GiftCard.
 |
 |          Previously, when the CurrencyIfc object was returned by
 |          getAmount(),its abs() function was also called. This substracted
 |          the amount from the GiftCard when a special order containing
 |          multiple items was cancelled. The previous line of code referred to
 |          here is
 |          String amount = amount().abs().getDecimalValue().toString();
 |
 |          The fix at line 348 is
 |          String amount = getAmount().getDecimalValue().toString();
 |    22    360Commerce 1.21        3/21/2008 12:03:29 PM  Manikandan Chellapan
 |          CR#301005 Modified Giftcard EJs to log truncated card number.
 |    21    360Commerce 1.20        3/12/2008 4:37:55 AM   Michael P. Barnett In
 |          getTenderAttributes, set the NUMBER value to the last 4 digits of
 |          the gift card account number rather than the encrypted value.
 |    20    360Commerce 1.19        1/18/2008 4:54:06 AM   Alan N. Sinton  CR
 |          29954: Refactor of EncipheredCardData to implement interface and be
 |          instantiated using a factory.
 |    19    360Commerce 1.18        1/11/2008 12:35:19 AM  Alan N. Sinton  CR
 |          29761:  Code review changes per Tony Zgarba and Jack Swan.
 |    18    360Commerce 1.17        12/20/2007 5:31:59 AM  Alan N. Sinton  CR
 |          29598: Fixed some broken unittests.
 |    17    360Commerce 1.16        12/14/2007 8:29:59 PM  Alan N. Sinton  CR
 |          29761: Removed non-PABP compliant methods and modified card RuleIfc
 |          to take an instance of EncipheredCardData.
 |    16    360Commerce 1.15        12/13/2007 6:17:38 AM  Alan N. Sinton  CR
 |          29761: FR 8: Prevent repeated decryption of PAN data.
 |    15    360Commerce 1.14        12/5/2007 7:48:27 AM   Alan N. Sinton  CR
 |          29598: code changes per code review.
 |    14    360Commerce 1.13        11/21/2007 1:29:17 PM  Deepti Sharma   CR
 |          29598: changes for credit/debit PAPB
 |    13    360Commerce 1.12        11/15/2007 9:39:55 PM  Christian Greene
 |          Belize merge - Changed comparision of MinimumAuthAmount to
 |          greater-than-equal-to so minimum amount is inclusive of amount
 |          required to authorize.
 |    12    360Commerce 1.11        7/25/2007 9:19:08 PM   Michael Boyd
 |          changed to get remainng balance from auth response instead of doing
 |          another inq.  Inq was returning the same for both amount.
 |    11    360Commerce 1.10        5/30/2007 7:31:57 PM   Anda D. Cadar   code
 |          cleanup
 |    10    360Commerce 1.9         5/18/2007 7:49:17 PM   Anda D. Cadar
 |          always use decimalValue toString
 |    9     360Commerce 1.8         4/25/2007 7:22:54 PM   Anda D. Cadar   I18N
 |          merge
 |
 |    8     360Commerce 1.7         10/12/2006 6:47:49 PM  Christian Greene
 |          Adding new functionality for PasswordPolicy.  Employee password
 |          will now be persisted as a byte[] in hexadecimal.  Updates include
 |          UI changes, persistence changes, and AppServer configuration
 |          changes.  A database rebuild with the new SQL scripts will be
 |          required.
 |    7     360Commerce 1.6         7/21/2006 6:36:38 PM   Keith L. Lesikar Gift
 |          Card Reload functionality
 |    6     360Commerce 1.5         2/15/2006 10:43:58 PM  Jason L. DeLeau 7468:
 |          Use the Entry Method attribute to determine the entry method,
 |          instead of some convoluted logic.
 |    5     360Commerce 1.4         1/26/2006 3:41:51 AM   Brett J. Larsen merge
 |          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 |    4     360Commerce 1.3         12/14/2005 4:12:33 AM  Barry A. Pape
 |          Base-lining of 7.1_LA
 |    3     360Commerce 1.2         4/1/2005 3:00:24 AM    Robert Pearse
 |    2     360Commerce 1.1         3/10/2005 9:55:57 PM   Robert Pearse
 |    1     360Commerce 1.0         2/11/2005 11:44:52 PM  Robert Pearse
 |   $: TenderGiftCardADO.java,v $
 |    5    .v710     1.2.2.0     9/21/2005 13:40:23     Brendan W. Farrell
 |         Initial Check in merge 67.
 |    4    .v700     1.2.3.0     11/17/2005 17:40:40    Deepanshu       CR
 |         5361: Moved puting initial balance and remaining balance in the
 |         hashmap, outside the if block. Set the initial and current balance
 |         in the GiftCardIfc
 |    3    360Commerce1.2         3/31/2005 15:30:24     Robert Pearse
 |    2    360Commerce1.1         3/10/2005 10:25:57     Robert Pearse
 |    1    360Commerce1.0         2/11/2005 12:14:52     Robert Pearse
 |   $
 |   Revision 1.35.2.2  2004/11/12 20:27:35  lzhao
 |   @scr 7688: set gift card status to active when auth offline
 |
 |   Revision 1.35.2.1  2004/11/12 14:28:53  kll
 |   @scr 7337: JournalFactory extensibility initiative
 |
 |   Revision 1.35  2004/10/05 19:05:32  bwf
 |   @scr 7305 Add logging of authorization in order to get response time.
 |
 |   Revision 1.34  2004/09/28 22:55:53  blj
 |   @scr 6650 - removed change from original 6650 scr
 |
 |   Revision 1.33  2004/09/17 22:09:45  blj
 |   @scr 5867 resolution for requirements change
 |
 |   Revision 1.32  2004/08/31 19:12:35  blj
 |   @scr 6855 - cleanup gift card credit code and fix defects found by PBY
 |
 |   Revision 1.31  2004/08/23 16:15:59  cdb
 |   @scr 4204 Removed tab characters
 |
 |   Revision 1.30  2004/08/20 21:37:02  bwf
 |   @scr 6785 Catch auth exception during reversal and voids and log it.
 |
 |   Revision 1.29  2004/08/05 21:12:50  blj
 |   @scr 6195 - corrected a problem with MSRModel data being lost for postvoids.
 |
 |   Revision 1.28  2004/08/02 13:05:31  crain
 |   @scr 6650 App Attempts to Activate any Gift Card used for Tendering
 |
 |   Revision 1.27  2004/08/02 04:40:50  crain
 |   @scr 6650 App Attempts to Activate any Gift Card used for Tendering
 |
 |   Revision 1.26  2004/07/30 20:10:45  lzhao
 |   @scr 6629: add initial/current balance and entry type in journal.
 |
 |   Revision 1.25  2004/07/27 22:06:39  kmcbride
 |   @scr 6354: Adding auto reversal when an authorization times out for gift card.
 |
 |   Revision 1.24  2004/07/26 22:45:48  blj
 |   @scr 5831 - added a check for training mode prior to validation.
 |
 |   Revision 1.23  2004/07/26 14:41:50  lzhao
 |   @scr 2681: change auth method to auto.
 |
 |   Revision 1.22  2004/07/23 22:17:25  epd
 |   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 |
 |   Revision 1.21  2004/07/20 16:29:00  blj
 |   @scr 5800 - updated to reflect expended status being removed.
 |
 |   Revision 1.20  2004/07/20 14:05:08  epd
 |   @scr 4268 Some minor refactoring and overhaul of gift card functionality in auth simulator
 |
 |   Revision 1.19  2004/07/16 22:15:14  epd
 |   @scr 4268 Updated logic to correctly set tender attributes
 |
 |   Revision 1.18  2004/07/15 22:14:39  bwf
 |   @scr 6272 Display Correct message for declines.
 |
 |   Revision 1.17  2004/07/14 22:59:18  lzhao
 |   @scr 6194: remove set balance, not necessary.
 |
 |   Revision 1.16  2004/07/14 22:11:17  kmcbride
 |   @scr 5954 (Services Impact): Adding log4j loggers to these classes to make them easier to debug, also fixed some catch statements that were not logging or re-throwing a new exception w/o nesting the original.
 |
 |   Revision 1.15  2004/07/13 20:13:33  lzhao
 |   @scr 6194: set gift card balance zero.
 |
 |   Revision 1.14  2004/06/25 15:05:03  kmcbride
 |   @scr 5797 and 5806, unit tests are failing
 |
 |   Revision 1.13  2004/06/25 06:28:15  kmcbride
 |   @scr 5797 and 5806: For whatever reason, the fact that the auth simulator uses the service activation and auth code did not register with me.  This should fix the problems with simulation misbehaving and properly integrate the ISD work.
 |
 |   Revision 1.9  2004/05/19 21:20:43  aschenk
 |   @scr 4521 - The remaining balance of a gift card was not correct on the receipt if the total was more than the card balance.
 |
 |   Revision 1.8  2004/04/28 15:46:37  blj
 |   @scr 4603 - Fix gift card change due defects.
 |
 |   Revision 1.7  2004/04/26 21:34:03  epd
 |   @scr 4513 updated so that user will not be prompted to deplete remaining balance if balance is zero
 |
 |   Revision 1.6  2004/04/22 21:03:53  epd
 |   @scr 4513 Changed all toFormattedString() calls to getStringValue() calls
 |
 |   Revision 1.5  2004/04/14 22:37:53  epd
 |   @scr 4322 Tender Invariant work.  Specifically for change invariant
 |
 |   Revision 1.4  2004/04/08 21:12:41  tfritz
 |   @scr 3884 - Do not do check digit validation when in training mode.
 |
 |   Revision 1.3  2004/02/17 18:36:03  epd
 |   @scr 0
 |   Code cleanup. Returned unused local variables.
 |
 |   Revision 1.2  2004/02/12 16:47:55  mcs
 |   Forcing head revision
 |
 |   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 |   updating to pvcs 360store-current
 |
 |    Rev 1.17   Feb 10 2004 15:16:10   blj
 | gift card refund
 |
 |    Rev 1.16   Feb 05 2004 13:46:34   rhafernik
 | log4j changes
 |
 |    Rev 1.15   Feb 04 2004 15:25:48   blj
 | more gift card refund work.
 |
 |    Rev 1.14   Feb 02 2004 09:17:14   blj
 | fixed a problem with gift card reversals
 |
 |    Rev 1.12   Jan 22 2004 15:27:16   blj
 | fixed a problem with voidAuth and reverse methods
 |
 |    Rev 1.11   Jan 19 2004 17:22:50   epd
 | Put back calls to DomainGateway
 |
 |    Rev 1.10   Jan 09 2004 13:50:44   blj
 | defect fixes
 | Resolution for 3592: Store Server offline During a GC Tender Transaction Hangs App.
 | Resolution for 3606: Gift Card Tender Crashes when swiping a Credit Card or any other type card.
 |
 |    Rev 1.9   Jan 06 2004 11:23:02   epd
 | refactorings to remove unfriendly references to TenderHelper and DomainGateway
 |
 |    Rev 1.8   Dec 12 2003 07:41:54   blj
 | Code Review changes
 |
 |    Rev 1.7   Dec 01 2003 15:12:08   blj
 | cleaned up code for code review.
 |
 |    Rev 1.6   Nov 25 2003 18:15:40   blj
 | giftcard functional testing resolutions
 |
 |    Rev 1.5   Nov 24 2003 16:27:42   blj
 | implemented recalculateTransactionTotal method, added reversal and voidauth capability to gift card tender.
 |
 |    Rev 1.4   Nov 19 2003 22:11:12   blj
 | added code for gift card tender using ado design
 |
 |    Rev 1.2   Oct 28 2003 14:16:32   epd
 | Added method to return whether tender is already authorized
 |
 |    Rev 1.1   Oct 21 2003 10:01:00   epd
 | Refactoring.  Moved RDO tender to abstract class
 |
 |    Rev 1.0   Oct 17 2003 12:33:48   epd
 | Initial revision.
 |
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModelIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.ado.context.ADOContextIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalConstants;
import oracle.retail.stores.pos.ado.journal.JournalData;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.security.OverridableIfc;
import oracle.retail.stores.pos.ado.utility.AuthorizationException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.pos.utility.CheckDigitUtility;

import org.apache.log4j.Logger;

/**
 * Business Logic for gift card tender.
 */
public class TenderGiftCardADO extends AbstractCardTender implements AuthorizableADOIfc, OverridableIfc, ReversibleTenderADOIfc
{
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -3594843213203564921L;

    /**
     * There is not place for this in the RDO tender
     */
    protected String authorizationResponseCode;

    /** Indicates whether this tender was reversed or not */
    protected boolean reversed = false;

    /** Indicates whether this tender was voided or not */
    protected boolean isVoided = false;

    /** Indicates that the gift card should be funded, or activated */
    protected int authorizationTransactionType = -1;

    /**
     *  our logger
     */
    protected transient Logger logger = Logger.getLogger(TenderGiftCardADO.class);

    /**
     * No-arg constructor
     * Note: constructor protected by design for use by tender factory
     */
    protected TenderGiftCardADO() {}

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.AbstractTenderADO#initializeTenderRDO()
     */
    protected void initializeTenderRDO()
    {
        tenderRDO = DomainGateway.getFactory().getTenderGiftCardInstance();
    }

    /**
     * This method resets the tender amount for partial authorization.
     *
     * @throws AuthorizationException
     */
    protected void resetAmountForAuthPartialDecline(String amt) throws AuthorizationException
    {
        try
        {
            getTenderAttributes().put(TenderConstants.AMOUNT, amt);
            setTenderAttributes(getTenderAttributes());
        }
        catch (TenderException te)
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("Caught TenderException while attempting to reset amount: " + te);
            }

            throw new AuthorizationException("Cannot reset amount for Partial Decline",
                                             AuthResponseCodeEnum.DECLINED,
                                             te.getMessage());
        }
    }

    /**
     * This method checks if the tender has been through the authorization
     * process. This doesnt imply that the authorization was approved because
     * the authorizationResponseCode could be a decline, referral, etc.
     *
     * @return boolean Check to see if the authorizationResponseCode is null.
     * @see oracle.retail.stores.ado.tender.AuthorizableADOIfc#isAuthorized()
     */
    public boolean isAuthorized()
    {
        // we should have at least an auth code or an auth response
        TenderGiftCardIfc tenderGiftCardRDO = (TenderGiftCardIfc)tenderRDO;

        boolean returnCode = false;

        if(!StringUtils.isBlank(tenderGiftCardRDO.getAuthorizationCode()) ||
                !StringUtils.isBlank(tenderGiftCardRDO.getAuthorizationResponse()))
        {
            returnCode = true;
        }

        return returnCode || isVoided();
    }

    /**
     * This result of this method is used to make a flow decision. True is
     * returned if the current balance on the card is less than the max change
     * limit for this tender type. Returns false if the balance is zero.
     *
     * @return
     * @deprecated in 13.4; this feature is not support in APF
     */
    public boolean isCurrentBalanceLessThanMaxChangeLimit()
    {
        return false;
    }

    /**
     * @see oracle.retail.stores.ado.tender.TenderADOIfc#getTenderType()
     */
    public TenderTypeEnum getTenderType()
    {
        return TenderTypeEnum.GIFT_CARD;
    }

    /**
     * This method will validate that the cardnumber passes bin range and
     * checkdigit validation.
     *
     * @throws TenderException
     * @see oracle.retail.stores.ado.tender.TenderADOIfc#validate()
     */
    public void validate() throws TenderException
    {
        // empty implementation.  As of APF, thirdparty payment system
        // should validate gift cards bin ranges and check digits.
    }

    /**
     * Get tender attributes
     *
     * @see oracle.retail.stores.ado.tender.TenderADOIfc#getTenderAttributes()
     * @return
     */
    public HashMap<String,Object> getTenderAttributes()
    {
        HashMap<String,Object> map = new HashMap<String,Object>(30);
        map.put(TenderConstants.TENDER_TYPE, getTenderType());
        map.put(TenderConstants.AMOUNT,
                getAmount().getStringValue());
        map.put(TenderConstants.NUMBER,((TenderGiftCardIfc)tenderRDO).getEncipheredCardData().getTruncatedAcctNumber());
        map.put(TenderConstants.ENCIPHERED_CARD_DATA, ((TenderGiftCardIfc)tenderRDO).getEncipheredCardData());
        map.put(TenderConstants.TRACE_NUMBER, ((TenderGiftCardIfc)tenderRDO).getGiftCard().getTraceNumber());
        if (((TenderGiftCardIfc)tenderRDO).isGiftCardCredit())
        {
            map.put(TenderConstants.GIFT_CARD_CREDIT_FLAG, TenderConstants.TRUE);
        }
        else
        {
            map.put(TenderConstants.GIFT_CARD_CREDIT_FLAG, TenderConstants.FALSE);
        }

        map.put(TenderConstants.REQUEST_CODE, ((TenderGiftCardIfc)tenderRDO).getRequestCode());

        // authorization info
        map.put(TenderConstants.AUTH_STATUS, new Integer(((TenderGiftCardIfc)tenderRDO).getAuthorizationStatus()));
        // Only put in map if we have authorized (status should be non-zero)
        if (isAuthorized())
        {
            map.put(TenderConstants.AUTH_AMOUNT, ((TenderGiftCardIfc)tenderRDO).getAuthorizationAmount()
                        .getStringValue());
            map.put(TenderConstants.AUTH_CODE, ((TenderGiftCardIfc)tenderRDO).getAuthorizationCode());
            map.put(TenderConstants.AUTH_METHOD, ((TenderGiftCardIfc)tenderRDO).getAuthorizationMethod());
            map.put(TenderConstants.AUTH_RESPONSE, ((TenderGiftCardIfc)tenderRDO).getAuthorizationResponse());
            map.put(TenderConstants.FINANCIAL_NETWORK_STATUS, ((TenderGiftCardIfc)tenderRDO).getFinancialNetworkStatus());
            map.put(TenderConstants.SETTLEMENT_DATA, ((TenderGiftCardIfc)tenderRDO).getSettlementData());
            map.put(TenderConstants.AUTH_DATE_TIME, ((TenderGiftCardIfc)tenderRDO).getAuthorizedDateTime());
            map.put(TenderConstants.ACCOUNT_NUMBER_TOKEN, ((TenderGiftCardIfc)tenderRDO).getAccountNumberToken());
            map.put(TenderConstants.JOURNAL_KEY, ((TenderGiftCardIfc)tenderRDO).getJournalKey());
            map.put(TenderConstants.LOCAL_TIME, ((TenderGiftCardIfc)tenderRDO).getAuthorizationTime());
            map.put(TenderConstants.LOCAL_DATE, ((TenderGiftCardIfc)tenderRDO).getAuthorizationDate());
            map.put(TenderConstants.REFERENCE_CODE, ((TenderGiftCardIfc)tenderRDO).getReferenceCode());
            map.put(TenderConstants.GIFT_CARD_ACCOUNT_TYPE, ((TenderGiftCardIfc)tenderRDO).getAccountType());

            if ( ((TenderGiftCardIfc)tenderRDO).getGiftCard() != null )
            {
                map.put(TenderConstants.ENTRY_METHOD, ((TenderGiftCardIfc)tenderRDO).getGiftCard().getEntryMethod());
            }
        }
        if (((TenderGiftCardIfc)tenderRDO).getGiftCard() != null && ((TenderGiftCardIfc)tenderRDO).getGiftCard().getCurrentBalance() != null)
        {
            map.put(TenderConstants.REMAINING_BALANCE, ((TenderGiftCardIfc)tenderRDO).getGiftCard()
                    .getCurrentBalance().getStringValue());
        }

        if (isCardSwiped())
        {
            map.put(TenderConstants.MSR_MODEL, this.msrModel);
            map.put(TenderConstants.ENTRY_METHOD, EntryMethod.Swipe);
        }
        else if(((TenderGiftCardIfc)tenderRDO).getEntryMethod() != null)
        {
            map.put(TenderConstants.ENTRY_METHOD, ((TenderGiftCardIfc)tenderRDO).getEntryMethod());
        }
        else if (EntryMethod.Automatic.equals(((TenderGiftCardIfc)tenderRDO).getEntryMethod()))
        {
            map.put(TenderConstants.ENTRY_METHOD, EntryMethod.Automatic);
        }
        else
        {
            map.put(TenderConstants.ENTRY_METHOD, EntryMethod.Manual);
        }

        map.put(TenderConstants.GIFT_CARD_EXISTING, ((TenderGiftCardIfc)tenderRDO).getGiftCard().isExisting());

        return map;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#setTenderAttributes(java.util.HashMap)
     */
    public void setTenderAttributes(HashMap<String,Object> tenderAttributes) throws TenderException
    {
        CurrencyIfc amount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
        tenderRDO.setAmountTender(amount);

        boolean giftCardCreditFlag = false;
        if (tenderAttributes.get(TenderConstants.GIFT_CARD_CREDIT_FLAG) != null)
        {
            String giftCardCreditString = (String)tenderAttributes.get(TenderConstants.GIFT_CARD_CREDIT_FLAG);
            if (giftCardCreditString.equals(TenderConstants.TRUE))
            {
                giftCardCreditFlag = true;
            }
        }

        if (tenderAttributes.get(TenderConstants.REQUEST_CODE) != null)
        {
            String code = (String)tenderAttributes.get(TenderConstants.REQUEST_CODE);
            ((TenderGiftCardIfc)tenderRDO).setRequestCode(code);
        }

        ((TenderGiftCardIfc)tenderRDO).setGiftCardCredit(giftCardCreditFlag);

        GiftCardIfc giftCard = ((TenderGiftCardIfc)tenderRDO).getGiftCard();
        if (giftCard == null)
        {
            giftCard = DomainGateway.getFactory().getGiftCardInstance();
            ((TenderGiftCardIfc)tenderRDO).setGiftCard(giftCard);
        }
        Integer statusCode = (Integer)tenderAttributes.get(TenderConstants.AUTH_STATUS);
        if (statusCode != null)
        {
            switch (statusCode)
            {
                case AuthorizableTenderIfc.AUTHORIZATION_STATUS_PENDING:
                    giftCard.setStatus(StatusCode.Unknown);
                    break;
                case AuthorizableTenderIfc.AUTHORIZATION_STATUS_APPROVED:
                    if(!AuthorizableTenderIfc.AUTHORIZATION_METHOD_MANUAL.equals(tenderAttributes.get(TenderConstants.AUTH_METHOD)))
                    {
                        giftCard.setStatus(StatusCode.Active);
                    }
                    break;
                case AuthorizableTenderIfc.AUTHORIZATION_STATUS_DECLINED:
                    giftCard.setStatus(StatusCode.Inactive);
            }
        }
        
        Object approvalCode = tenderAttributes.get(TenderConstants.AUTH_CODE);
        if (approvalCode != null)
        {
            giftCard.setApprovalCode(approvalCode.toString());
            ((TenderGiftCardIfc)tenderRDO).setAuthorizationCode(approvalCode.toString());
        }
        giftCard.setTraceNumber((String) tenderAttributes.get(TenderConstants.TRACE_NUMBER));
        ((TenderGiftCardIfc)tenderRDO).setTraceNumber((String) tenderAttributes.get(TenderConstants.TRACE_NUMBER));

        // we don't really know the status in re-entry mode. This prevents
        // balance available from printing on the receipt.
        if (isTransactionReentryMode())
        {
            giftCard.setStatus(StatusCode.Unknown);
        }

        giftCard.setEncipheredCardData((EncipheredCardDataIfc)tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA));
        ((TenderGiftCardIfc)tenderRDO).setEncipheredCardData((EncipheredCardDataIfc)tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA));

        giftCard.setCurrentBalance(parseAmount((String)tenderAttributes.get(TenderConstants.REMAINING_BALANCE),LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));

        if (tenderAttributes.get(TenderConstants.GIFT_CARD_EXISTING) != null)
        {
            Boolean boolObj = (Boolean) tenderAttributes.get(TenderConstants.GIFT_CARD_EXISTING);
            giftCard.setExisting(boolObj.booleanValue());
        }

        if (tenderAttributes.get(TenderConstants.MSR_MODEL) != null)
        {
            msrModel = (MSRModelIfc)tenderAttributes.get(TenderConstants.MSR_MODEL);
            ((TenderGiftCardIfc)tenderRDO).setBearerName(getBearerName(msrModel));
            ((TenderGiftCardIfc)tenderRDO).setEncipheredCardData(msrModel.getEncipheredCardData());
            ((TenderGiftCardIfc)tenderRDO).setExpirationDateString(msrModel.getExpirationDate());
            ((TenderGiftCardIfc)tenderRDO).setTrack2Data(msrModel.getTrack2Data());
        }
        // Set entry method
        EntryMethod entryMethod = (EntryMethod)tenderAttributes.get(TenderConstants.ENTRY_METHOD);
        ((TenderGiftCardIfc)tenderRDO).setEntryMethod(entryMethod);
        giftCard.setEntryMethod(entryMethod);

        // Authorization info
        if (statusCode != null)
        {
            ((TenderGiftCardIfc)tenderRDO).setAuthorizationStatus(statusCode.intValue());
        }
        // Only get from map if authorization occurred
        if (tenderAttributes.get(TenderConstants.AUTH_RESPONSE) != null)
        {
            ((TenderGiftCardIfc)tenderRDO).setAuthorizationAmount(
                    parseAmount((String)tenderAttributes.get(TenderConstants.AUTH_AMOUNT)));
            ((TenderGiftCardIfc)tenderRDO).setAuthorizationCode((String)tenderAttributes.get(TenderConstants.AUTH_CODE));
            ((TenderGiftCardIfc)tenderRDO).setAuthorizationMethod((String)tenderAttributes.get(TenderConstants.AUTH_METHOD));
            ((TenderGiftCardIfc)tenderRDO).setAuthorizationResponse((String)tenderAttributes.get(TenderConstants.AUTH_RESPONSE));
            ((TenderGiftCardIfc)tenderRDO).setFinancialNetworkStatus((String)tenderAttributes.get(TenderConstants.FINANCIAL_NETWORK_STATUS));
            CurrencyIfc cashChangeAmount = DomainGateway.getBaseCurrencyInstance((String)tenderAttributes.get(TenderConstants.CASH_CHANGE_AMOUNT));
            CurrencyIfc remainingBalance = DomainGateway.getBaseCurrencyInstance((String)tenderAttributes.get(TenderConstants.REMAINING_BALANCE));
            giftCard.setCurrentBalance(remainingBalance);
            ((TenderGiftCardIfc)tenderRDO).setSettlementData((String)tenderAttributes.get(TenderConstants.SETTLEMENT_DATA));
            ((TenderGiftCardIfc)tenderRDO).setAuthorizedDateTime((EYSDate)tenderAttributes.get(TenderConstants.AUTH_DATE_TIME));
            ((TenderGiftCardIfc)tenderRDO).setAccountNumberToken((String)tenderAttributes.get(TenderConstants.ACCOUNT_NUMBER_TOKEN));
            ((TenderGiftCardIfc)tenderRDO).setJournalKey((String)tenderAttributes.get(TenderConstants.JOURNAL_KEY));
            ((TenderGiftCardIfc)tenderRDO).setAuthorizationTime((String)tenderAttributes.get(TenderConstants.LOCAL_TIME));
            ((TenderGiftCardIfc)tenderRDO).setAuthorizationDate((String)tenderAttributes.get(TenderConstants.LOCAL_DATE));
            ((TenderGiftCardIfc)tenderRDO).setReferenceCode((String)tenderAttributes.get(TenderConstants.REFERENCE_CODE));
            ((TenderGiftCardIfc)tenderRDO).setAccountType((String)tenderAttributes.get(TenderConstants.GIFT_CARD_ACCOUNT_TYPE));
        }

        if (tenderAttributes.get(TenderConstants.SIGNATURE_REQUIRED) != null)
        {
            ((TenderGiftCardIfc)tenderRDO).setSignatureRequired((Boolean)tenderAttributes.get(TenderConstants.SIGNATURE_REQUIRED));
        }
    }

    /**
     * Indicates Gift Card is a NOT type of PAT Cash
     * @return false
     */
    public boolean isPATCash()
    {
        return false;
    }

    /**
     * Get card number from RDO
     * @return
     */
    protected GiftCardIfc createGiftCardItem()
    {
        GiftCardIfc giftCard = DomainGateway.getFactory().getGiftCardInstance();

        if (giftCard != null && ((TenderGiftCardIfc)tenderRDO).getCardNumber() != null)
        {
            try
            {
                EncipheredCardDataIfc cardData = FoundationObjectFactory.getFactory().createEncipheredCardDataInstance(((TenderGiftCardIfc)tenderRDO).getCardNumber().getBytes());
                giftCard.setEncipheredCardData(cardData);
            }
            catch(EncryptionServiceException ese)
            {
                logger.error("Could not decrypt gift card number", ese);
            }
        }
        return giftCard;
    }

    /**
     * Create map of journal attributes.
     *
     * @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    public Map<String,Object> getJournalMemento()
    {
        // We can reuse the tender attributes for journalling purposes
        Map<String,Object> memento = getTenderAttributes();
        // add tender descriptor
        memento.put(JournalConstants.DESCRIPTOR, getTenderType().toString());
        return memento;
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     * @param rdo
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        assert(rdo instanceof TenderGiftCardIfc);
        super.fromLegacy(rdo);
        tenderRDO = (TenderGiftCardIfc)rdo;
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
    public EYSDomainIfc toLegacy(Class type)
    {
        return null;
    }
    /**
     * Validate the card number using MOD10 algorithm and bin range.
     * @throws TenderException Thrown when card fails MOD10 check or bin range.
     */
    protected void validateCardNumber() throws TenderException
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)getContext().getManager(UtilityManagerIfc.TYPE);
        boolean checkDigitValid = true;
        EncipheredCardDataIfc cardData = ((TenderGiftCardIfc)tenderRDO).getEncipheredCardData();

        checkDigitValid = utility.validateCheckDigit(CheckDigitUtility.CHECK_DIGIT_FUNCTION_GIFTCARD, cardData);

        if (!checkDigitValid)
        {
            throw new TenderException("Invalid Card Number", TenderErrorCodeEnum.INVALID_CARD_NUMBER);
        }
    }

    protected void validateBinRange() throws TenderException
    {
        boolean binRangeValid = false;
        binRangeValid = isValidBinRange(((TenderGiftCardIfc)tenderRDO).getEncipheredCardData(), TenderTypeEnum.GIFT_CARD);
        if (!binRangeValid)
        {
            throw new TenderException("Invalid Bin Range", TenderErrorCodeEnum.INVALID_CARD_NUMBER);
        }
    }

    /**
     * Return reversed flag.
     * @return
     */
    public boolean isReversed()
    {
        return reversed;
    }

    /**
     * Return whether or not tender is a gift card credit.
     *
     * @param value
     */
    public void setGiftCardCredit(boolean value)
    {
        // update RDO
        ((TenderGiftCardIfc) tenderRDO).setGiftCardCredit(value);
    }

    /**
     * Set gift card status.
     *
     * @param status
     */
    public void setGiftCardStatus(StatusCode status)
    {
        // update RDO
        ((TenderGiftCardIfc) tenderRDO).setStatus(status);
    }

    /**
     * Return whether or not tender is a gift card credit.
     *
     * @return
     */
    public boolean isGiftCardCredit()
    {
        return ((TenderGiftCardIfc) tenderRDO).isGiftCardCredit();
    }

    /**
     * Return whether or not tender has been voided.
     *
     * @return
     * @see oracle.retail.stores.pos.ado.tender.ReversibleTenderADOIfc#isReversed()
     */
    public boolean isVoided()
    {
        return isVoided;
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
        if (logger.isInfoEnabled())
        {
            logger.info("Overriding gift card auth...");
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
            data.putJournalData(JournalConstants.BOOLEAN, new Boolean(result).toString());
            RegisterJournalIfc regJournal = getRegisterJournal();
            regJournal.journal(data, JournalFamilyEnum.TENDER, JournalActionEnum.OVERRIDE);
        }
        catch (LoginException e)
        {
            // do nothing. Obviously the override didn't work

            // KLM: This actually handled below, but let's at least log it
            logger.warn("Caught exception when trying to override tender limit: " + e);
        }

        // If the override succeeded, alter the internal state of this tender
        if (result)
        {

            ((TenderGiftCardIfc) tenderRDO).setAuthorizationMethod(AuthorizableTenderIfc.AUTHORIZATION_METHOD_MANUAL);
            ((TenderGiftCardIfc) tenderRDO).setAuthorizationStatus(AuthorizableTenderIfc.AUTHORIZATION_STATUS_APPROVED);
            // only reset auth code if not null (may have been entered manually
            // on call referral screen).
            if (((TenderGiftCardIfc) tenderRDO).getAuthorizationCode() == null)
            {
                ((TenderGiftCardIfc) tenderRDO).setAuthorizationCode(((TenderGiftCardIfc) tenderRDO)
                        .getAuthorizationResponse());
            }

            // journal the result using utility method in cargo
            // only journal for successful override. We could have multiple
            // unsuccessful overrides and only want to journal when we have a
            // resolution
            RegisterJournalIfc journal = getRegisterJournal();
            journal.journal(this, JournalFamilyEnum.TENDER, JournalActionEnum.AUTHORIZATION);
        }
        else
        {
            ((TenderGiftCardIfc) tenderRDO).setAuthorizationMethod(AuthorizableTenderIfc.AUTHORIZATION_METHOD_MANUAL);
            ((TenderGiftCardIfc) tenderRDO).setAuthorizationStatus(AuthorizableTenderIfc.AUTHORIZATION_STATUS_DECLINED);
        }

        return result;
    }

    /**
     * Convenience method to get current operator's Employee ID Method added to
     * facilitate Unit testing.
     *
     * @return
     */
    protected String getOperatorEmployeeID()
    {
        return ((UserAccessCargo) getContext().getBus().getCargo()).getOperator().getEmployeeID();
    }

    /**
     * Convenience method to get Register Journal Method added to facilitate
     * Unit testing.
     *
     * @return
     */
    protected RegisterJournalIfc getRegisterJournal()
    {
        return getJournalFactory().getRegisterJournal();
    }

    /**
     * Returns the authorizationTransactionType.
     *
     * @return the authorizationTransactionType
     */
    public int getAuthorizationTransactionType()
    {
        return authorizationTransactionType;
    }

    /**
     * Sets the authorizationTransactionType.
     *
     * @param authorizationTransactionType the authorizationTransactionType to
     *            set
     */
    public void setAuthorizationTransactionType(int authorizationTransactionType)
    {
        this.authorizationTransactionType = authorizationTransactionType;
    }

    /**
     * Convenience method to retrieve Register ID This method was created to
     * facilitate Unit Testing
     *
     * @return
     */
    protected String getRegisterID()
    {
        AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc)TourContext.getInstance().getTourBus().getCargo();
        return cargo.getRegister().getWorkstation().getWorkstationID();
    }

    /**
     * Convenience method to retrieve Store ID
     *
     * @return
     */
    protected String getStoreID()
    {
        AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc)TourContext.getInstance().getTourBus().getCargo();
        return cargo.getRegister().getWorkstation().getStoreID();
    }
}
