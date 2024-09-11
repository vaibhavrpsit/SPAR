/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/markdown/AmountEnteredAisle.java /main/12 2011/01/07 10:52:01 blarsen Exp $
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
 *    abonda 01/03/10 - update header date
 *    acadar 10/30/08 - localization of damage and markdown reason codes
 * ===========================================================================
     $Log:
      4    360Commerce 1.3         4/25/2007 8:52:17 AM   Anda D. Cadar   I18N
           merge

      3    360Commerce 1.2         3/31/2005 4:27:13 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:38 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:28 PM  Robert Pearse
     $
     Revision 1.11  2004/06/08 17:04:10  dfierling
     @scr 4411 - rollback changes

     Revision 1.10  2004/05/28 19:02:21  dfierling
     @scr 4411 - Fix for misleading error Dialog msg

     Revision 1.9  2004/03/19 23:27:50  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Code review cleanup.

     Revision 1.8  2004/03/12 23:12:53  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Extracted common code to abstract class.

     Revision 1.7  2004/03/12 16:24:59  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Added class specific methods and removed eligibleType.

     Revision 1.6  2004/03/11 22:29:08  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Extracted common code to AbstractAmountEnteredAisle.

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
 *    Rev 1.0   Aug 29 2003 16:05:22   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Jul 17 2003 07:00:36   jgs
 * Modifed journaling for item discounts.
 * Resolution for 3037: The ejournal for a transaction with multiple (3) % discounts applies and removes the first two discounts on the ejournal.
 *
 *    Rev 1.2   Jan 29 2003 10:40:42   mpb
 * SCR #1626
 * Added arguments to the error dialogs in order to make them I18n compliant.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.1   Jan 22 2003 15:52:48   mpb
 * SCR #1626.
 * Changed the way that markdowns are cleared from the transaction.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   05 Jun 2002 17:17:40   jbp
 * Initial revision.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   02 May 2002 17:41:34   jbp
 * Initial revision.
 * Resolution for POS SCR-1626: Pricing Feature

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.pricing.markdown;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.DiscountUtility;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.pricing.AbstractAmountEnteredAisle;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;

//--------------------------------------------------------------------------
/**
 *   This aisle validates the amount entered to make sure it doesn't
 *   exceed the maximum markdown amount/percent parameter and
 *   that there are no items that would have a negative price after the
 *   markdown is applied without a warning or error dialog.
 *   <P>
 *   Can show one of three dialogs:
 *   MultiItemInvalidDiscount - If more than one item has been selected and
 *      at least one of the item's prices would be negative after the
 *      discount. Allows you to apply the discount to only the items
 *      that don't go negative.
 *   InvalidDiscount - If there was only one item selected and it's
 *      price would be negative after the discount. Returns to entry screen.
 *   InvalidItemDiscount - if at least one item would violate the maximum
 *      discount amount parameter. Returns to entry screen.
 *   <P>
 *   @version $Revision: /main/12 $
 */
//--------------------------------------------------------------------------
public class AmountEnteredAisle extends AbstractAmountEnteredAisle
{
    /** Revision Number */
    public static final String revisionNumber = "$Revision: /main/12 $";
    /** tag for dialog <ARG> */
    public static final String MARKDOWN_TAG = "Markdown";
    /** text for dialog <ARG> */
    public static final String MARKDOWN_TEXT = "markdown";

    //----------------------------------------------------------------------
    /**
     *   This aisle validates the amount entered to make sure it doesn't
     *   exceed the maximum markdown amount/percent parameter and
     *   that there are no items that would have a negative price after the
     *   markdown is applied without a warning or error dialog.
     *   <P>
     *   Can show one of three dialogs:
     *   MultiItemInvalidDiscount - If more than one item has been selected and
     *      at least one of the item's prices would be negative after the
     *      discount. Allows you to apply the discount to only the items
     *      that don't go negative.
     *   InvalidDiscount - If there was only one item selected and it's
     *      price would be negative after the discount. Returns to entry screen.
     *   InvalidItemDiscount - if at least one item would violate the maximum
     *      discount amount parameter. Returns to entry screen.
     *   <P>
     *   @param  bus     Service Bus
     */
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        PricingCargo cargo = (PricingCargo) bus.getCargo();
        String dialog = null;
        String letter = CommonLetterIfc.CONTINUE;

        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        DecimalWithReasonBeanModel beanModel =
            (DecimalWithReasonBeanModel) ui.getModel(POSUIManagerIfc.MARKDOWN_AMOUNT);
        String discountAmount = beanModel.getValue().toString();

        // If input from the UI is emptystring,
        // then clear the discounts by amount for this item.
        // Otherwise, continue on to validating the discount amount.
        SaleReturnLineItemIfc[] lineItems = cargo.getItems();
        if (lineItems == null)
        {
            lineItems = new SaleReturnLineItemIfc[1];
            lineItems[0] = cargo.getItem();
        }

        // Create the discount strategy
        CurrencyIfc discount = DomainGateway.getBaseCurrencyInstance(discountAmount);

        LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
        CodeListIfc rcl = cargo.getLocalizedMarkdownAmountCodeList();
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
        // Ensure discount doesn't make any individual prices negative or exceeds MaximumItemDiscountAmountPercent
        dialog = validateLineItemDiscounts(bus, lineItems, discount, localizedCode);

        // No dialogs required
        if (dialog == null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
        // Show dialog screen
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
     * Returns the parameter name
     * @return The name of the parameter for the maximum markdown percent.
     */
    //----------------------------------------------------------------------
    protected String getMaxPercentParameterName()
    {
        return PricingCargo.MAX_MARKDOWN_PCT;
    }

    //----------------------------------------------------------------------
    /**
     * Determines if this is a markdown.
     * @return true
     */
    //----------------------------------------------------------------------
    protected boolean isMarkdown()
    {
        return true;
    }

}
