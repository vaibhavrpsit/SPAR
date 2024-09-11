
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*  Copyright (c) 2016 MAX Hyper Market Inc.    All Rights Reserved.
*
*	Rev	1.0 	03Mar,2017	Hitesh.Dua			Gift Card receipt print related changes 
*
********************************************************************************   
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
    Rev 1.9  25/04/2016    Mohd Arif            Change to print last 4 digit of gift card with remaining (*) and reload amount.
    Rev 1.8  11/02/2016    Changed by   Akanksha Chauhan				 Bug ID 16655
    Rev 1.7  4/Feb/2016    Bhanu Priya Gupta , Changes for gift Receipt of Issue and Reload
    Rev 1.6  29/jan/2016    Akanksha Chauhan, Bug Fixed 16620    
    Rev 1.5  26/nov/2014    Rahul Yadav, Changed the Gift Card Receipt    
    Rev 1.4  08/08/2013     Jyoti Rawal, Changed the Gift Card Tender flow
    Rev 1.3  27/06/2013     Jyoti Rawal, Fix for Bug 6662 Special Order : POS Crashed while tendering using GC
    Rev 1.2   07/Jun/2013   Jyoti Rawal, Fix for Bug 6193 - GC Issue/Reload - Error Message is not proper while QC is Offline 
   	Rev 1.1  10/May/2013	Jyoti Rawal, Changes in Offline Scenerio as per Client 
  	Rev 1.0  25/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
 
package max.retail.stores.pos.services.sale.complete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.qwikcilver.clientapi.svpos.GCPOS;

import max.retail.stores.domain.stock.MAXGiftCardPLUItem;
import max.retail.stores.domain.utility.MAXGiftCard;
import max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc;
import max.retail.stores.pos.receipt.MAXReceiptTypeConstantsIfc;
import max.retail.stores.pos.services.qc.MAXGiftCardUtilitiesQC;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import max.retail.stores.pos.ui.beans.MAXGiftCardBeanModel;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItem;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;
//--------------------------------------------------------------------------
/**
 * This site Checks at least one gift card exists. If there is gift card this
 * mails gift card letter for ActivationStation. If no gift card exists this
 * mails continue letter for Printing Station.
 * <p>
 * 
 *
 */
// --------------------------------------------------------------------------
public class MAXCheckForGiftCardsSite extends PosSiteActionAdapter {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1325608987866758520L;
	/**
	 * revision number supplied by source-code-control system
	 */
	public static final String revisionNumber = "$Revision: 1.27 $";
	protected static final String Gift_Card_Tender_Slip  = "* Gift Card  Tender Slip * ";
	
	protected static final String UNDERLINE="________________________________________";
	protected static final int LINE_LENGTH_DEFAULT = 40;

	/**
	 * line length
	 */
	public static int LINE_LENGTH = LINE_LENGTH_DEFAULT;

	// ----------------------------------------------------------------------
	/**
	 * Checks for the ActivationStation/Printing Station.
	 * 
	 * @param bus
	 *            Service Bus
	 */
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {

		 giftCardList=new ArrayList<>();
		LetterIfc letter = new Letter(CommonLetterIfc.GIFTCARD);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		
		SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
	
		MAXGiftCardUtilitiesQC utilObj = new MAXGiftCardUtilitiesQC();
		GCPOS pos = utilObj.getInstance();
		boolean isSwiped = false;
		boolean isScanned = false;
       //Rev 1.8 start
		  boolean traningMode = cargo.getRegister().getWorkstation().isTrainingMode();
		  boolean transactionReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
		if(transactionReentryMode||traningMode){
			//letter = new Letter(CommonLetterIfc.CONTINUE);
		String letter1 ="TenderSale";
		bus.mail(new Letter(letter1), BusIfc.CURRENT);
			   return;
		}
		 //Rev 1.8 End
		String cardTrackData = ""; 
		//boolean transactionReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
		MAXGiftCardBeanModel model1 = null;
		GiftCardBeanModel Gmodel = null;
		try{
		model1 = (MAXGiftCardBeanModel) ui.getModel(POSUIManagerIfc.GET_CARD_NUM_FOR_GIFT_CARD); 
		if(model1.isSwiped())
			isSwiped = true;
		if(model1.isScanned())
			isScanned = true;
		}catch(Exception e){
						
		}
		// We must check for both Gift Card items and tenders
		RetailTransactionIfc retailTransaction = ((MAXSaleCargo) cargo).getRetailTransactionIfc();
		SaleReturnLineItemIfc[] items = retailTransaction.getProductGroupLineItems(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD);
		/**Changes done by Prateek For GC**/
		if(cargo instanceof MAXSaleCargo){
			if(((MAXSaleCargo)cargo).getIndex() == -1)
				((MAXSaleCargo)cargo).setIndex(0);
			if(((MAXSaleCargo)cargo).getLength() == -1)
				if(items!=null && items.length >-1){
				((MAXSaleCargo)cargo).setLength(items.length);
				}                                          
		}
		
		TenderLineItemIfc[] tenders = retailTransaction.getTenderLineItems();
		boolean hasGiftCardTender = false;
		Vector giftCardTenderArray = new Vector();
		Vector failedgiftCardTenderArray = new Vector();
		Vector failedgiftCardIssueArray = new Vector();  //rev 1.2 changes
		
		Vector failedgiftCardMessageArray = new Vector();  //rev 1.2 changes
		
		//change start for enter bill amount parameter in redeem function as mail in q.c
		TransactionTotalsIfc transactionTotals = cargo.getTransaction().getTransactionTotals();
		String totalAmount = transactionTotals.getSubtotal().toString();
		//change End for enter bill amount parameter in redeem function as mail in q.c
		//for (int i = 0; i < tenders.length; i++) {
	/*	//Rev 1.4 changes
			// Rev 1.6 changes Starts 
			if (tenders[i].getTypeCode() == TenderLineItemIfc.TENDER_TYPE_GIFT_CARD) {
				//String cardNum = ((TenderGiftCard) tenders[i]).getCardNumber();
				String cardNumber = null;
				String var1 = ";";
				String var2 = "=";
				String var3 = "?";
				String trackData = "";
				boolean isSwiped1 = false;
				//hasGiftCardTender = true;
				String redeemAmount = tenders[i].getAmountTender().toString();
				String trackdataFromTender = ((TenderGiftCard) tenders[i]).getCardType();
				TenderGiftCard tgf = (TenderGiftCard) tenders[i];
				//changes start for issue if 26 digit barcode scan ,trackto data for 26 digit barcode to be passed
//				if(cardNum.length()> 16){
				if(((TenderGiftCard) tenders[i]).getEntryMethod().equalsIgnoreCase("Swipe")){
					//TrackData = var1+cardNum.substring(0,16)+var2+cardNum.substring(16)+var3;
					cardTrackData = var1+trackdataFromTender.substring(0,16)+var2+trackdataFromTender.substring(16)+var3;
					//cardNumber = cardNum.substring(0,16);
					if(trackdataFromTender.length()==26){
						cardNumber= utilObj.getCardNumberFromTrackData(trackdataFromTender,true);
						trackData=trackdataFromTender;
						((TenderGiftCard) tenders[i]).setCardNumber(cardNumber); //code for issue Card Number was wrong in DB when scanned 26 digit barcode (it took 1st 16 digit) 
						
					}else{
						cardNumber = trackdataFromTender.substring(0,16);
						trackData= cardTrackData;
					}
					//change End 
				isSwiped1 = true;
					
				}else{
					cardNumber = trackdataFromTender;
				}
				//String cardNumber = ((TenderGiftCard) tenders[i]).getCardNumber();
				POSBaseBeanModel model = new POSBaseBeanModel();
				ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
				if(cargo.getTransaction() instanceof MAXSaleReturnTransaction)
				{
					if (((MAXSaleReturnTransaction) cargo.getTransaction()).cardNumAck1 != null
							&& (((MAXSaleReturnTransaction) cargo
									.getTransaction()).cardNumAck1
									.get(cardNumber) != null)
							&& (((MAXSaleReturnTransaction) cargo
									.getTransaction()).cardNumAck1.get(
									cardNumber).toString()
									.equalsIgnoreCase("Sucess"))) {

						letter = new Letter(CommonLetterIfc.CONTINUE);
						bus.mail(letter, BusIfc.CURRENT);
					}
					// else if(MAXOrderTransaction(cargo.getTransaction()))
					// if(flag = true)
					else {
						ui.showScreen(
								MAXPOSUIManagerIfc.GIFT_CARD_VALIDATING_SCREEN,
								model);

						HashMap balanceEnquiryMap = null;
						// change start for enter bill amount parameter in
						// redeem function as mail in q.c
						if (!isSwiped1)
							balanceEnquiryMap = utilObj.redeemCard(pos,
									cardNumber, redeemAmount, totalAmount,
									cargo.getTransaction().getTransactionID());
						else
							balanceEnquiryMap = utilObj
									.redeemCardUsingTrackData(pos, cardNumber,
											redeemAmount, totalAmount, cargo
													.getTransaction()
													.getTransactionID(),
											trackData);
						// printChargeSlipData(bus, balanceEnquiryMap,
						// "REDEEM");
						individualslipforeveryGiftcard(bus, balanceEnquiryMap,
								"REDEEM");

						HashMap cardNumAck = new HashMap();
						MAXGiftCardIfc gc = (MAXGiftCardIfc) tgf.getGiftCard();
						if (balanceEnquiryMap.get("Amount") != null
								&& balanceEnquiryMap != null
								&& balanceEnquiryMap.size() != 0
								&& ("0").equals(balanceEnquiryMap.get(
										"ResponseCode").toString())) {
							gc = (MAXGiftCardIfc) tgf.getGiftCard();
							gc.setQcApprovalCode(balanceEnquiryMap.get(
									"ApprovalCode").toString());
							gc.setQcCardType(balanceEnquiryMap.get("CardType")
									.toString());
							gc.setExpirationDate(calculateEYSDate(balanceEnquiryMap
									.get("Expiry").toString()));
							gc.setQcTransactionId(balanceEnquiryMap.get(
									"TransactionId").toString());
							gc.setQcBatchNumber((balanceEnquiryMap
									.get("CurrentBatchNumber").toString()));
							cargo.setGiftCardApproved(true);
							if (balanceEnquiryMap.get("InvoiceNumber") != null
									&& !(("null").equals(balanceEnquiryMap
											.get("InvoiceNumber"))))
								gc.setQcInvoiceNumber(balanceEnquiryMap.get(
										"InvoiceNumber").toString());

							cardNumAck.put("CardNumber", cardNumber);
							cardNumAck.put("Amount", redeemAmount);
							((MAXSaleReturnTransaction) cargo.getTransaction()).cardNumAck1
									.put(cardNumber, "Sucess");
							giftCardTenderArray.add(cardNumAck);
							DialogBeanModel dialogModel = new DialogBeanModel();
							String msg[] = new String[7];
							dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
							msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
							String cardNumTender = balanceEnquiryMap.get(
									"CardNumber").toString();
							String cardnumber = cardNumTender.substring(
									cardNumTender.length() - 4,
									cardNumTender.length());
							msg[1] = "GIFTCard" + " " + cardnumber;
							msg[2] = " Total Amount In This Card Is "
									+ ""
									+ balanceEnquiryMap.get(
											"CardCurrencySymbol").toString()
									+ balanceEnquiryMap.get("Amount")
											.toString() + " ";

							msg[3] = "ExpiryDate "
									+ calculateEYSDate(balanceEnquiryMap.get(
											"Expiry").toString());
							msg[4] = "Request Successfull:  Press button To Proceed";
							msg[5] = balanceEnquiryMap.get("CardType")
									.toString();
							msg[6] = "::Thanks::";
							dialogModel.setArgs(msg);
							dialogModel
									.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
							dialogModel.setButtonLetter(
									DialogScreensIfc.BUTTON_OK, "Continue");

							ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
									dialogModel);

							((MAXSaleReturnTransaction) retailTransaction)
									.setFatalDeviceCall(false);

						}
						if (balanceEnquiryMap.size() != 0
								&& ((balanceEnquiryMap.get("Amount") == null) || (!("0")
										.equals(balanceEnquiryMap.get(
												"ResponseCode").toString())))) {
							// Changes the offline flow as per discussion with
							// Client(Showing all the Giftcards which didn't get
							// redeem)
							// utilObj.ShowInvalidCardTender("GIFTCARD_ENQUIRYQC",
							// balanceEnquiryMap,successCards,tenders, gc, bus);
							failedgiftCardTenderArray.add(cardNumber);
							cargo.setGiftCardApproved(false);
							((MAXSaleReturnTransaction) retailTransaction)
									.setFatalDeviceCall(true);
							// break;
						}
					}
			}
				//Rev 1.3 changes start here
				else if(cargo.getTransaction() instanceof MAXOrderTransaction){
				if (((MAXOrderTransaction) cargo.getTransaction()).cardNumAck1 != null
						&& (((MAXOrderTransaction) cargo
								.getTransaction()).cardNumAck1
								.get(cardNumber) != null)
						&& (((MAXOrderTransaction) cargo
								.getTransaction()).cardNumAck1.get(
								cardNumber).toString()
								.equalsIgnoreCase("Sucess"))) {

					letter = new Letter(CommonLetterIfc.CONTINUE);
					bus.mail(letter, BusIfc.CURRENT);
				}
				// else if(MAXOrderTransaction(cargo.getTransaction()))
				// if(flag = true)
				else {
					ui.showScreen(
							MAXPOSUIManagerIfc.GIFT_CARD_VALIDATING_SCREEN,
							model);

					HashMap balanceEnquiryMap = null;
					// change start for enter bill amount parameter in
					// redeem function as mail in q.c
					if (!isSwiped1)
						balanceEnquiryMap = utilObj.redeemCard(pos,
								cardNumber, redeemAmount, totalAmount,
								cargo.getTransaction().getTransactionID());
					else
						balanceEnquiryMap = utilObj
								.redeemCardUsingTrackData(pos, cardNumber,
										redeemAmount, totalAmount, cargo
												.getTransaction()
												.getTransactionID(),
										trackData);
					// printChargeSlipData(bus, balanceEnquiryMap,
					// "REDEEM");
					individualslipforeveryGiftcard(bus, balanceEnquiryMap,
							"REDEEM");

					HashMap cardNumAck = new HashMap();
					MAXGiftCardIfc gc = (MAXGiftCardIfc) tgf.getGiftCard();
					if (balanceEnquiryMap.get("Amount") != null
							&& balanceEnquiryMap != null
							&& balanceEnquiryMap.size() != 0
							&& ("0").equals(balanceEnquiryMap.get(
									"ResponseCode").toString())) {
								gc = (MAXGiftCardIfc) tgf.getGiftCard();
						gc.setQcApprovalCode(balanceEnquiryMap.get(
								"ApprovalCode").toString());
						gc.setQcCardType(balanceEnquiryMap.get("CardType")
								.toString());
						gc.setExpirationDate(calculateEYSDate(balanceEnquiryMap
								.get("Expiry").toString()));
						gc.setQcTransactionId(balanceEnquiryMap.get(
								"TransactionId").toString());
						gc.setQcBatchNumber((balanceEnquiryMap
								.get("CurrentBatchNumber").toString()));
						if (balanceEnquiryMap.get("InvoiceNumber") != null
								&& !(("null").equals(balanceEnquiryMap
										.get("InvoiceNumber"))))
							gc.setQcInvoiceNumber(balanceEnquiryMap.get(
									"InvoiceNumber").toString());

						cardNumAck.put("CardNumber", cardNumber);
						cardNumAck.put("Amount", redeemAmount);
						((MAXOrderTransaction) cargo.getTransaction()).cardNumAck1
								.put(cardNumber, "Sucess");
						giftCardTenderArray.add(cardNumAck);
						DialogBeanModel dialogModel = new DialogBeanModel();
						String msg[] = new String[7];
						dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
						msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
						String cardNumTender = balanceEnquiryMap.get(
								"CardNumber").toString();
						String cardnumber = cardNumTender.substring(
								cardNumTender.length() - 4,
								cardNumTender.length());
						msg[1] = "GIFTCard" + " " + cardnumber;
						msg[2] = " Total Amount In This Card Is "
								+ ""
								+ balanceEnquiryMap.get(
										"CardCurrencySymbol").toString()
								+ balanceEnquiryMap.get("Amount")
										.toString() + " ";

						msg[3] = "ExpiryDate "
								+ calculateEYSDate(balanceEnquiryMap.get(
										"Expiry").toString());
						msg[4] = "Request Successfull:  Press button To Proceed";
						msg[5] = balanceEnquiryMap.get("CardType")
								.toString();
						msg[6] = "::Thanks::";
						dialogModel.setArgs(msg);
						dialogModel
								.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
						dialogModel.setButtonLetter(
								DialogScreensIfc.BUTTON_OK, "Continue");

						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
								dialogModel);

						((MAXOrderTransaction) retailTransaction)
								.setFatalDeviceCall(false);

					}
					if (balanceEnquiryMap.size() != 0
							&& ((balanceEnquiryMap.get("Amount") == null) || (!("0")
									.equals(balanceEnquiryMap.get(
											"ResponseCode").toString())))) {
						// Changes the offline flow as per discussion with
						// Client(Showing all the Giftcards which didn't get
						// redeem)
						// utilObj.ShowInvalidCardTender("GIFTCARD_ENQUIRYQC",
						// balanceEnquiryMap,successCards,tenders, gc, bus);
						failedgiftCardTenderArray.add(cardNumber);
						((MAXOrderTransaction) retailTransaction)
								.setFatalDeviceCall(true);
						// break;
					}
				}
			}
				// break;
			}
			// Rev 1.6 changes Ends 
//Rev 1.4 changed end
		}*/
    
		/*if (hasGiftCardTender)
			printChargeSlipData(bus, giftCardTenderArray, "REDEEM");
		*///Changes the offline flow as per discussion with Client(Showing al the Giftcards which didn't get redeem)
		//Rev 1.1 changes
		if(failedgiftCardTenderArray!=null && failedgiftCardTenderArray.size()!= 0){
			String msg[] = new String[7];
			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
			msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
			msg[1] = "GIFTCards" + " " + failedgiftCardTenderArray;
			msg[2] = "are failed due to network offline";   //" Total Amount In This Card Is " + balanceEnquiryMap.get("Amount");
			msg[3] = "Press enter to delete GC tender line item and select another tender type.";
			msg[4] = "::Thanks::";
			msg[5] = " ";
			msg[6] = " ";
			dialogModel.setArgs(msg);
			dialogModel.setType(DialogScreensIfc.ERROR);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "TenderSaleT");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		}
		//GC_ISSUE && GC_RELOAD
		if (items != null && items.length != 0){
			String var1 = ";";
			String var2 = "=";
			String var3 = "?";
			/*for (int j = 0; j < items.length; j++) */
			int j = ((MAXSaleCargo)cargo).getIndex();
			if(j<items.length)
			{
				if (items[j].getPLUItem() instanceof GiftCardPLUItem) {
				
					//line comment for base Gift receipt should not print in issue/redeem g.c transaction
						//items[j].setGiftReceiptItem(true);						
						HashMap balanceEnquiryMap = new HashMap();
						MAXGiftCardPLUItem gfItem = (MAXGiftCardPLUItem) items[j].getPLUItem();
						MAXGiftCard giftCard = (MAXGiftCard) gfItem.getGiftCard();
						String cardNumber = gfItem.getGiftCard().getCardNumber();
						String amount = gfItem.getGiftCard().getInitialBalance().toString();
						String notes = null;
						String denominationAmount=null;
						String trackData = null;
						if(giftCard.isSwiped() || isSwiped){
							isSwiped = true;
							if(gfItem.getTrackData()!=null)
							trackData =gfItem.getTrackData();
							giftCard.setEntryMethod(EntryMethod.Swipe);
						//	trackData = gfItem.getGiftCard().getTrackData();
//							trackData = ((MAXGiftCardPLUItem)gfItem).getTrackData();
							//trackData=	var1 + cardNumber.substring(0, 16) + var2 + cardNumber.substring(16) + var3;
						}
						//rev 1.5 starts
						else if(giftCard.isScanned() || isScanned)
						{
							isScanned = true;
							trackData =gfItem.getTrackData();
							giftCard.setEntryMethod(EntryMethod.Scan);
							//System.out.println("trackData Before "+trackData);
							//12909191900001050051390971
							//2991190010001309
							/*if(trackData==null){
								trackData =giftCard.getApprovalCode();	
								 cardNumber = BarCodeReaderUtility.GetCardNumber(trackData);
							}*/
							//System.out.println("trackData After "+trackData);
						}
						//rev 1.5 ends
						else{
							isSwiped  = false;
						}
						
						/**
						 *  Fixed the GiftCard swiping issue while using multiple cards -- Jyoti
						 */
//						if(((MAXGiftCardPLUItem)gfItem).isSwiped()){
//							isSwiped = true;
//						//	trackData = gfItem.getGiftCard().getTrackData();
//							trackData = ((MAXGiftCardPLUItem)gfItem).getTrackData();
//							
//						}else{
//							isSwiped  = false;
//						}
						if (gfItem.getGiftCard().getRequestType() == GiftCardIfc.GIFT_CARD_RELOAD) {
							denominationAmount = giftCard.getCurrentBalance().toString();
							notes = "{VldType~GCRLD|AMT~" + denominationAmount +"}";
							// rev 1.5 starts
							//if(!isSwiped){
							if(!isSwiped && !isScanned)   // || replaced by && - karni
								//rev 1.5 ends 
							balanceEnquiryMap = utilObj.reloadLoad(pos, cardNumber, giftCard.getCurrentBalance().toString(), cargo.getTransaction()
									.getTransactionID());
							else
//								balanceEnquiryMap = utilObj.reloadLoadUsingTrackData(pos, cardNumber, giftCard.getCurrentBalance().toString(), cargo.getTransaction()
//								.getTransactionID(),model1.getTrackData());
								if(trackData != null){   // Suspend - retrival track data issue fixed.
									balanceEnquiryMap = utilObj.reloadLoadUsingTrackData(pos, cardNumber, giftCard.getCurrentBalance().toString(), cargo.getTransaction()
											.getTransactionID(),trackData, notes);
									
								}else{
									balanceEnquiryMap = utilObj.reloadLoad(pos, cardNumber, giftCard.getCurrentBalance().toString(), cargo.getTransaction()
											.getTransactionID());
								}
							
					    //printChargeSlipData(bus, balanceEnquiryMap, "RELOAD");
					    //giftreceipt(bus, balanceEnquiryMap, "RELOAD");
					    if(balanceEnquiryMap.get("PreviousBalance")!=null)
					    	giftCard.setInitialBalance(DomainGateway.getBaseCurrencyInstance(balanceEnquiryMap.get("PreviousBalance").toString()));
					    if(balanceEnquiryMap.get("Amount")!=null)
					    	giftCard.setCurrentBalance(DomainGateway.getBaseCurrencyInstance(balanceEnquiryMap.get("Amount").toString()));
					    if(balanceEnquiryMap.get("Expiry")!=null)
					    	giftCard.setExpirationDate(calculateEYSDate(balanceEnquiryMap.get("Expiry").toString()));
					    giftCardList.add(giftCard);
						
							
						}
						//Rev 1.4 chnages start
//						else if (gfItem.getGiftCard().getRequestType() == TenderAuthConstantsIfc.REDEEM) {
//							//change start by for enter bill amount parameter in redeem function and redeemCardUsingTrackData function as mail in q.c
//							if(!isSwiped)
//							balanceEnquiryMap = utilObj.redeemCard(pos, cardNumber, giftCard.getCurrentBalance().toString(),totalAmount, cargo.getTransaction()
//									.getTransactionID());
//							else
//								balanceEnquiryMap = utilObj.redeemCardUsingTrackData(pos, cardNumber, giftCard.getCurrentBalance().toString(),totalAmount, cargo.getTransaction()
//										.getTransactionID(),model1.getTrackData());
//							
//
//						} 
//						else if (gfItem.getGiftCard().getRequestType() == 22) {
//							balanceEnquiryMap = utilObj.redeemCard(pos, cardNumber, items[j].getSellingPrice().toString(),totalAmount, cargo.getTransaction()
//									.getTransactionID());
//							//change End by for enter bill amount parameter in redeem function and redeemCardUsingTrackData function as mail in q.c
//						} 
						else if (gfItem.getGiftCard().getRequestType() == 30 || gfItem.getGiftCard().getRequestType() == 31) {
							
							balanceEnquiryMap = utilObj.balanceEnquiry(pos, cardNumber);
									//.getTransactionID());
						} 
						else
						{//GC_ISSUE
							// rev 1.5 starts
							//if(!isSwiped){
							denominationAmount = giftCard.getInitialBalance().toString();
							notes = "{VldType~GCACT|AMT~" + denominationAmount +"}";
							
							if(!isSwiped && !isScanned){  // || replaced by && - karni
								//rev 1.5 ends 
								balanceEnquiryMap = utilObj.activateCard(pos, cardNumber, amount, cargo.getTransaction().getTransactionID());
								}
								else{
								//String td =	trackData;
									if(trackData != null){  // Suspend - retrival track data issue fixed.
										balanceEnquiryMap = utilObj.activateCardUsingTrackData(pos, cardNumber, amount, cargo.getTransaction().getTransactionID(),trackData,notes);
									}else{
										balanceEnquiryMap = utilObj.reloadLoad(pos, cardNumber, giftCard.getCurrentBalance().toString(), cargo.getTransaction()
												.getTransactionID());
									}
								 }
								 //giftreceipt(bus,balanceEnquiryMap, "ISSUE");
								 //setGiftCardList(giftCardList)
								 if(balanceEnquiryMap.get("PreviousBalance")!=null)
								    	giftCard.setInitialBalance(DomainGateway.getBaseCurrencyInstance(balanceEnquiryMap.get("PreviousBalance").toString()));
								 if(balanceEnquiryMap.get("Amount")!=null)
								    	giftCard.setCurrentBalance(DomainGateway.getBaseCurrencyInstance(balanceEnquiryMap.get("Amount").toString()));
								 if(balanceEnquiryMap.get("Expiry")!=null)
								    	giftCard.setExpirationDate(calculateEYSDate(balanceEnquiryMap.get("Expiry").toString()));
								 giftCardList.add(giftCard);
								 
								
						}

						if (balanceEnquiryMap.get("Amount") != null) {

							
							utilObj.ShowValidCard("GIFTCARD_ENQUIRYQC", balanceEnquiryMap, giftCard, bus);

						}
						if (balanceEnquiryMap != null && balanceEnquiryMap.size() != 0 && ("0").equals(balanceEnquiryMap.get("ResponseCode").toString())) {

							utilObj.SetValuesInGiftCard(giftCard, balanceEnquiryMap);

						}
						if (balanceEnquiryMap.size()!= 0 && (balanceEnquiryMap.get("Amount") == null) &&  (gfItem.getGiftCard()!= null && ((gfItem.getGiftCard().getRequestType() != 30) || (gfItem.getGiftCard().getRequestType() != 31)))) {

//							utilObj.ShowInvalidCard("GIFTCARD_ENQUIRYQC", balanceEnquiryMap, giftCard, bus);
							failedgiftCardIssueArray.add(giftCard.getCardNumber()); //Rev 1.2 changes
							if(balanceEnquiryMap.containsKey("ResponseMessage") && balanceEnquiryMap.get("ResponseMessage")!=null && !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseMessage")) && !"".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseMessage"))){
							failedgiftCardMessageArray.add((String) balanceEnquiryMap.get("ResponseMessage"));
							}
							
							//break;
						}
					//}
				}
			}
		 //print v14 gift card slip 
		printGiftcardSlip(bus, giftCardList);
			
		}
		//Rev 1.2 changes start here
		if(failedgiftCardIssueArray!=null && failedgiftCardIssueArray.size()!= 0){
			String msg[] = new String[7];
			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
			msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
			msg[1] = "GIFTCards" + " " + failedgiftCardIssueArray;
			//msg[2] = "POS is Inactive";   //" Total Amount In This Card Is " + balanceEnquiryMap.get("Amount");
			if(failedgiftCardMessageArray!=null && failedgiftCardMessageArray.size()!= 0){
				msg[2] = failedgiftCardMessageArray.toString();
				
			}else{
				msg[2] = "are failed due to network offline or decline from qwikcilwer";   //" Total Amount In This Card Is " + balanceEnquiryMap.get("Amount");
				
			}
			
			
			msg[3] = "These Gift Cards cannot be issued/reloaded";
			msg[4] = "::Thanks::";
			msg[5] = " ";
			msg[6] = " ";
			dialogModel.setArgs(msg);
			dialogModel.setType(DialogScreensIfc.ERROR);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "TenderSale");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		}
		//Rev 1.2 changes end here
//		if ((items == null && hasGiftCardTender) || transactionReentryMode||(items == null && hasGiftCardTender == false)) // skip
//		if (transactionReentryMode||(items == null && hasGiftCardTender == false)) // skip
																			// activation
																			// of
																			// trans.
																			// reentry
																			// mode
			if (items == null && hasGiftCardTender == false) // skip
		{
			letter = new Letter(CommonLetterIfc.CONTINUE);
			bus.mail(letter, BusIfc.CURRENT);
		}
		


		// bus.mail(letter, BusIfc.CURRENT);
		//bus.mail("Continue", BusIfc.CURRENT); // hardcoded no
												// validation
	}

	public  ArrayList<GiftCardIfc> giftCardList=null;
	
	public void printGiftcardSlip(BusIfc bus, ArrayList<GiftCardIfc> giftCardList)
	{

			// * Gift Card redeem/reload/issue Slip *
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
			SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
			TenderableTransactionIfc trans = cargo.getTransaction();

			PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc) bus
					.getManager(PrintableDocumentManagerIfc.TYPE);
			MAXReceiptParameterBeanIfc receipt;
			try {
				receipt = (MAXReceiptParameterBeanIfc) pdm
						.getReceiptParameterBeanInstance((SessionBusIfc) bus, trans);

			receipt.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
			receipt.setTransaction(trans);
			receipt.setDocumentType(MAXReceiptTypeConstantsIfc.GIFT_CARD_SLIP);
			
			for (GiftCardIfc giftCard : giftCardList) {
				
				if(giftCard.getRequestType()==GiftCardIfc.GIFT_CARD_ISSUE)
					receipt.setGiftCardRequestType(GiftCardIfc.GIFT_CARD_ISSUE);	
				else if(giftCard.getRequestType()==GiftCardIfc.GIFT_CARD_REDEEM)
					receipt.setGiftCardRequestType(GiftCardIfc.GIFT_CARD_REDEEM);
				else if(giftCard.getRequestType()==GiftCardIfc.GIFT_CARD_RELOAD)
						receipt.setGiftCardRequestType(GiftCardIfc.GIFT_CARD_RELOAD);	
		
				receipt.setGiftCard(giftCard);
				pdm.printReceipt((SessionBusIfc) bus, receipt);
			}
				
		
			//pdm.printReceipt((SessionBusIfc) bus, receipt);
					
			}catch (ParameterException |PrintableDocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

/*	public void printChargeSlipData(BusIfc bus, Vector response, String action) {

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
		ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
		String LINE_SEPARATOR = "line.separator";
		String sep = System.getProperty(LINE_SEPARATOR);
		StringBuffer sepBuffer = new StringBuffer();
		for (int i = 0; i < 3; i++) {
			sepBuffer.append(sep);
		}
		String sixBlankLines = sepBuffer.toString();

		try {
			
			//pda.printNormal(POSPrinterConst.PTR_S_RECEIPT, getFormattedReportForGiftCard(response, action) + sixBlankLines);
			 //change for print a single receipt for redeem g.c no into substring
			pda.printNormal(POSPrinterConst.PTR_S_RECEIPT,  header());
			pda.printNormal(POSPrinterConst.PTR_S_RECEIPT, getCardData(response));
			pda.printNormal(POSPrinterConst.PTR_S_RECEIPT, footer(bus) + sixBlankLines);
			// pda.printNormal(POSPrinterConst.PTR_S_RECEIPT,getFormattedReport(response)
			// + sixBlankLines);
			pda.cutPaper(100);
		} catch (DeviceException e) {
			e.printStackTrace();
			ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
			DialogBeanModel model = new DialogBeanModel();
			String msg[] = new String[1];
			UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
			msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG, BundleConstantsIfc.PRINTER_OFFLINE);
			model.setResourceID("RetryCancel");
			model.setType(DialogScreensIfc.RETRY_CANCEL);
			model.setArgs(msg);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}

	}
*/
	
	//changes for rev 1.8 end
		// Changes for rev 1.1 end

	/*public void individualslipforeveryGiftcard(BusIfc bus, HashMap response, String action) {

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
		ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
		String LINE_SEPARATOR = "line.separator";
		String sep = System.getProperty(LINE_SEPARATOR);
		StringBuffer sepBuffer = new StringBuffer();
		for (int i = 0; i < 3; i++) {
			sepBuffer.append(sep);
		}
		String sixBlankLines = sepBuffer.toString();

		try {
			
			pda.printNormal(POSPrinterConst.PTR_S_RECEIPT,getFormattedReportForGiftCard(response, action) + sixBlankLines);
			
			// pda.printNormal(POSPrinterConst.PTR_S_RECEIPT,getFormattedReport(response)
			// + sixBlankLines);
			pda.cutPaper(100);
		} catch (DeviceException e) {
			e.printStackTrace();
			ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
			DialogBeanModel model = new DialogBeanModel();
			String msg[] = new String[1];
			UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
			msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG, BundleConstantsIfc.PRINTER_OFFLINE);
			model.setResourceID("RetryCancel");
			model.setType(DialogScreensIfc.RETRY_CANCEL);
			model.setArgs(msg);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		}
		catch(Exception e)
		{
			e.printStackTrace(); 
		}

	}
*//*	public void giftreceipt(BusIfc bus, HashMap response, String action) {

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
		ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
		String LINE_SEPARATOR = "line.separator";
		String sep = System.getProperty(LINE_SEPARATOR);
		StringBuffer sepBuffer = new StringBuffer();
		for (int i = 0; i < 3; i++) {
			sepBuffer.append(sep);
		}
		String sixBlankLines = sepBuffer.toString();

		try {
			
			pda.printNormal(POSPrinterConst.PTR_S_RECEIPT, giftreceiptForGiftCard(response, action) + sixBlankLines);
			
			// pda.printNormal(POSPrinterConst.PTR_S_RECEIPT,getFormattedReport(response)
			// + sixBlankLines);
			pda.cutPaper(100);
		} catch (DeviceException e) {
			e.printStackTrace();
			ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
			DialogBeanModel model = new DialogBeanModel();
			String msg[] = new String[1];
			UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
			msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG, BundleConstantsIfc.PRINTER_OFFLINE);
			model.setResourceID("RetryCancel");
			model.setType(DialogScreensIfc.RETRY_CANCEL);
			model.setArgs(msg);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		}
		catch(Exception e)
		{
			e.printStackTrace(); 
		}

	}
*/	  // Rev 1.7 Changes Starts 
/*	public String getFormattedReportForGiftCard(HashMap map, String action) {
		StringBuffer buff = new StringBuffer();
		StringBuffer starPrintAsGCNumber=new StringBuffer();
		//buff.append("***************************************");
		// buff.append(RegisterReport.NEW_LINE);
		//buff.append(System.getProperty("line.separator"));
        // buff.append(RegisterReport.NEW_LINE);
		// String data = map.get("PrintLine" + i).toString();
	///	formatting("Gift Card", buff); //Rev 1.2
	//	buff.append(RegisterReport.NEW_LINE);
	//	formatting(action, buff);
	//	buff.append(RegisterReport.NEW_LINE);
		formatting(action +" "+ "Gift Card Receipt", buff);
		String data0 = "";
		String cardNum=null;
		cardNum=map.get("CardNumber").toString();
		if(map.get("CardNumber")!=null &&!(cardNum.equalsIgnoreCase("")) ){
			
			//cardNum=map.get("CardNumber").toString();
			data0 = cardNum.substring(cardNum.length()-4,cardNum.length());
			//Rev 1.9 start
			for(int i=0;i<cardNum.length()-4;i++){
				starPrintAsGCNumber.append('*');
			}
			//formatting("Card Number:" + cardNum, buff); 

			//Rev 1.9 ends
		}
		formatting("Card Number:" +starPrintAsGCNumber+""+data0, buff);
		
		//String	data1 = map.get("CardHolderName").toString();
		//formatting("Name:" + data1, buff);
		EYSDate data2=DomainGateway.getFactory().getEYSDateInstance();
		if(map.get("Expiry") !=null && !("null").equals(map.get("Expiry").toString()))
		data2=calculateEYSDate(map.get("Expiry").toString());
		//String  data2 = map.get("Expiry").toString();
		formatting("Expiry Date:" + data2, buff); //Rev 1.2
		String data3 = "";
		if(map.get("ResponseMessage")!=null)
		data3 = action + " " + map.get("ResponseMessage").toString();
		formatting(data3, buff);
		String data4 ="";
		String data5 = "";
		if(map.get("PreviousBalance")!=null )
		 data4 = map.get("PreviousBalance").toString();
		if(map.get("Amount")!=null)
		 data5 = map.get("Amount").toString();
		formatting("Pre. Bal:" + data4+"  Curr Bal:" + data5, buff);
		
		if(action.equalsIgnoreCase("RELOAD")){
			String data6 = map.get("ReloadAmount").toString();
	        
			formatting("Reloaded"+" Amt:" + data6, buff);
		}
		
		//String data6 = map.get("TransactionId").toString();
		//formatting("Trx ID:" + data6, buff);
		//String data7 = map.get("CurrentBatchNumber").toString();
		//formatting("BatchID:" + data7, buff);
		//String data8 ="";
		//if(map.get("CardType")!=null)
		//data8= map.get("CardType").toString();
		//formatting(data8, buff);
		String data9 = "THANK YOU";
		formatting(data9, buff);
		//buff.append("***************************************");
		//buff.append(System.getProperty("line.separator"));		
		//buff.append(System.getProperty("line.separator"));

		return (buff.toString());
	}  // Rev 1.7 Changes Ends 
*/	// Rev 1.7 changes starts 
/*	public String giftreceiptForGiftCard(HashMap map, String action) {
		StringBuffer buff = new StringBuffer();
		StringBuffer starPrintAsGCNumber=new StringBuffer();
		CurrencyIfc finalActionAmount=null;
		//buff.append("***************************************");
		//buff.append(System.getProperty("line.separator"));
		//formatting("Gift Card", buff); //Rev 1.2
		//buff.append(RegisterReport.NEW_LINE);
		//formatting("*Gift Receipt*", buff);
		//buff.append(RegisterReport.NEW_LINE);
		formatting(action + " "+"Gift Card Receipt", buff);  //Rev 1.2
		String data0 = "";
		String cardNum=null;
		if (map.get("CardNumber") != null && !(("null").equals(map.get("CardNumber")))){
		cardNum=map.get("CardNumber").toString();
		if(!(cardNum.equalsIgnoreCase("")) )
		data0 = cardNum.substring(cardNum.length()-4,cardNum.length());
		//Rev 1.9
		for(int i=0;i<cardNum.length()-4;i++){
			starPrintAsGCNumber.append('*');
		}
		}
		//rev 1.5 start
		formatting("Card Number:" +starPrintAsGCNumber+""+data0, buff);  //Rev 1.2
		//formatting("Card Number:" + cardNum, buff);
		//rev 1.5 ends
		EYSDate data2=DomainGateway.getFactory().getEYSDateInstance();
		if(map.get("Expiry") !=null && !("null").equals(map.get("Expiry").toString()))
		{
		data2=calculateEYSDate(map.get("Expiry").toString());		
		formatting("Expiry Date:" + data2, buff);  //Rev 1.2
		}
		String data3 = "";
		if(map.get("ResponseCode")!=null){
			if(("0").equals(map.get(
			"ResponseCode").toString())){
				if(map.get("ResponseMessage")!=null)
					data3 = action + " " + map.get("ResponseMessage").toString();
			}else{
				if(map.get("ResponseMessage")!=null)
				data3 = action + " " + map.get("ResponseMessage").toString()+". Please contact customer service desk.";
				
			}
		}
		formatting(data3, buff);
		String data4 =null;
		String data5 = null;
		map.put(SVTags.PREVIOUS_BALANCE, null);
		map.put(SVTags.RESPONSE_CODE, "10027");
		if(map.containsKey("PreviousBalance") && map.get("PreviousBalance") !=null && !("null").equals(map.get("PreviousBalance").toString()))
		{
		 data4 = map.get("PreviousBalance").toString();
		 if(map.containsKey("Amount") && map.get("Amount") !=null && !("null").equals(map.get("Amount").toString())){
		 data5 = map.get("Amount").toString();
		 }
		formatting("Pre. Bal:" + data4 +"  Curr Bal:" + data5 , buff);
		}
		double actionAmount=diffAmount(data4,data5);
		BigDecimal actionAmountBD = new BigDecimal(actionAmount);
		CurrencyIfc finalActionAmount = DomainGateway.getBaseCurrencyInstance(actionAmountBD);
		
		//Rev 1.9 
		if(action.equalsIgnoreCase("RELOAD")){
		if(map.get("ReloadAmount")==null){
			double actionAmount=diffAmount(data4,data5);
			BigDecimal actionAmountBD = new BigDecimal(actionAmount);
			finalActionAmount = DomainGateway.getBaseCurrencyInstance(actionAmountBD);
			formatting("Reloaded"+" Amt:" + finalActionAmount.toString(), buff);
		}else{
			String data6 = map.get("ReloadAmount").toString();
			formatting("Reloaded"+" Amt:" + data6, buff);
		}
		
				
		}
		
		//Rev 1.9
		String data8 =null;
		if(map.get("CardType") !=null && !("null").equals(map.get("CardType").toString()))
	    data8 = map.get("CardType").toString();
		formatting(data8, buff);
		String data9 = "THANK YOU";
		formatting(data9, buff);
		//buff.append("***************************************");
		//buff.append(System.getProperty("line.separator"));
		///buff.append(System.getProperty("line.separator"));		
		//buff.append(System.getProperty("line.separator"));
		return (buff.toString());
	}
*/	// Rev 1.7 changes End 
/*	public void formatting(String data, StringBuffer buff) {
		int LL = 40;
		int DL = 0;
		if (data != null && data.length() > 2)
			DL = data.length() / 2;

		int SL = 0;
		SL = (LL / 2) - DL;

		if (data != null && data.length() > 2) {
			for (int j = 0; j < SL; j++) {
				data = " " + data;
			}
		}
		buff.append(getFormattedLine(data, null, null));
		// System.getProperty("line.separator");
		buff.append(System.getProperty("line.separator"));
		
		 * if(("THANK YOU").equals(data.trim())) {
		 * buff.append(RegisterReport.NEW_LINE);
		 * buff.append(RegisterReport.NEW_LINE);
		 * buff.append(System.getProperty("line.separator"));
		 * 
		 * 
		 * }
		 
	}
*//*	public void formatdata(String data1, String data2,StringBuffer buff){
		
		String data ="   "+data1+"               "+data2;
		buff.append(getFormattedLine(data, null, null));
		buff.append(System.getProperty("line.separator"));
		}
*/		

/*	protected String getFormattedLine(String descString, String countString, String moneyString) {

		int delta = 0;
		if ((descString != null) && (countString != null) && (moneyString != null)) {
			if ((39 - moneyString.length()) < 28) {
				delta = 28 - (39 - moneyString.length());

			}
		}
		// String SPACES = "                                       ";
		String SPACES = "";
		StringBuffer str = new StringBuffer(SPACES);
		if (descString != null && descString.length() != 0)
			str.insert(0, descString);
		// Check for null
		if (countString != null) {
			str.insert(28 - countString.length(), countString);
		}
		if (moneyString != null) {

			str.insert(39 - moneyString.length() + delta, moneyString);
			// re-initialize the values

		}
		String pStr = str.toString();
		String prim = null; // pStr.substring(0,40);
		String sec = "";
		if (pStr.length() > 39) {
			prim = pStr.substring(0, 40);
			sec = pStr.substring(40);
			if (sec.trim().toString() != "")
				prim = prim + System.getProperty("line.separator") + sec;
			pStr = prim;
		}

		return pStr;
	}
	public String printGiftCardData(String cardNumber) {
		StringBuffer buff = new StringBuffer();
		buff.append("***************************************");
		// buff.append(RegisterReport.NEW_LINE);
		buff.append(System.getProperty("line.separator"));

		// buff.append(RegisterReport.NEW_LINE);
		// String data = map.get("PrintLine" + i).toString();
		formatting("GiftCard", buff);
		//buff.append(RegisterReport.NEW_LINE);
		//formatting(action, buff);
		//buff.append(RegisterReport.NEW_LINE);
		//formatting(action + "GiftCard Reciept", buff);
		String data0 = cardNumber;
		formatting("CardNumber:" + data0, buff);
		
	//	formatting(data9, buff);
		buff.append("***************************************");

		return (buff.toString());
	}
*/	
	
	/*public String getCardData(Vector cardData){
     		
		
		
		StringBuffer buff = new StringBuffer();
		buff.append(System.getProperty("line.separator"));
		
		int numberOfCards = cardData.size();
		for (int i = 0; i<numberOfCards; i++)
		{
			HashMap cardDetails = (HashMap) cardData.get(i);
		
			String cardNum=cardDetails.get("CardNumber").toString();
			String data0 ="";
			if(cardNum!=null && !(cardNum.equalsIgnoreCase("")))
			data0 = cardNum.substring(cardNum.length()-4,cardNum.length()); 
			//data0 = cardDetails.get("CardNumber").toString();
			String data1 = cardDetails.get("Amount").toString();
			formatdata(data0,data1, buff);
			
		}
		
		
		return (buff.toString());
	}
	public String footer(BusIfc bus)
	{
		SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		StringBuffer buff = new StringBuffer();
		String tillid=cargo.getTillID();
		String transactionid=cargo.getTenderableTransaction().getTransactionID();
		String StoreId=cargo.getTenderableTransaction().getFormattedStoreID();
		String registerId=cargo.getTransaction().getFormattedWorkstationID();
		String date=(cargo.getTransaction().getBusinessDay()).toString();
		String CashierName=cargo.getTransaction().getCashier().getLoginID();
		String seqno=cargo.getTransaction().getFormattedTransactionSequenceNumber();
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date systemDate = new Date();
		String[] foot = null;
		try {
			foot=pm.getStringValues("ReceiptFooterForGiftCardTenderChargeSlip");
		} catch (ParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buff.append(System.getProperty("line.separator"));
		buff.append(System.getProperty("line.separator"));
		buff.append(System.getProperty("line.separator"));
		buff.append("Signature:"+"----------------------------");
		buff.append(System.getProperty("line.separator"));
		buff.append("           (Customer Signature)");
		buff.append(System.getProperty("line.separator"));
		buff.append(System.getProperty("line.separator"));
		printStrings(foot,buff);
		formatting(UNDERLINE, buff);
		buff.append("TillID:"+tillid);
		buff.append("  Trans:"+seqno);
		buff.append(" ");
		buff.append(dateFormat.format(systemDate));
		buff.append(System.getProperty("line.separator"));
		buff.append("StoreId:"+ StoreId);
		buff.append("  Reg :" +registerId);
		buff.append("  Cashier:"+CashierName);
		//buff.append("  time:"+time);
		buff.append(System.getProperty("line.separator"));
		buff.append("Barcode:"+transactionid);
		buff.append(System.getProperty("line.separator"));
		formatting(UNDERLINE, buff);
		buff.append(System.getProperty("line.separator"));
		
		buff.append(System.getProperty("line.separator"));
		return buff.toString();
	}
	public String header(){
		
		StringBuffer buff = new StringBuffer();
		formatting(Gift_Card_Tender_Slip, buff);
		//buff.append(RegisterReport.NEW_LINE);
		formatting(UNDERLINE, buff);
		buff.append(System.getProperty("line.separator"));
		buff.append("Card Number"+"           Amount");
		buff.append(System.getProperty("line.separator"));
		return buff.toString();
	}
	protected void printStrings(String[] lines, StringBuffer buff)  {
		if (lines == null) {
			return;
		}
		StringBuffer rmdr = new StringBuffer("");
		for (int i = 0; i < lines.length; i++) {
			if (lines[i] != null && lines[i].length() <= LINE_LENGTH) {
				buff.append(lines[i]);
				buff.append(System.getProperty("line.separator"));
			} else if (lines[i] != null) {
				int arrayLength = Math.abs(lines[i].length() / LINE_LENGTH);
				String[] innerArray = new String[++arrayLength];
				rmdr.append(lines[i]);
				for (int j = 0; j < innerArray.length; j++) {
					if (rmdr.toString().length() > LINE_LENGTH) {
						innerArray[j] = rmdr.toString().substring(0, LINE_LENGTH);
					} else {
						innerArray[j] = rmdr.toString();
					}
					rmdr.delete(0, LINE_LENGTH);
				}
				printStrings(innerArray, buff);
			}
		}
	}
	*/
	/*double diffAmount(String data4,String data5){
		
		double data6;
		data5 = replaceComman(data5);
		data4 = replaceComman(data4);
		
		double doubleOfdata5 = Double.parseDouble(data5);
		double doubleOfdata4 = Double.parseDouble(data4);
		data6=doubleOfdata5-doubleOfdata4;
		
		if(data6<0)
			data6=data6*(-1);
		
		return data6;
	}
*/
/*	private String replaceComman(String data5) {
		while(data5.indexOf(",")!=-1){
			String replaceFirst = data5.replaceFirst(",", "");
			data5=replaceFirst;
		}
		return data5;
	}
*/
	}
		
	
	
