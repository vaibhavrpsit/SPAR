/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/jpa/JpaReadPriceDerRuleEligibilitiesByGroupID.java /main/9 2014/07/07 10:43:50 cgreene Exp $
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
 *    abondala  08/13/12 - jpa read discount rules by group id
 * 
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts.jpa;

import java.io.Serializable;
import java.util.List;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.transform.entity.customer.CustomerGroupTransformerIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JpaDataOperation;
import oracle.retail.stores.storeservices.entities.customer.CustomerAffiliationPriceDerRuleEligibility;
import oracle.retail.stores.storeservices.services.common.ServiceException;
import oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc;
import oracle.retail.stores.storeservices.services.customer.CustomerServiceIfc;
import oracle.retail.stores.transform.TransformerIfc;


/**
 * This operation reads all the customer groups associated with a customer.
 *
 * @since 14.0
 */
public class JpaReadPriceDerRuleEligibilitiesByGroupID extends JpaDataOperation
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

    /**
     * Read discount rules associated with the group id
     *
     * @param dataTransaction
     * @param dataConnection
     * @param action
     * @exception DataException
     */
    @Override
    protected Serializable execute(StoresDataServiceIfc service, Serializable dataObject) throws ServiceException,
            DataException
    {
        CustomerSearchCriteriaIfc criteria = (CustomerSearchCriteriaIfc)dataObject;
        CustomerServiceIfc customerService = (CustomerServiceIfc)service;
        DiscountRuleIfc[] discountRulesArray = null;

        List<CustomerAffiliationPriceDerRuleEligibility> discountRuleEligibilities = customerService
                .findPriceDerRuleEligibilitiesByGroupID(criteria.getGroupID());

        discountRulesArray = getCustomerGroupTransformer().transform(discountRuleEligibilities,
                criteria.getLocaleRequestor());
        return discountRulesArray;
    }
}