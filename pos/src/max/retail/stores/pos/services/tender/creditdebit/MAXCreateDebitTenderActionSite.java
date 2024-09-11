/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.0  	21 Dec, 2016              Ashish Yadav             Intial Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.HashMap;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Attempts to create a Debit tender
 */
public class MAXCreateDebitTenderActionSite extends PosSiteActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 **/
	public void arrive(BusIfc bus) {
		// Create the debit tender from the tender attributes. If the creation
		// fails,
		// it is because the number was manually entered of because the mag
		// stripe is bad.
		// attempt to create a Debit tender from the tender attributes.
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		HashMap tenderAttributes = cargo.getTenderAttributes();
		TenderADOIfc debitTender = null;
		try {
			TenderFactoryIfc factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
			debitTender = factory.createTender(tenderAttributes);
			debitTender.validate();
			cargo.setTenderADO(debitTender);

			bus.mail(new Letter("Success"), BusIfc.CURRENT);
		} catch (ADOException adoe) {
			adoe.printStackTrace();
		} catch (TenderException e) {
			TenderErrorCodeEnum error = e.getErrorCode();

			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

			if (error == TenderErrorCodeEnum.DEBIT_NOT_SWIPED) {
				displayNotSwipedDialog(utility, ui);
				return;
			}
			if (error == TenderErrorCodeEnum.INVALID_CARD_NUMBER) {
				showInvalidNumberDialog(utility, ui);
				return;
			}
			if (error == TenderErrorCodeEnum.BAD_MAG_SWIPE) {
				displayBadMagStripeDialog(ui);
				return;
			}
			if (error == TenderErrorCodeEnum.EXPIRED) {
				showExpiredCardDialog(utility, ui);
				return;
			} else {
				assert (false) : "Unhandled exception.  THis should not happen";
			}
		}
	}

	// --------------------------------------------------------------------------
	/**
	 * Shows the Invalid Number Error dialog screen.
	 */
	// --------------------------------------------------------------------------
	protected void showInvalidNumberDialog(UtilityManagerIfc utility, POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("InvalidNumberError");
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");

		String[] args = new String[2];
		args[1] = utility.retrieveDialogText("CreditCard", "Credit Card");
		args[1] += " " + utility.retrieveDialogText("Number", "number");
		Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
		args[0] = args[1].toLowerCase(locale);
		dialogModel.setArgs(args);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	/**
	 * Display dialog indicating bad mag swipe
	 * 
	 * @param utility
	 * @param ui
	 */
	protected void displayNotSwipedDialog(UtilityManagerIfc utility, POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("InvalidDebitNoSwipe");
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");

		String cardString = utility.retrieveDialogText("InvalidDebitNoSwipe.Debit", "debit");
		String args[] = new String[2];
		args[0] = cardString;
		args[1] = cardString;

		dialogModel.setArgs(args);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	/**
	 * Display dialog indicating bad mag swipe
	 * 
	 * @param utility
	 * @param ui
	 */
	protected void displayBadMagStripeDialog(POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();

		// set model properties
		dialogModel.setResourceID("DebitBadMSRReadError");
		dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, "No");
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	// --------------------------------------------------------------------------
	/**
	 * Shows the expired card dialog screen.
	 */
	// --------------------------------------------------------------------------
	protected void showExpiredCardDialog(UtilityManagerIfc utility, POSUIManagerIfc ui) {
		// set screen args
		String titleTag = "ExpiredDebitCardTitle";
		String cardString = utility.retrieveDialogText("ExpiredCardError.Debit", "debit");

		// Display error message
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("ExpiredCardError");
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.INVALID);
		dialogModel.setTitleTag(titleTag);
		String args[] = new String[2];
		args[0] = cardString;
		args[1] = cardString;
		dialogModel.setArgs(args);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

}
