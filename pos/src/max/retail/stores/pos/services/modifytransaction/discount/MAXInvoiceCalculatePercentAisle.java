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
import java.util.ArrayList;
import java.util.List;

import max.retail.stores.domain.discount.MAXAdvancedPricingRuleIfc;
import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

public class MAXInvoiceCalculatePercentAisle extends PosLaneActionAdapter {

	private static final long serialVersionUID = -4443998289758055965L;

	public void traverse(BusIfc bus) {
		List<BigDecimal> invoiceRuleList = new ArrayList<BigDecimal>();
		List<MAXAdvancedPricingRuleIfc> eligibleRuleList = new ArrayList<MAXAdvancedPricingRuleIfc>();
		BigDecimal percent = new BigDecimal("0.00");
		boolean createTransaction = false;
		MAXModifyTransactionDiscountCargo cargo = (MAXModifyTransactionDiscountCargo) bus.getCargo();

		ArrayList<MAXAdvancedPricingRuleIfc> invoiceRules = cargo.getInvoiceDiscounts();

		// getting the transaction current value;
		CurrencyIfc tranBalance = DomainGateway.getBaseCurrencyInstance();

		tranBalance = cargo.getTransaction().getTransactionTotals().getDiscountEligibleSubtotal();

		// getting length of all the invoice rules that may be applied;
		int invoiceRuleLength = invoiceRules.size();

		for (int i = 0; i < invoiceRuleLength; i++) {
			// amount: in invoice rule, to compare with transaction amount
			CurrencyIfc amount = invoiceRules.get(i).getSourceList().getItemThreshold();
			if ((tranBalance.compareTo(amount) >= 0) && !("0.00").equals(amount.getStringValue())) {
				// All eligible rules in a list;
				// and keeps the best discount rate;

				eligibleRuleList.clear();
				eligibleRuleList.add(invoiceRules.get(i));
				if (invoiceRuleList.size() == 0)
					invoiceRuleList.add(invoiceRules.get(i).getDiscountRate());

				// Compare the new Rate with Old rate;

				BigDecimal disRateCurrent = eligibleRuleList.get(0).getDiscountRate();
				BigDecimal disRatePrevious = invoiceRuleList.get(0);
				if ((disRateCurrent.compareTo(disRatePrevious)) > 0) {
					invoiceRuleList.clear();
					invoiceRuleList.add(invoiceRules.get(i).getDiscountRate());

				}
			}

		}

		if (invoiceRuleList.size() == 1) {
			// If only one rule is there;If no rule is eligible, in that case
			// also it has 0.00 discount;
			//Changes for Rev 1.1 : Starts
			percent = new BigDecimal(invoiceRuleList.get(0).toString());
			//Changes for Rev 1.1 : Ends
			cargo.setInvoiceRuleAppliedRate(percent);
			cargo.setInvoiceRuleAlreadyApplied(true);
		}
		if ((percent.intValue() == 0) && cargo.isInvoiceRuleAlreadyApplied()
				&& cargo.getInvoiceRuleAppliedRate() != null
				&& (cargo.getInvoiceRuleAppliedRate()).compareTo(new BigDecimal("0.00")) != 0)
			percent = cargo.getInvoiceRuleAppliedRate();

		if (percent.toString().length() >= 5) {
			BigDecimal scaleOne = new BigDecimal(1);
			percent = percent.divide(scaleOne, 2, BigDecimal.ROUND_HALF_UP);
		}
		if (percent.multiply(new BigDecimal("100.00")).intValue() > 0) {
			TransactionDiscountByPercentageIfc percentDiscount = DomainGateway.getFactory()
					.getTransactionDiscountByPercentageInstance();
			percentDiscount.setDiscountRate(percent);
			percentDiscount.setReasonCode(cargo.getDiscountType());
			
			cargo.setDiscount(percentDiscount);
			// If this discount is true, then only above invoice discount will
			// be
			// applied
			cargo.setDoDiscount(true);

			// Type for invoice type of rules
			cargo.setDiscountType(MAXRoleFunctionIfc.INVOICE_TYPE_DISCOUNT_PERCENT);
			percentDiscount.setReasonCode(cargo.getDiscountType());
		}
		cargo.setCreateTransaction(createTransaction);
		bus.mail("AfterInvoicePercent", BusIfc.CURRENT);
	}
}
