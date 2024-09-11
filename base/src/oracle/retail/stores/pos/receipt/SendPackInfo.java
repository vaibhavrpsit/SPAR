/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/SendPackInfo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:39 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;

/**
 * A container class for a {@link SendPackageLineItemIfc} and it related
 * {@link SaleReturnLineItemIfc}s.
 *
 * @author cgreene
 * @since 13.1
 */
public class SendPackInfo implements Serializable
{
    private static final long serialVersionUID = 9162742445463727128L;

    /** The send package being reported upon */
    private SendPackageLineItemIfc sendPackage;
    /** The items in the package being sent. */
    private List<SaleReturnLineItemIfc> itemsInPackage;
    /** The count of the send label being printed. */
    private int sendLabelCount;

    /**
     * Constructor
     *
     * @param sendPackage
     */
    public SendPackInfo(SendPackageLineItemIfc sendPackage, int sendLabelCount)
    {
        this(sendPackage, sendLabelCount, null);
    }

    /**
     * Constructor that calls {@link #applyLineItems(TenderableTransactionIfc)}.
     *
     * @param sendPackage
     * @param transaction
     */
    public SendPackInfo(SendPackageLineItemIfc sendPackage, int sendLabelCount, TenderableTransactionIfc transaction)
    {
        this.sendPackage = sendPackage;
        this.sendLabelCount = sendLabelCount;
        itemsInPackage = new ArrayList<SaleReturnLineItemIfc>();
        applyLineItems(transaction);
    }

    /**
     * For all the line items in the specified transaction, add the ones that
     * are being sent via this {@link #sendLabelCount} to the list
     * {@link #itemsInPackage}.
     * 
     * @param transaction
     */
    @SuppressWarnings("unchecked")
    protected void applyLineItems(TenderableTransactionIfc transaction)
    {
        for (Iterator iter = ((RetailTransactionIfc)transaction).getLineItemsIterator(); iter.hasNext();)
        {
            SaleReturnLineItemIfc shippingItem = (SaleReturnLineItemIfc)iter.next();
            if (shippingItem.getItemSendFlag() && shippingItem.getSendLabelCount() == sendLabelCount)
            {
                itemsInPackage.add(shippingItem);
            }
        }
    }

    /**
     * @return the sendPackage
     */
    public SendPackageLineItemIfc getSendPackage()
    {
        return sendPackage;
    }

    /**
     * @return the itemsInPackage
     */
    public SaleReturnLineItemIfc[] getItemsInPackage()
    {
        return itemsInPackage.toArray(new SaleReturnLineItemIfc[itemsInPackage.size()]);
    }

    /**
     * @return the sendLabelCount
     */
    public int getSendLabelCount()
    {
        return sendLabelCount;
    }

    /**
     * @param sendPackage the sendPackage to set
     */
    public void setSendPackage(SendPackageLineItemIfc sendPackage)
    {
        this.sendPackage = sendPackage;
    }

    /**
     * @param itemsInPackage the itemsInPackage to set
     */
    public void setItemsInPackage(List<SaleReturnLineItemIfc> itemsInPackage)
    {
        this.itemsInPackage = itemsInPackage;
    }

    /**
     * @param sendLabelCount the sendLabelCount to set
     */
    public void setSendLabelCount(int sendLabelCount)
    {
        this.sendLabelCount = sendLabelCount;
    }
}
