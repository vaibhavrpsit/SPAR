/********************************************************************************
 *   
 *	Copyright (c) 2018 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Aug 21, 2018		Bhanu Priya		Changes for Capture PAN CARD CR
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.pancard;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

public class MAXSelectNonResidentCustomerAisle extends PosLaneActionAdapter {

	
	private static final long serialVersionUID = 1L;

	public void traverse(BusIfc bus) {
		
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		String letters[] = new String[2];
		letters[0] = CommonLetterIfc.ADD;
		letters[1] = CommonLetterIfc.NO;

		int buttons[] = new int[2];
		buttons[0] = DialogScreensIfc.BUTTON_YES;
		buttons[1] = DialogScreensIfc.BUTTON_NO;

		UIUtilities.setDialogModel(ui, DialogScreensIfc.CONFIRMATION,
				"SelfDeclarationConfirm", null, buttons, letters);
		
	}

}
