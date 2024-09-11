/* =============================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/reversal/ReversalCargo.java /main/14 2013/12/12 17:06:00 asinton Exp $
 * =============================================================================
 * NOTES
 *
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   12/12/13 - transfer gift card account number to the
 *                         ReversalRequest
 *    asinton   11/21/13 - guarantee that the setBaseAmount is a positive
 *                         value.
 *    asinton   11/14/13 - added code to carry the transaction archive name to
 *                         reversal and authorization services for transaction
 *                         archival to support potential reversal of pending
 *                         authorizations in the case of application crash
 *    icole     08/30/13 - Add support for check approval sequence number
 *    blarsen   08/08/13 - Disambiguate POS transactoinID from payment service
 *                         transactionID
 *    asinton   07/29/13 - changed the logic to prevent regular checks about
 *                         allow echecks and other authroized tender to be
 *                         reversed
 *    blarsen   07/26/13 - Fix java npe when not echeck.
 *    icole     07/25/13 - don't create an auth reversal request for a
 *                         deposited check, only for echeck.
 *    asinton   05/02/13 - set the ACTION_CODE in the tender attributes hashmap
 *                         before building request in order to invoke reversal
 *                         in the request formatter
 *    icole     02/28/13 - Forward Port Print trace number on receipt for gift
 *                         cards, required by ACI.
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    asinton   04/27/12 - added tour locking for voiding tender line items
 *    cgreene   03/27/12 - implement deleting and reversing tender for
 *                         mobilepos
 *    jswan     09/12/11 - Modifications for reversals of Gift Cards when
 *                         escaping from the Tender Tour.
 *    cgreene   09/13/11 - corrected comparing of enums
 *    cgreene   09/12/11 - revert aba number encryption, which is not sensitive
 *    blarsen   09/12/11 - Added error logging for should-never-happen cases.
 *    ohorne    08/18/11 - APF: check cleanup
 *    jswan     08/15/11 - Added original journal to reversal request for gift
 *                         cards.
 *    ohorne    08/09/11 - APF:foreign currency support
 *    blarsen   08/02/11 - Misc cleanup. Renamed token to accountNumberToken.
 *    blarsen   07/22/11 - Changed cargo to hold reversal request list. Added
 *                         build-request helper methods which are called from
 *                         shuttles.
 *    blarsen   07/19/11 - Initial version.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.reversal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc.RequestSubType;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceRequestIfc.RequestType;
import oracle.retail.stores.domain.manager.payment.ReversalRequestIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderCheckADO;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderGiftCardADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.tender.AuthorizationLaunchShuttle;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;

/**
 * Data required by the reversal service.
 */
public class ReversalCargo extends AbstractFinancialCargo implements Serializable
{
    /**
     * Logger
     */
    public static final Logger logger = Logger.getLogger(ReversalCargo.class);

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 786917546472092592L;

    /** List of Request objects */
    protected List<ReversalRequestIfc> requestList;
    /** Specific tender object to remove if specified. */
    protected TenderLineItemIfc tenderToDelete;

    /**
     * Lock tour for CPOI session.  Used by MobilePOS where the CPOI
     * device is shared.
     */
    protected boolean lockTour = false;

    /**
     * Transaction archive name for managing the pending authorizations within
     * the {@link oracle.retail.stores.pos.manager.archive.TransactionArchiveManagerIfc}. 
     */
    protected String transactionArchiveName;

    /**
     * Default constructor for ReversalCargo
     */
    public ReversalCargo()
    {
    }

    /**
     * Get the request list.
     * @returnrequestList
     */
    public List<ReversalRequestIfc> getRequestList()
    {
        return requestList;
    }

    /**
     * Set the request list.
     * @param requestList
     */
    public void setRequestList(List<ReversalRequestIfc> requestList)
    {
        this.requestList = requestList;
    }

    /**
     * @return the tenderToDelete
     */
    public TenderLineItemIfc getTenderToDelete()
    {
        return tenderToDelete;
    }

    /**
     * @param tenderToDelete the tenderToDelete to set
     */
    public void setTenderToDelete(TenderLineItemIfc tenderToDelete)
    {
        this.tenderToDelete = tenderToDelete;
    }

    /**
     * General toString function
     *
     * @return the String representation of this class
     */
    public String toString()
    {
        super.toString();
        ToStringBuilder builder = new ToStringBuilder(this);
        appendToString(builder);
        return builder.toString();
    }

    /**
     * Add printable objects to the builder. Overriding methods should also
     * call super.appendToString(ToStringBuilder).
     *
     * @param builder
     * @see #toString()
     */
    protected void appendToString(ToStringBuilder builder)
    {
        for (ReversalRequestIfc request : requestList)
        {
            builder.append(request.toString());
        }
    }

    /**
     * Create a SnapshotIfc which can subsequently be used to restore the cargo to its current state.
     *
     * @return an object which stores the current state of the cargo.
     * @see oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc
     */
    public SnapshotIfc makeSnapshot()
    {
        return new TourCamSnapshot(this);
    }

    /**
     * Reset the cargo data using the snapshot passed in.
     *
     * @param snapshot
     *            is the SnapshotIfc which contains the desired state of the cargo.
     * @exception ObjectRestoreException
     *                is thrown when the cargo cannot be restored with this snapshot
     */
    public void restoreSnapshot(SnapshotIfc snapshot) throws ObjectRestoreException
    {
    }

    /**
     * Builds a reversal request list from the reversal tenders in the cargo's transactionADO.
     *
     * @param bus
     * @param cargo
     */
    public static List<ReversalRequestIfc> buildRequestList(
                    WorkstationIfc workstation,
                    int transactionType,
                    RetailTransactionADOIfc currentTransactionADO)
    {
        List<ReversalRequestIfc> requestList = new ArrayList<ReversalRequestIfc>();
        String transactionID = currentTransactionADO.getTransactionID();

        TenderADOIfc[] reversalTenders =
            currentTransactionADO.getTenderLineItems(TenderLineItemCategoryEnum.REVERSAL_PENDING);

        for (TenderADOIfc tender: reversalTenders)
        {
            HashMap<String, Object> tenderAttributes = tender.getTenderAttributes();
            if(tender instanceof TenderGiftCardADO)
            {
                tenderAttributes.put(TenderConstants.ACTION_CODE, RequestSubType.VoidGiftCard);
            }
            boolean addToList = true;
            if(tender instanceof TenderCheckADO && !"ECheck".equals(tenderAttributes.get(TenderConstants.CHECK_AUTH_TYPE)))
            {
                addToList = false;
            }
            if(addToList)
            {
                ReversalRequestIfc reversalRequest = buildRequest(
                        workstation,
                        transactionID,
                        transactionType,
                        tenderAttributes);
                requestList.add(reversalRequest);
            }
        }

        return requestList;
    }

    /**
     * @return the lockTour
     */
    public boolean isLockTour()
    {
        return lockTour;
    }

    /**
     * @param lockTour the lockTour to set
     */
    public void setLockTour(boolean lockTour)
    {
        this.lockTour = lockTour;
    }

    /**
     * Build a reversal request for the specified
     * tender when Post Voiding a transaction.
     *
     * @param workstation the workstation
     * @param transactionID the ID of the transaction being reversed
     * @param transactionType the transaction type
     * @param tenderAttributes the tender attributes describing the tender to reverse
     * @return the reversal request
     */
    public static ReversalRequestIfc buildRequest(
                    WorkstationIfc workstation,
                    String transactionID,
                    int transactionType,
                    HashMap<String, Object> tenderAttributes)
    {
        ReversalRequestIfc request = DomainGateway.getFactory().getReversalRequestInstance();

        // fill in the fields which are common between the auth and reversal request
        AuthorizationLaunchShuttle.buildAuthRequest(
                        request,
                        workstation,
                        transactionID,
                        transactionType,
                        tenderAttributes);

        // fill the additional fields required for reversals
        TenderTypeEnum tenderType = (TenderTypeEnum)tenderAttributes.get(TenderConstants.TENDER_TYPE);
        if (TenderTypeEnum.CREDIT.equals(tenderType) || TenderTypeEnum.DEBIT.equals(tenderType))
        {
            request.setRequestType(RequestType.ReverseCard);
            String token = (String)tenderAttributes.get(TenderConstants.ACCOUNT_NUMBER_TOKEN);
            request.setAccountNumberToken(token);
            request.setJournalKey((String)tenderAttributes.get(TenderConstants.JOURNAL_KEY));
            request.setRetrievalReferenceNumber((String)tenderAttributes.get(TenderConstants.AUTH_SEQUENCE_NUMBER));
            request.setAuthorizationTime((String)tenderAttributes.get(TenderConstants.LOCAL_TIME));
            request.setAuthorizationDate((String)tenderAttributes.get(TenderConstants.LOCAL_DATE));
            request.setAccountDataSource((String)tenderAttributes.get(TenderConstants.ACCOUNT_DATA_SOURCE));
            request.setPaymentServiceIndicator((String)tenderAttributes.get(TenderConstants.PAYMENT_SERVICE_INDICATOR));
            request.setPaymentServiceTransactionID((String)tenderAttributes.get(TenderConstants.TRANSACTION_ID));
            request.setTransactionID(transactionID);
            request.setAuthorizationCode((String)tenderAttributes.get(TenderConstants.AUTH_CODE));
            request.setAuthResponseCode((String)tenderAttributes.get(TenderConstants.AUTH_RESPONSE_CODE));
            request.setValidationCode((String)tenderAttributes.get(TenderConstants.VALIDATION_CODE));
            request.setAuthorizationSource((String)tenderAttributes.get(TenderConstants.AUTH_SOURCE));
            request.setHostReference((String)tenderAttributes.get(TenderConstants.HOST_REFERENCE));
            request.setTraceNumber((String)tenderAttributes.get(TenderConstants.TRACE_NUMBER));

        }
        else if (TenderTypeEnum.GIFT_CARD.equals(tenderType))
        {
            request.setRequestType(RequestType.ReverseGiftCard);
            request.setRequestSubType(RequestSubType.VoidGiftCard);
            request.setJournalKey((String)tenderAttributes.get(TenderConstants.JOURNAL_KEY));
            request.setAuthorizationTime((String)tenderAttributes.get(TenderConstants.LOCAL_TIME));
            request.setAuthorizationDate((String)tenderAttributes.get(TenderConstants.LOCAL_DATE));
            request.setGiftCardAccountType((String)tenderAttributes.get(TenderConstants.GIFT_CARD_ACCOUNT_TYPE));
            // Message sequence is stored in reference code.
            request.setReferenceCode((String)tenderAttributes.get(TenderConstants.REFERENCE_CODE));
            // The payment application specific request code is stored in this element.
            request.setRequestCode((String)tenderAttributes.get(TenderConstants.REQUEST_CODE));
            request.setTraceNumber((String)tenderAttributes.get(TenderConstants.TRACE_NUMBER));
            EncipheredCardDataIfc cardData = ((EncipheredCardDataIfc)tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA));
            if(cardData != null)
            {
                request.setAccountNumber(cardData.getEncryptedAcctNumber());
            }
        }
        else if (TenderTypeEnum.CHECK.equals(tenderType))
        {
            request.setRequestType(RequestType.ReverseECheck);
            request.setAuthorizationCode((String)tenderAttributes.get(TenderConstants.AUTH_CODE)); 
            request.setAuthorizationSequenceNumber((String)tenderAttributes.get(TenderConstants.CHECK_AUTH_SEQUENCE_NUMBER));
        }
        else
        {
            logger.error("Unable to populate reversal request. Unexpected tender type encountered: " + tenderType);
        }

        return request;
    }

    /**
     * Build a reversal request for the specified
     * tender when Canceling a transaction.
     *
     * @param authRequest the original request
     * @param authResponse the original response
     * @param currentTransactionID the current transaction ID
     * @return the reversal request
     */
    public static ReversalRequestIfc buildRequest(AuthorizeTransferRequestIfc authRequest, AuthorizeTransferResponseIfc authResponse,
                    String currentTransactionID)
    {
        ReversalRequestIfc reversalRequest = DomainGateway.getFactory().getReversalRequestInstance();

        // fields from the original request
        reversalRequest.setWorkstation(authRequest.getWorkstation());
        reversalRequest.setTransactionType(authRequest.getTransactionType());
        reversalRequest.setRequestTenderType(authRequest.getRequestTenderType());
        reversalRequest.setAccountNumberEncipheredData(authRequest.getAccountNumberEncipheredData());
        reversalRequest.setCardData(authRequest.getCardData());
        reversalRequest.setEntryMethod(authRequest.getEntryMethod());
        reversalRequest.setABANumber(authRequest.getABANumber());
        reversalRequest.setTenderSequenceNumber(authRequest.getTenderSequenceNumber());
        reversalRequest.setMicrEncipheredData(authRequest.getMicrEncipheredData());
        reversalRequest.setPersonalIDEncipheredData(authRequest.getPersonalIDEncipheredData());
        reversalRequest.setPersonalIDType(authRequest.getPersonalIDType());
        reversalRequest.setPersonalIDAuthority(authRequest.getPersonalIDAuthority());
        reversalRequest.setPersonalIDEntryMethod(authRequest.getPersonalIDEntryMethod());
        reversalRequest.setPersonalIDTrack1Data(authRequest.getPersonalIDTrack1Data());
        reversalRequest.setPersonalIDTrack2Data(authRequest.getPersonalIDTrack2Data());
        reversalRequest.setPhoneNumber(authRequest.getPhoneNumber());
        reversalRequest.setConversionCode(authRequest.getConversionCode());
        reversalRequest.setFloorLimit(authRequest.getFloorLimit());
        reversalRequest.setRequestSubType(authRequest.getRequestSubType());
        reversalRequest.setAlternateAmount(authRequest.getAlternateAmount());
        reversalRequest.setAuthorizationTransactionType(authRequest.getAuthorizationTransactionType());

        // fields from the original response
        reversalRequest.setBaseAmount(authResponse.getBaseAmount().abs());

        TenderType tenderType = authResponse.getTenderType();
        if (TenderType.CREDIT.equals(tenderType) || TenderType.DEBIT.equals(tenderType))
        {
            reversalRequest.setRequestType(RequestType.ReverseCard);
            reversalRequest.setAccountNumberToken(authResponse.getAccountNumberToken());
            reversalRequest.setJournalKey(authResponse.getJournalKey());
            reversalRequest.setRetrievalReferenceNumber(authResponse.getRetrievalReferenceNumber());
            reversalRequest.setAuthorizationTime(authResponse.getLocalTime());
            reversalRequest.setAuthorizationDate(authResponse.getLocalDate());
            reversalRequest.setAccountDataSource(authResponse.getAccountDataSource());
            reversalRequest.setPaymentServiceIndicator(authResponse.getPaymentServiceIndicator());
            reversalRequest.setPaymentServiceTransactionID(authResponse.getTransactionId());
            reversalRequest.setTransactionID(currentTransactionID);
            reversalRequest.setAuthorizationCode(authResponse.getAuthorizationCode());
            reversalRequest.setAuthResponseCode(authResponse.getAuthResponseCode());
            reversalRequest.setValidationCode(authResponse.getValidationCode());
            reversalRequest.setAuthorizationSource(authResponse.getAuthorizationSource());
            reversalRequest.setHostReference(authResponse.getHostReference());
            reversalRequest.setTraceNumber(authResponse.getTraceNumber());
        }
        else if (TenderType.GIFT_CARD.equals(tenderType))
        {
            reversalRequest.setRequestType(RequestType.ReverseGiftCard);
            reversalRequest.setRequestSubType(RequestSubType.VoidGiftCard);
            reversalRequest.setJournalKey(authResponse.getJournalKey());
            reversalRequest.setAuthorizationTime(authResponse.getLocalTime());
            reversalRequest.setAuthorizationDate(authResponse.getLocalDate());
            reversalRequest.setGiftCardAccountType(authResponse.getGiftCardAccountType());
            // Message sequence is stored in reference code
            reversalRequest.setReferenceCode(authResponse.getReferenceCode());
            reversalRequest.setRequestCode(authResponse.getRequestCode());
            reversalRequest.setAccountNumber(authResponse.getAccountNumber());
        }
        else if (TenderType.CHECK.equals(tenderType))
        {
            reversalRequest.setRequestType(RequestType.ReverseECheck);
            reversalRequest.setAuthorizationCode(authResponse.getAuthorizationCode());
            reversalRequest.setAuthorizationSequenceNumber(authResponse.getAuthorizationSequenceNumber());
        }
        else
        {
            logger.error("Unable to populate reveral request. Unexpected tender type encountered: " + tenderType);
        }

        return reversalRequest;
    }

    /**
     * Returns the <code>transactionArchiveName</code> value.
     * @return the transactionArchiveName
     */
    public String getTransactionArchiveName()
    {
        return transactionArchiveName;
    }

    /**
     * Sets the <code>transactionArchiveName</code> value.
     * @param transactionArchiveName the transactionArchiveName to set
     */
    public void setTransactionArchiveName(String transactionArchiveName)
    {
        this.transactionArchiveName = transactionArchiveName;
    }
}