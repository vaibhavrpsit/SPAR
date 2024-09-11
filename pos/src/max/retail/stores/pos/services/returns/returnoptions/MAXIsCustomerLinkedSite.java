/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Jan 06, 2017		Ashish Yadav		Changes for Online redemption loyalty OTP FES	
 *
 ********************************************************************************/

//<!-- MAX Rev 1.0 Change : Start -->
package max.retail.stores.pos.services.returns.returnoptions;

import max.retail.stores.domain.customer.MAXCustomer;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.returns.returnoptions.ReturnOptionsCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXIsCustomerLinkedSite extends PosSiteActionAdapter{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus)
	
	{
		 POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		 ReturnOptionsCargo cargo=(ReturnOptionsCargo)bus.getCargo();
		 /*Rev 1.1 start*/
		 // Changes starts for Rev 1.0 (Ashish : Loyalty OTP)
		 if(cargo.getTransaction()!=null && cargo.getTransaction().getCustomer()!= null &&
				 (((((MAXCustomer)(cargo.getTransaction().getCustomer())).getLoyaltyCardNumber())!= null) 
						 ||((cargo.getTransaction().getCustomer().getCustomerID() != null) &&
				 !cargo.getTransaction().getCustomer().getCustomerID().equals(""))))
		 {
			 // Changes starts for Rev 1.0 (Ashish : Loyalty OTP)
		 /*Rev 1.1 End*/
			 bus.mail(new Letter("Continue"),BusIfc.CURRENT);
		 }
		 else
		 {
			 
			 DialogBeanModel dModel = new DialogBeanModel();
             dModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
             dModel.setResourceID("CustomerIsNotLinked");
             dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "NoLink");
             ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dModel);
		 }
	}

}

//<!-- MAX Rev 1.0 Change : end -->