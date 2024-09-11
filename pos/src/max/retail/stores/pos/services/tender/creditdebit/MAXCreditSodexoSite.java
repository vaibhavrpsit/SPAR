/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 *	Rev 1.0			03 Nov 2017			Jyoti Yadav				Changes for Innoviti Integration CR
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.pos.ado.journal.MAXJournalFactory;
import max.retail.stores.pos.ado.tender.MAXTenderConstants;
import max.retail.stores.pos.services.edc.CallingOnlineDebitCardTender;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
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
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXCreditSodexoSite extends PosSiteActionAdapter {

	public void arrive(BusIfc bus) {
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		String transactionTime = "2012-07-21T13:55:58.0Z";
		BigDecimal amount = new BigDecimal(cargo.getTenderAttributes().get("AMOUNT").toString());
		String amountString = amount.multiply(new BigDecimal("100.00")).intValue() + "";
		//String amountString = BigDecimalConstants.ONE_AMOUNT.multiply(new BigDecimal("100.00")).intValue() + "";
		String invoiceNumber = "123"; // no need in sale
		HashMap responseMap = null;
		BigDecimal foodTotals = new BigDecimal("0.00");
		BigDecimal tenderAmt = new BigDecimal("0.00");

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel beanModel = new POSBaseBeanModel();
		ui.showScreen(MAXPOSUIManagerIfc.EDC_POST_VOID_SCREEN, beanModel);

		HashMap tenderAttributes = cargo.getTenderAttributes();
		if (tenderAttributes.get("foodTotals") != null) {
			foodTotals = (BigDecimal) tenderAttributes.get("foodTotals");
		}
		tenderAmt = new BigDecimal(tenderAttributes.get("AMOUNT").toString().replace(",", ""));
		if (tenderAmt.compareTo(foodTotals) > 0) {
			showSodexoFoodTotalAmountErrorDialog(bus,"Invalid");
		}
		else{
			CallingOnlineDebitCardTender edcObj = new CallingOnlineDebitCardTender();
			try {
				responseMap = edcObj.makePostVoidEDC(cargo.getCurrentTransactionADO().getTransactionID(), amountString,
						invoiceNumber, transactionTime, "00", "90");
				
				System.out.println(responseMap.toString());
				
				if (responseMap != null) {
					String hostResponseCode = (String) responseMap.get("HostResponseCode");
					if (hostResponseCode.equals("00")) {
						HashMap bankNameStrings = getBankNameStrings(bus);
						String acqName = responseMap.get("StateAquirerName").toString();
						String aquirerStatus=null;
						String authcode=responseMap.get("HostResponseApprovalCode").toString();  //mohan
					//	String authcode=responseMap.get("ApprovalCode").toString();
						
						System.out.println(authcode);
						
						//Added by Vaibhav --Null pointer check-Start
						if(responseMap.get("SelectedAquirerStatus")!=null){
						     aquirerStatus=responseMap.get("SelectedAquirerStatus").toString();
						}else{
							   aquirerStatus="       ";
						}//end
						String bankCode = getBankCodeFromCardType(bankNameStrings, acqName);
						if(bankCode == null){
							bankCode="3210";
						}

						// Changes for "Sodexo" to S
						//String reqBankName = "S-" + acqName;
						String reqBankName = acqName;

						if (reqBankName.length() > 20) {
							reqBankName = reqBankName.substring(0, 20);
						}
						/*tenderAttributes.put(MAXTenderConstants.AUTH_CODE,
								responseMap.get("HostResponseApprovalCode").toString());*/
						
						tenderAttributes.put(MAXTenderConstants.AUTH_CODE, authcode);
						tenderAttributes.put(MAXTenderConstants.BANK_CODE, bankCode);
						tenderAttributes.put(MAXTenderConstants.BANK_NAME, reqBankName);
						tenderAttributes.put(MAXTenderConstants.CARD_TYPE, reqBankName);
						tenderAttributes.put("AMOUNT", tenderAttributes.get(TenderConstants.AMOUNT).toString());
						String dateString = "12/2024";
						tenderAttributes.put("NUMBER", responseMap.get("CardNumber").toString());
						tenderAttributes.put("EXPIRATION_DATE", dateString);

						TenderADOIfc creditTender = null;
						try {
							TenderFactoryIfc factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
							creditTender = factory.createTender(tenderAttributes);

							// attempt to add the tender to the transaction
							cargo.getCurrentTransactionADO().addTender(creditTender);
							cargo.setLineDisplayTender(creditTender);
							MAXTenderChargeIfc tenderCharge = (MAXTenderChargeIfc) creditTender.toLegacy();
							tenderCharge.setCardType(reqBankName);
							tenderCharge.setBankName(acqName);
							tenderCharge.setAquirerStatus(aquirerStatus);
							tenderCharge.setResponseDate(responseMap);
							tenderCharge.setAuthCode(authcode);
							creditTender.setTenderAttributes(tenderAttributes);
							System.out.println("tenderAttributes");
							System.out.println(tenderAttributes);
							
							
							/*Change for Rev 1.2: Start*/
							JournalFactoryIfc jrnlFact = null;
							try {
								jrnlFact = MAXJournalFactory.getInstance();
							} catch (ADOException e) {
								logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
								throw new RuntimeException(
										JournalFactoryIfc.INSTANTIATION_ERROR, e);
							}
							RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
							registerJournal.journal(creditTender, JournalFamilyEnum.TENDER,
									JournalActionEnum.ADD);
						
							/*Change for Rev 1.2: End*/
							
							bus.mail(new Letter("Next"), BusIfc.CURRENT);
						} catch (ADOException adoe) {
							logger.warn("Could not get TenderFactory: " + adoe.getMessage());
						} catch (TenderException e) {
							handleException(e, bus);
							return;
						}
					} else {
						showDialogBoxMethod(responseMap, bus, "OnlineCredit");
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


	}

	public void showDialogBoxMethod(Map responseMap, BusIfc bus, String buttonLetter) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		String msg[] = new String[6];
		dialogModel.setResourceID("RESPONSE_DETAILS");
		msg[0] = "<<--||--:: Please Find The Response Details As Below ::--||-->>";
		msg[1] = "Your Credit/Debit Card has been Swiped";
		msg[2] = " Response Code Returned Is ";
		if (responseMap != null) {
			if (responseMap.get("HostResponseMessage") != null)
				msg[3] = responseMap.get("HostResponseMessage").toString();
		}
		msg[4] = "Press ENTER To Proceed / Using another Tender";
		msg[5] = "::Thanks::";
		dialogModel.setArgs(msg);
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, buttonLetter);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	public void showSodexoFoodTotalAmountErrorDialog(BusIfc bus,String buttonLetter) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("SODEXO_FOOD_TOTALS_AMOUNT_NOTICE");
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, buttonLetter);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	public HashMap getBankNameStrings(BusIfc bus) {
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		// CodeListIfc list =
		// utility.getCodeListMap().get(LSIPLCodeConstantsIfc.CODE_LIST_CREDIT_DEBIT_BANK_CODES);
		Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
		String storeID = Gateway.getProperty("application", "StoreID", "");

		CodeListIfc list = utility.getReasonCodes(storeID, MAXCodeConstantsIfc.CODE_LIST_CREDIT_DEBIT_BANK_CODES);

		if (list != null) {
			Vector reason = list.getTextEntries(locale);
			Vector codes = list.getKeyEntries();
			HashMap combinedcodes = new HashMap();
			for (int i = 0; i < codes.size(); i++) {

				combinedcodes.put(codes.get(i).toString(), reason.get(i).toString());
			}

			return (combinedcodes);
		}
		return null;
	}

	public String getBankCodeFromCardType(HashMap responseMap, String cardType) {
		String bankCode = null;
		String value = null;
		Object key = null;

		Iterator it = responseMap.keySet().iterator();
		while (it.hasNext()) {
			key = it.next();
			value = (String) responseMap.get(key);

			value = value.replaceAll("\\s+", "");
			cardType = cardType.replaceAll("\\s+", "");
			if (cardType.equalsIgnoreCase(value))
				break;
		}

		return (String) key;
	}

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
		String titleTag = "ExpiredCreditCardTitle";
		String cardString = utility.retrieveDialogText("ExpiredCardError.Credit", "credit");
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
