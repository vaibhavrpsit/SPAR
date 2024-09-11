/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Jan 06, 2017		Ashish Yadav		Changes for Online redemption loyalty OTP FES	
 *
 ********************************************************************************/
package max.retail.stores.pos.services.tender;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import max.retail.stores.domain.arts.MAXLoyaltyDataTransaction;
import max.retail.stores.domain.loyalty.MAXLoyaltyConstants;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderPurchaseOrder;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Deletes all tenders from a transaction
 */
public class MAXDeleteTendersActionSite extends PosSiteActionAdapter {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7114829431044620510L;

	private static int responseCode;

	public void arrive(BusIfc bus) {
		// Changes ends for rev 1.0 (Ashish : Loyalty OTP)
		System.out.println("MAXDeleteTendersActionSite");
		
		  /**Changes start for rev 1.1**/
		  boolean isLoyaltyTenderlineitemExists=false;
		    /**Changes start for rev 1.1**/
			HashMap reversalAttributes = new HashMap();
	           
			// Get and delete all tenders from the transaction
			TenderCargo cargo = (TenderCargo) bus.getCargo();

			TenderADOIfc[] tenders = cargo.getCurrentTransactionADO().getTenderLineItems(TenderLineItemCategoryEnum.ALL);
	             /**Changes start for rev 1.1**/
		     for (int i = 0; i < tenders.length; i++) {
				if (tenders[i].toLegacy() instanceof TenderPurchaseOrder) {
				isLoyaltyTenderlineitemExists=true;
				break;
				}
			}
	              /**Changes end for rev 1.1**/

			for (int i = 0; i < tenders.length; i++) {
				// Changes for MAX CRM FES
				// Check the tenderADO for Purchase order
				/**Changes start for rev 1.1**/
				//if (tenders[i].toLegacy() instanceof MAXTenderLoyaltyPoints) {
	                           if (isLoyaltyTenderlineitemExists) { 
				//	POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		    	//	ui.showScreen(MAXPOSUIManagerIfc.MAX_TIC_LOYALTY_POINTS);
	                POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
					DialogBeanModel beanModel = new DialogBeanModel();
					beanModel.setResourceID("DeleteTender");
					beanModel.setType(DialogScreensIfc.ERROR);
					beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.FAILURE);
					//model.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.NO);
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
					//if(tenders.length==1){
					
				    return;
	                          /*
					MAXLoyaltyDataTransaction loyaltyDataTransaction = null;
					loyaltyDataTransaction = (MAXLoyaltyDataTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.LOYALTY_DATA_TRANSACTION);
					// Changes for Rev 1.1 Starts 
					//MAXCustomerIfc customer = (MAXCustomerIfc) cargo.getCustomer();
					MAXCustomerIfc customer = (MAXCustomerIfc) ((AbstractTenderableTransaction) cargo.getCurrentTransactionADO().toLegacy()).getTicCustomer();
					reversalAttributes.put(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER, customer.getLoyaltyCardNumber());
					// Changes for Rev 1.1 ends 
					try {
						// Encrypt the values the value of TIC Number through
						// encryption function defined in DB
						reversalAttributes = loyaltyDataTransaction.encryptValue(reversalAttributes);
					} catch (DataException excep) {
						Logger.getLogger(MAXDeleteTendersActionSite.class.getName()).log(Level.SEVERE, null, excep);
					}

					MAXSaleReturnTransaction transaction = (MAXSaleReturnTransaction) cargo.getCurrentTransactionADO().toLegacy();
					// Changes for Rev 1.2 Starts
					CurrencyIfc tenderAmount = tenders[i].getAmount();

					// Method use to populate HashMap with the values from Cargo
					reversalAttributes = populateHashMap(transaction, reversalAttributes, tenderAmount);
					// Changes for Rev 1.2 Ends

					try {
						// Saving the request in the DB
						loyaltyDataTransaction.saveRequest(reversalAttributes);
					} catch (DataException e) {
						logger.error("DataException::" + e.getMessage() + "");
					}
					// Prepare URL for sending the request with the parameters
					String urlParameters = createURL(reversalAttributes, bus);

					// Set URL with parameters
					// String targetURL =
					// "http://www.lscircle.in/crmresponse/Response?"+
					// urlParameters;
					String URL = Gateway.getProperty("application", "LoyaltyWebServiceURL", null);
					String targetURL = URL + urlParameters;
					// call for response
					if (urlParameters != null) {
						// Defined for executing the web request
						String response = executePost(targetURL, urlParameters);

						// when Successful response is received
						// Response Code - 200 denotes successful response
						if (responseCode == 200 && !response.equalsIgnoreCase("") && !response.equalsIgnoreCase("Timeout")) {
							processSuccessResponse(response, loyaltyDataTransaction, reversalAttributes);
						}

						// Time Out Case handling
						// validating the error code for timeout
						// 408- HTTP_CLIENT_TIMEOUT && 504- HTTP_GATEWAY_TIMEOUT
						// if timeout then save timeout request other wise wait for
						// response
						else if (response.equalsIgnoreCase("Timeout")) {
							reversalAttributes.put(MAXLoyaltyConstants.REQUEST_STATUS, MAXLoyaltyConstants.TIMEOUT);
							reversalAttributes.put(MAXLoyaltyConstants.FLAG, MAXLoyaltyConstants.TIMEOUT_FLAG);
							try {
								loyaltyDataTransaction.updateRequest(reversalAttributes);
							} catch (DataException e) {
								logger.error("DataException::" + e.getMessage() + "");
							}
							Logger.getLogger("Error In getting Response:::" + MAXDeleteTendersActionSite.class.getName());
						}
					} else {
						Logger.getLogger("Error In forming URL:::" + MAXDeleteTendersActionSite.class.getName());
					}
					// if timeout then save timeout request other wise wait for
					// response
			
			*/
			/**Changes end for rev 1.1**/
				}
				// Code for CRM

				// delete this tender from the transaction
	                        /**Changes start for rev 1.1**/
			//	cargo.getCurrentTransactionADO().removeTender(tenders[i]);
	                    	if(!isLoyaltyTenderlineitemExists){
				cargo.getCurrentTransactionADO().removeTender(tenders[i]);
			}
	                      /**Changes end for rev 1.1**/
				// journal the deletion
				JournalFactoryIfc jrnlFact = null;
				try {
					jrnlFact = JournalFactory.getInstance();
				} catch (ADOException e) {
					logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
					throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
				}
				RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
				registerJournal.journal(tenders[i], JournalFamilyEnum.TENDER, JournalActionEnum.DELETE);
			}

			// go back to calling service (Mailing same letter that invoked this
			// functionality)
			bus.mail(new Letter("Undo"), BusIfc.CURRENT);
		}
	// Changes ends for rev 1.0 (Ashish : Loyalty OTP)

	/*
	 * Method use for sending web request
	 */
	public static String executePost(String targetURL, String urlParameters) {
		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL(targetURL);
			String timeOut = Gateway.getProperty("application", "LoyaltytimeOutInMilliSeconds", "5000");
			System.getProperties().setProperty("sun.net.client.defaultConnectTimeout", timeOut);
			System.getProperties().setProperty("sun.net.client.defaultReadTimeout", timeOut);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			// Get Response
			responseCode = connection.getResponseCode();

			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();
		} catch (Exception e) {
			logger.error("Error in sending Request" + e.getMessage() + "");
			try {
				responseCode = connection.getResponseCode();
			} catch (IOException e1) {
				logger.error("IO Exception Caught::" + e1.getMessage() + "");
				return "Timeout";
			}
			return "";

		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * Creates the URL with the input parameters needed by CRM System
	 * 
	 * @param bus
	 * @param requestInfo
	 */
	protected String createURL(HashMap requestInfo, BusIfc bus) {
		String urlParameters;
		try {
			urlParameters = URLEncoder.encode("flag", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aMsgId", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.MESSAGE_ID).toString(), "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aReqT1", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.REQUEST_TYPE_A).toString(), "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aTimOt", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.REQUEST_TIME_OUT).toString(), "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aStoId", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.STORE_ID).toString(), "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aRegId", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.REGISTER_ID).toString(), "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aTilId", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.TILL_ID).toString(), "UTF-8");
			
			SimpleDateFormat sm = new SimpleDateFormat("dd-MMM-yyyy");
			Date date = new Date(requestInfo.get(MAXLoyaltyConstants.INVOICE_BUSINESS_DATE).toString());
			String mdy = sm.format(date);
			
			urlParameters += "&" + URLEncoder.encode("aBusDt", "UTF-8") + "="
					+ URLEncoder.encode(/*requestInfo.get(MAXLoyaltyConstants.INVOICE_BUSINESS_DATE).toString()*/mdy, "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aInvNo", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.INVOICE_NUMBER).toString(), "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aInvTo", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.TRAN_TOTAL_AMOUNT).toString(), "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aRedAt", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.SETTLE_TOTAL_AMOUNT).toString(), "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aReqTp", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.REQUEST_TYPE_B).toString(), "UTF-8");
			urlParameters += "&" + URLEncoder.encode("aLoyNo", "UTF-8") + "="
					+ URLEncoder.encode(requestInfo.get(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER).toString(), "UTF-8");
			return urlParameters;
		} catch (UnsupportedEncodingException e) {
			Logger.getLogger(MAXDeleteTendersActionSite.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
	}

	/**
	 * Use for populating the HashMap with needed values from Cargo
	 * 
	 * @param targetURL
	 * @param urlParameters
	 */
	private HashMap populateHashMap(MAXSaleReturnTransaction transaction, HashMap reversalAttributes, CurrencyIfc tenderAmount) {
		EYSDate date = DomainGateway.getFactory().getEYSDateInstance();
		// Get value from application.properties
		String URL = Gateway.getProperty("application", "LoyaltyWebServiceURL", null);
		String timeOut = Gateway.getProperty("application", "LoyaltytimeOutInMilliSeconds", null);

		StringBuffer transanctionId = new StringBuffer();
		transanctionId.append(transaction.getTransactionID());
		transanctionId.append(date.toFormattedString(MAXLoyaltyConstants.NEW_DATE_FORMAT));
		reversalAttributes.put(MAXLoyaltyConstants.MESSAGE_ID, transanctionId.toString());
		reversalAttributes.put(MAXLoyaltyConstants.REQUEST_TYPE_A, MAXLoyaltyConstants.REGULAR_REQUEST);
		reversalAttributes.put(MAXLoyaltyConstants.REQUEST_STATUS, MAXLoyaltyConstants.REQUESTED);
		reversalAttributes.put(MAXLoyaltyConstants.STORE_ID, transaction.getWorkstation().getStoreID());
		reversalAttributes.put(MAXLoyaltyConstants.TILL_ID, transaction.getTillID());
		reversalAttributes.put(MAXLoyaltyConstants.REGISTER_ID, transaction.getWorkstation().getWorkstationID());
		reversalAttributes.put(MAXLoyaltyConstants.INVOICE_BUSINESS_DATE, transaction.getBusinessDay());
		reversalAttributes.put(MAXLoyaltyConstants.INVOICE_NUMBER, transaction.getTransactionID());
		String grandTotalStr = transaction.getTransactionTotals().getGrandTotal().toString();
		reversalAttributes.put(MAXLoyaltyConstants.TRAN_TOTAL_AMOUNT, grandTotalStr);
		// Changes for Rev 1.2 Starts
		reversalAttributes.put(MAXLoyaltyConstants.SETTLE_TOTAL_AMOUNT, tenderAmount);// transaction.getTransactionTotals().getAmountTender());
		// Changes for Rev 1.2 Ends
		reversalAttributes.put(MAXLoyaltyConstants.REQUEST_TYPE_B, MAXLoyaltyConstants.RELEASE);
		reversalAttributes.put(MAXLoyaltyConstants.REQUEST_DATE_TIME, date.toFormattedString(MAXLoyaltyConstants.DATE_FORMAT_NOW));
		reversalAttributes.put(MAXLoyaltyConstants.REQUEST_TIME_OUT, timeOut);
		reversalAttributes.put(MAXLoyaltyConstants.REQUEST_URL, URL);
		reversalAttributes.put(MAXLoyaltyConstants.TIME_OUT_REQUEST_MESSAGE_ID, null);
		return reversalAttributes;
	}

	/**
	 * Process Success Response from CRM
	 * 
	 * @param bus
	 * @param name
	 * @param dialogType
	 */
	protected void processSuccessResponse(String response, MAXLoyaltyDataTransaction loyaltyDataTransaction, HashMap reversalAttributes) {

		HashMap newvalue = new HashMap();

		String trimString = response.toString().trim();
		String[] splitStr = trimString.split("\r\r\r");

		String resMsg = splitStr[0];
		String valMsg = splitStr[1];

		String[] resFlag = resMsg.split(":");
		String[] messValue = valMsg.split(":");

		newvalue.put(resFlag[0], resFlag[1]);
		newvalue.put(messValue[0], messValue[1]);
		newvalue.put(MAXLoyaltyConstants.RESPONSE_APPROVED_VALUE, null);
		newvalue.put(MAXLoyaltyConstants.MESSAGE_ID, reversalAttributes.get(MAXLoyaltyConstants.MESSAGE_ID));
		newvalue.put(MAXLoyaltyConstants.FLAG, MAXLoyaltyConstants.RESPONSE_FLAG);
		newvalue.put(MAXLoyaltyConstants.REQUEST_STATUS, MAXLoyaltyConstants.RESPONSE_RECEIVED);

		// Update WEB REQUEST TABLE
		try {
			loyaltyDataTransaction.updateRequest(newvalue);
		} catch (DataException e) {
			Logger.getLogger("Data Error:::" + MAXDeleteTendersActionSite.class.getName());
		}
	}

}
