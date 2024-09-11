/********************************************************************************
 *   
 *	Copyright (c) 2019 MAX SPAR Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	Sep 10, 2019		Purushotham Reddy 	Changes for E-Receipt Integration With Karnival
 *
 ********************************************************************************/

package max.retail.stores.pos.services.printing;

/**
 @author Purushotham Reddy Sirison
 **/

import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.common.MAXCommonLetterIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXSendEReceiptDetailsAisle extends PosLaneActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static int EReceiptNetworkReEntryCount = 0;
	
	public static int EReceiptTimeoutReEntryCount = 0;
	
	String reEntryParam = Gateway.getProperty("application",
			"ReEntryCountForEReceiptSendRequest", "");

	public void traverse(BusIfc bus) {

		MAXPrintingCargo cargo = (MAXPrintingCargo) bus.getCargo();
		MAXSaleReturnTransaction trans = (MAXSaleReturnTransaction) cargo
				.getTransaction();
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		DataInputBeanModel model = (DataInputBeanModel) uiManager
				.getModel(MAXPOSUIManagerIfc.ERECEIPT_MOBILE_NUMBER_SCREEN);
		DialogBeanModel dialogModel = new DialogBeanModel();
		String[] messgArray = new String[1];
		
		String mobileNumber = (String) model.getValueAsString("MobileNumberField");

		MAXEReceiptResponse response = null;
		try{
			System.out.println("inside try ::"+mobileNumber);
		String url = Gateway.getProperty("application", "EReceiptSendURL", "");
		System.out.println("URL ::"+url);
		System.out.println("tran :"+trans);
		response = MAXEnableEReceiptHelperUtiltiy.sendRequest(trans, url, mobileNumber,"E-RECEIPT");
		response.setUrl(url);
		if (response.getOTP() != null) {
			trans.setEReceiptOTP(response.getOTP());
		}

		if (response != null && response.getSendResponse() == null
				&& response.getReqRespStatus() == null
				&& response.getStatusCode() == null
				&& response.getStatusMessage() == null) {
			
			dialogModel.setResourceID(MAXEReceiptRequestConstants.KARNIVALSERVEROFFLINE);
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
		}
		else if (response != null && response.isDataException()) {
			
			dialogModel.setResourceID(MAXEReceiptRequestConstants.KARNIVALSERVEROFFLINE);
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
		}

		else if (response != null
				&& response.getStatusMessage() != null
				&& response.getStatusMessage().equalsIgnoreCase(
						MAXEReceiptRequestConstants.KARNIVALTIMEOUTERROR)) {
			
			EReceiptTimeoutReEntryCount++;
			
			if(EReceiptTimeoutReEntryCount ==  Integer.parseInt(reEntryParam))
			{
				dialogModel.setResourceID(response.getStatusMessage());
				dialogModel.setType(DialogScreensIfc.ERROR);
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				EReceiptTimeoutReEntryCount = 0;
				
			}else{
				dialogModel.setResourceID(MAXEReceiptRequestConstants.ERECEIPTSENDREQPEDNING);
				dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"NWErrorReqPending");
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			}
			
			return;
			
		}
		else if (response != null
				&& response.getStatusMessage() != null
				&& response.getStatusMessage().equalsIgnoreCase(
						MAXEReceiptRequestConstants.KARNIVALNETWORKERROR)) {

			EReceiptNetworkReEntryCount++;
			
			if(EReceiptNetworkReEntryCount ==  Integer.parseInt(reEntryParam))
			{
				messgArray[0] = "E-Receipt Karnival Server is not Reachable";
				dialogModel.setArgs(messgArray);
				dialogModel.setResourceID(response.getStatusMessage());
				dialogModel.setType(DialogScreensIfc.ERROR);
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				EReceiptNetworkReEntryCount = 0;
				
			}else{
				dialogModel.setResourceID(MAXEReceiptRequestConstants.ERECEIPTSENDREQPEDNING);
				dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"NWErrorReqPending");
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			}
			
			return;
		
		}
		else if(response != null
				&& response.getMessage() != null
				&& response.getResponseCode()== 201 && response.getStatus().equalsIgnoreCase
				(MAXEReceiptRequestConstants.SUCCESS)){
			dialogModel.setResourceID(MAXEReceiptRequestConstants.ERECEIPTSENDSUCCESSFULLY);
			dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "PrintBakery");
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
		}
		else if(response != null
				&& response.getMessage() != null
				&& response.getStatus().equalsIgnoreCase("401")){
			messgArray[0] = response.getMessage();
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID(MAXEReceiptRequestConstants.ERECEIPTERROR);
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
		}
		else{
			EReceiptNetworkReEntryCount++;
			
			if(EReceiptNetworkReEntryCount ==  Integer.parseInt(reEntryParam))
			{
				messgArray[0] = "E-Receipt Karnival Server is not Reachable";
				dialogModel.setArgs(messgArray);
				dialogModel.setResourceID(response.getStatusMessage());
				dialogModel.setType(DialogScreensIfc.ERROR);
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				EReceiptNetworkReEntryCount = 0;
				
			}else{
				dialogModel.setResourceID(MAXEReceiptRequestConstants.ERECEIPTSENDREQPEDNING);
				dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"NWErrorReqPending");
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			}
			
			return;
			
		}
		
		}
		catch (Exception e) {
			
			messgArray[0] = "Error in calling E-Receipt webservice \n";
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID(MAXEReceiptRequestConstants.ERECEIPTERROR);
			dialogModel.setType(DialogScreensIfc.ERROR);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
					MAXCommonLetterIfc.PRINT);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			logger.error("Error in calling  E-Receipt  webservice : " + e);

			return;

		}
		
	}
}
