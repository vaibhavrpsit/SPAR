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

import max.retail.stores.domain.tender.MAXTenderStoreCreditIfc;
import max.retail.stores.pos.ado.tender.MAXTenderConstants;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
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

public class MAXWebReqForOTPSiteRedeemSc extends PosSiteActionAdapter  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus) {
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		String SClats4digits = null;
		
		//needs to changes
		MAXStoreCreditOTPResponse resp = new MAXStoreCreditOTPResponse();
		 RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
		MAXTenderStoreCreditIfc tscRedeem = (MAXTenderStoreCreditIfc) txnADO.getTenderStoreCreditIfcLineItem();
		int len = tscRedeem.getStoreCreditID().length();
		SClats4digits=  tscRedeem.getStoreCreditID().substring(len-4, len);	
	    String requestType = "Redemption";		
				HashMap tenderAttributes = cargo.getTenderAttributes();
				 String mobile = (String) tenderAttributes.get(MAXTenderConstants.mobileNumber);
				
						String storeId1 = Gateway.getProperty("application", "StoreID", (String) null);
						String storeId = storeId1.substring(0);
						MAXSCUtility utility = new MAXSCUtility();
					try {
						String  response=utility.GenerateOTPForSc(mobile, storeId, SClats4digits, requestType);
						if(response != null && response.equalsIgnoreCase("false")) {
							 DialogBeanModel dialogModel = new DialogBeanModel();	
							 POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
							dialogModel.setResourceID("GenerateOtpWebserviceNotWorking");
							dialogModel.setType(DialogScreensIfc.ERROR);
							dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.CANCEL);
							ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
							
						}
						else {
							cargo.setOtpForSc(response);
							bus.mail(new Letter("Success"), BusIfc.CURRENT);
							}
							
						
							}
						
						
						
					catch (JSONException e) {
					e.printStackTrace();
					}

	}

}