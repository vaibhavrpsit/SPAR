/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/eys/EYSListUI.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   10/22/10 - cleanup while debugging mouse wheel list issue
 *    cgreene   05/26/10 - convert to oracle packaging
 *    mchellap  02/04/10 - Remove old selected index from the list
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:09 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:21:34 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:10:59 PM  Robert Pearse
 *
 *  Revision 1.10  2004/03/16 18:30:48  cdb
 *  @scr 0 Removed tabs from all java source code.
 *
 *  Revision 1.9  2004/03/09 15:03:17  jdeleau
 *  @scr 3997 Final code review comments incorporated for the multi-item
 *  select feature.
 *
 *  Revision 1.8  2004/03/08 19:51:22  jdeleau
 *  @scr 0 Changes made according to code review results (Comments
 *  added or removed, in general).
 *
 *  Revision 1.7  2004/03/05 15:49:33  jdeleau
 *  @scr 0 Multi-Item Selection added to work with touchscreen.
 *
 *  Revision 1.6  2004/02/12 20:41:41  baa
 *  @scr 0 fixjavadoc
 *
 *  Revision 1.5  2004/02/12 16:52:14  mcs
 *  Forcing head revision
 *
 *  Revision 1.4  2004/02/11 23:41:44  bwf
 *  @scr 0 Organize imports.
 *
 *  Revision 1.3  2004/02/11 23:22:58  bwf
 *  @scr 0 Organize imports.
 *
 *  Revision 1.2  2004/02/11 21:52:29  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:13:24   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 14:46:04   msg
 * Initial revision.
 *
 *    Rev 1.1   10 Apr 2002 13:59:48   baa
 * make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.eys;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicListUI;

import oracle.retail.stores.pos.ui.beans.EYSList;

/**
 * A custom ui component that overrides the standard keyboard behavior for a
 * JList and integrates mouse selection.
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class EYSListUI extends BasicListUI
{

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    // keystroke constants
    public static KeyStroke KEY_DOWN = KeyStroke.getKeyStroke("DOWN");
    public static KeyStroke KEY_UP = KeyStroke.getKeyStroke("UP");
    public static KeyStroke PAGE_DOWN = KeyStroke.getKeyStroke("PAGE_DOWN");
    public static KeyStroke PAGE_UP = KeyStroke.getKeyStroke("PAGE_UP");
    public static KeyStroke SPACE = KeyStroke.getKeyStroke("SPACE");

    /**
     * Returns a new instance of EYSListUI.
     * 
     * @return A new ListUI implementation for the ExtendYourStore look and
     *         feel.
     */
    public static ComponentUI createUI(JComponent list)
    {
        return new EYSListUI();
    }

    /**
     * Creates a new EYSListSelectionHandler.
     * 
     * @see javax.swing.plaf.basic.BasicListUI#createListSelectionListener()
     */
    @Override
    protected ListSelectionListener createListSelectionListener()
    {
        return new EYSListSelectionHandler();
    }

    /**
     * Creates a delegate that implements MouseInputListener. The delegate is
     * added to the corresponding java.awt.Component listener lists at
     * installUI() time.
     * 
     * @see #installUI
     * @see javax.swing.plaf.basic.BasicListUI#createMouseInputListener()
     */
    @Override
    protected MouseInputListener createMouseInputListener()
    {
        return new EYSMouseHandler();
    }

    /**
     * Register keyboard actions for the up and down arrow keys. The actions
     * just call out to protected methods, subclasses that want to override or
     * extend keyboard behavior should consider just overriding those methods.
     * This method is called at installUI() time.
     * 
     * @see #selectPreviousIndex
     * @see #selectNextIndex
     * @see #installUI
     * @see javax.swing.plaf.basic.BasicListUI#installKeyboardActions()
     */
    @Override
    protected void installKeyboardActions()
    {
        // normal map for focused list
        InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);
        SwingUtilities.replaceUIInputMap(list, JComponent.WHEN_FOCUSED, inputMap);

        // added for non-focused list
        ComponentInputMap noFocusMap = (ComponentInputMap) getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        SwingUtilities.replaceUIInputMap(list, JComponent.WHEN_IN_FOCUSED_WINDOW, noFocusMap);

        // replace the action map
        ActionMap map = getActionMap();

        if (map != null)
        {
            SwingUtilities.replaceUIActionMap(list, map);
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.plaf.basic.BasicListUI#paintCell(java.awt.Graphics, int, java.awt.Rectangle, javax.swing.ListCellRenderer, javax.swing.ListModel, javax.swing.ListSelectionModel, int)
     */
    @Override
    protected void paintCell(Graphics g, int row, Rectangle rowBounds, ListCellRenderer cellRenderer,
            ListModel dataModel, ListSelectionModel selModel, int leadIndex)
    {
        Object value = dataModel.getElementAt(row);
        boolean isHighlight = (row == ((EYSList) list).getHighlightRow());
        boolean cellHasFocus = isHighlight;
        boolean isSelected = (selModel.isSelectedIndex(row) || isHighlight);

        Component rendererComponent = cellRenderer.getListCellRendererComponent(list, value, row, isSelected,
                cellHasFocus);

        int cx = rowBounds.x;
        int cy = rowBounds.y;
        int cw = rowBounds.width;
        int ch = rowBounds.height;
        rendererPane.paintComponent(g, rendererComponent, list, cx, cy, cw, ch, true);
    }

    /**
     * Unregister keyboard actions for the up and down arrow keys. This method
     * is called at uninstallUI() time - subclassess should ensure that all of
     * the keyboard actions registered at installUI time are removed here.
     * 
     * @see #selectPreviousIndex
     * @see #selectNextIndex
     * @see #installUI
     * @see javax.swing.plaf.basic.BasicListUI#uninstallKeyboardActions()
     */
    @Override
    protected void uninstallKeyboardActions()
    {
        SwingUtilities.replaceUIActionMap(list, null);
        SwingUtilities.replaceUIInputMap(list, JComponent.WHEN_FOCUSED, null);
        SwingUtilities.replaceUIInputMap(list, JComponent.WHEN_IN_FOCUSED_WINDOW, null);
    }

    /**
     * Retrieves the list action map from the ui.
     */
    ActionMap getActionMap()
    {
        ActionMap map = createActionMap();

        return map;
    }

    /**
     * Appends the custom eys list actions to the standard list actions.
     */
    ActionMap createActionMap()
    {
        // to avoid stepping on normal JList behavior,
        // we just append the current action map (if it exists)
        ActionMap map = (ActionMap) UIManager.get("List.actionMap");

        if (map == null)
        {
            map = new ActionMapUIResource();
        }
        // new eys actions
        map.put("pageUp", new PageUpAction("pageUp"));
        map.put("pageDown", new PageDownAction("pageDown"));
        map.put("movePreviousRow", new EYSIncrementAction("movePreviousRow", -1));
        map.put("moveNextRow", new EYSIncrementAction("moveNextRow", 1));
        map.put("selectCurrentRow", new EYSSelectAction("selectCurrentRow"));

        return map;
    }

    /**
     * Creates a custom input map for eys lists.
     * 
     * @returns InputMap the input map
     */
    InputMap getInputMap(int condition)
    {
        InputMap map = new ComponentInputMapUIResource(list);

        map.put(KEY_UP, "movePreviousRow");
        map.put(KEY_DOWN, "moveNextRow");
        map.put(PAGE_UP, "pageUp");
        map.put(PAGE_DOWN, "pageDown");
        map.put(SPACE, "selectCurrentRow");

        return map;
    }

    //------------------------------------------------------------------------------
    // Inner classes for event handlers and keyboard navigation actions.
    //------------------------------------------------------------------------------

    //----------------------------------------------------------------------
    /**
     * This class was written to repaint the highlighted cell
     * in a Jlist when the highlight changes
     *
     * $Revision: /rgbustores_13.4x_generic_branch/1 $
     */
    public class EYSListSelectionHandler implements ListSelectionListener
    {
        /* (non-Javadoc)
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent e)
        {
            maybeUpdateLayoutState();

            Rectangle bounds = getCellBounds(list,
                                             e.getFirstIndex(),
                                             e.getLastIndex());
            if (bounds != null)
            {
                list.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
            }

            // handle the fact that we have a concept of a highlight
            // repaint the cell that was highlighted when the highlight changes
            if ((list != null) && (list instanceof EYSList))
            {
                EYSList eysList = (EYSList) list;
                int previousHighlight = eysList.getPreviousHightlightRow();
                int highlight = eysList.getHighlightRow();
                if ((previousHighlight != highlight) &&
                    (previousHighlight != EYSList.NO_SELECTION))
                {
                    Rectangle rowBounds = getCellBounds(list,
                                                        previousHighlight,
                                                        previousHighlight);
                    if (rowBounds != null)
                    {
                        list.repaint(rowBounds);
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------------
    /**
     * Action that increments the highlighted item.
     */
    private static class EYSIncrementAction extends AbstractAction
    {
        private static final long serialVersionUID = -2746639519691463265L;

        int amount;

        /**
         * Increments the highlighted item.
         * 
         * @param name the item name
         * @param amount the new amount
         */
        private EYSIncrementAction(String name, int amount)
        {
            super(name);
            this.amount = amount;
        }

        /**
         * Perform action. Increments index and ensures its visibility
         * 
         * @param e action event
         */
        public void actionPerformed(ActionEvent e)
        {
            EYSList list = (EYSList) e.getSource();
            int index = getNextIndex(list);
            int oldIndex = list.getLastVisibleIndex();
            if (index >= 0 && index < list.getModel().getSize())
            {
                list.setSelectedIndex(index);
                list.removeSelectionInterval(oldIndex, index);
                ensureIndexIsVisible(list, index);
            }
        }

        /**
         * Ensures the particular index is visible. This simply forwards the
         * method to list.
         * 
         * @param list the list to operate against.
         * @param index the index of the selected item.
         */
        protected void ensureIndexIsVisible(JList list, int index)
        {
            list.ensureIndexIsVisible(index);
        }

        /**
         * Returns the next index to select. This is based on the lead selected
         * index and the <code>amount</code> ivar.
         * 
         * @param list the list to operate against.
         */
        protected int getNextIndex(JList list)
        {
            int index = ((EYSList)list).getSelectedRow();
            int size = list.getModel().getSize();

            if (index == -1)
            {
                if (size > 0)
                {
                    if (amount > 0)
                    {
                        index = 0;
                    }
                    else
                    {
                        index = size - 1;
                    }
                }
            }
            else
            {
                index += amount;
            }
            return index;
        }
    }

    // ------------------------------------------------------------------------------
    /**
     * Mouse input, and focus handling for JList. An instance of this class is
     * added to the appropriate java.awt.Component lists at installUI() time.
     * 
     * @see #createMouseInputListener
     * @see #installKeyboardActions
     * @see #installUI
     */
    private class EYSMouseHandler extends MouseInputHandler implements MouseInputListener
    {

        /**
         * Flag indicating that the current element in the list was selected on
         * mouse press.
         */
        private boolean EYSselectedOnPress = false;

        /* (non-Javadoc)
         * @see javax.swing.plaf.basic.BasicListUI.MouseInputHandler#mouseDragged(java.awt.event.MouseEvent)
         */
        public void mouseDragged(MouseEvent e)
        {
            if (!SwingUtilities.isLeftMouseButton(e))
                return;

            if (e.getSource() instanceof JList)
            {
                JList list = (JList) e.getSource();

                if (!list.isEnabled())
                    return;

                if (e.isShiftDown() || e.isControlDown())
                    return;

                int row = convertYToRow(e.getY());
                if (row != -1)
                {
                    Rectangle cellBounds = getCellBounds(list, row, row);
                    if (cellBounds != null)
                    {
                        // list.scrollRectToVisible(cellBounds);
                        list.addSelectionInterval(row, row);
                    }
                }
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.plaf.basic.BasicListUI.MouseInputHandler#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent e)
        {
            if (e.isConsumed())
            {
                EYSselectedOnPress = false;
                return;
            }
            
            EYSselectedOnPress = true;
            EYSAdjustFocusAndSelection(e);
        }

        /* (non-Javadoc)
         * @see javax.swing.plaf.basic.BasicListUI.MouseInputHandler#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent e)
        {
            if (e.getSource() instanceof JList)
            {
                list = (JList) e.getSource();
            }
            else
            {
                return;
            }

            if (EYSselectedOnPress &&
                    SwingUtilities.isLeftMouseButton(e))
            {
                list.setValueIsAdjusting(false);
            }
            else
            {
                EYSAdjustFocusAndSelection(e);
            }
        }

        /**
         * Adjust the focus and selection for the list. We only respond to left
         * mouse clicks. If the list is not the current focus, focus is
         * requested.
         */
        protected void EYSAdjustFocusAndSelection(MouseEvent e)
        {
            if (!SwingUtilities.isLeftMouseButton(e))
            {
                return;
            }

            if (e.getSource() instanceof JList)
            {
                list = (JList) e.getSource();
            }
            else
            {
                return;
            }

            if (!list.isEnabled())
            {
                return;
            }

            if (!list.hasFocus() && list.isRequestFocusEnabled())
            {
                list.requestFocus();
            }

            int row = convertYToRow(e.getY());
            if (row != -1)
            {
                boolean adjusting = (e.getID() == MouseEvent.MOUSE_PRESSED) ? true : false;
                list.setValueIsAdjusting(adjusting);
                int anchorIndex = list.getAnchorSelectionIndex();
                if (e.isShiftDown() && (anchorIndex != -1))
                {
                    list.setSelectionInterval(anchorIndex, row);
                }
                else if (list.isSelectedIndex(row))
                {
                    list.removeSelectionInterval(row, row);
                }
                else
                {
                    list.addSelectionInterval(row, row);
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    /**
     * Action that adds the current highlighted item to the list selection.
     */
    private static class EYSSelectAction extends AbstractAction
    {
        private static final long serialVersionUID = -7271327919891022892L;

        /**
         * Default constructor
         */
        private EYSSelectAction(String name)
        {
            super(name);
        }

        /**
         * Perform action. Mark multiple items as selected if list mode allows
         * it.
         * 
         * @param e action event
         */
        public void actionPerformed(ActionEvent e)
        {
            EYSList list = (EYSList) e.getSource();

            if (list.getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
            {
                int row = list.getSelectedRow();

                if (row != -1)
                {
                    if (list.isSelectedIndex(row))
                    {
                        list.removeSelectionInterval(row, row);
                    }
                    else
                    {
                        list.addSelectionInterval(row, row);
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------------
    /**
     * Action to move down one page.
     */
    private static class PageDownAction extends EYSIncrementAction
    {
        private static final long serialVersionUID = -2715020028083037321L;

        /**
         * Default constructor
         */
        protected PageDownAction(String name)
        {
            super(name, -1);
        }

        /**
         * Draws a visible rectangle around the selected item on the list
         * 
         * @param list the list to operate against.
         * @param index the index of the selected item.
         */
        protected void ensureIndexIsVisible(JList list, int index)
        {
            Rectangle visRect = list.getVisibleRect();
            Rectangle cellBounds = list.getCellBounds(index, index);
            cellBounds.y = Math.max(0, cellBounds.y + cellBounds.height - visRect.height);
            cellBounds.height = visRect.height;
            list.scrollRectToVisible(cellBounds);
        }

        /**
         * Retrieves next index
         * 
         * @param list the list to operate against.
         * @returns int the next index going down
         */
        protected int getNextIndex(JList list)
        {
            int index = list.getLastVisibleIndex();
            ListSelectionModel lsm = list.getSelectionModel();

            if (index == -1)
            {
                // Will happen if size < viewport size.
                index = list.getModel().getSize() - 1;
            }
            if (lsm.getLeadSelectionIndex() == index)
            {
                Rectangle visRect = list.getVisibleRect();
                visRect.y += visRect.height + visRect.height - 1;
                index = list.locationToIndex(visRect.getLocation());
                if (index == -1)
                {
                    index = list.getModel().getSize() - 1;
                }
            }
            return index;
        }
    }

    // ------------------------------------------------------------------------------
    /**
     * Action to move up one page.
     */
    private static class PageUpAction extends EYSIncrementAction
    {
        private static final long serialVersionUID = 1132614667465858961L;

        /**
         * Handles page up action.
         * 
         * @param name the current element
         */
        protected PageUpAction(String name)
        {
            super(name, -1);
        }

        /**
         * Draws a visible rectangle around the selected item on the list
         * 
         * @param list the list to operate against.
         * @param index the index of the selected item.
         */
        protected void ensureIndexIsVisible(JList list, int index)
        {
            Rectangle visRect = list.getVisibleRect();
            Rectangle cellBounds = list.getCellBounds(index, index);
            cellBounds.height = visRect.height;
            list.scrollRectToVisible(cellBounds);
        }

        /**
         * Retrieves next index
         * 
         * @param list the list to operate against.
         * @returns int the next index going up
         */
        protected int getNextIndex(JList list)
        {
            int index = list.getFirstVisibleIndex();
            ListSelectionModel lsm = list.getSelectionModel();

            if (lsm.getLeadSelectionIndex() == index)
            {
                Rectangle visRect = list.getVisibleRect();
                visRect.y = Math.max(0, visRect.y - visRect.height);
                index = list.locationToIndex(visRect.getLocation());
            }
            return index;
        }
    }
}
