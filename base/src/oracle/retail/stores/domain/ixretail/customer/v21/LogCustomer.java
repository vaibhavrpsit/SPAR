/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/customer/v21/LogCustomer.java /main/21 2012/11/23 12:52:40 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  11/23/12 - Customer receipt preference quickwin changes
 *    tzgarba   08/27/12 - Merge project Echo (MPOS) into trunk.
 *    cgreene   04/03/12 - removed deprecated methods
 *    rsnayak   10/21/11 - Tax Certificate Fix
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    cgreene   08/22/11 - removed deprecated methods
 *    mkutiana  08/16/11 - Removed depricated hashAccount field
 *                         EncipheredCardData and related classes
 *    rrkohli   07/18/11 - encryption CR
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    acadar    03/05/09 - use code instead of name, fix the result set problem
 *    sswamygo  02/12/09 - updated to set codeName which will be used for
 *                         POSLog
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *    ranojha   11/05/08 - Fixed Tax Exempt Reason Code for Customer
 *    mdecama   10/27/08 - I18N - Refactoring Reason Codes for
 *                         CaptureCustomerIDTypes
 * ===========================================================================
     $Log:
      7    360Commerce 1.6         1/7/2008 2:42:09 PM    Alan N. Sinton  CR
           29849: Update to POSLog export for customer table refactor.  These
           files were reviewed by Tony Zgarba.
      6    360Commerce 1.5         12/18/2007 5:47:48 PM  Alan N. Sinton  CR
           29661: Changes per code review.
      5    360Commerce 1.4         2/8/2006 10:32:19 AM   Jason L. DeLeau 5373:
            Make sure a pos log is generated for the post void of a redeemed
           gift card.
      4    360Commerce 1.3         1/25/2006 4:11:29 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:28:53 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:10 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:22 PM  Robert Pearse
     $:
      4    .v700     1.2.1.0     12/27/2005 17:42:27    Rohit Sachdeva
           c360:Name being empty
      3    360Commerce1.2         3/31/2005 15:28:53     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:23:10     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:22     Robert Pearse
     $
     Revision 1.4  2004/08/10 07:17:12  mwright
     Merge (3) with top of tree

     Revision 1.3.2.2  2004/08/08 21:20:44  mwright
     Added ID Type for capture customer

     Revision 1.3.2.1  2004/07/29 00:50:19  mwright
     No functional change, contains commented-out dump of all customer information

     Revision 1.3  2004/06/24 09:15:10  mwright
     POSLog v2.1 (second) merge with top of tree

     Revision 1.2.2.1  2004/06/10 10:45:49  mwright
     Updated to use schema types in commerce services

     Revision 1.2  2004/05/06 03:09:52  mwright
     Initial revision for POSLog v2.1

     Revision 1.1.2.2  2004/04/26 07:02:46  mwright
     Changes to use 360 addrss and customer types, instead of the unextendable ixretail ones

     Revision 1.1.2.1  2004/04/13 06:41:51  mwright
     Initial revision for v2.1

 *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.ixretail.customer.v21;

import java.util.Iterator;

import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.EmailAddress360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionAddress360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionCustomer360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.Telephone360Ifc;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.customer.CustomerConstantsIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsV21Ifc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.customer.LogCustomerIfc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.domain.ixretail.utility.LogAddressIfc;
import oracle.retail.stores.domain.ixretail.utility.LogPhoneIfc;
import oracle.retail.stores.domain.utility.EmailAddressConstantsIfc;
import oracle.retail.stores.domain.utility.EmailAddressIfc;
import oracle.retail.stores.domain.utility.Gender;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class creates the elements for a RetailTransactionCustomer element.
 * 
 */
public class LogCustomer extends AbstractIXRetailTranslator implements LogCustomerIfc, IXRetailConstantsV21Ifc
{
    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(LogCustomer.class);

    /**
     * Constructs LogCustomer object.
     */
    public LogCustomer()
    {
    }

    /**
     * Creates element for the specified customer.
     * 
     * @param customer customer to be translated
     * @param doc parent document
     * @param el parent element
     * @return Element representing customer object
     * @exception XMLConversionException thrown if error occurs
     */
    public Element createElement(CustomerIfc customer, Document doc, Element el) throws XMLConversionException
    {
        RetailTransactionCustomer360Ifc customerElement = (RetailTransactionCustomer360Ifc)el;

        if(customer != null)
        {
            if(logger.isDebugEnabled())
            {
                StringBuilder debugMessage = new StringBuilder();
                debugMessage.append("*** Logging customer:");
                debugMessage.append("\nCustomer name:");
                debugMessage.append(customer.getCustomerName());
                debugMessage.append("\nCustomer company name:");
                debugMessage.append(customer.getCompanyName());
                debugMessage.append("\nCustomer ID:");
                debugMessage.append(customer.getCustomerID());
                debugMessage.append("\nCustomer first last name:");
                debugMessage.append(customer.getFirstLastName());
                debugMessage.append("\nCustomer first name:");
                debugMessage.append(customer.getFirstName());
                debugMessage.append("\nCustomer last name:");
                debugMessage.append(customer.getLastName());
                debugMessage.append("\nCustomer middle name:");
                debugMessage.append(customer.getMiddleName());
                logger.debug(debugMessage.toString());
            }
        	if (customer instanceof CaptureCustomerIfc)
        	{
        		CaptureCustomerIfc capture = (CaptureCustomerIfc)customer;
        		customerElement.setIdType(capture.getPersonalIDType().getCode());
        	}



        	// only last name is used because first-last name is too long to fit into database
        	// column at import-data-from-xml time
        	customerElement.setName(customer.getCustomerName());        // this is the full name, used in contact first-last name field


        	createAddressElement(customer, doc, customerElement);       // element in customer

        	// this creates the IXRetail standard phone element (mandatory!!!!)
        	// NOTE: This element is duplicated in the full list of phone elements
        	createTelephoneNumberElement(customer, customerElement);

        	customerElement.setCustomerID(customer.getCustomerID());
        	customerElement.setFirstName(customer.getFirstName());
        	customerElement.setMiddleName(customer.getMiddleName());
        	customerElement.setLastName(customer.getLastName());        // this gets the full name ?????

        	customerElement.setSalutation(customer.getSalutation());
        	customerElement.setNameSuffix(customer.getNameSuffix());
        	customerElement.setCompanyName(customer.getCompanyName());

        	// setting encrypted/masked taxID into customer
        	customerElement.setEncryptedTaxID(customer.getEncipheredTaxID().getEncryptedNumber());
        	customerElement.setMaskedTaxID(customer.getEncipheredTaxID().getMaskedNumber());

        	if (Util.isEmpty(customerElement.getName()))
        	{
        		if (!customer.isBusinessCustomer())
        		{
        			customerElement.setName(customerElement.getFirstName() + " " + customerElement.getLastName());
        		}
        	}

        	if (customer.getBirthdate() != null)
        	{
        		customerElement.setBirthdate(dateValue(customer.getBirthdate()));
        	}

        	customerElement.setYearOfBirth(Long.toString(customer.getYearOfBirth()));

        	customerElement.setGender(Gender.GENDER_DESCRIPTOR[customer.getGenderCode()]);      // defined by ixretail
        	customerElement.setAccountNumber(customer.getHouseCardData().getEncryptedAcctNumber());                 // defined by ixretail
        	customerElement.setMaskedAccountNumber(customer.getHouseCardData().getMaskedAcctNumber());

        	customerElement.setEmployeeID(customer.getEmployeeID());
        	customerElement.setRecordID(customer.getRecordID());

        	// v1.0 set privacy explicitly for email, mail and phone.
        	// v2.1 sets all 3 in a list:
        	if (customer.getEMailPrivacy())
        	{
        		customerElement.addPrivacy(ENUMERATION_PRIVACY_EMAIL);
        	}
        	if (customer.getMailPrivacy())
        	{
        		customerElement.addPrivacy(ENUMERATION_PRIVACY_MAIL);
        	}
        	if (customer.getTelephonePrivacy())
        	{
        		customerElement.addPrivacy(ENUMERATION_PRIVACY_PHONE);
        	}
        	
        	customerElement.setReceiptPreference(customer.getReceiptPreference());


        	customerElement.setStatus(CustomerConstantsIfc.CUSTOMER_STATUS_DESCRIPTORS[customer.getStatus()]);

        	Iterator<EmailAddressIfc> iter = customer.getEmailAddresses();
        	while (iter.hasNext())
        	{
        		EmailAddressIfc eMail = iter.next();
        		EmailAddress360Ifc emailAddress = getSchemaTypesFactory().getEmailAddressInstance();
        		emailAddress.setEmailAddress(eMail.getEmailAddress());
        		emailAddress.setEmailAddresType(EmailAddressConstantsIfc.ADDRESS_TYPE_DESCRIPTOR[eMail.getEmailAddressType()]);
        		customerElement.addEmailAddress(emailAddress);
        	}

        	createPhoneElements(customer.getPhoneList().iterator(), customerElement);  // duplicates first phone number already stored...

        	if (customer.getPreferredLocale() != null)
        	{
        		customerElement.setLocale(customer.getPreferredLocale().toString());
        	}

        	customerElement.setBusinessCustomer(new Boolean(customer.isBusinessCustomer()));

        	if (customer.isBusinessCustomer())
        	{
        	    EncipheredDataIfc encipheredTaxCertificate = customer.getEncipheredTaxCertificate();
        	    customerElement.setEncryptedTaxCertificate(encipheredTaxCertificate.getEncryptedNumber());
        	    customerElement.setMaskedTaxCertificate(encipheredTaxCertificate.getMaskedNumber());
                if (customer.getTaxExemptionReason()!=null)
                {
                  customerElement.setTaxExemptReason(customer.getTaxExemptionReason().getCode());
                }
        	}
        }
        return customerElement;

    }

    /**
     * Create element for address.
     * 
     * @param customer customer object
     * @param doc parent document
     * @param customerElement customer element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createAddressElement(CustomerIfc customer, Document doc,
            RetailTransactionCustomer360Ifc customerElement) throws XMLConversionException
    {
        RetailTransactionAddress360Ifc address = getSchemaTypesFactory().getRetailTransactionAddressInstance();
        LogAddressIfc logAddress = IXRetailGateway.getFactory().getLogAddressInstance();
        logAddress.createElement(customer.getAddressList().get(0), doc, address);
        customerElement.setAddress(address);
    }

    /**
     * Create element for IXRetail standard telephone number.
     * 
     * @param customer customer object
     * @param customerElement customer element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTelephoneNumberElement(CustomerIfc customer, RetailTransactionCustomer360Ifc customerElement)
            throws XMLConversionException
    {
        PhoneIfc phone = customer.getPhoneList().get(0);
        if (phone != null)
        {
            customerElement.setTelephoneNumber(phone.getPhoneNumber());
        }
        else
        {
            customerElement.setTelephoneNumber(""); // mandatory element
        }
    }

    /**
     * Creates element for phone objects.
     * 
     * @param iter iterator for phones vector
     * @param customerElement parent element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createPhoneElements(Iterator<PhoneIfc> iter, RetailTransactionCustomer360Ifc customerElement)
            throws XMLConversionException
    {
        if (iter.hasNext())
        {
            LogPhoneIfc phoneLogger = IXRetailGateway.getFactory().getLogPhoneInstance();

            while (iter.hasNext())
            {
                Telephone360Ifc phoneElement = getSchemaTypesFactory().getPhoneElementInstance();
                phoneLogger.createElement(iter.next(), parentDocument, phoneElement);
                customerElement.addPhone(phoneElement);
            }
        }
    }
}
