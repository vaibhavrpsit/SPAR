/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	30/Jult/2013	  	Jyoti, Bug 7380 - Special Order : Customer name is visible after modifying the transaction
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.common;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXOrderWriteDataTransaction;
import max.retail.stores.domain.order.MAXOrderIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//------------------------------------------------------------------------------
/**
 * Updates an order based upon order in cargo. Updates the status of the order
 * and its order line items. After the order has been successfully updated, this
 * site clears the selected summary that was used to retrieve the order from
 * cargo.
 * 
 * @version $Revision: 7$
 **/
// ------------------------------------------------------------------------------

public class MAXUpdateOrderSite extends PosSiteActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7880432071729438248L;

	/**
	 * class name constant
	 **/
	public static final String SITENAME = "UpdateOrderSite";

	/**
	 * revision number for this class
	 **/
	public static final String revisionNumber = "$Revision: 7$";

	// --------------------------------------------------------------------------
	/**
	 * Performs a data transaction to save the state of the current order in
	 * cargo to persistent storage. Indirectly updates the inventory for the
	 * line items.
	 * 
	 * @param bus
	 *            the bus arriving at this site
	 **/
	// --------------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		MAXOrderCargo cargo = (MAXOrderCargo) bus.getCargo();
		OrderIfc order = cargo.getOrder();

		if (order != null) {
			Letter result = new Letter(CommonLetterIfc.SUCCESS);

			MAXOrderWriteDataTransaction orderWTransaction = (MAXOrderWriteDataTransaction) DataTransactionFactory
					.create(MAXDataTransactionKeys.ORDER_WRITE_DATA_TRANSACTION);
			ParameterManagerIfc pm = (ParameterManagerIfc) bus
					.getManager(ParameterManagerIfc.TYPE);
			try {
				if (order instanceof MAXOrderIfc
						&& ((MAXOrderIfc) order).isAlterOrder()) {
					orderWTransaction.insertOrder(order);
				} else {
					orderWTransaction.updateOrder(order);
				}
				// Journal the action that was performed on the order (PickList,
				// Fill or Cancel)
				// Pickup service journalling is executed elsewhere and
				// therefore is ignored here.
				// Pass the order, an empty String for transactionID, the
				// service name and cargo.
				if (!(cargo.getServiceType() == MAXOrderCargoIfc.SERVICE_PICKUP_TYPE)) {
					MAXOrderUtilities utility = new MAXOrderUtilities();
					utility.journalOrder(order, "", cargo.getServiceType(),
							cargo, pm);
				}
			} catch (DataException de) { // begin data base exception catch
				result = new Letter(CommonLetterIfc.DB_ERROR);
				logger.error(" DB error: " + de.getMessage() + "");
				cargo.setDataExceptionErrorCode(de.getErrorCode());
			} // end database error catch

			// clean up cargo, clear Selected Summary and shrink the list
			cargo.clearSelectedSummary(true);
			//Rev 1.1 changes
	        POSUIManagerIfc ui = 
	            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
	        ui.customerNameChanged("");

			bus.mail(result, BusIfc.CURRENT);
		} else {
			throw new NullPointerException(
					"OrderCargo contains null order reference in"
							+ "UpdateOrderSite.arrive()");
		}
	}
}
