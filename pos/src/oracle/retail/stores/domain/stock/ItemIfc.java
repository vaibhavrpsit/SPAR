package oracle.retail.stores.domain.stock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

public interface ItemIfc extends EYSDomainIfc {
  public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
  
  CurrencyIfc getCompareAtPrice();
  
  boolean getDamageDiscountEligible();
  
  DepartmentIfc getDepartment();
  
  String getDescription(Locale paramLocale);
  
  LocalizedTextIfc getLocalizedDescriptions();
  
  boolean getDiscountEligible();
  
  boolean getEmployeeDiscountEligible();
  
  void setEmployeeDiscountEligible(boolean employeeDiscountEligible);
  
  ItemClassificationIfc getItemClassification();
  
  CurrencyIfc getItemCost();
  
  String getItemID();
  
  BigDecimal getItemWeight();
  
  String getManufacturer(Locale paramLocale);
  
  LocalizedTextIfc getLocalizedManufacturer();
  
  CurrencyIfc getPermanentPrice();
  
  String[] getPlanogramID();
  
  String getPosItemID();
  
  int getPricingEventID();
  
  ProductIfc getProduct();
  
  String getProductGroupID();
  
  int getRestrictiveAge();
  
  CurrencyIfc getSellingPrice();
  
  String getShortDescription(Locale paramLocale);
  
  LocalizedTextIfc getShortLocalizedDescriptions();
  
  boolean getTaxable();
  
  int getTaxGroupID();
  
  UnitOfMeasureIfc getUnitOfMeasure();
  
  boolean isEmployeeDiscountEligible();
  
  boolean isDamageDiscountEligible();
  
  boolean isDiscountEligible();
  
  boolean isItemSizeRequired();
  
  boolean isKitComponent();
  
  boolean isKitHeader();
  
  boolean isSpecialOrderEligible();
  
  boolean isStoreCoupon();
  
  void setCompareAtPrice(CurrencyIfc paramCurrencyIfc);
  
  void setDepartment(DepartmentIfc paramDepartmentIfc);
  
  void setDescription(Locale paramLocale, String paramString);
  
  void setLocalizedDescriptions(LocalizedTextIfc paramLocalizedTextIfc);
  
  void setItemClassification(ItemClassificationIfc paramItemClassificationIfc);
  
  void setItemCost(CurrencyIfc paramCurrencyIfc);
  
  void setItemID(String paramString);
  
  void setItemSizeRequired(boolean paramBoolean);
  
  void setItemWeight(BigDecimal paramBigDecimal);
  
  void setManufacturer(Locale paramLocale, String paramString);
  
  void setLocalizedManufacturer(LocalizedTextIfc paramLocalizedTextIfc);
  
  void setPermanentPrice(CurrencyIfc paramCurrencyIfc);
  
  void setPlanogramID(String[] paramArrayOfString);
  
  void setPosItemID(String paramString);
  
  void setPricingEventID(int paramInt);
  
  void setProduct(ProductIfc paramProductIfc);
  
  void setRestrictiveAge(int paramInt);
  
  void setSellingPrice(CurrencyIfc paramCurrencyIfc);
  
  void setShortDescription(Locale paramLocale, String paramString);
  
  void setShortLocalizedDescriptions(LocalizedTextIfc paramLocalizedTextIfc);
  
  void setTaxable(boolean paramBoolean);
  
  void setTaxGroupID(int paramInt);
  
  void setUnitOfMeasure(UnitOfMeasureIfc paramUnitOfMeasureIfc);
  
  ItemImageIfc getItemImage();
  
  void setItemImage(ItemImageIfc paramItemImageIfc);
  
  void setAllItemLevelMessages(Map<String, List<MessageDTO>> paramMap);
  
  Map<String, List<MessageDTO>> getAllItemLevelMessages();
  
  List<MessageDTO> getAllItemMessagesInTransaction(String paramString);
  
  String getItemLevelMessage(String paramString1, String paramString2);
  
  String getItemLevelMessage(String paramString1, String paramString2, Locale paramLocale);
  
  String getItemLevelMessageCodeID(String paramString1, String paramString2);
}
