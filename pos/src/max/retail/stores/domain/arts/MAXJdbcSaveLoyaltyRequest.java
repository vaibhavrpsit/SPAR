/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
  Rev 1.0	 Prateek		20/05/2013		Initial Draft : Changes for TIC Customer Integration.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.arts;

import java.util.HashMap;

import max.retail.stores.domain.loyalty.MAXLoyaltyConstants;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

public class MAXJdbcSaveLoyaltyRequest extends JdbcDataOperation implements MAXARTSDatabaseIfc {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1961842680837595604L;

	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcSaveLoyaltyRequest.class);

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcSaveLoyaltyRequest.execute()");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		HashMap requestAttributes = (HashMap) action.getDataObject();

		saveRequestAttribute(connection, requestAttributes);

		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcSaveLoyaltyRequest.execute");

	}

	protected void saveRequestAttribute(JdbcDataConnection connection, HashMap requestAttributes) throws DataException {
		try {
			insertLoyaltyDetails(connection, requestAttributes);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN);
		}

	}

	protected void insertLoyaltyDetails(JdbcDataConnection connection, HashMap requestAttributes) throws DataException {

		SQLInsertStatement sql = new SQLInsertStatement();
		sql.setTable(TABLE_WEB_REQUEST_LOG);
		sql.addColumn(FIELD_MESSAGE_ID, String.valueOf(requestAttributes.get(MAXLoyaltyConstants.MESSAGE_ID)));
		sql.addColumn(FIELD_REQUEST_TYPE_A,
				makeSafeString((String) requestAttributes.get(MAXLoyaltyConstants.REQUEST_TYPE_A)));
		sql.addColumn(FIELD_REQUEST_STATUS,
				makeSafeString((String) requestAttributes.get(MAXLoyaltyConstants.REQUEST_STATUS)));

		// Checking condition for Time-out case
		if (requestAttributes.get(MAXLoyaltyConstants.TIME_OUT_REQUEST_MESSAGE_ID) != null) {
			sql.addColumn(FIELD_ID_MESSAGE_TYPMEOUT,
					String.valueOf(requestAttributes.get(MAXLoyaltyConstants.TIME_OUT_REQUEST_MESSAGE_ID)));
		}
		sql.addColumn(FIELD_STORE_ID, makeSafeString((String) requestAttributes.get(MAXLoyaltyConstants.STORE_ID)));
		sql.addColumn(FIELD_TILL_ID, makeSafeString((String) requestAttributes.get(MAXLoyaltyConstants.TILL_ID)));
		sql.addColumn(FIELD_REGISTER_ID,
				makeSafeString((String) requestAttributes.get(MAXLoyaltyConstants.REGISTER_ID)));
		sql.addColumn(FIELD_INVOICE_BUSINESS_DATE,
				getBusinessDayString((EYSDate) requestAttributes.get(MAXLoyaltyConstants.INVOICE_BUSINESS_DATE)));
		sql.addColumn(FIELD_INVOICE_NUMBER, String.valueOf(requestAttributes.get(MAXLoyaltyConstants.INVOICE_NUMBER)));
		sql.addColumn(FIELD_TIC_NUMBER,
				makeSafeString((String) requestAttributes.get(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER)));
		sql.addColumn(FIELD_TRANS_TOTAL_AMT,
				(String) requestAttributes.get(MAXLoyaltyConstants.TRAN_TOTAL_AMOUNT).toString());
		sql.addColumn(FIELD_SETTLE_TOTAL_AMT,
				(String) requestAttributes.get(MAXLoyaltyConstants.SETTLE_TOTAL_AMOUNT).toString());
		if (requestAttributes.get(MAXLoyaltyConstants.REQUEST_TYPE_B) != null) {
			sql.addColumn(FIELD_REQUEST_TYPE_B,
					makeSafeString((String) requestAttributes.get(MAXLoyaltyConstants.REQUEST_TYPE_B)));
		}
		sql.addColumn(FIELD_REQUEST_DATE, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_REQUEST_TIME_OUT,
				(String) requestAttributes.get(MAXLoyaltyConstants.REQUEST_TIME_OUT).toString());
		sql.addColumn(FIELD_REQUEST_URL,
				makeSafeString((String) requestAttributes.get(MAXLoyaltyConstants.REQUEST_URL)));

		try {
			connection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			logger.error("" + "");
			throw new DataException(DataException.UNKNOWN);
		}
	}

	public String getBusinessDayString(EYSDate date) {
		return (dateToSQLDateString(date));
	}

}
