/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.0		May 04, 2017		Kritica Agarwal 	GST Changes
 *
 ********************************************************************************/
package max.retail.stores.pos.services.send.transactionlevel;

import java.util.Vector;

import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.modifytransaction.ModifyTransactionCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
 * The purpose of this site is to allow only valid send items to the send
 * service $Revision: 3$
 **/
// --------------------------------------------------------------------------
public class MAXFilterTransactionLevelSendSelectionSite extends
		PosSiteActionAdapter {
	/**
	 * revision number of this class
	 **/
	public static final String revisionNumber = "$Revision: 3$";
	/**
	 * letter for items in transaction
	 **/
	public static final String ITEMS_IN_TRANSACTION = "ItemsInTransaction";
	
	public static final String ALL_ITEM_NOT_ELIGIBLE = "AllItemNotEligible";

	// ----------------------------------------------------------------------
	/**
	 * Filters for valid send items
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		ModifyTransactionCargo cargo = (ModifyTransactionCargo) bus.getCargo();
		LetterIfc letter = new Letter(ITEMS_IN_TRANSACTION);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
		.getManager(UIManagerIfc.TYPE);
		SendManagerIfc sendMgr = null;
		try {
			sendMgr = (SendManagerIfc) ManagerFactory
					.create(SendManagerIfc.MANAGER_NAME);
		} catch (ManagerException e) {
			// default to product version
			sendMgr = new SendManager();
		}
		boolean flag = true;
		if (cargo.getTransaction() != null && cargo.getItems() != null) {
			Vector validItems = new Vector();
			SaleReturnLineItemIfc[] items = cargo.getItems();
			for (int i = 0; i < items.length; i++) {
				// if the item is NOT eligible for send.
				if (sendMgr.checkValidSendItem(items[i])) {
					validItems.add(items[i]);
				}
				else
				{
					if(!items[i].isShippingCharge())
					items[i].setSendLabelCount(-1);
				}
			}
			SaleReturnLineItemIfc[] validForSend = new SaleReturnLineItemIfc[validItems
					.size()];
			for (int j = 0; j < validItems.size(); j++) {
				validForSend[j] = (SaleReturnLineItemIfc) validItems.get(j);
			}
			cargo.setItems(validForSend);

			if (validForSend.length == items.length)
				flag = true;
			else
				flag = false;
		}
		//Change for Rev 1.0 : Starts
		((MAXSaleReturnTransactionIfc)cargo.getTransaction()).setDeliverytrnx(true);
		//Change for Rev 1.0 : Starts
		if(flag)
			bus.mail(letter, BusIfc.CURRENT);
		else
		{
			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID("FewItemNotEligible");
			dialogModel.setArgs(null);
			dialogModel.setType(DialogScreensIfc.YES_NO);

			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, ITEMS_IN_TRANSACTION);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, ALL_ITEM_NOT_ELIGIBLE);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		}
	}

}
