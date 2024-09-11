/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/FixedAmountTaxCalculator.java /rgbustores_13.4x_generic_branch/2 2011/10/06 12:38:50 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       10/04/11 - rework table tax using tax rules instead of
 *                         calculator
 *    sgu       09/30/11 - change tax caculator api
 *    sgu       09/29/11 - set taxable line items to the tax calculator
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 10:00:29 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:28:12 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:43 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:06 PM  Robert Pearse
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;

/**
 * $Revision: /rgbustores_13.4x_generic_branch/2 $
 *
 * Returns a fixed amount when calculating the tax
 */
public class FixedAmountTaxCalculator extends AbstractTaxCalculator implements FixedAmountTaxCalculatorIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3470436943709216544L;

    private CurrencyIfc taxAmount = DomainGateway.getBaseCurrencyInstance();
    private boolean isUnitTaxAmount = true;

    /**
     * Calculate tax based on the amount in param amount.  This implementation
     * always returns the same amount
     * @param amount the taxable amount to use when calculating tax
     * @see oracle.retail.stores.domain.tax.TaxCalculatorIfc#calculateTaxAmount(oracle.retail.stores.domain.currency.CurrencyIfc)
     */
    public CurrencyIfc calculateTaxAmount(CurrencyIfc taxableAmount, TaxLineItemInformationIfc[] lineItems)
    {
        CurrencyIfc result;
        if (isUnitTaxAmount())
        {
            BigDecimal quantity = getItemQuantity(lineItems);   // item quantity is negative during return
            int scale = taxAmount.getScale() + quantity.scale();
            result = taxAmount.multiply(quantity, scale, getRoundingMode());
        }
        else
        {
            result = (CurrencyIfc) taxAmount.clone();
            if(taxableAmount.signum() == CurrencyIfc.NEGATIVE)
            {
                result = result.negate();
            }
        }

        return result;
    }

    /**
     * Create a deep copy of this Object
     * @return Object The copy of this object
     */
    public Object clone()
    {
        FixedAmountTaxCalculator newClass = new FixedAmountTaxCalculator();
        setCloneAttributes(newClass);
        return newClass;
    }

    /**
     * Set attributes for clone. <P>
     * @param newClass new instance of TaxRateCalculator
     */
    public void setCloneAttributes(FixedAmountTaxCalculator newClass)
    {
        super.setCloneAttributes(newClass);
        if(taxAmount != null)
        {
            newClass.taxAmount = (CurrencyIfc) taxAmount.clone();
        }
        else
        {
            newClass.taxAmount = null;
        }
        newClass.isUnitTaxAmount = this.isUnitTaxAmount;
    }

    /**
     * Retrieve the fixed tax amount for this calculator.
     * @return Returns the taxAmount.
     */

    public CurrencyIfc getTaxAmount()
    {
        return taxAmount;
    }

    /**
     * Set the fixed tax amount
     * @param value The taxAmount to set.
     */
    public void setTaxAmount(CurrencyIfc value)
    {
        taxAmount = value;
    }

    /*
     * Return a boolean indicating if the fixed tax amount is specified on a per unit basis
     * @return Returns the boolean.
     */
    public boolean isUnitTaxAmount()
    {
        return isUnitTaxAmount;
    }

    /*
     * Set the boolean indicating if the fixed tax amount is specified on a per unit basis
     * @param isUnitTaxAmount the boolean to set.
     */
    public void setIsUnitTaxAmount(boolean isUnitTaxAmount)
    {
        this.isUnitTaxAmount = isUnitTaxAmount;
    }

    /**
     * Is this tax calculator valid.
     * @return true if tax rule is valid, otherwise false
     */
    public boolean isValid()
    {
        boolean valid = super.isValid();

        if(taxAmount == null)
        {
            valid = false;
        }

        if(valid)
        {
            valid = taxAmount.signum() != CurrencyIfc.NEGATIVE;
        }
        return valid;
    }

}
