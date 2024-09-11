/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transform/entity/Item/TaxRulesTransformer.java /main/11 2014/07/24 15:23:28 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/22/14 - set tax authority name
 *    mkutiana  02/05/14 - Fortify Null Derefernce fix
 *    jswan     12/13/13 - Upated JAVADOC.
 *    abondala  09/04/13 - initialize collections
 *    rgour     03/04/13 - correcting Compound Tax order based on the sequence
 *                         number
 *    rgour     03/01/13 - checking null value for setting maximum taxable
 *                         amount
 *    rgour     02/28/13 - added capped tax rule
 *    abondala  01/27/13 - extending JPA
 *    abondala  01/10/13 - support extending jpa
 *    jswan     09/17/12 - Made changes due to code review.
 *    jswan     07/20/12 - Added to transform JPA Item Entitities into a
 *                         PLUItemIfc.
 *    jswan     07/03/12 - Intial version.
 * ===========================================================================
 */
package oracle.retail.stores.domain.transform.entity.Item;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility._360DateIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tax.CappedTaxRuleIfc;
import oracle.retail.stores.domain.tax.ExciseTaxRuleIfc;
import oracle.retail.stores.domain.tax.FixedAmountTaxCalculatorIfc;
import oracle.retail.stores.domain.tax.TableTaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxCalculatorIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.tax.TaxRateCalculatorIfc;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxTableLineItemIfc;
import oracle.retail.stores.domain.tax.ValueAddedTaxRuleIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.storeservices.entities.tax.TaxRateRule;

import org.apache.log4j.Logger;

/**
 * This class transforms a list of Tax Rate Rule Entity objects into an array of TaxRuleIfc
 * domain objects.
 * @since 14.0
 */
public class TaxRulesTransformer implements TaxRulesTransformerIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(TaxRulesTransformer.class);

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.Item.TaxRulesTransformerIfc#Transform(java.util.List)
     */
    @Override
    public TaxRuleIfc[] transform(List<TaxRateRule> jpaTaxRules)
    {
        // Order by compound sequence number.
        List<TaxRateRule> sortedRules = sortTaxRules(jpaTaxRules);

        HashMap<String, TaxRuleIfc> taxRules = new HashMap<String, TaxRuleIfc>(1);
        boolean taxHolidayRuleExists = false;

        for(TaxRateRule sortedRule: sortedRules)
        {
            // Setup the tax rule with its attribute data
            StringBuffer uniqueID = new StringBuffer(sortedRule.getGroupRule().getTaxGroupRuleID().getAuthorityID());
            uniqueID.append("-");
            uniqueID.append(sortedRule.getGroupRule().getTaxGroupRuleID().getGroupID());
            uniqueID.append("-");
            uniqueID.append(sortedRule.getGroupRule().getTaxGroupRuleID().getTaxType());
            uniqueID.append("-");
            if(sortedRule.getGroupRule().getTaxGroupRuleID().isHolidayFlag() == false)
            {
                uniqueID.append("0");
            }
            else
            {
                uniqueID.append("1");
            }

            TaxRuleIfc taxRule = getTaxRule(sortedRule, taxRules, uniqueID);
            if (taxRule == null)
            {
                continue;
            }
            
            TaxCalculatorIfc taxCalculator = getTaxCalculator(sortedRule);
            if (taxCalculator == null)
            {
                continue;
            }

            setCalculatorOnTaxRule(sortedRule, taxRule, taxCalculator);

            setDataOnTaxRule(sortedRule, taxRule, uniqueID.toString());
            
            if(taxRule.isValid() == false)
            {
                // An error message has already be logged; 
                // just go to the iteration of the loop.
                continue;
            }

            // Check to see if there is already a tax rule for this Rule ID
            // in the map.  If not, add this rule.  This works because the
            // rules have been sorted in priority order.
            if(taxRules.get(uniqueID.toString()) == null)
            {
                taxRules.put(uniqueID.toString(), taxRule);
            }
            else if (!(taxRule instanceof TableTaxRuleIfc))
            {
                logger.error(getValidationErrorMessage(sortedRule, "Duplicate rule found for "+taxRule.getUniqueID()));
                continue;
            }

            if(taxRule.isTaxHoliday() == true)
            {
                taxHolidayRuleExists = true;
            }
        }

        Collection<TaxRuleIfc> returnRules = taxRules.values();
        // Now, if we have rules with tax holidays, all rules without tax holidays need to
        // be discarded, but only if they have the same tax group id.
        if(taxHolidayRuleExists)
        {
            returnRules = filterForTaxHoliday(taxRules);
        }

        return (TaxRuleIfc[]) returnRules.toArray(new TaxRuleIfc[0]);
    }

    /**
     * This method interrogates the {@link TaxRateRule} entity to determine which
     * kind of TaxRuleIfc object to instantiate. The types of tax rules that can be
     * instantiated are:
     * <p>
     * oracle.retail.stores.domain.tax.TableTaxRuleIfc
     * <p>
     * oracle.retail.stores.domain.tax.ExciseTaxRuleIfc
     * <p>
     * oracle.retail.stores.domain.tax.CappedTaxRuleIfc
     * <p>
     * oracle.retail.stores.domain.tax.TaxRuleIfc (by line or pro-rated)
     * <p>
     * oracle.retail.stores.domain.tax.ValueAddedTaxRuleIfc (by line or pro-rated)
     * <p>
     * When extending this class to include a new rule, override this method to instantiate
     * the implementation specific rule, then if necessary call the super version of the method.
     * @param rule a TaxRateRule entity.
     * @param taxRules a map of all the TaxRule domain objects that have already been created.
     * @param uniqueID a tax rule ID constructed from the tax authority ID, the tax group ID,
     * the tax type code (calculated by percentage or amount) and the tax holiday flag.
     * @return the TaxRuleIfc domain object; if the tax data is not valid the return value is null.
     */
    protected TaxRuleIfc getTaxRule(TaxRateRule rule, HashMap<String, TaxRuleIfc> taxRules, 
            StringBuffer uniqueID)
    {
        TaxRuleIfc taxRule = null;
        
        // Check for valid values from the database.
        if (isTaxDateValid(rule) && isRuleSequenceValid(rule) && isTaxUsageCodeValid(rule) && 
            isCalculationMethodValid(rule) && isTaxTypeValid(rule))
        {
            // Generate the appropriate tax rules.
            if(rule.getGroupRule().getSequence() == 0 || rule.getGroupRule().getSequence() == 1)
            {
                if (rule.getGroupRule().getUsageCode() == TaxConstantsIfc.TAX_RULE_USAGE_CODE_TABLE_RULE)
                {
                    // create or retrieve existing table tax rule
                    if(taxRules.get(uniqueID.toString()) != null)
                    {
                        taxRule = (TaxRuleIfc) taxRules.get(uniqueID.toString());
                    }
                    else
                    {
                        taxRule = DomainGateway.getFactory().getTableTaxRuleInstance();
                    }
                }
                else if(rule.getGroupRule().getUsageCode() == TaxConstantsIfc.TAX_RULE_USAGE_CODE_THRESHOLD_RULE)
                {
                    taxRule = DomainGateway.getFactory().getExciseTaxRuleInstance();
                }
                else if(rule.getGroupRule().getUsageCode() == TaxConstantsIfc.TAX_RULE_USAGE_CODE_CAPPED_RULE)
                {
                    taxRule = DomainGateway.getFactory().getCappedTaxRuleInstance();
                }
                else
                {
                    if (rule.getGroupRule().getCalculationMethod().equals(TaxConstantsIfc.TAX_CAL_METHOD_BY_LINE))
                    {
                        taxRule = DomainGateway.getFactory().getTaxByLineRuleInstance();
                    }
                    else if(rule.getGroupRule().getCalculationMethod().equals(TaxConstantsIfc.TAX_CAL_METHOD_PRORATE))
                    {
                        taxRule = DomainGateway.getFactory().getTaxProrateRuleInstance();
                    }
                }
            }
            else // sequences 2 and 3, i.e. compound tax rule
            {
                ValueAddedTaxRuleIfc valueAddedTaxRule = null;
                if (rule.getGroupRule().getCalculationMethod().equals(TaxConstantsIfc.TAX_CAL_METHOD_BY_LINE))
                {
                    taxRule = valueAddedTaxRule = DomainGateway.getFactory().getValueAddedTaxByLineRuleInstance();
                }
                else if(rule.getGroupRule().getCalculationMethod().equals(TaxConstantsIfc.TAX_CAL_METHOD_PRORATE))
                {
                    taxRule = valueAddedTaxRule = DomainGateway.getFactory().getValueAddedTaxProrateRuleInstance();
                }

                TaxRuleIfc tempTaxRule = null;
                boolean initialRuleFound = false;
                // Find the rule to compound the tax on and set its uniqueID
                int taxRuleToFind = rule.getGroupRule().getSequence() - 1;
                for(Iterator<TaxRuleIfc> iter = taxRules.values().iterator(); iter.hasNext(); )
                {
                    tempTaxRule = iter.next();
                    if(valueAddedTaxRule != null && tempTaxRule.getOrder() <= taxRuleToFind )
                    {
                        valueAddedTaxRule.addValueAddedTaxUniqueId(tempTaxRule.getUniqueID());
                        initialRuleFound = true;                        
                    }
                    tempTaxRule = null;
                }
    
                if(!initialRuleFound )
                {
                    logger.error(getValidationErrorMessage(rule, "Could not find intial rule for compound tax rule."));
                }
            }
        }
        
        return taxRule;
    }

    /**
     * Validates the effective and expiration dates from TaxRateRule entity. 
     * @param rule a TaxRateRule entity
     * @return true if valid
     */
    protected boolean isTaxDateValid(TaxRateRule rule)
    {
        boolean valid = true;
        // If the current date doesn't fall in between the effective and expired 
        // time stamps, then skip this tax rule as it does not apply.
        EYSDate currentDate = new EYSDate();
        if(rule.getExpirationDate() != null)
        {
            _360DateIfc expirationDate = DomainGateway.getFactory().get_360DateInstance(); 
            expirationDate.initialize(rule.getExpirationDate());
            if(currentDate.after(expirationDate))
            {
                valid = false;
                logger.info(getValidationErrorMessage(rule, "Invalid expiration date received for tax rule:" +
                        rule.getGroupRule().getSequence()));
            }
        }
        if(rule.getEffectiveDate() != null)
        {
            _360DateIfc effectiveDate = DomainGateway.getFactory().get_360DateInstance(); 
            effectiveDate.initialize(rule.getEffectiveDate());
            if(currentDate.before(effectiveDate))
            {
                logger.info(getValidationErrorMessage(rule, "Invalid effective date received for tax rule:" +
                        rule.getGroupRule().getSequence()));
                valid = false;
            }
        }
        
        return valid;
    }
    
    /**
     * Validates the Tax Usage Code from TaxRateRule entity. 
     * @param rule a TaxRateRule entity
     * @return true if valid
     */
    protected boolean isTaxUsageCodeValid(TaxRateRule rule)
    {
        boolean valid = false;
        // Generate the appropriate tax rules.
        if(rule.getGroupRule().getSequence() == 0 || rule.getGroupRule().getSequence() == 1)
        {
            if (rule.getGroupRule().getUsageCode() == TaxConstantsIfc.TAX_RULE_USAGE_CODE_TABLE_RULE ||
                rule.getGroupRule().getUsageCode() == TaxConstantsIfc.TAX_RULE_USAGE_CODE_THRESHOLD_RULE ||
                rule.getGroupRule().getUsageCode() == TaxConstantsIfc.TAX_RULE_USAGE_CODE_SINGLE_LEVEL_RULE ||
                rule.getGroupRule().getUsageCode() == TaxConstantsIfc.TAX_RULE_USAGE_CODE_CAPPED_RULE)
            {
                valid = true;
            }
            else
            {
                logger.error(getValidationErrorMessage(rule, "Invalid usage code received for tax rule: " +
                        rule.getGroupRule().getUsageCode()));
            }
        }
        else if(rule.getGroupRule().getSequence() == 2 || rule.getGroupRule().getSequence() == 3)
        {
            if (rule.getGroupRule().getUsageCode() == TaxConstantsIfc.TAX_RULE_USAGE_CODE_TABLE_RULE)
            {
                logger.error(getValidationErrorMessage(rule, 
                        "Table tax rule can not be a compound sequence number greater than 1, value:" + 
                        rule.getGroupRule().getSequence()));
            }
            else if(rule.getGroupRule().getUsageCode() == TaxConstantsIfc.TAX_RULE_USAGE_CODE_THRESHOLD_RULE)
            {
                logger.error(getValidationErrorMessage(rule,
                        "Excise tax rule can not be a compound sequence number greater than 1, value:" + 
                        rule.getGroupRule().getSequence()));
            }
            else if (rule.getGroupRule().getUsageCode() == TaxConstantsIfc.TAX_RULE_USAGE_CODE_SINGLE_LEVEL_RULE)
            {
                valid = true;
            }
            else
            {
                logger.error(getValidationErrorMessage(rule, "Invalid usage code received for tax rule: " +
                        rule.getGroupRule().getUsageCode()));
            }
        }
        
        return valid;
    }

    /**
     * Validates the Tax Calculation Method Code from TaxRateRule entity. 
     * @param rule a TaxRateRule entity
     * @return true if valid
     */
    protected boolean isCalculationMethodValid(TaxRateRule rule)
    {
        boolean valid = false;
        if (rule.getGroupRule().getCalculationMethod().equals(TaxConstantsIfc.TAX_CAL_METHOD_BY_LINE) ||
            rule.getGroupRule().getCalculationMethod().equals(TaxConstantsIfc.TAX_CAL_METHOD_PRORATE))
        {
            valid = true;
        }
        else
        {
            logger.error(getValidationErrorMessage(rule, "Invalid calculation method received for tax rule: " +
                    rule.getGroupRule().getSequence()));
        }
        
        return valid;
    }

    /**
     * Validates the Tax Compound Sequence Code from TaxRateRule entity. 
     * @param rule a TaxRateRule entity
     * @return true if valid
     */
    public boolean isRuleSequenceValid(TaxRateRule rule)
    {
        boolean valid = false;
        if(rule.getGroupRule().getSequence() == 0 || rule.getGroupRule().getSequence() == 1 ||
           rule.getGroupRule().getSequence() == 2 || rule.getGroupRule().getSequence() == 3)
        {
            valid = true;
        }
        else
        {
            logger.error(getValidationErrorMessage(rule, "Invalid compound sequence number received for tax rule: " +
                    rule.getGroupRule().getSequence()));
        }
        
        return valid;
    }

    /**
     * Validates the Tax Type (rate or fixed amount) from TaxRateRule entity. 
     * @param rule a TaxRateRule entity
     * @return true if valid
     */
    protected boolean isTaxTypeValid(TaxRateRule rule)
    {
        boolean valid = false;
        if (rule.getTypeCode() == TaxConstantsIfc.TAX_TYPE_CODE_RATE ||
            rule.getTypeCode() == TaxConstantsIfc.TAX_TYPE_CODE_FIXED_AMT)
        {
            valid = true;
        }
        else
        {
            logger.error(getValidationErrorMessage(rule, "Unknown type (" + 
                    rule.getGroupRule().getTaxGroupRuleID().getTaxType() + 
                    " received for tax calculator."));
        }
        
        return valid;
    }

    /**
     * This method interrogates the {@link TaxRateRule} entity to determine which
     * kind of tax calculator object to instantiate.  The types of tax calculators 
     * that can be instantiated are:
     * <p>
     * oracle.retail.stores.domain.tax.TaxRateCalculatorIfc
     * <p>
     * oracle.retail.stores.domain.tax.FixedAmountTaxCalculatorIfc
     * <p>
     * When extending this class to include a new calculator, override this method to instantiate
     * the implementation specific calculator, then if necessary call the super version of the method.
     * 
     * @param rule a TaxRateRule entity.
     * @return the TaxCalculatorIfc domain object; if the tax data is not valid the return value is null.
     */
    protected TaxCalculatorIfc getTaxCalculator(TaxRateRule rule)
    {
        TaxCalculatorIfc taxCalculator = null;

        if (rule.getTypeCode() == TaxConstantsIfc.TAX_TYPE_CODE_RATE)
        {
            TaxRateCalculatorIfc taxRateCalculator = 
                DomainGateway.getFactory().getTaxRateCalculatorInstance(rule.getGroupRule().isInclusiveFlag());
            taxRateCalculator.setTaxRate(rule.getPercent().movePointLeft(2));
            taxCalculator = taxRateCalculator;
        }
        else if (rule.getTypeCode() == TaxConstantsIfc.TAX_TYPE_CODE_FIXED_AMT)
        {
            FixedAmountTaxCalculatorIfc fixedAmountTaxCalculator = 
                DomainGateway.getFactory().getFixedAmountTaxCalculatorInstance();
            fixedAmountTaxCalculator.setTaxAmount(
                DomainGateway.getBaseCurrencyInstance(rule.getAmount()));
            taxCalculator = fixedAmountTaxCalculator;
        }

        if(taxCalculator != null)
        {
            taxCalculator.setRoundingMode(rule.getGroupRule().getAuthority().getRoundingCode());
            taxCalculator.setScale(rule.getGroupRule().getAuthority().getRoundingDigits());
        }

        return taxCalculator;
    }

    /**
     * Different tax rules set the calculator in different locations;  This
     * method allows for that eventuality.
     * <p>
     * If an implementation specific class needs to placed in a location 
     * other than in the base class, override this method to perform that
     * action, then if necessary call the super version of the method. 
     * @param rule a TaxRateRule entity.
     * @param taxRule a TaxRuleIfc domain object
     * @param taxCalculator a TaxCalculatorIfc domain object
     */
    protected void setCalculatorOnTaxRule(TaxRateRule rule, TaxRuleIfc taxRule, 
            TaxCalculatorIfc taxCalculator)
    {
        if (taxRule instanceof TableTaxRuleIfc)
        {
            TableTaxRuleIfc tableTaxRule = (TableTaxRuleIfc) taxRule;
            TaxTableLineItemIfc taxTableLineItem = DomainGateway.getFactory().getTaxTableLineItemInstance();
            taxTableLineItem.setMaxTaxableAmount( getMaximumTaxableAmount (rule.getMaximumTaxableAmount()));
            tableTaxRule.addTaxTableLineItem(taxTableLineItem);
            taxTableLineItem.setTaxCalculator(taxCalculator);
        }
        else
        {
            taxRule.setTaxCalculator(taxCalculator);
        }
    }

    /**
     * This method sets the data from the {@link TaxRateRule} entity on the  
     * TaxRuleIfc domain object.
     * <p> 
     * If an implementation specific class needs to additional data from the
     * database, override this method to perform that
     * action, then if necessary call the super version of the method. 
     * @param rule a TaxRateRule entity.
     * @param taxRule a TaxRuleIfc domain object
     * @param uniqueID a tax rule ID constructed from the tax authority ID, the tax group ID,
     * the tax type code (calculated by percentage or amount) and the tax holiday flag.
     */
    protected void setDataOnTaxRule(TaxRateRule rule, TaxRuleIfc taxRule, String uniqueID)
    {
        // Tax Calculator retrieved
        taxRule.setUniqueID(uniqueID);
        taxRule.setTaxAuthorityID(
            Integer.parseInt(rule.getGroupRule().getTaxGroupRuleID().getAuthorityID()));
        taxRule.setTaxAuthorityName(rule.getGroupRule().getAuthority().getTaxAuthorityName());
        taxRule.setTaxGroupID(rule.getGroupRule().getTaxGroupRuleID().getGroupID());
        taxRule.setTaxTypeCode(
                Integer.parseInt(rule.getGroupRule().getTaxGroupRuleID().getTaxType()));
        taxRule.setTaxHoliday(rule.getGroupRule().getTaxGroupRuleID().isHolidayFlag());
        taxRule.setTaxRuleName(rule.getGroupRule().getName());
        taxRule.setOrder(rule.getGroupRule().getSequence());
        taxRule.setInclusiveTaxFlag(rule.getGroupRule().isInclusiveFlag());

        //use price before discount
        taxRule.setUseBasePrice(rule.getGroupRule().isGrossAmountFlag());


        boolean prorate = false;
        if (rule.getGroupRule().getCalculationMethod().equals(TaxConstantsIfc.TAX_CAL_METHOD_PRORATE))
        {
            prorate = true;
        }
        if (taxRule instanceof TableTaxRuleIfc)
        {
            TableTaxRuleIfc tableTaxRule = (TableTaxRuleIfc) taxRule;
            tableTaxRule.setProrated(prorate);
        }
        else if (taxRule instanceof ExciseTaxRuleIfc)
        {
            ExciseTaxRuleIfc exciseTaxRule = (ExciseTaxRuleIfc) taxRule;
            exciseTaxRule.setTaxEntireAmount(rule.isAboveThresholdFlag() == false);
            exciseTaxRule.setThresholdAmount(
                DomainGateway.getBaseCurrencyInstance(rule.getThresholdAmount()));
            exciseTaxRule.setProrated(prorate);
        }
        else if(taxRule instanceof CappedTaxRuleIfc)
        {
            CappedTaxRuleIfc cappedTaxRule = (CappedTaxRuleIfc) taxRule;
            //The MaximumTaxableAmount from Tax Rate table is the capped amount for CappedTaxRule
            cappedTaxRule.setCappedAmount( DomainGateway.getBaseCurrencyInstance(rule.getMaximumTaxableAmount()));
            cappedTaxRule.setProrated(prorate);
        }
    }

    /**
     *  This method provides an additional message which will help locate 
     *  the offending tax rule.
     *  @param The error text associated with the specific error text.
     *  @return The completed error text
     */
    protected String getValidationErrorMessage(TaxRateRule rule, String message)
    {
        String completeMessage = message + 
            "  Tax Rule Name: " + rule.getGroupRule().getName() +
            "; Tax Authority ID: " + rule.getGroupRule().getTaxGroupRuleID().getAuthorityID() +
            "; Tax Group ID: " + rule.getGroupRule().getTaxGroupRuleID() +
            "\n\t  The tax for items using this rule will be calculated at default rate.";
        
        return completeMessage;
    }

    /**
     * Filers outs rules that do not have the holiday flag set.
     * @param taxRules a map of TaxRuleIfc domain objects, keyed by the uniqueID.
     * @return rules filtered for tax holiday purposes.
     */
    protected ArrayList<TaxRuleIfc> filterForTaxHoliday(HashMap<String, TaxRuleIfc> taxRules)
    {
        ArrayList<TaxRuleIfc> returnRules = new ArrayList<TaxRuleIfc>();
        ArrayList<TaxRuleIfc> normalTaxRules = new ArrayList<TaxRuleIfc>();
        HashSet<String> returnTaxGroups = new HashSet<String>();
        returnRules.clear();
        Iterator<String> iter = taxRules.keySet().iterator();
        // Find the tax holiday rules and track what taxGroupId they belong to.
        // Separate the tax holiday from the normal rules.
        while(iter.hasNext())
        {
            String key = iter.next();
            TaxRuleIfc rule = (TaxRuleIfc) taxRules.get(key);
            if(rule.isTaxHoliday())
            {
                returnRules.add(rule);
                returnTaxGroups.add(String.valueOf(rule.getTaxGroupID()));
            }
            else
            {
                normalTaxRules.add(rule);
            }
        }
        // Add any rules that are not covered by a tax holiday rule for
        // their taxGroupId.
        ArrayList<TaxRuleIfc> rulesToAdd = new ArrayList<TaxRuleIfc>();
        for(int i=0; i<normalTaxRules.size(); i++)
        {
            TaxRuleIfc normalTaxRule = (TaxRuleIfc) normalTaxRules.get(i);
            String taxGroupId = String.valueOf(normalTaxRule.getTaxGroupID());
            if(!returnTaxGroups.contains(taxGroupId))
            {
                rulesToAdd.add(normalTaxRule);
            }
        }
        // Add the normal (non-tax-holiday) rules to the tax holiday rules.
        returnRules.addAll(rulesToAdd);
        return returnRules;
    }
    
    /**
     * This method uses the inner class TaxRuleSortConainer to sort the list
     * of TaxRateRule entities.
     * 
     * @param jpaTaxRules
     * @return the sorted List of TaxRateRule objects
     */
    protected List<TaxRateRule> sortTaxRules(List<TaxRateRule> jpaTaxRules)
    {
        ArrayList<TaxRuleSortConainer> containers = new ArrayList<TaxRuleSortConainer>();
        for(TaxRateRule jpaTaxRule: jpaTaxRules)
        {
            containers.add(new TaxRuleSortConainer(jpaTaxRule));
        }
        
        Collections.sort(containers);

        ArrayList<TaxRateRule> rules = new ArrayList<TaxRateRule>();
        for(TaxRuleSortConainer container: containers)
        {
            rules.add(container.jpaTaxRule);
        }
        
        return rules;
    }

    /**
     * This method converts a BigDecimal to a CurrencyIfc object.
     * @param amount a BigDecimal object
     * @return CurrencyIfc; if the amount is null, the return value is null.
     */
    protected CurrencyIfc getMaximumTaxableAmount(BigDecimal amount)
    {
        CurrencyIfc c = null;

        if (amount != null)
        {
            c = DomainGateway.getBaseCurrencyInstance(amount);
        }

        return c;
    }

    /*
     * Since we would like to keep the JPA class as single purpose as possible, this
     * inner class provides the implementation of the Comparable interface for sorting
     * the TaxRuleRate entities.  The the compareTo() method provides the ordering 
     * mechanism by comparing the sequence value from the contained TaxRateRule class.
     */
    private class TaxRuleSortConainer implements Comparable<TaxRuleSortConainer>
    {
        protected TaxRateRule jpaTaxRule;
        
        protected TaxRuleSortConainer(TaxRateRule jpaTaxRule)
        {
            this.jpaTaxRule = jpaTaxRule;
        }
        
        public int compareTo(TaxRuleSortConainer other)
        {
            return jpaTaxRule.getGroupRule().getSequence() - other.jpaTaxRule.getGroupRule().getSequence();
        }
    }
}
