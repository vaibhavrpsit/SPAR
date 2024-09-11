/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 * Rev 1.0  08 Nov, 2016              Nadia              MAX-StoreCredi_Return requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.returns.returnitem;

import max.retail.stores.pos.services.sale.multiplemrp.MAXMultipleMRPCargo;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.returns.returnitem.ReturnItemCargo;

public class MAXMultipleMRPReturnShuttle
  implements ShuttleIfc
{
  public static final String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/1 $";
  protected MAXMultipleMRPCargo mCargo = null;
  protected PLUItemIfc item = null;
  
  public void load(BusIfc bus)
  {
    MAXMultipleMRPCargo cargo = (MAXMultipleMRPCargo)bus.getCargo();
    this.item = cargo.getPLUItem();
  }
  
  public void unload(BusIfc bus)
  {
    ReturnItemCargo cargo = (ReturnItemCargo)bus.getCargo();
    if (cargo.getPLUItem() != null) {
      cargo.setPLUItem(this.item);
    }
  }
}
