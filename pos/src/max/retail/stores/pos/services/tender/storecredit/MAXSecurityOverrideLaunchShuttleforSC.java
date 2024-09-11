/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *
 *	Rev 1.0 	May 14, 2024		Kamlesh Pant		Store Credit OTP:
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.storecredit;

import java.util.HashMap;

import org.apache.log4j.Logger;

import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import max.retail.stores.domain.factory.MAXDomainObjectFactoryIfc;
import max.retail.stores.domain.manageroverride.MAXManagerOverride;
//import max.retail.stores.domain.security.MAXManagerOverrideIfc;
import max.retail.stores.domain.manageroverride.MAXManagerOverrideIfc;
import max.retail.stores.domain.tender.MAXTenderStoreCreditIfc;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.ado.tender.MAXTenderConstants;
import max.retail.stores.pos.services.pricing.employeediscount.MAXSecurityOverrideLaunchShuttle;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.pos.services.admin.security.override.SecurityOverrideCargo;

@SuppressWarnings("deprecation")
public class MAXSecurityOverrideLaunchShuttleforSC implements ShuttleIfc {
	  static final long serialVersionUID = 2873368255293267735L;
	  
	  protected static final Logger logger = Logger.getLogger(MAXSecurityOverrideLaunchShuttleforSC.class);
	  
	  protected UserAccessCargoIfc callingCargo = null;
	  String SCnumber = null;
	  
	  public void load(BusIfc bus) {
	    this.callingCargo = (UserAccessCargoIfc)bus.getCargo();
	    //MAXManagerOverrideIfc managerOverride = ((MAXDomainObjectFactoryIfc) DomainGateway.getFactory()).getMAXManagerOverrideInstance();
	   MAXManagerOverrideIfc managerOverride = new MAXManagerOverride();
	    MAXTenderCargo cargo = (MAXTenderCargo)bus.getCargo();
	    HashMap<String, Object> tenderAttributes = cargo.getTenderAttributes();
		 String mobile = (String) tenderAttributes.get(MAXTenderConstants.mobileNumber);
		 if(tenderAttributes.get("STATE").equals("ISSUED")) {
			 cargo.setAccessFunctionID(MAXRoleFunctionIfc.OtpCancelCNI);
		    	cargo.setAccessFunctionTitle("OtpCancelCNI");
		 
		if(cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
	     	MAXSaleReturnTransaction transaction=(MAXSaleReturnTransaction)cargo.getTransaction();
	     	if (transaction.getOgTransaction() != null) 
	     	managerOverride.setAdditionalInfo(transaction.getOgTransaction());
	     	else
	     		managerOverride.setAdditionalInfo("")	;
	     	managerOverride.setAdditionalValue(String.valueOf(transaction.getCustMobileforOTP()));
	 		managerOverride.setAccessFunctionTitle("OtpCancelCNI");
	 		
	}
	     else {
	     	if(cargo.getTransaction() instanceof MAXLayawayTransaction) {
	     		MAXLayawayTransaction	transaction=(MAXLayawayTransaction)cargo.getTransaction();
	     		if (transaction.getOgTransaction() 
	         			!= null) 
	     		managerOverride.setAdditionalInfo(transaction.getOgTransaction());
	     		else
	         		managerOverride.setAdditionalInfo("")	;
	         	managerOverride.setAdditionalValue(String.valueOf(transaction.getCustMobileforOTP()));
	     		managerOverride.setAccessFunctionTitle("OtpCancelCNI");
	                }
	     } 
	  }
		 else if(tenderAttributes.get("STATE").equals("REDEEM"))
		 {
			 cargo.setAccessFunctionID(MAXRoleFunctionIfc.OtpCancelCNR);
			  cargo.setAccessFunctionTitle("OtpCancelCNR");
			  RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
				MAXTenderStoreCreditIfc tscRedeem = (MAXTenderStoreCreditIfc) txnADO.getTenderStoreCreditIfcLineItem();
				SCnumber=  tscRedeem.getStoreCreditID();	
		     	managerOverride.setAdditionalValue(String.valueOf(mobile));
				managerOverride.setAdditionalInfo(String.valueOf(SCnumber));
				managerOverride.setAccessFunctionTitle("OtpCancelCNR");
		 }
			}
	  
	  public void unload(BusIfc bus) {
	    SecurityOverrideCargo calledCargo = (SecurityOverrideCargo)bus.getCargo();
	    calledCargo.setLastOperator(this.callingCargo.getOperator());
	    calledCargo.setAccessFunctionID(this.callingCargo.getAccessFunctionID());
	    calledCargo.setAccessFunctionTitle(this.callingCargo.getAccessFunctionTitle());
	    calledCargo.setResourceID(this.callingCargo.getResourceID());
	    }

}
