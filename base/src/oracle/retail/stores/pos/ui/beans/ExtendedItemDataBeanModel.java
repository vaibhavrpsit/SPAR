/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/07/14 - Improve performance of retrieving item images.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.beans;

import java.io.Serializable;

import oracle.retail.stores.common.item.ExtendedItemData;
import oracle.retail.stores.domain.stock.ItemImageIfc;

/**
 * UI Bean Model for display of extended item data from an external commerce system.
 * @since 14.1
 *
 */
public class ExtendedItemDataBeanModel implements Serializable
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8707244003489332722L;
    
    /** {@link ExtendedItemData} item. */
    private ExtendedItemData item;
    
    /** {@link ItemImageIfc}  itemImage. */
    private ItemImageIfc itemImage;

    /**
     * Returns the <code>items</code> value.
     * @return the items
     */
    public ExtendedItemData getItem()
    {
        return item;
    }

    /**
     * Sets the <code>items</code> value.
     * @param items the items to set
     */
    public void setItem(ExtendedItemData item)
    {
        this.item = item;
    }

    /**
     * @return the itemImage
     */
    public ItemImageIfc getItemImage()
    {
        return itemImage;
    }

    /**
     * @param itemImage the itemImage to set
     */
    public void setItemImage(ItemImageIfc itemImage)
    {
        this.itemImage = itemImage;
    }

    
}
