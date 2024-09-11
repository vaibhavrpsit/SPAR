package max.retail.stores.pos.services.sale;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

public class MAXIsExistingTICCustomerSignal implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -686917734389915266L;



    
    
    public boolean roadClear(BusIfc bus)
    {
    	MAXSaleCargoIfc cargo = (MAXSaleCargoIfc) bus.getCargo();
    	
        return cargo.getTicCustomer().getExistingCustomer().booleanValue();
    }


}
