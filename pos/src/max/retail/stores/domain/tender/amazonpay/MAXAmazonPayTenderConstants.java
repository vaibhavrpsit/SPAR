/********************************************************************************
 *   
 *	Copyright (c) 2019 MAX SPAR Hypermarket, Inc    All Rights Reserved.

 *	
 *	Rev	1.0 	Jul 01, 2019		Purushotham Reddy 	Changes for POS_Amazon Pay Integration 
 *
 ********************************************************************************/

package max.retail.stores.domain.tender.amazonpay;

/**
 * @author Purushotham Reddy Sirison
 **/

public interface MAXAmazonPayTenderConstants {
	
	public static final String AMOUNT = "amount";
	public static final String REFUNDAMOUNT = "refundAmount";
	public static final String AMAZONTRASACTIONID = "amazonTransactionId";
	public static final String AMAZONTRASACTIONIDTYPEFIELD = "amazonTransactionIdType";
	public static final String CURRENCYCODE = "currencyCode";
	
	public static final String AMAZONPAYMERCHANTID = "AmazonPayMerchantid";
	public static final String AMAZONPAYACCESSKEYID = "AmazonPayAccessKeyid";
	public static final String AMAZONPAYSECRETKEY = "AmazonPaySecretKey";

	public static final String MERCHANTORDERID = "merchantOrderId";
	public static final String INDUSTRYTYPE = "industryType";
	public static final String REFUNDREFERENCEID = "refundId";
	
	public static final String CONTENTTYPE = "Content-Type";
	public static final String JSON = "application/json";
	public static final String REQUESTMETHODPOST = "POST";
	public static final String REQUESTMETHODGET = "GET";
	public static final String CONTENTLENGTH = "Content-Length";
	public static final String REQUEST = "request";
	public static final String OPERATIONTYPE = "operationType";
	public static final String MERCHANTKEYCONFIG = "merchantKey";
	public static final String CONTENTTYPECONFIG = "contentType";
	public static final String OPERATIONWITHDRAW = "operationWithdraw";
	public static final String SUCCESS = "AuthApproved";
	public static final String SUCCESS1 = "CaptureApproved";
	
	public static final String FAILURE = "FAILURE";
	public static final String FAILURESTATUS = "F";
	public static final String PENDING = "PENDING";
	public static final String AMAZONPAYSERVEROFFLINE = "AmazonPayServerOffline";
	public static final String AMAZONPAYNETWORKERROR = "AmazonPayNetworkError";
	public static final String AMAZONPAYTIMEOUTERROR = "AmazonPayTimeoutError";
	
	public static final String AMAZONPAYPAYMENTFAILURE = "AmazonPayPaymentFailure";
	
	public static final String CONNECTIONTIMEOUT = "ConnectionTimeout";
	public static final String TECHNICALISSUE = "AmazonPayTechnicalError";
	public static final String AMAZONPAYDUPLICATEERROR = "AmazonPayDuplicateError";
	public static final String AMAZONACCOUNTNOTEXIST = "AmazonAccountNotExist";

	public static final String RESPONSERECEIVED = "RR";
	public static final String RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE = "R";
	public static final String TIMEOUT = "T";
	public static final String BURNED = "B";
	public static final String CURRENCY = "INR";
	public static final String TXNGUID = "txnGuid";
	public static final String OPERATIONREFUND = "operationRefund";
	public static final String AMAZONPAYERROR = "AmazonPayError";
	
	public static final String AMAZONPAYCHARGESUCCESS = "AmazonPayChargeSuccessful";
	public static final String AMAZONPAYVERIFYSUCCESS = "AmazonPayVerifySuccessful";
	public static final String AMAZONPAYREFUNDSUCCESS = "AmazonPayrefundSuccessful";
	public static final String AMAZONPAYVERIFYPENDING = "AmazonPayVerifyPending";
	public static final String AMAZONPAYVERIFYPENDINGNW = "AmazonPayVerifyPendingNetwork";

	public static final String CUSTOMERIDTYPEFIELD = "customerIdType";
	//public static final String CUSTOMERIDTYPEFIELD = "Barcode";
	public static final String SIGNATUREMETHODFIELD = "signatureMethod";
	public static final String SIGNATUREVERSIONFIELD = "signatureVersion";
	public static final String SIGNATUREFIELD = "signature";
	public static final String STOREDETAIL = "storeDetail";
	//public static final String CUSTOMERMOBILENUMBERFIELD = "customerIdValue";
	public static final String ACCESSKEYIDFIELD = "accessKeyId";
	public static final String MERCHANTGUIDFIELD = "merchantId";
	public static final String TIMSSTAMPFIELD = "timeStamp";
	public static final String MERCHANTTRANSACTIONIDFIELD = "merchantTransactionId";

	public static final String TRANSACTIONIDFIELD = "txnId";
	public static final String MERCHANTTRANSACTIONIDTYPEFIELD = "txnIdType";
	public static final String ATTPROGFIELD = "attributableProgram";
	public static final String ISSANDBOXFIELD = "isSandbox";
	public static final String PAYLOAD = "payload";
	public static final String IV = "iv";
	public static final String KEY = "key";

	//public static final String CUSTOMERIDTYPE = "PHONE_NUMBER";
	public static final String CUSTOMERIDTYPE = "Barcode";
	public static final String SIGNATUREMETHOD = "HmacSHA384";
	public static final String SIGNATUREVERSION = "4";
	//public static final String ATTPROGRAM = "S2S_PAY";
	public static final String ATTPROGRAM = "S2SPay";
	public static final String MERCHANTTRANSACTIONIDTYPE = "MerchantTxnId";
	public static final String AMAZONTRASACTIONIDTYPE = "AMAZON_ORDER_ID";
	//Added by Kumar Vaibhav for Amazon Pay barcode Integration
	public static final String INTENTFIELD = "intent";
	public static final String INTENTVALUE = "AuthorizeAndCapture";
	public static final String AMAZONPAYCODEFIELD = "customerIdValue";
	public static final String AMAZONPAYREFUNDSOFTFIELD = "softDescriptor";
	public static final String AMAZONPAYREFUNDSOFTDESC = "tender voided";
	public static final String CHARGEID = "chargeId";
	public static final String CHARGEIDTYPEFIELD = "chargeIdType";
	public static final String CHARGEIDTYPE = "MerchantTxnId";
	public static final String SELLERNOTE = "noteToCustomer";
	public static final String AMAZONCHARGEID = "amazonChargeId";
	public static final String AMAZONAPPROVEDAMOUNT = "approvedAmount";
	public static final String AMAZONREQUESTEDAMOUNT = "requestedAmount";
	public static final String STATUS = "status";
	public static final String CREATETIME = "createTime";
	public static final String UPDATETIME = "updateTime";
	public static final String AMAZONREFUNDID = "amazonRefundId";
	public static final String REFUNDFEE = "refundedFee";
	
	
	
	

}