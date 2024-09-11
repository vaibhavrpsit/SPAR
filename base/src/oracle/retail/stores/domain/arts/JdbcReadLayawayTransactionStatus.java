/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadLayawayTransactionStatus.java /main/9 2012/05/21 15:50:18 cgreene Exp $
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
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  02/18/09 - Fixed db2 query issue
 *    mchellap  11/14/08 - Code review javadoc changes
 *    mchellap  11/13/08 - Inventory Reservation Module
 *    mchellap  11/12/08 - Jdbc class to read layaway status
 *    mchellap  11/07/08 - Jdbc Class to read order line item status
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
 * This operation reads layaway transaction status for a given layaway transaction.
 */
public class JdbcReadLayawayTransactionStatus extends JdbcDataOperation
{
    private static final long serialVersionUID = -7527555213693220275L;

    /** The logger to which log messages will be sent. */
    private static final Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcReadLayawayTransactionStatus.class);

    /**
     * Class constructor.
     */
    public JdbcReadLayawayTransactionStatus()
    {
        setName("JdbcReadLayawayTransactionStatus");
    }

    /**
     * Executes the SQL statements against the database.
     *
     * @param  dataTransaction     The data transaction
     * @param  dataConnection      The connection to the data source
     * @param  action              The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug("JdbcReadLayawayTransactionStatus.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        try
        {
            // Get the transaction
            OrderLineItemSearchCriteriaIfc transaction = (OrderLineItemSearchCriteriaIfc) action.getDataObject();
            ArrayList<Integer> status = readLayawayTransactionStatus(connection, transaction);
            dataTransaction.setResult(status);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            logger.error(Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN,
                                    "read layaway status");
        }

        if (logger.isDebugEnabled()) logger.debug("JdbcReadLayawayTransactionStatus.execute");
    }

    /**
     * Reads the layaway transaction status.
     *
     * @param  dataConnection  connection to the db
     * @param  transaction  The layaway transaction
     * @return ArrayList The layaway status
     * @exception DataException upon error
     */
    public ArrayList<Integer> readLayawayTransactionStatus(JdbcDataConnection connection,
            OrderLineItemSearchCriteriaIfc transaction) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcReadLayawayTransactionStatus.readLayawayTransactionStatus()");

        // build SQL statement
        SQLSelectStatement sql = buildSQLSelectStatement(transaction);

        ArrayList<Integer> layawayStatus = new ArrayList<Integer>();

        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();

            // loop through result set
            if (rs.next())
            {                           // begin loop through result set
                layawayStatus.add(rs.getInt(ARTSDatabaseIfc.FIELD_LAYAWAY_STATUS));
                layawayStatus.add(rs.getInt(ARTSDatabaseIfc.FIELD_LAYAWAY_PREVIOUS_STATUS));
            } // end loop through result set
            // close result set
            rs.close();
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "layaway status table");
            throw new DataException(DataException.SQL_ERROR, "layaway status table", se);
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcReadLayawayTransactionStatus.readLayawayTransactionStatus()");

        return(layawayStatus);
    }

    /**
     * Build SQL select statement for retrieving layaway status 
     *
     * @param OrderLineItemSearchCriteriaIfc The search criteria
     * @return SQLSelectStatement to be used for reading layaway status
     */
    public SQLSelectStatement buildSQLSelectStatement(OrderLineItemSearchCriteriaIfc transaction)
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(ARTSDatabaseIfc.TABLE_RETAIL_TRANSACTION_LAYAWAY_STATUS);

        // add select columns
        sql.addColumn(ARTSDatabaseIfc.FIELD_LAYAWAY_STATUS);
        sql.addColumn(ARTSDatabaseIfc.FIELD_LAYAWAY_PREVIOUS_STATUS);

        // add qualifiers
        sql.addQualifier(ARTSDatabaseIfc.FIELD_RETAIL_STORE_ID, inQuotes(transaction.getStoreID()));
        sql.addQualifier(ARTSDatabaseIfc.FIELD_WORKSTATION_ID, inQuotes(transaction.getWorkStationID()));
        sql.addQualifier(ARTSDatabaseIfc.FIELD_TRANSACTION_SEQUENCE_NUMBER, transaction.getTransactionSequenceNo());
        sql.addQualifier(ARTSDatabaseIfc.FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(transaction.getBusinessDay()));
        sql.addQualifier(ARTSDatabaseIfc.FIELD_LAYAWAY_ID, inQuotes(transaction.getLayawayID()));

        return(sql);
    }
}
