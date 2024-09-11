/* ===========================================================================
* Copyright (c) 2007, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/PromotionLineItem.java /main/12 2012/12/10 19:13:59 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    cgreene   12/01/10 - implement saving applied promotion names into
 *                         tr_ltm_prm table
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   04/14/09 - convert pricingGroupID to integer instead of string
 *    npoola    11/20/08 - added Util.isObjectEquals method for the business
 *                         date and timestamps causing nullpointer exception
 *    npoola    11/30/08 - CSP POS and BO changes
 *
 * ===========================================================================
 * $Log:
 *  2    360Commerce 1.1         6/13/2007 4:47:57 PM   Christian Greene bulk
 *       price change checkin
 *  1    360Commerce 1.0         5/18/2007 10:54:50 AM  Maisa De Camargo
 *       Temporary Price Change Class. It represents the table TR_LTM_PRM
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

import java.io.Serializable;
import java.util.Locale;

import org.apache.commons.lang3.builder.ToStringBuilder;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;

/**
 * This interface represents a line item component of a RetailTransaction that
 * records the RMS promotion identifiers of a discount to one or more
 * RetailTransactionLineItems
 */
public class PromotionLineItem implements PromotionLineItemIfc, Serializable
{
    /** Generated Serial Version UID */
    private static final long serialVersionUID = -4874949965040588371L;

    /** The current store id */
    protected String storeId = "";

    /** Workstation id */
    protected String workstationId = "";

    /** Business Date of this Promotion Line Item */
    protected EYSDate businessDate = null;

    /** Transaction in which this promotion line item was applied */
    protected long transactionSequenceNumber = 0;

    /** Transaction Sale Line Item Sequence Number */
    protected int transactionLineItemSequenceNumber = 0;

    /** The promotion Line Item Sequence Number */
    protected int promotionLineItemSequenceNumber = 0;

    /** Promotion Id */
    protected long promotionId = 0;

    /** Promotion Component Id */
    protected long promotionComponentId = 0;

    /** Promotion Component Detail Id */
    protected long promotionComponentDetailId = 0;

    /** Discount Amount */
    protected CurrencyIfc discountAmount = null;

    /** Record Creation Timestamp */
    protected EYSDate creationTimestamp = null;

    /** Record Last Modified Timestamp */
    protected EYSDate lastModifiedTimestamp = null;

    /** Promotion Type */
    protected int promotionType = PROMOTION_TYPE_UNDEFINED;

    /** Pricing Group Id */
    protected int pricingGroupID;

    /** The locale in which the promotion name was printed on the receipt. */
    protected Locale receiptLocale;

    /** The name of the promotion which was printed on the receipt. */
    protected String promotionName;

    /**
     * Gets the StoreId
     *
     * @return
     */
    public String getStoreId()
    {
        return storeId;
    }

    /**
     * Sets the storeId
     *
     * @param storeId
     */
    public void setStoreId(String storeId)
    {
        this.storeId = storeId;

    }

    /**
     * Gets the workstationId
     *
     * @return
     */
    public String getWorkstationId()
    {
        return workstationId;
    }

    /**
     * Sets the workstationId
     *
     * @param workstationId
     */
    public void setWorkstationId(String workstationId)
    {
        this.workstationId = workstationId;
    }

    /**
     * Gets the businessDate
     *
     * @return
     */
    public EYSDate getBusinessDate()
    {
        return businessDate;
    }

    /**
     * Sets the businessDate
     *
     * @param businessDay
     */
    public void setBusinessDate(EYSDate businessDate)
    {
        this.businessDate = businessDate;
    }

    /**
     * Gets the transactionSequenceNumber
     *
     * @return
     */
    public long getTransactionSequenceNumber()
    {
        return transactionSequenceNumber;
    }

    /**
     * Sets the transactionSequenceNumber
     *
     * @param transactionSequenceNumber
     */
    public void setTransactionSequenceNumber(long transactionSequenceNumber)
    {
        this.transactionSequenceNumber = transactionSequenceNumber;
    }

    /**
     * Gets the transactionLineItemSequenceNumber
     *
     * @return
     */
    public int getTransactionLineItemSequenceNumber()
    {
        return transactionLineItemSequenceNumber;
    }

    /**
     * Sets the transactionLineItemSequenceNumber
     *
     * @param transactionLineItemSequenceNumber
     */
    public void setTransactionLineItemSequenceNumber(int transactionLineItemSequenceNumber)
    {
        this.transactionLineItemSequenceNumber = transactionLineItemSequenceNumber;
    }

    /**
     * Gets the promotionLineItemSequenceNumber
     *
     * @return
     */
    public int getPromotionLineItemSequenceNumber()
    {
        return promotionLineItemSequenceNumber;
    }

    /**
     * Sets the promotionLineItemSequenceNumber
     *
     * @param promotionLineItemSequenceNumber
     */
    public void setPromotionLineItemSequenceNumber(int promotionLineItemSequenceNumber)
    {
        this.promotionLineItemSequenceNumber = promotionLineItemSequenceNumber;
    }

    /**
     * Gets the promotionId
     *
     * @return
     */
    public long getPromotionId()
    {
        return promotionId;
    }

    /**
     * Sets the promotionId
     *
     * @param promotionId
     */
    public void setPromotionId(long promotionId)
    {
        this.promotionId = promotionId;
    }

    /**
     * Gets the promotionComponentId
     *
     * @return
     */
    public long getPromotionComponentId()
    {
        return promotionComponentId;
    }

    /**
     * Sets the promotionComponentId
     *
     * @param promotionComponentId
     */
    public void setPromotionComponentId(long promotionComponentId)
    {
        this.promotionComponentId = promotionComponentId;
    }

    /**
     * Gets the promotionComponentDetailId
     *
     * @return
     */
    public long getPromotionComponentDetailId()
    {
        return promotionComponentDetailId;
    }

    /**
     * Sets the promotionComponentDetailId
     *
     * @param promotionComponentDetailId
     */
    public void setPromotionComponentDetailId(long promotionComponentDetailId)
    {
        this.promotionComponentDetailId = promotionComponentDetailId;
    }

    /**
     * Gets the discountAmount
     *
     * @return
     */
    public CurrencyIfc getDiscountAmount()
    {
        return discountAmount;
    }

    /**
     * Sets the discountAmount
     *
     * @param discountAmount
     */
    public void setDiscountAmount(CurrencyIfc discountAmount)
    {
        this.discountAmount = discountAmount;
    }

    /**
     * Gets the creationTimestamp
     *
     * @return
     */
    public EYSDate getCreationTimestamp()
    {
        return creationTimestamp;
    }

    /**
     * Sets the creationTimestamp
     *
     * @param creationTimestamp
     */
    public void setCreationTimestamp(EYSDate creationTimestamp)
    {
        this.creationTimestamp = creationTimestamp;
    }

    /**
     * Gets the lastModifiedTimestamp
     *
     * @return
     */
    public EYSDate getLastModifiedTimestamp()
    {
        return lastModifiedTimestamp;
    }

    /**
     * Sets the lastModifiedTimestamp
     *
     * @param lastModifiedTimestamp
     */
    public void setLastModifiedTimestamp(EYSDate lastModifiedTimestamp)
    {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.PromotionLineItemIfc#isTypePriceChange()
     */
    public boolean isTypePriceChange()
    {
        return (this.promotionType == PROMOTION_TYPE_PRICE_CHANGE);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.PromotionLineItemIfc#setPromotionType(int)
     */
    public void setPromotionType(int promotionType)
    {
        this.promotionType = promotionType;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.PromotionLineItemIfc#getPromotionType()
     */
    public int getPromotionType()
    {
        return this.promotionType;
    }

    /**
     * Set the PricingGroupID
     * @param pricingGroupID
     */
    public void setPricingGroupID(int pricingGroupID)
    {
        this.pricingGroupID = pricingGroupID;
    }

    /**
     * Get the PricingGroupID.
     * @return int pricingGroupID
     */
    public int getPricingGroupID()
    {
        return this.pricingGroupID;
    }

    /**
     * @return the receiptLocale
     */
    public Locale getReceiptLocale()
    {
        return receiptLocale;
    }

    /**
     * @param receiptLocale the receiptLocale to set
     */
    public void setReceiptLocale(Locale receiptLocale)
    {
        this.receiptLocale = receiptLocale;
    }

    /**
     * @return the promotionName
     */
    public String getPromotionName()
    {
        return promotionName;
    }

    /**
     * @param promotionName the promotionName to set
     */
    public void setPromotionName(String promotionName)
    {
        this.promotionName = promotionName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        if (obj instanceof PromotionLineItem)
        {
            PromotionLineItem other = (PromotionLineItem) obj;
            isEqual = getStoreId().equals(other.getStoreId())
                    && getWorkstationId().equals(other.getWorkstationId())
                    && Util.isObjectEqual(getBusinessDate(), other.getBusinessDate())
                    && getTransactionSequenceNumber() == other.getTransactionSequenceNumber()
                    && getTransactionLineItemSequenceNumber() == other.getTransactionLineItemSequenceNumber()
                    && getPromotionLineItemSequenceNumber() == other.getPromotionLineItemSequenceNumber()
                    && getPromotionId() == other.getPromotionId()
                    && getPromotionComponentId() == other.getPromotionComponentId()
                    && getPromotionComponentDetailId() == other.getPromotionComponentDetailId()
                    && getDiscountAmount() == other.getDiscountAmount()
                    && Util.isObjectEqual(getCreationTimestamp(), other.getCreationTimestamp())
                    && Util.isObjectEqual(getLastModifiedTimestamp(), other.getLastModifiedTimestamp())
                    && Util.isObjectEqual(getReceiptLocale(), other.getReceiptLocale())
                    && Util.isObjectEqual(getPromotionName(), other.getPromotionName());
        }

        return isEqual;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("storeId", getStoreId());
        builder.append("workstationId", getWorkstationId());
        builder.append("businessDate", getBusinessDate());
        builder.append("transactionSequenceNumber", getTransactionSequenceNumber());
        builder.append("transactionLineItemSequenceNumber", getTransactionLineItemSequenceNumber());
        builder.append("promotionLineItemSequenceNumber", getPromotionLineItemSequenceNumber());
        builder.append("promotionId", getPromotionId());
        builder.append("promotionComponentId", getPromotionComponentId());
        builder.append("promotionComponentDetailId", getPromotionComponentDetailId());
        builder.append("discountAmount", getDiscountAmount());
        builder.append("creationTimestamp", getCreationTimestamp());
        builder.append("lastModifiedTimestamp", getLastModifiedTimestamp());
        builder.append("promotionType", getPromotionType());
        builder.append("pricingGroupID", getPricingGroupID());
        builder.append("receiptLocale", getReceiptLocale());
        builder.append("promotionName", getPromotionName());
        return builder.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        PromotionLineItemIfc promotionLineItem = DomainGateway.getFactory().getPromotionLineItemInstance();
        setCloneAttributes(promotionLineItem);
        return promotionLineItem;
    }

    /**
     * Sets attributes for clone.
     *
     * @param newClass instance of new object
     */
    protected void setCloneAttributes(PromotionLineItemIfc newClass)
    {
        newClass.setStoreId(getStoreId());
        newClass.setWorkstationId(getWorkstationId());
        if (getBusinessDate() != null)
            newClass.setBusinessDate((EYSDate)getBusinessDate().clone());
        newClass.setTransactionSequenceNumber(getTransactionSequenceNumber());
        newClass.setTransactionLineItemSequenceNumber(getTransactionLineItemSequenceNumber());
        newClass.setPromotionLineItemSequenceNumber(getPromotionLineItemSequenceNumber());
        newClass.setPromotionId(getPromotionId());
        newClass.setPromotionComponentId(getPromotionComponentId());
        newClass.setPromotionComponentDetailId(getPromotionComponentDetailId());
        newClass.setPromotionType(getPromotionType());
        newClass.setPricingGroupID(getPricingGroupID());
        if (getDiscountAmount() != null)
            newClass.setDiscountAmount((CurrencyIfc)getDiscountAmount().clone());
        if (getCreationTimestamp() != null)
            newClass.setCreationTimestamp((EYSDate)getCreationTimestamp().clone());
        if (getLastModifiedTimestamp() != null)
            newClass.setLastModifiedTimestamp((EYSDate)getLastModifiedTimestamp().clone());
        newClass.setReceiptLocale(getReceiptLocale());
        newClass.setPromotionName(getPromotionName());
    }
}
