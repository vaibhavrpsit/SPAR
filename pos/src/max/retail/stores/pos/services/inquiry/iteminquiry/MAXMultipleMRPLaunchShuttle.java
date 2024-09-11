/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev 1.0		Dec 27, 2016		Mansi Goel		Changes for Advanced Search
 *
 ********************************************************************************/

package max.retail.stores.pos.services.inquiry.iteminquiry;

import max.retail.stores.pos.services.sale.multiplemrp.MAXMultipleMRPCargo;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;

public class MAXMultipleMRPLaunchShuttle implements ShuttleIfc {
	private static final long serialVersionUID = 3096938593314674383L;

	protected PLUItemIfc item = null;

	public void load(BusIfc bus) {
		// retrieve cargo from the parent
		ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();
		item = cargo.getPLUItem();

	}

	public void unload(BusIfc bus) {

		MAXMultipleMRPCargo cargo = (MAXMultipleMRPCargo) bus.getCargo();
		cargo.setPLUItem(item);

	}
}
