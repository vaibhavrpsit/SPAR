/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Dec 12, 2016		Mansi Goel          Changes for Scan & Void FES
 *
 ********************************************************************************/
package max.retail.stores.pos.services.sale;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendException;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;

public class MAXCheckForSendSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = -6548521145106772044L;
	protected static final String UPDATE_SHIPPING_CHARGE = "UpdateShippingCharge";
	public static final String INVALID_SEND_MODIFICATION = "InvalidSendModification";

	public void arrive(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		MAXSaleCargo cargo = (MAXSaleCargo) bus.getCargo();
		LineItemsModel beanModel = null;
		if(cargo.isScanNVoidFlow()){
			beanModel = (LineItemsModel) ui.getModel();
		}else{
			beanModel = (LineItemsModel) ui.getModel(POSUIManagerIfc.SELL_ITEM);
		}

		int[] allSelected = beanModel.getRowsToDelete();

		SendManagerIfc sendMgr = null;
		try {
			sendMgr = (SendManagerIfc) ManagerFactory.create(SendManagerIfc.MANAGER_NAME);
		} catch (ManagerException e) {
			sendMgr = new SendManager();
		}
		try {
			sendMgr.checkItemsFromMultipleSends(cargo, allSelected);
		} catch (SendException e) {
			if (e.getErrorType() == SendException.MULTIPLE_SENDS) {
				UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, INVALID_SEND_MODIFICATION, null,
						CommonLetterIfc.FAILURE);
				return;
			}
		}

		ui.setModel(POSUIManagerIfc.SELL_ITEM, beanModel);

		Letter letter = new Letter(UPDATE_SHIPPING_CHARGE);

		bus.mail(letter, BusIfc.CURRENT);
	}
}
