/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ValidatingComboBoxModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
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
 *    ranojha   11/24/08 - Fixed for POS crash due to NPE.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;

import oracle.retail.stores.common.utility.Util;

/**
 * This class adds the ComboBoxModel interface to the DefaultListModel
 */
public class ValidatingComboBoxModel<E> extends DefaultListModel<E> implements ComboBoxModel<E>
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -4131674710215897519L;

    /** revision number **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";

    /** The selected item */
    protected E selectedItem = null;
    
    /**
     * Data
     * @deprecated as of 14.1 Use {@link DefaultListModel#delegate} instead.
     */
    protected Object[] listData = null;

    /**
     * ValidatingComboBoxModel constructor.
     */
    public ValidatingComboBoxModel()
    {
    }

    /**
     * ValidatingComboBoxModel constructor. It creates the model elements.
     * 
     * @param listVector Vector containing list elements
     */
    public ValidatingComboBoxModel(Vector<E> listVector)
    {
        listData = listVector.toArray();
        addVector2Model(listVector);
    }

    /**
     * ValidatingComboBoxModel constructor. It creates the model elements.
     * 
     * @param listArray Array containing list elements
     */
    public ValidatingComboBoxModel(E[] listArray)
    {
        listData = listArray;
        addArray2Model(listArray);
    }

    /**
     * Converts a Vector contents into a DefaultListModel
     * 
     * @param listVector Vector containing list elements
     */
    protected void addVector2Model(Vector<E> listVector)
    {
        if (listVector != null)
        {
            for (int i = 0; i < listVector.size(); i++)
            {
                addElement(listVector.elementAt(i));
            }
        }
    }

    /**
     * Converts a array contents into a DefaultListModel
     * 
     * @param listArray Array containing list elements
     */
    protected void addArray2Model(E[] listArray)
    {
        if (listArray != null)
        {
            int n = listArray.length;
            clear();
            for (int i = 0; i < n; i++)
            {
                add(i, listArray[i]);
            }
        }
    }

    /**
     * Sets the selected item.
     * 
     * @param item java.lang.Object
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setSelectedItem(Object item)
    {
        if (contains(item))
        {
            if (!item.equals(selectedItem))
            {
                selectedItem = (E)item;
                fireContentsChanged(this, -1, -1);
            }
        }
    }

    /**
     * Returns the selected item.
     * 
     * @return java.lang.Object
     */
    public Object getSelectedItem()
    {
        return selectedItem;
    }

    /* (non-Javadoc)
     * @see javax.swing.DefaultListModel#toString()
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: ValidatingComboBoxModel (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        return (strResult);
    }

    /**
     * Returns the revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * @return the listData
     * @deprecated as of 14.1. Use {@link #toArray()} instead.
     */
    public Object[] getListData()
    {
        return listData;
    }

    /**
     * @param listData the listData to set
     * @deprecated as of 14.1. Use {@link #ValidatingComboBoxModel(Object[])} instead.
     */
    public void setListData(E[] listData)
    {
        this.listData = listData;
    }

}
