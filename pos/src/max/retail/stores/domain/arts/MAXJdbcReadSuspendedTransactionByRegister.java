
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012 - 2013 MAX, Inc.    All Rights Reserved.
  Rev 1.0	5/04/2013	Prateek	 		Initial Draft: Changes for Suspended Bills FES.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

public class MAXJdbcReadSuspendedTransactionByRegister extends JdbcDataOperation implements ARTSDatabaseIfc {

	private static Logger logger = Logger
			.getLogger(max.retail.stores.domain.arts.MAXJdbcReadSuspendedTransactionByRegister.class);

	public MAXJdbcReadSuspendedTransactionByRegister() {
		super();
		setName("MAXJdbcReadSuspendedTransactionByRegister");
	}

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcReadSuspendedTransactionByRegister.execute");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
		TransactionIfc transaction = null;
		transaction = (TransactionIfc) action.getDataObject();
		int count = readTransactionListByStatusAndRegister(connection, transaction);
		dataTransaction.setResult(new Integer(count));
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcReadSuspendedTransactionByRegister.execute");
	}

	public int readTransactionListByStatusAndRegister(JdbcDataConnection dataConnection, TransactionIfc transaction)
			throws DataException {
		int count = 0;
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.addTable(TABLE_TRANSACTION);
		sql.addColumn(FIELD_WORKSTATION_ID);
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER);
		sql.addColumn(FIELD_TRANSACTION_STATUS_CODE);
		sql.addQualifier(FIELD_RETAIL_STORE_ID, getFormattedString(transaction.getFormattedStoreID()));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE, getBusinessDate(transaction));
		sql.addQualifier(FIELD_WORKSTATION_ID, getFormattedString(transaction.getFormattedWorkstationID()));
		sql.addQualifier(FIELD_TRANSACTION_STATUS_CODE, transaction.getTransactionStatus());

		try {
			dataConnection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) dataConnection.getResult();
			logger.info(sql.getSQLString());
			while (rs.next()) {
				count++;
				int index = 0;
				rs.getInt(++index);
				rs.getInt(++index);
				rs.getInt(++index);
			}
		} catch (DataException de) {
			logger.warn("" + de + "");
			throw de;
		} catch (SQLException se) {
			logger.error("" + Util.throwableToString(se) + "");
			dataConnection.logSQLException(se, "transaction table");
			throw new DataException(DataException.SQL_ERROR, "transaction table", se);
		} catch (Exception e) {
			logger.error("" + Util.throwableToString(e) + "");
			throw new DataException(DataException.UNKNOWN, "transaction table", e);
		}

		return count;
	}

	protected String getBusinessDate(TransactionIfc transaction) {
		return (dateToSQLDateString(transaction.getBusinessDay().dateValue()));
	}

	protected TransactionIfc createTransaction() { // begin
													// createTransactionSummary()
		return (DomainGateway.getFactory().getTransactionInstance());
	}

	protected String getFormattedString(String value) {
		return "'" + value + "'";
	}
}
