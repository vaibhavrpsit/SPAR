/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  Aug 25, 2021              Atul Shukla                   EWallet FES Implementation
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.tender.oxigenwallet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXOxigenWebRequestForOTPAisle extends PosLaneActionAdapter {

	  protected static Logger logger = Logger.getLogger(MAXOxigenWebRequestForOTPAisle.class);
	private static final long serialVersionUID = 1L;
	HttpURLConnection connection = null;

	public void traverse(BusIfc bus) {
		//System.out.println("Arrive in maxoxigenwebrequestforotpaisle");

		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();

		DialogBeanModel dialogModel = new DialogBeanModel();
		String[] messgArray = new String[1];
		BigDecimal enteredAmt = new BigDecimal((String) cargo.getTenderAttributes().get(TenderConstants.AMOUNT));
		BigDecimal transactionAmt = new BigDecimal(((MAXSaleReturnTransaction) cargo.getTransaction()).getTransactionTotals().getGrandTotal().toString());
		//System.out.println("transactionAmt"+transactionAmt);
		//System.out.println("enteredAmt"+enteredAmt);
		//System.out.println("(enteredAmt.compareTo(transactionAmt)) :"+(enteredAmt.compareTo(transactionAmt)));
		
		if(enteredAmt.compareTo(transactionAmt)==1 ||enteredAmt.compareTo(transactionAmt)==-1) {
		
		if (enteredAmt.compareTo(transactionAmt)==1) {
			//System.out.println("Inside if Condition for split");
			/*
			 * messgArray[0] = "Split Tendering Is Not Allowed In Refund";
			 * dialogModel.setArgs(messgArray);
			 * dialogModel.setResourceID("OxigenUserError");
			 * dialogModel.setArgs(messgArray); dialogModel.setType(DialogScreensIfc.ERROR);
			 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
			 * CommonLetterIfc.CANCEL);
			 * uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			 */
			// bus.mail("Cancel");
			//return;
			
			showErrorDialog(uiManager , "OxigenUserError");
			
		}
		if (enteredAmt.compareTo(transactionAmt)==-1) {
			//System.out.println("Arrive in if for overtendering");
			messgArray[0] = "Over Tendering Is Not Allowed For EWallet Tender";
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID("OxigenUserError");
			dialogModel.setArgs(messgArray);
			dialogModel.setType(DialogScreensIfc.ERROR);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CANCEL);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			// bus.mail("Cancel");

			//return;

		}
		bus.mail(CommonLetterIfc.CANCEL);
		}else {

		try {

			JSONObject jsonOxigenOtpGeneratRequest = getJsonRequestObject(cargo);
			String url = Gateway.getProperty("application",
					"OxigenWalletGeneratOtpURL", "");
			logger.info("GenerateOTO URL is " + url);
			logger.info("GetOTP Request for return is "
					+ jsonOxigenOtpGeneratRequest.toString());
			// Call CRM API
			System.out.println("GetOTP Request for return is "
					+ jsonOxigenOtpGeneratRequest.toString());
			MAXSaleReturnTransaction transaction = null;
			String oxigenGeneratedOtpResponse = null;
			String returnEwalletTraceID = null;
			
			
			
			try {
				oxigenGeneratedOtpResponse = executeOtpGeneratRequest(url,
						jsonOxigenOtpGeneratRequest);
				logger.info("getOTP Response for return is "
						+ oxigenGeneratedOtpResponse.toString());

				
				
				OxigenGeneratOtpResponse generateOxigenOtpResponse = handleOxigenOtpResponse(oxigenGeneratedOtpResponse);
				
				if(generateOxigenOtpResponse!=null&&generateOxigenOtpResponse.getResponseHeader() != null&&generateOxigenOtpResponse.getResponseHeader().getResponseCode() != null&&generateOxigenOtpResponse.getResponseHeader().getResponseCode().equalsIgnoreCase(MAXOxigenTenderConstants.USER_NOT_FOUND))
				{
					messgArray[0] = "Customer Not Attached To The Transaction \n Or \n Wallet Not Found For Customer";
					dialogModel.setArgs(messgArray);
					dialogModel.setResourceID("OxigenUserError");
					dialogModel.setArgs(messgArray); 
					dialogModel.setType(DialogScreensIfc.ERROR);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CANCEL);
					uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						//bus.mail("Cancel");
					
					return;
				}
				
				if (generateOxigenOtpResponse!=null&&generateOxigenOtpResponse.getOtpDetails()!=null&&generateOxigenOtpResponse.getOtpDetails().getRefNo()!=null) {
					cargo.setOtpRefNum(generateOxigenOtpResponse.getOtpDetails().getRefNo());
				}

				if (generateOxigenOtpResponse != null
						
						&& generateOxigenOtpResponse != null
						&& generateOxigenOtpResponse.getResponseHeader()
								.getTraceId() != null) {
					if (cargo != null
							&& cargo.getTransaction() != null
							&& cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
						returnEwalletTraceID = generateOxigenOtpResponse
								.getResponseHeader().getTraceId().trim()
								.toString();
						transaction = (MAXSaleReturnTransaction) cargo
								.getTransaction();
						transaction.seteWalletTraceId(returnEwalletTraceID);
					}

				
					return;
				} else {
					dialogModel.setResourceID("FailureMessageScreen");
					dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
							CommonLetterIfc.FAILURE);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,
							CommonLetterIfc.CANCEL);
					uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
							dialogModel);
					return;

				}
			} catch (SocketTimeoutException socketTimeOutException) {
				messgArray[0] = socketTimeOutException.getMessage();
				dialogModel.setArgs(messgArray);
				dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
				dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
						CommonLetterIfc.FAILURE);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,
						CommonLetterIfc.CANCEL);
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
						dialogModel);
				return;
			}

			catch (ConnectException connectException) {
				messgArray[0] = connectException.getMessage();
				dialogModel.setArgs(messgArray);
				dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
				dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
						CommonLetterIfc.FAILURE);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,
						CommonLetterIfc.CANCEL);
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
						dialogModel);
				return;
			} catch (NoRouteToHostException noRouteToHostException) {
				messgArray[0] = noRouteToHostException.getMessage();
				dialogModel.setArgs(messgArray);
				dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
				dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
						CommonLetterIfc.FAILURE);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,
						CommonLetterIfc.CANCEL);
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
						dialogModel);
				return;
			} catch (UnknownHostException unknownHostException) {
				messgArray[0] = "Connectivity Error";
				dialogModel.setArgs(messgArray);
				dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
				dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
						CommonLetterIfc.FAILURE);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,
						CommonLetterIfc.CANCEL);
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
						dialogModel);
				return;
			}
			catch (JsonParseException jsonParseException) {
			    messgArray[0] = "Error In Calling Oxigen Webservice Request";
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
			 dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
			 dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, CommonLetterIfc.FAILURE);
	        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.CANCEL);
	        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
		
			// TODO: handle exception
		}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in setting oxigen webservice calling "
					+ e.getMessage());
		}

		finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
     bus.mail("EWallet");
	}
		}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static JSONObject getJsonRequestObject(MAXTenderCargo cargo) {
		JSONObject job = new JSONObject();
		Map m1 = new LinkedHashMap(4);
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS");
		String requestTimestamp = format.format(date);
		MAXCustomer customer = null;
		String mobileNo = null;
		if (cargo.getTransaction().getCustomer() != null
				&& cargo.getTransaction().getCustomer() instanceof MAXCustomer) {
			customer = (MAXCustomer) cargo.getTransaction().getCustomer();
			mobileNo = customer.getPrimaryPhone().getPhoneNumber().toString();
		}
		m1.put(MAXOxigenTenderConstants.REQUEST_TYPE,
				MAXOxigenTenderConstants.OTP_GENERATE_REQUEST);
		m1.put(MAXOxigenTenderConstants.REQUEST_ID,
				cargo.getTransaction().getTransactionID().toString()+requestTimestamp);
		m1.put(MAXOxigenTenderConstants.REQUEST_TIME, requestTimestamp);
		m1.put(MAXOxigenTenderConstants.MOBILENUMBER, mobileNo);
		//m1.put(MAXOxigenTenderConstants.MOBILENUMBER, "9873477777");
		m1.put(MAXOxigenTenderConstants.ORIGINAL_DIALOGUE_TRACE_ID, null);
		m1.put(MAXOxigenTenderConstants.WALLET_OWNER,
				MAXOxigenTenderConstants.SPAR_CONSTANT);
		m1.put(MAXOxigenTenderConstants.CHANNEL,
				MAXOxigenTenderConstants.POS_CONSTANT);
		job.put(MAXOxigenTenderConstants.REQUEST_HEADER, m1);
		m1 = new LinkedHashMap(2);
		m1.put(MAXOxigenTenderConstants.STORE_CODE, cargo.getStoreStatus()
				.getStore().getStoreID());
		m1.put(MAXOxigenTenderConstants.TERMINAL_ID, cargo.getRegister()
				.getWorkstation().getWorkstationID());
		m1.put(MAXOxigenTenderConstants.OPTIONAL_INFO, null);
		job.put(MAXOxigenTenderConstants.STORE_DETAILS, m1);
		m1 = new LinkedHashMap(1);
		m1.put(MAXOxigenTenderConstants.OTP_TYPE,
				MAXOxigenTenderConstants.WALLET_CREDIT_REQUEST);
		m1.put(MAXOxigenTenderConstants.REF_NUM, null);
		m1.put(MAXOxigenTenderConstants.OTP, null);
		job.put(MAXOxigenTenderConstants.OTP_DETAILS, m1);
		return job;
	}

	public String executeOtpGeneratRequest(String URL, JSONObject jsonContentObj)
			throws IOException, JsonParseException {

		URL targetURL = new URL(URL);

		connection = (HttpURLConnection) targetURL.openConnection();
		connection
				.setRequestMethod(MAXOxigenTenderConstants.REQUEST_METHOD_POST);
		String urlParameters = jsonContentObj.toString();

		connection.setRequestProperty(MAXOxigenTenderConstants.CONTENTTYPE,
				MAXOxigenTenderConstants.JSON);
		connection.setRequestProperty(MAXOxigenTenderConstants.CONTENTLENGTH,
				Integer.toString(urlParameters.getBytes().length));
		connection.setUseCaches(false);
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);

		connection.setDoOutput(true);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.close();
		int responseCode = connection.getResponseCode();
		logger.info("CreditEwallet response code is  " + responseCode);
		InputStream is;
		if (responseCode == HttpURLConnection.HTTP_OK) {
			is = connection.getInputStream();
		} else {
			is = connection.getErrorStream();
		}

		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		StringBuilder response = new StringBuilder(); // or StringBuffer if not
														// Java 5+
		String line = "";
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
		logger.debug("Response Body:::" + response);
		System.out.println("Response Body:::" + response);
		return response.toString();
	}

	private OxigenGeneratOtpResponse handleOxigenOtpResponse(
			String oxigenGeneratedOtpResponse) throws JsonParseException,
			JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		OxigenGeneratOtpResponse oxigenOtpResponse = mapper.readValue(
				oxigenGeneratedOtpResponse, OxigenGeneratOtpResponse.class);
		return oxigenOtpResponse;
	}
	
	  protected void showErrorDialog(POSUIManagerIfc ui, String resourceID)
	    {
	        DialogBeanModel model = new DialogBeanModel();
	        //System.out.println("Split Tendering"+resourceID);
	        model.setResourceID(resourceID);
	        model.setType(DialogScreensIfc.ERROR);
	        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CANCEL);
	        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	        return;
	    }

}
