/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  Aug 25, 2021              Atul Shukla                   EWallet FES Implementation
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.tender.oxigenwallet;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

import org.apache.log4j.Logger;

public class MAXIsOxigenEWalletTenderSignal  implements TrafficLightIfc
{
    protected static final Logger logger = Logger.getLogger(MAXIsOxigenEWalletTenderSignal.class);
	private static final long serialVersionUID = -3907319525867521091L;

	public boolean roadClear(BusIfc bus)
    {
		logger.debug("MAXIsOxigenEWalletTenderSignal.roadClear() - entry");
        boolean result = false;
        MAXCustomer customer=null;
        MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo(); 
        if(cargo.getTransaction().getCustomer() instanceof MAXCustomer)
        {
        	customer=(MAXCustomer)cargo.getTransaction().getCustomer();
        	if(customer.isLMREWalletCustomerFlag())
        	{
        		
        		//added by vaibhav
        		 // result = true;
        		  result = false;
        		  logger.info("inside MAXIsOxigenEWalletTenderSignal, Signal value " + result );
        	}
        }
        logger.debug("MAXIsOxigenEWalletTenderSignal.roadClear() - exit");
        return (result);
}
}
