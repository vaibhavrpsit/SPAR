/** * * * * 
 *  Rev 2.0     July 03 ,2023       Kumar Vaibhav   Capillary coupon max discount validation
 *  Rev	1.1 	Mar 15, 2017		Hitesh Dua		Capillary coupon receipt 
 *
 * Rev 1.0 -12/06/2015 for capillary coupon discount changes to add class and subclass level in hierarchy by Mohd Arif
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.discountCoupon;

import java.io.Serializable;
import java.util.List;

public interface MAXDiscountCouponIfc extends Serializable {

	public Object clone();

	public List getCouponSeriesID();

	public void setCouponSeriesID(List couponSeriesID);

	public List getDepartment();

	public void setDepartment(List department);

	public List getBrand();

	public void setBrand(List brand);

	public List getClass1();

	public void setClass1(List class1);

	public List getSubClass();

	public void setSubClass(List subClass);

	public String getDiscountType();

	public void setDiscountType(String discountType);

	public boolean isDiscountOnDiscountedItems();

	public void setDiscountOnDiscountedItems(boolean discountOnDiscountedItems);

	public String getCouponNumber();

	public void setCouponNumber(String couponNumber);

	public Double getCouponDiscountAmountPercent();

	public void setCouponDiscountAmountPercent(Double couponDiscountAmount);

	public Double getMinThresholdValue();

	public void setMinThresholdValue(Double minThresholdValue);

	public double getMaxThresholdValue();

	public void setMaxThresholdValue(double maxThresholdValue);

	public String getCampaignId();

	public void setCampaignId(String campaignId);

	public String getDiscountOn();

	public void setDiscountOn(String discountOn);

	public List getDivision();

	public void setDivision(List division);

	public List getGroup();

	public void setGroup(List group);

	public List getCompany();

	public void setCompany(List company);

	public void setRedeemstatus(boolean b);

	public boolean getRedeemstatus();

	public void setReasonCode(String code);

	public String getReasonCode();

	/*
	 * Start: Rev 1.0 Changes Arif
	 */

	public List getSkuList();

	public void setSkuList(List skuList);

	public Double getCouponDiscountAmountByPerc();

	public void setCouponDiscountAmountByPerc(Double couponDiscountAmount);
	/*
	 * End: Rev 1.0 Changes Arif
	 */

	//changes for rev 1.1 start
	 public String getCouponDiscountAmountPercentString();
	
	 public String getCouponDiscountAmountByPercString();
	 
    //changes for rev 1.1 end
}
