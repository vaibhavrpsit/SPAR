/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved. 
  Rev 1.0   Kamlesh Pant		09/12/2022		SpecialEmpDiscount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale;

import java.util.Arrays;
import java.util.Vector;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXEmployeeDiscountBeanModel;
import oracle.retail.stores.domain.stock.PLUItem;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXEmployeeIDSite extends PosSiteActionAdapter {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus)
    {
		//MAXSaleCargo cargo = (MAXSaleCargo) bus.getCargo();		
		//cargo.setEmpID(true);
//		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
//		DataInputBeanModel model = new DataInputBeanModel();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		MAXEmployeeDiscountBeanModel beanModel=new MAXEmployeeDiscountBeanModel();
			
			 ParameterManagerIfc parameterManager = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
		        String parameter = "EmployeeCompanyNameList";
		        Vector<String>  v = null;
		        try
		        {
		           // parameter = ParameterConstantsIfc.TENDERAUTHORIZATION_CallReferralList;
		            String[] companyName = parameterManager.getStringValues(parameter);
		            v = new Vector(Arrays.asList(companyName));
		        }
		        catch(ParameterException pe)
		        {
		            logger.warn("Couldn't retrieve parameter: " + parameter, pe);
		        }
              beanModel.setCompanyName(v);
		ui.showScreen(MAXPOSUIManagerIfc.EMPLOYEE_ID,beanModel);
    }
}
