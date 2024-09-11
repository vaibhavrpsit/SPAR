/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcCreateOrder.java /main/18 2013/04/16 13:32:35 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    sgu       05/15/12 - use order line item sequence number
 *    sgu       04/18/12 - enhance order item tables to support xc
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
 *    6    360Commerce 1.5         5/3/2007 11:57:43 PM   Sandy Gu
 *         Enhance transaction persistence layer to store inclusive tax
 *    5    360Commerce 1.4         4/27/2006 7:26:57 PM   Brett J. Larsen CR
 *         17307 - remove inventory functionality - stage 2
 *    4    360Commerce 1.3         1/25/2006 4:11:06 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:36 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:36 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:53 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:02    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:36     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:36     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:53     Robert Pearse
 *
 *   Revision 1.10  2004/08/17 23:19:44  crain
 *   @scr 6843 Order table has DC_DY_BSN_CHG with zero time component
 *
 *   Revision 1.9  2004/08/12 12:30:20  kll
 *   @scr 0: access in a static way and fix SQLInsertStatement deprecation
 *
 *   Revision 1.8  2004/08/12 12:18:47  kll
 *   @scr 0: access field in a static way
 *
 *   Revision 1.7  2004/06/29 21:58:58  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.6  2004/04/09 16:55:44  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:35  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:30:22   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:35:42   msg
 * Initial revision.
 *
 *    Rev 1.2   22 May 2002 16:42:02   jbp
 * modification for compliance with DB2
 * Resolution for POS SCR-1693: Special Order - Void of Pickup gets stuck in the queue (db2)
 *
 *    Rev 1.1   Mar 18 2002 22:46:32   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:06:42   msg
 * Initial revision.
 *
 *    Rev 1.3   Feb 05 2002 16:33:26   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.2   14 Dec 2001 07:11:04   mpm
 * Handled changed signature of getLineItems() (now getOrderLineItems())
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   27 Nov 2001 06:23:24   mpm
 * Modifications to support data operations for special order.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 20 2001 15:58:12   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * @version $Revision: /main/18 $
 * @deprecated as of 14.0.  This class is no longer needed
 */
public class JdbcCreateOrder extends JdbcDataOperation implements ARTSDatabaseIfc
{
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcCreateOrder.class);

    /**
       Class constructor.
     */
    public JdbcCreateOrder()
    {
        super();
        setName("JdbcCreateOrder");
    }

    /**
       Executes the SQL statements against the database.
       <P>
       @param  dataTransaction
       @param  dataConnection
       @param  action
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcCreateOrder.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // grab arguments and call rerders()
        OrderIfc order = (OrderIfc)action.getDataObject();
        CreateOrder(connection, order);

        if (logger.isDebugEnabled()) logger.debug( "JdbcCreateOrder.execute");
    }

    /**
       Executes the SQL statements against the database.
       <P>
       @param  dataConnection
       @param  order  order object to save
       and enddate
       @exception DataException upon error
     */
    public void CreateOrder(JdbcDataConnection connection,
                            OrderIfc order) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcOrderCreateDataTransaction.CreateOrder()");

        try
        {
            insertOrder(connection, order);
        }
        catch (DataException de)
        {
            logger.warn(de);
            if (de.getErrorCode() == DataException.UNKNOWN)
            {
                throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
            }
            else
            {
                throw de;
            }
        }

        OrderLineItemIfc[] orderItems = order.getOrderLineItems();

        for (int i = 0;i < orderItems.length;i++ )
        {
            try
            {
                insertOrderLineItem(connection,order, orderItems[i]);
            }

            catch (DataException de)
            {
                logger.warn(de);
                if (de.getErrorCode() == DataException.UNKNOWN)
                {
                    throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
                }
                else
                {
                    throw de;
                }

            }
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcOrderCreateDataTransaction.CreateOrder()");
    }

    /**
       Inserts into the order table.
       <P>
       @param  dataConnection  the connection to the data source
       @param  order  The order to create in the database
       @exception DataException
     */
    public void insertOrder(JdbcDataConnection dataConnection,
                            OrderIfc order)
        throws DataException
    {
        // get an sql object
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_ORDER);

        // Fields
        sql.addColumn(FIELD_ORDER_ID, getOrderID(order));                       // unique order ID
        sql.addColumn(FIELD_ORDER_STATUS, Integer.toString(OrderConstantsIfc.ORDER_STATUS_NEW));    // order status
        sql.addColumn(FIELD_CUSTOMER_ID, getCustomerID(order));                 // customer id key
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDate(order));         // created date
        sql.addColumn(FIELD_ORDER_STATUS_PREVIOUS, Integer.toString(OrderConstantsIfc.ORDER_STATUS_NEW));   // previous order status
        sql.addColumn(FIELD_RETAIL_STORE_ID, getPickupStore(order));            // pickup store number
        sql.addColumn(FIELD_ORDER_STATUS_CHANGE, getBusinessDate(order));       // created date
        sql.addColumn(FIELD_GIFT_REGISTRY_ID, getGiftRegistryID(order));        // gift registry ID
        sql.addColumn(FIELD_ORDER_BEGIN, getBeginDate(order));                  // date order was begun
        sql.addColumn(FIELD_ORDER_DESCRIPTION, getOrderDescription(order));     // description of first line in order
        sql.addColumn(FIELD_ORDER_TOTAL, getOrderTotal(order));                 // grand total of order
        sql.addColumn(FIELD_OPERATOR_ID, getOperatorID(order));                 // operator ID
        sql.addColumn(FIELD_CONTACT_FIRST_NAME, getFirstName(order));           // customer first name
        sql.addColumn(FIELD_CONTACT_MIDDLE_INITIAL, getMiddleInitial(order));   // customer middle initial
        sql.addColumn(FIELD_CONTACT_LAST_NAME, getLastName(order));             // customer last name
        sql.addColumn(FIELD_EMAIL_ADDRESS, getEmailAddress(order));             // customer email address
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_CREATION_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());

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
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "insertOrder", e);
        }
    }

    /**
       Inserts an Order Line Item.
       <P>
       Uses <code>order</code> to obtain the primary keys.
       <p>
       A detail line item of Order.
       <P>
       @param  dataConnection          Data source connection to use
       @param  order                   The order
       @param  lineItemSequenceNumber  The sequence number of the line item
       within the order
       @exception DataException upon error
     */
    public void insertOrderLineItem(JdbcDataConnection dataConnection,
                                    OrderIfc order,
                                    OrderLineItemIfc lineItem)
        throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_ORDER_ITEM);

        // Fields
        sql.addColumn(FIELD_ORDER_ID, getItemOrderID(lineItem));                                                // unique order ID
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDate(order));                                         // created date
        sql.addColumn(FIELD_ITEM_STATUS, Integer.toString(OrderConstantsIfc.ORDER_ITEM_STATUS_NEW));                     // line item status
        sql.addColumn(FIELD_ITEM_STATUS_PREVIOUS, Integer.toString(OrderConstantsIfc.ORDER_ITEM_STATUS_NEW));            // previous line item status
        sql.addColumn(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, getLineReference(lineItem));                                  // order line number, 1 relative
        sql.addColumn(FIELD_PARTY_ID, "0");                                                                     // party ID
        sql.addColumn(FIELD_POS_ITEM_ID, getItemID(lineItem));                                                  // item ID
        sql.addColumn(FIELD_POS_DEPARTMENT_ID, getItemDepartment(lineItem));                                    // department ID
        sql.addColumn(FIELD_TAX_GROUP_ID, getItemTaxGroupID(lineItem));                                         // tax group ID
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY, getItemQuantity(lineItem));                         // quantity
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT, getItemExtendedAmount(lineItem));            // extended amount
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT, getItemVATAmount(lineItem));                      // tax amount
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT, getItemInclusiveTaxAmount(lineItem));			// Inclusive tax amount
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getItemSequenceNumber(lineItem));     // item sequence number, 0 relative
        sql.addColumn(FIELD_ITEM_DESCRIPTION, getItemDescription(lineItem));                                    // item description

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
            throw new DataException(DataException.UNKNOWN, "insertOrderLineItem", e);
        }
    }

    /**
       Get OrderID from the order and return it as a string. <P>
       @param  order The order
       @return orderID String representation of OrderID
     */
    public String getOrderID(OrderIfc order)
    {
        String orderID = order.getOrderID();

        return ("'" + orderID + "'");
    }

    /**
       Get CustomerID from the order and return it as a string. <P>
       @param  order The order
       @return customerID String representation of CustomerID
     */
    public String getCustomerID(OrderIfc order)
    {
        String customerID = order.getCustomer().getCustomerID();

        return ("'" + customerID + "'");
    }

    /**
       Get BusinessDate from the order and return it as a string. <P>
       @return businessDate String representation of BusinessDate
     */
    public String getBusinessDate(OrderIfc order)
    {
        String businessDate = dateToSQLDateString(order.getTimestampBegin().dateValue());

        return (businessDate);
    }

    /**
       Get BeginDate from the order and return it as a string. <P>
       @return beginDate String representation of BeginDate
     */
    public String getBeginDate(OrderIfc order)
    {
        String beginDate = dateToSQLDateString(order.getTimestampCreated().dateValue());

        return (beginDate);
    }

    /**
       Get PickupStore from the order and return it as a string. <P>
       @return pickupStore String representation of PickupStore
     */
    public String getPickupStore(OrderIfc order)
    {
        String pickupStore = order.getShipToStore().getStoreID();

        return ("'" + pickupStore + "'");
    }

    /**
       Get GiftRegistryID from the order and return it as a string. <P>
       @return giftRegistryID String representation of GiftRegistryID
     */
    public String getGiftRegistryID(OrderIfc order)
    {
        String giftRegistryID = "null";
        if (order.getDefaultRegistry() != null)
        {
            giftRegistryID = "'" + order.getDefaultRegistry().getID() + "'";
        }

        return (giftRegistryID);
    }

    /**
       Get OrderDescription from the order and return it as a string. <P>
       @return orderDescription String representation of OrderDescription
     */
    public String getOrderDescription(OrderIfc order)
    {
        OrderLineItemIfc[] orderItems = order.getOrderLineItems();

        String orderDescription = " ";

        if (orderItems.length > 0 )
            orderDescription = orderItems[0].getItemDescription(LocaleMap.getLocale(LocaleMap.DEFAULT));

        return ("'" + orderDescription + "'");
    }

    /**
       Get OrderTotal from the order and return it as a string. <P>
       @return orderTotal String representation of OrderTotal
     */
    public String getOrderTotal(OrderIfc order)
    {
        String orderTotal = order.getTotals().getGrandTotal().getStringValue();

        return ("'" + orderTotal + "'");
    }

    /**
       Get OperatorID from the order and return it as a string. <P>
       @return operatorID String representation of OperatorID
     */
    public String getOperatorID(OrderIfc order)
    {
        EmployeeIfc salesAssociate = order.getSalesAssociate();
        String operatorID = null;
        if (salesAssociate != null)
            operatorID = salesAssociate.getEmployeeID();

        return ("'" + operatorID + "'");
    }

    /**
       Get FirstName from the order and return it as a string. <P>
       @return firstName String representation of FirstName
     */
    public String getFirstName(OrderIfc order)
    {
        String firstName = order.getCustomer().getFirstName();

        return ("'" + firstName + "'");
    }

    /**
       Get MiddleInitial from the order and return it as a string. <P>
       @return middleInitial String representation of MiddleInitial
     */
    public String getMiddleInitial(OrderIfc order)
    {
        String middleInitial = order.getCustomer().getMiddleName();

        return ("'" + middleInitial + "'");
    }

    /**
       Get LastName from the order and return it as a string. <P>
       @return lastName String representation of LastName
     */
    public String getLastName(OrderIfc order)
    {
        String lastName = order.getCustomer().getLastName();

        return ("'" + lastName + "'");
    }

    /**
       Get EmailAddress from the order and return it as a string. <P>
       @return emailAddress String representation of EmailAddress
     */
    public String getEmailAddress(OrderIfc order)
    {
        String emailAddress = order.getCustomer().getEMailAddress();

        return ("'" + emailAddress + "'");
    }

    /**
       Get ItemOrderID from the order line item and return it as a string. <P>
       @return itemOrderID String representation of itemOrderID
     */
    public String getItemOrderID(OrderLineItemIfc lineItem)
    {
        String itemOrderID = lineItem.getOrderID();

        return ("'" + itemOrderID + "'");
    }

    /**
       Get LineReference from the order line item and return it as a string. <P>
       @return lineReference String representation of LineReference
     */
    public String getLineReference(OrderLineItemIfc lineItem)
    {
        String lineReference = lineItem.getReference();

        return (lineReference);
    }

    /**
       Get ItemID from the order line item and return it as a string. <P>
       @return itemID String representation of ItemID
     */
    public String getItemID(OrderLineItemIfc lineItem)
    {
        String itemID = lineItem.getPLUItemID();

        return ("'" + itemID + "'");
    }

    /**
       Get ItemDepartment from the order line item and return it as a string. <P>
       @return itemDepartment String representation of ItemDepartment
     */
    public String getItemDepartment(OrderLineItemIfc lineItem)
    {
        String itemDepartment = lineItem.getPLUItem().getDepartmentID();

        return ("'" + itemDepartment + "'");
    }

    /**
       Get ItemTaxGroupID from the order line item and return it as a string. <P>
       @return itemTaxGroupID String representation of ItemTaxGroupID
     */
    public String getItemTaxGroupID(OrderLineItemIfc lineItem)
    {
        String itemTaxGroupID = Integer.toString(lineItem.getPLUItem().getTaxGroupID());

        return ("'" + itemTaxGroupID + "'");
    }

    /**
       Get ItemQuantity from the order line item and return it as a string. <P>
       @return itemQuantity String representation of ItemQuantity
     */
    public String getItemQuantity(OrderLineItemIfc lineItem)
    {
        String itemQuantity = lineItem.getQuantityOrderedDecimal().toString();

        return ("'" + itemQuantity + "'");
    }

    /**
       Get ItemExtendedAmount from the order line item and return it as a string. <P>
       @return itemExtendedAmount String representation of ItemExtendedAmount
     */
    public String getItemExtendedAmount(OrderLineItemIfc lineItem)
    {
        String itemExtendedAmount =  lineItem.getExtendedSellingPrice().getStringValue();

        return ("'" + itemExtendedAmount + "'");
    }

    /**
       Get ItemVATAmount from the order line item and return it as a string. <P>
       @return itemVATAmount String representation of ItemVATAmount
     */
    public String getItemVATAmount(OrderLineItemIfc lineItem)
    {
        String itemVATAmount =  lineItem.getItemTaxAmount().getStringValue();

        return ("'" + itemVATAmount + "'");
    }

    /**
       Get ItemInclusiveTaxAmount from the order line item and return it
       as a string. <P>
       @return itemInclusiveTaxAmount String representation of ItemInclusiveTaxAmount
     */
    public String getItemInclusiveTaxAmount(OrderLineItemIfc lineItem)
    {
        String itemInclusiveTaxAmount =  lineItem.getItemInclusiveTaxAmount().getStringValue();

        return ("'" + itemInclusiveTaxAmount + "'");
    }

    /**
       Get ItemSequenceNumber from the order line item and return it as a string. <P>
       @return itemSequenceNumber String representation of ItemSequenceNumber
     */
    public String getItemSequenceNumber(OrderLineItemIfc lineItem)
    {
        String itemSequenceNumber = Integer.toString(lineItem.getLineNumber());

        return ("'" + itemSequenceNumber + "'");
    }

    /**
       Get ItemDescription from the order line item and return it as a string. <P>
       @return itemDescription String representation of ItemDescription
     */
    public String getItemDescription(OrderLineItemIfc lineItem)
    {
        String itemDescription = lineItem.getItemDescription(LocaleMap.getLocale(LocaleMap.DEFAULT));

        return ("'" + itemDescription + "'");
    }

}
