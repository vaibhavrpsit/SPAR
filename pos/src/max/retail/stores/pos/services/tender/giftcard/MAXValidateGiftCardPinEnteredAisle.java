/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 * Upgraded to ORPOS 14.0.1 from Lifestyle ORPOS 12.0.9IN: AAKASH GUPTA(EYLLP):Aug-11-2015
 * 
 *  Rev 1.1 22/07/2013 Priyanka Singh change for Issue The value to be 
 *  passed for notes field for Balance-Check call for Redeem Validation For manual entry.
 *  Rev 1.0  Priyanka Singh for Pin Entry
 *  
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.tender.giftcard;


import java.util.HashMap;

import max.retail.stores.domain.utility.MAXGiftCardIfc;
import max.retail.stores.pos.services.qc.MAXGiftCardUtilitiesQC;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.transaction.Transaction;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

import com.qwikcilver.clientapi.svclient.SVTags;
import com.qwikcilver.clientapi.svpos.GCPOS;

public class MAXValidateGiftCardPinEnteredAisle extends PosLaneActionAdapter{

	/**
	 * Rev 1.0 Pin Entry change  Priyanka Singh This site is
	 * created to check whether gift card is swept or perform
	 * multiple type validation for manual entry.
	 * 
	 */

	private static final long serialVersionUID = -7926925928495763969L;
	protected GiftCardIfc giftCard = null;
	protected RegisterIfc register;
	protected StoreStatusIfc storeStatus;
	protected RegisterIfc registerID;

	public void traverse(BusIfc bus)
	{
		TenderCargo cargo = (TenderCargo)bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager("UIManager");
		POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel();
		PromptAndResponseModel parModel = model.getPromptAndResponseModel();
		String cardPin = null;
		boolean vPin = true;
		MAXGiftCardIfc giftCard = null;
		String notes ="";

		if (parModel != null)
		{
			giftCard = (MAXGiftCardIfc)cargo.getGiftCard();

			cardPin = ui.getInput();

			giftCard.setCardPin(cardPin);
		}

		if ((cardPin != null) && (cardPin.length() > 0))
		{
			vPin = false;
		}

		if (vPin)
		{
			DialogBeanModel dialogModel = new DialogBeanModel();
			String[] msg = new String[7];
			dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
			msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
			msg[1] = ("GIFTCard " + giftCard.getCardNumber());
			msg[2] = "Either card number or card pin is incorrect.";
			msg[3] = "";
			msg[4] = "Please Press Enter To Proceed using another Tender.";
			msg[5] = "Could Not Validate Card";

			msg[6] = "::Thanks::";
			dialogModel.setArgs(msg);
			// dialogModel.setType(1);
			dialogModel.setType(DialogScreensIfc.ERROR);
			//dialogModel.setButtonLetter(0, "Undo");
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
			ui.showScreen("DIALOG_TEMPLATE", dialogModel);
		}

		//GiftCardHelperUtility utilObj = new GiftCardHelperUtility();
		MAXGiftCardUtilitiesQC utilObj = new MAXGiftCardUtilitiesQC();
		GCPOS pos = utilObj.getInstance();
		
		/* Change for Rev 1.1 : Starts */
		if(pos!=null){
			boolean isValid = false;
			String denominationAmount ="";
			if(giftCard.getBalanceForInquiryFailure()!=null)
				denominationAmount = giftCard.getBalanceForInquiryFailure().toString();
			if(denominationAmount!=null)
				notes="VldType~GCRDM|Amt~"+denominationAmount +"|Manual=Y" ;	
			HashMap balanceEnquiryMap = null;
			/*change strat by akanksha for bug 9573*/
			boolean isTrainingMode = cargo.getRegister().getWorkstation().isTransReentryMode();
			if(!isTrainingMode){
				if (!vPin) {
					
				//	System.out.println(balanceEnquiryMap);
					//cargo.getTransaction().getTransactionTotals().getSubtotal();
					//TransactionIfc transaction=cargo.getCurrentTransactionADO().toLegacy();
					balanceEnquiryMap = utilObj.balanceEnquiryUsingPin(pos, giftCard.getCardNumber(), cardPin ,notes );
					/*balanceEnquiryMap = utilObj.redeemCardWithBillAmountNPin(pos, giftCard.getCardNumber(), "250.00", cargo.getCurrentTransactionADO().getTransactionID(),
							"250.00", cardPin);	*/
					//akanksha
					String responseCode = balanceEnquiryMap.get("ResponseCode").toString();
					if (("0").equals(responseCode))
						isValid = true;

				} }else {


					CurrencyIfc amt = DomainGateway.getBaseCurrencyInstance(denominationAmount);
					giftCard.setInitialBalance(amt);
					giftCard.setCurrentBalance(amt);
					bus.mail(new Letter("AfterBalance"), BusIfc.CURRENT);
					return;
					//change End by akanksha for bug 9573

				}

			if(isValid){
				if (balanceEnquiryMap.get("Amount") != null) {
					DialogBeanModel dialogModel = new DialogBeanModel();
					String msg[] = new String[7];
					dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
					msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
					String cardNum= giftCard.getCardNumber();
					String cardnumber=cardNum.substring(cardNum.length()-4,cardNum.length());
					msg[1] = "GIFTCard" + " ***" + cardnumber;
					if(balanceEnquiryMap.get("CardCurrencySymbol")!=null)
					{
						msg[2] = " Total Amount In This Card Is " + balanceEnquiryMap.get("Amount") + " "
								+ balanceEnquiryMap.get("CardCurrencySymbol");
					}
					else{
						{
							msg[2] = " Total Amount In This Card Is " + balanceEnquiryMap.get("Amount") + " "
									+ "CardCurrencySymbol"+"N/A";
						}
					}
					/*msg[2] = " Total Amount In This Card Is " + balanceEnquiryMap.get("Amount").toString() + " "
				+ balanceEnquiryMap.get("CardCurrencySymbol").toString();*/
					/*if block added by seema for null check*/
					if (balanceEnquiryMap.get("Expiry") != null && !"null".equals(balanceEnquiryMap.get("Expiry")))
					{  msg[3] = "ExpiryDate " + utilObj.calculateEYSDate(balanceEnquiryMap.get("Expiry").toString());}
					else{
						msg[3] = "ExpiryDate:"+"N/A";
					}
					msg[4] = balanceEnquiryMap.get("ResponseMessage").toString();
					//msg[5] = (String) balanceEnquiryMap.get("CardType");
					if(balanceEnquiryMap.get("CardType")!=null){
						msg[5] = balanceEnquiryMap.get("CardType").toString(); 
					}
					else {
						msg[5] = "Lifestyle Gift Cards";
					}

					msg[6] = "::Thanks::";
					dialogModel.setArgs(msg);
					dialogModel.setType(DialogScreensIfc.CONFIRMATION);				
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "AfterBalance");
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Undo");
					// POSUIManagerIfc ui = (POSUIManagerIfc)
					// bus.getManager(UIManagerIfc.TYPE);

					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

				}} else{
					DialogBeanModel dialogModel = new DialogBeanModel();
					cargo.setGiftCard(null);
					String msg[] = new String[2];
					dialogModel.setResourceID("GIFTCARD_ENQUIRYQC1");
					//Modified By Chiranjib Starts Here
					if (balanceEnquiryMap!= null){
						msg[0]=(String) balanceEnquiryMap.get(SVTags.RESPONSE_MESSAGE);
						dialogModel.setArgs(msg);
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Undo");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					}
					else{
						String[] msg2 = new String[7];
						dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
						msg2[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
						msg2[1] = ("GIFTCard " + giftCard.getCardNumber());
						msg2[2] = "Either card number or card pin is incorrect.";
						msg2[3] = "";
						msg2[4] = "Please Press Enter To Proceed using another Tender.";
						msg2[5] = "Could Not Validate Card";

						msg2[6] = "::Thanks::";
						dialogModel.setArgs(msg2);
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
						ui.showScreen("DIALOG_TEMPLATE", dialogModel);
					}
					//Modified By Chiranjib Ends Here
				}
			/* Change for Rev 1.1 : End */
			if ((!vPin) && (balanceEnquiryMap != null) && (balanceEnquiryMap.size() != 0) && ("0".equals(balanceEnquiryMap.get("ResponseCode").toString())))
			{
				String amount = balanceEnquiryMap.get("Amount").toString();

				CurrencyIfc amt = DomainGateway.getBaseCurrencyInstance(amount);

				giftCard.setInitialBalance(amt);
				giftCard.setCurrentBalance(amt);
				// bus.mail(new Letter("RedeemGiftCard"), BusIfc.CURRENT);
				//bus.mail(new Letter("AfterBalance"), BusIfc.CURRENT);---------
			}

			if ((!vPin) && (balanceEnquiryMap.get("Amount") == null)) {
				DialogBeanModel dialogModel = new DialogBeanModel();
				String[] msg = new String[7];
				dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
				msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
				msg[1] = ("GIFTCard " + giftCard.getCardNumber());
				msg[2] = balanceEnquiryMap.get("ResponseMessage").toString();
				msg[3] = "";
				msg[4] = "Please Press Enter To Proceed using another Tender.";
				if (balanceEnquiryMap.get("AcquirerId") != null)
					msg[5] = balanceEnquiryMap.get("AcquirerId").toString();
				else {
					msg[5] = "Could Not Validate Card";
				}
				msg[6] = "::Thanks::";
				dialogModel.setArgs(msg);
				//dialogModel.setType(1);
				dialogModel.setType(DialogScreensIfc.ERROR);
				//dialogModel.setButtonLetter(0, "Undo");
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
				ui.showScreen("DIALOG_TEMPLATE", dialogModel);
			}
		}
		else
		{
			utilObj.showQCOfflineErrorBox(bus);	
		}
	}
}