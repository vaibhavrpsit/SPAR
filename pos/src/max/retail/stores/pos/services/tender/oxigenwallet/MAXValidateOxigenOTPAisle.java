/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  Aug 27, 2021              Atul Shukla                   EWallet FES Implementation
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
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

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import max.retail.stores.domain.MAXOxigenWalletCreditResponse;
import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXValidateOxigenOTPAisle extends LaneActionAdapter {
	/**
	 * 
	 */
	protected static final Logger logger = Logger
			.getLogger(MAXValidateOxigenOTPAisle.class);
	private static final long serialVersionUID = 1L;
	HttpURLConnection connection = null;

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
		String creditOtp = promptResponseModel.getResponseText();
		logger.info("AKS: eWalletCredit Response creditOtp   " + creditOtp);
		//System.out.println("cargo.geteWalletMobileNumber() :"+cargo.geteWalletMobileNumber());
		
		if(creditOtp.length()!=4) {
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
		try {

			JSONObject jsonOxigenOtpGeneratRequest = getJsonRequestObject(
					cargo, creditOtp);
			String url = Gateway.getProperty("application","OxigenWalletCreditURL", "");
			logger.info("Ewallet credit URL of Search Edge API is " + url);
			logger.info("Ewallet credit Request1 of Search Edge API is "
					+ jsonOxigenOtpGeneratRequest.toString());
			// Call CRM API

			String oxigenGeneratedOtpResponse = null;
			try {
				oxigenGeneratedOtpResponse = executeOtpGeneratRequest(url,
						jsonOxigenOtpGeneratRequest);

				MAXOxigenWalletCreditResponse generateOxigenOtpResponse = handleOxigenOtpResponse(oxigenGeneratedOtpResponse);
				logger.info("Ewallet credit Response of Search Edge API is "
						+ oxigenGeneratedOtpResponse.toString());
				MAXSaleReturnTransaction maxSaleReturnTransaction = (MAXSaleReturnTransaction) cargo
						.getTransaction();
				
				if (generateOxigenOtpResponse != null
						&& generateOxigenOtpResponse.getResponseHeader()
								.getResponseCode().toString()
								.equalsIgnoreCase(MAXOxigenTenderConstants.USER_NOT_FOUND)) {
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
				
				if (generateOxigenOtpResponse != null
						&& generateOxigenOtpResponse.getResponseHeader()
								.getResponseCode().toString()
								.equalsIgnoreCase("SUCCESS")) {
					//cargo.setEWalletTenderFlag(true);
					//cargo.(MAXSaleReturnTransaction)transaction)
                MAXSaleReturnTransaction abc =(MAXSaleReturnTransaction)cargo.getTransaction();
                abc.setEWalletTenderFlag(true);
					maxSaleReturnTransaction.setEWalletTenderFlag(true);
					maxSaleReturnTransaction
							.seteWalletCreditResponse(oxigenGeneratedOtpResponse);
					bus.mail("Cash", BusIfc.CURRENT);

					return;
				} 
				else {
				messgArray[0] = "Incorrect OTP entered to add refund amount to Wallet. Please ask the customer the OTP again or re-send fresh OTP";
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
			logger.error("Error in setting oxigen Ewallet credit webservice calling "
					+ e.getMessage());
		}

		finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static JSONObject getJsonRequestObject(MAXTenderCargo cargo,
			String creditOtp) {
		String eWalletTraceId = null;
		String mobileNo = null;
		String invoiceNo = null;
		Date invoiceDate = null;
		String formattedInvoiceDate = null;
		String invoiceGrossAmt = null;
		String invoiceNetAmt = null;
		// Vector<TenderLineItemIfc> modeOfPayment=null;
		String promoCode = "BOGO";
		// String amount=null;
		// String subWalletType=null;
		MAXCustomer customer = null;
		MAXSaleReturnTransaction maxTransaction = null;
		JSONArray array = new JSONArray();
		array.add("CASH");
		array.add("WALLET");
		array.add("LOYALTY_WALLET");
		array.add("CARD");
		JSONObject job = new JSONObject();
		Date date = new Date();
		SimpleDateFormat requestTimestampformat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS");
		SimpleDateFormat invoiceDateformat = new SimpleDateFormat(
				"dd-MM-yyyy HH:mm:ss");
		String requestDateTimestamp = requestTimestampformat.format(date);
		SaleReturnTransactionIfc[] origTransaction = cargo
				.getOriginalReturnTransactions();
		MAXSaleReturnTransaction maxorigTrx = null;
		
		/*
		 * for (int i = 0; i < origTransaction.length; i++) { if (origTransaction[i]
		 * instanceof MAXSaleReturnTransaction) { maxorigTrx =(MAXSaleReturnTransaction)
		 * origTransaction[i]; TenderLineItemIfc[] tenderLineItems = maxorigTrx
		 * .getTenderLineItems();
		 * 
		 * for (int j = 0; j < tenderLineItems.length; j++) { TenderLineItemIfc lineItem
		 * = tenderLineItems[j]; array.add(lineItem.getTypeCodeString().toString()); } }
		 * 
		 * }
		 */
		 
		if (cargo != null
				&& ((MAXSaleReturnTransaction) cargo.getTransaction())
						.geteWalletTraceId() != null) {
			eWalletTraceId = ((MAXSaleReturnTransaction) cargo.getTransaction())
					.geteWalletTraceId().toString();
		}
		if (cargo.getTransaction().getCustomer() != null
				&& cargo.getTransaction().getCustomer() instanceof MAXCustomer) {
			customer = (MAXCustomer) cargo.getTransaction().getCustomer();
			mobileNo = customer.getPrimaryPhone().getPhoneNumber().toString();
		}
		if (cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
			maxTransaction = (MAXSaleReturnTransaction) cargo.getTransaction();
			invoiceNo = maxTransaction.getTransactionID().toString();
			invoiceDate = maxTransaction.getBusinessDay().toDate();
			formattedInvoiceDate = invoiceDateformat.format(invoiceDate);
			invoiceGrossAmt = maxTransaction.getTransactionTotals().getGrandTotal().abs().toString();
			invoiceNetAmt = maxTransaction.getTransactionTotals().getPreTaxSubtotal().abs().toString();

		}

		Map m1 = new LinkedHashMap(4);
		m1.put(MAXOxigenTenderConstants.REQUEST_TYPE,
				MAXOxigenTenderConstants.WALLET_CREDIT_REQUEST);
		m1.put(MAXOxigenTenderConstants.REQUEST_ID,
				invoiceNo+requestDateTimestamp);
		m1.put(MAXOxigenTenderConstants.REQUEST_TIME, requestDateTimestamp);
		m1.put(MAXOxigenTenderConstants.MOBILENUMBER, mobileNo);
		//m1.put(MAXOxigenTenderConstants.MOBILENUMBER, "9873477777");
		m1.put(MAXOxigenTenderConstants.ORIGINAL_DIALOGUE_TRACE_ID,
				eWalletTraceId);
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
		m1 = new LinkedHashMap(3);
		m1.put(MAXOxigenTenderConstants.OTP_TYPE,
				MAXOxigenTenderConstants.WALLET_CREDIT_REQUEST);
		if (cargo.getOtpRefNum()!=null) {
			m1.put(MAXOxigenTenderConstants.REF_NUM, cargo.getOtpRefNum());
		}
		else {
			m1.put(MAXOxigenTenderConstants.REF_NUM, null);	
		}
		m1.put(MAXOxigenTenderConstants.OTP, creditOtp);
		job.put(MAXOxigenTenderConstants.OTP_DETAILS, m1);
		m1 = new LinkedHashMap(4);
		m1.put(MAXOxigenTenderConstants.INVOICENO, invoiceNo);
		m1.put(MAXOxigenTenderConstants.INVOICEDATE, formattedInvoiceDate);
		m1.put(MAXOxigenTenderConstants.INVOICEGROSSAMOUNT, invoiceGrossAmt);
		m1.put(MAXOxigenTenderConstants.INVOICENETAMOUNT, invoiceNetAmt);
		m1.put(MAXOxigenTenderConstants.MODEOFPAYMENT, array);
		m1.put(MAXOxigenTenderConstants.PROMOCODE, promoCode);
		job.put(MAXOxigenTenderConstants.TRANSACTIONINFO, m1);
		job.put(MAXOxigenTenderConstants.AMOUNT, invoiceNetAmt);
		job.put(MAXOxigenTenderConstants.SUBWALLETTYPE,
				"CREDIT_NOTE");

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
		return response.toString();
	}

	private MAXOxigenWalletCreditResponse handleOxigenOtpResponse(
			String oxigenGeneratedOtpResponse) throws JsonParseException,
			JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		MAXOxigenWalletCreditResponse oxigenOtpResponse = mapper
				.readValue(oxigenGeneratedOtpResponse,
						MAXOxigenWalletCreditResponse.class);
		return oxigenOtpResponse;
	}

	/*
	 * if (otps.length() < 4 || otps.length() > 8) {
	 * dialogModel.setResourceID("FailureMessageScreen");
	 * dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
	 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
	 * CommonLetterIfc.RETRY);
	 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,
	 * CommonLetterIfc.CANCEL);
	 * uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel); }
	 * 
	 * if (otps.equalsIgnoreCase(cargo.getPaytmTotp())) {
	 * 
	 * dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENSUCCESS);
	 * dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
	 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Success");
	 * uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	 * return; }
	 * 
	 * else { dialogModel.setResourceID("FailureMessageScreen");
	 * dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
	 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
	 * CommonLetterIfc.RETRY);
	 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,
	 * CommonLetterIfc.CANCEL);
	 * uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	 * return;
	 * 
	 * } }
	 */
	// bus.mail(new Letter("TotpSuccess"), BusIfc.CURRENT);
	// logger.debug("AKS letter TotpSuccess fired");
	// */}
	// }

}