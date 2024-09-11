/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2016-2017 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *     
 * Rev 1.0 		Apr 11,2017		Nadia Arora (EYLLP)   Paytm Integration
 * Initial revision.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit.paytmqr;

import Paytm.QrDisplay; 
import max.retail.stores.domain.arts.MAXConfigParameterTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXPaytmDataTransaction;
import max.retail.stores.domain.paytm.MAXPaytmQRCodeResponse;
import max.retail.stores.domain.MAXPaytmResponse;
import max.retail.stores.domain.tender.paytm.MAXPaytmTenderConstants;
import max.retail.stores.domain.tender.paytmqr.MAXPaytmQRCodeTenderConstants;
import max.retail.stores.domain.utility.MAXConfigParametersIfc;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.domain.arts.ARTSTill;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXCheckStatusQRCodeAisle extends LaneActionAdapter{

	private static final long serialVersionUID = -292973898915948786L;
	
	public static int paytmReEntryCount = 0;
	public static int paytmNWErrorReTryCount = 0;
	public static int paytmServerOfflineErrorReTryCount = 0;

	public void traverse(BusIfc bus) {
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		
		try {
			//Create connection
			MAXConfigParametersIfc configParam=getConfigparameter();
			//int a = configParam.getPaytmQRStatusCheckRetryCount();
			//System.out.println("configParam 49:::"+a);
			//System.out.println("configParam 50:::"+configParam.getPaytmQRStatusCheckRetryCount());
			
			String uri = Gateway.getProperty("application", "transactionStatusPaytmQRCodeURL", "");
			
			MAXPaytmQRCodeResponse response = MAXPaytmQRCodeHelperUtiltiy.checkTransactionStatus(uri, cargo.getPaytmQRCodeResp().getOrderId(), cargo.getTillID(), cargo.getStoreStatus().getStore().getStoreID());
			
			MAXPaytmResponse PaytmResponse = null;
			
			if(response != null && response.isDataException())
			{	
				DialogBeanModel dialogModel = new DialogBeanModel();
				dialogModel.setResourceID("ServerOffline");
				dialogModel.setType(DialogScreensIfc.ERROR);
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				
				return;
			}
			if(response != null && response.getResultStatus() != null && 
					response.getResultStatus().equalsIgnoreCase("TXN_SUCCESS"))
			{	
				//response.setAmountPaid(amount);
				//response.setPhoneNumber(cargo.getPhoneNumber());
				setResponseData(cargo, response);
				response.setReqRespStatus(MAXPaytmTenderConstants.RESPONSERECEIVED);
				
				String paytmResp = response.getPaytmResponse();
				response.setPaytmResponse(response.getOrderId() + " : " + paytmResp);
				response.setRequestTypeA(MAXPaytmTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
				cargo.setPaytmQRCodeResp(response);
				MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
				PaytmResponse = convertPaytmResponseData(response);
				PaytmResponse.setUrl(uri);
				paytmTrans.saveRequest(PaytmResponse);
				QrDisplay qrDisplay = new QrDisplay(); 
				String mid = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.MID);
				String port = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DEVICEPORT);
				int baudRate = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.BAUDRATE));
				int parity = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PARITY));
				int dataBits = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DATABITS));
				int stopBits = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.STOPBITS));
				int debugMode = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DEBUGMODE));
				
				Boolean sucessScreen=qrDisplay.showSuccessScreen(mid, port,baudRate, parity, dataBits, stopBits, response.getOrderId(), response.getAmountPaid(),"INR",debugMode,response.getRegisterId());
				bus.mail("CheckStatusSuccess");
					return;
				
			}else if(response != null && ((response.getResultStatus() != null && response.getResultStatus().equalsIgnoreCase("PENDING"))))
			{
				if(response.getAmountPaid() == null || 
						response.getAmountPaid().equals("null") || 
						response.getAmountPaid().equals(null))
				{
					response.setAmountPaid("0.00");
				}
				paytmReEntryCount++;
				setResponseData(cargo, response);
					response.setPaytmResponse(" : " + response.getPaytmResponse());
				//	cargo.setPaytmResp(response);
					cargo.setPaytmQRCodeResp(response);
					MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
					PaytmResponse = convertPaytmResponseData(response);
					PaytmResponse.setUrl(uri);
					paytmTrans.saveRequest(PaytmResponse);
					//System.out.println("configParam.getPaytmQRStatusCheckRetryCount()"+configParam.getPaytmQRStatusCheckRetryCount());
					if(paytmReEntryCount == configParam.getPaytmQRStatusCheckRetryCount()) {
						DialogBeanModel dialogModel = new DialogBeanModel();
						String[] messgArray = new String[1];
						
						messgArray[0] = "Payment Status not confirmed yet.";
						dialogModel.setArgs(messgArray);
						dialogModel.setResourceID(MAXPaytmQRCodeTenderConstants.PAYTMQRCODEPENDINGERROR);
						dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Ok");
						uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						paytmReEntryCount = 0;
						QrDisplay qrDisplay = new QrDisplay(); 
						String mid = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.MID);
						String port = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DEVICEPORT);
						int baudRate = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.BAUDRATE));
						int parity = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PARITY));
						int dataBits = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DATABITS));
						int stopBits = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.STOPBITS));
						int debugMode = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DEBUGMODE));
						
						Boolean sucessScreen=qrDisplay.showHomeScreen(mid, port,baudRate, parity, dataBits, stopBits, debugMode,response.getRegisterId());
					
					//	bus.mail("failure");
					}else {
						DialogBeanModel dialogModel = new DialogBeanModel();
						String[] messgArray = new String[1];
						if(response.getResultMessage() != null)
							messgArray[0] = response.getResultMessage();
						else
							messgArray[0] = "Payment not confirmed by bank.";
						dialogModel.setArgs(messgArray);
						dialogModel.setResourceID(MAXPaytmQRCodeTenderConstants.PAYTMQRCODEPENDING);
						dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
								"CheckStatusReqPending");
						uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					//	bus.mail("CheckStatusReqPending");
					}
				return;
			}
			else if(response != null && ((response.getResultStatus() != null && response.getResultStatus().equalsIgnoreCase("TXN_FAILURE") || 
					response.getResultCode() != null)))
			{
				if(response.getAmountPaid() == null || 
						response.getAmountPaid().equals("null") || 
						response.getAmountPaid().equals(null))
				{
					response.setAmountPaid("0.00");
				}
				setResponseData(cargo, response);
				response.setRequestTypeA(MAXPaytmTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
				if(response.getResponseCode() == 408)
				{
					response.setReqRespStatus(MAXPaytmTenderConstants.TIMEOUT);
				}
				else
				{
					response.setReqRespStatus(MAXPaytmTenderConstants.RESPONSERECEIVED);
				}
				if(response.getAmountPaid() == null || 
						response.getAmountPaid().equals("null") || 
						response.getAmountPaid().equals(null))
				{
					response.setAmountPaid("0.00");
				}
				response.setPaytmResponse(" : " + response.getPaytmResponse());
			//	cargo.setPaytmResp(response);
				cargo.setPaytmQRCodeResp(response);
				MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
				PaytmResponse = convertPaytmResponseData(response);
				PaytmResponse.setUrl(uri);
				paytmTrans.saveRequest(PaytmResponse);
				DialogBeanModel dialogModel = new DialogBeanModel();
				String[] messgArray = new String[1];
				if(response.getResultMessage() != null)
					messgArray[0] = response.getResultMessage();
				else
					messgArray[0] = "Failure response received from paytm server.";
				dialogModel.setArgs(messgArray);
				dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMERROR);
				dialogModel.setType(DialogScreensIfc.ERROR);
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				QrDisplay qrDisplay = new QrDisplay(); 
				String mid = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.MID);
				String port = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DEVICEPORT);
				int baudRate = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.BAUDRATE));
				int parity = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PARITY));
				int dataBits = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DATABITS));
				int stopBits = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.STOPBITS));
				int debugMode = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DEBUGMODE));
				
				Boolean sucessScreen=qrDisplay.showHomeScreen(mid, port,baudRate, parity, dataBits, stopBits, debugMode,response.getRegisterId());
			
				return;
			}
			else if(response != null && response.getResultMessage() != null && 
					(response.getResultMessage().equalsIgnoreCase(MAXPaytmTenderConstants.NETWORKERROR) || 
							response.getResultMessage().equalsIgnoreCase(MAXPaytmTenderConstants.PAYTMTIMEOUTERROR)))
			{
				paytmReEntryCount++;
				setResponseData(cargo, response);
				response.setRequestTypeA(MAXPaytmTenderConstants.TIMEOUT);
				if(response.getAmountPaid() == null || 
						response.getAmountPaid().equals("null") || 
						response.getAmountPaid().equals(null))
				{
					response.setAmountPaid("0.00");
				}
				response.setReqRespStatus(MAXPaytmTenderConstants.TIMEOUT);
				cargo.setPaytmQRCodeResp(response);
				MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
				PaytmResponse = convertPaytmResponseData(response);
				PaytmResponse.setUrl(uri);
				paytmTrans.saveRequest(PaytmResponse);
				
			DialogBeanModel dialogModel = new DialogBeanModel();
				
				String[] messgArray = new String[1];
				messgArray[0] = response.getResultMessage();
				if(paytmReEntryCount == configParam.getPaytmQRStatusCheckRetryCount()){
					//DialogBeanModel dialogModel = new DialogBeanModel();
					dialogModel.setResourceID(MAXPaytmQRCodeTenderConstants.PAYTMQRCODEPENDINGERROR);
					dialogModel.setArgs(messgArray);
					dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"failure");
					paytmReEntryCount = 0;
					QrDisplay qrDisplay = new QrDisplay(); 
					String mid = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.MID);
					String port = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DEVICEPORT);
					int baudRate = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.BAUDRATE));
					int parity = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PARITY));
					int dataBits = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DATABITS));
					int stopBits = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.STOPBITS));
					int debugMode = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DEBUGMODE));
					
					Boolean sucessScreen=qrDisplay.showHomeScreen(mid, port,baudRate, parity, dataBits, stopBits, debugMode,response.getRegisterId());
				
				}else {
					dialogModel.setArgs(messgArray);
					dialogModel.setResourceID(MAXPaytmQRCodeTenderConstants.PAYTMQRCODEPENDING);
					dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
							"CheckStatusReqPending");
				}
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				return;
			}
			else
			{
				paytmReEntryCount++;
				DialogBeanModel dialogModel = new DialogBeanModel();
				String[] messgArray = new String[1];
				messgArray[0] = "Error in sending request to Paytm";
				
				if(paytmReEntryCount == configParam.getPaytmQRStatusCheckRetryCount()){
					dialogModel.setArgs(messgArray);
					dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMERROR);
					dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"failure");
					paytmReEntryCount = 0;
					QrDisplay qrDisplay = new QrDisplay(); 
					String mid = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.MID);
					String port = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DEVICEPORT);
					int baudRate = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.BAUDRATE));
					int parity = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PARITY));
					int dataBits = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DATABITS));
					int stopBits = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.STOPBITS));
					int debugMode = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DEBUGMODE));
					
					Boolean sucessScreen=qrDisplay.showHomeScreen(mid, port,baudRate, parity, dataBits, stopBits, debugMode,response.getRegisterId());
				
				}else {
					dialogModel.setArgs(messgArray);
					dialogModel.setResourceID(MAXPaytmQRCodeTenderConstants.PAYTMQRCODEPENDING);
					dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
							"CheckStatusReqPending");
					
					//dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"CheckStatusReqPending");
				}
				
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in setting paytm webservice calling " + e.getMessage());
			bus.mail("failure");
		}
	}
	
	public void setResponseData(MAXTenderCargo cargo, MAXPaytmQRCodeResponse response)
	{
		response.setStoreId(cargo.getStoreStatus().getStore().getStoreID());
		response.setRegisterId(cargo.getRegister().getWorkstation().getWorkstationID());
		response.setTillId(cargo.getTillID());
	//	response.setPhoneNumber(cargo.getPhoneNumber());
		response.setBussinessdate(cargo.getStoreStatus().getBusinessDate());
		response.setTotalTransactionAmt(cargo.getTransaction().getTransactionTotals().getSubtotal().toString());
		response.setRequestTypeB("S");
		response.setTimeOut(MAXPaytmQRCodeConfig.get(MAXPaytmTenderConstants.CONNECTIONTIMEOUT));
		response.setTransactionId(cargo.getCurrentTransactionADO().getTransactionID());
	}
	
	
	public MAXPaytmResponse convertPaytmResponseData(MAXPaytmQRCodeResponse response)
	{
		MAXPaytmResponse paytmResponse = new MAXPaytmResponse();
		paytmResponse.setStoreId(response.getStoreId());
		paytmResponse.setRegisterId(response.getRegisterId());
		paytmResponse.setTillId(response.getTillId());
		paytmResponse.setPhoneNumber(null);
		paytmResponse.setBussinessdate(response.getBussinessdate());
		paytmResponse.setTotalTransactionAmt(response.getTotalTransactionAmt());
		paytmResponse.setRequestTypeB("S");
		paytmResponse.setTimeOut(MAXPaytmQRCodeConfig.get(MAXPaytmTenderConstants.CONNECTIONTIMEOUT));
		paytmResponse.setTransactionId(response.getTransactionId());		
		paytmResponse.setOrderId(response.getOrderId());
		paytmResponse.setRequestTypeA("R");
		paytmResponse.setReqRespStatus(response.getReqRespStatus());
		paytmResponse.setRequestTypeB(response.getRequestTypeB());		
		paytmResponse.setTimeOut(response.getTimeOut());
		paytmResponse.setPaytmResponse(response.getPaytmResponse());
		paytmResponse.setAmountPaid(response.getAmountPaid());
		return paytmResponse;
		
	}
	
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
	
	

}
