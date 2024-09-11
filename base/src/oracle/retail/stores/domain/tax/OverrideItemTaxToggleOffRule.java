/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/OverrideItemTaxToggleOffRule.java /main/11 2012/07/02 10:12:52 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/02/12 - Tax cleanup in preparation for JPA conversion.
 *    sgu       09/29/11 - set taxable line items to the tax calculator
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
 *$
 *Revision 1.4  2004/08/23 16:15:45  cdb
 *@scr 4204 Removed tab characters
 *
 *Revision 1.3  2004/07/22 20:28:27  jdeleau
 *@scr 2775 Fix the way certain tax things were done, to comply
 *with arch guidelines.
 *
 *Revision 1.2  2004/06/07 19:58:59  mkp1
 *@scr 2775 Put correct header on files
 *$
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;

/**
 * @author mkp1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OverrideItemTaxToggleOffRule extends AbstractOverrideItemTaxRule
{
    /** serialVersionUID */
    private static final long serialVersionUID = -1493815943248549848L;

    public OverrideItemTaxToggleOffRule()
    {
        super(TaxConstantsIfc.TAX_MODE_TOGGLE_OFF);
        FixedAmountTaxCalculatorIfc taxCalculator = DomainGateway.getFactory().getFixedAmountTaxCalculatorInstance();
        taxCalculator.setIsUnitTaxAmount(false);
        setTaxCalculator(taxCalculator);
    }

    /**
     * Get the taxable amount on this rule
     *
     *  @param item
     *  @return taxable amount
     */
    public CurrencyIfc getItemTaxableAmount(TaxLineItemInformationIfc item)
    {
        CurrencyIfc retValue = null;
        retValue = (CurrencyIfc) item.getExtendedSellingPrice().clone();
        retValue.setZero();
        return retValue;
    }
}
