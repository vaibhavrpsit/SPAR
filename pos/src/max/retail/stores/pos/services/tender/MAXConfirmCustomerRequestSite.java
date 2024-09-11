//changes by shyvanshu mehra//


package max.retail.stores.pos.services.tender;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXConfirmCustomerRequestSite extends PosSiteActionAdapter  {
	


	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus) {
		
		bus.getCargo();
		POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(POSUIManagerIfc.TYPE);
		
		

	
	//private void displayMobileEnterPrompt(POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("ConfirmRequest");
		dialogModel.setType(DialogScreensIfc.YES_NO);
		dialogModel.setArgs(null);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Yes");
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "No");
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}


	}
