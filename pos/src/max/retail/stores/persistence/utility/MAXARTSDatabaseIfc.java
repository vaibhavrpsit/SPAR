/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved. 
 *	Rev 1.5		Sep 01, 2020		Kumar Vaibhav   Pinelabs integration
 *  Rev 1.3		May 04, 2017		Kritica Agarwal GST Changes
 *	Rev	1.2 	Feb 03, 2017		Hitesh Dua		Changes for Customer related Query
 *	Rev	1.1 	Nov 07, 2016		Mansi Goel		Changes for Discount Rule FES
 *	Rev 1.0     Oct 17, 2016		Nitesh Kumar	Code Merge	
 *
 ********************************************************************************/

package max.retail.stores.persistence.utility;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

public interface MAXARTSDatabaseIfc extends ARTSDatabaseIfc {

	// Changes for Rev 1.0 : Starts
	public static final String FIELD_GIFT_CARD_AUTHORIZATION_CODE = "GC_AUTH_CD";
	public static final String FIELD_GIFT_CARD_INVOICE_ID = "GC_INV_ID";
	public static final String FIELD_GIFT_CARD_BATCH_ID = "GC_BTCH_ID";
	public static final String FIELD_GIFT_CARD_EXP_DATE = "GC_EXP_DT";
	public static final String FIELD_GIFT_CARD_TYPE = "GC_CRD_TP";
	public static final String FIELD_GIFT_CARD_TRANS_ID = "GC_TRX_ID";
	public static final String FIELD_EXPECTED_ORDER_DELIVERY_DATE = "EXP_DEL_DATE";
	public static final String FIELD_EXPECTED_ORDER_DELIVERY_TIME = "EXP_DEL_TIME";

	public static final String FIELD_STORE_CREDIT_VALIDATION_FLAG = "FL_VLD_CR_STR";
	public static final String FIELD_STORE_CREDIT_AUTHORIZATION_CODE = "CD_AUTH_CR_STR";
	public static final String TABLE_WEB_REQUEST_LOG = "WEB_RQST_LOG";
	/** MESSAGE ID */
	public static final String FIELD_MESSAGE_ID = "ID_MESSAGE";
	/** REQUEST_TYPE */
	public static final String FIELD_REQUEST_TYPE_A = "TY_RQST_A";
	/** REQUEST STATUS */
	public static final String FIELD_REQUEST_STATUS = "STS_RQST";
	/** STORE ID */
	public static final String FIELD_STORE_ID = "ID_STR_RT";
	/** TILL ID */
	public static final String FIELD_TILL_ID = "ID_TL";
	/** REGISTER ID */
	public static final String FIELD_REGISTER_ID = "ID_WS";
	/** INVOICE BUSINESS DATE TIME */
	public static final String FIELD_INVOICE_BUSINESS_DATE = "DC_DY_BSN";
	/** INVOICE NUMBER */
	public static final String FIELD_INVOICE_NUMBER = "NM_INV";
	/** TIC NUMBER */
	public static final String FIELD_TIC_NUMBER = "NM_TIC";
	/** TRANSACTION TOTAL AMOUNT */
	public static final String FIELD_TRANS_TOTAL_AMT = "AMT_TOT_TRN";
	/** SETTLE TOTAL AMOUNT */
	public static final String FIELD_SETTLE_TOTAL_AMT = "AMT_TOT_STL";
	/** REQUEST TYPE B */
	public static final String FIELD_REQUEST_TYPE_B = "TY_RQST_B";
	/** REQUEST DATE */
	public static final String FIELD_REQUEST_DATE = "DATE_RQST";
	/** REQUEST TIME OUT */
	public static final String FIELD_REQUEST_TIME_OUT = "OUT_TIME_RQST";
	/** REQUEST URL */
	public static final String FIELD_REQUEST_URL = "URL_RQST";
	/** RESPONSE APPROVED FLAG */
	public static final String FIELD_RESPONSE_APPROVED_FLAG = "FL_APR_RSP";
	/** RESPONSE APPROVED VALUE */
	public static final String FIELD_RESPONSE_APPROVED_VALUE = "VL_APR_RSP";
	/** RESPONSE APPROVED VALUE */
	public static final String FIELD_RESPONSE_MESSAGE = "MSG_RSP";
	/** RESPONSE APPROVED VALUE */
	public static final String FIELD_RESPONSE_RECEIVED_DATE_TIME = "TS_DT_RCV_RSP";
	/** TIME OUT MESSAGE ID **/
	public static final String FIELD_ID_MESSAGE_TYPMEOUT = "ID_MESSAGE_TYPMEOUT";

	public static final String TABLE_LOYALTY_POINT_TENDER_LINE_ITEM = "TR_LTM_LY_PT_TND";
	public static final String FIELD_LOYALTY_CARD_NUMBER = "LY_PT_CRD_NMB";
	public static final String FIELD_LOYALTY_POINT_AMOUNT = "MO_AZN_AMT";

	/** Changes for TIC Customer information **/
	public static final String FIELD_LOYALTY_POINT_BALANCE = "PNT_BLNC";
	public static final String FIELD_NEXT_MONTH_EXP_PNT = "MNTH_NXT_EXP_PNT";
	public static final String FIELD_POINT_LAST_UPDATED = "DT_UPD_LST_PNT_BLNC";
	// chnages for rev 1.2 start
	public static final String FIELD_CUSTOMER_TYPE = "TY_CT";
	// chnages for rev 1.2 end
	public static final String FIELD_CUSTOMER_TIER = "TR_CT";

	public static final String MAX_IN_AMT = "MAX_IN_AMT";
	public static final String MAX_OUT_AMT = "MAX_OUT_AMT";

	public static final String FIELD_INVOICE_NUMBERCRE = "INV_NUM";
	public static final String FIELD_ACQUIRER_BANK_CODE = "ACQ_BK_COD";
	public static final String FIELD_TRANSACTION_ACQUIRER = "TRN_ACQ";
	public static final String FIELD_AUTH_REMARKS = "AUTH_REM";
	public static final String FIELD_BATCH_NUMBER = "BTCH_NUM";
	public static final String FIELD_MERCHANT_ID = "MERCHANT_ID";
	public static final String FIELD_TERMINAL_ID = "TERMINAL_ID";
	public static final String FIELD_RETRIVAL_REF_NO = "RTRVL_REF_NO";
	public static final String FIELD_LAST_FOUR_DIGIT = "BK_ID";
	// Card Holder Name
	public static final String FIELD_CARD_HOLDER_NAME = "CRD_HOLD_NAME";

	public static final String ALIAS_MIX_AND_MATCH_PRICE_DERIVATION_RULE = "MXMH";
	public static final String TABLE_MIX_AND_MATCH_PRICE_DERIVATION_RULE = "RU_PRDVC_MXMH";

	// Changes for Rev 1.1 : Starts
	public static final String TABLE_GROUP_ITEM_LIST = "MAX_GRP_ITM_LST";
	public static final String ALIAS_TABLE_GROUP_ITEM_LIST = "GRP_ITM_LST";
	// Changes for Rev 1.1 : Ends

	public static final String TABLE_GROUP_ITEM = "LMG_GRP_ITM";
	public static final String ALIAS_TABLE_GROUP_ITEM = "GRP_ITM";

	public static final String FIELD_ITEM_GROUP_ID = "ID_GRP";
	public static final String FIELD_CO_ITEM_GROUP_ID = "CO_ID_GRP";
	public static final String FIELD_MIXNMATCH_ITEM_GROUP_ID = "ID_PRM_PRD";

	public static final String TABLE_ITEM_GROUP_PRICE_DERIVATION_RULE_ELIGIBILITY = "MAX_CO_EL_PRDV_ITM_GRP";
	public static final String ALIAS_TABLE_ITEM_GROUP_PRDV = "ITM_GRP_PRDV";

	public static final String FIELD_BRAND_DESCRIPTION = "DE_BRN";
	public static final String TABLE_BRAND_PRICE_DERIVATION_RULE_ELIGIBILITY = "MAX_CO_EL_PRDV_BRN";
	public static final String ALIAS_TABLE_BRAND_PRDV = "BRAND_PRDV";
	public static final String FIELD_BRAND_ID = "ID_BRN";
	public static final String FIELD_PRICE_DERIVATION_RULE_MVALUE = "RULE_QTY";

	public static final String TABLE_COUPON_DETAIL_TENDER = "MAX_QPON_DTLS_DSP";
	public static final String FIELD_RECONCILE_TENDER_WORKSTATION_ID = "ID_WS";
	public static final String FIELD_RECONCILE_TENDER_TILL_ID = "ID_RPSTY_TND";
	public static final String FIELD_RECONCILE_BUSINESS_DATE = "DC_DY_BSN";
	public static final String FIELD_RECONCILE_TENDER_STORE_ID = "ID_STR_RT";
	public static final String FIELD_RECONCILE_TENDER_TRANSACTION_ID = "AI_TRN";
	public static final String FIELD_COUPON_DETAIL_TENDER_COUPON_NAME = "QPON_NM";
	public static final String FIELD_COUPON_DETAIL_TENDER_COUPON_DNM = "QPON_DNM";
	public static final String FIELD_COUPON_DETAIL_TENDER_COUPON_DNM_QNTY = "QPON_DNM_QNTY";

	public static final String TABLE_LOYALTY_POINTS_DETAIL_TENDER = "MAX_LYPT_DTLS_DSP";;
	public static final String FIELD_LOYALTY_POINTS_DETAIL_TENDER_NAME = "LYPT_NM";
	public static final String FIELD_LOYALTY_POINTS_DETAIL_TENDER_DNM = "LYPT_DNM";
	public static final String FIELD_LOYALTY_POINTS_DETAIL_TENDER_DNM_QNTY = "LYPT_DNM_QNTY";

	/**
	 * Credit Card Table Specs
	 */
	public static final String TABLE_ACQUIRER_BANK_DETAIL = "MAX_ACQUIRER_BANK_RECORD";
	public static final String FIELD_CREDIT_DETAIL_TENDER_BANK_NAME = "BNK_NAME";

	public static final String FIELD_TID = "TID";
	public static final String FIELD_BATCHID = "BATCH_ID";
	public static final String FIELD_AMOUNT = "AMOUNT";

	public static final String TABLE_GIFT_CERTIFICATE_DETAIL = "MAX_GV_DTLS_DSP";
	public static final String FIELD_GIFT_CERTIFICATE_DENOMINATION = "GV_DNM";
	public static final String FIELD_GIFT_CERTIFICATE_QUANTITY = "QUANTITY";

	public static final String TABLE_CASH_DENOMINATION_DETAIL = "MAX_CSH_DTLS_DSP";
	public static final String FIELD_CASH_DENOMINATION = "CSH_DNM";
	public static final String FIELD_CASH_QUANTITY = "CSH_QNTY";
	public static final String FIELD_CASH_DNM_NAME = "NM_DNM";

	// public static final String FIELD_OPERATOR_ID = "ID_OPR";
	public static final String FIELD_OPERATOR_MODIFIER_ID = "ID_USR_MDF";
	public static final String FIELD_CREATION_TIMESTAMP = "TS_CRT_RCRD";
	public static final String FIELD_MODIFICATION_TIMESTAMP = "TS_MDF_RCRD";

	public static final String FIELD_TILL_RECONCILE_FROM_BO = "FL_RCNL_BO";
	public static final String FIELD_ALTER_TILL_RECONCILE = "MAX_ALTER_TL_RECO";

	public static final String FIELD_SUGGESTED_TENDER_SPL_ORD = "SGST_TND_TY";

	public static final String FIELD_TIC_CUSTOMER_ID = "ID_CT_TIC";

	public static final String FIELD_PROMO_DISCOUNT_ON_RECEIPT = "PR_DSC_REC";

	public static final String FIELD_ID_TR_EN = "ID_TR_EN";
	public static final String FIELD_ELG_AMT = "ELG_AMT";
	public static final String FIELD_AVL_AMT = "AVL_AMT";

	public static final String TABLE_TIC_CUSTOMER_CONFIG = "TIC_CT_FLD_DISP";
	public static final String ALIAS_TIC_CUSTOMER_CONFIG = "TIC_CT_FLD_DISP";
	public static final String FIELD_VIEW_FLD = "VIEW_FLD";
	public static final String FIELD_MAND_FLD = "MAND_FLD";

	public static final String TABLE_DISCOUNT_CARD = "DC_MAS";
	public static final String FIELD_SALE_USRID = "SALE_USRID";
	public static final String FIELD_GIFT_VOUCHER_NUMBER = "GVNUMBER";
	public static final String FIELD_CARD_TYPE = "CARD_TYPE";
	public static final String FIELD_RED_CODE = "GV_REDEEM_CODE";

	public static final String FIELD_ECOM_ORDER_NO = "ECOM_ORDER_NO";
	public static final String FIELD_ECOM_ORDER_AMOUNT = "ECOM_ORDER_AMOUNT";
	public static final String FIELD_ECOM_ORDER_TRANS_NO = "ECOM_ORDER_TRANS_NO";
	public static final String FIELD_ECOM_ORDER_TYPE = "ECOM_ORDER_TYPE"; // Changes to save Order type- Karni
				
	public static final String FIELD_CUSTOMER_FIRST_NAME = "FN_CT";
	public static final String FIELD_CUSTOMER_LAST_NAME = "LN_CT";
	public static final String FIELD_CUSTOMER_ID_TYPE = "ID_TY_CT";
	public static final String TABLE_PAYMENTONACCOUNT_UNKNOWN_LINE_ITEM = "TR_LTM_PYAN_UNK";
	public static final String TABLE_SHIPPING_RECORDS_TAX = "SHP_RDS_SLS_RTN_TX";
	public static final String TABLE_INSTANT_CREDIT = "CR_BRN";
	public static final String FIELD_AUTHORIZATION_RESPONSE = "RSPS_AZN";
	public static final String TABLE_IMPORT_BUNDLE_STATUS = "MA_STS_BNDL_IMP";
	public static final String TABLE_IMPORT_FILE_STATUS = "MA_STS_FL_IMP";
	public static final String TABLE_IMPORT_FILE_FAILURES = "MA_FL_IMP_FLRS";
	public static final String FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_CARD_ACCOUNT_HASH = "ID_HSH_ACNT";
	public static final String FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_CARD_EXPIRATION_DATE = "DC_EP_DB_CR_CRD";
	public static final String FIELD_CAPTURE_CUSTOMER_STORE_ID = "ID_STR_RT";
	public static final String FIELD_CAPTURE_CUSTOMER_WS_ID = "ID_WS";
	public static final String FIELD_CAPTURE_CUSTOMER_BUSINESS_DAY = "DC_DY_BSN";
	public static final String FIELD_CAPTURE_CUSTOMER_TRANSACTION_ID = "AI_TRN";
	public static final String FIELD_EMPLOYEE_SOCIAL_SECURITY_NUMBER = "UN_NMB_SCL_SCTY";
	public static final String FIELD_EMPLOYEE_STORE_ID = "ID_STR_RT";
	public static final String FIELD_EVENT_RETAIL_STORE_ID = "ID_STR_RT";
	public static final String FIELD_GIFT_CERTIFICATE_FACE_VALUE_AMOUNT = "MO_VL_FC_GF_CF";
	public static final String FIELD_GIFT_CERTIFICATE_SERIAL_NUMBER = "ID_NMB_SRZ_GF_CF";
	public static final String FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE0 = "ID_STRC_MR_CD0";
	public static final String FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE1 = "ID_STRC_MR_CD1";
	public static final String FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE2 = "ID_STRC_MR_CD2";
	public static final String FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE3 = "ID_STRC_MR_CD3";
	public static final String FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE4 = "ID_STRC_MR_CD4";
	public static final String FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE5 = "ID_STRC_MR_CD5";
	public static final String FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE6 = "ID_STRC_MR_CD6";
	public static final String FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE7 = "ID_STRC_MR_CD7";
	public static final String FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE8 = "ID_STRC_MR_CD8";
	public static final String FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE9 = "ID_STRC_MR_CD9";
	public static final String FIELD_RETAIL_LESS_THAN_MRP_FLAG = "FL_MRP_RT_LT";
	public static final String FIELD_TAX_CATGEORY = "ID_CTGY_TX";
	public static final String FIELD_MULTIPLE_MRP_FLAG = "FL_MRP_MU";
	public static final String FIELD_ITEM_MAINTENANCE_EVENT_RETAIL_STORE_ID = "ID_STR_RT";
	public static final String FIELD_ITEM_PRICE_MAINTENANCE_RETAIL_STORE_ID = "ID_STR_RT";
	public static final String FIELD_MAINTENANCE_EVENT_RETAIL_STORE_ID = "ID_STR_RT";
	public static final String FIELD_MANUFACTURER_NAME = "NM_MF";
	/*
	 * MultipleUnitThreshold
	 */
	public static final String FIELD_THRESHOLD_LEVEL_ID = "ID_LV_TH";
	public static final String FIELD_THRESHOLD_MONETARY_VALUE_AMOUNT = "MO_VL_TH";
	public static final String FIELD_THRESHOLD_PRICE_REDUCTION_MONETARY_AMOUNT = "MO_RDN_TH_PR";
	public static final String FIELD_THRESHOLD_PRICE_REDUCTION_PERCENT = "PE_RDN_TH_PR";
	public static final String FIELD_THRESHOLD_UNIT_COUNT = "QU_CNT_UN";
	public static final String FIELD_ORDER_LINE_REFERENCE = "LN_ITM_REF";
	public static final String FIELD_PERMANENT_PRICE_CHANGE_APPLIED_ON = "TY_PRC_APLY";
	public static final String FIELD_PERMANENT_PRICE_CHANGE_ITEM_RETAIL_STORE_ID = "ID_STR_RT";
	public static final String FIELD_PERMANENT_PRICE_CHANGE_ITEM_MAXIMUM_RETAIL_PRICE = "RP_MXM";
	public static final String FIELD_ORDER_DOCUMENT_RETAIL_STORE_ID = "ID_STR_RT";
	public static final String FIELD_MAXIMUM_RETAIL_PRICE = "PRC_MXM_RT";
	public static final String FIELD_TRANSACTION_OFF_TOTAL = "MO_OFF_TOT"; // TransactionTotals.offTotal
	public static final String FIELD_ORDER_REFERENCE_ID = "OR_ID_REF";
	public static final String FIELD_SUPPLY_ORDER_STORE_ID = "ID_STR_RT";
	public static final String FIELD_SUPPLY_ORDER_BUSINESS_DATE = "DC_DY_BSN";
	public static final String FIELD_SUPPLY_STORE_ID = "ID_STR_RT";
	public static final String FIELD_STORE_SUPPLY_ITEM_STORE_ID = "ID_STR_RT";
	public static final String FIELD_TEMPORARY_PRICE_CHANGE_RETAIL_STORE_ID = "ID_STR_RT";
	public static final String FIELD_TEMPORARY_PRICE_CHANGE_APPLIED_ON = "TY_PRM_APLY";
	public static final String FIELD_TEMPORARY_PRICE_CHANGE_ITEM_MAXIMUM_RETAIL_PRICE = "RP_MXM";
	public static final String FIELD_TEMPORARY_PRICE_CHANGE_ITEM_RETAIL_STORE_ID = "ID_STR_RT";
	public static final String FIELD_TIME_DATE_PRICE_DERIVATION_RULE_EXPIRATION_TIME = "TM_RU_PRDV_EP";
	public static final String FIELD_STORE_CREDIT_STATUS = "SC_CR_STR";
	public static final String FIELD_AUDIT_LOG_STORE_ID = "ID_STR_RT";
	public static final String FIELD_POS_DEPARTMENT_SUMMARY_RETAIL_STORE_ID = "ID_STR_RT";
	public static final String FIELD_IDDI_STORE_ID = "ID_STR_RT";
	public static final String ALIAS_AS_ITM = "AI";
	/* India Localization - Tax Changes Starts Here */
	/*
	 * Tax Assignment Table
	 */
	public static final String ALIAS_TAX_ASSIGNMENT = "TA";
	public static final String TABLE_AS_ITM = "AS_ITM";
	public static final String TABLE_TAX_ASSIGNMENT = "TX_ASGMT";
	public static final String TABLE_TAX_LINE_ITEM_BREAKUP = "TR_LTM_TX_BRKUP";

	public static final String ALIAS_MAXIMUM_RETAIL_PRICE_CHANGE_ITEM = "MRPCHANGE";
	public static final String TABLE_MAXIMUM_RETAIL_PRICE_CHANGE_ITEM = "MA_ITM_MRP_CHN";

	public static final String FIELD_ID_ITM = "ID_ITM";
	public static final String FIELD_ID_CTGY_TX = "ID_CTGY_TX";
	public static final String FIELD_TX_CD = "TX_CD";
	public static final String FIELD_TX_RT = "TX_RT";
	public static final String FIELD_TXBL_FCT = "TXBL_FCT";
	public static final String FIELD_TX_FCT = "TX_FCT";
	public static final String FIELD_TX_CD_DSCR = "TX_CD_DSCR";
	public static final String FIELD_APLY_ON = "APLY_ON";
	public static final String FIELD_APLN_ORD = "APLN_ORD";

	/*
	 * Line Item Tax Break Up Details
	 */
	public static final String FIELD_TAX_BREAKUP_CODE = "TX_BRKUP_TX_CD";
	public static final String FIELD_TAX_BREAKUP_CODE_DESC = "TX_BRKUP_TX_CD_DSCR";
	public static final String FIELD_TAX_BREAKUP_RATE = "TX_BRKUP_TX_RT";
	public static final String FIELD_TAX_BREAKUP_TAX_AMOUNT = "TX_BRKUP_TX_AMT";
	public static final String FIELD_TAX_BREAKUP_TAXABLE_AMOUNT = "TX_BRKUP_TXBL_AMT";
	public static final String FIELD_SALE_RETURN_LINE_ITEM_MRP_AMOUNT = "MO_MRP_LN_ITM_RTN";
	public static final String FIELD_LINE_ITEMTAX_SEQUENCE_NUMBER = "AI_TRN";
	/* MaximumRetailPriceChange */

	public static final String FIELD_MRP_ACTIVATION_DATE = "TS_PRC_MXM_RT_EF";
	public static final String FIELD_MRP_INACTIVATION_DATE = "TS_PRC_MXM_RT_EP";
	public static final String FIELD_MRP_PRIMARY_STATUS_CODE = "FL_PRMRY_PRC_MXM_RT";
	public static final String FIELD_MRP_ACTIVE_CODE = "FL_ACTV_PRC_MXM_RT";

	/* MaximumRetailPriceChangeItem */
	public static final String FIELD_MAXIMUM_RETAIL_PRICE_CHANGE_RETAIL_STORE_ID = "ID_STR_RT";
	public static final String FIELD_MAXIMUM_RETAIL_PRICE_CHANGE_ITEM_ID = "ID_ITM";
	public static final String FIELD_MAXIMUM_RETAIL_PRICE_CHANGE_MRP = "RP_MXM";
	public static final String FIELD_MAXIMUM_RETAIL_PRICE_CHANGE_MRP_ACTIVATION_DATE = "TS_PRC_MXM_RT_EF";
	public static final String FIELD_MAXIMUM_RETAIL_PRICE_CHANGE_MRP_INACTIVATION_DATE = "TS_PRC_MXM_RT_EP";
	public static final String FIELD_MAXIMUM_RETAIL_PRICE_CHANGE_PRIMARY_MRP_STATUS_CODE = "FL_PRMRY_PRC_MXM_RT";
	public static final String FIELD_MAXIMUM_RETAIL_PRICE_CHANGE_ACTIVE_MRP_STATUS_CODE = "FL_ACTV_PRC_MXM_RT";
	public static final String FIELD_ITEM_QUANTITY_PICKED = "QU_ITM_PCK";
	/* India Localization Changes - Tax Changes Ends Here */

	public static final String FIELD_TAX_INC_EX = "TAX_INC_EX";
	public static final String FIELD_VAT_EXCLUSIVE = "VAT_EX";
	public static final String FIELD_ID_RN_FM_TX = "ID_RN_FM_TX";
	public static final String FIELD_FROM_THRESHOLD = "MAX_TX_FRM_AMT";
	public static final String FIELD_TO_THRESHOLD = "MAX_TX_TO_AMT";
	// Changes for Rev 1.0 : Ends

	// Vat Extra
	public static final String FIELD_VAT_EXTRA = "FL_VAT_EXTRA";
	// vat Collection
	public static final String FIELD_VAT_COLLECTION_FLAG = "FL_VAT_COLLECTION";
	public static final String FIELD_VAT_COLLECTION_AMOUNT = "AMT_VAT_COLLECTION";
	// commented for rev 1.2
	// public static final String FIELD_CUSTOMER_TYPE = "TY_CUST";
	// Changes for Rev 1.1 : Starts
	public static final String FIELD_ITEM_GROUP_TYPE = "ITEM_GRP_TYPE";
	public static final String FIELD_RULE_QUANTITY = "RULE_QTY";
	// Changes for Rev 1.1 : Ends
	public static final String FIELD_GIFT_CARD_SERIAL_NUMBER_OLD = "ID_NMB_SRZ_GF_CRD";
	// Change for Rev 1.3 : Starts
	public static final String FIELD_TAX_DIFF = "TX_DIFF";
	public static final String FIELD_TAX_TO_REGION = "ID_RN_TO_TX";
	public static final String FIELD_TAX_FROM_REGION = "ID_RN_FM_TX";
	public static final String FIELD_GST_ENABLED = "GST_Enable";
	public static final String ALIAS_GST_REG_MAP = "state";
	public static final String TABLE_GST_REG_MAP = "MAX_GST_REG_MAP";
	public static final String FIELD_REGION_CODE = "GST_REG_CODE";
	public static final String FIELD_REGION_DESC = "GST_REG_DESC";
	// public static final String TABLE_LY_ITM_TX = "LS_LY_ITM_TX";
	public static final String FIELD_TAXABLE_AMOUNT = "TXBL_AMT";
	public static final String FIELD_TAX_AMT = "TX_AMT";
	// Change for Rev 1.3 : Ends
	public static final String TABLE_MOBIKWIK_WEB_REQUEST_LOG = "MOBIKWIK_WEB_REQUEST_LOG";
	/** MESSAGE ID */
	public static final String FIELD_MOBIKWIK_MESSAGE_ID = "ID_MESSAGE";
	/** REQUEST_TYPE */
	public static final String FIELD_MOBIKWIK_REQUEST_TYPE_A = "TY_RQST_A";
	/** REQUEST STATUS */
	public static final String FIELD_MOBIKWIK_REQUEST_STATUS = "STS_RQST";
	/** STORE ID */
	public static final String FIELD_MOBIKWIK_STORE_ID = "ID_STR_RT";
	/** TILL ID */
	public static final String FIELD_MOBIKWIK_TILL_ID = "ID_TL";
	/** REGISTER ID */
	public static final String FIELD_MOBIKWIK_REGISTER_ID = "ID_WS";
	/** INVOICE BUSINESS DATE TIME */
	public static final String FIELD_MOBIKWIK_INVOICE_BUSINESS_DATE = "DC_DY_BSN";
	/** INVOICE NUMBER */
	public static final String FIELD_MOBIKWIK_INVOICE_NUMBER = "NM_INV";
	/** TIC NUMBER */
	// public static final String FIELD_MOBIKWIK_TIC_NUMBER = "NM_TIC";
	public static final String FIELD_MOBIKWIK_PHONE_NUMBER = "NM_TIC";
	/** TRANSACTION TOTAL AMOUNT */
	public static final String FIELD_MOBIKWIK_TRANS_TOTAL_AMT = "AMT_TOT_TRN";
	/** SETTLE TOTAL AMOUNT */
	public static final String FIELD_MOBIKWIK_SETTLE_TOTAL_AMT = "AMT_TOT_STL";
	/** REQUEST TYPE B */
	public static final String FIELD_MOBIKWIK_REQUEST_TYPE_B = "TY_RQST_B";
	/** REQUEST DATE */
	public static final String FIELD_MOBIKWIK_REQUEST_DATE = "DATE_RQST";
	/** REQUEST TIME OUT */
	public static final String FIELD_MOBIKWIK_REQUEST_TIME_OUT = "OUT_TIME_RQST";
	/** REQUEST URL */
	public static final String FIELD_MOBIKWIK_REQUEST_URL = "URL_RQST";
	/** RESPONSE APPROVED FLAG */
	public static final String FIELD_MOBIKWIK_RESPONSE_APPROVED_FLAG = "FL_APR_RSP";
	/** RESPONSE APPROVED VALUE */
	public static final String FIELD_MOBIKWIK_RESPONSE_APPROVED_VALUE = "VL_APR_RSP";
	/** RESPONSE APPROVED VALUE */
	public static final String FIELD_MOBIKWIK_RESPONSE_MESSAGE = "MSG_RSP";
	/** RESPONSE APPROVED VALUE */
	public static final String FIELD_MOBIKWIK_RESPONSE_RECEIVED_DATE_TIME = "TS_DT_RCV_RSP";
	/** TIME OUT MESSAGE ID **/
	public static final String FIELD_MOBIKWIK_ID_MESSAGE_TYPMEOUT = "ID_MESSAGE_TYPMEOUT";

	// Changes Start by bhanu Priya
	public static final String TABLE_WALLET_TENDER_LINE_ITEM = "TR_LTM_WALLET_TND";
	// public static final String FIELD_TENDERLINE_TYPE_CODE = "AI_LN_ITM";
	public static final String FIELD_MOBILE_NUMBER = "MOB_NO";
	public static final String FIELD_WALLET_ORDER_ID = "WALLET_ORDER_ID";
	public static final String FIELD_TRANS_ID = "TRANS_ID";

	// Changes End by bhanu Priya
	// below code is added by atul shukla for employee discount
	public static final String FIELD_COMPANY_NAME = "COMPANY_NAME";
	/* //changes for Rev 1.4 start */
	// /Mcoupon enable /Disable flag::

	public static final String FIELD_ISSUE_COUPN_CAPILLARY = "FL_ISSUE_CAPL_CPN";

	// //mcoupon table :::
	public static final String TABLE_MAX_CAPILLARY_ISSUED_CPNS = "MAX_CAPILLARY_ISSUED_CPNS";
	public static final String FIELD_ID_STR_RT = "ID_STR_RT";
	public static final String FIELD_MCOUPON_ISSUE_DATE = "ISSUE_DATE";
	public static final String FIELD_ID_WS = "ID_WS";
	public static final String FIELD_AI_TRN = "AI_TRN";
	public static final String FIELD_CPN_NO = "CPN_NO";
	public static final String FIELD_REQ_STAT = "REQ_STAT";

	public static final String FIELD_CAPILLARY_REQ_STATUS = "CAPILLARY_REQ_STATUS";
	public static final String FIELD_CAPILLARY_MESSAGE_STATUS = "CAPILLARY_STATUS_MESSAGE";

	/*
	 * public static final String FIELD_AUTHORIZATION_CODE = "CD_AZN"; public
	 * static final String FIELD_BANK_CODE = "CD_BK"; public static final String
	 * FIELD_TERMINAL_ID_INNOVITI = "TID"; public static final String
	 * FIELD_BATCH_NUMBER_INNOVITI = "TR_BTCH_ID"; public static final String
	 * FIELD_INVOICE_ID = "TR_INVC_ID";
	 */
	public static final String FIELD_MERCHANT_ID_INNOVITI = "MID";
	public static final String FIELD_TRANSACTION_APPROVAL_TIME = "TS_CMPL_RCRD";
	public static final String FIELD_HOST_RESPONSE_ID = "TR_HS_RSP_CD";
	public static final String FIELD_HOST_RESPONSE_MESSAGE = "TR_HS_RSP_MSG";
	public static final String FIELD_APPROVAL_ID = "TR_APPR_CD";
	public static final String FIELD_RETURN_REFERENCE_NUMBER = "TR_RTN_REF_ID";
	public static final String FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_CARD_ACCOUNT_NUMBER = "ID_ACNT_DB_CR_CRD";
	public static final String FIELD_MASKED_ACC_ID = "ID_MSK_ACNT_CRD";
	/* //changes for Rev 1.4 end */
	
	//Changes starts for Rev 1.5 (Vaibhav : PineLab)
	public static final String FIELD_CRDB_DEVICE_DETAIL = "CRDB_DVC_DTLS";
	//Changes starts for Rev 1.5 (Vaibhav : PineLab)

	// PAN Changes

	public static final String TABLE_TRAN_PAN_DETAILS = "MAX_TRANS_PAN_DTLS";
	public static final String ALIAS_TRAN_PAN_DETAILS = "TR_PAN";
	public static final String FIELD_PAN_NUM = "PAN_NUM";
	public static final String FIELD_FORM60_IDNUM = "FORM60_IDNUM";
	public static final String FIELD_PASSPORT_NUM = "PASSPORT_NUM";
	public static final String FIELD_VISA_NUM = "VISA_NUM";
	public static final String FIELD_ITRACK_NUM = "ITR_ACKNUM";
	
	public static final String FIELD_ERECEIPT_OTP = "ERCPT_OTP";
	
	/** CHANGES FOR GST CODE BY ANUJ */
	
	
	/*
	 * public static final String TABLE_RETAIL_STORE = "RETAIL_STORE"; public static
	 * final String FIELD_ID_IDTN_TX_NMB1 = "ID_IDTN_TX_NMB1"; public static final
	 * String FIELD_RETAIL_STORE_ID = "RETAIL_STORE_ID";
	 * 
	 */
	
	 public static final String FIELD_SBI_POINT_CON = "SBI_POINT_CON";
     public static final String FIELD_SBI_POINT_MIN = "SBI_POINT_MIN";
     public static final String FIELD_SBI_LOYALTY_CON_RATE = "SBI_LOYALTY_CON_RATE";
     public static final String ALIAS_CONFIG_PARAMETER = "config_parameter";
 	public static final String TABLE_CONFIG_PARAMETER = "config_parameter";
 	public static final String FIELD_TIC_DISC_START_DATE = "TIC_STR_DT";
	public static final String FIELD_TIC_DISC_END_DATE = "TIC_END_DT";
	/** changes for gst code */
	public static final String TABLE_GSTIN_E_INVOICE_DETAILS = "GSTIN_E_INVOICE_DETAILS";
	public static final String FIELD_CO_TRANSFER_STATUS = "CO_TRANSFER_STATUS";
	public static final String TABLE_GSTIN_STATE_MASTER ="GSTIN_STATE_MASTER";
	public static final String FIELD_STATE_CODE	="STATE_CODE";
	public static final String FIELD_STATE_NAME	="STATE_NAME";
	public static final String FIELD_STATE_NM_CD="STATE_NM_CD";
	public static final String TABLE_GSTIN_STORE_DETAILS="GSTIN_STORE_DETAILS";
	public static final String FIELD_STORE_GSTIN="STORE_GSTIN";
	public static final String FIELD_ID_IDTN_TX_NMB1  ="ID_IDTN_TX_NMB1";
	
	
	/**CHANGES BY ANUJ SINGH*/
	public static final String TABLE_RETAIL_STORE_GSTIN = "RETAIL_STORE";
	public static final String FIELD_ID_IDTN_TX_NMB2 = "ID_IDTN_TX_NMB1";
	public static final String FIELD_RETAIL_STORE_ID_1 = "RETAIL_STORE_ID";
	
	

	public static final String FIELD_LEGAL_NAME="LEGAL_NAME";
	
	public static final String FIELD_STATE_JURIDICTION_CD="STATE_JURIDICTION_CD";
	
	public static final String FIELD_TAXPAYER_TYPE="TAXPAYER_TYPE";
	
	public static final String FIELD_DATE_OF_CANCEL="DATE_OF_CANCEL";
	
	public static final String FIELD_BUILDING_NM="BUILDING_NM";
	
	public static final String FIELD_STREET="STREET";
	
	public static final String FIELD_LOCALITY="LOCALITY";
	
	public static final String FIELD_BUILDING_NO="BUILDING_NO";
	
	public static final String FIELD_STATE="STATE";
	
	public static final String FIELD_CITY="CITY";
	
	public static final String FIELD_DISTRICT="DISTRICT";
	
	public static final String FIELD_FLOOR_NO="FLOOR_NO";
	
	public static final String FIELD_LATITUTE="LATITUTE";
	
	public static final String FIELD_PIN_CODE="PIN_CODE";
	
	public static final String FIELD_LONGTITUE="LONGTITUTE";
	
	public static final String FIELD_LAST_UPDATED="LAST_UPDATED";
	
	public static final String FIELD_REGISTRATION_DATE="REGISTRATION_DATE";
	
	public static final String FIELD_BUSINESS_CONSTITUTION="BUSINESS_CONSTITUTION";
	
	public static final String FIELD_GSTN_STATUS="GSTN_STATUS";
	
	public static final String FIELD_CENTR_JURISDICTION_CD="CENTR_JURISDICTION_CD";
	
	public static final String FIELD_CENTR_JURISDICTION_NM="CENTR_JURISDICTION_NM";
	
	public static final String FIELD_REGISTR_TRADE_NAME="REGISTR_TRADE_NAME";
	public static final String FIELD_TS_CRT_RCRD="TS_CRT_RCRD";
	
	public static final String FIELD_TS_MDF_RCRD="TS_MDF_RCRD";
	public static final String TABLE_GSTIN_SERVER_DETAILS="GSTIN_SERVER_DETAILS ";
    public static final String FIELD_PARAM_NM="PARAM_NM";
	
	public static final String FIELD_PARAM_VL="PARAM_VL";
	
	public static final String TABLE_GSTIN_CUSTOMER_DETAILS ="GSTIN_CUSTOMER_DETAILS ";
	
	public static final String FIELD_DC_DY_BSN="DC_DY_BSN";

	
	public static final String FIELD_TY_TRN="TY_TRN";
	
	public static final String FIELD_CUST_GSTIN="CUST_GSTIN";
	
	public static final String FIELD_SC_TRN="SC_TRN";
	
	public static final String FIELD_CO_TRANSFER_RETRY_COUNT="CO_TRANSFER_RETRY_COUNT";
	
	public static final String FIELD_CO_TRANSFER_ERROR_LOG="CO_TRANSFER_ERROR_LOG";
	
	public static final String FIELD_GET_INVOICE_STATUS="GATE_INVOICE_STATUS";
	
	public static final String FIELD_GET_EINVOICE_STATUS="GET_INVOICE_STATUS";
	
	public static final String FIELD_INVOICE_REQUEST="INVOICE_REQUEST";
	
	public static final String FIELD_INVOICE_REFERENCE_ID="INVOICE_REFERENCE_ID";
	
	public static final String FIELD_INVOICE_DOCUMENT_NO="INVOICE_DOCUMENT_NO";
	
	public static final String FIELD_GET_INVOICE_ERROR="GET_INVOICE_ERROR";
	
	public static final String FIELD_INVOICE_ACK_NO="INVOICE_ACK_NO";
	
	public static final String FIELD_INVOICE_ACKDATE="INVOICE_ACKDATE";
	
	public static final String FIELD_INVOICE_IRN="INVOICE_IRN";
	
	public static final String FIELD_INVOICE_SIGNED="INVOICE_SIGNED";
	
	public static final String FIELD_INVOICE_SIGNED_QRCODE="INVOICE_SIGNED_QRCODE";
	
	public static final String FIELD_INVOICE_QRCODE="INVOICE_QRCODE";
	
	public static final String FIELD_INVOICE_QRCODE_DATA="INVOICE_QRCODE_DATA";
	public static final String FIELD_TRANSFER_RETRY_COUNT = null;
	
	// gstin changes start here
	
	
	public static final String FIELD_TRANSFER_ERROR_LOG = "CO_TRANSFER_ERROR_LOG";
	
	public static final String FIELD_TRANSFER_STATUS = "CO_TRANSFER_STATUS";
	public static final String FIELD_INVOICE_STATUS = "GET_INVOICE_STATUS";
	public static final String FIELD_INVOICE_ERROR = "GET_INVOICE_ERROR";
	
	public static final String FIELD_INVOICE_TO_GSTIN = "CUST_GSTIN";
	public static final String FIELD_INVOICE_FROM_GSTIN = "STORE_GSTIN";
	public static final String FIELD_GST_REQUEST = "INVOICE_REQUEST";
	
	public static final String FIELD_STATE_JURISDICTION_CD  ="STATE_JURISDICTION_CD";
	
	public static final String FIELD_LONGTITUTE  ="LONGTITUTE";   
	/**/
	public static final String ENABLE_GSTIN_INVOICE = "ENABLE_GSTIN_INVOICE";
	
	public static final String TABLE_GSTIN_DETAILS = "GSTIN_SERVER_DETAILS";
	public static final String ALIAS_GSTIN_CONFIG = "GSTIN";
	public static final String FIELD_PARAM_VALUE = "PARAM_VL";
	
		// gstin changes end here
			public static final String TABLE_SUB_INV_REQ_REP = "SUB_INV_REQ_REP";
		
		public static final String FIELD_SUB_INV_REQ = "SUB_INV_REQ";
		public static final String FIELD_SUB_INV_REP = "SUB_INV_REP";
		//Changes for SubmitInvoice req-res end 1.6
		
		//Rev 1.7 start
        public static final String FIELD_PAYTMQR_STATUS_RETRY_COUNT = "PAYTMQR_STATUS_RETRY_COUNT";        
        //Rev 1.7 end
			// Changes for Manager Override Report Requirement - starts
		public static final String TABLE_RETAIL_TRANSACTION_MANAGER_OVERRIDE = "TR_LTM_EM_OVRD";
		public static final String FIELD_RETAIL_STOREID = "ID_STR_RT";
		public static final String FIELD_MGR_WORKSTATION_ID = "ID_WS";
		public static final String FIELD_BUSINESS_DATE = "DC_DY_BSN";
		public static final String FIELD_TRANSACTION_ID = "AI_TRN";
		public static final String FIELD_MANAGER_OVERRIDE_LINE_ITEM_SEQUENCE_NUMBER = "AI_LN_OVRD";
		public static final String FIELD_OVERRIDE_AUTHORIZING_EMPLOYEEID = "ID_EM_AZN_OVRD";
		public static final String FIELD_OVERRIDE_FEATURE_ID = "ID_RS";
		public static final String FIELD_MGR_STORE_CREDIT_ID = "ID_CR_STR";
		public static final String FIELD_MGR_ITEM_ID = "ID_ITM";
		public static final String FIELD_MGR_RECORD_CREATION_TIMESTAMP = "TS_CRT_RCRD";
		public static final String FIELD_MGR_RECORD_LAST_MODIFIED_TIMESTAMP = "TS_MDF_RCRD";
		public static final String FIELD_CASHIER_ID = "ID_OPR";
		public static final String FIELD_AMOUNT_MO = "AMT_MO";
		public static final String FIELD_LMR_ID = "LMR_ID";
		public static final String FIELD_SUBMIT_INV_FLAG = "SUB_INV_FL";
		
		//Changes Starts by Kamlesh Pant for SpecialEmpDiscount
		public static final String TABLE_SPECIAL_EMP_PRICE = "EMP_PRM";
		public static final String ALIAS_SPECIAL_EMP_PRICE = "EMP_PRM";  
		public static final String FIELD_SPECIAL_EMP_DISCOUNT = "SPL_INSTRC";  
		public static final String FIELD_SPCL_EMP_DISC = "SPCL_EMP_DISC";
		
		public static final String FIELD_CASH_LIMIT_PARAMETER  ="CASH_LIMIT_PARAMETER";
		
		//Added by Vaibhav for liquidation report
		public static final String TABLE_LIQUIDATION_REPORT = "LIQ_REP";
		public static final String FIELD_LIQ_BARCODE = "LIQ_BARCODE";
		public static final String FIELD_ITEM_PRICE = "ITM_PRC";
		
		
		//Added by Kumar Vaibhav for Employee Discount OTP
		public static final String FIELD_FL_EMP_OTP = "FL_EMP_OTP";
		
		public static final String ALIAS_PRICE_DERIVATION_RULE_ELIGIBILITY = "RPRDVITM";
			
		
		//Rev 1.6 Starts
				public static final String FIELD_SC_OTP_RETRIES = "SC_OTP_RETRIES";
				public static final String FIELD_CUST_MOBILE_NUM_SC = "CUST_MOBILE_NUM";
				//Rev 1.6 Ends
					
}
