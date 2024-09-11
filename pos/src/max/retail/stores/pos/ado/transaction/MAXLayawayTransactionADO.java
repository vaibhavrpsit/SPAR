/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Apr 13, 2017		Mansi Goel		Changes to disable store credit 
 *													button if layaway is deleted	
 *
 ********************************************************************************/

package max.retail.stores.pos.ado.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import max.retail.stores.pos.ado.tender.MAXTenderTypeEnum;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.domain.tender.TenderTypeMap;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.LayawayTransactionADO;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;

public class MAXLayawayTransactionADO extends LayawayTransactionADO {

	private static final long serialVersionUID = 3730758169270178142L;

	public TenderTypeEnum[] getEnabledTenderOptions() {
		// temporary list. initialize to a size that can hold
		// all tender types
		ArrayList<TenderTypeEnum> tenderList = new ArrayList<TenderTypeEnum>(14);

		// local String constants
		final String NONE = "None";

		// create utility object
		UtilityIfc util = getUtility();

		// /////
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

		// //////
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

		// ///////
		// Non-Store Coupon
		if (util.getParameterValue("CouponsAccepted", "Y").equalsIgnoreCase("Y")) {
			tenderList.add(TenderTypeEnum.COUPON);
		}

		// ///////
		// Credit
		if (util.getParameterValue("CreditCardsAccepted", "Y").equalsIgnoreCase("Y")) {
			tenderList.add(TenderTypeEnum.CREDIT);
		}

		// //////
		// Debit
		if (util.getParameterValue("DebitCardsAccepted", "Y").equalsIgnoreCase("Y")) {
			tenderList.add(TenderTypeEnum.DEBIT);
		}

		// //////////
		// Gift Card
		if (util.getParameterValue("GiftCardsAccepted", "Y").equalsIgnoreCase("Y")) {
			tenderList.add(TenderTypeEnum.GIFT_CARD);
		}

		// //////
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

		// ///////////////
		// Purchase Order
		if (util.getParameterValue("PurchaseOrdersAccepted", "Y").equalsIgnoreCase("Y")) {
			tenderList.add(TenderTypeEnum.PURCHASE_ORDER);
		}

		// /////////////
		// Store Credit
		/**
		 * if (util.getParameterValue("StoreCreditsAccepted",
		 * "Y").equalsIgnoreCase("Y")) {
		 * tenderList.add(TenderTypeEnum.STORE_CREDIT); }
		 **/

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

		// /////////////
		// Mall Certificate
		if (util.getParameterValue("MallCertificateAccepted", "Y").equalsIgnoreCase("Y")) {
			tenderList.add(TenderTypeEnum.MALL_CERT);
		}

		// /////////////
		// Money Order
		if (util.getParameterValue("MoneyOrderAccepted", "Y").equalsIgnoreCase("Y")) {
			tenderList.add(TenderTypeEnum.MONEY_ORDER);
		}

		// ///////////////
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
		/** MAX Rev 1.0 Change : Start **/
		if (util.getParameterValue("LoyaltyPointsAccepted", "Y").equalsIgnoreCase("Y")) {
			tenderList.add(MAXTenderTypeEnum.LOYALTY_POINTS);
		}
		
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

	// Changes for Rev 1.0 : Starts
	@Override
	public List<Integer> getEnabledRefundTenderTypes() {
		calculateRefundOptionsRow();

		TreeSet<Integer> TenderTypesSet = new TreeSet<Integer>();

		// if we're in trans reentry mode, clear the list and add all tender
		// types
		WorkstationIfc ws = transactionRDO.getWorkstation();
		boolean transactionReentry = ws.isTransReentryMode();
		if (transactionReentry) {
			TenderTypesSet.add(TenderLineItemConstantsIfc.TENDER_TYPE_CASH);
			TenderTypesSet.add(TenderLineItemConstantsIfc.TENDER_TYPE_CHARGE);
			TenderTypesSet.add(TenderLineItemConstantsIfc.TENDER_TYPE_GIFT_CARD);
			TenderTypesSet.add(TenderLineItemConstantsIfc.TENDER_TYPE_STORE_CREDIT);
			TenderTypesSet.add(TenderLineItemConstantsIfc.TENDER_TYPE_MAIL_BANK_CHECK);
		} else if (transactionRDO instanceof SaleReturnTransactionIfc) {
			ReturnTenderDataElementIfc[] originalTenders = ((SaleReturnTransactionIfc) transactionRDO)
					.getReturnTenderElements();
			AbstractTransactionLineItemIfc[] lineItems = ((SaleReturnTransactionIfc) transactionRDO)
					.getItemContainerProxy().getLineItems();

			if (originalTenders != null) {
				for (ReturnTenderDataElementIfc originalTender : originalTenders) {
					// Only include positive (i.e. sale) tenders
					if (originalTender.getTenderAmount().signum() > 0) {
						if (!(this.isLayawayDelete()) && originalTenders.length > 1)
							TenderTypesSet.addAll(getRefundTenderTypes(originalTender.getTenderType()));
					}
				}
			}

			for (AbstractTransactionLineItemIfc lineItem : lineItems) {
				if (lineItem instanceof SaleReturnLineItemIfc) {
					SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) lineItem;
					if (srli.getReturnItem() != null
							&& srli.getReturnItem().getUserSuppliedTenderType() != TenderLineItemConstantsIfc.TENDER_TYPE_UNKNOWN) {
						TenderTypesSet.addAll(getRefundTenderTypes(srli.getReturnItem().getUserSuppliedTenderType()));
					}
				}
			}

			if (TenderTypesSet.isEmpty() && !(this.isLayawayDelete())) {
				TenderTypesSet.addAll(getNonRetrievedRefundTenderTypes());
			}
		}

		List<Integer> refundTenderTypes = new ArrayList<Integer>();
		refundTenderTypes.addAll(TenderTypesSet);
		return refundTenderTypes;
	}

	private TreeSet<Integer> getRefundTenderTypes(int tenderType) {
		TenderTypeMapIfc map = TenderTypeMap.getTenderTypeMap();
		String parameterName = "RefundTenderFor" + map.getDescriptor(tenderType) + "Payment";
		TreeSet<Integer> acceptedTenderTypes = new TreeSet<Integer>();

		try {
			String[] tendersDescriptors = getParameterManager().getStringValues(parameterName);
			for (String descriptor : tendersDescriptors) {
				acceptedTenderTypes.add(map.getTypeFromDescriptor(descriptor));
			}
		} catch (ParameterException e) {
			logger.error("Could not retrieve " + parameterName + " from the ParameterManager.", e);
		}

		return acceptedTenderTypes;
	}

	private TreeSet<Integer> getNonRetrievedRefundTenderTypes() {
		TenderTypeMapIfc map = TenderTypeMap.getTenderTypeMap();
		TreeSet<Integer> acceptedTenderTypes = new TreeSet<Integer>();

		try {
			String[] tendersDescriptors = getParameterManager().getStringValues("RefundTenderForNonRetrievedTrans");
			for (String descriptor : tendersDescriptors) {
				acceptedTenderTypes.add(map.getTypeFromDescriptor(descriptor));
			}
		} catch (ParameterException e) {
			logger.error("Could not retrieve RefundTenderForNonRetrievedTrans from the ParameterManager.", e);
		}

		return acceptedTenderTypes;
	}
	// Changes for Rev 1.0 : Ends
}
