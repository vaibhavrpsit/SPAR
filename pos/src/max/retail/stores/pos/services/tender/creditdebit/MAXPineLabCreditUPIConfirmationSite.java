/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 *  Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved. 
 *
 *  Rev 1.0   Mar 12,2018           Nitika Arora            Changes for UPI new requirement.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXPineLabCreditUPIConfirmationSite extends PosSiteActionAdapter{

	private static final long serialVersionUID = -4354045474935486867L;

	@Override
	public void arrive(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("UPIConfirmation");
		dialogModel.setType(DialogScreensIfc.CONFIRMATION);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.YES);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.NO);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
	
	
}

