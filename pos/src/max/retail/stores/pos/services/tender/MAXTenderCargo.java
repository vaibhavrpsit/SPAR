/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved. \
   	Rev 1.3  May 14, 2024	Kamlesh Pant				Store Credit OTP:
   	Rev 1.2  28/May/2013	Jyoti Rawal					Change for Credit Card Functionality
  	Rev 1.1  08/May/2013	Himanshu					Change for Store Credit Functionality
	Rev 1.0  08/May/2013	Jyoti Rawal, Initial Draft: Changes for Hire Purchase Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.tender;

import java.util.HashMap;

import max.retail.stores.domain.MAXAmazonPayResponse;
import max.retail.stores.domain.MAXEWalletResponse;
import max.retail.stores.domain.MAXMobikwikResponse;
import max.retail.stores.domain.MAXOxigenResponse;
import max.retail.stores.domain.MAXPaytmResponse;
import max.retail.stores.domain.paytm.MAXPaytmQRCodeResponse;
import max.retail.stores.pos.services.tender.sbi.MAXSBIPointsRedemptionResponse;
import oracle.retail.stores.commerceservices.common.currency.CurrencyDecimal;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.pos.services.tender.TenderCargo;


public class MAXTenderCargo extends TenderCargo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4230820647521386469L;
	protected String approvalCode = null;
	protected TenderLineItemIfc tenderLineItem = null;
	protected boolean isGiftCardApproved = false;
	// system..CR 4; starts here added by atul shukla
	protected boolean creditOnlineFlow = false;
	protected HashMap responseMap = new HashMap();
	protected String phoneNumber = null;
	protected String totp = null;
	
	protected String oxigenOtp = null;
	
	protected String otpRefNum = null;
	
	protected String amazonPayMobileNumber = null;
	
	protected String eWalletMobileNumber = null;
	protected String eWalletTraceId = null;
	protected String eWalletReturnGetOtpResponse = null;
	
	protected String oxigenAppliedAmt = null;
	
	protected boolean isEWalletTenderFlag = false;

	protected String transactionID = null;
	
	//Changes for paytmqr
	protected MAXPaytmQRCodeResponse paytmQRCodeResp;
	
	//changes by shyvanshu mehra
	
	/* protected MAXEnterCustomerInfoSite entercustomerinfo; */

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	/*added by Kumar Vaibhav for Amazon Pay Barcode Integration Start*/
	protected String barcode = null;
	
   public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	/* end*/
	protected MAXPaytmResponse paytmResp;
	protected MAXMobikwikResponse mobikwikResp;
	
	protected MAXAmazonPayResponse amazonPayResp;

	public MAXAmazonPayResponse getAmazonPayResp() {
		return amazonPayResp;
	}

	public void setAmazonPayResp(MAXAmazonPayResponse amazonPayResp) {
		this.amazonPayResp = amazonPayResp;
	}

	public void setApprovalCode(String value) {
		approvalCode = value;
	}
    
    //---------------------------------------------------------------------
    /**
     * get gift card reference
     * @return GiftCardIfc
     */
    //---------------------------------------------------------------------
    public String getApprovalCode()
    {
        return approvalCode;
    }

	/**MAX Rev 1.1 Change : Start**/
	protected EYSDate stExpirtDate;
	protected  CurrencyDecimal  totalTenderAmount;
   
   public void setStoreCreditExpirtDate(EYSDate stExpirtDate)
   {
	   this.stExpirtDate=stExpirtDate;
   }
   
   public EYSDate getStoreCreditExpirtDate()
   {
	   return this.stExpirtDate;
   }
   
   public void setTotalTenderAmount(CurrencyDecimal totalTenderAmount)
   {
	   this.totalTenderAmount=totalTenderAmount;
   }
   
   public CurrencyDecimal getTotalTenderAmount()
   {
	   
    return this.totalTenderAmount;
   }
   	/**MAX Rev 1.1 Change : End**/
   /**
    * Rev 1.2 changes start
    */
	public TenderLineItemIfc getTenderLineItem() {
		return tenderLineItem;
	}

	public void setTenderLineItem(TenderLineItemIfc tenderLineItem) {
		this.tenderLineItem = tenderLineItem;
	}
	/**
	    * Rev 1.2 changes end
	 */
	// Changes start for code merging(Adding below methods as it is not present in base 14)
	public boolean isGiftCardApproved()
	{
		return isGiftCardApproved;
	}
	// changes ends for code merging
	public void setGiftCardApproved(boolean isGiftCardApproved)
	{
		this.isGiftCardApproved = isGiftCardApproved;
	}
	

	/* Changes for Rev 1.4 starts*/
	/**
	 * @return the phoneNumber
	 */
	public String getPaytmPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPaytmPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getAmazonPayPhoneNumber() {
		return amazonPayMobileNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setAmazonPayPhoneNumber(String amazonPayMobileNumber) {
		this.amazonPayMobileNumber = amazonPayMobileNumber;
	}
	
	/**
	 * @return the otp
	 */
	public String getPaytmTotp() {
		return totp;
	}

	/**
	 * @param otp the otp to set
	 */
	public void setPaytmTotp(String totp) {
		this.totp = totp;
	}
	/* Changes for Rev 1.4 ENDS*/
	
	/* Changes for Rev 1.5 starts*/
	/**
	 * @return the paytmResp
	 */
	public MAXPaytmResponse getPaytmResp() {
		return paytmResp;
	}

	/**
	 * @param paytmResp the paytmResp to set
	 */
	public void setPaytmResp(MAXPaytmResponse paytmResp) {
		this.paytmResp = paytmResp;
	}
	public boolean isCreditOnlineFlow() 
	{
		return creditOnlineFlow;
	}
	// added by atul shukla
	public void setCreditOnlineFlow(boolean creditOnlineFlow) 
	{
		this.creditOnlineFlow = creditOnlineFlow;
	}
	public HashMap getResponseMap() {
		return responseMap;
	}

	public void setResponseMap(HashMap responseMap) {
		this.responseMap = responseMap;
	}
	/*
	public HashMap getPaytmMobileNumber() {
		return paytmMobileNumber;
	}

	public void setPaytmMobileNumber(HashMap responseMap) {
		this.paytmMobileNumber = paytmMobileNumber;
	}
	*/
	
	// Changes Start By Bhanu Priya 
	public String getMobikwikMobileNo() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setMobikwikMobileNo(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	/**
	 * @return the otp
	 */
	public String getMobikwikTotp() {
		return totp;
	}

	/**
	 * @param otp the otp to set
	 */
	public void setMobikwikTotp(String totp) {
		this.totp = totp;
	}
	
	public MAXMobikwikResponse getMobikwikResp() {
		return mobikwikResp;
	}

	/**
	 * @param paytmResp the paytmResp to set
	 */
	public void setMobikwikResp(MAXMobikwikResponse mobikwikResp) {
		this.mobikwikResp = mobikwikResp;
	}
	int timeout;
	public int getMobikwikTimeout()
	{
	return timeout;
	}
	public void setMobikwikTimeout(int i)
	{
	this.timeout=i;
	
	}
	// Changes Ends by Bhanu Priya 
	
	public String redeemPointAmount = null;
	public String totalPointAmount = null;
	
	public String getRedeemPointAmount() {
		return redeemPointAmount;
	}
	public void setRedeemPointAmount(String redeemPointAmount) {
		this.redeemPointAmount = redeemPointAmount;
	}
	
	public String getTotalPointAmount() {
		return totalPointAmount;
	}

	public void setTotalPointAmount(String totalPointAmount) {
		this.totalPointAmount = totalPointAmount;
	}
	
public MAXSBIPointsRedemptionResponse sbiPointResp = new MAXSBIPointsRedemptionResponse();
public String getOxigenOtp() {
		return oxigenOtp;
	}

	public void setOxigenOtp(String oxigenOtp) {
		this.oxigenOtp = oxigenOtp;
	}
	
	
	public MAXSBIPointsRedemptionResponse getSbiPointResp() {
		return sbiPointResp;
	}

	public void setSbiPointResp(MAXSBIPointsRedemptionResponse sbiPointResp) {
		this.sbiPointResp = sbiPointResp;
	}
	protected boolean sbiFlag = false;
	public boolean isSbiFlag() {
		return sbiFlag;
	}

	public void setSbiFlag(boolean sbiFlag) {
		this.sbiFlag = sbiFlag;
	}
	public String geteWalletTraceId() {
		return eWalletTraceId;
	}

	public void seteWalletTraceId(String eWalletTraceId) {
		this.eWalletTraceId = eWalletTraceId;
	}

	public String geteWalletMobileNumber() {
		return eWalletMobileNumber;
	}

	public void seteWalletMobileNumber(String eWalletMobileNumber) {
		this.eWalletMobileNumber = eWalletMobileNumber;
	}
	

	public String geteWalletReturnGetOtpResponse() {
		return eWalletReturnGetOtpResponse;
	}

	public void seteWalletReturnGetOtpResponse(String eWalletReturnGetOtpResponse) {
		this.eWalletReturnGetOtpResponse = eWalletReturnGetOtpResponse;
	}

	public boolean isEWalletTenderFlag() {
		return isEWalletTenderFlag;
	}

	public void setEWalletTenderFlag(boolean isEWalletTenderFlag) {
		this.isEWalletTenderFlag = isEWalletTenderFlag;
	}

	public String getOtpRefNum() {
		return otpRefNum;
	}

	public void setOtpRefNum(String otpRefNum) {
		this.otpRefNum = otpRefNum;
	}

	public String getOxigenAppliedAmt() {
		return oxigenAppliedAmt;
	}

	public void setOxigenAppliedAmt(String oxigenAppliedAmt) {
		this.oxigenAppliedAmt = oxigenAppliedAmt;
	}
	
	//changes for paytmqr
	public MAXPaytmQRCodeResponse getPaytmQRCodeResp() {
		return paytmQRCodeResp;
	}

	public void setPaytmQRCodeResp(MAXPaytmQRCodeResponse paytmQRCodeResp) {
		this.paytmQRCodeResp = paytmQRCodeResp;
		
    //changes by shyvanshu mehra
	
		
	}

/*	public MAXEnterCustomerInfoSite getCanSkipCustomerPrompt() {
		return entercustomerinfo;
	}
	
	public void setentercustomerinfo(MAXEnterCustomerInfoSite entercustomerinfo) {
		this.entercustomerinfo = entercustomerinfo;*/
	
	protected CurrencyIfc parkingTotal = null;
	protected int couponType;
	protected String couponName;

	public CurrencyIfc getParkingTotal() {
		return parkingTotal;
	}

	public void setParkingTotal(CurrencyIfc parkingTotal) {
		this.parkingTotal = parkingTotal;
	}

	public int getCouponType() {
		return couponType;
	}

	public void setCouponType(int couponType) {
		this.couponType = couponType;
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	//Rev 1.3 changes start
		private int otpRetries = 0;
		public int getOtpRetries() {
			return otpRetries;
		}

		public void setOtpRetries(int otpRetries) {
			this.otpRetries = otpRetries;
		}
		
			private String otpForSc;
			/**
			 * @return the otpForSc
			 */
			public String getOtpForSc() {
				return otpForSc;
			}

			/**
			 * @param otpForSc the otpForSc to set
			 */
			public void setOtpForSc(String otpForSc) {
				this.otpForSc = otpForSc;
			}
			
			//Rev 1.3 changes end
			
			// Added by kamlesh pant for manager override
			protected EmployeeIfc lastOperator = null;
					
			public void setLastOperator(EmployeeIfc value) {
			     this.lastOperator = value;
			}
			public EmployeeIfc getLastOperator() {
			     return this.lastOperator;
			}
}
