/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/ItemDiscountAudit.java /main/1 2012/08/21 16:40:57 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         08/17/12 - refactor discount audit
* sgu         08/17/12 - fix discount rules
* sgu         08/16/12 - add ItemDiscountAudit discount rule
* sgu         08/14/12 - add ItemDiscountAudit
* sgu         08/14/12 - add new file
* sgu         08/14/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.domain.discount;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.foundation.utility.Util;

public class ItemDiscountAudit extends AbstractItemDiscountAuditStrategy
implements ItemDiscountAuditIfc
{
    private static final long serialVersionUID = -8706349430631329332L;

    /**
     * Unit Amount of Discount
     */
    protected CurrencyIfc unitDiscountAmount = null;

    //---------------------------------------------------------------------
    /**
        Calculate, return discount amount. <P>
        @param itemPrice price of the item
        @param itemQuantity quantity of item
        @return discount amount
    */
    //---------------------------------------------------------------------
    public CurrencyIfc calculateItemDiscount(CurrencyIfc itemPrice, BigDecimal itemQuantity)
    {
        CurrencyIfc itemDiscount = getDiscountAmount().abs();
        if (itemPrice.signum() < 0)
        {
            itemDiscount = itemDiscount.negate();
        }
        return itemDiscount;
    }

    //---------------------------------------------------------------------
    /**
        Calculate, return discount amount. <P>
        @param itemPrice price of the item
        @return discount amount
    */
    //---------------------------------------------------------------------
    public CurrencyIfc calculateItemDiscount(CurrencyIfc itemPrice)
    {
        // no calculated discount here
        return(getDiscountAmount());
    }

    //---------------------------------------------------------------------
    /**
        Retrieves discount scope. <P>
        @return scope discount scope
    **/
    //---------------------------------------------------------------------
    public int getDiscountScope()
    {
        return DISCOUNT_SCOPE_ITEM;
    }

    //---------------------------------------------------------------------
    /**
     * Returns the unit discount amount
     *
     * @return unit discount amount
     */
    //---------------------------------------------------------------------
    public CurrencyIfc getUnitDiscountAmount()
    {
        return unitDiscountAmount;
    }

    //---------------------------------------------------------------------
    /**
     * Sets the unit discount amount
     *
     * @param unitDiscountAmount
     */
    //---------------------------------------------------------------------
    public void setUnitDiscountAmount(CurrencyIfc unitDiscountAmount)
    {
        this.unitDiscountAmount = unitDiscountAmount;
    }

    //---------------------------------------------------------------------
    /**
        Clone this object. <P>
        @return generic object copy of this object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        ItemDiscountAudit newClass = new ItemDiscountAudit();
        setCloneAttributes(newClass);
        return newClass;
    }

    //---------------------------------------------------------------------
    /**
        Sets attributes in clone. <P>
        @param newClass new instance of class
    **/
    //---------------------------------------------------------------------
    protected void setCloneAttributes(ItemDiscountAudit newClass)
    {
        super.setCloneAttributes(newClass);

        if (unitDiscountAmount != null)
        {
            newClass.setUnitDiscountAmount((CurrencyIfc)unitDiscountAmount.clone());
        }
    }

    //--------------------------------------------------------------------------
    /**
        Determine whether the provided object is the same type and
        has the same field values as this one. <P>
        @param obj the object to compare
        @return true if the fields are equal; false otherwise
    **/
    //--------------------------------------------------------------------------
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        // Only test for equality if the objects are instances of the
        // same class.
        if (obj instanceof ItemDiscountAudit)
        {
            ItemDiscountAudit strategy = (ItemDiscountAudit)obj;

            isEqual = super.equals(obj) &&
            Util.isObjectEqual(getUnitDiscountAmount(), strategy.getUnitDiscountAmount());
        }
        return isEqual;
    }

}
