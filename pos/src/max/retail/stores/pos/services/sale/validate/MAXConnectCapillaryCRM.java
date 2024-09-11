/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * Copyright (c) 2015 Lifestyle.    All Rights Reserved.  **/

package max.retail.stores.pos.services.sale.validate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.gate.Gateway;
import sun.misc.BASE64Encoder;


/**
 * @author mohd.arif
 *
 */
public class MAXConnectCapillaryCRM 
{
	private static Logger logger = Logger.getLogger(MAXConnectCapillaryCRM.class);
	
	public static HashMap capillaryDataMap = new HashMap();
	
	/**
	 * Return Brand-Specific Details
	 * @return
	 */
	public void setUrlBrandWiseData()
	{
		String crmCapillaryBaseURL	= Gateway.getProperty("application", "CRMCapillaryBaseURL", "");
		String crmCapillaryUserId	= Gateway.getProperty("application", "CRMCapillaryUserId", "");
		String crmCapillaryPwd		= Gateway.getProperty("application", "CRMCapillaryPwd", "");
		String crmHttpHost			= Gateway.getProperty("application", "ProxyHost", "");
		String crmHttpPort			= Gateway.getProperty("application", "ProxyPort", "");
		String proxyUsername 		= Gateway.getProperty("application", "ProxyUsername", "");
		String proxyPassword		= Gateway.getProperty("application", "ProxyPassword", "");
		MAXConnectCapillaryCRM.capillaryDataMap.put("CRMCapillaryBaseURL", crmCapillaryBaseURL.trim());
		MAXConnectCapillaryCRM.capillaryDataMap.put("CRMCapillaryUserId", crmCapillaryUserId.trim());
		MAXConnectCapillaryCRM.capillaryDataMap.put("CRMCapillaryPwd", crmCapillaryPwd.trim());
		/*MAXConnectCapillaryCRM.capillaryDataMap.put("ProxyHost", crmHttpHost.trim());
		MAXConnectCapillaryCRM.capillaryDataMap.put("ProxyPort", crmHttpPort.trim());
		//Rev 1.1 Start
		MAXConnectCapillaryCRM.capillaryDataMap.put("ProxyUsername", proxyUsername.trim());
		MAXConnectCapillaryCRM.capillaryDataMap.put("ProxyPassword", proxyPassword.trim());*/
		//Rev 1.1 End
		logger.info(">> capillaryDataMap ::"+MAXConnectCapillaryCRM.capillaryDataMap);
	}
	
	/**
	 * 
	 * @param capillaryCRMObj
	 * @return
	 */
	public MAXCapillaryCRM processCRMRequest(MAXCapillaryCRM capillaryCRMObj)
	{
		setUrlBrandWiseData();
		// Coupons
		if(capillaryCRMObj.getRequestAction().equalsIgnoreCase(MAXUtilityConstantsIfc.CRM_CAPILLARY_ACTION_COUPON_IS_REDEEMABLE)
				|| capillaryCRMObj.getRequestAction().equalsIgnoreCase(MAXUtilityConstantsIfc.CRM_CAPILLARY_ACTION_COUPON_REDEEM))
		{
			capillaryCRMObj = processCouponAction(capillaryCRMObj);
		}
		return capillaryCRMObj;
	}
	
	/**
	 * Use for executing the Request with needed parameters
	 * 
	 * @param targetURL
	 * @param urlParameters
	 */
	public MAXCapillaryCRM executePost(MAXCapillaryCRM capillaryCRMObj) 
	{
		URL url;
		int responseCode;
		HttpURLConnection connection = null;
		try 
		{
			// Create connection
			url 					= new URL(capillaryCRMObj.getCRM_URL());
			String urlParameters 	= capillaryCRMObj.getRequestMessage(); 
			logger.info("CRM executePost() :: url:"+url+" , urlParameters:"+urlParameters);
			String timeOut 			= Gateway.getProperty("application", "LoyaltytimeOutInMilliSeconds", "5000");;
			Properties systemProperties = System.getProperties();
			
			//comment by arif ..proxy server is no required for MAX..
			/*if(((String)MAXConnectCapillaryCRM.capillaryDataMap.get("ProxyHost")).trim().length() > 0)
			{
				systemProperties.setProperty("http.proxyHost",(String) MAXConnectCapillaryCRM.capillaryDataMap.get("ProxyHost"));
				systemProperties.setProperty("http.proxyPort",(String) MAXConnectCapillaryCRM.capillaryDataMap.get("ProxyPort"));
				
				Authenticator.setDefault(new MAXProxyAuthenticator((String) MAXConnectCapillaryCRM.capillaryDataMap
						.get("ProxyUsername"), (String) MAXConnectCapillaryCRM.capillaryDataMap
						.get("ProxyPassword")));
				
			}*/
			connection 				= (HttpURLConnection) url.openConnection();
			System.getProperties().setProperty("sun.net.client.defaultConnectTimeout", timeOut);
			System.getProperties().setProperty("sun.net.client.defaultReadTimeout", timeOut);
			connection.setRequestMethod(capillaryCRMObj.getRequestMethod());
			connection.setRequestProperty("Content-Type","application/json; charset=utf-8");
			connection.setRequestProperty("Content-Language", "en-US");
		    connection.setRequestProperty( "Authorization", getAuthorizationHeader());
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			if(capillaryCRMObj.getRequestMethod().equalsIgnoreCase(MAXUtilityConstantsIfc.CRM_CAPILLARY_METHOD_POST))
			{
				connection.setRequestProperty("Content-Length","" + Integer.toString(urlParameters.getBytes().length));
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();
			}
			// Get Response
			responseCode = connection.getResponseCode();
			String responseMessage = connection.getResponseMessage();
			logger.info("executePost() :: responseCode:"+responseCode+" , ResponseMessage:"+connection.getResponseMessage());
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) 
			{
				response.append(line);
				
				response.append('\n');
			}
			rd.close();
		logger.info("**** CRM RESPONSE: " +response.toString());
			
			capillaryCRMObj.setConnResponseCode(String.valueOf(connection.getResponseCode()));
			capillaryCRMObj.setConnResponseMessage(response.toString());
			return capillaryCRMObj;
		} 
		
		catch (MalformedURLException e) 
		{
			logger.error("Response not recieved::" + e.getMessage() + "");
			capillaryCRMObj.setConnResponseCode("500");
			return capillaryCRMObj;
		}
		
		catch (Exception e) 
		{
			logger.error("Error in sending Request" + e.getMessage() + "");
			try 
			{
				responseCode = connection.getResponseCode();
				capillaryCRMObj.setConnResponseCode(String.valueOf(connection.getResponseCode()));
			} 
			catch (IOException e1) 
			{
				logger.error("IO Exception Caught::" + e1.getMessage() + "");
				capillaryCRMObj.setConnResponseCode("500");
				return capillaryCRMObj;
			}
			return capillaryCRMObj;
		} 
		finally 
		{
			if (connection != null) 
			{
				connection.disconnect();
				Properties systemProperties = System.getProperties();
				systemProperties.setProperty("http.proxyHost","");
				systemProperties.setProperty("http.proxyPort","");
			}
		}
	}
	
	/**
	 * Process Coupons 
	 * @param argAction
	 */
	protected MAXCapillaryCRM processCouponAction(MAXCapillaryCRM capillaryCRMObj)
	{
		String argAction = capillaryCRMObj.getRequestAction();
		logger.debug("CRM :: processCouponAction:"+argAction);

		if(argAction.equalsIgnoreCase(MAXUtilityConstantsIfc.CRM_CAPILLARY_ACTION_COUPON_IS_REDEEMABLE))
		{
			String mobileNo = ((String) ((HashMap) capillaryCRMObj
					.getTransData()).get("MOBILE_NO"));
			String externalId = ((String) ((HashMap) capillaryCRMObj
					.getTransData()).get("CUST_ID"));
			String couponNo = ((String) ((HashMap) capillaryCRMObj
					.getTransData()).get("COUPON_NO"));
			String email = ((String) ((HashMap) capillaryCRMObj
					.getTransData()).get("CUST_EMAIL"));
			capillaryCRMObj
					.setCRM_URL(MAXConnectCapillaryCRM.capillaryDataMap
							.get("CRMCapillaryBaseURL")
							+ "/coupon/isredeemable?format=json&mobile="
							+ mobileNo
							+ "&external_id="
							+ externalId
							+ "&email="
							+ email
							+ "&code="
							+ couponNo
							+ "&details=extended");
			
			capillaryCRMObj.setRequestMethod(MAXUtilityConstantsIfc.CRM_CAPILLARY_METHOD_GET);
			// Hit the webservice
			capillaryCRMObj = executePost(capillaryCRMObj);
			String url = capillaryCRMObj.getCRM_URL();
			logger.info(">> Request URL ::"+url);
			if(capillaryCRMObj.getConnResponseCode().equalsIgnoreCase("200"))
				capillaryCRMObj = MAXCreateMessagesCapillaryCRM.processCouponIsRedeemableResponseMessage(capillaryCRMObj);
		}
		else if(argAction.equalsIgnoreCase(MAXUtilityConstantsIfc.CRM_CAPILLARY_ACTION_COUPON_REDEEM))
		{
			capillaryCRMObj.setCRM_URL(MAXConnectCapillaryCRM.capillaryDataMap.get("CRMCapillaryBaseURL") + "/coupon/redeem?format=json");
			capillaryCRMObj.setRequestMethod(MAXUtilityConstantsIfc.CRM_CAPILLARY_METHOD_POST);
			// Create Request JSON Message
			String requestJsonMsg = MAXCreateMessagesCapillaryCRM.createCouponRedeemRequestMessage(capillaryCRMObj);
			capillaryCRMObj.setRequestMessage(requestJsonMsg);
			// Hit the webservice
			capillaryCRMObj = executePost(capillaryCRMObj);
			if(capillaryCRMObj.getConnResponseCode().equalsIgnoreCase("200"))
				capillaryCRMObj = MAXCreateMessagesCapillaryCRM.processCouponRedeemResponseMessage(capillaryCRMObj);
		}
		
		return capillaryCRMObj;
	}
	
	
	/**
	 * 
	 * @return
	 */
	protected String getAuthorizationHeader()
	{
		// stuff the Authorization request header
		String authHeader = "";
		String username = (String)MAXConnectCapillaryCRM.capillaryDataMap.get("CRMCapillaryUserId");
		String password = (String)MAXConnectCapillaryCRM.capillaryDataMap.get("CRMCapillaryPwd");	
		String md5_password = encode_MD5(password);
	    byte[] encodedPassword = ( username + ":" + md5_password ).getBytes();
	    BASE64Encoder encoder = new BASE64Encoder();  
	    authHeader = "Basic " + encoder.encode(encodedPassword);
	    
	/*    if(authHeader!=null)
	    {
	    	String split[]=authHeader.split("\r\n");
	    	if(split.length>1)
		    { 
	    		authHeader = "";
	    		for(int i=0;i<split.length;i++)
	    		{
	    			authHeader = authHeader + split[i];
	    		}
		    }
	    }*/    
	    return authHeader;
	}
	
	/**
	 * 
	 * @param md5
	 * @return
	 */
	public static String encode_MD5(String md5) {
		try {
			java.security.MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(md5.getBytes());
	//		String mdHash = String.format("%032x",new BigInteger(1, digest.digest()));
			String mdHash = new BigInteger(1,digest.digest()).toString(16);
			 while (mdHash.length() < 32) {
				 mdHash = "0" + mdHash;
	            }
			return mdHash;
		} catch (java.security.NoSuchAlgorithmException e) {
			logger.error("encode_MD5::" + e + "");
		}
		return null;
	}
}
