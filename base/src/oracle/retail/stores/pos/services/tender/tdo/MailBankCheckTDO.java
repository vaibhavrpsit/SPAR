/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/MailBankCheckTDO.java /main/22 2012/09/12 11:57:20 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    hyin      05/18/12 - rollback changes made to CustomerUI for AddressType.
 *                         Change required field to phone number from
 *                         postalcode.
 *    cgreene   04/03/12 - removed deprecated methods
 *    asinton   03/26/12 - Customer UI changes to accomodate multiple
 *                         addresses.
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mkochumm  11/17/08 - cleanup based on i18n changes
 *    abondala  11/06/08 - updated files related to reason codes
 *    abondala  11/05/08 - updated files related to reason codes
 *
 * ===========================================================================
 * $Log:
 *  4    360Commerce 1.3         12/17/2006 4:08:50 PM  Brett J. Larsen CR
 *       21298 - country code appearing where country name should appear
 *  3    360Commerce 1.2         3/31/2005 4:28:59 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:23:22 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:12:29 PM  Robert Pearse
 * $
 * Revision 1.33  2004/07/22 21:54:00  crain
 * @scr 6426 Tendering with Mail Bank Check causes the printer to go offline
 *
 * Revision 1.32  2004/07/13 18:51:15  bwf
 * @scr 6199 Remove spaces from keys
 *
 * Revision 1.31  2004/07/06 20:15:05  crain
 * @scr 6004 System crashes when redeeming a gift certificate for Mail Bank Check
 *
 * Revision 1.30  2004/06/24 23:50:48  crain
 * @scr 5837 Tender Redeem_ Gift cert redeem to mail bank check crashes system
 *
 * Revision 1.29  2004/05/11 15:58:45  aschenk
 * @scr 4711 - MBC Customer screen hads blank fields when returned to by using esc.  Now it is repopulated with the customer info if there was one.
 *
 * Revision 1.28  2004/05/07 15:33:05  aschenk
 * @scr 4694 - Business Name field was getting deleted when returning from Invalid Postal Code Notice.
 *
 * Revision 1.27  2004/03/16 18:30:47  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.26  2004/03/09 20:26:17  bjosserand
 * @scr 0 Mail Bank Check
 * Revision 1.25 2004/02/27 23:17:44 bjosserand @scr 0 Mail Bank Check Revision 1.24
 * 2004/02/27 22:08:04 bjosserand @scr 0 Mail Bank Check Revision 1.23 2004/02/27 20:59:05 bjosserand @scr 0 Mail Bank
 * Check Revision 1.22 2004/02/27 16:39:40 bjosserand @scr 0 Mail Bank Check Revision 1.21 2004/02/27 01:13:01
 * bjosserand @scr 0 Mail Bank Check
 *
 * Revision 1.20 2004/02/27 00:20:16 bjosserand @scr 0 Mail Bank Check Revision 1.19 2004/02/25 20:26:44 bjosserand
 * @scr 0 Mail Bank Check Revision 1.18 2004/02/24 23:41:48 bjosserand @scr 0 Mail Bank Check Revision 1.17 2004/02/24
 * 20:09:50 bjosserand @scr 0 Mail Bank Check Revision 1.16 2004/02/23 22:38:50 bjosserand @scr 0 Mail Bank Check
 *
 * Revision 1.15 2004/02/22 22:01:09 bjosserand @scr 0 Mail Bank Check Revision 1.14 2004/02/18 20:24:19 bjosserand
 * @scr 0
 *
 * Revision 1.13 2004/02/17 23:02:22 bjosserand @scr 0
 *
 * Revision 1.12 2004/02/17 22:54:54 bjosserand @scr 0
 *
 * Revision 1.11 2004/02/17 22:22:23 bjosserand @scr 0
 *
 * Revision 1.10 2004/02/17 19:26:17 epd @scr 0 Code cleanup. Returned unused local variables.
 *
 * Revision 1.9 2004/02/16 21:44:23 bjosserand @scr 0
 *
 * Revision 1.8 2004/02/16 19:57:06 bjosserand @scr 0
 *
 * Revision 1.7 2004/02/15 22:30:03 bjosserand @scr 0
 *
 * Revision 1.6 2004/02/13 18:21:32 bjosserand @scr 0
 *
 * Revision 1.5 2004/02/13 15:09:44 bjosserand @scr 0 Revision 1.4 2004/02/12 16:48:25 mcs Forcing head revision
 *
 * Revision 1.3 2004/02/12 00:47:15 bjosserand @scr 0
 *
 * Revision 1.2 2004/02/11 21:23:20 rhafernik @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:12 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.3 Feb 09 2004 14:23:06 bjosserand Mail Bank Check.
 *
 * Rev 1.2 Feb 06 2004 17:18:32 bjosserand Mail Bank Check.
 *
 * Rev 1.1 Feb 05 2004 14:28:02 bjosserand Mail Bank Check.
 *
 * Rev 1.0 Feb 01 2004 13:41:04 bjosserand Initial revision.
 *
 * Rev 1.2 Nov 19 2003 15:59:14 cdb Added verification of buttons to be enabled and disabled. Resolution for 3465:
 * House Account Enrollment during Return By Item Hangs App
 *
 * Rev 1.1 Nov 19 2003 14:11:04 epd TDO refactoring to use factory
 *
 * Rev 1.0 Nov 04 2003 11:19:12 epd Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.tdo;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.Address;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.ReturnableTransactionADOIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This is a tour helper class for Mail Bank Check operations.
 */
public class MailBankCheckTDO extends TDOAdapter implements TDOUIIfc
{
    //    attributeMap constants
    public static final String BUS = "Bus";
    public static final String TRANSACTION = "Transaction";
    public static final String ORIG_RETURN_TXNS = "OriginalReturnTransactions";

    /**
     * The transaction to which tender line items are added
     */
    protected TenderableTransactionIfc transaction = null;

    /**
     * the customer
     */
    protected CustomerIfc customer;

    protected boolean customerLink = false;

    /**
     *
     * Build UI bean model object from tender attributes.
     *
     * @param HashMap
     * @return POSBaseBeanModel
     */
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        BusIfc bus = (BusIfc) attributeMap.get(BUS);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        RetailTransactionADOIfc transADO = (RetailTransactionADOIfc) attributeMap.get(TRANSACTION);

        TenderCargo cargo = (TenderCargo) bus.getCargo();

        attributeMap = cargo.getTenderAttributes();
        // don't build new tender attributes HashMap
        attributeMap.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.MAIL_CHECK);

        // Get RDO version of transaction
        TenderableTransactionIfc trans = (TenderableTransactionIfc) ((ADO) transADO).toLegacy();
        if (trans == null)
        {
            trans = cargo.getTenderableTransaction();
        }
        else
        {
            cargo.setTransaction(trans);
            // make sure transaction is set in cargo for future use ???
        }

        CustomerIfc customerFromTransADO = null;

        customerFromTransADO = transADO.getCustomer();

        MailBankCheckInfoBeanModel model = new MailBankCheckInfoBeanModel();
        model.setContactInfoOnly(true);
        model.setMailBankCheck(true);

        // obtain and set ID types list
        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        CodeListIfc personalIDTypes =  utility.getReasonCodes(cargo.getOperator().getStoreID(), CodeConstantsIfc.CODE_LIST_MAIL_BANK_CHECK_ID_TYPES);

        model.setReasonCodes(personalIDTypes.getTextEntries(lcl));
        model.setSelectedReasonCode(0);
        model.setChangeState(true);

        CustomerIfc customer;
        if (cargo.isFindOrAddOrUpdateLinked())
        {
            //cargo.setFindOrAddOrUpdateLinked(false); // turn off flag
            customer = cargo.getCustomer();
        }
        else
        {
            customer = customerFromTransADO;
        }

        if (customer == null)
        {
            // Set default values on the model
            model = setModeldefaultValues(model, bus);

            CustomerIfc prevCustomer = cargo.getCustomer();
            if (prevCustomer != null)
            {
                AddressIfc address = prevCustomer.getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_HOME);
                if (address != null)
                {
                    int countryIndex = utility.getCountryIndex(address.getCountry(), pm);
                    model.setCountryIndex(countryIndex);
                    model.setStateIndex(utility.getStateIndex(countryIndex, address.getState(), pm));
                }
            }
        }
        else
        {
            model.setBusinessCustomer(customer.isBusinessCustomer());

            // if customer data not present (linked offline) then set defaults
            if (customer.getAddressList() == null || customer.getAddressList().isEmpty())
            {
                model = setModeldefaultValues(model, bus);
            }
            else
            {
                // Populate the UI model
                copyFromCustomerToModel(model, customer, bus);

                // get list of all available states and selected country and
                // state
                model.setCountries(utility.getCountriesAndStates(pm));
                AddressIfc address = customer.getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_HOME);
                if (address != null)
                {
                    int countryIndex = utility.getCountryIndex(address.getCountry(), pm);
                    model.setCountryIndex(countryIndex);
                    model.setStateIndex(utility.getStateIndex(countryIndex, address.getState(), pm));
                }
            }
        }


        NavigationButtonBeanModel navigationModel = new NavigationButtonBeanModel();
        if (cargo.getTransType() == TransactionIfc.TYPE_VOID)
        {
            navigationModel.setButtonEnabled(CommonActionsIfc.UNDO, false);
        }
        else
        {
            navigationModel.setButtonEnabled(CommonActionsIfc.UNDO, true);
        }

        //      set prompt not editable if this return was NOT with a receipt
        if (transADO instanceof ReturnableTransactionADOIfc
            && !((ReturnableTransactionADOIfc) transADO).isReturnWithReceipt())
        {
            PromptAndResponseModel parModel = new PromptAndResponseModel();
            parModel.setResponseEditable(false);
            model.setPromptAndResponseModel(parModel);
        }

        if (customerFromTransADO != null)
        {
            // if customer is linked to transaction, allow Update option.
            navigationModel.setButtonEnabled(CommonActionsIfc.UPDATE, true);
        }
        else
        {
            navigationModel.setButtonEnabled(CommonActionsIfc.UPDATE, false);
        }

        model.setLocalButtonBeanModel(navigationModel);
        model.setChangeState(false);
        model.setPhoneTypes(CustomerUtilities.getPhoneTypes(utility));

        return model;
    }

    /**
     * Copies the customer information from a Customer object to the UI model.
     *
     * @param CustomerInfoBeanModel
     * @param CustomerIfc
     * @param BusIfc
     */
    //--------------------------------------------------------------------------
    public void copyFromCustomerToModel(CustomerInfoBeanModel model, CustomerIfc customer, BusIfc bus)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        if (customer != null)
        {
            model.setBusinessCustomer(customer.isBusinessCustomer());
            model.setFirstName(customer.getFirstName());
            model.setLastName(customer.getLastName());
            model.setSelectedReasonCode(0); // reset ID type to first one
            model.setSelected(false);

            AddressIfc address = null;
            if (customer.isBusinessCustomer())
            {
                address = customer.getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_WORK);
            }
            else
            {
                address = customer.getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_HOME);
            }

            //    If we couldn't get an address entry by type, get all address entries and use 1st, if available
            List<AddressIfc> addresses = customer.getAddressList();
            if (address == null)
            {
                if ((addresses != null) && (addresses.size() > 0))
                {
                    address = (AddressIfc) addresses.get(0);
                }
                else
                {
                    getLogger().error(
                        "MailBankCheckTDO.copyFromCustomerToModel() - no address entries attached to customer");
                }
            }

            if (address != null)
            {
                Vector addressLines = address.getLines();

                if ((addressLines.size() > 0) && (addressLines.elementAt(0) != null))
                {
                    model.setAddressLine1((String) addressLines.elementAt(0));
                }
                if ((addressLines.size() > 1) && (addressLines.elementAt(1) != null))
                {
                    model.setAddressLine2((String) addressLines.elementAt(1));
                }
                if ((addressLines.size() > 2) && (addressLines.elementAt(2) != null))
                {
                    model.setAddressLine3((String) addressLines.elementAt(2));
                }

                model.setCity(address.getCity());
                int countryIndex = utility.getCountryIndex(address.getCountry(), pm);
                model.setCountryIndex(countryIndex);
                model.setStateIndex(utility.getStateIndex(countryIndex, address.getState(), pm));
                model.setPostalCode(address.getPostalCode());
            }

            PhoneIfc customerPhone = null;

            if (customer.isBusinessCustomer())
            {
                //model.setOrgName(customer.getCompanyName());
                model.setOrgName(customer.getCustomerName());
                customerPhone = customer.getPhoneByType(PhoneConstantsIfc.PHONE_TYPE_WORK);
            }
            else
            {
                customerPhone = customer.getPhoneByType(PhoneConstantsIfc.PHONE_TYPE_HOME);
            }

            // If we couldn't get a phone entry by type, get all phone entries and use 1st, if available
            if (customerPhone == null)
            {
                List<PhoneIfc> phones = customer.getPhoneList();
                if ((phones != null) && (phones.size() > 0))
                {
                    customerPhone = (PhoneIfc) phones.get(0);
                }
                else
                {
                    // No phone entries attached to customer...
                    // Nothing to do here, phone is not a required field
                }
            }

            //If we STILL couldn't get a valid phone entry, leave phone number field alone
            if (customerPhone != null)
            {
                model.setTelephoneNumber(customerPhone.getPhoneNumber());
            }

        }
        else
        {
            System.out.println("copyFromCustomerToModel - customer is null");
        }
    }

    /**
     * Copies the UI information into the tender attributes that will be used to create the TenderMailBankCheckADO
     * object.
     *
     * @param CustomerInfoBeanModel
     * @param HashMap
     */
    //--------------------------------------------------------------------------
    public void copyFromModelToMap(CustomerInfoBeanModel model, HashMap attributeMap)
    {
        attributeMap.put(TenderConstants.FIRST_NAME, model.getFirstName());
        attributeMap.put(TenderConstants.LAST_NAME, model.getLastName());
        attributeMap.put(TenderConstants.COUNTRY, model.getCountry());
        attributeMap.put(TenderConstants.STATE, model.getState());
        attributeMap.put(TenderConstants.CITY, model.getCity());
        attributeMap.put(TenderConstants.ADDRESS_1, model.getAddressLine1());
        attributeMap.put(TenderConstants.ADDRESS_2, model.getAddressLine2());
        attributeMap.put(TenderConstants.ADDRESS_3, model.getAddressLine3());
        attributeMap.put(TenderConstants.POSTAL_CODE_1, model.getPostalCode());
        attributeMap.put(TenderConstants.BUSINESS_CUSTOMER, new Boolean(model.isBusinessCustomer()));
        attributeMap.put(TenderConstants.ID_TYPE, model.getSelectedReason());

        int phoneType;
        if (model.isBusinessCustomer())
        {
            attributeMap.put(TenderConstants.BUSINESS_NAME, model.getOrgName());
            phoneType = PhoneConstantsIfc.PHONE_TYPE_WORK;
        }
        else
        {
            attributeMap.put(TenderConstants.BUSINESS_NAME, "  ");
            phoneType = PhoneConstantsIfc.PHONE_TYPE_HOME;
        }

        PhoneIfc phone = null;

        PhoneIfc[] phones = model.getPhoneList();
        if (phones != null)
        {
            for (int i = 0; i < phones.length; i++)
            {
                if (phones[i].getPhoneType() == phoneType)
                {
                    phone = phones[i];
                    attributeMap.put(TenderConstants.PHONE_NUMBER, phone.getPhoneNumber());
                    break;
                }
            }
        }

        attributeMap.put(TenderConstants.ID_TYPE, model.getSelectedReason());
    }

    /**
     * Copies the customer information into the tender attributes that will be used to create the
     * TenderMailBankCheckADO object.
     *
     * @param CustomerIfc
     * @param HashMap
     */
    //--------------------------------------------------------------------------
    public void copyFromCustomerToMap(CustomerIfc customer, HashMap attributeMap)
    {
        if (customer != null)
        {
            attributeMap.put(TenderConstants.BUSINESS_CUSTOMER, new Boolean(customer.isBusinessCustomer()));
            attributeMap.put(TenderConstants.FIRST_NAME, customer.getFirstName());
            attributeMap.put(TenderConstants.LAST_NAME, customer.getLastName());

            int addressType;
            int phoneType;
            if (customer.isBusinessCustomer())
            {
                attributeMap.put(TenderConstants.BUSINESS_NAME, customer.getCustomerName());
                addressType = AddressConstantsIfc.ADDRESS_TYPE_WORK;
                phoneType = PhoneConstantsIfc.PHONE_TYPE_WORK;
            }
            else
            {
                attributeMap.put(TenderConstants.BUSINESS_NAME, "  ");
                addressType = AddressConstantsIfc.ADDRESS_TYPE_HOME;
                phoneType = PhoneConstantsIfc.PHONE_TYPE_HOME;
            }
            // override phone type with value in tender constants if available
            Integer phoneTypeObj = (Integer) attributeMap.get(TenderConstants.PHONE_TYPE);
            if (phoneTypeObj != null)
            {
                phoneType = phoneTypeObj.intValue();
            }

            AddressIfc address = customer.getAddressByType(addressType);

            //  If we couldn't get an address entry by type, get all address entries and use 1st, if available
            if (address == null)
            {
                List<AddressIfc> addresses = customer.getAddressList();
                if (addresses.size() > 0)
                {
                    address = (AddressIfc) addresses.get(0);
                }
                else
                {
                    getLogger().error("No address entries attached to customer");
                }
            }

            if (address != null)
            {
                Vector addressLines = address.getLines();

                try
                {
                    if ((addressLines.size() > 0) && (addressLines.elementAt(0) != null))
                    {
                        attributeMap.put(TenderConstants.ADDRESS_1, addressLines.elementAt(0));
                    }
                    if ((addressLines.size() > 1) && (addressLines.elementAt(1) != null))
                    {
                        attributeMap.put(TenderConstants.ADDRESS_2, addressLines.elementAt(1));
                    }
                    if ((addressLines.size() > 2) && (addressLines.elementAt(2) != null))
                    {
                        attributeMap.put(TenderConstants.ADDRESS_3, addressLines.elementAt(2));
                    }
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    getLogger().warn("customer address array bounds error");
                }

                attributeMap.put(TenderConstants.CITY, address.getCity());
                attributeMap.put(TenderConstants.COUNTRY, address.getCountry());
                attributeMap.put(TenderConstants.STATE, address.getState());
                attributeMap.put(TenderConstants.POSTAL_CODE_1, address.getPostalCode());
                attributeMap.put(TenderConstants.POSTAL_CODE_2, address.getPostalCodeExtension());
            }
            PhoneIfc phone = null;
            phone = customer.getPhoneByType(phoneType);
            if (phone == null)
            {
                List<PhoneIfc> phones = customer.getPhoneList();
                if ((phones != null) && (phones.size() > 0))
                {
                    phone = (PhoneIfc) phones.get(0);
                }
                else
                { // no phone attached to customer....
                    // Nothing to really do here as phone number is not a required field
                }
            }

            if (phone != null)
            {
                attributeMap.put(TenderConstants.PHONE_NUMBER, phone.getPhoneNumber());
            }
        }
    }

    /**
     * Print out map entries for debugging purposes.
     *
     * @param HashMap
     */ //--------------------------------------------------------------------------
    public void printMap(HashMap attributeMap)
    {
        System.out.println("<<<<<<<<<<<<<Attribute map start>>>>>>>>>>>>>>");
        System.out.println(attributeMap.get(TenderConstants.FIRST_NAME));
        System.out.println(attributeMap.get(TenderConstants.LAST_NAME));
        System.out.println(attributeMap.get(TenderConstants.COUNTRY));
        System.out.println(attributeMap.get(TenderConstants.CITY));
        System.out.println(attributeMap.get(TenderConstants.ADDRESS_1));
        System.out.println(attributeMap.get(TenderConstants.ADDRESS_2));
        System.out.println(attributeMap.get(TenderConstants.ADDRESS_3));
        System.out.println(attributeMap.get(TenderConstants.POSTAL_CODE_1));
        System.out.println(attributeMap.get(TenderConstants.POSTAL_CODE_2));
        System.out.println(attributeMap.get(TenderConstants.BUSINESS_CUSTOMER));
        System.out.println(attributeMap.get(TenderConstants.ID_TYPE));
        System.out.println(attributeMap.get(TenderConstants.BUSINESS_NAME));
        System.out.println(attributeMap.get(TenderConstants.PHONE_NUMBER));
        System.out.println(attributeMap.get(TenderConstants.AMOUNT));
        System.out.println("<<<<<<<<<<<<<Attribute map end>>>>>>>>>>>>>>>>>");
    }

    /**
     * Copies the customer information from the UI model a new Customer object
     *
     * @param CustomerInfoBeanModel
     * @return CustomerIfc
     */
    public CustomerIfc copyFromModelToNewCustomer(CustomerInfoBeanModel model)
    {
        CustomerIfc customer = DomainGateway.getFactory().getCaptureCustomerInstance();
        if (model.isBusinessCustomer())
        {
            customer.setCompanyName(model.getLastName());
            if (model instanceof MailBankCheckInfoBeanModel)
                customer.setCustomerName(model.getCustomerName());
            customer.setBusinessCustomer(true);
        }

        customer.setLastName(model.getLastName());
        customer.setFirstName(model.getFirstName());
        Address address = new Address();
        address.addAddressLine(model.getAddressLine1());
        address.addAddressLine(model.getAddressLine2());
        address.addAddressLine(model.getAddressLine3());
        address.setCity(model.getCity());        
        address.setCountry(model.getCountry());
        address.setState(model.getState());
        address.setPostalCode(model.getPostalCode());
        customer.addAddress(address);
        PhoneIfc[] phones = model.getPhoneList();
        if (phones != null)
        {
            for (int i = 0; i < phones.length; i++)
            {
                customer.addPhone(phones[i]);
            }
        }

        return customer;
    } //    --------------------------------------------------------------------------

    /**
     * Sets the default state, country code and phone type when none is available or the customer was linked offline.
     * Uses the parameter manager to retrieve the values for state and country, sets phone type to Home.
     *
     * @param ui
     *            model to set the data
     * @param bus
     *            the bus arriving at this site to get the parameter manager
     * @return updated model
     */ //-------------------------------------------------------------------------
    protected MailBankCheckInfoBeanModel setModeldefaultValues(MailBankCheckInfoBeanModel model, BusIfc bus)
    {
        // Get defaults for state and country code from parameter mgr
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        String defaultState = CustomerUtilities.getStoreState(pm);
        String defaultCountry = CustomerUtilities.getStoreCountry(pm);
        // get list of all available states and selected country and state
        int countryIndex = utility.getCountryIndex(defaultCountry, pm);
        model.setCountryIndex(countryIndex);
        model.setStateIndex(utility.getStateIndex(countryIndex, defaultState.substring(3, defaultState.length()), pm));
        model.setCountries(utility.getCountriesAndStates(pm));
        model.setSelectedReasonCode(0);
        // reset ID type to first one
        model.setSelected(true);
        model.setChangeState(true);
        model.setPhoneTypes(CustomerUtilities.getPhoneTypes(utility));
        if (model.isBusinessCustomer())
        {
            model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_WORK);
        }
        else
        {
            model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_HOME);
        }

        return (model);
    }

    /**
     *
     * Null return implementation.
     */
    public String formatPoleDisplayLine1(RetailTransactionADOIfc txnADO)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     *
     * Null return implementation.
     */
    public String formatPoleDisplayLine2(RetailTransactionADOIfc txnADO)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
