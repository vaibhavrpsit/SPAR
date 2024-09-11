/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.0  	21 Dec, 2016              Ashish Yadav              Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXDebitTenderActionSite extends PosSiteActionAdapter {
	public void arrive(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		TenderADOIfc debitTender = cargo.getTenderADO();
		try {
			cargo.getCurrentTransactionADO().addTender(debitTender);
			cargo.setLineDisplayTender(debitTender);

			JournalFactoryIfc jrnlFact = null;
			try {
				jrnlFact = JournalFactory.getInstance();
			} catch (ADOException e) {
				logger.error("Configuration problem: could not instantiate JournalFactoryIfc instance", e);
				throw new RuntimeException("Configuration problem: could not instantiate JournalFactoryIfc instance",
						e);
			}
			RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
			registerJournal.journal(debitTender, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);

			bus.mail(new Letter("Success"), BusIfc.CURRENT);
		} catch (TenderException e) {
			TenderErrorCodeEnum error = e.getErrorCode();

			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager("UIManager");
			UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager("UtilityManager");

			if (error == TenderErrorCodeEnum.BAD_MAG_SWIPE) {
				displayBadMagStripeDialog(ui);
				return;
			}
			if (error == TenderErrorCodeEnum.DEBIT_NOT_SWIPED) {
				displayNotSwipedDialog(utility, ui);
				return;
			}
			if (error == TenderErrorCodeEnum.INVALID_CARD_NUMBER) {
				showInvalidNumberDialog(utility, ui);
				return;
			}
			if (error != TenderErrorCodeEnum.EXPIRED)
				return;
			showExpiredCardDialog(utility, ui);
		}
	}

	protected void displayBadMagStripeDialog(POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();

		dialogModel.setResourceID("DebitBadMSRReadError");
		dialogModel.setType(4);
		dialogModel.setButtonLetter(5, "No");
		ui.showScreen("DIALOG_TEMPLATE", dialogModel);
	}

	protected void displayNotSwipedDialog(UtilityManagerIfc utility, POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("InvalidDebitNoSwipe");
		dialogModel.setType(7);
		dialogModel.setButtonLetter(0, "Invalid");

		String cardString = utility.retrieveDialogText("InvalidDebitNoSwipe.Debit", "debit");
		String[] args = new String[2];
		args[0] = cardString;
		args[1] = cardString;

		dialogModel.setArgs(args);
		ui.showScreen("DIALOG_TEMPLATE", dialogModel);
	}

	protected void showInvalidNumberDialog(UtilityManagerIfc utility, POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("InvalidNumberError");
		dialogModel.setType(1);
		dialogModel.setButtonLetter(0, "Invalid");

		String[] args = new String[2];
		args[1] = utility.retrieveDialogText("Debit", "Debit");
		int tmp56_55 = 1;
		String[] tmp56_53 = args;
		tmp56_53[tmp56_55] = tmp56_53[tmp56_55] + " " + utility.retrieveDialogText("Number", "number");
		Locale locale = LocaleMap.getLocale("UI_LOCALE_KEY");
		args[0] = args[1].toLowerCase(locale);
		dialogModel.setArgs(args);
		ui.showScreen("DIALOG_TEMPLATE", dialogModel);
	}

	protected void showExpiredCardDialog(UtilityManagerIfc utility, POSUIManagerIfc ui) {
		String titleTag = "ExpiredDebitCardTitle";
		String cardString = utility.retrieveDialogText("ExpiredCardError.Debit", "debit");

		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("ExpiredCardError");
		dialogModel.setType(7);
		dialogModel.setButtonLetter(0, "Invalid");

		dialogModel.setTitleTag(titleTag);
		String[] args = new String[2];
		args[0] = cardString;
		args[1] = cardString;
		dialogModel.setArgs(args);
		ui.showScreen("DIALOG_TEMPLATE", dialogModel);
	}
}