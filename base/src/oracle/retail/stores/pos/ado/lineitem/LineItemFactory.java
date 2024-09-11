/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/lineitem/LineItemFactory.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:40 mszekely Exp $
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
 *  3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:23:06 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse   
 *
 * Revision 1.3  2004/04/08 20:33:02  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * 
 * Rev 1.1 Jan 21 2004 14:50:04 epd updated to use new GiftCardLineItemADOIfc
 * 
 * Rev 1.0 Nov 04 2003 11:11:44 epd Initial revision.
 * 
 * Rev 1.0 Oct 17 2003 12:31:58 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.lineitem;

import java.util.HashMap;

/**
 * 
 *  
 */
public class LineItemFactory
{
    protected static LineItemFactory instance;

    /**
     * Protected to prevent direct instantiation
     */
    protected LineItemFactory()
    {
    }

    /**
     * Factory method to return singleton instance
     * 
     * @return The TenderFactory instance.
     */
    public static LineItemFactory getInstance()
    {
        if (instance == null)
        {
            instance = new LineItemFactory();
        }
        return instance;
    }

    public LineItemADOIfc createLineItem(HashMap lineItemAttributes)
    {
        LineItemTypeEnum type =
            (LineItemTypeEnum) lineItemAttributes.get(
                LineItemConstants.LINE_ITEM_TYPE);

        LineItemADOIfc item = null;
        if (type == LineItemTypeEnum.TYPE_GIFT_CARD)
        {
            item = createGiftCardLineItem(lineItemAttributes);
        }
        return item;
    }

    protected GiftCardLineItemADOIfc createGiftCardLineItem(HashMap lineItemAttributes)
    {
        return new GiftCardLineItemADO();
    }
}
