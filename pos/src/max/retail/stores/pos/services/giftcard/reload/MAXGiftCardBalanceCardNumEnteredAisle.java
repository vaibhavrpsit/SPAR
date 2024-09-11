/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
    Rev 1.4  11/feb/2016  	Akanksha Chauhan\Bhanu Priya,  	Changes for Offline message
	Rev 1.3  16/Jul/2013	Jyoti Rawal,Bug 7147 - POS Crash: GC balance inquiry
	Rev 1.2  09/Jul/2013	Jyoti Rawal,Bug 6890 - GC- Incorrect GC Number is displaying
    Rev 1.1  06/Jun/2013	Jyoti Rawal, gc Balance Inq.- Error Message is not proper while QC is Offline
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.giftcard.reload;

import java.util.HashMap;

import com.qwikcilver.clientapi.svpos.GCPOS;

import max.retail.stores.pos.services.giftcard.MAXGiftCardUtilities;
import max.retail.stores.pos.services.qc.MAXGiftCardUtilitiesQC;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.services.giftcard.GiftCardConstantsIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;

//--------------------------------------------------------------------------
/**
 * Validates the gift card number entered or scanned and adds it to the cargo.
 * <p>
 * 
 * @version $Revision: 1.6 $
 **/
// --------------------------------------------------------------------------
public class MAXGiftCardBalanceCardNumEnteredAisle extends PosLaneActionAdapter implements GiftCardConstantsIfc {
	/**
	 * 
	 */
	private static final long serialVersionUID = 260387786691412107L;
	/**
	 * class name
	 **/
	public static final String LANENAME = "GiftCardReloadCardNumEnteredAisle";
	/**
	 * revision number
	 **/
	public static final String revisionNumber = "$Revision: 1.6 $";

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

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		//ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		DialogBeanModel dialogModel = new DialogBeanModel();
		GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();

		MAXGiftCardUtilitiesQC utilObj = new MAXGiftCardUtilitiesQC();

		GCPOS pos = utilObj.getInstance();

		/*UIModelIfc model2 = ui.getModel();
		String str="";
		if(model2 instanceof GiftCardBeanModel)
		{
			GiftCardBeanModel gcModel = (GiftCardBeanModel)model2;
			MSRModel msrModel = gcModel.getPromptAndResponseModel().getMSRModel();
			if(msrModel !=null)
			{
				byte[] track2Data = msrModel.getTrack2Data();
				
				if(track2Data!=null && track2Data.length>0)
				{
					for(int i = 0 ; i<track2Data.length ;i++)
					{
						str+=(char)track2Data[i];
					}
					System.out.println("Track Data: " + str);
				}
				else
				{	
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, MAXGiftCardUtilities.createBadMSRReadDialogModel(utility));
					return;
				}
				
			}
		}
		*/
		GiftCardIfc giftCard = cargo.getGiftCard();
		String cardNumber = null;
		String cardNum = ui.getInput();
		String var1 = ";";
		String var2 = "=";
		String var3 = "?";
		String trackData = "";
		boolean isSwiped = false;
		boolean isScanned = false;
		// Gift Card not getting accepted at QC after swiping
		/*if(str.equals(""))
			cardNum = ui.getInput();
		else
			cardNum = str.substring(0, str.indexOf("=")) + str.substring(str.indexOf("=")+1);*/
		System.out.println("Card Number captured: " + cardNum);
		
		if(cardNum != null && cardNum.length()!= 0)
		{
			if(cardNum.length() > 28)
				isSwiped = true;
			else if(cardNum.length() == 26)
				isScanned = true;
		}
		if(isScanned)
		{
			cardNumber = utilObj.getCardNumberFromTrackData(ui.getInput(),true);
			trackData = ui.getInput();
		}
		else if(isSwiped)
		{
			trackData = var1 + cardNum.substring(0, 16) + var2 + cardNum.substring(16) + var3;
			cardNumber = utilObj.getCardNumberFromTrackData(trackData,true);
		}
		
		/*if (cardNum.length() > 16) {
			trackData = var1 + cardNum.substring(0, 16) + var2 + cardNum.substring(16) + var3;
			cardNumber = cardNum.substring(0, 16);
			isSwiped = true;

		} else {
			cardNumber = cardNum;
		}*/
		// Gift Card not getting accepted at QC after swiping ends here
		GiftCardBeanModel model = (GiftCardBeanModel) ui.getModel(POSUIManagerIfc.GET_CARD_NUM_FOR_GIFT_CARD);

		if (isSwiped) {

			model.setSwiped(true);
			// model.setTrackData(trackData);
		}
		// cardNumber = model.getCardRangeFrom();
		if (logger.isInfoEnabled())
			logger.info("GiftCardNumberEnteredAisle.traverse(), cardNumber = " + cardNumber + "");

		//MGGiftCardUtilitiesQC utilObj = new MGGiftCardUtilitiesQC();

		//GCPOS pos = utilObj.getInstance();
		if (pos != null) {
			boolean isValidCard=true;
			try{ //Rev 1.3 changes
			//boolean isValid = false;
			if (giftCard == null) {
				giftCard = DomainGateway.getFactory().getGiftCardInstance();
			}
			String denominationAmount = giftCard.getInitialBalance().toString();
			// POSBaseBeanModel model = new POSBaseBeanModel();
			// POSUIManagerIfc ui =
			// (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
			ui.showScreen(MAXPOSUIManagerIfc.GIFT_CARD_VALIDATING_SCREEN, new DialogBeanModel());
			HashMap balanceEnquiryMap = null;
			//if (!isSwiped)
				//balanceEnquiryMap = utilObj.balanceEnquiry(pos, cardNumber, denominationAmount, "BLC");
			//else
				balanceEnquiryMap = utilObj.balanceEnquiry(pos, cardNumber, denominationAmount, "BLC", trackData);
				//balanceEnquiryMap = utilObj.balanceEnquiryUsingTrackData(pos, cardNumber, denominationAmount, "BLC", trackData);
	/*		if (balanceEnquiryMap != null && balanceEnquiryMap.size() != 0) {
				//String responseCode = balanceEnquiryMap.get("ResponseCode").toString();
				//String responseMessage = balanceEnquiryMap.get("ResponseMessage").toString();
				// String invoiceNumber =
				// balanceEnquiryMap.get("InvoiceNumber").toString();
				// String acquirerId =
				// balanceEnquiryMap.get("AcquirerId").toString();
				// String expiryDate =
				// balanceEnquiryMap.get("Expiry").toString();
				// String previousBalance =
				// balanceEnquiryMap.get("PreviousBalance").toString();
				// String cardType =
				// balanceEnquiryMap.get("CardType").toString();
				//if (("0").equals(responseCode))
					//isValid = true;

			}*/
				String CardNumber="N/A";
				String ResponseMessage="N/A";
				String ResponseCode="N/A";		
				String CardCurrencySymbol="N/A";
				String Amount="N/A";
				String Expiry="N/A";
				String CardType="N/A";
				String AcquirerId="Could Not Validate Card";
				
				 // Rev 1.4 Start
				
				String ResCode="";
				String msg[] = new String[7];
				if (balanceEnquiryMap != null && balanceEnquiryMap.size() != 0) {
					String responseCode = balanceEnquiryMap.get("ResponseCode").toString();
					
					if(balanceEnquiryMap.containsKey("ResponseCode") && balanceEnquiryMap.get("ResponseCode")!=null && !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseCode")) && !"".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseCode"))){
						 ResCode=(String) balanceEnquiryMap.get("ResponseCode");
					}
					if (balanceEnquiryMap.containsKey("ResponseMessage") && balanceEnquiryMap.get("ResponseMessage")!=null && !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseMessage")) 
							&& !"".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseMessage"))) {
					
						String responseMessage = balanceEnquiryMap.get("ResponseMessage").toString();
						if(responseMessage.equalsIgnoreCase("Network is unreachable: connect")&& !(ResCode.equalsIgnoreCase("0"))){
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
					 // Rev 1.4 End
				
				if (balanceEnquiryMap!=null && balanceEnquiryMap.size() != 0){
						if(balanceEnquiryMap.containsKey("ResponseCode") && balanceEnquiryMap.get("ResponseCode")!=null && !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseCode")) && !"".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseCode"))){
							ResponseMessage=(String) balanceEnquiryMap.get("ResponseMessage");
						}
						if(balanceEnquiryMap.containsKey("CardCurrencySymbol") && balanceEnquiryMap.get("CardCurrencySymbol")!=null && !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("CardCurrencySymbol")) && !"".equalsIgnoreCase((String) balanceEnquiryMap.get("CardCurrencySymbol"))){
							CardCurrencySymbol=(String) balanceEnquiryMap.get("CardCurrencySymbol");
						}
						if(balanceEnquiryMap.containsKey("Amount") && balanceEnquiryMap.get("Amount")!=null && !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("Amount")) && !"".equalsIgnoreCase((String) balanceEnquiryMap.get("Amount"))){
							Amount=(String) balanceEnquiryMap.get("Amount");
						}
						if(balanceEnquiryMap.containsKey("Expiry") && balanceEnquiryMap.get("Expiry")!=null && !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("Expiry")) && !"".equalsIgnoreCase((String) balanceEnquiryMap.get("Expiry"))){
							Expiry=(String)balanceEnquiryMap.get("Expiry");
						}
						if(balanceEnquiryMap.containsKey("CardType") && balanceEnquiryMap.get("CardType")!=null && !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("CardType")) && !"".equalsIgnoreCase((String) balanceEnquiryMap.get("CardType"))){
							CardType=(String) balanceEnquiryMap.get("CardType");
						}
						if(balanceEnquiryMap.containsKey("AcquirerId") && balanceEnquiryMap.get("AcquirerId")!=null && !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("AcquirerId")) && !"".equalsIgnoreCase((String) balanceEnquiryMap.get("AcquirerId"))){
							AcquirerId=(String) balanceEnquiryMap.get("AcquirerId");
						}
						if(balanceEnquiryMap.containsKey("ResponseCode") && balanceEnquiryMap.get("ResponseCode")!=null && !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseCode")) && !"".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseCode"))){
							ResponseCode=(String) balanceEnquiryMap.get("ResponseCode");
						}
						if(balanceEnquiryMap.containsKey("CardNumber") && balanceEnquiryMap.get("CardNumber")!=null && !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("CardNumber")) && !"".equalsIgnoreCase((String) balanceEnquiryMap.get("CardNumber"))){
							CardNumber=(String) balanceEnquiryMap.get("CardNumber");
						}
						
						if (ResponseCode.equalsIgnoreCase("10001")|| ResponseCode.equalsIgnoreCase("10027") || ResponseCode.equalsIgnoreCase("0")
								|| ResponseCode.equalsIgnoreCase("10029") || ResponseCode.equalsIgnoreCase("928") || ResponseCode.equalsIgnoreCase("10096")  || ResponseCode.equalsIgnoreCase("10121")) 
							{
							giftCard.setCardNumber(CardNumber);
							cargo.setGiftCard(giftCard);
							//String msg[] = new String[7];
							dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
							msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
							msg[1] = "GIFTCard" + " " +CardNumber;
							msg[2] = " Total Amount In This Card Is "+ Amount + " ";
							if (balanceEnquiryMap.get("Expiry") != null && !("null").equals(balanceEnquiryMap.get("Expiry")))
								msg[3] = "Expiry Date " +utilObj.calculateEYSDate(balanceEnquiryMap.get("Expiry").toString());
							else
								msg[3] = "Expiry Date "	+Expiry;
							if(ResponseCode.equalsIgnoreCase("10027") || ResponseCode.equalsIgnoreCase("10001") || 
									ResponseCode.equalsIgnoreCase("10029") || ResponseCode.equalsIgnoreCase("928") || ResponseCode.equalsIgnoreCase("10121") ){
								if(!ResponseCode.equalsIgnoreCase("928"))
								msg[4] = ResponseMessage;
								else
								msg[4] ="Invalid Card";	
								isValidCard=false;
								}
								else if(ResponseCode.equalsIgnoreCase("0"))
								{
								msg[4] = "CardType " + CardType;
								}
								else
								{
								msg[4] = ResponseMessage;
								if(ResponseCode.equalsIgnoreCase("10096"))
								isValidCard=false;
								}
							msg[5] = AcquirerId;
							msg[6] = "::Thanks::";
							dialogModel.setArgs(msg);
							if(isValidCard)
							{
							// dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
							dialogModel.setType(DialogScreensIfc.CONFIRMATION);
							dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Yes");
							dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Continue");
							}
							else{
								dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
								dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
							}
							ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

							}
						else 
						{
							//String msg[] = new String[7];
							dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
							msg[0] = " ";
							msg[1] = "Gift Card Balance Inquiry can not be completed";
							
							if(ResponseMessage!=null && !ResponseMessage.equalsIgnoreCase("") && !ResponseMessage.equalsIgnoreCase("N/A")){
								msg[2] = ResponseMessage;   //" Total Amount In This Card Is " + balanceEnquiryMap.get("Amount");
							}else{
								msg[2] = " Due to Network offline";   //" Total Amount In This Card Is " + balanceEnquiryMap.get("Amount");
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

						}
							
						}
						/*if (balanceEnquiryMap.get("Amount") != null) 
							}
				giftCard.setCardNumber(cardNumber);
				cargo.setGiftCard(giftCard);
				// DialogBeanModel dialogModel = new DialogBeanModel();
				String msg[] = new String[7];
				dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
				if(balanceEnquiryMap.get("CardCurrencySymbol")!=null)
				{
				msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
				msg[1] = "GIFTCard" + " " + giftCard.getCardNumber();
				
				msg[2] = " Total Amount In This Card Is " + balanceEnquiryMap.get("CardCurrencySymbol").toString()+ " "+ balanceEnquiryMap.get("Amount").toString() + " ";
			
				if(!(balanceEnquiryMap.get("Expiry")).equals(null) || (balanceEnquiryMap.get("Expiry") != null))		
				msg[3] = "ExpiryDate " + utilObj.calculateEYSDate(balanceEnquiryMap.get("Expiry").toString());
				else
					msg[3] = "ExpiryDate ";	
				EYSDate mydate=DomainGateway.getFactory().getEYSDateInstance();
				EYSDate expDate=utilObj.calculateEYSDate(balanceEnquiryMap.get("Expiry").toString());
				if(mydate.after(expDate) )
				{
					msg[4] = "Gift card number entered is Expired";
					isValidCard=false;

				}
				else{
				msg[4] = "Press Yes  To Print the Recipt else No to Return ";
				}
				if(balanceEnquiryMap.get("CardType") !=null)
				{
				msg[5] = balanceEnquiryMap.get("CardType").toString();
				}
				else{
					msg[5]="Press Enter button to return to previous screen ";
				}
				msg[6] = "::Thanks::";
				}
				else
				{
					msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
					msg[1] = "GIFTCard" + " " + giftCard.getCardNumber();
					
					msg[2] ="ExpiryDate " + utilObj.calculateEYSDate(balanceEnquiryMap.get("Expiry").toString());;
					
					msg[3] = "";
					msg[4]="The  Card is Deactive";
					isValidCard=false;
					
				}
				dialogModel.setArgs(msg);
				if(isValidCard)
				{
				// dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
				dialogModel.setType(DialogScreensIfc.CONFIRMATION);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Yes");
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Continue");
				}
				else{
					dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
				}
				// dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
				// "Continue");
				// POSUIManagerIfc ui = (POSUIManagerIfc)
				// bus.getManager(UIManagerIfc.TYPE);

				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

			}*/
			else if (balanceEnquiryMap != null && balanceEnquiryMap.size() != 0 && ("0").equals(balanceEnquiryMap.get("ResponseCode").toString())) {
				//String responseCode = balanceEnquiryMap.get("ResponseCode").toString();
				//String responseMessage = balanceEnquiryMap.get("ResponseMessage").toString();
				//String invoiceNumber = balanceEnquiryMap.get("InvoiceNumber").toString();
				//String acquirerId = balanceEnquiryMap.get("AcquirerId").toString();
				//String expiryDate = balanceEnquiryMap.get("Expiry").toString();
				String amount = balanceEnquiryMap.get("Amount").toString();
				//String cardType = balanceEnquiryMap.get("CardType").toString();

				if (giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_RELOAD) {
					amount = giftCard.getCurrentBalance().toString(); // Reload
																		// Amount
				}
				CurrencyIfc amt = DomainGateway.getBaseCurrencyInstance(amount);
				giftCard.setInitialBalance(amt);
				giftCard.setCurrentBalance(amt);

			}
			else if (balanceEnquiryMap.get("Amount") == null && balanceEnquiryMap.get("TerminalId")==null && balanceEnquiryMap.size()<=3 ) { //Rev 1.1 changes
				// DialogBeanModel dialogModel = new DialogBeanModel();
				giftCard.setCardNumber(cardNumber);  //Rev 1.2 changes
				//String msg[] = new String[7];
				dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
				msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
				msg[1] = "GIFTCard" + " " + giftCard.getCardNumber();
				msg[2] ="";// " Total Amount In This Card Is " + balanceEnquiryMap.get("Amount");
				msg[3] = "";//"ExpiryDate : N/A ";
				msg[4] = balanceEnquiryMap.get("ResponseMessage").toString();
				if (balanceEnquiryMap.get("AcquirerId") != null)
					msg[5] = balanceEnquiryMap.get("AcquirerId").toString();
				else {
					msg[5] = "Could Not Validate Card";
				}
				msg[6] = "::Thanks::";
				dialogModel.setArgs(msg);
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");

				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

			}
			/**
			*  Rev 1.1 changes
			**/
		
			else if (balanceEnquiryMap.get("Amount") == null && balanceEnquiryMap.get("TerminalId")==null ) {
				// DialogBeanModel dialogModel = new DialogBeanModel();
				//String msg[] = new String[7];
				dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
				msg[0] = " ";
				msg[1] = "Gift Card Balance Inquiry can not be completed";
	/*			msg[2] = " due to network offline";*/
				if(ResponseMessage!=null && !ResponseMessage.equalsIgnoreCase("") && !ResponseMessage.equalsIgnoreCase("N/A")){
					msg[2] = ResponseMessage;   //" Total Amount In This Card Is " + balanceEnquiryMap.get("Amount");
				}else{
					msg[2] = " Due to Network offline";   //" Total Amount In This Card Is " + balanceEnquiryMap.get("Amount");
				}
				//" Total Amount In This Card Is " + balanceEnquiryMap.get("Amount");
				msg[3] = "Press Enter button to return to previous screen";
				msg[4] = " ";
				msg[5] = " ";
				msg[6] = " ";
				msg[6] = "::Thanks::";
				dialogModel.setArgs(msg);
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");

				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

			}
		}catch(Exception e){ //Rev 1.3 changes
			utilObj.showQCOfflineErrorBox(bus);
		}} else {
			utilObj.showQCOfflineErrorBox(bus);
		}

	}

}