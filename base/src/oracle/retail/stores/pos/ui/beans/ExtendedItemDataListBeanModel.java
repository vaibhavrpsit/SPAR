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

import java.util.List;

/**
 * UI Bean Model for display of extended item data from an external commerce system.
 * @since 14.1
 *
 */
public class ExtendedItemDataListBeanModel extends POSBaseBeanModel
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7814358436258911846L;

    /** List of {@link ExtendedItemDataModel}. */
    private List<ExtendedItemDataBeanModel> itemModels;

    /**
     * Returns the <code>itemModels</code> value.
     * @return the itemModels
     */
    public List<ExtendedItemDataBeanModel> getItemModels()
    {
        return itemModels;
    }

    /**
     * Sets the <code>itemModels</code> value.
     * @param items the itemModels to set
     */
    public void setItemModels(List<ExtendedItemDataBeanModel> itemModels)
    {
        this.itemModels = itemModels;
    }

}
