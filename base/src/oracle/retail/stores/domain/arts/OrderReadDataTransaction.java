/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/OrderReadDataTransaction.java /main/24 2014/06/17 15:26:37 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  06/16/14 - CAE order summary enhancement phase I
 *    yiqzhao   01/15/13 - Change super class to StoreOrderReadDataTransaction.
 *    sgu       01/08/13 - add support for order picklist
 *    yiqzhao   01/08/13 - Add search by order status.
 *    yiqzhao   12/24/12 - Refactoring xc formatter, transformer and others.
 *    yiqzhao   12/17/12 - Read store orders from Central Office through
 *                         Webservices.
 *    yiqzhao   12/10/12 - Retrieve store orders from centraloffice through
 *                         WebService.
 *    yiqzhao   08/01/12 - Set store id to null for order search by
 *                         credit/debit card.
 *    sgu       07/18/12 - rename itemID to itemNumber
 *    sgu       07/17/12 - add order summary search by card token or masked
 *                         number
 *    sgu       07/17/12 - add item serach criteria
 *    sgu       07/13/12 - remove order retrieval by its status
 *    asinton   03/19/12 - removed deprecated methods.
 *    asinton   03/15/12 - Removed deprecated JdbcRetrieveOrder and data
 *                         operation.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    jswan     04/24/09 - Code review changes.
 *    jswan     04/24/09 - Modified to ensure that orders created in training
 *                         mode can only retrieve in training mode, and
 *                         non-training mode orders can only be retrieved in
 *                         non-training mode.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:14 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:52 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:53 PM  Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:38  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:48  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Mar 10 2003 09:37:56   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.1   Feb 15 2003 17:26:08   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   Jun 03 2002 16:42:04   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;

import org.apache.log4j.Logger;



/**
 * This class handles the DataTransaction behavior for reading Orders.
 */
public class OrderReadDataTransaction extends StoreOrderReadDataTransaction
{                                       // begin class OrderReadDataTransaction
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7348475060601327128L;

    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.OrderReadDataTransaction.class);

    /**
     * The name that links this transaction to a command within DataScript.
     */
    public static String dataCommandName="OrderReadDataTransaction";

    /**
     * Class constructor.
     */
    public OrderReadDataTransaction()
    {                                   // begin OrderReadDataTransaction()
        super(dataCommandName);
    }                                   // end OrderReadDataTransaction()

    /**
     * Class constructor.
     * @param name data command name
     */
    public OrderReadDataTransaction(String name)
    {                                   // begin OrderReadDataTransaction()
        super(name);
    }                                   // end OrderReadDataTransaction()

    /**
     * Retrieves summaries of orders for a given order ID and shipped to
     * a given store created within a date range.
     * The store and date range parameters are optional.
     * @param order Order reference
     * @param storeID store identifier  (optional)
     * @param beginDate begin date (optional)
     * @param endDate end date (optional)
     * @param itemID item ID (optional)
     * @return array of summaries of orders
     * @exception  DataException when an error occurs.
     * @deprecated
     */
    public OrderSummaryEntryIfc[]
    retrieveOrderSummaryByOrderID(OrderIfc order,
                                  String storeID,
                                  EYSDate beginDate,
                                  EYSDate endDate,
                                  String itemID)
        throws DataException
    {                                   // begin retrieveOrderSummaryByOrderID()
        return(retrieveOrderSummaryByOrderID
          (order,
           storeID,
           beginDate,
           endDate,
           itemID,
           OrderConstantsIfc.ORDER_CHANNEL_WEB));
    }                                   // end retrieveOrderSummaryByOrderID()

    /**
     * Retrieves summaries of orders for a given order ID and shipped to
     * a given store created within a date range.
     * The store and date range parameters are optional.
     * @param order Order reference
     * @param storeID store identifier  (optional)
     * @param beginDate begin date (optional)
     * @param endDate end date (optional)
     * @param itemID item ID (optional)
     * @param initiatingChannel initiating channel (web or store)
     * @return array of summaries of orders
     * @exception  DataException when an error occurs.
     * @deprecated
     */
    public OrderSummaryEntryIfc[]
    retrieveOrderSummaryByOrderID(OrderIfc order,
                                  String storeID,
                                  EYSDate beginDate,
                                  EYSDate endDate,
                                  String itemID,
                                  int initiatingChannel)
        throws DataException
    {                                   // begin retrieveOrderSummaryByOrderID()
        boolean trainingMode = false;
        return(retrieveOrderSummaryByOrderID(order, storeID, beginDate, endDate, itemID, initiatingChannel, trainingMode));
    }                                   // end retrieveOrderSummaryByOrderID()

    /**
     * Retrieves summaries of orders for a given order ID and shipped to
     * a given store created within a date range.
     * The store and date range parameters are optional.
     * @param order Order reference
     * @param storeID store identifier  (optional)
     * @param beginDate begin date (optional)
     * @param endDate end date (optional)
     * @param itemID item ID (optional)
     * @param trainingMode training Mode
     * @return array of summaries of orders
     * @exception  DataException when an error occurs.
     * @deprecated
     */
    public OrderSummaryEntryIfc[]
    retrieveOrderSummaryByOrderID(OrderIfc order,
                                  String storeID,
                                  EYSDate beginDate,
                                  EYSDate endDate,
                                  String itemID,
                                  boolean trainingMode)
        throws DataException
    {                                   // begin retrieveOrderSummaryByOrderID()
        return(retrieveOrderSummaryByOrderID
          (order,
           storeID,
           beginDate,
           endDate,
           itemID,
           OrderConstantsIfc.ORDER_CHANNEL_WEB,
           trainingMode));
    }

    /**
     * Retrieves summaries of orders for a given order ID and shipped to
     * a given store created within a date range.
     * The store and date range parameters are optional.
     * @param order Order reference
     * @param storeID store identifier  (optional)
     * @param beginDate begin date (optional)
     * @param endDate end date (optional)
     * @param initiatingChannel initiating channel (web or store)
     * @param trainingMode training Mode
     * @return array of summaries of orders
     * @exception  DataException when an error occurs.
     * @deprecated
     */
    public OrderSummaryEntryIfc[]
    retrieveOrderSummaryByOrderID(OrderIfc order,
                                  String storeID,
                                  EYSDate beginDate,
                                  EYSDate endDate,
                                  String itemID,
                                  int initiatingChannel,
                                  boolean trainingMode)
        throws DataException
    {                                   // begin retrieveOrderSummaryByOrderID()
        if (logger.isDebugEnabled()) logger.debug(
                     "OrderReadDataTransaction.retrieveOrderSummary (by Order)");

        OrderSummaryEntryIfc[] orderSummaryList = null;
        OrderSearchKey orderSearchKey = new OrderSearchKey();
        orderSearchKey.setOrder(order);
        orderSearchKey.setStoreID(storeID);
        orderSearchKey.setBeginDate(beginDate);
        orderSearchKey.setEndDate(endDate);
        orderSearchKey.setItemNumber(itemID);
        orderSearchKey.setSource(order.getSource());
        orderSearchKey.setInitiatingChannel(initiatingChannel);
        orderSearchKey.setTrainingMode(trainingMode);

        // set data actions and execute
        addAction("RetrieveOrderSummary", orderSearchKey);
        setActions();

        // execute data request
        orderSummaryList = (OrderSummaryEntryIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "OrderReadDataTransaction.retrieveOrderSummary (by Order)");

        return(orderSummaryList);
    }                                   // end retrieveOrderSummaryByOrderID()
    // end retrieveOrderSummaryByOrderID()



    /**
     * Retrieves summaries of orders for a given order ID and shipped to
     * a given store created within a date range.
     * The store and date range parameters are optional.
     * @param eMessage EMessage reference
     * @param storeID store identifier  (optional)
     * @param beginDate begin date (optional)
     * @param endDate end date (optional)
     * @param initiatingChannel initiating channel (store or web)
     * @return array of summaries of orders
     * @exception  DataException when an error occurs.
     * @deprecated as of 14.1
     */
    public OrderSummaryEntryIfc[]
    retrieveOrderSummaryByOrderID(EMessageIfc eMessage,
                                  String storeID,
                                  EYSDate beginDate,
                                  EYSDate endDate,
                                  int initiatingChannel)
        throws DataException
    {                                   // begin retrieveOrderSummaryByOrderID()
        boolean trainingMode = false;
        return(retrieveOrderSummaryByOrderID(eMessage, storeID, beginDate, endDate, initiatingChannel, trainingMode));
    }                                   // end retrieveOrderSummaryByOrderID()

    /**
     * Retrieves summaries of orders for a given order ID and shipped to
     * a given store created within a date range.
     * The store and date range parameters are optional.
     * @param eMessage EMessage reference
     * @param storeID store identifier  (optional)
     * @param beginDate begin date (optional)
     * @param endDate end date (optional)
     * @param initiatingChannel initiating channel (store or web)
     * @param trainingMode training Mode
     * @return array of summaries of orders
     * @exception  DataException when an error occurs.
     * @deprecated as of 14.1
     */
    public OrderSummaryEntryIfc[]
    retrieveOrderSummaryByOrderID(EMessageIfc eMessage,
                                  String storeID,
                                  EYSDate beginDate,
                                  EYSDate endDate,
                                  int initiatingChannel,
                                  boolean trainingMode)
        throws DataException
    {                                   // begin retrieveOrderSummaryByOrderID()
        if (logger.isDebugEnabled()) logger.debug(
                     "OrderReadDataTransaction.retrieveOrderSummary (by EMessage)");

        OrderIfc order = DomainGateway.getFactory().getOrderInstance();
        order.setOrderID(eMessage.getOrderID());
        order.setSource(eMessage.getSource());  // May not be necessary

        OrderSummaryEntryIfc[] orderSummaryList = null;
        OrderSearchKey orderSearchKey = new OrderSearchKey();
        orderSearchKey.setOrder(order);
        orderSearchKey.setStoreID(storeID);
        orderSearchKey.setBeginDate(beginDate);
        orderSearchKey.setEndDate(endDate);
        orderSearchKey.setSource(eMessage.getSource());
        orderSearchKey.setInitiatingChannel(initiatingChannel);
        orderSearchKey.setTrainingMode(trainingMode);

        // set data actions and execute
        addAction("RetrieveOrderSummary", orderSearchKey);
        setActions();

        // execute data request
        orderSummaryList = (OrderSummaryEntryIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "OrderReadDataTransaction.retrieveOrderSummary (by EMessage)");

        return(orderSummaryList);
    }                                   // end retrieveOrderSummaryByOrderID()

    /**
     * Retrieves summaries of orders for a given order ID and shipped to
     * a given store created within a date range.
     * The store and date range parameters are optional.
     * @param eMessage EMessage reference
     * @param storeID store identifier  (optional)
     * @param beginDate begin date (optional)
     * @param endDate end date (optional)
     * @return array of summaries of orders
     * @exception  DataException when an error occurs.
     * @deprecated as of 14.1
     */
    public OrderSummaryEntryIfc[]
    retrieveOrderSummaryByOrderID(EMessageIfc eMessage,
                                  String storeID,
                                  EYSDate beginDate,
                                  EYSDate endDate)
        throws DataException
    {                                   // begin retrieveOrderSummaryByOrderID()
        return(retrieveOrderSummaryByOrderID
          (eMessage,
           storeID,
           beginDate,
           endDate,
           OrderConstantsIfc.ORDER_CHANNEL_WEB));
    }                                   // end retrieveOrderSummaryByOrderID()

    /**
     * Retrieves summaries of orders for a given order ID and shipped to
     * a given store created within a date range.
     * The store and date range parameters are optional.
     * @param eMessage EMessage reference
     * @param storeID store identifier  (optional)
     * @param beginDate begin date (optional)
     * @param endDate end date (optional)
     * @param trainingMode training Mode
     * @return array of summaries of orders
     * @exception  DataException when an error occurs.
     * @deprecated as of 14.1
     */
    public OrderSummaryEntryIfc[]
    retrieveOrderSummaryByOrderID(EMessageIfc eMessage,
                                  String storeID,
                                  EYSDate beginDate,
                                  EYSDate endDate,
                                  boolean trainingMode)
        throws DataException
    {                                   // begin retrieveOrderSummaryByOrderID()
        return(retrieveOrderSummaryByOrderID
          (eMessage,
           storeID,
           beginDate,
           endDate,
           OrderConstantsIfc.ORDER_CHANNEL_WEB,
           trainingMode));
    }                                   // end retrieveOrderSummaryByOrderID()

    /**
     * Retrieves order by its identifier and channel.
     * @param orderID order identifier
     * @param sourceChannel channel identifier
     * @param localeReq locales of the order
     * @return requested order
     * @exception throws data exception if error occurs
     */
    public OrderIfc readOrder(String orderID,
                              int sourceChannel,
                              LocaleRequestor localeReq,
                              boolean trainingMode)
                              throws DataException
    {                                   // begin readOrder()
        if (logger.isDebugEnabled()) logger.debug(
                     "OrderReadDataTransaction.readOrder");

        OrderStatusIfc searchOrder =
          DomainGateway.getFactory().getOrderStatusInstance();
        searchOrder.setOrderID(orderID);
        searchOrder.setInitiatingChannel(sourceChannel);
        searchOrder.setLocaleRequestor(localeReq);
        searchOrder.setTrainingModeFlag(trainingMode);

        // set data actions and execute
        addAction("ReadOrderStatus", searchOrder);
        addAction("ReadOrderByTransaction", localeReq);
        setActions();

        OrderIfc order =
              (OrderIfc) getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "OrderReadDataTransaction.readOrder");

        return(order);
    }                                   // end readOrder()

    /**
     * Retrieves order by its identifier and channel.
     * @param searchOrder OrderStatusIfc Reference
     * @return requested order
     * @exception throws data exception if error occurs
     */
    public OrderIfc readOrder(OrderStatusIfc searchOrder)
                              throws DataException
    {                                   // begin readOrder()
        if (logger.isDebugEnabled()) logger.debug(
                     "OrderReadDataTransaction.readOrder");

        // set data actions and execute
        addAction("ReadOrderStatus", searchOrder);
        addAction("ReadOrderByTransaction", searchOrder.getLocaleRequestor());
        setActions();

        OrderIfc order =
              (OrderIfc) getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "OrderReadDataTransaction.readOrder");

        return(order);
    }                                   // end readOrder()

                                 

    /**
     * Returns the string representation of this object.
     * @return String representation of object
     */
    public String toString()
    {
        StringBuffer strResult =
            new StringBuffer("Class: OrderReadDataTransaction ");
        strResult.append("(Revision ").append(getRevisionNumber());
        strResult.append(") @").append(hashCode());
        return(strResult.toString());
    }
}                                                                               // end class OrderReadDataTransaction
