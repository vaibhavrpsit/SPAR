/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2016-2017 MAXHyperMarkets, Inc.    All Rights Reserved.
  Rev 1.1	Nadia Arora		20 Feb,2017		MMRP Changes
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import java.beans.PropertyChangeSupport;

import max.retail.stores.domain.factory.MAXDomainObjectFactoryIfc;
import max.retail.stores.domain.lineitem.MAXMaximumRetailPriceChangeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;



/**
 * @author vichandr
 *
 */
public class MAXMaximumRetailPriceListBeanModel extends POSBaseBeanModel {

	protected transient PropertyChangeSupport propertyChange;
    /**
        line items to be display on screen
    **/
    protected MAXMaximumRetailPriceChangeIfc[] lineItems ;
    /**
        Indicates the selected item.
    **/
    protected MAXMaximumRetailPriceChangeIfc selectedItemMaximumRetailPrice = null;

//  ----------------------------------------------------------------------
    /**
     * ItemListBeanModel constructor comment.
     */
    //----------------------------------------------------------------------
    public MAXMaximumRetailPriceListBeanModel()
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
    public MAXMaximumRetailPriceChangeIfc[] getItemMaximumRetailPriceList()
    {
        return lineItems;
    }

    //----------------------------------------------------------------------
    /**
     * Adds an item to the items list.
     * @param item  the item to be added.
     */
    //----------------------------------------------------------------------
    public void addItemMaximumRetailPrice(MAXMaximumRetailPriceChangeIfc item)
    {
        lineItems[lineItems.length]= item;
    }

    //----------------------------------------------------------------------
    /**
     * Updates the  the items list.
     * @param items  the items added.
     */
    //----------------------------------------------------------------------
    public void setItemMaximumRetailPriceList(MAXMaximumRetailPriceChangeIfc[] items)
    {
        if (lineItems == null)
        {
           lineItems = new MAXMaximumRetailPriceChangeIfc[items.length];
        }
        for (int i=0; i < items.length; i++)
        {
          lineItems[i]= ((MAXDomainObjectFactoryIfc) DomainGateway.getFactory()).getMaximumRetailPriceChangeInstance();
          lineItems[i] = items[i];
        }
    }

    //---------------------------------------------------------------------
    /**
     *  Returns selected item.
     *  @return selecteditem
    **/
    //---------------------------------------------------------------------
    public MAXMaximumRetailPriceChangeIfc getSelectedItem()
    {
        return(selectedItemMaximumRetailPrice);
    }

    //---------------------------------------------------------------------
    /**
     *  Sets selected item.
     *  @param item  the item that was selected
    **/
    //---------------------------------------------------------------------
    public void setSelectedItem(MAXMaximumRetailPriceChangeIfc item)
    {
        if (selectedItemMaximumRetailPrice == null)
        {
        	selectedItemMaximumRetailPrice = ((MAXDomainObjectFactoryIfc) DomainGateway.getFactory()).getMaximumRetailPriceChangeInstance();
        }
        selectedItemMaximumRetailPrice = item;
    }
}
