/********************************************************************************
 * Copyright (c) 2016 MAX Hyper Market Inc.    All Rights Reserved.
 *
 *	Rev	1.2 	Purushotham Reddy		June 01, 2019		 	Changes for POS-Amazon Pay Integration 
 *	Rev 1.1  	Ashish Yadav   			11 May,2017 			Changes for M-Coupon Issuance FES
 * 	Rev 1.0 	Hitesh.dua 				23Jan,2017				Initial revision. changes for printing customized receipt. 
 * 
 *********************************************************************************/
package max.retail.stores.pos.receipt;

import oracle.retail.stores.pos.receipt.ReceiptTypeConstantsIfc;

public abstract interface MAXReceiptTypeConstantsIfc extends ReceiptTypeConstantsIfc {
	public static final String LOYALTY_POINTS = "LoyaltyTenderReceipt";
	public static final String GIFT_CARD_SLIP = "GiftCardSlip";
	public static final String HIRE_PURCHASE = "HirePurchase";
	public static final String FREE_ITEM = "FreeItem";
	public static final String SHIPPING_SLIP = "ShippingSlip";
	// added by atul shukla
	public static final String PAYTMCHARGESLIP = "PaytmChargeSlip";
	public static final String PAYTMREFUNDSLIP = "PaytmRefundSlip";
	
	public static final String MOBIKWIKCHARGESLIP = "MobikwikChargeSlip";
	public static final String MOBIKWIKREFUNDSLIP = "MobikwikRefundSlip";
	//changes for rev 1.1 start
	public static final String MCOUPON_RECEIPT = "MCouponReceipt";
	public static final String BAKERYPOSRECEIPT="BakeryPosReceipt";
	
	//added for mall certificate
	public static final String MALLCERTIFICATE="MallCertificateReceipt";
	
	public static final String AMAZONPAYCHARGESLIP = "AmazonPayChargeSlip";
	public static final String AMAZONPAYPAYREFUNDSLIP = "AmazonPayRefundSlip";
	
	public static final String PAYTMQRCODECHARGESLIP = "PaytmQRCodeChargeSlip";
	public static final String PAYTMQRREFUNDSLIP = "PaytmQRRefundSlip";
}