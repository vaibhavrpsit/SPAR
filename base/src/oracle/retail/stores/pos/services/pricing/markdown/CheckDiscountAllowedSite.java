/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/markdown/CheckDiscountAllowedSite.java /main/14 2011/12/05 12:16:21 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    blarsen   12/21/10 - Changed to use an updated (and consistent) method in
 *                         DiscountUtility.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    mkutiana  02/08/10 - Using DiscountUtility to check if the item is
 *                         DiscountEligible
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         11/15/2007 11:12:52 AM Christian Greene
 *         CR28668 - backed out changes for CR25850. When determing ifdiscount
 *          allowed at CheckDiscountAllowedSite, reset lineitems in cargo to
 *         only those that are allowable.
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:54 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/19 23:27:50  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Code review cleanup.
 *
 *   Revision 1.2  2004/03/03 23:10:45  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *
 *   Revision 1.1  2004/03/02 16:06:36  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing.markdown;

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
    displays the item not discountable dialog or the discount items error 
    dialog.
    @version $Revision: /main/14 $
**/
//--------------------------------------------------------------------------
public class CheckDiscountAllowedSite 
extends oracle.retail.stores.pos.services.pricing.CheckDiscountAllowedSite
{
    private static final long serialVersionUID = -2304667776666957886L;

    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /main/14 $";
    /** tag for dialog <ARG> */
    public static final String MARKDOWN_TAG = "Markdown";
    /** text for dialog <ARG> */
    public static final String MARKDOWN_TEXT = "markdown";
        
    //----------------------------------------------------------------------
    /**
     *   Returns true if the discount is allowed on this line item.
     *   <P>
     *   Any return items with receipt are not allowed for returns.
     *   
     *   @param  srli       The SaleReturnLineItemIfc to check
     *   @return true if the discount is allowed on this line item
     */
    //----------------------------------------------------------------------
    public boolean isDiscountAllowed(SaleReturnLineItemIfc srli)
    {
        return DiscountUtility.isDiscountEligible(srli);
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
        
        return utility.retrieveCommonText(MARKDOWN_TAG, MARKDOWN_TEXT, uiLocale);
    }

    //----------------------------------------------------------------------
    /**
     *   Returns the Item Not Damage Discountable resource ID. <P>
     *   @return The ARG of the MultipleInvalidDiscountDialog
     */
    //----------------------------------------------------------------------
    public String getInvalidDiscountDialogResourceID()
    {
        return PricingCargo.DISCOUNT_NOT_ALLOWED;
    }

}
