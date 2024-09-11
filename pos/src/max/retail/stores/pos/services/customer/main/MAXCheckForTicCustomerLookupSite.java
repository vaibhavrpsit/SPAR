/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
	Rev 1.0 	20/05/2013		Prateek		Initial Draft: Changes for TIC Customer Integration
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.customer.main;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

public class MAXCheckForTicCustomerLookupSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus)
	{
		MAXCustomerMainCargo cargo = (MAXCustomerMainCargo)bus.getCargo();
		if(cargo.isTICCustomerLookup)
			bus.mail("TIC");
		else
			bus.mail("Continue");
	}
}
