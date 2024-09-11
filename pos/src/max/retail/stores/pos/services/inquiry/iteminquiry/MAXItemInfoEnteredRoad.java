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

//foundation imports
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ItemInquiryBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.utility.PLUItemUtility;

public class MAXItemInfoEnteredRoad extends LaneActionAdapter {

	private static final long serialVersionUID = 7650351625466021236L;

	public void traverse(BusIfc bus) {

		MAXItemInquiryCargo cargo = (MAXItemInquiryCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		// Initialize bean model values
		if (ui.getModel() instanceof ItemInquiryBeanModel) {
			ItemInquiryBeanModel model = (ItemInquiryBeanModel) ui.getModel();

			String itemNumber = null;
			String itemDesc = null;
			String geoCode = null;

			if (model.getItemNumber() != null && model.getItemNumber().trim().length() > 0) {
				itemNumber = model.getItemNumber().trim();
			}

			if (model.getItemDesc() != null && model.getItemDesc().trim().length() > 0) {
				itemDesc = model.getItemDesc().trim();
			}

			if (model.getManufacturer() != null && model.getManufacturer().trim().length() > 0) {
				String manufacturer = model.getManufacturer().trim();
			}
			Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
			cargo.setInquiry(uiLocale, itemNumber, itemDesc, model.getSelectedDept(), geoCode);
		} else {
			POSBaseBeanModel model = (POSBaseBeanModel) ui.getModel();
			PromptAndResponseModel parModel = model.getPromptAndResponseModel();
			String itemID = ui.getInput().trim();
			String itemNumber = itemID;
			if (parModel != null) {
				boolean isScanned = parModel.isScanned();
				if (isScanned) {
					itemNumber = processScannedItemNumber(bus, itemID);
				}
			}
			cargo.setInquiry(itemNumber, null, "-1");
		}
	}

	protected String processScannedItemNumber(BusIfc bus, String itemID) {
		String itemNumber = itemID;
		String[] parser = PLUItemUtility.getInstance().parseItemString(itemID);
		if (parser != null) {
			itemNumber = parser[0];
		}
		return itemNumber;
	}
}
