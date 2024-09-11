/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveOrderLineItems.java /main/25 2014/07/17 15:09:41 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   07/15/14 - Save original line number for order line item.
 *    yiqzhao   06/11/14 - Handle new columns for original transaction id for
 *                         order.
 *    sgu       01/14/13 - process pickup or cancel for store order items
 *    sgu       01/07/13 - add quantity pending
 *    jswan     12/13/12 - Modified to prorate discount and tax for returns of
 *                         order line items.
 *    sgu       10/16/12 - rename FIELD_ITEM_QUANTITY_PICKED to
 *                         FIELD_ITEM_QUANTITY_PICKED_UP
 *    sgu       10/16/12 - clean up order item quantities
 *    sgu       09/19/12 - add completed and cancelled amount at order line
 *                         item level
 *    sgu       05/15/12 - added order line sequence number
 *    sgu       05/15/12 - remove column LN_ITM_REF from order line item tables
 *    sgu       05/04/12 - refactor OrderStatus to support store order and
 *                         xchannel order
 *    sgu       04/18/12 - enhance order item tables to support xc
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/3/2007 11:57:43 PM   Sandy Gu
 *         Enhance transaction persistence layer to store inclusive tax
 *    4    360Commerce 1.3         1/25/2006 4:11:23 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:49 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:03 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:17    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:44     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:49     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:03     Robert Pearse
 *
 *   Revision 1.7  2004/08/17 23:19:44  crain
 *   @scr 6843 Order table has DC_DY_BSN_CHG with zero time component
 *
 *   Revision 1.6  2004/04/09 16:55:44  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:35  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:44  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:21  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:39:56   msg
 * Initial revision.
 *
 *    Rev 1.3   22 May 2002 18:06:42   sfl
 * Updated the date format during saving and updating
 * to match the yyyy-mm-dd format.
 * Resolution for POS SCR-1623: Feature_Dirty_Data_POS
 *
 *    Rev 1.2   22 May 2002 16:42:00   jbp
 * modification for compliance with DB2
 * Resolution for POS SCR-1693: Special Order - Void of Pickup gets stuck in the queue (db2)
 *
 *    Rev 1.1   Mar 18 2002 22:48:30   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:20   msg
 * Initial revision.
 *
 *    Rev 1.0   28 Jan 2002 15:42:02   jbp
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLDeleteStatement;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.common.utility.Util;

/**
 * This class is the data operation for saving order transaction line items to
 * the database.
 *
 * @version $Revision: /main/25 $
 */
public class JdbcSaveOrderLineItems extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -7436124639941268333L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveOrderLineItems.class);

    /**
     * Class constructor.
     */
    public JdbcSaveOrderLineItems()
    {
        setName("JdbcSaveOrderLineItems");
    }

    /**
     * Execute the SQL statements against the database.
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
                    "JdbcSaveOrderLineItems.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        OrderTransactionIfc transaction =
          (OrderTransactionIfc) action.getDataObject();

        // save order line items
        saveOrderLineItems(connection, transaction);

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcSaveOrderLineItems.execute()");
    }

    /**
       Saves order line items.
       @param dataConnection connection to database
       @param orderTransaction order transaction
       @exception DataException thrown if error occurs
     */
    public void saveOrderLineItems(JdbcDataConnection dataConnection,
                                   OrderTransactionIfc orderTransaction)
                                   throws DataException
    {
        // insert new line items into order line item table
        AbstractTransactionLineItemIfc[] lineItems =
          orderTransaction.getLineItems();
        int numItems = 0;
        if (lineItems != null)
        {
            numItems = lineItems.length;
        }

        // loop through line items
        for (int i = 0; i < numItems; i++)
        {
            SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc) lineItems[i];
            if (lineItem.getOrderItemStatus().isCrossChannelItem())
                continue;

            if (orderTransaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE &&
               orderTransaction.getOrderStatus().getStatus().getStatus() == OrderConstantsIfc.ORDER_STATUS_NEW)
            {
                insertOrderLineItem(dataConnection,
                                    orderTransaction,
                                    lineItem,
                                    i);
            }
            else
            {
                updateOrderLineItem(dataConnection,
                                    orderTransaction,
                                    lineItem,
                                    i);
            }
        }
    }

    /**
       Removes entries from order-line-item table.
       @param dataConnection connection to database
       @param orderTransaction order transaction
       @exception DataException thrown if error occurs
     */
    public void removeOrderLineItems(JdbcDataConnection dataConnection,
                                     OrderTransactionIfc orderTransaction)
                                     throws DataException
    {
        SQLDeleteStatement sql = new SQLDeleteStatement();

        sql.setTable(TABLE_ORDER_ITEM);

        sql.addQualifier(FIELD_ORDER_ID,
                         inQuotes(orderTransaction.getOrderID()));
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                logger.error(de);
                throw de;
            }
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "removeOrderLineItems", e);
        }

    }

    /**
       Inserts an order line item. <P>
       @param dataConnection connection to database
       @param orderTransaction order transaction
       @param lineItem line item
       @param lineItemSequenceNumber index into line item array
       @exception DataException thrown if error occurs
     */
    public void insertOrderLineItem(JdbcDataConnection dataConnection,
                                    OrderTransactionIfc orderTransaction,
                                    SaleReturnLineItemIfc lineItem,
                                    int lineItemSequenceNumber)
                                    throws DataException
    {
        // pull out order status
        OrderStatusIfc orderStatus = orderTransaction.getOrderStatus();
        OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        TransactionIDIfc transactionId = itemStatus.getOriginalTransactionId();

        // set up SQL
        SQLInsertStatement sql = new SQLInsertStatement();
        sql.setTable(TABLE_ORDER_ITEM);

        sql.addColumn(FIELD_ORDER_ID,
                      inQuotes(orderTransaction.getOrderID()));
        sql.addColumn(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER,
                      lineItem.getOrderLineReference());
        sql.addColumn(FIELD_ORDER_ORIGINAL_STORE_ID, makeSafeString(transactionId.getStoreID()));
        sql.addColumn(FIELD_ORDER_ORIGINAL_WORKSTATION_ID, makeSafeString(transactionId.getWorkstationID()));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE,
                      dateToSQLDateString(orderStatus.getTimestampBegin().dateValue()));
        sql.addColumn(FIELD_ITEM_STATUS,
                      Integer.toString(itemStatus.getStatus().getStatus()));
        sql.addColumn(FIELD_ITEM_STATUS_PREVIOUS,
                      Integer.toString(itemStatus.getStatus().getPreviousStatus()));
        sql.addColumn(FIELD_PARTY_ID, "0");
        sql.addColumn(FIELD_POS_ITEM_ID,
                      inQuotes(lineItem.getItemID()));
        sql.addColumn(FIELD_POS_DEPARTMENT_ID,
                      inQuotes(lineItem.getPLUItem().getDepartmentID()));
        sql.addColumn(FIELD_TAX_GROUP_ID,
                      Integer.toString(lineItem.getTaxGroupID()));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY,
                      getItemQuantity(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT,
                      getItemExtendedAmount(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT,
                      lineItem.getItemTaxAmount().getStringValue());
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT,
          	  		  lineItem.getItemInclusiveTaxAmount().getStringValue());
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER,
                Integer.toString(lineItemSequenceNumber));
        sql.addColumn(FIELD_ITEM_DESCRIPTION,
                      getItemDescription(lineItem));
        if (itemStatus.getStatus().getLastStatusChange() != null)
        {
            sql.addColumn(FIELD_ORDER_STATUS_CHANGE,
                          dateToSQLDateString(itemStatus.getStatus().getLastStatusChange()));
        }
    	sql.addColumn(FIELD_ITEM_QUANTITY_PICKED_UP,
    			itemStatus.getQuantityPickedUp().toString());
    	sql.addColumn(FIELD_ITEM_QUANTITY_SHIPPED,
    			itemStatus.getQuantityShipped().toString());
    	sql.addColumn(FIELD_ITEM_QUANTITY_NEW,
    			itemStatus.getQuantityNew().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_PENDING,
                itemStatus.getQuantityPending().toString());
    	sql.addColumn(FIELD_ITEM_QUANTITY_AVAILABLE,
    			itemStatus.getQuantityPicked().toString());
    	sql.addColumn(FIELD_ITEM_QUANTITY_CANCELLED,
    			itemStatus.getQuantityCancelled().toString());
    	sql.addColumn(FIELD_ITEM_QUANTITY_RETURNED,
    			itemStatus.getQuantityReturned().toString());
        sql.addColumn(FIELD_ORDER_COMPLETED_AMOUNT,
                itemStatus.getCompletedAmount().toString());
        sql.addColumn(FIELD_ORDER_CANCELLED_AMOUNT,
                itemStatus.getCancelledAmount().toString());
        sql.addColumn(FIELD_ORDER_RETURNED_AMOUNT,
                itemStatus.getReturnedAmount().toString());
        sql.addColumn(FIELD_ORDER_DEPOSIT_AMOUNT,
                itemStatus.getDepositAmount().toString());
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER,
                      getTransactionSequenceNumber(orderTransaction));

        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(orderTransaction));

        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(orderTransaction));
        
        sql.addColumn(FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER,String.valueOf(transactionId.getSequenceNumber()));
        sql.addColumn(FIELD_ORDER_ORIGINAL_TRANSACTION_BUSINESS_DATE, dateToSQLDateString(itemStatus.getOriginalBusinessDate()));  
        sql.addColumn(FIELD_ORIGINAL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, String.valueOf(itemStatus.getOriginalLineNumber()));

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
            logger.error(
                         "" + Util.throwableToString(e) + "");

            throw new DataException(DataException.UNKNOWN, "insertOrderLineItem", e);
        }
    }

    /**
       Updates an order line item. <P>
       @param dataConnection connection to database
       @param orderTransaction order transaction
       @param lineItem line item
       @param lineItemSequenceNumber index into line item array
       @exception DataException thrown if error occurs
     */
    public void updateOrderLineItem(JdbcDataConnection dataConnection,
                                    OrderTransactionIfc orderTransaction,
                                    SaleReturnLineItemIfc lineItem,
                                    int lineItemSequenceNumber)
                                    throws DataException
    {
        // pull out order status
        OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();

        SQLUpdateStatement sql = new SQLUpdateStatement();

        sql.setTable(TABLE_ORDER_ITEM);

        sql.addColumn(FIELD_ITEM_STATUS,
                      Integer.toString(itemStatus.getStatus().getStatus()));
        sql.addColumn(FIELD_ITEM_STATUS_PREVIOUS,
                      Integer.toString(itemStatus.getStatus().getPreviousStatus()));
        sql.addColumn(FIELD_TAX_GROUP_ID,
                      Integer.toString(lineItem.getTaxGroupID()));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY,
                      getItemQuantity(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT,
                      getItemExtendedAmount(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT,
                      lineItem.getItemTaxAmount().getStringValue());
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT,
    	  		  lineItem.getItemInclusiveTaxAmount().getStringValue());
        sql.addColumn(FIELD_ITEM_DESCRIPTION,
                      getItemDescription(lineItem));
        if (itemStatus.getStatus().getLastStatusChange() != null)
        {
            sql.addColumn(FIELD_ORDER_STATUS_CHANGE,
                          dateToSQLDateString(itemStatus.getStatus().getLastStatusChange()));
        }
    	sql.addColumn(FIELD_ITEM_QUANTITY_PICKED_UP,
    			itemStatus.getQuantityPickedUp().toString());
    	sql.addColumn(FIELD_ITEM_QUANTITY_SHIPPED,
    			itemStatus.getQuantityShipped().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_NEW,
                itemStatus.getQuantityNew().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_PENDING,
                itemStatus.getQuantityPending().toString());
    	sql.addColumn(FIELD_ITEM_QUANTITY_AVAILABLE,
    			itemStatus.getQuantityPicked().toString());
    	sql.addColumn(FIELD_ITEM_QUANTITY_CANCELLED,
    			itemStatus.getQuantityCancelled().toString());
    	sql.addColumn(FIELD_ITEM_QUANTITY_RETURNED,
    			itemStatus.getQuantityReturned().toString());
        sql.addColumn(FIELD_ORDER_COMPLETED_AMOUNT,
                itemStatus.getCompletedAmount().toString());
        sql.addColumn(FIELD_ORDER_CANCELLED_AMOUNT,
                itemStatus.getCancelledAmount().toString());
        sql.addColumn(FIELD_ORDER_RETURNED_AMOUNT,
                itemStatus.getReturnedAmount().toString());
        sql.addColumn(FIELD_ORDER_DEPOSIT_AMOUNT,
                itemStatus.getDepositAmount().toString());

        // add qualifiers for the update
        sql.addQualifier(FIELD_ORDER_ID + " = '" + orderTransaction.getOrderID() + "'");
        sql.addQualifier(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER + " = " + lineItem.getOrderLineReference() );
        sql.addQualifier(FIELD_ORDER_ORIGINAL_STORE_ID + " = " + getStoreID(itemStatus.getOriginalTransactionId()) );
        sql.addQualifier(FIELD_ORDER_ORIGINAL_WORKSTATION_ID + " = " + getWorkstationID(itemStatus.getOriginalTransactionId()) );
        
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
            logger.error(
                         "" + Util.throwableToString(e) + "");

            throw new DataException(DataException.UNKNOWN, "insertOrderLineItem", e);
        }
    }
    
    /**
      Returns the transaction sequence number
      <P>
      @param  transaction     a pos transaction
      @return  The transaction sequence number
     */
    public String getTransactionSequenceNumber(TransactionIDIfc transactionID)
    {
    	return(String.valueOf(transactionID.getSequenceNumber()));
    }

    /**
       Returns the transaction sequence number
       <P>
       @param  transaction     a pos transaction
       @return  The transaction sequence number
     */
    public String getTransactionSequenceNumber(TransactionIfc transaction)
    {
        return(String.valueOf(transaction.getTransactionSequenceNumber()));
    }

    /**
       Returns the Store ID
       <P>
       @param  transaction     a pos transaction
       @return  The Store ID
     */
    public String getStoreID(TransactionIDIfc transactionID)
    {
    	return("'" + transactionID.getStoreID() + "'");
    }

    /**
      Returns the Store ID
      <P>
      @param  transaction     a pos transaction
      @return  The Store ID
     */
    public String getStoreID(TransactionIfc transaction)
    {
    	return("'" + transaction.getWorkstation().getStoreID() + "'");
    }
    
    /**
      Returns the Workstation ID
      <P>
      @param  transaction     a pos transaction
      @return  The Workstation ID
     */
    public String getWorkstationID(TransactionIDIfc transactionID)
    {
    	return("'" + transactionID.getWorkstationID() + "'");
    }

    /**
       Returns the Workstation ID
       <P>
       @param  transaction     a pos transaction
       @return  The Workstation ID
     */
    public String getWorkstationID(TransactionIfc transaction)
    {
        return("'" + transaction.getWorkstation().getWorkstationID() + "'");
    }

    /**
        Returns the extended amount of the item.
        <p>
        @param  lineItem    The OrderLineItem
        @return the extended amount of the item.
     */
    protected String getItemExtendedAmount(SaleReturnLineItemIfc lineItem)
    {
        return(lineItem.getItemPrice().getExtendedSellingPrice().getStringValue());
    }

    /**
        Returns the item ID for the OrderLineItem.
        <p>
        @param  lineItem    The OrderLineItem
        @return the item ID for the OrderLineItem.
     */
    protected String getItemID(SaleReturnLineItemIfc lineItem)
    {
        return("'" + lineItem.getPLUItem().getItemID() + "'");
    }

    /**
        Returns the quantity of the item.
        <p>
        @param  lineItem    The OrderLineItem
        @return the quantity of the item.
     */
    protected String getItemQuantity(SaleReturnLineItemIfc lineItem)
    {
        return(lineItem.getItemQuantityDecimal().toString());
    }

    /**
        Returns the description of the item. This is transactional data
        <p>
        @param  lineItem    The OrderLineItem
        @return the description of the item.
     */
    protected String getItemDescription(SaleReturnLineItemIfc lineItem)
    {
        return(makeSafeString(lineItem.getPLUItem().getDescription(LocaleMap.getLocale(LocaleMap.DEFAULT))));
    }
}
