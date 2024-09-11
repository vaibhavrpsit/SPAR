/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/OrderReceiptParameterBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:40 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   12/11/08 - add set/getServiceType to api for printing
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         4/30/2007 7:00:39 PM   Alan N. Sinton  CR
 *       26485 - Merge from v12.0_temp.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt;

import oracle.retail.stores.domain.order.OrderIfc;

/**
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class OrderReceiptParameterBean extends ReceiptParameterBean implements OrderReceiptParameterBeanIfc
{
    private static final long serialVersionUID = -3179006764913618324L;

    /** Reference to the OrderIfc instance. */
    protected OrderIfc order;
    protected int serviceType;

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.OrderReceiptParameterBeanIfc#getOrder()
     */
    public OrderIfc getOrder()
    {
        return this.order;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.OrderReceiptParameterBeanIfc#setOrder(oracle.retail.stores.domain.order.OrderIfc)
     */
    public void setOrder(OrderIfc order)
    {
        this.order = order;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.OrderReceiptParameterBeanIfc#getServiceType()
     */
    public int getServiceType()
    {
        return serviceType;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.OrderReceiptParameterBeanIfc#setServiceType(int)
     */
    public void setServiceType(int serviceType)
    {
        this.serviceType = serviceType;
    }
}