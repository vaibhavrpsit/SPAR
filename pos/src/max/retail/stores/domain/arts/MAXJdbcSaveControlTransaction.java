/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Changes done for BUG 7162
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.arts.ARTSTransaction;
import oracle.retail.stores.domain.arts.JdbcSaveTransaction;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.TillOpenCloseTransaction;
import oracle.retail.stores.domain.transaction.TillOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//-------------------------------------------------------------------------
/**
 * This operation performs inserts into the transaction and control transaction
 * tables.
 * <P>
 * 
 * @version $Revision: 3$
 **/
// -------------------------------------------------------------------------
public class MAXJdbcSaveControlTransaction extends JdbcSaveTransaction {
	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcSaveControlTransaction.class);

	/** Constant For Till Not Approved **/
	private static String TILL_NOT_APPORVED = "'" + "NotAppr" + "'";

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public MAXJdbcSaveControlTransaction() {
		super();
		setName("JdbcSaveControlTransaction");
	}

	// ---------------------------------------------------------------------
	/**
	 * Executes the SQL statements against the database.
	 * <P>
	 * 
	 * @param dataTransaction
	 * @param dataConnection
	 * @param action
	 * @exception DataException
	 *                upon error
	 **/
	// ---------------------------------------------------------------------
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcSaveControlTransaction.execute()");

		/*
		 * getUpdateCount() is about the only thing outside of DataConnectionIfc
		 * that we need.
		 */
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		// Navigate the input object to obtain values that will be inserted
		// into the database.
		ARTSTransaction trans = (ARTSTransaction) action.getDataObject();
		saveControlTransaction(connection, trans.getPosTransaction());

		if (logger.isDebugEnabled())
			logger.debug("JdbcSaveControlTransaction.execute()");
	}

	// ---------------------------------------------------------------------
	/**
	 * Saves a control transaction.
	 * <P>
	 * 
	 * @param dataConnection
	 *            connection to the db
	 * @param transaction
	 *            a control transaction
	 * @exception DataException
	 *                upon error
	 **/
	// ---------------------------------------------------------------------
	public void saveControlTransaction(JdbcDataConnection dataConnection, TransactionIfc transaction)
			throws DataException {
		/*
		 * If the insert fails, then try to update the transaction
		 */
		try {
			insertControlTransaction(dataConnection, transaction);
		} catch (DataException de) {
			// updateControlTransaction(dataConnection, transaction);
			/*
			 * Shouldn't be updating this type of transaction. Pass back
			 * exception instead.
			 */
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			logger.error("Couldn't save control transaction.");
			logger.error("" + e + "");
			throw new DataException(DataException.UNKNOWN, "Couldn't save control transaction.", e);
		}
	}

	// ---------------------------------------------------------------------
	/**
	 * Updates the control transaction table.
	 * <P>
	 * 
	 * @param dataConnection
	 *            connection to the db
	 * @param transactionType
	 *            The type of control transaction.
	 * @param transaction
	 *            a control transaction
	 * @exception DataException
	 *                thrown when an error occurs.
	 **/
	// ---------------------------------------------------------------------
	public void updateControlTransaction(JdbcDataConnection dataConnection, TransactionIfc transaction)
			throws DataException {
		/*
		 * Update the transaction table first.
		 */
		updateTransaction(dataConnection, transaction);

		SQLUpdateStatement sql = new SQLUpdateStatement();

		// Table
		sql.setTable(TABLE_CONTROL_TRANSACTION);

		// Fields
		sql.addColumn(FIELD_CONTROL_TRANSACTION_TYPE_CODE, getTransactionType(transaction));

		// Qualifiers
		sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
		sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "updateControlTransaction", e);
		}
	}

	// ---------------------------------------------------------------------
	/**
	 * Inserts into the control transaction table.
	 * <P>
	 * 
	 * @param dataConnection
	 *            connection to the db
	 * @param transaction
	 *            a control transaction
	 * @exception DataException
	 *                thrown when an error occurs.
	 **/
	// ---------------------------------------------------------------------
	public void insertControlTransaction(JdbcDataConnection dataConnection, TransactionIfc transaction)
			throws DataException {
		/*
		 * Insert the transaction in the Transaction table first.
		 */
		insertTransaction(dataConnection, transaction);

		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_CONTROL_TRANSACTION);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_CONTROL_TRANSACTION_TYPE_CODE, getTransactionType(transaction));

		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertControlTransaction", e);
		}
	}

	// ---------------------------------------------------------------------
	/**
	 * Inserts into the transaction table.
	 * <P>
	 * 
	 * @param dataConnection
	 *            connection to the db
	 * @param transaction
	 *            a pos transaction
	 * @exception DataException
	 *                upon error
	 **/
	// ---------------------------------------------------------------------
	public void insertTransaction(JdbcDataConnection dataConnection, TransactionIfc transaction) throws DataException {
		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_TRANSACTION);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_OPERATOR_ID, getOperatorID(transaction));
		sql.addColumn(FIELD_TRANSACTION_BEGIN_DATE_TIMESTAMP, getTransactionBeginDateString(transaction));
		sql.addColumn(FIELD_TRANSACTION_END_DATE_TIMESTAMP, getTransactionEndDateString(transaction));
		sql.addColumn(FIELD_TRANSACTION_TYPE_CODE, getTransactionType(transaction));
		sql.addColumn(FIELD_TRANSACTION_TRAINING_FLAG, getTrainingFlag(transaction));
		sql.addColumn(FIELD_EMPLOYEE_ID, getSalesAssociateID(transaction));
		sql.addColumn(FIELD_CUSTOMER_INFO, getCustomerInfo(transaction));
		sql.addColumn(FIELD_CUSTOMER_INFO_TYPE, getCustomerInfoType(transaction));
		sql.addColumn(FIELD_TRANSACTION_STATUS_CODE, getTransactionStatus(transaction));
		sql.addColumn(FIELD_TENDER_REPOSITORY_ID, getTillID(transaction));
		sql.addColumn(FIELD_TRANSACTION_POST_PROCESSING_STATUS_CODE, transaction.getPostProcessingStatus());
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_TRANSACTION_REENTRY_FLAG, getTransReentryFlag(transaction));
		sql.addColumn(FIELD_TRANSACTION_SALES_ASSOCIATE_MODIFIED, getSalesAssociateModifiedFlag(transaction));
		if (transaction instanceof TillOpenCloseTransaction) {
			TillIfc till = ((TillOpenCloseTransactionIfc) transaction).getTill();
			if (transaction.getTransactionType() == TransactionIfc.TYPE_CLOSE_TILL
					&& till.getStatus() == AbstractFinancialEntityIfc.STATUS_RECONCILED)
				sql.addColumn(FIELD_TRANSACTION_RTLOG_BATCH_IDENTIFIER, TILL_NOT_APPORVED);
		}

		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			logger.error("" + e + "");
			throw new DataException(DataException.UNKNOWN, "insertTransaction", e);
		}

		// update transaction sequence number for register after each
		// transaction.
		updateTransactionSequenceNumber(dataConnection, transaction);

	}
}
