/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0     Jan 06, 2016		Ashish Yadav		Online Points Redemption FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.loyaltypoints;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXOTPRequestResponseInfoAisle extends PosLaneActionAdapter{

	/**
	 * This site will use for enter otp code  with timeout Interval parametrer
	 */
	private static final long serialVersionUID = 3726730763969968049L;

	public void traverse(BusIfc bus)
	{  
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel model=(POSBaseBeanModel)uiManager.getModel(MAXPOSUIManagerIfc.ENTER_OTP);
		PromptAndResponseModel promptResponseModel=model.getPromptAndResponseModel();
		String otps = promptResponseModel.getResponseText();
		int otp = Integer.parseInt(otps);
		promptResponseModel.setResponseText("");
		TenderCargo cargo=(TenderCargo) bus.getCargo();	
		MAXCustomerIfc customer=null;
		if(cargo.getCurrentTransactionADO().toLegacy() instanceof SaleReturnTransaction )
		{
			SaleReturnTransaction transaction= ((SaleReturnTransaction) cargo.getCurrentTransactionADO().toLegacy());
			customer=(MAXCustomerIfc) transaction.getCustomer();

		}
		else if( cargo.getCurrentTransactionADO().toLegacy() instanceof LayawayPaymentTransaction)
		{
			LayawayPaymentTransaction transaction=((LayawayPaymentTransaction)cargo.getCurrentTransactionADO().toLegacy());

			customer=(MAXCustomerIfc) transaction.getCustomer();	

		}
		else{
			SaleReturnTransaction transaction= ((SaleReturnTransaction) cargo.getCurrentTransactionADO().toLegacy());
			customer=(MAXCustomerIfc) transaction.getCustomer();
		}
		int otpcode=customer.getLoyaltyotp();
		  DialogBeanModel dialogModel = new DialogBeanModel();	
		if(otp!=0&&otp==otpcode)
		{	
		customer.setOtpValidation(true);
		bus.mail(new Letter("OtpSucessfull"), BusIfc.CURRENT);
		}
	
		else
			{   
			Integer timeOut = new Integer(0);
			int timeoutinterval=0;
			int previousinterval=0;
			ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
			try {
				 timeOut = pm.getIntegerValue("LoyaltyReentryTimeOutInterval");
			} catch (ParameterException e) {
				e.printStackTrace();
			}
		
			timeoutinterval=timeOut.intValue();
			previousinterval=customer.getLoyaltyRetryTimeout();
		     if(timeoutinterval>=previousinterval){
		    	 customer.setLoyaltyRetryTimeout(previousinterval+1);
			 dialogModel.setResourceID("FailureMessageScreen");
			 dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
			 dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, CommonLetterIfc.FAILURE);
	         dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.CANCEL);
	         uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		   }
		     else{
		    	 bus.mail(new Letter("Cancel"), BusIfc.CURRENT);
		    	  
		     }
		     
		   }

	}
}
  