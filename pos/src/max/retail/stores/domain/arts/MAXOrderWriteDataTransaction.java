/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.arts.OrderWriteDataTransaction;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

public class MAXOrderWriteDataTransaction extends OrderWriteDataTransaction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8486262185745709401L;
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXOrderWriteDataTransaction.class);

	public static String dataCommandName = "MAXOrderWriteDataTransaction";

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public MAXOrderWriteDataTransaction() { // begin OrderWriteDataTransaction()
		super(dataCommandName);
	} // end OrderWriteDataTransaction()

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * 
	 * @param name
	 *            data command name
	 **/
	// ---------------------------------------------------------------------
	public MAXOrderWriteDataTransaction(String name) { // begin
														// OrderWriteDataTransaction()
		super(name);
	}

	public void insertOrder(OrderIfc order) throws DataException { // begin
																	// updateOrder()
		if (logger.isDebugEnabled())
			logger.debug("MAXOrderWriteDataTransaction.insertOrder");

		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		dataActions[0] = createDataAction(order, "InsertOrder");
		setDataActions(dataActions);

		// execute data request
		getDataManager().execute(this);

		if (logger.isDebugEnabled())
			logger.debug("" + "MAXOrderWriteDataTransaction.insertOrder" + "");

	}
}
