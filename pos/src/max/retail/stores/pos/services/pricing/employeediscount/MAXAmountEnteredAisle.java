/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.pricing.employeediscount;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.DiscountUtility;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.pricing.AbstractAmountEnteredAisle;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
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
 * <P>
 * 
 * @version $Revision: 1.2 $
 */
// --------------------------------------------------------------------------
public class MAXAmountEnteredAisle extends AbstractAmountEnteredAisle {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4181517881898053860L;
	/** Revision Number furnished by CVS. */
	public static final String revisionNumber = "$Revision: 1.2 $";
	/** Employee Discount tag */
	public static final String EMPLOYEE_DISCOUNT_TAG = "EmployeeDiscount";
	/** Employee discount text */
	public static final String EMPLOYEE_DISCOUNT_TEXT = "employee discount";

	// ----------------------------------------------------------------------
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
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 */
	// ----------------------------------------------------------------------
	public void traverse(BusIfc bus) {
		PricingCargo cargo = (PricingCargo) bus.getCargo();
		String dialog = null;
		String letter = CommonLetterIfc.CONTINUE;

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel beanModel = (POSBaseBeanModel) ui.getModel(POSUIManagerIfc.ENTER_EMPLOYEE_AMOUNT_DISCOUNT);

		// Rev 1.0 changes Start here
		cargo.setAccessFunctionID(MAXRoleFunctionIfc.PRICE_DISCOUNT);
		cargo.getAccessFunctionID();
		// Rev 1.0 changes end here

		// If input from the UI is emptystring,
		// then clear the discounts by amount for this item.
		// Otherwise, continue on to validating the discount amount.
		SaleReturnLineItemIfc[] lineItems = cargo.getItems();
		if (lineItems == null) {
			lineItems = new SaleReturnLineItemIfc[1];
			lineItems[0] = cargo.getItem();
		}

		// Create the discount strategy
		CurrencyIfc discount = DomainGateway.getBaseCurrencyInstance(beanModel.getPromptAndResponseModel().getResponseText());
		// Ensure discount doesn't make any individual prices negative or
		// exceeds MaximumItemDiscountAmountPercent
		dialog = validateLineItemDiscounts(bus, lineItems, discount, null);

		// No dialogs required
		if (dialog == null) {
			bus.mail(letter, BusIfc.CURRENT);
		}
		// show dialog screen
		else {
			// showDialog(dialog, EMPLOYEE_DISCOUNT_TAG, EMPLOYEE_DISCOUNT_TEXT,
			// bus);
			/**
			 * Rev 1.0 changes start here
			 */
			ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
			String percentParameterName = getMaxPercentParameterName();
			BigDecimal maxDiscountPct = getMaximumDiscountPercent(pm, percentParameterName);

			// Count the items actually available for discount
			int totalDiscountableItems = 0;

			HashMap discountHash = cargo.getValidDiscounts();
			discountHash.clear();
			for (int i = 0; i < lineItems.length; i++) {
				SaleReturnLineItemIfc srli = lineItems[i];
				if (!isEligibleForDiscount(srli)) {
					continue;
				} else {
					totalDiscountableItems++;
					discountHash.put(new Integer(i), null);
				}
			}

			// This method will update the discountHash with valid discounts
			// whether or not the prorating is turned on or not
			//String reason = null;
			LocalizedCodeIfc reason = null;
			if (cargo.hasInvalidDiscounts(maxDiscountPct, lineItems, discount, cargo.isOnlyOneDiscountAllowed(pm, logger), getAssignment(), reason,
					isDamageDiscount(), isMarkdown())) {
				// Depending on how many line items, show the appropriate
				// dialog when there are some invalid discounts
				if (lineItems.length > 1) {
					dialog = PricingCargo.MULTI_ITEM_INVALID_DISC;
				} else if (lineItems.length == 1) {
					dialog = PricingCargo.INVALID_DISC;
				}
			}

			// Violation of the max percent amount paramter trumps invalid
			// discounts,
			// but we have to validate on the discounts that were considered
			// valid -
			// those that don't cause the price to go negative.
			boolean maxDiscountAmountFailure = false;
			Set keys = discountHash.keySet();
			Integer indexInteger = null;
			int index = -1;
			for (Iterator i = keys.iterator(); !maxDiscountAmountFailure && i.hasNext();) {
				indexInteger = (Integer) i.next();
				index = indexInteger.intValue();
				ItemDiscountByAmountIfc currentDiscountStrategy = (ItemDiscountByAmountIfc) discountHash.get(indexInteger);
				SaleReturnLineItemIfc srli = lineItems[index];

				CurrencyIfc maxDiscountAmt = srli.getExtendedSellingPrice().multiply(maxDiscountPct.movePointLeft(2));
				if (currentDiscountStrategy.getDiscountAmount().abs().compareTo(maxDiscountAmt.abs()) > 0) {
					// dialog = PricingCargo.INVALID_DISC;
					maxDiscountAmountFailure = true;
				}
			}

			bus.mail("AmountOverrideYes", BusIfc.CURRENT);

			/**
			 * Rev 1.0 changes end here
			 */
		}
	}

	// ----------------------------------------------------------------------
	/**
	 * Determines if the item is eligible for empliyee discount
	 * 
	 * @param srli
	 *            The line item
	 * @return true if the item is eligible for employee discount
	 */
	// ----------------------------------------------------------------------
	protected boolean isEligibleForDiscount(SaleReturnLineItemIfc srli) {
		boolean isEligible = DiscountUtility.isDiscountAllowed(srli, true, true);

		if (isEligible) {
			isEligible = srli.getPLUItem().getItemClassification().getEmployeeDiscountAllowedFlag();
		}
		return isEligible;
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns the parameter name for the maximum employee discount percent.
	 * 
	 * @return PricingCargo.MAX_EMPLOYEE_DISC_PCT
	 */
	// ----------------------------------------------------------------------
	protected String getMaxPercentParameterName() {
		return PricingCargo.MAX_EMPLOYEE_DISC_PCT;
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns the assignment specification for employee discount.
	 * 
	 * @return DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE
	 */
	// ----------------------------------------------------------------------
	protected int getAssignment() {
		return DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE;
	}

}
