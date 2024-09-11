package max.retail.stores.pos.services.tender.wallet.paytm;

//import max.retail.stores.domain.MAXPaytmResponse;
import max.retail.stores.domain.arts.MAXPaytmDataTransaction;
import max.retail.stores.domain.MAXPaytmResponse;
import max.retail.stores.domain.tender.paytm.MAXPaytmTenderConstants;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXPaytmWebRequestResponseAisle extends LaneActionAdapter

{
	//added by atul shukla for re-entry totp count
	public static  int TOTPRenentryCount=0;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void traverse(BusIfc bus) {
		
	
	POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
	//POSBaseBeanModel posBase = (POSBaseBeanModel) uiManager.getModel(LSIPLPOSUIManagerIfc.PAYTM_ENTER_OTP);
	MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
	/*String mobileNumber=cargo.getPaytmPhoneNumber();
	String totp=cargo.getPaytmTotp();*/
	// cadded by atul shukla for bug Id 18568
	boolean isReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
	
	try 
	{
		
		String uri = Gateway.getProperty("application", "PaytmWithdrawURL", "");

		String amount = (String) cargo.getTenderAttributes().get(TenderConstants.AMOUNT);
		MAXPaytmResponse response=null;
		
		//MAXPaytmResponse response = MAXPaytmHelperUtiltiy.withdrawAmount(uri, cargo.getPhoneNumber(), cargo.getOtp(), 
				//cargo.getCurrentTransactionADO().getTransactionID(), amount, cargo.getTillID(), cargo.getStoreStatus().getStore().getStoreID());
		//String firstPhoneNumber=cargo.getCurrentTransactionADO().getCustomer().
		//MAXTenderPaytmIfc tenderPaytm=(MAXTenderPaytmIfc) cargo.getCurrentTransactionADO().toLegacy();
		
		//TenderLineItemIfc[] tenderPaytm=cargo.getTransaction().getTenderLineItems();
		//[0].g
		if(!isReentryMode)
		{
		//MAXPaytmResponse 
		response = MAXPaytmHelperUtiltiy.withdrawAmount(cargo,uri,cargo.getPaytmPhoneNumber(),cargo.getPaytmTotp(), 
				cargo.getCurrentTransactionADO().getTransactionID(), amount, cargo.getTillID(), cargo.getStoreStatus().getStore().getStoreID());
		//System.out.println("Rsponse="+uri.toString() +""+response.getAmountPaid()+ "" + response.getPhoneNumber()+" " + response.getResponseCode());
		response.setUrl(uri);
		if(response != null && response.getPaytmResponse()==null && response.getReqRespStatus()==null && response.getStatusCode()==null && response.getStatusMessage()==null)
		{	
			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID("ServerOffline");
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			
			return;
		}else if(response != null && response.isDataException())
		{	
			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID("ServerOffline");
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			
			return;
		}
		if(response != null && response.getStatus() != null && 
				response.getStatus().equalsIgnoreCase(MAXPaytmTenderConstants.SUCCESS))
		{	
			response.setAmountPaid(amount);
			response.setPhoneNumber(cargo.getPaytmPhoneNumber());
			setResponseData(cargo, response);
			response.setReqRespStatus(MAXPaytmTenderConstants.RESPONSERECEIVED);
			
			String paytmResp = response.getPaytmResponse();
			response.setPaytmResponse(response.getWalletTxnId() + " : " + paytmResp);
			response.setRequestTypeA(MAXPaytmTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			cargo.setPaytmResp(response);
			// Bhanu Priya Gupta Changes Starts
			
		  // MAXSaleReturnTransaction tnd= (MAXSaleReturnTransaction) cargo.getTransaction();
		  // tnd.setPaytmResponse(response);
		   //tnd.setPaytmResponse(response);;
		// Bhanu Priya Gupta Changes Ends
			MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
			//ArrayList paytmResponse = tnd.getPaytmResponse();
			//List  paytmResponse = tnd.getPaytmResponse();
			//System.out.println("++++++++++++++++++++++++++++"+paytmResponse);
			paytmTrans.saveRequest(response);
		//	bus.mail("Success");
			// code added by atul shukla for dialog screen
			DialogBeanModel dialogModel = new DialogBeanModel();
			String[] messgArray = new String[2];
			if(response.getStatusMessage() != null)
				messgArray[0] = response.getStatusMessage();
			else
				messgArray[0] = "Error in getting response from Paytm";
			dialogModel.setArgs(messgArray); 
		//	dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMERROR);
		//	dialogModel.setResourceID("PaytmError");
			dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMSUCCESS);
			dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
			//String amt=response.getAmountPaid();
			messgArray[1]=response.getAmountPaid();
			//dialogModel.setArgs(new String[] = {amt});
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Success");
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		//	bus.mail("Success");
			return;
		}
		else if(response != null && ((response.getStatus() != null && response.getStatus().equalsIgnoreCase(MAXPaytmTenderConstants.FAILURE) || 
				response.getStatusCode() != null)))
		{
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
			response.setPaytmResponse(" : " + response.getStatusMessage());
			cargo.setPaytmResp(response);
			MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
			paytmTrans.saveRequest(response);
			DialogBeanModel dialogModel = new DialogBeanModel();
			String[] messgArray = new String[1];
			// below condition is added by atul shukla for bug ID 18557
			//String ReentryCountTotp1 = Gateway.getProperty("application", "ReentryCountTotpParameter", "");
			if(response.getStatusMessage() != null && response.getStatusCode().equals("404"))
					//response.getStatusMessage().equalsIgnoreCase("Invalid Otp."))
				//messgArray[0] = response.getStatusMessage();
			messgArray[0] = "The Entered Mobile Number is Not Valid ";
			else if(response.getStatusMessage() != null && response.getStatusCode().equals("403"))
			{
				TOTPRenentryCount++;
				messgArray[0] = "The Entered TOTP is Not Valid ";
			}
				else if(response.getStatusMessage() != null && response.getStatusCode().equals("WM_1006"))
				{
					messgArray[0] = "Payment declined due to insufficient balance";
				}
				else if(response.getStatusMessage() != null && response.getStatusCode().equals("RWL_0002"))
				{
					messgArray[0] = response.getStatusMessage();
				}
				else
				{
					messgArray[0] = "Error in getting response from Paytm";
				}
		String ReentryCountTotp = Gateway.getProperty("application", "ReentryCountTotpParameter", "");
		Integer ReentryCountTotp11=Integer.parseInt(ReentryCountTotp);

			if(ReentryCountTotp11==TOTPRenentryCount)
			{
				messgArray[0] = "TOTP Reentry Count Exceeded ";
				dialogModel.setArgs(messgArray); 
					dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMERROR);
					dialogModel.setType(DialogScreensIfc.ERROR);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
					uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					TOTPRenentryCount=0;
					return;
			}
	// reentry count code end here
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMERROR);
			dialogModel.setType(DialogScreensIfc.ERROR);
			
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			
			return;
		}
		else if(response != null && response.getStatusMessage() != null && 
				response.getStatusMessage().equalsIgnoreCase(MAXPaytmTenderConstants.PAYTMTIMEOUTERROR))
				//(response.getStatusMessage().equalsIgnoreCase(MAXPaytmTenderConstants.NETWORKERROR) || 
					//	response.getStatusMessage().equalsIgnoreCase(MAXPaytmTenderConstants.PAYTMTIMEOUTERROR)))
		{
			setResponseData(cargo, response);
			response.setRequestTypeA(MAXPaytmTenderConstants.TIMEOUT);
			if(response.getAmountPaid() == null || 
					response.getAmountPaid().equals("null") || 
					response.getAmountPaid().equals(null))
			{
				response.setAmountPaid("0.00");
			}
			response.setReqRespStatus(MAXPaytmTenderConstants.TIMEOUT);
		//	MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
		//	paytmTrans.saveRequest(response);
			
			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID(response.getStatusMessage());
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		}
		else
		{
			DialogBeanModel dialogModel = new DialogBeanModel();
			String[] messgArray = new String[1];
			//messgArray[0] = "Error in sending request to Paytm";
			// changes made for bug Id 16567
						messgArray[0] = "Server is not Reachable";
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMERROR);
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		logger.error("Error in setting paytm webservice calling " + e.getMessage());
		//bus.mail("failure");  // cadded by atul shukla for bug Id 18568
	}
	if(cargo.getRegister().getWorkstation().isTransReentryMode())
	{
	bus.mail("Success");
	}
}

public void setResponseData(MAXTenderCargo cargo, MAXPaytmResponse response)
{
	response.setStoreId(cargo.getStoreStatus().getStore().getStoreID());
	response.setRegisterId(cargo.getRegister().getWorkstation().getWorkstationID());
	response.setTillId(cargo.getTillID());
	response.setPhoneNumber(cargo.getPaytmPhoneNumber());
	response.setBussinessdate(cargo.getStoreStatus().getBusinessDate());
	response.setTotalTransactionAmt(cargo.getTransaction().getTransactionTotals().getSubtotal().toString());
	response.setRequestTypeB(MAXPaytmTenderConstants.BURNED);
	response.setTimeOut(MAXPaytmConfig.get(MAXPaytmTenderConstants.CONNECTIONTIMEOUT));
	response.setTransactionId(cargo.getCurrentTransactionADO().getTransactionID());
}
}

	
//}
