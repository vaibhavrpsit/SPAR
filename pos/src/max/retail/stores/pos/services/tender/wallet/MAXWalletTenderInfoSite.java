/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Jan 06, 2017		Atul Shukla		Changes for pos mobikwik Integration FES
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.wallet;

import java.util.HashMap;

import max.retail.stores.pos.ado.tender.MAXTenderTypeEnum;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXWalletTenderInfoSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		HashMap<String, Object> tenderAttributes = cargo.getTenderAttributes();


		tenderAttributes.put(TenderConstants.TENDER_TYPE,MAXTenderTypeEnum.PAYTM);
				

		/*try {
			cargo.getCurrentTransactionADO().validateTenderLimits(tenderAttributes);
					
		} catch (TenderException e) {
			TenderErrorCodeEnum errorCode = e.getErrorCode();

			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
					
			if (errorCode == TenderErrorCodeEnum.OVERTENDER_ILLEGAL) {
				// display error message
				DialogBeanModel model = new DialogBeanModel();
				model.setResourceID("OvertenderNotAllowed");
				model.setType(DialogScreensIfc.ERROR);
				model.setButtonLetter(DialogScreensIfc.BUTTON_OK,
						CommonLetterIfc.FAILURE);
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
				return;
			}
		}*/

		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		uiManager.showScreen(MAXPOSUIManagerIfc.TENDER_WALLET_OPTION);

	}

}
