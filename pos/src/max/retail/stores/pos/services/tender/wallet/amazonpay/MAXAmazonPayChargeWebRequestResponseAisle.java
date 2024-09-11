/********************************************************************************
 *   
 *	Copyright (c) 2019 MAX SPAR Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	June 01, 2019		Purushotham Reddy 	Changes for POS-Amazon Pay Integration 
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.wallet.amazonpay;

import com.octetstring.ldapv3.SubstringFilter_substrings;

/**
@author Purushotham Reddy Sirison
**/

import max.retail.stores.domain.MAXAmazonPayResponse;
import max.retail.stores.domain.arts.MAXPaytmDataTransaction;
import max.retail.stores.domain.tender.amazonpay.MAXAmazonPayTenderConstants;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXAmazonPayChargeWebRequestResponseAisle extends
		LaneActionAdapter {
	private static final long serialVersionUID = 1L;

	public void traverse(BusIfc bus) {
		
	POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
	MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();

	boolean isReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
	try 
	{
		String url = Gateway.getProperty("application", "AmazonPayChargeBarcodeURL", "");
		MAXPaytmDataTransaction amazonTrans = new MAXPaytmDataTransaction();
		String amount = (String) cargo.getTenderAttributes().get(TenderConstants.AMOUNT);
		MAXAmazonPayResponse response=null;
		
		boolean isSandBoxEnabled= false;
		
		ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        try{
        	isSandBoxEnabled = pm.getBooleanValue("IsSandBoxEnabled").booleanValue();
        }
        catch (ParameterException e){
          logger.warn("IsSandBoxEnabled Parameter does not exist in application.xml file");
        }
	
		if(!isReentryMode)
		{
		try {
			response = MAXAmazonPayHelperUtiltiy.chargeRequest(cargo, url, amount, isSandBoxEnabled);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		response.setUrl(url);
		
		if(response != null && response.getAmazonPayResponse()==null && response.getReqRespStatus()==null &&
				response.getStatusCode()==null && response.getStatusMessage()==null)
		{	
			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYSERVEROFFLINE);
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			
			return;
		}else if(response != null && response.isDataException())
		{	
			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYSERVEROFFLINE);
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			
			return;
		}
		if((response != null && response.getStatus() != null && response.getReasonCode()!=null && !response.getReasonCode().equalsIgnoreCase("04") &&   
				response.getStatus().equalsIgnoreCase(MAXAmazonPayTenderConstants.SUCCESS)) ||(response != null && response.getStatus() != null && 
						response.getStatus().equalsIgnoreCase(MAXAmazonPayTenderConstants.SUCCESS1)) )
		{
			response.setAmountPaid(amount);
			response.setPhoneNumber(cargo.getAmazonPayPhoneNumber());
			setResponseData(cargo, response);
			response.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSERECEIVED);
			String amazonPayResp = response.getAmazonPayResponse();
			response.setRequestTypeA(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			response.setAmazonPayResponse(response.getWalletTxnId() + " : " + amazonPayResp);
			cargo.setAmazonPayResp(response);
			
			try {
				amazonTrans.saveAmazonPayChargeRequest(response);
			} catch (DataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			DialogBeanModel dialogModel = new DialogBeanModel();
			String[] messgArray = new String[2];
			messgArray[0] = "Charge Request Successful!!";
			dialogModel.setArgs(messgArray); 
			dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYCHARGESUCCESS);
			dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
			messgArray[1]=response.getAmountPaid();
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "ChargeReqSuccess");
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
		}
		else if(response != null && response.getStatusMessage() != null && 
				response.getStatusMessage().equalsIgnoreCase(MAXAmazonPayTenderConstants.AMAZONPAYTIMEOUTERROR))
			{
			setResponseData(cargo, response);
			response.setRequestTypeA(MAXAmazonPayTenderConstants.TIMEOUT);
			if(response.getAmountPaid() == null || 
					response.getAmountPaid().equals("null") || 
					response.getAmountPaid().equals(null)){
				response.setAmountPaid("0.00");
			}
			response.setReqRespStatus(MAXAmazonPayTenderConstants.TIMEOUT);
			
			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID(response.getStatusMessage());
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		}
		else if(response != null && response.getStatus() != null && 
				response.getReasonCode()!=null && response.getReasonCode().equalsIgnoreCase("04")){
			
			DialogBeanModel dialogModel = new DialogBeanModel();
			String[] messgArray = new String[1];
			messgArray[0] = "Request authentication failed because of invalid signature";
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID(MAXAmazonPayTenderConstants.TECHNICALISSUE);
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
			
		}
		
		else if(response != null && response.getStatus() == null && 
				response.getReasonCode()!=null && response.getReasonCode().equalsIgnoreCase("02-10-01")){
			
			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONACCOUNTNOTEXIST);
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
			
		}
		else if(response != null && response.getStatus() == null && 
				response.getReasonCode()!=null && response.getReasonCode().equalsIgnoreCase("07")){
			
			DialogBeanModel dialogModel = new DialogBeanModel();
			String[] messgArray = new String[1];
			messgArray[0] = "Duplicate request. First request is already accepted.";
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYDUPLICATEERROR);
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
		}
		else if(response != null && response.getStatus() == null && 
				response.getReasonCode()==null && response.getResponseCode()!=0 ){
			
			DialogBeanModel dialogModel = new DialogBeanModel();
			String[] messgArray = new String[1];
			if(response.getResponseCode()==400 ) {
			//	messgArray[0] = "Charge could not be processed due to insufficient funds";
				messgArray[0] = response.getReasonDescription();
				
			}else {
			messgArray[0] = "No customer found with given barcode";
			}
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYDUPLICATEERROR);
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
		}
		else if (response != null && response.getStatus() == null && 
				response.getReasonCode()==null && response.getResponseCode()==0){
			DialogBeanModel dialogModel = new DialogBeanModel();
			String[] messgArray = new String[1];
			messgArray[0] = "Amazon Pay Server is not Reachable";
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
		}
		}
		} catch (Exception e) {
			logger.error("Error in calling Amazon Pay Charge request : "+ e.getMessage());
			
			DialogBeanModel dialogModel = new DialogBeanModel();
			String[] messgArray = new String[1];
			messgArray[0] = "Error in calling Amazon Pay Charge request\n";
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYERROR);
			dialogModel.setType(DialogScreensIfc.ERROR);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Undo");
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
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
	}
}
