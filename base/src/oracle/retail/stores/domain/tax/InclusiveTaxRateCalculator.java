/* ===========================================================================
* Copyright (c) 2007, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/InclusiveTaxRateCalculator.java /main/8 2012/07/02 10:12:52 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/02/12 - Tax cleanup in preparation for JPA conversion.
 *    sgu       09/30/11 - change tax caculator api
 *    sgu       08/02/11 - keep whole scale for multiplication during tax
 *                         calculation
 *    sgu       08/01/11 - donot round tax amount during tax calculation
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  2    360Commerce 1.1         6/14/2007 10:07:30 AM  Sandy Gu        added
 *       comments and logger
 *  1    360Commerce 1.0         4/30/2007 5:38:06 PM   Sandy Gu        added
 *       send package line items and inclusive tax calculator
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;

import org.apache.log4j.Logger;

/**
 * Class to calculate inclusive tax based on rate
 */
public class InclusiveTaxRateCalculator extends AbstractTaxRateCalculator {
	// This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1587803282349386235L;

    protected static Logger logger = Logger.getLogger(oracle.retail.stores.domain.tax.InclusiveTaxRateCalculator.class);

    /**
     * Default constructor
     */
    public InclusiveTaxRateCalculator()
    {
    }

    /**
     * Clone the inclusive tax rate calculator
     */
    public Object clone()
    {
    	InclusiveTaxRateCalculator taxRateCalculator = new InclusiveTaxRateCalculator();

        setCloneAttributes(taxRateCalculator);

        return taxRateCalculator;
    }

    /**
     * Calculate the tax amount based on the taxable amount passed in
     * Get the rounding mode from the database and do further calculations.
     * Clone used here, not to affect the original amount object.
     * @param amount the taxable amount
     * @return the tax amount
     */
    public CurrencyIfc calculateTaxAmount(CurrencyIfc taxableAmount, TaxLineItemInformationIfc[] lineItems)
    {
        int multiScale = taxableAmount.getScale() + getTaxRate().scale();
        CurrencyIfc retValue = taxableAmount.multiply(getTaxRate(), multiScale, getRoundingMode());
        retValue = retValue.divide(getTaxRate().add(BigDecimal.ONE), getScale(), getRoundingMode());

        return retValue;
    }
}
