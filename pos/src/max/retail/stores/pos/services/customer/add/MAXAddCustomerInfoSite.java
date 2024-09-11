/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *  Rev 1.1		May 04, 2017		Kritica Agarwal 	GST Changes
 *	Rev 1.0     Oct 19, 2016		Mansi Goel			Changes for Customer FES
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.customer.add;

import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.add.AddCustomerInfoSite;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

//--------------------------------------------------------------------------
/**
 * Put up Customer Info screen for input of customer name and address
 * information. This screen begins the Customer Add flow.
 * <p>
 * $Revision: 3$
 **/
// --------------------------------------------------------------------------
public class MAXAddCustomerInfoSite extends AddCustomerInfoSite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4082174721338136447L;

	// ----------------------------------------------------------------------
	/**
	 * Displays the Customer Info screen for input of customer name and address
	 * information.
	 * <p>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {

		CustomerCargo cargo = (CustomerCargo) bus.getCargo();
		// model to use for the UI
		CustomerInfoBeanModel model = getCustomerInfoBeanModel(bus);

		// Disable History button when adding new customer
		cargo.setHistoryMode(false);

		// allow the customer to edit in add.
		// set the link done switch
		int linkOrDone = cargo.getLinkDoneSwitch();
		model.setLinkDoneSwitch(linkOrDone);

		NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();

		if (linkOrDone == CustomerMainCargo.LINKANDDONE) {
			// enable done
			nModel.setButtonEnabled(CommonLetterIfc.DONE, true);

			// enable link
			nModel.setButtonEnabled(CommonLetterIfc.LINK, true);
		}
		if (linkOrDone == CustomerMainCargo.LINK) {
			// disable done
			nModel.setButtonEnabled(CommonLetterIfc.DONE, false);

			// enable link
			nModel.setButtonEnabled(CommonLetterIfc.LINK, true);
		}
		if (linkOrDone == CustomerMainCargo.DONE) {
			// disable Link
			nModel.setButtonEnabled(CommonLetterIfc.LINK, false);

			// enable done
			nModel.setButtonEnabled(CommonLetterIfc.DONE, true);
		}

		model.setLocalButtonBeanModel(nModel);

		// Check if History button should be enabled
		nModel.setButtonEnabled(CustomerCargo.HISTORY, cargo.isHistoryModeEnabled());
		// check if only link is allowed
		boolean linkOnly = false;
		if (cargo.getLinkDoneSwitch() == CustomerCargo.LINK) {
			linkOnly = true;
		}
		nModel.setButtonEnabled(CustomerCargo.DONE_BTN, !linkOnly);

		model.setEditableFields(true);
		if (cargo.getEmployee() != null)
			model.setEmployeeID(cargo.getEmployee().getLoginID());
		if (cargo.getOperator() != null)
			model.setEmployeeID(cargo.getOperator().getLoginID());
		model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_MOBILE);
		// show the screen
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		uiManager.showScreen(POSUIManagerIfc.ADD_CUSTOMER, model);
	}
	

}
