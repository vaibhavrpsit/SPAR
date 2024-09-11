/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev 1.0		Feb 16, 2017		Nadia Arora		fix : In ADV search, search the item with item desc 
 	and if we click on item detail application comming to the main screen
 *
 ********************************************************************************/
package max.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemDetailDisplayedRoad;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ItemListBeanModel;

public class MAXItemDetailDisplayedRoad extends ItemDetailDisplayedRoad {

	private static final long serialVersionUID = 7824349032727325893L;

	@Override
	public void traverse(BusIfc bus){
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		ItemListBeanModel model = (ItemListBeanModel) ui.getModel();
		MAXItemInquiryCargo cargo = (MAXItemInquiryCargo)bus.getCargo();
		cargo.setPLUItem(model.getSelectedItem());
	}
}
