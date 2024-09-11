/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/OrderStatusChangeTransaction.java /main/1 2013/06/24 12:27:17 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/19/13 - Modified to perform the status update of an Order in
 *                         the context of a transaction.
 *                         
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.transaction;
// java imports
import java.io.Serializable;

import oracle.retail.stores.domain.order.OrderIfc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
//--------------------------------------------------------------------------
/**
    This class helps track changes to an Order's status when only the status 
    of line items change to filled or pending.
    <p>
    @version $Revision: /main/1 $
**/
//--------------------------------------------------------------------------
public class OrderStatusChangeTransaction
extends StatusChangeTransaction
implements OrderStatusChangeTransactionIfc, Serializable
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4174984995887250887L;
    
    /**
     * Order object for Order Table Update.
     */
    protected OrderIfc order = null;
    
    /**
     * Constructs VoidTransaction object.
     */
    public OrderStatusChangeTransaction()
    {
        super.initialize();
    }

    //---------------------------------------------------------------------
    /**
        Clones StatusChangeTransaction object.
        <p>
        @return instance of StatusChangeTransaction object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        // instantiate new object
        OrderStatusChangeTransaction trans = new OrderStatusChangeTransaction();

        setCloneAttributes(trans);

        // pass back object
        return((Object)trans);
    }

    // ---------------------------------------------------------------------
    /**
     * Sets attributes in new instance of class.
     * <P>
     *
     * @param newClass new instance of class
     */
    // ---------------------------------------------------------------------
    public void setCloneAttributes(OrderStatusChangeTransaction newClass)
    {
        // set attributes in super class
        super.setCloneAttributes(newClass);
        if (order != null)
        {
            newClass.setOrder((OrderIfc)order.clone());
        }
        // set attributes
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof OrderStatusChangeTransaction)
        {
            OrderStatusChangeTransaction other = (OrderStatusChangeTransaction)o;
            EqualsBuilder builder = new EqualsBuilder();
            appendEquals(builder, other);
            return builder.isEquals();
        }
        return false;
    }

    /**
     * Add objects for comparison to the builder. Overriding methods should also
     * call super.appendEquals(EqualsBuilder, AuthorizeTransferResponse).
     *
     * @param builder
     * @see #equals(Object)
     */
    protected void appendEquals(EqualsBuilder builder, OrderStatusChangeTransaction other)
    {
        super.appendEquals(builder, other);
        builder.append(order, other.getOrder());
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);
        appendToString(builder);
        return builder.toString();
    }

    /**
     * Add printable objects to the builder. Overriding methods should also
     * call super.appendToString(ToStringBuilder).
     *
     * @param builder
     * @see #toString()
     */
    protected void appendToString(ToStringBuilder builder)
    {
        super.appendToString(builder);
        builder.append("transactionSummaries", transactionSummaries);
    }

    /**
     * @return the order
     */
    public OrderIfc getOrder()
    {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(OrderIfc order)
    {
        this.order = order;
    }
}
