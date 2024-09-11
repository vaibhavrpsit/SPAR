/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*  Copyright (c) 2016 MAX Hyper Market Inc.    All Rights Reserved.
*
*	Rev	1.1 	06 Apr,2017	Nitesh Kumar		Layaway payment through Giftcard 
*	Rev	1.0 	03 Mar,2017	Hitesh.Dua			Gift Card redeem receipt print related changes 
*
********************************************************************************   
*   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*   
Rev 1.2  11/feb/2016	Akanksha Chauhan, Changes for Bug 16655   
Rev 1.1  11/feb/2016	Akanksha Chauhan, Changes for Bug 16654   
Rev 1.0  2/feb/2016	Akanksha Chauhan, Changes for giftcard offline
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.giftcard;

import java.util.HashMap;

import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.utility.MAXGiftCardIfc;
import max.retail.stores.pos.ado.transaction.MAXLayawayTransactionADO;
import max.retail.stores.pos.ado.transaction.MAXPaymentTransactionADO;
import max.retail.stores.pos.ado.transaction.MAXSaleReturnTransactionADO;
import max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc;
import max.retail.stores.pos.receipt.MAXReceiptTypeConstantsIfc;
import max.retail.stores.pos.services.qc.MAXGiftCardUtilitiesQC;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransaction;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

import com.qwikcilver.clientapi.svpos.GCPOS;

public class MAXGiftCardRedeemAisle extends PosLaneActionAdapter{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8902085315548179621L;

	public void traverse(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel model = (POSBaseBeanModel) ui.getModel();
		PromptAndResponseModel parModel = model.getPromptAndResponseModel();
		MAXGiftCardIfc giftCard = null;
		String actionLetter = "Redeem";
		boolean isSwiped = false;
		boolean isScanned= false;
		String trackData = "";
		String cardPin="";
		if (parModel != null) {

			
			giftCard = (MAXGiftCardIfc) cargo.getGiftCard();
			// Gift Card not getting accepted at QC after swiping

						
		}
		
		MAXGiftCardUtilitiesQC utilObj = new MAXGiftCardUtilitiesQC();
		GCPOS pos = utilObj.getInstance();
		//String denominationAmount = giftCard.getInitialBalance().toString();
		HashMap balanceEnquiryMap = null;
		String invoiceNumber = null;
		String totalAmount = null;
		if(cargo.getCurrentTransactionADO() instanceof MAXSaleReturnTransactionADO)
		{
			MAXSaleReturnTransaction trans = ((MAXSaleReturnTransaction)(cargo.getCurrentTransactionADO().toLegacy()));
			invoiceNumber = (trans).getTransactionID();
			totalAmount = (trans).getTransactionTotals().getGrandTotal().toString();
		}
	else if(cargo.getCurrentTransactionADO() instanceof MAXLayawayTransactionADO)
		{
		LayawayTransactionIfc trans = ((LayawayTransactionIfc)(cargo.getCurrentTransactionADO().toLegacy()));
		invoiceNumber = (trans).getTransactionID();
		totalAmount = (trans).getTransactionTotals().getGrandTotal().toString();
		}
	//Changes for rev 1.1 start
	else if(cargo.getCurrentTransactionADO() instanceof MAXPaymentTransactionADO)
	{
		LayawayPaymentTransactionIfc trans = ((LayawayPaymentTransaction)(cargo.getCurrentTransactionADO().toLegacy()));
		invoiceNumber = (trans).getTransactionID();
		totalAmount = (trans).getTransactionTotals().getGrandTotal().toString();
	}
	//Changes for rev 1.1 ends	
//		String cardNumber = null;
		String cardNumber = giftCard.getCardNumber();
		cardPin=giftCard.getCardPin();
		trackData = giftCard.getTrackData();
		String var1 = ";";
		String var2 = "=";
		String var3 = "?";

		if(giftCard.isSwiped() == true){
			isSwiped = true;
		}
		
		if(giftCard.isScanned() == true){
			isScanned = true;
		}
//		if(cardNum != null && cardNum.length()!= 0)
//		{
//			if(cardNum.length() > 28)
//				isSwiped = true;
//			else if(cardNum.length() == 26)
//				isScanned = true;
//		}
//		if(isScanned)
//		{
//			trackData = cardNum;
//			cardNumber = utilObj.getCardNumberFromTrackData(ui.getInput(),true);
//	
//		}
//		else if(isSwiped)
//		{
//			trackData = var1 + cardNum.substring(0, 16) + var2 + cardNum.substring(16) + var3;
//			cardNumber = utilObj.getCardNumberFromTrackData(trackData,true);
//			
//		}
//		else
//		{
//			cardNumber = cardNum;
//			
//		}
		/*Rev 1.0 changes starts */
		//Rev 1.1 change start
		 boolean transactionReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
		  boolean traningMode = cargo.getRegister().getWorkstation().isTrainingMode(); 
		if(transactionReentryMode||traningMode){
			bus.mail(new Letter("AfterBalanceAndRedeem"), BusIfc.CURRENT);
			return;
		}
		//Rev 1.1 change End
		/*Rev 1.0 changes Ends */
		if(trackData != null && !trackData.equals("")){
		if (!isSwiped && !isScanned){
			balanceEnquiryMap = utilObj.redeemCard(pos,
					cardNumber, cargo.getTenderAttributes().get("AMOUNT").toString(), totalAmount,
					invoiceNumber);
		}
		else
			balanceEnquiryMap = utilObj
					.redeemCardUsingTrackData(pos, cardNumber,
							cargo.getTenderAttributes().get("AMOUNT").toString(), totalAmount, invoiceNumber,
							trackData);
		}else{
			balanceEnquiryMap = utilObj.redeemCardWithBillAmountNPin(pos, giftCard.getCardNumber(), cargo.getTenderAttributes().get("AMOUNT").toString(), 
					cargo.getCurrentTransactionADO().getTransactionID(), totalAmount, cardPin);	
		}
		
		if (balanceEnquiryMap != null && balanceEnquiryMap.size() != 0 && ("0").equals(balanceEnquiryMap.get("ResponseCode").toString())) {
			String expiryDate = balanceEnquiryMap.get("Expiry").toString();
			String peviousBalance = balanceEnquiryMap.get("PreviousBalance").toString();
			String amount = balanceEnquiryMap.get("Amount").toString();
			
			// String cardType = balanceEnquiryMap.get("CardType").toString();
			CurrencyIfc prevAmt = DomainGateway.getBaseCurrencyInstance(peviousBalance);
			CurrencyIfc amt = DomainGateway.getBaseCurrencyInstance(amount);
			cargo.getTenderAttributes().put(TenderConstants.NUMBER, giftCard.getCardNumber());
			giftCard.setInitialBalance(prevAmt);
			giftCard.setCurrentBalance(amt);
			giftCard.setQcApprovalCode(balanceEnquiryMap.get(
					"ApprovalCode").toString());
			giftCard.setQcCardType(balanceEnquiryMap.get("CardType")
					.toString());
			giftCard.setExpirationDate(calculateEYSDate(balanceEnquiryMap
					.get("Expiry").toString()));
			giftCard.setExpirationDate(calculateEYSDate(expiryDate));
			giftCard.setQcTransactionId(balanceEnquiryMap.get(
					"TransactionId").toString());
			giftCard.setQcBatchNumber((balanceEnquiryMap
					.get("CurrentBatchNumber").toString()));
			((MAXTenderCargo) cargo).setGiftCardApproved(true);
			if (balanceEnquiryMap.get("InvoiceNumber") != null
					&& !(("null").equals(balanceEnquiryMap
							.get("InvoiceNumber"))))
				giftCard.setQcInvoiceNumber(balanceEnquiryMap.get(
						"InvoiceNumber").toString());
			
//			DialogBeanModel dialogModel = new DialogBeanModel();
//			String msg[] = new String[7];
//			dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
//			msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
//			String cardNumTender = balanceEnquiryMap.get(
//					"CardNumber").toString();
//			String cardnumber = cardNumTender.substring(
//					cardNumTender.length() - 4,
//					cardNumTender.length());
//			msg[1] = "GIFTCard" + " " + cardnumber;
//			msg[2] = " Total Amount In This Card Is "
//					+ ""
//					+ balanceEnquiryMap.get(
//							"CardCurrencySymbol").toString()
//					+ balanceEnquiryMap.get("Amount")
//							.toString() + " ";
//
//			msg[3] = "ExpiryDate "
//					+ calculateEYSDate(balanceEnquiryMap.get(
//							"Expiry").toString());
//			msg[4] = "Request Successfull:  Press button To Proceed";
//			msg[5] = balanceEnquiryMap.get("CardType")
//					.toString();
//			msg[6] = "::Thanks::";
//			dialogModel.setArgs(msg);
//			dialogModel
//					.setType(DialogScreensIfc.CONFIRMATION);
//			dialogModel.setButtonLetter(
//					DialogScreensIfc.BUTTON_UPDATE, "AfterBalanceAndRedeem");
//			dialogModel.setButtonLetter(
//					DialogScreensIfc.BUTTON_NO, "Fa");
			
			bus.mail(new Letter("AfterBalanceAndRedeem"), BusIfc.CURRENT);
			
		}
		if (balanceEnquiryMap != null && balanceEnquiryMap.size() != 0 && ("0").equals(balanceEnquiryMap.get("ResponseCode").toString())) {
            
			//Changes for redeem amount.
			balanceEnquiryMap.put("RedeemedAmount", cargo.getTenderAttributes().get("AMOUNT").toString());
			//commented v12 receipt
			//utilObj.individualslipforeveryGiftcard(bus, balanceEnquiryMap, actionLetter);
			utilObj.SetValuesInGiftCard(giftCard, balanceEnquiryMap);
			//Print v14 receipt for gift card redeem 
			printGiftcardSlip(bus,giftCard.getRequestType(),giftCard);
			
		}
		/*akanksha offline*/
		if(balanceEnquiryMap.get("ResponseMessage") != null&& !balanceEnquiryMap.get("ResponseCode").equals("0")){
			// Changes start for merge build 
			if(trackData == null){
				DialogBeanModel dialogModel = new DialogBeanModel();
				String msg[] = new String[7];
				dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
				msg[0] =  balanceEnquiryMap.get("ResponseMessage").toString();
				msg[1] = " Press Enter to return Tender Options screen";
				
				msg[2]="";
				msg[3]="";
				msg[4]="";
				msg[5]="";
				msg[6]="";
				dialogModel.setArgs(msg);
				/*akanksha offline*/
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "offline");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				}else{
				
				utilObj.showQCOfflineErrorBox(bus);
				}
			// Changes ends for merge build
			
			
			//utilObj.showQCOfflineErrorBox(bus);
			return;
		}
		if((balanceEnquiryMap.get("Amount") == null) || !(("0").equals(balanceEnquiryMap.get("ResponseCode").toString()))) {
			DialogBeanModel dialogModel = new DialogBeanModel();
			String msg[] = new String[7];
			dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
			msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
			msg[1] = "GIFTCard" + " " + giftCard.getCardNumber();
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
			dialogModel.setType(DialogScreensIfc.ERROR);
			// dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES,
			// "AfterBalance");
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
			// POSUIManagerIfc ui = (POSUIManagerIfc)
			// bus.getManager(UIManagerIfc.TYPE);

			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

		}
		
	}
	
	public EYSDate calculateEYSDate(String strDate) {
		EYSDate expDate =null;
		try{
		expDate = DomainGateway.getFactory().getEYSDateInstance();
		expDate.initialize();
		expDate.setDay(Integer.parseInt(strDate.substring(0, 2)));
		expDate.setMonth(Integer.parseInt(strDate.substring(2, 4)));
		expDate.setYear(Integer.parseInt(strDate.substring(4, strDate.length())));
		}catch(Exception e){
			
		}
		return expDate;

	}

	//print redeem receipt as per 14
	public void printGiftcardSlip(BusIfc bus,int requestType,GiftCardIfc giftCard) {

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		try {
				//* Gift Card  redeem/reload/issue Slip *
				PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
				MAXReceiptParameterBeanIfc receipt=(MAXReceiptParameterBeanIfc) BeanLocator.getApplicationBean("application_ReceiptParameterBean");

				receipt.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
				receipt.setGiftCardRequestType(requestType);
				receipt.setGiftCard(giftCard);
				receipt.setDocumentType(MAXReceiptTypeConstantsIfc.GIFT_CARD_SLIP);

				pdm.printReceipt((SessionBusIfc)bus, receipt);

		} catch (PrintableDocumentException e) {
			logger.warn(e);
			ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
			DialogBeanModel dialogModel = new DialogBeanModel();
			String msg[] = new String[1];
			UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
			msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG, BundleConstantsIfc.PRINTER_OFFLINE);
			dialogModel.setResourceID("RetryCancel");
			dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
			dialogModel.setArgs(msg);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		
		}
		
	}

}
