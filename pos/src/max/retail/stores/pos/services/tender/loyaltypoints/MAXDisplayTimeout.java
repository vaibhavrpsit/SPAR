/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0     Jan 06, 2016		Ashish Yadav		Online Points Redemption FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.loyaltypoints;

import max.retail.stores.domain.customer.MAXCustomer;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;


public class MAXDisplayTimeout extends PosSiteActionAdapter {

	/**
	 * @author mohd.arif
	 *for FES FES_OTP_Loyalty_Point_Redemption_in_POS_v3.0_.docx
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus) {
	POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		DialogBeanModel beanModel = new DialogBeanModel();
		
			beanModel.setResourceID("TimeoutMessageScreen");
			beanModel.setType(DialogScreensIfc.ERROR);
			beanModel.setButtonLetter(MAXDialogScreensIfc.ResendOTP, "Ok");
			
			
			TenderCargo cargo=(TenderCargo) bus.getCargo();	
			
			MAXCustomer cust =((MAXCustomer)cargo.getCustomer());
			cust.setLoyaltyTimeout(1);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
	}
}
