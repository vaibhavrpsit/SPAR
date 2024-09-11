/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    mchell 08/27/13 - Refactor item basket flow for tour guidelines
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    aariye 02/28/09 - For ejournal of ItemBasket
 *    aariye 02/27/09 - For ejournal entries of Item Basket
 *    aariye 02/02/09 - Added element BasketDTO for ItemBasket feature.
 *    aariye 01/28/09 - Adding element for ItemBasket feature
 *    vikini 01/23/09 - Changed the toString methed
 *    vikini 01/21/09 - Basket DTO Creation
 *    vikini 01/21/09 - Basket Creation
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.itembasket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;

/**
 * Data transfer object for item basket details.
 */
public class BasketDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private int seqNumber = -1; 

    private ArrayList<PLUItemIfc> items = new ArrayList<PLUItemIfc>();

    private SaleReturnTransactionIfc transaction = null;

    private ArrayList<SaleReturnLineItemIfc> saleReturnItems = new ArrayList<SaleReturnLineItemIfc>();

    private ArrayList<BasketLineItemDTO> basketItems = null;

    private String errorCondition = null;

    private String basketId = null;

    private HashMap<String, PLUItemIfc> pluItemMap = null;

    public int getSeqNumber()
    {
        return seqNumber;
    }

    public void incrementSeqNumber()
    {
        seqNumber++;
    }

    public int getTotalItemsInBasket()
    {
        return saleReturnItems.size();
    }

    public ArrayList<PLUItemIfc> getItems()
    {
        return items;
    }

    public void setItemInList(PLUItemIfc ifc)
    {
        this.items.add(ifc);
    }

    public ArrayList<SaleReturnLineItemIfc> getSaleReturnItems()
    {
        return saleReturnItems;
    }

    public void setSaleReturnItemInList(SaleReturnLineItemIfc ifc)
    {
        this.saleReturnItems.add(ifc);
    }

    public SaleReturnTransactionIfc getTransaction()
    {
        return transaction;
    }

    public void setTransaction(SaleReturnTransactionIfc transaction)
    {
        this.transaction = transaction;
    }

    public PLUItemIfc getSpecifiedItem()
    {
        return (PLUItemIfc) ((SaleReturnLineItemIfc) saleReturnItems.get(this.seqNumber)).getPLUItem();
    }

    public SaleReturnLineItemIfc getsaleReturnSpecifiedItem()
    {
        return (SaleReturnLineItemIfc) this.saleReturnItems.get(this.seqNumber);
    }

    public ArrayList<BasketLineItemDTO> getBasketItems()
    {
        return basketItems;
    }

    public void setBasketItems(ArrayList<BasketLineItemDTO> basketItems)
    {
        this.basketItems = basketItems;
    }

    public String getErrorCondition()
    {
        return errorCondition;
    }

    public void setBasketId(String basketId)
    {
        this.basketId = basketId;
    }

    public void setPLUItemMap(HashMap<String, PLUItemIfc> pluItemMap)
    {
        this.pluItemMap = pluItemMap;
    }

    public HashMap<String, PLUItemIfc> getPLUItemMap()
    {
        return pluItemMap;
    }

    public String getBasketId()
    {
        return basketId;
    }

    public void setErrorCondition(String error)
    {
        this.errorCondition = error;
    }

    public String toString()
    {
        return basketItems.toString();
    }
}
