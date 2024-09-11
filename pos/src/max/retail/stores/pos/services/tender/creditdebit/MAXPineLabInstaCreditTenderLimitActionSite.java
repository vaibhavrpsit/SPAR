/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2001 360Commerce, Inc.    All Rights Reserved.

     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:33 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:20:27 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:10:15 PM  Robert Pearse   
     $
     Revision 1.1  2004/04/08 19:30:59  bwf
     @scr 4263 Decomposition of Debit and Credit.

     Revision 1.3  2004/02/12 16:48:22  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:22:51  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Nov 04 2003 11:17:42   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 23 2003 17:29:48   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 20 2003 16:32:42   epd
 * Initial revision.
     
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.HashMap;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site invokes credit tender limit validation
 */
public class MAXPineLabInstaCreditTenderLimitActionSite extends PosSiteActionAdapter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.foundation.tour.application.SiteActionAdapter#arrive
	 * (com.extendyourstore.foundation.tour.ifc.BusIfc)
	 */
	public void arrive(BusIfc bus) {
		// add tender type to attributes
		TenderCargo cargo = (TenderCargo) bus.getCargo();
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
		if ((bus.getCurrentLetter().getName() != null) && !(("OnlineCredit").equals(bus.getCurrentLetter().getName())))
			bus.mail(new Letter("Success"), BusIfc.CURRENT);
		else
			bus.mail(new Letter("OnlineCredit"), BusIfc.CURRENT);

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
	 * @see com.extendyourstore.pos.ui.DialogScreensIfc
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
