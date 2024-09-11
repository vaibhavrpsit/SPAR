/*===========================================================================
 * Copyright (c) 2008, 2009, Oracle and/or its affiliates. 
 * All rights reserved. 
 * ===========================================================================
 * $Log:
 *
 *
 * ===========================================================================
 */
/**
 *
 */
package max.retail.stores.domain.lineitem;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

/**
 * Interface for {@link MaximumRetailPriceChange} Domain object which exposes
 * methods
 *
 *
 * @author vichandr
 * @since 12.0.9IN
 *
 */
public interface MAXMaximumRetailPriceChangeIfc extends EYSDomainIfc, Comparable {

	public static final String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/1 $";

	public static final String PICK_ONE_MRP = "PickOneMRP";

	/**
	 * Returns the MaximumRetailPrice
	 *
	 * @return
	 */
	public CurrencyIfc getMaximumRetailPrice();

	/**
	 * Sets the maximumRetailPrice
	 *
	 * @param maximumRetailPrice
	 */
	public void setMaximumRetailPrice(CurrencyIfc maximumRetailPrice);

	/**
	 * Returns the ActivationDate
	 *
	 * @return EYSDate
	 */
	public EYSDate getActivationDate();

	/**
	 * Returns the InactivationDate
	 *
	 * @return EYSDate
	 */
	public EYSDate getInActivationDate();

	/**
	 * Returns the InactivationDate
	 *
	 * @return String
	 */
	public String getItemId();

	/**
	 * Returns the RetailStoreId
	 *
	 * @return String
	 */
	public String getRetailStoreId();

	/**
	 * Returns if the MRP Active.
	 *
	 * @return boolean
	 */
	public boolean isActive();

	/**
	 * Returns if the MRP Primary.
	 *
	 * @return boolean
	 */
	public boolean isPrimary();

	/**
	 * Sets the ActivationDate
	 *
	 * @param EYSDate
	 */
	public void setActivationDate(EYSDate date);

	/**
	 * Sets the ActivationDate
	 *
	 * @param EYSDate
	 */
	public void setInActivationDate(EYSDate date);

	/**
	 * Sets the MRP Primary.
	 *
	 * @param primary
	 */
	public void setPrimary(boolean active);

	/**
	 * Sets the MRP Primary.
	 *
	 * @param active
	 */
	public void setActive(boolean active);

	/**
	 * Sets the RetailStoreId.
	 *
	 * @param retailStoreId
	 *
	 */
	public void setRetailStoreId(String retailStoreId);

	/**
	 * Sets the Item Id.
	 *
	 * @return String
	 */
	public void setItemId(String itemId);
	
	public CurrencyIfc getRetailSellingPrice();

	/**
	 * @param retailSellingPrice
	 *            CurrencyIfc
	 */
	public void setRetailSellingPrice(CurrencyIfc retailSellingPrice);

}
