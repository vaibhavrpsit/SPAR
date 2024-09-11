/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.

	Rev 1.0  17/June/2013	Jyoti Rawal, Initial Draft: Fix for Bug 6394 Credit Charge Slip is not getting printed
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */



package max.retail.stores.pos.services.printing;

import java.util.Locale;
import java.util.Properties;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.ResourceBundleUtil;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptConstantsIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.reprintreceipt.ReprintReceiptCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXPrintDuplicateChargeSlipAisle extends PosLaneActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3581041341750303006L;

	public void traverse(BusIfc bus) {

		ReprintReceiptCargo cargo = (ReprintReceiptCargo) bus.getCargo();
		TenderableTransactionIfc trans = cargo.getTenderableTransaction();

		TenderLineItemIfc[] tenders = trans.getTenderLineItems();

		for (int i = 0; i < tenders.length; i++) {

			if (tenders[i] instanceof TenderChargeIfc) {

				TenderFactoryIfc factory = null;
				try {
					factory = (TenderFactoryIfc) ADOFactoryComplex
					.getFactory("factory.tender");
				} catch (ADOException e) {

					e.printStackTrace();
				}
				TenderADOIfc tenderADO = factory.createTender(tenders[i]);
				((ADO) tenderADO).fromLegacy(tenders[i]);
				// Added check Charge Slip Printing only for ONLINE Approval
				if (((TenderChargeIfc)tenders[i]).getAuthorizationMethod().equalsIgnoreCase("ONLINE")) {

					POSUIManagerIfc ui = (POSUIManagerIfc) bus
					.getManager(UIManagerIfc.TYPE);

					ParameterManagerIfc pm = (ParameterManagerIfc) bus
					.getManager(ParameterManagerIfc.TYPE);

					String[] header = new String[] {};
					String[] footer = new String[] {};

					POSDeviceActions pda = new POSDeviceActions(
							(SessionBusIfc) bus);

					// Get debit tenders
					try {
						//Changes done for code merging(commenting below lines for error resolving)
						/*MAXCreditChargeSlipReciept chargeSlip = new MAXCreditChargeSlipReciept(
								trans.getTransactionID(), tenderADO
								.getTenderAttributes(), header, footer,
								"", getTransactionType(trans
										.getTransactionType()), true);*/
						// retrieve receipt locale
						Locale locale = LocaleMap
						.getLocale(LocaleConstantsIfc.RECEIPT);
						// get properties for receipt
						Properties props = ResourceBundleUtil.getGroupText(
								"receipt", ReceiptConstantsIfc.RECEIPT_BUNDLES,
								locale);
						//Changes done for code merging(commenting below lines for error resolving)
						/*chargeSlip.setProps(props);
						pda.printDocument(chargeSlip);

						// For Customer Copy

						chargeSlip = new MAXCreditChargeSlipReciept(trans
								.getTransactionID(), tenderADO
								.getTenderAttributes(), header, footer,
								"Customer", getTransactionType(trans
										.getTransactionType()), true);*/
						// retrieve receipt locale
						locale = LocaleMap
						.getLocale(LocaleConstantsIfc.RECEIPT);
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
						logger.warn("Unable to print debit slip. "
								+ e.getMessage() + "");

						// Update printer status
						ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS,
								POSUIManagerIfc.OFFLINE);
						//Changes done for code merging(commenting below lines for error resolving)
						/*if (e.getOrigException() != null) {
							logger.warn("DeviceException.NestedException:\n"
									+ Util.throwableToString(e
											.getOrigException()) + "");
						}*/

						String msg[] = new String[1];
						UtilityManagerIfc utility = (UtilityManagerIfc) bus
						.getManager(UtilityManagerIfc.TYPE);
						msg[0] = utility.retrieveDialogText(
								"RetryContinue.PrinterOffline",
						"Printer is offline.");

						DialogBeanModel model = new DialogBeanModel();
						model.setResourceID("RetryContinue");
						model.setType(DialogScreensIfc.RETRY_CONTINUE);
						model.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
						"RetryChargeSlip");
						model.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE,
						"Continue");
						model.setArgs(msg);

						// display dialog
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

					}

				}
			}

		}
		bus.mail("Continue");
	}

	protected String getTransactionType(int transactionType) {

		String transType = "";
		switch(transactionType)
		{
		case 1:
			transType = "SALE";
			break;
		case 2:
			transType = "REFUND";
			break;
		case 3:
			transType = "POST VOID";
			break;
		default :
			transType = "";

		}


		return transType;
	}
}