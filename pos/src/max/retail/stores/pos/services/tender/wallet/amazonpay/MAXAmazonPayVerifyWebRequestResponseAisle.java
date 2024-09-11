/****************************************************************************************************
 *   
 *	Copyright (c) 2019 MAX SPAR Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	June 20 2019		Purushotham Reddy 	Changes for POS-Amazon Pay Integration 
 *
 ****************************************************************************************************/

package max.retail.stores.pos.services.tender.wallet.amazonpay;

/**
 @author Purushotham Reddy Sirison
 **/

import max.retail.stores.domain.MAXAmazonPayResponse;
import max.retail.stores.domain.arts.MAXPaytmDataTransaction;
import max.retail.stores.domain.tender.amazonpay.MAXAmazonPayTenderConstants;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXAmazonPayVerifyWebRequestResponseAisle extends
		LaneActionAdapter {
	public static int AMPYReEntryCount = 0;
	public static int AMPYNWErrorReTryCount = 0;
	public static int AMPYServerOfflineErrorReTryCount = 0;

	private static final long serialVersionUID = 1L;

	public void traverse(BusIfc bus) {

		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		
		String reEntryParam = Gateway.getProperty("application",
				"ReEntryCountForAmazonPayVerifyRequest", "");
		
		boolean isReentryMode = cargo.getRegister().getWorkstation()
				.isTransReentryMode();
	try {
			String url = Gateway.getProperty("application",
					"AmazonPayVerifyBarcodeChargeURL", "");
			
			MAXPaytmDataTransaction amazonTrans = new MAXPaytmDataTransaction();
			String amount = (String) cargo.getTenderAttributes().get(
					TenderConstants.AMOUNT);
			String tranId = cargo.getAmazonPayResp().getOrderId();
			MAXAmazonPayResponse response = null;
			boolean isSandBoxEnabled = false;
			ParameterManagerIfc pm = (ParameterManagerIfc) bus
					.getManager(ParameterManagerIfc.TYPE);
			try {
				isSandBoxEnabled = pm.getBooleanValue("IsSandBoxEnabled")
						.booleanValue();
			} catch (ParameterException e) {
				logger.warn("IsSandBoxEnabled Parameter does not exist in application.xml file");
			}
			if (!isReentryMode) {
				response = MAXAmazonPayHelperUtiltiy.verifyRequest(cargo, url,
						amount, isSandBoxEnabled, tranId);

				response.setUrl(url);

				if (response != null && response.getAmazonPayResponse() == null
						&& response.getReqRespStatus() == null
						&& response.getStatusCode() == null
						&& response.getStatusMessage() == null) {
					
					setResponseData(cargo, response);
					DialogBeanModel dialogModel = new DialogBeanModel();
					AMPYServerOfflineErrorReTryCount++;
					if(AMPYServerOfflineErrorReTryCount ==  Integer.parseInt(reEntryParam)){
						
						dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYSERVEROFFLINE);
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
								CommonLetterIfc.FAILURE);
						uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
								dialogModel);
						AMPYServerOfflineErrorReTryCount = 0;
					}
					else{
						dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYVERIFYPENDINGNW);
						dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
								"NWErrorReqPending");
						uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
								dialogModel);
					}
					return;
				} else if (response != null && response.isDataException()) {
					
					setResponseData(cargo, response);
					DialogBeanModel dialogModel = new DialogBeanModel();
					AMPYServerOfflineErrorReTryCount++;
					
					if(AMPYServerOfflineErrorReTryCount ==  Integer.parseInt(reEntryParam)){
						
						dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYSERVEROFFLINE);
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
								CommonLetterIfc.FAILURE);
						uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
								dialogModel);
						AMPYServerOfflineErrorReTryCount = 0;
					}
					else{
						dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYVERIFYPENDINGNW);
						dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
								"NWErrorReqPending");
						uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
								dialogModel);
					}
					return;
				}
				if ((response != null
						&& response.getStatus() != null
						&& response.getStatus().equalsIgnoreCase(
								MAXAmazonPayTenderConstants.SUCCESS))||(response != null
								&& response.getStatus() != null
								&& response.getStatus().equalsIgnoreCase(
										MAXAmazonPayTenderConstants.SUCCESS1))) {
					response.setAmountPaid(amount);
					response.setPhoneNumber(cargo.getAmazonPayPhoneNumber());
					setResponseData(cargo, response);
					response.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSERECEIVED);
					String amazonPayResp = response.getAmazonPayResponse();
					response.setRequestTypeA(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
					response.setAmazonPayResponse(response.getWalletTxnId()
							+ " : " + amazonPayResp);
					cargo.setAmazonPayResp(response);
					AMPYNWErrorReTryCount = 0;
					amazonTrans.saveAmazonPayVerifyRequest(response);

					DialogBeanModel dialogModel = new DialogBeanModel();
					String[] messgArray = new String[2];
					messgArray[0] = "Verify Request Successful!!";
					dialogModel.setArgs(messgArray);
					dialogModel
							.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYVERIFYSUCCESS);
					dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					messgArray[1] = response.getAmountPaid();
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
							"VerifyReqSuccess");
					uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
							dialogModel);
					return;
				} else if (response != null
						&& response.getStatus() != null
						&& response.getStatus().equalsIgnoreCase(
								MAXAmazonPayTenderConstants.PENDING)) {
					
					Integer amazonPayVerifyCount = Integer
							.parseInt(reEntryParam);
					response.setAmountPaid(amount);
					response.setPhoneNumber(cargo.getAmazonPayPhoneNumber());
					setResponseData(cargo, response);
					response.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSERECEIVED);
					String amazonPayResp = response.getAmazonPayResponse();
					response.setRequestTypeA(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
					response.setAmazonPayResponse(response.getWalletTxnId()
							+ " : " + amazonPayResp);
					cargo.setAmazonPayResp(response);
					amazonTrans.saveAmazonPayVerifyRequest(response);

					DialogBeanModel dialogModel = new DialogBeanModel();

					AMPYReEntryCount++;

					if (AMPYReEntryCount == amazonPayVerifyCount) {
						String[] messgArray = new String[1];
						messgArray[0] = "Payment not Confirmed..";
						dialogModel.setArgs(messgArray);
						dialogModel
								.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYERROR);
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
								CommonLetterIfc.FAILURE);
						uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
								dialogModel);
						AMPYReEntryCount = 0;
					} else {
						String[] messgArray = new String[2];
						messgArray[0] = "Verify Request Pending!!";
						dialogModel.setArgs(messgArray);
						dialogModel
								.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYVERIFYPENDING);
						dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
						messgArray[1] = response.getAmountPaid();
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
								"VerifyReqPending");
						uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
								dialogModel);
					}
					return;
				} else if (response != null
						&& response.getStatus() != null
						&& response.getStatus().equalsIgnoreCase(
								MAXAmazonPayTenderConstants.FAILURE)) {
				
					setResponseData(cargo, response);
					response.setAmountPaid("0.00");
					response.setRequestTypeA(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
					response.setRequestTypeB(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
					response.setReqRespStatus(MAXAmazonPayTenderConstants.FAILURESTATUS);
					amazonTrans.saveAmazonPayVerifyRequest(response);
					
					DialogBeanModel dialogModel = new DialogBeanModel();
					String[] messgArray = new String[1];
					messgArray[0] = response.getReasonDescription();
					dialogModel.setArgs(messgArray);
					dialogModel
							.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYPAYMENTFAILURE);
					dialogModel.setType(DialogScreensIfc.ERROR);
					uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
							dialogModel);
					return;
				} else if (response != null
						&& response.getStatusMessage() != null
						&& response
								.getStatusMessage()
								.equalsIgnoreCase(
										MAXAmazonPayTenderConstants.AMAZONPAYTIMEOUTERROR)) {
					setResponseData(cargo, response);
					response.setRequestTypeA(MAXAmazonPayTenderConstants.TIMEOUT);
					response.setReqRespStatus(MAXAmazonPayTenderConstants.TIMEOUT);

					DialogBeanModel dialogModel = new DialogBeanModel();
					dialogModel
							.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYTIMEOUTERROR);
					dialogModel.setType(DialogScreensIfc.ERROR);
					uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
							dialogModel);
					return;
				} else if (response != null
						&& response.getStatusMessage() != null
						&& response.getStatusMessage()
								.equalsIgnoreCase(
										MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR)) {
					setResponseData(cargo, response);
					AMPYNWErrorReTryCount++;
					response.setRequestTypeA(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
					DialogBeanModel dialogModel = new DialogBeanModel();
					
					response.setReqRespStatus(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
					
					if(AMPYNWErrorReTryCount ==  Integer.parseInt(reEntryParam))
					{
						String[] messgArray = new String[1];
						messgArray[0] = "Amazon Pay Server is not Reachable";
						dialogModel.setArgs(messgArray);
						dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
						dialogModel.setType(DialogScreensIfc.ERROR);
						uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
								dialogModel);
						AMPYNWErrorReTryCount=0;
					}else{
						dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYVERIFYPENDINGNW);
						dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
								"NWErrorReqPending");
						uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
								dialogModel);
					}
					
					return;
				} else if (response != null && response.getStatus() == null
						&& response.getReasonCode() != null
						&& response.getReasonCode().equalsIgnoreCase("04")) {

					setResponseData(cargo, response);
					amazonTrans.saveAmazonPayVerifyRequest(response);

					DialogBeanModel dialogModel = new DialogBeanModel();
					String[] messgArray = new String[1];
					messgArray[0] = "Request authentication failed because of invalid signature";
					dialogModel.setArgs(messgArray);
					dialogModel
							.setResourceID(MAXAmazonPayTenderConstants.TECHNICALISSUE);
					dialogModel.setType(DialogScreensIfc.ERROR);
					uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
							dialogModel);
					return;

				} else {
					
					DialogBeanModel dialogModel = new DialogBeanModel();
					String[] messgArray = new String[1];
					setResponseData(cargo, response);
					AMPYNWErrorReTryCount++;
					response.setRequestTypeA(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
					response.setReqRespStatus(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
					
					if(AMPYNWErrorReTryCount ==  Integer.parseInt(reEntryParam))
					{
						messgArray[0] = "Server is not Reachable";
						dialogModel.setArgs(messgArray);
						dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
						dialogModel.setType(DialogScreensIfc.ERROR);
						uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						AMPYNWErrorReTryCount=0;
					}else{
						dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYVERIFYPENDINGNW);
						dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
								"NWErrorReqPending");
						uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					}
					
					return;
				}
			}
		} catch (Exception e) {
			
			DialogBeanModel dialogModel = new DialogBeanModel();
			String[] messgArray = new String[1];
			messgArray[0] = "Error in calling Amazon Pay webservice\n";
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYERROR);
			dialogModel.setType(DialogScreensIfc.ERROR);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			logger.error("Error in calling Amazon Pay  webservice : " + e);

			return;

		}
		if (cargo.getRegister().getWorkstation().isTransReentryMode()) {
			bus.mail("Success");
		}
		
	}

	public void setResponseData(MAXTenderCargo cargo,
			MAXAmazonPayResponse response) {
		String connectionTimeout = Gateway.getProperty("application",
				"AmazonPayTimeOutInMilliSeconds", "");
		response.setStoreId(cargo.getStoreStatus().getStore().getStoreID());
		response.setRegisterId(cargo.getRegister().getWorkstation()
				.getWorkstationID());
		response.setTillId(cargo.getTillID());
		response.setPhoneNumber(cargo.getAmazonPayPhoneNumber());
		response.setBussinessdate(cargo.getStoreStatus().getBusinessDate());
		response.setTotalTransactionAmt(cargo.getTransaction()
				.getTransactionTotals().getSubtotal().toString());
		response.setRequestTypeB(MAXAmazonPayTenderConstants.BURNED);
		response.setTimeOut(connectionTimeout);
		response.setTransactionId(cargo.getCurrentTransactionADO()
				.getTransactionID());
		// response.setAmazonTransactionId(amazonTransactionId);
	}
}
