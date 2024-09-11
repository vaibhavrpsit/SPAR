/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale.validate;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;

/**
 * This site displays a screen asking the user to enter the Capillary Coupon Number
 */
public class MAXEnterCouponNumberSite extends PosSiteActionAdapter{
	/**
	 * serialVersionUID long
	 */
	private static final long serialVersionUID = 786154529313870338L;

	public void arrive(BusIfc bus){
		
		
		POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);		
		
		// Changes Start for capillary POS timeout in cash of network issue - Karni
		
		LineItemsModel beanModel = new LineItemsModel();		
		DefaultTimerModel timeModel = new DefaultTimerModel(bus, false);
		
		timeModel.setActionName("Timeout");
		
		String timeOut = Gateway.getProperty("application", "CapillarytimeOutInMilliSeconds", null);
		timeModel.setTimerInterval(Integer.parseInt(timeOut));
		
		beanModel.setTimerModel(timeModel);
		
		// Changes End for capillary POS timeout in cash of network issue - Karni		
		uiManager.showScreen(MAXPOSUIManagerIfc.ENTER_COUPON_NUMBER, beanModel);
	}
}