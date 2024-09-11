/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0     Oct 19, 2016		Mansi Goel			Changes for Customer FES
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.customer.common;

import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerCargoLaunchShuttle;

public class MAXCustomerCargoLaunchShuttle extends CustomerCargoLaunchShuttle {

	private static final long serialVersionUID = 1L;

	public void load(BusIfc bus) {
		super.load(bus);
	}

	public void unload(BusIfc bus) {
		super.unload(bus);

		CustomerCargo cargo = (CustomerCargo) bus.getCargo();
		//Changes for Rev 1.0 : Starts
		((MAXCustomerCargo) cargo)
				.setTicCustomerPhoneNo(((MAXCustomerMainCargo) customerCargo)
						.getTicCustomerPhoneNo());
		((MAXCustomerCargo) cargo)
				.setTicCustomerPhoneNoFlag(((MAXCustomerMainCargo) customerCargo)
						.isTicCustomerPhoneNoFlag());
		//Changes for Rev 1.0 : Ends
	}

}
