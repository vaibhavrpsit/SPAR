/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transform/entity/customer/PricingGroupTransformer.java /main/7 2013/12/11 11:24:09 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  12/09/13 - Added Javadocs
 *    abondala  01/27/13 - extending JPA
 *    abondala  01/10/13 - support extending jpa
 *    abondala  01/03/13 - refactored transformers
 *    abondala  08/21/12 - jpa for pricing group
 *    abondala  08/21/12 - pricing group transformer
 * 
 * 
 * ===========================================================================
 */
package oracle.retail.stores.domain.transform.entity.customer;


import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.PricingGroupIfc;
import oracle.retail.stores.storeservices.entities.customer.PricingGroup;
import oracle.retail.stores.transform.TransformerIfc;

import org.apache.log4j.Logger;


/**
 * The PricingGroupTransformer is a utility class for converting between oracle.retail.stores.domain.customer.PricingGroupIfc 
 * Domain objects and {@link oracle.retail.stores.storeservices.entities.customer.PricingGroup} JPA Entity objects.
 * <p>
 * The CustomerTransformer is a spring loaded bean defined in TransformerContext.xml
 * @since 14.0
 */
public class PricingGroupTransformer implements PricingGroupTransformerIfc, TransformerIfc
{

    /** Static Logger for this class */
    protected static Logger logger = Logger.getLogger(PricingGroupTransformer.class);

    
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.PricingGroupTransformerIfc#transform
     *               (oracle.retail.stores.storeservices.entities.customer.PricingGroup, oracle.retail.stores.common.utility.LocaleRequestor)
     */
    public PricingGroupIfc transform(PricingGroup pricingGroup, LocaleRequestor localeReq)
    {
       PricingGroupIfc pGroup = DomainGateway.getFactory().getPricingGroupInstance();

       pGroup.setPricingGroupID(pricingGroup.getPricingGroupID());
       pGroup.setLocalizedNames(pricingGroup.getPricingGroupName(localeReq));
       pGroup.setLocalizedDescriptions(pricingGroup.getPricingGroupDescription(localeReq));
       pGroup.setPricingGroupName(pricingGroup.getPricingGroupName());
       pGroup.setPricingGroupDescription(pricingGroup.getPricingGroupDescription());

       return pGroup;
    }    
    

    /**
     * Utility method Returns java.util.Locale from String Locale Description
     * @param value String Locale Description
     * @return Locale entity
     */
    protected Locale getLocale(String value)
    {
        Locale locale = new Locale(value);

        return locale;
    }

}

