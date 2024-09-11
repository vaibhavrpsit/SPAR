/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadLastClockEntryPairID.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;


import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
    JdbcReadConfirmedClockEntries retrieves the confirmed clock entry pairs from the DB.
    @see oracle.retail.stores.domain.employee.EmployeeClockEntryPairIfc
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
public class JdbcReadLastClockEntryPairID
extends JdbcDataOperation
{
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadLastClockEntryPairID.class);
    /**
        revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        Class constructor.
     */
    public JdbcReadLastClockEntryPairID()
    {
    }

    /**
        Executes the SQL statements against the database. <P>
        @param  dataTransaction     The data transaction
        @param  dataConnection      The connection to the data source
        @param  action              The information passed by the valet
        @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcReadLastClockEntryPairID.execute");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        int lastPairID = readLastPairID(connection);
        dataTransaction.setResult(Integer.valueOf(lastPairID));

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcReadLastClockEntryPairID.execute");
    }

    /**
        Reads the Clock Entry pairs from the DB.
        @param  dataConnection  a connection to the database
        @return an array of the clock entry pairs
        @exception  DataException thrown when an error occurs executing the
        SQL against the DataConnection, or when processing the ResultSet
     */
    protected int readLastPairID(JdbcDataConnection dataConnection)
        throws DataException
    {
        // build SQL statement, execute and parse
        SQLSelectStatement sql = buildClockEntryPairIDSQLStatement();
        int lastPairID = -1;

        try
        {
            ResultSet rs = execute(dataConnection, sql);

            while (rs.next())
            {
                int pairID = rs.getInt(1);
                
                if (pairID > lastPairID)
                {
                    lastPairID = pairID;
                }
            }
        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se,
                                           "Employee clock entry lookup");
            throw new DataException(DataException.SQL_ERROR,
                                    "Employee clock entry lookup",
                                    se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN,
                                    "Employee clock entry lookup", e);
        }

        return(lastPairID);
    }

    /**
        Builds and returns SQL statement for retrieving the clock entry pairs
        @return SQL select statement for retrieving the clock entry pairs
     */
    protected SQLSelectStatement buildClockEntryPairIDSQLStatement()
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // add tables
        sql.addTable(ARTSDatabaseIfc.TABLE_EMPLOYEE_CONFIRMED_CLOCK_ENTRY);

        // add select columns
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_TIME_ENTRY_ID);

        return(sql);
    }

    /**
        Executes the SQL Statement.<P>
        @param  dataConnection  a connection to the database
        @param  sql  the SQl statement
        @return result set
        @exception  DataException thrown when an error occurs executing the
                                  SQL against the DataConnection, or when
                                  processing the ResultSet
     */
    protected ResultSet execute(JdbcDataConnection dataConnection,
            SQLSelectStatement sql) throws DataException
    {

        ResultSet rs;
        String sqlString = sql.getSQLString();
        dataConnection.execute(sqlString);
        rs = (ResultSet) dataConnection.getResult();
        return rs;
    }

}
