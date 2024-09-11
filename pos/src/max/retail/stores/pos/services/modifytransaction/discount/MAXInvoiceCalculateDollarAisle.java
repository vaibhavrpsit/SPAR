/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev 1.1		Mar 20, 2016		Mansi Goel		Changes to resolve best deal issue for bill buster rules
 *	Rev	1.0 	Nov 30, 2016		Mansi Goel		Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.modifytransaction.discount;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import max.retail.stores.domain.discount.MAXAdvancedPricingRuleIfc;
import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.TransactionDiscountByAmountIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.utility.CheckDigitUtility;

public class MAXInvoiceCalculateDollarAisle extends PosLaneActionAdapter {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * revision number
	 **/
	public static final String revisionNumber = "$Revision: 1.2 $";
	/**
	 * length of available space for discount value
	 **/
	public static int AVAIL_DISCOUNT_LENGTH = 23;
	/**
	 * constant for parameter name
	 * 
	 * @deprecated as of release 7.0. No replacement
	 **/
	public static final String MAX_DISC_PCT = "MaximumTransactionDiscountAmountPercent";
	/**
	 * constant for error dialog screen
	 * 
	 * @deprecated as of release 7.0. No replacement
	 **/
	public static final String INVALID_DISC = "InvalidDiscount";
	/**
	 * constant for error dialog screen
	 * 
	 * @deprecated as of release 7.0. No replacement
	 **/
	public static final String INVALID_TRANS_DISC = "InvalidTransactionDiscountPercent";
	/**
	 * constant for error dialog screen
	 **/
	public static final String INVALID_REASON_CODE = "InvalidReasonCode";

	/**
	 * resource id for invalid transaction discount dialog
	 **/
	protected static final String INVALID_TRANSACTION_DISCOUNT_DIALOG = "InvalidTransactionDiscountPercent";

	// ----------------------------------------------------------------------
	/**
	 * Stores the amount and reason code.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void traverse(BusIfc bus) {
		// Get access to common elements

		List<CurrencyIfc> invoiceRuleList = new ArrayList<CurrencyIfc>();
		List<MAXAdvancedPricingRuleIfc> eligibleRuleList = new ArrayList<MAXAdvancedPricingRuleIfc>();
		BigDecimal percent = new BigDecimal("0.00");
		boolean createTransaction = false;

		MAXModifyTransactionDiscountCargo cargo = (MAXModifyTransactionDiscountCargo) bus.getCargo();
		SaleReturnLineItemIfc[] lineItems = null;
		lineItems = (SaleReturnLineItemIfc[]) cargo.getTransaction().getLineItems();
        if (lineItems == null)
        {
            lineItems = new SaleReturnLineItemIfc[1];
            lineItems[0] = (SaleReturnLineItemIfc) cargo.getTransaction().getLineItemsVector().get(0);
        }
		
		ArrayList<MAXAdvancedPricingRuleIfc> invoiceRules = cargo.getInvoiceDiscounts();
		CurrencyIfc tranBalance = DomainGateway.getBaseCurrencyInstance();
		//changed for calculating discount on discount eligible items only
		//tranBalance = cargo.getTransaction().getTransactionTotals().getBalanceDue();
		tranBalance = cargo.getTransaction().getTransactionTotals().getDiscountEligibleSubtotal();
		int invoiceRuleLength = invoiceRules.size();
		for (int i = 0; i < invoiceRuleLength; i++) {
			// amount: in invoice rule, to compare with transaction amount
			CurrencyIfc amount = invoiceRules.get(i).getSourceList().getItemThreshold();

			// if transaction value satisfies any of the invoice rule, keep
			// aside all ;
			// the existing invoice rule(s);

			if ((tranBalance.compareTo(amount) >= 0) && !("0.00").equals(amount.getStringValue())) {
				// All eligible rules in a list;
				// and keeps the best discount rate;

				eligibleRuleList.clear();
				eligibleRuleList.add(invoiceRules.get(i));
				if (invoiceRuleList.size() == 0)
					invoiceRuleList.add(invoiceRules.get(i).getDiscountAmount());

				// Compare the new Rate with Old rate;
				(eligibleRuleList.get(0).getDiscountAmount()).getStringValue();
				BigDecimal disRateCurrent = new BigDecimal(eligibleRuleList.get(0).getDiscountAmount().getStringValue());
				BigDecimal disRatePrevious = new BigDecimal((invoiceRuleList.get(0)).toString());
				if ((disRateCurrent.compareTo(disRatePrevious)) > 0) {
					invoiceRuleList.clear();
					invoiceRuleList.add(invoiceRules.get(i).getDiscountAmount());
				}
			}

		}

		if (invoiceRuleList.size() == 1) {
			// If only one rule is there;If no rule is eligible, in that case
			// also it has 0.00 discount;
			//Changes for Rev 1.1 : Starts
			percent = new BigDecimal(invoiceRuleList.get(0).toString());
			//Changes for Rev 1.1 : Ends
			//commented by Izhar
			//cargo.setInvoiceRuleAppliedRate(percent); // To stop toggling
			//cargo.setInvoiceRuleAlreadyApplied(true); // To stop toggling
		}
		//commented by Izhar
		/*if ((percent.intValue() == 0) && cargo.isInvoiceRuleAlreadyApplied() && cargo.getInvoiceRuleAppliedRate() != null
				&& (cargo.getInvoiceRuleAppliedRate()).compareTo(new BigDecimal("0.00")) != 0)
			percent = cargo.getInvoiceRuleAppliedRate();*/ // This check was
															// applied by gaurav
															// to stop toggling
															// of Apply BestDeal
															// button

		if (percent.toString().length() >= 5) {
			BigDecimal scaleOne = new BigDecimal(1);
			percent = percent.divide(scaleOne, 2, BigDecimal.ROUND_HALF_UP);
		}

		CurrencyIfc percentDiscount = DomainGateway.getBaseCurrencyInstance(String.valueOf(percent));
		// percentDiscount.setDiscountRate(percent);

		if (percent.intValue() > 0) {
			TransactionDiscountByAmountIfc amountDiscount = createDiscountStrategy(cargo, percentDiscount, bus.getServiceName());
			// cargo.setOldDiscount(percentDiscount);

			cargo.setAccessFunctionID(MAXRoleFunctionIfc.PRICE_DISCOUNT);
			cargo.setDiscount(amountDiscount);
			// If this discount is true, then only above invoice discount will
			// be
			// applied
			cargo.setDoDiscount(true);

			// Type for invoice type of rules
			cargo.setDiscountType(MAXRoleFunctionIfc.INVOICE_TYPE_DISCOUNT_DOLLAR);

			// percentDiscount.setReasonCode(cargo.getDiscountType());
		}
		cargo.setCreateTransaction(createTransaction);
		bus.mail("AfterInvoicePercent", BusIfc.CURRENT);

	}

	// --------------------------------------------------------------------------
	/**
	 * Clears the transaction discount.
	 * <P>
	 * 
	 * @param cargo
	 *            The cargo containing discounts to be cleared
	 * @deprecated as of release 7.0. No replacement
	 **/
	// ----------------------------------------------------------------------
	public void clearDiscount(MAXModifyTransactionDiscountCargo cargo) {
		// get journal manager
		JournalManagerIfc mgr = (JournalManagerIfc) Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);

		cargo.setClearDiscount(true);

		// journal removal of discount due to Clear key
		if (cargo.getDiscount() != null) {
			TransactionDiscountByAmountIfc discountAmount = ((TransactionDiscountByAmountIfc) (cargo.getDiscount()));
			CurrencyIfc discountCurr = discountAmount.getDiscountAmount();
			String discountAmountStr = discountCurr.toFormattedString().trim();
			StringBuffer msg = new StringBuffer();
			msg.append(Util.EOL).append("TRANS: Discount").append(Util.SPACES.substring(discountAmountStr.length(), AVAIL_DISCOUNT_LENGTH))
					.append(discountAmountStr).append(Util.EOL).append("  Discount: $ Deleted")
					// RRNdebug - replaced "Removed"
					.append(Util.EOL).append("  Disc. Rsn.: ").append(discountAmount.getReasonCodeText());
			String str = "";
			mgr.journal(str, str, msg.toString());
		}

	}

	// ----------------------------------------------------------------------
	/**
	 * Displays the invalid discount error screen.
	 * <P>
	 * 
	 * @param uiManager
	 *            The POSUIManager
	 */
	// ----------------------------------------------------------------------
	protected void showInvalidReasonCodeDialog(POSUIManagerIfc uiManager) {
		// display the invalid discount error screen
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(INVALID_REASON_CODE);
		dialogModel.setType(DialogScreensIfc.ERROR);

		uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	// ----------------------------------------------------------------------
	/**
	 * Creates discount strategy.
	 * 
	 * @param cargo
	 *            The bus cargo
	 * @param beanModel
	 *            The DecimalWithReasonBeanModel
	 * @param discount
	 *            Discount amount
	 * @param serviceName
	 *            The name of the calling service
	 * @return The TransactionDiscountByAmount strategy
	 */
	// ----------------------------------------------------------------------
	protected TransactionDiscountByAmountIfc createDiscountStrategy(ModifyTransactionDiscountCargo cargo, CurrencyIfc discount, String serviceName) {

		// String reason = beanModel.getSelectedReason();

		// Validate the Reason Code ID Check Digit, Valid Reason Code exists
		// CodeEntryIfc reasonEntry =
		// cargo.getReasonCodes().findListEntry(reason, false);

		int reasonInt = MAXRoleFunctionIfc.INVOICE_TYPE_DISCOUNT_PERCENT;

		TransactionDiscountByAmountIfc amountDiscount = DomainGateway.getFactory().getTransactionDiscountByAmountInstance();
		amountDiscount.setDiscountAmount(discount);
		amountDiscount.setReasonCode(reasonInt);
		// amountDiscount.setReasonCodeText(reason);
		return amountDiscount;
	}

	// --------------------------------------------------------------------------
	/**
	 * Check digit validation.
	 * <P>
	 * 
	 * @param utility
	 *            utility manager
	 * @param reasonCodeID
	 *            the reason code ID that needs to be checked
	 * @param serviceName
	 *            service name
	 * @return boolean return true if valid, otherwise return false
	 **/
	// ----------------------------------------------------------------------
	public static boolean isValidCheckDigit(UtilityManagerIfc utility, String reasonCodeID, String serviceName) {
		boolean isValid = false;
		if (!utility.validateCheckDigit(CheckDigitUtility.CHECK_DIGIT_FUNCTION_REASON_CODE, reasonCodeID)) {
			// If check digit is not configured for reason code, the check digit
			// function will always return true
			if (logger.isInfoEnabled())
				logger.info("Invalid number received. check digit is invalid. Prompting user to re-enter the information ...");
		} else {
			isValid = true;
		}
		return isValid;
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns a BigInteger, the maximum discount % allowed from the parameter
	 * file.
	 * <P>
	 * 
	 * @param pm
	 *            ParameterManagerIfc reference
	 * @param serviceName
	 *            service name (for log)
	 * @return maximum discount percent allowed as BigInteger
	 **/
	// ----------------------------------------------------------------------
	private BigInteger getMaximumDiscountPercent(ParameterManagerIfc pm, String serviceName) {
		BigInteger maximum = new BigInteger("100"); // default
		try {
			String s = pm.getStringValue(MAX_DISC_PCT);
			s.trim();
			maximum = new BigInteger(s);
			if (logger.isInfoEnabled())
				logger.info("Parameter read: " + MAX_DISC_PCT + "=[" + maximum + "]");
		} catch (ParameterException e) {
			logger.error("" + Util.throwableToString(e) + "");
		}

		return (maximum);
	}

	// --------------------------------------------------------------------------
	/**
	 * Validates the discount.
	 * <P>
	 * 
	 * @param bus
	 *            The service bus
	 * @param percent
	 *            The percentage of the discount as a Big Decimal
	 * @return boolean return true if valid
	 **/
	// ----------------------------------------------------------------------
	public boolean isValidDiscount(BusIfc bus, BigDecimal percent) {
		BigInteger percentInt = percent.movePointRight(2).toBigInteger();

		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		// get maximum disc % allowed parameter
		BigInteger maxTransDiscPct = getMaximumDiscountPercent(pm, bus.getServiceName());

		return (percentInt.compareTo(maxTransDiscPct) < 1);

	}

	// ----------------------------------------------------------------------
	/**
	 * Displays the invalid discount error screen.
	 * <P>
	 * 
	 * @param uiManager
	 *            The POSUIManager
	 * @param msg
	 *            The string array representing the arguments for the dialog
	 */
	// ----------------------------------------------------------------------
	protected void showInvalidTransactionDiscountDiscountDialog(POSUIManagerIfc uiManager, String[] msg) {
		// display the invalid discount error screen
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(INVALID_TRANSACTION_DISCOUNT_DIALOG);
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setArgs(msg);

		// display dialog
		uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

}
