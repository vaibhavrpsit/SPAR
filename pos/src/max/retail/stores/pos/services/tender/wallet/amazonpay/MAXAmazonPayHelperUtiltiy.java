/********************************************************************************
 *   
 *	Copyright (c) 2019 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	June 01, 2019		Purushotham Reddy 	Changes for POS-Amazon Pay Integration 
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.wallet.amazonpay;

/**
 @author Purushotham Reddy Sirison
 **/

import java.io.BufferedReader; 
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import max.retail.stores.domain.MAXAmazonPayResponse;
import max.retail.stores.domain.tender.amazonpay.MAXAmazonPayTenderConstants;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.foundation.tour.gate.Gateway;

import org.apache.derby.impl.sql.catalog.SYSROUTINEPERMSRowFactory;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import weblogic.utils.collections.TreeMap;

public class MAXAmazonPayHelperUtiltiy implements MAXAmazonPayTenderConstants {

	protected static final Logger logger = Logger
			.getLogger(MAXAmazonPayHelperUtiltiy.class);

	/*private static final String merchantID = MAXAmazonPayConfig
			.get(MAXAmazonPayTenderConstants.AMAZONPAYMERCHANTID);*/
	private static final String merchantID = MAXAmazonPayConfig
			.get(MAXAmazonPayTenderConstants.AMAZONPAYMERCHANTID);
	
	protected static final String accessKeyId = MAXAmazonPayConfig
			.get(MAXAmazonPayTenderConstants.AMAZONPAYACCESSKEYID);

	protected static final String amazonPayTimeOut = Gateway.getProperty(
			"application", "AmazonPayTimeOutInMilliSeconds", "");

	protected static final SimpleDateFormat myFormat = new SimpleDateFormat(
			"yyyyMMddhhmmss");
	protected static final String DATE_TIME_FORMAT = "YYYYMMdd'T'HHmmss'Z'";
	protected static final String HTTPREUESTMETHOD = "POST";
	protected static final String NEW_LINE_CHARACTER = "\n";
	
	
	

	public static MAXAmazonPayResponse chargeRequest(MAXTenderCargo cargo,
			String targetURL, String amount, boolean isSandBoxEnabled)
			throws Exception {

		HttpURLConnection connection = null;

		MAXAmazonPayResponse resp = new MAXAmazonPayResponse();

		Long timestamp = System.currentTimeMillis();
		InetAddress myIP=InetAddress.getLocalHost();
		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_TIME_FORMAT);

		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));
	
		String currentDate = myFormatNew.format(new Date(timestamp))
				.toString();

		Map<String, Object> payloadMap = getRequestObjectForChargePayload(
				cargo, targetURL, amount, timestamp);
		
		
		SignatureUtil signutil= new SignatureUtil();
		String signature1=signutil.signature.toString();
	//	System.out.println(signature1);
	//	System.out.println("requestbody:"+merchantID);

	//	Map<?, ?> encriptedValues = CryptoForMerchant.encrypt(payloadMap);

		//String payloadValue = encriptedValues.get("payload").toString();
		/*String payloadValue=payloadMap.toString();
		String ivValue = encriptedValues.get("iv").toString();
		String keyValue = encriptedValues.get("key").toString();*/

		//JSONObject jsonContentObjForCharge = getJsonRequestObjectForAmazonPay(
			//	payloadValue, ivValue, keyValue);
		JSONObject jsonContentObjForCharge = getJsonRequestObjectForAmazonPay(
				payloadMap);
		
		URL url = new URL(targetURL);
		String url1=url.toString();

		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(MAXAmazonPayTenderConstants.REQUESTMETHODPOST);

		String urlParameters = jsonContentObjForCharge.toString();

		logger.info("chargeRequest urlParameters : " + urlParameters);

		try {
			connection.setRequestProperty(
					MAXAmazonPayTenderConstants.CONTENTTYPE,
					MAXAmazonPayTenderConstants.JSON);
			connection.setRequestProperty(
					"x-amz-algorithm", "AWS4-HMAC-SHA384");
			connection.setRequestProperty(
					"x-amz-client-id", merchantID);
		//	System.out.println("header:"+merchantID);
			connection.setRequestProperty(
					"x-amz-date", currentDate);
			connection.setRequestProperty(
					"x-amz-expires", "900");
			connection.setRequestProperty(
					"x-amz-source", "Browser");
			connection.setRequestProperty(
					"x-amz-user-agent", "Postman");
			connection.setRequestProperty(
					"x-amz-user-ip",myIP.getHostAddress());
			connection.setRequestProperty("Authorization","AMZ+"+accessKeyId+":"+signature1);
		//	System.out.println("AMZ+"+accessKeyId+":"+signature1);
			/*connection.setRequestProperty(
					MAXAmazonPayTenderConstants.ATTPROGFIELD,
					MAXAmazonPayTenderConstants.ATTPROGRAM);
			connection.setRequestProperty(
					MAXAmazonPayTenderConstants.TIMSSTAMPFIELD,
					timestamp.toString());
			connection.setRequestProperty(
					MAXAmazonPayTenderConstants.ISSANDBOXFIELD,
					String.valueOf(isSandBoxEnabled));
			connection.setRequestProperty(
					MAXAmazonPayTenderConstants.CONTENTLENGTH,
					Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);
			connection.setConnectTimeout(Integer.parseInt(amazonPayTimeOut));
			connection.setReadTimeout(Integer.parseInt(amazonPayTimeOut));*/
			connection.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			System.out.println(wr.toString());
			wr.close();
			//System.out.println(connection.getHeaderFields().toString());
		 //    String responsedate=connection.getHeaderFields().get("x-amz-date").toString();
		 //    responsedate=responsedate.substring(1,responsedate.lastIndexOf("]"));
		    // System.out.println(responsedate);
		 //    String requestid=connection.getHeaderFields().get("x-amz-request-id").toString();
		//     requestid=requestid.substring(1,requestid.lastIndexOf("]"));
		   //  System.out.println(requestid);
			//System.out.println(connection.getHeaderFields().get("x-amz-signature"));
			
		
			int responseCode = connection.getResponseCode();
			System.out.println(responseCode);
			resp.setResponseCode(responseCode);
			
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSERECEIVED);
			resp.setRespReceivedDate(new Date());
			InputStream is;
			if (responseCode == HttpURLConnection.HTTP_OK) {
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
			logger.info("The Amazon Pay Charge response : " + response);
		//	System.out.println(response);

			MAXAmazonPayResponse amazonPayResponse = convertChargeResponse(
					response.toString(), resp);
			
			SignatureUtil.genarateSignatureForChargeResponse(cargo,url1,resp,connection);
			
		//	System.out.println("signatureResponsegen="+SignatureUtil.signatureResponse.toString());
			
			String respSign=connection.getHeaderFields().get("x-amz-signature").toString();
			 respSign=respSign.substring(1,respSign.lastIndexOf("]"));
			// System.out.println(respSign);
           if(SignatureUtil.signatureResponse.toString().equalsIgnoreCase(respSign)){
        	   
        	   amazonPayResponse.setReasonCode("200");
				
			}else {
				amazonPayResponse.setReasonCode("04");
			}
			
			return amazonPayResponse;
          
			
			
			
		} catch (ConnectException e) {
			logger.error("\nWith  chargeRequest, timeout exception is "
					+ e.getMessage() + " with cause " + e.getCause());
			resp = new MAXAmazonPayResponse();
			resp.setStatusMessage(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			logger.error(e.getMessage());
			return resp;
		} catch (SocketTimeoutException e) {
			logger.error("\nWith  chargeRequest, connection exception is "
					+ e.getMessage() + " with cause " + e.getCause());
			resp = new MAXAmazonPayResponse();

			resp.setStatusMessage(MAXAmazonPayTenderConstants.AMAZONPAYTIMEOUTERROR);
			resp.setRequestTypeA(MAXAmazonPayTenderConstants.TIMEOUT);
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.TIMEOUT);
			logger.error(e.getMessage());
			return resp;
		} catch (NoRouteToHostException e) {
			logger.error("\nWith  chargeRequest, NoRouteToHostException is "
					+ e.getMessage() + " with cause " + e.getCause());
			resp = new MAXAmazonPayResponse();
			resp.setStatusMessage(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			logger.error(e.getMessage());
			return resp;
		} catch (UnknownHostException e) {
			logger.error("\nWith  chargeRequest, UnknownHostException is "
					+ e.getMessage() + " with cause " + e.getCause());
			resp = new MAXAmazonPayResponse();
			resp.setStatusMessage(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			logger.error(e.getMessage());
			return resp;
		} catch (Exception e) {
			logger.error("chargeRequest exception is " + e.getMessage());
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		if (resp.getReqRespStatus() == null
				|| resp.getReqRespStatus().equals(""))
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		return resp;
		
	}

	/*@SuppressWarnings("unchecked")
	private static JSONObject getJsonRequestObjectForAmazonPay(
			String payloadValue, String ivValue, String keyValue) {

		JSONObject jsonContentObjForCharge = new JSONObject();

		jsonContentObjForCharge.put(null,
				payloadValue);
       
		jsonContentObjForCharge.putAll(payloadValue);
		
		//jsonContentObjForCharge.put(MAXAmazonPayTenderConstants.IV, ivValue);

		//jsonContentObjForCharge.put(MAXAmazonPayTenderConstants.KEY, keyValue);

		return jsonContentObjForCharge;
	}
	*/
	@SuppressWarnings("unchecked")
	private static JSONObject getJsonRequestObjectForAmazonPay(
			Map payloadMap) {

		JSONObject jsonContentObjForCharge = new JSONObject();

		
       
		jsonContentObjForCharge.putAll(payloadMap);
		
		//jsonContentObjForCharge.put(MAXAmazonPayTenderConstants.IV, ivValue);

		//jsonContentObjForCharge.put(MAXAmazonPayTenderConstants.KEY, keyValue);

		return jsonContentObjForCharge;
	}
	

	private static Map<String, Object> getRequestObjectForChargePayload(
			MAXTenderCargo cargo, String url, String totalAmount,
			Long currentTime) throws Exception {

		Map<String, Object> contentObjForPayload = new TreeMap<String, Object>();

		Map<String, String> storeDetail = new HashMap<String, String>();
		
		String storeid=cargo.getStoreStatus().getStore().getStoreID();
		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_TIME_FORMAT);

		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));
	//	String dateStamp = myFormatNew.format(new Date(currentTime)).toString();
		String currentDate = myFormatNew.format(new Date(currentTime))
				.toString();

		String tranId = cargo.getCurrentTransactionADO().getTransactionID()
				+ myFormat.format(new Date()).toString();
		String hostname = url.substring(8);
		InetAddress myIP=InetAddress.getLocalHost();
		
		storeDetail.put("storeIdType", "MERCHANT_STORE_ID");
		storeDetail.put("storeId", cargo.getStoreStatus().getStore().getStoreID());
		
		/*changes by Vaibhav for Amazon Pay Barcode integration start*/
		//storeDetail.put("terminalId", cargo.getRegister().getWorkstation().getWorkstationID());
		//storeDetail.put("cashierMobileNo", cargo.getAmazonPayPhoneNumber());
		
		
		
		
		contentObjForPayload.put(MAXAmazonPayTenderConstants.INTENTFIELD,
				MAXAmazonPayTenderConstants.INTENTVALUE);
		
		contentObjForPayload.put(MAXAmazonPayTenderConstants.MERCHANTGUIDFIELD,
				merchantID);
		/*contentObjForPayload.put(
				MAXAmazonPayTenderConstants.MERCHANTTRANSACTIONIDFIELD, tranId);
		
		*/
		contentObjForPayload.put(
				MAXAmazonPayTenderConstants.CHARGEID, tranId);

		contentObjForPayload.put(MAXAmazonPayTenderConstants.AMOUNT,
				totalAmount);

		contentObjForPayload.put(MAXAmazonPayTenderConstants.CURRENCYCODE,
				MAXAmazonPayTenderConstants.CURRENCY);

		contentObjForPayload.put(
				MAXAmazonPayTenderConstants.CUSTOMERIDTYPEFIELD,
				MAXAmazonPayTenderConstants.CUSTOMERIDTYPE);
		contentObjForPayload.put(
				MAXAmazonPayTenderConstants.ATTPROGFIELD,
				MAXAmazonPayTenderConstants.ATTPROGRAM);
		
		
		/*contentObjForPayload.put(
				MAXAmazonPayTenderConstants.CUSTOMERMOBILENUMBERFIELD,
				cargo.getAmazonPayPhoneNumber());*/
		contentObjForPayload.put(
				MAXAmazonPayTenderConstants.AMAZONPAYCODEFIELD,
				cargo.getBarcode());
		//cargo.setBarcode("1111111111111111");
	//	cargo.setBarcode(barode);

		/*contentObjForPayload.put(
				MAXAmazonPayTenderConstants.SIGNATUREMETHODFIELD,
				MAXAmazonPayTenderConstants.SIGNATUREMETHOD);

		contentObjForPayload.put(
				MAXAmazonPayTenderConstants.SIGNATUREVERSIONFIELD,
				MAXAmazonPayTenderConstants.SIGNATUREVERSION);*/

		contentObjForPayload.put(MAXAmazonPayTenderConstants.STOREDETAIL,
				storeDetail);
		contentObjForPayload.put(MAXAmazonPayTenderConstants.SELLERNOTE,
				storeid);

		/*contentObjForPayload.put(MAXAmazonPayTenderConstants.MERCHANTGUIDFIELD,
				merchantID);*/

		/*contentObjForPayload.put(MAXAmazonPayTenderConstants.ACCESSKEYIDFIELD,
				accessKeyId);*/

		/*contentObjForPayload.put(
				MAXAmazonPayTenderConstants.MERCHANTTRANSACTIONIDFIELD, tranId);*/

		/*contentObjForPayload.put(MAXAmazonPayTenderConstants.TIMSSTAMPFIELD,
				currentTime.toString());*/


				SignatureUtil.genarateSignatureForCharge(cargo, url,
						totalAmount, storeDetail, currentTime, tranId);
		
		/*contentObjForPayload.put(
				MAXAmazonPayTenderConstants.TIMSSTAMPFIELD,
				currentDate);*/
		

		return contentObjForPayload;
	}

	private static Map<String, Object> getRequestObjectForVerifyPayload(
			MAXTenderCargo cargo, String url, String totalAmount,
			Long currentTime, String tranId) throws Exception {

		Map<String, Object> contentObjForPayload = new TreeMap<String, Object>();

		contentObjForPayload.put(MAXAmazonPayTenderConstants.MERCHANTGUIDFIELD,
				merchantID);

		contentObjForPayload.put(
				MAXAmazonPayTenderConstants.MERCHANTTRANSACTIONIDTYPEFIELD,
				MAXAmazonPayTenderConstants.MERCHANTTRANSACTIONIDTYPE);

		contentObjForPayload.put(
				MAXAmazonPayTenderConstants.TRANSACTIONIDFIELD, tranId);

		/*contentObjForPayload.put(
				MAXAmazonPayTenderConstants.SIGNATUREMETHODFIELD,
				MAXAmazonPayTenderConstants.SIGNATUREMETHOD);

		contentObjForPayload.put(
				MAXAmazonPayTenderConstants.SIGNATUREVERSIONFIELD,
				MAXAmazonPayTenderConstants.SIGNATUREVERSION);*/

		/*contentObjForPayload.put(MAXAmazonPayTenderConstants.ACCESSKEYIDFIELD,
				accessKeyId);

		contentObjForPayload.put(MAXAmazonPayTenderConstants.TIMSSTAMPFIELD,
				currentTime.toString());*/

	
				SignatureUtil.genarateSignatureForVerify(cargo, url,
						currentTime, tranId);

		return contentObjForPayload;
	}

	private static Map<String, Object> getRequestObjectForRefundPayload(
			MAXTenderCargo cargo, String url, String totalAmount,
			Long currentTime, String amazonPayTranId, String tranId)
			throws Exception {

		Map<String, Object> contentObjForPayload = new TreeMap<String, Object>();
		String storeid=cargo.getStoreStatus().getStore().getStoreID();
		contentObjForPayload.put(
				MAXAmazonPayTenderConstants.CHARGEIDTYPEFIELD, MAXAmazonPayTenderConstants.CHARGEIDTYPE);

		/*contentObjForPayload
				.put(MAXAmazonPayTenderConstants.AMAZONTRASACTIONID,
						amazonPayTranId);*/

		/*contentObjForPayload.put(
				MAXAmazonPayTenderConstants.AMAZONTRASACTIONIDTYPEFIELD,
				MAXAmazonPayTenderConstants.AMAZONTRASACTIONIDTYPE);*/

		contentObjForPayload.put(MAXAmazonPayTenderConstants.AMOUNT,
				totalAmount);

		contentObjForPayload.put(MAXAmazonPayTenderConstants.CURRENCYCODE,
				MAXAmazonPayTenderConstants.CURRENCY);

		contentObjForPayload.put(MAXAmazonPayTenderConstants.REFUNDREFERENCEID,
				tranId);

		/*contentObjForPayload.put(
				MAXAmazonPayTenderConstants.SIGNATUREMETHODFIELD,
				MAXAmazonPayTenderConstants.SIGNATUREMETHOD);

		contentObjForPayload.put(
				MAXAmazonPayTenderConstants.SIGNATUREVERSIONFIELD,
				MAXAmazonPayTenderConstants.SIGNATUREVERSION);*/

		contentObjForPayload.put(MAXAmazonPayTenderConstants.MERCHANTGUIDFIELD,
				merchantID);

		/*contentObjForPayload.put(MAXAmazonPayTenderConstants.ACCESSKEYIDFIELD,
				accessKeyId);

		contentObjForPayload.put(MAXAmazonPayTenderConstants.TIMSSTAMPFIELD,
				currentTime.toString());
*/
		
		/*contentObjForPayload.put(MAXAmazonPayTenderConstants.AMAZONPAYREFUNDSOFTFIELD,
				MAXAmazonPayTenderConstants.AMAZONPAYREFUNDSOFTDESC);*/
		contentObjForPayload.put(
				MAXAmazonPayTenderConstants.CHARGEID, tranId );
		
		contentObjForPayload.put(
				MAXAmazonPayTenderConstants.AMAZONPAYREFUNDSOFTFIELD, MAXAmazonPayTenderConstants.AMAZONPAYREFUNDSOFTDESC);
		

		contentObjForPayload.put(
				MAXAmazonPayTenderConstants.SELLERNOTE, storeid);
		
		
				String Signature1=SignatureUtil.genarateSignatureForRefund(cargo, url,
						totalAmount, currentTime, amazonPayTranId, tranId);

		return contentObjForPayload;
	}

	public static MAXAmazonPayResponse convertChargeResponse(String response,
			MAXAmazonPayResponse resp) {
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
				if (keyValue[0].contains("{")) {
					map.put(keyValue[0].substring(
							keyValue[0].indexOf('\"') + 1,
							keyValue[0].length() - 1), keyValue[1]);
				} else {
					map.put(keyValue[0]
							.substring(keyValue[0].indexOf('\"') + 1,
									keyValue[0].length()),
							keyValue[1].substring(0, keyValue[1].indexOf('\"')));
					//System.out.println(map.toString());
				}

			}
		} catch (Exception e) {
			logger.error("Error in converting Amazon Pay Chaarge response : "
					+ e.getMessage());
		}
		resp.setOrderId(map.get("chargeId"));
		resp.setWalletTxnId(map.get("chargeId"));
		resp.setStoreId(map.get("merchantId"));
		resp.setAmazonTransactionId(map.get("amazonChargeId"));
		resp.setStatus(map.get("status"));
		resp.setAmount(map.get("requestedAmount"));
		resp.setApprovedAmount(map.get("approvedAmount"));
		resp.setCurrencyCode(map.get("currencyCode"));
		resp.setCreateTime(map.get("createTime"));
		resp.setUpdateTime(map.get("updateTime"));
		resp.setReasonDescription(response);
		
		
		
		return resp;
	}

	public static MAXAmazonPayResponse convertVerifyResponse(String response,
			MAXAmazonPayResponse resp) {
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
				if (keyValue[0].contains("{")) {
					map.put(keyValue[0].substring(
							keyValue[0].indexOf('\"') + 1,
							keyValue[0].length() - 1), keyValue[1]);
				} else {
					map.put(keyValue[0]
							.substring(keyValue[0].indexOf('\"') + 1,
									keyValue[0].length()),
							keyValue[1].substring(0, keyValue[1].indexOf('\"')));
				}
			}
		} catch (Exception e) {
			logger.error("Error in converting Amazon Pay Chaarge response : "
					+ e.getMessage());
		}
		resp.setOrderId(map.get("chargeId"));
		resp.setWalletTxnId(map.get("chargeId"));
		resp.setStoreId(map.get("merchantId"));
		resp.setAmazonTransactionId(map.get("amazonChargeId"));
		resp.setStatus(map.get("status"));
		resp.setAmount(map.get("requestedAmount"));
		resp.setApprovedAmount(map.get("approvedAmount"));
		resp.setCurrencyCode(map.get("currencyCode"));
		resp.setCreateTime(map.get("createTime"));
		resp.setUpdateTime(map.get("updateTime"));
		return resp;
	}

	public static MAXAmazonPayResponse convertRefundResponse(String response,
			MAXAmazonPayResponse resp) {
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
				if (keyValue[0].contains("{")) {
					map.put(keyValue[0].substring(
							keyValue[0].indexOf('\"') + 1,
							keyValue[0].length() - 1), keyValue[1]);
				} else if (keyValue[1].contains("\"")) {
					map.put(keyValue[0]
							.substring(keyValue[0].indexOf('\"') + 1,
									keyValue[0].length()),
							keyValue[1].substring(0, keyValue[1].indexOf('\"')));
				} else {
					map.put(keyValue[0]
							.substring(keyValue[0].indexOf('\"') + 1,
									keyValue[0].length()),
							keyValue[1].substring(0));
				}

			}
		} catch (Exception e) {
			logger.error("Error in converting Amazon Pay Chaarge response : "
					+ e.getMessage());
		}
		//resp.setOrderId(map.get("refundReferenceId"));
		resp.setStatus(map.get("status"));
		resp.setAmount(map.get("amount"));
		resp.setWalletTxnId(map.get("refundI"));
		resp.setAmazonTransactionId(map.get("amazonRefundId"));
		resp.setRefundFee(map.get("refundedFee"));
		resp.setCurrencyCode(map.get("currencyCode"));
		resp.setCreateTime(map.get("createTime"));
		resp.setUpdateTime(map.get("updateTime"));
		
		return resp;

	}

	public static MAXAmazonPayResponse refundAmount(MAXTenderCargo cargo,
			String targetURL, String phoneNumber, String amount,
			String amazonPayTranId, String tranId, boolean isSandBoxEnabled)
			throws Exception {

		HttpURLConnection connection = null;

		MAXAmazonPayResponse resp = new MAXAmazonPayResponse();

		Long timestamp = System.currentTimeMillis();
		InetAddress myIP=InetAddress.getLocalHost();
		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_TIME_FORMAT);

		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));
	
		String currentDate = myFormatNew.format(new Date(timestamp))
				.toString();

		Map<String, Object> payloadMap = getRequestObjectForRefundPayload(
				cargo, targetURL, amount, timestamp, amazonPayTranId, tranId);
		
		SignatureUtil signutil1= new SignatureUtil();
		String signature2=signutil1.signature2.toString();
		//System.out.println("signature="+signature2);
		
		

		/*Map<?, ?> encriptedValues = CryptoForMerchant.encrypt(payloadMap);

		String payloadValue = encriptedValues.get("payload").toString();
		String ivValue = encriptedValues.get("iv").toString();
		String keyValue = encriptedValues.get("key").toString();*/

		/*JSONObject jsonContentObjForRefund = getJsonRequestObjectForAmazonPay(
				payloadValue, ivValue, keyValue);*/
		JSONObject jsonContentObjForRefund = getJsonRequestObjectForAmazonPay(
				payloadMap);
		URL url = new URL(targetURL);
		String url2=url.toString();

		
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(MAXAmazonPayTenderConstants.REQUESTMETHODPOST);
		
		String urlParameters = jsonContentObjForRefund.toString();

		logger.info("Refund Request urlParameters : " + urlParameters);

		try {

			connection.setRequestProperty(
					MAXAmazonPayTenderConstants.CONTENTTYPE,
					MAXAmazonPayTenderConstants.JSON);
			connection.setRequestProperty(
					"x-amz-algorithm", "AWS4-HMAC-SHA384");
			connection.setRequestProperty(
					"x-amz-client-id", merchantID);
			//System.out.println("header:"+merchantID);
			connection.setRequestProperty(
					"x-amz-date", currentDate);
			connection.setRequestProperty(
					"x-amz-expires", "900");
			connection.setRequestProperty(
					"x-amz-source", "Browser");
			connection.setRequestProperty(
					"x-amz-user-agent", "Postman");
			connection.setRequestProperty(
					"x-amz-user-ip",myIP.getHostAddress());
			connection.setRequestProperty("Authorization","AMZ+"+accessKeyId+":"+signature2);
			connection.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.close();
		//	System.out.println(connection.getHeaderFields().toString());
			
		   /*  String responsedate=connection.getHeaderFields().get("x-amz-date").toString();
		     responsedate=responsedate.substring(1,responsedate.lastIndexOf("]"));
		   //  System.out.println(responsedate);
		     String requestid=connection.getHeaderFields().get("x-amz-request-id").toString();
		     requestid=requestid.substring(1,requestid.lastIndexOf("]"));
		   //  System.out.println(requestid);
*/		
		//	System.out.println(connection.getHeaderFields().get("x-amz-signature"));
			
		
			int responseCode = connection.getResponseCode();
			resp.setResponseCode(responseCode);
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSERECEIVED);
			resp.setRespReceivedDate(new Date());
			InputStream is;
			if (responseCode == HttpURLConnection.HTTP_OK) {
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
			logger.info("The Amazon Pay Refund response : " + response);
			//System.out.println(response);

			MAXAmazonPayResponse amazonPayResponse = convertRefundResponse(
					response.toString(), resp);
			
			SignatureUtil.genarateSignatureForRefundResponse(cargo,url2,resp,connection);
			
			//System.out.println("signatureRefundResponsegen="+SignatureUtil.signatureResponseRefund.toString());
			//System.out.println("signatureResponse="+connection.getHeaderFields().get("x-amz-signature").toString().substring(1, endIndex));
			String respSign=connection.getHeaderFields().get("x-amz-signature").toString();
			 respSign=respSign.substring(1,respSign.lastIndexOf("]"));
			// System.out.println(respSign);
           if(SignatureUtil.signatureResponseRefund.toString().equalsIgnoreCase(respSign)){
        	   
        	   amazonPayResponse.setReasonCode("200");
				
			}else {
				amazonPayResponse.setReasonCode("04");
			}
			return amazonPayResponse;
		} catch (ConnectException e) {
			logger.error("\nWith  refundAmount Request, timeout exception is "
					+ e.getMessage() + " with cause " + e.getCause());
			resp = new MAXAmazonPayResponse();
			resp.setStatusMessage(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			logger.error(e.getMessage());
			return resp;
		} catch (SocketTimeoutException e) {
			logger.error("\nWith  refundAmount Request, connection exception is "
					+ e.getMessage() + " with cause " + e.getCause());
			resp = new MAXAmazonPayResponse();

			resp.setStatusMessage(MAXAmazonPayTenderConstants.AMAZONPAYTIMEOUTERROR);
			resp.setRequestTypeA(MAXAmazonPayTenderConstants.TIMEOUT);
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.TIMEOUT);
			logger.error(e.getMessage());
			return resp;
		} catch (NoRouteToHostException e) {
			logger.error("\nWith  refundAmount Request, NoRouteToHostException is "
					+ e.getMessage() + " with cause " + e.getCause());
			resp = new MAXAmazonPayResponse();
			resp.setStatusMessage(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			logger.error(e.getMessage());
			return resp;
		} catch (UnknownHostException e) {
			logger.error("\nWith  refundAmount Request, UnknownHostException is "
					+ e.getMessage() + " with cause " + e.getCause());
			resp = new MAXAmazonPayResponse();
			resp.setStatusMessage(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			logger.error(e.getMessage());
			return resp;
		} catch (Exception e) {
			logger.error("refundAmount Request exception is " + e.getMessage());
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		if (resp.getReqRespStatus() == null
				|| resp.getReqRespStatus().equals(""))
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		return resp;

	}

	public static MAXAmazonPayResponse verifyRequest(MAXTenderCargo cargo,
			String targetURL, String amount, boolean isSandBoxEnabled,
			String tranId) throws Exception {

		HttpURLConnection connection = null;
		URL url = null;

		MAXAmazonPayResponse resp = new MAXAmazonPayResponse();

		Long timestamp = System.currentTimeMillis();
		InetAddress myIP=InetAddress.getLocalHost();
		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_TIME_FORMAT);

		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));
	
		String currentDate = myFormatNew.format(new Date(timestamp))
				.toString();

		Map<String, Object> payloadMap = getRequestObjectForVerifyPayload(
				cargo, targetURL, amount, timestamp, tranId);

		/*Map<?, ?> encriptedValues = CryptoForMerchant.encrypt(payloadMap);

		String payloadValue = encriptedValues.get("payload").toString();
		String ivValue = encriptedValues.get("iv").toString();
		String keyValue = encriptedValues.get("key").toString();*/

		/*JSONObject jsonContentObjForVerify = getJsonRequestObjectForAmazonPay(
				payloadValue, ivValue, keyValue);*/
		
		/*JSONObject jsonContentObjForVerify = getJsonRequestObjectForAmazonPay(
				payloadMap);*/
		SignatureUtil signutil= new SignatureUtil();
		String signature1=signutil.signature.toString();

		String urlParameters = formatParameters(payloadMap);

		StringBuilder sb = new StringBuilder();

		sb.append("?").append(urlParameters);

		url = new URL(targetURL + sb);
		String url3=url.toString();
		
		//System.out.println(url);

		connection = (HttpURLConnection) url.openConnection();

		connection
				.setRequestMethod(MAXAmazonPayTenderConstants.REQUESTMETHODGET);

		logger.info("verifyRequest urlParameters : " + urlParameters);

		try {
			/*connection.setRequestProperty(
					MAXAmazonPayTenderConstants.CONTENTTYPE,
					MAXAmazonPayTenderConstants.JSON);*/
			connection.setRequestProperty(
					"x-amz-algorithm", "AWS4-HMAC-SHA384");
			connection.setRequestProperty(
					"x-amz-client-id", merchantID);
			System.out.println("header:"+merchantID);
			connection.setRequestProperty(
					"x-amz-date", currentDate);
			connection.setRequestProperty(
					"x-amz-expires", "900");
			connection.setRequestProperty(
					"x-amz-source", "Browser");
			connection.setRequestProperty(
					"x-amz-user-agent", "Postman");
			connection.setRequestProperty(
					"x-amz-user-ip",myIP.getHostAddress());
			connection.setRequestProperty("Authorization","AMZ+"+accessKeyId+":"+signature1);
		//	System.out.println("AMZ+"+accessKeyId+":"+signature1);
			//connection.setDoInput(true);
			
			/*//System.out.println(connection.getHeaderFields().toString());
		     String responsedate=connection.getHeaderFields().get("x-amz-date").toString();
		     responsedate=responsedate.substring(1,responsedate.lastIndexOf("]"));
		   //  System.out.println(responsedate);
		     String requestid=connection.getHeaderFields().get("x-amz-request-id").toString();
		     requestid=requestid.substring(1,requestid.lastIndexOf("]"));
		   //  System.out.println(requestid);
		//	System.out.println(connection.getHeaderFields().get("x-amz-signature"));
*/
			int responseCode = connection.getResponseCode();
			resp.setResponseCode(responseCode);
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSERECEIVED);
			resp.setRespReceivedDate(new Date());
			InputStream is;
			if (responseCode == HttpURLConnection.HTTP_OK) {
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
			logger.info("The Amazon Pay Verify response : " + response);

			MAXAmazonPayResponse amazonPayResponse = convertVerifyResponse(
					response.toString(), resp);
			
            SignatureUtil.genarateSignatureForVerifyResponse(cargo,url3,resp,connection);
			
		//	System.out.println("signatureVerifyResponsegen="+SignatureUtil.signatureVerifyResponse.toString());
			
			String respSign=connection.getHeaderFields().get("x-amz-signature").toString();
			 respSign=respSign.substring(1,respSign.lastIndexOf("]"));
			// System.out.println(respSign);
           if(SignatureUtil.signatureVerifyResponse.toString().equalsIgnoreCase(respSign)){
        	   
        	   amazonPayResponse.setReasonCode("200");
				
			}else {
				amazonPayResponse.setReasonCode("04");
			}
			
			return amazonPayResponse;
			
		} catch (ConnectException e) {
			logger.error("\nWith  verifyRequest, timeout exception is "
					+ e.getMessage() + " with cause " + e.getCause());
			resp = new MAXAmazonPayResponse();
			resp.setStatusMessage(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			logger.error(e.getMessage());
			return resp;
		} catch (SocketTimeoutException e) {
			logger.error("\nWith  verifyRequest, connection exception is "
					+ e.getMessage() + " with cause " + e.getCause());
			resp = new MAXAmazonPayResponse();

			resp.setStatusMessage(MAXAmazonPayTenderConstants.AMAZONPAYTIMEOUTERROR);
			resp.setRequestTypeA(MAXAmazonPayTenderConstants.TIMEOUT);
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.TIMEOUT);
			logger.error(e.getMessage());
			return resp;
		} catch (NoRouteToHostException e) {
			logger.error("\nWith  verifyRequest, NoRouteToHostException is "
					+ e.getMessage() + " with cause " + e.getCause());
			resp = new MAXAmazonPayResponse();
			resp.setStatusMessage(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			logger.error(e.getMessage());
			return resp;
		} catch (UnknownHostException e) {
			logger.error("\nWith  verifyRequest, UnknownHostException is "
					+ e.getMessage() + " with cause " + e.getCause());
			resp = new MAXAmazonPayResponse();
			resp.setStatusMessage(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			logger.error(e.getMessage());
			return resp;
		} catch (Exception e) {
			logger.error("verifyRequest exception is " + e.getMessage());
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		if (resp.getReqRespStatus() == null
				|| resp.getReqRespStatus().equals(""))
			resp.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		return resp;
	}
	private static String formatParameters(final Map<String, Object> parameters) {
		Map<String, Object> sorted = new TreeMap<String, Object>();
		sorted.putAll(parameters);
		Iterator<Map.Entry<String, Object>> pairs = sorted.entrySet()
				.iterator();
		StringBuilder queryStringBuilder = new StringBuilder();
		while (pairs.hasNext()) {
			Map.Entry<String, Object> pair = pairs.next();
			String key = pair.getKey();
		//	queryStringBuilder.append("\"");
			queryStringBuilder.append(percentEncodeRfc3986(key));
			queryStringBuilder.append("=");
			String value = pair.getValue().toString();
			queryStringBuilder.append(percentEncodeRfc3986(value));
			if (pairs.hasNext()) {
				queryStringBuilder.append("&");
				//queryStringBuilder.append("\",");
			}
		}
		return queryStringBuilder.toString();
	}
	private static String percentEncodeRfc3986(final String s) {
		String out;
		try {
			out = URLEncoder.encode(s, StandardCharsets.UTF_8.name())
					.replace("+", "%20").replace("*", "%2A")
					.replace("%7E", "~");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return out;
	}

}
