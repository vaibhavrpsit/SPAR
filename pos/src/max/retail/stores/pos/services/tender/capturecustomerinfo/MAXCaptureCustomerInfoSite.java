
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	Rev 1.4		May 04, 2017		Kritica Agarwal 	GST Changes
 *	Rev 1.3     Feb 22, 2017		Ashish Yadav		Bug (Enter wrong phone no and then go to item send, unexpected exception due to state null)
 *	Rev 1.1     Nov 08, 2016		Ashish Yadav		Home Delivery Send FES
 *	Rev 1.1     Nov 08, 2016		Ashish Yadav		Code merging
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.capturecustomerinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXGSTRegionMappingTransactions;
import max.retail.stores.domain.arts.MAXReadHomeStateTransactions;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.utility.MAXGSTRegion;
import max.retail.stores.pos.manager.ifc.MAXUtilityManagerIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.CustomerReadDataTransaction;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.CountryIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.domain.utility.StateIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.DispatcherIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.tender.capturecustomerinfo.CaptureCustomerInfoCargo;
import oracle.retail.stores.pos.services.tender.tdo.CaptureCustomerInfoTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CaptureCustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;


public class MAXCaptureCustomerInfoSite extends PosSiteActionAdapter{

	/**
     * SerialVersionUID
	 */
    private static final long serialVersionUID = 9145736589209726721L;

    public static final String SITENAME = "CaptureCustomerInfoSite";

	  public void arrive(BusIfc bus)
	    {
	    	CaptureCustomerInfoCargo cargo = (CaptureCustomerInfoCargo) bus.getCargo();
	    	//Change for Rev 1.4 :Starts
	    	if((cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc) && ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).isCaptureCustomer())
	    	{
	    		bus.mail("Success");
	    	}else{
	    		//Change for Rev 1.4 :Ends
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        //Change for Rev 1.4 :Starts
	    	//UtilityManagerIfc   utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);  
	    	MAXUtilityManagerIfc utility = (MAXUtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
	        utility.setCountries();
	      //Change for Rev 1.4 : Ends
		// changes start for Rev 1.0
	    	UtilityIfc utility1;
		// Changes ends for Rev 1.0
            CustomerInfoBeanModel custModel = null;
	    	TransactionIfc trans = cargo.getTransaction();
	        CaptureCustomerIfc customer = null;
	        // Initialize to type unknown
	      //Change for Rev 1.4 :Starts
	        HashMap<Integer,MAXGSTRegion> GSTstates=getGSTMapping();
	        if(trans instanceof MAXSaleReturnTransactionIfc)
	        	((MAXSaleReturnTransactionIfc)trans).setHomeState(readHomeState(Gateway.getProperty("application", "StoreID", "")));
	        //Change for Rev 1.4 :Ends
	        
	        //int transactionType = TransactionIfc.TYPE_UNKNOWN;
	        
	        if (cargo.getCustomer() != null)
	        {
	            customer = cargo.getCustomer();
	            
	            if (trans != null)
	            {
	                if (trans.getCustomerInfo() != null)
	                {
                	// Changes start for Rev 1.1 (Send)
                    customer.setPersonalIDType(trans.getCustomerInfo().getLocalizedPersonalIDType());
                 // Changes ends for Rev 1.1 (Send)
	                }
	            }
	        }
	        else
	        {
	            if (trans != null)
	            {
	                if (trans.getCaptureCustomer() != null)
	                {    
	                    cargo.setCustomer(trans.getCaptureCustomer());
	                }
	               // transactionType = trans.getTransactionType();   
	            }
	        }

        CustomerInfoBeanModel customerInfoBeanModel = new CustomerInfoBeanModel();
        custModel = (CustomerInfoBeanModel)CustomerUtilities.populateCustomerInfoBeanModel(customer, utility, pm, customerInfoBeanModel);
	        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
	        TDOUIIfc tdo = null;
		// Changes start for Rev 1.0
	        utility1=null;
		// Changes neds for Rev 1.0

	        // Create the tdo object.
	        try
	        {
		// chnages start for Rev 1.0
	        	utility1 = Utility.createInstance();
		// Changes ends for Rev 1.0
	            tdo = (TDOUIIfc) TDOFactory.create("tdo.tender.CaptureCustomerInfo");
	        }
	        catch (TDOException tdoe)
	        {
	            tdoe.printStackTrace();
	        }
		// Chnages start for Rev 1.0
	        catch (ADOException e) {
	        	e.printStackTrace();
			} 
			// Chnages ends for Rev 1.0

        // Load the CodeList
        String storeID = Gateway.getProperty("application", "StoreID", "");

        CodeListIfc codeList = null;
        if (cargo.getTenderType() == TenderLineItemConstantsIfc.TENDER_TYPE_MAIL_BANK_CHECK
                || cargo.getTenderType() == TenderLineItemConstantsIfc.TENDER_TYPE_STORE_CREDIT) // Store credits use mail bank reason codes
        {
        	codeList = utility.getReasonCodes(storeID, CodeConstantsIfc.CODE_LIST_MAIL_BANK_CHECK_ID_TYPES);
        }
        else
        {
        	codeList = utility.getReasonCodes(storeID, CodeConstantsIfc.CODE_LIST_CAPTURE_CUSTOMER_ID_TYPES);
        }
        cargo.setPersonalIDTypes(codeList);

        HashMap<String, Object> map = new HashMap<String, Object>(1);
	        map.put("Bus", bus);

        // Check to see if there is already a model in the cargo. If so, that
        // model
        // takes precedence. If no model exists on the cargo, then we create a
        // new model
	        // and prefill it with any available customer data.
        // The reason we check the cargo for a model is to take care of the case
        // when we
	        // return from the ValidateCustomerInfoSite with a "Retry" letter.

	        CaptureCustomerInfoBeanModel model = null;
	       
	        if (cargo.getModel() != null)
	        {
	            model = cargo.getModel();
	        }
	        else
	        {
            if (tdo != null)
            {
	            model = (CaptureCustomerInfoBeanModel) ((CaptureCustomerInfoTDO) tdo).buildBeanModel(map);
	            model.setPhoneTypes(CustomerUtilities.getPhoneTypes(utility));
	            
	            // Transfer any customer information into the model.
	            // Changes starts for Rev 1.3 (Ashish)
	            CaptureCustomerIfc captureCustomerIfc=(CaptureCustomerIfc) cargo.getCustomer();
				
				if(captureCustomerIfc !=null && captureCustomerIfc.getPhoneType()==-1){
					
					captureCustomerIfc.setPhoneType(0);
				}
				// Transfer any customer information into the model.
				if(captureCustomerIfc !=null  
					    && captureCustomerIfc.getCountry()!=null
						&& captureCustomerIfc.getState()!=null
						//&& captureCustomerIfc.getAreaCode()!=null
						&& captureCustomerIfc.getPhoneNumber()!=null
						){
					
				// Transfer any customer information into the model.
				((CaptureCustomerInfoTDO) tdo).customerToModel(model, bus, captureCustomerIfc);
				
				}
				 // Changes ends for Rev 1.3 (Ashish)
            }
	        }
		// Changes start for Rev 1.0
	        CustomerReadDataTransaction ct = null;
	        
	        ct = (CustomerReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.CUSTOMER_READ_DATA_TRANSACTION);
	        
	        try {
	        	
	        	
	        	String customer_type="";
	        	CustomerIfc[] customerArray=  null;
	        	if(cargo.getCustomer()!=null)
	        	customerArray = ct.selectCustomers((CustomerIfc)cargo.getCustomer());
	        	
	        	if(customerArray!=null && customerArray.length!=0)
	        	{
	        	  customer_type=((MAXCustomerIfc) customerArray[0]).getCustomerType();
	        	}
	        	//Changes for TIC Customer By Manpreet :Start
	        	/*if(customer_type.equals("T"))
	        	{
	        		model.setEditableFields(false);
	        	}*/
	        	//Changes for TIC Customer By Manpreet :End
				/**MAX Rev 1.1 Change : End**/
	        } catch (DataException e) {
				
	        	String message = "Could not get remoteDT MAXCaptureCustomerInfoSite line 145";
				logger.error(message, e);
 			}
			// Changes nends for Rev 1.0
        if (model != null && custModel != null && custModel.getPhoneList() != null)
        {
            model.setPhoneList(custModel.getPhoneList());
        }
        // Determine the screen type based on the transaction/tender
        // type that was passed to it.
	        if (cargo.getTenderType() == TransactionConstantsIfc.TYPE_SEND)
	        {
	            if (cargo.getTransaction() instanceof SaleReturnTransaction)
	            {
	                SaleReturnTransaction saleReturnTransaction = (SaleReturnTransaction)cargo.getTransaction();
	                if (saleReturnTransaction.getCustomer() != null)
	                {
	                    if (model != null && saleReturnTransaction.getCustomer().isBusinessCustomer())
	                    {
	                        String companyName = saleReturnTransaction.getCustomer().getCompanyName();
	                        model.setOrgName(companyName);
	                        model.setBusinessCustomer(true);
	                    }
	                }
	
	            }
	            //Change for Rev 1.4 :Starts
	           
                	if(cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc && ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).isGstEnable() && (((MAXCaptureCustomerInfoCargo)cargo).isSend()||((MAXSaleReturnTransactionIfc)cargo.getTransaction()).isDeliverytrnx()) && GSTstates != null)
                	{
                		if(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getLineItemsSize()>1){
                			//Home state only -> since previous items was not marked for interstate delivery.
                			               			
                			CountryIfc[] country=model.getCountries();
	                		StateIfc[] states = country[0].getStates();	                		
	                        
	                        //StateIfc[] stateList = new StateIfc[2] ;
	                        ArrayList<StateIfc> state = new ArrayList<StateIfc>();
	                        if(state.isEmpty()){
		                    	   StateIfc aState = DomainGateway.getFactory().getStateInstance();
	                       			aState.setStateCode("");
	                       			aState.setStateName("Select State");
		                    	    state.add(aState);
		                       }
	                        StateIfc aState = DomainGateway.getFactory().getStateInstance();
	                        aState.setCountryCode("IN");
	                        for(int i=0;i<states.length;i++){
	                        	if(states[i].getStateName().compareToIgnoreCase(
	                        			((MAXSaleReturnTransactionIfc)trans).getHomeState())==0){
	                        		aState.setStateCode(states[i].getStateCode());
	                        	((MAXSaleReturnTransactionIfc)cargo.getTransaction()).setHomeStateCode(states[i].getStateCode());
	                        	}
	                        }
	                        aState.setStateName(((MAXSaleReturnTransactionIfc)trans).getHomeState());
	                        state.add(aState);
	                        StateIfc[] stateList = new StateIfc[state.size()];
	                        state.toArray(stateList); 
	                        model.getCountries()[0].setStates(stateList);
	                        String[] homeState= new String[1];
	                        homeState[0]=((MAXSaleReturnTransactionIfc)trans).getHomeState();
	                        model.setStateNames(homeState);
	                        model.setStateIndex(0);
                		}else{
	                		CountryIfc[] country=model.getCountries();
	                		StateIfc[] states = country[0].getStates();      
	                        ArrayList<StateIfc> state = new ArrayList<StateIfc>();
	                       if(state.isEmpty()){
	                    	   StateIfc aState = DomainGateway.getFactory().getStateInstance();
                       			aState.setStateCode("");
                       			aState.setStateName("Select State");
	                    	    state.add(aState);
	                       }
	                        String[] region = new String[GSTstates.size()];;
	                		for(int i=1;i<=GSTstates.size();i++){
	                			region[i-1]=GSTstates.get(i).getRegionDesc();
	                		}
	                		HashMap<String,String> stateMap = new HashMap<String,String>();
	                		
	                        for(int i=0;i<states.length;i++){
	                        	stateMap.put(states[i].getStateCode(), states[i].getStateName());
	                        	for(int j=0;j<GSTstates.size();j++){
	                        	if(states[i].getStateName().compareToIgnoreCase(GSTstates.get(j+1).getRegionDesc())==0){
	                        		 StateIfc aState = DomainGateway.getFactory().getStateInstance();
	                        		aState.setStateCode(states[i].getStateCode());
	                        		aState.setStateName(states[i].getStateName());
	                        		state.add(aState);
	                        		
	                        		}
	                        	}
	                        	try{
	 	                       if(states[i].getStateName().compareToIgnoreCase(((MAXSaleReturnTransactionIfc)trans).getHomeState())==0)
	 	                        	((MAXSaleReturnTransactionIfc)cargo.getTransaction()).setHomeStateCode(states[i].getStateCode());
	                        	}
	                        	catch (NullPointerException e)
	                        	{
	                        		logger.error(e);
	                        	}
	                        	}
	                        ((MAXSaleReturnTransactionIfc)trans).setStates(stateMap);
	                        StateIfc[] newStates = new StateIfc[state.size()];
	                        state.toArray(newStates);
	                        
	                   	   // model.setCountriesMapping(model.getCountries());              
	                        model.getCountries()[0].setStates(newStates);
	                        model.setStateNames(region);
	                        model.setStateIndex(0);
	                       
                		}
                	}
	              //Change for Rev 1.4 :Ends
	        
	            cargo.setScreenType(POSUIManagerIfc.CAPTURE_CUSTOMER_INFO_SEND);
	        }
	        else if (cargo.getTenderType() == TenderLineItemConstantsIfc.TENDER_TYPE_MAIL_BANK_CHECK)
	        {
            if (cargo.getTransaction() instanceof SaleReturnTransaction)
            {
                SaleReturnTransaction saleReturnTransaction = (SaleReturnTransaction)cargo.getTransaction();
                if (saleReturnTransaction.getCustomer() != null)
                {
                    CustomerIfc saleReturnCustomer = saleReturnTransaction.getCustomer();
                    model = populateCustomerDetails(model, saleReturnCustomer, utility, pm);
                }
            }
            if (model != null)
            {
                model.setMailCheck(true);
            }
	            cargo.setScreenType(POSUIManagerIfc.CAPTURE_CUSTOMER_INFO_BANK_CHECK);
	        }
	        else
	        {
            if (cargo.getTransaction() instanceof SaleReturnTransaction)
            {
                SaleReturnTransaction saleReturnTransaction = (SaleReturnTransaction)cargo.getTransaction();
                if (saleReturnTransaction.getCustomer() != null)
                {
                    CustomerIfc saleReturnCustomer = saleReturnTransaction.getCustomer();
                    model = populateCustomerDetails(model, saleReturnCustomer, utility, pm);
                }
            }

	            cargo.setScreenType(POSUIManagerIfc.CAPTURE_CUSTOMER_INFO_DEFAULT);
	        }
	        
	        // Display the screen.
	        ui.showScreen(cargo.getScreenType(), model);
	    	}
	    }

    private CaptureCustomerInfoBeanModel populateCustomerDetails(CaptureCustomerInfoBeanModel model,
            CustomerIfc customer, UtilityManagerIfc utility, ParameterManagerIfc pm)
    {
        int index = 0;
        int countryIndex = 0;
        PhoneIfc phone = null;
        AddressIfc address = null;

        if (customer.isBusinessCustomer())
        {
            String companyName = customer.getLastName();
            model.setOrgName(companyName);
            model.setLastName(companyName);
            model.setBusinessCustomer(true);
        }
        else
        {
            model.setFirstName(customer.getFirstName());
            model.setLastName(customer.getLastName());
            model.setBusinessCustomer(false);
        }

        // set the address in the model
        List<AddressIfc> addressVector = customer.getAddressList();

        if (!addressVector.isEmpty())
        {
            // look for the first available address
            while (address == null && index < AddressConstantsIfc.ADDRESS_TYPE_DESCRIPTOR.length)
            {
                address = customer.getAddressByType(index);
                index++;
            }

            if (address != null)
            {
                Vector<String> lines = address.getLines();
                if (lines.size() >= 1)
                {
                    model.setAddressLine1(lines.get(0));
                }

                if (lines.size() >= 2 && lines.get(1) != null)
                {
                    model.setAddressLine2(lines.get(1));
                }

                // get list of all available states and selected country and
                // state
                countryIndex = CustomerUtilities.getCountryIndex(address.getCountry(), utility, pm);
                model.setCountryIndex(countryIndex);

                if (Util.isEmpty(address.getState()))
                {
                    model.setStateIndex(-1);
                }
                else
                {
                    model.setStateIndex(utility.getStateIndex(countryIndex, address.getState(), pm));
                }

                model.setCity(address.getCity());
                model.setPostalCode(address.getPostalCode());
            }
        }
        else
        {
            // if the address vector is empty, set the state and the country
            // to the store's state and country from parameters
            String storeState = CustomerUtilities.getStoreState(pm);
            String storeCountry = CustomerUtilities.getStoreCountry(pm);

            countryIndex = utility.getCountryIndex(storeCountry, pm);
            model.setCountryIndex(countryIndex);
            model.setStateIndex(utility.getStateIndex(countryIndex, storeState.substring(3, storeState.length()), pm));
        }

        // get customer phone list
        for (int i = PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR.length - 1; i >= 0; i--)
        {
            phone = customer.getPhoneByType(i);
            if (phone != null)
            {
                model.setPhoneNumber(phone.getPhoneNumber(), phone.getPhoneType());
                model.setPhoneType(phone.getPhoneType());
            }
        }

        return model;
    }
	// Chnages start for Rev 1.0
	  protected boolean isSystemOffline(UtilityIfc utility) {
			DispatcherIfc d = Gateway.getDispatcher();
			DataManagerIfc dm = (DataManagerIfc) d.getManager(DataManagerIfc.TYPE);
			boolean offline = true;
			try {
				if (dm.getTransactionOnline(UtilityManagerIfc.CLOSE_REGISTER_TRANSACTION_NAME)
						|| dm.getTransactionOnline(UtilityManagerIfc.CLOSE_STORE_REGISTER_TRANSACTION_NAME)) {
					offline = false;
				}
			} catch (DataException e) {
				e.printStackTrace();
			}
			return offline;

		}
		// Chnages ends for Rev 1.0
	  //Change for Rev 1.4 : Starts
	  private HashMap<Integer,MAXGSTRegion> getGSTMapping() {
	    	MAXGSTRegionMappingTransactions gstMappingTransaction = new MAXGSTRegionMappingTransactions();
			//MAXConfigParametersIfc configParameters = null;
			gstMappingTransaction = (MAXGSTRegionMappingTransactions) DataTransactionFactory
					.create(MAXDataTransactionKeys.GSTMappingTransaction);

			try {
				return gstMappingTransaction.readRegionFromMaping();
				
			} catch (DataException e1) {
				e1.printStackTrace();
			}
			return null;
		}

	    private String readHomeState(String storeId) {
	    	MAXReadHomeStateTransactions gstMappingTransaction = new MAXReadHomeStateTransactions();
			//MAXConfigParametersIfc configParameters = null;
			gstMappingTransaction = (MAXReadHomeStateTransactions) DataTransactionFactory
					.create(MAXDataTransactionKeys.ReadHomeStateTransactions);

			try {
				return gstMappingTransaction.readHomeState(storeId);
				
			} catch (DataException e1) {
				e1.printStackTrace();
			}
			return null;
		}
	  //Change for Rev 1.4 : Ends
}
