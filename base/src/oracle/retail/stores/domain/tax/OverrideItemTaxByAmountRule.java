/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/OverrideItemTaxByAmountRule.java /rgbustores_13.4x_generic_branch/2 2011/10/06 12:38:50 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       09/29/11 - set taxable line items to the tax calculator
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         4/25/2007 10:00:28 AM  Anda D. Cadar   I18N
 *      merge
 * 3    360Commerce 1.2         3/31/2005 4:29:16 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:23:56 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:12:56 PM  Robert Pearse
 *$$
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;

/**
 * @author mkp1
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OverrideItemTaxByAmountRule
    extends AbstractOverrideItemTaxRule
    implements OverrideItemTaxByAmountRuleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 70831945717640010L;


    public OverrideItemTaxByAmountRule()
    {
        super(TaxConstantsIfc.TAX_MODE_OVERRIDE_AMOUNT);
        FixedAmountTaxCalculatorIfc taxCalculator = DomainGateway.getFactory().getFixedAmountTaxCalculatorInstance();
        taxCalculator.setIsUnitTaxAmount(false);
        setTaxCalculator(taxCalculator);
    }

    public CurrencyIfc getFixedTaxAmount()
    {
        return ((FixedAmountTaxCalculator) getTaxCalculator()).getTaxAmount();
    }

    public void setFixedTaxAmount(CurrencyIfc value)
    {
        ((FixedAmountTaxCalculator) getTaxCalculator()).setTaxAmount(value);
    }
}
