/********************************************************************************
 *   
 *	Copyright (c) 2019 MAX SPAR Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	June 01, 2019		Purushotham Reddy 	Changes for POS_Amazon Pay Integration 
 *
 ********************************************************************************/

package max.retail.stores.domain.arts;

/**
@author Purushotham Reddy Sirison
**/


import max.retail.stores.domain.MAXAmazonPayResponse;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

public class MAXJdbcSaveAmazonPayResponseDetails  extends JdbcDataOperation implements MAXARTSDatabaseIfc
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger
			.getLogger(max.retail.stores.domain.arts.MAXJdbcSaveAmazonPayResponseDetails.class);

	public void execute(DataTransactionIfc dataTransaction,
			DataConnectionIfc dataConnection, DataActionIfc action)
					throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcSaveAmazonPayResponseDetails.execute()");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		MAXAmazonPayResponse resp = (MAXAmazonPayResponse) action.getDataObject();

		saveRequestAttribute(connection, resp);

		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcSaveAmazonPayResponseDetails.execute");

	}

	protected void saveRequestAttribute(JdbcDataConnection connection,
			MAXAmazonPayResponse resp) throws DataException {
		try {
			insertAmazonPayDetails(connection, resp);
		} catch (Exception e) {
			throw new DataException(e.toString());
		}

	}

	protected void insertAmazonPayDetails(JdbcDataConnection connection,
			MAXAmazonPayResponse resp) throws DataException {

		SQLInsertStatement sql = new SQLInsertStatement();
		sql.setTable(TABLE_WEB_REQUEST_LOG);
		EYSDate date = DomainGateway.getFactory().getEYSDateInstance();
		StringBuffer transanctionId = new StringBuffer();
		transanctionId.append(resp.getTransactionId());
		transanctionId.append(date.toFormattedString("yyyyMMddHHmmss"));
		sql.addColumn(FIELD_MESSAGE_ID, String.valueOf(transanctionId));
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
		if(resp.getAmountPaid()!=null){
			sql.addColumn(FIELD_SETTLE_TOTAL_AMT, resp.getAmountPaid());
		}
		sql.addColumn(FIELD_REQUEST_TYPE_B, makeSafeString(resp.getRequestTypeB()));
		sql.addColumn(FIELD_REQUEST_DATE, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_REQUEST_TIME_OUT, resp.getTimeOut());
		sql.addColumn(FIELD_REQUEST_URL, makeSafeString(resp.getUrl()));
		sql.addColumn(FIELD_RESPONSE_MESSAGE, makeSafeString(resp.getAmazonPayResponse()));
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
	
}
