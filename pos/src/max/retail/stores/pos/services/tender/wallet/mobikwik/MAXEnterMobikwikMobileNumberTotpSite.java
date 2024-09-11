/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Jan 06, 2017		Atul Shukla		Changes for pos mobikwik Integration FES
 *
 ********************************************************************************/
package max.retail.stores.pos.services.tender.wallet.mobikwik;

import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;

public class MAXEnterMobikwikMobileNumberTotpSite extends PosSiteActionAdapter
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
		
		if(cargo.getTransaction().getCustomer()!=null && cargo.getTransaction().getCustomer().getContact().getPhoneList().size() >=1)
		{	
				
		PhoneIfc phone =cargo.getTransaction().getCustomer().getContact().getPhoneList().get(0);
		mobileNumber = phone.getPhoneNumber();
		model.setValue("MobileNumberField", mobileNumber);
					
		
		}
		
			uiManager.showScreen(MAXPOSUIManagerIfc.ENTER_WALLET_MOBILE_NUMBER_AND_TOTP_MOBIKWIK,model);
		
		//}
		/**Rev 1.1 Changes End */
		}
	}


