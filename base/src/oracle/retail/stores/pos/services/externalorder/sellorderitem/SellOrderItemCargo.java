/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/sellorderitem/SellOrderItemCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:59 mszekely Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED (MM/DD/YY)
*    sgu    06/09/10 - merge after refresh to latest
*    sgu    06/08/10 - fix tab
*    sgu    06/08/10 - remove unused import
*    sgu    06/08/10 - add item # & desc to the screen prompt. fix unknow item
*                      screen to disable price and quantity for external item
*    acadar 06/03/10 - refresh to tip
*    sgu    06/02/10 - use ExternalOrderItem from common moduel
*    sgu    06/01/10 - check in after merge
*    sgu    06/01/10 - skip UOM flow for external order item
*    sgu    06/01/10 - check in order sell item flow
*    cgreen 05/26/10 - convert to oracle packaging
*    acadar 05/21/10 - renamed from _externalorder to externalorder
*    acadar 05/17/10 - temporarily rename the package
*    acadar 05/17/10 - additional logic added for processing orders
*    acadar 05/14/10 - initial version for external order processing
*    acadar 05/14/10 - initial version
* ===========================================================================
*/
package oracle.retail.stores.pos.services.externalorder.sellorderitem;

// domain imports
import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderIfc;
import oracle.retail.stores.domain.externalorder.ExternalOrderSaleItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.pos.services.sale.SaleCargo;


/**
    This is the cargo used by the Sell Order Item service
**/
public class SellOrderItemCargo extends SaleCargo implements SellOrderItemCargoIfc
{
    /**
     * generated serial version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * The external order to process
     */
    protected ExternalOrderIfc externalOrder;

    /**
     * Current external order sale item to process
     */
    protected ExternalOrderSaleItemIfc externalOrderItem;

    /**
     * List of PLUs
     */
    protected PLUItemIfc[] itemList;

    /**
     * The flag indificating if the item has been modified
     */
    protected boolean modifiedFlag = false;

   /**
    * @return the external order
    */
   public ExternalOrderIfc getExternalOrder()
   {
	   return externalOrder;
   }

   /**
    * Set the external order
    * @param externalOrder the external order
    */
   public void setExternalOrder(ExternalOrderIfc externalOrder)
   {
	   this.externalOrder = externalOrder;
   }

	/**
     * @return the currentExternalOrderItem
     */
    public ExternalOrderSaleItemIfc getExternalOrderItem()
    {
        return externalOrderItem;
    }

    /**
     * @param currentExternalOrderItem the currentExternalOrderItem to set
     */
    public void setExternalOrderItem(ExternalOrderSaleItemIfc currentExternalOrderItem)
    {
        this.externalOrderItem = currentExternalOrderItem;
    }

    //----------------------------------------------------------------------
    /**
     * @return the itemList
     */
    //----------------------------------------------------------------------
    public PLUItemIfc[] getItemList()
    {
        return itemList;
    }

    //----------------------------------------------------------------------
    /**
     * @param itemList the itemList to set
     */
    //----------------------------------------------------------------------
    public void setItemList(PLUItemIfc[] itemList)
    {
        this.itemList = itemList;
    }

    //----------------------------------------------------------------------
    /**
     * Returns the itemScanned flag.
     *
     * @return boolean
     */
    //----------------------------------------------------------------------
    public boolean isItemScanned()
    {
    	// An external order item is never scanned. Always return false.
    	return false;
    }

    /**
     * Sets the itemScanned flag.
     *
     * @param value
     *            boolean
     */
    //----------------------------------------------------------------------
    public void setItemScanned(boolean value)
    {
    	//Ignore the value. It is not possible to scan an external order item
    }

    //----------------------------------------------------------------------
    /**
       Gets the item quantity value. <P>
       @return long value
    **/
    //----------------------------------------------------------------------
    public BigDecimal getItemQuantity()
    {
        return getExternalOrderItem().getQuantity();
    }

    //----------------------------------------------------------------------
    /**
     * Sets the items quantity value <P>
     * @param value The BigDecimal representation of the items quantity
     */
    //----------------------------------------------------------------------
    public void setItemQuantity(BigDecimal value)
    {
    	//Ignore the value. Can't set quantity for an external order item.
    }

    //----------------------------------------------------------------------
    /**
     * Returns true if the item has been modfied; false otherwise
     *
     * @return boolean True if the item has been modified. False otherwise.
     */
    //----------------------------------------------------------------------
    public boolean getModifiedFlag()
    {
        return (modifiedFlag);
    }

    /**
     * Sets the flag that determines if the item has been modified.
     *
     * @param value True if the item has been modified. False otherwise.
     */
    public void setModifiedFlag(boolean value)
    {
        modifiedFlag = value;
    }

    /*
     * This function does nothing.
     */
    public void completeItemNotFound()
    {
    }

    /**
     * Returns the enableCancel
     * @return boolean
     */
    public boolean isEnableCancelItemNotFoundFromReturns()
    {
        //for PLUCargoIfc
        return true;
    }
    /**
     * Set enable cancel
     * @param enableCancel The enableCancel to set.
     */
    public void setEnableCancelItemNotFoundFromReturns(boolean enableCancel)
    {
        //for PLUCargoIfc
    }

    //----------------------------------------------------------------------
    /**
     * Returns the flag indicating if the plu item is from an external order
     * @return the boolean flag
     */
    //----------------------------------------------------------------------
    public boolean isExternalOrder()
    {
        return true;
    }

    //----------------------------------------------------------------------
    /**
        Returns the external item price.
        <P>
        @return the CurrencyIfc value
    **/
    //----------------------------------------------------------------------
    public CurrencyIfc getItemPrice()
    {
        // use external price if available; otherwise use the plu item price
        CurrencyIfc price = getExternalOrderItem().getPrice();
        if (price == null)
        {
            price = super.getItemPrice();
        }

        return price;
    }

    //----------------------------------------------------------------------
    /**
     * Return the item description
     * @return the String value
     */
    //----------------------------------------------------------------------
    public String getItemDescription()
    {
        // use plu description first before getting the external item description
        String desc = super.getItemDescription();
        if (desc == null)
        {
            desc = getExternalOrderItem().getDescription();
        }

        return desc;
    }
}


