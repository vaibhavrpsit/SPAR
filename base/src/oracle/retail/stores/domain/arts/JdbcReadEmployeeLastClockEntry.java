/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadEmployeeLastClockEntry.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:02 mszekely Exp $
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
 *    cgreene   02/10/10 - set code on criteria with previous clock entry's
 *                         reason
 *    abondala  01/03/10 - update header date
 *    mdecama   11/05/08 - I18N - Reason Codes. Fixed implementation for
 *                         readLocalizedReason
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
 *    4    .v700     1.2.1.0     11/16/2005 16:26:54    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:40     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:43     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:58     Robert Pearse
 *
 *   Revision 1.7  2004/08/19 23:02:50  cdb
 *   @scr 6644 DB2 updates.
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:31:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:37:14   msg
 * Initial revision.
 *
 *    Rev 1.3   30 May 2002 07:00:54   dal
 * Added last modified data member to EmployeeClockEntryIfc
 * Resolution for Backoffice SCR-908: Calculating Hour method is not accurate at Employee Edit Hours screen
 *
 *    Rev 1.2   30 Apr 2002 20:57:38   dfh
 * added clock entry type code
 * Resolution for POS SCR-1622: Employee Clock In/Out needs type code
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

import org.apache.log4j.Logger;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeClockEntryIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * JdbcReadEmployeeLastClockEntry retrieves the latest clock entry for a given
 * employee and store.
 * 
 * @see oracle.retail.stores.domain.employee.EmployeeClockEntryIfc
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcReadEmployeeLastClockEntry extends JdbcDataOperation
{
    /**
     * Generated SerialVersion UID
     */
    private static final long serialVersionUID = -5200380416003831457L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadEmployeeLastClockEntry.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Class constructor.
     */
    public JdbcReadEmployeeLastClockEntry()
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
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadEmployeeLastClockEntry.execute");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // look up the item
        EmployeeClockEntryIfc searchEntry = (EmployeeClockEntryIfc) action.getDataObject();
        EmployeeClockEntryIfc clockEntry = readEmployeeLastClockEntry(connection, searchEntry);
        dataTransaction.setResult(clockEntry);

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadEmployeeLastClockEntry.execute");
    }

    /**
     * Selects the latest clock entry from the employee clock entry table
     * 
     * @param dataConnection a connection to the database
     * @param searchEntry search clock entry object
     * @return selected clock entry object
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected EmployeeClockEntryIfc readEmployeeLastClockEntry(JdbcDataConnection dataConnection,
            EmployeeClockEntryIfc searchEntry) throws DataException
    {
        EmployeeClockEntryIfc clockEntry = null;

        // build SQL statement, execute and parse
        SQLSelectStatement sql = buildClockEntrySQLStatement(searchEntry);
        try
        {
            ResultSet rs = execute(dataConnection, sql);
            clockEntry = parseEmployeeClockEntryResultSet(rs);

            // Read Localized Reason Codes
            clockEntry.setReason(readLocalizedReason(dataConnection, clockEntry));
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "Last employee clock entry lookup");
            throw new DataException(DataException.SQL_ERROR, "Last employee clock entry lookup", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Last employee clock entry lookup", e);
        }

        return (clockEntry);
    }

    /**
     * Builds and returns SQL statement for retrieving the last employee clock
     * entry.
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
     * 
     * @param sql SQLSelectStatement object
     */
    protected void addEmployeeClockEntrySelectColumns(SQLSelectStatement sql)
    {
        sql.addColumn(ARTSDatabaseIfc.ALIAS_EMPLOYEE, ARTSDatabaseIfc.FIELD_EMPLOYEE_ID);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_EMPLOYEE_CLOCK_ENTRY, ARTSDatabaseIfc.FIELD_RETAIL_STORE_ID);
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_TIME_ENTRY_TIMESTAMP);
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_TIME_ENTRY_REASON_CODE);
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_TIME_ENTRY_TYPE_CODE);
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP);
    }

    /**
     * Adds qualifiers for employee clock entry lookup to SQL statement.
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
     * 
     * @param sql SQLSelectStatement object
     */
    protected void addEmployeeClockEntryOrdering(SQLSelectStatement sql)
    {
        sql.addOrdering(ARTSDatabaseIfc.FIELD_EMPLOYEE_TIME_ENTRY_TIMESTAMP + " DESC");
    }

    /**
     * Parse clock entry result set and returns clock entry object.
     * 
     * @param rs ResultSet object
     * @return EmployeeClockEntryIfc object
     * @exception SQLException thrown if error SQL error occurs while parsing
     *                result set
     * @exception DataException thrown if error other error occurs while parsing
     *                result set
     */
    protected EmployeeClockEntryIfc parseEmployeeClockEntryResultSet(ResultSet rs) throws DataException, SQLException
    {
        EmployeeClockEntryIfc clockEntry = null;
        EmployeeIfc employee = null;
        LocalizedCodeIfc reason = null;
        try
        {
            // begin parse-result-set try block
            if (rs.next())
            {
                // begin handle result set
                int index = 0;
                // initialize objects
                employee = DomainGateway.getFactory().getEmployeeInstance();
                clockEntry = DomainGateway.getFactory().getEmployeeClockEntryInstance();
                reason = DomainGateway.getFactory().getLocalizedCode();

                clockEntry.setReason(reason);
                clockEntry.setEmployee(employee);
                clockEntry.getEmployee().setEmployeeID(getSafeString(rs, ++index));
                clockEntry.setStoreID(getSafeString(rs, ++index));
                clockEntry.setClockEntry(JdbcDataOperation.timestampToEYSDate(rs, ++index));
                clockEntry.getReason().setCode(Integer.toString(rs.getInt(++index)));
                clockEntry.setTypeCode(rs.getInt(++index));
                clockEntry.setLastModified(JdbcDataOperation.timestampToEYSDate(rs, ++index));
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
        if (clockEntry == null)
        {
            throw new DataException(DataException.NO_DATA,
                    "No clock entry was found processing the result set in JdbcReadEmployeeLastClockEntry.");
        }

        return (clockEntry);
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
    protected ResultSet execute(JdbcDataConnection dataConnection, SQLSelectStatement sql) throws DataException
    {

        ResultSet rs;
        String sqlString = sql.getSQLString();
        dataConnection.execute(sqlString);
        rs = (ResultSet) dataConnection.getResult();
        return rs;
    }

    /**
     * Reads a Localized Reason
     * 
     * @param dataConnection
     * @param employeeClockEntry
     * @return
     */
    protected LocalizedCodeIfc readLocalizedReason(JdbcDataConnection dataConnection,
            EmployeeClockEntryIfc employeeClockEntry)
    {
        LocalizedCodeIfc reason = DomainGateway.getFactory().getLocalizedCode();

        Locale[] supportedLocales = LocaleMap.getSupportedLocales();
        LocaleRequestor localeRequestor = new LocaleRequestor(supportedLocales);

        CodeSearchCriteriaIfc criteria = DomainGateway.getFactory().getCodeSearchCriteriaInstance();
        criteria.setStoreID(employeeClockEntry.getStoreID());
        criteria.setCode(employeeClockEntry.getReason().getCode());
        criteria.setListID(CodeConstantsIfc.CODE_LIST_TIMEKEEPING_REASON_CODES);
        criteria.setLocaleRequestor(localeRequestor);
        try
        {
            reason = new JdbcReadCodeList().readCode(dataConnection, criteria);
        }
        catch (DataException e)
        {
            logger.warn(
                    "An error occured retrieving the localized descriptions for reason code: " + criteria.getCode(), e);
            reason = DomainGateway.getFactory().getLocalizedCode();
            reason.setCode(criteria.getCode());
        }

        return reason;
    }
}
