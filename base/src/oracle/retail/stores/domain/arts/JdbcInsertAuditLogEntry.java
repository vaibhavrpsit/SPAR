/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcInsertAuditLogEntry.java /main/15 2012/05/21 15:50:17 cgreene Exp $
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

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.audit.AuditEntry;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * JdbcInsertAuditLogEntry is used to insert a new Audit Log entry into the DB.
 * 
 * @see oracle.retail.stores.domain.audit.AuditEntryIfc
 * @version $Revision: /main/15 $
 */
public class JdbcInsertAuditLogEntry extends JdbcDataOperation
{
    private static final long serialVersionUID = -1329615783058822388L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcInsertAuditLogEntry.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Class constructor.
     */
    public JdbcInsertAuditLogEntry()
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
                     "JdbcInsertAuditLogEntry.execute");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        
        AuditEntry entry = (AuditEntry) action.getDataObject();
        insertAuditEntry(connection, entry);

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcInsertAuditLogEntry.execute");
    }

    /**
     * Inserts the entry.
     * 
     * @param dataConnection a connection to the database
     * @param entry
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void insertAuditEntry(JdbcDataConnection dataConnection, AuditEntry entry) throws DataException
    {
        execute(dataConnection, buildAuditEntrySQLStatement(entry));
    }

    /**
     * Builds and returns SQL statement for inserting the entry.
     * 
     * @param entry the entry to insert
     * @return SQL select statement for retrieving the latest employee clock
     *         entry
     */
    protected SQLInsertStatement buildAuditEntrySQLStatement(AuditEntry entry)
    {
   SQLInsertStatement sql = new SQLInsertStatement();
        // add tables
        sql.setTable(ARTSDatabaseIfc.TABLE_AUDIT_LOG);

        sql.addColumn(ARTSDatabaseIfc.FIELD_AUDIT_LOG_ENTRY_ID, entry.getEntryID());
        sql.addColumn(ARTSDatabaseIfc.FIELD_RETAIL_STORE_ID, entry.getStoreID());
        sql.addColumn(ARTSDatabaseIfc.FIELD_AUDIT_LOG_ENTRY_TYPE, entry.getEntryType());
        sql.addColumn(ARTSDatabaseIfc.FIELD_AUDIT_LOG_FIELD_NAME, super.makeSafeString(entry.getFieldName()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_AUDIT_LOG_EMPLOYEE_ID, entry.getUserID());
        sql.addColumn(ARTSDatabaseIfc.FIELD_AUDIT_LOG_OBJECT_ID, entry.getObjectID());
        sql.addColumn(ARTSDatabaseIfc.FIELD_AUDIT_LOG_OLD_VALUE, makeSafeString(entry.getOldValue()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_AUDIT_LOG_NEW_VALUE, makeSafeString(entry.getNewValue()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_CREATION_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());

        if (entry.getReasonCode() != -1)
        {
            sql.addColumn(ARTSDatabaseIfc.FIELD_AUDIT_LOG_REASON_CODE, entry.getReasonCode());
        }

        return(sql);
    }

    /**
     * Generate a safe string based on the needs of the Audit Entry.
     * 
     * @param str the object
     */
    public String makeSafeString(Object str)
    {
        if (str == null)
        {
            return "'Empty'";
        }

        else if (str instanceof EYSDate)
        {
            return dateToSQLTimestampString((EYSDate) str);
        }

        else
        {
            return super.makeSafeString(str.toString());
        }
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
