package oracle.retail.stores.domain.stock;

import java.util.HashMap;
import java.util.Iterator;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderItemIfc;
import oracle.retail.stores.common.item.ExtendedItemDataContainer;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.utility.EYSDate;

public interface PLUItemIfc extends ItemIfc {
  public static final String revisionNumber = "$Revision: /main/37 $";
  
  void addAdvancedPricingRule(AdvancedPricingRuleIfc paramAdvancedPricingRuleIfc);
  
  void addAdvancedPricingRules(AdvancedPricingRuleIfc[] paramArrayOfAdvancedPricingRuleIfc);
  
  void addPermanentPriceChange(PriceChangeIfc paramPriceChangeIfc);
  
  void addPermanentPriceChanges(PriceChangeIfc[] paramArrayOfPriceChangeIfc);
  
  void addTemporaryPriceChange(PriceChangeIfc paramPriceChangeIfc);
  
  void addTemporaryPriceChangeForReturns(PriceChangeIfc paramPriceChangeIfc);
  
  void addClearancePriceChangeForReturns(PriceChangeIfc paramPriceChangeIfc);
  
  void addTemporaryPriceChanges(PriceChangeIfc[] paramArrayOfPriceChangeIfc);
  
  void addTaxRule(TaxRuleIfc paramTaxRuleIfc);
  
  void addTaxRules(TaxRuleIfc[] paramArrayOfTaxRuleIfc);
  
  Iterator<AdvancedPricingRuleIfc> advancedPricingRules();
  
  void clearAdvancedPricingRules();
  
  void clearPermanentPriceChanges();
  
  void clearTemporaryPriceChanges();
  
  void clearTaxRules();
  
  void addAvailableItemSize(ItemSizeIfc paramItemSizeIfc);
  
  ItemSizeIfc[] getAvailableItemSizes();
  
  AdvancedPricingRuleIfc[] getAdvancedPricingRules();
  
  CurrencyIfc getCompareAtPrice();
  
  String getDepartmentID();
  
  ItemIfc getItem();
  
  int getManufacturerID();
  
  String getManufacturerItemUPC();
  
  String getMerchandiseCodesString();
  
  CurrencyIfc getPermanentPrice(EYSDate paramEYSDate);
  
  PriceChangeIfc[] getPermanentPriceChanges();
  
  String getPosItemID();
  
  CurrencyIfc getPrice();
  
  CurrencyIfc getPrice(EYSDate paramEYSDate, int paramInt);
  
  PriceChangeIfc[] getTemporaryPriceChanges();
  
  PriceChangeIfc[] getClearancePriceChanges();
  
  PriceChangeIfc getEffectiveTemporaryPriceChange();
  
  PriceChangeIfc getEffectiveTemporaryPriceChange(EYSDate paramEYSDate);
  
  PriceChangeIfc getEffectiveTemporaryPriceChange(EYSDate paramEYSDate, int paramInt);
  
  PriceChangeIfc getEffectiveClearancePriceChange();
  
  PriceChangeIfc getEffectiveClearancePriceChange(EYSDate paramEYSDate);
  
  PriceChangeIfc getEffectiveClearancePriceChange(EYSDate paramEYSDate, int paramInt);
  
  HashMap<String, RelatedItemGroupIfc> getRelatedItemContainer();
  
  StockItemIfc getStockItem();
  
  String getStoreID();
  
  String getTaxGroupName();
  
  TaxRuleIfc[] getTaxRules();
  
  boolean hasAdvancedPricingRules();
  
  boolean hasRelatedItems();
  
  boolean hasPermanentPriceChanges();
  
  boolean hasTemporaryPriceChanges();
  
  boolean hasClearancePriceChanges();
  
  boolean hasTaxRules();
  
  boolean isAlterationItem();
  
  boolean isSerializedItem();
  
  Iterator<PriceChangeIfc> priceChangesPermanent();
  
  Iterator<PriceChangeIfc> priceChangesTemporary();
  
  Iterator<PriceChangeIfc> priceChangesClearance();
  
  void setAdvancedPricingRules(AdvancedPricingRuleIfc[] paramArrayOfAdvancedPricingRuleIfc);
  
  void setCloneAttributes(PLUItem paramPLUItem);
  
  void setCompareAtPrice(CurrencyIfc paramCurrencyIfc);
  
  void setDepartmentID(String paramString);
  
  void setDiscountEligible(boolean paramBoolean);
  
  void setItem(ItemIfc paramItemIfc);
  
  void setManufacturerID(int paramInt);
  
  void setManufacturerItemUPC(String paramString);
  
  void setPosItemID(String paramString);
  
  void setPrice(CurrencyIfc paramCurrencyIfc);
  
  void setPermanentPriceChanges(PriceChangeIfc[] paramArrayOfPriceChangeIfc);
  
  void setTemporaryPriceChanges(PriceChangeIfc[] paramArrayOfPriceChangeIfc);
  
  void setClearancePriceChanges(PriceChangeIfc[] paramArrayOfPriceChangeIfc);
  
  void setRelatedItemContainer(HashMap<String, RelatedItemGroupIfc> paramHashMap);
  
  void setStoreID(String paramString);
  
  void setTaxGroupName(String paramString);
  
  void setTaxRules(TaxRuleIfc[] paramArrayOfTaxRuleIfc);
  
  Iterator<TaxRuleIfc> taxRules();
  
  int getReturnPriceDays();
  
  void setReturnPriceDays(int paramInt);
  
  void setTemporaryPriceChangesAndTemporaryPriceChangesForReturns(PriceChangeIfc[] paramArrayOfPriceChangeIfc);
  
  void setClearancePriceChangesAndClearancePriceChangesForReturns(PriceChangeIfc[] paramArrayOfPriceChangeIfc);
  
  CurrencyIfc getReturnPrice(int paramInt);
  
  ExternalOrderItemIfc getReturnExternalOrderItem();
  
  void setReturnExternalOrderItem(ExternalOrderItemIfc paramExternalOrderItemIfc);
  
  boolean isOnClearance();
  
  void setOnClearance(boolean paramBoolean);
  
  boolean isAvailableInCurrentStore();
  
  void setAvailableInCurrentStore(boolean paramBoolean);
  
  PriceChangeIfc getEffectivePromotionalPrice();
  
  PriceChangeIfc getEffectivePromotionalPrice(EYSDate paramEYSDate);
  
  PriceChangeIfc getEffectivePromotionalPrice(EYSDate paramEYSDate, int paramInt);
  
  ExtendedItemDataContainer getExtendedItemDataContainer();
  
  void setExtendedItemDataContainer(ExtendedItemDataContainer paramExtendedItemDataContainer);
  
  void setExtendedImageOnItem(ExtendedItemDataContainer paramExtendedItemDataContainer);
  
  void consolidate(PLUItemIfc paramPLUItemIfc);
  
	/*
	 * public String getliqcat(); public void setliqcat( String liqcat);
	 * 
	 * public String getliquom(); public void setliquom( String liquom);
	 */
   public boolean getEmpID();
   public void setEmpID(boolean empID);
	
   public String getSpclEmpDisc();
   public void setSpclEmpDisc(String SpclEmpDisc);

void setEmployeeDiscountEligible(boolean value);
}
