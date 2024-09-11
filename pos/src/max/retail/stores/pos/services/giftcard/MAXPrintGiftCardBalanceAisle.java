
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
*  Copyright (c) 2016 MAX Hyper Market Inc.    All Rights Reserved.
*
*	Rev	1.0 	03Mar,2017	Hitesh.Dua			Gift Card receipt print related changes 
*
********************************************************************************
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
    Rev 1.2  03/feb/2016	BhanuPriya Gupta, 		Changes for gift Receipt  
   	Rev 1.2  15/Jan/2015	Bhanu Priya Gupta      for Bug [Bug 16618]
   	Rev 1.1  09/July/2013	Jyoti Rawal, Fix for Bug 6895 - GC - Balance inquiry Receipt 
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.pos.services.giftcard;

import java.util.HashMap;

import com.qwikcilver.clientapi.svpos.GCPOS;

import jpos.POSPrinterConst;
import max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc;
import max.retail.stores.pos.receipt.MAXReceiptTypeConstantsIfc;
import max.retail.stores.pos.services.qc.MAXGiftCardUtilitiesQC;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.reports.RegisterReport;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;

public class MAXPrintGiftCardBalanceAisle extends PosLaneActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8659959112183354132L;
	
	
	public void traverse(BusIfc bus) {

		/*
		 * read the data from the UI
		 */

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();

		GiftCardIfc giftCard = cargo.getGiftCard();
		String cardNumber = giftCard.getCardNumber();
		GiftCardBeanModel model = (GiftCardBeanModel) ui.getModel(POSUIManagerIfc.GET_CARD_NUM_FOR_GIFT_CARD);
		// cardNumber = model.getCardRangeFrom();
		if (logger.isInfoEnabled())
			logger.info("GiftCardNumberEnteredAisle.traverse(), cardNumber = " + cardNumber + "");

		MAXGiftCardUtilitiesQC utilObj = new MAXGiftCardUtilitiesQC();
		GCPOS pos = utilObj.getInstance();
		String denominationAmount = giftCard.getInitialBalance().toString();
		HashMap balanceEnquiryMap = utilObj.balanceEnquiry(pos, cardNumber, denominationAmount, "BLC");
		giftCard.setExpirationDate(calculateEYSDate(balanceEnquiryMap.get("Expiry").toString()));
		giftCard.setInitialBalance(DomainGateway.getBaseCurrencyInstance(balanceEnquiryMap.get("PreviousBalance").toString()));
		giftCard.setCurrentBalance(DomainGateway.getBaseCurrencyInstance(balanceEnquiryMap.get("Amount").toString()));
		
		printGiftcardSlip(bus, GiftCardIfc.GIFT_CARD_INQUIRY, giftCard);
		bus.mail("Continue", BusIfc.CURRENT);

	}
	//pritning v14 gc balance receipt
	public void printGiftcardSlip(BusIfc bus,int requestType,GiftCardIfc giftCard) {

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		try {
				//* Gift Card  balance slip
				PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
				MAXReceiptParameterBeanIfc receipt=(MAXReceiptParameterBeanIfc) BeanLocator.getApplicationBean("application_ReceiptParameterBean");

				receipt.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
				receipt.setGiftCardRequestType(requestType);
				receipt.setGiftCard(giftCard);
				receipt.setDocumentType(MAXReceiptTypeConstantsIfc.GIFT_CARD_SLIP);

				pdm.printReceipt((SessionBusIfc)bus, receipt);

		} catch (PrintableDocumentException e) {
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
	
/*	public void printChargeSlipData(BusIfc bus, HashMap response, String action) {

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
			pda.printNormal(POSPrinterConst.PTR_S_RECEIPT, getFormattedReportForGiftCard(response, action) + sixBlankLines);
			// pda.printNormal(POSPrinterConst.PTR_S_RECEIPT,getFormattedReport(response)
			// + sixBlankLines);
			pda.cutPaper(100);

		} catch (DeviceException e) {
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

	}
*//*	public String getFormattedReportForGiftCard(HashMap map, String action) {
		StringBuffer buff = new StringBuffer();
		StringBuffer starPrintAsGCNumber=new StringBuffer();
		//Rev 1.2 Changes starts 
		// bhanu changes starts
		//buff.append("***************************************");
		// buff.append(RegisterReport.NEW_LINE);
	//	buff.append(System.getProperty("line.separator"));

		// buff.append(RegisterReport.NEW_LINE);
		// String data = map.get("PrintLine" + i).toString();
		//formatting("GiftCard", buff);
		//buff.append(RegisterReport.NEW_LINE);
		//formatting(action, buff);
		//buff.append(RegisterReport.NEW_LINE);
		formatting(action + "GiftCard Receipt", buff);
		String cardNum = map.get("CardNumber").toString();
		
		String data0 = cardNum.substring(cardNum.length()-4,cardNum.length());
		//Rev 1.9 start
		for(int i=0;i<cardNum.length()-4;i++){
			starPrintAsGCNumber.append('*');
		}
		formatting("Card Number:" +starPrintAsGCNumber+""+data0, buff);
		//formatting("CardNumber:" + data0, buff);
		//Rev 1.1 changes start
//		String data1 = map.get("CardHolderName").toString();
//		formatting("Name:" + data1, buff);
//		String data2 = map.get("Expiry").toString();
//		formatting("ExpiryDate:" + data2, buff);
//		EYSDateField e = new EYSDateField();
//		e.setD
//		e.getDate();
		EYSDate data2=DomainGateway.getFactory().getEYSDateInstance();
		if(map.get("Expiry") !=null && !("null").equals(map.get("Expiry").toString()))
		{
		data2=calculateEYSDate(map.get("Expiry").toString());		
		formatting("Expiry Date:" + data2, buff);  //Rev 1.2
		}
		//Rev 1.1 changes end
		//String data3 = action + " " + map.get("ResponseMessage").toString();
		//formatting(data3, buff);
		//akanksha offline
		if(map.get("PreviousBalance") !=null && !("null").equals(map.get("PreviousBalance").toString())){
		String data4 = map.get("PreviousBalance").toString();
		//formatting("Pre. Bal:" + data4, buff);
		if(map.get("Amount") !=null && !("null").equals(map.get("Amount").toString())){
		String data5 = map.get("Amount").toString();
		//formatting("Curr Bal:" + data5, buff);
		formatting("Pre. Bal:" + data4+"  Curr Bal:" + data5, buff);
		}}
		//akanksha offline
		//String data6 = map.get("TransactionId").toString();
		//formatting("Trx ID:" + data6, buff);
		//String data7 = map.get("CurrentBatchNumber").toString();
		//formatting("BatchID:" + data7, buff);
		//String data8 = map.get("CardType").toString();
		//formatting(data8, buff);
		String data9 = "THANK YOU";
		formatting(data9, buff);
		//buff.append("***************************************");
		//Rev 1.2 Changes Ends 
		return (buff.toString());
	}
*/
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
*/	//Rev 1.1 changes start
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
	//Rev 1.1 changes end
}
