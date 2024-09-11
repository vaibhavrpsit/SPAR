/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *      Copyright (c) 2022-2023 MAXHyperMarket, Inc.    All Rights Reserved.    
 * Rev 1.0 		April 2, 2022    Kamlesh Pant   Paytm QR Integration
 * Initial revision.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit.paytmqr;

import max.retail.stores.domain.tender.paytmqr.MAXPaytmQRCodeTenderConstants;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXPaytmQRTenderCancelAisle extends LaneActionAdapter{

	private static final long serialVersionUID = -292973898915948786L;
	
	public void traverse(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		DialogBeanModel model = new DialogBeanModel();			    
		
		model.setResourceID(MAXPaytmQRCodeTenderConstants.PAYTMQRCODETENDERCANCEL);	
		model.setType(DialogScreensIfc.CONFIRMATION);
		model.setButtonLetter(DialogScreensIfc.BUTTON_YES, "failure");
		model.setButtonLetter(DialogScreensIfc.BUTTON_NO, "CheckStatusReqPending");
		
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}	
	

}
