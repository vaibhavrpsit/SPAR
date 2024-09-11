/*
 * ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/jpa/
 * JpaReadPricingGroup.java /main/7 2013/12/13 14:24:24 jswan Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.> MODIFIED (MM/DD/YY) jswan
 * 12/13/13 - Upated JAVADOC. cgreene  07/01/14 - refactor throwing and
 * 12/13/13 - Upated JAVADOC.                     catching exceptions so that
 * 12/13/13 - Upated JAVADOC.                     SQLExceptions get mapped
 * 12/13/13 - Upated JAVADOC. abondala 01/27/13 - extending JPA abondala
 * 01/10/13 - support extending jpa abondala 01/03/13 - refactored transformers
 * abondala 08/21/12 - jpa for pricing group abondala 08/21/12 - jpa read
 * customer pricing groups
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.customer.PricingGroupIfc;
import oracle.retail.stores.domain.transform.entity.customer.PricingGroupTransformerIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JpaDataOperation;
import oracle.retail.stores.storeservices.entities.customer.PricingGroup;
import oracle.retail.stores.storeservices.services.common.ServiceException;
import oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc;
import oracle.retail.stores.storeservices.services.customer.CustomerServiceIfc;
import oracle.retail.stores.transform.TransformerIfc;

/**
 * This operation reads all the pricing groups.
 * 
 * @since 14.0
 */
public class JpaReadPricingGroup extends JpaDataOperation
{
    /** serialVersionUID */
    private static final long serialVersionUID = -826579619147724862L;

    /**
     * Customer pricing Service transformer
     */
    protected PricingGroupTransformerIfc pricingGroupTransformer;

    /**
     * Gets the utility for transforming between the entity and domain objects.
     * The implementation for the {@link PricingGroupTransformerIfc} is defined
     * in the TransformerContext.xml file; bean ID is
     * 'transformer_PricingGroupDomainTransformer'.
     * 
     * @return CustomerEntityTransformerService
     */
    protected PricingGroupTransformerIfc getPricingGroupTransformer()
    {

        if (pricingGroupTransformer == null)
        {
            pricingGroupTransformer = (PricingGroupTransformerIfc)BeanLocator
                    .getTransformerBean(TransformerIfc.TRANSF_CUSTOMER_PRICING_DOM_TRANSFORMER);
        }
        return pricingGroupTransformer;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.data.JpaDataOperation#execute(oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc, java.io.Serializable)
     */
    @Override
    protected Serializable execute(StoresDataServiceIfc service, Serializable dataObject) throws ServiceException,
            DataException
    {
        LocaleRequestor localeReq = null;
        if (dataObject instanceof LocaleRequestor)
        {
            localeReq = (LocaleRequestor)dataObject;
        }

        CustomerServiceIfc customerService = (CustomerServiceIfc)service;

        ArrayList<PricingGroupIfc> groups = new ArrayList<PricingGroupIfc>();

        List<PricingGroup> pricingGroups = customerService.findAllPricingGroups();

        if (pricingGroups.size() == 0)
        {
            String msg = "JpaReadPricingGroup: pricing groups not found.";
            throw new DataException(DataException.NO_DATA, msg);
        }

        for (PricingGroup pricingGroup : pricingGroups)
        {
            PricingGroupIfc group = getPricingGroupTransformer().transform(pricingGroup, localeReq);
            groups.add(group);
        }

        return groups;
    }
}
