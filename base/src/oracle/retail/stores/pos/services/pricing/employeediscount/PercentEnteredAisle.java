/* ===========================================================================
* Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/employeediscount/PercentEnteredAisle.java /main/16 2013/12/10 16:54:08 rabhawsa Exp $
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
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    cgreene   03/20/09 - implement printing of Employee by making it a
 *                         separate lookup reason code like damage discount
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         3/13/2008 11:57:40 PM  Vikram Gopinath
 *         Removed code to ROUND_HALF_UP.
 *    7    360Commerce 1.6         2/13/2006 3:56:03 PM   Edward B. Thorne
 *         Merge from PercentEnteredAisle.java, Revision 1.4.1.0
 *    6    360Commerce 1.5         2/6/2006 5:20:34 PM    Rohit Sachdeva
 *         10513: Fixing unit tests in trunk
 *    5    360Commerce 1.4         1/25/2006 4:11:35 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         1/22/2006 11:45:16 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:29:20 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:02 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/20/2005 15:44:19    Deepanshu       CR
 *         6127: Check each item for return before applying discount
 *    3    360Commerce1.2         3/31/2005 15:29:20     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:24:03     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:13:02     Robert Pearse
 *
 *   Revision 1.14  2004/03/19 23:27:50  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Code review cleanup.
 *
 *   Revision 1.13  2004/03/17 23:03:11  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Code review modifications.
 *
 *   Revision 1.12  2004/03/12 23:12:53  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Extracted common code to abstract class.
 *
 *   Revision 1.11  2004/02/24 16:21:31  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.10  2004/02/20 17:34:57  cdb
 *   @scr 3588 Removed "developmental" log entries from file header.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing.employeediscount;

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
import java.math.BigDecimal;

/**
 *   This aisle validates the percent entered to make sure it doesn't
 *   exceed the maximum employee discount amount/percent parameter and
 *   that there are no items that would have a negative price after the
 *   discount is applied without a warning or error dialog.
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
 *   @version $Revision: /main/16 $
 */
public class PercentEnteredAisle extends AbstractPercentEnteredAisle
{
    private static final long serialVersionUID = -4049265753729757220L;
    /** Revision Number */
    public static final String revisionNumber = "$Revision: /main/16 $";
    /** tag for dialog <ARG> */
    public static final String EMPLOYEE_DISCOUNT_TAG = "EmployeeDiscount";
    /** text for dialog <ARG> */
    public static final String EMPLOYEE_DISCOUNT_TEXT = "employee discount";

    /**
     * This aisle validates the percent entered to make sure it doesn't exceed
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
        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        String dialog = null;
        String letter = CommonLetterIfc.CONTINUE;

        POSBaseBeanModel beanModel =
            (POSBaseBeanModel) ui.getModel(POSUIManagerIfc.ENTER_EMPLOYEE_PERCENT_DISCOUNT);

        // validate the discount amount.
        BigDecimal response = new BigDecimal(beanModel.getPromptAndResponseModel().getResponseText()).setScale(2);
        SaleReturnLineItemIfc[] lineItems = cargo.getItems();

        if (lineItems == null)
        {
            lineItems = new SaleReturnLineItemIfc[1];
            lineItems[0] = cargo.getItem();
        }

        response = response.divide(new BigDecimal(100.00));

        // Chop off the potential long values caused by BigDecimal.
        if (response.toString().length() > 5)
        {
            BigDecimal scaleOne = new BigDecimal(1);
            response = response.divide(scaleOne, 2);
        }

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
        reason.setCodeName(DiscountRuleConstantsIfc.ASSIGNMENT_BASIS_DESCRIPTORS[DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE]);

        // Ensure discount doesn't make any individual prices negative or exceeds MaximumItemDiscountAmountPercent
        dialog = validateLineItemDiscounts(bus, lineItems, response, reason);

        // No dialogs required
        if (dialog == null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
        // showdialog screen
        else
        {
            showDialog(dialog, EMPLOYEE_DISCOUNT_TAG, EMPLOYEE_DISCOUNT_TEXT, bus);
        }
    }

    /**
     * Determines if the item is eligible for the discount
     * @param srli  The line item
     * @return true if the item is eligible for the discount
     */
    protected boolean isEligibleForDiscount(SaleReturnLineItemIfc srli)
    {
        return DiscountUtility.isEmployeeDiscountEligible(srli);
    }

    /**
     * Returns the parameter name for the maximum employee discount percent
     * @return PricingCargo.MAX_EMPLOYEE_DISC_PCT
     */
    protected String getMaxPercentParameterName()
    {
        return PricingCargo.MAX_EMPLOYEE_DISC_PCT;
    }

    /**
     * Clears the employee discounts by percentage from the line item
     * @param srli   The line item
     */
    protected void clearDiscountsByPercentage(SaleReturnLineItemIfc srli)
    {
        srli.clearItemDiscountsByPercentage(DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE, false);
    }

    /**
     * Determines if this is an employee discount.
     * @return true
     */
    protected boolean isEmployeeDiscount()
    {
        return true;
    }

}
