/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DualListBean.java /main/18 2013/08/12 09:01:28 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   08/01/13 - Set default selected index to 0
 *    cgreene   05/01/13 - Add some more space to top list in case the header
 *                         font is increased
 *    acadar    05/23/12 - Refactor CustomerManager
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   09/01/09 - XbranchMerge cgreene_3765_pt2 from
 *                         rgbustores_13.1x_branch
 *    cgreene   09/01/09 - do not set cell renderer if its already set
 *
 * ===========================================================================
 * $Log:
 3    360Commerce 1.2         3/31/2005 4:27:52 PM   Robert Pearse   
 2    360Commerce 1.1         3/10/2005 10:21:11 AM  Robert Pearse   
 1    360Commerce 1.0         2/11/2005 12:10:43 PM  Robert Pearse   
 *
Revision 1.7  2004/07/08 18:15:56  cdb
@scr 6038 Updated so that inability to find PLUItem associated with a
given sale return line item will throw data not found data exception. Cleaned
up some associated errors.
 *
Revision 1.6  2004/07/05 19:15:00  jeffp
@scr 5703 Employee Select Add screen should not be selectable
 *
Revision 1.5  2004/05/28 17:15:12  khassen
@scr 5275 Commented off the offending line of code that was removing the list entries.
 *
Revision 1.4  2004/03/16 17:15:22  build
Forcing head revision
 *
Revision 1.3  2004/03/16 17:15:17  build
Forcing head revision
 *
Revision 1.2  2004/02/11 20:56:26  rhafernik
@scr 0 Log4J conversion and code cleanup
 *
Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 10 2003 15:18:14   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:10:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Aug 07 2002 19:34:16   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:53:52   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:54:58   msg
 * Initial revision.
 * 
 *    Rev 1.7   Feb 23 2002 15:04:12   mpm
 * Re-started internationalization initiative.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.6   08 Feb 2002 18:52:30   baa
 * defect fix
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.5   30 Jan 2002 16:42:40   baa
 * ui fixes
 * Resolution for POS SCR-965: Add Customer screen UI defects
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Properties;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 *  Extends the list bean to implement a second list above the main list.
 *  These lists typically have a parent/child relationship, with the
 *  top list selection influencing the main list display.
 */
public class DualListBean extends ListBean
{
    private static final long serialVersionUID = 8841180800862797513L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/18 $";

    /** header for top list */
    protected BaseHeaderBean topHeader;

    /** list to display in top section */
    protected EYSList topList;

    /** list to display in top section */
    protected ListCellRenderer topRenderer;

    /** list to display in top section */
    protected JScrollPane topScrollPane;

    /**
     * Default constructor.
     */
    public DualListBean()
    {
        beanModel = new DualListBeanModel();
    }

    /**
     * Activates this class.
     */
    @Override
    public void activate()
    {
        super.activate();

        if(topScrollPane == null)
        {
            topScrollPane = getTopScrollPane();
        }
        topHeader.activate();
        getList().addFocusListener(this);
    }

    /**
     * Deactivates this class.
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        getList().removeFocusListener(this);
    }

    /**
     * Sets the header bean.
     * 
     * @param propValue the class name of the header bean
     */
    public void setTopHeader(String propValue)
    {
        if (propValue != null)
        {
            topHeader = (BaseHeaderBean)UIUtilities.getNamedClass(propValue);
        }
        if (topHeader == null)
        {
            topHeader = new BaseHeaderBean();
        }
        if (topScrollPane != null)
        {
            topScrollPane.setColumnHeaderView(topHeader);
        }
    }

    /**
     * Convenience method for setting alignments of the header labels.
     * 
     * @param aligns the alignments for the labels
     */
    public void setTopLabelAlignments(String aligns)
    {
        getTopHeader().setAlignments(aligns);
    }

    /**
     * Convenience method for setting property tags of the header labels.
     * 
     * @param tags the property tags for the labels
     */
    public void setTopLabelTags(String tags)
    {
        getTopHeader().setLabelTags(tags);
    }

    /**
     * Convenience method for setting text of the header labels.
     * 
     * @param text the text of the labels
     */
    public void setTopLabelText(String text)
    {
        getTopHeader().setLabelText(text);
    }

    /**
     * Convenience method for setting the weights of the header labels.
     * 
     * @param text the weights of the labels
     */
    public void setTopLabelWeights(String text)
    {
        getTopHeader().setLabelWeights(text);
    }

    /**
     * Sets the list.
     * 
     * @param propValue the class name of the list
     */
    public void setTopList(String propValue)
    {
        if (propValue != null)
        {
            topList = (EYSList)UIUtilities.getNamedClass(propValue);
        }
        if (topList == null)
        {
            topList = new EYSList();
        }
        if (topScrollPane != null)
        {
            topScrollPane.getViewport().setView(list);
        }
        configureTopList();
    }

    /**
     * Sets the scroll pane.
     * 
     * @param propValue the class name of the scroll pane
     */
    public void setTopScrollPane(String propValue)
    {
        // if there is an existing scrollPane, remove it
        if (topScrollPane != null)
        {
            remove(topScrollPane);
        }
        // try to create the new scrollPane using reflection
        if (propValue != null)
        {
            topScrollPane = (JScrollPane)UIUtilities.getNamedClass(propValue);
        }
        // if the creation failed, use the default scrollPane
        if (topScrollPane == null)
        {
            topScrollPane = new JScrollPane();
        }

        configureTopScrollPane();
    }

    /**
     *  Sets the list renderer.
     *  @param propValue the class name of the renderer
     */
    public void setTopRenderer(String propValue)
    {
        if (propValue != null)
        {
            if (topRenderer == null || !topRenderer.getClass().getName().equals(propValue))
            {
                topRenderer = (AbstractListRenderer)UIUtilities.getNamedClass(propValue);
                if (topRenderer != null)
                {
                    ((AbstractListRenderer)topRenderer).initialize();
                }
            }
        }
        if (topRenderer == null)
        {
            topRenderer = new DefaultListCellRenderer();
        }
        if (topList != null && topList.getCellRenderer() != topRenderer)
        {
            topList.setCellRenderer(topRenderer);
        }
    }
    
    /**
     * Set the default selected index
     */
    @Override
    public void setVisible(boolean aFlag) 
    {
        super.setVisible(aFlag);

        if (aFlag)
        {
            if ((selectionMode == ListSelectionModel.SINGLE_SELECTION) && list.getSelectedIndex() == -1)
            {
                list.setSelectedIndex(0);
            }
        }
    }

    /**
     * Updates the bean model before sending it to the ui.
     */
    @Override
    public void updateModel()
    {
        super.updateModel();

        if (beanModel != null && beanModel instanceof DualListBeanModel)
        {
            DualListBeanModel model = (DualListBeanModel)beanModel;

            if (selectionMode == NO_SELECTION_MODE)
            {
                model.setSelectedRow(-1);
                model.setSelectedValue(null);
            }
            else
            {
                int row = list.getSelectedRow();
                model.setSelectedRow(row);

                if (row != -1)
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
     * Configures the list.
     */
    protected void configureTopList()
    {
        uiFactory.configureUIComponent(topList, "List");

        if (topRenderer != null)
        {
            topList.setCellRenderer(topRenderer);
        }
        if (selectionMode == NO_SELECTION_MODE)
        {
            topList.setEnabled(false);
            topList.setFocusable(false);
            topList.setRequestFocusEnabled(false);
        }
        else
        {
            topList.setSelectionMode(selectionMode);
            topList.setEnabled(true);
        }
    }

    /**
     * Configures the scrollPane.
     */
    protected void configureTopScrollPane()
    {
        uiFactory.configureUIComponent(topScrollPane, "ScrollPane");

        topScrollPane.setVerticalScrollBar(new EYSScrollBar());
        topScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        topScrollPane.setColumnHeaderView(getTopHeader());
        topScrollPane.getColumnHeader().setOpaque(false);

        topScrollPane.getViewport().setView(getTopList());
        topScrollPane.getViewport().setOpaque(false);

        Dimension preferredSize = uiFactory.getDimension("smallSelectListDimension");
        preferredSize.height += getTopHeader().getPreferredSize().height;
        topScrollPane.setPreferredSize(preferredSize);
        topScrollPane.setMinimumSize(preferredSize);

        add(topScrollPane, BorderLayout.NORTH);
    }

    /**
     * Gets the header bean.
     * 
     * @return a header bean
     */
    protected BaseHeaderBean getTopHeader()
    {
        // lazy instantiation
        if (topHeader == null)
        {
            setTopHeader(null);
        }
        return topHeader;
    }

    /**
     * Gets the list that will be displayed. Subclasses should override for
     * different list objects.
     * 
     * @return a JList object
     */
    protected EYSList getTopList()
    {
        if (topList == null)
        {
            setTopList(null);
        }
        return topList;
    }

    /**
     * Gets the scroll pane that wraps this list.
     * 
     * @return a renderer object
     */
    protected JScrollPane getTopScrollPane()
    {
        // lazy instantiation
        if (topScrollPane == null)
        {
            setTopScrollPane(null);
        }
        return topScrollPane;
    }

    /**
     * Enables or disables the Query list and panel.
     * 
     * @param aValue true to enable, false to disable
     */
    protected void enableQuery(boolean aValue)
    {
        getTopList().setEnabled(false);
        getTopScrollPane().setEnabled(false);
        getTopScrollPane().setRequestFocusEnabled(false);

        getScrollPane().setEnabled(aValue);
        getList().setEnabled(aValue);
    }

    /**
     * Updates the bean if It's been changed
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void updateBean()
    {
        if (beanModel instanceof DualListBeanModel)
        {    
            DualListBeanModel model = (DualListBeanModel)beanModel;
    
            POSListModel topListModel = (POSListModel) getTopList().getModel();
            topListModel.removeAllElements();
            topListModel.addElement(model.getTopListVector().get(0));
    
            Vector matches = model.getListVector();
            POSListModel posListModel = new POSListModel(matches);
    
            // set the possible matches
            getList().setModel(posListModel);

            // request focus
            if (selectionMode != NO_SELECTION_MODE)
            {
                enableQuery(true);
            }
            else
            {
                enableQuery(false);
            }

            getList().setFocusTraversalKeysEnabled(false);
            setCurrentFocus(getList());
        }
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
        super.setProps(props);
        getTopHeader().setProps(props);

        if (topRenderer != null && topRenderer instanceof AbstractListRenderer)
        {
           ((AbstractListRenderer) topRenderer).setProps(props);
        }
    }

    /**
     *  Returns the revision number of the class.
     *  @return String representation of revision number
     */
    @Override
    public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }
}
