/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*	
*
*	Rev 1.2     Feb 18, 2016		Ashish Yadav		Changes done for Bug fixing business customer
*	Rev 1.1     Oct 21, 2016		Mansi Goel			Changes for Customer FES
*	Rev 1.0     Oct 17, 2016		Ashish Yadav		Code Merging
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.customer.common;

// java imports
import java.util.Vector;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.pos.manager.ifc.MAXUtilityManagerIfc;
import max.retail.stores.pos.ui.beans.MAXCustomerInfoBeanModel;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.CustomerReadDataTransaction;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.EmailAddressConstantsIfc;
import oracle.retail.stores.domain.utility.EmailAddressIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.JournalableIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


public class MAXCustomerUtilities extends CustomerUtilities
{
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.customer.common.MAXCustomerUtilities.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 9$";

    /**
       Default maximum matches to use when parameter is not availible
    **/
    protected static final int DEFAULT_MAXIMUM_MATCHES = 20;

    /**
       The parameter name for the maximum number of matches returned.
    **/
    protected static final String PARAMETER_MAXIMUM_MATCHES = "CustomerMaximumMatches";

    /**
       The parameter name for the store state/province.
    **/
    protected static final String PARAMETER_STORE_STATE_PROVINCE = "StoreStateProvince";

    /**
       The parameter name for the store state/province.
    **/
    protected static final String PARAMETER_STORE_COUNTRY = "StoreCountry";

    //---------------------------------------------------------------------
    /**
       Returns the maximum matches parameter or sets
       the maximum matches to a default. <P>
       @param parameterManager the parameter manager
       @param callingSite      the calling site
       @param employeeID       the employee ID
       @param transactionID    the transaction ID
       @return value of maximum matches
       @deprecated Use getMaximumMatches(ParameterManagerIfc)
    **/
    //---------------------------------------------------------------------
    public static int getMaximumMatches(ParameterManagerIfc parameterManager,
                                        String callingSite,
                                        String employeeID,
                                        String transactionID)
    {
        return(getMaximumMatches(parameterManager));
    }

    //---------------------------------------------------------------------
    /**
       Returns the maximum matches parameter or sets
       the maximum matches to a default. <P>
       @param parameterManager the parameter manager
       @return value of maximum matches
    **/
    //---------------------------------------------------------------------
    public static int getMaximumMatches(ParameterManagerIfc parameterManager)
    {
        // look up maximum matches parameter
        int maxMatches = DEFAULT_MAXIMUM_MATCHES; //initialize to default
        try
        {                           //begin try maximum matches parameter
            String maximumMatches = parameterManager.getStringValue(PARAMETER_MAXIMUM_MATCHES);
            maxMatches = Integer.parseInt(maximumMatches);
        }                           // end try maximum matches parameter
        catch (ParameterException pe)
        {
            logger.error( "Parameter Exception.");
            logger.error( "" + pe + "");
        }
        catch (NumberFormatException e)
        {
            logger.error( "NumberFormatException.");
            logger.error( "" + e + "");
        }

        return(maxMatches);
    }

    //---------------------------------------------------------------------
    /**
       Returns the store state parameter<P>
       @param parameterManager the parameter manager
       @param employeeID       the employee ID
       @param transactionID    the transaction ID
       @return value of state
       @deprecated Use getStoreState(ParameterManagerIfc)
    **/
    //--------------------------------------------------------------------------
    public static String getStoreState(ParameterManagerIfc parameterManager,
                                       String employeeID,
                                       String transactionID)
    {
        return(getStoreState(parameterManager));
    }

    //---------------------------------------------------------------------
    /**
       Returns the store state parameter<P>
       @param parameterManager the parameter manager
       @return value of state
    **/
    //---------------------------------------------------------------------
    public static String getStoreState(ParameterManagerIfc parameterManager)
    {
        // look up StoreStateProvince parameter
        String state = null;

        try
        {
            state = parameterManager.getStringValue(PARAMETER_STORE_STATE_PROVINCE);
        }
        catch (ParameterException e)
        {
            logger.error( "Parameter Exception.");
            logger.error( "" + e + "");
        }
        return(state);
    }

    //---------------------------------------------------------------------
    /**
       Returns the store country parameter<P>
       @param parameterManager the parameter manager
       @param employeeID       the employee ID
       @param transactionID    the transaction ID
       @return value of country
       @deprecated Use getStoreCountry(ParameterManagerIfc)
    **/
    //----------------------------------------------------------------------
    public static String getStoreCountry(ParameterManagerIfc parameterManager,
                                         String employeeID,
                                         String transactionID)
    {
        return(getStoreCountry(parameterManager));
    }

    //---------------------------------------------------------------------
    /**
       Returns the store country parameter<P>
       @param parameterManager the parameter manager
       @return value of country
    **/
    //----------------------------------------------------------------------
    public static String getStoreCountry(ParameterManagerIfc parameterManager)
    {
        // look up StoreCountry parameter
        String country = null;

        try
        {                           //begin try maximum matches parameter
            country = parameterManager.getStringValue(PARAMETER_STORE_COUNTRY);
        }                           // end try maximum matches parameter
        catch (ParameterException e)
        {
            logger.error( "Parameter Exception.");
            logger.error( "" + e + "");
        }
        return( country );
    }

    //---------------------------------------------------------------------
    /**
       Returns the store country parameter<P>
       @param parameterManager the parameter manager
       @return value of country
    **/
    //----------------------------------------------------------------------
    public static String[] getPhoneTypes(UtilityManagerIfc utility)
    {
        // look up StoreCountry parameter
        String[] phoneTypes= new String[PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR.length];

        for(int i=0;i<PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR.length;i++)
        {
            phoneTypes[i] = utility.retrieveCommonText(PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR[i]);
        }

        return(phoneTypes);
    }
   //---------------------------------------------------------------------
    /**
       Updates the customer address and phone information.  This method
       is called from multiple Lane Actions and resides here for
       minimization of repeated code.
       <p>
       @param customer the customer
       @param model    the CustomerAddressBeanModel object
       @deprecated as of release 6.0 replace use the signature that
       passes CustomerInfoBeanModel as a parameter instead of this one
    **/
    //---------------------------------------------------------------------
   /* public static void updateAddressAndPhone(CustomerIfc customer, CustomerAddressBeanModel model)
    {
        customer = updateAddressAndPhone(customer, (CustomerInfoBeanModel) model);
    }*/
    //---------------------------------------------------------------------
    /**
       Updates the customer address and phone information.  This method
       is called from multiple Lane Actions and resides here for
       minimization of repeated code.
       <p>
       @param customer the customer
       @param model    the CustomerInfoBeanModel object
       @return CustomerIfc the modified customer object
    **/
    //---------------------------------------------------------------------
    public static CustomerIfc updateAddressAndPhone(CustomerIfc customer, CustomerInfoBeanModel model)
    {
        // Update Address and Phone objects
        AddressIfc address = customer.getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_HOME);

        if (address == null)
        {
             address = DomainGateway.getFactory().getAddressInstance();
        }
        Vector linesVector = new Vector(3);
        linesVector.addElement(model.getAddressLine1());
        linesVector.addElement(model.getAddressLine2());
        linesVector.addElement(model.getAddressLine3());

        address.setLines(linesVector);
        address.setCity(model.getCity());
        address.setState(model.getState());
        address.setCountry(model.getCountryName());
        address.setPostalCode(model.getPostalCode());
 // Changes start for code merging(commenting below line)
        // deprecated in 14 version
        //address.setPostalCodeExtension(model.getExtPostalCode());
 // Changes end for code merging
        address.setAddressType(AddressConstantsIfc.ADDRESS_TYPE_HOME);

        // Replace the old address(es) with the new one
        Vector addressVector = new Vector();
        addressVector.add(address);

        customer.setAddresses(addressVector);
        updatePhoneInfo(customer, model);
        return customer;

    }

   //---------------------------------------------------------------------
    /**
        Updates the phone information from the bean model.
        <p>
        @param customer the customer
        @param model the CustomerInfoBeanModel object
    **/
    //---------------------------------------------------------------------
    public static void updatePhoneInfo(CustomerIfc customer,
                                CustomerInfoBeanModel model)
    {
        // Get customer's telephone information.

        PhoneIfc phones[]  = model.getPhoneList();
        Vector phoneVector = new Vector();
        if ( phones != null )
        {
            for (int i = 0; i < phones.length; i++)
            {
               if (phones[i] != null)
               {
                   phoneVector.addElement(phones[i]);
               }
            }
            // convert to vector;
            customer.setPhones(phoneVector);
        }

     }
    
    public  static CustomerIfc updateCustomer(MAXCustomerIfc customer, MAXCustomerInfoBeanModel model)
    {

        if (customer == null)
        {
            customer = (MAXCustomerIfc) DomainGateway.getFactory().getCustomerInstance();
        }
        //Update customer name and id
        if (model.isBusinessCustomer())
        {
            customer.setBusinessCustomer(true);
            customer.setTaxCertificate(model.getTaxCertificate());
            if (model.isSelected() && model.getReasonCodeTags() != null && model.getSelectedIndex() > -1)
            {
               String reasonCode = model.getReasonCodeTags()[model.getSelectedIndex()];
               customer.setTaxExemptionReason(DomainGateway.getFactory().getLocalizedCode());
            }
        }
        else
        {
          customer.setBusinessCustomer(false);
          customer.setFirstName(model.getFirstName());
          customer.setLastName(model.getLastName());
        }
        customer.setCustomerName(model.getCustomerName());
		
        if(model.isBusinessCustomer())
        	 customer.setCustomerName(model.getLastName());
        
        customer.setEmployeeID(model.getEmployeeID());
        // Update Address and Phone objects
        customer = (MAXCustomerIfc) updateAddressAndPhone(customer, model);
        // set the Customer's email address
        EmailAddressIfc  email = customer.getEmailAddress(EmailAddressConstantsIfc.EMAIL_ADDRESS_TYPE_HOME);
        email.setEmailAddress(model.getEmail());
        customer.setEmailAddress(email);

        // Prefix is storeID
        String storeID = Gateway.getProperty("application", "StoreID", null);
        customer.setCustomerIDPrefix(storeID);
        // Update customer discount
        return(customer);

    }

    //--------------------------------------------------------------------------
    /**
        Retrieve Customer data into a mailbank check model
        @param customerModel a CustomerInfoBeanModel containing the customer data
    **/
    //--------------------------------------------------------------------------
    public static MailBankCheckInfoBeanModel copyCustomerToModel(CustomerIfc customer,
                                                           UtilityManagerIfc utility,
                                                           ParameterManagerIfc pm)
    {
        CustomerInfoBeanModel customerModel = getCustomerInfo(customer,utility, pm);
        MailBankCheckInfoBeanModel model = new MailBankCheckInfoBeanModel();

        model.setBusinessCustomer(customerModel.isBusinessCustomer());
        if (customerModel.isBusinessCustomer())
        {
            model.setOrgName(customerModel.getOrgName());
            model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_WORK);
            model.setTaxCertificate(customerModel.getTaxCertificate());
            model.setReasonCodes(customerModel.getReasonCodes());
            model.setReasonCodeTags(getTaxExceptionsTags((MAXUtilityManagerIfc) utility));
            model.setSelectedReasonCode(customerModel.getSelectedReason());
            model.setSelected(customerModel.isSelected());
        }
        else
        {
            model.setFirstName(customerModel.getFirstName());
            model.setLastName(customerModel.getLastName());
            model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_HOME);
        }
        model.setCustomerName(customerModel.getCustomerName());
        model.setPhoneList(customerModel.getPhoneList());
        model.setPhoneTypes(customerModel.getPhoneTypes());

        model.set3LineAddress(customerModel.is3LineAddress());
        model.setAddressLine1(customerModel.getAddressLine1());
        model.setAddressLine2(customerModel.getAddressLine2());
        model.setAddressLine3(customerModel.getAddressLine3());
        model.setCity(customerModel.getCity());
        model.setCountryIndex(customerModel.getCountryIndex());
        model.setCountries(customerModel.getCountries());
        model.setStateIndex(customerModel.getStateIndex());

        model.setCustomerID(customerModel.getCustomerID());
        model.setEmployeeID(customerModel.getEmployeeID());
        // Changes for Rev 1.0 : Starts
        // deprecated in version 14
       // model.setExtPostalCode(customerModel.getExtPostalCode());
        // // Changes for Rev 1.0 : Ends
        model.setEmail(customerModel.getEmail());
        model.setPostalCode(customerModel.getPostalCode());

        return model;
    }
    //----------------------------------------------------------------------
    /**
        Retrieves the customer groups available.
        @return CustomerGroupIfc the list of customer groups
    **/
    //----------------------------------------------------------------------
    public static CustomerGroupIfc[] getCustomerGroups()
    {
            // get available customer groups

        CustomerGroupIfc[] groups = null;
        try
        {
 /*changes start for code merging(added variable beacuse 
  getCustomerGroups() expect some argument which is missing here but it is present in base 14)*/
        	LocaleRequestor localeReq;
// Changes ends for code merging
            CustomerReadDataTransaction ct = null;

            ct = (CustomerReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.CUSTOMER_READ_DATA_TRANSACTION);
// Changes starts for code merging(base 14 accept LocaleRequestor as an parameter)
           // groups = ct.selectCustomerGroups(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
            groups = ct.selectCustomerGroups(new LocaleRequestor(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
// Changes ends for code merging
        }
        catch (DataException e)
        {
            // if exception getting available group occurs, essentially ignore it
            logger.warn(
                        "DataException occurred retrieving customer customer groups.");
        }

        return groups;
    }

    //----------------------------------------------------------------------
    /**
        Retrieves the list of taxt exceptions for business customer
        @return Vector list of exceptions
    **/
    //----------------------------------------------------------------------
    public static Vector getTaxExceptions(MAXUtilityManagerIfc utility)
    {
           CodeListIfc rcl = utility.getReasonCodes("CORP",CodeConstantsIfc.CODE_LIST_TAX_EXEMPT_REASON_CODES);
           return  utility.getReasonCodeTextEntries(rcl);
    }

      //----------------------------------------------------------------------
    /**
        Retrieves the list of taxt exceptions for business customer
        @return Vector list of exceptions
    **/
    //----------------------------------------------------------------------
    public static String[] getTaxExceptionsTags(MAXUtilityManagerIfc utility)
    {
           CodeListIfc rcl = utility.getReasonCodes("CORP",CodeConstantsIfc.CODE_LIST_TAX_EXEMPT_REASON_CODES);
           CodeEntryIfc[] entries = rcl.getEntries();
           String[] textEntries = new String[entries.length];
           for (int i=0 ; i < entries.length;i++)
           {
             textEntries[i] = entries[i].getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
           }
           return  textEntries;
    }
    //---------------------------------------------------------------------
    /**
       Makes a journal entry to indicate when the operator is
       entering the customer service. <p>
       @param employeeID       the employee ID of the operator
       @param transactionID    the current transaction ID (if applicable)
    **/
    //---------------------------------------------------------------------
    public static void journalCustomerEnter(String employeeID, String transactionID)
    {
        String journalText = Util.EOL + "Entering Customer";

        // get the Journal manager
        JournalManagerIfc jmi = (JournalManagerIfc)Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);

        // Journal the entry.
        if (jmi != null)
        {
            if (transactionID == null)
            {
                jmi.setEntryType(JournalableIfc.ENTRY_TYPE_START);
            }
            else
            {
                jmi.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);
            }
            jmi.journal(employeeID, transactionID, journalText);
            jmi.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);

        }
        else
        {
            logger.warn( "No journal manager found!");
        }
    }

    //---------------------------------------------------------------------
    /**
       Makes a journal entry to indicate when a customer has been link
       @param employeeID       the employee ID of the operator
       @param customerID       the customer ID
       @param transactionID    the current transaction ID (if applicable)
    **/
    //--------------------------------------------------------------------------
     public static void journalCustomerLink(String employeeID, String customerID, String transactionID)
     {
            StringBuffer journalText = new StringBuffer();
            journalText.append("  Link customer ").append(customerID);

            // get the Journal manager
            JournalManagerIfc jmi = (JournalManagerIfc)Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);
            if (jmi != null)
            {

                jmi.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);

                jmi.journal(employeeID,transactionID, journalText.toString());


            }
            else
            {
                logger.error( "No journal manager found!");
            }
     }
    //---------------------------------------------------------------------
    /**
       Makes a journal entry to indicate when the operator is
       exiting the customer service.
       @param employeeID       the employee ID of the operator
       @param transactionID    the current transaction ID (if applicable)
    **/
    //--------------------------------------------------------------------------
    public static void journalCustomerExit(String employeeID, String transactionID)
    {
        String journalText = "Exiting Customer";

        // get the Journal manager
        JournalManagerIfc jmi = (JournalManagerIfc)Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);

        // Journal the entry.
        if (jmi != null)
        {
            if(jmi.getEntryType() == JournalableIfc.ENTRY_TYPE_CUST)
            {
                jmi.setEntryType(JournalableIfc.ENTRY_TYPE_NOTTRANS);
                jmi.journal(employeeID, transactionID, journalText);
            }
            else
            {
                jmi.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);
            }

            jmi.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);
        }
        else
        {
            logger.warn( "No journal manager found!");
        }
    }

    
    // Changes for Rev 1.1 : Starts
    public static MAXCustomerInfoBeanModel populateCustomerInfoBeanModel(CustomerIfc customer, UtilityManagerIfc utility, ParameterManagerIfc pm, MAXCustomerInfoBeanModel model)
    {
        // Retrieve default home phone
        if (customer != null)
        {
            if (customer.isBusinessCustomer())
            {
                model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_WORK);
                model.setEncipheredTaxCertificate(customer.getEncipheredTaxCertificate());
                model.setReasonCodes(getTaxExemptions(utility, Gateway.getProperty("application", "StoreID", "")));
                model.setReasonCodeTags(getTaxExemptionsTags(utility, Gateway.getProperty("application", "StoreID", "")));
                CodeListIfc reasons = utility.getReasonCodes(Gateway.getProperty("application", "StoreID", ""),
                        CodeConstantsIfc.CODE_LIST_TAX_EXEMPT_REASON_CODES);
                model.setReasonCodeKeys(reasons.getKeyEntries());
                model.setSelected(false);
                if (customer.getTaxExemptionReason() != null)
                {
                    String reason = customer.getTaxExemptionReason().getCode();

                    if (!Util.isEmpty(reason) && !reason.equals(LocalizedCodeIfc.CODE_UNDEFINED))
                    {
                        model.setSelectedReasonCode(utility.retrieveCommonText(reason));
                        model.setSelected(true);
                        
                        //For cross channel, only reason code is populated. The reason code text should be set here.
                        if ( customer.getTaxExemptionReason().getText() == null || StringUtils.isBlank(customer.getTaxExemptionReason().getText().getText()))
                        {
                            // set up reason code list
                            CodeListIfc rcl = utility.getReasonCodes(Gateway.getProperty("application", "StoreID", ""),
                                    CodeConstantsIfc.CODE_LIST_TAX_EXEMPT_REASON_CODES);

                            LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
                            if (rcl != null)
                            {
                                CodeEntryIfc reasonEntry = rcl.findListEntryByCode(model.getSelectedReasonKey());
                                localizedCode.setCode(reasonEntry.getCode());
                                localizedCode.setText(reasonEntry.getLocalizedText());
                            }
                            else
                            {
                                localizedCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
                            }
                            customer.setTaxExemptionReason(localizedCode);
                        }    
                    }
                }
                model.setBusinessCustomer(true);
            }
            else
            {
                model.setFirstName(customer.getFirstName());
                model.setLastName(customer.getLastName());
                model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_HOME);
                model.setBusinessCustomer(false);
                model.setTaxID(customer.getEncipheredTaxID());
            }
            model.setCustomerName(customer.getCustomerName());
            // set the customer names in the model.

            // 3 line address is always true as of 13.1
            model.set3LineAddress(true);
            // set the address in the model; get the primary one
            AddressIfc addr = customer.getPrimaryAddress();

            if (addr != null)
            {
                Vector<String> lines = addr.getLines();
                if (lines.size() >= 1)
                {
                    if (!Util.isEmpty(lines.get(0)) && lines.get(0).trim().length() > 0)
                    {
                        model.setAddressLine1(lines.get(0));
                    }
                }

                if (lines.size() >= 2)
                {
                    if (!Util.isEmpty(lines.get(1)) && lines.get(1).trim().length() > 0)
                    {
                        model.setAddressLine2(lines.get(1));
                    }
                }

                if (lines.size() >= 3 )
                {
                    if (!Util.isEmpty(lines.get(2)) && lines.get(2).trim().length() > 0)
                    {
                        model.setAddressLine3(lines.get(2));
                        model.set3LineAddress(true);
                    }
                }

                if (!Util.isEmpty(addr.getCity()) && addr.getCity().length() > 0)
                {
                    model.setCity(addr.getCity());
                }

                // get list of all available states and selected country and
                // state
                int countryIndx = getCountryIndex(addr.getCountry(), utility, pm);
                model.setCountryIndex(countryIndx);
                if (Util.isEmpty(addr.getState()))
                {
                    model.setStateIndex(-1);
                }
                else
                {
                    model.setStateIndex(utility.getStateIndex(countryIndx, addr.getState(), pm));
                }

                if (!Util.isEmpty(addr.getPostalCode()) && addr.getPostalCode().length() > 0)
                {
                    model.setPostalCode(addr.getPostalCode());
                }
            }

            else
            {
                // if the address vector was empty, set the state and the
                // country
                // to the store's state and country from parameters
                String storeState = CustomerUtilities.getStoreState(pm);
                String storeCountry = CustomerUtilities.getStoreCountry(pm);

                int countryIndx = utility.getCountryIndex(storeCountry, pm);
                model.setCountryIndex(countryIndx);
                model.setStateIndex(utility
                        .getStateIndex(countryIndx, storeState.substring(3, storeState.length()), pm));
            }

            model.setCustomerID(customer.getCustomerID());
            model.setEmployeeID(customer.getEmployeeID());
            // get customer phone list
            PhoneIfc phone = null;
            for (int i = PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR.length - 1; i >= 0; i--)
            {
                phone = customer.getPhoneByType(i);
                if (phone != null && phone.isActive())
                {
                    model.setTelephoneNumber(phone.getPhoneNumber(), phone.getPhoneType());

                    model.setTelephoneType(phone.getPhoneType());
                }

            }

            // set the Customer's email address
            EmailAddressIfc email = customer.getEmailAddress(EmailAddressConstantsIfc.EMAIL_ADDRESS_TYPE_HOME);

            if (!StringUtils.isEmpty(email.getEmailAddress()))
            {
                model.setEmail(email.getEmailAddress());
            }
            
            model.setSelectedReceiptMode(customer.getReceiptPreference());

            populateExtendedCustomerData(customer, model);
        }
        else
        {
            // if customer information is not available setup default fields on
            // the screeen
            String storeState = CustomerUtilities.getStoreState(pm);
            String storeCountry = CustomerUtilities.getStoreCountry(pm);

            int countryIndx = utility.getCountryIndex(storeCountry, pm);
            model.setCountryIndex(countryIndx);
            model.setStateIndex(utility.getStateIndex(countryIndx, storeState.substring(3, storeState.length()), pm));

        }
        model.setCountries(utility.getCountriesAndStates(pm));
        model.setPhoneTypes(getPhoneTypes(utility));      
        model.setReceiptModes(CustomerUtilities.getReceiptPreferenceTypes(utility));
        return model;
    }
    // Changes for Rev 1.1 : Ends
    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.   <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  getClass().getName() (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
    
    // Changes starts for Rev 1.2 (ashish)
    public static MAXCustomerInfoBeanModel getCustomerInfo(CustomerIfc customer, UtilityManagerIfc utility, ParameterManagerIfc pm)
    {
        // model to use for the UI
        MAXCustomerInfoBeanModel model = new MAXCustomerInfoBeanModel();
        return populateCustomerInfoBeanModel(customer, utility, pm, model);
    }
    // Changes ends for Rev 1.2 (ashish)
}
