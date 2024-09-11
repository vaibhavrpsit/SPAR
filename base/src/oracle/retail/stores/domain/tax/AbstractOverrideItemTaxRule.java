/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/AbstractOverrideItemTaxRule.java /rgbustores_13.4x_generic_branch/2 2011/10/06 12:38:51 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       09/30/11 - change tax caculator api
 *    sgu       09/29/11 - set taxable line items to the tax calculator
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         4/25/2007 10:00:29 AM  Anda D. Cadar   I18N
 *      merge
 * 3    360Commerce 1.2         3/31/2005 4:27:07 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:19:27 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:09:20 PM  Robert Pearse
 *$$
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;


import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;

/**
 * Base class for overriding item tax
  */

public abstract class AbstractOverrideItemTaxRule extends AbstractTaxRule implements OverrideItemTaxRuleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -6091581334375073055L;

    /**
    The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.domain.tax.AbstractOverrideItemTaxRule.class);

    private int itemTaxIdentityHashCode;
    private int taxMode;

    public AbstractOverrideItemTaxRule(int taxModeValue)
    {
        taxMode = taxModeValue;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.TaxRuleIfc#calculateTax(oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc[], oracle.retail.stores.domain.transaction.TransactionTotalsIfc)
     */
    public void calculateTax(
        TaxLineItemInformationIfc[] items,
        TransactionTotalsIfc totals)
    {
        if(items.length == 0)
        {
            logger.error("Have item tax rule but no items found to match rule.  Rule unique ID:  " + getUniqueID());
        }
        else if(items.length == 1)
        {

            CurrencyIfc itemTaxableAmount = getItemTaxableAmount(items[0]);
            TaxCalculatorIfc taxCalculator = getTaxCalculator();
            CurrencyIfc itemTaxAmount = taxCalculator.calculateTaxAmount(itemTaxableAmount, items[0]);

            TaxInformationIfc taxInformation = createTaxInformation(itemTaxableAmount,
                    itemTaxAmount, taxMode);
            taxInformation.setUniqueID(String.valueOf(this.getItemTaxIdentityHashCode()));
            items[0].getTaxInformationContainer().addTaxInformation(taxInformation);


            totals.getTaxInformationContainer().addTaxInformation(taxInformation);
        }
        else
        {
            logger.error("Have item tax rule but too many items found that match rule.  No tax has been applied.  Rule unique ID:  " + getUniqueID());
        }
    }

    /**
     * @param item
     *
     */
    public boolean isRuleActiveForItem(TaxLineItemInformationIfc item)
    {
        boolean returnValue = super.isRuleActiveForItem(item);
        if(returnValue)
        {
            returnValue = itemTaxIdentityHashCode == item.getLineItemTaxIdentifier();
        }
        return returnValue;
    }


    /**
     * @return Returns the itemsIdentityHashCode.
     */
    public int getItemTaxIdentityHashCode()
    {
        return itemTaxIdentityHashCode;
    }

    /**
     * @param itemsIdentityHashCode The itemsIdentityHashCode to set.
     */
    public void setItemTaxIdentityHashCode(int value)
    {
        itemTaxIdentityHashCode = value;
    }

}
