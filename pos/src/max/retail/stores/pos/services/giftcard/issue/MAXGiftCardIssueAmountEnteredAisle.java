/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
   	Rev 1.1  21/Jan/2016	Bhanu Priya Gupta,     Fixed Bug [16632]
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
 * This aisle is to get use's input/selection for gift card issue amount
 * 
 * @version $Revision: 1.3 $
 **/
// --------------------------------------------------------------------------
public class MAXGiftCardIssueAmountEnteredAisle extends PosLaneActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4202639040202631852L;
	/** revision number from PVCS */
	public static final String revisionNumber = "$Revision: 1.3 $";

	// ----------------------------------------------------------------------
	/**
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void traverse(BusIfc bus) {
		GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
		// changes starts for code merging(commenting below line)
		//cargo.setItemQuantity(BigDecimalConstants.ZERO);
		cargo.setItemQuantity(BigDecimalConstants.ZERO_AMOUNT);
		// changes ends for code merging
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		GiftCardIfc giftCard = cargo.getGiftCard();
		String letterName = bus.getCurrentLetter().getName();
		LetterIfc letter = null;
		String letter2 = null;
		String amountString = null;
		// get amount from user's input/selection
		CurrencyIfc amount = null;
		// Rev 1.1  Changes Starts
		boolean over=false;
		CurrencyIfc ovAmt = DomainGateway.getBaseCurrencyInstance();
		try {
			ovAmt.setStringValue(pm.getStringValue("MaximumGiftCardIssueOverridableAmount"));
		} catch (ParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Rev 1.1  Changes ends
		if (cargo.getTransaction() != null && cargo.getTransaction().isTrainingMode()) { // if
																							// (cargo.getTransaction().isTrainingMode())
																							// {
			amount = GiftCardUtilities.getCurrency("100.0");
		} else if (letterName.equals(CommonLetterIfc.NEXT) || (letterName.equals("Overrideden"))) {
			// get the amount from input text
			try{
			 amountString = LocaleUtilities.parseNumber(ui.getInput(), LocaleConstantsIfc.USER_INTERFACE).toString();
			}catch(Exception e)
			{
			amountString = "0.0";	
			}
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
			return;
		} 
		else if (GiftCardUtilities.isLessThanMin(pm, amount, logger, bus.getServiceName())) {
			DialogBeanModel dModel = GiftCardUtilities.getLessThanMinDialogModel(utility);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dModel);
			return;
		} 
		else {
			if (giftCard == null) {
				giftCard = DomainGateway.getFactory().getGiftCardInstance();
				giftCard.setCurrentBalance(amount);
				giftCard.setInitialBalance(amount);
				cargo.setGiftCard(giftCard);
			}
			giftCard.setCurrentBalance(amount);
			giftCard.setInitialBalance(amount);
			MAXGiftCardBeanModel model = null; 
			// MAXGiftCardBeanModel model = null;
			if (ui.getModel() instanceof MAXGiftCardBeanModel) {
				model = (MAXGiftCardBeanModel) ui.getModel();
			} else {
				model = new MAXGiftCardBeanModel();
			}
			model.setGiftCardAmount(new BigDecimal(amount.toString()));
			// changes starts for code merging(commenting below line as per lispl)
			//model.setGiftCardStatus(MAXTenderAuthConstantsIfc.ACTIVE);
			// Changes ends for code merging
			ui.setModel(POSUIManagerIfc.SELL_GIFT_CARD, model);
			letter = new Letter(CommonLetterIfc.CONTINUE);
			//letter="Continue";
		}
		/// Rev 1.1  Changes Starts
		if (!over){
			if (amount.compareTo(ovAmt) <= 0) {
				//letter = "Overrideden";
				letter = new Letter(CommonLetterIfc.CONTINUE);
			}
			else {
				cargo.setAccessFunctionID(705); //Rev 1.1 changes
			
				letter = new Letter(CommonLetterIfc.OVERRIDE);
			}
			} 
		
		
		/// Rev 1.1  Changes Ends
		
		if (letter != null) {
			bus.mail(letter, BusIfc.CURRENT);
		}
	}
}
