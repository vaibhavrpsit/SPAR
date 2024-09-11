/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transform/entity/customer/CustomerTransformer.java /main/33 2013/12/11 11:24:09 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  12/09/13 - Added Javadocs
 *    abondala  11/01/13 - fix the reconnect and mail issues when the
 *                         webservice hosting server comes back online
 *    rgour     06/07/13 - marking a phone record as deleted if it is removed
 *    icole     02/01/13 - Changes to customer first and last names not
 *                         reflected on UI nor receipt nor persisted.
 *    abondala  01/28/13 - avoid adding duplicate phones during transformations
 *    abondala  01/27/13 - extending JPA
 *    abondala  01/15/13 - customer update
 *    abondala  01/15/13 - remove the existing customer group liked to a
 *                         customer if NONE is selected for the group while
 *                         updating a cutomer.
 *    abondala  01/10/13 - support extending jpa
 *    abondala  01/03/13 - transformers refactored
 *    abondala  12/17/12 - fixed the NPE issues for the non-required fields
 *    abondala  12/13/12 - changed the datatype from string to int for the
 *                         receipt preference in the contact table
 *    hyin      12/03/12 - fix NULL exception.
 *    mchellap  11/30/12 - Code review changes
 *    mchellap  11/23/12 - Customer receipt preference changes
 *    rabhawsa  10/26/12 - tax certificate should be encrypted in db
 *    abondala  09/13/12 - check for an encrypted tax id if it is blank before
 *                         decrypting
 *    acadar    08/21/12 - fixes for pricing group id
 *    abondala  08/17/12 - fixing the exisitng customer updates and few other
 *                         issues
 *    acadar    08/16/12 - bug fixes
 *    acadar    08/15/12 - support for multiple XC shipping addresses
 *    acadar    08/14/12 - merged with tip
 *    acadar    08/14/12 - fixes
 *    acadar    08/10/12 - merged with latest label
 *    abondala  08/10/12 - added couple more jpa operation classes for missing
 *                         operations
 *    abondala  08/09/12 - customer jpa related changes
 *    acadar    08/08/12 - merged
 *    acadar    08/07/12 - set the status code
 *    acadar    08/07/12 - cleanup
 *    acadar    08/07/12 - updates
 *    abondala  08/05/12 - close the manager in the jpaDatacommand after
 *                         execution.
 *    acadar    08/05/12 - XC refactoring
 *    acadar    08/03/12 - refactoring
 *    acadar    08/01/12 - changes for JPA
 *    acadar    08/01/12 - new transformer
 * ===========================================================================
 */
package oracle.retail.stores.domain.transform.entity.customer;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerConstantsIfc;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.ContactIfc;
import oracle.retail.stores.domain.utility.EmailAddressIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.storeservices.entities.customer.Address;
import oracle.retail.stores.storeservices.entities.customer.AddressID;
import oracle.retail.stores.storeservices.entities.customer.BusinessCustomer;
import oracle.retail.stores.storeservices.entities.customer.Contact;
import oracle.retail.stores.storeservices.entities.customer.Customer;
import oracle.retail.stores.storeservices.entities.customer.CustomerAffiliation;
import oracle.retail.stores.storeservices.entities.customer.EmailAddress;
import oracle.retail.stores.storeservices.entities.customer.EmailAddressID;
import oracle.retail.stores.storeservices.entities.customer.Phone;
import oracle.retail.stores.storeservices.entities.customer.PhoneID;
import oracle.retail.stores.transform.TransformerIfc;

import org.apache.log4j.Logger;

/**
 * The CustomerTransformer is a utility class for converting between the set of 
 * Customer Domain objects oracle.retail.stores.domain.customer.CustomerIfc and
 * Customer Entity objects(JPA) {@link oracle.retail.stores.storeservices.entities.customer.Customer}.
 * <p>
 * The CustomerTransformer is a spring loaded bean defined in TransformerContext.xml
 * @since 14.0
 */
public class CustomerTransformer implements CustomerTransformerIfc, TransformerIfc
{

    /**
     * Static Logger for this class
     */
    protected static Logger logger = Logger.getLogger(CustomerTransformer.class);

    /**
     * The AddressTransformerIfc is a utility class for transformation of objects between 
     * the domain objects oracle.retail.stores.domain.utility.AddressBookEntryIfc and oracle.retail.stores.domain.utility.AddressIfc
     * and JPA entity {@link oracle.retail.stores.storeservices.entities.customer.Address}. 
     * This is used to transform the customers Address information.
     */
    protected AddressTransformerIfc addressDomainTransformer;
    
    /**
     * The PhoneTransformerIfc is a utility class for transformation of objects between  
     * the domain objects oracle.retail.stores.domain.utility.PhoneIfc
     * and JPA entity {@link oracle.retail.stores.storeservices.entities.customer.Phone}. 
     * This is utilized to transform the customers Phone information
     */
    protected PhoneTransformerIfc phoneDomainTransformer;
    
    /**
     * The EmailTransformerIfc is a utility class for transformation of objects between 
     * the domain objects oracle.retail.stores.domain.utility.EmailAddressIfc 
     * and entity {@link oracle.retail.stores.storeservices.entities.customer.EmailAddress}. 
     * This is utilized to transform the Customers Email information.
     */
    protected EmailTransformerIfc emailDomainTransformer;  
    
    /**
     * The ContactTransformerIfc is a utility class for transformation of objects between 
     * the domain objects oracle.retail.stores.domain.utility.ContactIfc 
     * and entity {@link oracle.retail.stores.storeservices.entities.customer.Contact}. 
     * This is utilized to transform the Customers Contact information
     */
    protected ContactTransformerIfc contactDomainTransformer;
    
    /**
     * The CustomerGroupTransformerIfc is a utility class for transformation of objects between 
     * the domain objects  oracle.retail.stores.domain.customer.CustomerGroupIfc
     * and entities {@link oracle.retail.stores.storeservices.entities.customer.CustomerAffiliation}, {@link oracle.retail.stores.storeservices.entities.customer.CustomerGroup}.
     * This is utilized during the transformation of the Customers Group and Affiliation and Discount information. 
     */
    protected CustomerGroupTransformerIfc customerGroupDomainTransformer;
    
    /**
    * Returns the CustomerGroupTransformerIfc
    * @return CustomerGroupTransformerIfc
    */
    protected CustomerGroupTransformerIfc getCustomerGroupDomainTransformer()
    {
       return customerGroupDomainTransformer;
    }

    /**
    * Returns the ContactTransformerIfc
    * @return ContactTransformerIfc
    */
    protected ContactTransformerIfc getContactDomainTransformer()
    {
       return contactDomainTransformer;
    }

    /**
     * Returns the AddressTransformerIfc.
     * @return AddressTransformerIfc
     */
    protected AddressTransformerIfc getAddressDomainTransformer()
    {
        return addressDomainTransformer;
    }

    /**
     * Returns the EmailTransformerIfc.
     * @return EmailTransformerIfc
     */
    protected EmailTransformerIfc getEmailDomainTransformer()
    {
      return emailDomainTransformer;
    }
    
    /**
     * Returns the PhoneTransformerIfc.
     * @return PhoneTransformerIfc
     */
    protected PhoneTransformerIfc getPhoneDomainTransformer()
    {
        return phoneDomainTransformer;
    }
    
    /**
     * Set the AddressTransformerIfc. In the default product implementation it is spring loaded as defined in TransformerContext.xml.
     * @param addressDomainTransformer the addressDomainTransformer to set
     */
    public void setAddressDomainTransformer(AddressTransformerIfc addressDomainTransformer)
    {
        this.addressDomainTransformer = addressDomainTransformer;
    }
    
    /**
     * Set the PhoneTransformerIfc. In the default product implementation it is a spring loaded as defined in TransformerContext.xml.
     * @param phoneDomainTransformer the phoneDomainTransformer to set
     */
    public void setPhoneDomainTransformer(PhoneTransformerIfc phoneDomainTransformer)
    {
        this.phoneDomainTransformer = phoneDomainTransformer;
    }
    
    /**
     * set the EmailTransformerIfc. In the default product implementation it is a spring loaded as defined in TransformerContext.xml.
     * @param emailDomainTransformer the emailDomainTransformer to set
     */
    public void setEmailDomainTransformer(EmailTransformerIfc emailDomainTransformer)
    {
        this.emailDomainTransformer = emailDomainTransformer;
    }
    
    /**
     * Set the CustomerGroupTransformerIfc. In the default product implementation it is a spring loaded as defined in TransformerContext.xml.
     * @param customerGroupDomainTransformer the customerGroupDomainTransformer to set
     */
    public void setCustomerGroupDomainTransformer(CustomerGroupTransformerIfc customerGroupDomainTransformer)
    {
        this.customerGroupDomainTransformer = customerGroupDomainTransformer;
    }
    
    /**
     * Set the ContactTransformerIfc. In the default product implementation it is a spring loaded as defined in TransformerContext.xml.
     * @param contactDomainTransformer the contactDomainTransformer to set
     */
     public void setContactDomainTransformer(ContactTransformerIfc contactDomainTransformer)
     {
         this.contactDomainTransformer = contactDomainTransformer;
     }
    
    /**
     * Utility method used to transform a {@link oracle.retail.stores.storeservices.entities.customer.Customer} JPA Entity 
     * into a oracle.retail.stores.domain.customer.CustomerIfc domain object.
     * @param customerEntity Customer instance to be transformed
     * @param localeReq LocaleRequestor
     * @return CustomerIfc
     */
    protected CustomerIfc toBaseDomain(Customer customerEntity, LocaleRequestor localeReq)
    {
        CustomerIfc customer = DomainGateway.getFactory().getCustomerInstance();
        //set customer id
        customer.setCustomerID(customerEntity.getCustomerID());
        customer.setStatus(customerEntity.getCustomerStatusCode());
       
        customer.setRecordID(Integer.toString(customerEntity.getPartyID()));

        //set employeeId
        customer.setEmployeeID(customerEntity.getEmployeeID());

        //set full name
        customer.setCustomerName(customerEntity.getCustomerName());
        
       if(!customerEntity.getBusinessCustomers().isEmpty())
       {
           BusinessCustomer bsc = customerEntity.getBusinessCustomers().iterator().next();
           customer.setCompanyName(bsc.getOrganizationName());

           customer.setCustomerName(bsc.getOrganizationName());
           customer.setBusinessCustomer(true);
           
           if (!Util.isEmpty(bsc.getEncryptedTaxExemptionCertificate()))
           {    
               EncipheredDataIfc taxCertificateData = FoundationObjectFactory.getFactory().createEncipheredDataInstance(bsc.getEncryptedTaxExemptionCertificate());
               customer.setEncipheredTaxCertificate(taxCertificateData);        
           }
           
           LocalizedCodeIfc reasonCode = DomainGateway.getFactory().getLocalizedCode(bsc.getExemptionReason());
           customer.setTaxExemptionReason(reasonCode);
       }

        //set preferred locale
        if(customerEntity.getCustomerPreferredLocale() != null)
        {
            customer.setPreferredLocale(getLocale(customerEntity.getCustomerPreferredLocale()));
        }

        //tax id
        if(!Util.isBlank(customerEntity.getEncryptedCustomerTaxID()))
        {
            EncipheredDataIfc taxData = FoundationObjectFactory.getFactory().createEncipheredDataInstance(customerEntity.getEncryptedCustomerTaxID(), customerEntity.getMaskedCustomerTaxID());
            customer.setEncipheredTaxID(taxData);
       }
  
        //pricing group
        customer.setPricingGroupID(customerEntity.getPricingGroupID());
        
        if(!customerEntity.getContacts().isEmpty())
        {
            Contact contact = customerEntity.getContacts().get(0);
            customer.setEMailPrivacy(contact.isAllowEmail());
            customer.setTelephonePrivacy(contact.isAllowTelephone());
            customer.setMailPrivacy(contact.isAllowMail());
            customer.setGenderCode(contact.getGender());
            customer.setReceiptPreference(contact.getReceiptPreference());
            
        }

        return customer;
    }
    
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.CustomerTransformerIfc#transform
     *           (oracle.retail.stores.storeservices.entities.customer.Customer, oracle.retail.stores.common.utility.LocaleRequestor)
     */
    public CustomerIfc transform(Customer entity, LocaleRequestor localeReq)
    {
        CustomerIfc customer = toBaseDomain(entity, localeReq);
        // set contact information
        if(!entity.getContacts().isEmpty())
        {
            Contact contact= entity.getContacts().iterator().next();
            customer.setContact(getContactDomainTransformer().transform(contact));
        }

       //address
       if (!entity.getAddresses().isEmpty())
       {
           for (Address entry : entity.getAddresses())
           {
               customer.addAddressBookEntry(getAddressDomainTransformer().transform(entry));
           }
       }

       //Get the phones
       if (entity.getPhones()!= null && !entity.getPhones().isEmpty())
       {
           for (Phone phone: entity.getPhones())
           {
        	   PhoneIfc domainPhone = getPhoneDomainTransformer().transform(phone);
        	   if(!customer.getPhoneList().contains(domainPhone) )
        	   {
        		   customer.addPhone(domainPhone);
        	   }
           }
       }

       //Get emails
       if(entity.getEmailAddresses() != null && !entity.getEmailAddresses().isEmpty())
       {
       
           EmailAddress emailEntity = entity.getEmailAddresses().iterator().next();
           customer.setEmailAddress(getEmailDomainTransformer().transform(emailEntity));
       }
       
       //get customer groups
       
       //customer groups
       if (!entity.getCustomerAffiliations().isEmpty())
       {
           for( CustomerAffiliation cAffil: entity.getCustomerAffiliations())
           {
               CustomerGroupIfc cGroup = getCustomerGroupDomainTransformer().transform(cAffil, localeReq);
              
               customer.addCustomerGroup(cGroup);
           }
       }
       
       return customer;
    }
    
    /**
     * Utility method used to transform a  oracle.retail.stores.domain.customer.CustomerIfc domain object 
     * into a {@link oracle.retail.stores.storeservices.entities.customer.Customer} JPA Entity.
     * @param customer CustomerIfc instance to be transformed
     * @return  Customer
     */
    protected Customer toBaseEntity(CustomerIfc customer)
    {
        Customer entity = new Customer();
        
        //set customer id
        entity.setCustomerID(customer.getCustomerID());
        
        if(!customer.getRecordID().isEmpty())
        {
            entity.setPartyID(Integer.parseInt(customer.getRecordID()));
        }

        //set employeeId
        entity.setEmployeeID(customer.getEmployeeID());

        //set full name
        entity.setCustomerName(customer.getCustomerName());
        
        //set the status of the customer
        entity.setCustomerStatusCode(customer.getStatus());
        
       if(customer.isBusinessCustomer())
       {
           BusinessCustomer bsc = new BusinessCustomer();
           bsc.setOrganizationName(customer.getLastName());
           if(customer.getTaxExemptionReason() != null)
           {
               bsc.setExemptionReason(customer.getTaxExemptionReason().getCode());
           }    
           
           if(customer.getEncipheredTaxCertificate() != null)
           {
               bsc.setEncryptedTaxExemptionCertificate(customer.getEncipheredTaxCertificate().getEncryptedNumber());
               bsc.setMaskedTaxExemptionCertificate(customer.getEncipheredTaxCertificate().getMaskedNumber());
           }
           entity.addBusinessCustomer(bsc);
           entity.setCustomerType(CustomerConstantsIfc.CUSTOMER_TYPE_BUSINESS);
       }

       
       if(customer.getPreferredLocale() != null)
       {
           entity.setCustomerPreferredLocale(customer.getPreferredLocale().toString());
       }
       
       if(customer.getEncipheredTaxID() != null)
       {
           entity.setEncryptedCustomerTaxID(customer.getEncipheredTaxID().getEncryptedNumber());
           entity.setMaskedCustomerTaxID(customer.getEncipheredTaxID().getMaskedNumber());
       }

        //pricing group
        if(customer.getPricingGroupID() != null)
        {
            entity.setPricingGroupID(customer.getPricingGroupID());

        }
        
        return entity;
    }
       
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.CustomerTransformerIfc#transform(oracle.retail.stores.domain.customer.CustomerIfc)
     */
    public Customer transform(CustomerIfc customer)
    {
        Customer entity = toBaseEntity(customer);

        // set contact information
        if(customer.getContact() != null)
        {           
            entity.addContact(getContactDomainTransformer().transform(customer));            
        }

       //address
       if (!customer.getAddressList().isEmpty())
       {
           for (AddressIfc address : customer.getAddressList())
           {
               entity.addAddress(getAddressDomainTransformer().transform(address));
           }
       }

       //Get the phones
       if (!customer.getPhoneList().isEmpty())
       {
           for (PhoneIfc phone: customer.getPhoneList())
           {
               entity.addPhone(getPhoneDomainTransformer().transform(phone));
           }
       }

       //Get emails

       Iterator<EmailAddressIfc> emailIt = customer.getEmailAddresses();
       while(emailIt.hasNext())
       {
           EmailAddressIfc email = (EmailAddressIfc)emailIt.next();
           if(email.getEmailAddress() != null && !email.getEmailAddress().trim().isEmpty())
           {
               entity.addEmailAddress(getEmailDomainTransformer().transform(email));
           }
       }   
       
     //customer groups
       if (customer.getCustomerGroups()!= null && customer.getCustomerGroups().length >0)
       {

           for( int i = 0; i < customer.getCustomerGroups().length; i++)
           {
               CustomerAffiliation cAffil = getCustomerGroupDomainTransformer().
                           transform(customer.getCustomerGroups()[i], customer.getCustomerID());
  
               entity.addCustomerAffiliation(cAffil);
           }
       }
        return entity;
    }
    
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.CustomerTransformerIfc#updateEntity(oracle.retail.stores.domain.customer.CustomerIfc, oracle.retail.stores.storeservices.entities.customer.Customer)
     */
    public void updateEntity(CustomerIfc customer, Customer existingCustomer)
    {

        existingCustomer.setCustomerStatusCode(customer.getStatus());
        
        //set employeeId
        existingCustomer.setEmployeeID(customer.getEmployeeID());

        //set full name
        existingCustomer.setCustomerName(customer.getCustomerName());
          
        //set the status of the customer
        existingCustomer.setCustomerStatusCode(customer.getStatus());
          
        if(customer.isBusinessCustomer())
        {
             BusinessCustomer bsc = null;
             
             if(existingCustomer.getBusinessCustomers().size() > 0)
             {
                 bsc = existingCustomer.getBusinessCustomers().get(0);
             }
             else
             {
                 bsc = new BusinessCustomer();
                 existingCustomer.addBusinessCustomer(bsc);
             }
             bsc.setOrganizationName(customer.getLastName());
             if(customer.getTaxExemptionReason() != null)
             {
                 bsc.setExemptionReason(customer.getTaxExemptionReason().getCode());
             }    
             
             if(customer.getEncipheredTaxCertificate() != null)
             {
                 bsc.setEncryptedTaxExemptionCertificate(customer.getEncipheredTaxCertificate().getEncryptedNumber());
                 bsc.setMaskedTaxExemptionCertificate(customer.getEncipheredTaxCertificate().getMaskedNumber());
             }
        }
         
        if(customer.getPreferredLocale() != null)
        {
             existingCustomer.setCustomerPreferredLocale(customer.getPreferredLocale().toString());
        }
         
        if(customer.getEncipheredTaxID() != null)
        {
             existingCustomer.setEncryptedCustomerTaxID(customer.getEncipheredTaxID().getEncryptedNumber());
             existingCustomer.setMaskedCustomerTaxID(customer.getEncipheredTaxID().getMaskedNumber());
        }

        //pricing group
        if(customer.getPricingGroupID() != null)
        {
            existingCustomer.setPricingGroupID(customer.getPricingGroupID());
        }
        
        //If any updates for a customer, will be sent to the CO due to the reset of the batch id.
        existingCustomer.setCustomerBatchID(-1);
        
        // update contact information
        if(customer.getContact() != null)
        {   
            ContactIfc contact = customer.getContact();
            Contact entity = existingCustomer.getContacts().get(0);
            
            entity.setFirstName(contact.getFirstName());
            
            entity.setMiddleName(contact.getMiddleName());
            entity.setSalutation(contact.getNamePrefix());
            entity.setSuffix(contact.getNameSuffix());
            
            entity.setFullName(contact.getFullName());
            entity.setLastName(customer.getLastName());
            
            entity.setBirthDate(contact.getBirthDateAsString());
            if(customer.isBusinessCustomer())
            {
                entity.setCompanyName(customer.getLastName());
            }
            if(!customer.isBusinessCustomer())
            {
                entity.setGender(customer.getGenderCode());
            }
            entity.setAllowEmail(customer.getEMailPrivacy());
            entity.setAllowMail(customer.getMailPrivacy());
            entity.setAllowTelephone(customer.getTelephonePrivacy());
            entity.setReceiptPreference(customer.getReceiptPreference());
        }

       //update the address
       if (!customer.getAddressList().isEmpty())
       {
           for (AddressIfc address : customer.getAddressList())
           {
               boolean addressFound = false;
               
               ArrayList<Address> addresses = existingCustomer.getAddresses();
               for (Address addressEntity : addresses) 
               {
                   if(addressEntity.getAddressKey().getAddressTypeCode() == address.getAddressType())
                   {
                       addressEntity.setAddressLine1(address.getLine1());
                       addressEntity.setAddressLine2(address.getLine2());
                       addressEntity.setAddressLine3(address.getLine3());
                       addressEntity.setCity(address.getCity());
                       addressEntity.setCountry(address.getCountry());
                       addressEntity.setPostalCode(address.getPostalCode());
                       addressEntity.setState(address.getState());
                       
                       addressFound = true;
                   }
               }
               
               if(!addressFound)
               {
                   Address addressEntity = new Address();
                   
                   AddressID id = new AddressID();
                   id.setAddressID(address.getAddressID());
                   id.setAddressTypeCode(address.getAddressType());
                   id.setPartyID(existingCustomer.getPartyID());

                   addressEntity.setAddressKey(id);
                   addressEntity.setAddressLine1(address.getLine1());
                   addressEntity.setAddressLine2(address.getLine2());
                   addressEntity.setAddressLine3(address.getLine3());
                   addressEntity.setCity(address.getCity());
                   addressEntity.setCountry(address.getCountry());
                   addressEntity.setPostalCode(address.getPostalCode());
                   addressEntity.setState(address.getState());
                   
                   existingCustomer.addAddress(addressEntity);
               }
           }
       }

       //update the phones
       if (!customer.getPhoneList().isEmpty())
       {
           for (PhoneIfc phone: customer.getPhoneList())
           {
               boolean phoneFound = false;
               
               ArrayList<Phone> phones =  existingCustomer.getPhones();
               for (Phone phoneEntity : phones) 
               {
                   if(phoneEntity.getPhoneKey().getPhoneTypeCode() == phone.getPhoneType())
                   {
                        phoneEntity.setPhoneNumber(phone.getPhoneNumber());
                        phoneEntity.setExtension(phone.getExtension());
                        phoneEntity.setCountry(phone.getCountry());
                        if(phone.isActive())
                        {
                        phoneEntity.setStatusCode(PhoneConstantsIfc.STATUS_ACTIVE);
                        }
                        else
                        {
                            phoneEntity.setStatusCode(PhoneConstantsIfc.STATUS_INACTIVE);
                        }

                        phoneFound = true;
                   }
               }
               if(!phoneFound)
               {
                     Phone phoneEntity = new Phone();

                     PhoneID phoneID = new PhoneID();
                     phoneID.setPhoneTypeCode(phone.getPhoneType());
                     phoneID.setPartyID(existingCustomer.getPartyID());
                     phoneID.setPhoneID(phone.getPhoneType());

                     phoneEntity.setPhoneKey(phoneID);
                     phoneEntity.setPhoneNumber(phone.getPhoneNumber());
                     phoneEntity.setExtension(phone.getExtension());
                     phoneEntity.setCountry(phone.getCountry());
                     phoneEntity.setStatusCode(PhoneConstantsIfc.STATUS_ACTIVE);
                     existingCustomer.addPhone(phoneEntity);
               }
           }
       }       
              
       //update the emails
       Iterator<EmailAddressIfc> emailIt = customer.getEmailAddresses();
       if (emailIt.hasNext())
       {
           EmailAddressIfc emailDomain = (EmailAddressIfc)emailIt.next();
           ArrayList<EmailAddress> eaddresses = existingCustomer.getEmailAddresses();
           
           boolean emailFound = false;
           
           for (EmailAddress emailAddressEntity : eaddresses) 
           {
               if(emailAddressEntity.getEmailAddressID().getEmailAddressTypeCode() == emailDomain.getEmailAddressType())
               {
                   emailAddressEntity.setEmailAddress(emailDomain.getEmailAddress());
                     
                   emailFound =  true;
               }
           }
           
           if(!emailFound)
           {
             EmailAddress emailEntity = new EmailAddress();
               
             EmailAddressID emailAddressID = new EmailAddressID();
             emailAddressID.setEmailAddressTypeCode(emailDomain.getEmailAddressType());
             emailAddressID.setPartyID(existingCustomer.getPartyID());
             emailEntity.setEmailAddressID(emailAddressID);
             
             emailEntity.setEmailAddress(emailDomain.getEmailAddress());
             existingCustomer.addEmailAddress(emailEntity);
           }
       }  
       else
       {
           existingCustomer.setEmailAddresses(new ArrayList<EmailAddress>());
       }
       
       //update customer groups
       if (customer.getCustomerGroups()!= null && customer.getCustomerGroups().length >0)
       {

           for( int i = 0; i < customer.getCustomerGroups().length; i++)
           {
               boolean groupFound = false;
               
               CustomerGroupIfc CustomerGroupDomain = customer.getCustomerGroups()[i];
               
               ArrayList<CustomerAffiliation> affiliations = existingCustomer.getCustomerAffiliations();
               for (CustomerAffiliation customerAffiliationEntity : affiliations) 
               {
                   if(customerAffiliationEntity.getCustomerGroupID() == Integer.parseInt(CustomerGroupDomain.getGroupID()))
                   {
                       groupFound = true;
                   }
               }
               
               if(!groupFound)
               {
                    CustomerAffiliation cAffilEntity = new CustomerAffiliation();
                    cAffilEntity.setCustomerGroupID(Integer.parseInt(CustomerGroupDomain.getGroupID()));
                    cAffilEntity.setCustomerID(customer.getCustomerID());
                    existingCustomer.addCustomerAffiliation(cAffilEntity);
               }
           }
       }
       // Remove if any affiliations linked by empty list
       else
       {
           existingCustomer.setCustomerAffiliations(new ArrayList<CustomerAffiliation>());   
       }
    }    

    /**
     * Utility method returns java.util.Locale from Locale Description
     * @param value String Locale Description
     * @return Locale entity
     */
    protected Locale getLocale(String value)
    {
        Locale locale = LocaleUtilities.getLocaleFromString(value);
        
        return locale;
    }
}
