//changes by shyvanshu mehra

package max.retail.stores.pos.services.tender;

import max.retail.stores.pos.ui.beans.MAXDialogBeanModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

public class MAXCheckCustomerNumber extends PosSiteActionAdapter {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8621251155678289300L;

	/**
	 * 
	 */
	

	public void arrive(BusIfc bus) {
		System.out.println("CheckCustomerNumber");
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		if(cargo.getCustomerInfo().getPhoneNumber().getPhoneNumber()==null || cargo.getCustomerInfo().getPhoneNumber().getPhoneNumber().equals("") ) {
			MAXDialogBeanModel dialogModel = new MAXDialogBeanModel();
			dialogModel.setResourceID("Attach_Customer_Number");
			dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "OK");
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		}
		else {
			bus.mail("Yes");
		}
		}
	}

