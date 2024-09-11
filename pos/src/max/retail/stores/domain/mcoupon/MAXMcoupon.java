/********************************************************************************
 *   
 *	Copyright (c) 2015  MAX India pvt Ltd    All Rights Reserved.
 *	
 *	Rev	1.0 	11-May-2017		Ashish Yadav			Changes for M-Coupon Issuance FES
 *
 ********************************************************************************/
package max.retail.stores.domain.mcoupon;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class MAXMcoupon implements MAXMcouponIfc {

	private static final long serialVersionUID = 1L;

	
	private String couponNumber;
	private String couponDescription;
	private String validTill;

	
	@Override
	public Object clone() {
		MAXMcoupon c = new MAXMcoupon();

		setCloneAttributes(c);

		return (c);
	}

	public void setCloneAttributes(MAXMcoupon newClass) {
		if (this.couponNumber != null) {
			newClass.setCouponNumber(this.couponNumber);
			newClass.setCouponDescription(this.couponDescription);
			newClass.setValidTill(this.validTill);
					
		}

	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append(couponNumber);
		builder.append(couponDescription);
		builder.append(validTill);
		
		
		return builder.toString();
	}

	
	/**
	 * @return the couponNumber
	 */
	public String getCouponNumber() {
		return couponNumber;
	}

	/**
	 * @param couponNumber the couponNumber to set
	 */
	public void setCouponNumber(String couponNumber) {
		this.couponNumber = couponNumber;
	}

	/**
	 * @return the couponDescription
	 */
	public String getCouponDescription() {
		return couponDescription;
	}

	/**
	 * @param couponDescription the couponDescription to set
	 */
	public void setCouponDescription(String couponDescription) {
		this.couponDescription = couponDescription;
	}

	/**
	 * @return the validTill
	 */
	public String getValidTill() {
		return validTill;
	}

	/**
	 * @param validTill the validTill to set
	 */
	public void setValidTill(String validTill) {
		this.validTill = validTill;
	}
	
	
	
	

}
