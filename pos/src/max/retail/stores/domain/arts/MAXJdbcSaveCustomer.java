/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*	
*	Rev 1.2		Feb 03,2017			Hitesh Dua			Changes for Customer Save Query
*	Rev 1.1     Oct 19, 2016		Mansi Goel			Changes for Customer FES
*	Rev 1.0     Oct 17, 2016		Nitesh Khadaria		Code Merge
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import java.util.Vector;

import max.retail.stores.domain.customer.MAXCustomerConstantsIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.arts.ARTSCustomer;
import oracle.retail.stores.domain.arts.JdbcSaveCustomer;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

public class MAXJdbcSaveCustomer extends JdbcSaveCustomer implements MAXARTSDatabaseIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1893179848687227176L;
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcSaveCustomer.class);

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcSaveCustomer.execute");

		/*
		 * getUpdateCount() is about the only thing outside of DataConnectionIfc
		 * that we need.
		 */
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
		ARTSCustomer artsCustomer = (ARTSCustomer) action.getDataObject();

		saveCustomer(connection, artsCustomer);
		dataTransaction.setResult(artsCustomer.getPosCustomer());
		if (logger.isDebugEnabled())
			logger.debug("JdbcSaveCustomer.execute");
	}

	protected void insertCustomer(JdbcDataConnection dataConnection, ARTSCustomer artsCustomer) throws DataException {
		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_CUSTOMER);

		// Fields
		sql.addColumn(FIELD_CUSTOMER_ID, getCustomerID(artsCustomer));
		sql.addColumn(FIELD_PARTY_ID, getPartyID(artsCustomer));
		sql.addColumn(FIELD_CUSTOMER_NAME, getCustomerName(artsCustomer));
		sql.addColumn(FIELD_CUSTOMER_STATUS, getCustomerStatus(artsCustomer));
		sql.addColumn(FIELD_EMPLOYEE_ID, getEmployeeID(artsCustomer));
		sql.addColumn(FIELD_HOUSE_ACCOUNT_NUM, getHouseAccountNumber(artsCustomer));
		sql.addColumn(FIELD_LOYALTY_POINT_BALANCE, null);
		sql.addColumn(FIELD_NEXT_MONTH_EXP_PNT, null);
		sql.addColumn(FIELD_POINT_LAST_UPDATED, null);
		//Changes for Rev 1.1 : Starts
		//Changes for Rev 1.2 Start
		sql.addColumn(FIELD_CUSTOMER_TYPE, "'" + MAXCustomerConstantsIfc.LOCAL + "'");
		//Changes for Rev 1.2 end
		//Changes for Rev 1.1 : Ends
		sql.addColumn(FIELD_CUSTOMER_TIER, null);

		if (getCustomerLocale(artsCustomer) != null) {
			sql.addColumn(FIELD_LOCALE, getCustomerLocale(artsCustomer));
		}
		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			logger.error("" + e + "");
			throw new DataException(DataException.UNKNOWN, "insertCustomer", e);
		}
	}

	public String getCustomerName(ARTSCustomer customer) {
		// CustomerIfc should extend or contain PersonNameIfc
		boolean handleQuoteOnly = true;
		return makeSafeString(customer.getPosCustomer().getCustomerName(), handleQuoteOnly);
	}

	public static String makeSafeString(String value, boolean handleQuoteOnly) {
		// done by prateek
		return "'" + value + "'";
	}

	public String getPostalCode(AddressIfc address) {
		String postalCodeExtension = address.getPostalCodeExtension();
		StringBuffer postalCodeString = new StringBuffer(address.getPostalCode());
		boolean handleQuoteOnly = true;
		if (postalCodeExtension != null && postalCodeExtension.length() > 0) {
			postalCodeString.append("-" + postalCodeExtension);
		}

		return (makeSafeString(postalCodeString.toString(), handleQuoteOnly));
	}

	public String getCity(AddressIfc address) {
		boolean handleQuoteOnly = true;
		return (makeSafeString(address.getCity(), handleQuoteOnly));
	}

	public String getAddressLine3(AddressIfc address) {
		String value = null;
		Vector<String> addressLines = address.getLines();
		boolean handleQuoteOnly = true;
		if (addressLines != null && addressLines.size() > 2) {
			value = ((String) addressLines.elementAt(2)).replace("'", "''");
		}
		return (makeSafeString(value, handleQuoteOnly));
	}

	public String getAddressLine2(AddressIfc address) {
		String value = null;
		Vector<String> addressLines = address.getLines();
		boolean handleQuoteOnly = true;
		if (addressLines != null && addressLines.size() > 1) {
			value = ((String) addressLines.elementAt(1)).replace("'", "''");
		}
		return (makeSafeString(value, handleQuoteOnly));
	}

	public String getAddressLine1(AddressIfc address) {
		String value = null;
		Vector<String> addressLines = address.getLines();
		boolean handleQuoteOnly = true;
		if (addressLines != null && addressLines.size() > 0) {
			value = ((String) addressLines.elementAt(0)).replace("'", "''");
		}
		return (makeSafeString(value, handleQuoteOnly));
	}

	public String getMiddleName(ARTSCustomer customer) {
		boolean handleQuoteOnly = true;
		return (makeSafeString(customer.getPosCustomer().getMiddleName(), handleQuoteOnly));
	}

	public String getFirstName(ARTSCustomer customer) {
		boolean handleQuoteOnly = true;
		return (makeSafeString(customer.getPosCustomer().getFirstName(), handleQuoteOnly));
	}

	public String getLastName(ARTSCustomer customer) {
		boolean handleQuoteOnly = true;
		return (makeSafeString(customer.getPosCustomer().getLastName(), handleQuoteOnly));
	}
}
