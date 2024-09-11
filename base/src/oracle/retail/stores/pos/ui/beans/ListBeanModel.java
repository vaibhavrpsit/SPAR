/* ===========================================================================
 * Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ListBeanModel.java /main/16 2012/05/31 18:40:56 acadar Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/13/14 - add generics
 *    acadar    05/23/12 - CustomerManager refactoring
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.List;
import java.util.Vector;

import oracle.retail.stores.pos.ui.POSListModel;

/**
 * Standard model for list beans.
 */
public class ListBeanModel<E> extends POSBaseBeanModel
{
    private static final long serialVersionUID = -4180523927669173465L;

    /** the list model */
    protected POSListModel<E> listModel;

    /** the selected item in the list */
    protected int selectedRow;

    /** array of selected rows for multiple selections */
    protected int[] selectedRows;

    /** the selected value from the list */
    protected E selectedValue;

    /**
     * Default constructor.
     */
    public ListBeanModel()
    {
        listModel = new POSListModel<E>();
        selectedRow = -1;
        selectedRows = new int[0];
    }

    /**
     * Gets the data in the list model as a POSListModel.
     * 
     * @return the list model
     */
    public POSListModel<E> getListModel()
    {
        return listModel;
    }

    /**
     * Gets the data in the list model as a vector of objects.
     * 
     * @return a vector of objects
     */
    public Vector<E> getListVector()
    {
        return listModel.toVector();
    }

    /**
     * Gets the data in the list model as an array of objects.
     * 
     * @return an object array
     */
    public Object[] getListArray()
    {
        return listModel.toArray();
    }

    /**
     * Gets the index of the selected row in the list.
     * 
     * @return the selected row index
     */
    public int getSelectedRow()
    {
        return selectedRow;
    }

    /**
     * Gets an array of the selected indices if the list is in multiple
     * selection mode.
     * 
     * @return an array of the selected row indices
     */
    public int[] getSelectedRows()
    {
        return selectedRows;
    }

    /**
     * Sets the index that corresponds to the selected row.
     */
    public void setSelectedRow(int row)
    {
        selectedRow = row;
    }

    /**
     * Sets the indexes that corresponds to the selected rows.
     */
    public void setSelectedRows(int[] rows)
    {
        selectedRows = rows;
    }

    /**
     * Gets the value in the model that corresponds to the selected row.
     * 
     * @return the selected value, or null if nothing is selected
     */
    public E getSelectedValue()
    {
        return selectedValue;
    }

    /**
     * Sets the value in the model that corresponds to the selected row.
     */
    public void setSelectedValue(E aValue)
    {
        selectedValue = aValue;
    }

    /**
     * Sets the list model using a POSListModel as the data source.
     * 
     * @param newList a POSListModel
     */
    public void setListModel(POSListModel<E> newList)
    {
        listModel = newList;
    }

    /**
     * Sets the list model using an object array as the data source.
     * 
     * @param newList an object array
     */
    public void setListModel(E[] newList)
    {
        listModel = new POSListModel<E>(newList);
    }

    /**
     * Sets the list model using an object array as the data source.
     * 
     * @param newList an object array
     */
    public void setListModel(List<E> newList)
    {
        listModel = new POSListModel<E>(newList);
    }
}
