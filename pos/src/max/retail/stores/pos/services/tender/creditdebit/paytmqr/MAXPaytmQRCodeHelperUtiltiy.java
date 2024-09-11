/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *      Copyright (c) 2022-2023 MAXHyperMarket, Inc.    All Rights Reserved.    
 * Rev 1.0 		March 28, 2022    Kamlesh Pant   Paytm QR Integration
 * Initial revision.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit.paytmqr;

import java.io.BufferedReader; 

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import Paytm.QrDisplay;

import max.retail.stores.domain.paytm.MAXPaytmQRCodeResponse;
import max.retail.stores.domain.tender.amazonpay.MAXAmazonPayTenderConstants;
import max.retail.stores.domain.tender.paytmqr.MAXPaytmQRCodeTenderConstants;


public class MAXPaytmQRCodeHelperUtiltiy implements MAXPaytmQRCodeTenderConstants{
	protected static final Logger logger = Logger.getLogger(MAXPaytmQRCodeHelperUtiltiy.class);

	public static MAXPaytmQRCodeResponse generateQRCode(String targetURL, String transactionId, String amount, String tillId, String storeId) throws Exception 
	{
		//System.out.println("MAXPaytmQRCodeHelperUtiltiy :");
		HttpURLConnection connection = null;
		MAXPaytmQRCodeResponse resp = new MAXPaytmQRCodeResponse();
		
		JSONObject jsonContentObj = getCreateQRCodeJsonRequestObject(transactionId,amount,storeId);

		logger.info("The Paytm generate QR code Request is = " +jsonContentObj.toString());
		//System.out.println("Request is :" +jsonContentObj.toString());

		try {
		
			/*
			 * MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
			 * 
			 * ARTSTill till = new ARTSTill(tillId, storeId); boolean dbStatus =
			 * paytmTrans.verifyDatabaseStatus(till); if(! dbStatus) {
			 * MAXPaytmQRCodeResponse respDataException = new MAXPaytmQRCodeResponse();
			 * respDataException.setDataException(Boolean.TRUE); return respDataException; }
			 */
			
			//Create connection
			URL url = new URL(targetURL);

			String urlParameters = jsonContentObj.toString();
	
			java.lang.System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
			connection=(HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
		//	connection.setRequestMethod(MAXPaytmQRCodeTenderConstants.REQUESTMETHODPOST);
			
		/*
		 * System.out.println("68 connection ::"+connection.getResponseCode());
		 * System.out.println("69 connection ::"+connection.getResponseMessage());
		 */
			
			//System.out.println(conn.getHeaderFields()); //if i comment this code,everything is ok, if not the 'Cannot write output after reading input' error happens
			//connection.connect();
			/*
			 * SSLContext sc = SSLContext.getInstance("TLSv1.2"); TrustManager[] certs = new
			 * TrustManager[] { new X509TrustManager() { public X509Certificate[]
			 * getAcceptedIssuers() { return null; }
			 * 
			 * public void checkClientTrusted(X509Certificate[] certs, String t) { }
			 * 
			 * public void checkServerTrusted(X509Certificate[] certs, String t) { } } };
			 * sc.init(null, certs, new java.security.SecureRandom()); ((HttpURLConnection)
			 * connection).setSSLSocketFactory(sc.getSocketFactory()); ((HttpURLConnection)
			 * connection).setHostnameVerifier(new HostnameVerifier() { public boolean
			 * verify(String hostname, SSLSession session) { return true; } });
			 */
			
			
		/*	
			String useProxy = "true";
			String httpProtocol ="" ;
			useProxy = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.USEPROXY);
			logger.info("UseProxy value " + MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.USEPROXY));
			
			 * if(useProxy.equalsIgnoreCase("true")) { //Rev 1.2 start //httpProtocol =
			 * MAXPaytmConfig.get(MAXPaytmQRCodeTenderConstants.HTTPPROTOCOL); httpProtocol
			 * = Gateway.getProperty("application",
			 * MAXPaytmQRCodeTenderConstants.HTTPPROTOCOL, ""); if(
			 * httpProtocol.equalsIgnoreCase("https")) { Authenticator.setDefault(new
			 * MAXProxyAuthenticator(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.
			 * PROXYUSER),
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPASSWORD)));
			 * System.setProperty("https.proxyHost",
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYHOST));
			 * System.setProperty("https.proxyPort",
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPORT)); }
			 * 
			 * else if( httpProtocol.equalsIgnoreCase("http")) {
			 * Authenticator.setDefault(new
			 * MAXProxyAuthenticator(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.
			 * PROXYUSER),
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPASSWORD)));
			 * System.setProperty("http.proxyHost",
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYHOST));
			 * System.setProperty("http.proxyPort",
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPORT)); }
			 * 
			 * 
			 * //Rev 1.2 end } logger.info("ProxyHost value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYHOST));
			 * logger.info("ProxyPort value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPORT));
			 * logger.info("ProxyUser value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYUSER));
			 * logger.info("ProxyPassword value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPASSWORD));
			 * 
			 */
			connection.setRequestProperty(MAXAmazonPayTenderConstants.CONTENTTYPE,MAXAmazonPayTenderConstants.JSON);
			//connection.setRequestProperty(MAXPaytmQRCodeTenderConstants.CONTENTTYPE, MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.CONTENTTYPECONFIG));
			//connection.setRequestProperty(MAXPaytmQRCodeTenderConstants.CONTENTLENGTH, Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);
			//connection.setConnectTimeout(Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.CONNECTIONTIMEOUT)));
			connection.setDoOutput(true);
			
		//	connection.setAllowUserInteraction(true);
		/*
		 * int responseCode = connection.getResponseCode();
		 * System.out.println("hello :"+connection.getURL());
		 * System.out.println("urlParameters :"+urlParameters);
		 * System.out.println("responseCode ::"+responseCode);
		 */
			//System.out.println(connection.getOutputStream().toString());
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			
			//System.out.println("Write"+wr);
			wr.writeBytes(urlParameters);
			wr.close();
			int responseCode = connection.getResponseCode();
			/*
			 * System.out.println("hello :"+connection.getURL());
			 * System.out.println("urlParameters :"+urlParameters);
			 * System.out.println("responseCode ::"+responseCode);
			 */
			//int responseCode = connection.getResponseCode();
			
			resp.setResponseCode(responseCode);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSERECEIVED);
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
				//response.append('\r');
			}
			rd.close();
			logger.info("Response : " + response);
			//System.out.println("response is  :"+response);
			
			JSONObject jsonResponseObject = new JSONObject(response.toString());
			
			MAXPaytmQRCodeResponse responsePaytm = convertPaytmResponse(jsonResponseObject, resp);
			//System.out.println("responsePaytm :"+responsePaytm.getQrData());
			
			
			if(responsePaytm != null && 
					(responsePaytm.getOrderId() == null || responsePaytm.getOrderId().equals("null") || responsePaytm.getOrderId().equals(null)))
			{
				responsePaytm.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
			}
			//System.out.println("response is 189 :"+responsePaytm);
			return responsePaytm;
		}
		catch(SocketTimeoutException e){
			logger.error("\nWith  withdraw, timeout exception is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmQRCodeResponse();
			resp.setResultMessage(MAXPaytmQRCodeTenderConstants.PAYTMTIMEOUTERROR);
			resp.setRequestTypeA(MAXPaytmQRCodeTenderConstants.TIMEOUT);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.TIMEOUT);
			resp.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch (ConnectException e) {
			logger.error("\nWith  withdraw, connection exception is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmQRCodeResponse();
			resp.setResultMessage(MAXPaytmQRCodeTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			resp.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
			logger.error(e.getMessage());
			return resp;
		} 
		catch(NoRouteToHostException e){
			logger.error("\nWith  withdraw, NoRouteToHostException is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmQRCodeResponse();
			resp.setResultMessage(MAXPaytmQRCodeTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			resp.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch(UnknownHostException e){
			logger.error("\nWith  withdraw, UnknownHostException is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmQRCodeResponse();
			resp.setResultMessage(MAXPaytmQRCodeTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			resp.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
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
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		return resp;
	}


	public static JSONObject getCreateQRCodeJsonRequestObject(String transactionId, String amount,String storeId) throws Exception 
	{
		JSONObject body = null, paytmParams = null;
		//System.out.println("Helper inside getCreateQRCodeJsonRequestObject");
		//String currencyCode = MAXPaytmQRCodeTenderConstants.CURRENCY;
		
		String clientId = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.CLIENTID);
		//System.out.println("clientId"+clientId);
		String version = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.VERSION);
		
		String mid = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.MID);
		SimpleDateFormat myFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
		String orderId = transactionId + myFormat.format(new Date()).toString();//"07302102002808-14-17 05:14:25";
		//String orderId = transactionId;
		String businessType = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.BUSINESSTYPE);
		/* Rev 1.1 changes */
		String posId = transactionId.substring(0,8);
		
		//System.out.println("posId :"+posId);
		
		//Calendar date = Calendar.getInstance();
		//long timeInSecs = date.getTimeInMillis();
		//SimpleDateFormat expiryDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//System.out.println("expiryDateFormat :"+expiryDateFormat);
		//int qrExpiryTime = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.EXPIRYMINUTES));
		//System.out.println("qrExpiryTime :"+qrExpiryTime);
		//int qrExpiryTime = 10000;
		//String expiryDate = expiryDateFormat.format(new Date(timeInSecs + (qrExpiryTime * 60 * 1000))).toString();

		//System.out.println("expiryDate :"+expiryDate);
		body = new JSONObject();
		paytmParams = new JSONObject(); 

		body.put(MAXPaytmQRCodeTenderConstants.AMOUNT, amount);
		body.put(MAXPaytmQRCodeTenderConstants.MID, mid);
		body.put(MAXPaytmQRCodeTenderConstants.ORDERID, orderId);
		body.put(MAXPaytmQRCodeTenderConstants.BUSINESSTYPE, businessType);
		body.put(MAXPaytmQRCodeTenderConstants.POSID, posId);
		//body.put(MAXPaytmQRCodeTenderConstants.EXPIRYDATE, expiryDate);
		
		//System.out.println("body :"+body);
		//String storeGstinNumber = getStoreGstin(storeId);
		
		 //JSONObject gstinfo = new JSONObject(); 
		 
		// SimpleDateFormat invoiceDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		 
		 
		// StringBuffer stringBuffer = new StringBuffer(invoiceDateFormat.format(new Date()).toString());
		 
	      // String invoiceDate =  stringBuffer.insert(stringBuffer.length()-2, ':').toString();
	        
		 
		// gstinfo.put(MAXPaytmQRCodeTenderConstants.GSTIN, storeGstinNumber);
		 //gstinfo.put(MAXPaytmQRCodeTenderConstants.GSTBRKUP, taxInfo);
		 //gstinfo.put(MAXPaytmQRCodeTenderConstants.INVOICENO, transactionId);
		// gstinfo.put(MAXPaytmQRCodeTenderConstants.INVOICEDATE, invoiceDate); //invoiceDateFormat.format(new Date()));
		 
		
		// body.put(MAXPaytmQRCodeTenderConstants.GSTINFORMATION, gstinfo);
		 //System.out.println("body 2 :"+body);
		QrDisplay qrDisplay = new QrDisplay(); 
		String checksum = qrDisplay.generateSignature(body.toString(), MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.MERCHANTKEYCONFIG));

        JSONObject head = new JSONObject(); 
        
        head.put(MAXPaytmQRCodeTenderConstants.CLIENTID, clientId); 
        head.put(MAXPaytmQRCodeTenderConstants.VERSION, version); 
        head.put(MAXPaytmQRCodeTenderConstants.SIGNATURE, checksum);

        paytmParams.put(MAXPaytmQRCodeTenderConstants.HEAD, head);
        paytmParams.put(MAXPaytmQRCodeTenderConstants.BODY, body); 
       
        
        //System.out.println("paytmParams :"+paytmParams);

        return paytmParams;
	}
	
	
	public static MAXPaytmQRCodeResponse convertPaytmResponse(JSONObject jsonResponse, MAXPaytmQRCodeResponse resp)
	{
		try {
		JSONObject resultInfo  = jsonResponse.getJSONObject("body").getJSONObject("resultInfo");
		//System.out.println("resultInfo :"+resultInfo);
		resp.setResultStatus(resultInfo.getString("resultStatus"));
		resp.setResultCode(resultInfo.getString("resultCode"));
		resp.setResultMessage(resultInfo.getString("resultMsg"));
		resp.setPaytmResponse(jsonResponse.toString());
		resp.setDataException(Boolean.FALSE);	
		if(resultInfo.getString("resultCode").equalsIgnoreCase("QR_0001")) {
			resp.setQrCodeId(jsonResponse.getJSONObject("body").getString("qrCodeId"));
			resp.setQrData(jsonResponse.getJSONObject("body").getString("qrData"));
			resp.setImage(jsonResponse.getJSONObject("body").getString("image"));
			//System.out.println("337 ::"+jsonResponse.getJSONObject("body").getString("image"));
		}
		}catch(Exception e) {
			logger.error(e.getMessage());
			//System.out.println("339 :"+e);
		}
		//System.out.println("330 resp:"+resp);
		return resp;
	}

	
	public static MAXPaytmQRCodeResponse checkTransactionStatus(String targetURL, String orderId, String tillId, String storeId) throws Exception {
		HttpURLConnection connection = null;
		MAXPaytmQRCodeResponse resp = new MAXPaytmQRCodeResponse();
		
		JSONObject jsonContentObj = getCheckStatusJsonRequestObject(orderId);

		logger.info("The Paytm check status Request is = " +jsonContentObj.toString());

		try {
		
			//MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
			
			/*
			 * ARTSTill till = new ARTSTill(tillId, storeId); boolean dbStatus =
			 * paytmTrans.verifyDatabaseStatus(till); if(! dbStatus) {
			 * MAXPaytmQRCodeResponse respDataException = new MAXPaytmQRCodeResponse();
			 * respDataException.setDataException(Boolean.TRUE); return respDataException; }
			 */
			
			//Create connection
			URL url = new URL(targetURL);

			String urlParameters = jsonContentObj.toString();
			connection = (HttpURLConnection)url.openConnection();
			
			/*
			 * SSLContext sc = SSLContext.getInstance("TLSv1.2"); TrustManager[] certs = new
			 * TrustManager[] { new X509TrustManager() { public X509Certificate[]
			 * getAcceptedIssuers() { return null; }
			 * 
			 * public void checkClientTrusted(X509Certificate[] certs, String t) { }
			 * 
			 * public void checkServerTrusted(X509Certificate[] certs, String t) { } } };
			 * sc.init(null, certs, new java.security.SecureRandom()); ((HttpURLConnection)
			 * connection).setSSLSocketFactory(sc.getSocketFactory()); ((HttpURLConnection)
			 * connection).setHostnameVerifier(new HostnameVerifier() { public boolean
			 * verify(String hostname, SSLSession session) { return true; } });
			 */
			connection.setRequestMethod("POST");
			
			/*
			 * String useProxy = "true"; String httpProtocol ="" ; useProxy =
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.USEPROXY);
			 * logger.info("UseProxy value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.USEPROXY));
			 * if(useProxy.equalsIgnoreCase("true")) { //Rev 1.2 start //httpProtocol =
			 * MAXPaytmConfig.get(MAXPaytmQRCodeTenderConstants.HTTPPROTOCOL); httpProtocol
			 * = Gateway.getProperty("application",
			 * MAXPaytmQRCodeTenderConstants.HTTPPROTOCOL, ""); if(
			 * httpProtocol.equalsIgnoreCase("https")) { Authenticator.setDefault(new
			 * MAXProxyAuthenticator(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.
			 * PROXYUSER),
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPASSWORD)));
			 * System.setProperty("https.proxyHost",
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYHOST));
			 * System.setProperty("https.proxyPort",
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPORT)); }
			 * 
			 * else if( httpProtocol.equalsIgnoreCase("http")) {
			 * Authenticator.setDefault(new
			 * MAXProxyAuthenticator(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.
			 * PROXYUSER),
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPASSWORD)));
			 * System.setProperty("http.proxyHost",
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYHOST));
			 * System.setProperty("http.proxyPort",
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPORT)); }
			 * 
			 * 
			 * //Rev 1.2 end } logger.info("ProxyHost value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYHOST));
			 * logger.info("ProxyPort value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPORT));
			 * logger.info("ProxyUser value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYUSER));
			 * logger.info("ProxyPassword value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPASSWORD));
			 */
		

			connection.setRequestProperty(MAXPaytmQRCodeTenderConstants.CONTENTTYPE, MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.CONTENTTYPECONFIG));
			connection.setRequestProperty(MAXPaytmQRCodeTenderConstants.CONTENTLENGTH, Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);
			connection.setConnectTimeout(Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.CONNECTIONTIMEOUT)));
			connection.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.close();
			int responseCode = connection.getResponseCode();
			
			resp.setResponseCode(responseCode);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSERECEIVED);
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
				//response.append('\r');
			}
			rd.close();
			logger.info("The check status response string= " + response);
			//System.out.println("451 response :"+response);
			
			JSONObject jsonResponseObject = new JSONObject(response.toString());
			
			MAXPaytmQRCodeResponse responsePaytm = convertPaytmResponseForCheckStatus(jsonResponseObject, resp);
			
			if(responsePaytm != null && 
					(responsePaytm.getOrderId() == null || responsePaytm.getOrderId().equals("null") || responsePaytm.getOrderId().equals(null)))
			{
				responsePaytm.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
			}
			return responsePaytm;
		}
		catch(SocketTimeoutException e){
			logger.error("\nWith  withdraw, timeout exception is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmQRCodeResponse();
			resp.setResultMessage(MAXPaytmQRCodeTenderConstants.PAYTMTIMEOUTERROR);
			resp.setRequestTypeA(MAXPaytmQRCodeTenderConstants.TIMEOUT);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.TIMEOUT);
			resp.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch (ConnectException e) {
			logger.error("\nWith  withdraw, connection exception is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmQRCodeResponse();
			resp.setResultMessage(MAXPaytmQRCodeTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			resp.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
			logger.error(e.getMessage());
			return resp;
		} 
		catch(NoRouteToHostException e){
			logger.error("\nWith  withdraw, NoRouteToHostException is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmQRCodeResponse();
			resp.setResultMessage(MAXPaytmQRCodeTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			resp.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch(UnknownHostException e){
			logger.error("\nWith  withdraw, UnknownHostException is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmQRCodeResponse();
			resp.setResultMessage(MAXPaytmQRCodeTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			resp.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
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
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		return resp;
	}


	public static JSONObject getCheckStatusJsonRequestObject(String orderId) throws Exception {
		JSONObject body = null, paytmParams = null;

		String clientId = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.CLIENTID);
		String version = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.VERSION);
		
		String mid = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.MID);

		body = new JSONObject();
		paytmParams = new JSONObject(); 

		body.put(MAXPaytmQRCodeTenderConstants.MID, mid);
		body.put(MAXPaytmQRCodeTenderConstants.ORDERID, orderId);
				
		QrDisplay qrDisplay = new QrDisplay(); 
		String checksum = qrDisplay.generateSignature(body.toString(), MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.MERCHANTKEYCONFIG));

        JSONObject head = new JSONObject(); 
        
        head.put(MAXPaytmQRCodeTenderConstants.CLIENTID, clientId); 
        head.put(MAXPaytmQRCodeTenderConstants.VERSION, version); 
        head.put(MAXPaytmQRCodeTenderConstants.SIGNATURE, checksum);

        paytmParams.put(MAXPaytmQRCodeTenderConstants.BODY, body); 
        
        paytmParams.put(MAXPaytmQRCodeTenderConstants.HEAD, head);

        return paytmParams;
	}

	public static MAXPaytmQRCodeResponse convertPaytmResponseForCheckStatus(JSONObject jsonResponse, MAXPaytmQRCodeResponse resp)
	{
		try {
		JSONObject resultInfo  = jsonResponse.getJSONObject("body").getJSONObject("resultInfo");
		resp.setResultStatus(resultInfo.getString("resultStatus"));
		resp.setResultCode(resultInfo.getString("resultCode"));
		resp.setResultMessage(resultInfo.getString("resultMsg"));
		resp.setPaytmResponse(jsonResponse.toString());
		resp.setDataException(Boolean.FALSE);	
		if(resultInfo.getString("resultCode").equalsIgnoreCase("01")) {
			
			resp.setTxnId(jsonResponse.getJSONObject("body").getString("txnId"));
			resp.setBankTxnId(jsonResponse.getJSONObject("body").getString("bankTxnId"));
			resp.setOrderId(jsonResponse.getJSONObject("body").getString("orderId"));
			resp.setAmountPaid(jsonResponse.getJSONObject("body").getString("txnAmount"));
			resp.setTxtType(jsonResponse.getJSONObject("body").getString("txnType"));
			resp.setGatewayName(jsonResponse.getJSONObject("body").getString("gatewayName"));
			resp.setBankName(jsonResponse.getJSONObject("body").getString("bankName"));
			resp.setMid(jsonResponse.getJSONObject("body").getString("mid"));
			resp.setPaymentMode(jsonResponse.getJSONObject("body").getString("paymentMode"));
			resp.setMerchantUniqueReference(jsonResponse.getJSONObject("body").getString("merchantUniqueReference"));
			
		//	resp.setQrCodeId(jsonResponse.getJSONObject("body").getString("refundAmt"));
		//	resp.setQrData(jsonResponse.getJSONObject("body").getString("txnDate"));
		//	resp.setAuthRefId(jsonResponse.getJSONObject("body").getString("authRefId"));
		}
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
		return resp;
	}	
	
	
	/*
	 * private static String getStoreGstin(String storeID) {
	 * 
	 * String storeGstin = null; HashMap inputData = new HashMap();
	 * inputData.put(MAXCodeConstantsIfc.STORE_ID, storeID);
	 * inputData.put(MAXCodeConstantsIfc.GSTIN_DATA_TRANFER_ID, 5);
	 * //inputData.put(MAXCodeConstantsIfc.TXNID, txnId);
	 * MAXEGSTINDataTransferTransaction gstinTransaction =
	 * (MAXEGSTINDataTransferTransaction)
	 * DataTransactionFactory.create(MAXDataTransactionKeys.
	 * GSTIN_DATA_TRANSFER_TRANSACTION);
	 * 
	 * //try { //storeGstin = gstinTransaction.getStoreGSTINNumber(inputData);
	 * storeGstin = "1234567890"; //} //catch (DataException e1) { //
	 * e1.printStackTrace(); //} return storeGstin; }
	 */
	
	
	public static MAXPaytmQRCodeResponse initiateRefundPaytmQR(String targetURL, String transactionId, String amount, String tillId, String storeId,String orderId,String txnId) throws Exception {
		HttpURLConnection connection = null;
		MAXPaytmQRCodeResponse resp = new MAXPaytmQRCodeResponse();
		resp.setTxnId(txnId);
		resp.setOrderId(orderId);
		
		JSONObject jsonContentObj = getInitiateRefundQRCodeJsonRequestObject(transactionId, amount,storeId,orderId,txnId);

		logger.info("The Paytm initaite Refund QR code Request is = " +jsonContentObj.toString());

		try {
		
			/*
			 * MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
			 * 
			 * ARTSTill till = new ARTSTill(tillId, storeId); boolean dbStatus =
			 * paytmTrans.verifyDatabaseStatus(till); if(! dbStatus) {
			 * MAXPaytmQRCodeResponse respDataException = new MAXPaytmQRCodeResponse();
			 * respDataException.setDataException(Boolean.TRUE); return respDataException; }
			 */
			
			//Create connection
			URL url = new URL(targetURL);

			String urlParameters = jsonContentObj.toString();
			connection = (HttpURLConnection)url.openConnection();
			/*
			 * SSLContext sc = SSLContext.getInstance("TLSv1.2"); TrustManager[] certs = new
			 * TrustManager[] { new X509TrustManager() { public X509Certificate[]
			 * getAcceptedIssuers() { return null; }
			 * 
			 * public void checkClientTrusted(X509Certificate[] certs, String t) { }
			 * 
			 * public void checkServerTrusted(X509Certificate[] certs, String t) { } } };
			 * sc.init(null, certs, new java.security.SecureRandom()); ((HttpURLConnection)
			 * connection).setSSLSocketFactory(sc.getSocketFactory()); ((HttpURLConnection)
			 * connection).setHostnameVerifier(new HostnameVerifier() { public boolean
			 * verify(String hostname, SSLSession session) { return true; } });
			 */
			connection.setRequestMethod("POST");
			
			/*
			 * String useProxy = "true"; String httpProtocol ="" ; useProxy =
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.USEPROXY);
			 * logger.info("UseProxy value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.USEPROXY));
			 * if(useProxy.equalsIgnoreCase("true")) { //Rev 1.2 start //httpProtocol =
			 * MAXPaytmConfig.get(MAXPaytmQRCodeTenderConstants.HTTPPROTOCOL); httpProtocol
			 * = Gateway.getProperty("application",
			 * MAXPaytmQRCodeTenderConstants.HTTPPROTOCOL, ""); if(
			 * httpProtocol.equalsIgnoreCase("https")) { Authenticator.setDefault(new
			 * MAXProxyAuthenticator(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.
			 * PROXYUSER),
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPASSWORD)));
			 * System.setProperty("https.proxyHost",
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYHOST));
			 * System.setProperty("https.proxyPort",
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPORT)); }
			 * 
			 * else if( httpProtocol.equalsIgnoreCase("http")) {
			 * Authenticator.setDefault(new
			 * MAXProxyAuthenticator(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.
			 * PROXYUSER),
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPASSWORD)));
			 * System.setProperty("http.proxyHost",
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYHOST));
			 * System.setProperty("http.proxyPort",
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPORT)); }
			 * 
			 * 
			 * //Rev 1.2 end } logger.info("ProxyHost value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYHOST));
			 * logger.info("ProxyPort value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPORT));
			 * logger.info("ProxyUser value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYUSER));
			 * logger.info("ProxyPassword value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPASSWORD));
			 */
		

			connection.setRequestProperty(MAXPaytmQRCodeTenderConstants.CONTENTTYPE, MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.CONTENTTYPECONFIG));
			connection.setRequestProperty(MAXPaytmQRCodeTenderConstants.CONTENTLENGTH, Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);
			connection.setConnectTimeout(Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.CONNECTIONTIMEOUT)));
			connection.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.close();
			int responseCode = connection.getResponseCode();
			
			resp.setResponseCode(responseCode);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSERECEIVED);
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
				//response.append('\r');
			}
			rd.close();
			logger.info("The Paytm Initaite Refund QR code response string= " + response);
			
			JSONObject jsonResponseObject = new JSONObject(response.toString());
			
			MAXPaytmQRCodeResponse responsePaytm = convertPaytmQRResponseForRefund(jsonResponseObject, resp);
			
			if(responsePaytm != null && 
					(responsePaytm.getOrderId() == null || responsePaytm.getOrderId().equals("null") || responsePaytm.getOrderId().equals(null)))
			{
				responsePaytm.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
			}
			return responsePaytm;
		}
		catch(SocketTimeoutException e){
			logger.error("\nWith  withdraw, timeout exception is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmQRCodeResponse();
			resp.setResultMessage(MAXPaytmQRCodeTenderConstants.PAYTMTIMEOUTERROR);
			resp.setRequestTypeA(MAXPaytmQRCodeTenderConstants.TIMEOUT);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.TIMEOUT);
			resp.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch (ConnectException e) {
			logger.error("\nWith  withdraw, connection exception is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmQRCodeResponse();
			resp.setResultMessage(MAXPaytmQRCodeTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			resp.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
			logger.error(e.getMessage());
			return resp;
		} 
		catch(NoRouteToHostException e){
			logger.error("\nWith  withdraw, NoRouteToHostException is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmQRCodeResponse();
			resp.setResultMessage(MAXPaytmQRCodeTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			resp.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch(UnknownHostException e){
			logger.error("\nWith  withdraw, UnknownHostException is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmQRCodeResponse();
			resp.setResultMessage(MAXPaytmQRCodeTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			resp.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
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
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		return resp;
	}


	public static JSONObject getInitiateRefundQRCodeJsonRequestObject(String transactionId, String amount,String storeId,String orderId,String txnId) throws Exception {
		JSONObject body = null, paytmParams = null;

		String clientId = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.CLIENTID);
		String version = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.VERSION);
		
		String mid = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.MID);
		SimpleDateFormat myFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
		String refId = transactionId + myFormat.format(new Date()).toString();//"07302102002808-14-17 05:14:25";
		body = new JSONObject();
		paytmParams = new JSONObject(); 

		body.put(MAXPaytmQRCodeTenderConstants.REFUNDAMOUNT, amount);
		body.put(MAXPaytmQRCodeTenderConstants.MID, mid);
		body.put(MAXPaytmQRCodeTenderConstants.ORDERID, orderId);
		body.put(MAXPaytmQRCodeTenderConstants.REFID, refId);
		body.put(MAXPaytmQRCodeTenderConstants.TRANSACTIONTYPE, "REFUND");
		body.put(MAXPaytmQRCodeTenderConstants.TXNID, txnId);
		
		QrDisplay qrDisplay = new QrDisplay(); 
		String checksum = qrDisplay.generateSignature(body.toString(), MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.MERCHANTKEYCONFIG));

        JSONObject head = new JSONObject(); 
        
        head.put(MAXPaytmQRCodeTenderConstants.CLIENTID, clientId); 
        head.put(MAXPaytmQRCodeTenderConstants.VERSION, version); 
        head.put(MAXPaytmQRCodeTenderConstants.SIGNATURE, checksum);

        paytmParams.put(MAXPaytmQRCodeTenderConstants.BODY, body); 
        
        paytmParams.put(MAXPaytmQRCodeTenderConstants.HEAD, head);

        return paytmParams;
	}
	
	
	public static MAXPaytmQRCodeResponse convertPaytmQRResponseForRefund(JSONObject jsonResponse, MAXPaytmQRCodeResponse resp)
	{
		try {
		JSONObject resultInfo  = jsonResponse.getJSONObject("body").getJSONObject("resultInfo");
		resp.setResultStatus(resultInfo.getString("resultStatus"));
		resp.setResultCode(resultInfo.getString("resultCode"));
		resp.setResultMessage(resultInfo.getString("resultMsg"));
		resp.setPaytmResponse(jsonResponse.toString());
		resp.setDataException(Boolean.FALSE);	
		if(resultInfo.getString("resultCode").equalsIgnoreCase("601")) {
			resp.setRefId(jsonResponse.getJSONObject("body").getString("refId"));
			resp.setRefundId(jsonResponse.getJSONObject("body").getString("refundId"));
		}
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
		return resp;
	}

	
	public static MAXPaytmQRCodeResponse refundStatusCheckPaytmQR(String targetURL, String transactionId, String amount, String tillId, String storeId,String orderId,String refId) throws Exception {
		HttpURLConnection connection = null;
		MAXPaytmQRCodeResponse resp = new MAXPaytmQRCodeResponse();
		resp.setRefId(refId);
		resp.setOrderId(orderId);
		
		JSONObject jsonContentObj = getRefundStatusCheckQRCodeJsonRequestObject(transactionId, amount,storeId,orderId,refId);

		logger.info("The Paytm Refund Status QR code Request is = " +jsonContentObj.toString());

		try {
		
			/*
			 * MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
			 * 
			 * ARTSTill till = new ARTSTill(tillId, storeId); boolean dbStatus =
			 * paytmTrans.verifyDatabaseStatus(till); if(! dbStatus) {
			 * MAXPaytmQRCodeResponse respDataException = new MAXPaytmQRCodeResponse();
			 * respDataException.setDataException(Boolean.TRUE); return respDataException; }
			 */
			
			//Create connection
			URL url = new URL(targetURL);

			String urlParameters = jsonContentObj.toString();
			connection = (HttpURLConnection)url.openConnection();
			
			/*
			 * SSLContext sc = SSLContext.getInstance("TLSv1.2"); TrustManager[] certs = new
			 * TrustManager[] { new X509TrustManager() { public X509Certificate[]
			 * getAcceptedIssuers() { return null; }
			 * 
			 * public void checkClientTrusted(X509Certificate[] certs, String t) { }
			 * 
			 * public void checkServerTrusted(X509Certificate[] certs, String t) { } } };
			 * sc.init(null, certs, new java.security.SecureRandom()); ((HttpURLConnection)
			 * connection).setSSLSocketFactory(sc.getSocketFactory()); ((HttpURLConnection)
			 * connection).setHostnameVerifier(new HostnameVerifier() { public boolean
			 * verify(String hostname, SSLSession session) { return true; } });
			 */
			connection.setRequestMethod("POST");
			
			/*
			 * String useProxy = "true"; String httpProtocol ="" ; useProxy =
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.USEPROXY);
			 * logger.info("UseProxy value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.USEPROXY));
			 * if(useProxy.equalsIgnoreCase("true")) { //Rev 1.2 start //httpProtocol =
			 * MAXPaytmConfig.get(MAXPaytmQRCodeTenderConstants.HTTPPROTOCOL); httpProtocol
			 * = Gateway.getProperty("application",
			 * MAXPaytmQRCodeTenderConstants.HTTPPROTOCOL, ""); if(
			 * httpProtocol.equalsIgnoreCase("https")) { Authenticator.setDefault(new
			 * MAXProxyAuthenticator(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.
			 * PROXYUSER),
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPASSWORD)));
			 * System.setProperty("https.proxyHost",
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYHOST));
			 * System.setProperty("https.proxyPort",
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPORT)); }
			 * 
			 * else if( httpProtocol.equalsIgnoreCase("http")) {
			 * Authenticator.setDefault(new
			 * MAXProxyAuthenticator(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.
			 * PROXYUSER),
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPASSWORD)));
			 * System.setProperty("http.proxyHost",
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYHOST));
			 * System.setProperty("http.proxyPort",
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPORT)); }
			 * 
			 * 
			 * //Rev 1.2 end } logger.info("ProxyHost value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYHOST));
			 * logger.info("ProxyPort value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPORT));
			 * logger.info("ProxyUser value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYUSER));
			 * logger.info("ProxyPassword value " +
			 * MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PROXYPASSWORD));
			 */
		

			connection.setRequestProperty(MAXPaytmQRCodeTenderConstants.CONTENTTYPE, MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.CONTENTTYPECONFIG));
			connection.setRequestProperty(MAXPaytmQRCodeTenderConstants.CONTENTLENGTH, Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);
			connection.setConnectTimeout(Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.CONNECTIONTIMEOUT)));
			connection.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.close();
			int responseCode = connection.getResponseCode();
			
			resp.setResponseCode(responseCode);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSERECEIVED);
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
				//response.append('\r');
			}
			rd.close();
			logger.info("The Paytm Refund Status QR code response string= " + response);
			
			JSONObject jsonResponseObject = new JSONObject(response.toString());
			
			MAXPaytmQRCodeResponse responsePaytm = convertPaytmQRResponseForRefundStatusCheck(jsonResponseObject, resp);
			
			if(responsePaytm != null && 
					(responsePaytm.getOrderId() == null || responsePaytm.getOrderId().equals("null") || responsePaytm.getOrderId().equals(null)))
			{
				responsePaytm.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
			}
			return responsePaytm;
		}
		catch(SocketTimeoutException e){
			logger.error("\nWith  withdraw, timeout exception is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmQRCodeResponse();
			resp.setResultMessage(MAXPaytmQRCodeTenderConstants.PAYTMTIMEOUTERROR);
			resp.setRequestTypeA(MAXPaytmQRCodeTenderConstants.TIMEOUT);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.TIMEOUT);
			resp.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch (ConnectException e) {
			logger.error("\nWith  withdraw, connection exception is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmQRCodeResponse();
			resp.setResultMessage(MAXPaytmQRCodeTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			resp.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
			logger.error(e.getMessage());
			return resp;
		} 
		catch(NoRouteToHostException e){
			logger.error("\nWith  withdraw, NoRouteToHostException is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmQRCodeResponse();
			resp.setResultMessage(MAXPaytmQRCodeTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			resp.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
			logger.error(e.getMessage());
			return resp;
		}
		catch(UnknownHostException e){
			logger.error("\nWith  withdraw, UnknownHostException is " + e.getMessage() + " with cause " + e.getCause());
			resp = new MAXPaytmQRCodeResponse();
			resp.setResultMessage(MAXPaytmQRCodeTenderConstants.NETWORKERROR);
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			resp.setOrderId(jsonContentObj.getJSONObject("body").getString(MAXPaytmQRCodeTenderConstants.ORDERID));
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
			resp.setReqRespStatus(MAXPaytmQRCodeTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		return resp;
	}
	
	public static JSONObject getRefundStatusCheckQRCodeJsonRequestObject(String transactionId, String amount,String storeId,String orderId,String refId) throws Exception {
		JSONObject body = null, paytmParams = null;

		String clientId = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.CLIENTID);
		String version = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.VERSION);
		
		String mid = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.MID);
		body = new JSONObject();
		paytmParams = new JSONObject(); 
		
		body.put(MAXPaytmQRCodeTenderConstants.MID, mid);
		body.put(MAXPaytmQRCodeTenderConstants.ORDERID, orderId);
		body.put(MAXPaytmQRCodeTenderConstants.REFID, refId);
		
		QrDisplay qrDisplay = new QrDisplay(); 
		String checksum = qrDisplay.generateSignature(body.toString(), MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.MERCHANTKEYCONFIG));

        JSONObject head = new JSONObject(); 
        
        head.put(MAXPaytmQRCodeTenderConstants.CLIENTID, clientId); 
        head.put(MAXPaytmQRCodeTenderConstants.VERSION, version); 
        head.put(MAXPaytmQRCodeTenderConstants.SIGNATURE, checksum);

        paytmParams.put(MAXPaytmQRCodeTenderConstants.BODY, body); 
        
        paytmParams.put(MAXPaytmQRCodeTenderConstants.HEAD, head);

        return paytmParams;
	}

	
	public static MAXPaytmQRCodeResponse convertPaytmQRResponseForRefundStatusCheck(JSONObject jsonResponse, MAXPaytmQRCodeResponse resp)
	{
		try {
		JSONObject resultInfo  = jsonResponse.getJSONObject("body").getJSONObject("resultInfo");
		resp.setResultStatus(resultInfo.getString("resultStatus"));
		resp.setResultCode(resultInfo.getString("resultCode"));
		resp.setResultMessage(resultInfo.getString("resultMsg"));
		resp.setPaytmResponse(jsonResponse.toString());
		resp.setDataException(Boolean.FALSE);	
		if(resultInfo.getString("resultCode").equalsIgnoreCase("10") || resultInfo.getString("resultCode").equalsIgnoreCase("601")) {
			resp.setRefId(jsonResponse.getJSONObject("body").getString("refId"));
			resp.setRefundId(jsonResponse.getJSONObject("body").getString("refundId"));
			resp.setBankTxnId(jsonResponse.getJSONObject("body").getString("refundId"));
			resp.setTxnId(jsonResponse.getJSONObject("body").getString("txnId"));
		}
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
		return resp;
	}


}
