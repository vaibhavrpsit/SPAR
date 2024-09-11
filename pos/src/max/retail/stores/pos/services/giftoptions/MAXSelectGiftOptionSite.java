/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2002 360Commerce, Inc.    All Rights Reserved.

     $Log:
      3    360Commerce 1.2         3/31/2005 4:29:54 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:25:08 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse   
     $
     Revision 1.2  2004/02/12 16:50:25  mcs
     Forcing head revision

     Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Nov 21 2003 15:09:22   lzhao
 * Initial revision.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.giftoptions;

import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.DispatcherIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.services.giftoptions.SelectGiftOptionSite;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
 * This site displays the Gift options
 * 
 * @version $Revision: 3$
 */
// --------------------------------------------------------------------------
public class MAXSelectGiftOptionSite extends SelectGiftOptionSite {
	/** revision number of this class */
	public static final String revisionNumber = "$Revision: 3$";

	// ----------------------------------------------------------------------
	/**
	 * @param bus
	 *            Service Bus
	 */
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		// *****************************izhar

		GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
		UtilityIfc utility;
		// String letter = "";
		boolean offline = false;
		boolean showDialog = false;
		// ******************************end
		// *****************************izhar
		try {
			utility = Utility.createInstance();

			// added by izhar MAX-POS-CASH_THRESHOLD_AMOUNT-FES_v1.1
			offline = isSystemOffline(utility);
			FinancialCountIfc fci = cargo.getRegister().getCurrentTill()
					.getTotals().getCombinedCount().getExpected();
			FinancialCountTenderItemIfc[] fctis = fci.getTenderItems();
			String tillFloat = "0.00";
			for (int i = 0; i < fctis.length; i++) {
				if (fctis[i].getDescription().equalsIgnoreCase("CASH")) {
					tillFloat = fctis[i].getAmountTotal().toString();
				}

			}

			String limitallowed = utility.getParameterValue(
					"CashThresholdAmount", "50000.00");
			double tf = Double.parseDouble(tillFloat);
			double cta = Double.parseDouble(limitallowed);
			if (tf >= cta)
				showDialog = true;
			// addition ends
			// added by izhar MAX-POS-CASH_THRESHOLD_AMOUNT-FES_v1.1
			if (showDialog && !offline) {

				DialogBeanModel model = new DialogBeanModel();
				model.setResourceID("cashthresholdamounterror");
				model.setType(DialogScreensIfc.ERROR);

				model.setButtonLetter(DialogScreensIfc.BUTTON_OK,
						"blockgiftoption");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
			} else {
				ui.showScreen(POSUIManagerIfc.GIFT_OPTIONS);

			}
		} catch (ADOException e) {
			String message = "Configuration problem: could not instantiate UtilityIfc instance";
			logger.error(message, e);
			throw new RuntimeException(message, e);
		}
		// ******************************end

	}

	// ----------------------------------------------------------------------
	/**
	 * Returns the revision number of the class.
	 * <P>
	 * 
	 * @return String representation of revision number
	 **/
	// ----------------------------------------------------------------------
	public String getRevisionNumber() {
		return revisionNumber;
	}

	// added by izhar MAX-POS-CASH_THRESHOLD_AMOUNT-FES_v1.1
	protected boolean isSystemOffline(UtilityIfc utility) {
		DispatcherIfc d = Gateway.getDispatcher();
		DataManagerIfc dm = (DataManagerIfc) d.getManager(DataManagerIfc.TYPE);
		boolean offline = true;
		try {
			if (dm.getTransactionOnline(UtilityManagerIfc.CLOSE_REGISTER_TRANSACTION_NAME)
					|| dm.getTransactionOnline(UtilityManagerIfc.CLOSE_STORE_REGISTER_TRANSACTION_NAME)) {
				offline = false;
			}
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return offline;

	}
	// end
}
