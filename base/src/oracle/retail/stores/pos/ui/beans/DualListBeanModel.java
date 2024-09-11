/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DualListBeanModel.java /main/16 2012/05/31 18:40:56 acadar Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    05/23/12 - CustomerManager refactoring
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 3    360Commerce 1.2         3/31/2005 4:27:52 PM   Robert Pearse   
 2    360Commerce 1.1         3/10/2005 10:21:11 AM  Robert Pearse   
 1    360Commerce 1.0         2/11/2005 12:10:43 PM  Robert Pearse   
 *
Revision 1.4  2004/03/16 17:15:22  build
Forcing head revision
 *
Revision 1.3  2004/03/16 17:15:17  build
Forcing head revision
 *
Revision 1.2  2004/02/11 20:56:26  rhafernik
@scr 0 Log4J conversion and code cleanup
 *
Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:10:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:53:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:54:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   30 Jan 2002 16:42:42   baa
 * ui fixes
 * Resolution for POS SCR-965: Add Customer screen UI defects
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.util.Vector;

import oracle.retail.stores.pos.ui.POSListModel;

//------------------------------------------------------------------------------
/**
 *  Standard model for list beans.
 */
//------------------------------------------------------------------------------
public class DualListBeanModel extends ListBeanModel
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 5128269847318014869L;

    /** the list model */
    protected POSListModel topListModel;

    /** the selected item in the list */
    protected int topSelectedRow;

    /** array of selected rows for multiple selections */
    protected int[] topSelectedRows;

    /** the selected value from the list */
    protected Object topSelectedValue = null;

    //--------------------------------------------------------------------------
    /**
     *  Default constructor.
     */
    public DualListBeanModel()
    {
        super();
        topListModel = new POSListModel();
        topSelectedRow = -1;
        topSelectedRows = new int[0];
    }

    //--------------------------------------------------------------------------
    /**
     *  Gets the data in the list model as a POSListModel.
     *  @return the list model
     */
    public POSListModel getTopListModel()
    {
        return topListModel;
    }

    //--------------------------------------------------------------------------
    /**
     *  Gets the data in the list model as a vector of objects.
     *  @return a vector of objects
     */
    @SuppressWarnings("rawtypes")
    public Vector getTopListVector()
    {
        return topListModel.toVector();

    }

    //--------------------------------------------------------------------------
    /**
     *  Gets the data in the list model as an array of objects.
     *  @return an object array
     */
    public Object[] getTopListArray()
    {
        return topListModel.toArray();
    }

    //--------------------------------------------------------------------------
    /**
     *  Gets the index of the selected row in the list.
     *  @return the selected row index
     */
    public int getTopSelectedRow()
    {
        return topSelectedRow;
    }

    //--------------------------------------------------------------------------
    /**
     *  Gets an array of the selected indices if the list is in multiple
     *  selection mode.
     *  @return an array of the selected row indices
     */
    public int[] getTopSelectedRows()
    {
        return topSelectedRows;
    }

    //--------------------------------------------------------------------------
    /**
     *  Gets the value in the model that corresponds to the selected row.
     *  @return the selected value, or null if nothing is selected
     */
    public Object getTopSelectedValue()
    {
        return topSelectedValue;
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the list model using a POSListModel as the data source.
     *  @param newList a POSListModel
     */
    public void setTopListModel(POSListModel newList)
    {
        topListModel = newList;
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the list model using an object array as the data source.
     *  @param newList an object array
     */
    public void setTopListModel(Object[] newList)
    {
        topListModel = new POSListModel(newList);
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the list model using a vector as the data source.
     *  @param newList a vector
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setTopListModel(Vector newList)
    {
        topListModel = new POSListModel(newList);
    }

    public void setTopSelectedRow(int row)
    {
        topSelectedRow = row;
    }

    public void setTopSelectedRows(int[] rows)
    {
        topSelectedRows = rows;
    }

    public void setTopSelectedValue(Object aValue)
    {
        topSelectedValue = aValue;
    }
}
