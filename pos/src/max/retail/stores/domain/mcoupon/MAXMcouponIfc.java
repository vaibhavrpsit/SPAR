/********************************************************************************
 *   
 *	Copyright (c) 2015  MAX India pvt Ltd    All Rights Reserved.
 *	
 *	Rev	1.0 	11-May-2017		Ashish Yadav			Changes for M-Coupon Issuance FES
 *
 ********************************************************************************/
package max.retail.stores.domain.mcoupon;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

public interface MAXMcouponIfc extends EYSDomainIfc{
	
	public String getCouponNumber();

	public void setCouponNumber(String couponNumber);
	
	public String getCouponDescription();

	
	public void setCouponDescription(String couponDescription);
	
	public String getValidTill();
	
	public void setValidTill(String validTill);
	
	
	
}
