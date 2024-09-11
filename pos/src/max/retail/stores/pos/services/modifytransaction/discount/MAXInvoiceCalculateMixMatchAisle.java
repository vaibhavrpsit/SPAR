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
import java.util.Arrays;
import java.util.List;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXHotKeysTransaction;
import max.retail.stores.domain.arts.MAXJdbcReadBillBusterPctDetails;
import max.retail.stores.domain.discount.MAXAdvancedPricingRule;
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
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.Bus;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;

public class MAXInvoiceCalculateMixMatchAisle extends PosLaneActionAdapter {

	private static final long serialVersionUID = -8660375205766304135L;
	public static int AVAIL_DISCOUNT_LENGTH = 23;
//	public static CurrencyIfc prev_disc_amt;
	//static int discapplimit;

	public void traverse(BusIfc bus) {

		MAXModifyTransactionDiscountCargo cargo = (MAXModifyTransactionDiscountCargo) bus.getCargo();
		CurrencyIfc tranBalance = DomainGateway.getBaseCurrencyInstance();
		tranBalance = cargo.getTransaction().getTransactionTotals().getDiscountEligibleSubtotal();
		CurrencyIfc disc_amt=cargo.getTransaction().getTransactionTotals().getDiscountTotal();
		String letter = null;
		
		
		//if(disc_amt.toString().equalsIgnoreCase("0.00") && !cargo.getInvoiceRuleAppliedRate().toString().equalsIgnoreCase("0.00") 
				// ){
			
			
		
		
	//	String letter = null;

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
		List <String> targetItems =null;
		CurrencyIfc amount= null;
		

		ArrayList<MAXAdvancedPricingRuleIfc> invoiceRulesAll = cargo.getInvoiceDiscounts();

		for (int i = 0; i < invoiceRulesAll.size(); i++) {
			if (Integer.parseInt(invoiceRulesAll.get(i).getReason().getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZ$offTiered) {
				invoiceDollarRulesTotalList.add(invoiceRulesAll.get(i));
			}
			if (Integer.parseInt(invoiceRulesAll.get(i).getReason().getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZPctoffTiered) {
				invoicePercentRulesTotalList.add(invoiceRulesAll.get(i));
			}
			if (Integer.parseInt(invoiceRulesAll.get(i).getReason().getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZ$offTiered_BillBuster) {
				invoiceDollarRulesTotalList.add(invoiceRulesAll.get(i));
				 amount = invoicePercentRulesTotalList.get(i).getSourceList().getItemThreshold();
				if((tranBalance.compareTo(amount) >= 0) && !("0.00").equals(amount.getStringValue()))
				{
				
					targetItems = invoiceDollarRulesTotalList.get(i).getItemList();
					eligibleRuleListDollar.clear();
					eligibleRuleListDollar.add(invoiceDollarRulesTotalList.get(i));
					if (invoiceRuleListDollar.size() == 0)
						invoiceRuleListDollar.add(invoiceDollarRulesTotalList.get(i).getDiscountAmount());
					
					(eligibleRuleListDollar.get(0).getDiscountAmount()).getStringValue();
					BigDecimal disRateCurrent = new BigDecimal(eligibleRuleListDollar.get(0).getDiscountAmount().getStringValue());
					BigDecimal disRatePrevious = new BigDecimal((invoiceRuleListDollar.get(0)).toString());
					if ((disRateCurrent.compareTo(disRatePrevious)) > 0) {
						invoiceRuleListDollar.clear();
						invoiceRuleListDollar.add(invoiceDollarRulesTotalList.get(i).getDiscountAmount());
					}

					
			}
			if (Integer.parseInt(invoiceRulesAll.get(i).getReason().getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZPctoffTiered_BillBuster) {
				invoicePercentRulesTotalList.add(invoiceRulesAll.get(i));
				 amount = invoicePercentRulesTotalList.get(i).getSourceList().getItemThreshold();
					if((tranBalance.compareTo(amount) >= 0) && !("0.00").equals(amount.getStringValue()))
					{
					
						targetItems = invoiceDollarRulesTotalList.get(i).getItemList();
						
						eligibleRuleListPercent.clear();
						eligibleRuleListPercent.add(invoiceDollarRulesTotalList.get(i));
						if (invoiceRuleListPercent.size() == 0)
							invoiceRuleListPercent.add(invoiceDollarRulesTotalList.get(i).getDiscountRate());

						// Compare the new Rate with Old rate;

						BigDecimal disRateCurrent = eligibleRuleListPercent.get(0).getDiscountRate();
						BigDecimal disRatePrevious = invoiceRuleListPercent.get(0);

						if ((disRateCurrent.compareTo(disRatePrevious)) > 0) {
							invoiceRuleListPercent.clear();
							invoiceRuleListPercent.add(invoiceDollarRulesTotalList.get(i).getDiscountRate());
						}
						
				}
			}
		}
		
	
		

		if (invoiceDollarRulesTotalList.size() == 1) {
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

		//CurrencyIfc percentDiscount = DomainGateway.getBaseCurrencyInstance(String.valueOf(percentVariableDollar));
		// percentDiscount.setDiscountRate(percent);

		//TransactionDiscountByAmountIfc amountDiscount = createDiscountStrategy(cargo, percentDiscount, bus.getServiceName());
		TransactionDiscountByAmountIfc amountDiscount = DomainGateway.getFactory().getTransactionDiscountByAmountInstance();
	//	TransactionDiscountByPercentageIfc percentDiscountPercent = DomainGateway.getFactory().getTransactionDiscountByPercentageInstance();
		// //////Discount Dollar end

		// /////////Percent Discount starts here...

	//	AdvancedPricingRuleIfc[] invoicePercentRulesTotalArray = new AdvancedPricingRuleIfc[invoicePercentRulesTotalList.size()];
		//invoicePercentRulesTotalArray = invoicePercentRulesTotalList.toArray(new AdvancedPricingRuleIfc[invoicePercentRulesTotalList
				//.size()]);

		int invoiceRulePercentLength = invoiceRuleListPercent.size();

		// This loop is just to get the amount of all the existing invoice
		// rules and to compare if the transaction Amount satisfies any invoice
		// rule
		// criteria.

		
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

		//for (int j = 0; j < invoiceRulesAll.size(); j++)
		if(!(cargo.getInvoiceDiscounts().get(0).getDescription().equalsIgnoreCase("Buy$NorMoreGetYatZ$offTiered_BillBuster")) && !(cargo.getInvoiceDiscounts().get(0).getDescription().equalsIgnoreCase("Buy$NorMoreGetYatZ%offTiered_BillBuster")))
		{
			if ((percentVariableDollar.compareTo(CalculationOfPercent)) > 0) {
			
			CurrencyIfc discountAmt = DomainGateway.getBaseCurrencyInstance(percentVariableDollar);
			
			
					cargo.setDiscount(amountDiscount);
					amountDiscount.setReasonCode(MAXRoleFunctionIfc.INVOICE_TYPE_DISCOUNT_DOLLAR);
					cargo.setDiscountType(MAXRoleFunctionIfc.INVOICE_TYPE_DISCOUNT_DOLLAR);
					bus.mail("AfterInvoiceDollar", BusIfc.CURRENT);
							//}
		} 		 
		 else {
			 	cargo.setDiscount(percentDiscountPercent);
				percentDiscountPercent.setReasonCode(MAXRoleFunctionIfc.INVOICE_TYPE_DISCOUNT_PERCENT);
				cargo.setDiscountType(MAXRoleFunctionIfc.INVOICE_TYPE_DISCOUNT_PERCENT);
				bus.mail("AfterInvoicePercent", BusIfc.CURRENT);
		 }
			
		}
		
		else
		{
			
			for (int j = 0; j < invoiceRulesAll.size(); j++)
			{
				if(!(letter!=null))
				{
				if((cargo.getInvoiceDiscounts().get(j).getDescription().equalsIgnoreCase("Buy$NorMoreGetYatZ$offTiered_BillBuster")))
					
					if((tranBalance.compareTo(amount) >= 0) && !("0.00").equals(amount.getStringValue()))
					{
					
					targetItems = cargo.getInvoiceDiscounts().get(j).getItemList();
					for (int k=0;k<cargo.getTransaction().getLineItemsVector().size();k++)
					{
						String itemid = cargo.getTransaction().getLineItemsVector().get(k).getItemID();
						for (int m=0 ; m>targetItems.size();m++) {
							if( itemid.equalsIgnoreCase(targetItems.get(m))){
								invoiceDollarRulesTotalList.get(j).setDiscountAmount(cargo.getInvoiceDiscounts().get(j).getDiscountAmount());
							
								letter = "BillBuster";
								break;
							}
						//	invoiceDollarRulesTotalList.get(i).setTargetItemId(billBusterPromo);
						//	invoiceDollarRulesTotalList.get(i).setDiscountAmount(cargo.getInvoiceDiscounts().get(j).getDiscountAmount());
							//invoiceDollarRulesTotalList.get(j).setTargetItemId(billBusterPromo);
							//invoiceDollarRulesTotalList.get(j).setDiscountAmount(cargo.getInvoiceDiscounts().get(j).getDiscountAmount());
							//invoic
							//invoiceRulesAll.add(0, invoiceDollarRulesTotalList.get(j));
							
						}
						
					}}
					
					//bus.mail("BillBuster", BusIfc.CURRENT);
				}
				if((cargo.getInvoiceDiscounts().get(j).getDescription().equalsIgnoreCase("Buy$NorMoreGetYatZ%offTiered_BillBuster")))
				{
					for (int k=0;k<cargo.getTransaction().getLineItemsVector().size();k++)
					{
						String itemid = cargo.getTransaction().getLineItemsVector().get(k).getItemID();
						for (int m=0  ; m>targetItems.size();m++) {
							if( itemid.equalsIgnoreCase(targetItems.get(m))){
								//invoicePercentRulesTotalList.get(j).setDiscountAmount(cargo.getInvoiceDiscounts().get(j).getDiscountAmount());
								invoicePercentRulesTotalList.get(j).setDiscountRate(cargo.getInvoiceDiscounts().get(j).getDiscountRate());
							letter = "BillBusterPct";
							break;
						}
					}	
			}
		}
				else {
					break;
				}
			}
		/*}
		}else {
			cargo.setInvoiceRuleAlreadyApplied(true);
		}*/
		
			if(letter!=null)
			{
				bus.mail(letter);
			}
			
			else
			{
				bus.mail("AfterInvoicePercent", BusIfc.CURRENT);
				
				
				
			}
		
			
			
		}}
		
		
	
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
		int reasonInt = MAXRoleFunctionIfc.INVOICE_TYPE_DISCOUNT_DOLLAR;

		TransactionDiscountByAmountIfc amountDiscount = DomainGateway.getFactory().getTransactionDiscountByAmountInstance();
		amountDiscount.setDiscountAmount(discount);
		amountDiscount.setReasonCode(reasonInt);
		// amountDiscount.setReasonCodeText(reason);
		return amountDiscount;
	}

}
