/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/markdown/ModifyMarkdownAmountSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/27/10 - updating deprecated names
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    acadar 10/31/08 - fixes
 *    acadar 10/30/08 - localization of damage and markdown reason codes
  * ===========================================================================
     $Log:
      7    360Commerce 1.6         4/25/2007 8:52:16 AM   Anda D. Cadar   I18N
           merge

      6    360Commerce 1.5         3/29/2007 6:51:43 PM   Michael Boyd    CR
           26172 - v8x merge to trunk

           6    .v8x      1.4.1.0     3/8/2007 5:07:30 PM    Brett J. Larsen
           CR 4530
           - when no default value is specified for a code list a blank value
           should be used (not the 1st value in the list)
      5    360Commerce 1.4         1/25/2006 4:11:31 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      4    360Commerce 1.3         1/22/2006 11:45:16 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:34 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse
     $:
      4    .v700     1.2.1.0     11/17/2005 16:39:23    Jason L. DeLeau 4345:
           Replace any uses of Gateway.log() with the log4j.
      3    360Commerce1.2         3/31/2005 15:29:04     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:23:34     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:40     Robert Pearse
     $
     Revision 1.11  2004/07/20 22:42:46  dcobb
     @scr 4377 Invalid Reason Code clears markdown fields
     Save the bean model in the cargo and clear the selected reason code.

     Revision 1.10  2004/07/20 20:07:42  dcobb
     @scr 6369 Markdown amount field default incorrectly with previous transaction amount
     Backing out tmorris 'fix' of 4377.

     Revision 1.9  2004/05/13 17:08:05  tmorris
     @scr 4377 -Markdown amount no longer clears when reason code is invalid.

     Revision 1.8  2004/03/19 23:27:50  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Code review cleanup.

     Revision 1.7  2004/03/17 23:28:32  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Code review modifications.

     Revision 1.6  2004/03/17 23:03:10  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Code review modifications.

     Revision 1.5  2004/03/03 23:10:45  dcobb
     @scr 3911 Feature Enhancement: Markdown

     Revision 1.4  2004/02/13 22:24:29  cdb
     @scr 3588 Added dialog to indicate when discount will reduce
     some prices below zero but not others.

     Revision 1.3  2004/02/12 16:51:37  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:52:05  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.2   Jan 06 2004 11:02:06   cdb
 * Enhanced configurability. When non-editable combo boxes are used, a default value is set if a previously existing reason code hasn't been selected.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.1   Jan 05 2004 14:45:54   cdb
 * Modified to use editable combo box.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.0   Aug 29 2003 16:05:24   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Jul 10 2003 17:07:00   bwf
 * Get correct markdown reason codes and set markdownFlg to true during validation.
 * Resolution for 2678: Markdown Reason Codes not displaying correct information in Pricing
 *
 *    Rev 1.4   Mar 05 2003 14:14:02   HDyer
 * Fixed deprecation warnings.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.3   Jan 29 2003 10:40:42   mpb
 * SCR #1626
 * Added arguments to the error dialogs in order to make them I18n compliant.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.2   Jan 27 2003 14:51:08   mpb
 * SCR #1626
 * Get the screen name from a resource bundle for internationalization.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.1   Jan 22 2003 15:54:44   mpb
 * SCR #1626.
 * Verify that the reduction is a markdown before displaying it.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   05 Jun 2002 17:17:40   jbp
 * Initial revision.
 * Resolution for POS SCR-1626: Pricing Feature

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.pricing.markdown;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountStrategy;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;

//------------------------------------------------------------------------------
/**
    Displays markdown by amount screen, with information from existing
    markdown by amount, if present.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class ModifyMarkdownAmountSite extends PosSiteActionAdapter
{

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
     *   Displays markdown by amount dialog, with information from existing
     *   markdown by amount, if present.
     *   @param bus
     */
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // get the UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.
            getManager(UIManagerIfc.TYPE);

        // get the cargo for the service
        PricingCargo cargo = (PricingCargo)bus.getCargo();

        // get default discount amount
        ItemDiscountByAmountStrategy sgy = null;

        if(cargo.getItems().length == 1)
        {
            SaleReturnLineItemIfc item = cargo.getItems()[0];
            // retrieve the discount that is not an advanced pricing discount)
            ItemDiscountStrategyIfc[] sgyArray = item.getItemDiscountsByAmount();
            if (sgyArray != null)
            {
                int arraySize = sgyArray.length;
                if (arraySize > 0)
                {
                    for (int i = 0; i < arraySize; i++)
                    {
                        ItemDiscountByAmountStrategy discount = (ItemDiscountByAmountStrategy) sgyArray[i];
                        // checks that the discount is not an advanced Pricing Rule and
                        // that the discount matches the operation.
                        if (!discount.isAdvancedPricingRule() &&
                            discount.getTypeCode() == DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_ITEM &&
                            discount.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_MARKDOWN)
                        {
                            sgy = discount;
                        }
                    }
                }
            }
        }

        DecimalWithReasonBeanModel beanModel = cargo.getDecimalWithReasonBeanModel();
        if (beanModel == null)
        {
            // use existing discount if available; otherwise set no default
            // discount rate and use first entry in list
            String selectedReasonCode = null;
            // get reason code list from cargo
            // need markdown reason codes

            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            CodeListIfc rcl = utility.getReasonCodes(cargo.getOperator().getStoreID(),CodeConstantsIfc.CODE_LIST_MARKDOWN_AMOUNT_REASON_CODES);
            cargo.setLocalizedMarkdownAmountCodeList(rcl);

            // set new bean model
            beanModel = new DecimalWithReasonBeanModel();

            logger.info("Valid Code IDs for Markdown by Amt. are: " + rcl.getKeyEntries());

            // if discount in place, run with it; otherwise, don't set values
            if (sgy != null)
            {
                // set rate in bean model
                CurrencyIfc discount = sgy.getDiscountAmount();
                beanModel.setValue(new BigDecimal(discount.getStringValue()));
                // get reason string to select in list
                selectedReasonCode = sgy.getReason().getCode();

            }
            // set selection
            beanModel.inject(rcl, selectedReasonCode, LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
        }

        // show the discount amount entry screen
        ui.showScreen(POSUIManagerIfc.MARKDOWN_AMOUNT, beanModel);
    }

}
