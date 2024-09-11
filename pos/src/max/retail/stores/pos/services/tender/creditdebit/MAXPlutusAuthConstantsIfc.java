/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  Rev 1.0  22/May/2013	Jyoti Rawal, Initial Draft: Changes for Credit Card Functionality 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

public interface MAXPlutusAuthConstantsIfc {

	// Error String From Plutus Response
	public static String ERROR_INVALID_EXPIRY_DATE = "Invalid expiry date";
	public static String ERROR_CONNECT_FAILED = "Could not connect to the Acquirer Host";
	public static String FAILD_PROCESS_TRANSACTION = "Failed To Process Transaction. Please Retry ";
	public static String ERROR_NOT_INITIALIZED = "Application not initialized";
	public static String ERROR_BANK_AUTHORIZATION_FAIL = "Bank Authorization fail";
	public static String ERROR_TRANS_NOT_ALLOWED = "Transaction not allowed";
	// Added new Plutus Error Code
	public static String ERROR_REFUND_TRANS_NOT_ALLOWED = "Refund transaction not allowed on this card";

	public static String INVALID_OR_UNSUPPORTED_CARD ="Invalid or unsupported card";

	public static String RESPONSE_APPROVED = "APPROVED";
	public static String RESPONSE_DECLINED = "DECLINED";
	public static String AUTH_REMARK_PROCESSED = "PROCESSED";
	// Last four digit
	public static Object LAST_FOUR_DIGITS = "LASTDIGIT";
	
	public static String ACQUIRER_BANK_CODE = "ACQUIRER_BANK_CODE";
	
	public static String TRANSACTION_ACQ_NAME = "TRANSACTION_ACQ_NAME";
	
	public static String MERCHANT_ID = "MERCHANT_ID";
	public static String RETRIEVAL_REF_NUMBER = "RETRIEVAL_REF_NUMBER";
	public static String CARD_ENTRY_MODE = "CARD_ENTRY_MODE";
	public static String PRINT_CARDHOLDER_NAME = "PRINT_CARDHOLDER_NAME";
	public static String MERCHANT_NAME = "MERCHANT_NAME";
	public static String MERCHANT_ADDRESS = "MERCHANT_ADDRESS";
	public static String MERCHANT_CITY = "MERCHANT_CITY";
	public static String PLUTUS_VER = "PLUTUS_VER";
	public static String TOT_NUMBER_EMI = "TOT_NUMBER_EMI";
	public static String EMI_FEE_PERCENT = "EMI_FEE_PERCENT";
	public static String EMI_FEE_RUPEES = "EMI_FEE_RUPEES";
	public static String EMI_INT_RATE = "EMI_INT_RATE";
	public static String NUMBER_OF_ADV_EMI = "NUMBER_OF_ADV_EMI";
	public static String TRANSACTION_TYPE = "TRANSACTION_TYPE";
	public static String CARD_NUMBER = "CARD_NUMBER";
	public static String EXPIRATION_DATE = "EXPIRATION_DATE";
	public static String CARDHOLDER_NAME = "CARDHOLDER_NAME";
	public static String CARD_TYPE = "CARD_TYPE";
	public static String INVOICE_NUMBER = "INVOICE_NUMBER";
	public static String BATCH_NUMBER = "BATCH_NUMBER";
	public static String TERMINAL_ID = "TERMINAL_ID";
}
