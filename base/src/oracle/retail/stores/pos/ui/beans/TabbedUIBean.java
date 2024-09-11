/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TabbedUIBean.java /main/3 2014/06/13 14:55:44 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   10/30/14 - change access modifier to protected for initTabs().
 *    asinton   09/09/14 - this class now implements ChangeListener in order
 *                         to store the selected tab index.  Subclasses use
 *                         this value to preserve the selected tab when
 *                         navigating back from say a cancel dialog
 *    asinton   06/13/14 - Added configurability to the padding and size of
 *                         tabs in the TabbedUIBean
 *    asinton   06/11/14 - added code to make disabled tabs look disabled.
 *    asinton   06/04/14 - made fixes to address disabled tabs and when tabbed
 *                         is is not shown.
 *    asinton   06/02/14 - Added support for extended customer data.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.manager.gui.ButtonBarBeanIfc;

/**
 * A panel for display on a tabbed UI.
 * 
 * @since 14.1
 *
 */
public abstract class TabbedUIBean extends ValidatingBean implements ButtonBarBeanIfc, ChangeListener
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7033605356104007488L;

    /**
     * Value to indicate orientation of tabs.  Specified tab placement of either: JTabbedPane.TOP,
     * JTabbedPane.BOTTOM, JTabbedPane.LEFT, or JTabbedPane.RIGHT. The default is JTabbedPane.TOP.
     */
    private int orientation = JTabbedPane.TOP;

    /** Inner tabbed pane */
    protected JTabbedPane tabbedPane;

    /** flag to indicate that the Tabbed UI should be used */
    private boolean showTabbedPane = true;

    /** Tabs left margin - default is 15 */
    private String leftMargin = "15";

    /** Tabls top margin - default is 8 */
    private String topMargin = "8";

    /** Tabs margin width - default is 15 */
    private String marginWidth = "15";

    /** Tabs margin height - default is 5 */
    private String marginHeight = "5";

    /**
     * Method to initialize this bean.
     */
    protected void initialize()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);
        initTabs();
    }

    /**
     * Called by the subclass to reinitialize this tabbed UI bean. 
     */
    protected void reinitialize()
    {
        initTabs();
    }

    /**
     * This method will initialize the tabs in this UI, it should be called by the subclass.
     */
    protected void initTabs()
    {
        setLayout(new BorderLayout());
        Map<String, JComponent> componentMap = getComponentMap();
        if(componentMap != null && componentMap.size() > 0)
        {
            if(componentMap.size() > 1 && isShowTabbedPane())
            {
                initTabsWhenGreaterThanOne(componentMap);
            }
            else
            {
                initTabsWhenExactlyOne(componentMap);
            }
        }
    }

    /**
     * Initializes tabs when there is exactly one tab to show.
     * @param componentMap the component map
     */
    protected void initTabsWhenExactlyOne(Map<String, JComponent> componentMap)
    {
        // this method does not use the tabbed pane.  Instead it adds the single
        // component to *this* tabbed UI bean.
        removeAll();
        String key = componentMap.keySet().toArray(new String[1])[0];
        JComponent component = componentMap.get(key);
        uiFactory.configureUIComponent(component, UI_PREFIX);
        add(component, BorderLayout.CENTER);
    }

    /**
     * Initializes tabs when there is more than one tab for this bean.
     * @param componentMap the component map
     */
    protected void initTabsWhenGreaterThanOne(Map<String, JComponent> componentMap)
    {
        // if tabbedPane is null, then create one
        if(tabbedPane == null)
        {
            tabbedPane = createTabbedPane();
        }
        // clear previous components from tabbedPane
        tabbedPane.removeAll();
        JComponent component = null;
        List<String> names = getTabNames();
        if(names != null)
        {
            // iterate over list of tab names to retrieve the associated component.
            String name = null;
            for(int i = 0; i < names.size(); i++)
            {
                name = names.get(i);
                component = componentMap.get(name);
                if(component != null)
                {
                    // update the component to conform to UI look and feel
                    uiFactory.configureUIComponent(component, UI_PREFIX);
                    // add the component to the tabbed pane
                    tabbedPane.insertTab(retrieveText(name, name), null, component, null, i);
                }
            }
        }
        // add the tabbed pane to the center of this TabbedUIBean
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Creates an instance of the tabbed pane.
     * @return newly created instance of {@link JTabbedPane}.
     */
    @SuppressWarnings("serial")
    protected JTabbedPane createTabbedPane()
    {
        String top = UIManager.getString(TABBED_UI_PREFIX + ".tab.topmargin");
        String left = UIManager.getString(TABBED_UI_PREFIX + ".tab.leftmargin");
        String width = UIManager.getString(TABBED_UI_PREFIX + ".tab.marginwidth");
        String height = UIManager.getString(TABBED_UI_PREFIX + ".tab.marginheight");
        if(!Util.isEmpty(top))
        {
            setTopMargin(top);
        }
        if(!Util.isEmpty(left))
        {
            setLeftMargin(left);
        }
        if(!Util.isEmpty(width))
        {
            setMarginWidth(width);
        }
        if(!Util.isEmpty(height))
        {
            setMarginHeight(height);
        }
        JTabbedPane tp = new JTabbedPane(orientation)
        {
            /**
             * Overriding setEnabledAt in order to change the tab color for disabled
             * tabs.
             * <p/>
             * @param index index of the tab to be enabled or disabled
             * @param enabled flag to indicate that the tab should be enabled or disabled
             */
            @Override
            public void setEnabledAt(int index, boolean enabled)
            {
                // over
                if(enabled)
                {
                    setBackgroundAt(index, getBackground());
                }
                else
                {
                    setBackgroundAt(index, getBackground().darker());
                }
                super.setEnabledAt(index, enabled);
            }

            /**
             * Overriding in order to format from the swan plaf.
             * <p/>
             * @param title the title for the inserted tab
             * @param icon the icon for the inserted tab
             * @param component the component for the inserted tab
             * @param tip the tool tip for the inserted tab
             * @param index the index of the inserted tab
             */
            @Override
            public void insertTab(String title, Icon icon, Component component, String tip, int index)
            {
                StringBuilder sb = new StringBuilder("<html><body leftmargin=");
                sb.append(getLeftMargin());
                sb.append(" topmargin=");
                sb.append(getTopMargin());
                sb.append(" marginwidth=");
                sb.append(getMarginWidth());
                sb.append(" marginheight=");
                sb.append(getMarginHeight());
                sb.append(">");
                sb.append(title);
                sb.append("</body></html>");
                super.insertTab(sb.toString(), icon, component, tip, index);
            }
        };
        tp.setBorder(null);
        uiFactory.configureUIComponent(tp, TABBED_UI_PREFIX);
        return tp;
    }

    /**
     * Overrides the {@link oracle.retail.stores.foundation.manager.gui.ButtonBarBeanIfc#setOrientation(int)}
     * specification.  This method sets the orientation value and will take the following valid values:
     * JTabbedPane.TOP, JTabbedPane.BOTTOM, JTabbedPane.RIGHT, and JTabbedPane.LEFT.
     * <br/>  
     * (non-Javadoc)
     * @param orientation The desired orientation as an int described above
     * 
     * @see oracle.retail.stores.foundation.manager.gui.ButtonBarBeanIfc#setOrientation(int)
     */
    @Override
    public void setOrientation(int orientation)
    {
        this.orientation = orientation;
    }

    /**
     * This method allows for the setting of the tab position from within the <code>*.uicfg.xml</code> configuration.
     * Set by using bean configuration in the <code>*uicfg.xml</code> as in the following example: <br/>
     * <pre>
     * &lt;BEANPROPERTY
     *      propName="orientation"
     *      propValue="RIGHT"/&gt;
     * </pre>
     * <br/>
     * Valid values are TOP, RIGHT, BOTTOM, LEFT.<br/>
     * @param orientation The desired orientation as a string described above
     */
    public void setOrientation(String orientation)
    {
        if("BOTTOM".equalsIgnoreCase(orientation))
        {
            this.orientation = JTabbedPane.BOTTOM;
        }
        else if("RIGHT".equalsIgnoreCase(orientation))
        {
            this.orientation = JTabbedPane.RIGHT;
        }
        else if("LEFT".equalsIgnoreCase(orientation))
        {
            this.orientation = JTabbedPane.LEFT;
        }
        else
        {
            this.orientation = JTabbedPane.TOP;
        }
    }

   /**
    * Overrides the {@link oracle.retail.stores.foundation.manager.gui.ButtonBarBeanIfc#getOrientation()}
    * specification.  The return values are one of: JTabbedPane.TOP, JTabbedPane.BOTTOM, JTabbedPane.RIGHT,
    * and JTabbedPane.LEFT.
    * <br/>  
    * @return int The desired orientation as an int described above
    * 
    * @see oracle.retail.stores.foundation.manager.gui.ButtonBarBeanIfc#getOrientation()
    */
    @Override
    public int getOrientation()
    {
        return this.orientation;
    }

    /**
     * Abstract method to return a map of tab names to the assigned component.
     * @return a map of tab names to the assigned component
     */
    protected abstract Map<String, JComponent> getComponentMap();

    /**
     * Abstract method to return a list of tab names in the order as the tabs should appear. 
     * @return ordered list of tab names
     */
    protected abstract List<String> getTabNames();

    /**
     * Returns the <code>showTabbedPane</code> value.
     * @return the showTabbedPane
     */
    public boolean isShowTabbedPane()
    {
        return showTabbedPane;
    }

    /**
     * Sets the <code>showTabbedPane</code> value.
     * @param showTabbedPane the showTabbedPane to set
     */
    public void setShowTabbedPane(boolean showTabbedPane)
    {
        this.showTabbedPane = showTabbedPane;
    }

    /**
     * Returns the <code>leftMargin</code> value.
     * @return the leftMargin
     */
    public String getLeftMargin()
    {
        return leftMargin;
    }

    /**
     * Sets the <code>leftMargin</code> value.
     * @param leftMargin the leftMargin to set
     */
    public void setLeftMargin(String leftMargin)
    {
        this.leftMargin = leftMargin;
    }

    /**
     * Returns the <code>topMargin</code> value.
     * @return the topMargin
     */
    public String getTopMargin()
    {
        return topMargin;
    }

    /**
     * Sets the <code>topMargin</code> value.
     * @param topMargin the topMargin to set
     */
    public void setTopMargin(String topMargin)
    {
        this.topMargin = topMargin;
    }

    /**
     * Returns the <code>marginWidth</code> value.
     * @return the marginWidth
     */
    public String getMarginWidth()
    {
        return marginWidth;
    }

    /**
     * Sets the <code>marginWidth</code> value.
     * @param marginWidth the marginWidth to set
     */
    public void setMarginWidth(String marginWidth)
    {
        this.marginWidth = marginWidth;
    }

    /**
     * Returns the <code>marginHeight</code> value.
     * @return the marginHeight
     */
    public String getMarginHeight()
    {
        return marginHeight;
    }

    /**
     * Sets the <code>marginHeight</code> value.
     * @param marginHeight the marginHeight to set
     */
    public void setMarginHeight(String marginHeight)
    {
        this.marginHeight = marginHeight;
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    @Override
    public void stateChanged(ChangeEvent changeEvent)
    {
        if(tabbedPane != null && beanModel != null)
        {
            beanModel.setSelectedTabIndex(tabbedPane.getSelectedIndex());
        }
    }

}
