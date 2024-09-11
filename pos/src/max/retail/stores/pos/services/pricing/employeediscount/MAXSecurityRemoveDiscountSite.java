package max.retail.stores.pos.services.pricing.employeediscount;

import max.retail.stores.pos.services.pricing.MAXPricingCargo;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
/**
 * @author kajal nautiyal Employee Discount validation through OTP
 */
public class MAXSecurityRemoveDiscountSite extends SiteActionAdapter{
	  public void arrive(BusIfc bus)
	    {
		 PricingCargo pricingCargo = (PricingCargo) bus.getCargo();
		 ((MAXPricingCargo)pricingCargo).setEmployeeDiscountID(null);
	
		 bus.mail("Failure", BusIfc.CURRENT);
	    }

}
