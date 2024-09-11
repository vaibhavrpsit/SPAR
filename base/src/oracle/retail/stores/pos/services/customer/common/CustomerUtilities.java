/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/CustomerUtilities.java /main/81 2014/06/03 17:06:11 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   06/02/14 - Added support for extended customer data.
 *    tksharma  01/22/14 - updated updateCustomer(..) method to set
 *                         emailAddressType as work for business customer
 *    ohorne    11/22/13 - added ej for mpos add customer
 *    yiqzhao   11/05/13 - Journal business customer tax id, tax certificate
 *                         and tax exempt reason code.
 *    sgu       07/24/13 - remove postal ext code
 *    vbongu    06/24/13 - fix null pointer for setting an email on model
 *    rgour     06/07/13 - marking a phone record as deleted if it is removed
 *    rabhawsa  04/09/13 - reason code -1 is undefined and should be treated as
 *                         model selected as false
 *    mchellap  03/19/13 - Null check for address line fields
 *    mchellap  03/18/13 - Check for empty postal code
 *    abhinavs  03/13/13 - Fix to save correct ejournal tape of customer phone
 *                         number and types.
 *    mkutiana  03/05/13 - Check if postal code is empty.
 *    abhineek  01/31/13 - Added preference field to CusotmerInfo screen
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    mchellap  11/23/12 - Receipt enhancement quickwin changes
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   04/03/12 - removed deprecated methods
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    acadar    08/17/12 - merged to tip
 *    abondala  08/17/12 - fixing the exisitng customer updates and few other
 *                         issues
 *    acadar    08/16/12 - bug fixes
 *    acadar    08/03/12 - moved customer search criteria
 *    acadar    07/20/12 - fix XC issues
 *    acadar    06/28/12 - changes for XChannel
 *    acadar    05/29/12 - changes for cross channel
 *    hyin      05/18/12 - rollback changes made to CustomerUI for AddressType.
 *                         Change required field to phone number from
 *                         postalcode.
 *    acadar    05/10/12 - check for empty phone or email address
 *    asinton   03/26/12 - Customer UI changes to accomodate multiple
 *                         addresses.
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    asinton   11/05/11 - protect an already existing tax ID
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   06/30/11 - Encryption CR
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    rrkohli   12/10/10 - Fixed updated customer info in EJ
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/05/10 - use default locale for currency and date/time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    cgreene   03/05/10 - add check on index size before getting reason codes
 *    abondala  01/03/10 - update header date
 *    asinton   12/11/09 - Implemented EJournaling of Price Promotion for items
 *                         including changes to price by linking a customer.
 *    asinton   09/29/09 - Forward port of 13.1.x defect where customer info
 *                         not journaled for pickup and delivery orders.
 *    blarsen   07/22/09 - XbranchMerge
 *                         blarsen_bug8680500-wrong-rsn-code-saved-for-biz-customer
 *                         from rgbustores_13.1x_branch
 *    blarsen   07/15/09 - Default business customer tax exempt reason code
 *                         always being saved. The wrong reason code was used
 *                         in a comparison. The numeric code is required in
 *                         this case.
 *    asinton   07/01/09 - set the tax exempt reason code to the code value,
 *                         not the user readable string.
 *    asinton   06/24/09 - Fixed (1) the null || empty check in getCustomerInfo
 *                         and (2) set the proper 'reasonCode' in
 *                         updateCustomer.
 *    cgreene   04/14/09 - convert pricingGroupID to integer instead of string
 *    vapartha  04/02/09 - Seeting the TaxId from the screen in the customer
 *                         object only if the model is not from LayAway.
 *    mahising  03/20/09 - Fixed CSP issue if item qty change and customer link
 *                         to the transaction
 *    npoola    03/16/09 - fixed Pricing Groups to display as per the user
 *                         locale
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *    acadar    02/26/09 - populate the telephone number
 *    vchengeg  02/16/09 - Removed multiple occurrances of the string Link
 *                         Customer and retained only one for EJournalling
 *    deghosh   02/05/09 - EJ i18n defect fixes
 *    mahising  01/22/09 - fixed mearge issue
 *    mahising  01/22/09 - fixed business customer search issue
 *    mahising  01/19/09 - fixed reason code issue
 *    aphulamb  01/02/09 - fix delivery issues
 *    mahising  12/23/08 - fix base issue
 *    aphulamb  12/17/08 - bug fixing of PDO
 *    vchengeg  12/08/08 - EJ I18n formatting
 *    mahising  12/04/08 - JUnit fix and SQL fix
 *    aphulamb  11/27/08 - fixed merge issue
 *    aphulamb  11/27/08 - checking files after merging code for receipt
 *                         printing by Amrish
 *    aphulamb  11/24/08 - Checking files after code review by amrish
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    mahising  11/25/08 - updated due to merge
 *    ranojha   11/20/08 - Fixed CustomerUtilities for checking for null
 *                         codeLists and taxExemptions
 *    mkochumm  11/17/08 - cleanup based on i18n changes
 *
 * ===========================================================================
 * $Log:
 |    9    360Commerce 1.8         3/30/2007 4:48:30 AM   Michael Boyd    CR
 |         26172 - v8x merge to trunk
 |
 |         9    .v8x      1.7.1.0     3/12/2007 12:31:02 PM  Maisa De Camargo
 |         Fixed
 |         Reason Code Default Settings.
 |    8    360Commerce 1.7         8/10/2006 7:30:00 AM   Robert Zurga
 |         Merge: 4159: Country Name appearing incorrectly, defect fixed.
 |    7    360Commerce 1.6         2/11/2006 12:38:58 AM  Brett J. Larsen CR
 |         10430 - fixed in 7.2 - merge into trunk - having a phone number
 |         array of 6 w/ "null" entries is not required - cleaning this up
 |         since it has led to confusion in the past
 |    6    360Commerce 1.5         2/11/2006 12:10:23 AM  Brett J. Larsen Merge
 |          from CustomerUtilities.java, Revision 1.4.1.0
 |    5    360Commerce 1.4         1/26/2006 3:40:56 AM   Brett J. Larsen merge
 |          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 |    4    360Commerce 1.3         12/14/2005 4:12:39 AM  Barry A. Pape
 |         Base-lining of 7.1_LA
 |    3    360Commerce 1.2         4/1/2005 2:57:39 AM    Robert Pearse
 |    2    360Commerce 1.1         3/10/2005 9:50:43 PM   Robert Pearse
 |    1    360Commerce 1.0         2/11/2005 11:40:25 PM  Robert Pearse
 |   $:
 |    8    .v710     1.2.2.1     10/28/2005 16:39:50    Charles Suehs
 |         updatePhoneInfo: Was filling vector up with same empty phone number.
 |          Now using new instance of empty phone number so that when we change
 |         the type, it doesn't change the type of all empty phone numbers.
 |    7    .v710     1.2.2.0     9/21/2005 13:39:23     Brendan W. Farrell
 |         Initial Check in merge 67.
 |    6    .v700     1.2.3.2     12/23/2005 17:17:22    Rohit Sachdeva  8203:
 |         Null Pointer Fix for Business Customer Info
 |    5    .v700     1.2.3.1     12/16/2005 10:39:41    Rohit Sachdeva  4029:At
 |         Customer Update, Null Phone Types will not be converted to Empty
 |         Phone Types
 |    4    .v700     1.2.3.0     11/28/2005 14:39:40    Rohit Sachdeva
 |         CR4029:New phone instance to be added to phone vector
 |    3    360Commerce1.2         3/31/2005 15:27:39     Robert Pearse
 |    2    360Commerce1.1         3/10/2005 10:20:43     Robert Pearse
 |    1    360Commerce1.0         2/11/2005 12:10:25     Robert Pearse
 |   $
 |   Revision 1.8  2004/06/29 13:37:47  kll
 |   @scr 4400: usage of JournalManager's entry type to dictate whether Customer addition belongs inside or outside the context of a transaction
 |
 |   Revision 1.7  2004/06/03 14:47:43  epd
 |   @scr 5368 Update to use of DataTransactionFactory
 |
 |   Revision 1.6  2004/04/20 13:11:00  tmorris
 |   @scr 4332 -Sorted imports
 |
 |   Revision 1.5  2004/04/12 18:58:36  pkillick
 |   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 |
 |   Revision 1.4  2004/04/09 16:56:02  cdb
 |   @scr 4302 Removed double semicolon warnings.
 |
 |   Revision 1.3  2004/02/12 16:49:25  mcs
 |   Forcing head revision
 |
 |   Revision 1.2  2004/02/11 21:40:12  rhafernik
 |   @scr 0 Log4J conversion and code cleanup
 |
 |   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 |   updating to pvcs 360store-current
 |
 |
 |
 |    Rev 1.0   Aug 29 2003 15:55:18   CSchellenger
 | Initial revision.
 |
 |    Rev 1.19   Jul 27 2003 11:45:34   jgs
 | Test model.getReasonCodeTags() in updateCustomer() for null before indexing the return.  Under some conditions the model will not have this value set.
 | Resolution for 3263:   Linking a Business Customer to a Refund of MBC crashes app.
 |
 |    Rev 1.18   17 Jul 2003 03:25:22   baa
 | customer cleanup/ more fixes
 |
 |    Rev 1.17   02 Jul 2003 02:12:02   baa
 | show 3line address
 |
 |    Rev 1.16   Jun 25 2003 16:00:08   baa
 | remove default state setting for customer info lookup
 |
 |    Rev 1.15   May 29 2003 11:06:44   baa
 | look for all available addresses
 |
 |    Rev 1.14   May 27 2003 08:48:02   baa
 | rework customer offline flow
 | Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 |
 |    Rev 1.13   May 11 2003 22:54:22   baa
 | business customer bug fixes
 |
 |    Rev 1.12   May 09 2003 12:50:46   baa
 | more fixes to business customer
 | Resolution for POS SCR-2366: Busn Customer - Tax Exempt- Does not display Tax Cert #
 |
 |    Rev 1.11   May 06 2003 20:23:08   baa
 | fix null pointer in customer
 | Resolution for POS SCR-2216: Valid Postal Code not accepted for a new customer
 |
 |    Rev 1.10   May 06 2003 13:41:08   baa
 | updates for business customer
 | Resolution for POS SCR-2203: Business Customer- unable to Find previous entered Busn Customer
 |
 |    Rev 1.9   Apr 28 2003 09:47:22   baa
 | updates to for business customer
 | Resolution for POS SCR-2217: System crashes if new business customer is created and Return is selected
 |
 |    Rev 1.8   Apr 17 2003 12:34:30   baa
 | fix defect with business phone
 | Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 |
 |    Rev 1.7   Apr 07 2003 16:48:30   baa
 | remove system out
 | Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 |
 |    Rev 1.6   Apr 02 2003 13:52:20   baa
 | I18n Database support for customer groups
 | Resolution for POS SCR-1866: I18n Database  support
 |
 |    Rev 1.5   Mar 26 2003 16:41:44   baa
 | fix minor bugs with customer refactoring
 | Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 |
 |    Rev 1.4   Mar 26 2003 10:42:46   baa
 | add changes from acceptance test
 | Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 |
 |    Rev 1.3   Mar 20 2003 18:18:46   baa
 | customer screens refactoring
 | Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 |
 |    Rev 1.2   Sep 20 2002 17:55:12   baa
 | country/state fixes and other I18n changes
 | Resolution for POS SCR-1740: Code base Conversions
 |
 |    Rev 1.1   Sep 20 2002 09:06:16   baa
 | fix references to get state
 | Resolution for POS SCR-1740: Code base Conversions
 |
 |    Rev 1.0   Apr 29 2002 15:33:40   msg
 | Initial revision.
 |
 |    Rev 1.1   Mar 18 2002 23:11:28   msg
 | - updated copyright
 |
 |    Rev 1.0   Mar 18 2002 11:24:14   msg
 | Initial revision.
 |
 |    Rev 1.2   12 Mar 2002 12:50:28   baa
 | cleanup dead code
 | Resolution for POS SCR-1553: cleanup dead code
 |
 |    Rev 1.1   11 Jan 2002 18:08:12   baa
 | update phone field
 | Resolution for POS SCR-561: Changing the Type on Add Customer causes the default to change
 | Resolution for POS SCR-567: Customer Select Add, Find, Delete display telephone type as Home/Work
 |
 |    Rev 1.0   Sep 21 2001 11:14:46   msg
 | Initial revision.
 |
 |    Rev 1.1   Sep 17 2001 13:06:48   msg
 | header updateCustomerUtilities
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import oracle.retail.stores.common.customer.CustomerGiftList;
import oracle.retail.stores.common.customer.ExtendedCustomerDataContainer;
import oracle.retail.stores.common.item.ExtendedItemData;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.CustomerReadCustomerGroupsDataTransaction;
import oracle.retail.stores.domain.arts.CustomerReadPricingGroupTransaction;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.PricingGroupIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.CountryIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.EmailAddressConstantsIfc;
import oracle.retail.stores.domain.utility.EmailAddressIfc;
import oracle.retail.stores.domain.utility.Gender;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.JournalableIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptConstantsIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel;
import oracle.retail.stores.pos.ui.localization.AddressField;
import oracle.retail.stores.pos.ui.localization.OrderableField;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * CustomerUtilites contains methods that are shared by more than one Customer
 * service.
 */
public class CustomerUtilities
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(CustomerUtilities.class);

    /**
     * Default maximum matches to use when parameter is not availible
     */
    protected static final int DEFAULT_MAXIMUM_MATCHES = 20;

    /**
     * This variable is a constant and set if no groups are selected
     */
    protected static final Integer NO_GROUP_SELECTED = Integer.valueOf(-1);

    /**
     * No Selection value
     */
    public static final String NONE_SELECTED = "None";

    /**
     * Default Value
     */
    public static final int DEFAULT_SELECTED = 0;

    public static Locale journalLocale  = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);



    /**
     * Returns the maximum matches parameter or sets the maximum matches to a
     * default.
     *
     * @param parameterManager the parameter manager
     * @return value of maximum matches
     */
    public static int getMaximumMatches(ParameterManagerIfc parameterManager)
    {
        // look up maximum matches parameter
        int maxMatches = DEFAULT_MAXIMUM_MATCHES; // initialize to default
        try
        { // begin try maximum matches parameter
            String maximumMatches = parameterManager.getStringValue(ParameterConstantsIfc.CUSTOMER_CustomerMaximumMatches);
            maxMatches = Integer.parseInt(maximumMatches);
        } // end try maximum matches parameter
        catch (ParameterException pe)
        {
            logger.error("Parameter Exception.\n" + pe);
        }
        catch (NumberFormatException e)
        {
            logger.error("NumberFormatException.\n" + e);
        }

        return (maxMatches);
    }



    /**
     * Returns the store state parameter
     *
     * @param parameterManager the parameter manager
     * @return value of state
     */
    public static String getStoreState(ParameterManagerIfc parameterManager)
    {
        // look up StoreStateProvince parameter
        String state = null;

        try
        {
            state = parameterManager.getStringValue(ParameterConstantsIfc.BASE_StoreStateProvince);
        }
        catch (ParameterException e)
        {
            logger.error("Parameter Exception.\n" + e);
        }
        return (state);
    }



    /**
     * Returns the store country parameter
     *
     * @param parameterManager the parameter manager
     * @return value of country
     */
    public static String getStoreCountry(ParameterManagerIfc parameterManager)
    {
        // look up StoreCountry parameter
        String country = null;

        try
        { // begin try maximum matches parameter
            country = parameterManager.getStringValue(ParameterConstantsIfc.BASE_StoreCountry);
        } // end try maximum matches parameter
        catch (ParameterException e)
        {
            logger.error("Parameter Exception.");
            logger.error("" + e + "");
        }
        return (country);
    }

    /**
     * Returns the store country parameter
     *
     * @param utility the utility manager
     * @return value of country
     */
    public static String[] getPhoneTypes(UtilityManagerIfc utility)
    {
        // look up StoreCountry parameter
        String[] phoneTypes = new String[PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR.length];

        for (int i = 0; i < PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR.length; i++)
        {
            phoneTypes[i] = utility.retrieveCommonText(PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR[i]);
        }

        return (phoneTypes);
    }
    
    /**
     * Returns receipt preference types
     *
     * @param utility the utility manager
     * @return receipt Types
     */
    public static String[] getReceiptPreferenceTypes(UtilityManagerIfc utility)
    {
   
        String[] receiptTypes = new String[ReceiptConstantsIfc.RECEIPT_TYPE_DESCRIPTOR.length];

        for (int i = 0; i < ReceiptConstantsIfc.RECEIPT_TYPE_DESCRIPTOR.length; i++)
        {
            receiptTypes[i] = utility.retrieveText("CustomerMasterSpec", "customerText",
                    ReceiptConstantsIfc.RECEIPT_TYPE_DESCRIPTOR[i], ReceiptConstantsIfc.RECEIPT_TYPE_DESCRIPTOR[i]);
        }

        return (receiptTypes);
    }

    /**
     * Updates the customer address and phone information. This method is called
     * from multiple Lane Actions and resides here for minimization of repeated
     * code.
     *
     * @param customer the customer
     * @param model the CustomerInfoBeanModel object
     * @return CustomerIfc the modified customer object
     */
    public static CustomerIfc updateAddressAndPhone(CustomerIfc customer, CustomerInfoBeanModel model)
    {
        // Update Address and Phone objects
        AddressIfc address = customer.getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_HOME);


        if (address == null)
        {
            address = DomainGateway.getFactory().getAddressInstance();
        }
        Vector<String> linesVector = new Vector<String>(3);
        linesVector.addElement(model.getAddressLine1());
        linesVector.addElement(model.getAddressLine2());
        linesVector.addElement(model.getAddressLine3());

        address.setLines(linesVector);
        address.setCity(model.getCity());
        address.setState(model.getState());
        address.setCountry(model.getCountry());
        address.setPostalCode(model.getPostalCode());
        address.setAddressType(AddressConstantsIfc.ADDRESS_TYPE_HOME);

        // Replace the old address(es) with the new one
        List<AddressIfc> addressList = new ArrayList<AddressIfc>();
        addressList.add(address);

        customer.setAddressList(addressList);
        updatePhoneInfo(customer, model);
        return customer;
    }

    /**
     * Updates the phone information from the bean model.
     *
     * @param customer the customer
     * @param model the CustomerInfoBeanModel object
     */
    public static void updatePhoneInfo(CustomerIfc customer, CustomerInfoBeanModel model)
    {
        // Get customer's telephone information.

        PhoneIfc phones[] = model.getPhoneList();

        if (phones != null)
        {
            List<PhoneIfc> phoneList = new ArrayList<PhoneIfc>();
            for (PhoneIfc phone: phones)
            {
                if (phone != null && !phone.getPhoneNumber().isEmpty())
                {
                    phoneList.add(phone);
                }
            }
            // convert to list;
            customer.setPhoneList(phoneList);
        }

    }

    /**
     * Updates the customer information. This method is called from multiple
     * Lane Actions and resides here for minimization of repeated code.
     *
     * @param customer the customer
     * @param model the CustomerInfoBeanModel object
     * @return CustomerIfc the modified customer object
     */
    @SuppressWarnings("rawtypes")
    public static CustomerIfc updateCustomer(CustomerIfc customer, CustomerInfoBeanModel model)
    {

        if (customer == null)
        {
            customer = DomainGateway.getFactory().getCustomerInstance();
        }
        // Update customer name and id
        if (model.isBusinessCustomer())
        {
            customer.setBusinessCustomer(true);
            customer.setEncipheredTaxCertificate(model.getEncipheredTaxCertificate());
            customer.setCompanyName(model.getCustomerName());
            customer.setReceiptPreference(model.getSelectedReceiptMode());
            Vector reasons = model.getReasonCodeKeys();
            int selectedIndex = model.getSelectedIndex();
            if (model.isSelected() && selectedIndex > -1
                    && reasons != null && reasons.size() > selectedIndex)
            {
                String reasonCode = (String)reasons.get(selectedIndex);
                UtilityManagerIfc utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(
                        UtilityManagerIfc.TYPE);
                // set up reason code list
                CodeListIfc rcl = utility.getReasonCodes(Gateway.getProperty("application", "StoreID", ""),
                        CodeConstantsIfc.CODE_LIST_TAX_EXEMPT_REASON_CODES);
                CodeEntryIfc reasonEntry = null;
                LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
                if (rcl != null)
                {
                    reasonEntry = rcl.findListEntryByCode(reasonCode);
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
        else
        {
            customer.setBusinessCustomer(false);
            customer.setFirstName(model.getFirstName());
            customer.setLastName(model.getLastName());
            customer.setReceiptPreference(model.getSelectedReceiptMode());
        }
        customer.setCustomerName(model.getCustomerName());
        // customer.setCustomerID(model.getCustomerID());
        customer.setEmployeeID(model.getEmployeeID());
        // Update Address and Phone objects
        customer = updateAddressAndPhone(customer, model);

        // set the Customer's email address
        if(!model.getEmail().isEmpty())
        {
            EmailAddressIfc email = DomainGateway.getFactory().getEmailAddressInstance();
            email.setEmailAddress(model.getEmail());
            if (!model.isBusinessCustomer())
            {
                email.setEmailAddressType(EmailAddressConstantsIfc.EMAIL_ADDRESS_TYPE_HOME);
            }
            else
            {
                email.setEmailAddressType(EmailAddressConstantsIfc.EMAIL_ADDRESS_TYPE_WORK);
            }
            customer.setEmailAddress(email);
        }


        //customer.setTaxID(model.getCustomerTaxID());
        // if not from layaway flow for CSP
        if (!model.isFromLayaway())
        {
            /* setting the tax ID from the model may kill an already present tax ID.
             * not all models showing a customer will have the tax ID, so preserver
             * the original.
             */
            if(StringUtils.isNotEmpty(model.getTaxID().getMaskedNumber()))
            {
                customer.setEncipheredTaxID(model.getTaxID());
            }
            PricingGroupIfc[] pricingGroups = model.getCustomerPricingGroups();
            // Setting Pricing group ID into customer from model
            if (pricingGroups != null)
            {
                for (int i = 0; i <= pricingGroups.length; i++)
                {
                    if (i == model.getSelectedCustomerPricingGroup())
                    {
                        if (i == 0)
                        {
                            // if None is selected
                            customer.setPricingGroupID(null);
                        }
                        else
                        {
                            // if one of the pricing group is selected
                            customer.setPricingGroupID(pricingGroups[i - 1].getPricingGroupID());
                        }
                    }
                }
            }
            else
            {
                customer.setPricingGroupID(CustomerUtilities.NO_GROUP_SELECTED);
            }
        }
        model.setFromLayaway(false);
        // Update customer discount
        return (customer);

    }

    /**
     * Initialzes the CustomerInfoBeanModel from the cargo.
     *
     * @param customer the customer object containing the data to be used for
     *            populating the customer info model.
     * @param utility a reference to the UtilityManager
     * @param pm a reference to the parameter manager
     * @return The CustomerInfoBeanModel
     * @deprecated As of 13.4, please use {@link CustomerUtilities#populateCustomerInfoBeanModel(CustomerIfc, UtilityManagerIfc, ParameterManagerIfc, CustomerInfoBeanModel)} instead.
     */
    public static CustomerInfoBeanModel getCustomerInfo(CustomerIfc customer, UtilityManagerIfc utility, ParameterManagerIfc pm)
    {
        // model to use for the UI
        CustomerInfoBeanModel model = new CustomerInfoBeanModel();
        return populateCustomerInfoBeanModel(customer, utility, pm, model);
    }

    /**
     * Initialzes the CustomerInfoBeanModel from the cargo.
     *
     * @param customer the customer object containing the data to be used for
     *            populating the customer info model.
     * @param utility a reference to the UtilityManager
     * @param pm a reference to the parameter manager
     * @param model Instance of the customerInfoBeanModel to populate.
     * @return The CustomerInfoBeanModel
     */
    public static CustomerInfoBeanModel populateCustomerInfoBeanModel(CustomerIfc customer, UtilityManagerIfc utility, ParameterManagerIfc pm, CustomerInfoBeanModel model)
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

    /**
     * Populates extended customer data into the given {@link CustomerInfoBeanModel} instance.
     * @param customer the {@link CustomerIfc} instance to retrieve the extended customer data
     * @param model the instance of the {@link CustomerInfoBeanModel} to populate
     */
    public static void populateExtendedCustomerData(CustomerIfc customer, CustomerInfoBeanModel model)
    {
        boolean retrieveExternalData = Gateway.getBooleanProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "RetrieveExtendedData", false);
        model.setShowTabbedPane(retrieveExternalData);
        // fetch extended customer data
        if(retrieveExternalData && customer.getExtendedCustomerDataContainer() != null)
        {
            ExtendedCustomerDataContainer container = customer.getExtendedCustomerDataContainer();
            // get the extended data
            List<ExtendedItemData> recentItems = container.getRecentItems();
            List<ExtendedItemData> recommendedItems = container.getRecommendedItems();
            List<CustomerGiftList> giftLists = container.getGiftLists();

            // populate into the model
            model.setRecentItems(recentItems);
            model.setRecommendedItems(recommendedItems);
            model.setGiftLists(giftLists);
        }
    }

    /**
     * Retrieve Customer data into a mailbank check model
     *
     * @param customer The customer data
     * @param utility The utility manager
     * @param pm The parameter manager
     * @return The MailBankCheckInfoBeanModel
     */
    public static MailBankCheckInfoBeanModel copyCustomerToModel(CustomerIfc customer, UtilityManagerIfc utility, ParameterManagerIfc pm)
    {
        MailBankCheckInfoBeanModel model = new MailBankCheckInfoBeanModel();
        model = (MailBankCheckInfoBeanModel)populateCustomerInfoBeanModel(customer, utility, pm, model);
        return model;
    }

    /**
     * Retrieves the customer pricing groups available.
     *
     * @return PricingGroupIfc the list of customer pricing groups groups
     */
    public static PricingGroupIfc[] getCustomerPricingGroups()
    {
        return getCustomerPricingGroups(new LocaleRequestor(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
    }

    /**
     * Retrieves the customer pricing groups available.
     *
     * @return PricingGroupIfc the list of customer pricing groups groups
     */
    public static PricingGroupIfc[] getCustomerPricingGroups(LocaleRequestor locale)
    {
        // get available customer pricing groups

        PricingGroupIfc[] groups = null;
        try
        {
            CustomerReadPricingGroupTransaction ct = null;

            ct = (CustomerReadPricingGroupTransaction)DataTransactionFactory
                    .create(DataTransactionKeys.CUSTOMER_READ_PRICING_GROUP_TRANSACTION);

            groups = ct.readPricingGroup(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
        }
        catch (DataException e)
        {
            // if exception getting available group occurs, essentially ignore
            // it
            logger.warn("DataException occurred retrieving customer pricing groups.");
        }

        return groups;
    }

    /**
     * Retrieves the customer groups available.
     *
     * @return CustomerGroupIfc the list of customer groups
     */
    public static CustomerGroupIfc[] getCustomerGroups(LocaleRequestor localeReq)
    {
        // get available customer groups

        CustomerGroupIfc[] groups = null;
        try
        {
            CustomerReadCustomerGroupsDataTransaction ct = null;

            ct = (CustomerReadCustomerGroupsDataTransaction)DataTransactionFactory
                    .create(DataTransactionKeys.CUSTOMER_READ_CUSTOMER_GROUPS_DATA_TRANSACTION);

            groups = ct.selectCustomerGroups(localeReq);
        }
        catch (DataException e)
        {
            // if exception getting available group occurs, essentially ignore
            // it
            logger.warn("DataException occurred retrieving customer customer groups.");
        }

        return groups;
    }

    /**
     * Retrieves the list of taxt exemptions for business customer
     *
     * @param utility The utility manager
     * @param storeID
     * @return Vector The list of exceptions
     */
    public static Vector<String> getTaxExemptions(UtilityManagerIfc utility, String storeID)
    {
        CodeListIfc rcl = utility.getReasonCodes(storeID, CodeConstantsIfc.CODE_LIST_TAX_EXEMPT_REASON_CODES);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        return rcl.getTextEntries(locale);
    }

    /**
     * Retrieves the list of tax exemptions for business customer
     *
     * @param utilityManager
     * @param storeID
     * @return Array of exception strings
     */
    public static String[] getTaxExemptionsTags(UtilityManagerIfc utility, String storeID)
    {
        CodeListIfc rcl = utility.getReasonCodes(storeID, CodeConstantsIfc.CODE_LIST_TAX_EXEMPT_REASON_CODES);
        String[] textEntries = null;
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        if (rcl != null)
        {
            CodeEntryIfc[] entries = rcl.getEntries();
            textEntries = new String[entries.length];
            for (int i = 0; i < entries.length; i++)
            {
                textEntries[i] = entries[i].getText(locale);
            }
        }
        return textEntries;
    }

    /**
     * Retrieves the list of tax exemptions for business customer
     *
     * @param reasons The CodeList object
     * @param storeID
     * @return Array of exception strings
     */
    public static String[] getTaxExemptionsTags(CodeListIfc rcl, String storeID)
    {

        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String[] textEntries = null;
        if (rcl != null)
        {
            CodeEntryIfc[] entries = rcl.getEntries();
            textEntries = new String[entries.length];
            for (int i = 0; i < entries.length; i++)
            {
                textEntries[i] = entries[i].getText(locale);
            }
        }
        return textEntries;
    }

    /**
     * Makes a journal entry to indicate when the operator is entering the
     * customer service.
     *
     * @param employeeID the employee ID of the operator
     * @param transactionID the current transaction ID (if applicable)
     */
    public static void journalCustomerEnter(BusIfc bus, String employeeID, String transactionID)
    {
        // String journalText = Util.EOL + "Entering Customer";
        StringBuffer journalText = new StringBuffer();
        journalText.append(Util.EOL);
        journalText.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.ENTERING_CUSTOMER_LABEL, null));

        // get the Journal manager
        JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

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
            jmi.journal(employeeID, transactionID, journalText.toString());
            jmi.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);

        }
        else
        {
            logger.warn("No journal manager found!");
        }
    }

    /**
     * Makes a journal entry to indicate when a customer has been link
     *
     * @param employeeID the employee ID of the operator
     * @param customerID the customer ID
     * @param transactionID the current transaction ID (if applicable)
     */
    public static void journalCustomerLink(BusIfc bus, String employeeID, String customerID, String transactionID)
    {
        StringBuilder journalText = new StringBuilder();
        Object[] dataArgs = new Object[2];
        dataArgs[0] = customerID;
        journalText.append(Util.EOL).append(
                I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LINK_CUSTOMER_LABEL, dataArgs));

        // get the Journal manager
        JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        if (jmi != null)
        {

            jmi.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);

            jmi.journal(employeeID, transactionID, journalText.toString());

        }
        else
        {
            logger.error("No journal manager found!");
        }
    }

    /**
     * Journals price promotions to items that qualify customer's price group.
     * @param transaction
     * @param pricingGroupID
     */
    public static void journalCustomerPricing(BusIfc bus, SaleReturnTransactionIfc transaction, Integer pricingGroupID)
    {
        AbstractTransactionLineItemIfc[] items = transaction.getLineItems();


        StringBuilder journalText = new StringBuilder(Util.EOL);
        SaleReturnLineItemIfc saleReturnLineItem = null;
        String sellingPriceString = null;
        Object[] dataArgs = null;
        // test if there's something to journal
        if(pricingGroupID != null)
        {
            for(AbstractTransactionLineItemIfc item : items)
            {
                if(item instanceof SaleReturnLineItemIfc)
                {
                    saleReturnLineItem = (SaleReturnLineItemIfc)item;
                    // if it's not a return line item and the pricingGroupID matches
                    if(!saleReturnLineItem.isReturnLineItem() &&
                            saleReturnLineItem.getItemPrice() != null &&
                            saleReturnLineItem.getItemPrice().getAppliedPromotion() != null)
                    {
                        if(saleReturnLineItem.getItemPrice().getAppliedPromotion().getPricingGroupID() == pricingGroupID.intValue())
                        {
                            // list the item ID
                            dataArgs = new Object[]{saleReturnLineItem.getItemID()};
                            journalText.append(Util.EOL);
                            journalText.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_LABEL, dataArgs));
                            // list the item's price
                            //use default locale for currency display
                            sellingPriceString = saleReturnLineItem.getItemPrice().getSellingPrice().toGroupFormattedString();
                            dataArgs = new Object[]{sellingPriceString};
                            journalText.append(Util.EOL);
                            journalText.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_PRICE_LABEL, dataArgs));
                            // list the promotion name
                            journalText.append(Util.EOL);
                            journalText.append(saleReturnLineItem.getItemPrice().getAppliedPromotion().getPromotionName());
                            journalText.append(Util.EOL);
                        }
                    }
                }
            }

            // get the Journal manager
            JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
            if (jmi != null && journalText.length() > Util.EOL.length())
            {
                jmi.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);
                jmi.journal(transaction.getCashier().getEmployeeID(), transaction.getTransactionID(), journalText.toString());
            }
            else
            {
                logger.error("Journal manager not found!");
            }
        }
    }

    /**
     * Makes a journal entry to indicate when the operator is exiting the
     * customer service.
     *
     * @param employeeID the employee ID of the operator
     * @param transactionID the current transaction ID (if applicable)
     */
    public static void journalCustomerExit(BusIfc bus, String employeeID, String transactionID)
    {
        String journalText = Util.EOL
                + I18NHelper
                        .getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.EXITING_CUSTOMER_LABEL, null)
                + Util.EOL;

        // get the Journal manager
        JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        // Journal the entry.
        if (jmi != null)
        {
            if (jmi.getEntryType() == JournalableIfc.ENTRY_TYPE_CUST)
            {
                jmi.setEntryType(JournalableIfc.ENTRY_TYPE_NOTTRANS);
                jmi.journal(employeeID, transactionID, journalText);
            }
            else
            {
                jmi.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);
                jmi.journal(employeeID, transactionID, journalText);
            }

            jmi.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);
        }
        else
        {
            logger.warn("No journal manager found!");
        }
    }

    /**
     * Accepts an ordered list of fields and reorders them based on the Region
     * configured and it's corresponding field order.
     *
     * @param fieldMap a list of OrderableField objects
     * @return an ordered List of fields or the original List if the field order
     *         cannot be established
     */
    public static List<OrderableField> arrangeInAddressFieldOrder(List<OrderableField> fieldMap)
    {
        // get the address field order as per the current configuration
        // for example it could return a list {"Country", "State", "PostalCode",
        // etc.}
        List<AddressField> fieldsOrder = getAddressFieldsOrder();

        // if the field order cannot be established
        if (fieldsOrder.size() == 0)
        {
            // return the original list
            return fieldMap;
        }

        List<OrderableField> orderedFieldMap = new ArrayList<OrderableField>(fieldMap.size());

        // first add all fields that match the fieldsOrder and the UI fields in
        // the order of the fieldsOrder.
        // basically we add all the fields in the fieldsOrder if they occur in
        // the UI.
        for (AddressField addressField : fieldsOrder)
        {
            int index = fieldPositionInUI(addressField, fieldMap);
            if (index != -1)
            {
                orderedFieldMap.add(fieldMap.get(index));
            }
        }

        // next we add all the fields in the UI that are not part of the
        // fieldsOrder. These are generally not
        // address fields. we keep such fields in the same position on the UI as
        // before.
        for (int i = 0; i < fieldMap.size(); i++)
        {
            if (fieldMap.get(i).getLogicalField() == null)
            {
                // defensive protection against IndexOutOfBoundsException
                // gross mismanagement of domain.properties and the UI could
                // potentially result in this case
                if (i > orderedFieldMap.size())
                {
                    orderedFieldMap.add(fieldMap.get(i));
                }
                else
                {
                    orderedFieldMap.add(i, fieldMap.get(i));
                }
            }
        }

        // as a final safety check to ensure that we're not inadvertantly
        // dropping
        // fields from the UI, compare the list sizes
        if (fieldMap.size() != orderedFieldMap.size())
        {
            // there's a mismatch in sizes, log the error
            logger.error("Mismatch in field list sizes, one or more fields were not properly ordered.");
            // return the input list
            return fieldMap;
        }

        return orderedFieldMap;
    }

    /**
     * Returns a List of fields as they should be ordered for this region.
     * <P>
     * The list order is based on properties in the domain.properties file. The
     * properties take the form of &lt;Region&gt;.Line&lt;n&gt; where
     * &lt;Region&gt; is the value of the Region parameter and n is the line
     * order starting at line 1 and ending at the size of the AddressField
     * enumeration.
     *
     * @return an ordered List of fields or an empty list if the field order
     *         cannot be established
     */
    protected static List<AddressField> getAddressFieldsOrder()
    {
        // look up Region property
        String region = DomainGateway.getProperty("Region");
        if (region == null)
        {
            // the region isn't set and we can't determine the field order
            return new ArrayList<AddressField>(); // empty list
        }

        // get the address fields we're looking for
        AddressField[] fields = AddressField.values();
        // storage for the ordered fields
        List<AddressField> orderedFields = new ArrayList<AddressField>(fields.length);
        // we need to read the properties
        // property name is Region.<region_value>.Line<n> where n is the ordinal
        // for that field
        for (int i = 1; i <= fields.length; i++)
        {
            // get the address properties from domain.properties
            String propertyName = "Region." + region + ".Line" + i;
            String propertyValue = DomainGateway.getProperty(propertyName);
            if (propertyValue != null)
            {
                // if the property was found
                AddressField field = AddressField.getInstance(propertyValue);
                if (field != null)
                {
                    // add the orderable field
                    orderedFields.add(field);
                }
                else
                {
                    logger.error("Unknown address field: " + propertyValue);
                }
            }
            else
            {
                logger.error("Could not find expected property: " + propertyName);
            }
        }

        return orderedFields;
    }

    /**
     * Returns the position of the field in the ordered fields or -1 if the
     * field does not occurr in the list.
     *
     * @return the index of the field in fields or -1 if it is not found
     */
    @SuppressWarnings("rawtypes")
    protected static int fieldPositionInUI(Enum field, List<OrderableField> fields)
    {
        // default return value for not found
        int index = -1;
        // loop over the list
        for (int i = 0; i < fields.size(); i++)
        {
            // if the field enum is the same as the logical field enum
            if (field == fields.get(i).getLogicalField())
            {
                // return the current index
                index = i;
                // and exit the loop
                break;
            }
        }
        return index;
    }

    /**
     * Retrieves the index in the country array given the country code
     *
     * @param code the country code
     * @param utility utility manager reference
     * @param pm parameter manager reference
     * @return index in the country array
     */
    public static int getCountryIndex(String code, UtilityManagerIfc utility, ParameterManagerIfc pm)
    {
        int index = 0;
        if (utility.getCountriesAndStates(pm) != null)
        {
            CountryIfc[] countries = utility.getCountriesAndStates(pm);
            for (int i = 0; i < countries.length; i++)
            {
                if (code.equals(countries[i].getCountryCode()) || code.equals(countries[i].getCountryName()))
                {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    /**
     * Retrieve Array of Pricing group names.
     *
     * @param bus pricingGroups
     * @return useArray Array of Pricing group names
     */
    public static String[] getPricingGroups(PricingGroupIfc[] pricingGroups, Locale locale)
    {
        if (locale == null)
        {
            locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        }

        String[] useArray = null;
        if (pricingGroups != null)
        {
            String[] pricingGroupName = new String[pricingGroups.length];
            for (int i = 0; i < pricingGroups.length; i++)
            {
                pricingGroupName[i] = pricingGroups[i].getPricingGroupName(locale);
            }
            useArray = new String[pricingGroupName.length + 1];
            useArray[0] = NONE_SELECTED;
            for (int i = 0; i < pricingGroupName.length; i++)
            {
                useArray[i + 1] = pricingGroupName[i];
            }
        }
        return useArray;
    }

    /**
     * Utility method for journaling customer detail information.
     *
     * @param customer
     * @param journalManager
     * @param transaction
     */
    public static void journalCustomerInformation(CustomerIfc customer, JournalManagerIfc journalManager, RetailTransactionIfc transaction)
    {
        if(customer != null && journalManager != null && transaction != null)
        {
            // journal the customer's name or business name depending on is business customer flag
            StringBuilder sb = new StringBuilder();
            if(customer.isBusinessCustomer())
            {
                createJournalEntry(sb, JournalConstantsIfc.CUSTOMER_COMPANY_NM, customer.getCompanyName());
            }
            else
            {
                createJournalEntry(sb, JournalConstantsIfc.CUSTOMER_FIRST_NAME, customer.getFirstName());
                createJournalEntry(sb, JournalConstantsIfc.CUSTOMER_LAST_NM, customer.getLastName());
            }
            createJournalEntry(sb, JournalConstantsIfc.CUSTOMER_CUST_ID, customer.getCustomerID());

            // loop through all addresses
            for(AddressIfc address : customer.getAddressList())
            {

                // loop through all address lines
                for (String line : address.getLines())
                {
                    createJournalEntry(sb, JournalConstantsIfc.CUSTOMER_ADDR_LINE, line);
                }
                createJournalEntry(sb, JournalConstantsIfc.CUSTOMER_CITY, address.getCity());
                createJournalEntry(sb, JournalConstantsIfc.CUSTOMER_STATE, address.getState());
                
                if(!Util.isEmpty(address.getPostalCode()))
                {
                    StringBuilder postalCode = new StringBuilder(address.getPostalCode());
                    // Add the postal code extension, if it exists
                    if(!Util.isEmpty(address.getPostalCodeExtension()))
                    {
                        postalCode.append(" - ");
                        postalCode.append(address.getPostalCodeExtension());
                    }
                    createJournalEntry(sb, JournalConstantsIfc.CUSTOMER_POSTAL_CD, postalCode.toString());
                }

                // journal the country
                createJournalEntry(sb, JournalConstantsIfc.CUSTOMER_COUNTRY, address.getCountry());
            }

            // loop through all the phone numbers
            for(PhoneIfc phone : customer.getPhoneList())
            {
                createJournalEntry(sb, JournalConstantsIfc.CUSTOMER_PH_NO, phone.getPhoneNumber());
            }

            // loop through all the email addresses
            EmailAddressIfc emailAddress;
            for(Iterator<EmailAddressIfc> emailAddresses = customer.getEmailAddresses(); emailAddresses.hasNext();)
            {
                emailAddress = emailAddresses.next();
                createJournalEntry(sb, JournalConstantsIfc.EMAIL_ADDRESS_LABEL, emailAddress.getEmailAddress());
            }
            // Journal the results
            journalManager.journal(transaction.getCashier().getEmployeeID(), transaction.getTransactionID(), sb.toString());
        }
    }

    /**
     * Adds the journals entry to the given StringBuilder if text is not empty.
     * @param sb
     * @param labelKey
     * @param text
     */
    protected static void createJournalEntry(StringBuilder sb, String labelKey, String text)
    {
        if(!Util.isEmpty(text))
        {
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, labelKey, new Object[]{text}));
        }
    }

    /**
     * Returns all changes made to customer object
     *
     * @param original original customer information
     * @param customer the new customer information
     * @return string with data
     */
    public static String getChangedCustomerData(CustomerIfc original, CustomerIfc customer)
    {
      StringBuilder jString = new StringBuilder("");

      jString.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_FIRSTNAME_LABEL, null,journalLocale),
          original.getFirstName(),customer.getFirstName()));
      if (customer.isBusinessCustomer())
      {
        jString.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
            JournalConstantsIfc.MODIFY_BUSINESS_CUSTOMER_NAME_LABEL, null, journalLocale), original
            .getLastName(), customer.getLastName()));
        if ( original.getEncipheredTaxID() != null && customer.getEncipheredTaxID() != null )
        {
            jString.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.MODIFY_TAXID_LABEL, null, journalLocale), original
                    .getEncipheredTaxID().getMaskedNumber(), customer.getEncipheredTaxID().getMaskedNumber()));
        }
        if ( original.getEncipheredTaxCertificate() != null && customer.getEncipheredTaxCertificate() != null )
        {        
            jString.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.MODIFY_TAX_CERTIFICATE_LABEL, null, journalLocale), original
                    .getEncipheredTaxCertificate().getMaskedNumber(), customer.getEncipheredTaxCertificate().getMaskedNumber()));
        }
        if ( original.getTaxExemptionReason() != null && customer.getTaxExemptionReason() != null )
        {
            jString.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.MODIFY_TAX_EXEMPT_REASON_CODE_LABEL, null, journalLocale), original
                    .getTaxExemptionReason().getText(journalLocale), customer.getTaxExemptionReason().getText(journalLocale)));
        }
      }
      else
      {
        jString.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
            JournalConstantsIfc.MODIFY_LASTNAME_LABEL, null, journalLocale), original.getLastName(), customer
            .getLastName()));
      }
      jString.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_SALUTATION_LABEL, null,journalLocale),
          original.getSalutation(),customer.getSalutation()));
      jString.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_CUSTOMERID_LABEL, null,journalLocale),
          original.getNameSuffix(),customer.getNameSuffix()));
      if (original.getPricingGroupID() != null && customer.getPricingGroupID() != null)
      {
        jString.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_PRICINGGROUP_LABEL, null,journalLocale),
            original.getPricingGroupID(),customer.getPricingGroupID()));
      }

      String oldGender = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
          JournalConstantsIfc.JOURNAL_ENTRY_PREFIX+Gender.GENDER_DESCRIPTOR[original.getGenderCode()],
          null,journalLocale);
      String newGender = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
          JournalConstantsIfc.JOURNAL_ENTRY_PREFIX+Gender.GENDER_DESCRIPTOR[customer.getGenderCode()],
          null,journalLocale);
      jString.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_GENDER_LABEL, null,journalLocale),
          oldGender,newGender));

      jString.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_EMAILADDRESS_LABEL, null,journalLocale),
          original.getEmailAddress(EmailAddressConstantsIfc.EMAIL_ADDRESS_TYPE_HOME).getEmailAddress(),
          customer.getEmailAddress(EmailAddressConstantsIfc.EMAIL_ADDRESS_TYPE_HOME).getEmailAddress()));
      jString.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_BIRTHDATE_LABEL, null,journalLocale),
          original.getBirthDateAsString(),customer.getBirthDateAsString()));
      jString.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_EMPLOYEEID_LABEL, null,journalLocale),
          original.getEmployeeID(),customer.getEmployeeID()));
      jString.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_MAILPRIVACY_LABEL, null,journalLocale),
          original.getMailPrivacy(),customer.getMailPrivacy()));
      jString.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_EMAILPRIVACY_LABEL, null,journalLocale),
          original.getEMailPrivacy(),customer.getEMailPrivacy()));

      jString.append(compareDiscounts(original.getCustomerGroups(),customer.getCustomerGroups()));
      jString.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_PHONEPRIVACY_LABEL, null,journalLocale),
          original.getTelephonePrivacy(),customer.getTelephonePrivacy()));
      jString.append(comparePhones(original.getPhoneList(),customer.getPhoneList()));
      jString.append(compareAddresses(original.getAddressList(),customer.getAddressList()));
      
      return (jString.toString());
    }

    /**
     * Compare two Strings
     *
     * @param fieldName field being checked
     * @param field1 old value
     * @param field2 new value
     * @return string with data
     */
    public static String compareFields(String fieldName, String field1, String field2)
    {
        String jString = "";

        if (!Util.isObjectEqual(field1, field2))
        {
            jString = printChange(fieldName, field1, field2);
        }
        return jString;
    }

    /**
     * Compare two integers
     *
     * @param fieldName field being checked
     * @param field1 old value
     * @param field2 new value
     * @return string with data
     */
    public static String compareFields(String fieldName, int field1, int field2)
    {
        String jString = "";
        if (field1 != field2)
        {
            jString = printChange(fieldName, new Integer(field1).toString(), new Integer(field2).toString());
        }
        return (jString);
    }

    /**
     * Compare two booleans
     *
     * @param fieldName field being checked
     * @param field1 old value
     * @param field2 new value
     * @return string with data
     */
    public static String compareFields(String fieldName, boolean field1, boolean field2)
    {
        String jString = "";
        if (field1 != field2)
        {
            jString = printChange(fieldName, convertBoolean(field1), convertBoolean(field2));
        }
        return (jString);
    }

    /**
     * Compare two Discounts
     *
     * @param fieldName field being checked
     * @param oldCustomerGroups old value
     * @param oldCustomerGroups new value
     * @return string with data
     */
    public static String compareDiscounts(CustomerGroupIfc[] oldCustomerGroups, CustomerGroupIfc[] newCustomerGroups)
    {
        String str = "";
        // CustomerGroupIfc[] customerGroups = customer.getCustomerGroups();
        DiscountRuleIfc[] oldDiscounts = null, newDiscounts = null;
        if (newCustomerGroups != null && newCustomerGroups.length > 0)
        {
            if (oldCustomerGroups != null)
            {
                oldDiscounts = oldCustomerGroups[0].getDiscountRules();
            }
            newDiscounts = newCustomerGroups[0].getDiscountRules();
        }

        if (!Util.isObjectEqual(oldDiscounts, newDiscounts))
        {
            String oldRuleName = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.MODIFY_NONE_LABEL, null, journalLocale);
            String newRuleName = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.MODIFY_NONE_LABEL, null, journalLocale);
            if (oldDiscounts != null && oldDiscounts.length > 0)
            {
                DiscountRuleIfc oldRule = oldDiscounts[0];
                oldRuleName = oldRule.getName(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
            }

            if (newDiscounts != null && newDiscounts.length > 0)
            {

                DiscountRuleIfc newRule = newDiscounts[0];
                newRuleName = newRule.getName(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
            }
            str += compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.MODIFY_DISCOUNTS_LABEL, null, journalLocale), oldRuleName, newRuleName);
        }
        return str;
    }

    /**
     * Compare two Address objects
     *
     * @param oldAddr old value
     * @param newAddr new value
     * @return string with data
     */
    public static String compareAddresses(List<AddressIfc> oldAddr, List<AddressIfc> newAddr)
    {
        StringBuilder str = new StringBuilder();
        // loop through all addresses
        for(int index = 0; index < oldAddr.size() && index < newAddr.size(); index++)
        {
            AddressIfc oldAddress = oldAddr.get(index);
            AddressIfc newAddress = newAddr.get(index);

            Vector<String> oldLines = oldAddress.getLines();
            Vector<String> newLines = newAddress.getLines();
            Enumeration<String> oldLinesEnum = oldLines.elements();
            Enumeration<String> newLinesEnum = newLines.elements();

            // loop through all address lines
            if (!Util.isObjectEqual(oldAddress, newAddress))
            {

                int i = 1;
                while (oldLinesEnum.hasMoreElements())
                {
                    String oldAddrLine = oldLinesEnum.nextElement();
                    if (newLinesEnum.hasMoreElements())
                    {
                        String newAddrLine = newLinesEnum.nextElement();
                        str.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.MODIFY_ADDRESSLINE_LABEL, new Object[] { String.valueOf(i) },
                                journalLocale), oldAddrLine, newAddrLine));
                        i++;
                    }
                }

                str.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.MODIFY_CITY_LABEL, null, journalLocale), oldAddress.getCity(),
                        newAddress.getCity()));
                str.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.MODIFY_STATE_LABEL, null, journalLocale), oldAddress.getState(),
                        newAddress.getState()));
                str.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.MODIFY_POSTALCODE_LABEL, null, journalLocale),
                        oldAddress.getPostalCode() + " - " + oldAddress.getPostalCodeExtension(),
                        newAddress.getPostalCode() + " - " + newAddress.getPostalCodeExtension()));
                str.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.MODIFY_COUNTRY_LABEL, null, journalLocale), oldAddress.getCountry(),
                        newAddress.getCountry()));
            }
        }
        return (str.toString());
    }

    /**
      Compare two Phone objects
      @param oldAddr  old value
      @param newAddr new value
      @return string with EJournal data
     **/
    public static String comparePhones(List<PhoneIfc> oldPhones, List<PhoneIfc> newPhones)
    {
        // loop through all the phone numbers
        StringBuilder str = new StringBuilder("");
        boolean isPhoneTypeSame=false;
        List<PhoneIfc> changedNumbers=new ArrayList<PhoneIfc>();
        for(int i=0;i <oldPhones.size();i++)
        {
            for (int j=0;j <newPhones.size();j++)
            {   
                //This means phone number is modified
                if((oldPhones.get(i).getPhoneType()==newPhones.get(j).getPhoneType()) )
                {
                    if(!((oldPhones.get(i).getPhoneNumber().trim()).equals(newPhones.get(j).getPhoneNumber().trim())))
                    {
                        str.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CUSTOMER_PH_TYPE, new Object[] {PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR[newPhones.get(j).getPhoneType()]}, journalLocale));
                        str.append(Util.EOL);
                        str.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_PHONENUMBER_LABEL, null, journalLocale), oldPhones.get(i).getPhoneNumber(),
                                newPhones.get(j).getPhoneNumber()));
                        str.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_EXTENSION_LABEL, null, journalLocale), oldPhones.get(i).getExtension(),
                                newPhones.get(j).getExtension())); 
                    }
                    isPhoneTypeSame=true;
                    changedNumbers.add(newPhones.get(j));
                    continue;
                }

                
                if(oldPhones.get(i).getPhoneType()!=newPhones.get(j).getPhoneType())
                {
                    //This means number is deleted from the new phone number list
                    if(j==newPhones.size()-1 && !isPhoneTypeSame)
                    {
                        str.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CUSTOMER_PH_TYPE, new Object[] {PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR[oldPhones.get(i).getPhoneType()]}, journalLocale));
                        str.append(Util.EOL);
                        str.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_PHONENUMBER_LABEL, null, journalLocale), oldPhones.get(i).getPhoneNumber(),
                                ""));
                        str.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_EXTENSION_LABEL, null, journalLocale), oldPhones.get(i).getExtension(),
                                "")); 
                        isPhoneTypeSame=false;
                    }

                }


            }
        }
        Set<PhoneIfc> set1=new HashSet<PhoneIfc>();
        set1.addAll(newPhones);

        Set<PhoneIfc> set2=new HashSet<PhoneIfc>();
        set2.addAll(changedNumbers);

        set1.removeAll(set2);
        for(PhoneIfc phone:set1)
        {   
            //This prints all the newly added number.
            str.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CUSTOMER_PH_TYPE, new Object[]{PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR [phone.getPhoneType()]}, journalLocale));
            str.append(Util.EOL);
            str.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_PHONENUMBER_LABEL, null, journalLocale), "",
                    phone.getPhoneNumber()));
            str.append(compareFields(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_EXTENSION_LABEL, null, journalLocale), "",
                    phone.getExtension()));
        }
        return (str.toString());
    }

    /**
     * Prints the field differences
     *
     * @param fieldName the field
     * @param field1 old value
     * @param field2 new value
     * @return string with data
     **/
    public static String printChange(String fieldName, String field1, String field2)
    {
        StringBuilder jString = new StringBuilder();

        if (field1 == null || field1.length() == 0)
            field1 = "";
        if (field2 == null || field2.length() == 0)
            field2 = "";

        jString.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_OLDDETAIL_LABEL,
                new Object[] { fieldName, field1 }, journalLocale));
        jString.append(Util.EOL);
        jString.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_NEWDETAIL_LABEL,
                new Object[] { fieldName, field2 }, journalLocale));
        jString.append(Util.EOL);
        return (jString.toString());
    }

    /**
     * Change boolean Answer to Yes/No
     *
     * @param value the value
     * @return string with data
     */
    public static String convertBoolean(boolean value)
    {
        String txt = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NO_LABEL, null,
                journalLocale);
        if (value)
        {
            txt = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.YES_LABEL, null,
                    journalLocale);
        }
        return (txt);
    }

    /**
     * Retrieve text value for phone type
     *
     * @param type value
     * @return string with data
     */
    public static String convertPhoneTypeToText(int type)
    {
        String str = "";
        if (type >= 0)
        {
            str = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PHONE_TYPE_MAIN
                    + PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR[type], null, journalLocale);
        }
        else
        {
            str = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MODIFY_UNSPECIFIED_LABEL,
                    null, journalLocale);
        }

        return str;
    }

    /**
     *
     * @param criteria
     * @param customer
     */
    public static void configureCustomerSearchCriteria(CustomerSearchCriteriaIfc criteria, CustomerIfc customer)
    {

        if(customer.isBusinessCustomer())
        {
            criteria.setCompanyName(customer.getCompanyName());
        }
        else
        {
            criteria.setFirstName(customer.getFirstName());
            criteria.setLastName(customer.getLastName());
        }

        if( !customer.getAddressList().isEmpty())
        {
            AddressIfc address = customer.getAddressList().get(0);
            criteria.setPostal(address.getPostalCode());
        }

        if(!customer.getPhoneList().isEmpty())
        {

            PhoneIfc phone = customer.getPhoneList().get(0);
            criteria.setPhoneNumber(phone.getPhoneNumber());

        }
    }

    /**
     * Returns all changes made to customer object
     *
     * @param customer the new customer information
     * @return string with data
     */
    public static String getNewCustomerJournalString(CustomerIfc customer)
    {
        StringBuilder jString = new StringBuilder("");
        Object[] dataArgs = new Object[2];

        // test every field
        if (customer.getCustomerID() != null && !customer.getCustomerID().equals(""))
        {
            dataArgs[0] = customer.getCustomerID();
            jString.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CUSTOMERID_LABEL, dataArgs));
        }
        if (customer.getEmployeeID() != null && !customer.getEmployeeID().equals(""))
        {
            dataArgs[0] = customer.getEmployeeID();
            jString.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.EMPLOYEE_ID_LABEL, dataArgs));
        }
        if (customer.getFirstName() != null && !customer.getFirstName().equals(""))
        {
            dataArgs[0] = customer.getFirstName();
            jString.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.FIRST_NAME_LABEL, dataArgs));
        }
        if (customer.isBusinessCustomer() && customer.getLastName() != null && !customer.getLastName().equals(""))
        {
            dataArgs[0] = customer.getLastName();
            jString.append(Util.EOL).append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.BUSINESS_CUSTOMER_NAME_LABEL, dataArgs));
        }
        else if (customer.getLastName() != null && !customer.getLastName().equals(""))
        {
            dataArgs[0] = customer.getLastName();
            jString.append(Util.EOL)
                    .append(
                            I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LAST_NAME_LABEL,
                                    dataArgs));
        }
        if (customer.getAddressList() != null)
        {
            for (AddressIfc address : customer.getAddressList())
            {
                int lineCount = 0;
                // loop through all address lines
                for (String addrLine : address.getLines())
                {
                    lineCount++;
                    if (addrLine != null && !addrLine.trim().equals(""))
                    {
                        dataArgs[0] = lineCount;
                        dataArgs[1] = addrLine;
                        jString
                                .append(Util.EOL)
                                .append(
                                        I18NHelper
                                                .getString(
                                                        I18NConstantsIfc.EJOURNAL_TYPE,
                                                        JournalConstantsIfc.ADDRESS_LINE_TAG_LABEL,
                                                        dataArgs));
                    }
                }
                dataArgs[0] = address.getCity();
                jString.append(Util.EOL).append(
                        I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.CITY_LABEL, dataArgs));
                dataArgs[0] = address.getCountry();
                jString.append(Util.EOL).append(
                        I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.COUNTRY_LABEL, dataArgs));
                dataArgs[0] = address.getState();
                jString.append(Util.EOL).append(
                        I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.STATE_LABEL, dataArgs));
                dataArgs[0] = address.getPostalCode();
                jString.append(Util.EOL).append(
                        I18NHelper
                                .getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                        JournalConstantsIfc.POSTAL_CODE_LABEL,
                                        dataArgs));
                if (address.getPostalCodeExtension() != null
                        && !address.getPostalCodeExtension().trim().equals(""))
                    jString.append(" - " + address.getPostalCodeExtension());

            }
        }

        // find customer tax id
        if (customer.getEncipheredTaxID() != null
            && customer.getEncipheredTaxID().getMaskedNumber() != null
            && !customer.getEncipheredTaxID().getMaskedNumber().equals(""))
        {

            dataArgs[0] = customer.getEncipheredTaxID().getMaskedNumber();
            jString.append(Util.EOL)
                    .append(
                            I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CUSTOMER_TAX_ID,
                                    dataArgs));

        }

        // set customer pricing group
        if (customer.getPricingGroupID() != null)
        {
            dataArgs[0] = customer.getPricingGroupID();
            jString.append(Util.EOL)
                    .append(
                            I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CUSTOMER_PRICING_GROUP,
                                    dataArgs));
        }

        // loop through all the phone numbers
        for (int i = 0; i < PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR.length; i++)
        {
            PhoneIfc phone = customer.getPhoneByType(i);
            if (phone != null)
            {
                jString.append(phone.toJournalString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
            }
        }

        // find email address
        EmailAddressIfc emailAddress = customer.getEmailAddress(0);
        if ((emailAddress != null)&& !(emailAddress.getEmailAddress().trim()).equals(""))
        {
            String email = emailAddress.getEmailAddress();
            if (email != null && !email.trim().equals(""))
                dataArgs[0] = email;
            jString.append(Util.EOL).append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.EMAIL_ADDRESS_LABEL, dataArgs));
        }

        // Get associated discount
        CustomerGroupIfc[] groups = customer.getCustomerGroups();
        DiscountRuleIfc[] rules = null;
        if (groups != null)
        {
            for (int i = 0; i < groups.length; i++)
            {
                rules = groups[i].getDiscountRules();
                if (rules != null && rules.length > 0)
                {
                    String discount = rules[0].getName(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
                    if (discount != null && !discount.trim().equals(""))
                    {
                        dataArgs[0] = discount;
                        jString.append(Util.EOL).append(
                                I18NHelper.getString(
                                        I18NConstantsIfc.EJOURNAL_TYPE,
                                        JournalConstantsIfc.DISCOUNT_LABEL,
                                        dataArgs));
                    }
                }
            }
        }

        // set tax certificate
        byte[] taxCertificate = new byte[0];
        try
        {
            taxCertificate = customer.getEncipheredTaxCertificate().getDecryptedNumber();
            if (customer.getEncipheredTaxCertificate() != null && taxCertificate.length != 0)
            {
                dataArgs[0] = customer.getEncipheredTaxCertificate().getMaskedNumber();
                jString.append(Util.EOL)
                        .append(
                                I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CUSTOMER_TAX_CERTF,
                                        dataArgs));
            }
        }
        finally
        {
            Util.flushByteArray(taxCertificate);
        }

        // set tax exemption reason
        LocalizedCodeIfc taxExemptReason = customer.getTaxExemptionReason();
        if(taxExemptReason != null && taxExemptReason.getText(journalLocale) != null && !taxExemptReason.getText(journalLocale).equals(""))
        {
            if(taxExemptReason.getText(journalLocale) !=null && !taxExemptReason.getText(journalLocale).equals(""))
            {
                dataArgs[0] = taxExemptReason.getText(journalLocale) ;
                jString.append(Util.EOL)
                .append(
                        I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CUSTOMER_EXEMPTION_REASON,
                                dataArgs));
            }
        }

        return (jString.toString());
    }

}
