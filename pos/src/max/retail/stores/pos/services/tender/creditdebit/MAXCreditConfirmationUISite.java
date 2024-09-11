/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.0  	21 Dec, 2016              Ashish Yadav              Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import max.retail.stores.pos.device.MAXPOSDeviceActions;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.device.cidscreens.CIDAction;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXCreditConfirmationUISite extends PosSiteActionAdapter {
	public void arrive(BusIfc bus) {
		POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
		try {
			UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager("UtilityManager");

			String message = utility.retrieveCommonText("creditConfirmation",
					"Authorize Charge Amount:\n {0}\n Credit Card # {1}");

			TenderCargo cargo = (TenderCargo) bus.getCargo();
			String cardNumber = (String) cargo.getTenderAttributes().get("NUMBER");

			if (cardNumber == null) {
				MSRModel msrModel = (MSRModel) cargo.getTenderAttributes().get("MSR_MODEL");
				cardNumber = msrModel.getAccountNumber();
			}
			String amountString = null;
			if (cargo.getTenderADO() == null) {
				amountString = (String) cargo.getTenderAttributes().get("AMOUNT");
			} else {
				amountString = cargo.getTenderADO().getAmount().toFormattedString();
			}

			String[] parms = { amountString, cardNumber.substring(cardNumber.length() - 4) };

			message = LocaleUtilities.formatComplexMessage(message, parms);
			String cancelButtonLabel = utility.retrieveCommonText("Cancel", "Cancel");
			String okButtonLabel = utility.retrieveCommonText("OK", "Ok");

			if (((MAXPOSDeviceActions)pda).isFormOnline().equals(Boolean.TRUE)) {
				CIDAction resetAction = new CIDAction("TwoButtonScreen", 0);
				pda.cidScreenPerformAction(resetAction);

				CIDAction messageAction = new CIDAction("TwoButtonScreen", 12);
				messageAction.setStringValue(message);
				pda.cidScreenPerformAction(messageAction);

				CIDAction b1LabelAction = new CIDAction("TwoButtonScreen", 14);
				b1LabelAction.setStringValue(okButtonLabel);
				pda.cidScreenPerformAction(b1LabelAction);

				CIDAction b1ActionAction = new CIDAction("TwoButtonScreen", 16);
				b1ActionAction.setStringValue("Success");
				pda.cidScreenPerformAction(b1ActionAction);

				CIDAction b2LabelAction = new CIDAction("TwoButtonScreen", 15);
				b2LabelAction.setStringValue(cancelButtonLabel);
				pda.cidScreenPerformAction(b2LabelAction);

				CIDAction b2ActionAction = new CIDAction("TwoButtonScreen", 17);
				b2ActionAction.setStringValue("Failure");
				pda.cidScreenPerformAction(b2ActionAction);

				CIDAction showAction = new CIDAction("TwoButtonScreen", 2);
				pda.cidScreenPerformAction(showAction);

				POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager("UIManager");
				ui.showScreen("REQUEST_CUSTOMER_VERIFY", new POSBaseBeanModel());
			} else {
				if (("WithOutExpValOCC".equals(bus.getCurrentLetter().getName())))
					bus.mail(new Letter("WithOutExpValOCC"), BusIfc.CURRENT);
				else
				bus.mail(new Letter("Success"), BusIfc.CURRENT);
			}
		} catch (Exception e) {
			logger.warn(e);

			if (("WithOutExpValOCC".equals(bus.getCurrentLetter().getName())))
				bus.mail(new Letter("WithOutExpValOCC"), BusIfc.CURRENT);
			else
			bus.mail(new Letter("Success"), BusIfc.CURRENT);
		}
	}

	public void depart(BusIfc bus) {
		try {
			POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
			CIDAction clearAction = new CIDAction("TwoButtonScreen", 1);
			pda.cidScreenPerformAction(clearAction);
		} catch (Exception e) {
			logger.warn(e);
		}
	}
}