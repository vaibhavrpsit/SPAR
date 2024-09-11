/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SearchItemListBean.java /main/5 2014/05/20 12:14:37 cgreene Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    09/11/14 - Sorting item search result by default for a  
                         given column when it gets displayed first time.
* cgreene     05/20/14 - refactor list model sorting
* abhinavs    05/11/14 - Sorting Item search results enhancement
* yiqzhao     11/07/12 - Enable and disable Related Item local navigation
*                        button based the item constains related items or not.
* hyin        08/31/12 - meta tag search POS UI work.
* hyin        08/30/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.ui.beans;

import java.util.Arrays;
import java.util.List;

import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;

import oracle.retail.stores.common.item.ItemSearchResult;
import oracle.retail.stores.common.item.RelatedItemSearchResult;
import oracle.retail.stores.domain.stock.ItemImage;
import oracle.retail.stores.domain.stock.ItemImageIfc;
import oracle.retail.stores.domain.stock.RelatedItemGroupIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.behavior.EnableButtonListener;
import oracle.retail.stores.pos.ui.behavior.LocalButtonListener;


public class SearchItemListBean extends ListBean
{
    private static final long serialVersionUID = -4197391412784549029L;

    /** The local enable button listener * */
    protected EnableButtonListener localButtonListener = null;

    public static final String CLASSNAME = "SearchItemListBean";

    protected SearchItemListBeanModel beanModel = new SearchItemListBeanModel();

    /**
     * constructor
     */
    public SearchItemListBean()
    {
    }
    
    /**
     * Activate this screen.
     */
    @Override
    public void activate()
    {
        super.activate();
        setDefaultSortColumn();
    }

    /**
     * Sets the information to be shown by this bean.
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set SearchItemListBean model to null");
        }
        if (model instanceof SearchItemListBeanModel)
        {
            beanModel = (SearchItemListBeanModel) model;
            updateBean();
        }
    }

    /**
     * update model for currently selected item
     */
    @Override
    public void updateModel()
    {
        int index = list.getSelectedRow();
        beanModel.setSelectedItem((ItemSearchResult)list.getModel().getElementAt(index));
    }

    /**
     * update the model if it's been changed
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void updateBean()
    {
        // mark all the images as loading if they have not been loaded yet
        List<ItemSearchResult> items = beanModel.getItemList();
        for (int i=0; i<items.size(); i++)
        {
            ItemSearchResult item = items.get(i);
            ItemImageIfc image = populateImage(item);
            loadImage(image, i);
        }

        POSListModel posListModel = new POSListModel(items);
        getList().setModel(posListModel);

        Object item = beanModel.getSelectedItem();
        if (item != null)
        {
            list.setSelectedValue(item, true);
        }

        manageRelatedItemButtons();
    }

    /**
     * create an ItemImageIfc object from ItemSearchResult
     * @param item
     * @return ItemImageIfc
     */
    protected ItemImageIfc populateImage(ItemSearchResult item)
    {
        ItemImageIfc image = new ItemImage();
        byte[] imageData = item.getImageBlob();
        String imageUrl = item.getImageLocation();

        if ((imageData != null) && (imageData.length > 0))
        {
            image.setImageBlob(imageData);
        }else
        {
            image.setImageLocation(imageUrl);
        }

        return image;
    }

    /**
     * Sets the visibility of this bean.
     */
    @Override
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        // if visible is true, set the selection and request focus
        if (aFlag)
        {
            if (list.getSelectedIndex() == -1 && list.getModel().getSize() > 0 && selectionMode != NO_SELECTION_MODE)
            {
                list.setSelectedIndex(0);
            }

            list.setFocusable(true);
            list.setFocusTraversalKeysEnabled(false);
            setCurrentFocus(list);
        }

    }

    /**
     * Adds (actually sets) the enable button listener on the bean.
     *
     * @param listener
     */
    public void addLocalButtonListener(LocalButtonListener listener)
    {
        localButtonListener = listener;
    }

    /**
     * Removes the enable button listener from the bean.
     *
     * @param listener
     */
    public void removeLocalButtonListener(LocalButtonListener listener)
    {
        localButtonListener = null;
    }

    /**
     * Set related item type buttons for selected pos item
     */
    public void updateLocalNavigationButtons(ListSelectionEvent e)
    {
        this.repaint();
    }

    /**
     *
     */
    protected void manageRelatedItemButtons()
    {
        if (localButtonListener != null)
        {
            localButtonListener.enableButton(RelatedItemGroupIfc.AUTOMATIC, false);
            localButtonListener.enableButton(RelatedItemGroupIfc.CROSS_SELL, false);
            localButtonListener.enableButton(RelatedItemGroupIfc.UPSELL, false);
            localButtonListener.enableButton(RelatedItemGroupIfc.SUBSTITUTE, false);

            ItemSearchResult itemSearchResult = beanModel.getSelectedItem();
            if ( itemSearchResult!=null)
            {
                List<RelatedItemSearchResult> relatedItemSearchResults = itemSearchResult.getRelatedItemSearchResult();
                if ( relatedItemSearchResults!=null && relatedItemSearchResults.size() >0 )
                {
                    for (RelatedItemSearchResult relatedItemSearchResult: relatedItemSearchResults)
                    {
                        if (relatedItemSearchResult.getRelatedItemTypeCode().equals(RelatedItemGroupIfc.AUTOMATIC))
                            localButtonListener.enableButton(RelatedItemGroupIfc.AUTOMATIC, true);
                        else if (relatedItemSearchResult.getRelatedItemTypeCode().equals(RelatedItemGroupIfc.CROSS_SELL))
                            localButtonListener.enableButton(RelatedItemGroupIfc.CROSS_SELL, true);
                        else if (relatedItemSearchResult.getRelatedItemTypeCode().equals(RelatedItemGroupIfc.UPSELL))
                            localButtonListener.enableButton(RelatedItemGroupIfc.UPSELL, true);
                        else if (relatedItemSearchResult.getRelatedItemTypeCode().equals(RelatedItemGroupIfc.SUBSTITUTE))
                            localButtonListener.enableButton(RelatedItemGroupIfc.SUBSTITUTE, true);
                    }
                }
            }
        }
    }
    
    /**
     * This method sorts and set the sorting icon
     * on the default sorted column. By default it has to be 
     * in ascending order. 
     */
    protected void setDefaultSortColumn()
    {
        if (super.getColumnSortProperties() != null && super.getDefaultSortedColumn() != null)
        {
            String defaultSortColumn = super.getDefaultSortedColumn();
            for (String beanProperty : super.getColumnSortProperties())
            {
                int index = Arrays.asList(super.getColumnSortProperties()).indexOf(beanProperty);
                if (defaultSortColumn.equalsIgnoreCase(beanProperty))
                {
                      super.getHeaderBean().setSortOrder(SortOrder.ASCENDING, index);
                      super.getHeaderBean().sortList();
                }
            }
        }
    }
}
