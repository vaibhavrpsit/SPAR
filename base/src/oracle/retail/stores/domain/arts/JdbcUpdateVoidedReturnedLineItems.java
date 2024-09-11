/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateVoidedReturnedLineItems.java /main/17 2014/07/17 15:09:41 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   07/16/14 - Update return quantity for order take with item.
 *    jswan     05/10/13 - Added code to update the order item tables on voided
 *                         return based on a store order.
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
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:28 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:46 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:55 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:06 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:08    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:46     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:55     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:06     Robert Pearse
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
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:41:46   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 30 2002 09:37:58   mpm
 * Imported changes for PostgreSQL compatibility from 5.0.
 * Resolution for Backoffice SCR-795: Employee Assignment report abends under Postgresql
 *
 *    Rev 1.1   Mar 18 2002 22:50:22   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:09:50   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:56:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.List;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.lineitem.OrderItemDiscountStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderItemTaxStatusIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This operation updates the original return line items of voided return
 * transactions.
 * 
 * @version $Revision: /main/17 $
 */
public class JdbcUpdateVoidedReturnedLineItems extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 5499296037056049439L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcUpdateVoidedReturnedLineItems.class);

    /**
     * Class constructor.
     */
    public JdbcUpdateVoidedReturnedLineItems()
    {
        super();
        setName("JdbcUpdateVoidedReturnedLineItems");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction
     * @param dataConnection a connection to the database
     * @param action
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateVoidedReturnedLineItems.execute");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        /*
         * Send back the correct transaction (or lack thereof)
         */
        SaleReturnLineItemIfc[] lineItems =
            (SaleReturnLineItemIfc[]) action.getDataObject();
        for(int i = 0; i < lineItems.length; i++)
        {
            updateSaleReturnLineItem(connection, lineItems[i]);
            
            // Reverse in store order return information. 
            if (!Util.isEmpty(lineItems[i].getOrderID()) && 
                    !lineItems[i].getOrderItemStatus().isCrossChannelItem())
            {
                updateOrderTables(connection, lineItems[i]);
            }
                    
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateVoidedReturnedLineItems.execute");
    }

    /**
     * Updates original return line items for voided transactions.
     * 
     * @param dataConnection a connection to the database
     * @param SaleReturnLineItem the line containing the original transaction
     *            information
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void updateSaleReturnLineItem(JdbcDataConnection dataConnection,
                                            SaleReturnLineItemIfc lineItem)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcUpdateVoidedReturnedLineItems.updateSaleReturnLineItem()");

        SQLUpdateStatement sql = new SQLUpdateStatement();

        /*
         * Add Table(s)
         */
        sql.setTable(TABLE_SALE_RETURN_LINE_ITEM);

        // Backout the number of items returned
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_RETURN_QUANTITY,
                      FIELD_SALE_RETURN_LINE_ITEM_RETURN_QUANTITY + "-" +
                      safeSQLCast(lineItem.getItemQuantityDecimal().toString()));

        /*
         * Add Qualifier(s)
         */
        // For the specific transaction only
        ReturnItemIfc ri = lineItem.getReturnItem();
        if (ri.getOriginalTransactionID()!=null)
        {
            //if the transaction is order transaction, only for take with order. Pickup and shipping order
            //item does not contain original transaction id. The data is read from OMS.
            sql.addQualifier(FIELD_RETAIL_STORE_ID + " = "
                             + getStoreID(ri.getOriginalTransactionID()));
            sql.addQualifier(FIELD_WORKSTATION_ID + " = "
                             + getWorkstationID(ri.getOriginalTransactionID()));
            sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER
                             + " = " + getTransactionSequenceNumber(ri.getOriginalTransactionID()));
            sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER
                             + " = " + String.valueOf(ri.getOriginalLineNumber()));
            sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = "
                    + dateToSQLDateString(ri.getOriginalTransactionBusinessDate().dateValue()));

            dataConnection.execute(sql.getSQLString());

            int count = dataConnection.getUpdateCount();
            if (count <= 0)
            {
                logger.warn( "No Return Line is available to restore to it's pre-void State.");
            }
        }
        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcUpdateVoidedReturnedLineItems.selectSaleReturnLineItems");
    }

    protected void updateOrderTables(JdbcDataConnection connection,
            SaleReturnLineItemIfc saleReturnLineItemIfc) throws DataException
    {
        try
        {
            updateOrderItem(connection, saleReturnLineItemIfc);
            updateOrderItemDiscountAmount(connection, saleReturnLineItemIfc);
            updateOrderItemTaxAmount(connection, saleReturnLineItemIfc);
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "updateOrderTables", e);
        }
    }

    /**
     * Reset the return values in the order item table.
     * @param connection
     * @param lineItem
     * @throws DataException
     */
    protected void updateOrderItem(JdbcDataConnection connection,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug("Start updateOrderItem()");

        OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        SQLUpdateStatement sql = new SQLUpdateStatement();

        /*
         * Add Table(s)
         */
        sql.setTable(TABLE_ORDER_ITEM);

        // Backout the number of items returned
        sql.addColumn(FIELD_ITEM_QUANTITY_RETURNED,
                      FIELD_ITEM_QUANTITY_RETURNED + "-" +
                      safeSQLCast(lineItem.getOrderItemStatus().getQuantityReturned().toString()));

        // Backout the amount of items returned
        sql.addColumn(FIELD_ORDER_RETURNED_AMOUNT,
                      FIELD_ORDER_RETURNED_AMOUNT + "-" +
                      safeSQLCast(lineItem.getOrderItemStatus().getReturnedAmount().toString()));

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_ORDER_ID, inQuotes(lineItem.getOrderID()));
        sql.addQualifier(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
        sql.addQualifier(FIELD_ORDER_ORIGINAL_STORE_ID + " = " + makeSafeString(itemStatus.getOriginalTransactionId().getStoreID()) );
        sql.addQualifier(FIELD_ORDER_ORIGINAL_WORKSTATION_ID + " = " + makeSafeString(itemStatus.getOriginalTransactionId().getWorkstationID()) );
        
        connection.execute(sql.getSQLString());

        int count = connection.getUpdateCount();
        if (count <= 0)
        {
            logger.warn( "No Order Line Item is available to restore to it's pre-void State.");
        }
        
        if (logger.isDebugEnabled()) logger.debug("End updateOrderItem()");
    }

    /**
     * Reset the return values in the order item discount table.
     * @param connection
     * @param lineItem
     * @throws DataException
     */
    protected void updateOrderItemDiscountAmount(JdbcDataConnection connection,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug("Start updateOrderItemDiscountAmount()");

        OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        List<OrderItemDiscountStatusIfc> discountList = itemStatus.getDiscountStatusList();
        
        for (OrderItemDiscountStatusIfc oids: discountList)
        {
            SQLUpdateStatement sql = new SQLUpdateStatement();
            sql.setTable(TABLE_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER);
    
            sql.addColumn(FIELD_RETURNED_DISCOUNT_AMOUNT, 
                          FIELD_RETURNED_DISCOUNT_AMOUNT + "-" +
                          safeSQLCast(oids.getReturnedAmount().toString()));
    
            sql.addQualifier(FIELD_ORDER_ID, inQuotes(lineItem.getOrderID()));
            sql.addQualifier(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
            sql.addQualifier(FIELD_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER, oids.getLineNumber());
            sql.addQualifier(FIELD_ORDER_ORIGINAL_STORE_ID + " = " + makeSafeString(itemStatus.getOriginalTransactionId().getStoreID()) );
            sql.addQualifier(FIELD_ORDER_ORIGINAL_WORKSTATION_ID + " = " + makeSafeString(itemStatus.getOriginalTransactionId().getWorkstationID()) );
            
            connection.execute(sql.getSQLString());

            int count = connection.getUpdateCount();
            if (count <= 0)
            {
                logger.warn( "No Order Line Item Discount is available to restore to it's pre-void State.");
            }
        }
        
        if (logger.isDebugEnabled()) logger.debug("End updateOrderItemDiscountAmount()");
    }

    /**
     * Reset the return values in the order item tax table.
     * @param connection
     * @param lineItem
     * @throws DataException
     */
    protected void updateOrderItemTaxAmount(JdbcDataConnection connection,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug("Start updateOrderItemDiscountAmount()");

        OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        List<OrderItemTaxStatusIfc> taxList = itemStatus.getTaxStatusList();
        
        for (OrderItemTaxStatusIfc oits: taxList)
        {
            SQLUpdateStatement sql = new SQLUpdateStatement();
            sql.setTable(TABLE_ORDER_LINE_ITEM_TAX);

            sql.addColumn(FIELD_RETURNED_TAX_AMOUNT, 
                    FIELD_RETURNED_TAX_AMOUNT + "-" +
                    safeSQLCast(oits.getReturnedAmount().toString()));

            sql.addQualifier(FIELD_ORDER_ID, inQuotes(lineItem.getOrderID()));
            sql.addQualifier(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
            sql.addQualifier(FIELD_TAX_AUTHORITY_ID, oits.getAuthorityID());
            sql.addQualifier(FIELD_TAX_GROUP_ID, oits.getTaxGroupID());
            sql.addQualifier(FIELD_TAX_TYPE, oits.getTypeCode());
            sql.addQualifier(FIELD_ORDER_ORIGINAL_STORE_ID + " = " + makeSafeString(itemStatus.getOriginalTransactionId().getStoreID()) );
            sql.addQualifier(FIELD_ORDER_ORIGINAL_WORKSTATION_ID + " = " + makeSafeString(itemStatus.getOriginalTransactionId().getWorkstationID()) );
            
            connection.execute(sql.getSQLString());

            int count = connection.getUpdateCount();
            if (count <= 0)
            {
                logger.warn( "No Order Line Item Tax is available to restore to it's pre-void State.");
            }
        }
        
        if (logger.isDebugEnabled()) logger.debug("End updateOrderItemDiscountAmount()");
    }

    /**
     * Returns SQL-formatted workstation identifier from transaction ID object.
     * 
     * @param transactionID object
     * @return SQL-formatted workstation identifier
     */
    protected String getWorkstationID(TransactionIDIfc transactionID)
    {
        return (getWorkstationID(transactionID.getWorkstationID()));
    }

    /**
     * Returns SQL-formatted workstation identifier from string.
     * 
     * @param input string
     * @return SQL-formatted workstation identifier
     */
    protected String getWorkstationID(String input)
    {
        StringBuffer sb = new StringBuffer("'");
        sb.append(input);
        sb.append("'");
        return (sb.toString());
    }

    /**
     * Returns the SQL-formatted store ID from the transaction ID object.
     * 
     * @param transactionID transaction ID object
     * @return the sql-formatted store ID
     */
    protected String getStoreID(TransactionIDIfc transactionID)
    {
        return (getStoreID(transactionID.getStoreID()));
    }

    /**
     * Returns the store ID
     * 
     * @param storeID The store ID
     * @return the store ID
     */
    protected String getStoreID(String storeID)
    {
        return ("'" + storeID + "'");
    }

    /**
     * Returns the transaction sequence number
     * 
     * @param transaction a pos transaction
     * @return The transaction sequence number
     */
    public String getTransactionSequenceNumber(TransactionIDIfc transactionID)
    {
        return (String.valueOf(transactionID.getSequenceNumber()));
    }

}
