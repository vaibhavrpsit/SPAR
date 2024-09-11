/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
	Rev 1.0 	13/08/2013		Prateek		Initial Draft: Changes for TIC Customer CR
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.pos.services.sale.validate;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXLinkBlindTICCustomerSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1340319866952517996L;

	public void arrive(BusIfc bus)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel baseModel = new POSBaseBeanModel();
		PromptAndResponseModel model = new PromptAndResponseModel();
		boolean enableResponse = true;
    	ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
    	try {
			enableResponse = pm.getBooleanValue("ManualEntryEnable").booleanValue();
		} catch (ParameterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	model.setResponseEnabled(enableResponse);
		baseModel.setPromptAndResponseModel(model);
		ui.showScreen(MAXPOSUIManagerIfc.ENTER_TIC_CUSTOMER_ID, baseModel);
	}
}
