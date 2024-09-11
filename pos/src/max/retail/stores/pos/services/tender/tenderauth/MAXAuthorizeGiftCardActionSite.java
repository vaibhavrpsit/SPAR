/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  24/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.tenderauth;

import java.util.HashMap;

import max.retail.stores.domain.manager.tenderauth.MAXTenderAuthConstantsIfc;
import max.retail.stores.pos.ado.tender.MAXAuthorizableADOIfc;
import max.retail.stores.pos.ado.tender.MAXReversibleTenderADOIfc;
import max.retail.stores.pos.ado.tender.MAXTenderConstants;
import max.retail.stores.pos.services.giftcard.MAXGiftCardUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.AuthResponseCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderGiftCardADO;
import oracle.retail.stores.pos.ado.utility.AuthorizationException;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * Authorizes a Gift Card tender
 */
public class MAXAuthorizeGiftCardActionSite extends PosSiteActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -84171544954153934L;

	/*
	 * Using the existing bean for check referral screen.
	 * 
	 * @see
	 * com.extendyourstore.foundation.tour.application.SiteActionAdapter#arrive
	 * (com.extendyourstore.foundation.tour.ifc.BusIfc)
	 */
	public void arrive(BusIfc bus) {
		MAXTenderAuthCargo cargo = (MAXTenderAuthCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

		// journal the result
		JournalFactoryIfc jrnlFact = null;
		try {
			jrnlFact = JournalFactory.getInstance();
		} catch (ADOException e) {
			logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
			throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
		}
		RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();

		// Display authorizing UI
		//displayAuthorizationUI(bus);

		// get and authorize the current gift card tender
		String letter = null;
		try {
			HashMap map = new HashMap();
			// get transaction sequence number
			map.put(TenderConstants.SEQUENCE_NUMBER, cargo.getCurrentTransactionADO().getTransactionID().substring(7));
			map.put(TenderConstants.TRANSACTION_NUMBER, cargo.getCurrentTransactionADO().getTransactionID());
			String store = cargo.getRegister().getWorkstation().getStoreID();
			String workStationID = cargo.getRegister().getWorkstation().getWorkstationID();
			map.put(TenderConstants.AUTH_SEQUENCE_NUMBER, store + workStationID);
			map.put(TenderConstants.STORE_NUMBER, Gateway.getProperty("application", "StoreID", ""));
			if (cargo.getCurrentAuthTender() instanceof TenderGiftCardADO) {
				map.put(MAXTenderConstants.GIFT_CARD_APPROVED_FLAG, String.valueOf(cargo.isGiftCardApproved()));
			}

			if (cargo.getTenderCategory().equals(TenderLineItemCategoryEnum.AUTH_PENDING))// Rev 1.0 changes 
			{
				// attempt the authorization
				((MAXAuthorizableADOIfc) cargo.getCurrentAuthTender()).authorize(map);
			} else if (cargo.getTenderCategory().equals(TenderLineItemCategoryEnum.REVERSAL_PENDING)) {
				((MAXReversibleTenderADOIfc) cargo.getCurrentAuthTender()).reverse(map);
			} else {
				// attempt to reverse the authorization
				((MAXReversibleTenderADOIfc) cargo.getCurrentAuthTender()).voidAuth(map);
			}

			// if we get here, it was approved.
			if (((TenderGiftCardADO) cargo.getCurrentAuthTender()).isCurrentBalanceLessThanMaxChangeLimit()) {
				displayDepleteDialog(bus, (TenderGiftCardADO) cargo.getCurrentAuthTender());
			} else {
				letter = "Success";
			}
			if (cargo.isGiftCardApproved()) // Rev 1.0 changes 
			{
				letter = "Success";
			}

			registerJournal.journal(cargo.getCurrentAuthTender(), JournalFamilyEnum.TENDER, JournalActionEnum.AUTHORIZATION);

		} catch (AuthorizationException e) {
			AuthResponseCodeEnum error = e.getResponseCode();

			if (error == AuthResponseCodeEnum.DECLINED) {
				DialogBeanModel model = MAXGiftCardUtilities.createIssueErrorDialogModel(utility, e.getResponseDisplay());
				model.setResourceID("GiftCardAuthError");
				model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
			} else if (error == AuthResponseCodeEnum.REFERRAL) {
				letter = "ReferCharge";
			} else if (error == AuthResponseCodeEnum.TIMEOUT) {
				DialogBeanModel model = MAXGiftCardUtilities.createProcessorOfflineDialogModel(utility, MAXTenderAuthConstantsIfc.TIMEOUT);
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
			} else if (error == AuthResponseCodeEnum.OFFLINE) {
				//DialogBeanModel model = GiftCardUtilities.createProcessorOfflineDialogModel(utility, TenderAuthConstantsIfc.OFFLINE);
				//ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
				letter = "Referral";
			} else if (error == AuthResponseCodeEnum.ERROR_RETRY) {
				handleErrorRetry(bus, e);
			}
		}

		if (letter != null) {
			bus.mail(new Letter(letter), BusIfc.CURRENT);
		}
	}

	/**
	 * Displays dialog prompting for gift card depletion
	 * 
	 * @param bus
	 */
	protected void displayDepleteDialog(BusIfc bus, TenderGiftCardADO giftCardTender) {
		String args[] = new String[2];
		args[0] = (String) giftCardTender.getTenderAttributes().get(TenderConstants.NUMBER);
		args[1] = (String) giftCardTender.getTenderAttributes().get(TenderConstants.REMAINING_BALANCE);

		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setArgs(args);
		dialogModel.setResourceID("GiftCardRemainingBalance");
		dialogModel.setType(DialogScreensIfc.CONFIRMATION);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Deplete");
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Success");

		// display dialog
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	/**
	 * Simply return the appropriate letter
	 * 
	 * @return
	 */
	protected void handleErrorRetry(BusIfc bus, AuthorizationException e) {
		String[] args = new String[1];
		args[0] = e.getResponseDisplay();

		// Display error message
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("AuthRetry");
		dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
		dialogModel.setArgs(args);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Yes");
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, "No");
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	/**
	 * Displays Authorization UI screen
	 * 
	 * @param bus
	 */
	protected void displayAuthorizationUI(BusIfc bus) {
		// get manager for ui and put up "authorizing..." screen
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

		PromptAndResponseModel parModel = new PromptAndResponseModel();
		// get text
		String promptText = utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC, BundleConstantsIfc.COMMON_BUNDLE_NAME,
				"GiftCardAuthorizationPrompt", "GiftCardAuthorizationPrompt");
		parModel.setPromptText(promptText);

		POSBaseBeanModel baseModel = new POSBaseBeanModel();
		baseModel.setPromptAndResponseModel(parModel);
		ui.showScreen(POSUIManagerIfc.AUTHORIZATION, baseModel);

		ui.statusChanged(POSUIManagerIfc.CREDIT_STATUS, POSUIManagerIfc.ONLINE);
	}
}