/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcIncrementBatchCount.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:59 mszekely Exp $
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
 *  1    360Commerce 1.0         3/1/2008 12:23:44 PM   Alan N. Sinton  CR
 *       30729: Merged in fix for DCLOSE count from v12x branch.  Code
 *       reviewed by Jack Swan.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.ixretail.log.POSLogTransactionEntryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation increments the Batch Count for RTLog export.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcIncrementBatchCount extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -6168098389510113805L;

    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcIncrementBatchCount.class);

    /**
       revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       Class constructor.
     */
    public JdbcIncrementBatchCount()
    {
        super();
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
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcIncrementBatchCount.execute()");

        // Down cast the connecion and call the select
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        POSLogTransactionEntryIfc entry = (POSLogTransactionEntryIfc) action.getDataObject();
        if (updateBatchCount(connection, entry) < 1)
        {
            insertBatchCount(connection, entry);
        }
    }

    /**
     * Executes the update statement against the db.
     * 
     * @param connection a JdbcDataConnection object
     * @param entry Transaction header info.
     * @exception DataException upon error
     */
    protected int updateBatchCount(JdbcDataConnection connection, POSLogTransactionEntryIfc entry)
        throws DataException
    {
        // Put away the role record
        // Define the table
        SQLUpdateStatement sql = new SQLUpdateStatement();
        sql.setTable(TABLE_BUSINESS_DAY_BATCH_COUNT);

        // Add columns and their values and qualifiers
        sql.addColumn(FIELD_BATCH_COUNT, FIELD_BATCH_COUNT + " + 1");
        sql.addQualifier(FIELD_RETAIL_STORE_ID,
                inQuotes(entry.getStoreID()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE,
                dateToSQLDateString(entry.getBusinessDate()));

        // Execute the SQL statement
        connection.execute(sql.getSQLString());

        int ret = connection.getUpdateCount();
        return ret;
    }

    /**
     * Executes the insert statement against the db.
     * 
     * @param connection a Jdbcconnection object
     * @param entry transaction header info.
     * @exception DataException upon error
     */
    protected void insertBatchCount(JdbcDataConnection connection, POSLogTransactionEntryIfc entry)
        throws DataException
    {
        // Put away the role record
        // Define the table
        SQLInsertStatement sql = new SQLInsertStatement();
        sql.setTable(TABLE_BUSINESS_DAY_BATCH_COUNT);

        // Add columns and their values and qualifiers
        sql.addColumn(FIELD_BATCH_COUNT, "1");
        sql.addColumn(FIELD_RETAIL_STORE_ID,
                inQuotes(entry.getStoreID()));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE,
                dateToSQLDateString(entry.getBusinessDate()));

        // Execute the SQL statement
        connection.execute(sql.getSQLString());
    }

    /**
     * Returns a string representation of this object.
     * 
     * @param none
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  JdbcIncrementBatchCount (Revision " + getRevisionNumber() + ")"
                + hashCode());

        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @param none
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}
