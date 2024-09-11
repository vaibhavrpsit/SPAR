/********************************************************************************
 *   
 *	Copyright (c) 2019-2020 MAX SPAR Hypermarket, Inc    All Rights Reserved. 
 *	
 *	Rev	1.0 	Nov 6, 2019		Purushotham Reddy 	Changes for E-Receipt Integration With Karnival
 *
 ********************************************************************************/
package max.retail.stores.pos.services.printing;

/**
@author Purushotham Reddy Sirison
**/

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.text.SimpleDateFormat;

import max.retail.stores.common.data.MAXTAXUtils;
import max.retail.stores.domain.discountCoupon.MAXDiscountCouponIfc;
import max.retail.stores.domain.lineitem.MAXItemTaxIfc;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetail;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import max.retail.stores.domain.mcoupon.MAXMcouponIfc;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.tender.MAXTenderAmazonPay;
import max.retail.stores.domain.tender.MAXTenderPaytm;
import max.retail.stores.domain.tender.MAXTenderMobikwik;
import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.transaction.MAXVoidTransaction;
import max.retail.stores.pos.receipt.MAXTaxSummaryDetailsBean;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.VoidTransaction;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MAXEnableEReceiptHelperUtiltiy {
	
	protected static final Logger logger = Logger
			.getLogger(MAXEnableEReceiptHelperUtiltiy.class);
	

	protected static final String eReceiptTimeOut = Gateway.getProperty(
			"application", "EReceiptTimeOutInMilliSeconds", "");
	
	
	protected static final String authCode = Gateway.getProperty(
			"application", "EReceiptAuthorization", "");
	
	public static MAXEReceiptResponse sendRequest(
			SaleReturnTransactionIfc trans, String targetURL,
			String mobileNumber, String billMode) {
		
		HttpURLConnection connection = null;

		MAXEReceiptResponse resp = new MAXEReceiptResponse();

		JSONObject jsonContentObjForEReceipt = getJsonRequestObjectForEReceipt(trans, mobileNumber, billMode);
		
		URL url = null;
		try {
			url = new URL(targetURL);
			java.lang.System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(MAXEReceiptRequestConstants.REQUESTMETHODPOST);
		} catch (IOException e1) {
			logger.warn(e1);
		}

		String urlParameters = jsonContentObjForEReceipt.toString();

		logger.info("Karnival Send Request url 104:: " + url);
		logger.info("Karnival Send Request urlParameters : " + urlParameters);

		try {			
			  connection.setRequestProperty(MAXEReceiptRequestConstants.AUTHCODE,authCode);			  
			  connection.setRequestProperty(MAXEReceiptRequestConstants.CONTENTTYPE,MAXEReceiptRequestConstants.JSON);			  
			  connection.setRequestProperty(MAXEReceiptRequestConstants.ACCEPT,MAXEReceiptRequestConstants.JSON);
			 System.out.println("112 ::"+connection);
			connection.setRequestProperty(
					MAXEReceiptRequestConstants.CONTENTLENGTH,
					Integer.toString(urlParameters.getBytes().length));
			
			connection.setUseCaches(false);
			connection.setConnectTimeout(Integer.parseInt(eReceiptTimeOut));
			connection.setReadTimeout(Integer.parseInt(eReceiptTimeOut));
			connection.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.close();
			int responseCode = connection.getResponseCode();
			resp.setResponseCode(responseCode);
			resp.setRespReceivedDate(new Date());
			InputStream is;
			if (responseCode == HttpURLConnection.HTTP_CREATED) {
				is = connection.getInputStream();
			} else {
				is = connection.getErrorStream();
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			logger.info(" Karnival Send response : " + response);

			MAXEReceiptResponse sendResponse =convertSendResponse(
					response.toString(), resp);
			
			return sendResponse;
		} catch (ConnectException e) {
			logger.error("\nWith  Send Request, timeout exception is "
					+ e.getMessage() + " with cause " + e.getCause());
			resp = new MAXEReceiptResponse();
			resp.setStatusMessage(MAXEReceiptRequestConstants.KARNIVALNETWORKERROR);
			logger.error(e.getMessage());
			return resp;
		} catch (SocketTimeoutException e) {
			logger.error("\nWith  Send Request , connection exception is "
					+ e.getMessage() + " with cause " + e.getCause());
			resp = new MAXEReceiptResponse();
			resp.setStatusMessage(MAXEReceiptRequestConstants.KARNIVALTIMEOUTERROR);
			return resp;
		} catch (NoRouteToHostException e) {
			logger.error("\nWith  Send Request , NoRouteToHostException is "
					+ e.getMessage() + " with cause " + e.getCause());
			resp = new MAXEReceiptResponse();
			resp.setStatusMessage(MAXEReceiptRequestConstants.KARNIVALNETWORKERROR);
			return resp;
		} catch (UnknownHostException e) {
			logger.error("\nWith  Send Request , UnknownHostException is "
					+ e.getMessage() + " with cause " + e.getCause());
			resp = new MAXEReceiptResponse();
			resp.setStatusMessage(MAXEReceiptRequestConstants.KARNIVALNETWORKERROR);
			return resp;
		} catch (Exception e) {
			logger.error("Send Request exception is " + e.getMessage());
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return resp;
	}

	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static JSONObject getJsonRequestObjectForEReceipt(
			SaleReturnTransactionIfc trans, String mobileNumber, String billMode) {

		JSONObject jsonContentObj = null, jsonPosInfoObj = null, jsonBillInfoObj = null, jsonCustomerInfoObj = null,
				jsonCustomerAddressObj = null, jsonPaymentInfoObj = null, jsonPaymentModeObj =  null, jsonPaymentDetailsObj = null,
				jsonBillDiscountObj = null, jsonTranTaxInfoObj = null, jsonStoreDetailsObj = null, 
				jsonItemDetailsObj = null, jsonItemDiscountDetailsObj= null, jsonItemTaxDetailsObj = null, 
				jsonAttributesObj = null, jsonOtherDiscountsObj = null, jsonEmployeeDiscountObj = null, 
				jsonMCouponDiscountObj = null, jsonFreeItemDiscountObj = null;
		
		JSONArray jsonPaymentModeObjArray = null, jsonItemTaxesObjArray = null, jsonItemDetailsObjArray = null, 
				 jsonTranTaxInfoObjArray = null , jsonOtherDiscountsObjArray = null, jsonOffersObjArray = null;
		
		jsonContentObj = new JSONObject();
		jsonPosInfoObj = new JSONObject();
		jsonBillInfoObj = new JSONObject();
		jsonCustomerInfoObj = new JSONObject();
		jsonCustomerAddressObj = new JSONObject();
		jsonPaymentInfoObj = new JSONObject();
		jsonPaymentModeObj = new JSONObject();
		jsonPaymentDetailsObj = new JSONObject();
		jsonBillDiscountObj = new JSONObject();
		jsonTranTaxInfoObj = new JSONObject();
		jsonStoreDetailsObj = new JSONObject(); 
		jsonItemDetailsObj = new JSONObject();
		jsonItemDiscountDetailsObj = new JSONObject(); 
		jsonItemTaxDetailsObj = new JSONObject();
		jsonAttributesObj = new JSONObject();
		jsonOtherDiscountsObj = new JSONObject();
		jsonEmployeeDiscountObj = new JSONObject();
		jsonMCouponDiscountObj = new JSONObject();
		jsonFreeItemDiscountObj = new JSONObject();
		
		
		jsonPaymentModeObjArray = new JSONArray();
		jsonItemTaxesObjArray = new JSONArray();
		jsonTranTaxInfoObjArray = new JSONArray();
		jsonItemDetailsObjArray = new JSONArray();
		jsonOtherDiscountsObjArray = new JSONArray();
		jsonOffersObjArray = new JSONArray();
		
		MAXSaleReturnTransactionIfc transaction = (MAXSaleReturnTransactionIfc)trans;
		CurrencyIfc employeeDiscount=DomainGateway.getBaseCurrencyInstance();
		CurrencyIfc otherDiscount=DomainGateway.getBaseCurrencyInstance();
		 CurrencyIfc transactionDiscountTotal=((MAXSaleReturnTransactionIfc)transaction).
				 getTransactionTotals().getTransactionDiscountTotal();
		
		jsonPosInfoObj.put(MAXEReceiptRequestConstants.POSNUMBER, trans.getWorkstation().getStoreID());
		jsonPosInfoObj.put(MAXEReceiptRequestConstants.USERID, trans.getCashier().getEmployeeID());
		jsonPosInfoObj.put(MAXEReceiptRequestConstants.USERNAME, trans.getCashier().getName().getFullName());
		
		jsonBillInfoObj.put(MAXEReceiptRequestConstants.BILLNUMBER, trans.getTransactionID());
		jsonBillInfoObj.put(MAXEReceiptRequestConstants.BILLSTATUS, trans.getTransactionStatus());
		jsonBillInfoObj.put(MAXEReceiptRequestConstants.BILLTYPE, trans.getTransactionTypeDescription());

		SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat myTimeFormat = new SimpleDateFormat("HHmmss");
		jsonBillInfoObj.put(MAXEReceiptRequestConstants.PURCHASEDATE, 
				myDateFormat.format(new Date()));
		jsonBillInfoObj.put(MAXEReceiptRequestConstants.PURCHASETIME, 
				myTimeFormat.format(new Date()));
		
		
		if( trans.getCustomer() != null ){
		jsonCustomerAddressObj.put(MAXEReceiptRequestConstants.ADDRESSSTRING,
				trans.getCustomer().getAddressBookEntries().get(0).getAddress().getLine1());
		jsonCustomerAddressObj.put(MAXEReceiptRequestConstants.CITY, 
				trans.getCustomer().getAddressBookEntries().get(0).getCity());
		jsonCustomerAddressObj.put(MAXEReceiptRequestConstants.STATE,
				trans.getCustomer().getAddressBookEntries().get(0).getState());
		jsonCustomerAddressObj.put(MAXEReceiptRequestConstants.COUNTRY,
				trans.getCustomer().getAddressBookEntries().get(0).getCountry());
		
	
		jsonCustomerInfoObj.put(MAXEReceiptRequestConstants.CUSTOMERADDRESS, jsonCustomerAddressObj);
		jsonCustomerInfoObj.put(MAXEReceiptRequestConstants.CUSTOMEREMAIL, trans.getCustomer().getEMailAddress());
		jsonCustomerInfoObj.put(MAXEReceiptRequestConstants.CUSTOMERID, trans.getCustomer().getCustomerID());
		jsonCustomerInfoObj.put(MAXEReceiptRequestConstants.CUSTOMERNAME, trans.getCustomer().getCustomerName());
		jsonCustomerInfoObj.put(MAXEReceiptRequestConstants.CUSTOMERNUMBER, mobileNumber);
		jsonCustomerInfoObj.put(MAXEReceiptRequestConstants.LOYALTYDESC, trans.getCustomer().getCustomerName());
		jsonCustomerInfoObj.put(MAXEReceiptRequestConstants.LOYALTYNUMBER,  trans.getCustomer().getCustomerID());
		}
		else{
			jsonCustomerInfoObj.put(MAXEReceiptRequestConstants.CUSTOMERNUMBER, mobileNumber);
		}
		
		Iterator<TenderLineItemIfc> value = trans.getTenderLineItemsVector().iterator();
		
		 while (value.hasNext()) {
			 TenderLineItemIfc tenderRDO = (TenderLineItemIfc) value.next();
			 MAXTenderChargeIfc tenderCharge= null;
			 jsonPaymentModeObj.put(MAXEReceiptRequestConstants.AMOUNT, 
					 tenderRDO.getAmountTender());
			 jsonPaymentModeObj.put(MAXEReceiptRequestConstants.MODE,  
					 tenderRDO.getTypeCodeString());
			
			jsonPaymentDetailsObj.put(MAXEReceiptRequestConstants.PAYMENTDATETIME,tenderRDO.getTenderDateTime());
			jsonPaymentModeObj.put(MAXEReceiptRequestConstants.PAYMENTDETAILS, jsonPaymentDetailsObj);
			
			if (tenderRDO instanceof MAXTenderChargeIfc) {
			 if( tenderRDO.getTypeCodeString().equalsIgnoreCase("CRDT") ||
					 tenderRDO.getTypeCodeString().equalsIgnoreCase("DBIT") )
			 {
				 tenderCharge=  (MAXTenderChargeIfc) tenderRDO;
				 jsonPaymentModeObj.put(MAXEReceiptRequestConstants.CODE,  
						 tenderCharge.getAuthCode());
				 jsonPaymentModeObj.put(MAXEReceiptRequestConstants.ACCOUNTNUMBER,  
						 tenderCharge.getLastFourDigits());
				 jsonPaymentModeObj.put(MAXEReceiptRequestConstants.SUBTENDERTYPE,  
						 tenderCharge.getBankName());
				 
			 }
			}
			else if(tenderRDO instanceof MAXTenderAmazonPay){
				
				 if( tenderRDO.getTypeCodeString().equalsIgnoreCase("AMPY") )
				 {
					 MAXTenderAmazonPay tenderCharge1=  (MAXTenderAmazonPay) tenderRDO;
					 jsonPaymentModeObj.put(MAXEReceiptRequestConstants.CODE,
							 tenderCharge1.getAmazonPayWalletTransactionID());
				 }
			}
			else if(tenderRDO instanceof MAXTenderMobikwik){
				
				 if( tenderRDO.getTypeCodeString().equalsIgnoreCase("MBWK") )
				 {
					 MAXTenderMobikwik tenderCharge1=  (MAXTenderMobikwik) tenderRDO;
					 jsonPaymentModeObj.put(MAXEReceiptRequestConstants.CODE,
							 tenderCharge1.getMobikwikWalletTransactionID());
				 }
			}
			else if(tenderRDO instanceof MAXTenderPaytm){
				
				 if( tenderRDO.getTypeCodeString().equalsIgnoreCase("PYTM") )
				 {
					 MAXTenderPaytm tenderCharge1=  (MAXTenderPaytm) tenderRDO;
					 jsonPaymentModeObj.put(MAXEReceiptRequestConstants.CODE,
							 tenderCharge1.getPaytmWalletTransactionID());
				 }
			}
			 jsonPaymentModeObjArray.add(jsonPaymentModeObj);
			 jsonPaymentModeObj = new JSONObject();
	        }
		 // Emp Discount
			 if(trans.getEmployeeDiscountID()!=null && transactionDiscountTotal.signum()>0){
				
				 TransactionDiscountStrategyIfc[] discounts=((MAXSaleReturnTransactionIfc)transaction)
							.getItemContainerProxy().getTransactionDiscounts();
					for (TransactionDiscountStrategyIfc discount : discounts) {
						if(discount!=null && discount.getDiscountEmployeeID()!=null && 
								!discount.getDiscountEmployeeID().equals("") && discount.getDiscountEmployee()!=null
								&& discount.getReasonCode()==-1){
							employeeDiscount=employeeDiscount.add(discount.getDiscountAmount());
						}
						else{
							otherDiscount=otherDiscount.add(discount.getDiscountAmount());
						}
					}
				 jsonEmployeeDiscountObj.put(MAXEReceiptRequestConstants.EMPID,
						 transaction.getEmployeeDiscountID());
				 jsonEmployeeDiscountObj.put(MAXEReceiptRequestConstants.EMPNAME,  
						 transaction.getDiscountEmployeeName());
				 jsonEmployeeDiscountObj.put(MAXEReceiptRequestConstants.COMPAYNAME,
						 transaction.getEmployeeCompanyName());
				 jsonEmployeeDiscountObj.put(MAXEReceiptRequestConstants.AVALIABLELIMIT,
						 transaction.getEmpDiscountAvailLimit());
				 
				 
				 jsonOtherDiscountsObj.put(MAXEReceiptRequestConstants.AMOUNT, employeeDiscount);
				 jsonOtherDiscountsObj.put(MAXEReceiptRequestConstants.ATTRIBUTES, jsonEmployeeDiscountObj);
				 jsonOtherDiscountsObj.put(MAXEReceiptRequestConstants.DESCRIPTION, "Employee Discount");
				 jsonOtherDiscountsObj.put(MAXEReceiptRequestConstants.DISCAMOUNT, employeeDiscount);
				 jsonOtherDiscountsObjArray.add(jsonOtherDiscountsObj);
				 jsonOtherDiscountsObj = new JSONObject();
			 }
			 // Trans Disc
			jsonBillDiscountObj.put(MAXEReceiptRequestConstants.AMOUNT, 
					trans.getTenderTransactionTotals().getTransactionDiscountTotal().subtract(employeeDiscount));
			Object coupons[] = transaction.getCapillaryCouponsApplied().toArray();
			if( coupons!= null && coupons.length > 0 )
			{
				for (int i = 0; i < coupons.length; i++) {
					MAXDiscountCouponIfc capillaryCoupon = (MAXDiscountCouponIfc) coupons[i];
					jsonBillDiscountObj.put(MAXEReceiptRequestConstants.DESCRIPTION, capillaryCoupon.getCouponNumber());
				}
			}
			
			else{
				jsonBillDiscountObj.put(MAXEReceiptRequestConstants.DESCRIPTION, "");
			}
			
			jsonBillDiscountObj.put(MAXEReceiptRequestConstants.DISCAMOUNT, 
					trans.getTenderTransactionTotals().getTransactionDiscountTotal().subtract(employeeDiscount));
			
			// M-Coupons
			
			if(((MAXSaleReturnTransactionIfc) trans).getMcouponList()!=null && ((MAXSaleReturnTransactionIfc) trans).getMcouponList().size()>0){
				for(MAXMcouponIfc mcoupon:((MAXSaleReturnTransactionIfc) trans).getMcouponList()){
					jsonMCouponDiscountObj.put(MAXEReceiptRequestConstants.CODE, mcoupon.getCouponNumber());
					jsonMCouponDiscountObj.put(MAXEReceiptRequestConstants.DESCRIPTION, mcoupon.getCouponDescription());
					jsonMCouponDiscountObj.put(MAXEReceiptRequestConstants.VALIDTILL, mcoupon.getValidTill());
					jsonOffersObjArray.add(jsonMCouponDiscountObj);
					jsonMCouponDiscountObj = new JSONObject();
				}
			}
			
			// Free Items
			
			 if((trans instanceof MAXSaleReturnTransaction && ((MAXSaleReturnTransaction)trans).getPrintFreeItem()!=null) ){
				 jsonFreeItemDiscountObj.put(MAXEReceiptRequestConstants.CODE, "SPECIAL OFFER");
				 jsonFreeItemDiscountObj.put(MAXEReceiptRequestConstants.DESCRIPTION, "You have got a SPECIAL OFFER");
				 jsonFreeItemDiscountObj.put(MAXEReceiptRequestConstants.VALIDTILL, ((MAXSaleReturnTransaction)trans).getPrintFreeItem());
				 jsonOffersObjArray.add(jsonFreeItemDiscountObj);
				 jsonFreeItemDiscountObj = new JSONObject();
			 }
			
			
			jsonPaymentInfoObj.put(MAXEReceiptRequestConstants.PAIDAMOUNT, 
					trans.getTenderTransactionTotals().getGrandTotal());
			jsonPaymentInfoObj.put(MAXEReceiptRequestConstants.PAYMENTMODE, jsonPaymentModeObjArray);
			jsonPaymentInfoObj.put(MAXEReceiptRequestConstants.PAYMENTSTATUS, "Success");
			jsonPaymentInfoObj.put(MAXEReceiptRequestConstants.ROUNDOFF,  
					trans.getTenderTransactionTotals().getCashChangeRoundingAdjustment());
			jsonPaymentInfoObj.put(MAXEReceiptRequestConstants.ROUNDEDOFFAMOUNT,  
					trans.getTenderTransactionTotals().getGrandTotal());
			jsonPaymentInfoObj.put(MAXEReceiptRequestConstants.TOTALTENDER,
					trans.getTenderTransactionTotals().getAmountTender());
			jsonPaymentInfoObj.put(MAXEReceiptRequestConstants.CHANGEDUE, 
					trans.getTenderTransactionTotals().getChangeDue().negate());
			
			// Transaction Tax @Puru
			tranTaxSummaryDetails(trans, jsonTranTaxInfoObj, jsonTranTaxInfoObjArray);

			jsonStoreDetailsObj.put(MAXEReceiptRequestConstants.STOREID, trans.getWorkstation().getStoreID());
			
			Vector<?> lineItemsVector = trans.getLineItemsVector();

			SaleReturnLineItemIfc lineItem = null;
			for (int i = 0; i < lineItemsVector.size(); i++) {
				if (lineItemsVector.get(i) instanceof SaleReturnLineItem)
					lineItem = (SaleReturnLineItemIfc) lineItemsVector.get(i);
					
				MAXLineItemTaxBreakUpDetailIfc[] lineItemBreakUpDetails = null;
				
				lineItemBreakUpDetails = ((MAXItemTaxIfc) (lineItem.getItemPrice().getItemTax()))
						.getLineItemTaxBreakUpDetail();
				
				//Item Tax @Puru
				for(int j = 0; j< lineItemBreakUpDetails.length; j++){
					
					jsonItemTaxDetailsObj.put(MAXEReceiptRequestConstants.AMOUNT, 
							lineItemBreakUpDetails[j].getTaxAmount());
					jsonItemTaxDetailsObj.put(MAXEReceiptRequestConstants.DESCRIPTION, 
							lineItemBreakUpDetails[j].getTaxCodeDescription());
					jsonItemTaxDetailsObj.put(MAXEReceiptRequestConstants.TAXABLEAMOUNT,
							lineItemBreakUpDetails[j].getTaxableAmount());
					jsonItemTaxDetailsObj.put(MAXEReceiptRequestConstants.CODE,
							lineItemBreakUpDetails[j].getTaxAssignment().getTaxType());
					jsonItemTaxDetailsObj.put(MAXEReceiptRequestConstants.PERCENTAGE,
							lineItemBreakUpDetails[j].getTaxRate());
					jsonItemTaxesObjArray.add(jsonItemTaxDetailsObj);
					jsonItemTaxDetailsObj = new JSONObject(); 
				}
				
				// Item Disc
				jsonItemDiscountDetailsObj.put(MAXEReceiptRequestConstants.AMOUNT, 
						lineItem.getItemPrice().getItemDiscountAmount().divide(lineItem.getItemQuantityDecimal()));
				jsonItemDiscountDetailsObj.put(MAXEReceiptRequestConstants.DESCRIPTION, "");
				jsonItemDiscountDetailsObj.put(MAXEReceiptRequestConstants.DISCAMOUNT, 
						lineItem.getItemPrice().getItemDiscountAmount().divide(lineItem.getItemQuantityDecimal()));
				
			jsonItemDetailsObj.put(MAXEReceiptRequestConstants.AMOUNT, lineItem
					.getItemPrice().getExtendedSellingPrice().subtract(lineItem
							.getItemPrice().getItemDiscountAmount()));
			jsonItemDetailsObj.put(MAXEReceiptRequestConstants.CODE,
					lineItem.getPLUItemID());
			jsonItemDetailsObj.put(MAXEReceiptRequestConstants.DESCRIPTION,
							lineItem.getPLUItem().getDescription(
							LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
			jsonItemDetailsObj.put(MAXEReceiptRequestConstants.HSNCODE,
					((MAXPLUItemIfc) lineItem.getPLUItem()).getHsnNum());
			jsonItemDetailsObj.put(MAXEReceiptRequestConstants.MRP,
					((MAXPLUItemIfc) lineItem.getPLUItem()).getMaximumRetailPrice());
			jsonItemDetailsObj.put(MAXEReceiptRequestConstants.QUANTITY,
					lineItem.getItemQuantityDecimal());
			jsonItemDetailsObj.put(MAXEReceiptRequestConstants.SELLINGPRICE,
					lineItem.getItemPrice().getExtendedSellingPrice());
			jsonItemDetailsObj.put(MAXEReceiptRequestConstants.ITEMDISCOUNT,
					jsonItemDiscountDetailsObj);
			jsonItemDiscountDetailsObj = new JSONObject(); 
			
			jsonItemDetailsObj.put(MAXEReceiptRequestConstants.ITEMTAXES,
					jsonItemTaxesObjArray);
			jsonItemDetailsObjArray.add(jsonItemDetailsObj);
			jsonItemDetailsObj = new JSONObject(); 
			jsonItemTaxesObjArray = new JSONArray(); 
		}
		//jsonContentObj.put(MAXEReceiptRequestConstants.AUTHCODE, authCode);
		//jsonContentObj.put(MAXEReceiptRequestConstants.CONTENTTYPE,MAXEReceiptRequestConstants.JSON);
		//jsonContentObj.put(MAXEReceiptRequestConstants.ACCEPT,MAXEReceiptRequestConstants.JSON);
					 
		jsonAttributesObj.put(MAXEReceiptRequestConstants.EASYBUYTOTAL, transaction.getEasyBuyTotals());
		jsonAttributesObj.put(MAXEReceiptRequestConstants.BILLMODE, billMode);
		
		jsonContentObj.put(MAXEReceiptRequestConstants.POSINFO, jsonPosInfoObj);
		jsonContentObj.put(MAXEReceiptRequestConstants.BILLINFO, jsonBillInfoObj);
		jsonContentObj.put(MAXEReceiptRequestConstants.CUSTOMERINFO, jsonCustomerInfoObj);
		jsonContentObj.put(MAXEReceiptRequestConstants.PAYMENTINFO, jsonPaymentInfoObj);
		jsonContentObj.put(MAXEReceiptRequestConstants.BILLDISCOUNT, jsonBillDiscountObj);
		jsonContentObj.put(MAXEReceiptRequestConstants.STOREDETAILS, jsonStoreDetailsObj);
		jsonContentObj.put(MAXEReceiptRequestConstants.ITEMDETAILS, jsonItemDetailsObjArray);
		jsonContentObj.put(MAXEReceiptRequestConstants.TAXES, jsonTranTaxInfoObjArray );
		jsonContentObj.put(MAXEReceiptRequestConstants.SUBTOTAL, trans.getTenderTransactionTotals().getSubtotal());
		jsonContentObj.put(MAXEReceiptRequestConstants.TOTALQTY, trans.getTenderTransactionTotals().getQuantitySale());
		jsonContentObj.put(MAXEReceiptRequestConstants.TOTALSAVING, trans.getTenderTransactionTotals().getAmountOffTotal());
		jsonContentObj.put(MAXEReceiptRequestConstants.ATTRIBUTES, jsonAttributesObj);
		jsonContentObj.put(MAXEReceiptRequestConstants.OTHERDISCOUNTS, jsonOtherDiscountsObjArray);
		jsonContentObj.put(MAXEReceiptRequestConstants.OFFERS, jsonOffersObjArray);
		
		/*
		 * JSONObject head = new JSONObject();
		 * head.put(MAXEReceiptRequestConstants.AUTHCODE, authCode);
		 * head.put(MAXEReceiptRequestConstants.CONTENTTYPE,"application/json");
		 * head.put(MAXEReceiptRequestConstants.ACCEPT,"application/json");
		 * jsonContentObj.put(MAXEReceiptRequestConstants.HEAD,head);
		 */
		 
		return jsonContentObj;
	
	}
	public static MAXTaxSummaryDetailsBean[] tranTaxSummaryDetails(SaleReturnTransactionIfc transaction, 
			JSONObject jsonTranTaxInfoObj, JSONArray jsonTranTaxInfoObjArray)
	{

		if (!(transaction instanceof VoidTransaction && (((VoidTransaction) transaction)
				.getOriginalTransactionType() == TransactionConstantsIfc.TYPE_LAYAWAY_INITIATE || 
				((VoidTransaction) transaction)
				.getOriginalTransactionType() == TransactionConstantsIfc.TYPE_LAYAWAY_PAYMENT)))
		{
			if ((transaction instanceof RetailTransactionIfc) && 
					((RetailTransactionIfc) transaction).getLineItemsVector() != null)
			{
				List<MAXLineItemTaxBreakUpDetailIfc> lineItemTaxBreakupDetailsList = 
						new ArrayList<MAXLineItemTaxBreakUpDetailIfc>();
				String taxCode = null;
				String taxCodeVal = null;
				Map<?, ?> taxmap = new HashMap<Object, Object>();

				if (transaction instanceof RetailTransactionIfc)
				{
					for (Enumeration<?> e = ((RetailTransactionIfc) transaction).getLineItemsVector()
							.elements(); e.hasMoreElements();)
					{
						SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) e.nextElement();

						MAXLineItemTaxBreakUpDetailIfc[] lineItemBreakUpDetails = null;
						
						lineItemBreakUpDetails = ((MAXItemTaxIfc) (srli.getItemPrice().getItemTax()))
								.getLineItemTaxBreakUpDetail();
						
						if (lineItemBreakUpDetails != null)
						{
							for (int j = 0; j < lineItemBreakUpDetails.length ; j++)
							{
								lineItemTaxBreakupDetailsList.add(lineItemBreakUpDetails[j]);
							}
						}
					}
				}

				// Group the Tax Breakup's by TaxCode.
				Map<String, MAXTaxSummaryDetailsBean> taxSummaryDetailsMap = groupByTaxCode(lineItemTaxBreakupDetailsList);
				
				taxSummaryDetailsMap.values().toArray();
				
				Object arr[] = taxSummaryDetailsMap.values().toArray();

				MAXTaxSummaryDetailsBean taxSummaryDetailsBean[] = new MAXTaxSummaryDetailsBean[arr.length];
				// colorDescArray[0] = initialSubDept;
				for (int i = 0; i < arr.length; i++){
					
					if(transaction instanceof VoidTransaction){
					taxSummaryDetailsBean[i] = (MAXTaxSummaryDetailsBean) arr[i];
						taxSummaryDetailsBean[i].setTaxableAmount(taxSummaryDetailsBean[i].getTaxableAmount().negate());
						taxSummaryDetailsBean[i].setTaxAmount(taxSummaryDetailsBean[i].getTaxAmount().negate());
					}				
					else{
						taxSummaryDetailsBean[i] = (MAXTaxSummaryDetailsBean) arr[i];
					}
					MAXTAXUtils.getItemTaxType((RetailTransactionIfc) transaction);
					RetailTransactionIfc retailTransaction = (RetailTransactionIfc) transaction;
				
				   if (retailTransaction instanceof MAXVoidTransaction) {
						taxmap = ((MAXVoidTransaction) retailTransaction).getTaxCode();
					}
				   if (retailTransaction instanceof MAXSaleReturnTransactionIfc) {
						taxmap =  ((MAXSaleReturnTransactionIfc)retailTransaction).getTaxCode();
					}
				   if(taxmap!= null){
					for( Object key : taxmap.keySet() ){
						taxCode = key.toString();
						taxCodeVal = (String) taxmap.get(key);
						if(taxSummaryDetailsBean[i].getTaxCode() == taxCode){
							taxSummaryDetailsBean[i].setTaxCodeValue(taxCodeVal);
							break;
						}
					  }
				   }
				}
				setTaxSummary(taxSummaryDetailsBean, jsonTranTaxInfoObj, jsonTranTaxInfoObjArray);
				
				//return getTaxSummary();
			}
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public static void setTaxSummary(MAXTaxSummaryDetailsBean[] taxSummary, JSONObject jsonTranTaxInfoObj,
			JSONArray jsonTranTaxInfoObjArray)
	{
		
		HashMap<String,CurrencyIfc> tax= new HashMap<String,CurrencyIfc>();
		HashMap<String,CurrencyIfc> taxable= new HashMap<String,CurrencyIfc>();
		for(int i=0; i<taxSummary.length;i++){
			tax.put(taxSummary[i].getTaxCodeValue(), DomainGateway.getBaseCurrencyInstance());
			taxable.put(taxSummary[i].getTaxCodeValue(), DomainGateway.getBaseCurrencyInstance());
		}
		
		taxSummary = sortItem(taxSummary);
		
		for(int i=0; i<taxSummary.length; i++){
			
			jsonTranTaxInfoObj.put(MAXEReceiptRequestConstants.AMOUNT, 
					taxSummary[i].getTaxAmount());
			jsonTranTaxInfoObj.put(MAXEReceiptRequestConstants.DESCRIPTION,
					taxSummary[i].getTaxDescription());			
			jsonTranTaxInfoObj.put(MAXEReceiptRequestConstants.TAXABLEAMOUNT, 
					taxSummary[i].getTaxableAmount());	
			jsonTranTaxInfoObj.put(MAXEReceiptRequestConstants.CODE, 
					taxSummary[i].getTaxCodeValue());	
			jsonTranTaxInfoObj.put(MAXEReceiptRequestConstants.PERCENTAGE, 
					taxSummary[i].getTaxRate());
			
			
			jsonTranTaxInfoObjArray.add(jsonTranTaxInfoObj);
			jsonTranTaxInfoObj= new JSONObject();
		}
		
	}


	private static MAXTaxSummaryDetailsBean[] sortItem(
			MAXTaxSummaryDetailsBean[] taxLineItem) {
		quickSort(taxLineItem, taxLineItem.length);
		return taxLineItem;
	}


		public static void quickSort(MAXTaxSummaryDetailsBean[] slItems,
			int len) {
		int a, b;
		MAXTaxSummaryDetailsBean temp = null;
		int sortTheStrings = len - 1;
		for (a = 0; a < sortTheStrings; ++a)
			for (b = 0; b < sortTheStrings; ++b)
			{
				String itemDesc = ((MAXTaxSummaryDetailsBean) slItems[b]).getTaxCodeValue();
				String itemDesc1 = ((MAXTaxSummaryDetailsBean) slItems[b+1]).getTaxCodeValue();
				if(itemDesc != null && itemDesc1 != null){
					if (itemDesc.compareTo(itemDesc1)> 0)
					{
						temp = slItems[b];
						slItems[b] = slItems[b + 1];
						slItems[b + 1] = temp;
					}
				}
	      }
	}

	protected static Map<String, MAXTaxSummaryDetailsBean> groupByTaxCode(
			List<MAXLineItemTaxBreakUpDetailIfc> lineItemTaxBreakupDetailsList) {

		Map<String, MAXTaxSummaryDetailsBean> taxCodeMap = new HashMap<String, MAXTaxSummaryDetailsBean>();

		Iterator<MAXLineItemTaxBreakUpDetailIfc> it = lineItemTaxBreakupDetailsList.iterator();
		
		while (it.hasNext()) {

			MAXLineItemTaxBreakUpDetail litbd = (MAXLineItemTaxBreakUpDetail) it.next();
			String taxCode = litbd.getTaxAssignment().getTaxCode();
			// Add only + ve Tax Amount based on the Tax Code. Negative Taxes
			// shouldn't be showed on Receipt.

			// if (taxCode != null && !taxCode.equals(Util.EMPTY)) {
			if (taxCode != null && !taxCode.equals(Util.EMPTY_STRING)) {

				if (taxCodeMap.containsKey(taxCode)) {
					MAXTaxSummaryDetailsBean tsdb = (MAXTaxSummaryDetailsBean) taxCodeMap
							.get(taxCode);
					tsdb.addTaxableAmount(litbd.getTaxableAmount());
					tsdb.addTaxAmount(litbd.getTaxAmount());
					tsdb.setTaxRate(litbd.getTaxAssignment().getTaxRate());
					tsdb.setTaxDescription(litbd.getTaxAssignment()
							.getTaxCodeDescription());
					tsdb.addTotalTaxAmount(litbd.getTaxableAmount().add(
							litbd.getTaxAmount()));

				} else {
					MAXTaxSummaryDetailsBean tsdb = new MAXTaxSummaryDetailsBean();
					tsdb.setTaxableAmount(litbd.getTaxableAmount());
					tsdb.setTaxAmount(litbd.getTaxAmount());
					tsdb.setTaxRate(litbd.getTaxAssignment().getTaxRate());
					tsdb.setTaxDescription(litbd.getTaxAssignment()
							.getTaxCodeDescription());
					tsdb.setTaxCode(litbd.getTaxAssignment().getTaxCode());
					tsdb.setTotalTaxAmount(litbd.getTaxableAmount().add(
							litbd.getTaxAmount()));
					taxCodeMap.put(taxCode, tsdb);
				}
			}
		}
		return taxCodeMap;
	}


	public static MAXEReceiptResponse convertSendResponse(String response,
			MAXEReceiptResponse resp) {
		String[] tokens = response.split(",");
		Map<String, String> map = new HashMap<>();
		try {
			for (int index = 0; index < tokens.length;) {
				String[] keyValue = null;
				if (tokens[index].contains("\":{")) {
					keyValue = tokens[index].split(":");
					if (keyValue.length == 3) {
						keyValue[0] = keyValue[1];
						keyValue[1] = keyValue[2];
						if (keyValue[1] != null && keyValue[1].startsWith("\""))
							keyValue[1] = keyValue[1].substring(1);
						if (keyValue[1] != null && keyValue[1].endsWith("\""))
							keyValue[1] = keyValue[1].substring(0,
									keyValue[1].length() - 1);
					}
					++index;
				} else {
					keyValue = tokens[index].split("\":");
					if (keyValue != null && keyValue.length >= 2) {
						if (keyValue[1] != null && keyValue[1].startsWith("\""))
							keyValue[1] = keyValue[1].substring(1);
						if (keyValue[1] != null && keyValue[1].endsWith("\"}"))
							keyValue[1] = keyValue[1].substring(0,
									keyValue[1].length() - 1);
					}
					++index;
				}
				if (keyValue[0].contains("{") && keyValue.length != 3) {
					map.put(keyValue[0].substring(
							keyValue[0].indexOf('\"') + 1,
							keyValue[0].length()),keyValue[1].substring(0, keyValue[1].indexOf('\"')));
				} 
				else if(keyValue[1].contains("}") && keyValue.length != 3){
					map.put(keyValue[0]
							.substring(keyValue[0].indexOf('\"') + 1,
									keyValue[0].length()),keyValue[1].substring(0, keyValue[1].indexOf('\"')));
				}
				else {
					map.put(keyValue[0]
							.substring(keyValue[0].indexOf('\"') + 1,
									keyValue[0].length()), keyValue[1]);
				}

			}
		} catch (Exception e) {
			logger.error("Error in converting Karnival Send response : "
					+ e.getMessage());
		}
		resp.setStatus(map.get("status"));
		resp.setMessage(map.get("message"));
		resp.setError(map.get("error"));
		resp.setSendResponse(response);
		
		if (map.get("checkout.otp") != null) {
			resp.setOTP(map.get("checkout.otp"));
		}
		resp.setDataException(Boolean.FALSE);
		if (map.get("entity.id\"") != null) {
			resp.setStatusMessage(map.get("entity.id\""));
		}
		return resp;
	}
}
