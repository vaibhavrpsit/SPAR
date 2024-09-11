/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 *	Rev 1.0			30 Oct 2017			Jyoti Yadav				Changes for Innoviti Integration CR
 *	
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender;

import java.util.HashMap;

import max.retail.stores.pos.services.edc.CallingOnlineDebitCardTender;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.TenderBeanModel;

public class MAXPostEDCTenderEnquiryAisle extends PosLaneActionAdapter {
	private static int responseCode;

	public void traverse(BusIfc bus) {

		CallingOnlineDebitCardTender edcClassObj = new CallingOnlineDebitCardTender();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		TenderBeanModel model = (TenderBeanModel) ui.getModel();
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();

		ui.showScreen(MAXPOSUIManagerIfc.EDC_VOID_SCREEN, model);

		HashMap responseMap = new HashMap();
		String trxId = null;

		if (cargo.getTransaction() != null) {
			if (cargo.getTransaction().getTransactionID() != null)
				trxId = cargo.getTransaction().getTransactionID();
			try {
				responseMap = edcClassObj.makePostVoidEDC(trxId, null, null, null, "13", "9");
				edcClassObj.printChargeSlipData(bus, responseMap);

				showDialogBoxMethod(responseMap, bus);

			} catch (Exception e) {

			}

			if (responseMap.get("HostResponseApprovalCode") == null && responseMap.get("CardNumber") == null
					&& responseMap.get("HostResponseRetrievelRefNumber") == null)
				bus.mail(new Letter("EDCPostVoidedFail"), BusIfc.CURRENT);
		}
	}

	public void showDialogBoxMethod(HashMap responseMap, BusIfc bus) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		String msg[] = new String[25];
		dialogModel.setResourceID("RESPONSE_DETAILS_POST_VOID");

		for (int j = 0; j < 20; j++) {
			if (responseMap.get("PrintLine" + j) != null)
				msg[j] = responseMap.get("PrintLine" + j).toString();
			else
				msg[j] = "";
		}

		dialogModel.setArgs(msg);
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "EDCPostVoidedFail");
		// Again returning to Org Tender screen
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
}
