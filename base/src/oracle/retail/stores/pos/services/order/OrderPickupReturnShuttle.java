package oracle.retail.stores.pos.services.order;

import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.services.order.common.OrderShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.order.pickup.PickupOrderCargo;

public class OrderPickupReturnShuttle extends OrderShuttle {
    
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1L;

    /** Calling PickupOrderCargo cargo */
    protected PickupOrderCargo orderPickupCargo = null;

    // ----------------------------------------------------------------------
    /**
     * Loads the item cargo.
     * <P>
     *
     * @param bus Service Bus to copy cargo from.
     */
    // ----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        orderPickupCargo = (PickupOrderCargo)bus.getCargo();
    }

    // ----------------------------------------------------------------------
    /**
     * Transfers the item cargo to the pickup delivery order cargo for the
     * modify item service.
     * <P>
     *
     * @param bus Service Bus to copy cargo to.
     */
    // ----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        OrderCargo orderCargo = (OrderCargo)bus.getCargo();
        if (orderPickupCargo.getTransaction() != null)
        {
            orderCargo.setOrderTransaction(orderPickupCargo.getTransaction());
        }
    }

}
