/********************************************************************************
 *   
 *	Copyright (c) 2015  MAX India pvt Ltd    All Rights Reserved.
 *	
 *	Rev	1.0 	11-May-2017		Ashish Yadav			Changes for M-Coupon Issuance FES
 *
 ********************************************************************************/
package max.retail.stores.pos.services.tender.mcoupon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.capillary.solutions.landmark.api.TransactionApi;
import com.capillary.solutions.landmark.transaction.dto.Effect;
import com.capillary.solutions.landmark.transaction.dto.LineItem;
import com.capillary.solutions.landmark.transaction.dto.Payment;
import com.capillary.solutions.landmark.transaction.dto.TransactionRequest;
import com.capillary.solutions.landmark.transaction.dto.TransactionResponse;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.mcoupon.MAXMcoupon;
import max.retail.stores.domain.mcoupon.MAXMcouponIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import org.apache.log4j.Logger;


public class MAXMcouponCapillaryRequestSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = 1L;
	private static final String TYPE = "Regular";
	private static final String COUPON_ISSUAL_CRITERIA = "1";
	private static final int CAPILLARY_RESP_SUCCESS = 600;
	private static final int CAPILLARY_NETWORKERROR = 500;
	  private static final Logger LOGGER = Logger.getLogger(MAXMcouponCapillaryRequestSite.class);

	@Override
	public void arrive(BusIfc bus) {

		String mobile = null;
		String email = null;
		String cardNumber = null; // LMR_ID
		String firstName = null;
		String lastName = null;

		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(POSUIManagerIfc.TYPE);
		MAXSaleReturnTransaction transaction = null;
		if (cargo.getTransaction() != null && cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
			transaction = (MAXSaleReturnTransaction) cargo.getTransaction();
		}

		MAXCustomerIfc customerIfc = null;
		if (transaction.getCustomer() != null && transaction.getCustomer() instanceof MAXCustomer) {
			customerIfc = (MAXCustomerIfc) transaction.getCustomer();
		}

		// // get LMR customer data
		if (customerIfc != null && customerIfc.getCustomerID() != null
				&& !customerIfc.getCustomerID().equalsIgnoreCase("")
				&& customerIfc.getCustomerType().equalsIgnoreCase("T")) {

			// get card number of customer
			cardNumber = customerIfc.getCustomerID();

			// get mobile number of customer
			if (customerIfc.getPhoneByType(2) != null && customerIfc.getPhoneByType(2).getPhoneNumber() != null
					&& !customerIfc.getPhoneByType(2).getPhoneNumber().equalsIgnoreCase("NOTPROVIDED")
					&& !customerIfc.getPhoneByType(2).getPhoneNumber().equalsIgnoreCase("NA")) {
				mobile = (customerIfc.getPhoneByType(2)).getPhoneNumber();
			}

			// /get firstname,lastname and mobil number for customer

			if (customerIfc.getFirstName() != null && !customerIfc.getFirstName().equalsIgnoreCase("NOTPROVIDED")
					&& !customerIfc.getFirstName().equalsIgnoreCase("NA")) {
				firstName = customerIfc.getFirstName();
			}

			if (customerIfc.getLastName() != null && !customerIfc.getLastName().equalsIgnoreCase("NOTPROVIDED")
					&& !customerIfc.getLastName().equalsIgnoreCase("NA")) {
				lastName = customerIfc.getLastName();
			}

			if (customerIfc.getEMailAddress() != null && !customerIfc.getEMailAddress().equalsIgnoreCase("NOTPROVIDED")
					&& !customerIfc.getEMailAddress().equalsIgnoreCase("NA")) {
				email = customerIfc.getEMailAddress();
			}

		} else if (transaction.getMAXTICCustomer() != null
				&& ((MAXTICCustomer) transaction.getMAXTICCustomer()).getTICCustomerID() != null
				&& !((MAXTICCustomer) transaction.getMAXTICCustomer()).getTICCustomerID().equalsIgnoreCase("")) {
			MAXTICCustomer ticCustomer = (MAXTICCustomer) transaction.getMAXTICCustomer();
			cardNumber = ticCustomer.getTICCustomerID();
			firstName = ticCustomer.getTICFirstName();
			lastName = ticCustomer.getTICLastName();
			if (ticCustomer.getTICEmail() != null && !ticCustomer.getTICEmail().equalsIgnoreCase("NOTPROVIDED")
					&& !ticCustomer.getTICEmail().equalsIgnoreCase("NA")) {
				email = ticCustomer.getTICEmail();
			}
			mobile = ticCustomer.getTICMobileNumber();

		}

		if ((cardNumber == null || (cardNumber != null && cardNumber.trim().equalsIgnoreCase("")))) {

			if (ui.getModel(MAXPOSUIManagerIfc.MCOUPON_PHONE_NUMBER) != null) {
				POSBaseBeanModel beanmodel = (POSBaseBeanModel) ui.getModel("MCOUPON_PHONE_NUMBER");
				if (beanmodel != null && beanmodel.getPromptAndResponseModel() != null
						&& beanmodel.getPromptAndResponseModel().getResponseText() != null
						&& !beanmodel.getPromptAndResponseModel().getResponseText().trim().equalsIgnoreCase("")) {
					mobile = beanmodel.getPromptAndResponseModel().getResponseText();
				} else {
					cardNumber = Gateway.getProperty("application", "CAPDefaultExternalID", "5555555555");
				}
			} else {
				cardNumber = Gateway.getProperty("application", "CAPDefaultExternalID", "5555555555");
			}
		}

		// /process screen initialize...
		POSBaseBeanModel base = (POSBaseBeanModel) ui.getModel();
		if (base == null) {
			base = new POSBaseBeanModel();
		}
		PromptAndResponseModel responseModel = new PromptAndResponseModel();
		base.setPromptAndResponseModel(responseModel);
		ui.showScreen(MAXPOSUIManagerIfc.PROCESS_CAPILLARY_SCREEN, base);
		// /process screen initialize end...

		// changes for rev 1.1 starts-capillary request inside try
		try {
			// changes for rev 1.1 ends-capillary request inside try
			// send Capillary request
			TransactionApi transactionApi = TransactionApi.getInstance();
			TransactionRequest request = new TransactionRequest();

			setCapillaryRequest(request, transaction, firstName, lastName, mobile, cardNumber, email);
			TransactionResponse response = null;

			/// response = transactionApi.add(request);
			ExecutorService executorService = Executors.newSingleThreadExecutor();

			/*
			 * Gson gson=new Gson(); System.out.println(gson.toJson(request));
			 *
			 */

			Future<TransactionResponse> future = executorService.submit(new MAXMcouponCapillaryService(request));
			LOGGER.debug("Request for M-coupon Issuance: " + request);

			try {

				long capillaryTimeout = new Long(
						Gateway.getProperty("application", "LoyaltytimeOutInMilliSeconds", "30000"));
				response = future.get(capillaryTimeout, TimeUnit.MILLISECONDS);
				
				LOGGER.info("Response for M-coupon Issuance: SATAUS CODE " + response.getStatus_code());
				LOGGER.info("Response for M-coupon Issuance: Effects " + response.getEffects().get(1));
				LOGGER.info("Response for M-coupon Issuance: is empty " + response.getEffects().isEmpty());
				LOGGER.info("Response for M-coupon Issuance: Effect Straing value " + response.getEffects().toString());
				LOGGER.info("Response for M-coupon Issuance: External ID " + response.getExternal_id());
				LOGGER.info("Response for M-coupon Issuance: Loyalty points " + response.getLoyalty_points());
				LOGGER.info("Response for M-coupon Issuance: Status message " + response.getStatus_message());
			} catch (TimeoutException e) {
				future.cancel(true);
				response = null;
			}
			catch(Exception e){
				
			}
			executorService.shutdownNow();

			// test data set

			/*
			 * if (response.getStatus_code() == 500) { ArrayList<Effect> effects
			 * = new ArrayList<Effect>(); response.setStatus_code(600); Effect
			 * effect = new Effect(); effect.setCoupon_code("10001");
			 * effect.setDescription("10001 TEST LOCAL"); effect.setId("1");
			 * effect.setValid_till("2017-12-12 11:12:11"); effects.add(effect);
			 *
			 * Effect effect1 = new Effect(); effect1.setCoupon_code("10002");
			 * effect1.setDescription("10002  TEST LOCAL"); effect1.setId("2");
			 * effect1.setValid_till("2017-12-12 11:12:12");
			 * effects.add(effect1);
			 *
			 * response.setEffects(effects); }
			 */
			// test data set
/*commented by Abhishek*/
			
			//transaction.setMcouponStatusMessage(response.getStatus_message());
			if (response != null && response.getStatus_code() == CAPILLARY_RESP_SUCCESS) {
				ArrayList<MAXMcouponIfc> list = new ArrayList<MAXMcouponIfc>();
				if (response.getEffects() != null && response.getEffects().size() > 0) {
					list = getMcouponList(response);
					transaction.setMcouponStatusMessage(response.getStatus_message());
				}
				transaction.setMcouponList(list);
				bus.mail(new Letter("ExitTender"), BusIfc.CURRENT);

			} else if (response == null || (response != null && response.getStatus_code() == CAPILLARY_NETWORKERROR)) {
				transaction.setMcouponList(null);
				displayCapillaryConnectionError(ui, "CapillaryConnectionError");

				// /error during
			} else {
				transaction.setMcouponList(null);
				String[] errors = { (response.getStatus_message()) };
				displayCapillaryError(ui, errors);

			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			// changes for rev 1.1 starts
			displayCapillaryConnectionError(ui, "CapillaryConnectionError");
			// bus.mail("ExitTender",BusIfc.CURRENT);
			// changes for rev 1.1 ends
		}

	}

	private void setCapillaryRequest(TransactionRequest request, MAXSaleReturnTransaction transaction,
			String firstName, String lastName, String mobile, String cardNumber, String email) {

		request.setBill_client_id(null);
		request.setReturn_type(null);
		request.setNotes(null);
		request.setPurchase_time(null);
		request.setOriginal_bill_number(null);
		request.setDiscount(transaction.getTenderTransactionTotals().getDiscountTotal().getDoubleValue());
		request.setNot_interested_reason(null);

		request.setType(TYPE);
		request.setNumber(transaction.getTransactionID());
		/// changes for Rev 1.0 param value changes start
		request.setAmount(transaction.getTenderTransactionTotals().getGrandTotal()
				.subtract(transaction.getTenderTransactionTotals().getInclusiveTaxTotal()).getDoubleValue());

		request.setBilling_time(transaction.getTimestampBegin().toFormattedString("yyyy-MM-dd HH:mm:ss"));
		request.setGross_amount(transaction.getTenderTransactionTotals().getGrandTotal().getDoubleValue());

		/// changes for Rev 1.0 param value changes end
		request.setCoupon_issual_criteria(COUPON_ISSUAL_CRITERIA);

		request.setMobile(mobile);
		request.setEmail(email);
		request.setExternal_id(cardNumber);
		request.setFirstname(firstName);
		request.setLastname(lastName);

		setPayment(request, transaction);

		setLineitems(request, transaction);

	}

	private void setPayment(TransactionRequest request, MAXSaleReturnTransaction transaction) {
		List<Payment> paymentList = new ArrayList<Payment>();
		Payment payment;
		TenderLineItemIfc[] tenderitems = transaction.getTenderLineItems();
		if (tenderitems != null && tenderitems.length > 0) {

			for (TenderLineItemIfc tenderItem : tenderitems) {
				payment = new Payment();
				payment.setMode(tenderItem.getTypeCodeString());
				payment.setValue(tenderItem.getAmountTender().getStringValue());
				payment.setNotes(null);
				payment.setAttributes(null);
				paymentList.add(payment);

			}
		}
		request.setPayments(paymentList);

	}

	private void setLineitems(TransactionRequest request, MAXSaleReturnTransaction transaction) {
		Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

		List<LineItem> lineItemList = new ArrayList<LineItem>();
		LineItem lineItem = null;
		SaleReturnLineItemIfc[] linItems = (SaleReturnLineItemIfc[]) transaction.getItemContainerProxy().getLineItems();

		if (linItems != null && linItems.length > 0) {
			for (SaleReturnLineItemIfc itemIfc : linItems) {
				lineItem = new LineItem();
				lineItem.setSerial(itemIfc.getLineNumber());
				lineItem.setType(TYPE);
				/// changes for Rev 1.0 param value changes start
				lineItem.setAmount(itemIfc.getExtendedDiscountedSellingPrice()
						.subtract(itemIfc.getItemInclusiveTaxAmount()).getDoubleValue());
				lineItem.setDescription(itemIfc.getItemDescription(uiLocale));
				lineItem.setItem_code(itemIfc.getItemID());
				lineItem.setQty(itemIfc.getItemQuantity().doubleValue());
				lineItem.setRate(itemIfc.getPLUItem().getPrice().getDoubleValue());
				lineItem.setDiscount(itemIfc.getItemDiscountTotal().getDoubleValue());
				lineItem.setValue(itemIfc.getExtendedDiscountedSellingPrice()

						/// changes for Rev 1.0 param value changes end
						.getDoubleValue());
				lineItem.setOriginal_bill_number(null);
				lineItemList.add(lineItem);
			}
		}
		request.setLine_items(lineItemList);

	}

	/* to retrieve list of MAXMcouponIfc from TransactionResponse */
	private ArrayList<MAXMcouponIfc> getMcouponList(TransactionResponse response) {
		ArrayList<MAXMcouponIfc> listMcoupons = new ArrayList<MAXMcouponIfc>();
		if (response.getEffects() != null && response.getEffects().size() > 0) {
			MAXMcouponIfc mcoupon = null;
			for (Effect effect : response.getEffects()) {
				mcoupon = new MAXMcoupon();

				mcoupon.setCouponNumber(effect.getCoupon_code().trim());
				mcoupon.setCouponDescription(effect.getDescription().trim());
				mcoupon.setValidTill(effect.getValid_till().trim());

				listMcoupons.add(mcoupon);
			}
		}
		return listMcoupons;
	}

	private void displayCapillaryConnectionError(POSUIManagerIfc ui, String resourceID) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(resourceID);
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setArgs(null);

		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "ExitTender");

		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

	}

	private void displayCapillaryError(POSUIManagerIfc ui, String[] error) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("CapillaryResponseErrorScreen");
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setArgs(error);

		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "ExitTender");

		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

	}

}