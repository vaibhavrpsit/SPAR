/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  
 *  Rev 1.0  29/May/2013               Izhar                                      Discount rule
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

public class MAXIsInvoiceApplied implements TrafficLightIfc {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = -2465771884264845029L;

	/**
	 * revision number
	 **/
	public static String revisionNumber = "$Revision: 1.2 $";
	/**
	 * The logger to which log messages will be sent.
	 */
	private static Logger logger = Logger.getLogger(max.retail.stores.pos.services.sale.MAXIsInvoiceApplied.class);

	// ----------------------------------------------------------------------
	/**
	 * Checks to see if the till is not suspended.
	 * <P>
	 * 
	 * @return true if the till is not suspended, false otherwise.
	 **/
	// ----------------------------------------------------------------------
	public boolean roadClear(BusIfc bus) {
		// logger.info(LogManagerIfc.TYPE_ENTRY +
		// "IsNotLayawayTransactionSignal.roadClear()");

		boolean result = false;
		MAXSaleCargoIfc cargo = (MAXSaleCargoIfc) bus.getCargo();

		if ((cargo.getTransaction().getItemContainerProxy().getTransactionDiscounts() != null)
				&& (cargo.getTransaction().getItemContainerProxy().getTransactionDiscounts().length != 0) && cargo.isApplyBestDeal()) {
			result = true;
		}

		// logger.info(LogManagerIfc.TYPE_EXIT +
		// "IsNotLayawayTransactionSignal.roadClear()");
		return (result);
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns a string representation of the object.
	 * <P>
	 * 
	 * @return String representation of object
	 **/
	// ----------------------------------------------------------------------
	public String toString() {
		String strResult = new String("Class:  IsTillNotSuspendedSignal (Revision " + getRevisionNumber() + ")" + hashCode());
		return (strResult);
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
		return (revisionNumber);
	}
}
