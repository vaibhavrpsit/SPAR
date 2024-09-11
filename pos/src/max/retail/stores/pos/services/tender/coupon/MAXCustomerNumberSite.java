/*changes by shyvanshu mehra*/

package max.retail.stores.pos.services.tender.coupon;

import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.beans.MAXDialogBeanModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

public class MAXCustomerNumberSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5779754699397240413L;
	
	public void arrive(BusIfc bus) {
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		//System.out.println("MAXCheckforCustomerNumberSite===== "+cargo.getCustomerInfo().getPhoneNumber());
		 if(cargo.getCustomerInfo().getPhoneNumber().getPhoneNumber()==null || cargo.getCustomerInfo().getPhoneNumber().getPhoneNumber().equals("") )
		 {
			/*
			 * // System.out.println(" Anuj SINGH  "); MAXDialogBeanModel dialogModel = new
			 * MAXDialogBeanModel(); //System.out.println(dialogModel);
			 * //dialogModel.setArgs(m); // dialogModel.setResourceID("Customer_Number");
			 * dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
			 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
			 * POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			 * ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			 */
			 bus.mail("Undo");
		 }
		 else
		 {
			  bus.mail("Yes");
		 }
	
	}
	
	

}
