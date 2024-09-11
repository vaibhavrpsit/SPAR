/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/OverrideItemTaxByRateRule.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:50 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         1/22/2006 11:41:54 AM  Ron W. Haight   Removed
 *      references to com.ibm.math.BigDecimal
 * 3    360Commerce 1.2         3/31/2005 4:29:16 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:23:56 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:12:56 PM  Robert Pearse
 *$$
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import java.math.BigDecimal;

/**
 * @author mkp1
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OverrideItemTaxByRateRule
    extends AbstractOverrideItemTaxRule
    implements OverrideItemTaxByRateRuleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -3398548715038524785L;


    public OverrideItemTaxByRateRule()
    {
        super(TaxConstantsIfc.TAX_MODE_OVERRIDE_RATE);
        setTaxCalculator(new TaxRateCalculator());
    }
    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.OverrideItemTaxByRateRuleIfc#getTaxRate()
     */
    public BigDecimal getTaxRate()
    {
        return ((TaxRateCalculator) getTaxCalculator()).getTaxRate();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.tax.OverrideItemTaxByRateRuleIfc#setTaxRate(com.ibm.math.BigDecimal)
     */
    public void setTaxRate(BigDecimal value)
    {
        ((TaxRateCalculator) getTaxCalculator()).setTaxRate(value);
    }

}
