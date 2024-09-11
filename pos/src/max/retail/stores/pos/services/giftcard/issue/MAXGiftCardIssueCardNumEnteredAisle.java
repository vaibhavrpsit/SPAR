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
import java.util.HashMap;
import java.util.Locale;

import max.retail.stores.domain.stock.MAXGiftCardPLUItem;
import max.retail.stores.domain.utility.MAXGiftCard;
import max.retail.stores.domain.utility.MAXGiftCardIfc;
import max.retail.stores.pos.services.giftcard.MAXGiftCardCargo;
import max.retail.stores.pos.services.giftcard.MAXGiftCardUtilities;
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
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardConstantsIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import com.qwikcilver.clientapi.svpos.GCPOS;

public class MAXGiftCardIssueCardNumEnteredAisle extends PosLaneActionAdapter implements GiftCardConstantsIfc {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8858882899593712764L;
	/**
	 * class name
	 **/
	public static final String LANENAME = "MAXGiftCardIssueCardNumEnteredAisle";
	/**
	 * revision number
	 **/
	public static final String revisionNumber = "$Revision: 1.7 $";

	// static GCPOS pos;

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

		/*
		 * read the data from the UI
		 */
		MAXGiftCardCargo cargo = (MAXGiftCardCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		DialogBeanModel dialogModel = new DialogBeanModel(); // Rev 1.0 changes
		
		boolean isReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
		boolean traningMode = cargo.getRegister().getWorkstation().isTrainingMode();
		
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
		System.out.println("Card Number captured: " + cardNum);
		String var1 = ";";
		String var2 = "=";
		String var3 = "?";
		String trackData = null;

		boolean isSwiped = false;
		boolean isScanned = false;
		MAXGiftCardUtilitiesQC utilObj = new MAXGiftCardUtilitiesQC();

		GCPOS pos = utilObj.getInstance();
		// parModel.getPromptAndResponseModel().getMSRModel();

		if (cardNum != null && cardNum.length() != 0) {
			if (cardNum.length() > 28)
				isSwiped = true;
			else if (cardNum.length() == 26)
				isScanned = true;
		}

		// String cardNumberTracked = null;

		if (isScanned) {
			trackData = ui.getInput();
			cardNumber = utilObj.getCardNumberFromTrackData(ui.getInput(), true);
		} else if (isSwiped) {
			trackData = var1 + cardNum.substring(0, 16) + var2 + cardNum.substring(16) + var3;
			cardNumber = utilObj.getCardNumberFromTrackData(trackData, true);
		}

		/*
		 * if (cardNum.length() > 16) { trackData = var1 + cardNum.substring(0,
		 * 16) + var2 + cardNum.substring(16) + var3; cardNumber =
		 * cardNum.substring(0, 16); ///isSwiped = true;
		 * 
		 * } else { cardNumber = cardNum; }
		 */
		// Gift Card not getting accepted at QC after swiping ends here
		boolean duplicate = false;
		boolean giftCardExists = false;
	
		// GiftCardBeanModel model = (GiftCardBeanModel)
		// ui.getModel(POSUIManagerIfc.SELL_GIFT_CARD);
		GiftCardBeanModel model = (GiftCardBeanModel) ui.getModel(POSUIManagerIfc.GET_CARD_NUM_FOR_GIFT_CARD); // Added
																												// by
		if (isSwiped) {
			model.setSwiped(true);
			// model.setTrackData(trackData);
		}
		if (isScanned) {
			model.setScanned(true);
			// model.setTrackData(trackData);
		}
		RetailTransactionIfc retailTransaction = cargo.getRetailTransaction();
		SaleReturnLineItemIfc[] items = null;
		if (retailTransaction != null)
			items = retailTransaction.getProductGroupLineItems(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD);

		if (items != null && items.length != 0) {
			for (int j = 0; j < items.length; j++) {
				if (items[j].getPLUItem() instanceof GiftCardPLUItem) {
					GiftCardPLUItemIfc gfItem = (GiftCardPLUItemIfc) items[j].getPLUItem();
					MAXGiftCard giftCard = (MAXGiftCard) gfItem.getGiftCard();
					if (isSwiped) {
						giftCard.setSwiped(true);
						giftCard.setTrackData(trackData);
					}
					if (cardNumber.equals(giftCard.getCardNumber())) {
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
		if (giftCardExists) {
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
					MAXGiftCardUtilities.createAlreadyGiftCardNumErrorDialogModel());
			giftCardExists = false;
			return;
		}

		boolean noError = true;
		// boolean multipleCard = false;
		String denominationAmount = null;
		MAXGiftCardIfc giftCard = (MAXGiftCardIfc) cargo.getGiftCard();
		
		
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
	        if ( noError )
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
		// MGGiftCardUtilitiesQC utilObj = new MGGiftCardUtilitiesQC();

		// GCPOS pos = utilObj.getInstance();
		if (pos != null) {
			denominationAmount = giftCard.getInitialBalance().toString();
			// POSBaseBeanModel pmodel = (POSBaseBeanModel) ui.getModel();
			PromptAndResponseModel parModel = model.getPromptAndResponseModel();

			parModel.getMSRModel();// .getTrack2Data();
			// POSUIManagerIfc ui =
			// (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
			ui.showScreen(MAXPOSUIManagerIfc.GIFT_CARD_VALIDATING_SCREEN, model);
			HashMap balanceEnquiryMap = null;
			String notes = null;
			notes = "{VldType~GCACT|AMT~" + denominationAmount + "}";
			/*
			 * if (!isSwiped) { cardNumber =
			 * utilObj.getCardNumberFromTrackData(trackData,true);
			 * balanceEnquiryMap = utilObj.balanceEnquiry(pos, cardNumber,
			 * denominationAmount, "BLC"); } else {
			 */
			// cardNumber = utilObj.getCardNumberFromTrackData(trackData,true);
			balanceEnquiryMap = utilObj.balanceEnquiryWithNotes(pos, cardNumber, trackData, notes);
			// balanceEnquiryMap = utilObj.balanceEnquiryUsingTrackData(pos,
			// cardNumber, denominationAmount, "BLC", trackData);
			// }

			if (logger.isInfoEnabled()) {
				logger.info("GiftCardIssueCardNumEnteredAisle.traverse(), cardNumber = " + cardNumber + "");
			}

			LetterIfc letter = null;
			if (MAXGiftCardUtilities.isEmpty(model, cardNumber, logger, bus.getServiceName())) {
				noError = false;
				parModel = model.getPromptAndResponseModel();
				if ((parModel != null) && parModel.isSwiped()) {
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
							MAXGiftCardUtilities.createBadMSRReadDialogModel(utility));
				} else {
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
							MAXGiftCardUtilities.createInvalidGiftCardNumErrorDialogModel());
				}
			}
			// if (noError && balanceEnquiryMap.get("Expiry") != null &&
			// ("0").equals(balanceEnquiryMap.get("ResponseCode").toString())) {
			else {
				String CardNumber = "N/A";
				String ResponseMessage = "N/A";
				String ResponseCode = "N/A";
				String CardCurrencySymbol = "N/A";
				String Amount = "N/A";
				String Expiry = "N/A";
				String CardType = "N/A";
				String AcquirerId = "Could Not Validate Card";

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

							return;
						}
					}
				}

				if (balanceEnquiryMap != null && balanceEnquiryMap.size() != 0) {
					if (balanceEnquiryMap.containsKey("ResponseCode") && balanceEnquiryMap.get("ResponseCode") != null
							&& !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseCode"))
							&& !"".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseCode"))) {
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
					if (balanceEnquiryMap.containsKey("CardNumber") && balanceEnquiryMap.get("CardNumber") != null
							&& !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("CardNumber"))
							&& !"".equalsIgnoreCase((String) balanceEnquiryMap.get("CardNumber"))) {
						CardNumber = (String) balanceEnquiryMap.get("CardNumber");
					}

					if (ResponseCode.equalsIgnoreCase("10001") || ResponseCode.equalsIgnoreCase("10027")
							|| ResponseCode.equalsIgnoreCase("0") || ResponseCode.equalsIgnoreCase("10096")) {
						noError = false;
						// String msg[] = new String[7];
						dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
						msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
						String giftCardNum = CardNumber;
						msg[1] = "GIFTCard" + " "
								+ giftCardNum.substring(giftCardNum.length() - 4, giftCardNum.length());
						msg[2] = " Total Amount In This Card Is " + Amount;
						if (balanceEnquiryMap.get("Expiry") != null
								&& !("null").equals(balanceEnquiryMap.get("Expiry")))
							msg[3] = "Expiry Date "
									+ utilObj.calculateEYSDate(balanceEnquiryMap.get("Expiry").toString());
						else
							msg[3] = "Expiry Date " + Expiry;
						if (ResponseCode.equalsIgnoreCase("0"))
							msg[4] = "Gift Card Number Entered is Already Active";
						else
							msg[4] = ResponseMessage;
						msg[5] = AcquirerId;
						msg[6] = "::Thanks::";
						dialogModel.setArgs(msg);
						dialogModel.setType(DialogScreensIfc.CONFIRMATION);
						dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "InvalidCardNumber");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						return;
					} else if (!ResponseCode.equalsIgnoreCase("10027") && !ResponseCode.equalsIgnoreCase("10001")
							&& !ResponseCode.equalsIgnoreCase("10029")) {
						noError = false;
						// String msg[] = new String[7];
						dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
						msg[0] = " ";
						msg[1] = "Gift Card cannot be issued";
						// msg[2] = " due to network offline"; //" Total Amount
						// In This Card Is " + balanceEnquiryMap.get("Amount");
						if (ResponseMessage != null && !ResponseMessage.equalsIgnoreCase("")
								&& !ResponseMessage.equalsIgnoreCase("N/A")) {
							msg[2] = ResponseMessage; // " Total Amount In This
														// Card Is " +
														// balanceEnquiryMap.get("Amount");
						} else {
							msg[2] = " Due to Network offline"; // " Total
																// Amount In
																// This Card Is
																// " +
																// balanceEnquiryMap.get("Amount");
						}
						msg[3] = "Press Enter button to return to previous screen";
						msg[4] = " ";
						msg[5] = " ";
						msg[6] = " ";
						msg[6] = "::Thanks::";
						dialogModel.setArgs(msg);
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						return;
					}
				}

				if (noError) {
					GiftCardPLUItemIfc pluItem = (GiftCardPLUItemIfc) cargo.getPLUItem();

					if (pluItem == null) {
						String itemID = "0";
						try {
							itemID = pm.getStringValue(DEFAULT_GIFT_CARD_ITEM_ID);
						} catch (ParameterException e) {
							itemID = DEFAULT_ITEM_ID;
							if (logger.isInfoEnabled())
								logger.info(
										"GiftCardIssueCardNumEnteredAisle.traverse(), cannot find default giftCard item.");
						}
						try {
							pluItem = MAXGiftCardUtilities.getPluItem(ui, cargo, itemID, logger, bus.getServiceName(),
									true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (pluItem != null) {
						CurrencyIfc amount = giftCard.getCurrentBalance();
						if (cargo.getGcType() != null && ("promoGiftCard").equals(cargo.getGcType())) {
							amount = DomainGateway.getBaseCurrencyInstance(new BigDecimal("0.0"));
						}

						giftCard.setExpirationDate(
								utilObj.calculateEYSDate(balanceEnquiryMap.get("Expiry").toString()));
						giftCard.setCardNumber(cardNumber);
						giftCard.setTrackData(trackData);
						if (isSwiped) {
							giftCard.setSwiped(true);
							((MAXGiftCardPLUItem) pluItem).setTrackData(trackData);
						}
						if (isScanned) {
							giftCard.setScanned(isScanned);
							((MAXGiftCardPLUItem) pluItem).setTrackData(trackData);
						}
						pluItem.setGiftCard(giftCard);
						pluItem.setPrice(amount);
						cargo.setItemQuantity(Util.I_BIG_DECIMAL_ONE);
						cargo.setPLUItem(pluItem);
						String soldDate = "N/A";
						if (giftCard.getDateSold() != null) {
							Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
							soldDate = giftCard.getDateSold().toFormattedString(locale);
						}
						JournalManagerIfc jmi = (JournalManagerIfc) Gateway.getDispatcher()
								.getManager(JournalManagerIfc.TYPE);
						StringBuffer journalString = new StringBuffer(giftCard.toJournalString());
						journalString.append("\n    Date Sold: ");
						journalString.append(soldDate);
						journalString.append("\n    Issued.");
						jmi.journal(cargo.getTransaction().getCashier().getLoginID(),
								cargo.getTransaction().getTransactionID(), journalString.toString());
						letter = new Letter(CommonLetterIfc.CONTINUE);
					}
				}
			}
			if (letter != null) {
				bus.mail(letter, BusIfc.CURRENT);
			}
		} else {
			utilObj.showQCOfflineErrorBox(bus);
		}
	}
}
