package oracle.retail.stores.domain.stock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.store.DepartmentIfc;

public class Item implements ItemIfc {
  public static final String revisionNumber = "$Revision: /main/23 $";
  
  static final long serialVersionUID = 8484560095662637080L;
  
  protected LocalizedTextIfc localizedManufacturer = DomainGateway.getFactory().getLocalizedText();
  
  protected CurrencyIfc sellingPrice;
  
  protected CurrencyIfc permanentPrice;
  
  protected CurrencyIfc itemCost;
  
  protected CurrencyIfc compareAtPrice;
  
  protected DepartmentIfc department;
  
  protected UnitOfMeasureIfc unitOfMeasure;
  
  protected ItemClassificationIfc itemClassification;
  
  protected ProductIfc product;
  
  protected BigDecimal itemWeight = BigDecimal.ZERO;
  
  protected String itemID = "";
  
  protected LocalizedTextIfc descriptions = null;
  
  protected LocalizedTextIfc shortDescriptions = null;
  
  protected String manufacturer = "";
  
  protected String posItemID = "";
  
  protected String[] planogramID;
  
  protected boolean itemSizeRequired;
  
  protected boolean taxable = true;
  
  protected int pricingEventID = -1;
  
  protected int taxGroupID;
  
  protected int restrictiveAge;
  
  protected Map<String, List<MessageDTO>> itemMessageCollectionMap;
  
  protected ItemImageIfc itemImage;
  
  boolean EmployeeDiscountAllowedFlag = true;
  
  public Item() {
    initialize();
  }
  
  public Object clone() {
    Item newItem = new Item();
    setCloneAttributes(newItem);
    return newItem;
  }
  
  public boolean equals(Object obj) {
    boolean result = (this == obj);
    if (!result && obj instanceof Item) {
      ItemIfc item = (Item)obj;
      if (Util.isObjectEqual(getItemID(), item.getItemID()) && Util.isObjectEqual(getLocalizedDescriptions(), item.getLocalizedDescriptions()) && Util.isObjectEqual(getShortLocalizedDescriptions(), item.getShortLocalizedDescriptions()) && Util.isObjectEqual(getSellingPrice(), item.getSellingPrice()) && Util.isObjectEqual(getPermanentPrice(), item.getPermanentPrice()) && Util.isObjectEqual(getItemCost(), item.getItemCost()) && getPricingEventID() == item.getPricingEventID() && getTaxGroupID() == item.getTaxGroupID() && getTaxable() == item.getTaxable() && Util.isObjectEqual(getDepartment(), item.getDepartment()) && Util.isObjectEqual(getItemClassification(), item.getItemClassification()) && Util.isObjectEqual(getProduct(), item.getProduct()) && Util.isObjectEqual(getPosItemID(), item.getPosItemID()) && Util.isObjectEqual(getCompareAtPrice(), item.getCompareAtPrice()) && Util.isObjectEqual(getItemWeight(), item.getItemWeight()))
        result = true; 
    } 
    return result;
  }
  
  public CurrencyIfc getCompareAtPrice() {
    return this.compareAtPrice;
  }
  
  public boolean getDamageDiscountEligible() {
    return getItemClassification().isDamageDiscountEligible();
  }
  
  public DepartmentIfc getDepartment() {
    return this.department;
  }
  
  public String getDescription(Locale locale) {
    return this.descriptions.getText(LocaleMap.getBestMatch(locale));
  }
  
  public LocalizedTextIfc getLocalizedDescriptions() {
    return this.descriptions;
  }
  
  public String getDescription() {
    return getDescription(LocaleMap.getLocale("locale_Default"));
  }
  
  public void setAllItemLevelMessages(Map<String, List<MessageDTO>> itmMessageCollectionMap) {
    this.itemMessageCollectionMap = itmMessageCollectionMap;
  }
  
  public Map<String, List<MessageDTO>> getAllItemLevelMessages() {
    return this.itemMessageCollectionMap;
  }
  
  public List<MessageDTO> getAllItemMessagesInTransaction(String transactionName) {
    if (this.itemMessageCollectionMap != null)
      return this.itemMessageCollectionMap.get(transactionName); 
    return null;
  }
  
  public String getItemLevelMessage(String trnName, String msgTypName) {
    return getItemLevelMessage(trnName, msgTypName, null);
  }
  
  public String getItemLevelMessage(String trnName, String msgTypName, Locale localeKey) {
    List<MessageDTO> messageList = this.itemMessageCollectionMap.get(trnName);
    String itemLvlMsg = null;
    MessageDTO msgBean = null;
    if (messageList != null)
      for (int msgCnt = messageList.size() - 1; msgCnt >= 0; msgCnt--) {
        msgBean = messageList.get(msgCnt);
        if (msgBean != null && msgBean.getItemMessageType().equalsIgnoreCase(msgTypName))
          if ("4".equals(msgTypName) || !msgBean.isDuplicate()) {
            itemLvlMsg = (localeKey != null) ? msgBean.getLocalizedItemMessage(LocaleMap.getBestMatch(localeKey)) : msgBean.getDefaultItemMessage();
            break;
          }  
      }  
    if (itemLvlMsg == null || itemLvlMsg.equalsIgnoreCase("null"))
      itemLvlMsg = ""; 
    return itemLvlMsg;
  }
  
  public String getItemLevelMessageCodeID(String trnName, String msgTypName) {
    String itemLvlMsgID = null;
    MessageDTO msgBean = null;
    if (this.itemMessageCollectionMap != null) {
      List<MessageDTO> messageList = this.itemMessageCollectionMap.get(trnName);
      if (messageList != null)
        for (int msgCnt = 0; msgCnt < messageList.size(); msgCnt++) {
          msgBean = messageList.get(msgCnt);
          if (msgBean != null && msgBean.getItemMessageType().equals(msgTypName))
            if (!msgBean.isDuplicate()) {
              itemLvlMsgID = msgBean.getItemMessageCodeID();
            } else {
              itemLvlMsgID = null;
            }  
        }  
    } 
    if (itemLvlMsgID == null || itemLvlMsgID.equalsIgnoreCase("null"))
      itemLvlMsgID = ""; 
    return itemLvlMsgID;
  }
  
  public boolean getDiscountEligible() {
    return getItemClassification().isDiscountEligible();
  }
  
  public boolean getEmployeeDiscountEligible() {
    return getItemClassification().getEmployeeDiscountAllowedFlag();
  }
  
  public ItemClassificationIfc getItemClassification() {
    return this.itemClassification;
  }
  
  public CurrencyIfc getItemCost() {
    return this.itemCost;
  }
  
  public String getItemID() {
    return this.itemID;
  }
  
  public BigDecimal getItemWeight() {
    return this.itemWeight;
  }
  
  public ItemImageIfc getItemImage() {
    return this.itemImage;
  }
  
  public void setItemImage(ItemImageIfc itemImage) {
    this.itemImage = itemImage;
  }
  
  public String getManufacturer() {
    return getManufacturer(LocaleMap.getLocale("locale_Default"));
  }
  
  public String getManufacturer(Locale lcl) {
    Locale bestMatch = LocaleMap.getBestMatch(lcl);
    return getLocalizedManufacturer().getText(bestMatch);
  }
  
  public CurrencyIfc getPermanentPrice() {
    return this.permanentPrice;
  }
  
  public String[] getPlanogramID() {
    return this.planogramID;
  }
  
  public String getPosItemID() {
    return this.posItemID;
  }
  
  public int getPricingEventID() {
    return this.pricingEventID;
  }
  
  public ProductIfc getProduct() {
    return this.product;
  }
  
  public String getProductGroupID() {
    return getItemClassification().getGroup().getGroupID();
  }
  
  public int getRestrictiveAge() {
    return this.restrictiveAge;
  }
  
  public String getRevisionNumber() {
    return "$Revision: /main/23 $";
  }
  
  public CurrencyIfc getSellingPrice() {
    return this.sellingPrice;
  }
  
  public String getShortDescription(Locale locale) {
    return this.shortDescriptions.getText(LocaleMap.getBestMatch(locale));
  }
  
  public LocalizedTextIfc getShortLocalizedDescriptions() {
    return this.shortDescriptions;
  }
  
  public String getShortDescription() {
    return getShortDescription(LocaleMap.getLocale("locale_Default"));
  }
  
  public boolean getTaxable() {
    return this.taxable;
  }
  
  public int getTaxGroupID() {
    return this.taxGroupID;
  }
  
  public UnitOfMeasureIfc getUnitOfMeasure() {
    return this.unitOfMeasure;
  }
  
  public void initialize() {
    this.sellingPrice = DomainGateway.getBaseCurrencyInstance();
    this.permanentPrice = DomainGateway.getBaseCurrencyInstance();
    this.compareAtPrice = DomainGateway.getBaseCurrencyInstance();
    this.itemCost = DomainGateway.getBaseCurrencyInstance();
    this.descriptions = DomainGateway.getFactory().getLocalizedText();
    this.shortDescriptions = DomainGateway.getFactory().getLocalizedText();
    this.itemClassification = DomainGateway.getFactory().getItemClassificationInstance();
    this.department = DomainGateway.getFactory().getDepartmentInstance();
    this.unitOfMeasure = DomainGateway.getFactory().getUnitOfMeasureInstance();
    this.product = DomainGateway.getFactory().getProductInstance();
    this.itemImage = DomainGateway.getFactory().getItemImageInstance();
    this.itemMessageCollectionMap = new HashMap<>(0);
  }
  
  public boolean isEmployeeDiscountEligible() {
    return getItemClassification().isEmployeeDiscountEligible();
  }
  
  public boolean isDamageDiscountEligible() {
    return getItemClassification().isDamageDiscountEligible();
  }
  
  public boolean isDiscountEligible() {
    return getItemClassification().isDiscountEligible();
  }
  
  public boolean isItemSizeRequired() {
    return this.itemSizeRequired;
  }
  
  public boolean isKitComponent() {
    return false;
  }
  
  public boolean isKitHeader() {
    return false;
  }
  
  public boolean isSpecialOrderEligible() {
    return getItemClassification().isSpecialOrderEligible();
  }
  
  public boolean isStoreCoupon() {
    return getItemClassification().isStoreCoupon();
  }
  
  public void setCloneAttributes(Item item) {
    item.setItemID(this.itemID);
    item.setLocalizedDescriptions(this.descriptions);
    item.setShortLocalizedDescriptions(this.shortDescriptions);
    item.setSellingPrice((CurrencyIfc)this.sellingPrice.clone());
    item.setPermanentPrice((CurrencyIfc)this.permanentPrice.clone());
    item.setItemCost((CurrencyIfc)this.itemCost.clone());
    item.setPricingEventID(this.pricingEventID);
    item.setTaxGroupID(this.taxGroupID);
    item.setTaxable(this.taxable);
    if (this.department != null)
      item.setDepartment((DepartmentIfc)this.department.clone()); 
    if (this.unitOfMeasure != null)
      item.setUnitOfMeasure((UnitOfMeasureIfc)this.unitOfMeasure.clone()); 
    if (this.itemClassification != null)
      item.setItemClassification((ItemClassificationIfc)this.itemClassification.clone()); 
    if (this.product != null)
      item.setProduct((ProductIfc)this.product.clone()); 
    if (this.itemImage != null)
      item.setItemImage((ItemImageIfc)this.itemImage.clone()); 
    item.setItemWeight(this.itemWeight);
    item.setPosItemID(this.posItemID);
    if (this.compareAtPrice != null)
      item.setCompareAtPrice((CurrencyIfc)this.compareAtPrice.clone()); 
    item.setRestrictiveAge(this.restrictiveAge);
    item.setAllItemLevelMessages(cloneItemMessages(this.itemMessageCollectionMap));
  }
  
  public void setCompareAtPrice(CurrencyIfc value) {
    this.compareAtPrice = value;
  }
  
  public void setDepartment(DepartmentIfc value) {
    this.department = value;
  }
  
  public void setDescription(Locale locale, String value) {
    this.descriptions.putText(LocaleMap.getBestMatch(locale), value);
  }
  
  public void setLocalizedDescriptions(LocalizedTextIfc value) {
    this.descriptions = value;
  }
  
  public void setDescription(String value) {
    setDescription(LocaleMap.getLocale("locale_Default"), value);
  }
  
  public void setItemClassification(ItemClassificationIfc value) {
    this.itemClassification = value;
  }
  
  public void setItemCost(CurrencyIfc value) {
    this.itemCost = value;
  }
  
  public void setItemID(String value) {
    this.itemID = value;
  }
  
  public void setItemSizeRequired(boolean required) {
    this.itemSizeRequired = required;
  }
  
  public void setItemWeight(BigDecimal value) {
    this.itemWeight = value;
  }
  
  public void setManufacturer(String manufacturer) {
    setManufacturer(LocaleMap.getLocale("locale_Default"), manufacturer);
  }
  
  public void setManufacturer(Locale lcl, String value) {
    Locale bestMatch = LocaleMap.getBestMatch(lcl);
    getLocalizedManufacturer().putText(bestMatch, value);
  }
  
  public LocalizedTextIfc getLocalizedManufacturer() {
    return this.localizedManufacturer;
  }
  
  public void setLocalizedManufacturer(LocalizedTextIfc localizedManufacturer) {
    this.localizedManufacturer = localizedManufacturer;
  }
  
  public void setPermanentPrice(CurrencyIfc value) {
    this.permanentPrice = value;
  }
  
  public void setPlanogramID(String[] planogramId) {
    this.planogramID = planogramId;
  }
  
  public void setPosItemID(String value) {
    this.posItemID = value;
  }
  
  public void setPricingEventID(int value) {
    this.pricingEventID = value;
  }
  
  public void setProduct(ProductIfc value) {
    this.product = value;
  }
  
  public void setRestrictiveAge(int restrictiveAge) {
    this.restrictiveAge = restrictiveAge;
  }
  
  public void setSellingPrice(CurrencyIfc value) {
    this.sellingPrice = value;
  }
  
  public void setShortDescription(Locale locale, String value) {
    this.shortDescriptions.putText(LocaleMap.getBestMatch(locale), value);
  }
  
  public void setShortLocalizedDescriptions(LocalizedTextIfc value) {
    this.shortDescriptions = value;
  }
  
  public void setShortDescription(String value) {
    setShortDescription(LocaleMap.getLocale("locale_Default"), value);
  }
  
  public void setTaxable(boolean value) {
    this.taxable = value;
  }
  
  public void setTaxGroupID(int value) {
    this.taxGroupID = value;
  }
  
  public void setUnitOfMeasure(UnitOfMeasureIfc value) {
    this.unitOfMeasure = value;
  }
  
  public String toString() {
    StringBuilder strResult = Util.classToStringHeader("Item", "$Revision: /main/23 $", hashCode());
    strResult.append(Util.formatToStringEntry("itemID", getItemID())).append(Util.formatToStringEntry("description", getDescription(LocaleMap.getLocale("locale_Default")))).append(Util.formatToStringEntry("shortDescription", getShortDescription(LocaleMap.getLocale("locale_Default")))).append(Util.formatToStringEntry("sellingPrice", getSellingPrice().toString())).append(Util.formatToStringEntry("permanentPrice", getPermanentPrice().toString())).append(Util.formatToStringEntry("pricingEventID", getPricingEventID())).append(Util.formatToStringEntry("taxGroupID", getTaxGroupID())).append(Util.formatToStringEntry("taxable", getTaxable())).append(Util.formatToStringEntry("department", getDepartment())).append(Util.formatToStringEntry("unitOfMeasure", getUnitOfMeasure())).append(Util.formatToStringEntry("itemClassification", getItemClassification())).append(Util.formatToStringEntry("product", getProduct())).append(Util.formatToStringEntry("itemWeight", getItemWeight()));
    return strResult.toString();
  }
  
  private Map<String, List<MessageDTO>> cloneItemMessages(Map<String, List<MessageDTO>> itemMsgMap) {
    int initSize = (int)Math.ceil(itemMsgMap.size() / 0.75D);
    Map<String, List<MessageDTO>> clonedItemMessagesMap = new HashMap<>(initSize);
    Map<String, List<MessageDTO>> orignalItemMessages = itemMsgMap;
    List<MessageDTO> messageDTOList = null;
    List<MessageDTO> clonedMessageDTOList = new ArrayList<>();
    Set<String> keyset = null;
    Iterator<String> iter = null;
    MessageDTO msgDTO = null;
    MessageDTO clonedMsgDTO = null;
    String key = null;
    if (orignalItemMessages != null) {
      keyset = orignalItemMessages.keySet();
      iter = keyset.iterator();
      while (iter.hasNext()) {
        key = iter.next();
        messageDTOList = orignalItemMessages.get(key);
        if (messageDTOList != null)
          for (int msgctr = 0; msgctr < messageDTOList.size(); msgctr++) {
            msgDTO = messageDTOList.get(msgctr);
            if (msgDTO != null)
              clonedMsgDTO = (MessageDTO)msgDTO.clone(); 
            clonedMessageDTOList.add(clonedMsgDTO);
            clonedMsgDTO = null;
          }  
        clonedItemMessagesMap.put(key, clonedMessageDTOList);
        clonedMessageDTOList = new ArrayList<>();
      } 
    } 
    return clonedItemMessagesMap;
  }


public void setEmployeeDiscountEligible(boolean EmployeeDiscountAllowedFlag) {
	getItemClassification().setEmployeeDiscountAllowedFlag(EmployeeDiscountAllowedFlag);
}
}
