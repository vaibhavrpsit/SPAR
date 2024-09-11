/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.2  	07 Nov, 2017              Jyoti Yadav               Changes for Innoviti Integration CR
 * Rev  	1.1  	21 Dec, 2016              Ashish Yadav              Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.ado.tender;

import java.util.HashMap;

import max.retail.stores.domain.manager.tenderauth.MAXTenderAuthConstantsIfc;
import max.retail.stores.domain.manager.tenderauth.MAXTenderAuthResponse;
import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.pos.manager.ifc.MAXUtilityManagerIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderLimits;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PersonName;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.pos.ado.context.ADOContextIfc;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.tender.AuthResponseCodeEnum;
import oracle.retail.stores.pos.ado.tender.CreditTypeEnum;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderCreditADO;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.transaction.PaymentTransactionADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.utility.AuthorizationException;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;

public class MAXTenderCreditADO extends TenderCreditADO {

	/**
	 * 
	 */
	// Changes done for code merging
	public String authorizationResponseCode;
	// changes ends for code merging
	private static final long serialVersionUID = -5554909875373706226L;
	boolean voided = false;

	public boolean isVoided() {
		return voided;
	}

	public void setVoided(boolean voided) {
		this.voided = voided;
	}

	/*
	 * public void validate() throws TenderException { if (logger.isInfoEnabled()) {
	 * logger.info("Validating card information..."); }
	 * 
	 * // Make sure we have a valid swipe // validateMSRData();
	 * 
	 * // First determine whether we are in training mode if (!isTrainingMode()) {
	 * // Make sure we have a known card type // validateKnownCardType();
	 * 
	 * // Make sure we have a valid card type // validateCardTypeAccepted();
	 * 
	 * // Check the card number // validateCardNumber(); } }
	 */
	/**
	 * Returns a boolean indicating whether or not the card was swiped. This
	 * decision is based on whether we have an MSR model or not and on the entry
	 * method of the tender item.
	 * 
	 * @return card swiped boolean flag.
	 */
	public boolean isCreditCardSwiped() {

		boolean isCardSwiped = false;
		if (msrModel != null) {
			isCardSwiped = true;
		} else if (tenderRDO instanceof MAXTenderChargeIfc
				&& ((MAXTenderChargeIfc) tenderRDO).getEntryMethod() != null) {
// // Changes starts for code merging(commenting below line as per MAX)
			//if (((MAXTenderChargeIfc) tenderRDO).getEntryMethod().equals(TenderLineItemIfc.ENTRY_METHOD_MAGSWIPE)) {
			if (((MAXTenderChargeIfc) tenderRDO).getEntryMethod().equals(EntryMethod.Swipe)) {
	// Changes ends
				isCardSwiped = true;
			} else {
				isCardSwiped = false;
			}
		}

		return isCardSwiped;
	}

	/**
	 * Attempt a credit authorization
	 */
	public void authorize(HashMap authAttributes) throws AuthorizationException {
		if (logger.isInfoEnabled()) {
			logger.info("Authorizing credit...");
		}
		
	}

	/**
	 * Parse the authorization response
	 * 
	 * @param response
	 */
// Changes starts for cod merging(changing TenderAuthResponse to MAXTenderAuthResponse)
	//protected void decodeAuthResponse(TenderAuthResponse response)throws AuthorizationException {
	protected void decodeAuthResponse(MAXTenderAuthResponse response)throws AuthorizationException {
// Changes ends for code merging
		if (logger.isInfoEnabled()) {
			logger.info("Received credit authorization response...");
		}
		// amount will be same as tender amount (also same as request amount)
		((MAXTenderChargeIfc) tenderRDO).setAuthorizationAmount(getAmount().abs());
		if (response.getApprovalCode() == null) {
			// if there is no approval code then set to the response text for
			// receipt printing.
			((MAXTenderChargeIfc) tenderRDO).setAuthorizationCode(response
					.getResponseText());
		} else {
			((MAXTenderChargeIfc) tenderRDO).setAuthorizationCode(response
					.getApprovalCode());
		}
		((MAXTenderChargeIfc) tenderRDO).setAuthorizationResponse(response
				.getResponseText());
		((MAXTenderChargeIfc) tenderRDO)
		.setFinancialNetworkStatus(AuthorizableTenderIfc.AUTHORIZATION_NETWORK_ONLINE);
		((MAXTenderChargeIfc) tenderRDO)
		.setAuthorizationMethod(AuthorizableTenderIfc.AUTHORIZATION_METHOD_AUTO);
		authorizationResponseCode = response.getResponseCode();
		((MAXTenderChargeIfc) tenderRDO).setSettlementData(response
				.getSettlementData());
		((MAXTenderChargeIfc) tenderRDO).setAuthorizedDateTime(response
				.getAuthorizationDateTime());

		// the other values depend on response code
		// Approved
		if (authorizationResponseCode.equals(MAXTenderAuthConstantsIfc.APPROVED)) {
			// set status as approved
			((MAXTenderChargeIfc) tenderRDO)
			.setAuthorizationStatus(AuthorizableTenderIfc.AUTHORIZATION_STATUS_APPROVED);
		} else if (authorizationResponseCode
				.equals(MAXTenderAuthConstantsIfc.DECLINED)) {
			// set status as declined
			((MAXTenderChargeIfc) tenderRDO)
			.setAuthorizationStatus(AuthorizableTenderIfc.AUTHORIZATION_STATUS_DECLINED);
			// throw new exception
			throw new AuthorizationException("Credit Auth Declined",
					AuthResponseCodeEnum.DECLINED, response.getResponseText());
		} else if (authorizationResponseCode
				.equals(MAXTenderAuthConstantsIfc.ERROR_RETRY)) {
			// simply throw an excpetion to handle retry
			((MAXTenderChargeIfc) tenderRDO).setAuthorizationResponse(response
					.getResponseText());
			throw new AuthorizationException("ErrorRetry",
					AuthResponseCodeEnum.ERROR_RETRY, "");
		} else if (authorizationResponseCode
				.equals(MAXTenderAuthConstantsIfc.POSITIVE_ID)) {
			// simply throw an exception
			throw new AuthorizationException("PositiveID",
					AuthResponseCodeEnum.POSITIVE_ID, "");
		} else if (authorizationResponseCode
				.equals(MAXTenderAuthConstantsIfc.TIMEOUT)) {
			// set network status offline and auth method as manual
			((MAXTenderChargeIfc) tenderRDO)
			.setFinancialNetworkStatus(AuthorizableTenderIfc.AUTHORIZATION_NETWORK_OFFLINE);
			((MAXTenderChargeIfc) tenderRDO)
			.setAuthorizationMethod(AuthorizableTenderIfc.AUTHORIZATION_METHOD_MANUAL);
			if (offlineAuthorizationOk()) {
				((MAXTenderChargeIfc) tenderRDO)
				.setAuthorizationStatus(AuthorizableTenderIfc.AUTHORIZATION_STATUS_APPROVED);
				((MAXTenderChargeIfc) tenderRDO)
				.setAuthorizationMethod(AuthorizableTenderIfc.AUTHORIZATION_METHOD_SYSTEM);
				((MAXTenderChargeIfc) tenderRDO).setAuthorizationCode(getUtility()
						.getParameterValue("SystematicApprovalAuthCode", ""));
			}
			throw new AuthorizationException("Timeout",
					AuthResponseCodeEnum.TIMEOUT, "");
		} else if (authorizationResponseCode
				.equals(MAXTenderAuthConstantsIfc.OFFLINE)) {
			// set network status offline and auth method as manual
			((MAXTenderChargeIfc) tenderRDO)
			.setFinancialNetworkStatus(AuthorizableTenderIfc.AUTHORIZATION_NETWORK_OFFLINE);
			((MAXTenderChargeIfc) tenderRDO)
			.setAuthorizationMethod(AuthorizableTenderIfc.AUTHORIZATION_METHOD_MANUAL);
			// if we can auth offline, do it, otherwise throw an exception
			if (offlineAuthorizationOk()) {
				((MAXTenderChargeIfc) tenderRDO)
				.setAuthorizationStatus(AuthorizableTenderIfc.AUTHORIZATION_STATUS_APPROVED);
				((MAXTenderChargeIfc) tenderRDO)
				.setAuthorizationMethod(AuthorizableTenderIfc.AUTHORIZATION_METHOD_SYSTEM);
				((MAXTenderChargeIfc) tenderRDO).setAuthorizationCode(getUtility()
						.getParameterValue("SystematicApprovalAuthCode", ""));
			} else {
				throw new AuthorizationException("Offline",
						AuthResponseCodeEnum.OFFLINE, "");
			}
		} else if (authorizationResponseCode
				.equals(MAXTenderAuthConstantsIfc.REFERRAL)) {
			// throw an exception to route to call center screen.
			((MAXTenderChargeIfc) tenderRDO)
			.setAuthorizationMethod(AuthorizableTenderIfc.AUTHORIZATION_METHOD_MANUAL);
			throw new AuthorizationException("Call Center",
					AuthResponseCodeEnum.REFERRAL, response.getResponseText());
		} else if (authorizationResponseCode
				.equals((MAXTenderAuthConstantsIfc.FIRST_TIME_USAGE))) {
			if (determineCreditType() == CreditTypeEnum.HOUSECARD) {
				// throw an exception to route to check id
				throw new AuthorizationException("First Time Usage",
						AuthResponseCodeEnum.FIRST_TIME_USAGE, "");
			} else {
				// set status as approved
				((MAXTenderChargeIfc) tenderRDO)
				.setAuthorizationStatus(AuthorizableTenderIfc.AUTHORIZATION_STATUS_APPROVED);
			}
		} else {
			((MAXTenderChargeIfc) tenderRDO)
			.setAuthorizationMethod(AuthorizableTenderIfc.AUTHORIZATION_METHOD_MANUAL);
			throw new AuthorizationException("Call Center",
					AuthResponseCodeEnum.REFERRAL, response.getResponseText());
		}
	}

	public HashMap getTenderAttributes() {
		HashMap map = new HashMap();
		map.put(TenderConstants.TENDER_TYPE, getTenderType());
		map.put(TenderConstants.AMOUNT, getAmount().getStringValue());
		// occ approval code
		map.put(TenderConstants.OCC_APPROVAL_CODE,
				((MAXTenderChargeIfc) tenderRDO).getOccApprovalCode());
		if (capturedCustomerInfo()) {
			map.put(TenderConstants.ID_TYPE, ((MAXTenderChargeIfc) tenderRDO)
					.getPersonalIDType());
			map.put(TenderConstants.ID_COUNTRY, ((MAXTenderChargeIfc) tenderRDO)
					.getIDCountry());
			map.put(TenderConstants.ID_STATE, ((MAXTenderChargeIfc) tenderRDO)
					.getIDState());
			String expDateString = ((MAXTenderChargeIfc) tenderRDO)
			.getIDExpirationDate()
			.toFormattedString(
					LocaleMap
					.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
			map.put(TenderConstants.ID_EXPIRATION_DATE, expDateString);
		}
		if (isCreditCardSwiped()) {
			if (this.msrModel != null) {
				map.put(TenderConstants.MSR_MODEL, this.msrModel);
			}
// Changes starts for code merging(comment below line)
			//map.put(TenderConstants.ENTRY_METHOD,
					//TenderLineItemIfc.ENTRY_METHOD_MAGSWIPE);
			map.put(TenderConstants.ENTRY_METHOD,
					EntryMethod.Swipe);
// Changes ends for code merging
		} else {
// Changes starts for code merging(comment below line)
			//map.put(TenderConstants.ENTRY_METHOD,
					//TenderLineItemIfc.ENTRY_METHOD_MANUAL);
			map.put(TenderConstants.ENTRY_METHOD,
					EntryMethod.Manual);
// Changes ends for code merging
		}
		// always put these in regardless of entry method
		map.put(TenderConstants.NUMBER, ((MAXTenderChargeIfc) tenderRDO)
				.getCardNumber());
		map.put(TenderConstants.EXPIRATION_DATE, ((MAXTenderChargeIfc) tenderRDO)
				.getExpirationDateString());

		// authorization info
		// Only put in map if we have authorized (status should be non-zero)
		// if (isAuthorized()) {
		map.put(TenderConstants.AUTH_AMOUNT, ((MAXTenderChargeIfc) tenderRDO)
				.getAuthorizationAmount().getStringValue());
		map.put(TenderConstants.AUTH_CODE, ((MAXTenderChargeIfc) tenderRDO)
				.getAuthorizationCode());
		map.put(TenderConstants.AUTH_METHOD, ((MAXTenderChargeIfc) tenderRDO)
				.getAuthorizationMethod());
		map.put(TenderConstants.AUTH_RESPONSE, ((MAXTenderChargeIfc) tenderRDO)
				.getAuthorizationResponse());
		map.put(TenderConstants.AUTH_STATUS, new Integer(
				((MAXTenderChargeIfc) tenderRDO).getAuthorizationStatus()));
		map.put(TenderConstants.FINANCIAL_NETWORK_STATUS,
				((MAXTenderChargeIfc) tenderRDO).getFinancialNetworkStatus());
		map.put(TenderConstants.AUTH_RESPONSE_CODE, authorizationResponseCode);
		map.put(TenderConstants.SETTLEMENT_DATA, ((MAXTenderChargeIfc) tenderRDO)
				.getSettlementData());
		map.put(TenderConstants.AUTH_DATE_TIME, ((MAXTenderChargeIfc) tenderRDO)
				.getAuthorizedDateTime());

		map.put("INVOICE_NUMBER",
				((MAXTenderChargeIfc) tenderRDO).getInvoiceNumber());

		map.put("TRANSACTION_ACQ_NAME",
				((MAXTenderChargeIfc) tenderRDO).getTransactionAcquirer());

		map.put("ACQUIRER_BANK_CODE",
				((MAXTenderChargeIfc) tenderRDO).getAcquiringBankCode());

		map.put("REMARK", ((MAXTenderChargeIfc) tenderRDO)
				.getAuthRemarks());

		map.put("BATCH_NUMBER",
				((MAXTenderChargeIfc) tenderRDO).getBatchNumber());
		map.put("MERCHANT_ID",
				((MAXTenderChargeIfc) tenderRDO).getMerchID());

		// roshana for transaction type of credit tender.
		map.put("TERMINAL_ID",
				((MAXTenderChargeIfc) tenderRDO).getTID());
		map.put("TRANSACTION_TYPE",
				((MAXTenderChargeIfc) tenderRDO).getTransactionType());
		// end changes
		map.put("RETRIEVAL_REF_NUMBER",
				((MAXTenderChargeIfc) tenderRDO).getRetrievalRefNumber());
		// Added for Last four Digits of Credit Card
		map.put("LAST_FOUR_DIGITS",
				((MAXTenderChargeIfc) tenderRDO).getLastFourDigits());

		map.put(TenderConstants.AUTH_METHOD, ((MAXTenderChargeIfc) tenderRDO)
				.getAuthorizationMethod());
		// Added for including card Type in ChargeSlip
		map.put("CARD_TYPE", ((MAXTenderChargeIfc) tenderRDO)
				.getCardType());

		// added For Cardholders Name
		if (((MAXTenderChargeIfc) tenderRDO).getBearerName() != null) {

		// Added for CardHolder name
			map.put("CARDHOLDER_NAME",
					((MAXTenderChargeIfc) tenderRDO).getBearerName()
					.getFirstName()
					+ " "
					+ ((MAXTenderChargeIfc) tenderRDO).getBearerName()
					.getLastName());
		}
		/*Change for Rev 1.2: Start*/
		map.put(MAXTenderConstants.AUTH_CODE, ((MAXTenderChargeIfc) tenderRDO).getAuthCode());
		map.put(MAXTenderConstants.BANK_CODE, ((MAXTenderChargeIfc) tenderRDO).getBankCode());
		map.put(MAXTenderConstants.MERCHANT_TRANSACTION_ID, ((MAXTenderChargeIfc) tenderRDO).getMerchantTransactionId());
		/*Change for Rev 1.2: End*/
		// }

		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com._360commerce.ado.tender.TenderADOIfc#setTenderAttributes(java.util
	 * .HashMap)
	 */
	public void setTenderAttributes(HashMap tenderAttributes)
	throws TenderException {
		// get the amount
		((MAXTenderChargeIfc) tenderRDO)
		.setAmountTender(parseAmount((String) tenderAttributes
				.get(TenderConstants.AMOUNT)));
		/*Change for Rev 1.2: Start*/
		((TenderChargeIfc)tenderRDO).setAuthorizationRequestAmount(parseAmount((String) tenderAttributes
				.get(TenderConstants.AMOUNT)));
		EncipheredCardDataIfc encipheredCardData = (EncipheredCardDataIfc)tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA);
        ((TenderChargeIfc)tenderRDO).setEncipheredCardData(encipheredCardData);
		/*Change for Rev 1.2: End*/
		// occ approval code
		((MAXTenderChargeIfc) tenderRDO)
		.setOccApprovalCode((String) tenderAttributes
				.get(TenderConstants.OCC_APPROVAL_CODE));
		// card info
		PersonNameIfc person = new PersonName();
		person.setFirstName((String) tenderAttributes
				.get("CARDHOLDER_NAME"));
		((MAXTenderChargeIfc) tenderRDO).setBearerName(person);
		((MAXTenderChargeIfc) tenderRDO).setCardNumber((String) tenderAttributes
				.get("NUMBER"));
		/*Change for Rev 1.2: Start*/
		// Changes starts for Rev 1.1 (Ashish : Credit Card)
		/*if (tenderAttributes.get(TenderConstants.ENTRY_METHOD) != null)
        {
		EntryMethod entryMethod = (EntryMethod)tenderAttributes.get(TenderConstants.ENTRY_METHOD);
		((MAXTenderChargeIfc) tenderRDO).setEntryMethod((EntryMethod) tenderAttributes
				.get("ENTRY_METHOD"));
		((MAXTenderChargeIfc) tenderRDO).setEntryMethod(entryMethod);
        }*/
		// Changes starts for Rev 1.1 (Ashish : Credit Card)
		EntryMethod entryMethod = (EntryMethod)tenderAttributes.get(TenderConstants.ENTRY_METHOD);
        ((TenderChargeIfc)tenderRDO).setEntryMethod(entryMethod);
        boolean swiped = isCreditCardSwiped(tenderAttributes);
        if (entryMethod == null)
        {
            ((TenderChargeIfc)tenderRDO).setEntryMethod((swiped)? EntryMethod.Swipe : EntryMethod.Manual);
        }
        /*((TenderChargeIfc)tenderRDO).setCardNumber((String)tenderAttributes.get(TenderConstants.NUMBER));*/
        /*Change for Rev 1.2: End*/
		((MAXTenderChargeIfc) tenderRDO)
		.setExpirationDateString((String) tenderAttributes
				.get("EXPIRATION_DATE"));

		// Authorization info
		// Only get from map if authorization occurred
		if (tenderAttributes.get(TenderConstants.AUTH_RESPONSE) != null
				&& tenderAttributes.get(TenderConstants.AUTH_RESPONSE).equals(
				"APPROVED")) {

			((MAXTenderChargeIfc) tenderRDO)
			.setAuthorizationStatus(AuthorizableTenderIfc.AUTHORIZATION_STATUS_APPROVED);

			((MAXTenderChargeIfc) tenderRDO)
			.setAuthorizationAmount(parseAmount((String) tenderAttributes
					.get("AMOUNT")));
			((MAXTenderChargeIfc) tenderRDO)
			.setAuthorizationCode((String) tenderAttributes
					.get(TenderConstants.AUTH_CODE));
			((MAXTenderChargeIfc) tenderRDO)
			.setAuthorizationMethod((String) tenderAttributes
					.get(TenderConstants.AUTH_METHOD));
			((MAXTenderChargeIfc) tenderRDO)
			.setAuthorizationResponse((String) tenderAttributes
					.get(TenderConstants.AUTH_RESPONSE));

			authorizationResponseCode = (String) tenderAttributes
			.get(TenderConstants.AUTH_RESPONSE_CODE);

			/*
			 * ((MAXTenderChargeIfc) tenderRDO) .setAuthorizationStatus(((Integer)
			 * tenderAttributes .get(TenderConstants.AUTH_STATUS)).intValue());
			 */
			((MAXTenderChargeIfc) tenderRDO)
			.setFinancialNetworkStatus((String) tenderAttributes
					.get(TenderConstants.FINANCIAL_NETWORK_STATUS));
			((MAXTenderChargeIfc) tenderRDO)
			.setSettlementData((String) tenderAttributes
					.get(TenderConstants.SETTLEMENT_DATA));
			((MAXTenderChargeIfc) tenderRDO)
			.setAuthorizedDateTime((EYSDate) tenderAttributes
					.get(TenderConstants.AUTH_DATE_TIME));

			/*
			 * authorizationResponseCode = (String) tenderAttributes
			 * .get(TenderConstants.AUTH_RESPONSE_CODE);
			 */
			// START Added For Storing New details From plutus

			((MAXTenderChargeIfc) tenderRDO)
			.setInvoiceNumber((String) tenderAttributes
					.get("INVOICE_NUMBER"));

			((MAXTenderChargeIfc) tenderRDO)
			.setAuthRemarks((String) tenderAttributes
					.get("REMARK"));
			((MAXTenderChargeIfc) tenderRDO)
			.setBatchNumber((String) tenderAttributes
					.get("BATCH_NUMBER"));
			
			
			/*Change for Rev 1.2: Start*/
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
            /*Change for Rev 1.2: End*/
		}
		((MAXTenderChargeIfc) tenderRDO)
		.setTransactionAcquirer((String) tenderAttributes
				.get("TRANSACTION_ACQ_NAME"));
		((MAXTenderChargeIfc) tenderRDO)
		.setAcquiringBankCode((String) tenderAttributes
				.get("ACQUIRER_BANK_CODE"));
		if (tenderAttributes.get(TenderConstants.ID_TYPE) != null
				&& tenderAttributes.get(TenderConstants.ID_COUNTRY) != null
				&& tenderAttributes.get(TenderConstants.ID_STATE) != null
				&& tenderAttributes.get(TenderConstants.ID_EXPIRATION_DATE) != null) {
			// Changes for cod emerging(commenting below line)
			//((MAXTenderChargeIfc) tenderRDO).setIDType((String) tenderAttributes
					//.get(TenderConstants.ID_TYPE));
			((MAXTenderChargeIfc) tenderRDO).setPersonalIDType((LocalizedCodeIfc)tenderAttributes.get(TenderConstants.LOCALIZED_ID_TYPE));
			// Changes ends ofr code merging
			((MAXTenderChargeIfc) tenderRDO)
			.setIDCountry((String) tenderAttributes
					.get(TenderConstants.ID_COUNTRY));
			((MAXTenderChargeIfc) tenderRDO).setIDState((String) tenderAttributes
					.get(TenderConstants.ID_STATE));
			EYSDate idExpDate = parseExpirationDate(CARD_DATE_FORMAT,
					(String) tenderAttributes
					.get(TenderConstants.ID_EXPIRATION_DATE));
			((MAXTenderChargeIfc) tenderRDO).setIDExpirationDate(idExpDate);

		}

		// Perform additional steps
		if (null == tenderAttributes.get("CARD_TYPE")) {
			((MAXTenderChargeIfc) tenderRDO).setCardType("");
		} else {
			((MAXTenderChargeIfc) tenderRDO).setCardType(tenderAttributes.get(
					"CARD_TYPE").toString());

		}

		((MAXTenderChargeIfc) tenderRDO)
		.setRetrievalRefNumber((String) tenderAttributes
				.get("RETRIEVAL_REF_NUMBER"));
		((MAXTenderChargeIfc) tenderRDO).setTID((String) tenderAttributes
				.get("TERMINAL_ID"));
		((MAXTenderChargeIfc) tenderRDO).setMerchID((String) tenderAttributes
				.get("MERCHANT_ID"));
		((MAXTenderChargeIfc) tenderRDO)
		.setTransactionType((String) tenderAttributes
				.get("TRANSACTION_TYPE"));
		((MAXTenderChargeIfc) tenderRDO)
		.setLastFourDigits(getLastFourDigits((String) tenderAttributes
				.get("NUMBER")));

		/*Change for Rev 1.2: Start*/
		if (tenderRDO instanceof MAXTenderChargeIfc) {
			if(tenderAttributes.get(MAXTenderConstants.AUTH_CODE) != null){
				((MAXTenderChargeIfc) tenderRDO).setAuthCode(tenderAttributes.get(MAXTenderConstants.AUTH_CODE).toString());
			}
			if(tenderAttributes.get(MAXTenderConstants.BANK_CODE) != null){
				((MAXTenderChargeIfc) tenderRDO).setBankCode(tenderAttributes.get(MAXTenderConstants.BANK_CODE).toString());
			}
			if(tenderAttributes.get(MAXTenderConstants.BANK_NAME) != null){
				((MAXTenderChargeIfc) tenderRDO).setCardType(tenderAttributes.get(MAXTenderConstants.BANK_NAME).toString());
			}
			if((((MAXTenderChargeIfc) tenderRDO).getBankName() ==null || ((MAXTenderChargeIfc) tenderRDO).getBankName().equalsIgnoreCase("")) &&
					tenderAttributes.get(MAXTenderConstants.BANK_NAME) != null){
				((MAXTenderChargeIfc) tenderRDO).setBankName(tenderAttributes.get(MAXTenderConstants.BANK_NAME).toString());
			}
		}
		/*Change for Rev 1.2: End*/
	}

	/**
	 * 
	 * This Method will give the Last Four Digits
	 * 
	 * @param cardNumber
	 * @return String Last four Digi
	 */
	protected String getLastFourDigits(String cardNumber) {
		String lastFourDigit = "";

		if (cardNumber != null) {
			if (cardNumber.length() > 3) {
				lastFourDigit = cardNumber.substring(cardNumber.length() - 4,
						cardNumber.length());
			} else {
				lastFourDigit = cardNumber;
			}
		}
		return lastFourDigit;
	}
	
	// Changes starts for code merging(added below methods as it is not present in 14 base but present in 12 base)
	public boolean offlineAuthorizationOk()
    {
        boolean result = false;

        UtilityIfc util = getUtility();
        CurrencyIfc floorLimit = DomainGateway.getBaseCurrencyInstance(util.getParameterValue("OfflineCreditFloorLimit", "50.00"));

        // check limit
        if (!floorLimit.equals(TenderLimits.getTenderNoLimitAmount()) &&
            !(getAmount().abs().compareTo(floorLimit) == CurrencyIfc.GREATER_THAN))
        {
            result = true;
        }

        return result;
    }
	protected CreditTypeEnum determineCreditType()
    {
        ADOContextIfc context = getContext();
  // changes starts for code merging(changing UtilityManagerIfc to MAxUtilityManagerIfc)
        //UtilityManagerIfc utility = (UtilityManagerIfc)context.getManager(UtilityManagerIfc.TYPE);
        MAXUtilityManagerIfc utility = (MAXUtilityManagerIfc)context.getManager(UtilityManagerIfc.TYPE);
  // Changes neds for code merging

        CreditTypeEnum returnType = utility.determineCreditType(((TenderChargeIfc)tenderRDO).getCardNumber());

        if(logger.isDebugEnabled())
        {
            logger.debug("Credit type is: " + returnType);
        }

        return returnType;
    }
	// Changes ends for code merging
	// Changes starts for rev 1.1 (Ashish : Credit card)
	public static void checkHouseAcctOnHousePayment(EncipheredCardDataIfc cardData, RetailTransactionADOIfc trans)
			throws TenderException {
		if ((getCreditType(cardData) != CreditTypeEnum.HOUSECARD) || (!(trans instanceof PaymentTransactionADO))) {
			return;
		}
		throw new TenderException("Cannot make House Account payment with House Account card",
				TenderErrorCodeEnum.INVALID_TENDER_TYPE);
	}
	protected static CreditTypeEnum getCreditType(EncipheredCardDataIfc cardNumber) {
		ADOContextIfc context = ContextFactory.getInstance().getContext();
		UtilityManagerIfc utility = (UtilityManagerIfc) context.getManager("UtilityManager");
		return utility.determineCreditType(cardNumber);
	}
	// Changes starts for rev 1.1 (Ashish : Credit card)

}
