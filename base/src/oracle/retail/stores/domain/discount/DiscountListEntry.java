/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/DiscountListEntry.java /main/13 2013/10/07 14:43:42 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  10/04/13 - Forward port 17214966
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   03/10/09 - prevent NPE when checking discount description
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         11/15/2007 10:48:46 AM Christian Greene
 *         Belize merge - add support for Any/All sources/targets
 *    4    360Commerce 1.3         4/25/2007 10:01:01 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:27:45 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:58 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:36 PM  Robert Pearse
 *
 *   Revision 1.4  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:28  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:34:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:49:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:57:42   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:18:02   msg
 * Initial revision.
 *
 *    Rev 1.2   12 Oct 2001 11:21:42   pjf
 * Added support for resetting the amount counted when DiscountListEntry is used for amount threshold criteria.
 * Resolution for POS SCR-10: Advanced Pricing
 *
 *    Rev 1.1   08 Oct 2001 11:10:48   pjf
 * Added support for tracking threshold amounts, utility method for removing entries.  Modified to use composition rather than inheritance for storage (no longer extends hashmap.)
 * Resolution for POS SCR-10: Advanced Pricing
 *
 *    Rev 1.0   Sep 20 2001 16:12:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:36:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;

/**
 * A DiscountListEntry is used by a DiscountList to associate a quantity
 * required and a quantity achieved with a String value. This class can also be
 * used to associate a threshold amount and amount counted with a String value.
 * 
 * @see oracle.retail.stores.domain.discount.DiscountList
 * @version $Revision: /main/13 $
 */
public class DiscountListEntry implements DiscountListEntryIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 7105598833748027576L;

    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    int quantityRequired;

    List<DiscountItemIfc> discountItems;
    
    List<DiscountItemIfc> discountAltItems;

    CurrencyIfc regularPrice;

    CurrencyIfc amount;

    CurrencyIfc amountRequired;

    /**
     * Default constructor.
     */
    public DiscountListEntry()
    {
        this(0);
    }

    /**
     * Constructor initializes the entry with the quantity required.
     * 
     * @param quantityRequired indicates the number of times the quantity must
     *            be incremented to satisfy the criteria for this entry
     */
    public DiscountListEntry(int quantityRequired)
    {
        this.quantityRequired = quantityRequired;
        discountItems = new ArrayList<DiscountItemIfc>(quantityRequired);
        discountAltItems = new ArrayList<DiscountItemIfc>();
    }

    /**
     * Constructor initializes the entry with the amount required.
     * 
     * @param CurrencyIfc valueRequired indicates the threshold value that the
     *            amount counted must reach to satisfy the criteria for this
     *            entry
     */
    public DiscountListEntry(CurrencyIfc thresholdValue)
    {
        amountRequired = thresholdValue;
    }

    /**
     * Constructor initializes the entry with the quantity required.
     * 
     * @param int numRequired indicates the number of times the quantity must be
     *        incremented to satisfy the criteria for this entry
     */
    public DiscountListEntry(int quantityRequired, CurrencyIfc regularPrice)
    {
        this.quantityRequired = quantityRequired;
        this.regularPrice = regularPrice;
    }

    /**
     * Tests whether the target quantity has been achieved for this entry.
     * 
     * @return boolean true if the String is associated with an entry and has
     *         been incremented the required number of times, false otherwise
     */
    public boolean quantitySatisfied()
    {
        return (discountItems.size() >= quantityRequired);
    }

    /**
     * Returns the quantity attained for this entry.
     * 
     * @return int indicating the number of times the quantity has been
     *         incremented
     */
    public int getQuantity()
    {
        return discountItems.size();
    }

    /**
     * Returns the quantity required for this entry.
     * 
     * @return int indicating the number of times the quantity must be
     *         incremented to satisfy the criteria for this entry
     */
    public int getQuantityRequired()
    {
        return quantityRequired;
    }

    /**
     * Sets the quantity required for this entry.
     * 
     * @param int value indicating the number of times the quantity must be
     *        incremented to satisfy the criteria for this entry
     */
    public void setQuantityRequired(int value)
    {
        quantityRequired = value;
    }

    /**
     * Tests whether the threshold amount has been achieved for this entry.
     * 
     * @return boolean true if the String is associated with an entry and
     *         amountCounted has been incremented to be greater than or equal to
     *         the threshold amoumtRequired, false otherwise
     */
    public boolean amountSatisfied()
    {
        return (amount.compareTo(amountRequired) >= 0);
    }

    /**
     * Returns the currency amount counted for this entry.
     * 
     * @return CurrencyIfc indicating the current value counted for this entry
     */
    public CurrencyIfc getAmount()
    {
        return (CurrencyIfc)amount.clone();
    }

    /**
     * Returns the threshold amount for this entry.
     * 
     * @return CurrencyIfc indicating the value the amount must reach to satisfy
     *         the criteria for this entry
     */
    public CurrencyIfc getAmountRequired()
    {
        return (CurrencyIfc)amountRequired.clone();
    }

    /**
     * Sets the threshold amount for this entry.
     * 
     * @param CurrencyIfc value indicating the value the amount must reach to
     *            satisfy the criteria for this entry
     */
    public void setAmountRequired(CurrencyIfc value)
    {
        amountRequired = value;
    }

    /**
     * Returns the price required for this entry.
     * 
     * @return CurrencyIfc
     */
    public CurrencyIfc getRegularPrice()
    {
        return regularPrice;
    }

    /**
     * Sets the price required for this entry.
     * 
     * @param CurrencyIfc
     */
    public void setRegularPrice(CurrencyIfc regularPrice)
    {
        this.regularPrice = regularPrice;
    }

    /**
     * Increments the quantity attained for this entry by one.
     */
    public void incrementQuantity(DiscountItemIfc item)
    {
        discountItems.add(item);
    }
    
    /**
     * Increments the Alternate quantity till the quantity can be incremented.
     */
    public void incrementAltQuantity(DiscountItemIfc item)
    {
        discountAltItems.add(item);
        if (((discountAltItems.size() + discountItems.size()) % quantityRequired) == 0)
        {
            discountItems.addAll(discountAltItems);
            discountAltItems.clear();
        }
    }

    /**
     * Adds a currency value to the amount counted for this this entry.
     * 
     * @param amountToAdd - the currency value to add to the amount counted
     */
    public void addToAmount(CurrencyIfc amountToAdd)
    {
        amount = amount.add(amountToAdd);
    }

    /**
     * Resets the quantity attained for this entry to zero.
     */
    public void resetQuantity()
    {
        discountItems.clear();
        discountAltItems.clear();
    }

    /**
     * Resets the amount counted for this entry to default currency value.
     */
    public void resetAmount()
    {
        amount = DomainGateway.getBaseCurrencyInstance();
    }

    /**
     * Returns the total price for all the items that are in this entry.
     * 
     * @return CurrencyIfc indicating the total selling price
     */
    public CurrencyIfc getTotalPrice()
    {
       CurrencyIfc totalSellingPrice = DomainGateway.getBaseCurrencyInstance();
       // iterate over the entries and sum the amount
       for (DiscountItemIfc item : discountItems)
       {
           totalSellingPrice = totalSellingPrice.add(item.getExtendedSellingPrice());
       }
       return totalSellingPrice;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.DiscountListEntryIfc#getDiscountItems()
     */
    public List<DiscountItemIfc> getDiscountItems()
    {
        return discountItems;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean equal = false;

        if (obj instanceof DiscountListEntry)
        {
            DiscountListEntry entry = (DiscountListEntry) obj;

            if (discountItems.size() == entry.discountItems.size() &&
                   discountAltItems.size() == entry.discountAltItems.size() && 
                   quantityRequired == entry.quantityRequired &&
                   Util.isObjectEqual(regularPrice,entry.regularPrice) &&
                   Util.isObjectEqual(amount,entry.amount) &&
                   Util.isObjectEqual(amountRequired,entry.amountRequired))
            {
                equal = true;
            }

        }

        return equal;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object clone()
    {
        DiscountListEntry newEntry = new DiscountListEntry();

        newEntry.discountItems = (List<DiscountItemIfc>)((ArrayList<DiscountItemIfc>)discountItems).clone();
        newEntry.discountAltItems = (List<DiscountItemIfc>)((ArrayList<DiscountItemIfc>)discountAltItems).clone();
        newEntry.quantityRequired = quantityRequired;
        if (regularPrice != null)
        {
            newEntry.regularPrice = (CurrencyIfc)regularPrice.clone();
        }
        if (amount != null)
        {
            newEntry.amount = (CurrencyIfc)amount.clone();
        }
        if (amountRequired != null)
        {
            newEntry.amountRequired = (CurrencyIfc)amountRequired.clone();
        }
        return newEntry;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // result string
        StringBuilder strResult = new StringBuilder("Class:  ");
        strResult.append(getClass().getName() + " (Revision ")
                 .append(revisionNumber)
                 .append(") @").append(hashCode())
                 .append(Util.EOL)
                 .append("\tquantity: " + discountItems.size() + Util.EOL)
                 .append("\tquantityRequired: " + quantityRequired + Util.EOL)
                 .append("\tregularPrice: " + regularPrice + Util.EOL)
                 .append("\tamount: " + amount + Util.EOL)
                 .append("\tamountRequired: " + amountRequired + Util.EOL);

        return(strResult.toString());
    }
}