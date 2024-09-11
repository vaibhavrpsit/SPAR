/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  
*
*       Rev 1.0  27/May/2013	Tanmaya Kamal	 Initial Draft: Coupon Till History
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

// foundation imports
import oracle.retail.stores.domain.arts.ARTSTill;
import oracle.retail.stores.domain.arts.AccumulatorTransactionIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * This operation performs updates in the till history table.
 * <P>
 * 
 * @version $Revision: 4$
 **/
// -------------------------------------------------------------------------
public class MAXJdbcUpdateTillTotals extends MAXJdbcSaveTill implements ARTSDatabaseIfc {
	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcUpdateTillTotals.class);

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 **/
	// ---------------------------------------------------------------------
	public MAXJdbcUpdateTillTotals() {
		super();
		setName("JdbcUpdateTillTotals");
	}

	// ---------------------------------------------------------------------
	/**
	 * Executes the SQL statements against the database.
	 * <P>
	 * 
	 * @param dataTransaction
	 *            The data transaction
	 * @param dataConnection
	 *            The connection to the data source
	 * @param action
	 *            The information passed by the valet
	 * @exception DataException
	 *                upon error
	 **/
	// ---------------------------------------------------------------------
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcUpdateTillTotals.execute()");

		/*
		 * getUpdateCount() is about the only thing outside of DataConnectionIfc
		 * that we need.
		 */
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		// Navigate the input object to obtain values that will be updated
		// in the database.
		ARTSTill artsTill = (ARTSTill) action.getDataObject();
		AccumulatorTransactionIfc trans = (AccumulatorTransactionIfc) dataTransaction;
		updateTillTotals(connection, artsTill.getPosTill(), artsTill.getRegister());

		if (logger.isDebugEnabled())
			logger.debug("JdbcUpdateTillTotals.execute()");
	}

	// ---------------------------------------------------------------------
	/**
	 * Updates the financial totals of a till.
	 * <P>
	 * 
	 * @param dataConnection
	 *            connection to the db
	 * @param till
	 *            The till information
	 * @param register
	 *            the register associated with the till
	 * @exception DataException
	 *                upon error
	 **/
	// ---------------------------------------------------------------------
	public void updateTillTotals(JdbcDataConnection dataConnection, TillIfc till, RegisterIfc register)
			throws DataException {
		/*
		 * This is rather simplified at the moment, The update will only return
		 * false is there is no record
		 */
		if (!updateTillHistory(dataConnection, till, register)) {
			insertTillHistory(dataConnection, till, register);
		}
	}
}
