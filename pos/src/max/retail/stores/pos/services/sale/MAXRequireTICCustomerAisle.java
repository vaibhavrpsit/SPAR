
/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 

 *  Rev 1.0     10/03/2015      Akhilesh kumar          		Loyalty Customer
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.sale;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXRequireTICCustomerAisle extends PosLaneActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void traverse(BusIfc bus) {

		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		DialogBeanModel model = new DialogBeanModel();
		model.setResourceID("TicCustomerTransaction");
		model.setType(DialogScreensIfc.CONFIRMATION);
		model.setButtonLetter(DialogScreensIfc.BUTTON_YES, "CaptureTICCustomer");
		model.setButtonLetter(DialogScreensIfc.BUTTON_NO, "DONOTCaptureTICCustomer");
	    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}
}
