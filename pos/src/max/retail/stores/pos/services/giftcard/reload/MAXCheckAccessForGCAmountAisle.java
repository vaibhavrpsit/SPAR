/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
   Rev 1.1  18/Jul/2013	Jyoti Rawal, Bug 7195 - GC- POS Crashed while reloading with 5500
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.giftcard.reload;

// Foundation imports
import java.math.BigDecimal;

import max.retail.stores.pos.ui.beans.MAXGiftCardBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
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
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;

//--------------------------------------------------------------------------
/**
 * This site checks to see if the current operator has access to the specified
 * function.
 * 
 * @version $Revision: 1.1 $
 **/
// --------------------------------------------------------------------------
public class MAXCheckAccessForGCAmountAisle extends PosLaneActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6382206059434237404L;
	/**
	 * revision number
	 **/
	public static final String revisionNumber = "$Revision: 1.1 $";

	// protected static Logger logger =
	// Logger.getLogger(com.extendyourstore.pos.services.PosLaneActionAdapter.class);

	// ----------------------------------------------------------------------
	/**
	 * Check access and mail appropriate letter.
	 * 
	 * @param bus
	 *            the bus arriving at this site
	 **/
	// ----------------------------------------------------------------------
	public void traverse(BusIfc bus) {
		GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		String letterName = bus.getCurrentLetter().getName();
		String letter = null;
		boolean over = false;

		// create the transaction if it doesn't exist.
		if (cargo.getTransaction() == null) {
			cargo.initializeTransaction(bus);
			cargo.getTransaction().setSalesAssociate(cargo.getTransaction().getCashier());
		}

		// get amount from user's input/selection
		CurrencyIfc amount;
		CurrencyIfc ovAmt = DomainGateway.getBaseCurrencyInstance();
		try {
			ovAmt.setStringValue(pm.getStringValue("MaximumGiftCardIssueOverridableAmount"));
		} catch (ParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GiftCardUtilities.getCurrency("5000.0");
		if (cargo.getTransaction().isTrainingMode()) {
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
		} else {
			GiftCardIfc giftCard = DomainGateway.getFactory().getGiftCardInstance();
			giftCard.setCurrentBalance(amount);
			giftCard.setRequestType(GiftCardIfc.GIFT_CARD_RELOAD);

			cargo.setGiftCard(giftCard);
			MAXGiftCardBeanModel model = new MAXGiftCardBeanModel();
			if (ui.getModel() instanceof GiftCardBeanModel) {
				model = (MAXGiftCardBeanModel) ui.getModel();
			}
			model.setGiftCardAmount(new BigDecimal(amount.getStringValue()));
			ui.setModel(POSUIManagerIfc.SELL_GIFT_CARD, model);
			// letter = new Letter(CommonLetterIfc.CONTINUE);
		}

		if (!over)
			if (amount.compareTo(ovAmt) <= 0) {
				letter = "Overrideden";
			} else {
				cargo.setAccessFunctionID(705); //Rev 1.1 changes
				letter = "Override";
			}

		if (letter != null) {
			bus.mail(new Letter(letter), BusIfc.CURRENT);
		}
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns the revision number of the class.
	 * <P>
	 * 
	 * @return String representation of revision number
	 **/
	// ----------------------------------------------------------------------
	public String getRevisionNumber() {
		return (Util.parseRevisionNumber(revisionNumber));
	}
}
