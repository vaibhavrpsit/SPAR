/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.1    Aug 22, 2018		Bhanu Priya		Changes for PAN CARD CR
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;

public class MAXJdbcSaveCustomerTypePANDeatils extends JdbcDataOperation
		implements max.retail.stores.persistence.utility.MAXARTSDatabaseIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(DataTransactionIfc dataTransaction,
			DataConnectionIfc dataConnection, DataActionIfc dataAction)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcSaveCustomerTypePANDeatils.execute");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		saveCustomerTypePANDetails(connection, null);

		dataTransaction.setResult(null);
		if (!(logger.isDebugEnabled()))
			return;
		logger.debug("JdbcSaveCustomer.execute");

	}

	public void saveCustomerTypePANDetails(JdbcDataConnection connection,
			MAXSaleReturnTransaction transaction) throws DataException {

		String panNum = "";
		String form60Num = "";
		String passportNo = "";
		String visaNum = "";
		String ackNum = "";
		MAXCustomerIfc customer = null;
		SQLInsertStatement sql = new SQLInsertStatement();
		sql.setTable(MAXARTSDatabaseIfc.TABLE_TRAN_PAN_DETAILS);

		sql.addColumn(MAXARTSDatabaseIfc.FIELD_ID_STR_RT,
				makeSafeString(transaction.getWorkstation().getStoreID()));
		sql.addColumn(MAXARTSDatabaseIfc.FIELD_ID_WS,
				makeSafeString(transaction.getWorkstation().getWorkstationID()));
		sql.addColumn(MAXARTSDatabaseIfc.FIELD_AI_TRN,
				getTransactionSequenceNumber(transaction));
		sql.addColumn(MAXARTSDatabaseIfc.FIELD_BUSINESS_DAY_DATE,
				dateToSQLDateString(transaction.getBusinessDay()));
		panNum = transaction.getPanNumber();
		customer = (MAXCustomerIfc) transaction.getCustomer();
		TransactionTotalsIfc totals = transaction.getTransactionTotals();
		if (panNum != null) {

			try {
				EncipheredDataIfc panData = FoundationObjectFactory
						.getFactory().createEncipheredDataInstance(
								panNum.getBytes());
				String pan = panData.getEncryptedNumber().toString();
				// searchCriteria.setTaxID(taxData.getMaskedNumber());
				sql.addColumn(MAXARTSDatabaseIfc.FIELD_PAN_NUM,
						makeSafeString(pan));
			} catch (EncryptionServiceException ese) {
				logger.warn("could not encrypt pan no", ese);
			}

		}
		form60Num = transaction.getForm60IDNumber();
		if (form60Num != null) {
			sql.addColumn(MAXARTSDatabaseIfc.FIELD_FORM60_IDNUM,
					makeSafeString(form60Num));
		}
		passportNo = transaction.getPassportNumber();
		if (passportNo != null) {
			sql.addColumn(MAXARTSDatabaseIfc.FIELD_PASSPORT_NUM,
					makeSafeString(passportNo));
		}
		visaNum = transaction.getVisaNumber();
		if (visaNum != null) {
			sql.addColumn(MAXARTSDatabaseIfc.FIELD_VISA_NUM,
					makeSafeString(visaNum));
		}
		ackNum = transaction.getITRAckNumber();
		if (ackNum != null) {
			sql.addColumn(MAXARTSDatabaseIfc.FIELD_ITRACK_NUM,
					makeSafeString(ackNum));
		}
		if(customer!=null){
		sql.addColumn(FIELD_CUSTOMER_ID, makeSafeString(customer.getCustomerID()));
		}
		 sql.addColumn(FIELD_TRANSACTION_NET_TOTAL, totals.getGrandTotal().getStringValue());
		sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,
				getSQLCurrentTimestampFunction());

		try {
			logger.info("Save CustomerType L" + sql.getSQLString());
			connection.execute(sql.getSQLString(), false);
		} catch (DataException de) {
			logger.error(de);
			throw de;
		} catch (Exception e) {
			logger.error(e);
			throw new DataException(0, "CustomerType", e);
		}
	}

	public String getTransactionSequenceNumber(TransactionIfc transaction) {
		return (String.valueOf(transaction.getTransactionSequenceNumber()));
	}

}
