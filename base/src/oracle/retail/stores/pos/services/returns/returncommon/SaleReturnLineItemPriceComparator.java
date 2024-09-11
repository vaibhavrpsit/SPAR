/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/SaleReturnLineItemPriceComparator.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abananan  09/25/14 - Fix for comparing extendedDiscountedSellingPrice.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/27/10 - Fixed warning messages.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:59 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:01 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/03/10 14:16:46  baa
 *   @scr 0 fix javadoc warnings
 *
 *   Revision 1.1  2004/03/04 20:52:46  epd
 *   @scr 3561 Returns.  Updates for highest price item functionality and code cleanup
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
 * @author epd
 *
 * Provides an ordering for sale return line items on the 
 * extended discounted selling price in descending order.
 * That is, highest price items will be at the front of the
 * list.
 */
public class SaleReturnLineItemPriceComparator implements Comparator<SaleReturnLineItemIfc>
{

    /**
     *  Compares prices within salelReturnLineItem objects to determine order
     * according to the highest price
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     * @param arg0
     * @param arg1
     */
    public int compare(SaleReturnLineItemIfc arg0, SaleReturnLineItemIfc arg1)
    {
        int result = 0;
        
        switch(arg0.getExtendedDiscountedSellingPrice().compareTo(arg1.getExtendedDiscountedSellingPrice()))
        {
            case -1: result = 1; break;
            case 1: result = -1; break;
            // don't need a case for 0,
            // or a default
        }
        
        return result;
    }

}
