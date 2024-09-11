package max.retail.stores.pos.services.sale.advsearch;

import max.retail.stores.pos.services.inquiry.iteminquiry.MAXItemInquiryCargo;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;

public class MAXAdvSearchLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8200351090666433676L;
	
	protected MAXSaleCargo maxSaleCargo= null;

	public void load(BusIfc bus)
    {
        // load financial cargo
        super.load(bus);
        maxSaleCargo = (MAXSaleCargo)bus.getCargo();
        
    }
	
	public void unload(BusIfc bus)
    {
        // unload financial cargo
        super.unload(bus);
        MAXItemInquiryCargo itemInquiryCargo = (MAXItemInquiryCargo) bus.getCargo();
        itemInquiryCargo.setTransaction(maxSaleCargo.getTransaction());
        itemInquiryCargo.setAddSearchPLUItem(true);
    
    }

}
