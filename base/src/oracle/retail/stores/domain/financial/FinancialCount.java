/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/FinancialCount.java /main/15 2013/09/05 10:36:14 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   11/03/11 - in offline mode it seems the array of tenders
 *                         included summaries for some totals and not for
 *                         others. Added some logic to ignore the tenders that
 *                         should not be added in.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    jswan     08/13/09 - Added the till reconcile amount and count to the
 *                         till, workstation and store tender history tables
 *                         and to code that reads from and writes to them.
 *    glwang    02/26/09 - get count type from TillAdjustmentTransaction
 *                         instead of FinancialCount
 *    glwang    02/26/09 - enhance till pick to support detail count
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.utility.CountryCodesIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This class represents the count data for a financial entity. It includes a
 * set of expected totals, a set of entered totals and a count type (which may
 * be "none"). The array of tender items is stored internally as a Vector. This
 * class offers several methods for adding tender items. The basic method is:
 * <ul>
 * <code>tenderCount.addTenderItem("Cash", 1, 0, new CurrencyIfc(10.25),
 * DomainGateway.getBaseCurrencyInstance())</code>
 * </ul>
 * This example adds a "Cash" tender item for $10.25 with the number of items
 * set to 1. (The third and fifth parameters are for numberItemsOut and
 * amountOut). The <code>addTenderItemIn</code> and
 * <code>addTenderItemOut</code> methods offer a shortcut by adding only items
 * in or out, respectively. The <code>addTenderItem</code> methods search for
 * a matching tender item in the array and, if found, add the amounts and
 * counts. If no match is found, a new tender item is added to the array. Some
 * media are counted but not reconciled, such as denominations of currency and
 * coinage. To handle this, the following <code>addTenderItem</code> method
 * should be used:
 * <ul>
 * <code>tenderCount.addTenderItem("Nickels", 205, 0, new CurrencyIfc(10.25),
 * DomainGateway.getBaseCurrencyInstance(), "Cash");</code>
 * </ul>
 * This method creates an entry for the 205 nickels (with a summary flag set to
 * false) and a summary entry for the $10.25 cash.
 * 
 * @see oracle.retail.stores.domain.financial.FinancialCountTenderItem
 * @see oracle.retail.stores.commerceservices.common.currency.CurrencyIfc
 * @version $Revision: /main/15 $
 */
public class FinancialCount implements FinancialCountIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 2522814123685435945L;

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * TenderTypeMapIfc
     */
    protected static final TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();

    /**
     * count type
     */
    protected int countType = FinancialCountIfc.COUNT_TYPE_NONE;

    /**
     * tenderItems vector
     */
    protected List<FinancialCountTenderItemIfc> tenderItems;

    /**
     * Persistent currency type.
     */
    protected CurrencyTypeIfc baseCurrencyType;

    /**
     * Constructor
     */
    public FinancialCount()
    {
        tenderItems = new ArrayList<FinancialCountTenderItemIfc>();
        baseCurrencyType = DomainGateway.getBaseCurrencyType();
    }

    /**
     * Constructs FinancialCount object. / public FinancialCount() {
     * tenderItems = new ArrayList(); } /** Creates clone of this object.
     * 
     * @return Object clone of this object
     */
    public Object clone()
    {
        FinancialCountIfc s = new FinancialCount();
        setCloneAttributes(s);
        return (s);
    }

    /**
     * Determine if two objects are identical.
     * 
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        if (obj instanceof FinancialCount)
        {
            FinancialCount c = (FinancialCount)obj;
            isEqual = (countType == c.getCountType() && Util.isArrayEqual(getTenderItems(), c.getTenderItems()));
        }
        return isEqual;
    }

    /**
     * Sets attributes in clone.
     * 
     * @param newClass new instance of class
     */
    protected void setCloneAttributes(FinancialCountIfc newClass)
    {
        // set values
        newClass.setCountType(countType);
        // clone tender items
        FinancialCountTenderItemIfc[] t = getTenderItems();

        if (t != null)
        {
            FinancialCountTenderItemIfc[] tclone = new FinancialCountTenderItemIfc[t.length];

            for (int i = 0; i < t.length; i++)
            {
                tclone[i] = (FinancialCountTenderItemIfc)t[i].clone();
            }
            newClass.setTenderItems(tclone);
        }
    }

    /**
     * Retrieves an int tenderType code given a String value. Attempts to
     * retrieve the value from the TenderTypeMap instance by first using the
     * String argument as a descriptor, if this fails (map returns -1) an
     * attempt is made to retrieve the value using the String as tender type
     * code.
     * 
     * @param String
     * @return int
     * @see oracle.retail.stores.domain.tender.TenderTypeMap
     */
    protected int getTenderType(String value)
    {
        int tenderType = tenderTypeMap.getTypeFromDescriptor(value);
        if (tenderType < 0)
        {
            // will set the return value to -1 if value
            // doesn't match anything in the map
            tenderType = tenderTypeMap.getTypeFromCode(value);
        }
        return tenderType;
    }

    /**
     * Adds a FinancialCount object to this object and returns result. This
     * method is incomplete.
     * 
     * @param t FinancialCount object to be added to this object
     * @return resulting FinancialCount object
     */
    public FinancialCountIfc add(FinancialCountIfc addCount)
    {
        // loop through tender items and add
        if (addCount != null)
        {
            FinancialCountTenderItemIfc[] t = addCount.getTenderItems();
            if (t != null)
            {
                for (int i = 0; i < t.length; i++)
                {
                    addTenderItem(t[i]);
                }
            }
        }

        // clone this object and return it
        FinancialCountIfc newCount = (FinancialCountIfc)clone();
        return (newCount);
    }

    /**
     * Creates a negative copy of this object.
     * 
     * @return resulting FinancialCount object
     */
    public FinancialCountIfc negate()
    {
        FinancialCountIfc newCount = DomainGateway.getFactory().getFinancialCountInstance();
        // loop through tender items and negate
        FinancialCountTenderItemIfc[] t = getTenderItems();

        if (t != null)
        {
            for (int i = 0; i < t.length; i++)
            {
                newCount.addTenderItem(t[i].negate());
            }
        }

        return (newCount);
    }

    /**
     * Resets financial count object.
     */
    public void resetTotals() {
        tenderItems = new ArrayList<FinancialCountTenderItemIfc>();
    }

    /** Get amount total of items in
     * count.
     * 
     * @return amount total
     */
    public CurrencyIfc getAmount()
    {
        String countryCode = baseCurrencyType.getCountryCode();
        return getAmount(countryCode);
    }

    /**
     * Get amount total of items in count of the specified currency.
     * 
     * @param countryCode String
     * @return amount total
     */
    public CurrencyIfc getAmount(String countryCode)
    {
        CurrencyIfc amount = DomainGateway.getCurrencyInstance(countryCode);

        // loop through tender items
        for (FinancialCountTenderItemIfc t : tenderItems)
        {
            // add up only summary items
            if (t.getCurrencyCode().equals(countryCode) && t.isSummary())
            {
                amount = amount.add(t.getAmountTotal());
            }
        }
        return (amount);
    }

    /**
     * Get total number of items in count.
     * 
     * @return total number of items
     */
    public int getNumberItems()
    {
        int number = 0;

        for (FinancialCountTenderItemIfc t : tenderItems)
        {
            if (t.isSummary())
            {
                number += t.getNumberItemsTotal();
            }
        }
        return (number);
    }

    /**
     * Adds values to the array of FinancialCountTenderItemIfc objects. If the
     * item already exists in the array, the specified values are added to the
     * item in the array. Otherwise, a new item is created.
     * 
     * @param description identifier of tender type
     * @param numberItemsIn number of items of this type
     * @param numberItemsOut number of items of this type
     * @param amountIn amount of this tender type taken in
     * @param amountOut amount of this tender type paid out
     * @see FinancialCountTenderItemIfc
     */
    public void addTenderItem(String description, int numberItemsIn, int numberItemsOut, CurrencyIfc amountIn,
            CurrencyIfc amountOut)
    {
        addTenderItem(description, numberItemsIn, numberItemsOut, amountIn, amountOut, "", false);
    }

    /**
     * Adds values to the array of FinancialCountTenderItemIfc objects. If the
     * item already exists in the array, the specified values are added to the
     * item in the array. Otherwise, a new item is created.
     * 
     * @param description identifier of tender type
     * @param numberItemsIn number of items of this type
     * @param numberItemsOut number of items of this type
     * @param amountIn amount of this tender type taken in
     * @param amountOut amount of this tender type paid out
     * @param hasDenominations indicator that count item has denominations
     * @see FinancialCountTenderItemIfc
     */
    public void addTenderItem(String description, int numberItemsIn, int numberItemsOut, CurrencyIfc amountIn,
            CurrencyIfc amountOut, boolean hasDenominations)
    {
        // Hacked until we get rid of the all encompassing description!
        String baseCountryCode = baseCurrencyType.getCountryCode();
        String currencyCode = baseCountryCode;
        int currencyID = baseCurrencyType.getCurrencyId(); // I18N

        if (amountIn.getCountryCode() != baseCountryCode)
        {
            currencyCode = amountIn.getCountryCode();
            currencyID = amountIn.getType().getCurrencyId();
        }
        else if (amountOut.getCountryCode() != baseCountryCode)
        {
            currencyCode = amountOut.getCountryCode();
            currencyID = amountOut.getType().getCurrencyId();
        }

        // create new item object
        FinancialCountTenderItemIfc item = instantiateFinancialCountTenderItemIfc(currencyCode, currencyID);
        // set attributes
        item.setDescription(description);
        item.setTenderType(getTenderType(description));
        item.setNumberItemsIn(numberItemsIn);
        item.setNumberItemsOut(numberItemsOut);
        item.setAmountIn(amountIn);
        item.setAmountOut(amountOut);
        item.setSummary(true);
        item.setHasDenominations(hasDenominations);
        item.setCurrencyID(currencyID); // I18N
        // add item to array
        addTenderItem(item);
    }

    /**
     * Adds values to the array of FinancialCountTenderItemIfc objects. If the
     * item already exists in the array, the specified values are added to the
     * item in the array. Otherwise, a new item is created. In this case, an
     * item is added with the <code>summary</code> attribute set to false.
     * Another item is added matching the <code>summaryDescription</code>
     * parameter.
     * 
     * @param description identifier of tender type
     * @param numberItemsIn number of items of this type
     * @param numberItemsOut number of items of this type
     * @param amountIn amount of this tender type taken in
     * @param amountOut amount of this tender type paid out
     * @param summaryDescription summary description
     * @see FinancialCountTenderItemIfc
     */
    public void addTenderItem(String description, int numberItemsIn, int numberItemsOut, CurrencyIfc amountIn,
            CurrencyIfc amountOut, String summaryDescription)
    {
        addTenderItem(description, numberItemsIn, numberItemsOut, amountIn, amountOut, summaryDescription, false);
    }

    /**
     * Adds values to the array of FinancialCountTenderItemIfc objects. If the
     * item already exists in the array, the specified values are added to the
     * item in the array. Otherwise, a new item is created. In this case, an
     * item is added with the <code>summary</code> attribute set to false.
     * Another item is added matching the <code>summaryDescription</code>
     * parameter.
     * 
     * @param description identifier of tender type
     * @param numberItemsIn number of items of this type
     * @param numberItemsOut number of items of this type
     * @param amountIn amount of this tender type taken in
     * @param amountOut amount of this tender type paid out
     * @param summaryDescription summary description
     * @param hasDenominations indicator that count item has denominations
     * @see FinancialCountTenderItemIfc
     */
    public void addTenderItem(String description, int numberItemsIn, int numberItemsOut, CurrencyIfc amountIn,
            CurrencyIfc amountOut, String summaryDescription, boolean hasDenominations)
    {
        String baseCountryCode = baseCurrencyType.getCountryCode();
        String currencyCode = baseCountryCode;
        int currencyID = baseCurrencyType.getCurrencyId(); // I18N

        if (amountIn.getCountryCode() != baseCountryCode)
        {
            currencyCode = amountIn.getCountryCode();
            currencyID = amountIn.getType().getCurrencyId();
        }
        else if (amountOut.getCountryCode() != baseCountryCode)
        {
            currencyCode = amountOut.getCountryCode();
            currencyID = amountOut.getType().getCurrencyId();
        }

        // create new item object
        FinancialCountTenderItemIfc item = instantiateFinancialCountTenderItemIfc(currencyCode, currencyID);

        // set attributes
        // Note: This was changed to use the summaryDescription
        // (rather than description) during 5.1 to correct a problem.
        item.setTenderType(getTenderType(summaryDescription));
        item.setDescription(description);
        item.setNumberItemsIn(numberItemsIn);
        item.setNumberItemsOut(numberItemsOut);
        item.setAmountIn(amountIn);
        item.setAmountOut(amountOut);
        item.setSummaryDescription(summaryDescription);
        // Note: This was changed to use the description (rather than summary
        // description during 5.1 to correct a problem.
        item.setTenderSubType(description);
        item.setSummary(false);
        item.setHasDenominations(hasDenominations);
        // add item to array
        addTenderItem(item);
        // create summary item
        FinancialCountTenderItemIfc summaryItem = (FinancialCountTenderItemIfc)item.clone();
        summaryItem.setSummary(true);
        summaryItem.setDescription(summaryDescription);
        summaryItem.setSummaryDescription("");
        summaryItem.setTenderSubType("");
        addTenderItem(summaryItem);
    }

    /**
     * Adds values to the array of FinancialCountTenderItemIfc objects. If the
     * item already exists in the array, the specified values are added to the
     * item in the array. Otherwise, a new item is created. In this case, an
     * item is added with the <code>summary</code> attribute set to false.
     * Another item is added matching the <code>summaryDescription</code>
     * parameter.
     * 
     * @param descriptor TenderDescriptorIfc describes the tender
     *            characteristics
     * @param numberItemsIn int number of items of this type
     * @param numberItemsOut int number of items of this type
     * @param amountIn CurrencyIfc amount of this tender type taken in
     * @param amountOut CurrencyIfc amount of this tender type paid out
     * @param description String description
     * @param summaryDescription String summary description
     * @param hasDenominations boolean indicator that count item has
     *            denominations
     * @see FinancialCountTenderItemIfc
     */
    public void addTenderItem(TenderDescriptorIfc descriptor, int numberItemsIn, int numberItemsOut,
            CurrencyIfc amountIn, CurrencyIfc amountOut, String desc, String summaryDesc, boolean hasDenominations)
    {
        addTenderItem(descriptor, numberItemsIn, numberItemsOut, amountIn, amountOut, desc, summaryDesc,
                hasDenominations, true);
    }

    /**
     * Adds values to the array of FinancialCountTenderItemIfc objects. If the
     * item already exists in the array, the specified values are added to the
     * item in the array. Otherwise, a new item is created. In this case, an
     * item is added with the <code>summary</code> attribute set to false.
     * Another item is added matching the <code>summaryDescription</code>
     * parameter.
     * 
     * @param descriptor TenderDescriptorIfc identifies the tender
     *            characteristics
     * @param numberItemsIn int number of items of this type
     * @param numberItemsOut int number of items of this type
     * @param amountIn CurrencyIfc amount of this tender type taken in
     * @param amountOut CurrencyIfc amount of this tender type paid out
     * @param description String description
     * @param summaryDesc String summary description
     * @param hasDenominations boolean indicator that count item has
     *            denominations
     * @param isSummary boolean indicator that determines whether this is a
     *            tender denomination or not
     * @see FinancialCountTenderItemIfc
     */
    public void addTenderItem(TenderDescriptorIfc descriptor, int numberItemsIn, int numberItemsOut,
            CurrencyIfc amountIn, CurrencyIfc amountOut, String desc, String summaryDesc, boolean hasDenominations,
            boolean isSummary)
    {
        FinancialCountTenderItemIfc item = instantiateFinancialCountTenderItemIfc(descriptor.getCountryCode(),
                descriptor.getCurrencyID());
        item.setTenderDescriptor(descriptor);
        item.setDescription(desc);
        item.setSummaryDescription(summaryDesc);
        item.setNumberItemsIn(numberItemsIn);
        item.setNumberItemsOut(numberItemsOut);
        item.setAmountIn(amountIn);
        item.setAmountOut(amountOut);
        item.setSummary(isSummary);
        item.setHasDenominations(hasDenominations);
        // add item to array
        addTenderItem(item);
    }

    /**
     * Adds a FinancialCountTenderItemIfc object to the array of
     * FinancialCountTenderItemIfc objects. If the item already exists in the
     * array, the specified values are added to the item in the array.
     * Otherwise, a new item is created.
     * 
     * @param item FinancialCountTenderItemIfc object
     */
    public void addTenderItem(FinancialCountTenderItemIfc item)
    {
        FinancialCountTenderItemIfc t = null;
        FinancialCountTenderItemIfc listItem = null;
        String desc = item.getDescription();
        TenderDescriptorIfc descriptor = item.getTenderDescriptor();
        int tenderType = item.getTenderType();

        // If the tender item gets this far without a valid tender discriptor,
        // generate from the description.
        if (tenderType == -1)
        {
            tenderType = getTenderType(desc);
            descriptor.setTenderType(tenderType);
            descriptor.setCountryCode(item.getAmountTotal().getCountryCode());
        }

        boolean matchFound = false;

        // loop through tenderItems
        for (int i = 0; i < tenderItems.size(); i++)
        {
            listItem = tenderItems.get(i);

            // if match found, set desc and exit
            if (listItem.getTenderDescriptor().equals(descriptor)
                    && listItem.getTenderSubType().equals(item.getTenderSubType()) && item.isSummary())
            {
                // add to matched element and replace it
                t = listItem.add(item);
                tenderItems.set(i, t);
                matchFound = true;
                i = tenderItems.size();
            }
        }

        // if no match, add to vector
        if (matchFound == false)
        {
            tenderItems.add(item);
        }
    }

    /**
     * Adds values to the array of FinancialCountTenderItemIfc objects for an
     * "in" entry. If the item already exists in the array, the specified values
     * are added to the item in the array. Otherwise, a new item is created.
     * 
     * @param description identifier of tender type
     * @param numberItemsIn number of items of this type
     * @param amountIn amount of this tender type taken in
     * @see FinancialCountTenderItemIfc
     */
    public void addTenderItemIn(String description, int numberItemsIn, CurrencyIfc amountIn)
    {
        String currencyCode = amountIn.getCountryCode();
        addTenderItem(description, numberItemsIn, 0, amountIn, DomainGateway.getCurrencyInstance(currencyCode));
    }

    /**
     * Adds values to the array of FinancialCountTenderItemIfc objects for an
     * "in" entry. If the item already exists in the array, the specified values
     * are added to the item in the array. Otherwise, a new item is created.
     * 
     * @param type String tender type {@link TenderLineItemIfc}
     * @param nationality String code {@link CountryCodesIfc}
     * @param numberItemsIn number of items of this type
     * @param amountIn amount of this tender type taken in
     * @see FinancialCountTenderItemIfc
     */
    public void addTenderItemIn(int type, String nationality, int numberItemsIn, CurrencyIfc amountIn)
    {
        TenderDescriptorIfc descriptor = DomainGateway.getFactory().getTenderDescriptorInstance();
        CurrencyIfc cif = DomainGateway.getCurrencyInstance(nationality);
        descriptor.setCurrencyID(cif.getType().getCurrencyId());
        descriptor.setCountryCode(nationality);
        descriptor.setTenderType(type);
        addTenderItem(descriptor, numberItemsIn, 0, amountIn, cif, tenderTypeMap.getDescriptor(type), "", false);
    }

    /**
     * Adds values to the array of FinancialCountTenderItemIfc objects for an
     * "in" entry. If the item already exists in the array, the specified values
     * are added to the item in the array. Otherwise, a new item is created.
     * 
     * @param descriptor TenderDescriptorIfc
     * @param numberItemsIn number of items of this type
     * @param amountIn amount of this tender type taken in
     * @see FinancialCountTenderItemIfc
     */
    public void addTenderItemIn(TenderDescriptorIfc descriptor, int numberItemsIn, CurrencyIfc amountIn)
    {
        addTenderItem(descriptor, numberItemsIn, 0, amountIn, DomainGateway.getCurrencyInstance(descriptor
                .getCountryCode()), tenderTypeMap.getDescriptor(descriptor.getTenderType()), "", false);
    }

    /**
     * Adds values to the array of FinancialCountTenderItemIfc objects for an
     * "out" entry. If the item already exists in the array, the specified
     * values are added to the item in the array. Otherwise, a new item is
     * created.
     * 
     * @param description identifier of tender type
     * @param numberItemsOut number of items of this type
     * @param amountOut amount of this tender type paid out
     * @see FinancialCountTenderItemIfc
     */
    public void addTenderItemOut(String description, int numberItemsOut, CurrencyIfc amountOut)
    {
        String currencyCode = amountOut.getCountryCode();

        addTenderItem(description, 0, numberItemsOut, DomainGateway.getCurrencyInstance(currencyCode), amountOut);
    }

    /**
     * Adds values to the array of FinancialCountTenderItemIfc objects for an
     * "out" entry. If the item already exists in the array, the specified
     * values are added to the item in the array. Otherwise, a new item is
     * created.
     * 
     * @param type String tender type {@link TenderLineItemIfc}
     * @param nationality String code {@link CountryCodesIfc}
     * @param numberItemsOut number of items of this type
     * @param amountOut amount of this tender type paid out
     * @see FinancialCountTenderItemIfc
     */
    public void addTenderItemOut(int type, String nationality, int numberItemsOut, CurrencyIfc amountOut)
    {
        TenderDescriptorIfc descriptor = DomainGateway.getFactory().getTenderDescriptorInstance();
        descriptor.setCountryCode(nationality);
        descriptor.setTenderType(type);
        descriptor.setCurrencyID(DomainGateway.getAlternateCurrencyInstance(nationality).getType().getCurrencyId());
        addTenderItem(descriptor, 0, numberItemsOut, DomainGateway.getCurrencyInstance(nationality), amountOut,
                tenderTypeMap.getDescriptor(type), "", false);
    }

    /**
     * Adds values to the array of FinancialCountTenderItemIfc objects for an
     * "out" entry. If the item already exists in the array, the specified
     * values are added to the item in the array. Otherwise, a new item is
     * created.
     * 
     * @param descriptor TenderDescriptorIfc
     * @param numberItemsOut number of items of this type
     * @param amountOut amount of this tender type paid out
     * @see FinancialCountTenderItemIfc
     */
    public void addTenderItemOut(TenderDescriptorIfc descriptor, int numberItemsOut, CurrencyIfc amountOut)
    {
        addTenderItem(descriptor, 0, numberItemsOut, DomainGateway.getCurrencyInstance(descriptor.getCountryCode()),
                amountOut, tenderTypeMap.getDescriptor(descriptor.getTenderType()), "", false);
    }

    /**
     * Replaces a FinancialCountTenderItemIfc object in the array of
     * FinancialCountTenderItemIfc objects. If the item does not already exist
     * in the array, a new item is created.
     * 
     * @param item FinancialCountTenderItemIfc object
     */
    public void replaceTenderItem(FinancialCountTenderItemIfc item)
    {
        FinancialCountTenderItemIfc listFinancialCountTenderItem = null;
        // get description
        String desc = item.getDescription();
        String summaryDescription = item.getSummaryDescription();
        // flag indicating item should be added to array
        boolean bAdd = true;
        // look for item in array
        for (int i = 0; i < tenderItems.size(); i++)
        {
            // pull till from vector
            listFinancialCountTenderItem = tenderItems.get(i);
            // if match found, set desc and exit
            if (Util.isObjectEqual(listFinancialCountTenderItem.getDescription(), desc)
                    && Util.isObjectEqual(listFinancialCountTenderItem.getSummaryDescription(), summaryDescription))
            {
                tenderItems.set(i, item);
                i = tenderItems.size();
                bAdd = false;
            }
        }
        // if item not found, replace new one
        if (bAdd)
        {
            tenderItems.add(item);
        }
    }

    /**
     * Instantiates FinancialCountTenderItemIfc object.
     * 
     * @return FinancialCountTenderItemIfc object
     */
    public FinancialCountTenderItemIfc instantiateFinancialCountTenderItemIfc()
    {
        CurrencyTypeIfc type = baseCurrencyType;
        return instantiateFinancialCountTenderItemIfc(type.getCountryCode(), type.getCurrencyId());
    }

    /**
     * Instantiates FinancialCountTenderItemIfc object for non-base currency.
     * 
     * @param currencyCode String
     * @return FinancialCountTenderItemIfc object
     */
    public FinancialCountTenderItemIfc instantiateFinancialCountTenderItemIfc(String currencyCode)
    {
        FinancialCountTenderItemIfc item = DomainGateway.getFactory().getFinancialCountTenderItemInstance();
        item.initialize(currencyCode);
        return item;
    }

    /**
     * Instantiates FinancialCountTenderItemIfc object for non-base currency.
     * 
     * @param currencyCode String
     * @param currencyID int
     * @return FinancialCountTenderItemIfc object
     */
    public FinancialCountTenderItemIfc instantiateFinancialCountTenderItemIfc(String currencyCode, int currencyID)
    {
        FinancialCountTenderItemIfc item = instantiateFinancialCountTenderItemIfc(currencyCode);
        item.setCurrencyID(currencyID);
        return item;
    }

    /**
     * Retrieves FinancialCountTenderItemIfc object matching specified type and
     * country code. If no match is found, null is returned.
     * 
     * @param td {@link TenderDescriptorIfc}
     * @param sumTenderItems loop thru all tenders and add all similar tenders
     *            together
     * @return FinancialCountTenderItemIfc object if found; null if not found
     */
    public FinancialCountTenderItemIfc getTenderItem(TenderDescriptorIfc td, boolean sumTenderItems)
    {
        FinancialCountTenderItemIfc t = null;
        FinancialCountTenderItemIfc listFinancialCountTenderItem = null;

        // loop through tenderItems
        for (int i = 0; i < tenderItems.size(); i++)
        {
            listFinancialCountTenderItem = tenderItems.get(i);

            if (td.equals(listFinancialCountTenderItem.getTenderDescriptor()))
            {
                if (t == null)
                {
                    t = listFinancialCountTenderItem;
                }
                else
                {
                    t = t.add(listFinancialCountTenderItem);
                }
                if (sumTenderItems == false) // "break" for loop if we are
                // not summing
                {
                    i = tenderItems.size(); // "break"
                }
            }
        }
        return (t);
    }

    /**
     * Retrieves FinancialCountTenderItemIfc object matching specified
     * description. If no match is found, null is returned.
     * 
     * @param td descriptor of requested item
     * @return FinancialCountTenderItemIfc object if found; null if not found
     */
    public FinancialCountTenderItemIfc getSummaryTenderItemByDescriptor(TenderDescriptorIfc td)
    {
        FinancialCountTenderItemIfc t = null;
        FinancialCountTenderItemIfc listFinancialCountTenderItem = null;

        // loop through tenderItems
        for (int i = 0; i < tenderItems.size() && t == null; i++)
        {
            // pull till from vector
            listFinancialCountTenderItem = tenderItems.get(i);

            // if match found, set desc and exit
            if (listFinancialCountTenderItem.getTenderDescriptor().equals(td)
                    && listFinancialCountTenderItem.isSummary())
            {
                t = listFinancialCountTenderItem;
            }
        }
        return (t);
    }

    /**
     * Retrieves FinancialCountTenderItemIfc for all base currency tenders.
     * Alternate currency tenders are excluded.
     * 
     * @return FinancialCountTenderItemIfc
     */
    public FinancialCountTenderItemIfc getBaseCurrencyFinancialCountTenderTotals()
    {
        FinancialCountTenderItemIfc currentItem = null;
        int numItems = tenderItems.size();
        String baseCurrencyCode = baseCurrencyType.getCountryCode();
        List<FinancialCountTenderItemIfc> baseTenders = new ArrayList<FinancialCountTenderItemIfc>(tenderItems.size());

        // loop through tenderItems and collect the base currency ones
        for (int i = 0; i < numItems; i++)
        {
            currentItem = tenderItems.get(i);

            if (currentItem.getAmountTotal().getCountryCode().equals(baseCurrencyCode))
            {
                baseTenders.add(currentItem);
            }
        }

        // now add up all the tenders
        FinancialCountTenderItemIfc tempItem = instantiateFinancialCountTenderItemIfc();
        for (Iterator<FinancialCountTenderItemIfc> iterTenders = baseTenders.iterator(); iterTenders.hasNext();)
        {
            currentItem = iterTenders.next();
            // only count the summaries
            if (currentItem.isSummary())
            {
                tempItem.addAmountIn(currentItem.getAmountIn());
                tempItem.addAmountOut(currentItem.getAmountOut());
                tempItem.addNumberItemsIn(currentItem.getNumberItemsIn());
                tempItem.addNumberItemsOut(currentItem.getNumberItemsOut());
            }
        }


        return tempItem;
    }

    /**
     * Retrieves FinancialCountTenderItemIfc array with one entry per alternate
     * currency. The base (local) currency is excluded.
     * 
     * @return FinancialCountTenderItemIfc[] list of totals by currency
     */
    public FinancialCountTenderItemIfc[] getAlternateCurrencyFinancialCountTenderTotals()
    {
        Map<String,FinancialCountTenderItemIfc> totals = new HashMap<String,FinancialCountTenderItemIfc>(0);
        Map<String,List<FinancialCountTenderItemIfc>> tempMap = new HashMap<String,List<FinancialCountTenderItemIfc>>(0);
        FinancialCountTenderItemIfc currentItem = null;
        FinancialCountTenderItemIfc tempTotalItem = null;
        int numItems = tenderItems.size();
        String baseCurrencyCode = baseCurrencyType.getCountryCode();
        String currencyCode = null;
        List<FinancialCountTenderItemIfc> tempList = null;
        int currencyID = 0;

        // loop through tenderItems and group them into currencyCodes
        for (int i = 0; i < numItems; i++)
        {
            currentItem = tenderItems.get(i);
            currencyCode = currentItem.getAmountTotal().getCountryCode();
            if (totals.containsKey(currencyCode))
            {
                tempList = tempMap.get(currencyCode);
            }
            else
            {
                tempList = new ArrayList<FinancialCountTenderItemIfc>();
                tempMap.put(currencyCode, tempList);
            }
            tempList.add(currentItem);
        }
        // loop through the organized tenderitems and add them
        for (Iterator<String> iter = tempMap.keySet().iterator(); iter.hasNext();)
        {
            currencyCode = iter.next();
            tempList = tempMap.get(currencyCode);
            for (Iterator<FinancialCountTenderItemIfc> iterTenders = tempList.iterator(); iterTenders.hasNext();)
            {
                currentItem = iterTenders.next();
                // ignore the summaries unless it was the only one
                if (!currentItem.isSummary() || tempList.size() == 1)
                {
                    currencyID = currentItem.getAmountTotal().getType().getCurrencyId();

                    if (!currencyCode.equals(baseCurrencyCode))
                    {
                        if (totals.containsKey(currencyCode))
                        {
                            tempTotalItem = totals.get(currencyCode);
                        }
                        else
                        {
                            tempTotalItem = instantiateFinancialCountTenderItemIfc(currencyCode, currencyID);
                            totals.put(currencyCode, tempTotalItem);
                        }
                        tempTotalItem.addAmountIn(currentItem.getAmountIn());
                        tempTotalItem.addAmountOut(currentItem.getAmountOut());
                        tempTotalItem.addNumberItemsIn(currentItem.getNumberItemsIn());
                        tempTotalItem.addNumberItemsOut(currentItem.getNumberItemsOut());
                    }
                }
            }
        }

        FinancialCountTenderItemIfc totalsArray[] = new FinancialCountTenderItemIfc[totals.size()];
        totals.values().toArray(totalsArray);
        return totalsArray;
    }

    /**
     * Retrieves FinancialCountTenderItemIfc array with one entry per currency.
     * 
     * @return FinancialCountTenderItemIfc[] list of totals by currency
     */
    public FinancialCountTenderItemIfc[] getFinancialCountTenderTotalsByCurrency()
    {
        FinancialCountTenderItemIfc alternates[] = getAlternateCurrencyFinancialCountTenderTotals();
        List<FinancialCountTenderItemIfc> arrayList = new ArrayList<FinancialCountTenderItemIfc>(alternates.length + 1);
        arrayList.add(0, getBaseCurrencyFinancialCountTenderTotals());
        arrayList.addAll(Arrays.asList(alternates));
        return arrayList.toArray(new FinancialCountTenderItemIfc[alternates.length + 1]);
    }

    /**
     * Retrieves FinancialCountTenderItemIfc objects that are summaries
     * description. If no match is found, null is returned.
     * 
     * @return FinancialCountTenderItemIfc[] object if found; null if not found
     */
    public FinancialCountTenderItemIfc[] getSummaryTenderItems()
    {
        List<FinancialCountTenderItemIfc> listFCTI = new ArrayList<FinancialCountTenderItemIfc>();
        FinancialCountTenderItemIfc listFinancialCountTenderItem = null;

        // loop through tenderItems
        for (int i = 0; i < tenderItems.size(); i++)
        {
            // pull Tender from vector
            listFinancialCountTenderItem = tenderItems.get(i);
            // if match found, add it to list
            if (listFinancialCountTenderItem.isSummary())
            {
                listFCTI.add(listFinancialCountTenderItem);
            }
        }

        FinancialCountTenderItemIfc[] ftci = null;

        if (listFCTI.size() > 0)
        {
            ftci = listFCTI.toArray(new FinancialCountTenderItemIfc[listFCTI.size()]);
        }

        return (ftci);
    }

    /**
     * Retrieves FinancialCountTenderItemIfc objects that are details matching
     * specified Summary description. If no match is found, null is returned.
     * 
     * @param desc description of requested item
     * @return FinancialCountTenderItemIfc object if found; null if not found
     */
    public FinancialCountTenderItemIfc[] getDetailTenderItemBySummaryDescription(String desc)
    {
        List<FinancialCountTenderItemIfc> listFCTI = new ArrayList<FinancialCountTenderItemIfc>();
        FinancialCountTenderItemIfc listFinancialCountTenderItem = null;
        // loop through tenderItems
        for (int i = 0; i < tenderItems.size(); i++)
        {
            // pull Tender from vector
            listFinancialCountTenderItem = tenderItems.get(i);
            // if match found, add it to list
            if (listFinancialCountTenderItem.getSummaryDescription().equals(desc))
            {
                listFCTI.add(listFinancialCountTenderItem);
            }
        }

        FinancialCountTenderItemIfc[] ftci = null;

        if (listFCTI.size() > 0)
        {
            ftci = listFCTI.toArray(new FinancialCountTenderItemIfc[listFCTI.size()]);
        }

        return (ftci);
    }

    /**
     * Retrieves FinancialCountTenderItemIfc objects that are details matching
     * specified Summary tender descriptor. If no match is found, null is
     * returned.
     * 
     * @param td The TenderDescriptorIfc of requested item
     * @return FinancialCountTenderItemIfc object if found; null if not found
     */
    public FinancialCountTenderItemIfc[] getDetailTenderItemBySummaryDescription(TenderDescriptorIfc td)
    {
        List<FinancialCountTenderItemIfc> listFCTI = new ArrayList<FinancialCountTenderItemIfc>();
        FinancialCountTenderItemIfc listFinancialCountTenderItem = null;
        // loop through tenderItems
        for (int i = 0; i < tenderItems.size(); i++)
        {
            // pull Tender from vector
            listFinancialCountTenderItem = tenderItems.get(i);
            // if match found, add it to list
            if (listFinancialCountTenderItem.getTenderDescriptor().equals(td))
            {
                listFCTI.add(listFinancialCountTenderItem);
            }
        }

        FinancialCountTenderItemIfc[] ftci = null;

        if (listFCTI.size() > 0)
        {
            ftci = listFCTI.toArray(new FinancialCountTenderItemIfc[listFCTI.size()]);
        }

        return (ftci);
    }

    /**
     * Retrieves count type flag (see FinancialCountIfc).
     * 
     * @return countType flag (see FinancialCountIfc)
     */
    public int getCountType()
    {
        return (countType);
    }

    /**
     * Sets count type flag (see FinancialCountIfc).
     * 
     * @param value countType flag (see FinancialCountIfc)
     */
    public void setCountType(int value)
    {
        countType = value;
    }

    /**
     * Retrieves tender items array, loading tenderItems array
     * <code>tenderItems</code> from <code>tenderItems</code>.
     * 
     * @return tenderItems
     */
    public FinancialCountTenderItemIfc[] getTenderItems()
    {
        return tenderItems.toArray(new FinancialCountTenderItemIfc[tenderItems.size()]);
    }

    /**
     * Sets tender items array, loading tenderItems array
     * <code>tenderItems</code> into <code>tenderItems</code>.
     * 
     * @param value tenderItems
     */
    public void setTenderItems(FinancialCountTenderItemIfc[] value)
    {
        tenderItems = new ArrayList<FinancialCountTenderItemIfc>(value.length);

        for (int i = 0; i < value.length; i++)
        {
            tenderItems.add(value[i]);
        }
    }

    /**
     * Returns tender items in iterator.
     * 
     * @return tender items iterator
     */
    public Iterator<FinancialCountTenderItemIfc> getTenderItemsIterator()
    {
        return (tenderItems.iterator());
    }

    /**
     * This method converts hard totals information to a comma delimited String.
     * 
     * @return String
     */
    public void getHardTotalsData(HardTotalsBuilderIfc builder)
    {
        builder.appendStringObject(getClass().getName());
        builder.appendInt(countType);

        int len = 0;
        FinancialCountTenderItemIfc[] tenderItems = getTenderItems();

        if (tenderItems != null)
        {
            len = tenderItems.length;
        }
        builder.appendInt(len);

        for (int i = 0; i < len; i++)
        {
            tenderItems[i].getHardTotalsData(builder);
        }
    }

    /**
     * This method populates this object from a comma delimited string.
     * 
     * @param String String containing hard totals data.
     */
    public void setHardTotalsData(HardTotalsBuilderIfc builder) throws HardTotalsFormatException
    {
        // Get type field
        countType = builder.getIntField();
        // Get the financial count tender items
        int number = builder.getIntField();

        for (int i = 0; i < number; i++)
        {
            FinancialCountTenderItemIfc count = (FinancialCountTenderItemIfc)builder.getFieldAsClass();
            count.setHardTotalsData(builder);
            addTenderItem(count);
        }
    }

    /**
     * Returns count type descriptor string, checking for invalid countType
     * value.
     * 
     * @return String countType descriptor
     * @see oracle.retail.stores.domain.financial.FinancialCountIfc#COUNT_TYPE_DESCRIPTORS
     */
    public String countTypeToString()
    {
        String strResult;
        try
        {
            strResult = FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[countType];
        }
        // if not valid value, say unknown
        catch (ArrayIndexOutOfBoundsException e)
        {
            strResult = "Unknown [" + countType + "]";
        }
        return (strResult);
    }

    /**
     * Returns count type determined by string.
     * 
     * @param str count type string
     * @return count type
     */
    public static int stringToCountType(String str)
    {
        int countType = FinancialCountIfc.COUNT_TYPE_INVALID;

        for (int i = 0; i < FinancialCountIfc.COUNT_TYPE_DESCRIPTORS.length; i++)
        {
            if (str.equals(FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[i]))
            {
                // if match found, set count type and exit loop
                countType = i;
                i = FinancialCountIfc.COUNT_TYPE_DESCRIPTORS.length;
            }
        }

        return (countType);
    }

    /**
     * This method takes a FinancialCount object and returns a new
     * FinancialCount object with the specified tender summed up. This is used
     * primarily for Cash tenders that have denominations and the total amount
     * of cash is desired.
     * 
     * @param fcIn FinancialCount object containing tender items to be summed.
     * @param td The desired tender to be summed. (@see TenderDescriptorIfc)
     */
    public static FinancialCountIfc sumFinancialCountTender(FinancialCountIfc fcIn, TenderDescriptorIfc descriptor)
    {
        FinancialCountTenderItemIfc[] fcti = fcIn.getTenderItems();
        FinancialCountTenderItemIfc summedTender = null;
        List<FinancialCountTenderItemIfc> tenderList = new ArrayList<FinancialCountTenderItemIfc>();

        for (int i = 0; i < fcti.length; i++)
        {
            // if this matches the tender we want to add up
            if (fcti[i].getTenderDescriptor().equals(descriptor))
            {
                if (summedTender == null)
                {
                    summedTender = (FinancialCountTenderItemIfc)fcti[i].clone();
                    TenderDescriptorIfc td = summedTender.getTenderDescriptor();
                    td.setTenderSubType("");
                    summedTender.setTenderDescriptor(td);
                    summedTender.setDescription(tenderTypeMap.getDescriptor(td.getTenderType()));
                    summedTender.setSummaryDescription("");
                }
                else
                {
                    // add up amounts
                    summedTender.setAmountIn(summedTender.getAmountIn().add(fcti[i].getAmountIn()));
                    summedTender.setAmountOut(summedTender.getAmountOut().add(fcti[i].getAmountOut()));
                }
            }
            // just add the tender to the list if we are not summing it.
            else
            {
                tenderList.add(fcti[i]);
            }
        }

        // add the summed tender to the list (if we actually have a summed
        // tender)
        if (summedTender != null)
        {
            tenderList.add(summedTender);
        }

        // convert ArrayList back to array and return
        FinancialCountTenderItemIfc[] fctiOut = new FinancialCountTenderItemIfc[tenderList.size()];
        FinancialCountIfc fcOut = (FinancialCountIfc)fcIn.clone();
        fcOut.setTenderItems(tenderList.toArray(fctiOut));
        return fcOut;
    }
    
    /**
     * Returns an array of TillAdjustmentReportItem objects which contains 
     * FinancialCountTenderItemIfc objects that are details
     * matching all specified Summary tender descriptor.
     */
    public TillAdjustmentReportItem[] getAllDetailTenderItems()
    {
    	List<TillAdjustmentReportItem> details = new ArrayList<TillAdjustmentReportItem>();
    	 
    	FinancialCountTenderItemIfc[] sumTenders = getSummaryTenderItems();
    	if(sumTenders != null)
        {
            for(int cnt = 0; cnt < sumTenders.length; cnt++)
            {
                FinancialCountTenderItemIfc sumTender = sumTenders[cnt];
                TenderDescriptorIfc td = sumTender.getTenderDescriptor();
            
                FinancialCountTenderItemIfc[] detTenders =
                    getDetailTenderItemBySummaryDescription(td);
            
                if(detTenders != null)
                {
                	TillAdjustmentReportItem tillAdjItem = new TillAdjustmentReportItem(sumTender);
                	for (FinancialCountTenderItemIfc detTender: detTenders){
                		if (!detTender.isSummary())
                		{
                			tillAdjItem.addDetailTender(detTender);
                		}
                	}
                	details.add(tillAdjItem);
                }
            }         
        }        
        return details.toArray(new TillAdjustmentReportItem[details.size()]);                        
    }
    
    /**
     * Method to default display string function.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        // result string
        StringBuilder strResult = new StringBuilder("Class:  FinancialCount (Revision ");
        strResult.append(getRevisionNumber()).append(") @").append(hashCode());
        strResult.append("\n");
        // add attributes to string
        strResult.append("countType                               [").append(countTypeToString()).append("]\n");

        if (getTenderItems().length == 0)
        {
            strResult.append("tenderItems:                            [none]\n");
        }
        else
        {
            FinancialCountTenderItemIfc t = null;

            for (int i = 0; i < tenderItems.size(); i++)
            {
                t = tenderItems.get(i);
                strResult.append("Financial Count Tender Item ").append(i + 1).append(": \nSub").append(t.toString());
            }
        }
        // pass back result
        return strResult.toString();
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    /**
     * FinancialCount main method.
     * 
     * @param String args[] command-line parameters
     */
    public static void main(String args[])
    {
        // instantiate class
        FinancialCount clsFinancialCount = new FinancialCount();
        // output toString()
        System.out.println(clsFinancialCount.toString());
        try
        {
            HardTotalsStringBuilder builder = null;
            // instantiate class
            FinancialCount a1 = new FinancialCount();
            a1.setCountType(COUNT_TYPE_SUMMARY);
            builder = new HardTotalsStringBuilder();
            a1.getHardTotalsData(builder);
            Serializable obj = builder.getHardTotalsOutput();
            builder.setHardTotalsInput(obj);
            FinancialCountIfc a2 = (FinancialCountIfc)builder.getFieldAsClass();
            a2.setHardTotalsData(builder);
            if (a1.equals(a2))
            {
                System.out.println("Empty FinancialCountes are equal");
            }
            else
            {
                System.out.println("Empty FinancialCountes are NOT equal");
                System.out.println("FCTI 1 = " + a1.toString());
                System.out.println("FCTI 2 = " + a2.toString());
            }

            // instantiate class
            setTestData(a1);
            builder = new HardTotalsStringBuilder();
            a1.getHardTotalsData(builder);
            obj = builder.getHardTotalsOutput();
            builder.setHardTotalsInput(obj);
            a2 = (FinancialCountIfc)builder.getFieldAsClass();
            a2.setHardTotalsData(builder);

            if (a1.equals(a2))
            {
                System.out.println("Full FinancialCountes are equal");
            }
            else
            {
                System.out.println("Full FinancialCountes are NOT equal");
                System.out.println("FCTI 1 = " + a1.toString());
                System.out.println("FCTI 2 = " + a2.toString());
            }
        }
        catch (HardTotalsFormatException iae)
        {
            System.out.println("FinancialCount convertion failed:");
            iae.printStackTrace();
        }
    }

    /**
     * Sets test data in a financial count object.
     * 
     * @param FinancialCountIfc the financial count object
     */
    public static void setTestData(FinancialCountIfc fc)
    {
        fc.setCountType(COUNT_TYPE_DETAIL);
        FinancialCountTenderItem fcti = new FinancialCountTenderItem();
        fcti.setNumberItemsIn(8);
        fcti.setNumberItemsOut(2);
        fcti.setAmountIn(DomainGateway.getBaseCurrencyInstance("133.47"));
        fcti.setAmountOut(DomainGateway.getBaseCurrencyInstance("22.27"));
        fcti.setDescription("Credit");
        fcti.setSummaryDescription("Visa");
        fcti.setSummary(true);
        fc.addTenderItem(fcti);

        fcti = new FinancialCountTenderItem();
        fcti.setNumberItemsIn(10);
        fcti.setNumberItemsOut(1);
        fcti.setAmountIn(DomainGateway.getBaseCurrencyInstance("433.66"));
        fcti.setAmountOut(DomainGateway.getBaseCurrencyInstance("2.99"));
        fcti.setDescription("Cash");
        fcti.setSummaryDescription("");
        fcti.setSummary(false);
        fc.addTenderItem(fcti);
        fc.getTenderItems();
    }
}
