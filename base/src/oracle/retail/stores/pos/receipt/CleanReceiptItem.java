/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/CleanReceiptItem.java /main/10 2013/06/18 10:16:49 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  11/20/14 - Bug 20063202: Removed item transaction discount comparison from equals.
 *    crain     10/31/14 - Bug 19904615: corrected the null check for advanced pricing rule id
 *    icole     10/14/14 - Forward port 19811541, sale receipt and order receipt
 *                         should show same item grouping. FA agrees with the customer
 *                         that same item number should be grouped in the case where the
 *                         tax applied to each differs by .01.
 *    mchellap  06/18/13 - Enable grouping for VAT items
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     02/18/10 - Modified to prevent sale and return items from being
 *                         grouped together on the receipt when the parameter
 *                         GroupLikeItems is set to true.
 *    cgreene   02/01/10 - added OrderStatus to the clean receipt compare
 *    cgreene   02/01/10 - added SendLabelCount to compare for like items
 *    abondala  01/03/10 - update header date
 *    cgreene   07/27/09 - XbranchMerge cgreene_bug8707097-miscdiscounts from
 *                         rgbustores_13.1x_branch
 *    cgreene   07/19/09 - XbranchMerge cgreene_bug-8707097 from
 *                         rgbustores_13.1x_branch
 *    cgreene   07/16/09 - test discount arrays so that discounted lineItems
 *                         can be merged for clean receipts
 *    cgreene   04/05/09 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt;

import java.io.Serializable;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;

/**
 * This class wraps a line item in order to identify which line items are "like"
 * each other based upon item id and price. This is useful for printing a
 * "clean" receipt where line items that have the same id and price are rolled
 * back up into the same line item with an increased quantity.
 * <p>
 * The primary implementation of this class is its {@link #equals(Object)}
 * method where the id and price are compared. 
 * 
 * @author cgreene
 * @since 13.1
 */
public class CleanReceiptItem implements Serializable
{
    private static final long serialVersionUID = -8401036981592104439L;
    
    /**
     * The line item that this "item" wraps.
     */
    protected SaleReturnLineItemIfc saleLineItem;

    /**
     * Constructor
     *
     * @param itemId
     * @param price
     */
    public CleanReceiptItem(SaleReturnLineItemIfc saleLineItem)
    {
        this.saleLineItem = saleLineItem;
    }

    /**
     * Compare the input CleanReceiptItem object with the current object.
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean isEqual = false;
        if (obj instanceof CleanReceiptItem)
        {
            CleanReceiptItem c = (CleanReceiptItem)obj;
            ItemPriceIfc price = getPrice();

            if (getItemId().equals(c.getItemId())
                    && Util.isObjectEqual(c.getPrice().getSellingPrice(), price.getSellingPrice())
                    && Util.isObjectEqual(c.getPrice().getPermanentSellingPrice(), price.getPermanentSellingPrice())
                    && CodeConstantsIfc.CODE_UNDEFINED.equals(c.getPrice().getItemPriceOverrideReason().getCode())
                    && CodeConstantsIfc.CODE_UNDEFINED.equals(price.getItemPriceOverrideReason().getCode())
                    && (c.getPrice().getDiscountEligible() == price.getDiscountEligible())
                    && (c.getPrice().getEmployeeDiscountEligible() == price.getEmployeeDiscountEligible())
                    && (c.getPrice().getDamageDiscountEligible() == price.getDamageDiscountEligible())
                    // check that the discounts applied are the same
                    && (Util.isObjectEqual(c.getPrice().getItemDiscounts(), price.getItemDiscounts())
                    // they may not be due to the calculated amount but if they are of the same rule, then its ok
                    || (getAdvancedPricingRuleID() != null && getAdvancedPricingRuleID().equals(c.getAdvancedPricingRuleID())))
                    && Util.isObjectEqual(c.getPrice().getRestockingFee(), price.getRestockingFee())
                    && c.getPrice().getItemTax().getDefaultRate() == price.getItemTax().getDefaultRate()
                    && c.getPrice().getItemTax().getTaxable() == price.getItemTax().getTaxable()
                    && c.getPrice().getItemTax().getExternalTaxEnabled() == price.getItemTax().getExternalTaxEnabled()
                    && Util.isObjectEqual(c.getPrice().getItemTax().getTaxByTaxJurisdiction(), price.getItemTax().getTaxByTaxJurisdiction())
                    && isOrderItemStatusEqual(c.getSaleReturnLineItem().getOrderItemStatus(), getSaleReturnLineItem().getOrderItemStatus())
                    && c.getSaleReturnLineItem().getSendLabelCount() == getSaleReturnLineItem().getSendLabelCount()
                    && c.getSaleReturnLineItem().getItemQuantityDecimal().signum() == getSaleReturnLineItem().getItemQuantityDecimal().signum())
            {
                isEqual = true;
            }
        }
        return isEqual;
    }

    /**
     * Determine if the OrderItemStatusIfc objects are equal.  If the deposit
     * amounts are within one cent, they will be considered matching.
     *
     * @param obj1
     * @param obj2
     * @return true if OrderItemStatus objects match
     * @since 14.1
     */
    public boolean isOrderItemStatusEqual (OrderItemStatusIfc obj1, OrderItemStatusIfc obj2)
    {
        boolean isEqual;

        if (Util.isObjectEqual(obj1.getStatus(), obj2.getStatus())
                && Util.isObjectEqual(obj1.getQuantityPickedUp(), obj2.getQuantityPickedUp())
                && Util.isObjectEqual(obj1.getQuantityPicked(), obj2.getQuantityPicked())
                && Util.isObjectEqual(obj1.getQuantityShipped(), obj2.getQuantityShipped())
                && (Util.isObjectEqual(obj1.getDepositAmount(), obj2.getDepositAmount())
                    || isDepositAmountEqual(obj1.getDepositAmount(), obj2.getDepositAmount()))
                && Util.isObjectEqual(obj1.getReference(), obj2.getReference())
                && Util.isObjectEqual(obj1.getItemDispositionCode(), obj2.getItemDispositionCode())
                && Util.isObjectEqual(obj1.getPickupDate(), obj2.getPickupDate())
                && Util.isObjectEqual(obj1.getDeliveryDetails(), obj2.getDeliveryDetails()))
        {
            isEqual = true;
        }
        else
        {
            isEqual = false;
        }
        return isEqual;
    }    
    
    /**
     * Returns true if the two objects are within 
     * one cent apart.  
     *
     * @param obj1
     * @param obj2
     * @return boolean true if the amount differ by a hundredth
     * @since 14.1
     */
    public boolean isDepositAmountEqual(CurrencyIfc deposit1, CurrencyIfc deposit2)
    {
        if (Util.isObjectEqual(deposit1, deposit2))
            return true;
        double difference = deposit1.subtract(deposit2).abs().getDoubleValue();
        return (difference <= 0.01);
    }    
    
    /**
     * returns the item id
     *
     * @return item id
     */
    public String getItemId()
    {
        return getSaleReturnLineItem().getItemID();
    }

    /**
     * Return the rule id associated with this line item.
     * 
     * @return
     */
    public String getAdvancedPricingRuleID()
    {
        String ruleId = null;
        if (saleLineItem instanceof SaleReturnLineItem)
        {
            ruleId = ((SaleReturnLineItem)saleLineItem).getAdvancedPricingRuleID();
        }
        return ruleId;
    }

    /**
     * returns the item price
     *
     * @return price
     */
    public ItemPriceIfc getPrice()
    {
        return getSaleReturnLineItem().getItemPrice();
    }

    /**
     * returns the SaleReturnLineItemIfc
     *
     * @return price
     */
    public SaleReturnLineItemIfc getSaleReturnLineItem()
    {
        return saleLineItem;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return getItemId().hashCode();
    }
}
