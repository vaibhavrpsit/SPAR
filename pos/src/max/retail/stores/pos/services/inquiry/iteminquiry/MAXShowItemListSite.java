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

import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ShowItemListSite;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ItemListBeanModel;

public class MAXShowItemListSite extends ShowItemListSite {

	private static final long serialVersionUID = -8428999083150697461L;

	public void arrive(BusIfc bus) {
		MAXItemInquiryCargo cargo = (MAXItemInquiryCargo) bus.getCargo();
		PLUItemIfc[] items = cargo.getItemList();
		ItemListBeanModel model = new ItemListBeanModel();
		model.setItemList(items);
		// Display the screen
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		ui.showScreen(POSUIManagerIfc.ITEMS_LIST, model);
	}

	@Override
	public void reset(BusIfc bus) {
		arrive(bus);
	}
}
