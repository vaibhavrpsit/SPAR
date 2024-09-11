package max.retail.stores.domain.event;

import oracle.retail.stores.domain.event.ItemPriceMaintenanceEventIfc;

public interface MAXItemPriceMaintenanceEventIfc extends
		ItemPriceMaintenanceEventIfc {
	
	  public String getAppliedOn();
			

		    /**
		     *Sets the PriceChangeEvent applied on.
		     *
		     * @param appliedOn
		     */
		    public void setAppliedOn(String appliedOn);
			

}
