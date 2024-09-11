/*===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/OrderHistoryReadDataTransaction.java /main/1 2013/01/22 21:01:34 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         01/22/13 - calling getOrderHistory api for order summary report
* sgu         01/22/13 - added jpa operation to retrieve order snapshots
* sgu         01/18/13 - add new transaction
* sgu         01/18/13 - Creation
* ===========================================================================
*/

package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.order.OrderSearchCriteriaIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.storeservices.services.common.ServiceException;

import org.apache.log4j.Logger;

public class OrderHistoryReadDataTransaction extends DataTransaction
{
    /**
     * serialVersionUID
     */
	private static final long serialVersionUID = 8366978273127076066L;

	/**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.OrderHistoryReadDataTransaction.class);

    /**
     * The name that links this transaction to a command within DataScript.
     */
    public static String dataCommandName="OrderHistoryReadDataTransaction";
    
    /**
     * Class constructor.
     */
    public OrderHistoryReadDataTransaction()
    {                                   // begin OrderReadDataTransaction()
        super(dataCommandName);
    }                                   // end OrderReadDataTransaction()

    /**
     * Class constructor.
     * @param name data command name
     */
    public OrderHistoryReadDataTransaction(String name)
    {                                   // begin OrderReadDataTransaction()
        super(name);
    }    
    
    /**
     * retrieve snapshots of orders modified within the specified date range
     * @param beginDate the begin date
     * @param endDate the end date
     * @param storeId the store id
     * @param trainingMode training mode flag
     * @return a list of order snapshots
     * @throws ServiceException
     */
    public OrderSummaryEntryIfc[] retrieveOrderSnapshotByDateRange(EYSDate beginDate, EYSDate endDate, 
    		String storeId, boolean trainingMode) throws DataException
    {
    	OrderSearchCriteriaIfc criteria = DomainGateway.getFactory().getOrderSearchCriteriaInstance();
    	
    	criteria.setStartDate(beginDate.toGregorianCalendar().getTime());
    	criteria.setEndDate(endDate.toGregorianCalendar().getTime());
    	criteria.setStoreID(storeId);
    	criteria.setTrainingMode(trainingMode);
    	OrderSummaryEntryIfc[] summaryList = retrieveOrderSnapshot(criteria);
    	
    	return summaryList;
    }
    
    protected OrderSummaryEntryIfc[] retrieveOrderSnapshot(OrderSearchCriteriaIfc criteria) throws DataException
    { 
    	 DataAction dataAction = new DataAction();
    	 dataAction.setDataObject(criteria);
    	 dataAction.setDataOperationName("RetrieveOrderSnapshot");
    	
         DataActionIfc[] dataActions = new DataActionIfc[1];
         dataActions[0] = dataAction;

         setDataActions(dataActions);
         OrderSummaryEntryIfc[] summaries = (OrderSummaryEntryIfc[]) getDataManager().execute(this);
         return summaries;
    }
   
}


