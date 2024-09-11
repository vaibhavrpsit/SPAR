/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Nov 30, 2016		Mansi Goel		Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.domain.discount;

import java.util.HashMap;

import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;

public interface MAXItemDiscountStrategyIfc extends ItemDiscountStrategyIfc {
	
	public HashMap getCapillaryCoupon();
	public void setCapillaryCoupon(HashMap capillaryCoupon);

}
