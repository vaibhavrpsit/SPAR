/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/CurrencyRoundingCalculator.java /main/1 2013/03/07 13:20:43 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     02/13/13 - Added to support cash change rounding.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.CurrencyRoundingCalculatorIfc;
import oracle.retail.stores.storeservices.entities.currency.CurrencyRoundingRule;

/**
 * This class exposes a single method that calculates the cash change rounding amount.
 * @since 14.0
 */
public class CurrencyRoundingCalculator implements CurrencyRoundingCalculatorIfc
{
    /** 
      The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(CurrencyRoundingCalculator.class);
    
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.utility.CurrencyRoundingCalculatorIfc#calculateCurrencyRounding(oracle.retail.stores.commerceservices.common.currency.CurrencyIfc, java.lang.String, java.lang.String)
     * @Override
     */
    public CurrencyIfc calculateCashChangeRoundingAdjustment(CurrencyIfc cashChangeAmount, String roundingType, String roundingDenomination)
    {
        // Initialize the adjustment to zero
        CurrencyIfc roundingAdjustment = DomainGateway.getBaseCurrencyInstance();

        // Validate the parameters. Return zero if invalid or no rounding should occur.
        if (roundingType.equals(NO_ROUNDING) || !isParameterValid(roundingType, roundingDenomination))
        {
            return roundingAdjustment;
        }

        
        BigDecimal denomination = new BigDecimal(roundingDenomination);
        BigDecimal amountToRound = getAmountToRound(denomination, cashChangeAmount.abs());
        
        // If the amount to be rounded is equal to zero or the rounding denomination, then no 
        // rounding needs to be done.  For example, if the rounding denomination .05, then
        // if the amount to round is .00 or .05, then the rounding amount is 0.00.
        if (amountToRound.signum() == 0 || amountToRound.equals(denomination))
        {
            return roundingAdjustment;
        }

        // Get the rounding rules, and calculate the adjustment.
        List<CurrencyRoundingRule> rules = getRuleList(roundingType, denomination);
        if (rules != null)
        {
            roundingAdjustment = DomainGateway.getBaseCurrencyInstance(calculateRoundingAdjustment(amountToRound, rules));
        }
        return roundingAdjustment;
    }

    /**
     * This method determines if the parameter values are valid. 
     * @param roundingType
     * @param roundingDenomination
     * @return boolean true if valid
     */
    protected boolean isParameterValid(String roundingType, String roundingDenomination)
    {
        boolean roundingParameterValid = true;
        
        if (!roundingType.equals(NO_ROUNDING) &&
            !roundingType.equals(SWEDISH_ROUNDING) && 
            !roundingType.equals(ALWAYS_ROUND_UP) && 
            !roundingType.equals(ALWAYS_ROUND_DOWN))
        {
            logger.error("Unknown rounding type; unable to calcuate cash change rounding.");
            roundingParameterValid = false;
        }
        
        if (roundingParameterValid &&
            !roundingDenomination.equals(POINT_05_ROUNDING) && 
            !roundingDenomination.equals(POINT_10_ROUNDING) && 
            !roundingDenomination.equals(POINT_50_ROUNDING) &&
            !roundingDenomination.equals(ONE_POINT_ROUNDING)) 
        {
            logger.error("Unknown rounding denomination; unable to calcuate cash change rounding.");
            roundingParameterValid = false;
        }
        
        return roundingParameterValid;
    }
    
    /**
     * This method gets the portion of the change amount that is
     * significant to the rounding algorithm. 
     * @param denomination
     * @param cashChangeAmount
     * @return
     */
    protected BigDecimal getAmountToRound(BigDecimal denomination,
            CurrencyIfc cashChangeAmount)
    {
        /*
         * The best to describe the following code is through
         * an example;  for each of the following steps, use values
         * of .05 for the denomination and 4.47 for the change amount.  
         */
        
        // Get the divisor for the rounding denomination.  The getDivisor()
        // method returns a value of 10 for .05.
        int iDivisor = getDivisor(denomination);
        
        // Get the number of decimals in the cash change amount.  The scale
        // for 4.47 is 2.
        int scale = cashChangeAmount.getDecimalValue().scale();
        
        // Shift the change amount so that the significant digits are all above the
        // the decimal point.  The integer change amount is 447.
        int iChangeAmount = cashChangeAmount.getDecimalValue().movePointRight(scale).intValue();
        
        // The remainder of 447 divided by 10 is 7; put 7 in the BigDecimal variable.
        Integer iAmountToRound   = iChangeAmount % iDivisor;
        BigDecimal amountToRound = new BigDecimal(iAmountToRound.toString());
        
        // Moving the decimal point 2 to left makes the amount to round .07. 
        amountToRound = amountToRound.movePointLeft(scale);
        
        return amountToRound;
    }

    /**
     * Gets the divisor associated with rounding denomination parameter.
     * @param roundingDenomination determines the value of the divisor
     * @return the divisor
     */
    protected int getDivisor(BigDecimal roundingDenomination)
    {
        int divisor = 0;
        
        if (roundingDenomination.equals(D05_ROUNDING) || 
            roundingDenomination.equals(D10_ROUNDING))
        {
            divisor = 10;
        }
        else
        {
            divisor = 100;
        }

        return divisor;
    }

    /**
     * Gets the list of rounding rules
     * @param roundingType
     * @param denomination
     * @return List<CurrencyRoundingRule>
     */
    protected List<CurrencyRoundingRule> getRuleList(String roundingType, BigDecimal denomination)
    {
        return DomainGateway.getCurrencyRoundingRuleList(roundingType, denomination);
    }

    protected BigDecimal calculateRoundingAdjustment(BigDecimal amountToRound, List<CurrencyRoundingRule> rules)
    {
        BigDecimal roundingAdjustment = new BigDecimal("0.00"); 
        CurrencyRoundingRule rule = getRule(rules, amountToRound);
        if (rule == null)
        {
            logger.error("No rule was found for value to be rounded [" + amountToRound +"]; unable to calcuate cash change rounding.");
            return roundingAdjustment;
        }
        
        BigDecimal denomination = rule.getCurrencyRoundingDenomination();
        switch (rule.getCurrencyRoundingCalculationType())
        {
            case AMOUNT_TO_ROUND:
                roundingAdjustment = amountToRound;
                break;
            
            case AMOUNT_TO_ROUND_MINUS_DENOMINATION:
                roundingAdjustment = amountToRound.subtract(denomination);
                break;
                
            case AMOUNT_TO_ROUND_MINUS_DOUBLE_DENOMINATION:
                roundingAdjustment = amountToRound.subtract(denomination.multiply(TWO));
                break;
            
            default:
                roundingAdjustment = performImplementationCalculation(amountToRound, rules);
        }
        
        return roundingAdjustment;
    }

    /**
     * Implementors can override this method to provide calculations
     * for their own rules.
     * @param amountToRound
     * @param rules
     * @return rounded amount
     */
    protected BigDecimal performImplementationCalculation(BigDecimal amountToRound,
            List<CurrencyRoundingRule> rules)
    {
        logger.error("Base class error;  unable to calcuate cash change rounding.");
        return new BigDecimal("0.00"); 
    }

    /**
     * Gets the rounding rule for the remainder value
     * @param rules
     * @param remainder
     * @return the rounding rule
     */
    private CurrencyRoundingRule getRule(List<CurrencyRoundingRule> rules,
            BigDecimal amountToRound)
    {
        CurrencyRoundingRule theRule = null;
        for(CurrencyRoundingRule rule: rules)
        {
            if (amountToRound.compareTo(rule.getMinimumValueAmount()) >= 0 &&
                amountToRound.compareTo(rule.getMaximumValueAmount()) <= 0)
            {
                theRule = rule;
                break;
            }
        }
        
        return theRule;
    }
}
