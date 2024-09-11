/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/jpa/JpaSaveCustomer.java /main/15 2014/07/07 10:43:50 cgreene Exp $
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
 *    abondala  08/17/12 - fixing the exisitng customer updates and few other
 *                         issues
 *    abondala  08/16/12 - customer retrieved from CO may be treated as new
 *                         customer and tries to add a new custome all the time
 *                         and will lead to integrity exceptions. Look for a
 *                         customer based on customer id and if exist update
 *                         otherwise add a new customer.
 *    acadar    08/05/12 - XC refactoring
 *    acadar    08/05/12 - refactoring
 *    acadar    08/03/12 - refactoring
 *    acadar    08/03/12 - moved customer search criteria
 *    acadar    08/02/12 - updates
 *    acadar    08/02/12 - code review updates
 *    acadar    08/02/12 - new Jpaoperation
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts.jpa;

import java.io.Serializable;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.domain.arts.ARTSCustomer;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transform.entity.customer.CustomerTransformerIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JpaDataOperation;
import oracle.retail.stores.storeservices.entities.customer.Customer;
import oracle.retail.stores.storeservices.services.common.ServiceException;
import oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc;
import oracle.retail.stores.storeservices.services.customer.CustomerServiceIfc;
import oracle.retail.stores.transform.TransformerIfc;


/**
 * This operation takes a POS domain Customer and saves the information in the
 * database through JPA.
 * @since 14.0
 */
public class JpaSaveCustomer extends JpaDataOperation
{
    private static final long serialVersionUID = -2150333877590045472L;

    protected CustomerTransformerIfc customerTransfomer;

    /**
     * Gets the utility for transforming between the entity and domain objects.
     * The implementation for the {@link CustomerTransformerIfc} is defined in
     * the TransformerContext.xml file; bean ID is
     * 'transformer_CustomerDomainTransformer'.
     * 
     * @return CustomerEntityTransformerService
     */
    protected CustomerTransformerIfc getCustomerTransformer()
    {
        if (customerTransfomer == null)
        {
            customerTransfomer = (CustomerTransformerIfc)BeanLocator.getTransformerBean(TransformerIfc.TRANSF_CUSTOMER_DOM_TRANSFORMER);
        }
        return customerTransfomer;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.data.JpaDataOperation#execute(oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc, java.io.Serializable)
     */
    @Override
    protected Serializable execute(StoresDataServiceIfc service, Serializable dataObject) throws ServiceException, DataException
    {
        ARTSCustomer artsCustomer = (ARTSCustomer)dataObject;
        CustomerIfc customer = artsCustomer.getPosCustomer();
        CustomerServiceIfc customerService = (CustomerServiceIfc)service;

        Customer existingCustomer = customerService.findCustomerByID(customer.getCustomerID());

        // new customer thats not persisted
        if (existingCustomer == null)
        {
            Customer customerEntity = getCustomerTransformer().transform(customer);
            customerService.createCustomer(customerEntity);
        }
        else
        {
            getCustomerTransformer().updateEntity(customer, existingCustomer);
            customerService.updateCustomer(existingCustomer);
        }

        return null;
    }
}