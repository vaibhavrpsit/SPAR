/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/ItemIndexContainerComparator.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/27/10 - Fixed warning messages.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:26 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:37 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/10 14:16:46  baa
 *   @scr 0 fix javadoc warnings
 *
 *   Revision 1.2  2004/03/05 21:46:58  epd
 *   @scr 3561 Updates to implement select highest price item
 *
 *   Revision 1.1  2004/03/03 22:31:23  epd
 *   @scr 3561 Returns updates - select highest price item
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

import java.util.Comparator;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;


/**
 * Sorts ItemIndexContainers in descending order according
 * the the extended discounted selling price of their contained
 * Sale Return line item
 */
public class ItemIndexContainerComparator implements Comparator<ItemIndexContainer>
{

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     * @param arg0
     * @param arg1
     */
    public int compare(ItemIndexContainer arg0, ItemIndexContainer arg1)
    {
        int result = 0;
        
        SaleReturnLineItemIfc item0 = arg0.getItem();
        SaleReturnLineItemIfc item1 = arg1.getItem();
        
        switch (item0.getExtendedDiscountedSellingPrice().compareTo(item1.getExtendedDiscountedSellingPrice()))
        {
            case -1: result = 1; break;
            case 1: result = -1; break;
            // no need for case 0 or default
        }
        return result;
    }

}
