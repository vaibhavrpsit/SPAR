/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.pricing.employeediscount;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import max.retail.stores.pos.services.pricing.MAXPricingCargo;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByPercentageIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.DiscountUtility;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.pricing.AbstractPercentEnteredAisle;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
 * This aisle validates the percent entered to make sure it doesn't exceed the
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
public class MAXRemovePercentEnteredAisle extends AbstractPercentEnteredAisle {
	/**
	 * 
	 */
	private static final long serialVersionUID = 140366044407432783L;
	/** Revision Number */
	public static final String revisionNumber = "$Revision: 1.2 $";
	/** tag for dialog <ARG> */
	public static final String EMPLOYEE_DISCOUNT_TAG = "EmployeeDiscount";
	/** text for dialog <ARG> */
	public static final String EMPLOYEE_DISCOUNT_TEXT = "employee discount";

	// ----------------------------------------------------------------------
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
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 */
	// ----------------------------------------------------------------------
	public void traverse(BusIfc bus) {
		/**
		 * Rev 1.0 changes start here
		 */
		MAXPricingCargo cargo = (MAXPricingCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		String dialog = null;
		String letter = CommonLetterIfc.CONTINUE;
		String pmValue = null;
		int pmPercent = 0;
		BigDecimal response = new BigDecimal("0.00");
		ParameterManagerIfc pm2 = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		try {
			pmValue = pm2.getStringValue("EmployeeDiscountMethod");
		} catch (ParameterException e) {
			if (logger.isInfoEnabled())
				logger.info("MAXRemovePercentEnteredAisle.traverse(), cannot find EmployeeDiscountMethod paremeter.");
		}
		if ("Manual".equalsIgnoreCase(pmValue)) {
			POSBaseBeanModel beanModel = (POSBaseBeanModel) ui.getModel(POSUIManagerIfc.ENTER_EMPLOYEE_PERCENT_DISCOUNT);

			// validate the discount amount.
			response = new BigDecimal(beanModel.getPromptAndResponseModel().getResponseText()).setScale(2);
		} else {
			ParameterManagerIfc pm1 = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
			try {
				pmPercent = pm1.getIntegerValue("EmployeeDiscountAmountPercent").intValue();
				response = new BigDecimal(pmPercent).setScale(2);
				cargo.setAccessFunctionID(MAXRoleFunctionIfc.PRICE_DISCOUNT);
			} catch (ParameterException e) {
				if (logger.isInfoEnabled())
					logger.info("MAXRemovePercentEnteredAisle.traverse(), cannot find EmployeeDiscountAmountPercent paremeter.");
			}

		}
		/**
		 * Rev 1.0 changes end here
		 */
		SaleReturnLineItemIfc[] lineItems = cargo.getItems();

		if (lineItems == null) {
			lineItems = new SaleReturnLineItemIfc[1];
			lineItems[0] = cargo.getItem();
		}

		response = response.divide(new BigDecimal(100.0), BigDecimal.ROUND_HALF_UP);

		// Chop off the potential long values caused by BigDecimal.
		if (response.toString().length() > 5) {
			BigDecimal scaleOne = new BigDecimal(1);
			response = response.divide(scaleOne, 2, BigDecimal.ROUND_HALF_UP);
		}

		// Ensure discount doesn't make any individual prices negative or
		// exceeds MaximumItemDiscountAmountPercent
		dialog = validateLineItemDiscounts(bus, lineItems, response, null);

		// No dialogs required
		if (dialog == null) {
			bus.mail(letter, BusIfc.CURRENT);
		}
		// showdialog screen
		else {

			// showDialog(dialog, EMPLOYEE_DISCOUNT_TAG, EMPLOYEE_DISCOUNT_TEXT,
			// bus);
				/**
				 * Rev 1.0 changes start here
				 */
			ItemDiscountByPercentageIfc sgy = null;
			// String dialog = null;
			ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

			// convert beanModel percent value to BigInteger
			// fractional portion of BigDecimal throws off the comparison to
			// parameter
			BigInteger percentInt = response.movePointRight(2).toBigInteger();
			// String reason = beanModel.getSelectedReason();

			// get maximum discount % allowed from parameter file
			BigInteger maxDiscountPct = getMaximumDiscountPercent(pm, getMaxPercentParameterName());

			cargo.setAccessFunctionID(MAXRoleFunctionIfc.PRICE_DISCOUNT);
			cargo.getAccessFunctionID();

			HashMap discountHash = cargo.getValidDiscounts();
			discountHash.clear();
			boolean hasInvalidDiscounts = false;
			for (int i = 0; i < lineItems.length; i++) {
				SaleReturnLineItemIfc srli = lineItems[i];

				// If the item is not discount eligible, go on to
				// next item.
				if (!(isEligibleForDiscount(srli))) {
					continue;
				}

				/*
				 * // Ensure the discount doesn't exceed the
				 * MaximumItemDiscountAmountPercent parameter if
				 * (percentInt.compareTo(maxDiscountPct) > 0) { dialog =
				 * PricingCargo.INVALID_ITEM_DISC; }
				 */
				else {
					sgy = createDiscountStrategy(cargo, response, null);
					// check to see if adding this discount will make the item's
					// price go
					// negative (or positive if it is a return item)
					SaleReturnLineItemIfc clone = (SaleReturnLineItemIfc) srli.clone();
					clearDiscountsByPercentage(clone);
					clone.addItemDiscount(sgy);
					clone.calculateLineItemPrice();

					if ((clone.isSaleLineItem() && clone.getExtendedDiscountedSellingPrice().signum() < 0)
							|| (clone.isReturnLineItem() && clone.getExtendedDiscountedSellingPrice().signum() > 0)) {
						hasInvalidDiscounts = true;
					} else {
						discountHash.put(new Integer(i), sgy);
					}
				}
			}

			// check to see if adding this discount will make the item's price
			// go
			// negative (or positive if it is a return item)
			if (dialog == null && hasInvalidDiscounts) {
				if (lineItems.length > 1) {
					dialog = PricingCargo.MULTI_ITEM_INVALID_DISC;
				} else if (lineItems.length == 1) {
					dialog = PricingCargo.INVALID_DISC;
				}
			}

			// return dialog;
			if (pmValue.equalsIgnoreCase("Automatic")) {
				if (cargo.isEmployeeRemoveSelected())
					bus.mail("RemoveEmpDiscAuto", BusIfc.CURRENT);
				else
					bus.mail("EmpDiscAuto", BusIfc.CURRENT);
			} else
				bus.mail("PercentOverrideYes", BusIfc.CURRENT);

			/**
			 * Rev 1.0 changes end here
			 */
		}
	}

	// ----------------------------------------------------------------------
	/**
	 * Determines if the item is eligible for the discount
	 * 
	 * @param srli
	 *            The line item
	 * @return true if the item is eligible for the discount
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
	 * Returns the parameter name for the maximum employee discount percent
	 * 
	 * @return PricingCargo.MAX_EMPLOYEE_DISC_PCT
	 */
	// ----------------------------------------------------------------------
	protected String getMaxPercentParameterName() {
		return PricingCargo.MAX_EMPLOYEE_DISC_PCT;
	}

	// ----------------------------------------------------------------------
	/**
	 * Clears the employee discounts by percentage from the line item
	 * 
	 * @param srli
	 *            The line item
	 */
	// ----------------------------------------------------------------------
	protected void clearDiscountsByPercentage(SaleReturnLineItemIfc srli) {
		srli.clearItemDiscountsByPercentage(DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE, false);
	}

	// ----------------------------------------------------------------------
	/**
	 * Determines if this is an employee discount.
	 * 
	 * @return true
	 */
	// ----------------------------------------------------------------------
	protected boolean isEmployeeDiscount() {
		return true;
	}

}
