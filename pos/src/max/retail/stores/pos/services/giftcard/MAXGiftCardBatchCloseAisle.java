/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
    Rev 1.1  06/06/2013	Jyoti Rawal,      Fix for bug 5792 POS hangs on pressing enter while closing till 
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.giftcard;

import java.util.HashMap;

import com.qwikcilver.clientapi.svpos.GCPOS;

import max.retail.stores.pos.services.qc.MAXGiftCardUtilitiesQC;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXGiftCardBatchCloseAisle extends PosLaneActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 528958082937473289L;

	public void traverse(BusIfc bus) {

		boolean qcAllowed = false;

		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

		try {
			qcAllowed = pm.getBooleanValue("IsQCThirdAPICallAllowed").booleanValue();
		} catch (ParameterException e) {
			e.printStackTrace();
		}

		if (qcAllowed) {
			MAXGiftCardUtilitiesQC utilObj = new MAXGiftCardUtilitiesQC();
			GCPOS pos = utilObj.getInstance();
			if (pos != null) {
				POSBaseBeanModel model = new POSBaseBeanModel();
				POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
				ui.showScreen(MAXPOSUIManagerIfc.Gift_CARD_BATCH_CLOASE_SCREEN, model);

				HashMap balanceEnquiryMap = utilObj.batchClose(pos);

				if (balanceEnquiryMap.get("ResponseMessage") != null&& balanceEnquiryMap.get("ResponseCode").equals("0")) {
					DialogBeanModel dialogModel = new DialogBeanModel();
					String msg[] = new String[7];
					dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
					msg[0] = "<<--||--:: Please Find The Details As Below ::--||-->>";
					msg[1] = "Batch Close Requested";
					msg[2] = "Current Batch Number :: " + balanceEnquiryMap.get("CurrentBatchNumber").toString();
					msg[3] = balanceEnquiryMap.get("ResponseMessage").toString();
					msg[4] = "Request  Sent:  Press button To Proceed";
					msg[5] = "Batch closed";
					msg[6] = "::Thanks::";
					dialogModel.setArgs(msg);
					dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "NoIssue");

					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

				}else if(balanceEnquiryMap.get("ResponseMessage")!=null&& balanceEnquiryMap.get("ResponseMessage").toString().indexOf("No route to host")!= -1){
					showQCOfflineErrorBoxForBatchClose(bus);  //Rev 1.1 changes
				}
				else if(balanceEnquiryMap.get("ResponseMessage") != null&& !balanceEnquiryMap.get("ResponseCode").equals("0")){
					DialogBeanModel dialogModel = new DialogBeanModel();
					String msg[] = new String[7];
					dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
					msg[0] = "<<--||--:: Please Find The Details As Below ::--||-->>";
					msg[1] = " ";
					msg[2] = "There are no Gift Card transactions done in this batch" ;
					msg[3] = "Please press Enter button to proceed further";
					msg[4] = "";
					msg[5] = "";
					msg[6] = "";
					dialogModel.setArgs(msg);
					dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "NoIssue");
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				}
			} else {
				utilObj.showQCOfflineErrorBox(bus);
			}

		} else {
			bus.mail(new Letter("NoIssue"), BusIfc.CURRENT);
		}
	}
	/**
	 * Rev 1.1 changes
	 * @param bus
	 */
	public void showQCOfflineErrorBoxForBatchClose(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		String msg[] = new String[7];
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
		msg[0] = " ";
		msg[1] = "Gift Card Batch Close can not be completed";
		msg[2] = " due to network offline";   //" Total Amount In This Card Is " + balanceEnquiryMap.get("Amount");
		msg[4] = " ";
		msg[5] = " ";
		msg[6] = " ";
		dialogModel.setArgs(msg);
		
		bus.getCurrentLetter().getName();
		if(bus.getCurrentLetter().getName().equalsIgnoreCase("Retry")){
			dialogModel.setType(DialogScreensIfc.ERROR);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
			msg[3] = "Press Enter button To proceed further";
		}
		else{
			dialogModel.setType(DialogScreensIfc.RETRY);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Retry");
			msg[3] = "Press Enter button To retry again";
		}
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

}
