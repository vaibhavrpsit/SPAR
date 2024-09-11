/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved.
 *
 *	Rev 1.0			05 Mar 2017			Kritica Agarwal			Changes for Innoviti Integration CR
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.HashMap;
import java.util.Map;

import org.apache.derby.impl.sql.catalog.SYSROUTINEPERMSRowFactory;

import max.retail.stores.pos.services.edc.CallingOnlineDebitCardTender;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXStatusEnquirySite extends PosSiteActionAdapter {

	private static final long serialVersionUID = 7439344167607141766L;

	public void arrive(BusIfc bus) {
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		HashMap responseMap = null;
		CallingOnlineDebitCardTender edcObj = new CallingOnlineDebitCardTender();
		try {
			responseMap = edcObj.statusEnquiry();

			if (responseMap != null) {
				String hostResponseCode = (String) responseMap.get("HostResponseCode");
				//System.out.println(hostResponseCode);
				if (hostResponseCode.equalsIgnoreCase("00")) {
					//System.out.println("Entering showDialogBoxMethod ");
					//System.out.println(responseMap);
					showDialogBoxMethod(responseMap, bus, "Next");
					//System.out.println("exiting showDialogBoxMethod");
				} else {
					showErrorDialogBoxMethod(responseMap, bus, "Invalid");
					//bus.mail(new Letter("Invalid"), BusIfc.CURRENT);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}

	public void showDialogBoxMethod(Map responseMap, BusIfc bus, String buttonLetter) {
		try{
			//System.out.println("IN showDialogBoxMethod ");
		DialogBeanModel dialogModel = new DialogBeanModel();
		String msg[] = new String[16];
		dialogModel.setResourceID("STATUS_ENQUIRY");
		msg[0] = responseMap.get("MerchantName").toString();
		msg[1] = responseMap.get("MerchantAddress").toString();
		msg[2] = responseMap.get("MerchantCity").toString();
		//msg[3] = "DATE     :  " + responseMap.get("TransactionTime").toString().substring(0,10)+"         ";
		if (responseMap.get("TransactionTime")!=null){
			msg[3] = "DATE     :  " + responseMap.get("TransactionTime").toString().substring(0,10)+"         ";
		    msg[4] = "TIME     :  " + responseMap.get("TransactionTime").toString().substring(11, 19);
		
		}else{
			msg[3]="DATE :         ";
			msg[4]="TIME :         ";
		}
		msg[5] = " MID     :  " + responseMap.get("MerchantID").toString();
		msg[6] = "TID        :  " + responseMap.get("StateTID").toString();
		msg[7] = "BATCH    :  " + responseMap.get("StateBatchNumber").toString()+"             ";
		msg[8] = "INVOICE  :  " + responseMap.get("StateInvoiceNumber").toString();
		if (responseMap.get("TransactionType")!=null){
	      msg[9] = responseMap.get("TransactionType").toString();
		}else{
			msg[9]="        ";
		}
		msg[10] = "CARD NUM : " + responseMap.get("CardNumber").toString();
		msg[11] = "CARD TYPE : " + responseMap.get("SchemeType").toString();
		msg[12] = "APPR CODE : " + responseMap.get("HostResponseApprovalCode").toString();
		msg[13] = "RRN NO :";
		msg[14] = "AMOUNT :Rs. " + responseMap.get("StateAmount").toString();
		msg[15] = "Press ENTER To Proceed";
		//System.out.println("press enter");
		//System.out.println(msg);
		dialogModel.setArgs(msg);
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, buttonLetter);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		//bus.mail(new Letter("Next"), BusIfc.CURRENT);
		//System.out.println("out showDialogBoxMethod ");
		}catch(Exception e){
			bus.mail(new Letter("Next"), BusIfc.CURRENT);
			//System.out.println("OUT second showDialogBoxMethod ");
		  //  System.out.println(e);
		}
	}
	public void showErrorDialogBoxMethod(Map responseMap, BusIfc bus, String buttonLetter) {
		try{
		DialogBeanModel dialogModel = new DialogBeanModel();
		String msg[] = new String[4];
		dialogModel.setResourceID("STATUS_ENQUIRY_ERROR");
		msg[0]=" Transaction not found ";
		msg[1]="                       ";
		msg[2]="                       ";
		/*msg[0] = responseMap.get("HostResponseCode").toString();
		msg[1] = responseMap.get("HostResponseMessage").toString();
		msg[2] = responseMap.get("StateStatusMessage").toString();*/
		msg[3] = "Press ENTER To Proceed";
		dialogModel.setArgs(msg);
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, buttonLetter);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		}catch(Exception e){
			logger.info(e.getMessage());
		}
	}

}
