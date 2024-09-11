/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ListBean.java /main/24 2014/05/20 12:14:37 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  09/11/14 - Sorting item search result by default for a  
                           given column when it gets displayed first time.
 *    cgreene   08/13/14 - add generics
 *    cgreene   05/20/14 - refactor list model sorting
 *    abhinavs  05/11/14 - Sorting Item search results enhancement
 *    vtemker   08/01/13 - Fix ArrayIndexOutofBoundsException bug db id:
 *                         16862930
 *    cgreene   12/11/12 - allow sale renderer to show item's promotion name
 *    cgreene   11/27/12 - Refactored loadImage into superclass
 *    icole     04/17/12 - Removed forward port code and moved fix into
 *                         updateBean method.
 *    icole     04/16/12 - Forward port djindal_bug-12902389, wrong item
 *                         highlighted after add item.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   09/01/09 - XbranchMerge cgreene_3765_pt2 from
 *                         rgbustores_13.1x_branch
 *    cgreene   09/01/09 - do not set renderer onto list if it is the same
 *    mkochumm  02/17/09 - check for empty contents
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         7/9/2007 3:07:53 PM    Anda D. Cadar   I18N
 *         changes for CR 27494: POS 1st initialization when Server is offline
 *    3    360Commerce 1.2         3/31/2005 4:28:52 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:07 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse
 *
 *   Revision 1.8  2004/08/10 22:18:37  jdeleau
 *   @scr 6800 Do not allow tabbing out of Prompt&Response Bean for the
 *   return item screen.  Make sure the items in the receipt can be selected
 *   via keyboard.
 *
 *   Revision 1.7  2004/05/17 17:50:02  khassen
 *   @scr 3773 - Fixed scrollbar properties
 *
 *   Revision 1.6  2004/03/24 18:42:56  pkillick
 *   @scr 3097 -Removed "model.setListModel(new Object[0])" at line 642.
 *
 *   Revision 1.5  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.4  2004/03/09 15:03:17  jdeleau
 *   @scr 3997 Final code review comments incorporated for the multi-item
 *   select feature.
 *
 *   Revision 1.3  2004/03/05 15:49:33  jdeleau
 *   @scr 0 Multi-Item Selection added to work with touchscreen.
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Dec 01 2003 14:21:12   baa
 * address focus issues
 *
 *    Rev 1.1.1.0   Dec 01 2003 08:58:06   baa
 * address focus issues scr  3438
 *
 *    Rev 1.1   Sep 10 2003 15:15:24   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:11:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Jul 10 2003 10:39:48   baa
 * set default selected index to 0
 * Resolution for 2890: Layaway List screen - local navigation buttons disabled after selecting Help
 *
 *    Rev 1.2   Sep 05 2002 16:50:24   baa
 * I18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:17:58   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:48:34   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:35:26   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:56:02   msg
 * Initial revision.
 *
 *    Rev 1.3   Feb 23 2002 15:04:16   mpm
 * Re-started internationalization initiative.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   30 Jan 2002 16:42:48   baa
 * ui fixes
 * Resolution for POS SCR-965: Add Customer screen UI defects
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.ScrollPane;
import java.util.Properties;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.stock.ItemImageIfc;
import oracle.retail.stores.gui.utility.SwingWorker;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Presents a list of objects that can be selected.
 *
 * @version $Revision: /main/24 $
 */
public class ListBean extends CycleRootPanel
{
    private static final long serialVersionUID = -3486171856089074553L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/24 $";

    /** section mode of none */
    public static final int NO_SELECTION_MODE = -1;
    /** unit increment for scrollPane */
    public static final int UNIT_INCREMENT = 10;
    /** block increment for scrollPane */
    public static final int BLOCK_INCREMENT = 300;

    /** number of pixels the icon will take up. */
    public static int ICON_WIDTH = 50;

    /** header object */
    protected BaseHeaderBean headerBean = null;

    /** flag that indicates if the list should get focus */
    protected boolean focused = false;

    /** list to display */
    protected EYSList<?> list = null;

    /** renderer for the list */
    @SuppressWarnings("rawtypes")
    protected ListCellRenderer renderer = null;

    /** scroll pane that contains the list */
    protected JScrollPane scrollPane = null;

    /** behavior of the vertical scrollBar */
    protected int scrollPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

    /** selection mode of the list */
    protected int selectionMode = ListSelectionModel.SINGLE_SELECTION;

     /**
     *  Default Constructor.
     */
    public ListBean()
    {
        super();
    }

    /**
     * Activates this class.
     */
    @Override
    public void activate()
    {
        super.activate();

        if (scrollPane == null)
        {
            scrollPane = getScrollPane();
        }
        headerBean.activate();
        if (list != null)
        {
            list.addFocusListener(this);
        }
    }

    /**
     * Deactivates this class.
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        if (list != null)
        {
            list.removeFocusListener(this);
        }
    }

    /**
     * Configures this bean.
     */
    @Override
    public void configure()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);
        setLayout(new BorderLayout());
    }

    /**
     * Sets the focused attribute of this list. If true, the list will request
     * focus when displayed.
     *
     * @param aFlag true if focused, false otherwise
     */
    public void setFocused(String aValue)
    {
        focused = Boolean.valueOf(aValue).booleanValue();
    }

    /**
     * Sets the header bean. Constructs a header if a classname was specified.
     * Initiate the header with the list. Set the sorting properties. Puts the
     * header into the scrollpane.
     *
     * @param propValue the class name of the header bean
     */
    public void setHeaderBean(String propValue)
    {
        // construct a header if a classname was specified
        if (propValue != null)
        {
            if (headerBean == null ||
               !headerBean.getClass().getName().equals(propValue))
            {
                headerBean =
                    (BaseHeaderBean)UIUtilities.getNamedClass(propValue);
            }
        }

        // initiate the header with the list.
        if (headerBean == null)
        {
            headerBean = new BaseHeaderBean(list);
        }
        else
        {
            headerBean.setList(list);
        }

        // set the sorting properties
        if (getColumnSortProperties() != null)
        {
            headerBean.setColumnSortProperties(getColumnSortProperties());
        }

        // put the header into the scrollpane
        if (scrollPane != null)
        {
            scrollPane.setColumnHeaderView(headerBean);
        }
    }

    /**
     * Convenience method for setting alignments of the header labels.
     *
     * @param aligns the alignments for the labels
     */
    public void setLabelAlignments(String aligns)
    {
        getHeaderBean().setAlignments(aligns);
    }

    /**
     * Convenience method for setting property tags of the header labels.
     *
     * @param tags the property tags for the labels
     */
    public void setLabelTags(String tags)
    {
        getHeaderBean().setLabelTags(tags);
    }

    /**
     * Convenience method for setting text of the header labels.
     *
     * @param text the text of the labels
     */
    public void setLabelText(String text)
    {
        getHeaderBean().setLabelText(text);
    }

    /**
     * Convenience method for setting the weights of the header labels.
     *
     * @param text the weights of the labels
     */
    public void setLabelWeights(String text)
    {
        getHeaderBean().setLabelWeights(text);
    }

    /**
     * @return the columnSortProperties
     */
    public String[] getColumnSortProperties()
    {
        return getHeaderBean().getColumnSortProperties();
    }

    /**
     * Set the comma-delimited list of bean properties for which the matching
     * column can be sorted. For example, if there are three columns displayed
     * in the list, this <code>properties</code> should have three tokens
     * separated by commas. The word "null" can be used to specify no matching
     * bean-property for that column. Otherwise, the non-null token should
     * be a name that matches a bean-property for the objects in the list's
     * model.
     *
     * @param propertiesList
     */
    public void setColumnSortProperties(String propertiesList)
    {
       getHeaderBean().setColumnSortProperties(UIUtilities.parseDelimitedList(propertiesList, ","));
    }
    
    /**
     * @return defaultSortedColumn the column name sorted by default
     * @since 14.1
     */
    public String getDefaultSortedColumn()
    {
        return getHeaderBean().getDefaultSortedColumn();
    }
    
    /**
     * Set the column name to be sorted by default.
     * @param defaultSortedColumn the defaultSortedColumn to set
     * @since 14.1
     */
    public void setDefaultSortedColumn(String defaultSortedColumn)
    {
        getHeaderBean().setDefaultSortedColumn(defaultSortedColumn);
    }
    
    /**
     * Sets the list. Creates a list if a classname was specified. Sets the
     * list onto the {@link #headerBean}. Puts the list into the
     * {@link ScrollPane}. Calls {@link #configureList()}.
     *
     * @param propValue the class name of the list
     */
    @SuppressWarnings( "rawtypes" )
    public void setList(String propValue)
    {
        // Creates a list if a classname was specified.
        if (propValue != null)
        {
            if(list == null ||
               !list.getClass().getName().equals(propValue))
            {
                list =
                    (EYSList<?>)UIUtilities.getNamedClass(propValue);
            }
        }
        if (list == null)
        {
            list = new EYSList();
        }

        // Sets the list onto the headerBean.
        headerBean.setList(list);

        // Puts the list into the ScrollPane.
        if (scrollPane != null)
        {
            scrollPane.getViewport().setView(list);
        }
        configureList();
    }

    /**
     * Set the properties to be used by this bean. If the header and renderer
     * accept properties, this method will pass the properties on to those
     * objects.
     *
     * @param props the properties object
     */
    @Override
    public void setProps(Properties props)
    {
        getHeaderBean().setProps(props);

        if (renderer != null && renderer instanceof AbstractListRenderer)
        {
            ((AbstractListRenderer<?>)renderer).setProps(props);
        }
    }

    /**
     * Sets the list renderer.
     *
     * @param propValue the class name of the renderer
     */
    @SuppressWarnings("unchecked")
    public void setRenderer(String propValue)
    {
        if (propValue != null)
        {
            if(renderer == null ||
               !renderer.getClass().getName().equals(propValue))
            {
                renderer = (AbstractListRenderer<?>)UIUtilities.getNamedClass(propValue);
                if (renderer != null)
                {
                    ((AbstractListRenderer<?>)renderer).initialize();
                }
            }
        }
        if (renderer == null)
        {
            renderer = new DefaultListCellRenderer();
        }
        if (list != null && list.getCellRenderer() != renderer)
        {
            list.setCellRenderer(renderer);
        }
    }

    /**
     * Sets the scroll pane.
     *
     * @param propValue the class name of the scroll pane
     */
    public void setScrollPane(String propValue)
    {
        // if there is an existing scrollPane, remove it
        if (scrollPane != null)
        {
            remove(scrollPane);
        }
        // try to create the new scrollPane using reflection
        if (propValue != null)
        {
            scrollPane = (JScrollPane)UIUtilities.getNamedClass(propValue);
        }
        // if the creation failed, use the default scrollPane
        if (scrollPane == null)
        {
            scrollPane = new JScrollPane();
            scrollPane.setFocusTraversalKeysEnabled(false);
        }
        configureScrollPane();
    }

    /**
     * Sets the vertical scrollbar policy for the scrollPane. The default policy
     * is to always show the vertical scroll bars. Valid values are "always",
     * "needed" and "never". The default is NEVER.
     *
     * @param propValue a string representation of the scroll policy
     */
    public void setScrollPolicy(String propValue)
    {
        if (propValue.equalsIgnoreCase("always"))
        {
            scrollPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;
        }
        else if (propValue.equalsIgnoreCase("needed"))
        {
            scrollPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED;
        }
        else
        {
            scrollPolicy = JScrollPane.VERTICAL_SCROLLBAR_NEVER;
        }
    }

    /**
     * Sets the selection mode for the list. Acceptable values are 'single'
     * 'multiple' or 'none'. The default is SINGLE.
     *
     * @param propValue a string representation of the selection mode
     */
    public void setSelectionMode(String propValue)
    {
        // If its single
        if (propValue.equalsIgnoreCase("single"))
        {
            selectionMode = ListSelectionModel.SINGLE_SELECTION;
        }
        // Or multiple.. xml files generally say either multiple or multi
        else if (propValue.equalsIgnoreCase("multi") || propValue.equalsIgnoreCase("multiple"))
        {
            selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
        }
        else
        {
            selectionMode = NO_SELECTION_MODE;
        }
    }

    /**
     * Sets the visibility of this bean.
     *
     * @param aFlag true if visible, false otherwise
     */
    @Override
    public void setVisible(boolean aFlag)
    {

        super.setVisible(aFlag);

        // if visible is true, set the selection and request focus
        if (aFlag)
        {
            if (focused)
            {
                // make sure that the list can in fact get focus
                list.setFocusable(true);
                list.setEnabled(true);
                list.setRequestFocusEnabled(true);
                setCurrentFocus(list);
            }
            else
            {
                list.setFocusable(false);
            }
            list.setFocusTraversalKeysEnabled(false);
        }
    }

    /**
     * Updates the bean model before sending it to the ui.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void updateModel()
    {
        if (beanModel != null && beanModel instanceof ListBeanModel)
        {
            ListBeanModel model = (ListBeanModel)beanModel;

            if (selectionMode == NO_SELECTION_MODE)
            {
                model.setSelectedRow(-1);
                model.setSelectedValue(null);
            }
            else
            {
                int row = list.getSelectedRow();
                model.setSelectedRow(row);

                if (row != -1 && list.getModel().getSize() > 0 && row < list.getModel().getSize())
                {
                    model.setSelectedValue(list.getModel().getElementAt(row));
                }
                else
                {
                    model.setSelectedValue(null);
                }
                if (selectionMode == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
                {
                    model.setSelectedRows(list.getAllSelectedRows());
                }
            }
        }
    }

    /**
     * Configures the list. Assumes {@link #list()} is non-null.
     */
    @SuppressWarnings("unchecked")
    protected void configureList()
    {
        uiFactory.configureUIComponent(list, "List");

        if (renderer != null)
        {
            list.setCellRenderer(renderer);
        }
        if (selectionMode == NO_SELECTION_MODE)
        {
            list.setEnabled(false);
        }
        else
        {
            list.setSelectionMode(selectionMode);
            list.setEnabled(true);
        }

    }

    /**
     * Configures the scrollPane.
     */
    protected void configureScrollPane()
    {
        uiFactory.configureUIComponent(scrollPane, "ScrollPane");

        scrollPane.setVerticalScrollBar(new EYSScrollBar());
        scrollPane.getVerticalScrollBar().setUnitIncrement(UNIT_INCREMENT);
        scrollPane.getVerticalScrollBar().setBlockIncrement(BLOCK_INCREMENT);
        scrollPane.getVerticalScrollBar().setFocusable(false);
        scrollPane.setVerticalScrollBarPolicy(scrollPolicy);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setColumnHeaderView(getHeaderBean());
        scrollPane.getColumnHeader().setOpaque(false);

        scrollPane.getViewport().setView(getList());
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Gets the header bean.
     *
     * @return a header bean
     */
    protected BaseHeaderBean getHeaderBean()
    {
        // lazy instantiation
        if (headerBean == null)
        {
            setHeaderBean(null);
        }
        return headerBean;
    }

    /**
     * Gets the list that will be displayed. Subclasses should override for
     * different list objects.
     *
     * @return a JList object
     */
    protected EYSList<?> getList()
    {
        if (list == null)
        {
            setList(null);
        }
        return list;
    }

    /**
     * Gets the renderer for this list.
     *
     * @return a renderer object
     */
    protected ListCellRenderer<?> getRenderer()
    {
        // lazy instantiation
        if(renderer == null)
        {
            setRenderer(null);
        }
        return renderer;
    }

    /**
     * Gets the scroll pane that wraps this list.
     *
     * @return a renderer object
     */
    protected JScrollPane getScrollPane()
    {
        // lazy instantiation
        if(scrollPane == null)
        {
            setScrollPane(null);
        }
        return scrollPane;
    }

    /**
     * Updates the bean with new data from the model.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void updateBean()
    {
        if (beanModel != null && beanModel instanceof ListBeanModel)
        {
            ListBeanModel model = (ListBeanModel)beanModel;
            POSListModel listModel = model.getListModel();

            if (listModel != null)
            {
                getList().setModel(listModel);

                if (listModel.size() > 0)
                {
                    int[] rows = model.getSelectedRows();
                    if (rows.length > 0)
                    {
                        list.setSelectedIndices(rows);
                    }
                    else
                    {
                        int row = model.getSelectedRow();
                        if (row < 0 || row > listModel.size())
                        {
                            row = 0;
                        }
                        list.setSelectedIndex(row);
                    }
                }
            }
        }
    }

    /**
     * Adds a list selection listener to the internal list.
     *
     * @param l the listener
     */
    public void addListSelectionListener(ListSelectionListener l)
    {
        list.addListSelectionListener(l);
    }

    /**
     * Removes a list selection listener to the internal list.
     *
     * @param l the listener
     */
    public void removeListSelectionListener(ListSelectionListener l)
    {
        list.removeListSelectionListener(l);
    }

    /**
     * Load the specified item image into the model by using a {@link SwingWorker}
     *
     * @param itemImage
     */
    protected void loadImage(final ItemImageIfc itemImage, final int row)
    {
        if (itemImage == null)
        {
            logger.warn("Unable to load null item image.");
            return;
        }
        if (itemImage.getImage() != null)
        {
            logger.debug("ImageIcon is already loaded for " + itemImage);
            return;
        }
        itemImage.setLoadingImage(true);
        // constructing the worker starts it
        new SwingWorker(Integer.toString(row))
        {
            @Override
            public Object construct()
            {
                try
                {
                    itemImage.scaleImage(ICON_WIDTH, ICON_WIDTH);
                }
                finally
                {
                    // since we have the image now
                    itemImage.setLoadingImage(false);
                    repaint();//getList().getCellBounds(row, row));
                }
                return itemImage;
            }
        };
    }

    /* (non-Javadoc)
     * @see java.awt.Component#toString()
     */
    @Override
    public String toString()
    {
        return new String("Class: " + Util.getSimpleClassName(this.getClass()) +
                          "(Revision " + getRevisionNumber() +
                          ") @" + hashCode());
    }

    /**
     * Returns the revision number of the class.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Main entry point for testing.
     *
     * @param args command line parameters
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        ListBean bean = new ListBean();
        bean.configure();
        bean.setLabelText("Column 1,Column 2,Column 3,Column 4");
        bean.setLabelWeights("30,20,30,20");
        bean.setScrollPane(null);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
