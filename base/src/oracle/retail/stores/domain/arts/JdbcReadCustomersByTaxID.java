/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadCustomersByTaxID.java /main/16 2013/12/20 10:27:42 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  12/11/13 - fix null dereferences
 *    abondala  12/14/12 - enhancements to the customer search
 *    abondala  12/13/12 - customer search criteria fields are all optional.
 *    acadar    08/05/12 - refactoring
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    asinton   03/08/12 - Changed query to use masked tax ID instead of the
 *                         hashed value. Also, refactored to use
 *                         SQLSelectStatement instead of building the sql
 *                         string with hardcoded table/column names.
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    cgreene   08/22/11 - removed deprecated methods
 *    masahu    07/07/11 - FORTIFY FIX: The sensitive SQLs get logged
 *    rrkohli   07/01/11 - Encryption CR
 *    rrkohli   07/01/11 - Encryption CR
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    cgreene   04/14/09 - convert pricingGroupID to integer instead of string
 *    mahising  12/04/08 - JUnit fix and SQL fix
 *    mahising  11/19/08 - Updated for review comments
 *    mahising  11/19/08 - Added for Customer Module
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.ResultList;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerConstantsIfc;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.CustomerSort;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This operation takes a POS domain Customer Tax ID and and retrieves the
 * customer from the database.
 */
public class JdbcReadCustomersByTaxID extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -6081143183029227109L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadCustomersByTaxID.class);

    /**
     * Class constructor.
     */
    public JdbcReadCustomersByTaxID()
    {
        setName("JdbcReadCustomersByTaxID");
    }

    /**
     * Execute the SQL statements against the database.
     *
     * @param dataTransaction
     * @param dataConnection
     * @param action
     * @exception DataException
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        // get the input Customer object that contains search criteria
        CustomerSearchCriteriaIfc criteria = (CustomerSearchCriteriaIfc)action.getDataObject();
        CustomerIfc posCustomer = configureCustomer(criteria);
        LocaleRequestor locale = posCustomer.getLocaleRequestor();

        // Remove elements from previous searches.
        ArrayList<CustomerIfc> customers = new ArrayList<CustomerIfc>();

        // attempt the database access
        try
        {
            // build an SQL query String
            SQLSelectStatement sql = buildQuery(posCustomer);

            // execute the SQL query
            dataConnection.execute(sql.getSQLString(), false);

            // get the result set
            ResultSet rs = (ResultSet)dataConnection.getResult();

            // loop through the result set, building the returned set of
            // Customers
            while (rs.next())
            {
                // create a new Customer object
                CustomerIfc customer = DomainGateway.getFactory().getCustomerInstance();
                // set the Customer ID and Record ID (party ID) that met the
                // search criteria
                customer.setRecordID(getSafeString(rs, 1));
                customer.setCustomerID(getSafeString(rs, 2));
                customer.setStatus(rs.getInt(3));
                // adding two more setter method for setting tax id
                // and pricing group in customer
                String encryptedNumber = rs.getString(4);
                String maskedNumber = rs.getString(5);
                EncipheredDataIfc customerTaxID =
                        FoundationObjectFactory.getFactory().createEncipheredDataInstance(encryptedNumber, maskedNumber);
                customer.setEncipheredTaxID(customerTaxID);
                int pricingGroupId = rs.getInt(6);
                if (pricingGroupId > 0)
                {
                    customer.setPricingGroupID(pricingGroupId);
                }
                // set the employee id for each customer
                customer.setEmployeeID(rs.getString(7));
                // add the Customer object to the returned set
                customers.add(customer);
            }

            if (customers.size() == 0)
            {
                throw new DataException(DataException.NO_DATA,
                        "No customers were found proccessing the result set in JdbcReadCustomersbyTaxID.");
            }

            // Get contact, address, and phone info for the Customers
            for (CustomerIfc customer : customers)
            {
                // We're going to issue three statements: first to retrieve the
                // contact information, second to retrieve the addresses, and
                // third
                // to retrieve the phone numbers.
                int partyID = Integer.parseInt(customer.getRecordID());
                selectContactInfo(dataConnection, customer, partyID);

                // set Addresses to reflect input search parameters
                customer.setAddressList(posCustomer.getAddressList());
                selectAddressInfo(dataConnection, customer, partyID);
                // set email address
                selectEmailInfo(dataConnection, customer, partyID);
                selectPhoneInfo(dataConnection, customer, partyID);
                selectGroupInfo(dataConnection, customer, locale);
                selectBusinessInfo(dataConnection, customer, partyID);
                readCustomerLocale(dataConnection, customer);

            } // End get additional info

            // sort the returned set of Customers
            CustomerSort.sort(customers);
            
            ResultList resultList = new ResultList(customers, 1);

            dataTransaction.setResult(resultList);
        }
        catch (SQLException e)
        {
            ((JdbcDataConnection)dataConnection).logSQLException(e, "Processing result set.");
            throw new DataException(
                    DataException.SQL_ERROR,
                    "An SQL Error occurred proccessing the result set from selecting customers in JdbcReadCustomersbyTaxID.",
                    e);
        }
    }

    /**
     * Read from the contact table.
     *
     * @param the data connection on which to execute.
     * @param cusomer to hold the data
     * @param unique identifier for a party
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection
     * @exception SQLException thrown when an error occurs with the ResultSet
     */
    public void selectContactInfo(DataConnectionIfc dataConnection, CustomerIfc customer, int partyID)
            throws DataException, SQLException
    {
        String sqlString = ReadARTSCustomerSQL.selectContactInfoSQL(partyID);
        dataConnection.execute(sqlString, false);
        ResultSet rs = (ResultSet)dataConnection.getResult();
        ReadARTSCustomerSQL.readContactResultsForCustomer(rs, customer);
        rs.close();
    }

    /**
     * Select all addresses from the address table.
     *
     * @param the data connection on which to execute.
     * @param cusomer to hold the data
     * @param unique identifier for a party
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection
     * @exception SQLException thrown when an error occurs with the ResultSet
     */
    public void selectAddressInfo(DataConnectionIfc dataConnection, CustomerIfc customer, int partyID)
            throws DataException, SQLException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomersbyTaxID.selectAddressInfo");

        String sqlString = ReadARTSCustomerSQL.selectAddressInfoSQL(partyID, customer);
        dataConnection.execute(sqlString, false);
        ResultSet rs = (ResultSet)dataConnection.getResult();
        ReadARTSCustomerSQL.readAddressResultsForCustomer(rs, customer);
        rs.close();

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomersbyTaxID.selectAddressInfo");
    }

    /**
     * Select all email addresses from the email address table.
     *
     * @param the data connection on which to execute.
     * @param customer to hold the data
     * @param unique identifier for a party
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection
     * @exception SQLException thrown when an error occurs with the ResultSet
     */
    public void selectEmailInfo(DataConnectionIfc dataConnection, CustomerIfc customer, int partyID)
            throws DataException, SQLException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomersbyTaxID.selectEmailInfo");

        String sqlString = ReadARTSCustomerSQL.selectEmailInfoSQL(partyID);
        dataConnection.execute(sqlString, false);
        ResultSet rs = (ResultSet)dataConnection.getResult();
        ReadARTSCustomerSQL.readEmailResultsForCustomer(rs, customer);
        rs.close();

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomersbyTaxID.selectEmailInfo");
    }

    /**
     * Select all phone numbers from the phone table.
     *
     * @param the data connection on which to execute.
     * @param cusomer to hold the data
     * @param unique identifier for a party
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection
     * @exception SQLException thrown when an error occurs with the ResultSet
     */
    public void selectPhoneInfo(DataConnectionIfc dataConnection, CustomerIfc customer, int partyID)
            throws DataException, SQLException
    {
        // Need to figure out what to do with telephone numbers in  address table.
        String sqlString = ReadARTSCustomerSQL.selectPhoneInfoSQL(partyID);
        dataConnection.execute(sqlString, false);
        ResultSet rs = (ResultSet)dataConnection.getResult();
        ReadARTSCustomerSQL.readPhoneResultsForCustomer(rs, customer);
        rs.close();
    }

    /**
     * Select all the groups assigned to this customer.
     *
     * @param dataConnection The data connection on which to execute.
     * @param customer The output Customer with retrieved data
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection
     * @exception SQLException thrown when an error occurs with the ResultSet
     */
    public void selectGroupInfo(DataConnectionIfc dataConnection, CustomerIfc customer, LocaleRequestor locale)
            throws DataException, SQLException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomersbyTaxID.selectGroupInfo");
        // build SQL for groups
        SQLSelectStatement sql = ReadARTSCustomerSQL.buildCustomerGroupSQL(customer.getCustomerID(), customer
                .getLocaleRequestor());

        // execute and retrieve results
        dataConnection.execute(sql.getSQLString());
        ResultSet rs = (ResultSet)dataConnection.getResult();

        // parse result set
        CustomerGroupIfc[] groups = ReadARTSCustomerSQL.readCustomerGroupResultSet(rs);

        rs.close();

        if (groups != null)
        {
            customer.setCustomerGroups(groups);
            selectDiscountInfo(dataConnection, customer, locale);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomersbyTaxID.selectGroupInfo");
    }

    /**
     * Select all the discount rules available to this customer.
     *
     * @param dataConnection The data connection on which to execute.
     * @param customer The output Customer with retrieved data
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection
     * @exception SQLException thrown when an error occurs with the ResultSet
     */
    public void selectDiscountInfo(DataConnectionIfc dataConnection, CustomerIfc customer, LocaleRequestor locale)
            throws DataException, SQLException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomersbyTaxID.selectDiscountInfo");

        ResultSet rs = null;
        DiscountRuleIfc[] discounts = null;
        DiscountRuleIfc discount = null;
        SQLSelectStatement sql = null;
        // loop through groups for discount rules
        CustomerGroupIfc[] groups = customer.getCustomerGroups();
        int numGroups = 0;
        if (groups != null)
        {
            numGroups = groups.length;
        }
        for (int i = 0; i < numGroups; i++)
        {
            sql = ReadARTSCustomerSQL.buildCustomerDiscountSQL(groups[i].getGroupID());

            dataConnection.execute(sql.getSQLString());
            rs = (ResultSet)dataConnection.getResult();
            discounts = ReadARTSCustomerSQL.readCustomerDiscountResultSet(rs);

            if (discounts != null)
            {
                for (int j = 0; j < discounts.length; j++)
                {
                    discount = discounts[j];
                    sql = ReadARTSCustomerSQL.buildLocalizedRuleSQL(discount.getRuleID(), locale);
                    dataConnection.execute(sql.getSQLString());
                    rs = (ResultSet)dataConnection.getResult();
                    discount = ReadARTSCustomerSQL.readLocalizedRule(rs, discount);
                    groups[i].addDiscountRule(discount);
                }
            }
        }
        if (rs != null)
        {
            rs.close();
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomersbyTaxID.selectDiscountInfo");
    }

    /**
     * Read customer preferred locale.
     *
     * @param dataConnection The data connection on which to execute.
     * @param customer The output Customer with retrieved data
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection
     * @exception SQLException thrown when an error occurs with the ResultSet
     */
    public void readCustomerLocale(DataConnectionIfc dataConnection, CustomerIfc customer) throws DataException,
            SQLException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomersbyTaxID.readCustomerLocale");

        ResultSet rs = null;
        Locale customerLocale = customer.getPreferredLocale();

        SQLSelectStatement sql = new SQLSelectStatement();

        // build sql query
        sql.addTable(TABLE_CUSTOMER, ALIAS_CUSTOMER);
        sql.addColumn(FIELD_LOCALE);
        sql.addQualifier(FIELD_CUSTOMER_ID + " = '" + customer.getCustomerID() + "'");

        dataConnection.execute(sql.getSQLString(), false);
        rs = (ResultSet)dataConnection.getResult();

        // read locale from database result set
        if (rs.next())
        {
            String localeString = getSafeString(rs, 1);

            if (!Util.isEmpty(localeString))
            {
                // convert string representation of the locale into a
                // locale object
                customerLocale = LocaleUtilities.getLocaleFromString(localeString);
                customer.setPreferredLocale(customerLocale);
            }
        }
        rs.close();

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomersbyTaxID.readCustomerLocale");
    }

    /**
     * Attempt to resolve the party id from the customer table.
     *
     * @param dataConnection The data connection on which to execute.
     * @return an integer denoting the party_id
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection
     * @exception SQLException thrown when an error occurs with the ResultSet
     */
    public void selectBusinessInfo(DataConnectionIfc dataConnection, CustomerIfc customer, int partyId)
            throws DataException, SQLException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomersbyTaxID.selectBusinessInfo");

        SQLSelectStatement sql = ReadARTSCustomerSQL.buildBusinessInfoSQL(partyId);

        dataConnection.execute(sql.getSQLString(), false);
        ResultSet rs = (ResultSet)dataConnection.getResult();

        ReadARTSCustomerSQL.readBusinessInfoResultsForCustomer(rs, customer, dataConnection, customer.getLocaleRequestor());

        rs.close();

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomersbyTaxID.selectBusinessInfo");
    }

    /**
     * Building the query to retrieve TaxID
     *
     * @param customer The pos customer object.
     * @exception Exception thrown when an error occurs
     */
    protected SQLSelectStatement buildQuery(CustomerIfc customer)
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // add columns
        sql.addTable(TABLE_CUSTOMER);
        sql.addColumn(FIELD_PARTY_ID);
        sql.addColumn(FIELD_CUSTOMER_ID);
        sql.addColumn(FIELD_CUSTOMER_STATUS);
        sql.addColumn(FIELD_ENCRYPTED_CUSTOMER_TAX_ID);
        sql.addColumn(FIELD_MASKED_CUSTOMER_TAX_ID);
        sql.addColumn(FIELD_CUSTOMER_PRICING_GROUP_ID);
        sql.addColumn(FIELD_EMPLOYEE_ID);
        // add qualifiers
        sql.addQualifier(FIELD_MASKED_CUSTOMER_TAX_ID, getCustomerMaskedTaxID(customer));
        sql.addNotQualifier(FIELD_CUSTOMER_STATUS, CustomerConstantsIfc.CUSTOMER_STATUS_DELETED);
        return sql;
    }

    /**
     * Returns the masked tax ID of the given customer as a database safe string.
     * @param posCustomer
     * @return the masked tax ID of the given customer as a database safe string.
     */
    protected String getCustomerMaskedTaxID(CustomerIfc customer)
    {
        String maskedTaxID = "";
        if(customer != null && customer.getEncipheredTaxID() != null)
        {
            maskedTaxID = customer.getEncipheredTaxID().getMaskedNumber();
        }
        return makeSafeString(maskedTaxID);
    }

    /**
     * Building the query to retrive TaxID
     *
     * @param posCustomer The pos customer object.
     * @param customers Vector of customer object.
     * @exception DataException thrown when an error occurs
     * @deprecated As of 14.0, use {@link JdbcReadCustomersByTaxID#buildQuery(CustomerIfc)} instead.
     */
    protected String buildQuery(CustomerIfc posCustomer, Vector<CustomerIfc> customers) throws DataException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("JdbcReadCustomersbyTaxID.buildQuery()");
        }

        String sqlString = "";
        buildQuery(posCustomer).getSQLString();

        if (logger.isDebugEnabled())
        {
            logger.debug("JdbcReadCustomersbyTaxID.buildQuery()");
        }

        return sqlString;
    }

    /**
     * Set all data members should be set to their initial state.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI> All processing must be complete
     * <LI>
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI> All data member have been returned to the initial state.
     * </UL>
     */
    public void initialize() throws DataException
    {
    }
    
    /**
     * 
     */
    protected CustomerIfc configureCustomer (CustomerSearchCriteriaIfc criteria)
    {
         CustomerIfc customer = DomainGateway.getFactory().getCustomerInstance();
 
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
            
              case UNDEFINED:
                 throw new IllegalArgumentException("Unknown search type");
              default:
                  break;
         }
         return customer;
     }
    
   
    

}
