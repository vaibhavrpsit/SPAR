/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/ItemIndexContainer.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:26 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:37 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/03/03 22:31:23  epd
 *   @scr 3561 Returns updates - select highest price item
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;


/**
 * This class simply associates a SaleReturnLineItem and
 * the index at which this line item exists within the 
 * collection of line items in the transaction.
 *
 */
public class ItemIndexContainer
{
    /**
     * An item from a sale return transaction
     */
    protected SaleReturnLineItemIfc item;
    
    /**
     * The index of the list of line items on 
     * the transaction at which the item exists
     */
    protected int index;
    
    /**
     * @return Returns the index.
     */
    public int getIndex()
    {
        return index;
    }
    
    /**
     * @param index The index to set.
     */
    public void setIndex(int index)
    {
        this.index = index;
    }
    
    /**
     * @return Returns the item.
     */
    public SaleReturnLineItemIfc getItem()
    {
        return item;
    }
    
    /**
     * @param item The item to set.
     */
    public void setItem(SaleReturnLineItemIfc item)
    {
        this.item = item;
    }
}
