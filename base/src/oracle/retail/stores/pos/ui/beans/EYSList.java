/* ===========================================================================
* Copyright (c) 1999, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EYSList.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/13/14 - add generics
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *3    360Commerce 1.2         3/31/2005 4:28:08 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:21:34 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:10:59 PM  Robert Pearse   
 *
 Revision 1.5  2004/03/16 17:15:17  build
 Forcing head revision
 *
 Revision 1.4  2004/03/09 15:03:17  jdeleau
 @scr 3997 Final code review comments incorporated for the multi-item 
 select feature.
 *
 Revision 1.3  2004/03/05 15:49:33  jdeleau
 @scr 0 Multi-Item Selection added to work with touchscreen.
 *
 Revision 1.2  2004/02/11 20:56:27  rhafernik
 @scr 0 Log4J conversion and code cleanup
 *
 Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 updating to pvcs 360store-current
 *
 *
 *    Rev 1.1   Aug 14 2003 17:36:10   xx20254
 * Added logic to repaint the previously highlighted item.  This prevents
 *   a "ghost" highlight box from remaining over the no-longer highlighted
 *   item
 * 
 *    Rev 1.0   Aug 29 2003 16:10:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:50:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:53:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   30 Jan 2002 16:42:44   baa
 * ui fixes
 * Resolution for POS SCR-965: Add Customer screen UI defects
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import oracle.retail.stores.pos.ui.POSListModel;

/**
 * Implements a JList that allows mouse selection and the eys multiple item
 * selection functionality.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class EYSList<E> extends JList<E>
{
    private static final long serialVersionUID = 4458142552250572367L;
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** the ui component to use */
    private static final String uiClassID = "EYSListUI";
    /** numeric constant for no list selection */
    public static final int NO_SELECTION = -1;
    /** an internal selection model */
    protected EYSListSelectionModel listSelectionModel = null;

    /**
     * Default constructor
     */
    public EYSList()
    {
        this(new POSListModel<E>());
    }

    /**
     * Constructor that associates a model with the list. This is implemented
     * for compatibility with existing code.
     * 
     * @param msr a subclass of AbstractMultiSelectRenderer
     */
    public EYSList(DefaultListModel<E> dlm)
    {
        super(dlm);
        setSelectionModel(getListSelectionModel());
    }

    /**
     * Constructor that associates a renderer with the list. This is
     * implemented for compatibility with existing code.
     * 
     * @param msr a subclass of AbstractMultiSelectRenderer
     */
    public EYSList(AbstractListRenderer<? super E> msr)
    {
        initialize(msr);
    }

    /**
     * Initializes the list.
     */
    public void initialize(AbstractListRenderer<? super E> msr)
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
     * Overridden for multiselect mode so that the current selected index is not
     * included in the selection model during arrow key navigation. The
     * selected index is included when the spacebar character is hit.
     */
    public void setSelectedIndex(int index)
    {
        if (getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        {
            listSelectionModel.setHighlightItem(index);
            ensureIndexIsVisible(index);
            repaint();
        }
        else
        {
            super.setSelectedIndex(index);
        }
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
     */
    private EYSListSelectionModel getListSelectionModel()
    {
        if (listSelectionModel == null)
        {
            listSelectionModel = new EYSListSelectionModel();
        }
        return listSelectionModel;
    }

    /**
     * Return the current hightlight row from the selection model. This is a
     * convenience method which accesses the list selection model.
     * 
     * @return int the current hightlight row
     */
    public int getHighlightRow()
    {
        int result = NO_SELECTION;
        if ((listSelectionModel != null) && (listSelectionModel instanceof EYSListSelectionModel))
        {
            result = listSelectionModel.getHighlightItem();
        }
        return result;
    }

    /**
     * Return the previously highlighted row from the selection model. This is a
     * convenience method which accesses the list selection model.
     * 
     * @return the previously highlighted row
     */
    public int getPreviousHightlightRow()
    {
        int result = NO_SELECTION;
        if ((listSelectionModel != null) && (listSelectionModel instanceof EYSListSelectionModel))
        {
            result = listSelectionModel.getPreviousHighlightItem();
        }
        return result;
    }
}
