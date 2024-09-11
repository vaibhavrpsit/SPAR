/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/AbstractItemDiscountStrategy.java /main/20 2014/06/23 13:03:35 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       06/18/14 - convert transactional item discount to item discount
 *                         for order pickup/cancel
 *    tksharma  09/23/13 - moved promotionId, promotionComponentId and
 *                         promotionComponentDetailId to DiscountRule.java
 *    yiqzhao   07/11/13 - Populate the return total amount.
 *    jswan     12/14/12 - Modified to convert Transaction Discounts into Item
 *                         discount with returning items from and order.
 *    sgu       12/10/12 - prorate discount for order pickup and return
 *    sgu       10/15/12 - change return type to ItemDiscountStrategyIfc
 *    sgu       10/09/12 - create pickup cancel order transaction from an order
 *    sgu       10/05/12 - add function to get prorated item discount
 *    sgu       08/16/12 - add ItemDiscountAudit discount rule
 *    cgreene   01/05/11 - XbranchMerge cgreene_itemprice_empdiscount from
 *                         rgbustores_13.3x_generic_branch
 *    cgreene   01/04/11 - prevent creating non-null employee if the employee
 *                         id set is null or empty string.
 *    blarsen   12/23/10 - XbranchMerge
 *                         blarsen_bug10396003-item-discount-flag-overrides-employee-discount
 *                         from main
 *    blarsen   12/22/10 - Added isEmployeeDiscount() method.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   04/14/09 - convert pricingGroupID to integer instead of string
 *    npoola    11/30/08 - CSP POS and BO changes
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         5/15/2007 5:53:46 PM   Maisa De Camargo
 *         Added PromotionId, PromotionComponentId and
 *         PromotionComponentDetailId
 *    3    360Commerce 1.2         3/31/2005 4:27:06 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:26 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:20 PM  Robert Pearse
 *
 *   Revision 1.2  2004/03/22 03:48:51  cdb
 *   @scr 3588 Code Review Updates
 *
 *   Revision 1.1  2004/03/02 18:33:42  cdb
 *   @scr 3588 Migrated common code to abstract class. Had
 *   Transaction Discounts begin preserving employee ID via
 *   the Audit's.
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.OrderItemDiscountStatusIfc;

/**
 * Discount by amount strategy.
 *
 * @see oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc
 * @see oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc
 * @see oracle.retail.stores.domain.discount.DiscountRule
 * @version $Revision: /main/20 $
 */
public abstract class AbstractItemDiscountStrategy extends DiscountRule implements ItemDiscountStrategyIfc
{
    private static final long serialVersionUID = -1164315293954530368L;

    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/20 $";

    /**
     * calculated item discount amount
     */
    protected CurrencyIfc itemDiscountAmount = null;

    /**
     * indicates if this is a damage discount
     */
    protected boolean damageDiscount = false;

    /**
     * discount employee
     */
    protected EmployeeIfc discountEmployee = null;
    
    /**
     * pricing Group Id
     */
    protected int pricingGroupID = -1;

    /**
     * Returns the calculatd item discount amount
     *
     * @return item discount amount
     */
    public CurrencyIfc getItemDiscountAmount()
    {
        return itemDiscountAmount;
    }

    /**
     * Sets the calculated item discount amount
     *
     * @param itemDiscountAmount item discount amount
     */
    public void setItemDiscountAmount(CurrencyIfc itemDiscountAmount)
    {
        this.itemDiscountAmount = itemDiscountAmount;
    }

    /**
     * Sets if the rule is a damage discount.
     *
     * @param value
     */
    public void setDamageDiscount(boolean value)
    {
        damageDiscount = value;
    }

    /**
     * Indicates if the rule is a damage discount.
     *
     * @return true if this is a damage discount
     */
    public boolean isDamageDiscount()
    {
        return damageDiscount;
    }

    /**
     * Sets employee discount employee.
     *
     * @param value employee discount employee
     */
    public void setDiscountEmployee(EmployeeIfc value)
    {
        discountEmployee = value;
    }

    /**
     * Sets employee discount employee. Is no-op if the specified value is null
     * or empty string.
     *
     * @param value employee discount employee ID
     */
    public void setDiscountEmployee(String value)
    {
        if (!Util.isEmpty(value))
        {
            setDiscountEmployee(DomainGateway.getFactory().getEmployeeInstance());
            getDiscountEmployee().setEmployeeID(value);
        }
    }

    /**
     * Retrieves employee discount employee.
     *
     * @return employee discount employee
     */
    public EmployeeIfc getDiscountEmployee()
    {
        return discountEmployee;
    }

    /**
     * Returns identifier for employee discount employee
     *
     * @return identifier for employee discount employee
     */
    public String getDiscountEmployeeID()
    {
        String employeeID = "";
        if (getDiscountEmployee() != null)
        {
            employeeID = getDiscountEmployee().getEmployeeID();
        }
        return(employeeID);
    }

    /**
     * Indicates if the rule is a employee discount. Checks if the
     * {@link #discountEmployee} is not null.
     *
     * @return true if this is a employee discount
     */
    public boolean isEmployeeDiscount()
    {
        return discountEmployee != null;
    }

    /**
     * Sets attributes in clone of this object.
     *
     * @param newClass new instance of object
     */
    public void setCloneAttributes(AbstractItemDiscountStrategy newClass)
    {
        super.setCloneAttributes(newClass);

        if (itemDiscountAmount != null)
        {
            newClass.setItemDiscountAmount((CurrencyIfc)getItemDiscountAmount().clone());
        }
        newClass.setDamageDiscount(isDamageDiscount());
        newClass.setDiscountEmployee(getDiscountEmployeeID());
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.DiscountRule#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean objectIsEqual = (obj instanceof AbstractItemDiscountStrategy) ? super.equals(obj) : false;

        if (objectIsEqual)
        {
            AbstractItemDiscountStrategy other = (AbstractItemDiscountStrategy)obj;
            objectIsEqual = Util.isObjectEqual(this.getItemDiscountAmount(), other.getItemDiscountAmount()) &&
                    (this.isDamageDiscount() == other.isDamageDiscount()) &&
                    Util.isObjectEqual(this.getDiscountEmployeeID(), other.getDiscountEmployeeID());
        }
        return objectIsEqual;
    }


    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.DiscountRule#toString()
     */
    @Override
    public String toString()
    {
        // result string
        StringBuilder strResult = new StringBuilder(super.toString());
        if (getAssignmentBasis() == ASSIGNMENT_EMPLOYEE)
        {
            strResult.append("Discount Employee:          [")
            .append(this.getDiscountEmployeeID()).append("]")
            .append(Util.EOL);
        }
        strResult.append("ItemDiscountAmount:           [").append(this.getItemDiscountAmount()).append("]");
        if (isDamageDiscount())
        {
            strResult.append("Damage Discount:          [true]")
            .append(Util.EOL);
        }
        else
        {
            strResult.append("Damage Discount:          [false]")
            .append(Util.EOL);
        }
        // pass back result
        return(strResult.toString());
    }

    /**
     * Retrieves the Team Connection revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    /**
     * Retrieves the PricingGroupID.
     * @return pricingGroupID
     */
    public int getPricingGroupID()
    {
        return pricingGroupID;
    }

    /**
     * Sets the PricingGroupID
     * @param pricingGroupID
     */
    public void setPricingGroupID(int pricingGroupID)
    {
        this.pricingGroupID = pricingGroupID;
    }
    
    //---------------------------------------------------------------------
    /**
     * Return an item discount strategy to calculate prorated order item 
     * discount for pickup or cancel
     * @param qty the quantity to pick up or cancel
     * @param pickedupQty the quantity already picked up 
     * @param pendingQty the total pending quantity
     * @param discountStatus the order item discount status
     *
     * @return the item discount strategy
     */
    //---------------------------------------------------------------------
    public ItemDiscountStrategyIfc getProratedOrderItemDiscountForPickupOrCancel(
            BigDecimal qty, BigDecimal pickedupQty, BigDecimal pendingQty, 
            OrderItemDiscountStatusIfc discountStatus)
    {
        ItemDiscountAuditStrategyIfc itemDiscountAudit = createNewProratedItemDiscountAudit();
        CurrencyIfc pendingDiscountAmt = discountStatus.getTotalAmount().
                    subtract(discountStatus.getCompletedAmount()).
                    subtract(discountStatus.getCancelledAmount());
            
        // qty is always positive 
        CurrencyIfc discountAmount = pendingDiscountAmt.prorate(qty, pickedupQty, pendingQty); 
        itemDiscountAudit.setDiscountAmount(discountAmount);

        return itemDiscountAudit;
    }
    
    //---------------------------------------------------------------------
    /**
     * Return an item discount strategy to calculate prorated order item 
     * discount for return
     * @param returnQty the quantity to return
     * @param returnableQty the returnable quantity
     * @param discountStatus the order item discount status
     *
     * @return the item discount strategy
     */
    //---------------------------------------------------------------------
    public ItemDiscountStrategyIfc getProratedOrderItemDiscountForReturn(
            BigDecimal returnQty, BigDecimal returnableQty, 
            OrderItemDiscountStatusIfc discountStatus)
    {
        ItemDiscountAuditStrategyIfc itemDiscountAudit = createNewProratedItemDiscountAudit();
        CurrencyIfc returnableDiscountAmt = discountStatus.getCompletedAmount().subtract(
                discountStatus.getReturnedAmount());
       
        // returnQty is negative for return
        CurrencyIfc discountAmount = returnableDiscountAmt.prorate(returnQty.abs(), returnableQty); 
        discountAmount = discountAmount.negate();
        itemDiscountAudit.setDiscountAmount(discountAmount);

        if (itemDiscountAudit instanceof ItemDiscountAuditIfc )
            ((ItemDiscountAuditIfc)itemDiscountAudit).setUnitDiscountAmount(discountAmount);
        
        return itemDiscountAudit;
    }

    //---------------------------------------------------------------------
    /**
     * @return a new proroated item discount audit strategy
     */
    //---------------------------------------------------------------------
    protected ItemDiscountAuditStrategyIfc createNewProratedItemDiscountAudit()
    {
        ItemDiscountAuditIfc itemDiscountAudit = DomainGateway.getFactory().getItemDiscountAuditInstance();
        setCloneAttributes((ItemDiscountAudit)itemDiscountAudit);
        itemDiscountAudit.setOriginalDiscountMethod(getDiscountMethod());
        
        return itemDiscountAudit;
    }
}

