/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 

 *  Rev 1.1  06/June/2013                Prateek                                       Changes for Bug 5984
 *  Rev 1.0  11/April/2013               Izhar                                       MAX-POS-Customer-FES_v1.2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.customer.common;

// java imports
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import max.retail.stores.domain.arts.MAXConfigParameterTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerConstantsIfc;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.factory.MAXDomainObjectFactory;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXLayawayTransactionIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.utility.MAXConfigParametersIfc;
import max.retail.stores.domain.utility.MAXGSTUtility;
import max.retail.stores.pos.services.capillary.MAXCapillaryCustomer;
import max.retail.stores.pos.services.capillary.MAXCapillaryHelperUtility;
import max.retail.stores.pos.services.customer.tic.MAXCRMSearchCustomer;
import max.retail.stores.pos.services.customer.tic.MAXWebCRMCustomerSearchUtility;
import max.retail.stores.pos.services.tender.ewallet.MAXEWalletHelperUtiltiy;
import max.retail.stores.pos.ui.beans.MAXCustomerInfoBeanModel;
import max.retail.stores.pos.ui.beans.MAXDialogBeanModel;
import oracle.retail.stores.common.utility.ResultList;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.Address;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.Phone;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.Bus;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

//--------------------------------------------------------------------------
/**
 * Displays Customer Contact screen.
 * <p>
 * 
 * @version $Revision: 3$
 **/
// --------------------------------------------------------------------------
// public class CustomerContactSite extends EnterCustomerInfoSite
public class MAXCustomerContactSite extends MAXEnterCustomerInfoSite {
	/**
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
	
	//MAXConfigParametersIfc config = MAXGSTUtility.getConfigparameter();

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
				if (customer.getCustomerType().equalsIgnoreCase(MAXCustomerConstantsIfc.CRM)) {

					MAXDialogBeanModel dialogModel = new MAXDialogBeanModel();
					EYSDate lastvisitdate = customer
							.getBalancePointLastUpdationDate();

					// Below changes made by aks for getting the wallet details
					String eWalletBalance = null;
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
						// requestType =ateway.getProperty("application",
							//	"RequestType", "");

						 phoneNumber = customer.getPrimaryPhone().getPhoneNumber().toString();
						 
						String wallet = MAXEWalletHelperUtiltiy
								.getEWalletDetails(targetURL, storeCode,
										phoneNumber, workstationID, channel,
										walletOwner, requestId, requestType );
					//	System.out.println("219MAXCustomer :");
						System.out.println("Wallet Details " + wallet);
						logger.info("AKS: GetEWallet API Response \n" + wallet.toString());
						JSONParser parser = new JSONParser();
						JSONObject json = (JSONObject) parser.parse(wallet);
						Map ewalletresponseMap = (HashMap) json.get("responseHeader");
						String responseCode = ewalletresponseMap.get("responseCode").toString();
						if (responseCode.equalsIgnoreCase("SUCCESS")) {
							@SuppressWarnings("rawtypes")
							Map walletMap = (HashMap) json.get("walletDetails");
							eWalletBalance = walletMap.get(
									"totalWalletOwnerBalance").toString();
							eWallettraceId = ewalletresponseMap.get("traceId")
									.toString();
						//	transactionID = walletMap.get("transactionID").toString();


						} else {
							
							eWalletBalance = ewalletresponseMap.get("responseCode")
									.toString();
						}
					//	System.out.println("ewallet balance 238:" + eWalletBalance);

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
						
					
					}

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
					args[6] = getEquivalentAmount(bus,customer.getBalancePoint())+ "";
					//System.out.println("lmrPointBal :"+customer.getBalancePoint());
					args[7] = customer.getCustomerName();
					//System.out.println("272 :"+ eWalletBalance);
					EYSDate birthdate = customer.getBirthdate();
					if (birthdate != null)
					{
						EYSDate date = new EYSDate();
						int birthMonth = birthdate.getMonth();
						int month = date.getMonth();
						if (birthMonth == month)
						{
							int birthDay = birthdate.getDay();
							int currentDay = date.getDay();
							
							/*
							 * try { //System.out.println("Inside try block"); Integer birthPeriod = pm
							 * .getIntegerValue("FlashCustomerBirthdayMessagePeriod"); int remainingDay =
							 * Math.abs(currentDay - birthDay);
							 * 
							 * if (remainingDay <= birthPeriod.intValue()) { String birthDayMessage = pm
							 * .getStringValue("CustomerBirthdayMessage");
							 * 
							 * // MAX Changes Rev 1.1 Start String birthdayString = util.retrieveText(
							 * "CustomerMasterSpec", "customerText", "BirthdayMessage", "BirthdayMessage");
							 * 
							 * args[8] = birthdayString; args[9] = birthDayMessage; } else { args[8] = "";
							 * args[9] = ""; }} catch (ParameterException e) { logger.error(""
							 * +e.getMessage() + ""); }
							 */
							 
							// MAX Changes Rev 1.1 End
							// below condition added by atul for Ewallet
					}
					
					
						else if(eWalletBalance !=null && eWalletBalance.length() > 0)
						{
							//System.out.println("315 inside ifelse :"+ eWalletBalance);
							if(!(eWalletBalance.equalsIgnoreCase("0"))) {
							args[8] = eWalletBalance;
							}
							else {
								args[8]="Available Wallet balance for customer is 0. Ask customer: if they want to add amount to Wallet, or proceed with other payment modes";
							}
							args[9] = "";
							//System.out.println("eWalletBalance" + eWalletBalance);
						}}
						else 
						{
							//System.out.println("324 :"+ eWalletBalance);
							args[14] = eWalletBalance;
							args[8] = "";
							args[9] = "";
							//System.out.println("eWalletBalance" + eWalletBalance);
						}
						/*else {
							args[8] = "";
							args[9] = "";
						} */
					
				//	System.out.println("338 :"+ eWalletBalance);
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
					
					
					args[11] = getSBIPointBalAndRewards(bus, "rewardPoints");
					args[10] = getSBIPointBalAndRewards(bus, "sbiPointBal");
					 

				//	args[14] = eWalletBalance;
				//	System.out.println("eWalletBalance" + eWalletBalance);
					dialogModel.setResourceID("LoyaltyPointsDetails");
					dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					dialogModel.setArgs(args);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Link");
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
	
	private String getSBIPointBalAndRewards(BusIfc bus, String rewardOrSbiPointBal) {
		String amount=null;
		
		MAXCustomerCargo cargo = (MAXCustomerCargo)bus.getCargo();
		MAXConfigParametersIfc configParam = getAllConfigparameter();
		//boolean pointConFlag = configParam.isSbiPointConversion();
		//int sbiMinPoint = configParam.getSbiMinPoint();
		//int conversionRate = configParam.getSbiPointConversionRate();
		int sbiMinPoint=0 ,conversionRate=0;
		String pointConFlag = null;
		try {
			
			ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus
					.getManager(ParameterManagerIfc.TYPE);
			
			sbiMinPoint = parameterManager.getIntegerValue("SbiMinPoint");
			conversionRate = parameterManager.getIntegerValue("SbiPointConversionRate");
			pointConFlag = parameterManager.getStringValue("isSbiPointConversion");
			}
			catch(Exception e)
			{
				logger.info(e);
			}
		double totalPoints = 0;
		MAXCustomer customer=null;
		if (cargo.getCustomer()!=null && cargo.getCustomer() instanceof MAXCustomerIfc) {
		 customer = (MAXCustomer)cargo.getCustomer();
		}
		getSbiPointsByCRM(bus, customer);
		/*
		 * if(cargo.getTransaction() instanceof MAXSaleReturnTransaction) { customer =
		 * (MAXCustomer) ((MAXSaleReturnTransaction)
		 * cargo.getTransaction()).getCustomer(); //((MAXSaleReturnTransaction)
		 * cargo.getTransaction()).setSbiRewardredeemFlag(true); }else
		 * if(cargo.getTransaction() instanceof MAXLayawayTransaction) { customer =
		 * (MAXCustomer) ((MAXLayawayTransaction) cargo.getTransaction()).getCustomer();
		 * //((MAXLayawayTransaction)
		 * cargo.getTransaction()).setSbiRewardredeemFlag(true); }
		 */
		
		//totalPoints =200;  // need to comment before delivery
		if(customer != null &&  (customer.getSbiPointBal()) != null) {
		//if(customer != null && customer.getMAXTICCustomer() != null && ((MAXTICCustomer) customer.getMAXTICCustomer()).getSbiPointBal() != null) {
			//String sbiPoints = ((MAXTICCustomer) customer.getMAXTICCustomer()).getSbiPointBal();
			String sbiPoints =  customer.getSbiPointBal();
			totalPoints = Double.parseDouble(sbiPoints);
		}
		
		//if(pointConFlag && totalPoints >= sbiMinPoint && (cargo.getTransaction() instanceof MAXSaleReturnTransaction|| cargo.getTransaction() instanceof MAXLayawayTransaction)){
			if(pointConFlag.equals("Y") && totalPoints >= sbiMinPoint && rewardOrSbiPointBal.equalsIgnoreCase("rewardPoints")){
			//int amount = (int)Math.round((double)totalPoints/conversionRate);
			DecimalFormat decimalFormat = new DecimalFormat("##.##");
			//amount= 
			return decimalFormat.format(totalPoints/conversionRate);
		
		}
        if(rewardOrSbiPointBal.equalsIgnoreCase("sbiPointBal")) {
        	return customer.getSbiPointBal();
		}
		//return amount;
		//return "Default SBI Amount";
        return customer.getSbiPointBal();
	}

	private void getSbiPointsByCRM(BusIfc bus, MAXCustomer maxCustomer) {
		String isSbiPointConversionflag = null;
		try {
			
			ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus
					.getManager(ParameterManagerIfc.TYPE);
			
			isSbiPointConversionflag= parameterManager.getStringValue("isSbiPointConversion");
			}
			catch(Exception e)
			{
				logger.info(e);
			}
			if(isSbiPointConversionflag.equals("Y")){
			searchCustomerThroughExistingCRMCall(bus, maxCustomer);
			/*
			 * if((resultList==null || (resultList!=null && resultList.getList()==null) ||
			 * (resultList!=null && resultList.getList()!=null &&
			 * resultList.getList().size()==0)) && (!mailLetter)){
			 * searchCustomerInPosDB(bus, mailLetter); if(!mailLetter){
			 * displayCRMResponseError(bus, "Customer Not Found", "CRMCustomersearchError",
			 * "promptTICAdd"); } }else { isCRMCustomerFetched = true; }
			 */
		//	System.out.println("searchCustomerThroughExistingCRMCall"+maxCustomer.getSbiPointBal());
		}
		
	}

	public BigDecimal getEquivalentAmount(BusIfc bus, BigDecimal points) {
		ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
		BigDecimal equivalentAmount = null;
		String conversionFactor;
		try {
			conversionFactor = parameterManager
					.getStringValue("LoyaltyPointConversionRate");
			//System.out.println("conversionFactor"+conversionFactor);

		} catch (Exception e) {
			conversionFactor = "166.00";
			logger.error(
					"Error While getting value for parameter(LoyaltyPointConversionRate)",e);
		}
		BigDecimal cRate = new BigDecimal(conversionFactor);
		//System.out.println("cRate"+cRate);
		if(points!=null) {
		equivalentAmount = points.multiply(new BigDecimal("0.60"));
		//System.out.println("points.multiply(new BigDecimal(\"100.00\"));"+equivalentAmount);
		//equivalentAmount = equivalentAmount.divide(cRate, 0);
		//System.out.println("equivalentAmount.divide(cRate, 0)"+equivalentAmount);
		}
		//System.out.println("equivalentAmount"+equivalentAmount);
		return equivalentAmount;
	}
	
	private MAXConfigParametersIfc getAllConfigparameter() {

		MAXConfigParameterTransaction configTransaction = new MAXConfigParameterTransaction();
		MAXConfigParametersIfc configParameters = null;
		configTransaction = (MAXConfigParameterTransaction) DataTransactionFactory
				.create(MAXDataTransactionKeys.CONFIG_PARAMETER_TRANSACTION);

		try {
			configParameters = configTransaction.selectConfigParameters();
		} catch (DataException e1) {
			//e1.printStackTrace();
			logger.error(e1.getMessage());
		}
		return configParameters;
	}
	
	public void searchCustomerThroughExistingCRMCall(BusIfc bus, MAXCustomer maxCustomer){
		MAXCustomerCargo cargo = (MAXCustomerCargo) bus.getCargo();
		CustomerIfc customer = cargo.getCustomer();
		//POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		boolean dualDisplayEnabled = Gateway.getBooleanProperty("application", "DualDisplayEnabled", false);
		
		/*Rev 1.3 start*/
		//cargo.setTicCustomerPhoneNo(null);
		/*Rev 1.3 end*/
		////request 
		MAXWebCRMCustomerSearchUtility customerSearchUtility= MAXWebCRMCustomerSearchUtility.getInstance();
		MAXCRMSearchCustomer searchCustomer=new MAXCRMSearchCustomer();
		if(customer!=null && customer.getPrimaryPhone()!=null && customer.getPrimaryPhone().getPhoneNumber()!=null){
		searchCustomer.setMobileNumber(customer.getPrimaryPhone().getPhoneNumber());
		}
		if(cargo!=null && cargo.getTransactionID()!=null){
			searchCustomer.setMessageId(cargo.getTransactionID());
		}
		
		customerSearchUtility.searchCRMCustomer(bus, searchCustomer);
		
			if(searchCustomer.getResponse()!=null && searchCustomer.getResponse().trim().equalsIgnoreCase("S")){
			MAXDomainObjectFactory domainFactory = (MAXDomainObjectFactory) DomainGateway
					.getFactory();
			
			//MAXTICCustomerIfc crmCustomer = (MAXTICCustomerIfc) domainFactory.getTICCustomerInstance();
			
		
			
			Long custID=0l;
			try{
			 custID=new Long(searchCustomer.getResCardNumber());
			}catch(Exception e){
				
			}
			//lsiplCustomer.setTicCustomerVisibleFlag(true);
			maxCustomer.setBalancePoint(new BigDecimal(searchCustomer.getPointBal()));
			maxCustomer.setSbiPointBal(searchCustomer.getSbiPointBal());
		//	maxCustomer.setSbiPointBal("1000");
		//	maxCustomer.setPinNumber(searchCustomer.getPincode());
		//	maxCustomer.setCustomerPoints(searchCustomer.getPointBal());
			
			
			
}
	}
}
