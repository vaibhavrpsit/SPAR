/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/AuthorizationLaunchShuttle.java /main/8 2014/07/01 13:33:27 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   06/05/14 - XbranchMerge
 *                         blarsen_bug18854403-ajb-call-ref-trans-cancel-bad-invoiceid
 *                         from rgbustores_14.0x_generic_branch
 *    blarsen   06/03/14 - Refactor: Moving call referral fields into their new
 *                         class.
 *    blarsen   02/04/14 - AJB requires original auth response for call
 *                         referrals. Adding this to appropriate
 *                         shuttles/cargos.
 *    rgour     11/22/13 - Setting CurrentTransactionADO value for
 *                         authorizationCargo in unload method
 *    asinton   11/15/13 - refactored to use TransactionUtilityManager to
 *                         obtain transactionArchiveName
 *    asinton   11/14/13 - added code to carry the transaction archive name to
 *                         reversal and authorization services for transaction
 *                         archival to support potential reversal of pending
 *                         authorizations in the case of application crash
 *    jswan     10/25/12 - Modified to support returns by order.
 *    asinton   08/02/12 - Call referral refactor
 *    asinton   07/02/12 - carry call referral authorization details from
 *                         Mobile POS to call referral site.
 *    asinton   12/29/11 - removed logic setting floor limit. this is now done
 *                         in AuthorizationSite.java
 *    cgreene   09/12/11 - revert aba number encryption, which is not sensitive
 *    icole     09/08/11 - Correct check ejournal entry for swipped drivers
 *                         license.
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    jswan     08/26/11 - Modified to prevent signature capture for Re-entry
 *                         transactionsactions.
 *    ohorne    08/18/11 - APF: check cleanup
 *    jswan     08/15/11 - Modified to support gift card reversals.
 *    ohorne    08/09/11 - APF:foreign currency support
 *    blarsen   08/02/11 - Misc cleanup. Renamed token to accountNumberToken.
 *    cgreene   07/28/11 - added support for manager override for card decline
 *    cgreene   07/21/11 - remove DebitBinFileLookup and DebitCardsAccepted
 *                         parameters for APF
 *    blarsen   07/19/11 - Removing dependency on TenderCargo from
 *                         buildAuthRequest so it can be used by reversal
 *                         service.
 *    rrkohli   07/19/11 - encryption cr
 *    blarsen   07/12/11 - Refactored auth-request-building code into its own
 *                         method so it can be called from reersal auth launch
 *                         shuttle.
 *    cgreene   07/12/11 - update generics
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    rrkohli   07/04/11 - Encryptin CR
 *    blarsen   07/01/11 - Fixed problem when entry method is not initialized.
 *    asinton   07/01/11 - Fixed ClassCastException
 *    rrkohli   06/30/11 - encryption CR
 *    asinton   06/29/11 - Refactored to use EntryMethod and
 *                         AuthorizationMethod enums.
 *    ohorne    06/21/11 - set Enciphered Card Data on request object
 *    cgreene   06/15/11 - implement gift card for servebase and training mode
 *    ohorne    06/15/11 - set entryMethod
 *    ohorne    06/09/11 - set TransactionType on request and handling for
 *                         encrypted # when tendering with HA/ShoppingPass
 *    asinton   05/31/11 - Refactored Gift Card Redeem and Tender for APF
 *    cgreene   05/27/11 - move auth response objects into domain
 *    blarsen   05/20/11 - Changed TenderType from int constants to a real
 *                         enum.
 *    ohorne    05/18/11 - set training mode flag on request
 *    ohorne    05/09/11 - Added support for Check
 *    asinton   03/29/11 - Removed subclasses of AuthorizeTransferRequest and
 *                         AuthorizeTransferResponse.
 *    asinton   03/25/11 - Moved APF request and response objects to common
 *                         module.
 *    asinton   03/21/11 - creating new tender authorization service
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc.RequestSubType;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModelIfc;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.tender.authorization.AuthorizationCargo;
import oracle.retail.stores.pos.services.tender.authorization.CallReferralData;
import oracle.retail.stores.pos.services.tender.tdo.TenderTDOConstants;

import org.apache.commons.codec.binary.Base64;


/**
 * Prepares the request for the Tender Authorization service.
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class AuthorizationLaunchShuttle extends FinancialCargoShuttle
{
    /** handle to the list of request objects. */
    protected List<AuthorizeTransferRequestIfc> requestList;
    /** The cargo from the tender service. */
    protected TenderCargo tenderCargo;

    /** call referral data */
    private CallReferralData callReferralData = new CallReferralData();

    /** transaction archive name */
    private String transactionArchiveName;

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        // create a list of 1 request for the tender
        requestList = new ArrayList<AuthorizeTransferRequestIfc>(1);
        // build request
        tenderCargo = (TenderCargo)bus.getCargo();
        AuthorizeTransferRequestIfc request = DomainGateway.getFactory().getAuthorizeTransferRequestInstance();

        HashMap<String, Object> tenderAttributes = tenderCargo.getTenderAttributes();

        WorkstationIfc workstation = tenderCargo.getRegister().getWorkstation();
        int transType = tenderCargo.getTransType();
        TenderableTransactionIfc transaction = tenderCargo.getTransaction();
        String transactionID = transaction.getTransactionID();
        buildAuthRequest(request, workstation, transactionID, transType, tenderAttributes);

        // build transacitonArchiveName
        TransactionUtilityManagerIfc transactionUtilityManager = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        transactionArchiveName = transactionUtilityManager.getArchiveName(transaction);

        // add request to the list
        requestList.add(request);

        callReferralData = tenderCargo.getCallReferralData();
    }

    /**
     * Build an auth request for the authorization service.
     *
     * This request is used by reversals as well as normal auths.
     *
     * @param request
     * @param workstation
     * @param transactionID
     * @param transactionType
     * @param tenderAttributes
     * @return
     */
    static public void buildAuthRequest(
                    AuthorizeTransferRequestIfc request,
                    WorkstationIfc workstation,
                    String transactionID,
                    int transactionType,
                    HashMap<String, Object> tenderAttributes)
    {

        String amount = (String)tenderAttributes.get(TenderConstants.AMOUNT);
        // the amount should be positive.  The type of request (i.e. charge in a sale, or a credit
        // as in a refund) should be handle with other field.
        CurrencyIfc requestAmount = DomainGateway.getBaseCurrencyInstance(amount);
        request.setBaseAmount(requestAmount.abs());

        CurrencyIfc alternateAmount = (CurrencyIfc) tenderAttributes.get(TenderTDOConstants.ALTERNATE_CURRENCY);
        if (alternateAmount != null)
        {
            request.setAlternateAmount(alternateAmount.abs());
        }
        else
        {
            request.setAlternateAmount(null);
        }

        int authorizationTransactionType = getAuthorizationTransactionType(transactionType, requestAmount);
        request.setAuthorizationTransactionType(authorizationTransactionType);

        request.setWorkstation(workstation);
        request.setTransactionID(transactionID);
        request.setTransactionType(transactionType);

        TenderTypeEnum tenderType = (TenderTypeEnum)tenderAttributes.get(TenderConstants.TENDER_TYPE);
        if (tenderType != null)
        {
            request.setRequestTenderType(tenderType.getTenderType());
        }

        AuthorizeRequestIfc.RequestType requestType = (AuthorizeRequestIfc.RequestType) tenderAttributes.get(TenderConstants.REQUEST_TYPE);
        request.setRequestType(requestType);

        EncipheredCardDataIfc cardData = (EncipheredCardDataIfc)tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA);
        if (cardData == null)
        {
          EncipheredDataIfc accountNumber = (EncipheredDataIfc) tenderAttributes.get(TenderConstants.ENCIPHERED_DATA_ACCOUNT_NUMBER);
          request.setAccountNumberEncipheredData(accountNumber);
        }
        else
        {
            request.setCardData(cardData);
        }

        Object entryMethodRaw = tenderAttributes.get(TenderConstants.ENTRY_METHOD);
        EntryMethod entryMethod = null;
        if (entryMethodRaw != null)
        {
            entryMethod = (EntryMethod)entryMethodRaw;
            request.setEntryMethod(entryMethod);
        }

        String abaNumber = (String) tenderAttributes.get(TenderConstants.ABA_NUMBER);
        request.setABANumber(abaNumber);

        String checkNumber = (String)tenderAttributes.get(TenderConstants.CHECK_NUMBER);
        request.setTenderSequenceNumber(checkNumber);

        EncipheredDataIfc micrData = (EncipheredDataIfc) tenderAttributes.get(TenderConstants.ENCIPHERED_DATA_MICR_NUMBER);
        if (micrData != null)
        {
            request.setMicrEncipheredData(micrData);
        }

        LocalizedCodeIfc personalIDType = (LocalizedCodeIfc) tenderAttributes.get(TenderConstants.LOCALIZED_ID_TYPE);
        request.setPersonalIDType(personalIDType);

        EntryMethod personalIDEntryMethod = (EntryMethod)tenderAttributes.get(TenderConstants.ID_ENTRY_METHOD);
        request.setPersonalIDEntryMethod(personalIDEntryMethod);

        if (EntryMethod.Swipe.equals(personalIDEntryMethod))
        {
            MSRModelIfc msrModel = (MSRModelIfc) tenderAttributes.get(TenderConstants.MSR_MODEL);

            byte[] customerIDTrack1Data = Base64.encodeBase64(msrModel.getTrack1Data());
            request.setPersonalIDTrack1Data(customerIDTrack1Data);

            byte[] customerIDTrack2Data = Base64.encodeBase64(msrModel.getTrack2Data());
            request.setPersonalIDTrack2Data(customerIDTrack2Data);

            String stateCode = parseStateCode(customerIDTrack1Data);
            request.setPersonalIDAuthority(stateCode);
        }
        else
        {
            String stateCode = (String)tenderAttributes.get(TenderConstants.ID_STATE);
            request.setPersonalIDAuthority(stateCode);
        }

        EncipheredDataIfc personalID = (EncipheredDataIfc) tenderAttributes.get(TenderConstants.ENCIPHERED_DATA_ID_NUMBER);
        request.setPersonalIDEncipheredData(personalID);

        String phoneNumber = (String) tenderAttributes.get(TenderConstants.PHONE_NUMBER);
        request.setPhoneNumber(phoneNumber);

        String conversionCode = (String)tenderAttributes.get(TenderConstants.CONVERSION_CODE);
        request.setConversionCode(conversionCode);

        String token = (String) tenderAttributes.get(TenderConstants.ACCOUNT_NUMBER_TOKEN);
        request.setAccountNumberToken(token);

        RequestSubType requestSubType = ((RequestSubType)tenderAttributes.get(TenderConstants.ACTION_CODE));
        if(requestSubType != null)
        {
            request.setRequestSubType(requestSubType);
        }

    }

    /*
     * Determine the Authorization Transaction Type from the tender cargo and requested amount.
     */
    private static int getAuthorizationTransactionType(int transactionType,
            CurrencyIfc requestAmount)
    {
        int retvalue = AuthorizationConstantsIfc.TRANS_SALE;
        if(transactionType == TransactionConstantsIfc.TYPE_VOID)
        {
            // based on sign of tender amount, set request action
            if (requestAmount.signum() == CurrencyIfc.POSITIVE)
            {
                retvalue = AuthorizationConstantsIfc.TRANS_VOID;
            }
            else
            {
                retvalue = AuthorizationConstantsIfc.TRANS_CREDIT_VOID;
            }
        }
        else
        {
            // based on sign of tender amount, set request action
            if (requestAmount.signum() == CurrencyIfc.POSITIVE)
            {
                retvalue = AuthorizationConstantsIfc.TRANS_SALE;
            }
            else
            {
                retvalue = AuthorizationConstantsIfc.TRANS_CREDIT;
            }
        }

        return retvalue;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        AuthorizationCargo authorizationCargo = (AuthorizationCargo)bus.getCargo();
        authorizationCargo.setRequestList(requestList);
        authorizationCargo.setRegister(tenderCargo.getRegister());
        authorizationCargo.setCreditReferralBeanModel(tenderCargo.getCreditReferralBeanModel());

        // MPOS: copy in call referral data captured from previous authorization attempt
        authorizationCargo.setCallReferralData(callReferralData);
        authorizationCargo.setCurrentTransactionADO(tenderCargo.getCurrentTransactionADO());


        // store the transactionArchiveName into the authorization cargo
        authorizationCargo.setTransactionArchiveName(transactionArchiveName);
    }

    /**
     * Parse the state code from the track 1 data of the swiped ID card.
     * @param encryptedTrack1Data encrypted track 1 data
     * @return the state code
     * @throws EncryptionServiceException
     */
    protected static String parseStateCode(byte[] encryptedTrack1)
    {
        String stateCode = null;
        byte[] track1 = null;
        try
        {
            KeyStoreEncryptionManagerIfc encryptionManager =(KeyStoreEncryptionManagerIfc)Gateway.getDispatcher().getManager(KeyStoreEncryptionManagerIfc.TYPE);
            track1 = encryptionManager.decrypt(Base64.decodeBase64(encryptedTrack1));

            // get the state code from track1
            if( track1 != null && track1.length >= 2)
            {
                byte[] tmp = new byte[2];
                tmp[0] = track1[0];
                tmp[1] = track1[1];
                stateCode = new String(tmp);
            }
        }
        catch (EncryptionServiceException e)
        {
            logger.warn("Unable to parse state code from track1 data", e);
        }
        finally
        {
            // dispose of track1 data
            Util.flushByteArray(track1);
            track1 = null;
        }
        return stateCode;
    }


}
