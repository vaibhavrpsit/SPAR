/*===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/jpa/JpaRetrieveOrderSummary.java /main/2 2014/07/07 10:43:50 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 * cgreene     07/01/14 - refactor throwing and catching exceptions so that
 *                        SQLExceptions get mapped
 * abhinavs    06/16/14 - CAE Order summary enhancement phase I
 * abhinavs    06/13/14 - Initial Version
 * abhinavs    06/13/14 - Creation
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts.jpa;

import java.io.Serializable;
import java.util.List;

import oracle.retail.stores.domain.order.OrderSearchCriteriaIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.storeservices.entities.order.OrderSummary;
import oracle.retail.stores.storeservices.services.common.ServiceException;
import oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc;
import oracle.retail.stores.storeservices.services.order.OrderServiceIfc;

/**
 * This class retrieves an order summary.
 * 
 * @since 14.1
 * @author abhinavs
 */
@SuppressWarnings("serial")
public class JpaRetrieveOrderSummary extends JpaRetrieveOrder
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.data.JpaDataOperation#execute(oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc, java.io.Serializable)
     */
    @Override
    protected Serializable execute(StoresDataServiceIfc service, Serializable dataObject) throws ServiceException, DataException
    {
        OrderServiceIfc orderService = (OrderServiceIfc)service;
        OrderSearchCriteriaIfc criteria = (OrderSearchCriteriaIfc)dataObject;
        List<OrderSummary> orderSummaries = orderService.getOrderSummaries(criteria);

        OrderSummaryEntryIfc[] orderSummaryEntries = transformOrderSummary(orderSummaries);
        return orderSummaryEntries;
    }
}
