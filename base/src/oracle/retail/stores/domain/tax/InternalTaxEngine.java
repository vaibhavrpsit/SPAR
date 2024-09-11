/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/InternalTaxEngine.java /main/19 2014/01/24 16:58:49 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  01/24/14 - fix null dereferences
 *    abondala  09/04/13 - initialize collections
 *    abondala  05/07/13 - for price adjustments, prorated tax calculator
 *                         should not be called.
 *    sgu       09/05/12 - use aggregator to calculate combined order
 *                         transaction tax
 *    sgu       09/05/12 - refactor transaction tax transformation
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    sgu       09/22/11 - add send package to tax adjustment
 *    sgu       08/02/11 - adjust penny for tax amount
 *    blarsen   07/15/11 - Fix misspelled word: retrival
 *    cgreene   03/23/11 - XbranchMerge cgreene_shippingtax from main
 *    cgreene   03/18/11 - XbranchMerge cgreene_124_receipt_quick_wins from
 *                         main
 *    cgreene   03/16/11 - implement You Saved feature on reciept and
 *                         AllowMultipleQuantity parameter
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 11   360Commerce 1.10        8/7/2007 11:48:28 AM   Alan N. Sinton  CR 28170:
 *    Removed condition that checked if taxable item is not a Kit Header when
 *    doing a tax exempt tax calculation.
 10   360Commerce 1.9         5/14/2007 6:08:34 PM   Sandy Gu        update
 *    inclusive information in financial totals and history tables
 9    360Commerce 1.8         5/7/2007 2:21:04 PM    Sandy Gu        enhance
 *    shipping method retrieval and internal tax engine to handle tax rules
 8    360Commerce 1.7         4/30/2007 5:38:35 PM   Sandy Gu        added api
 *    to handle inclusive tax
 7    360Commerce 1.6         4/2/2007 5:50:05 PM    Snowber Khan    CR 25856 -
 *     Updating to preserve "tax exempt amount" for record keeping - without
 *    treating it as a charged tax., CR 25856 - Updated to handle exemption of
 *     default tax rules.
 *
 6    360Commerce 1.5         2/13/2006 4:06:25 PM   Edward B. Thorne Merge
 *    from InternalTaxEngine.java, Revision 1.3.1.0
 5    360Commerce 1.4         2/9/2006 4:12:58 PM    Rohit Sachdeva  10589:
 *    Crash being fixed
 4    360Commerce 1.3         1/25/2006 4:11:04 PM   Brett J. Larsen merge
 *    7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 3    360Commerce 1.2         3/31/2005 4:28:24 PM   Robert Pearse
 2    360Commerce 1.1         3/10/2005 10:22:09 AM  Robert Pearse
 1    360Commerce 1.0         2/11/2005 12:11:26 PM  Robert Pearse
 *:
 4    .v700     1.2.1.0     1/6/2006 12:37:30      Deepanshu       CR 6017:
 *    Calculate and save tax exempt
 3    360Commerce1.2         3/31/2005 15:28:24     Robert Pearse
 2    360Commerce1.1         3/10/2005 10:22:09     Robert Pearse
 1    360Commerce1.0         2/11/2005 12:11:26     Robert Pearse
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.factory.DomainObjectFactoryIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;

/**
 * @version 1.0
 * @created 13-Apr-2004 6:40:27 PM
 */
public class InternalTaxEngine implements InternalTaxEngineIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 5052346861071900704L;


    /**
     * Default constructor
     */
    public InternalTaxEngine()
    {

    }

    /**
     * This is the entry point of the tax engine, used to calculate tax on items.
     *
     * @param lineItems Line items to be taxed
     * @param totals This is populated with totals tax informatoin
     * @param transactionTax This is populated with transactionTax info
     * @see oracle.retail.stores.domain.tax.TaxEngineIfc#calculateTax(oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc[], oracle.retail.stores.domain.transaction.TransactionTotalsIfc, oracle.retail.stores.domain.transaction.TransactionTaxIfc)
     */
    public void calculateTax(TaxLineItemInformationIfc[] lineItems, TransactionTotalsIfc totals,
            TransactionTaxIfc transactionTax)
    {
        if(transactionTax == null)
        {
           transactionTax = DomainGateway.getFactory().getTransactionTaxInstance();
        }
        if(totals == null)
        {
            totals = DomainGateway.getFactory().getTransactionTotalsInstance();
        }
        totals.getTaxInformationContainer().reset();

        TaxLineItemInformationIfc[] taxableLineItems = collectTaxableLineItems(lineItems, totals);
        TaxRuleItemContainerIfc[] taxRuleItemContainer = collectTaxRulesAddItems(taxableLineItems, transactionTax);

        //order tax rules
        taxRuleItemContainer = orderTaxRules(taxRuleItemContainer);

        for(int i = 0; i < taxRuleItemContainer.length; i++)
        {
            taxRuleItemContainer[i].calculateTax(totals);
        }

        // Do penny adjustment of the tax amount
        adjustTax(taxableLineItems, totals, transactionTax);

        for(int i = 0; i < taxableLineItems.length; i++)
        {
            postTaxCalculation(taxableLineItems[i]);
        }

        postTaxCalculation(totals);

    }

    /**
     * This is the entry point of the tax engine, used to calculate exempt tax on items.
     *
     * @param lineItems TaxLineItemInformationIfc the array of Line items to be exempted for tax
     * @param totals TransactionTotalsIfc This is populated with totals tax informatoin
     * @param transactionTax TransactionTaxIfc This is populated with transactionTax info
     */
    public void calculateExepmtTax(TaxLineItemInformationIfc[] lineItems, TransactionTotalsIfc totals,
            TransactionTaxIfc transactionTax)
    {
        if(transactionTax == null)
        {
           transactionTax = DomainGateway.getFactory().getTransactionTaxInstance();
        }
        if(totals == null)
        {
            totals = DomainGateway.getFactory().getTransactionTotalsInstance();
        }
        totals.getTaxInformationContainer().reset();

        TaxRuleItemContainerIfc[] taxRuleItemContainer = collectTaxRulesAddItemsForExemptTax(lineItems, transactionTax);

        //order tax rules
        taxRuleItemContainer = orderTaxRules(taxRuleItemContainer);

        for(int i = 0; i < taxRuleItemContainer.length; i++)
        {
            taxRuleItemContainer[i].calculateExepmtTax(totals);
        }

        // Do penny adjustment of the tax amount
        adjustTax(lineItems, totals, transactionTax);

        TaxInformationContainerIfc taxInformationContainer = totals.getTaxInformationContainer();
        totals.setExemptTaxTotal(taxInformationContainer.getTaxExemptAmount());
    }

    /**
     * Any operations that need to be done after the tax is calculated are put here
     *
     * @param item
     * @see oracle.retail.stores.domain.tax.InternalTaxEngineIfc#postTaxCalculation(oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc)
     */
    public void postTaxCalculation(TaxLineItemInformationIfc item)
    {
        item.setItemTaxAmount(item.getTaxInformationContainer().getTaxAmount());
        item.setItemInclusiveTaxAmount(item.getTaxInformationContainer().getInclusiveTaxAmount());
        item.setTaxScope(item.getTaxInformationContainer().getTaxScope());
    }

    /**
     * Any operations that need to be done on the transaction after tax is calculated can be done here
     *
     * @param totals
     * @see oracle.retail.stores.domain.tax.InternalTaxEngineIfc#postTaxCalculation(oracle.retail.stores.domain.transaction.TransactionTotalsIfc)
     */
    public void postTaxCalculation(TransactionTotalsIfc totals)
    {
        TaxInformationContainerIfc taxInformationContainer = totals.getTaxInformationContainer();
        totals.setTaxTotal(taxInformationContainer.getTaxAmount());
        totals.setInclusiveTaxTotal(taxInformationContainer.getInclusiveTaxAmount());
        totals.setTaxTotalUI(taxInformationContainer.getTaxAmount());
    }

    /**
     * Sort the tax rules so compound taxes and such are computed correctly
     *
     * @param taxRuleItemContainer
     * @return
     * @see oracle.retail.stores.domain.tax.InternalTaxEngineIfc#orderTaxRules(oracle.retail.stores.domain.tax.TaxRuleItemContainerIfc[])
     */
    public TaxRuleItemContainerIfc[] orderTaxRules(TaxRuleItemContainerIfc[] taxRuleItemContainer)
    {
        Arrays.sort(taxRuleItemContainer, new TaxRuleComparator());
        return taxRuleItemContainer;
    }

    /**
     * Collect tax rules into appropriate groups for calculation
     *
     * @param lineItems
     * @param transactionTax
     * @return Array of tax rules
     * @see oracle.retail.stores.domain.tax.InternalTaxEngineIfc#collectTaxRulesAddItems(oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc[], oracle.retail.stores.domain.transaction.TransactionTaxIfc)
     */
    public TaxRuleItemContainerIfc[] collectTaxRulesAddItems(TaxLineItemInformationIfc[] lineItems,
                TransactionTaxIfc transactionTax)
    {
        DomainObjectFactoryIfc factory = DomainGateway.getFactory();
        HashMap<String,TaxRuleItemContainerIfc> rules = new HashMap<String,TaxRuleItemContainerIfc>(1);

        RunTimeTaxRuleIfc[] taxRules = transactionTax.getActiveTaxRules();
        boolean haveTransactionTaxRules = false;
        TaxRuleItemContainer[] transactionTaxRuleItemContainer = null;

        if(taxRules != null && taxRules.length > 0)
        {
            haveTransactionTaxRules = true;
            for(int i = 0; i < taxRules.length; i++)
            {
                TaxRuleItemContainerIfc taxRuleItemContainer = factory.getTaxRuleItemContainerInstance();
                taxRuleItemContainer.setTaxRule(taxRules[i]);
                taxRuleItemContainer.setTaxScope(TaxConstantsIfc.TAX_SCOPE_TRANSACTION);
                rules.put(taxRules[i].getUniqueID(), taxRuleItemContainer);
            }
            transactionTaxRuleItemContainer = rules.values().toArray(new TaxRuleItemContainer[0]);
        }

        //?? If have transaction override do have go through
        //still might for returns
        for( int i = 0; i < lineItems.length; i++)
        {
            boolean addRules = true;

            if(haveTransactionTaxRules)
            {
                addRules = lineItems[i].canTransactionOverrideTaxRules() == false;
            }

            if(addRules)
            {
                taxRules = lineItems[i].getActiveTaxRules();
                int taxScope = TaxConstantsIfc.TAX_SCOPE_ITEM;

                //If a line item tax rules are used to calculate transaction tax override,
                //its tax scope should be kept as TAX_SCOPE_TRANSACTION.
                if (transactionTax.useItemRulesForTaxOverride())
                {
                    boolean transactioScopeFlag = lineItems[i].getTaxScope() == TaxConstantsIfc.TAX_SCOPE_TRANSACTION;
                    boolean taxOverrideFlag = (lineItems[i].getTaxMode() == TaxConstantsIfc.TAX_MODE_OVERRIDE_AMOUNT) ||
                                              (lineItems[i].getTaxMode() == TaxConstantsIfc.TAX_MODE_OVERRIDE_RATE);

                    if (taxOverrideFlag && transactioScopeFlag)
                    {
                        taxScope = TaxConstantsIfc.TAX_SCOPE_TRANSACTION;
                    }
                }
                if (taxRules != null && taxRules.length > 0)
                {
                    for(int j = 0; j < taxRules.length; j++)
                    {
                        TaxRuleItemContainerIfc testRuleItemContainer = rules.get(taxRules[j].getUniqueID());
                        // Has rule already been used? If not add it
                        if(testRuleItemContainer == null)
                        {
                            testRuleItemContainer = factory.getTaxRuleItemContainerInstance();
                            testRuleItemContainer.setTaxRule(taxRules[j]);
                            testRuleItemContainer.addItem(lineItems[i], taxRules);
                            testRuleItemContainer.setTaxScope(taxScope);
                            rules.put(taxRules[j].getUniqueID(), testRuleItemContainer);
                        }
                        // Otherwise add this line item to be used with the rule
                        else
                        {
                            testRuleItemContainer.addItem(lineItems[i], taxRules);
                        }
                    }
                }
            }
            else if (haveTransactionTaxRules && transactionTaxRuleItemContainer != null)
            {
                for(int j = 0; j < transactionTaxRuleItemContainer.length; j++)
                {
                    transactionTaxRuleItemContainer[j].addItem(lineItems[i], transactionTax.getActiveTaxRules());
                }
            }

            lineItems[i].clearTaxAmounts();
        }
        return rules.values().toArray(new TaxRuleItemContainerIfc[0]);
    }

    /**
     * Collect tax rules into appropriate groups for calculation
     * @param lineItems TaxLineItemInformationIfc the array of Tax line items
     * @param transactionTax TransactionTaxIfc the transaction tax
     * @return TaxRuleItemContainerIfc the tax rule item container
     */
    protected TaxRuleItemContainerIfc[] collectTaxRulesAddItemsForExemptTax(TaxLineItemInformationIfc[] lineItems,
            TransactionTaxIfc transactionTax)
    {
        DomainObjectFactoryIfc factory = DomainGateway.getFactory();
        HashMap<String,TaxRuleItemContainerIfc> rules = new HashMap<String,TaxRuleItemContainerIfc>(1);

        RunTimeTaxRuleIfc[] taxRules = transactionTax.getActiveTaxRules();
        boolean haveTransactionTaxRules = false;
        TaxRuleItemContainer[] transactionTaxRuleItemContainer = null;

        if (taxRules != null && taxRules.length > 0)
        {
            haveTransactionTaxRules = true;
            for (int i = 0; i < taxRules.length; i++)
            {
                TaxRuleItemContainerIfc taxRuleItemContainer = factory.getTaxRuleItemContainerInstance();
                taxRuleItemContainer.setTaxRule(taxRules[i]);
                taxRuleItemContainer.setTaxScope(TaxConstantsIfc.TAX_SCOPE_TRANSACTION);
                rules.put(taxRules[i].getUniqueID(), taxRuleItemContainer);
            }
            transactionTaxRuleItemContainer = rules.values().toArray(
                    new TaxRuleItemContainer[0]);
        }

        for (int i = 0; i < lineItems.length; i++)
        {
            taxRules = ((SaleReturnLineItemIfc)lineItems[i]).getPLUItem().getTaxRules();
            // Use default tax rules if we don't find one, unless its a kit header which is expected
            // to have no rules (the individual kit items have the rules)
            if(taxRules == null)
            {
                taxRules = ((SaleReturnLineItemIfc)lineItems[i]).getItemPrice().getDefaultTaxRules();
            }
            // We have to keep the tax rules that are NOT used during tax exempt transaction
            // so that we can know what tax amount was exempted for reporting purposes.
            // We balance this by TaxByLineRule.calculateExepmtTax doesn't update
            // the actual tax amount, but only the tax exempted amount.
            if (taxRules != null && taxRules.length > 0)
            {
                for (int j = 0; j < taxRules.length; j++)
                {
                    TaxRuleItemContainerIfc testRuleItemContainer = rules.get(taxRules[j].getUniqueID());
                    // Has rule already been used? If not add it
                    if (testRuleItemContainer == null)
                    {
                        testRuleItemContainer = factory.getTaxRuleItemContainerInstance();
                        testRuleItemContainer.setTaxRule(taxRules[j]);
                        testRuleItemContainer.addItem(lineItems[i], taxRules);
                        testRuleItemContainer.setTaxScope(TaxConstantsIfc.TAX_SCOPE_ITEM);
                        rules.put(taxRules[j].getUniqueID(), testRuleItemContainer);
                    }
                    // Otherwise add this line item to be used with the rule
                    else
                    {
                        testRuleItemContainer.addItem(lineItems[i], taxRules);
                    }
                }
            }
            if (haveTransactionTaxRules && transactionTaxRuleItemContainer != null)
            {
                for (int j = 0; j < transactionTaxRuleItemContainer.length; j++)
                {
                    transactionTaxRuleItemContainer[j].addItem(lineItems[i], transactionTax.getActiveTaxRules());
                }
            }

            lineItems[i].clearTaxAmounts();
        }
        return rules.values().toArray(new TaxRuleItemContainerIfc[0]);
    }

    /**
     * Collect all taxable line items
     * @param lineItems transaction line items
     * @param totals transaction totals
     * @return an array of taxable line items of the transaction
     */
    protected TaxLineItemInformationIfc[] collectTaxableLineItems(TaxLineItemInformationIfc[] lineItems,
            TransactionTotalsIfc totals)
    {
        ArrayList<TaxLineItemInformationIfc> taxableLineItems = new ArrayList<TaxLineItemInformationIfc>();
        taxableLineItems.addAll(Arrays.asList(lineItems));

        return taxableLineItems.toArray(new TaxLineItemInformationIfc[taxableLineItems.size()]);
    }

    /**
     * Clone the tax engine
     *
     * @return
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        InternalTaxEngine newClass = new InternalTaxEngine();
        setCloneAttributes(newClass);
        return newClass;
    }

    /**
     * Set this classes attributes for cloning
     *
     * @param newClass
     */
    public void setCloneAttributes(InternalTaxEngine newClass)
    {
    }

    /**
     * Perform penny adjustment for tax at line item and transaction totals level
     * @param lineItems the line items
     * @param totals the transaction totlas
     * @param transactionTax the transaction tax
     */
    protected void adjustTax(TaxLineItemInformationIfc[] lineItems, TransactionTotalsIfc totals, TransactionTaxIfc transactionTax)
    {
        TaxInformationContainerIfc[] adjustedTaxInfoContainers = new TaxInformationContainerIfc[lineItems.length];
        HashMap<Integer, List<TaxInformationIfc>> roundingMap = new HashMap<Integer, List<TaxInformationIfc>>(1);
        int index = 0;
        for (TaxLineItemInformationIfc lineItem : lineItems)
        {
            TaxInformationContainerIfc taxInformationContainer = (TaxInformationContainerIfc)lineItem.getTaxInformationContainer().clone();
            adjustedTaxInfoContainers[index++] = taxInformationContainer;
            addTaxInformationsWhichNeedRounding(roundingMap, taxInformationContainer);
        }

        round(roundingMap);
        setRoundedTaxAmount(lineItems, totals, adjustedTaxInfoContainers);
    }


    /**
     * This method goes through all the {@link TaxInformationIfc}s in the container and adds
     * the ones to the <code>roundingMap</code> which have current amounts with more
     * significant digits than their currency types allow.
     * @param roundingMap map to be populated with tax informations that need rounding
     * @param taxInformationContainer the container that holds current tax information
     */
    protected void addTaxInformationsWhichNeedRounding(HashMap<Integer, List<TaxInformationIfc>> roundingMap, TaxInformationContainerIfc taxInformationContainer)
    {
        for (TaxInformationIfc taxInformation : taxInformationContainer.getTaxInformation())
        {
            // Do rounding only if its amount has more significant digits than its currency type allows
            if (taxInformation.getTaxAmount().getScale() > taxInformation.getTaxAmount().getType().getScale())
            {
                Integer roundingMode = new Integer(taxInformation.getTaxAmount().getRoundingMode());
                List<TaxInformationIfc> taxInformationList = null;
                if (roundingMap.get(roundingMode) == null)
                {
                    taxInformationList = new ArrayList<TaxInformationIfc>();
                    roundingMap.put(roundingMode, taxInformationList);
                }
                else
                {
                    taxInformationList = (List<TaxInformationIfc>)roundingMap.get(roundingMode);
                }

                taxInformationList.add(taxInformation);
            }
        }
    }

    /**
     * Rounds all the tax amount in the map
     * @param roundingMap the map
     */
    protected void round(HashMap<Integer, List<TaxInformationIfc>> roundingMap)
    {
        Set<Integer> roundingModeSet = roundingMap.keySet();
        for (Integer roundingMode : roundingModeSet)
        {
            List<TaxInformationIfc> taxInformationList = roundingMap.get(roundingMode);
            round(roundingMode, taxInformationList);
        }
    }

    /**
     * Rounds all the tax amount from the list
     * @param roundingMode the rounding mode
     * @param taxInformationList the tax information list
     */
    protected void round(int roundingMode, List<TaxInformationIfc> taxInformationList)
    {
        // Rounded nth tax amount = Round(Sum of n unrounded tax amount) - Sum of n-1 rounded tax amount

        // variable sumOfTax holds the sum of n unrounded tax amount
        CurrencyIfc sumOfTax = DomainGateway.getBaseCurrencyInstance();
        int scale = sumOfTax.getType().getScale();

        // variable sumOfRoundedTax holds the sum of n-1 rounded tax amount
        CurrencyIfc sumOfRoundedTax = DomainGateway.getBaseCurrencyInstance();

        for (TaxInformationIfc taxInfo : taxInformationList)
        {
            sumOfTax = sumOfTax.add(taxInfo.getTaxAmount());
            CurrencyIfc roundedSumOfTax = sumOfTax.multiply(BigDecimal.ONE, scale, roundingMode);   // round to the currency scale

            CurrencyIfc roundedTax = roundedSumOfTax.subtract(sumOfRoundedTax);
            taxInfo.clearTaxAmount();
            taxInfo.setTaxAmount(roundedTax);

            sumOfRoundedTax = sumOfRoundedTax.add(roundedTax);
        }
    }

    /**
     * Set the rounded tax amount back to the line item and transaction totals's tax containers
     * @param lineItems the line item
     * @param totals the transaction totals
     * @param adjustedTaxInfoContainers the tax containers with rounded tax amount
     */
    protected void setRoundedTaxAmount(TaxLineItemInformationIfc[] lineItems, TransactionTotalsIfc totals,
            TaxInformationContainerIfc[] adjustedTaxInfoContainers)
    {
        // reset total tax information container
        TaxInformationContainerIfc totalsTaxInfoContainer = totals.getTaxInformationContainer();
        int taxScope = totalsTaxInfoContainer.getTaxScope();
        CurrencyIfc exemptTaxAmount = (CurrencyIfc)totalsTaxInfoContainer.getTaxExemptAmount().clone();
        totalsTaxInfoContainer.reset();
        totalsTaxInfoContainer.setTaxScope(taxScope);

        int index = 0;
        for (TaxLineItemInformationIfc lineItem : lineItems)
        {
            // reset line item tax information container
            TaxInformationContainerIfc taxInfoContainer = lineItem.getTaxInformationContainer();
            taxScope = taxInfoContainer.getTaxScope();
            taxInfoContainer.reset();
            taxInfoContainer.setTaxScope(taxScope);

            TaxInformationIfc[] adjustedTaxInfoList = adjustedTaxInfoContainers[index++].getTaxInformation();

            for (TaxInformationIfc adjustedTaxInfo : adjustedTaxInfoList)
            {
                // set the adjusted tax amount to line item
                taxInfoContainer.addTaxInformation(adjustedTaxInfo);

                // set the adjusted tax amount to total tax
                totalsTaxInfoContainer.addTaxInformation((TaxInformationIfc)adjustedTaxInfo.clone());
            }
        }

        // round the exempt tax amount
        int scale = exemptTaxAmount.getType().getScale();
        int roundingMode = exemptTaxAmount.getRoundingMode();
        CurrencyIfc roundedExemptTaxAmount = exemptTaxAmount.multiply(BigDecimal.ONE, scale, roundingMode);    // round to the currency scale
        totalsTaxInfoContainer.addTaxExemptInformation(roundedExemptTaxAmount);
    }


    /**
     * Compare two tax rules. They are compared by order, which is the order the
     * Compare two tax rules. They are compared by order, which is the order the
     * tax rules will be executed in. For practical purposes, this is only
     * needed when using compound taxes, and then the order is going to be the
     *
     * $Revision: /main/19 $
     */
    protected class TaxRuleComparator implements Comparator<TaxRuleItemContainerIfc>
    {

        /**
         * Compare two tax rules
         *
         * @param cont0 rule 1
         * @param cont1 rule 2
         * @return compare value
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(TaxRuleItemContainerIfc cont0, TaxRuleItemContainerIfc cont1)
        {
            RunTimeTaxRuleIfc rule0 = cont0.getTaxRule();
            RunTimeTaxRuleIfc rule1 = cont1.getTaxRule();
            int ret = 0;
            if( rule0.getOrder() < rule1.getOrder())
            {
                ret = -1;
            }
            else if( rule0.getOrder() > rule1.getOrder())
            {
                ret = 1;
            }

            return ret;
        }

    }

}
