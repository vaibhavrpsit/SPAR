/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2016-2017 MAXHyperMarkets, Inc.    All Rights Reserved.
  Rev 1.1	Nadia Arora		20 Feb,2017		MMRP Changes
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.lineitem;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This class represents the MaximumRetailPriceChange for an Item.
 *
 * @author vichandr
 * @since: 12.0.9IN
 * @version:
 *
 */
public class MAXMaximumRetailPriceChange implements MAXMaximumRetailPriceChangeIfc {

	/**
	 * This id is used to tell the compiler not to generate a new
	 * serialVersionUID.
	 */
	static final long serialVersionUID = 3374313771141421493L;

	/** MaximumRetailPrice for the item */
	protected CurrencyIfc maximumRetailPrice;

	/** Flag which indicates whether the MRP is primary or not */
	protected boolean primary;

	/** Activation Date of the MRP */
	protected EYSDate activationDate;

	/** Inactivation Date of the MRP */
	protected EYSDate inActivationDate;

	/** ItemId */
	protected String itemId;

	/** RetailStoreId */
	protected String retailStoreId;

	/** Inactive Flag */
	protected boolean active;

	protected CurrencyIfc retailSellingPrice;
	/**
	 * Sorts MaximumRetailPriceChange by Primary and then by activation date.
	 *
	 * @param o
	 *            the other MaximumRetailPriceChange to compare against.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {

		if (o != null) {
			MAXMaximumRetailPriceChangeIfc other = (MAXMaximumRetailPriceChangeIfc) o;
			int result = 0;
			if (other.isPrimary()) {
				// Sort by Primary
				result = 1;
			}
			if (result == 0 && !this.primary) {
				if (other.getActivationDate() == null) {
					result = -1;
				} else if (activationDate == null) {
					result = 1;
				} else {
					// Sort by Activation Date
					result = other.getActivationDate().compareTo(activationDate);
				}
				if (result == 0) {
					result = other.getMaximumRetailPrice().compareTo(this.getMaximumRetailPrice());
				}
			}
			return result;
		}
		return 1;
	}

	/**
	 *
	 * @return
	 */
	public EYSDate getActivationDate() {
		return this.activationDate;
	}

	/**
	 *
	 * @param activationDate
	 */

	public void setActivationDate(EYSDate activationDate) {
		this.activationDate = activationDate;
	}

	/**
	 *
	 * @return
	 */

	public EYSDate getInActivationDate() {
		return this.inActivationDate;
	}

	/**
	 *
	 * @param inactivationDate
	 */
	public void setInActivationDate(EYSDate inactivationDate) {
		this.inActivationDate = inactivationDate;
	}

	/**
	 *
	 * @return
	 */
	public String getItemId() {
		return this.itemId;
	}

	/**
	 *
	 * @param itemId
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	/**
	 *
	 */
	public CurrencyIfc getMaximumRetailPrice() {
		return this.maximumRetailPrice;
	}

	/**
	 *
	 */
	public void setMaximumRetailPrice(CurrencyIfc maximumRetailPrice) {
		this.maximumRetailPrice = maximumRetailPrice;
	}

	/**
	 *
	 * @return
	 */
	public boolean isPrimary() {
		return this.primary;
	}

	/**
	 *
	 * @param primary
	 */
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	/**
	 *
	 * @return
	 */
	public String getRetailStoreId() {
		return this.retailStoreId;
	}

	/**
	 *
	 * @param retailStoreID
	 */
	public void setRetailStoreId(String retailStoreID) {
		this.retailStoreId = retailStoreID;
	}

	/**
	 *
	 * @return
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 *
	 * @param active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 *
	 */
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	/**
	 *
	 */
	public Object clone() {
		MAXMaximumRetailPriceChangeIfc newItem = new MAXMaximumRetailPriceChange();
		setCloneAttributes(newItem);
		return newItem;
	}

	/**
	 *
	 * @param newItem
	 */
	private void setCloneAttributes(MAXMaximumRetailPriceChangeIfc newItem) {
		newItem.setActivationDate(this.getActivationDate());
		newItem.setActive(this.isActive());
		newItem.setInActivationDate(this.getInActivationDate());
		newItem.setItemId(this.getItemId());
		newItem.setMaximumRetailPrice(this.getMaximumRetailPrice());
		newItem.setPrimary(this.isPrimary());
		newItem.setRetailStoreId(this.getRetailStoreId());
		newItem.setRetailSellingPrice(retailSellingPrice);
	}

	/**
	 *
	 */
	public String toString() {
		// build result string
		StringBuilder strResult = Util.classToStringHeader("MaximumRetailPriceChange", revisionNumber, hashCode());
		// add attributes to string
		strResult.append(Util.formatToStringEntry("maximumRetailPrice", getMaximumRetailPrice()));

		strResult.append(Util.formatToStringEntry("itemId", this.getItemId()));

		if (this.getActivationDate() != null) {
			strResult.append(Util.formatToStringEntry("ActivationDate", this.getActivationDate()));
		}
		if (this.getInActivationDate() != null) {
			strResult.append(Util.formatToStringEntry("InactivationDate", this.getInActivationDate()));
		}
		strResult.append(Util.formatToStringEntry("Active", this.isActive()));
		strResult.append(Util.formatToStringEntry("Primary", this.isPrimary()));
		strResult.append(Util.formatToStringEntry("RetailStoreId", this.getRetailStoreId()));
		// pass back result
		return (strResult.toString());
	}
	
	public CurrencyIfc getRetailSellingPrice() {
		return retailSellingPrice;
	}

	/**
	 * @param retailSellingPrice
	 *            CurrencyIfc
	 */
	public void setRetailSellingPrice(CurrencyIfc retailSellingPrice) {
		this.retailSellingPrice = retailSellingPrice;
	}

}
