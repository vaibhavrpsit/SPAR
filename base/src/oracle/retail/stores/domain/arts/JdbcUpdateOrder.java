/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateOrder.java /main/24 2014/07/17 15:09:41 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   07/15/14 - Save original line number for order line item.
 *    yiqzhao   06/11/14 - Handle new columns for original transaction id for
 *                         order.
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    sgu       01/14/13 - process pickup or cancel for store order items
 *    sgu       01/07/13 - add quantity pending
 *    sgu       10/16/12 - rename FIELD_ITEM_QUANTITY_PICKED to
 *                         FIELD_ITEM_QUANTITY_PICKED_UP
 *    sgu       10/16/12 - clean up order item quantities
 *    sgu       05/15/12 - added order line sequence number
 *    sgu       05/15/12 - remove column LN_ITM_REF from order line item tables
 *    sgu       04/18/12 - enhance order item tables to support xc
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *    acadar    10/30/08 - use localized reason codes for item and transaction
 *                         discounts
 * ===========================================================================
     $Log:
      11   360Commerce 1.10        3/3/2008 10:01:04 AM   Siva Papenini
           CR:30,232 : Updated FIELD_ORDER_LINE_REFERENCE value with
           orderItem.getOrderLineReference() in updatePriceModifier method.
      10   360Commerce 1.9         5/30/2007 9:03:21 AM   Anda D. Cadar   code
           cleanup
      9    360Commerce 1.8         5/18/2007 9:16:35 AM   Anda D. Cadar   use
           decimalValue toString when saving amounts in the database
      8    360Commerce 1.7         5/3/2007 11:57:43 PM   Sandy Gu
           Enhance transaction persistence layer to store inclusive tax
      7    360Commerce 1.6         6/14/2006 1:14:52 PM   Brendan W. Farrell
           Update.
      6    360Commerce 1.5         4/27/2006 7:26:59 PM   Brett J. Larsen CR
           17307 - remove inventory functionality - stage 2
      5    360Commerce 1.4         1/25/2006 4:11:27 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      4    360Commerce 1.3         1/22/2006 11:41:24 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:28:46 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:22:52 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:06 PM  Robert Pearse
     $:
      4    .v700     1.2.1.0     11/16/2005 16:26:22    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:28:46     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:52     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:06     Robert Pearse
     $
     Revision 1.9  2004/08/17 23:19:44  crain
     @scr 6843 Order table has DC_DY_BSN_CHG with zero time component

     Revision 1.8  2004/06/29 21:58:58  aachinfiev
     Merge the changes for inventory & POS integration

     Revision 1.7  2004/04/09 16:55:44  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.6  2004/02/17 17:57:36  bwf
     @scr 0 Organize imports.

     Revision 1.5  2004/02/17 16:18:45  rhafernik
     @scr 0 log4j conversion

     Revision 1.4  2004/02/12 17:13:19  mcs
     Forcing head revision

     Revision 1.3  2004/02/11 23:25:22  bwf
     @scr 0 Organize imports.

     Revision 1.2  2004/02/11 22:00:26  baa
     @scr 0 Add == to comparison

     Revision 1.1  2004/02/11 01:04:28  cschellenger
     Initial revision

 *
 *    Rev 1.0   Aug 29 2003 15:33:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jun 10 2002 11:14:58   epd
 * Merged in changes for Oracle
 * Resolution for Domain SCR-83: Merging database fixes into base code
 *
 *    Rev 1.5   Jun 07 2002 17:47:46   epd
 * Merging in fixes made for McDonald's Oracle demo
 * Resolution for Domain SCR-83: Merging database fixes into base code
 *
 *    Rev 1.4   19 May 2002 00:36:46   jbp
 * changes for db2
 * Resolution for POS SCR-1668: Return - Cannot retrieve any receipts to do a return
 *
 *    Rev 1.3   25 Apr 2002 10:27:44   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 *
 *    Rev 1.2   Apr 02 2002 18:58:26   mpm
 * Corrected instantiation, cloning of BigDecimal.
 * Resolution for Domain SCR-46: Correct initialization of BigDecimal objects
 *
 *    Rev 1.1   Mar 18 2002 22:49:50   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:09:24   msg
 * Initial revision.
 *
 *    Rev 1.10   Mar 07 2002 10:48:40   dfh
 * updates to save the newest order location
 * Resolution for POS SCR-1522: Location for a Filled Special Order does not update correctly
 *
 *    Rev 1.9   Feb 05 2002 16:33:54   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.8   Jan 25 2002 16:55:02   dfh
 * use order line reference
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.7   Jan 22 2002 22:10:00   dfh
 * order line item last status change format change to yyyymmdd in db
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.6   15 Jan 2002 18:28:06   cir
 * Updated for SaleReturnLineItem
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.5   28 Oct 2001 13:23:00   jbp
 * updated sql statements to conform with standards.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.4   14 Dec 2001 07:14:28   mpm
 * Fixed another instance of getLineItems().
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.2   27 Nov 2001 06:23:30   mpm
 * Modifications to support data operations for special order.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   13 Nov 2001 07:03:14   mpm
 * Installed support for ItemContainerProxy.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 20 2001 15:57:46   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.util.List;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemDiscountStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderItemTaxStatusIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * @version $Revision: /main/24 $
 */
public class JdbcUpdateOrder extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 8780085631223112078L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcUpdateOrder.class);

    /**
     * Class constructor.
     */
    public JdbcUpdateOrder()
    {
        super();
        setName("JdbcUpdateOrder");
    }

    /**
     * Executes the SQL statements against the database.
     *
     * @param dataTransaction
     * @param dataConnection
     * @param action
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateOrder.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // grab arguments
        OrderIfc order = (OrderIfc) action.getDataObject();
        updateOrder(connection, order);

        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateOrder.execute");
    }

    /**
     * Executes the SQL statements against the database.
     *
     * @param dataConnection
     * @param order object stati and location to update and enddate
     * @exception DataException upon error
     */
    public void updateOrder(JdbcDataConnection connection,
                            OrderIfc order) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateOrder.updateOrder()");

        SQLUpdateStatement sql = new SQLUpdateStatement();

        // calculate order total - do not include canceled items
        BigDecimal orderTotal = BigDecimal.ZERO;

        for (int i = 0; i < order.getOrderLineItems().length; i++)
        {
            if (((SaleReturnLineItemIfc) order.retrieveItemByIndex(i)).getOrderItemStatus().getStatus().getStatus() !=
                  OrderConstantsIfc.ORDER_ITEM_STATUS_CANCELED)
            {
                orderTotal = orderTotal.add(new BigDecimal
                                            (order.retrieveItemByIndex(i).getLineItemAmount().getDecimalValue().toString()));//toDecimalFormattedString()));
            }
        }

        // set table
        sql.setTable(TABLE_ORDER);

        // add columns and values
        sql.addColumn(FIELD_ORDER_STATUS, order.getOrderStatus());
        sql.addColumn(FIELD_ORDER_STATUS_PREVIOUS,order.getPreviousOrderStatus());
        sql.addColumn(FIELD_ORDER_STATUS_CHANGE, dateToSQLDateString(order.getLastStatusChange()));
        sql.addColumn(FIELD_ORDER_TOTAL,orderTotal.toString());
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());

        // add location if not empty - length > 0
        if (order.getStatus().getLocation().length() > 0)
        {
            sql.addColumn(FIELD_ORDER_LOCATION, "'" + order.getStatus().getLocation() + "'");
        }

        // add qualifiers for the order ID
        sql.addQualifier(FIELD_ORDER_ID + " = '" + order.getOrderID() + "'");

        try
        {
            connection.execute(sql.getSQLString());
        }

        catch (DataException de)
        {
            logger.warn(de);
            if (de.getErrorCode() == DataException.UNKNOWN)
            {
                throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
            }

            throw de;
        }

        if (0 >= connection.getUpdateCount())
        {
            logger.warn( "No orders updated ");
            throw new DataException(DataException.NO_DATA, "No orders updated ");
        }

        // update the order line item price modifiers
        AbstractTransactionLineItemIfc[] orderItems = order.getLineItems();
        for (int i = 0;i < orderItems.length;i++ )
        {
            try
            {
                updateLineItem(connection, order.getOrderID(), (SaleReturnLineItemIfc)orderItems[i]);
            }
            catch (DataException de)
            {
                logger.warn(de);
                if (de.getErrorCode() == DataException.UNKNOWN)
                {
                    throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
                }

                throw de;
            }
        }
        if (logger.isDebugEnabled()) logger.debug( "JdbcOrderWriteDataTransaction.updateOrder()");
    }

    /**
     * Updates order line items in the database.
     *
     * @param dataConnection
     * @param orderItem order item
     * @exception DataException upon error
     */
    public void updateLineItem(JdbcDataConnection connection, String orderID,
        SaleReturnLineItemIfc orderItem)
        throws DataException
    {
        // update orderlineitems status
        SQLUpdateStatement sqlnew = new SQLUpdateStatement();

        // set table
        sqlnew.setTable(TABLE_ORDER_ITEM);

        // add columns and values
        sqlnew.addColumn(FIELD_ITEM_STATUS, orderItem.getOrderItemStatus().getStatus().getStatus());
        sqlnew.addColumn(FIELD_ITEM_STATUS_PREVIOUS, orderItem.getOrderItemStatus().getStatus().getPreviousStatus() );
        sqlnew.addColumn(FIELD_ORDER_STATUS_CHANGE,
                dateToSQLDateString(orderItem.getOrderItemStatus().getStatus().getLastStatusChange()));
        sqlnew.addColumn(FIELD_ITEM_QUANTITY_PICKED_UP, orderItem.getOrderItemStatus().getQuantityPickedUp().toString());
        sqlnew.addColumn(FIELD_ITEM_QUANTITY_SHIPPED, orderItem.getOrderItemStatus().getQuantityShipped().toString() );
        sqlnew.addColumn(FIELD_ITEM_QUANTITY_NEW, orderItem.getOrderItemStatus().getQuantityNew().toString());
        sqlnew.addColumn(FIELD_ITEM_QUANTITY_PENDING, orderItem.getOrderItemStatus().getQuantityPending().toString());
        sqlnew.addColumn(FIELD_ITEM_QUANTITY_AVAILABLE, orderItem.getOrderItemStatus().getQuantityPicked().toString());
        sqlnew.addColumn(FIELD_ITEM_QUANTITY_CANCELLED, orderItem.getOrderItemStatus().getQuantityCancelled().toString());
        sqlnew.addColumn(FIELD_ITEM_QUANTITY_RETURNED, orderItem.getOrderItemStatus().getQuantityReturned().toString());
        sqlnew.addColumn(FIELD_ORDER_COMPLETED_AMOUNT, orderItem.getOrderItemStatus().getCompletedAmount().toString());
        sqlnew.addColumn(FIELD_ORDER_CANCELLED_AMOUNT, orderItem.getOrderItemStatus().getCancelledAmount().toString());
        sqlnew.addColumn(FIELD_ORDER_RETURNED_AMOUNT, orderItem.getOrderItemStatus().getReturnedAmount().toString());
        sqlnew.addColumn(FIELD_ORDER_DEPOSIT_AMOUNT, orderItem.getOrderItemStatus().getDepositAmount().toString());

        // line item values
        sqlnew.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY,  orderItem.getItemQuantityDecimal().toString());
        sqlnew.addColumn(FIELD_POS_ITEM_ID, "'" + orderItem.getPLUItem().getItemID() + "'");
        sqlnew.addColumn(FIELD_TAX_GROUP_ID, orderItem.getPLUItem().getTaxGroupID());
        sqlnew.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT, orderItem.getItemPrice().getExtendedSellingPrice().getDecimalValue().toString());//toDecimalFormattedString());
        sqlnew.addColumn(FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT, orderItem.getItemTaxAmount().getDecimalValue().toString());//toDecimalFormattedString());
        sqlnew.addColumn(FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT, orderItem.getItemInclusiveTaxAmount().getDecimalValue().toString());//toDecimalFormattedString());

        if (orderItem.getOrderItemStatus().getStatus().getLastStatusChange() != null)
        {
            sqlnew.addColumn(FIELD_ORDER_STATUS_CHANGE,
                          dateToSQLDateString(orderItem.getOrderItemStatus().getStatus().getLastStatusChange()));
        }

        // add qualifiers for the
        sqlnew.addQualifier(FIELD_ORDER_ID + " = '" + orderID + "'");
        sqlnew.addQualifier(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER  + " = " + orderItem.getOrderLineReference());
        sqlnew.addQualifier(FIELD_ORDER_ORIGINAL_STORE_ID + " = " + makeSafeString(orderItem.getOrderItemStatus().getOriginalTransactionId().getStoreID()) );
        sqlnew.addQualifier(FIELD_ORDER_ORIGINAL_WORKSTATION_ID + " = " + makeSafeString(orderItem.getOrderItemStatus().getOriginalTransactionId().getWorkstationID()) );
        try
        {
            connection.execute(sqlnew.getSQLString());
        }

        catch (DataException de)
        {
            logger.warn(de);
            if (de.getErrorCode() == DataException.UNKNOWN)
            {
                throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
            }

            throw de;
        }

        if (0 >= connection.getUpdateCount())
        {
            logger.warn( "No order line item updated ");
            throw new DataException(DataException.NO_DATA, "No order line item updated ");
        }

        List<OrderItemDiscountStatusIfc> discStatusList = orderItem.getOrderItemStatus().getDiscountStatusList();
        for (OrderItemDiscountStatusIfc oids : discStatusList)
        {
            updateLineItemDiscount(connection, orderID, orderItem, oids);
        }

        List<OrderItemTaxStatusIfc> taxStatusList = orderItem.getOrderItemStatus().getTaxStatusList();
        for(OrderItemTaxStatusIfc oits: taxStatusList)
        {
            updateLineItemTax(connection, orderID, orderItem, oits);
        }
    }

    /**
     * This method updates the discount related data in the Order Line Item
     * Retail Price Modifier table.  This table is a part of the Order group of
     * tables and maintains current state of the discount amounts - total, completed,
     * cancelled, and returned.
     *
     * As with all the other Order Tables, it contians in-store order information only.
     *
     * @param dataConnection
     * @param orderTransaction
     * @param lineItem
     * @param discountLineItem
     * @throws DataException
     */
    protected void updateLineItemDiscount(JdbcDataConnection dataConnection, String orderID,
            SaleReturnLineItemIfc lineItem, OrderItemDiscountStatusIfc oids) throws DataException
    {
    	OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        SQLUpdateStatement sql = new SQLUpdateStatement();
        sql.setTable(TABLE_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER);

        sql.addColumn(FIELD_TOTAL_DISCOUNT_AMOUNT, oids.getTotalAmount().toString());
        sql.addColumn(FIELD_COMPLETED_DISCOUNT_AMOUNT, oids.getCompletedAmount().toString());
        sql.addColumn(FIELD_CANCELLED_DISCOUNT_AMOUNT, oids.getCancelledAmount().toString());
        sql.addColumn(FIELD_RETURNED_DISCOUNT_AMOUNT, oids.getReturnedAmount().toString());

        sql.addQualifier(FIELD_ORDER_ID, inQuotes(orderID));
        sql.addQualifier(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
        sql.addQualifier(FIELD_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER, oids.getLineNumber());
        sql.addQualifier(FIELD_ORDER_ORIGINAL_STORE_ID + " = " + makeSafeString(itemStatus.getOriginalTransactionId().getStoreID()) );
        sql.addQualifier(FIELD_ORDER_ORIGINAL_WORKSTATION_ID + " = " + makeSafeString(itemStatus.getOriginalTransactionId().getWorkstationID()) );

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");

            throw new DataException(DataException.UNKNOWN, "updateLineItemDiscount", e);
        }
     }

    /**
     * Update the totals in the Order Line Item Tax table.
     * @param dataConnection
     * @param orderTransaction
     * @param lineItem
     * @throws DataException
     */
    protected void updateLineItemTax(JdbcDataConnection dataConnection, String orderID,
            SaleReturnLineItemIfc lineItem, OrderItemTaxStatusIfc oits) throws DataException
    {
        // set up SQL
    	OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        SQLUpdateStatement sql = new SQLUpdateStatement();
        sql.setTable(TABLE_ORDER_LINE_ITEM_TAX);

        sql.addColumn(FIELD_TOTAL_TAX_AMOUNT, oits.getTotalAmount().toString());
        sql.addColumn(FIELD_COMPLETED_TAX_AMOUNT, oits.getCompletedAmount().toString());
        sql.addColumn(FIELD_CANCELLED_TAX_AMOUNT, oits.getCancelledAmount().toString());
        sql.addColumn(FIELD_RETURNED_TAX_AMOUNT, oits.getReturnedAmount().toString());

        sql.addQualifier(FIELD_ORDER_ID, inQuotes(orderID));
        sql.addQualifier(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
        sql.addQualifier(FIELD_TAX_AUTHORITY_ID, oits.getAuthorityID());
        sql.addQualifier(FIELD_TAX_GROUP_ID, oits.getTaxGroupID());
        sql.addQualifier(FIELD_TAX_TYPE, oits.getTypeCode());
        sql.addQualifier(FIELD_ORDER_ORIGINAL_STORE_ID + " = " + makeSafeString(itemStatus.getOriginalTransactionId().getStoreID()) );
        sql.addQualifier(FIELD_ORDER_ORIGINAL_WORKSTATION_ID + " = " + makeSafeString(itemStatus.getOriginalTransactionId().getWorkstationID()) );

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");

            throw new DataException(DataException.UNKNOWN, "updateLineItemTax", e);
        }
      }
}
