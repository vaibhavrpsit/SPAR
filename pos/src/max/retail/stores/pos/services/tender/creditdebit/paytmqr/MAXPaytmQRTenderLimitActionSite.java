/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *    Copyright (c) 2022-2023 MAXHyperMarket, Inc.    All Rights Reserved.
 *
 * Rev 1.1  April 09, 2022    Kamlesh Pant 		Paytm QR integration
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
 
package max.retail.stores.pos.services.tender.creditdebit.paytmqr;

import java.util.HashMap;

import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site invokes credit tender limit validation
 */
public class MAXPaytmQRTenderLimitActionSite extends PosSiteActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * */
	public void arrive(BusIfc bus) {
		// add tender type to attributes
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		HashMap attributes = cargo.getTenderAttributes();
		attributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.CREDIT);

		try {
			// invoke tender limit validation
			cargo.getCurrentTransactionADO().validateTenderLimits(attributes);
		} catch (TenderException te) {
			TenderErrorCodeEnum errorCode = te.getErrorCode();

			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			if (errorCode == TenderErrorCodeEnum.OVERTENDER_ILLEGAL) {
				displayErrorDialog(ui, "OvertenderNotAllowed", null, DialogScreensIfc.ERROR);
				return;
			} 
		}
			bus.mail(new Letter("Success"), BusIfc.CURRENT);
		
		

	}

	// ----------------------------------------------------------------------
	/**
	 * Displays the specified Dialog.
	 * 
	 * @param ui
	 *            UI Manager to handle the IO
	 * @param name
	 *            name of the Error Dialog to display
	 * @param args
	 *            arguments for the dialog screen
	 * @param type
	 *            the dialog type
	 **/
	// ----------------------------------------------------------------------
	private void displayErrorDialog(POSUIManagerIfc ui, String name, String[] args, int type) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(name);

		if (args != null) {
			dialogModel.setArgs(args);
		}
		dialogModel.setType(type);

		if (type == DialogScreensIfc.ERROR) {
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");
		} 
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

}
