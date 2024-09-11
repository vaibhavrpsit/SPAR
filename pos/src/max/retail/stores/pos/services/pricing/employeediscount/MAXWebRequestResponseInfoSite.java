package max.retail.stores.pos.services.pricing.employeediscount;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;

import max.retail.stores.domain.employee.MAXEmployee;
import max.retail.stores.domain.loyalty.MAXLoyaltyConstants;
import max.retail.stores.domain.paytm.MAXPaytmQRCodeResponse;
import max.retail.stores.domain.tender.paytmqr.MAXPaytmQRCodeTenderConstants;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.pricing.MAXPricingCargo;
import max.retail.stores.pos.services.tender.creditdebit.paytmqr.MAXPaytmQRCodeConfig;
import max.retail.stores.pos.services.tender.creditdebit.paytmqr.MAXPaytmQRCodeHelperUtiltiy;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXWebRequestResponseInfoSite extends PosSiteActionAdapter {
	/**
	 * @author kajal nautiyal Employee Discount validation through OTP
	 */
	private static final long serialVersionUID = 1227174702805395077L;
	// Variable defined for Response Code
	private static int responseCode;
	// HashMap defined for RequestAttributes
	private HashMap requestAttributes;
	// Change Start for Rev 1.3
	boolean IsRedemcall2 = false;
	protected static final String authCode = Gateway.getProperty(
            "application", "EmployeeDiscounAuthorization", "");
	
	protected static final Logger logger = Logger.getLogger(MAXWebRequestResponseInfoSite.class);

	public void arrive(BusIfc bus) {
		MAXPricingCargo cargo = (MAXPricingCargo) bus.getCargo();
		MAXEmployeeDiscountOTPResponse resp = new MAXEmployeeDiscountOTPResponse();

		HttpsURLConnection connection = null;
		String phnNumber=null;
		String availableAmt = null;
		if(cargo.getTransaction() instanceof MAXSaleReturnTransaction){

			MAXSaleReturnTransaction trans=(MAXSaleReturnTransaction) cargo.getTransaction();
			 phnNumber = trans.getLocale().toString();
			 availableAmt = trans.getEmpDiscountAvailLimit();
			}
			else {
				if(cargo.getTransaction() instanceof MAXLayawayTransaction)
			    {
				MAXLayawayTransaction trans=(MAXLayawayTransaction) cargo.getTransaction();
				 phnNumber = trans.getLocale().toString();
				 availableAmt = trans.getEmpAvailableAmt();
			      }
			}
		
		
		String storeCode = Gateway.getProperty("application", "StoreID", (String) null);
		//String storeId = storeId1.substring(1);
		//System.out.println("StoreId-----------------------------"+storeId);  
		String targetURL = Gateway.getProperty("application", "EmployeeDiscountGenerateOTPURL", "");
		String timeOut = Gateway.getProperty("application",
				"LoyaltytimeOutInMilliSeconds", "5000");
		String channel = "emp_disc";
		try {
			JSONObject jsonContentObj = getcreateOTPCallRequestMessage(phnNumber, channel, storeCode,availableAmt);
			//System.out.println("Request" + jsonContentObj);
			
			logger.info("The Employee Discount Generate OTP Request is = " +jsonContentObj.toString());
             System.out.println("The Employee Discount Generate OTP Request is = " +jsonContentObj.toString());
			
			//	URL url = new URL(targetURL);
				URL url = new URL(null, targetURL, new sun.net.www.protocol.https.Handler());

			String urlParameters = jsonContentObj.toString();
			
				connection = (HttpsURLConnection)url.openConnection();
			
				
			
		//	connection = (HttpsURLConnection)url.openConnection();
			
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
			}catch(Exception e) {
				logger.error(e);
				//System.out.println("Error occured while fetching the OTP for Employee discount"+e);
				 DialogBeanModel dialogModel = new DialogBeanModel();	
				 POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
				dialogModel.setResourceID("GenerateOtpWebserviceNotWorking");
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.CANCEL);
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				
			}
			int responseCode = connection.getResponseCode();
			logger.info("responseCode " + responseCode);
			resp.setResponseCode(responseCode);
			resp.setRespReceivedDate(new Date());
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
				//response.append('\r');
			}
			rd.close();
			logger.info("The Employee Discount Generate OTP response string= " + response);
			System.out.println("response string= " + response);
			if (responseCode == 200) {
				JSONObject jsonResponseObject = new JSONObject(response.toString());
				String responseOTP =convertSendResponse(jsonResponseObject, resp);
			
			if(resp.getResultStatus().toString().equalsIgnoreCase("false")) {
				 DialogBeanModel dialogModel = new DialogBeanModel();	
				 POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
				dialogModel.setResourceID("GenerateOtpWebserviceNotWorking");
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.CANCEL);
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				
			}
			else {
				
				 //cargo = (MAXPricingCargo) bus.getCargo();
				if(cargo.getTransaction() instanceof MAXSaleReturnTransaction){

				MAXSaleReturnTransaction trans=(MAXSaleReturnTransaction) cargo.getTransaction();
				trans.setEmplyoeeDicsOtp(responseOTP);
				System.out.println("responseOTP ::"+responseOTP.toString());
				bus.mail(new Letter("Success"), BusIfc.CURRENT);
				}
				else {
					if(cargo.getTransaction() instanceof MAXLayawayTransaction)
				{
					MAXLayawayTransaction trans=(MAXLayawayTransaction) cargo.getTransaction();
					trans.setEmplyoeeDicsOtp(responseOTP);
					//System.out.println("responseOTP ::"+responseOTP.toString());
					bus.mail(new Letter("Success"), BusIfc.CURRENT);
				}
				}
				

			}
			}
			
			else {
				 DialogBeanModel dialogModel = new DialogBeanModel();	
				 POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
				dialogModel.setResourceID("GenerateOtpWebserviceNotWorking");
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.CANCEL);
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	public static JSONObject getcreateOTPCallRequestMessage(String locale, String channel, String storeCode, String availableAmt)
			throws Exception {
		JSONObject body = null, OTPParams = null;
		body = new JSONObject();
		OTPParams = new JSONObject();
		body.put("mobileNumber", locale);
		// body.put(key,employee.getLocale();
		body.put("channel", channel);
		body.put("storeCode", storeCode);
		body.put("avl_amt", availableAmt);
		
		return body;
	}

	
	
	  public static String convertSendResponse(JSONObject jsonResponse,MAXEmployeeDiscountOTPResponse resp) {
		  
		  try {
			  
		   JSONObject resultInfo=jsonResponse;
		   resp.setResultStatus(resultInfo.getString("result"));
		   resp.setResultCode(resultInfo.getString("resultCode"));
		   resp.setResultMessage(resultInfo.getString("message"));
		   resp.setResultOtp(resultInfo.getString("otp"));
		   resp.setDataException(Boolean.FALSE);
		   resp.setOTP(jsonResponse.getString("otp"));
		 // resp.setStoreId(jsonResponse.getString("StoreId"));
		  
		  }
		 // }
		  catch(Exception e) { 
			  logger.error(e.getMessage()); 
			  }
		  return resp.getOTP();
		  }
	 
	 
}
