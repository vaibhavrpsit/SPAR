/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.1  29/May/2013               Tanmaya              Bug 6076 - Blank screen after clicking on quantity button.
 *  Rev 1.0  29/April/2013               Tanmaya              Home Delivery - Special Order
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.order.alter;

import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.pos.services.order.common.MAXOrderCargo;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;

public class MAXAlterCheckQuantityModifiableAisle extends PosLaneActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8394216201089591177L;
	/**
	 * revision number of this class
	 **/
	public static final String revisionNumber = "$Revision: 9$";
	/**
	 * lane name constant
	 **/
	public static final String LANENAME = "ItemQuantityModifiedAisle";

	// ----------------------------------------------------------------------
	/**
	 * ##COMMENT-TRAVERSE##
	 * <P>
	 * 5
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void traverse(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		MAXOrderCargo cargo = (MAXOrderCargo) bus.getCargo();
		LineItemsModel beanModel = (LineItemsModel) ui.getModel();
		OrderIfc order = cargo.getOrder();
		int[] allSelected = beanModel.getSelectedRows();
		if (allSelected != null && (allSelected.length == 1)) {
			OrderLineItemIfc lineItem = (OrderLineItemIfc) order.getLineItems()[allSelected[0]];
			PLUItemIfc pluItem = lineItem.getPLUItem();

			if (pluItem instanceof MAXPLUItemIfc
					&& (((MAXPLUItemIfc) pluItem).IsWeightedBarCode())) {
				DialogBeanModel dModel = new DialogBeanModel();
				dModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
				dModel.setResourceID("QuantityCannotBeModified");
				dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dModel);
			}

			else {
				if (lineItem.getOrderItemStatus().getStatus().getStatus() == 0 || lineItem.getOrderItemStatus().getStatus().getStatus() == 1) {
					cargo.setLineItem(lineItem);
					bus.mail(new Letter("QuantityModify"), BusIfc.CURRENT);
				} else {
					DialogBeanModel dModel = new DialogBeanModel();
					dModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					dModel.setResourceID("QuantityCantBeModified");
					dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dModel);
				}
			}
		} else {
			DialogBeanModel dModel = new DialogBeanModel();
			dModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
			dModel.setResourceID("Item");
			dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dModel);
		}

	}

	// ---------------------------------------------------------------------
	/**
	 * Returns a string representation of the object.
	 * <P>
	 * 
	 * @return String representation of class
	 **/
	// ---------------------------------------------------------------------
	public String toString() { // begin toString()
		// result string
		StringBuffer strResult = new StringBuffer("Class:  ");
		strResult.append(LANENAME).append(" (Revision ")
				.append(getRevisionNumber()).append(") @").append(hashCode());
		// pass back result
		return (strResult.toString());
	} // end toString()

	// ---------------------------------------------------------------------
	/**
	 * Returns the revision number of this class.
	 * <p>
	 * 
	 * @return String representation of revision number
	 **/
	// ---------------------------------------------------------------------
	public String getRevisionNumber() { // begin getRevisionNumber()
		// return string
		return (revisionNumber);
	} // end getRevisionNumber()

	// ---------------------------------------------------------------------
	/**
	 * Main to run a test..
	 **/
	// ---------------------------------------------------------------------
	public static void main(String args[]) { // begin main()
		MAXAlterCheckQuantityModifiableAisle clsItemQuantityModifiedAisle = new MAXAlterCheckQuantityModifiableAisle();

		System.out.println(clsItemQuantityModifiedAisle.toString());
	} // end main()
}
