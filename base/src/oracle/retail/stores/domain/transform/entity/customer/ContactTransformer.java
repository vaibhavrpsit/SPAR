/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transform/entity/customer/ContactTransformer.java /main/19 2013/12/11 11:24:09 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.transform.entity.customer;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.utility.ContactIfc;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.storeservices.entities.customer.Contact;
import oracle.retail.stores.storeservices.entities.customer.ContactID;
import oracle.retail.stores.storeservices.entities.customer.Phone;
import oracle.retail.stores.transform.TransformerIfc;

import org.apache.log4j.Logger;

/**
 * The ContactTransformer is a utility class for converting between the set of 
 * Contact Domain objects oracle.retail.stores.domain.utility.ContactIfc and
 * Contact Entity objects(JPA) {@link oracle.retail.stores.storeservices.entities.customer.Contact}.
 * <p>
 * Additionally the Entity {@link oracle.retail.stores.storeservices.entities.customer.Contact} is also extracted from 
 * domain objects oracle.retail.stores.domain.customer.CustomerIfc, oracle.retail.stores.domain.order.OrderDeliveryDetailIfc
 * and oracle.retail.stores.domain.lineitem.OrderItemStatusIfc.
 * <p>
 * The ContactTransformer is a spring loaded bean defined in TransformerContext.xml
 * @since 14.0
 */
public class ContactTransformer implements ContactTransformerIfc, TransformerIfc
{
    /**
     * Static Logger for this class
     */
    protected static Logger logger = Logger.getLogger(ContactTransformer.class);

    /**
     * The PhoneTransformerIfc is a utility class for transformation of objects between 
     * the domain objects oracle.retail.stores.domain.utility.PhoneIfc and oracle.retail.stores.domain.utility.AddressIfc
     * to JPA entity {@link oracle.retail.stores.storeservices.entities.customer.Phone}. 
     * This is used to transform the Contacts Phone information. 
     */
    protected PhoneTransformerIfc phoneDomainTransformer;

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.ContactTransformerIfc#transform
     *                          (oracle.retail.stores.storeservices.entities.customer.Contact)
     */
    public ContactIfc transform(Contact contactEntity)
    {
        ContactIfc contact = DomainGateway.getFactory().getContactInstance();

        contact.setFirstName(contactEntity.getFirstName());
        contact.setLastName(contactEntity.getLastName());
        contact.setMiddleName(contactEntity.getMiddleName());
        contact.setNamePrefix(contactEntity.getSalutation());
        contact.setNameSuffix(contactEntity.getSuffix());
        contact.setCompanyName(contactEntity.getCompanyName());

        contact.setFullName(contactEntity.getFullName());
        if(contactEntity.getBirthDate() != null)
        {
          contact.parseBirthDate(contactEntity.getBirthDate());
        }
        if(contactEntity.getContactKey() != null)
        {
            contact.setRecordID(contactEntity.getContactKey().getPartyID());
            contact.setContactID(contactEntity.getContactKey().getContactID());
        }

        if (contactEntity.getPhones() != null && contactEntity.getPhones().size()>=1)
        {
        	List<PhoneIfc> phoneList = new ArrayList<PhoneIfc>();
        	for (Phone phoneEntity : contactEntity.getPhones())
        	{
        	    if(phoneEntity.getStatusCode()==PhoneConstantsIfc.STATUS_ACTIVE)
                {
        		PhoneIfc phone = getPhoneDomainTransformer().transform(phoneEntity);
        		phoneList.add(phone);
                }
        	}
        	contact.setPhoneList(phoneList);
        }
        
        return contact;
    }


    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.ContactTransformerIfc#transformToPersonName
     *                                          (oracle.retail.stores.storeservices.entities.customer.Contact)
     */
    public PersonNameIfc transformToPersonName(Contact contactEntity)
    {
        PersonNameIfc personName = DomainGateway.getFactory().getPersonNameInstance();
        personName.setFirstName(contactEntity.getFirstName());
        personName.setLastName(contactEntity.getLastName());
        personName.setMiddleName(contactEntity.getMiddleName());
        personName.setSalutation(contactEntity.getSalutation());
        personName.setNameSuffix(contactEntity.getSuffix());
        personName.setFullName(contactEntity.getFullName());

        return personName;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.ContactTransformerIfc#transform(oracle.retail.stores.domain.utility.ContactIfc)
     */
    public Contact transform(ContactIfc contact)
    {
        Contact entity = new Contact();

        ContactID id = new ContactID();
        id.setContactID(contact.getContactID());
        id.setPartyID(contact.getRecordID());
        entity.setContactKey(id);

        entity.setFirstName(contact.getFirstName());
        entity.setLastName(contact.getLastName());
        entity.setMiddleName(contact.getMiddleName());
        entity.setSalutation(contact.getNamePrefix());
        entity.setSuffix(contact.getNameSuffix());

        entity.setFullName(contact.getFullName());

        entity.setBirthDate(contact.getBirthDateAsString());

        return entity;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.ContactTransformerIfc#transform(oracle.retail.stores.domain.utility.PersonNameIfc)
     */
    public Contact transform(PersonNameIfc personName)
    {
        Contact contactEntity = new Contact();
        contactEntity.setFirstName(personName.getFirstName());
        contactEntity.setLastName(personName.getLastName());
        contactEntity.setMiddleName(personName.getMiddleName());
        contactEntity.setSalutation(personName.getSalutation());
        contactEntity.setSuffix(personName.getNameSuffix());
        contactEntity.setFullName(personName.getFullName());

        return contactEntity;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.ContactTransformerIfc#transform(oracle.retail.stores.domain.customer.CustomerIfc)
     */
    public Contact transform(CustomerIfc customer)
    {
        Contact entity = transform(customer.getContact());
        entity.setAllowEmail(customer.getEMailPrivacy());
        entity.setAllowMail(customer.getMailPrivacy());
        entity.setAllowTelephone(customer.getTelephonePrivacy());
        entity.setReceiptPreference(customer.getReceiptPreference());

        if(customer.isBusinessCustomer())
        {
        	entity.setLastName(customer.getLastName());
        	entity.setCompanyName(customer.getLastName());
        }

        if(!customer.isBusinessCustomer())
        {
            entity.setGender(customer.getGenderCode());
        }

        return entity;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.ContactTransformerIfc#transform(oracle.retail.stores.domain.order.OrderDeliveryDetailIfc)
     */
    public Contact transform(OrderDeliveryDetailIfc deliveryDetail)
    {
        Contact entity = new Contact();
        entity.setFirstName(deliveryDetail.getFirstName());
        entity.setLastName(deliveryDetail.getLastName());
        return entity;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.ContactTransformerIfc#transform(oracle.retail.stores.domain.lineitem.OrderItemStatusIfc)
     */
    public Contact transform(OrderItemStatusIfc orderItemStatus)
    {
        Contact entity = null;
        if (orderItemStatus.getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY)
        {
            entity = transform(orderItemStatus.getDeliveryDetails());
        }
        else if (orderItemStatus.getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP)
        {
            entity = new Contact();
            entity.setFirstName(orderItemStatus.getPickupFirstName());
            entity.setLastName(orderItemStatus.getPickupLastName());
        }
        return entity;
    }

    /**
     * Returns the PhoneTransformerIfc.
     * @return phoneDomainTransformer
     */
    public PhoneTransformerIfc getPhoneDomainTransformer()
    {
        return phoneDomainTransformer;
    }

    /**
     * Set the PhoneTransformerIfc. In the default product implementation it is spring loaded as defined in TransformerContext.xml
     * @param phoneDomainTransformer the phoneDomainTransformer to set
     */
    public void setPhoneDomainTransformer(PhoneTransformerIfc phoneDomainTransformer)
    {
        this.phoneDomainTransformer = phoneDomainTransformer;
    }
}
