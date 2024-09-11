/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *
 *	Rev 1.0 	May 14, 2024			Kamlesh Pant		Store Credit OTP:
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.storecredit;

//import java.util.HashMap;

//import max.retail.stores.pos.services.pricing.MAXPricingCargo;
//import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
//import oracle.retail.stores.pos.ado.tender.TenderStoreCreditADO;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;

public class MAXEnterOtpForSC extends PosSiteActionAdapter{

		private static final long serialVersionUID = 8291261569953634094L;
		
		
		public void arrive(BusIfc bus) {
		
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			//MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
			// HashMap tenderAttributes = cargo.getTenderAttributes();
			// TenderStoreCreditADO storeCreditTender = (TenderStoreCreditADO)cargo.getTenderADO();
			LineItemsModel beanModel = new LineItemsModel();
			DefaultTimerModel timeModel = new DefaultTimerModel(bus, false);
			timeModel.setActionName("Timeout");
			ui.showScreen(MAXPOSUIManagerIfc.ENTER_SC_OTP, beanModel);
				}

	}