/*===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/eys/EYSBasicTableHeaderUI.java /main/2 2014/05/20 12:14:37 cgreene Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* cgreene     05/20/14 - refactor list model sorting
* abhinavs    05/11/14 - Sorting Item search results enhancement
* abhinavs    05/11/14 - Initial Version
* abhinavs    05/11/14 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.ui.plaf.eys;
import java.awt.Component;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.beans.BaseHeaderBean;

/**
 * Implements a table header UI.
 *
 * @version $Revision: /main/2 $
 * @author abhinavs
 * @since 14.1
 */
public class EYSBasicTableHeaderUI extends EYSPanelUI
{
    public static final Logger logger = Logger.getLogger(EYSBasicTableHeaderUI.class);

    // the widget that this UI provides behavior for
    private BaseHeaderBean header;
    private HeaderMouseListener mouseListener;
    private HeaderContainerListener containerListener;
    private SortOrderChangeListener sortOrderChangeListener;

    /**
     * Creates basic table header UI object
     *
     * @param c the  JComponent
     * @returns the basic table header UI
     */
    public static ComponentUI createUI(JComponent c)
    {
        return new EYSBasicTableHeaderUI();
    }

    /* (non-Javadoc)
     * @see javax.swing.plaf.basic.BasicPanelUI#installDefaults(javax.swing.JComponent)
     */
    @Override
    public void installDefaults(JPanel p)
    {
        super.installDefaults(p);
        header = (BaseHeaderBean)p;
        installListeners(header);
    }

    /* (non-Javadoc)
     * @see javax.swing.plaf.basic.BasicPanelUI#uninstallDefaults(javax.swing.JPanel)
     */
    @Override
    public void uninstallDefaults(JPanel p)
    {
        super.uninstallDefaults(p);
        uninstallListeners();
    }

    /**
     * This method attaches a container listener to listen for header labels
     * being added.
     *
     * @param header
     */
    protected void installListeners(final BaseHeaderBean header)
    {
        mouseListener = new HeaderMouseListener();
        containerListener = new HeaderContainerListener();
        sortOrderChangeListener = new SortOrderChangeListener();
        header.addContainerListener(containerListener);
        header.addPropertyChangeListener("sortOrder", sortOrderChangeListener);
    }

    /**
     * Cleanup listeners that were added to the header.
     */
    protected void uninstallListeners()
    {
        header.removePropertyChangeListener("sortOrder", sortOrderChangeListener);
        header.removeContainerListener(containerListener);
        for (Component child : header.getComponents())
        {
            child.removeMouseListener(mouseListener);
        }
        header = null;
        mouseListener = null;
        containerListener = null;
    }

    // ------------------------------------------------------------------------
    /**
     * Listen for header labels being added to the header. Add a mouse listener
     * to each one so as to trigger sorting when it is clicked.
     */
    protected class HeaderContainerListener implements ContainerListener
    {
        @Override
        public void componentAdded(ContainerEvent e)
        {
            if (e.getChild() instanceof JLabel)
            {
                // header label is being added. Add a mouselistener to it.
                e.getChild().addMouseListener(mouseListener);
            }
        }

        @Override
        public void componentRemoved(ContainerEvent e)
        {
            if (e.getChild() instanceof JLabel)
            {
                // header label is being removed. Remove mouselistener from it.
                e.getChild().removeMouseListener(mouseListener);
            }
        }
    }

    // -------------------------------------------------------------------------
    /**
     * Listener to install icons if the sort order changes.
     *
     * @author cgreene
     */
    protected class SortOrderChangeListener implements PropertyChangeListener
    {

        /* (non-Javadoc)
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt)
        {
            int sortedColumn = header.getSortedColumn();
            Icon sortedIcon = determineIcon();
            // put an icon in the label and remove any sort icon from all other columns.
            JLabel[] labels = header.getLabels();
            for (int i = labels.length - 1; i >= 0; i--)
            {
                if (i == sortedColumn)
                {
                    labels[i].setIcon(sortedIcon);
                }
                else
                {
                    labels[i].setIcon(null);
                }
            }
        }

        /**
         * @return
         */
        private Icon determineIcon()
        {
            Icon icon = null;
            if (header.getSortOrder() == SortOrder.ASCENDING)
            {
                icon = header.getIconAscending();
                if (icon == null)
                {
                    icon = UIManager.getIcon("Table.ascendingSortIcon");
                }
            }
            else if (header.getSortOrder() == SortOrder.DESCENDING)
            {
                icon = header.getIconDescending();                
                if (icon == null)
                {
                    icon = UIManager.getIcon("Table.descendingSortIcon");
                }
            }
            return icon;
        }
    }

    // ------------------------------------------------------------------------
    /**
     * This mouse action listeners should be added to the respective header
     * labels. Once clicked, the header's list is sorted based on which label
     * got clicked.
     */
    protected class HeaderMouseListener extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            if (header.getColumnSortProperties() == null ||
                    !(header.getList().getModel() instanceof POSListModel))
            {
                // quick exit, do nothing on this click
                return;
            }

            // Respond to mouse click by checking for a sorting property.
            JLabel clickedHeader = (JLabel)e.getSource();
            for (int i = header.getComponentCount() - 1; i >= 0; i--)
            {
                if (clickedHeader == header.getComponent(i))
                {
                    String beanProperty = header.getColumnSortProperty(i);
                    if (beanProperty != null)
                    {
                        if (header.getSortOrder() != SortOrder.ASCENDING)
                        {
                            header.setSortOrder(SortOrder.ASCENDING, i);
                        }
                        else
                        {
                            header.setSortOrder(SortOrder.DESCENDING, i);
                        }
                        header.sortList();
                    }
                    break;
                }
            }
        }
    }
}