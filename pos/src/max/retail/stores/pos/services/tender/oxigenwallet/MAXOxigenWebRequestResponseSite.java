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

import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import org.json.simple.parser;

import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.domain.tender.TenderCash;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;;

public class MAXOxigenWebRequestResponseSite extends PosSiteActionAdapter

{
	private static final long serialVersionUID = 1L;
	
	HttpURLConnection connection=null;
	
	boolean transContainsEwalletTender=false;

	public void arrive(BusIfc bus) {
		
		System.out.println("Inside MAXOxigenWebRequestResponseSite");
	POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
	MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
	TenderLineItemIfc[] transTenders=  ((TenderCargo) bus.getCargo()).getTransaction().getTenderLineItems();
	
	for (TenderLineItemIfc tenderLineItem : transTenders) {
		if (tenderLineItem.isCollected() && (tenderLineItem instanceof TenderCash)) {
			TenderCash cashTender = (TenderCash) tenderLineItem;
			if (cashTender.isEWalletTenderType()) {
       		 transContainsEwalletTender=true;
       		 break;
       	 }
		}
	}
	
	 
	 DialogBeanModel dialogModel = new DialogBeanModel();
		String[] messgArray = new String[1];
	 
	 if(transContainsEwalletTender) {
		 
		    messgArray[0] = "EWallet Tender Is Already Used Once In Current Transaction";
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID("OxigenUserError");
			dialogModel.setArgs(messgArray);
			dialogModel.setType(DialogScreensIfc.ERROR);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CANCEL);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			// bus.mail("Cancel");
			transContainsEwalletTender=false;
			return;
	 }
	
	
	
	BigDecimal enteredAmt = new BigDecimal((String) cargo.getTenderAttributes().get(TenderConstants.AMOUNT));
	BigDecimal transactionAmt = new BigDecimal(((MAXSaleReturnTransaction) cargo.getTransaction()).getTransactionTotals().getGrandTotal().toString());
	//System.out.println("transactionAmt :"+transactionAmt);
	if (enteredAmt.compareTo(transactionAmt)==1) {
		//System.out.println("Arrive in maxoxigenwebrequestResponseSite over tendering");
		messgArray[0] = "Over Tendering Is Not Allowed For EWallet Tender";
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
		if (cargo.getTransaction().getCustomer()==null) {
			messgArray[0] ="Customer Is Not Attched To Transaction \n Or \n Customer Does Not Have The Wallet"; 
			  dialogModel.setArgs(messgArray);
			  dialogModel.setResourceID("OxigenCustomerFailure");
			  dialogModel.setType(DialogScreensIfc.ERROR);
			  //dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,CommonLetterIfc.FAILURE);
			  dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.CANCEL);
			  uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel); return;
		}
		
		JSONObject jsonOxigenOtpGeneratRequest = getJsonRequestObject(cargo);
		String url = Gateway.getProperty("application", "OxigenWalletGeneratOtpURL", "");
		//String url = "http://landmark-qa.thnxwallet.com/api/v1/otp/generate";
		logger.info("URL of OxigenWalletGeneratOtp API is " + url);
		logger.info("Request of OxigenWalletGeneratOtp API is " + jsonOxigenOtpGeneratRequest.toString());
		System.out.println("URL of OxigenWalletGeneratOtp API is " + url);
		System.out.println("Request of OxigenWalletGeneratOtp API is " + jsonOxigenOtpGeneratRequest.toString());
		// Call CRM API
		
		String oxigenGeneratedOtpResponse = null;
		try {
			oxigenGeneratedOtpResponse = executeOtpGeneratRequest(url,jsonOxigenOtpGeneratRequest);
			
			OxigenGeneratOtpResponse generateOxigenOtpResponse = handleOxigenOtpResponse(oxigenGeneratedOtpResponse);
			//System.out.println("generateOxigenOtpResponse :"+ generateOxigenOtpResponse.toString());
			
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
			
			if (generateOxigenOtpResponse!=null && generateOxigenOtpResponse.getOtpDetails()!=null && generateOxigenOtpResponse.getOtpDetails().getRefNo()!=null) {
				cargo.setOtpRefNum(generateOxigenOtpResponse.getOtpDetails().getRefNo());
			}
			
			
			if(generateOxigenOtpResponse!=null&&generateOxigenOtpResponse.getResponseHeader() != null&&generateOxigenOtpResponse.getResponseHeader().getResponseCode() != null&&generateOxigenOtpResponse.getResponseHeader().getResponseCode().equalsIgnoreCase(MAXOxigenTenderConstants.SUCCESS))
			{
				cargo.seteWalletTraceId(generateOxigenOtpResponse.getResponseHeader().getTraceId());
				bus.mail(CommonLetterIfc.SUCCESS, BusIfc.CURRENT);
				
				return;
			}
			
			else 
			{
				dialogModel.setResourceID("FailureMessageScreen");
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
		
	} 
		  catch (NullPointerException nullPointerException) { 
		  messgArray[0] ="Null Element In Request"; dialogModel.setArgs(messgArray);
		  dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
		  dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
		  dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
		  CommonLetterIfc.FAILURE);
		  dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,
		  CommonLetterIfc.CANCEL);
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
		  
	
	catch (Exception e) {
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
	private static JSONObject getJsonRequestObject(MAXTenderCargo cargo) {
		JSONObject job = new JSONObject();
		Map m1 = new LinkedHashMap(4);
		Date date= new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		String requestTimestamp=format.format(date);
		String number=null;
		m1.put(MAXOxigenTenderConstants.REQUEST_HEADER, MAXOxigenTenderConstants.OTP_GENERATE_REQUEST);
		m1.put(MAXOxigenTenderConstants.REQUEST_ID, cargo.getTransaction().getTransactionID().toString()+requestTimestamp.toString());
		m1.put(MAXOxigenTenderConstants.REQUEST_TIME, requestTimestamp.toString());
		String mobNumber = cargo.getTransaction().getCustomer().getPrimaryPhone().getPhoneNumber().toString();
		m1.put(MAXOxigenTenderConstants.MOBILENUMBER, cargo.getTransaction().getCustomer().getPrimaryPhone().getPhoneNumber().toString());
		//m1.put(MAXOxigenTenderConstants.MOBILENUMBER, mobNumber);
		m1.put(MAXOxigenTenderConstants.ORIGINAL_DIALOGUE_TRACE_ID, null);
		m1.put(MAXOxigenTenderConstants.WALLET_OWNER, MAXOxigenTenderConstants.SPAR_CONSTANT);
		m1.put(MAXOxigenTenderConstants.CHANNEL,MAXOxigenTenderConstants.POS_CONSTANT);
		job.put(MAXOxigenTenderConstants.REQUEST_HEADER, m1);
		m1 = new LinkedHashMap(2);
		m1.put(MAXOxigenTenderConstants.STORE_CODE, cargo.getStoreStatus().getStore().getStoreID());
		m1.put(MAXOxigenTenderConstants.TERMINAL_ID, cargo.getRegister().getWorkstation().getWorkstationID());
		m1.put(MAXOxigenTenderConstants.OPTIONAL_INFO, null);
		job.put(MAXOxigenTenderConstants.STORE_DETAILS, m1);
		m1 = new LinkedHashMap(1);
		m1.put(MAXOxigenTenderConstants.OTP_TYPE, MAXOxigenTenderConstants.OTP_TYPE_DEBIT);
		m1.put(MAXOxigenTenderConstants.REF_NUM, null);
		m1.put(MAXOxigenTenderConstants.OTP, null);
		job.put(MAXOxigenTenderConstants.OTP_DETAILS, m1);
		return job;
	}
	
	public String executeOtpGeneratRequest(String URL, JSONObject jsonContentObj) throws IOException, JsonParseException {

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
			logger.info("GeneratOtp Response Body:::" + response);
			//System.out.println("response :"+response);
			return response.toString();
		}
	
	private OxigenGeneratOtpResponse handleOxigenOtpResponse(String oxigenGeneratedOtpResponse) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		OxigenGeneratOtpResponse oxigenOtpResponse = mapper.readValue(oxigenGeneratedOtpResponse, OxigenGeneratOtpResponse.class);
		return oxigenOtpResponse;
	}

}

	
//}
