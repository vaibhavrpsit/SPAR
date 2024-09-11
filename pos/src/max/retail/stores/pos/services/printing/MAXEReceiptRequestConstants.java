/********************************************************************************
 *   
 *	Copyright (c) 2019-2020 MAX SPAR Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	Nov 6, 2019		Purushotham Reddy 	Changes for E-Receipt Integration With Karnival
 *
 ********************************************************************************/

package max.retail.stores.pos.services.printing;

/**
 * @author Purushotham Reddy Sirison
 **/

public interface MAXEReceiptRequestConstants {
	
	public static final String TOTALAMOUNT = "amount";
	public static final String HEAD = "head";
	
	public static final String CONTENTTYPE = "Content-Type";
	public static final String ACCEPT = "Accept";
	public static final String JSON = "application/json";
	public static final String REQUESTMETHODPOST = "POST";
	public static final String REQUESTMETHODGET = "GET";
	public static final String CONTENTLENGTH = "Content-Length";
	public static final String AUTHCODE = "Authorization";
	
	public static final String OPERATIONTYPE = "operationType";
	public static final String MERCHANTKEYCONFIG = "merchantKey";
	public static final String CONTENTTYPECONFIG = "contentType";
	public static final String OPERATIONWITHDRAW = "operationWithdraw";
	public static final String SUCCESS = "success";
	public static final String FAILURE = "FAILURE";
	public static final String FAILURESTATUS = "F";
	public static final String PENDING = "PENDING";
	public static final String KARNIVALSERVEROFFLINE = "KarnivalServerOffline";
	public static final String KARNIVALNETWORKERROR = "KarnivalNetworkError";
	public static final String KARNIVALTIMEOUTERROR = "KarnivalTimeoutError";
	
	public static final String CONNECTIONTIMEOUT = "ConnectionTimeout";
	public static final String ERECEIPTSAVEDSUCCESS = "Entity has been saved.";
	public static final String ACCESSDENIED = "Access Denied";
	public static final String ERECEIPTSENDSUCCESSFULLY = "EReceiptSendSuccessfully";


	public static final String POSINFO = "posInfo";
	public static final String BILLINFO = "billInfo";
	public static final String CUSTOMERINFO = "customerInfo";
	public static final String PAYMENTINFO = "paymentInfo";
	public static final String BILLDISCOUNT = "billDiscount";
	public static final String ITEMS = "items";
	public static final String STOREDETAILS = "store";
	public static final String ITEMDETAILS = "items";
	public static final String TAXES = "taxes";
	public static final String DISCOUNT = "discount";
	public static final String ITEMDISCOUNT = "discount";
	public static final String ITEMTAXES = "taxes";
	
	public static final String SUBTOTAL = "subTotal";
	public static final String TOTALQTY = "totalQuantity";
	public static final String TOTALSAVING = "totalSaving";
	public static final String TOTALTENDER = "totalTender";
	public static final String CHANGEDUE = "changeDue";
	public static final String ATTRIBUTES = "attributes";
	
	public static final String EASYBUYTOTAL = "easyBuyTotal";
	public static final String BILLMODE = "Bill-Mode";
	
	public static final String POSNUMBER = "posNumber";
	public static final String USERID = "userId";
	public static final String USERNAME = "userName";
	public static final String BILLNUMBER = "billNumber";
	public static final String BILLSTATUS = "billStatus";
	public static final String BILLTYPE = "billType";
	public static final String PURCHASEDATE = "purchaseDate";
	public static final String PURCHASETIME = "purchaseTime";
	
	public static final String CUSTOMERADDRESS = "customerAddress";
	public static final String ADDRESSSTRING = "addressString";
	public static final String CITY = "city";
	public static final String COUNTRY = "country";
	public static final String STATE = "state";
	public static final String CUSTOMEREMAIL = "customerEmail";
	public static final String CUSTOMERID = "customerId";
	public static final String CUSTOMERNAME = "customerName";
	public static final String CUSTOMERNUMBER = "customerNumber";
	public static final String LOYALTYDESC = "loyaltyDescription";
	public static final String LOYALTYNUMBER = "loyaltyNumber";
	
	public static final String PAIDAMOUNT = "paidAmount";
	public static final String PAYMENTMODE = "paymentMode";
	public static final String AMOUNT = "amount";
	public static final String MODE = "mode";
	public static final String PAYMENTSTATUS = "paymentStatus";
	public static final String ROUNDOFF = "roundoff";
	public static final String ROUNDEDOFFAMOUNT = "roundedOffAmount";
	public static final String DESCRIPTION = "description";
	public static final String DISCAMOUNT = "discountableAmount";
	public static final String PERCENTAGE = "percentage";
	public static final String TAXABLEAMOUNT = "taxableAmount";
	public static final String CODE = "code";
	public static final String ACCOUNTNUMBER = "accountNumber";
	public static final String SUBTENDERTYPE = "subTenderType";
	public static final String PAYMENTDATETIME = "paymentDateTime";
	public static final String PAYMENTDETAILS = "details";
	
	public static final String OTHERDISCOUNTS = "otherDiscounts";
	public static final String OFFERS = "offers";
	public static final String EMPID = "Emp Id";
	public static final String EMPNAME = "Emp Name";
	public static final String COMPAYNAME = "Company Name";
	public static final String AVALIABLELIMIT = "Avl Limit";
	
	
	public static final String HSNCODE = "hsnCode";
	public static final String MRP = "mrp";
	public static final String VALIDTILL = "validTill";
	public static final String QUANTITY = "quantity";
	public static final String SELLINGPRICE = "sellingPrice";
	
	public static final String CUSTOMERCAREEMAIL = "customerCareEmail";
	public static final String CUSTOMERCARENUMBER = "customerCareNumber";
	public static final String DISPLAYADDRESS = "displayAddress";
	public static final String DISPLAYNAME = "displayName";
	public static final String GSTIN = "gstin";
	public static final String OFFICIALEMAIL = "officialEmail";
	public static final String PRIMARYCONTACTNUMBER = "primaryContactNumber";
	public static final String REGISTEREDADDRESS = "registeredAddress";
	public static final String REGISTEREDNAME = "registeredName";
	public static final String SECONDARTCONTACTNUMBERS = "secondaryContactNumbers";
	public static final String STOREID = "storeId";

	public static final String ERECEIPTERROR = "EReceiptError";

	public static final String ERECEIPTSENDREQPEDNING = "EReceiptSendRequestPending";
	
	public static final String ERECEIPTSENDREQPEDNINGTIMEOUT = "EReceiptSendRequestPendingTimeout";	

}