package max.retail.stores.domain.bakery;

import java.math.BigDecimal;

public class MAXBakeryItem implements MAXBakeryItemIfc{

	  private static final long serialVersionUID = 1L;
	  private String itemDesc;
	  private String itemId;
	  private String categoryDesc;
	  private BigDecimal quantity;
	
	  
	  public Object clone()
	  {
		  MAXBakeryItem c = new MAXBakeryItem();

	    setCloneAttributes(c);

	    return c;
	  }

	  public void setCloneAttributes(MAXBakeryItem newClass) {
	    if (this.itemId != null) {
	      newClass.setItemDesc(this.itemDesc);
	      newClass.setItemId(this.itemId);
	      newClass.setQuantity(this.quantity);
	      newClass.setCategoryDesc(this.categoryDesc);
	      
	    }
	  }

	  
	@Override
	public String getItemDesc() {
		return this.itemDesc;
	}

	@Override
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
		
	}

	@Override
	public String getItemId() {
		
		return this.itemId;
	}

	@Override
	public void setItemId(String id) {
		this.itemId = id;
		
	}

	@Override
	public String getCategoryDesc() {
		
		return this.categoryDesc;
	}

	@Override
	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
		
	}

	@Override
	public BigDecimal getQuantity() {
		return this.quantity;
	}

	@Override
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
		
	}
	
	public boolean equals(Object obj) { // begin equals()
		boolean isEqual = false;
		
		if(obj instanceof MAXBakeryItem)
		{
			if(((this.itemDesc).equals(((MAXBakeryItem)obj).itemDesc)) && ((this.itemId).equals(((MAXBakeryItem)obj).itemId))
			&&	((this.categoryDesc).equals(((MAXBakeryItem)obj).categoryDesc)))
			{
				isEqual = true;
			}
		}
		return isEqual;
	}

}
