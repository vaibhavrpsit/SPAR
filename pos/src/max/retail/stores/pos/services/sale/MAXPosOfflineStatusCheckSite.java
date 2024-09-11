/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
Rev 1.0   Rahul		01/April/2014	initial Draft for Pos Offline Alert Prompt
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale;

import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.DispatcherIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CheckOfflineSite;

public class MAXPosOfflineStatusCheckSite extends CheckOfflineSite
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String revisionNumber = "$Revision: 3$";

	public void arrive(BusIfc bus)
	{
		DispatcherIfc d = Gateway.getDispatcher();
		DataManagerIfc dm = (DataManagerIfc)d.getManager("DataManager");
		MAXSaleCargo cargo = (MAXSaleCargo) bus.getCargo();
		Letter letter = new Letter("Continue");
		if(!cargo.isPosOfflineAlertOverrideSuccess())
		{
			if (!transactionsAreOffline(dm))
			{
				 letter = new Letter("Continue");
				
				
				
			}
			else
			{
				cargo.setAccessFunctionID(MAXRoleFunctionIfc.POS_OFFLINE_ALERT);
				 letter = new Letter("Invalid");

				
			}
		}
		bus.mail(letter, BusIfc.CURRENT);
	}
	public void depart(BusIfc bus)
	{
		MAXSaleCargo cargo = (MAXSaleCargo) bus.getCargo();
//		cargo.setAccessFunctionID(0);
	}
}
