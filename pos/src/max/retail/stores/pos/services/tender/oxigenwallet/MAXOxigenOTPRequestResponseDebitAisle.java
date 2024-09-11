package max.retail.stores.pos.services.tender.oxigenwallet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
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
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.services.tender.oxigenwallet.debitresponse.OxigenDebitWalletResponse;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXOxigenOTPRequestResponseDebitAisle extends PosLaneActionAdapter {

	/**
	 * This site will use for enter otp code with timeout Interval parametrer
	 */
	private static final long serialVersionUID = 3726730763969968049L;
	HttpURLConnection connection=null;
	public void traverse(BusIfc bus) {	
			
			POSUIManagerIfc uiManager = (POSUIManagerIfc) bus
					.getManager(UIManagerIfc.TYPE);
			MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
			POSBaseBeanModel model = (POSBaseBeanModel) uiManager
					.getModel(MAXPOSUIManagerIfc.ENTER_OTP);
			PromptAndResponseModel promptResponseModel = model
					.getPromptAndResponseModel();
			DialogBeanModel dialogModel = new DialogBeanModel();
			String[] messgArray = new String[1];
			String debitOtp = promptResponseModel.getResponseText();
			
			if(debitOtp.length()!=4) {
				 messgArray[0] = "OTP Length Cannot Be Less Than Or Greater Than 4";
					dialogModel.setArgs(messgArray);
					dialogModel.setResourceID("OxigenUserError");
					dialogModel.setArgs(messgArray);
					dialogModel.setType(DialogScreensIfc.ERROR);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CANCEL);
					uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					// bus.mail("Cancel");

					return;
			}

		
		try 
		{
			
			JSONObject jsonOxigenDebitAmountRequest = getJsonRequestObject(cargo, debitOtp);
			String url = Gateway.getProperty("application", "OxigenWalletDebitAmountURL", "");
			//String url = "http://landmark-qa.thnxwallet.com/api/v1/wallet/debit";
			logger.info("URL of OxigenWalletDebitAmountURL API is " + url);
			logger.info("Request of OxigenWalletDebitAmountURL API is " + jsonOxigenDebitAmountRequest.toString());
			// Call CRM API
			
			String oxigenDebitAmountResponse = null;
			
			try {
				oxigenDebitAmountResponse = executeDebitRequest(url,jsonOxigenDebitAmountRequest);
				
				OxigenDebitWalletResponse debitWalletResponse = handleOxigenDebitAmountResponse(oxigenDebitAmountResponse);
				
				
				if(debitWalletResponse!=null&&debitWalletResponse.getResponseHeader() != null&&debitWalletResponse.getResponseHeader().getResponseCode() != null&&debitWalletResponse.getResponseHeader().getResponseCode().equalsIgnoreCase(MAXOxigenTenderConstants.USER_NOT_FOUND))
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
				
				
				if(debitWalletResponse!=null&&debitWalletResponse.getResponseHeader() != null&&debitWalletResponse.getResponseHeader().getResponseCode() != null&&debitWalletResponse.getResponseHeader().getResponseCode().equalsIgnoreCase(MAXOxigenTenderConstants.SUCCESS))
				{
				
					if(debitWalletResponse.getAppliedAmount().toString().equalsIgnoreCase("0")) {
						bus.mail("EWallet");
					}
					else{
						MAXSaleReturnTransaction maxSaleReturnTransaction = (MAXSaleReturnTransaction) cargo.getTransaction();
					       // cargo.setEWalletTenderFlag(true);
						cargo.setOxigenAppliedAmt(debitWalletResponse.getAppliedAmount().toString());
							maxSaleReturnTransaction.setEWalletTenderFlag(true);
							maxSaleReturnTransaction.seteWalletCreditResponse(oxigenDebitAmountResponse);
							messgArray[0]=debitWalletResponse.getAppliedAmount().toString();
							System.out.println("messgArray[0]"+messgArray[0]);
							dialogModel.setArgs(messgArray); 
						dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENSUCCESS);
						dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.SUCCESS);
						uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						/*
						 * System.out.println("Value of campareTo 1 :"+(messgArray[0].compareTo((cargo.
						 * getTransaction().getTenderTransactionTotals().getBalanceDue().toString()))==-
						 * 1));
						 * System.out.println("Value of campareTo 2:"+(messgArray[0].equalsIgnoreCase((
						 * cargo.getTransaction().getTenderTransactionTotals().getBalanceDue().toString(
						 * )))));
						 * 
						 * if(!(messgArray[0].equalsIgnoreCase((cargo.getTransaction().
						 * getTenderTransactionTotals().getBalanceDue().toString())))) {
						 * System.out.println("Value of campareTo :"+!(messgArray[0].compareTo((cargo.
						 * getTransaction().getTenderTransactionTotals().getBalanceDue().toString()))==-
						 * 1)); bus.mail("Cancel"); }
						 */
					}
					return;
				}
				
				
				
				else 
				{
					messgArray[0] = "Incorrect OTP entered to use Wallet money. Please ask the customer for the OTP again, or re-send fresh OTP";
					dialogModel.setArgs(messgArray);
					dialogModel.setResourceID("OxigenUserError");
					dialogModel.setArgs(messgArray); 
					//dialogModel.setResourceID("FailureMessageScreen");
					 dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
					 dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, CommonLetterIfc.FAILURE);
			        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.CANCEL);
			        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					return;
					 
				}
			}
			catch (SocketTimeoutException socketTimeOutException) {
				if (socketTimeOutException.getMessage()!=null) 
					messgArray[0] = socketTimeOutException.getMessage();
				 else 
				    messgArray[0] = "Connection TimeOut";
				dialogModel.setArgs(messgArray);
				dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
				 dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
				 dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, CommonLetterIfc.FAILURE);
		        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.CANCEL);
		        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				return;
			} 
			
			catch (ConnectException  connectException) {
				if (connectException.getMessage()!=null) 
					messgArray[0] = connectException.getMessage();
				 else 
				    messgArray[0] = "Connection Exception";
				dialogModel.setArgs(messgArray);
				dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
				 dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
				 dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, CommonLetterIfc.FAILURE);
		        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.CANCEL);
		        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				return;
			}
			catch(NoRouteToHostException noRouteToHostException){
				if (noRouteToHostException.getMessage()!=null) 
					messgArray[0] = noRouteToHostException.getMessage();
				 else 
				    messgArray[0] = "Host Exception";
				dialogModel.setArgs(messgArray);
				dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
				 dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
				 dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, CommonLetterIfc.FAILURE);
		        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.CANCEL);
		        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				return;
			}
			catch (UnknownHostException unknownHostException) {
				if (unknownHostException.getMessage()!=null) 
					messgArray[0] = unknownHostException.getMessage();
				 else 
				    messgArray[0] = "Connectivity Error";
				dialogModel.setArgs(messgArray);
				dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
				 dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
				 dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, CommonLetterIfc.FAILURE);
		        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.CANCEL);
		        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				return;
			}catch (JsonParseException jsonParseException) {
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
			logger.error("Error in setting oxigen webservice calling " + e.getMessage());
		}
		
		finally {
			if(connection != null) {
				connection.disconnect(); 
			}
		}
		
	}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private JSONObject getJsonRequestObject(MAXTenderCargo cargo, String debitOtp) {
			JSONObject job = new JSONObject();
			String mobileNo=null;
			MAXCustomer customer=null;
			String invoiceRequestID=null;
			MAXSaleReturnTransaction maxTransaction=null;
			JSONArray modeOfPayment = new JSONArray();
			Date date= new Date();
			 SimpleDateFormat format = new SimpleDateFormat(
					    "yyyy-MM-dd'T'HH:mm:ss.SSS");
					String requestTimestamp=format.format(date);
					SimpleDateFormat invoiceDateformat = new SimpleDateFormat(
							"dd-MM-yyyy HH:mm:ss");
					if (cargo.getTransaction().getCustomer() != null
							&& cargo.getTransaction().getCustomer() instanceof MAXCustomer) {
						customer = (MAXCustomer) cargo.getTransaction().getCustomer();
						mobileNo = customer.getPrimaryPhone().getPhoneNumber().toString();
					}
					if (cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
						maxTransaction = (MAXSaleReturnTransaction) cargo.getTransaction();
						invoiceRequestID = maxTransaction.getTransactionID().toString();
					}
			modeOfPayment.add(MAXOxigenTenderConstants.MODE_OF_PAYMENT_WALLET);
			Map m1 = new LinkedHashMap(1);
			m1.put(MAXOxigenTenderConstants.REQUEST_TYPE, MAXOxigenTenderConstants.OTP_TYPE_DEBIT);
		//	m1.put(MAXOxigenTenderConstants.REQUEST_ID, MAXOxigenTenderConstants.REQUEST_ID_CONSTANT);
			m1.put(MAXOxigenTenderConstants.REQUEST_ID, invoiceRequestID+requestTimestamp.toString());
			m1.put(MAXOxigenTenderConstants.REQUEST_TIME, requestTimestamp.toString());
			m1.put(MAXOxigenTenderConstants.MOBILENUMBER, mobileNo);
			//m1.put(MAXOxigenTenderConstants.MOBILENUMBER, "9873477777");
			m1.put(MAXOxigenTenderConstants.ORIGINAL_DIALOGUE_TRACE_ID, null);
			//m1.put(MAXOxigenTenderConstants.ORIGINAL_DIALOGUE_TRACE_ID, MAXOxigenTenderConstants.ORIGINAL_DIALOGUE_TRACE_ID_CONSTANT);
			m1.put(MAXOxigenTenderConstants.WALLET_OWNER, MAXOxigenTenderConstants.SPAR_CONSTANT);
			m1.put(MAXOxigenTenderConstants.CHANNEL,MAXOxigenTenderConstants.POS_CONSTANT);
			job.put(MAXOxigenTenderConstants.REQUEST_HEADER, m1);
			m1 = new LinkedHashMap(2);
			m1.put(MAXOxigenTenderConstants.STORE_CODE, cargo.getStoreStatus().getStore().getStoreID());
			m1.put(MAXOxigenTenderConstants.TERMINAL_ID, cargo.getRegister().getWorkstation().getWorkstationID());
			m1.put(MAXOxigenTenderConstants.OPTIONAL_INFO, null);
			job.put(MAXOxigenTenderConstants.STORE_DETAILS, m1);
			m1 = new LinkedHashMap(3);
			m1.put(MAXOxigenTenderConstants.OTP_TYPE, MAXOxigenTenderConstants.OTP_TYPE_DEBIT);
			if (cargo.getOtpRefNum()!=null) {
				m1.put(MAXOxigenTenderConstants.REF_NUM, cargo.getOtpRefNum());
			}
			else {
				m1.put(MAXOxigenTenderConstants.REF_NUM, null);	
			}
			
			m1.put(MAXOxigenTenderConstants.OTP, debitOtp);
			job.put(MAXOxigenTenderConstants.OTP_DETAILS, m1);
			m1 = new LinkedHashMap(4);
			m1.put(MAXOxigenTenderConstants.INVOICENO, cargo.getTransaction().getTransactionID().toString());
			m1.put(MAXOxigenTenderConstants.INVOICEDATE, invoiceDateformat.format(cargo.getRetailTransaction().getBusinessDay().toDate()));
			m1.put(MAXOxigenTenderConstants.INVOICEGROSSAMOUNT, (cargo.getTransaction().getTransactionTotals().getGrandTotal().abs().toString()));
			m1.put(MAXOxigenTenderConstants.INVOICENETAMOUNT, (cargo.getTransaction().getTransactionTotals().getBalanceDue().abs().toString()));
			m1.put(MAXOxigenTenderConstants.MODEOFPAYMENT, modeOfPayment);
			m1.put(MAXOxigenTenderConstants.PROMOCODE, MAXOxigenTenderConstants.PROMO_CODE_BOGO);
			job.put(MAXOxigenTenderConstants.TRANSACTIONINFO, m1);
			job.put(MAXOxigenTenderConstants.AMOUNT, (String) cargo.getTenderAttributes().get(TenderConstants.AMOUNT));
			return job;
		}
		
		public String executeDebitRequest(String URL, JSONObject jsonContentObj) throws IOException, JsonParseException 
		{

			URL targetURL = new URL(URL);
			
			connection = (HttpURLConnection) targetURL.openConnection();
			connection.setRequestMethod(MAXOxigenTenderConstants.REQUEST_METHOD_POST);
			String urlParameters = jsonContentObj.toString();


			connection.setRequestProperty(MAXOxigenTenderConstants.CONTENTTYPE, MAXOxigenTenderConstants.JSON);
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
			InputStream is;
			if (responseCode == HttpURLConnection.HTTP_OK) {
				is = connection.getInputStream();
			} else {
				is = connection.getErrorStream();
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
			String line = "";
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
				logger.debug(" Oxigen Debit Amount Response Body:::" + response);
				System.out.println("Oxigen Debit Amount Response Body:::"+response);
				return response.toString();
			}
		
		private OxigenDebitWalletResponse handleOxigenDebitAmountResponse(String oxigenDebitAmountResponse) throws JsonParseException, JsonMappingException, IOException{
			ObjectMapper mapper = new ObjectMapper();
			OxigenDebitWalletResponse oxigenDebitWalletResponse = mapper.readValue(oxigenDebitAmountResponse, OxigenDebitWalletResponse.class);
			return oxigenDebitWalletResponse;
		}

}

