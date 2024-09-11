/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/TaxTableLineItem.java /main/13 2012/07/02 10:12:52 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/02/12 - Tax cleanup in preparation for JPA conversion.
 *    sgu       10/04/11 - rework table tax using tax rules instead of
 *                         calculator
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 5    360Commerce 1.4         4/25/2007 10:00:24 AM  Anda D. Cadar   I18N
 *      merge
 * 4    360Commerce 1.3         1/22/2006 11:41:56 AM  Ron W. Haight   Removed
 *      references to com.ibm.math.BigDecimal
 * 3    360Commerce 1.2         3/31/2005 4:30:20 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:25:49 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:14:44 PM  Robert Pearse
 *$ 4    360Commerce1.3         1/22/2006 11:41:56 AM  Ron W. Haight   Removed
 *      references to com.ibm.math.BigDecimal
 * 3    360Commerce1.2         3/31/2005 4:30:20 PM   Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:25:49 AM  Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:14:44 PM  Robert Pearse
 *$$
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;

/**
 * @author mkp1
 *
 * From the RU_TX_RT table, if CD_TYP is 1, then the table is percentage based.
 * If CD_TYP is 2, then it is amount based.  If its percentage based, the value of
 * the tax comes from PE_TX.  If it is amount based, it comes from MO_TX.
 */
public class TaxTableLineItem implements TaxTableLineItemIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -594115310278817584L;

    private CurrencyIfc maxTaxableAmount;
    private TaxCalculatorIfc taxCalculator;

    /**
     * Default tax table constructor
     */
    public TaxTableLineItem()
    {
    }

    /**
     * Retrieve the max table amount to apply this tax to
     * @return max taxable amount to use the amount of tax for
     */
    public CurrencyIfc getMaxTaxableAmount()
    {
        return maxTaxableAmount;
    }

    /**
     * Set the max taxable amount
     * @param value the max taxable amount
     */
    public void setMaxTaxableAmount(CurrencyIfc value)
    {
        maxTaxableAmount = value;
    }

    /**
     * @return the tax calculator
     */
    public TaxCalculatorIfc getTaxCalculator()
    {
        return taxCalculator;
    }

    /**
     * Set the tax calculator
     * @param taxCalculator the calculator
     */
    public void setTaxCalculator(TaxCalculatorIfc taxCalculator)
    {
        this.taxCalculator = taxCalculator;
    }

    /**
     * Clone this object
     *
     * @return a copy of this object
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        TaxTableLineItem newClass = new TaxTableLineItem();
        setCloneAttributes(newClass);
        return newClass;
    }

    /**
     * Set attributes for clone. <P>
     * @param newClass new instance of TaxTableLineItem
     **/
    public void setCloneAttributes(TaxTableLineItem newClass)
    {
        if(getMaxTaxableAmount() != null)
        {
            newClass.setMaxTaxableAmount((CurrencyIfc) getMaxTaxableAmount().clone());
        }
        else
        {
            newClass.setMaxTaxableAmount(null);
        }

        if (this.getTaxCalculator() != null)
        {
            newClass.setTaxCalculator((TaxCalculatorIfc)getTaxCalculator().clone());
        }
        else
        {
            newClass.setTaxCalculator(null);
        }
    }

    /*
     * Compare this tax table line item to another one
     */
    public int compareTo(TaxTableLineItemIfc other)
    {
        CurrencyIfc thisMaxTaxableAmt = this.getMaxTaxableAmount();
        CurrencyIfc otherMaxTaxableAmt = other.getMaxTaxableAmount();

        if ((thisMaxTaxableAmt == null) && (otherMaxTaxableAmt == null))
        {
            return CurrencyIfc.EQUALS;
        }
        else if ((thisMaxTaxableAmt == null) && (otherMaxTaxableAmt != null))
        {
            return CurrencyIfc.GREATER_THAN;
        }
        else if ((thisMaxTaxableAmt != null) && (otherMaxTaxableAmt == null))
        {
            return CurrencyIfc.LESS_THAN;
        }
        else
        {
            return thisMaxTaxableAmt.compareTo(otherMaxTaxableAmt);
        }
    }
}
