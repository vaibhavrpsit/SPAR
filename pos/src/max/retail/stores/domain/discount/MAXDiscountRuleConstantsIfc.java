/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Nov 07, 2016		Mansi Goel		Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.domain.discount;

import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;

public interface MAXDiscountRuleConstantsIfc extends DiscountRuleConstantsIfc {

	public static final String COMPARISION_BASIS = "ComparisonBasis";
	public static final String CRITERION = "Criterion";
	public static final String PLUITEM_ID = "PLUITEMID";

	//Changes for Rev 1.0 : Starts
	public static final int DISCOUNT_REASON_Buy$NofXatZPctoffTiered = 16;
	public static final int DISCOUNT_REASON_Buy$NofXatZ$offTiered = 17;
	public static final int DISCOUNT_REASON_BuyNofXatZPctoffTiered = 26;
	public static final int DISCOUNT_REASON_BuyNofXatZ$offTiered = 27;
	public static final int DISCOUNT_REASON_BuyNofXforZ$Tiered = 28;
	public static final int DISCOUNT_REASON_Buy$NatZPctoffTiered = 19;
	public static final int DISCOUNT_REASON_Buy$NatZ$offTiered = 20;
	public static final int DISCOUNT_REASON_BuyNOrMoreOfXGetatUnitPriceTiered = 22;
	
	//Changes for Rev 1.0 : Ends
	
	public static final String DISCOUNT_DESCRIPTION_BuyNofXgetYatZPctoff = "BuyNofXgetYatZ%off";
	public static final String DISCOUNT_DESCRIPTION_BuyNofXgetYatZ$off = "BuyNofXgetYatZ$off";
	public static final String DISCOUNT_DESCRIPTION_BuyNofXgetYatZ$ = "BuyNofXgetYatZ$";
	public static final String DISCOUNT_DESCRIPTION_BuyNofXforZPctoff = "BuyNofXforZ%off";
	public static final String DISCOUNT_DESCRIPTION_BuyNofXforZ$off = "BuyNofXforZ$off";
	public static final String DISCOUNT_DESCRIPTION_BuyNofXforZ$ = "BuyNofXforZ$";
	public static final String DISCOUNT_DESCRIPTION_BuyNofXgetLowestPricedXatZPctoff = "BuyNofXgetLowestPricedXatZ%off";
	public static final String DISCOUNT_DESCRIPTION_Buy$NorMoreOfXgetYatZPctoff = "Buy$NorMoreOfXgetYatZ%off";
	public static final String DISCOUNT_DESCRIPTION_Buy$NorMoreOfXgetYatZ$off = "Buy$NorMoreOfXgetYatZ$off";
	public static final String DISCOUNT_DESCRIPTION_Buy$NorMoreOfXgetYatZ$ = "Buy$NorMoreOfXgetYatZ$";
	public static final String DISCOUNT_DESCRIPTION_BuyNofXgetHighestPricedXatZPctoff = "BuyNofXgetHighestPricedXatZ%off";

	public static final String DISCOUNT_DESCRIPTION_BuyNofXatZPctoffTiered = "BuyNofXatZ%offTiered";
	public static final String DISCOUNT_DESCRIPTION_BuyNofXatZ$offTiered = "BuyNofXatZ$offTiered";
	public static final String DISCOUNT_DESCRIPTION_Buy$NofXatZPctoffTiered = "Buy$NofXatZ%offTiered";
	public static final String DISCOUNT_DESCRIPTION_Buy$NofXatZ$offTiered = "Buy$NofXatZ$offTiered";
	public static final String DISCOUNT_DESCRIPTION_BuyRsNofXgetCoupforRsZ = "BuyRsNofXgetCoupforRsZ";
	public static final String DISCOUNT_DESCRIPTION_BuyXforZPctoffAtHappyHours = "BuyXforZ%offAtHappyHours";
	public static final String DISCOUNT_DESCRIPTION_BuyXforZ$offAtHappyHours = "BuyXforZ$offAtHappyHours";
	public static final String DISCOUNT_DESCRIPTION_BuyRsNgetCoupforRsZ = "BuyRsNgetCoupforRsZ";

	public static final String DISCOUNT_DESCRIPTION_BuyXatRsZAtHappyHours = "BuyXatRsZAtHappyHours";
	public static final String DISCOUNT_DESCRIPTION_BuyNofXgetMofXwithLowestPriceatZPctoff = "BuyNofXgetMofXwithLowestPriceatZ%off";

	public static final int COMPARISON_BASIS_BRAND = 6;
	public static final int COMPARISON_BASIS_ITEM_GROUP = 7;
	public static final int DISCOUNT_REASON_Buy$NofXgetYFree = 29;
	public static final int COMPARISON_BASIS_SUBCLASS = 4;
	public static final int COMPARISON_BASIS_DEPARTMENT = 1;
	public static final String joinCondition = " " + ":" + " ";
	public static final int DISCOUNT_METHOD_WEIGHTED_ITEM = 4;
	// added for capillary Coupon discount
	public static final int ASSIGNMENT_CAPILLARYCOUPON = 6;
	// end for capillary coupon discount
	
	public static final String DISCOUNT_DESCRIPTION_SPECLEMPDISC = "SpecEmpDisc";
	
	public static final String DISCOUNT_DESCRIPTION_Buy$NorMoreGetYatZPctoffTiered_BillBuster = "Buy$NorMoreGetYatZ%offTiered_BillBuster";
	public static final String DISCOUNT_DESCRIPTION_Buy$NorMoreGetYatZ$offTiered_BillBuster = "Buy$NorMoreGetYatZ$offTiered_BillBuster";
	public static final int DISCOUNT_REASON_Buy$NorMoreGetYatZ$offTiered_BillBuster= 34;
	public static final int DISCOUNT_REASON_Buy$NorMoreGetYatZPctoffTiered_BillBuster = 33;
	
}
