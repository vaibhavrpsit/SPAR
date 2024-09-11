/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  11/April/2013               Izhar                                       MAX-POS-Customer-FES_v1.2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.arts;

// java imports
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import org.apache.log4j.Logger;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.arts.JdbcReadCustomerbyEmployee;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.utility.Util;

//-------------------------------------------------------------------------
/**
 * This operation takes a POS domain Customer employee ID and and retrieves the
 * cutomer from the database.
 * <P>
 * 
 * @version $Revision: 6$
 **/
// -------------------------------------------------------------------------
public class MAXJdbcReadCustomerbyEmployee extends JdbcReadCustomerbyEmployee {
	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcReadCustomerbyEmployee.class);

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public MAXJdbcReadCustomerbyEmployee() {
		super();
		setName("JdbcReadCustomerbyEmployee");
	}

	// ---------------------------------------------------------------------
	/**
	 * Read customer preferred locale.
	 * <P>
	 * 
	 * @param dataConnection
	 *            The data connection on which to execute.
	 * @param customer
	 *            The output Customer with retrieved data
	 * @exception DataException
	 *                thrown when an error occurs executing the against the
	 *                DataConnection
	 * @exception SQLException
	 *                thrown when an error occurs with the ResultSet
	 **/
	// ---------------------------------------------------------------------
	public void readCustomerLocale(DataConnectionIfc dataConnection, CustomerIfc customer)
			throws DataException, SQLException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadCustomerbyEmployee.readCustomerLocale");

		ResultSet rs = null;
		Locale customerLocale = customer.getPreferredLocale();

		SQLSelectStatement sql = new SQLSelectStatement();

		// build sql query
		sql.addTable(TABLE_CUSTOMER, ALIAS_CUSTOMER);
		sql.addColumn(FIELD_LOCALE);
		// MAX Rev 1.0 Change : Start
		sql.addColumn("TY_CT");
		// MAX Rev 1.0 Change : end
		sql.addQualifier(FIELD_CUSTOMER_ID + " = '" + customer.getCustomerID() + "'");

		dataConnection.execute(sql.getSQLString());
		rs = (ResultSet) dataConnection.getResult();

		// read locale from database result set
		if (rs.next()) {
			String localeString = getSafeString(rs, 1);
			// MAX Rev 1.0 Change : Start
			String typeString = getSafeString(rs, 2);
			((MAXCustomerIfc) customer).setCustomerType(typeString);
			// MAX Rev 1.0 Change : end
			if (!Util.isEmpty(localeString)) {
				// convert string representation of the locale into a
				// locale object
				customerLocale = LocaleUtilities.getLocaleFromString(localeString);
				customer.setPreferredLocale(customerLocale);
			}
		}
		rs.close();

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadCustomerbyEmployee.readCustomerLocale");
	}
	// end initialize()
} // end JdbcReadCustomerbyEmployee
