/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.arts;

import java.util.List;

import max.retail.stores.domain.financial.MAXFinancialTotals;
import max.retail.stores.domain.tender.MAXDenominationCount;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.utility._360Date;
import oracle.retail.stores.common.utility._360DateIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;

import org.apache.log4j.Logger;

public class MAXJdbcSaveCashDenomination extends JdbcUtilities implements MAXARTSDatabaseIfc {

	private static Logger logger = Logger.getLogger(MAXJdbcSaveAcquirerBankDetails.class);

	protected boolean insertCashData(JdbcDataConnection dataConnection, TillIfc till, RegisterIfc register)
			throws DataException {
		FinancialTotalsIfc totals = till.getTotals();
		List dnmList = ((MAXFinancialTotals) totals).getCashDenomination();
		if (dnmList != null) {
			for (int i = 0; i < dnmList.size(); i++) {
				MAXDenominationCount count = (MAXDenominationCount) dnmList.get(i);
				if (count.getQuantity() != 0)
					if (!insertCashData(dataConnection, register, till, count.getCurrency(), count.getQuantity(),
							count.getSubType()))
						return false;
			}
		}
		return true;
	}

	private boolean insertCashData(JdbcDataConnection dataConnection, RegisterIfc register, TillIfc till,
			CurrencyIfc currency, int quantity, String subType) throws DataException {

		boolean returnCode = true;

		SQLInsertStatement sql = new SQLInsertStatement();
		sql.setTable(TABLE_CASH_DENOMINATION_DETAIL);
		sql.addColumn(FIELD_RECONCILE_TENDER_STORE_ID, getStoreID(register));
		sql.addColumn(FIELD_RECONCILE_TENDER_WORKSTATION_ID, getRegisterId(register));
		sql.addColumn(FIELD_RECONCILE_TENDER_TILL_ID, getTillID(till));
		sql.addColumn(FIELD_RECONCILE_BUSINESS_DATE, getBusinessDay(till.getBusinessDate()));
		sql.addColumn(FIELD_RECONCILE_TENDER_TRANSACTION_ID, getTransactionSequenceNumber(register));
		sql.addColumn(FIELD_CASH_DENOMINATION, getAmount(currency));
		sql.addColumn(FIELD_CASH_QUANTITY, quantity);
		sql.addColumn(FIELD_CASH_DNM_NAME, getSafeString(subType));
		sql.addColumn(FIELD_OPERATOR_ID, "'" + till.getSignOffOperator().getLoginID() + "'");
		sql.addColumn(FIELD_OPERATOR_MODIFIER_ID, "''");
		String timestamp = getCurrentDate();
		sql.addColumn(FIELD_CREATION_TIMESTAMP, timestamp);
		sql.addColumn(FIELD_MODIFICATION_TIMESTAMP, timestamp);

		dataConnection.execute(sql.getSQLString());
		logger.info(sql.getSQLString());
		if (0 >= dataConnection.getUpdateCount()) {
			returnCode = false;
		}
		return (returnCode);
	}

	protected String getStoreID(RegisterIfc register) {
		return ("'" + register.getWorkstation().getStoreID() + "'");
	}

	protected String getTillID(TillIfc till) {
		return ("'" + till.getTillID() + "'");
	}

	protected String getRegisterId(RegisterIfc register) {
		return ("'" + register.getWorkstation().getWorkstationID() + "'");
	}

	protected String getBusinessDay(EYSDate businessDate) {
		return (dateToSQLDateString(businessDate.dateValue()));
	}

	protected String getTransactionSequenceNumber(RegisterIfc register) {
		return (String.valueOf(register.getLastTransactionSequenceNumber()));
	}

	protected String getAmount(CurrencyIfc amount) {
		return amount.getStringValue();
	}

	protected String getCurrentDate() {
		_360DateIfc date = new _360Date();
		date.initialize(0);
		return dateToSQLTimestampFunction(date);
	}

	protected String getSafeString(String value) {
		return "'" + value + "'";
	}
}
