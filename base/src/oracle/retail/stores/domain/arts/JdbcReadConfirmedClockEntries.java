/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadConfirmedClockEntries.java /main/15 2013/09/05 10:36:19 abondala Exp $
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
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeClockEntryIfc;
import oracle.retail.stores.domain.employee.EmployeeClockEntryPair;
import oracle.retail.stores.domain.employee.EmployeeClockEntryPairIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
    JdbcReadConfirmedClockEntries retrieves the confirmed clock entry pairs from the DB.
    @see oracle.retail.stores.domain.employee.EmployeeClockEntryPairIfc
    @version $Revision: /main/15 $
**/
public class JdbcReadConfirmedClockEntries
extends JdbcDataOperation
{
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadConfirmedClockEntries.class);
    /**
        revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
        Class constructor.
     */
    public JdbcReadConfirmedClockEntries()
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
                     "JdbcReadEmployeeClockEntries.execute");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        EmployeeClockEntryPairIfc[] entries = readEmployeeClockEntries(connection);
        dataTransaction.setResult(entries);

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcReadEmployeeClockEntries.execute");
    }

    /**
        Reads the Clock Entry pairs from the DB.
        @param  dataConnection  a connection to the database
        @return an array of the clock entry pairs
        @exception  DataException thrown when an error occurs executing the
        SQL against the DataConnection, or when processing the ResultSet
     */
    protected EmployeeClockEntryPairIfc[] readEmployeeClockEntries(JdbcDataConnection dataConnection)
        throws DataException
    {
        // build SQL statement, execute and parse
        SQLSelectStatement sql = buildClockEntrySQLStatement();
        EmployeeClockEntryPairIfc[] entries;

        try
        {
            ResultSet rs = execute(dataConnection, sql);
            entries = parseEmployeeClockEntryResultSet(rs);
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

        return(entries);
    }

    /**
        Builds and returns SQL statement for retrieving the clock entry pairs
        @return SQL select statement for retrieving the clock entry pairs
     */
    protected SQLSelectStatement buildClockEntrySQLStatement()
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // add tables
        sql.addTable(ARTSDatabaseIfc.TABLE_EMPLOYEE_CONFIRMED_CLOCK_ENTRY,
                     ARTSDatabaseIfc.ALIAS_EMPLOYEE_CONFIRMED_CLOCK_ENTRY);

        // add select columns
        addEmployeeClockEntrySelectColumns(sql);

        return(sql);
    }

    /**
        Adds select columns for employee clock entry lookup to SQL statement. <P>
        @param sql SQLSelectStatement object
     */
    protected void addEmployeeClockEntrySelectColumns(SQLSelectStatement sql)
    {
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_TIME_ENTRY_ID);
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_ID);
        sql.addColumn(ARTSDatabaseIfc.FIELD_RETAIL_STORE_ID);
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_ENTRY_IN);
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_ENTRY_OUT);
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_MANAGER_ID);
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_MANAGER_EDIT_CODE);

        // read the modification flag.
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_IS_MODIFIED);

        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_TIME_TYPE);
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_CONFIRMED);
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP);
        sql.addQualifier(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_IS_DELETED +" = '0'" );
    }

    /**
        Parse clock entry result set and returns clock entry pair array.
        @param rs ResultSet object
        @return EmployeeClockEntryPairIfc[] array
        @exception SQLException thrown if error SQL error occurs while parsing result set
        @exception DataException thrown if error other error occurs while parsing result set
     */
    protected EmployeeClockEntryPairIfc[] parseEmployeeClockEntryResultSet(ResultSet rs)
        throws DataException, SQLException
    {
        Vector v = new Vector(4);

        EmployeeClockEntryPairIfc clockEntryPair = null;

        EmployeeClockEntryIfc clockIn = null;
        EmployeeClockEntryIfc clockOut = null;

        EmployeeIfc employee = null;

        try
        {
            while (rs.next())
            {
                int index = 0;
                // initialize objects
                employee = DomainGateway.getFactory().getEmployeeInstance();
                clockEntryPair = new EmployeeClockEntryPair();
                clockIn = DomainGateway.getFactory().getEmployeeClockEntryInstance();
                clockOut = DomainGateway.getFactory().getEmployeeClockEntryInstance();

                clockIn.setEmployee(employee);
                clockOut.setEmployee(employee);

                int partyID = Integer.parseInt(getSafeString(rs, ++index));
                clockEntryPair.setPairID(partyID);

                String employeeID = getSafeString(rs, ++index);
                clockIn.getEmployee().setEmployeeID(employeeID);
                clockOut.getEmployee().setEmployeeID(employeeID);

                String storeID = getSafeString(rs, ++index);
                clockIn.setStoreID(storeID);
                clockOut.setStoreID(storeID);

                String str = getSafeString(rs, ++index);
                clockIn.setClockEntry(getEYSDateTimeFromString(str));

                str = getSafeString(rs, ++index);
                clockOut.setClockEntry(getEYSDateTimeFromString(str));

                clockEntryPair.setManagerID(getSafeString(rs, ++index));
                clockEntryPair.setEditReasonCode(Integer.parseInt(getSafeString(rs, ++index)));

                // set the modification flag.
                clockEntryPair.setEdited(getBooleanFromString(rs, ++index));

                clockEntryPair.setTimeType(Integer.parseInt(getSafeString(rs, ++index)));
                clockEntryPair.setConfirmed(getBooleanFromString(rs, ++index));

                str = getSafeString(rs, ++index);
                clockEntryPair.setModifiedDate(getEYSDateTimeFromString(str));

                clockEntryPair.setClockIn(clockIn);
                clockEntryPair.setClockOut(clockOut);

                clockEntryPair.setReadFromDB(true);

                v.addElement(clockEntryPair);
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

        EmployeeClockEntryPairIfc[] pairs = new EmployeeClockEntryPairIfc[v.size()];

        Enumeration e = v.elements();
        int i = 0;
        while (e.hasMoreElements())
        {
            pairs[i++] = (EmployeeClockEntryPairIfc) e.nextElement();
        }

        return(pairs);

    }

    public EYSDate getEYSDateTimeFromString(String str)
    {
        EYSDate date = null;
        if (str != null && str.length() > 0)
        {
            date = new EYSDate();

            // parse String into date format
            String year;
            String month;
            String day;
            String hour;
            String minute;
            String second;

            if (str.length() < 15)
            {
                year = str.substring(0, 4);
                month = str.substring(4, 6);
                day = str.substring(6, 8);
                hour = str.substring(8, 10);
                minute = str.substring(10, 12);
                second = str.substring(12, 14);
            }
            else
            {
                // parse String into date format
                year = str.substring(0, 4);
                month = str.substring(5, 7);
                day = str.substring(8, 10);
                hour = str.substring(11, 13);
                minute = str.substring(14, 16);
                second = str.substring(17, 19);
            }

            date.setYear(Integer.parseInt(year));
            date.setMonth(Integer.parseInt(month));
            date.setDay(Integer.parseInt(day));
            date.setHour(Integer.parseInt(hour));
            date.setMinute(Integer.parseInt(minute));
            date.setSecond(Integer.parseInt(second));
            date.setType(EYSDate.TYPE_DATE_TIME);
        }

        return date;
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
