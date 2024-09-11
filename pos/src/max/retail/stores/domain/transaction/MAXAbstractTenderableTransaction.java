/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 * 
 * 
 * Rev 1.1  08 Nov, 2016              Nadia              MAX-StoreCredi_Return requirement.
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.domain.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerConstantsIfc;
import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.factory.MAXDomainObjectFactory;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.IRSCustomerIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCashIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderCouponIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLimits;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheckIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.transaction.AbstractTenderableTransaction;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.CountryCodeMap;

/**
 * This class contains the behavior associated with a tenderable transaction,
 * i.e., a transaction that involves the tendering of money.
 *
 * @see oracle.retail.stores.domain.transaction.TenderableTransactionIfc
 */
public abstract class MAXAbstractTenderableTransaction extends AbstractTenderableTransaction {
	// This id is used to tell the compiler not to generate a new
	// serialVersionUID.
	static final long serialVersionUID = 7452948367237270458L;

	/**
	 * tender line items
	 */
	protected Vector<TenderLineItemIfc> tenderLineItemsVector;

	/**
	 * transaction totals
	 */
	protected MAXTransactionTotalsIfc totals;

	/**
	 * customer assignation
	 */
	protected CustomerIfc customer;

	/**
	 * irs customer assignation
	 */
	protected IRSCustomerIfc irsCustomer;

	/**
	 * uniqueId -- used to store ID for layaway, special order, and store
	 * credits.
	 */
	protected String uniqueID;

	/**
	 * This vector contains all echeck declined that need to be franked.
	 */
	protected Vector<TenderLineItemIfc> ECheckDeclinedItems;

	/**
	 * return ticket id received from Returns Management server
	 */
	protected String returnTicket = null;

	protected String customerId = null;

	protected CustomerIfc ticCustomer = null;

	/**
	**/
	protected void initialize() {
		tenderLineItemsVector = new Vector<TenderLineItemIfc>();
		MAXDomainObjectFactory domainFactory = (MAXDomainObjectFactory) DomainGateway
				.getFactory();
		totals = domainFactory.getTransactionTotalsInstance();
	}

	/**
	 * Sets clone attributes. This method is provided to facilitate
	 * extensibility.
	 *
	 * @param newClass
	 *            new instance of AbstractTenderableTransaction
	 */
	protected void setCloneAttributes(MAXAbstractTenderableTransaction newClass) {
		// set attributes
		super.setCloneAttributes(newClass);

		newClass.setTransactionTotals((TransactionTotalsIfc) (totals.clone()));
		// clone tender line items
		TenderLineItemIfc[] tli = getTenderLineItems();
		if (tli != null) {
			TenderLineItemIfc[] tclone = new TenderLineItemIfc[tli.length];
			for (int i = 0; i < tli.length; i++) {
				tclone[i] = (TenderLineItemIfc) tli[i].clone();
			}
			newClass.setTenderLineItems(tclone);
		}
		if (customer != null) {
			newClass.setCustomer((CustomerIfc) customer.clone());
		}
		if (irsCustomer != null) {
			newClass.setIRSCustomer((IRSCustomerIfc) irsCustomer.clone());
		}
		if (uniqueID != null) {
			newClass.setUniqueID(this.getUniqueID());
		}

		if (this.getECheckDeclinedItems() != null) {
			int declinedEchecks = this.getECheckDeclinedItems().size();
			if (declinedEchecks > 0) {
				for (int i = 0; i < declinedEchecks; i++) {
					newClass.addECheckDeclinedItems(getECheckDeclinedItems().get(i));
				}
			}
		}
		if (this.getTicCustomer() == null) {
			if (this.getCustomer() != null && ((MAXCustomer) this.getCustomer()).getCustomerType()
					.equalsIgnoreCase(MAXCustomerConstantsIfc.CRM))
				newClass.setTicCustomer(this.getCustomer());
		} else
			newClass.setTicCustomer(this.getTicCustomer());
	}

	/**
	 * Determines whether this transaction has any tender line items that match
	 * the given tender type code. Type codes are defined in TenderLineItemIfc.
	 *
	 * @param tenderType
	 *            int tender type code to use in the test.
	 * @return boolean if the line item collection includes one or more tender
	 *         line items of the given type.
	 */
	public boolean containsTenderLineItems(int tenderType) {
		boolean value = false;

		for (Iterator<TenderLineItemIfc> i = tenderLineItemsVector.iterator(); i.hasNext();) {
			if (tenderType == i.next().getTypeCode()) {
				value = true;
				break;
			}
		}
		return value;
	}

	/**
	 * Returns an array containing the line items of a given tender type which
	 * are contained by this transaction. If no items of the given type are
	 * found, the returned array will have a length of 0.
	 *
	 * @param tenderType
	 *            int indicating the tender line item type to iterate over.
	 * @return TenderLineItemIfc[] containing tender line items of the given
	 *         type.
	 */
	public TenderLineItemIfc[] getTenderLineItemArray(int tenderType) {
		Collection<TenderLineItemIfc> c = getTenderLineItems(tenderType);
		TenderLineItemIfc[] array = new TenderLineItemIfc[c.size()];
		c.toArray(array);
		return array;
	}

	/**
	 * Returns an iterator over the line items of a given tender type which are
	 * contained by this transaction. If no items of the given type are found,
	 * Iterator.hasNext() will return false.
	 *
	 * @param tenderType
	 *            int indicating the tender line item type to iterate over.
	 * @return Iterator over tender line items of the given type.
	 */
	public Iterator<TenderLineItemIfc> getTenderLineItemIterator(int tenderType) {
		return getTenderLineItems(tenderType).iterator();
	}

	/**
	 * Returns a collection of tender line items of a given tender type which
	 * are contained by this transaction. If no items of the given type are
	 * found, Collection.isEmpty() will return true.
	 *
	 * @param tenderType
	 *            int indicating the tender line item type to return.
	 * @return Collection containing tender line items of the given type.
	 */
	public Collection<TenderLineItemIfc> getTenderLineItems(int tenderType) {
		Collection<TenderLineItemIfc> items = new ArrayList<TenderLineItemIfc>();
		TenderLineItemIfc item = null;

		for (Iterator<TenderLineItemIfc> i = tenderLineItemsVector.iterator(); i.hasNext();) {
			item = i.next();
			if (tenderType == item.getTypeCode()) {
				items.add(item);
			}
		}
		return items;
	}

	/**
	 * Removes all tender line items of the given tender type from this
	 * transaction. If no items of the given type are found, no action is taken.
	 *
	 * @param tenderType
	 *            int type code indicating the tender line item type to remove.
	 */
	public void removeTenderLineItems(int tenderType) {
		for (Iterator<TenderLineItemIfc> i = tenderLineItemsVector.iterator(); i.hasNext();) {
			if (tenderType == i.next().getTypeCode()) {
				i.remove();
			}
		}
		updateTenderTotals();
	}

	/**
	 * Removes all tender line items
	 */
	public void removeTenderLineItems() {
		tenderLineItemsVector.removeAllElements();
		updateTenderTotals();
	}

	/**
	 * Update totals in the Transaction totals object.
	 */
	public void updateTenderTotals() {
		totals.updateTenderTotals(getTenderLineItems());
	}

	/**
	 * Calculates FinancialTotals based on current transaction.
	 *
	 * @return FinancialTotalsIfc object
	 */
	public abstract FinancialTotalsIfc getFinancialTotals();

	/**
	 * Derive additive financial totals from tender line items.
	 *
	 * @param tenderLineItems
	 *            array of tender line items
	 * @param transTotals
	 *            transaction totals
	 * @return additive financial totals
	 */
	protected FinancialTotalsIfc getTenderFinancialTotals(TenderLineItemIfc[] tenderLineItems,
			TransactionTotalsIfc transTotals) {
		FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();
		// get size of array
		int numTenderLineItems = 0;

		if (tenderLineItems != null) {
			numTenderLineItems = tenderLineItems.length;
		}

		// if elements exist, loop through them
		for (int i = 0; i < numTenderLineItems; i++) {
			if (tenderLineItems[i] != null) {
				financialTotals = financialTotals.add(getFinancialTotalsFromTender(tenderLineItems[i]));
			}
		}

		return (financialTotals);
	}

	/**
	 * Derive the additive financial totals from a given tender line item.
	 *
	 * @param tli
	 *            TenderLineItemIfc entry
	 * @return FinancialTotalsIfc
	 */
	public FinancialTotalsIfc getFinancialTotalsFromTender(TenderLineItemIfc tli) {
		CurrencyIfc amount = tli.getAmountTender();
		TenderDescriptorIfc descriptor = DomainGateway.getFactory().getTenderDescriptorInstance();
		String desc = tli.getTypeDescriptorString();

		// Get the appropriate amount based on the currency used.
		if (tli instanceof TenderAlternateCurrencyIfc) {
			TenderAlternateCurrencyIfc alternate = (TenderAlternateCurrencyIfc) tli;
			CurrencyIfc alternateAmount = alternate.getAlternateCurrencyTendered();

			// if tendered in alternate currency, use that amount.
			if (alternateAmount != null) {
				amount = alternate.getAlternateCurrencyTendered();
				// Prepend nationality to the description.
				String countryCode = amount.getCountryCode();
				String countryDescriptor = CountryCodeMap.getCountryDescriptor(countryCode);
				if (countryDescriptor == null) {
					countryDescriptor = countryCode;
				}
				desc = countryDescriptor + " " + desc;
			}
		}

		String currencyCode = amount.getCountryCode();
		descriptor.setCountryCode(currencyCode);
		descriptor.setCurrencyID(amount.getType().getCurrencyId());
		int tenderType = tli.getTypeCode();

		descriptor.setTenderType(tenderType);
		int numberItems = 1;

		// Traveler's checks have multiple checks for one line item.
		if (tenderType == TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK) {
			numberItems = ((TenderTravelersCheckIfc) tli).getNumberChecks();
		}

		String sDesc = null;

		// Add individual credit card totals to the financial totals
		if (tenderType == TenderLineItemIfc.TENDER_TYPE_CHARGE) {
			// Assuming that Credit cards are always handled in the local
			// currency,
			// so there is no conflict between this description and the
			// alternate above.
			desc = ((TenderChargeIfc) tli).getCardType();
			sDesc = tli.getTypeDescriptorString();
			descriptor.setTenderSubType(desc);
		}
		if (tenderType == TenderLineItemIfc.TENDER_TYPE_COUPON) {
			// Assuming that Cr

			descriptor.setTenderSubType(((TenderCouponIfc) tli).getCouponNumber());
		}
		if (tenderType == TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE) {
			String mallCertType = ((TenderGiftCertificateIfc) tli).getCertificateType();
			if (mallCertType != null && !mallCertType.equals("")) {
				TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
				descriptor.setTenderType(tenderType);
				desc = tenderTypeMap.getDescriptor(tenderType);
				sDesc = tenderTypeMap.getDescriptor(tenderType);
			}

		}

		CurrencyIfc amtIn = null;
		CurrencyIfc amtOut = null;
		int cntIn = 0;
		int cntOut = 0;

		// Set amounts, count in/count out
		if (tli.getAmountTender().signum() == CurrencyIfc.POSITIVE) {
			amtIn = amount;
			amtOut = DomainGateway.getCurrencyInstance(currencyCode);
			cntIn = numberItems;
		} else {
			amtIn = DomainGateway.getCurrencyInstance(currencyCode);
			amtOut = amount.negate();
			cntOut = numberItems;
		}

		FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();
		financialTotals.getTenderCount().addTenderItem(descriptor, cntIn, cntOut, amtIn, amtOut, desc, sDesc,
				tli.getHasDenominations());

		if (tli instanceof TenderStoreCreditIfc) {
			// Add Store Credit Issued ONLY.
			if (tli.getAmountTender().signum() == CurrencyIfc.NEGATIVE) {
				financialTotals.addAmountGrossStoreCreditsIssued(amtOut);
				financialTotals.addUnitsGrossStoreCreditsIssued(BigDecimalConstants.ONE_AMOUNT);
			}
			// Else it is an use of StoreCredit as tender; should not considered
			// as Redeem (Cash Redeem).
		} else if (tli instanceof TenderGiftCardIfc) {
			// if transaction is a refund, add the gift card amount to the item
			// credit bucket
			if (getTransactionType() == TransactionConstantsIfc.TYPE_RETURN
					|| getTransactionType() == TransactionConstantsIfc.TYPE_LAYAWAY_DELETE
					|| getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_CANCEL) {
				financialTotals.addAmountGrossGiftCardItemCredit(tli.getAmountTender().abs());
				financialTotals.addUnitsGrossGiftCardItemCredit(BigDecimalConstants.ONE_AMOUNT);
			}

			// If the transaction is a sale and the tender amount is less than 0
			if (getTransactionType() == TransactionConstantsIfc.TYPE_SALE && tli.getAmountTender().signum() < 0) {
				financialTotals.addAmountGrossGiftCardItemCredit(tli.getAmountTender().abs());
				financialTotals.addUnitsGrossGiftCardItemCredit(BigDecimalConstants.ONE_AMOUNT);
			}
		}

		return (financialTotals);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.retail.stores.domain.transaction.TenderableTransactionIfc#
	 * getCollectedTenderLineItems()
	 */
	public TenderLineItemIfc[] getCollectedTenderLineItems() {
		List<TenderLineItemIfc> tenders = new ArrayList<TenderLineItemIfc>(tenderLineItemsVector.size());
		for (TenderLineItemIfc tender : tenderLineItemsVector) {
			if (tender.isCollected()) {
				tenders.add(tender);
			}
		}
		return tenders.toArray(new TenderLineItemIfc[tenders.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.retail.stores.domain.transaction.TenderableTransactionIfc#
	 * getIssuedStoreCredit()
	 */
	public TenderStoreCreditIfc[] getIssuedStoreCredit() {
		List<TenderStoreCreditIfc> tenderStoreCredit = new ArrayList<TenderStoreCreditIfc>();
		for (TenderLineItemIfc item : tenderLineItemsVector) {
			if (item instanceof TenderStoreCreditIfc && ((TenderStoreCreditIfc) item).isIssued()) {
				tenderStoreCredit.add((TenderStoreCreditIfc) item);
			}
		}
		return tenderStoreCredit.toArray(new TenderStoreCreditIfc[tenderStoreCredit.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.retail.stores.domain.transaction.TenderableTransactionIfc#
	 * getCollectedTenderTotalAmount()
	 */
	public CurrencyIfc getCollectedTenderTotalAmount() {
		CurrencyIfc collectedTotal = DomainGateway.getBaseCurrencyInstance();
		TenderLineItemIfc[] tli = getCollectedTenderLineItems();
		for (int i = 0; i < tli.length; i++) {
			collectedTotal = collectedTotal.add(tli[i].getAmountTender());
		}
		return collectedTotal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.retail.stores.domain.transaction.TenderableTransactionIfc#
	 * getTenderTotalAmountPlusChangeDue()
	 */
	public CurrencyIfc getTenderTotalAmountPlusChangeDue() {
		CurrencyIfc total = DomainGateway.getBaseCurrencyInstance();
		TenderLineItemIfc[] tli = getTenderLineItems();
		for (int i = 0; i < tli.length; i++) {
			total = total.add(tli[i].getAmountTender());
		}
		// Since the change due is negative, subtracting this amount will
		// increase
		// the total by this amount.
		return total.subtract(calculateChangeDue())
				.subtract(getTenderTransactionTotals().getCashChangeRoundingAdjustment());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.retail.stores.domain.transaction.TenderableTransactionIfc#
	 * hasCollectedTenderLineItems()
	 */
	public boolean hasCollectedTenderLineItems() {
		return (getCollectedTenderLineItems().length > 0);
	}

	/**
	 * Retrieves tender line items array in vector form. Don't modify this
	 * vector!
	 *
	 * @return vector of tender line items for this transaction
	 */
	public Vector<TenderLineItemIfc> getTenderLineItemsVector() {
		return tenderLineItemsVector;
	}

	/**
	 * Retrieves tender line items array.
	 *
	 * @return tender line items for this transaction
	 */
	public TenderLineItemIfc[] getTenderLineItems() {
		TenderLineItemIfc[] items = new TenderLineItemIfc[tenderLineItemsVector.size()];
		tenderLineItemsVector.toArray(items);
		return items;
	}

	/**
	 * Set tender line items array and update totals.
	 *
	 * @param tli
	 *            array tender line items
	 */
	public void setTenderLineItems(TenderLineItemIfc[] tli) {
		tenderLineItemsVector.clear();
		if (tli != null) {
			for (TenderLineItemIfc tender : tli) {
				tenderLineItemsVector.add(tender);
				tender.setLineNumber(tenderLineItemsVector.size() - 1);
			}
		}
		updateTenderTotals();
	}

	/**
	 * Adds tender line item if it is within Tender Limits.
	 *
	 * @param item
	 *            oracle.retail.stores.domain.tender.TenderLineItemIfc The
	 *            tender line item to be added
	 * @exception IllegalArgumentException
	 *                if tender line item cannot be added
	 */
	public void addTenderLineItem(TenderLineItemIfc item) throws IllegalArgumentException {
		// if no tender limits on this object, add them
		if (item.getTenderLimits() == null) {
			item.setTenderLimits(getTenderLimits());
		}

		if (totals.getGrandTotal().signum() == CurrencyIfc.POSITIVE) {
			// test maximum change for the transaction
			if (exceedsMaxChangeLimit(item)) {
				throw new IllegalArgumentException("New tender item amount [" + item.getAmountTender()
						+ "] would exceed cash back limit for " + item.getTypeDescriptorString() + " tender.");

			}

			// test maximum amount for the transaction
			if (exceedsMaxAmountLimit(item)) {
				throw new IllegalArgumentException("New tender item amount [" + item.getAmountTender()
						+ "] would exceed maximum allowable amount for " + item.getTypeDescriptorString() + " tender.");
			}
		}
		// add element to vector
		addTender(item);
	}

	/**
	 * Tests tender line item to see if it can be added. This tests to see if
	 * maximum change value has been overrun.
	 *
	 * @param item
	 *            TenderLineItemIfc item to be added
	 * @return boolean true if limit exceeded.
	 */
	public boolean exceedsMaxChangeLimit(TenderLineItemIfc item) {
		boolean value = false;
		boolean exempt = false;
		// get change amount parameter for each tender line item
		CurrencyIfc maximumChange = DomainGateway.getBaseCurrencyInstance();
		CurrencyIfc noLimit = (CurrencyIfc) TenderLimits.getTenderNoLimitAmount().clone();
		CurrencyIfc itemMaximumChange = null;
		TenderLineItemIfc testItem = null;
		int numberLines = tenderLineItemsVector.size();
		// loop through number of lines and then additional line
		for (int i = 0; i < (numberLines + 1); i++) {
			// if still in vector, use item from vector
			if (i < numberLines) {
				testItem = tenderLineItemsVector.get(i);
			}
			// if done with vector, use additional item
			else {
				testItem = item;
			}

			// find out if exempt
			if (testItem.IsExemptFromMaxCashLimit()) {
				exempt = true;
				break; // no point in continuing for loop
			}

			// get maximum change property for tender line item
			itemMaximumChange = testItem.getAmountMaximumChange();

			// if no limit set, then it's zero, so we do nothing
			if (itemMaximumChange != null && !itemMaximumChange.equals(noLimit))
			// if limit exists, accumulate it
			{
				maximumChange = maximumChange.add(itemMaximumChange);
			}
		}

		// check against overpayment
		CurrencyIfc amountTendered = totals.getAmountTender().add(item.getAmountTender());
		CurrencyIfc amountOverpay = amountTendered.subtract(totals.getGrandTotal());

		// compare overpay to maximum change
		if (amountOverpay.compareTo(maximumChange) == CurrencyIfc.GREATER_THAN && !exempt) {
			value = true;
		}
		return value;
	}

	/**
	 * Tests a tender line item to see if it can be added. This tests to see if
	 * adding the item to the transaction will exceed the maximum amount for the
	 * item's tender type.
	 *
	 * @param item
	 *            TenderLineItemIfc item to be added
	 * @return boolean true if limit exceeded.
	 */
	public boolean exceedsMaxAmountLimit(TenderLineItemIfc item) {
		boolean value = false;
		// get maximum amount parameter for each tender line item
		CurrencyIfc currentAmount = DomainGateway.getBaseCurrencyInstance();
		CurrencyIfc maximumAmount = null;
		TenderLineItemIfc temp = null;

		switch (item.getTypeCode()) {
		case TenderLineItemIfc.TENDER_TYPE_CASH:
			maximumAmount = getTenderLimits().getCurrencyLimit("MaximumCashAccepted");
			break;
		case TenderLineItemIfc.TENDER_TYPE_CHECK:
			maximumAmount = getTenderLimits().getCurrencyLimit("MaximumCheckAmount");
			break;
		case TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK:
			maximumAmount = getTenderLimits().getCurrencyLimit("MaximumTravelersCheckAmount");
			break;
		default:
			return value;
		}

		// loop through current tender items and sum the amounts for the target
		// item's tender type
		for (Enumeration<TenderLineItemIfc> e = tenderLineItemsVector.elements(); e.hasMoreElements();) {
			temp = e.nextElement();
			if (temp.getTypeCode() == item.getTypeCode()) {
				currentAmount = currentAmount.add(temp.getAmountTender());
			}
		}

		// add item amount to the current total for the type
		currentAmount = currentAmount.add(item.getAmountTender());

		// compare to maximum amount
		if (currentAmount.compareTo(maximumAmount) == CurrencyIfc.GREATER_THAN) {
			value = true;
		}
		return value;
	}

	/**
	 * Tests tender line item to see if it can be added. This tests to see if
	 * maximum refund value has been overrun.
	 *
	 * @param item
	 *            TenderLineItemIfc item to be added
	 * @return boolean true if limit exceeded.
	 */
	public boolean exceedsMaxCashRefundLimit(TenderLineItemIfc item) {
		boolean value = false;

		// Don't bother if it's not a return or not cash
		if (transactionType == TYPE_RETURN && item.getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CASH) {
			CurrencyIfc maximumRefund = ((TenderCashIfc) item).getAmountMaximumRefund();
			CurrencyIfc totalRefundAmount = item.getAmountTender();
			TenderLineItemIfc testItem = null;
			int numberLines = tenderLineItemsVector.size();

			// accumulate the cash refund amounts
			for (int i = 0; i < numberLines; i++) {
				testItem = tenderLineItemsVector.get(i);

				// if it's cash add it to the cash refund total
				if (testItem.getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CASH) {
					totalRefundAmount = totalRefundAmount.add(testItem.getAmountTender());
				}
			}

			// Is the total over the limit?
			if (totalRefundAmount.compareTo(maximumRefund.negate()) == CurrencyIfc.LESS_THAN) {
				value = true;
			}
		}
		return value;
	}

	/**
	 * Adds tender line item.
	 *
	 * @param tender
	 *            The tender line item to be added
	 */
	public void addTender(TenderLineItemIfc tender) {
		// If no tender limits on this object, add them
		if (tender.getTenderLimits() == null) {
			tender.setTenderLimits(getTenderLimits());
		}
		// add element to vector
		tenderLineItemsVector.addElement(tender);
		tender.setLineNumber(tenderLineItemsVector.size() - 1);
		// update totals
		updateTenderTotals();
	}

	/**
	 * Remove a tender line from the transaction.
	 *
	 * @param index
	 *            Index of the item to remove
	 */
	public void removeTenderLineItem(int index) {
		tenderLineItemsVector.removeElementAt(index);
		updateTenderTotals();
	}

	/**
	 * Remove a tender line from the transaction.
	 *
	 * @param tenderToRemove
	 *            Tender line item to remove from the list
	 */
	public void removeTenderLineItem(TenderLineItemIfc tenderToRemove) {
		// first try to remove the tender based on object equality
		if (tenderLineItemsVector.contains(tenderToRemove)) {
			tenderLineItemsVector.remove(tenderToRemove);
		}
		// next try based on line number
		else {
			tenderLineItemsVector.removeElementAt(tenderToRemove.getLineNumber());
		}
		updateTenderTotals();
	}

	/**
	 * Retrieves customer.
	 *
	 * @return customer
	 */
	public CustomerIfc getCustomer() {
		return (customer);
	}

	/**
	 * Sets customer attribute. Note that this does not perform any other
	 * customer-related operations on the transaction, such as establishing
	 * discount rules for customer-based discounts.
	 *
	 * @param value
	 *            customer
	 */
	public void setCustomer(CustomerIfc value) {
		customer = value;
		if (value instanceof MAXCustomer)
			if (((MAXCustomer) value).getCustomerType().equalsIgnoreCase(MAXCustomerConstantsIfc.CRM))
				ticCustomer = value;
	}

	/**
	 * Retrieves irs customer.
	 *
	 * @return IRSCustomerIfc irs customer reference
	 */
	public IRSCustomerIfc getIRSCustomer() {
		return (irsCustomer);
	}

	/**
	 * Sets irs customer
	 *
	 * @param value
	 *            irs customer
	 */
	public void setIRSCustomer(IRSCustomerIfc value) {
		irsCustomer = value;
	}

	/**
	 * Gets uniqueID.
	 *
	 * @return uniqueID
	 */
	public String getUniqueID() {
		return this.uniqueID;
	}

	/**
	 * Sets the UniqueID
	 *
	 * @param uniqueID
	 *            value to set
	 */
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	/**
	 * Get return ticket ID, which was created by RM.
	 *
	 * @see oracle.retail.stores.domain.transaction.TenderableTransactionIfc#getReturnTicket()
	 */
	public String getReturnTicket() {
		return returnTicket;
	}

	/**
	 * Set return ticket ID, which was created by RM.
	 *
	 * @see oracle.retail.stores.domain.transaction.TenderableTransactionIfc#setReturnTicket(java.lang.String)
	 */
	public void setReturnTicket(String returnTicket) {
		this.returnTicket = returnTicket;
	}

	/**
	 * Calculates the change due according to the transaction type. This method
	 * is intended to be overwritten by transactions for which the change due is
	 * not the same as the balance due PLUS the forced cash change amount
	 * (negative cash change) due to depleted gift card tenders.
	 *
	 * @return changeDue as CurrencyIfc
	 */
	public CurrencyIfc calculateChangeDue() {
		CurrencyIfc changeDue = totals.getBalanceDue();

		// if we are in a return then the negative cash is not change it is a
		// refund
		if (!(getTransactionType() == TransactionConstantsIfc.TYPE_RETURN
				|| getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_CANCEL
				|| getTransactionType() == TransactionConstantsIfc.TYPE_REDEEM
				|| getTransactionType() == TransactionConstantsIfc.TYPE_LAYAWAY_DELETE)) {
			// calculate sum of negative cash tenders
			changeDue = changeDue.add(getNegativeCashTotal());
		}

		// HPQC-2894 / BugDB-8331862 : 'Change Due' line shows on a canceled
		// transaction
		if (this.getTransactionStatus() == TransactionConstantsIfc.STATUS_CANCELED) {
			changeDue.setZero(); // force the change due back to zero, since no
									// tender was taken
		}

		return (changeDue);
	}

	/**
	 * Calculates the change given on a transaction considering the Rounded
	 * Change Amount
	 *
	 * @return changeGiven as CurrencyIfc
	 */
	public CurrencyIfc calculateChangeGiven() {
		CurrencyIfc adjustment = getTenderTransactionTotals().getCashChangeRoundingAdjustment();
		return calculateChangeDue().add(adjustment);
	}

	/**
	 * Calculates sum of all negative cash tenders
	 *
	 * @return
	 */
	public CurrencyIfc getNegativeCashTotal() {
		TenderLineItemIfc[] cashTenders = getTenderLineItemArray(TenderLineItemIfc.TENDER_TYPE_CASH);
		CurrencyIfc result = DomainGateway.getBaseCurrencyInstance();
		for (int i = 0; cashTenders != null && i < cashTenders.length; i++) {
			if (cashTenders[i].getAmountTender().signum() == CurrencyIfc.NEGATIVE) {
				result = result.add(cashTenders[i].getAmountTender());
			}
		}
		return result;
	}

	/**
	 * Retrieves transaction totals object.
	 *
	 * @return transaction totals object
	 */
	public MAXTransactionTotalsIfc getTransactionTotals() {
		return (totals);
	}

	/**
	 * Sets transaction totals object.
	 *
	 * @param value
	 *            transaction totals object to set
	 */
	public void setTransactionTotals(MAXTransactionTotalsIfc value) {
		totals = value;
	}

	/**
	 * Retrieves tender display transaction totals. In this case, these are the
	 * same as the standard transaction totals.
	 *
	 * @return tender display transaction totals
	 */
	public TransactionTotalsIfc getTenderTransactionTotals() {
		return (getTransactionTotals());
	}

	/**
	 * Retrieves size of tenderLineItemsVector vector.
	 *
	 * @return tender line items vector size
	 */
	public int getTenderLineItemsSize() {
		return tenderLineItemsVector.size();
	}

	/**
	 * The metod set the echeck declined items.
	 *
	 * @param item
	 */
	public void addECheckDeclinedItems(TenderLineItemIfc item) {
		if (ECheckDeclinedItems == null) {
			ECheckDeclinedItems = new Vector<TenderLineItemIfc>();
		}
		if (item != null) {
			ECheckDeclinedItems.addElement(item);
		}
	}

	/**
	 * This method returns all echecks that were declined.
	 *
	 * @return vector Decline echecks
	 */
	public Vector<TenderLineItemIfc> getECheckDeclinedItems() {
		return ECheckDeclinedItems;
	}

	/**
	 * Check to see if the transaction is taxable. A transaction is taxable is
	 * assumed taxable, unless every item in the transaction is a non-taxable
	 * item.
	 *
	 * @return true or false
	 * @since 7.0
	 */
	public boolean isTaxableTransaction() {
		boolean taxableItemFound = false;
		boolean nonTaxableItemFound = false;
		if (getTransactionTotals() != null && getTransactionTotals().getTaxInformationContainer() != null) {
			TaxInformationIfc[] taxInfo = getTransactionTotals().getTaxInformationContainer().getTaxInformation();
			if (taxInfo != null) {
				for (int i = 0; i < taxInfo.length; i++) {
					if (taxInfo[i].getTaxMode() == TaxConstantsIfc.TAX_MODE_NON_TAXABLE
							|| taxInfo[i].getTaxMode() == TaxConstantsIfc.TAX_MODE_EXEMPT
							|| taxInfo[i].getTaxMode() == TaxConstantsIfc.TAX_MODE_TOGGLE_OFF) {
						nonTaxableItemFound = true;
					} else {
						taxableItemFound = true;
					}
				}
			}
		}

		boolean taxable = true; // default
		if (!taxableItemFound && nonTaxableItemFound) {
			taxable = false;
		}
		return taxable;
	}

	/**
	 * Method to default display string function.
	 *
	 * @return String representation of object
	 */
	public String toString() {
		// set result string
		StringBuilder strResult = Util.classToStringHeader("AbstractTenderableTransaction", null, hashCode());
		strResult.append(super.toString()).append(Util.formatToStringEntry("Tender line items", getTenderLineItems()))
				.append(Util.formatToStringEntry("Customer", getCustomer()))
				.append(Util.formatToStringEntry("IRSCustomer", getIRSCustomer()))
				.append(Util.formatToStringEntry("Transaction totals", getTransactionTotals()))
				.append(Util.formatToStringEntry("UniqueID", getUniqueID()));
		// pass back result
		return (strResult.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * oracle.retail.stores.domain.transaction.Transaction#equals(java.lang.
	 * Object)
	 */
	@Override
	public boolean equals(Object obj) {
		boolean equal = false;

		// If it's a AbstractTenderableTransaction, compare its attributes
		if (obj instanceof AbstractTenderableTransaction) {
			// downcast the input object
			AbstractTenderableTransaction c = (AbstractTenderableTransaction) obj;
			if (!super.equals(obj)) {
				equal = false;
			} else if (!Util.isObjectEqual(tenderLineItemsVector, c.getTenderLineItemsVector())) {
				equal = false;
			} else if (!Util.isObjectEqual(totals, getTransactionTotals())) {
				equal = false;
			} else if (!Util.isObjectEqual(customer, c.getCustomer())) {
				equal = false;
			} else if (!Util.isObjectEqual(irsCustomer, c.getIRSCustomer())) {
				equal = false;
			} else if (!Util.isObjectEqual(uniqueID, c.getUniqueID())) {
				equal = false;
			} else {
				equal = true;
			}
		}

		return (equal);
	}

	/**
	 * Retrieves id of customer associated with transaction.
	 */

	public String getCustomerId() {
		return customerId;
	}

	/**
	 * Sets id of customer associated with transaction.
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public CustomerIfc getTicCustomer() {
		return ticCustomer;
	}

	public void setTicCustomer(CustomerIfc ticCustomer) {
		this.ticCustomer = ticCustomer;
		if (this.customer == null)
			this.customer = this.ticCustomer;
	}
}
