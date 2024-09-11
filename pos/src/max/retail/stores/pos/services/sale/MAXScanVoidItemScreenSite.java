/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Dec 12, 2016		Mansi Goel          Changes for Scan & Void FES
 *
 ********************************************************************************/

package max.retail.stores.pos.services.sale;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXScanVoidItemScreenSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = -4942598978304606631L;

	public void arrive(BusIfc bus) {
		// Display the screen
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		PromptAndResponseModel parModel = ((POSBaseBeanModel) ui.getModel(MAXPOSUIManagerIfc.LINEITEM_VOID))
				.getPromptAndResponseModel();

		if (parModel != null)
			parModel.setResponseText("");
		ui.showScreen(MAXPOSUIManagerIfc.LINEITEM_VOID);

	}
}