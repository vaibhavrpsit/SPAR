/* ===========================================================================
* Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateTransactionExternalOrder.java /main/3 2012/05/21 15:50:20 cgreene Exp $
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
 *    ohorne    06/11/10 - created class
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.Date;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * Insert External Order Line Item IDs in External Order Line Item Transaction Table
 */
public class JdbcUpdateTransactionExternalOrder extends JdbcDataOperation implements ARTSDatabaseIfc
{

    private static final long serialVersionUID = 6370851977876233507L;
    
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcUpdateTransactionExternalOrder.class);

    /**
     * Class constructor.
     */
    public JdbcUpdateTransactionExternalOrder()
    {
        super();
        setName("JdbcUpdateTransactionExternalOrder");
    }

    public void execute(DataTransactionIfc dt, DataConnectionIfc dataConnection, DataActionIfc action) throws DataException
    {
        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        SaleReturnTransactionIfc transaction = (SaleReturnTransactionIfc) action.getDataObject();
        AbstractTransactionLineItemIfc[] lineItems = transaction.getLineItems();
        
        WorkstationIfc workStation = transaction.getWorkstation();
        String storeId = workStation.getStoreID();
        String workStationId =  workStation.getWorkstationID();
        Date businessDate = transaction.getBusinessDay().dateValue();
        long transactionSequence = transaction.getTransactionSequenceNumber();
        
        for (AbstractTransactionLineItemIfc lineItem : lineItems)
        {
            if (lineItem instanceof SaleReturnLineItemIfc)
            {
                SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) lineItem;
                
                updateRetailTransactionLineItem(connection,
                                            storeId,
                                            workStationId,
                                            businessDate,
                                            transactionSequence,
                                            srli.getLineNumber(),
                                            getExternalOrderItemID(srli));
            }
        }
    }
    
    /**
     * Updates External Order Line Item Transaction Table with External Order Line Item IDs 
     * @param dataConnection Data source connection to use
     * @param storeId transaction's store id
     * @param workStationId transaction's workstation id
     * @param businessDate transaction's business date
     * @param transactionSequence transaction's sequence number
     * @param lineNumber line item's line number
     * @param lineItemId line item's external order line item Id  
     * @exception DataException upon error
     */
    public void updateRetailTransactionLineItem(JdbcDataConnection dataConnection,
            String storeId, String workStationId, Date businessDate, 
            long transactionSequence, int lineNumber, String lineItemId) throws DataException
    {

        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_EXTERNAL_ORDER_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_EXTERNAL_ORDER_ITEM_ID, lineItemId);

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID, inQuotes(storeId));
        sql.addQualifier(FIELD_WORKSTATION_ID, inQuotes(workStationId));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, String.valueOf(transactionSequence));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(businessDate));
        sql.addQualifier(FIELD_LINE_ITEM_SEQUENCE_NUMBER, lineNumber);

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
            throw new DataException(DataException.UNKNOWN, "saveRetailTransactionLineItem", e);
        }
    }

    /**
     * Get external order item ID
     * @param lineItem the sale return line item
     * @return the external order item ID
     */
    protected String getExternalOrderItemID(SaleReturnLineItemIfc lineItem)
    {
        return makeSafeString(lineItem.getExternalOrderItemID());
    }
    
}
