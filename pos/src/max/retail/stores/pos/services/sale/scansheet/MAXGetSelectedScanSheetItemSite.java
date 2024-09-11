/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	Sep 10, 2018		Purushotham Reddy         Added the class for Code Merge CR	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.sale.scansheet;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ImageGridBeanModel;

@SuppressWarnings("serial")
public class MAXGetSelectedScanSheetItemSite extends PosSiteActionAdapter {
	@Override
	public void arrive(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		ImageGridBeanModel model = (ImageGridBeanModel) ui
				.getModel(POSUIManagerIfc.SCAN_SHEET);
		String itemID = model.getSelectedItemID();
		String categoryID = model.getCategoryID();
		String categoryDesc = model.getCategoryDescription();
		model.setSelectedItemID(null);
		model.setCategoryDescription(null);
		model.setCategorySelected(false);
		MAXScanSheetCargo cargo = (MAXScanSheetCargo) bus.getCargo();
		cargo.setSelectedScanSheetItemID(itemID.toUpperCase());
		cargo.setScansheetCategoryID(categoryID);
		cargo.setScansheetCategoryDescription(categoryDesc);
		bus.mail(CommonLetterIfc.NEXT, BusIfc.CURRENT);
	}

}
