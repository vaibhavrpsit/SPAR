/********************************************************************************
*   
*	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*
*	Rev	1.2 	Apr 25, 2017		Hitesh dua			change class for cloning
*   Bug: M-Coupon discount is printing at line item level discount   	
*
*	Rev 1.1     Feb 27,2017         Nitika Arora        Fix for Bug(scann the item and give item level disc and change the qty. Disc is removing).Change the clone method which was wrong.
*	Rev	1.0 	Nov 30, 2016		Mansi Goel			Changes for Discount Rule FES
*
********************************************************************************/
package max.retail.stores.domain.discount;

import java.util.HashMap;

import oracle.retail.stores.domain.discount.DiscountRule;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountStrategy;

public class MAXItemDiscountByAmountStrategy extends ItemDiscountByAmountStrategy implements MAXItemDiscountStrategyIfc {
	
	
	
	private static final long serialVersionUID = -4605769521787241538L;
	protected HashMap capillaryCoupon = new HashMap();
	
	public HashMap getCapillaryCoupon() {
		return capillaryCoupon;
	}

	public void setCapillaryCoupon(HashMap capillaryCoupon) {
		this.capillaryCoupon = capillaryCoupon;
	}
	
	public Object clone() {
		MAXItemDiscountByAmountStrategy clone = new MAXItemDiscountByAmountStrategy();
		setCloneAttributes(clone);
		return clone;
	}
	
	public void setCloneAttributes(MAXItemDiscountByAmountStrategy newClass)
    {
        super.setCloneAttributes(newClass);
        ((MAXItemDiscountByAmountStrategy)newClass).setCapillaryCoupon(capillaryCoupon);
    }
}
