/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *      Copyright (c) 2022-2023 MAXHyperMarket, Inc.    All Rights Reserved.
 *     
 * Rev 1.0 		April 08,2022		Kamlesh Pant   Paytm QR Integration
 * Initial revision.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit.paytmqr;

import max.retail.stores.domain.tender.paytm.MAXPaytmTenderConstants;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.TenderBeanModel;

public class MAXDeletePaytmQRTenderConfirmationSite extends PosSiteActionAdapter{

	private static final long serialVersionUID = 3145945081556163551L;

	public void arrive(BusIfc bus)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		TenderBeanModel model = (TenderBeanModel)ui.getModel();
        
        TenderLineItemIfc tenderToRemove = model.getTenderToDelete();
        MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
        //cargo.setTenderToRemove(tenderToRemove);
		
		int buttons[] = new int[2];
		DialogBeanModel dialogModel = new DialogBeanModel();
		buttons[0] = DialogScreensIfc.BUTTON_YES;
		buttons[1] = DialogScreensIfc.BUTTON_NO;
		
		dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMDELETETENDER);
		dialogModel.setType(DialogScreensIfc.CONFIRMATION);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Yes");
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "No");

		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

}
