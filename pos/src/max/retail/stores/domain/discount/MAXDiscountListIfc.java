/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Nov 07, 2016		Mansi Goel		Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.domain.discount;

import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import oracle.retail.stores.domain.discount.DiscountListIfc;

public interface MAXDiscountListIfc extends DiscountListIfc {

	public MAXDiscountListEntry getMAXEntry(String id);

	// ---------------------------------------------------------------------
	/**
	 * Returns a boolean indicating whether the argument can be used to satisfy
	 * the criteria maintained in this list.
	 * 
	 * @return boolean true if the item can be used, false otherwise
	 **/
	// ---------------------------------------------------------------------
	public boolean uses(MAXSaleReturnLineItem item, boolean flag);

}