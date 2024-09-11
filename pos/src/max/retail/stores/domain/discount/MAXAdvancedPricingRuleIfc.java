/********************************************************************************
*   
*	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*	
*
*	Rev	1.1 	Nov 22, 2016    Ashish Yadav		Changes for Employee Discount FES
*	Rev	1.0 	Nov 7, 2016		Mansi Goel			Changes for Discount Rule FES
*
********************************************************************************/

package max.retail.stores.domain.discount;

//java imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.BestDealGroupIfc;


public interface MAXAdvancedPricingRuleIfc extends AdvancedPricingRuleIfc {
	
	public BestDealGroupIfc findBestDealGroupForRule(ArrayList sources, ArrayList targets, int groupLimit);

	public CurrencyIfc getDiscountAmountOnMLowestPricedItem();

	public void setmValue(int mValue);

	public int getmValue();

	public void setDescription(String value);

	public String getDescription();
	
	public HashMap getCapillaryCoupon();

	public void setCapillaryCoupon(HashMap capillaryCoupon);
	
	// Changes start for Rev 1.1 (Ashish : Employee Discount)
	public String getCustomerType();
	
	public void setCustomerType(String customerType) ;
	// Changes ends for Rev 1.1 (Ashish : Employee Discount)
	
	//Changes by Kamlesh
	public String getTargetItemId();
	
	public String setTargetItemId(String targetItemId) ;
	
public List<String> getItemList();
	
	public List<String> setItemList(List<String> itemList) ;
}
