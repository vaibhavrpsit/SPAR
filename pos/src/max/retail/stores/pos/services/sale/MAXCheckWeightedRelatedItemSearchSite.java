/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev. 1.1 		Tanmaya		28/05/2013		Bug 6034 - POS crashed while click on Quantity button in Scan&Void Screen 
  Rev. 1.0 		Tanmaya		05/04/2013		Initial Draft: Change for Scan and void
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXCheckWeightedRelatedItemSearchSite extends PosSiteActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4369844446033359081L;
	// ----------------------------------------------------------------------
	/**
	 * This defines the dialog screen to display from the bundles.
	 */
	public static String DELETE_INVALID = "DeleteInvalid";

	public void arrive(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		PromptAndResponseModel parModel = ((POSBaseBeanModel) ui
				.getModel(MAXPOSUIManagerIfc.LINEITEM_VOID))
				.getPromptAndResponseModel();
		String txt = parModel.getResponseText();
		txt = txt.substring(1, 7);
		SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
		SaleReturnTransactionIfc transaction = cargo.getTransaction();
		AbstractTransactionLineItemIfc[] itm = transaction.getLineItems();
		PLUItemIfc[] allSelected1 = new PLUItemIfc[itm.length];
		;
		AbstractTransactionLineItemIfc[] selectedLineItems = new AbstractTransactionLineItemIfc[itm.length];
		int j = 0;
		for (int i = 0; i < itm.length; i++) {
			PLUItemIfc plu = ((SaleReturnLineItemIfc) itm[i]).getPLUItem();
			if (txt.equals(plu.getPosItemID())) {
				{
					selectedLineItems[j++] = itm[i];
					allSelected1[i] = plu;
				}
			}
		}
		AbstractTransactionLineItemIfc[] allSelectedLineItems = new AbstractTransactionLineItemIfc[j];
		for (int i = 0; i < j; i++) {
			allSelectedLineItems[i] = selectedLineItems[i];
		}

		LineItemsModel lineModel = new LineItemsModel();
		lineModel.setLineItems(allSelectedLineItems);
		// MAX Rev 1.1: Change: starts
		NavigationButtonBeanModel localNav = new NavigationButtonBeanModel();
		if (allSelectedLineItems.length == 0) {

			localNav.setButtonEnabled("Quantity", false);

		} else {
			localNav.setButtonEnabled("Quantity", true);
		}
		// MAX Rev 1.1: Change: ends
		lineModel.setSelectedRow(0);
		lineModel.setLocalButtonBeanModel(localNav);

		ui.setModel(MAXPOSUIManagerIfc.LINEITEM_VOID_LIST, lineModel);
		ui.showScreen(MAXPOSUIManagerIfc.LINEITEM_VOID_LIST);
	}
}
