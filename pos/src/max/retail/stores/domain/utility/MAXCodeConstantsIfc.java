/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2010 MAX Spar India Pvt Ltd.    All Rights Reserved.

 * Rev 1.0 MAX_FES_Capillary CouponRedemption-8-JUN-15 (1) 08/06/2015 Abhishek Singh
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.utility;

import oracle.retail.stores.domain.utility.CodeConstantsIfc;

public interface MAXCodeConstantsIfc extends CodeConstantsIfc {
	public static final String CAPILLARY_COUPON_NUM = "CapillaryCouponNumber";
	public static final String CAPILLARY_COUPON_CAMPAIGN_ID = "CapillaryCouponCampaignId";
	public static final String CAPILLARY_COUPON_REASON_CODE = "CapillaryCouponReasonCode";
	public static final String CAPILLARY_COUPON_DISCOUNT_TYPE = "CapillaryCouponDiscountType";
	public static final int DISCOUNT_SCOPE_CAPILLARY_COUPON = 4;
	public static final String CODE_LIST_CAPILLARY_COUPON_DISCOUNT = "DiscountByCapillaryCoupon";
	public static final String DISCOUNT_CARD_AMOUNT = "DC_AMOUNT";
	public static final String DISCOUNT_CARD_NUM = "Discountcardnumber";
	public static final String DISCOUNT_CARD_REDEEM_CODE = "DC_REDEMPTION_CODE";
	public static final String CODE_LIST_DISCOUNT_CARD_DISCOUNT= "DiscountByDiscountCard";
	public static final int DISCOUNT_SCOPE_DISCOUNT_CARD = 3; 
	
	public static final String CODE_LIST_CREDIT_DEBIT_BANK_CODES = "CreditDebitBankCodes";
	// public static final String STORE_ID = "storeId";
	// public static final String GSTIN_DATA_TRANFER_ID = "gstinDataTransferID";

    public static final String GSTIN_DATA_TRANFER_ID = "GSTIN_DATA_TRANFER_ID";
    public static final String GSTIN_DATA_TRANFER_DETAILS ="GSTIN_DATA_TRANFER_DETAILS";
    public static final String STORE_ID ="STORE_ID";
	public static final Object GSTIN_AUTOMATION = "GSTIN_AUTOMATION ";
	public static final Object GSTIN_TTL_TXN ="GSTIN_TTL_TXN" ;
	public static final Object GSTIN_TTL_DAYS = "GSTIN_TTL_DAYS";
	public static final Object GSTIN_EINV = "GSTIN_EINV";
	public static final Object GSTIN_DOC_ID = "GSTIN_DOC_ID";
	public static final Object GSTIN_INVOICE = "GSTIN_INVOICE";
	public static final Object GSTIN_DOC_ERROR = "GSTIN_DOC_ERROR";
	public static final Object TXNID = "TXNID";
	public static final Object GSTIN_STORE_NO = "GSTIN_STORE_NO";
	public static final Object GSTIN_STORE = "GSTIN_STORE";
	//added by vaibhav for BILL buster
	public static final String BILL_BUSTER_PCT = "bb_pct";
	public static final String BILL_BUSTER_AMT ="bb_amt";
}
