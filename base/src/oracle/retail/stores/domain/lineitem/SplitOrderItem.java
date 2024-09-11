/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/lineitem/SplitOrderItem.java /main/3 2013/04/16 13:32:47 vtemker Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* vtemker     04/16/13 - Moved constants in OrderLineItemIfc to
*                        OrderConstantsIfc in common project
* sgu         10/29/12 - disable pickup and cancel buttons when not applicable
* sgu         10/18/12 - add clone, equal, and toString methods
* sgu         10/04/12 - add suport to split order line for partial
*                        pickup/cancel
* sgu         09/20/12 - add new class
* sgu         09/20/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.domain.lineitem;

import java.math.BigDecimal;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.utility.EYSStatusIfc;
import oracle.retail.stores.foundation.utility.Util;

public class SplitOrderItem implements SplitOrderItemIfc
{
    private static final long serialVersionUID = 4046324682538258632L;

    /**
     * The original order line item split from
     */
    private SaleReturnLineItemIfc originalOrderLineItem = null;

    /**
     * quantity of ordered item
     */
    private BigDecimal quantity = null;

    /**
     * status of ordered item
     **/
    protected EYSStatusIfc status = null;

    //---------------------------------------------------------------------
    /**
     * private default constructor
     */
    //---------------------------------------------------------------------
    protected SplitOrderItem()
    {
    }

    //---------------------------------------------------------------------
    /**
     * Create a split order item
     * @param originalOrderLineItem
     * @param quantity
     * @param status
     */
    //---------------------------------------------------------------------
    public SplitOrderItem(SaleReturnLineItemIfc originalOrderLineItem,
            BigDecimal quantity, int statusValue)
    {
        EYSStatusIfc status = DomainGateway.getFactory().getEYSStatusInstance();
        status.setDescriptors(OrderConstantsIfc.ORDER_ITEM_STATUS_DESCRIPTORS);
        status.setStatus(statusValue);

        setOriginalOrderLineItem(originalOrderLineItem);
        setQuantity(quantity);
        setStatus(status);
    }

    //---------------------------------------------------------------------
    /**
     * @return original order line item
     */
    //---------------------------------------------------------------------
    public SaleReturnLineItemIfc getOriginalOrderLineItem()
    {
        return originalOrderLineItem;
    }

    //---------------------------------------------------------------------
    /**
     * Set the original order line item
     *
     * @param originalOrderLineItem
     */
    //---------------------------------------------------------------------
    public void setOriginalOrderLineItem(SaleReturnLineItemIfc originalOrderLineItem)
    {
        this.originalOrderLineItem = originalOrderLineItem;
    }

    //---------------------------------------------------------------------
    /**
     * @return the quantity ordered
     */
    //---------------------------------------------------------------------
    public BigDecimal getQuantity()
    {
        return quantity;
    }

    //---------------------------------------------------------------------
    /**
     * Set the quantity ordered
     *
     * @param quantity the quantity
     */
    //---------------------------------------------------------------------
    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    //---------------------------------------------------------------------
    /**
     * @return order item status
     */
    //---------------------------------------------------------------------
    public EYSStatusIfc getStatus()
    {
        return status;
    }

    //---------------------------------------------------------------------
    /**
     * Set order item status
     *
     * @param status
     */
    //---------------------------------------------------------------------
    public void setStatus(EYSStatusIfc status)
    {
        this.status = status;
    }

    //---------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        @return Object clone of this object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        SplitOrderItemIfc c = new SplitOrderItem();

        // set values
        setCloneAttributes(c);

        // pass back Object
        return c;
    }

    //--------------------------------------------------------------------------
    // --
    /**
     * Sets attributes in clone of this object.
     * <P>
     *
     * @param newClass new instance of object
     **/
    //--------------------------------------------------------------------------
    // --
    public void setCloneAttributes(SplitOrderItemIfc newClass)
    { // begin setCloneAttributes()
        newClass.setOriginalOrderLineItem(this.getOriginalOrderLineItem());
        newClass.setQuantity(this.getQuantity());
        newClass.setStatus((EYSStatusIfc)this.getStatus().clone());
    }

    //---------------------------------------------------------------------
    /**
        Determine if two objects are identical. <P>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //---------------------------------------------------------------------
    public boolean equals(Object obj)
    {
        boolean isEqual = false;
        // confirm object instanceof this object
        if (obj instanceof SplitOrderItemIfc)
        { // begin compare objects

            SplitOrderItemIfc c = (SplitOrderItemIfc)obj; // downcast the input
            // object

            // compare all the attributes of SplitOrderItemIfc
            if (Util.isObjectEqual(getOriginalOrderLineItem(), c.getOriginalOrderLineItem()) &&
                    Util.isObjectEqual(getQuantity(), c.getQuantity()) &&
                    Util.isObjectEqual(getStatus(), c.getStatus()))
            {
                isEqual = true;
            }
        }
        return isEqual;
    }

    //---------------------------------------------------------------------
    /**
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        StringBuilder strResult = new StringBuilder();
        // add attributes to string
        strResult.append(Util.formatToStringEntry("originalOrderLineItem", getOriginalOrderLineItem())).append(
                Util.formatToStringEntry("quantity", getQuantity())).append(
                Util.formatToStringEntry("status", getStatus()));
        // pass back result
        return (strResult.toString());
    }
}