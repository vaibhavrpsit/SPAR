/********************************************************************************
 *   
 *	Copyright (c) 2019 MAX SPAR Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	Sep 05, 2019		Purushotham Reddy 	Changes for E-Receipt Integration With Karnival
 *
 ********************************************************************************/

package max.retail.stores.pos.services.printing;

/**
@author Karni Singh
**/

import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.receipt.blueprint.MAXParameterConstantsIfc;
import max.retail.stores.pos.services.common.MAXCommonLetterIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.domain.utility.PhoneIfc;

public class MAXSendEReceiptDataSite extends PosSiteActionAdapter{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus)
    {
        MAXPrintingCargo cargo = (MAXPrintingCargo) bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        //TenderableTransactionIfc trans = cargo.getTransaction();
		
		MAXSaleReturnTransaction trans = (MAXSaleReturnTransaction) cargo.getTransaction();
        
        boolean eReceiptConfig = false;
        try{
        	eReceiptConfig = pm.getBooleanValue(MAXParameterConstantsIfc.ENABLEERECEIPT);
        }
        catch (ParameterException e){
            logger.error("Could not find the parameter "+ MAXParameterConstantsIfc.ENABLEERECEIPT +" : ", e);
        }

        if (eReceiptConfig && trans.getTransactionType()== TransactionIfc.TYPE_SALE && !trans.isTrainingMode()){
        	String mobileNumber = "";
    if ((cargo.getTransaction().getCustomer() != null) && 
      (cargo.getTransaction().getCustomer().getContact().getPhoneList().size() >= 1)) {
      PhoneIfc phone = (PhoneIfc)cargo.getTransaction().getCustomer().getContact().getPhoneList().get(0);
      mobileNumber = phone.getPhoneNumber();
	  MAXEReceiptResponse response = null;
		try{
		String url = Gateway.getProperty("application", "EReceiptSendURL", "");
		response = MAXEnableEReceiptHelperUtiltiy.sendRequest(trans, url, mobileNumber,"PAPER-BILL");
		response.setUrl(url);
			if (response != null && response.getOTP() != null) {
				trans.setEReceiptOTP(response.getOTP()+"-PB");
				System.out.println("OTP"+response.getOTP());
			}else{
				trans.setEReceiptOTP("ERROR");
			}
		}catch(Exception e){
			bus.mail(MAXCommonLetterIfc.PRINT, BusIfc.CURRENT);
		}
		
        }
		bus.mail(MAXCommonLetterIfc.PRINT, BusIfc.CURRENT);
		}
        else {
            bus.mail(MAXCommonLetterIfc.PRINT, BusIfc.CURRENT);
        }
       
    }
}
