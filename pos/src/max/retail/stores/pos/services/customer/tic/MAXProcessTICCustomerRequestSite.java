/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.2  05th May 2020		Karni Singh 	POS REQ: Register CRM customer with OTP
 *  Rev 1.1  07/Sept/2018       Bhanu Priya     Changes for code merging CR
 *  Rev 1.0  20/Aug/2015        Mohd Arif       Changes for capillary coupon prompt not coming when add tic customer.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.customer.tic;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.factory.MAXDomainObjectFactory;
import max.retail.stores.domain.loyalty.MAXTICCRMConstants;
import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import max.retail.stores.pos.services.tender.loyaltypoints.MAXWebRequestResponseInfoAisle;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.Gender;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXProcessTICCustomerRequestSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int responseCode;
	HashMap requestAttributes;

	private static Logger logger = Logger
	.getLogger(max.retail.stores.pos.services.customer.tic.MAXProcessTICCustomerRequestSite.class);

	public void arrive(BusIfc bus) {

		MAXCustomerMainCargo cargo = (MAXCustomerMainCargo) bus.getCargo();

		MAXTICCustomerIfc ticCustomer = cargo.getTICCustomer(); 
		requestAttributes = new HashMap();
		requestAttributes = populateHashMap(cargo, requestAttributes);

		String urlParameters = createURL(requestAttributes, bus);
		//String URL = Gateway.getProperty("application","LoyaltyTicWebServiceURL", null);
		//showProcessCRMRequestScreen(bus);
		//String response = executePost(URL, urlParameters);

		//Rev 1.3 start
			String URL	= "";
			String response = "";
			cargo.setOtpRetries(0);
			if(cargo.isCRMEnrolmentOTPValidated())
			{
			URL = Gateway.getProperty("application","LoyaltyTicWebServiceURL", null);
			showProcessCRMRequestScreen(bus);
			response = executePost(URL, urlParameters);
			cargo.setCRMEnrolmentOTPValidated(false);
			cargo.setCrmEnrolmentOTP(null);
			}
			else
			{
				
				URL = Gateway.getProperty("application","LoyaltyTicGenerateOTPURL", null);		
				try {
				      JSONObject obj = new JSONObject();
				      obj.put("mobileNumber", new Long(requestAttributes.get(MAXTICCRMConstants.MOBILE_NUMBER).toString()));				      
				      obj.put("storeCode", requestAttributes.get(MAXTICCRMConstants.TIC_STORE_ID).toString());
				     // obj.put("storeCode", "1342");
				      String urlParams = obj.toJSONString();
				      //System.out.println("Request to CRM: "+urlParams);
				      logger.info("Request to CRM: "+urlParams);
				      response = executePost(URL,urlParams);
				      String readJsonString = response.toString().trim();
				     // System.out.println("Response from CRM: "+readJsonString);
				      JSONParser parser=new JSONParser();
				      JSONObject jsonObject=(JSONObject)parser.parse(readJsonString);				       
				      String resultCode ="";
				      String message="";
				      String otp ="";
				      if(jsonObject.get("resultCode")!=null)
				    	  resultCode =(String)jsonObject.get("resultCode");
				      if(jsonObject.get("message")!=null)
				    	  message =(String)jsonObject.get("message");
				      if(jsonObject.get("otp")!=null)
				    	  otp =(String)jsonObject.get("otp");				      
				      logger.info("Response from CRM:- resultCode:"+resultCode+" message:"+message+" OTP:"+(otp.equals("")?false:true));
				      if(resultCode.equals("SUCCESS") && message.equals("SUCCESS") && !otp.equals(""))
				      {
				    	  cargo.setCrmEnrolmentOTP(otp);
				    	  bus.mail("OTPValidationRequired");
				    	  return;
				    	  
				      }
				      else if((resultCode.equals("SUCCESS") && message.equals("SUCCESS") && otp.equals("")) ||(resultCode.equals("9") && message.toUpperCase().contains("ALREADY REGISTERED")))
				      {
				    	  URL = Gateway.getProperty("application","LoyaltyTicWebServiceURL", null);
							showProcessCRMRequestScreen(bus);
							response = executePost(URL, urlParameters);
							cargo.setCRMEnrolmentOTPValidated(false);
				      }		
				      else if (!resultCode.equals("SUCCESS"))
				      {
				    	  logger.error("An following error was received from CRM: "+message);
				    	  displayErrorDialogMessg(bus,"Internal Error","CRMInternalError","Ok"); 
				      }
				}
				      catch(Exception e)
				      {
				    	  logger.error("An exception occurred during customer enrolment with CRM: "+e.getMessage());
				      }					
			}
			
			//Rev 1.2 end
	//	System.out.println("response:::"+response);
		/*   
	    if(response==null || response.trim().equalsIgnoreCase("")){
	    	response="{\"cardNum\":\"8000121\",\"firstName\":\"Akhilesh\",\"lastName\":\"kumar\",\"requestId\":\"5465465456654564\",\"responseId\":\"879878954545\",\"errorCode\":\"1\",\"errorDesc\":\"mobile no exists\",\"responseStatus\":\"SUCCESS\"}";
	    }*/
		processSuccessResponse( bus,  response,  cargo);


	}

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
			connection.setRequestMethod("POST");

			connection.setRequestProperty("Content-Type", "application/json");

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
			//System.out.println("responseCode:::"+responseCode);

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
			e.printStackTrace();
			logger.error("Error in sending Request" + e.getMessage() + "");
			try {
				responseCode = connection.getResponseCode();
			} catch (IOException e1) {
				e1.printStackTrace();
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




	protected String createURL(HashMap requestInfo, BusIfc bus) {
		String urlParameters;
		try {



			JSONObject obj = new JSONObject();

			obj.put("mobileNum", new Long(requestInfo.get(MAXTICCRMConstants.MOBILE_NUMBER).toString()));
			obj.put("firstName", requestInfo.get(MAXTICCRMConstants.FIRST_NAME).toString());
			obj.put("lastName", requestInfo.get(MAXTICCRMConstants.LAST_NAME).toString());

			if(requestInfo.get(MAXTICCRMConstants.EMAIL_ID)!=null && !requestInfo.get(MAXTICCRMConstants.EMAIL_ID).toString().equalsIgnoreCase("")){
				obj.put("emailId", requestInfo.get(MAXTICCRMConstants.EMAIL_ID).toString());
			}

			if(requestInfo.get(MAXTICCRMConstants.PIN_CODE)!=null && !requestInfo.get(MAXTICCRMConstants.PIN_CODE).toString().equalsIgnoreCase("")){
				obj.put("pinCode", requestInfo.get(MAXTICCRMConstants.PIN_CODE).toString());
			}

			if(requestInfo.get(MAXTICCRMConstants.DOB)!=null && !requestInfo.get(MAXTICCRMConstants.DOB).toString().equalsIgnoreCase("")){
				obj.put("dateOfBirth", requestInfo.get(MAXTICCRMConstants.DOB).toString());
			}

			if(requestInfo.get(MAXTICCRMConstants.GENDER) !=null  && !requestInfo.get(MAXTICCRMConstants.GENDER).toString().equalsIgnoreCase("")  && (!requestInfo.get(MAXTICCRMConstants.GENDER).toString().equalsIgnoreCase(new Integer(Gender.GENDER_UNSPECIFIED).toString()) && !requestInfo.get(MAXTICCRMConstants.GENDER).toString().equalsIgnoreCase("Unspecified"))){
				obj.put("gender", requestInfo.get(MAXTICCRMConstants.GENDER).toString());
			}
			obj.put("storeCode", requestInfo.get(MAXTICCRMConstants.TIC_STORE_ID).toString());
			obj.put("invoiceNumber", requestInfo.get(MAXTICCRMConstants.TIC_INVOICE_NUM).toString());
			obj.put("invoiceDate", requestInfo.get(MAXTICCRMConstants.TIC_INVOICE_DATE).toString());
			obj.put("invoiceAmount",new Double( requestInfo.get(MAXTICCRMConstants.INVOICE_AMOUNT).toString()));
			obj.put("tillId", requestInfo.get(MAXTICCRMConstants.TIC_TILL_ID).toString());
			obj.put("cashierId", requestInfo.get(MAXTICCRMConstants.CASHIER_ID).toString());
			obj.put("requestSource", requestInfo.get(MAXTICCRMConstants.REQUEST_SOURCE).toString());
			obj.put("requestId", requestInfo.get(MAXTICCRMConstants.REQUEST_ID).toString());
			obj.put("customerType", requestInfo.get(MAXTICCRMConstants.CUSTOMER_TYPE).toString());



			return obj.toJSONString();
		} catch (Exception e) {
			Logger.getLogger(MAXWebRequestResponseInfoAisle.class.getName()).log(null, e);
			return null;
		}
	}



	protected void processSuccessResponse(BusIfc bus, String response, MAXCustomerMainCargo cargo) {
		HashMap newvalue = new HashMap();
		//HashMap tenderAttributes = cargo.getTenderAttributes();
		MAXTICCustomerIfc customerIfc=cargo.getTICCustomer();
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		String cardNum ="";
		String firstName ="";
		String lastName ="";
		String requestId ="";
		String responseId ="";
		String errorCode ="";
		String errorDesc ="";
		String responseStatus ="";


		String trimString = response.toString().trim();

		JSONParser parser=new JSONParser();
		try{

			JSONObject jsonObject=(JSONObject)parser.parse(trimString);

			if(jsonObject.get("cardNum")!=null)
				cardNum=(String)jsonObject.get("cardNum");
			if(jsonObject.get("firstName")!=null)
				firstName=(String)jsonObject.get("firstName");
			if(jsonObject.get("lastName")!=null)
				lastName=(String)jsonObject.get("lastName");
			if(jsonObject.get("requestId")!=null)
				requestId=(String)jsonObject.get("requestId");
			if(jsonObject.get("responseId")!=null)
				responseId=(String)jsonObject.get("responseId");
			if(jsonObject.get("errorCode")!=null)
				errorCode=(String)jsonObject.get("errorCode");
			if(jsonObject.get("errorDesc")!=null)
				errorDesc=(String)jsonObject.get("errorDesc");
			if(jsonObject.get("responseStatus")!=null)
				responseStatus=(String)jsonObject.get("responseStatus");






		}catch(Exception e){

		}




		if(response.equalsIgnoreCase("Timeout")){
			displayErrorDialog(bus);
			cargo.setTICCustomer(null);
			///for updating the MAXTIcCustomer information in the customer object


			SaleReturnTransaction returnTransaction=null;
			MAXCustomer customer=null;
			if(cargo.getTransaction()!=null ){
				returnTransaction =(SaleReturnTransaction)cargo.getTransaction();

				if(returnTransaction.getCustomer()!=null){
					customer=(MAXCustomer)returnTransaction.getCustomer();
					customer.setMAXTICCustomer(null);
				}


				returnTransaction.setCustomer(customer);

				cargo.setTransaction(returnTransaction); 

			}


			return;
		}
		else{

			POSUIManagerIfc ui = (POSUIManagerIfc) bus
			.getManager(UIManagerIfc.TYPE);



			if(responseStatus.trim().equalsIgnoreCase("SUCCESS")){
				//in the successs
				if(errorCode==null || errorCode.trim().equalsIgnoreCase("") || errorCode.trim().equalsIgnoreCase("NULL")){
					//in the  case of the success
					DataInputBeanModel beanModel=new DataInputBeanModel();
					beanModel.setValue("CustomerIDLabel",cardNum );
					customerIfc.setTICCustomerID(cardNum);
					customerIfc.setTICFirstName(firstName);
					customerIfc.setTICLastName(lastName);
					customerIfc.setExistingCustomer(new Boolean("FALSE"));
					/*Rev 1.0 start*/
					customerIfc.setCustomerType("T");
					customerIfc.setCustomerID(cardNum);
					/*Rev 1.0 end*/
					cargo.setTICCustomer(customerIfc);
					

					///for updating the MAXTIcCustomer information in the customer object

					MAXCustomer customer=null;
					SaleReturnTransaction returnTransaction=null;
					if(cargo.getTransaction()!=null ){
						returnTransaction =(SaleReturnTransaction)cargo.getTransaction();
						if(returnTransaction!=null && returnTransaction.getCustomer()!=null  && returnTransaction.getCustomer() instanceof MAXCustomer){
							customer=(MAXCustomer) returnTransaction.getCustomer();
							customer.setMAXTICCustomer(customerIfc);

						} else{
							// MAXDomainObjectFactory
							MAXDomainObjectFactory domainFactory = (MAXDomainObjectFactory) DomainGateway.getFactory();
							customer=(MAXCustomer)domainFactory.getCustomerInstance();
							customer.setMAXTICCustomer(customerIfc);
						}
						returnTransaction.setCustomer(customer);
						cargo.setTransaction(returnTransaction); 

					}


					ui.showScreen(MAXPOSUIManagerIfc.CRM_RESPONSE_NEW, beanModel);


				}else if(errorCode.trim().equalsIgnoreCase("1")){

					// in the case of the success with existing data
					DataInputBeanModel beanModel=new DataInputBeanModel();
					beanModel.setValue("CustomerIDLabel", cardNum);
					beanModel.setValue("CustomerNameLabel", firstName);



					customerIfc.setTICCustomerID(cardNum);
					customerIfc.setTICFirstName(firstName);
					customerIfc.setTICLastName(lastName);
					customerIfc.setExistingCustomer(new Boolean("TRUE"));
					/*Rev 1.0 start*/
					customerIfc.setCustomerType("T");
					customerIfc.setCustomerID(cardNum);
					/*Rev 1.0 end*/
					cargo.setTICCustomer(customerIfc);


					///for updating the MAXTIcCustomer information in the customer object

					MAXCustomer customer=null;
					SaleReturnTransaction returnTransaction=null;
					if(cargo.getTransaction()!=null ){
						returnTransaction =(SaleReturnTransaction)cargo.getTransaction();
						if(returnTransaction!=null && returnTransaction.getCustomer()!=null  && returnTransaction.getCustomer() instanceof MAXCustomer){
							customer=(MAXCustomer) returnTransaction.getCustomer();
							customer.setMAXTICCustomer(customerIfc);

						} else{
							// MAXDomainObjectFactory
							MAXDomainObjectFactory domainFactory = (MAXDomainObjectFactory) DomainGateway.getFactory();
							customer=(MAXCustomer)domainFactory.getCustomerInstance();
							customer.setMAXTICCustomer(customerIfc);
						}
						returnTransaction.setCustomer(customer);
						cargo.setTransaction(returnTransaction); 

					}
					ui.showScreen(MAXPOSUIManagerIfc.CRM_RESPONSE_EXISTING, beanModel);

				}else{
					displayErrorDialogMessg(bus,"Internal Error","CRMInternalError","Ok");
				}


			}else if(responseStatus.trim().equalsIgnoreCase("FAIL")){
				//in the fail

				if(errorCode.trim().equalsIgnoreCase("2")){
					/// 2	Invalid Mobile Number 
					displayErrorDialogMessg(bus,errorDesc,"CRMInvalidMobileError","ReturnAdd");



				}else if(errorCode.trim().equalsIgnoreCase("9")){
					///9	Internal Error
					displayErrorDialogMessg(bus,errorDesc,"CRMInternalError","Ok");

				}else{
					displayErrorDialogMessg(bus,"Internal Error","CRMInternalError","Ok");

				}



			}else{
				//in other cases
				displayErrorDialogMessg(bus,"Internal Error","CRMInternalError","Ok");


			}





		}


	}




	private HashMap populateHashMap(MAXCustomerMainCargo cargo, HashMap requestAttributes) {


		//changes for getting the transaction amount to be sent to the crm START
		CurrencyIfc totalPrice =null;
		if(cargo.getTransaction()!=null && cargo.getTransaction() instanceof SaleReturnTransactionIfc){	
			SaleReturnTransactionIfc transaction = (SaleReturnTransactionIfc)cargo.getTransaction();
			totalPrice = transaction.getTransactionTotals().getGrandTotal();
		}

		//changes for getting the transaction amount to be sent to the crm END

		EYSDate date = DomainGateway.getFactory().getEYSDateInstance();
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MMM-yyyy");

		requestAttributes.put(MAXTICCRMConstants.TIC_STORE_ID, cargo
				.getRegister().getWorkstation().getStore().getStoreID());
		requestAttributes.put(MAXTICCRMConstants.TIC_TILL_ID, cargo
				.getRegister().getCurrentTillID());


		// Get value from application.properties
		String URL = Gateway.getProperty("application", "LoyaltyTicWebServiceURL",
				null);

		String timeOut = Gateway.getProperty("application",
				"LoyaltytimeOutInMilliSeconds", null);

		requestAttributes.put(MAXTICCRMConstants.MOBILE_NUMBER, cargo.getTICCustomer().getTICMobileNumber());
		requestAttributes.put(MAXTICCRMConstants.FIRST_NAME, cargo.getTICCustomer().getTICFirstName());
		requestAttributes.put(MAXTICCRMConstants.LAST_NAME, cargo.getTICCustomer().getTICLastName());

		if(cargo.getTICCustomer().getTICEmail()!=null && !cargo.getTICCustomer().getTICEmail().equalsIgnoreCase("")){
			requestAttributes.put(MAXTICCRMConstants.EMAIL_ID, cargo.getTICCustomer().getTICEmail());
		}

		if(cargo.getTICCustomer().getTICPinNumber()!=null && !cargo.getTICCustomer().getTICPinNumber().equalsIgnoreCase("")){
			requestAttributes.put(MAXTICCRMConstants.PIN_CODE, cargo.getTICCustomer().getTICPinNumber());
		}

		if(cargo.getTICCustomer().getTICbirthdate()!=null && !cargo.getTICCustomer().getTICbirthdate().equalsIgnoreCase("")){

			SimpleDateFormat dateDob=new SimpleDateFormat("dd/MM/yyyy");
			try{
				requestAttributes.put(MAXTICCRMConstants.DOB,dateFormat.format(dateDob.parse(cargo.getTICCustomer().getTICbirthdate())));

			}catch(Exception e){

			}
		}

		if(cargo.getTICCustomer().getTICGender()!=null && !cargo.getTICCustomer().getTICGender().equalsIgnoreCase("")){
			requestAttributes.put(MAXTICCRMConstants.GENDER, cargo.getTICCustomer().getTICGender());
		}


		if(cargo.getEmployee()!=null && cargo.getEmployee().getEmployeeID()!=null){
			requestAttributes.put(MAXTICCRMConstants.CASHIER_ID, cargo.getEmployee().getEmployeeID());
		}else if(cargo.getOperator()!=null && cargo.getOperator().getEmployeeID()!=null){
			requestAttributes.put(MAXTICCRMConstants.CASHIER_ID, cargo.getOperator().getEmployeeID());

		}

		StringBuffer requestID = new StringBuffer();
		requestID.append(cargo.getTransactionID());
		requestID.append(date
				.toFormattedString(MAXTICCRMConstants.NEW_DATE_FORMAT));

		requestAttributes.put(MAXTICCRMConstants.REQUEST_ID, requestID);
		requestAttributes.put(MAXTICCRMConstants.REQUEST_SOURCE, "POS");
		requestAttributes.put(MAXTICCRMConstants.CUSTOMER_TYPE, "0000");

		if(cargo.getTransactionID()!=null && !cargo.getTransactionID().equalsIgnoreCase("")){
			requestAttributes.put(MAXTICCRMConstants.TIC_INVOICE_NUM,cargo.getTransactionID());
		}else{

			///for the case of the special order where transaction id is null
			requestAttributes.put(MAXTICCRMConstants.TIC_INVOICE_NUM,cargo
					.getRegister().getWorkstation().getStore().getStoreID()+ cargo
					.getRegister().getCurrentTillID()+"1000");

		}

		SimpleDateFormat dateBuss=new SimpleDateFormat("dd/MM/yyyy");
		try{
			//Code merging changes start: Patch 17_DateFormatLMRCustomer

			requestAttributes.put(MAXTICCRMConstants.TIC_INVOICE_DATE, dateFormat.format(dateBuss.parse(cargo.getRegister().getBusinessDate().toFormattedString("dd/MM/yyyy"))));

			//Code merging changes Ends : 17_DateFormatLMRCustomer

		}catch(Exception e){

		}
		if(totalPrice!=null && totalPrice.getStringValue()!=null  ){
			requestAttributes.put(MAXTICCRMConstants.INVOICE_AMOUNT,totalPrice.getStringValue());
		}else{
			requestAttributes.put(MAXTICCRMConstants.INVOICE_AMOUNT,"0");
		}


		// return HasMap with required values
		return requestAttributes;
	}

	/**
	 * Show an error dialog
	 * 
	 * @param bus
	 * @param name
	 * @param dialogType
	 */
	protected void displayErrorDialog(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
		.getManager(UIManagerIfc.TYPE);
		DialogBeanModel beanModel = new DialogBeanModel();
		beanModel.setResourceID("CRMConnectionError");
		beanModel.setType(DialogScreensIfc.ERROR);
		beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Ok");
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);

	}

	/* show
	 * an error dialog 
	 *  messg
	 *  resourceId
	 *  letterName
	 */
	protected void displayErrorDialogMessg(BusIfc bus,String messg,String resourceId,String letterName) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
		.getManager(UIManagerIfc.TYPE);
		DialogBeanModel beanModel = new DialogBeanModel();
		String[] messgArray=new String[1];
		messgArray[0]=messg;
		beanModel.setArgs(messgArray);
		beanModel.setResourceID(resourceId);
		beanModel.setType(DialogScreensIfc.ERROR);
		beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, letterName);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
	}



	private void showProcessCRMRequestScreen(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
		.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel  model=new POSBaseBeanModel();
		ui.showScreen(MAXPOSUIManagerIfc.PROCESS_CRM_REQUEST,model);
	}


}
