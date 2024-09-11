/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/AbstractOverrideTransactionTaxRule.java /main/12 2012/09/06 12:41:33 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       09/06/12 - set transaction tax override reason code to item
 *                         level
 *    jswan     07/02/12 - Tax cleanup in preparation for JPA conversion.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 4    360Commerce 1.3         4/25/2007 10:00:29 AM  Anda D. Cadar   I18N merge
 3    360Commerce 1.2         3/31/2005 4:27:07 PM   Robert Pearse
 2    360Commerce 1.1         3/10/2005 10:19:27 AM  Robert Pearse
 1    360Commerce 1.0         2/11/2005 12:09:20 PM  Robert Pearse
 *
Revision 1.3  2004/07/27 00:07:46  jdeleau
@scr 6251 Make sure when tax is toggled off, that the original tax is charged when its toggled back on
 *
Revision 1.2  2004/07/13 19:36:46  mkp1
@scr 2775 Added a way to turn off tax rule if another tax rule overrides
 *
Revision 1.1  2004/06/17 19:17:23  mkp1
@scr 5414 Transaction tax override doubling tax on kit header
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;


/**
 * Base class of all transaction tax overrides
 */
public abstract class AbstractOverrideTransactionTaxRule extends AbstractTaxRule
implements OverrideTransactionTaxRuleIfc
{

    /** serialVersionUID */
    private static final long serialVersionUID = -4010926702651438990L;

    /**
     * Tax override reason code
     */
    private LocalizedCodeIfc reasonCode;

    public boolean isRuleActiveForItem(TaxLineItemInformationIfc item)
    {
        boolean returnValue = super.isRuleActiveForItem(item);
        if(returnValue && item instanceof SaleReturnLineItemIfc )
        {
            //have to exclude kit header -- Will double tax calculation if not
            //excluded
            returnValue = ((SaleReturnLineItemIfc) item).isKitHeader() == false;
        }
        return returnValue;
    }

    /**
     * Get the taxable amount on this rule, overriden from the base class.
     * Transaction level taxes need to treat the taxable amounts of non-taxable
     * or tax-toggle-off items as 0.
     *
     *  @param item
     *  @return taxable amount
     */
    public CurrencyIfc getItemTaxableAmount(TaxLineItemInformationIfc item)
    {

        CurrencyIfc retValue = super.getItemTaxableAmount(item);

        if(item.getTaxMode() == TaxConstantsIfc.TAX_MODE_TOGGLE_OFF ||
                item.getTaxMode() == TaxConstantsIfc.TAX_MODE_NON_TAXABLE)
        {
            retValue = (CurrencyIfc) retValue.clone();
            retValue.setZero();
        }
        return retValue;
    }

    /**
     * @return override reason code
     */
    public LocalizedCodeIfc getReasonCode()
    {
        return this.reasonCode;
    }

    /**
     * Sets override reason code
     *
     * @param code reason code
     */
    public void setReasonCode(LocalizedCodeIfc code)
    {
        this.reasonCode = code;
    }

    /**
     * Set attributes for clone.
     *
     * @param newClass new instance of AbstractOverrideTransactionTaxRule
     */
    public void setCloneAttributes(AbstractOverrideTransactionTaxRule newClass)
    {
        super.setCloneAttributes(newClass);
        newClass.setReasonCode(this.getReasonCode());
    }

}
