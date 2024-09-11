/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateCustomerBatchIDs.java /rgbustores_13.4x_generic_branch/2 2011/09/13 17:21:32 masahu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    masahu    09/13/11 - Fortify Fix: Cannot log sensitive SQL statements
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    npoola    01/23/09 - fixed the update query for the customer
 *    mahising  12/09/08 - rework of base issue
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *    mahising  11/12/08 - added for customer
 *
 * ===========================================================================
 */

package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.ixretail.log.POSLogTransactionEntryIfc;
import oracle.retail.stores.domain.manager.datareplication.DataReplicationCustomerEntryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation updates the t-log batch ID columns in the customer table.
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class JdbcUpdateCustomerBatchIDs extends JdbcSaveTransaction implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 904117710824782450L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcUpdateCustomerBatchIDs.class);

    /**
     * Class constructor.
     */
    public JdbcUpdateCustomerBatchIDs()
    {
        super();
        setName("JdbcUpdateCustomerBatchIDs");
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
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcUpdateCustomerBatchIDs.execute()");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        DataReplicationCustomerEntryIfc[] customers =
          (DataReplicationCustomerEntryIfc[]) action.getDataObject();

        int updateCount = 0;
        String batchID = POSLogTransactionEntryIfc.NO_BATCH_IDENTIFIED;
        // pull batch ID out of first entry
        if (customers.length > 0)
        {
            batchID = customers[0].getBatchID();
        }
        for (int i = 0; i < customers.length; i++)
        {
            updateCustomerBatchID(connection, customers[i]);
            updateCount++;
        }

        Integer returnCount = Integer.valueOf(updateCount);
        dataTransaction.setResult(returnCount);

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcUpdateCustomerBatchIDs.execute()");
    }

    /**
     * Updates batch ID on a given customer records.
     *
     * @param dataConnection connection to database
     * @param customer entry
     * @exception DataException thrown if error occurs
     */
    public void updateCustomerBatchID(JdbcDataConnection dataConnection,DataReplicationCustomerEntryIfc customers)
    throws DataException
    {
        try
        {
            SQLUpdateStatement sql = buildUpdateSQL(customers);
            dataConnection.execute(sql.getSQLString(), false);
        }
        catch (DataException de)
        {
            // no data found error is Ok
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                logger.error(de);
                throw de;
            }
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "updateCustomerBatchID", e);
        }
    }

    /**
     * Builds SQL statement for updating batch ID for a transaction.
     *
     * @param customer transaction entry
     * @param batchID batch identifier
     * @return SQL statement for performing update
     */
    protected SQLUpdateStatement buildUpdateSQL(DataReplicationCustomerEntryIfc customer)
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();
        // set table
        sql.setTable(TABLE_CUSTOMER);
        // set column
        sql.addColumn(FIELD_CUSTOMER_TLOG_BATCH_IDENTIFIER, customer.getBatchID());

        // add qualifier
        sql.addQualifier(FIELD_CUSTOMER_ID, inQuotes(customer.getCustomerID()));

        return (sql);
    }

    /**
     * Retrieves the source-code-control system revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}
