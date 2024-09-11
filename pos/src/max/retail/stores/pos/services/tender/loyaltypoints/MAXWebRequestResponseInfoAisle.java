/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0     Jan 06, 2016		Ashish Yadav		Online Points Redemption FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.tender.loyaltypoints;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXLoyaltyDataTransaction;
import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.loyalty.MAXLoyaltyConstants;
import max.retail.stores.domain.transaction.MAXAbstractTenderableTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.sale.validate.MAXUtilityConstantsIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

// --------------------------------------------------------------------------
/**
 * Saving the request and sending the request to CRM system
 * 
 */
// --------------------------------------------------------------------------
public class MAXWebRequestResponseInfoAisle extends PosLaneActionAdapter {
	// ----------------------------------------------------------------------
	/**
	 * serialVersionUID long
	 */
	// ----------------------------------------------------------------------
	private static final long serialVersionUID = 1227174702805395077L;
	// Variable defined for Response Code
	private static int responseCode;
	// HashMap defined for RequestAttributes
	private HashMap requestAttributes;
	boolean IsRedeemcall2 =false;
	MAXCustomerIfc maxCustomer = null;

	public void traverse(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		String responce = null;
		/**Changes start for rev 1.1**/
		MAXSaleReturnTransaction tran = null;
	     maxCustomer = (MAXCustomerIfc) cargo.getCustomer();
        if(cargo != null && cargo.getTransaction() instanceof MAXSaleReturnTransaction)
        {
        	tran = (MAXSaleReturnTransaction) cargo.getTransaction();
        }
        
        /**Changes end for rev 1.1**/
		requestAttributes = new HashMap();

		HashMap encryptAtribute = new HashMap();
		// Adding TIC Number in HashMap for encryption.
		// Added Null check for the Customer

		 //Changes for Rev 1.1 starts 
		if (cargo.getCustomer() == null && (tran !=null)) {
			maxCustomer =(MAXCustomer) tran.getCustomer();
			encryptAtribute.put(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER, maxCustomer.getCustomerID());
		} 
		//Changes start for bug 17105
		else if (maxCustomer != null && maxCustomer.getCustomerID() !=null)
		{
			encryptAtribute.put(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER, maxCustomer.getCustomerID());	
		}
		else if (maxCustomer != null && maxCustomer.getCustomerID() ==null && maxCustomer.getLoyaltyCardNumber() != null)
		{
			encryptAtribute.put(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER, maxCustomer.getLoyaltyCardNumber());	
		}
		//Changes end for bug 17105
		else {
			MAXCustomerIfc customer = (MAXCustomerIfc) ((MAXAbstractTenderableTransaction) cargo.getCurrentTransactionADO().toLegacy()).getTicCustomer();
			if(customer==null && cargo.getCurrentTransactionADO().getCustomer()!=null && cargo.getCurrentTransactionADO().getCustomer() instanceof MAXCustomer)
			{
				customer=(MAXCustomerIfc)cargo.getCurrentTransactionADO().getCustomer();
			}
			
			encryptAtribute.put(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER, customer.getLoyaltyCardNumber());
		/*}*/
		/* Changes for Rev 1.1 ends */
		}
		MAXLoyaltyDataTransaction loyaltyDataTransaction = null;
		loyaltyDataTransaction = (MAXLoyaltyDataTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.LOYALTY_DATA_TRANSACTION);
		
        /**Changes start for send without encrypted value to crm**/
		/*try {
			// Encrypt the values the value of TIC Number through encryption
			// function defined in DB
			requestAttributes = loyaltyDataTransaction.encryptValue(encryptAtribute);
		} catch (DataException e) {
			logger.error("DataException::" + e.getMessage() + "");
			*//**Changes start for rev 1.1 **//*
			return;
			*//**Changes end for rev 1.1 **//*
		}*/
		requestAttributes = encryptAtribute;
		/**Changes end for send without encrypted value to crm**/
		
		// Method use to populate HashMap with the values from Cargo
		requestAttributes = populateHashMap(cargo, requestAttributes);
        if(!IsRedeemcall2){
		try {
			// Saving the request in the DB
			loyaltyDataTransaction.saveRequest(requestAttributes);
		} catch (DataException e1) {
			logger.error("DataException::" + e1.getMessage() + "");
		}
}
		// Start Send OTP request to CRM
		boolean transReentry = cargo.getRegister().getWorkstation().isTransReentryMode();;
		boolean transTrainingmode=cargo.getRegister().getWorkstation().isTrainingMode();
		String response = "";
		MAXCRMObj crmobj= new MAXCRMObj();
		if(!transReentry &&!transTrainingmode){
			if(!maxCustomer.IsOtpValidation()){
				String url=Gateway.getProperty("application", "LoyaltyWebServiceURLRedeemCall1", null);

				crmobj.setCRM_URL(url);

			}
			else{
				crmobj.setCRM_URL(Gateway.getProperty("application", "LoyaltyWebServiceURLRedeemCall2", null));;	

			}
		crmobj.setRequestMethod(MAXUtilityConstantsIfc.CRM_CAPILLARY_METHOD_POST);
		String requestJsonMsg =MAXWebRequestResponseInfoAisle.createOTPCallRequestMessage(crmobj,requestAttributes);
		logger.debug("OTP Request : "+ requestJsonMsg);
		//System.out.println("OTP Request : "+ requestJsonMsg);
		crmobj.setRequestMessage(requestJsonMsg);
		// Hit the webservice
		
		try
		{
			response = executePost(crmobj).getConnResponseMessage().toString();
		}catch(SocketException exe)
		{
			responce = exe.getMessage();
		}
		catch(Exception e)
		{
			logger.debug("error in receiving response");
		}
		logger.debug("OTP RESPONSE : "+ response);
		//System.out.println("OTP RESPONSE : "+ response);
		/*if(customer.IsOtpValidation()){
			customer.setOtpValidation(false);
		}*/
			if(crmobj.getConnResponseCode().equalsIgnoreCase("200")){
				JSONParser jsonParser = new JSONParser();
				String jsonMessage = crmobj.getConnResponseMessage();
				Object object = null;
				try {
					object = jsonParser.parse(jsonMessage.toString());
				} catch (ParseException e) {

					e.printStackTrace();
				}
				Boolean isOTPEnabled = new Boolean(false);
				
				JSONObject jsonObject = (JSONObject) object;				
				isOTPEnabled = (Boolean) jsonObject.get("isOTPEnabled");
				
				String message = (String) jsonObject.get("message");
				int otp = 0 ;
				Double points = new Double(0);
				double loayaltypoint = 0; 
				try{
					if((jsonObject.get("otp")!=null))
					otp=Integer.parseInt(jsonObject.get("otp").toString());
					if( jsonObject.get("points")!=null){
					points =	((Double) jsonObject.get("points"));
					}
					loayaltypoint=points.doubleValue();
					}
				catch(Exception e){
					e.printStackTrace();
				}
				crmobj.setOtp(otp) ;
				//System.out.println("OTP :" +otp);
				crmobj.setMessage(message);
				crmobj.setLoyaltypoint(loayaltypoint);
				if(isOTPEnabled!=null){
				crmobj.setOTPenabledstore(isOTPEnabled.booleanValue());
				}
				crmobj.setResponse( (String) jsonObject.get("response"));
				response= crmobj.getResponse();
				 
				if(response.equals("S")){
					if(!maxCustomer.IsOtpValidation()&&isOTPEnabled!=null){
				if(!isOTPEnabled.booleanValue() &&loayaltypoint==0){
					if(!maxCustomer.IsOtpValidation()){ /*In case of Redeemcall2 no error message should prompt*/
					displayErrorDialog(bus, "LoyaltyConnectionError", null,crmobj);
					upadateFaliurerequest( bus,  response,  cargo,  loyaltyDataTransaction, crmobj);
					return;
					}
					else{
						upadateFaliurerequest( bus,  response,  cargo,  loyaltyDataTransaction, crmobj);
						return;
					}
				}
					}
					else if(loayaltypoint==0){
						if(!maxCustomer.IsOtpValidation()){
						displayErrorDialog(bus, "LoyaltyConnectionError", null,crmobj);
						upadateFaliurerequest( bus,  response,  cargo,  loyaltyDataTransaction, crmobj);
						}
						else{
							upadateFaliurerequest( bus,  response,  cargo,  loyaltyDataTransaction, crmobj);
							bus.mail(new Letter("Continue"), BusIfc.CURRENT);
						}
					}
				}
				
				else if(!response.equals("S")&& !response.equals("16")){
					crmobj.setResponse("F");
					if(!maxCustomer.IsOtpValidation()){
						upadateFaliurerequest( bus,  response,  cargo,  loyaltyDataTransaction, crmobj);
						String []crmMessageDetails = crmobj.getConnResponseMessage().split(",");
						String crmMessage = crmMessageDetails[2];
						String []msg = crmMessage.split(":");
						if(msg[1] != null)
						{
						displayErrorDialog(bus, "MESSAGE_FAILURE_NOTICE", msg[1],crmobj);
						}
						return;
						}
						else{
							upadateFaliurerequest( bus,  response,  cargo,  loyaltyDataTransaction, crmobj);
							bus.mail(new Letter("Continue"), BusIfc.CURRENT);
						}
					
				}
		
		
			
		
		}
		// End Send OTP request to CRM
		}
	else{

			HashMap tenderAttributes = cargo.getTenderAttributes();
		
			tenderAttributes.put(TenderConstants.AMOUNT, cargo.getTenderAttributes().get("AMOUNT").toString());
			//MAXCustomerIfc customer=null;
			if(cargo.getCurrentTransactionADO().toLegacy() instanceof SaleReturnTransaction)
			{
				maxCustomer = (MAXCustomerIfc) ((SaleReturnTransaction) cargo.getCurrentTransactionADO().toLegacy()).getCustomer();
			}else if(cargo.getCurrentTransactionADO().toLegacy() instanceof LayawayPaymentTransaction)
			{
				maxCustomer=(MAXCustomerIfc) ((LayawayPaymentTransaction)cargo.getCurrentTransactionADO().toLegacy()).getCustomer();
			}
			
			tenderAttributes.put(TenderConstants.NUMBER, maxCustomer.getCustomerID());
			
			tenderAttributes.put(TenderConstants.AGENCY_NAME, "Loyalty Points");
			bus.mail(new Letter("Continue"), BusIfc.CURRENT);

		
	}
		// Prepare URL for sending the request with the parameters
		//String urlParameters = createURL(requestAttributes, bus);

		// Displaying error message if URL could not be formed
		/*if (urlParameters.equalsIgnoreCase(null)) {
			displayErrorDialog(bus, "LoyaltyConnectionError", null,crmobj);
			return;
		}*/
		// Set URL with parameters
		// String targetURL
		// ="http://172.16.13.21/crmresponse/Response?"+urlParameters;
		/*String URL = Gateway.getProperty("application", "LoyaltyWebServiceURL", null);
		String targetURL = URL + urlParameters;*/
		//System.out.println("URL:" + targetURL);
		// Defined for executing the web request
		//String response = executePost(targetURL, urlParameters);
	//	System.out.println("Response: "+response);

		processResponseCode(bus, response, loyaltyDataTransaction,crmobj,responce);
		
	}

	/**
	 * Use for populating the HashMap with needed values from Cargo
	 * 
	 * @param targetURL
	 * @param urlParameters
	 */
	private HashMap populateHashMap(TenderCargo cargo, HashMap requestAttributes) {
		EYSDate date = DomainGateway.getFactory().getEYSDateInstance();

		requestAttributes.put(MAXLoyaltyConstants.STORE_ID, cargo.getStoreStatus().getStore().getStoreID());
		requestAttributes.put(MAXLoyaltyConstants.TILL_ID, cargo.getRegister().getCurrentTillID());
		requestAttributes.put(MAXLoyaltyConstants.REGISTER_ID, cargo.getRegister().getWorkstation().getWorkstationID());
		requestAttributes.put(MAXLoyaltyConstants.INVOICE_BUSINESS_DATE, cargo.getStoreStatus().getBusinessDate());
		requestAttributes.put(MAXLoyaltyConstants.INVOICE_NUMBER, cargo.getCurrentTransactionADO().getTransactionID());
		// Invoice Total Amount
		//changes start for rev 1.1
		CurrencyIfc grandTotal=null;
		//CurrencyIfc grandTotal = ((SaleReturnTransaction) cargo.getCurrentTransactionADO().toLegacy()).getTransactionTotals().getGrandTotal();
		if(!(cargo.getTransaction().getTransactionTotals().getGrandTotal() instanceof SaleReturnTransaction))
		{
	        grandTotal = cargo.getTransaction().getTransactionTotals().getGrandTotal();
		}
		else
		{
			grandTotal = ((SaleReturnTransaction) cargo.getCurrentTransactionADO().toLegacy()).getTransactionTotals().getGrandTotal();
		}
		//changes end for rev 1.1
		String grandTotalStr = grandTotal.toString();
		requestAttributes.put(MAXLoyaltyConstants.TRAN_TOTAL_AMOUNT, grandTotalStr); 
		// Redeem Amount
		requestAttributes.put(MAXLoyaltyConstants.SETTLE_TOTAL_AMOUNT, cargo.getTenderAttributes().get(TenderConstants.AMOUNT).toString());
		requestAttributes.put(MAXLoyaltyConstants.REQUEST_TYPE_A, MAXLoyaltyConstants.REGULAR_REQUEST);
		requestAttributes.put(MAXLoyaltyConstants.REQUEST_STATUS, MAXLoyaltyConstants.REQUESTED);
		
		// Get value from application.properties
		//Rev 1.3 start  for RedeemCall1 and RedeemCall2
		//MAXCustomerIfc customer = (MAXCustomerIfc) cargo.getCustomer();
		String URL="";
		
		try
		{
		if(!maxCustomer.IsOtpValidation()){
		 URL = Gateway.getProperty("application", "LoyaltyWebServiceURLRedeemCall1", null);
		}
		/*If otp valiadtion is suceesfull localy Redeemcall2 will be set in url */
		else {
			 URL = Gateway.getProperty("application", "LoyaltyWebServiceURLRedeemCall2", null);
			 IsRedeemcall2=true;
		}
		}catch(Exception e)
		{
			e.getMessage();
			logger.debug("URL not found in application.properties to hit CRM : ");
		}
		//Rev 1.3 End 
		
		requestAttributes.put(MAXLoyaltyConstants.REQUEST_TYPE_B, MAXLoyaltyConstants.BLOCK);
		// Get value from application.properties
		//String URL = Gateway.getProperty("application", "LoyaltyWebServiceURL", null);
		String timeOut = Gateway.getProperty("application", "LoyaltytimeOutInMilliSeconds", null);
		requestAttributes.put(MAXLoyaltyConstants.REQUEST_URL, URL);
		requestAttributes.put(MAXLoyaltyConstants.REQUEST_TIME_OUT, timeOut);

		requestAttributes.put(MAXLoyaltyConstants.REQUEST_DATE_TIME, date.toFormattedString(MAXLoyaltyConstants.DATE_FORMAT_NOW));
		// Message ID
		StringBuffer transanctionId = new StringBuffer();
		transanctionId.append(cargo.getCurrentTransactionADO().getTransactionID());
		transanctionId.append(date.toFormattedString(MAXLoyaltyConstants.NEW_DATE_FORMAT));
		/*Changes start for rev bug 17101*/
		/*requestAttributes.put(MAXLoyaltyConstants.MESSAGE_ID, transanctionId.toString());
		requestAttributes.put(MAXLoyaltyConstants.TIME_OUT_REQUEST_MESSAGE_ID, null);
		// return HasMap with required values
		return requestAttributes;*/
		if(!maxCustomer.IsOtpValidation()){
			requestAttributes.put(MAXLoyaltyConstants.MESSAGE_ID, transanctionId.toString());
			requestAttributes.put(MAXLoyaltyConstants.REQUEST_TYPE_B, MAXLoyaltyConstants.BLOCK);
			maxCustomer.setMessageId(transanctionId.toString());
			} 
			else{
				requestAttributes.put(MAXLoyaltyConstants.MESSAGE_ID, maxCustomer.getMessageId());	
				requestAttributes.put(MAXLoyaltyConstants.REQUEST_TYPE_B, MAXLoyaltyConstants.BLOCK);
				IsRedeemcall2=true;
				
			}
			requestAttributes.put(MAXLoyaltyConstants.TIME_OUT_REQUEST_MESSAGE_ID, null);
			// return HasMap with required values
			return requestAttributes;
			/*Changes end for rev bug 17101*/
	}

	private void processResponseCode(BusIfc bus, String response, MAXLoyaltyDataTransaction loyaltyDataTransaction,MAXCRMObj crmobj,String respon) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();

		// when Successful response is received
		// Response Code - 200 denotes successful response
		if(crmobj.getResponse()!=null){
		if (response.equalsIgnoreCase("Timeout")) {
			processTimeOutResponse(bus, response, cargo, loyaltyDataTransaction,crmobj);
		}
		else if(respon !=null && respon.equalsIgnoreCase("NetworkError"))
		{
			displayErrorDialog(bus, "LoyaltyNetworkError", null,crmobj);
		}
		else if (responseCode == 200 && !response.equalsIgnoreCase("") && (!response.equalsIgnoreCase("Timeout"))) {
			processSuccessResponse(bus, response, cargo, loyaltyDataTransaction,crmobj);
		}
		// Time Out Case handling
		// validating the error code for timeout
		// 408- HTTP_CLIENT_TIMEOUT && 504- HTTP_GATEWAY_TIMEOUT
		else if (crmobj.getResponse()!=null && crmobj.getResponse().equalsIgnoreCase("Timeout")) {
			if(!maxCustomer.IsOtpValidation()){
				POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
				DialogBeanModel beanModel = new DialogBeanModel();
				beanModel.setResourceID("LoyaltyNetworkError");
				beanModel.setType(DialogScreensIfc.ERROR);
				beanModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Undo");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
				return;
				
				}
				else{
					upadateFaliurerequest( bus,  response,  cargo,  loyaltyDataTransaction, crmobj);
					bus.mail(new Letter("Continue"), BusIfc.CURRENT);
				}
			
		} 
		//1.3 start
		else if (responseCode != 200 && response.equalsIgnoreCase("")) {
			
			
			if(!maxCustomer.IsOtpValidation()){
				displayErrorDialog(bus, "LoyaltyNetworkError", null,crmobj);
				return;
				}
				else{
					upadateFaliurerequest( bus,  response,  cargo,  loyaltyDataTransaction, crmobj);
					bus.mail(new Letter("Continue"), BusIfc.CURRENT);
				}
		} 
		}
		//1.3 End
	//Rev1.2 Start
	else if(cargo.getRegister().getWorkstation().isTransReentryMode()){
		return;
	}
	else if(cargo.getRegister().getWorkstation().isTrainingMode()){
		return;
	}
	//Rev1.2 End
	else {
		if(!maxCustomer.IsOtpValidation()){
			displayErrorDialog(bus, "LoyaltyNetworkError", null,crmobj);
			
			}
			else{
				upadateFaliurerequest( bus,  response,  cargo,  loyaltyDataTransaction, crmobj);
				bus.mail(new Letter("Continue"), BusIfc.CURRENT);
			}
	}

}

	/**
	 * Use for executing the Request with needed parameters
	 * 
	 * @param targetURL
	 * @param urlParameters
	 */
	public static String executePost(String targetURL, String urlParameters) {
		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL(targetURL);
			String timeOut = Gateway.getProperty("application", "LoyaltytimeOutInMilliSeconds", "5000");
			System.getProperties().setProperty("sun.net.client.defaultConnectTimeout", timeOut);
			System.getProperties().setProperty("sun.net.client.defaultReadTimeout", timeOut);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			// Get Response
			responseCode = connection.getResponseCode();

			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();
		} catch (Exception e) {
			logger.error("Error in sending Request" + e.getMessage() + "");
			try {
				responseCode = connection.getResponseCode();
			} catch (IOException e1) {
				logger.error("IO Exception Caught::" + e1.getMessage() + "");
				return "Timeout";
			}
			return "";

		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * Show an error dialog
	 * 
	 * @param bus
	 * @param name
	 * @param dialogType
	 */
	protected void displayErrorDialog(BusIfc bus, String name, String message,MAXCRMObj crmobj) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		DialogBeanModel beanModel = new DialogBeanModel();
		if (name.equalsIgnoreCase("LoyaltyNetworkError")) {
			beanModel.setResourceID("LoyaltyNetworkError");
			beanModel.setType(DialogScreensIfc.ERROR);
			beanModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "No");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
		}
		if (name.equalsIgnoreCase("MESSAGE_FAILURE_NOTICE")) {
			String msg[] = new String[1];
			msg[0] = message;
			beanModel.setResourceID("MESSAGE_FAILURE_NOTICE");
			beanModel.setArgs(msg);
			beanModel.setType(DialogScreensIfc.ERROR);
			beanModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, "No");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
		}
		if (name.equalsIgnoreCase("AMOUNT_NOT_AVAILABLE_NOTICE")) {
			String msg[] = new String[1];
			msg[0] = message;
			beanModel.setResourceID("AMOUNT_NOT_AVAILABLE_NOTICE");
			beanModel.setArgs(msg);
			beanModel.setType(DialogScreensIfc.CONFIRMATION);
			beanModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Yes");
			beanModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "No");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
		}
		/**Changes start for rev 1.1 **/
		if (name.equalsIgnoreCase("LOYALTY_DATABASE_ERROR")) {
			beanModel.setResourceID("LOYALTY_DATABASE_ERROR");
			beanModel.setType(DialogScreensIfc.ERROR);
			beanModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Ok");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
		}
		if (name.equalsIgnoreCase("LoyaltyConnectionError")) {
			beanModel.setResourceID("LoyaltyConnectionError");
			beanModel.setType(DialogScreensIfc.ERROR);
			beanModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Ok");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
		}
		if (name.equalsIgnoreCase("MESSAGE_FAILURE_NOTICE")) {
			String msg[] = new String[1];
			msg[0] = message;
			beanModel.setResourceID("MESSAGE_FAILURE_NOTICE");
			beanModel.setArgs(msg);
			beanModel.setType(DialogScreensIfc.ERROR);
			beanModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, "No");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
		}
		/**Changes end for rev 1.1 **/
	}

	/**
	 * Creates the URL with the input parameters needed by CRM System
	 * 
	 * @param bus
	 * @param requestInfo
	 */
	protected String createURL(HashMap requestInfo, BusIfc bus) {
		String urlParameters;
		try {
			urlParameters = URLEncoder.encode("flag", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aMsgId", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.MESSAGE_ID).toString(), "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aReqT1", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.REQUEST_TYPE_A).toString(), "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aTimOt", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.REQUEST_TIME_OUT).toString(), "UTF-8")+"+";
			urlParameters += "&" + URLEncoder.encode("aStoId", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.STORE_ID).toString(), "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aRegId", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.REGISTER_ID).toString(), "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aTilId", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.TILL_ID).toString(), "UTF-8");
			
		    SimpleDateFormat sm = new SimpleDateFormat("dd-MMM-yyyy");
			Date date = new Date(requestInfo.get(MAXLoyaltyConstants.INVOICE_BUSINESS_DATE).toString());
			String mdy = sm.format(date);
			
			urlParameters += "&" + URLEncoder.encode("aBusDt", "UTF-8") + "="
					+ URLEncoder.encode(/*requestInfo.get(MAXLoyaltyConstants.INVOICE_BUSINESS_DATE).toString()*/mdy, "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aInvNo", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.INVOICE_NUMBER).toString(), "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aInvTo", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.TRAN_TOTAL_AMOUNT).toString(), "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aRedAt", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.SETTLE_TOTAL_AMOUNT).toString(), "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aReqTp", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.REQUEST_TYPE_B).toString(), "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aLoyNo", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER).toString(), "UTF-8");
			return urlParameters;
			
		} catch (UnsupportedEncodingException e) {
			Logger.getLogger(MAXWebRequestResponseInfoAisle.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
	}

	/**
	 * Process Success Response from CRM
	 * 
	 * @param bus
	 * @param name
	 * @param dialogType
	 */
	protected void processSuccessResponse(BusIfc bus, String response, TenderCargo cargo, MAXLoyaltyDataTransaction loyaltyDataTransaction,MAXCRMObj crmobj) {
		HashMap newvalue = new HashMap();
		HashMap tenderAttributes = cargo.getTenderAttributes();
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		// For formatting response
		// Eg. Response:S
		// Message: Invalid Redemption
		// Points: 10.00
		//String trimString = response.toString().trim();
		//System.out.println("Response: "+trimString );
		//String[] splitStr = trimString.split("\r\r\r");

		//String resMsg = splitStr[0];
		//String valMsg = splitStr[1];
		//String ptsMsg = splitStr[2];

		//String[] resFlag = resMsg.split(":");
		//String[] messValue = valMsg.split(":");
		//String[] pointValue = ptsMsg.split(":");

		// setting up the HashMap for updating DB
		//newvalue.put(resFlag[0], resFlag[1]);
		//newvalue.put(messValue[0], messValue[1]);
		//newvalue.put(pointValue[0].replaceAll("\r", "").trim(), pointValue[1]);
		if(crmobj!=null){
		newvalue.put(MAXLoyaltyConstants.MESSAGE_ID, requestAttributes.get(MAXLoyaltyConstants.MESSAGE_ID));
		newvalue.put(MAXLoyaltyConstants.FLAG, MAXLoyaltyConstants.RESPONSE_FLAG);
		newvalue.put(MAXLoyaltyConstants.REQUEST_STATUS, MAXLoyaltyConstants.RESPONSE_RECEIVED);
		crmobj.getLoyaltypoint();
		newvalue.put(MAXLoyaltyConstants.RESPONSE_APPROVED_VALUE, Double.toString(crmobj.getLoyaltypoint()));
		newvalue.put(MAXLoyaltyConstants.RESPONSE_MESSAGE, crmobj.getMessage());
		newvalue.put(MAXLoyaltyConstants.RESPONSE, MAXLoyaltyConstants.SUCCESS);
		newvalue.put(MAXLoyaltyConstants.REQUEST_URL, requestAttributes.get(MAXLoyaltyConstants.REQUEST_URL));
		newvalue.put(MAXLoyaltyConstants.REQUEST_TYPE_B, requestAttributes.get(MAXLoyaltyConstants.REQUEST_TYPE_B));
		// Update WEB REQUEST TABLE
		try {
			loyaltyDataTransaction.updateRequest(newvalue);
		} catch (DataException e) {
			logger.error("DataException::" + e.getMessage() + "");
		}

		// If we receive Failure notice from CRM display error message
		// as it is coming from CRM
		/*if (resFlag[1].equalsIgnoreCase("F")) {

			String msg[] = new String[1];
			msg[0] = messValue[1];
			displayErrorDialog(bus, "MESSAGE_FAILURE_NOTICE", msg[0]);
			return;
		}*/
		
		if (crmobj.result) {

			String msg[] = new String[1];
			msg[0] = crmobj.getMessage();
			System.out.println("718 :"+msg[0]);
			displayErrorDialog(bus, "MESSAGE_FAILURE_NOTICE", msg[0], crmobj);
			return;
		}
		
		if(!crmobj.isOTPenabledstore()|| maxCustomer.IsOtpValidation()){
		// Re-calculate points value on success message from CRM
		CurrencyIfc loyaltyPointsApproved = DomainGateway.getBaseCurrencyInstance(newvalue.get(MAXLoyaltyConstants.RESPONSE_APPROVED_VALUE).toString());
		CurrencyIfc loyaltyPointsConversionFactor = null;
		try {
			loyaltyPointsConversionFactor = DomainGateway.getBaseCurrencyInstance(pm.getStringValue("LoyaltyPointsConversionFactor"));
		} catch (ParameterException pe) {
			logger.error("Error retrieving parameter:" + pe.getMessage() + "");
		}
		CurrencyIfc approvedAmount = (DomainGateway.getBaseCurrencyInstance("100").multiply(loyaltyPointsApproved)).divide(loyaltyPointsConversionFactor);
		CurrencyIfc tenderAmount = DomainGateway.getBaseCurrencyInstance(tenderAttributes.get(TenderConstants.AMOUNT).toString());

		// Check condition for Less amount
		// if comes true display message AMOUNT_NOT_AVAILABLE_NOTICE
		if (approvedAmount.compareTo(tenderAmount) == CurrencyIfc.LESS_THAN) {
			tenderAttributes.put(TenderConstants.AMOUNT, approvedAmount.toString());
			String msg[] = new String[1];
			msg[0] = loyaltyPointsApproved.toString();
			displayErrorDialog(bus, "AMOUNT_NOT_AVAILABLE_NOTICE", msg[0],crmobj);
			return;
		}
		// when tender amount is equal to settlement amount
		// set tender attributes with value
		tenderAttributes.put(TenderConstants.AMOUNT, approvedAmount.toString());
		tenderAttributes.put(TenderConstants.FACE_VALUE_AMOUNT, approvedAmount.toString());
		MAXCustomerIfc customer = (MAXCustomerIfc) cargo.getCustomer();
		tenderAttributes.put(TenderConstants.NUMBER, customer.getLoyaltyCardNumber());
		tenderAttributes.put(TenderConstants.AGENCY_NAME, "Loyalty Points");
		}
		
		
		
		int otp=crmobj.getOtp();
		maxCustomer.setLoyaltyotp(otp);
		//System.out.println("otppppp" +otp);
		cargo.setCustomer(maxCustomer);
		

		boolean OTPAllowed;

		OTPAllowed=true;
		if(crmobj.isOTPenabledstore()){
			if(!maxCustomer.IsOtpValidation()){
				if(OTPAllowed&&(otp!=0))
				{  
					bus.mail(new Letter("Otp"), BusIfc.CURRENT);
				}		 
			}
			else
			{
				bus.mail(new Letter("Continue"), BusIfc.CURRENT);
			}
		}
		else
		{
			crmobj.setOTPenabledstore(false);
			maxCustomer.setOtpValidation(false);
			bus.mail(new Letter("Continue"), BusIfc.CURRENT);
		}
		/*Rev 1.3 End*/
		}

	}

	/**
	 * Process TimeOut Response to CRM
	 * 
	 * @param bus
	 * @param name
	 * @param dialogType
	 */
	protected void processTimeOutResponse(BusIfc bus, String response, TenderCargo cargo, MAXLoyaltyDataTransaction loyaltyDataTransaction,MAXCRMObj crmobj) {

		HashMap newvalue = new HashMap();
		EYSDate date = DomainGateway.getFactory().getEYSDateInstance();

		requestAttributes.put(MAXLoyaltyConstants.REQUEST_STATUS, MAXLoyaltyConstants.TIMEOUT);
		requestAttributes.put(MAXLoyaltyConstants.FLAG, MAXLoyaltyConstants.TIMEOUT_FLAG);
		try {
			loyaltyDataTransaction.updateRequest(requestAttributes);
		} catch (DataException e) {
			logger.error("DataException::" + e.getMessage() + "");
		}
		// Setting the timeout message id with the old Message ID
		String timeOutMsgId = requestAttributes.get(MAXLoyaltyConstants.MESSAGE_ID).toString();

		StringBuffer transanctionId = new StringBuffer();
		transanctionId.append(cargo.getCurrentTransactionADO().getTransactionID());
		transanctionId.append(date.toFormattedString(MAXLoyaltyConstants.NEW_DATE_FORMAT));
		requestAttributes.put(MAXLoyaltyConstants.MESSAGE_ID, transanctionId.toString());
		requestAttributes.put(MAXLoyaltyConstants.REQUEST_TYPE_A, MAXLoyaltyConstants.TIMEOUT_REQUEST);
		requestAttributes.put(MAXLoyaltyConstants.REQUEST_STATUS, MAXLoyaltyConstants.REQUESTED);
		requestAttributes.put(MAXLoyaltyConstants.TIME_OUT_REQUEST_MESSAGE_ID, timeOutMsgId);
		try {
			loyaltyDataTransaction.saveRequest(requestAttributes);
		} catch (DataException e) {
			logger.error("DataException::" + e.getMessage() + "");
		}
		// Send reversal request in case of time-out
		//String urlParameters1 = createURL(requestAttributes, bus);

		/*if (urlParameters1.equalsIgnoreCase(null)) {
			displayErrorDialog(bus, "LoyaltyConnectionError", null);
			return;
		}*/

		// String targetURL1
		// ="http://www.lscircle.in/crmresponse/Response?"+urlParameters1;
		//String URL1 = Gateway.getProperty("application", "LoyaltyWebServiceURL", null);
	//	String targetURL1 = URL1 + urlParameters1;
		//String response1 = executePost(targetURL1, urlParameters1);

		String requestJsonMsg =MAXWebRequestResponseInfoAisle.createOTPCallRequestMessage(crmobj,requestAttributes);
		if (requestJsonMsg.equalsIgnoreCase(null)) {
			displayErrorDialog(bus, "LoyaltyConnectionError", null,crmobj);
			return;
		}
		crmobj.setRequestMessage(requestJsonMsg);
		// Hit the webservice
		try
		{
		response = executePost(crmobj).getConnResponseMessage().toString();
		}catch(Exception e)
		{
			
		}
		// On success response of timeout request
		// 200 denotes success code
		if (responseCode == 200 && !response.equalsIgnoreCase("")) {
			String resMsg="";
			String valMsg="";
			String ptsMsg="";
			//String trimString = response1.toString().trim();
		//	String[] splitStr = trimString.split("\r\r\r");
			//changes  start for rev 1.1
            try
            {
            	//changes end for rev 1.1
			//String resMsg = splitStr[0];
			//String valMsg = splitStr[1];

			//String[] resFlag = resMsg.split(":");
		//	String[] messValue = valMsg.split(":");
            	
            	if(crmobj.getConnResponseCode().equalsIgnoreCase("200")){
    				JSONParser jsonParser = new JSONParser();
    				String jsonMessage = crmobj.getConnResponseMessage();
    				Object object = null;
    				try {
    					object = jsonParser.parse(jsonMessage.toString());
    				} catch (ParseException e) {

    					e.printStackTrace();
    				}
    				/*Boolean isOTPEnabled = new Boolean(false);*/
    				String resultCode=new String();
    				JSONObject jsonObject = (JSONObject) object;
    				//String otp = (String) jsonObject.get("otp");
    				/*Boolean result = (Boolean) jsonObject.get("result");
    				isOTPEnabled = (Boolean) jsonObject.get("isOTPEnabled");*/
    				resultCode = (String) jsonObject.get("resultCode");
    				String message = (String) jsonObject.get("message");
    				crmobj.setOTPEnabled(true);
    				Integer otp= null;
    				try{
    					if(jsonObject.get("otp")!=null&& jsonObject.get("otp")!=""){
    					String otpcode = (String) jsonObject.get("otp");
    				 otp = Integer.getInteger( otpcode);
    				 crmobj.setOtp(otp.intValue());
    					}
    				}
    				catch(Exception e){
    					e.printStackTrace();
    				}
    				
    				
    				message="Successful Transaction"; //dummy value
    				crmobj.setMessage(message);
    				crmobj.setResultCode(resultCode);
    				crmobj.setResponse( (String) jsonObject.get("response"));
    				//crmobj.setLoyaltypoint("166");
    				
    		
    				resMsg = crmobj.getResponse();
    				valMsg= crmobj.getMessage();
    				ptsMsg = Double.toString(crmobj.getLoyaltypoint());
    		
    			
    		
    		}
            	resMsg = crmobj.getResponse();
			if (resMsg.equalsIgnoreCase("F")) {
				String msg[] = new String[1];
				msg[0] = valMsg;
				displayErrorDialog(bus, "MESSAGE_FAILURE_NOTICE", msg[0],crmobj);
				return;
			}

		//	newvalue.put(resFlag[0], resFlag[1]);
			//newvalue.put(messValue[0], messValue[1]);
			newvalue.put(MAXLoyaltyConstants.RESPONSE_APPROVED_VALUE, null);
			newvalue.put(MAXLoyaltyConstants.MESSAGE_ID, requestAttributes.get(MAXLoyaltyConstants.MESSAGE_ID));
			newvalue.put(MAXLoyaltyConstants.FLAG, MAXLoyaltyConstants.RESPONSE_FLAG);
			newvalue.put(MAXLoyaltyConstants.REQUEST_STATUS, MAXLoyaltyConstants.RESPONSE_RECEIVED);

			try {
				loyaltyDataTransaction.updateRequest(newvalue);
			} catch (DataException e) {
				e.printStackTrace();
			}
            }
          //changes start for rev 1.1
            catch (Exception e) {
            	logger.error("Time Out Exception::" + e.getMessage() + "");
			}
          //changes end for rev 1.1
			bus.mail(new Letter("No"), BusIfc.CURRENT);
			return;
		}
		// Time Out Case handling
		// validating the error code for timeout
		// 408- HTTP_CLIENT_TIMEOUT && 504- HTTP_GATEWAY_TIMEOUT
		else if (crmobj.getResponse().equalsIgnoreCase("Timeout")) {
			requestAttributes.put(MAXLoyaltyConstants.REQUEST_STATUS, MAXLoyaltyConstants.TIMEOUT);
			requestAttributes.put(MAXLoyaltyConstants.FLAG, MAXLoyaltyConstants.TIMEOUT_FLAG);
			try {
				loyaltyDataTransaction.updateRequest(requestAttributes);
			} catch (DataException e) {
				logger.error("DataException::" + e.getMessage() + "");
			}
			displayErrorDialog(bus, "LoyaltyConnectionError", null,crmobj);
			return;
		}

		else {
			String msg[] = new String[1];
			msg[0] = "Unexpected error occured";
			displayErrorDialog(bus, "MESSAGE_FAILURE_NOTICE", msg[0],crmobj);
			return;
		}

	}
//Rev 1.3 start
	
	protected static String createOTPCallRequestMessage(MAXCRMObj Obj, HashMap requestInfo) {
		
		    StringWriter reqJsonMessage = new StringWriter();
		//try {
		    Boolean sendSMS = new Boolean(true);
			JSONObject objLoyaltyValues = new JSONObject();
			
           /* String str=requestInfo.get(MAXLoyaltyConstants.INVOICE_BUSINESS_DATE).toString();
            //Date date = new Date(requestInfo.get(MAXLoyaltyConstants.INVOICE_BUSINESS_DATE).toString());
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
		    Date date = null;
			try {
				date = df.parse(str);
			} catch (java.text.ParseException e) {
				System.out.println(e);	
			}*/
		    /**Changes start for rev 1.2**/
			// Changes starts for Rev 1.0 (Ashish : OTP loyalty)
			String str = requestInfo.get(MAXLoyaltyConstants.INVOICE_BUSINESS_DATE).toString();
			DateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
			Date dt = null;
			try {
				dt = format.parse(str);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
			format = new SimpleDateFormat("dd/MM/YY");
			String st = format.format(dt);
		    long epoch = dt.getTime();
		 // Changes starts for Rev 1.0 (Ashish : OTP loyalty)
		    Long invoiceDate =new Long(epoch);
			objLoyaltyValues.put(MAXLoyaltyConstants.SEND_SMS,  sendSMS);
			objLoyaltyValues.put(MAXLoyaltyConstants.OTP_MESSAGE_ID, requestInfo.get(MAXLoyaltyConstants.MESSAGE_ID).toString());
			objLoyaltyValues.put(MAXLoyaltyConstants.OTP_TIME_OUT_REQUEST_MESSAGE_ID, null);
			objLoyaltyValues.put(MAXLoyaltyConstants.OTP_REQUEST_TYPE_B, requestInfo.get(MAXLoyaltyConstants.REQUEST_TYPE_B).toString());
			//objLoyaltyValues.put("storeId",  "1642");
			objLoyaltyValues.put(MAXLoyaltyConstants.OTP_STORE_ID,  requestInfo.get(MAXLoyaltyConstants.STORE_ID).toString());
			objLoyaltyValues.put(MAXLoyaltyConstants.OTP_TILL_ID,  requestInfo.get(MAXLoyaltyConstants.TILL_ID).toString());
			objLoyaltyValues.put(MAXLoyaltyConstants.OTP_INVOICE_BUSINESS_DATE, invoiceDate);
			objLoyaltyValues.put(MAXLoyaltyConstants.OTP_INVOICE_NUMBER,  requestInfo.get(MAXLoyaltyConstants.INVOICE_NUMBER).toString());
			objLoyaltyValues.put(MAXLoyaltyConstants.OTP_TRAN_TOTAL_AMOUNT,  requestInfo.get(MAXLoyaltyConstants.TRAN_TOTAL_AMOUNT).toString());
			objLoyaltyValues.put(MAXLoyaltyConstants.OTP_SETTLE_TOTAL_AMOUNT,  requestInfo.get(MAXLoyaltyConstants.SETTLE_TOTAL_AMOUNT).toString());
			if(requestInfo.get(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER)!=null){
		    objLoyaltyValues.put(MAXLoyaltyConstants.OTP_LOYALTY_CARD_NUMBER, requestInfo.get(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER).toString());	
				//objLoyaltyValues.put("cardNumber", "2010606648");	
		    /**Changes end for rev 1.2**/	 
			}
			return objLoyaltyValues.toString();
	
	
	
		}
	
	public MAXCRMObj executePost(MAXCRMObj crmCRMObj) throws Exception
	{
		URL url;
		//int responseCode;
		HttpURLConnection connection = null;
		//com.akansha.httpconect.HttpURLConnection connection = null;
		HttpConnectionParams con = null;
		
		try 
		{
			// Create connection
			System.getProperties().setProperty("http.proxyHost","");
			System.getProperties().setProperty("http.proxyPort","");
			url 					= new URL(crmCRMObj.getCRM_URL());
			connection 				= (HttpURLConnection) url.openConnection();
			String urlParameters 	= crmCRMObj.getRequestMessage(); 
			logger.info("CRM executePost() :: url:"+url+" , urlParameters:"+urlParameters);
			String timeOut 			= Gateway.getProperty("application", "LoyaltytimeOutInMilliSeconds", "5000");
			System.getProperties().setProperty("sun.net.client.defaultConnectTimeout", timeOut);
			System.getProperties().setProperty("sun.net.client.defaultReadTimeout", timeOut);
			connection.setRequestMethod("POST");
	        connection.setInstanceFollowRedirects(false);
	        connection.setRequestProperty("Content-Type", "application/json"); 
			connection.setUseCaches(false); 
			connection.setDoInput(true);
			/*connection.setRequestProperty("sun.net.client.defaultConnectTimeout","5000");
			connection.setRequestProperty("sun.net.client.defaultReadTimeout", "5000");*/
			/*System.setProperty("sun.net.client.defaultConnectTimeout",
            "1000");
            System.setProperty("sun.net.client.defaultReadTimeout",
            "1000");*/
		//	connection.setAllowUserInteraction(false);   
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Length","" + Integer.toString(urlParameters.getBytes().length));
			try
			{
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			} catch (SocketException ex) 
	        {
	        	logger.error("Error in sending Request::" + ex.getMessage() + "");
				throw new SocketException("NetworkError");
			 }
			catch (Exception e) 
			{   //System.out.println("ThirdResponseError Problem in network");
				logger.error("Error in sending Request" + e.getMessage() + "");
			}
			
			// Get Response
			
			
			long before= System.currentTimeMillis();
			if(!maxCustomer.IsOtpValidation()){
			//System.out.println("Redeem call1 request time "+before);
			logger.error("Redeem call1 request time "+before);
			}
			else{
				//System.out.println("Redeem call2 request time "+before);
				logger.error("Redeem call2 request time "+before);
			}
			responseCode = connection.getResponseCode();
			long end =System.currentTimeMillis();
			if(!maxCustomer.IsOtpValidation()){
				//System.out.println("Redeem call1 response time "+end);
				
				logger.error("Redeem call1 response time "+end);
				}
				else{
					//System.out.println("Redeem call2 response time "+end);
					logger.error("Redeem call2 response time "+end);
				}
		
			long diffrence=before-end;
			if(!maxCustomer.IsOtpValidation()){
				//System.out.println("Redeem call1 response time diffrence"+diffrence);
				logger.error("Redeem call1 response time diffrence"+diffrence);
				}
				else{
					//System.out.println("Redeem call2 response time diffrence "+diffrence);
					logger.error("Redeem call2 response time diffrence "+diffrence);
				}
			
			//System.out.println("Time out Diffrence::" + diffrence + "");
			//logger.error("Time out Diffrence::" + diffrence + "");
			connection.getResponseMessage();
			
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
		//System.out.println("**** Redeemcall2 CRM Response: " +response.toString());
		//logger.info("**** Redeemcall2 CRM Response: " +response.toString());
		crmCRMObj.setConnResponseCode(String.valueOf(connection.getResponseCode()));
		crmCRMObj.setConnResponseMessage(response.toString());	

			
		} 
		catch (MalformedURLException e) 
		
		{   // System.out.println("FirstResponseError Problem in network");
			logger.error("Response not recieved::" + e.getMessage() + "");
			
		
		}
		
        catch (SocketTimeoutException e) 
		
		{     // System.out.println("SecondResponseError Problem in network");
		    // System.out.println("Redeem call1 response time "+responseCode);
			logger.error("Response not recieved::" + e.getMessage() + "");
			crmCRMObj.setConnResponseCode("700");
			crmCRMObj.setResponse("Timeout");
			
			
		
		}
		catch (Exception e) 
		{   //System.out.println("ThirdResponseError Problem in network");
			logger.error("Error in sending Request" + e.getMessage() + "");
			try 
			{
				responseCode = connection.getResponseCode();
				crmCRMObj.setResponseMessage(e.getMessage());			
			} 
			catch (IOException e1) 
			{    //System.out.println("ThirdResponseError Problem in network");
				logger.error("IO Exception Caught::" + e1.getMessage() + "");

				if(e.getMessage().equalsIgnoreCase("NetworkError"))
				{
					throw new SocketException("NetworkError");
				}
							
			}
			//return MAXCRMObj;
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
	
		return crmCRMObj;
	}
	//Rev 1.3 End
	
	protected void upadateFaliurerequest(BusIfc bus, String response, TenderCargo cargo, MAXLoyaltyDataTransaction loyaltyDataTransaction,MAXCRMObj crmobj)
	{
		HashMap newvalue = new HashMap();
		if(crmobj!=null){
			newvalue.put(MAXLoyaltyConstants.MESSAGE_ID, requestAttributes.get(MAXLoyaltyConstants.MESSAGE_ID));
			newvalue.put(MAXLoyaltyConstants.FLAG, MAXLoyaltyConstants.RESPONSE_FLAG);
			newvalue.put(MAXLoyaltyConstants.REQUEST_STATUS, MAXLoyaltyConstants.RESPONSE_RECEIVED);
			crmobj.getLoyaltypoint();
			newvalue.put(MAXLoyaltyConstants.RESPONSE_APPROVED_VALUE, Double.toString(crmobj.getLoyaltypoint()));
			newvalue.put(MAXLoyaltyConstants.RESPONSE_MESSAGE, crmobj.getMessage());
			newvalue.put(MAXLoyaltyConstants.RESPONSE, MAXLoyaltyConstants.FAIL);
			newvalue.put(MAXLoyaltyConstants.REQUEST_URL, requestAttributes.get(MAXLoyaltyConstants.REQUEST_URL));
			newvalue.put(MAXLoyaltyConstants.REQUEST_TYPE_B, requestAttributes.get(MAXLoyaltyConstants.REQUEST_TYPE_B));

			// Update WEB REQUEST TABLE in case of failure response 
			try {
				loyaltyDataTransaction.updateRequest(newvalue);
			} catch (DataException e) {
				logger.error("DataException::" + e.getMessage() + "");
			}
		}


	}
}

