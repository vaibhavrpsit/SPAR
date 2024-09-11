/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Aug 21, 2018		Bhanu Priya		Changes for Capture PAN CARD CR
 *
 ********************************************************************************/
package max.retail.stores.pos.services.tender.pancard;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

public class MAXEnterSelfDeclarationDetailsSite extends PosSiteActionAdapter {
	
	
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		DataInputBeanModel model=(DataInputBeanModel)ui.getModel(MAXPOSUIManagerIfc.CAPTURE_PASSPORT_DETAILS);
		model.setValue("PassportNumberField", null);
		model.setValue("VisaNumberField", null);
		model.setValue("ITRAckField", null);
		
		ui.showScreen(MAXPOSUIManagerIfc.CAPTURE_PASSPORT_DETAILS);
		
	}

	
}
