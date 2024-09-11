/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transform/entity/customer/EmailTransformer.java /main/8 2013/12/11 11:24:09 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  12/09/13 - Added Javadocs
 *    abondala  01/27/13 - extending JPA
 *    abondala  01/10/13 - support extending jpa
 *    abondala  01/03/13 - refactored transformers
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

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EmailAddressIfc;
import oracle.retail.stores.storeservices.entities.customer.EmailAddress;
import oracle.retail.stores.storeservices.entities.customer.EmailAddressID;
import oracle.retail.stores.transform.TransformerIfc;


/**
 * This EmailTransformer is a utility class used for converting/transforming between
 * Domain objects oracle.retail.stores.domain.utility.EmailAddressIfc and
 * JPA Entity {@link oracle.retail.stores.storeservices.entities.customer.EmailAddress} objects.
 * <p>
 * The EmailTransformer is a spring loaded bean defined in TransformerContext.xml

 * @since 14.0
 */
public class EmailTransformer implements EmailTransformerIfc, TransformerIfc
{

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.EmailTransformerIfc#transform
     *                                      (oracle.retail.stores.storeservices.entities.customer.EmailAddress)
     */
    public EmailAddressIfc  transform(EmailAddress emailEntity)
    {
         oracle.retail.stores.domain.utility.EmailAddressIfc  email = 
                         DomainGateway.getFactory().getEmailAddressInstance();
         email.setEmailAddress(emailEntity.getEmailAddress());
         email.setEmailAddressType(emailEntity.getEmailAddressID().getEmailAddressTypeCode());
         email.setRecordID(emailEntity.getEmailAddressID().getPartyID());
  
         return email;
    }
    

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.EmailTransformerIfc#transform(oracle.retail.stores.domain.utility.EmailAddressIfc)
     */
    public EmailAddress  transform(EmailAddressIfc email)
    {
         EmailAddress emailEntity = new EmailAddress();
         emailEntity.setEmailAddress(email.getEmailAddress());
         
         EmailAddressID emailAddressID = new EmailAddressID();
         emailAddressID.setEmailAddressTypeCode(email.getEmailAddressType());
         emailAddressID.setPartyID(email.getRecordID());
         emailEntity.setEmailAddressID(emailAddressID);
        
         return emailEntity;
    }



}
