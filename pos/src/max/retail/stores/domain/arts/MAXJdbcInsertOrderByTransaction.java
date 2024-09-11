/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXJdbcInsertOrderByTransaction extends MAXJdbcSaveOrderByTransaction {

	private static final long serialVersionUID = -4739076889022815903L;
	private static Logger logger = Logger
			.getLogger(max.retail.stores.domain.arts.MAXJdbcInsertOrderByTransaction.class);

	// ----------------------------------------------------------------------
	/**
	 * Class constructor.
	 **/
	// ----------------------------------------------------------------------
	public MAXJdbcInsertOrderByTransaction() {
		super();
		setName("MAXJdbcInsertOrderByTransaction");
	}

	// ----------------------------------------------------------------------
	/**
	 * Executes the SQL statements against the database.
	 * <P>
	 * 
	 * @param dataTransaction
	 * @param dataConnection
	 * @param action
	 **/
	// ----------------------------------------------------------------------
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcInsertOrderByTransaction.execute");

		// set data connection
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		// retrieve input from data object
		OrderTransactionIfc orderTransaction = (OrderTransactionIfc) action.getDataObject();
		insertOrder(connection, orderTransaction);

		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcInsertOrderByTransaction.execute");
	}
}
