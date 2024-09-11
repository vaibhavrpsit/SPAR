/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/customer/LogCustomer.java /main/14 2012/09/12 11:57:19 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tzgarba   08/27/12 - Merge project Echo (MPOS) into trunk.
 *    cgreene   04/03/12 - removed deprecated methods
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    cgreene   09/02/11 - update deprecated tax methods
 *    cgreene   08/22/11 - removed deprecated methods
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    ranojha   11/05/08 - Fixed Tax Exempt Reason Code for Customer
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         12/18/2007 5:47:48 PM  Alan N. Sinton  CR
 *         29661: Changes per code review.
 *    3    360Commerce 1.2         3/31/2005 4:28:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:22 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 17:13:38  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:29  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:36:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Jul 01 2003 14:09:30   jgs
 * Modifications for new 6.0 data.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 * 
 *    Rev 1.3   May 18 2003 09:06:34   mpm
 * Merged 5.1 changes into 6.0
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.2   Feb 15 2003 14:52:06   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.1   Jan 22 2003 09:55:56   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   Sep 05 2002 11:12:38   msg
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.customer;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.customer.CustomerConstantsIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.domain.ixretail.utility.LogAddressIfc;
import oracle.retail.stores.domain.ixretail.utility.LogPhoneIfc;
import oracle.retail.stores.domain.utility.EmailAddressConstantsIfc;
import oracle.retail.stores.domain.utility.EmailAddressIfc;
import oracle.retail.stores.domain.utility.Gender;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

/**
 * This class creates the elements for a RetailTransactionCustomer element.
 * 
 */
public class LogCustomer extends AbstractIXRetailTranslator implements LogCustomerIfc
{

    /**
     * Constructs LogCustomer object.
     */
    public LogCustomer()
    {
        setTypeName(IXRetailConstantsIfc.TYPE_CUSTOMER_360);
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
        setParentDocument(doc);
        setParentElement(el);

        Element customerElement = parentDocument.createElement
          (IXRetailConstantsIfc.ELEMENT_CUSTOMER);

        customerElement.setAttribute
          (IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
           getTypeName());

        // only last name is used because first-last name is too long to fit into database
        // column at import-data-from-xml time
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_NAME,
           // customer.getFirstLastName(),
           customer.getLastName(),
           customerElement);

        createAddressElement(customer,
                             doc,
                             customerElement);

        // this creates the IXRetail standard phone element
        // 360 phone entries are at bottom of schema
        createTelephoneNumberElement(customer,
                                     customerElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_CUSTOMER_ID,
           customer.getCustomerID(),
           customerElement);

        // create first name element
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_FIRST_NAME,
           customer.getFirstName(),
           customerElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_MIDDLE_NAME,
           customer.getMiddleName(),
           customerElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_LAST_NAME,
           customer.getLastName(),
           customerElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_SALUTATION,
           customer.getSalutation(),
           customerElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_NAME_SUFFIX,
           customer.getNameSuffix(),
           customerElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COMPANY_NAME,
           customer.getCompanyName(),
           customerElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_FULL_NAME,
           customer.getCustomerName(),
           customerElement);

        if (customer.getBirthdate() != null)
        {
            createDateTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_DATE_OF_BIRTH,
               customer.getBirthdate(),
               customerElement);
        }

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_YEAR_OF_BIRTH,
           customer.getYearOfBirth(),
           customerElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_GENDER,
           Gender.GENDER_DESCRIPTOR[customer.getGenderCode()],
           customerElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_ACCOUNT_NUMBER,
           customer.getHouseCardData().getEncryptedAcctNumber(),
           customerElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_EMPLOYEE_ID,
           customer.getEmployeeID(),
           customerElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_RECORD_ID,
           customer.getRecordID(),
           customerElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_EMAIL_PRIVACY,
           customer.getEMailPrivacy(),
           customerElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_MAIL_PRIVACY,
           customer.getMailPrivacy(),
           customerElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_TELEPHONE_PRIVACY,
           customer.getTelephonePrivacy(),
           customerElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_STATUS,
           CustomerConstantsIfc.CUSTOMER_STATUS_DESCRIPTORS[customer.getStatus()],
           customerElement);

        Iterator<EmailAddressIfc> iter = customer.getEmailAddresses();
        Element eMailElement = null;
        while (iter.hasNext())
        {
            eMailElement = parentDocument.createElement
              (IXRetailConstantsIfc.ELEMENT_EMAIL_ADDRESS);

            EmailAddressIfc eMail = iter.next();

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_ADDRESS,
               eMail.getEmailAddress(),
               eMailElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_ADDRESS_TYPE,
               EmailAddressConstantsIfc.ADDRESS_TYPE_DESCRIPTOR[eMail.getEmailAddressType()],
               eMailElement);

            customerElement.appendChild(eMailElement);
        }

        createPhoneElements(customer.getPhoneList().iterator(),
                            customerElement);

        if (customer.getPreferredLocale() != null)
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_LOCALE,
                customer.getPreferredLocale().toString(), customerElement);
        }
        
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_BUSINESS_CUSTOMER,
           customer.isBusinessCustomer(),
           customerElement);

        if (customer.isBusinessCustomer())
        {
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_TAX_CERTIFICATE,
               customer.getEncipheredTaxCertificate().getEncryptedNumber(),
               customerElement);
    
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_TAX_EXEMPT_REASON,
               customer.getTaxExemptionReason().getCode(),
               customerElement);
        }
        
        appendElements(customer, customerElement);
        parentElement.appendChild(customerElement);

        return(customerElement);
    }

    /**
     * Create element for address.
     * 
     * @param customer customer object
     * @param doc parent document
     * @param el customer element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createAddressElement(CustomerIfc customer, Document doc, Element el) throws XMLConversionException
    {
        LogAddressIfc logAddress = IXRetailGateway.getFactory().getLogAddressInstance();
        logAddress.createElement(customer.getAddressList().get(0), doc, el);
    }

    /**
     * Create element for IXRetail standard telephone number.
     * 
     * @param customer customer object
     * @param customerElement customer element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTelephoneNumberElement(CustomerIfc customer, Element customerElement)
            throws XMLConversionException
    {
        PhoneIfc phone = customer.getPhoneList().get(0);
        if (phone != null)
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TELEPHONE_NUMBER, phone.getPhoneNumber(),
                    customerElement);
        }
    }

    /**
     * Creates element for phone objects.
     * 
     * @param iter iterator for phones vector
     * @param el parent element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createPhoneElements(Iterator<PhoneIfc> iter, Element el) throws XMLConversionException
    {
        if (iter.hasNext())
        {
            LogPhoneIfc phoneLogger = IXRetailGateway.getFactory().getLogPhoneInstance();

            while (iter.hasNext())
            {
                phoneLogger.createElement(iter.next(), parentDocument, el);
            }
        }
    }
}
