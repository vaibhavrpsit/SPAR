/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/itemdiscount/ModifyItemDiscountPercentSite.java /main/15 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 03/29/12 - initial implementation of item discounts for mobilepos
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/27/10 - updating deprecated names
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    acadar 03/25/09 - force correct refresh by calling showScreen twice
 *    acadar 10/30/08 - cleanup
 *    acadar 10/30/08 - localization of reason codes for item and transaction
 *                      discounts
 * ===========================================================================
     $Log:
      5    360Commerce 1.4         3/30/2007 5:20:45 AM   Michael Boyd    CR
           26172 - v8x merge to trunk

           5    .v8x      1.3.1.0     3/8/2007 4:52:10 PM    Brett J. Larsen
           CR 4530
           - when no default code list value is designated a blank value
           should be displayed
      4    360Commerce 1.3         1/22/2006 11:15:16 PM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         4/1/2005 2:59:03 AM    Robert Pearse
      2    360Commerce 1.1         3/10/2005 9:53:33 PM   Robert Pearse
      1    360Commerce 1.0         2/11/2005 11:42:40 PM  Robert Pearse
     $
     Revision 1.6.4.1  2004/11/11 23:09:53  cdb
     @scr 7693 Updated to make app more robust DB is missing required data.

     Revision 1.7  2004/11/11 22:48:42  cdb
     @scr 7693 Removed crash potential for bad DB data - unable to retrieve reason codes.

     Revision 1.6  2004/03/22 18:35:05  cdb
     @scr 3588 Corrected some javadoc

     Revision 1.5  2004/03/22 03:49:27  cdb
     @scr 3588 Code Review Updates

     Revision 1.4  2004/02/26 18:26:20  cdb
     @scr 3588 Item Discounts no longer have the Damage
     selection. Use the Damage Discount flow to apply Damage
     Discounts.

     Revision 1.3  2004/02/12 16:51:36  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:52:05  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.6   Feb 04 2004 16:41:40   cdb
 * Specifying Manual assignment basis for clarity now that Employee Discounts are also an assignment basis.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.5   Jan 27 2004 14:00:20   cdb
 * Added Damaged flag to UI for damage discounts
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.4   Jan 06 2004 11:02:00   cdb
 * Enhanced configurability. When non-editable combo boxes are used, a default value is set if a previously existing reason code hasn't been selected.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.3   Jan 02 2004 11:56:40   cdb
 * Added logging of valid reason code IDs.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.2   Dec 23 2003 17:45:02   cdb
 * No longer use a "Default" reason code if none is selected or entered.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.1   Oct 17 2003 10:42:20   bwf
 * Set reason code keys and removed unsused imports.
 * Resolution for 3412: Feature Enhancement: Employee Discount
 *
 *    Rev 1.0   Aug 29 2003 16:05:22   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Jul 29 2003 14:47:32   DCobb
 * Use cargo.getItem() when cargo.getItems() is null.
 * Resolution for POS SCR-3280: When a user with no access attempts to apply a discount amount to a kit component, selecting yes on the security error screen results in a loop.
 *
 *    Rev 1.4   Jul 11 2003 17:16:56   sfl
 * Have format control on percentage rate value because IBM BigDecimal could generate a long value.
 * Resolution for POS SCR-3113: Item Discount % precision incorrect during insertion
 *
 *    Rev 1.3   Mar 05 2003 13:55:30   HDyer
 * Fixed deprecation warnings.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.2   Jan 22 2003 15:50:16   mpb
 * SCR #1626.
 * Verify that the reduction is a discount before displaying the percentage amount.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.1   05 Jun 2002 17:13:20   jbp
 * changes for pricing feature
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   02 May 2002 17:41:38   jbp
 * Initial revision.
 * Resolution for POS SCR-1626: Pricing Feature

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.pricing.itemdiscount;

import java.math.BigDecimal;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByPercentageStrategy;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;

/**
 * Displays item-discount-by-percentage dialog. If discount already exists,
 * display is primed with data from existing discount.
 * 
 * @version $Revision: /main/15 $
 */
@SuppressWarnings("serial")
public class ModifyItemDiscountPercentSite extends PosSiteActionAdapter
{
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Displays item-discount-by-percentage dialog. If discount already exists,
     * display is primed with data from existing discount.
     * 
     * @param bus BusIfc
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // get default discount amount
        PricingCargo cargo = (PricingCargo) bus.getCargo();

        // Since ORPOS supports only one discount, we will get the first one from array
        ItemDiscountByPercentageStrategy selectedDiscount = null;
        SaleReturnLineItemIfc item = cargo.getItem();
        if (cargo.getItems() != null)
        {
            item = cargo.getItems()[0];
        }

        ItemDiscountStrategyIfc[] discounts = item.getItemDiscountsByPercentage();
        if (discounts != null)
        {
            int arraySize = discounts.length;
            if ( arraySize > 0)
            {
                for (int i = 0; i < arraySize; i++)
                {
                    ItemDiscountByPercentageStrategy discount = (ItemDiscountByPercentageStrategy)discounts[i];
                    if (discount.getDiscountRate().toString().length() > 5)
                    {
                         BigDecimal scaleOne = new BigDecimal(1);
                         discount.setDiscountRate(discount.getDiscountRate().divide(scaleOne, 2));
                    }
                    if (!discount.isAdvancedPricingRule() &&
                        discount.getTypeCode() == DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_ITEM &&
                        discount.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_DISCOUNT &&
                        discount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL &&
                        !discount.isDamageDiscount())
                    {
                        selectedDiscount = discount;
                        break;
                    }
                }
            }
        }

        // use existing discount if available; otherwise set no default
        // discount rate and use first entry in list
        DecimalWithReasonBeanModel beanModel = cargo.getDecimalWithReasonBeanModel();
        if (beanModel == null)
        {
            beanModel = new DecimalWithReasonBeanModel();
    
            // get reason code list from cargo
            CodeListIfc rcl = cargo.getLocalizedDiscountAmountCodeList();
            if (rcl == null)
            {
                UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
                rcl = utility.getReasonCodes(cargo.getStoreStatus().getStore().getStoreID(), CodeConstantsIfc.CODE_LIST_ITEM_DISCOUNT_BY_PERCENTAGE);
                cargo.setLocalizedDiscountPercentCodeList(rcl);
            }
    
            String selectedReasonCode =  null;
    
            // if discount in place, run with it; otherwise, don't set values
            if (selectedDiscount != null)
            {
                // set rate in bean model
                BigDecimal rate = selectedDiscount.getDiscountRate();
                beanModel.setValue(rate);
                // get reason string to select in list
                selectedReasonCode = selectedDiscount.getReason().getCode();
            }
    
            // set selection
            beanModel.inject(rcl, selectedReasonCode, LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
        }

        // Show the input screen
        ui.showScreen(POSUIManagerIfc.ITEM_DISC_PCNT, beanModel);
    }
}
