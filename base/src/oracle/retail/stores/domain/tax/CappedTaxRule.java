/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/CappedTaxRule.java /main/1 2013/03/01 13:02:53 rgour Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rgour     02/28/13 - added capped tax rule
 * 
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;

import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;

public class CappedTaxRule extends AbstractTaxRule implements CappedTaxRuleIfc
{

    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1270882845504100058L;

    protected static Logger logger = Logger.getLogger(oracle.retail.stores.domain.tax.CappedTaxRule.class);

    /**
     * Capped amount on the taxable amount.
     */
    private CurrencyIfc cappedAmount;

    /**
     * Set the calculation mode for either line item or group. Always line item
     * for the capped tax rule
     */
    private boolean prorateFlag = false;

    /**
     * Constructs the Capped tax rule object.
     */
    public CappedTaxRule()
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

        if (valid)
        {
            if (cappedAmount != null)
            {
                if (cappedAmount.signum() == CurrencyIfc.NEGATIVE)
                {
                    valid = false;
                    logger.error("CappedTaxRule is invalid since cappedAmount is negative");
                }
                else if (isProrated())
                {
                    valid = false;
                    logger.error("CappedTaxRule is invalid since prorated is set to true");
                }

                else
                {
                    valid = true;
                }
            }
            else
            {
                logger.error("CappedTaxRule is invalid since cappedAmount is null");
            }
        }

        return valid;
    }

    /**
     * Clone a copy of this object
     * <P>
     * 
     * @return copy of this object
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        CappedTaxRule newClass = new CappedTaxRule();
        setCloneAttributes(newClass);
        return newClass;
    }

    /**
     * Set attributes for clone.
     * <P>
     * 
     * @param newClass new instance of CappedTaxRule
     */
    public void setCloneAttributes(CappedTaxRule newClass)
    {
        super.setCloneAttributes(newClass);
        if (cappedAmount != null)
        {
            newClass.cappedAmount = (CurrencyIfc)cappedAmount.clone();
        }
        else
        {
            newClass.cappedAmount = null;
        }
        newClass.prorateFlag = prorateFlag;
    }

    /**
     * Calculate the capped tax. CappedTaxRule allows calculation at only
     * LineItem Level. No support for Prorated Tax.
     * 
     * @param items List of items to tax
     * @param totals totals object to put the tax total into
     * @see oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc#calculateTax(oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc[],oracle.retail.stores.domain.transaction.TransactionTotalsIfc)
     */
    public void calculateTax(TaxLineItemInformationIfc[] items, TransactionTotalsIfc totals)
    {

        if (!isProrated())
        {
            calculateLineItemTax(items, totals);
        }
        else
        {
            logger.error("CappedTaxRule is invalid as the Tax Calculation Method is set to prorated");
        }
    }

    /**
     * Calculate the tax on the capped amount on a line item basis
     * 
     * @param items List of items to tax
     * @param totals totals object to put the tax total into
     * @see oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc#calculateTax(oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc[],
     *      oracle.retail.stores.domain.transaction.TransactionTotalsIfc)
     */
    public void calculateLineItemTax(TaxLineItemInformationIfc[] items, TransactionTotalsIfc totals)
    {
        TaxInformationIfc itemTaxInformation = null;

        TaxInformationIfc taxInformation = createTaxInformation(TaxConstantsIfc.TAX_MODE_STANDARD);
        for (int i = 0; i < items.length; i++)
        {
            // Reinitialize the taxable and tax amount to zero, for each item in
            // the loop
            CurrencyIfc taxableAmount = DomainGateway.getBaseCurrencyInstance();
            CurrencyIfc taxAmount = DomainGateway.getBaseCurrencyInstance();
            // If the capped amount is set to a non-zero value, and if the
            // taxable amount is
            // greater than or equal to the capped amount, then the taxable
            // amount eligible for
            // this rule would be the capped amount. Otherwise, the taxable
            // amount is used as it is.

            if (getCappedAmount() != null && getCappedAmount().getDecimalValue().doubleValue() > 0)
            {
                taxableAmount = getItemTaxableAmount(items[i]);
                TaxCalculatorIfc taxCalculator = getTaxCalculator();
                taxAmount = taxCalculator.calculateTaxAmount(taxableAmount, items[i]);
                itemTaxInformation = createTaxInformation(taxableAmount, taxAmount, items[i].getTaxMode());
                taxInformation.add(itemTaxInformation);
                items[i].getTaxInformationContainer().addTaxInformation(itemTaxInformation);
            }
            // Items didnt meet the capped amount criteria. Still need to save
            // the
            // tax rules associated with the item, for returns purposes.

            else
            {
                itemTaxInformation = createTaxInformation(taxableAmount, taxAmount, items[i].getTaxMode());
                items[i].getTaxInformationContainer().addTaxInformation(itemTaxInformation);
            }
        }

        totals.getTaxInformationContainer().addTaxInformation(taxInformation);
    }

    /**
     * Get the taxable amount on this rule
     * 
     * @param item
     * @return taxable amount
     */
    public CurrencyIfc getItemTaxableAmount(TaxLineItemInformationIfc item)
    {

        CurrencyIfc itemTaxableAmount = super.getItemTaxableAmount(item);
        if (itemTaxableAmount != null)

        {
            int isGreaterThanCappedAmt = itemTaxableAmount.abs().compareTo(getExtendedCappedAmount(item).abs());
            // if the taxable amount is greater than or equal to the capped
            // amount,
            // then return the capped amount, otherwise return the taxable
            // amount as it is
            if (isGreaterThanCappedAmt >= CurrencyIfc.EQUALS)
            {
                itemTaxableAmount = getExtendedCappedAmount(item);
            }
        }
        return itemTaxableAmount;
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
     * @return true or false
     */
    public boolean isProrated()
    {
        return this.prorateFlag;
    }

    /**
     * Retrieve the Capped Amount.
     * 
     * @return the capped amount.
     */
    public CurrencyIfc getCappedAmount()
    {
        return cappedAmount;
    }

    /**
     * Set the capped amount.
     * 
     * @param cappedAmount
     */
    public void setCappedAmount(CurrencyIfc cappedAmount)
    {
        this.cappedAmount = cappedAmount;
    }

    /**
     * Get the extended capped amount. .
     * 
     * @return Returns the cappedAmount multiplied by the item quantity.
     */
    public CurrencyIfc getExtendedCappedAmount(TaxLineItemInformationIfc item)
    {
        return cappedAmount.multiply(new BigDecimal(item.getItemQuantity().doubleValue()));
    }

}
