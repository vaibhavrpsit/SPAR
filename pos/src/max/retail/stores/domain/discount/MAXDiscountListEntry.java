/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev 1.1		Mar 29, 2016		Mansi Goel		Changes to resolve Airthmetic Exception when same source is scanned twice where any qty is selected
 *	Rev	1.0 	Nov 07, 2016		Mansi Goel		Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.domain.discount;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountItemIfc;
import oracle.retail.stores.domain.discount.DiscountListEntry;
import oracle.retail.stores.foundation.utility.Util;

public class MAXDiscountListEntry extends DiscountListEntry {

	private static final long serialVersionUID = 1748121065441957742L;
	int quantityRequired;
	List<DiscountItemIfc> discountItems;
	List<DiscountItemIfc> discountAltItems;
	CurrencyIfc regularPrice;
	CurrencyIfc amount;
	CurrencyIfc amountRequired;

	public MAXDiscountListEntry() {
		this(0);
	}

	public MAXDiscountListEntry(int quantityRequired) {
		this.quantityRequired = quantityRequired;
		discountItems = new ArrayList<DiscountItemIfc>(quantityRequired);
		discountAltItems = new ArrayList<DiscountItemIfc>();
	}

	public MAXDiscountListEntry(CurrencyIfc thresholdValue) {
		amountRequired = thresholdValue;
	}

	public MAXDiscountListEntry(int quantityRequired, CurrencyIfc regularPrice) {
		this.quantityRequired = quantityRequired;
		this.regularPrice = regularPrice;
	}

	// --------------------------------------------------------------------------
	/**
	 * Tests whether the target quantity has been achieved for this entry.
	 * 
	 * @return boolean true if the String is associated with an entry and has
	 *         been incremented the required number of times, false otherwise
	 **/
	// --------------------------------------------------------------------------
	public boolean quantitySatisfied() {
		return (discountItems.size() >= quantityRequired);
	}

	public boolean quantitySatisfiedForKGItem() {
		return (discountItems.size() >= 1);
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns the quantity attained for this entry.
	 * 
	 * @return int indicating the number of times the quantity has been
	 *         incremented
	 **/
	// --------------------------------------------------------------------------
	public int getQuantity() {
		return discountItems.size();
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns the quantity required for this entry.
	 * 
	 * @return int indicating the number of times the quantity must be
	 *         incremented to satisfy the criteria for this entry
	 **/
	// --------------------------------------------------------------------------
	public int getQuantityRequired() {
		return quantityRequired;
	}

	// --------------------------------------------------------------------------
	/**
	 * Sets the quantity required for this entry.
	 * 
	 * @param int value indicating the number of times the quantity must be
	 *        incremented to satisfy the criteria for this entry
	 **/
	// --------------------------------------------------------------------------
	public void setQuantityRequired(int value) {
		quantityRequired = value;
	}

	// --------------------------------------------------------------------------
	/**
	 * Tests whether the threshold amount has been achieved for this entry.
	 * 
	 * @return boolean true if the String is associated with an entry and
	 *         amountCounted has been incremented to be greater than or equal to
	 *         the threshold amoumtRequired, false otherwise
	 **/
	// --------------------------------------------------------------------------
	public boolean amountSatisfied() {
		return (amount.compareTo(amountRequired) >= 0);
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns the currency amount counted for this entry.
	 * 
	 * @return CurrencyIfc indicating the current value counted for this entry
	 **/
	// --------------------------------------------------------------------------
	public CurrencyIfc getAmount() {
		return (CurrencyIfc) amount.clone();
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns the threshold amount for this entry.
	 * 
	 * @return CurrencyIfc indicating the value the amount must reach to satisfy
	 *         the criteria for this entry
	 **/
	// --------------------------------------------------------------------------
	public CurrencyIfc getAmountRequired() {
		return (CurrencyIfc) amountRequired.clone();
	}

	// --------------------------------------------------------------------------
	/**
	 * Sets the threshold amount for this entry.
	 * 
	 * @param CurrencyIfc
	 *            value indicating the value the amount must reach to satisfy
	 *            the criteria for this entry
	 **/
	// --------------------------------------------------------------------------
	public void setAmountRequired(CurrencyIfc value) {
		amountRequired = value;
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns the price required for this entry.
	 * 
	 * @return CurrencyIfc
	 **/
	// --------------------------------------------------------------------------
	public CurrencyIfc getRegularPrice() {
		return regularPrice;
	}

	// --------------------------------------------------------------------------
	/**
	 * Sets the price required for this entry.
	 * 
	 * @param CurrencyIfc
	 **/
	// --------------------------------------------------------------------------
	public void setRegularPrice(CurrencyIfc regularPrice) {
		this.regularPrice = regularPrice;
	}

	// --------------------------------------------------------------------------
	/**
	 * Increments the quantity attained for this entry by one.
	 **/
	// --------------------------------------------------------------------------
	public void incrementQuantity(DiscountItemIfc item) {
		discountItems.add(item);
	}

	// --------------------------------------------------------------------------
	/**
	 * Adds a currency value to the amount counted for this this entry.
	 * 
	 * @param amountToAdd
	 *            - the currency value to add to the amount counted
	 **/
	// --------------------------------------------------------------------------
	public void addToAmount(CurrencyIfc amountToAdd) {
		amount = amount.add(amountToAdd);
	}

	// --------------------------------------------------------------------------
	/**
	 * Resets the quantity attained for this entry to zero.
	 **/
	// --------------------------------------------------------------------------
	public void resetQuantity() {
		discountItems.clear();
	}

	// --------------------------------------------------------------------------
	/**
	 * Resets the amount counted for this entry to default currency value.
	 **/
	// --------------------------------------------------------------------------
	public void resetAmount() {
		amount = DomainGateway.getBaseCurrencyInstance();
	}

	@Override
	public CurrencyIfc getTotalPrice() {
		CurrencyIfc totalSellingPrice = DomainGateway.getBaseCurrencyInstance();
		// iterate over the entries and sum the amount
		for (DiscountItemIfc item : discountItems) {
			totalSellingPrice = totalSellingPrice.add(item.getExtendedSellingPrice());
		}
		return totalSellingPrice;
	}

	// ---------------------------------------------------------------------
	/**
	 * Determine if two objects are identical.
	 * 
	 * @param obj
	 *            object to compare with
	 * @return true if the objects are identical, false otherwise
	 **/
	// ---------------------------------------------------------------------
	public boolean equals(Object obj) {
		boolean equal = false;

		if (obj instanceof MAXDiscountListEntry) {
			MAXDiscountListEntry entry = (MAXDiscountListEntry) obj;

			if (discountItems.size() == entry.discountItems.size() && quantityRequired == entry.quantityRequired
					&& Util.isObjectEqual(regularPrice, entry.regularPrice) && Util.isObjectEqual(amount, entry.amount)
					&& Util.isObjectEqual(amountRequired, entry.amountRequired)) {
				equal = true;
			}

		}

		return equal;
	}

	// ---------------------------------------------------------------------
	/**
	 * Clones the list.
	 * 
	 * @return new DiscountList Object
	 **/
	// ---------------------------------------------------------------------
	public Object clone() {
		MAXDiscountListEntry newEntry = new MAXDiscountListEntry();

		newEntry.discountItems = (List) ((ArrayList) discountItems).clone();
		newEntry.quantityRequired = quantityRequired;
		if (regularPrice != null) {
			newEntry.regularPrice = (CurrencyIfc) regularPrice.clone();
		}
		if (amount != null) {
			newEntry.amount = (CurrencyIfc) amount.clone();
		}
		if (amountRequired != null) {
			newEntry.amountRequired = (CurrencyIfc) amountRequired.clone();
		}
		return newEntry;
	}

	// ---------------------------------------------------------------------
	/**
	 * Method to default display string function.
	 * <P>
	 * 
	 * @return String representation of object
	 **/
	// ---------------------------------------------------------------------
	public String toString() {
		// result string
		StringBuffer strResult = new StringBuffer("Class:  ");
		strResult.append(getClass().getName() + " (Revision ").append(revisionNumber).append(") @").append(hashCode())
				.append(Util.EOL).append("\tquantity: " + discountItems.size() + Util.EOL)
				.append("\tquantityRequired: " + quantityRequired + Util.EOL)
				.append("\tregularPrice: " + regularPrice + Util.EOL).append("\tamount: " + amount + Util.EOL)
				.append("\tamountRequired: " + amountRequired + Util.EOL);

		return (strResult.toString());
	}

	//Changes for Rev 1.1 : Starts
	public void incrementAltQuantity(DiscountItemIfc item) {
		discountAltItems.add(item);
		if (((discountAltItems.size() + discountItems.size()) % quantityRequired) == 0) {
			discountItems.addAll(discountAltItems);
			discountAltItems.clear();
		}
	}
	//Changes for Rev 1.1 : Ends
}
