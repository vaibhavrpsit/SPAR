/* *****************************************************************************************
 * Copyright (c) 2015   Lifestyle India Pvt. Ltd.All Rights Reserved.
 * Upgraded to MAX ORPOS 14.0.1 from Lifestyle ORPOS 12.0.9IN: AAKASH GUPTA(EYLLP):Aug-31-2015
 * 
 * *******************************************************************************************/

package max.retail.stores.domain.discount;

import java.util.HashMap;

import oracle.retail.stores.domain.discount.DiscountRuleIfc;

//----------------------------------------------------------------------------
/**
 * Interface for DiscountRule class.
 * <P>
 *
 * @version $Revision: 1.2 $
 **/
// ----------------------------------------------------------------------------
public  interface MAXDiscountRuleIfc extends DiscountRuleIfc {

	// lmg code changes for promoGV by shavinki starts here

	// --------------------------------------------------------------------------
	/**
	 * Returns the value of the promoGVDiscount flag - true indicates that its
	 * promoGV Discount
	 * <P>
	 *
	 * @return promoGVDiscount flag
	 **/
	// --------------------------------------------------------------------------
	public boolean isPromoGVDiscount();

	// --------------------------------------------------------------------------
	/**
	 * Sets the value of the promoGVDiscount flag
	 * <P>
	 *
	 * @param promoGVDiscount
	 **/
	// --------------------------------------------------------------------------
	public void setPromoGVDiscount(boolean promoGVDiscount);

	// --------------------------------------------------------------------------
	/**
	 * Returns the promoGVNumbers
	 * <P>
	 *
	 * @return promoGVNumbers
	 **/
	// --------------------------------------------------------------------------
	public HashMap getPromoGVNumbers();

	// --------------------------------------------------------------------------
	/**
	 * Sets the Promo Gift Voucher range From and To Numbers
	 * <P>
	 *
	 * @param promoGVNumbers
	 **/
	// --------------------------------------------------------------------------
	public void setPromoGVNumbers(HashMap promoGVNum);

	// --------------------------------------------------------------------------
	/**
	 * Returns the value of the promoGVRedemptionCode
	 * <P>
	 *
	 * @return promoGVRedemptionCode
	 **/
	// --------------------------------------------------------------------------
	public String getPromoGVRedemptionCode();

	// --------------------------------------------------------------------------
	/**
	 * Sets the value of the promoGVRedemptionCode
	 * <P>
	 *
	 * @param promoGVRedemptionCode
	 **/
	// --------------------------------------------------------------------------
	public void setPromoGVRedemptionCode(String promoGVRedemptionCode);

	// lmg code changes for promoGV by shavinki ends here
	/**
	 * Sets and Gets Discount card Numbers
	 */

	public HashMap getDiscountCardNumber();

	public void setDiscountCardNumber(HashMap DCNum);
	/**Rev 1.5 change start 18/10/2013 Priyanka Singh for three items wehere scanned and
	 * discount voucher is redeemed. When they done the return only one item returned
	 * and store credit generated with more value because the discount voucher amount
	 * is added to store credit. */
	public boolean isDiscountCardRedeemptionFlag();
	public void setDiscountCardRedeemptionFlag(boolean discountCardRedeemptionFlag);
	/*Rev 1.5 change End*/
	//geetika for capillary coupons

	public HashMap getCapillaryCoupon();

	public void setCapillaryCoupon(HashMap capillaryCoupon);
}