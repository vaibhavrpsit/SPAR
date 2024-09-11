/********************************************************************************
 *   
 *	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
 *	
 *  Rev	1.1 	21-Apr-2017		Nadia.Arora		
 *  Fix for Reversal Data to be saved to database
 *
 *	Rev	1.0 	05-Apr-2017		Nadia.Arora		Paytm Integration
 *
 ********************************************************************************/
package max.retail.stores.domain.arts;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.log4j.Logger;


import max.retail.stores.domain.MAXPaytmResponse;
import max.retail.stores.domain.tender.paytm.MAXPaytmTenderConstants;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;

public class MAXJdbcSavePaytmDetails  extends JdbcDataOperation implements
MAXARTSDatabaseIfc {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1961842680837595604L;

	private static Logger logger = Logger
			.getLogger(max.retail.stores.domain.arts.MAXJdbcSaveLoyaltyRequest.class);

	public void execute(DataTransactionIfc dataTransaction,
			DataConnectionIfc dataConnection, DataActionIfc action)
					throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcSaveLoyaltyRequest.execute()");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		MAXPaytmResponse resp = (MAXPaytmResponse) action.getDataObject();

		saveRequestAttribute(connection, resp);

		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcSaveLoyaltyRequest.execute");

	}

	protected void saveRequestAttribute(JdbcDataConnection connection,
			MAXPaytmResponse resp) throws DataException {
		try {
			insertPaytmDetails(connection, resp);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN);
		}

	}

	protected void insertPaytmDetails(JdbcDataConnection connection,
			MAXPaytmResponse resp) throws DataException {

		SQLInsertStatement sql = new SQLInsertStatement();
		sql.setTable(TABLE_WEB_REQUEST_LOG);
		sql.addColumn(FIELD_MESSAGE_ID, String.valueOf(resp.getOrderId()));
		sql.addColumn(FIELD_REQUEST_TYPE_A, makeSafeString(resp.getRequestTypeA()));
		sql.addColumn(FIELD_REQUEST_STATUS, makeSafeString(resp.getReqRespStatus()));

		sql.addColumn(FIELD_ID_MESSAGE_TYPMEOUT, null);
		sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(resp.getStoreId()));
		sql.addColumn(FIELD_TILL_ID, makeSafeString(resp.getTillId()));
		sql.addColumn(FIELD_REGISTER_ID, makeSafeString(resp.getRegisterId()));
		sql.addColumn(FIELD_INVOICE_BUSINESS_DATE, getBusinessDayString(resp.getBussinessdate()));
		sql.addColumn(FIELD_INVOICE_NUMBER, makeSafeString(resp.getTransactionId()));
		try {
			if(resp.getPhoneNumber() != null) {
				sql.addColumn(FIELD_TIC_NUMBER, makeSafeString(encryptRequestInfo(connection, resp.getPhoneNumber())));
			}else {
				sql.addColumn(FIELD_TIC_NUMBER, makeSafeString("1111111111"));
			}
			
		} catch (SQLException e) {
			logger.error("Error in saving phone number for paytm " + e.getMessage());
			sql.addColumn(FIELD_TIC_NUMBER, makeSafeString(resp.getPhoneNumber()));
		}
		sql.addColumn(FIELD_TRANS_TOTAL_AMT, resp.getTotalTransactionAmt());
		sql.addColumn(FIELD_SETTLE_TOTAL_AMT, resp.getAmountPaid());
		sql.addColumn(FIELD_REQUEST_TYPE_B, makeSafeString(resp.getRequestTypeB()));
		sql.addColumn(FIELD_REQUEST_DATE, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_REQUEST_TIME_OUT, resp.getTimeOut());
		String uri = null;
		/* Chnages for Rev 1.1 starts*/
		if(resp.getUrl() != null && !resp.getUrl().equalsIgnoreCase("")) {
			uri = resp.getUrl();
		}else {
		if(resp.getRequestTypeB().equalsIgnoreCase(MAXPaytmTenderConstants.BURNED))
		{
			uri = Gateway.getProperty("application", "PaytmWithdrawURL", "");
		}
		else if(resp.getRequestTypeB().equalsIgnoreCase("G"))
		{
			uri = Gateway.getProperty("application", "createPaytmQRCodeURL", "");
		}
		else if(resp.getRequestTypeB().equalsIgnoreCase("S"))
		{
			uri = Gateway.getProperty("application", "transactionStatusPaytmQRCodeURL", "");
		}else
		{
			uri = Gateway.getProperty("application", "PaytmReversalURL", "");
		}
		}
		/* Chnages for Rev 1.1 starts*/
		sql.addColumn(FIELD_REQUEST_URL, makeSafeString(uri));
		sql.addColumn(FIELD_RESPONSE_MESSAGE, makeSafeString(resp.getPaytmResponse()));
		//sql.addColumn(column);
		//sql.addColumn(FIELD_WALLET_ID, null));
		sql.addColumn(FIELD_RESPONSE_RECEIVED_DATE_TIME, getSQLCurrentTimestampFunction());
		try {
			logger.info(sql.getSQLString());
			connection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		}  catch (Exception e) {
			logger.error("" + "");
			throw new DataException(DataException.UNKNOWN);
		}
	}

	public String getBusinessDayString(EYSDate date) {
		return (dateToSQLDateString(date));
	}
	
	protected String encryptRequestInfo(JdbcDataConnection dataConnection,
			String phoneNumber) throws SQLException {
		CallableStatement cs;
		Connection conn = dataConnection.getConnection();
		cs = conn.prepareCall("{? = call POS_ENC(?)}");

		// Register the type of the return value
		cs.registerOutParameter(1, Types.VARCHAR);

		// Set the value for the IN parameter
		cs.setString(2, phoneNumber); // provide the value

		// Execute and retrieve the returned value
		cs.execute();
		String retValue = cs.getString(1);
		return retValue;
	}

}