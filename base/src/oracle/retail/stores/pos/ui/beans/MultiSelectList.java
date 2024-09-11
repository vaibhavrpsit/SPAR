/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/MultiSelectList.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:52 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   10/22/10 - correct reference to EYSListUI
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:06 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:38 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:43 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:11:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:52:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:56:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:31:00   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:36:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:17:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import oracle.retail.stores.pos.ui.plaf.eys.EYSListUI;

/**
 * Implements a JList that allows mouse selection and the eys multiple item
 * selection functionality.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class MultiSelectList extends JList
{
    private static final long serialVersionUID = -5494266615019705858L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** the ui component to use */
    public static final String uiClassID = "EYSListUI";

    /** numeric constant for no list selection */
    public static final int NO_SELECTION = -1;

    /** an internal selection model */
    protected MultiListSelectionModel listSelectionModel = null;

    /**
     * Constructor that associates a renderer with the list. This is implemented
     * for compatibility with existing code.
     * 
     * @param msr a subclass of AbstractMultiSelectRenderer
     */
    public MultiSelectList(DefaultListModel dlm)
    {
        super(dlm);
        setSelectionModel(getListSelectionModel());
    }

    /**
     * Constructor that associates a renderer with the list. This is implemented
     * for compatibility with existing code.
     * 
     * @param msr a subclass of AbstractMultiSelectRenderer
     */
    public MultiSelectList(AbstractMultiSelectRenderer msr)
    {
        super();
        initialize(msr);
    }

    /**
     * Constructor that associates a renderer with the list. This is implemented
     * for compatibility with existing code.
     * 
     * @param msr a subclass of AbstractMultiSelectRenderer
     */
    public MultiSelectList(AbstractListRenderer msr)
    {
        super();
        initialize(msr);
    }

    /**
     * Initializes the list.
     */
    public void initialize(AbstractMultiSelectRenderer msr)
    {
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setSelectionModel(getListSelectionModel());
        setCellRenderer(msr);
    }

    /**
     * Initializes the list.
     */
    public void initialize(AbstractListRenderer msr)
    {
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setSelectionModel(getListSelectionModel());
        setCellRenderer(msr);
    }

    /**
     * Overridden to use the custom list ui component.
     */
    public String getUIClassID()
    {
        return uiClassID;
    }

    /**
     * Overridden to use the custom list ui component.
     */
    public void setUI(EYSListUI newUI)
    {
        super.setUI(newUI);
    }

    /**
     * Overridden to use the custom list ui component.
     */
    public void updateUI()
    {
        setUI(new EYSListUI());
    }

    /**
     * Overridden so that the current selected index is not included in the
     * selection model during arrow key navigation. The selected index is
     * included when the spacebar character is hit.
     */
    public void setSelectedIndex(int index)
    {
        listSelectionModel.setHighlightItem(index);
        ensureIndexIsVisible(index);
        repaint();
    }

    /**
     * Gets the currently highlighted row from the selection model.
     * 
     * @return the currently highlighted row
     */
    public int getSelectedRow()
    {
        return listSelectionModel.getHighlightItem();
    }

    /**
     * Gets an array of all selected indices along with the current highlighted
     * row (if it is not already selected).
     * 
     * @return an array of integer objects represented the selected rows
     */
    public int[] getAllSelectedRows()
    {
        int row = getSelectedRow();

        if (!isSelectedIndex(row) || row != NO_SELECTION)
        {
            addSelectionInterval(row, row);
        }
        return getSelectedIndices();
    }

    /**
     * creates a List selection model.
     **/
    private MultiListSelectionModel getListSelectionModel()
    {
        if (listSelectionModel == null)
        {
            listSelectionModel = new MultiListSelectionModel();
        }
        return listSelectionModel;
    }

    // -------------------------------------------------------------------------
    /**
     * Inner class that fires a value changed event. This is used when the
     * highlighted row changes without affecting the selection.
     */
    private class MultiListSelectionModel extends DefaultListSelectionModel
    {
        private static final long serialVersionUID = 6340691868279475521L;
        private int highlightItem = NO_SELECTION;

        protected int getHighlightItem()
        {
            return highlightItem;
        }

        protected void setHighlightItem(int newValue)
        {
            int old = highlightItem;
            highlightItem = newValue;
            fireValueChanged(old, newValue);
        }

        public void setSelectionInterval(int first, int last)
        {
            highlightItem = last;
            super.setSelectionInterval(first, last);
        }

        public void addSelectionInterval(int first, int last)
        {
            highlightItem = last;
            super.addSelectionInterval(first, last);
        }
    }
}
