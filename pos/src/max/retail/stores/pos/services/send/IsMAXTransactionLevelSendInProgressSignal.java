/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2016	MAX HyperMarkets.    All Rights Reserved.
 
	Rev 1.0 	16/07/2016		Abhishek Goyal		Initial Draft: Changes for CR
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.send;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.services.send.address.SendCargo;

public class IsMAXTransactionLevelSendInProgressSignal implements TrafficLightIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.send.IsMAXTransactionLevelSendInProgressSignal.class);
	
	public static final String ECOM_ORDER_FUNCTIONALITY_REQUIRED = "EComOrderFunctionalityRequired";

	public boolean roadClear(BusIfc bus) {
		// TODO Auto-generated method stub
		ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
		boolean flag = false;
		try {
			Boolean para = pm.getBooleanValue(ECOM_ORDER_FUNCTIONALITY_REQUIRED);
			if(para!=null)
			{
				flag = para.booleanValue();
			}
		} catch (ParameterException pe) {
			// TODO Auto-generated catch block
			pe.printStackTrace();
			logger.error( "" + Util.throwableToString(pe) + "");
		}
		if(bus.getCargo() instanceof ItemCargo)
		{
			ItemCargo cargo = (ItemCargo) bus.getCargo();
			return cargo.isTransactionLevelSendInProgress() && flag;
		}
		else if(bus.getCargo() instanceof SendCargo)
		{
			SendCargo cargo = (SendCargo) bus.getCargo();
			return cargo.isTransactionLevelSendInProgress() && flag;
		}
		else
		{
			return false;
		}
	}

}
