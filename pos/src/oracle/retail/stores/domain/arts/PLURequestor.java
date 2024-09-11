package oracle.retail.stores.domain.arts;

import java.io.Serializable;
import java.util.EnumSet;

public class PLURequestor implements Serializable, Cloneable {
  private static final long serialVersionUID = -1113815327726188960L;
  
  private static final String FIELD_MANUFACTURER = "manufacturer";
  
  private static final String FIELD_PLANOGRAM = "planogram";
  
  private EnumSet<RequestType> requestTypeSet;
  
  public enum RequestType {
    Price, Planogram, LocalizedDescription, ItemImage, Manufacturer, AdvancedPricingRules, POSDepartment, TaxRules, RelatedItems, ItemMessages, KitComponents, StockItem, Color, Style, Size, MerchandiseHierarchy;
  }
  
  public PLURequestor() {
    this(true);
    removeRequestType(RequestType.Color);
    removeRequestType(RequestType.Size);
    removeRequestType(RequestType.Style);
    removeRequestType(RequestType.MerchandiseHierarchy);
  }
  
  public PLURequestor(boolean value) {
    if (value) {
      this.requestTypeSet = EnumSet.allOf(RequestType.class);
    } else {
      this.requestTypeSet = EnumSet.noneOf(RequestType.class);
    } 
  }
  
  protected PLURequestor(String fieldsToInclude) {
    this(true);
    if (fieldsToInclude != null && fieldsToInclude.indexOf("manufacturer") == -1)
      removeRequestType(RequestType.Manufacturer); 
    if (fieldsToInclude != null && fieldsToInclude.indexOf("planogram") == -1)
      removeRequestType(RequestType.Planogram); 
  }
  
  protected PLURequestor(boolean selectDiscountRules, String fieldsToInclude) {
    this(fieldsToInclude);
    if (!selectDiscountRules)
      removeRequestType(RequestType.AdvancedPricingRules); 
  }
  
  public void addRequestType(RequestType type) {
    this.requestTypeSet.add(type);
  }
  
  public void removeRequestType(RequestType type) {
    this.requestTypeSet.remove(type);
  }
  
  public boolean containsRequestType(RequestType type) {
    return this.requestTypeSet.contains(type);
  }
  
  public Object clone() {
    PLURequestor newPLURequestor = new PLURequestor();
    newPLURequestor.requestTypeSet = this.requestTypeSet.clone();
    return newPLURequestor;
  }
  
  public boolean equals(Object obj) {
    boolean equals = false;
    if (obj != null && obj instanceof PLURequestor) {
      PLURequestor requestor = (PLURequestor)obj;
      if (this.requestTypeSet.equals(requestor.requestTypeSet))
        equals = true; 
    } 
    return equals;
  }
  
//Changes Starts by Kamlesh Pant for SpecialEmpDiscount
  protected boolean empID = false;

  public boolean isEmpID() {
	return empID;
  }

  public void setEmpID(boolean empID) {
	this.empID = empID;
  }
//Changes Ends for SpecialEmpDiscount
}
