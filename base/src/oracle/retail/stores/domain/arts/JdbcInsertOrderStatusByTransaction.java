/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcInsertOrderStatusByTransaction.java /main/7 2014/01/28 17:48:01 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 * cgreene     01/28/14 - Add support for saving type of external order
 * cgreene     01/24/14 - add column to indicate transaction is created from an
 *                        ATG order
 * abhinavs    07/08/13 - Modified to insert a row in order status table when
 *                        order status changed to printed or filled
 * sgu         02/19/13 - donot set blank customer id
 * sgu         02/19/13 - save order status for a suspended order transaction
 * sgu         01/22/13 - insert customer id and training flag
 * sgu         01/17/13 - add order service api to get order snapshots by date
 *                        range
 * yiqzhao     12/17/12 - Read store orders from Central Office through
 *                        Webservices.
 * sgu         05/11/12 - add more comments
 * sgu         05/10/12 - handle the case the customer is optional to an xc
 *                        order
 * sgu         05/09/12 - separate minimum deposit amount into xchannel part
 *                        and store order part
 * sgu         05/08/12 - prorate store order and xchannel deposit amount
 *                        separatly
 * sgu         05/07/12 - rename crossChannel to XChannel
 * sgu         05/07/12 - read/write order status table
 * sgu         05/07/12 - added jdbc class to insert order status at transaction
 *                        level
 * sgu         05/07/12 - added jdbc class to insert order status at transaction
 *                        level
 * sgu         05/07/12 - Creation
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.Currency;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.transaction.OrderStatusChangeTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSStatusIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;
import oracle.retail.stores.storeservices.entities.order.OrderConstantsIfc;

import org.apache.log4j.Logger;

public class JdbcInsertOrderStatusByTransaction extends JdbcDataOperation
{
    private static final long serialVersionUID = 3538592524777977064L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcInsertOrderStatusByTransaction.class);

    /**
     * Class constructor.
     */
    public JdbcInsertOrderStatusByTransaction()
    {
        setName("JdbcInsertOrderStatusByTransaction");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction
     * @param dataConnection
     * @param action
     */
    @Override
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcInsertOrderStatusByTransaction.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        String currentTimeStamp = getSQLCurrentTimestampFunction();
        
        if(action.getDataObject() instanceof OrderTransactionIfc)
        {
        // retrieve input from data object
        OrderTransactionIfc orderTransaction =
            (OrderTransactionIfc) action.getDataObject();
        OrderStatusIfc orderStatus = orderTransaction.getOrderStatus();
               
        boolean insertStoreOrderStatus = false, insertXChannelOrderStatus = false;
        boolean isSuspendedTxn = orderTransaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED;
        boolean storeOrderStatusDefined = orderStatus.getStoreOrderStatus().getStatus() != OrderConstantsIfc.ORDER_STATUS_UNDEFINED;
        boolean xChannelOrderStatusDefined = orderStatus.getXChannelStatus().getStatus() != OrderConstantsIfc.ORDER_STATUS_UNDEFINED;
        
        // Only an ORDER_INITIATE transaction can be suspended. Its store order status is always undefined for a suspended order.
        // Invoke function containsXChannelOrderLineItemOnly to determine if it has an internal store order part.
        if (isSuspendedTxn && !orderTransaction.containsXChannelOrderLineItemOnly())
        {
            insertStoreOrderStatus = true;
        }
        // For a completed order transaction, if its store order status is defined, the order has an internal store order part.
        else if (!isSuspendedTxn && storeOrderStatusDefined)
        {
            insertStoreOrderStatus = true;
        }
        
        // Only an ORDER_INITIATE transaction can be suspended. Its xchannel order status is always undefined for a suspended order.
        // Invoke function containsXChannelOrderLineItem to determine if it has an xchannel order part.
        if (isSuspendedTxn && orderTransaction.containsXChannelOrderLineItem())
        {
            insertXChannelOrderStatus = true;
        }
        // For a completed order transaction, if its xchannel order status is defined, the order has an xchannel order part.
        else if (!isSuspendedTxn && xChannelOrderStatusDefined)
        {
            insertXChannelOrderStatus = true;
        }

        // If there is internal store order part to the order, save its satus.
        if (insertStoreOrderStatus)
        {
            insertOrderStatus(connection, orderTransaction, false, currentTimeStamp);
        }
        // If there is xchannel order part to the order, save its status.
        if (insertXChannelOrderStatus)
        {
            insertOrderStatus(connection, orderTransaction, true, currentTimeStamp);
        }

        }
        else if(action.getDataObject() instanceof OrderStatusChangeTransactionIfc)
        {
            // retrieve input from data object
            OrderStatusChangeTransactionIfc orderStatusChangeTransaction =
                (OrderStatusChangeTransactionIfc) action.getDataObject();
            insertOrderStatusChangeTransaction(connection, orderStatusChangeTransaction, currentTimeStamp);
        }
   
        if (logger.isDebugEnabled()) logger.debug(
                "JdbcInsertOrderStatusByTransaction.execute");
    }

    private void insertOrderStatusChangeTransaction(JdbcDataConnection dataConnection,
            OrderStatusChangeTransactionIfc orderStatusChangeTransaction, String currentTimeStamp) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(ARTSDatabaseIfc.TABLE_ORDER_STATUS);

        // add insert columns
        addInsertColumnsStatusChangeTransaction(sql, orderStatusChangeTransaction, currentTimeStamp);
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error(Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN, "insertOrderStatus", e);
        }
    }

    public void insertOrderStatus(JdbcDataConnection dataConnection, OrderTransactionIfc orderTransaction, 
            boolean isXChannelOrder,  String currentTimestamp) throws DataException
    {
        // get an sql object
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(ARTSDatabaseIfc.TABLE_ORDER_STATUS);

        // add insert columns
        addInsertColumns(sql, orderTransaction, isXChannelOrder, currentTimestamp);

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error(Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN, "insertOrderStatus", e);
        }
    }

    private void addInsertColumnsStatusChangeTransaction(SQLInsertStatement sql, OrderStatusChangeTransactionIfc orderStatusChangeTransaction,
            String currentTimestamp)
    {
        // add insert columns
        OrderStatusIfc order = orderStatusChangeTransaction.getOrder().getStatus();
        EYSStatusIfc status = order.getStoreOrderStatus();
        CurrencyIfc orderTotal = order.getStoreOrderTotal();
        CurrencyIfc orderDepositAmount = order.getStoreOrderDepositAmount();
        CurrencyIfc orderBalanceDue = order.getStoreOrderBalanceDue();
        CurrencyIfc orderMinimumDepositAmount = order.getStoreOrderMinimumDepositAmount();

        CustomerIfc customer = orderStatusChangeTransaction.getOrder().getCustomer();
        sql.addColumn(ARTSDatabaseIfc.FIELD_RETAIL_STORE_ID,
                inQuotes(orderStatusChangeTransaction.getWorkstation().getStoreID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_WORKSTATION_ID,
                inQuotes(orderStatusChangeTransaction.getWorkstation().getWorkstationID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_BUSINESS_DAY_DATE,
                dateToSQLDateString(orderStatusChangeTransaction.getBusinessDay()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_TRANSACTION_SEQUENCE_NUMBER,
                String.valueOf(orderStatusChangeTransaction.getTransactionSequenceNumber()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_XC_ORDER_FLAG,
                makeStringFromBoolean(false));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_ID,
                inQuotes(orderStatusChangeTransaction.getOrder().getOrderID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_STATUS,
                Integer.toString(status.getStatus()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_STATUS_PREVIOUS,
                Integer.toString(status.getPreviousStatus()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_STATUS_CHANGE,
                dateToSQLDateString
                (status.getLastStatusChange()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_BEGIN,
                dateToSQLDateString
                (order.getTimestampBegin()));
        if (customer != null)
        {
            sql.addColumn(ARTSDatabaseIfc.FIELD_CUSTOMER_ID,
                    inQuotes(customer.getCustomerID()));
            sql.addColumn(ARTSDatabaseIfc.FIELD_CONTACT_FIRST_NAME,
                    makeSafeString
                    (customer.getFirstName()));
            sql.addColumn(ARTSDatabaseIfc.FIELD_CONTACT_MIDDLE_INITIAL,
                    makeSafeString
                    (customer.getMiddleName()));
            sql.addColumn(ARTSDatabaseIfc.FIELD_CONTACT_LAST_NAME,
                    makeSafeString
                    (customer.getLastName()));
            sql.addColumn(ARTSDatabaseIfc.FIELD_CONTACT_BUSINESS_NAME, 
                    makeSafeString
                    (customer.getCompanyName()));
            sql.addColumn(ARTSDatabaseIfc.FIELD_EMAIL_ADDRESS,
                    inQuotes
                    (customer.getEMailAddress()));
        }
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_TOTAL,
                orderTotal.toString());
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_DEPOSIT_AMOUNT,
                orderDepositAmount.toString());
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_INITIATION_CHANNEL,
                inQuotes(order.getInitiatingChannel()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_MINIMUM_DEPOSIT_AMOUNT,
                orderMinimumDepositAmount.toString());
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_BALANCE_DUE,
                orderBalanceDue.toString());
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_ORIGINAL_STORE_ID,
                inQuotes(order.getInitialTransactionID().getStoreID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_ORIGINAL_WORKSTATION_ID,
                inQuotes(order.getInitialTransactionID().getWorkstationID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_ORIGINAL_TRANSACTION_BUSINESS_DATE,
                dateToSQLDateString
                (order.getInitialTransactionBusinessDate()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER,
                Long.toString
                (order.getInitialTransactionID().getSequenceNumber()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_RECORDING_STORE_ID,
                inQuotes(order.getRecordingTransactionID().getStoreID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_RECORDING_WORKSTATION_ID,
                inQuotes(order.getRecordingTransactionID().getWorkstationID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_RECORDING_TRANSACTION_BUSINESS_DATE,
                emptyStringToSpaceString(dateToSQLDateString
                        (order.getRecordingTransactionBusinessDate())));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_RECORDING_TRANSACTION_SEQUENCE_NUMBER,
                Long.toString
                (order.getRecordingTransactionID().getSequenceNumber()));

        sql.addColumn(ARTSDatabaseIfc.FIELD_CURRENCY_ID, order.getSaleAmount().getType().getCurrencyId());

        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, currentTimestamp);
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_MODIFIED_TIMESTAMP, currentTimestamp);

        // set description column
        setDescriptionColumnStatusChangeTransaction(sql, orderStatusChangeTransaction);

        // add field for order ID
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_LOCATION,
                inQuotes(orderStatusChangeTransaction.getOrder().getStatus().getLocation()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_CREATION_TIMESTAMP, currentTimestamp);
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_CREATE_TIMESTAMP,
                dateToSQLTimestampFunction
                (orderStatusChangeTransaction.getOrder().getStatus().getTimestampCreated()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_BEGIN_TIMESTAMP,
                dateToSQLTimestampFunction
                (orderStatusChangeTransaction.getOrder().getStatus().getTimestampBegin()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_TYPE, orderStatusChangeTransaction.getOrder().getOrderType());

        String saleAmount = Currency.ZERO.toString();
        boolean isSuspendedTxn = orderStatusChangeTransaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED;
        if(!isSuspendedTxn)
        {
            saleAmount = orderStatusChangeTransaction.getOrder().getStatus().getSaleAmount().toString();
        }
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_SALE_AMOUNT, saleAmount);
        sql.addColumn(ARTSDatabaseIfc.FIELD_TRANSACTION_TRAINING_FLAG,
                makeStringFromBoolean(orderStatusChangeTransaction.isTrainingMode()));
    }

    /**
     * Adds insert columns to the SQL statement.
     * 
     * @param sql sql statement
     * @param orderTransaction The order to create in the database
     */
    protected void addInsertColumns(SQLUpdatableStatementIfc sql, OrderTransactionIfc orderTransaction,
            boolean isXChannelOrder, String currentTimestamp)
    {
        // add insert columns
        OrderStatusIfc order = orderTransaction.getOrderStatus();
        EYSStatusIfc status = order.getStoreOrderStatus();
        CurrencyIfc orderTotal = order.getStoreOrderTotal();
        CurrencyIfc orderDepositAmount = order.getStoreOrderDepositAmount();
        CurrencyIfc orderBalanceDue = order.getStoreOrderBalanceDue();
        CurrencyIfc orderMinimumDepositAmount = order.getStoreOrderMinimumDepositAmount();
        if (isXChannelOrder)
        {
            status = order.getXChannelStatus();
            orderTotal = order.getXChannelTotal();
            orderDepositAmount = order.getXChannelDepositAmount();
            orderBalanceDue = order.getXChannelBalanceDue();
            orderMinimumDepositAmount = order.getXChannelMinimumDepositAmount();
        }

        CustomerIfc customer = orderTransaction.getCustomer();
        sql.addColumn(ARTSDatabaseIfc.FIELD_RETAIL_STORE_ID,
                inQuotes(orderTransaction.getWorkstation().getStoreID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_WORKSTATION_ID,
                inQuotes(orderTransaction.getWorkstation().getWorkstationID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_BUSINESS_DAY_DATE,
                dateToSQLDateString(orderTransaction.getBusinessDay()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_TRANSACTION_SEQUENCE_NUMBER,
                String.valueOf(orderTransaction.getTransactionSequenceNumber()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_XC_ORDER_FLAG,
                makeStringFromBoolean(isXChannelOrder));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_ID,
                inQuotes(orderTransaction.getOrderID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_STATUS,
                Integer.toString(status.getStatus()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_STATUS_PREVIOUS,
                Integer.toString(status.getPreviousStatus()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_STATUS_CHANGE,
                dateToSQLDateString
                (status.getLastStatusChange()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_BEGIN,
                dateToSQLDateString
                (order.getTimestampBegin()));
        if (customer != null)
        {
            sql.addColumn(ARTSDatabaseIfc.FIELD_CUSTOMER_ID,
                    inQuotes(customer.getCustomerID()));
            sql.addColumn(ARTSDatabaseIfc.FIELD_CONTACT_FIRST_NAME,
                    makeSafeString
                    (customer.getFirstName()));
            sql.addColumn(ARTSDatabaseIfc.FIELD_CONTACT_MIDDLE_INITIAL,
                    makeSafeString
                    (customer.getMiddleName()));
            sql.addColumn(ARTSDatabaseIfc.FIELD_CONTACT_LAST_NAME,
                    makeSafeString
                    (customer.getLastName()));
            sql.addColumn(ARTSDatabaseIfc.FIELD_CONTACT_BUSINESS_NAME, 
                    makeSafeString
                    (customer.getCompanyName()));
            sql.addColumn(ARTSDatabaseIfc.FIELD_EMAIL_ADDRESS,
                    inQuotes
                    (customer.getEMailAddress()));
        }
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_TOTAL,
                orderTotal.toString());
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_DEPOSIT_AMOUNT,
                orderDepositAmount.toString());
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_INITIATION_CHANNEL,
                inQuotes(order.getInitiatingChannel()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_MINIMUM_DEPOSIT_AMOUNT,
                orderMinimumDepositAmount.toString());
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_BALANCE_DUE,
                orderBalanceDue.toString());
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_ORIGINAL_STORE_ID,
                inQuotes(order.getInitialTransactionID().getStoreID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_ORIGINAL_WORKSTATION_ID,
                inQuotes(order.getInitialTransactionID().getWorkstationID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_ORIGINAL_TRANSACTION_BUSINESS_DATE,
                dateToSQLDateString
                (order.getInitialTransactionBusinessDate()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER,
                Long.toString
                (order.getInitialTransactionID().getSequenceNumber()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_RECORDING_STORE_ID,
                inQuotes(order.getRecordingTransactionID().getStoreID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_RECORDING_WORKSTATION_ID,
                inQuotes(order.getRecordingTransactionID().getWorkstationID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_RECORDING_TRANSACTION_BUSINESS_DATE,
                emptyStringToSpaceString(dateToSQLDateString
                        (order.getRecordingTransactionBusinessDate())));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_RECORDING_TRANSACTION_SEQUENCE_NUMBER,
                Long.toString
                (order.getRecordingTransactionID().getSequenceNumber()));

        sql.addColumn(ARTSDatabaseIfc.FIELD_CURRENCY_ID, order.getSaleAmount().getType().getCurrencyId());

        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, currentTimestamp);
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_MODIFIED_TIMESTAMP, currentTimestamp);

        // set description column
        setDescriptionColumn(sql, orderTransaction);

        // add field for order ID
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_LOCATION,
                inQuotes(orderTransaction.getOrderStatus().getLocation()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_CREATION_TIMESTAMP, currentTimestamp);
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_CREATE_TIMESTAMP,
                dateToSQLTimestampFunction
                (orderTransaction.getOrderStatus().getTimestampCreated()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_BEGIN_TIMESTAMP,
                dateToSQLTimestampFunction
                (orderTransaction.getOrderStatus().getTimestampBegin()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_TYPE, orderTransaction.getOrderType());

        String saleAmount = Currency.ZERO.toString();
        //In case of a xchannel order or a suspend transaction SaleAmount Value is zero.
        boolean isSuspendedTxn = orderTransaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED;
        if(!isXChannelOrder && !isSuspendedTxn)
        {
            saleAmount = orderTransaction.getOrderStatus().getSaleAmount().toString();
        }
        sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_SALE_AMOUNT, saleAmount);
        sql.addColumn(ARTSDatabaseIfc.FIELD_TRANSACTION_TRAINING_FLAG,
                makeStringFromBoolean(orderTransaction.isTrainingMode()));
    }

    /**
     * Sets description from first line item, if available, into SQL statement.
     * This is transactional data
     * 
     * @param sql SQLUpdatableStatementIfc object
     * @param orderStatusChangeTransaction orderStatusChange transaction object
     */
    protected void setDescriptionColumnStatusChangeTransaction(SQLUpdatableStatementIfc sql,
            OrderStatusChangeTransactionIfc orderStatusChangeTransaction)
    {
        String description = null;
        AbstractTransactionLineItemIfc[] lineItems =
                orderStatusChangeTransaction.getOrder().getLineItems();
        if (lineItems.length > 0)
        {
            description = lineItems[0].getItemDescription(LocaleMap.getLocale(LocaleMap.DEFAULT));
        }

        if (!Util.isEmpty(description))
        {
            sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_DESCRIPTION,
                          makeSafeString(description));
        }
    }

    /**
     * Sets description from first line item, if available, into SQL statement.
     * This is transactional data
     * 
     * @param sql SQLUpdatableStatementIfc object
     * @param orderTransaction order transaction object
     */
    protected void setDescriptionColumn(SQLUpdatableStatementIfc sql, OrderTransactionIfc orderTransaction)
    {
        String description = null;
        AbstractTransactionLineItemIfc[] lineItems =
          orderTransaction.getLineItems();
        if (lineItems.length > 0)
        {
            description = lineItems[0].getItemDescription(LocaleMap.getLocale(LocaleMap.DEFAULT));
        }

        if (!Util.isEmpty(description))
        {
            sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_DESCRIPTION,
                          makeSafeString(description));
        }
    }
}
