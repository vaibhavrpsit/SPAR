/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved 
*  	Rev 1.7 	May 14, 2024	Kamlesh Pant		Store Credit OTP:
*	Rev	1.6		sep 22, 2022	Kamlesh Pant   		CapLimit Enforcement for Liquor
*	Rev 1.5     Sep 02, 2020	Kumar Vaibhav		Pinelabs Integration
*	Rev 1.4     May 11, 2017	Ashish Yadav		Changes for M-Coupon Issuance FES
*   Rev 1.3     May 04, 2017	Kritica Agarwal     GST Changes
*	Rev 1.2     Dec 28, 2016	Ashish Yadav		Changes for Online points redemption FES
*	Rev 1.1     Oct 19, 2016	Mansi Goel			Changes for Customer FES
* 	Rev 1.0		Aug 26,2016		Nitesh Kumar		changes for code merging 
* ===========================================================================
*/
package max.retail.stores.domain.transaction;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import max.retail.stores.domain.bakery.MAXBakeryItemIfc;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.discountCoupon.MAXDiscountCouponIfc;
import max.retail.stores.domain.gstin.MAXGSTINValidationResponseIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.mcoupon.MAXMcouponIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;

public abstract interface MAXSaleReturnTransactionIfc extends SaleReturnTransactionIfc
{
	/// for the tic customer addition akhilesh START

	public void setMAXTICCustomer(MAXCustomerIfc ticcustomer);

	public MAXCustomerIfc getMAXTICCustomer();
	
    //public void MAXSaleReturnTransaction();

	public boolean isTicCustomerVisibleFlag();

	public void setTicCustomerVisibleFlag(boolean ticCustomerVisibleFlag);

	// added by atul shukla for saving into transaction 
	
	public String getEmployeeCompanyName();
	public void setEmployeeCompanyName(String companyName);
	
	
	/**
	 * 
	 * Rev 1.0 changes start here
	 */
	public void clearEmployeeDiscount(TransactionDiscountStrategyIfc tds);// Jyoti

	/**
	 * 
	 * Rev 1.0 changes end here
	 */
	// izhar
	public SaleReturnLineItemIfc addPLUItem(PLUItemIfc pItem, BigDecimal qty, boolean idApplyBestDeal);

	public boolean itemsSellingPriceExceedsMRP();

	// Rev 1.2 changes start here
	public String getDiscountEmployeeRole();

	public void setDiscountEmployeeRole(String discountEmployeeRole);

	public String getDiscountEmployeeName();

	public void setDiscountEmployeeName(String discountEmployeeName);

	public String getDiscountEmployeeLocation();

	public void setDiscountEmployeeLocation(String discountEmployeeLocation);

	public boolean isTaxTypeLegal();

	public void setTaxTypeLegal(boolean taxTypeLegal);

	// Rev 1.2 changes end here
	public void replaceLineItemWithoutBestDeal(AbstractTransactionLineItemIfc lineItem, int index);

	/** Changes for Rev 1.3 : Starts **/
	public String getEmpDiscountAvailLimit();

	public void setEmpDiscountAvailLimit(String empDiscountAvailLimit);

	/** Changes for Rev 1.3 : Ends **/
	/* added by Dipak Goit */
	/** implementation of Capillary Coupon Discount */

	public Vector getCapillaryCouponsApplied();

	public void addCapillaryCouponsApplied(MAXDiscountCouponIfc discountCoupon);

	public void removeCapillaryCouponsApplied();

	public int getNthCoupon();

	public void setNthCoupon(int couponCount);

	public void setItemLevelDiscount(boolean isItemLevelDiscount);

	public boolean isItemLevelDiscount();

	/** end of the implementation of Capillary Coupon Discount */

	// Changes by Gaurav : Start
	public boolean isSendTransaction();

	public void setSendTransaction(boolean flag);

	public int getItemLevelCouponCount();

	public void setItemLevelCouponCount(int itemLevelCouponCount);

	//Changes for Rev 1.1 : Starts
	public CustomerIfc getTicCustomer();

	public void setTicCustomer(CustomerIfc ticCustomer);
	//Changes for Rev 1.1 : Ends
	// Changes starts for Rev 1.2 (Ashish : Online points redemption)
	public String getCustomerId();

	public void setCustomerId(String paramString);
	
	
	
	// Changes ends for Rev 1.2 (Ashish : Online points redemption)
	
	// public HashMap getManagerOverrideMap() ;
	
	 //public void setManagerOverrideMap1(HashMap managerOverrideMap);
	 
	
	
	//Change for Rev 1.3 : Starts
	public Map<String, String> getTaxCode();
	public void setTaxCode(Map<String, String> taxCode);
	public HashMap<String, String> getStates();
	public void setStates(HashMap<String, String> states);
	public String getHomeStateCode();
	public void setHomeStateCode(String homeStateCode);
	public String getToState();
	public void setToState(String toState);
	public String getHomeState();
	public void setHomeState(String homeState);
	public boolean isIgstApplicable();
	public void setIgstApplicable(boolean igstApplicable);
	public boolean isGstEnable();
	public void setGstEnable(boolean gstEnable);
	public boolean isDeliverytrnx();
	public void setDeliverytrnx(boolean deliverytrnx);
	public boolean isCaptureCustomer();
	public void setCaptureCustomer(boolean captureCustomer);
	//Change for Rev 1.3 : Ends
// Changes for Rev 1.4 Start

	public List getDeliveryItems();
	public void addDeliveryItems(MAXSaleReturnLineItemIfc deliveryItem);

	// Changes for Rev 1.3 end
	/*	changes for REV 1.3 started */
	
	public ArrayList<MAXMcouponIfc> getMcouponList();
	
	public void setMcouponList(ArrayList<MAXMcouponIfc> mcouponList);
	/*changes for REV 1.4 end */
 public Vector <MAXBakeryItemIfc> getScansheetLineItemsVector();
	// Changes ends for Rev 1.2 (Ashish : Online points redemption)
/*	public ArrayList getPaytmResponse();
	
	void setPaytmResponse(MAXPaytmResponse response);
*/
	public String getPanNumber();
	public void setPanNumber(String panNumber) ;
	
	public String getForm60IDNumber();
  public void setForm60IDNumber(String idnumber) ;
  
	public String getPassportNumber();

	public void setPassportNumber(String passportNum) ;
	public String getVisaNumber();

	public void setVisaNumber(String visaNum) ;
	public String getITRAckNumber();

	public void setITRAckNumber(String ackNum) ;
	
	public BigDecimal getEasyBuyTotals();

	public void setEasyBuyTotals(BigDecimal easyBuyTotals) ;
	
	public String getEReceiptOTP() ;

	public void setEReceiptOTP(String eReceiptOTP);
	
	//Changes starts for Rev 1.5 (Vaibhav : PineLab)
	public String getEdcType();
	public void setEdcType(String edcType);
	//Changes ends for Rev 1.5 (Vaibhav : PineLab)
	
	public boolean isSbiRewardredeemFlag();
	public void setSbiRewardredeemFlag(boolean sbiRewardredeemFlag);
	// Changes starts for Rev 1.6
	public String geteWalletTraceId(); 
	public void seteWalletTraceId(String eWalletTraceId); 
	public boolean isEWalletTenderFlag();
	public void setEWalletTenderFlag(boolean isEWalletTenderFlag);
	// Changes End for Rev 1.6

		/**
	 * Changes for Manager Override requirement
	 * 
	 * @return
	 */
	public HashMap getManagerOverrideMap();

	/**
	 * Changes for Manager Override requirement
	 * 
	 * @param managerOverrideMap
	 */
	public void setManagerOverrideMap(HashMap managerOverrideMap);

	//Changes for RTS Manager Override starts
	/**
	 * @return the rtsManagerOverride
	 */
	public boolean isRtsManagerOverride();
	
	/**
	 * @param rtsManagerOverride the rtsManagerOverride to set
	 */
	public void setRtsManagerOverride(boolean rtsManagerOverride);
	// Changes for RTS Manager Override ends
	
	//Submit Invoice
	public String getSubmitinvresponse();
	public void setSubmitinvresponse(String submitinvresponse);
	
	//changes for paytmqr
	public String getPaytmQROrderId();
	public void setPaytmQROrderId(String paytmQROrderId);

	
	//GST changes
	public void setGSTINNumber(String gstin);
	public String getGSTINNumber();
	
	public void setGstinresp(MAXGSTINValidationResponseIfc response);
	
	public MAXGSTINValidationResponseIfc getGstinresp();
	
	public void setStoreGSTINNumber(String gstin);
	public String getStoreGSTINNumber();
	
	public void setReceiptData(byte[] readerData);
	
	public byte[] getReceiptData();
	 
	//REV 1.6 Starts :Changes by kamlesh for Liquor Starts
	
	public float getBeertot();
	public void setBeertot(float beertot);
	
	public float getliquortot();
	public void setliquortot(float liquorTotal);
	
	public float getInLiqtot();
	public void setInLiqtot(float InLiqtot);
	
	public float getfrnLiqtot();
	public void setfrnLiqtot(float frnLiqtot);
	
	//REV 1.6 Ends :Changes for Liquor Ends.
	
	//Rev 1.7 Starts
	public String getScOtp();
	public void setScOtp(String scOtp);
	public String getCustOgMobile() ;
	public void setCustOgMobile(String custOgMobile);
	public String getOgTransaction();
	public void setOgTransaction(String ogTransaction);
	public String getCustMobileforOTP() ;
	public void setCustMobileforOTP(String custMobileforOTP);
	 //Rev 1.22 Ends
	public boolean isEmployeeOtpValidated();
	public void setEmployeeOtpValidated(boolean employeeOtpValidated);
	public String getEmplyoeeDicsOtp() ;
	public void setEmplyoeeDicsOtp(String EmplyoeeDicsOtp);
	public String getLocale();
	public void setLocale(String value);
	public String getEmpID();
    public void setEmpID(String EmpID);
	public String getEmpAvailableAmt();
	public void setEmpAvailableAmt(String empAvailableAmt);

}
