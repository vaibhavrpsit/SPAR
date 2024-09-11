/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	29/May/2013	  	Tanmaya, Bug 6078 - Screen with pre filled item id. 
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.alter;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//------------------------------------------------------------------------------
/**
 * Displays the edit item status screen for changing the order item status.
 * Displays the error dialog screen is order status is Canceled, Completed, or
 * Voided.
 * 
 * @version $Revision: 5$
 **/
// ------------------------------------------------------------------------------

public class MAXAddAlterItemSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = 4360566058234735342L;

	public void arrive(BusIfc bus) {
		
		POSUIManagerIfc     ui          = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel(MAXPOSUIManagerIfc.ADD_ORDER_ITEM);
		PromptAndResponseModel pModel =model.getPromptAndResponseModel();
		if(pModel==null)
			pModel = new PromptAndResponseModel();
		pModel.setResponseText("");
		ui.showScreen(MAXPOSUIManagerIfc.ADD_ORDER_ITEM);
	}
}
