/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/lineitem/MAXItemPriceIfc.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.1  Mar 01, 2017    Nitika Arora    Changes for printing the csp mrp difference on discount column of receipt.
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.lineitem;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;

public interface MAXItemPriceIfc extends ItemPriceIfc {
	/**
	 * Author: Dipak Goit
	 * 
	 */
	/* Changes for implementation of Capillary Coupon FES */

	/**
	 * @return Returns the permanentSellingPrice.
	 */
	// public boolean isDiscountedPriceModified();
	// public void setDiscountedPriceModified(boolean discountedPriceModified);
	public CurrencyIfc getReturnItemDiscountCardAmount();

	public void setReturnItemDiscountCardAmount(CurrencyIfc returnItemDiscountCardAmount);

	public CurrencyIfc getItemDiscountCardAmount();

	public void setItemDiscountCardAmount(CurrencyIfc itemDiscountCardAmount);

	public CurrencyIfc getSoldMRP();

	public void setSoldMRP(CurrencyIfc soldMRP);
	/**
	 * Adds a Promotion Line Item to the Sale Return Line Item
	 * 
	 * @param promotionLineItem
	 */
	

	/**
	 * set the calculated value for discount.
	 * 
	 * @param discountAmount
	 * 
	 */
	public void setDiscountAmount(CurrencyIfc discountAmount);
	
	/**
	 * @return  
	 * get the calculated discount amount to be printed on receipt
	 */
	public CurrencyIfc getDiscountAmount();
}
