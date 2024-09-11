/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.1  	10 Jan, 2017              Ashish Yadav              Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXCreditDebitTenderWithEDCSite extends PosSiteActionAdapter{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8876180447240798387L;
	
	private String bankAcquirer;
	private String cardNumber = null;
	private String maskedCardNumber = null;
	private String invoiceNumber;
	private String approvalCode;
	private String cardHolder;
	private String expirationDate;
	private String hostResponse;
	private String cardEntryMode;
	private String cardType;
	private String batchNumber;
	private String merchAddress;
	private String merchCity;
	private String merchName;
	private String transAcquirer;
	private String acquirerBankCode;
	private String authRemark;
	private String merchId;
	private String retrievalRefNo;
	private String transactionType;
	private String tId;
	private String loyaltyPoint;
	private String resourceId;
	private static final String REFUND_LABEL = "REFUND";
	private String req_str = "";
	private final String cardHolderName = "";
	private String batchString ;
	public void arrive(BusIfc bus) {
		//ProcessTransaction();
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
		.getManager(UIManagerIfc.TYPE);
		DialogBeanModel model = new DialogBeanModel();

		RetailTransactionADOIfc saleTraADO = cargo.getCurrentTransactionADO();
		SaleReturnTransactionIfc saleTraRDO;
		LayawayPaymentTransactionIfc layawaypaymentTraRDO;
		VoidTransactionIfc voidTrans;
		String tracsactionID = "";

		int traType = 0;

		TransactionIfc trn = (TransactionIfc) saleTraADO.toLegacy();
		if (trn instanceof SaleReturnTransaction) {
			saleTraRDO = (SaleReturnTransaction) trn;
			traType = saleTraRDO.getTransactionType();
			tracsactionID = saleTraRDO.getTransactionID();
			//tracsactionID = saleTraRDO.getTransactionSequenceNumber();
		} else if (trn instanceof LayawayPaymentTransactionIfc) {
			layawaypaymentTraRDO = (LayawayPaymentTransactionIfc) trn;
			traType = layawaypaymentTraRDO.getTransactionType();
			tracsactionID = layawaypaymentTraRDO.getTransactionID();
		} else if (trn instanceof VoidTransactionIfc) {
			voidTrans = (VoidTransactionIfc) trn;
			traType = voidTrans.getTransactionType();
			tracsactionID = voidTrans.getTransactionID();
			
		}
		String saleTransaction = Gateway.getProperty("application", "saleTransaction", null);
		String voidTransaction = Gateway.getProperty("application", "voidTransaction", null);

		switch (traType) {

		case TransactionIfc.TYPE_SALE:
			transactionType = saleTransaction;
			break;

		case TransactionIfc.TYPE_VOID:
			transactionType = voidTransaction;
			break;

		case TransactionIfc.TYPE_ORDER_INITIATE:	//Rev 1.5 changes
		case TransactionIfc.TYPE_ORDER_COMPLETE:
		case TransactionIfc.TYPE_ORDER_PARTIAL:
		
			transactionType = saleTransaction;
			break;
		case TransactionIfc.TYPE_ORDER_CANCEL:
			transactionType = voidTransaction;
			break;
		//end	

		}
		cargo.getTenderAttributes().put("TRANSACTION_TYPE", transactionType);
		cargo.getTenderAttributes().put("TENDER_TYPE", TenderTypeEnum.CREDIT);

		try {
			cargo.getCurrentTransactionADO().validateRefundLimits(
					cargo.getTenderAttributes(), true, true);
		} catch (TenderException e) {
			TenderErrorCodeEnum error = e.getErrorCode();
			POSUIManagerIfc uiM = (POSUIManagerIfc) bus.getManager("UIManager");
			if (error == TenderErrorCodeEnum.OVERTENDER_ILLEGAL) {
				UIUtilities.setDialogModel(uiM, 1, "OvertenderNotAllowed",
						null, "Invalid");
				return;
			}
			if (error == TenderErrorCodeEnum.MAX_LIMIT_VIOLATED) {
				int buttons[] = { 1, 2 };
				String letters[] = { "Override", "Invalid" };
				String args[] = { TenderTypeEnum.CREDIT.toString() };
				UIUtilities.setDialogModel(uiM, 0, "AmountExceedsMaximum",
						args, buttons, letters);
				return;
			}
			if (error == TenderErrorCodeEnum.MIN_LIMIT_VIOLATED) {
				int buttons[] = { 1, 2 };
				String letters[] = { "Override", "Invalid" };
				String args[] = { TenderTypeEnum.CREDIT.toString() };
				UIUtilities.setDialogModel(uiM, 0, "AmountLessThanMinimum",
						args, buttons, letters);
				return;
			}
		}
		String total = cargo
		.getTenderAttributes().get(TenderConstants.AMOUNT).toString();
		double l1 = Double.parseDouble(total);
//		System.out.println(" The total amount is"+l1);
		l1 = l1*100;
		long l2 = (new Double(l1)).longValue();
		//System.out.println(" The total amount is"+l2);
		String total1 = String.valueOf(l2);
		//Rev 1.6 For Transaction re-entry mode online offline flow will be enabled
		if(cargo.getRegister().getWorkstation().isTransReentryMode()){
			
			bus.mail("Offline");
		}else{
		ui.showScreen(MAXPOSUIManagerIfc.CREDIT_CARD_EDC, new POSBaseBeanModel());
		ProcessTransaction(transactionType,tracsactionID,total1,cargo,ui,model,bus);
		}
	}
	private void ProcessTransaction(String transactionType,String transactionID,String total,TenderCargo cargo,POSUIManagerIfc ui,DialogBeanModel model,BusIfc bus )
	{
		boolean isTrack1TheCSV = false;
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);   //rev 1.4 changes
		String letter = "Success";
		String FAILED_TRANSACTION = Gateway.getProperty("application", "FailedTransactionEDC", null);
		String CONNECTION_ACQUIRER_FAILED = Gateway.getProperty("application", "ConnAcquirerHostFailed", null);
		String CreditCardTimeOut=Gateway.getProperty("application", "CreditCardTimeOut", "180000");
//		strSwipeTxn = "TRUE";
		try {
			if (!isTrack1TheCSV) {
				// total = total + "," + bankCode + "," + strTrack1 + "," +
				// strTrack2 + "," + tracking_id + "," + strSwipeTxn +
				// ","+/*TID*/","+"Operator";
				//total = "5000";
				total = transactionType + "," + "T1/" + transactionID + ","
						+ total+","+","+","+","+","+","+","+","+",";
			} else {
				total = transactionType + total;
			}
		//	System.out.println("Request is == " + total);
			SocketAddress sockaddr = new InetSocketAddress("127.0.0.1", 8082);
			Socket clientSocket = new Socket();
			//Production bug EDC hange after scanning card  - Karni
			try{
			clientSocket.connect(sockaddr,Integer.parseInt(CreditCardTimeOut));
			}catch(Exception e){
				bus.mail("Offline");   
			}// 2nd parameter is timeout.
			//Rev 1.2 changes start here
			String timeout = Gateway.getProperty("application", "CreditCardTimeOut", null);
			if(timeout!=null && !timeout.equalsIgnoreCase("")){
				clientSocket.setSoTimeout(Integer.parseInt(timeout));
			}
				//Rev 1.2 changes end here
			InputStream in = clientSocket.getInputStream();
			OutputStream out = clientSocket.getOutputStream();
			out.flush();
			// the first argument is not being used anywhere
			// tx type is covered as a a part of the csv itself.
			out.write(GetTransmissionPacketForCentral(4001, total));
			//Rev 1.2 changes start here
			boolean bIsApproved = false;
			boolean batchOpen= false;
			boolean blankResponse = false;
			ArrayList alist = new ArrayList();
			try{
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

				if(responseLength<0)
					bus.mail("Offline");  //Rev 1.8 changes
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			byte[] responseCSV = new byte[responseLength];
			for (int k = 0; k < responseLength; k++) {
				responseCSV[k] = (byte) in.read();
			}
			byte rEtx = (byte) in.read();
			// if(rEtx != 0xFF)
			if (rEtx != -1) {
				System.out.println("Invalid end sentinel recieved");
				return;
			} else {
				String str = new String(responseCSV);
				System.out.println("response csv:" + str);
				
				char ch = 44;// ascii for , character
				ParseCSV(str, alist, ch);
//				if(alist == null && alist.size()==0){
//					blankResponse = true;
//				}
			}
//				else 
			}catch(InterruptedIOException ex){
				System.out.println("The exception is "+ex.getMessage());
			}catch(Exception ex){
				bus.mail("Offline");
			}
			
			if(alist == null || alist.size()==0){
				blankResponse = true;
				cardNumber = null;
				hostResponse = null;
				authRemark = null;
			}
				else if(alist!=null && alist.size()>0){
					blankResponse = false; 
				for (int i = 0; i < alist.size(); i++) {
					String strVal = alist.get(i).toString();
					if (i == 1) {
						if (strVal.length() > 0) {
							// indicates the approval code is available
							// this shows that the transaction is approved
							bIsApproved = true;
						} else {
							bIsApproved = false;
						}
					}
					if (alist != null && alist.size() > 0
							&&bIsApproved == true) {
						invoiceNumber = alist.get(7).toString();
						approvalCode = alist.get(1).toString();
						hostResponse = alist.get(2).toString();
						cardNumber = alist.get(3).toString();
						expirationDate = alist.get(4).toString();
						cardHolder = alist.get(5).toString();
						cardType = alist.get(6).toString();
						batchNumber = alist.get(8).toString();
						tId = alist.get(9).toString();
						loyaltyPoint = alist.get(10).toString();
						authRemark = alist.get(11).toString();
						transAcquirer = alist.get(12).toString();
						merchId = alist.get(13).toString();
						retrievalRefNo = alist.get(14).toString();
						cardEntryMode = alist.get(15).toString();
						merchName = alist.get(17).toString();
						merchAddress = alist.get(18).toString();
						merchCity = alist.get(19).toString();
						acquirerBankCode = alist.get(21).toString();
					}else if(alist != null && alist.size() > 0
							&&bIsApproved == false){
						if(alist.size()>=8)
						invoiceNumber = alist.get(7).toString();
						if(alist.size()>=2)
						approvalCode = alist.get(1).toString();
						if(alist.size()>=3)
						hostResponse = alist.get(2).toString();
						if(alist.size()>=4)
						cardNumber = alist.get(3).toString();
						if(alist.size()>=5)
						expirationDate = alist.get(4).toString();
						if(alist.size()>=6)
						cardHolder = alist.get(5).toString();
						if(alist.size()>=7)
						cardType = alist.get(6).toString();
						if(alist.size()>=9)
						batchNumber = alist.get(8).toString();
						if(alist.size()>=10)
						tId = alist.get(9).toString();
						if(alist.size()>=11)
						loyaltyPoint = alist.get(10).toString();
						if(alist.size()>=12)
						authRemark = alist.get(11).toString();
					}
					// bankAcquirer = alist.get(21).toString();
					if (expirationDate != null) {
						if ((expirationDate.equals("XXXX")
								|| expirationDate.equalsIgnoreCase("XXXX") || expirationDate
								.equals(""))) {
							expirationDate = "12/99";

						} else {

							String yearString = expirationDate.substring(0, 2);
							String monthString = expirationDate.substring(2, 4);
							expirationDate = monthString + "/" + yearString;
						}
					}
				}
			}
				if ((cardNumber != null) && (cardNumber.length() > 1)
						&& (invoiceNumber != null)
						&& (invoiceNumber.length() > 0)
						&& (hostResponse != null)
						&& (hostResponse.equals("APPROVED"))
						&& (cardType != null) && (authRemark != null)) {
					// Rev 1.2 changes
//					maskedCardNumber = mask(cardNumber);
//					if (maskedCardNumber != null
//							&& (maskedCardNumber.length() > 1)) {
//						cargo.getTenderAttributes().put("NUMBER",
//								maskedCardNumber);
//					} else {
//					}
					cargo.getTenderAttributes().put("NUMBER", cardNumber);
					// Change ends here by Amit(Infodart) for bug: #303

					cargo.getTenderAttributes().put("EXPIRATION_DATE",
							expirationDate);
					cargo.getTenderAttributes().put("AUTH_METHOD",
							AuthorizableTenderIfc.AUTHORIZATION_NETWORK_ONLINE);
					cargo.getTenderAttributes().put("HOST_RESPONSE",
							hostResponse);
					cargo.getTenderAttributes().put(
							TenderConstants.AUTH_RESPONSE, hostResponse);
					cargo.getTenderAttributes().put("AUTH_CODE", approvalCode);
					if(cardEntryMode != null&&(!(cardEntryMode.equalsIgnoreCase("")) )){
						cardEntryMode = "MagSwipe";
						// Changes starts for Rev 1.1 (Ashish : credit Card)
					cargo.getTenderAttributes().put("ENTRY_METHOD",
							EntryMethod.Swipe);
					// Changes ends for Rev 1.1 (Ashish : credit Card)
					}
					// cargo.getTenderAttributes().put("NAME", cardHolder);
					cargo.getTenderAttributes().put("CARDHOLDER_NAME",
							cardHolder);
					// Changed above for Cardholder Name
					cargo.getTenderAttributes().put("CARD_TYPE", transAcquirer);
					cargo.getTenderAttributes()
							.put("BATCH_NUMBER", batchNumber);
					cargo.getTenderAttributes().put("MERCHANT_ADDRESS",
							merchAddress);
					cargo.getTenderAttributes().put("MERCHANT_CITY", merchCity);
					cargo.getTenderAttributes().put("MERCHANT_NAME", merchName);
					cargo.getTenderAttributes().put("INVOICE_NUMBER",
							invoiceNumber);
					cargo.getTenderAttributes().put("ACQUIRER_BANK_CODE",
							acquirerBankCode);

					cargo.getTenderAttributes().put("TRANSACTION_ACQ_NAME",
							transAcquirer);
					cargo.getTenderAttributes().put("REMARK", authRemark);
					cargo.getTenderAttributes().put("MERCHANT_ID", merchId);
					cargo.getTenderAttributes().put("RETRIEVAL_REF_NUMBER",
							retrievalRefNo);
					cargo.getTenderAttributes().put("TERMINAL_ID", tId);
					//Rev 1.4 changes start
					try {
						if (pm.getStringValue("PrintCreditChargeSlip").equalsIgnoreCase("Y"))
						    {
							letter = "Success";
						    }else{
						       letter = "Done";
						    }
					}  catch(ParameterException pe)
		            {
		                logger.warn("Failed to retrieve parameter PrintCreditChargeSlip: " + pe.getMessage() + "");             
		            }
					//Rev 1.4 changes end
				} 
//				else if (authRemark != null
//						&& authRemark
//								.equalsIgnoreCase(FAILED_TRANSACTION)) {
//					letter = "Offline";
//					
//				} else if (authRemark != null
//						&& authRemark
//								.equalsIgnoreCase(CONNECTION_ACQUIRER_FAILED)) {
//					letter = "Offline";
//				}
				else if(authRemark == null && blankResponse == true){
					letter = "Offline";
				}
//				else if(batchOpen == true){
//					model.setType(DialogScreensIfc.ERROR);
//					String[] msg = new String[2];
//					model.setResourceID("CreditAuthDeclinedFromEDC");
//					msg[0] = batchString;
//					
//					msg[1] = "Press enter";		
//					model.setArgs(msg);
////					model.setArgs(new String[] { authRemark });
//					model.setButtonLetter(DialogScreensIfc.BUTTON_OK,
//							"Invalid");
//					ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
//					ui.showDialogScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
//					return;
//				}
				else {
					model.setType(DialogScreensIfc.YES_NO);
					String msg[] = new String[4];
					model.setResourceID("CreditAuthDeclinedFromEDC");
					msg[0] = "";
					if(hostResponse!=null && !(hostResponse.equalsIgnoreCase(""))){
						msg[0] = hostResponse; //Rev 1.7 changes
					}else{
					msg[0] = authRemark; //Rev 1.7 changes
					}
					msg[1] = "Do you want to got to Offline flow?"; 
					msg[2] = "Press Yes to continue with Offline flow";
					msg[3] = "or Press No to choose another tender type";
					
					model.setArgs(msg);
//					model.setArgs(new String[] { authRemark });
					model.setButtonLetter(DialogScreensIfc.BUTTON_NO,
							"Invalid");
					model.setButtonLetter(DialogScreensIfc.BUTTON_YES,
					"Decline");
					ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
		
					
					return;
				}
				if (letter != null) {
					bus.mail(letter);
				}
//			}
	//rev 1.2 changes end here
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		
		System.out.println(byteArrayToHexString(msgBytesExtra));
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
	
	public static void ParseCSV (String str,ArrayList vValues,char  chDelimiter)
	{
	    int pos = 0; 
		char ch = 0; 
		
		char current_quote = 0; 
		boolean quoted = false; 
		String token = "";  
		boolean token_complete = false; // indicates if the current token is
									 // read to be added to the result vector
		int len = str.length();  // length of the input-string

		// for every char in the input-string
		while ( len > pos )
		{
			// get the character of the string and reset the delimiter buffer
			ch = str.charAt(pos);
			//delimiter = 0;

			// assume ch isn't a delimiter
			boolean add_char = true;

			// check ...


			// ... if the delimiter is a quote
			
				// if quote chars are provided and the char isn't protected
				if ( ch == '"' )
				{
					// if not quoted, set state to open quote and set
					// the quote character
					if ( false == quoted )
					{
						quoted = true;
						current_quote = ch;

						// don't add the quote-char to the token
						add_char = false;
					}
					else // if quote is open already
					{
						// check if it is the matching character to close it
						if ( current_quote == ch )
						{
							// close quote and reset the quote character
							quoted = false;
							current_quote = 0;

							// don't add the quote-char to the token
							add_char = false;
							if(token.compareTo("") == 0)
							{
								//vValues.push_back( token );
								vValues.add(token);
								// clear the contents
								//token.Empty();
								token = "";
								// build the next token
								token_complete = false;

							}

						}
					} // else
				}
			

			// ... if the delimiter isn't preserved
			if ( false == quoted )
			{
				// if a delimiter is provided and the char isn't protected by
				// quote or escape char
				if ( ch == chDelimiter )
				{
					// if ch is a delimiter and the token string isn't empty
					// the token is complete
					//if ( ! token.IsEmpty() ) 
					if(token.compareTo("") != 0)
					{
						token_complete = true;
					}

					// don't add the delimiter to the token
					add_char = false;
				}
			}

			// ... if the delimiter is preserved - add it as a token		

			// add the character to the token
			if ( true == add_char )
			{
				// add the current char
				//token.AppendFormat("%c", ch );
				String str1 =Character.toString(ch);
				token = token + str1;
			}

			// add the token if it is complete
			//if ( true == token_complete && ! token.IsEmpty() )
			if ( true == token_complete && (token.compareTo("") != 0) )
			{
				// add the token string
				//vValues.push_back( token );
				vValues.add(token);
				// clear the contents
				//token.Empty();
				token = "";
				// build the next token
				token_complete = false;
			}
			// repeat for the next character
			++pos;
		} // while

		// add the final token
		if ( token.compareTo("") != 0 )
		{
			//vValues.push_back( token );
			vValues.add(token);
		}
	}
	public static String zeropad(String str, int size){
		int finalSize = str.length();
		for (int i=0; i<size-finalSize; i++){
			str = "0"+str;
		}
		return str;
	}

	public static byte[] a2bcd(String str) {
		int r= str.length()%2;
		if(r==1){
			str = "0"+str;
		}
		byte[] src = str.getBytes();
		int len = src.length;
		byte[] dest = new byte[len/2];
		String temp;
		for(int i=0; i<len; i +=2) {
			temp = new String(src, i, 2);
			dest[i/2] = (byte)Long.parseLong(temp, 16);
		}
		return dest;
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
	
	protected String mask(String cardNumber) 
	{
		String maskedNumber=cardNumber.substring(0,(cardNumber.length()-4));
		char temp[]=maskedNumber.toCharArray();
		for(int i=0;i<temp.length;i++)
		{
			temp[i]='*';
		}
		cardNumber=new String(temp)+cardNumber.substring(maskedNumber.length());
		return cardNumber;
	}
	private void displayErrorDialog(POSUIManagerIfc ui, String resourceId) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(resourceId);
		dialogModel.setType(DialogScreensIfc.RETRY_CONTINUE);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Back");
		ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
	}
	
	public void depart(BusIfc bus){
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
		.getManager(UIManagerIfc.TYPE);
		if(bus.getCurrentLetter().getName().equalsIgnoreCase("Back")){
			ui.showScreen(POSUIManagerIfc.CREDIT_DEBIT_CARD, new POSBaseBeanModel());
		}
	}
}
