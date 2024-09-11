/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Jan 06, 2017		Ashish Yadav		Changes for Online redemption loyalty OTP FES
 *
 ********************************************************************************/
package max.retail.stores.domain.arts;

import java.util.HashMap;

import max.retail.stores.domain.loyalty.MAXLoyaltyConstants;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

public class MAXJdbcUpdateLoyaltyRequest extends JdbcDataOperation implements MAXARTSDatabaseIfc {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4739616224286547742L;

	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcUpdateLoyaltyRequest.class);

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcUpdateLoyaltyRequest.execute()");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		HashMap reponseAttributes = (HashMap) action.getDataObject();
		/**
		 * This method is commented because this method will update the request
		 * and responce in table WEB_RQST_LOG. but according to FES, request and
		 * responce are not save in any table. and also WEB_RQST_LOG table are
		 * not present in DB
		 **/
		// updateRequestAttribute(connection, reponseAttributes);

		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcUpdateLoyaltyRequest.execute");

	}

	protected void updateRequestAttribute(JdbcDataConnection connection, HashMap responseAttributes)
			throws DataException {
		try {
			updateLoyaltyDetails(connection, responseAttributes);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN);
		}

	}

	protected void updateLoyaltyDetails(JdbcDataConnection connection, HashMap responseAttributes)
			throws DataException {

		SQLUpdateStatement sql = new SQLUpdateStatement();
		// Table
		sql.setTable(TABLE_WEB_REQUEST_LOG);
		// Qualifiers
		sql.addQualifier(
				FIELD_MESSAGE_ID + " = " + String.valueOf(responseAttributes.get(MAXLoyaltyConstants.MESSAGE_ID)));
		// Fields
		if (responseAttributes.get(MAXLoyaltyConstants.FLAG).toString() == MAXLoyaltyConstants.TIMEOUT_FLAG) {
			sql.addColumn(FIELD_REQUEST_STATUS,
					makeSafeString((String) responseAttributes.get(MAXLoyaltyConstants.REQUEST_STATUS)));
		} else {
			sql.addColumn(FIELD_REQUEST_STATUS,
					makeSafeString((String) responseAttributes.get(MAXLoyaltyConstants.REQUEST_STATUS)));
			sql.addColumn(FIELD_RESPONSE_APPROVED_FLAG,
					makeSafeString((String) responseAttributes.get(MAXLoyaltyConstants.RESPONSE_APPROVED_FLAG)));
			if (responseAttributes.get(MAXLoyaltyConstants.RESPONSE_APPROVED_VALUE) != null) {
				sql.addColumn(FIELD_RESPONSE_APPROVED_VALUE,
						(String) responseAttributes.get(MAXLoyaltyConstants.RESPONSE_APPROVED_VALUE).toString());
			}
			sql.addColumn(FIELD_RESPONSE_MESSAGE,
					makeSafeString((String) responseAttributes.get(MAXLoyaltyConstants.RESPONSE_MESSAGE)));
			sql.addColumn(FIELD_RESPONSE_RECEIVED_DATE_TIME, getSQLCurrentTimestampFunction());
			 /**Changes start for rev 1.0 (Ashish :Loyalty OTP)**/
            if(responseAttributes.get(MAXLoyaltyConstants.REQUEST_URL)!=null)
	sql.addColumn(FIELD_REQUEST_URL,
			makeSafeString((String) responseAttributes
					.get(MAXLoyaltyConstants.REQUEST_URL)));
	if (responseAttributes.get(MAXLoyaltyConstants.REQUEST_TYPE_B) != null) {
		sql.addColumn(FIELD_REQUEST_TYPE_B,
				makeSafeString((String) responseAttributes
						.get(MAXLoyaltyConstants.REQUEST_TYPE_B)));
	}
	/**Changes start for rev 1.0 (Ashish :Loyalty OTP)**/
		}
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
