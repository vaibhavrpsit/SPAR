package oracle.retail.stores.domain.stock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderItemIfc;
import oracle.retail.stores.common.item.ExtendedItemDataContainer;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.common.utility._360Date;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import org.apache.log4j.Logger;

public class PLUItem implements PLUItemIfc {
  private static Logger logger = Logger.getLogger(PLUItem.class);
  
  static final long serialVersionUID = 7321887861981917535L;
  
  protected ItemIfc item = null;
  
  protected HashMap<String, RelatedItemGroupIfc> relatedItemContainer;
  
  protected boolean itemSizeRequired;
  
  protected List<ItemSizeIfc> availableItemSizes;
  
  protected List<PriceChangeIfc> priceChangesTemporary;
  
  protected List<PriceChangeIfc> priceChangesClearance;
  
  protected List<PriceChangeIfc> priceChangesPermanent;
  
  protected List<PriceChangeIfc> priceChangesTemporaryForReturns;
  
  protected List<PriceChangeIfc> priceChangesClearanceForReturns;
  
  protected List<AdvancedPricingRuleIfc> advancedPricingRules;
  
  protected List<TaxRuleIfc> taxRules;
  
  protected String storeID = "";
  
  protected String taxGroupName = "";
  
  protected int restrictiveAge = 0;
  
  protected int manufacturerID;
  
  protected String manufacturerItemUPC;
  
  protected int returnPriceDays = 0;
  
  EYSDate returnPriceCutoffDate = null;
  
  protected ExternalOrderItemIfc returnExternalOrderItem = null;
  
  protected boolean onClearance;
  
  protected boolean availableInCurrentStore = true;
  
  protected ExtendedItemDataContainer extendedItemDataContainer = null;
  
	/*
	 * protected String liqcat = null;
	 * 
	 * protected String liquom = null;
	 */
  
 
public PLUItem() {
    this.item = DomainGateway.getFactory().getItemInstance();
    this.advancedPricingRules = new ArrayList<>();
    this.priceChangesTemporary = new ArrayList<>();
    this.priceChangesClearance = new ArrayList<>();
    this.priceChangesTemporaryForReturns = new ArrayList<>();
    this.priceChangesClearanceForReturns = new ArrayList<>();
    this.priceChangesPermanent = new ArrayList<>();
    this.taxRules = new ArrayList<>();
    this.relatedItemContainer = new HashMap<>(0);
    retrieveReturnPriceDays();
  }
  
  public Set<String> getAdvancedPricingRuleIDs() {
    Set<String> ruleIDs = new HashSet<>();
    for (int i = 0; i < this.advancedPricingRules.size(); i++) {
      AdvancedPricingRuleIfc rule = this.advancedPricingRules.get(i);
      ruleIDs.add(rule.getRuleID());
    } 
    return ruleIDs;
  }
  
  public void addAdvancedPricingRule(AdvancedPricingRuleIfc rule) {
    if (!this.advancedPricingRules.contains(rule))
      this.advancedPricingRules.add(rule); 
  }
  
  public void addAdvancedPricingRules(AdvancedPricingRuleIfc[] rules) {
    Set<String> existingRules = getAdvancedPricingRuleIDs();
    for (AdvancedPricingRuleIfc newRule : rules) {
      if (!existingRules.contains(newRule.getRuleID()))
        this.advancedPricingRules.add(newRule); 
    } 
  }
  
  public void addPermanentPriceChange(PriceChangeIfc priceChange) {
    this.priceChangesPermanent.add(priceChange);
    Collections.sort(this.priceChangesPermanent);
  }
  
  public void addPermanentPriceChanges(PriceChangeIfc[] changes) {
    this.priceChangesPermanent.addAll(Arrays.asList(changes));
    Collections.sort(this.priceChangesPermanent);
  }
  
  public void addTemporaryPriceChange(PriceChangeIfc priceChange) {
    this.priceChangesTemporary.add(priceChange);
    Collections.sort(this.priceChangesTemporary);
  }
  
  public void addClearancePriceChange(PriceChangeIfc priceChange) {
    this.priceChangesClearance.add(priceChange);
    Collections.sort(this.priceChangesClearance);
  }
  
  public void addTemporaryPriceChangeForReturns(PriceChangeIfc priceChange) {
    this.priceChangesTemporaryForReturns.add(priceChange);
    Collections.sort(this.priceChangesTemporaryForReturns);
  }
  
  public void addClearancePriceChangeForReturns(PriceChangeIfc priceChange) {
    this.priceChangesClearanceForReturns.add(priceChange);
    Collections.sort(this.priceChangesClearanceForReturns);
  }
  
  public void addTemporaryPriceChanges(PriceChangeIfc[] changes) {
    this.priceChangesTemporary.addAll(Arrays.asList(changes));
    Collections.sort(this.priceChangesTemporary);
  }
  
  public void addTaxRule(TaxRuleIfc rule) {
    if (this.taxRules == null)
      this.taxRules = new ArrayList<>(); 
    this.taxRules.add(rule);
  }
  
  public void addTaxRules(TaxRuleIfc[] rules) {
    if (this.taxRules == null)
      this.taxRules = new ArrayList<>(); 
    if (rules != null)
      this.taxRules.addAll(Arrays.asList(rules)); 
  }
  
  public Iterator<AdvancedPricingRuleIfc> advancedPricingRules() {
    return this.advancedPricingRules.iterator();
  }
  
  public void clearAdvancedPricingRules() {
    this.advancedPricingRules.clear();
  }
  
  public void clearPermanentPriceChanges() {
    this.priceChangesPermanent.clear();
  }
  
  public void clearTemporaryPriceChanges() {
    this.priceChangesTemporary.clear();
  }
  
  public void clearClearancePriceChanges() {
    this.priceChangesClearance.clear();
  }
  
  public void clearTaxRules() {
    if (this.taxRules != null)
      this.taxRules.clear(); 
  }
  
  public AdvancedPricingRuleIfc[] getAdvancedPricingRules() {
    AdvancedPricingRuleIfc[] apr = new AdvancedPricingRuleIfc[this.advancedPricingRules.size()];
    this.advancedPricingRules.toArray(apr);
    return apr;
  }
  
  public CurrencyIfc getCompareAtPrice() {
    CurrencyIfc comparePrice = null;
    comparePrice = this.item.getCompareAtPrice();
    if (comparePrice == null)
      comparePrice = DomainGateway.getBaseCurrencyInstance(); 
    return comparePrice;
  }
  
  public boolean getDamageDiscountEligible() {
    return this.item.getItemClassification().isDamageDiscountEligible();
  }
  
  public boolean isAvailableInCurrentStore() {
    return this.availableInCurrentStore;
  }
  
  public void setAvailableInCurrentStore(boolean availableInCurrentStore) {
    this.availableInCurrentStore = availableInCurrentStore;
  }
  
  public DepartmentIfc getDepartment() {
    return this.item.getDepartment();
  }
  
  public String getDepartmentID() {
    String id = null;
    if (this.item != null && this.item.getDepartment() != null)
      id = this.item.getDepartment().getDepartmentID(); 
    return id;
  }
  
  public String getDescription(Locale locale) {
    return this.item.getDescription(locale);
  }
  
  public LocalizedTextIfc getLocalizedDescriptions() {
    return this.item.getLocalizedDescriptions();
  }
  
  public boolean getDiscountEligible() {
    return this.item.getItemClassification().isDiscountEligible();
  }
  
  public boolean getEmployeeDiscountEligible() {
    return this.item.getItemClassification().getEmployeeDiscountAllowedFlag();
  }
  
  public ItemIfc getItem() {
    return this.item;
  }
  
  public ItemClassificationIfc getItemClassification() {
    return this.item.getItemClassification();
  }
  
  public CurrencyIfc getItemCost() {
    return this.item.getItemCost();
  }
  
  public String getItemID() {
    return this.item.getItemID();
  }
  
  public BigDecimal getItemWeight() {
    return this.item.getItemWeight();
  }
  
  public String getManufacturer(Locale lcl) {
    Locale bestMatch = LocaleMap.getBestMatch(lcl);
    return this.item.getManufacturer(bestMatch);
  }
  
  public int getManufacturerID() {
    return this.manufacturerID;
  }
  
  public String getMerchandiseCodesString() {
    return getItemClassification().getMerchandiseCodesString();
  }
  
  public CurrencyIfc getPermanentPrice() {
    return getPermanentPrice(new EYSDate());
  }
  
  public CurrencyIfc getPermanentPrice(EYSDate when) {
    CurrencyIfc price = null;
    if (hasPermanentPriceChanges())
      for (Iterator<PriceChangeIfc> iter = priceChangesPermanent(); iter.hasNext(); ) {
        PriceChangeIfc priceChange = iter.next();
        if (priceChange.isInEffect(when)) {
          price = priceChange.getNewPrice();
          break;
        } 
      }  
    return (price != null) ? price : DomainGateway.getBaseCurrencyInstance("0");
  }
  
  public String[] getPlanogramID() {
    return this.item.getPlanogramID();
  }
  
  public String getPosItemID() {
    return this.item.getPosItemID();
  }
  
  public CurrencyIfc getPrice() {
    return getPrice(new EYSDate(), -1);
  }
  
  public CurrencyIfc getPrice(EYSDate when, int pricingGroupID) {
    CurrencyIfc price = null;
    CurrencyIfc tempPrice = null;
    CurrencyIfc clrPrice = null;
    PriceChangeIfc priceChange = getEffectiveTemporaryPriceChange(when, pricingGroupID);
    if (priceChange != null)
      tempPrice = priceChange.getNewPrice(); 
    priceChange = getEffectiveClearancePriceChange(when, pricingGroupID);
    if (priceChange != null)
      clrPrice = priceChange.getNewPrice(); 
    if (tempPrice != null && clrPrice != null)
      if (tempPrice.compareTo(clrPrice) < 0) {
        price = tempPrice;
      } else {
        price = clrPrice;
        setOnClearance(true);
      }  
    if (tempPrice != null && clrPrice == null)
      price = tempPrice; 
    if (tempPrice == null && clrPrice != null) {
      price = clrPrice;
      setOnClearance(true);
    } 
    if (tempPrice == null && clrPrice == null)
      price = getPermanentPrice(when); 
    return price;
  }
  
  public CurrencyIfc getReturnPrice(int pricingGroupID) {
    CurrencyIfc price = null;
    CurrencyIfc lpp = getLowestPermanentPrice();
    CurrencyIfc ltp = getLowestTemporaryPrice(pricingGroupID);
    CurrencyIfc lcp = getLowestClearancePrice(pricingGroupID);
    if (ltp == null && lpp == null && lcp == null) {
      price = this.item.getSellingPrice();
    } else if (ltp == null && lcp == null) {
      price = lpp;
    } else if (lpp == null && lcp == null) {
      price = ltp;
    } else if (lpp == null && ltp == null) {
      price = lcp;
    } else if (lcp == null) {
      if (ltp.compareTo(lpp) < 0) {
        price = ltp;
      } else {
        price = lpp;
      } 
    } else if (ltp == null) {
      if (lcp.compareTo(lpp) < 0) {
        price = lcp;
        setOnClearance(true);
      } else {
        price = lpp;
      } 
    } else if (lpp == null) {
      if (ltp.compareTo(lcp) < 0) {
        price = ltp;
      } else {
        price = lcp;
        setOnClearance(true);
      } 
    } else if (lpp != null && lcp != null && ltp != null) {
      if (ltp.compareTo(lcp) < 0) {
        if (ltp.compareTo(lpp) < 0) {
          price = ltp;
        } else {
          price = lpp;
        } 
      } else if (lcp.compareTo(lpp) < 0) {
        price = lcp;
        setOnClearance(true);
      } else {
        price = lpp;
      } 
    } 
    return price;
  }
  
  protected CurrencyIfc getLowestPermanentPrice() {
    CurrencyIfc lowestPrice = null;
    EYSDate[] endDates = getPermanentPriceEndDates();
    int index = 0;
    for (PriceChangeIfc priceChange : this.priceChangesPermanent) {
      if (endDates[index].compareTo((_360Date)getReturnPriceCutoffDate()) >= 0) {
        CurrencyIfc newPrice = priceChange.getNewPrice();
        if (lowestPrice == null) {
          lowestPrice = newPrice;
        } else if (newPrice.compareTo(lowestPrice) < 0) {
          lowestPrice = newPrice;
        } 
      } 
      index++;
    } 
    return lowestPrice;
  }
  
  protected CurrencyIfc getLowestTemporaryPrice(int pricingGroupID) {
    CurrencyIfc lowestPrice = null;
    EYSDate[] endDates = getPermanentPriceEndDates();
    if (!this.priceChangesTemporaryForReturns.isEmpty()) {
      PriceChangeIfc[] expanded = getExpandedPriceChangesTemporaryForReturns(endDates);
      for (PriceChangeIfc priceChange : expanded) {
        CurrencyIfc price = priceChange.getNewPrice();
        if (priceChange.getPricingGroupID() < 0) {
          if (lowestPrice == null || price.compareTo(lowestPrice) < 0)
            lowestPrice = price; 
        } else if (priceChange.getPricingGroupID() == pricingGroupID) {
          if (lowestPrice == null || price.compareTo(lowestPrice) < 0)
            lowestPrice = price; 
        } 
      } 
    } 
    return lowestPrice;
  }
  
  protected CurrencyIfc getLowestClearancePrice(int pricingGroupID) {
    CurrencyIfc lowestPrice = null;
    EYSDate[] endDates = getPermanentPriceEndDates();
    if (!this.priceChangesClearanceForReturns.isEmpty()) {
      PriceChangeIfc[] expanded = getExpandedPriceChangesClearanceForReturns(endDates);
      for (PriceChangeIfc priceChange : expanded) {
        CurrencyIfc price = priceChange.getNewPrice();
        if (priceChange.getPricingGroupID() < 0) {
          if (lowestPrice == null || price.compareTo(lowestPrice) < 0)
            lowestPrice = price; 
        } else if (priceChange.getPricingGroupID() == pricingGroupID) {
          if (lowestPrice == null || price.compareTo(lowestPrice) < 0)
            lowestPrice = price; 
        } 
      } 
    } 
    return lowestPrice;
  }
  
  protected PriceChangeIfc[] getExpandedPriceChangesClearanceForReturns(EYSDate[] endDates) {
    ArrayList<PriceChangeIfc> ExpandedPriceChanges = new ArrayList<>();
    for (PriceChangeIfc priceChange : this.priceChangesClearanceForReturns)
      ExpandedPriceChanges.addAll(getAssociatedPermanentPrices(priceChange, endDates)); 
    PriceChangeIfc[] expanded = new PriceChangeIfc[ExpandedPriceChanges.size()];
    return ExpandedPriceChanges.<PriceChangeIfc>toArray(expanded);
  }
  
  protected PriceChangeIfc[] getExpandedPriceChangesTemporaryForReturns(EYSDate[] endDates) {
    ArrayList<PriceChangeIfc> ExpandedPriceChanges = new ArrayList<>();
    for (PriceChangeIfc priceChange : this.priceChangesTemporaryForReturns)
      ExpandedPriceChanges.addAll(getAssociatedPermanentPrices(priceChange, endDates)); 
    PriceChangeIfc[] expanded = new PriceChangeIfc[ExpandedPriceChanges.size()];
    return ExpandedPriceChanges.<PriceChangeIfc>toArray(expanded);
  }
  
  protected Collection<PriceChangeIfc> getAssociatedPermanentPrices(PriceChangeIfc priceChange, EYSDate[] endDates) {
    ArrayList<PriceChangeIfc> expandedPriceChanges = new ArrayList<>();
    for (int i = 0; i < endDates.length; i++) {
      PriceChangeIfc permPriceChange = this.priceChangesPermanent.get(i);
      if (isPermanentPriceValidForTemporaryPrice(priceChange, permPriceChange.getPriceMaintenanceEvent().getEffectiveDateTimestamp(), endDates[i])) {
        PLUItemIfc dummy = DomainGateway.getFactory().getPLUItemInstance();
        dummy.addPermanentPriceChange(permPriceChange);
        PriceChangeIfc newPriceChange = (PriceChangeIfc)priceChange.clone();
        newPriceChange.setItem(dummy);
        expandedPriceChanges.add(newPriceChange);
      } 
    } 
    return expandedPriceChanges;
  }
  
  private boolean isPermanentPriceValidForTemporaryPrice(PriceChangeIfc priceChange, EYSDate permStartDate, EYSDate permEndDate) {
    boolean isPriceValid = false;
    EYSDate tempEffDate = priceChange.getPriceMaintenanceEvent().getEffectiveDateTimestamp();
    EYSDate tempExpDate = priceChange.getPriceMaintenanceEvent().getExpirationDateTimestamp();
    if (tempEffDate.compareTo((_360Date)permStartDate) >= 0 && tempEffDate.compareTo((_360Date)permEndDate) <= 0) {
      isPriceValid = true;
    } else if (tempExpDate.compareTo((_360Date)permStartDate) >= 0 && tempExpDate.compareTo((_360Date)permEndDate) <= 0) {
      isPriceValid = true;
    } 
    return isPriceValid;
  }
  
  private EYSDate[] getPermanentPriceEndDates() {
    EYSDate[] endDates = new EYSDate[this.priceChangesPermanent.size()];
    int index = 0;
    EYSDate lastDate = null;
    for (PriceChangeIfc priceChange : this.priceChangesPermanent) {
      if (index == 0) {
        EYSDate firstEndDate = new EYSDate();
        firstEndDate.add(1, 10000);
        endDates[index] = firstEndDate;
      } else {
        endDates[index] = lastDate;
      } 
      lastDate = priceChange.getPriceMaintenanceEvent().getEffectiveDateTimestamp();
      index++;
    } 
    return endDates;
  }
  
  public PriceChangeIfc[] getPermanentPriceChanges() {
    PriceChangeIfc[] array = new PriceChangeIfc[this.priceChangesPermanent.size()];
    this.priceChangesPermanent.toArray(array);
    return array;
  }
  
  public PriceChangeIfc[] getTemporaryPriceChanges() {
    PriceChangeIfc[] array = new PriceChangeIfc[this.priceChangesTemporary.size()];
    this.priceChangesTemporary.toArray(array);
    return array;
  }
  
  public PriceChangeIfc[] getClearancePriceChanges() {
    PriceChangeIfc[] array = new PriceChangeIfc[this.priceChangesClearance.size()];
    this.priceChangesClearance.toArray(array);
    return array;
  }
  
  public void setClearancePriceChanges(PriceChangeIfc[] changes) {
    this.priceChangesClearance.clear();
    Arrays.sort((Object[])changes);
    this.priceChangesClearance.addAll(Arrays.asList(changes));
  }
  
  public PriceChangeIfc getEffectiveTemporaryPriceChange() {
    return getEffectiveTemporaryPriceChange(new EYSDate());
  }
  
  public PriceChangeIfc getEffectiveTemporaryPriceChange(EYSDate when) {
    return getEffectiveTemporaryPriceChange(when, -1);
  }
  
  public PriceChangeIfc getEffectiveClearancePriceChange() {
    return getEffectiveClearancePriceChange(new EYSDate());
  }
  
  public PriceChangeIfc getEffectiveClearancePriceChange(EYSDate when) {
    return getEffectiveClearancePriceChange(when, -1);
  }
  
  public PriceChangeIfc getEffectiveTemporaryPriceChange(EYSDate when, int pricingGroupID) {
    PriceChangeIfc effectivePriceChange = null;
    if (hasTemporaryPriceChanges())
      for (Iterator<PriceChangeIfc> iter = priceChangesTemporary(); iter.hasNext(); ) {
        PriceChangeIfc currentPriceChange = iter.next();
        if (currentPriceChange.isInEffect(when)) {
          if (effectivePriceChange == null && currentPriceChange.getPricingGroupID() < 1)
            effectivePriceChange = currentPriceChange; 
          if (pricingGroupID < 1 && currentPriceChange.getPricingGroupID() < 1)
            return effectivePriceChange; 
          if (currentPriceChange.getPricingGroupID() == pricingGroupID)
            return currentPriceChange; 
        } 
      }  
    return effectivePriceChange;
  }
  
  public PriceChangeIfc getEffectiveClearancePriceChange(EYSDate when, int pricingGroupID) {
    PriceChangeIfc effectivePriceChange = null;
    if (hasClearancePriceChanges())
      for (Iterator<PriceChangeIfc> iter = priceChangesClearance(); iter.hasNext(); ) {
        PriceChangeIfc currentPriceChange = iter.next();
        if (currentPriceChange.isInEffect(when)) {
          if (effectivePriceChange == null && currentPriceChange.getPricingGroupID() < 1)
            effectivePriceChange = currentPriceChange; 
          if (pricingGroupID < 1 && currentPriceChange.getPricingGroupID() < 1)
            return effectivePriceChange; 
          if (currentPriceChange.getPricingGroupID() == pricingGroupID)
            return currentPriceChange; 
        } 
      }  
    return effectivePriceChange;
  }
  
  public int getPricingEventID() {
    return this.item.getPricingEventID();
  }
  
  public ProductIfc getProduct() {
    return this.item.getProduct();
  }
  
  public String getProductGroupID() {
    return this.item.getItemClassification().getGroup().getGroupID();
  }
  
  public HashMap<String, RelatedItemGroupIfc> getRelatedItemContainer() {
    return this.relatedItemContainer;
  }
  
  public int getRestrictiveAge() {
    return this.restrictiveAge;
  }
  
  public String getRevisionNumber() {
    return Util.parseRevisionNumber("$Revision: /main/37 $");
  }
  
  public CurrencyIfc getSellingPrice() {
    CurrencyIfc sellingPrice = null;
    if (hasAdvancedPricingRules()) {
      ItemContainerProxyIfc proxy = DomainGateway.getFactory().getItemContainerProxyInstance();
      sellingPrice = proxy.addPLUItem(this).getExtendedDiscountedSellingPrice();
    } else {
      sellingPrice = getPrice();
    } 
    return sellingPrice;
  }
  
  public String getShortDescription(Locale locale) {
    return this.item.getShortDescription(locale);
  }
  
  public LocalizedTextIfc getShortLocalizedDescriptions() {
    return this.item.getShortLocalizedDescriptions();
  }
  
  public StockItemIfc getStockItem() {
    StockItemIfc stockItem = DomainGateway.getFactory().getStockItemInstance();
    stockItem.initialize(getItemID(), getLocalizedDescriptions(), (CurrencyIfc)getPrice().clone(), getTaxGroupID(), getTaxable(), getDepartmentID());
    return stockItem;
  }
  
  public String getStoreID() {
    return this.storeID;
  }
  
  public boolean getTaxable() {
    return this.item.getTaxable();
  }
  
  public int getTaxGroupID() {
    return this.item.getTaxGroupID();
  }
  
  public String getTaxGroupName() {
    return this.taxGroupName;
  }
  
  public TaxRuleIfc[] getTaxRules() {
    TaxRuleIfc[] tr = null;
    if (this.taxRules != null && this.taxRules.size() > 0)
      tr = this.taxRules.<TaxRuleIfc>toArray(new TaxRuleIfc[this.taxRules.size()]); 
    return tr;
  }
  
  public UnitOfMeasureIfc getUnitOfMeasure() {
    return this.item.getUnitOfMeasure();
  }
  
  public boolean hasAdvancedPricingRules() {
    return !this.advancedPricingRules.isEmpty();
  }
  
  public boolean hasMerchandiseClassifications() {
    return getItemClassification().hasMerchandiseClassifications();
  }
  
  public boolean hasPermanentPriceChanges() {
    return !this.priceChangesPermanent.isEmpty();
  }
  
  public boolean hasTemporaryPriceChanges() {
    return !this.priceChangesTemporary.isEmpty();
  }
  
  public boolean hasClearancePriceChanges() {
    return !this.priceChangesClearance.isEmpty();
  }
  
  public boolean hasTaxRules() {
    if (this.taxRules == null)
      this.taxRules = new ArrayList<>(); 
    return !this.taxRules.isEmpty();
  }
  
  public boolean hasRelatedItems() {
    return (getRelatedItemContainer() != null && !getRelatedItemContainer().isEmpty());
  }
  
  public void initialize(String id, LocalizedTextIfc desc, CurrencyIfc prc, int taxGroup, boolean taxFlag, String dept) {
    setItemID(id);
    setLocalizedDescriptions(desc);
    setPrice(prc);
    setTaxGroupID(taxGroup);
    setDepartmentID(dept);
    setTaxable(taxFlag);
  }
  
  public boolean isAlterationItem() {
    return false;
  }
  
  public boolean isEmployeeDiscountEligible() {
    return getEmployeeDiscountEligible();
  }
  
  public boolean isDamageDiscountEligible() {
    return getDamageDiscountEligible();
  }
  
  public boolean isDiscountEligible() {
    return getDiscountEligible();
  }
  
  public boolean isItemSizeRequired() {
    return this.itemSizeRequired;
  }
  
  public boolean isKitComponent() {
    return this.item.isKitComponent();
  }
  
  public boolean isKitHeader() {
    return this.item.isKitHeader();
  }
  
  public boolean isSerializedItem() {
    return getItemClassification().isSerializedItem();
  }
  
  public boolean isSpecialOrderEligible() {
    return this.item.isSpecialOrderEligible();
  }
  
  public boolean isStoreCoupon() {
    return this.item.isStoreCoupon();
  }
  
  public Iterator<PriceChangeIfc> priceChangesPermanent() {
    return this.priceChangesPermanent.iterator();
  }
  
  public Iterator<PriceChangeIfc> priceChangesTemporary() {
    return this.priceChangesTemporary.iterator();
  }
  
  public Iterator<PriceChangeIfc> priceChangesClearance() {
    return this.priceChangesClearance.iterator();
  }
  
  public void setAdvancedPricingRules(AdvancedPricingRuleIfc[] rules) {
    this.advancedPricingRules.clear();
    this.advancedPricingRules.addAll(Arrays.asList(rules));
  }
  
  public void setCloneAttributes(PLUItem newClass) {
    if (this.item != null)
      newClass.setItem((ItemIfc)this.item.clone()); 
    PriceChangeIfc priceChange = null;
    Iterator<PriceChangeIfc> i;
    for (i = priceChangesTemporary(); i.hasNext(); ) {
      priceChange = i.next();
      if (priceChange != null)
        newClass.addTemporaryPriceChange((PriceChangeIfc)priceChange.clone()); 
    } 
    for (i = this.priceChangesTemporaryForReturns.iterator(); i.hasNext(); ) {
      priceChange = i.next();
      if (priceChange != null)
        newClass.addTemporaryPriceChangeForReturns((PriceChangeIfc)priceChange.clone()); 
    } 
    for (i = priceChangesClearance(); i.hasNext(); ) {
      priceChange = i.next();
      if (priceChange != null)
        newClass.addClearancePriceChange((PriceChangeIfc)priceChange.clone()); 
    } 
    for (i = priceChangesPermanent(); i.hasNext(); ) {
      priceChange = i.next();
      if (priceChange != null)
        newClass.addPermanentPriceChange((PriceChangeIfc)priceChange.clone()); 
    } 
    newClass.setTaxGroupName(getTaxGroupName());
    TaxRuleIfc txRule = null;
    for (Iterator<TaxRuleIfc> iterator = taxRules(); iterator.hasNext(); ) {
      txRule = iterator.next();
      newClass.addTaxRule((txRule == null) ? null : (TaxRuleIfc)txRule.clone());
    } 
    newClass.setPosItemID(getPosItemID());
    AdvancedPricingRuleIfc rule = null;
    for (Iterator<AdvancedPricingRuleIfc> iterator1 = advancedPricingRules(); iterator1.hasNext(); ) {
      rule = iterator1.next();
      newClass.addAdvancedPricingRule((rule == null) ? null : (AdvancedPricingRuleIfc)rule.clone());
    } 
    if (this.relatedItemContainer != null)
      newClass.setRelatedItemContainer((HashMap<String, RelatedItemGroupIfc>)this.relatedItemContainer.clone()); 
    if (this.returnPriceCutoffDate != null)
      newClass.returnPriceCutoffDate = (EYSDate)this.returnPriceCutoffDate.clone(); 
    newClass.itemSizeRequired = this.itemSizeRequired;
    newClass.storeID = this.storeID;
    newClass.restrictiveAge = this.restrictiveAge;
    newClass.manufacturerID = this.manufacturerID;
    if (this.returnExternalOrderItem != null)
      newClass.setReturnExternalOrderItem((ExternalOrderItemIfc)this.returnExternalOrderItem.clone()); 
    if (this.extendedItemDataContainer != null)
      newClass.setExtendedItemDataContainer((ExtendedItemDataContainer)this.extendedItemDataContainer.clone()); 
  }
  
  public void setCompareAtPrice(CurrencyIfc value) {
    this.item.setCompareAtPrice(value);
  }
  
  public void setDamageDiscountEligible(boolean value) {
    this.item.getItemClassification().setDamageDiscountEligible(value);
  }
  
  public void setDepartment(DepartmentIfc value) {
    this.item.setDepartment(value);
  }
  
  public void setDepartmentID(String value) {
    this.item.getDepartment().setDepartmentID(value);
  }
  
  public void setDescription(Locale locale, String value) {
    this.item.setDescription(locale, value);
  }
  
  public void setLocalizedDescriptions(LocalizedTextIfc value) {
    this.item.setLocalizedDescriptions(value);
  }
  
  public void setDiscountEligible(boolean value) {
    this.item.getItemClassification().setDiscountEligible(value);
  }
  
  public void setItem(ItemIfc value) {
    this.item = value;
  }
  
  public void setItemClassification(ItemClassificationIfc value) {
    this.item.setItemClassification(value);
  }
  
  public void setItemCost(CurrencyIfc value) {
    this.item.setItemCost(value);
  }
  
  public void setItemID(String value) {
    this.item.setItemID(value);
  }
  
  public void setItemSizeRequired(boolean required) {
    this.itemSizeRequired = required;
  }
  
  public void setItemWeight(BigDecimal value) {
    this.item.setItemWeight(value);
  }
  
  public void setManufacturer(Locale lcl, String value) {
    Locale bestMatch = LocaleMap.getBestMatch(lcl);
    this.item.setManufacturer(bestMatch, value);
  }
  
  public void setManufacturerID(int value) {
    this.manufacturerID = value;
  }
  
  public void setPermanentPrice(CurrencyIfc value) {
    this.item.setPermanentPrice(value);
  }
  
  public void setPlanogramID(String[] value) {
    this.item.setPlanogramID(value);
  }
  
  public void setPosItemID(String value) {
    this.item.setPosItemID(value);
  }
  
  public void setPrice(CurrencyIfc value) {
    setSellingPrice(value);
  }
  
  public void setPermanentPriceChanges(PriceChangeIfc[] changes) {
    this.priceChangesPermanent.clear();
    Arrays.sort((Object[])changes);
    this.priceChangesPermanent.addAll(Arrays.asList(changes));
  }
  
  public void setTemporaryPriceChanges(PriceChangeIfc[] changes) {
    this.priceChangesTemporary.clear();
    Arrays.sort((Object[])changes);
    this.priceChangesTemporary.addAll(Arrays.asList(changes));
  }
  
  public void setTemporaryPriceChangesAndTemporaryPriceChangesForReturns(PriceChangeIfc[] changes) {
    this.priceChangesTemporary.clear();
    this.priceChangesTemporaryForReturns.clear();
    EYSDate now = new EYSDate();
    EYSDate returnsDate = getReturnPriceCutoffDate();
    Arrays.sort((Object[])changes);
    for (PriceChangeIfc priceChange : changes) {
      if (priceChange != null && priceChange.getPriceMaintenanceEvent() != null && priceChange.getPriceMaintenanceEvent().getExpirationDateTimestamp() != null) {
        if (priceChange.getPriceMaintenanceEvent().getExpirationDateTimestamp().compareTo((_360Date)now) >= 0)
          this.priceChangesTemporary.add(priceChange); 
        if (priceChange.getPriceMaintenanceEvent().getExpirationDateTimestamp().compareTo((_360Date)returnsDate) >= 0)
          this.priceChangesTemporaryForReturns.add(priceChange); 
      } 
    } 
  }
  
  public void setClearancePriceChangesAndClearancePriceChangesForReturns(PriceChangeIfc[] changes) {
    this.priceChangesClearance.clear();
    this.priceChangesClearanceForReturns.clear();
    EYSDate now = new EYSDate();
    EYSDate returnsDate = getReturnPriceCutoffDate();
    Arrays.sort((Object[])changes);
    for (PriceChangeIfc priceChange : changes) {
      if (priceChange != null && priceChange.getPriceMaintenanceEvent() != null)
        if (priceChange.getPriceMaintenanceEvent().getExpirationDateTimestamp() != null) {
          if (priceChange.getPriceMaintenanceEvent().getExpirationDateTimestamp().compareTo((_360Date)now) >= 0)
            this.priceChangesClearance.add(priceChange); 
          if (priceChange.getPriceMaintenanceEvent().getExpirationDateTimestamp().compareTo((_360Date)returnsDate) >= 0)
            this.priceChangesClearanceForReturns.add(priceChange); 
        } else {
          this.priceChangesClearance.add(priceChange);
          this.priceChangesClearanceForReturns.add(priceChange);
        }  
    } 
  }
  
  public void setPricingEventID(int value) {
    this.item.setPricingEventID(value);
  }
  
  public void setProduct(ProductIfc value) {
    this.item.setProduct(value);
  }
  
  public void setRelatedItemContainer(HashMap<String, RelatedItemGroupIfc> relatedItemContainer) {
    this.relatedItemContainer = relatedItemContainer;
  }
  
  public void setRestrictiveAge(int restrictiveAge) {
    this.restrictiveAge = restrictiveAge;
  }
  
  public void setSellingPrice(CurrencyIfc value) {
    this.item.setSellingPrice(value);
    PriceChangeIfc pc = DomainGateway.getFactory().getPriceChangeInstance();
    pc.setOverridePriceAmount(value);
    addTemporaryPriceChange(pc);
  }
  
  public void setShortDescription(Locale locale, String value) {
    this.item.setShortDescription(locale, value);
  }
  
  public void setShortLocalizedDescriptions(LocalizedTextIfc value) {
    this.item.setShortLocalizedDescriptions(value);
  }
  
  public void setStoreID(String storeID) {
    this.storeID = storeID;
  }
  
  public void setTaxable(boolean value) {
    this.item.setTaxable(value);
  }
  
  public void setTaxGroupID(int value) {
    this.item.setTaxGroupID(value);
  }
  
  public void setTaxGroupName(String name) {
    this.taxGroupName = name;
  }
  
  public void setTaxRules(TaxRuleIfc[] rules) {
    if (rules != null) {
      if (this.taxRules != null) {
        this.taxRules.clear();
      } else {
        this.taxRules = new ArrayList<>();
      } 
      this.taxRules.addAll(Arrays.asList(rules));
    } else if (this.taxRules == null) {
      this.taxRules = new ArrayList<>();
    } else {
      this.taxRules.clear();
    } 
  }
  
  public void setUnitOfMeasure(UnitOfMeasureIfc value) {
    this.item.setUnitOfMeasure(value);
  }
  
  public Iterator<TaxRuleIfc> taxRules() {
    if (this.taxRules == null)
      this.taxRules = new ArrayList<>(); 
    return this.taxRules.iterator();
  }
  
  public LocalizedTextIfc getLocalizedManufacturer() {
    return this.item.getLocalizedManufacturer();
  }
  
  public void setLocalizedManufacturer(LocalizedTextIfc localizedManufacturer) {
    this.item.setLocalizedManufacturer(localizedManufacturer);
  }
  
  public Map<String, List<MessageDTO>> getAllItemLevelMessages() {
    return this.item.getAllItemLevelMessages();
  }
  
  public List<MessageDTO> getAllItemMessagesInTransaction(String transactionName) {
    return this.item.getAllItemMessagesInTransaction(transactionName);
  }
  
  public ItemImageIfc getItemImage() {
    return this.item.getItemImage();
  }
  
  public String getItemLevelMessage(String transactionName, String msgTypName, Locale language) {
    return this.item.getItemLevelMessage(transactionName, msgTypName, language);
  }
  
  public String getItemLevelMessage(String transactionName, String msgTypName) {
    return this.item.getItemLevelMessage(transactionName, msgTypName);
  }
  
  public String getItemLevelMessageCodeID(String transactionType, String MessageType) {
    return this.item.getItemLevelMessageCodeID(transactionType, MessageType);
  }
  
  public void setAllItemLevelMessages(Map<String, List<MessageDTO>> itmMessageCollectionMap) {
    this.item.setAllItemLevelMessages(itmMessageCollectionMap);
  }
  
  public void setItemImage(ItemImageIfc itemImage) {
    this.item.setItemImage(itemImage);
  }
  
  public int getReturnPriceDays() {
    return this.returnPriceDays;
  }
  
  public void setReturnPriceDays(int returnPriceDays) {
    this.returnPriceDays = returnPriceDays;
  }
  
  protected void retrieveReturnPriceDays() {
    try {
      String days = Gateway.getProperty("application", "ReturnPriceDays", "0");
      this.returnPriceDays = Integer.parseInt(days);
    } catch (Exception e) {
      this.returnPriceDays = 0;
      logger.error("Failed to retrieve or convert to integer the 'ReturnPriceDays'.", e);
    } 
  }
  
  protected EYSDate getReturnPriceCutoffDate() {
    if (this.returnPriceCutoffDate == null) {
      this.returnPriceCutoffDate = new EYSDate();
      this.returnPriceCutoffDate.add(6, this.returnPriceDays * -1);
      this.returnPriceCutoffDate.setHour(0);
      this.returnPriceCutoffDate.setMinute(0);
      this.returnPriceCutoffDate.setSecond(0);
      this.returnPriceCutoffDate.setMillisecond(0);
    } 
    return this.returnPriceCutoffDate;
  }
  
  public ExternalOrderItemIfc getReturnExternalOrderItem() {
    return this.returnExternalOrderItem;
  }
  
  public void setReturnExternalOrderItem(ExternalOrderItemIfc returnExternalOrderItem) {
    this.returnExternalOrderItem = returnExternalOrderItem;
  }
  
  public String getManufacturerItemUPC() {
    return this.manufacturerItemUPC;
  }
  
  public void setManufacturerItemUPC(String manufacturerItemUPC) {
    this.manufacturerItemUPC = manufacturerItemUPC;
  }
  
  public boolean isOnClearance() {
    return this.onClearance;
  }
  
  public void setOnClearance(boolean onClearance) {
    this.onClearance = onClearance;
  }
  
  public PriceChangeIfc getEffectivePromotionalPrice() {
    PriceChangeIfc effTempPrice = getEffectiveTemporaryPriceChange();
    PriceChangeIfc effClrPrice = getEffectiveClearancePriceChange();
    return getLowestPromotionalPrice(effTempPrice, effClrPrice);
  }
  
  public PriceChangeIfc getEffectivePromotionalPrice(EYSDate date) {
    PriceChangeIfc effTempPrice = getEffectiveTemporaryPriceChange(date);
    PriceChangeIfc effClrPrice = getEffectiveClearancePriceChange(date);
    return getLowestPromotionalPrice(effTempPrice, effClrPrice);
  }
  
  public PriceChangeIfc getEffectivePromotionalPrice(EYSDate date, int pricingGroupId) {
    PriceChangeIfc effTempPrice = getEffectiveTemporaryPriceChange(date, pricingGroupId);
    PriceChangeIfc effClrPrice = getEffectiveClearancePriceChange(date, pricingGroupId);
    return getLowestPromotionalPrice(effTempPrice, effClrPrice);
  }
  
  private PriceChangeIfc getLowestPromotionalPrice(PriceChangeIfc effTempPrice, PriceChangeIfc effClrPrice) {
    PriceChangeIfc effPromoPrice = null;
    if (effClrPrice != null) {
      if (effTempPrice != null) {
        CurrencyIfc newTempPrice = effTempPrice.getNewPrice();
        CurrencyIfc newClrPrice = effClrPrice.getNewPrice();
        if (newTempPrice != null && newClrPrice != null)
          if (newTempPrice.compareTo(newClrPrice) < 0) {
            effPromoPrice = effTempPrice;
          } else {
            effPromoPrice = effClrPrice;
          }  
        if (newTempPrice != null && newClrPrice == null)
          effPromoPrice = effTempPrice; 
        if (newTempPrice == null && newClrPrice != null)
          effPromoPrice = effClrPrice; 
      } else {
        effPromoPrice = effClrPrice;
      } 
    } else {
      effPromoPrice = effTempPrice;
    } 
    return effPromoPrice;
  }
  
  public void addAvailableItemSize(ItemSizeIfc itemSize) {
    if (this.availableItemSizes == null)
      this.availableItemSizes = new ArrayList<>(); 
    this.availableItemSizes.add(itemSize);
    Collections.sort(this.availableItemSizes);
  }
  
  public ItemSizeIfc[] getAvailableItemSizes() {
    if (this.availableItemSizes == null)
      return null; 
    ItemSizeIfc[] array = new ItemSizeIfc[this.availableItemSizes.size()];
    this.availableItemSizes.toArray(array);
    return array;
  }
  
  public ExtendedItemDataContainer getExtendedItemDataContainer() {
    return this.extendedItemDataContainer;
  }
  
  public void setExtendedItemDataContainer(ExtendedItemDataContainer extendedItemDataContainer) {
    this.extendedItemDataContainer = extendedItemDataContainer;
  }
  
  public void setExtendedImageOnItem(ExtendedItemDataContainer extendedItemDataContainer) {
    if (extendedItemDataContainer != null && extendedItemDataContainer.getExtendedItemData() != null && extendedItemDataContainer.getExtendedItemData().getImageLocations() != null && !Util.isEmpty(extendedItemDataContainer.getExtendedItemData().getImageLocations().get(0)))
      if (getItem().getItemImage() == null) {
        ItemImageIfc itemImage = DomainGateway.getFactory().getItemImageInstance();
        itemImage.setImageLocation(extendedItemDataContainer.getExtendedItemData().getImageLocations().get(0));
        getItem().setItemImage(itemImage);
      } else if (!getItem().getItemImage().hasImageBlob() && Util.isEmpty(getItem().getItemImage().getImageLocation())) {
        getItem().getItemImage().setImageLocation(extendedItemDataContainer.getExtendedItemData().getImageLocations().get(0));
      }  
  }
  
  public void consolidate(PLUItemIfc differentPLUItem) {
    for (int i = this.advancedPricingRules.size() - 1; i >= 0; i--) {
      for (AdvancedPricingRuleIfc rule : differentPLUItem.getAdvancedPricingRules()) {
        if (rule.equals(this.advancedPricingRules.get(i))) {
          if (logger.isDebugEnabled())
            logger.debug("Consolidating rule " + rule.getRuleID()); 
          this.advancedPricingRules.set(i, rule);
        } 
      } 
    } 
  }
  
  public Object clone() {
    PLUItem newItem = new PLUItem();
    setCloneAttributes(newItem);
    return newItem;
  }
  
  public boolean equals(Object obj) {
    boolean result = false;
    PLUItem itemIn = null;
    try {
      if (this == obj) {
        result = true;
      } else {
        itemIn = (PLUItem)obj;
        if (Util.isObjectEqual(this.storeID, itemIn.storeID) && Util.isObjectEqual(this.item, itemIn.item) && Util.isObjectEqual(getPrice(), itemIn.getPrice()) && Util.isObjectEqual(getAdvancedPricingRuleIDs(), itemIn.getAdvancedPricingRuleIDs()) && Util.isObjectEqual(this.taxGroupName, itemIn.taxGroupName) && Util.isObjectEqual(getRelatedItemContainer(), itemIn.getRelatedItemContainer()) && Util.isObjectEqual(this.taxRules, itemIn.taxRules) && Util.isObjectEqual(this.taxGroupName, itemIn.taxGroupName) && Util.isObjectEqual(this.taxGroupName, itemIn.taxGroupName) && Util.isObjectEqual(this.returnPriceCutoffDate, itemIn.returnPriceCutoffDate) && Util.isObjectEqual(this.returnExternalOrderItem, itemIn.returnExternalOrderItem) && Util.isObjectEqual(this.extendedItemDataContainer, itemIn.extendedItemDataContainer) && this.restrictiveAge == itemIn.restrictiveAge && this.manufacturerID == itemIn.manufacturerID && this.returnPriceDays == itemIn.returnPriceDays && this.itemSizeRequired == itemIn.itemSizeRequired && this.onClearance == itemIn.isOnClearance())
          result = true; 
      } 
    } catch (Exception e) {
      result = false;
    } 
    return result;
  }
  
  public String toString() {
    StringBuilder strResult = new StringBuilder("Class:  PLUItem ");
    strResult.append("(Revision ").append(getRevisionNumber()).append(") @").append(hashCode()).append(Util.EOL).append("\titemID:        [").append(getItemID()).append("]").append(Util.EOL).append("\tiPosItemID:    [").append(getPosItemID()).append("]").append(Util.EOL).append("\tdescription:   [").append(getDescription(LocaleMap.getLocale("locale_Default"))).append("]").append(Util.EOL).append("\tprice:         [").append(getPrice()).append("]").append(Util.EOL).append("\ttaxGroupID:    [").append(getTaxGroupID()).append("]").append(Util.EOL).append("\ttaxable:       [").append(getTaxable()).append("]").append(Util.EOL).append("\titemWeight:    [").append(getItemWeight()).append("]").append(Util.EOL).append("\tClearanceInd:  [").append(isOnClearance() ? "YES" : "No").append("]").append(Util.EOL);
    if (getItemClassification() == null) {
      strResult.append("\tclassification:[null]").append(Util.EOL);
    } else {
      strResult.append("\tclassification:").append(Util.EOL).append(getItemClassification().toString());
    } 
    if (getRelatedItemContainer() == null) {
      strResult.append("\tRelatedItemContainer:[null]").append(Util.EOL);
    } else {
      strResult.append("\tRelatedItemContainer:").append(Util.EOL);
      strResult.append(getRelatedItemContainer().toString());
    } 
    strResult.append("\treturnPriceDays:[").append(this.returnPriceDays).append("]").append(Util.EOL);
    if (this.returnPriceCutoffDate == null) {
      strResult.append("\treturnPriceCutoffDate:[null]").append(Util.EOL);
    } else {
      strResult.append("\treturnPriceCutoffDate:[").append(this.returnPriceCutoffDate).append("]").append(Util.EOL);
    } 
    if (this.returnExternalOrderItem == null) {
      strResult.append("\treturnExternalOrderItem:[null]").append(Util.EOL);
    } else {
      strResult.append("\treturnExternalOrderItem:[").append(Util.EOL);
      strResult.append(this.returnExternalOrderItem.toString());
    } 
    if (this.extendedItemDataContainer == null) {
      strResult.append("\textendedItemDataContainer:[null]").append(Util.EOL);
    } else {
      strResult.append("\textendedItemDataContainer:[").append(Util.EOL);
      strResult.append(this.extendedItemDataContainer.toString());
    } 
    return strResult.toString();
  }

	/*
	 * @Override public String getliqcat() { // TODO Auto-generated method stub
	 * return liqcat; }
	 * 
	 * @Override public void setliqcat(String liqcat) { // TODO Auto-generated
	 * method stub this.liqcat= liqcat; }
	 * 
	 * @Override public String getliquom() { // TODO Auto-generated method stub
	 * return liquom; }
	 * 
	 * @Override public void setliquom(String liquom) { // TODO Auto-generated
	 * method stub this.liquom = liquom; }
	 */
  protected boolean empID = false;

	public boolean getEmpID() {
		return empID;
	}

	public void setEmpID(boolean empID) {
		this.empID = empID;
	}
	
	protected String spclEmpDisc;

	private boolean EmployeeDiscountAllowedFlag = true;

	public String getSpclEmpDisc() {
		return spclEmpDisc;
	}

	public void setSpclEmpDisc(String spclEmpDisc) {
		this.spclEmpDisc = spclEmpDisc;
	}

	@Override
	public void setEmployeeDiscountEligible(boolean EmployeeDiscountAllowedFlag) {
		getItemClassification().setEmployeeDiscountAllowedFlag(EmployeeDiscountAllowedFlag);
	}
}
