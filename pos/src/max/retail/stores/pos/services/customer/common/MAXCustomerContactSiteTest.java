package max.retail.stores.pos.services.customer.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import max.retail.stores.domain.customer.MAXCustomerConstantsIfc;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.pos.services.capillary.MAXCapillaryCustomer;
import max.retail.stores.pos.services.capillary.MAXCapillaryHelperUtility;
import max.retail.stores.pos.services.tender.ewallet.MAXEWalletHelperUtiltiy;
import max.retail.stores.pos.ui.beans.MAXCustomerInfoBeanModel;
import max.retail.stores.pos.ui.beans.MAXDialogBeanModel;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.Address;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

public class MAXCustomerContactSiteTest extends MAXCustomerContactSite {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * revision number
	 **/
	public static final String revisionNumber = "$Revision: 3$";

	/**
	 * Constants for done action name
	 * 
	 * @deprecated as of release 6.0 replaced by CommonLetterIfc.DONE
	 **/
	public static final String DONE = CommonLetterIfc.DONE;
	/**
	 * Constants for link action name
	 * 
	 * @deprecated as of release 6.0 replaced by CommonLetterIfc.LINK
	 **/
	public static final String LINK = CommonLetterIfc.LINK;

	// ----------------------------------------------------------------------
	/**
	 * Displays the Customer Contact screen.
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------

	public void arrive(BusIfc bus) {

		// get the cargo for the service
		MAXCustomerCargo cargo = (MAXCustomerCargo) bus.getCargo();
		ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
		UtilityManagerIfc util = (UtilityManagerIfc) bus
				.getManager(UtilityManagerIfc.TYPE);
		/*
		 * for tic customer change akhilesh when the page is called during
		 * customer search by phone number during start of transaction start
		 */

		boolean invalidCustomerDataFlag = true;

		CustomerIfc customerIfc = (MAXCustomerIfc) cargo.getCustomer();

		if (cargo.isTicCustomerPhoneNoFlag()) {
			// /for the
			try {
				AddressIfc address = (AddressIfc) cargo
						.getCustomer()
						.getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_HOME);

				if (address == null) {
					address = new Address();
					address.setAddressType(AddressConstantsIfc.ADDRESS_TYPE_HOME);
					address.setPostalCode("000000");
					address.setState("OT");
					address.setCountry("IN");
					Vector addVec = new Vector();
					addVec.add(address);
					customerIfc.setAddresses(addVec);
					cargo.setCustomer(customerIfc);
				}

				if (address.getState() == null
						|| (address.getState() != null && (address.getState()
								.equalsIgnoreCase("") || address.getState()
								.equalsIgnoreCase("SE")))) {
					address.setState("OT");
				}

				if (address.getPostalCode() == null
						|| (address.getPostalCode() != null && address
								.getPostalCode().equalsIgnoreCase(""))) {
					address.setPostalCode("000000");
				}

				String postalString = address.validatePostalCode(
						address.getPostalCode(), address.getCountry());

				// save formatted postal code that was returned from the
				// validation method
				address.setPostalCode(postalString);

			} catch (Exception e) {
				invalidCustomerDataFlag = false;
			}
		}

		if (cargo.isTicCustomerPhoneNoFlag() && invalidCustomerDataFlag) {
			// MAX Chanhges for TIC customer By Manpreet:Start
			// Commented Start by arif write code on maxlinkordonesite for
			// display loyalty point details
			if (cargo.getCustomer() instanceof MAXCustomerIfc) {
				MAXCustomerIfc customer = (MAXCustomerIfc) cargo.getCustomer();

				if (customer.getCustomerType().equalsIgnoreCase(
						MAXCustomerConstantsIfc.CRM)) {
					

					MAXDialogBeanModel dialogModel = new MAXDialogBeanModel();
					EYSDate lastvisitdate = customer
							.getBalancePointLastUpdationDate();
					String eWalletBalance = null;
                     if(!cargo.isCustomerCRMsearch()) {
					// Below changes made by aks for getting the wallet details
					
					String eWallettraceId = null;
					String targetURL=null;
					String storeCode=null;
					String workstationID=null;
					String channel=null;
					String walletOwner=null;
					String requestId=null;
					String requestType=null;
					String phoneNumber=null; 
					try {
						 targetURL = Gateway.getProperty("application",
								"EwalletURLForgettingBalance", "");
						 storeCode = Gateway.getProperty("application",
								"StoreID", "");
						 workstationID = Gateway.getProperty(
								"application", "WorkstationID", "");
						// channel = Gateway.getProperty("application",
							//	"Channel", "");
					//	 walletOwner = Gateway.getProperty("application",
						//		"WalletOwner", "");
					//	 requestId = Gateway.getProperty("application",
						//		"RequestId", "");
						// requestType = Gateway.getProperty("application",
							//	"RequestType", "");

						 phoneNumber = customer.getPrimaryPhone().getPhoneNumber().toString();
						String wallet = MAXEWalletHelperUtiltiy.getEWalletDetails(targetURL, storeCode,phoneNumber, workstationID, channel,walletOwner, requestId, requestType);
						System.out.println("Wallet Details " + wallet);
						logger.info("AKS: GetEWallet API Response \n" + wallet.toString());
						JSONParser parser = new JSONParser();
						JSONObject json = (JSONObject) parser.parse(wallet);
						Map ewalletresponseMap = (HashMap) json.get("responseHeader");
						String responseCode = ewalletresponseMap.get("responseCode")
								.toString();

						if (responseCode.equalsIgnoreCase("SUCCESS")) {
							@SuppressWarnings("rawtypes")
							Map walletMap = (HashMap) json.get("walletDetails");
							eWalletBalance = walletMap.get(
									"totalWalletOwnerBalance").toString();
							eWallettraceId = ewalletresponseMap.get("traceId")
									.toString();

						} else {
							
							eWalletBalance = ewalletresponseMap.get("responseCode")
									.toString();
						}
						System.out.println("ewallet balance" + eWalletBalance);

						logger.info("AKS: EWallet Balance \n" + wallet.toString());

					} catch (Exception e7) {
						e7.printStackTrace();
						eWalletBalance="error Occurred";
					}
					
					if (customer instanceof MAXCustomerIfc) {
						customer.setLMREWalletTraceId(eWallettraceId);
						
						customer.setLMREWalletCustomerFlag(true);
						((MAXCustomerIfc)customer).setLMREWalletCustomerFlag(true);
						cargo.setCustomer(customer);
						((MAXCustomerCargo) cargo).setCustomer(customer);
						
					
					}}

					// args[14]="eWalletBalance";

					// String args[] =new String[14];
					String args[] = new String[15];
					if (lastvisitdate != null) {
						args[0] = lastvisitdate.toFormattedString("dd/MM/yyyy");
					}
					args[1] = " " + customer.getBalancePoint() + "";
					args[2] = customer.getPointsExpiringNextMonth() + "";
					args[3] = customer.getCustomerTier();
					args[4] = "false";
					args[5] = "false";
					args[6] = getEquivalentAmount(bus,
							customer.getBalancePoint())
							+ "";
					args[7] = customer.getCustomerName();
                     

					EYSDate birthdate = customer.getBirthdate();
					if (birthdate != null) {
						EYSDate date = new EYSDate();
						int birthMonth = birthdate.getMonth();
						int month = date.getMonth();
						if (birthMonth == month) {
							int birthDay = birthdate.getDay();
							int currentDay = date.getDay();
							try {
								Integer birthPeriod = pm
										.getIntegerValue("FlashCustomerBirthdayMessagePeriod");
								int remainingDay = Math.abs(currentDay
										- birthDay);

								if (remainingDay <= birthPeriod.intValue()) {
									String birthDayMessage = pm
											.getStringValue("CustomerBirthdayMessage");

									// MAX Changes Rev 1.1 Start
									String birthdayString = util.retrieveText(
											"CustomerMasterSpec",
											"customerText", "BirthdayMessage",
											"BirthdayMessage");

									args[8] = birthdayString;
									args[9] = birthDayMessage;
								} else {
									args[8] = "";
									args[9] = "";
								}
							} catch (ParameterException e) {
								logger.error("" + e.getMessage() + "");
							}
							// MAX Changes Rev 1.1 End
						} else {
							args[8] = "";
							args[9] = "";
						}
					} else {
						args[8] = "";
						args[9] = "";
					}

					MAXCapillaryCustomer capCustomer = new MAXCapillaryCustomer();

					MAXCapillaryHelperUtility cust = new MAXCapillaryHelperUtility();
					String custid = customer.getCustomerID();
					ArrayList customerList = null;
					HashMap request = new HashMap();
					HashMap responseMap = new HashMap();

					request.put("Customer Card Number", custid);

					MAXCustomerIfc[] capCustomerArray = null;

					try {
						responseMap = cust.lookup(request, responseMap); /*
																		 * Request
																		 * send
																		 * to
																		 * Capillary
																		 */

					} catch (Exception e) {
						logger.error(e);
					}
					String ItemStatusCode = "";
					String ConnResponseCode = responseMap.get("Response Code")
							.toString();
					if (responseMap.get("ItemStatusCode") != null) {
						ItemStatusCode = responseMap.get("ItemStatusCode")
								.toString();
					}
					if (ConnResponseCode.equals("200")) {

						if (ItemStatusCode.equals("1000"))

						{
							customerList = (ArrayList) responseMap
									.get("Customers");
							capCustomerArray = new MAXCustomerIfc[customerList
									.size()];
							Iterator custItr = customerList.iterator();
							int custCount = 0;

							while (custItr.hasNext()) {
								capCustomer = (MAXCapillaryCustomer) custItr
										.next();
								custCount++;
							}

						} else {
							args[10] = "";
							args[11] = "";
							args[12] = "";
							args[13] = "";
						}
						ArrayList offers = capCustomer.getOffers();
						if (offers == null) {
							offers = new ArrayList();
						}
						if (!offers.isEmpty()) {
							try {
								int s = 10;
								for (int i = 0; i < offers.size(); i++) {
									if (s <= 12) {

										args[s] = ("Offer" + (i + 1)) + ":"
												+ (String) offers.get(i);
										s++;
									}
								}
								if (offers.size() > 3) {
									args[13] = "";
									// commented after client discussion by Arif
									// args[13]="And more";
								} else {
									args[13] = "";
								}
								if (args[10] == null) {
									args[10] = "";
								}
								if (args[11] == null) {
									args[11] = "";
								}
								if (args[12] == null) {
									args[12] = "";
								}
							} catch (Exception e) {

							}

						} else {
							args[10] = "";
							args[11] = "";
							args[12] = "";
							args[13] = "";

						}
					} else {

						args[10] = "";
						args[11] = "";
						args[12] = "";
						args[13] = "";

					}
                      if(!cargo.isCustomerCRMsearch()) {
					args[14] = eWalletBalance;
					System.out.println("eWalletBalance" + eWalletBalance);
                      }
					dialogModel.setResourceID("LoyaltyPointsDetails");
					dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					dialogModel.setArgs(args);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
							"Link");
					POSUIManagerIfc ui = (POSUIManagerIfc) bus
							.getManager(UIManagerIfc.TYPE);
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					return;
				
				}
			}

			// MAX Chanhges for TIC customer By Manpreet:End

			bus.mail("Link", BusIfc.CURRENT);
		} else {

			/*
			 * for tic customer change akhilesh when the page is called during
			 * customer search by phone number during start of transaction End
			 */

			// instantiate the bean model for the UI bean
			// MAX Rev 1.0 Change : Start
			MAXCustomerInfoBeanModel model = (MAXCustomerInfoBeanModel) getCustomerInfoBeanModel(bus);
			String typeTICOrPOS = ((MAXCustomerIfc) cargo.getCustomer())
					.getCustomerType();
			model.setTypeTICOrPOS(typeTICOrPOS);
			// model.setTypeTICOrPOS(typeTICOrPOS);
			// MAX Rev 1.0 Change : end
			// set the link done switch
			int linkOrDone = cargo.getLinkDoneSwitch();
			model.setLinkDoneSwitch(linkOrDone);

			NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();

			if (linkOrDone == CustomerMainCargo.LINKANDDONE) {
				// enable done
				nModel.setButtonEnabled(CommonLetterIfc.DONE, true);

				// enable link
				nModel.setButtonEnabled(CommonLetterIfc.LINK, true);
			}
			if (linkOrDone == CustomerMainCargo.LINK) {
				// disable done
				nModel.setButtonEnabled(CommonLetterIfc.DONE, false);

				// enable link
				nModel.setButtonEnabled(CommonLetterIfc.LINK, true);
			}
			if (linkOrDone == CustomerMainCargo.DONE) {
				// disable Link
				nModel.setButtonEnabled(CommonLetterIfc.LINK, false);

				// enable done
				nModel.setButtonEnabled(CommonLetterIfc.DONE, true);
			}

			model.setLocalButtonBeanModel(nModel);

			// Check if History button should be enabled
			if (cargo.isHistoryModeEnabled()) {
				nModel.setButtonEnabled(CustomerCargo.HISTORY, true);
			} else {
				nModel.setButtonEnabled(CustomerCargo.HISTORY, false);
			}

			// Display customer if linked
			cargo.displayCustomer(bus);
			model.setEditableFields(true);

			// setup default phone type
			if (!model.isBusinessCustomer()) {
				model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_HOME);
			}

			// display the UI screen
			POSUIManagerIfc ui = (POSUIManagerIfc) bus
					.getManager(UIManagerIfc.TYPE);
			ui.showScreen(POSUIManagerIfc.CUSTOMER_INFO, model);
		}
	}

	/** MAX Rev 1.1 Change: Start **/
	public void depart(BusIfc bus) {

		// akhilesh changes tic customer start
		MAXCustomerCargo cargo = (MAXCustomerCargo) bus.getCargo();
		boolean invalidCustomerDataFlag = true;

		if (cargo.isTicCustomerPhoneNoFlag()) {
			try {
				AddressIfc address = (AddressIfc) cargo
						.getCustomer()
						.getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_HOME);

				String postalString = address.validatePostalCode(
						address.getPostalCode(), address.getCountry());

				// save formatted postal code that was returned from the
				// validation method
				address.setPostalCode(postalString);

			} catch (Exception e) {
				invalidCustomerDataFlag = false;
			}
		}

		// akhilesh changes tic customer END

		// If sent letter is not Cancel or Undo
		// save data from screen to cargo
		if (!CommonLetterIfc.CANCEL.equals(bus.getCurrentLetter().getName())
				&& !CommonLetterIfc.UNDO.equals(bus.getCurrentLetter()
						.getName())
				&& !(cargo.isTicCustomerPhoneNoFlag() && invalidCustomerDataFlag)) {

			POSUIManagerIfc ui = (POSUIManagerIfc) bus
					.getManager(UIManagerIfc.TYPE);
			MAXCustomerInfoBeanModel model = (MAXCustomerInfoBeanModel) ui
					.getModel(POSUIManagerIfc.CUSTOMER_INFO);

			// CustomerCargo cargo = (CustomerCargo)bus.getCargo();
			CustomerIfc customer = cargo.getCustomer();
			cargo.setOriginalCustomer(customer);

			CustomerIfc newCustomer = MAXCustomerUtilities.updateCustomer(
					customer, model);
			int index = model.getSelectedCustomerGroupIndex();
			cargo.setSelectedCustomerGroup(index);
			// update the customer from the model
			cargo.setCustomer(newCustomer);

			// set dialog name ahead of customer lookup
			cargo.setDialogName(CustomerCargo.TOO_MANY_CUSTOMERS); // handle
																	// possible
																	// change in
																	// customer
																	// group
		}
	}

	/** MAX Rev 1.1 Change: End **/

	public BigDecimal getEquivalentAmount(BusIfc bus, BigDecimal points) {
		ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		String conversionFactor;
		try {
			conversionFactor = parameterManager
					.getStringValue("LoyaltyPointConversionRate");

		} catch (Exception e) {
			conversionFactor = "166.00";
			logger.error(
					"Error While getting value for parameter(LoyaltyPointConversionRate)",
					e);
		}
		BigDecimal cRate = new BigDecimal(conversionFactor);
		BigDecimal equivalentAmount = points.multiply(new BigDecimal("100.00"));
		equivalentAmount = equivalentAmount.divide(cRate, 0);

		return equivalentAmount;
	}}
