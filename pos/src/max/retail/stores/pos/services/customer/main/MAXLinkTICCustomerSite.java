/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
	Rev 1.0 	20/05/2013		Prateek		Initial Draft: Changes for TIC Customer Integration
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.customer.main;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXLinkTICCustomerSite extends PosSiteActionAdapter {

	public void arrive(BusIfc bus)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		
		PromptAndResponseModel prmp = new PromptAndResponseModel();
		POSBaseBeanModel baseModel = new POSBaseBeanModel();
		boolean enableResponse = true;
    //	ParameterManagerIfc pm = (ParameterManagerIfc) bus
	//			.getManager(ParameterManagerIfc.TYPE);
    //	try {
	//		enableResponse = pm.getBooleanValue("ManualEntryEnable").booleanValue();
	//	} catch (ParameterException e1) {
			// TODO Auto-generated catch block
	//		e1.printStackTrace();
	//	}
    	prmp.setResponseEnabled(enableResponse);
    	//model.setPromptAndResponseModel(pModel);
		baseModel.setPromptAndResponseModel(prmp);
		
		
		ui.showScreen(MAXPOSUIManagerIfc.MAX_TIC_CUSTOMER_OPTIONS, baseModel);
	}
}
