/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Aug 21, 2018		Bhanu Priya		Changes for Capture PAN CARD CR
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.pancard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;

public class MAXCustomerTypeWithPANCardAisle extends PosLaneActionAdapter {

	
	private static final long serialVersionUID = 1L;

	public void traverse(BusIfc bus)
 {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		JournalManagerIfc jmi = (JournalManagerIfc) bus
				.getManager(JournalManagerIfc.TYPE);
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		// Changes done By Purushotham
		MAXSaleReturnTransaction maxSaleReturnTransaction = (MAXSaleReturnTransaction) cargo
				.getTransaction();
		POSBaseBeanModel model = (POSBaseBeanModel) ui
				.getModel(MAXPOSUIManagerIfc.PANCARD_NUMBER_CAPTURE);
		PromptAndResponseModel pAndRModel = model.getPromptAndResponseModel();
		String panNumber = pAndRModel.getResponseText();
		
		
		Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
		Matcher matcher = pattern.matcher(panNumber);
		// Check if pattern matches 
		if (matcher.matches()) {
			maxSaleReturnTransaction.setPanNumber(panNumber);
			try {
				// Set the value in Journal
				if (jmi != null) {
					Object dataArgs[] = new Object[1];
					dataArgs[0] = panNumber;
					StringBuilder message = new StringBuilder(Util.EOL);
					message.append(I18NHelper.getString(
							I18NConstantsIfc.EJOURNAL_TYPE,
							"JournalEntry.PanCard", dataArgs));
					message.append(Util.EOL);
					jmi.journal(cargo.getTransaction().getCashier()
							.getLoginID(), cargo.getTransaction()
							.getTransactionID(), message.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			bus.mail("Continue");
		}
		else {
			DialogBeanModel dModel = new DialogBeanModel();
			dModel.setType(DialogScreensIfc.ERROR);
			dModel.setResourceID("InvalidPAN");
			dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Invalid");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dModel);
			return;
		}
 	}
}
