/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
* Rev 1.1  17/June/2013	Jyoti Rawal, Fix for Bug 6394 Credit Charge Slip is not getting printed
*  Rev 1.0  28/May/2013	Jyoti Rawal, Initial Draft: Changes for Credit Card Functionality 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.postvoid;

import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.pos.ado.tender.MAXTenderCreditADO;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.transaction.VoidTransaction;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.ResourceBundleUtil;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptConstantsIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.postvoid.VoidCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
public class MAXOfflineMessageAisle extends PosLaneActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 300092664015178312L;
	public static final String LANENAME = "OfflineMessageAisle";

	// --------------------------------------------------------------------------
	/**
	 * @param bus
	 *            the bus traversing this lane
	 **/
	// --------------------------------------------------------------------------

	public void traverse(BusIfc bus) {
		String letter = "Success";
		POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
		String[] header = new String[]{};
		String[] footer = new String[]{};
		DialogBeanModel dialogModel = new DialogBeanModel();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
		.getManager(UIManagerIfc.TYPE);
		VoidCargo cargo = (VoidCargo) bus.getCargo();
		VoidTransaction rdo = (VoidTransaction) cargo
		.getCurrentTransactionADO().toLegacy();
		TenderADOIfc[] charge = cargo.getOriginalTransactionADO()
		.getTenderLineItems(TenderLineItemCategoryEnum.ALL);
		dialogModel.setResourceID("TransactionVoidManualy");
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, letter);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

		for (int i = 0; i < charge.length; i++) {
			if (charge[i] instanceof MAXTenderCreditADO) {

				HashMap tenderAttri = charge[i].getTenderAttributes();
				MAXTenderChargeIfc creditTender = (MAXTenderChargeIfc) charge[i]
				                                                        .toLegacy();
				boolean creditSlip = false;
				ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
				try {
					if(pm.getStringValue("PrintCreditChargeSlip").equalsIgnoreCase("Y")){
						creditSlip = true;
					}
				}  catch(ParameterException pe)
		        {
		            logger.warn("Failed to retrieve parameter PrintCreditChargeSlip: " + pe.getMessage() + "");             
		        }
				if (creditTender.getTransactionType() != null
						&& creditTender.getAuthRemarks() != null) {
					if (creditTender.getTransactionType().equalsIgnoreCase(
					"VOID")
					&& creditTender.getAuthRemarks().equalsIgnoreCase(
					"PROCESSED"))

					{
						if(creditSlip == true){
						try {
							//Changes done for code merging(commenting below lines for error resolving)
							/*MAXCreditChargeSlipReciept chargeSlip = new MAXCreditChargeSlipReciept(
									cargo.getCurrentTransactionADO(), charge[i]
									                                         .getTenderAttributes(), header,
									                                         footer, "");*/
							// retrieve receipt locale
							Locale locale = LocaleMap
							.getLocale(LocaleConstantsIfc.RECEIPT);
							// get properties for receipt
							Properties props = ResourceBundleUtil
							.getGroupText(
									"receipt",
									ReceiptConstantsIfc.RECEIPT_BUNDLES,
									locale);
							//Changes done for code merging(commenting below lines for error resolving)
							/*chargeSlip.setProps(props);
							pda.printDocument(chargeSlip);*/

							// For Customer Copy
							//Changes done for code merging(commenting below lines for error resolving)
							/*chargeSlip = new MAXCreditChargeSlipReciept(cargo
									.getCurrentTransactionADO(), charge[i]
									                                    .getTenderAttributes(), header, footer,
							"Customer");*/
							locale = LocaleMap
							.getLocale(LocaleConstantsIfc.RECEIPT);

							props = ResourceBundleUtil
							.getGroupText(
									"receipt",
									ReceiptConstantsIfc.RECEIPT_BUNDLES,
									locale);
							//Changes done for code merging(commenting below lines for error resolving)
							/*chargeSlip.setProps(props);
							pda.printDocument(chargeSlip);*/
							// Update printer status
							ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS,
									POSUIManagerIfc.ONLINE);
						} catch (Exception e) {
							logger.warn("Unable to print debit slip. "
									+ e.getMessage() + "");

							// Update printer status
							ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS,
									POSUIManagerIfc.OFFLINE);
							//Changes done for code merging(commenting below lines for error resolving)
							/*if (e.getOrigException() != null) {
								logger
								.warn("DeviceException.NestedException:\n"
										+ Util.throwableToString(e
												.getOrigException())
												+ "");
							}*/

							String msg[] = new String[1];
							UtilityManagerIfc utility = (UtilityManagerIfc) bus
							.getManager(UtilityManagerIfc.TYPE);
							msg[0] = utility.retrieveDialogText(
									"RetryContinue.PrinterOffline",
							"Printer is offline.");
							dialogModel.setResourceID("RetryContinue");
							dialogModel
							.setType(DialogScreensIfc.RETRY_CONTINUE);
							dialogModel.setButtonLetter(
									DialogScreensIfc.BUTTON_RETRY, "Retry");
							dialogModel.setButtonLetter(
									DialogScreensIfc.BUTTON_CONTINUE,
							"Continue");
							dialogModel.setArgs(msg);
							ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
									dialogModel);
							return;
							//Rev 1.1 changes end
						}
						}
 					}
				}
			}
		}
	}
}
