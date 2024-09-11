/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/MultiSelectJTable.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
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
 *   Revision 1.1  2004/07/24 00:17:57  jdeleau
 *   @scr 3750 Add multi-select to the JTable that will allow non-contiguous
 *   selections via a spacebar.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * This class contains various and sundry modifications to support
 * multi select as defined in the POS requirements.  The keyboard
 * multi-select does not work in default swing implementations.
 * This class is just a work-around that limitation.
 * 
 * There is no border in this implementation, to denote the focused
 * item, that may be added in the future.
 * 
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class MultiSelectJTable extends JTable
{
    
    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
    protected int highlightRow = -1;
    /**
     *  Constructor
     * 
     */
    public MultiSelectJTable()
    {
        init();
    }

    /**
     * @param arg0 tableModel
     */
    public MultiSelectJTable(TableModel arg0)
    {
        super(arg0);
        init();
    }

    /**
     * @param arg0 tableModel
     * @param arg1 tableColumnModel
     */
    public MultiSelectJTable(TableModel arg0, TableColumnModel arg1)
    {
        super(arg0, arg1);
        init();
    }

    /**
     * @param arg0 tableModel
     * @param arg1 tableColumnModel
     * @param arg2 listSelectionModel
     */
    public MultiSelectJTable(TableModel arg0, TableColumnModel arg1, ListSelectionModel arg2)
    {
        super(arg0, arg1, arg2);
        init();
    }

    /**
     * @param arg0 rows
     * @param arg1 columns
     */
    public MultiSelectJTable(int arg0, int arg1)
    {
        super(arg0, arg1);
        init();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public MultiSelectJTable(Vector arg0, Vector arg1)
    {
        super(arg0, arg1);
        init();
    }

    /**
     *  
     * @param arg0
     * @param arg1
     */
    public MultiSelectJTable(Object[][] arg0, Object[] arg1)
    {
        super(arg0, arg1);
        init();
    }
    
    /**
     * Initialize this JTable to support the multi-select mechanism
     * we desire.
     *
     */
    public void init()
    {
        setDefaultRenderer(Object.class, new MultiItemSelectTableCellRenderer());
        
        KeyStroke moveDownStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
        this.getInputMap().put(moveDownStroke, "moveDownAnchor");
        this.getActionMap().put("moveDownAnchor", new MoveDownAction(this));
        
        KeyStroke moveUpStroke = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
        this.getInputMap().put(moveUpStroke, "moveUpAnchor");
        this.getActionMap().put("moveUpAnchor", new MoveUpAction(this));
        
        KeyStroke selectStroke = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0);
        this.getInputMap().put(selectStroke, "selectAction");
        this.getActionMap().put("selectAction", new SelectAction(this));
        
    }
    
    /**
     * Set the currently highlighted row in the table
     *  
     *  @param row
     */
    public void setHighlightRow(int row)
    {
        this.highlightRow = row;
    }

    /**
     * Someone hit the down arrow key.  Move the highlighted
     * row down
     *
     * $Revision: /rgbustores_13.4x_generic_branch/1 $
     */
    class MoveDownAction extends AbstractAction 
    {
        JTable table;
     
        /**
         * Constructor
         *  
         * @param table
         */
        public MoveDownAction(JTable table)
        {
            this.table = table;
        }
        
        /**
         * Perform the action of moving the highlighed down
         *  
         * @param e
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {      
            int row = table.getSelectionModel().getAnchorSelectionIndex();
            if(row < table.getModel().getRowCount())
            {
                table.getSelectionModel().setAnchorSelectionIndex(row + 1);
                // This is a little one off, for some reason when
                // the table is first displayed row 0 is automatically
                // selected, so we need to deselect it if someone moves off it.
                if(row == 0 && highlightRow == -1)
                {
                    table.getSelectionModel().removeSelectionInterval(0, 0);
                }
                setHighlightRow(row+1);
            }
        }
    }

    /**
     * Someone hit the up arrow key.  Move the highlighted row up
     *
     * $Revision: /rgbustores_13.4x_generic_branch/1 $
     */
    class MoveUpAction extends AbstractAction 
    {
        JTable table;
     
        /**
         * Constructor
         *  
         * @param table
         */
        public MoveUpAction(JTable table)
        {
            this.table = table;
        }
        
        /**
         * Perform the action of moving the row up
         *  
         * @param e
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {      
            int row = table.getSelectionModel().getAnchorSelectionIndex();
            if(row > 0)
            {
                table.getSelectionModel().setAnchorSelectionIndex(row - 1);
                setHighlightRow(row-1);
            }
        }
    }

    /**
     * Someone pressed the spacebar, select or deselect a row.
     *
     * $Revision: /rgbustores_13.4x_generic_branch/1 $
     */
    class SelectAction extends AbstractAction
    {
        JTable table;
        /**
         * Constructor
         *  
         * @param table
         */
        public SelectAction(JTable table)
        {
            this.table = table;
        }
        
        /**
         * Perform Action of selecting a row
         *  
         * @param e
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            int row = table.getSelectionModel().getAnchorSelectionIndex();
            if(table.isRowSelected(row))
            {
                table.getSelectionModel().removeSelectionInterval(row, row);    
            }
            else
            {
                table.getSelectionModel().addSelectionInterval(row, row);    
            }
        }
    }

    /**
     * This is the cell renderer, that will highlight botht he selected and
     * highlighted rows.
     *
     * $Revision: /rgbustores_13.4x_generic_branch/1 $
     */
    class MultiItemSelectTableCellRenderer extends JLabel implements TableCellRenderer
    {
        
        /**
         * Constructor
         */
        public MultiItemSelectTableCellRenderer()
        {
            setOpaque(true);
            setBorder(noFocusBorder);
        }
        // implements javax.swing.table.TableCellRenderer
        /**
         *
         * Returns the default table cell renderer.
         *
         * @param table  the <code>JTable</code>
         * @param value  the value to assign to the cell at
         *          <code>[row, column]</code>
         * @param isSelected true if cell is selected
         * @param hasFocus true if cell has focus
         * @param row  the row of the cell to render
         * @param column the column of the cell to render
         * @return the default table cell renderer
         */
        public Component getTableCellRendererComponent(JTable table, Object value,
                              boolean isSelected, boolean hasFocus, int row, int column)
        {
            if(row == highlightRow || isSelected)
            {
               super.setForeground(table.getSelectionForeground());
               super.setBackground(table.getSelectionBackground());
               /* if(row == highlightRow)
               {
                   setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
               }
               else
               {
                   setBorder(null);
               }*/
            }
            else
            {
                super.setForeground(java.awt.Color.black);
                super.setBackground(java.awt.Color.white);
            }
            super.setText(value.toString());
            return this;
        }
        
    }

    /**
     * The selected row is always the highlighted row
     *  
     * @return selectedRow
     * @see javax.swing.JTable#getSelectedRow()
     */
    public int getSelectedRow()
    {
        return getSelectionModel().getAnchorSelectionIndex();
    }

    /**
     * Override the getSelectedRows method to return all the
     * selected rows, plus the highlighted one.
     *  
     * @return selectedRows
     * @see javax.swing.JTable#getSelectedRows()
     */
    public int[] getSelectedRows()
    {
        int[] results = super.getSelectedRows();
        int lastRow = this.getSelectedRow();
        if(results != null)
        {
            // Determine if the highlighted row needs to be added to the list of selected rows
            boolean addLastRow = true;
            for(int i=0; i<results.length; i++)
            {
                if(results[i] == lastRow)
                {
                    addLastRow = false;
                    break;
                }
            }
            if(addLastRow)
            {
                int newResults[] = new int[results.length + 1];
                for(int i=0; i<results.length; i++)
                {
                    newResults[i] = results[i];
                }
                newResults[newResults.length - 1] = lastRow;
                results = newResults;
            }
        }
        else
        {
            results = new int[] { lastRow };
        }
        return results;
    }
    
    /**
     * Clear old selections and reset
     */
    public void activate()
    {
        clearSelection();
        setHighlightRow(-1);
    }
}
