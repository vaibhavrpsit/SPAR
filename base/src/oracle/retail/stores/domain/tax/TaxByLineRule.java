/* ===========================================================================
* Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/TaxByLineRule.java /main/15 2013/04/26 10:17:58 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  04/26/13 - Fix to prevent overwriting of itemtaxinformation by
 *                         total taxinformation
 *    jswan     06/29/12 - Rename NewTaxRuleIfc to TaxRulesIfc
 *    jswan     02/04/12 - Re add threshold pricing (table table rate).
 *    jswan     02/04/12 - XbranchMerge jswan_bug13599093-rework from
 *                         rgbustores_13.4x_generic_branch
 *    jswan     02/02/12 - Refactor to add treshold tax back into the tax table
 *                         calculations.
 *    jswan     10/27/11 - Added repeating tax table calculation for taxable
 *                         amounts greater than the table upper limit.
 *    sgu       09/30/11 - change tax caculator api
 *    sgu       09/29/11 - set taxable line items to the tax calculator
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 6    360Commerce 1.5         4/25/2007 10:00:27 AM  Anda D. Cadar   I18N
 *      merge
 * 5    360Commerce 1.4         4/2/2007 5:52:15 PM    Snowber Khan    merge
 *      from v8x - CR 25856 - Updating to preserve "tax exempt amount" for
 *      record keeping - without treating it as a charged tax., CR 25856 -
 *      Updated to handle exemption of default tax rules.
 *
 * 4    360Commerce 1.3         1/25/2006 4:11:49 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:30:18 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:25:46 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:14:41 PM  Robert Pearse
 *$ 5    360Commerce1.4         4/2/2007 5:52:15 PM    Snowber Khan    merge from
 *      v8x - CR 25856 - Updating to preserve "tax exempt amount" for record
 *      keeping - without treating it as a charged tax., CR 25856 - Updated to
 *      handle exemption of default tax rules.
 * 4    360Commerce1.3         1/25/2006 4:11:49 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce1.2         3/31/2005 4:30:18 PM   Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:25:46 AM  Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:14:41 PM  Robert Pearse
 *$ 5    .v8x      1.3.1.0     3/9/2007 6:23:50 PM    Charles D. Baker CR 25856 -
 *       Updating to preserve "tax exempt amount" for record keeping - without
 *      treating it as a charged tax.
 * 4    360Commerce1.3         1/25/2006 4:11:49 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce1.2         3/31/2005 3:30:18 PM   Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:25:46 AM  Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:14:41 PM  Robert Pearse
 *$ 4    .v710     1.2.2.0     1/10/2006 1:42:27 PM   Deepanshu       CR 8345:
 *      MERGE v703 to v710 domain module
 * 3    360Commerce1.2         3/31/2005 3:30:18 PM   Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:25:46 AM  Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:14:41 PM  Robert Pearse
 *$:
 * 5    .v700     1.2.1.1     1/6/2006 12:37:44      Deepanshu       CR 6017:
 *      Calculate and save tax exempt
 * 4    .v700     1.2.1.0     12/14/2005 12:47:19    Deepanshu       CR 3884:
 *      Get the tax mode to set in tax information
 * 3    360Commerce1.2         3/31/2005 15:30:18     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:25:46     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:14:41     Robert Pearse
 *$ 4    .v700     1.2.1.0     12/14/2005 12:47:19 PM Deepanshu       CR 3884:
 *      Get the tax mode to set in tax information
 * 3    360Commerce1.2         3/31/2005 3:30:18 PM   Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:25:46 AM  Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:14:41 PM  Robert Pearse
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
 * @version 1.0
 * @created 13-Apr-2004 6:41:00 PM
 */
public class TaxByLineRule extends AbstractTaxRule implements TaxRuleIfc, TaxByLineRuleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 22679831477965519L;


    public TaxByLineRule()
    {
    }


    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.TaxRuleIfc#calculateTax(oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc[], oracle.retail.stores.domain.transaction.TransactionTotalsIfc)
     */
    public void calculateTax(TaxLineItemInformationIfc[] items, TransactionTotalsIfc totals)
    {
        TaxInformationIfc taxInformation = createTaxInformation(TaxConstantsIfc.TAX_MODE_STANDARD);

        TaxInformationIfc itemTaxInformation = null;

        TaxCalculatorIfc taxCalculator = getTaxCalculator();

        CurrencyIfc itemTaxableAmount = null;
        CurrencyIfc itemTaxAmount = null;

        for(int i = 0; i < items.length; i++ )
        {
            itemTaxableAmount = getItemTaxableAmount(items[i]);
            itemTaxAmount = taxCalculator.calculateTaxAmount(itemTaxableAmount, items[i]);
            
            int taxMode = items[i].getTaxMode();
            itemTaxInformation =  createTaxInformation(itemTaxableAmount,
                    itemTaxAmount, taxMode);

            items[i].getTaxInformationContainer().addTaxInformation(itemTaxInformation);


            taxInformation.add(itemTaxInformation);
        }
        // calculateEffectiveTaxRate(taxInformation);
        totals.getTaxInformationContainer().addTaxInformation(taxInformation);
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.AbstractTaxRule#calculateTax(oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc[], oracle.retail.stores.domain.tax.BaseTableTaxContainer[], oracle.retail.stores.domain.transaction.TransactionTotalsIfc)
     */
    public void calculateTax(TaxLineItemInformationIfc item,  CurrencyIfc taxableAmountForCalculation, 
    		CurrencyIfc additionalTaxAmount, TransactionTotalsIfc totals)
    {
    	// Calculate the total tax.
    	CurrencyIfc totalItemTaxableAmount = getItemTaxableAmount(item);
    	CurrencyIfc itemTaxAmount = getTaxCalculator().calculateTaxAmount(taxableAmountForCalculation, item);
    	itemTaxAmount = itemTaxAmount.add(additionalTaxAmount);

    	// Update the line item and transaction totals
    	TaxInformationIfc itemTaxInformation =  createTaxInformation(totalItemTaxableAmount,
    			itemTaxAmount, item.getTaxMode());
    	item.getTaxInformationContainer().addTaxInformation(itemTaxInformation);
    	if(totals.getTaxInformationContainer().getTaxInformation(itemTaxInformation.getUniqueID())==null)
    	{
    		totals.getTaxInformationContainer().addTaxInformation((TaxInformationIfc)itemTaxInformation.clone());
    	}
    	else 
    	{
    		totals.getTaxInformationContainer().addTaxInformation(itemTaxInformation);
    	}
    }
    
    /**
     * Calculate the exempt tax for all the items.  Save the results in the totals.
     * @param items TaxLineItemInformationIfc the array of items to calculate tax for.
     * @param totals TransactionTotalsIfc the totals object to save the results in.
     */
    public void calculateExepmtTax(TaxLineItemInformationIfc[] items, TransactionTotalsIfc totals)
    {
        TaxInformationIfc taxInformation = createTaxInformation(TaxConstantsIfc.TAX_MODE_EXEMPT);

        TaxInformationIfc itemTaxInformation = null;

        TaxCalculatorIfc taxCalculator = getTaxCalculator();

        CurrencyIfc itemTaxableAmount = null;
        CurrencyIfc itemTaxAmount = null;

        for(int i = 0; i < items.length; i++ )
        {
            itemTaxableAmount = getItemTaxableAmount(items[i]);
            itemTaxAmount = taxCalculator.calculateTaxAmount(itemTaxableAmount, items[i]);
            int taxMode = items[i].getTaxMode();
            itemTaxInformation =  createTaxInformation(itemTaxableAmount,
                    DomainGateway.getBaseCurrencyInstance(), taxMode);

            items[i].getTaxInformationContainer().addTaxInformation(itemTaxInformation);
            taxInformation.add(itemTaxInformation);
            totals.getTaxInformationContainer().addTaxExemptInformation(itemTaxAmount);
        }
        totals.getTaxInformationContainer().addTaxInformation(taxInformation);
    }
    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.AbstractTaxRule#clone()
     */
    public Object clone()
    {
        TaxByLineRule newClass = new TaxByLineRule();
        setCloneAttributes(newClass);
        return newClass;
    }

    //---------------------------------------------------------------------
    /**
     Set attributes for clone. <P>
     @param newClass new instance of TaxProrateRule
     **/
    //---------------------------------------------------------------------
    public void setCloneAttributes(TaxByLineRule newClass)
    {
        super.setCloneAttributes(newClass);
    }

}
