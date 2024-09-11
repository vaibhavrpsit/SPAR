/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*
*
*
*	Rev 1.1     Oct 19, 2016		Mansi Goel			Changes for Customer FES
* 	Rev 1.0		Sep 13, 2016		Ashish Yadav		Changes done for code merging
* 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.sale.CustomerLaunchShuttle;

//--------------------------------------------------------------------------
/**
    Transfer necessary data from the POS service to the Customer service.
    <p>
    $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXCustomerLaunchShuttle extends CustomerLaunchShuttle
{
    
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.sale.MAXCustomerLaunchShuttle.class);


	private static final long serialVersionUID = 1L;

	public void load(BusIfc bus)
    {
		super.load(bus);
		
    }
	
	public void unload(BusIfc bus)
    {
		super.unload(bus);
		
		///changes for the tic customer CR
		
		MAXCustomerMainCargo cargo = (MAXCustomerMainCargo)bus.getCargo();
		// Changes Starts for rev 1.0
		if(cargo.getOriginalCustomer()!=null && (cargo.getCustomer()==null || (cargo.getCustomer()!=null && cargo.getCustomer().getCustomerID().trim().equalsIgnoreCase("")))){
			cargo.setCustomer(cargo.getOriginalCustomer());
		}
		cargo.setStoreStatus(saleCargo.getStoreStatus());
		// Changes ends for rev 1.0
		cargo.setTransaction(saleCargo.getTransaction());
		// Changes Starts for rev 1.0
		if(((MAXSaleCargoIfc) saleCargo).getTicCustomerPhoneNo()!=null && !((MAXSaleCargoIfc) saleCargo).getTicCustomerPhoneNo().getPhoneNumber().trim().equalsIgnoreCase("")){
			//Changes for Rev 1.1 : Starts
			cargo.setTicCustomerPhoneNo(((MAXSaleCargoIfc) saleCargo).getTicCustomerPhoneNo());
			cargo.setTicCustomerPhoneNoFlag(((MAXSaleCargoIfc) saleCargo).isTicCustomerPhoneNoFlag());
			//Changes for Rev 1.1 : Ends	
		}		
		// Changes ends for rev 1.0
        
    }
	
}
