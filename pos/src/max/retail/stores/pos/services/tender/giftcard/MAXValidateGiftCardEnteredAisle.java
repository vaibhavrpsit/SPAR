/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
    Rev 1.5  11/09/2018     Bhanu Priya Gupta    Changes starts for code merging
   `Rev 1.4  20/jan/2016    Bhanu Priya Gupta , Fixed a Bug 16631
    Rev 1.3  14/08/2013     Jyoti Rawal, Fix for Bug 7692 - MAX: POS: Application crash on selecting Tender type Gift Card, APIs are not configured.
    Rev 1.2  08/08/2013     Jyoti Rawal, Changed the Gift Card Tender flow
   Rev 1.1 05/06/2013       Jyoti  Multiple Tender lines are getting added for single GC in same transaction
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.tender.giftcard;

import java.util.HashMap;

import max.retail.stores.domain.manager.tenderauth.MAXTenderAuthConstantsIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.utility.MAXGiftCard;
import max.retail.stores.domain.utility.MAXGiftCardIfc;
import max.retail.stores.pos.ado.transaction.MAXPaymentTransactionADO;
import max.retail.stores.pos.ado.transaction.MAXSaleReturnTransactionADO;
import max.retail.stores.pos.services.giftcard.MAXGiftCardUtilities;
import max.retail.stores.pos.services.qc.MAXGiftCardUtilitiesQC;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.tender.TenderGiftCard;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderGiftCardADO;
import oracle.retail.stores.pos.ado.transaction.LayawayTransactionADO;
import oracle.retail.stores.pos.ado.transaction.OrderTransactionADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

import com.qwikcilver.clientapi.svpos.GCPOS;

public class MAXValidateGiftCardEnteredAisle extends PosLaneActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7926925928495763969L;

	protected GiftCardIfc giftCard = null;

	/**
	 * the financial data for the register
	 */
	protected RegisterIfc register;

	/**
	 * The financial data for the store
	 */
	protected StoreStatusIfc storeStatus;

	protected RegisterIfc registerID;

	public void traverse(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel model = (POSBaseBeanModel) ui.getModel();
		PromptAndResponseModel parModel = model.getPromptAndResponseModel();
		boolean isSwiped = false;
		boolean isScanned= false;
		
		
		MAXGiftCardUtilitiesQC utilObj = new MAXGiftCardUtilitiesQC();
		GCPOS pos = utilObj.getInstance();
		String trackData = "";
		boolean isRefund = false;
// Changes starts for code merging(commenting below line as per MAX)
		//cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, TenderLineItemIfc.ENTRY_METHOD_MAGSWIPE);
		cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Swipe);
// Changes ends for code merging		

		if (parModel != null) {

			if (parModel.isSwiped()) {
				// Put the model in tenderAttributes
				cargo.getTenderAttributes().put(TenderConstants.MSR_MODEL, parModel.getMSRModel());
				cargo.getTenderAttributes().put(TenderConstants.NUMBER, ui.getInput());
// Changes starts for code merging(commenting below line as per MAX)			
				//cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, TenderLineItemIfc.ENTRY_METHOD_MAGSWIPE);
				cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Swipe);
//Changes ends for code merging
			} else if (parModel.isScanned()) {
				cargo.getTenderAttributes().put(TenderConstants.NUMBER, ui.getInput());
// Changes starts for code merging(commenting below line as per MAX)	
				//cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, TenderLineItemIfc.ENTRY_METHOD_AUTO);
				cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Automatic);
//Changes ends for code merging
			} else if (cargo.getPreTenderMSRModel() == null) {
				// if manually entered, we only have the card number.
				cargo.getTenderAttributes().put(TenderConstants.NUMBER, ui.getInput());
				//cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, TenderLineItemIfc.ENTRY_METHOD_MANUAL);
				// remove MSR (may have been previously swiped)
				cargo.getTenderAttributes().remove(TenderConstants.MSR_MODEL);
			} else {
				MSRModel msr = (MSRModel) cargo.getTenderAttributes().get(TenderConstants.MSR_MODEL);
				cargo.getTenderAttributes().put(TenderConstants.NUMBER, msr.getAccountNumber());
			}
			//Rev 1.1 changes 
			MAXGiftCardIfc giftCardTend = (MAXGiftCardIfc) DomainGateway.getFactory().getGiftCardInstance();
			String prevCardNumber = "";
			if(giftCard!=null)
			prevCardNumber = giftCard.getCardNumber();
			// Gift Card not getting accepted at QC after swiping
			String cardNumber = null;
			String cardNum = cargo.getTenderAttributes().get(TenderConstants.NUMBER).toString();

			String var1 = ";";
			String var2 = "=";
			String var3 = "?";

			HashMap ref = cargo.getTenderAttributes();
			boolean duplicate = false;
			if (ref != null && cargo.getTenderAttributes().get("AMOUNT").toString().indexOf("-") != -1)
				isRefund = true;

			if(cardNum != null && cardNum.length()!= 0)
			{
				if(cardNum.length() > 28)
					isSwiped = true;
				else if(cardNum.length() == 26)
					isScanned = true;
			}
			if(isScanned)
			{
				var1 = "";
				var2 = "";
				var3 = "";
				trackData = cardNum;
				cardNumber = utilObj.getCardNumberFromTrackData(ui.getInput(),true);
				giftCardTend.setTrackData(trackData);
				giftCardTend.setCardNumber(cardNumber);
			}
			else if(isSwiped)
			{
				trackData = var1 + cardNum.substring(0, 16) + var2 + cardNum.substring(16) + var3;
				cardNumber = utilObj.getCardNumberFromTrackData(trackData,true);
				giftCardTend.setTrackData(trackData);
				giftCardTend.setCardNumber(cardNumber);
			}
			else
			{
				cardNumber = cardNum;
				giftCardTend.setCardNumber(cardNumber);
			}
			//Rev 1.1 changes 
			if ((cardNum.length() > 16 && isSwiped)||( cardNum.length() > 16 && isScanned)){
				trackData = var1 + cardNum.substring(0, 16) + var2 + cardNum.substring(16) + var3;
				//cardNumber = cardNum.substring(0, 16);
				//isSwiped = true;

			} else if (!isRefund && cardNum.length() <= 16) {
				//cardNumber = cardNum;
				//ispinEntryRequired = true;
			} else
				cardNumber = cardNum;

			// parModel.getPromptAndResponseModel().getMSRModel();
			/*
			 * if(cargo.getTenderAttributes().get(TenderConstants.NUMBER).toString
			 * ().length()> 16){ cardNumber = cardNum.substring(0,16); }else{
			 * cardNumber = cardNum; }
			 */
			if (isSwiped) {// Gaurav
				giftCardTend.setSwiped(true);
				// giftCard.setTrackData(trackData);
			}
			//Rev 1.2 changes
			if(isScanned){
				giftCardTend.setScanned(true);
			}

			//giftCard.setCardNumber(cardNumber);
			// Gift Card not getting accepted at QC after swiping ends here
			RetailTransactionADOIfc retailTransaction = cargo.getCurrentTransactionADO();//getTenderableTransaction();
			giftCardTend.setRequestType(GiftCardIfc.GIFT_CARD_INQUIRY);
			cargo.setGiftCard(giftCardTend);

			// This was added to force processing past the off-line condition
			// when performing
			// an inquiry during tendering. The current tender amount is being
			// set on the available
			giftCardTend.setInquireAmountForTender(true);
			//String tenderAmount = cargo.getTenderAttributes().get(TenderConstants.AMOUNT).toString();
			String tenderAmount ="0.00";
			giftCardTend.setBalanceForInquiryFailure(DomainGateway.getBaseCurrencyInstance(tenderAmount));
			
			// Changes starts for code merging
			if ((cardNum.length() > 16 && isSwiped)||( cardNum.length() > 16 && isScanned)){
				trackData = var1 + cardNum.substring(0, 16) + var2 + cardNum.substring(16) + var3;
			}
			else  {
				cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Manual);
				cardNumber = cardNum;
				PromptAndResponseModel bean=new PromptAndResponseModel() ;
				model.setPromptAndResponseModel(bean);
				ui.showScreen("GIFT_CARD_PIN", model);
				return;
			}
			// Changes starts for code merging
		}

		// set pre tender msr model to null
		// we dont want to come back into credit/debit automatically if we have
		// problems.
		cargo.setPreTenderMSRModel(null);

		// TenderCargo cargo = (TenderCargo) bus.getCargo();
		giftCard = cargo.getGiftCard();
		registerID = cargo.getRegister();
        model.getPromptAndResponseModel().setResponseEditable(false);
		ui.showScreen(MAXPOSUIManagerIfc.GIFT_CARD_VALIDATING_SCREEN, model);
		// ui.showScreen(MAXPOSUIManagerIfc.EDC_VOID_SCREEN, model);

		boolean duplicate = false;
		String cardNumber = giftCard.getCardNumber();
		TenderGiftCard tgc = null;
		// MGSaleReturnTransactionADO ado = (MGSaleReturnTransactionADO)
		// cargo.getCurrentTransactionADO();
		// ((MGSaleReturnTransactionADO)
		// cargo.getCurrentTransactionADO()).getTenderLineItems(TenderLineItemCategoryEnum.AUTH_PENDING);

		TenderADOIfc[] tdc = null;
		if(cargo.getCurrentTransactionADO() instanceof MAXSaleReturnTransactionADO)
			tdc = ((MAXSaleReturnTransactionADO) cargo.getCurrentTransactionADO()).getTenderLineItems(TenderLineItemCategoryEnum.AUTH_PENDING);
		else if(cargo.getCurrentTransactionADO() instanceof LayawayTransactionADO)
			tdc = ((LayawayTransactionADO) cargo.getCurrentTransactionADO()).getTenderLineItems(TenderLineItemCategoryEnum.AUTH_PENDING);
		// Rev 1.4 changes Start
		else if(cargo.getCurrentTransactionADO() instanceof MAXPaymentTransactionADO)
			tdc = ((MAXPaymentTransactionADO) cargo.getCurrentTransactionADO()).getTenderLineItems(TenderLineItemCategoryEnum.AUTH_PENDING);
		// Rev 1.4 changes End
		//Rev 1.1 changes start
		else if(cargo.getCurrentTransactionADO() instanceof OrderTransactionADO){
			tdc = ((OrderTransactionADO) cargo.getCurrentTransactionADO()).getTenderLineItems(TenderLineItemCategoryEnum.AUTH_PENDING);
		}
		//Rev 1.1 changes end
		for (int k = 0; k < tdc.length; k++) {
			// TenderGiftCardADO tgc = (TenderGiftCardADO)tdc[k];
		 
			if(tdc[k] instanceof TenderGiftCardADO)
			tgc = (TenderGiftCard) ((TenderGiftCardADO) tdc[k]).toLegacy();
			//Rev 1.1 changes start here
			String actualCardNString = "";
			//Rev 1.3 change
			if (tgc != null && tgc.getCardNumber() != null && cardNumber != null){
				if(tgc.getCardNumber().length()>16){
					 actualCardNString = tgc.getCardNumber().substring(0, 16);
				}else{
					actualCardNString = tgc.getCardNumber();
				}
				if (cardNumber.equals(actualCardNString)) {
					duplicate = true;
				}
				}
			//Rev 1.1 changes end here

		}
		if (duplicate) {
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, MAXGiftCardUtilities.createDuplicateGiftCardNumErrorDialogModel());
			return;
		}

		//MGGiftCardUtilitiesQC utilObj = new MGGiftCardUtilitiesQC();
		//GCPOS pos = utilObj.getInstance();
		String denominationAmount = giftCard.getInitialBalance().toString();
		//denominationAmount="200";
		HashMap balanceEnquiryMap = null;
		if(pos!=null){
		if (!isSwiped && !isScanned)
			balanceEnquiryMap = utilObj.balanceEnquiryForRecipt(pos, giftCard.getCardNumber(), denominationAmount, "BLC");
		else {
			balanceEnquiryMap = utilObj.balanceEnquiry(pos, giftCard.getCardNumber(), denominationAmount, "BLC", trackData);
			// balanceEnquiryMap = utilObj.balanceEnquiryUsingTrackData(pos,
			// giftCard.getCardNumber(), denominationAmount, "BLC", trackData);
		}
			String CardNumber="N/A";
			String ResponseMessage="N/A";
			String ResponseCode="N/A";		
			String CardCurrencySymbol="N/A";
			String Amount="N/A";
			String Expiry="N/A";
			String CardType="N/A";
			String AcquirerId="Could Not Validate Card";
			
			
			
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
					DialogBeanModel dialogModel = new DialogBeanModel();
					if (ResponseCode.equalsIgnoreCase("928")|| ResponseCode.equalsIgnoreCase("10001") || ResponseCode.equalsIgnoreCase("0") 
							|| ResponseCode.equalsIgnoreCase("10029")  || ResponseCode.equalsIgnoreCase("10027")  
							|| ResponseCode.equalsIgnoreCase("10004") || ResponseCode.equalsIgnoreCase("10096") || ResponseCode.equalsIgnoreCase("21079") || ResponseCode.equalsIgnoreCase("10121")) {
						String msg[] = new String[7];
						dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
						msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
						msg[1] = "GIFTCard" + " " + CardNumber;
						if (balanceEnquiryMap.get("Amount") != null && balanceEnquiryMap.get("CardCurrencySymbol") != null){
							if(!ResponseCode.equalsIgnoreCase("0") && !ResponseCode.equalsIgnoreCase("10029"))
							{
								if(CardCurrencySymbol!=null && !CardCurrencySymbol.equalsIgnoreCase("N/A"))
								msg[2] = " Total Amount In This Card Is " + CardCurrencySymbol + " " + Amount+ ". "+ ResponseMessage;
								else
								msg[2] = " Total Amount In This Card Is " + Amount+ ". "+ ResponseMessage;
							}
							else
							{
								if(CardCurrencySymbol!=null && !CardCurrencySymbol.equalsIgnoreCase("N/A"))
								msg[2] = " Total Amount In This Card Is " + CardCurrencySymbol + " " + Amount;
								else
								msg[2] = " Total Amount In This Card Is " + Amount;
							}
							}	
						else {
							if(!ResponseCode.equalsIgnoreCase("10027") && !ResponseCode.equalsIgnoreCase("10004") && !ResponseCode.equalsIgnoreCase("10096"))
							{
							msg[2] = " Total Amount In This Card Is " + CardCurrencySymbol + " " + Amount+ ". "+ ResponseMessage;
							}
							else
							{
								if(!ResponseCode.equalsIgnoreCase("10004") && !ResponseCode.equalsIgnoreCase("10096"))
								msg[2] = " Total Amount In This Card Is " + " " + Amount+ ". "+ ResponseMessage;
								else
								msg[2] = " Total Amount In This Card Is " + " " + Amount;	
							}
							cargo.getGiftCard().setRequestType(MAXTenderAuthConstantsIfc.ACTIVATE);
						}
						if (balanceEnquiryMap.get("Expiry") != null && !("null").equals(balanceEnquiryMap.get("Expiry")))
						{
							msg[3] = "Expiry Date " +utilObj.calculateEYSDate(balanceEnquiryMap.get("Expiry").toString());
						}
						else
						{
							if(!ResponseCode.equalsIgnoreCase("10004"))
							msg[3] = "Expiry Date "	+Expiry+ ". "+ResponseMessage;
							else
								msg[3] = "Expiry Date "	+Expiry+ ". " + " Invalid Card";
						}
						
						if(("0.00").equals((balanceEnquiryMap.get("Amount"))))
							msg[4] = "Choose option to proceed";
						else if((("0").equals(balanceEnquiryMap.get("ResponseCode").toString())))
							msg[4] = "Press YES To Proceed : NO To Use another Tender";
						else
							msg[4] = "Press button To Proceed.";
							msg[5] = CardType;
						msg[6] = "::Thanks::";
						dialogModel.setArgs(msg);
						
						if(("0.00").equals((balanceEnquiryMap.get("Amount"))))
						{   dialogModel.setResourceID("GiftCardBalance");
							dialogModel.setType(DialogScreensIfc.ERROR);
							dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
							ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
							return;
						}
						
						if(ResponseCode.equalsIgnoreCase("928")|| ResponseCode.equalsIgnoreCase("10001") || ResponseCode.equalsIgnoreCase("10029")  
								|| ResponseCode.equalsIgnoreCase("10027")  || ResponseCode.equalsIgnoreCase("10004")  || ResponseCode.equalsIgnoreCase("10096") || ResponseCode.equalsIgnoreCase("10121") || ResponseCode.equalsIgnoreCase("21079"))
						{
							dialogModel.setType(DialogScreensIfc.ERROR);
							dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
							
						}
						else
						{
						dialogModel.setType(DialogScreensIfc.CONFIRMATION);
						if(("0").equals(balanceEnquiryMap.get("ResponseCode").toString()))
						{
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "AfterBalance");
						}
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Undo");
						}
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					}
					else
						utilObj.showQCOfflineErrorBox(bus);
					if (!(("0.00").equals((balanceEnquiryMap.get("Amount")))) &&  (balanceEnquiryMap != null && balanceEnquiryMap.size() != 0 && ("0").equals(balanceEnquiryMap.get("ResponseCode").toString()))) {
				String amount = balanceEnquiryMap.get("Amount").toString();
				
				//CurrencyIfc amt = DomainGateway.getBaseCurrencyInstance("-17");
				//cargo.getTransaction().setTenderTransactionTotals().setBalanceDue();
				
			//	CurrencyIfc cardRedeemAmount =amt.subtract(arg0);
	            if(giftCard instanceof MAXGiftCard)
	            {
	            	((MAXGiftCard) giftCard).setTrackData(trackData);
	            }
	            
	        	CurrencyIfc giftcardamt = DomainGateway.getBaseCurrencyInstance(amount);
	        	String tenderAmount = cargo.getTenderAttributes().get(TenderConstants.AMOUNT).toString();
	      	    CurrencyIfc amtran =DomainGateway.getBaseCurrencyInstance(tenderAmount) ;
				giftCard.setInitialBalance(giftcardamt);//amtn
				//double  trans_amt = Double.parseDouble(tenderAmount);
				//double  gift_amt = Double.parseDouble(amount);
				HashMap tenderAttributes = cargo.getTenderAttributes();
				
				//tenderAttributes.put(TenderConstants.AMOUNT, amount);
				//mk change
				
			//	CurrencyIfc Amount1 = amtn.subtract(amt);
				
				//BigDecimal transamt=new BigDecimal(tenderAmount);
				//BigDecimal giftamt=new BigDecimal(amount);
			//	if(giftamt.compareTo(transamt) < 0)
				if(giftcardamt.compareTo(amtran) < 0)
				{
				tenderAttributes.put(TenderConstants.AMOUNT, giftcardamt.toString());
				}
				else
				{
					tenderAttributes.put(TenderConstants.AMOUNT, amtran.toString());
				}
				cargo.setTenderAttributes(tenderAttributes);
			//	if(foo < 200){
				giftCard.setCurrentBalance(giftcardamt);
				//}
				if(cargo.getCurrentTransactionADO() instanceof MAXSaleReturnTransactionADO){
					 TenderableTransactionIfc txnRDO = (TenderableTransactionIfc) ((ADO) cargo.getCurrentTransactionADO()).toLegacy();
					
					 
				((MAXSaleReturnTransaction)txnRDO).setFatalDeviceCall(false);
				}
					}
			}
			
		// if(balanceEnquiryMap.get("Amount")!= null &&
		// !("0.00").equals(balanceEnquiryMap.get("Amount").toString()))
		/*if ((("0").equals(balanceEnquiryMap.get("ResponseCode").toString())) || ((balanceEnquiryMap.get("Amount") != null) || (isRefund))) {
			DialogBeanModel dialogModel = new DialogBeanModel();
			String msg[] = new String[7];
			dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
			msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
			msg[1] = "GIFTCard" + " " + giftCard.getCardNumber();
			if (balanceEnquiryMap.get("Amount") != null && balanceEnquiryMap.get("CardCurrencySymbol") != null)
				msg[2] = " Total Amount In This Card Is " + balanceEnquiryMap.get("CardCurrencySymbol").toString() + " " + balanceEnquiryMap.get("Amount").toString() + " ";
						
			else {
				msg[2] = balanceEnquiryMap.get("ResponseMessage").toString();
				cargo.getGiftCard().setRequestType(TenderAuthConstantsIfc.ACTIVATE);
			}
			if ((!("null").equals(balanceEnquiryMap.get("Expiry")))&& (balanceEnquiryMap.get("Expiry") != null))
				msg[3] = "ExpiryDate " + utilObj.calculateEYSDate(balanceEnquiryMap.get("Expiry").toString());
			else
				msg[3] = "";
			
			if(("0.00").equals((balanceEnquiryMap.get("Amount"))))
				msg[4] = "Choose option to proceed";
			else if((("0").equals(balanceEnquiryMap.get("ResponseCode").toString())))
				msg[4] = "Press YES To Proceed : NO To Use another Tender";
			else
				msg[4] = "Press button To Proceed.";
			if (balanceEnquiryMap.get("CardType") != null)
				msg[5] = balanceEnquiryMap.get("CardType").toString();
			else
				msg[5] = "";
			msg[6] = "::Thanks::";
			dialogModel.setArgs(msg);
			
			if(("0.00").equals((balanceEnquiryMap.get("Amount"))))
			{
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
				
			}
			else
			{
			dialogModel.setType(DialogScreensIfc.CONFIRMATION);
			if(("0").equals(balanceEnquiryMap.get("ResponseCode").toString()))
			{
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "AfterBalance");
			}
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Undo");
			}
			
			
			// POSUIManagerIfc ui = (POSUIManagerIfc)
			// bus.getManager(UIManagerIfc.TYPE);

			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

		}
		else if (((balanceEnquiryMap.get("Amount") == null && !isRefund)) || !(("0").equals(balanceEnquiryMap.get("ResponseCode").toString()))) {
			DialogBeanModel dialogModel = new DialogBeanModel();
			String msg[] = new String[7];
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
			// dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES,
			// "AfterBalance");
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
			// POSUIManagerIfc ui = (POSUIManagerIfc)
			// bus.getManager(UIManagerIfc.TYPE);

			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

		}
			
		if (!(("0.00").equals((balanceEnquiryMap.get("Amount")))) &&  (balanceEnquiryMap != null && balanceEnquiryMap.size() != 0 && ("0").equals(balanceEnquiryMap.get("ResponseCode").toString()))) {
			// String responseCode =
			// balanceEnquiryMap.get("ResponseCode").toString();
			// String responseMessage =
			// balanceEnquiryMap.get("ResponseMessage").toString();
			// String invoiceNumber =
			// balanceEnquiryMap.get("InvoiceNumber").toString();
			// String acquirerId =
			// balanceEnquiryMap.get("AcquirerId").toString();
			// String expiryDate = balanceEnquiryMap.get("Expiry").toString();
			String amount = balanceEnquiryMap.get("Amount").toString();
			// String cardType = balanceEnquiryMap.get("CardType").toString();
		//	MAXGiftCardUtilities.netOffline = false;

			CurrencyIfc amt = DomainGateway.getBaseCurrencyInstance(amount);
            if(giftCard instanceof MAXGiftCard)
            {
            	((MAXGiftCard) giftCard).setTrackData(trackData);
            }
			giftCard.setInitialBalance(amt);
			giftCard.setCurrentBalance(amt);
			if(cargo.getCurrentTransactionADO() instanceof MAXSaleReturnTransactionADO){
				 TenderableTransactionIfc txnRDO = (TenderableTransactionIfc) ((ADO) cargo.getCurrentTransactionADO()).toLegacy();
				
				 
			((MAXSaleReturnTransaction)txnRDO).setFatalDeviceCall(false);
			}
		}*/

		if (!isRefund)
			giftCard.setRequestType(GiftCardIfc.GIFT_CARD_REDEEM);
		// ends
		cargo.setGiftCard(giftCard);
		cargo.setRegister(registerID);// gaurav
	
		//cargo.setTenderAttributes(cargo.getTransaction().get));

	}
		else {
			utilObj.showQCOfflineErrorBox(bus);
		}
}

}
