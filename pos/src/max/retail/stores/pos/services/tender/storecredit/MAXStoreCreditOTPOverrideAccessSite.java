/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *
 *	Rev 1.0 	May 14, 2024			Kamlesh Pant		Store Credit OTP:
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.storecredit;

import java.util.HashMap;

import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

public class MAXStoreCreditOTPOverrideAccessSite extends SiteActionAdapter  {
//OTP Override for store credit
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus) 
    {
    	boolean access = false;
		
		
    	String letter = CommonLetterIfc.OVERRIDE;
 
    	MAXTenderCargo cargo = (MAXTenderCargo)bus.getCargo();
    	HashMap<String, Object> tenderAttributes = cargo.getTenderAttributes();
		 if(tenderAttributes.get("STATE").equals("ISSUED")) {
			 cargo.setAccessFunctionID(MAXRoleFunctionIfc.OtpCancelCNI);
    	SecurityManagerIfc securityManager = (SecurityManagerIfc) Gateway.getDispatcher().getManager(SecurityManagerIfc.TYPE);
		access = securityManager.checkAccess(cargo.getAppID(), cargo.getAccessFunctionID());
		if (access) {
			letter = CommonLetterIfc.SUCCESS;
		}
		else
		{
 
			letter = CommonLetterIfc.OVERRIDE;
 		
}
    
 }
		 
  else if(tenderAttributes.get("STATE").equals("REDEEM"))
  {
	  cargo.setAccessFunctionID(MAXRoleFunctionIfc.OtpCancelCNR);
	  SecurityManagerIfc securityManager = (SecurityManagerIfc) Gateway.getDispatcher().getManager(SecurityManagerIfc.TYPE);
		access = securityManager.checkAccess(cargo.getAppID(), cargo.getAccessFunctionID());
		if (access) {
			letter = CommonLetterIfc.SUCCESS;
		}
		else 
		{
	  
			letter = CommonLetterIfc.OVERRIDE;
     
     } 
		}
		
	bus.mail(new Letter(letter), BusIfc.CURRENT);
    }
}