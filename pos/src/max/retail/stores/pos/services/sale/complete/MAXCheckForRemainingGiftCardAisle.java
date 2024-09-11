package max.retail.stores.pos.services.sale.complete;

import max.retail.stores.pos.services.sale.MAXSaleCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

public class MAXCheckForRemainingGiftCardAisle extends PosLaneActionAdapter {

	public void traverse(BusIfc bus)
	{
		MAXSaleCargo cargo = (MAXSaleCargo)bus.getCargo();
		int index = cargo.getIndex();
		index++ ;
		cargo.setIndex(index);
		if(index<cargo.getLength())
			bus.mail("Retry");
		else
			bus.mail("Print");
	}
}
