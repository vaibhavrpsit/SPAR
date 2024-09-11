
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
    Rev 1.5  03/05/2016    Mohd Arif            Change for message "Bill Amount Cannot be less than Redemption Amount" coming from QC when amount>dddd.5d
    Rev 1.4  25/04/2016    Mohd Arif            Change to print last 4 digit of gift card with remaining (*) and reload amount.
    Rev 1.3 2/feb/2016     Akanksha chauhan    Changes for giftcard offline
    Rev 1.2 18/Jan/2016     Bhanu Priya Gupta  Change for Bug 16617
    Rev 1.1 26/nov/2014     Rahul Yadav  Change for Customer requirement 
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.qc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;

import jpos.POSPrinterConst;
import max.retail.stores.domain.utility.MAXGiftCardIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

import com.qwikcilver.clientapi.svclient.SVTags;
import com.qwikcilver.clientapi.svpos.GCPOS;
import com.qwikcilver.clientapi.svpos.GCPOSUtils;

public class MAXGiftCardUtilitiesQC {

	static String posHome = null;
	static short posType = 2;
	static boolean forceInit = false;
	DialogBeanModel dialogModel = new DialogBeanModel();

	// static String userId = "infodart";
	// static String password = "welcome";

	public static GCPOS pos;
	public static GCPOSUtils gcPOSUtil;

	public GCPOS getInstance() {
		String QCURL = Gateway.getProperty("application", "QCAPIFolderURL", null);

		try {
			posHome = QCURL;

		} catch (Exception e1) {
			System.out.println("The path as mentiones can not be verified, please contact Infodart" + posHome);
		}

		try {

			pos = GCPOS.getInstance(posType, posHome, forceInit);

		} catch (Exception e) {
//			System.out.println("****************8"+e);
		}
		return pos;

	}
//Rev 1.0 changes
public  String getCardNumberFromTrackData(String trackData, boolean a)
	{	
		//gcPOSUtil = new GCPOSUtils();
		String b = GCPOSUtils.getCardNumberFromTrackData(trackData);	
		
		return b;
	}

	public HashMap balanceEnquiry(GCPOS pos, String cardNumber, String Amount, String invoiceNumber, String trackData) {
		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

 		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		requestMap.put(SVTags.TRACK_DATA, trackData);
		// requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
		// requestMap.put(SVTags.AMOUNT, Amount);

		boolean activate = pos.balanceEnquiry(requestMap, outputResponseMap);
		
		System.out.println("Balance Enquiry" + activate);

		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}

		return outputResponseMap;
	}

	public HashMap balanceEnquiryWithNotes(GCPOS pos, String cardNumber,
			String trackData, String notes) {
		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		requestMap.put(SVTags.TRACK_DATA, trackData);
		requestMap.put(SVTags.NOTES, notes);

		boolean activate = pos.balanceEnquiry(requestMap, outputResponseMap);

		System.out.println("Balance Enquiry : " + activate);
		System.out.println("Request to QC : " + requestMap);
		System.out.println("Response from QC : " + outputResponseMap);
		
		return outputResponseMap;
	}

	public HashMap balanceEnquiryForRecipt(GCPOS pos, String cardNumber, String Amount, String invoiceNumber) {
		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		//requestMap.put(SVTags.TRACK_DATA, trackData);
		// requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
		// requestMap.put(SVTags.AMOUNT, Amount);

		boolean activate = pos.balanceEnquiry(requestMap, outputResponseMap);

		System.out.println("Balance Enquiry" + activate);

		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}

		return outputResponseMap;
	}

	public HashMap balanceEnquiry(GCPOS pos, String cardNumber, String Amount, String invoiceNumber) {
		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		// requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
		// requestMap.put(SVTags.AMOUNT, Amount);

		boolean activate = pos.balanceEnquiry(requestMap, outputResponseMap);

		System.out.println("Balance Enquiry" + activate);

		/*for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}*/

		return outputResponseMap;
	}

	public HashMap balanceEnquiryTrackData(GCPOS pos, String cardNumber,
			String trackData) {
		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		requestMap.put(SVTags.TRACK_DATA, trackData);
		boolean activate = pos.balanceEnquiry(requestMap, outputResponseMap);

		for (Iterator iter = outputResponseMap.keySet().iterator(); iter
				.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;
	}

	public HashMap balanceEnquiryUsingTrackData(GCPOS pos, String cardNumber, String Amount, String invoiceNumber, String trackData) {
		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
		requestMap.put(SVTags.AMOUNT, Amount);

		boolean activate = pos.balanceEnquiry(requestMap, outputResponseMap);

		System.out.println("balance Enquiry Using TrackData" + activate);

		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}

		return outputResponseMap;
	}

	public HashMap activateCard(GCPOS pos, String cardNumber, String Amount, String invoiceNumber) {

		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
		requestMap.put(SVTags.AMOUNT, Amount);

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);

		boolean response = pos.activate(requestMap, outputResponseMap);

		System.out.println("activateCard" + response);
		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;
	}

	public HashMap activateCardUsingTrackData(GCPOS pos, String cardNumber, String Amount, String invoiceNumber, String trackData, String notes) {

		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
		requestMap.put(SVTags.AMOUNT, Amount);
		requestMap.put(SVTags.TRACK_DATA, trackData);
		requestMap.put(SVTags.NOTES, notes);
		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		//rev 1.1 ends
		boolean response = pos.activate(requestMap, outputResponseMap);

		System.out.println(response);
		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;
	}

//	public HashMap activateCardUsingTrackDatanPin(GCPOS pos, String cardNumber, String Amount, String invoiceNumber, String trackData, String pin) {
//
//		HashMap requestMap = new HashMap();
//		HashMap outputResponseMap = new HashMap();
//
//		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
//		requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
//		requestMap.put(SVTags.AMOUNT, Amount);
//		requestMap.put(SVTags.TRACK_DATA, trackData);
//		requestMap.put(SVTags.CARD_PIN, pin);
//
//		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
//
//		boolean response = pos.activate(requestMap, outputResponseMap);
//
//		System.out.println(response);
//		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
//			String key = (String) iter.next();
//			System.out.println(key + ":" + outputResponseMap.get(key));
//		}
//		return outputResponseMap;
//	}

	

//	public HashMap redeemCard(GCPOS pos, String cardNumber, String Amount, String invoiceNumber, String cardPin) {
//
//		HashMap requestMap = new HashMap();
//		HashMap outputResponseMap = new HashMap();
//
//		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
//		requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
//		requestMap.put(SVTags.AMOUNT, Amount);
//
//		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
//		requestMap.put(SVTags.CARD_PIN, cardPin);
//
//		boolean response = pos.redeem(requestMap, outputResponseMap);
//
//		System.out.println("Redeem Card Using Pin Number "+response);
//		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
//			String key = (String) iter.next();
//			System.out.println(key + ":" + outputResponseMap.get(key));
//		}
//		return outputResponseMap;
//
//	}
	public HashMap redeemCard(GCPOS pos, String cardNumber, String Amount,String billAmount,
			String invoiceNumber) {
		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();
		//requestMap.clear();
		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
		requestMap.put(SVTags.AMOUNT, Amount);
		requestMap.put(SVTags.BILL_AMOUNT, billAmount);
		boolean response = pos.redeem(requestMap, outputResponseMap);
		for (Iterator iter = outputResponseMap.keySet().iterator(); iter
				.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;

	}
	public HashMap redeemCard(GCPOS pos, String cardNumber, String Amount, String invoiceNumber) {

		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
		requestMap.put(SVTags.AMOUNT, Amount);

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		// requestMap.put(SVTags.CARD_PIN, cardPin);

		boolean response = pos.redeem(requestMap, outputResponseMap);

		System.out.println("Redeem Card "+response);
		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;

	}

	public HashMap redeemCardWithBillAmount(GCPOS pos, String cardNumber, String Amount, String invoiceNumber, String billAmount) {

		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
		requestMap.put(SVTags.AMOUNT, Amount);
		requestMap.put(SVTags.BILL_AMOUNT, billAmount);

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		// requestMap.put(SVTags.CARD_PIN, cardPin);

		boolean response = pos.redeem(requestMap, outputResponseMap);

		System.out.println("Redeem Card "+response);
		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;

	}

public HashMap redeemCardWithBillAmount(GCPOS pos, String cardNumber, String Amount, String invoiceNumber, String billAmount, String trackData) {

		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
		requestMap.put(SVTags.AMOUNT, Amount);
		requestMap.put(SVTags.BILL_AMOUNT, billAmount);
		requestMap.put(SVTags.TRACK_DATA, trackData);
		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		// requestMap.put(SVTags.CARD_PIN, cardPin);

		boolean response = pos.redeem(requestMap, outputResponseMap);

		System.out.println("Redeem Card "+response);
		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;

	}
	
	
	public HashMap reloadCardWithBillAmount(GCPOS pos, String cardNumber, String Amount, String invoiceNumber, String billAmount) {

		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
		requestMap.put(SVTags.AMOUNT, Amount);
		requestMap.put(SVTags.BILL_AMOUNT, billAmount);

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		// requestMap.put(SVTags.CARD_PIN, cardPin);

		boolean response = pos.reload(requestMap, outputResponseMap);

		System.out.println("Reload Card "+response);
		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;

	}
//	
//	public HashMap reloadCardWithBillAmountnPin(GCPOS pos, String cardNumber, String Amount, String invoiceNumber, String billAmount, String pin) {
//
//		HashMap requestMap = new HashMap();
//		HashMap outputResponseMap = new HashMap();
//
//		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
//		requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
//		requestMap.put(SVTags.AMOUNT, Amount);
//		requestMap.put(SVTags.BILL_AMOUNT, billAmount);
//		requestMap.put(SVTags.CARD_PIN, pin);
//		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
//		// requestMap.put(SVTags.CARD_PIN, cardPin);
//
//		boolean response = pos.reload(requestMap, outputResponseMap);
//
//		System.out.println("Reload Card "+response);
//		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
//			String key = (String) iter.next();
//			System.out.println(key + ":" + outputResponseMap.get(key));
//		}
//		return outputResponseMap;
//
//	}

//	public HashMap reloadCardWithBillAmountnPin(GCPOS pos, String cardNumber, String Amount, String invoiceNumber, String pin) {
//
//		HashMap requestMap = new HashMap();
//		HashMap outputResponseMap = new HashMap();
//
//		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
//		requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
//		requestMap.put(SVTags.AMOUNT, Amount);
//		//requestMap.put(SVTags.BILL_AMOUNT, Amount);
//		requestMap.put(SVTags.CARD_PIN, pin);
//		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
//		// requestMap.put(SVTags.CARD_PIN, cardPin);
//
//		boolean response = pos.reload(requestMap, outputResponseMap);
//
//		System.out.println("Reload Card "+response);
//		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
//			String key = (String) iter.next();
//			System.out.println(key + ":" + outputResponseMap.get(key));
//		}
//		return outputResponseMap;
//
//	}
	
//	public HashMap redeemCardWithBillAmountNPin(GCPOS pos, String cardNumber, String Amount, String invoiceNumber, String billAmount, String cardPin) {
//
//		HashMap requestMap = new HashMap();
//		HashMap outputResponseMap = new HashMap();
//
//		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
//		requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
//		requestMap.put(SVTags.AMOUNT, Amount);
//		requestMap.put(SVTags.BILL_AMOUNT, billAmount);
//		requestMap.put(SVTags.CARD_PIN, cardPin);
//
//		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
//		// requestMap.put(SVTags.CARD_PIN, cardPin);
//
//		boolean response = pos.redeem(requestMap, outputResponseMap);
//
//		System.out.println("Redeem Card "+response);
//		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
//			String key = (String) iter.next();
//			System.out.println(key + ":" + outputResponseMap.get(key));
//		}
//		return outputResponseMap;
//
//	}
	
	// Changes start for merge build
	public HashMap redeemCardWithBillAmountNPin(GCPOS pos, String cardNumber, String Amount, String invoiceNumber, String billAmount, String cardPin) {

	HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
		requestMap.put(SVTags.AMOUNT, Amount);
		requestMap.put(SVTags.BILL_AMOUNT, billAmount);
		requestMap.put(SVTags.CARD_PIN, cardPin);

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		// requestMap.put(SVTags.CARD_PIN, cardPin);

		boolean response = pos.redeem(requestMap, outputResponseMap);

		System.out.println("Redeem Card "+response);
		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;

	}

	// Changes Ends for merge build

	public HashMap redeemCardUsingTrackData(GCPOS pos, String cardNumber, String Amount,String billAmount,
			String invoiceNumber, String trackData) {
		//requestMap.clear();
		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();
		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
		requestMap.put(SVTags.AMOUNT, Amount);
		requestMap.put(SVTags.TRACK_DATA, trackData);
		/*Rev 1.5 start*/
		double dv = Double.parseDouble(billAmount);
		BigDecimal billAmountRounded = new BigDecimal(dv);
		BigDecimal bigOne = new BigDecimal(1);
		billAmountRounded = billAmountRounded.divide(bigOne, 0, billAmountRounded.ROUND_HALF_UP);
		requestMap.put(SVTags.BILL_AMOUNT, billAmountRounded);
		/*Rev 1.5 End*/
		boolean response = pos.redeem(requestMap, outputResponseMap);
		for (Iterator iter = outputResponseMap.keySet().iterator(); iter
				.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;

	}

	public HashMap reloadLoad(GCPOS pos, String cardNumber, String Amount, String invoiceNumber) {

		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
		requestMap.put(SVTags.AMOUNT, Amount);
		requestMap.put(SVTags.CARD_NUMBER, cardNumber);

		boolean response = pos.reload(requestMap, outputResponseMap);
		System.out.println("Reload Card "+ response);

		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;
	}

	public HashMap reloadLoadUsingTrackData(GCPOS pos, String cardNumber, String Amount, String invoiceNumber, String trackData, String notes) {

		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		requestMap.put(SVTags.INVOICE_NUMBER, invoiceNumber);
		requestMap.put(SVTags.AMOUNT, Amount);
		requestMap.put(SVTags.NOTES, notes);
		requestMap.put(SVTags.TRACK_DATA, trackData);

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);

		boolean response = pos.reload(requestMap, outputResponseMap);

		System.out.println(response);
          outputResponseMap.put("ReloadAmount", Amount);
		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;
	}

	public HashMap batchClose(GCPOS pos) {

		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		boolean response = pos.batchClose(requestMap, outputResponseMap);

		System.out.println("batch Close"+ response);

		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;
	}

	public HashMap CancelActivation(GCPOS pos, MAXGiftCardIfc giftCard) {

		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put(SVTags.CARD_NUMBER, giftCard.getCardNumber().toString());
		requestMap.put(SVTags.ORIGINAL_AMOUNT, giftCard.getCurrentBalance().toString());
		requestMap.put(SVTags.ORIGINAL_APPROVAL_CODE, giftCard.getQcApprovalCode().toString());
		requestMap.put(SVTags.ORIGINAL_BATCH_NUMBER, giftCard.getQcBatchNumber());
		requestMap.put(SVTags.ORIGINAL_INVOICE_NUMBER, giftCard.getQcInvoiceNumber());
		requestMap.put(SVTags.ORIGINAL_TRANSACTION_ID, giftCard.getQcTransactionId());

		boolean response = pos.cancelActivation(requestMap, outputResponseMap);

		System.out.println("Cancel Activation "+response);

		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;

	}

	public HashMap CancelReload(GCPOS pos, MAXGiftCardIfc giftCard) {

		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put(SVTags.CARD_NUMBER, giftCard.getCardNumber().toString());
		requestMap.put(SVTags.ORIGINAL_AMOUNT, giftCard.getCurrentBalance().toString());
		requestMap.put(SVTags.ORIGINAL_APPROVAL_CODE, giftCard.getQcApprovalCode().toString());
		requestMap.put(SVTags.ORIGINAL_BATCH_NUMBER, giftCard.getQcBatchNumber());
		requestMap.put(SVTags.ORIGINAL_INVOICE_NUMBER, giftCard.getQcInvoiceNumber());
		requestMap.put(SVTags.ORIGINAL_TRANSACTION_ID, giftCard.getQcTransactionId());

		boolean response = pos.cancelReload(requestMap, outputResponseMap);

		System.out.println("Cancel Reload"+response);

		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;

	}

	public HashMap balanceEnquiry(GCPOS pos, String cardNumber) {

		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		boolean activate = pos.balanceEnquiry(requestMap, outputResponseMap);

		System.out.println("BalanceEnquiry "+activate);

		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}

		return outputResponseMap;
	}

	public void showQCOfflineErrorBox(BusIfc bus) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		String msg[] = new String[7];
		dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
		msg[0] = "System is Offline";
		msg[1] = " Gift Card Activity Can not be perfomed";
		/*msg[2] = " We have encountered some error in calling the GiftCard API";
		msg[3] = "It seems that QC Server is not available/Network Offline";
		msg[4] = "Please Try after some time :  Press button To Proceed";
		msg[5] = "Auchan India Pvt Ltd";
		msg[6] = "::Thanks::";*/
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
		return;
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

	public HashMap CancelReload(GCPOS pos, MAXGiftCardIfc giftCard, String amount) {
		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();
		requestMap.put(SVTags.CARD_NUMBER, giftCard.getCardNumber().toString());
		requestMap.put(SVTags.ORIGINAL_AMOUNT, amount);
		requestMap.put(SVTags.ORIGINAL_APPROVAL_CODE, giftCard.getQcApprovalCode().toString());
		requestMap.put(SVTags.ORIGINAL_BATCH_NUMBER, giftCard.getQcBatchNumber());
		requestMap.put(SVTags.ORIGINAL_INVOICE_NUMBER, giftCard.getQcInvoiceNumber());
		requestMap.put(SVTags.ORIGINAL_TRANSACTION_ID, giftCard.getQcTransactionId());

		boolean response = pos.cancelReload(requestMap, outputResponseMap);

		System.out.println("Cancel Reload"+ response);

		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;

	}

	public HashMap CancelRedeem(GCPOS pos, MAXGiftCardIfc giftCard, String amount, String cardNumber, String trackData) {
		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();
		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		requestMap.put(SVTags.ORIGINAL_AMOUNT, amount);
		requestMap.put(SVTags.ORIGINAL_APPROVAL_CODE, giftCard.getQcApprovalCode().toString());
		requestMap.put(SVTags.ORIGINAL_BATCH_NUMBER, giftCard.getQcBatchNumber());
		requestMap.put(SVTags.ORIGINAL_INVOICE_NUMBER, giftCard.getQcInvoiceNumber());
		requestMap.put(SVTags.ORIGINAL_TRANSACTION_ID, giftCard.getQcTransactionId());
		if(trackData != null)
		requestMap.put(SVTags.TRACK_DATA, trackData);

		boolean response = pos.cancelRedeem(requestMap, outputResponseMap);

		System.out.println("Cancel Redeem"+ response);

		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;

	}

public HashMap CancelRedeem(GCPOS pos, MAXGiftCardIfc giftCard, String amount) {
		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();
		requestMap.put(SVTags.CARD_NUMBER, giftCard.getCardNumber().toString());
		requestMap.put(SVTags.ORIGINAL_AMOUNT, amount);
		requestMap.put(SVTags.ORIGINAL_APPROVAL_CODE, giftCard.getQcApprovalCode().toString());
		requestMap.put(SVTags.ORIGINAL_BATCH_NUMBER, giftCard.getQcBatchNumber());
		requestMap.put(SVTags.ORIGINAL_INVOICE_NUMBER, giftCard.getQcInvoiceNumber());
		requestMap.put(SVTags.ORIGINAL_TRANSACTION_ID, giftCard.getQcTransactionId());

		boolean response = pos.cancelReload(requestMap, outputResponseMap);

		System.out.println("Cancel Reload"+ response);

		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;

	}

	public HashMap CancelReload(GCPOS pos, MAXGiftCardIfc giftCard, String amount, String cardNumber, String trackData) {
		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();
		requestMap.put(SVTags.CARD_NUMBER, cardNumber);
		requestMap.put(SVTags.ORIGINAL_AMOUNT, amount);
		requestMap.put(SVTags.ORIGINAL_APPROVAL_CODE, giftCard.getQcApprovalCode().toString());
		requestMap.put(SVTags.ORIGINAL_BATCH_NUMBER, giftCard.getQcBatchNumber());
		requestMap.put(SVTags.ORIGINAL_INVOICE_NUMBER, giftCard.getQcInvoiceNumber());
		requestMap.put(SVTags.ORIGINAL_TRANSACTION_ID, giftCard.getQcTransactionId());
		requestMap.put(SVTags.TRACK_DATA, trackData);

		boolean response = pos.cancelReload(requestMap, outputResponseMap);

		System.out.println("Cancel Reload"+ response);

		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + ":" + outputResponseMap.get(key));
		}
		return outputResponseMap;

	}

	public void ShowInvalidCard(String screenID, HashMap balanceEnquiryMap, MAXGiftCardIfc giftCard, BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);		
		String msg[] = new String[7];
		dialogModel.setResourceID(screenID);
		msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
		String cardNum = giftCard.getCardNumber();
		String cardnumber = cardNum.substring(cardNum.length() - 4, cardNum.length());
		msg[1] = "GIFTCard" + " " + cardnumber;
		msg[2] = "";   //" Total Amount In This Card Is " + balanceEnquiryMap.get("Amount");
		msg[3] = "";   //"ExpiryDate : N/A ";
		msg[4] = balanceEnquiryMap.get("ResponseMessage").toString();
		if (balanceEnquiryMap.get("AcquirerId") != null)
			msg[5] = balanceEnquiryMap.get("AcquirerId").toString();
		else {
			msg[5] = "Could Not Validate Card, Please try again/ Use CSD";
		}
		msg[6] = "::Thanks::";
		dialogModel.setArgs(msg);
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "TenderSale");
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		
	}
	
	public void ShowInvalidCardTender(String screenID, HashMap balanceEnquiryMap, MAXGiftCardIfc giftCard, BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);		
		String msg[] = new String[7];
		dialogModel.setResourceID(screenID);
		msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
		String cardNum = balanceEnquiryMap.get("CardNumber").toString();
		String cardnumber = cardNum.substring(cardNum.length() - 4, cardNum.length());
		msg[1] = "GIFTCard" + " " + cardnumber;
		msg[2] = balanceEnquiryMap.get("ResponseMessage").toString();   //" Total Amount In This Card Is " + balanceEnquiryMap.get("Amount");
		//msg[3] = "";   //"ExpiryDate : N/A ";
		//msg[4] = balanceEnquiryMap.get("ResponseMessage").toString();
		if (balanceEnquiryMap.get("AcquirerId") != null)
			msg[3] = balanceEnquiryMap.get("AcquirerId").toString();
		else {
			msg[3] = "Press enter to delete GC tender line item and select another tender type.";
		}
		msg[4] = "::Thanks::";
		msg[5] = " ";
		msg[6] = " ";
		dialogModel.setArgs(msg);
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "TenderSaleT");
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		
	}
	public void ShowInvalidCard(String screenID, HashMap balanceEnquiryMap,  BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);		
		String msg[] = new String[7];
		dialogModel.setResourceID(screenID);
		msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";	
		
		msg[1] = "GIFTCard" ;
		msg[2] = " " ;
		msg[3] = "";
		msg[4] = balanceEnquiryMap.get("ResponseMessage").toString();
		if (balanceEnquiryMap.get("AcquirerId") != null)
			msg[5] = balanceEnquiryMap.get("AcquirerId").toString();
		else {
			msg[5] = "Could Not Validate Card, Please try again/ Use CSD";
		}
		msg[6] = "::Thanks::";
		dialogModel.setArgs(msg);
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Continue");
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

	}

	public void SetValuesInGiftCard(MAXGiftCardIfc giftCard, HashMap balanceEnquiryMap) {

		giftCard.setApprovalCode(balanceEnquiryMap.get("ApprovalCode").toString());
		giftCard.setExpirationDate(calculateEYSDate(balanceEnquiryMap.get("Expiry").toString()));
		giftCard.setSettlementData(balanceEnquiryMap.get("CardType").toString());

		giftCard.setQcApprovalCode(balanceEnquiryMap.get("ApprovalCode").toString());
		if(balanceEnquiryMap.get("InvoiceNumber")!= null && !(("null").equals(balanceEnquiryMap.get("InvoiceNumber"))))
		giftCard.setQcInvoiceNumber(balanceEnquiryMap.get("InvoiceNumber").toString());
		giftCard.setQcTransactionId(balanceEnquiryMap.get("TransactionId").toString());
		giftCard.setQcBatchNumber(balanceEnquiryMap.get("CurrentBatchNumber").toString());
		giftCard.setQcCardType(balanceEnquiryMap.get("CardType").toString());
		CurrencyIfc amt = DomainGateway.getBaseCurrencyInstance(balanceEnquiryMap.get("Amount").toString());

		//giftCard.setInitialBalance(giftCard.getInitialBalance());
		giftCard.setCurrentBalance(amt);

	}
	
	public void ShowValidCard(String screenID, HashMap balanceEnquiryMap, MAXGiftCardIfc giftCard, BusIfc bus)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		String ResponseCode="N/A";
		String ResponseMessage="N/A";
		
		
		if (balanceEnquiryMap.get("ApprovalCode") != null && !(("null").equals(balanceEnquiryMap.get("ApprovalCode"))))
		giftCard.setApprovalCode(balanceEnquiryMap.get("ApprovalCode").toString());
		if (balanceEnquiryMap.get("Expiry") != null && !(("null").equals(balanceEnquiryMap.get("Expiry"))))
		giftCard.setExpirationDate(calculateEYSDate(balanceEnquiryMap.get("Expiry").toString()));
		if (balanceEnquiryMap.get("CardType") != null && !(("null").equals(balanceEnquiryMap.get("CardType"))))
		giftCard.setSettlementData(balanceEnquiryMap.get("CardType").toString());
		if (balanceEnquiryMap.get("ApprovalCode") != null && !(("null").equals(balanceEnquiryMap.get("ApprovalCode"))))
		giftCard.setQcApprovalCode(balanceEnquiryMap.get("ApprovalCode").toString());
		if (balanceEnquiryMap.get("InvoiceNumber") != null && !(("null").equals(balanceEnquiryMap.get("InvoiceNumber"))))
		giftCard.setQcInvoiceNumber(balanceEnquiryMap.get("InvoiceNumber").toString());
		if (balanceEnquiryMap.get("TransactionId") != null && !(("null").equals(balanceEnquiryMap.get("TransactionId"))))
		giftCard.setQcTransactionId(balanceEnquiryMap.get("TransactionId").toString());
		if (balanceEnquiryMap.get("CurrentBatchNumber") != null && !(("null").equals(balanceEnquiryMap.get("CurrentBatchNumber"))))
		giftCard.setQcBatchNumber(balanceEnquiryMap.get("CurrentBatchNumber").toString());
		if (balanceEnquiryMap.get("CardType") != null && !(("null").equals(balanceEnquiryMap.get("CardType"))))
		giftCard.setQcCardType(balanceEnquiryMap.get("CardType").toString());
		if(balanceEnquiryMap.containsKey("ResponseCode") && balanceEnquiryMap.get("ResponseCode")!=null && !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseCode")) && !"".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseCode"))){
			ResponseCode=(String) balanceEnquiryMap.get("ResponseCode");
		}
		if(balanceEnquiryMap.containsKey("ResponseMessage") && balanceEnquiryMap.get("ResponseMessage")!=null && !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseMessage")) && !"".equalsIgnoreCase((String) balanceEnquiryMap.get("ResponseMessage"))){
			ResponseMessage=(String) balanceEnquiryMap.get("ResponseMessage");
		}
		

		
		String msg[] = new String[7];
		dialogModel.setResourceID(screenID);
		msg[0] = "<<--||--:: Please Find The GiftCard Details As Below ::--||-->>";
		String cardNum = giftCard.getCardNumber();
		String cardnumber = cardNum.substring(cardNum.length() - 4, cardNum.length());
		msg[1] = "GIFTCard" + " " + cardnumber;
		if (balanceEnquiryMap.get("CardCurrencySymbol") != null && !(("null").equals(balanceEnquiryMap.get("CardCurrencySymbol"))) 
				&& balanceEnquiryMap.get("CardCurrencySymbol") != null && !(("null").equals(balanceEnquiryMap.get("CardCurrencySymbol"))))
		msg[2] = " Total Amount In This Card Is " + "" + balanceEnquiryMap.get("CardCurrencySymbol").toString() + balanceEnquiryMap.get("Amount").toString() + " ";
		else if(balanceEnquiryMap.get("Amount") != null && !(("null").equals(balanceEnquiryMap.get("Amount"))) )
		msg[2] = " Total Amount In This Card Is " + "" + balanceEnquiryMap.get("Amount").toString() + " ";
		else
		msg[2] = " Total Amount In This Card Is N/A";	
		if (balanceEnquiryMap.get("Expiry") != null && !(("null").equals(balanceEnquiryMap.get("Expiry"))))		
		msg[3] = "ExpiryDate " + calculateEYSDate(balanceEnquiryMap.get("Expiry").toString());
		else
		msg[3] = "ExpiryDate " + "N/A";
		/*if(!ResponseCode.equalsIgnoreCase("10027") && !ResponseCode.equalsIgnoreCase("10001") && !ResponseCode.equalsIgnoreCase("10021") && !ResponseCode.equalsIgnoreCase("10249") )
		msg[4] = "Request Successfull:  Press button To Proceed";
		else if(ResponseCode.equalsIgnoreCase("10027") || ResponseCode.equalsIgnoreCase("10021"))
		msg[4] = "Card is Deactivated:  Press button To Proceed";
		else if(ResponseCode.equalsIgnoreCase("10001"))
		msg[4] = "Card is Expired:  Press button To Proceed";
		*/
		if(ResponseCode.trim().equalsIgnoreCase("0") ){
			msg[4] = "Request Successfull:  Press button To Proceed";	
		}else if(ResponseMessage!=null && !ResponseMessage.trim().equalsIgnoreCase("") && !ResponseMessage.trim().equalsIgnoreCase("N/A")){
			msg[4] = ResponseMessage  +": Press button To Proceed";
		}
		
		
		if (balanceEnquiryMap.get("CardType") != null && !(("null").equals(balanceEnquiryMap.get("CardType"))))
		msg[5] = balanceEnquiryMap.get("CardType").toString();
		else
		msg[5] ="Card Type is N/A";	
		msg[6] = "::Thanks::";
		// Rev 1.2 changes starts
		dialogModel.setArgs(msg);
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
	/*	if(!ResponseCode.equalsIgnoreCase("10027") && !ResponseCode.equalsIgnoreCase("10001"))*/
		//dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Continue");
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		bus.mail("Continue", BusIfc.CURRENT);
		// Rev 1.2 changes Ends
	}
	
	public void SetValuesInTenderGiftCard(TenderGiftCardIfc tgf, HashMap balanceEnquiryMap) 
	{
		//MGTenderGiftCardIfc tgc = (MGTenderGiftCardIfc) tgf;

		//tgc.setQcApprovalCode(balanceEnquiryMap.get("ApprovalCode").toString());
	//	tgc.setQcType(balanceEnquiryMap.get("CardType").toString());
		//tgc.setQcExpiryDate(calculateEYSDate(balanceEnquiryMap.get("Expiry").toString()));
		//tgc.setQcTransId(balanceEnquiryMap.get("TransactionId").toString());
			
	}

	public void individualslipforeveryGiftcard(BusIfc bus, HashMap response, String action) {

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
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception.....ooooooooooops");

		}

	}
	// Bhanu Priya Changes starts
	public String getFormattedReportForGiftCard(HashMap map, String action) {
		StringBuffer buff = new StringBuffer();
		StringBuffer starPrintAsGCNumber=new StringBuffer();
		//buff.append("***************************************");
		// buff.append(RegisterReport.NEW_LINE);
		//buff.append(System.getProperty("line.separator"));
		// buff.append(RegisterReport.NEW_LINE);
		// String data = map.get("PrintLine" + i).toString();
	//	formatting("GiftCard", buff);
		//buff.append(RegisterReport.NEW_LINE);
		//formatting(action, buff);
		//buff.append(RegisterReport.NEW_LINE);
		formatting(action + "GiftCard Receipt", buff);
		// Priyanka
		String data0 = "";
		String cardNum = null;
		if (map.get("CardNumber") != null)
			cardNum = map.get("CardNumber").toString();
		data0 = cardNum.substring(cardNum.length() - 4, cardNum.length());
		
		// Rev 1.4
		for(int i=0;i<cardNum.length()-4;i++){
			starPrintAsGCNumber.append('*');
		}
		//rev 1.1 starts
//		formatting("CardNumber:" + data0, buff);
		formatting("Card Number:" +starPrintAsGCNumber+""+data0, buff);
		//rev 1.1 ends 
		// String data1 = map.get("CardHolderName").toString();
		// formatting("Name:" + data1, buff);
		EYSDate data2 = DomainGateway.getFactory().getEYSDateInstance();
		if (map.get("Expiry") != null && !("null").equals(map.get("Expiry").toString()))
			data2 = calculateEYSDate(map.get("Expiry").toString());
		// String data2 = map.get("Expiry").toString();
		formatting("ExpiryDate:" + data2, buff);
		String data3 = "";
		if (map.get("ResponseMessage") != null)
			data3 = action + " " + map.get("ResponseMessage").toString();
		formatting(data3, buff);
		String data4 = "";
		String data5 = "";
		if (map.get("PreviousBalance") != null || map.get("Amount") != null)
			data4 = map.get("PreviousBalance").toString();
		data5 = map.get("Amount").toString();
		formatting("Pre. Bal:" + data4 + "  Curr Bal:" + data5, buff);
		
		/*double actionAmount=diffAmount(data4,data5);
		BigDecimal actionAmountBD = new BigDecimal(actionAmount);
		CurrencyIfc finalActionAmount = DomainGateway.getBaseCurrencyInstance(actionAmountBD);*/

		 //Rev 1.4
		String finalActionAmount = map.get("RedeemedAmount").toString();
		
		formatting("Redeemed" +" Amt:" + finalActionAmount, buff);
		// Rev 1.4
		// String data6 = map.get("TransactionId").toString();
		// formatting("Trx ID:" + data6, buff);
		// String data7 = map.get("CurrentBatchNumber").toString();
		// formatting("BatchID:" + data7, buff);
	//	String data8 = map.get("CardType").toString();
	//	formatting(data8, buff);
		String data9 = "THANK YOU";
		formatting(data9, buff);
		//buff.append("***************************************");

		return (buff.toString());
	}
	// Bhanu Priya Changes Ends
	public void formatting(String data, StringBuffer buff) {
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
		/*
		 * if(("THANK YOU").equals(data.trim())) {
		 * buff.append(RegisterReport.NEW_LINE);
		 * buff.append(RegisterReport.NEW_LINE);
		 * buff.append(System.getProperty("line.separator"));
		 * 
		 * 
		 * }
		 */
	}
	
	protected String getFormattedLine(String descString, String countString, String moneyString) {

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
	
   double diffAmount(String data4,String data5){
		
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

	private String replaceComman(String data5) {
		while(data5.indexOf(",")!=-1){
			String replaceFirst = data5.replaceFirst(",", "");
			data5=replaceFirst;
		}
		return data5;
	}

// Changes Start For merged Build 
	
	public HashMap balanceEnquiryUsingPin(GCPOS pos, String cardNumber, String pinNumber ,String notes) {
		HashMap requestMap = new HashMap();
		HashMap outputResponseMap = new HashMap();

		requestMap.put("CardNumber", cardNumber);
		requestMap.put(SVTags.NOTES, notes);
		requestMap.put("CardPIN", pinNumber);


		boolean activate = pos.balanceEnquiry(requestMap, outputResponseMap);

		// System.out.println("Balance Enquiry using Pin " + activate);

		for (Iterator iter = outputResponseMap.keySet().iterator(); iter.hasNext(); ) {
			String key = (String)iter.next();
			// System.out.println(key + ":" + outputResponseMap.get(key));
		}

		return outputResponseMap;
	}
// Changes end for merge build 

}
