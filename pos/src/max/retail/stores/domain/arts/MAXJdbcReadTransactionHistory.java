/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  Rev 1.1  21/June/2013	Prateek, Changes done for BUG 6554
*  Rev 1.0  13/June/2013	Jyoti Rawal, Initial Draft: Fix for Bug 6230 - Return with No receipt:- Search transaction by Credit/Debit Card.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.arts;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.arts.ARTSExcludePostVoidSQL;
import oracle.retail.stores.domain.arts.JdbcReadTransactionHistory;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.utility.Util;

public class MAXJdbcReadTransactionHistory extends JdbcReadTransactionHistory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5475753092179776888L;
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcReadTransactionHistory.class);

	protected Vector readTransactionHistory(JdbcDataConnection dataConnection, SearchCriteriaIfc criteria)
			throws DataException { // begin
									// readTransactionHistory()
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransactionHistory.readTransactionHistory"
					+ "(JdbcDataConnection dataConnection,SearchCriteriaIfc criteria)" + "");
		Vector transVector = new Vector();
		Vector postVoidTransVector = new Vector();

		SQLSelectStatement sql = buildBaseSQL();
		SQLSelectStatement postVoidSql = buildPostVoidSQL();

		CustomerIfc customer = criteria.getCustomer();
		String customerID = null;
		if (customer != null) {
			customerID = customer.getCustomerID();
		} else {
			customerID = criteria.getCustomerID();
		}
		EYSDate[] dateRange = criteria.getDateRange();
		String storeNumber = criteria.getStoreNumber();
		String trainingMode = criteria.getTrainingMode();
		int maxMatches = criteria.getMaximumMatches();
		/** MAX Rev 1.1 Change : Start **/
		String actCardNumber = null;
		String cardNumber = "";
		// extract card number data from criteria object
		actCardNumber = criteria.getMaskedAccountNumber();
		if (actCardNumber != null && actCardNumber.length() > 0) {
			StringBuffer buf = new StringBuffer(actCardNumber);
			cardNumber = buf.replace(6, buf.length() - 4, "******").toString();
		} else {
			cardNumber = criteria.getMaskedAccountNumber();
		}
		/** MAX Rev 1.1 Change : End **/
		String giftcardNumber = criteria.getMaskedGiftCardNumber();
		String accountNumber = criteria.getMaskedMICRNumber();

		String itemSizeCode = criteria.getItemSizeCode();
		String itemID = criteria.getItemID();
		String maskedAccountNumber = criteria.getMaskedAccountNumber();
		String accountNumberToken = criteria.getAccountNumberToken();
		String maskedGiftcardNumber = criteria.getMaskedGiftCardNumber();
		String maskedMICRNumber = criteria.getMaskedMICRNumber();
		// Boolean flag indicating we should filter the results by
		// exact matches to decrypted card number.

		boolean bFilterByCardNumber = false;

		if (customerID != null) {
			// add customer ID qualifier
			sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_CUSTOMER_ID + " = '" + customerID + "'");
			postVoidSql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_CUSTOMER_ID + " = '" + customerID + "'");
		}

		if (storeNumber != null) {
			// add store number qualifier
			sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_RETAIL_STORE_ID + " = '" + storeNumber + "'");
			postVoidSql
					.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_RETAIL_STORE_ID + " = '" + storeNumber + "'");
		}

		if (trainingMode != null) {
			// add store number qualifier
			sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_TRAINING_FLAG + " ='" + trainingMode + "'");
		}
		if (dateRange != null) {
			if (dateRange[0] != null) {
				// add begin business date qualifier
				sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE + " >= "
						+ dateToSQLDateString(dateRange[0]));
				postVoidSql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE + " >= "
						+ dateToSQLDateString(dateRange[0]));
			}

			if (dateRange[1] != null) {
				// add end business date qualifier
				sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE + " <= "
						+ dateToSQLDateString(dateRange[1]));
				postVoidSql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE + " <= "
						+ dateToSQLDateString(dateRange[1]));
			}
		}

		if (!Util.isEmpty(accountNumberToken)) {
			searchCreditDebitTransactionsByToken(sql, accountNumberToken);
		} else if (!Util.isEmpty(maskedMICRNumber)) {
			searchCheckTransactions(sql, maskedMICRNumber);
		} else if (!Util.isEmpty(maskedAccountNumber)) {
			searchCreditDebitTransactions(sql, maskedAccountNumber);
		} else if (!Util.isEmpty(maskedGiftcardNumber)) {
			searchGiftCardTransactions(sql, maskedGiftcardNumber);
		}

		searchForLineItems(sql, itemSizeCode, itemID, criteria.getItemSerialNumber());

		// add maximum matches parameter
		// sql.setMaximumMatches(maxMatches);

		// add ordering clauses
		addOrdering(sql);

		try {
			// build sub-select to exclude post voided transaction
			postVoidTransVector = executePostVoidSQL(dataConnection, postVoidSql);
			ARTSExcludePostVoidSQL.buildSQL(sql, postVoidTransVector);
			if (criteria.getExclusionMode()) {
				excludeOtherSQL(sql);
			}
			transVector = executeAndParse(dataConnection, sql, criteria.getLocaleRequestor());

			ArrayList<Integer> arrOfSpOrderInitiateTransNo = getSpecialOrderInitiateTransNo(transVector,
					dataConnection);
			if (arrOfSpOrderInitiateTransNo.size() > 0) {
				for (int i = 0; i < transVector.size(); i++) {
					int transactionNO = (int) ((TransactionSummaryIfc) transVector.get(i)).getTransactionID()
							.getSequenceNumber();
					if (arrOfSpOrderInitiateTransNo.contains(new Integer(transactionNO))) {
						transVector.remove(i);
					}
				}
			}
		} catch (DataException de) {
			logger.warn("" + de + "");
			throw de;
		} catch (SQLException se) {
			dataConnection.logSQLException(se, "transaction table");
			throw new DataException(DataException.SQL_ERROR, "transaction table", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "transaction table", e);
		}

		if (transVector.isEmpty()) {
			logger.warn("No transactions found");
			throw new DataException(DataException.NO_DATA, "No transactions found");
		} else {
			if (logger.isInfoEnabled())
				logger.info("Transactions found:  " + Integer.toString(transVector.size()) + "");
		}

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransactionHistory.readTransactionHistory"
					+ "(JdbcDataConnection dataConnection,SearchCriteriaIfc criteria)" + "");

		if ((maxMatches > 0) && (transVector.size() > maxMatches)) {
			throw new DataException(DataException.RESULT_SET_SIZE, "Too many matches");
		}
		return (transVector);
	}
}
