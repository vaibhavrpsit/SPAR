/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/damagediscount/PercentEnteredAisle.java /main/17 2013/12/10 16:54:07 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    rabhaw 12/10/13 - Check if the code list has any entries before using to
 *                      avoid exception when the list is empty
 *    cgreen 04/06/12 - initial implementation of damage discount
 *    blarse 12/21/10 - Changed to use an updated (and consistent) method in
 *                      DiscountUtility.
 *    abonda 06/21/10 - Disable item level editing for an external order line
 *                      item
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/27/10 - updating deprecated names
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    acadar 10/31/08 - fixes to reason code localization
 *    acadar 10/30/08 - localization of damage and markdown reason codes
 * ===========================================================================
     $Log:
      9    360Commerce 1.8         3/13/2008 11:57:21 PM  Vikram Gopinath
           Removed code to ROUND_HALF_UP.
      8    360Commerce 1.7         3/3/2008 10:05:53 AM   Alan N. Sinton  CR
           29873: Merge from v12x branch.  Code reviewed by Jack Swan.
      7    360Commerce 1.6         2/13/2006 3:53:44 PM   Edward B. Thorne
           Merge from PercentEnteredAisle.java, Revision 1.4.1.0
      6    360Commerce 1.5         2/6/2006 5:20:34 PM    Rohit Sachdeva
           10513: Fixing unit tests in trunk
      5    360Commerce 1.4         1/25/2006 4:11:35 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      4    360Commerce 1.3         1/22/2006 11:45:16 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:29:20 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:03 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:13:02 PM  Robert Pearse
     $:
      4    .v700     1.2.1.0     11/20/2005 15:44:03    Deepanshu       CR
           6127: Check each item for return before applying discount
      3    360Commerce1.2         3/31/2005 15:29:20     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:24:03     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:13:02     Robert Pearse
     $
     Revision 1.4.4.1  2004/11/10 18:58:02  cdb
     @scr 7681 Modified to disallow Gift Receipt line items from damage discount distribution.

     Revision 1.4  2004/03/22 18:35:05  cdb
     @scr 3588 Corrected some javadoc

     Revision 1.3  2004/03/12 23:12:54  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Extracted common code to abstract class.

     Revision 1.2  2004/02/26 19:09:14  cdb
     @scr 0 Replaced deprecated import.

     Revision 1.1  2004/02/25 22:51:41  dcobb
     @scr 3870 Feature Enhancement: Damage Discounts

 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.pricing.damagediscount;

import java.math.BigDecimal;

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
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.pricing.AbstractPercentEnteredAisle;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This aisle validates the percent entered to make sure it doesn't exceed the
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
 * @version $Revision: /main/17 $
 */
@SuppressWarnings("serial")
public class PercentEnteredAisle extends AbstractPercentEnteredAisle
{
    /** Revision Number furnished by TeamConnection */
    public static final String revisionNumber = "$Revision: /main/17 $";
    /** tag for dialog <ARG> */
    public static final String DAMAGE_DISCOUNT_TAG = "DamageDiscount";
    /** text for dialog <ARG> */
    public static final String DAMAGE_DISCOUNT_TEXT = "damage discount";

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.pricing.AbstractPercentEnteredAisle#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        PricingCargo cargo = (PricingCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        BigDecimal response = null;

        POSBaseBeanModel beanModel = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.DAMAGE_PERCENT);
        if (beanModel.getPromptAndResponseModel() != null)
        {
            // If input from the UI is emptystring,
            // then clear the discounts by percent for this item.
            // Otherwise, continue on to validating the discount amount.
            response = new BigDecimal(beanModel.getPromptAndResponseModel().getResponseText()).setScale(2);
            response = response.divide(BigDecimal.valueOf(100));
        }
        else if (cargo.getDecimalWithReasonBeanModel() != null)
        {
            response = cargo.getDecimalWithReasonBeanModel().getValue();
        }

        SaleReturnLineItemIfc[] lineItems = cargo.getItems();
        if (lineItems == null)
        {
            lineItems = new SaleReturnLineItemIfc[1];
            lineItems[0] = cargo.getItem();
        }

        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        CodeListIfc reasonCodes = utility.getReasonCodes(cargo.getOperator().getStoreID(),
                CodeConstantsIfc.CODE_LIST_DAMAGE_DISCOUNT_REASON_CODES);

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
        // Chop off the potential long values caused by BigDecimal.
        if (response.toString().length() > 5)
        {
            BigDecimal scaleOne = new BigDecimal(1);
            response = response.divide(scaleOne, 2);
        }

        // Ensure discount doesn't make any individual prices negative or
        // exceeds MaximumItemDiscountAmountPercent
        String dialog = validateLineItemDiscounts(bus, lineItems, response, localizedCode);

        // No dialogs required
        if (dialog == null)
        {
            bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
        }
        // show dialog screen
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
     * Clears the discounts by percentage from the line item
     * 
     * @param srli The line item
     */
    protected void clearDiscountsByPercentage(SaleReturnLineItemIfc srli)
    {
        srli.clearItemDiscountsByPercentage(DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL, true);
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
