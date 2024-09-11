/* ===========================================================================
* Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/TaxRuleItemContainer.java /main/14 2013/02/06 16:33:45 rgour Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rgour     02/05/13 - putting a check if tax is exempted while calculating
 *                         tax
 *    jswan     07/02/12 - Tax cleanup in preparation for JPA conversion.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         1/25/2006 4:11:50 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:30:20 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:25:49 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:14:44 PM  Robert Pearse
 *$ 4    .v710     1.2.2.0     1/10/2006 1:42:27 PM   Deepanshu       CR 8345:
 *      MERGE v703 to v710 domain module
 * 3    360Commerce1.2         3/31/2005 3:30:20 PM   Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:25:49 AM  Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:14:44 PM  Robert Pearse
 *$:
 * 4    .v700     1.2.1.0     1/6/2006 12:37:49      Deepanshu       CR 6017:
 *      Calculate and save tax exempt
 * 3    360Commerce1.2         3/31/2005 15:30:20     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:25:49     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:14:44     Robert Pearse
 *$$
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;

import java.util.ArrayList;

/**
 * @author mkp1
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TaxRuleItemContainer implements TaxRuleItemContainerIfc
{
    private ArrayList<TaxLineItemInformationIfc> items = new ArrayList<TaxLineItemInformationIfc>();
    private RunTimeTaxRuleIfc taxRule;
    private int taxScope;

    /**
     *Constructor for tax rule item container
     *
     */
    public TaxRuleItemContainer()
    {
    }

    /**
     * Constructor for tax rule item container
     * @param taxRuleValue the tax rule
     */
    public TaxRuleItemContainer(RunTimeTaxRuleIfc taxRuleValue)
    {
        taxRule = taxRuleValue;
    }

    /**
     *Keeps a copy of the line item if the tax rule indicates
     *that it is needed to compute the tax amount for this rule
     *@param item the item to ask the tax rule whether it should be added
     *@param taxRules all of the tax rules that could potentially get
     *      applied to this line item
     */
    public void addItem(TaxLineItemInformationIfc item, RunTimeTaxRuleIfc[] taxRules)
    {
        if(taxRule.shouldAddItem(item, taxRules) == true)
        {
            items.add(item);
        }
    }

    /**
     *Retrieve the items that the tax rules needs to compute the tax amount
     *@return array of TaxLineItemInformationIfc
     */
    public TaxLineItemInformationIfc[] getItems()
    {
        return items.toArray(new TaxLineItemInformationIfc[0]);
    }

    /**
     * Retrieves the tax rule associated with the container
     * @return the tax rule associated with the container
     */
    public RunTimeTaxRuleIfc getTaxRule()
    {
        return taxRule;
    }

    /**
     * Set the tax rule associated with this container
     * @param value the tax rule to associate with this container
     */
    public void setTaxRule(RunTimeTaxRuleIfc value)
    {
        taxRule = value;
    }

    /**
     * Calculate the tax for the items in this container
     * and the tax rule associated with this container
     * @param totals the transaction totals to record the tax total results
     */
    public void calculateTax(TransactionTotalsIfc totals)
    {
        if(items.isEmpty() == false )
        {
            taxRule.calculateTax(getItems(), totals);

            // Set the tax scope once the tax info containers have been set by the calculate tax method
            TaxLineItemInformationIfc[] items = getItems();
            for(int i=0; i<items.length; i++)
            {
                items[i].getTaxInformationContainer().setTaxScope(getTaxScope());
            }
            totals.getTaxInformationContainer().setTaxScope(getTaxScope());
        }
    }

    /**
	 * Calculate the exempt tax for the items in this container
	 * and the tax rule associated with this container
	 * @param totals TransactionTotalsIfc the transaction totals to record the tax total results
	 */
    public void calculateExepmtTax(TransactionTotalsIfc totals)
    {
        if (items.isEmpty() == false)
        {
            if (taxRule instanceof TaxByLineRule)
            {
                ((TaxByLineRuleIfc)taxRule).calculateExepmtTax(getItems(), totals);
            }
            else if (taxRule instanceof TaxProrateRule)
            {
                ((TaxProrateRule)taxRule).calculateTax(getItems(), totals, TaxConstantsIfc.TAX_MODE_EXEMPT);
            }
            else
            {
                taxRule.calculateTax(getItems(), totals);
            }
            TaxLineItemInformationIfc[] items = getItems();
            for (int i = 0; i < items.length; i++)
            {
                items[i].getTaxInformationContainer().setTaxScope(getTaxScope());
            }
            totals.getTaxInformationContainer().setTaxScope(getTaxScope());
        }
    }

	/**
     * Set the tax scope
     *
     * @param scope
     * @see oracle.retail.stores.domain.tax.TaxRuleItemContainerIfc#setTaxScope(int)
     */
    public void setTaxScope(int scope)
    {
        this.taxScope = scope;
    }

    /**
     * Get the tax scope
     *
     * @return scope
     * @see oracle.retail.stores.domain.tax.TaxRuleItemContainerIfc#getTaxScope()
     */
    public int getTaxScope()
    {
        return this.taxScope;
    }


}
