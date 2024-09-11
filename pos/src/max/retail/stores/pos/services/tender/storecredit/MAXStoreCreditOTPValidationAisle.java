/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *
 *	Rev 1.0 	May 14, 2024			Kamlesh Pant		Store Credit OTP:
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.storecredit;

import max.retail.stores.domain.arts.MAXConfigParameterTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.utility.MAXConfigParametersIfc;
///import max.retail.stores.pos.services.pricing.MAXPricingCargo;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXStoreCreditOTPValidationAisle extends PosLaneActionAdapter {

	
	private static final long serialVersionUID = 1L;
	
	public void traverse(BusIfc bus) {
	
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel model=(POSBaseBeanModel)uiManager.getModel(MAXPOSUIManagerIfc.ENTER_SC_OTP);
		PromptAndResponseModel promptResponseModel=model.getPromptAndResponseModel();
		String otp = promptResponseModel.getResponseText();
	  
		promptResponseModel.setResponseText("");
		 DialogBeanModel dialogModel = new DialogBeanModel();	
			
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
	
		String scOtp=null;
		int retryOTPAttemptsAllowed = Integer.valueOf(getAllConfigparameter().getScOtpRetries());
		
		int otpRetriesUsed = 0;
		if(bus.getCargo() instanceof MAXTenderCargo)
		{
			//Gets the value of otp retries already used from cargo
			otpRetriesUsed = cargo.getOtpRetries();
		}
		if(cargo.getTransaction() instanceof MAXSaleReturnTransaction){
			scOtp=cargo.getOtpForSc(); 
	       
		}
		else if (cargo.getTransaction() instanceof MAXLayawayTransaction)
			{
				 scOtp=cargo.getOtpForSc(); 
				 
			}
		else {
			scOtp=cargo.getOtpForSc();
		}
			
		if(otp!=null && otp.equalsIgnoreCase(scOtp))
		{	
		
		bus.mail(new Letter("Success"), BusIfc.CURRENT);
		}
		else if(retryOTPAttemptsAllowed != otpRetriesUsed )
 		{
			
					 
       	 if(bus.getCargo() instanceof MAXTenderCargo) 
    		  cargo.setOtpRetries(otpRetriesUsed+1);
	       
	
			if(retryOTPAttemptsAllowed == cargo.getOtpRetries()) {
       	 bus.mail(new Letter("Again"), BusIfc.CURRENT);
			}
			else{
		    	 
		    	 dialogModel.setResourceID("FailureMessageScreen");
				 dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
				 dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, CommonLetterIfc.RETRY);
		         dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.CANCEL);
		         uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		        
		     }
		}
	   
	 }
	
	private MAXConfigParametersIfc getAllConfigparameter() {

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
