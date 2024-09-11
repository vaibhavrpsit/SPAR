/*===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/OrderSummaryReadDataTransaction.java /main/1 2014/06/17 15:26:38 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    06/16/14 - CAE order summary enhancement phase I
* abhinavs    06/16/14 - Initial Version
* abhinavs    06/16/14 - Creation
* ===========================================================================
*/
package oracle.retail.stores.domain.arts;

// java imports
import oracle.retail.stores.domain.order.OrderSearchCriteriaIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;

import org.apache.log4j.Logger;

/**
 * This class handles the DataTransaction behavior for reading Order summaries.
 * 
 * @Since 14.1
 * @author abhinavs
 */
public class OrderSummaryReadDataTransaction extends OrderReadDataTransaction
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1572953368772605939L;

    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger
            .getLogger(oracle.retail.stores.domain.arts.OrderSummaryReadDataTransaction.class);

    /**
     * The name that links this transaction to a command within DataScript.
     */
    public static String dataCommandName = "OrderSummaryReadDataTransaction";

    /**
     * Class constructor.
     */
    public OrderSummaryReadDataTransaction()
    { // begin OrderSummaryReadDataTransaction()
        super(dataCommandName);
    } // end OrderSummaryReadDataTransaction()

    /**
     * Class constructor.
     * 
     * @param name data command name
     */
    public OrderSummaryReadDataTransaction(String name)
    { // begin OrderSummaryReadDataTransaction()
        super(name);
    } // end OrderSummaryReadDataTransaction()

    /**
     * Retrieves summaries of orders based on a search criteria
     * 
     * @param searchCriteria order search criteria
     * @exception DataException when an error occurs.
     */
    public OrderSummaryEntryIfc[] retrieveOrderSummary(OrderSearchCriteriaIfc searchCriteria)
            throws DataException
    { // begin retrieveOrderSummaryByDateRange()
        if (logger.isDebugEnabled())
            logger.debug("OrderSummaryReadDataTransaction.retrieveOrderSummary");

        // set data actions and execute
        addAction("RetrieveOrderSummary", searchCriteria);
        setActions();

        // execute data request
        OrderSummaryEntryIfc[] orderSummaryList = (OrderSummaryEntryIfc[])getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("" + "OrderSummaryReadDataTransaction.retrieveOrderSummary" + "");

        return (orderSummaryList);
    } // end retrieveOrderSummaryByDateRange()

    /**
     * Returns the string representation of this object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        StringBuffer strResult = new StringBuffer("Class: OrderSummaryReadDataTransaction ");
        strResult.append("(Revision ").append(getRevisionNumber());
        strResult.append(") @").append(hashCode());
        return (strResult.toString());
    }
} // end class OrderSummaryReadDataTransaction

