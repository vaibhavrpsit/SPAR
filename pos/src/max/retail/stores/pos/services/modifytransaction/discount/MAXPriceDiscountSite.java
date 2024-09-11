/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Nov 30, 2016		Mansi Goel		Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.modifytransaction.discount;

import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;

public class MAXPriceDiscountSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = -395732166229857305L;

	public void arrive(BusIfc bus) {

		ModifyTransactionDiscountCargo cargo = (ModifyTransactionDiscountCargo) bus.getCargo();

		cargo.setAccessFunctionID(MAXRoleFunctionIfc.DISCOUNT);
		bus.mail("Next");
	}

}
