/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/TaxExemptTaxRule.java /rgbustores_13.4x_generic_branch/2 2011/10/06 12:38:51 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       09/30/11 - change tax caculator api
 *    sgu       09/29/11 - set taxable line items to the tax calculator
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         4/25/2007 10:00:26 AM  Anda D. Cadar   I18N
 *      merge
 * 3    360Commerce 1.2         3/31/2005 4:30:19 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:25:46 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:14:41 PM  Robert Pearse
 *$
 *Revision 1.4  2004/09/23 00:30:49  kmcbride
 *@scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *Revision 1.3  2004/07/24 16:27:55  jdeleau
 *@scr 6430 Make Tax Exempt items save their tax correctly in the database.
 *
 *Revision 1.2  2004/06/07 19:58:59  mkp1
 *@scr 2775 Put correct header on files
 *$
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;


/**
 * @author mkp1
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class TaxExemptTaxRule extends AbstractTaxRule implements
        TaxExemptTaxRuleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5980170227662365239L;

    /**
     * Default constructor
     */
    public TaxExemptTaxRule()
    {
        setTaxCalculator(new FixedAmountTaxCalculator());
    }


    /**
     * Calculate the tax exempt tax info
     *
     * @param items items to tax
     * @param totals totals to put tax info into
     * @see oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc#calculateTax(oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc[], oracle.retail.stores.domain.transaction.TransactionTotalsIfc)
     */
    public void calculateTax(TaxLineItemInformationIfc[] items,
            TransactionTotalsIfc totals)
    {
        TaxInformationIfc taxInformation = createTaxInformation(TaxConstantsIfc.TAX_MODE_EXEMPT);
        TaxInformationIfc itemTaxInformation = null;

        TaxCalculatorIfc taxCalculator = getTaxCalculator();

        CurrencyIfc itemTaxableAmount = null;
        CurrencyIfc itemTaxAmount = null;
        for(int i=0; i<items.length; i++)
        {
            itemTaxableAmount = getItemTaxableAmount(items[i]);
            itemTaxAmount = taxCalculator.calculateTaxAmount(itemTaxableAmount, items[i]);

            itemTaxInformation =  createTaxInformation(itemTaxableAmount,
                    itemTaxAmount, TaxConstantsIfc.TAX_MODE_EXEMPT);

            items[i].getTaxInformationContainer().addTaxInformation(itemTaxInformation);


            taxInformation.add(itemTaxInformation);
        }
        totals.getTaxInformationContainer().addTaxInformation(taxInformation);
    }

}
