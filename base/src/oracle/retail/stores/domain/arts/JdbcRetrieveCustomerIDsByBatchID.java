/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcRetrieveCustomerIDsByBatchID.java /main/6 2012/09/24 15:23:54 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/24/12 - Implement maximum customer record retrieval for dtm
 *                         export
 *    masahu    09/13/11 - Fortify Fix: Cannot log sensitive SQL statements
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  12/09/08 - rework of base issue
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *    mahising  11/12/08 - added for customer
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.datareplication.DataReplicationCustomerEntryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation reads a list of customer IDs from a database.
 * 
 * @version $Revision: /main/6 $
 */
public class JdbcRetrieveCustomerIDsByBatchID extends JdbcDataOperation
{
    private static final long serialVersionUID = 2833116634721334999L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/6 $";

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcRetrieveCustomerIDsByBatchID.class);

    /**
     * Class constructor.
     */
    public JdbcRetrieveCustomerIDsByBatchID()
    {
        setName("JdbcRetrieveCustomerIDsByBatchID");
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
            logger.debug("JdbcRetrieveCustomerIDsByBatchID.execute");

        DataReplicationCustomerEntryIfc[] entries = selectCustomerIDs((JdbcDataConnection) dataConnection,
                (DataReplicationCustomerEntryIfc) action.getDataObject());

        dataTransaction.setResult(entries);

        if (logger.isDebugEnabled())
            logger.debug("JdbcRetrieveCustomerIDsByBatchID.execute");
    }

    /**
     * Selects customer IDs matching input criteria.
     * 
     * @param dataConnection JDBC data connection
     * @param tLogEntry transaction TLog entry object used as search criteria
     * @return array of transaction IDs matching input criteria
     * @throws DataException thrown if error occurs
     */
    protected DataReplicationCustomerEntryIfc[] selectCustomerIDs(JdbcDataConnection dataConnection,
            DataReplicationCustomerEntryIfc tLogEntry) throws DataException
    {
      // build sql statement
        SQLSelectStatement sql = buildSelectCustomerIDsStatement(tLogEntry);
        DataReplicationCustomerEntryIfc[] entries = null;
        ResultSet rs = null;
        // execute select statement and parse result set
        try
        {
            String sqlString = sql.getSQLString();
            int maxRows = tLogEntry.getMaximumTransactionsToExport();
            if (maxRows > 0)
            {
                if (logger.isDebugEnabled()) logger.debug("Restricting maximum customers retrieved to " + maxRows);
                StringBuilder sqlMaxString = new StringBuilder(100);
                sqlMaxString.append(" SELECT ");
                sqlMaxString.append(JdbcUtilities.numberLimitingSelectPart(maxRows));
                sqlMaxString.append(" * FROM (");
                sqlMaxString.append(sqlString);
                sqlMaxString.append(")");
                sqlMaxString.append(JdbcUtilities.numberLimitingWhereClauseEndPart(maxRows));
                sqlMaxString.append(JdbcUtilities.numberLimitingEndPart(maxRows));
                sqlString = sqlMaxString.toString();
            }

            // execute sql and get result set
            dataConnection.execute(sqlString, false);
            rs = (ResultSet) dataConnection.getResult();
            // parse result set
            entries = parseSelectCustomerIDsResultSet(rs);
        }
        catch (DataException de)
        {
            // The NO_DATA condition is very common here; if the Daemon calling
            // this class has a short sleep interval, it will flood the
            // log with messages. Setting this to info gives the system
            // implementor the option of removing it from log using
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
        catch (SQLException se)
        {
            logger.error(Util.throwableToString(se));
            dataConnection.logSQLException(se, "select customer IDs");
            throw new DataException(DataException.SQL_ERROR, "select customer IDs", se);
        }
        catch (Exception e)
        {
            logger.error(Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN, "customer table", e);
        }
        finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (SQLException se)
                {
                    throw new DataException(DataException.SQL_ERROR, "customer table", se);
                }
            }
        }
        return (entries);
    }

    /**
     * Builds select statement for retrieving customer IDs.
     * 
     * @param tLogEntry customer TLog entry object used as search criteria
     * @return SQLSelectStatement to be used for retrieving customer IDs.
     */
    protected SQLSelectStatement buildSelectCustomerIDsStatement(DataReplicationCustomerEntryIfc tLogEntry)
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // set table
        sql.addTable(ARTSDatabaseIfc.TABLE_CUSTOMER);
        sql.addColumn(ARTSDatabaseIfc.FIELD_CUSTOMER_ID);
        sql.addQualifier(ARTSDatabaseIfc.FIELD_CUSTOMER_TLOG_BATCH_IDENTIFIER, tLogEntry.getBatchID());

        if (logger.isDebugEnabled())
        {
            String strSQL = "";
            try
            {
                strSQL = sql.getSQLString();
            }
            catch (Exception e)
            {
                strSQL = "Exception Message: " + e.getLocalizedMessage();
            }
            logger.debug("buildSelectCustomerIDsStatement - SQL for Customer ID retrieval: " + strSQL);
        }
        return (sql);
    }

    /**
     * Parses ResultSet and returns list of entries. If the ResultSet is empty,
     * then a DataException will be thrown stating &quot;No transactions found
     * matching search criteria.&quot;
     * 
     * @param rs result set
     * @return list of DataReplicationCustomerEntryIfc entries
     * @exception SQLException thrown if SQL error occurs
     * @exception DataException if <code>rs</code> is empty or if there is an
     *                error retrieving EYSDate from string.
     * @see JdbcDataOperation#getEYSDateFromString(ResultSet, int)
     */
    protected DataReplicationCustomerEntryIfc[] parseSelectCustomerIDsResultSet(ResultSet rs) throws SQLException,
            DataException
    {
        ArrayList<DataReplicationCustomerEntryIfc> entryList = new ArrayList<DataReplicationCustomerEntryIfc>();
        DataReplicationCustomerEntryIfc entry = null;
        int index = 0;

        while (rs.next())
        {
            index = 0;
            // instantiate entry and set values
            entry = DomainGateway.getFactory().getDataReplicationCustomerEntry();

            // set customer id into entity
            entry.setCustomerID(getSafeString(rs, ++index));

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