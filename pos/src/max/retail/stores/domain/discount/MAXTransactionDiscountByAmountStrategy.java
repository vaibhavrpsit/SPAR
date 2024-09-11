/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *
 * 	Rev	1.0	    Apr 20, 2017		Hitesh dua		intial revision
 *  bug:unexpected error while redeeming capillary ABS type coupon. 
 *
 ********************************************************************************/

package max.retail.stores.domain.discount;

import java.util.HashMap;

import oracle.retail.stores.domain.discount.TransactionDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByAmountStrategy;

public class MAXTransactionDiscountByAmountStrategy extends TransactionDiscountByAmountStrategy
		implements TransactionDiscountByAmountIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected HashMap capillaryCoupon = new HashMap();
	
	public HashMap getCapillaryCoupon() {
		return capillaryCoupon;
	}

	public void setCapillaryCoupon(HashMap capillaryCoupon) {
		this.capillaryCoupon = capillaryCoupon;
	}
	/*
	public Object clone() {
		MAXTransactionDiscountByAmountStrategy clone = new MAXTransactionDiscountByAmountStrategy();
		setCloneAttributes(clone);
		clone.setDiscountEmployee(this.discountEmployee);
		return clone;
	}
	
	*/
	public Object clone()
	    {
	        TransactionDiscountByAmountIfc
	            newClass = new MAXTransactionDiscountByAmountStrategy();
	        setCloneAttributes(newClass);
	        return newClass;
	    }
}
