/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2010 Max India Pvt Ltd.    All Rights Reserved.
 * 
 *   Rev	1.1 	Apr 20, 2017		Hitesh Dua		changes for "ABS" type Capillary coupon 
 *   Rev	1.1 	Mar 15, 2017		Hitesh Dua		Capillary coupon receipt
 *   
 *Rev 1.0 -12/06/2015 for capillary coupon discount changes to add class and subclass level in hierarchy by Mohd Arif
 * Rev 1.0 LS_FES_CouponRedemption-05-June-15 (1) 05/06/2015 Danish.Anshari
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.discountCoupon;

import java.util.ArrayList;
import java.util.List;

/**
 * @author danish.anshari
 * 
 */

public class MAXDiscountCoupon implements MAXDiscountCouponIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected List department = new ArrayList();

	protected List brand = new ArrayList();

	protected List class1 = new ArrayList();

	protected List subClass = new ArrayList();

	protected List skuslist = new ArrayList();

	protected String discountType = "";

	protected boolean discountOnDiscountedItems = false;

	protected String couponNumber = "";

	protected Double couponDiscountAmountPercent = new Double(0.0);
	protected Double couponDiscountAmountByPercent = new Double(0.0);

	protected Double minThresholdValue;

	protected double maxThresholdValue;

	protected List division = new ArrayList();

	protected List group = new ArrayList();

	protected List company = new ArrayList();
	// akanksha
	protected String reasonCode = "";

	public List getDivision() {
		return division;
	}

	public void setDivision(List division) {
		this.division = division;
	}

	public List getGroup() {
		return group;
	}

	public void setGroup(List group) {
		this.group = group;
	}

	public List getCompany() {
		return company;
	}

	public void setCompany(List company) {
		this.company = company;
	}

	public String getDiscountOn() {
		return discountOn;
	}

	public void setDiscountOn(String discountOn) {
		this.discountOn = discountOn;
	}

	protected String discountOn = "";

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	protected String campaignId = "";

	protected List couponSeriesID = new ArrayList();

	public List getCouponSeriesID() {
		return couponSeriesID;
	}

	public void setCouponSeriesID(List couponSeriesID) {
		this.couponSeriesID = couponSeriesID;
	}

	// Aakash:Start
	public List getDepartment() {
		return department;
	}

	public void setDepartment(List department) {
		this.department = department;
	}

	public List getBrand() {
		return brand;
	}

	public void setBrand(List brand) {
		this.brand = brand;
	}

	/*
	 * Start: Rev 1.0 Changes
	 */
	public List getClass1() {
		return class1;
	}

	public void setClass1(List class1) {
		this.class1 = class1;
	}

	public List getSubClass() {
		return subClass;
	}

	public void setSubClass(List subClass) {
		this.subClass = subClass;
	}
	/*
	 * End: Rev 1.0 Changes
	 */

	public String getDiscountType() {
		return discountType;
	}

	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	public boolean isDiscountOnDiscountedItems() {
		return discountOnDiscountedItems;
	}

	public void setDiscountOnDiscountedItems(boolean discountOnDiscountedItems) {
		this.discountOnDiscountedItems = discountOnDiscountedItems;
	}

	public String getCouponNumber() {
		return couponNumber;
	}

	public void setCouponNumber(String couponNumber) {
		this.couponNumber = couponNumber;
	}

	// Aakash:Start
	public Double getCouponDiscountAmountPercent() {
		return couponDiscountAmountPercent;
	}

	public void setCouponDiscountAmountPercent(Double couponDiscountAmount) {
		this.couponDiscountAmountPercent = couponDiscountAmount;
	}

	// Aakash:End
	public Double getMinThresholdValue() {
		return minThresholdValue;
	}

	public void setMinThresholdValue(Double minThresholdValue) {
		this.minThresholdValue = minThresholdValue;
	}

	// Aakash:End
	public double getMaxThresholdValue() {
		return maxThresholdValue;
	}

	public void setMaxThresholdValue(double maxThresholdValue) {
		this.maxThresholdValue = maxThresholdValue;
	}

	public MAXDiscountCoupon() {
	}

	// ---------------------------------------------------------------------
	/**
	 * Clones this object.
	 * <P>
	 * 
	 * @return cloned object
	 **/
	// ---------------------------------------------------------------------
	public Object clone() { // begin clone()
		MAXDiscountCoupon discountCoupon = new MAXDiscountCoupon();

		// set attributes in clone
		setCloneAttributes(discountCoupon);

		return ((Object) discountCoupon);
	} // end clone()

	// ---------------------------------------------------------------------
	/**
	 * Sets attributes in clone.
	 * <P>
	 * 
	 * @param newClass
	 *            new instance of class
	 **/
	// ---------------------------------------------------------------------
	protected void setCloneAttributes(MAXDiscountCoupon newClass) { // begin
		// setCloneAttributes()
		newClass.setBrand(brand);
		newClass.setCampaignId(campaignId);
		newClass.setCouponDiscountAmountPercent(couponDiscountAmountPercent);
		newClass.setCouponDiscountAmountByPerc(couponDiscountAmountByPercent);
		newClass.setCouponNumber(couponNumber);
		newClass.setCouponSeriesID(couponSeriesID);
		newClass.setDepartment(department);
		newClass.setDiscountOn(discountOn);
		newClass.setDiscountType(discountType);
		newClass.setMaxThresholdValue(maxThresholdValue);
		newClass.setMinThresholdValue(minThresholdValue);
		newClass.setCompany(company);
		newClass.setDivision(division);
		newClass.setGroup(group);
		newClass.setSubClass(subClass);
		// aakansha
		newClass.setRedeemstatus(redeemstaus);
		newClass.setReasonCode(getReasonCode());

	} // end setCloneAttributes()

	boolean redeemstaus = false;

	public void setRedeemstatus(boolean redeemstaus) {
		this.redeemstaus = redeemstaus;

	}

	public String getReasonCode() {
		return (reasonCode);
	}

	public boolean getRedeemstatus() {

		return redeemstaus;
	}

	public void setReasonCode(String value) {
		reasonCode = value;

	}

	public List getSkuList() {
		// TODO Auto-generated method stub
		return skuslist;
	}

	public void setSkuList(List skuList) {
		skuslist = skuList;

	}

	public Double getCouponDiscountAmountByPerc() {
		// TODO Auto-generated method stub
		return couponDiscountAmountByPercent;
	}

	public void setCouponDiscountAmountByPerc(Double couponDiscountAmount) {
		this.couponDiscountAmountByPercent = couponDiscountAmount;

	}

	//changes for rev 1.1  & 1.2 start 
	@Override
	public String getCouponDiscountAmountPercentString() {

		if(getDiscountType().equalsIgnoreCase("ABS"))
			return "            -"+String.format("%.2f", getCouponDiscountAmountPercent());
		return String.format("%.2f", getCouponDiscountAmountPercent())+"%";
	}

	@Override
	public String getCouponDiscountAmountByPercString() {
		if(!getCouponDiscountAmountByPerc().toString().equals("0.0"))
		return "      -"+String.format("%.2f", getCouponDiscountAmountByPerc())+"   ";
		return "";
	}
	
	//changes for rev 1.1 & 1.2 end
}