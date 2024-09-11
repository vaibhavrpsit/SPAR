/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Sep 13, 2016		Ashish Yadav	Changes for Code Merging	
 *
 ********************************************************************************/

package max.retail.stores.pos.ado.transaction;

import java.util.ArrayList;
import java.util.Iterator;

import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.ado.tender.MAXTenderTypeEnum;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.lineitem.LineItemADOIfc;
import oracle.retail.stores.pos.ado.lineitem.LineItemTypeEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.SaleReturnTransactionADO;
import oracle.retail.stores.pos.ado.transaction.TenderStateEnum;
import oracle.retail.stores.pos.ado.transaction.VoidException;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;

public class MAXSaleReturnTransactionADO extends SaleReturnTransactionADO {

	private static final long serialVersionUID = -9011193686524077590L;

	public TenderTypeEnum[] getEnabledTenderOptions() {

		ArrayList<TenderTypeEnum> tenderList = new ArrayList<TenderTypeEnum>(16);
		String NONE = "None";

		// create utility object
		UtilityIfc util = getUtility();
		// Cash
		String[] cashAccepted = util.getParameterValueList("CashAccepted");
		if (cashAccepted == null) {
			// initialize to take base currency
			cashAccepted = new String[1];
			cashAccepted[0] = NONE;
		}
		// if our base cash description exists as one of the cash accepted
		// options, add to list
		if (util.isStringListed(DomainGateway.getBaseCurrencyInstance().getDescription(), cashAccepted)) {
			tenderList.add(TenderTypeEnum.CASH);
		}

		// Check
		String[] checksAccepted = util.getParameterValueList("ChecksAccepted");
		if (checksAccepted == null) {
			checksAccepted = new String[1];
			checksAccepted[0] = NONE;
		} else {
			checksAccepted = extractCheckCurrencyPart(checksAccepted);
		}
		// Add Check to the list if the base currency is listed in the check
		// currencies
		if (util.isStringListed(DomainGateway.getBaseCurrencyInstance().getDescription(), checksAccepted)) {
			tenderList.add(TenderTypeEnum.CHECK);
		}

		// Non-Store Coupon
		if (util.getParameterValue("CouponsAccepted", "Y").equalsIgnoreCase("Y")) {
			tenderList.add(TenderTypeEnum.COUPON);
		}

		// Credit
		if (util.getParameterValue("CreditCardsAccepted", "Y").equalsIgnoreCase("Y")) {
			tenderList.add(TenderTypeEnum.CREDIT);
		}

		// Debit
		if (util.getParameterValue("DebitCardsAccepted", "Y").equalsIgnoreCase("Y")) {
			tenderList.add(TenderTypeEnum.DEBIT);
		}

		// Gift Card
		if (util.getParameterValue("GiftCardsAccepted", "Y").equalsIgnoreCase("Y")) {
			tenderList.add(TenderTypeEnum.GIFT_CARD);
		}

		// Gift Cert
		String[] gcAccepted = util.getParameterValueList("GiftCertificatesAccepted");
		if (gcAccepted == null) {
			gcAccepted = new String[1];
			gcAccepted[0] = NONE;
		}
		// Add gift certificate to the list if the base currency is listed in
		// the gift certificate currencies
		if (util.isStringListed(DomainGateway.getBaseCurrencyInstance().getDescription(), gcAccepted)) {
			tenderList.add(TenderTypeEnum.GIFT_CERT);
		}

		// Purchase Order
		if (util.getParameterValue("PurchaseOrdersAccepted", "Y").equalsIgnoreCase("Y")) {
			tenderList.add(TenderTypeEnum.PURCHASE_ORDER);
		}

		// Store Credit
		String[] scAccepted = util.getParameterValueList("StoreCreditsAccepted");
		if (scAccepted == null) {
			scAccepted = new String[1];
			scAccepted[0] = NONE;
		}

		// Add store credit to the list if the base currency is listed in the
		// store credit currencies
		if (util.isStringListed(DomainGateway.getBaseCurrencyInstance().getDescription(), scAccepted)) {
			tenderList.add(TenderTypeEnum.STORE_CREDIT);
		}

		// Mall Certificate
		if (util.getParameterValue("MallCertificateAccepted", "Y").equalsIgnoreCase("Y")) {
			tenderList.add(TenderTypeEnum.MALL_CERT);
		}

		// Money Order
		if (util.getParameterValue("MoneyOrderAccepted", "Y").equalsIgnoreCase("Y")) {
			tenderList.add(TenderTypeEnum.MONEY_ORDER);
		}

		// Traveler Check
		String[] travChecksAccepted = util.getParameterValueList("TravelersChecksAccepted");
		if (travChecksAccepted == null) {
			travChecksAccepted = new String[1];
			travChecksAccepted[0] = NONE;
		} else {
			travChecksAccepted = extractCheckCurrencyPart(travChecksAccepted);
		}
		// Add Traveler Check to the list if the base currency is listed in the
		// check currencies
		if (util.isStringListed(DomainGateway.getBaseCurrencyInstance().getDescription(), travChecksAccepted)) {
			tenderList.add(TenderTypeEnum.TRAVELERS_CHECK);
		}
		if (util.getParameterValue("LoyaltyPointsAccepted", "Y").equalsIgnoreCase("Y")) {
			tenderList.add(MAXTenderTypeEnum.LOYALTY_POINTS);
		}
		// Changes Starts by Bhanu Priya
		tenderList.add(MAXTenderTypeEnum.PAYTM);
		tenderList.add(MAXTenderTypeEnum.MOBIKWIK);
		// Changes Ends by Bhanu Priya
		tenderList.add(MAXTenderTypeEnum.ECOM_PREPAID);
		tenderList.add(MAXTenderTypeEnum.ECOM_COD);
		// Enable the button for the "Alternate" currency if Cash or Traveler's
		// checks or Checks
		// are accepted in more than one currency and there are alternate
		// currencies
		// available.
		// Note that "Alternate" is not the label for the button, just the
		// action
		CurrencyTypeIfc[] altCurrencies = DomainGateway.getAlternateCurrencyTypes();
		String baseCurrency = DomainGateway.getBaseCurrencyInstance().getDescription();
		ArrayList<String> parmAltCash = getAltCurrenciesAccepted(baseCurrency, cashAccepted);
		ArrayList<String> parmAltTC = getAltCurrenciesAccepted(baseCurrency, travChecksAccepted);
		ArrayList<String> parmAltCheck = getAltCurrenciesAccepted(baseCurrency, checksAccepted);

		if ((altCurrencies != null) && (altCurrencies.length > 0)) {
			String firstAltCurr = altCurrencies[0].getCurrencyCode();

			// If the first alternate currency (from domain) appears in one of
			// the
			// accepted tender parameters
			if (util.isStringListed(firstAltCurr, parmAltCash.toArray())
					|| util.isStringListed(firstAltCurr, parmAltTC.toArray())
					|| util.isStringListed(firstAltCurr, parmAltCheck.toArray())) {
				tenderList.add(TenderTypeEnum.ALTERNATE);
			}
		}

		// convert list to array
		TenderTypeEnum[] tenderTypeArray = new TenderTypeEnum[tenderList.size()];
		tenderTypeArray = tenderList.toArray(tenderTypeArray);
		return tenderTypeArray;
	}

	public void fromLegacy(EYSDomainIfc rdo) {
		transactionRDO = (SaleReturnTransaction) rdo;

		// get and convert RDO tenders
		Iterator<TenderLineItemIfc> iter = ((TenderableTransactionIfc) transactionRDO).getTenderLineItemsVector()
				.iterator();
		while (iter.hasNext()) {
			// Create ADO tender from RDO tender
			TenderLineItemIfc tenderRDO = (TenderLineItemIfc) iter.next();
			TenderTypeEnum type = TenderTypeEnum.makeEnumFromString(tenderRDO.getTypeDescriptorString());
			TenderFactoryIfc factory;
			try {
				factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
				TenderADOIfc tenderADO;
				if (type != null) { // if using hash map
					tenderADO = factory.createTender(type);
				} else {
					tenderADO = factory.createTender(tenderRDO);
				}
				((ADO) tenderADO).fromLegacy(tenderRDO);

				// add the tender to the transaction
				addTenderNoValidation(tenderADO);
			} catch (ADOException e) {
				logger.error(e.getMessage(), e);
				String message = "No exception should occur here.  " + "Please correct the source of the problem.";
				assert (false) : message;
			}
		}
	}

	public TenderStateEnum evaluateTenderState() {
		// recalculate transaction total if needed based on dirty flag
		// set by tender.
		recalculateTransactionTotal();

		TenderStateEnum result = null;
	//	String tendertype=get`.toString();
		// 1) if the balance is positive, tenders are due
		if (getBalanceDue().signum() == CurrencyIfc.POSITIVE) {
			result = TenderStateEnum.TENDER_OPTIONS;
		}
		// 2) if the balance is negative, the forced cash change is zero, and
		// the grand total of the transaction is positive, then change is due
		else if (getBalanceDue().signum() == CurrencyIfc.NEGATIVE
				&& getForcedCashChangeAmount().signum() == CurrencyIfc.ZERO
				&& getTransactionGrandTotal().signum() == CurrencyIfc.POSITIVE) {
			result = TenderStateEnum.CHANGE_DUE;
		}
		// 3) If the balance is negative, forced cash change is positive, and
		// grand total is positive, then change due
		else if (getBalanceDue().signum() == CurrencyIfc.NEGATIVE
				&& getForcedCashChangeAmount().signum() == CurrencyIfc.POSITIVE
				&& getTransactionGrandTotal().signum() == CurrencyIfc.POSITIVE) {
			result = TenderStateEnum.CHANGE_DUE;
		}
		// 3) If the balance is zero, forced cash change is positive, and
		// grand total is positive, then paid up
		else if (getBalanceDue().signum() == CurrencyIfc.ZERO
				&& getForcedCashChangeAmount().signum() == CurrencyIfc.POSITIVE
				&& getTransactionGrandTotal().signum() == CurrencyIfc.POSITIVE) {
			result = TenderStateEnum.PAID_UP;
		}
		// 4) if the balance is negative and the grand total of the transaction
		// is positive, then refund tenders are due.
		else if (getBalanceDue().signum() == CurrencyIfc.NEGATIVE
				&& getTransactionGrandTotal().signum() == CurrencyIfc.NEGATIVE) {
			result = TenderStateEnum.REFUND_OPTIONS;
		}
		// 5) There is a refund due
		else if (getBalanceDue().signum() == CurrencyIfc.ZERO
				&& getTransactionGrandTotal().signum() == CurrencyIfc.NEGATIVE) {
			result = TenderStateEnum.REFUND_DUE;
		} else if (((MAXSaleReturnTransaction) transactionRDO).isFatalDeviceCall()) {
			result = TenderStateEnum.TENDER_OPTIONS;

			// result = TenderStateEnum.PAID_UP;
		}
		// 6) All paid up
		else {
			result = TenderStateEnum.PAID_UP;
		}

		return result;
	}

	public boolean isVoidable(String currentTillID) throws VoidException {
		// 1 Make sure the transaction has the same Till ID
		voidCheckForSameTill(currentTillID);

		// 2) Transaction should not already be voided
		voidCheckForPreviousVoid();

		// 3) Make sure any issued tenders have not been used.
		voidCheckForIssuedTenderModifications();

		// Changes for Rev 1.0 : Starts
		voidCheckForGiftCardItems();
		// Changes for Rev 1.0 : Ends

		// 5) Check for modified transaction
		voidCheckForModifiedTransaction();

		// 6) Check that void is allowed for tranasactions
		// containing debit tenders
		voidCheckDebitAllowed();

		// 7) Make sure the transaction is not suspended
		voidCheckForSuspendedTransaction();

		return true;
	}

	// Changes for Rev 1.0 : Starts
	protected void voidCheckForGiftCardItems() throws VoidException {
		LineItemADOIfc[] giftCards = getLineItemsForType(LineItemTypeEnum.TYPE_GIFT_CARD);
		if (giftCards != null && giftCards.length > 0) {
			throw new VoidException("Transaction Modified", MAXVoidErrorCodeEnum.GIFT_CARD_NOT_ALLOWED);
		}
	}
	// Changes for Rev 1.0 : Ends

}
