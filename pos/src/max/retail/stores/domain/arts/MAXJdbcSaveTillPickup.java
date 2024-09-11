/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/arts/MAXJdbcSaveTillPickup.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.arts.JdbcSaveTillPickup;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This class save the Till Pickup transaction info.
 * 
 * @version $Revision: /main/18 $
 */
public class MAXJdbcSaveTillPickup extends JdbcSaveTillPickup {

	/**
	 * The logger to which log messages will be sent.
	 */
	private static final Logger logger = Logger.getLogger(MAXJdbcSaveTillPickup.class);

	/**
	 * revision number of this class
	 */
	public static final String revisionNumber = "$Revision: /main/18 $";

	/**
	 * Executes the SQL statements against the database.
	 * 
	 * @param dataTransaction
	 *            The data transaction
	 * @param dataConnection
	 *            The connection to the data source
	 * @param action
	 *            The information passed by the valet
	 * @exception DataException
	 *                upon error
	 */
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcSaveTillPickup.execute()");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		// pull the data from the action object
		TillAdjustmentTransactionIfc transaction = (TillAdjustmentTransactionIfc) action.getDataObject();

		insertTransaction(connection, transaction);
		if (!transaction.isTrainingMode())
			saveTillPickup(connection, transaction);

		if (logger.isDebugEnabled())
			logger.debug("JdbcSaveTillPickup.execute()");
	}
}
