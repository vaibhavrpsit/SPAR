/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/AbstractItemDiscountAuditStrategy.java /main/5 2014/06/23 13:03:35 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         06/18/14 - convert transactional item discount to item discount
*                        for order pickup/cancel
* jswan       12/14/12 - Modified to convert Transaction Discounts into Item
*                        discount with returning items from and order.
* sgu         12/10/12 - prorate discount for order pickup and return
* sgu         10/15/12 - change return type to ItemDiscountStrategyIfc
* sgu         10/09/12 - create pickup cancel order transaction from an order
* sgu         10/05/12 - add new function to return prorated item discount
* sgu         08/20/12 - fix defects in xc order discount rule reading
* sgu         08/17/12 - refactor discount audit
* sgu         08/17/12 - add new class
* sgu         08/17/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.domain.discount;

public abstract class AbstractItemDiscountAuditStrategy extends AbstractItemDiscountStrategy
implements ItemDiscountAuditStrategyIfc
{
    protected int originalDiscountMethod = DISCOUNT_METHOD_AMOUNT;

    //---------------------------------------------------------------------
    /**
     * For a discount audit, the discount method is always DISCOUNT_METHOD_AMOUNT
     * The original discount method stores the method used when the discount is applied.
     */
    //---------------------------------------------------------------------
    public AbstractItemDiscountAuditStrategy()
    {
        discountMethod = DISCOUNT_METHOD_AMOUNT;
    }


    //---------------------------------------------------------------------
    /**
        Retrieves description code. <P>
        @return description code
    **/
    //---------------------------------------------------------------------
    public int getDescriptionCode()
    {
        // Use the original discount method for display
        String code = Integer.toString(getAssignmentBasis()) +
                                        getOriginalDiscountMethod() +
                                        getDiscountScope() +
                                        getAccountingMethod();
        return Integer.valueOf(code);
    }

    //---------------------------------------------------------------------
    /**
     * Sets discount by amount or rate.
     *
     * @param value discount by amount or rate
     */
    //---------------------------------------------------------------------
    public void setDiscountMethod(int value)
    {
        //Can't change discount method of a discount audit
        //It is always DISCOUNT_METHOD_AMOUNT
    }

    //---------------------------------------------------------------------
    /**
        Retrieves original discount method. <P>
        @return method original discount method
    **/
    //---------------------------------------------------------------------
    public int getOriginalDiscountMethod()
    {
        return originalDiscountMethod;
    }

    //---------------------------------------------------------------------
    /**
        Sets original discount method. <P>
        @param value original discount method
    **/
    //---------------------------------------------------------------------
    public void setOriginalDiscountMethod(int value)
    {
        originalDiscountMethod = value;
    }

    //---------------------------------------------------------------------
    /**
        Sets attributes in clone. <P>
        @param newClass new instance of class
    **/
    //---------------------------------------------------------------------
    protected void setCloneAttributes(AbstractItemDiscountAuditStrategy newClass)
    {
        super.setCloneAttributes(newClass);
        newClass.setOriginalDiscountMethod(originalDiscountMethod);
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
        if (obj instanceof AbstractItemDiscountAuditStrategy)
        {
            AbstractItemDiscountAuditStrategy strategy = (AbstractItemDiscountAuditStrategy)obj;

            isEqual = super.equals(obj) &&
            (getOriginalDiscountMethod() == strategy.getOriginalDiscountMethod());
        }
        return isEqual;
    }
    
    //---------------------------------------------------------------------
    /**
     * @return a new proroated item discount audit
     */
    //---------------------------------------------------------------------
    protected ItemDiscountAuditStrategyIfc createNewProratedItemDiscountAudit()
    {
        ItemDiscountAuditStrategyIfc itemDiscountAudit = (ItemDiscountAuditStrategyIfc)clone();
        return itemDiscountAudit;
    }
}

