package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.modifyitem.relateditem.RelatedItemCargo;

public class RelatedItemReturnShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    /**
     * Serialized Item Cargo
     */
    protected RelatedItemCargo relatedItemCargo;


    /**
     * Loads the Sale Cargo.
     * @param bus Service Bus to copy cargo from.
     */
    public void load(BusIfc bus)
    {
        relatedItemCargo = (RelatedItemCargo)bus.getCargo();

    }

    /**
     * Copies the SerializedItemCargo contents info to the Salecargo.
     * @param  bus     Service Bus to copy cargo to.
     */
    public void unload(BusIfc bus)
    {
      SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
      
      for (SaleReturnLineItemIfc orderLineItem: relatedItemCargo.getOrderLineItems() )
      {
          cargo.addOrderLineItem(orderLineItem);
      }

    }

}
