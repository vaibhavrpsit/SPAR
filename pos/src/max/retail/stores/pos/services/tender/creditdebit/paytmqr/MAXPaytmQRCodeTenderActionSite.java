/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *      Copyright (c) 2022-2023 MAXHyperMarket, Inc.    All Rights Reserved.    
 * Rev 1.0 		March 28, 2022    Kamlesh Pant   Paytm QR Integration
 * Initial revision.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit.paytmqr;

import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.pos.ado.journal.MAXJournalFactory;
import max.retail.stores.pos.ado.tender.MAXTenderConstants;
import max.retail.stores.pos.ado.tender.MAXTenderCreditADO;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.services.tender.MAXTenderUtils;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderCreditADO;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXPaytmQRCodeTenderActionSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = 2551468313424128355L;

	public void arrive(BusIfc bus)
	{
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		HashMap<String, Object> tenderAttributes = cargo.getTenderAttributes();
		
		
		//Rev 1.2 start	
		tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.CREDIT);
		//Rev 1.2 end
		tenderAttributes.put(TenderConstants.AMOUNT, cargo.getPaytmQRCodeResp().getAmountPaid());
		
		TenderADOIfc creditTender = null;
		TenderADOIfc creditCashBackTender = null;
		try 
		{
			TenderFactoryIfc factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
			creditTender = factory.createTender(tenderAttributes);
		}
		 catch (ADOException adoe) {
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
			/*
			 * RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal(); if
			 * (creditCashBackTender != null) {
			 * registerJournal.journal(creditCashBackTender, JournalFamilyEnum.TENDER,
			 * JournalActionEnum.ADD); } // MAX Change for Rev 1.7: End
			 * System.out.println("107 :"+registerJournal.toString());
			 * registerJournal.journal(creditTender, JournalFamilyEnum.TENDER,
			 * JournalActionEnum.ADD); System.out.println("109 :"+registerJournal);
			 */
			
		} catch (TenderException e) {
			handleException(e, bus);
			return;
		}
		
		if (bus.getCurrentLetter().getName().equalsIgnoreCase("CheckStatusSuccess") || bus.getCurrentLetter().getName().equalsIgnoreCase("AddTender")) 
		{
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			TenderADOIfc tender = cargo.getLineDisplayTender();

			HashMap respMap = cargo.getResponseMap();
			
			String bankCode = null;
			String cardType = "PAYTM_QR";
			String issuerName = null;
			String issuerBankCode = null;
	
			HashMap bankNameStrings = getBankNameStrings(bus);
			bankCode = MAXTenderUtils.getBankCodeFromCardType(bankNameStrings, cardType);
			//bankCode = cardType;
			

			
			MAXTenderChargeIfc tenderCharge = null;
			if (tender instanceof MAXTenderCreditADO) {
				tenderCharge = (MAXTenderChargeIfc) tender.toLegacy();
				tenderCharge.setBankCode(bankCode);
				tenderCharge.setResponseDate(respMap);
				tenderCharge.setCardType(cardType);
				tenderCharge.setMerchantTransactionId(cargo.getPaytmQRCodeResp().getTxnId());
				tenderCharge.setOrderNumber(cargo.getPaytmQRCodeResp().getOrderId());
			//	tenderCharge.setPhoneNumber(cargo.getPaytmResp().getPhoneNumber());
								
				//Rev 1.1 Starts
				//tenderCharge.setAuthCode(cargo.getOtp());
				tenderCharge.setAuthCode(cargo.getPaytmQRCodeResp().getBankTxnId());
				tenderCharge.setRrnNumber(cargo.getPaytmQRCodeResp().getMerchantUniqueReference());
				//Rev 1.1 Ends
				if(cargo.getPaytmQRCodeResp().getPaymentMode() != null && cargo.getPaytmQRCodeResp().getPaymentMode().equalsIgnoreCase("PPI") && cargo.getPaytmQRCodeResp().getBankName().equalsIgnoreCase("WALLET")) {
					tenderCharge.setPaytmUPIorWalletPaytment("WALLET");
				}else {
					tenderCharge.setPaytmUPIorWalletPaytment("UPI");
				}
				
			} 
			

			// cargo.getTenderADO().
			tenderAttributes.put(MAXTenderConstants.BANK_CODE, bankCode);
			tenderAttributes.put(MAXTenderConstants.BANK_NAME, cardType);
			// update tender with new attributes
			try
			{
				tender.setTenderAttributes(tenderAttributes);
			} catch (TenderException e) {
				logger.error("TenderException:  This should not happen.");
			}

			TenderADOIfc[] tenderVector = cargo.getCurrentTransactionADO().getTenderLineItems(TenderLineItemCategoryEnum.ALL);
			if (tenderVector != null && tenderVector.length > 0)
			{
				for (int i=0; i < tenderVector.length; i++)
				{
					TenderADOIfc tenderObject = tenderVector[i];
					if (tenderObject instanceof MAXTenderCreditADO)
					{
							MAXTenderChargeIfc tenderChargeObj = (MAXTenderChargeIfc) tenderObject.toLegacy();
							// MAX Change for Rev 1.2: Start
							if (tenderChargeObj.getCardType().startsWith("CSHBK") &&
								tenderChargeObj.getResponseDate().equals(tenderCharge.getResponseDate()))
							{
								tenderChargeObj.setAuthCode(tenderCharge.getAuthCode());								
								tenderChargeObj.setEmiTransaction(tenderCharge.isEmiTransaction());
								
								if (issuerName != null)
								{
							      String reqBankName = "C";// + SEPARATOR + issuerName;
									
									if (reqBankName.length() > 20)
									{
										reqBankName = reqBankName.substring(0, 20);
									}
									tenderChargeObj.setCardType(reqBankName);
									tenderChargeObj.setBankCode(issuerBankCode);
								}
								else
								{
									tenderChargeObj.setCardType(tenderCharge.getCardType());
									tenderChargeObj.setBankCode(tenderCharge.getBankCode());
								}
								break;
							}
					 }
				 }
						
			}

			JournalManagerIfc journal = (JournalManagerIfc) Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);

			StringBuffer sb = new StringBuffer();
			
			sb.append("  Bank Code: " + bankCode);
			journal.journal(sb.toString());
		} else if (bus.getCurrentLetter().getName().equalsIgnoreCase(CommonLetterIfc.UNDO)) {
			tenderAttributes.remove(TenderConstants.EXPIRATION_DATE);
		}
		
		bus.mail("Success");
		}
	
	private TenderADOIfc setTransactionReentry(TenderCreditADO creditTender, TenderCargo cargo) {
		creditTender.setTransactionReentryMode(cargo.getRegister().getWorkstation().isTransReentryMode());
		return creditTender;
	}
	
	/*public String getBankCodeFromCardType(HashMap responseMap, String cardType) {
		String bankCode = null;
		String value = null;
		Object key = null;

		Iterator it = responseMap.keySet().iterator();
		while (it.hasNext()) {
			key = it.next();
			value = (String) responseMap.get(key);
          
		 value= value.replaceAll("\\s+","");
		 cardType= cardType.replaceAll("\\s+","");
		//Rev 1.1 End by Akanksha 
           
			if (cardType.equalsIgnoreCase(value))
				break;
		}

		return (String) key;
	}*/
	
	public HashMap getBankNameStrings(BusIfc bus) {
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		//CodeListIfc list = utility.getCodeListMap().get(MAXCodeConstantsIfc.CODE_LIST_CREDIT_DEBIT_BANK_CODES);
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
