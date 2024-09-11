/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 1998-2001 360Commerce, Inc.    All Rights Reserved.
     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:27 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:20:14 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:10:00 PM  Robert Pearse   
     $
     Revision 1.3  2004/02/12 16:52:00  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:52:30  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 16:07:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:01:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:47:56   msg
 * Initial revision.
 * 
 *    Rev 1.3   Dec 04 2001 16:11:56   dfh
 * test
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.2   04 Dec 2001 15:56:58   msg
 * test
 * Resolution for 7: test scr
 * 
 *    Rev 1.1   04 Dec 2001 15:55:54   msg
 * test
 * Resolution for 260: Special Order feature for release 5.0
 * 
 *    Rev 1.0   Dec 04 2001 14:48:20   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.specialorder;

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
import oracle.retail.stores.pos.services.specialorder.ChooseSpecialOrderOptionSite;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
 * Displays the special order options screen.
 * <P>
 * 
 * @version $Revision: 3$
 **/
// ------------------------------------------------------------------------------
public class MAXChooseSpecialOrderOptionSite extends
		ChooseSpecialOrderOptionSite {
	/**
	 * class name constant
	 **/
	public static final String SITENAME = "ChooseSpecialOrderOptionSite";
	/**
	 * revision number for this class
	 **/
	public static final String revisionNumber = "$Revision: 3$";

	// --------------------------------------------------------------------------
	/**
	 * Displays the specal order options screen.
	 * <P>
	 * 
	 * @param bus
	 *            the bus arriving at this site
	 **/
	// --------------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel pModel = new POSBaseBeanModel();
		// *****************************izhar

		SpecialOrderCargo cargo = (SpecialOrderCargo) bus.getCargo();
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
						"blockspecialorder");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
			} else {
				ui.showScreen(POSUIManagerIfc.SPECIAL_ORDER_OPTIONS, pModel);

			}
		} catch (ADOException e) {
			String message = "Configuration problem: could not instantiate UtilityIfc instance";
			logger.error(message, e);
			throw new RuntimeException(message, e);
		}
		// ******************************end

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
