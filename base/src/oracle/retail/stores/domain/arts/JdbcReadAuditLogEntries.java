/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadAuditLogEntries.java /main/16 2013/09/05 10:36:19 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
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
import oracle.retail.stores.domain.audit.AuditEntryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
    JdbcReadAuditLogEntries retrieves the audit log entries from the DB.
    @see oracle.retail.stores.domain.employee.EmployeeClockEntryPairIfc
    @version $Revision: /main/16 $
**/
public class JdbcReadAuditLogEntries
    extends JdbcDataOperation
{
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadAuditLogEntries.class);
    /**
        revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
        Class constructor.
     */
    public JdbcReadAuditLogEntries()
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
                     "JdbcReadAuditLogEntries.execute");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        AuditEntryIfc[] entries = readAuditLogEntries(connection, ((Integer) action.getDataObject()).intValue());
        dataTransaction.setResult(entries);

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcReadAuditLogEntries.execute");
    }

    /**
        Reads the Audit Entries from the DB.
        @param  dataConnection  a connection to the database
        @return an array of the audit entries
        @exception  DataException thrown when an error occurs executing the
        SQL against the DataConnection, or when processing the ResultSet
     */
    protected AuditEntryIfc[] readAuditLogEntries(JdbcDataConnection dataConnection, int typeCode)
        throws DataException
    {
        // build SQL statement, execute and parse
        SQLSelectStatement sql = buildAuditEntryIDSQLStatement(typeCode);
        Vector v = new Vector(2);
        AuditEntryIfc[] entries;

        try
        {
            ResultSet rs = execute(dataConnection, sql);

            while (rs.next())
            {
                int index = 0;
                AuditEntryIfc entry = DomainGateway.getFactory().getAuditEntryInstance();

                entry.setEntryID(rs.getInt(++index));
                entry.setFieldName(getSafeString(rs, ++index));

                String fieldName = entry.getFieldName();

                entry.setReasonCode(rs.getInt(++index));
                entry.setStoreID(getSafeString(rs, ++index));
                entry.setUserID(Integer.parseInt(getSafeString(rs, ++index)));

                // if the entry is a Time entry, set the value as an EYSDate
                
                if (fieldName.compareTo("TimeIn") == 0 || fieldName.compareTo("TimeOut") == 0)
                {
                    entry.setOldValue(getEYSDateTimeFromString(getSafeString(rs, ++index)));
                    entry.setNewValue(getEYSDateTimeFromString(getSafeString(rs, ++index)));
                }
                else
                {
                    entry.setOldValue(getSafeString(rs, ++index));
                    entry.setNewValue(getSafeString(rs, ++index));
                }

                entry.setObjectID("" + rs.getInt(++index));
                entry.setDateCreated(getEYSDateTimeFromString(getSafeString(rs, ++index)));

                v.addElement(entry);
            }

            entries = new AuditEntryIfc[v.size()];
            int i = 0;

            Enumeration e = v.elements();
            while (e.hasMoreElements())
            {
                entries[i++] = (AuditEntryIfc) e.nextElement();
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

        return(entries);
    }

    /**
        Builds and returns SQL statement for retrieving the clock entry pairs
        @return SQL select statement for retrieving the clock entry pairs
     */
    protected SQLSelectStatement buildAuditEntryIDSQLStatement(int typeCode)
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // add tables
        sql.addTable(ARTSDatabaseIfc.TABLE_AUDIT_LOG);

        // add select columns
        sql.addColumn(ARTSDatabaseIfc.FIELD_AUDIT_LOG_ENTRY_ID);
        sql.addColumn(ARTSDatabaseIfc.FIELD_RETAIL_STORE_ID);
        sql.addColumn(ARTSDatabaseIfc.FIELD_AUDIT_LOG_FIELD_NAME);
        sql.addColumn(ARTSDatabaseIfc.FIELD_AUDIT_LOG_REASON_CODE);
        sql.addColumn(ARTSDatabaseIfc.FIELD_AUDIT_LOG_EMPLOYEE_ID);
        sql.addColumn(ARTSDatabaseIfc.FIELD_AUDIT_LOG_OLD_VALUE);
        sql.addColumn(ARTSDatabaseIfc.FIELD_AUDIT_LOG_NEW_VALUE);
        sql.addColumn(ARTSDatabaseIfc.FIELD_AUDIT_LOG_OBJECT_ID);
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_CREATION_TIMESTAMP);

        sql.addQualifier(ARTSDatabaseIfc.FIELD_AUDIT_LOG_ENTRY_TYPE + " = " + typeCode);

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

    /**
        Generate an EYSDate object from a string.
        @param str the string to convert
        @return an EYSDate of the string
     */
    public EYSDate getEYSDateTimeFromString(String str)
    {
        EYSDate date = new EYSDate();

        if (str == null || str.length() == 0 || str.compareTo("Empty") == 0)
            return null;

        // parse String into date format
        String year = str.substring(0, 4);
        String month = str.substring(5, 7);
        String day = str.substring(8, 10);
        String hour = str.substring(11, 13);
        String minute = str.substring(14, 16);
        String second = str.substring(17, 19);

        date.setYear(Integer.parseInt(year));
        date.setMonth(Integer.parseInt(month));
        date.setDay(Integer.parseInt(day));
        date.setHour(Integer.parseInt(hour));
        date.setMinute(Integer.parseInt(minute));
        date.setSecond(Integer.parseInt(second));
        date.setType(EYSDate.TYPE_DATE_TIME);

        return date;
    }
}
