/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  
*
*       Rev 1.0  27/May/2013	Tanmaya Kamal	 Initial Draft: Coupon Till History
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.arts;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.arts.ARTSTill;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//-------------------------------------------------------------------------
/**
 * This operation performs attempts to update a till. If it is not found, an
 * insert is performed.
 * <P>
 * 
 * @version $Revision: 3$
 **/
// -------------------------------------------------------------------------
public class MAXJdbcCreateUpdateTill extends MAXJdbcSaveTill {
	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcCreateUpdateTill.class);

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 **/
	// ---------------------------------------------------------------------
	public MAXJdbcCreateUpdateTill() {
		super();
		setName("JdbcCreateUpdateTill");
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
			logger.debug("JdbcCreateUpdateTill.execute()");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		ARTSTill artsTill = (ARTSTill) action.getDataObject();

		createUpdateTill(connection, artsTill.getPosTill(), artsTill.getRegister());

		if (logger.isDebugEnabled())
			logger.debug("JdbcCreateUpdateTill.execute()");
	}

	// ---------------------------------------------------------------------
	/**
	 * Updates the status of a till. If this fails, an insert is performed.
	 * 
	 * @param dataConnection
	 *            connection to the db
	 * @param till
	 *            the till information to save
	 * @param register
	 *            the register associated with the till
	 * @return return code (true if operation succeeds, false otherwise)
	 * @exception DataException
	 *                upon error
	 **/
	// ---------------------------------------------------------------------
	public boolean createUpdateTill(JdbcDataConnection dataConnection, TillIfc till, RegisterIfc register)
			throws DataException {
		boolean returnCode = updateTill(dataConnection, till, register);
		if (returnCode == false) {
			returnCode = insertTill(dataConnection, till, register);
		}

		return (returnCode);
	}
}
