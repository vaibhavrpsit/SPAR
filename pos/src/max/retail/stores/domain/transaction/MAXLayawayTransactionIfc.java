/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2015 Lifestyle India Pvt Ltd.    All Rights Reserved.
 * 
 * Rev 2.0 		May 14, 2024			Kamlesh Pant		Store Credit OTP:
 * Rev 1.13		9th Mar 2020			Vidhya Kommareddi	POS REQ: RRP for Layaway Transactions.
 * Rev 1.12		7th Nov 2019			Vidhya Kommareddi
 * MPOS REQ:  MPOS Transaction Indentifier
 * Rev 1.11 	Jul 10,2019	    Nitika Arora        E-Wallet Integration
 * Rev 1.10		6th June 2019	Vidhya Kommareddi	POS REQ: Block suspend after N suspends
 * Rev 1.9  	19th Apr 2019	Vidhya Kommareddi	Suspend Retrieve using Mobile Number CR
 * Rev 1.8		Jan 09,2019		Vidhya Kommareddi	TO Auto-Scheduling CR
 * Rev 1.7 		Aug 10, 2018	Jyoti Yadav	  Quoting PAN CR
 * *Rev 1.6 	 May 03,2018     	Kritica Goel  Layaway calculation change
 * Rev 1.5  	Sep 20,2017     Nitika Arora  Omni Channel integration
 *
 * Rev 1.4 			GST changes
 * Rev 1.3	Jul 30,2016  Aakash Gupta
 * Requirement: Transaction suspended in MPOS should not get altered upon retrieval in POS
 *
 * Rev 1.2	Nov 20th,2015		Aakash Gupta
 * Changes for Employee Discount Limit Functionality.
 *
 * Rev 1.1  25/Sep/2015  Priyanka  Singh  Change for Layaway upgrade in 14 version .
 * Rev 1.0  March 23, 2011 03:00:30 PM Amit.Tiwari
 * Initial revision.
 * Resolution for FES_LMG_Receipt_Soft_Copy_v1.1
 * To save/retrieve the Receipt data for the LAYAWAY transaction.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.transaction;

import java.util.Vector;

//import max.retail.stores.domain.businessassociate.MAXBusinessAssociateIfc;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXCustomerType;
import max.retail.stores.domain.discountCoupon.MAXDiscountCouponIfc;
//import max.retail.stores.domain.easyexchange.MAXEasyExchangeDTO;
//import max.retail.stores.domain.ewallet.MAXEWalletAPIWebProperties;
//import max.retail.stores.domain.gstin.MAXGSTINValidationResponseIfc;
//import max.retail.stores.domain.paytm.MAXPaytmResponse;
//import max.retail.stores.domain.selfcheckout.InnovitiResponseIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public interface MAXLayawayTransactionIfc extends LayawayTransactionIfc {

	/**
	 * revision number
	 **/
	public static final String revisionNumber = "$Revision: 1.3 $";

	// ---------------------------------------------------------------------
	/**
	 * @param strBuffer
	 */
	public void setStringBuffer(StringBuffer strBuffer);

	// ---------------------------------------------------------------------
	/**
	 * @return
	 */
	// ---------------------------------------------------------------------
	public StringBuffer getStringBuffer();

	// Added By Chiranjibi Routray For Business Associate Starts Here
	//public MAXBusinessAssociateIfc getBusinessAssociate();

	//public void setBusinessAssociate(MAXBusinessAssociateIfc businessAssociate);

	// Added By Chiranjibi Routray For Business Associate Ends Here
	public void setLSSIPLTICCustomer(MAXCustomerIfc ticcustomer);

	public MAXCustomerIfc getLSSIPLTICCustomer();

	// Change for Rev 1.1 :Start
	public boolean isTicCustomerVisibleFlag();

	public void setTicCustomerVisibleFlag(boolean ticCustomerVisibleFlag);

	public Vector getCapillaryCouponsApplied();

	public void addCapillaryCouponsApplied(MAXDiscountCouponIfc discountCoupon);

	public void removeCapillaryCouponsApplied();
	// Change for Rev 1.1 :End

	// Change for Rev 1.2:Starts
	public String getDiscountEmployeeLocation();

	public void setDiscountEmployeeLocation(String discountEmployeeLocation);

	public String getDiscountEmployeeRole();

	public void setDiscountEmployeeRole(String discountEmployeeRole);

	public String getDiscountEmployeeName();

	public void setDiscountEmployeeName(String discountEmployeeName);

	public void clearEmployeeDiscount(TransactionDiscountStrategyIfc tds);
	// Change for Rev 1.2:Ends

	// Change for Rev 1.3:Starts
	public boolean isMposSuspended();

	public void setMposSuspended(boolean mposSuspended);

	public String getMposSuspndedTxnNum();

	public void setMposSuspndedTxnNum(String mposSuspndedTxnNum);

	public String getMposSuspendedRegisterID();

	public void setMposSuspendedRegisterID(String mposSuspendedRegisterID);

	public String getMposSuspendedBusinessDate();

	public void setMposSuspendedBusinessDate(String mposSuspendedBusinessDate);
	// Change for Rev 1.3:Ends
	// Change for Rev 1.4:Starts
	public boolean isGstEnable();
	public void setGstEnable(boolean gstEnable);
	public Map<String, String> getTaxCode();
	public void setTaxCode(Map<String, String> taxCode);
	// Change for Rev 1.4:Ends

	//Changes start for Omni Channel integration	
	public String getOmniChannelOrderId();

	public void setOmniChannelOrderId(String omniChannelOrderId);

	public String getOmniChannelOrderIdFlag();

	public void setOmniChannelOrderIdFlag(String omniChannelOrderIdFlag);

	//Changes end for Omni Channel integration
	//Changes for getting the Concept based on the register id starts
	public String getConcept();
	public void setConcept(String concept);
	//Changes for getting the Concept based on the register id ends
	//Changes for Manager override functionality starts
	public int getAccessFunctionId();
	public void setAccessFunctionId(int accessFunctionId);
	//Changes for Manager override functionality ends
	//Change for Rev 1.6: Starts
	public String getToLocation();
	public void setToLocation(String toLocation);
	//Change for Rev 1.6: Ends
	public void setCustomerFeedback(String feedback);
	public String getCustomerFeedback();
	/*Change for Rev 1.7: Start*/
	public MAXCustomerType[] getCustomerTypeDetails();
	public void setCustomerTypeDetails(MAXCustomerType[] customerTypeDetails);
	public String getMaxUINnum();
	public void setMaxUINnum(String maxUINnum);
	/*Change for Rev 1.7: End*/
	//Changes starts for Rev 1.1 (Ashish : GoogleMap)
	public String getLatitude();

	public void setLatitude(String latitude);
	public String getLongitude();

	public void setLongitude(String longitude);

	//Changes ends for Rev 1.1 (Ashish : GoogleMap)
	//Rev 1.8 start
	public String getPickupLocation();

	public void setPickupLocation(String pickupLocation);

	public EYSDate getDeliveryDate();

	public void setDeliveryDate(EYSDate deliveryDate);
	public void setDeliveryEmailID(String deliveryEmailID);
	public String getDeliveryEmailID();
	//Rev 1.8 end

	public HashMap<String, String> getGcCustomer();
	public void setGcCustomer(HashMap<String, String> gcCustomer);
	//Changes starts for rev 1.1 (Ashish : ICMP)
	//public abstract InnovitiResponseIfc[] getInnovitiCustReceipt();

	//public abstract void setInnovitiCustReceipt(
		//	InnovitiResponseIfc[] paramArrayOfInnovitiResponseIfc);
	/*public abstract InnovitiResponseIfc[] getInnovitiStoreReceipt();

	public abstract void setInnovitiStoreReceipt(
			InnovitiResponseIfc[] paramArrayOfInnovitiResponseIfc);*/
	//Changes ends for rev 1.1 (Ashish : ICMP)
	//Rev 1.9 start 
	public void setSuspendRetrieveMobileNum(String mobileNumber);
	public String getSuspendRetrieveMobileNum();
	//Rev 1.9 end 
	//Changes starts for Rev 1.1 (Ashish : SCPhase3)
	/*public MAXPaytmResponse getPaymentResponse();
	public void setPaymentResponse(MAXPaytmResponse paymentResponse);
	public MAXPaytmResponse getReversalResponse();
	public void setReversalResponse(MAXPaytmResponse paymentResponse);
	public void setPaytmArrayResponse(ArrayList<MAXPaytmResponse> paytmArrayResponse);
	public ArrayList<MAXPaytmResponse> getRevPaytmArrayResponse();
	public void setRevPaytmArrayResponse(ArrayList<MAXPaytmResponse> revPaytmArrayResponse);
	public ArrayList<MAXPaytmResponse> getPaytmArrayResponse();*/
	//Changes ends for Rev 1.1 (Ashish : SCPhase3)

	//Rev 1.10 start
	public void setNumberofSuspends(int numberOfSuspends);
	public int getNumberOfSuspends();
	//Rev 1.10 end
	public boolean isMposTxn();
	public void setMposTxn(boolean mposTxn);

	/*//Change for Rev 1.11 Start
	public MAXEWalletAPIWebProperties getApiWebProperties();

	public void setApiWebProperties(MAXEWalletAPIWebProperties apiWebProperties);
	//Change for Rev 1.11 Ends
	//Changes starts for rev 1.1 (Ashish : EasyExchange)
	public MAXEasyExchangeDTO getEasyExchangeRequest();

	public void setEasyExchangeRequest(MAXEasyExchangeDTO dto);
	//Changes ends for rev 1.1 (Ashish : EasyExchange)
*/
	//Rev 1.12 start
	public boolean getIsMposInitiated() ;	
	public void setIsMposInitiated(boolean isMposInitiated) ;
	//Rev 1.12 end
	public boolean isDeleteReceipt();
	public void setDeleteReceipt(boolean deleteReceipt);
	//Changes starts for rev 1.1 (Ashish ; EReceipt)
	public String geteReceiptConf();
	public void seteReceiptConf(String eReceiptConf);
	public String geteReceiptTrantype();
	public void seteReceiptTrantype(String eReceiptTrantype);
	public boolean isEReceiptValueSelected();
	public void setEReceiptValueSelected(boolean isEReceiptValueSelected);

	//Changes ends for rev 1.1 (Ashish ; EReceipt)

	//Rev 1.13 start
	public String getThresholdPrice() ;		
	public void setThresholdPrice(String thresholdPrice);
	//Rev 1.13 end
	
	//Changes ends for rev 1.1 (Ashish ; EReceipt)
		public String getEdcType();
		public void setEdcType(String edcType);
		//Changes starts for Rev 1.1 (Ashish : PineLab)
		//public MAXGSTINValidationResponseIfc getGstinresp();
	//	public void setGstinresp(MAXGSTINValidationResponseIfc gstinresp);
		public boolean isLocalCustLink();
		public void setLocalCustLink(boolean localCustLink);
		public boolean isSbiRewardredeemFlag();
		public void setSbiRewardredeemFlag(boolean sbiRewardredeemFlag);
		
		//changes for paytmqr
		public String getPaytmQROrderId();
		public void setPaytmQROrderId(String paytmQROrderId);
		
		//Rev 2.0 Starts
    	public String getScOtp();
		public void setScOtp(String scOtp);
		public String getCustOgMobile() ;
		public void setCustOgMobile(String custOgMobile);
		public String getOgTransaction();
		public void setOgTransaction(String ogTransaction);
		public String getCustMobileforOTP() ;
		public void setCustMobileforOTP(String custMobileforOTP); 
		//Rev 2.0 Ends 
		
}
