/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*   
*   Rev 1.2		27 Oct 2017			Jyoti Yadav			Changes for Innoviti Integration CR
*   Rev 1.1  25/June/2013 Jyoti Rawal,Fix for bug 6635:Application crash when entering Credit Card no 
*   of 3 digits while doing tendering with Credit Card.
*  Rev 1.0  24/May/2013	Jyoti Rawal, Initial Draft: Changes for Credit Card Functionality 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.math.BigDecimal;
import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.pos.ado.journal.MAXJournalFactory;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.AbstractCardTender;
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

public class MAXCreditTenderActionSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6252205951960366400L;

	public void arrive(BusIfc bus) {
		// attempt to create a Credit tender from the tender attributes
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		String crdNum = "";
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
		.getManager(UIManagerIfc.TYPE);
		HashMap tenderAttributes = cargo.getTenderAttributes();
		if(tenderAttributes.get("NUMBER")!=null)
		 crdNum = tenderAttributes.get("NUMBER").toString();
		StringBuffer buf = new StringBuffer(crdNum);
		// Rev 1.1 changes start
		if (buf.length() < 12) {
			DialogBeanModel model = new DialogBeanModel();
			model.setType(DialogScreensIfc.ERROR);
			String msg[] = new String[2];
			model.setResourceID("CreditCardInvalid");
			msg[0] = "Card number is less than minimum length";
			msg[1] = "Please enter valid card";
			model.setArgs(msg);
			model.setButtonLetter(DialogScreensIfc.BUTTON_OK,
					"CardInvalid");
			ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
			return;
		}
				// Rev 1.1 changes end
		if (buf.length() > 12) {
			String num = crdNum.substring(6, buf.length() - 4);

			if (num.equalsIgnoreCase("******")) {
				tenderAttributes.put("NUMBER", crdNum);
			} else {

				String maskCrd = buf.replace(6, buf.length() - 4, "******")
						.toString();
				tenderAttributes.put("NUMBER", maskCrd);

			}
		}

		TenderADOIfc creditTender = null;
		/*Change for Rev 1.2: Start*/
		TenderADOIfc creditCashBackTender = null;
		/*Change for Rev 1.2: End*/
		try {
			TenderFactoryIfc factory = (TenderFactoryIfc) ADOFactoryComplex
					.getFactory("factory.tender");
			creditTender = factory.createTender(tenderAttributes);
			
			/*Change for Rev 1.2: Start*/
			if(cargo instanceof MAXTenderCargo)
			{
				HashMap responseMap = ((MAXTenderCargo)cargo).getResponseMap();
				//Object object = responseMap.get("StateDiscount");
				Object object = responseMap.get("StateTotalAmount");
			
				if(object!=null && object instanceof String)
				{
					String cashbackAmount = (String) object;
					if(!(cashbackAmount.trim().equals("") || cashbackAmount.trim().equals("00"))){  // Added for CSHBK 0 Amount issue - Karni
					
					String actualAmt = tenderAttributes.get(TenderConstants.AMOUNT).toString();
							
					CurrencyIfc currencyAmt = DomainGateway.getBaseCurrencyInstance(actualAmt);
					CurrencyIfc currencyCashBackAmt = DomainGateway.getBaseCurrencyInstance(new BigDecimal(cashbackAmount).divide(new BigDecimal(100), 2, 2).toString());
					
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
					tenderCharge.setBankName("CSHBK");
					tenderCharge.setAmountTender(currencyCashBackAmt);				
					tenderCharge.setResponseDate(responseMap);
					
					cargo.getCurrentTransactionADO().addTender(creditCashBackTender);
				}
				}
			}
			
			/*else if (creditTender instanceof AbstractCardTender) {

				TenderChargeIfc tender = (TenderChargeIfc) ((TenderCreditADO) creditTender)
						.toLegacy();
				String date = tender.getExpirationDateString();
//				tenderAttributes.put("AUTH_METHOD",
//						AuthorizableTenderIfc.AUTHORIZATION_NETWORK_OFFLINE);
				// START Added If condition for not going inside in case of
				if (!((String) creditTender.getTenderAttributes().get(
						TenderConstants.AUTH_METHOD))
						.equalsIgnoreCase("ONLINE")) {
					// END START Added If condition for not going inside in case
					// of Online

					if (date != null
							&& (date.length() >= 4 || date.length() >= 5)) {
						EYSDate expireDate = null;
						try {
							expireDate = new EYSDate(new SimpleDateFormat(
									"MM/yy").parse(date));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						EYSDate today = new EYSDate();
						if (expireDate.before(today)
								&& !isCurrentMonth(expireDate)) {
							ui = (POSUIManagerIfc) bus
									.getManager(UIManagerIfc.TYPE);
							UtilityManagerIfc utility = (UtilityManagerIfc) bus
									.getManager(UtilityManagerIfc.TYPE);
							showExpiredCardDialogForOfflineMode(utility, ui);
							return;
						}
					}
				}
			}*/
			/*Change for Rev 1.2: End*/
		} catch (ADOException adoe) {
			logger.warn("Could not get TenderFactory: " + adoe.getMessage());
		} catch (TenderException e) {
			handleException(e, bus);
			return;
		}

		try {
			if (creditTender instanceof TenderCreditADO) {
				creditTender = setTransactionReentry(
						(TenderCreditADO) creditTender, cargo);
			}
			// attempt to add the tender to the transaction

			cargo.getCurrentTransactionADO().addTender(creditTender);
			cargo.setLineDisplayTender(creditTender);

			// journal the added tender
			JournalFactoryIfc jrnlFact = null;
			try {
				jrnlFact = MAXJournalFactory.getInstance();
			} catch (ADOException e) {
				logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
				throw new RuntimeException(
						JournalFactoryIfc.INSTANTIATION_ERROR, e);
			}
			RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
			/*Change for Rev 1.2: Start*/
			if (creditCashBackTender != null)
			{
				registerJournal.journal(creditCashBackTender, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);
			}
			/*Change for Rev 1.2: End*/
			registerJournal.journal(creditTender, JournalFamilyEnum.TENDER,
					JournalActionEnum.ADD);

			// mail a letter
			/*Change for Rev 1.2: Start*/
			if (("WithOutExpValOCC".equals(bus.getCurrentLetter().getName())))
				bus.mail(new Letter("WithOutExpValOCC"), BusIfc.CURRENT);
			else
				/*Change for Rev 1.2: End*/
			bus.mail(new Letter("Success"), BusIfc.CURRENT);
		} catch (TenderException e) {
			handleException(e, bus);
			return;
		}
	}

	private TenderADOIfc setTransactionReentry(TenderCreditADO creditTender,
			TenderCargo cargo) {
		creditTender.setTransactionReentryMode(cargo.getRegister()
				.getWorkstation().isTransReentryMode());
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

		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		UtilityManagerIfc utility = (UtilityManagerIfc) bus
				.getManager(UtilityManagerIfc.TYPE);

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
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO,
				CommonLetterIfc.INVALID);
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
	protected void showInvalidNumberDialog(UtilityManagerIfc utility,
			POSUIManagerIfc ui) {
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
	protected void showExpiredCardDialog(UtilityManagerIfc utility,
			POSUIManagerIfc ui) {
		// set screen args
		String titleTag = "ExpiredCreditCardTitle";
		String cardString = utility.retrieveDialogText(
				"ExpiredCardError.Credit", "credit");

		// Display error message
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("ExpiredCardError");
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
				CommonLetterIfc.INVALID);
		dialogModel.setTitleTag(titleTag);
		String args[] = new String[2];
		args[0] = cardString;
		args[1] = cardString;
		dialogModel.setArgs(args);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	// --------------------------------------------------------------------------
	/**
	 * Shows the expired card dialog screen.
	 */
	// --------------------------------------------------------------------------
	protected void showExpiredCardDialogForOfflineMode(
			UtilityManagerIfc utility, POSUIManagerIfc ui) {
		// set screen args
		String titleTag = "ExpiredCreditCardTitle";
		String cardString = utility.retrieveDialogText(
				"ExpiredCardError.Credit", "credit");

		// Display error message
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("ExpiredCardError");
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
				CommonLetterIfc.FAILURE);
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
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
				CommonLetterIfc.INVALID);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	protected boolean isCurrentMonth(EYSDate expdate) {

		boolean curr_month = false;
		EYSDate today = new EYSDate();

		if (expdate.getYear() == today.getYear()) {

			if (expdate.getMonth() == today.getMonth()) {

				curr_month = true;
			}

		}

		return curr_month;
	}
}
