/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  Rev 1.0  22/May/2013	Jyoti Rawal, Initial Draft: Changes for Credit Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXShowOfflineSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6467772489754940617L;

	/**
	 * 
	 */
	

	public void arrive(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
		.getManager(UIManagerIfc.TYPE);
		DialogBeanModel model = new DialogBeanModel();
		String msg[] = new String[2];
		msg[0] = "Plutus is offline";
		msg[1] = "Please click offline button to tender in offline mode";
		model.setArgs(msg);
		ui.showScreen(MAXPOSUIManagerIfc.MAX_SHOW_CREDIT_OFFLINE,model);

	}

}
