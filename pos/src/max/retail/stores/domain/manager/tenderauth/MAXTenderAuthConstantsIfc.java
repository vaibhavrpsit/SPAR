/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/tenderauth/MAXTenderAuthConstantsIfc.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.manager.tenderauth;

public abstract interface MAXTenderAuthConstantsIfc {
	public static final int CREDIT = 1;
	public static final int CHECK = 2;
	public static final int DEBIT = 3;
	public static final int GIFT_CARD = 4;
	public static final int HOUSE_ACCOUNT = 5;
	public static final int ITEM_ACTIVATION = 6;
	public static final int INSTANT_CREDIT = 7;
	public static final String CREDIT_TYPE = "01";
	public static final String DEBIT_TYPE = "03";
	public static final String CHECK_TYPE = "02";
	public static final String GIFT_CARD_TYPE = "04";
	public static final String HOUSE_ACCOUNT_TYPE = "05";
	public static final String ITEM_ACTIVATION_TYPE = "06";
	public static final String INSTANT_CREDIT_TYPE = "07";
	public static final String BOSE_CREDITAPP_TYPE = "08";
	public static final int UNUSED = 0;
	public static final int PROCESS = 1;
	public static final int DISCARD = 2;
	public static final int TRANS_SALE = 1;
	public static final int TRANS_VOID = 2;
	public static final int TRANS_CREDIT = 3;
	public static final int TRANS_CREDIT_VOID = 4;
	public static final int TRANS_AUTH_ONLY = 5;
	public static final int TRANS_FORCE = 6;
	public static final int TRANS_SETTLEMENT = 7;
	public static final int TRANS_GUARANTEE_DL = 11;
	public static final int TRANS_GUARANTEE_MICR = 12;
	public static final int TRANS_GUARANTEE_DL_AND_MICR = 13;
	public static final int MAX_TRANS_CONSTANT = 13;
	public static final String APPROVED = "00";
	public static final String DECLINED = "02";
	public static final String REFERRAL = "03";
	public static final String POSITIVE_ID = "04";
	public static final String TIMEOUT = "08";
	public static final String OFFLINE = "09";
	public static final String INVALID_PIN = "10";
	public static final String DECLINED_PARTIAL = "11";
	public static final String CHECK_VELOCITY = "12";
	public static final String ERROR_RETRY = "13";
	public static final String FIRST_TIME_USAGE = "14";
	public static final String ACTIVE = "20";
	public static final String INACTIVE = "21";
	public static final String INVALID = "22";
	public static final String EXPIRED = "23";
	public static final String DUPLICATE = "24";
	public static final String EXPENDED = "25";
	public static final String UNKNOWN = "26";
	public static final String RELOAD = "27";
	public static final String INVALID_TRANS = "28";
	public static final String CARD_NUM_ERROR = "29";
	public static final String NO_MORE_LOADS_ALLOWED = "30";
	public static final String TERM_ID_ERROR = "31";
	public static final String APPROVAL_SPLIT_TENDER = "32";
	public static final String CALL_CENTER = "33";
	public static final String NOTFOUND = "34";
	public static final String HOLD_CALL = "35";
	public static final String INVALID_MERCH_CALL = "37";
	public static final String MAX_PIN_TRY_DECLINE = "38";
	public static final String GIFT_CARD_INQUIRY_FOR_TENDER_FAILED = "39";
	public static final String[] RESPONSE_CODE_DESCRIPTORS = { "Approved", "Undefined", "Declined", "Referral",
			"Positive ID", "Undefined", "Undefined", "Undefined", "Timeout", "Offline", "Invalid PIN",
			"Declined Partial", "Check Velocity", "Error/Retry", "First Time Usage", "Undefined", "Undefined",
			"Undefined", "Undefined", "Undefined", "Active", "Deactive", "Invalid", "Expired", "Duplicate", "Expended",
			"Unknown", "Reload", "InvalidTrans", "CardNumError", "NoMoreLoadsAllowed", "TermIdError",
			"ApprovalSplitTender", "CallCenter", "NotFound", "HoldCall", "Undefined", "InvalidMerchCall",
			"MaxPinTryDecline", "GCardInquiryForTenedrFailed" };
	public static final int NO_ACTION = 0;
	public static final int HOLDCARD = 1;
	public static final int RETRY = 2;
	public static final int ACTIVATE = 10;
	public static final int DEACTIVATE = 11;
	public static final int INQUIRY = 12;
	public static final int REDEEM = 13;
	public static final int RELOAD_GIFT_CARD = 14;

	/** @deprecated */
	public static final int MANUALLY_ACTIVATE = 15;

	/** @deprecated */
	public static final int MANUALLY_DEACTIVATE = 16;

	/** @deprecated */
	public static final int MANUALLY_INQUIRY = 17;

	/** @deprecated */
	public static final int MANUALLY_RELOAD = 18;
	public static final int RELOAD_VOID = 18;
	public static final int REDEEM_VOID = 21;
	public static final String MANUAL = "Manual";
	public static final String AUTOMATIC = "Auto";
}
