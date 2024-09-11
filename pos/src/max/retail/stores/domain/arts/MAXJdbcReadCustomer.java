/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*	
*	Rev 1.3		Feb 03,2017			Hitesh Dua			Changes for Customer Search Query
*	Rev 1.2		Dec 28,2016			Ashish Yadav		Chnages for Onlinepoints Redemption
*	Rev 1.1     Oct 19, 2016		Mansi Goel			Changes for Customer FES
*	Rev 1.0     Oct 17, 2016		Nitesh Khadaria		Code Merge
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import java.math.BigDecimal;
// java imports
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerConstantsIfc;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.ResultList;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.ARTSCustomer;
import oracle.retail.stores.domain.arts.JdbcReadCustomer;
import oracle.retail.stores.domain.customer.CustomerConstantsIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;

import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * This operation takes a POS domain Customer and creates a new entry in the
 * database.
 * <P>
 * 
 * @version $Revision: 6$
 **/
// -------------------------------------------------------------------------

public class MAXJdbcReadCustomer extends JdbcReadCustomer implements MAXARTSDatabaseIfc {
	/**
	 * 
	 */
	private static final long serialVersionUID = -49645098936049873L;
	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcReadCustomer.class);

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public MAXJdbcReadCustomer() {
		super();
		setName("JdbcReadCustomer");
	}

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		//Changes for Rev 1.1 : Starts
		// Changes starts for Rev 1.2 (Ashish : Online Points Redemption)
		CustomerSearchCriteriaIfc criteria = (CustomerSearchCriteriaIfc)action.getDataObject();
		// Changes ends for Rev 1.2 (Ashish : Online Points Redemption)
        CustomerIfc posCustomer = configureCustomer(criteria);
        LocaleRequestor localeRequestor = criteria.getLocaleRequestor();
        //Changes for Rev 1.1 : Ends
		try {
			// Changes starts for Rev 1.2 (Ashish : Online Points Redemption)
			ARTSCustomer workingARTSCustomer = selectCustomer(dataConnection, posCustomer);
			// Changes ends for Rev 1.2 (Ashish : Online Points Redemption)

			if (workingARTSCustomer.getPosCustomer() == null) {
				throw new DataException(DataException.NO_DATA,
						"No customer was found proccessing the result set in JdbcReadCustomer.");
			} else {
				// We're going to issue three statements: first to retrieve the
				// contact information, second to retrieve the addresses, and
				// third
				// to retrieve the phone numbers.
				selectContactInfo(dataConnection, workingARTSCustomer);

				// set Addresses to reflect input search parameters
				workingARTSCustomer.getPosCustomer().setAddressList(posCustomer.getAddressList());
				selectAddressInfo(dataConnection, workingARTSCustomer);
				selectEmailInfo(dataConnection, workingARTSCustomer);
				selectPhoneInfo(dataConnection, workingARTSCustomer);
				selectGroupInfo(dataConnection, workingARTSCustomer);
				selectBusinessInfo(dataConnection, workingARTSCustomer, localeRequestor);
				readCustomerLocale(dataConnection, workingARTSCustomer);
				//Changes for Rev 1.1 : Starts
				ArrayList<CustomerIfc> list = new ArrayList<CustomerIfc>();
				list.add(workingARTSCustomer.getPosCustomer());
				ResultList resultList = new ResultList(list, 1);
				dataTransaction.setResult(resultList);
				//Changes for Rev 1.1 : Ends
			}

		} catch (SQLException e) {
			((JdbcDataConnection) dataConnection).logSQLException(e, "Processing result set.");
			throw new DataException(DataException.SQL_ERROR,
					"An SQL Error occurred proccessing the result set from selecting a customer in JdbcReadCustomer.",
					e);
		}
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
	public void readCustomerLocale(DataConnectionIfc dataConnection, ARTSCustomer customer)
			throws DataException, SQLException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadCustomer.readCustomerLocale");

		ResultSet rs = null;
		Locale customerLocale = customer.getPosCustomer().getPreferredLocale();
		SQLSelectStatement sql = new SQLSelectStatement();

		// build sql query
		sql.addTable(TABLE_CUSTOMER, ALIAS_CUSTOMER);
		sql.addColumn(FIELD_LOCALE);	
		//	changes for rev 1.3 start
		sql.addColumn(FIELD_CUSTOMER_TYPE);		
		//	changes for rev 1.3 end
		sql.addQualifier(FIELD_CUSTOMER_ID + " = '" + customer.getPosCustomer().getCustomerID() + "'");

		dataConnection.execute(sql.getSQLString());
		rs = (ResultSet) dataConnection.getResult();

		// read locale from database result set
		if (rs.next()) {
			String localeString = getSafeString(rs, 1);
			String typeString = getSafeString(rs, 2);
			((MAXCustomerIfc) customer.getPosCustomer()).setCustomerType(typeString);
			if (!Util.isEmpty(localeString)) {
				// convert string representation of the locale into a
				// locale object
				customerLocale = LocaleUtilities.getLocaleFromString(localeString);
				customer.getPosCustomer().setPreferredLocale(customerLocale);
			}
		}
		rs.close();

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadCustomer.readCustomerLocale");
	}
	// Changes starts for Rev 1.2 (Ashish : Online Points Redemption)
	public ARTSCustomer selectCustomer(DataConnectionIfc dataConnection, CustomerIfc customerSearchCriteria)
			throws DataException, SQLException {
		// Changes ends for Rev 1.2 (Ashish : Online Points Redemption)
		ARTSCustomer artsCustomer = new ARTSCustomer();
		CustomerIfc customer = null;

		String customerID = customerSearchCriteria.getCustomerIDPrefix() + customerSearchCriteria.getCustomerID();
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.addTable(TABLE_CUSTOMER);

		sql.addColumn(FIELD_PARTY_ID);
		sql.addColumn(FIELD_EMPLOYEE_ID);
		sql.addColumn(FIELD_CUSTOMER_STATUS);
		sql.addColumn(FIELD_HOUSE_ACCOUNT_NUM);

		sql.addColumn(FIELD_LOYALTY_POINT_BALANCE);
		sql.addColumn(FIELD_NEXT_MONTH_EXP_PNT);
		sql.addColumn(FIELD_POINT_LAST_UPDATED);
		//Changes for Rev 1.1 : Starts
		//changes for rev 1.3 start
		sql.addColumn(FIELD_CUSTOMER_TYPE);
		//changes for rev 1.3 end
		//Changes for Rev 1.1 : Ends
		sql.addColumn(FIELD_CUSTOMER_TIER);

		sql.addQualifier(FIELD_CUSTOMER_ID, makeSafeString(customerID));
		sql.addNotQualifier(FIELD_CUSTOMER_STATUS, CustomerConstantsIfc.CUSTOMER_STATUS_DELETED);
		dataConnection.execute(sql.getSQLString());

		ResultSet resultSet = (ResultSet) dataConnection.getResult();
		if (resultSet.next()) {
			int index = 0;
			int partyID = Integer.parseInt(getSafeString(resultSet, ++index));
			customer = DomainGateway.getFactory().getCustomerInstance();
			customer.setCustomerID(customerID);
			customer.setRecordID(String.valueOf(partyID));
			customer.setEmployeeID(getSafeString(resultSet, ++index));
			customer.setStatus(Integer.parseInt(getSafeString(resultSet, ++index)));
			((MAXCustomer) customer).setHouseAccountNumber(resultSet.getString(++index));

			// Changes for Loyalty Points
			if (resultSet.getString(5) != null)
				((MAXCustomer) customer).setBalancePoint(new BigDecimal(resultSet.getString(5)));
			if (resultSet.getString(6) != null)
				((MAXCustomer) customer).setPointsExpiringNextMonth(new BigDecimal(resultSet.getString(6)));
			if (resultSet.getString(7) != null)
				((MAXCustomer) customer).setBalancePointLastUpdationDate(
						new EYSDate(resultSet.getString(7), 1, LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE)));
			if (resultSet.getString(8) != null) {
				String customerType = resultSet.getString(8);
				((MAXCustomer) customer).setCustomerType(MAXCustomerConstantsIfc.CRM);
				if (customerType.equalsIgnoreCase(MAXCustomerConstantsIfc.CRM))
					((MAXCustomer) customer).setLoyaltyCardNumber(customer.getCustomerID());
			}
			if (resultSet.getString(9) != null)
				((MAXCustomer) customer).setCustomerTier(resultSet.getString(9));
			// dataFetched for loyatly Points customer
			artsCustomer.setPartyId(partyID);
			artsCustomer.setPosCustomer(customer);
			resultSet.close();
		} else {
			artsCustomer = selectCustomer(dataConnection, customerSearchCriteria.getCustomerID());
		}

		return artsCustomer;
	}

	// ---------------------------------------------------------------------
	/**
	 * Attempt to resolve the party id from the customer table.
	 * <P>
	 * 
	 * @param dataConnection
	 *            The data connection on which to execute.
	 * @return an integer denoting the party_id
	 * @exception DataException
	 *                thrown when an error occurs executing the against the
	 *                DataConnection
	 * @exception SQLException
	 *                thrown when an error occurs with the ResultSet
	 **/
	// ---------------------------------------------------------------------
	public ARTSCustomer selectCustomer(DataConnectionIfc dataConnection, String customerID)
			throws DataException, SQLException {
		ARTSCustomer artsCustomer = new ARTSCustomer();
		CustomerIfc customer = null;

		// Select the party id, employee id, and status code from the
		// customer table
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.addTable(TABLE_CUSTOMER);
		sql.addColumn(FIELD_PARTY_ID);
		sql.addColumn(FIELD_TILL_PAYMENT_EMPLOYEE_ID);
		sql.addColumn(FIELD_CUSTOMER_STATUS);
		sql.addColumn(FIELD_HOUSE_ACCOUNT_NUM);
		sql.addColumn(FIELD_LOYALTY_POINT_BALANCE);
		sql.addColumn(FIELD_NEXT_MONTH_EXP_PNT);
		sql.addColumn(FIELD_POINT_LAST_UPDATED);
		//Changes for Rev 1.1 : Starts
		//changes for rev 1.3 start
		sql.addColumn(FIELD_CUSTOMER_TYPE);
		//changes for rev 1.3 end
		//Changes for Rev 1.1 : Ends
		sql.addColumn(FIELD_CUSTOMER_TIER);
		sql.addQualifier(FIELD_CUSTOMER_ID, makeSafeString(customerID));
		sql.addNotQualifier(FIELD_CUSTOMER_STATUS, CustomerConstantsIfc.CUSTOMER_STATUS_DELETED);

		dataConnection.execute(sql.getSQLString());

		ResultSet resultSet = (ResultSet) dataConnection.getResult();
		if (resultSet.next()) {
			int partyID = Integer.parseInt(getSafeString(resultSet, 1));

			if (partyID != 0) {
				customer = DomainGateway.getFactory().getCustomerInstance();
				customer.setCustomerID(customerID);
				customer.setRecordID(String.valueOf(partyID));
				customer.setEmployeeID(getSafeString(resultSet, 2));
				customer.setStatus(Integer.parseInt(getSafeString(resultSet, 3)));
				((MAXCustomer) customer).setHouseAccountNumber(resultSet.getString(4));

				// Changes for Loyalty Points
				if (resultSet.getString(5) != null)
					((MAXCustomer) customer).setBalancePoint(new BigDecimal(resultSet.getString(5)));
				if (resultSet.getString(6) != null)
					((MAXCustomer) customer).setPointsExpiringNextMonth(new BigDecimal(resultSet.getString(6)));
				if (resultSet.getString(7) != null) {
					EYSDate dd = DomainGateway.getFactory().getEYSDateInstance();
					dd.initialize(resultSet.getString(7).substring(0, 10), "yyyy-MM-dd");
					((MAXCustomer) customer).setBalancePointLastUpdationDate((EYSDate) dd.clone());
				}
				// ((MAXCustomer)customer).setBalancePointLastUpdationDate(new
				// EYSDate(resultSet.getString(7),"dd/MM/yyyy");
				if (resultSet.getString(8) != null)
					((MAXCustomer) customer).setCustomerType(resultSet.getString(8));
				if (resultSet.getString(9) != null)
					((MAXCustomer) customer).setCustomerTier(resultSet.getString(9));
				// dataFetched for loyatly Points customer

				artsCustomer.setPartyId(partyID);
				artsCustomer.setPosCustomer(customer);
			}
		}
		resultSet.close();
		return artsCustomer;
	}
	// Changes start for Rev 1.2 (Ashish : Online points redemtion)
	protected CustomerIfc configureCustomer (CustomerSearchCriteriaIfc criteria)
    {
         CustomerIfc customer = DomainGateway.getFactory().getCustomerInstance();
         boolean isRegularCustomer = true;
         switch (criteria.getSearchType())
         {
             case SEARCH_BY_TAX_ID:
                 try
                 {
                     EncipheredDataIfc taxData = FoundationObjectFactory.getFactory().createEncipheredDataInstance(criteria.getTaxID().getBytes());
                     customer.setEncipheredTaxID(taxData);
                 }
                 catch(EncryptionServiceException ese)
                 {
                     logger.warn("could not encrypt tax ID", ese);
                 }
                 break;
             case SEARCH_BY_CUSTOMER_INFO:
                 configureCustomerFromInformation(criteria, isRegularCustomer, customer);
                 break;
             case SEARCH_BY_EMPLOYEE_ID:
                 customer.setEmployeeID(criteria.getEmployeeID());
                 break;
             case SEARCH_BY_CUSTOMER_ID:
                 //TODO change this code once JPA is in place, to pass is the search criteria  
                 customer.setCustomerID(criteria.getCustomerID());          
                 break; 
             case SEARCH_BY_BUSINESS_INFO:
                 configureCustomerFromInformation(criteria, !isRegularCustomer, customer);
                 break;
              case UNDEFINED:
                 throw new IllegalArgumentException("Unknown search type");
              default:
                  break;
         }
         return customer;
     }
	protected void configureCustomerFromInformation(CustomerSearchCriteriaIfc criteria, boolean isRegularCustomer, CustomerIfc customer)
    {
        if (isRegularCustomer)
        {
            customer.setFirstName(criteria.getFirstName());
            customer.setLastName(criteria.getLastName());
        }
        else
        {
            customer.setCompanyName(criteria.getCompanyName());
        }
        
        AddressIfc address = DomainGateway.getFactory().getAddressInstance();
        address.addAddressLine(criteria.getAddressLine1());
        address.setCountry(criteria.getCountry());
        address.setPostalCode(criteria.getPostal());
        customer.addAddress(address);
        
        PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
        phone.setPhoneNumber(criteria.getPhoneNumber());
        customer.addPhone(phone);
        customer.setCustomerID(criteria.getCustomerID());
        
    }
	// Changes start for Rev 1.2 (Ashish : Online points redemtion)
}
