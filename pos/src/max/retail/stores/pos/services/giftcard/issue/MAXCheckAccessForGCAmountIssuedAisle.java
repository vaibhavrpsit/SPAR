/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Dec 20, 2016		Mansi Goel		Changes for Gift Card FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.giftcard.issue;

import java.math.BigDecimal;

import max.retail.stores.pos.ui.beans.MAXGiftCardBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;

public class MAXCheckAccessForGCAmountIssuedAisle extends PosLaneActionAdapter {

	private static final long serialVersionUID = -2597520381726951093L;

	public void traverse(BusIfc bus) {

		GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
		cargo.setItemQuantity(BigDecimalConstants.ZERO_AMOUNT);
		// Changes ends for code merging
		boolean over = false;
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		GiftCardIfc giftCard = cargo.getGiftCard();
		String letterName = bus.getCurrentLetter().getName();
		String letter = null;
		// get amount from user's input/selection
		CurrencyIfc amount = null;
		CurrencyIfc ovAmt = DomainGateway.getBaseCurrencyInstance();
		try {
			ovAmt.setStringValue(pm.getStringValue("MaximumGiftCardIssueOverridableAmount"));
		} catch (ParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (cargo.getTransaction() != null && cargo.getTransaction().isTrainingMode()) { 
			amount = GiftCardUtilities.getCurrency("10.0");
		} else if (letterName.equals(CommonLetterIfc.NEXT)) {
			// get the amount from input text
			String amountString = LocaleUtilities.parseNumber(ui.getInput(), LocaleConstantsIfc.USER_INTERFACE).toString();
			amount = GiftCardUtilities.getCurrency(amountString);
		} else {
			// get the amount from a amount button click
			amount = GiftCardUtilities.getButtonDenomination(pm, letterName, logger, bus.getServiceName());
		}
		// check amount
		if (GiftCardUtilities.isMoreThanMax(pm, amount, logger, bus.getServiceName())) {
			DialogBeanModel dModel = GiftCardUtilities.getMoreThanMaxDialogModel(utility);
			if (cargo.getTransaction().getTransactionType() == TransactionIfc.TYPE_RETURN) {
				dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.UNDO);
			}
			over = true;
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dModel);
		} else if (GiftCardUtilities.isLessThanMin(pm, amount, logger, bus.getServiceName())) {
			DialogBeanModel dModel = GiftCardUtilities.getLessThanMinDialogModel(utility);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dModel);
			return;
		} else {
			if (giftCard == null) {
				giftCard = DomainGateway.getFactory().getGiftCardInstance();
				giftCard.setCurrentBalance(amount);
				giftCard.setInitialBalance(amount);
				cargo.setGiftCard(giftCard);
			}
			giftCard.setCurrentBalance(amount);
			giftCard.setInitialBalance(amount);
			GiftCardBeanModel model = null; 
			// MAXGiftCardBeanModel model = null;
			if (ui.getModel() instanceof MAXGiftCardBeanModel) {
				model = (MAXGiftCardBeanModel) ui.getModel();
			} else {
				model = new MAXGiftCardBeanModel();
			}
			model.setGiftCardAmount(new BigDecimal(amount.toString()));
			ui.setModel(POSUIManagerIfc.SELL_GIFT_CARD, model);
			letter = "Continue";
		}

		if (!over)
			if (amount.compareTo(ovAmt) <= 0) {
				letter = "Overrideden";
			} else {
				cargo.setAccessFunctionID(705);
				letter = "Override";
			}

		if (letter != null) {
			bus.mail(new Letter(letter), BusIfc.CURRENT);
		}
	}

}
