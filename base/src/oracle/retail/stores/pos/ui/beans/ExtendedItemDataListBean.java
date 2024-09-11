/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ExtendedItemDataListBean.java /main/1 2014/06/03 17:06:10 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/07/14 - Improve performance of retrieving item images.
 *    asinton   06/02/14 - Added support for extended customer data.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import oracle.retail.stores.common.item.ExtendedItemData;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.ItemImageIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.POSListModel;

/**
 * UI Bean for display of extended item data from an external commerce system.
 * @author since 14.1
 *
 */
public class ExtendedItemDataListBean extends ListBean implements ChangeListener
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2364624176767103276L;

    /** Handle to the ExtendedItemDataListBeanModel instance. */
    protected ExtendedItemDataListBeanModel beanModel = null;
    
    /**
     * Default constructor
     */
    public ExtendedItemDataListBean()
    {
        // read only data, currently no actions are taken
        selectionMode = NO_SELECTION_MODE;
        setLayout(new BorderLayout());
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ListBean#updateModel()
     */
    @Override
    public void updateModel()
    {
        // empty, no data is moved back to the model
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ListBean#updateBean()
     */
    @SuppressWarnings({ "unchecked" })
    @Override
    protected void updateBean()
    {
        // mark all the images as loading if they have not been loaded yet
        List<ExtendedItemDataBeanModel> models = beanModel.getItemModels();
        POSListModel posListModel = new POSListModel(models);
        getList().setModel(posListModel);

        // when updated, set the position back to the top
        if(scrollPane != null && scrollPane.getVerticalScrollBar() != null)
        {
            scrollPane.getVerticalScrollBar().setValue(0);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#setModel(oracle.retail.stores.foundation.manager.gui.UIModelIfc)
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set ExtendedItemDataListBeanModel model to null");
        }
        if (model instanceof ExtendedItemDataListBeanModel)
        {
            beanModel = (ExtendedItemDataListBeanModel)model;
            updateBean();
        }
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ListBean#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);

        // if visible is true, request focus
        if (aFlag)
        {
            list.setFocusable(true);
            list.setFocusTraversalKeysEnabled(false);
            setCurrentFocus(list);
        }

    }

    /**
     * This method is called by the tabbedPane when any tab is selected.
     * (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     * @since 14.1
     */
    @Override
    public void stateChanged(ChangeEvent e)
    {
        // Check to see if the selected tab contains this bean
        JTabbedPane tabbedPane = (JTabbedPane)e.getSource();
        Component selected = tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
        // If so, load each item images.
        if (selected == this)
        {
            loadImages();
        }
    }
    
    /**
     * Load the Images from the extended item data in the model.
     */
    public void loadImages()
    {
        // mark all the images as loading if they have not been loaded yet
        List<ExtendedItemDataBeanModel> models = beanModel.getItemModels();
        if(models != null)
        {
            ItemImageIfc itemImage = null;
            ExtendedItemData item = null;
            for (int i = 0; i < models.size(); i++)
            {
                ExtendedItemDataBeanModel model = models.get(i);
                item = model.getItem();
                // If there is a a location and if model does not already contain a valid image, get the 
                // image from the location.
                if(item.getImageLocations() != null && item.getImageLocations().size() > 0 && model.getItemImage() == null)
                {
                    itemImage = DomainGateway.getFactory().getItemImageInstance();
                    itemImage.setImageLocation(item.getImageLocations().get(0));
                    model.setItemImage(itemImage);
                    loadImage(itemImage, i);
                }
            }
        }
    }

}
