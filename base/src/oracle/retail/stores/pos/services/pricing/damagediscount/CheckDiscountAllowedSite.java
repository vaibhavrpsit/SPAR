/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/damagediscount/CheckDiscountAllowedSite.java /main/13 2011/12/05 12:16:21 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    blarsen   12/21/10 - Changed to use an updated (and consistent) method in
 *                         DiscountUtility.
 *    abondala  06/21/10 - Disable item level editing for an external order
 *                         line item
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:10:51 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:54 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/20/2005 15:43:58    Deepanshu       CR
 *         6127: Check each item for return before applying discount
 *    3    360Commerce1.2         3/31/2005 15:27:24     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:20:06     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:09:54     Robert Pearse
 *
 *   Revision 1.5  2004/03/22 18:35:05  cdb
 *   @scr 3588 Corrected some javadoc
 *
 *   Revision 1.4  2004/03/17 23:03:11  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Code review modifications.
 *
 *   Revision 1.3  2004/03/02 22:40:05  dcobb
 *   @scr 3870 Feature Enhancement: Damage Discounts
 *   Item flagged with gift receipt is not eligible for damage discount.
 *
 *   Revision 1.2  2004/03/02 16:04:21  dcobb
 *   @scr 3870 Feature Enhancement: Damage Discounts
 *   Code cleanup.
 *
 *   Revision 1.1  2004/02/25 22:51:41  dcobb
 *   @scr 3870 Feature Enhancement: Damage Discounts
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing.damagediscount;

import java.util.Locale;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.DiscountUtility;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.pricing.PricingCargo;

//--------------------------------------------------------------------------
/**
    This site checks the selected item(s) for the discount allowed flag and
    displays the invalid discount dialog or the discount items error dialog.
    <P>
    @version $Revision: /main/13 $
    @see oracle.retail.stores.pos.services.pricing.CheckDiscountAllowedSite
**/
//--------------------------------------------------------------------------
public class CheckDiscountAllowedSite
extends oracle.retail.stores.pos.services.pricing.CheckDiscountAllowedSite
{
    private static final long serialVersionUID = -562265046913479972L;

    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /main/13 $";
    /** tag for dialog <ARG> */
    public static final String DAMAGE_DISCOUNT_TAG = "DamageDiscount";
    /** text for dialog <ARG> */
    public static final String DAMAGE_DISCOUNT_TEXT = "damage discount";

    //----------------------------------------------------------------------
    /**
     *   Returns true if the discount is allowed on this line item. <P>
     *   @param  srli       The SaleReturnLineItemIfc to check
     *   @return true if the discount is allowed on this line item
     */
    //----------------------------------------------------------------------
    public boolean isDiscountAllowed(SaleReturnLineItemIfc srli)
    {
        return DiscountUtility.isDamageDiscountEligible(srli);
    }

    //----------------------------------------------------------------------
    /**
     *   Returns ARG of the MultipleInvalidDiscountDialog. <P>
     *   @param  utility       The UtilityManagerIfc with the bundle resources
     *   @return The ARG of the MultipleInvalidDiscountDialog
     */
    //----------------------------------------------------------------------
    public String getMultipleInvalidDiscountDialogArg(UtilityManagerIfc utility)
    {
        // Get the locale for the user interface.
        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        return utility.retrieveCommonText(DAMAGE_DISCOUNT_TAG, DAMAGE_DISCOUNT_TEXT, uiLocale);
    }

    //----------------------------------------------------------------------
    /**
     *   Returns the Item Not Damage Discountable resource ID. <P>
     *   @return The ARG of the MultipleInvalidDiscountDialog
     */
    //----------------------------------------------------------------------
    public String getInvalidDiscountDialogResourceID()
    {
        return PricingCargo.ITEM_NOT_DAMAGE_DISCOUNTABLE;
    }

}
