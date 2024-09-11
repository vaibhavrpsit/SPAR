/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
  Rev 1.0       Tanmaya			24/05/2013		Initial Draft: Changes for Store Credit
  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.storecredit;

import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;

public class MAXExpiredStoreCreditRoad extends PosLaneActionAdapter{

	private static final long serialVersionUID = -575029949368045733L;

	public void traverse(BusIfc bus) {

		TenderCargo cargo = (TenderCargo)bus.getCargo();
		
		cargo.setAccessFunctionID(MAXRoleFunctionIfc.ALLOW_EXPIRED_STORE_CREDIT);

	}
}
