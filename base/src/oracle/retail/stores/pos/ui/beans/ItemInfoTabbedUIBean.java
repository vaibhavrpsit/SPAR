/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemInfoTabbedUIBean.java /main/2 2014/07/10 14:02:15 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/07/14 - Prevent NPE.
 *    jswan     11/07/14 - Improve performance of retrieving item images.
 *    asinton   09/26/14 - Added condition to show tabs in override of
 *                         isShowTabbedPane() method.
 *    asinton   09/09/14 - Added code to preserve selected tab when user
 *                         temporarily views another screen (ie. F1 help)
 *    yiqzhao   08/19/14 - Read text for the subcomponent from bundles. 
 *    jswan     07/10/14 - Modified to make sure that the top of the detailed
 *                         description is positioned at the top of the text
 *                         area.
 *    jswan     06/10/14 - Added to support Item Info Tabbed UI for ICE.
 *    asinton   06/04/14 - made fixes to address disabled tabs and when tabbed
 *                         is is not shown.
 *    asinton   06/02/14 - Added support for extended customer data.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import oracle.retail.stores.common.item.ExtendedItemData;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.manager.gui.ButtonSpec;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * A panel displays the tabbed item info view.
 * 
 * @since 14.1
 *
 */
public class ItemInfoTabbedUIBean extends TabbedUIBean
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3475073796468035838L;

    /** Constant for Summary tab index. */
    public static final int SUMMARY_TAB_INDEX = 0;

    /** Constant for Recent tab index. */
    public static final int DETAIL_TAB_INDEX = 1;

    /** Constant for Recommended tab index. */
    public static final int RECOMMENDED_TAB_INDEX = 2;

    /** 
     * This is the bean that displays the summary item information. 
     */
    protected ItemInfoBean itemInfoBean = null;

    /**
     * Mapping for tab names to component used by super class, {@link TabbedUIBean}.
     */
    protected Map<String, JComponent> componentMap;

    /** 
     * Recommended items list bean 
     */
    protected ExtendedItemDataListBean recommededItemsBean = null;

    /** 
     * Item detail panel 
     */
    protected JTextArea   detailText = null;
    protected JScrollPane detailPane = null;

    /** 
     * Comma delimited list of the header tags for the list of recommended items. 
     */
    protected String recommendedLabelTags = null;

    /** 
     * Comma delimited list of the header weights for the list of recommended items. 
     */
    protected String recommendedLabelWeights = null;

    /** 
     * Tabbed Button specs 
     */
    protected ButtonSpec[] tabbedButtonSpecs;

    /**
     * Flag to indicate if environment is set to retrieve extended item data.
     */
    protected boolean isRetrieveExtendedData = Gateway.getBooleanProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "RetrieveExtendedData", false);

    /**
     * Default constructor.
     */
    public ItemInfoTabbedUIBean()
    {
        setName("ItemInfoTabbedUIBean");
        initialize();
    }
    
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.TabbedUIBean#initialize()
     */
    @Override
    public void initialize()
    {
        super.initialize();
        componentMap = new HashMap<String, JComponent>(3);
        itemInfoBean = new ItemInfoBean();
        detailText = new JTextArea(10, 50);
        detailText.setLineWrap(true);
        detailText.setEditable(false);
        detailText.setWrapStyleWord(true);
        detailPane = new JScrollPane(detailText);
    }
    
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#configure()
     */
    @Override
    public void configure()
    {
        itemInfoBean.configure();
    }

    /**
     * Sets the information to be shown by this bean and the beans
     * in each of the tabbed beans.
     * @param model the model to be shown. The runtime type should be
     *            ItemInfoBeanModel
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set ItemInfoBean " + "model to null");
        }
        if (model instanceof ItemInfoBeanModel)
        {
            beanModel = (ItemInfoBeanModel)model;
            updateBean();
            
            // Set and update the model on the item info bean.
            itemInfoBean.setModel(model);
        }
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#updateBean()
     */
    @Override
    protected void updateBean()
    {
        if(beanModel instanceof ItemInfoBeanModel)
        {
            ItemInfoBeanModel itemInfoBeanModel = (ItemInfoBeanModel)beanModel;
            // Set the show tabbed pane boolean on the super class.  it
            // indicates if the super class should show a single panel or
            // the tabbe pane.
            setShowTabbedPane(itemInfoBeanModel.isShowExtendedDataUI());
            
            // Build component map specific to this bean, and reinitialize it.
            buildComponentMap();
            reinitialize();
            // set the selected index
            if(tabbedPane != null && itemInfoBeanModel instanceof ItemInfoBeanModel)
            {
                int selectedIndex = ((ItemInfoBeanModel)itemInfoBeanModel).getSelectedTabIndex();
                if(selectedIndex >= 0 && selectedIndex < tabbedPane.getTabCount())
                {
                    tabbedPane.setSelectedIndex(selectedIndex);
                }
                tabbedPane.addChangeListener(this);
                if (itemInfoBeanModel.isShowExtendedDataUI() && recommededItemsBean != null)
                {
                    tabbedPane.addChangeListener(recommededItemsBean);
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.TabbedUIBean#reinitialize()
     */
    @Override
    protected void reinitialize()
    {
        super.reinitialize();
        if(tabbedPane != null && beanModel instanceof ItemInfoBeanModel)
        {
            ItemInfoBeanModel itemInfoBeanModel = (ItemInfoBeanModel)beanModel;
            // set the tabs enabled if there is data
            if(tabbedPane.getTabCount() > DETAIL_TAB_INDEX)
            {
                boolean enabled = !Util.isEmpty(itemInfoBeanModel.getDetail());
                tabbedPane.setEnabledAt(DETAIL_TAB_INDEX, enabled);
            }
            if(tabbedPane.getTabCount() > RECOMMENDED_TAB_INDEX)
            {
                boolean enabled = itemInfoBeanModel.getRecommendedItems() != null && itemInfoBeanModel.getRecommendedItems().size() > 0;
                tabbedPane.setEnabledAt(RECOMMENDED_TAB_INDEX, enabled);
            }
        }
    }

    /**
     * Builds the component map for the Tabbed UI.
     */
    protected void buildComponentMap()
    {
        if(componentMap == null)
        {
            componentMap = new HashMap<String, JComponent>();
        }
        componentMap.clear();
        if(tabbedPane != null)
        {
            tabbedPane.removeChangeListener(this);
            tabbedPane.removeChangeListener(recommededItemsBean);
            tabbedPane.removeAll();
        }
        if(tabbedButtonSpecs != null && tabbedButtonSpecs.length > 0 && beanModel instanceof ItemInfoBeanModel)
        {
            ItemInfoBeanModel itemInfoBeanModel = (ItemInfoBeanModel)beanModel;
            // Set up the summary screen
            JScrollPane sp = new JScrollPane(itemInfoBean);
            sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            componentMap.put(tabbedButtonSpecs[SUMMARY_TAB_INDEX].getLabelTag(), sp);
            if (beanModel != null && isShowTabbedPane())
            {
                // set up detail
                if (itemInfoBeanModel.getDetail() != null)
                {
                    detailText.setText(itemInfoBeanModel.getDetail());
                    detailText.setCaretPosition(0);
                }
                else
                {
                    detailText.setText("");
                }
                componentMap.put(tabbedButtonSpecs[DETAIL_TAB_INDEX].getLabelTag(), detailPane);
    
                // set up recommended items
                ExtendedItemDataListBean recommededItems = getRecommededItemsBean();
                recommededItems.getHeaderBean().setLabelTags(recommendedLabelTags);
                recommededItems.getHeaderBean().setLabelWeights(recommendedLabelWeights);
                recommededItems.getHeaderBean().setProps(props);
                ExtendedItemDataListBeanModel recommededModel = new ExtendedItemDataListBeanModel();
                ExtendedItemDataBeanModel dataModel;
                List<ExtendedItemDataBeanModel> dataModels = new ArrayList<ExtendedItemDataBeanModel>();
                if (itemInfoBeanModel.getRecommendedItems() != null)
                {
                    for(ExtendedItemData data: itemInfoBeanModel.getRecommendedItems())
                    {
                        dataModel = new ExtendedItemDataBeanModel();
                        dataModel.setItem(data);
                        dataModels.add(dataModel);
                    }
                }
                recommededModel.setItemModels(dataModels);
                configureExtendedItemsDataListBean(recommededModel, recommededItems);
                componentMap.put(tabbedButtonSpecs[RECOMMENDED_TAB_INDEX].getLabelTag(), recommededItems);
                //recommededItems.setTabIndex(RECOMMENDED_TAB_INDEX);
            }
        }
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
        itemsBean.setRenderer(ExtendedItemDataListRenderer.class.getName());
        itemsBean.setModel(itemsModel);
        itemsBean.updateBean();
        itemsBean.activate();
        itemsBean.configureScrollPane();
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.TabbedUIBean#getTabNames()
     */
    @Override
    protected List<String> getTabNames()
    {
        List<String> names = null;
        if(tabbedButtonSpecs != null)
        {
            names = new ArrayList<String>(tabbedButtonSpecs.length);
            String name = null;
            for(ButtonSpec buttonSpec : tabbedButtonSpecs)
            {
                name = buttonSpec.getLabelTag();
                names.add(name);
            }
        }
        else
        {
            // if no configured button spec, return an empty list
            names = new ArrayList<String>(0);
        }
        return names;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.gui.ButtonBarBeanIfc#configureButtons(oracle.retail.stores.foundation.manager.gui.ButtonSpec[])
     */
    @Override
    public void configureButtons(ButtonSpec[] buttons)
    {
        this.tabbedButtonSpecs = buttons;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.TabbedUIBean#getComponentMap()
     */
    @Override
    protected Map<String, JComponent> getComponentMap()
    {
        return this.componentMap;
    }

    /**
     * Returns the <code>recommededItemsBean</code> value.
     * @return the recommededItemsBean
     */
    public ExtendedItemDataListBean getRecommededItemsBean()
    {
        if(this.recommededItemsBean == null)
        {
            // if null, initialize with default
            this.setRecommendedItemsBean(null);
        }
        return recommededItemsBean;
    }

    /**
     * Sets the <code>recommededItemsBean</code> value.
     * @param recommededItemsBean the recommededItemsBean to set
     */
    public void setRecommededItemsBean(ExtendedItemDataListBean recommededItemsBean)
    {
        this.recommededItemsBean = recommededItemsBean;
    }

    /**
     * Sets the recommended items bean via the beanClass specified from the *uicfg.xml file.
     * @param beanClass The bean class to use for the <code>recommededItemsBean</code> attribute.  Must be an extension of {@link ListBean}.
     */
    public void setRecommendedItemsBean(String propValue)
    {
        if (propValue != null)
        {
            if(recommededItemsBean == null ||
               !recommededItemsBean.getClass().getName().equals(propValue))
            {
                recommededItemsBean = (ExtendedItemDataListBean)UIUtilities.getNamedClass(propValue);
                recommededItemsBean.setHeaderBean(null);
                recommededItemsBean.setList(null);
                recommededItemsBean.setRenderer(ExtendedItemDataListRenderer.class.getName());
                recommededItemsBean.activate();
                recommededItemsBean.configureScrollPane();
            }
        }
        if (recommededItemsBean == null)
        {
            recommededItemsBean = new ExtendedItemDataListBean();
        }
    }

    /**
     * Sets the recommended label text via the beanClass specified from the *uicfg.xml file.
     * @param recommendedLabelWeights
     */
    public void setRecommendedLabelTags(String recommendedLabelTags)
    {
        this.recommendedLabelTags = recommendedLabelTags;
    }

    /**
     * Sets the recommended label weights via the beanClass specified from the *uicfg.xml file.
     * @param recommendedLabelWeights
     */
    public void setRecommendedLabelWeights(String recommendedLabelWeights)
    {
        this.recommendedLabelWeights = recommendedLabelWeights;
    }
    
    /**
     * Set the properties for itemInfoBean which is a subcomponent of ItemInfoTabbledUIBean. 
     * There are three components which reside ItemInfoTabbledUIBean directly or indirectly.
     * itemInfoBean, recommededItemsBean and a detail pane. 
     * The content of recommededItemsBean is from the database.
     * The content of the detail panel is captured from ItemService.
     * 
     * @param props the properties object
     */
    @Override
    public void setProps(Properties props)
    {
        super.setProps(props);
        itemInfoBean.setProps(props);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.TabbedUIBean#isShowTabbedPane()
     */
    @Override
    public boolean isShowTabbedPane()
    {
        // do not display tabbed pane if not in RetrieveExtendedData environment
        return super.isShowTabbedPane() && isRetrieveExtendedData;
    }

}
