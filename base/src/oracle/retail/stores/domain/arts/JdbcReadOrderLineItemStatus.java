/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadOrderLineItemStatus.java /main/11 2012/05/21 15:50:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  02/18/09 - Fixed db2 specific query issue
 *    mchellap  12/02/08 - Changed the order line item status table reference
 *    mchellap  11/13/08 - Inventory Reservation Module
 *    mchellap  11/07/08 - Jdbc Class to read order line item status
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.transaction.OrderLineItemSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

/**
 * This operation reads the order line item status for a given order
 * transaction.
 */
public class JdbcReadOrderLineItemStatus extends JdbcDataOperation
{
    private static final long serialVersionUID = -5305572063508810015L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadOrderLineItemStatus.class);

    /**
     * Class constructor.
     */
    public JdbcReadOrderLineItemStatus()
    {
        setName("JdbcReadOrderLineItemStatus");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadOrderLineItemStatus.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        try
        {
            // Get the transaction
            OrderLineItemSearchCriteriaIfc transaction = (OrderLineItemSearchCriteriaIfc)action.getDataObject();
            int itemStatus = readOrderLineItemStatus(connection, transaction);
            dataTransaction.setResult(itemStatus);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            logger.error(Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN, "read order status");
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadOrderLineItemStatus.execute");
    }

    /**
     * Reads the order status object.
     * 
     * @param dataConnection connection to the db
     * @param orderIn order with order id to search for the complete order
     * @return order status object
     * @exception DataException upon error
     */
    public int readOrderLineItemStatus(JdbcDataConnection connection, OrderLineItemSearchCriteriaIfc transaction)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadOrderLineItemStatus.readOrderLineItemStatus()");

        // build SQL statement
        SQLSelectStatement sql = buildSQLSelectStatement(transaction);;

        int itemStatus = -1;

        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)connection.getResult();

            // loop through result set
            if (rs.next())
            {
                itemStatus = rs.getInt(ARTSDatabaseIfc.FIELD_ITEM_STATUS);
            }
            // close result set
            rs.close();
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "order table");
            throw new DataException(DataException.SQL_ERROR, "order table", se);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadOrderLineItemStatus.readOrderLineItemStatus()");

        return (itemStatus);
    }

    /**
     * Build SQL select statement for retrieving order status
     * 
     * @param orderIn order status object to be used as key
     * @return SQLSelectStatement to be used for reading order
     */
    public SQLSelectStatement buildSQLSelectStatement(OrderLineItemSearchCriteriaIfc transaction)
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(ARTSDatabaseIfc.TABLE_ORDER_LINE_ITEM_STATUS);

        // add select columns
        sql.addColumn(ARTSDatabaseIfc.FIELD_ITEM_STATUS);

        // add qualifiers
        sql.addQualifier(ARTSDatabaseIfc.FIELD_RETAIL_STORE_ID, inQuotes(transaction.getStoreID()));
        sql.addQualifier(ARTSDatabaseIfc.FIELD_WORKSTATION_ID, inQuotes(transaction.getWorkStationID()));
        sql.addQualifier(ARTSDatabaseIfc.FIELD_TRANSACTION_SEQUENCE_NUMBER, transaction.getTransactionSequenceNo());
        sql.addQualifier(ARTSDatabaseIfc.FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(transaction.getBusinessDay()));
        sql.addQualifier(ARTSDatabaseIfc.FIELD_LINE_ITEM_SEQUENCE_NUMBER, transaction.getLineItemSequenceNo());
        return (sql);
    }
}
