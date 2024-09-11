/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev 1.2  	Jan 22,2017  		Ashish Yadav  	Changes Loyalty OTP FES (issue during post void loyalty transaction)
 *	Rev	1.1 	Dec 20, 2016		Mansi Goel		Changes for Gift Card FES
 *	Rev 1.0  	Aug 22,2016  		Ashish Yadav  	Changes for code merging	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.postvoid;

import max.retail.stores.pos.ado.store.MAXRegisterADO;
import max.retail.stores.pos.ado.transaction.MAXVoidErrorCodeEnum;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionID;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
// Foundation imports
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.VoidErrorCodeEnum;
import oracle.retail.stores.pos.ado.transaction.VoidException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.postvoid.VoidCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site checks to see that the transaction exists, was created on the same
 * day, and has a transaction type that can be voided.
 * 
 */
@SuppressWarnings("serial")
public class MAXValidateTransactionIDSite extends PosSiteActionAdapter {
	/**
	 * purchase date field
	 */
	public static final String PURCHASE_DATE_FIELD = "purchaseDateField";

	/**
	 * store number
	 */
	public static final String STORE_NUMBER_FIELD = "storeNumberField";

	/**
	 * register number
	 */
	public static final String REGISTER_NUMBER_FIELD = "registerNumberField";

	/**
	 * transaction number
	 */
	public static final String TRANS_NUMBER_FIELD = "transactionNumberField";

	/**
	 * Constant for error screen
	 */
	public static final String INVALID_RETURN_NUMBER = "InvalidReturnNumber";


	@Override
	public void arrive(BusIfc bus) {
		VoidCargo cargo = (VoidCargo) bus.getCargo();
		RetailTransactionADOIfc txn = null;
		RegisterADO registerADO = ContextFactory.getInstance().getContext().getRegisterADO();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

		try {
			String receiptId = ui.getInput();

			if (receiptId.length() != TransactionID.getTransactionIDLength()) {
				// "Receipt" or "Other" number.
				// Using "generic dialog bean".
				DialogBeanModel dialogModel = new DialogBeanModel();
				dialogModel.setResourceID(INVALID_RETURN_NUMBER);
				dialogModel.setType(DialogScreensIfc.ERROR);
				// set and display the model
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			} else {
				TransactionIDIfc receiptNo = DomainGateway.getFactory().getTransactionIDInstance();
				receiptNo.setTransactionID(receiptId);
				// Changes starts for Rev 1.3 (Ashish : Loyalty OTP)
				txn = ((RegisterADO)registerADO).loadTransaction(utility.getRequestLocales(), receiptNo.getTransactionIDString());
				// Changes ends for Rev 1.3 (Ashish : Loyalty OTP)
			}

			if (txn == null) {
				displayNotFoundDialog(bus);
				return;
			}

			try {
				if (txn.isVoidable(registerADO.getCurrentTillID())) {
					cargo.setOriginalTransactionADO(txn);
					bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
				}
			} catch (VoidException ve) {
				// Display proper exception dialog
				VoidErrorCodeEnum error = ve.getErrorCode();

				if (error == VoidErrorCodeEnum.DIFFERENT_TILL) {
					displayErrorDialog("DifferentTillError", ui);
					return;
				} else if (error == VoidErrorCodeEnum.PREVIOUSLY_VOIDED) {
					displayErrorDialog("TransactionAlreadyVoided", ui);
					return;
				} else if (error == VoidErrorCodeEnum.TRANSACTION_MODIFIED) {
					displayErrorDialog("VoidModifiedTransaction", ui);
					return;
				} else if (error == VoidErrorCodeEnum.INVALID_TRANSACTION) {
					displayErrorDialog("InvalidTransactionType", ui);
					return;
				} else if (error == VoidErrorCodeEnum.VOID_GIFT_CARD_INVALID) {
					displayErrorDialog("VoidGiftCardInvalid", ui);
					return;
				} else if (error == VoidErrorCodeEnum.GIFT_CARD_VOID_INVALID) {
					displayErrorDialog("GiftCardVoidInvalid", ui);
					return;
				} else if (error == VoidErrorCodeEnum.GIFT_CERTIFICATE_VOID_INVALID) {
					displayErrorDialog("GiftCertificateVoidInvalid", ui);
					return;
				} else if (error == VoidErrorCodeEnum.STORE_CREDIT_VOID_INVALID) {
					displayErrorDialog("StoreCreditVoidInvalid", ui);
					return;
				} else if (error == VoidErrorCodeEnum.AUTH_TENDER_NOT_VOIDABLE) {
					displayErrorDialog("AuthTenderNotVoidable", ui);
					return;
				//Changes for Rev 1.1 : Starts
				} else if (error == MAXVoidErrorCodeEnum.GIFT_CARD_NOT_ALLOWED) {
					displayGiftcardNotAllowedDialog(bus);
					return;
				//Changes for Rev 1.1 : Ends
				} else {
					logger.error("VoidException occurred with unknown error: " + error.toString());
				}
			}
		} catch (DataException de) {
			if (de.getErrorCode() == DataException.NO_DATA) {
				displayNotFoundDialog(bus);
				return;
			}
			displayDatabaseErrorDialog(bus, de);
		}
	}

	private void displayNotFoundDialog(BusIfc bus) {
		DialogBeanModel dialogModel;
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		String[] args = new String[1];
		args[0] = utility.retrieveDialogText("TransactionNotFound.TransactionNotFound",
				"Transaction number not found for this date or till.");
		dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("TransactionNotFound");
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setArgs(args);

		// display dialog
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
	}

	/**
	 * Displays the error dialog for the given resource ID.
	 * 
	 * @param resourceID
	 * @param ui
	 */
	protected void displayErrorDialog(String resourceID, POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(resourceID);
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

	}

	private void displayDatabaseErrorDialog(BusIfc bus, DataException de) {
		String errorString[] = new String[2];
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		errorString[0] = utility.getErrorCodeString(de.getErrorCode());
		errorString[1] = "";
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("DATABASE_ERROR");
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setArgs(errorString);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	//Changes for Rev 1.0 : Starts
	private void displayGiftcardNotAllowedDialog(BusIfc bus) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("GiftCardVoidNotAllowed");
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
		dialogModel.setType(DialogScreensIfc.ERROR);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
	//Changes for Rev 1.0 : Ends
}
