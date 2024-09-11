/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		24/04/2013		Initial Draft: Changes for Gift Card
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.pricing;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

public class MAXCheckManagerOverrideAisle extends PosLaneActionAdapter{
	 /**
	 * 
	 */
	private static final long serialVersionUID = -6452913266317430399L;

	public void traverse(BusIfc bus)
	    {
		MAXPricingCargo cargo = (MAXPricingCargo) bus.getCargo();
		//POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		
		String letter = CommonLetterIfc.OVERRIDE;
		cargo.setAccessFunctionID(705);//functionid for gift card only.
		
		bus.mail(letter, BusIfc.CURRENT);
	    }
			
}
