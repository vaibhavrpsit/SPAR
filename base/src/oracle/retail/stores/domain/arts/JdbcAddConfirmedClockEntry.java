/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcAddConfirmedClockEntry.java /main/14 2012/05/17 13:18:29 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  05/17/12 - Fortify: fix redundant null checks, part 6
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
import oracle.retail.stores.domain.employee.EmployeeClockEntryIfc;
import oracle.retail.stores.domain.employee.EmployeeClockEntryPairIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * JdbcAddConfirmedClockEntry adds a Confirmed Clock Entry pair to the DB.
 * 
 * @see oracle.retail.stores.domain.employee.EmployeeClockEntryPairIfc
 * @version $Revision: /main/14 $
 */
public class JdbcAddConfirmedClockEntry extends JdbcDataOperation
{
    private static final long serialVersionUID = 2074803400977242839L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcAddConfirmedClockEntry.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * Class constructor.
     */
    public JdbcAddConfirmedClockEntry()
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
                     "JdbcReadEmployeeClockEntries.execute");
        
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        
        EmployeeClockEntryPairIfc pair =
            (EmployeeClockEntryPairIfc) action.getDataObject();
        addClockEntry(connection, pair);
        
        if (logger.isDebugEnabled()) logger.debug(
            "JdbcReadEmployeeClockEntries.execute");
    }

    /**
     * Adds the clock entry pair to the DB.
     * 
     * @param dataConnection a connection to the database
     * @param pair the clock entry pair
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void addClockEntry(JdbcDataConnection dataConnection,
                                 EmployeeClockEntryPairIfc pair)
        throws DataException
    {
        execute(dataConnection, buildClockEntrySQLStatement(pair));
    }

    /**
     * Builds and returns SQL statement for adding the employee clock entry.
     * 
     * @param pair the pair to add
     * @return SQL select statement for retrieving the latest employee clock
     *         entry
     */
    protected SQLInsertStatement buildClockEntrySQLStatement(EmployeeClockEntryPairIfc pair) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();
        // add tables
        sql.setTable(ARTSDatabaseIfc.TABLE_EMPLOYEE_CONFIRMED_CLOCK_ENTRY);

        EmployeeClockEntryIfc clockInEntry = pair.getClockIn();
        EmployeeClockEntryIfc clockOutEntry = pair.getClockOut();

        EmployeeClockEntryIfc entry = (clockInEntry == null)? clockOutEntry : clockInEntry;
        if (entry == null)
        {
            throw new DataException("No EmployeeClockEntry was provided to insert.");
        }

        String employeeId = (entry.getEmployee() == null)? "" : entry.getEmployee().getEmployeeID();
        EYSDate clockInDate = (clockInEntry == null)? null : clockInEntry.getClockEntry();
        EYSDate clockOutDate = (clockOutEntry == null)? null : clockOutEntry.getClockEntry();

        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_TIME_ENTRY_ID,
                      pair.getPairID());
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_ID,
                      inQuotes(employeeId));
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_MANAGER_ID,
                      inQuotes(pair.getManagerID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_RETAIL_STORE_ID,
                      inQuotes(entry.getStoreID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_ENTRY_IN,
                      clockInDate == null ? null :
                      dateToSQLTimestampString(clockInDate));
        
        // add the display string for time In
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_ENTRY_IN_STR,
                      clockInDate == null ? "'??:??'" :
                      inQuotes(dateToCrystalDisplayString(clockInDate)));

        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_ENTRY_OUT,
                      clockOutDate == null ? null :
                      dateToSQLTimestampString(clockOutDate));

        // add the display string for time Out
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_ENTRY_OUT_STR,
                      clockOutDate == null ? "'??:??'" :
                      inQuotes(dateToCrystalDisplayString(clockOutDate)));

        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_TIME_TYPE,
                      inQuotes(pair.getTimeType()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_CONFIRMED,
                      pair.isConfirmed() ? "'1'" : "'0'");
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_IS_MODIFIED,
                      pair.isEdited() ? "'1'" : "'0'");
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_IS_DELETED,
                      pair.isRemoved() ? "'1'" : "'0'");
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_MANAGER_EDIT_CODE,
                      pair.getEditReasonCode());
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_CREATION_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());

        // determine which column gets the hours for this entry, since we can only have Other OR Regular hours for one
        // entry
        String hoursColumnName = "";
        String emptyHoursColumnName = "";
        if (pair.getTimeType() == 0)
        {
            hoursColumnName = ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_ENTRY_HOURS_STR;
            emptyHoursColumnName = ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_ENTRY_OTHER_HOURS_STR;
        }
        else
        {
            hoursColumnName = ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_ENTRY_OTHER_HOURS_STR;
            emptyHoursColumnName = ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_ENTRY_HOURS_STR;
        }

        sql.addColumn(hoursColumnName,
                      (clockInDate == null || clockOutDate == null) ? "'??:??'" :
                      getCrystalHoursDisplayString(clockInDate, clockOutDate));
        sql.addColumn(emptyHoursColumnName, "'0:00'");

        return (sql);
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
        rs = (ResultSet) dataConnection.getResult();
        return rs;
    }

    /**
     * Convert an EYSDate to a display string (am/pm).
     * 
     * @param date the date/time
     */
    public String dateToCrystalDisplayString(EYSDate date)
    {
        int hours = date.getHour();
        int minutes = date.getMinute();
        
        String ampm = " am";

        if (hours >= 12)
        {
            ampm = " pm";

            if (hours > 12)
            {
                hours -= 12;
            }
        }

        if (hours == 0)
        {
            hours = 12;
        }

        String hourString = (hours < 10 ? "0" : "") + hours;
        String minuteString = (minutes < 10 ? "0" : "") + minutes;

        return hourString + ":" + minuteString + ampm;
    }

    
    /**
        Determine the duration of a clock entry given a clock In and a clock Out to write a display string.
        @param clockIn clock In date
        @param clockOut clock Out date
     */
    public String getCrystalHoursDisplayString(EYSDate clockIn, EYSDate clockOut)
    {
        int diffHours = 0;
        int diffMinutes = 0;

        int inHours = clockIn.getHour();
        int inMinutes = clockIn.getMinute();
        
        int outHours = clockOut.getHour();
        int outMinutes = clockOut.getMinute();

        diffHours = outHours - inHours;
        diffMinutes = outMinutes - inMinutes;

        while (diffMinutes > 59)
        {
            diffMinutes -= 60;
            diffHours++;
        }

        if (diffMinutes < 0)
        {
            diffMinutes = 60 + diffMinutes;
            diffHours--;
        }

        String minutesString = (diffMinutes < 10 ? "0" : "") + diffMinutes;

        return "'" + diffHours + ":" + minutesString + "'";
    }
}
