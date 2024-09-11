/*
 * Rev 1.6  10th May 2021	Vidhya Kommareddi	POS BUG:Search using card number issue. Ashish missed some code. Added the same.
*  Rev 1.5	Nov 05, 2018	Ashish Yadav		  Changes for Edge
 * Rev 1.4	 17th Oct 2018		Jyoti Yadav			LS Edge Phase 2
 * Rev 1.3   9th August 2017 Vidhya Kommareddi
 * Local customer ID should always be inserted into AS_LY table even when LMR customer exists.
 * Rev 1.2   13th June 2017 Vidhya Kommareddi
 * Tic to work with local customer and layaway.
 * Rev 1.1  		11 DEC,2016 		Akhilesh kumar    changes to set the value of cusotmer id to blank in case cusotmer is not found in local db
 * Initial Draft	Akhilesh kumar     29/09/2016   Cardless loyalty CRM for Customer search request to CRM with Card Number 
 *
 */

package max.retail.stores.pos.services.customer.common;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerConstantsIfc;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.factory.MAXDomainObjectFactory;
import max.retail.stores.domain.transaction.MAXLayawayTransactionIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.utility.MAXConfigParametersIfc;
import max.retail.stores.domain.utility.MAXCustomerSearchCriteria;
import max.retail.stores.domain.utility.MAXCustomerSearchCriteriaIfc;
import max.retail.stores.domain.utility.MAXGSTUtility;
import max.retail.stores.pos.services.customer.edge.MAXCRMCustomerStatusUtility;
//import max.retail.stores.pos.services.customer.edge.MAXCRMCustomerStatusUtility;
import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import max.retail.stores.pos.services.customer.tic.MAXCRMSearchCustomer;
import max.retail.stores.pos.services.customer.tic.MAXWebCRMCustomerSearchUtility;
import max.retail.stores.pos.ui.MAXEdgeDialogScreensIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EmailAddress;
import oracle.retail.stores.domain.utility.Phone;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

import org.apache.log4j.Logger;

public class MAXLookupCustomerByCardNumberSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = 5606663030351415316L;

	protected static Logger logger = Logger
			.getLogger(max.retail.stores.pos.services.customer.common.MAXLookupCustomerByCardNumberSite.class);	
	private static Long CUST_ID_MIN_LIMIT = new Long("12500");
	
	/*Change for Rev 1.4: Start*/
	MAXConfigParametersIfc config = MAXGSTUtility.getConfigparameter();
	CustomerIfc customerIfc=null;
	boolean mailLetter = true;
	int maximumMatches = 0;
	/*Change for Rev 1.4: End*/

	@SuppressWarnings("deprecation")
	public void arrive(BusIfc bus) {
		String letterName = CommonLetterIfc.SUCCESS;
	
		MAXCustomerCargo cargo = (MAXCustomerCargo) bus.getCargo();
		/*Change for Rev 1.4: Start*/
		//boolean mailLetter = true;
		/*POSUIManagerIfc uiManager = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);*/
		/*Change for Rev 1.4: End*/
		//Changes starts for Rev 1.1 (Ashish :Edge)
				boolean isCRMCustomerFetched = false;
				String custID = ((MAXCustomer)cargo.getCustomer()).getCustomerID();
				//Changes ends for Rev 1.1 (Ashish :Edge)
		Vector tempPhones = cargo.getCustomer().getPhones();
			
		try {
			/*Change for Rev 1.4: Start*/
			//Changes starts for Rev 1.1 (Ashish : Edge)
		boolean edgePreview = MAXGSTUtility.edgePreviewSaleEnabled(cargo.getRegister().getBusinessDate(), config);
			if(edgePreview){
				//Request existing CRM API and then new CRM API to get edge customer status
				searchCustomerThroughExistingCRMCall(bus,edgePreview);
				if((!mailLetter)){
					//Changes starts for rev 1.1 (Ashish : Edge : sending cust ID extra)
					//searchCustomerInPosDB(bus,custID);
					searchCustomerInPosDB(bus,custID);
					//Changes starts for rev 1.1 (Ashish : Edge)
					if(!mailLetter){
						displayCRMResponseError(bus, "Customer Not Found",
								"CRMCustomersearchError", "promptTICAdd");
					}
				}
				else{
					isCRMCustomerFetched = true;
				}
				//Changes ends for Rev 1.1 (Ashish : Edge)
			}else{
				//Old Flow--> Lookp the customer in POS database and if not found then call existing CRM API for customer details
				//Changes starts for rev 1.1 (Ashish : Edge : sending cust ID extra)
				//searchCustomerInPosDB(bus,custID);
				searchCustomerInPosDB(bus,custID);
				//Changes starts for rev 1.1 (Ashish : Edge)
				if(customerIfc==null || (customerIfc!=null && (customerIfc.getFirstLastName()==null || (customerIfc.getFirstLastName()!=null && customerIfc.getFirstLastName().equalsIgnoreCase(""))))){
					//Changes starts for Rev 1.1 (Ashish : Edge)
					searchCustomerThroughExistingCRMCall(bus,edgePreview);
					isCRMCustomerFetched = true;
					//Changes ends for Rev 1.1 (Ashish : Edge)
				}
			}
			//Changes starts for Rev 1.1 (Ashish : Edge)
			/*Change for Rev 1.4: End*/
		} catch (Exception e) {
			logger.error("" + e + "");
			letterName = "failureByPhoneNumber";

		}
		cargo.getCustomer().setPhones(tempPhones);
		((MAXCustomerCargo) cargo).setTicCustomerPhoneNoFlag(false);
//Changes starts for Rev 1.1 (Ashish : Edge)
		if (mailLetter && !isCRMCustomerFetched) {
			//Changes ends for Rev 1.1 (Ashish : Edge)
			bus.mail(new Letter(letterName), BusIfc.CURRENT);
		}

		//re-initialise the variables
		customerIfc = null;
		maximumMatches = 0;
		mailLetter = true;

	}

        private void displayCRMCustomerInfo(String resourceID,
			POSUIManagerIfc uiManager, String letter,MAXTICCustomerIfc crmCustomer,MAXEdgeStatus status) {
		
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(resourceID);
		String msg[] = new String[15];
		
		
		UtilityIfc utility = null;
		try {
			utility = Utility.createInstance();
		} catch (ADOException adoe) {
			String message = "Configuration problem: could not instantiate UtilityIfc instance";
			logger.error(message, adoe);
			throw new RuntimeException(message, adoe);
		}
		
		String ticConvFactor = utility.getParameterValue("LoyaltyPointsConversionFactor", null);
		Double equivalentAmount = null;
		try {
			if (ticConvFactor != null) {
				DecimalFormat decimalFormat = new DecimalFormat("##.##");
				Double ticConvFactor1 = new Double(ticConvFactor);
				if (crmCustomer.getTICCustomerPoints() != null)
					equivalentAmount = new Double(decimalFormat.format(
							(new Double(crmCustomer.getTICCustomerPoints()) / ticConvFactor1.doubleValue()) * 100));
			}
		} catch (NumberFormatException exception) {
			equivalentAmount = null;
		}
		msg[0] ="N/A";
		if (crmCustomer.getTICCustomerPoints() != null) {
			msg[1] = String.valueOf(new BigDecimal(crmCustomer.getTICCustomerPoints()).setScale(2, BigDecimal.ROUND_HALF_UP));
		}
		
		msg[2] ="N/A";
		msg[3] = crmCustomer.getTICCustomerTier();
		msg[4] = "N/A";
		msg[5] = "N/A";
		if (equivalentAmount != null) {
			msg[6] = String.valueOf(equivalentAmount);
		}
		
		String customerName="";
		if(crmCustomer!=null && crmCustomer.getTICFirstName()!=null){
			customerName=crmCustomer.getTICFirstName();
		}else{
			customerName="N/A";
		}
		if(crmCustomer!=null && crmCustomer.getTICLastName()!=null && !crmCustomer.getTICLastName().equalsIgnoreCase("")){
			customerName= customerName+ " "+crmCustomer.getTICFirstName();
		}
		msg[7] = customerName;
		msg[8] = "";
		msg[9] = "";

		//Rev 1.6 start 
		msg[10] = status.getSbiCard();
		/*msg[11] = status.getLsEdge();
		msg[12] = status.getMaxEdge();
		msg[13] = status.getHcEdge();
		msg[14] = status.getMaxBlue();*/
		//Rev 1.6 end

		dialogModel.setArgs(msg);
		/*dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, letter);
		uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);*/
		//Changes starts for Rev 1.5 (Ashish : Edge)
		dialogModel.setType(MAXEdgeDialogScreensIfc.EDGE_CUSTOMER_INFORMATION);
		dialogModel.setButtonLetter(MAXEdgeDialogScreensIfc.CUSTOMER_INFORMATION, "CustInfo");
		dialogModel.setButtonLetter(MAXEdgeDialogScreensIfc.CUSTOMER_ENTER, letter);
		//Changes ends for Rev 1.5 (Ashish : Edge)
		uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

	}

        //Changes starts for rev 1.1 (Ashish : Edge)
    	public void searchCustomerInPosDB(BusIfc bus, String custID){
    		//Changes ends for rev 1.1 (Ashish : Edge)
			//boolean capApiAllowed = false;
			/*int maximumMatches = 0;*/
			MAXCustomerCargo cargo = (MAXCustomerCargo) bus.getCargo();
			CustomerIfc customer = cargo.getCustomer();
			CustomerManagerIfc customerManager = (CustomerManagerIfc) bus
					.getManager(CustomerManagerIfc.TYPE);
			UtilityManagerIfc utilityManager = (UtilityManagerIfc) bus
					.getManager(UtilityManagerIfc.TYPE);
			LocaleRequestor locale = utilityManager.getRequestLocales();
			customer.setLocaleRequestor(locale);
			MAXCustomerSearchCriteriaIfc criteria= new MAXCustomerSearchCriteria(
					SearchType.SEARCH_BY_CUSTOMER_ID, locale);
			Locale extendedDataRequestLocale = cargo.getOperator().getPreferredLocale();
			if (extendedDataRequestLocale == null) {
				extendedDataRequestLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
			}
			criteria.setExtendedDataRequestLocale(extendedDataRequestLocale);
			int maxCustomerItemsPerListSize = Integer.parseInt(Gateway
					.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP,
							"MaxCustomerItemsPerListSize", "10"));
			criteria.setMaxCustomerItemsPerListSize(maxCustomerItemsPerListSize);
			int maxTotalCustomerItemsSize = Integer.parseInt(Gateway
					.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP,
							"MaxTotalCustomerItemsSize", "40"));
			criteria.setMaxTotalCustomerItemsSize(maxTotalCustomerItemsSize);
			//Changes starts for Rev 1.1 (Ashish : Edge)
			criteria.setCustomerID(custID);
			//Changes ends for Rev 1.1 (Ashish : Edge)
			MAXCustomerUtilities.configureCustomerSearchCriteria(
					criteria, customer);
			int maxNumberCustomerGiftLists = Integer.parseInt(Gateway
					.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP,
							"MaxNumberCustomerGiftLists", "4"));
			criteria.setMaxNumberCustomerGiftLists(maxNumberCustomerGiftLists);
			criteria.setMaximumMatches(maximumMatches);

			try{
			customerIfc=customerManager.getCustomer(criteria);
			List<CustomerIfc> customerList=new ArrayList<CustomerIfc>();
			if(customerIfc!=null){
				customerList.add(customerIfc);
				//Changes starts for Rev 1.1 (Ashish : Edge)
				mailLetter = true;
				if(MAXGSTUtility.edgePreviewSaleEnabled(cargo.getRegister().getBusinessDate(), config)){
					if(bus.getCargo() instanceof MAXCustomerCargo){
						cargo.setCustLinkedThroughDB(true);
					}
					else if(bus.getCargo() instanceof MAXCustomerMainCargo){
						cargo.setCustLinkedThroughDB(true);
					}
				}
				//Changes ends for rev 1.1 (Ashish : Edge)
			}
			//Changes starts for rev 1.1 (Ashish : Edge : added else condition for using mail letter)
			else{
				mailLetter = false;
			}
			//Changes ends for rev 1.1 (Ashish : Edge)
			cargo.setCustomerList(customerList);
		}catch (DataException e) {
				logger.warn("" + e + "");
				cargo.setDataExceptionErrorCode(e.getErrorCode());
				mailLetter = false;
			}
	}
	//Changes starts for Rev 1.1 (Ashish: Edge)
	public void searchCustomerThroughExistingCRMCall(BusIfc bus, boolean edgePreview){
		//Changes ends for Rev 1.1 (Ashish: Edge)
		MAXCustomerCargo cargo = (MAXCustomerCargo) bus.getCargo();
		CustomerIfc customer = cargo.getCustomer();
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		MAXWebCRMCustomerSearchUtility customerSearchUtility= MAXWebCRMCustomerSearchUtility.getInstance();
		MAXCRMSearchCustomer searchCustomer=new MAXCRMSearchCustomer();
		if(customer!=null && customer.getCustomerID()!=null && !customer.getCustomerID().equalsIgnoreCase("")){
		searchCustomer.setCardNumber(customer.getCustomerID());
		}
		
		if(cargo!=null && cargo.getTransactionID()!=null){
			searchCustomer.setMessageId(cargo.getTransactionID());
		}
		
		// chnages for Rev 1.2 start
		// Rev 1.5 start
		if(!(cargo.getTransaction() instanceof MAXLayawayTransactionIfc))
		{
			customer.setCustomerID("");
		}
		//Rev 1.5 ends
		// chnages for Rev 1.2 end
		// chnages for Rev 1.1 end
		//Changes starts for Rev 1.1 (Ashish: Edge)
		customerSearchUtility.searchCRMCustomer(bus, searchCustomer);
		//Changes ends for Rev 1.1 (Ashish: Edge)
		
		if(searchCustomer.getResponse()!=null && searchCustomer.getResponse().trim().equalsIgnoreCase("S")){	
			MAXDomainObjectFactory domainFactory = (MAXDomainObjectFactory) DomainGateway
					.getFactory();
			
			MAXTICCustomerIfc crmCustomer = (MAXTICCustomerIfc) domainFactory
					.getTICCustomerInstance();
			
			crmCustomer.setTICCustomerID(searchCustomer.getResCardNumber());
			crmCustomer.setTICEmail(searchCustomer.getEmail());
			crmCustomer.setTICMobileNumber(searchCustomer.getResMobileNumber());
			crmCustomer.setTICFirstName(searchCustomer.getCustName());
			crmCustomer.setTICLastName("");
			
			crmCustomer.setTICCustomerTier(searchCustomer.getCustTier());
			crmCustomer.setTICPinNumber(searchCustomer.getPincode());
			crmCustomer.setTICCustomerPoints(searchCustomer.getPointBal());
			
		if(cargo.getCustomer()!=null && cargo.getCustomer() instanceof MAXCustomer){
			MAXCustomer maxCustomer=(MAXCustomer)cargo.getCustomer();
			maxCustomer.setMAXTICCustomer(crmCustomer);
			
			Long custID=0l;
			try{
			 custID=new Long(crmCustomer.getTICCustomerID());
			}catch(Exception e){
				
			}
			if (custID.compareTo(CUST_ID_MIN_LIMIT) > 0) {
				crmCustomer.setCustomerType(MAXCustomerConstantsIfc.CRM);
			}
			crmCustomer.setTicCustomerVisibleFlag(true);
			cargo.setCustomer(maxCustomer);
			
			//Rev 1.2 start
			// set transaction data start
			cargo.setCustomer(crmCustomer);
			if (cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
				((MAXSaleReturnTransaction) cargo.getTransaction()).setTicCustomerVisibleFlag(true);
				((MAXSaleReturnTransaction) cargo.getTransaction()).setMAXTICCustomer(crmCustomer);

			} else if (cargo.getTransaction() instanceof MAXLayawayTransactionIfc) {
				((MAXLayawayTransactionIfc) cargo.getTransaction()).setTicCustomerVisibleFlag(true);
				((MAXLayawayTransactionIfc) cargo.getTransaction()).setLSSIPLTICCustomer(crmCustomer);

			}
			// set transaction data end
			//Rev 1.2 end
			
			}
		
		
		//Rev 1.2 start
		// Change for Rev 1.4:Starts
		MAXCustomerIfc origCustomer = null;
		if (cargo.getOriginalCustomer() instanceof MAXCustomer)
			origCustomer = (MAXCustomerIfc) cargo.getOriginalCustomer();

		MAXCustomerIfc cargCustomer = (MAXCustomerIfc) cargo.getCustomer();

		if (origCustomer != null
				&& origCustomer.getCustomerType().equals(MAXCustomerConstantsIfc.LOCAL)
				&& cargCustomer.getCustomerType().equals(MAXCustomerConstantsIfc.LOCAL)
				&& origCustomer.getFirstLastName() != null
				&& !origCustomer.getFirstLastName().trim().equalsIgnoreCase("")) {

			origCustomer.setBalancePoint(cargCustomer.getBalancePoint());
			origCustomer.setOrigCustomerID(origCustomer.getCustomerID());
			origCustomer
					.setBalancePointLastUpdationDate(cargCustomer.getBalancePointLastUpdationDate());
			origCustomer.setBirthdate(cargCustomer.getBirthdate());
			origCustomer.setCustomerTier(cargCustomer.getCustomerTier());
			origCustomer.setBothLocalAndLoyaltyCustomerAttached(true);
			origCustomer.setCustomerType(cargCustomer.getCustomerType());
			origCustomer.setLoyaltyCardNumber(cargCustomer.getCustomerID());
			//Rev 1.3 start
			origCustomer.setCustomerID(origCustomer.getCustomerID());
			//origCustomer.setCustomerID(cargCustomer.getCustomerID());
			//Rev 1.3 end
			origCustomer.setLoyaltyCustomerFirstName(cargCustomer.getFirstName());
			
			//Rev 1.2 start
			origCustomer.setMAXTICCustomer(cargCustomer.getMAXTICCustomer());
			//Rev 1.2 end
			// origCustomer.setCustomerName(cargCustomer.getCustomerName());
			// origCustomer.setFirstName(cargCustomer.getFirstName());

			Vector phones = cargCustomer.getPhones();
			for (int i = 0; i < phones.size(); i++) {
				origCustomer.setLoyaltyCustomerPhone((Phone) phones.elementAt(i));
			}

			for (Iterator emailAddr = cargCustomer.getEmailAddresses(); emailAddr.hasNext();) {
				origCustomer.setLoyaltyCustomerEMail((EmailAddress) emailAddr.next());
			}

			cargo.setCustomer(origCustomer);
			cargCustomer = (MAXCustomerIfc) cargo.getCustomer();
		}
		// Change for Rev 1.4:Ends
			
		//Rev 1.2 end
		
			//bus.mail("CrmCustomerLinked",BusIfc.CURRENT);

		//Rev 1.6 start  

		// changes start here for edge renewal
		MAXCRMCustomerStatusUtility edgeValidateUtility= MAXCRMCustomerStatusUtility.getInstance();
		HashMap requestAttributes = new HashMap();
		requestAttributes = edgeValidateUtility.populateValidationHashMapForEdgeStatus(bus, requestAttributes);
		String urlParameters = edgeValidateUtility.createValidationURLForEdgeStatus(requestAttributes, bus);
		String URL = Gateway.getProperty("application", "GetCustEdgeDetails", null);
		logger.info("URL of validate Edge API is " + URL);
		logger.info("Request of validate Edge API is " + urlParameters);
		// Call CRM API
		String response = edgeValidateUtility.executeValidationPost(URL, urlParameters);
		logger.info("Response of validate Edge API is " + response);
		MAXEdgeStatus status = edgeValidateUtility.validateResp(response);
		logger.info("Enrole EDGE Flag Value is " + status.isEnrollFlag());
		cargo.setEnrollEdge(status.isEnrollFlag());
		//changes end here for edge renewal
		//Rev 1.6 end
		
			
			displayCRMCustomerInfo("LOYALTY_POINTS_DETAILS", uiManager,"CrmCustomerLinked", crmCustomer,status);
			
			//Changes starts for Rev 1.1 (Ashish : Edge : orignal mailLetter was "false"...now put the condition)
			if(searchCustomer.getResponse().equalsIgnoreCase("S")){
				mailLetter = true;
			}
			else{
			mailLetter = false;
			}
			//Changes ends for Rev 1.1 (Ashish : Edge)
			
		}else{
			if(MAXGSTUtility.edgePreviewSaleEnabled(cargo.getRegister().getBusinessDate(), config)){
				cargo.setDataExceptionErrorCode(DataException.NO_DATA);
			}
			mailLetter = false;
		}
	}	
	//Changes starts for Rev 1.1 (Ashish : Edge)
		protected void displayCRMResponseError(BusIfc bus, String errorMessage,
				String resourceId, String letterName) {
			POSUIManagerIfc ui = (POSUIManagerIfc) bus
					.getManager(UIManagerIfc.TYPE);
			DialogBeanModel beanModel = new DialogBeanModel();
			String[] messgArray = new String[1];
			messgArray[0] = errorMessage;
			beanModel.setArgs(messgArray);
			beanModel.setResourceID(resourceId);
			beanModel.setType(DialogScreensIfc.ERROR);
			beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, letterName);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
		}
		//Changes ends for Rev 1.1 (Ashish : Edge)

}
