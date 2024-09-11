/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.0  	21 Dec, 2016              Ashish Yadav              Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.HashMap;

import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;


public class MAXCreditTenderLimitActionSite extends PosSiteActionAdapter {
	
	public void arrive(BusIfc bus) {
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		HashMap attributes = cargo.getTenderAttributes();
		
		attributes.put("TENDER_TYPE", TenderTypeEnum.CREDIT);
		try {
			cargo.getCurrentTransactionADO().validateTenderLimits(attributes);
		} catch (TenderException te) {
			TenderErrorCodeEnum errorCode = te.getErrorCode();

			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager("UIManager");
			if (errorCode == TenderErrorCodeEnum.OVERTENDER_ILLEGAL) {
				displayErrorDialog(ui, "OvertenderNotAllowed", null, 1);
				return;
			}
			if (errorCode == TenderErrorCodeEnum.MAX_LIMIT_VIOLATED) {
				String[] args = { TenderTypeEnum.CREDIT.toString() };
				displayErrorDialog(ui, "AmountExceedsMaximum", args, 0);
				return;
			}
			if (errorCode == TenderErrorCodeEnum.MIN_LIMIT_VIOLATED) {
				String[] args = { TenderTypeEnum.CREDIT.toString() };
				displayErrorDialog(ui, "AmountLessThanMinimum", args, 0);
				return;
			}

		}

		//if ((bus.getCurrentLetter().getName() != null) && !(("OnlineCredit").equals(bus.getCurrentLetter().getName())))
		if ((bus.getCurrentLetter().getName() != null) && !(("Innoviti").equals(bus.getCurrentLetter().getName())))
		{
			//((MAXTenderCargo)cargo).setCreditOnlineFlow(false);
			
			cargo.setCreditOnlineFlow(false);
		bus.mail(new Letter("Success"), BusIfc.CURRENT);
	}
		else
		{
			//((MAXTenderCargo)cargo).setCreditOnlineFlow(true);
			cargo.setCreditOnlineFlow(true);
			bus.mail(new Letter("OnlineCredit"), BusIfc.CURRENT);
		}
	}

	private void displayErrorDialog(POSUIManagerIfc ui, String name, String[] args, int type) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(name);

		if (args != null) {
			dialogModel.setArgs(args);
		}
		dialogModel.setType(type);

		if (type == 1) {
			dialogModel.setButtonLetter(0, "Invalid");
		} else {
			dialogModel.setButtonLetter(1, "Override");
			dialogModel.setButtonLetter(2, "Invalid");
		}
		ui.showScreen("DIALOG_TEMPLATE", dialogModel);
	}
}