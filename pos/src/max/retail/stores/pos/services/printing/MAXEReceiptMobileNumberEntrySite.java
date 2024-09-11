/********************************************************************************
 *   
 *	Copyright (c) 2019 MAX SPAR Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	Sep 10, 2019		Purushotham Reddy 	Changes for E-Receipt Integration With Karnival 
 *
 ********************************************************************************/

package max.retail.stores.pos.services.printing;

/**
@author Purushotham Reddy Sirison
**/
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

public class MAXEReceiptMobileNumberEntrySite extends PosSiteActionAdapter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus)
	{
		DataInputBeanModel model =new DataInputBeanModel();
		POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		MAXPrintingCargo cargo = (MAXPrintingCargo)bus.getCargo();		
		String mobileNumber = "";
		if(cargo.getTransaction().getCustomer()!=null && 
				cargo.getTransaction().getCustomer().getContact().getPhoneList().size() >= 1){
			PhoneIfc phone =cargo.getTransaction().getCustomer().getContact().getPhoneList().get(0);
			mobileNumber = phone.getPhoneNumber();
			model.setValue("MobileNumberField", mobileNumber);
		
		}
			uiManager.showScreen(MAXPOSUIManagerIfc.ERECEIPT_MOBILE_NUMBER_SCREEN,model);
			
	}
}