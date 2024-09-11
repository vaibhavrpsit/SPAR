/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*    Rev 1.4  08/08/2013     Jyoti Rawal, Changed the Gift Card Tender flow
* Rev 1.3  23/Jul/2013	Jyoti Rawal, Fix for Bug 7208 - GC- Unable to POST Void transaction tendered by GC
* Rev 1.2  17/June/2013	Jyoti Rawal, Fix for Bug 6394 Credit Charge Slip is not getting printed
* Rev 1.1  07/Jun/2013	Jyoti Rawal, Bug 6042 Credit/Debit Tender- Post Void Transaction Message is not proper 
*  Rev 1.0  28/May/2013	Jyoti Rawal, Initial Draft: Changes for Credit Card Functionality 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.postvoid;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import com.qwikcilver.clientapi.svpos.GCPOS;

import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.domain.utility.MAXGiftCard;
import max.retail.stores.pos.ado.tender.MAXTenderCreditADO;
import max.retail.stores.pos.ado.tender.MAXTenderGiftCardADO;
import max.retail.stores.pos.services.qc.MAXGiftCardUtilitiesQC;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderGiftCard;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.ResourceBundleUtil;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ado.context.ADOContextIfc;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptConstantsIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.postvoid.VoidCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXVoidAuthorizationSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8215853264798483808L;
	private String transactionType;
	private String cardExpDate;
	private final String dialogId = null;
	String[] header = new String[] {};
	String[] footer = new String[] {};

	public void arrive(BusIfc bus) {

		POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
		// String letter = "ExitTender";
		String letter = "Success";

		DialogBeanModel model = new DialogBeanModel();
		VoidCargo cargo = (VoidCargo) bus.getCargo();

		TenderADOIfc[] tndado = cargo.getOriginalTransactionADO()
				.getTenderLineItems(TenderLineItemCategoryEnum.ALL);

		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);

		for (int i = 0; i < tndado.length; i++) {
			//System.out.println(tndado[i]);
			if (tndado[i] instanceof MAXTenderCreditADO
					&& !((MAXTenderCreditADO)tndado[i]).isVoided()) {
				HashMap tenderAttri = tndado[i].getTenderAttributes();
				// online credit/debit tender check
				if (!((String) tenderAttri.get(TenderConstants.AUTH_METHOD))
						.equalsIgnoreCase("ONLINE")) {

					MAXTenderChargeIfc creditTender = (MAXTenderChargeIfc) tndado[i]
					                                                        .toLegacy();
					creditTender.setTransactionType(transactionType);

					String amount = creditTender.getAmountTender()
					.getStringValue();
					// String transactionNO = transactionRDO.getTransactionID();
					String invoiceNo = creditTender.getInvoiceNumber();
					String bankcode = creditTender.getAcquiringBankCode();
					cardExpDate = creditTender.getExpirationDateString();
					String transactionID = cargo.getOriginalTransactionADO().getTransactionID();
					cargo.getOriginalTransactionID();
					double l1 = Double.parseDouble(amount);
					System.out.println(" The total amount is"+l1);
					l1 = l1*100;
					long l2 = (new Double(l1)).longValue();
					System.out.println(" The total amount is"+l2);
//					l = 5000;
					String total1 = String.valueOf(l2);
					String requestString = "4006"+","+"T1/"+transactionID+","+total1+","+creditTender.getAcquiringBankCode()+","+","+","+creditTender.getInvoiceNumber()+","+","+","+","+",";  //Rev 1.2 changes
				try {
//					ProcessTransaction(requestString);
					System.out.println("Request is == " + requestString);
					SocketAddress sockaddr = new InetSocketAddress("127.0.0.1", 8082);
					Socket clientSocket = new Socket();
					clientSocket.connect(sockaddr, 180000); // 2nd parameter is timeout.
					InputStream in = clientSocket.getInputStream();
					OutputStream out = clientSocket.getOutputStream();
					out.flush();
					// the first argument is not being used anywhere
					// tx type is covered as a a part of the csv itself.
					out.write(GetTransmissionPacketForCentral(0, requestString));
					byte bFirstByte = (byte) in.read();
					byte bInnerbytes;
					for (int l = 0; l < 5; l++) {
						bInnerbytes = (byte) in.read();
					}

					byte[] lengthBytes = new byte[2];
					for (int l = 0; l < 2; l++) {
						lengthBytes[l] = (byte) in.read();
					}

					int responseLength = -1;
					try {
						responseLength = Integer.parseInt(bcd2a(lengthBytes));

						responseLength = lengthBytes[0];
						responseLength = (responseLength << 8);
						responseLength |= lengthBytes[1];

					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					byte[] responseCSV = new byte[responseLength];
					for (int k = 0; k < responseLength; k++) {
						responseCSV[k] = (byte) in.read();
					}
					byte rEtx = (byte) in.read();
					String str = null;
					// if(rEtx != 0xFF)
					if (rEtx != -1) {
						System.out.println("Invalid end sentinel recieved");
						return;
					} else {
						 str = new String(responseCSV);
						System.out.println("response csv:" + str);
					}
					if (str!= null && str.indexOf("APPROVED") != -1) {
						((MAXTenderCreditADO) tndado[i]).setVoided(true);
						//Rev 1.2 changes start
						boolean creditSlip = false;
						tndado[i].getTenderAttributes().put(
								"TRANSACTION_TYPE", transactionType);
						ParameterManagerIfc pm = (ParameterManagerIfc) bus
						.getManager(ParameterManagerIfc.TYPE);
						if(pm.getStringValue("PrintCreditChargeSlip").equalsIgnoreCase("Y")){
							creditSlip = true;
						}
						if(creditSlip == true){
							try{
								//Changes done for code merging(commenting below lines for error resolving)
						/*MAXCreditChargeSlipReciept chargeSlip = new MAXCreditChargeSlipReciept(
								cargo.getCurrentTransactionADO(), tndado[i]
								                                         .getTenderAttributes(), header,
								                                         footer, "", "POST VOID");*/
						// retrieve receipt locale
						Locale locale = LocaleMap
						.getLocale(LocaleConstantsIfc.RECEIPT);
						
						// get properties for receipt
						Properties props = ResourceBundleUtil
						.getGroupText(
								"receipt",
								ReceiptConstantsIfc.RECEIPT_BUNDLES,
								locale);
						//Changes done for code merging(commenting below lines for error resolving)
						/*chargeSlip.setProps(props);
						pda.printDocument(chargeSlip);

						// For Customer Copy

						chargeSlip = new MAXCreditChargeSlipReciept(cargo
								.getCurrentTransactionADO(), tndado[i]
								                                    .getTenderAttributes(), header, footer,
								                                    "Customer", "POST VOID");*/
						// retrieve receipt locale
						locale = LocaleMap
						.getLocale(LocaleConstantsIfc.RECEIPT);
						// get properties for receipt
						props = ResourceBundleUtil
						.getGroupText(
								"receipt",
								ReceiptConstantsIfc.RECEIPT_BUNDLES,
								locale);
						//Changes done for code merging(commenting below lines for error resolving)
						/*chargeSlip.setProps(props);
						pda.printDocument(chargeSlip);*/

						// Update printer status
						ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS,
								POSUIManagerIfc.ONLINE);
						}
					 //catch (DeviceException e) {
							catch(Exception e){
						logger.warn("Unable to print debit slip. "
								+ e.getMessage() + "");

						// Update printer status
						ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS,
								POSUIManagerIfc.OFFLINE);
						//Changes done for code merging(commenting below lines for error resolving)
						/*if (e.getOrigException() != null) {
							logger
							.warn("DeviceException.NestedException:\n"
									+ Util.throwableToString(e
											.getOrigException())
											+ "");
						}*/

						String msg[] = new String[1];
						UtilityManagerIfc utility = (UtilityManagerIfc) bus
						.getManager(UtilityManagerIfc.TYPE);
						msg[0] = utility.retrieveDialogText(
								"RetryContinue.PrinterOffline",
						"Printer is offline.");

						/* DialogBeanModel model = new DialogBeanModel(); */
						model.setResourceID("RetryContinue");
						model.setType(DialogScreensIfc.RETRY_CONTINUE);
						model.setButtonLetter(
								DialogScreensIfc.BUTTON_RETRY, "Retry");
						model.setButtonLetter(
								DialogScreensIfc.BUTTON_CONTINUE,
						"Continue");
						model.setArgs(msg);
						// display dialog
						//ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
							//	model);
						return;
					}
					}	
					}
					//Rev 1/2 changes end
					else if (!str.equals("") 
							&& str.indexOf("APPROVED") < 0){
					 if (str
							.indexOf("No transaction available with this invoice number") > 0) {
						displayContinueCancelDialog(cargo, tndado[i]);
						return;

					} else if (str.indexOf("Failed") >-1&& str.indexOf("Failed") <1) {
						displayContinueCancelDialog(cargo, tndado[i]);
						cargo.setNextTender(tndado[i]);
						model.setArgs(new String[] { str });
						model.setResourceID("CreditUnknownDeclinedReason");
						model.setType(DialogScreensIfc.RETRY_CANCEL);
						model.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
						"Retry");
						model.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,
						"Cancel");
						ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
						return;
					}else if (str.indexOf("Failed") >1) {
//						displayContinueCancelDialog(cargo, tndado[i]);
						cargo.setNextTender(tndado[i]);
						String finalString = str.substring(str.indexOf("Failed"));
						model.setArgs(new String[] { finalString });
						model.setResourceID("CreditUnknownDeclinedReason");
						model.setType(DialogScreensIfc.RETRY_CANCEL);
						model.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
						"Retry");
						model.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,
						"Cancel");
						ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
						return;
					} 
					else if (!str.equalsIgnoreCase("")) {
						// Added Dialog for handling Unknown Plutus Responses 
						cargo.setNextTender(tndado[i]);
						model.setArgs(new String[] { str });
						model.setResourceID("CreditUnknownDeclinedReason");
						model.setType(DialogScreensIfc.RETRY_CANCEL);
						model.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
						"Retry");
						model.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,
						"Cancel");
						ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
						return;
					}
					}
					else {
						displayContinueCancelDialog(cargo, tndado[i]);
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}


				} else {
					((MAXTenderCreditADO) tndado[i]).setVoided(true);
					//displayDialog(ui, "ManualTenderLineVoid",DialogScreensIfc.ERROR, "Voided");
					

                    bus.mail("Voided");
					return;

				}
			}else if (tndado[i] instanceof MAXTenderGiftCardADO
					&& !((MAXTenderGiftCardADO)tndado[i]).isVoided()) {
					//Rev 1.3 changes start
				TenderGiftCard giftTender = (TenderGiftCard) tndado[i].toLegacy();
				MAXGiftCard gc = (MAXGiftCard) giftTender.getGiftCard();
				gc.getQcBatchNumber();
				gc.getQcInvoiceNumber();
//				gc.getQcTransactionId(); //Rev 1.4 changes
//				gc.getQcApprovalCode();
				MAXGiftCardUtilitiesQC utilObj = new MAXGiftCardUtilitiesQC();
	    		GCPOS pos = utilObj.getInstance();
	    		HashMap balanceEnquiryMap = null;
	    		if(pos!=null){
	    		balanceEnquiryMap =	utilObj.CancelRedeem(pos, gc, giftTender.getAmountTender().toString(),giftTender.getCardNumber(), null);
	    		
	    		String CardNumber="N/A";
				String ResponseMessage="N/A";
				String ResponseCode="N/A";		
				String CardCurrencySymbol="N/A";
				String Amount="N/A";
				String Expiry="N/A";
				String CardType="N/A";
				String AcquirerId="Could Not Validate Card";
				String PreviousBalance="N/A";
				
				
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
						if(balanceEnquiryMap.containsKey("PreviousBalance") && balanceEnquiryMap.get("PreviousBalance")!=null && !"null".equalsIgnoreCase((String) balanceEnquiryMap.get("PreviousBalance")) && !"".equalsIgnoreCase((String) balanceEnquiryMap.get("PreviousBalance"))){
							PreviousBalance=(String) balanceEnquiryMap.get("CardNumber");
						}
				}
				
			        		if(ResponseCode.equalsIgnoreCase("0")){
			        			utilObj.individualslipforeveryGiftcard(bus, balanceEnquiryMap, "Void");
			        			utilObj.SetValuesInGiftCard(gc, balanceEnquiryMap);
			        			CurrencyIfc prevAmt = DomainGateway.getBaseCurrencyInstance(PreviousBalance);
			        			CurrencyIfc amt = DomainGateway.getBaseCurrencyInstance(Amount);
			        			
			        			gc.setInitialBalance(prevAmt);
			        			gc.setCurrentBalance(amt);
			        			giftTender.getGiftCard().setInitialBalance(prevAmt);
			        			giftTender.getGiftCard().setCurrentBalance(amt);
			        		}
			        		if(!(ResponseCode.equalsIgnoreCase("0"))){
			        			DialogBeanModel dialogModel = new DialogBeanModel();
			        			
			        			String msg[] = new String[7];
			        			dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
			        			msg[0] = "<<--||--:: Please Find The Error Details As Below ::--||-->>";
			        			msg[1] = "GIFTCard" + " " +CardNumber;
			        			msg[2] = " Total Amount In This Card Is "+ Amount + " ";
			        			if (balanceEnquiryMap.get("Expiry") != null && !("null").equals(balanceEnquiryMap.get("Expiry")))
			        			msg[3] = "Expiry Date " +utilObj.calculateEYSDate(balanceEnquiryMap.get("Expiry").toString());
			        			else
			        			msg[3] = "Expiry Date "+ Expiry;
			        			msg[4] = ResponseMessage;
			        			msg[6] = "::Thanks::";
			        			dialogModel.setArgs(msg);
			        			dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
			        			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
			        			//offlineFlag = true;
			        			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			        			return;
			        		}
	    		
	    		
	    		
	    		
	    		
	    	/*	if(balanceEnquiryMap!=null){
	        		balanceEnquiryMap.get("ResponseMessage").toString();
	        		String resp = balanceEnquiryMap.get("ResponseCode").toString();
					//Rev 1.4 changes
	        		if(resp.equalsIgnoreCase("0")){
	        			utilObj.individualslipforeveryGiftcard(bus, balanceEnquiryMap, "Void");
	        			utilObj.SetValuesInGiftCard(gc, balanceEnquiryMap);
	        			String peviousBalance = balanceEnquiryMap.get("PreviousBalance").toString();
	        			String amount = balanceEnquiryMap.get("Amount").toString();
	        			
	        			// String cardType = balanceEnquiryMap.get("CardType").toString();
	        			CurrencyIfc prevAmt = DomainGateway.getBaseCurrencyInstance(peviousBalance);
	        			CurrencyIfc amt = DomainGateway.getBaseCurrencyInstance(amount);
	        			
	        			gc.setInitialBalance(prevAmt);
	        			gc.setCurrentBalance(amt);
	        			giftTender.getGiftCard().setInitialBalance(prevAmt);
	        			giftTender.getGiftCard().setCurrentBalance(amt);
	        		}
	        		if(!(resp.equalsIgnoreCase("0"))){
	        			DialogBeanModel dialogModel = new DialogBeanModel();
	        			
	        			String msg[] = new String[7];
	        			dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
	        			msg[0] = "<<--||--:: Please Find The Error Details As Below ::--||-->>";
	        			msg[1] = "GIFTCard" + "" + " Request API Error";
	        			msg[2] = " We have encountered some error in calling the GiftCard API";
	        			msg[3] = "It seems that QC Server is not available";
	        			msg[4] = "Please Try after some time :  Press button To Proceed";
	        			msg[5] = "Lifestyle India Pvt Ltd";
	        			msg[6] = "::Thanks::";
	        			dialogModel.setArgs(msg);
	        			dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
	        			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
	        			//offlineFlag = true;
	        			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	        			return;
	        		}
	        		}*/
			}else{
				DialogBeanModel dialogModel = new DialogBeanModel();
    			
    			String msg[] = new String[7];
    			dialogModel.setResourceID("GIFTCARD_ENQUIRYQC");
    			msg[0] = "<<--||--:: Please Find The Error Details As Below ::--||-->>";
    			msg[1] = "GIFTCard" + "" + " Request API Error";
    			msg[2] = " We have encountered some error in calling the GiftCard API";
    			msg[3] = "It seems that QC Server is not available";
    			msg[4] = "Please Try after some time :  Press button To Proceed";
    			msg[5] = "Auchan India Pvt Ltd";
    			msg[6] = "::Thanks::";
    			dialogModel.setArgs(msg);
    			dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
    			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
    			//offlineFlag = true;
    			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    			return;
			}
			} //Rev 1.3 changes end
			else {
				continue;
			}

		}

		bus.mail("Success");
	}

	protected void displayContinueCancelDialog(VoidCargo cargo,
			TenderADOIfc tndado) {

		cargo.setNextTender(tndado);
		ADOContextIfc context = ContextFactory.getInstance().getContext();

		POSUIManagerIfc ui = (POSUIManagerIfc) context
				.getManager(UIManagerIfc.TYPE);
		DialogBeanModel model = new DialogBeanModel();
		model.setResourceID("PlutusRetryVoid");
		model.setType(DialogScreensIfc.RETRY_CONTINUE);
		model.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Retry");
		model.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Process");
		ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
		return;
	}

	protected void displayDialog(POSUIManagerIfc ui, String name,
			int dialogType, String letter) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(name);
		dialogModel.setType(dialogType);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, letter);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	private byte[] GetTransmissionPacketForCentral(int txnType, String csvData)
	{
		int iOffset = 0;
		byte[] msgBytes = csvData.getBytes();
		int iCSVLen = msgBytes.length;
		int finalMsgLen = iCSVLen + 7; // 7 = 2 byte source , 2 byte function code, 2 byte length, 1 byte termination
		byte[] msgBytesExtra = new byte[finalMsgLen];
		
		//source id - 2 bytes
		msgBytesExtra[iOffset] = 0x10; iOffset++;
		msgBytesExtra[iOffset] = 0x00; iOffset++;
		//function code or MTI - 2 bytes
		msgBytesExtra[iOffset] = 0x09; iOffset++;
		msgBytesExtra[iOffset] = (byte)0x97; iOffset++;
		//data length to follow
		msgBytesExtra[iOffset] = (byte)((byte)(iCSVLen >> 8) & 0xFF); iOffset++;
		msgBytesExtra[iOffset] = (byte)(iCSVLen & 0xFF); iOffset++;
		//
		System.arraycopy(msgBytes,0,msgBytesExtra,iOffset,msgBytes.length);
		iOffset += msgBytes.length;
		msgBytesExtra[iOffset] = (byte)0xFF; iOffset++;
		
		// System.out.println(byteArrayToHexString(msgBytesExtra));
		return msgBytesExtra;
	}
	public static String byteArrayToHexString(byte in[]) {
		byte ch = 0x00;
		int i = 0;
		if (in == null || in.length <= 0)
			return null;

		String pseudo[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
		StringBuffer out = new StringBuffer(in.length * 2);

		while (i < in.length) {
			ch = (byte) (in[i] & 0xF0); // Strip off high nibble
			ch = (byte) (ch >>> 4);   // shift the bits down
			ch = (byte) (ch & 0x0F);  // must do this is high order bit is on!
			out.append(pseudo[ (int) ch]); // convert the nibble to a String Character
			ch = (byte) (in[i] & 0x0F); // Strip off low nibble
			out.append(pseudo[ (int) ch]); // convert the nibble to a String Character
			i++;
		}
		String rslt = new String(out);
		return rslt;
	}
	public static String bcd2a(byte[] src)
	{
		String dest = "";
		int len = src.length;
		byte b1;
		byte b2;
		for (int i = 0; i < len; i++)
		{
			b1 = src[i];
			b1 = (byte)(b1 & 0x0F);
			b2 = (byte)(src[i] >>> 4);
			b2 = (byte)(b2 & 0x0F);
			dest = dest + (int)b2 + (int)b1;
		}
		return dest;
	}
}
