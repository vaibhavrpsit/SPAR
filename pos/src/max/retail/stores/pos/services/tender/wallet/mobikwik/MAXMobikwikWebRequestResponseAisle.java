package max.retail.stores.pos.services.tender.wallet.mobikwik;

import max.retail.stores.domain.MAXMobikwikResponse;
import max.retail.stores.domain.arts.MAXPaytmDataTransaction;
import max.retail.stores.domain.tender.mobikwik.MAXMobikwikTenderConstants;
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

public class MAXMobikwikWebRequestResponseAisle extends LaneActionAdapter

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
	String mobileNumber=cargo.getMobikwikMobileNo();
	String totp=cargo.getMobikwikTotp();
	// cadded by atul shukla for bug Id 18568
	boolean isReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
	
	try 
	{
		
		String uri = Gateway.getProperty("application", "MobikwikWithdrawURL", "");

		String amount = (String) cargo.getTenderAttributes().get(TenderConstants.AMOUNT);
		MAXMobikwikResponse response=null;
	
		if(!isReentryMode)
		{
		    	 
					
		//MAXPaytmResponse 
		response = MAXMobikwikHelperUtiltiy.withdrawAmount(cargo,uri,cargo.getMobikwikMobileNo(),cargo.getMobikwikTotp(), 
				cargo.getCurrentTransactionADO().getTransactionID(), amount, cargo.getTillID(), cargo.getStoreStatus().getStore().getStoreID());
		//System.out.println("Rsponse="+uri.toString() +""+response.getAmountPaid()+ "" + response.getPhoneNumber()+" " + response.getResponseCode());
		response.setUrl(uri);
		if(response != null && response.getMobikwikResponse()==null && response.getReqRespStatus()==null && response.getStatusCode()==null && response.getStatusMessage()==null )
		{	
			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID("MobikwikServerOffline");
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			
			return;
		}else if(response != null && response.isDataException())
		{	
			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID("MobikwikServerOffline");
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			
			return;
		}
		if(response != null && response.getStatus() != null && 
				response.getStatus().equalsIgnoreCase(MAXMobikwikTenderConstants.SUCCESS))
		
				
		{	
			response.setAmountPaid(amount);
			response.setPhoneNumber(cargo.getMobikwikMobileNo());
			setResponseData(cargo, response);
			response.setReqRespStatus(MAXMobikwikTenderConstants.RESPONSERECEIVED);
			
			String mobikwikResp = response.getMobikwikResponse();
			response.setMobikwikResponse(response.getWalletTxnId() + " : " + mobikwikResp);
			response.setRequestTypeA(MAXMobikwikTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			cargo.setMobikwikResp(response);
			// Bhanu Priya Gupta Changes Starts
			
		  // MAXSaleReturnTransaction tnd= (MAXSaleReturnTransaction) cargo.getTransaction();
		  // tnd.setPaytmResponse(response);
		   //tnd.setPaytmResponse(response);;
		// Bhanu Priya Gupta Changes Ends
			MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
			//ArrayList paytmResponse = tnd.getPaytmResponse();
			//List  paytmResponse = tnd.getPaytmResponse();
			//System.out.println("++++++++++++++++++++++++++++"+paytmResponse);
			paytmTrans.saveMobikwikRequest(response);
		//	bus.mail("Success");
			// code added by atul shukla for dialog screen
			DialogBeanModel dialogModel = new DialogBeanModel();
			String[] messgArray = new String[2];
			if(response.getStatus()!= null)
			{
				messgArray[0] = response.getStatus();
			}
			else
			{
				messgArray[0] = "Error in getting response from Mobikwik";
			}
			dialogModel.setArgs(messgArray); 
		//	dialogModel.setResourceID(MAXMobikwikTenderConstants.PAYTMERROR);
		//	dialogModel.setResourceID("PaytmError");
			dialogModel.setResourceID(MAXMobikwikTenderConstants.MOBIKWIKSUCCESS);
			dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
			//String amt=response.getAmountPaid();
			messgArray[1]=response.getAmountPaid();
			//dialogModel.setArgs(new String[] = {amt});
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Success");
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		//	bus.mail("Success");
			return;
		}
		else if(response != null && ((response.getStatus() != null && response.getStatus().equalsIgnoreCase(MAXMobikwikTenderConstants.FAILURE) || 
				response.getStatusCode() != null)))
		{
			setResponseData(cargo, response);
			response.setRequestTypeA(MAXMobikwikTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			if(response.getResponseCode() == 408)
			{
				response.setReqRespStatus(MAXMobikwikTenderConstants.TIMEOUT);
			}
			else
			{
				response.setReqRespStatus(MAXMobikwikTenderConstants.RESPONSERECEIVED);
			}
			if(response.getAmountPaid() == null || 
					response.getAmountPaid().equals("null") || 
					response.getAmountPaid().equals(null))
			{
				response.setAmountPaid("0.00");
			}
			response.setMobikwikResponse(" : " + response.getStatusMessage());
			cargo.setMobikwikResp(response);
			MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
			paytmTrans.saveMobikwikRequest(response);
			DialogBeanModel dialogModel = new DialogBeanModel();
			String[] messgArray = new String[1];
			// below condition is added by atul shukla for bug ID 18557
			String ReentryCountTotp1 = Gateway.getProperty("application", "ReentryCountTotpParameter", "");
			if(response.getStatusMessage() != null && response.getStatusCode().equals("120"))
					//response.getStatusMessage().equalsIgnoreCase("Invalid Otp."))
				//messgArray[0] = response.getStatusMessage();
			messgArray[0] = "The Entered Mobile Number is Not Valid ";
			else if(response.getStatusMessage() != null && response.getStatusCode().equals("164"))
			{
				TOTPRenentryCount++;
				messgArray[0] = "The Entered TOTP is Not Valid ";
			}
				else if(response.getStatusMessage() != null && response.getStatusCode().equals("33"))
				{
					messgArray[0] = "Payment declined due to insufficient balance";
				}
				else
				{
					messgArray[0] = "Error in getting response from Mobikwik";
				}
		String ReentryCountTotp = Gateway.getProperty("application", "ReentryCountTotpParameter", "");
		Integer ReentryCountTotp11=Integer.parseInt(ReentryCountTotp);

			if(ReentryCountTotp11==TOTPRenentryCount)
			{
				messgArray[0] = "TOTP Reentry Count Exceeded ";
				dialogModel.setArgs(messgArray); 
					dialogModel.setResourceID(MAXMobikwikTenderConstants.MOBIKWIKERROR);
					dialogModel.setType(DialogScreensIfc.ERROR);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
					uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					TOTPRenentryCount=0;
					return;
			}
	// reentry count code end here
			dialogModel.setArgs(messgArray); 
			dialogModel.setResourceID(MAXMobikwikTenderConstants.MOBIKWIKERROR);
			dialogModel.setType(DialogScreensIfc.ERROR);
			
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			
			return;
		}
		else if(response != null && response.getStatusMessage() != null && 
				response.getStatusMessage().equalsIgnoreCase(MAXMobikwikTenderConstants.MOBIKWIKTIMEOUTERROR))
				//(response.getStatusMessage().equalsIgnoreCase(MAXMobikwikTenderConstants.NETWORKERROR) || 
						//response.getStatusMessage().equalsIgnoreCase(MAXMobikwikTenderConstants.MOBIKWIKTIMEOUTERROR)))
		{
			setResponseData(cargo, response);
			response.setRequestTypeA(MAXMobikwikTenderConstants.TIMEOUT);
			if(response.getAmountPaid() == null || 
					response.getAmountPaid().equals("null") || 
					response.getAmountPaid().equals(null))
			{
				response.setAmountPaid("0.00");
			}
			response.setReqRespStatus(MAXMobikwikTenderConstants.TIMEOUT);
		//	MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
		//	paytmTrans.saveMobikwikRequest(response);
			
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
			dialogModel.setResourceID(MAXMobikwikTenderConstants.MOBIKWIKERROR);
			dialogModel.setType(DialogScreensIfc.ERROR);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		logger.error("Error in setting paytm webservice calling " + e.getMessage());
		bus.mail("failure");  // cadded by atul shukla for bug Id 18568
	}
	if(cargo.getRegister().getWorkstation().isTransReentryMode())
	{
	bus.mail("Success");
	}
}

public void setResponseData(MAXTenderCargo cargo, MAXMobikwikResponse response)
{
	response.setStoreId(cargo.getStoreStatus().getStore().getStoreID());
	response.setRegisterId(cargo.getRegister().getWorkstation().getWorkstationID());
	response.setTillId(cargo.getTillID());
	response.setPhoneNumber(cargo.getMobikwikMobileNo());
	response.setBussinessdate(cargo.getStoreStatus().getBusinessDate());
	response.setTotalTransactionAmt(cargo.getTransaction().getTransactionTotals().getSubtotal().toString());
	response.setRequestTypeB(MAXMobikwikTenderConstants.BURNED);
	response.setTimeOut(MAXMobikwikConfig.get(MAXMobikwikTenderConstants.CONNECTIONTIMEOUT));
	response.setTransactionId(cargo.getCurrentTransactionADO().getTransactionID());
}
}

	
//}
