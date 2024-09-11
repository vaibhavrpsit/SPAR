/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2022-2023 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *     
 * Rev 1.0 		Apr 11,2017		Nadia Arora (EYLLP)   Paytm Integration
 * Initial revision.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit.paytmqr;

import max.retail.stores.domain.arts.MAXPaytmDataTransaction; 
import max.retail.stores.domain.paytm.MAXPaytmResponse;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.domain.arts.ARTSTill;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXCheckForDatabaseConnectivityQRCodeAisle extends LaneActionAdapter{

	private static final long serialVersionUID = -2929738989815949986L;

	public void traverse(BusIfc bus) {
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		//System.out.println("MAXCheckForDatabaseConnectivityQRCodeAisle :"+cargo.getTransaction());
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		
		try {
			//Create connection

			/*
			 * MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
			 * ARTSTill till = new ARTSTill(cargo.getTillID(),
			 * cargo.getStoreStatus().getStore().getStoreID()); boolean dbStatus =
			 * paytmTrans.verifyDatabaseStatus(till); if(! dbStatus) { MAXPaytmResponse
			 * respDataException = new MAXPaytmResponse();
			 * respDataException.setDataException(Boolean.TRUE); DialogBeanModel dialogModel
			 * = new DialogBeanModel(); dialogModel.setResourceID("ServerOffline");
			 * dialogModel.setType(DialogScreensIfc.ERROR);
			 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
			 * ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			 * 
			 * return; } else {
			 */
				bus.mail("CreateQRCode");
			//}
		}
		catch(Exception e)
		{
			logger.error("Error while checking database connection for paytm qrcode before create QRCode " + e.getMessage());
		}
		
	}

}
