/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/AbstractTaxRateCalculator.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:50 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  2    360Commerce 1.1         6/4/2007 1:44:23 PM    Sandy Gu        rework
 *       based on review comments
 *  1    360Commerce 1.0         4/30/2007 5:38:06 PM   Sandy Gu        added
 *       send package line items and inclusive tax calculator
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * Abstract base class for tax rate calculator
 */
public abstract class AbstractTaxRateCalculator extends AbstractTaxCalculator implements TaxRateCalculatorIfc {
	// This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1319495607798240147L;

    protected static Logger logger = Logger.getLogger(oracle.retail.stores.domain.tax.AbstractTaxRateCalculator.class);
    
    protected BigDecimal taxRate;

    public AbstractTaxRateCalculator()
    {
    }

    /**
     * Get the tax rate associated with this tax calculator
     * The rate with the percent already divided by 100.
     * For example 6% would be 0.06
     * @return the tax rate
     */
    public BigDecimal getTaxRate()
    {
        return taxRate;
    }
    
    /**
     * Set the tax rate associated with this tax calculator
     * @param value the rate
     * @see oracle.retail.stores.domain.tax.TaxRateCalculatorIfc#getTaxRate
     */
    public void setTaxRate(BigDecimal value)
    {
        taxRate = value;
    }

    /**
     * Determines whether the tax rate calculator is valid
     * @return true if taxCaclculator is valid, otherwise false
     */
    public boolean isValid()
    {
        boolean valid = super.isValid();
        
        if(taxRate == null)
        {
            valid = false;
            logger.error("AbstractTaxRateCalculator is invalid since taxRate is null.");
        }
        
        if(valid)
        {
            if( taxRate.signum() == -1 )
            {
                valid = false;
                logger.error("AbstractTaxRateCalculator is invalid since taxRate is negative.");
            }
        }
        return valid;
    }

    //---------------------------------------------------------------------
    /**
     Set attributes for clone. <P>
     @param newClass new instance of AbstractTaxRateCalculator
     **/
    //---------------------------------------------------------------------
    public void setCloneAttributes(AbstractTaxRateCalculator newClass)
    {
        super.setCloneAttributes(newClass);
        newClass.taxRate = taxRate;
    }
}
