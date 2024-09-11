/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/TransactionDiscountAudit.java /main/2 2012/08/28 18:42:01 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         08/28/12 - compare code not the entire object
* sgu         08/27/12 - persist a TransactionDiscountAuditIfc discount rule
* sgu         08/23/12 - set original discount method
* sgu         08/23/12 - make sure that total discount amount can't be null.
* sgu         08/23/12 - create transaction discounts for a partial pickup
* sgu         08/23/12 - add support for transaction discount audit
* sgu         08/22/12 - add new files
* sgu         08/21/12 - add new file
* sgu         08/21/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.domain.discount;

import java.util.ArrayList;
import java.util.Collection;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.utility.Util;

public class TransactionDiscountAudit extends TransactionDiscountByAmountStrategy
implements TransactionDiscountAuditIfc
{
    /**
     * Serial Version ID
     */
    private static final long serialVersionUID = 1878462223783702620L;

    /**
     * This list stores item discounts for each sale return line item for this
     * transaction discount. Each element is a collection of item discounts
     * for line item of the given index.
     */
    private Collection<ItemTransactionDiscountAuditIfc>[] itemDiscounts;

    /**
     * The original discount method
     */
    protected int originalDiscountMethod = DISCOUNT_METHOD_AMOUNT;

    /**
     * Create a transaction discount audit
     */
    public TransactionDiscountAudit()
    {
        super();
        initialize(0, null);
    }

    /**
     * Initialize a transaction discount from an item transactional discount
     *
     * @param itemDiscount ItemTransactionDiscountAuditIfc
     */
    public void initialize(int totalLineItems, ItemTransactionDiscountAuditIfc itemDiscount)
    {
        setItemDiscounts(new Collection[totalLineItems]);
        if (itemDiscount != null)
        {
            setAccountingMethod(itemDiscount.getAccountingMethod());
            setAssignmentBasis(itemDiscount.getAssignmentBasis());
            setOriginalDiscountMethod(itemDiscount.getOriginalDiscountMethod());
            setDiscountEmployee(itemDiscount.getDiscountEmployee());
            setRuleID(itemDiscount.getRuleID());
            setReason(itemDiscount.getReason());
            setLocalizedNames(itemDiscount.getLocalizedNames());
            setDiscountRate(itemDiscount.getDiscountRate());
            setReferenceID(itemDiscount.getReferenceID());
            setReferenceIDCode(itemDiscount.getReferenceIDCode());
            setPromotionId(itemDiscount.getPromotionId());
        }
    }

    /**
     * Returns a boolean flag indicating if the item discount belongs to this transaction discount
     * @param itemDiscount the item discount
     * @return the boolean flag
     */
    public boolean belongToTransactionDiscount(ItemTransactionDiscountAuditIfc itemDiscount)
    {
        String reasonCode = null, anotherReasonCode = null;
        boolean result = false;

        LocalizedCodeIfc reason = this.getReason();
        if (reason != null)
        {
            reasonCode = reason.getCode();
        }
        LocalizedCodeIfc anotherReason = itemDiscount.getReason();
        if (anotherReason != null)
        {
            anotherReasonCode = anotherReason.getCode();
        }
        if ((this.getAccountingMethod() == itemDiscount.getAccountingMethod()) &&
            (this.getAssignmentBasis() == itemDiscount.getAssignmentBasis())   &&
            (this.getOriginalDiscountMethod() == itemDiscount.getOriginalDiscountMethod()) &&
            Util.isObjectEqual(this.getDiscountEmployeeID(), itemDiscount.getDiscountEmployeeID()) &&
            Util.isObjectEqual(this.getRuleID(), itemDiscount.getRuleID()) &&
            Util.isObjectEqual(reasonCode, anotherReasonCode) &&
            Util.isObjectEqual(this.getDiscountRate(), itemDiscount.getDiscountRate()) &&
            Util.isObjectEqual(this.getReferenceID(), itemDiscount.getReferenceID()) &&
            (this.getReferenceIDCode() == itemDiscount.getReferenceIDCode()) &&
            (this.getPromotionId() == itemDiscount.getPromotionId()))
        {
            result = true;
        }

        return result;
    }

    /**
     * Create a new transaction discount audit with only item discounts as specified by the indexes
     * @param indexes the item discount indexes
     * @return a new transaction discount
     */
    public TransactionDiscountAuditIfc createNewTransactionDiscount(int[] indexes)
    {
        TransactionDiscountAuditIfc newAudit = (TransactionDiscountAuditIfc)clone();

        Collection<ItemTransactionDiscountAuditIfc>[] itemDiscounts = newAudit.getItemDiscounts();
        Collection<ItemTransactionDiscountAuditIfc>[] newItemDiscounts = new Collection[indexes.length];
        for (int i=0; i<indexes.length; i++)
        {
            newItemDiscounts[i] = itemDiscounts[indexes[i]];
        }
        newAudit.setItemDiscounts(newItemDiscounts);

        return newAudit;

    }

    /**
     * Returns item discounts
     *
     * @return item discounts
     */
    public Collection<ItemTransactionDiscountAuditIfc>[] getItemDiscounts()
    {
        return itemDiscounts;
    }

    /**
     * Set item discounts
     *
     * @param itemDiscounts item discounts
     */
    public void setItemDiscounts(
            Collection<ItemTransactionDiscountAuditIfc>[] itemDiscounts)
    {
        this.itemDiscounts = itemDiscounts;
        calculateTotalDiscountAmount();
    }

    //---------------------------------------------------------------------
    /**
     * Add item discount at the specified line item index
     * @param index the line item index
     * @param itemDiscount the item discount
     */
    //---------------------------------------------------------------------
    public void addItemDiscount(int index, ItemTransactionDiscountAuditIfc itemDiscount)
    {
        Collection<ItemTransactionDiscountAuditIfc>[] itemDiscounts = getItemDiscounts();
        if (itemDiscounts[index] == null)
        {
            itemDiscounts[index] = new ArrayList<ItemTransactionDiscountAuditIfc>();
        }
        itemDiscounts[index].add(itemDiscount);
        setItemDiscounts(itemDiscounts);
    }

    //---------------------------------------------------------------------
    /**
        Clone this object. <P>
        @return generic object copy of this object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        TransactionDiscountAuditIfc
            newClass = new TransactionDiscountAudit();
        setCloneAttributes(newClass);
        return newClass;
    }

    //---------------------------------------------------------------------
    /**
            Sets attributes in clone. <P>
    @param newClass new instance of class
    **/
    //---------------------------------------------------------------------
    protected void setCloneAttributes(TransactionDiscountAuditIfc newClass)
    {
        super.setCloneAttributes(newClass);

        Collection<ItemTransactionDiscountAuditIfc>[] newItemDiscounts = new Collection[itemDiscounts.length];
        for (int i=0; i<itemDiscounts.length; i++)
        {
            if (itemDiscounts[i] != null)
            {
                newItemDiscounts[i] = new ArrayList<ItemTransactionDiscountAuditIfc>();
                for (ItemTransactionDiscountAuditIfc itemDiscunt : itemDiscounts[i])
                {
                    newItemDiscounts[i].add((ItemTransactionDiscountAuditIfc)itemDiscunt.clone());
                }
            }
        }
        newClass.setItemDiscounts(newItemDiscounts);
        newClass.setOriginalDiscountMethod(originalDiscountMethod);
    }

    /**
     * Recalculate the total discount amount
     */
    protected void calculateTotalDiscountAmount()
    {
        CurrencyIfc totalDiscount = DomainGateway.getBaseCurrencyInstance();
        Collection<ItemTransactionDiscountAuditIfc>[] itemDiscounts = getItemDiscounts();
        for (Collection<ItemTransactionDiscountAuditIfc> itemDiscountCol : itemDiscounts)
        {
            // This check is necessary. In case a transaction discount is not applied to
            // a line item, its item discount collection is null.
            if (itemDiscountCol == null)
                continue;
            for (ItemTransactionDiscountAuditIfc itemDiscount : itemDiscountCol)
            {
                totalDiscount = itemDiscount.getDiscountAmount().add(totalDiscount);
            }
        }
        setDiscountAmount(totalDiscount);
    }

    //---------------------------------------------------------------------
    /**
        Retrieves original discount method. <P>
        @return method original discount method
    **/
    //---------------------------------------------------------------------
    public int getOriginalDiscountMethod()
    {
        return originalDiscountMethod;
    }

    //---------------------------------------------------------------------
    /**
        Sets original discount method. <P>
        @param value original discount method
    **/
    //---------------------------------------------------------------------
    public void setOriginalDiscountMethod(int value)
    {
        originalDiscountMethod = value;
    }
}


