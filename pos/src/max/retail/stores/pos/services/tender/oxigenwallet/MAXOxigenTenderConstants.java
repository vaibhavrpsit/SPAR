/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2016-2017 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *
 * Rev 1.0  Oct 13, 2017    Atul Shukla		Requirement Oxigen
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.oxigenwallet;


public interface MAXOxigenTenderConstants 
{	public static final String OXIGEN = "Oxigen";
	public static final String TOTALAMOUNT = "totalAmount";
	public static final String CURRENCYCODE = "currencyCode";
	public static final String MERCHANTGUID = "merchantGuid";
	public static final String MERCHANTORDERID = "merchantOrderId";
	public static final String INDUSTRYTYPE = "industryType";
	public static final String POSID = "posId";
	public static final String COMMENT = "comment";
	public static final String UNIQUEREFERENCELABEL = "uniqueReferenceLabel";
	public static final String UNIQUEREFERENCEVALUE = "uniqueReferenceValue";
	public static final String INVOICEDETAILS = "invoiceDetails";
	public static final String HEADERNAME = "headerName";
	public static final String ITEMNAME = "ItemName";
	public static final String RATE = "Rate";
	public static final String QUANTITY = "Quantity";
	public static final String AMOUNT = "amount";
	public static final String ITEMDETAILS = "itemDetails";
	public static final String TAXDETAILS = "taxDetails";
	public static final String OTHERDETAILS = "otherDetails";
	public static final String PLATFORMNAME = "platformName";
	public static final String IPADDRESS = "ipAddress";
	public static final String CHANNEL = "channel";
	public static final String VERSION = "version";
	public static final String STATUS = "status";
	public static final String STATUSCODE = "statusCode";
	public static final String STATUSMESSGAE = "statusMessage";
	public static final String WALLETSYSTEMTXNID = "walletSystemTxnId";
	public static final String HEADING = "heading";
	public static final String PAYEESSOLD = "payeeSsold";
	public static final String CASHBACKSTATUS = "cashBackSatus";
	public static final String CASHBACKMESSAGE = "cashBackMessage";
	public static final String METADATA = "metaData";
	public static final String REFUNDTXNGUID = "refundTxnGuid";
	public static final String REFUNDTXNSTATUS = "refundTxnStatus";
	public static final String MOBILENUMBER = "mobileNumber";
	public static final String OTP = "otp";
	public static final String OTP_DETAILS = "otpDetails";
	public static final String OTP_TYPE = "otpType";
	public static final String OTP_TYPE_LOGIN = "LOGIN";
	public static final String OTP_TYPE_DEBIT = "DEBIT_WALLET";
	public static final String CONTENTTYPE = "Content-Type";
	public static final String JSON = "application/json";
	public static final String REQUEST_METHOD_POST = "POST";
	public static final String MID = "mid";
	public static final String CHECKSUMHASH = "checksumhash";
	public static final String CONTENTLENGTH = "Content-Length";
	public static final String REQUEST = "request";
	public static final String OPERATIONTYPE = "operationType";
	public static final String MERCHANTKEYCONFIG = "merchantKey";
	public static final String CONTENTTYPECONFIG = "contentType";
	public static final String OPERATIONWITHDRAW = "operationWithdraw";
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	public static final String NETWORKERROR = "OxigenNetworkError";
	public static final String OXIGENTIMEOUTERROR = "OxigenTimeoutError";
	public static final String CONNECTIONTIMEOUT = "ConnectionTimeout";
	public static final String RESPONSERECEIVED = "RR";
	public static final String RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE = "R";
	public static final String TIMEOUT = "T";
	public static final String BURNED = "B";
	public static final String CURRENCY = "INR";
	public static final String TXNGUID = "txnGuid";
	public static final String OPERATIONREFUND = "operationRefund";
	public static final String OXIGENERROR = "OxigenError";
	public static final String OXIGENSUCCESS = "OxigenSuccess";
	public static final String OXIGENSAMEWALLETEOxigen= "OxigenSameWalletError";
	public static final String OXIGENDELETETENDER = "OxigenDeleteTender";
	public static final String REF_NUM = "refNo";
	public static final String REQUEST_TYPE = "requestType";
	public static final String GET_WALLET_DETAILS = "GET_WALLET_DETAILS";
	public static final String OTP_GENERATE_REQUEST = "GENERATE_OTP";
	public static final String REQUEST_ID = "requestId";
	public static final String GETWALLET_REQUEST_ID = "1234567890";
	public static final String REQUEST_ID_CONSTANT = "1234567890";
	public static final String CREDIT_REQUEST_ID_CONSTANT = "11223344";
	public static final String REQUEST_TIME = "requesterTimestamp";
	public static final String ORIGINAL_DIALOGUE_TRACE_ID = "originalDialogueTraceId";
	public static final String ORIGINAL_DIALOGUE_TRACE_ID_CONSTANT = "b00a2330-ee56-4e43-9688-be11af9408ce";
	public static final String WALLET_OWNER = "walletOwner";
	public static final String SPAR_CONSTANT = "SPAR";
	public static final String REQUEST_HEADER = "requestHeader";
	public static final String STORE_CODE = "storeCode";
	public static final String STORE_CODE_CONSTANT = "SPAR123";
	public static final String TERMINAL_ID = "terminalId";
	public static final String TERMINAL_ID_CONSTANT = "456";
	public static final String OPTIONAL_INFO = "optionalInfo";
	public static final String STORE_DETAILS = "storeDetails";
	public static final String POS_CONSTANT = "POS";
	
	public static final String USEPROXY = "UseProxy";
	public static final String PROXYUSER = "ProxyUsername";
	public static final String PROXYPASSWORD = "ProxyPassword";
	public static final String HTTPPROTOCOL = "httpProtocol";
	public static final String PROXYHOST = "ProxyHost";
	public static final String PROXYPORT = "ProxyPortNumber";
	public static final String PHONENUMBER = "mobileNumber";
	
	// for wallet credit
	public static final String WALLET_CREDIT_REQUEST = "CREDIT_WALLET";
	//public static final String OTP_TYPE_CREDIT = "CREDIT";
	//for Ewallet Reversal
	public static final String WALLET_REVERSAL_REQUEST = "TRANSACTION_REVERSAL";
	
	public static final String INVOICENO = "invoiceNo";
	public static final String INVOICENETAMOUNT = "invoiceNetAmount";
	public static final String INVOICEGROSSAMOUNT = "invoiceGrossAmount";
	public static final String INVOICEDATE = "invoiceDate";
	public static final String MODEOFPAYMENT = "modeOfPayment";
	public static final String PROMOCODE = "promoCode";
	public static final String SUBWALLETTYPE = "subWalletType";
	public static final String SUBWALLETTYPE_VALUE = "ECASH";
	public static final String TRANSACTIONINFO = "transactionInfo";
	
	public static final String TRANSACTIONID = "transactionId";
	
	public static final String PROMO_CODE_BOGO = "BOGO";
	public static final String MODE_OF_PAYMENT_WALLET = "WALLET";
	public static final String REQUEST_TYPE_SUBMIT_INVOICE = "SUBMIT_INVOICE_DETAILS";
	public static final String ITEMS = "items";
	public static final String ITEMS_CODE = "itemCode";
	public static final String INVOICE_ITEMS_QUANTITY = "quantity";
	public static final String INVOICE_ITEMS_RATE = "rate";
	public static final String INVOICE_ITEMS_VALUE = "value";
	public static final String INVOICE_ITEMS_GROSS_AMOUNT = "grossAmount";
	public static final String INVOICE_ITEMS_DISCOUNT_VALUE = "discountValue";
	public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
	
	

}