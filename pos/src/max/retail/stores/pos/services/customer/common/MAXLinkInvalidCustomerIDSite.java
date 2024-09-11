/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
    Rev 1.1 	17/06/2013		Prateek		Changes done for Bug 6119 
	Rev 1.0 	20/05/2013		Prateek		Initial Draft: Changes for TIC Customer Integration
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.customer.common;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;

public class MAXLinkInvalidCustomerIDSite extends PosSiteActionAdapter {

	public void arrive(BusIfc bus)
	{
		CustomerCargo cargo = (CustomerCargo)bus.getCargo();
		MAXCustomerIfc customer = (MAXCustomerIfc)cargo.getCustomer();
		customer.setCustomerID(cargo.getCustomerID());
		/**MAX Rev 1.1 Change : Start**/
        cargo.getCustomer().setLastName(cargo.getCustomerID());
        cargo.getCustomer().setCustomerName(cargo.getCustomerID());
		/**MAX Rev 1.1 Change : End**/
		cargo.setCustomer(customer);
		customer.setCustomerType("T");
		bus.mail("Success");
	}
}
