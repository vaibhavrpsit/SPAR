/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/RelatedItemListBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED (MM/DD/YY)
*    cgreen 05/27/10 - convert to oracle packaging
*    cgreen 05/26/10 - convert to oracle packaging
*    atirke 09/29/08 - added arraylist for item images
*
*
* ===========================================================================
  $$Log:
  $ 1    360Commerce 1.0         12/13/2005 4:47:07 PM  Barry A. Pape
  $$ 1    .v710     1.0         9/21/2005 1:34:18 PM   Brendan W. Farrell
  $$ 3    NEP67-TSCEnhancements1.2         8/2/2005 9:44:25 AM    Jason L.
  $      DeLeau Make sure the selection mechanism works properly.
  $ 2    NEP67-TSCEnhancements1.1         8/1/2005 4:33:50 PM    Jason L.
  $      DeLeau Minor changes so this gets associated with the correct CR, star
  $      team plug in did not correctly
  $      associate new files.
  $ 1    NEP67-TSCEnhancements1.0         8/1/2005 3:56:30 PM    Jason L.
  $      DeLeau Create screens for selecting related items.
  $$$
 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.beans;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;

import oracle.retail.stores.domain.stock.RelatedItemSummaryIfc;
/**
 *
 * @author jdeleau
 *
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class RelatedItemListBeanModel extends POSBaseBeanModel
{
    /**
     * List of all items
     * @since NEP67
     */
    protected ArrayList itemList = new ArrayList();


    protected ArrayList<Image> itemImages = new ArrayList();


    /**
     * The selected item(s)
     * @since NEP67
     */
    protected RelatedItemSummaryIfc selectedItems[];

    /**
     * Default Constructor
     * @since NEP67
     */
    public RelatedItemListBeanModel()
    {
        super();
    }

    /**
     * Add an item to the list of items in the model.
     * @param item RelatedItemIfc object to add.
     * @since NEP67
     */
    public void addItem(RelatedItemSummaryIfc item)
    {
        itemList.add(item);
    }

    /**
     * @return Returns the itemList.
     * @since NEP67
     */
    public RelatedItemSummaryIfc[] getItemList()
    {
        return (RelatedItemSummaryIfc[]) this.itemList.toArray(new RelatedItemSummaryIfc[0]);
    }

    /**
     * @param itemList The itemList to set.
     * @since NEP67
     */
    public void setItemList(RelatedItemSummaryIfc[] itemList)
    {
        this.itemList.addAll(Arrays.asList(itemList));
    }

    /**
     * @return Returns the selectedItem.
     * @since NEP67
     */
    public RelatedItemSummaryIfc getSelectedItem()
    {
        RelatedItemSummaryIfc selectedItem = null;
        if(this.selectedItems != null && this.selectedItems.length >= 1)
        {
            selectedItem =  this.selectedItems[0];
        }
        return  selectedItem;
    }

    /**
     * Return the list of selected items.
     * @return A list of items, null if none are selected.
     */
    public RelatedItemSummaryIfc[] getSelectedItems()
    {
        return this.selectedItems;
    }

    /**
     * @param selectedItem The selectedItem to set.
     * @since NEP67
     */
    public void setSelectedItem(RelatedItemSummaryIfc selectedItem)
    {
        this.selectedItems = new RelatedItemSummaryIfc[1];
        this.selectedItems[0] = selectedItem;
    }

    /**
     * Set the list of selected items to the list passed in.
     * @param selectedItems
     * @since NEP67
     */
    public void setSelectedItems(Object selectedItems[])
    {
        ArrayList list = new ArrayList();
        for(int i=0; i<selectedItems.length; i++)
        {
            if(selectedItems[i] instanceof RelatedItemSummaryIfc)
            {
                list.add(selectedItems[i]);
            }
        }
        this.selectedItems = (RelatedItemSummaryIfc []) list.toArray(new RelatedItemSummaryIfc[0]);
    }

}
