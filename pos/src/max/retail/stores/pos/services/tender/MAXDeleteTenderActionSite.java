/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 2.0     May 15,2023         Kumar Vaibhav       Changes for CN lock
 *	Rev	1.2 	June 23, 2019		Purushotham Reddy 	Changes for POS_Amazon Pay Integration 
 *	Rev	1.1 	Jan 06, 2017		Ashish Yadav		Changes for Online redemption loyalty OTP FES
 *	Rev	1.0 	Dec 20, 2016		Mansi Goel			Changes for Gift Card FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import max.retail.stores.domain.MAXAmazonPayResponse;
import max.retail.stores.domain.MAXEWalletResponse;
import max.retail.stores.domain.MAXMobikwikResponse;
import max.retail.stores.domain.MAXOxigenWalletCreditResponse;
import max.retail.stores.domain.MAXPaytmResponse;
import max.retail.stores.domain.arts.MAXCertificateTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXLoyaltyDataTransaction;
import max.retail.stores.domain.arts.MAXPaytmDataTransaction;
import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.loyalty.MAXLoyaltyConstants;
import max.retail.stores.domain.tender.MAXTenderAmazonPay;
import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.domain.tender.MAXTenderEWallet;
import max.retail.stores.domain.tender.MAXTenderMobikwik;
import max.retail.stores.domain.tender.MAXTenderPaytm;
import max.retail.stores.domain.tender.MAXTenderStoreCredit;
import max.retail.stores.domain.tender.MAXTenderStoreCreditIfc;
import max.retail.stores.domain.tender.amazonpay.MAXAmazonPayTenderConstants;
import max.retail.stores.domain.tender.mobikwik.MAXMobikwikTenderConstants;
import max.retail.stores.domain.tender.paytm.MAXPaytmTenderConstants;
import max.retail.stores.domain.transaction.MAXAbstractTenderableTransaction;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.utility.MAXGiftCard;
import max.retail.stores.pos.ado.tender.MAXTenderAmazonPayADO;
import max.retail.stores.pos.ado.tender.MAXTenderConstants;
import max.retail.stores.pos.ado.tender.MAXTenderCreditADO;
import max.retail.stores.pos.ado.tender.MAXTenderLoyaltyPointsADO;
import max.retail.stores.pos.ado.tender.MAXTenderMobikwikADO;
import max.retail.stores.pos.ado.tender.MAXTenderPaytmADO;
import max.retail.stores.pos.services.qc.MAXGiftCardUtilitiesQC;
import max.retail.stores.pos.services.sale.validate.MAXUtilityConstantsIfc;
import max.retail.stores.pos.services.tender.loyaltypoints.MAXCRMObj;
import max.retail.stores.pos.services.tender.oxigenwallet.MAXOxigenTenderConstants;
import max.retail.stores.pos.services.tender.wallet.amazonpay.MAXAmazonPayHelperUtiltiy;
import max.retail.stores.pos.services.tender.wallet.mobikwik.MAXMobikwikConfig;
import max.retail.stores.pos.services.tender.wallet.mobikwik.MAXMobikwikHelperUtiltiy;
import max.retail.stores.pos.services.tender.wallet.paytm.MAXPaytmConfig;
import max.retail.stores.pos.services.tender.wallet.paytm.MAXPaytmHelperUtiltiy;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXTenderBeanModel;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.tender.TenderCash;
import oracle.retail.stores.domain.tender.TenderCashIfc;
import oracle.retail.stores.domain.tender.TenderCoupon;
import oracle.retail.stores.domain.tender.TenderGiftCard;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.AbstractTenderableTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.ResourceBundleUtil;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.DeleteTenderActionSite;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qwikcilver.clientapi.svpos.GCPOS;

public class MAXDeleteTenderActionSite extends DeleteTenderActionSite {

	private static final long serialVersionUID = 6511967414190872818L;
	private static int responseCode;

	HttpURLConnection connection = null;

	public void arrive(BusIfc bus) {
		//System.out.println("Inside MAXDeleteTenderActionSite");
		String letter = "VoidSuccess";
		String dialogId = null;
		// Get the tender from the model and construct an ADO tender from it
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		MAXTenderBeanModel model = null;
		HashMap reversalAttributes = new HashMap();
		DialogBeanModel dModel = new DialogBeanModel();
		String currentLetter = bus.getCurrentLetter().getName();
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();

		boolean isReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
		boolean offlineFlag = false;
		TenderLineItemIfc tenderToRemove = null;
		// Create ADO tender
		TenderFactoryIfc factory = null;
		if (currentLetter.equals("Retry") || currentLetter.equals("Yes")) {
			tenderToRemove = cargo.getTenderLineItem();
		} else {
			model = (MAXTenderBeanModel) ui.getModel();
			tenderToRemove = (TenderLineItemIfc) model.getTenderToDelete();
			// System.out.println("TenderToRemoveValue 157 :"+tenderToRemove);
			logger.info("TenderToRemoveValue" + tenderToRemove);
		}
		try {
			factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
		} catch (ADOException e) {
			Logger.getLogger(MAXDeleteTenderActionSite.class.getName()).log(Level.SEVERE, null, e);
		}
		TenderADOIfc tenderADO = factory.createTender(tenderToRemove);
		((ADO) tenderADO).fromLegacy(tenderToRemove);
		TenderADOIfc cashBackTenderADO = null;

		if (tenderToRemove instanceof MAXTenderChargeIfc) {
			MAXTenderCargo tenderCargo = (MAXTenderCargo) bus.getCargo();
			MAXTenderChargeIfc tenderCharge = (MAXTenderChargeIfc) tenderToRemove;
			TenderADOIfc[] tenderVector = tenderCargo.getCurrentTransactionADO()
					.getTenderLineItems(TenderLineItemCategoryEnum.ALL);
			if (tenderVector != null && tenderVector.length > 0) {
				for (int i = 0; i < tenderVector.length; i++) {
					TenderADOIfc tenderObject = tenderVector[i];
					if (tenderObject instanceof MAXTenderCreditADO) {
						MAXTenderChargeIfc tenderChargeObj = (MAXTenderChargeIfc) tenderObject.toLegacy();
						if (tenderChargeObj.getCardType().startsWith("C-")
								&& tenderChargeObj.getResponseDate().equals(tenderCharge.getResponseDate())) {
							cashBackTenderADO = factory.createTender(tenderChargeObj);
							((ADO) cashBackTenderADO).fromLegacy(tenderChargeObj);
							break;
						}
					}
				}
			}
		}

		RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();

		if (tenderToRemove instanceof TenderCoupon) {
			Map tenderAttributes = cargo.getTenderAttributes();
			int couponType = getCouponType(bus, ((TenderCoupon) tenderToRemove).getCouponNumber());
			if (couponType == 1) {
				BigDecimal amt = (BigDecimal) tenderAttributes.get("foodTotals");
				BigDecimal amtTender = tenderToRemove.getAmountTender().getDecimalValue();
				amt = amt.add(amtTender);
				tenderAttributes.put("foodTotals", amt);
			}
			if (couponType == 0) {
				BigDecimal amt = (BigDecimal) tenderAttributes.get("nonFoodTotals");
				BigDecimal amtTender = tenderToRemove.getAmountTender().getDecimalValue();
				amt = amt.add(amtTender);
				tenderAttributes.put("nonFoodTotals", amt);
			}
		}
		//Added by Vaibhav LS Credit note code merging start--Rev 2.0
		if (tenderToRemove instanceof MAXTenderStoreCredit)
		{
			MAXTenderStoreCreditIfc tenderStoreCreditObj = (MAXTenderStoreCreditIfc)tenderToRemove;
			String storeCreditId = tenderStoreCreditObj.getStoreCreditID();
			
			MAXCertificateTransaction dataTransaction = null;
			if (logger.isInfoEnabled()) {
				logger.info("Updating Store Credit: " + tenderStoreCreditObj.getStoreCreditID() + " in ORCO.");
			}
			dataTransaction = (MAXCertificateTransaction) DataTransactionFactory
					.create(MAXDataTransactionKeys.MAXCERTIFICATE_TRANSACTION);
			boolean result = dataTransaction.updateStoreCreditLockStatus(storeCreditId,"N");
			if (result)
				logger.info("Successfuly updated Store Credit: " + tenderStoreCreditObj.getStoreCreditID() + " in ORCO.");
			else
			{
				//Rev 1.8 start
				//SECOND ATTEMPT
				logger.info("ERROR!!! First/Initial Attempt failed in updated Store Credit lock status: " + tenderStoreCreditObj.getStoreCreditID() + " in ORCO.");
				
				logger.info("Second Attempt. Attempting to update Store Credit lock status "+ tenderStoreCreditObj.getStoreCreditID() + "in central database." );
				
				result = dataTransaction.updateStoreCreditLockStatus(storeCreditId,"N");					
				if (result)
					logger.info("Successfuly updated Store Credit: " + tenderStoreCreditObj.getStoreCreditID() + " in ORCO.");
				else
				{
					logger.info("ERROR!!! Second Attempt failed in updated Store Credit lock status: " + tenderStoreCreditObj.getStoreCreditID() + " in ORCO.");
				}
			}	
				
		}
		//end  Rev 2.0

		if (tenderToRemove instanceof TenderGiftCard) {
			// Changes for Rev 1.0 : Starts
			String cardNum = ((TenderGiftCard) tenderToRemove).getCardNumber();
			// Changes for Rev 1.0 : Ends
			String var1 = ";";
			String var2 = "=";
			String var3 = "?";
			boolean isSwiped = false;
			boolean isScanned = false;
			String trackData = null;

			if (cardNum != null && cardNum.length() != 0) {
				if (cardNum.length() > 28)
					isSwiped = true;
				else if (cardNum.length() == 26)
					isScanned = true;
			}
			if (isScanned) {
				trackData = cardNum;

			} else if (isSwiped) {
				trackData = var1 + cardNum.substring(0, 16) + var2 + cardNum.substring(16) + var3;

			}

			if (cardNum.length() > 16) {
				trackData = var1 + cardNum.substring(0, 16) + var2 + cardNum.substring(16) + var3;
			}

			MAXGiftCardUtilitiesQC utilObj = new MAXGiftCardUtilitiesQC();
			GCPOS pos = utilObj.getInstance();
			HashMap balanceEnquiryMap = null;

			if (isSwiped || isScanned) {
				cardNum = utilObj.getCardNumberFromTrackData(trackData, true);
			}

			if (((MAXGiftCard) (((TenderGiftCard) tenderToRemove).getGiftCard())).getQcApprovalCode() != null) {
				balanceEnquiryMap = utilObj.CancelRedeem(pos,
						(MAXGiftCard) (((TenderGiftCard) tenderToRemove).getGiftCard()),
						tenderToRemove.getAmountTender().toString(), cardNum, trackData);
			}
			if (balanceEnquiryMap != null) {
				balanceEnquiryMap.get("ResponseMessage").toString();
				String resp = balanceEnquiryMap.get("ResponseCode").toString();
				if (balanceEnquiryMap.get("ResponseMessage") != null
						&& !balanceEnquiryMap.get("ResponseCode").equals("0")) {
					utilObj.showQCOfflineErrorBox(bus);
					return;
				}
				if (!(resp.equalsIgnoreCase("0"))) {
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
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
					offlineFlag = true;
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					return;
				}
			}
		}

		// Changes start by Bhanu Priya
		if (tenderADO instanceof MAXTenderPaytmADO) {

			MAXTenderCargo tenderCargo = (MAXTenderCargo) bus.getCargo();
			MAXSaleReturnTransaction sr = (MAXSaleReturnTransaction) tenderCargo.getTransaction();
			String orderId = null;
			String walletTxnID = null;
			String mobileNo = null;
			String transID = null;
			String amountPaid = null;
			// below code added by atul for paytm tender reversal bug
			if (tenderToRemove instanceof MAXTenderPaytm) {
				mobileNo = ((MAXTenderPaytm) tenderToRemove).getPaytmMobileNumber().toString().trim();
				amountPaid = ((MAXTenderPaytm) tenderToRemove).getAmountTender().toString().trim();
				orderId = ((MAXTenderPaytm) tenderToRemove).getOrderID().toString().trim();
				walletTxnID = ((MAXTenderPaytm) tenderToRemove).getPaytmWalletTransactionID().toString().trim();
			}
			if (!isReentryMode) {
				MAXPaytmHelperUtiltiy util = new MAXPaytmHelperUtiltiy();
				try {
					String uri = Gateway.getProperty("application", "PaytmReversalURL", "");
					/*
					 * MAXPaytmResponse response = util.reverseAmount(uri, ((MAXPaytmResponse)
					 * tenderChargeObj).getPhoneNumber(), ((MAXTenderCargo)
					 * bus.getCargo()).getTransaction().getTransactionID(),
					 * tenderChargeObj.getAmountTender().toString(), ((MAXTenderChargeIfc)
					 * tenderChargeObj).getMerchantTransactionId());
					 */

					@SuppressWarnings("static-access")
					// MAXPaytmResponse response =
					// util.reverseAmount(orderId,uri, mobileNo,transID,
					// amountPaid, walletTxnID);
					MAXPaytmResponse response = util.reverseAmount(orderId, uri, mobileNo, amountPaid, walletTxnID);
					response.setUrl(uri);
					logger.info("Paytm reversal - response - " + response);
					if (response != null && response.isDataException()) {
						logger.info("Error in paytm reversal - server offline");
						DialogBeanModel dialogModel = new DialogBeanModel();
						dialogModel.setResourceID("ServerOffline");
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

						return;
					}
					if (response != null && response.getStatusCode() != null
							&& response.getStatusCode().equalsIgnoreCase(MAXPaytmTenderConstants.SUCCESS)) {
						letter = "PaytmPrint";
						/* Changes for Rev 1.8 starts */
						logger.info("paytm reversal - success response");
						setResponseData(cargo, response);
						response.setReqRespStatus(MAXPaytmTenderConstants.RESPONSERECEIVED);

						String paytmResp = response.getPaytmResponse();
						response.setPaytmResponse(response.getWalletTxnId() + " : " + paytmResp);
						response.setRequestTypeA(MAXPaytmTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
						cargo.setPaytmResp(response);
						MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
						// response.setPhoneNumber(sr.getPaytmResponse().getPhoneNumber());
						response.setPhoneNumber(mobileNo);
						// response.setAmountPaid(sr.getPaytmResponse().getAmountPaid().toString());
						response.setAmountPaid(amountPaid);
						paytmTrans.saveRequest(response);
						/* Changes for Rev 1.8 ends */
						cargo.setPaytmResp(response);

						// return;
					} else if (response != null && ((response.getStatusCode() != null
							&& response.getStatusCode().equalsIgnoreCase(MAXPaytmTenderConstants.FAILURE)
							|| response.getStatusCode() != null))) {
						/* Changes for Rev 1.8 starts */
						setResponseData(cargo, response);
						logger.info("Error in Paytm reversal - failure response");
						response.setRequestTypeA(MAXPaytmTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
						if (response.getResponseCode() == 408) {
							response.setReqRespStatus(MAXPaytmTenderConstants.TIMEOUT);
						} else {
							response.setReqRespStatus(MAXPaytmTenderConstants.RESPONSERECEIVED);
						}
						/*
						 * response.setAmountPaid(sr.getPaytmResponse(). getAmountPaid().toString());
						 * response.setPhoneNumber(sr .getPaytmResponse().getPhoneNumber());
						 */
						response.setPhoneNumber(mobileNo);
						// response.setAmountPaid(sr.getPaytmResponse().getAmountPaid().toString());
						response.setAmountPaid(amountPaid);

						MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
						paytmTrans.saveRequest(response);
						/* Changes for Rev 1.8 ends */
						DialogBeanModel dialogModel = new DialogBeanModel();
						String[] messgArray = new String[1];
						if (response.getStatusMessage() != null)
							messgArray[0] = response.getStatusMessage();
						else
							messgArray[0] = "Error in getting response from Paytm";
						dialogModel.setArgs(messgArray);
						dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMERROR);
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

						return;
					} else if (response != null && response.getStatusMessage() != null
							&& (response.getStatusMessage().equalsIgnoreCase(MAXPaytmTenderConstants.NETWORKERROR)
									|| response.getStatusMessage()
											.equalsIgnoreCase(MAXPaytmTenderConstants.PAYTMTIMEOUTERROR))) {
						/* Changes for Rev 1.8 starts */
						setResponseData(cargo, response);
						logger.info("Error in paytm reversal - network and timeout error response");
						response.setRequestTypeA(MAXPaytmTenderConstants.TIMEOUT);
						if (response.getAmountPaid() == null || response.getAmountPaid().equals("null")
								|| response.getAmountPaid().equals(null)) {
							response.setAmountPaid("0.00");
						}
						response.setReqRespStatus(MAXPaytmTenderConstants.TIMEOUT);
						// response.setPhoneNumber(sr.getPaytmResponse().getPhoneNumber());
						response.setPhoneNumber(mobileNo);

						MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
						paytmTrans.saveRequest(response);
						/* Changes for Rev 1.8 ends */
						DialogBeanModel dialogModel = new DialogBeanModel();
						dialogModel.setResourceID(response.getStatusMessage());
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						return;
					} else {
						logger.info("Error in paytm reversal - in else condition");
						DialogBeanModel dialogModel = new DialogBeanModel();
						String[] messgArray = new String[1];
						messgArray[0] = "\n\nCould not delete the tender \nThere was an error in sending \nReversal request to Paytm";
						dialogModel.setArgs(messgArray);
						dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMERROR);
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						return;
					}
				} catch (Exception e) {
					logger.error("Error in paytm reversal " + e.getMessage());
					e.printStackTrace();
					DialogBeanModel dialogModel = new DialogBeanModel();
					String[] messgArray = new String[1];
					messgArray[0] = "\n\nCould not delete the tender \nThere was an error in sending \nReversal request to Paytm";
					dialogModel.setArgs(messgArray);
					dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMERROR);
					dialogModel.setType(DialogScreensIfc.ERROR);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					return;
				}

			}
		}

		// Changes done for POS-Amazon Pay Integration @Purushotham
		if (tenderADO instanceof MAXTenderAmazonPayADO) {

			MAXPaytmDataTransaction dataTransaction = new MAXPaytmDataTransaction();

			String mobileNo = null;
			String amountPaid = null;
			String orderId = null;
			String amazonPayTranId = null;
			if (tenderToRemove instanceof MAXTenderAmazonPay) {
				mobileNo = ((MAXTenderAmazonPay) tenderToRemove).getAmazonPayMobileNumber().toString().trim();
				amountPaid = ((MAXTenderAmazonPay) tenderToRemove).getAmountTender().toString().trim();
				orderId = ((MAXTenderAmazonPay) tenderToRemove).getAmazonPayOrderID().toString().trim();
				amazonPayTranId = ((MAXTenderAmazonPay) tenderToRemove).getAmazonPayWalletTransactionID().trim();

				/*
				 * if(cargo.getAmazonPayResp()!=null &&
				 * cargo.getAmazonPayResp().getAmazonTransactionId()!= null){ amazonPayTranId =
				 * cargo.getAmazonPayResp().getAmazonTransactionId(); }
				 */

			}
			if (!isReentryMode) {
				try {
					String url = Gateway.getProperty("application", "AmazonPayReversalBarcodeURL", "");
					boolean isSandBoxEnabled = false;

					try {
						isSandBoxEnabled = pm.getBooleanValue("IsSandBoxEnabled").booleanValue();
					} catch (ParameterException e) {
						logger.warn("IsSandBoxEnabled Parameter does not exist in application.xml file");
					}
					MAXAmazonPayResponse response = MAXAmazonPayHelperUtiltiy.refundAmount(cargo, url, mobileNo,
							amountPaid, amazonPayTranId, orderId, isSandBoxEnabled);
					response.setUrl(url);
					logger.info("Amazon Pay Refund - response - " + response);
					if (response != null && response.isDataException()) {
						logger.info("Error in Amazon Pay Refund - server offline");
						DialogBeanModel dialogModel = new DialogBeanModel();
						dialogModel.setResourceID("AmazonPayServerOffline");
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

						return;
					}
					if (response != null && response.getStatus() != null
							&& response.getStatus().equalsIgnoreCase(MAXAmazonPayTenderConstants.PENDING)
							&& response.getReasonCode() != null && !response.getReasonCode().equalsIgnoreCase("04")
							|| response.getReasonCode().equalsIgnoreCase("200")) {
						letter = "AmazonPayRefundPrint";
						/* Changes for Rev 1.8 starts */
						logger.info("Amazon Pay Refund - success response");
						setResponseDataForAmazonPayRefund(cargo, response);
						response.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSERECEIVED);

						String amazonPayResponse = response.getAmazonPayResponse();
						response.setAmazonPayResponse(response.getWalletTxnId() + " : " + amazonPayResponse);
						response.setRequestTypeA(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
						cargo.setAmazonPayResp(response);

						response.setPhoneNumber(mobileNo);
						response.setAmountPaid(amountPaid);
						dataTransaction.saveAmazonPayRefundRequest(response);
						cargo.setAmazonPayResp(response);

					} else if (response != null && ((response.getReasonCode() != null
							&& response.getReasonCode().equalsIgnoreCase("99")))) {
						setResponseDataForAmazonPayRefund(cargo, response);
						logger.info("Error in Amazon pay  Refund - Unexpected/Undefined state");
						response.setRequestTypeA(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
						if (response.getResponseCode() == 408) {
							response.setReqRespStatus(MAXAmazonPayTenderConstants.TIMEOUT);
						} else {
							response.setReqRespStatus(MAXAmazonPayTenderConstants.RESPONSERECEIVED);
						}

						response.setPhoneNumber(mobileNo);
						response.setAmountPaid(amountPaid);

						dataTransaction.saveAmazonPayRefundRequest(response);
						DialogBeanModel dialogModel = new DialogBeanModel();
						String[] messgArray = new String[1];
						if (response.getStatusMessage() != null)
							messgArray[0] = response.getStatusMessage();
						else
							messgArray[0] = "Error in getting response from Amazon Pay";
						dialogModel.setArgs(messgArray);
						dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYERROR);
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

						return;
					} else if (response != null && response.getStatusMessage() != null && response.getStatusMessage()
							.equalsIgnoreCase(MAXAmazonPayTenderConstants.AMAZONPAYTIMEOUTERROR)) {
						setResponseDataForAmazonPayRefund(cargo, response);
						response.setRequestTypeA(MAXAmazonPayTenderConstants.TIMEOUT);
						response.setReqRespStatus(MAXAmazonPayTenderConstants.TIMEOUT);

						DialogBeanModel dialogModel = new DialogBeanModel();
						dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYTIMEOUTERROR);
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						return;
					} else if (response != null && response.getStatusMessage() != null && response.getStatusMessage()
							.equalsIgnoreCase(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR)) {
						setResponseDataForAmazonPayRefund(cargo, response);
						response.setRequestTypeA(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
						String[] messgArray = new String[1];
						messgArray[0] = "Amazon Pay Server is not Reachable";
						response.setReqRespStatus(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);

						DialogBeanModel dialogModel = new DialogBeanModel();
						dialogModel.setArgs(messgArray);
						dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYNETWORKERROR);
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						return;
					} else {
						logger.info("Error in Amazon Pay Refund  - in else condition");
						DialogBeanModel dialogModel = new DialogBeanModel();
						String[] messgArray = new String[1];
						messgArray[0] = "\n\nCould not delete the tender \nThere was an error in sending \nReversal request to Amazon Pay Refund";
						dialogModel.setArgs(messgArray);
						dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYERROR);
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						return;
					}
				} catch (Exception e) {
					logger.error("Error in Amazon Aay  Refund " + e.getMessage());
					DialogBeanModel dialogModel = new DialogBeanModel();
					String[] messgArray = new String[1];
					messgArray[0] = "\n\nCould not delete the tender \nThere was an error in sending \nReversal request to Amazon Pay Refund";
					dialogModel.setArgs(messgArray);
					dialogModel.setResourceID(MAXAmazonPayTenderConstants.AMAZONPAYERROR);
					dialogModel.setType(DialogScreensIfc.ERROR);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					return;
				}

			}

		}

		if (tenderADO instanceof MAXTenderMobikwikADO) {

			String orderId = null;
			String mobileNo = null;
			String amountPaid = null;
			// below code added by atul for paytm tender reversal bug
			if (tenderToRemove instanceof MAXTenderMobikwik) {
				mobileNo = ((MAXTenderMobikwik) tenderToRemove).getMobikwikMobileNumber().toString().trim();
				amountPaid = ((MAXTenderMobikwik) tenderToRemove).getAmountTender().toString().trim();
				orderId = ((MAXTenderMobikwik) tenderToRemove).getMobikwikOrderID().toString().trim();

				// walletTxnID= ((MAXTenderMobikwik)
				// tenderToRemove).getMobikwikWalletTransactionID().toString().trim();
				// transactionid=cargo.getCurrentTransactionADO().getTransactionID();
			}
			if (!isReentryMode) {
				MAXMobikwikHelperUtiltiy util = new MAXMobikwikHelperUtiltiy();
				try {
					String uri = Gateway.getProperty("application", "MobikwikReversalURL", "");
					/*
					 * MAXPaytmResponse response = util.reverseAmount(uri, ((MAXPaytmResponse)
					 * tenderChargeObj).getPhoneNumber(), ((MAXTenderCargo)
					 * bus.getCargo()).getTransaction().getTransactionID(),
					 * tenderChargeObj.getAmountTender().toString(), ((MAXTenderChargeIfc)
					 * tenderChargeObj).getMerchantTransactionId());
					 */

					@SuppressWarnings("static-access")
					// String uri1=uri.trim().toString();
					// MAXPaytmResponse response =
					// util.reverseAmount(orderId,uri, mobileNo,transID,
					// amountPaid, walletTxnID);
					MAXMobikwikResponse response = util.reverseAmount(orderId, uri, mobileNo, amountPaid);
					response.setUrl(uri);
					logger.info("Mobikwik reversal - response - " + response);
					if (response != null && response.isDataException()) {
						logger.info("Error in mobikwik reversal - server offline");
						DialogBeanModel dialogModel = new DialogBeanModel();
						dialogModel.setResourceID("ServerOffline");
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

						return;
					}
					if (response != null && response.getStatusCode() != null
							&& response.getStatus().equalsIgnoreCase(MAXPaytmTenderConstants.SUCCESS)) {
						letter = "MobikwikPrint";
						logger.info("Error in mobikwik reversal - success response");
						/* Changes for Rev 1.8 starts */
						setMobikwikResponseData(cargo, response);
						response.setReqRespStatus(MAXMobikwikTenderConstants.RESPONSERECEIVED);

						String mobikwikResp = response.getMobikwikResponse();
						response.setMobikwikResponse(response.getWalletTxnId() + " : " + mobikwikResp);
						response.setRequestTypeA(MAXPaytmTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
						cargo.setMobikwikResp(response);
						MAXPaytmDataTransaction mobikwikTrans = new MAXPaytmDataTransaction();
						// response.setPhoneNumber(sr.getPaytmResponse().getPhoneNumber());
						response.setPhoneNumber(mobileNo);
						// response.setAmountPaid(sr.getPaytmResponse().getAmountPaid().toString());
						response.setAmountPaid(amountPaid);
						mobikwikTrans.saveMobikwikRequest(response);
						/* Changes for Rev 1.8 ends */
						cargo.setMobikwikResp(response);

					} else if (response != null && ((response.getStatusCode() != null
							&& response.getStatusCode().equalsIgnoreCase(MAXPaytmTenderConstants.FAILURE)
							|| response.getStatusCode() != null))) {
						/* Changes for Rev 1.8 starts */
						logger.info("Error in mobikwik reversal - failure response");
						setMobikwikResponseData(cargo, response);
						response.setRequestTypeA(MAXMobikwikTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
						if (response.getResponseCode() == 408) {
							response.setReqRespStatus(MAXMobikwikTenderConstants.TIMEOUT);
						} else {
							response.setReqRespStatus(MAXMobikwikTenderConstants.RESPONSERECEIVED);
						}
						/*
						 * response.setAmountPaid(sr.getPaytmResponse(). getAmountPaid().toString());
						 * response.setPhoneNumber(sr .getPaytmResponse().getPhoneNumber());
						 */
						response.setPhoneNumber(mobileNo);
						// response.setAmountPaid(sr.getPaytmResponse().getAmountPaid().toString());
						response.setAmountPaid(amountPaid);

						MAXPaytmDataTransaction mobikTrans = new MAXPaytmDataTransaction();
						mobikTrans.saveMobikwikRequest(response);
						/* Changes for Rev 1.8 ends */
						DialogBeanModel dialogModel = new DialogBeanModel();
						String[] messgArray = new String[1];
						if (response.getStatusMessage() != null)
							messgArray[0] = response.getStatusMessage();
						else
							messgArray[0] = "Error in getting response from Mobikwik";
						dialogModel.setArgs(messgArray);
						dialogModel.setResourceID(MAXMobikwikTenderConstants.MOBIKWIKERROR);
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

						return;
					} else if (response != null && response.getStatusMessage() != null
							&& (response.getStatusMessage().equalsIgnoreCase(MAXMobikwikTenderConstants.NETWORKERROR)
									|| response.getStatusMessage()
											.equalsIgnoreCase(MAXMobikwikTenderConstants.MOBIKWIKTIMEOUTERROR))) {
						logger.info("Error in mobikwik reversal - network and timeout error");
						/* Changes for Rev 1.8 starts */
						setMobikwikResponseData(cargo, response);
						response.setRequestTypeA(MAXMobikwikTenderConstants.TIMEOUT);
						if (response.getAmountPaid() == null || response.getAmountPaid().equals("null")
								|| response.getAmountPaid().equals(null)) {
							response.setAmountPaid("0.00");
						}
						response.setReqRespStatus(MAXMobikwikTenderConstants.TIMEOUT);
						// response.setPhoneNumber(sr.getPaytmResponse().getPhoneNumber());
						response.setPhoneNumber(mobileNo);

						MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
						paytmTrans.saveMobikwikRequest(response);
						/* Changes for Rev 1.8 ends */
						DialogBeanModel dialogModel = new DialogBeanModel();
						dialogModel.setResourceID(response.getStatusMessage());
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						return;
					} else {
						logger.info("Error in mobikwik reversal in else condition");
						DialogBeanModel dialogModel = new DialogBeanModel();
						String[] messgArray = new String[1];
						messgArray[0] = "\n\nCould not delete the tender \nThere was an error in sending \nReversal request to Mobikwik";
						dialogModel.setArgs(messgArray);
						dialogModel.setResourceID(MAXMobikwikTenderConstants.MOBIKWIKERROR);
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						return;
					}
				} catch (Exception e) {
					logger.error("Error in mobikwik reversal " + e.getMessage());
					e.printStackTrace();
					DialogBeanModel dialogModel = new DialogBeanModel();
					String[] messgArray = new String[1];
					messgArray[0] = "\n\nCould not delete the tender \nThere was an error in sending \nReversal request to Mobikwik";
					dialogModel.setArgs(messgArray);
					dialogModel.setResourceID(MAXMobikwikTenderConstants.MOBIKWIKERROR);
					dialogModel.setType(DialogScreensIfc.ERROR);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					return;
				}

			}
		}

		// else

		// {

		// bus.mail(letterNo, BusIfc.CURRENT);

		// letterNo="NO";
		// }

		// } // Rev 1.6 changes ends

		// }
		// }

		// Changes End by Bhanu Priya

		else if (tenderADO != null && tenderADO instanceof MAXTenderCreditADO) {
			logger.info(" INSIDE DELETE TENDER ACTION FOR CREDIT");
			HashMap tenderAttributes = tenderADO.getTenderAttributes();
			MAXTenderChargeIfc tender = (MAXTenderChargeIfc) tenderADO.toLegacy();

			if (tenderAttributes.get(MAXTenderConstants.AUTH_METHOD) != null) {
				if (((String) tenderAttributes.get(MAXTenderConstants.AUTH_METHOD)).equalsIgnoreCase("ONLINE")) {
					String transactionID = cargo.getCurrentTransactionADO().getTransactionID();
					String amount = tender.getAmountTender().getStringValue();
					double l1 = Double.parseDouble(amount);
					l1 = l1 * 100;
					long l2 = (new Double(l1)).longValue();
					String total1 = String.valueOf(l2);
					String voidTransaction = Gateway.getProperty("application", "voidTransaction", null);
					String requestString = voidTransaction + "," + "T1/" + transactionID + "," + total1 + ","
							+ tender.getAcquiringBankCode() + "," + "," + "," + "0000" + tender.getInvoiceNumber() + ","
							+ "," + "," + "," + ",";
					try {
						System.out.println("Request is == " + requestString);
						SocketAddress sockaddr = new InetSocketAddress("127.0.0.1", 8082);
						Socket clientSocket = new Socket();
						clientSocket.connect(sockaddr, 180000); // 2nd parameter
																// is
																// timeout.
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
						if (str != null && str.indexOf("APPROVED") != -1) {
							boolean creditSlip = false;
							if (pm.getStringValue("PrintCreditChargeSlip").equalsIgnoreCase("Y")) {
								creditSlip = true;
							}
							String[] header = new String[] {};
							String[] footer = new String[] {};
							POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
							try {
								String traType = tenderAttributes.get("TRANSACTION_TYPE").toString();
								if (traType != null) {
									traType = TransactionConstantsIfc.TYPE_DESCRIPTORS[TransactionConstantsIfc.TYPE_VOID];
								}
								tenderAttributes.put("TRANSACTION_TYPE", traType);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (creditSlip == true) {
								try {
									// Changes done for code merging(commenting
									// below lines for error resolving)
									/*
									 * MAXCreditChargeSlipReciept debitSlip = new MAXCreditChargeSlipReciept(
									 * cargo.getCurrentTransactionADO(), tenderAttributes, header, footer, "");
									 */
									// retrieve receipt locale
									Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT);
									// get properties for receipt
									Properties props = ResourceBundleUtil.getGroupText("receipt",
											ReceiptConstantsIfc.RECEIPT_BUNDLES, locale);
									// Changes done for code merging(commenting
									// below lines for error resolving)
									/*
									 * debitSlip.setProps(props); pda.printDocument(debitSlip); // For Customer Copy
									 * 
									 * debitSlip = new MAXCreditChargeSlipReciept(cargo .getCurrentTransactionADO(),
									 * tenderAttributes, header, footer, "Customer");
									 */
									// retrieve receipt locale
									locale = LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT);
									// get properties for receipt
									props = ResourceBundleUtil.getGroupText("receipt",
											ReceiptConstantsIfc.RECEIPT_BUNDLES, locale);
									// Changes done for code merging(commenting
									// below lines for error resolving)
									/*
									 * debitSlip.setProps(props); pda.printDocument(debitSlip);
									 */

									// Update printer status
									ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
									// Changes done for code merging(commenting
									// below lines for error resolving)
									// } catch (DeviceException e) {
								} catch (Exception e) {
									logger.warn("Unable to print debit slip. " + e.getMessage() + "");

									// Update printer status
									ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
									// Changes done for code merging(commenting
									// below lines for error resolving)
									/*
									 * if (e.getOrigException() != null) { logger.warn
									 * ("DeviceException.NestedException:\n" + Util.throwableToString(e
									 * .getOrigException()) + ""); }
									 */

									String msg[] = new String[1];
									UtilityManagerIfc utility = (UtilityManagerIfc) bus
											.getManager(UtilityManagerIfc.TYPE);
									msg[0] = utility.retrieveDialogText("RetryContinue.PrinterOffline",
											"Printer is offline.");

									dModel.setResourceID("RetryContinue");
									dModel.setType(DialogScreensIfc.RETRY_CONTINUE);
									dModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Retry");
									dModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Continue");
									dModel.setArgs(msg);

									// display dialog
									ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dModel);
									return;

								}
							}
							letter = "VoidSuccess";
							currentLetter = "Clear";
						} else if (!str.equals("") && str.indexOf("APPROVED") < 0) {
							if (str.indexOf("Failed To Process Transaction") != -1) {
								dModel.setType(DialogScreensIfc.ERROR);
								dModel.setArgs(new String[] { str });
								dModel.setResourceID("PlutusOfflineDelete");
								dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
								ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dModel);
								return;

							} else if (str.indexOf("Failed To Communicate With Device") != -1) {
								str = str.substring(str.indexOf("Failed To Communicate With Device"),
										str.indexOf("Device") + 6);
								dModel.setType(DialogScreensIfc.ERROR);
								dModel.setArgs(new String[] { str });
								dModel.setResourceID("CommunicationFailed");
								dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
								ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dModel);
								return;

							} else {
								dModel.setType(DialogScreensIfc.RETRY_CONTINUE);
								dModel.setArgs(new String[] { str });
								dModel.setResourceID("CreditDeleteDeclined");
								dModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Retry");
								dModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Undo");
								ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dModel);
								return;
							}

						} else {
							dialogId = "PlutusError";
							letter = "PlutusError";
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					dialogId = "ManualTenderLineDelete";
					letter = "VoidOfflineTransaction";
				}
			}
		}

		else if (tenderADO instanceof MAXTenderLoyaltyPointsADO) {
			// changes starts for Rev 1.1 (Ashish :Loyalty OTP)
			// ui.showScreen(MAXPOSUIManagerIfc.MAX_TIC_LOYALTY_POINTS);
			// changes starts for Rev 1.1 (Ashish :Loyalty OTP)
			MAXLoyaltyDataTransaction loyaltyDataTransaction = null;
			loyaltyDataTransaction = (MAXLoyaltyDataTransaction) DataTransactionFactory
					.create(MAXDataTransactionKeys.LOYALTY_DATA_TRANSACTION);
			MAXSaleReturnTransaction tran = null;
			MAXCustomerIfc maxCustomer = null;
			if (cargo != null && cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
				tran = (MAXSaleReturnTransaction) cargo.getTransaction();
				maxCustomer = tran.getMAXTICCustomer();
			}
			if (cargo.getCustomer() != null && (tran != null && maxCustomer == null)) {
				MAXCustomerIfc customer = (MAXCustomerIfc) cargo.getCustomer();
				// changes starts for Rev 1.1 (Ashish :Loyalty OTP)
				maxCustomer = customer;
				// changes ends for Rev 1.1 (Ashish :Loyalty OTP)
				reversalAttributes.put(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER, customer.getCustomerID());
			} else if (maxCustomer != null) {
				reversalAttributes.put(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER, maxCustomer.getCustomerID());
			} else {
				// Changes starts for deleting otp during layaway : starts
				if (cargo.getCurrentTransactionADO().toLegacy() instanceof MAXAbstractTenderableTransaction) {
					MAXCustomerIfc customer = (MAXCustomerIfc) ((MAXAbstractTenderableTransaction) cargo
							.getCurrentTransactionADO().toLegacy()).getTicCustomer();
					if (customer == null && cargo.getCurrentTransactionADO().getCustomer() != null
							&& cargo.getCurrentTransactionADO().getCustomer() instanceof MAXCustomer) {
						customer = (MAXCustomerIfc) cargo.getCurrentTransactionADO().getCustomer();
					}

					reversalAttributes.put(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER, customer.getLoyaltyCardNumber());
				} else if (cargo.getCurrentTransactionADO().toLegacy() instanceof MAXLayawayTransaction) {
					MAXCustomerIfc customer = (MAXCustomerIfc) ((MAXLayawayTransaction) cargo.getCurrentTransactionADO()
							.toLegacy()).getTicCustomer();
					if (customer == null && cargo.getCurrentTransactionADO().getCustomer() != null
							&& cargo.getCurrentTransactionADO().getCustomer() instanceof MAXCustomer) {
						customer = (MAXCustomerIfc) cargo.getCurrentTransactionADO().getCustomer();
					}

					reversalAttributes.put(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER, customer.getLoyaltyCardNumber());

				}
			}
			// Changes ends for deleting otp during layaway
			// changes starts for Rev 1.1 (Ashish :Loyalty OTP)
			/*
			 * try { // Encrypt the values the value of TIC Number through encryption //
			 * function defined in DB reversalAttributes =
			 * loyaltyDataTransaction.encryptValue(reversalAttributes); } catch
			 * (DataException e1) { Logger.getLogger(MAXDeleteTenderActionSite.class
			 * .getName()).log(Level.SEVERE, null, e1); }
			 */
			// changes ends for Rev 1.1 (Ashish :Loyalty OTP)

			AbstractTenderableTransaction transaction = (AbstractTenderableTransaction) txnADO.toLegacy();
			reversalAttributes = populateHashMap(transaction, reversalAttributes, tenderToRemove);
			// changes starts for Rev 1.1 (Ashish :Loyalty OTP)
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
			// "http://www.lscircle.in/crmresponse/Response?"+ urlParameters;
			// Changes start for rev 1.11
			// String URL = Gateway.getProperty("application",
			// "LoyaltyWebServiceURL", null);
			// String targetURL = URL + urlParameters;
			// call for response
			// if (urlParameters != null) {
			// Defined for executing the web request
			// String response = executePost(targetURL, urlParameters);

			// when Successful response is received
			// Response Code - 200 denotes successful response

			String response = "";
			MAXCRMObj crmobj = new MAXCRMObj();

			crmobj.setRequestMethod(MAXUtilityConstantsIfc.CRM_CAPILLARY_METHOD_POST);
			// Create Request JSON Message

			String requestJsonMsg = MAXDeleteTenderActionSite.createOTPCallRequestMessage(crmobj, reversalAttributes);
			logger.debug("OTP Request : " + requestJsonMsg);
			crmobj.setRequestMessage(requestJsonMsg);
			crmobj.setCRM_URL(Gateway.getProperty("application", "LoyaltyWebServiceURLRedeemCall2", null));
			;

			crmobj.setRequestMethod(MAXUtilityConstantsIfc.CRM_CAPILLARY_METHOD_POST);
			// Hit the webservice

			response = executePost(crmobj).getConnResponseMessage().toString();
			logger.debug("OTP Response : " + response);
			// Changes start for bug 17104
			if (maxCustomer != null && maxCustomer.IsOtpValidation()) {
				maxCustomer.setOtpValidation(false);
			}
			// Changes end for bug 17104
			String message = "";
			if (crmobj.getConnResponseCode().equalsIgnoreCase("200")) {
				JSONParser jsonParser = new JSONParser();
				String jsonMessage = crmobj.getConnResponseMessage();
				Object object = null;
				try {
					object = jsonParser.parse(jsonMessage.toString());
				} catch (ParseException e) {

					e.printStackTrace();
				}

				JSONObject jsonObject = (JSONObject) object;
				message = (String) jsonObject.get("message");
				crmobj.setMessage(message);
				crmobj.setResponse((String) jsonObject.get("response"));
				response = crmobj.getResponse();
				response = "4";

			}
			if (responseCode == 200 && !response.equalsIgnoreCase("")) {
				processSuccessResponse(response, loyaltyDataTransaction, reversalAttributes, crmobj);

				/*
				 * if (responseCode == 200 && !response.equalsIgnoreCase("") &&
				 * !response.equalsIgnoreCase("Timeout")) { processSuccessResponse(response,
				 * loyaltyDataTransaction, reversalAttributes);
				 */
				// Changes end for rev 1.11
			}

			// Time Out Case handling
			// validating the error code for timeout
			// 408- HTTP_CLIENT_TIMEOUT && 504- HTTP_GATEWAY_TIMEOUT
			// if timeout then save timeout request other wise wait for
			// response
			// Changes start for rev 1.11
			// else if (response.equalsIgnoreCase("Timeout")) {
			else if (response.equals("") || !response.equalsIgnoreCase("S")) {

				if (message != "") {
					reversalAttributes.put(MAXLoyaltyConstants.RESPONSE_MESSAGE, crmobj.getMessage());

				} else {
					reversalAttributes.put(MAXLoyaltyConstants.RESPONSE_MESSAGE, "Network Issue");
				}
				reversalAttributes.put(MAXLoyaltyConstants.RESPONSE, crmobj.getResponse());
				reversalAttributes.put(MAXLoyaltyConstants.RESPONSE, MAXLoyaltyConstants.FAIL);
				reversalAttributes.put(MAXLoyaltyConstants.REQUEST_STATUS, MAXLoyaltyConstants.TIMEOUT);
				reversalAttributes.put(MAXLoyaltyConstants.FLAG, MAXLoyaltyConstants.FAIL);
				// reversalAttributes.put(MAXLoyaltyConstants.REQUEST_STATUS,
				// MAXLoyaltyConstants.TIMEOUT);
				// reversalAttributes.put(MAXLoyaltyConstants.FLAG,
				// MAXLoyaltyConstants.TIMEOUT_FLAG);
				// Changes end for rev 1.11
				try {
					loyaltyDataTransaction.updateRequest(reversalAttributes);
				} catch (DataException e) {
					logger.error("DataException::" + e.getMessage() + "");
				}
				Logger.getLogger("Error In getting Response:::" + MAXDeleteTendersActionSite.class.getName());
			} else {
				Logger.getLogger("Error In forming URL:::" + MAXDeleteTenderActionSite.class.getName());
			}

		}

		// changes starts(Kamlesh Pant :EWallet Reversal)
			 //System.out.println("Inside if :"+cargo.isEWalletTenderFlag());
			 // if (cargo.eWalletTraceId != null) {
				//  System.out.println("cargo.eWalletTraceId :"+cargo.eWalletTraceId);
		MAXSaleReturnTransaction abc =(MAXSaleReturnTransaction)cargo.getTransaction();
        // System.out.println("ABC value :"+abc);
         System.out.println("(abc.isEWalletTenderFlag()) :"+(abc.isEWalletTenderFlag() && tenderToRemove.getTypeCodeString().equalsIgnoreCase("EWLT")));
         //System.out.println("abc Value :"+abc);
  
          if (abc.isEWalletTenderFlag() && tenderToRemove.getTypeCodeString().equalsIgnoreCase("EWLT")) {
				  DialogBeanModel  dialogModel = new DialogBeanModel();
				  POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
				  dialogModel.setResourceID("EWalletReversal");
				  dialogModel.setType(DialogScreensIfc.ERROR);
				  dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "OK");
				  uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel); 
				 return;
			  }
		
			/* 
			 * System.out.println("1355 inside if ewallettraceID"+cargo.eWalletTraceId);
			 * POSUIManagerIfc uiManager = (POSUIManagerIfc)
			 * bus.getManager(UIManagerIfc.TYPE); //MAXTenderCargo cargo1 = (MAXTenderCargo)
			 * bus.getCargo(); POSBaseBeanModel model1 = (POSBaseBeanModel)
			 * uiManager.getModel(MAXPOSUIManagerIfc.ENTER_OTP); PromptAndResponseModel
			 * promptResponseModel = model1.getPromptAndResponseModel(); DialogBeanModel
			 * dialogModel = new DialogBeanModel(); String[] messgArray = new String[1];
			 * 
			 * String creditOtp = promptResponseModel.getResponseText();
			 * logger.info("AKS: eWalletCredit Response creditOtp   " + creditOtp);
			 * //System.out.println("1370 cargo1.geteWalletMobileNumber() Value :"+cargo.
			 * geteWalletMobileNumber()); if(creditOtp.length()!=4) { messgArray[0] =
			 * "OTP Length Cannot Be Less Than Or Greater Than 4";
			 * dialogModel.setArgs(messgArray);
			 * dialogModel.setResourceID("OxigenUserError");
			 * dialogModel.setArgs(messgArray); dialogModel.setType(DialogScreensIfc.ERROR);
			 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
			 * CommonLetterIfc.CANCEL);
			 * uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel); //
			 * bus.mail("Cancel");
			 * 
			 * return; }
			 * 
			 * 
			 * try {
			 * 
			 * JSONObject jsonOxigenOtpGeneratRequest = getJsonRequestObject(cargo); String
			 * url = Gateway.getProperty("application","EWalletReversalURL", "");
			 * logger.info("Ewallet credit URL of Search Edge API is " + url);
			 * System.out.println("Reversal URL :"+ url);
			 * logger.info("Ewallet credit Request1 of Search Edge API is " +
			 * jsonOxigenOtpGeneratRequest.toString()); // Call CRM API
			 * 
			 * String oxigenGeneratedOtpResponse = null; try { oxigenGeneratedOtpResponse =
			 * executeOtpGeneratRequest(url,jsonOxigenOtpGeneratRequest); //
			 * MAXOxigenWalletCreditResponse generateOxigenOtpResponse =
			 * handleOxigenOtpResponse(oxigenGeneratedOtpResponse);
			 * logger.info("Ewallet credit Response of Search Edge API is " +
			 * oxigenGeneratedOtpResponse.toString()); MAXSaleReturnTransaction
			 * maxSaleReturnTransaction = (MAXSaleReturnTransaction) cargo
			 * .getTransaction();
			 * 
			 * 
			 * if (cargo.eWalletTraceId != null &&
			 * generateOxigenOtpResponse.getResponseHeader() .getResponseCode().toString()
			 * .equalsIgnoreCase(MAXOxigenTenderConstants.USER_NOT_FOUND)) { messgArray[0] =
			 * "Customer Not Attached To The Transaction \n Or \n Wallet Not Found For Customer"
			 * ; dialogModel.setArgs(messgArray);
			 * dialogModel.setResourceID("OxigenUserError");
			 * dialogModel.setArgs(messgArray); dialogModel.setType(DialogScreensIfc.ERROR);
			 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
			 * CommonLetterIfc.CANCEL);
			 * uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			 * //bus.mail("Cancel");
			 * 
			 * return; }
			 * 
			 * 
			 * 
			 * if (generateOxigenOtpResponse != null &&
			 * generateOxigenOtpResponse.getResponseHeader() .getResponseCode().toString()
			 * .equalsIgnoreCase("SUCCESS")) { //cargo.setEWalletTenderFlag(true);
			 * maxSaleReturnTransaction.setEWalletTenderFlag(true); maxSaleReturnTransaction
			 * .seteWalletCreditResponse(oxigenGeneratedOtpResponse); bus.mail("Cash",
			 * BusIfc.CURRENT);
			 * 
			 * return; } else { messgArray[0] =
			 * "Incorrect OTP entered to add refund amount to Wallet. Please ask the customer the OTP again or re-send fresh OTP"
			 * ; dialogModel.setArgs(messgArray);
			 * dialogModel.setResourceID("OxigenUserError");
			 * dialogModel.setArgs(messgArray);
			 * //dialogModel.setResourceID("FailureMessageScreen");
			 * dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
			 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
			 * CommonLetterIfc.FAILURE);
			 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,
			 * CommonLetterIfc.CANCEL);
			 * uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel); return; }
			 * 
			 * 
			 * } catch (SocketTimeoutException socketTimeOutException) { messgArray[0] =
			 * socketTimeOutException.getMessage(); dialogModel.setArgs(messgArray);
			 * dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
			 * dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
			 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
			 * CommonLetterIfc.FAILURE);
			 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,
			 * CommonLetterIfc.CANCEL);
			 * uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel); return; }
			 * 
			 * catch (ConnectException connectException) { messgArray[0] =
			 * connectException.getMessage(); dialogModel.setArgs(messgArray);
			 * dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
			 * dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
			 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
			 * CommonLetterIfc.FAILURE);
			 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,
			 * CommonLetterIfc.CANCEL);
			 * uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel); return; }
			 * catch (NoRouteToHostException noRouteToHostException) { messgArray[0] =
			 * noRouteToHostException.getMessage(); dialogModel.setArgs(messgArray);
			 * dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
			 * dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
			 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
			 * CommonLetterIfc.FAILURE);
			 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,
			 * CommonLetterIfc.CANCEL);
			 * uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel); return; }
			 * catch (UnknownHostException unknownHostException) { messgArray[0] =
			 * "Connectivity Error"; dialogModel.setArgs(messgArray);
			 * dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
			 * dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
			 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
			 * CommonLetterIfc.FAILURE);
			 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,
			 * CommonLetterIfc.CANCEL);
			 * uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel); return; }
			 * 
			 * catch (JsonParseException jsonParseException) { messgArray[0] =
			 * "Error In Calling Oxigen Webservice Request";
			 * dialogModel.setArgs(messgArray);
			 * dialogModel.setResourceID(MAXOxigenTenderConstants.OXIGENERROR);
			 * dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
			 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
			 * CommonLetterIfc.FAILURE);
			 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,
			 * CommonLetterIfc.CANCEL);
			 * uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel); return;
			 * 
			 * // TODO: handle exception }
			 * 
			 * } catch (Exception e) { e.printStackTrace();
			 * logger.error("Error in setting oxigen Ewallet Reversal webservice calling " +
			 * e.getMessage()); }
			 * 
			 * finally { if (connection != null) { connection.disconnect(); } }
			 * 
			 * 
			 * 
			 * }
			 * 
			 * //// Changes ends(kamlesh Pant:EWallet Reversal)
			 * 
			 * // changes starts for Rev 1.1 (Ashish :Loyalty OTP)
			 */ 
			 if (!letter.equalsIgnoreCase("PlutusError")) { if
			  (!currentLetter.equals("Retry")) { if (offlineFlag == false)
			  txnADO.removeTender(tenderADO); if (cashBackTenderADO != null) {
			  txnADO.removeTender(cashBackTenderADO); } JournalFactoryIfc jrnlFact = null;
			  try { jrnlFact = JournalFactory.getInstance(); } catch (ADOException e) {
			  logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e); throw new
			  RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e); }
			  RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal(); if
			  (cashBackTenderADO != null) { registerJournal.journal(cashBackTenderADO,
			  JournalFamilyEnum.TENDER, JournalActionEnum.DELETE); }
			  registerJournal.journal(tenderADO, JournalFamilyEnum.TENDER,
			  JournalActionEnum.DELETE); } } if (cargo != null && cargo.getTransaction() !=
			  null && cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
			  ((MAXSaleReturnTransaction)
			  cargo.getTransaction()).setFatalDeviceCall(false); } if
			  (!letter.equals("VoidOfflineTransaction")) { bus.mail(letter,
			  BusIfc.CURRENT); } else { displayDialog(ui, dialogId, DialogScreensIfc.ERROR,
			  letter);
			  return; 
			  }
			 
			  //}
	}

	private String showConfirmationDialog(BusIfc bus) {

		// TODO Auto-generated method stub
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		// int buttons[] = new int[2];
		DialogBeanModel dialogModel = new DialogBeanModel();
		// buttons[0] = DialogScreensIfc.BUTTON_YES;
		// buttons[1] = DialogScreensIfc.BUTTON_NO;

		dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMDELETETENDER);
		dialogModel.setType(DialogScreensIfc.CONFIRMATION);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Yes");
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "No");

		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		// ui.setModel(paramString, paramUIModelIfc);

		return (bus.getCurrentLetter().toString());
	}

	/* Changes for Rev 1.8 starts */
	public void setResponseData(MAXTenderCargo cargo, MAXPaytmResponse response) {
		response.setStoreId(cargo.getStoreStatus().getStore().getStoreID());
		response.setRegisterId(cargo.getRegister().getWorkstation().getWorkstationID());
		response.setTillId(cargo.getTillID());
		response.setPhoneNumber(cargo.getAmazonPayPhoneNumber());
		response.setBussinessdate(cargo.getStoreStatus().getBusinessDate());
		response.setTotalTransactionAmt(cargo.getTransaction().getTransactionTotals().getSubtotal().toString());
		response.setRequestTypeB(MAXPaytmTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		response.setTimeOut(MAXPaytmConfig.get(MAXPaytmTenderConstants.CONNECTIONTIMEOUT));
		response.setTransactionId(cargo.getCurrentTransactionADO().getTransactionID());
	}

	/* Changes for Rev 1.8 starts */
	public void setResponseDataForAmazonPayRefund(MAXTenderCargo cargo, MAXAmazonPayResponse response) {
		String connectionTimeout = Gateway.getProperty("application", "AmazonPayTimeOutInMilliSeconds", "");
		response.setStoreId(cargo.getStoreStatus().getStore().getStoreID());
		response.setRegisterId(cargo.getRegister().getWorkstation().getWorkstationID());
		response.setTillId(cargo.getTillID());
		response.setPhoneNumber(cargo.getAmazonPayPhoneNumber());
		response.setBussinessdate(cargo.getStoreStatus().getBusinessDate());
		response.setTotalTransactionAmt(cargo.getTransaction().getTransactionTotals().getSubtotal().toString());
		response.setRequestTypeB(MAXAmazonPayTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		response.setTimeOut(connectionTimeout);
		response.setTransactionId(cargo.getCurrentTransactionADO().getTransactionID());
	}

	/* Changes for Rev 1.8 ends */

	public void setMobikwikResponseData(MAXTenderCargo cargo, MAXMobikwikResponse response) {
		response.setStoreId(cargo.getStoreStatus().getStore().getStoreID());
		response.setRegisterId(cargo.getRegister().getWorkstation().getWorkstationID());
		response.setTillId(cargo.getTillID());
		response.setPhoneNumber(cargo.getMobikwikMobileNo());
		response.setBussinessdate(cargo.getStoreStatus().getBusinessDate());
		response.setTotalTransactionAmt(cargo.getTransaction().getTransactionTotals().getSubtotal().toString());
		response.setRequestTypeB(MAXMobikwikTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
		response.setTimeOut(MAXMobikwikConfig.get(MAXMobikwikTenderConstants.CONNECTIONTIMEOUT));
		response.setTransactionId(cargo.getCurrentTransactionADO().getTransactionID());
	}

	public int getCouponType(BusIfc bus, String couponName) {
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		String list[] = null;
		try {
			list = pm.getStringValues("FoodCouponTypeList");
			for (int i = 0; i < list.length; i++)
				if (couponName.equalsIgnoreCase(list[i]))
					return 1;
			list = pm.getStringValues("NonFoodCouponTypeList");
			for (int i = 0; i < list.length; i++)
				if (couponName.equalsIgnoreCase(list[i]))
					return 0;
		} catch (ParameterException e) {
			logger.error(e);

		}
		return -1;

	}

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
			// Changes starts for Rev 1.1 (Ashish :OTP Loyalty)
			/*
			 * SimpleDateFormat sm = new SimpleDateFormat("dd-MMM-yyyy"); Date date = new
			 * Date(requestInfo.get(MAXLoyaltyConstants.INVOICE_BUSINESS_DATE ).toString());
			 * String mdy = sm.format(date);
			 */
			String str = requestInfo.get(MAXLoyaltyConstants.INVOICE_BUSINESS_DATE).toString();
			DateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
			Date dt = null;
			try {
				dt = format.parse(str);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
			format = new SimpleDateFormat("dd/MM/YY");
			String st = format.format(dt);
			long epoch = dt.getTime();
			Long invoiceDate = new Long(epoch);

			urlParameters += "&" + URLEncoder.encode("aBusDt", "UTF-8") + "="
					+ URLEncoder.encode(/*
										 * requestInfo . get ( MAXLoyaltyConstants . INVOICE_BUSINESS_DATE ) . toString
										 * ( )
										 */invoiceDate.toString(), "UTF-8");
			// Changes starts for Rev 1.1 (Ashish :OTP Loyalty)
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

	private HashMap populateHashMap(AbstractTenderableTransaction transaction, HashMap reversalAttributes,
			TenderLineItemIfc tenderLineItem) {
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
		reversalAttributes.put(MAXLoyaltyConstants.SETTLE_TOTAL_AMOUNT, tenderLineItem.getAmountTender());
		reversalAttributes.put(MAXLoyaltyConstants.REQUEST_TYPE_B, MAXLoyaltyConstants.RELEASE);
		reversalAttributes.put(MAXLoyaltyConstants.REQUEST_DATE_TIME,
				date.toFormattedString(MAXLoyaltyConstants.DATE_FORMAT_NOW));
		reversalAttributes.put(MAXLoyaltyConstants.REQUEST_TIME_OUT, timeOut);
		reversalAttributes.put(MAXLoyaltyConstants.REQUEST_URL, URL);
		reversalAttributes.put(MAXLoyaltyConstants.TIME_OUT_REQUEST_MESSAGE_ID, null);
		return reversalAttributes;
	}

	public HashMap parseResponseDate(String output) {

		HashMap responseDataMap = new HashMap();
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
			// System.out.println("IsManualEntry: " +
			// getCharacterDataFromElement(line));

			NodeList track1 = element.getElementsByTagName("CardNumber");
			line = (Element) track1.item(0);
			responseDataMap.put("CardNumber", getCharacterDataFromElement(line));
			// System.out.println("CardNumber: " +
			// getCharacterDataFromElement(line));

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
			// System.out.println("IssuerName: " +
			// getCharacterDataFromElement(line));

			NodeList track5 = element.getElementsByTagName("SchemeType");
			line = (Element) track5.item(0);
			responseDataMap.put("SchemeType", getCharacterDataFromElement(line));
			// System.out.println("SchemeType: " +
			// getCharacterDataFromElement(line));

			NodeList nodes1 = doc.getElementsByTagName("SelectedAcquirer");
			// iterate the employees
			Element element1 = (Element) nodes1.item(0);

			NodeList track6 = element1.getElementsByTagName("ID");
			Element line1 = (Element) track6.item(0);
			responseDataMap.put("SelectedAcquirerID", getCharacterDataFromElement(line1));
			// System.out.println("ID: " + getCharacterDataFromElement(line1));

			NodeList track7 = element1.getElementsByTagName("Name");
			line1 = (Element) track7.item(0);
			responseDataMap.put("SelectedAquirerName", getCharacterDataFromElement(line1));
			// System.out.println("Name: " +
			// getCharacterDataFromElement(line1));

			NodeList track8 = element1.getElementsByTagName("DiscountRate");
			line1 = (Element) track8.item(0);
			responseDataMap.put("SelectedAquirerDiscountRate", getCharacterDataFromElement(line1));
			// System.out.println("DiscountRate: " +
			// getCharacterDataFromElement(line1));

			NodeList track9 = element1.getElementsByTagName("Status");
			line1 = (Element) track9.item(0);
			responseDataMap.put("SelectedAquirerStatus", getCharacterDataFromElement(line1));
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
			// System.out.println("Address: " +
			// getCharacterDataFromElement(line2));

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
			// System.out.println("ResponseCode: " +
			// getCharacterDataFromElement(line3));

			NodeList track15 = HostResponseElem.getElementsByTagName("ResponseMessage");
			line3 = (Element) track15.item(0);
			responseDataMap.put("HostResponseMessage", getCharacterDataFromElement(line3));
			// System.out.println("ResponseMessage: " +
			// getCharacterDataFromElement(line3));

			NodeList track16 = HostResponseElem.getElementsByTagName("ApprovalCode");
			line3 = (Element) track16.item(0);
			responseDataMap.put("HostResponseApprovalCode", getCharacterDataFromElement(line3));
			// System.out.println("ApprovalCode: " +
			// getCharacterDataFromElement(line3));

			NodeList track17 = HostResponseElem.getElementsByTagName("RetrievalReferenceNumber");
			line3 = (Element) track17.item(0);
			responseDataMap.put("HostResponseRetrievelRefNumber", getCharacterDataFromElement(line3));
			// System.out.println("RetrievalReferenceNumber: " +
			// getCharacterDataFromElement(line3));

			NodeList State = doc.getElementsByTagName("State");
			// iterate the employees
			Element StateElem = (Element) State.item(0);

			NodeList track18 = StateElem.getElementsByTagName("TID");
			Element line4 = (Element) track18.item(0);
			responseDataMap.put("StateTID", getCharacterDataFromElement(line4));
			// System.out.println("TID: " + getCharacterDataFromElement(line4));

			NodeList track19 = StateElem.getElementsByTagName("InvoiceNumber");
			line4 = (Element) track19.item(0);
			responseDataMap.put("StateInvoiceNumber", getCharacterDataFromElement(line4));
			// System.out.println("InvoiceNumber: " +
			// getCharacterDataFromElement(line4));

			NodeList track20 = StateElem.getElementsByTagName("BatchNumber");
			line4 = (Element) track20.item(0);
			responseDataMap.put("StateBatchNumber", getCharacterDataFromElement(line4));
			// System.out.println("BatchNumber: " +
			// getCharacterDataFromElement(line4));

			NodeList track21 = StateElem.getElementsByTagName("AcquirerName");
			line4 = (Element) track21.item(0);
			responseDataMap.put("StateAquirerName", getCharacterDataFromElement(line4));
			// System.out.println("AcquirerName: " +
			// getCharacterDataFromElement(line4));

			NodeList track22 = StateElem.getElementsByTagName("TransactionTime");
			line4 = (Element) track22.item(0);
			responseDataMap.put("StateTransactionTime", getCharacterDataFromElement(line4));
			// System.out.println("TransactionTime: " +
			// getCharacterDataFromElement(line4));

			NodeList track23 = StateElem.getElementsByTagName("Amount");
			line4 = (Element) track23.item(0);
			responseDataMap.put("StateAmount", getCharacterDataFromElement(line4));
			System.out.println("Amount: " + getCharacterDataFromElement(line4));

			NodeList track24 = StateElem.getElementsByTagName("Discount");
			line4 = (Element) track24.item(0);
			responseDataMap.put("StateDiscount", getCharacterDataFromElement(line4));
			// System.out.println("Discount: " +
			// getCharacterDataFromElement(line4));

			NodeList track25 = StateElem.getElementsByTagName("TotalAmount");
			line4 = (Element) track25.item(0);
			responseDataMap.put("StateTotalAmount", getCharacterDataFromElement(line4));
			// System.out.println("TotalAmount: " +
			// getCharacterDataFromElement(line4));

			NodeList track26 = StateElem.getElementsByTagName("StatusCode");
			line4 = (Element) track26.item(0);
			responseDataMap.put("StateStatusCode", getCharacterDataFromElement(line4));
			// System.out.println("StatusCode: " +
			// getCharacterDataFromElement(line4));

			NodeList track27 = StateElem.getElementsByTagName("StatusMessage");
			line4 = (Element) track27.item(0);
			responseDataMap.put("StateStatusMessage", getCharacterDataFromElement(line4));
			// System.out.println("StatusMessage: " +
			// getCharacterDataFromElement(line4));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseDataMap;
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

	// Changes start for rev 1.1 (Ashish : Loyalty OTP)
	// protected void processSuccessResponse(String response,
	// MAXLoyaltyDataTransaction loyaltyDataTransaction, HashMap
	// reversalAttributes) {
	protected void processSuccessResponse(String response, MAXLoyaltyDataTransaction loyaltyDataTransaction,
			HashMap reversalAttributes, MAXCRMObj crmobj) {
		// Changes start for rev 1.1 (Ashish : Loyalty OTP)

		HashMap newvalue = new HashMap();
		// Changes start for rev 1.1 (Ashish : Loyalty OTP)
		/*
		 * String trimString = response.toString().trim(); String[] splitStr =
		 * trimString.split("\r\r\r");
		 * 
		 * String resMsg = splitStr[0]; String valMsg = splitStr[1];
		 * 
		 * String[] resFlag = resMsg.split(":"); String[] messValue = valMsg.split(":");
		 * 
		 * newvalue.put(resFlag[0], resFlag[1]); newvalue.put(messValue[0],
		 * messValue[1]);
		 * 
		 * newvalue.put(MAXLoyaltyConstants.RESPONSE_APPROVED_VALUE, null);
		 * newvalue.put(MAXLoyaltyConstants.MESSAGE_ID,
		 * reversalAttributes.get(MAXLoyaltyConstants.MESSAGE_ID));
		 * newvalue.put(MAXLoyaltyConstants.FLAG, MAXLoyaltyConstants.RESPONSE_FLAG);
		 * newvalue.put(MAXLoyaltyConstants.REQUEST_STATUS,
		 * MAXLoyaltyConstants.RESPONSE_RECEIVED);
		 */
		if (crmobj != null) {
			newvalue.put(MAXLoyaltyConstants.RESPONSE_MESSAGE, crmobj.getMessage());
			newvalue.put(MAXLoyaltyConstants.RESPONSE, crmobj.getResponse());
			newvalue.put(MAXLoyaltyConstants.RESPONSE_APPROVED_VALUE, null);
			newvalue.put(MAXLoyaltyConstants.MESSAGE_ID, reversalAttributes.get(MAXLoyaltyConstants.MESSAGE_ID));
			newvalue.put(MAXLoyaltyConstants.FLAG, MAXLoyaltyConstants.RESPONSE_FLAG);
			newvalue.put(MAXLoyaltyConstants.REQUEST_STATUS, MAXLoyaltyConstants.RESPONSE_RECEIVED);
			newvalue.put(MAXLoyaltyConstants.REQUEST_STATUS, MAXLoyaltyConstants.RESPONSE_RECEIVED);

			// Changes end for rev 1.1 (Ashish : Loyalty OTP)
			// Update WEB REQUEST TABLE
			try {
				loyaltyDataTransaction.updateRequest(newvalue);
			} catch (DataException e) {
				Logger.getLogger("Data Error:::" + MAXDeleteTendersActionSite.class.getName());
			}
		}
	}

	private byte[] GetTransmissionPacketForCentral(int txnType, String csvData) {
		int iOffset = 0;
		byte[] msgBytes = csvData.getBytes();
		int iCSVLen = msgBytes.length;
		int finalMsgLen = iCSVLen + 7; // 7 = 2 byte source , 2 byte function
										// code, 2 byte length, 1 byte
										// termination
		byte[] msgBytesExtra = new byte[finalMsgLen];

		// source id - 2 bytes
		msgBytesExtra[iOffset] = 0x10;
		iOffset++;
		msgBytesExtra[iOffset] = 0x00;
		iOffset++;
		// function code or MTI - 2 bytes
		msgBytesExtra[iOffset] = 0x09;
		iOffset++;
		msgBytesExtra[iOffset] = (byte) 0x97;
		iOffset++;
		// data length to follow
		msgBytesExtra[iOffset] = (byte) ((byte) (iCSVLen >> 8) & 0xFF);
		iOffset++;
		msgBytesExtra[iOffset] = (byte) (iCSVLen & 0xFF);
		iOffset++;
		//
		System.arraycopy(msgBytes, 0, msgBytesExtra, iOffset, msgBytes.length);
		iOffset += msgBytes.length;
		msgBytesExtra[iOffset] = (byte) 0xFF;
		iOffset++;

		System.out.println(byteArrayToHexString(msgBytesExtra));
		return msgBytesExtra;
	}

	public static String byteArrayToHexString(byte in[]) {
		byte ch = 0x00;
		int i = 0;
		if (in == null || in.length <= 0)
			return null;

		String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
		StringBuffer out = new StringBuffer(in.length * 2);

		while (i < in.length) {
			ch = (byte) (in[i] & 0xF0); // Strip off high nibble
			ch = (byte) (ch >>> 4); // shift the bits down
			ch = (byte) (ch & 0x0F); // must do this is high order bit is on!
			out.append(pseudo[(int) ch]); // convert the nibble to a String
											// Character
			ch = (byte) (in[i] & 0x0F); // Strip off low nibble
			out.append(pseudo[(int) ch]); // convert the nibble to a String
											// Character
			i++;
		}
		String rslt = new String(out);
		return rslt;
	}

	public static String bcd2a(byte[] src) {
		String dest = "";
		int len = src.length;
		byte b1;
		byte b2;
		for (int i = 0; i < len; i++) {
			b1 = src[i];
			b1 = (byte) (b1 & 0x0F);
			b2 = (byte) (src[i] >>> 4);
			b2 = (byte) (b2 & 0x0F);
			dest = dest + (int) b2 + (int) b1;
		}
		return dest;
	}

	protected void displayDialog(POSUIManagerIfc ui, String name, int dialogType, String letter) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(name);
		dialogModel.setType(dialogType);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, letter);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

	}

	// Changes starts for rev1 1.1 (Ashish :Loyalty OTP)
	// Changes start for rev 1.11
	private static String createOTPCallRequestMessage(MAXCRMObj crmobj, HashMap requestInfo) {
		StringWriter reqJsonMessage = new StringWriter();
		Boolean sendSMS = new Boolean(true);
		JSONObject objLoyaltyValues = new JSONObject();
		// Changes starts for Rev 1.1 (Ashish : OTP)
		String str = requestInfo.get(MAXLoyaltyConstants.INVOICE_BUSINESS_DATE).toString();
		/*
		 * SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy"); Date date = null; try
		 * { date = df.parse(str); } catch (java.text.ParseException e) {
		 * System.out.println(e); }
		 */
		DateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
		Date dt = null;
		try {
			dt = format.parse(str);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		format = new SimpleDateFormat("dd/MM/YY");
		String st = format.format(dt);
		long epoch = dt.getTime();
		// Changes starts for Rev 1.1 (Ashish : OTP)
		Long invoiceDate = new Long(epoch);
		objLoyaltyValues.put(MAXLoyaltyConstants.SEND_SMS, sendSMS);
		objLoyaltyValues.put(MAXLoyaltyConstants.OTP_MESSAGE_ID,
				requestInfo.get(MAXLoyaltyConstants.MESSAGE_ID).toString());
		objLoyaltyValues.put(MAXLoyaltyConstants.OTP_TIME_OUT_REQUEST_MESSAGE_ID, null);
		objLoyaltyValues.put(MAXLoyaltyConstants.OTP_REQUEST_TYPE_B,
				requestInfo.get(MAXLoyaltyConstants.REQUEST_TYPE_B).toString());
		objLoyaltyValues.put(MAXLoyaltyConstants.OTP_STORE_ID,
				requestInfo.get(MAXLoyaltyConstants.STORE_ID).toString());
		objLoyaltyValues.put(MAXLoyaltyConstants.OTP_TILL_ID, requestInfo.get(MAXLoyaltyConstants.TILL_ID).toString());
		objLoyaltyValues.put(MAXLoyaltyConstants.OTP_INVOICE_BUSINESS_DATE, invoiceDate);
		objLoyaltyValues.put(MAXLoyaltyConstants.OTP_INVOICE_NUMBER,
				requestInfo.get(MAXLoyaltyConstants.INVOICE_NUMBER).toString());
		objLoyaltyValues.put(MAXLoyaltyConstants.OTP_TRAN_TOTAL_AMOUNT,
				requestInfo.get(MAXLoyaltyConstants.TRAN_TOTAL_AMOUNT).toString());
		objLoyaltyValues.put(MAXLoyaltyConstants.OTP_SETTLE_TOTAL_AMOUNT,
				requestInfo.get(MAXLoyaltyConstants.SETTLE_TOTAL_AMOUNT).toString());
		if (requestInfo.get(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER) != null) {
			objLoyaltyValues.put(MAXLoyaltyConstants.OTP_LOYALTY_CARD_NUMBER,
					requestInfo.get(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER).toString());
		}

		return objLoyaltyValues.toString();
	}

	private MAXCRMObj executePost(MAXCRMObj crmCRMObj) {
		URL url;
		// int responseCode;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL(crmCRMObj.getCRM_URL());
			connection = (HttpURLConnection) url.openConnection();
			String urlParameters = crmCRMObj.getRequestMessage();
			logger.info("CRM executePost() :: url:" + url + " , urlParameters:" + urlParameters);
			String timeOut = Gateway.getProperty("application", "LoyaltytimeOutInMilliSeconds", null);
			;
			System.getProperties().setProperty("sun.net.client.defaultConnectTimeout", timeOut);
			System.getProperties().setProperty("sun.net.client.defaultReadTimeout", timeOut);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			// Get Response
			responseCode = connection.getResponseCode();
			connection.getResponseMessage();

			logger.info("executePost() :: responseCode:" + responseCode + " , ResponseMessage:"
					+ connection.getResponseMessage());
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);

				response.append('\n');
			}
			rd.close();
			logger.info("**** CRM RESPONSE: " + response.toString());
			crmCRMObj.setConnResponseCode(String.valueOf(connection.getResponseCode()));
			crmCRMObj.setConnResponseMessage(response.toString());
		} catch (MalformedURLException e) {
			logger.error("Response not recieved::" + e.getMessage() + "");
		} catch (SocketTimeoutException e) {
			logger.error("Response not recieved::" + e.getMessage() + "");
		} catch (Exception e) {
			logger.error("Error in sending Request" + e.getMessage() + "");
			try {
				responseCode = connection.getResponseCode();
				crmCRMObj.setResponseMessage(e.getMessage());
			} catch (IOException e1) {
				logger.error("IO Exception Caught::" + e1.getMessage() + "");
				crmCRMObj.setResponse(e1.toString());
			}
			// return MAXCRMObj;
		} finally {
			if (connection != null) {
				connection.disconnect();
				Properties systemProperties = System.getProperties();
				systemProperties.setProperty("http.proxyHost", "");
				systemProperties.setProperty("http.proxyPort", "");
			}
		}

		return crmCRMObj;
		// Changes end for rev 1.11
	}
	// Changes ends for Rev 1.1 (Ashish :Loyalty OTP)

	// Changes starts(kamlesh Pant:EWallet Reversal)

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static JSONObject getJsonRequestObject(MAXTenderCargo cargo) {
		String eWalletTraceId = cargo.eWalletTraceId;
		String mobileNo = null;
		String invoiceNo = null;
		Date invoiceDate = null;
		String formattedInvoiceDate = null;
		String invoiceGrossAmt = null;
		String invoiceNetAmt = null;
		// Vector<TenderLineItemIfc> modeOfPayment=null;
		String promoCode = "BOGO";
		// String amount=null;
		// String subWalletType=null;
		MAXCustomer customer = null;
		MAXSaleReturnTransaction maxTransaction = null;
		JSONArray array = new JSONArray();
		// array.add("CASH");
		// array.add("WALLET");
		// array.add("LOYALTY_WALLET");
		// array.add("CARD");
		JSONObject job = new JSONObject();
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		String requestTimestamp = format.format(date);
		SimpleDateFormat invoiceDateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		// String requestDateTimestamp = requestTimestampformat.format(date);
		// SaleReturnTransactionIfc[] origTransaction =
		// cargo.getOriginalReturnTransactions();
		// MAXSaleReturnTransaction maxorigTrx = null;

		/*
		 * for (int i = 0; i < origTransaction.length; i++) { if (origTransaction[i]
		 * instanceof MAXSaleReturnTransaction) { maxorigTrx =(MAXSaleReturnTransaction)
		 * origTransaction[i]; TenderLineItemIfc[] tenderLineItems = maxorigTrx
		 * .getTenderLineItems();
		 * 
		 * for (int j = 0; j < tenderLineItems.length; j++) { TenderLineItemIfc lineItem
		 * = tenderLineItems[j]; array.add(lineItem.getTypeCodeString().toString()); } }
		 * 
		 * }
		 */

		if (cargo.getTransaction().getCustomer() != null
				&& cargo.getTransaction().getCustomer() instanceof MAXCustomer) {
			customer = (MAXCustomer) cargo.getTransaction().getCustomer();
			mobileNo = customer.getPrimaryPhone().getPhoneNumber().toString();
		}
		if (cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
			maxTransaction = (MAXSaleReturnTransaction) cargo.getTransaction();
			invoiceNo = maxTransaction.getTransactionID().toString();
			invoiceDate = maxTransaction.getBusinessDay().toDate();
			formattedInvoiceDate = invoiceDateformat.format(invoiceDate);
			invoiceGrossAmt = maxTransaction.getTransactionTotals().getGrandTotal().abs().toString();
			invoiceNetAmt = maxTransaction.getTransactionTotals().getPreTaxSubtotal().abs().toString();

		}

		Map m1 = new LinkedHashMap(3);
		// job.put(MAXOxigenTenderConstants.TRANSACTIONID,
		// cargo.getTransaction().getTransactionID().toString());
		job.put(MAXOxigenTenderConstants.TRANSACTIONID, eWalletTraceId);

		m1 = new LinkedHashMap(2);
		m1.put(MAXOxigenTenderConstants.STORE_CODE, cargo.getStoreStatus().getStore().getStoreID());
		m1.put(MAXOxigenTenderConstants.TERMINAL_ID, cargo.getRegister().getWorkstation().getWorkstationID());
		m1.put(MAXOxigenTenderConstants.OPTIONAL_INFO, null);
		job.put(MAXOxigenTenderConstants.STORE_DETAILS, m1);
		m1 = new LinkedHashMap(4);
		m1.put(MAXOxigenTenderConstants.REQUEST_TYPE, MAXOxigenTenderConstants.WALLET_REVERSAL_REQUEST);
		m1.put(MAXOxigenTenderConstants.REQUEST_ID, cargo.getTransaction().getTransactionID().toString());
		m1.put(MAXOxigenTenderConstants.REQUEST_TIME, requestTimestamp.toString());
		// m1.put(MAXOxigenTenderConstants.REQUEST_TIME, requestDateTimestamp);
		m1.put(MAXOxigenTenderConstants.MOBILENUMBER, mobileNo);
		// m1.put(MAXOxigenTenderConstants.MOBILENUMBER, "9873477777");
		m1.put(MAXOxigenTenderConstants.ORIGINAL_DIALOGUE_TRACE_ID, "");
		m1.put(MAXOxigenTenderConstants.WALLET_OWNER, MAXOxigenTenderConstants.SPAR_CONSTANT);
		m1.put(MAXOxigenTenderConstants.CHANNEL, MAXOxigenTenderConstants.POS_CONSTANT);
		job.put(MAXOxigenTenderConstants.REQUEST_HEADER, m1);
		// m1.put(MAXOxigenTenderConstants.OTP_TYPE,MAXOxigenTenderConstants.WALLET_CREDIT_REQUEST);
		/*
		 * if (cargo.getOtpRefNum()!=null) { m1.put(MAXOxigenTenderConstants.REF_NUM,
		 * cargo.getOtpRefNum()); } else { m1.put(MAXOxigenTenderConstants.REF_NUM,
		 * null); }
		 */
		// m1.put(MAXOxigenTenderConstants.OTP, creditOtp);
		// job.put(MAXOxigenTenderConstants.OTP_DETAILS, m1);x
		m1 = new LinkedHashMap(4);
		m1.put(MAXOxigenTenderConstants.INVOICENO, invoiceNo);
		m1.put(MAXOxigenTenderConstants.INVOICEDATE, formattedInvoiceDate);
		m1.put(MAXOxigenTenderConstants.INVOICEGROSSAMOUNT, invoiceGrossAmt);
		m1.put(MAXOxigenTenderConstants.INVOICENETAMOUNT, invoiceNetAmt);
		m1.put(MAXOxigenTenderConstants.MODEOFPAYMENT, array);
		m1.put(MAXOxigenTenderConstants.PROMOCODE, promoCode);
		job.put(MAXOxigenTenderConstants.TRANSACTIONINFO, m1);
		job.put(MAXOxigenTenderConstants.AMOUNT, invoiceNetAmt);
		// job.put(MAXOxigenTenderConstants.SUBWALLETTYPE,"CREDIT_NOTE");

		System.out.println("Reversal request :" + job);
		return job;
	}

	public String executeOtpGeneratRequest(String URL, JSONObject jsonContentObj)
			throws IOException, JsonParseException {

		URL targetURL = new URL(URL);

		connection = (HttpURLConnection) targetURL.openConnection();
		connection.setRequestMethod(MAXOxigenTenderConstants.REQUEST_METHOD_POST);
		String urlParameters = jsonContentObj.toString();

		connection.setRequestProperty(MAXOxigenTenderConstants.CONTENTTYPE, MAXOxigenTenderConstants.JSON);
		connection.setRequestProperty(MAXOxigenTenderConstants.CONTENTLENGTH,
				Integer.toString(urlParameters.getBytes().length));
		connection.setUseCaches(false);
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);

		connection.setDoOutput(true);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.close();
		int responseCode = connection.getResponseCode();
		InputStream is;
		if (responseCode == HttpURLConnection.HTTP_OK) {
			is = connection.getInputStream();
		} else {
			is = connection.getErrorStream();
		}

		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		StringBuilder response = new StringBuilder(); // or StringBuffer if not
														// Java 5+
		String line = "";
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
		logger.debug("Response Body:::" + response);
		System.out.println("Reversal 2539 :" + response);
		return response.toString();
	}

	/*
	 * private MAXOxigenWalletCreditResponse handleOxigenOtpResponse( String
	 * oxigenGeneratedOtpResponse) throws JsonParseException, JsonMappingException,
	 * IOException { ObjectMapper mapper = new ObjectMapper();
	 * MAXOxigenWalletCreditResponse oxigenOtpResponse = mapper
	 * .readValue(oxigenGeneratedOtpResponse, MAXOxigenWalletCreditResponse.class);
	 * return oxigenOtpResponse; }
	 */

}
//Changes ends(kamlesh Pant:EWallet Reversal)