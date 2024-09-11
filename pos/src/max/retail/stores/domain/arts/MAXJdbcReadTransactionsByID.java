/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* initial draft -- by vaibhav
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
 * This operation reads all of the transactions matching a specified transaction
 * ID.
 * <P>
 * 
 * @version $Revision: 3$
 **/
// --------------------------------------------------------------------------
public class MAXJdbcReadTransactionsByID extends MAXJdbcReadTransaction {
	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcReadTransactionsByID.class);

	// ----------------------------------------------------------------------
	/**
	 * Class constructor.
	 **/
	// ----------------------------------------------------------------------
	public MAXJdbcReadTransactionsByID() {
		super();
		setName("MAXJdbcReadTransactionsByID");
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
			logger.debug("JdbcReadTransactionsByID.execute");

		// set data connection
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		// grab arguments and call readTransactionsByID()
		TransactionIfc transaction = (TransactionIfc) action.getDataObject();
		TransactionIfc[] transactions = readTransactionsByID(connection, transaction, transaction.getLocaleRequestor());

		// return array
		dataTransaction.setResult(transactions);

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransactionsByID.execute");
	}
}
