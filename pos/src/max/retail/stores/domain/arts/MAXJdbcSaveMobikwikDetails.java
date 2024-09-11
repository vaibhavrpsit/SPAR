package max.retail.stores.domain.arts;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import max.retail.stores.domain.MAXMobikwikResponse;
import max.retail.stores.domain.paytm.MAXPaytmResponse;
import max.retail.stores.domain.tender.mobikwik.MAXMobikwikTenderConstants;
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
import oracle.retail.stores.domain.DomainGateway;

import org.apache.log4j.Logger;

public class MAXJdbcSaveMobikwikDetails  extends JdbcDataOperation implements MAXARTSDatabaseIfc
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger
			.getLogger(max.retail.stores.domain.arts.MAXJdbcSaveMobikwikDetails.class);

	public void execute(DataTransactionIfc dataTransaction,
			DataConnectionIfc dataConnection, DataActionIfc action)
					throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcSaveMobikwikDetails.execute()");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		MAXMobikwikResponse resp = (MAXMobikwikResponse) action.getDataObject();

		saveRequestAttribute(connection, resp);

		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcSaveMobikwikDetails.execute");

	}

	protected void saveRequestAttribute(JdbcDataConnection connection,
			MAXMobikwikResponse resp) throws DataException {
		try {
			insertMobikwikDetails(connection, resp);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN);
		}

	}

	protected void insertMobikwikDetails(JdbcDataConnection connection,
			MAXMobikwikResponse resp) throws DataException {

		SQLInsertStatement sql = new SQLInsertStatement();
		sql.setTable(TABLE_WEB_REQUEST_LOG);
		// below line is modified by atul shukla
	EYSDate date = DomainGateway.getFactory().getEYSDateInstance();
		 StringBuffer transanctionId = new StringBuffer();
    transanctionId.append(resp.getTransactionId());
    transanctionId.append(date.toFormattedString("yyyyMMddHHmmss"));	
		sql.addColumn(FIELD_MESSAGE_ID, String.valueOf(transanctionId));
	//	sql.addColumn(FIELD_MESSAGE_ID, String.valueOf(resp.getOrderId())); 
		sql.addColumn(FIELD_REQUEST_TYPE_A, makeSafeString(resp.getRequestTypeA()));
		sql.addColumn(FIELD_REQUEST_STATUS, makeSafeString(resp.getReqRespStatus()));

		sql.addColumn(FIELD_ID_MESSAGE_TYPMEOUT, null);
		sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(resp.getStoreId()));
		sql.addColumn(FIELD_TILL_ID, makeSafeString(resp.getTillId()));
		sql.addColumn(FIELD_REGISTER_ID, makeSafeString(resp.getRegisterId()));
		sql.addColumn(FIELD_INVOICE_BUSINESS_DATE, getBusinessDayString(resp.getBussinessdate()));
		sql.addColumn(FIELD_INVOICE_NUMBER, makeSafeString(resp.getTransactionId()));
		sql.addColumn(FIELD_TIC_NUMBER, makeSafeString(resp.getPhoneNumber()));
		sql.addColumn(FIELD_TRANS_TOTAL_AMT, resp.getTotalTransactionAmt());
		sql.addColumn(FIELD_SETTLE_TOTAL_AMT, resp.getAmountPaid());
		sql.addColumn(FIELD_REQUEST_TYPE_B, makeSafeString(resp.getRequestTypeB()));
		sql.addColumn(FIELD_REQUEST_DATE, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_REQUEST_TIME_OUT, resp.getTimeOut());
		/* Chnages for Rev 1.1 starts*/
			//uri = Gateway.getProperty("application", "PaytmWithdrawURL", "");
		//	uri = Gateway.getProperty("application", "MobikwikWithdrawURL", "");
		/* Chnages for Rev 1.1 starts*/
		sql.addColumn(FIELD_REQUEST_URL, makeSafeString(resp.getUrl()));
		sql.addColumn(FIELD_RESPONSE_MESSAGE, makeSafeString(resp.getMobikwikResponse()));
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
	

		// Register the type of the return value

		// Set the value for the IN parameter

		// Execute and retrieve the returned value
}
