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

import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.services.tender.oxigenwallet.debitresponse.OxigenDebitWalletResponse;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXOxigenWalletDebitInfoSite extends PosSiteActionAdapter

{
	private static final long serialVersionUID = 1L;
	
	HttpURLConnection connection=null;

	public void arrive(BusIfc bus) {
		
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		POSBaseBeanModel model = (POSBaseBeanModel) uiManager.getModel(MAXPOSUIManagerIfc.ENTER_OTP);
		PromptAndResponseModel promptResponseModel = model.getPromptAndResponseModel();
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
		
		JSONObject jsonOxigenDebitAmountRequest = getJsonRequestObject(cargo,debitOtp);
		String url = Gateway.getProperty("application", "OxigenWalletDebitAmountURL", "");
		//String url = "http://landmark-qa.thnxwallet.com/api/v1/wallet/debit";
		logger.info("URL of OxigenWalletDebitAmountURL API is " + url);
		logger.info("Request of OxigenWalletDebitAmountURL API is " + jsonOxigenDebitAmountRequest.toString());
		// Call CRM API
		
		String oxigenDebitAmountResponse = null;
		
		try {
			oxigenDebitAmountResponse = executeDebitRequest(url,jsonOxigenDebitAmountRequest);
			
			OxigenDebitWalletResponse debitWalletResponse = handleOxigenDebitAmountResponse(oxigenDebitAmountResponse);
			
			if(debitWalletResponse!=null&&debitWalletResponse.getResponseHeader() != null&&debitWalletResponse.getResponseHeader().getResponseCode() != null&&debitWalletResponse.getResponseHeader().getResponseCode().equalsIgnoreCase(MAXOxigenTenderConstants.SUCCESS))
			{
				MAXSaleReturnTransaction maxSaleReturnTransaction = (MAXSaleReturnTransaction) cargo.getTransaction();
						//cargo.setEWalletTenderFlag(true);
						maxSaleReturnTransaction.setEWalletTenderFlag(true);
						maxSaleReturnTransaction.seteWalletCreditResponse(oxigenDebitAmountResponse);
				messgArray[0]=debitWalletResponse.getAppliedAmount().toString();
				System.out.println("messgArray[0]"+messgArray[0]);
				dialogModel.setArgs(messgArray); 
					dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENSUCCESS);
					dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.SUCCESS);
					uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				
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
			messgArray[0] = socketTimeOutException.getMessage();
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
			 dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
			 dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, CommonLetterIfc.FAILURE);
	        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.CANCEL);
	        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
		} 
		
		catch (ConnectException  connectException) {
			messgArray[0] = connectException.getMessage();
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
			 dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
			 dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, CommonLetterIfc.FAILURE);
	        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.CANCEL);
	        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
		}
		catch(NoRouteToHostException noRouteToHostException){
			messgArray[0] = noRouteToHostException.getMessage();
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
			 dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
			 dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, CommonLetterIfc.FAILURE);
	        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.CANCEL);
	        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
		}
		catch (UnknownHostException unknownHostException) {
			messgArray[0] = "Connectivity Error";
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
			 dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
			 dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, CommonLetterIfc.FAILURE);
	        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.CANCEL);
	        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
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
		JSONArray modeOfPayment = new JSONArray();
		Date date= new Date();
		 SimpleDateFormat format = new SimpleDateFormat(
				    "yyyy-MM-dd'T'HH:mm:ss.SSS");
				String requestTimestamp=format.format(date);
				SimpleDateFormat invoiceDateformat = new SimpleDateFormat(
						"dd-MM-yyyy HH:mm:ss");
		modeOfPayment.add(MAXOxigenTenderConstants.MODE_OF_PAYMENT_WALLET);
		Map m1 = new LinkedHashMap(1);
		m1.put(MAXOxigenTenderConstants.REQUEST_TYPE, MAXOxigenTenderConstants.OTP_TYPE_DEBIT);
		m1.put(MAXOxigenTenderConstants.REQUEST_ID, cargo.getTransaction().getTransactionID().toString()+requestTimestamp.toString());
		m1.put(MAXOxigenTenderConstants.REQUEST_TIME, requestTimestamp.toString());
		
		m1.put(MAXOxigenTenderConstants.MOBILENUMBER, cargo.getTransaction().getCustomer().getPrimaryPhone().getPhoneNumber().toString());
		//m1.put(MAXOxigenTenderConstants.MOBILENUMBER, "9873477777");
		//m1.put(MAXOxigenTenderConstants.ORIGINAL_DIALOGUE_TRACE_ID, cargo.geteWalletTraceId());
		m1.put(MAXOxigenTenderConstants.ORIGINAL_DIALOGUE_TRACE_ID, null);
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
		System.out.println("MAXOxigenWalletDebitInfoSite 235: "+ m1);
		return job;
	}
	
	public String executeDebitRequest(String URL, JSONObject jsonContentObj) throws IOException, JsonParseException {

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
			return response.toString();
		}
	
	private OxigenDebitWalletResponse handleOxigenDebitAmountResponse(String oxigenDebitAmountResponse) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		OxigenDebitWalletResponse oxigenDebitWalletResponse = mapper.readValue(oxigenDebitAmountResponse, OxigenDebitWalletResponse.class);
		return oxigenDebitWalletResponse;
	}

}
