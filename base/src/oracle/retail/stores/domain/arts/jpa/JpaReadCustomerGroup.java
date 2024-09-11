/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/jpa/JpaReadCustomerGroup.java /main/9 2014/07/07 10:43:49 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/01/14 - refactor throwing and catching exceptions so that
 *                         SQLExceptions get mapped
 *    jswan     12/13/13 - Upated JAVADOC.
 *    abondala  01/27/13 - extending JPA
 *    abondala  01/10/13 - support extending jpa
 *    abondala  01/03/13 - refactored transformers
 *    abondala  08/13/12 - jpa read customer group
 * 
 * 
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts.jpa;


import java.io.Serializable;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.transform.entity.customer.CustomerGroupTransformerIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JpaDataOperation;
import oracle.retail.stores.storeservices.entities.customer.CustomerGroup;
import oracle.retail.stores.storeservices.services.common.ServiceException;
import oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc;
import oracle.retail.stores.storeservices.services.customer.CustomerServiceIfc;
import oracle.retail.stores.transform.TransformerIfc;

/**
 * This operation read the customer group by group id
 * @since 14.0
 */
public class JpaReadCustomerGroup extends JpaDataOperation
{

    /** serialVersionUID */
    private static final long serialVersionUID = 7671143649908443711L;

    /**
     * Customer Service transformer
     */
    protected CustomerGroupTransformerIfc customerGroupTransformer;

    /**
     * Gets the utility for transforming between the entity and domain objects.
     * The implementation for the {@link CustomerGroupTransformerIfc} is defined
     * in the TransformerContext.xml file; bean ID is
     * 'transformer_CustomerGroupDomainTransformer'.
     * 
     * @return CustomerEntityTransformerService
     */
    protected CustomerGroupTransformerIfc getCustomerGroupTransformer()
    {
        if (customerGroupTransformer == null)
        {
            customerGroupTransformer = (CustomerGroupTransformerIfc)BeanLocator.getTransformerBean(TransformerIfc.TRANSF_CUSTOMER_GROUP_DOM_TRANSFORMER);
        }
        return customerGroupTransformer;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.data.JpaDataOperation#execute(oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc, java.io.Serializable)
     */
    @Override
    protected Serializable execute(StoresDataServiceIfc service, Serializable dataObject) throws ServiceException, DataException
    {
        CustomerSearchCriteriaIfc criteria = (CustomerSearchCriteriaIfc)dataObject;
        CustomerServiceIfc customerService = (CustomerServiceIfc)service;
        CustomerGroupIfc group = null;

        CustomerGroup customerGroup = customerService.findCustomerGroupByID(criteria.getGroupID());
        if (customerGroup != null)
        {
            group = getCustomerGroupTransformer().transform(customerGroup, criteria.getLocaleRequestor());
            return group;
        }
        return null;
    }

}
