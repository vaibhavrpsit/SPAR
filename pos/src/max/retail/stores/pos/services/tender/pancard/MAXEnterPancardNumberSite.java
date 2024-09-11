/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Aug 21, 2018		Bhanu Priya		Changes for Capture PAN CARD CR
 *
 ********************************************************************************/
package max.retail.stores.pos.services.tender.pancard;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXSelectCustomerTypeBeanModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

public class MAXEnterPancardNumberSite extends PosSiteActionAdapter
{
	
	private static final long serialVersionUID = 1L;

		public void arrive(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);		
		MAXSelectCustomerTypeBeanModel model=  (MAXSelectCustomerTypeBeanModel)ui.getModel();
		model.setPromptAndResponseModel(null);
		ui.showScreen(MAXPOSUIManagerIfc.PANCARD_NUMBER_CAPTURE,model);
		
	}
}
