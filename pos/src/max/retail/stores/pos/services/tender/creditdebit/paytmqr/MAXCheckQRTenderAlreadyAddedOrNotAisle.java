/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *      Copyright (c) 2022-2023 MAXHyperMarket, Inc.    All Rights Reserved.
 *     
 * Rev 1.0 		Apr 30,2022		Kamlesh Pant   Paytm Integration
 * Initial revision.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit.paytmqr;

import max.retail.stores.domain.arts.MAXPaytmDataTransaction;
import max.retail.stores.domain.paytm.MAXPaytmResponse;
import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.domain.tender.paytmqr.MAXPaytmQRCodeTenderConstants;
import max.retail.stores.pos.ado.tender.MAXTenderCreditADO;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.domain.arts.ARTSTill;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXCheckQRTenderAlreadyAddedOrNotAisle extends LaneActionAdapter{

	private static final long serialVersionUID = -2929738989815949986L;

	public void traverse(BusIfc bus) {
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		boolean isTenderExists = false;
		try {
			//Create connection
			
			MAXTenderCargo tenderCargo = (MAXTenderCargo)bus.getCargo();
			
			TenderADOIfc[] tenderVector = tenderCargo.getCurrentTransactionADO().getTenderLineItems(TenderLineItemCategoryEnum.ALL);
			if (tenderVector != null && tenderVector.length > 0)
			{
				for (int i=0; i < tenderVector.length; i++)
				{
					TenderADOIfc tenderObject = tenderVector[i];
					if (tenderObject instanceof MAXTenderCreditADO)
					{
						MAXTenderChargeIfc tenderChargeObj = (MAXTenderChargeIfc) tenderObject.toLegacy();
						if(tenderChargeObj.getCardType().equalsIgnoreCase("PAYTM_QR"))
						{
							if(cargo.getPaytmQRCodeResp() != null && cargo.getPaytmQRCodeResp().getOrderId() != null) {
							if(tenderChargeObj.getOrderNumber().equalsIgnoreCase(cargo.getPaytmQRCodeResp().getOrderId()) && tenderChargeObj.getMerchantTransactionId().equalsIgnoreCase(cargo.getPaytmQRCodeResp().getTxnId())) {
								isTenderExists = true;	
							}
							}
						}
					}
				}
			}	
			
			if(isTenderExists)
			{
				DialogBeanModel dialogModel = new DialogBeanModel();
				String[] messgArray = new String[1];
				
				messgArray[0] = "Paytm QR Already added for the order Id.";
				dialogModel.setArgs(messgArray);
				dialogModel.setResourceID(MAXPaytmQRCodeTenderConstants.PAYTMQRCODEPENDINGERROR);
				dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"failure");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				return;
			}
			else
			{
				bus.mail("AddTender");
			}
		}
		catch(Exception e)
		{
			logger.error("Error while checking for paytm qrcode already Exists or not. " + e.getMessage());
		}
		
	}

}
