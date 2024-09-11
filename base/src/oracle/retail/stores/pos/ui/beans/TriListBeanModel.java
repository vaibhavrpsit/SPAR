/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TriListBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
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

import java.util.Vector;

import oracle.retail.stores.pos.ui.POSListModel;


//---------------------------------------------------------------------
/**
    This model provides the information required by the 
    TriListBean.
    <p>
    @version
*/
//---------------------------------------------------------------------
public class TriListBeanModel extends DualListBeanModel
{

	/** the list model */
    protected POSListModel bottomListModel;

    /** the selected item in the list */
    protected int bottomSelectedRow;

    /** array of selected rows for multiple selections */
    protected int[] bottomSelectedRows;

    /** the selected value from the list */
    protected Object bottomSelectedValue = null;

    //--------------------------------------------------------------------------
    /**
     *  Default constructor.
     */
    public TriListBeanModel()
    {
        super();
        bottomListModel = new POSListModel();
        bottomSelectedRow = -1;
        bottomSelectedRows = new int[0];
    }

    //--------------------------------------------------------------------------
    /**
     *  Gets the data in the list model as a POSListModel.
     *  @return the list model
     */
    public POSListModel getBottomListModel()
    {
        return bottomListModel;
    }

    //--------------------------------------------------------------------------
    /**
     *  Gets the data in the list model as a vector of objects.
     *  @return a vector of objects
     */
    public Vector getBottomListVector()
    {
        return bottomListModel.toVector();
    }

    //--------------------------------------------------------------------------
    /**
     *  Gets the data in the list model as an array of objects.
     *  @return an object array
     */
    public Object[] getBottomListArray()
    {
        return bottomListModel.toArray();
    }

    //--------------------------------------------------------------------------
    /**
     *  Gets the index of the selected row in the list.
     *  @return the selected row index
     */
    public int getBottomSelectedRow()
    {
        return bottomSelectedRow;
    }

    //--------------------------------------------------------------------------
    /**
     *  Gets an array of the selected indices if the list is in multiple
     *  selection mode.
     *  @return an array of the selected row indices
     */
    public int[] getBottomSelectedRows()
    {
        return bottomSelectedRows;
    }

    //--------------------------------------------------------------------------
    /**
     *  Gets the value in the model that corresponds to the selected row.
     *  @return the selected value, or null if nothing is selected
     */
    public Object getBottomSelectedValue()
    {
        return bottomSelectedValue;
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the list model using a POSListModel as the data source.
     *  @param newList a POSListModel
     */
    public void setBottomListModel(POSListModel newList)
    {
        bottomListModel = newList;
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the list model using an object array as the data source.
     *  @param newList an object array
     */
    public void setBottomListModel(Object[] newList)
    {
        bottomListModel = new POSListModel(newList);
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the list model using a vector as the data source.
     *  @param newList a vector
     */
    public void setBottomListModel(Vector newList)
    {
        bottomListModel = new POSListModel(newList);
    }

    public void setBottomSelectedRow(int row)
    {
        bottomSelectedRow = row;
    }

    public void setBottomSelectedRows(int[] rows)
    {
        bottomSelectedRows = rows;
    }

    public void setBottomSelectedValue(Object aValue)
    {
        bottomSelectedValue = aValue;
    }
}
