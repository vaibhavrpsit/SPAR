/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadEmployeeClockEntries.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:55 mszekely Exp $
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
 *    mdecama   11/05/08 - I18N Reason Code - Refactored the EmployeeClockEntry
 *                         reason field.
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:15 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:40 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:43 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:58 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:02    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:40     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:43     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:58     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:25  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Oct 16 2003 13:26:06   cdb
 * Cannot use time stamp handling for "timestamp" fields.
 * Resolution for 2368: Unable to access Time Maintenance after a clock in/out.
 *
 *    Rev 1.0   Aug 29 2003 15:31:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:37:12   msg
 * Initial revision.
 *
 *    Rev 1.1   30 May 2002 07:00:54   dal
 * Added last modified data member to EmployeeClockEntryIfc
 * Resolution for Backoffice SCR-908: Calculating Hour method is not accurate at Employee Edit Hours screen
 *
 *    Rev 1.0   15 May 2002 08:28:52   dal
 * Initial revision.
 * Resolution for Backoffice SCR-867: Time Maintenance Initial checkin
 *
 *    Rev 1.1   Mar 18 2002 22:45:38   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:05:36   msg
 * Initial revision.
 *
 *    Rev 1.0   28 Oct 2001 11:45:14   mpm
 * Initial revision.
 * Resolution for POS SCR-235: Employee clock-in, clock-out
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Stack;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeClockEntryIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * JdbcReadEmployeeLastClockEntry retrieves all of the clock entries for a given
 * employee and store.
 *
 * @see oracle.retail.stores.domain.employee.EmployeeClockEntryIfc
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcReadEmployeeClockEntries extends JdbcDataOperation
{
    /**
     * Generated Serial Version UID
     */
    private static final long serialVersionUID = -8549049733735688541L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadEmployeeClockEntries.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Class constructor.
     */
    public JdbcReadEmployeeClockEntries()
    {
    }

    /**
     * Executes the SQL statements against the database.
     * <P>
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
            logger.debug("JdbcReadEmployeeClockEntries.execute");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // look up the item
        EmployeeClockEntryIfc searchEntry = (EmployeeClockEntryIfc)action.getDataObject();
        Stack entries = readEmployeeClockEntries(connection, searchEntry);
        dataTransaction.setResult(entries);

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadEmployeeClockEntries.execute");
    }

    /**
     * Selects the latest clock entry from the employee clock entry table
     *
     * @param dataConnection a connection to the database
     * @param searchEntry search clock entry object
     * @return all of the entries
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected Stack readEmployeeClockEntries(JdbcDataConnection dataConnection, EmployeeClockEntryIfc searchEntry)
            throws DataException
    {
        Stack entries = null;

        // build SQL statement, execute and parse
        SQLSelectStatement sql = buildClockEntrySQLStatement(searchEntry);
        try
        {
            ResultSet rs = execute(dataConnection, sql);
            entries = parseEmployeeClockEntryResultSet(rs);
            readLocalizedReasonCode (dataConnection, entries);
        }
        catch (DataException de)
        {
            logger.warn("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "Employee clock entry lookup");
            throw new DataException(DataException.SQL_ERROR, "Employee clock entry lookup", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Employee clock entry lookup", e);
        }

        return (entries);
    }

    /**
     * Builds and returns SQL statement for retrieving all of the employee clock
     * entries
     *
     * @param searchEntry EmployeeClockEntryIfc object containing search
     *            criteria
     * @return SQL select statement for retrieving the latest employee clock
     *         entry
     */
    protected SQLSelectStatement buildClockEntrySQLStatement(EmployeeClockEntryIfc searchEntry)
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // add tables
        sql.addTable(ARTSDatabaseIfc.TABLE_EMPLOYEE, ARTSDatabaseIfc.ALIAS_EMPLOYEE);
        sql.addTable(ARTSDatabaseIfc.TABLE_EMPLOYEE_CLOCK_ENTRY, ARTSDatabaseIfc.ALIAS_EMPLOYEE_CLOCK_ENTRY);
        // add select columns
        addEmployeeClockEntrySelectColumns(sql);
        // add qualifiers
        addEmployeeClockEntryQualifierColumns(sql, searchEntry);
        // add ordering
        addEmployeeClockEntryOrdering(sql);

        return (sql);
    }

    /**
     * Adds select columns for employee clock entry lookup to SQL statement.
     * <P>
     *
     * @param sql SQLSelectStatement object
     */
    protected void addEmployeeClockEntrySelectColumns(SQLSelectStatement sql)
    {
        sql.addColumn(ARTSDatabaseIfc.ALIAS_EMPLOYEE, ARTSDatabaseIfc.FIELD_EMPLOYEE_ID);
        sql.addColumn(ARTSDatabaseIfc.FIELD_RETAIL_STORE_ID);
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_TIME_ENTRY_TIMESTAMP);
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_TIME_ENTRY_REASON_CODE);
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_TIME_ENTRY_TYPE_CODE);
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP);
    }

    /**
     * Adds qualifiers for employee clock entry lookup to SQL statement.
     * <P>
     *
     * @param sql SQLSelectStatement object
     * @param searchEntry EmployeeClockEntryIfc search criteria
     */
    protected void addEmployeeClockEntryQualifierColumns(SQLSelectStatement sql, EmployeeClockEntryIfc searchEntry)
    {
        // add login ID to predicate
        sql.addQualifier(ARTSDatabaseIfc.FIELD_EMPLOYEE_ID_LOGIN, inQuotes(searchEntry.getEmployee().getLoginID()));
        // add store ID to predicate
        sql.addQualifier(ARTSDatabaseIfc.ALIAS_EMPLOYEE_CLOCK_ENTRY, ARTSDatabaseIfc.FIELD_RETAIL_STORE_ID,
                inQuotes(searchEntry.getStoreID()));
        // Join employee and employee clock entry tables
        sql.addJoinQualifier(ARTSDatabaseIfc.ALIAS_EMPLOYEE, ARTSDatabaseIfc.FIELD_EMPLOYEE_ID,
                ARTSDatabaseIfc.ALIAS_EMPLOYEE_CLOCK_ENTRY, ARTSDatabaseIfc.FIELD_EMPLOYEE_ID);
    }

    /**
     * Adds ordering columns for employee clock entry lookup to SQL statement.
     * <P>
     *
     * @param sql SQLSelectStatement object
     */
    protected void addEmployeeClockEntryOrdering(SQLSelectStatement sql)
    {
        sql.addOrdering(ARTSDatabaseIfc.FIELD_EMPLOYEE_TIME_ENTRY_TIMESTAMP + " DESC");
    }

    /**
     * Parse clock entry result set and returns clock entry object.
     * <P>
     *
     * @param rs ResultSet object
     * @return EmployeeClockEntryIfc object
     * @exception SQLException thrown if error SQL error occurs while parsing
     *                result set
     * @exception DataException thrown if error other error occurs while parsing
     *                result set
     */
    protected Stack parseEmployeeClockEntryResultSet(ResultSet rs) throws DataException, SQLException
    {
        Stack<EmployeeClockEntryIfc> entries = new Stack<EmployeeClockEntryIfc>();

        EmployeeClockEntryIfc clockEntry = null;
        EmployeeIfc employee = null;
        LocalizedCodeIfc reason = null;

        try
        {
            while (rs.next())
            {
                int index = 0;
                // initialize objects
                employee = DomainGateway.getFactory().getEmployeeInstance();
                reason = DomainGateway.getFactory().getLocalizedCode();
                clockEntry = DomainGateway.getFactory().getEmployeeClockEntryInstance();
                clockEntry.setReason(reason);

                clockEntry.setEmployee(employee);
                clockEntry.getEmployee().setEmployeeID(getSafeString(rs, ++index));
                clockEntry.setStoreID(getSafeString(rs, ++index));
                clockEntry.setClockEntry(getEYSDateTimeFromString(getSafeString(rs, ++index)));
                clockEntry.getReason().setCode(String.valueOf(rs.getInt(++index)));
                clockEntry.setTypeCode(rs.getInt(++index));
                clockEntry.setLastModified(JdbcDataOperation.timestampToEYSDate(rs, ++index));

                entries.push(clockEntry);
            }
            rs.close();
        }

        catch (SQLException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Last employee clock entry lookup", e);
        }
        if (entries == null)
        {
            throw new DataException(DataException.NO_DATA,
                    "No clock entry was found processing the result set in JdbcReadEmployeeLastClockEntry.");
        }
        return (entries);

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
     * Executes the SQL Statement.
     * <P>
     *
     * @param dataConnection a connection to the database
     * @param sql the SQl statement
     * @param int id comparison basis type
     * @return result set
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected ResultSet execute(JdbcDataConnection dataConnection, SQLSelectStatement sql) throws DataException
    {

        ResultSet rs;
        String sqlString = sql.getSQLString();
        dataConnection.execute(sqlString);
        rs = (ResultSet)dataConnection.getResult();
        return rs;
    }

    /**
     * Retrieves a localized reason code.
     *
     * @param dataConnection
     * @param criteria
     * @return Localized Code object
     */
    protected void readLocalizedReasonCode(JdbcDataConnection dataConnection, Stack entries)
    {
        Locale[] supportedLocales = LocaleMap.getSupportedLocales();
        LocaleRequestor localeRequestor = new LocaleRequestor(supportedLocales);

        for (int i = 0; i < entries.size(); i++)
        {
            EmployeeClockEntryIfc employeeClockEntry = (EmployeeClockEntryIfc)entries.elementAt(i);
            LocalizedCodeIfc reason = employeeClockEntry.getReason();
            if (!reason.getCode().equals(CodeConstantsIfc.CODE_UNDEFINED))
            {
                // Read Localized Reason Code
                CodeSearchCriteriaIfc criteria = DomainGateway.getFactory().getCodeSearchCriteriaInstance();
                criteria.setStoreID(employeeClockEntry.getStoreID());
                criteria.setListID(CodeConstantsIfc.CODE_LIST_TIMEKEEPING_REASON_CODES);
                criteria.setLocaleRequestor(localeRequestor);
                criteria.setCode(reason.getCode());
                try
                {
                    reason = new JdbcReadCodeList().readCode(dataConnection, criteria);
                }
                catch (DataException e)
                {
                    logger.warn("An error occured retrieving the localized descriptions for reason code: " + criteria.getCode(), e);
                    reason = DomainGateway.getFactory().getLocalizedCode();
                    reason.setCode(criteria.getCode());
                }
                employeeClockEntry.setReason(reason);
            }
        }
    }
}
