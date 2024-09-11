/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/itembasket/ItemBasketReturnShuttle.java /main/14 2014/01/07 18:00:13 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    mjwall 01/07/14 - fix dereferencing of null objects.
 *    tkshar 02/04/13 - avoid initializing a transaction when there is 
 *                      no item basket in the cargo
 *    cgreen 03/16/12 - split transaction-methods out of utilitymanager
 *    cgreen 03/09/12 - add support for journalling queues by current register
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    aariye 04/02/09 - For Serial Number check of multiple quantities in
 *                      basket
 *    aariye 04/01/09 - For Item Basket
 *    mchell 04/01/09 - Setting alteration item flag for alteration items
 *    aariye 03/09/09 - FOr error in ItemBasket
 *    aariye 02/28/09 - For ejournal of ItemBasket
 *    aariye 02/19/09 - Added capapbility for Size check and items not
 *                      authorized for sale in basket
 *    vikini 02/03/09 - Including Code Review Comments
 *    aariye 02/02/09 - Added files for Item Basket feature
 *    aariye 01/28/09 - Adding elemts for Item Basket Feature
 *    vikini 01/23/09 - Formatting changes
 *    vikini 01/23/09 - Updating line items in basket Transaction
 *    vikini 01/21/09 - Creation of ITembasket return Shuttle
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.itembasket;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.modifytransaction.ModifyTransactionCargo;

import org.apache.log4j.Logger;

/**
 * The return shuttle is used to create the transaction, before going to Modify
 * Transaction
 */
public class ItemBasketReturnShuttle implements ShuttleIfc
{
    private static final long serialVersionUID = 6179150794258025135L;

    protected static final Logger logger = Logger.getLogger(ItemBasketReturnShuttle.class);

    ItemBasketCargo itemBsktCargo = null;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        logger.debug("In the Item Basket Return Shuttle");
        itemBsktCargo = (ItemBasketCargo)bus.getCargo();
        SaleReturnTransactionIfc transaction = itemBsktCargo.getTransaction();

        if (transaction != null)
        {
            setBasketDetails(transaction);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        ModifyTransactionCargo cargo = (ModifyTransactionCargo)bus.getCargo();
        logger.debug(bus.getCurrentLetter().getName());
        SaleReturnTransactionIfc trn = itemBsktCargo.getTransaction();

        if (itemBsktCargo.getBasket() != null && trn == null)
        {
            trn = initializeTransaction(bus);
            setBasketDetails(trn);
        }
        if (trn != null)
        {
            trn.setIsItemBasketTransactionComplete(true);
        }
        cargo.setBasketDTO(itemBsktCargo.getBasket());
        cargo.setTransaction(trn);
    }

    private void setBasketDetails(SaleReturnTransactionIfc trn)
    {
        HashMap itemsMap = itemBsktCargo.getPLUItems();
        BasketDTO basket = itemBsktCargo.getBasket();

        ArrayList<String> itemIDs = itemBsktCargo.getItemIdList();
        SaleReturnLineItemIfc srli = null;

        if (basket != null)
        {
            ArrayList basketItems = basket.getBasketItems();
            BigDecimal qty = new BigDecimal(1.0);
            basket.setPLUItemMap(itemsMap);

            if (itemsMap != null)
            {
                for (int itmCtr = 0; itmCtr < itemsMap.size(); itmCtr++)
                {
                    PLUItemIfc item = (PLUItemIfc)itemsMap.get(itemIDs.get(itmCtr));

                    if (item != null)
                    {
                        for (int basketItmCtr = 0; basketItmCtr < basketItems.size(); basketItmCtr++)
                        {
                            BasketLineItemDTO basketLineItem = (BasketLineItemDTO)basketItems.get(basketItmCtr);
                            if (basketLineItem.getItemID() != null
                                    && (basketLineItem.getItemID()).equals(item.getItemID()))
                            {
                                qty = basketLineItem.getItemQuantity();
                            }

                        }
                        String srliItemId = null;
                        BigDecimal oneQty = new BigDecimal(1.0);

                        if (item.isSerializedItem() && qty.compareTo(oneQty) > 0)
                        {
                            for (int i = 0; i < qty.intValue(); i++)
                            {
                                srli = trn.addPLUItem(item, oneQty);
                                srliItemId = srli.getItemID();
                                srli.setPreSplitLineNumber(srli.getLineNumber());
                                basket.setSaleReturnItemInList(srli);
                                if (item.isAlterationItem())
                                {
                                    srli.setAlterationItemFlag(true);
                                }

                            }
                        }
                        else
                        {
                            srli = trn.addPLUItem(item, qty);
                            srliItemId = srli.getItemID();
                            srli.setPreSplitLineNumber(srli.getLineNumber());
                            basket.setSaleReturnItemInList(srli);
                            if (item.isAlterationItem())
                            {
                                srli.setAlterationItemFlag(true);
                            }

                        }

                        // If the plu item is an alteration item then set the
                        // alteration item flag

                        for (int basketItmCtr = 0; basketItmCtr < basketItems.size(); basketItmCtr++)
                        {
                            BasketLineItemDTO basketLineItem = (BasketLineItemDTO)basketItems.get(basketItmCtr);
                            if (srli != null && basketLineItem.getItemID().equals(srliItemId))
                            {
                                srli.setItemSizeCode(basketLineItem.getItemSizeCode());
                            }

                        }

                        // basket.setSaleReturnItemInList(srli);
                        qty = new BigDecimal(1.0);
                    }
                }

                basket.setTransaction(trn);
            }
        }
    }

    private SaleReturnTransactionIfc initializeTransaction(BusIfc bus)
    {
        ModifyTransactionCargo cargo = (ModifyTransactionCargo)bus.getCargo();
        SaleReturnTransactionIfc transaction = DomainGateway.getFactory().getSaleReturnTransactionInstance();
        transaction.setCashier(cargo.getOperator());
        transaction.setSalesAssociate(cargo.getSalesAssociate());

        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        utility.initializeTransaction(transaction);
        return transaction;
    }
}
