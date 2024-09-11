/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/ExciseTaxRule.java /main/14 2012/02/04 07:50:27 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     02/04/12 - Fix issues with loading tax rules when some rules
 *                         have logical errors.
 *    jswan     02/03/12 - XbranchMerge jswan_bug-13599093 from
 *                         rgbustores_13.4x_generic_branch
 *    jswan     01/30/12 - Modified to: 1) provide a more detailed log message
 *                         when a tax rule is invalid, and 2) allow valid tax
 *                         rules to load even if one or more other rules are
 *                         not valid.
 *    sgu       09/30/11 - change tax caculator api
 *    sgu       09/29/11 - set taxable line items to the tax calculator
 *    sgu       08/25/11 - fix taxable amount calculation in threshold tax rule
 *                         and the proration algorithm
 *    abhayg    03/18/11 - Added fix for invalid Tax Notice
 *    abhayg    03/18/11 - XbranchMerge abhayg_bug-11838289 from main
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  8    360Commerce 1.7         5/30/2007 9:03:22 AM   Anda D. Cadar   code
 *       cleanup
 *  7    360Commerce 1.6         4/25/2007 10:00:28 AM  Anda D. Cadar   I18N
 *       merge
 *  6    360Commerce 1.5         1/25/2006 4:11:00 PM   Brett J. Larsen merge
 *       7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *  5    360Commerce 1.4         1/22/2006 11:41:52 AM  Ron W. Haight   Removed
 *        references to com.ibm.math.BigDecimal
 *  4    360Commerce 1.3         12/13/2005 4:43:51 PM  Barry A. Pape
 *       Base-lining of 7.1_LA
 *  3    360Commerce 1.2         3/31/2005 4:28:07 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:21:32 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:10:57 PM  Robert Pearse
 *:
 *  5    .v700     1.2.1.1     9/28/2005 12:28:01     Jason L. DeLeau 4067: Fix
 *       the way a receipt prints for a price adjusted threshold item, where
 *       the price adjustment is below the threshold,
 *       and the original price is above it.
 *  7    .v710     1.2.1.0.1.2 11/15/2005 18:59:12    Charles Suehs   Record
 *       taxableAmount instead of startingTaxableAmount in TaxInfoContainer.
 *  6    .v710     1.2.1.0.1.1 10/25/2005 17:22:09    Charles Suehs   Merge
 *       from v700
 *  5    .v710     1.2.1.0.1.0 10/24/2005 15:47:43    Charles Suehs   Merge
 *       from ExciseTaxRule.java, Revision 1.2.1.1
 *  4    .v700     1.2.1.0     5/19/2005 10:07:36     Jason L. DeLeau Fix
 *       threshold tax being non-zero in some cases, when it should be zero.
 *  3    360Commerce1.2         3/31/2005 15:28:07     Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:21:32     Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:10:57     Robert Pearse
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;

import org.apache.log4j.Logger;

/**
 * Excise tax rule. This tax rule calculates tax above a threshold amount. It
 * either calculates based on the entire amount or the amount above the
 * threshold. This is controlled by the tax entire amount flag.
 */

public class ExciseTaxRule extends AbstractTaxRule implements ExciseTaxRuleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4663789447002044796L;

    protected static Logger logger = Logger.getLogger(oracle.retail.stores.domain.tax.ExciseTaxRule.class);

    /**
     * Threshold amount on the threshold tax, don't tax unless above this amount.
     */
    private CurrencyIfc thresholdAmount;

    /**
     * Flag - tax entire amount or only tax amount minus the threshold
     */
    private boolean taxEntireAmount = true;

    /**
     * Set the calculation mode for eithre line item or group.
     */
    private boolean prorateFlag = false;

    /**
     * Constructs the Excise tax rule object.
     */
    public ExciseTaxRule()
    {

    }

    /**
     * Tests whether this tax rule is valid or not.
     *
     * @return true if valid false otherwise
     */
    public boolean isValid()
    {
        boolean valid = super.isValid();
        String message = null;

        if (valid)
        {
            if (thresholdAmount != null)
            {
                if (thresholdAmount.signum() == CurrencyIfc.NEGATIVE)
                {
                    valid = false;
                    message = getValidationErrorMessage("ExciseTaxRule is invalid since thresholdAmount is negative.");
                    logger.error(message);
                }
                else
                {
                    valid = true;
                }
            }
            else
            {
                message = getValidationErrorMessage("ExciseTaxRule is invalid since thresholdAmount is null.");
                logger.error(message);
            }
        }

        return valid;
    }

    /**
     * Determine whether or not this tax rule is active for
     * the passed in item
     *
     * @param item Item to add to this tax rule
     * @return true if the item is part of this tax rule, otherwise false.
     */
    public boolean isRuleActiveForItem(TaxLineItemInformationIfc item)
    {
        return super.isRuleActiveForItem(item);
    }

    /**
     * Clone a copy of this object <P>
     *
     * @return copy of this object
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        ExciseTaxRule newClass = new ExciseTaxRule();
        setCloneAttributes(newClass);
        return newClass;
    }

    /**
     * Set attributes for clone.
     * <P>
     *
     * @param newClass new instance of ExciseTaxRule
     */
    public void setCloneAttributes(ExciseTaxRule newClass)
    {
        super.setCloneAttributes(newClass);
        if (thresholdAmount != null)
        {
            newClass.thresholdAmount = (CurrencyIfc) thresholdAmount.clone();
        }
        else
        {
            newClass.thresholdAmount = null;
        }
        newClass.taxEntireAmount = taxEntireAmount;
        newClass.prorateFlag = this.prorateFlag;
    }

    /**
     * Calculate the excise tax
     *
     * @param items List of items to tax
     * @param totals totals object to put the tax total into
     * @see oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc#calculateTax(oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc[],oracle.retail.stores.domain.transaction.TransactionTotalsIfc)
     */
    public void calculateTax(TaxLineItemInformationIfc[] items, TransactionTotalsIfc totals)
    {

        if(!isProrated())
        {
            calculateLineItemTax(items, totals);
        }
        else
        {
            calculateProratedTax(items, totals);
        }
    }

    /**
     * Calculate the excise tax on a pro-rated basis (tax by group)
     *
     * @param items List of items to tax
     * @param totals totals object to put the tax total into
     */
    public void calculateProratedTax(TaxLineItemInformationIfc[] items, TransactionTotalsIfc totals)
    {
        // First get the taxable and tax amount for all of the positive items(Sale).
        List<TaxLineItemInformationIfc> taxableItems = new ArrayList<TaxLineItemInformationIfc>();
        CurrencyIfc taxableAmount = getItemTaxableAmount(items, CurrencyIfc.POSITIVE, taxableItems);
        CurrencyIfc taxAmount = calculateGroupTax(taxableAmount, taxableItems.toArray(new TaxLineItemInformationIfc[0]));

        // Next get the taxable and tax amount for all of the negitive items(returns).
        List<TaxLineItemInformationIfc> negativeTaxableItems = new ArrayList<TaxLineItemInformationIfc>();
        CurrencyIfc negativeTaxableAmount = getItemTaxableAmount(items, CurrencyIfc.NEGATIVE, negativeTaxableItems);
        CurrencyIfc negativeTaxAmount = calculateGroupTax(negativeTaxableAmount, negativeTaxableItems.toArray(new TaxLineItemInformationIfc[0]));

        // Calculate and update the taxable amounts for both negative and positive separately.
        // To combine them can cause threshold calculation errors.
        updateTotals(taxableAmount, taxAmount, totals);
        updateTotals(negativeTaxableAmount, negativeTaxAmount, totals);
    }

    /**
     * Update the totals object with the calculated taxes.
     *
     *  @param taxableAmount
     *  @param taxAmount
     *  @param totals
     */
    protected void updateTotals(CurrencyIfc taxableAmount, CurrencyIfc taxAmount, TransactionTotalsIfc totals)
    {
        if(taxableAmount.getDecimalValue().compareTo(BigDecimal.ZERO) != CurrencyIfc.EQUALS)
        {
            // Create the subtotal infomation for the sum of the sale and return taxable and tax amounts.
            TaxInformationIfc taxInformation = createTaxInformation(taxableAmount, taxAmount, TaxConstantsIfc.TAX_MODE_STANDARD);
            if(taxAmount.getDecimalValue().compareTo(BigDecimal.ZERO) == CurrencyIfc.EQUALS)
            {
            	taxInformation.setEffectiveTaxableAmount(DomainGateway.getBaseCurrencyInstance());
            }
            totals.getTaxInformationContainer().addTaxInformation(taxInformation);
        }
    }

    /**
     * Calculate the excise tax on a line item basis
     *
     * @param items List of items to tax
     * @param totals totals object to put the tax total into
     * @see oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc#calculateTax(oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc[], oracle.retail.stores.domain.transaction.TransactionTotalsIfc)
     */
    public void calculateLineItemTax(TaxLineItemInformationIfc[] items, TransactionTotalsIfc totals)
    {
        TaxInformationIfc itemTaxInformation = null;
        TaxInformationIfc taxInformation = createTaxInformation(TaxConstantsIfc.TAX_MODE_STANDARD);
        for(int i = 0; i < items.length; i++)
        {
            // Reinitialize the taxable and tax amount to zero, for each item in the loop
            CurrencyIfc taxableAmount = getItemTaxableAmount(items[i]);
            CurrencyIfc taxAmount = DomainGateway.getBaseCurrencyInstance();

            // Check to see if the taxable amount is above the threshold
            if(taxableAmount.getDecimalValue().compareTo(BigDecimal.ZERO) != CurrencyIfc.EQUALS)
            {
                TaxCalculatorIfc taxCalculator = getTaxCalculator();
                taxAmount = taxCalculator.calculateTaxAmount(taxableAmount, items[i]);
                itemTaxInformation =  createTaxInformation(taxableAmount, taxAmount, items[i].getTaxMode());
                taxInformation.add(itemTaxInformation);
                items[i].getTaxInformationContainer().addTaxInformation(itemTaxInformation);
            }
            else
            {
                // Items didnt meet the threshold criteria.  Still need to save the tax rules
                // associated with the item, for returns purposes.
                itemTaxInformation =  createTaxInformation(taxableAmount, taxAmount, items[i].getTaxMode());
                items[i].getTaxInformationContainer().addTaxInformation(itemTaxInformation);
            }

        }
        totals.getTaxInformationContainer().addTaxInformation(taxInformation);
    }

    /**
     * ProRate the tax across all the line items in a tax group
     *
     *  @param items Items to pro-rate across
     *  @param taxableAmount amount that is taxable across all items
     *  @param taxAmount amount to tax across all items
     */
    public void prorate(TaxLineItemInformationIfc[] items,
            CurrencyIfc taxableAmount, CurrencyIfc taxAmount)
    {
        CurrencyIfc itemTaxableAmount = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc itemTaxAmount = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc workingTaxAmount = (CurrencyIfc) taxAmount.clone();
        CurrencyIfc workingTaxableAmount = (CurrencyIfc) taxableAmount.clone();
        TaxInformationIfc itemTaxInformation = null;

        for (int i = 0; i < items.length; i++)
        {
            itemTaxableAmount = getItemTaxableAmount(items[i]);
            itemTaxAmount = workingTaxAmount.prorate(itemTaxableAmount, workingTaxableAmount);
            workingTaxAmount = workingTaxAmount.subtract(itemTaxAmount);
            workingTaxableAmount = workingTaxableAmount.subtract(itemTaxableAmount);
            itemTaxInformation =  createTaxInformation(itemTaxableAmount, itemTaxAmount, items[i].getTaxMode());
            items[i].getTaxInformationContainer().addTaxInformation(itemTaxInformation);
        }
    }

    /**
     * Get the total taxable amount for all positive or all negative items, this
     * is used in the taxByGroup scenario.
     *
     * @param items List of items to generate tax amounts for
     * @param sign Collect either CurrencyIfc.NEGATIVE or CurrencyIfc.POSITIVE Items
     * @return The sum total of the positive or negative amounts.
     */
    public CurrencyIfc getItemTaxableAmount(TaxLineItemInformationIfc[] items, int sign,
            List<TaxLineItemInformationIfc> taxableItems)
    {
        CurrencyIfc taxableAmount = DomainGateway.getBaseCurrencyInstance();

        for (int i = 0; i < items.length; i++)
        {
            CurrencyIfc itemTaxableAmount = getItemTaxableAmount(items[i]);

            if (itemTaxableAmount.signum() == sign)
            {
                taxableAmount = taxableAmount.add(itemTaxableAmount);
                taxableItems.add(items[i]);
            }
        }
        return taxableAmount;
    }

    /**
     * Get the taxable amount of the tax line item based on this rule
     *
     *  @param item the tax line item
     *  @return taxable amount
     */
    public CurrencyIfc getItemTaxableAmount(TaxLineItemInformationIfc item)
    {
        CurrencyIfc itemTaxableAmount = super.getItemTaxableAmount(item);
        CurrencyIfc thresholdAmount = getExtendedThresholdAmount(item); //A threshold amount is always positive

        if(itemTaxableAmount.abs().compareTo(thresholdAmount) != CurrencyIfc.GREATER_THAN)
        {
            return DomainGateway.getBaseCurrencyInstance();
        }
        else if (!getTaxEntireAmount())
        {
            // if the current item is a return item then the taxableamount
            // will be negative. We have to subtract the threshold amount.
            if (itemTaxableAmount.signum() == CurrencyIfc.NEGATIVE)
            {
                itemTaxableAmount = itemTaxableAmount.add(thresholdAmount);
            }
            else
            {
                itemTaxableAmount = itemTaxableAmount.subtract(thresholdAmount);
            }
        }
        return itemTaxableAmount;
    }

    /**
     * Calculate the tax for the list of items and pro-rate over the taxable amount.
     *
     *  @param items The list of items to pro-rate over
     *  @param taxableAmount The taxable amount for all these items
     *  @return The tax for all these items.
     */
    public CurrencyIfc calculateGroupTax(CurrencyIfc taxableAmount, TaxLineItemInformationIfc[] items)
    {
        CurrencyIfc taxAmount = DomainGateway.getBaseCurrencyInstance();

        if(taxableAmount.getDecimalValue().compareTo(BigDecimal.ZERO) != CurrencyIfc.EQUALS)
        {
             TaxCalculatorIfc taxCalculator = getTaxCalculator();
             taxAmount = taxCalculator.calculateTaxAmount(taxableAmount, items);
             prorate(items, taxableAmount, taxAmount);
        }
        else
        {
        	// Update the taxInfoContainer for each item below threshold, to show the 0 tax.
        	for(int i=0; i<items.length; i++)
        	{
        		//TaxInformationIfc itemTaxInformation =  createTaxInformation(startingTaxableAmount, taxAmount, items[i].getTaxMode());
                //15-Nov-2005 CR 7450
                TaxInformationIfc itemTaxInformation =  createTaxInformation(taxableAmount, taxAmount, items[i].getTaxMode());
        		items[i].getTaxInformationContainer().addTaxInformation(itemTaxInformation);
        	}
        }
        return taxAmount;
    }
    /**
     * Get the threshold amount.  Anything below this
     * amount is not taxed.
     *
     * @return Returns the thresholdAmount.
     */
    public CurrencyIfc getThresholdAmount()
    {
        return thresholdAmount;
    }

    /**
     * Set the threshold amount.  Anything below this
     * amount is not taxed.
     *
     * @param value The thresholdAmount to set.
     */
    public void setThresholdAmount(CurrencyIfc value)
    {
        thresholdAmount = value;
    }

    /**
     * Get the threshold amount of a tax line item.  Anything below this
     * amount is not taxed.
     *
     * @return the threshold amount.
     */
    public CurrencyIfc getExtendedThresholdAmount(TaxLineItemInformationIfc item)
    {
        BigDecimal quantityDecimal = new BigDecimal(item.getItemQuantity().toString());
        return getThresholdAmount().multiply(quantityDecimal).abs();
    }

    /**
     * Get the tax entire amount flag.  If true, the entire taxable
     * amount is taxed.  If false, the entire taxable amount is taxed,
     * less the threshold amount.
     *
     * @return Returns the taxEntireAmount.
     */
    public boolean getTaxEntireAmount()
    {
        return taxEntireAmount;
    }

    /**
     * Set the tax entire amount flag
     *
     * @param value The taxEntireAmount to set.
     */
    public void setTaxEntireAmount(boolean value)
    {
        taxEntireAmount = value;
    }

    /**
     * Set the prorated flag
     *
     * @param prorated whether or not this is a pro-rated tax
     */
    public void setProrated(boolean prorated)
    {
        this.prorateFlag = prorated;
    }

    /**
     * Determine whether or not a tax should be pro-rated.
     *
     *  @return true or false
     */
    public boolean isProrated()
    {
        return this.prorateFlag;
    }
}
