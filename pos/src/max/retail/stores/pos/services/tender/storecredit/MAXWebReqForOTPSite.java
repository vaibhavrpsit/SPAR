/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *
 *	Rev 1.0 	May 14, 2024			Kamlesh Pant		Store Credit OTP:
 *
 ********************************************************************************/


package max.retail.stores.pos.services.tender.storecredit;

import java.util.HashMap;
import org.codehaus.jettison.json.JSONException;

import max.retail.stores.domain.customer.MAXCaptureCustomer;
import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.tender.MAXTenderStoreCreditIfc;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.ado.tender.MAXTenderStoreCreditADO;
//import max.retail.stores.pos.services.pricing.MAXEasyExchangeUtility;
//import max.retail.stores.pos.services.pricing.employeediscount.MAXEmployeeDiscountOTPResponse;
//import max.retail.stores.pos.services.returns.returnfindtrans.MAXReturnTransactionReturnShuttle;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.domain.customer.CaptureCustomer;
//import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXWebReqForOTPSite extends PosSiteActionAdapter {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	public void arrive(BusIfc bus) {
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		HashMap<String, Object> tenderAttributes = cargo.getTenderAttributes();
		//needs to changes
		MAXStoreCreditOTPResponse resp = new MAXStoreCreditOTPResponse();
		String SClats4digits = null;
		//getting the customer details from customer capture screen model
		 MAXCustomer customer = (MAXCustomer)cargo.getCustomer();
		 String phnNumber=null;
		 String customerogMobile = null;
		 String custMobileForOTP = null;
		 String requestType = "Issuance";
		 RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
		    MAXTenderStoreCreditIfc tscRedeem = (MAXTenderStoreCreditIfc) txnADO.getTenderStoreCreditIfcLineItem();
		    SClats4digits=  tscRedeem.getStoreCreditID().substring(14, 18);
		 if(cargo.getTransaction() instanceof MAXSaleReturnTransaction){
				MAXSaleReturnTransaction trans=(MAXSaleReturnTransaction) cargo.getTransaction();
				if(cargo.getTransaction().getCustomer()!=null) {
				 phnNumber=cargo.getTransaction().getCustomer().getPhoneList().get(0).getPhoneNumber();
				}
			     customerogMobile=trans.getCustOgMobile();
			     if (customerogMobile != null)
			    	 if (customerogMobile.equalsIgnoreCase(""))
			    	 custMobileForOTP = phnNumber;
			    	 else
			    		 custMobileForOTP = customerogMobile;
			     else
			    	 custMobileForOTP = phnNumber;
			     
			    
			     			     //Used to save the mobile number in do_cr_str tables
			     MAXTenderStoreCreditADO storeCreditTender = (MAXTenderStoreCreditADO)cargo.getTenderADO();
				    storeCreditTender.setMobileNumber(phnNumber);
				    storeCreditTender.createStoreCredit().getStoreCreditID();
				    trans.setCustMobileforOTP(custMobileForOTP);
				   
		 }
		 else {
				if(cargo.getTransaction() instanceof MAXLayawayTransaction)
			    {
				MAXLayawayTransaction trans=(MAXLayawayTransaction) cargo.getTransaction();
				if(cargo.getTransaction().getCustomer()!=null) {
					 phnNumber=cargo.getTransaction().getCustomer().getPhoneList().get(0).getPhoneNumber();
					}
			     customerogMobile=trans.getCustOgMobile();
			     if (customerogMobile != null)
			    	 custMobileForOTP = customerogMobile;
			     else
			    	 custMobileForOTP = phnNumber;
			     MAXTenderStoreCreditADO storeCreditTender = (MAXTenderStoreCreditADO)cargo.getTenderADO();
				    storeCreditTender.setMobileNumber(phnNumber);
				    trans.setCustMobileforOTP(custMobileForOTP);
			      }
			}
		 String storeId1 = Gateway.getProperty("application", "StoreID", (String) null);
			String storeId = storeId1.substring(0);
			MAXSCUtility utility = new MAXSCUtility();
		try {
			
			String  response=utility.GenerateOTPForSc(custMobileForOTP, storeId, SClats4digits, requestType);
			if(response != null && response.equalsIgnoreCase("false")) {
				 DialogBeanModel dialogModel = new DialogBeanModel();	
				 POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
				dialogModel.setResourceID("GenerateOtpWebserviceNotWorking");
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.CANCEL);
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				
			}
			else {
				if(cargo.getTransaction() instanceof MAXSaleReturnTransaction){
				MAXSaleReturnTransaction trans=(MAXSaleReturnTransaction) cargo.getTransaction();
				trans.setScOtp(response);
				cargo.setOtpRetries(0);
				bus.mail(new Letter("Success"), BusIfc.CURRENT);
				}
				else {
					if(cargo.getTransaction() instanceof MAXLayawayTransaction)
				{
					MAXLayawayTransaction trans=(MAXLayawayTransaction) cargo.getTransaction();
					trans.setScOtp(response);
					cargo.setOtpRetries(0);
					bus.mail(new Letter("Success"), BusIfc.CURRENT);
				}
				}
			}
			
			cargo.setOtpForSc(response);
		} catch (JSONException e) {
		e.printStackTrace();
		}
	
	}

}