/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2016-2017 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *     
 * Rev 1.0 		Apr 11,2017		Nadia Arora (EYLLP)   Paytm Integration
 * Initial revision.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.wallet.paytm;

import max.retail.stores.domain.tender.MAXTenderPaytm;
import max.retail.stores.domain.tender.mobikwik.MAXMobikwikTenderConstants;
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

public class MAXDeletePaytmTenderConfirmationSite extends PosSiteActionAdapter{

	private static final long serialVersionUID = 3145945081556163551L;

	public void arrive(BusIfc bus)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		TenderBeanModel model = (TenderBeanModel)ui.getModel();
        
        TenderLineItemIfc tenderToRemove = model.getTenderToDelete();
        MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
       // cargo.setTenderToRemove(tenderToRemove);
		
		int buttons[] = new int[2];
		DialogBeanModel dialogModel = new DialogBeanModel();
		buttons[0] = DialogScreensIfc.BUTTON_YES;
		buttons[1] = DialogScreensIfc.BUTTON_NO;
		if (tenderToRemove instanceof MAXTenderPaytm)	
		{
		dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMDELETETENDER);
		}
		else
		{
			dialogModel.setResourceID(MAXMobikwikTenderConstants.MOBIKWIKDELETETENDER);
		}
		dialogModel.setType(DialogScreensIfc.CONFIRMATION);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Yes");
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "No");

		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

}
