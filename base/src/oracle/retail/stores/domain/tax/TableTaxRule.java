/*===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/TableTaxRule.java /main/4 2014/07/24 15:23:28 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         07/22/14 - set tax authority name
* jswan       02/04/12 - Re add threshold pricing (table table rate).
* jswan       02/04/12 - XbranchMerge jswan_bug13599093-rework from
*                        rgbustores_13.4x_generic_branch
* jswan       02/04/12 - Fix issues with loading tax rules when some rules have
*                        logical errors.
* jswan       02/03/12 - XbranchMerge jswan_bug-13599093 from
*                        rgbustores_13.4x_generic_branch
* jswan       02/02/12 - Refactor to add treshold tax back into the tax table
*                        calculations.
* jswan       01/30/12 - Modified to: 1) provide a more detailed log message
*                        when a tax rule is invalid, and 2) allow valid tax
*                        rules to load even if one or more other rules are not
*                        valid.
* jswan       11/07/11 - Fixed issues with tax unit tests.
* jswan       10/27/11 - Added repeating tax table calculation for taxable
*                        amounts greater than the table upper limit.
* sgu         10/05/11 - fix tax junit tests
* sgu         10/04/11 - rework table tax using tax rules instead of calculator
* sgu         10/03/11 - add table tax rule
* sgu         10/03/11 - Creation
* ===========================================================================
*/

package oracle.retail.stores.domain.tax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;

import org.apache.log4j.Logger;

public class TableTaxRule extends AbstractTaxRule implements TableTaxRuleIfc
{
    private static final long serialVersionUID = -7320667247202359745L;

    /**
     * Set the calculation mode for either line item or group.
     */
    private boolean prorateFlag = false;

    /**
     * The tax rule
     */
    private transient TaxByLineRuleIfc tableLineTaxRule = null;

    /**
     * The tax table line items
     */
    private ArrayList<TaxTableLineItemIfc> taxTableLineItems = new ArrayList<TaxTableLineItemIfc>();

    /**
     * the logger
     */
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.domain.tax.TableTaxRule.class);

    /**
     * Tests whether the TaxTableCalculator is valid.
     * @return true if this TaxTableCalculator is valid, otherwise false
     */
    public boolean isValid()
    {
        boolean valid = true;
        String message = null;
        
        if (isProrated())
        {
            logger.warn("Table tax cannot be prorated; tax will be calculated on a sale return line item basis.");
        }
        
        TaxTableLineItemIfc[] sortedTaxTableLineItems = getSortedTaxTableLineItems();
        if(sortedTaxTableLineItems.length <= 0)
        {
            valid = false;
            message = getValidationErrorMessage("TaxTableRule is invalid since there is no tax table line item.");
            logger.error(message);
        }
        else
        {
            CurrencyIfc previousMaxValue = null;
            CurrencyIfc maxValue = null;
            int i = 0;
            for(TaxTableLineItemIfc taxTableLineItem : sortedTaxTableLineItems)
            {
                 boolean isLastItem = i == sortedTaxTableLineItems.length - 1;
                 previousMaxValue = maxValue;
                 maxValue = taxTableLineItem.getMaxTaxableAmount();

                 if ((maxValue == null) && !isLastItem)
                 {
                     valid = false;
                     message = getValidationErrorMessage("TaxTableRule is invalid since max value on tax table line item " + i + " is null.");
                     logger.error(message);
                 }
                 else if ((maxValue != null) && (maxValue.signum() == CurrencyIfc.NEGATIVE))
                 {
                     valid = false;
                     message = getValidationErrorMessage("TaxTableRule is invalid since max value on tax table line item " + i + " is negative.");
                     logger.error(message);
                 }
                 else if ((previousMaxValue != null) && (maxValue != null) && (previousMaxValue.compareTo(maxValue) != CurrencyIfc.LESS_THAN))
                 {
                     valid = false;
                     message = getValidationErrorMessage("TaxTableRule is invalid since max value on previous item is greater than or equal to the max value on line item " + i + ".");
                     logger.error(message);
                 }
                 else
                 {
                     valid = taxTableLineItem.getTaxCalculator().isValid();
                     if (!valid)
                     {
                         message = getValidationErrorMessage("TaxTableRule at line " + i + "is invalid because the calculator is not valid.");
                         logger.error(message);
                     }
                 }
                 if (!valid)
                 {
                     break;
                 }
                 else
                 {
                     i++;
                 }
            }
        }
        return valid;
    }
    
    /**
     * Calculate the table tax
     *
     * @param items List of items to tax
     * @param totals totals object to put the tax total into
     * @see oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc#calculateTax(oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc[],oracle.retail.stores.domain.transaction.TransactionTotalsIfc)
     */
    public void calculateTax(TaxLineItemInformationIfc[] lineItems, TransactionTotalsIfc totals)
    {
        // Sort the line items
        TaxTableLineItemIfc[] sortedArray = getSortedTaxTableLineItems();
        for(TaxLineItemInformationIfc lineItem: lineItems)
        {
            calculateItemTaxAmount(lineItem, totals, sortedArray);
        }
    }

    /**
     * Calculate the table tax for a single taxable line item. 
     * @param lineItem
     * @param totals
     */
    protected void calculateItemTaxAmount(TaxLineItemInformationIfc lineItem, TransactionTotalsIfc totals, TaxTableLineItemIfc[] TaxTableLineItemArray) 
    {
        // This method calculates the tax in two steps:
        //  1. Calculates the tax for the part of taxable amount which is greater than the highest 
        //     threshold amount in the table.  If taxable amount is less than the highest threshold 
        //     amount, it skips this step. 
        //
        //  2. Calculates the tax for the part of taxable amount which less than the highest threshold 
        //     amount.  If there is some tax amount available from step 1, it combines that tax amount
        //     with amount calculated in this step.

        // Get total and unit taxable amount; the tax table is set up by unit taxable amount, not by total
        // total line item taxable amount.
        CurrencyIfc totalTaxableAmount = getItemTaxableAmount(lineItem).abs();
        CurrencyIfc unitTaxableAmount  = getUnitTaxableAmount(totalTaxableAmount, lineItem.getItemQuantity());

        // Get the tax table line item with highest threshold value.
        TaxTableLineItem maxTaxTableLineItem = (TaxTableLineItem) TaxTableLineItemArray[TaxTableLineItemArray.length - 1];
        
        // Initialize the taxable amount to find in the table, and the pre-calculated amount.
        CurrencyIfc remainingTaxableAmount = totalTaxableAmount;
        CurrencyIfc lookupTaxableAmount    = unitTaxableAmount;
        CurrencyIfc preCalculatedTaxAmount = DomainGateway.getBaseCurrencyInstance();
        
        // This section performs step 1. from above.  If the the highest threshold tax amount is equal to 
        // null, then the table does not repeat, and this step is skipped.
        if(maxTaxTableLineItem.getMaxTaxableAmount() != null &&
           maxTaxTableLineItem.getMaxTaxableAmount().signum() != CurrencyIfc.ZERO &&  // CR-20207
           unitTaxableAmount.compareTo(maxTaxTableLineItem.getMaxTaxableAmount()) == CurrencyIfc.GREATER_THAN)
        {
            // Get the decimal values for the total taxable amount and 
            //     the highest threshold value from the table.
            BigDecimal totalTaxableAmountBigDecimal = totalTaxableAmount.getDecimalValue();
            BigDecimal maxTableThresholdAmountBigDecimal = maxTaxTableLineItem.getMaxTaxableAmount().getDecimalValue();
            
            // Divide the threshold value into the total taxable amount to determine the how many max values
            // there are in the total taxable amount.
            BigDecimal decimalPart = totalTaxableAmountBigDecimal.divide(maxTableThresholdAmountBigDecimal, BigDecimal.ROUND_HALF_DOWN);
            
            // Get the integer part of the decimal part
            BigDecimal integerPart = new BigDecimal(decimalPart.toBigInteger());
            
            // Multiply the divide value times the max threshold amount to derive the taxable amount
            // for this part of the tax calculation.  
            CurrencyIfc tableTaxableAmount = maxTaxTableLineItem.getMaxTaxableAmount().
                multiply(integerPart);
            
            // Use the tax calculator to calculate the tax for this section.
            TaxLineItemInformationIfc[] lineItems = new TaxLineItemInformationIfc[1];
            lineItems[0] = lineItem;
            preCalculatedTaxAmount = maxTaxTableLineItem.getTaxCalculator().
                calculateTaxAmount(tableTaxableAmount, lineItems);
            
            // Additional calculations are required for fixed amount tax tables.
            if (maxTaxTableLineItem.getTaxCalculator() instanceof FixedAmountTaxCalculator)
            {
                // Calculate the number times the max table amount divides into the unit taxable amount
                BigDecimal unitTaxableAmountBigDecimal = unitTaxableAmount.getDecimalValue();
                BigDecimal unitDecimalPart = unitTaxableAmountBigDecimal.divide(maxTableThresholdAmountBigDecimal, BigDecimal.ROUND_HALF_DOWN);
                BigDecimal unitIntegerPart = new BigDecimal(unitDecimalPart.toBigInteger());
                
                // Multiply the unit integer part times the pre calculated tax amount to get
                // fixed tax amount for this section
                preCalculatedTaxAmount = preCalculatedTaxAmount.multiply(unitIntegerPart);
            }
            
            // Get taxable amount for the calculation in the next section.
            remainingTaxableAmount = totalTaxableAmount.subtract(tableTaxableAmount);
            lookupTaxableAmount = remainingTaxableAmount;
            
            // If there is no more tax to be calculated, update the line item and totals
            // with the tax information.
            if(remainingTaxableAmount.signum() == CurrencyIfc.ZERO)
            {
                TaxInformationIfc itemTaxInformation =  createTaxInformation(totalTaxableAmount,
                        preCalculatedTaxAmount, lineItem.getTaxMode());
                lineItem.getTaxInformationContainer().addTaxInformation(itemTaxInformation);
                totals.getTaxInformationContainer().addTaxInformation(itemTaxInformation);
            }
        }
        
        // This section performs step 2. from above.  If there is no remaining taxable amount,
        // skip this step.
        if(remainingTaxableAmount.signum() != CurrencyIfc.ZERO)
        {
            //find tax line item to compute the rest of tax
            boolean foundTaxLineItem = false;
            for(TaxTableLineItemIfc candidate : TaxTableLineItemArray)
            {
                // only the last entry in the table can be null; the isValid() method verifies this.
                if(candidate.getMaxTaxableAmount() == null)
                {    
                    foundTaxLineItem = true;
                }
                else
                {
                    int upperBound = lookupTaxableAmount.compareTo(candidate.getMaxTaxableAmount());
                    if (upperBound == CurrencyIfc.EQUALS || upperBound == CurrencyIfc.LESS_THAN)
                    {
                        foundTaxLineItem = true;
                    }
                }
                
                // Calculate the tax using the rule/tax calculator from the table tax line item; add
                // in the amount from section 1 if there is any.
                if(foundTaxLineItem)
                {
                    TaxByLineRuleIfc rule = getTableLineTaxRule(candidate);
                    rule.calculateTax(lineItem, remainingTaxableAmount, preCalculatedTaxAmount, totals);
                    break;
                }
            }
        }
    }

    /**
     * The threshold amounts in the tax table are based on unit selling price.
     * This method calculates the amount used to lookup the correct row in the table.
     * @param taxableAmount
     * @param itemQuantity
     * @return
     */
    protected CurrencyIfc getUnitTaxableAmount(CurrencyIfc taxableAmount,
            Number itemQuantity)
    {
        return taxableAmount.divide(new BigDecimal(itemQuantity.toString()), 
                taxableAmount.getScale(), BigDecimal.ROUND_DOWN).abs();
    }

    /**
     * Return the tax table line items
     */
    public List<TaxTableLineItemIfc> getTaxTableLineItems()
    {
        return taxTableLineItems;
    }

    /**
     * Return the tax table line items in ascending order based on its max taxable amount
     */
    public TaxTableLineItemIfc[] getSortedTaxTableLineItems()
    {
        TaxTableLineItemIfc[] taxTableLineItems = getTaxTableLineItems().toArray(new TaxTableLineItemIfc[0]);
        Arrays.sort(taxTableLineItems);
        return taxTableLineItems;
    }

    /**
     * Add the tax table line item to the Tax Table Calculator rule
     * @param taxTableLineItem
     */
    public void addTaxTableLineItem(TaxTableLineItemIfc taxTableLineItem)
    {
        taxTableLineItems.add(taxTableLineItem);
    }

    /**
     * Set all of the tax table line items
     * @param taxTableLineItemsValue
     */
    public void setTaxTableLineItems(List<TaxTableLineItemIfc> taxTableLineItems)
    {
        this.taxTableLineItems.clear();
        if (taxTableLineItems != null)
        {
            for (TaxTableLineItemIfc taxTableLineItem : taxTableLineItems)
            {
                this.taxTableLineItems.add(taxTableLineItem);
            }
        }
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

    /**
     * @return the tax rule
     */
    protected TaxByLineRuleIfc getTableLineTaxRule(TaxTableLineItemIfc tableLineItem)
    {
        if (tableLineTaxRule == null)
        {
            tableLineTaxRule = (TaxByLineRuleIfc)DomainGateway.getFactory().getTaxByLineRuleInstance();
            tableLineTaxRule.setUniqueID(getUniqueID());
            tableLineTaxRule.setTaxAuthorityID(getTaxAuthorityID());
            tableLineTaxRule.setTaxAuthorityName(getTaxAuthorityName());
            tableLineTaxRule.setTaxGroupID(getTaxGroupID());
            tableLineTaxRule.setTaxTypeCode(getTaxTypeCode());
            tableLineTaxRule.setTaxHoliday(isTaxHoliday());
            tableLineTaxRule.setTaxRuleName(getTaxRuleName());
            tableLineTaxRule.setOrder(getOrder());
            tableLineTaxRule.setInclusiveTaxFlag(getInclusiveTaxFlag());
            tableLineTaxRule.setUseBasePrice(getUseBasePrice());
        }
        tableLineTaxRule.setTaxCalculator(tableLineItem.getTaxCalculator());
        return tableLineTaxRule;
    }

    /**
     * Clone a copy of this object <P>
     *
     * @return copy of this object
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        TableTaxRule newClass = new TableTaxRule();
        setCloneAttributes(newClass);
        return newClass;
    }

    /**
     * Set attributes for clone.
     * <P>
     *
     * @param newClass new instance of ExciseTaxRule
     */
    public void setCloneAttributes(TableTaxRule newClass)
    {
        super.setCloneAttributes(newClass);
        newClass.prorateFlag = this.prorateFlag;
        if (getTaxTableLineItems() != null)
        {
            ArrayList<TaxTableLineItemIfc> tableLineItemList = new ArrayList<TaxTableLineItemIfc>();
            for (TaxTableLineItemIfc tableLineItem : getTaxTableLineItems())
            {
                tableLineItemList.add((TaxTableLineItemIfc)tableLineItem.clone());
            }
            newClass.setTaxTableLineItems(tableLineItemList);
        }
        else
        {
            newClass.setTaxTableLineItems(null);
        }
    }

}    
