/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/TaxRateCalculator.java /rgbustores_13.4x_generic_branch/3 2011/10/06 12:38:51 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       09/30/11 - change tax caculator api
 *    sgu       08/02/11 - keep whole scale for multiplication during tax
 *                         calculation
 *    sgu       08/01/11 - donot round tax amount during tax calculation
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 9    360Commerce 1.8         6/14/2007 10:07:30 AM  Sandy Gu        added
 *      comments and logger
 * 8    360Commerce 1.7         4/30/2007 5:38:35 PM   Sandy Gu        added api
 *       to handle inclusive tax
 * 7    360Commerce 1.6         4/25/2007 2:09:53 PM   Ashok.Mondal    CR 3864
 *      :V7.2.2 merge to trunk.
 * 6    360Commerce 1.5         4/25/2007 10:00:24 AM  Anda D. Cadar   I18N
 *      merge
 * 5    360Commerce 1.4         10/9/2006 9:13:16 AM   Keith L. Lesikar Merge
 *      fix from v7x. Using clone to preserve initial amount.
 * 4    360Commerce 1.3         1/22/2006 11:41:55 AM  Ron W. Haight   Removed
 *      references to com.ibm.math.BigDecimal
 * 3    360Commerce 1.2         3/31/2005 4:30:20 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:25:48 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:14:43 PM  Robert Pearse
 *$ 5    360Commerce1.4         10/9/2006 9:13:16 AM   Keith L. Lesikar Merge fix
 *      from v7x. Using clone to preserve initial amount.
 * 4    360Commerce1.3         1/22/2006 11:41:55 AM  Ron W. Haight   Removed
 *      references to com.ibm.math.BigDecimal
 * 3    360Commerce1.2         3/31/2005 4:30:20 PM   Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:25:48 AM  Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:14:43 PM  Robert Pearse
 *$ 4    360Commerce1.3         1/22/2006 11:41:55 AM  Ron W. Haight   Removed
 *      references to com.ibm.math.BigDecimal
 * 3    360Commerce1.2         3/31/2005 3:30:20 PM   Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:25:48 AM  Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:14:43 PM  Robert Pearse
 *$$
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;

import org.apache.log4j.Logger;

/**
 * @version 1.0
 * @created 13-Apr-2004 6:41:33 PM
 */
public class TaxRateCalculator extends AbstractTaxRateCalculator
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4405246926351223838L;

    protected static Logger logger = Logger.getLogger(oracle.retail.stores.domain.tax.TaxRateCalculator.class);

    /**
     * Default constructor
     */
    public TaxRateCalculator()
    {
    }

    /**
     * Clone the tax rate calculator
     */
    public Object clone()
    {
        TaxRateCalculator taxRateCalculator = new TaxRateCalculator();

        setCloneAttributes(taxRateCalculator);

        return taxRateCalculator;
    }

    /**
     * Calculate the tax amount based on the taxable amount passed in
     * Get the rounding mode from the database and do further calculations.
     * Clone used here, not to affect the original amount object.
     * @param amount the taxable amount
     * @return the tax amount
     */
    public CurrencyIfc calculateTaxAmount(CurrencyIfc taxableAmount, TaxLineItemInformationIfc[] lineItems)
    {
        int scale = taxableAmount.getScale() + getTaxRate().scale();
    	CurrencyIfc retValue = taxableAmount.multiply(getTaxRate(), scale, getRoundingMode());
        return retValue;
    }
}
