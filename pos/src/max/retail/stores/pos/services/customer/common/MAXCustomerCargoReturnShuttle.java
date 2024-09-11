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
import oracle.retail.stores.pos.services.customer.common.CustomerCargoReturnShuttle;


public class MAXCustomerCargoReturnShuttle extends CustomerCargoReturnShuttle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void load(BusIfc bus)
	{   // Store customer cargo
		super.load(bus);	
	}

	//----------------------------------------------------------------------
	/**
       Transfers data from one customer service to another.
       @param  bus     Service Bus
	 **/
	//----------------------------------------------------------------------
	public void unload(BusIfc bus)
	{
		super.unload(bus);
		CustomerCargo cargo  = (CustomerCargo)bus.getCargo();
		//  cargo.setTicCustomerPhoneNo(null);
		//Changes for Rev 1.0 : Starts
		((MAXCustomerMainCargo)cargo).setTicCustomerPhoneNoFlag(false);
		//Changes for Rev 1.0 : Ends
	}
}
