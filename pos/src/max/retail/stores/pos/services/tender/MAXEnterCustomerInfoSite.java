//changes by shyvanshu mehra

package max.retail.stores.pos.services.tender;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXEnterCustomerInfoSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6739653830395161735L;

	public void arrive(BusIfc bus)
	{
		System.out.println("EnterCustomerInfoSite");
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		
		PromptAndResponseModel prmp = new PromptAndResponseModel();
		POSBaseBeanModel baseModel = new POSBaseBeanModel();
		boolean enableResponse = true;
   
    	prmp.setResponseEnabled(enableResponse);
    	//model.setPromptAndResponseModel(pModel);
		baseModel.setPromptAndResponseModel(prmp);
		
		
		ui.showScreen(MAXPOSUIManagerIfc.NEW_SCREEN, baseModel);
		//bus.mail("OK");
	}
}
