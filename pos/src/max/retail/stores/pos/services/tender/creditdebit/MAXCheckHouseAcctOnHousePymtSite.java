/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.0  	21 Dec, 2016              Ashish Yadav              Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModelIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderCreditADO;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXCheckHouseAcctOnHousePymtSite extends PosSiteActionAdapter {
	public static final String revisionNumber = "$Revision: 3$";

	public void arrive(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		String cardNumber = null;
		MSRModelIfc msrModel = (MSRModelIfc) cargo.getTenderAttributes().get("MSR_MODEL");
		EncipheredCardDataIfc cardData = (EncipheredCardDataIfc)cargo.getTenderAttributes().get(TenderConstants.ENCIPHERED_CARD_DATA);
		if (msrModel != null) {
			cardNumber = msrModel.getAccountNumber();
		} else {
			cardNumber = (String) cargo.getTenderAttributes().get("NUMBER");
		}
		try {
			TenderCreditADO.checkHouseAcctOnHousePayment(cardData, cargo.getCurrentTransactionADO());

			bus.mail("Continue", BusIfc.CURRENT);
		} catch (TenderException te) {
			TenderErrorCodeEnum error = te.getErrorCode();
			if (error != TenderErrorCodeEnum.INVALID_TENDER_TYPE)
				return;
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager("UIManager");

			showInvalidTenderTypeDialog(ui);
		}
	}

	protected void showInvalidTenderTypeDialog(POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("HouseCardOnHousePayment");
		dialogModel.setType(0);
		ui.showScreen("DIALOG_TEMPLATE", dialogModel);
	}
}