/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
  Rev 1.0	 Prateek		20/05/2013		Initial Draft : Changes for TIC Customer Integration.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.arts;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import max.retail.stores.domain.loyalty.MAXLoyaltyConstants;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

public class MAXJdbcSelectLoyaltyRequest extends JdbcDataOperation implements MAXARTSDatabaseIfc {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1961842680837595604L;

	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcSelectLoyaltyRequest.class);

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcSelectLoyaltyRequest.execute()");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		// HashMap requestAttributes = (HashMap) action.getDataObject();

		List requestAttributes = selectLoyaltyDetails(connection);

		dataTransaction.setResult((Serializable) requestAttributes);
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcSelectLoyaltyRequest.execute");

	}

	protected List selectLoyaltyDetails(JdbcDataConnection connection) throws DataException {

		HashMap requestAttributes = new HashMap();
		List list = new ArrayList();
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.setTable(TABLE_WEB_REQUEST_LOG);
		sql.addColumn(FIELD_MESSAGE_ID);
		sql.addColumn(FIELD_REQUEST_TYPE_A);
		sql.addColumn(FIELD_REQUEST_TIME_OUT);
		sql.addColumn(FIELD_STORE_ID);
		sql.addColumn(FIELD_REGISTER_ID);
		sql.addColumn(FIELD_TILL_ID);
		sql.addColumn(FIELD_INVOICE_BUSINESS_DATE);
		sql.addColumn(FIELD_INVOICE_NUMBER);
		sql.addColumn(FIELD_TRANS_TOTAL_AMT);
		sql.addColumn(FIELD_SETTLE_TOTAL_AMT);
		sql.addColumn(FIELD_REQUEST_TYPE_B);
		sql.addColumn(FIELD_TIC_NUMBER);

		sql.addQualifier(FIELD_REQUEST_TYPE_A + "=" + "'" + "T" + "'");
		sql.addQualifier(FIELD_REQUEST_STATUS + "=" + "'" + "T" + "'");
		try {
			connection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) connection.getResult();
			while (rs.next()) {
				requestAttributes.put(MAXLoyaltyConstants.MESSAGE_ID, rs.getString(FIELD_MESSAGE_ID));
				requestAttributes.put(MAXLoyaltyConstants.REQUEST_TYPE_A, rs.getString(FIELD_REQUEST_TYPE_A));
				requestAttributes.put(MAXLoyaltyConstants.REQUEST_TIME_OUT, rs.getString(FIELD_REQUEST_TIME_OUT));
				requestAttributes.put(MAXLoyaltyConstants.STORE_ID, rs.getString(FIELD_STORE_ID));
				requestAttributes.put(MAXLoyaltyConstants.REGISTER_ID, rs.getString(FIELD_REGISTER_ID));
				requestAttributes.put(MAXLoyaltyConstants.TILL_ID, rs.getString(FIELD_TILL_ID));
				requestAttributes.put(MAXLoyaltyConstants.INVOICE_BUSINESS_DATE,
						rs.getString(FIELD_INVOICE_BUSINESS_DATE));
				requestAttributes.put(MAXLoyaltyConstants.INVOICE_NUMBER, rs.getString(FIELD_INVOICE_NUMBER));
				requestAttributes.put(MAXLoyaltyConstants.TRAN_TOTAL_AMOUNT, rs.getString(FIELD_TRANS_TOTAL_AMT));
				requestAttributes.put(MAXLoyaltyConstants.SETTLE_TOTAL_AMOUNT, rs.getString(FIELD_SETTLE_TOTAL_AMT));
				requestAttributes.put(MAXLoyaltyConstants.REQUEST_TYPE_B, rs.getString(FIELD_REQUEST_TYPE_B));
				requestAttributes.put(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER, rs.getString(FIELD_TIC_NUMBER));
				list.add(requestAttributes);
			}
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (SQLException se) {
			logger.error("" + se + "");
			throw new DataException(DataException.SQL_ERROR);
		} catch (Exception e) {
			logger.error("" + "");
			throw new DataException(DataException.UNKNOWN);
		}
		return list;
	}

}
