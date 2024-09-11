package max.retail.stores.pos.services.pricing.employeediscount;

import java.util.HashMap;

import max.retail.stores.domain.arts.MAXConfigParameterTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.utility.MAXConfigParametersIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.pricing.AbstractDiscountOptionsSite;
/**
 * @author kajal nautiyal Employee Discount validation through OTP Enable check
 */
public final class MAXCheckEmpOtpStatusSite extends AbstractDiscountOptionsSite{
	boolean check= false;
	
	 public void arrive(BusIfc bus)
	  {
		 
		 check=getConfigparameter();
			
			if(check) {
				
				bus.mail("EmpDisOtp");
			}
			else {
				bus.mail("Continue");
				
			}
	  
	  }
		private Boolean getConfigparameter( ) {

			MAXConfigParameterTransaction configTransaction = new MAXConfigParameterTransaction();
			MAXConfigParametersIfc configParameters = null;
			configTransaction = (MAXConfigParameterTransaction) DataTransactionFactory
					.create(MAXDataTransactionKeys.CONFIG_PARAMETER_TRANSACTION);

			try {
				configParameters = configTransaction.selectConfigParameters();
				
			} catch (DataException e1) {
				e1.printStackTrace();
			}

			boolean test= false;
			test=configParameters.isEmpOtpEnableCheck();
			
			String s1=String.valueOf(test);  
			//System.out.println(s1);

			return configParameters.isEmpOtpEnableCheck();

		}
	
	private String toString(boolean test) {
			// TODO Auto-generated method stub
			return null;
		}
	private MAXConfigParametersIfc getAllConfigparameter() {

		MAXConfigParameterTransaction configTransaction = new MAXConfigParameterTransaction();
		MAXConfigParametersIfc configParameters = null;
		configTransaction = (MAXConfigParameterTransaction) DataTransactionFactory
				.create(MAXDataTransactionKeys.CONFIG_PARAMETER_TRANSACTION);

		try {
			configParameters = configTransaction.selectConfigParameters();
		} catch (DataException e1) {
			e1.printStackTrace();
		}
		return configParameters;
	}

}
