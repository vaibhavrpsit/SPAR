/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderDebitADO.java /main/27 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abonda 09/04/13 - initialize collections
 *    icole  03/06/13 - Print Trace Number on Debit receipt if exists, else print
 *                      System Audit Trace Number if exists per ACI's
 *                      requirements.
 *    cgreen 10/21/11 - added missing debit type and auth amount to journal
 *    cgreen 10/20/11 - correct setting of EntryMethod when getting the tender
 *                      atttributes
 *    jswan  10/17/11 - Fixed issue with display of card type on the receipt.
 *    cgreen 09/22/11 - ensure enciphered card data is set so that it can be
 *                      printed on receipt
 *    sgu    09/16/11 - remove unused expiration date
 *    cgreen 09/12/11 - added support for setting balance left on prepaid card
 *    cgreen 09/09/11 - made more sense out of setting entry method and also
 *                      ensured iccdetails is set.
 *    blarse 08/02/11 - Renamed token to accountNumberToken to be consistent.
 *    jswan  07/26/11 - Modified to support refunding return amount to original
 *                      credit/debit card.
 *    blarse 07/22/11 - Changed isAuthorized() to use AuthCode since this
 *                      column is more likely to be filled after auth.
 *    blarse 07/15/11 - Fix misspelled word: retrival
 *    blarse 07/14/11 - Added get/sets for reversal-related tender attribs.
 *    blarse 07/12/11 - Added support for account number token and Retrieval
 *                      Reference Number.
 *    asinto 07/12/11 - fixed some entry method coding
 *    cgreen 07/12/11 - update generics
 *    cgreen 07/07/11 - convert entryMethod to an enum
 *    cgreen 06/29/11 - add token column and remove encrypted/hashed account
 *                      number column in credit-debit tender table.
 *    blarse 06/16/11 - Saving the payment service token onto the tenderRDO so
 *                      it can be persisted.
 *    cgreen 05/27/11 - move auth response objects into domain
 *    asinto 07/27/10 - Add track 2 data to the request.
 *    asinto 05/28/10 - KSN bytes need to be captured from CPOI device and
 *                      formatted in the ISD request message for debit
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    asinton   05/06/10 - Added Prepaid Remaining Balance to receipt and
 *                         ejournal
 *    sgu       03/24/10 - add comment
 *    sgu       03/24/10 - null pointer check for reversal response
 *    asinton   03/19/10 - set reversal to true in the request when voiding
 *                         tender
 *    dwfung    03/10/10 - Handling Training Mode Requests
 *    abondala  01/03/10 - update header date
 *    asinton   12/10/09 - Changes per review comments
 *    asinton   12/07/09 - Storing SettlementData in the tenderRDO to the
 *                         decodeAuthResponse method.
 *    asinton   12/03/09 - Changes to support credit card authorizations on
 *                         returns and voids.
 *    asinton   06/17/09 - Removing parameter MerchantNumber.
 *    cgreene   04/02/09 - prevent NPE in tenderDebitADO by ensuring ones digit
 *                         is positive in SimAuthTechnician and that if the
 *                         decodeAuthResponse() method gets a null response
 *                         from the tech that a proper exception is thrown.
 *    mweis     03/31/09 - add audit trace number for debit receipts
 *    vchengeg  11/07/08 - To fix BAT test failure
 *
 * ===========================================================================
 * $Log:
 *    13   360Commerce 1.12        1/17/2008 5:24:06 PM   Alan N. Sinton  CR
 *         29954: Refactor of EncipheredCardData to implement interface and be
 *          instantiated using a factory.
 *    12   360Commerce 1.11        12/19/2007 6:01:59 PM  Alan N. Sinton  CR
 *         29598: Fixed some broken unittests.
 *    11   360Commerce 1.10        12/12/2007 6:47:38 PM  Alan N. Sinton  CR
 *         29761: FR 8: Prevent repeated decryption of PAN data.
 *    10   360Commerce 1.9         12/4/2007 8:18:27 PM   Alan N. Sinton  CR
 *         29598: code changes per code review.
 *    9    360Commerce 1.8         11/21/2007 1:59:17 AM  Deepti Sharma   CR
 *         29598: changes for credit/debit PAPB
 *    8    360Commerce 1.7         11/15/2007 10:09:55 AM Christian Greene
 *         Belize merge - Changed comparision of MinimumAuthAmount to
 *         greater-than-equal-to so minimum amount is inclusive of amount
 *         required to authorize.
 *    7    360Commerce 1.6         8/22/2007 6:41:30 PM   Michael Boyd    Set
 *         the trans. ID on the request in the misc variable.  To be used in
 *         the journal key.
 *    6    360Commerce 1.5         5/30/2007 9:01:56 AM   Anda D. Cadar   code
 *         cleanup
 *    5    360Commerce 1.4         5/18/2007 9:19:17 AM   Anda D. Cadar
 *         always use decimalValue toString
 *    4    360Commerce 1.3         12/13/2005 4:42:32 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:23 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:56 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:51 PM  Robert Pearse
 *
 *   Revision 1.13.2.1  2004/11/09 20:59:06  bwf
 *   @scr 7650 Allow error retry for debit.
 *
 *   Revision 1.13  2004/10/05 19:05:32  bwf
 *   @scr 7305 Add logging of authorization in order to get response time.
 *
 *   Revision 1.12  2004/08/20 21:37:02  bwf
 *   @scr 6785 Catch auth exception during reversal and voids and log it.
 *
 *   Revision 1.11  2004/08/05 21:12:50  blj
 *   @scr 6195 - corrected a problem with MSRModel data being lost for postvoids.
 *
 *   Revision 1.10  2004/07/23 22:17:25  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.9  2004/07/14 22:11:17  kmcbride
 *   @scr 5954 (Services Impact): Adding log4j loggers to these classes to make them easier to debug, also fixed some catch statements that were not logging or re-throwing a new exception w/o nesting the original.
 *
 *   Revision 1.8  2004/07/12 21:42:19  bwf
 *   @scr 6125 Made available expiration validation of debit before pin.
 *
 *   Revision 1.7  2004/05/06 21:12:54  bwf
 *   @scr 3377 Debit Reversal Work
 *
 *   Revision 1.6  2004/05/04 20:05:15  bwf
 *   @scr 3377 Debit Reversal Work
 *
 *   Revision 1.5  2004/04/29 17:30:00  bwf
 *   @scr 3377 Debit Reversal Work
 *
 *   Revision 1.4  2004/04/22 21:03:53  epd
 *   @scr 4513 Changed all toFormattedString() calls to getStringValue() calls
 *
 *   Revision 1.3  2004/04/08 21:12:41  tfritz
 *   @scr 3884 - Do not do check digit validation when in training mode.
 *
 *   Revision 1.2  2004/02/12 16:47:55  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.17   Feb 05 2004 13:46:30   rhafernik
 * log4j changes
 *
 *    Rev 1.16   Jan 30 2004 12:49:56   bwf
 * Updated isAuthorized to fix problems when gift card reload fails and tender must be reversed.
 *
 *    Rev 1.15   Jan 07 2004 15:25:54   epd
 * Refactoring to add MOD10 to debit
 *
 *    Rev 1.14   Jan 07 2004 13:34:10   epd
 * fixed SCR 3674 - Debit now registers bad mag stripe
 *
 *    Rev 1.13   Jan 06 2004 11:23:00   epd
 * refactorings to remove unfriendly references to TenderHelper and DomainGateway
 *
 *    Rev 1.12   Dec 18 2003 09:47:28   epd
 * removed unused imports
 *
 *    Rev 1.11   Dec 17 2003 14:48:44   epd
 * Refactorings to accommodate Unit testing
 *
 *    Rev 1.10   Dec 10 2003 10:16:36   epd
 * Makes use of new MSRModel interface
 *
 *    Rev 1.9   Dec 08 2003 11:06:56   epd
 * checked in some Reentry mode logic
 *
 *    Rev 1.8   Dec 04 2003 19:23:48   epd
 * Updates for debit auth
 *
 *    Rev 1.7   Dec 04 2003 18:55:26   epd
 * updates for debit decline
 *
 *    Rev 1.6   Dec 04 2003 17:46:32   epd
 * Updates for debit auth
 *
 *    Rev 1.5   Dec 04 2003 16:52:32   epd
 * Updates for debit auth
 *
 *    Rev 1.4   Dec 04 2003 10:13:50   epd
 * updates for PIN
 *
 *    Rev 1.3   Dec 02 2003 16:19:54   epd
 * Updates
 *
 *    Rev 1.2   Dec 01 2003 19:06:26   epd
 * Updates for Credit/Debit
 *
 *    Rev 1.1   Nov 13 2003 08:59:18   bwf
 * Updated for new authorize.
 *
 *    Rev 1.0   Nov 04 2003 11:13:14   epd
 * Initial revision.
 *
 *    Rev 1.2   Oct 28 2003 14:16:32   epd
 * Added method to return whether tender is already authorized
 *
 *    Rev 1.1   Oct 21 2003 10:01:00   epd
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

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.IntegratedChipCardDetailsIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderDebitIfc;
import oracle.retail.stores.domain.utility.CardTypeCodesIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModelIfc;
import oracle.retail.stores.pos.ado.journal.JournalConstants;

import org.apache.log4j.Logger;

/**
 * A Debit tender object
 */
public class TenderDebitADO extends AbstractCardTender
    implements AuthorizableADOIfc, ReversibleTenderADOIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6296179321286566186L;

    /** The format of expiration dates as read from UI */
    public static final String CARD_DATE_FORMAT = "MM/yyyy";

    /**
     * There is not place for this in the RDO tender
     */
    protected String authorizationResponseCode;

    /** Indicates whether this tender was reversed or not */
    protected boolean reversed = false;

    /** Indicates whether this tender was voided or not */
    protected boolean voided = false;

    /** Indicating that this tender is in the process of voiding itself **/
    protected boolean voiding = false;

    /** debug logger */
    protected static final Logger logger = Logger.getLogger(TenderDebitADO.class);

    /**
     * No-arg constructor
     * Note: constructor protected by design for use by tender factory
     */
    protected TenderDebitADO() {}

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.AbstractTenderADO#initializeTenderRDO()
     */
    protected void initializeTenderRDO()
    {
        tenderRDO = DomainGateway.getFactory().getTenderDebitInstance();
    }

    /**
     * @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#getTenderType()
     */
    public TenderTypeEnum getTenderType()
    {
        return TenderTypeEnum.DEBIT;
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
     * This method returns if the tender is authorized.
     *
     * @return boolean authorized
     * @see oracle.retail.stores.pos.ado.tender.AuthorizableADOIfc#isAuthorized()
     */
    public boolean isAuthorized()
    {
        TenderDebitIfc tenderDebitRDO = (TenderDebitIfc)tenderRDO;
        boolean returnCode = false;

        if(!StringUtils.isBlank(tenderDebitRDO.getAuthorizationCode()) ||
                !StringUtils.isBlank(tenderDebitRDO.getAuthorizationResponse()) ||
                !StringUtils.isBlank(authorizationResponseCode))
        {
            returnCode = true;
        }
        return returnCode || isVoided();
    }

    /**
     * @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#validate()
     */
    public void validate() throws TenderException
    {
        // does nothing
    }

    /**
     * Debit cards MUST be swiped.
     *
     * @throws TenderException Thrown when card is not swiped.
     */
    protected void validateCardSwiped() throws TenderException
    {
        if (!isCardSwiped())
        {
            throw new TenderException("Debit cards must be swiped", TenderErrorCodeEnum.DEBIT_NOT_SWIPED);
        }
    }

    /**
     * This method gets the tender attributes.
     *
     * @return
     * @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#getTenderAttributes()
     */
    public HashMap<String,Object> getTenderAttributes()
    {
        TenderDebitIfc tenderDebitRDO = (TenderDebitIfc)tenderRDO;
        HashMap<String,Object> map = new HashMap<String,Object>(35);
        map.put(TenderConstants.TENDER_TYPE, getTenderType());
        map.put(TenderConstants.AMOUNT, getAmount().getStringValue());
        map.put(TenderConstants.MSR_MODEL, this.msrModel);
        if (tenderDebitRDO.getEntryMethod() == null)
        {
            map.put(TenderConstants.ENTRY_METHOD, (msrModel != null)? EntryMethod.Swipe : EntryMethod.Manual);
        }
        else
        {
            map.put(TenderConstants.ENTRY_METHOD, tenderDebitRDO.getEntryMethod());
        }
        // always put these in regardless of entry method
        map.put(TenderConstants.NUMBER, tenderDebitRDO.getCardNumber());
        map.put(TenderConstants.EXPIRATION_DATE, tenderDebitRDO.getExpirationDateString());
        map.put(TenderConstants.ENCIPHERED_CARD_DATA, tenderDebitRDO.getEncipheredCardData());
        map.put(TenderConstants.SIGNATURE_REQUIRED, ((TenderChargeIfc)tenderRDO).isSignatureRequired());

        if (tenderDebitRDO.getAccountNumberToken() != null)
        {
            map.put(TenderConstants.ACCOUNT_NUMBER_TOKEN, tenderDebitRDO.getAccountNumberToken());
        }

        // NOTE: intentionally left off PIN number.  No reason to return that byte array here.

        // authorization info
        map.put(TenderConstants.AUTH_STATUS, new Integer(tenderDebitRDO.getAuthorizationStatus()));
        // Only put in map if we have authorized (status should be non-zero)
        if (isAuthorized())
        {
            map.put(TenderConstants.AUTH_AMOUNT, tenderDebitRDO.getAuthorizationAmount()
                    .getStringValue());
            map.put(TenderConstants.AUTH_CODE, tenderDebitRDO.getAuthorizationCode());
            map.put(TenderConstants.AUTH_METHOD, tenderDebitRDO.getAuthorizationMethod());
            map.put(TenderConstants.AUTH_RESPONSE, tenderDebitRDO.getAuthorizationResponse());
            map.put(TenderConstants.FINANCIAL_NETWORK_STATUS, tenderDebitRDO.getFinancialNetworkStatus());
            map.put(TenderConstants.AUTH_RESPONSE_CODE, authorizationResponseCode);
            map.put(TenderConstants.PREPAID_REMAINING_BALANCE, tenderDebitRDO.getPrepaidRemainingBalance());

            // for reversals
            map.put(TenderConstants.JOURNAL_KEY, tenderDebitRDO.getJournalKey());
            map.put(TenderConstants.AUTH_SEQUENCE_NUMBER, tenderDebitRDO.getRetrievalReferenceNumber());
            map.put(TenderConstants.LOCAL_TIME, tenderDebitRDO.getAuthorizationTime());
            map.put(TenderConstants.LOCAL_DATE, tenderDebitRDO.getAuthorizationDate());
            map.put(TenderConstants.ACCOUNT_DATA_SOURCE, tenderDebitRDO.getAccountDataSource());
            map.put(TenderConstants.PAYMENT_SERVICE_INDICATOR, tenderDebitRDO.getPaymentServiceIndicator());
            map.put(TenderConstants.TRANSACTION_ID, tenderDebitRDO.getTransactionIdentificationNumber());
            map.put(TenderConstants.AUTH_RESPONSE_CODE, tenderDebitRDO.getAuthResponseCode());
            map.put(TenderConstants.VALIDATION_CODE, tenderDebitRDO.getValidationCode());
            map.put(TenderConstants.AUTH_SOURCE, tenderDebitRDO.getAuthorizationSource());
            map.put(TenderConstants.HOST_REFERENCE, tenderDebitRDO.getHostReference());
            map.put(TenderConstants.TRACE_NUMBER, tenderDebitRDO.getTraceNumber());
            map.put(TenderConstants.SYSTEM_TRACE_AUDIT_NUMBER, tenderDebitRDO.getAuditTraceNumber());
        }

        return map;
    }

    /**
     * This method sets the tender attributes.
     *
     * @param tenderAttributes
     * @throws TenderException
     * @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#setTenderAttributes(java.util.HashMap)
     */
    public void setTenderAttributes(HashMap<String,Object> tenderAttributes) throws TenderException
    {
        TenderDebitIfc tenderDebitRDO = (TenderDebitIfc)tenderRDO;
        // get the amount
        tenderDebitRDO.setAmountTender(parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT)));
        // card info
        EntryMethod entryMethod = (EntryMethod)tenderAttributes.get(TenderConstants.ENTRY_METHOD);
        if (entryMethod == null)
        {
            boolean swiped = (tenderAttributes.get(TenderConstants.MSR_MODEL) != null) ? true : false;
            if (swiped)
            {
                msrModel = (MSRModelIfc)tenderAttributes.get(TenderConstants.MSR_MODEL);
                tenderDebitRDO.setBearerName(getBearerName(msrModel));
                tenderDebitRDO.setEncipheredCardData(msrModel.getEncipheredCardData());
                tenderDebitRDO.setEntryMethod(EntryMethod.Swipe);
                tenderDebitRDO.setTrack2Data(msrModel.getTrack2Data());
            }
            else
            {
                tenderDebitRDO.setEncipheredCardData((EncipheredCardDataIfc)tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA));
                tenderDebitRDO.setEntryMethod(EntryMethod.Manual);
                tenderDebitRDO.setExpirationDateString((String)tenderAttributes.get(TenderConstants.EXPIRATION_DATE));
            }
        }
        else
        {
            tenderDebitRDO.setEntryMethod(entryMethod);
            tenderDebitRDO.setEncipheredCardData((EncipheredCardDataIfc)tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA));
            tenderDebitRDO.setExpirationDateString((String)tenderAttributes.get(TenderConstants.EXPIRATION_DATE));
        }
        // set additional security info value
        tenderDebitRDO.setAdditionalSecurityInfo((String)tenderAttributes.get(TenderConstants.ADDITIONAL_SECURITY_INFO));
        // set encrypted PIN
        tenderDebitRDO.setEncryptedPIN((byte[])tenderAttributes.get(TenderConstants.PIN_NUMBER));
        if (Util.isEmpty(tenderDebitRDO.getCardType()))
        {
            tenderDebitRDO.setCardType(determineCreditType(tenderDebitRDO.getEncipheredCardData()).toString());
            if (tenderDebitRDO.getCardType().equals(CardTypeCodesIfc.UNKNOWN))
            {
                tenderDebitRDO.setCardType(tenderDebitRDO.getEncipheredCardData().getCardName());
            }
        }
        tenderDebitRDO.setAccountNumberToken((String)tenderAttributes.get(TenderConstants.ACCOUNT_NUMBER_TOKEN));

        ((TenderChargeIfc)tenderRDO).setICCDetails((IntegratedChipCardDetailsIfc)tenderAttributes.get(TenderConstants.ICC_DETAILS));
        if (tenderAttributes.get(TenderConstants.SIGNATURE_REQUIRED) != null)
        {
            ((TenderChargeIfc)tenderRDO).setSignatureRequired((Boolean)tenderAttributes.get(TenderConstants.SIGNATURE_REQUIRED));
        }

        // Authorization info
        tenderDebitRDO.setAuthorizationStatus(((Integer)tenderAttributes.get(TenderConstants.AUTH_STATUS)).intValue());
        // Only get from map if authorization occurred
        if (tenderAttributes.get(TenderConstants.AUTH_RESPONSE) != null)
        {
            tenderDebitRDO.setAuthorizationAmount(
                    parseAmount((String)tenderAttributes.get(TenderConstants.AUTH_AMOUNT)));
            tenderDebitRDO.setAuthorizationCode((String)tenderAttributes.get(TenderConstants.AUTH_CODE));
            tenderDebitRDO.setAuthorizationMethod((String)tenderAttributes.get(TenderConstants.AUTH_METHOD));
            tenderDebitRDO.setAuthorizationResponse((String)tenderAttributes.get(TenderConstants.AUTH_RESPONSE));
            tenderDebitRDO.setFinancialNetworkStatus((String)tenderAttributes.get(TenderConstants.FINANCIAL_NETWORK_STATUS));
            authorizationResponseCode = (String)tenderAttributes.get(TenderConstants.AUTH_RESPONSE_CODE);
            tenderDebitRDO.setPrepaidRemainingBalance((CurrencyIfc)tenderAttributes.get(TenderConstants.PREPAID_REMAINING_BALANCE));

            // for reversals
            tenderDebitRDO.setJournalKey((String)tenderAttributes.get(TenderConstants.JOURNAL_KEY));
            tenderDebitRDO.setRetrievalReferenceNumber((String)tenderAttributes.get(TenderConstants.AUTH_SEQUENCE_NUMBER));
            tenderDebitRDO.setAuthorizationTime((String)tenderAttributes.get(TenderConstants.LOCAL_TIME));
            tenderDebitRDO.setAuthorizationDate((String)tenderAttributes.get(TenderConstants.LOCAL_DATE));
            tenderDebitRDO.setAccountDataSource((String)tenderAttributes.get(TenderConstants.ACCOUNT_DATA_SOURCE));
            tenderDebitRDO.setPaymentServiceIndicator((String)tenderAttributes.get(TenderConstants.PAYMENT_SERVICE_INDICATOR));
            tenderDebitRDO.setTransactionIdentificationNumber((String)tenderAttributes.get(TenderConstants.TRANSACTION_ID));
            tenderDebitRDO.setAuthResponseCode((String)tenderAttributes.get(TenderConstants.AUTH_RESPONSE_CODE));
            tenderDebitRDO.setValidationCode((String)tenderAttributes.get(TenderConstants.VALIDATION_CODE));
            tenderDebitRDO.setAuthorizationSource((String)tenderAttributes.get(TenderConstants.AUTH_SOURCE));
            tenderDebitRDO.setHostReference((String)tenderAttributes.get(TenderConstants.HOST_REFERENCE));
            tenderDebitRDO.setTraceNumber((String)tenderAttributes.get(TenderConstants.TRACE_NUMBER));
            tenderDebitRDO.setAuditTraceNumber((String)tenderAttributes.get(TenderConstants.SYSTEM_TRACE_AUDIT_NUMBER));
        }
    }

    /**
     * Indicates Debit is a NOT type of PAT Cash
     * @return false
     */
    public boolean isPATCash()
    {
        return false;
    }

    /**
     * This method gets the journal memento.
     *
     * @return
     * @see oracle.retail.stores.pos.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    public Map<String,Object> getJournalMemento()
    {
        // reuse tender attributes for memento
        Map<String,Object> memento = getTenderAttributes();
        TenderDebitIfc tenderDebitRDO = (TenderDebitIfc)tenderRDO;
        memento.put(JournalConstants.DESCRIPTOR, getTenderType().toString());
        memento.put(JournalConstants.ENTRY_METHOD, tenderDebitRDO.getEntryMethod());
        memento.put(TenderConstants.REMAINING_BALANCE, tenderDebitRDO.getPrepaidRemainingBalance());
        memento.put(JournalConstants.CARD_TYPE, tenderDebitRDO.getCardType());

        return memento;
    }

    /**
     * This method determines if there has been a pin entered for this debit
     * card.
     *
     * @return hasPin boolean that determines if there is a pin
     */
    public boolean hasPIN()
    {
        boolean hasPin = false;
        if (((TenderDebitIfc)tenderRDO).getEncryptedPIN() != null)
        {
            hasPin = true;
        }
        return hasPin;
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
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        assert (rdo instanceof TenderDebitIfc);
        super.fromLegacy(rdo);
        tenderRDO = (TenderDebitIfc)rdo;
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
        return toLegacy();
    }


    /**
     * Returns the authorization response code
     * @return String
     */
    public String getAuthorizationResponseCode()
    {
        return authorizationResponseCode;
    }
}
