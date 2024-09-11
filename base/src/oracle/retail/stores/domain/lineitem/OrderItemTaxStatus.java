/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/lineitem/OrderItemTaxStatus.java /main/1 2012/11/21 14:28:21 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         11/21/12 - added support for order item discount and tax status
* sgu         11/20/12 - add new class
* sgu         11/20/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.domain.lineitem;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.utility.Util;

public class OrderItemTaxStatus implements OrderItemTaxStatusIfc
{
    /**
     * serial version UID
     */
    private static final long serialVersionUID = -389757108489824671L;

    /**
     * revision number supplied by source-code-control system
     **/
    public static String revisionNumber = "$Revision: /main/1 $";
    
    protected int authorityID;
    protected int taxGroupID;
    protected int typeCode;
    protected CurrencyIfc totalAmount;
    protected CurrencyIfc completedAmount;
    protected CurrencyIfc cancelledAmount;
    protected CurrencyIfc returnedAmount;
    
    //--------------------------------------------------------------------------
    // --
    /**
     * Constructs OrderItemTaxStatus object.
     * <P>
     **/
    //--------------------------------------------------------------------------
    // --
    public OrderItemTaxStatus()
    { // begin OrderItemTaxStatus()
        totalAmount = DomainGateway.getBaseCurrencyInstance();
        completedAmount = DomainGateway.getBaseCurrencyInstance();
        cancelledAmount = DomainGateway.getBaseCurrencyInstance();
        returnedAmount = DomainGateway.getBaseCurrencyInstance();
    } // end OrderItemTaxStatus()
    
    //---------------------------------------------------------------------
    /**
     * Retrieve tax authority ID
     * @return tax authority ID
     */
    //---------------------------------------------------------------------
    public int getAuthorityID()
    {
        return authorityID;
    }

    //---------------------------------------------------------------------
    /**
     * Set tax authority ID
     * @param authorityID tax authority ID
     */
    //---------------------------------------------------------------------
    public void setAuthorityID(int authorityID) 
    {
        this.authorityID = authorityID;
    }

    //---------------------------------------------------------------------
    /**
     * Retrieve tax group ID
     * @return tax group ID
     */
    //---------------------------------------------------------------------
    public int getTaxGroupID() 
    {
        return taxGroupID;
    }

    //---------------------------------------------------------------------
    /**
     * Set tax group ID
     * @param taxGroupID tax group ID
     */
    //---------------------------------------------------------------------
    public void setTaxGroupID(int taxGroupID) 
    {
        this.taxGroupID = taxGroupID;
    }

    //---------------------------------------------------------------------
    /**
     * Retrieve tax type code
     * @return tax type code
     */
    //---------------------------------------------------------------------
    public int getTypeCode() 
    {
        return typeCode;
    }

    //---------------------------------------------------------------------
    /**
     * Set tax type code
     * @param typeCode tax type code
     */
    //---------------------------------------------------------------------
    public void setTypeCode(int typeCode) 
    {
        this.typeCode = typeCode;
    }

    //---------------------------------------------------------------------
    /**
     * Retrieves total tax amount
     * @return total tax amount
     */
    //---------------------------------------------------------------------
    public CurrencyIfc getTotalAmount() 
    {
        return totalAmount;
    }
    
    //---------------------------------------------------------------------
    /**
     * Set total tax amount
     * @param totalAmount total tax amount
     */
    //---------------------------------------------------------------------
    public void setTotalAmount(CurrencyIfc totalAmount) 
    {
        this.totalAmount = totalAmount;
    }
    
    //---------------------------------------------------------------------
    /**
     * Retrieves completed tax amount
     * @return completed tax amount
     */
    //---------------------------------------------------------------------
    public CurrencyIfc getCompletedAmount() 
    {
        return completedAmount;
    }
    
    //---------------------------------------------------------------------
    /**
     * Set completed tax amount
     * @param completedAmount completed tax amount
     */
    //---------------------------------------------------------------------
    public void setCompletedAmount(CurrencyIfc completedAmount) 
    {
        this.completedAmount = completedAmount;
    }
    
    //---------------------------------------------------------------------
    /**
     * Retrieves cancelled tax amount
     * @return cancelled tax amount
     */
    //---------------------------------------------------------------------
    public CurrencyIfc getCancelledAmount() 
    {
        return cancelledAmount;
    }
    
    //---------------------------------------------------------------------
    /**
     * Set cancelled tax amount
     * @param cancelledAmount cancelled tax amount
     */
    //---------------------------------------------------------------------
    public void setCancelledAmount(CurrencyIfc cancelledAmount) 
    {
        this.cancelledAmount = cancelledAmount;
    }
    
    //---------------------------------------------------------------------
    /**
     * Retrieves returned tax amount
     * @return returned tax amount
     */
    //---------------------------------------------------------------------
    public CurrencyIfc getReturnedAmount() 
    {
        return returnedAmount;
    }
    
    //---------------------------------------------------------------------
    /**
     * Set returned tax amount
     * @param returnedAmount returned tax amount
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
        OrderItemTaxStatusIfc c = new OrderItemTaxStatus();

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
    public void setCloneAttributes(OrderItemTaxStatusIfc newClass)
    { // begin setCloneAttributes()
        newClass.setAuthorityID(getAuthorityID());
        newClass.setTaxGroupID(getTaxGroupID());
        newClass.setTypeCode(getTypeCode());
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
        if (obj instanceof OrderItemTaxStatusIfc)
        { // begin compare objects

            OrderItemTaxStatusIfc c = (OrderItemTaxStatusIfc)obj; // downcast the input
            // object

            // compare all the attributes of OrderItemStatus
            if ((getAuthorityID() == c.getAuthorityID())
                    && (getTaxGroupID() == c.getTaxGroupID())
                    && (getTypeCode() == c.getTypeCode())
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
        strResult.append(Util.formatToStringEntry("authorityID", getAuthorityID())).append(
                Util.formatToStringEntry("taxGroupID", getTaxGroupID())).append(
                Util.formatToStringEntry("typeCode", getTypeCode())).append(
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

