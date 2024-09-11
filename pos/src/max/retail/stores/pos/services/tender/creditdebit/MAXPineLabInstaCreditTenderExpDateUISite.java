/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2001 360Commerce, Inc.    All Rights Reserved.

     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:33 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:20:27 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:10:15 PM  Robert Pearse   
     $
     Revision 1.1  2004/04/08 19:30:59  bwf
     @scr 4263 Decomposition of Debit and Credit.

     Revision 1.2  2004/02/12 16:48:22  mcs
     Forcing head revision

     Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.3   Nov 20 2003 16:57:30   epd
 * updated to use new ADO Factory Complex
 * 
 *    Rev 1.2   Nov 19 2003 14:10:46   epd
 * TDO refactoring to use factory
 * 
 *    Rev 1.1   Nov 14 2003 14:25:14   epd
 * moved logic from Site into ADO
 * 
 *    Rev 1.0   Nov 04 2003 11:17:40   epd
 * Initial revision.
 * 
 *    Rev 1.1   Nov 01 2003 15:09:58   epd
 * changed letter
 * 
 *    Rev 1.0   Oct 23 2003 17:29:48   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 22 2003 19:19:14   epd
 * Initial revision.
     
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import max.retail.stores.pos.services.edc.CallingOnlineDebitCardTender;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Determines whether a screen prompting for Exp Date needs to be displayed
 */
public class MAXPineLabInstaCreditTenderExpDateUISite extends PosSiteActionAdapter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.foundation.tour.application.SiteActionAdapter#arrive
	 * (com.extendyourstore.foundation.tour.ifc.BusIfc)
	 */

	public static String readRequestXmlClient() {
		String fileName = "D:\\opt\\innoviti\\requestClient_chk.xml";

		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			String bufferString = "";
			String lineString = "";
			while ((lineString = reader.readLine()) != null) {
				bufferString += lineString;
			}

			return bufferString;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void arrive(BusIfc bus) {
		// If we have an MSR model, then no need to prompt for exp. date,
		// otherwise prompt the user unless we have a House card for which
		// no exp date is required
		HashMap responseMap = null;

		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();

		BigDecimal amount = new BigDecimal(cargo.getTenderAttributes().get("AMOUNT").toString());

		if (cargo.getTenderAttributes().get(TenderConstants.MSR_MODEL) == null) {

			// Added by Gaurav for the unipay functionality..starts
			// code added by Gaurav using unipay..starts
			if (("SwipeCard").equals(bus.getCurrentLetter().getName())) {
				// System.out.println("Inside Test") ;

				// Needs to provide request xml
				String transactionTime = "2012-07-21T13:55:58.0Z";
				String amountString = amount.multiply(new BigDecimal("100.00")).intValue() + "";
				String invoiceNumber = "123"; // no need in sale
				POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
				DialogBeanModel dialogModel = new DialogBeanModel();
				ui.showScreen(MAXPOSUIManagerIfc.EDC_POST_VOID_SCREEN, dialogModel);
				CallingOnlineDebitCardTender edcObj = new CallingOnlineDebitCardTender();

				try {
					responseMap = edcObj.makePostVoidEDC(cargo.getCurrentTransactionADO().getTransactionID(), amountString, invoiceNumber, transactionTime,
							"0", "0");

					/*
					 * String requestXmlArg =
					 * "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
					 * "<purchase-request><Transaction ID=" + "\"" +
					 * cargo.getCurrentTransactionADO().getTransactionID() +"\""
					 * + ">" +//"SLF02W012040044609" + ">" + "<Card>" +
					 * "<IsManualEntry>false</IsManualEntry>" +
					 * "<Track1>B4293932010997296^      ^151012600644000000</Track1>"
					 * + "<Track2>4763382007436851=1510126644</Track2>" +
					 * "</Card>" + " <Amount><BaseAmount>" + amount.multiply(new
					 * BigDecimal("100.00")).intValue() +// "10000"
					 * "</BaseAmount><discount>00</discount> " + "<Amount>" +
					 * amount.multiply(new BigDecimal("100.00")).intValue() +//
					 * "10000"
					 * "</Amount> <CurrencyCode>INR</CurrencyCode></Amount>" +
					 * "<POS><ReferenceNumber>" + "000313" +
					 * "</ReferenceNumber><TransactionTime>" + transactionTime +
					 * //"2012-07-21T13:55:58.0Z" + "</TransactionTime>" +
					 * "<TrackingNumber>424</TrackingNumber></POS></Transaction> </purchase-request>"
					 * ;
					 * 
					 * String output = "";
					 * 
					 * try { System.setSecurityManager(null);
					 * 
					 * 
					 * output = UnipayClient.innovEFT("0", "0", requestXmlArg);
					 * // 0,0 sale 1,0 Void 2,0 refund
					 * 
					 * responseMap = parseResponseDate(output);
					 * 
					 * System.out.println("billing`112 \n"+ responseMap);
					 */

					String dateString = "12/2024";
					cargo.getTenderAttributes().put("NUMBER", responseMap.get("CardNumber"));
					cargo.getTenderAttributes().put("EXPIRATION_DATE", dateString);
					cargo.setResponseMap(responseMap);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			// modification by Gaurav..ends

			// attempt to create a credit tender. Because we do not yet have
			// an expiration date, this will fail for all cards needing an
			// expiration date. It will pass for those cards that do not require
			// an exp. date.

			try {
				TenderFactoryIfc factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
				factory.createTender(cargo.getTenderAttributes());
			} catch (ADOException adoe) {
				adoe.printStackTrace();
			} catch (TenderException e) {
				if (e.getErrorCode() == TenderErrorCodeEnum.INVALID_EXPIRATION_DATE) {
					// prompt for the exp. date
					POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
					// build the bean model
					TDOUIIfc tdo = null;
					try {
						tdo = (TDOUIIfc) TDOFactory.create("tdo.tender.CreditExpDate");
					} catch (TDOException tdoe) {
						// TODO Auto-generated catch block
						tdoe.printStackTrace();
					}
					if (("SwipeCard").equals(bus.getCurrentLetter().getName())) {
						bus.mail(new Letter("SwipeCard"), BusIfc.CURRENT);
						return;
					} else
						ui.showScreen(POSUIManagerIfc.CREDIT_EXP_DATE, tdo.buildBeanModel(null));
					return;
				}
			} catch (Exception e) {
				if (responseMap != null && !("00").equals(responseMap.get("HostResponseCode").toString())) {
					String msgLetter = "SwipeWithOutExpError";
					showDialogBoxMethod(responseMap, bus, msgLetter);
					/*
					 * DialogBeanModel dialogModel = new DialogBeanModel();
					 * String msg[] = new String[6];
					 * dialogModel.setResourceID("RESPONSE_DETAILS"); msg[0] =
					 * "Please Find The Details As Below"; msg[1] =
					 * "Your Credit/Debit Card Swipe Status Code Is "
					 * +responseMap.get("HostResponseCode").toString(); ; msg[2]
					 * = responseMap.get("HostResponseMessage").toString();
					 * msg[3] = "Please Press OK To Proceed"; msg[4] =
					 * "You Can Also Use Another Tender"; msg[5] = "::Thanks::";
					 * dialogModel.setArgs(msg);
					 * dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
					 * "SwipeWithOutExpError"); POSUIManagerIfc ui =
					 * (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
					 * 
					 * ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
					 * dialogModel);
					 */

				}
			}

		}
		if (responseMap != null && ("00").equals(responseMap.get("HostResponseCode").toString())) {
			// this will only get mailed if we don't need to prompt for an exp.
			// date
			if ((("SwipeCard").equals(bus.getCurrentLetter().getName())) && responseMap != null
					&& ("00").equals(responseMap.get("HostResponseCode").toString())) // Gaurav
			{
				if (("SwipeCard").equals(bus.getCurrentLetter().getName())) {
					String msgLetter = "SwipeWithOutExp";
					showDialogBoxMethod(responseMap, bus, msgLetter);
					/*
					 * DialogBeanModel dialogModel = new DialogBeanModel();
					 * String msg[] = new String[6];
					 * dialogModel.setResourceID("RESPONSE_DETAILS"); msg[0] =
					 * "Please Find The Details As Below"; msg[1] =
					 * "Your Credit/Debit Card Swipe Status Code Is"
					 * +responseMap.get("HostResponseCode").toString(); ; msg[2]
					 * = responseMap.get("HostResponseMessage").toString();
					 * msg[3] = "Please Press OK To Proceed"; msg[4] =
					 * "You Can Also Use Another Tender"; msg[5] = "::Thanks::";
					 * dialogModel.setArgs(msg);
					 * dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
					 * "SwipeWithOutExp"); POSUIManagerIfc ui =
					 * (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
					 * ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
					 * dialogModel);
					 */

				}
				// bus.mail(new Letter("SwipeWithOutExp"), BusIfc.CURRENT);
			} else
				bus.mail(new Letter("Continue"), BusIfc.CURRENT);
		} else {
			String msgLetter = "SwipeWithOutExpError";
			showDialogBoxMethod(responseMap, bus, msgLetter);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.foundation.tour.application.SiteActionAdapter#depart
	 * (com.extendyourstore.foundation.tour.ifc.BusIfc)
	 */
	/*
	 * public HashMap parseResponseDate(String output) {
	 * 
	 * 
	 * 
	 * HashMap responseDataMap = new HashMap(); String xmlRecords =
	 * "<hub-response><Transaction ID=\"SLF02W012040044609\"><Card>" +
	 * "<IsManualEntry>true</IsManualEntry><CardNumber>418157xxxxxx9203</CardNumber><ExpirationDate><MM>xx</MM><YY>xx</YY>"
	 * +
	 * "</ExpirationDate><IssuerName>xxx</IssuerName><SchemeType>VISA</SchemeType></Card><State><SelectedAcquirer><ID>111</ID>"
	 * +
	 * "<Name>CITI BANK</Name><DiscountRate>NA</DiscountRate><Status>APPROVED</Status></SelectedAcquirer><TID>56000101</TID>"
	 * +
	 * "<Merchant><ID>441656286106573</ID><Name>WS-Commercial Street</Name><Address>77 COMMERCIAL STREET</Address>"
	 * +
	 * "<City>Banglore</City></Merchant><InvoiceNumber>000316</InvoiceNumber><BatchNumber>000036</BatchNumber>"
	 * +
	 * "<AcquirerName>CITI BANK</AcquirerName><TransactionTime>2012-07-24T14:28:48.0Z</TransactionTime><Amount>10000</Amount>"
	 * +
	 * "<Discount>000</Discount><TotalAmount>10000</TotalAmount><HostResponse><ResponseCode>00</ResponseCode>"
	 * +
	 * "<ResponseMessage>APPROVED</ResponseMessage><ApprovalCode>123456</ApprovalCode>"
	 * +
	 * "<RetrievalReferenceNumber>515151515151</RetrievalReferenceNumber></HostResponse><StatusCode>00</StatusCode>"
	 * +
	 * "<StatusMessage>APPROVED</StatusMessage></State><ChargeslipData lineCount=\"20\" printerWidth=\"48\">"
	 * +
	 * "<Receipt isCustomerCopy=\"false\"><PrintLine isBold=\"true\" isCentered=\"true\" lineNumber=\"1\">CITI BANK</PrintLine>"
	 * +
	 * "<PrintLine isCentered=\"true\" lineNumber=\"2\">WS-Commercial Street</PrintLine>"
	 * +
	 * "<PrintLine lineNumber=\"3\">77 COMMERCIAL STREET</PrintLine><PrintLine lineNumber=\"4\">Banglore</PrintLine>"
	 * +
	 * "<PrintLine lineNumber=\"5\"> </PrintLine><PrintLine lineNumber=\"6\">DATE:2012-07-24             TIME:14:28:48 </PrintLine>"
	 * +
	 * "<PrintLine lineNumber=\"7\">MID: 441656286106573         TID: 06106573</PrintLine>"
	 * +
	 * "<PrintLine lineNumber=\"8\">BATCH NUM: 000036            INV. NUM: 000316</PrintLine>"
	 * +
	 * "<PrintLine isCentered=\"true\" lineNumber=\"9\">SALE</PrintLine><PrintLine lineNumber=\"10\">Card No: 418157xxxxxx9203 </PrintLine>"
	 * +
	 * "<PrintLine lineNumber=\"11\">EXP DATE: xx/xx              CARD: VISA</PrintLine>"
	 * +
	 * "<PrintLine lineNumber=\"12\">APPR CODE: 123456            RRN: 515151515151</PrintLine>"
	 * +
	 * "<PrintLine lineNumber=\"13\">AMOUNT: 100.00</PrintLine><PrintLine lineNumber=\"14\"> </PrintLine>"
	 * + "<PrintLine lineNumber=\"15\">SIGN :________________</PrintLine>" +
	 * "<PrintLine lineNumber=\"16\">568573 VISA DEBITCARD    /</PrintLine>" +
	 * "<PrintLine lineNumber=\"17\">I AGREE TO PAY AS PER CARD ISSUER AGRMNT                 uniPAY Ver1.1</PrintLine>"
	 * +
	 * "<PrintLine isCentered=\"true\" lineNumber=\"18\">***** MERCHANT COPY *****</PrintLine>"
	 * +
	 * "<PrintLine lineNumber=\"19\"/><PrintLine isCentered=\"true\" lineNumber=\"20\">THANK YOU</PrintLine>"
	 * +
	 * "</Receipt><Receipt isCustomerCopy=\"true\"><PrintLine isBold=\"true\" isCentered=\"true\" lineNumber=\"1\">CITI BANK</PrintLine>"
	 * +
	 * "<PrintLine isCentered=\"true\" lineNumber=\"2\">WS-Commercial Street</PrintLine>"
	 * +
	 * "<PrintLine lineNumber=\"3\">77 COMMERCIAL STREET</PrintLine><PrintLine lineNumber=\"4\">Banglore</PrintLine>"
	 * +
	 * "<PrintLine lineNumber=\"5\"> </PrintLine><PrintLine lineNumber=\"6\">DATE:2012-07-24             TIME:14:28:48 </PrintLine>"
	 * +
	 * "<PrintLine lineNumber=\"7\">MID: 441656286106573         TID: 06106573</PrintLine>"
	 * +
	 * "<PrintLine lineNumber=\"8\">BATCH NUM: 000036            INV. NUM: 000316</PrintLine>"
	 * + "<PrintLine isCentered=\"true\" lineNumber=\"9\">SALE</PrintLine>" +
	 * "<PrintLine lineNumber=\"10\">Card No: 418157xxxxxx9203 </PrintLine>" +
	 * "<PrintLine lineNumber=\"11\">EXP DATE: xx/xx              CARD: VISA</PrintLine>"
	 * +
	 * "<PrintLine lineNumber=\"12\">APPR CODE: 123456            RRN: 515151515151</PrintLine>"
	 * +
	 * "<PrintLine lineNumber=\"13\">AMOUNT: 100.00</PrintLine><PrintLine lineNumber=\"14\"> </PrintLine>"
	 * + "<PrintLine lineNumber=\"15\">SIGN :________________</PrintLine>" +
	 * "<PrintLine lineNumber=\"16\">568573 VISA DEBITCARD    /</PrintLine>" +
	 * "<PrintLine lineNumber=\"17\">I AGREE TO PAY AS PER CARD ISSUER AGRMNT                 uniPAY Ver1.1</PrintLine>"
	 * +
	 * "<PrintLine isCentered=\"true\" lineNumber=\"18\">***** CUSTOMER COPY *****</PrintLine><PrintLine lineNumber=\"19\"/>"
	 * +
	 * "<PrintLine isCentered=\"true\" lineNumber=\"20\">THANK YOU</PrintLine></Receipt>"
	 * + "</ChargeslipData></Transaction></hub-response>";
	 * 
	 * 
	 * String xmlRecords= output;
	 * 
	 * 
	 * 
	 * try { DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	 * DocumentBuilder db = dbf.newDocumentBuilder(); InputSource is = new
	 * InputSource(); is.setCharacterStream(new StringReader(xmlRecords));
	 * 
	 * Document doc = db.parse(is);
	 * 
	 * 
	 * NodeList nodes = doc.getElementsByTagName("Card"); // iterate the
	 * employees Element element = (Element) nodes.item(0);
	 * 
	 * NodeList track = element.getElementsByTagName("IsManualEntry"); Element
	 * line = (Element) track.item(0); responseDataMap.put("IsManualEntry",
	 * getCharacterDataFromElement(line)); System.out.println("IsManualEntry: "
	 * + getCharacterDataFromElement(line));
	 * 
	 * NodeList track1 = element.getElementsByTagName("CardNumber"); line =
	 * (Element) track1.item(0); responseDataMap.put("CardNumber",
	 * getCharacterDataFromElement(line)); System.out.println("CardNumber: " +
	 * getCharacterDataFromElement(line));
	 * 
	 * NodeList track2 = element.getElementsByTagName("MM"); line = (Element)
	 * track2.item(0); responseDataMap.put("MM",
	 * getCharacterDataFromElement(line)); System.out.println("MM: " +
	 * getCharacterDataFromElement(line));
	 * 
	 * NodeList track3 = element.getElementsByTagName("YY"); line = (Element)
	 * track3.item(0); responseDataMap.put("YY",
	 * getCharacterDataFromElement(line)); System.out.println("YY: " +
	 * getCharacterDataFromElement(line));
	 * 
	 * NodeList track4 = element.getElementsByTagName("IssuerName"); line =
	 * (Element) track4.item(0); responseDataMap.put("IssuerName",
	 * getCharacterDataFromElement(line)); System.out.println("IssuerName: " +
	 * getCharacterDataFromElement(line));
	 * 
	 * NodeList track5 = element.getElementsByTagName("SchemeType"); line =
	 * (Element) track5.item(0); responseDataMap.put("SchemeType",
	 * getCharacterDataFromElement(line)); System.out.println("SchemeType: " +
	 * getCharacterDataFromElement(line));
	 * 
	 * 
	 * 
	 * 
	 * NodeList nodes1 = doc.getElementsByTagName("SelectedAcquirer"); //
	 * iterate the employees Element element1 = (Element) nodes1.item(0);
	 * 
	 * NodeList track6 = element1.getElementsByTagName("ID"); Element line1 =
	 * (Element) track6.item(0); responseDataMap.put("SelectedAcquirerID",
	 * getCharacterDataFromElement(line1)); System.out.println("ID: " +
	 * getCharacterDataFromElement(line1));
	 * 
	 * NodeList track7 = element1.getElementsByTagName("Name"); line1 =
	 * (Element) track7.item(0); responseDataMap.put("SelectedAquirerName",
	 * getCharacterDataFromElement(line1)); System.out.println("Name: " +
	 * getCharacterDataFromElement(line1));
	 * 
	 * NodeList track8 = element1.getElementsByTagName("DiscountRate"); line1 =
	 * (Element) track8.item(0);
	 * responseDataMap.put("SelectedAquirerDiscountRate",
	 * getCharacterDataFromElement(line1)); System.out.println("DiscountRate: "
	 * + getCharacterDataFromElement(line1));
	 * 
	 * NodeList track9 = element1.getElementsByTagName("Status"); line1 =
	 * (Element) track9.item(0); responseDataMap.put("SelectedAquirerStatus",
	 * getCharacterDataFromElement(line1)); System.out.println("Status: " +
	 * getCharacterDataFromElement(line1));
	 * 
	 * 
	 * 
	 * NodeList nodes2 = doc.getElementsByTagName("Merchant"); // iterate the
	 * employees Element element2 = (Element) nodes2.item(0);
	 * 
	 * NodeList track10= element2.getElementsByTagName("ID"); Element line2 =
	 * (Element) track10.item(0); responseDataMap.put("MerchantID",
	 * getCharacterDataFromElement(line2)); System.out.println("ID: " +
	 * getCharacterDataFromElement(line2));
	 * 
	 * NodeList track11 = element2.getElementsByTagName("Name"); line2 =
	 * (Element) track11.item(0); responseDataMap.put("MerchantName",
	 * getCharacterDataFromElement(line2)); System.out.println("Name: " +
	 * getCharacterDataFromElement(line2));
	 * 
	 * NodeList track12 = element2.getElementsByTagName("Address"); line2 =
	 * (Element) track12.item(0); responseDataMap.put("MerchantAddress",
	 * getCharacterDataFromElement(line2)); System.out.println("Address: " +
	 * getCharacterDataFromElement(line2));
	 * 
	 * NodeList track13 = element2.getElementsByTagName("City"); line2=
	 * (Element) track13.item(0); responseDataMap.put("MerchantCity",
	 * getCharacterDataFromElement(line2)); System.out.println("City: " +
	 * getCharacterDataFromElement(line2));
	 * 
	 * 
	 * 
	 * NodeList HostResponse = doc.getElementsByTagName("HostResponse"); //
	 * iterate the employees Element HostResponseElem = (Element)
	 * HostResponse.item(0);
	 * 
	 * NodeList track14= HostResponseElem.getElementsByTagName("ResponseCode");
	 * Element line3 = (Element) track14.item(0);
	 * responseDataMap.put("HostResponseCode",
	 * getCharacterDataFromElement(line3)); System.out.println("ResponseCode: "
	 * + getCharacterDataFromElement(line3));
	 * 
	 * NodeList track15 =
	 * HostResponseElem.getElementsByTagName("ResponseMessage"); line3 =
	 * (Element) track15.item(0); responseDataMap.put("HostResponseMessage",
	 * getCharacterDataFromElement(line3));
	 * System.out.println("ResponseMessage: " +
	 * getCharacterDataFromElement(line3));
	 * 
	 * NodeList track16 = HostResponseElem.getElementsByTagName("ApprovalCode");
	 * line3 = (Element) track16.item(0);
	 * responseDataMap.put("HostResponseApprovalCode",
	 * getCharacterDataFromElement(line3)); System.out.println("ApprovalCode: "
	 * + getCharacterDataFromElement(line3));
	 * 
	 * NodeList track17 =
	 * HostResponseElem.getElementsByTagName("RetrievalReferenceNumber"); line3=
	 * (Element) track17.item(0);
	 * responseDataMap.put("HostResponseRetrievelRefNumber",
	 * getCharacterDataFromElement(line3));
	 * System.out.println("RetrievalReferenceNumber: " +
	 * getCharacterDataFromElement(line3));
	 * 
	 * 
	 * NodeList State = doc.getElementsByTagName("State"); // iterate the
	 * employees Element StateElem = (Element) State.item(0);
	 * 
	 * NodeList track18= StateElem.getElementsByTagName("TID"); Element line4 =
	 * (Element) track18.item(0); responseDataMap.put("StateTID",
	 * getCharacterDataFromElement(line4)); System.out.println("TID: " +
	 * getCharacterDataFromElement(line4));
	 * 
	 * NodeList track19 = StateElem.getElementsByTagName("InvoiceNumber"); line4
	 * = (Element) track19.item(0); responseDataMap.put("StateInvoiceNumber",
	 * getCharacterDataFromElement(line4)); System.out.println("InvoiceNumber: "
	 * + getCharacterDataFromElement(line4));
	 * 
	 * NodeList track20 = StateElem.getElementsByTagName("BatchNumber"); line4 =
	 * (Element) track20.item(0); responseDataMap.put("StateBatchNumber",
	 * getCharacterDataFromElement(line4)); System.out.println("BatchNumber: " +
	 * getCharacterDataFromElement(line4));
	 * 
	 * NodeList track21 = StateElem.getElementsByTagName("AcquirerName"); line4=
	 * (Element) track21.item(0); responseDataMap.put("StateAquirerName",
	 * getCharacterDataFromElement(line4)); System.out.println("AcquirerName: "
	 * + getCharacterDataFromElement(line4));
	 * 
	 * NodeList track22 = StateElem.getElementsByTagName("TransactionTime");
	 * line4= (Element) track22.item(0);
	 * responseDataMap.put("StateTransactionTime",
	 * getCharacterDataFromElement(line4));
	 * System.out.println("TransactionTime: " +
	 * getCharacterDataFromElement(line4));
	 * 
	 * NodeList track23 = StateElem.getElementsByTagName("Amount"); line4=
	 * (Element) track23.item(0); responseDataMap.put("StateAmount",
	 * getCharacterDataFromElement(line4)); System.out.println("Amount: " +
	 * getCharacterDataFromElement(line4));
	 * 
	 * NodeList track24 = StateElem.getElementsByTagName("Discount"); line4=
	 * (Element) track24.item(0); responseDataMap.put("StateDiscount",
	 * getCharacterDataFromElement(line4)); System.out.println("Discount: " +
	 * getCharacterDataFromElement(line4));
	 * 
	 * NodeList track25 = StateElem.getElementsByTagName("TotalAmount"); line4=
	 * (Element) track25.item(0); responseDataMap.put("StateTotalAmount",
	 * getCharacterDataFromElement(line4)); System.out.println("TotalAmount: " +
	 * getCharacterDataFromElement(line4));
	 * 
	 * NodeList track26 = StateElem.getElementsByTagName("StatusCode"); line4=
	 * (Element) track26.item(0); responseDataMap.put("StateStatusCode",
	 * getCharacterDataFromElement(line4)); System.out.println("StatusCode: " +
	 * getCharacterDataFromElement(line4));
	 * 
	 * NodeList track27 = StateElem.getElementsByTagName("StatusMessage");
	 * line4= (Element) track27.item(0);
	 * responseDataMap.put("StateStatusMessage",
	 * getCharacterDataFromElement(line4)); System.out.println("StatusMessage: "
	 * + getCharacterDataFromElement(line4));
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * 
	 * 
	 * return responseDataMap; }
	 */
	public void showDialogBoxMethod(Map responseMap, BusIfc bus, String buttonLetter) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		String msg[] = new String[6];
		dialogModel.setResourceID("RESPONSE_DETAILS");
		msg[0] = "<<--||--:: Please Find The Response Details As Below ::--||-->>";
		msg[1] = "Your Credit/Debit Card has been Swiped";
		msg[2] = " Response Code Returned Is ";// +responseMap.get("HostResponseCode").toString();
												// ;
		msg[3] = responseMap.get("HostResponseMessage").toString();
		msg[4] = "Press ENTER To Proceed / Using another Tender";
		msg[5] = "::Thanks::";
		dialogModel.setArgs(msg);
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, buttonLetter);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		// if (child instanceof CharacterData) {
		CharacterData cd = null;
		try {
			cd = (CharacterData) child;
		} catch (Exception eb) {

		}
		if (cd != null)
			return cd.getData();
		else
			return null;
		// }
		// return "?";
	}

	public void depart(BusIfc bus) {
		if (bus.getCurrentLetter().getName().equals("Next")) {
			// Get information from UI
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			TenderCargo cargo = (TenderCargo) bus.getCargo();
			cargo.getTenderAttributes().put(TenderConstants.EXPIRATION_DATE, ui.getInput());
		}
	}

}
