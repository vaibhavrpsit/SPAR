package max.retail.stores.pos.services.order.pickup;

import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.pos.services.order.pickup.PickupOrderCargo;

public class MAXPickupOrderCargo extends PickupOrderCargo{
	
	// Changes starts for code merging(adding below line as it is not present in base 14)
	protected OrderIfc savedOrder = null;
	public void setSavedOrder(OrderIfc value)
    {
        savedOrder = value;
    }

}
