/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.

	Rev 1.0  17/June/2013	Jyoti Rawal, Initial Draft: Fix for Bug 6394 Credit Charge Slip is not getting printed
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.printing;

import java.util.Locale;
import java.util.Properties;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.ResourceBundleUtil;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptConstantsIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXPrintChargeSlipSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5086729610170166635L;

	public void arrive(BusIfc bus) {
		MAXPrintingCargo cargo = (MAXPrintingCargo) bus.getCargo();
		boolean sendMail = true;

		POSUIManagerIfc ui = (POSUIManagerIfc) bus
		.getManager(UIManagerIfc.TYPE);

		ParameterManagerIfc pm = (ParameterManagerIfc) bus
		.getManager(ParameterManagerIfc.TYPE);
		/*
		 * String debitSlipConfig = cargo.getParameterValue(pm,
		 * "PrintDebitSlip");
		 */
		String[] header = cargo.getReceiptText(pm, "ReceiptHeader");
		String[] footer = cargo.getReceiptText(pm, "ReceiptFooter");

		POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);

		// Get debit tenders
		try {
			//Changes done for code merging(commenting below lines for error resolving)
			/*MAXCreditChargeSlipReciept chargeSlip = new MAXCreditChargeSlipReciept(cargo.getTransactionId(),
					cargo.getTenderattributes(), header, footer, "");*/
			// retrieve receipt locale
			Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT);
			// get properties for receipt
			Properties props = ResourceBundleUtil.getGroupText("receipt",
					ReceiptConstantsIfc.RECEIPT_BUNDLES, locale);
			//Changes done for code merging(commenting below lines for error resolving)
			/*chargeSlip.setProps(props);
			pda.printDocument(chargeSlip);

			// For Customer Copy

			chargeSlip = new MAXCreditChargeSlipReciept(cargo.getTransactionId(),
					cargo.getTenderattributes(), header, footer, "Customer");*/
			// retrieve receipt locale
			locale = LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT);
			// get properties for receipt
			props = ResourceBundleUtil.getGroupText("receipt",
					ReceiptConstantsIfc.RECEIPT_BUNDLES, locale);
			//Changes done for code merging(commenting below lines for error resolving)
			/*chargeSlip.setProps(props);
			pda.printDocument(chargeSlip);*/

			// Update printer status
			ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS,
					POSUIManagerIfc.ONLINE);
		} 
		//catch (DeviceException e) {
		catch (Exception e) {
			logger.warn("Unable to print debit slip. " + e.getMessage() + "");

			// Update printer status
			ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS,
					POSUIManagerIfc.OFFLINE);
			//Changes done for code merging(commenting below lines for error resolving)
			/*if (e.getOrigException() != null) {
				logger.warn("DeviceException.NestedException:\n"
						+ Util.throwableToString(e.getOrigException()) + "");
			}*/

			String msg[] = new String[1];
			UtilityManagerIfc utility = (UtilityManagerIfc) bus
			.getManager(UtilityManagerIfc.TYPE);
			msg[0] = utility.retrieveDialogText("RetryContinue.PrinterOffline",
			"Printer is offline.");

			DialogBeanModel model = new DialogBeanModel();
			model.setResourceID("RetryContinue");
			model.setType(DialogScreensIfc.RETRY_CONTINUE);
			model.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Retry");
			model.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Continue");
			model.setArgs(msg);

			// display dialog
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

			sendMail = false;
		}
		// end Trans Void
		if (sendMail) {
			bus.mail(new Letter("Done"), BusIfc.CURRENT);
		}
	}
}
