/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.

 *  Rev 1.4     Sep 06, 2022		Kamlesh Pant	CapLimit Enforcement for Liquor	
 *  Rev 1.3     May 04, 2017		Kritica Agarwal GST Changes
 *	Rev 1.2		Dec 20, 2016		Mansi Goel		Changes for Gift Card FES
 *	Rev 1.1		Nov 07, 2016		Mansi Goel		Changes for Discount Rule FES
 *	Rev	1.0 	Aug 16, 2016		Nitesh Kumar	Changes for Code Merging	
 *
 ********************************************************************************/

package max.retail.stores.domain.stock;

import java.util.ArrayList;
import java.util.List;

import max.retail.stores.domain.discount.MAXAdvancedPricingRuleIfc;
import max.retail.stores.domain.lineitem.MAXMaximumRetailPriceChangeIfc;
import max.retail.stores.domain.tax.MAXTaxAssignmentIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;

public interface MAXPLUItemIfc extends PLUItemIfc {

	public List getItemExclusionGroupList();

	public void setItemExclusionGroupList(List itemExclusionGroupList);

	public void setWeightedBarCode(boolean isWeightedBarCode);

	public boolean IsWeightedBarCode();

	public String getItemGroup();

	public void setItemGroup(String itemGroup);

	public String getItemDivision();

	public void setItemDivision(String itemDivision);

	public void setSubClass(String merchandiseHierarchyGroupId);

	public String getSubClass();

	public void setItemDepartmentId(String deptid);

	public String getItemDepartmentId();

	public void setItemClassId(String classId);

	public String getClassId();

	public void setBrandName(String brandName);

	public String getBrandName();

	public void setRetailLessThanMRPFlag(boolean equals);

	public int getTaxCategory();
	
	public void setTaxCategory(int taxCategory);

	public void setMultipleMaximumRetailPriceFlag(boolean equals);

	public boolean getRetailLessThanMRPFlag();

	// Changes starts for code merging(adding below method as it is not present
	// in base 14)
	public boolean getMultipleMaximumRetailPriceFlag();

	public void setMaximumRetailPrice(CurrencyIfc value);

	public CurrencyIfc getMaximumRetailPrice();

	public void addTaxAssignments(MAXTaxAssignmentIfc[] taxAssignment);

	public void addTaxAssignment(MAXTaxAssignmentIfc taxAssignment);

	public void setTaxAssignments(MAXTaxAssignmentIfc[] taxAssignment);

	public MAXTaxAssignmentIfc[] getTaxAssignments();

	public void setInActiveMaximumRetailPriceChanges(MAXMaximumRetailPriceChangeIfc[] changes);

	public void setInactivePriceChangesMaximumRetailPrice(List priceChangesMaximumRetailPrice);

	public void setActivePriceChangesMaximumRetailPrice(List priceChangesMaximumRetailPrice);

	public MAXMaximumRetailPriceChangeIfc[] getActiveMaximumRetailPriceChanges();

	public MAXMaximumRetailPriceChangeIfc[] getInActiveMaximumRetailPriceChanges();

	public boolean hasInActiveMaximumRetailPriceChanges();

	public void setActiveMaximumRetailPriceChanges(MAXMaximumRetailPriceChangeIfc[] changes);

	// Changes ends for code merging

	// Changes for Rev 1.1 : Starts
	public String getItemGroups();

	public void setItemGroups(String itemGroups);

	public ArrayList<MAXAdvancedPricingRuleIfc> getInvoiceDiscounts();

	public void setInvoiceDiscounts(ArrayList<MAXAdvancedPricingRuleIfc> invoiceDiscounts);

	// Changes for Rev 1.1 : Ends
	
	// Changes for Rev 1.2 : Starts
	public String getItemSizeDesc();

	public void setItemSizeDesc(String itemSize);
	// Changes for Rev 1.2 : Ends
	// Changes for Rev 1.3 : Starts
	public String getHsnNum();
	public void setHsnNum(String hsnNum);
	// Changes for Rev 1.3 : Ends
	
	public void setliquom(String liquom);
	public String getliquom();
	
	public void setliqcat(String liqcat);
	public String getliqcat();
	
	
	public boolean isEdgeItem();
	public void setEdgeItem(boolean edgeItem);
	
	// Changes for Rev 1.4 : Starts
	//public float getLiqQuantity();
	//public void setLiqQuantity(float liqQuantity);
	
	// Changes for Rev 1.4 : Ends
}
