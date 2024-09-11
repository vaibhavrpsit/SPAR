/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/lineitem/OrderItemDiscountStatus.java /main/2 2012/12/07 12:21:55 jswan Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* jswan       12/06/12 - Modified to support JDBC opertions for order tax and
*                        discount status.
* sgu         11/21/12 - added support for order item discount and tax status
* sgu         11/20/12 - add new class
* sgu         11/20/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.domain.lineitem;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.common.utility.Util;

public class OrderItemDiscountStatus implements OrderItemDiscountStatusIfc
{
    /**
     * serial version UID
     */
    private static final long serialVersionUID = 6572769970806477722L;
    
    /**
     * revision number supplied by source-code-control system
     **/
    public static String revisionNumber = "$Revision: /main/2 $";
    
    /** There can more than one discount status associated with on order.
     *  This value distinguishes one status from an other. */
    protected int lineNumber;
    /** The total amount of discount associated with this status */
    protected CurrencyIfc totalAmount;
    /** The amount of discount that the customer has received associated with this status */
    protected CurrencyIfc completedAmount;
    /** The total amount of discount that has been cancelled associated with this status */
    protected CurrencyIfc cancelledAmount;
    /** The total amount of discount that has been returned associated with this status */
    protected CurrencyIfc returnedAmount;
    
    //--------------------------------------------------------------------------
    // --
    /**
     * Constructs OrderItemDiscountStatus object.
     * <P>
     **/
    //--------------------------------------------------------------------------
    // --
    public OrderItemDiscountStatus()
    { // begin OrderItemDiscountStatus()
        totalAmount = DomainGateway.getBaseCurrencyInstance();
        completedAmount = DomainGateway.getBaseCurrencyInstance();
        cancelledAmount = DomainGateway.getBaseCurrencyInstance();
        returnedAmount = DomainGateway.getBaseCurrencyInstance();
    } // end OrderItemDiscountStatus()

    
    //---------------------------------------------------------------------
    /**
        Retrieves line number.  <P>
        @return int line number
    **/
    //---------------------------------------------------------------------
    public int getLineNumber() {
        return lineNumber;
    }
    
    //---------------------------------------------------------------------
    /**
        Set line number.  <P>
        @param num int line number
    **/
    //---------------------------------------------------------------------
    public void setLineNumber(int lineNumber) 
    {
        this.lineNumber = lineNumber;
    }
   
    //---------------------------------------------------------------------
    /**
     * Retrieves total discount amount
     * @return total discount amount
     */
    //---------------------------------------------------------------------
    public CurrencyIfc getTotalAmount() 
    {
        return totalAmount;
    }
    
    //---------------------------------------------------------------------
    /**
     * Set total discount amount
     * @param totalAmount total discount amount
     */
    //---------------------------------------------------------------------
    public void setTotalAmount(CurrencyIfc totalAmount) 
    {
        this.totalAmount = totalAmount;
    }
    
    //---------------------------------------------------------------------
    /**
     * Retrieves completed discount amount
     * @return completed discount amount
     */
    //---------------------------------------------------------------------
    public CurrencyIfc getCompletedAmount() 
    {
        return completedAmount;
    }
    
    //---------------------------------------------------------------------
    /**
     * Set completed discount amount
     * @param completedAmount completed discount amount
     */
    //---------------------------------------------------------------------
    public void setCompletedAmount(CurrencyIfc completedAmount) 
    {
        this.completedAmount = completedAmount;
    }
    
    //---------------------------------------------------------------------
    /**
     * Retrieves cancelled discount amount
     * @return cancelled discount amount
     */
    //---------------------------------------------------------------------
    public CurrencyIfc getCancelledAmount() 
    {
        return cancelledAmount;
    }
    
    //---------------------------------------------------------------------
    /**
     * Set cancelled discount amount
     * @param cancelledAmount cancelled discount amount
     */
    //---------------------------------------------------------------------
    public void setCancelledAmount(CurrencyIfc cancelledAmount) 
    {
        this.cancelledAmount = cancelledAmount;
    }
    
    //---------------------------------------------------------------------
    /**
     * Retrieves returned discount amount
     * @return returned discount amount
     */
    //---------------------------------------------------------------------
    public CurrencyIfc getReturnedAmount() 
    {
        return returnedAmount;
    }
    
    //---------------------------------------------------------------------
    /**
     * Set returned discount amount
     * @param returnedAmount returned discount amount
     */
    //---------------------------------------------------------------------
    public void setReturnedAmount(CurrencyIfc returnedAmount) 
    {
        this.returnedAmount = returnedAmount;
    }
    
    //--------------------------------------------------------------------------
    // --
    /**
     * Creates clone of this object.
     * <P>
     *
     * @return Object clone of this object
     **/
    //--------------------------------------------------------------------------
    // --
    public Object clone()
    { // begin clone()
        // instantiate new object
        OrderItemDiscountStatusIfc c = new OrderItemDiscountStatus();

        // set values
        setCloneAttributes(c);

        // pass back Object
        return ((Object)c);
    } // end clone()

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
    public void setCloneAttributes(OrderItemDiscountStatusIfc newClass)
    { // begin setCloneAttributes()
        newClass.setLineNumber(getLineNumber());
        if (getTotalAmount() != null)
        {
            newClass.setTotalAmount((CurrencyIfc)getTotalAmount().clone());
        }
        if (getCompletedAmount() != null)
        {
            newClass.setCompletedAmount((CurrencyIfc)getCompletedAmount().clone());
        }
        if (getCancelledAmount() != null)
        {
            newClass.setCancelledAmount((CurrencyIfc)getCancelledAmount().clone());
        }
        if (getReturnedAmount() != null)
        {
            newClass.setReturnedAmount((CurrencyIfc)getReturnedAmount().clone());
        }
    } // end setCloneAttributes()
    
    //--------------------------------------------------------------------------
    // --
    /**
     * Determine if two objects are identical.
     * <P>
     *
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     **/
    //--------------------------------------------------------------------------
    // --
    public boolean equals(Object obj)
    { // begin equals()
        boolean isEqual = true;
        // confirm object instanceof this object
        if (obj instanceof OrderItemDiscountStatusIfc)
        { // begin compare objects

            OrderItemDiscountStatusIfc c = (OrderItemDiscountStatusIfc)obj; // downcast the input
            // object

            // compare all the attributes of OrderItemStatus
            if ((getLineNumber() == c.getLineNumber())
                    && Util.isObjectEqual(getTotalAmount(), c.getTotalAmount())
                    && Util.isObjectEqual(getCompletedAmount(), c.getCompletedAmount())
                    && Util.isObjectEqual(getCancelledAmount(), c.getCancelledAmount())
                    && Util.isObjectEqual(getReturnedAmount(), c.getReturnedAmount()))
            {
                isEqual = true; // set the return code to true
            }
            else
            {
                isEqual = false; // set the return code to false
            }
        } // end compare objects
        else
        {
            isEqual = false;
        }
        return (isEqual);
    } // end equals()

    // ---------------------------------------------------------------------
    /**
     * Returns default display string.
     * <P>
     *
     * @return String representation of object
     **/
    //--------------------------------------------------------------------------
    // --
    public String toString()
    { // begin toString()
        // build result string
        StringBuilder strResult = Util.classToStringHeader("OrderItemDiscountStatus", getRevisionNumber(), hashCode());
        // add attributes to string
        strResult.append(Util.formatToStringEntry("lineNumber", getLineNumber())).append(
                Util.formatToStringEntry("totalAmount", getTotalAmount())).append(
                Util.formatToStringEntry("completedAmount", getCompletedAmount())).append(
                Util.formatToStringEntry("cancelledAmount", getCancelledAmount())).append(
                Util.formatToStringEntry("returnedAmount", getReturnedAmount()));
        return (strResult.toString());
    } // end toString()
    
    //--------------------------------------------------------------------------
    // --
    /**
     * Retrieves the source-code-control system revision number.
     * <P>
     *
     * @return String representation of revision number
     **/
    //--------------------------------------------------------------------------
    // --
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    }

}


