/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ServiceTableModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
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
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.table.AbstractTableModel;

import oracle.retail.stores.foundation.utility.Util;

//---------------------------------------------------------------------
/**
    This class is used to setup the table model for a JTable
    @return String representation of object
        @deprecated as of release 5.0.0
**/
//---------------------------------------------------------------------
public class ServiceTableModel extends AbstractTableModel
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 5909673122379870563L;

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";
    /**
        constant for class name
    **/
    public static final String CLASSNAME = "ServiceTableModel";
    /**
        Array of names.
    **/
    protected String[] names = null;
    /**
        Number of rows.
    **/
    protected int rows = 0;
    /**
        Number of columns.
    **/
    protected int columns = 0;
    /**
        Two dimensional of array of data objects.
    **/
    protected Object data[][] = null;

    //---------------------------------------------------------------------
    /**
        Constructor for class.
    **/
    //---------------------------------------------------------------------
    public ServiceTableModel()
    {
    }

    //---------------------------------------------------------------------
    /**
        Constructor for class.
        @param columnName the array of column names
        @param rowSize the number of rows
    **/
    //---------------------------------------------------------------------
    public ServiceTableModel(String[] columnName, int rowSize)
    {
        rows = rowSize;
        columns = columnName.length;

        names = new String[columns];
        data = new Object[rows][columns];
        System.arraycopy(columnName, 0, names, 0, columns);
    }

    //---------------------------------------------------------------------
    /**
        Constructor for class.
        @param rowSize the number of rows
        @param columnSize the number of columns
    **/
    //---------------------------------------------------------------------
    public ServiceTableModel(int rowSize, int columnSize)
    {
        rows = rowSize;
        columns = columnSize;
        data = new Object[rows][columns];
    }

    //---------------------------------------------------------------------
    /**
        Constructor for class.
        @param values a 2-dimensional array of Objects
    **/
    //---------------------------------------------------------------------
    public ServiceTableModel(Object[][] values)
    {
        data = new Object[rows][columns];

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                data[i][j] = values[i][j];
            }
        }
    }

    //---------------------------------------------------------------------
    /**
        This method is used to get the number of rows in the table.
        @return int the number of rows
    **/
    //---------------------------------------------------------------------
    public int getRowCount()
    {
        return rows;
    }

    //---------------------------------------------------------------------
    /**
        This method is used to get the number of columns in the table.
        @return int the number of columns
    **/
    //---------------------------------------------------------------------
    public int getColumnCount()
    {
        return columns;
    }

    //---------------------------------------------------------------------
    /**
        This method is used to get the value for a specific row and column.
        @param row the row number
        @param column the column number
        @return Object the Object value
    **/
    //---------------------------------------------------------------------
    public Object getValueAt(int row, int column)
    {
        return data[row][column];
    }

    //---------------------------------------------------------------------
    /**
        This method is used to set the value for a specific row and column.
        @param Object the Object value
        @param row the row number
        @param column the column number
    **/
    //---------------------------------------------------------------------
    public void setValueAt(Object value, int row, int column)
    {
        data[row][column] = value;
    }

    //---------------------------------------------------------------------
    /**
        This method is used to set whether a cell is editable.
        @param row the row number
        @param column the column number
        @return boolean the boolean value
    **/
    //---------------------------------------------------------------------
    public boolean isCellEditable(int row, int column)
    {
        return false;
    }


    //---------------------------------------------------------------------
    /**
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class: " + CLASSNAME + " (Revision " +
                                       getRevisionNumber() +
                                       ")" +
                                       hashCode());
        // pass back result
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }
}
