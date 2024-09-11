/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transform/entity/customer/AddressTransformer.java /main/12 2013/12/11 11:24:09 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  12/09/13 - Added Javadocs
 *    abondala  01/27/13 - extending JPA
 *    abondala  01/10/13 - support extending jpa
 *    abondala  01/03/13 - transformers refactored
 *    sgu       08/31/12 - convert payment to tender in xc order
 *    acadar    08/16/12 - bug fixes
 *    acadar    08/15/12 - support for multiple XC shipping addresses
 *    sgu       08/08/12 - add nullpointer check
 *    acadar    08/01/12 - changes for JPA
 *    acadar    08/01/12 - new transformer
 * ===========================================================================
 */
package oracle.retail.stores.domain.transform.entity.customer;


import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.AddressBookEntryIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.storeservices.entities.customer.Address;
import oracle.retail.stores.storeservices.entities.customer.AddressID;
import oracle.retail.stores.transform.TransformerIfc;

import org.apache.log4j.Logger;


/**
 * The AddressTransformer is a utility class for converting between 
 * the Domain objects oracle.retail.stores.domain.utility.AddressBookEntryIfc and oracle.retail.stores.domain.utility.AddressIfc
 * and Entity (JPA) {@link oracle.retail.stores.storeservices.entities.customer.Address} objects.
 * The AddressTransformer is a spring loaded bean defined in TransformerContext.xml
 * 
 * @since 14.0
 */
public class AddressTransformer implements AddressTransformerIfc, TransformerIfc
{

    /**
     * Static Logger for this class
     */
    protected static Logger logger = Logger.getLogger(AddressTransformer.class);

    /**
     * The ContactTransformerIfc is a utility class for transformation of objects between 
     * the domain object oracle.retail.stores.domain.utility.ContactIfc
     * to entity {@link oracle.retail.stores.storeservices.entities.customer.Contact}. 
     * This is used to transform the Address's Contact information.
     */
    protected ContactTransformerIfc contactDomainTransformer;

    /**
     * Returns the ContactTransformerIfc
     * @return the contactDomainTransformer
     */
    public ContactTransformerIfc getContactDomainTransformer()
    {
        return contactDomainTransformer;
    }

    /**
     * Set the ContactTransformerIfc. In the default product implementation it is spring loaded as defined in TransformerContext.xml
     * @param contactDomainTransformer the contactDomainTransformer to set
     */
    public void setContactDomainTransformer(ContactTransformerIfc contactDomainTransformer)
    {
        this.contactDomainTransformer = contactDomainTransformer;
    }


    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.AddressTransformerIfc#transform
     *                                  (oracle.retail.stores.storeservices.entities.customer.Address)
     */
    public AddressBookEntryIfc transform(Address entity)
    {
        AddressBookEntryIfc addressBookEntry = DomainGateway.getFactory().getAddressBookEntryInstance();
        AddressIfc address = transformAddress(entity);

        addressBookEntry.setAddress(address);
        addressBookEntry.setAddressAlias(entity.getAddressAlias());
        addressBookEntry.setPrimaryAddress(entity.isPrimaryAddress());

        if(entity.getContact() != null)
        {
            addressBookEntry.setContact(getContactDomainTransformer().transform(entity.getContact()));
        }
        return addressBookEntry;
    }


    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.AddressTransformerIfc#transformAddress
     *                                      (oracle.retail.stores.storeservices.entities.customer.Address)
     */
    public AddressIfc transformAddress(Address entity)
    {
        AddressIfc address = DomainGateway.getFactory().getAddressInstance();

        AddressID addressKey = entity.getAddressKey();
        if (addressKey != null)
        {
            address.setAddressType(addressKey.getAddressTypeCode());
            address.setAddressID(addressKey.getAddressID());
            address.setRecordID(addressKey.getPartyID());
        }
        address.addAddressLine(entity.getAddressLine1());
        address.addAddressLine(entity.getAddressLine2());
        address.addAddressLine(entity.getAddressLine3());
        address.setCity(entity.getCity());
        address.setCountry(entity.getCountry());
        address.setPostalCode(entity.getPostalCode());
        address.setState(entity.getState());

        return address;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.AddressTransformerIfc#
     *                          transform(oracle.retail.stores.domain.utility.AddressIfc)
     */
    public Address transform(AddressIfc address)
    {
        Address entity = new Address();

        AddressID id = new AddressID();
        id.setAddressID(address.getAddressID());
        id.setAddressTypeCode(address.getAddressType());
        id.setPartyID(address.getRecordID());

        entity.setAddressKey(id);

        entity.setAddressLine1(address.getLine1());
        entity.setAddressLine2(address.getLine2());
        entity.setAddressLine3(address.getLine3());
        entity.setCity(address.getCity());
        entity.setCountry(address.getCountry());
        entity.setPostalCode(address.getPostalCode());
        entity.setState(address.getState());

        return entity;
    }

}
