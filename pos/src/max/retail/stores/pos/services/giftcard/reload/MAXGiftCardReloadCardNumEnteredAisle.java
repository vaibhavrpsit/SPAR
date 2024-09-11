/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.2 	Nov 19, 2018		Purushotham Reddy	Defect fix for GC Reload
 *	Rev	1.1 	Jun 23, 2017		Jyoti Yadav			HSN number issue for GC Reload
 *	Rev	1.0 	Dec 20, 2016		Mansi Goel			Changes for Gift Card FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.giftcard.reload;

import java.util.HashMap;

import max.retail.stores.domain.stock.MAXGiftCardPLUItem;
import max.retail.stores.domain.utility.MAXGiftCard;
import max.retail.stores.pos.services.giftcard.MAXGiftCardUtilities;
import max.retail.stores.pos.services.giftcard.MaxGiftCardPromptMessages;
import max.retail.stores.pos.services.qc.MAXGiftCardUtilitiesQC;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItem;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.services.giftcard.GiftCardConstantsIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import com.qwikcilver.clientapi.svpos.GCPOS;

//--------------------------------------------------------------------------
/**
 * Validates the gift card number entered or scanned and adds it to the cargo.
 * <p>
 * 
 * @version $Revision: 1.7 $
 **/
// --------------------------------------------------------------------------
public class MAXGiftCardReloadCardNumEnteredAisle extends PosLaneActionAdapter implements GiftCardConstantsIfc {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5693833249190182356L;
	/**
	 * class name
	 **/
	public static final String LANENAME = "GiftCardReloadCardNumEnteredAisle";
	/**
	 * revision number
	 **/
	public static final String revisionNumber = "$Revision: 1.7 $";

	// ----------------------------------------------------------------------
	/**
	 * Get the data entered on the Gift Card screen or from MSR Validate and
	 * save the gift card number. Send the 'Continue' letter
	 * <p>
	 * 
	 * @param bus
	 *            the bus traversing this lane
	 **/
	// ----------------------------------------------------------------------
	public void traverse(BusIfc bus) {

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		GiftCardBeanModel model = (GiftCardBeanModel) ui.getModel(POSUIManagerIfc.GET_CARD_NUM_FOR_GIFT_CARD);
		GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
		boolean isFreshCard = false;
		boolean isReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
		boolean traningMode = cargo.getRegister().getWorkstation().isTrainingMode();
		
		GiftCardIfc giftCard = cargo.getGiftCard();
		// Gift Card not getting accepted at QC after swiping

		DialogBeanModel dialogModel = new DialogBeanModel(); // Rev 1.0 changes
		/*UIModelIfc model2 = ui.getModel();
		String str = "";
		if (model2 instanceof GiftCardBeanModel) {
			GiftCardBeanModel gcModel = (GiftCardBeanModel) model2;
			MSRModel msrModel = gcModel.getPromptAndResponseModel().getMSRModel();
			if (msrModel != null) {
				byte[] track2Data = msrModel.getTrack2Data();

				if (track2Data != null && track2Data.length > 0) {
					for (int i = 0; i < track2Data.length; i++) {
						str += (char) track2Data[i];
					}
					System.out.println("Track Data: " + str);
				} else {
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
							MAXGiftCardUtilities.createBadMSRReadDialogModel(utility));
					return;
				}

			}
		}

		// Gift Card not getting accepted at QC after swiping
		String cardNumber = null;
		String cardNum = "";
		if (str.equals(""))
			cardNum = ui.getInput();
		else
			cardNum = str.substring(0, str.indexOf("=")) + str.substring(str.indexOf("=") + 1);*/
		String cardNumber = null;
		String cardNum = ui.getInput();
		
		String var1 = ";";
		String var2 = "=";
		String var3 = "?";
		String trackData = "";
		boolean isSwiped = false;
		boolean isScanned = false;
		MAXGiftCardUtilitiesQC utilObj = new MAXGiftCardUtilitiesQC();
		GCPOS pos = utilObj.getInstance();
		
		System.out.println("Card Number captured: " + cardNum);

		if (cardNum != null && cardNum.length() != 0) {
			if (cardNum.length() > 28)
				isSwiped = true;
			else if (cardNum.length() == 26)
				isScanned = true;
		}

		if (isScanned) {
			trackData = ui.getInput();
			cardNumber = utilObj.getCardNumberFromTrackData(ui.getInput(), true);

		} else if (isSwiped) {
			trackData = var1 + cardNum.substring(0, 16) + var2 + cardNum.substring(16) + var3;
			cardNumber = utilObj.getCardNumberFromTrackData(trackData, true);
		}

		
	
		boolean duplicate = false;
		if (isSwiped) {
			model.setSwiped(true);
		}
		if (isScanned) {
			model.setScanned(true);

		}
		
		RetailTransactionIfc retailTransaction = cargo.getRetailTransaction();
		
		SaleReturnLineItemIfc[] items = null;
		if (retailTransaction != null)
			items = retailTransaction.getProductGroupLineItems(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD);

		if (items != null && items.length != 0) {
			for (int j = 0; j < items.length; j++) {
				if (items[j].getPLUItem() instanceof GiftCardPLUItem) {
					GiftCardPLUItemIfc gfItem = (GiftCardPLUItemIfc) items[j].getPLUItem();
					MAXGiftCard giftCardr = (MAXGiftCard) gfItem.getGiftCard();
					if (isSwiped) {
					}
					if (cardNumber != null && cardNumber.length() != 0 && cardNumber.equals(giftCardr.getCardNumber())) {
						duplicate = true;
					}
				}
			}
		}
		if (duplicate) {
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
					MAXGiftCardUtilities.createDupllicateGiftCardNumErrorDialogModel());
			duplicate = false;
			return;
		}
		if (logger.isInfoEnabled())
			logger.info("GiftCardNumberEnteredAisle.traverse(), cardNumber = " + cardNumber + "");
		
		if (isReentryMode || traningMode) {
			 PromptAndResponseModel parModel = model.getPromptAndResponseModel();
		        if(parModel.isSwiped())
		        {
		            giftCard.setEntryMethod(EntryMethod.Swipe);
		        }
		        else
		        {
		        
		            if(parModel.isScanned())
		            {
		                giftCard.setEntryMethod(EntryMethod.Scan);
		            }
		            else
		            {
		                giftCard.setEntryMethod(EntryMethod.Manual);
		            }
		          
		       
		        }
	        if ( noError(bus, cardNumber, true) )
	        {
	            String itemID="0";
	            try
	            {
	                itemID = pm.getStringValue(DEFAULT_GIFT_CARD_ITEM_ID);
	            }
	            catch (ParameterException e)
	            {
	                itemID = DEFAULT_ITEM_ID;
	            }

	            GiftCardPLUItemIfc pluItem = GiftCardUtilities.getPluItem(ui, cargo, itemID, logger, bus.getServiceName(), utility.getRequestLocales());
	            if ( pluItem != null)
	            {
	                CurrencyIfc amount = giftCard.getCurrentBalance();
	                giftCard.setCardNumber(cardNumber);
	                giftCard.setRequestType(GiftCardIfc.GIFT_CARD_RELOAD);
	                pluItem.setGiftCard(giftCard);
	                pluItem.setPrice(amount);
	                cargo.setItemQuantity(BigDecimalConstants.ONE_AMOUNT);
	                cargo.setPLUItem(pluItem);

	                JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
					jmi.journal(cargo.getTransaction().getCashier().getLoginID(),
							cargo.getTransaction().getTransactionID(), giftCard
									.toJournalString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL))
									+ Util.EOL
									+ I18NHelper.getString(
											I18NConstantsIfc.EJOURNAL_TYPE,
											JournalConstantsIfc.RELOADED_LABEL,
											null));
					Letter letter = new Letter(CommonLetterIfc.CONTINUE);
					bus.mail(letter, BusIfc.CURRENT);
	            }
	        }
	        return;
		}
		if (pos != null) {
			boolean isValid = false;
			if (giftCard == null) {
				giftCard = DomainGateway.getFactory().getGiftCardInstance();
			}
			String denominationAmount = giftCard.getCurrentBalance().toString();
			String notes = null;
			notes = "{VldType~GCRLD|AMT~" + denominationAmount + "}";
			ui.showScreen(MAXPOSUIManagerIfc.GIFT_CARD_VALIDATING_SCREEN, new POSBaseBeanModel());
			HashMap balanceEnquiryMap = utilObj.balanceEnquiryWithNotes(pos, cardNumber, trackData, notes);
			String ResCode = "";
			String msg[] = new String[7];
			if (balanceEnquiryMap != null && balanceEnquiryMap.size() != 0) {
				String responseCode = balanceEnquiryMap.get("ResponseCode").toString();

				if (balanceEnquiryMap.containsKey("ResponseCode") && balanceEnquiryMap.get("ResponseCode") != null
						&& !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseCode"))
						&& !"".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseCode"))) {
					ResCode = (String) balanceEnquiryMap.get("ResponseCode");
				}
				if (balanceEnquiryMap.containsKey("ResponseMessage")
						&& balanceEnquiryMap.get("ResponseMessage") != null
						&& !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseMessage"))
						&& !"".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseMessage"))) {

					String responseMessage = balanceEnquiryMap.get("ResponseMessage").toString();
					if (responseMessage.equalsIgnoreCase("Network is unreachable: connect")
							&& !(ResCode.equalsIgnoreCase("0"))) {
						dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
						msg[0] = "System is Offline ";
						msg[1] = " Gift Card Activity Can not be perfomed";
						msg[2] = "";
						msg[3] = "";
						msg[4] = "";
						msg[5] = "";
						msg[6] = "";
						dialogModel.setArgs(msg);
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						isFreshCard = true;
						return;
					}
				}
				 if (("0".equals(responseCode)) || ("21071".equalsIgnoreCase(responseCode))) {
			          isValid = true;
			        }
			}

			String CardNumber = "N/A";
			String ResponseMessage = "N/A";
			String ResponseCode = "N/A";

			String CardCurrencySymbol = "N/A";
			String Amount = "N/A";
			String Expiry = "N/A";
			String CardType = "N/A";
			String AcquirerId = "N/A";

			msg[2] = " ";

			if (balanceEnquiryMap != null) {
				if (balanceEnquiryMap.containsKey("ResponseMessage")
						&& balanceEnquiryMap.get("ResponseMessage") != null
						&& !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseMessage"))
						&& !"".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseMessage"))) {
					ResponseMessage = (String) balanceEnquiryMap.get("ResponseMessage");
				}
				if (balanceEnquiryMap.containsKey("CardCurrencySymbol")
						&& balanceEnquiryMap.get("CardCurrencySymbol") != null
						&& !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("CardCurrencySymbol"))
						&& !"".equalsIgnoreCase((String) balanceEnquiryMap.get("CardCurrencySymbol"))) {
					CardCurrencySymbol = (String) balanceEnquiryMap.get("CardCurrencySymbol");
				}
				if (balanceEnquiryMap.containsKey("Amount") && balanceEnquiryMap.get("Amount") != null
						&& !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("Amount"))
						&& !"".equalsIgnoreCase((String) balanceEnquiryMap.get("Amount"))) {
					Amount = (String) balanceEnquiryMap.get("Amount");
				}
				if (balanceEnquiryMap.containsKey("Expiry") && balanceEnquiryMap.get("Expiry") != null
						&& !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("Expiry"))
						&& !"".equalsIgnoreCase((String) balanceEnquiryMap.get("Expiry"))) {
					Expiry = (String) balanceEnquiryMap.get("Expiry");
				}
				if (balanceEnquiryMap.containsKey("CardType") && balanceEnquiryMap.get("CardType") != null
						&& !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("CardType"))
						&& !"".equalsIgnoreCase((String) balanceEnquiryMap.get("CardType"))) {
					CardType = (String) balanceEnquiryMap.get("CardType");
				}
				if (balanceEnquiryMap.containsKey("AcquirerId") && balanceEnquiryMap.get("AcquirerId") != null
						&& !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("AcquirerId"))
						&& !"".equalsIgnoreCase((String) balanceEnquiryMap.get("AcquirerId"))) {
					AcquirerId = (String) balanceEnquiryMap.get("AcquirerId");
				}
				if (balanceEnquiryMap.containsKey("ResponseCode") && balanceEnquiryMap.get("ResponseCode") != null
						&& !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseCode"))
						&& !"".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseCode"))) {
					ResponseCode = (String) balanceEnquiryMap.get("ResponseCode");
				}
			}

			if (ResponseCode.equalsIgnoreCase("0")) {

				dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
				msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
				msg[1] = "GIFTCard" + " " + cardNumber;
				if (CardCurrencySymbol != null && !CardCurrencySymbol.equalsIgnoreCase("N/A")) {
					msg[2] = " Total Amount In This Card Is " + CardCurrencySymbol + " " + Amount + " ";
				} else {
					msg[2] = " Total Amount In This Card Is " + " " + Amount + " ";
				}
				if (balanceEnquiryMap.get("Expiry") != null && !("null").equals(balanceEnquiryMap.get("Expiry")))
					msg[3] = "Expiry Date " + utilObj.calculateEYSDate(balanceEnquiryMap.get("Expiry").toString());
				else
					msg[3] = "Expiry Date " + Expiry;
				msg[4] = "Press Enter To Proceed ";

				msg[5] = CardType;
				msg[6] = "::Thanks::";
				dialogModel.setArgs(msg);
				dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				/*Change for Rev 1.1: Start*/
				//bus.mail("Continue", BusIfc.CURRENT);
				/*Change for Rev 1.1: End*/
			} else {
				dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
				msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
				msg[1] = "GIFTCard" + " " + cardNumber;
				if (balanceEnquiryMap.get("Expiry") != null && !("null").equals(balanceEnquiryMap.get("Expiry")))
					msg[3] = "Expiry Date " + utilObj.calculateEYSDate(balanceEnquiryMap.get("Expiry").toString());
				else
					msg[3] = "Expiry Date " + Expiry;
				if (!ResponseCode.equalsIgnoreCase("928"))
					msg[4] = ResponseMessage;
				else
					msg[4] = "Invalid Card";
				// Rev	1.2 Defect fix for GC Reload
				if (ResponseCode.equalsIgnoreCase("21071")) {
					msg[5] = "Press Enter To Proceed ";
					msg[6] = "::Thanks::";
					dialogModel.setArgs(msg);
					dialogModel.setType(7);
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				} else {
					msg[5] = AcquirerId;
					msg[6] = "::Thanks::";
					dialogModel.setArgs(msg);
					dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Undo");
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					isFreshCard = true;
				}
			}
			if (balanceEnquiryMap != null && balanceEnquiryMap.size() != 0
					&& ("0").equals(balanceEnquiryMap.get("ResponseCode").toString())) {
				// String responseCode =
				// balanceEnquiryMap.get("ResponseCode").toString();
				// String responseMessage =
				// balanceEnquiryMap.get("ResponseMessage").toString();
				// String invoiceNumber =
				// balanceEnquiryMap.get("InvoiceNumber").toString();
				// String acquirerId =
				// balanceEnquiryMap.get("AcquirerId").toString();
				// String expiryDate =
				// balanceEnquiryMap.get("Expiry").toString();
				String amount = balanceEnquiryMap.get("Amount").toString();
				// String cardType =
				// balanceEnquiryMap.get("CardType").toString();

				if (giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_RELOAD) {
					amount = giftCard.getCurrentBalance().toString(); // Reload
																		// Amount
				}
				CurrencyIfc amt = DomainGateway.getBaseCurrencyInstance(amount);
				giftCard.setInitialBalance(amt);
				giftCard.setCurrentBalance(amt);
				((MAXGiftCard) giftCard).setTrackData(trackData);

			}

			if (balanceEnquiryMap.size() == 0) {
				dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
				msg[0] = " ";
				msg[1] = "Gift Card cannot be reloaded";
				msg[2] = MaxGiftCardPromptMessages.CONNECTION_TIMEOUT;
				msg[3] = "Press Enter button to return to previous screen";
				msg[4] = " ";
				msg[5] = " ";
				msg[6] = " ";
				msg[6] = "::Thanks::";
				dialogModel.setArgs(msg);
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");

				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				isFreshCard = true;
			}
			if (!isFreshCard && noError(bus, cardNumber, isValid)) {
				String itemID = "0";
				try {
					itemID = pm.getStringValue(DEFAULT_GIFT_CARD_ITEM_ID);
				} catch (ParameterException e) {
					itemID = DEFAULT_ITEM_ID;
				}
				// changes starts ofr code merging(commenting below line as per
				// MAX)
				// GiftCardPLUItemIfc pluItem = GiftCardUtilities.getPluItem(ui,
				// cargo, itemID, logger, bus.getServiceName());
				/*Change for Rev 1.1: Start*/
				/*GiftCardPLUItemIfc pluItem = GiftCardUtilities.getPluItem(ui, cargo, itemID, logger,
						bus.getServiceName(),
						new LocaleRequestor(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));*/
				GiftCardPLUItemIfc pluItem = null;
				try{
					pluItem = MAXGiftCardUtilities.getPluItem(ui, cargo, itemID, logger,
							bus.getServiceName(), true);	
				}catch (Exception e) {
					e.printStackTrace();
				}
				/*Change for Rev 1.1: End*/
				// Changes ends for code merging
				if (pluItem != null) {
					CurrencyIfc amount = giftCard.getCurrentBalance();
					giftCard.setCardNumber(cardNumber);
					giftCard.setExpirationDate(utilObj.calculateEYSDate(balanceEnquiryMap.get("Expiry").toString()));
					((MAXGiftCardPLUItem) pluItem).setTrackData(trackData);
					((MAXGiftCardPLUItem) pluItem).setSwiped(isSwiped);
					((MAXGiftCardPLUItem) pluItem).setScanned(isScanned);
					giftCard.setRequestType(GiftCardIfc.GIFT_CARD_RELOAD);
					pluItem.setGiftCard(giftCard);
					pluItem.setPrice(amount);
					cargo.setItemQuantity(Util.I_BIG_DECIMAL_ONE);
					cargo.setPLUItem(pluItem);

					JournalManagerIfc jmi = (JournalManagerIfc) Gateway.getDispatcher().getManager(
							JournalManagerIfc.TYPE);
					jmi.journal(cargo.getTransaction().getCashier().getLoginID(), cargo.getTransaction()
							.getTransactionID(), giftCard.toJournalString() + "\n    Reloaded.");
					Letter letter = new Letter(CommonLetterIfc.CONTINUE);
					if (logger.isInfoEnabled())
						logger.info("GiftCardNumberEnteredAisle.traverse(), giftCard = " + cardNumber + "");
					bus.mail(letter, BusIfc.CURRENT);
				}
			}
		} else {
			utilObj.showQCOfflineErrorBox(bus);
		}
	}

	protected boolean noError(BusIfc bus, String cardNumber, boolean flag) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		// ParameterManagerIfc pm = (ParameterManagerIfc)
		// bus.getManager(ParameterManagerIfc.TYPE);
		// GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
		GiftCardBeanModel model = (GiftCardBeanModel) ui.getModel(POSUIManagerIfc.SELL_GIFT_CARD);

		// String cardNumber = ui.getInput();

		boolean noError = true;

		if (GiftCardUtilities.isEmpty(model, cardNumber, logger, bus.getServiceName())) {
			noError = false;
			PromptAndResponseModel parModel = model.getPromptAndResponseModel();
			if ((parModel != null) && parModel.isSwiped()) {
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, GiftCardUtilities.createBadMSRReadDialogModel(utility));

			} else {
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
						GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel());
			}
		}

		if (!flag) {
			noError = false;
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel());
		}

		return noError;
	}
	
    protected boolean noErrorBase(BusIfc bus, EncipheredCardDataIfc cardData)
    {
        POSUIManagerIfc     ui        = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc   utility   = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm        = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        GiftCardCargo       cargo     = (GiftCardCargo)bus.getCargo();
        GiftCardBeanModel   model     = (GiftCardBeanModel) ui.getModel(POSUIManagerIfc.SELL_GIFT_CARD);

        boolean noError = true;

        if ( GiftCardUtilities.isEmpty(model, cardData.getMaskedAcctNumber(), logger, bus.getServiceName()) )
        {
            noError = false;
            PromptAndResponseModel parModel = model.getPromptAndResponseModel();
            if ( (parModel!=null) && parModel.isSwiped() )
            {
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
                              GiftCardUtilities.createBadMSRReadDialogModel(utility));

            }
            else
            {
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
                              GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel());
            }
        }

        if ( cardData != null && noError && !GiftCardUtilities.isValidBinRange(pm, utility, cardData, logger, bus.getServiceName()) )
        {
            noError = false;
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
                          GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel());
        }

        if ( noError && !cargo.getRegister().getWorkstation().isTrainingMode() )
        {
            if (!GiftCardUtilities.isValidCheckDigit(utility, cardData, logger, bus.getServiceName()) )
            {
               noError = false;
               ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
                             GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel());
            }
        }
        return noError;
    }
}
