/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
   Rev 1.0	Izhar		29/05/2013		Discount Rule
 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.comparators;
//package com.extendyourstore.domain.comparators;

//java imports
import java.io.Serializable;
import java.util.Comparator;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;

//--------------------------------------------------------------------------
/**
 * Comparison class used to sort discount sources and targets. Sorts a list of
 * SaleReturnLineItems in descending order by extended discounted selling price.
 * 
 * @version $Revision: 1.1 $
 **/
// --------------------------------------------------------------------------
public class MAXLineItemPriceAscending implements Comparator, Serializable {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = 3902060199122558460L;

	/**
	 * revision number supplied by source-code control system
	 **/
	public static final String revisionNumber = "$Revision: 1.1 $";

	private static Comparator instance = new MAXLineItemPriceAscending();

	// ---------------------------------------------------------------------
	/**
	 * Constructs the singleton.
	 **/
	// ---------------------------------------------------------------------
	private MAXLineItemPriceAscending() {

	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the singleton.
	 **/
	// ---------------------------------------------------------------------
	public static Comparator getInstance() {
		return instance;
	}

	// ---------------------------------------------------------------------
	/**
	 * Compares two sale return line items and returns -1,0 or 1 if the first
	 * item's extended discounted selling price is greater than, equal to or
	 * less than the second item's extended discounted selling price. This
	 * allows collections of line items to be sorted in descending order by
	 * final pre-tax price.
	 **/
	// ---------------------------------------------------------------------
	public int compare(Object o1, Object o2) {
		int value = 0;

		SaleReturnLineItemIfc item1 = (SaleReturnLineItemIfc) o1;
		SaleReturnLineItemIfc item2 = (SaleReturnLineItemIfc) o2;

		value = (item1.getExtendedDiscountedSellingPrice().compareTo(item2.getExtendedDiscountedSellingPrice()));

		return value;
	}

}
