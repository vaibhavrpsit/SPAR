/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/damagediscount/AmountEnteredAisle.java /main/18 2013/12/20 18:04:46 ohorne Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    ohorne 12/20/13 - cleared item gift receipt when applying damaged
 *                      discount
 *    rabhaw 12/10/13 - Check if the code list has any entries before using to
 *                      avoid exception when the list is empty
 *    cgreen 04/06/12 - initial implementation of damage discount
 *    blarse 12/21/10 - Changed to use an updated (and consistent) method in
 *                      DiscountUtility.
 *    abonda 06/21/10 - Disable item level editing for an external order line
 *                      item
 *    acadar 06/10/10 - use default locale for currency display
 *    acadar 06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    sgu    01/14/09 - convert user entered currency amount into non locale
 *                      sensitive, decimal format before calling
 *                      CurrencyIfc.setStringValue
 *    acadar 10/31/08 - fixes to reason code localization
 *    acadar 10/30/08 - localization of damage and markdown reason codes
 * ===========================================================================
     $Log:
      6    360Commerce 1.5         3/3/2008 10:05:53 AM   Alan N. Sinton  CR
           29873: Merge from v12x branch.  Code reviewed by Jack Swan.
      5    360Commerce 1.4         4/25/2007 8:52:17 AM   Anda D. Cadar   I18N
           merge

      4    360Commerce 1.3         1/25/2006 4:10:48 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:27:13 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:37 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:28 PM  Robert Pearse
     $:
      4    .v700     1.2.1.0     11/20/2005 15:43:48    Deepanshu       CR
           6127: Check each item for return before applying discount
      3    360Commerce1.2         3/31/2005 15:27:13     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:19:37     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:09:28     Robert Pearse
     $
     Revision 1.5.4.1  2004/11/10 18:58:02  cdb
     @scr 7681 Modified to disallow Gift Receipt line items from damage discount distribution.

     Revision 1.5  2004/03/12 23:12:54  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Extracted common code to abstract class.

     Revision 1.4  2004/03/12 16:24:59  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Added class specific methods and removed eligibleType.

     Revision 1.3  2004/03/11 22:29:08  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Extracted common code to AbstractAmountEnteredAisle.

     Revision 1.2  2004/03/03 23:08:42  dcobb
     @scr 3870 Feature Enhancement: Damage Discounts
     code cleanup.

     Revision 1.1  2004/02/25 22:51:41  dcobb
     @scr 3870 Feature Enhancement: Damage Discounts


 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.pricing.damagediscount;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.DiscountUtility;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.pricing.AbstractAmountEnteredAisle;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This aisle validates the amount entered to make sure it doesn't exceed the
 * maximum damage discount amount/percent parameter and that there are no items
 * that would have a negative price after the discount is applied without a
 * warning or error dialog.
 * <P>
 * Can show one of three dialogs:
 * <ul>
 * <li>MultiItemInvalidDiscount - If more than one item has been selected and at
 * least one of the item's prices would be negative after the discount. Allows
 * you to apply the discount to only the items that don't go negative.
 * <li>InvalidDiscount - If there was only one item selected and it's price
 * would be negative after the discount. Returns to entry screen.
 * <li>InvalidItemDiscount - if at least one item would violate the maximum
 * discount amount parameter. Returns to entry screen.
 * </ul>
 * <P>
 * 
 * @version $Revision: /main/18 $
 */
@SuppressWarnings("serial")
public class AmountEnteredAisle extends AbstractAmountEnteredAisle
{
    /** Revision Number furnished by CVS. */
    public static final String revisionNumber = "$Revision: /main/18 $";
    /** Damage Discount tag */
    public static final String DAMAGE_DISCOUNT_TAG = "DamageDiscount";
    /** Damage discount text */
    public static final String DAMAGE_DISCOUNT_TEXT = "damage discount";

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.pricing.AbstractAmountEnteredAisle#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        PricingCargo cargo = (PricingCargo)bus.getCargo();

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel beanModel = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.DAMAGE_AMOUNT);

        SaleReturnLineItemIfc[] lineItems = cargo.getItems();
        if (lineItems == null)
        {
            lineItems = new SaleReturnLineItemIfc[1];
            lineItems[0] = cargo.getItem();
        }

        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        CodeListIfc reasonCodes = utility.getReasonCodes(cargo.getOperator().getStoreID(),
                CodeConstantsIfc.CODE_LIST_DAMAGE_DISCOUNT_REASON_CODES);

        CurrencyIfc discount = null;
        if (beanModel.getPromptAndResponseModel() != null)
        {
            String enteredAmount = beanModel.getPromptAndResponseModel().getResponseText().trim();
            discount = DomainGateway.getBaseCurrencyInstance(LocaleUtilities.parseCurrency(enteredAmount,
                    LocaleMap.getLocale(LocaleMap.DEFAULT)).toString());
        }
        else if (cargo.getDecimalWithReasonBeanModel() != null)
        {
            discount = DomainGateway.getBaseCurrencyInstance(cargo.getDecimalWithReasonBeanModel().getValue());
        }

        LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
        if (reasonCodes != null && reasonCodes.getNumberOfEntries() != 0)
        {
            String reason = reasonCodes.getDefaultCodeString();
            CodeEntryIfc entry = reasonCodes.findListEntryByCode(reason);
            localizedCode.setCode(reason);
            localizedCode.setText(entry.getLocalizedText());
        }
        else
        {
            localizedCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        }
        // Ensure discount doesn't make any individual prices negative or
        // exceeds MaximumItemDiscountAmountPercent
        String dialog = validateLineItemDiscounts(bus, lineItems, discount, localizedCode);

        // No dialogs required
        if (dialog == null)
        {
            //damaged items cannot be returned so they are not eligible for
            //gift receipt.  Disable gift receipt for any gift receipted line items
            if (this.isDamageDiscount())
            {
                for (SaleReturnLineItemIfc srli : lineItems)
                {
                    if (srli.isGiftReceiptItem())
                    {
                        srli.setGiftReceiptItem(false);
                    }
                }
            }
            
            bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
        }
        // Show the dialog screen
        else
        {
            showDialog(dialog, DAMAGE_DISCOUNT_TAG, DAMAGE_DISCOUNT_TEXT, bus);
        }
    }

    /**
     * Determines if the item is eligible for the discount
     * 
     * @param srli The line item
     * @return true if the item is eligible for the discount
     */
    protected boolean isEligibleForDiscount(SaleReturnLineItemIfc srli)
    {
        return DiscountUtility.isDamageDiscountEligible(srli);
    }

    /**
     * Returns the parameter name
     * 
     * @return The name of the parameter for the maximum percent
     */
    protected String getMaxPercentParameterName()
    {
        return PricingCargo.MAX_DAMAGE_DISC_PCT;
    }

    /**
     * Determines if this is a damage discount.
     * 
     * @return true is the discount is a damage discount
     */
    protected boolean isDamageDiscount()
    {
        return true;
    }

}
