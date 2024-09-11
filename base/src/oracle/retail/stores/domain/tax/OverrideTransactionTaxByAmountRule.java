/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/OverrideTransactionTaxByAmountRule.java /main/12 2012/09/06 12:41:33 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       09/06/12 - set transaction tax override reason code to item
 *                         level
 *    jswan     07/02/12 - Tax cleanup in preparation for JPA conversion.
 *    sgu       09/30/11 - change tax caculator api
 *    sgu       09/29/11 - set taxable line items to the tax calculator
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         4/25/2007 10:00:28 AM  Anda D. Cadar   I18N
 *      merge
 * 3    360Commerce 1.2         3/31/2005 4:29:16 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:23:56 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:12:56 PM  Robert Pearse
 *$$
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;

/**
 * @author mkp1
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OverrideTransactionTaxByAmountRule
    extends AbstractOverrideTransactionTaxRule
    implements OverrideTransactionTaxByAmountRuleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3312111243346934719L;

    public OverrideTransactionTaxByAmountRule()
    {
        FixedAmountTaxCalculatorIfc taxCalculator = DomainGateway.getFactory().getFixedAmountTaxCalculatorInstance();
        taxCalculator.setIsUnitTaxAmount(false);
        setTaxCalculator(taxCalculator);
    }

    public CurrencyIfc getFixedTaxAmount()
    {
        return ((FixedAmountTaxCalculator) getTaxCalculator()).getTaxAmount();
    }

    public void setFixedTaxAmount(CurrencyIfc value)
    {
        ((FixedAmountTaxCalculator) getTaxCalculator()).setTaxAmount(value);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc#calculateTax(oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc[], oracle.retail.stores.domain.transaction.TransactionTotalsIfc)
     */
    public void calculateTax(TaxLineItemInformationIfc[] items, TransactionTotalsIfc totals)
    {
        CurrencyIfc taxableAmount = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc taxAmount = DomainGateway.getBaseCurrencyInstance();

        for(int i = 0; i < items.length; i++)
        {
            CurrencyIfc itemTaxableAmount = getItemTaxableAmount(items[i]);
            taxableAmount = taxableAmount.add(itemTaxableAmount);
        }

        // Dont charge a tax if there were no taxable items.
        if(items.length > 0 && taxableAmount.signum() != CurrencyIfc.ZERO)
        {
            taxAmount = getTaxCalculator().calculateTaxAmount(taxableAmount, items);
        }

        TaxInformationIfc taxInformation = createTaxInformation(taxableAmount,
                taxAmount, TaxConstantsIfc.TAX_MODE_OVERRIDE_AMOUNT);
        totals.getTaxInformationContainer().addTaxInformation(taxInformation);


        prorate(items, taxableAmount, taxAmount);

    }

    /**
     * Prorate the tax charged
     *
     *  @param items
     *  @param taxableAmount
     *  @param taxAmount
     */
    public void prorate(TaxLineItemInformationIfc[] items, CurrencyIfc taxableAmount,
            CurrencyIfc taxAmount)
    {
        CurrencyIfc itemTaxableAmount = null;
        CurrencyIfc itemTaxAmount = null;
        CurrencyIfc workingTaxAmount = (CurrencyIfc) taxAmount.clone();
        CurrencyIfc workingTaxableAmount = (CurrencyIfc) taxableAmount.clone();
        TaxInformationIfc itemTaxInformation = null;

        for(int i = 0; i < items.length; i++)
        {
            itemTaxableAmount = getItemTaxableAmount(items[i]);

            itemTaxAmount = workingTaxAmount.prorate(itemTaxableAmount, workingTaxableAmount);

            workingTaxAmount = workingTaxAmount.subtract(itemTaxAmount);
            workingTaxableAmount = workingTaxableAmount.subtract(itemTaxableAmount);

            itemTaxInformation =  createTaxInformation(itemTaxableAmount,
                    itemTaxAmount, TaxConstantsIfc.TAX_MODE_OVERRIDE_AMOUNT);
            items[i].getTaxInformationContainer().addTaxInformation(itemTaxInformation);
            items[i].setTaxModReasonCode(getReasonCode());
        }
    }

}
