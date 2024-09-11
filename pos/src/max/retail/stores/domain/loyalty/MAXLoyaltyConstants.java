/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Jan 06, 2017		Ashish Yadav		Changes for Online redemption loyalty OTP FES	
 *
 ********************************************************************************/

package max.retail.stores.domain.loyalty;

public class MAXLoyaltyConstants {
	public static final String STORE_ID = "aStoId";

	public static final String TILL_ID = "aTilId";

	public static final String REGISTER_ID = "aRegId";

	public static final String INVOICE_BUSINESS_DATE = "aBusDt";

	public static final String INVOICE_NUMBER = "aTraNo";

	public static final String LOYALTY_CARD_NUMBER = "aLoyNo";

	public static final String SETTLE_TOTAL_AMOUNT = "aRedAt";

	public static final String TRAN_TOTAL_AMOUNT = "aTraTo";

	public static final String REQUEST_TYPE_A = "aReqT1";

	public static final String REGULAR_REQUEST = "R";

	public static final String TIMEOUT_REQUEST = "T";

	public static final String REQUEST_STATUS = "aReqSt";

	public static final String REQUESTED = "R";

	public static final String TIMEOUT = "T";

	public static final String RESPONSE_RECEIVED = "RR";

	public static final String REQUEST_DATE_TIME = "aReqTp";

	public static final String MESSAGE_ID = "aMsgId";

	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	public static final String NEW_DATE_FORMAT = "yyyyMMddHHmmss";

	public static final String TIME_OUT_REQUEST_MESSAGE_ID = "aTimeoutmsgid";

	public static final String REQUEST_TYPE_B = "aReqT2";

	public static final String BLOCK = "B";

	public static final String RELEASE = "R";

	public static final String REQUEST_TIME_OUT = "aTimOt";

	public static final String REQUEST_URL = "aReqURL";

	public static final String FLAG = "flag";

	public static final String TIMEOUT_FLAG = "TIMEOUT";

	public static final String RESPONSE_FLAG = "RESPONSE";

	public static final String SUCCESS = "S";

	public static final String FAIL = "F";

	public static final String PARTIAL_SUCCESS = "P";

	public static final String RESPONSE_APPROVED_FLAG = "Response";

	public static final String RESPONSE_APPROVED_VALUE = "Points";

	public static final String RESPONSE_MESSAGE = "Message";

	public static final String RESPONSE_DATE_TIME = "RESPONSE_DATE_TIME";

	// Changes starts for Rev 1.0 (Ashish :Loyalty OTP)
	public static final String RESPONSE = "Response";
	public static final String SEND_SMS = "sendSMS";
	public static final String OTP_MESSAGE_ID = "messageId";
	public static final String OTP_TIME_OUT_REQUEST_MESSAGE_ID = "timeoutMessageId";
	public static final String OTP_REQUEST_TYPE_B = "requestType";
	public static final String OTP_STORE_ID = "storeId";
	public static final String OTP_TILL_ID = "tillId";
	public static final String OTP_INVOICE_BUSINESS_DATE = "invoiceDate";
	public static final String OTP_INVOICE_NUMBER = "invoiceNumber";
	public static final String OTP_TRAN_TOTAL_AMOUNT = "invoiceAmount";
	public static final String OTP_SETTLE_TOTAL_AMOUNT = "redeemAmount";
	public static final String OTP_LOYALTY_CARD_NUMBER = "cardNumber";
	// Changes ends for Rev 1.0 (Ashish :Loyalty OTP)
}
