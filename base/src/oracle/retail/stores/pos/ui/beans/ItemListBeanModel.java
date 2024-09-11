/* ===========================================================================
* Copyright (c) 2001, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemListBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:47 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:32 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:22:28 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:38 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.beans.PropertyChangeSupport;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.PLUItemIfc;
//--------------------------------------------------------------------------
/**
 * This is the bean model used by the ItemListBean. <P>
 * @version $KW=@(#); $Ver=pos_4.5.0:49; $EKW;
 * @see oracle.retail.stores.pos.ui.beans.ItemListBean
**/
//--------------------------------------------------------------------------
public class ItemListBeanModel extends POSBaseBeanModel
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:49; $EKW;";
    /**
       property change listener
    **/
    protected transient PropertyChangeSupport propertyChange;
    /**
        line items to be display on screen
    **/
    protected PLUItemIfc[] lineItems ;
    /**
        Indicates the selected item.
    **/
    protected PLUItemIfc selectedItem = null;


    //----------------------------------------------------------------------
    /**
     * ItemListBeanModel constructor comment.
     */
    //----------------------------------------------------------------------
    public ItemListBeanModel()
    {
        super();
        initialize();
    }

    //----------------------------------------------------------------------
    /**
     *  Instatiates the item list
     */
    //----------------------------------------------------------------------
    public void initialize()
    {
      lineItems= null;
    }

    //----------------------------------------------------------------------
    /**
     *  Returns the list of matching items
         *  return lineItems vector of items
         */
    //----------------------------------------------------------------------
    public PLUItemIfc[] getItemList()
    {
        return lineItems;
    }

    //----------------------------------------------------------------------
    /**
     * Adds an item to the items list.
     * @param item  the item to be added.
     */
    //----------------------------------------------------------------------
    public void addItem(PLUItemIfc item)
    {
        lineItems[lineItems.length]= item;
    }

    //----------------------------------------------------------------------
    /**
     * Updates the  the items list.
     * @param items  the items added.
     */
    //----------------------------------------------------------------------
    public void setItemList(PLUItemIfc[] items)
    {
        if (lineItems == null)
        {
           lineItems = new PLUItemIfc[items.length];
        }
        for (int i=0; i < items.length; i++)
        {
          lineItems[i]= DomainGateway.getFactory().getPLUItemInstance();
          lineItems[i] = items[i];
        }
    }

    //---------------------------------------------------------------------
    /**
     *  Returns selected item.
     *  @return selecteditem
    **/
    //---------------------------------------------------------------------
    public PLUItemIfc getSelectedItem()
    {
        return(selectedItem);
    }

    //---------------------------------------------------------------------
    /**
     *  Sets selected item.
     *  @param item  the item that was selected
    **/
    //---------------------------------------------------------------------
    public void setSelectedItem(PLUItemIfc item)
    {
        if (selectedItem == null)
        {
            selectedItem = DomainGateway.getFactory().getPLUItemInstance();
        }
        selectedItem = item;
    }

}  // end ItemListBeanModel
