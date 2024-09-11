/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016	MAX HyperMarkets.    All Rights Reserved.
 *
 *	Rev 1.0 	27 Oct 2017		Jyoti Yadav		Changes for Innoviti Integration CR
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.edc;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jpos.POSPrinterConst;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

import org.apache.log4j.Logger;
import org.innoviti.integrate.UnipayClient;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CallingOnlineDebitCardTender {
	protected static final Logger logger = Logger.getLogger(CallingOnlineDebitCardTender.class);

	public CallingOnlineDebitCardTender() {

	}

	HashMap responseMap = new LinkedHashMap();
	int i = 0;

	public HashMap statusEnquiry() {

		String requestType = "13";
		String amount = "10000";
		String invoiceId = "1234";
		String transactionTimeToBeSet = "2012-07-21T13:55:58.0Z";
		String tranNumber = "SLF02W012040044609";
		String requestXmlArg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<purchase-request><Transaction ID="
				+ "\"" + tranNumber + "\"" + ">" + "<Card>" + "<IsManualEntry>false</IsManualEntry>"
				+ "<Track1>B4293932010997296^      ^151012600644000000</Track1>"
				+ "<Track2>4763382007436851=1510126644</Track2>" + "</Card>" + " <Amount><BaseAmount>" + amount
				+ "</BaseAmount><discount>00</discount> " + "<Amount>" + amount
				+ "</Amount> <CurrencyCode>INR</CurrencyCode></Amount>" + "<POS><ReferenceNumber>" + invoiceId
				+ "</ReferenceNumber><TransactionTime>" + transactionTimeToBeSet +
				"</TransactionTime>" + "<TrackingNumber>424</TrackingNumber></POS></Transaction> </purchase-request>";

		String requestMode = "9";

		String output = "";
		logger.info("requestType " + requestType);
		logger.info("requestXmlArg " + requestXmlArg);
		logger.info("requestMode " + requestMode);
		try {
			System.setSecurityManager(null);
			output = UnipayClient.innovEFT(requestType, requestMode, requestXmlArg);
			logger.info("Response "+output);
			responseMap = parseStatusResponseDate(output);
			responseMap = parsePrintingData(output, responseMap);

		} catch (Exception e) {
			logger.warn(e);
		}
		return responseMap;

	}

	public HashMap makePostVoidEDC(String transactionID, String amountString, String invoiceNumber,
			String transactionTime, String requestType, String requestMode) {

		String amount = "10000";
		String invoiceId = "1234";
		String transactionTimeToBeSet = "2012-07-21T13:55:58.0Z";
		String tranNumber = "SLF02W012040044609";

		if (amountString != null)
			amount = amountString;
		if (invoiceNumber != null)
			invoiceId = invoiceNumber;
		if (transactionTime != null)
			transactionTimeToBeSet = transactionTime;
		if (transactionID != null)
			tranNumber = transactionID;

		String requestXmlArg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<purchase-request><Transaction ID="
				+ "\"" + tranNumber + "\"" + ">" + "<Card>" + "<IsManualEntry>false</IsManualEntry>"
				+ "<Track1>B4293932010997296^      ^151012600644000000</Track1>"
				+ "<Track2>4763382007436851=1510126644</Track2>" + "</Card>" + " <Amount><BaseAmount>" + amount
				+ "</BaseAmount><discount>00</discount> " + "<Amount>" + amount
				+ "</Amount> <CurrencyCode>INR</CurrencyCode></Amount>" + "<POS><ReferenceNumber>" + invoiceId
				+ "</ReferenceNumber><TransactionTime>" + transactionTimeToBeSet /*
																				 * +
																				 * "\""
																				 * +
																				 * ">"
																				 */+ // "2012-07-21T13:55:58.0Z"
				// +
				"</TransactionTime>" + "<TrackingNumber>424</TrackingNumber></POS></Transaction> </purchase-request>";

		String output = "";

		try {
			System.setSecurityManager(null);
			output = UnipayClient.innovEFT(requestType, requestMode, requestXmlArg); // 0,0
																						// sale
			// 1,0
			// Void
			// 2,0
			// refund

			responseMap = parseResponseDate(output);
			responseMap = parsePrintingData(output, responseMap);

		} catch (Exception e) {
			logger.warn(e);
		}
		return responseMap;

	}

	public HashMap parseStatusResponseDate(String output) {

		HashMap responseDataMap = new LinkedHashMap();

		String xmlRecords = output;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlRecords));

			Document doc = db.parse(is);

			NodeList nodes = doc.getElementsByTagName("Card");
			// iterate the employees
			Element element = (Element) nodes.item(0);

			NodeList track = element.getElementsByTagName("IsManualEntry");
			Element line = (Element) track.item(0);
			responseDataMap.put("IsManualEntry", getCharacterDataFromElement(line));
			// System.out.println("IsManualEntry: "
			// + getCharacterDataFromElement(line));

			NodeList track1 = element.getElementsByTagName("CardNumber");
			line = (Element) track1.item(0);
			responseDataMap.put("CardNumber", getCharacterDataFromElement(line));
			// System.out.println("CardNumber: "
			// + getCharacterDataFromElement(line));

			NodeList track2 = element.getElementsByTagName("MM");
			line = (Element) track2.item(0);
			responseDataMap.put("MM", getCharacterDataFromElement(line));
			// System.out.println("MM: " + getCharacterDataFromElement(line));

			NodeList track3 = element.getElementsByTagName("YY");
			line = (Element) track3.item(0);
			responseDataMap.put("YY", getCharacterDataFromElement(line));
			// System.out.println("YY: " + getCharacterDataFromElement(line));

			NodeList track4 = element.getElementsByTagName("IssuerName");
			line = (Element) track4.item(0);
			responseDataMap.put("IssuerName", getCharacterDataFromElement(line));
			// System.out.println("IssuerName: "
			// + getCharacterDataFromElement(line));

			NodeList track5 = element.getElementsByTagName("SchemeType");
			line = (Element) track5.item(0);
			responseDataMap.put("SchemeType", getCharacterDataFromElement(line));
			// System.out.println("SchemeType: "
			// + getCharacterDataFromElement(line));

			NodeList nodes1 = doc.getElementsByTagName("SelectedAcquirer");
			// iterate the employees
			//Element element1 = (Element) nodes1.item(0);

			//NodeList track6 = element1.getElementsByTagName("ID");
			//Element line1 = (Element) track6.item(0);
			//responseDataMap.put("SelectedAcquirerID", getCharacterDataFromElement(line1));
			// System.out.println("ID: " + getCharacterDataFromElement(line1));

			/*NodeList track7 = element1.getElementsByTagName("Name");
			 line1 = (Element) track7.item(0);
			responseDataMap.put("SelectedAquirerName", getCharacterDataFromElement(line1));*/
			// System.out.println("Name: " +
			// getCharacterDataFromElement(line1));

			/*NodeList track8 = element1.getElementsByTagName("DiscountRate");
			 line1 = (Element) track8.item(0);
			responseDataMap.put("SelectedAquirerDiscountRate", getCharacterDataFromElement(line1));*/
			// System.out.println("DiscountRate: "
			// + getCharacterDataFromElement(line1));

			/*NodeList track9 = element1.getElementsByTagName("Status");
			line1 = (Element) track9.item(0);
			responseDataMap.put("SelectedAquirerStatus", getCharacterDataFromElement(line1));*/
			// System.out.println("Status: " +
			// getCharacterDataFromElement(line1));

			NodeList nodes2 = doc.getElementsByTagName("Merchant");
			// iterate the employees
			Element element2 = (Element) nodes2.item(0);

			NodeList track10 = element2.getElementsByTagName("ID");
			Element line2 = (Element) track10.item(0);
			responseDataMap.put("MerchantID", getCharacterDataFromElement(line2));
			// System.out.println("ID: " + getCharacterDataFromElement(line2));

			NodeList track11 = element2.getElementsByTagName("Name");
			line2 = (Element) track11.item(0);
			responseDataMap.put("MerchantName", getCharacterDataFromElement(line2));
			// System.out.println("Name: " +
			// getCharacterDataFromElement(line2));

			NodeList track12 = element2.getElementsByTagName("Address");
			line2 = (Element) track12.item(0);
			responseDataMap.put("MerchantAddress", getCharacterDataFromElement(line2));
			// System.out
			// .println("Address: " + getCharacterDataFromElement(line2));

			NodeList track13 = element2.getElementsByTagName("City");
			line2 = (Element) track13.item(0);
			responseDataMap.put("MerchantCity", getCharacterDataFromElement(line2));
			// System.out.println("City: " +
			// getCharacterDataFromElement(line2));

			NodeList nodes0 = doc.getElementsByTagName("TXT");
			// iterate the employees
			Element transactionTime = (Element) nodes0.item(0);
			responseDataMap.put("TransactionTime", getCharacterDataFromElement(transactionTime));
			NodeList TT = doc.getElementsByTagName("TT");
			// iterate the employees
			Element txnType = (Element) TT.item(0);
			responseDataMap.put("TransactionType", getCharacterDataFromElement(txnType));

			NodeList HostResponse = doc.getElementsByTagName("HostResponse");
			// iterate the employees
			Element HostResponseElem = (Element) HostResponse.item(0);

			NodeList track14 = HostResponseElem.getElementsByTagName("ResponseCode");
			Element line3 = (Element) track14.item(0);
			responseDataMap.put("HostResponseCode", getCharacterDataFromElement(line3));
			// System.out.println("ResponseCode: "
			// + getCharacterDataFromElement(line3));

			NodeList track15 = HostResponseElem.getElementsByTagName("ResponseMessage");
			line3 = (Element) track15.item(0);
			responseDataMap.put("HostResponseMessage", getCharacterDataFromElement(line3));
			// System.out.println("ResponseMessage: "
			// + getCharacterDataFromElement(line3));

			NodeList track16 = HostResponseElem.getElementsByTagName("ApprovalCode");
			line3 = (Element) track16.item(0);
			responseDataMap.put("HostResponseApprovalCode", getCharacterDataFromElement(line3));
			// System.out.println("ApprovalCode: "
			// + getCharacterDataFromElement(line3));

			NodeList track17 = HostResponseElem.getElementsByTagName("RetrievalReferenceNumber");
			line3 = (Element) track17.item(0);
			responseDataMap.put("HostResponseRetrievelRefNumber", getCharacterDataFromElement(line3));
			// System.out.println("RetrievalReferenceNumber: "
			// + getCharacterDataFromElement(line3));

			NodeList State = doc.getElementsByTagName("State");
			// iterate the employees
			Element StateElem = (Element) State.item(0);

			// Change for Rev 1.0(AAKASH EYLLP):Starts
			NodeList track18 = StateElem.getElementsByTagName("TerminalID");
			// NodeList track18 = StateElem.getElementsByTagName("TID");
			// Change for Rev 1.0(AAKASH EYLLP):Ends

			Element line4 = (Element) track18.item(0);
			responseDataMap.put("StateTID", getCharacterDataFromElement(line4));
			// System.out.println("TID: " + getCharacterDataFromElement(line4));

			NodeList track19 = StateElem.getElementsByTagName("InvoiceNumber");
			line4 = (Element) track19.item(0);
			responseDataMap.put("StateInvoiceNumber", getCharacterDataFromElement(line4));
			// System.out.println("InvoiceNumber: "
			// + getCharacterDataFromElement(line4));

			NodeList track20 = StateElem.getElementsByTagName("BatchNumber");
			line4 = (Element) track20.item(0);
			responseDataMap.put("StateBatchNumber", getCharacterDataFromElement(line4));
			// System.out.println("BatchNumber: "
			// + getCharacterDataFromElement(line4));

			NodeList track21 = StateElem.getElementsByTagName("AcquirerName");
			line4 = (Element) track21.item(0);
			responseDataMap.put("StateAquirerName", getCharacterDataFromElement(line4));
			// System.out.println("AcquirerName: "
			// + getCharacterDataFromElement(line4));

			NodeList track22 = StateElem.getElementsByTagName("TransactionTime");
			line4 = (Element) track22.item(0);
			responseDataMap.put("StateTransactionTime", getCharacterDataFromElement(line4));
			// System.out.println("TransactionTime: "
			// + getCharacterDataFromElement(line4));

			NodeList track23 = StateElem.getElementsByTagName("Amount");
			line4 = (Element) track23.item(0);

			String amt = getCharacterDataFromElement(line4);
			if (amt != null && !amt.equals(" ")) {
				String amtStrWithDecimal = new BigDecimal(amt).divide(new BigDecimal(100), 2, 2).toString();
				responseDataMap.put("StateAmount", amtStrWithDecimal);
			} else {
				responseDataMap.put("StateAmount", getCharacterDataFromElement(line4));
			}

			NodeList track24 = StateElem.getElementsByTagName("Discount");
			line4 = (Element) track24.item(0);
			String cashBackAmt = getCharacterDataFromElement(line4);
			if (cashBackAmt != null && !cashBackAmt.equals(" ") && !cashBackAmt.trim().equals("")) {
				String cashBackAmtWithDecimal = new BigDecimal(cashBackAmt).divide(new BigDecimal(100), 2, 2)
						.toString();
				responseDataMap.put("StateDiscount", cashBackAmtWithDecimal);
			} else {
				responseDataMap.put("StateDiscount", getCharacterDataFromElement(line4));
			}

			NodeList track25 = StateElem.getElementsByTagName("TotalAmount");
			line4 = (Element) track25.item(0);
			responseDataMap.put("StateTotalAmount", getCharacterDataFromElement(line4));

			NodeList track26 = StateElem.getElementsByTagName("StatusCode");
			line4 = (Element) track26.item(0);
			responseDataMap.put("StateStatusCode", getCharacterDataFromElement(line4));

			NodeList track27 = StateElem.getElementsByTagName("StatusMessage");
			line4 = (Element) track27.item(0);
			responseDataMap.put("StateStatusMessage", getCharacterDataFromElement(line4));

			NodeList track28 = StateElem.getElementsByTagName("Points");
			line4 = (Element) track28.item(0);
			responseDataMap.put("Points", getCharacterDataFromElement(line4));
			NodeList emiTag = doc.getElementsByTagName("EMI");
			if (emiTag != null) {
				Element emiElement = (Element) emiTag.item(0);

				// Override AcquirerName and IssuerName from EMI tag in
				// responseMap
				// This is done as per the suggestion from Innoviti (Sunil)
				// Ideally there is no difference between the two
				NodeList emiData = emiElement.getElementsByTagName("AcquirerName");
				line4 = (Element) emiData.item(0);
				String nodeStrValue = getCharacterDataFromElement(line4);
				if (nodeStrValue != null) {
					responseDataMap.put("StateAquirerName", nodeStrValue);
					responseDataMap.put("SelectedAquirerName", nodeStrValue);
				}

				emiData = emiElement.getElementsByTagName("IssuerName");
				line4 = (Element) emiData.item(0);
				nodeStrValue = getCharacterDataFromElement(line4);
				if (nodeStrValue != null) {
					responseDataMap.put("IssuerName", nodeStrValue);
				}

				emiData = emiElement.getElementsByTagName("Tenure");
				line4 = (Element) emiData.item(0);
				responseDataMap.put("EMITenure", getCharacterDataFromElement(line4));
			}

		} catch (Exception e) {
			logger.warn(e);
		}
		return responseDataMap;
	}
	public HashMap parseResponseDate(String output) {

		HashMap responseDataMap = new LinkedHashMap();

		String xmlRecords = output;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlRecords));

			Document doc = db.parse(is);

			NodeList nodes = doc.getElementsByTagName("Card");
			// iterate the employees
			Element element = (Element) nodes.item(0);

			NodeList track = element.getElementsByTagName("IsManualEntry");
			Element line = (Element) track.item(0);
			responseDataMap.put("IsManualEntry", getCharacterDataFromElement(line));
			// System.out.println("IsManualEntry: "
			// + getCharacterDataFromElement(line));

			NodeList track1 = element.getElementsByTagName("CardNumber");
			line = (Element) track1.item(0);
			responseDataMap.put("CardNumber", getCharacterDataFromElement(line));
			// System.out.println("CardNumber: "
			// + getCharacterDataFromElement(line));

			NodeList track2 = element.getElementsByTagName("MM");
			line = (Element) track2.item(0);
			responseDataMap.put("MM", getCharacterDataFromElement(line));
			// System.out.println("MM: " + getCharacterDataFromElement(line));

			NodeList track3 = element.getElementsByTagName("YY");
			line = (Element) track3.item(0);
			responseDataMap.put("YY", getCharacterDataFromElement(line));
			// System.out.println("YY: " + getCharacterDataFromElement(line));

			NodeList track4 = element.getElementsByTagName("IssuerName");
			line = (Element) track4.item(0);
			responseDataMap.put("IssuerName", getCharacterDataFromElement(line));
			// System.out.println("IssuerName: "
			// + getCharacterDataFromElement(line));

			NodeList track5 = element.getElementsByTagName("SchemeType");
			line = (Element) track5.item(0);
			responseDataMap.put("SchemeType", getCharacterDataFromElement(line));
			// System.out.println("SchemeType: "
			// + getCharacterDataFromElement(line));

			NodeList nodes1 = doc.getElementsByTagName("SelectedAcquirer");
			// iterate the employees
			//Element element1 = (Element) nodes1.item(0);

			//NodeList track6 = element1.getElementsByTagName("ID");
			//Element line1 = (Element) track6.item(0);
			//responseDataMap.put("SelectedAcquirerID", getCharacterDataFromElement(line1));
			// System.out.println("ID: " + getCharacterDataFromElement(line1));

			/*NodeList track7 = element1.getElementsByTagName("Name");
			 line1 = (Element) track7.item(0);
			responseDataMap.put("SelectedAquirerName", getCharacterDataFromElement(line1));*/
			// System.out.println("Name: " +
			// getCharacterDataFromElement(line1));

			/*NodeList track8 = element1.getElementsByTagName("DiscountRate");
			 line1 = (Element) track8.item(0);
			responseDataMap.put("SelectedAquirerDiscountRate", getCharacterDataFromElement(line1));*/
			// System.out.println("DiscountRate: "
			// + getCharacterDataFromElement(line1));

			/*NodeList track9 = element1.getElementsByTagName("Status");
			line1 = (Element) track9.item(0);
			responseDataMap.put("SelectedAquirerStatus", getCharacterDataFromElement(line1));*/
			// System.out.println("Status: " +
			// getCharacterDataFromElement(line1));

			NodeList nodes2 = doc.getElementsByTagName("Merchant");
			// iterate the employees
			Element element2 = (Element) nodes2.item(0);

			NodeList track10 = element2.getElementsByTagName("ID");
			Element line2 = (Element) track10.item(0);
			responseDataMap.put("MerchantID", getCharacterDataFromElement(line2));
			// System.out.println("ID: " + getCharacterDataFromElement(line2));

			NodeList track11 = element2.getElementsByTagName("Name");
			line2 = (Element) track11.item(0);
			responseDataMap.put("MerchantName", getCharacterDataFromElement(line2));
			// System.out.println("Name: " +
			// getCharacterDataFromElement(line2));

			NodeList track12 = element2.getElementsByTagName("Address");
			line2 = (Element) track12.item(0);
			responseDataMap.put("MerchantAddress", getCharacterDataFromElement(line2));
			// System.out
			// .println("Address: " + getCharacterDataFromElement(line2));

			NodeList track13 = element2.getElementsByTagName("City");
			line2 = (Element) track13.item(0);
			responseDataMap.put("MerchantCity", getCharacterDataFromElement(line2));
			// System.out.println("City: " +
			// getCharacterDataFromElement(line2));

			NodeList HostResponse = doc.getElementsByTagName("HostResponse");
			// iterate the employees
			Element HostResponseElem = (Element) HostResponse.item(0);

			NodeList track14 = HostResponseElem.getElementsByTagName("ResponseCode");
			Element line3 = (Element) track14.item(0);
			responseDataMap.put("HostResponseCode", getCharacterDataFromElement(line3));
			// System.out.println("ResponseCode: "
			// + getCharacterDataFromElement(line3));

			NodeList track15 = HostResponseElem.getElementsByTagName("ResponseMessage");
			line3 = (Element) track15.item(0);
			responseDataMap.put("HostResponseMessage", getCharacterDataFromElement(line3));
			// System.out.println("ResponseMessage: "
			// + getCharacterDataFromElement(line3));

			NodeList track16 = HostResponseElem.getElementsByTagName("ApprovalCode");
			line3 = (Element) track16.item(0);
			responseDataMap.put("HostResponseApprovalCode", getCharacterDataFromElement(line3));
			// System.out.println("ApprovalCode: "
			// + getCharacterDataFromElement(line3));

			NodeList track17 = HostResponseElem.getElementsByTagName("RetrievalReferenceNumber");
			line3 = (Element) track17.item(0);
			responseDataMap.put("HostResponseRetrievelRefNumber", getCharacterDataFromElement(line3));
			// System.out.println("RetrievalReferenceNumber: "
			// + getCharacterDataFromElement(line3));

			NodeList State = doc.getElementsByTagName("State");
			// iterate the employees
			Element StateElem = (Element) State.item(0);

			// Change for Rev 1.0(AAKASH EYLLP):Starts
			NodeList track18 = StateElem.getElementsByTagName("TerminalID");
			// NodeList track18 = StateElem.getElementsByTagName("TID");
			// Change for Rev 1.0(AAKASH EYLLP):Ends

			Element line4 = (Element) track18.item(0);
			responseDataMap.put("StateTID", getCharacterDataFromElement(line4));
			// System.out.println("TID: " + getCharacterDataFromElement(line4));

			NodeList track19 = StateElem.getElementsByTagName("InvoiceNumber");
			line4 = (Element) track19.item(0);
			responseDataMap.put("StateInvoiceNumber", getCharacterDataFromElement(line4));
			// System.out.println("InvoiceNumber: "
			// + getCharacterDataFromElement(line4));

			NodeList track20 = StateElem.getElementsByTagName("BatchNumber");
			line4 = (Element) track20.item(0);
			responseDataMap.put("StateBatchNumber", getCharacterDataFromElement(line4));
			// System.out.println("BatchNumber: "
			// + getCharacterDataFromElement(line4));

			NodeList track21 = StateElem.getElementsByTagName("AcquirerName");
			line4 = (Element) track21.item(0);
			responseDataMap.put("StateAquirerName", getCharacterDataFromElement(line4));
			// System.out.println("AcquirerName: "
			// + getCharacterDataFromElement(line4));

			NodeList track22 = StateElem.getElementsByTagName("TransactionTime");
			line4 = (Element) track22.item(0);
			responseDataMap.put("StateTransactionTime", getCharacterDataFromElement(line4));
			// System.out.println("TransactionTime: "
			// + getCharacterDataFromElement(line4));

			NodeList track23 = StateElem.getElementsByTagName("Amount");
			line4 = (Element) track23.item(0);

			String amt = getCharacterDataFromElement(line4);
			if (amt != null && !amt.equals(" ")) {
				String amtStrWithDecimal = new BigDecimal(amt).divide(new BigDecimal(100), 2, 2).toString();
				responseDataMap.put("StateAmount", amtStrWithDecimal);
			} else {
				responseDataMap.put("StateAmount", getCharacterDataFromElement(line4));
			}

			NodeList track24 = StateElem.getElementsByTagName("Discount");
			line4 = (Element) track24.item(0);
			String cashBackAmt = getCharacterDataFromElement(line4);
			if (cashBackAmt != null && !cashBackAmt.equals(" ") && !cashBackAmt.trim().equals("")) {
				String cashBackAmtWithDecimal = new BigDecimal(cashBackAmt).divide(new BigDecimal(100), 2, 2)
						.toString();
				responseDataMap.put("StateDiscount", cashBackAmtWithDecimal);
			} else {
				responseDataMap.put("StateDiscount", getCharacterDataFromElement(line4));
			}

			NodeList track25 = StateElem.getElementsByTagName("TotalAmount");
			line4 = (Element) track25.item(0);
			responseDataMap.put("StateTotalAmount", getCharacterDataFromElement(line4));

			NodeList track26 = StateElem.getElementsByTagName("StatusCode");
			line4 = (Element) track26.item(0);
			responseDataMap.put("StateStatusCode", getCharacterDataFromElement(line4));

			NodeList track27 = StateElem.getElementsByTagName("StatusMessage");
			line4 = (Element) track27.item(0);
			responseDataMap.put("StateStatusMessage", getCharacterDataFromElement(line4));

			NodeList track28 = StateElem.getElementsByTagName("Points");
			line4 = (Element) track28.item(0);
			responseDataMap.put("Points", getCharacterDataFromElement(line4));
			NodeList emiTag = doc.getElementsByTagName("EMI");
			if (emiTag != null) {
				Element emiElement = (Element) emiTag.item(0);

				// Override AcquirerName and IssuerName from EMI tag in
				// responseMap
				// This is done as per the suggestion from Innoviti (Sunil)
				// Ideally there is no difference between the two
				NodeList emiData = emiElement.getElementsByTagName("AcquirerName");
				line4 = (Element) emiData.item(0);
				String nodeStrValue = getCharacterDataFromElement(line4);
				if (nodeStrValue != null) {
					responseDataMap.put("StateAquirerName", nodeStrValue);
					responseDataMap.put("SelectedAquirerName", nodeStrValue);
				}

				emiData = emiElement.getElementsByTagName("IssuerName");
				line4 = (Element) emiData.item(0);
				nodeStrValue = getCharacterDataFromElement(line4);
				if (nodeStrValue != null) {
					responseDataMap.put("IssuerName", nodeStrValue);
				}

				emiData = emiElement.getElementsByTagName("Tenure");
				line4 = (Element) emiData.item(0);
				responseDataMap.put("EMITenure", getCharacterDataFromElement(line4));
			}

		} catch (Exception e) {
			logger.warn(e);
		}
		return responseDataMap;
	}

	public HashMap parsePrintingData(String output, HashMap responseMap) {
		Document doc = parseFile(output);

		Node root = doc.getDocumentElement();

		writeDocumentToOutput(root, 0);

		return responseMap;

	}

	public final static String getElementValue(Node elem) {
		Node kid;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
					if (kid.getNodeType() == Node.TEXT_NODE) {
						return kid.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	public HashMap writeDocumentToOutput(Node node, int indent) {
		// get element name
		String nodeName = node.getNodeName();
		if (nodeName.equals("PrintLine")) {
			responseMap.put("PrintLine" + i, getElementValue(node));
			i++;
			if (i == 20) {
				// System.out.println(responseMap);
			}
		}

		String nodeValue = getElementValue(node);
		NamedNodeMap attributes = node.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
		}
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				writeDocumentToOutput(child, indent + 2);
			}
		}
		return responseMap;
	}

	public Document parseFile(String output) {

		DocumentBuilder docBuilder;
		Document doc = null;
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(true);
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {

			return null;
		}
		try {
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(output));
			doc = docBuilder.parse(is);
		} catch (SAXException e) {
			logger.warn("Wrong XML file structure: " + e);
			return null;
		} catch (IOException e) {
			logger.warn("Could not read source file: " + e);
		}
		// System.out.println("XML file parsed");
		return doc;
	}

	public static String getCharacterDataFromElement(Element e) {
		// Rev 1.1 start
		if (e == null || e.getFirstChild() == null)
			return null;
		// Rev 1.1 end
		else {
			Node child = e.getFirstChild();
			CharacterData cd = null;
			try {
				cd = (CharacterData) child;
			} catch (Exception eb) {

			}
			if (cd != null)
				return cd.getData();
			else
				return null;
		}
	}

	/*
	 * String xmlRecords =
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

	public void printChargeSlipData(BusIfc bus, HashMap response) {
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
			// akanksha

			ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
			boolean Value = false;
			try {
				String check = pm.getStringValue("PrintingOnlineCreditCardChargeSlip");
				if (check.equals("Y"))
					Value = true;
				else {
					Value = false;
				}
			} catch (ParameterException pe) {

			}

			// akanksha
			if (Value) {
				pda.printNormal(POSPrinterConst.PTR_S_RECEIPT, getFormattedReportForCustomer(response) + sixBlankLines);
				// pda.printNormal(POSPrinterConst.PTR_S_RECEIPT,getFormattedReport(response)
				// + sixBlankLines);
				pda.cutPaper(100);

				pda.printNormal(POSPrinterConst.PTR_S_RECEIPT, getFormattedReportForMerchant(response) + sixBlankLines);
				// pda.printNormal(POSPrinterConst.PTR_S_RECEIPT,getFormattedReport(response)
				// + sixBlankLines);
				pda.cutPaper(100);
			}
		} catch (DeviceException e) {
			ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
			DialogBeanModel model = new DialogBeanModel();
			String msg[] = new String[1];
			UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
			msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
					BundleConstantsIfc.PRINTER_OFFLINE);
			model.setResourceID("RetryCancel");
			model.setType(DialogScreensIfc.RETRY_CANCEL);
			model.setArgs(msg);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		}
	}

	public String getFormattedReportForMerchant(HashMap map) {
		StringBuffer buff = new StringBuffer();
		buff.append("***************************************");
		// buff.append(RegisterReport.NEW_LINE);
		buff.append(System.getProperty("line.separator"));
		for (int i = 20; i < 40; i++) {

			// buff.append(RegisterReport.NEW_LINE);
			String data = map.get("PrintLine" + i).toString();
			// if(data.length() < 17)
			// {
			// data = " " + data;
			// }
			// /Gaurav
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

			// Gaurav
			// commented printing code
			/*
			 * buff.append(getFormattedLine(data, null, null)); //
			 * System.getProperty("line.separator");
			 * buff.append(System.getProperty("line.separator")); if
			 * (("THANK YOU").equals(data.trim())) {
			 * buff.append(RegisterReport.NEW_LINE);
			 * buff.append(RegisterReport.NEW_LINE);
			 * buff.append(System.getProperty("line.separator"));
			 *
			 * }
			 */
		}
		return (buff.toString());
	}

	public String getFormattedReportForCustomer(HashMap map) {
		StringBuffer buff = new StringBuffer();
		buff.append("***************************************");
		// buff.append(RegisterReport.NEW_LINE);
		buff.append(System.getProperty("line.separator"));
		for (int i = 0; i < 20; i++) {

			// buff.append(RegisterReport.NEW_LINE);
			String data = map.get("PrintLine" + i).toString();
			// /Gaurav
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

			// Gaurav

			// if(data.length() < 22)
			// {
			// data = " " + data;
			// }
			// commented printing code
			/*
			 * buff.append(getFormattedLine(data, null, null)); //
			 * System.getProperty("line.separator");
			 * buff.append(System.getProperty("line.separator")); if
			 * (("THANK YOU").equals(data.trim())) {
			 * buff.append(RegisterReport.NEW_LINE);
			 * buff.append(RegisterReport.NEW_LINE);
			 * buff.append(System.getProperty("line.separator"));
			 *
			 * }
			 */
		}

		return (buff.toString());
	}

	protected String getFormattedLine(String descString, String countString, String moneyString) {

		int delta = 0;
		if ((descString != null) && (countString != null) && (moneyString != null)) {
			if ((39 - moneyString.length()) < 28) {
				delta = 28 - (39 - moneyString.length());

			}
		}
		// String SPACES = " ";
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

}
