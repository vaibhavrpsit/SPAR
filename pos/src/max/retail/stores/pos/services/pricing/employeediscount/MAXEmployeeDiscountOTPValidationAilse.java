package max.retail.stores.pos.services.pricing.employeediscount;

import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.pricing.MAXPricingCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
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


public class MAXEmployeeDiscountOTPValidationAilse extends PosLaneActionAdapter {

	/**
	 * @author kajal nautiyal Employee Discount validation through OTP
	 */
	private static final long serialVersionUID = 1L;
	
	public void traverse(BusIfc bus) {
	
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel model=(POSBaseBeanModel)uiManager.getModel(MAXPOSUIManagerIfc.ENTER_OTP);
		PromptAndResponseModel promptResponseModel=model.getPromptAndResponseModel();
		String otp = promptResponseModel.getResponseText();
	  
		promptResponseModel.setResponseText("");
		 DialogBeanModel dialogModel = new DialogBeanModel();	
			
		MAXPricingCargo cargo = (MAXPricingCargo) bus.getCargo();
	
		String EmployeeDiscOtp=null;
		int retryOTPAttemptsAllowed = 3;
		int otpRetriesUsed = 0;
		if(bus.getCargo() instanceof MAXPricingCargo)
		{
			
			otpRetriesUsed = cargo.getOtpRetries();
		}
		if(cargo.getTransaction() instanceof MAXSaleReturnTransaction){
	        EmployeeDiscOtp=((MAXSaleReturnTransaction) cargo.getTransaction()).getEmplyoeeDicsOtp(); 
	       
		}
		else {
			if(cargo.getTransaction() instanceof MAXLayawayTransaction)
			{
				 EmployeeDiscOtp=((MAXLayawayTransaction) cargo.getTransaction()).getEmplyoeeDicsOtp(); 
				 
			}
		}
	
			
			
		
		if(otp!=null && otp.equalsIgnoreCase(EmployeeDiscOtp))
		{	
		//System.out.println("final otp"+EmployeeDiscOtp);
			if(cargo.getTransaction() instanceof MAXSaleReturnTransaction){
		        ((MAXSaleReturnTransaction) cargo.getTransaction()).setEmployeeOtpValidated(true);
		       
			}
			else {
				if(cargo.getTransaction() instanceof MAXLayawayTransaction)
				{
					((MAXLayawayTransaction) cargo.getTransaction()).setEmployeeOtpValidated(true);
					 
				}
			}	
		bus.mail(new Letter("OtpSucessfull"), BusIfc.CURRENT);
		}
		else if(retryOTPAttemptsAllowed != otpRetriesUsed )
 		{
			if(cargo.getTransaction() instanceof MAXSaleReturnTransaction){
		        ((MAXSaleReturnTransaction) cargo.getTransaction()).setEmployeeOtpValidated(false);
		       
			}
			else {
				if(cargo.getTransaction() instanceof MAXLayawayTransaction)
				{
					((MAXLayawayTransaction) cargo.getTransaction()).setEmployeeOtpValidated(false);
					 
				}
			}	
       	 if(bus.getCargo() instanceof MAXPricingCargo) 
    		  cargo.setOtpRetries(otpRetriesUsed+1);
	       
	
			if(retryOTPAttemptsAllowed== cargo.getOtpRetries()) {
       	 bus.mail(new Letter("MaximumOTPAttempsReached"), BusIfc.CURRENT);
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
	}


