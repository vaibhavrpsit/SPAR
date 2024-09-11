/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.0  	21 Dec, 2016              Ashish Yadav              Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.HashMap;
import java.util.Vector;

import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.CreditTypeEnum;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerIDBeanModel;

public class MAXVerifyCustomerIDUISite extends PosSiteActionAdapter {
	public void arrive(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		HashMap tenderAttributes = cargo.getTenderAttributes();
		EncipheredCardDataIfc cardData = (EncipheredCardDataIfc)tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA);
		if ((getCardType(tenderAttributes, bus,cardData) == CreditTypeEnum.HOUSECARD)
				&& (tenderAttributes.get("MSR_MODEL") == null)
				&& (!(capturedCustomerInfo(cargo.getTenderAttributes())))) {
			HashMap tdoAttributes = new HashMap();
			tdoAttributes.put("bus", bus);

			TDOUIIfc tdo = null;
			try {
				tdo = (TDOUIIfc) TDOFactory.create("tdo.tender.VerifyCustomerID");
			} catch (TDOException tdoe) {
				tdoe.printStackTrace();
			}
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager("UIManager");
			ui.showScreen("ENTER_ID_INFO", tdo.buildBeanModel(tdoAttributes));
		} else {
			bus.mail(new Letter("Continue"), BusIfc.CURRENT);
		}
	}

	public void depart(BusIfc bus) {
		if (!(bus.getCurrentLetter().getName().equals("Next"))) {
			return;
		}
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager("UIManager");
		CustomerIDBeanModel model = (CustomerIDBeanModel) ui.getModel("ENTER_ID_INFO");

		TenderCargo cargo = (TenderCargo) bus.getCargo();
		Vector idTypes = model.getIDTypes();
		cargo.getTenderAttributes().put("ID_TYPE", idTypes.get(model.getSelectedIDType()));
		cargo.getTenderAttributes().put("ID_COUNTRY", model.getCountry());
		cargo.getTenderAttributes().put("ID_STATE", model.getState());
		cargo.getTenderAttributes().put("ID_EXPIRATION_DATE", model.getExpirationDate());
	}

	protected CreditTypeEnum getCardType(HashMap tenderAttributes, BusIfc bus, EncipheredCardDataIfc cardData) {
		String cardNumber = "";
		if (tenderAttributes.get("MSR_MODEL") != null) {
			cardNumber = ((MSRModel) tenderAttributes.get("MSR_MODEL")).getAccountNumber();
		} else {
			cardNumber = (String) tenderAttributes.get("NUMBER");
		}

		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager("UtilityManager");
		return utility.determineCreditType(cardData);
	}

	protected boolean capturedCustomerInfo(HashMap tenderAttributes) {
		boolean result = true;

		if ((tenderAttributes.get("ID_TYPE") == null) || (tenderAttributes.get("ID_STATE") == null)
				|| (tenderAttributes.get("ID_EXPIRATION_DATE") == null)) {
			result = false;
		}

		return result;
	}
}