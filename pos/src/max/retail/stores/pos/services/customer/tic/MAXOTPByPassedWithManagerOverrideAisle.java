/* ===========================================================================
 *  Copyright (c) 2019 Lifestyle India Pvt Ltd.    All Rights Reserved. 
 * ===========================================================================
 *
 * Rev 1.0  5th May 2020	Karni Singh POS REQ: Register CRM customer with OTP
 * Initial revision.
 *
 * ===========================================================================
 */
package max.retail.stores.pos.services.customer.tic;

import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

public class MAXOTPByPassedWithManagerOverrideAisle extends PosLaneActionAdapter {

	private static final long serialVersionUID = -3118309612038386583L;

	@Override
	public void traverse(BusIfc bus) {		
		
		MAXCustomerMainCargo customerCargo = null;
		MAXSaleCargo saleCargo = null;		
		if(bus.getCargo() instanceof MAXCustomerMainCargo)
		{
		 customerCargo= (MAXCustomerMainCargo) bus.getCargo();
		 customerCargo.setCRMEnrolmentOTPValidated(true);
		 
		}
		else if(bus.getCargo() instanceof MAXSaleCargo)
		{
			saleCargo= (MAXSaleCargo) bus.getCargo();
			saleCargo.setCRMEnrolmentOTPValidated(true);
			
		}
	}
		 
		
	
}
