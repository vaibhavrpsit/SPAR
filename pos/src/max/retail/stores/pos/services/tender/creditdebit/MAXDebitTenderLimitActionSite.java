/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.0  	21 Dec, 2016              Ashish Yadav             Intial Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

public class MAXDebitTenderLimitActionSite extends PosSiteActionAdapter {
	public void arrive(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		cargo.getTenderAttributes().put("TENDER_TYPE", TenderTypeEnum.DEBIT);
		try {
			cargo.getCurrentTransactionADO().validateTenderLimits(cargo.getTenderAttributes());
		} catch (TenderException e) {
			TenderErrorCodeEnum error = e.getErrorCode();

			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager("UIManager");
			if (error == TenderErrorCodeEnum.OVERTENDER_ILLEGAL) {
				UIUtilities.setDialogModel(ui, 1, "OvertenderNotAllowed", null, "Invalid");

				return;
			}
			if (error == TenderErrorCodeEnum.MAX_LIMIT_VIOLATED) {
				int[] buttons = { 1, 2 };

				String[] letters = { "Override", "Invalid" };
				String[] args = { TenderTypeEnum.DEBIT.toString() };
				UIUtilities.setDialogModel(ui, 0, "AmountExceedsMaximum", args, buttons, letters);

				return;
			}
			if (error == TenderErrorCodeEnum.MIN_LIMIT_VIOLATED) {
				int[] buttons = { 1, 2 };

				String[] letters = { "Override", "Invalid" };
				String[] args = { TenderTypeEnum.DEBIT.toString() };
				UIUtilities.setDialogModel(ui, 0, "AmountLessThanMinimum", args, buttons, letters);

				return;
			}
		}

		bus.mail(new Letter("Success"), BusIfc.CURRENT);
	}
}