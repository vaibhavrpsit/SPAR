/********************************************************************************
*   
*	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
*	
*
*
*	Rev	1.0 	Nov 07, 2016		Mansi Goel		Changes for Discount Rule FES	
*
********************************************************************************/
package max.retail.stores.domain.comparators;

import java.util.Comparator;

import oracle.retail.stores.domain.comparators.DiscountListEntryComparator;

public class MAXComparators {
	// ---------------------------------------------------------------------
	/**
	 * This utility class provides global access to strategies that can be used
	 * to compare and sort collections of eys domain objects. Classes in the
	 * comparators package describe object behavior only. They should not
	 * maintain any state.
	 **/
	// ---------------------------------------------------------------------
	public MAXComparators() {
	}

	public static final Comparator lineItemPriceAscending = MAXLineItemPriceAscending.getInstance();
	// ---------------------------------------------------------------------
	/**
	 * Reference to the BestDealDiscountDescending singleton.
	 * <P>
	 * This comparison class can be used to sort a collection of
	 * BestDealGroupIfcs in descending order by total discount amount.
	 **/
	// ---------------------------------------------------------------------
	public static final Comparator discountListEntryDescending = DiscountListEntryComparator.getDescendingInstance();
	// ---------------------------------------------------------------------
	/**
	 * Reference to the BestDealDiscountDescending singleton.
	 * <P>
	 * This comparison class can be used to sort a collection of
	 * BestDealGroupIfcs in descending order by total discount amount.
	 **/
	// ---------------------------------------------------------------------
	public static final Comparator discountListEntryAscending = DiscountListEntryComparator.getAscendingInstance();

}
