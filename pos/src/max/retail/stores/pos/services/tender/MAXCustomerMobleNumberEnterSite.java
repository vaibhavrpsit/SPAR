package max.retail.stores.pos.services.tender;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXCustomerMobleNumberEnterSite extends PosSiteActionAdapter {
	  
	private static final long serialVersionUID = 1L;


    public void arrive(BusIfc bus)
    {
    
    	POSBaseBeanModel beanmodel=new POSBaseBeanModel();
    	POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
    	ui.showScreen(MAXPOSUIManagerIfc.MCOUPON_PHONE_NUMBER,beanmodel);
    	
    }


 
}
