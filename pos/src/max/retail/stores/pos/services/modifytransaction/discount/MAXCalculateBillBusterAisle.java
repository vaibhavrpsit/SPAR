package max.retail.stores.pos.services.modifytransaction.discount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXHotKeysTransaction;
import max.retail.stores.domain.discount.MAXAdvancedPricingRuleIfc;
import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;

public class MAXCalculateBillBusterAisle extends PosLaneActionAdapter {

	private static final long serialVersionUID = -8660375205766304135L;
	public static int AVAIL_DISCOUNT_LENGTH = 23;

	public void traverse(BusIfc bus) {

		MAXModifyTransactionDiscountCargo cargo = (MAXModifyTransactionDiscountCargo) bus.getCargo();
		CurrencyIfc tranBalance = DomainGateway.getBaseCurrencyInstance();
		tranBalance = cargo.getTransaction().getTransactionTotals().getDiscountEligibleSubtotal();

		if (cargo.getDiscountType() == MAXRoleFunctionIfc.INVOICE_TYPE_DISCOUNT) {

		}

		ArrayList<CurrencyIfc> invoiceRuleListDollar = new ArrayList<CurrencyIfc>();
		ArrayList<AdvancedPricingRuleIfc> eligibleRuleListDollar = new ArrayList<AdvancedPricingRuleIfc>();
		List<BigDecimal> invoiceRuleListPercent = new ArrayList<BigDecimal>();
		List<AdvancedPricingRuleIfc> eligibleRuleListPercent = new ArrayList<AdvancedPricingRuleIfc>();
		BigDecimal percentVariableDollar = new BigDecimal("0.00");
		BigDecimal percentVariablePercent = new BigDecimal("0.00");
		boolean createTransaction = false;
		List<MAXAdvancedPricingRuleIfc> invoiceDollarRulesTotalList = new ArrayList<MAXAdvancedPricingRuleIfc>();
		List<MAXAdvancedPricingRuleIfc> invoicePercentRulesTotalList = new ArrayList<MAXAdvancedPricingRuleIfc>();
		
		String billBusterPromo = null;

		ArrayList<MAXAdvancedPricingRuleIfc> invoiceRulesAll = cargo.getInvoiceDiscounts();

		for (int i = 0; i < invoiceRulesAll.size(); i++) {
			
			if (Integer.parseInt(invoiceRulesAll.get(i).getReason().getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZ$offTiered_BillBuster) {
				invoiceDollarRulesTotalList.add(invoiceRulesAll.get(i));
				//invoiceDollarRulesTotalList.get(i).setSourcesAreTargets(false);
				//if(invoicePercentRulesTotalList.get(i).getSourceList().getItemThreshold().compareTo(cargo.getTransaction().getTenderTransactionTotals().getBalanceDue())==-1)
				//{
					MAXHotKeysTransaction hotKeysTransaction = (MAXHotKeysTransaction) DataTransactionFactory
						.create(MAXDataTransactionKeys.MAX_HOT_KEYS_LOOKUP_TRANSACTION);
					//cargo.getTransaction().setCustomerInfo(cargo.getCustomerInfo());
					try {
						billBusterPromo = ((MAXHotKeysTransaction) hotKeysTransaction)
								.getBillBusterAmtDetails(cargo.getTransaction());
						invoiceDollarRulesTotalList.get(0).setTargetItemId(billBusterPromo);
					} catch (DataException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}//}
			}
			if (Integer.parseInt(invoiceRulesAll.get(i).getReason().getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZPctoffTiered_BillBuster) {
				invoicePercentRulesTotalList.add(invoiceRulesAll.get(i));
				//invoiceDollarRulesTotalList.get(i).setSourcesAreTargets(false);
				//String billBusterPromo = null;
				if(invoicePercentRulesTotalList.get(i).getSourceList().getItemThreshold().compareTo(cargo.getTransaction().getTenderTransactionTotals().getBalanceDue())==-1)
				{
					MAXHotKeysTransaction hotKeysTransaction = (MAXHotKeysTransaction) DataTransactionFactory
						.create(MAXDataTransactionKeys.MAX_HOT_KEYS_LOOKUP_TRANSACTION);
					//cargo.getTransaction().setCustomerInfo(cargo.getCustomerInfo());
					try {
						billBusterPromo = ((MAXHotKeysTransaction) hotKeysTransaction)
								.getBillBusterPctDetails(cargo.getTransaction());
						invoicePercentRulesTotalList.get(0).setTargetItemId(billBusterPromo);
					} catch (DataException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			}
		}
		
		AdvancedPricingRuleIfc[] invoiceDollarRulesTotalArray = new AdvancedPricingRuleIfc[invoiceDollarRulesTotalList.size()];
		invoiceDollarRulesTotalArray = invoiceDollarRulesTotalList.toArray(new AdvancedPricingRuleIfc[invoiceDollarRulesTotalList
				.size()]);
		
		int invoiceRuleLength = invoiceDollarRulesTotalArray.length;
		for (int i = 0; i < invoiceRuleLength; i++) {
			// amount: in invoice rule, to compare with transaction amount
			CurrencyIfc amount = invoiceDollarRulesTotalArray[i].getSourceList().getItemThreshold();

			// if transaction value satisfies any of the invoice rule, keep
			// aside all ;
			// the existing invoice rule(s);

			if ((tranBalance.compareTo(amount) >= 0) && !("0.00").equals(amount.getStringValue())) {
				// All eligible rules in a list;
				// and keeps the best discount rate;

				eligibleRuleListDollar.clear();
				eligibleRuleListDollar.add(invoiceDollarRulesTotalArray[i]);
				if (invoiceRuleListDollar.size() == 0)
					invoiceRuleListDollar.add(invoiceDollarRulesTotalArray[i].getDiscountAmount());

				// Compare the new Rate with Old rate;
				(eligibleRuleListDollar.get(0).getDiscountAmount()).getStringValue();
				BigDecimal disRateCurrent = new BigDecimal(eligibleRuleListDollar.get(0).getDiscountAmount().getStringValue());
				BigDecimal disRatePrevious = new BigDecimal((invoiceRuleListDollar.get(0)).toString());
				if ((disRateCurrent.compareTo(disRatePrevious)) > 0) {
					invoiceRuleListDollar.clear();
					invoiceRuleListDollar.add(invoiceDollarRulesTotalArray[i].getDiscountAmount());
				}
			}

		}

		if (invoiceRuleListDollar.size() == 1) {
			// If only one rule is there;If no rule is eligible, in that case
			// also it has 0.00 discount;
			//Changes for Rev 1.1 : Starts
			percentVariableDollar = new BigDecimal(invoiceRuleListDollar.get(0).toString());
			//Changes for Rev 1.1 : Ends
		}

		if (percentVariableDollar.toString().length() >= 5) {
			BigDecimal scaleOne = new BigDecimal(1);
			percentVariableDollar = percentVariableDollar.divide(scaleOne, 2, BigDecimal.ROUND_HALF_UP);
		}

		CurrencyIfc percentDiscount = DomainGateway.getBaseCurrencyInstance(String.valueOf(percentVariableDollar));
		// percentDiscount.setDiscountRate(percent);

		TransactionDiscountByAmountIfc amountDiscount = createDiscountStrategy(cargo, percentDiscount, bus.getServiceName());

		// //////Discount Dollar end

		// /////////Percent Discount starts here...

		AdvancedPricingRuleIfc[] invoicePercentRulesTotalArray = new AdvancedPricingRuleIfc[invoicePercentRulesTotalList.size()];
		invoicePercentRulesTotalArray = invoicePercentRulesTotalList.toArray(new AdvancedPricingRuleIfc[invoicePercentRulesTotalList
				.size()]);

		int invoiceRulePercentLength = invoicePercentRulesTotalArray.length;

		// This loop is just to get the amount of all the existing invoice
		// rules and to compare if the transaction Amount satisfies any invoice
		// rule
		// criteria.

		for (int i = 0; i < invoiceRulePercentLength; i++) {
			// amount: in invoice rule, to compare with transaction amount
			CurrencyIfc amount = invoicePercentRulesTotalArray[i].getSourceList().getItemThreshold();

			// if transaction value satisfies any of the invoice rule, keep
			// aside all ;
			// the existing invoice rule(s);

			if ((tranBalance.compareTo(amount) >= 0) && !("0.00").equals(amount.getStringValue())) {
				// All eligible rules in a list;
				// and keeps the best discount rate;

				eligibleRuleListPercent.clear();
				eligibleRuleListPercent.add(invoicePercentRulesTotalArray[i]);
				if (invoiceRuleListPercent.size() == 0)
					invoiceRuleListPercent.add(invoicePercentRulesTotalArray[i].getDiscountRate());

				// Compare the new Rate with Old rate;

				BigDecimal disRateCurrent = eligibleRuleListPercent.get(0).getDiscountRate();
				BigDecimal disRatePrevious = invoiceRuleListPercent.get(0);
				if ((disRateCurrent.compareTo(disRatePrevious)) > 0) {
					invoiceRuleListPercent.clear();
					invoiceRuleListPercent.add(invoicePercentRulesTotalArray[i].getDiscountRate());
				}
			}

		}

		if (invoiceRuleListPercent.size() == 1) {
			// If only one rule is there;If no rule is eligible, in that case
			// also it has 0.00 discount;
			//Changes for Rev 1.1 : Starts
			percentVariablePercent = new BigDecimal(invoiceRuleListPercent.get(0).toString());
			//Changes for Rev 1.1 : Ends
		}

		if (percentVariablePercent.toString().length() >= 5) {
			BigDecimal scaleOne = new BigDecimal(1);
			percentVariablePercent = percentVariablePercent.divide(scaleOne, 2, BigDecimal.ROUND_HALF_UP);
		}

		TransactionDiscountByPercentageIfc percentDiscountPercent = DomainGateway.getFactory().getTransactionDiscountByPercentageInstance();
		percentDiscountPercent.setDiscountRate(percentVariablePercent);
		BigDecimal CalculationOfPercent = new BigDecimal("0.00");
		BigDecimal tranBalanceBig = new BigDecimal(tranBalance.getStringValue());
		CalculationOfPercent = tranBalanceBig.multiply(percentVariablePercent);

		cargo.setAccessFunctionID(MAXRoleFunctionIfc.PRICE_DISCOUNT);
		cargo.setDoDiscount(true);
		cargo.setCreateTransaction(createTransaction);

		if ((percentVariableDollar.compareTo(CalculationOfPercent)) > 0) {
			//by kamlesh
			CurrencyIfc discountAmt = DomainGateway.getBaseCurrencyInstance(percentVariableDollar);
			
			//ends
		//	for(int i=0;i<cargo.getTransaction().getLineItemsVector().size();i++)
			//{
				if((cargo.getInvoiceDiscounts().get(0).getDescription().equalsIgnoreCase("Buy$NorMoreGetYatZ$offTiered_BillBuster")) )//&& (cargo.getTransaction().getLineItemsVector().get(i).getItemID().equalsIgnoreCase(billBusterPromo)))
				{
					bus.mail("BillBuster", BusIfc.CURRENT);
				}
				//}
		} else {

		 if((cargo.getInvoiceDiscounts().get(0).getDescription().equalsIgnoreCase("Buy$NorMoreGetYatZ%offTiered_BillBuster")) )//&& (cargo.getTransaction().getLineItemsVector().get(i).getItemID().equalsIgnoreCase(billBusterPromo)))
			{
				bus.mail("BillBusterPct", BusIfc.CURRENT);
			}
			
		}

	}

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

	protected TransactionDiscountByAmountIfc createDiscountStrategy(ModifyTransactionDiscountCargo cargo, CurrencyIfc discount, String serviceName) {
		int reasonInt = MAXRoleFunctionIfc.INVOICE_TYPE_DISCOUNT_PERCENT;

		TransactionDiscountByAmountIfc amountDiscount = DomainGateway.getFactory().getTransactionDiscountByAmountInstance();
		amountDiscount.setDiscountAmount(discount);
		amountDiscount.setReasonCode(reasonInt);
		// amountDiscount.setReasonCodeText(reason);
		return amountDiscount;
	}

}
