/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
* Rev 1.1  17/June/2013	Jyoti Rawal, Fix for Bug 6394 Credit Charge Slip is not getting printed
*  Rev 1.0  28/May/2013	Jyoti Rawal, Initial Draft: Changes for Credit Card Functionality 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.tender;

import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import max.retail.stores.pos.ado.tender.MAXTenderCreditADO;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.ResourceBundleUtil;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptConstantsIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXCancelPlutusAisle extends PosLaneActionAdapter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1164184093476215819L;

	/**
	 * revision number supplied by Team Connection
	 */
	public static final String revisionNumber = "$Revision: 1.1 $";

	TenderFactoryIfc factory = null;

	String letter = null;
	private final String dialogId = "PlutusError";

	public void traverse(BusIfc bus)
	{
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE); //Rev 1.1 changes
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		TenderCargo cargo = (TenderCargo)bus.getCargo();
		RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
		TenderADOIfc tendADO = cargo.getLineDisplayTender();
		//Rev 1.1 changes start
		try {
			if (pm.getStringValue("PrintCreditChargeSlip").equalsIgnoreCase("Y"))
			{
			printChargeSlip(tendADO,bus ,cargo);
			}
		}  catch(ParameterException pe)
        {
            logger.warn("Failed to retrieve parameter PrintCreditChargeSlip: " + pe.getMessage() + "");             
        }
		//Rev 1.1 changes end
		try
		{
			HashMap tenderAttributes = tendADO.getTenderAttributes();
			if (tendADO instanceof MAXTenderCreditADO)
			{
				if (((String)tenderAttributes.get(TenderConstants.AUTH_METHOD)).equalsIgnoreCase("ONLINE"))
				{
					txnADO.removeTender(tendADO);
					JournalFactoryIfc jrnlFact = null;
					letter = "Delete";
					displayDialog(ui, dialogId, DialogScreensIfc.ERROR, letter);
					try
					{
						jrnlFact = JournalFactory.getInstance();
					}
					catch (ADOException e)
					{
						logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
						throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
					}
					RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
					registerJournal.journal(tendADO, JournalFamilyEnum.TENDER, JournalActionEnum.DELETE);
				}
			}

		}
		catch (Exception e)
		{
			logger.info("Exception during delete tender "+e);
		}
	}

	protected void printChargeSlip(TenderADOIfc tenderToRemove ,BusIfc bus,TenderCargo cargo) {

		DialogBeanModel model = new DialogBeanModel();
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

		String[] header = new String[] {};
		String[] footer = new String[] {};
		String transactionType;

		transactionType = TransactionConstantsIfc.TYPE_DESCRIPTORS[TransactionIfc.TYPE_VOID];

		POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);

		tenderToRemove.getTenderAttributes().put("TRANSACTION_TYPE", transactionType);
		//Rev 1. changes start
		try
		{
		// Changed Constructor for fixing Charge Slipin case of tender VOID
			//Changes done for code merging(commenting below lines for error resolving)
			/*MAXCreditChargeSlipReciept chargeSlip = new MAXCreditChargeSlipReciept(cargo
					.getCurrentTransactionADO(), tenderToRemove
							.getTenderAttributes(), header, footer, "", "VOID");*/
			// retrieve receipt locale
			Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT);
			// get properties for receipt
			Properties props = ResourceBundleUtil.getGroupText("receipt",
					ReceiptConstantsIfc.RECEIPT_BUNDLES, locale);
			//Changes done for code merging(commenting below lines for error resolving)
			/*chargeSlip.setProps(props);
			pda.printDocument(chargeSlip);

			// For Customer Copy
		// Changed Constructor for fixing Charge Slipin case of tender VOID
			chargeSlip = new MAXCreditChargeSlipReciept(cargo.getCurrentTransactionADO(), tenderToRemove
					.getTenderAttributes(), header, footer, "Customer", "VOID");*/
			// retrieve receipt locale
			locale = LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT);
			// get properties for receipt
			props = ResourceBundleUtil.getGroupText("receipt", ReceiptConstantsIfc.RECEIPT_BUNDLES, locale);
			//Changes done for code merging(commenting below lines for error resolving)
			/*chargeSlip.setProps(props);
			pda.printDocument(chargeSlip);*/

			// Update printer status
			ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
		}
		//Changes done for code merging(commenting below lines for error resolving)
		//catch (DeviceException e)
		catch(Exception e)
		{
			logger.warn("Unable to print debit slip. " + e.getMessage() + "");

			// Update printer status
			ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
			//Changes done for code merging(commenting below lines for error resolving)
			/*if (e.getOrigException() != null)
			{
				logger.warn("DeviceException.NestedException:\n"
						+ Util.throwableToString(e.getOrigException()) + "");
			}*/

			String msg[] = new String[1];
			UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
			msg[0] = utility.retrieveDialogText("RetryContinue.PrinterOffline", "Printer is offline.");

			/* DialogBeanModel model = new DialogBeanModel(); */
			model.setResourceID("RetryContinue");
			model.setType(DialogScreensIfc.RETRY_CONTINUE);
			model.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Retry");
			model.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Continue");
			model.setArgs(msg);
			// display dialog
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
			return;
		}
		//Rev 1.1 changes end

	}

	protected void displayDialog(POSUIManagerIfc ui, String name,
			int dialogType, String letter) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(name);
		dialogModel.setType(dialogType);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, letter);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
}
