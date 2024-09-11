package max.retail.stores.domain.bakery;

import java.math.BigDecimal;

import oracle.retail.stores.domain.utility.EYSDomainIfc;

public interface MAXBakeryItemIfc  extends EYSDomainIfc {

	  public abstract String getItemDesc();

	  public abstract void setItemDesc(String itemDesc);

	  public abstract String getItemId();

	  public abstract void setItemId(String id);

	  public abstract String getCategoryDesc();

	  public abstract void setCategoryDesc(String categoryDesc);
	  
	  public abstract BigDecimal getQuantity();

	  public abstract void setQuantity(BigDecimal quantity);
	
}
