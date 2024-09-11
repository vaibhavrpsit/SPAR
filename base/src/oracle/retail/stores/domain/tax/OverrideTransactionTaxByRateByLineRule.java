/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/OverrideTransactionTaxByRateByLineRule.java /main/12 2012/09/06 12:41:33 sgu Exp $
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
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;

/**
 * @author mkp1
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OverrideTransactionTaxByRateByLineRule
    extends AbstractOverrideTransactionByRateTaxRule
{

    /** serialVersionUID */
    private static final long serialVersionUID = 2042850565039141370L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc#calculateTax(oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc[], oracle.retail.stores.domain.transaction.TransactionTotalsIfc)
     */
    public void calculateTax(
        TaxLineItemInformationIfc[] items,
        TransactionTotalsIfc totals)
    {
        TaxInformationIfc taxInformation = createTaxInformation(TaxConstantsIfc.TAX_MODE_OVERRIDE_RATE);

        TaxInformationIfc itemTaxInformation = null;

        TaxCalculatorIfc taxCalculator = getTaxCalculator();

        CurrencyIfc itemTaxableAmount = null;
        CurrencyIfc itemTaxAmount = null;

        for(int i = 0; i < items.length; i++ )
        {
            itemTaxableAmount = getItemTaxableAmount(items[i]);

            itemTaxAmount = DomainGateway.getBaseCurrencyInstance();
            if(itemTaxableAmount.signum() != CurrencyIfc.ZERO)
            {
                itemTaxAmount = taxCalculator.calculateTaxAmount(itemTaxableAmount, items[i]);
            }

            itemTaxInformation = createTaxInformation(itemTaxableAmount,
                    itemTaxAmount, TaxConstantsIfc.TAX_MODE_OVERRIDE_RATE);

            items[i].getTaxInformationContainer().addTaxInformation(itemTaxInformation);
            items[i].setTaxModReasonCode(getReasonCode());

            taxInformation.add(itemTaxInformation);
        }

        // calculateEffectiveTaxRate(taxInformation);

        totals.getTaxInformationContainer().addTaxInformation(taxInformation);

    }

}
