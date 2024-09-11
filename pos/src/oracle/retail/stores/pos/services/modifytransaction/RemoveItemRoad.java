/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/RemoveItemRoad.java /main/1 2013/08/27 15:28:27 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  08/27/13 - Refactor item basket flow for tour guidelines
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.itembasket.BasketDTO;


/**
 * 
 * This road removes the selected item from the item basket transaction 
 *
 */
@SuppressWarnings("serial")
public class RemoveItemRoad extends PosLaneActionAdapter
{

    /**
     * Removes the selected item from the transaction.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ModifyTransactionCargo cargo = (ModifyTransactionCargo) bus.getCargo();
        BasketDTO basket = cargo.getBasketDTO();
        SaleReturnTransactionIfc transaction = cargo.getBasketDTO().getTransaction();

        SaleReturnLineItemIfc srli = basket.getsaleReturnSpecifiedItem();
        removeLineItem(transaction, srli.getItemID());
    }

    /**
     * Removes an item from the transaction
     * 
     * @param transaction The transaction from which the item needs to be removed
     * @param itemID ID of the item needs to be removed from transaction.
     */
    protected void removeLineItem(SaleReturnTransactionIfc transaction, String itemID)
    {

        Vector<AbstractTransactionLineItemIfc> saleLineItems = transaction.getLineItemsVector();
        List<SaleReturnLineItemIfc> itemsForRemoval = new ArrayList<SaleReturnLineItemIfc>(saleLineItems.size());

        SaleReturnLineItemIfc saleItem = null;
        for (int i = 0; i < saleLineItems.size(); i++)
        {
            saleItem = (SaleReturnLineItemIfc) saleLineItems.elementAt(i);

            if (saleItem.getItemID().equals(itemID) && saleItem.getItemSerial() == null)
            {
                itemsForRemoval.add(saleItem);
                break;
            }
        }

        for (int i = 0; i < itemsForRemoval.size(); i++)
        {
            SaleReturnLineItemIfc lineToRemove = itemsForRemoval.get(i);
            transaction.removeLineItem(lineToRemove.getLineNumber());
        }

    }

}
