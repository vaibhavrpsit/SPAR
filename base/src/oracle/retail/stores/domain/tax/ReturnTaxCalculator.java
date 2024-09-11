/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/ReturnTaxCalculator.java /main/13 2014/07/10 13:27:45 amishash Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    amishash  07/10/14 - Retrieving the scale from Currency for tax
 *                         calculation.
 *    sgu       10/21/11 - negate return tax correctly
 *    sgu       09/30/11 - change tax caculator api
 *    sgu       08/02/11 - keep whole scale for multiplication during tax
 *                         calculation
 *    sgu       08/01/11 - donot round tax amount during tax calculation
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    10   360Commerce 1.9         4/1/2008 11:25:11 AM   Jack G. Swan
 *         Modified to use tax mode for items read from the database.
 *    9    360Commerce 1.8         4/25/2007 10:00:27 AM  Anda D. Cadar   I18N
 *         merge
 *    8    360Commerce 1.7         4/5/2006 11:31:30 AM   Michael Wisbauer Was
 *         not prorating Effective taxable amount when returning item
 *    7    360Commerce 1.6         3/30/2006 4:54:05 PM   Michael Wisbauer
 *         Removed prorating amounts when updating negitive amounts.
 *    6    360Commerce 1.5         1/25/2006 4:11:44 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    5    360Commerce 1.4         1/22/2006 11:41:55 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    4    360Commerce 1.3         12/13/2005 4:43:51 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:29:46 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:53 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:55 PM  Robert Pearse
 *: ReturnTaxCalculator.java,v $
 *    7    .v710     1.2.2.1     10/25/2005 17:22:09    Charles Suehs   Merge
 *         from v700
 *    6    .v710     1.2.2.0     10/24/2005 15:48:09    Charles Suehs   Merge
 *         from ReturnTaxCalculator.java, Revision 1.2.1.0
 *    5    .v700     1.2.1.1     1/4/2006 11:08:45      Jason L. DeLeau 7383:
 *         Fix taxMode setting for a return item
 *    4    .v700     1.2.1.0     9/28/2005 12:28:02     Jason L. DeLeau 4067:
 *         Fix the way a receipt prints for a price adjusted threshold item,
 *         where the price adjustment is below the threshold,
 *         and the original price is above it.
 *    3    360Commerce1.2         3/31/2005 15:29:46     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:24:53     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:13:55     Robert Pearse
 *
 *   Revision 1.7.2.1  2004/11/03 21:12:56  jdeleau
 *   @scr 7354 Make sure uniqueID doesn't exceed space allocated
 *   in the database of 35 characters.
 *
 *   Revision 1.7  2004/09/23 00:30:48  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.6  2004/08/05 16:30:07  jdeleau
 *   @scr 2775 Correct the way uniqueness of tax Info is stored, to always use uniqueID
 *
 *   Revision 1.5  2004/08/04 18:24:33  jdeleau
 *   @scr 6773 Fix subtotal amounts on receipt for price-adjusted items where
 *   there are more than 2 items on the receipt with different tax rules applied
 *   to each item.
 *
 *   Revision 1.4  2004/07/19 21:53:44  jdeleau
 *   @scr 6329 Fix the way post-void taxes were being retrieved.
 *   Fix for tax overrides, fix for post void receipt printing, add new
 *   tax rules for reverse transaction types.
 *
 *   Revision 1.3  2004/07/07 22:10:34  jdeleau
 *   @scr 5785 Tax on returns needs to be pro-rated if the original tax was
 *   on a quantified number of items.  The pro-rating needs to have
 *   no rounding errors in the longer term, so that if a person is taxed
 *   68 cents on 5 items, he will only be refunded 68 cents even if he returns
 *   the 5 items one at a time.
 *
 *   Revision 1.2  2004/06/30 21:26:55  jdeleau
 *   @scr 5921 Void transactions were doubling returned tax on
 *   kit Items.  This is because the header and individual line items were
 *   both calculating tax.  This is now corrected.
 *
 *   Revision 1.1  2004/06/29 21:29:15  jdeleau
 *   @scr 5777 Improve on the way return taxes are calculated, to solve this defect.
 *   Returns and purchases were going into the same container, and return
 *   values were being cleared on subsequent calculations.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;

/**
 *
 * $Revision: /main/13 $
 */
public class ReturnTaxCalculator extends AbstractTaxCalculator implements ReturnTaxCalculatorIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3721786160163339976L;

    private TaxInformationIfc taxInformationArray[] = new TaxInformationIfc[0];
    private TaxInformationIfc calculatedTaxInformationArray[];
    private BigDecimal quantityPurchased;
    private BigDecimal quantityReturnable;
    private BigDecimal quantityBeingReturned;

    /**
     * @return Clone of this calculator
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        ReturnTaxCalculator calculator = new ReturnTaxCalculator();
        setCloneAttributes(calculator);
        return calculator;
    }

    /**
     * Set the clone attributes
     *
     * @param newClass
     */
    public void setCloneAttributes(ReturnTaxCalculator newClass)
    {
        super.setCloneAttributes(newClass);

        newClass.taxInformationArray = cloneTaxInformation(this.taxInformationArray);
        newClass.calculatedTaxInformationArray = cloneTaxInformation(this.calculatedTaxInformationArray);
        newClass.quantityPurchased = new BigDecimal(quantityPurchased.doubleValue());
        newClass.quantityReturnable = new BigDecimal(quantityReturnable.doubleValue());
        newClass.quantityBeingReturned = new BigDecimal(quantityBeingReturned.doubleValue());
    }

    /**
     * Set the tax calculation parameters
     *
     * @param taxInformation
     * @see oracle.retail.stores.domain.tax.ReturnTaxCalculatorIfc#setCalculationParameters(oracle.retail.stores.domain.tax.TaxInformationIfc[])
     */
    public void setCalculationParameters(TaxInformationIfc[] taxInformation)
    {
        if(taxInformation != null)
        {
            this.taxInformationArray = new TaxInformationIfc[taxInformation.length];
            for(int i=0; i<taxInformation.length; i++)
            {
                this.taxInformationArray[i] = (TaxInformationIfc) taxInformation[i].clone();
                int holidayValue = 0;
                if(taxInformationArray[i].getTaxHoliday() == true)
                {
                    holidayValue = 1;
                }
                String uniqueID = taxInformationArray[i].getTaxAuthorityID()+"-"+taxInformation[i].getTaxGroupID()+"-"+
                   taxInformationArray[i].getTaxTypeCode()+"-"+holidayValue;
                this.taxInformationArray[i].setUniqueID(uniqueID);
                this.taxInformationArray[i].setUsesTaxRate(this.taxInformationArray[i].getTaxPercentage().signum() == CurrencyIfc.POSITIVE);
                this.taxInformationArray[i].setTaxMode(taxInformation[i].getTaxMode());
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
     * Set the calculated tax info
     *
     *  @param taxInfo
     */
    protected void setCalculatedTaxInformation(TaxInformationIfc[] taxInfo)
    {
        this.calculatedTaxInformationArray = taxInfo;
    }

    /**
     * Return the tax info, with taxes calculated correctly if calculateTax has been called
     *
     *  @return Tax info
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
     * Calculate the tax amount for a return
     * @param amount Not used, for returns all amounts are in the TaxInformationIfc[] array.
     * @return Tax charged
     * @see oracle.retail.stores.domain.tax.TaxCalculatorIfc#calculateTaxAmount(oracle.retail.stores.domain.currency.CurrencyIfc)
     */
    public CurrencyIfc calculateTaxAmount(CurrencyIfc amount, TaxLineItemInformationIfc[] lineItems)
    {
        CurrencyIfc totalTaxCharged = null;

        // Clone our baseline object, since it should be immutable we cant set values on it.
        TaxInformationIfc cloneArray[] = cloneTaxInformation(this.taxInformationArray);

        for(int i=0; i<cloneArray.length; i++)
        {
            CurrencyIfc tax = prorateCurrency(cloneArray[i].getTaxAmount());
            CurrencyIfc taxableAmount = prorateCurrency(cloneArray[i].getTaxableAmount());

            // Make sure tax is negative
            CurrencyIfc negativeTax = tax.abs().negate();
            CurrencyIfc negativeTaxableAmount = taxableAmount.abs().negate();
            cloneArray[i].setEffectiveTaxableAmount(negativeTaxableAmount);
            cloneArray[i].setTaxableAmount(negativeTaxableAmount);
            cloneArray[i].setTaxAmount(negativeTax);

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

    /**
     * Given a currency, pro-rate it based on the items being returned.  The calculation is this:
     * 1) Find out how many items were already returned <BR>
     * 2) Figure out the tax due from the already returned items <BR>
     * 3) Find out the tax for the already returned items + the currently being returned items <BR>
     * 4) Subtract the already returned total from the tax that calculated in step 4. <BR>
     *
     * @param tax
     * @return prorated amount
     */
    protected CurrencyIfc prorateCurrency(CurrencyIfc tax)
    {
        BigDecimal quantityItemsAlreadyReturned = getQuantityPurchased().subtract(getQuantityReturnable());
        int multiScale = tax.getScale() + quantityItemsAlreadyReturned.abs().scale();
        CurrencyIfc taxAlreadyRefunded = tax.multiply(quantityItemsAlreadyReturned.abs(), multiScale, getRoundingMode())
            .divide(getQuantityPurchased(), tax.getScale(), getRoundingMode());
        BigDecimal quantityItemsToTax = quantityItemsAlreadyReturned.add(getQuantityBeingReturned()).abs();
        multiScale = tax.getScale() + quantityItemsToTax.scale();
        CurrencyIfc taxToRefund = tax.multiply(quantityItemsToTax, multiScale, getRoundingMode())
            .divide(getQuantityPurchased(), tax.getScale(), getRoundingMode());
        CurrencyIfc netTaxRefund = taxToRefund.subtract(taxAlreadyRefunded);
        return netTaxRefund;
    }

    /**
     * Internal to this class only, clone one of its internal data arrays.
     * @param arrayToClone array of TaxInformationIfc data to clone
     * @return cloned taxInfo
     */
    private TaxInformationIfc[] cloneTaxInformation(TaxInformationIfc[] arrayToClone)
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
     * Return the number of items being returned
     *
     * @return Returns the quantityBeingReturned
     * @see oracle.retail.stores.domain.tax.ReturnTaxCalculatorIfc#getQuantityBeingReturned()
     */
    public BigDecimal getQuantityBeingReturned()
    {
        if(quantityBeingReturned == null)
        {
            quantityBeingReturned = new BigDecimal(1.0);
        }
        return quantityBeingReturned;
    }

    /**
     * Set the number of items being returned
     *
     * @param quantityBeingReturned
     * @see oracle.retail.stores.domain.tax.ReturnTaxCalculatorIfc#setQuantityBeingReturned(com.ibm.math.BigDecimal)
     */
    public void setQuantityBeingReturned(BigDecimal quantityBeingReturned)
    {
        this.quantityBeingReturned = quantityBeingReturned;
    }

    /**
     * Get the quantity purchased
     *
     * @return quantity purchased
     * @see oracle.retail.stores.domain.tax.ReturnTaxCalculatorIfc#getQuantityPurchased()
     */
    public BigDecimal getQuantityPurchased()
    {
        if(quantityPurchased == null)
        {
            quantityPurchased = new BigDecimal(1.0);
        }
        return quantityPurchased;
    }

    /**
     * Set the quantity of items purchased
     *
     * @param quantityPurchased
     * @see oracle.retail.stores.domain.tax.ReturnTaxCalculatorIfc#setQuantityPurchased(com.ibm.math.BigDecimal)
     */
    public void setQuantityPurchased(BigDecimal quantityPurchased)
    {
        this.quantityPurchased = quantityPurchased;
    }

    /**
     * Get the quantity of returnable items
     *
     * @return quantityReturnable
     * @see oracle.retail.stores.domain.tax.ReturnTaxCalculatorIfc#getQuantityReturnable()
     */
    public BigDecimal getQuantityReturnable()
    {
        if(quantityReturnable == null)
        {
            quantityReturnable = new BigDecimal(1.0);
        }
        return quantityReturnable;
    }

    /**
     * Set the quantity of items returnable
     *
     * @param quantityReturnable
     * @see oracle.retail.stores.domain.tax.ReturnTaxCalculatorIfc#setQuantityReturnable(com.ibm.math.BigDecimal)
     */
    public void setQuantityReturnable(BigDecimal quantityReturnable)
    {
        this.quantityReturnable = quantityReturnable;
    }
}
