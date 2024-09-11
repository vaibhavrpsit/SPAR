/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/lineitem/OrderLineItem.java /main/42 2014/07/24 15:23:29 sgu Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    sgu    07/23/14 - add tax authority name
 *    sgu    07/14/14 - clear tax override or exemption flag
 *    sgu    07/05/14 - set order line reference for in-store priced item
 *    sgu    06/26/14 - update order item status for repriced order item
 *    sgu    06/25/14 - add repriced order line item
 *    vinees 06/24/14 - Added 2 flags, indicating status of line item during
 *                      order pickup
 *    sgu    06/20/14 - disable transactional discount and tax override for
 *                      pickup cancel order line item
 *    yiqzha 06/20/14 - Remove depositAmount and getDepositApplied. Attribute
 *                      depositAmount is specified at the super class.
 *    sgu    06/18/14 - convert transactional discount to item discount
 *    sgu    06/15/14 - fix transaction total and discount calculation for
 *                      order pickup transaction
 *    sgu    05/25/14 - update transaction total, order payment and tender to
 *                      support adding take with items for order pickup
 *                      transaction
 *    sgu    04/24/14 - update logic to get returnable quantity
 *    cgreen 03/11/14 - add support for returning ASA ordered items
 *    rabhaw 10/10/13 - Retrun quantity should be fetched from original item
 *                      status
 *    abonda 05/07/13 - for price adjustments, prorated tax calculator should
 *                      not be called.
 *    vtemke 04/16/13 - Moved constants in OrderLineItemIfc to
 *                      OrderConstantsIfc in common project
 *    sgu    03/19/13 - add back restocking fee for an order line item's
 *                      returned amount
 *    sgu    02/28/13 - split UOM item
 *    sgu    02/12/13 - display prorated deposit amount for partial pickup or
 *                      cancel item
 *    sgu    01/14/13 - process pickup or cancel for store order items
 *    sgu    01/08/13 - add support for order picklist
 *    sgu    01/08/13 - handle pending status during split
 *    sgu    12/27/12 - correct picked quantity
 *    sgu    12/18/12 - calculate prorated order item tax using original order
 *                      item status
 *    sgu    12/14/12 - remove get/set order id to sale return line item
 *    jswan  12/14/12 - Modified to update the Order Item Status object with
 *                      the result of the return discount and tax proration.
 *    sgu    12/12/12 - prorate tax for order pickup, cancel, and return
 *    sgu    12/10/12 - prorate discount for order pickup and return
 *    jswan  10/25/12 - Modified to support returns by order.
 *    sgu    10/25/12 - add filled status for order and order item
 *    sgu    10/17/12 - fix quantity in pickup cancel order line item
 *    sgu    10/17/12 - rearragen orders to set order item level quantity and
 *                      amounts
 *    sgu    10/17/12 - fix quantity passing error
 *    sgu    10/17/12 - prorate item tax for partial pickup or cancellation
 *    sgu    10/16/12 - add function to determine if an order line item is a
 *                      pickup or cancel line item
 *    sgu    10/16/12 - clean up order item quantities
 *    sgu    10/15/12 - add ordered amount
 *    sgu    10/09/12 - create pickup cancel order transaction from an order
 *    sgu    10/04/12 - add suport to split order line for partial
 *                      pickup/cancel
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    acadar 10/28/08 - removed old deprecated methods
 *    acadar 10/25/08 - localization of price override reason codes
 *
 * ===========================================================================


     $Log:
      4    360Commerce 1.3         1/22/2006 11:41:40 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:29:13 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:51 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:52 PM  Robert Pearse
     $
     Revision 1.3  2004/02/12 17:13:57  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:26:32  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:32  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:38:04   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:58:36   msg
 * Initial revision.
 *
 *    Rev 1.3   25 Apr 2002 10:28:06   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 *
 *    Rev 1.2   Apr 02 2002 18:58:32   mpm
 * Corrected instantiation, cloning of BigDecimal.
 * Resolution for Domain SCR-46: Correct initialization of BigDecimal objects
 *
 *    Rev 1.1   Mar 18 2002 23:04:36   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:24:22   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 23 2002 10:31:26   mpm
 * Modified Util.BIG_DECIMAL to Util.I_BIG_DECIMAL, Util.ROUND_HALF to Util.I_ROUND_HALF.
 * Resolution for Domain SCR-35: Accept Foundation BigDecimal backward-compatibility changes
 *
 *    Rev 1.1   Feb 05 2002 16:35:58   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.0   Sep 20 2001 16:16:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:38:06   msg
 * header update
 * ===========================================================================
 */

package oracle.retail.stores.domain.lineitem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderConstantsIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.ProratedTaxCalculatorIfc;
import oracle.retail.stores.domain.tax.ReverseItemTaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.foundation.utility.xml.XMLConverterIfc;

/**
 * This class contains methods for handling line item operations for orders.
 * <P>
 * OrderLineItem extends SaleReturnLineItem, because many, if not all, of the
 * operations performed on SaleReturnLineItems are also performed on
 * OrderLineItems. The quantity ordered on this class is the quantity in the
 * SaleReturnLineItem.
 * <P>
 * The primary difference between SaleReturnLineItem and OrderLineItem is the
 * maintenance of other quantities (picked, shipped) against a line item.
 * <P>
 * The status of a line item is also maintained.
 * 
 * @version $Revision: /main/42 $
 **/
public class OrderLineItem extends SaleReturnLineItem implements OrderLineItemIfc
{
    private static final long serialVersionUID = -2837170668479441082L;

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/42 $";
    /**
     * quantity picked
     */
    protected BigDecimal quantityPicked = BigDecimal.ZERO;
    /**
     * quantity shipped, i.e., given over to the purchaser
     */
    protected BigDecimal quantityShipped = BigDecimal.ZERO;
    /**
     * status of order item
     */
    protected int itemStatus = OrderConstantsIfc.ORDER_ITEM_STATUS_NEW;
    /**
     * status of order item
     */
    protected int previousItemStatus = OrderConstantsIfc.ORDER_ITEM_STATUS_UNDEFINED;
    /**
     * timestamp of last status change
     */
    protected EYSDate lastStatusChange = DomainGateway.getFactory().getEYSDateInstance();
    /**
     * line item reference number
     */
    protected String reference = "";

    /**
     * The balance due for this item
     */
    protected CurrencyIfc itemBalanceDue = null;
    
    /**
     * Flag indicating if order line item is one with price cancelled
     */
    protected boolean priceCancelledDuringPickup = false;
    
    /**
     * Flag indicating if order line item is with in-store price
     */
    protected boolean inStorePriceDuringPickup = false;

    /**
     * Constructs OrderLineItem object.
     */
    public OrderLineItem()
    {
        itemBalanceDue = DomainGateway.getBaseCurrencyInstance();
        depositAmount = DomainGateway.getBaseCurrencyInstance();
    }

    /**
     * Constructs OrderLineItem object, setting item, tax rate, sales associate
     * and registry attributes
     * 
     * @param item PLU item
     * @param tax ItemTax object
     * @param pSalesAssociate default sales associate
     * @param pRegistry default registry
     * @param id order identifier
     * @param ref line item reference
     */
    public OrderLineItem(PLUItemIfc item, ItemTaxIfc tax, EmployeeIfc pSalesAssociate, RegistryIDIfc pRegistry,
            String id, String ref)
    {
        initialize(item, BigDecimal.ONE, tax, pSalesAssociate, pRegistry, (ReturnItemIfc)null);
        setOrderID(id);
        setReference(ref);
    }

    /**
     * Constructs OrderLineItem object, setting item, tax rate, sales associate
     * and registry attributes.
     * 
     * @param item PLU item
     * @param tax ItemTax object
     * @param pSalesAssociate default sales associate
     * @param pRegistry default registry
     * @param id order identifier
     * @param ref line item reference
     */
    public OrderLineItem(PLUItemIfc item, BigDecimal itemQuantity, ItemTaxIfc tax, EmployeeIfc pSalesAssociate,
            RegistryIDIfc pRegistry, String id, String ref)
    {
        initialize(item, itemQuantity, tax, pSalesAssociate, pRegistry, (ReturnItemIfc)null);
        setOrderID(id);
        setReference(ref);
    }

    /**
     * Creates clone of this object.
     * 
     * @return Object clone of this object
     */
    public Object clone()
    {
        // instantiate new object
        OrderLineItem c = new OrderLineItem();

        setCloneAttributes(c);

        // pass back Object
        return ((Object)c);
    }

    /**
     * Sets attributes used by clone method.
     * 
     * @param newClass new class on which attributes are to be set
     */
    protected void setCloneAttributes(OrderLineItem newClass)
    {
        // clone superclass attributes
        super.setCloneAttributes((SaleReturnLineItem)newClass);

        // set values
        newClass.setQuantityPicked(quantityPicked);
        newClass.setQuantityShipped(quantityShipped);
        newClass.setItemStatus(itemStatus);
        newClass.setPreviousItemStatus(previousItemStatus);
        newClass.setLastStatusChange(lastStatusChange);
        newClass.setReference(reference);
        newClass.setItemBalanceDue(itemBalanceDue);
        newClass.setDepositAmount(depositAmount);
        newClass.setPriceCancelledDuringPickup(priceCancelledDuringPickup);
        newClass.setInStorePriceDuringPickup(inStorePriceDuringPickup);
    }

    /**
     * Determine if two objects are identical.
     * 
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = false;
        if (obj instanceof OrderLineItem)
        {
            OrderLineItem c = (OrderLineItem)obj;

            // compare all the attributes of OrderLineItem
            if (super.equals(obj) && Util.isObjectEqual(getQuantityPickedDecimal(), c.getQuantityPickedDecimal())
                    && Util.isObjectEqual(getQuantityShippedDecimal(), c.getQuantityShippedDecimal())
                    && getItemStatus() == c.getItemStatus() && Util.isObjectEqual(getReference(), c.getReference())
                    && Util.isObjectEqual(getItemBalanceDue(), c.getItemBalanceDue())
                    && Util.isObjectEqual(isInStorePriceDuringPickup(), c.isInStorePriceDuringPickup())
                    && Util.isObjectEqual(isPriceCancelledDuringPickup(), c.isPriceCancelledDuringPickup()))
            {
                isEqual = true; // set the return code to true
            }
            else
            {
                isEqual = false; // set the return code to false
            }
        }
        return (isEqual);
    }

    /**
     * Retrieves quantity ordered.
     * 
     * @return quantity ordered
     */
    public BigDecimal getQuantityOrderedDecimal()
    {
        return (getItemQuantityDecimal());
    }

    /**
     * Modifies quantity ordered and re-calculates prices as needed.
     * 
     * @param value quantity ordered
     */
    public void modifyQuantityOrdered(BigDecimal value)
    {
        modifyItemQuantity(value);
    }

    /**
     * Retrieves quantity picked.
     * 
     * @return quantity picked
     */
    public BigDecimal getQuantityPickedDecimal()
    {
        return (quantityPicked);
    }

    /**
     * Sets quantity picked.
     * 
     * @param value quantity picked
     */
    public void setQuantityPicked(BigDecimal value)
    {
        quantityPicked = value;
    }

    /**
     * Retrieves quantity shipped, i.e., given over to the purchaser.
     * 
     * @return quantity shipped
     */
    public BigDecimal getQuantityShippedDecimal()
    {
        return (quantityShipped);
    }

    /**
     * Sets quantity shipped, i.e., given over to the purchaser.
     * 
     * @param value quantity shipped
     */
    public void setQuantityShipped(BigDecimal value)
    {
        quantityShipped = value;
    }

    /**
     * Retrieves status of order item.
     * 
     * @return status of order item
     */
    public int getItemStatus()
    {
        return (itemStatus);
    }

    /**
     * Sets status of order item and updates previous status, last status change
     * timestamp accordingly.
     * 
     * @param value status of order item
     * @see #setItemStatus
     */
    public void changeItemStatus(int value)
    {
        setPreviousItemStatus(itemStatus);
        itemStatus = value;
        setLastStatusChange();
    }

    /**
     * Sets status of order item. Previous status is not updated.
     * 
     * @param value status of order item
     * @see #changeItemStatus
     */
    public void setItemStatus(int value)
    {
        itemStatus = value;
    }

    /**
     * Retrieves previous item status.
     * 
     * @return previous item status
     */
    public int getPreviousItemStatus()
    {
        return (previousItemStatus);
    }

    /**
     * Sets previous item status.
     * 
     * @param value previous item status
     */
    public void setPreviousItemStatus(int value)
    {
        previousItemStatus = value;
    }

    /**
     * Retrieves timestamp of last status change.
     * 
     * @return timestamp of last status change
     */
    public EYSDate getLastStatusChange()
    {
        return (lastStatusChange);
    }

    /**
     * Sets timestamp of last status change.
     * 
     * @param value timestamp of last status change
     */
    public void setLastStatusChange(EYSDate value)
    {
        lastStatusChange = value;
    }

    /**
     * Sets timestamp of last status change to current time.
     * <P>
     */
    public void setLastStatusChange()
    {
        lastStatusChange = DomainGateway.getFactory().getEYSDateInstance();
    }

    /**
     * Retrieves line item reference
     * 
     * @return line item reference
     */
    public String getReference()
    {
        return (reference);
    }

    /**
     * Sets line item reference
     * 
     * @param value line item reference
     */
    public void setReference(String value)
    {
        reference = value;
    }

    /**
     * @return item balance due
     */
    public CurrencyIfc getItemBalanceDue()
    {
        return itemBalanceDue;
    }

    /**
     * Set item balance due
     *
     * @param itemBalanceDue
     */
    public void setItemBalanceDue(CurrencyIfc itemBalanceDue)
    {
        this.itemBalanceDue = itemBalanceDue;
    }

    /**
     * Split the order line item into an array of order items per status.
     *
     * @return an array of split order items
     */
    public SplitOrderItemIfc[] getSplitLineItemsByStatus()
    {
        List<SplitOrderItemIfc> splitItems = new ArrayList<SplitOrderItemIfc>();
        OrderItemStatusIfc orderItemStatus = getOrderItemStatus();

        int status = orderItemStatus.getStatus().getStatus();
        boolean isSingleQty = orderItemStatus.getQuantityOrdered().compareTo(BigDecimal.ONE) == 0;
        boolean isVoided = status == OrderConstantsIfc.ORDER_ITEM_STATUS_VOIDED;

        if (isSingleQty || isVoided)
        {
            //Donot split items that are single qty, of UOM, or is voided already
            SplitOrderItemIfc splitItem = new SplitOrderItem(this,
                     orderItemStatus.getQuantityOrdered(), status);
            splitItems.add(splitItem);
        }
        else
        {
            if (orderItemStatus.getQuantityNew().compareTo(BigDecimal.ZERO) > 0)
            {
                splitItems.add(new SplitOrderItem(this, orderItemStatus.getQuantityNew(),
                        OrderConstantsIfc.ORDER_ITEM_STATUS_NEW));
            }
            if (orderItemStatus.getQuantityPending().compareTo(BigDecimal.ZERO) > 0)
            {
                splitItems.add(new SplitOrderItem(this, orderItemStatus.getQuantityPending(),
                        OrderConstantsIfc.ORDER_ITEM_STATUS_PENDING));
            }
            if (orderItemStatus.getQuantityPicked().compareTo(BigDecimal.ZERO) > 0)
            {
                splitItems.add(new SplitOrderItem(this, orderItemStatus.getQuantityPicked(),
                        OrderConstantsIfc.ORDER_ITEM_STATUS_FILLED));
            }
            if (orderItemStatus.getQuantityPickedUp().compareTo(BigDecimal.ZERO) > 0)
            {
                splitItems.add(new SplitOrderItem(this, orderItemStatus.getQuantityPickedUp(),
                        OrderConstantsIfc.ORDER_ITEM_STATUS_PICKED_UP));
            }
            if (orderItemStatus.getQuantityShipped().compareTo(BigDecimal.ZERO) > 0)
            {
                splitItems.add(new SplitOrderItem(this, orderItemStatus.getQuantityShipped(),
                        OrderConstantsIfc.ORDER_ITEM_STATUS_SHIPPED));
            }
            if (orderItemStatus.getQuantityCancelled().compareTo(BigDecimal.ZERO) > 0)
            {
                splitItems.add(new SplitOrderItem(this, orderItemStatus.getQuantityCancelled(),
                        OrderConstantsIfc.ORDER_ITEM_STATUS_CANCELED));
            }
        }

        SplitOrderItemIfc[] result = splitItems.toArray(new SplitOrderItem[0]);
        return result;
    }

    /**
     * Retrieves quantity returnable. For order items, the application
     * uses quantities from the Order Item Status rather than the Sale
     * Return Line Item.  The calculation is (Quantity Picked Up) +
     * (Quantity Shipped) - (Quantity Returned).
     * <p>
     * For 14.0, orders initiated at ATG will be returnable per normal sale
     * transaction rules.
     *
     * @return quantity returnable
     */
    @Override
    public BigDecimal getQuantityReturnable()
    {
        OrderItemStatusIfc status = getOrderItemStatus();
        if (status.getExternalOrderType() == ExternalOrderConstantsIfc.TYPE_ATG)
        {
            return super.getQuantityReturnable();
        }
        
        return status.getQuantityPickedUp().add(status.getQuantityShipped()).
            subtract(quantityReturned);
    }

    /**
     * Returns an array of partial line items to pickup or cancel. The array
     * contains two elements at most, one for pickup and one for cancel.
     *
     * @param reprice a boolean flag indicating if repricing the order item with
     *        in-store price is allowed
     * @return the array
     */
    public OrderLineItemIfc[] getPickupCancelLineItems(boolean reprice)
    {
        List<OrderLineItemIfc> results = new ArrayList<OrderLineItemIfc>();
        OrderItemStatusIfc orderItemStatus = getOrderItemStatus();
        if (orderItemStatus.getQuantityPickup().compareTo(BigDecimal.ZERO) > 0)
        {
            OrderLineItemIfc pickupLineItem = getPickupCancelLineItem(true);
            results.add(pickupLineItem);
            
            if (reprice && isRepriceableDuringPickup())
            {
                // If a pickup item is to be repriced with in-store price, we are creating two
                // order line items in the transaction. The first one is an order item that 
                // records the cancelled original price. The second one is an order item with
                // the new in store price.
                OrderLineItemIfc repriceLineItem = (OrderLineItemIfc)pickupLineItem.clone();
                pickupLineItem.setPriceCancelledDuringPickup(true);
                repriceLineItem.setInStorePriceDuringPickup(true);
                results.add(repriceLineItem);
            }
        }
        if (orderItemStatus.getQuantityCancel().compareTo(BigDecimal.ZERO) > 0)
        {
            OrderLineItemIfc cancelLineItem = getPickupCancelLineItem(false);
            results.add(cancelLineItem);
        }

        return results.toArray(new OrderLineItemIfc[0]);
    }
    
    /**
     * This method should be called when doing order partial pickup or order complete
     */
    @Override
    public boolean isPickedUpOrderItem()
    {
        boolean isPickedUpOrderFlag = false;
        if (getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_STATUS_FILLED)
        {
            isPickedUpOrderFlag = true;
        }
        return (isPickedUpOrderFlag);
    }

    /**
     * Return a boolean flag indicating if an order item can be repriced to use 
     * in store price during pickup time
     * 
     * @return the boolean flag
     */
    protected boolean isRepriceableDuringPickup()
    {
        boolean isRepriceable = false;
        
        // reprice can only be applied to a cross channel order item
        OrderItemStatusIfc orderItemStatus = getOrderItemStatus();
        if (orderItemStatus.isCrossChannelItem())
        {
            // calculate deposit amount paid for items pending for pickup
            CurrencyIfc depositPending = orderItemStatus.getDepositAmount().subtract(
                    orderItemStatus.getCompletedAmount());
            // reprice can only be applied to an order item that has no deposit paid for items 
            // pending for pickup
            if (depositPending.getDecimalValue().compareTo(BigDecimal.ZERO) == 0)
            {
                isRepriceable = true;
            }
        }
        
        return isRepriceable;
    }
    
    /**
     * Returns a pickup or cancel line item
     *
     * @return the line item
     */
    protected OrderLineItemIfc getPickupCancelLineItem(boolean isPickup)
    {
        OrderLineItem lineItem = (OrderLineItem)clone();
        lineItem.setOriginalLineNumber(lineItem.getLineNumber());

        OrderItemStatusIfc orderItemStatus = lineItem.getOrderItemStatus();
        BigDecimal pickupQty = orderItemStatus.getQuantityPickup();
        if (isPickup)
        {
            lineItem.setItemStatus(OrderConstantsIfc.ORDER_ITEM_STATUS_PICK_UP);
            lineItem.prorateItemForPickupOrCancel(pickupQty, BigDecimal.ZERO);

        }
        else
        {
            lineItem.setItemStatus(OrderConstantsIfc.ORDER_ITEM_STATUS_CANCEL);
            BigDecimal cancelQty = orderItemStatus.getQuantityCancel();
            lineItem.prorateItemForPickupOrCancel(cancelQty, pickupQty);
        }

        return lineItem;
    }

    /**
     * @return a boolean flag indicating if this is an order pickup or cancel
     *         line item
     */
    public boolean isPickupCancelLineItem()
    {
        int status = getItemStatus();
        boolean isPickupCancelLineItem = status == OrderConstantsIfc.ORDER_ITEM_STATUS_PICK_UP ||
                                         status == OrderConstantsIfc.ORDER_ITEM_STATUS_CANCEL;
        return isPickupCancelLineItem;
    }
    
    //----------------------------------------------------------------------------
    /**
     * Retrieve indicator item is totalable in transaction totals
     * @return indicator item is totalable in transaction totals
     */
    //----------------------------------------------------------------------------
    public boolean isTotalable()
    {
        // a cancel order item is not totalable
        if (getItemStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_CANCEL)
        {
            return false;
        }
        
        // a pickup order item with its original price cancelled for in store price is not totalable
        if ((getItemStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_PICK_UP) && isPriceCancelledDuringPickup())
        {
            return false;
        }
        
        return super.isTotalable();
    }

    /**
     * Prorate item for pickup or cancel
     * 
     * @param qty the quantity to pickup or cancel
     */
    public void prorateItemForPickupOrCancel(BigDecimal qty, BigDecimal pickedupQty)
    {
        // save a copy of the order item status before any updates for later use
        // in tax calculation
        OrderItemStatusIfc orderItemStatus = getOrderItemStatus();
        setOriginalOrderItemStatus((OrderItemStatusIfc)orderItemStatus.clone());

        setItemQuantity(qty); // qty is a positive number
        // The following step cannot be skip even if qty equals item quantity because
        // by prorating discounts, we convert transactional discounts to item discounts.
        getItemPrice().prorateOrderItemForPickupOrCancel(qty, pickedupQty, orderItemStatus);
    }

    /**
     * Prorate item for return
     * 
     * @param qtyToReturn the quantity to return
     */
    public void prorateItemForReturn(BigDecimal qtyToReturn)
    {
        // save a copy of the order item status before any updates for later use
        // in tax calculation
        OrderItemStatusIfc orderItemStatus = getOrderItemStatus();
        setOriginalOrderItemStatus((OrderItemStatusIfc)orderItemStatus.clone());

        // qtyToReturn is a negative number
        setItemQuantity(qtyToReturn);
        getItemPrice().prorateOrderItemForReturn(qtyToReturn, orderItemStatus);
    }

    /**
     * Carry over pickup and cancel quantity and amount from another order line
     * item of the same original order item
     *
     * @param carryOverLineItems a list of line items of the same original order item
     * @return the line item that gets carried over. It can be null if no item is carried over.
     */
    protected OrderLineItemIfc carryOverPickupCancelInfo(OrderLineItemIfc[] carryOverLineItems)
    {
        OrderLineItemIfc carryOverLineItem = null;

        // in case of cancel, carry over order status from the pickup item prior to the cancel item 
        // in case of pickup, there is nothing to carry over
        if (getItemStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_CANCEL)
        {
            if (carryOverLineItems != null)
            {
                for (OrderLineItemIfc lineItem : carryOverLineItems)
                {
                    // If reprice is enabled for pickup item, only carry over the pickup item 
                    // that still has the original order price. Ignore the pickup item with 
                    // the new in store price.
                    if (!lineItem.isInStorePriceDuringPickup())
                    {
                        carryOverLineItem = lineItem;
                        break;
                    }
                }
            }
        }

        // return if there is nothing to carry over for this order line item
        if (carryOverLineItem != null)
        {
            OrderItemStatusIfc carryOverOrderItemStatus = carryOverLineItem.getOrderItemStatus();
            OrderItemStatusIfc orderItemStatus = getOrderItemStatus();

            orderItemStatus.setQuantityOrdered(carryOverOrderItemStatus.getQuantityOrdered());
            orderItemStatus.setQuantityPickedUp(carryOverOrderItemStatus.getQuantityPickedUp());
            orderItemStatus.setQuantityCancelled(carryOverOrderItemStatus.getQuantityCancelled());
            orderItemStatus.setQuantityPicked(carryOverOrderItemStatus.getQuantityPicked());
            orderItemStatus.setQuantityNew(carryOverOrderItemStatus.getQuantityNew());
            orderItemStatus.setQuantityPending(carryOverOrderItemStatus.getQuantityPending());
            orderItemStatus.setOrderedAmount(carryOverOrderItemStatus.getOrderedAmount());
            orderItemStatus.setCompletedAmount(carryOverOrderItemStatus.getCompletedAmount());
            orderItemStatus.setCancelledAmount(carryOverOrderItemStatus.getCancelledAmount());
            orderItemStatus.setDepositAmount(carryOverOrderItemStatus.getDepositAmount());
            for (OrderItemDiscountStatusIfc discountStatus : orderItemStatus.getDiscountStatusList())
            {
                OrderItemDiscountStatusIfc anotherDiscountStatus = carryOverOrderItemStatus.getDiscountStatus(
                        discountStatus.getLineNumber());
                discountStatus.setTotalAmount(anotherDiscountStatus.getTotalAmount());
                discountStatus.setCompletedAmount(anotherDiscountStatus.getCompletedAmount());
                discountStatus.setCancelledAmount(anotherDiscountStatus.getCancelledAmount());
            }
            for (OrderItemTaxStatusIfc taxStatus : orderItemStatus.getTaxStatusList())
            {
                OrderItemTaxStatusIfc anotherTaxStatus = carryOverOrderItemStatus.getTaxStatus(
                        taxStatus.getAuthorityID(), taxStatus.getTaxGroupID(),
                        taxStatus.getTypeCode());
                taxStatus.setTotalAmount(anotherTaxStatus.getTotalAmount());
                taxStatus.setCompletedAmount(anotherTaxStatus.getCompletedAmount());
                taxStatus.setCancelledAmount(anotherTaxStatus.getCancelledAmount());
            }
        }
        return carryOverLineItem;
    }

    /**
     * Prepare a pickup or cancel order line item
     *
     * @param lineItem the line item
     */
    public void preparePickupCancelLineItem(OrderLineItemIfc[] carryOverLineItems)
    {
        // If there are order line items of the same original order item to pickup or cancel,
        // we need to carry over its pickup or cancel quantity/amount.
        OrderLineItemIfc carryOverLineItem = carryOverPickupCancelInfo(carryOverLineItems);

        OrderItemStatusIfc orderItemStatus = getOrderItemStatus();
        BigDecimal itemQty = getItemQuantityDecimal();
        CurrencyIfc itemTotal = getItemPrice().getItemTotal();
        CurrencyIfc depositPending = orderItemStatus.getDepositAmount().subtract(
                orderItemStatus.getCompletedAmount());
        CurrencyIfc amountPending = (orderItemStatus.getOrderedAmount().subtract(
                orderItemStatus.getCompletedAmount())).subtract(
                orderItemStatus.getCancelledAmount());
        CurrencyIfc itemDeposit = depositPending.prorate(itemTotal, amountPending);
        CurrencyIfc itemBalanceDue = null;
        if (getItemStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_CANCEL)
        {
            // calculate item balance due
            itemBalanceDue = itemDeposit.negate();
            
            // increment item cancelled quantity
            BigDecimal quantityCancelled = orderItemStatus.getQuantityCancelled().add(itemQty);
            orderItemStatus.setQuantityCancelled(quantityCancelled);

            // increment item cancelled amount
            CurrencyIfc cancelledAmount = orderItemStatus.getCancelledAmount().add(itemTotal);
            orderItemStatus.setCancelledAmount(cancelledAmount);
            
            // update item deposit amount
            CurrencyIfc depositAmount = orderItemStatus.getDepositAmount().add(itemBalanceDue);
            orderItemStatus.setDepositAmount(depositAmount);
            
            // subtract the cancel from quantity added back to the pickup order line item carried over.
            if (carryOverLineItem != null)
            {
                adjustQuantityCancelFrom(false /* for cancel order item */);
            }
        }
        else // pickup order item
        {
            // calculate item balance due
            itemBalanceDue = itemTotal.subtract(itemDeposit);
            
            if (isPriceCancelledDuringPickup())
            {
                // a pickup item with its original price cancelled for in-store repricing.
                // The in-store repriced ones will spin off to be its own order item.
                // Decrement the total ordered quantity 
                BigDecimal quantityOrdered = orderItemStatus.getQuantityOrdered().subtract(itemQty);
                orderItemStatus.setQuantityOrdered(quantityOrdered);
                
                // Decrement item ordered amount
                CurrencyIfc orderedAmount = orderItemStatus.getOrderedAmount().subtract(itemTotal);
                orderItemStatus.setOrderedAmount(orderedAmount);
                
                // Add back the not yet cancelled quantity to their previous status
                adjustQuantityCancelFrom(true /* for pickup order item */);
            }
            else if (!isInStorePriceDuringPickup())
            {
                // a normal pickup item with no reprice
                // Increment item picked up quantity.
                BigDecimal quantityPickedUp = orderItemStatus.getQuantityPickedUp().add(itemQty);
                orderItemStatus.setQuantityPickedUp(quantityPickedUp);
                
                // increment item completed amount
                CurrencyIfc completedAmount = orderItemStatus.getCompletedAmount().add(itemTotal);
                orderItemStatus.setCompletedAmount(completedAmount);
                
                // update item deposit amount
                CurrencyIfc depositAmount = orderItemStatus.getDepositAmount().add(itemBalanceDue);
                orderItemStatus.setDepositAmount(depositAmount);
                
                // Add back the not yet cancelled quantity to their previous status
                adjustQuantityCancelFrom(true /* for pickup order item */);      
                
            }
            else
            {
                // a in-store repriced order pickup item that spins off to be its own order item.
                // create a new order item status that is completed/picked up.
                orderItemStatus.clearQuantityAndAmount();
                orderItemStatus.setQuantityOrdered(itemQty);
                orderItemStatus.setQuantityPickedUp(itemQty);
                orderItemStatus.setOrderedAmount(itemTotal);
                orderItemStatus.setCompletedAmount(itemTotal);  
                orderItemStatus.setDepositAmount(itemTotal);   
            }
            
        }

        // set line item balance due
        setItemBalanceDue(itemBalanceDue);
        
        // set the deposit applied to the picked up or cancelled quantity
        setDepositAmount(itemDeposit);

        // clean up pickup and cancel quantity
        orderItemStatus.setQuantityPickup(BigDecimal.ZERO);
        orderItemStatus.setQuantityCancel(BigDecimal.ZERO);

        // Accumulate item discount status
        accumulatePickupCancelLineItemDiscountStatus();

        // Accumulate item tax status
        accumulatePickupCancelLineItemTaxStatus();

        // update order item status
        orderItemStatus.setStatusByQuantity();
    }

    /**
     * Adjust quantity cancel from for a pickup or cancel order item
     * 
     * @param forPickupItem a boolean indicating if the adjustment is being done
     *            for a pickup item or a cancel item.
     */
    protected void adjustQuantityCancelFrom(boolean forPickupItem)
    {
        OrderItemStatusIfc orderItemStatus = getOrderItemStatus();
        for (int key : orderItemStatus.getQuantityCancelFrom().keySet())
        {
            BigDecimal qtyCancelFrom = orderItemStatus.getQuantityCancelFrom().get(key);
            //For a pickup item, add qtyCancelFrom to its previous status since it is not yet cancelled;
            //For a cancel item, negate qtyCancelFrom to subtract it from its previous status.
            if (!forPickupItem)
            {
                qtyCancelFrom = qtyCancelFrom.negate();
            }
            switch (key)
            {
            case OrderConstantsIfc.ORDER_ITEM_STATUS_FILLED:
                orderItemStatus.setQuantityPicked(orderItemStatus.getQuantityPicked().add(qtyCancelFrom));
                break;
            case OrderConstantsIfc.ORDER_ITEM_STATUS_PENDING:
                orderItemStatus.setQuantityPending(orderItemStatus.getQuantityPending().add(qtyCancelFrom));
                break;
            case OrderConstantsIfc.ORDER_ITEM_STATUS_NEW:
                orderItemStatus.setQuantityNew(orderItemStatus.getQuantityNew().add(qtyCancelFrom));
                break;
            }
        }
    }

    /**
     * Update completed or cancelled discount amount in order item discount
     * status of the order pickup cancel line item
     */
    protected void accumulatePickupCancelLineItemDiscountStatus()
    {
        OrderItemStatusIfc orderItemStatus = getOrderItemStatus();
        ItemDiscountStrategyIfc[] itemDiscounts = getItemPrice().getItemDiscounts();
        for (ItemDiscountStrategyIfc itemDiscount : itemDiscounts)
        {
            OrderItemDiscountStatusIfc itemDiscountStatus = orderItemStatus.getDiscountStatus(
                    itemDiscount.getOrderItemDiscountLineReference());
            if (itemDiscountStatus != null)
            {
                if (getItemStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_CANCEL)
                {
                    CurrencyIfc cancelledAmt = itemDiscountStatus.getCancelledAmount().add(itemDiscount.getDiscountAmount());
                    itemDiscountStatus.setCancelledAmount(cancelledAmt);
                }
                else
                {
                    if (isPriceCancelledDuringPickup())
                    {
                        // a discount is cancelled for in-store repricing.
                        // Decrement the cancelled discount amount from the total discount amount.
                        CurrencyIfc totalAmount = itemDiscountStatus.getTotalAmount().subtract(itemDiscount.getDiscountAmount());
                        itemDiscountStatus.setTotalAmount(totalAmount);
                    }
                    else if (!isInStorePriceDuringPickup())
                    {
                        // a discount that is completed/picked up without in-store repricing
                        // Increment the completed discount amount
                        CurrencyIfc completedAmt = itemDiscountStatus.getCompletedAmount().add(itemDiscount.getDiscountAmount());
                        itemDiscountStatus.setCompletedAmount(completedAmt);
                    }
                    else
                    {
                        // A repriced completed in-store discount item spin off from its original discount item
                        itemDiscountStatus.setTotalAmount(itemDiscount.getDiscountAmount());
                        itemDiscountStatus.setCompletedAmount(itemDiscount.getDiscountAmount());
                        itemDiscountStatus.setCancelledAmount(DomainGateway.getBaseCurrencyInstance());
                        itemDiscountStatus.setReturnedAmount(DomainGateway.getBaseCurrencyInstance());
                    }

                }
            }
        }
    }

    /**
     * Update completed or cancelled tax amount in order item tax status of the
     * order pickup cancel line item
     * 
     * @param lineItem the order pickup cancel line item
     */
    protected void accumulatePickupCancelLineItemTaxStatus()
    {
        OrderItemStatusIfc orderItemStatus = getOrderItemStatus();
        TaxInformationIfc[] taxInformations = getItemTax().getTaxInformationContainer().getTaxInformation();
        for (TaxInformationIfc taxInformation : taxInformations)
        {
            OrderItemTaxStatusIfc itemTaxStatus = orderItemStatus.getTaxStatus(
                    taxInformation.getTaxAuthorityID(), taxInformation.getTaxGroupID(), taxInformation.getTaxTypeCode());
            if (itemTaxStatus != null)
            {
                if (getItemStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_CANCEL)
                {
                    CurrencyIfc cancelledAmt = itemTaxStatus.getCancelledAmount().add(taxInformation.getTaxAmount());
                    itemTaxStatus.setCancelledAmount(cancelledAmt);
                }
                else
                {
                    if (isPriceCancelledDuringPickup())
                    {
                        // a tax is cancelled for in-store repricing.
                        // Decrement the cancelled tax amount from the total tax amount.
                        CurrencyIfc totalAmount = itemTaxStatus.getTotalAmount().subtract(taxInformation.getTaxAmount());
                        itemTaxStatus.setTotalAmount(totalAmount);
                        
                    }
                    else if (!isInStorePriceDuringPickup())
                    {
                        // a tax that is completed/picked up without in-store repricing
                        // Increment the completed tax amount
                        CurrencyIfc completedAmt = itemTaxStatus.getCompletedAmount().add(taxInformation.getTaxAmount());
                        itemTaxStatus.setCompletedAmount(completedAmt);
                    }
                    else
                    {
                        // A repriced completed in-store tax item spin off from its original tax item
                        itemTaxStatus.setTotalAmount(taxInformation.getTaxAmount());
                        itemTaxStatus.setCompletedAmount(taxInformation.getTaxAmount());
                        itemTaxStatus.setCancelledAmount(DomainGateway.getBaseCurrencyInstance());
                        itemTaxStatus.setReturnedAmount(DomainGateway.getBaseCurrencyInstance());
                    }
                }
            }
        }
    }

    /**
     * Prepare a return order line item
     *
     * @param lineItem the line item
     */
    public void prepareReturnLineItem()
    {
        OrderItemStatusIfc orderItemStatus = getOrderItemStatus();

        BigDecimal itemQty = getItemQuantityDecimal();
        
        //The item returned amount should add back the restocking fee.
        CurrencyIfc itemTotal = getItemPrice().getItemTotal();
        CurrencyIfc restockingFee = getItemPrice().getExtendedRestockingFee();
        if (restockingFee != null)
        {
            itemTotal = itemTotal.add(restockingFee);
        }
        
        // increment item cancelled quantity
        BigDecimal quantityReturned = orderItemStatus.getQuantityReturned().add(itemQty.abs());
        orderItemStatus.setQuantityReturned(quantityReturned);

        // increment item returned amount
        CurrencyIfc amountReturned = orderItemStatus.getReturnedAmount().add(itemTotal.abs());
        orderItemStatus.setReturnedAmount(amountReturned);

        // prepare item discount status
        accumulateReturnLineItemDiscountStatus();

        // accumulate item tax status
        accumulateReturnLineItemTaxStatus();

        // update order item status
        orderItemStatus.setStatusByQuantity();
    }

    /**
     * Accumulate the current return discount amount in the list of
     * discount status objects.
     */
    protected void accumulateReturnLineItemDiscountStatus()
    {
        OrderItemStatusIfc orderItemStatus = getOrderItemStatus();
        ItemDiscountStrategyIfc[] itemDiscounts = getItemPrice().getItemDiscounts();
        for (ItemDiscountStrategyIfc itemDiscount : itemDiscounts)
        {
            OrderItemDiscountStatusIfc itemDiscountStatus = orderItemStatus.getDiscountStatus(
                    itemDiscount.getOrderItemDiscountLineReference());
            if (itemDiscountStatus != null)
            {
                CurrencyIfc returntedAmt = itemDiscountStatus.getReturnedAmount().abs().add(itemDiscount.getDiscountAmount().abs());
                itemDiscountStatus.setReturnedAmount(returntedAmt);
            }
        }
    }

    /**
     * Accumulate the current return tax amount in the list of
     * tax status objects.
     */
    protected void accumulateReturnLineItemTaxStatus()
    {
        OrderItemStatusIfc orderItemStatus = getOrderItemStatus();
        TaxInformationIfc[] taxInformations = getItemTax().getTaxInformationContainer().getTaxInformation();
        for (TaxInformationIfc taxInformation : taxInformations)
        {
            OrderItemTaxStatusIfc itemTaxStatus = orderItemStatus.getTaxStatus(
                    taxInformation.getTaxAuthorityID(), taxInformation.getTaxGroupID(), taxInformation.getTaxTypeCode());
            if (itemTaxStatus != null)
            {
                CurrencyIfc returnedAmt = itemTaxStatus.getReturnedAmount().abs().add(taxInformation.getTaxAmount().abs());
                itemTaxStatus.setReturnedAmount(returnedAmt);
            }
        }
    }

    /**
     * Tax rules applied on returns
     *
     * @return Array of reverse itemtax rules
     */
    protected ReverseItemTaxRuleIfc[] getRetrievedReturnTaxRules()
    {
        // If the original order Item status is null, it means that the item is already 
        // persisted in the db; therefore no proration should be done. It should just
        // read tax information from the tax line items saved in the db.
        if((!isPriceAdjustmentLineItem()) && (getOriginalOrderItemStatus() != null))
        {
            return getProrationTaxRule(ProratedTaxCalculatorIfc.Type.RETURN);
        }
        
        return super.getReverseTaxRules();
    }

    /**
     * Tax rules applied on reverse transactions other than returns
     *
     * @return Array of revers item tax rules
     */
    protected ReverseItemTaxRuleIfc[] getReverseTaxRules()
    {
        int itemStatus = getItemStatus();
        if (getOriginalOrderItemStatus() == null)
        {
            // If the original order Item status is null, it means that the item is already 
            // persisted in the db; therefore no proration should be done. It should just
            // read tax information from the tax line items saved in the db.
            return super.getReverseTaxRules();
        }
        else if (itemStatus == OrderConstantsIfc.ORDER_ITEM_STATUS_PICK_UP)
        {
            return getProrationTaxRule(ProratedTaxCalculatorIfc.Type.PICKUP);
        }
        else if (itemStatus == OrderConstantsIfc.ORDER_ITEM_STATUS_CANCEL)
        {
            return getProrationTaxRule(ProratedTaxCalculatorIfc.Type.CANCEL);
        }
        else
        {
            return super.getReverseTaxRules();
        }
    }

    /**
     * Tax rules applied to order item pickup, cancel, or return
     *
     * @return Array of reverse item tax rules
     */
    protected ReverseItemTaxRuleIfc[] getProrationTaxRule(ProratedTaxCalculatorIfc.Type type)
    {
        // if this order line item is a return, pickup or cancel line item, item tax has to be prorated
        // to support partial return, pickup or cancellation.
        if (reverseTaxRules == null)
        {
            List<ReverseItemTaxRuleIfc> rules = new ArrayList<ReverseItemTaxRuleIfc>();
            if (itemPrice != null && itemPrice.getItemTax() != null
                    && itemPrice.getItemTax().getTaxInformationContainer() != null)
            {
                TaxInformationIfc[] originalTaxes = itemPrice.getItemTax().getTaxInformationContainer()
                        .getTaxInformation();
                if (originalTaxes != null)
                {
                    OrderItemStatusIfc orderItemStatus = getOriginalOrderItemStatus();
                    BigDecimal quantityToProrate = getItemQuantityDecimal();
                    for (int i = 0; i < originalTaxes.length; i++)
                    {
                        ReverseItemTaxRuleIfc taxRule = DomainGateway.getFactory().getReturnItemTaxRuleInstance();
                        taxRule.setOrder(i);
                        taxRule.setTaxRuleName(originalTaxes[i].getTaxRuleName());
                        taxRule.setTaxAuthorityName(originalTaxes[i].getTaxAuthorityName());
                        taxRule.setInclusiveTaxFlag(originalTaxes[i].getInclusiveTaxFlag());
                        taxRule.setUniqueID(String.valueOf(taxRule.hashCode()));
                        ProratedTaxCalculatorIfc calculator = DomainGateway.getFactory()
                                .getProratedTaxCalculatorInstance();
                        taxRule.setTaxCalculator(calculator);
                        calculator.setOrderItemStatus(orderItemStatus);
                        calculator.setCalculationParameters(new TaxInformationIfc[] { originalTaxes[i] });
                        calculator.setQuantityToProrate(quantityToProrate);
                        calculator.setType(type);

                        rules.add(taxRule);
                    }
                }
            }
            else
            {
                logger.error("Could not set up prorated item tax rule.  Either itemPrice, itemTax, or the tax information container is null.");
            }

            this.reverseTaxRules = rules.toArray(new ReverseItemTaxRuleIfc[rules.size()]);
        }
        return this.reverseTaxRules;
    }

    /**
     * Restores the object from the contents of the xml tree based on the
     * current node property of the converter.
     * 
     * @param converter is the conversion utility
     * @exception XMLConversionException if error occurs transalating from XML
     */
    public void translateFromElement(XMLConverterIfc converter) throws XMLConversionException
    {
        try
        {                                                              
                Element top = converter.getCurrentElement();
                Element[] properties = converter.getChildElements(top,XMLConverterIfc.TAG_PROPERTY);

                // Retrieve and store the values for each property
                for (int i = 0; i < properties.length; i++)
                {                                                      
                    Element element = properties[i];
                    String name = element.getAttribute("name");

                    if ("itemQuantity".equals(name) ||
                        "quantityOrdered".equals(name))
                    {
                        itemQuantity = new BigDecimal(converter.getElementText(element));
                    }
                    else if ("quantityShipped".equals(name))
                    {
                        quantityShipped = new BigDecimal(converter.getElementText(element));
                    }
                    else if ("quantityPicked".equals(name))
                    {
                        quantityPicked = new BigDecimal(converter.getElementText(element));
                    }
                    else if ("itemStatus".equals(name))
                    {
                        itemStatus = new Integer(converter.getElementText(element)).intValue();
                    }
                    else if ("previousItemStatus".equals(name))
                    {
                        previousItemStatus = new Integer(converter.getElementText(element)).intValue();
                    }
                        else if ("lastStatusChange".equals(name))
                        {
                            lastStatusChange = (EYSDate) converter.getPropertyObject(element);
                        }
                    else if ("giftRegistry".equals(name))
                    {
                        registry = (RegistryIDIfc) converter.getPropertyObject(element);
                    }
                    else if ("itemPrice".equals(name))
                    {
                        itemPrice = (ItemPriceIfc) converter.getPropertyObject(element);
                     }
                    else if ("pluItem".equals(name))
                    {
                        pluItem = (PLUItemIfc) converter.getPropertyObject(element);
                    }
                    else if ("returnItem".equals(name))
                    {
                        returnItem = (ReturnItemIfc) converter.getPropertyObject(element);
                    }
                    else if ("salesAssociate".equals(name))
                    {
                        salesAssociate = (EmployeeIfc) converter.getPropertyObject(element);
                    }
                    else if ("lineNumber".equals(name))
                    {
                        lineNumber = new Integer(converter.getElementText(element)).intValue();
                    }
                    else if ("reference".equals(name))
                    {
                        reference = (String) converter.getPropertyObject(element);
                }
                else
                {
                    // take no action on not-found attribute
                }
            }
        }
        catch (Exception e)
        {
            throw new XMLConversionException(e.toString());
        }
    }
    
    /**
     * returns true if order line item price was cancelled during pickup
     * 
     * @return the isPriceCancelledDuringPickup
     */
    public boolean isPriceCancelledDuringPickup()
    {
        return (priceCancelledDuringPickup);
    }

    /**
     * sets the flag for price-cancelled orderlineitem 
     * 
     * @param isPriceCancelledDuringPickup the isPriceCancelledDuringPickup to set
     */
    public void setPriceCancelledDuringPickup(boolean priceCancelledDuringPickup)
    {
        this.priceCancelledDuringPickup = priceCancelledDuringPickup;
    }

    /**
     * returns true if line item price was set in-store during order pickup
     * 
     * @return the isInStorePriceDuringPickup
     */
    public boolean isInStorePriceDuringPickup()
    {
        return inStorePriceDuringPickup;
    }

    /**
     * sets the flag to show if price is in-store during order pick up 
     * 
     * @param isInStorePriceDuringPickup the isInStorePriceDuringPickup to set
     */
    public void setInStorePriceDuringPickup(boolean inStorePriceDuringPickup)
    {
        this.inStorePriceDuringPickup = inStorePriceDuringPickup;
    }

    /**
     * Returns string representation of status value.
     * 
     * @param value status value
     * @return string representation of status value
     */
    public String statusToString(int value)
    {
        StringBuilder strResult = new StringBuilder();
        // attempt to use descriptor
        try
        {
            if (value == OrderConstantsIfc.ORDER_ITEM_STATUS_UNDEFINED)
            {
                strResult.append("Undefined");
            }
            else
            {
                strResult.append(OrderConstantsIfc.ORDER_ITEM_STATUS_DESCRIPTORS[value]);
            }
        }
        // if out of bounds, build special message
        catch (ArrayIndexOutOfBoundsException e)
        {
            strResult.append("Invalid [").append(value).append("]");
        }

        return (strResult.toString());
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        // build result string
        StringBuilder strResult = new StringBuilder();
        strResult.append("Class:  OrderLineItem (Revision ");
        strResult.append(getRevisionNumber());
        strResult.append(") @");
        strResult.append(hashCode());
        strResult.append("\n");
        // add attributes to string
        strResult.append(super.toString())
                 .append("reference:                          [")
                 .append(getReference()).append("]\n")
                 .append("quantityOrdered:                    [")
                 .append(getQuantityOrderedDecimal()).append("]\n")
                 .append("quantityPicked:                     [")
                 .append(getQuantityPickedDecimal()).append("]\n")
                 .append("quantityShipped:                    [")
                 .append(getQuantityShippedDecimal()).append("]\n")
                 .append("itemStatus:                         [")
                 .append(statusToString(itemStatus)).append("]\n")
                 .append("previousItemStatus:                 [")
                 .append(statusToString(previousItemStatus)).append("]\n")
                 .append("priceCancelledDuringPickup:         [")
                 .append(isPriceCancelledDuringPickup()).append("]\n")
                 .append("inStorePriceDuringPickup:           [")
                 .append(isInStorePriceDuringPickup()).append("]\n");
        // pass back result
        return(strResult.toString());
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
