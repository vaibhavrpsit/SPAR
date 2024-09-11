/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerGiftListsBean.java /main/1 2014/06/03 17:06:10 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/16/14 - Prevent NPE and fix color of tabs.
 *    jswan     11/07/14 - Improved performance of retrieving item images.
 *    yiqzhao   10/30/14 - Make sub tabs scrollable.
 *    asinton   09/09/14 - Added empty implementation for ChangeListener
 *    asinton   06/02/14 - Added support for extended customer data.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.beans;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;

import oracle.retail.stores.common.customer.CustomerGiftList;
import oracle.retail.stores.common.item.ExtendedItemData;
import oracle.retail.stores.foundation.manager.gui.ButtonSpec;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * UI Bean for showing Customer gift list of gift lists using a {@link TabbedUIBean}.
 * @since 14.1
 *
 */
public class CustomerGiftListsBean extends TabbedUIBean
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6590630586073421560L;

    /** List of customer gift lists. */
    protected List<CustomerGiftList> customerGiftLists = null;
    
    protected List<ExtendedItemDataListBean> giftListBeans = null;

    /** Default gift lists list bean class */
    private String giftListsListBeanClass = "oracle.retail.stores.pos.ui.beans.ExtendedItemDataListBean"; 

    /** label tags for list headers */
    protected String labelTags = "ItemLabel,DepartmentLabel,PriceLabel";

    /** label weights for list headers */
    protected String labelWeights = "30,40,30";

    /**
     * No argument constructor.
     */
    public CustomerGiftListsBean()
    {
        super();
        super.setShowTabbedPane(true);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.TabbedUIBean#getComponentMap()
     */
    @Override
    protected Map<String, JComponent> getComponentMap()
    {
        giftListBeans = new ArrayList<ExtendedItemDataListBean>();
        Map<String, JComponent> componentMap = null;
        if(customerGiftLists != null && customerGiftLists.size() > 0)
        {
            componentMap = new HashMap<String, JComponent>(customerGiftLists.size());
            uiFactory.configureUIComponent(this, TABBED_UI_PREFIX);
            ExtendedItemDataListBean giftItemsBean = null;
            for(CustomerGiftList list : customerGiftLists)
            {
                if (list != null && list.getGiftItems().size() > 0)
                {
                    giftItemsBean = createExtendedItemsBean(list.getGiftItems(), getGiftListsListBeanClass());
                    giftListBeans.add(giftItemsBean);
                    componentMap.put(list.getListName(), giftItemsBean);
                }
            }
        }
        return componentMap;
    }
    
    /*
     * This method will initialize the tabs in this UI and make sub tabs scroll when there are too many.
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.initTabs()
     */
    @Override
    protected void initTabs()
    {
        super.initTabs();
        if (tabbedPane!=null)
        {
            tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            uiFactory.configureUIComponent(tabbedPane, TABBED_UI_PREFIX);
        }
    }

    /*
     * In the case of customer gift lists, we want the same behavior for one tab as we have for many tabs.
     * It makes sub tabs scroll when there are too many.
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.TabbedUIBean#initTabsWhenExactlyOne(java.util.Map)
     */
    @Override
    protected void initTabsWhenExactlyOne(Map<String, JComponent> componentMap)
    {
        super.initTabsWhenGreaterThanOne(componentMap);
        if (tabbedPane!=null)
        {
            tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        }
    }

	/**
     * Creates an instance of the ExtendedItemsDataListBean initialized with the given <code>items</code> to show on a tab.
     *  
     * @param items The list of <code>ExtendedItemData</code> to show
     * @return the new instance of the <code>ExtendedItemDataListBean</code>
     */
    protected ExtendedItemDataListBean createExtendedItemsBean(List<ExtendedItemData> items, String beanClass)
    {
        ExtendedItemDataListBeanModel itemsModel = configureExtendedItemDataListBeanModel(items);
        ExtendedItemDataListBean itemsBean = (ExtendedItemDataListBean)UIUtilities.getNamedClass(beanClass);
        configureExtendedItemsDataListBean(itemsModel, itemsBean);
        return itemsBean;
    }

    /**
     * Builds an ExtendedItemDataListBeanModel from a list of ExtendedItemData objects 
     * @param items a list of ExtendedItemData objects
     * @return ExtendedItemDataListBeanModel
     */
    protected ExtendedItemDataListBeanModel configureExtendedItemDataListBeanModel(List<ExtendedItemData> items)
    {
        ExtendedItemDataListBeanModel listModel = new ExtendedItemDataListBeanModel();
        ExtendedItemDataBeanModel itemModel;
        List<ExtendedItemDataBeanModel> itemModels = new ArrayList<ExtendedItemDataBeanModel>();
        for(ExtendedItemData itemData: items)
        {
            itemModel = new ExtendedItemDataBeanModel();
            itemModel.setItem(itemData);
            itemModels.add(itemModel);
        }
        listModel.setItemModels(itemModels);
        return listModel;
    }

    /**
     * Configures the given <code>ExtendedItemDataListBean</code> instance.
     * @param itemsModel The <code>ExtendedItemDataListBeanModel</code> instance
     * @param itemsBean The <code>ExtendedItemDataListBean</code> instance
     */
    protected void configureExtendedItemsDataListBean(ExtendedItemDataListBeanModel itemsModel, ExtendedItemDataListBean itemsBean)
    {
        itemsBean.setHeaderBean(null);
        itemsBean.setList(null);
        itemsBean.setHeaderBean(null);
        itemsBean.getHeaderBean().setProps(props);
        itemsBean.getHeaderBean().setLabelTags(labelTags);
        itemsBean.getHeaderBean().setLabelWeights(labelWeights);
        itemsBean.setRenderer(ExtendedItemDataListRenderer.class.getName());
        itemsBean.setModel(itemsModel);
        itemsBean.updateBean();
        itemsBean.activate();
        itemsBean.configureScrollPane();
    }

    /**
     * Returns the <code>customerGiftLists</code> value.
     * @return the customerGiftLists
     */
    public List<CustomerGiftList> getCustomerGiftLists()
    {
        return customerGiftLists;
    }

    /**
     * Sets the <code>customerGiftLists</code> value.
     * @param customerGiftLists the customerGiftLists to set
     */
    public void setCustomerGiftLists(List<CustomerGiftList> customerGiftLists)
    {
        this.customerGiftLists = customerGiftLists;
    }

    /**
     * Returns the <code>giftListsListBeanClass</code> value.
     * @return the giftListsListBeanClass
     */
    public String getGiftListsListBeanClass()
    {
        return giftListsListBeanClass;
    }

    /**
     * Sets the <code>giftListsListBeanClass</code> value.
     * @param giftListsListBeanClass the giftListsListBeanClass to set
     */
    public void setGiftListsListBeanClass(String giftListsListBeanClass)
    {
        this.giftListsListBeanClass = giftListsListBeanClass;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.gui.ButtonBarBeanIfc#configureButtons(oracle.retail.stores.foundation.manager.gui.ButtonSpec[])
     */
    @Override
    public void configureButtons(ButtonSpec[] buttons)
    {
        // empty implementation
        
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.TabbedUIBean#getTabNames()
     */
    @Override
    protected List<String> getTabNames()
    {
        List<String> names = null;
        if(customerGiftLists != null)
        {
            names = new ArrayList<String>(customerGiftLists.size());
            String name = null;
            for(CustomerGiftList list : customerGiftLists)
            {
                name = list.getListName();
                names.add(name);
            }
        }
        else
        {
            // if no customerGiftLists, return an empty list
            names = new ArrayList<String>(0);
        }
        return names;
    }

    /**
     * Label tags set in the UICFG.XML for the list header.
     * @param labelTags the label tags to be used for the list header
     * @since 14.1
     */
    public void setLabelTags(String labelTags)
    {
        this.labelTags = labelTags;
    }

    /**
     * Label weights set in the UICFG.XML for the list header.
     * @param labelWeights the label weights for the list header
     * @since 14.1
     */
    public void setLabelWeights(String labelWeights)
    {
        this.labelWeights = labelWeights;
    }

    /*
     * This method is called by the tabbedPane when any tab is selected.
     * (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     * @since 14.1
     */
    @Override
    public void stateChanged(ChangeEvent e)
    {
        // Check to see if the tab contains this bean
        JTabbedPane tabbedPane = (JTabbedPane)e.getSource();
        Component selected = tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
        if (selected == this)
        {
            // Get the image for each item in the gift list.
            for(ExtendedItemDataListBean giftListBean: giftListBeans)
            {
                giftListBean.loadImages();
            }
        }
    }
}
