/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  29/April/2013               Himanshu              MAX-StoreCreditTender-FES_v1 2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

import max.retail.stores.domain.factory.MAXDomainObjectFactoryIfc;
import max.retail.stores.domain.manager.centralvalidation.MAXCentralizedDataEntryIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility._360DateIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * This class provides the methods needed to read or update store credit data.
 * <P>
 * 
 * @author Shavinki Goyal
 **/
// -------------------------------------------------------------------------
public class MAXJdbcReadUpdateStoreCredit extends JdbcUtilities implements MAXARTSDatabaseIfc, DataOperationIfc {
	/**
	 * The logger to which log messages will be sent.
	 */
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcReadUpdateStoreCredit.class);

	/** The DataOperation name **/
	protected String name = null;

	private static String transactionName = "MGLookUpCouponDataTransaction";

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 */
	// ---------------------------------------------------------------------
	public MAXJdbcReadUpdateStoreCredit() {
		super();
		setName("MGJdbcReadUpdateStoreCredit");
	}

	// -------------------------------------------------------------------------
	/**
	 * MFL Customizations Executes the SQL statements against the database.
	 * <P>
	 * 
	 * @param dataTransaction
	 *            The data transaction
	 * @param dataConnection
	 *            The connection to the data source
	 * @param action
	 *            The information passed by the valet
	 * @exception DataException
	 *                upon error
	 * 
	 *                Added by Shavinki Goyal
	 **/
	// -------------------------------------------------------------------------

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
		MAXCentralizedDataEntryIfc[] strCrdts = null;

		Object obj = action.getDataObject();
		if (obj != null) {
			strCrdts = (MAXCentralizedDataEntryIfc[]) action.getDataObject();
		}
		MAXCentralizedDataEntryIfc[] unverifiedCoupons = null;

		try {
			if (strCrdts != null) {
				if (dataTransaction.getTransactionName().equals(transactionName)) {
					lookUpTenderedStoreCredit(connection, strCrdts);
				} else {
					for (int i = 0; i < strCrdts.length; i++) {
						updateStrCrdtValidationFlag(connection, strCrdts[i]);
					}
				}
			} else {
				unverifiedCoupons = lookUpUnVerifiedStoreCredit(connection);
				dataTransaction.setResult(unverifiedCoupons);
			}
		} catch (DataException de) {
			throw de;
		}

	}

	public void initialize() throws DataException {
		// TODO Auto-generated method stub

	}

	// ----------------------------------------------------------------------
	/**
	 * MFL Customizations Validates if store credit has already been tendered
	 * <P>
	 * 
	 * @param dataConnection
	 * @param strCrdtList
	 * 
	 *            Added by Shavinki Goyal
	 **/
	// ----------------------------------------------------------------------
	public void lookUpTenderedStoreCredit(JdbcDataConnection dataConnection, MAXCentralizedDataEntryIfc[] strCrdtList)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcLookupStoreCredit.lookupTenderedStoreCredit()");

		for (int i = 0; i < strCrdtList.length; i++) {
			SQLSelectStatement sql = new SQLSelectStatement();

			// Table
			sql.setTable(TABLE_STORE_CREDIT_TENDER_LINE_ITEM);
			sql.addTable(TABLE_TRANSACTION, ALIAS_TRANSACTION);

			// add columns
			sql.addColumn(TABLE_STORE_CREDIT_TENDER_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID);
			sql.addColumn(TABLE_STORE_CREDIT_TENDER_LINE_ITEM + "." + FIELD_WORKSTATION_ID);
			sql.addColumn(TABLE_STORE_CREDIT_TENDER_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
			sql.addColumn(TABLE_STORE_CREDIT_TENDER_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE);
			sql.addColumn(TABLE_STORE_CREDIT_TENDER_LINE_ITEM + "." + FIELD_STORE_CREDIT_BALANCE);
			MAXCentralizedDataEntryIfc strCrdt = strCrdtList[i];
			// add Qualifiers
			sql.addQualifier(FIELD_STORE_CREDIT_ID + " = " + makeSafeString(strCrdt.getStrCrdtNumber()));

			sql.addQualifier(
					addAbsFunction(FIELD_STORE_CREDIT_BALANCE) + " = " + addAbsFunction(strCrdt.getStrCrdtAmt()));
			sql.addQualifier(FIELD_STORE_CREDIT_TENDER_STATE + " = " + makeSafeString(TenderCertificateIfc.REDEEMED));

			// add Join Qualifiers
			sql.addJoinQualifier(TABLE_STORE_CREDIT_TENDER_LINE_ITEM, FIELD_RETAIL_STORE_ID, ALIAS_TRANSACTION,
					FIELD_RETAIL_STORE_ID);

			sql.addJoinQualifier(TABLE_STORE_CREDIT_TENDER_LINE_ITEM, FIELD_WORKSTATION_ID, ALIAS_TRANSACTION,
					FIELD_WORKSTATION_ID);

			sql.addJoinQualifier(TABLE_STORE_CREDIT_TENDER_LINE_ITEM, FIELD_TRANSACTION_SEQUENCE_NUMBER,
					ALIAS_TRANSACTION, FIELD_TRANSACTION_SEQUENCE_NUMBER);

			sql.addJoinQualifier(TABLE_STORE_CREDIT_TENDER_LINE_ITEM, FIELD_BUSINESS_DAY_DATE, ALIAS_TRANSACTION,
					FIELD_BUSINESS_DAY_DATE);

			// add Order by desc
			sql.addOrdering(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_END_DATE_TIMESTAMP + " desc");

			try {
				dataConnection.execute(sql.getSQLString());
				ResultSet rs = (ResultSet) dataConnection.getResult();
				boolean tendered = false;
				if (rs.next()) {
					tendered = true;
					strCrdt.setValidationFailed(true);
					String storeId = rs.getString(FIELD_RETAIL_STORE_ID);
					String businessDate = inQuotes(rs.getString(FIELD_BUSINESS_DAY_DATE));
					String transId = rs.getString(FIELD_TRANSACTION_SEQUENCE_NUMBER);
					if (!(storeId.equals(strCrdt.getStoreId()) && businessDate.equals(strCrdt.getBusinessDate())
							&& transId.equals(strCrdt.getTransactionID()))) {
						strCrdt.setValidationFailed(true);
						tendered = true;
					}

				}
				if (!tendered) {
					lookupStoreCredit(dataConnection, strCrdt);
				}
				rs.close();

			} catch (DataException de) {
				logger.warn("" + de + "");
				throw de;
			} catch (SQLException se) {
				dataConnection.logSQLException(se, "lookupTenderedStoreCredit");
				throw new DataException(DataException.SQL_ERROR, "lookupTenderedStoreCredit", se);
			} catch (Exception e) {
				throw new DataException(DataException.UNKNOWN, "lookupTenderedStoreCredit", e);
			}
		}

		if (logger.isDebugEnabled())
			logger.debug("JdbcLookupStoreCredit.lookupTenderedStoreCredit()");
	}

	// ----------------------------------------------------------------------
	/**
	 * MFL Customizations Searches for a store credit in DO_CR_STR
	 * <P>
	 * 
	 * @param dataConnection
	 * @param storeCredit
	 * 
	 *            Added by Shavinki Goyal
	 **/
	// ----------------------------------------------------------------------
	public void lookupStoreCredit(JdbcDataConnection dataConnection, MAXCentralizedDataEntryIfc storeCredit)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcLookupStoreCredit.lookupStoreCredit()");

		SQLSelectStatement sql = new SQLSelectStatement();
		// Table
		sql.setTable(TABLE_STORE_CREDIT);
		// add column
		sql.addColumn(FIELD_STORE_CREDIT_ID);

		// Qualifiers
		sql.addQualifier(FIELD_STORE_CREDIT_ID + " = " + makeSafeString(storeCredit.getStrCrdtNumber()));
		sql.addQualifier(FIELD_STORE_CREDIT_BALANCE + " = " + storeCredit.getStrCrdtAmt());
		sql.addQualifier(
				FIELD_STORE_CREDIT_TRAINING_FLAG + " = " + makeStringFromBoolean(storeCredit.isTrainingMode()));

		try {
			dataConnection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) dataConnection.getResult();

			if (rs.next()) {
			} else {
				storeCredit.setValidationFailed(true);
				throw new DataException(DataException.NO_DATA, "lookupStoreCredit");
			}

			rs.close();
		} catch (DataException de) {
			logger.warn("" + de + "");
			throw de;
		} catch (SQLException se) {
			dataConnection.logSQLException(se, "lookupStoreCredit");
			throw new DataException(DataException.SQL_ERROR, "lookupStoreCredit", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "lookupStoreCredit", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("JdbcLookupStoreCredit.lookupStoreCredit()");
	}

	// ----------------------------------------------------------------------
	/**
	 * MFL Customizations Returns the unverified tendered store credits
	 * <P>
	 * 
	 * @param dataConnection
	 * 
	 *            Added by Shavinki Goyal
	 **/
	// ----------------------------------------------------------------------
	public MAXCentralizedDataEntryIfc[] lookUpUnVerifiedStoreCredit(JdbcDataConnection dataConnection)
			throws DataException {

		MAXCentralizedDataEntryIfc[] entries = null;

		SQLSelectStatement sql = new SQLSelectStatement();
		sql.setTable(TABLE_STORE_CREDIT_TENDER_LINE_ITEM);
		sql.addColumn(FIELD_RETAIL_STORE_ID);
		sql.addColumn(FIELD_WORKSTATION_ID);
		sql.addColumn(FIELD_BUSINESS_DAY_DATE);
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER);
		sql.addColumn(FIELD_STORE_CREDIT_ID);
		sql.addColumn(FIELD_STORE_CREDIT_BALANCE);
		sql.addQualifier(FIELD_STORE_CREDIT_VALIDATION_FLAG, String.valueOf('0'));

		try {
			if (logger.isDebugEnabled())
				logger.debug(sql.getSQLString());
			dataConnection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) dataConnection.getResult();
			// parse result set
			entries = parseSelectedStoreCreditFromResultSet(rs);
		} catch (DataException de) {
			if (de.getErrorCode() == DataException.NO_DATA) {
				logger.info("No store credits are found which needs to be validated.");
			} else {
				logger.warn(de.toString());
			}
			throw de;
		} catch (SQLException se) {
			throw new DataException(DataException.SQL_ERROR, "lookUpUnVerifiedStoreCredit", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "lookUpUnVerifiedStoreCredit", e);
		}
		return entries;
	}

	// ----------------------------------------------------------------------
	/**
	 * MFL Customizations Parse the result set
	 * <P>
	 * 
	 * @param rs
	 * 
	 *            Added by Shavinki Goyal
	 **/
	// ----------------------------------------------------------------------
	protected MAXCentralizedDataEntryIfc[] parseSelectedStoreCreditFromResultSet(ResultSet rs)
			throws SQLException, DataException {
		ArrayList entryList = new ArrayList();
		Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
		MAXCentralizedDataEntryIfc entry = null;
		int index = 0;

		while (rs.next()) {
			index = 0;
			// instantiate entry and set values
			MAXDomainObjectFactoryIfc domainFactory = (MAXDomainObjectFactoryIfc) DomainGateway.getFactory();
			entry = domainFactory.getCentralizedDataEntryInstance(locale);
			entry.setStoreId(rs.getString(++index));
			entry.setWorkstationId(rs.getString(++index));
			entry.setBusinessDate(getEYSDateFromString(rs, ++index));
			entry.setTransactionID(String.valueOf(rs.getInt(++index)));
			entry.setStrCrdtNumber(rs.getString(++index));
			entry.setStrCrdtAmt(rs.getString(++index));

			// add entry to list
			entryList.add(entry);
		}

		// if no entries, throw exception
		if (entryList.size() == 0) {
			throw new DataException(DataException.NO_DATA, "No transactions found matching search criteria.");
		}
		// copy list into array
		MAXCentralizedDataEntryIfc[] entries = new MAXCentralizedDataEntryIfc[entryList.size()];
		entryList.toArray(entries);

		return (entries);
	}

	// ----------------------------------------------------------------------
	/**
	 * MFL Customizations Updates store credit validation flag.
	 * <P>
	 * 
	 * @param dataConnection
	 * @param strCrdt
	 * 
	 *            Added by Shavinki Goyal
	 **/
	// ----------------------------------------------------------------------
	protected void updateStrCrdtValidationFlag(JdbcDataConnection dataConnection, MAXCentralizedDataEntryIfc strCrdt)
			throws DataException {

		SQLUpdateStatement strCrdtSQL = new SQLUpdateStatement();
		strCrdtSQL.setTable(TABLE_STORE_CREDIT_TENDER_LINE_ITEM);
		strCrdtSQL.addColumn(FIELD_STORE_CREDIT_VALIDATION_FLAG, '1');
		strCrdtSQL.addQualifier(FIELD_RETAIL_STORE_ID, strCrdt.getStoreId());
		strCrdtSQL.addQualifier(FIELD_WORKSTATION_ID, strCrdt.getWorkstationId());
		strCrdtSQL.addQualifier(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(strCrdt.getBusinessDate()));
		strCrdtSQL.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, strCrdt.getTransactionID());
		strCrdtSQL.addQualifier(FIELD_STORE_CREDIT_ID, getDBStringValue(strCrdt.getStrCrdtNumber()));
		strCrdtSQL.addQualifier(FIELD_STORE_CREDIT_BALANCE, strCrdt.getStrCrdtAmt());

		try {
			dataConnection.execute(strCrdtSQL.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			logger.error("" + e + "");
			throw new DataException(DataException.UNKNOWN, "update store credit validation flag", e);
		}
	}

	public String getDBStringValue(String value) {
		return (makeSafeString(value));
	}

	// ---------------------------------------------------------------------
	/**
	 * Set the name.
	 * <P>
	 * 
	 * @param name
	 *            The name to assign the operation.
	 **/
	// ---------------------------------------------------------------------
	public void setName(String name) {
		this.name = name;
	}

	// ---------------------------------------------------------------------
	/**
	 * Return the name of the operation.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public String getName() {
		return name;
	}
	// ---------------------------------------------------------------------

	public static EYSDate getEYSDateFromString(ResultSet rs, int index) throws SQLException, DataException {
		_360DateIfc date = get_360DateFromString(rs, index);
		if (date == null) {
			return null;
		}
		return new EYSDate(date.dateValue());
	}

	public String getBusinessDayString(EYSDate date) {
		return (dateToSQLDateString(date));
	}

	protected String addAbsFunction(String str) {
		return "abs(" + str + ")";
	}

}
