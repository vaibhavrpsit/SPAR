/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcInsertWorkWeek.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
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

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * JdbcInsertWorkWeek inserts a new Work Week start date into the DB.
 * 
 * @see oracle.retail.stores.domain.employee.EmployeeClockEntryIfc
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcInsertWorkWeek extends JdbcDataOperation
{
    private static final long serialVersionUID = 7918244535098600126L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcInsertWorkWeek.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Class constructor.
     */
    public JdbcInsertWorkWeek()
    {
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcInsertWorkWeek.execute");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        EYSDate weekEnd = (EYSDate) action.getDataObject();
        insertWorkWeek(connection, weekEnd);
        
        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcInsertWorkWeek.execute");
    }

    /**
     * Insert a work week into the DB
     * 
     * @param dataConnection the dataConnection
     * @param date the EYSDate representing the start of the week
     * @exception DataException
     */
    protected void insertWorkWeek(JdbcDataConnection dataConnection, EYSDate date)
        throws DataException
    {
        // build SQL statement, execute and parse
        SQLInsertStatement sql = buildInsertWorkWeekSQLStatement(date);

        try
        {
            execute(dataConnection, sql);
        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN,
                                    "Employee clock entry lookup", e);
        }
    }

    /**
     * Build the SQL Insert statement to insert the week.
     * 
     * @param date the date to start the week
     */
    protected SQLInsertStatement buildInsertWorkWeekSQLStatement(EYSDate date)
    {
        SQLInsertStatement sql = new SQLInsertStatement();
        sql.setTable(ARTSDatabaseIfc.TABLE_WORK_WEEK);
        sql.addColumn(ARTSDatabaseIfc.FIELD_WORK_WEEK_WEEK_START,
                      dateToSQLTimestampString(date));
        sql.addColumn(ARTSDatabaseIfc.FIELD_WORK_WEEK_WEEK_CONFIRMED, "'0'");
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_CREATION_TIMESTAMP,
                      dateToSQLTimestampString(date));
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      dateToSQLTimestampString(date));
        return sql;
    }

    /**
     * Executes the SQL Statement.
     * 
     * @param dataConnection a connection to the database
     * @param sql the SQl statement
     * @return result set
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected ResultSet execute(JdbcDataConnection dataConnection,
            SQLInsertStatement sql) throws DataException
    {
        ResultSet rs = null;
        String sqlString = sql.getSQLString();

        dataConnection.execute(sqlString);
        return rs;
    }
}
