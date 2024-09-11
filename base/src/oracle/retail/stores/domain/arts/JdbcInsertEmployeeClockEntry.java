/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcInsertEmployeeClockEntry.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:05 mszekely Exp $
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
 *    mdecama   11/05/08 - I18N Reason Code - Refactored the EmployeeClockEntry
 *                         reason field.
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:08 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:37 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:38 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:54 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:32    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:37     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:38     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:54     Robert Pearse
 *
 *   Revision 1.8  2004/08/13 13:47:08  kll
 *   @scr 0: deprecation fixes
 *
 *   Revision 1.6  2004/04/09 16:55:44  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:14  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:30:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jun 10 2002 11:14:52   epd
 * Merged in changes for Oracle
 * Resolution for Domain SCR-83: Merging database fixes into base code
 *
 *    Rev 1.4   Jun 07 2002 17:47:38   epd
 * Merging in fixes made for McDonald's Oracle demo
 * Resolution for Domain SCR-83: Merging database fixes into base code
 *
 *    Rev 1.3   May 31 2002 17:02:06   dfh
 * removed inquotes
 * Resolution for POS SCR-1709: (Sybase) Void of new special order is stuck in the queue.
 *
 *    Rev 1.2   30 Apr 2002 20:57:36   dfh
 * added clock entry type code
 * Resolution for POS SCR-1622: Employee Clock In/Out needs type code
 *
 *    Rev 1.1   Mar 18 2002 22:46:40   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:06:50   msg
 * Initial revision.
 *
 *    Rev 1.1   31 Oct 2001 07:54:06   mpm
 * Corrected comment.
 *
 *    Rev 1.0   28 Oct 2001 11:45:14   mpm
 * Initial revision.
 * Resolution for POS SCR-235: Employee clock-in, clock-out
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.domain.employee.EmployeeClockEntryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation inserts an employee clock entry.
 * 
 * @see oracle.retail.stores.domain.arts.EmployeeWriteTransaction
 * @see oracle.retail.stores.domain.employee.EmployeeClockEntryIfc
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcInsertEmployeeClockEntry extends JdbcDataOperation
{
    /**
     * Generated Serial Version UID
     */
    private static final long serialVersionUID = 2270631651308185944L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcInsertEmployeeClockEntry.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Class constructor.
     */
    public JdbcInsertEmployeeClockEntry()
    {
        setName("JdbcInsertEmployeeClockEntry");
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
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcInsertEmployeeClockEntry.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        EmployeeClockEntryIfc clockEntry =
          (EmployeeClockEntryIfc) action.getDataObject();
        insertEmployeeClockEntry(connection, clockEntry);

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcInsertEmployeeClockEntry.execute()");
    }

    /**
     * Perform employee clock entry insert.
     * 
     * @param dataConnection JdbcDataConnection
     * @param clockEntry EmployeeClockEntryIfc reference
     * @exception DataException thrown if error occurs
     */
    public void insertEmployeeClockEntry(JdbcDataConnection dataConnection, EmployeeClockEntryIfc clockEntry)
            throws DataException
    {
        // build sql statement
        SQLInsertStatement sql = new SQLInsertStatement();
        // add table, columns, qualifiers
        sql.setTable(ARTSDatabaseIfc.TABLE_EMPLOYEE_CLOCK_ENTRY);
        addInsertColumns(clockEntry, sql);
        // execute statement
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error( de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error(
                         e.toString());
            throw new DataException(DataException.UNKNOWN,
                                    "Employee clock entry insert", e);
        }

    }

    /**
     * Add insert columns.
     * 
     * @param EmployeeClockEntryIfc clock entry object
     * @param sql SQLInsertStatement
     */
    public void addInsertColumns(EmployeeClockEntryIfc clockEntry,
                                SQLUpdatableStatementIfc sql)
    {
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_ID,
                      inQuotes(clockEntry.getEmployee().getEmployeeID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_RETAIL_STORE_ID,
                      inQuotes(clockEntry.getStoreID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_TIME_ENTRY_TIMESTAMP,
                      dateToSQLTimestampFunction(clockEntry.getClockEntry()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_TIME_ENTRY_REASON_CODE,
                      clockEntry.getReason().getCode());
        sql.addColumn(ARTSDatabaseIfc.FIELD_EMPLOYEE_TIME_ENTRY_TYPE_CODE,
                      Integer.toString(clockEntry.getTypeCode()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_CREATION_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    /**
     * Returns the string representation of this object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        return (Util.classToStringHeader("JdbcInsertEmployeeClockEntry", getRevisionNumber(), hashCode()).toString());
    }
}
