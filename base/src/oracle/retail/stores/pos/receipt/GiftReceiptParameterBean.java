/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/GiftReceiptParameterBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:40 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   03/03/10 - prevent duplicate kit components from being added
 *    cgreene   02/11/10 - add kit components for kit headers that are gift
 *                         receipted
 *    abondala  01/03/10 - update header date
 *    asinton   09/30/09 - Removed damage discounted items from gift receipt.
 *    asinton   09/30/09 - XbranchMerge asinton_bug-8921672 from
 *                         rgbustores_13.1x_branch
 *    asinton   09/28/09 - Remove damage discount line items from
 *                         getSaleReturnLineItems method.
 *    cgreene   11/13/08 - configure print beans into Spring context
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         4/30/2007 7:00:39 PM   Alan N. Sinton  CR
 *       26485 - Merge from v12.0_temp.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;

/**
 * Parameter bean for Send Gift Receipt printable document.
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class GiftReceiptParameterBean extends ReceiptParameterBean implements GiftReceiptParameterBeanIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2635456951046699094L;

    /**
     * Price code.
     */
    protected String priceCode;

    /**
     * SaleReturnLineItemIfc reference.
     */
    protected SaleReturnLineItemIfc[] saleReturnLineItems;

    /**
     * Gift Receipt header.
     */
    protected String[] giftReceiptHeader;

    /**
     * Gift Receipt foooter.
     */
    protected String[] giftReceiptFooter;

    /**
     * Constructor.
     *
     */
    public GiftReceiptParameterBean()
    {
        super();
        // set the documentType to TYPE_RECEIPT_GIFT by default
        super.documentType = ReceiptTypeConstantsIfc.GIFT;
    }

    /**
     * Returns the price code for this SendGiftReceiptParameterBean.
     * @return The price code for this SendGiftReceiptParameterBean.
     * @see oracle.retail.stores.pos.receipt.GiftReceiptParameterBeanIfc#getPriceCode()
     */
    public String getPriceCode()
    {
        return this.priceCode;
    }

    /**
     * Returns the SaleReturnLineItem for this SendGiftReceiptParameterBean.
     * 
     * @return The SaleReturnLineItem for this SendGiftReceiptParameterBean.
     * @see oracle.retail.stores.pos.receipt.GiftReceiptParameterBeanIfc#getSaleReturnLineItem()
     */
    public SaleReturnLineItemIfc[] getSaleReturnLineItems()
    {
        return saleReturnLineItems;
    }

    /**
     * Sets the price code for this SendGiftReceiptParameterBean.
     *
     * @param code
     * @see oracle.retail.stores.pos.receipt.GiftReceiptParameterBeanIfc#setPriceCode(java.lang.String)
     */
    public void setPriceCode(String code)
    {
        this.priceCode = code;
    }

    /**
     * Sets the SaleReturnLineItem for this SendGiftReceiptParameterBean.
     * <p>
     * NOTE: The array passed in is modified to eliminate any line item where
     * {@link SaleReturnLineItemIfc#hasDamageDiscount()} is true. Also, any
     * {@link KitHeaderLineItemIfc} in the array will cause its list of
     * {@link KitComponentLineItemIfc}s to be added to the array.
     *
     * @param srLineItem
     * @see GiftReceiptParameterBeanIfc#setSaleReturnLineItem(oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc)
     */
    @SuppressWarnings("unchecked")
    public void setSaleReturnLineItems(SaleReturnLineItemIfc[] srLineItems)
    {
        if (srLineItems != null)
        {
            List<SaleReturnLineItemIfc> list = new ArrayList<SaleReturnLineItemIfc>(srLineItems.length);
            for (SaleReturnLineItemIfc item : srLineItems)
            {
                // if item does not have damage discount, then add to the return list
                if(!item.hasDamageDiscount())
                {
                    // don't add kit components, we'll add them after their header next
                    if (!(item instanceof KitComponentLineItemIfc))
                    {
                        list.add(item);
                    }

                    // check for kit header and add all components
                    if (item instanceof KitHeaderLineItemIfc)
                    {
                        Iterator iter = ((KitHeaderLineItemIfc)item).getKitComponentLineItems();
                        while (iter.hasNext())
                        {
                            list.add((SaleReturnLineItemIfc)iter.next());
                        }
                    }
                }
            }
            srLineItems = list.toArray(new SaleReturnLineItemIfc[list.size()]);
        }
        this.saleReturnLineItems = srLineItems;
    }

    /**
     * Returns the gift receipt footer.
     *
     * @return The gift receipt footer.
     * @see oracle.retail.stores.pos.receipt.GiftReceiptParameterBeanIfc#getGiftReceiptFooter()
     */
    public String[] getGiftReceiptFooter()
    {
        return this.giftReceiptFooter;
    }

    /**
     * Returns the gift receipt header.
     *
     * @return The gift receipt header.
     * @see oracle.retail.stores.pos.receipt.GiftReceiptParameterBeanIfc#getGiftReceiptHeader()
     */
    public String[] getGiftReceiptHeader()
    {
        return this.giftReceiptHeader;
    }

    /**
     * Sets the gift receipt footer.
     *
     * @param giftReceiptFooter
     * @see oracle.retail.stores.pos.receipt.GiftReceiptParameterBeanIfc#setGiftReceiptFooter(java.lang.String[])
     */
    public void setGiftReceiptFooter(String[] giftReceiptFooter)
    {
        this.giftReceiptFooter = giftReceiptFooter;
    }

    /**
     * Sets the gift receipt header.
     *
     * @param giftReceiptHeader
     * @see oracle.retail.stores.pos.receipt.GiftReceiptParameterBeanIfc#setGiftReceiptHeader(java.lang.String[])
     */
    public void setGiftReceiptHeader(String[] giftReceiptHeader)
    {
        this.giftReceiptHeader = giftReceiptHeader;
    }
}
