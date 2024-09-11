package max.retail.stores.pos.services.modifytransaction.discount;

import java.util.List;

import max.retail.stores.domain.discount.MAXAdvancedPricingRuleIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;

public class MAXBillBusterCalculation {
	
	public static String billBusterCalculation(AdvancedPricingRuleIfc[] invoiceDollarRulesTotalArray) throws Exception 
	{
		int invoiceRuleLength = invoiceDollarRulesTotalArray.length;
		for (int i = 0; i < invoiceRuleLength; i++) {
			// amount: in invoice rule, to compare with transaction amount
			//CurrencyIfc amount = invoiceDollarRulesTotalArray[i].getSourceList().getItemThreshold();
			//String disc = invoiceDollarRulesTotalArray[i].getDescription();
			if(invoiceDollarRulesTotalArray[i].getDescription().equalsIgnoreCase("Buy$NorMoreGetYatZ$offTiered_BillBuster"))
			{
				invoiceDollarRulesTotalArray[i].getDiscountAmount();
			}
		}
		return null;
	}

}
