/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateConfirmedClockEntry.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:05 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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
 * $Log:
 *  5    360Commerce 1.4         5/30/2006 4:15:56 PM   Brett J. Larsen CR
 *       18490 - UDM - rework for CO_CONF_EM_TM_ENR
 *  4    360Commerce 1.3         1/25/2006 4:11:26 PM   Brett J. Larsen merge
 *       7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *  3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:22:52 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:05 PM  Robert Pearse   
 *:
 *  4    .v700     1.2.1.0     11/16/2005 16:28:31    Jason L. DeLeau 4215: Get
 *       rid of redundant ArtsDatabaseifc class
 *  3    360Commerce1.2         3/31/2005 15:28:45     Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:22:52     Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:12:05     Robert Pearse
 *
 * Revision 1.6  2004/04/09 16:55:46  cdb
 * @scr 4302 Removed double semicolon warnings.
 *
 * Revision 1.5  2004/02/17 17:57:36  bwf
 * @scr 0 Organize imports.
 *
 * Revision 1.4  2004/02/17 16:18:45  rhafernik
 * @scr 0 log4j conversion
 *
 * Revision 1.3  2004/02/12 17:13:19  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 23:25:23  bwf
 * @scr 0 Organize imports.
 *
 * Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 * updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   May 15 2003 12:09:04   dal
 * Merge from Cole
 * Resolution for 1968: Integrate Time Maintenance features from Cole
 *
 *    Rev 1.2   Jan 22 2003 16:15:40   dal
 * Merge from 5.5/KB
 * Resolution for Backoffice SCR-1582: Code merge from KB Backoffice/Domain
 *
 *    Rev 1.1   Jun 10 2002 11:14:58   epd
 * Merged in changes for Oracle
 * Resolution for Domain SCR-83: Merging database fixes into base code
 *
 *    Rev 1.2   Jun 03 2002 16:15:06   dfh
 * added log, put inQuotes around id_type for db2
 * Resolution for POS SCR-1709: (Sybase) Void of new special order is stuck in the queue.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.employee.EmployeeClockEntryIfc;
import oracle.retail.stores.domain.employee.EmployeeClockEntryPairIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * JdbcUpdateConfirmedClockEntry updates the selected clock entry pair.
 * 
 * @see oracle.retail.stores.domain.employee.EmployeeClockEntryIfc
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcUpdateConfirmedClockEntry extends JdbcDataOperation
{
    private static final long serialVersionUID = 6538043337625165563L;
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcUpdateConfirmedClockEntry.class);
    /**
        revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        Class constructor.
     */
    public JdbcUpdateConfirmedClockEntry()
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

        EmployeeClockEntryPairIfc pair =
            (EmployeeClockEntryPairIfc) action.getDataObject();
        updateClockEntry(connection, pair);

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcReadEmployeeClockEntries.execute");
    }

    /**
        Updates the selected clock entry pair.
        @param  dataConnection  a connection to the database
        @param  searchEntry search clock entry object
        @return selected clock entry object
        @exception  DataException thrown when an error occurs executing the
        SQL against the DataConnection, or when processing the ResultSet
     */
    protected void updateClockEntry(JdbcDataConnection dataConnection,
                                    EmployeeClockEntryPairIfc pair)
        throws DataException
    {
        execute(dataConnection, buildClockEntrySQLStatement(pair));
    }

    /**
        Builds and returns SQL statement for updating the selected clock entry pair.
        @param pair the pair to update
        @return SQL select statement for retrieving the latest employee clock entry
     */
    protected SQLUpdateStatement buildClockEntrySQLStatement(EmployeeClockEntryPairIfc pair)
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        EmployeeClockEntryIfc clockInEntry = pair.getClockIn();
        EmployeeClockEntryIfc clockOutEntry = pair.getClockOut();

        EmployeeClockEntryIfc entry = clockInEntry == null ? clockOutEntry : clockInEntry;

        EYSDate clockInDate = clockInEntry == null ? null : clockInEntry.getClockEntry();
        EYSDate clockOutDate = clockOutEntry == null ? null : clockOutEntry.getClockEntry();

        sql.setTable(ARTSDatabaseIfc.TABLE_EMPLOYEE_CONFIRMED_CLOCK_ENTRY);
        sql.addQualifier(ARTSDatabaseIfc.FIELD_EMPLOYEE_TIME_ENTRY_ID + " = " + pair.getPairID());

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
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_MANAGER_EDIT_CODE,
                      pair.getEditReasonCode());
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_CONF_TIME_IS_MODIFIED,
                      pair.isEdited() ? "'1'" : "'0'");

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

        return sql;
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
                                SQLUpdateStatement sql) throws DataException
    {

        ResultSet rs = null;
        String sqlString = sql.getSQLString();
        dataConnection.execute(sqlString);
        rs = (ResultSet) dataConnection.getResult();
        return rs;
    }

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
