/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale.validate;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;


/**
 * This site displays a screen asking the user to enter Non-TIC Customer's Phone Number
 */
public class MAXEnterMobileNumberSite extends PosSiteActionAdapter{
	/**
	 * serialVersionUID long
	 */
	private static final long serialVersionUID = 7483644178049487312L;

	public void arrive(BusIfc bus){
		POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		uiManager.showScreen(MAXPOSUIManagerIfc.NON_TIC_MOBILE_NUMBER);
	}
}