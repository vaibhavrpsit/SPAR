/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.1 	Jun 23, 2017		Jyoti Yadav		HSN number issue for GC Issue/Reload
 *	Rev	1.0 	Dec 20, 2016		Mansi Goel		Changes for Gift Card FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.giftcard;

import java.util.LinkedList;
import java.util.List;

import max.retail.stores.domain.manager.tenderauth.MAXTenderAuthConstantsIfc;
import max.retail.stores.domain.transaction.MAXSearchCriteriaIfc;
import max.retail.stores.pos.services.tender.activation.MAXActivationCargo;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.PLUTransaction;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

import org.apache.log4j.Logger;

public class MAXGiftCardUtilities extends GiftCardUtilities implements MAXGiftCardConstantsIfc {

	public static DialogBeanModel createActivatedGiftCardNumErrorDialogModel() {
		DialogBeanModel dialogModel = new DialogBeanModel();

		dialogModel.setResourceID(ACTIVE_GIFT_CARD_NUMBER_ERROR);
		dialogModel.setType(DialogScreensIfc.ERROR);

		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, INVALID_CARD_NUM_LETTER);
		return dialogModel;
	}

	public static DialogBeanModel createDupllicateGiftCardNumErrorDialogModel() {
		DialogBeanModel dialogModel = new DialogBeanModel();

		dialogModel.setResourceID(DUPLICATE_GIFT_CARD_NUMBER_ERROR);
		dialogModel.setType(DialogScreensIfc.ERROR);

		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, INVALID_CARD_NUM_LETTER);
		return dialogModel;
	}

	public static DialogBeanModel createAlreadyGiftCardNumErrorDialogModel() {
		DialogBeanModel dialogModel = new DialogBeanModel();

		dialogModel.setResourceID(CREATE_GIFTCARD_ALREADY_EXISTS);
		dialogModel.setType(DialogScreensIfc.ERROR);

		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, INVALID_CARD_CAST_NUM_LETTER);
		return dialogModel;
	}

	public static DialogBeanModel createDuplicateGiftCardNumErrorDialogModel() {
		DialogBeanModel dialogModel = new DialogBeanModel();

		dialogModel.setResourceID(DUPLICATE_GIFT_CARD_NUMBER_ERROR);
		dialogModel.setType(DialogScreensIfc.ERROR);

		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
		return dialogModel;
	}

	public static DialogBeanModel createMaximumGiftCardIssuedDialogModel() {
		DialogBeanModel dialogModel = new DialogBeanModel();

		dialogModel.setResourceID("MAXIMUM_GIFT_CARD_NUMBER_ISSUED_ERROR");
		dialogModel.setType(DialogScreensIfc.ERROR);

		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "NoIssue");
		return dialogModel;
	}

	public static DialogBeanModel createInvalidTransactionDialogModel() {
		DialogBeanModel dialogModel = new DialogBeanModel();

		dialogModel.setResourceID("TRANSACTION_TYPE_ERROR");
		dialogModel.setType(DialogScreensIfc.ERROR);

		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "NoIssue");
		return dialogModel;
	}

	public static DialogBeanModel createAlreadyGiftCardNumErrorDialogModel(boolean a) {
		DialogBeanModel dialogModel = new DialogBeanModel();

		dialogModel.setResourceID(CREATE_GIFTCARD_ALREADY_EXISTS);
		dialogModel.setType(DialogScreensIfc.ERROR);

		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
		return dialogModel;
	}

	public static DialogBeanModel createInvalidGiftCardNumErrorDialogModel() {
		DialogBeanModel dialogModel = new DialogBeanModel();

		dialogModel.setResourceID("InvalidGiftCardNumberError");
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "InvalidCardNumber");
		return dialogModel;
	}


	// Changes starts for code merging(below method is not present in base 14)
	public static SaleReturnLineItemIfc getCurrentLineItem(MAXActivationCargo cargo) {
		SaleReturnLineItemIfc item = null;
		SaleReturnLineItemIfc[] items = getItems(cargo);
		if (items != null) {
			// Get index for the current line item. Subtract 1 because the
			// counter
			// has already been incremented during beginning of site processing
			int index = cargo.getLineItemCounter() - 1;
			if (index >= 0 && index < items.length) {
				item = items[index];
			}
		}
		return item;
	}

	public static SaleReturnLineItemIfc[] getItems(MAXActivationCargo cargo) {
		RetailTransactionIfc retailTransaction = cargo.getRetailTransaction();
		if (retailTransaction == null) {
			return null;
		} else {
			return retailTransaction.getProductGroupLineItems(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD);
		}
	}

	public static TenderGiftCardIfc[] getTenders(MAXActivationCargo cargo) {
		if (cargo.getRetailTransaction() == null) {
			return new TenderGiftCardIfc[0];
		}

		TenderLineItemIfc[] tenders = cargo.getRetailTransaction().getTenderLineItems();
		List giftCardTenderList = new LinkedList();
		for (int i = 0; i < tenders.length; i++) {
			TenderLineItemIfc tender = tenders[i];
			if (tender.getTypeCode() == TenderLineItemIfc.TENDER_TYPE_GIFT_CARD) {
				TenderGiftCardIfc giftCardTender = (TenderGiftCardIfc) tender;
				if (giftCardTender.getGiftCard() == null) {
					giftCardTender.setGiftCard(DomainGateway.getFactory().getGiftCardInstance());
				}
				giftCardTenderList.add(tender);

			}
		}

		TenderGiftCardIfc[] result = new TenderGiftCardIfc[giftCardTenderList.size()];
		result = (TenderGiftCardIfc[]) giftCardTenderList.toArray(result);
		return result;
	}

	public static DialogBeanModel createIssueErrorDialogModel(UtilityManagerIfc utility, String responseCode) {
		String errorMessage = null;
		if (responseCode.equals(MAXTenderAuthConstantsIfc.NOTFOUND)) {
			errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
					ERROR_UNKNOWN_CARD_TAG, ERROR_UNKNOWN_CARD);
		} else if (responseCode.equals(MAXTenderAuthConstantsIfc.INVALID)) {
			errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
					ERROR_INVALID_REQUEST_TAG, ERROR_INVALID_REQUEST);
		} else if (responseCode.equals(MAXTenderAuthConstantsIfc.DECLINED)) {
			errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
					ERROR_DECLINED_TAG, ERROR_DECLINED);
		} else if (responseCode.equals(MAXTenderAuthConstantsIfc.UNKNOWN)) {
			errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
					ERROR_UNKNOWN_TAG, ERROR_UNKNOWN);
		} else {
			errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
					ERROR_UNKNOWN_TAG, ERROR_UNKNOWN);
		}

		String args[] = new String[1];
		args[0] = errorMessage;
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(GIFT_CARD_ISSUE_ERROR_DIALOG_ID);
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, ACTIVATION_RETRY_LETTER);
		dialogModel.setArgs(args);

		return dialogModel;
	}

	public static DialogBeanModel createProcessorOfflineDialogModel(UtilityManagerIfc utility, String responseCode) {
		String errorMessage = null;
		if (responseCode.equals(MAXTenderAuthConstantsIfc.TIMEOUT))
			errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
					ERROR_TIMEOUT_TAG, ERROR_TIMEOUT);
		else if (responseCode.equals(MAXTenderAuthConstantsIfc.OFFLINE))
			errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
					ERROR_OFFLINE_TAG, ERROR_OFFLINE);
		else if (responseCode.equals(MAXTenderAuthConstantsIfc.DECLINED)) {
			errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
					ERROR_DECLINED_TAG, ERROR_DECLINED);
		}
		String args[] = new String[1];
		args[0] = errorMessage;
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(GIFT_CARD_PROCESSOR_OFFLINE_ERRIR_DIALOG_ID);
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Referral");
		dialogModel.setArgs(args);

		return dialogModel;
	}
	// Changes neds for code merging
	
	//Changes for Rev 1.0 : Starts
	public static GiftCardPLUItemIfc getPluItem(POSUIManagerIfc ui, GiftCardCargo cargo, String defaultItemID, Logger logger, String serviceName, boolean a) {
		PLUTransaction pluTransaction = null;

		pluTransaction = (PLUTransaction) DataTransactionFactory.create(DataTransactionKeys.PLU_TRANSACTION);
 
		GiftCardPLUItemIfc item = null;

		MAXSearchCriteriaIfc inquiry = (MAXSearchCriteriaIfc) DomainGateway.getFactory().getSearchCriteriaInstance();
		inquiry.setLocaleRequestor(LocaleMap.getSupportedLocaleRequestor());

		String itemID = "";
		item = (GiftCardPLUItemIfc) cargo.getPLUItem();
		if (item != null)
			itemID = item.getItemID();

		if (itemID.length() != 0)
			inquiry.setItemID(itemID);
		else
			inquiry.setItemID(defaultItemID);

		try {
			PLUItemIfc pluItem = pluTransaction.getPLUItem(inquiry);
			if (pluItem instanceof GiftCardPLUItemIfc)
				item = (GiftCardPLUItemIfc) pluItem;
		} catch (DataException de) {
			logger.warn("Unable to find pluItem " + de.getMessage() + "");

			int errorCode = de.getErrorCode();
			cargo.setDataExceptionErrorCode(errorCode);
			/*Change for Rev 1.1: Start*/
			DialogBeanModel model = new DialogBeanModel();
			if(de.getMessage().equalsIgnoreCase("HSN number not found")){
				model.setResourceID("ITEM_HSN_NOT_FOUND");
				model.setType(DialogScreensIfc.ERROR);
				//model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonActionsIfc.CONTINUE);
				model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "InvalidCardNumber");
			}else{
				model = GiftCardUtilities.createGenericDataBaseErrorDialogModel(DataException.getErrorCodeString(errorCode));	
			}
			//DialogBeanModel model = GiftCardUtilities.createGenericDataBaseErrorDialogModel(DataException.getErrorCodeString(errorCode));
			/*Change for Rev 1.1: End*/
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		}
		return item;
	}
	//Changes for Rev 1.0 : Ends
}
