/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev. 1.0 		Tanmaya		05/04/2013		Initial Draft: Change for Scan and void
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale;

import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

public class MAXUnsetScanNVoidFlowRoad extends LaneActionAdapter{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7326638045577051956L;

	public void traverse(BusIfc bus) {
		// TODO Auto-generated method stub
		((MAXSaleCargo)bus.getCargo()).setScanNVoidFlow(false);
		}

}
