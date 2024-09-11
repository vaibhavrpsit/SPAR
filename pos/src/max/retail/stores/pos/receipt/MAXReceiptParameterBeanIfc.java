/********************************************************************************
 * Copyright (c) 2016 MAX Hyper Market Inc.    All Rights Reserved.
 * 
 *	Rev	1.6 	June 20 2019		Purushotham Reddy 	Changes for POS-Amazon Pay Integration 
 *
 * 	Rev 1.5  	hitesh dua   19 Apr,2017 Changes for removing NA from last name of customer

 * 	Rev 1.4		May 04, 2017		Kritica Agarwal 	GST Changes
 * 	Rev 1.3  	hitesh dua   19 Apr,2017 removed extra methods of employee discount
 * 
 * 	Rev 1.2  	Nitika   11 Apr,2017  Code done for printing the Refund Amount on the receipt for Ecom functionality
 * 
 * 	Rev 1.1  	Nitika   22 Mar,2017 Code done for EComPrepaid and EComCOD tender functionality.
 * 
 *	Rev 1.0	 	Hitesh.dua 		15dec,2016	Initial revision.changes for printing customized receipt. 
 * ===========================================================================
 */

package max.retail.stores.pos.receipt;

import java.util.Vector;

import max.retail.stores.domain.MAXAmazonPayResponse;
import max.retail.stores.domain.MAXMobikwikResponse;
import max.retail.stores.domain.bakery.MAXBakeryItemIfc;
import max.retail.stores.domain.gstin.MAXGSTINValidationResponseIfc;
import max.retail.stores.domain.mcoupon.MAXMcouponIfc;
//import max.retail.stores.domain.MAXPaytmQRCodeResponse;
import max.retail.stores.domain.paytm.MAXPaytmQRCodeResponse;
import max.retail.stores.domain.MAXPaytmResponse;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.VATHelper;

public abstract interface MAXReceiptParameterBeanIfc extends
ReceiptParameterBeanIfc {
	
	public abstract String[] getLoyaltyPointsSlipFooter();

	public abstract void setLoyaltyPointsSlipFooter(
			String[] loyaltyPointsSlipFooter);

	public abstract String getLoyaltyRedeemedPoints();

	public abstract void setLoyaltyRedeemedPoints(String loyaltyRedeemedPoints);

	public abstract String getLoyaltyRedeemedAmount();

	public abstract void setLoyaltyRedeemedAmount(String loyaltyRedeemedAmount);

	public abstract VATHelper getVATHelper();

	public abstract String getPhoneByMobileType();
	 
	public abstract CurrencyIfc getTotalAmountForEmployeeDiscount();
	 
	public abstract boolean isNormalItemShouldPrint();
	 
	public abstract boolean printLoyaltyCustomer();
	
	public abstract CurrencyIfc getTotalSavings();
	 
	public abstract CurrencyIfc getTotalAmount();
	 
	public abstract Vector getCapillaryCoupon();
	 
	public abstract TenderLineItemIfc[] getTenders();

	public int getGiftCardRequestType();
	
	public void setGiftCardRequestType(int giftCardRequestType);

	public GiftCardIfc getGiftCard();

	public void setGiftCard(GiftCardIfc giftCard);

	public String[] getReceiptFooter();

	public void setReceiptFooter(String[] receiptFooter);
			 
	public CurrencyIfc getTotalRoundedAmount();

	public boolean isNonTaxableItem();

	public CurrencyIfc getEmpDiscountAvailLimit();

	public CurrencyIfc printOtherDiscount();

	public CurrencyIfc getEmployeeDiscountAmount();

	public void setCopyReportText(String copyReportText);

	public String getCopyReportText();
	
	public boolean printEcomDetails();

	public CurrencyIfc getBillBusterDiscount();

	public boolean isExtraCopy();
	
	public CurrencyIfc geteComOrderRefund();

	//Change for Rev 1.4 : Starts
	public Boolean isGSTEnabled();
	public MAXTaxSummaryDetailsBean[] printTaxSummaryDetails() ;
	/*public String getgSTNumber();
	public void setgSTNumber(String gstNumber);*/
	public boolean isGSTDisable();
	public CurrencyIfc getTAXSummaryTotal();
	public boolean isGSTEnabledReturnTransaction();
	public String getOriginalTransactionNumber();
	public String getOriginalTransactionDate();
	//public int getNumberLineItem();
	public CurrencyIfc getTaxCollectedOnAdvance();
	//Change for Rev 1.4 : Ends
	public boolean isGSTTaxApplicable();
//Change for Rev 1.5 : Starts
	public String getLoyaltyCustomerName();

	public String getLayawayCustomerName();
//Change for Rev 1.5 : end
	
	public MAXBakeryItemIfc[] getBakeryItems();
	public void setBakeryItems(MAXBakeryItemIfc[] bakery);
	public String getCategoryDesc();
	public void setCategoryDesc(String  category);
	public MAXPaytmResponse getPaytmResponse();

	public void setPaytmResponse(MAXPaytmResponse paytmResponse);	
	
	public MAXMobikwikResponse getMobikwikResponse();

	public void setMobikwikResponse(MAXMobikwikResponse mobikwikResponse);

	/* Changes start by Atul Shukla */
	public void setEmployeeCompanyName(String companyName);
	public String getEmployeeCompanyName();
	
	/*changes for Rev 1.6 start*/

	public MAXMcouponIfc getMcoupon();	
	public void setMcoupon(MAXMcouponIfc mcoupon);
	
	public String getPanCardNumber();	
	public void setPanCardNumber(String  pancard);
	
	public String getForm60Number();	
	public void setForm60Number(String  form60ID);
	
	public String getPassportNumber();	
	public void setPassportrdNumber(String  passportNum);
	
	public String getITRAckNumber();	
	public void setITRAckNumber(String  acknum);
	
	public String getVisaNumber();	
	public void setVisNumber(String  visaNum);
	
	public MAXAmazonPayResponse getAmazonPayResponse();

	public void setAmazonPayResponse(MAXAmazonPayResponse amazonPayResponse);
	/*changes for Rev 1.6 end*/
	
	public String getEReceiptOTPNumber() ;

	public void setEReceiptOTPNumber(String eReceiptOTP);
	
	public MAXPaytmQRCodeResponse getPaytmQRCodeResponse();
	
	public void setPaytmQRCodeResponse(MAXPaytmQRCodeResponse paytmQRCodeResponse);
	
	
	public MAXGSTINValidationResponseIfc getTransactionGSTDetails() ;
	public String getExchangeDate();
	public String getTaxableAmount();

	void setTaxableAmount(String taxableAmount);
}
