/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/markdown/PercentEnteredAisle.java /main/13 2011/01/07 10:52:02 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    blarse 01/07/11 - XbranchMerge
 *                      blarsen_bug10624300-discount-flag-change-side-effects
 *                      from rgbustores_13.3x_generic_branch
 *    blarse 01/06/11 - Changed discount eligibility check to use
 *                      DiscountUtility helper method. SRLI was changed to
 *                      return the simple item eligibility flag.
 *                      DiscountUtility includes additional checks (e.g.
 *                      external pricing).
 *    abonda 06/21/10 - Disable item level editing for an external order line
 *                      item
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/27/10 - updating deprecated names
 *    abonda 01/03/10 - update header date
 *    acadar 10/30/08 - localization of damage and markdown reason codes
 * ===========================================================================
     $Log:
      5    360Commerce 1.4         3/13/2008 11:59:26 PM  Vikram Gopinath
           Removed code to ROUND_HALF_UP.
      4    360Commerce 1.3         1/22/2006 11:45:17 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:29:20 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:03 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:13:02 PM  Robert Pearse
     $
     Revision 1.9  2004/03/22 03:49:28  cdb
     @scr 3588 Code Review Updates

     Revision 1.8  2004/03/19 23:27:50  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Code review cleanup.

     Revision 1.7  2004/03/12 23:12:53  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Extracted common code to abstract class.

     Revision 1.6  2004/03/04 19:52:34  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Clear markdown items.

     Revision 1.5  2004/03/03 23:10:45  dcobb
     @scr 3911 Feature Enhancement: Markdown

     Revision 1.4  2004/02/24 16:21:30  cdb
     @scr 0 Remove Deprecation warnings. Cleaned code.

     Revision 1.3  2004/02/12 16:51:37  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:52:05  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:05:24   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Jul 17 2003 07:00:38   jgs
 * Modifed journaling for item discounts.
 * Resolution for 3037: The ejournal for a transaction with multiple (3) % discounts applies and removes the first two discounts on the ejournal.
 *
 *    Rev 1.2   Jan 29 2003 10:40:44   mpb
 * SCR #1626
 * Added arguments to the error dialogs in order to make them I18n compliant.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.1   Jan 22 2003 15:54:04   mpb
 * SCR #1626.
 * Changed the way that markdowns are cleared from the transaction.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   05 Jun 2002 17:17:38   jbp
 * Initial revision.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   02 May 2002 17:41:38   jbp
 * Initial revision.
 * Resolution for POS SCR-1626: Pricing Feature

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.pricing.markdown;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.DiscountUtility;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.pricing.AbstractPercentEnteredAisle;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;
import java.math.BigDecimal;

//----------------------------------------------------------------------
/**
 *   This aisle validates the percent entered to make sure it doesn't
 *   exceed the maximum markdown amount/percent parameter and
 *   that there are no items that would have a negative price after the
 *   markdown is applied without a warning or error dialog.
 *   <P>
 *   Can show one of three dialogs:
 *   MultiItemInvalidDiscount - If more than one item has been selected and
 *      at least one of the item's prices would be negative after the
 *      markdown. Allows you to apply the markdown to only the items
 *      that don't go negative.
 *   InvalidDiscount - If there was only one item selected and it's
 *      price would be negative after the markdown. Returns to entry screen.
 *   InvalidItemDiscount - if at least one item would violate the maximum
 *      markdown amount parameter. Returns to entry screen.
    @version $Revision: /main/13 $
*/
//--------------------------------------------------------------------------
public class PercentEnteredAisle extends AbstractPercentEnteredAisle
{
    /** Revision Number */
    public static final String revisionNumber = "$Revision: /main/13 $";
    /** tag for dialog <ARG> */
    public static final String MARKDOWN_TAG = "Markdown";
    /** text for dialog <ARG> */
    public static final String MARKDOWN_TEXT = "markdown";


    //----------------------------------------------------------------------
    /**
     *   This aisle validates the percent entered to make sure it doesn't
     *   exceed the maximum markdown amount/percent parameter and
     *   that there are no items that would have a negative price after the
     *   markdown is applied without a warning or error dialog.
     *   <P>
     *   Can show one of three dialogs:
     *   MultiItemInvalidDiscount - If more than one item has been selected and
     *      at least one of the item's prices would be negative after the
     *      markdown. Allows you to apply the markdown to only the items
     *      that don't go negative.
     *   InvalidDiscount - If there was only one item selected and it's
     *      price would be negative after the markdown. Returns to entry screen.
     *   InvalidItemDiscount - if at least one item would violate the maximum
     *      markdown amount/oercent parameter. Returns to entry screen.
     *   <P>
     *   Mails the Contiunue letter if no dialog is shown.
     *   <P>
     *   @param  bus     Service Bus
     */
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        PricingCargo cargo = (PricingCargo) bus.getCargo();
        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        String dialog = null;
        String letter = CommonLetterIfc.CONTINUE;

        DecimalWithReasonBeanModel beanModel =
            (DecimalWithReasonBeanModel) ui.getModel(POSUIManagerIfc.MARKDOWN_PERCENT);
        BigDecimal  response    = beanModel.getValue();


        SaleReturnLineItemIfc[] lineItems = cargo.getItems();

        if (lineItems == null)
        {
            lineItems = new SaleReturnLineItemIfc[1];
            lineItems[0] = cargo.getItem();
        }

        // Chop off the potential long values caused by BigDecimal.
        if (response.toString().length() > 5)
        {
            BigDecimal scaleOne = new BigDecimal(1);
            response = response.divide(scaleOne, 2);
        }

        LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
        CodeListIfc rcl = cargo.getLocalizedMarkdownPercentCodeList();
        String  reason = beanModel.getSelectedReasonKey();

        if (rcl != null)
        {
            CodeEntryIfc entry = rcl.findListEntryByCode(reason);
            localizedCode.setCode(reason);
            localizedCode.setText(entry.getLocalizedText());

        }
        else
        {
            localizedCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        }

        // Ensure markdown doesn't make any individual prices negative or exceeds MaximumMarkdownAmountPercent
        dialog = validateLineItemDiscounts(bus, lineItems, response, localizedCode);

        // No dialogs required
        if (dialog == null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
        // show dialog screen
        else
        {
            showDialog(dialog, MARKDOWN_TAG, MARKDOWN_TEXT, bus);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Determines if the item is eligible for the discount
     * @param srli  The line item
     * @return true if the item is eligible for the discount
     */
    //----------------------------------------------------------------------
    protected boolean isEligibleForDiscount(SaleReturnLineItemIfc srli)
    {
        return DiscountUtility.isDiscountEligible(srli);
    }

    //----------------------------------------------------------------------
    /**
     * Returns the parameter name for the maximum markdown percent
     * @return The name of the parameter for the maximum percent
     */
    //----------------------------------------------------------------------
    protected String getMaxPercentParameterName()
    {
        return PricingCargo.MAX_MARKDOWN_PCT;
    }

    //----------------------------------------------------------------------
    /**
     * Clears the discounts by percentage from the line item
     * @param srli  The line item
     */
    //----------------------------------------------------------------------
    protected void clearDiscountsByPercentage(SaleReturnLineItemIfc srli)
    {
        srli.clearItemMarkdownsByPercentage(DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_ITEM);
    }

    //----------------------------------------------------------------------
    /**
     * Determines if this is a markdown.
     * @return true is the discount is a markdown
     */
    //----------------------------------------------------------------------
    protected boolean isMarkdown()
    {
        return true;
    }

    //----------------------------------------------------------------------
    /**
     * Returns the reason code list for markdown percentage discount.
     * @param cargo
     * @return the reason code list
     * @deprecated as of 13.1. Use {@link getLocalizedPercentCodeList(PricingCargo)}
     */
    //----------------------------------------------------------------------
    /*protected CodeListIfc getPercentCodeList(PricingCargo cargo)
    {
        return cargo.getMarkdownPercentCodeList();
    }*/

    /**
     * Gets the localized reason codes
     */
    protected CodeListIfc getLocalizedPercentCodeList(PricingCargo cargo)
    {
        return cargo.getLocalizedMarkdownPercentCodeList();
    }

    //----------------------------------------------------------------------
    /**
     * Determines the accounting method.
     * @return the markdown accounting method
     */
    //----------------------------------------------------------------------
    protected int getAccountingMethod()
    {
        return DiscountRuleConstantsIfc.ACCOUNTING_METHOD_MARKDOWN;
    }


}
