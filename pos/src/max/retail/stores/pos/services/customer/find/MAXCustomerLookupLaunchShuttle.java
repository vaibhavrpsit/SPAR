package max.retail.stores.pos.services.customer.find;

import max.retail.stores.pos.services.customer.common.MAXCustomerCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.find.CustomerLookupLaunchShuttle;

public class MAXCustomerLookupLaunchShuttle extends CustomerLookupLaunchShuttle{

	
	
	private static final long serialVersionUID = 1L;

	public void load(BusIfc bus) {

		super.load(bus);

	}

	public void unload(BusIfc bus) {
		super.unload(bus);
	
		 CustomerCargo cargo = (CustomerCargo)bus.getCargo();
			
		
		 ((MAXCustomerCargo)cargo).setTicCustomerPhoneNo(((MAXCustomerCargo)customerCargo).getTicCustomerPhoneNo());
		 ((MAXCustomerCargo)cargo).setTicCustomerPhoneNoFlag(((MAXCustomerCargo)customerCargo).isTicCustomerPhoneNoFlag());
	        

	}
	
}
