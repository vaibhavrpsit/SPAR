package max.retail.stores.pos.services.tender.oxigenwallet;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXOxigenWalletTenderInfoSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus) {
		POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel model = new POSBaseBeanModel();
		PromptAndResponseModel parModel = new PromptAndResponseModel();	
		LineItemsModel beanModel = new LineItemsModel();
		NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();
		//nModel.addButton("Failure", "Re-Send OTP", "Re-Send OTP", true,  null);
		nModel.addButton("Failure", "Re-Send OTP", "Re-Send OTP", true, null);
		parModel.setResponseText("");
		model.setPromptAndResponseModel(parModel); 	
		beanModel.setLocalButtonBeanModel(nModel);
		uiManager.showScreen(MAXPOSUIManagerIfc.ENTER_OTP, beanModel);
	}

}
