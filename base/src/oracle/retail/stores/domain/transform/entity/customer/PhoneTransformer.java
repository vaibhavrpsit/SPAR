/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transform/entity/customer/PhoneTransformer.java /main/11 2013/12/11 11:24:09 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  12/09/13 - Added Javadocs
 *    rgour     06/07/13 - marking a phone record as deleted if it is removed
 *    abondala  01/27/13 - extending JPA
 *    abondala  01/10/13 - support extending jpa
 *    abondala  01/03/13 - refactored transformers
 *    abondala  08/17/12 - fixing the exisitng customer updates and few other
 *                         issues
 *    sgu       08/03/12 - refactor transformers
 *    sgu       08/03/12 - relocate transformers
 *    acadar    08/01/12 - changes for JPA
 *    acadar    08/01/12 - new transformer
 *    acadar    07/20/12 - fix XC issues
 *    acadar    07/18/12 - updates for XC Customer
 *    acadar    07/02/12 - new package and class for XC
 *    acadar    07/02/12 - new folder
 *    ohorne    05/23/12 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.transform.entity.customer;


import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.storeservices.entities.customer.Phone;
import oracle.retail.stores.storeservices.entities.customer.PhoneID;
import oracle.retail.stores.transform.TransformerIfc;

import org.apache.log4j.Logger;


/**
 * The PhoneTransformer is a utility class for converting between 
 * the Domain objects oracle.retail.stores.domain.utility.PhoneIfc and
 * Entity objects(JPA) {@link oracle.retail.stores.storeservices.entities.customer.Phone}.
 * <p>
 * The PhoneTransformer is a spring loaded bean defined in TransformerContext.xml
 * @since 14.0
 */
public class PhoneTransformer implements PhoneTransformerIfc, TransformerIfc
{
    /**
     * Static Logger for this class
     */
    protected static Logger logger = Logger.getLogger(PhoneTransformer.class);

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.PhoneTransformerIfc#transform
     *                                  (oracle.retail.stores.storeservices.entities.customer.Phone)
     */
    public PhoneIfc transform(Phone phoneEntity)
    {
        PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();

        phone.setPhoneNumber(phoneEntity.getPhoneNumber());
        phone.setExtension(phoneEntity.getExtension());

        phone.setCountry(phoneEntity.getCountry());
        phone.setPhoneType(phoneEntity.getPhoneKey().getPhoneTypeCode());
        phone.setRecordID(phoneEntity.getPhoneKey().getPartyID());
        phone.setPhoneID(phoneEntity.getPhoneKey().getPhoneID());       
        phone.setStatusCode(phoneEntity.getStatusCode());
        return phone;
    }
    
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.PhoneTransformerIfc#transform(oracle.retail.stores.domain.utility.PhoneIfc)
     */
    public Phone transform(PhoneIfc phone)
    {
         Phone phoneEntity = new Phone();

         phoneEntity.setPhoneNumber(phone.getPhoneNumber());
         phoneEntity.setExtension(phone.getExtension());
         phoneEntity.setCountry(phone.getCountry());

         PhoneID phoneID = new PhoneID();
         phoneID.setPhoneTypeCode(phone.getPhoneType());
         phoneID.setPartyID(phone.getRecordID());
         phoneID.setPhoneID(phone.getPhoneType());
        if (phone.isActive())
        {
            phoneEntity.setStatusCode(PhoneConstantsIfc.STATUS_ACTIVE);
        }
        else
        {
            phoneEntity.setStatusCode(PhoneConstantsIfc.STATUS_INACTIVE);
        }

         phoneEntity.setPhoneKey(phoneID);
         return phoneEntity;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.PhoneTransformerIfc#transform(java.util.List)
     */
    public List<Phone> transform(List<PhoneIfc> phones)
    {
        List<Phone> entities = new ArrayList<Phone>();
        if (phones != null)
        {
            for (PhoneIfc phone : phones)
            {
                Phone phoneEntity = transform(phone);
                entities.add(phoneEntity);
            }
        }
        return entities;
    }

}
