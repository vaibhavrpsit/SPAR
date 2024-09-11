/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SearchItemListBeanModel.java /main/1 2012/09/04 16:43:06 hyin Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* hyin        08/31/12 - meta tag search POS UI work.
* hyin        08/30/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.ui.beans;

import java.beans.PropertyChangeSupport;
import java.util.List;


import oracle.retail.stores.common.item.ItemSearchResult;

public class SearchItemListBeanModel extends POSBaseBeanModel
{

    /**
     * 
     */
    private static final long serialVersionUID = 7644106686332276947L;
    
    /**
     * property change listener
     */
    protected transient PropertyChangeSupport propertyChange;

    /**
     * line items to be display on screen
     */
    protected List<ItemSearchResult> lineItems;
    
    /**
     * Indicates the selected item.
     */
    protected ItemSearchResult selectedItem = null;
    
    /**
     * constructor
     */
    public SearchItemListBeanModel()
    {
        super();
        initialize();
    }
    
    /**
     * Initialize list
     */
    public void initialize()
    {
        lineItems = null;
    }
    
    /**
     * Returns list of matching items
     * @return lineItems 
     */
    public List<ItemSearchResult> getItemList()
    {
        return lineItems;
    }
    
    /**
     * Adds an item to the item list
     * @param item
     */
    public void addItem(ItemSearchResult item)
    {
        lineItems.add(item);
    }
    
    /**
     * set the item list
     * @param items
     */
    public void setItemList(List<ItemSearchResult> items)
    {
        this.lineItems = items;
    }
    
    /**
     * return selected item
     * @return
     */
    public ItemSearchResult getSelectedItem()
    {
        return selectedItem;
    }
    
    /**
     * set selected item
     * @param item
     */
    public void setSelectedItem(ItemSearchResult item)
    {
        selectedItem = item;
    }
}