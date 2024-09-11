/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadWorkWeek.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:03 mszekely Exp $
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
 *    cgreene   04/15/10 - remove deprecate string-to-timestamp methods
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
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * JdbcReadWorkWeek retrieves the start date for the current work week.
 * 
 * @see oracle.retail.stores.domain.employee.EmployeeClockEntryIfc
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcReadWorkWeek extends JdbcDataOperation
{
    private static final long serialVersionUID = 8373682839879970793L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadWorkWeek.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Class constructor.
     */
    public JdbcReadWorkWeek()
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
                     "JdbcReadWorkWeek.execute");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        EYSDate entries = readWorkWeek(connection);
        dataTransaction.setResult(entries);

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcReadWorkWeek.execute");
    }

    /**
     * Gets the date of the start of the current work week
     * 
     * @param dataConnection a connection to the database
     * @return the start of the work week
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected EYSDate readWorkWeek(JdbcDataConnection dataConnection)
        throws DataException
    {
        // build SQL statement, execute and parse
        SQLSelectStatement sql = buildWorkWeekSQLStatement();
        EYSDate date;

        try
        {
            ResultSet rs = execute(dataConnection, sql);
            date = parseWorkWeekResultSet(rs);
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

        return(date);
    }

    /**
     * Builds and returns SQL statement for retrieving the start of the work
     * week.
     * 
     * @return SQL select statement for retrieving the latest employee clock
     *         entry
     */
    protected SQLSelectStatement buildWorkWeekSQLStatement()
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // add tables
        sql.addTable(ARTSDatabaseIfc.TABLE_WORK_WEEK,
                     ARTSDatabaseIfc.ALIAS_WORK_WEEK);

        // add select columns
        addWorkWeekSelectColumns(sql);
        sql.addQualifier(ARTSDatabaseIfc.FIELD_WORK_WEEK_WEEK_CONFIRMED, "'0'");

        return(sql);
    }

    /**
     * Adds select columns for work week entry lookup to SQL statement.
     * 
     * @param sql SQLSelectStatement object
     */
    protected void addWorkWeekSelectColumns(SQLSelectStatement sql)
    {
        sql.addColumn(ARTSDatabaseIfc.FIELD_WORK_WEEK_WEEK_START);
    }

    /**
     * Parse clock entry result set and returns the EYSDate.
     * 
     * @param rs ResultSet object
     * @return EYSDate the date
     * @exception SQLException thrown if error SQL error occurs while parsing
     *                result set
     * @exception DataException thrown if error other error occurs while parsing
     *                result set
     */
    protected EYSDate parseWorkWeekResultSet(ResultSet rs)
        throws DataException, SQLException
    {
        EYSDate date = null;

        try
        {
            if (rs.next())
            {
                date = new EYSDate(rs.getDate(1));
            }
            rs.close();
        }

        catch (SQLException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN,
                                    "Last employee clock entry lookup", e);
        }
        if (date == null)
        {
            throw new DataException(DataException.NO_DATA,
                                    "No clock entry was found processing the result set in JdbcReadWorkWeek.");
        }

        return(date);
    }

    /**
     * Executes the SQL Statement.
     * 
     * @param dataConnection a connection to the database
     * @param sql the SQl statement
     * @param int id comparison basis type
     * @return result set
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
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
