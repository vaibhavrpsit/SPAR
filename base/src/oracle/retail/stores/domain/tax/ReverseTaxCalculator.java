/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/ReverseTaxCalculator.java /main/13 2012/10/19 12:46:36 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       10/17/12 - prorate item tax for partial pickup or cancellation
 *    sgu       09/30/11 - change tax caculator api
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 10:00:27 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:29:46 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:54 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:56 PM  Robert Pearse
 *
 *   Revision 1.5.2.1  2004/11/03 21:12:56  jdeleau
 *   @scr 7354 Make sure uniqueID doesn't exceed space allocated
 *   in the database of 35 characters.
 *
 *   Revision 1.5  2004/09/23 00:30:49  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.4  2004/08/05 16:30:07  jdeleau
 *   @scr 2775 Correct the way uniqueness of tax Info is stored, to always use uniqueID
 *
 *   Revision 1.3  2004/07/23 00:49:31  jdeleau
 *   @scr 6332 Make ReturnTaxRule extend TaxRuleIfc, so that
 *   it can be properly read over the network, it is not a runtime tax rule.
 *
 *   Revision 1.2  2004/07/22 17:37:19  jdeleau
 *   @scr 6408 Make sure reprint receipt is exactly the same as the original.  To
 *   do this the right data needed to be pulled from the database, and
 *   the uniqueID on the taxRule needed to be properly set.
 *
 *   Revision 1.1  2004/07/19 21:53:44  jdeleau
 *   @scr 6329 Fix the way post-void taxes were being retrieved.
 *   Fix for tax overrides, fix for post void receipt printing, add new
 *   tax rules for reverse transaction types.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;

/**
 * This class is intended to represent tax already charged on a line item, this rule
 * if for data operations that need to reverse that operation such as PostVoids.  Returns
 * have their own special tax calculator, and are not included here.
 *
 * $Revision: /main/13 $
 */
public class ReverseTaxCalculator extends AbstractTaxCalculator implements ReverseTaxCalculatorIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 6157476440434414935L;

    protected TaxInformationIfc taxInformationArray[] = new TaxInformationIfc[0];
    protected TaxInformationIfc calculatedTaxInformationArray[];

    /**
    * @return Clone of this calculator
    * @see java.lang.Object#clone()
    */
   public Object clone()
   {
       ReverseTaxCalculator calculator = new ReverseTaxCalculator();
       setCloneAttributes(calculator);
       return calculator;
   }

   /**
    * Clone this object
    *
    * @param newClass class to fill with cloned data
    */
   public void setCloneAttributes(ReverseTaxCalculator newClass)
   {
       super.setCloneAttributes(newClass);

       newClass.taxInformationArray = cloneTaxInformation(this.taxInformationArray);
       newClass.calculatedTaxInformationArray = cloneTaxInformation(this.calculatedTaxInformationArray);
   }

    /**
     * Set up how the tax charged for this item
     * @param taxInformation
     * @see oracle.retail.stores.domain.tax.ReverseTaxCalculatorIfc#setCalculationParameters(oracle.retail.stores.domain.tax.TaxInformationIfc[])
     */
    public void setCalculationParameters(TaxInformationIfc[] taxInformation)
    {
        if(taxInformation != null)
        {
            this.taxInformationArray = new TaxInformationIfc[taxInformation.length];
            for(int i=0; i<taxInformation.length; i++)
            {
                this.taxInformationArray[i] = (TaxInformationIfc) taxInformation[i].clone();
                if(taxInformation[i].getUniqueID().startsWith("Rev"))
                {
                    this.taxInformationArray[i].setUniqueID(taxInformation[i].getUniqueID());
                }
                else
                {
                    this.taxInformationArray[i].setUniqueID("Rev"+taxInformation[i].getUniqueID());
                }
                this.taxInformationArray[i].setUsesTaxRate(this.taxInformationArray[i].getTaxPercentage().signum() == CurrencyIfc.POSITIVE);
            }
        }
        else
        {
            this.taxInformationArray = new TaxInformationIfc[0];
        }
        // Set a cloned version as the calculated one. The taxInformationArray is to be immutable.
        TaxInformationIfc cloneArray[] = cloneTaxInformation(this.taxInformationArray);
        setCalculatedTaxInformation(cloneArray);
    }

    /**
     * Internal to this class only, clone one of its internal data arrays.
     *
     *  @return cloned taxInfo
     */
    protected TaxInformationIfc[] cloneTaxInformation(TaxInformationIfc[] arrayToClone)
    {
        TaxInformationIfc cloneArray[] = null;
        if(arrayToClone != null)
        {
            cloneArray = new TaxInformationIfc[arrayToClone.length];
            for(int i=0; i<arrayToClone.length; i++)
            {
                cloneArray[i] = (TaxInformationIfc) arrayToClone[i].clone();
            }
        }
        return cloneArray;
    }

    /**
     * Get the tax charged on this line item
     * @return calculated tax amount
     * @see oracle.retail.stores.domain.tax.ReverseTaxCalculatorIfc#getTaxInformation()
     */
    public TaxInformationIfc[] getTaxInformation()
    {
        if(this.calculatedTaxInformationArray == null)
        {
            this.calculatedTaxInformationArray = new TaxInformationIfc[0];
        }
        return this.calculatedTaxInformationArray;
    }

    /**
     * Set the calculated tax info
     *
     *  @param taxInfo
     */
    protected void setCalculatedTaxInformation(TaxInformationIfc[] taxInfo)
    {
        this.calculatedTaxInformationArray = taxInfo;
    }

    /**
     * @param amount Not used for this type of rule
     * @return tax charged
     * @see oracle.retail.stores.domain.tax.TaxCalculatorIfc#calculateTaxAmount(oracle.retail.stores.domain.currency.CurrencyIfc)
     */
    public CurrencyIfc calculateTaxAmount(CurrencyIfc taxableAmount, TaxLineItemInformationIfc[] lineItems)
    {
        CurrencyIfc totalTaxCharged = null;

        // Clone our baseline object, since it should be immutable we cant set values on it.
        TaxInformationIfc cloneArray[] = cloneTaxInformation(this.taxInformationArray);

        for(int i=0; i<cloneArray.length; i++)
        {
            if(totalTaxCharged == null)
            {
                totalTaxCharged = cloneArray[i].getTaxAmount();
            }
            else
            {
                totalTaxCharged = totalTaxCharged.add(cloneArray[i].getTaxAmount());
            }
        }
        setCalculatedTaxInformation(cloneArray);
        if(totalTaxCharged == null)
        {
            totalTaxCharged = DomainGateway.getBaseCurrencyInstance();
        }
        return totalTaxCharged;
    }
}
