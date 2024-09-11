/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/employeediscount/CheckDiscountAlreadyAppliedSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:54 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/03/22 03:49:28  cdb
 *   @scr 3588 Code Review Updates
 *
 *   Revision 1.6  2004/02/20 17:34:57  cdb
 *   @scr 3588 Removed "developmental" log entries from file header.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing.employeediscount;

import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.pos.services.pricing.AbstractCheckDiscountAlreadyAppliedSite;

//--------------------------------------------------------------------------
/**
    This site determines if discounts of this type have already been applied.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckDiscountAlreadyAppliedSite extends AbstractCheckDiscountAlreadyAppliedSite
{
    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
     *   Returns true if item discount array has a non-damage employee item discount.
     *   @param sgyArray The array of ItemDiscountStrategy's to search
     *   @return true if item discount array has a non-damage employee item discount.
     */
    //----------------------------------------------------------------------
    public boolean hasSameTypeDiscount(ItemDiscountStrategyIfc[] sgyArray)
    {
        boolean hasSameType = false;
        
        // retrieve the discount that is not an advanced pricing discount
        if (sgyArray != null && sgyArray.length >0)
        {
            for (int i = 0; i < sgyArray.length; i++)
            {
                ItemDiscountStrategyIfc discount = sgyArray[i];
                // checks that the discount is not APR and
                // that the discount matches the operation.
                if (!discount.isAdvancedPricingRule() &&
                        discount.getTypeCode() == DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_ITEM &&
                        discount.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_DISCOUNT &&
                        discount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE &&
                        !discount.isDamageDiscount())
                {
                    hasSameType = true;
                    break;
                }
            }
        }
        return hasSameType;
    }
}
