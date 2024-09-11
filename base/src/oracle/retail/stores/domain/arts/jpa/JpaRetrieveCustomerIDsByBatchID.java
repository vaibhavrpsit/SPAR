/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/jpa/JpaRetrieveCustomerIDsByBatchID.java /main/8 2014/07/07 10:43:50 cgreene Exp $
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
 *    cgreene   09/24/12 - Implement maximum customer record retrieval for dtm
 *                         export
 *    abondala  08/13/12 - jpa for retrieve customers by batch id for DTM
 *                         related
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.datareplication.DataReplicationCustomerEntryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JpaDataOperation;
import oracle.retail.stores.storeservices.entities.customer.Customer;
import oracle.retail.stores.storeservices.services.common.ServiceException;
import oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc;
import oracle.retail.stores.storeservices.services.customer.CustomerServiceIfc;

/**
 * This operation reads a list of customer IDs from a database.
 * @since 14.0
 */
public class JpaRetrieveCustomerIDsByBatchID extends JpaDataOperation
{
    /** serialVersionUID **/
    private static final long serialVersionUID = 7671143649908443711L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JpaRetrieveCustomerIDsByBatchID.class);

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.data.JpaDataOperation#execute(oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc, java.io.Serializable)
     */
    @Override
    protected Serializable execute(StoresDataServiceIfc service, Serializable dataObject) throws ServiceException, DataException
    {
        CustomerServiceIfc customerService = (CustomerServiceIfc)service;

        try
        {
            if (logger.isDebugEnabled()) logger.debug("JpaRetrieveCustomerIDsByBatchID.execute()");

            DataReplicationCustomerEntryIfc tLogEntry = (DataReplicationCustomerEntryIfc)dataObject;

            int batchId = Integer.parseInt(tLogEntry.getBatchID());
            List<Customer> customers = customerService.findCustomerIDsByBatchID(batchId, tLogEntry.getMaximumTransactionsToExport());
            DataReplicationCustomerEntryIfc[] entries =  parseSelectCustomerIDs(customers);

            if (logger.isDebugEnabled()) logger.debug("JpaRetrieveCustomerIDsByBatchID.execute()");
            return entries;
        }
        catch (DataException de)
        {
            // The NO_DATA condition is very common here; if the Daemon calling
            // this class has a short sleep interval, it will flood the
            // log with messages.  Setting this to info gives the system
            // implementator the option of removing it from log using
            // the log4j filter.
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                logger.info("No customers transactions found by batch id.");
            }
            else
            {
                logger.warn(de.toString());
            }
            throw de;
        }
    }

    private DataReplicationCustomerEntryIfc[] parseSelectCustomerIDs(List<Customer> customers) throws DataException
    {
        ArrayList<DataReplicationCustomerEntryIfc> entryList = new ArrayList<DataReplicationCustomerEntryIfc>();

        DataReplicationCustomerEntryIfc entry = null;

        for (Customer customer : customers)
        {
            entry = DomainGateway.getFactory().getDataReplicationCustomerEntry();

            // set customer id into entity
            entry.setCustomerID(customer.getCustomerID());

            // add entry to list
            entryList.add(entry);
        }

        // if no entries, throw exception
        if (entryList.size() == 0)
        {
            throw new DataException(DataException.NO_DATA,
                    "No transactions found matching search criteria for customers.");
        }
        // copy list into array
        DataReplicationCustomerEntryIfc[] entries = new DataReplicationCustomerEntryIfc[entryList.size()];

        entryList.toArray(entries);

        return (entries);
    }

}
