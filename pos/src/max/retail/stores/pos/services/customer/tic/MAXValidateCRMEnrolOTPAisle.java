/* ===========================================================================
 *  Copyright (c) 2019 Lifestyle India Pvt Ltd.    All Rights Reserved. 
 * ===========================================================================
 *
 * Rev 1.0  5th May 2019	Karni Singh POS REQ: Register CRM customer with OTP
 * Initial revision.
 *
 * ===========================================================================
 */
package max.retail.stores.pos.services.customer.tic;

import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.foundation.tour.gate.Gateway;

public class MAXValidateCRMEnrolOTPAisle extends PosLaneActionAdapter {

	private static final long serialVersionUID = -3118309612038386583L;

	@Override
	public void traverse(BusIfc bus) {
		
		
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel model = (POSBaseBeanModel) ui.getModel(MAXPOSUIManagerIfc.CRM_ENROLMENT_OTP_SCREEN);
		PromptAndResponseModel parModel = model.getPromptAndResponseModel();
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		int retryOTPAttemptsAllowed = 1;
		//try {
			retryOTPAttemptsAllowed = Integer.parseInt(Gateway.getProperty("application", "ReentryCountLMREnrolmentOtp","1"));
			//pm.getIntegerValue("LoyaltyReentryTimeOutInterval").intValue();
	//	} catch (ParameterException e) {
		//	e.printStackTrace();
		//}
		MAXCustomerMainCargo customerCargo = null;
		MAXSaleCargo saleCargo = null;
		String crmOTP = "";
		int otpRetriesUsed = 0;
		if(bus.getCargo() instanceof MAXCustomerMainCargo)
		{
		 customerCargo= (MAXCustomerMainCargo) bus.getCargo();
		  crmOTP = customerCargo.getCrmEnrolmentOTP();
		  otpRetriesUsed = customerCargo.getOtpRetries();
		}
		else if(bus.getCargo() instanceof MAXSaleCargo)
		{
			saleCargo= (MAXSaleCargo) bus.getCargo();
			crmOTP = saleCargo.getCrmEnrolmentOTP();
			otpRetriesUsed = saleCargo.getOtpRetries();
		}
		
		 
		String userEnteredOTP = parModel.getResponseText();
		if(crmOTP.equals(userEnteredOTP))
		{
			if(bus.getCargo() instanceof MAXCustomerMainCargo)
				customerCargo.setCRMEnrolmentOTPValidated(true);
			else if(bus.getCargo() instanceof MAXSaleCargo)
				saleCargo.setCRMEnrolmentOTPValidated(true);
			
			bus.mail("Success");
		}
		else if(retryOTPAttemptsAllowed >= otpRetriesUsed )
		{
			if(bus.getCargo() instanceof MAXCustomerMainCargo)
				customerCargo.setOtpRetries(otpRetriesUsed+1);
			else if(bus.getCargo() instanceof MAXSaleCargo)
				saleCargo.setOtpRetries(otpRetriesUsed+1);
			
			displayIncorrectOTPMessage(bus);
		}
		else
		{
			
			displayMaximumOTPAttempsReached(bus);
			/*if(bus.getCargo() instanceof MAXCustomerMainCargo)
			{
				customerCargo.setCrmEnrolmentOTP(null);
				customerCargo.setCRMEnrolmentOTPValidated(false);	
			}
			else if(bus.getCargo() instanceof MAXSaleCargo)
			{
				saleCargo.setCrmEnrolmentOTP(null);
				saleCargo.setCRMEnrolmentOTPValidated(false);	
			}				
			displayIncorrectOTPMessage(bus);*/
		}
		
}
	protected void displayIncorrectOTPMessage(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		DialogBeanModel beanModel = new DialogBeanModel();
		beanModel.setResourceID("InvalidCRMEnrolmentOTP");
		beanModel.setType(DialogScreensIfc.RETRY_CANCEL);
		beanModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, CommonLetterIfc.RETRY);
		beanModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.CANCEL);	    
    	ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
		
	}
	
	protected void displayMaximumOTPAttempsReached(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		DialogBeanModel beanModel = new DialogBeanModel();
		beanModel.setResourceID("OTPRetryLimitExceededLMREnrolment");
		beanModel.setType(DialogScreensIfc.YES_NO);
		beanModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.YES);
		beanModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.NO);	    
    	ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
		
	}
	
}
