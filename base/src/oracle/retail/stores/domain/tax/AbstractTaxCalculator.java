/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/AbstractTaxCalculator.java /rgbustores_13.4x_generic_branch/3 2011/10/06 12:38:50 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       09/30/11 - change tax caculator api
 *    sgu       09/29/11 - set taxable line items to the tax calculator
 *    sgu       08/01/11 - donot round tax amount during tax calculation
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 5    360Commerce 1.4         4/25/2007 10:00:29 AM  Anda D. Cadar   I18N
 *      merge
 * 4    360Commerce 1.3         1/22/2006 11:41:52 AM  Ron W. Haight   Removed
 *      references to com.ibm.math.BigDecimal
 * 3    360Commerce 1.2         3/31/2005 4:27:07 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:19:28 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:09:21 PM  Robert Pearse
 *$ 4    360Commerce1.3         1/22/2006 11:41:52 AM  Ron W. Haight   Removed
 *      references to com.ibm.math.BigDecimal
 * 3    360Commerce1.2         3/31/2005 4:27:07 PM   Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:19:28 AM  Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:09:21 PM  Robert Pearse
 *$$
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;

import org.apache.log4j.Logger;

/**
 * Base class for tax calculator.
  */
public abstract class AbstractTaxCalculator implements TaxCalculatorIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 2523017242481091373L;

    protected static Logger logger = Logger.getLogger(oracle.retail.stores.domain.tax.AbstractTaxCalculator.class);

    private int roundingMode = BigDecimal.ROUND_HALF_UP;
    private int scale = 2;


    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.TaxCalculatorIfc#isValid()
     */
    public boolean isValid()
    {
        boolean returnValue = false;
        switch(roundingMode)
        {
        case BigDecimal.ROUND_CEILING:
        case BigDecimal.ROUND_DOWN:
        case BigDecimal.ROUND_FLOOR:
        case BigDecimal.ROUND_HALF_DOWN:
        case BigDecimal.ROUND_HALF_EVEN:
        case BigDecimal.ROUND_HALF_UP:
        case BigDecimal.ROUND_UP:
            returnValue = true;
            break;
        default:
            logger.error("Rounding mode (" + roundingMode + ") is not valid.");
        }

        if(returnValue)
        {
            if( scale <= 0 )
            {
                logger.error("Scale can not be less than or equal to zero.  Scale is " + scale);
                returnValue = false;
            }
        }


        return returnValue;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.TaxCalculatorIfc#getRoundingMode()
     */
    public int getRoundingMode()
    {
        return roundingMode;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.TaxCalculatorIfc#setRoundingMode(int)
     */
    public void setRoundingMode(int value)
    {
        roundingMode = value;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.TaxCalculatorIfc#getRoundingScale()
     */
    public int getScale()
    {
        return scale;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.TaxCalculatorIfc#setRoundingScale(int)
     */
    public void setScale(int value)
    {
        scale = value;
    }

    /**
     * Calculate tax amount
     * @param taxableAmount taxable amount
     * @return the tax amount
     * @deprecated As of 13.4 Use {@link TaxCalculatorIfc#calculateTaxAmount(CurrencyIfc, TaxLineItemInformationIfc)}
     * or {@link TaxCalculatorIfc#calculateTaxAmount(CurrencyIfc, TaxLineItemInformationIfc[])}
     */
    public CurrencyIfc calculateTaxAmount(CurrencyIfc taxableAmount)
    {
        throw new UnsupportedOperationException("Taxable line items must be specfied to calculate tax correctly.");
    }

    /**
     * Calculate tax amount
     * @param taxableAmount taxable amount
     * @param lineItem the line item
     * @return
     */
    public CurrencyIfc calculateTaxAmount(CurrencyIfc taxableAmount, TaxLineItemInformationIfc lineItem)
    {
        TaxLineItemInformationIfc[] lineItems = new TaxLineItemInformationIfc[1];
        lineItems[0] = lineItem;

        return calculateTaxAmount(taxableAmount, lineItems);
    }


    /**
     * @return the total item quantities
     */
    protected BigDecimal getItemQuantity(TaxLineItemInformationIfc[] lineItems)
    {
        BigDecimal itemQuantity = BigDecimal.ZERO;
        if (lineItems != null)
        {
            for (TaxLineItemInformationIfc taxLineItem : lineItems)
            {
                itemQuantity = itemQuantity.add(new BigDecimal(taxLineItem.getItemQuantity().toString()));
            }
        }

        return itemQuantity;
    }

    abstract public Object clone();

    //---------------------------------------------------------------------
    /**
     Set attributes for clone. <P>
     @param newClass new instance of TaxRateCalculator
     **/
    //---------------------------------------------------------------------
    public void setCloneAttributes(AbstractTaxCalculator newClass)
    {
        newClass.roundingMode = roundingMode;
        newClass.scale = scale;
    }

}
