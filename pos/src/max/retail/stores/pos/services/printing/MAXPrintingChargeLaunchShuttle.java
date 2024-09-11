/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.

	Rev 1.0  17/June/2013	Jyoti Rawal, Initial Draft: Fix for Bug 6394 Credit Charge Slip is not getting printed
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.printing;

// foundation imports
import java.util.HashMap;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;

//--------------------------------------------------------------------------
/**
 * Shuttle used to launch the printing service.
 * 
 * @version $Revision: 1.1 $
 **/
// --------------------------------------------------------------------------
public class MAXPrintingChargeLaunchShuttle implements ShuttleIfc {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = 9218330229065219843L;

	/**
	 * The logger to which log messages will be sent.
	 **/
	protected static Logger logger = Logger
	.getLogger(max.retail.stores.pos.services.printing.MAXPrintingChargeLaunchShuttle.class);
	/**
	 * revision number supplied by Team Connection
	 **/
	public static final String revisionNumber = "$Revision: 1.1 $";

	HashMap map = null;
	TenderCargo tc = null;

	// ----------------------------------------------------------------------
	/**
	 * Loads the shuttle data from the cargo.
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void load(BusIfc bus) { // begin load()
		tc = (TenderCargo) bus.getCargo();
		map = tc.getTenderAttributes();

	} // end load()

	// ----------------------------------------------------------------------
	/**
	 * Unloads the shuttle data into the Printing cargo.
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void unload(BusIfc bus) {
		MAXPrintingCargo printingCargo = (MAXPrintingCargo) bus.getCargo();

		printingCargo.setTenderattributes(map);
		printingCargo.setTransaction(tc.getTransaction());
		printingCargo.setTransactionId(tc.getCurrentTransactionADO().getTransactionID());
		UtilityManagerIfc utility = (UtilityManagerIfc) bus
		.getManager(UtilityManagerIfc.TYPE);
		//printingCargo.setCodeListMap(utility.getCodeListMap());
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns a string representation of this object.
	 * <P>
	 * 
	 * @return String representation of object
	 **/
	// ----------------------------------------------------------------------
	public String toString() { // begin toString()
		// result string
		String strResult = new String(
				"Class:  PrintingLaunchShuttle (Revision "
				+ getRevisionNumber() + ") @" + hashCode());
		// pass back result
		return (strResult);
	} // end toString()

	// ----------------------------------------------------------------------
	/**
	 * Returns the revision number of the class.
	 * <P>
	 * 
	 * @return String representation of revision number
	 **/
	// ----------------------------------------------------------------------
	public String getRevisionNumber() { // begin getRevisionNumber()
		// return string
		return (revisionNumber);
	} // end getRevisionNumber()

} // end class PrintingLaunchShuttle
