/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0     Oct 19, 2016		Mansi Goel			Changes for Customer FES
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.customer.lookup;

import java.util.Vector;

import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXValidateCustInfoAisle extends PosLaneActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7141137112906359802L;

	public void traverse(BusIfc bus) {
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		PhoneIfc phone = null;
		CustomerCargo cc = (CustomerCargo) bus.getCargo();
		Vector phones = cc.getCustomer().getPhones();
		phone = (PhoneIfc) phones.elementAt(0);

		if (cc.getCustomer().getFirstName().equalsIgnoreCase("")
				&& cc.getCustomer().getLastName().equalsIgnoreCase("")
				&& phone.getPhoneNumber().equalsIgnoreCase("")
				|| !cc.getCustomer().getFirstName().equalsIgnoreCase("")
				&& cc.getCustomer().getLastName().equalsIgnoreCase("")
				&& phone.getPhoneNumber().equalsIgnoreCase("")
				|| cc.getCustomer().getFirstName().equalsIgnoreCase("")
				&& !cc.getCustomer().getLastName().equalsIgnoreCase("")
				&& phone.getPhoneNumber().equalsIgnoreCase("")) {
			
			// Using "generic dialog bean". display the error dialog
			DialogBeanModel model = new DialogBeanModel();

			// Set model to same name as dialog
			// Set button and arguments
			model.setResourceID("EnterCustSearchDetails");
			model.setType(DialogScreensIfc.ERROR);
			model.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
					CommonLetterIfc.RETRY);
			
			// set and display the model
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		} else if (!cc.getCustomer().getFirstName().equalsIgnoreCase("")
				&& cc.getCustomer().getLastName().equalsIgnoreCase("")
				&& !phone.getPhoneNumber().equalsIgnoreCase("")
				|| cc.getCustomer().getFirstName().equalsIgnoreCase("")
				&& !cc.getCustomer().getLastName().equalsIgnoreCase("")
				&& !phone.getPhoneNumber().equalsIgnoreCase("")) {

			// Using "generic dialog bean". display the error dialog
			DialogBeanModel model = new DialogBeanModel();

			// Set model to same name as dialog
			// Set button and arguments
			model.setResourceID("EnterCustSearchDetails");
			model.setType(DialogScreensIfc.ERROR);
			model.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
					CommonLetterIfc.RETRY);
			// set and display the model
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		} else if (!cc.getCustomer().getFirstName().equalsIgnoreCase("")
				&& !cc.getCustomer().getLastName().equalsIgnoreCase("")
				&& phone.getPhoneNumber().equalsIgnoreCase(""))
			bus.mail("Continueforward");
		else if (cc.getCustomer().getFirstName().equalsIgnoreCase("")
				&& cc.getCustomer().getLastName().equalsIgnoreCase("")
				&& !phone.getPhoneNumber().equalsIgnoreCase(""))
			bus.mail("Continueforward");
		else if (!cc.getCustomer().getFirstName().equalsIgnoreCase("")
				&& !cc.getCustomer().getLastName().equalsIgnoreCase("")
				&& !phone.getPhoneNumber().equalsIgnoreCase(""))
			bus.mail("Continueforward");
	}
}
