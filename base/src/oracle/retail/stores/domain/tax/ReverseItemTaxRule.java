/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/ReverseItemTaxRule.java /main/14 2012/07/02 10:12:52 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/02/12 - Tax cleanup in preparation for JPA conversion.
 *    jswan     02/04/12 - Fix issues with loading tax rules when some rules
 *                         have logical errors.
 *    jswan     02/03/12 - XbranchMerge jswan_bug-13599093 from
 *                         rgbustores_13.4x_generic_branch
 *    jswan     01/30/12 - Modified to: 1) provide a more detailed log message
 *                         when a tax rule is invalid, and 2) allow valid tax
 *                         rules to load even if one or more other rules are
 *                         not valid.
 *    sgu       09/30/11 - change tax caculator api
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 4    360Commerce 1.3         7/21/2006 4:14:15 PM   Brendan W. Farrell Merge
 *    from v7.x.  Use ifc so that it is extendable.
 3    360Commerce 1.2         3/31/2005 4:29:46 PM   Robert Pearse
 2    360Commerce 1.1         3/10/2005 10:24:54 AM  Robert Pearse
 1    360Commerce 1.0         2/11/2005 12:13:56 PM  Robert Pearse
 *
Revision 1.3  2004/09/23 00:30:48  kmcbride
@scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
Revision 1.2  2004/07/28 21:11:02  jdeleau
@scr 6578 return rules had the same uniqueID for different items,
causing tax to be calculated incorrectly.
 *
Revision 1.1  2004/07/26 14:39:55  jdeleau
@scr 2775 Rename ReturnItemTaxRule to ReverseItemTaxRule add isReturn
method, to the ReverseItemTaxRule interface
 *
Revision 1.6  2004/07/23 18:15:22  jdeleau
@scr 2775 Minor updates to the way tax works
 *
Revision 1.5  2004/07/23 00:49:31  jdeleau
@scr 6332 Make ReturnTaxRule extend TaxRuleIfc, so that
it can be properly read over the network, it is not a runtime tax rule.
 *
Revision 1.4  2004/07/19 21:53:44  jdeleau
@scr 6329 Fix the way post-void taxes were being retrieved.
Fix for tax overrides, fix for post void receipt printing, add new
tax rules for reverse transaction types.
 *
Revision 1.3  2004/07/07 22:10:34  jdeleau
@scr 5785 Tax on returns needs to be pro-rated if the original tax was
on a quantified number of items.  The pro-rating needs to have
no rounding errors in the longer term, so that if a person is taxed
68 cents on 5 items, he will only be refunded 68 cents even if he returns
the 5 items one at a time.
 *
Revision 1.2  2004/06/29 21:29:15  jdeleau
@scr 5777 Improve on the way return taxes are calculated, to solve this defect.
Returns and purchases were going into the same container, and return
values were being cleared on subsequent calculations.
 *
Revision 1.1  2004/06/14 13:50:33  mkp1
@scr 2775 Changed returns that are retrieved not to recalculate tax
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;

import org.apache.log4j.Logger;


/**
 * @author jdeleau
 * @version $Revision: /main/14 $
 */
public class ReverseItemTaxRule extends AbstractTaxRule implements ReverseItemTaxRuleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 8590343521830006606L;

    protected static Logger logger = Logger.getLogger(oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc.class);

    /**
     * Tells whether or not this tax rule is for a return
     */
    protected boolean isReturn;

    /**
     * Default constructor
     */
    public ReverseItemTaxRule()
    {
    }

    /**
     * Calculate tax
     *
     * @param items Items to calculate tax on
     * @param totals totals to put results in
     * @see oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc#calculateTax(oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc[], oracle.retail.stores.domain.transaction.TransactionTotalsIfc)
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
            if(getTaxCalculator() instanceof ReverseTaxCalculatorIfc)
            {
                TaxInformationIfc taxInformation[] = ((ReverseTaxCalculatorIfc)getTaxCalculator()).getTaxInformation();
                // Tax information has not been calculated yet if its positive.  To continue
                // calculating multiple times is bad, because the taxInfo is not the info in the
                // original transaction.
                if(taxInformation != null && taxInformation.length > 0)
                {
                    getTaxCalculator().calculateTaxAmount(null, new TaxLineItemInformationIfc[0]);
                    // Set the new taxInformation on the appropriate objects
                    taxInformation = ((ReverseTaxCalculatorIfc)getTaxCalculator()).getTaxInformation();
                    for(int i=0; i<taxInformation.length; i++)
                    {
                        items[0].getTaxInformationContainer().addTaxInformation((TaxInformationIfc)taxInformation[i].clone());
                        totals.getTaxInformationContainer().addTaxInformation((TaxInformationIfc) taxInformation[i].clone());
                    }
                }
            }
            else
            {
                if(getTaxCalculator() != null)
                {
                    logger.error("Unexpected calculator, returns can only handle return/reverse calculators not "+getTaxCalculator().getClass().getName());
                }
                else
                {
                    logger.error("Unexpected calculator, returns can only handle return/reverse calculators not NULL calculators");
                }
            }

        }
        else
        {
            logger.error("Have item tax rule but too many items found that match rule.  No tax has been applied.  Rule unique ID:  " + getUniqueID());
        }
    }

    /**
     * Returns whether this tax rule is valid or not.
     * @return true if valid, otherwise false.
     */
    public boolean isValid()
    {
        boolean valid = true;
        String message = null;

        if (getTaxCalculator() == null)
        {
            valid = false;
            message = getValidationErrorMessage("ReverseItemTaxRule is invalid since the Tax Calculator is null.");
            logger.error(message);
        }

        return valid;
    }

    /**
     * Retrieve the tax information for this return item
     * @return the tax information for this return item
     */
    public TaxInformationIfc[] getTaxInformation()
    {
        TaxInformationIfc[] taxInformation = new TaxInformationIfc[0];
        if(getTaxCalculator() instanceof ReturnTaxCalculatorIfc)
        {
            taxInformation = ((ReturnTaxCalculatorIfc)getTaxCalculator()).getTaxInformation();
        }
        return taxInformation;
    }

    /**
     * Tells whether or not this tax rule is for a return
     *
     * @return true or false
     * @see oracle.retail.stores.domain.tax.ReverseItemTaxRuleIfc#isReturn()
     */
    public boolean isReturn()
    {
        return this.isReturn;
    }

    /**
     * Set whether or not this rule is a return
     *
     * @param value
     * @see oracle.retail.stores.domain.tax.ReverseItemTaxRuleIfc#setReturn(boolean)
     */
    public void setReturn(boolean value)
    {
        this.isReturn = value;
    }

    /**
     * Clone this object
     *
     * @return cloned object
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        ReverseItemTaxRule taxRule = new ReverseItemTaxRule();
        setCloneAttributes(taxRule);
        return taxRule;
    }

    /**
     * Set the clone attributes
     *
     *  @param rule
     */
    public void setCloneAttributes(ReverseItemTaxRule rule)
    {
        super.setCloneAttributes(rule);
        rule.isReturn = isReturn();
    }
    public void setUniqueID(String id)
    {
        super.setUniqueID(id);
    }
}
