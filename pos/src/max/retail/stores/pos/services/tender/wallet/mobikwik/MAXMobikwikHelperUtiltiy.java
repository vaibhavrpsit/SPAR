/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2016-2017 Lifestyle India Pvt Ltd.    All Rights Reserved. 
 * Rev 1.2 August 9th, 2017 Vidhya Kommareddi
 * PAYTM proxy change. Added a new property to application.properties.
 * 
 * Rev 1.1		Apr 21,2017		Nadia Arora (EYLLP)   
 * posid to contain store id along with register id
 *     
 * Rev 1.0 		Apr 11,2017		Nadia Arora (EYLLP)   Paytm Integration
 * Initial revision.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.tender.wallet.mobikwik;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import max.retail.stores.domain.MAXMobikwikResponse;
import max.retail.stores.domain.arts.MAXPaytmDataTransaction;
import max.retail.stores.domain.tender.amazonpay.MAXAmazonPayTenderConstants;
import max.retail.stores.domain.tender.mobikwik.MAXMobikwikTenderConstants;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.services.tender.wallet.paytm.MAXProxyAuthenticator;
import oracle.retail.stores.domain.arts.ARTSTill;
import oracle.retail.stores.foundation.tour.gate.Gateway;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;

import com.zaakpay.api.lib.ChecksumCalculator;


public class MAXMobikwikHelperUtiltiy implements MAXMobikwikTenderConstants{
//	static  int suffixMobileNumberCount=1;
	protected static final Logger logger = Logger.getLogger(MAXMobikwikHelperUtiltiy.class);

	
	public static MAXMobikwikResponse withdrawAmount(MAXTenderCargo cargo,String targetURL, String phoneNumber, String totp, String transactionId, String amount, String tillId, String storeId) throws Exception {
		HttpURLConnection connection = null;
		MAXMobikwikResponse resp = new MAXMobikwikResponse();
		JSONObject	jsonContentChecksumObj = new JSONObject();
		//JSONObject jsonRequestObj = getJsonRequestObject(phoneNumber, otp, transactionId, amount);
		JSONObject jsonContentObj = getJsonRequestObject(cargo,phoneNumber, totp, transactionId, amount);
		//String otp1="602390";
		//System.out.println("The Mobikwik Withdraw Request is"+jsonContentObj.toString());
		//Create connection
	try
	{
		URL url = new URL(targetURL);
		System.out.println("URL ="+url);
		//System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,TLSv1"); 
		
		//connection = (HttpURLConnection) url.openConnection();
		//connection.setRequestMethod("POST");
		String mobileTotp=phoneNumber+totp;
		//String urlParameters = jsonContentObj.toString();
		String CHECKSUMHASH = null;

	//String 
		
		//String marchantKey=MAXMobikwikConfig.get(MAXMobikwikTenderConstants.MERCHANTKEYCONFIG);
		/*CHECKSUMHASH =  Checksum.calculateChecksum(marchantKey.toString().trim()
				, allParamValue);*/
			String marchantKey=MAXMobikwikConfig.get(MAXMobikwikTenderConstants.MERCHANT_ID);
		CHECKSUMHASH =  ChecksumCalculator.calculateChecksum(marchantKey.toString().trim()
			, jsonContentObj.toString());
		jsonContentChecksumObj.put(MAXMobikwikTenderConstants.REQUEST, jsonContentObj);
		jsonContentChecksumObj.put(MAXMobikwikTenderConstants.CHECKSUM,CHECKSUMHASH);
		logger.info("The Mobikwik Withdraw Request is = " +jsonContentChecksumObj.toString());
		System.out.println("The Mobikwik Withdraw Request is :: " +jsonContentChecksumObj.toString());
		//jsonContentChecksumObj.put(MAXMobikwikTenderConstants.CHECKSUM, CHECKSUMHASH);
		
	//logger.info("The Paytm Withdraw checksum is " + CHECKSUMHASH);
	
		String urlParameters = jsonContentChecksumObj.toString();
		//System.out.println("before try"+urlParameters);
			//Create connection
			//System.out.println("Inside try");
			
			 MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
			 ARTSTill till = new ARTSTill(tillId, storeId); 
			 boolean dbStatus = paytmTrans.verifyDatabaseStatus(till); if(! dbStatus) { MAXMobikwikResponse
			 respDataException = new MAXMobikwikResponse();
			 respDataException.setDataException(Boolean.TRUE); return respDataException; }
			//System.out.println("Hello");
			//java.lang.System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
			//connection = (HttpURLConnection)url.openConnection();
			//System.out.println("Hello110");
			//logger.info("ProxyHost value " + MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PROXYHOST));
			//logger.info("ProxyPort value " + MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PROXYPORT));
			//logger.info("ProxyUser value " + MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PROXYUSER));
			//logger.info("ProxyPassword value " + MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PROXYPASSWORD));
			
			 java.lang.System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2"); 
			 connection = (HttpURLConnection)url.openConnection();
			 connection.setRequestMethod("POST");
			 
			//connection.setRequestProperty(MAXMobikwikTenderConstants.PHONENUMBER,phoneNumber);
			//connection.setRequestProperty(MAXMobikwikTenderConstants.OTP,totp);
			//connection.setRequestProperty(MAXMobikwikTenderConstants.PHONENUMBER,mobileTotp);	
			//connection.setRequestProperty(MAXMobikwikTenderConstants.CONTENTTYPE, MAXMobikwikConfig.get(MAXMobikwikTenderConstants.CONTENTTYPECONFIG));
			//connection.setRequestProperty(MAXMobikwikTenderConstants.MID, MAXMobikwikConfig.get(MAXMobikwikTenderConstants.SUBMERCHANT_ID));
			//connection.setRequestProperty(MAXMobikwikTenderConstants.CHECKSUMHASH,CHECKSUMHASH);
			connection.setRequestProperty(MAXAmazonPayTenderConstants.CONTENTTYPE,MAXAmazonPayTenderConstants.JSON);
			connection.setRequestProperty(MAXMobikwikTenderConstants.CONTENTLENGTH, Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);
		//	connection.setReadTimeout(100);
			connection.setConnectTimeout(Integer.parseInt(MAXMobikwikConfig.get(MAXMobikwikTenderConstants.CONNECTIONTIMEOUT)));
			connection.setDoOutput(true);
			System.out.println("Hello120");
			//System.out.println("Connection 120::"+connection.getResponseCode());
			//System.out.println("Connection 121::"+connection.getResponseMessage());
			//logger.info("Connection 118::"+connection);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.close();
			int responseCode = connection.getResponseCode();
			resp.setResponseCode(responseCode);
			resp.setReqRespStatus(MAXMobikwikTenderConstants.RESPONSERECEIVED);
			resp.setRespReceivedDate(new Date());
			InputStream is;
			if(responseCode == HttpURLConnection.HTTP_OK){
				is = connection.getInputStream();
			}else {
				is = connection.getErrorStream();
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+ 
			String line = "";
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			logger.info("The Mobikwik Withdraw response string= " + response);
			
			MAXMobikwikResponse responseMobikwik = convertMobikwikResponse(response.toString(), resp);
			
		//	if(responseMobikwik != null && 
					//(responseMobikwik.getOrderId() == null || responseMobikwik.getOrderId().equals("null") || responseMobikwik.getOrderId().equals(null)))
		//	{
				
				//responsePaytm.setOrderId(jsonContentObj.getJSONObject("request").getString(MAXMobikwikTenderConstants.MERCHANTORDERID));
			//}
			//System.out.println(responseMobikwik.getResponseCode()+""+responseMobikwik.getStatus()+""+responseMobikwik.getStatusMessage()+""+responseMobikwik.getWalletTxnId());
			//System.out.println("AKS"+responseMobikwik.toString());
			return responseMobikwik;		
		}
		catch(SocketTimeoutException e){
			logger.error("\nWith  withdraw, timeout exception is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXMobikwikResponse();
			resp.setStatusMessage(MAXMobikwikTenderConstants.MOBIKWIKTIMEOUTERROR);
			resp.setRequestTypeA(MAXMobikwikTenderConstants.TIMEOUT);
			resp.setReqRespStatus(MAXMobikwikTenderConstants.TIMEOUT);
			//resp.setOrderId(jsonContentObj.getJSONObject("request").getString(MAXMobikwikTenderConstants.MERCHANTORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch (ConnectException e) {
			logger.error("\nWith  withdraw, connection exception is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXMobikwikResponse();
			resp.setStatusMessage(MAXMobikwikTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXMobikwikTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		//	resp.setOrderId(jsonContentObj.getJSONObject("request").getString(MAXMobikwikTenderConstants.MERCHANTORDERID));
			logger.error(e.getMessage());
			return resp;
		} 
		catch(NoRouteToHostException e){
			logger.error("\nWith  withdraw, NoRouteToHostException is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXMobikwikResponse();
			resp.setStatusMessage(MAXMobikwikTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXMobikwikTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			//resp.setOrderId(jsonContentObj.getJSONObject("request").getString(MAXMobikwikTenderConstants.MERCHANTORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch(UnknownHostException e){
			logger.error("\nWith  withdraw, UnknownHostException is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXMobikwikResponse();
			resp.setStatusMessage(MAXMobikwikTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXMobikwikTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		//	resp.setOrderId(jsonContentObj.getJSONObject("request").getString(MAXMobikwikTenderConstants.MERCHANTORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("With  withdraw, exception is " + e.getMessage());
		}  finally {
			if(connection != null) {
				connection.disconnect(); 
			}
		}
		if(resp.getReqRespStatus() == null || resp.getReqRespStatus().equals(""))
			resp.setReqRespStatus(MAXMobikwikTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		return resp;
	}


	public static JSONObject getJsonRequestObject(MAXTenderCargo cargo, String phoneNumber, String totp, String transactionId, String amount) throws Exception {
		JSONObject jsonContentObj = null, jsonRequestObj = null;
		//String currencyCode = MAXMobikwikTenderConstants.CURRENCY; 
		String submerchantGuid = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.SUBMERCHANT_ID);
		String merchantName = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.MERCHANT_NAME);
		String MerhantID = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.MERCHANT_ID);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		String mobilenoTotp=phoneNumber+totp;
		
		String merchantOrderId = transactionId + myFormat.format(new Date()).toString();

	//String merchantOrderId = transactionId + myFormat.format(new Date()).toString();//"07302102002808-14-17 05:14:25";
	//	String industryType = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.INDUSTRYTYPE);
		/* Rev 1.1 changes 
		String posId = transactionId.substring(0,8);
		String platformName = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PLATFORMNAME);
		String ipAddress = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.IPADDRESS);
		String operationType = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.OPERATIONWITHDRAW);
		String channel = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.CHANNEL);
		String version = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.VERSION);*/
		jsonContentObj = new JSONObject();
		jsonRequestObj = new JSONObject(); 

		jsonRequestObj.put(MAXMobikwikTenderConstants.TOTALAMOUNT, amount);
		jsonRequestObj.put(MAXMobikwikTenderConstants.MOBILE_NO, mobilenoTotp);
		jsonRequestObj.put(MAXMobikwikTenderConstants.MERCHANT_NAME, merchantName.trim().toString());
		jsonRequestObj.put(MAXMobikwikTenderConstants.ORDER_ID, merchantOrderId.trim().toString());
		jsonRequestObj.put(MAXMobikwikTenderConstants.SUBMERCHANT_ID, submerchantGuid.trim().toString());
		jsonRequestObj.put(MAXMobikwikTenderConstants.GENRATE_OTP, "Yes");
		//jsonRequestObj.put(MAXMobikwikTenderConstants.CURRENCYCODE, currencyCode);
	//	jsonRequestObj.put(MAXMobikwikTenderConstants.MERCHANTGUID, merchantGuid);
		//jsonRequestObj.put(MAXMobikwikTenderConstants.MERCHANTORDERID, merchantOrderId);
		//jsonRequestObj.put(MAXMobikwikTenderConstants.INDUSTRYTYPE, industryType);
		//jsonRequestObj.put(MAXMobikwikTenderConstants.POSID, posId);
		//  jsonRequestObj.put("comment", comment);
		//jsonContentObj.put(MAXMobikwikTenderConstants.REQUEST, jsonRequestObj);
		
		//jsonContentObj.put(MAXMobikwikTenderConstants.PLATFORMNAME, platformName);
		//jsonContentObj.put(MAXMobikwikTenderConstants.IPADDRESS, ipAddress);
		//jsonContentObj.put(MAXMobikwikTenderConstants.OPERATIONTYPE, operationType);
		//jsonContentObj.put(MAXMobikwikTenderConstants.CHANNEL, channel);
		//jsonContentObj.put(MAXMobikwikTenderConstants.VERSION,version);
		
		//jsonRequestObj.put(MAXMobikwikTenderConstants.REQUEST, jsonRequestObj);
		return	jsonRequestObj;
		//return jsonContentObj;
	}
	public static MAXMobikwikResponse convertMobikwikResponse(String response, MAXMobikwikResponse resp)
	{
		
		String[] tokens = response.split(",");
	    Map<String, String> map = new HashMap<>();
	    try
		{
	    for (int index = 0; index < tokens.length-1; ) 
	    {
	    	String[] keyValue = null;
	    	if(tokens[index].contains("\":{"))
	    	{
	    		keyValue = tokens[index].split(":");
	    		if(keyValue.length == 3)
	    		{
	    			// code added by atul for parsing
	    			keyValue[0] = keyValue[1];
	    			if(keyValue[0] !=null && keyValue[0].endsWith("\""))
	    				keyValue[0] = keyValue[0].substring(0, keyValue[0].length()-1);
	    			//keyValue[0] = keyValue[1];
	    			keyValue[1] = keyValue[2];
	    			if(keyValue[1] != null && keyValue[1].startsWith("\""))
	    				keyValue[1] = keyValue[1].substring(1);
	    			if(keyValue[1] != null && keyValue[1].endsWith("\""))
	    				keyValue[1] = keyValue[1].substring(0, keyValue[1].length()-1);
	    		}
	    		++index;
	    	}
	    	else
	    	{
	    		keyValue = tokens[index].split("\":");
	    		if(keyValue != null && keyValue.length >= 2)
	    		{
		    		if(keyValue[1] != null && keyValue[1].startsWith("\""))
	    				keyValue[1] = keyValue[1].substring(1);
	    			//if(keyValue[1] != null && keyValue[1].endsWith("\""))
		    		
		    		if(keyValue[1] != null && keyValue[1].endsWith("\"}}"))
			    		//if(keyValue[1] != null && keyValue[1].endsWith("}}"+"\""))
		    				keyValue[1] = keyValue[1].substring(0, keyValue[1].length()-3);
		    		if(keyValue[1] != null && keyValue[1].endsWith("\""))
				    	//	if(keyValue[1] != null && keyValue[1].endsWith("}}"+"\""))
			    				keyValue[1] = keyValue[1].substring(0, keyValue[1].length()-1);
		    		
		    		
	    		}
	    		++index;
	    	}
	    	map.put(keyValue[0].substring(keyValue[0].indexOf('\"') + 1, keyValue[0].length()), keyValue[1]);
	    }
		}
		catch(Exception e)
		{
			logger.error("Error in converting Mobikwik response : " + e.getMessage());
			//logger.info(e.printStackTrace());
		}
	    /*1000
	    resp.setOrderId(map.get("orderId"));
		resp.setStatus(map.get("status"));
		resp.setStatusCode(map.get("statusCode"));
		resp.setStatusMessage(map.get("statusDescription"));
		resp.setWalletTxnId(map.get("refId"));
		resp.setMobikwikResponse(response);
		resp.setDataException(Boolean.FALSE);
		if(map.get("refundTxnGuid\"") != null)
		{
			resp.setWalletTxnId(map.get("refundTxnGuid\""));
		}
		return resp;
		*/
	    String statusDescription=map.get("statusDescription");
	    if(statusDescription.equalsIgnoreCase("Refunded successfully"))
	    {
	    	resp.setOrderId(map.get("orderId"));
	    }
	    else
	    {
	    	resp.setOrderId(map.get("orderid"));
	    }
	   
		resp.setStatus(map.get("status"));
		resp.setStatusCode(map.get("statusCode"));
		resp.setStatusMessage(map.get("statusDescription"));
		resp.setWalletTxnId(map.get("refId"));
		resp.setMobikwikResponse(response);
		resp.setDataException(Boolean.FALSE);
		if(map.get("refundTxnGuid\"") != null)
		{
			resp.setWalletTxnId(map.get("refundTxnGuid\""));
		}
		return resp;
	}
	
	public static MAXMobikwikResponse reverseAmount(String orderId, String targetURL, String phoneNumber, String amount) throws Exception {
		HttpURLConnection connection = null;
		MAXMobikwikResponse resp = new MAXMobikwikResponse();
		

		JSONObject jsonRequestObj = getJsonReverseRequestObject(orderId, phoneNumber, amount);
		JSONObject jsonContentChecksumObj= new JSONObject();
		logger.info("The Mobikwik Reversal Request is = " +jsonRequestObj.toString());

		targetURL = Gateway.getProperty("application", "MobikwikReversalURL", "");
		//Create connection
		URL url = new URL(targetURL.trim().toString());

		connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("POST");

		//String urlParameters=jsonRequestObj.toString();   
		String marchantKey=MAXMobikwikConfig.get(MAXMobikwikTenderConstants.MERCHANT_ID);
		String CHECKSUMHASH =  ChecksumCalculator.calculateChecksum(marchantKey.toString().trim()
			, jsonRequestObj.toString());
	/*	String marchantKey=MAXMobikwikConfig.get(MAXMobikwikTenderConstants.MERCHANTKEYCONFIG);
	String CHECKSUMHASH =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(marchantKey.toString().trim(), jsonRequestObj.toString());*/
	jsonContentChecksumObj.put(MAXMobikwikTenderConstants.REQUEST, jsonRequestObj);
	jsonContentChecksumObj.put(MAXMobikwikTenderConstants.CHECKSUM,CHECKSUMHASH);
	String urlParameters=jsonContentChecksumObj.toString();
		logger.info("The Mobikwik Reversal checksum is " + CHECKSUMHASH);
		try {
			//Create connection

			connection = (HttpURLConnection)url.openConnection();
			
			String useProxy = "true";
			String httpProtocol ="";
			useProxy = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.USEPROXY);
			logger.info("UseProxy value " + MAXMobikwikConfig.get(MAXMobikwikTenderConstants.USEPROXY));
			if(useProxy.equalsIgnoreCase("true"))
			{
				//Rev 1.2 start
				//httpProtocol = MAXPaytmConfig.get(LSIPLWebOrderConstants.HTTPPROTOCOL);
				httpProtocol = Gateway.getProperty("application", MAXMobikwikTenderConstants.HTTPPROTOCOL, "");
				if( httpProtocol.equalsIgnoreCase("https"))
				{
				Authenticator.setDefault(new MAXProxyAuthenticator(MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PROXYUSER), MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PROXYPASSWORD)));
				System.setProperty("https.proxyHost",
						MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PROXYHOST));
				System.setProperty("https.proxyPort",
						MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PROXYPORT));
				}
				
				else if( httpProtocol.equalsIgnoreCase("http"))
				{
					Authenticator.setDefault(new MAXProxyAuthenticator(MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PROXYUSER), MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PROXYPASSWORD)));
					System.setProperty("http.proxyHost",
							MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PROXYHOST));
					System.setProperty("http.proxyPort",
							MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PROXYPORT));
				}
				//Rev 1.2 end
			}
			logger.info("ProxyHost value " + MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PROXYHOST));
			logger.info("ProxyPort value " + MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PROXYPORT));
			logger.info("ProxyUser value " + MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PROXYUSER));
			logger.info("ProxyPassword value " + MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PROXYPASSWORD));
			
			//connection.setRequestProperty(MAXMobikwikTenderConstants.PHONENUMBER,phoneNumber);
			

			/*connection.setRequestProperty(MAXMobikwikTenderConstants.CONTENTTYPE, MAXMobikwikConfig.get(MAXMobikwikTenderConstants.CONTENTTYPECONFIG));
			connection.setRequestProperty(MAXMobikwikTenderConstants.MID, MAXMobikwikConfig.get(MAXMobikwikTenderConstants.MERCHANTGUID));
			connection.setRequestProperty(MAXMobikwikTenderConstants.CHECKSUMHASH,CHECKSUMHASH);
			connection.setRequestProperty(MAXMobikwikTenderConstants.CONTENTLENGTH, Integer.toString(urlParameters.getBytes().length));*/
			
			//connection.setRequestProperty(MAXMobikwikTenderConstants.PHONENUMBER,mobileTotp);	
			connection.setRequestProperty(MAXMobikwikTenderConstants.CONTENTTYPE, MAXMobikwikConfig.get(MAXMobikwikTenderConstants.CONTENTTYPECONFIG).trim().toString());
			//connection.setRequestProperty(MAXMobikwikTenderConstants.MID, MAXMobikwikConfig.get(MAXMobikwikTenderConstants.SUBMERCHANT_ID));
			//connection.setRequestProperty(MAXMobikwikTenderConstants.CHECKSUMHASH,CHECKSUMHASH);
			connection.setRequestProperty(MAXMobikwikTenderConstants.CONTENTLENGTH, Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);
			//connection.setRequestProperty(key, value);
			connection.setConnectTimeout(Integer.parseInt(MAXMobikwikConfig.get(MAXMobikwikTenderConstants.CONNECTIONTIMEOUT)));
			connection.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
		wr.writeBytes(urlParameters);
		//	wr.writeBytes(urlParameters1);
			wr.close();
			int responseCode = connection.getResponseCode();
			
			resp.setReqRespStatus(MAXMobikwikTenderConstants.RESPONSERECEIVED);
			resp.setRespReceivedDate(new Date());
			InputStream is;
			if(responseCode == HttpURLConnection.HTTP_OK){
				is = connection.getInputStream();
			}else {
				is = connection.getErrorStream();
			}
			
			resp.setResponseCode(responseCode);
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+ 
			String line = "";
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			logger.info("The Mobikwik Reversal Response is= " + response);
			
			MAXMobikwikResponse responseMobikwik = convertMobikwikResponse(response.toString(), resp);
			//if(responseMobikwik != null && 
					//(responseMobikwik.getOrderId() == null || responseMobikwik.getOrderId().equals("null") || responseMobikwik.getOrderId().equals(null)))
		//	{
				//responsePaytm.setOrderId(jsonRequestObj.getJSONObject("request").getString(MAXMobikwikTenderConstants.MERCHANTORDERID));
		//	}
			return responseMobikwik;
		}
		catch(SocketTimeoutException e){
			logger.error("\nWith  Mobikwik reversal, timeout exception is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXMobikwikResponse();
			resp.setStatusMessage(MAXMobikwikTenderConstants.MOBIKWIKTIMEOUTERROR);
			resp.setRequestTypeA(MAXMobikwikTenderConstants.TIMEOUT);
			resp.setReqRespStatus(MAXMobikwikTenderConstants.TIMEOUT);
		//	resp.setOrderId(jsonRequestObj.getJSONObject("request").getString(MAXMobikwikTenderConstants.MERCHANTORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch (ConnectException e) {
			logger.error("\nWith  Mobikwik reversal, connection exception is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXMobikwikResponse();
			resp.setStatusMessage(MAXMobikwikTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXMobikwikTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		//	resp.setOrderId(jsonRequestObj.getJSONObject("request").getString(MAXMobikwikTenderConstants.MERCHANTORDERID));
			logger.error(e.getMessage());
			return resp;
		} 
		catch(NoRouteToHostException e){
			logger.error("\nWith  Mobikwik reversal, NoRouteToHostException is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXMobikwikResponse();
			resp.setStatusMessage(MAXMobikwikTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXMobikwikTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		//	resp.setOrderId(jsonRequestObj.getJSONObject("request").getString(MAXMobikwikTenderConstants.MERCHANTORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch(UnknownHostException e){
			logger.error("\nWith  Mobikwik reversal, UnknownHostException is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXMobikwikResponse();
			resp.setStatusMessage(MAXMobikwikTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXMobikwikTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			//resp.setOrderId(jsonRequestObj.getJSONObject("request").getString(MAXMobikwikTenderConstants.MERCHANTORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch (Exception e) {
			logger.error(" error while Mobikwik reversal " + e.getMessage());
			logger.error(e.getMessage());
		}  
		finally {
			if(connection != null) {
				connection.disconnect(); 
			}
		}
		/*if(resp.getReqRespStatus() == null || resp.getReqRespStatus().equals(""))
			resp.setReqRespStatus(MAXMobikwikTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);*/
		return resp;
	}


	public static JSONObject getJsonReverseRequestObject(String orderId, String phoneNumber, String amount) throws Exception {
		JSONObject jsonContentObj = null, jsonRequestObj = null;

		/*String currencyCode = MAXMobikwikTenderConstants.CURRENCY;
		String merchantGuid = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.MERCHANTGUID);
		//SimpleDateFormat myFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
		//String merchantOrderId = transactionId + myFormat.format(new Date()).toString();//"07302102002808-14-17 05:14:25";
		String merchantOrderId = orderId.toString().trim();
		String submerchantGuid = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.SUBMERCHANT_ID);*/
		/*String platformName = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.PLATFORMNAME);
		String ipAddress = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.IPADDRESS);
		String operationType = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.OPERATIONREFUND);
		String channel = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.CHANNEL);
		String version = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.VERSION);*/
		String submerchantGuid = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.SUBMERCHANT_ID);
		String merchantName = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.MERCHANT_NAME);
		String MerhantID = MAXMobikwikConfig.get(MAXMobikwikTenderConstants.MERCHANT_ID);
		jsonContentObj = new JSONObject();
		jsonRequestObj = new JSONObject(); 

		jsonRequestObj.put(MAXMobikwikTenderConstants.TOTALAMOUNT, amount);
		jsonRequestObj.put(MAXMobikwikTenderConstants.ORDER_ID, orderId.trim().toString());
		jsonRequestObj.put(MAXMobikwikTenderConstants.SUBMERCHANT_ID, submerchantGuid.trim().toString());
		//jsonContentObj.put(MAXMobikwikTenderConstants.REQUEST, jsonRequestObj);
		
		return jsonRequestObj;
	}
}

