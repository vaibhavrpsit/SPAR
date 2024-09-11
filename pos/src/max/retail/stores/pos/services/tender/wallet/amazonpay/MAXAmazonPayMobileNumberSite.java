/********************************************************************************
 *   
 *	Copyright (c) 2019 MAX SPAR Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	Jul 01, 2019		Purushotham Reddy 	Changes for POS_Amazon Pay Integration 
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.wallet.amazonpay;

/**
@author Purushotham Reddy Sirison
**/
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

public class MAXAmazonPayMobileNumberSite extends PosSiteActionAdapter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus)
	{
		DataInputBeanModel model =new DataInputBeanModel();
		POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
	
		MAXTenderCargo cargo = (MAXTenderCargo)bus.getCargo();		
		String mobileNumber = "";
		//Added by Kumar Vaibhav for Amazon Pay barcode Integration
		String barcode="";
		//end
		
		if(cargo.getTransaction().getCustomer()!=null && 
				cargo.getTransaction().getCustomer().getContact().getPhoneList().size() >= 1){	
			PhoneIfc phone =cargo.getTransaction().getCustomer().getContact().getPhoneList().get(0);
			mobileNumber = phone.getPhoneNumber();
			model.setValue("MobileNumberField", mobileNumber);
			//Added by Kumar Vaibhav for Amazon Pay Barcode Integration
			model.setValue("BarcodeField", barcode);
			/*end*/
		
		}
			uiManager.showScreen(MAXPOSUIManagerIfc.ENTER_AMAZON_PAY_MOBILE_NUMBER_SCREEN,model);
	}
}