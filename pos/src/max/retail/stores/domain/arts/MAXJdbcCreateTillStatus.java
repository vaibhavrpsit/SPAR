/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  
*
*       Rev 1.0  27/May/2013	Tanmaya Kamal	 Initial Draft: Coupon Till History
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.arts.ARTSTill;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

//-------------------------------------------------------------------------
/**
 * This operation performs inserts into the till table.
 * <P>
 * 
 * @version $Revision: 4$
 **/
// -------------------------------------------------------------------------
public class MAXJdbcCreateTillStatus extends MAXJdbcSaveTill implements ARTSDatabaseIfc {
	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcCreateTillStatus.class);

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 **/
	// ---------------------------------------------------------------------
	public MAXJdbcCreateTillStatus() {
		super();
		setName("MAXJdbcCreateTillStatus");
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
			logger.debug("JdbcCreateTillStatus.execute()");

		/*
		 * getUpdateCount() is about the only thing outside of DataConnectionIfc
		 * that we need.
		 */
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		// Navigate the input object to obtain values that will be inserted
		// into the database.
		ARTSTill artsTill = (ARTSTill) action.getDataObject();

		createTillStatus(connection, artsTill.getPosTill(), artsTill.getRegister());

		if (logger.isDebugEnabled())
			logger.debug("JdbcCreateTillStatus.execute()");
	}

	// ---------------------------------------------------------------------
	/**
	 * Creates the totals for a till. Also updates the till status.
	 * <P>
	 * 
	 * @param dataConnection
	 *            connection to the db
	 * @param till
	 *            the till information to create
	 * @param register
	 *            the register associated with the till
	 * @return true if successful
	 * @exception DataException
	 *                upon error
	 **/
	// ---------------------------------------------------------------------
	public boolean createTillStatus(JdbcDataConnection dataConnection, TillIfc till, RegisterIfc register)
			throws DataException {
		boolean returnCode = false;

		/*
		 * Insert the Till record.
		 */
		if (insertTill(dataConnection, till, register)) {
			returnCode = true;
		}

		return (returnCode);
	}
}
