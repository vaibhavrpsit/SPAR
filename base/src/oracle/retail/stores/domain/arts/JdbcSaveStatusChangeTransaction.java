/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveStatusChangeTransaction.java /main/2 2013/06/24 12:27:18 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/19/13 - Modified to perform the status update of an Order in
 *                         the context of a transaction.
 *    jswan     01/05/12 - Refactor the status change of suspended transaction
 *                         to occur in a transaction so that status change can
 *                         be sent to CO as part of DTM.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.List;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.transaction.OrderStatusChangeTransactionIfc;
import oracle.retail.stores.domain.transaction.StatusChangeTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This operation saves the Transaction Status Change and updates the status 
 * column in the original transaction table.
 */
public class JdbcSaveStatusChangeTransaction extends JdbcSaveControlTransaction implements ARTSDatabaseIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7971120514346167627L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveStatusChangeTransaction.class);

    /**
     * Class constructor.   
     */
    public JdbcSaveStatusChangeTransaction()
    {
        super();
        setName("JdbcSaveStatusChangeTransaction");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveStatusChangeTransaction.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        // get transaction object
        StatusChangeTransactionIfc transaction = (StatusChangeTransactionIfc) action.getDataObject();
        saveStatusChangeTransaction(connection, transaction);
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveStatusChangeTransaction.execute()");
    }

    /**
     * Saves a void transaction and updates the voided transaction's status.
     * <P>
     *
     * @param dataConnection connection to the db
     * @param transaction a void transaction
     * @exception DataException upon error
     */
    public void saveStatusChangeTransaction(JdbcDataConnection dataConnection, StatusChangeTransactionIfc transaction)
            throws DataException
    {
        /*
         * Update the Control Transaction table first.
         */
        insertControlTransaction(dataConnection, transaction);

        SQLInsertStatement sql = new SQLInsertStatement();
        
        List<TransactionSummaryIfc> transactionSummaries = 
            transaction.getTransactionSummaries();
        
        int lineItemNumber         = 0;
        String orderCode           = " ";
        boolean isOrderCancelled   = false;
        boolean isLayawayCancelled = false;
        boolean isTransCancelled   = false;
        
        for(TransactionSummaryIfc summary: transactionSummaries)
        {
            // Table
            sql.setTable(TABLE_STATUS_CHANGE_TRANSACTION);
            
            // Fields
            sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
            sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
            sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
            sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, 
                    getTransactionSequenceNumber(transaction));
            sql.addColumn(FIELD_LINE_ITEM_SEQUENCE_NUMBER, Integer.toString(lineItemNumber));
            lineItemNumber++;
            
            if (summary.getTransactionID() != null)
            {
                sql.addColumn(FIELD_CHANGED_WORKSTATION_ID, 
                        inQuotes(summary.getTransactionID().getFormattedWorkstationID()));
                sql.addColumn(FIELD_CHANGED_TRANSACTION_SEQUENCE_NUMBER, 
                        String.valueOf(summary.getTransactionID().getSequenceNumber()));
                sql.addColumn(FIELD_CHANGED_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
                sql.addColumn(FIELD_TRANSACTION_STATUS_CODE, 
                        String.valueOf(summary.getTransactionStatus()));
                isTransCancelled = true;
            }
    
            // If there is an internal order, add the order column; external
            // orders are not stored in database and are canceled from the site code.
            if (summary.getInternalOrderID() != null)
            {
                sql.addColumn(FIELD_ORDER_ID, 
                    inQuotes(summary.getInternalOrderID()));
                if (transaction instanceof OrderStatusChangeTransactionIfc)
                {
                    orderCode        = ARTSDatabaseIfc.ORDER_LAYAWAY_UPDATE_CODE;
                }
                else
                {
                    orderCode = ARTSDatabaseIfc.ORDER_LAYAWAY_CANCEL_CODE;
                    isOrderCancelled = true;
                }
            }
    
            // If there is a layaway, add the layaway column
            if (summary.getLayawayID() != null)
            {
                orderCode = ARTSDatabaseIfc.ORDER_LAYAWAY_CANCEL_CODE;
                sql.addColumn(FIELD_LAYAWAY_ID, 
                    inQuotes(summary.getLayawayID()));
                isLayawayCancelled = true;
            }
            sql.addColumn(FIELD_ORDER_LAYAWAY_ACTION_CODE, inQuotes(orderCode));
            
            try
            {
                dataConnection.execute(sql.getSQLString());
            }
            catch (DataException de)
            {
                logger.error("" + de + "");
                throw de;
            }
            catch (Exception e)
            {
                logger.error("" + e + "");
                throw new DataException(DataException.UNKNOWN, "insertTransaction", e);
            }
            
            if (isTransCancelled)
            {
                updateTransactionStatus(dataConnection, transaction, summary);
            }

            if (isOrderCancelled)
            {
                cancelOrder(dataConnection, summary.getInternalOrderID());
            }
            
            if (isLayawayCancelled)
            {
                cancelLayaway(dataConnection, summary.getLayawayID());
            }

        }
    }
    
    /**
     * Updates the transaction status.
     * 
     * @param connection JdbcDataConnection
     * @param transaction TransactionIfc object
     * @exception DataException thrown if error occurs
     */
    protected void updateTransactionStatus(JdbcDataConnection dataConnection,
            StatusChangeTransactionIfc transaction, TransactionSummaryIfc summary)
            throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_TRANSACTION);

        // Fields
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                getTransactionEndDateString(transaction));
        sql.addColumn(FIELD_TRANSACTION_STATUS_CODE,
                String.valueOf(summary.getTransactionStatus()));
        
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + 
                inQuotes(summary.getTransactionID().getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + 
                inQuotes(summary.getTransactionID().getWorkstationID()));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + 
                String.valueOf(summary.getTransactionID().getSequenceNumber()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE
                + " = " + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "updateTransactionStatus", e);
        }
    }

    /**
     * Cancel an order which is associated with suspended transaction
     * @param dataConnection
     * @param internalOrderID
     */
    protected void cancelOrder(JdbcDataConnection dataConnection,
            String internalOrderID)
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Add table, order status field and update status value and where clause 
        sql.setTable(TABLE_ORDER);
        sql.addColumn(FIELD_ORDER_STATUS, Integer.toString(OrderConstantsIfc.ORDER_STATUS_SUSPENDED_CANCELED));
        sql.addQualifier(ARTSDatabaseIfc.FIELD_ORDER_ID, inQuotes(internalOrderID));

        // Execute the query, but don't throw exceptions; this not critical processing
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error( "" + de + "");
        }
        catch (Exception e)
        {
            logger.error("" + e + "");
        }
    }

    /**
     * Cancel a layaway which is associated with suspended transaction
     * @param dataConnection
     * @param internalOrderID
     */
    protected void cancelLayaway(JdbcDataConnection dataConnection,
            String layawayID)
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Add table, order status field and update status value and where clause 
        sql.setTable(TABLE_LAYAWAY);
        sql.addColumn(FIELD_LAYAWAY_STATUS, Integer.toString(LayawayConstantsIfc.STATUS_SUSPENDED_CANCELED));
        sql.addQualifier(ARTSDatabaseIfc.FIELD_LAYAWAY_ID, inQuotes(layawayID));

        // Execute the query, but don't throw exceptions; this not critical processing
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error( "" + de + "");
        }
        catch (Exception e)
        {
            logger.error("" + e + "");
        }
    }
}
