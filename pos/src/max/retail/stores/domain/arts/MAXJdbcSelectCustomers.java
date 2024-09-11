/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*	
*	Rev 1.2		Feb 03,2017			Hitesh Dua			Changes for Customer Search Query
*	Rev 1.1     Oct 19, 2016		Mansi Goel			Changes for Customer FES
*	Rev 1.0     Oct 17, 2016		Nitesh Khadaria		Code Merge
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import max.retail.stores.domain.customer.MAXCaptureCustomer;
import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import oracle.retail.stores.common.utility.ResultList;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.JdbcSelectCustomers;
import oracle.retail.stores.domain.customer.CustomerConstantsIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.CustomerSort;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * This operation selects Customer data from the database based on name,
 * address, and phone number.
 * <P>
 * 
 * @version $Revision: 5$
 **/
// -------------------------------------------------------------------------

public class MAXJdbcSelectCustomers extends JdbcSelectCustomers {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5974997927817196914L;
	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcSelectCustomers.class);

	// ---------------------------------------------------------------------
	/**
	 * Execute the SQL statements against the database.
	 * <P>
	 * 
	 * @param dataTransaction
	 * @param dataConnection
	 * @param action
	 * @exception DataException
	 **/
	// ---------------------------------------------------------------------
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		
		// get the input Customer object that contains search criteria
        CustomerSearchCriteriaIfc criteria = (CustomerSearchCriteriaIfc)action.getDataObject();
        CustomerIfc posCustomer = configureCustomer(criteria);
		// Remove elements from previous searches.
		ArrayList<CustomerIfc> customers = new ArrayList<CustomerIfc>();
		logger.info("Start time to fetch customer from local database ::");
		// attempt the database access
		try {
			// build an SQL query String
			String sqlString = buildQuery(posCustomer, customers);

			// execute the SQL query
			dataConnection.execute(sqlString);

			// get the result set
			ResultSet rs = (ResultSet) dataConnection.getResult();

			// loop through the result set, building the returned set of
			// Customers
			while (rs.next()) {
				// create a new Customer object
				CustomerIfc customer = DomainGateway.getFactory().getCustomerInstance();

				// set the Customer ID and Record ID (party ID) that met the
				// search criteria
				customer.setCustomerID(getSafeString(rs, 1));
				customer.setRecordID(getSafeString(rs, 2));
				customer.setEmployeeID(getSafeString(rs, 3));
				customer.setStatus(rs.getInt(4));
				((MAXCustomerIfc) customer).setCustomerType(getSafeString(rs, 5));
				//Changes for Rev 1.1 : Starts
				if (rs.getString(6) != null) {
					EYSDate dd = DomainGateway.getFactory().getEYSDateInstance();
					dd.initialize(rs.getString(6).substring(0, 10), "yyyy-MM-dd");
					((MAXCustomer) customer).setBalancePointLastUpdationDate((EYSDate) dd.clone());
				}
				if (rs.getString(7) != null) {
					((MAXCustomer) customer).setBalancePoint(new BigDecimal(rs.getString(7)));
				}
				if (rs.getString(8) != null) {
					((MAXCustomer) customer).setCustomerTier(getSafeString(rs, 8));
				}
				if (rs.getString(9) != null) {
					((MAXCustomer) customer).setPointsExpiringNextMonth(new BigDecimal(rs.getString(9)));
				}
				//Changes for Rev 1.1 : Ends
				// add the Customer object to the returned set
				customers.add(customer);
			}

			if (customers.size() == 0) {
				throw new DataException(DataException.NO_DATA,
						"No customers were found proccessing the result set in JdbcSelectCustomers.");
			}

			// Get contact, address, and phone info for the Customers
			for (CustomerIfc customer : customers) {
				// We're going to issue three statements: first to retrieve the
				// contact information, second to retrieve the addresses, and
				// third
				// to retrieve the phone numbers.
				int partyID = Integer.parseInt(customer.getRecordID());
				selectContactInfo(dataConnection, customer, partyID);
				
				//set customer preferred locale
                selectCustomerLocale(dataConnection, customer, partyID);

                // set Addresses to reflect input search parameters
                customer.setAddressList(posCustomer.getAddressList());
				selectAddressInfo(dataConnection, customer, partyID);
				// set email address
				selectEmailInfo(dataConnection, customer, partyID);

				selectPhoneInfo(dataConnection, customer, partyID);
				selectGroupInfo(dataConnection, customer);
			} // End get additional info

			// sort the returned set of Customers
			CustomerSort.sort(customers);

			ResultList resultList = new ResultList(customers, 1);

			dataTransaction.setResult(resultList);
			//Code merging changes start : Patch 15_MMRPSelection

			 logger.info("End time to fetch customer from local database ::");
				//Code merging changes Ends :  Patch 15_MMRPSelection

		} catch (SQLException e) {
			((JdbcDataConnection) dataConnection).logSQLException(e, "Processing result set.");
			throw new DataException(DataException.SQL_ERROR,
					"An SQL Error occurred proccessing the result set from selecting customers in JdbcSelectCustomers.",
					e);
		}

	}

	//Changes for Rev 1.1 : Starts
	protected String buildQuery(CustomerIfc posCustomer, ArrayList<CustomerIfc> customers) {

		AddressIfc addr;
		String firstName;
		String lastName;
		String line1 = "";
		String postalCode;
		String postalCodeExt;
		Vector<String> lines;
	    List<AddressIfc> addressList;
	    
		if (logger.isDebugEnabled())
			logger.debug("JdbcSelectCustomers.buildQuery()");
		
		if (((MAXCustomerIfc) posCustomer).getCustomerType() == null)
			((MAXCustomerIfc) posCustomer).setCustomerType("L");
		else if (!((MAXCustomerIfc) posCustomer).getCustomerType().equalsIgnoreCase("T"))
			((MAXCustomerIfc) posCustomer).setCustomerType("L");
		// set the constant part of the SQL query
		//changes for rev 1.2 start
		String sqlString = "\n" + "select distinct cust.id_ct, cust.id_prty, cust.id_em, cust.sts_ct, cust.ty_ct,cust.DT_UPD_LST_PNT_BLNC,cust.PNT_BLNC,cust.TR_CT,cust.MNTH_NXT_EXP_PNT\n"
				+ "from pa_ct cust, pa_cnct cnct, lo_ads addr, pa_phn phone \n" + "where \n"
				+ "cust.id_prty = cnct.id_prty and \n" + "cust.id_prty = addr.id_prty and \n" +  "cust.sts_ct <> " + CustomerConstantsIfc.CUSTOMER_STATUS_DELETED;
		
		if (!(posCustomer instanceof MAXCaptureCustomer)) {
			sqlString = sqlString + " and " + "\n " + "(cust.ty_ct = '"
					+ ((MAXCustomerIfc) posCustomer).getCustomerType() + "'" + "\n";
			sqlString = sqlString + " or " + "\n" + "cust.ty_ct = '" + "T" + "')" + "\n";
		}
		//changes for rev 1.2 end
		// if a first name is given, include it in the lookup
		firstName = posCustomer.getFirstName();
		if (firstName != null && firstName.length() > 0) { // Begin add first
															// name to query

			sqlString = sqlString
					.concat("and UPPER(cnct.fn_cnct) like " + "UPPER(" + makeSafeString(firstName + "%") + ")" + " \n");

		} // End add first name to query

		// if a last name is given, include it in the lookup
		lastName = posCustomer.getLastName();
		if (lastName != null && lastName.length() > 0) { // Begin add last name
															// to query

			sqlString = sqlString
					.concat("and UPPER(cnct.ln_cnct) like " + "UPPER(" + makeSafeString(lastName + "%") + ")" + " \n");

		} // End add last name to query

		// if any address line parameters are given, include them in the lookup
		addressList = posCustomer.getAddressList();

		// if there is at least one Address object in the Vector
		if (addressList.size() >= 1) { // Begin get Address object

			addr = (AddressIfc) addressList.get(0);

			lines = addr.getLines(); // get Vector of address lines

			// if there is at least one address line in the Vector
			if (lines.size() >= 1) { // Begin get address lines

				line1 = (String) lines.elementAt(0);
			} // End get address lines

			// get the other search parameters from the Address object
			postalCode = addr.getPostalCode();
			postalCodeExt = addr.getPostalCodeExtension();

			// append the postal code extension to the postal code
			if (postalCodeExt != null && postalCodeExt.length() > 0)
				postalCode = postalCode.concat("-" + postalCodeExt);

			// if there was a search parameter for Address Line 1
			if (line1 != null && line1.length() > 0) { // Begin add parameter to
														// SQL statement

				sqlString = sqlString.concat("and UPPER(a1_cnct) like " + "UPPER(" + "'" + line1 + "%')" + " \n");

			} // End add parameter to SQL statement

			// if there was a search parameter for postal code
			if (postalCode != null && postalCode.length() > 0) { // Begin add
																	// parameter
																	// to SQL
																	// statement

				sqlString = sqlString.concat("and UPPER(pc_cnct) like " + "UPPER(" + "'" + postalCode + "%')" + " \n");

			} // End add parameter to SQL statement

		} // End get Address object

		PhoneIfc phone = null;
		List<PhoneIfc> phoneList = posCustomer.getPhoneList();

		// if there was a phone number entered
		if (phoneList != null && phoneList.size() > 0) {
			phone = phoneList.get(0);
			if (phone != null) {
				String pn = phone.getPhoneNumber();
				//Changes for Rev 1.1 : Starts
				/*if(!pn.startsWith("91")){
					pn="91".concat(pn);
				}*/
				//Changes for Rev 1.1 : Ends
				sqlString = sqlString.concat(" and\n" + "cust.id_prty = phone.id_prty\n" +	" ");
				if (pn != null) {
				//	sqlString = sqlString.concat(" and\n" + "UPPER(phone.tl_cnct) like " + "UPPER('" + pn + "%') ");
					 sqlString = sqlString.concat(" and\nphone.tl_cnct like '" + pn + "%'");
				}
			}
		}

		sqlString = sqlString.concat("\n");
		if (logger.isInfoEnabled())
			logger.info("SQL is " + sqlString + "");

		if (logger.isDebugEnabled())
			logger.debug("JdbcSelectCustomers.buildQuery()");

		return sqlString;
	}
	//Changes for Rev 1.1 : Ends
}
