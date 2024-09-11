/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.poscount.coupon;

import max.retail.stores.pos.services.dailyoperations.poscount.MAXPosCountCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXCouponDenominationCountBeanModel;
import max.retail.stores.pos.ui.beans.MAXCouponDenominationCounterBeanModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

public class MAXCouponDenominationEnteredAisle extends PosLaneActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void traverse(BusIfc bus)
	{
		MAXPosCountCargo cargo = (MAXPosCountCargo)bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		MAXCouponDenominationCounterBeanModel model = null;

		if (ui.getModel(MAXPOSUIManagerIfc.SELECT_COUPON_TO_COUNT_DETAIL) instanceof MAXCouponDenominationCountBeanModel)
		{
			model = (MAXCouponDenominationCounterBeanModel)ui.getModel(MAXPOSUIManagerIfc.SELECT_COUPON_TO_COUNT_DETAIL);
			ui.setModel(MAXPOSUIManagerIfc.SELECT_COUPON_TO_COUNT_DETAIL, new MAXCouponDenominationCounterBeanModel());
		}
		bus.mail("Success");
	}
}
