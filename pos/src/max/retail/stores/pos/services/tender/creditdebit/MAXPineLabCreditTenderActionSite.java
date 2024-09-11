/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *
 * Rev 1.1  Dec 08, 2014    Shavinki Goyal 		Resolution for LSIPL-FES:-Multiple Tender using Innoviti 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.HashMap;
import java.util.Locale;

import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.pos.ado.journal.MAXJournalFactory;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderCreditADO;
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
 * Attempts to add a credit to the transaction
 */
public class MAXPineLabCreditTenderActionSite extends PosSiteActionAdapter {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus) {
		// attempt to create a Credit tender from the tender attributes
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		HashMap tenderAttributes = cargo.getTenderAttributes();
		
		// MAX Change for Rev 1.7: Start
		TenderADOIfc creditTender = null;
		TenderADOIfc creditCashBackTender = null;
		try 
		{
			TenderFactoryIfc factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
			creditTender = factory.createTender(tenderAttributes);
			
			//Modified by Deepshikha, Credit/Loyalty Implementation..starts 
			if(cargo instanceof MAXTenderCargo)
			{
				HashMap responseMap = ((MAXTenderCargo)cargo).getResponseMap();
				Object object = responseMap.get("StateDiscount");				
			
				if(object!=null && object instanceof String)
				{
					String cashbackAmount = (String) object;
					
					String actualAmt = tenderAttributes.get(TenderConstants.AMOUNT).toString();
							
					CurrencyIfc currencyAmt = DomainGateway.getBaseCurrencyInstance(actualAmt);
					CurrencyIfc currencyCashBackAmt = DomainGateway.getBaseCurrencyInstance(cashbackAmount);
					CurrencyIfc amtWithoutCashBAck = currencyAmt.subtract(currencyCashBackAmt);
					
					tenderAttributes.put(TenderConstants.AMOUNT, amtWithoutCashBAck.toString());
					creditTender.setTenderAttributes(tenderAttributes);
					cargo.setTenderAttributes(tenderAttributes);					

					creditCashBackTender = factory.createTender(tenderAttributes);
					if (creditCashBackTender instanceof TenderCreditADO) 
					{
						creditCashBackTender = setTransactionReentry((TenderCreditADO) creditCashBackTender, cargo);

					}
					// attempt to add the tender to the transaction
					
					MAXTenderChargeIfc tenderCharge = (MAXTenderChargeIfc) creditCashBackTender.toLegacy();
					//tenderCharge.setCardType("Visa");

					//creditCashBackTender.setTenderAttributes(tenderAttributes);
					tenderCharge.setCardType("CSHBK");
					tenderCharge.setAmountTender(currencyCashBackAmt);				
					tenderCharge.setResponseDate(responseMap);
					
					cargo.getCurrentTransactionADO().addTender(creditCashBackTender);
					
				}
			}
			//Modified by Deepshikha, Credit/Loyalty Implementation ..ends

		} catch (ADOException adoe) {
			logger.warn("Could not get TenderFactory: " + adoe.getMessage());
		} catch (TenderException e) {
			handleException(e, bus);
			return;
		}

		try
		{
			if (creditTender instanceof TenderCreditADO) {
				creditTender = setTransactionReentry((TenderCreditADO) creditTender, cargo);

			}
			// attempt to add the tender to the transaction
			cargo.getCurrentTransactionADO().addTender(creditTender);
			cargo.setLineDisplayTender(creditTender);

			// journal the added tender
			JournalFactoryIfc jrnlFact = null;
			try 
			{
				jrnlFact = MAXJournalFactory.getInstance();
			} catch (ADOException e) {
				logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
				throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
			}
			RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
			if (creditCashBackTender != null)
			{
				registerJournal.journal(creditCashBackTender, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);
			}
			// MAX Change for Rev 1.7: End
			registerJournal.journal(creditTender, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);

			// mail a letter
			if (("WithOutExpValOCC".equals(bus.getCurrentLetter().getName())))
				bus.mail(new Letter("WithOutExpValOCC"), BusIfc.CURRENT);
			else
				bus.mail(new Letter("Success"), BusIfc.CURRENT);
		} catch (TenderException e) {
			handleException(e, bus);
			return;
		}
	}

	private TenderADOIfc setTransactionReentry(TenderCreditADO creditTender, TenderCargo cargo) {
		creditTender.setTransactionReentryMode(cargo.getRegister().getWorkstation().isTransReentryMode());
		return creditTender;
	}

	/**
	 * Displays the proper dialog depending on the problem
	 * 
	 * @param e
	 *            The thrown exception
	 * @param bus
	 *            The current bus
	 */
	protected void handleException(TenderException e, BusIfc bus) {
		TenderErrorCodeEnum error = e.getErrorCode();

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

		// clear the tender attributes
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		cargo.getTenderAttributes().remove(TenderConstants.MSR_MODEL);
		cargo.getTenderAttributes().remove(TenderConstants.NUMBER);
		cargo.getTenderAttributes().remove(TenderConstants.EXPIRATION_DATE);
		cargo.setPreTenderMSRModel(null);

		if (error == TenderErrorCodeEnum.UNKNOWN_CARD_TYPE) {
			showUnknownCardDialog(ui);
		} else if (error == TenderErrorCodeEnum.INVALID_CARD_TYPE) {
			showCardTypeNotAcceptedDialog(ui);
		} else if (error == TenderErrorCodeEnum.INVALID_CARD_NUMBER) {
			showInvalidNumberDialog(utility, ui);
		} else if (error == TenderErrorCodeEnum.EXPIRED) {
			showExpiredCardDialog(utility, ui);
		} else if (error == TenderErrorCodeEnum.BAD_MAG_SWIPE) {
			showBadMagStripeDialog(ui);
		} else if (error == TenderErrorCodeEnum.INVALID_TENDER_TYPE) {
			showInvalidTenderTypeDialog(ui);
		}

	}

	// --------------------------------------------------------------------------
	/**
	 * Shows the unknown card dialog screen.
	 */
	// --------------------------------------------------------------------------
	protected void showUnknownCardDialog(POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("UnknownCreditCard");
		dialogModel.setType(DialogScreensIfc.CONFIRMATION);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Loop");
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.INVALID);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	// --------------------------------------------------------------------------
	/**
	 * Shows the card type not accepted dialog screen.
	 */
	// --------------------------------------------------------------------------
	protected void showCardTypeNotAcceptedDialog(POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("CardTypeNotAccepted");
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
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
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");

		String[] args = new String[2];
		args[1] = utility.retrieveDialogText("CreditCard", "Credit Card");
		args[1] += " " + utility.retrieveDialogText("Number", "number");
		Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
		args[0] = args[1].toLowerCase(locale);
		dialogModel.setArgs(args);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	// --------------------------------------------------------------------------
	/**
	 * Shows the expired card dialog screen.
	 */
	// --------------------------------------------------------------------------
	protected void showExpiredCardDialog(UtilityManagerIfc utility, POSUIManagerIfc ui) {
		// set screen args
		String titleTag = "ExpiredCreditCardTitle";
		String cardString = utility.retrieveDialogText("ExpiredCardError.Credit", "credit");

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

	/**
	 * Display dialog indicating bad mag swipe
	 * 
	 * @param utility
	 * @param ui
	 */
	protected void showBadMagStripeDialog(POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();

		// set model properties
		dialogModel.setResourceID("BadCreditMSRReadError");
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	// --------------------------------------------------------------------------
	/**
	 * Shows the card type not accepted dialog screen.
	 */
	// --------------------------------------------------------------------------
	protected void showInvalidTenderTypeDialog(POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("NoHouseAccountPaymentWithHouseAccount");
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.INVALID);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
}
