/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/MultipleListSelectionModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
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
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;

//-------------------------------------------------------------------------
/**
    This Selection model is used by the MultiSelectList to help override
    the default traversal of the list
    @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
**/
//-------------------------------------------------------------------------
public class MultipleListSelectionModel extends DefaultListSelectionModel
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3844584112917997743L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";
    /** The current position  **/
    protected int curPos = -1;
    /** The list in which the model resides  **/
    protected JList list = null;
    /** A list of all selected rows  **/
    protected Vector allSelectedRows = new Vector();
    /** Static index value indicating no selected row.  **/
    protected static final int NO_SELECTION = -1;
    
    //---------------------------------------------------------------------
    /**
        constructor
    */
    //---------------------------------------------------------------------
    public MultipleListSelectionModel()
    {
    }

    //---------------------------------------------------------------------
    /**
        constructor
        @param jlist the list component using this Selection model.
    */
    //---------------------------------------------------------------------
    public MultipleListSelectionModel(JList jlist)
    {
        list = jlist;
    }
    
    //---------------------------------------------------------------------
    /**
        Set the current position in the list
        @param pos The position in the list
    */
    //---------------------------------------------------------------------
    public void setCurrentPosition(int pos)
    {
        if((pos < (list.getModel().getSize()) && pos >= 0) ||
           (curPos == -1 && pos == 0))
        {
            int oldPos = curPos;
            curPos = pos;
            //to limit the calls just force the refresh
            //on the old cell and the new cell
            fireValueChanged(oldPos, oldPos, false);
            fireValueChanged(curPos, curPos, false);
        }
    }
    
    //---------------------------------------------------------------------
    /**
        Increment the current list highlight by 1
    */
    //---------------------------------------------------------------------
    public void incCurrentPosition()
    {
        if(curPos < (list.getModel().getSize() - 1))
        {
            int oldPos = curPos;
            curPos++;
            fireValueChanged(oldPos, curPos, false);
        }
    }
    
    //---------------------------------------------------------------------
    /**
        Decrement the current list highlight by 1
    */
    //---------------------------------------------------------------------
    public void decCurrentPosition()
    {
        if(curPos > 0)
        {
            int oldPos = curPos;
            curPos--;
            fireValueChanged(curPos, oldPos, false);
        }
    }
    //---------------------------------------------------------------------
    /**
    Get the position of where the Highlight is in the list
    */
    //---------------------------------------------------------------------
    public int getCurrentPosition()
    {
        return(curPos);
    }
    //---------------------------------------------------------------------
    /**
       Add index to all selected rows vector.
       @param value The index of the selected row.
    **/
    //---------------------------------------------------------------------
    public void addToAllSelectedRows(int index)
    {
        Integer value = new Integer(index);
        // if empty Vector, OK to add at end
        if (allSelectedRows.isEmpty())
        {
            allSelectedRows.addElement(value);
        }
        // if not already in Vector, find the proper sorted place
        else if (allSelectedRows.contains(value) == false)
        {
            // if this index is after the last one in the Vector, add at end
            if (index > ((Integer)allSelectedRows.elementAt(allSelectedRows.size()-1)).intValue())
            {
                allSelectedRows.addElement(value);
            }
            else
            {
                // search for place to add this index
                for (int i=0;i<allSelectedRows.size();i++)
                {
                    if (index < ((Integer)allSelectedRows.elementAt(i)).intValue())
                    {
                        allSelectedRows.insertElementAt(value, i);
                        break;
                    }
                }
            }
        }
    }
    //---------------------------------------------------------------------
    /**
       Remove index from all selected rows vector.
       @param value The index of the selected row.
    **/
    //---------------------------------------------------------------------
    public void removeFromAllSelectedRows(int value)
    {
        Integer index = new Integer(value);
        if (allSelectedRows.contains(index) == true)
        {
            allSelectedRows.removeElementAt(allSelectedRows.indexOf(index));
        }
    }
    //---------------------------------------------------------------------
    /**
       This method returns all selected rows.
       @return all selected rows as a vector of Integer objects.
    */
    //---------------------------------------------------------------------
    public Vector getAllSelectedRows()
    {
        return allSelectedRows;
    }
    //---------------------------------------------------------------------
    /**
       This method returns first selected row.
       @return int the index of the first selected row; -1 if none are selected.
    */
    //---------------------------------------------------------------------
    public int getFirstSelectedRowIndex()
    {
        int index = -1;
        if (allSelectedRows.size() > 0)
        {
            Integer i = (Integer)allSelectedRows.elementAt(0);
            index = i.intValue();
        }
        return index;
    }
    //---------------------------------------------------------------------
    /**
       This method returns all selected rows and current item.
       @return all selected rows as a vector of Integer objects.
    */
    //---------------------------------------------------------------------
    public Vector getAllSelectedRowsWithCurrentItem()
    {
        Vector allSelected = (Vector)allSelectedRows.clone();
        int currentItem = getCurrentPosition();
        
        // put current item in vector if not already there

        // if there are no selected items
        if (currentItem != NO_SELECTION && allSelected.size()==0)
        {
            allSelected.insertElementAt(new Integer(currentItem), 0);
        }
        else
        {
            for (int i=allSelected.size()-1;i>-1;i--)
            {
                int thisItem = ((Integer)allSelected.elementAt(i)).intValue();
                // if current item is selected
                if (thisItem == currentItem)
                {
                    break;
                }
                // if current item is not selected
                else if (thisItem < currentItem)
                {
                    // put current item in proper ordered spot
                    allSelected.insertElementAt(new Integer(currentItem), i+1);
                    break;
                }
                else if (i == 0)
                {
                    // if current item is before all other selected items
                    if (thisItem > currentItem)
                    {
                        // put current item in proper ordered spot
                        allSelected.insertElementAt(new Integer(currentItem), i);
                        break;
                    }
                }
            }
        }
        return allSelected;
    }
    //---------------------------------------------------------------------
    /**
       Remove all selected rows of vector.
    **/
    //---------------------------------------------------------------------
    public void removeAllSelectedRows()
    {
        allSelectedRows.removeAllElements();
    }
}
