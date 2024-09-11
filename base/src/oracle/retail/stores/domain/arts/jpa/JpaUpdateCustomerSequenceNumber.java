/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/jpa/JpaUpdateCustomerSequenceNumber.java /main/4 2014/07/07 10:43:50 cgreene Exp $
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
 *    abondala  08/10/12 - jpa update customer sequence number
 * 
 * 
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts.jpa;

import java.io.Serializable;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JpaDataOperation;
import oracle.retail.stores.storeservices.services.common.ServiceException;
import oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc;
import oracle.retail.stores.storeservices.services.customer.CustomerServiceIfc;

/**
 * This operation update customer sequence number database.
 * 
 * @since 14.0
 */
public class JpaUpdateCustomerSequenceNumber extends JpaDataOperation
{
    /** serialVersionUID **/
    private static final long serialVersionUID = -6215317542279700835L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JpaUpdateCustomerSequenceNumber.class);

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.data.JpaDataOperation#execute(oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc, java.io.Serializable)
     */
    @Override
    protected Serializable execute(StoresDataServiceIfc service, Serializable dataObject) throws ServiceException, DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JpaUpdateCustomerSequenceNumber.execute");

        CustomerServiceIfc customerService = (CustomerServiceIfc)service;
        RegisterIfc register = (RegisterIfc)dataObject;

        int status = customerService.updateCustomerSequenceNumber(register.getWorkstation().getWorkstationID(),
                register.getWorkstation().getStoreID(), register.getLastCustomerSequenceNumber());

        if (status != 1)
        {
            throw new DataException(DataException.NO_DATA, "Update customer sequence number in Workstation table");
        }

        if (logger.isDebugEnabled())
            logger.debug("JpaUpdateCustomerSequenceNumber.execute");

        return null;
    }
}
