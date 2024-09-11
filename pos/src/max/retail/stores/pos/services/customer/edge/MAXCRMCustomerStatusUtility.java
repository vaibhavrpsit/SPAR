/********************************************************************************
*   
*	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
*	Rev 1.1  10th May 2021	Vidhya Kommareddi 	POS BUG: All Edge Renewal bugs and changes after build delivery.
*	Rev	1.0 	22-Oct-2018		Jyoti Yadav		LS Edge Phase 2	
*
********************************************************************************/
package max.retail.stores.pos.services.customer.edge;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import max.retail.stores.domain.arts.MAXConfigParameterTransaction;
import max.retail.stores.domain.arts.MAXCustomerWriteDataTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXTICCustomerDataTransaction;
import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.loyalty.MAXTICCRMConstants;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.utility.MAXConfigParametersIfc;
import max.retail.stores.domain.utility.MAXGSTUtility;
import max.retail.stores.pos.services.customer.common.MAXCustomerCargo;
import max.retail.stores.pos.services.customer.common.MAXEdgeStatus;
import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import max.retail.stores.pos.services.sale.MAXSaleCargoIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXCRMCustomerStatusUtility {

	private static Logger logger = Logger
			.getLogger(MAXCRMCustomerStatusUtility.class);
	
	private static MAXCRMCustomerStatusUtility crmCustomerStatusUtility;
	//MAXConfigParametersIfc config = MAXGSTUtility.getConfigparameter();
	public static MAXCRMCustomerStatusUtility getInstance() {
		synchronized (MAXCRMCustomerStatusUtility.class) {
			if (crmCustomerStatusUtility == null) {
				crmCustomerStatusUtility = new MAXCRMCustomerStatusUtility();
			}
		}
		return crmCustomerStatusUtility;
	}
	
	public void lmrCustomerValidationStatus(BusIfc bus){
		HashMap requestAttributes = new HashMap();
		String response = "";
		requestAttributes = populateValidationHashMap(bus, requestAttributes);
		String urlParameters = createValidationURL(requestAttributes, bus);
		String URL = Gateway.getProperty("application", "searchEdgeURL", null);
		logger.info("URL of Search Edge API is " + URL);
		logger.info("Request of Search Edge API is " + urlParameters);
		// Call CRM API
		response = executeValidationPost(URL, urlParameters);
		//response = dummyValidationResponse(urlParameters);
		
		// Process response and navigate accordingly
		processValidationSuccessResponse(bus, response);
	}
	
	public HashMap populateValidationHashMap(BusIfc bus, HashMap requestAttributes) {
		// Get value from application.properties
		if(bus.getCargo() instanceof MAXCustomerCargo){
			MAXCustomerCargo cargo = (MAXCustomerCargo) bus.getCargo();	
			//Rev 1.1 start
			requestAttributes.put(MAXTICCRMConstants.TIC_STORE_ID, cargo.getRegister().getWorkstation().getStore().getStoreID().substring(1));
			//Rev 1.1 end
			if(((MAXCustomerCargo)cargo).getTicCustomer() != null ){
				if( ((MAXCustomerCargo)cargo).getTicCustomer().getTICCustomerID() != null 
						&& !((MAXCustomerCargo)cargo).getTicCustomer().getTICCustomerID().equalsIgnoreCase("null")
						&& ((MAXCustomerCargo)cargo).getTicCustomer().getTICMobileNumber() != null 
								&& !((MAXCustomerCargo)cargo).getTicCustomer().getTICMobileNumber().equalsIgnoreCase("null") ){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXCustomerCargo)cargo).getTicCustomer().getTICCustomerID());
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXCustomerCargo)cargo).getTicCustomer().getTICMobileNumber());
				}
				else if(((MAXCustomerCargo)cargo).getTicCustomer().getTICCustomerID() != null 
						&& !((MAXCustomerCargo)cargo).getTicCustomer().getTICCustomerID().equalsIgnoreCase("null")){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXCustomerCargo)cargo).getTicCustomer().getTICCustomerID()); //getLoyaltyCardNumber
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
				}else if(((MAXCustomerCargo)cargo).getTicCustomer().getTICMobileNumber() != null 
						&& !((MAXCustomerCargo)cargo).getTicCustomer().getTICMobileNumber().equalsIgnoreCase("null")){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXCustomerCargo)cargo).getTicCustomer().getTICMobileNumber());
				}else{
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
				}
			}else if(cargo.getTransaction() != null && cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc 
					&& ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() != null){
				if( ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() instanceof MAXTICCustomer 
						&& ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID() != null 
						&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID().equalsIgnoreCase("null")
						&& ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber() != null 
						&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber().equalsIgnoreCase("null") ){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID());
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber());
				}
				else if(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() instanceof MAXTICCustomer 
						&& ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID() != null 
						&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID().equalsIgnoreCase("null") ){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID());
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
				}else{
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
					if(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() instanceof MAXTICCustomer 
							&& ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber() != null 
							&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber().equalsIgnoreCase("null") ){
						requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber());
					}
				}
			}else if(cargo.getTransaction() != null && cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc 
					&& ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer() != null){
				if( ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer() instanceof MAXCustomer 
						&& ((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone() != null 
						&& ((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone().getPhoneNumber() != null
						&& !((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone().getPhoneNumber().equalsIgnoreCase("null") ){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone().getPhoneNumber());
				}else{
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
				}
			}else{
				requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
				requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
			}
		}else if(bus.getCargo() instanceof MAXCustomerMainCargo){
			MAXCustomerMainCargo cargo = (MAXCustomerMainCargo) bus.getCargo();	
			//Rev 1.1 start
			requestAttributes.put(MAXTICCRMConstants.TIC_STORE_ID, cargo.getRegister().getWorkstation().getStore().getStoreID().substring(1));
			//Rev 1.1 end
			if(((MAXCustomerMainCargo)cargo).getTICCustomer() != null){
				if( ((MAXCustomerMainCargo)cargo).getTICCustomer().getTICCustomerID() != null 
						&& !((MAXCustomerMainCargo)cargo).getTICCustomer().getTICCustomerID().equalsIgnoreCase("null")
						&& ((MAXCustomerMainCargo)cargo).getTICCustomer().getTICMobileNumber() != null 
								&& !((MAXCustomerMainCargo)cargo).getTICCustomer().getTICMobileNumber().equalsIgnoreCase("null") ){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXCustomerMainCargo)cargo).getTICCustomer().getTICCustomerID());
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXCustomerMainCargo)cargo).getTICCustomer().getTICMobileNumber());
				}
				else if(((MAXCustomerMainCargo)cargo).getTICCustomer().getTICCustomerID() != null 
						&& !((MAXCustomerMainCargo)cargo).getTICCustomer().getTICCustomerID().equalsIgnoreCase("null") ){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXCustomerMainCargo)cargo).getTICCustomer().getTICCustomerID());
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
				}else{
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXCustomerMainCargo)cargo).getTICCustomer().getTICMobileNumber());	
				}	
			}else if(cargo.getTransaction() != null && cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc 
					&& ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() != null){
				if( ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() instanceof MAXTICCustomer 
						&& ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID() != null 
						&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID().equalsIgnoreCase("null")
						&& ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber() != null 
						&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber().equalsIgnoreCase("null") ){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID());
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber());
				}
				else if(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() instanceof MAXTICCustomer 
						&& ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID() != null 
						&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID().equalsIgnoreCase("null") ){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID());
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
				}else{
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
					if(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() instanceof MAXTICCustomer 
							&& ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber() != null 
							&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber().equalsIgnoreCase("null") ){
						requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber());
					}/*else{
						requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer().getLoyaltyCustomerPhone().getPhoneNumber());	
					}*/
				}
			}else if(cargo.getTransaction() != null && cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc 
					&& ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer() != null){
				if( ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer() instanceof MAXCustomer 
						&& ((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone() != null 
						&& ((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone().getPhoneNumber() != null
						&& !((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone().getPhoneNumber().equalsIgnoreCase("null") ){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone().getPhoneNumber());
				}else{
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
			}
			}else{
				requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
				requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
			}
		}else{
			//Rev 1.1 start
			requestAttributes.put(MAXTICCRMConstants.TIC_STORE_ID, "");
			//Rev 1.1 end
			requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
			requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
		}
		// return HasMap with required values
		return requestAttributes;
	}

	@SuppressWarnings("unchecked")
	public String createValidationURL(HashMap requestInfo, BusIfc bus) {
		try {
			JSONObject inputJson = new JSONObject();
			inputJson.put(MAXTICCRMConstants.EDGE_CARD_NUMBER,
					requestInfo.get(MAXTICCRMConstants.EDGE_CARD_NUMBER).toString());
			inputJson.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER,
					requestInfo.get(MAXTICCRMConstants.EDGE_MOBILE_NUMBER).toString());
			//Rev 1.1 start
			if(requestInfo.get(MAXTICCRMConstants.TIC_STORE_ID) != null)
			{
			inputJson.put(MAXTICCRMConstants.TIC_STORE_ID,
					requestInfo.get(MAXTICCRMConstants.TIC_STORE_ID).toString());
			}
			//Rev 1.1 end
			return inputJson.toJSONString();
		} catch (Exception e) {
			Logger.getLogger(MAXCRMCustomerStatusUtility.class.getName()).log(null, e);
			return null;
		}
	}

	public String executeValidationPost(String targetURL, String urlParameters) {
		URL url;
		HttpURLConnection connection = null;
		logger.debug("targetURL:::" + targetURL);
		logger.debug("Request Body:::" + urlParameters);
		try {
			// Create connection
			url = new URL(targetURL);

			String timeOut = Gateway.getProperty("application",
					"LoyaltytimeOutInMilliSeconds", "5000");
			System.getProperties().setProperty(
					"sun.net.client.defaultConnectTimeout", timeOut);
			System.getProperties().setProperty(
					"sun.net.client.defaultReadTimeout", timeOut);
			connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
			connection.setRequestMethod("POST");
			
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");
			//connection.setRequestProperty("authkey", "959F1B0A537EDDCDD4998DEF360CED3AB6D323AEC795B915");
			
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			// Get Response
			logger.error("responseCode:::" + connection.getResponseCode());
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			logger.debug("Response Body:::" + response);
			return response.toString();
		} catch (Exception e) {
			logger.error("Error in sending CRM Customer Search Request"
					+ e.getMessage() + "");
			/*try {
				int responseCode = connection.getResponseCode();
			} catch (IOException e1) {
				logger.error("IO Exception Caught::" + e1.getMessage() + "");
				return "Timeout";
			}
			return "";*/
			return "Timeout";
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	
	public String dummyValidationResponse(String urlParameters){
		String response = "";
		JSONObject outputJson = new JSONObject();
		
		outputJson.put("result", true);
		outputJson.put("resultCode", "SUCCESS");
		//outputJson.put("message", "EDGE");
		outputJson.put("message", "NON-EDGE");
		outputJson.put("cardNumber", "761337");
		
		response = outputJson.toJSONString();
		return response;
	}
	
	public void processValidationSuccessResponse(BusIfc bus, String response) {
		MAXCustomerCargo cargo = null;
		MAXCustomerMainCargo mainCargo = null;
		if(bus.getCargo() instanceof MAXCustomerCargo){
			cargo = (MAXCustomerCargo) bus.getCargo();	
		}else if(bus.getCargo() instanceof MAXCustomerMainCargo){
			mainCargo = (MAXCustomerMainCargo) bus.getCargo();	
		}
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		String result = "";
		String resultCode = "";
		String message = "";
		String trimString = response.toString().trim();
		logger.info("Response of Search Edge API is " + trimString);
		JSONParser parser = new JSONParser();
		if(!response.equalsIgnoreCase("Timeout")){
		try {
			JSONObject jsonObject = (JSONObject) parser.parse(trimString);

			if (jsonObject.get("result") != null)
				result = (String) jsonObject.get("result").toString();
			if (jsonObject.get("resultCode") != null)
				resultCode = (String) jsonObject.get("resultCode");
			if (jsonObject.get("message") != null)
				message = (String) jsonObject.get("message");
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
		if (response.equalsIgnoreCase("Timeout")) {
			//if(MAXGSTUtility.edgePreviewSaleEnabled(cargo.getRegister().getBusinessDate(), config)){
				//cargo.setDataExceptionErrorCode(DataException.NO_DATA);
			//}
			displayConnectionErrorDialogMessage(bus, "CRM_CONNECTIVITY_ERROR", "EdgeFailure");
			cargo.setDataExceptionErrorCode(DataException.NO_DATA);
			return;
		} else {
			if (result.trim().equalsIgnoreCase("true") && resultCode.trim().equalsIgnoreCase("SUCCESS")) {
				if(message.trim().equalsIgnoreCase("NON-EDGE")){
					displayErrorDialogMessage(bus, "NOT_EDGE_CUSTOMER", "CrmCustomerLinked");
				}else if(message.trim().equalsIgnoreCase("EDGE")){
					if(cargo != null){
						if(cargo.getCustomer() != null && cargo.getCustomer() instanceof MAXCustomer){
						((MAXCustomer)cargo.getCustomer()).setCustomerTier(message.trim());
						}
					}else if(mainCargo != null){
						if(mainCargo.getCustomer() != null && mainCargo.getCustomer() instanceof MAXCustomer){
							((MAXCustomer)mainCargo.getCustomer()).setCustomerTier(message.trim());	
						}
					}
					bus.mail(new Letter("CrmCustomerLinked"), BusIfc.CURRENT);
				}else{
					bus.mail(new Letter("CrmCustomerLinked"), BusIfc.CURRENT);
				}
			}else{
				//if(MAXGSTUtility.edgePreviewSaleEnabled(cargo.getRegister().getBusinessDate(), config)){
					//cargo.setDataExceptionErrorCode(DataException.NO_DATA);
				//}
				displayCRMResponseError(bus, message.trim(), "CRM_RESPONSE_ERROR", "EdgeFailure");
				return;
			}
		}
	}
	
	protected void displayErrorDialogMessage(BusIfc bus,String resourceId,String letterName) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		DialogBeanModel beanModel = new DialogBeanModel();
		//Rev 1.1 start
		MAXConfigParametersIfc config = getConfigparameter();
		String name = "EDGE";
		if(config != null) {
			name = config.getEdgeName();
		}
		String[] msg = new String[1];
		msg[0] = name;
		beanModel.setArgs(msg);
		//Rev 1.1 end
		beanModel.setResourceID(resourceId);
		beanModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, letterName);
    		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
	}
	
	protected void displayConnectionErrorDialogMessage(BusIfc bus,String resourceId,String letterName) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		DialogBeanModel beanModel = new DialogBeanModel();
		beanModel.setResourceID(resourceId);
		beanModel.setType(DialogScreensIfc.ERROR);
		beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, letterName);
    		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
	}
	
	protected void displayCRMResponseError(BusIfc bus, String errorMessage,
			String resourceId, String letterName) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		DialogBeanModel beanModel = new DialogBeanModel();
		String[] messgArray = new String[1];
		messgArray[0] = errorMessage;
		beanModel.setArgs(messgArray);
		beanModel.setResourceID(resourceId);
		beanModel.setType(DialogScreensIfc.ERROR);
		beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, letterName);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
	}
	
	public HashMap populateUpdationHashMap(MAXSaleCargoIfc cargo, HashMap requestAttributes, MAXConfigParametersIfc config) {
		if(cargo.getTransaction() != null && cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc 
				&& ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() != null 
				&& ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() instanceof MAXTICCustomer){
			if(((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID() != null 
					&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID().equalsIgnoreCase("null")
					&& ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber() != null 
					&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber().equalsIgnoreCase("null") ){
				requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID());
				requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber());
			}
			else if(((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID() != null 
					&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID().equalsIgnoreCase("null") ){
				requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID());
				requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
			}else if(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() instanceof MAXTICCustomer 
					&& ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber() != null 
					&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber().equalsIgnoreCase("null") ){
				requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
				requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber());
			}else{
				requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
				requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
			}
		}else if(cargo.getTransaction() != null && cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc 
				&& ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer() != null 
				&& ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer() instanceof MAXCustomer){
			if(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer() instanceof MAXCustomer
					&& ((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone() != null
					&& ((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone().getPhoneNumber() != null 
					&& !((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone().getPhoneNumber().equalsIgnoreCase("null") ){
				requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
				requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone().getPhoneNumber());
			}else{
				requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
				requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
			}
		}
		requestAttributes.put(MAXTICCRMConstants.STORECODE, cargo.getRegister().getWorkstation().getStore().getStoreID());
		requestAttributes.put(MAXTICCRMConstants.EDGE_OU_CODE, config.getOraganizationUnit());

		// return HasMap with required values
		return requestAttributes;
	}

	public String createUpdationURL(HashMap requestInfo, BusIfc bus) {
		String urlParameters;
		try {
			JSONObject inputJson = new JSONObject();
			inputJson.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, requestInfo.get(MAXTICCRMConstants.EDGE_CARD_NUMBER).toString());
			inputJson.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, requestInfo.get(MAXTICCRMConstants.EDGE_MOBILE_NUMBER).toString());
			inputJson.put(MAXTICCRMConstants.STORECODE, requestInfo.get(MAXTICCRMConstants.STORECODE).toString());
			inputJson.put(MAXTICCRMConstants.EDGE_OU_CODE, requestInfo.get(MAXTICCRMConstants.EDGE_OU_CODE).toString());
			return inputJson.toJSONString();
		} catch (Exception e) {
			Logger.getLogger(MAXEnrolEdgeCustomerSite.class.getName()).log(null, e);
			return null;
		}
	}

	public String dummyUpdationResponse(String urlParameters){
		String response = "";
		JSONObject outputJson = new JSONObject();
		
		outputJson.put("result", true);
		outputJson.put("resultCode", "SUCCESS");
		outputJson.put("message", "Enrolemt Successful.");
		outputJson.put("cardNumber", "761337");
		
		response = outputJson.toJSONString();
		return response;
	}
	
	public void processUpdationSuccessResponse(BusIfc bus, String response) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		String result = "";
		String resultCode = "";
		String message = "";
		MAXCustomer customer = null;
		if(bus.getCargo() instanceof MAXSaleCargoIfc && ((MAXSaleCargoIfc)bus.getCargo()).getTransaction() != null 
				&& ((MAXSaleCargoIfc)bus.getCargo()).getTransaction() instanceof MAXSaleReturnTransactionIfc){
			if(((MAXSaleReturnTransactionIfc)((MAXSaleCargoIfc)bus.getCargo()).getTransaction()).getMAXTICCustomer() != null 
					&& ((MAXSaleReturnTransactionIfc)((MAXSaleCargoIfc)bus.getCargo()).getTransaction()).getMAXTICCustomer() instanceof MAXTICCustomer){
				customer = (MAXTICCustomer) ((MAXSaleReturnTransactionIfc)((MAXSaleCargoIfc)bus.getCargo()).getTransaction()).getMAXTICCustomer(); 
			}else if(((MAXSaleReturnTransactionIfc)((MAXSaleCargoIfc)bus.getCargo()).getTransaction()).getCustomer() != null 
					&& ((MAXSaleReturnTransactionIfc)((MAXSaleCargoIfc)bus.getCargo()).getTransaction()).getCustomer() instanceof MAXCustomer){
				customer = (MAXCustomer) ((MAXSaleReturnTransactionIfc)((MAXSaleCargoIfc)bus.getCargo()).getTransaction()).getCustomer(); 
			}
		}
		String trimString = response.toString().trim();
		logger.info("Response of Enrol Edge API is " + trimString);
		JSONParser parser = new JSONParser();
		try {
			JSONObject jsonObject = (JSONObject) parser.parse(trimString);

			if (jsonObject.get("result") != null)
				result = (String) jsonObject.get("result").toString();
			if (jsonObject.get("resultCode") != null)
				resultCode = (String) jsonObject.get("resultCode");
			if (jsonObject.get("message") != null)
				message = (String) jsonObject.get("message");
		} catch (Exception e) {
			
		}
		if (response.equalsIgnoreCase("Timeout")) {
			displayConnectionErrorDialogMessage(bus, "CRM_CONNECTIVITY_ERROR", "Failure");
			return;
		} else {
			if (result.trim().equalsIgnoreCase("true") && resultCode.trim().equalsIgnoreCase("SUCCESS")) {
				/*if(message.trim().contains("Successful")){*/
					//Update existing customer record with EDGE status
				MAXTICCustomerDataTransaction custTransaction = null;
					custTransaction = /*(MAXCustomerWriteDataTransaction) DataTransactionFactory
							.create(DataTransactionKeys.CUSTOMER_WRITE_DATA_TRANSACTION);*/
							(MAXTICCustomerDataTransaction) DataTransactionFactory
							.create(MAXDataTransactionKeys.TIC_CUSTOMER_DATA_TRANSACTION);
					try {
						if(customer != null){
							custTransaction.updateCustomerTier(customer);	
						}
					} catch (DataException e1) {
						//e1.printStackTrace();
					}
				/*}else{
					//Complete the transaction
				}*/
			}else {
				// in other cases
				displayCRMResponseError(bus, message.trim(), "CRM_RESPONSE_ERROR", "Failure");
				return;
			}
		}
	}
	
	public HashMap populateValidationHashMapForEdgeStatus(BusIfc bus, HashMap requestAttributes) {
		// Get value from application.properties
		String storeId = Gateway.getProperty("application", "StoreID", "");
		int storeCode = Integer.parseInt(storeId.trim());
		if(bus.getCargo() instanceof MAXCustomerCargo){
			MAXCustomerCargo cargo = (MAXCustomerCargo) bus.getCargo();	
			if(((MAXCustomerCargo)cargo).getTicCustomer() != null ){
				if( ((MAXCustomerCargo)cargo).getTicCustomer().getTICCustomerID() != null 
						&& !((MAXCustomerCargo)cargo).getTicCustomer().getTICCustomerID().equalsIgnoreCase("null")
						&& ((MAXCustomerCargo)cargo).getTicCustomer().getTICMobileNumber() != null 
								&& !((MAXCustomerCargo)cargo).getTicCustomer().getTICMobileNumber().equalsIgnoreCase("null") ){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXCustomerCargo)cargo).getTicCustomer().getTICCustomerID());
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXCustomerCargo)cargo).getTicCustomer().getTICMobileNumber());
				}
				else if(((MAXCustomerCargo)cargo).getTicCustomer().getTICCustomerID() != null 
						&& !((MAXCustomerCargo)cargo).getTicCustomer().getTICCustomerID().equalsIgnoreCase("null")){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXCustomerCargo)cargo).getTicCustomer().getTICCustomerID()); //getLoyaltyCardNumber
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
				}else if(((MAXCustomerCargo)cargo).getTicCustomer().getTICMobileNumber() != null 
						&& !((MAXCustomerCargo)cargo).getTicCustomer().getTICMobileNumber().equalsIgnoreCase("null")){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXCustomerCargo)cargo).getTicCustomer().getTICMobileNumber());
				}else{
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
				}
			}
		}else	 if(bus.getCargo() instanceof MAXCustomerMainCargo){
			MAXCustomerMainCargo cargo = (MAXCustomerMainCargo) bus.getCargo();	
			if(((MAXCustomerMainCargo)cargo).getTICCustomer() != null){
				if( ((MAXCustomerMainCargo)cargo).getTICCustomer().getTICCustomerID() != null 
						&& !((MAXCustomerMainCargo)cargo).getTICCustomer().getTICCustomerID().equalsIgnoreCase("null")
						&& ((MAXCustomerMainCargo)cargo).getTICCustomer().getTICMobileNumber() != null 
								&& !((MAXCustomerMainCargo)cargo).getTICCustomer().getTICMobileNumber().equalsIgnoreCase("null") ){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXCustomerMainCargo)cargo).getTICCustomer().getTICCustomerID());
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXCustomerMainCargo)cargo).getTICCustomer().getTICMobileNumber());
				}
				else if(((MAXCustomerMainCargo)cargo).getTICCustomer().getTICCustomerID() != null 
						&& !((MAXCustomerMainCargo)cargo).getTICCustomer().getTICCustomerID().equalsIgnoreCase("null") ){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXCustomerMainCargo)cargo).getTICCustomer().getTICCustomerID());
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
				}else{
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXCustomerMainCargo)cargo).getTICCustomer().getTICMobileNumber());	
				}	
			}else if(cargo.getTransaction() != null && cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc 
					&& ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() != null){
				if( ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() instanceof MAXTICCustomer 
						&& ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID() != null 
						&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID().equalsIgnoreCase("null")
						&& ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber() != null 
						&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber().equalsIgnoreCase("null") ){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID());
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber());
				}
				else if(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() instanceof MAXTICCustomer 
						&& ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID() != null 
						&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID().equalsIgnoreCase("null") ){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID());
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
				}else{
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
					if(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() instanceof MAXTICCustomer 
							&& ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber() != null 
							&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber().equalsIgnoreCase("null") ){
						requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber());
					}/*else{
						requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer().getLoyaltyCustomerPhone().getPhoneNumber());	
					}*/
				}
			}else if(cargo.getTransaction() != null && cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc 
					&& ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer() != null){
				if( ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer() instanceof MAXCustomer 
						&& ((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone() != null 
						&& ((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone().getPhoneNumber() != null
						&& !((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone().getPhoneNumber().equalsIgnoreCase("null") ){
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone().getPhoneNumber());
				}else{
					requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
					requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
			}
			}else{
				requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
				requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
			}
		}else{
			requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
			requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
		}
		
		//9000132213
		//requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "9000132213");
		requestAttributes.put(MAXTICCRMConstants.STORECODE, storeCode);
		return requestAttributes;
	}
	@SuppressWarnings("unchecked")
	public String createValidationURLForEdgeStatus(HashMap requestInfo, BusIfc bus) {
		try {
			JSONObject inputJson = new JSONObject();
			inputJson.put(MAXTICCRMConstants.STORECODE,
					requestInfo.get(MAXTICCRMConstants.STORECODE).toString());
			inputJson.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER,
					requestInfo.get(MAXTICCRMConstants.EDGE_MOBILE_NUMBER).toString());

			inputJson.put(MAXTICCRMConstants.EDGE_CARD_NUMBER,
					requestInfo.get(MAXTICCRMConstants.EDGE_CARD_NUMBER).toString());
			return inputJson.toJSONString();
		} catch (Exception e) {
			Logger.getLogger(MAXCRMCustomerStatusUtility.class.getName()).log(null, e);
			return null;
		}
	}
	
	public MAXEdgeStatus validateResp(String response) {
		MAXEdgeStatus status = new MAXEdgeStatus();
		
		if(response !=null && !response.equals("")) {
			String trimString = response.toString().trim();
			JSONParser parser = new JSONParser();
			try {
				JSONObject jsonObject = (JSONObject) parser.parse(trimString);
				if (jsonObject.get("result") != null) {
					status.setResult((String) jsonObject.get("result").toString());
				}
				if (jsonObject.get("resultCode") != null) {
					status.setResultCode((String) jsonObject.get("resultCode"));
				}
				if (jsonObject.get("message") != null)
					status.setMessage((String) jsonObject.get("message"));
				if (trimString.contains("cardNum") && jsonObject.get("cardNum") != null)
					status.setCardNum((String) jsonObject.get("cardNum"));
				if (trimString.contains("sbiVariant") &&  jsonObject.get("sbiVariant") != null)
					status.setSbiVariant((String) jsonObject.get("sbiVariant"));
				if(trimString.contains("lsEdge") && jsonObject.get("lsEdge") != null) {
					status.setLsEdge((String) jsonObject.get("lsEdge"));
				}
				//Rev 1.1 start--property names have been changes from CRM
				/*if(trimString.contains("maxEdge") && jsonObject.get("maxEdge") != null) {
					status.setMaxEdge((String) jsonObject.get("maxEdge"));
				}
				if(trimString.contains("maxBlue") && jsonObject.get("maxBlue") != null) {
					status.setMaxBlue((String) jsonObject.get("maxBlue"));
				}*/
				if(trimString.contains("maxElite") && jsonObject.get("maxElite") != null) {
					status.setMaxEdge((String) jsonObject.get("maxElite"));
				}
				if(trimString.contains("salesPitch") && jsonObject.get("salesPitch") != null) {
					status.setMaxBlue((String) jsonObject.get("salesPitch"));
				}				
				//Rev 1.1 end
				if(trimString.contains("hcEdge") && jsonObject.get("hcEdge") != null) {
					status.setHcEdge((String) jsonObject.get("hcEdge"));
				}
				if(trimString.contains("sbiCard") && jsonObject.get("sbiCard") != null) {
					status.setSbiCard((String) jsonObject.get("sbiCard"));
				}
				if(trimString.contains("enrollFlag") && jsonObject.get("enrollFlag") != null) {
					String enrollEdge = (String) jsonObject.get("enrollFlag");
					if(enrollEdge != null && !enrollEdge.equals("") && enrollEdge.equalsIgnoreCase("Y")) {
						status.setEnrollFlag(true);
					}
				}
				
			} catch (Exception e) {
				status.setMessage("Unable to parse edge validation response");
			}
		}
		
		return status;
		
	}
	
	public HashMap populateUpdationHashMapForEdgeReversal(MAXSaleCargoIfc cargo, HashMap requestAttributes, MAXConfigParametersIfc config) {
		String storeId = Gateway.getProperty("application", "StoreID", "");
		int storeCode = Integer.parseInt(storeId.trim());
		if(cargo.getTransaction() != null && cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc 
				&& ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() != null 
				&& ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() instanceof MAXTICCustomer){
			if(((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID() != null 
					&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID().equalsIgnoreCase("null")
					&& ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber() != null 
					&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber().equalsIgnoreCase("null") ){
				requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID());
				requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber());
			}
			else if(((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID() != null 
					&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID().equalsIgnoreCase("null") ){
				requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICCustomerID());
				requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
			}else if(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer() instanceof MAXTICCustomer 
					&& ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber() != null 
					&& !((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber().equalsIgnoreCase("null") ){
				requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
				requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXTICCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getMAXTICCustomer()).getTICMobileNumber());
			}else{
				requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
				requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
			}
		}else if(cargo.getTransaction() != null && cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc 
				&& ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer() != null 
				&& ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer() instanceof MAXCustomer){
			if(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer() instanceof MAXCustomer
					&& ((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone() != null
					&& ((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone().getPhoneNumber() != null 
					&& !((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone().getPhoneNumber().equalsIgnoreCase("null") ){
				requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
				requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, ((MAXCustomer)((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getCustomer()).getLoyaltyCustomerPhone().getPhoneNumber());
			}else{
				requestAttributes.put(MAXTICCRMConstants.EDGE_CARD_NUMBER, "");
				requestAttributes.put(MAXTICCRMConstants.EDGE_MOBILE_NUMBER, "");
			}
		}
		requestAttributes.put(MAXTICCRMConstants.STORECODE, storeCode);

		// return HasMap with required values
		return requestAttributes;
	}
	
	//Rev 1.1 start
	private MAXConfigParametersIfc getConfigparameter() {

		MAXConfigParameterTransaction configTransaction = new MAXConfigParameterTransaction();
		MAXConfigParametersIfc configParameters = null;
		configTransaction = (MAXConfigParameterTransaction) DataTransactionFactory
				.create(MAXDataTransactionKeys.CONFIG_PARAMETER_TRANSACTION);

		try {
			configParameters = configTransaction.selectConfigParameters();
		} catch (DataException e1) {
			e1.printStackTrace();
		}
		return configParameters;
	}
	//Rev 1.1 end
}
