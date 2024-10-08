package max.retail.stores.pos.services.customer.tic;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import max.retail.stores.pos.services.sale.validate.MAXProxyAuthenticator;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import sun.misc.BASE64Encoder;

public class MAXWebCRMCustomerSearchUtility
{
  private static int responseCode;
  HashMap requestAttributes;
  private static Logger logger = Logger.getLogger(MAXWebCRMCustomerSearchUtility.class);
  private static MAXWebCRMCustomerSearchUtility crmCustomerSearchUtility;
  // final String authCode = Gateway.getProperty("application", "SearchCustomerAuthorization", "");

  public static MAXWebCRMCustomerSearchUtility getInstance()
  {
    synchronized (MAXWebCRMCustomerSearchUtility.class)
    {
      if (crmCustomerSearchUtility == null) {
        crmCustomerSearchUtility = new MAXWebCRMCustomerSearchUtility();
      }
    }
    
    return crmCustomerSearchUtility;
  }
  
  public MAXCRMSearchCustomer searchCRMCustomer(BusIfc bus, MAXCRMSearchCustomer customer)
  {
    this.requestAttributes = new HashMap();
    this.requestAttributes = populateHashMapforCustSearch(bus, this.requestAttributes, customer);
    
   // final String authCode = Gateway.getProperty("application", "SearchCustomerAuthorization", "");
    String urlParameters = createURLForCustSearch(this.requestAttributes, bus);
    logger.info("Request to CRM For Customer Search : " + urlParameters);
    boolean capApiAllowedCRM = false;
    ParameterManagerIfc pm = null;
    try
    {
      pm = (ParameterManagerIfc)bus.getManager("ParameterManager");
      
      capApiAllowedCRM = pm.getBooleanValue("IsCapillaryAPIAllowedCRM").booleanValue();
    }
    catch (ParameterException e)
    {
      logger.info("IsCapillaryAPIAllowed parameter is not exist");
    }
    String URL = "";
    String response = "";
    if (capApiAllowedCRM)
    {
      URL = Gateway.getProperty("application", "CapillaryCRMWebServiceSearchCustomerURL", "");
      String crmCapillaryUserId = Gateway.getProperty("application", "CapillaryUserId", "");
      String crmCapillaryPwd = Gateway.getProperty("application", "CapillaryPassword", "");
      
      String crmHttpHost = Gateway.getProperty("application", "ProxyHost", "172.16.254.5");
      String proxyUserName = Gateway.getProperty("application", "ProxyUserName", "172.16.254.5");
      String proxyUserPswd = Gateway.getProperty("application", "ProxyUserPswd", "172.16.254.5");
      String crmHttpPort = Gateway.getProperty("application", "ProxyPort", "2790");
      String authRequire = Gateway.getProperty("application", "AuthRequire", "true");

      showProcessCRMRequestScreen(bus);
      
      response = executePost(URL, crmCapillaryUserId, crmCapillaryPwd, crmHttpHost, crmHttpPort, proxyUserName, proxyUserPswd, authRequire, customer);
    }
    else
    {
    	try{
    		URL = Gateway.getProperty("application", "LoyaltyCRMWebServiceSearchCustomerURL", "");
      
    		response = executePost(URL, urlParameters);
    		//System.out.println("urlParameters :"+urlParameters);
    		//System.out.println("CRM Response\n"+response);
    	}
    	catch(Exception e)
    	{
    		logger.error(e);
    	}
    }
    
    processSuccessResponse(bus, response, customer);
    return customer;
  }
  
  public MAXCRMSearchCustomer searchBalanceEnquiryCRMCustomer(BusIfc bus, MAXCRMSearchCustomer customer)
  {
    this.requestAttributes = new HashMap();
    this.requestAttributes = populateHashMap(bus, this.requestAttributes, customer);
    
    String urlParameters = createURL(this.requestAttributes, bus);
    
    boolean capApiAllowedCRM = false;
    
    ParameterManagerIfc pm = null;
    try
    {
      pm = (ParameterManagerIfc)bus.getManager("ParameterManager");
      
      capApiAllowedCRM = pm.getBooleanValue("IsCapillaryAPIAllowedCRM").booleanValue();
    }
    catch (ParameterException e)
    {
      logger.info("IsCapillaryAPIAllowed parameter is not exist");
      e.printStackTrace();
    }
    String URL = "";
    String response = "";
    if (capApiAllowedCRM)
    {
      URL = Gateway.getProperty("application", "CapillaryCRMWebServiceSearchCustomerURL", "");
      String crmCapillaryUserId = Gateway.getProperty("application", "CapillaryUserId", "");
      String crmCapillaryPwd = Gateway.getProperty("application", "CapillaryPassword", "");
      String crmHttpHost = Gateway.getProperty("application", "ProxyHost", "172.16.254.5");
      String proxyUserName = Gateway.getProperty("application", "ProxyUserName", "172.16.254.5");
      String proxyUserPswd = Gateway.getProperty("application", "ProxyUserPswd", "172.16.254.5");
      String crmHttpPort = Gateway.getProperty("application", "ProxyPort", "2790");
      String authRequire = Gateway.getProperty("application", "AuthRequire", "true");
      
      response = executePost(URL, crmCapillaryUserId, crmCapillaryPwd, crmHttpHost, crmHttpPort, proxyUserName, proxyUserPswd, authRequire, customer);
    }
    else
    {
      URL = Gateway.getProperty("application", "LoyaltyCRMWebServiceSearchCustomerURL", "");
      response = executePost(URL, urlParameters);
    }
    processBalanceEnquirySuccessResponse(bus, response, customer);
    return customer;
  }
  
  protected String createEasyBuyURL(HashMap map, String url)
  {
    String urlParameters = "";
    try
    {
      String mobile = (String)map.get("mobileNumber");
      if ((mobile != null) && (mobile != "")) {
        urlParameters = url + "mobileNumber=" + map.get("mobileNumber") + "&messageId=" + map.get("messageId");
      } else {
        urlParameters = url + "mobileNumber=" + map.get("cardNumber") + "&messageId=" + map.get("messageId");
      }
    }
    catch (Exception e)
    {
      Logger.getLogger(MAXWebCRMCustomerSearchUtility.class.getName()).log(null, e);
    }
    return urlParameters;
  }
  
  public String executePost(String targetURL, String crmCapillaryUserId, String crmCapillaryPwd, String host, String port, String user, String pass, String auth, MAXCRMSearchCustomer customer)
  {
    HttpURLConnection connection = null;
    try
    {
      String authHeader = "";
      
      byte[] encodedPassword = (crmCapillaryUserId + ":" + crmCapillaryPwd).getBytes();
      BASE64Encoder encoder = new BASE64Encoder();
      authHeader = "Basic " + encoder.encode(encodedPassword);
      

      URL url = new URL(targetURL);
      
      Properties systemProperties = System.getProperties();
      if (host.trim().length() > 0)
      {
        systemProperties.setProperty("http.proxyHost", host);
        systemProperties.setProperty("http.proxyPort", port);
        systemProperties.setProperty("proxySet", auth);
        Authenticator.setDefault(new MAXProxyAuthenticator(user, pass));
      }
      String mobileNumber = customer.getMobileNumber() != null ? customer.getMobileNumber() : "";
      String messageID = customer.getMessageId() != null ? customer.getMessageId() : "";
      String CardNumber = customer.getCardNumber() != null ? customer.getCardNumber() : "";
      

      JSONObject request = new JSONObject();
      request.put("messageId", messageID);
      request.put("mobileNumber", mobileNumber);
      request.put("cardNumber", CardNumber);
     // System.out.println("request"+request);
      
      String timeOut = Gateway.getProperty("application", "LoyaltytimeOutInMilliSeconds", "5000");
      
      System.getProperties().setProperty("sun.net.client.defaultConnectTimeout", timeOut);
      
      System.getProperties().setProperty("sun.net.client.defaultReadTimeout", timeOut);
      

      connection = (HttpURLConnection)url.openConnection();
      

      connection.setRequestMethod("POST");
      
      connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      connection.setRequestProperty("Authorization", authHeader);
      


      connection.setRequestProperty("Content-Language", "en-US");
      connection.setUseCaches(false);
      connection.setDoInput(true);
      connection.setDoOutput(true);
      


      connection.connect();
      
      OutputStream os = connection.getOutputStream();
      os.write(request.toString().getBytes());
      os.flush();
      os.close();
      

      responseCode = connection.getResponseCode();
      
      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      
      StringBuffer response = new StringBuffer();
      String line;
      while ((line = rd.readLine()) != null)
      {
        response.append(line);
        response.append('\r');
      }
      rd.close();
      return response.toString();
    }
    catch (Exception e)
    {
      BASE64Encoder encoder;
      logger.error("Error in sending CRM Customer Search Request" + e.getMessage() + "");
      try
      {
        responseCode = connection.getResponseCode();
      }
      catch (IOException e1)
      {
        logger.error("IO Exception Caught::" + e1.getMessage() + "");
        return "Timeout";
      }
      return "";
    }
    finally
    {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }
  
  public static String encode_MD5(String md5)
  {
    try
    {
      MessageDigest digest = MessageDigest.getInstance("MD5");
      
      digest.update(md5.getBytes());
      

      String mdHash = new BigInteger(1, digest.digest()).toString(16);
      while (mdHash.length() < 32) {
        mdHash = "0" + mdHash;
      }
      return mdHash;
    }
    catch (NoSuchAlgorithmException e)
    {
      logger.error("encode_MD5::" + e + "");
    }
    return null;
  }
  
  public static String executePost(String targetURL, String urlParameters)
  {
    HttpsURLConnection connection = null;
    try
    {
   //   URL url = new URL(targetURL);
    URL url = new URL(null,targetURL,new sun.net.www.protocol.https.Handler());
      
      java.lang.System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
      connection = (HttpsURLConnection)url.openConnection();
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
      
      String timeOut = Gateway.getProperty("application", "LoyaltytimeOutInMilliSeconds", "5000");
      
      System.getProperties().setProperty("sun.net.client.defaultConnectTimeout", timeOut);
      
      System.getProperties().setProperty("sun.net.client.defaultReadTimeout", timeOut);

     // connection = (HttpURLConnection)url.openConnection(Proxy.NO_PROXY);
      
      final String AuthKey = Gateway.getProperty("application", "SearchCustomerAuthorization", "");

      connection.setRequestProperty("AuthKey",AuthKey);;
      
      connection.setRequestMethod("POST");
      
      connection.setRequestProperty("Content-Type", "application/json");
      
      connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
      
      connection.setRequestProperty("Content-Language", "en-US");
      connection.setUseCaches(false);
      connection.setDoInput(true);
      connection.setDoOutput(true);
      
      DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
      wr.writeBytes(urlParameters);
      wr.flush();
      wr.close();
      
      responseCode = connection.getResponseCode();
      
      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      
      StringBuffer response = new StringBuffer();
      String line;
      while ((line = rd.readLine()) != null)
      {
        response.append(line);
        response.append('\r');
      }
      rd.close();
      return response.toString();
    }
    catch (Exception e)
    {
      InputStream is;
      logger.error("Error in sending CRM Customer Search Request" + e.getMessage() + "");
      try
      {
        responseCode = connection.getResponseCode();
      }
      catch (IOException e1)
      {
        logger.error("IO Exception Caught::" + e1.getMessage() + "");
        return "Timeout";
      }
      return "";
    }
    finally
    {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }
  
  protected String createURL(HashMap requestInfo, BusIfc bus)
  {
    try
    {
      JSONObject obj = new JSONObject();
      
      obj.put("cardNumber", requestInfo.get("cardNumber"));
      
      obj.put("messageId", requestInfo.get("messageId"));
      
      obj.put("mobileNumber", requestInfo.get("mobileNumber"));
      

      return obj.toJSONString();
    }
    catch (Exception e)
    {
      Logger.getLogger(MAXWebCRMCustomerSearchUtility.class.getName()).log(null, e);
    }
    return null;
  }
  
  protected String createURLForCustSearch(HashMap requestInfo, BusIfc bus)
  {
    try
    {
      JSONObject obj = new JSONObject();
      
     // obj.put("messageId", requestInfo.get("messageId"));
      obj.put("mobile", requestInfo.get("mobile"));
      obj.put("source", requestInfo.get("source"));
    //  obj.put("cardNumber", requestInfo.get("cardNumber"));
      
      
      
      return obj.toJSONString();
    }
    catch (Exception e)
    {
      Logger.getLogger(MAXWebCRMCustomerSearchUtility.class.getName()).log(null, e);
    }
    return null;
  }
  protected void processSuccessResponse(BusIfc bus, String response, MAXCRMSearchCustomer searchCrmCustomer)
  {
    String trimString = response.toString().trim();
    
    logger.info("Response from CRM For Customer Search : " + trimString);
    
    JSONParser parser = new JSONParser();
    try
    {
    	//Modified By vaibhav for bypassing sbi points and ewallet
      JSONObject jsonObject = (JSONObject)parser.parse(trimString);
      //System.out.println("jsonObject"+jsonObject.get("custName"));
     // if (jsonObject.get("messageId") != null) {
      if (jsonObject.get("messageCode") != null) {
        searchCrmCustomer.setResMessageId((String)jsonObject.get("messageCode"));
      }
      if (jsonObject.get("cardNum") != null) {
        searchCrmCustomer.setResCardNumber((String)jsonObject.get("cardNum"));
      }
      if (jsonObject.get("mobile") != null) {
        searchCrmCustomer.setResMobileNumber((String)jsonObject.get("mobile"));
      }
      if (jsonObject.get("message") != null) {
        searchCrmCustomer.setMessage((String)jsonObject.get("message"));
      }
      if (jsonObject.get("respose") != null) {
        searchCrmCustomer.setResponse((String)jsonObject.get("respose"));
      }
      if (jsonObject.get("custName") != null) {
        searchCrmCustomer.setCustName((String)jsonObject.get("custName"));
      }
      if (jsonObject.get("custTier") != null) {
        searchCrmCustomer.setCustTier((String)jsonObject.get("custTier"));
      }
      if (jsonObject.get("email") != null) {
        searchCrmCustomer.setEmail((String)jsonObject.get("email"));
      }
      if (jsonObject.get("pincode") != null) {
        searchCrmCustomer.setPincode(((Long)jsonObject.get("pincode")).toString());
      }
      if (jsonObject.get("pointBal") != null) {
        searchCrmCustomer.setPointBal(((Double)jsonObject.get("pointBal")).toString());
      }
      if (jsonObject.get(MAXCRMCSearchCustomerParam.SBIPOINTS) != null) {
			searchCrmCustomer.setSbiPointBal(((Double) jsonObject
					.get(MAXCRMCSearchCustomerParam.SBIPOINTS)).toString());
    }      
    }
    catch (Exception localException) {}
    if (response.equalsIgnoreCase("Timeout"))
    {
      displayCRMNetworkError(bus, "CRMCustomerSearchNetworkError", "failureByPhoneNumber");
      return;
    }
    
    POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager("UIManager");
    if(!(searchCrmCustomer != null && searchCrmCustomer.getMessage().equalsIgnoreCase("LMR Customer"))){
    if ((searchCrmCustomer == null) || (searchCrmCustomer.getResponse() == null) ||
    		(!searchCrmCustomer.getResponse().trim().equalsIgnoreCase("S"))) {
      if ((searchCrmCustomer != null) && (searchCrmCustomer.getResponse() != null) &&
    		  (!searchCrmCustomer.getResponse().trim().equalsIgnoreCase("S")))
      {
        if ((searchCrmCustomer != null) && (searchCrmCustomer.getMobileNumber() != null) && 
        		(!searchCrmCustomer.getMobileNumber().trim().equalsIgnoreCase(""))) {
        	displayCRMResponseError(bus, searchCrmCustomer.getMessage(), "CRMInvalidMobileNumberError", "Failure");
        	return;
        } else {
          displayCRMResponseError(bus, searchCrmCustomer.getMessage(), "CRMInvalidMobileNumberError", "failureByPhoneNumber");
        }
      }
      else if ((searchCrmCustomer != null) && (searchCrmCustomer.getMobileNumber() != null) 
    		  && (!searchCrmCustomer.getMobileNumber().trim().equalsIgnoreCase(""))) {
        displayCRMNetworkError(bus, "CRMInvalidMobileNumberError", "failureByPhoneNumber");
      } else {
        displayCRMNetworkError(bus, "CRMInvalidMobileNumberError", "failureByPhoneNumber");
      }
    }
    }
  }
  
  protected void processBalanceEnquirySuccessResponse(BusIfc bus, String response, MAXCRMSearchCustomer searchCrmCustomer)
  {
    String trimString = response.toString().trim();
    
    JSONParser parser = new JSONParser();
    try
    {
      JSONObject jsonObject = (JSONObject)parser.parse(trimString);
      if (jsonObject.get("messageId") != null) {
        searchCrmCustomer.setResMessageId((String)jsonObject.get("messageId"));
      }
      if (jsonObject.get("cardNumber") != null) {
        searchCrmCustomer.setResCardNumber((String)jsonObject.get("cardNumber"));
      }
      if (jsonObject.get("mobileNumber") != null) {
        searchCrmCustomer.setResMobileNumber((String)jsonObject.get("mobileNumber"));
      }
      if (jsonObject.get("message") != null) {
        searchCrmCustomer.setMessage((String)jsonObject.get("message"));
      }
      if (jsonObject.get("response") != null) {
        searchCrmCustomer.setResponse((String)jsonObject.get("response"));
      }
      if (jsonObject.get("custName") != null) {
        searchCrmCustomer.setCustName((String)jsonObject.get("custName"));
      }
      if (jsonObject.get("custTier") != null) {
        searchCrmCustomer.setCustTier((String)jsonObject.get("custTier"));
      }
      if (jsonObject.get("email") != null) {
        searchCrmCustomer.setEmail((String)jsonObject.get("email"));
      }
      if (jsonObject.get("pincode") != null) {
        searchCrmCustomer.setPincode(((Long)jsonObject.get("pincode")).toString());
      }
      if (jsonObject.get("pointBal") != null) {
        searchCrmCustomer.setPointBal(((Double)jsonObject.get("pointBal")).toString());
      }
     }
    catch (Exception localException) {}
    if (response.equalsIgnoreCase("Timeout"))
    {
      displayCRMNetworkError(bus, "PaytmNetworkError", "Failure");
      
      return;
    }
    POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager("UIManager");
    if ((searchCrmCustomer == null) || (searchCrmCustomer.getResponse() == null) || (!searchCrmCustomer.getResponse().trim().equalsIgnoreCase("S"))) {
      if ((searchCrmCustomer != null) && (searchCrmCustomer.getResponse() != null) && (!searchCrmCustomer.getResponse().trim().equalsIgnoreCase("S")))
      {
        if ((searchCrmCustomer != null) && (searchCrmCustomer.getMobileNumber() != null) && (!searchCrmCustomer.getMobileNumber().trim().equalsIgnoreCase(""))) {
          displayCRMResponseError(bus, searchCrmCustomer.getMessage(), "CRMCustomerBalanceEnquiryError", "failureByPhoneNumber");
        } else {
          displayCRMResponseError(bus, searchCrmCustomer.getMessage(), "CRMCustomerBalanceEnquiryError", "failureByPhoneNumber");
        }
      }
      else if ((searchCrmCustomer != null) && (searchCrmCustomer.getMobileNumber() != null) && (!searchCrmCustomer.getMobileNumber().trim().equalsIgnoreCase(""))) {
        displayCRMNetworkError(bus, "CRMCustomerSearchNetworkError", "failureByPhoneNumber");
      } else {
        displayCRMNetworkError(bus, "CRMCustomerSearchNetworkError", "failureByPhoneNumber");
      }
    }
  }
  
  private HashMap populateHashMap(BusIfc bus, HashMap requestAttributes, MAXCRMSearchCustomer customer)
  {
    String mobileNumber = customer.getMobileNumber() != null ? customer.getMobileNumber() : "";
    String messageID = customer.getMessageId() != null ? customer.getMessageId() : "";
    String cardNumber = customer.getCardNumber() != null ? customer.getCardNumber() : "";
    String source = "SPAR";
    requestAttributes.put("mobileNumber", mobileNumber);
    requestAttributes.put("messageId", messageID);
    requestAttributes.put("cardNumber", cardNumber);
    
    return requestAttributes;
  }
  
  private HashMap populateHashMapforCustSearch(BusIfc bus, HashMap requestAttributes, MAXCRMSearchCustomer customer)
  {
    String mobile = customer.getMobileNumber() != null ? customer.getMobileNumber() : "";
    String messageID = customer.getMessageId() != null ? customer.getMessageId() : "";
    String cardNumber = customer.getCardNumber() != null ? customer.getCardNumber() : "";
    
    final String source = "SPAR";
    requestAttributes.put("mobile", mobile);
    //requestAttributes.put("messageId", messageID);
    requestAttributes.put("source", source);
   // requestAttributes.put("cardNumber", cardNumber);
    //requestAttributes.put("source", source);
    return requestAttributes;
  }
  
  protected void displayCRMResponseError(BusIfc bus, String errorMessage, String resourceId, String letterName)
  {
    POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager("UIManager");
    
    DialogBeanModel beanModel = new DialogBeanModel();
    String[] messgArray = new String[1];
    messgArray[0] = errorMessage;
    beanModel.setArgs(messgArray);
    beanModel.setResourceID(resourceId);
    beanModel.setType(DialogScreensIfc.CONFIRMATION);
    
    beanModel.setButtonLetter(DialogScreensIfc.BUTTON_YES,CommonLetterIfc.FAILURE);
    beanModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.CANCEL);
    
    ui.showScreen("DIALOG_TEMPLATE", beanModel);
  }
  
  protected void displayCRMNetworkError(BusIfc bus, String resourceId, String letterName)
  {
    POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager("UIManager");
    
    DialogBeanModel beanModel = new DialogBeanModel();
    beanModel.setResourceID(resourceId);
    beanModel.setType(1);
    beanModel.setButtonLetter(0, letterName);
    ui.showScreen("DIALOG_TEMPLATE", beanModel);
  }
  
  private void showProcessCRMRequestScreen(BusIfc bus)
  {
    POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager("UIManager");
    
    POSBaseBeanModel model = new POSBaseBeanModel();
    ui.showScreen("PROCESS_CRM_REQUEST", model);
  }
}
