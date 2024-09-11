/*===========================================================================
* Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/jpa/JpaRetrieveOrderSnapshot.java /main/6 2014/07/07 10:43:50 cgreene Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* cgreene     07/01/14 - refactor throwing and catching exceptions so that
*                        SQLExceptions get mapped
* abhinavs    06/16/14 - CAE Order summary enhancement phase I
* jswan       12/13/13 - Upated JAVADOC.
* abondala    01/27/13 - extending JPA
* sgu         01/25/13 - use currency in order summary
* sgu         01/22/13 - retrieve order summary from ORCO
* sgu         01/22/13 - calling getOrderHistory api for order summary report
* sgu         01/22/13 - added jpa operation to retrieve order snapshots
* sgu         01/21/13 - add new jpa operation
* sgu         01/21/13 - Creation
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
 * This class retrieves an order snapshot.
 * @since 14.0
 */
@SuppressWarnings("serial")
public class JpaRetrieveOrderSnapshot extends JpaRetrieveOrder
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.data.JpaDataOperation#execute(oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc, java.io.Serializable)
     */
    @Override
    protected Serializable execute(StoresDataServiceIfc service, Serializable dataObject) throws ServiceException, DataException
    {
		OrderServiceIfc orderService = (OrderServiceIfc) service; 
		OrderSearchCriteriaIfc criteria = (OrderSearchCriteriaIfc)dataObject;
		List<OrderSummary>  orderSummaries = null;

        orderSummaries = orderService.getOrderSnapshotsByDateRange(criteria.getStartDate(), criteria.getEndDate(),
                criteria.getStoreID(), criteria.isTrainingMode());

		OrderSummaryEntryIfc[] orderSummaryEntries = transformOrderSummary(orderSummaries);
		return orderSummaryEntries;
	}

}
