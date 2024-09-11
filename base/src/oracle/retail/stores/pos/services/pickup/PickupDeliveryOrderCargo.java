/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pickup/PickupDeliveryOrderCargo.java /main/1 2012/09/12 11:57:17 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  02/26/09 - Rework for PDO functionality
 *    aphulamb  01/02/09 - fix delivery issues
 *    aphulamb  12/23/08 - Mock padding fix and PDO flow related changes for
 *                         buttons enable/disable
 *    aphulamb  11/24/08 - Checking files after code review by amrish
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/17/08 - Pickup Delivery Order
 *    aphulamb  11/17/08 - Pickup Delivery order
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - Pickup Delilvery order cargo
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pickup;

import java.math.BigDecimal;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;

public class PickupDeliveryOrderCargo extends SpecialOrderCargo implements CargoIfc
{
    private static final long serialVersionUID = 2974539093488867263L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/1 $";

    // The current register
    protected RegisterIfc register = null;

    // The giftcard to add or search for
    protected GiftCardIfc giftcard = null;

    // PLU item to add or search for
    protected PLUItemIfc pluItem = null;

    /**
     * The serial number of item to be added to current transaction.
     */
    protected String itemSerial = null;

    /**
     * Sale Return Line Item Array
     */
    protected SaleReturnLineItemIfc[] lineItems;

    /**
     * Sale Return Line Item Array
     */
    protected SaleReturnLineItemIfc[] items;

    /**
     * Sale Return Line Item
     */
    protected SaleReturnLineItemIfc item;

    /**
     * Item index
     */
    protected int index;

    /**
     * Item indices
     */
    protected int[] indices;

    /**
     * bolean result
     */
    protected boolean result = false;

    /**
     * This flag indicates whether the item should be added to the transaction.
     * True, if the item should be added to the transaction. False otherwise.
     * Default is true.
     */
    protected boolean modifiedFlag = false;

    /**
     * Item Quantity
     */
    protected BigDecimal itemQuantity = new BigDecimal("1.00");

    // transaction type of current transaction, if one in progress
    protected int transType = TransactionIfc.TYPE_UNKNOWN;

    /**
     * Transaction
     */
    protected RetailTransactionIfc transaction = null;

    /**
     * order transaction
     */
    protected OrderTransactionIfc orderTransaction = null;

    /**
     * Customer
     */
    protected CustomerIfc customer;

    /**
     * pickup date
     */
    protected String pickupDate;

    /**
     * For Pickup Delivery isPickupDelivery is true
     */
    boolean isPickupDelivery;

    /**
     * For Delivery isDeliveryAction is true
     */
    boolean isDeliveryAction;

    /**
     * If all selected item are already delivery status then set to true
     */
    boolean isAllItemAlreadyDelivery;

    /**
     * Constructs InquiryOptionsCargo object.
     */
    public PickupDeliveryOrderCargo()
    {
    }

    /**
     * Returns the transaction type or TYPE_UNKNOWN, if no transaction in
     * progress.
     * 
     * @return The transaction type or TYPE_UNKNOWN.
     */
    public int getTransactionType()
    {
        return transType;
    }

    /**
     * Sets the transaction type for the current transaction.
     * 
     * @param value The transaction type.
     */
    public void setTransactionType(int value)
    {
        transType = value;
    }

    /**
     * Returns the transaction.
     * 
     * @return The transaction.
     */
    public RetailTransactionIfc getTransaction()
    {
        return transaction;
    }

    /**
     * Returns the order transaction.
     * 
     * @return The order transaction.
     */
    public OrderTransactionIfc getOrderTransaction()
    {
        return orderTransaction;
    }

    /**
     * Sets the order transaction.
     * 
     * @param order transaction
     */
    public void setOrderTransaction(OrderTransactionIfc value)
    {
        orderTransaction = value;
    }

    /**
     * Sets the retail transaction.
     * 
     * @param retail transaction
     */
    public void setTransaction(RetailTransactionIfc value)
    {
        transaction = value;
    }

    /**
     * Returns the pickup date.
     * 
     * @return The pickup date.
     */
    public String getPickupDate()
    {
        return pickupDate;
    }

    /**
     * Sets the pickup date.
     * 
     * @param pickupDate
     */
    public void setPickupDate(String pickupDate)
    {
        this.pickupDate = pickupDate;
    }

    /**
     * Sets the Sale Return line item array.
     * 
     * @param items array of SaleReturnLineItem
     */
    public void setLineItems(SaleReturnLineItemIfc[] items)
    {
        lineItems = items;
    }

    /**
     * Returns the sale return line item array.
     * 
     * @return The SaleReturnLIneItem.
     */
    public SaleReturnLineItemIfc[] getLineItems()
    {
        return lineItems;
    }

    /**
     * Sets the sale return line item.
     * 
     * @param sale return line item
     */
    public void setItem(SaleReturnLineItemIfc value)
    {
        item = value;
    }

    /**
     * Returns the sale return line item.
     * 
     * @return The SaleReturnLineItem.
     */
    public SaleReturnLineItemIfc getItem()
    {
        return item;
    }

    /**
     * Sets isPickupDelivery flag true for Pickup or Delivery items
     */
    public void setActionPickupDelivery(boolean isPickDel)
    {
        isPickupDelivery = isPickDel;
    }

    /**
     * Returns the flag fo rpickup or delivery items.
     * 
     * @return The isPickupDelivery.
     */
    public boolean getActionPickupDelivery()
    {
        return isPickupDelivery;
    }

    /**
     * Sets isDeliveryaction flag true for Delivery items
     */
    public void setDeliveryAction(boolean isDeliveryAction)
    {
        this.isDeliveryAction = isDeliveryAction;
    }

    /**
     * Returns the flag for DeliveryAction.
     * 
     * @return The isDeliveryAction.
     */
    public boolean isDeliveryAction()
    {
        return isDeliveryAction;
    }
}