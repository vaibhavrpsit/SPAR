/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *
 * Rev 1.1  Dec 08, 2014    Shavinki Goyal 		Resolution for LSIPL-FES:-Multiple Tender using Innoviti 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
 
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.HashMap;

import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site invokes credit tender limit validation
 */
public class MAXPineLabCreditTenderLimitActionSite extends PosSiteActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * */
	public void arrive(BusIfc bus) {
		// add tender type to attributes
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		MAXSaleReturnTransaction saleTran = null;
		MAXLayawayTransaction laytran = null;
		if (cargo.getTransaction() != null
				&& cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
			saleTran = (MAXSaleReturnTransaction) cargo.getTransaction();
		} else if (cargo.getTransaction() != null
				&& cargo.getTransaction() instanceof MAXLayawayTransaction) {
			laytran = (MAXLayawayTransaction) cargo.getTransaction();
		}
		HashMap attributes = cargo.getTenderAttributes();
		attributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.CREDIT);

		try {
			// invoke tender limit validation
			cargo.getCurrentTransactionADO().validateTenderLimits(attributes);
		} catch (TenderException te) {
			TenderErrorCodeEnum errorCode = te.getErrorCode();

			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			if (errorCode == TenderErrorCodeEnum.OVERTENDER_ILLEGAL) {
				displayErrorDialog(ui, "OvertenderNotAllowed", null, DialogScreensIfc.ERROR);
				return;
			} else if (errorCode == TenderErrorCodeEnum.MAX_LIMIT_VIOLATED) {
				String[] args = new String[] { TenderTypeEnum.CREDIT.toString() };
				displayErrorDialog(ui, "AmountExceedsMaximum", args, DialogScreensIfc.CONFIRMATION);
				return;
			} else if (errorCode == TenderErrorCodeEnum.MIN_LIMIT_VIOLATED) {
				String[] args = new String[] { TenderTypeEnum.CREDIT.toString() };
				displayErrorDialog(ui, "AmountLessThanMinimum", args, DialogScreensIfc.CONFIRMATION);
				return;
			}
		}
		//if ((bus.getCurrentLetter().getName() != null) && !(("OnlineCredit").equals(bus.getCurrentLetter().getName())))Pinelab
		if ((bus.getCurrentLetter().getName() != null) && !(("Pinelab").equals(bus.getCurrentLetter().getName())))
		{
			// MAX Change for Rev 1.1: Start
			if (saleTran != null) {
				saleTran.setEdcType("PLUTUS");
			} else if (laytran != null) {
				laytran.setEdcType("PLUTUS");
			}
			cargo.setCreditOnlineFlow(false);
			bus.mail(new Letter("Success"), BusIfc.CURRENT);
		}
		else
		{
			cargo.setCreditOnlineFlow(true);
			bus.mail(new Letter("OnlineCredit"), BusIfc.CURRENT);
			// MAX Change for Rev 1.1: End
		}

	}

	// ----------------------------------------------------------------------
	/**
	 * Displays the specified Dialog.
	 * 
	 * @param ui
	 *            UI Manager to handle the IO
	 * @param name
	 *            name of the Error Dialog to display
	 * @param args
	 *            arguments for the dialog screen
	 * @param type
	 *            the dialog type
	 **/
	// ----------------------------------------------------------------------
	private void displayErrorDialog(POSUIManagerIfc ui, String name, String[] args, int type) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(name);

		if (args != null) {
			dialogModel.setArgs(args);
		}
		dialogModel.setType(type);

		if (type == DialogScreensIfc.ERROR) {
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");
		} else {
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Override");
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Invalid");
		}
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

}
