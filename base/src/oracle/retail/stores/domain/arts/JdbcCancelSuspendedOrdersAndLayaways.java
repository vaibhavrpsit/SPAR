/* ===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcCancelSuspendedOrdersAndLayaways.java /main/1 2012/03/08 08:56:25 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     01/05/12 - Refactor the status change of suspended transaction
 *                         to occur in a transaction so that status change can
 *                         be sent to CO as part of DTM.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.List;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.transaction.StatusChangeTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This operation cancels all the suspended orders and layaways for a store and
 * business date, setting the status to "Suspended-Canceled".
 * 
 * @version $Revision: /main/1 $
 */
public class JdbcCancelSuspendedOrdersAndLayaways extends JdbcSaveTransaction implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -5304475832378144949L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcCancelSuspendedOrdersAndLayaways.class);

    /**
     * Class constructor.   
     */
    public JdbcCancelSuspendedOrdersAndLayaways()
    {
        super();
        setName("JdbcCancelSuspendedOrdersAndLayaways");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcCancelSuspendedOrdersAndLayaways.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        // get transaction object
        StatusChangeTransactionIfc transaction = (StatusChangeTransactionIfc) action.getDataObject();

        // cancel layaways which have been suspended along with a suspended transaction
        cancelSuspendedLayaways(connection, transaction);
        
        // cancel orders which have been suspended along with a suspended transaction
        updateOrderStatus(connection, transaction);

        if (logger.isDebugEnabled()) logger.debug( "JdbcCancelSuspendedOrdersAndLayaways.execute()");
    }

    /**
     * Cancel layaways corresponding to suspended layaway-initiate transactions,
     * if any.
     * 
     * @param connection JdbcDataConnection
     * @param transaction TransactionIfc object key
     * @exception DataException thrown if error occurs
     */
    public void cancelSuspendedLayaways(JdbcDataConnection dataConnection,
            StatusChangeTransactionIfc transaction) throws DataException
    {
        // Iterate through the transaction summaries.
        List<TransactionSummaryIfc> summaries = transaction.getTransactionSummaries();
        for(TransactionSummaryIfc summary: summaries)
        {
            // If the summary has a layawayID...
            if (!Util.isEmpty(summary.getLayawayID()))
            {
                try
                {
                    // Cancel it.
                    SQLUpdateStatement sql = new SQLUpdateStatement();
                    
                    // Table
                    sql.setTable(TABLE_LAYAWAY);
                    
                    // Columns
                    sql.addColumn(FIELD_LAYAWAY_STATUS, Integer.toString(LayawayConstantsIfc.STATUS_SUSPENDED_CANCELED));
                    sql.addColumn(FIELD_LAYAWAY_PREVIOUS_STATUS, FIELD_LAYAWAY_STATUS);
                    sql.addColumn(FIELD_LAYAWAY_TIMESTAMP_LAST_STATUS_CHANGE, getSQLCurrentTimestampFunction());
                    sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
                    
                    // Qualifiers
                    sql.addQualifier(FIELD_LAYAWAY_ID + " = " + inQuotes(summary.getLayawayID()));
                    
                    dataConnection.execute(sql.getSQLString());
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
                    throw new DataException(DataException.UNKNOWN, "cancelSuspendedLayaways", e);
                }
            }
        }
    }

    /**
     * Updates the order status.
     * 
     * @param connection JdbcDataConnection
     * @param transaction TransactionIfc object
     * @exception DataException thrown if error occurs
     */
    protected void updateOrderStatus(JdbcDataConnection dataConnection,
            StatusChangeTransactionIfc transaction) throws DataException
    {
        // Iterate through the summaries
        List<TransactionSummaryIfc> summaries = transaction.getTransactionSummaries();
        for(TransactionSummaryIfc summary: summaries)
        {
            // If the summary has an order ID...
            if (!Util.isEmpty(summary.getInternalOrderID()))
            {
                try
                {
                    // Cancel the order.
                    SQLUpdateStatement sql = new SQLUpdateStatement();
            
                    // Table
                    sql.setTable(TABLE_ORDER);
            
                    // Columns
                    sql.addColumn(FIELD_ORDER_STATUS,
                                  String.valueOf(TransactionIfc.STATUS_SUSPENDED_CANCELED));
                    sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
                    sql.addColumn(FIELD_ORDER_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
                    
                    // Qualifier
                    sql.addQualifier(FIELD_ORDER_ID + " = " + inQuotes(summary.getInternalOrderID()));
                    
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
        }
    }
}
