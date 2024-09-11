package max.retail.stores.pos.services.customer.common;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

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
import max.retail.stores.domain.utility.MAXCustomerSearchCriteria;
import max.retail.stores.domain.utility.MAXCustomerSearchCriteriaIfc;
import max.retail.stores.domain.utility.MAXGSTUtility;
import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import max.retail.stores.pos.services.customer.tic.MAXCRMSearchCustomer;
import max.retail.stores.pos.services.customer.tic.MAXWebCRMCustomerSearchUtility;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.ResultList;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.Phone;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXLookupCustomerByPhoneSite extends PosSiteActionAdapter {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	/**
	 * 
	 * revision number
	 **/
	//public static final String revisionNumber = "$Revision: 1.2 $";
	
	MAXConfigParametersIfc config = MAXGSTUtility.getConfigparameter();
	ResultList resultList =null;
	int maximumMatches = 0;
	boolean mailLetter = true;
	private static Long CUST_ID_MIN_LIMIT = new Long("12500");
	
	// ----------------------------------------------------------------------
	/**
	 * Sends a customer lookup inquiry to the database manager.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		String letterName = CommonLetterIfc.SUCCESS;
		boolean mailLetter = true;
		boolean isLocalCustomerEnabled = false;
		MAXCustomerCargo cargo = (MAXCustomerCargo) bus.getCargo();
		CustomerIfc customer = cargo.getCustomer();
		// exclude phones from the search query
		//System.out.println("Arrive Method value()"+customer.getCustomerName());
		Vector<PhoneIfc> tempPhones = cargo.getCustomer().getPhones();
		
		MAXWebCRMCustomerSearchUtility customerSearchUtility = MAXWebCRMCustomerSearchUtility.getInstance();
		boolean isCRMCustomerFetched = false;
		
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        try
        {
         	isLocalCustomerEnabled = pm.getBooleanValue("IsLocalSearchCustomerEnabled").booleanValue();
        }
        catch (ParameterException e)
        {
          logger.warn("IsLocalCustomerEnabled parameter does not exist");
          e.printStackTrace();
        }
		
		// attempt to do the database lookup
		//Changes for Rev 1.0 : Starts
		try {
			
			 CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
	            UtilityManagerIfc utilityManager = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

	            if(isLocalCustomerEnabled){
	            	
	            int maximumMatches = CustomerUtilities.getMaximumMatches(pm);

	            LocaleRequestor locale = utilityManager.getRequestLocales();
	            //set the LocaleRequestor in the Customer object
	            customer.setLocaleRequestor(locale);
	            // set the search criteria
	            CustomerSearchCriteriaIfc criteria = new CustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_INFO, locale);
	            Locale extendedDataRequestLocale = cargo.getOperator().getPreferredLocale();
	            if(extendedDataRequestLocale == null)
	            {
	                extendedDataRequestLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
	            }
	            criteria.setExtendedDataRequestLocale(extendedDataRequestLocale);
	            int maxCustomerItemsPerListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxCustomerItemsPerListSize", "10"));
	            criteria.setMaxCustomerItemsPerListSize(maxCustomerItemsPerListSize);
	            int maxTotalCustomerItemsSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxTotalCustomerItemsSize", "40"));
	            criteria.setMaxTotalCustomerItemsSize(maxTotalCustomerItemsSize);
	            CustomerUtilities.configureCustomerSearchCriteria(criteria, customer);
	            int maxNumberCustomerGiftLists = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxNumberCustomerGiftLists", "4"));
	            criteria.setMaxNumberCustomerGiftLists(maxNumberCustomerGiftLists);
	            criteria.setMaximumMatches(maximumMatches);
	          //Added by Vaibhav for CRM customer serach withou SBI and wallet
	            if(!cargo.isCustomerCRMsearch()) {
	         // sbi reward point changes start here
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
	    		
	    		
	    			if(isSbiPointConversionflag.equals("Y")) {
					//System.out.println("config.isSbiPointConversion()");
					searchCustomerThroughExistingCRMCall(bus, resultList, maximumMatches);
					if((resultList==null || (resultList!=null && resultList.getList()==null) || (resultList!=null && resultList.getList()!=null && resultList.getList().size()==0)) && (!mailLetter)){
						searchCustomerInPosDB(bus, mailLetter);
						if(!mailLetter){
							displayCRMResponseError(bus, "Customer Not Found",
									"CRMCustomersearchError", "promptTICAdd");
						}
					}else {
						isCRMCustomerFetched = true;
					}
				}else {
					// sbi reward point changes end here
	            
				}
	            ResultList resultList = customerManager.getCustomers(criteria);
	            
	            cargo.setCustomerList(resultList.getList());
	            
	            
				if (resultList.getTotalRecords() > maximumMatches) {
					POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
					UIUtilities.setDialogModel(uiManager, DialogScreensIfc.ERROR, cargo.getDialogName(), null, CommonLetterIfc.RETRY);
					mailLetter = false;
				 }}
	            
	            else{
	            	System.out.println("Linking customer");
	            	linkCRMCustomerToTransaction(bus, cargo, customerSearchUtility, mailLetter, tempPhones );
	            	
	            }
				
				}}
			//Changes for Rev 1.0 : Ends
		 catch (Exception e) {
			logger.warn(e);
			linkCRMCustomerToTransaction(bus, cargo, customerSearchUtility, mailLetter, tempPhones );
			letterName = "failureByPhoneNumber";
		}
		// reassign the phone numbers
		cargo.getCustomer().setPhones(tempPhones);
		((MAXCustomerCargo) cargo).setTicCustomerPhoneNoFlag(false);
		//Added by Vaibhav for CRM customer serach withou SBI and wallet
		if(!cargo.isCustomerCRMsearch()) {
if(true) {
	String amount=null;
	//MAXCustomerCargo cargo2 = (MAXCustomerCargo)bus.getCargo();
	MAXConfigParametersIfc configParam = getAllConfigparameter();
	//System.out.println("configParam :"+configParam);
	//boolean pointConFlag = configParam.isSbiPointConversion();
	//int sbiMinPoint = configParam.getSbiMinPoint();
	//System.out.println("Cargo :"+sbiMinPoint);
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
	MAXCustomerIfc customer2=null;
	if (cargo.getCustomer()!=null && cargo.getCustomer() instanceof MAXCustomerIfc) {
		//System.out.println("cargo.getCustomer()!=null && cargo2.getCustomer() instanceof MAXCustomerIfc");
		customer2 = (MAXCustomerIfc) cargo.getCustomer();
		//System.out.println(customer2);
		//System.out.println("customer2.getCustomerName()"+customer2.getCustomerName());
	}
	if(cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
		//System.out.println("cargo.getTransaction() instanceof MAXSaleReturnTransaction");
		customer2 = (MAXCustomerIfc) ((MAXSaleReturnTransaction) cargo.getTransaction()).getCustomer();
		//System.out.println("customer2.getCustomerName()"+customer2.getCustomerName());
		//((MAXSaleReturnTransaction) cargo.getTransaction()).setSbiRewardredeemFlag(true);
	}else if(cargo.getTransaction() instanceof MAXLayawayTransaction) {
		//System.out.println("cargo.getTransaction() instanceof MAXLayawayTransaction");
		customer2 = (MAXCustomerIfc) ((MAXLayawayTransaction) cargo.getTransaction()).getCustomer();
		//System.out.println("customer2.getCustomerName()"+customer2.getCustomerName());
		//((MAXLayawayTransaction) cargo.getTransaction()).setSbiRewardredeemFlag(true);
	}
	
	//totalPoints =200;  // need to comment before delivery
	if(customer2 != null && (customer2 instanceof MAXTICCustomer) && ((MAXTICCustomer) customer2).getSbiPointBal() != null) {
	//if(customer != null && customer.getMAXTICCustomer() != null && ((MAXTICCustomer) customer.getMAXTICCustomer()).getSbiPointBal() != null) {
		//String sbiPoints = ((MAXTICCustomer) customer.getMAXTICCustomer()).getSbiPointBal();
		String sbiPoints = ((MAXTICCustomer) customer2).getSbiPointBal();
		totalPoints = Double.parseDouble(sbiPoints);
		//System.out.println("customer2.getCustomerName()"+customer2.getCustomerName());
	}
	
	//if(pointConFlag && totalPoints >= sbiMinPoint && (cargo.getTransaction() instanceof MAXSaleReturnTransaction|| cargo.getTransaction() instanceof MAXLayawayTransaction)){
		if(pointConFlag.equals("Y") && totalPoints >= sbiMinPoint){
		//int amount = (int)Math.round((double)totalPoints/conversionRate);
		DecimalFormat decimalFormat = new DecimalFormat("##.##");
		amount= decimalFormat.format(totalPoints/conversionRate);
	
	}
}
		}
		
		if (mailLetter) {
			bus.mail(new Letter(letterName), BusIfc.CURRENT);
		}
	
		
	}
	public void linkCRMCustomerToTransaction(BusIfc bus, MAXCustomerCargo cargo, MAXWebCRMCustomerSearchUtility customerSearchUtility,
								boolean mailLetter, Vector<PhoneIfc> tempPhones) {

    	MAXCustomerIfc newCustomer = (MAXCustomerIfc) cargo.getCustomer();
        MAXCRMSearchCustomer searchCustomer = new MAXCRMSearchCustomer();
        String mobileNumber = cargo.getCustomer().getPrimaryPhone().getPhoneNumber();
        searchCustomer.setMobileNumber(mobileNumber);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        searchCustomer.setMessageId(sdf.format(timestamp));
        //searchCustomer.setCardNumber("0001829274");
        searchCustomer.setCardNumber("");
        
    	customerSearchUtility.searchCRMCustomer(bus, searchCustomer);
    	//Added by vaibhav
    	if(!cargo.isCustomerCRMsearch()) {
    	if(searchCustomer.getPointBal()!= null){
    		newCustomer.setBalancePoint(new BigDecimal(searchCustomer.getPointBal()));
    	}
    	else{mailLetter = false;
    	}}
    	
    	if(searchCustomer.getCustName() != null){
    		newCustomer.setFirstName(searchCustomer.getCustName());
    		newCustomer.setCustomerName(searchCustomer.getCustName());}
    	if(searchCustomer.getCustTier()!= null && ( searchCustomer.getCustTier().equalsIgnoreCase("PLATINUM")||
    						searchCustomer.getCustTier().equalsIgnoreCase("GOLD") ) ){
    		newCustomer.setCustomerType("T");
    		newCustomer.setCustomerTier(searchCustomer.getCustTier());
    	}
    	newCustomer.setPhones(tempPhones);
    	if(searchCustomer.getResCardNumber()!=null){
    		newCustomer.setLoyaltyCardNumber(searchCustomer.getResCardNumber());
    		newCustomer.setCustomerID(searchCustomer.getResCardNumber());
    	}
	if (searchCustomer.getPincode() != null) {
		AddressIfc address = (AddressIfc) cargo.getCustomer()
				.getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_HOME);
		address.setPostalCode(searchCustomer.getPincode());
		Vector addVec = new Vector();
		addVec.add(address);
		newCustomer.setAddresses(addVec);
	}
	cargo.setCustomer(newCustomer);
	}
	
    
		
	
	public void searchCustomerThroughExistingCRMCall(BusIfc bus, ResultList resultList, int maximumMatches){
		MAXCustomerCargo cargo = (MAXCustomerCargo) bus.getCargo();
		CustomerIfc customer = cargo.getCustomer();
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		boolean dualDisplayEnabled = Gateway.getBooleanProperty("application", "DualDisplayEnabled", false);
		
		/*Rev 1.3 start*/
		cargo.setTicCustomerPhoneNo(null);
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
			
			MAXTICCustomerIfc crmCustomer = (MAXTICCustomerIfc) domainFactory.getTICCustomerInstance();
			
			crmCustomer.setTICCustomerID(searchCustomer.getResCardNumber());
			crmCustomer.setTICEmail(searchCustomer.getEmail());
			crmCustomer.setTICMobileNumber(searchCustomer.getResMobileNumber());
			crmCustomer.setTICFirstName(searchCustomer.getCustName());
			crmCustomer.setTICLastName("");
			
			crmCustomer.setTICCustomerTier(searchCustomer.getCustTier());
			crmCustomer.setTICPinNumber(searchCustomer.getPincode());
			crmCustomer.setTICCustomerPoints(searchCustomer.getPointBal());
			crmCustomer.setSbiPointBal(searchCustomer.getSbiPointBal());
		
		if(cargo.getCustomer()!=null && cargo.getCustomer() instanceof MAXCustomer){
			MAXCustomer maxCustomer=(MAXCustomer)cargo.getCustomer();
			
			Long custID=0l;
			try{
			 custID=new Long(crmCustomer.getTICCustomerID());
			}catch(Exception e){
				
			}
			if (custID.compareTo(CUST_ID_MIN_LIMIT) > 0) {
				crmCustomer.setCustomerType(MAXCustomerConstantsIfc.CRM);
			}
			//lsiplCustomer.setTicCustomerVisibleFlag(true);
			
			crmCustomer.setTicCustomerVisibleFlag(true);
			maxCustomer.setTicCustomerVisibleFlag(true);
			maxCustomer.setCustomerID(searchCustomer.getResCardNumber());
			maxCustomer.setEMailAddress(searchCustomer.getEmail());
			//maxCustomer.getPrimaryPhone().setPhoneNumber(searchCustomer.getResMobileNumber());
			List<PhoneIfc> phoneArr = new ArrayList<PhoneIfc>();
			//PhoneIfc phoneArr[] = new PhoneIfc[1];
			Phone phone = new Phone();
			phone.setPhoneNumber(searchCustomer.getResMobileNumber());
			//Changes for Rev 1.0 : Starts
			phone.setPhoneType(PhoneConstantsIfc.PHONE_TYPE_MOBILE);
			//Changes for Rev 1.0 : Ends
			phoneArr.add(phone);
			maxCustomer.setPhoneList(phoneArr);
			maxCustomer.setFirstName(searchCustomer.getCustName());
			maxCustomer.setCustomerName(searchCustomer.getCustName());
			maxCustomer.setCustomerTier(searchCustomer.getCustTier());
			maxCustomer.setBalancePoint(new BigDecimal(searchCustomer.getPointBal()));
			maxCustomer.setSbiPointBal(searchCustomer.getSbiPointBal());
		//	maxCustomer.setPinNumber(searchCustomer.getPincode());
		//	maxCustomer.setCustomerPoints(searchCustomer.getPointBal());
			
			if (custID.compareTo(CUST_ID_MIN_LIMIT) > 0) {
				maxCustomer.setCustomerType(MAXCustomerConstantsIfc.CRM);
			}
			maxCustomer.setMAXTICCustomer(crmCustomer);
			cargo.setCustomer(maxCustomer);
			//Rev 1.4  start
			// set transaction data start
			//cargo.setCustomer(crmCustomer);
			if (cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
				((MAXSaleReturnTransaction) cargo.getTransaction()).setTicCustomerVisibleFlag(true);
				((MAXSaleReturnTransaction) cargo.getTransaction()).setMAXTICCustomer(crmCustomer);

			} else if (cargo.getTransaction() instanceof MAXLayawayTransactionIfc) {
				((MAXLayawayTransactionIfc) cargo.getTransaction()).setTicCustomerVisibleFlag(true);
				((MAXLayawayTransactionIfc) cargo.getTransaction()).setLSSIPLTICCustomer(crmCustomer);
			}
			// set transaction data end
			//Rev 1.4  end
			List<CustomerIfc> customerList=new ArrayList<CustomerIfc>();
			customerList.add(maxCustomer);
			cargo.setCustomerList(customerList);
			}
}
			getCustomerDetails(cargo);
	}
	private void getCustomerDetails(MAXCustomerCargo cargo) {
		if (cargo.getCustomer() instanceof MAXCustomerIfc) {
			MAXCustomerIfc customer = (MAXCustomerIfc) cargo.getCustomer();
		}
	}

	public void searchCustomerInPosDB(BusIfc bus, Boolean mailLetter1){
		/*int maximumMatches = 0;*/
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		CustomerManagerIfc customerManager = (CustomerManagerIfc) bus.getManager(CustomerManagerIfc.TYPE);
		UtilityManagerIfc utilityManager = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		MAXCustomerCargo cargo = (MAXCustomerCargo) bus.getCargo();
		CustomerIfc customer = cargo.getCustomer();
		//System.out.println("searchCustomerInPosDB()"+customer.getCustomerName());
		
		LocaleRequestor locale = utilityManager.getRequestLocales();
		// set the LocaleRequestor in the Customer object
		customer.setLocaleRequestor(locale);
		// set the search criteria
		MAXCustomerSearchCriteriaIfc criteria = new MAXCustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_INFO, locale);
		Locale extendedDataRequestLocale = cargo.getOperator().getPreferredLocale();
		if (extendedDataRequestLocale == null) {
			extendedDataRequestLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
		}
		criteria.setExtendedDataRequestLocale(extendedDataRequestLocale);
		int maxCustomerItemsPerListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP,"MaxCustomerItemsPerListSize", "10"));
		criteria.setMaxCustomerItemsPerListSize(maxCustomerItemsPerListSize);
		int maxTotalCustomerItemsSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP,"MaxTotalCustomerItemsSize", "40"));
		criteria.setMaxTotalCustomerItemsSize(maxTotalCustomerItemsSize);
		MAXCustomerUtilities.configureCustomerSearchCriteria(criteria, customer);
		int maxNumberCustomerGiftLists = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP,"MaxNumberCustomerGiftLists", "4"));
		criteria.setMaxNumberCustomerGiftLists(maxNumberCustomerGiftLists);
		criteria.setMaximumMatches(maximumMatches);
		try {
			resultList = customerManager.getCustomers(criteria);
		} catch (oracle.retail.stores.foundation.manager.data.DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*changes for Rev 1.2  end*/
		cargo.setCustomerList(resultList.getList());
		maximumMatches = CustomerUtilities.getMaximumMatches(pm);
		/*changes for Rev 1.2  start*/
		if(resultList==null || (resultList!=null && resultList.getList()==null) || (resultList!=null && resultList.getList()!=null && resultList.getList().size()==0)){
			mailLetter = false;
		}
		else{
			mailLetter = true;
			if(MAXGSTUtility.edgePreviewSaleEnabled(cargo.getRegister().getBusinessDate(), config)){
				if(bus.getCargo() instanceof MAXCustomerCargo){
					cargo.setCustLinkedThroughDB(true);
				}
				else if(bus.getCargo() instanceof MAXCustomerMainCargo){
					cargo.setCustLinkedThroughDB(true);
				}
			}
		}
	}
	protected void displayCRMResponseError(BusIfc bus, String errorMessage,String resourceId, String letterName) {
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
	public MAXConfigParametersIfc getAllConfigparameter() {

		MAXConfigParameterTransaction configTransaction = new MAXConfigParameterTransaction();
		MAXConfigParametersIfc configParameters = null;
		configTransaction = (MAXConfigParameterTransaction) DataTransactionFactory
				.create(MAXDataTransactionKeys.CONFIG_PARAMETER_TRANSACTION);

		try {
			configParameters = configTransaction.selectConfigParameters();
		} catch (oracle.retail.stores.foundation.manager.data.DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return configParameters;
	}

}
