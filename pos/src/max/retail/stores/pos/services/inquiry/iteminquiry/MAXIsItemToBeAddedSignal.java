/********************************************************************************
 *
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 *	
 * 	Rev 1.0  02 Jan, 2016		Ashish yadav		Changes for Item inquiry
 *
 ********************************************************************************/
package max.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
// foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.IsItemToBeAddedSignal;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ItemListBeanModel;

public class MAXIsItemToBeAddedSignal extends IsItemToBeAddedSignal
{
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = 7224235090964733327L;

	/**
        revision number supplied by Team Connection
	 **/
	public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

	//----------------------------------------------------------------------
	/**
        Checks to make sure that the item is to be added
        @return boolean true if the  item is to be added false otherwise.
	 **/
	//----------------------------------------------------------------------
	@Override
	public boolean roadClear(BusIfc bus)
	{
		boolean result = false;
		ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
		if (cargo.getModifiedFlag())
		{
			result = true;
			POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
			if(ui.getModel() instanceof ItemListBeanModel){
				ItemListBeanModel model = (ItemListBeanModel) ui.getModel();
				MAXItemInquiryCargo lsiplcargo = (MAXItemInquiryCargo)bus.getCargo();
				lsiplcargo.setPLUItem(model.getSelectedItem());
			}

		}
		return(result);
	}
}
