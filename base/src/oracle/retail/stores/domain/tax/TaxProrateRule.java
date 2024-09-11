/* ===========================================================================
* Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/TaxProrateRule.java /main/12 2013/02/06 16:33:46 rgour Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rgour     02/05/13 - putting a check if tax is exempted while calculating
 *                         tax
 *    jswan     06/29/12 - Rename NewTaxRuleIfc to TaxRulesIfc
 *    sgu       10/04/11 - rework table tax using tax rules instead of
 *                         calculator
 *    sgu       09/30/11 - change tax caculator api
 *    sgu       09/29/11 - set taxable line items to the tax calculator
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         4/25/2007 10:00:24 AM  Anda D. Cadar   I18N
 *      merge
 * 3    360Commerce 1.2         3/31/2005 4:30:20 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:25:48 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:14:43 PM  Robert Pearse
 *$$
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.factory.DomainObjectFactoryIfc;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;





/**
 * @version 1.0
 * @created 13-Apr-2004 6:41:26 PM
 */
public class TaxProrateRule extends AbstractTaxRule implements TaxRuleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -256871400345584591L;


    public TaxProrateRule()
    {
    }

    /**
     * Calculate tax for the list of items
     * 
     * @param items the item lists
     * @param transactionTotals the transaction totals
     * @param taxMode the transaction taxMode
     */
    public void calculateTax(TaxLineItemInformationIfc[] items, TransactionTotalsIfc totals, int taxMode)
    {
        calculateTax(items, CurrencyIfc.POSITIVE, totals, taxMode);
        calculateTax(items, CurrencyIfc.NEGATIVE, totals, taxMode);
    }
    /**
     * Calculate tax for the list of items
     * @param items the item lists
     * @param transactionTotals the transaction totals
     *
     */
    public void calculateTax(TaxLineItemInformationIfc[] items, TransactionTotalsIfc totals)
    {
        calculateTax(items, CurrencyIfc.POSITIVE, totals, TaxConstantsIfc.TAX_MODE_STANDARD);
        calculateTax(items, CurrencyIfc.NEGATIVE, totals, TaxConstantsIfc.TAX_MODE_STANDARD);
    }

    /**
     * Calculate tax for items of the give sign
     * 
     * @param items the item list
     * @param sign the sign can be CurrencyIfc.POSITIVE or CurrencyIfc.NEGATIVE
     * @param totals the transaction totals
     * @param taxMode the transaction taxMode
     */
    protected void calculateTax(TaxLineItemInformationIfc[] items, int sign, TransactionTotalsIfc totals , int taxMode)
    {
        ArrayList<TaxLineItemInformationIfc> taxableItemsList = new ArrayList<TaxLineItemInformationIfc>();
        CurrencyIfc taxableAmount = getItemTaxableAmount(items, sign, taxableItemsList);
        TaxLineItemInformationIfc[] taxableItems = taxableItemsList.toArray(new TaxLineItemInformationIfc[0]);

        TaxCalculatorIfc taxCalculator = getTaxCalculator();
        CurrencyIfc taxAmount = taxCalculator.calculateTaxAmount(taxableAmount, taxableItems);

        TaxInformationIfc taxInformation = createTaxInformation(taxableAmount,
                taxAmount, taxMode);
        totals.getTaxInformationContainer().addTaxInformation(taxInformation);
        prorate(taxableItems, taxableAmount, taxAmount, taxMode);
    }

    public void prorate(TaxLineItemInformationIfc[] items, CurrencyIfc taxableAmount,
            CurrencyIfc taxAmount , int taxMode)
    {
        DomainObjectFactoryIfc factory = DomainGateway.getFactory();
        CurrencyIfc itemTaxableAmount = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc itemTaxAmount = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc workingTaxAmount = (CurrencyIfc) taxAmount.clone();
        CurrencyIfc workingTaxableAmount = (CurrencyIfc) taxableAmount.clone();
        TaxInformationIfc itemTaxInformation = null;

        for(int i = 0; i < items.length; i++)
        {
            itemTaxableAmount = getItemTaxableAmount(items[i]);

            itemTaxAmount = workingTaxAmount.prorate(itemTaxableAmount, workingTaxableAmount);

            workingTaxAmount = workingTaxAmount.subtract(itemTaxAmount);
            workingTaxableAmount = workingTaxableAmount.subtract(itemTaxableAmount);

            itemTaxInformation = factory.getTaxInformationInstance();
            if (taxMode == TaxConstantsIfc.TAX_MODE_EXEMPT)
            {
                itemTaxInformation = createTaxInformation(itemTaxableAmount, DomainGateway.getBaseCurrencyInstance(),
                        taxMode);
            }
            else
            {
                itemTaxInformation = createTaxInformation(itemTaxableAmount, itemTaxAmount, taxMode);
            }

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


    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.AbstractTaxRule#clone()
     */
    public Object clone()
    {
        TaxProrateRule newClass = new TaxProrateRule();
        setCloneAttributes(newClass);
        return newClass;
    }

    //---------------------------------------------------------------------
    /**
     Set attributes for clone. <P>
     @param newClass new instance of TaxProrateRule
     **/
    //---------------------------------------------------------------------
    public void setCloneAttributes(TaxProrateRule newClass)
    {
        super.setCloneAttributes(newClass);
    }


}
