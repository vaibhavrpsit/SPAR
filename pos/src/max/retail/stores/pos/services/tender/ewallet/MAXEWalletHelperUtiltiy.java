/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved.

 *  Rev 1.0  Aug 30, 2021              Atul Shukla                   EWallet FES Implementation
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.tender.ewallet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import max.retail.stores.pos.services.sale.MAXSaleCargo;
import max.retail.stores.pos.services.tender.oxigenwallet.MAXOxigenTenderConstants;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXEWalletHelperUtiltiy implements MAXOxigenTenderConstants
{
	protected static final Logger logger = Logger.getLogger(MAXEWalletHelperUtiltiy.class);
	public static String getEWalletDetails(String targetURL, String storeCode,  String mobileNumber, String terminalId, String channel, String walletOwner, String requestId, String requestType) throws Exception
	{
		//System.out.println("Inside getEWalletDetails");
		HttpsURLConnection connection = null;
		JSONObject	jsonStoreDetailsObj = new JSONObject();
		JSONObject	jsonCompleteRequestBody = new JSONObject();
		StringBuilder response = new StringBuilder();

		//MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		//MAXSaleReturnTransaction maxSaleReturnTransaction = (MAXSaleReturnTransaction) cargo.getTransaction();

		URL url = new URL(targetURL);
		//System.out.println("Credit URL 34:"+ url);
		java.lang.System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
		connection = (HttpsURLConnection)url.openConnection();
		
		connection.setRequestMethod("POST");
		try
		{

			jsonStoreDetailsObj.put(MAXOxigenTenderConstants.STORE_CODE , storeCode);
			jsonStoreDetailsObj.put(MAXOxigenTenderConstants.TERMINAL_ID , terminalId);
			jsonStoreDetailsObj.put(MAXOxigenTenderConstants.OPTIONAL_INFO , " ");
			//jsonStoreDetailsObj.put(MAXOxigenTenderConstants.TRANSACTIONID , transactionID);

		//System.out.println("43 :"+jsonStoreDetailsObj.put(MAXOxigenTenderConstants.TRANSACTIONID , transactionID));

		}catch(Exception e)
{
	e.printStackTrace();
}
		JSONObject jsonContentObj = getJsonRequestObject(mobileNumber , walletOwner, requestId, requestType, channel);
		//System.out.println("jsonContentObj 60");

		logger.info("Get EwalletDetail Request is = " +jsonContentObj.toString());
		jsonCompleteRequestBody.put(MAXOxigenTenderConstants.REQUEST_HEADER, jsonContentObj);
		jsonCompleteRequestBody.put(MAXOxigenTenderConstants.STORE_DETAILS, jsonStoreDetailsObj);
		String urlParameters = jsonCompleteRequestBody.toString();
		logger.info("AKS: GetEWallet url \n" + url);
		logger.info("AKS: GetEWallet Final Request is \n" + urlParameters.toString());
		//System.out.println("Credit URL 57:"+ url);
		//System.out.println(""+transactionID);

		try
		{
			java.lang.System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
			connection = (HttpsURLConnection)url.openConnection();
			
			//Added by kamlesh for Ewallet SSL Error : Starts
			SSLContext sc = SSLContext.getInstance("TLSv1.2");
			TrustManager[] certs = new TrustManager[] { new X509TrustManager() {
		        @Override
				public X509Certificate[] getAcceptedIssuers() {
		            return null;
		        }

		        @Override
				public void checkClientTrusted(X509Certificate[] certs, String t) {
		        }

		        @Override
				public void checkServerTrusted(X509Certificate[] certs, String t) {
		        }
		    } };
			sc.init(null, certs, new java.security.SecureRandom());
			connection.setSSLSocketFactory(sc.getSocketFactory());
			connection.setHostnameVerifier(new HostnameVerifier()
			{
			    @Override
				public boolean verify(String hostname, SSLSession session)
			    {
			        return true;
			    }
			});
			//Changes for Ewallet SSL Error : Ends
			
			connection.setUseCaches(false);
			connection.setRequestProperty(MAXOxigenTenderConstants.CONTENTTYPE, MAXOxigenTenderConstants.JSON);
		//	connection.setReadTimeout(100);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.close();
			int responseCode = connection.getResponseCode();
			logger.info("Get EwalletDetail ResponseCode is = " +responseCode);
			InputStream is;
			if(responseCode == HttpURLConnection.HTTP_OK){
				is = connection.getInputStream();
			}else {
				is = connection.getErrorStream();
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		//	StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
			String line = "";
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			logger.info("The Ewallet  response string= " + response);

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		//System.out.println("Before return response.toString()");
		 	return response.toString();

}

	@SuppressWarnings("unchecked")
	private static JSONObject getJsonRequestObject(String mobileNumber, String walletOwner ,String  requestId  , String requestType, String  channel)
	{
		//System.out.println("getJsonRequestObject 111");
		JSONObject jsonRequestObj = new JSONObject();
		Date date= new Date();
		 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		String requestTimestamp=format.format(date);
		jsonRequestObj.put(MAXOxigenTenderConstants.MOBILENUMBER, mobileNumber.toString());
		//jsonRequestObj.put(MAXOxigenTenderConstants.MOBILENUMBER, "9873477777");
		jsonRequestObj.put(MAXOxigenTenderConstants.REQUEST_TYPE, MAXOxigenTenderConstants.GET_WALLET_DETAILS);
		jsonRequestObj.put(MAXOxigenTenderConstants.REQUEST_ID, MAXOxigenTenderConstants.GETWALLET_REQUEST_ID);
		jsonRequestObj.put(MAXOxigenTenderConstants.REQUEST_TIME, requestTimestamp.toString());
		jsonRequestObj.put(MAXOxigenTenderConstants.ORIGINAL_DIALOGUE_TRACE_ID, "");
		jsonRequestObj.put(MAXOxigenTenderConstants.WALLET_OWNER, MAXOxigenTenderConstants.SPAR_CONSTANT);
		jsonRequestObj.put(MAXOxigenTenderConstants.CHANNEL, MAXOxigenTenderConstants.POS_CONSTANT);
	//	jsonRequestObj.put(MAXOxigenTenderConstants.TRANSACTIONID, "");
		//System.out.println("113 :"+transactionID);
		return jsonRequestObj;
	}

	public static String getEWalletReversal(BusIfc bus, String targetURL, String storeCode,  String mobileNumber, String terminalId, String channel, String walletOwner, String requestId, String requestType) throws Exception
	{
		//System.out.println("inside getEWalletReversal");
		MAXSaleCargo saleCargo = (MAXSaleCargo) bus.getCargo();

		DialogBeanModel dialogModel = new DialogBeanModel();

	//	JSONObject jsonOxigenSubmitInvoiceRequest = getJsonRequestObject(saleCargo);

		HttpURLConnection connection = null;
		JSONObject	jsonStoreDetailsObj = new JSONObject();
		JSONObject	jsonCompleteRequestBody = new JSONObject();
		StringBuilder response = new StringBuilder();

		//MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		//MAXSaleReturnTransaction maxSaleReturnTransaction = (MAXSaleReturnTransaction) cargo.getTransaction();

		URL url = new URL(targetURL);
		//System.out.println("Credit URL 145:"+ url);
		connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("POST");
		try
		{

			jsonStoreDetailsObj.put(MAXOxigenTenderConstants.STORE_CODE , storeCode);
			jsonStoreDetailsObj.put(MAXOxigenTenderConstants.TERMINAL_ID , terminalId);
			jsonStoreDetailsObj.put(MAXOxigenTenderConstants.OPTIONAL_INFO , " ");
			//jsonStoreDetailsObj.put(MAXOxigenTenderConstants.TRANSACTIONID , transactionID);

		//System.out.println("43 :"+jsonStoreDetailsObj.put(MAXOxigenTenderConstants.TRANSACTIONID , transactionID));

		}catch(Exception e)
{
	e.printStackTrace();
}
		JSONObject jsonContentObj = getJsonRequestObj(saleCargo,mobileNumber , walletOwner, requestId, requestType, channel);
		logger.info("Get EwalletDetail Request is = " +jsonContentObj.toString());
		jsonCompleteRequestBody.put(MAXOxigenTenderConstants.REQUEST_HEADER, jsonContentObj);
		jsonCompleteRequestBody.put(MAXOxigenTenderConstants.STORE_DETAILS, jsonStoreDetailsObj);
		String urlParameters = jsonCompleteRequestBody.toString();
		logger.info("AKS: GetEWallet url \n" + url);
		logger.info("AKS: GetEWallet Final Request is \n" + urlParameters.toString());
		//System.out.println("Credit URL 57:"+ url);
		//System.out.println(""+transactionID);

		try
		{
			connection = (HttpURLConnection)url.openConnection();
			connection.setUseCaches(false);
			connection.setRequestProperty(MAXOxigenTenderConstants.CONTENTTYPE, MAXOxigenTenderConstants.JSON);
		//	connection.setReadTimeout(100);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.close();
			int responseCode = connection.getResponseCode();
			logger.info("Get EwalletDetail ResponseCode is = " +responseCode);
			InputStream is;
			if(responseCode == HttpURLConnection.HTTP_OK){
				is = connection.getInputStream();
			}else {
				is = connection.getErrorStream();
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		//	StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
			String line = "";
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			logger.info("The Ewallet  response string= " + response);

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		 	return response.toString();

}

	@SuppressWarnings("unchecked")
	private static JSONObject getJsonRequestObj(MAXSaleCargo cargo,String mobileNumber, String walletOwner ,String  requestId  , String requestType, String  channel)
	{
		//System.out.println("JSONObject getJsonRequestObject 215");
		JSONObject jsonRequestObj = new JSONObject();
		Date date= new Date();
		 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		String requestTimestamp=format.format(date);
		jsonRequestObj.put(MAXOxigenTenderConstants.MOBILENUMBER, mobileNumber.toString());
		//jsonRequestObj.put(MAXOxigenTenderConstants.MOBILENUMBER, "9873477777");
		jsonRequestObj.put(MAXOxigenTenderConstants.REQUEST_TYPE, MAXOxigenTenderConstants.GET_WALLET_DETAILS);
		jsonRequestObj.put(MAXOxigenTenderConstants.REQUEST_ID, MAXOxigenTenderConstants.GETWALLET_REQUEST_ID);
		jsonRequestObj.put(MAXOxigenTenderConstants.REQUEST_TIME, requestTimestamp.toString());
		jsonRequestObj.put(MAXOxigenTenderConstants.ORIGINAL_DIALOGUE_TRACE_ID, "");
		jsonRequestObj.put(MAXOxigenTenderConstants.WALLET_OWNER, MAXOxigenTenderConstants.SPAR_CONSTANT);
		jsonRequestObj.put(MAXOxigenTenderConstants.CHANNEL, MAXOxigenTenderConstants.POS_CONSTANT);
		jsonRequestObj.put(MAXOxigenTenderConstants.TRANSACTIONID, cargo.getTransaction().getTransactionID().toString());
		return jsonRequestObj;
	}
}
