/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/DiscountList.java /main/30 2014/01/16 10:53:45 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  01/16/14 - added multithreshold attribute to clone and equals
 *                         methods
 *    tksharma  12/13/13 - modifed the evaluateAnyComboAmountSatisifed method
 *                         to select the bestPosibble combination of items as
 *                         candidates
 *    tksharma  10/04/13 - Forward port 17214966
 *    tksharma  10/04/13 - corrected evaluateAnyComboAmountSatisfied method to
 *                         break when the amount is met
 *    rabhawsa  09/10/13 - multi-threshold item entries should be sorted.
 *    rabhawsa  08/27/13 - multi-threshold quantitySatisfied should not be
 *                         dependent on criteria.
 *    rabhawsa  08/27/13 - multi-threshold amountSatisfied should not be
 *                         dependent on criteria.
 *    tksharma  07/30/13 - corrected evaluateAnyAmountSatisifed method. This is
 *                         the new name to anyOneAmountSatisfied.
 *    tksharma  06/25/13 - added implementation for anyOneAmountSatisfied() and
 *                         anyComboAmountSatisfied() to support multithreshold
 *                         on amount based rules
 *    tksharma  05/21/13 - added implementation for methods anyComboSatisfied
 *                         and isAnyComboAcceptable
 *    tksharma  08/02/12 - multithreshold-merge with sthallam code
 *    sthallam  07/10/12 - Enhanced RPM Integration - Multithreshold discount
 *                         rules
 *    mchellap  08/11/11 - BUG#12623177 Added support for Equal or Lesser Value
 *                         (EOLV)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    vapartha  02/19/10 - Added code to handle discount rules when the source
 *                         and target is a class.
 *    abondala  01/03/10 - update header date
 *    asinton   12/18/09 - Added null checks in the equals method for non
 *                         primative class members.
 *    cgreene   10/29/09 - XbranchMerge cgreene_nonreceiptreturns from
 *                         rgbustores_13.1x_branch
 *    cgreene   10/27/09 - added method isAllRequired
 *    cgreene   03/10/09 - prevent NPE when checking discount description
 *    lslepeti  11/07/08 - add description to clone method
 *    lslepeti  11/05/08 - add rules of type BuyNorMoreOfXforZ%off and
 *                         BuyNorMoreOfXforZ$each
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
 *   Revision 1.5  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.4  2004/07/09 18:39:18  aachinfiev
 *   @scr 6082 - Replacing "new" with DomainObjectFactory.
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
 *    Rev 1.0   Aug 29 2003 15:34:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 20 2003 09:26:28   jgs
 * Changes due to code reveiw.
 * Resolution for 103: New Advanced Pricing Features
 * 
 *    Rev 1.1   Jan 20 2003 11:50:14   jgs
 * Added allow repeating sources, deal distribution, and percent off lowest priced Item to Advanced Pricing Rule processing.
 * Resolution for 103: New Advanced Pricing Features
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
 *    Rev 1.12   06 Mar 2002 18:43:38   pjf
 * Pricing performance enhancements.
 * Resolution for POS SCR-117: Fixed price discounts are not in parentheses on returns
 *
 *    Rev 1.11   05 Mar 2002 14:32:20   KAC
 * Modified attributesEqual(), so that it doesn't call
 * item.getComparator() needlessly.
 * Resolution for Domain SCR-40: Enhance performance
 *
 *    Rev 1.10   25 Feb 2002 17:11:40   pjf
 * Override equals
 * Resolution for POS SCR-954: Domain - Arts Translation
 *
 *    Rev 1.9   27 Dec 2001 09:03:02   pjf
 * Added setCloneAttributes method.
 * Resolution for POS SCR-245: Domain Refactoring
 *
 *    Rev 1.8   21 Dec 2001 14:23:06   pjf
 * Refactored advanced pricing classes.
 * Resolution for POS SCR-245: Domain Refactoring
 *
 *    Rev 1.7   24 Oct 2001 15:58:04   adc
 * Added method : getTotalPrice() for use by Online Office
 * Resolution for Backoffice SCR-7: F1001
 *
 *    Rev 1.6   23 Oct 2001 10:57:22   adc
 * Added getTotalAmountRequired method, for use by OnlineOffice
 * Resolution for Backoffice SCR-7: F1001
 *
 *    Rev 1.5   19 Oct 2001 11:25:06   pjf
 * Added methods getAmount(String id) and getAmountRequired(String id)
 * Resolution for POS SCR-10: Advanced Pricing
 *
 *    Rev 1.4   12 Oct 2001 11:21:42   pjf
 * Added support for resetting the amount counted when DiscountListEntry is used for amount threshold criteria.
 * Resolution for POS SCR-10: Advanced Pricing
 *
 *    Rev 1.3   08 Oct 2001 13:13:30   pjf
 * Added criteriaArray() method to return the set of keys as an Object[]
 * Resolution for POS SCR-10: Advanced Pricing
 *
 *    Rev 1.2   08 Oct 2001 11:34:32   pjf
 * Added isEmpty() method.
 * Resolution for POS SCR-10: Advanced Pricing
 *
 *    Rev 1.0   Sep 20 2001 16:12:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:36:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;

/**
 * A DiscountList associates a set of criteria (keys) with a quantity required
 * and quantity achieved (entries.) The itemLimit and itemThreshold values set
 * bounds for a single item price to be used in the evaluation. The
 * thresholdTypeCode indicates whether total item quantities or an aggregate
 * threshold amount is used as criteria for a rule.
 * 
 * @see oracle.retail.stores.domain.discount.DiscountListEntry
 * @version $Revision: /main/30 $
 */
public class DiscountList implements DiscountListIfc, DiscountRuleConstantsIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -3617219081319730881L;

    /** revision number supplied by source-code control system */
    public static final String revisionNumber = "$Revision: /main/30 $";

    /**
     * Collection that maps a set of string criteria to a set of
     * DiscountListEntries containing associated data.
     */
    protected Map<String, DiscountListEntryIfc> map = new HashMap<String, DiscountListEntryIfc>();

    /**
     * the basis of comparison for satisfying the source criteria for this rule
     */
    protected int comparisonBasis = COMPARISON_BASIS_UNINITIALIZED;

    /**
     * threshold amount - an item's pre-discounted prices must be >= this
     * threshold in order to be considered by the evaluate method
     */
    protected CurrencyIfc itemThreshold = null;

    /**
     * limit amount - an items pre-discounted price must be < this limit in
     * order to be considered by the evaluate method. The default value of -1
     * indicates that the item price for this rule is not limited
     */
    protected CurrencyIfc itemLimit = null;

    /**
     * threshold type - indicates whether to evaluate criteria by aggregate $
     * amount or by item quantity
     */
    protected int thresholdType = THRESHOLD_QUANTITY;

    /**
     * Any quantity. If greater than zero, indicates how many of the items in
     * this list must be present for this criteria to be met as opposed to
     * requiring all of them.
     * <p>
     * Not related to the individual item entries actual quantity requirement.
     * {@link DiscountListEntry#quantityRequired}
     * 
     * @see #allSatisfied()
     */
    protected int anyQuantity;

    /**
     * Reason code of rule owning this discount list. Useful to determine in
     * what order to sort items.
     */
    protected int ruleReasonCode;

    /**
     * Description of rule owning this discount list. Useful to determine in
     * what order to sort items.
     */
    protected String description;
    
    /**
     * Indicator used to specify rule is of type multithreshold.
     */
    protected boolean isMultiThreshold;

    /**
     * Constructor initializes threshold/limit values.
     */
    public DiscountList()
    {
        // no initial threshold
        itemThreshold = DomainGateway.getBaseCurrencyInstance();

        // no initial limit
        itemLimit = DomainGateway.getBaseCurrencyInstance("-1.0");
    }

    /**
     * Returns an iterator over the criterion strings for this list.
     * 
     * @return Iterator
     */
    public Iterator<String> criteria()
    {
        return map.keySet().iterator();
    }

    /**
     * Returns an array containing the criteria strings (keys) for this list.
     * 
     * @return Object[]
     */
    public Object[] criteriaArray()
    {
        return map.keySet().toArray();
    }

    /**
     * Clears all entries from the list.
     */
    public void clear()
    {
        map.clear();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        DiscountList newList = new DiscountList();
        setCloneAttributes(newList);
        return newList;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof DiscountList)
        {
            DiscountList list = (DiscountList)o;

            //test list attributes
            if (anyQuantity == list.anyQuantity &&
                isMultiThreshold == list.isMultiThreshold &&
                comparisonBasis == list.comparisonBasis &&
                ruleReasonCode == list.ruleReasonCode &&
                thresholdType == list.thresholdType &&
                Util.isObjectEqual(itemLimit, list.itemLimit) &&
                Util.isObjectEqual(itemThreshold, list.itemThreshold) &&
                Util.isObjectEqual(description, list.description))
            {
                //ensure all mappings are present and entries are equivalent
                for (Iterator<String> i = list.criteria(); i.hasNext();)
                {
                    String key = i.next();
                    if (! containsEntry(key) ||
                        ! getEntry(key).equals(list.getEntry(key)))
                    {
                        return false;
                    }
                }
                return true; // all attributes and mappings matched
            }
        }
        return false;
    }

    /**
     * Sets the attributes for a clone of this list.
     * 
     * @param uninitialized DiscountList object
     */
    protected void setCloneAttributes(DiscountList newList)
    {
        newList.anyQuantity = anyQuantity;
        newList.isMultiThreshold = isMultiThreshold;
        newList.comparisonBasis = comparisonBasis;
        newList.itemThreshold   = (CurrencyIfc)itemThreshold.clone();
        newList.itemLimit       = (CurrencyIfc)itemLimit.clone();
        newList.ruleReasonCode  = ruleReasonCode;
        newList.thresholdType   = thresholdType;
        newList.description     = description;
        for (Iterator<String> i = criteria(); i.hasNext(); )
        {
            String s = i.next();
            newList.map.put(s, (DiscountListEntry)getEntry(s).clone());
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // result string
        StringBuffer strResult = new StringBuffer("Class:  ");
        strResult.append(getClass().getName() + " (Revision ")
                 .append(revisionNumber)
                 .append(") @").append(hashCode())
                 .append("\n")
                 .append(map.toString());

        return strResult.toString();
    }

    /**
     * Creates a DiscountListEntry and adds it to this discount list.
     * 
     * @param String id - the String (criterion) to associate with the
     *            DiscountListEntry (maintains quantity attained, quantity
     *            required)
     * @param int quantityRequired - the quantity required in order to satisfy a
     *        criterion
     */
    public void addEntry(String id, int quantityRequired)
    {
        DiscountListEntryIfc entry = DomainGateway.getFactory().getDiscountListEntryInstance();
        entry.setQuantityRequired(quantityRequired);
        map.put(id, entry);
    }

    /**
     * Creates a DiscountListEntry and adds it to this discount list.
     * 
     * @param String id - the String (criterion) to associate with the
     *            DiscountListEntry (maintains quantity attained, quantity
     *            required)
     * @param CurrencyIfc amountRequired - the currency amount required in order
     *            to satisfy a criterion
     */
    public void addEntry(String id, CurrencyIfc amountRequired)
    {
        DiscountListEntryIfc entry = DomainGateway.getFactory().getDiscountListEntryInstance();
        entry.setAmountRequired(amountRequired);
        map.put(id, entry);
    }

    /**
     * Creates a DiscountListEntry and adds it to the discount list. Used only
     * in StoreCentral.
     * 
     * @param String id - the String (criterion) to associate with the
     *            DiscountListEntry (maintains quantity, price)
     * @param int quantityRequired - the quantity required in order to satisfy a
     *        criterion
     * @param CurrencyIfc - the price of the source identified by the id
     * @deprecated as of 13.1 due to no usage. No replacement. 
     */
    public void addEntry(String id, int quantityRequired, CurrencyIfc price)
    {
        DiscountListEntryIfc entry = DomainGateway.getFactory().getDiscountListEntryInstance();
        entry.setQuantityRequired(quantityRequired);
        entry.setRegularPrice(price);
        map.put(id, entry);
    }

    /**
     * Tests whether this list contains a DiscountListEntry for a given String.
     * 
     * @param String id - the String to test
     * @return boolean true if the String is associated with an entry, false
     *         otherwise
     */
    public boolean containsEntry(String id)
    {
        return map.containsKey(id);
    }

    /**
     * Removes a DiscountListEntry from this list.
     * 
     * @param String id - the String associated with the DiscountListEntry to
     *            remove
     */
    public void removeEntry(String id)
    {
        map.remove(id);
    }

    /**
     * Tests whether this list contains any DiscountListEntries.
     * 
     * @return boolean true if the list contains entries, false otherwise
     */
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.DiscountListIfc#isAllRequired()
     */
    public boolean isAllRequired()
    {
        return (anyQuantity == 0);        
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.DiscountListIfc#isAnyComboAcceptable()
     */
    @Override
    public boolean isAnyComboAcceptable()
    {
        return (anyQuantity == -1);
    }

    /**
     * Returns the comparison basis constant for this list.
     * 
     * @return int comparison basis constant
     * @see DiscountRuleConstantsIfc
     */
    public int getComparisonBasis()
    {
        return comparisonBasis;
    }

    /**
     * Sets the comparison basis value for this list.
     * 
     * @param int comparison basis constant
     * @see DiscountRuleConstantsIfc
     */
    public void setComparisonBasis(int basis)
    {
        comparisonBasis = basis;
    }

    /**
     * Sets the any quantity value for this list.
     * 
     * @param qty any quantity
     */
    public void setAnyQuantity(int qty)
    {
        anyQuantity = qty;
    }

    /**
     * Returns the any quantity for this list.
     * 
     * @return any quantity
     */
    public int getAnyQuantity()
    {
        return anyQuantity;
    }

    /**
     * Sets the ruleReasonCode value for this list.
     * 
     * @param ruleReasonCode
     */
    public void setRuleReasonCode(int ruleReasonCode)
    {
        this.ruleReasonCode = ruleReasonCode;
    }

    /**
     * Returns the ruleReasonCode for this list.
     * 
     * @return ruleReasonCode
     */
    public int getRuleReasonCode()
    {
        return ruleReasonCode;
    }

    /**
     * Returns the description for this list.
     * 
     * @return description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the description for this list.
     * 
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Returns the item threshold for this list. An item whose selling price is
     * below the threshold will not satisfy the criteria maintained by the list.
     * 
     * @return CurrencyIfc threshold
     */
    public CurrencyIfc getItemThreshold()
    {
        return itemThreshold;
    }

    /**
     * Sets the item threshold for this list. An item whose selling price is
     * below the threshold will not satisfy the criteria maintained by the list.
     * 
     * @param CurrencyIfc threshold
     */
    public void setItemThreshold(CurrencyIfc threshold)
    {
        itemThreshold = threshold;
    }

    /**
     * Returns the item limit for this list. An item whose selling price is
     * above this limit will not satisfy the criteria maintained by the list.
     * 
     * @return CurrencyIfc
     */
    public CurrencyIfc getItemLimit()
    {
        return itemLimit;
    }

    /**
     * Sets the item limit for this list. An item whose selling price is above
     * this limit will not satisfy the criteria maintained by the list.
     * 
     * @param CurrencyIfc limit
     */
    public void setItemLimit(CurrencyIfc limit)
    {
        itemLimit = limit;
    }

    /**
     * Retrieves the threshold type code for the list. This value is used to
     * indicate whether item quantities or aggregate selling amounts are to be
     * used to test for satisfying the criteria of a discount rule.
     * 
     * @return threshold type code of discount rule
     */
    public int getThresholdType()
    {
        return (thresholdType);
    }

    /**
     * Sets thresholdTypeCode of an advanced pricing rule.
     * 
     * @param value threshold type code of advanced pricing rule
     */
    public void setThresholdType(int value)
    {
        thresholdType = value;
    }    

    /**
     * @return the isMultiThreshold
     */
    public boolean isMultiThreshold()
    {
        return isMultiThreshold;
    }

    /**
     * @param isMultiThreshold the isMultiThreshold to set
     */
    public void setMultiThreshold(boolean isMultiThreshold)
    {
        this.isMultiThreshold = isMultiThreshold;
    }

    /**
     * Returns a collection of keys. Extract the code from the list. Used only
     * by StoreCentral.
     * 
     * @returnVector
     * @deprecated as of 13.1 due to no usage. No replacement. 
     */
    public Vector<String> keyVector()
    {
        String code = "";
        Vector<String> codes = new Vector<String>();

        for (Iterator<String> i = map.keySet().iterator(); i.hasNext();)
        {
            // the concatCode is in format CodeId - Name
            // truncate the string and get only the code
            String concatCode = i.next();

            int endIndex = concatCode.indexOf("-");
            if (endIndex != -1)
            {
                code = concatCode.substring(0, (endIndex - 1));
            }
            else
            {
                code = concatCode;
            }

            codes.add(code);

        }

        return codes;
    }

    /**
     * Tests whether this list contains a DiscountListEntry for a given String.
     * 
     * @param String id - the String to test
     * @return boolean true if the String is associated with an entry, false
     *         otherwise
     */
    public DiscountListEntryIfc getEntry(String id)
    {
        return map.get(id);
    }

    /**
     * Increments the quantity achieved for a given String.
     * 
     * @param String id - the String to increment
     * @return boolean true if the String is associated with an entry, false
     *         otherwise
     */
    public boolean incrementQuantity(String id, DiscountItemIfc item)
    {
        if (containsEntry(id))
        {
            DiscountListEntryIfc sle = map.get(id);
            sle.incrementQuantity(item);
            return true;
        }
        return false;
    }
    
    /**
     * Increments the alternate quantity achieved for a given String, once the required
     * any quantity is met, the quantity will be incremented as well
     * 
     * @param String id - the String to increment
     * @return boolean true if the String is associated with an entry, false
     *         otherwise
     */
    public boolean incrementAltQuantity(String id, DiscountItemIfc item)
    {
        if (containsEntry(id))
        {
            DiscountListEntryIfc sle = map.get(id);
            sle.incrementAltQuantity(item);
            return true;
        }
        return false;
    } 

    /**
     * Adds a currency value to the amount counted for a given String.
     * 
     * @param String id - the String to increment
     * @param CurrencyIfc amountToAdd - the currency amount to add
     * @return boolean true if the String is associated with an entry, false
     *         otherwise
     */
    public boolean addToAmount(String id, CurrencyIfc amountToAdd)
    {
        if (containsEntry(id))
        {
            DiscountListEntryIfc sle = map.get(id);
            sle.addToAmount(amountToAdd);
            return true;
        }
        return false;
    }
    
    /**
     * @param id - the String to increment
     * @param item - item to add to the discountListEntry
     * @return - boolean true if the String is associated with an entry, false
     *         otherwise
     */
    public boolean addDiscountItem(String id, DiscountItemIfc item)
    {
        if (containsEntry(id))
        {
            DiscountListEntryIfc sle = map.get(id);
            sle.incrementQuantity(item);
            return true;
        }
        return false;
        
    }

    /**
     * Tests whether the threshold amount has been counted for a given String.
     * 
     * @param String id - the String to test
     * @return boolean true if the String is associated with an entry and has
     *         been reached the threshold value, false otherwise
     */
    public boolean amountSatisfied(String id)
    {
        boolean satisfied = false;
        if (containsEntry(id))
        {
            DiscountListEntryIfc dle = map.get(id);
            satisfied = dle.amountSatisfied();
        }
        return satisfied;
    }
    
   
    /**
     * Tests whether the target quantity has been achieved for a given String.
     * 
     * @param String id - the String to test
     * @return boolean true if the String is associated with an entry and has
     *         been incremented the required number of times, false otherwise
     */
    public boolean quantitySatisfied(String id)
    {
        boolean satisfied = false;
        if (containsEntry(id))
        {
            DiscountListEntryIfc dle = map.get(id);
            satisfied = dle.quantitySatisfied();
        }
        return satisfied;
    }
    

    /**
     * Resets the quantity counted for each DiscountListEntry in this list.
     */
    public void resetQuantities()
    {
        for (DiscountListEntryIfc dle : map.values())
        {
            dle.resetQuantity();
        }
    }

    /**
     * Resets the amount counted for each DiscountListEntry in this list.
     */
    public void resetAmounts()
    {
        for (DiscountListEntryIfc dle : map.values())
        {
            dle.resetAmount();
        }
    }

    /**
     * Returns the quantity counted for a criterion String or -1 if the list
     * does not contain an entry for the String.
     * 
     * @return int quantity counted or -1
     */
    public int getQuantity(String id)
    {
        int value = -1;

        if (containsEntry(id))
        {
            DiscountListEntryIfc sle = map.get(id);
            value = sle.getQuantity();
        }
        return value;
    }

    /**
     * Returns the quantity required for a criterion String or -1 if the list
     * does not contain an entry for the String.
     * 
     * @return int quantity required or -1
     */
    public int getQuantityRequired(String id)
    {
        int value = -1;

        if (containsEntry(id))
        {
            DiscountListEntryIfc sle = map.get(id);
            value = sle.getQuantityRequired();
        }
        return value;
    }

    /**
     * Returns the currency amount counted for an entry.
     * 
     * @param String - the id used as a key for the discount list entry to query
     * @return CurrencyIfc indicating the current value counted for the entry
     *         whose key is the string id
     */
    public CurrencyIfc getAmount(String id)
    {
        CurrencyIfc value = DomainGateway.getBaseCurrencyInstance();

        if (containsEntry(id))
        {
            DiscountListEntryIfc sle = map.get(id);
            value = (CurrencyIfc)sle.getAmount().clone();
        }
        return value;
    }

    /**
     * Returns the threshold amount counted for this entry.
     * 
     * @param String - the id used as a key for the discount list entry to query
     * @return CurrencyIfc indicating the value the amount must reach to satisfy
     *         the criteria for this entry
     */
    public CurrencyIfc getAmountRequired(String id)
    {
        CurrencyIfc value = DomainGateway.getBaseCurrencyInstance();

        if (containsEntry(id))
        {
            DiscountListEntryIfc sle = map.get(id);
            value = (CurrencyIfc)sle.getAmountRequired().clone();
        }
        return value;
    }

    /**
     * Returns the total threshold amount for all the entries that are in the
     * list.
     * 
     * @return CurrencyIfc indicating the total amount required to satisfy the
     *         criteria for the rule
     */
    public CurrencyIfc getTotalAmountRequired()
    {
        CurrencyIfc value = DomainGateway.getBaseCurrencyInstance();
        // iterate over the entries and sum the amount
        for (DiscountListEntryIfc dle : map.values())
        {
            value = value.add(dle.getAmountRequired());
        }
        return value;
    }

    /**
     * Returns the total price for all the items that are in the list.
     * 
     * @return CurrencyIfc indicating the total selling price
     */
    public CurrencyIfc getTotalPrice()
    {
        CurrencyIfc totalSellingPrice = DomainGateway.getBaseCurrencyInstance();
        // iterate over the entries and sum the amount
        for (DiscountListEntryIfc dle : map.values())
        {
            totalSellingPrice = totalSellingPrice.add(dle.getRegularPrice());
        }
        return totalSellingPrice;
    }

    /**
     * Returns the regular price required for a criterion String or 0 if the
     * list does not contain an entry for the String. Used only by StoreCentral.
     * 
     * @return CurrencyIfc
     */
    public CurrencyIfc getRegularPrice(String id)
    {
        CurrencyIfc value = DomainGateway.getBaseCurrencyInstance();

        if (containsEntry(id))
        {
            DiscountListEntryIfc sle = map.get(id);
            value = sle.getRegularPrice();
        }
        return value;
    }

    /**
     * Returns a boolean value indicating whether all the criteria have been
     * satisfied for this list.
     * 
     * @return boolean
     */
    public boolean allSatisfied()
    {
        boolean satisfied = true;

        // check to see if the minimum quantity has been met for each target criterion
        for (DiscountListEntryIfc dle : map.values())
        {
            // if any of the minimums have not been met, exit the method
            if (!dle.quantitySatisfied())
            {
                satisfied = false;
                break;
            }
        }

        return satisfied;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.DiscountListIfc#anyComboSatisfied()
     */
    public boolean anyComboSatisfied()
    {
        boolean satisfied = false;
        
        int quantity = 0;
        Iterator<DiscountListEntryIfc> entries = map.values().iterator();
        while (entries.hasNext())
        {
            DiscountListEntryIfc entry = entries.next();
            quantity += entry.getQuantity();
            if (quantity >= entry.getQuantityRequired())
            {
                satisfied = true;
                break;
            }
        }

        return satisfied;
    }

     /**
      * Return true if the number of satisfied criteria is equal to the
      * {@link #anyQuantity}.
      * 
      * @return boolean true if any has been satisfied.
      */
     public boolean anySatisfied()
     {
        int qtySatisfied = 0;

        // check if the minimum quantity has been met for each target criterion
        for (DiscountListEntryIfc dle : map.values())
        {
            if (dle.quantitySatisfied())
            {
            	qtySatisfied += 1;
            }
        }

        return (qtySatisfied >= anyQuantity);
    }
     
    /**
     * Return true if the number of satisfied criteria is equal to the
     * {@link #anyQuantity}. This is evaluated for amount based rules.
     * 
     * @return boolean true if any has been satisfied.
     */
     public boolean anyAmountSatisfied()
    {
        boolean satisfied = false;
        for (DiscountListEntryIfc dle : map.values())
        {
            if (dle.amountSatisfied())
            {
                satisfied = true;
                break;
            }
        }
        return satisfied;
    }

    /**
     * Returns a boolean value indicating whether all the threshold amounts have
     * been reached for this list.
     * 
     * @return boolean
     */
     public boolean allAmountsSatisfied()
    {
        boolean satisfied = true;

        // check to see if the minimum quantity has been met for each target criterion
        for (DiscountListEntryIfc dle : map.values())
        {
            // if any of the minimums have not been met, exit the method
            if (!dle.amountSatisfied())
            {
                satisfied = false;
                break;
            }
        }

        return satisfied;
    }
     
     /**
      * Returns a boolean value indicating whether anyOne of the sources in the
      * source list have attained the threshold amount. Used for multi threshold
      * rule only.
      * 
      * @return
      */
     public boolean evaluateAnyAmountSatisfied(List<DiscountItemIfc> candidates, List<DiscountItemIfc> selected)
    {
        boolean satisfied = false;
        satisfied = anyAmountSatisfied();
        if (satisfied)
        {
            // if we are satisfied, pick which anys to use.
            List<DiscountItemIfc> tempSelected = pickAnyEntries();
            // clear candidates of selected anys
            candidates.removeAll(tempSelected);
            // set selected
            selected.addAll(tempSelected);
        }
        return satisfied;
    }
     
     /**
      * Returns a boolean value indicating whether the threshold amount has been
      * attained by any combination of sources in the source list. Used for multi
      * threshold rule only.
      * 
      * @return
      */
     public boolean anyComboAmountSatisfied()
    {
        boolean satisfied = false;
        CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();
        Iterator<DiscountListEntryIfc> itr = map.values().iterator();

        while (itr.hasNext())
        {
            DiscountListEntryIfc listEntry = itr.next();
            amount = amount.add(listEntry.getAmount());
            if (amount.compareTo(listEntry.getAmountRequired()) >= 0)
            {
                satisfied = true;
                break;
            }
        }

        return satisfied;
    }

    /**
     * Returns a boolean indicating whether the argument can be used to satisfy
     * the criteria maintained in this list.
     * 
     * @return boolean true if the item can be used, false otherwise
     */
    public boolean uses(DiscountItemIfc item)
    {
        boolean uses = false;

        if (isPriceValid(item))
        {
            for (Iterator<String> i = criteria(); i.hasNext(); )
            {
                if (attributesEqual(i.next(), item))
                {
                    uses = true;
                    break;
                }
            }
        }

        return uses;
    }

    /**
     * Tests an item's price to see if it is within bounds for this criteria
     * list.
     * 
     * @param DiscountItemIfc element - the item to test
     */
    public boolean isPriceValid(DiscountItemIfc element)
    {
        boolean value = true;

        CurrencyIfc price = element.getSellingPrice();

        //if limit value is negative don't check upper bounds
        if (itemLimit.signum() < 0)
        {
            if (price.compareTo(itemThreshold) < 0)
            {
                value = false;
            }
        }
        else if ((price.compareTo(itemLimit) > 0) ||
                 (price.compareTo(itemThreshold) < 0))
        {
            value = false;
        }

        return value;
    }

    /**
     * Tests to see if the attribute of a DiscountItem is equal to a criterion
     * string used for comparison.
     * 
     * @param String - the String value to compare against
     * @param DiscountItemIfc - the item whose attribute is to be compared
     * @return boolean indicating whether the String value and attribute are
     *         equivalent
     */
    public boolean attributesEqual(String criterion, DiscountItemIfc item)
    {
        return comparisonBasis == COMPARISON_BASIS_MERCHANDISE_CLASS ? item.isClassifiedAs(criterion) :
                criterion.equals(item.getComparator(comparisonBasis));
    }

    /**
     * Tests to see if the criteria for this list have been met. Adds items used
     * to satisfy a criterion to the selected ArrayList. This method should be
     * overridden by subclasses which encapsulate the selection strategy for a
     * list's criteria.
     * 
     * @param ArrayList containing potential sources
     * @param ArrayList empty ArrayList used to store selected sources
     * @return boolean indicating whether all the source criteria for this list
     *         have been met.
     */
    public boolean evaluate(ArrayList<? extends DiscountItemIfc> candidates, ArrayList<? extends DiscountItemIfc> selected)
    {
        return false;
    }

    /**
     * Tests to see if the criteria for this list have been met. Adds items used
     * to satisfy a criterion to the selected ArrayList. This method should be
     * overridden by subclasses which encapsulate the selection strategy for a
     * list's criteria.
     * 
     * @param ArrayList containing potential sources
     * @param ArrayList empty ArrayList used to store selected sources
     * @param sourcesAreTargets indicates if sources are also considered targets
     * @return boolean indicating whether all the source criteria for this list
     *         have been met.
     */
    public boolean evaluate(ArrayList<? extends DiscountItemIfc> candidates, ArrayList<? extends DiscountItemIfc> selected, boolean sourcesAreTargets)
    {
        return false;
    }

    /**
     * Internal method to determine if enough of the "Any" entries have been
     * satisfied. In which case, the selected list will be populated and the
     * candidates will be trimmed.
     * 
     * @param candidates
     * @param selected
     * @return true if enough dle's are satisfied to meet the anyQty
     */
    protected boolean evaluateAnySatisfied(List<DiscountItemIfc> candidates, List<DiscountItemIfc> selected)
    {
        return evaluateAnySatisfied(candidates, selected, false);
    }
    
    /**
     * Internal method to determine if enough of the "Any combo of quantity"
     * entries have been satisfied. In which case, the selected list will be
     * populated and the candidates will be trimmed.
     * 
     * @param candidates
     * @param selected
     * @return
     */
    protected boolean evaluateAnyComboQuantitySatisfied(List<DiscountItemIfc> candidates, List<DiscountItemIfc> selected)
    {
        boolean anyComboSatisfied = anyComboSatisfied();

        if (anyComboSatisfied)
        {
            List<DiscountListEntryIfc> entries = new ArrayList<DiscountListEntryIfc>(map.size());
            entries.addAll(map.values());
            sortAnyEntries(entries);

            List<DiscountItemIfc> anyComboSelected = new ArrayList<DiscountItemIfc>();

            int quantityRequired = entries.get(0).getQuantityRequired();

            for (int i = 0; i < entries.size(); i++)
            {
                DiscountListEntryIfc dle = entries.get(i);

                if (entries.get(i).getQuantity() <= quantityRequired)
                {
                    anyComboSelected.addAll(dle.getDiscountItems());
                    quantityRequired -= dle.getQuantity();
                }
                else
                {
                    anyComboSelected.addAll(dle.getDiscountItems().subList(0, quantityRequired));
                    break;
                }

            }

            candidates.removeAll(anyComboSelected);
            selected.clear();
            selected.addAll(anyComboSelected);
        }

        return anyComboSatisfied;
    }
    
    /**
     * Internal method to determine if enough of the "AnyCombo of Amount"
     * entries have been satisfied. In which case, the selected list will be
     * populated and the candidates will be trimmed.
     * 
     * @param candidates
     * @param selected
     * @return
     */
    protected boolean evaluateAnyComboAmountSatisfied(List<DiscountItemIfc> candidates, List<DiscountItemIfc> selected)
    {
        boolean anyComboAmountSatisfied = anyComboAmountSatisfied();

        if (anyComboAmountSatisfied)
        {
            List<DiscountListEntryIfc> entries = new ArrayList<DiscountListEntryIfc>(map.size());
            entries.addAll(map.values());
            sortAnyEntries(entries);
            List<DiscountItemIfc> bestCombo = getBestPossibleCombo(entries);
            candidates.removeAll(bestCombo);
            selected.clear();
            selected.addAll(bestCombo);
        }

        return anyComboAmountSatisfied;
    }
    
    /**
     * Internal method to find the best candidate set combination whose
     * extendedDiscountedSellingPrice some is equal or just greater than the
     * amountRequired.
     * 
     * @param entries sorted in descending order.
     * @return the best combo list which is equal or just greater than the
     *         amount required.
     */
    private List<DiscountItemIfc> getBestPossibleCombo(List<DiscountListEntryIfc> entries)
    {
        List<DiscountItemIfc> candidates = new ArrayList<DiscountItemIfc>();
        CurrencyIfc amountRequired = entries.get(0).getAmountRequired();
        for (int j = 0; j < entries.size(); j++)
        {
            DiscountListEntryIfc dle = entries.get(j);
            List<DiscountItemIfc> ditem = dle.getDiscountItems();
            for (DiscountItemIfc item : ditem)
            {
                candidates.add(item);
            }
        }
        List<DiscountItemIfc> subSet = new ArrayList();
        double sd = Double.MAX_VALUE;
        for (int i = 0, k = i + 1; i < candidates.size();)
        {

            CurrencyIfc a = candidates.get(i).getExtendedDiscountedSellingPrice();
            if (a.compareTo(amountRequired) == CurrencyIfc.GREATER_THAN)
            {
                subSet.clear();
                subSet.add(candidates.get(i));
                sd = (a.getDoubleValue() - amountRequired.getDoubleValue());
                i++;
                k++;
            }
            else if (a.compareTo(amountRequired) == CurrencyIfc.EQUALS)
            {
                subSet.clear();
                subSet.add(candidates.get(i));
                sd = 0.0;
                break;
            }
            else
            {
                CurrencyIfc b = (CurrencyIfc)a.clone();
                List<DiscountItemIfc> subSet2 = new ArrayList<>();
                subSet2.add(candidates.get(i));
                while (b.compareTo(amountRequired) == CurrencyIfc.LESS_THAN && k < candidates.size())
                {
                    b = b.add(candidates.get(k).getExtendedDiscountedSellingPrice());
                    subSet2.add(candidates.get(k));
                    k++;
                }
                if (b.compareTo(amountRequired) == CurrencyIfc.GREATER_THAN)
                {
                    double sd1 = b.getDoubleValue() - amountRequired.getDoubleValue();
                    if (sd1 < sd)
                    {
                        sd = sd1;
                        subSet.clear();
                        subSet.addAll(subSet2);
                    }
                }
                else if (b.compareTo(amountRequired) == CurrencyIfc.EQUALS)
                {
                    subSet.clear();
                    subSet.addAll(subSet2);
                    break;
                }
                i++;
                k = i + 1;
            }

        }

        return subSet;
    }

    /**
     * Internal method to determine if enough of the "Any" entries have been
     * satisfied. In which case, if allEligibleTargets is false then the selected list
     * will be populated and the candidates will be trimmed. If allEligibleTargets is true
     * and the any criteria is satisfied then the selected list will be populated with all the
     * candidates.
     *
     * @param candidates
     * @param selected
     * @return true if enough dle's are satisfied to meet the anyQty
     */
    protected boolean evaluateAnySatisfied(List<DiscountItemIfc> candidates, List<DiscountItemIfc> selected,
            boolean allEligibleTargets)
    {
        boolean anySatisfied = anySatisfied();
        if (anySatisfied && allEligibleTargets)
        {
            // if Any is satisfied, and ellEligibleTargets is true,
            // return all the eligible targets.
            for (DiscountListEntryIfc dle : map.values())
            {
                selected.addAll(dle.getDiscountItems());
            }
        }
        else if (anySatisfied)
        {
            // if we are satisfied, pick which anys to use.
            List<DiscountItemIfc> tempSelected = pickAnyEntries();
            // clear candidates of selected anys
            candidates.removeAll(tempSelected);
            // set selected
            selected.addAll(tempSelected);
        }
        return anySatisfied;
    }

    /**
     * Select {@link #anyQuantity} of the most expensive
     * {@link DiscountListEntry}s. This method will call
     * {@link #sortAnyEntries(List)} if there are more anys than we need.
     * 
     * @return the list of selected selected "any" discount items.
     */
    protected List<DiscountItemIfc> pickAnyEntries()
    {
        List<DiscountListEntryIfc> entries = new ArrayList<DiscountListEntryIfc>(map.size());
        // add qualified dles to the list
        for (DiscountListEntryIfc dle : map.values())
        {
            switch (thresholdType)
            {
                case THRESHOLD_QUANTITY:
                {
                    if (dle.quantitySatisfied())
                    {
                        entries.add(dle);
                    }
                    break;
                }
                case THRESHOLD_AMOUNT:
                {
                    if (dle.amountSatisfied())
                    {
                        entries.add(dle);
                    }
                    break;
                }
            }
        }
        // if too many, sort by most expensive
        if (entries.size() > anyQuantity)
        {
            sortAnyEntries(entries);
        }
        int anySelectedQty = anyQuantity;
        if(entries.size() < anyQuantity)
        {
            //When the all source items belong to the same creiterion and match the ANY quantity
            anySelectedQty = entries.size();
        }
        // add any qty to selected
        List<DiscountItemIfc> anySelected = new ArrayList<DiscountItemIfc>(entries.size());
        for (int i = 0; i < anySelectedQty; i++)
        {
            // don't stop if "NorMore" type discount
            if (i >= anyQuantity
                && !(DISCOUNT_DESCRIPTION_BuyNorMoreOfXforZPctoff.equals(description)
                        || DISCOUNT_DESCRIPTION_BuyNorMoreOfXforZ$Each.equals(description)))
            {
                break;
            }
            DiscountListEntryIfc dle = entries.get(i);
            anySelected.addAll(dle.getDiscountItems());
        }
        return anySelected;
    }

    /**
     * Override this method to sort the entries in the order you want them
     * selected. This implementation will not sort the list.
     * 
     * @param entries the {@link DiscountListEntry}s to sort.
     */
    protected void sortAnyEntries(List<DiscountListEntryIfc> entries)
    {
        // do nothing.
    }
    
    /**
    Tests to see if the criteria for this list have been met.
    Adds items used to satisfy a criterion to the selected ArrayList.

    @param ArrayList containing potential sources
    @param ArrayList empty ArrayList used to store selected sources
    @return boolean indicating whether all the source criteria for this list
    have been met.
    **/
    //---------------------------------------------------------------------
    public boolean evaluateAllEligibleSourcesAndTargets(ArrayList candidates, ArrayList selected)
    {
    	return false;
    }

    /**
     * When both the source and target of the discount rule are set to "Class",
     * evaluates the eligible sources and targets dynamically.
     *
     * @param eligibleSources all the available sources
     * @param eligibleTargets all the available targets
     * @param isEqualOrLesserValue denotes the EOLV flag
     * @return returns a boolean value indicating the status of the evaluation.
     */
    public boolean reevaluate(ArrayList eligibleSources, ArrayList eligibleTargets, boolean isEqualOrLesserValue)
    {
    	return false;
    }

    /**
    Tests whether the required quantity has been achieved for a given ArrayList.

    @param ArrayList sourcesOrTargets - the ArrayList containing the Sources Or Targets
    @return boolean value indicating whether the target quantity is satisfied or not.
    **/
    //--------------------------------------------------------------------------
    public boolean quantitySatisfied(ArrayList sourcesOrTargets)
    {
    	return false;
    }   
    
}
