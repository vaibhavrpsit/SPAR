/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.1 	Jan 06, 2017		Ashish Yadav		Changes for Online redemption loyalty OTP FES	
 *	Rev	1.0 	Jan 03, 2017		Ashish Yadav		Changes for Online redemption loyalty OTP FES
 *
 ********************************************************************************/
package max.retail.stores.pos.services.sale.validate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerConstantsIfc;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.utility.MAXCustomerSearchCriteria;
import max.retail.stores.domain.utility.MAXCustomerSearchCriteriaIfc;
import max.retail.stores.pos.services.capillary.MAXCapillaryCustomer;
import max.retail.stores.pos.services.capillary.MAXCapillaryHelperUtility;
import max.retail.stores.pos.services.sale.MAXSaleCargoIfc;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.arts.CustomerReadDataTransaction;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.TagConstantsIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

public class MAXCustomerLookupAisle extends PosLaneActionAdapter {

	public void traverse(BusIfc bus) {
		SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		String id = ui.getInput();
		
		ParameterManagerIfc pm =(ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
		int min = 0, max =0;
		try
		{
			min = pm.getIntegerValue("MinTicCardLength").intValue();
			max = pm.getIntegerValue("MaxTicCardLength").intValue();
		}
		catch(ParameterException e)
		{
			logger.error(e);
		}
		if(id.length()>= min && id.length()<= max)
		{
			CustomerReadDataTransaction ct = null;
			ct = (CustomerReadDataTransaction) DataTransactionFactory
					.create(DataTransactionKeys.CUSTOMER_READ_DATA_TRANSACTION);
			// Changes start for Rev 1.0 (Cardless Loyalty)
						MAXCustomerIfc customer = (MAXCustomerIfc) ((MAXSaleReturnTransaction)((MAXSaleCargoIfc) cargo).getTransaction()).getTicCustomer();
						// Changes ends for Rev 1.0 (Cardless Loyalty)
						// Changes starts for Rev 1.1 (Ashish : Online points redemption)
						UtilityManagerIfc utilityManager = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
						LocaleRequestor locale = utilityManager.getRequestLocales();
						MAXCustomerSearchCriteriaIfc criteria = new MAXCustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_INFO, locale);
				
						/*if (customer == null)
							criteria = (CustomerSearchCriteriaIfc) ((MAXDomainObjectFactory) DomainGateway.getFactory())
									.getCustomerInstance();*/
						criteria.setCustomerID(id);
						//((Customer)criteria).setCustomerIDPrefix(Gateway.getProperty("application","StoreID", null));
						criteria.setCustomerIDPrefix(Gateway.getProperty("application","StoreID", null));
						try {
							//customer = (MAXCustomerIfc) ct.readCustomer(customer);
							
							customer = (MAXCustomerIfc) ct.readCustomer(criteria);
							// Changes ends for Rev 1.1 (Ashish : Online points redemption)
				if (((MAXCustomer) customer).getCustomerType().equalsIgnoreCase(
						MAXCustomerConstantsIfc.CRM)) {
					((MAXSaleReturnTransaction) cargo.getTransaction()).setTicCustomer(customer);
					JournalManagerIfc journal = (JournalManagerIfc) bus
							.getManager(JournalManagerIfc.TYPE);
	
					/** Journaling TIC Details **/
					StringBuffer sb = new StringBuffer();
					sb.append("\nTIC Customer ID: ").append(
							customer.getCustomerID());
	
					sb.append("\nTIC Customer Name: ").append(
							customer.getFirstLastName() + "\n");
					journal.setEntryType(3);
					journal.journal(cargo.getTenderableTransaction().getCashier()
							.getEmployeeID(), cargo.getTenderableTransaction()
							.getTransactionID(), sb.toString());
					//MAX Changes for TIC customer By Manpreet:Start
					//if (cargo.getTransaction().getCustomer() == null) {
						cargo.getTransaction().setCustomer(customer);
					//}
					//MAX Changes for TIC customer By Manpreet:END
					/** Setting UP Customer Details in Status Panel **/
					UtilityManagerIfc utility = (UtilityManagerIfc) bus
							.getManager(UtilityManagerIfc.TYPE);
					StatusBeanModel statusModel = new StatusBeanModel();
					String[] vars = { customer.getFirstName(),
							customer.getLastName() };
					String pattern = utility.retrieveText("CustomerAddressSpec",
							BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
							TagConstantsIfc.CUSTOMER_NAME_TAG,
							TagConstantsIfc.CUSTOMER_NAME_PATTERN_TAG);
					String customerName = LocaleUtilities.formatComplexMessage(
							pattern, vars);
					statusModel.setCustomerName(customerName);
					LineItemsModel baseModel = new LineItemsModel();
					baseModel.setStatusBeanModel(statusModel);
					ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);
					
					/**Setting up display on Pole Display**/
					POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
					try
					{				
						pda.clearText();
						String loyaltyMSG = utility.retrieveLineDisplayText("LoyaltyCard", "");
						loyaltyMSG += ": "+customerName; 
						pda.displayTextAt(0, 0, loyaltyMSG);
					}
					catch(DeviceException de)
					{
						logger.warn("Unable to use Line Display: " + de.getMessage() + "");
					}
					//MAX Changes for TIC customer By Manpreet:Start
					showLoyaltyPointsInfoDialog(bus, ui, (MAXCustomerIfc)customer);
					//bus.mail(CommonLetterIfc.SUCCESS);
					//MAX Changes for TIC customer By Manpreet:End
				}
				else
				{
					showErrorDialog(ui);
				}
				
			} catch (DataException e) {
				showYesNoDialog(ui);
			}
		}
		else
			showErrorDialog(ui);
	}
	private void showErrorDialog(POSUIManagerIfc ui)
	{
		DialogBeanModel dialogModel = new DialogBeanModel();
		// build the dialog screen
		dialogModel.setResourceID("InvalidTicCard");
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,
				CommonLetterIfc.RETRY);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	private void showYesNoDialog(POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		// build the dialog screen
		dialogModel.setResourceID("INVALID_MEMBERSHIP_CARD_NOTICE");
		dialogModel.setType(DialogScreensIfc.YES_NO);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES,
				CommonLetterIfc.YES);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO,
				CommonLetterIfc.NO);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
	//MAX Changes for TIC customer By Manpreet:Start
	private void showLoyaltyPointsInfoDialog(BusIfc bus,POSUIManagerIfc ui,MAXCustomerIfc customer) {
		 ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
	        UtilityManagerIfc util = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            if(customer.getCustomerType().equalsIgnoreCase(MAXCustomerConstantsIfc.CRM))
            {
            	EYSDate date =customer.getBalancePointLastUpdationDate();
            	String args[] =new String[14];
            	args[0]=date.toFormattedString("dd/MM/yyyy");
            	args[1]=customer.getBalancePoint()+"";
            	args[2]=customer.getPointsExpiringNextMonth()+"";
            	args[3]=customer.getCustomerTier();
            	args[4]="false";
            	args[5]="false";
            	args[6]=getEquivalentAmount(bus, customer.getBalancePoint())+"";
            	args[7]=customer.getCustomerName();
            	
            	EYSDate birthdate = customer.getBirthdate();
    			if (birthdate != null) {
    				EYSDate bdate = new EYSDate();
    				int birthMonth = birthdate.getMonth();
    				int month = bdate.getMonth();
    				if (birthMonth == month) {
    					int birthDay = birthdate.getDay();
    					int currentDay = date.getDay();
    					try {
    						Integer birthPeriod = pm.getIntegerValue("FlashCustomerBirthdayMessagePeriod");
    						int remainingDay = Math.abs(currentDay - birthDay);

    						if (remainingDay <= birthPeriod.intValue()) {
    							String birthDayMessage = pm.getStringValue("CustomerBirthdayMessage");
    							
    							// MAX Changes Rev 1.1 Start
    							String birthdayString = util.retrieveText("CustomerMasterSpec", "customerText", "BirthdayMessage", "BirthdayMessage");
    	    					
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
    			
    			MAXCapillaryCustomer capCustomer=new MAXCapillaryCustomer();
    			
    			MAXCapillaryHelperUtility cust= new MAXCapillaryHelperUtility();
    			String custid=customer.getCustomerID();
    			ArrayList customerList=null;
    	    	HashMap request = new HashMap();
    	    	HashMap responseMap = new HashMap();
    	    	
    	     	request.put("Customer Card Number", custid);
    		
    	    	MAXCustomerIfc[] capCustomerArray = null;
    	    
    	    	try {
    				responseMap=cust.lookup(request, responseMap); /*Request send to Capillary*/	
    			} catch (Exception e) {
    				
    				e.printStackTrace();
    			} 
    			String ItemStatusCode="";
    	    	String ConnResponseCode= responseMap
    			.get("Response Code").toString();
    	    	if(responseMap.get("ItemStatusCode")!=null){
    	    	 ItemStatusCode= responseMap
    			.get("ItemStatusCode").toString();
    	    	}
    			if(ConnResponseCode.equals("200")){
    				
    				if(ItemStatusCode.equals("1000"))
    				
    				{	
    			customerList=(ArrayList) responseMap.get("Customers");
    			capCustomerArray=new MAXCustomerIfc[customerList.size()];
    			Iterator custItr=customerList.iterator();
    			int custCount=0;
    			
    			while(custItr.hasNext()){
    				capCustomer=(MAXCapillaryCustomer)custItr.next();
    				//capCustomerArray[custCount]=(MAXCustomerIfc) DomainGateway.getFactory()
    						//.getCustomerInstance();
    				
    		    	
    				custCount++;
    			   }
    			 
    				}
    				else{
    					args[10]="";	
    					args[11]="";
    					args[12]="";
    					args[13]="";	
    				}
    				
    				
    			
    				ArrayList offers=capCustomer.getOffers();
    				if(offers==null){
    					offers=new ArrayList();
    				}
    				if(!offers.isEmpty()){
    				try{
    				int s = 10;
    				for(int i = 0;i<offers.size();i++){
    				if (s<=12){
    					
    					args[s]=("Offer"+(i+1))+":"+(String) offers.get(i);
    					 s++;
    				}	
    				}
    				if(offers.size()>3){
    					args[13]="";
    					//commented after client discussion  by Arif
    					//args[13]="And more";
    				}
    				else{
    					args[13]="";
    				}
    				if(args[10]==null){
    					args[10]="";		
    				}
    				if(args[11]==null){
    					args[11]="";		
    				}
    				if(args[12]==null){
    					args[12]="";		
    				}
    				}
    	            catch(Exception e){
    					
    				}
    	            
    	            }
    	              else{
    	            	  args[10]="";	
    	            	  args[11]="";
    	            	  args[12]="";
    	            	  args[13]="";
    						
    					}
    				}
    				else{
    					
    					args[10]="";	
    					args[11]="";
    					args[12]="";
    					args[13]="";
    						
    					}
    			
            	
            	DialogBeanModel dialogModel = new DialogBeanModel();
            	dialogModel.setResourceID("LoyaltyPointsDetails");
            	dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            	dialogModel.setArgs(args);
            	dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.SUCCESS);
            	ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            	return;     
        }
	}
	 public BigDecimal getEquivalentAmount(BusIfc bus,BigDecimal points)
	    {
	    	ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
	    	String conversionFactor;
	    	try {
				conversionFactor = parameterManager.getStringValue("LoyaltyPointConversionRate");
				
			} catch (Exception e) {
				conversionFactor = "166.00";
				logger.error("Error While getting value for parameter(LoyaltyPointConversionRate)",e);
			}
			BigDecimal cRate = new BigDecimal(conversionFactor);
			BigDecimal equivalentAmount=points.multiply(new BigDecimal("100.00"));
			equivalentAmount = equivalentAmount.divide(cRate, 0);
			
			return equivalentAmount;
	    }
	//MAX Changes for TIC customer By Manpreet:End

}
