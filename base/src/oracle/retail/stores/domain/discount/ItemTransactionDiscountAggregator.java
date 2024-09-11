/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/ItemTransactionDiscountAggregator.java /main/3 2012/12/07 12:21:56 jswan Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* jswan       12/06/12 - Modified to support JDBC opertions for order tax and
*                        discount status.
* sgu         08/29/12 - check null pointer for item discounts
* sgu         08/27/12 - persist a TransactionDiscountAuditIfc discount rule
* sgu         08/27/12 - read transaction discount audit from db
* sgu         08/23/12 - add support for transaction discount audit
* sgu         08/22/12 - add new files
* sgu         08/22/12 - add new file
* sgu         08/22/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.domain.discount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;

public class ItemTransactionDiscountAggregator implements ItemTransactionDiscountAggregatorIfc
{
    /**
     * Public constructor
     */
    public ItemTransactionDiscountAggregator()
    {
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.ItemTransactionDiscountAggregatorIfc#aggregate(oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc[])
     */
    public TransactionDiscountAuditIfc[] aggregate(SaleReturnLineItemIfc[] lineItems)
    {
        TransactionDiscountAuditIfc[] transactionDiscounts = new TransactionDiscountAuditIfc[0];
        return aggregate(lineItems, transactionDiscounts);
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.ItemTransactionDiscountAggregatorIfc#aggregate(oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc[], oracle.retail.stores.domain.discount.TransactionDiscountAuditIfc[])
     */
    public TransactionDiscountAuditIfc[] aggregate(SaleReturnLineItemIfc[] lineItems, TransactionDiscountAuditIfc[] transactionDiscounts)
    {
        Collection<TransactionDiscountAuditIfc> transactionDiscountList = new ArrayList<TransactionDiscountAuditIfc>();
        transactionDiscountList.addAll(Arrays.asList(transactionDiscounts));

        Collection<ItemTransactionDiscountAuditIfc>[] itemDiscounts = getItemTransactionDiscounts(lineItems);
        int totalLineItems = itemDiscounts.length;

        for (int index = 0; index < totalLineItems; index++)
        {
            if (itemDiscounts[index] == null)
            {
                continue;
            }
            for (ItemTransactionDiscountAuditIfc itemDiscount : itemDiscounts[index])
            {
                if (!aggregate(index, itemDiscount, transactionDiscountList))
                {
                    // Add a new transaction discount if this item discount does not belong to any existing transaction discount
                    TransactionDiscountAuditIfc transactionDiscount = new TransactionDiscountAudit();
                    transactionDiscount.initialize(totalLineItems, itemDiscount);
                    transactionDiscount.addItemDiscount(index, itemDiscount);
                    transactionDiscountList.add(transactionDiscount);
                }
            }

        }

        TransactionDiscountAuditIfc[] result = transactionDiscountList.toArray(new TransactionDiscountAuditIfc[0]);
        return result;
    }

    /**
     * Get all item transaction discounts of the line items
     * @param lineItems the sale return line items
     * @return a collection of item transaction discounts
     */
    @SuppressWarnings("unchecked")
    protected Collection<ItemTransactionDiscountAuditIfc>[] getItemTransactionDiscounts(SaleReturnLineItemIfc[] lineItems)
    {
        Collection<ItemTransactionDiscountAuditIfc>[] itemDiscounts = new Collection[lineItems.length];
        for (int i=0; i<lineItems.length; i++)
        {
            ItemDiscountStrategyIfc[] discounts = (ItemDiscountStrategyIfc[])lineItems[i].getTransactionDiscounts();
            if (discounts.length > 0)
            {
                itemDiscounts[i] = new ArrayList<ItemTransactionDiscountAuditIfc>();
                for (ItemDiscountStrategyIfc discount : discounts)
                {
                    itemDiscounts[i].add((ItemTransactionDiscountAuditIfc)discount);
                }
            }
        }

        return itemDiscounts;
    }

    /**
     * Add an item discount into a transaction discount that it belongs to.
     * @param index the line item index of the item discount
     * @param itemDiscount the item discount
     * @param transactionDiscounts an collection of transaction discounts
     * @return true if the item discount has been successfully added into a transaction discount;
     * false if it does not belong to any transaction discount.
     */
    protected boolean aggregate(int index, ItemTransactionDiscountAuditIfc itemDiscount, Collection<TransactionDiscountAuditIfc> transactionDiscounts)
    {
        boolean found = false;
        for (TransactionDiscountAuditIfc transactionDiscount : transactionDiscounts)
        {
            found = transactionDiscount.belongToTransactionDiscount(itemDiscount);
            if (found)
            {
                transactionDiscount.addItemDiscount(index, itemDiscount);
                break;
            }
        }

        return found;
    }

}


