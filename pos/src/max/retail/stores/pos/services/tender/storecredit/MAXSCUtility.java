/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *
 *	Rev 1.0 	May 14, 2024			Kamlesh Pant		Store Credit OTP:
 *
 ********************************************************************************/


package max.retail.stores.pos.services.tender.storecredit;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import oracle.retail.stores.foundation.tour.gate.Gateway;

public class MAXSCUtility {
	private static int responseCode;
	@SuppressWarnings("rawtypes")
		private static Logger logger = Logger
			.getLogger(MAXSCUtility.class);
	protected static final String authCode = Gateway.getProperty("application", "StoreCreditAuthorization", "");
	public String GenerateOTPForSc(String mobileNo, String storeID,String SClats4digits, String requestType) throws JSONException {
		MAXStoreCreditOTPResponse resp = new MAXStoreCreditOTPResponse();
				String targetURL = Gateway.getProperty("application", "StoreCreditGenerateOTPURL", "");
		String timeOut = Gateway.getProperty("application",
				"LoyaltytimeOutInMilliSeconds", "5000");
		
		HttpsURLConnection connection = null;
		JSONObject request = new JSONObject();
		request.put("mobileNumber", mobileNo);
		request.put("storeCode", storeID);
		request.put("creditNoteLast4Digits", SClats4digits);
		request.put("requestType", requestType);
		String urlParameters = request.toString();
		
		logger.info("The OTP Request for Store credit is = " + urlParameters);
		try {
			URL url = new URL(targetURL);

			connection = (HttpsURLConnection)url.openConnection();
			
			SSLContext sc = SSLContext.getInstance("TLSv1.2");
			TrustManager[] certs = new TrustManager[] { new X509TrustManager() {
		        public X509Certificate[] getAcceptedIssuers() {
		            return null;
		        }
		 
		        public void checkClientTrusted(X509Certificate[] certs, String t) {
		        }
		 
		        public void checkServerTrusted(X509Certificate[] certs, String t) {
		        }
		    } };
			sc.init(null, certs, new java.security.SecureRandom()); 
			((HttpsURLConnection) connection).setSSLSocketFactory(sc.getSocketFactory());
			((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier()
			{      
			    public boolean verify(String hostname, SSLSession session)
			    {
			        return true;
			    }
			});
			connection.setRequestMethod("POST");

			connection.setRequestProperty("AuthKey",authCode);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setReadTimeout(Integer.parseInt(timeOut));
			connection.setConnectTimeout(Integer.parseInt(timeOut));
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			try {
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			
			wr.writeBytes(urlParameters);
			wr.close();
			
			responseCode = connection.getResponseCode();
			InputStream is;
			if (responseCode == HttpURLConnection.HTTP_OK) {
				is = connection.getInputStream();
			} else {
				is = connection.getErrorStream();
			}
			logger.info("is " + is);
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
			String line = "";
			while ((line = rd.readLine()) != null) {
				response.append(line);
			}
			rd.close();
			logger.info("The resposne for OTP Validation is  = "
					+ response.toString());
			String responseOTP = null;
			if (responseCode == 200) {
				JSONObject jsonResponseObject = new JSONObject(response.toString());
				responseOTP =convertSendResponse(jsonResponseObject, resp);}
			if(resp.getResult().toString().equalsIgnoreCase("false")) {
				
				 return "false";
				
			}
			else
			return responseOTP;
			
			}catch(Exception e) {
				logger.error(e);
				return "ConnectException";
				}
		}
		 catch (Exception e) {
				e.printStackTrace();
				return "Exception";
			}
	

	}

	private String convertSendResponse(JSONObject jsonResponseObject, MAXStoreCreditOTPResponse resp) {
		 
		  try {
			  
		   JSONObject resultInfo=jsonResponseObject;
		   resp.setResult(resultInfo.getString("result"));
		   resp.setResultCode(resultInfo.getString("resultCode"));
		   resp.setResultMessage(resultInfo.getString("message"));
		   resp.setOTP(resultInfo.getString("otp"));
		   resp.setDataException(Boolean.FALSE);
		   resp.setOTP(jsonResponseObject.getString("otp"));
		  
		  }
		  catch(Exception e) { 
			  logger.error(e.getMessage()); 
			  }
		  return resp.getOTP();
		  
	}
}
