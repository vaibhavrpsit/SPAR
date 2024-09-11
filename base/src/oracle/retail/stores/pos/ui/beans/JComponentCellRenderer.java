/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/JComponentCellRenderer.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:35 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:22:35 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:52 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

//--------------------------------------------------------------------------
/**
     Gets the table cell renderer component. <P>
     @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
**/
//--------------------------------------------------------------------------
public class JComponentCellRenderer implements TableCellRenderer 
{
    //--------------------------------------------------------------------------
    /**
        Gets the table cell renderer component. <P>
        @param table the cell's table
        @param value the componenet
        @param isSelected 
        @param hasFocus
        @param row
        @param column
    **/
    //--------------------------------------------------------------------------
    public Component getTableCellRendererComponent(JTable table, 
                               Object value, boolean isSelected, 
                          boolean hasFocus, int row, int column)
     {
         return (JComponent)value; 
    }
}    
