/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/jpa/JpaUpdateCustomerBatchIDs.java /main/4 2014/07/07 10:43:50 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/01/14 - refactor throwing and catching exceptions so that
 *                         SQLExceptions get mapped
 *    jswan     12/13/13 - Upated JAVADOC.
 *    abondala  08/10/12 - added couple more jpa operation classes for missing
 *                         operations
 *    abondala  08/10/12 - jpa class to update customer batch id's in the
 *                         customer table
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts.jpa;

import java.io.Serializable;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.manager.datareplication.DataReplicationCustomerEntryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JpaDataOperation;
import oracle.retail.stores.storeservices.services.common.ServiceException;
import oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc;
import oracle.retail.stores.storeservices.services.customer.CustomerServiceIfc;

/**
 * This operation updates the t-log batch ID columns in the customer table.
 * 
 * @since 14.0
 */
public class JpaUpdateCustomerBatchIDs extends JpaDataOperation
{
    /** serialVersionUID **/
    private static final long serialVersionUID = -8500511368505043621L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JpaUpdateCustomerBatchIDs.class);

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.data.JpaDataOperation#execute(oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc, java.io.Serializable)
     */
    @Override
    protected Serializable execute(StoresDataServiceIfc service, Serializable dataObject) throws ServiceException, DataException
    {
        CustomerServiceIfc customerService = (CustomerServiceIfc)service;

        if (logger.isDebugEnabled())
            logger.debug("JpaUpdateCustomerBatchIDs.execute()");

        DataReplicationCustomerEntryIfc[] customers = (DataReplicationCustomerEntryIfc[])dataObject;

        int updateCount = 0;
        int batchID = -1;
        if (customers.length > 0)
        {
            batchID = Integer.parseInt(customers[0].getBatchID());
        }
        for (int i = 0; i < customers.length; i++)
        {
            customerService.updateCustomerBatchID(customers[i].getCustomerID(), batchID);
            updateCount++;
        }

        Integer returnCount = Integer.valueOf(updateCount);

        if (logger.isDebugEnabled())
            logger.debug("JpaUpdateCustomerBatchIDs.execute()");

        return returnCount;
    }
}