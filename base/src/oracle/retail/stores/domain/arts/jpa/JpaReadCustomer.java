/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/jpa/JpaReadCustomer.java /main/14 2014/07/07 10:43:49 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/01/14 - refactor throwing and catching exceptions so that
 *                         SQLExceptions get mapped
 *    jswan     12/13/13 - Upated JAVADOC.
 *    abhineek  05/21/13 - fix to include both individual and business
 *                         customers during phone number based search in MPOS
 *    abondala  01/27/13 - extending JPA
 *    abondala  01/10/13 - support extending jpa
 *    abondala  01/03/13 - refactored transformers
 *    abondala  12/13/12 - customer search criteria fields are all optional.
 *    acadar    08/05/12 - refactoring
 *    acadar    08/03/12 - refactoring
 *    acadar    08/03/12 - moved customer search criteria
 *    acadar    08/02/12 - return array list
 *    acadar    08/02/12 - code review updates
 *    acadar    08/02/12 - new JPA Operation
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts.jpa;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.utility.ResultList;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transform.entity.customer.CustomerTransformerIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JpaDataOperation;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.storeservices.entities.customer.Customer;
import oracle.retail.stores.storeservices.services.common.ServiceException;
import oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc;
import oracle.retail.stores.storeservices.services.customer.CustomerServiceIfc;
import oracle.retail.stores.transform.TransformerIfc;


/**
 * This operation takes a POS domain Customer and creates a new entry in the
 * database.
 * @since 14.0
 */
public class JpaReadCustomer extends JpaDataOperation
{
    private static final long serialVersionUID = -2150333877590045472L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JpaReadCustomer.class);

   /**
    * Customer Service transformer
    */
   protected CustomerTransformerIfc customerTransformer;


   /**
    * Gets the utility for transforming between the entity and domain objects.  The implementation for
    * the {@link CustomerTransformerIfc} is defined in the TransformerContext.xml file; bean ID
     * is 'transformer_CustomerDomainTransformer'.
    * @return CustomerEntityTransformerService
    */
   protected CustomerTransformerIfc getCustomerTransformer()
   {

       if(customerTransformer == null)
       {
           customerTransformer = (CustomerTransformerIfc)BeanLocator.getTransformerBean(TransformerIfc.TRANSF_CUSTOMER_DOM_TRANSFORMER);
       }
       return customerTransformer;
   }

    /**
     * Execute the SQL statements against the database.
     *
     * @param dataTransaction
     * @param dataConnection
     * @param action
     * @exception DataException
     */
    @Override
    protected Serializable execute(StoresDataServiceIfc service, Serializable dataObject) throws ServiceException, DataException
    {
        CustomerSearchCriteriaIfc searchCriteria = (CustomerSearchCriteriaIfc)dataObject;
        CustomerServiceIfc customerService = (CustomerServiceIfc)service;

        switch (searchCriteria.getSearchType())
        {

            case SEARCH_BY_TAX_ID:
                try
                {
                    EncipheredDataIfc taxData = FoundationObjectFactory.getFactory().createEncipheredDataInstance(searchCriteria.getTaxID().getBytes());
                    searchCriteria.setTaxID(taxData.getMaskedNumber());
                }
                catch(EncryptionServiceException ese)
                {
                    logger.warn("could not encrypt tax ID", ese);
                }

            case SEARCH_BY_PHONE_NUMBER:
            case SEARCH_BY_CUSTOMER_ID:
            case SEARCH_BY_EMPLOYEE_ID:
            case SEARCH_BY_BUSINESS_INFO:
            case SEARCH_BY_CUSTOMER_INFO:

                return selectCustomers(searchCriteria, customerService);
            default:
                //throw exception for unsupported search type
                throw new DataException(DataException.INVALID_TYPE, "Requested search criteria type is not supported=" + searchCriteria.getSearchType());
        }
    }

    /**
     *
     */
    public ResultList selectCustomers(CustomerSearchCriteriaIfc searchCriteria, CustomerServiceIfc customerService) throws ServiceException, DataException
    {
        //lookup customer related entities
        CustomerIfc customer = null;
        ResultList resultList = customerService.findCustomerBySearchCriteria(searchCriteria);

        @SuppressWarnings("unchecked")
        List<Customer> customerEntities = resultList.getList();

        if (customerEntities == null || customerEntities.size() == 0)
        {
            //throw exception for no data
            throw new DataException(DataException.NO_DATA, "Customer Not found for search criteria=" + searchCriteria.toString());
        }
        else
        {

            ArrayList<CustomerIfc>  customerList = new ArrayList<CustomerIfc>(customerEntities.size());

            for (Customer entity : customerEntities)
            {
                customer= getCustomerTransformer().transform(entity, searchCriteria.getLocaleRequestor());
                customerList.add(customer);
            }

            resultList.setList(customerList);

            return resultList;
        }
    }

}
