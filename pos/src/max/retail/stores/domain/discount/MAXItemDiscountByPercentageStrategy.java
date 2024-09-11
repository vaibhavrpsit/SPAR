/********************************************************************************
*   
*	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*	
*
*	Rev	1.1 	Apr 05, 2017		Hitesh dua			change class for cloning
*   Bug: M-Coupon discount is printing at line item level discount
*   
*	Rev	1.0 	Nov 30, 2016		Mansi Goel			Changes for Discount Rule FES
*
********************************************************************************/

package max.retail.stores.domain.discount;

import java.math.BigDecimal;
import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.discount.DiscountRule;
import oracle.retail.stores.domain.discount.ItemDiscountByPercentageStrategy;

public class MAXItemDiscountByPercentageStrategy extends ItemDiscountByPercentageStrategy implements
MAXItemDiscountByPercentageIfc {
	
	private static final long serialVersionUID = 1552879447639372250L;
	
	protected HashMap capillaryCoupon = new HashMap();
	
	public HashMap getCapillaryCoupon() {
		return capillaryCoupon;
	}

	public void setCapillaryCoupon(HashMap capillaryCoupon) {
		this.capillaryCoupon = capillaryCoupon;
	}
	
	public Object clone() {
		MAXItemDiscountByPercentageStrategy clone = new MAXItemDiscountByPercentageStrategy();
		setCloneAttributes(clone);
		return clone;
	}
	
	//changes for rev 1.1 
	public void setCloneAttributes(ItemDiscountByPercentageStrategy newClass)
    {
        super.setCloneAttributes(newClass);
        ((MAXItemDiscountByPercentageStrategy)newClass).setCapillaryCoupon(capillaryCoupon);
    }
	
	public CurrencyIfc calculateItemDiscount(CurrencyIfc itemPrice, BigDecimal itemQuantity) {
	   if(itemPrice !=null && itemQuantity!=null) {
		BigDecimal quantity = itemQuantity.abs();
	    if (quantity.equals(BigDecimal.ONE) || !isWhole(quantity)) {
	      discountAmount = itemPrice.multiply(this.discountRate);
	      return discountAmount;
	    } 
	    CurrencyIfc individualPrice = itemPrice.divide(quantity);
	    discountAmount = individualPrice.multiply(this.discountRate);
	   }
	    return discountAmount;
	   
	  }
	private boolean isWhole(BigDecimal quantity) {
	    BigDecimal scaledZero = BigDecimal.ZERO.setScale(quantity.scale());
	    BigDecimal[] parts = quantity.divideAndRemainder(BigDecimal.ONE);
	    boolean isWhole = scaledZero.equals(parts[1]);
	    return isWhole;
	  }
	 public CurrencyIfc calculateItemDiscount(CurrencyIfc itemPrice)
	    {
	        // If itemPrice is negative, discount amount returned is negative
	        discountAmount = itemPrice.multiply(discountRate);
	        return discountAmount;
	    }
}
