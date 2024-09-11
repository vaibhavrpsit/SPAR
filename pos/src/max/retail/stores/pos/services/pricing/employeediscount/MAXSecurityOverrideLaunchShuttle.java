package max.retail.stores.pos.services.pricing.employeediscount;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.pos.services.admin.security.override.SecurityOverrideCargo;
import org.apache.log4j.Logger;

import max.retail.stores.pos.services.modifytransaction.discount.MAXModifyTransactionDiscountCargo;

public class MAXSecurityOverrideLaunchShuttle implements ShuttleIfc {
  static final long serialVersionUID = 2873368255293267735L;
  
  protected static final Logger logger = Logger.getLogger(MAXSecurityOverrideLaunchShuttle.class);
  
  public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
  
  public static final String SHUTTLENAME = "SecurityOverrideLaunchShuttle";
  
  protected UserAccessCargoIfc callingCargo = null;
  /**
	 * @author kajal nautiyal Employee Discount validation through OTP
	 */
  public void load(BusIfc bus) {
    this.callingCargo = (UserAccessCargoIfc)bus.getCargo();
    }
  
  public void unload(BusIfc bus) {
    SecurityOverrideCargo calledCargo = (SecurityOverrideCargo)bus.getCargo();
    calledCargo.setLastOperator(this.callingCargo.getOperator());
    calledCargo.setAccessFunctionID(this.callingCargo.getAccessFunctionID());
    calledCargo.setAccessFunctionTitle(this.callingCargo.getAccessFunctionTitle());
    calledCargo.setResourceID(this.callingCargo.getResourceID());
    }
  
  public String getRevisionNumber() {
    return Util.parseRevisionNumber("$Revision: /rgbustores_13.4x_generic_branch/1 $");
  }
}
