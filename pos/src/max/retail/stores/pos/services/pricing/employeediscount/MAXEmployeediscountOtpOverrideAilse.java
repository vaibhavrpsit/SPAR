package max.retail.stores.pos.services.pricing.employeediscount;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
//import max.retail.stores.domain.arts.MAXJdbcSaveManagerOverrideAccess;
//import max.retail.stores.domain.arts.MAXManagerOverrideAccessTransaction;
import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import max.retail.stores.domain.factory.MAXDomainObjectFactoryIfc;
//import max.retail.stores.domain.security.MAXManagerOverride;
//import max.retail.stores.domain.security.MAXManagerOverrideIfc;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.pricing.MAXPricingCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.employee.Role;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
public class MAXEmployeediscountOtpOverrideAilse extends PosLaneActionAdapter  {
	/**
	 * @author kajal nautiyal Employee Discount validation through OTP
	 */
	private static final long serialVersionUID = 1L;

	public void traverse(BusIfc bus) 
    {
    	boolean access = false;
    	String letter = CommonLetterIfc.OVERRIDE;

    	MAXPricingCargo cargo = (MAXPricingCargo)bus.getCargo();

        cargo.setAccessFunctionID(MAXRoleFunctionIfc.OtpCancelED);
       cargo.setAccessFunctionTitle("OtpCancelED");
     
       
		/*
		 * MAXManagerOverrideIfc managerOverride = ((MAXDomainObjectFactoryIfc)
		 * DomainGateway .getFactory()).getMAXManagerOverrideInstance(); //
		 * managerOverride.setAccessFunctionTitle("EmployeeDiscountOtpCancel");
		 * if(cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
		 * MAXSaleReturnTransaction
		 * transaction=(MAXSaleReturnTransaction)cargo.getTransaction();
		 * 
		 * managerOverride.setAdditionalInfo(String.valueOf(transaction.getEmpID()));
		 * //managerOverride.setAdditionalInfo(transaction.getLocale());
		 * //managerOverride.setAdditionalValue(String.valueOf(transaction.getEmpID()));
		 * managerOverride.setAdditionalValue(transaction.getLocale());
		 * managerOverride.setAccessFunctionTitle("OtpCancelED");
		 * //managerOverride.setisEmployeeDiscount(true);
		 * //System.out.println(managerOverride.getAdditionalInfo());
		 * //System.out.println(managerOverride.getAdditionalValue());
		 * 
		 * } else { if(cargo.getTransaction() instanceof MAXLayawayTransaction) {
		 * 
		 * MAXLayawayTransaction
		 * transaction=(MAXLayawayTransaction)cargo.getTransaction();
		 * 
		 * managerOverride.setAdditionalInfo(String.valueOf(transaction.getEmpID()));
		 * //managerOverride.setAdditionalInfo(transaction.getLocale());
		 * //managerOverride.setAdditionalValue(String.valueOf(transaction.getEmpID()));
		 * managerOverride.setAdditionalValue(transaction.getLocale());
		 * managerOverride.setAccessFunctionTitle("OtpCancelED"); //
		 * managerOverride.setisEmployeeDiscount(true);
		 * 
		 * } }
		 */
		
	bus.mail(new Letter(letter), BusIfc.CURRENT);
    }
}
