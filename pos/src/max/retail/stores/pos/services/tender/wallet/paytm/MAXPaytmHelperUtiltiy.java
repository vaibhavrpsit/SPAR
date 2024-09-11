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

package max.retail.stores.pos.services.tender.wallet.paytm;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import max.retail.stores.domain.MAXPaytmResponse;
import max.retail.stores.domain.arts.MAXPaytmDataTransaction;
import max.retail.stores.domain.tender.MAXTenderPaytmIfc;
import max.retail.stores.domain.tender.paytm.MAXPaytmTenderConstants;
import max.retail.stores.pos.ado.tender.MAXTenderPaytmADO;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.domain.arts.ARTSTill;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.paytm.pg.merchant.CheckSumServiceHelper;

public class MAXPaytmHelperUtiltiy implements MAXPaytmTenderConstants{
//	static  int suffixMobileNumberCount=1;
	protected static final Logger logger = Logger.getLogger(MAXPaytmHelperUtiltiy.class);

	
	public static MAXPaytmResponse withdrawAmount(MAXTenderCargo cargo,String targetURL, String phoneNumber, String totp, String transactionId, String amount, String tillId, String storeId) throws Exception {
		HttpURLConnection connection = null;
		MAXPaytmResponse resp = new MAXPaytmResponse();
		
		JSONObject jsonContentObj = getJsonRequestObject(cargo,phoneNumber, totp, transactionId, amount);
		
		logger.info("The Paytm Withdraw Request is = " +jsonContentObj.toString());
		//Create connection
		
		URL url = new URL(targetURL);
		
		connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("POST");

		String urlParameters = jsonContentObj.toString();
		String CHECKSUMHASH = null;

	//String 
		try {
			String marchantKey = MAXPaytmConfig
					.get(MAXPaytmTenderConstants.MERCHANTKEYCONFIG);
			CHECKSUMHASH = CheckSumServiceHelper.getCheckSumServiceHelper()
					.genrateCheckSum(marchantKey.toString().trim(),jsonContentObj.toString());
			} catch (Exception e) {
				logger.warn(e);
			}
	

		try {
			//Create connection

			MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
			ARTSTill till = new ARTSTill(tillId, storeId);
			boolean dbStatus = paytmTrans.verifyDatabaseStatus(till);
			if(! dbStatus)
			{
				MAXPaytmResponse respDataException = new MAXPaytmResponse();
				respDataException.setDataException(Boolean.TRUE);
				return respDataException;
			}
			connection = (HttpURLConnection)url.openConnection();
		// Starts Changes for proxy - Karni
			String useProxy = "false";
			String httpProtocol ="";
			useProxy = MAXPaytmConfig.get(MAXPaytmTenderConstants.USEPROXY);
			logger.info("UseProxy value " + MAXPaytmConfig.get(MAXPaytmTenderConstants.USEPROXY));
			if(useProxy.equalsIgnoreCase("true"))
			{
				//Rev 1.2 start
				//httpProtocol = MAXPaytmConfig.get(LSIPLWebOrderConstants.HTTPPROTOCOL);
				httpProtocol = Gateway.getProperty("application", MAXPaytmTenderConstants.HTTPPROTOCOL, "");
				if( httpProtocol.equalsIgnoreCase("https"))
				{
				Authenticator.setDefault(new MAXProxyAuthenticator(MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYUSER), MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYPASSWORD)));
				System.setProperty("https.proxyHost",
						MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYHOST));
				System.setProperty("https.proxyPort",
						MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYPORT));
				}
				
				else if( httpProtocol.equalsIgnoreCase("http"))
				{
					Authenticator.setDefault(new MAXProxyAuthenticator(MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYUSER), MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYPASSWORD)));
					System.setProperty("http.proxyHost",
							MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYHOST));
					System.setProperty("http.proxyPort",
							MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYPORT));
				}
				//Rev 1.2 end
			}
			logger.info("ProxyHost value " + MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYHOST));
			logger.info("ProxyPort value " + MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYPORT));
			logger.info("ProxyUser value " + MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYUSER));
			logger.info("ProxyPassword value " + MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYPASSWORD));
			
			// end Changes for proxy - Karni
			connection.setRequestProperty(MAXPaytmTenderConstants.PHONENUMBER,phoneNumber);
			connection.setRequestProperty(MAXPaytmTenderConstants.OTP,totp);

			connection.setRequestProperty(MAXPaytmTenderConstants.CONTENTTYPE, MAXPaytmConfig.get(MAXPaytmTenderConstants.CONTENTTYPECONFIG));
			connection.setRequestProperty(MAXPaytmTenderConstants.MID, MAXPaytmConfig.get(MAXPaytmTenderConstants.MERCHANTGUID));
			connection.setRequestProperty(MAXPaytmTenderConstants.CHECKSUMHASH,CHECKSUMHASH);
			connection.setRequestProperty(MAXPaytmTenderConstants.CONTENTLENGTH, Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);
			connection.setConnectTimeout(Integer.parseInt(MAXPaytmConfig.get(MAXPaytmTenderConstants.CONNECTIONTIMEOUT)));
			connection.setReadTimeout(Integer.parseInt(MAXPaytmConfig.get(MAXPaytmTenderConstants.CONNECTIONTIMEOUT)));
			
			connection.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
			wr.writeBytes(urlParameters);
		//	System.out.println("url parameter="+urlParameters.toString());
			wr.close();
			int responseCode = connection.getResponseCode();
			//System.out.println("response"+responseCode);
			resp.setResponseCode(responseCode);
			resp.setReqRespStatus(MAXPaytmTenderConstants.RESPONSERECEIVED);
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
			logger.info("The Paytm Withdraw response string= " + response);
			
			MAXPaytmResponse responsePaytm = convertPaytmResponse(response.toString(), resp);
			return responsePaytm;
		}
		catch(ConnectException e){
			//SocketTimeoutException
			logger.error("\nWith  withdraw, timeout exception is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmResponse();
			resp.setStatusMessage(MAXPaytmTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			//resp.setOrderId(jsonContentObj.getJSONObject("request").getString(MAXPaytmTenderConstants.MERCHANTORDERID));
			logger.error(e.getMessage());
			return resp;
			//ConnectException
		}
		catch (SocketTimeoutException e) {
			logger.error("\nWith  withdraw, connection exception is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmResponse();
			
			resp.setStatusMessage(MAXPaytmTenderConstants.PAYTMTIMEOUTERROR);
			resp.setRequestTypeA(MAXPaytmTenderConstants.TIMEOUT);
			resp.setReqRespStatus(MAXPaytmTenderConstants.TIMEOUT);
		//	resp.setOrderId(jsonContentObj.getJSONObject("request").getString(MAXPaytmTenderConstants.MERCHANTORDERID));
			logger.error(e.getMessage());
			return resp;
		} 
		catch(NoRouteToHostException e){
			logger.error("\nWith  withdraw, NoRouteToHostException is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmResponse();
			resp.setStatusMessage(MAXPaytmTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			//resp.setOrderId(jsonContentObj.getJSONObject("request").getString(MAXPaytmTenderConstants.MERCHANTORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch(UnknownHostException e){
			logger.error("\nWith  withdraw, UnknownHostException is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmResponse();
			resp.setStatusMessage(MAXPaytmTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		//	resp.setOrderId(jsonContentObj.getJSONObject("request").getString(MAXPaytmTenderConstants.MERCHANTORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch (Exception e) {
			logger.error("With  withdraw, exception is " + e.getMessage());
		}  finally {
			if(connection != null) {
				connection.disconnect(); 
			}
		}
		if(resp.getReqRespStatus() == null || resp.getReqRespStatus().equals(""))
			resp.setReqRespStatus(MAXPaytmTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		return resp;
	}


	public static JSONObject getJsonRequestObject(MAXTenderCargo cargo, String phoneNumber, 
			String totp, String transactionId, String amount) throws Exception {
		JSONObject jsonContentObj = null, jsonRequestObj = null;
		String currencyCode = MAXPaytmTenderConstants.CURRENCY; 
		String merchantGuid = MAXPaytmConfig.get(MAXPaytmTenderConstants.MERCHANTGUID);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMddhhmmss");

		   String merchantOrderId = transactionId + myFormat.format(new Date()).toString();

	//String merchantOrderId = transactionId + myFormat.format(new Date()).toString();//"07302102002808-14-17 05:14:25";
		String industryType = MAXPaytmConfig.get(MAXPaytmTenderConstants.INDUSTRYTYPE);
		/* Rev 1.1 changes */
		String posId = transactionId.substring(0,8);
		String platformName = MAXPaytmConfig.get(MAXPaytmTenderConstants.PLATFORMNAME);
		String ipAddress = MAXPaytmConfig.get(MAXPaytmTenderConstants.IPADDRESS);
		String operationType = MAXPaytmConfig.get(MAXPaytmTenderConstants.OPERATIONWITHDRAW);
		String channel = MAXPaytmConfig.get(MAXPaytmTenderConstants.CHANNEL);
		String version = MAXPaytmConfig.get(MAXPaytmTenderConstants.VERSION);

		jsonContentObj = new JSONObject();
		jsonRequestObj = new JSONObject(); 

		jsonRequestObj.put(MAXPaytmTenderConstants.TOTALAMOUNT, amount);
		jsonRequestObj.put(MAXPaytmTenderConstants.CURRENCYCODE, currencyCode);
		jsonRequestObj.put(MAXPaytmTenderConstants.MERCHANTGUID, merchantGuid);
		jsonRequestObj.put(MAXPaytmTenderConstants.MERCHANTORDERID, merchantOrderId);
		jsonRequestObj.put(MAXPaytmTenderConstants.INDUSTRYTYPE, industryType);
		jsonRequestObj.put(MAXPaytmTenderConstants.POSID, posId);
		//  jsonRequestObj.put("comment", comment);
		jsonContentObj.put(MAXPaytmTenderConstants.REQUEST, jsonRequestObj);
		jsonContentObj.put(MAXPaytmTenderConstants.PLATFORMNAME, platformName);
		jsonContentObj.put(MAXPaytmTenderConstants.IPADDRESS, ipAddress);
		jsonContentObj.put(MAXPaytmTenderConstants.OPERATIONTYPE, operationType);
		jsonContentObj.put(MAXPaytmTenderConstants.CHANNEL, channel);
		jsonContentObj.put(MAXPaytmTenderConstants.VERSION,version);
		
		return jsonContentObj;
	}
	public static MAXPaytmResponse convertPaytmResponse(String response, MAXPaytmResponse resp)
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
	    			keyValue[0] = keyValue[1];
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
	    			if(keyValue[1] != null && keyValue[1].endsWith("\""))
	    				keyValue[1] = keyValue[1].substring(0, keyValue[1].length()-1);
	    		}
	    		++index;
	    	}
	    	map.put(keyValue[0].substring(keyValue[0].indexOf('\"') + 1, keyValue[0].length()), keyValue[1]);
	    }
		}
		catch(Exception e)
		{
			logger.error("Error in converting paytm response : " + e.getMessage());
		}
	    resp.setOrderId(map.get("orderId"));
		resp.setStatus(map.get("status"));
		resp.setStatusCode(map.get("statusCode"));
		resp.setStatusMessage(map.get("statusMessage"));
		resp.setWalletTxnId(map.get("walletSystemTxnId"));
		resp.setPaytmResponse(response);
		resp.setDataException(Boolean.FALSE);
		if(map.get("refundTxnGuid\"") != null)
		{
			resp.setWalletTxnId(map.get("refundTxnGuid\""));
		}
		return resp;
	}
	
	public static MAXPaytmResponse reverseAmount(String orderId, String targetURL, String phoneNumber, String amount, String orgTxnId) throws Exception {
		HttpURLConnection connection = null;
		MAXPaytmResponse resp = new MAXPaytmResponse();
		JSONObject jsonRequestObj = getJsonReverseRequestObject(orderId, phoneNumber, amount, orgTxnId);

		logger.info("The Paytm Reversal Request is = " +jsonRequestObj.toString());

		targetURL = Gateway.getProperty("application", "PaytmReversalURL", "");
		//Create connection
		URL url = new URL(targetURL);

		connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("POST");

		String urlParameters=jsonRequestObj.toString();   
		String marchantKey=MAXPaytmConfig.get(MAXPaytmTenderConstants.MERCHANTKEYCONFIG);
	String CHECKSUMHASH =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(marchantKey.toString().trim(), jsonRequestObj.toString());
		
		logger.info("The Paytm Reversal checksum is " + CHECKSUMHASH);
		try {
			//Create connection

			connection = (HttpURLConnection)url.openConnection();
			
			String useProxy = "true";
			String httpProtocol ="";
			useProxy = MAXPaytmConfig.get(MAXPaytmTenderConstants.USEPROXY);
			logger.info("UseProxy value " + MAXPaytmConfig.get(MAXPaytmTenderConstants.USEPROXY));
			if(useProxy.equalsIgnoreCase("true"))
			{
				//Rev 1.2 start
				//httpProtocol = MAXPaytmConfig.get(LSIPLWebOrderConstants.HTTPPROTOCOL);
				httpProtocol = Gateway.getProperty("application", MAXPaytmTenderConstants.HTTPPROTOCOL, "");
				if( httpProtocol.equalsIgnoreCase("https"))
				{
				Authenticator.setDefault(new MAXProxyAuthenticator(MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYUSER), MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYPASSWORD)));
				System.setProperty("https.proxyHost",
						MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYHOST));
				System.setProperty("https.proxyPort",
						MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYPORT));
				}
				
				else if( httpProtocol.equalsIgnoreCase("http"))
				{
					Authenticator.setDefault(new MAXProxyAuthenticator(MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYUSER), MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYPASSWORD)));
					System.setProperty("http.proxyHost",
							MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYHOST));
					System.setProperty("http.proxyPort",
							MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYPORT));
				}
				//Rev 1.2 end
			}
			logger.info("ProxyHost value " + MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYHOST));
			logger.info("ProxyPort value " + MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYPORT));
			logger.info("ProxyUser value " + MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYUSER));
			logger.info("ProxyPassword value " + MAXPaytmConfig.get(MAXPaytmTenderConstants.PROXYPASSWORD));
			
			connection.setRequestProperty(MAXPaytmTenderConstants.PHONENUMBER,phoneNumber);
			

			connection.setRequestProperty(MAXPaytmTenderConstants.CONTENTTYPE, MAXPaytmConfig.get(MAXPaytmTenderConstants.CONTENTTYPECONFIG));
			connection.setRequestProperty(MAXPaytmTenderConstants.MID, MAXPaytmConfig.get(MAXPaytmTenderConstants.MERCHANTGUID));
			connection.setRequestProperty(MAXPaytmTenderConstants.CHECKSUMHASH,CHECKSUMHASH);
			connection.setRequestProperty(MAXPaytmTenderConstants.CONTENTLENGTH, Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);
			connection.setConnectTimeout(Integer.parseInt(MAXPaytmConfig.get(MAXPaytmTenderConstants.CONNECTIONTIMEOUT)));
			connection.setReadTimeout(Integer.parseInt(MAXPaytmConfig.get(MAXPaytmTenderConstants.CONNECTIONTIMEOUT)));
			connection.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.close();
			int responseCode = connection.getResponseCode();
			
			resp.setReqRespStatus(MAXPaytmTenderConstants.RESPONSERECEIVED);
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
			logger.info("The Paytm Reversal Response is= " + response);
			
			MAXPaytmResponse responsePaytm = convertPaytmResponse(response.toString(), resp);
			logger.info("The Paytm Reversal cenverted Response is= " + responsePaytm);
			return responsePaytm;
		}
		catch(SocketTimeoutException e){
			logger.error("\nWith  paytm reversal, timeout exception is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmResponse();
			resp.setStatusMessage(MAXPaytmTenderConstants.PAYTMTIMEOUTERROR);
			resp.setRequestTypeA(MAXPaytmTenderConstants.TIMEOUT);
			resp.setReqRespStatus(MAXPaytmTenderConstants.TIMEOUT);
		//	resp.setOrderId(jsonRequestObj.getJSONObject("request").getString(MAXPaytmTenderConstants.MERCHANTORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch (ConnectException e) {
			logger.error("\nWith  paytm reversal, connection exception is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmResponse();
			resp.setStatusMessage(MAXPaytmTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		//	resp.setOrderId(jsonRequestObj.getJSONObject("request").getString(MAXPaytmTenderConstants.MERCHANTORDERID));
			logger.error(e.getMessage());
			return resp;
		} 
		catch(NoRouteToHostException e){
			logger.error("\nWith  paytm reversal, NoRouteToHostException is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmResponse();
			resp.setStatusMessage(MAXPaytmTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		//	resp.setOrderId(jsonRequestObj.getJSONObject("request").getString(MAXPaytmTenderConstants.MERCHANTORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch(UnknownHostException e){
			logger.error("\nWith  paytm reversal, UnknownHostException is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmResponse();
			resp.setStatusMessage(MAXPaytmTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			//resp.setOrderId(jsonRequestObj.getJSONObject("request").getString(MAXPaytmTenderConstants.MERCHANTORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch (Exception e) {
			logger.error(" error while paytm reversal " + e.getMessage());
			logger.error(e.getMessage());
		}  
		finally {
			if(connection != null) {
				connection.disconnect(); 
			}
		}
		logger.info("The Paytm Reversal after all the catches ");
		return resp;
	}


	public static JSONObject getJsonReverseRequestObject(String orderId, String phoneNumber, String amount, String orgTrnId) throws Exception {
		JSONObject jsonContentObj = null, jsonRequestObj = null;

		String currencyCode = MAXPaytmTenderConstants.CURRENCY;
		String merchantGuid = MAXPaytmConfig.get(MAXPaytmTenderConstants.MERCHANTGUID);
		//SimpleDateFormat myFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
		//String merchantOrderId = transactionId + myFormat.format(new Date()).toString();//"07302102002808-14-17 05:14:25";
		String merchantOrderId = orderId.toString().trim();
		
		String platformName = MAXPaytmConfig.get(MAXPaytmTenderConstants.PLATFORMNAME);
		String ipAddress = MAXPaytmConfig.get(MAXPaytmTenderConstants.IPADDRESS);
		String operationType = MAXPaytmConfig.get(MAXPaytmTenderConstants.OPERATIONREFUND);
		String channel = MAXPaytmConfig.get(MAXPaytmTenderConstants.CHANNEL);
		String version = MAXPaytmConfig.get(MAXPaytmTenderConstants.VERSION);

		jsonContentObj = new JSONObject();
		jsonRequestObj = new JSONObject(); 

		jsonRequestObj.put(MAXPaytmTenderConstants.TXNGUID, orgTrnId);
		jsonRequestObj.put(MAXPaytmTenderConstants.AMOUNT, amount);
		jsonRequestObj.put(MAXPaytmTenderConstants.CURRENCYCODE, currencyCode);
		jsonRequestObj.put(MAXPaytmTenderConstants.MERCHANTGUID, merchantGuid);
		jsonRequestObj.put(MAXPaytmTenderConstants.MERCHANTORDERID, merchantOrderId);
		jsonContentObj.put(MAXPaytmTenderConstants.REQUEST, jsonRequestObj);
		
		jsonContentObj.put(MAXPaytmTenderConstants.IPADDRESS, ipAddress);
		jsonContentObj.put(MAXPaytmTenderConstants.PLATFORMNAME, platformName);
		jsonContentObj.put(MAXPaytmTenderConstants.OPERATIONTYPE, operationType);
		jsonContentObj.put(MAXPaytmTenderConstants.CHANNEL, channel);
		jsonContentObj.put(MAXPaytmTenderConstants.VERSION,version);
		
		return jsonContentObj;
	}
}

