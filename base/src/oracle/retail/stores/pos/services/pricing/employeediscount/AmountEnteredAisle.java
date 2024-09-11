/* ===========================================================================
* Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/employeediscount/AmountEnteredAisle.java /main/19 2013/12/10 16:54:07 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  12/10/13 - Check if the code list has any entries before using
 *                         to avoid exception when the list is empty
 *    blarsen   12/21/10 - Changed to use an updated (and consistent) method in
 *                         DiscountUtility.
 *    abondala  06/21/10 - Disable item level editing for an external order
 *                         line item
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    cgreene   03/20/09 - implement printing of Employee by making it a
 *                         separate lookup reason code like damage discount
 *    sgu       01/14/09 - convert user entered currency amount into non locale
 *                         sensitive, decimal format before calling
 *                         CurrencyIfc.setStringValue
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/25/2007 8:52:17 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    4    360Commerce 1.3         1/25/2006 4:10:48 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:27:13 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:37 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:28 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/20/2005 15:44:09    Deepanshu       CR
 *         6127: Check each item for return before applying discount
 *    3    360Commerce1.2         3/31/2005 15:27:13     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:19:37     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:09:28     Robert Pearse
 *
 *   Revision 1.23  2004/03/22 03:49:28  cdb
 *   @scr 3588 Code Review Updates
 *
 *   Revision 1.22  2004/03/19 23:27:50  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Code review cleanup.
 *
 *   Revision 1.21  2004/03/17 23:03:11  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Code review modifications.
 *
 *   Revision 1.20  2004/03/12 23:12:53  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Extracted common code to abstract class.
 *
 *   Revision 1.19  2004/03/12 16:24:59  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Added class specific methods and removed eligibleType.
 *
 *   Revision 1.18  2004/03/11 22:29:08  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Extracted common code to AbstractAmountEnteredAisle.
 *
 *   Revision 1.17  2004/02/24 22:36:30  cdb
 *   @scr 3588 Added ability to check for previously existing
 *   discounts of the same type and capture the prorate user
 *   selection. Also migrated item discounts to validate in
 *   the percent and amount entered aisle to be consistent
 *   with employee discounts.
 *
 *   Revision 1.16  2004/02/24 16:21:31  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.15  2004/02/21 18:30:39  cdb
 *   @scr 3588 Updated prorating algorithm to actually prorate discounts.
 *
 *   Revision 1.14  2004/02/20 19:39:45  cdb
 *   @scr 3588 Updated complex pro-rating behavior to take
 *   maximum discount percent into account.
 *
 *   Revision 1.13  2004/02/20 17:34:57  cdb
 *   @scr 3588 Removed "developmental" log entries from file header.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing.employeediscount;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.DiscountUtility;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.pricing.AbstractAmountEnteredAisle;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This aisle validates the amount entered to make sure it doesn't exceed the
 * maximum employee discount amount/percent parameter and that there are no
 * items that would have a negative price after the discount is applied without
 * a warning or error dialog.
 * <P>
 * Can show one of three dialogs: MultiItemInvalidDiscount - If more than one
 * item has been selected and at least one of the item's prices would be
 * negative after the discount. Allows you to apply the discount to only the
 * items that don't go negative. InvalidDiscount - If there was only one item
 * selected and it's price would be negative after the discount. Returns to
 * entry screen. InvalidItemDiscount - if at least one item would violate the
 * maximum discount amount parameter. Returns to entry screen.
 *
 * @version $Revision: /main/19 $
 */
public class AmountEnteredAisle extends AbstractAmountEnteredAisle
{
    private static final long serialVersionUID = -2219151654158239061L;

    /** Revision Number furnished by CVS. */
    public static final String revisionNumber = "$Revision: /main/19 $";

    /** Employee Discount tag */
    public static final String EMPLOYEE_DISCOUNT_TAG = "EmployeeDiscount";

    /** Employee discount text */
    public static final String EMPLOYEE_DISCOUNT_TEXT = "employee discount";

    /**
     * This aisle validates the amount entered to make sure it doesn't exceed
     * the maximum employee discount amount/percent parameter and that there are
     * no items that would have a negative price after the discount is applied
     * without a warning or error dialog.
     * <P>
     * Can show one of three dialogs: MultiItemInvalidDiscount - If more than
     * one item has been selected and at least one of the item's prices would be
     * negative after the discount. Allows you to apply the discount to only the
     * items that don't go negative. InvalidDiscount - If there was only one
     * item selected and it's price would be negative after the discount.
     * Returns to entry screen. InvalidItemDiscount - if at least one item would
     * violate the maximum discount amount parameter. Returns to entry screen.
     *
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        PricingCargo cargo = (PricingCargo) bus.getCargo();
        String dialog = null;
        String letter = CommonLetterIfc.CONTINUE;

        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel beanModel =
            (POSBaseBeanModel) ui.getModel(POSUIManagerIfc.ENTER_EMPLOYEE_AMOUNT_DISCOUNT);

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
        String enteredAmount = beanModel.getPromptAndResponseModel().getResponseText().trim();
        CurrencyIfc discount = DomainGateway.getBaseCurrencyInstance(LocaleUtilities.parseCurrency(enteredAmount, LocaleMap.getLocale(LocaleMap.DEFAULT)).toString());

        // get reason from db
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        CodeListIfc reasonCodes = utility.getReasonCodes(cargo.getOperator().getStoreID(),CodeConstantsIfc.CODE_LIST_EMPLOYEE_DISCOUNT_REASON_CODES);
        LocalizedCodeIfc reason = DomainGateway.getFactory().getLocalizedCode();
        if (reasonCodes != null && reasonCodes.getNumberOfEntries() != 0)
        {
            String defaultReason = reasonCodes.getDefaultCodeString();
            CodeEntryIfc entry =  reasonCodes.findListEntryByCode(defaultReason);
            reason.setCode(defaultReason);
            reason.setText(entry.getLocalizedText());
        }
        else
        {
            reason.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        }
        reason.setCodeName(DiscountRuleConstantsIfc.ASSIGNMENT_BASIS_DESCRIPTORS[getAssignment()]);

        // Ensure discount doesn't make any individual prices negative or exceeds MaximumItemDiscountAmountPercent
        dialog = validateLineItemDiscounts(bus, lineItems, discount, reason);

        // No dialogs required
        if (dialog == null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
        // show dialog screen
        else
        {
            showDialog(dialog, EMPLOYEE_DISCOUNT_TAG, EMPLOYEE_DISCOUNT_TEXT, bus);
        }
    }

    /**
     * Determines if the item is eligible for empliyee discount
     *
     * @param srli The line item
     * @return true if the item is eligible for employee discount
     */
    protected boolean isEligibleForDiscount(SaleReturnLineItemIfc srli)
    {
        return DiscountUtility.isEmployeeDiscountEligible(srli);
    }

    /**
     * Returns the parameter name for the maximum employee discount percent.
     *
     * @return PricingCargo.MAX_EMPLOYEE_DISC_PCT
     */
    protected String getMaxPercentParameterName()
    {
        return PricingCargo.MAX_EMPLOYEE_DISC_PCT;
    }

    /**
     * Returns the assignment specification for employee discount.
     *
     * @return DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE
     */
    protected int getAssignment()
    {
        return DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE;
    }

}
