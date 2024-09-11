/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadCustomer.java /main/30 2013/07/26 10:23:23 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/24/13 - remove postal ext code
 *    abondala  12/14/12 - enhancements to the customer search
 *    abondala  12/13/12 - customer search criteria fields are all optional.
 *    acadar    08/05/12 - refactoring
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    cgreene   08/22/11 - removed deprecated methods
 *    mkutiana  08/17/11 - Removed deprecated Customer.ID_HSH_ACNT from DB and
 *                         all using classes
 *    vtemker   07/28/11 - Build query in selectCustomer() method based on
 *                         layaway link status
 *    masahu    07/07/11 - FORTIFY FIX: The sensitive SQLs get logged
 *    rrkohli   07/05/11 - encryption CR
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    djenning  07/17/09 - can't delete readCustomerLocale since it must be
 *                         deprecated first.
 *    djenning  07/17/09 - removing extra call to customer locale. not locale
 *                         is read out with the customer.
 *    cgreene   04/14/09 - convert pricingGroupID to integer instead of string
 *    mahising  12/23/08 - fix base issue
 *    mahising  12/04/08 - JUnit fix and SQL fix
 *    npoola    11/30/08 - CSP POS and BO changes
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *    ranojha   11/05/08 - Fixed Tax Exempt Reason Code for Customer
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *
 * ===========================================================================
 * $Log:
 *    11   360Commerce 1.10        1/17/2008 5:24:06 PM   Alan N. Sinton  CR
 *         29954: Refactor of EncipheredCardData to implement interface and be
 *          instantiated using a factory.
 *    10   360Commerce 1.9         1/4/2008 4:46:41 PM    Alan N. Sinton  CR
 *         29849: Refactor of customer table to use encrypted, hashed and
 *         masked fields.
 *    9    360Commerce 1.8         12/18/2007 5:47:48 PM  Alan N. Sinton  CR
 *         29661: Changes per code review.
 *    8    360Commerce 1.7         12/7/2007 4:20:51 PM   Alan N. Sinton  CR
 *         29661: Expect the House Account number to be encrypted in the
 *         customer database.
 *    7    360Commerce 1.6         11/27/2007 12:32:24 PM Alan N. Sinton  CR
 *         29661: Encrypting, masking and hashing account numbers for House
 *         Account.
 *    6    360Commerce 1.5         5/12/2006 5:26:27 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    5    360Commerce 1.4         1/25/2006 4:11:14 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:43:43 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:40 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:42 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:58 PM  Robert Pearse
 *:
 *    6    .v710     1.2.2.0     9/21/2005 13:39:46     Brendan W. Farrell
 *         Initial Check in merge 67.
 *    5    .v700     1.2.3.1     11/16/2005 16:26:19    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    4    .v700     1.2.3.0     11/12/2005 12:10:53    Deepanshu       CR
 *         4064: Replace the hardcoded SQL containing "\n" characters with the
 *         SQLSelectStatement
 *    3    360Commerce1.2         3/31/2005 15:28:40     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:42     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:58     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:44  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:16  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:31:40   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Mar 07 2003 17:04:54   baa
 * code review changes for I18n
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   03 Sep 2002 14:12:52   djefferson
 * added support for Business Customer
 * Resolution for POS SCR-1605: Business Customer
 *
 *    Rev 1.0   Jun 03 2002 16:37:00   msg
 * Initial revision.
 *
 *    Rev 1.2   May 14 2002 21:05:40   mpm
 * Made corrections for DB2.
 * Resolution for Domain SCR-50: db2 port fixes
 *
 *    Rev 1.1   Mar 18 2002 22:47:16   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:20   msg
 * Initial revision.
 *
 *    Rev 1.2   06 Feb 2002 18:20:14   sfl
 * Use work around to avoid using getInt because
 * Postgresql database doesn't support getInt.
 * Resolution for Domain SCR-28: Porting POS 5.0 to Postgresql
 *
 *    Rev 1.1   07 Jan 2002 11:49:44   jbp
 * updates for modification of email address.
 * Resolution for POS SCR-544: Email Address Updates
 *
 *    Rev 1.0   Sep 20 2001 15:58:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.ResultList;

import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerConstantsIfc;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation takes a POS domain Customer and creates a new entry in the
 * database.
 * 
 * @version $Revision: /main/30 $
 */
public class JdbcReadCustomer extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -2150333877590045472L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadCustomer.class);

    /**
     * Class constructor.
     */
    public JdbcReadCustomer()
    {
        setName("JdbcReadCustomer");
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
        //ARTSCustomer artsCustomer = (ARTSCustomer)action.getDataObject();
        CustomerSearchCriteriaIfc criteria = (CustomerSearchCriteriaIfc)action.getDataObject();
        CustomerIfc posCustomer = configureCustomer(criteria);
        LocaleRequestor localeRequestor = criteria.getLocaleRequestor();

        try
        {
            ARTSCustomer workingARTSCustomer = selectCustomer(dataConnection, posCustomer);

            if (workingARTSCustomer.getPosCustomer() == null)
            {
                throw new DataException(DataException.NO_DATA,
                        "No customer was found proccessing the result set in JdbcReadCustomer.");
            }

            workingARTSCustomer.getPosCustomer().setLocaleRequestor(localeRequestor);
            // We're going to issue three statements: first to retrieve the
            // contact information, second to retrieve the addresses, and third
            // to retrieve the phone numbers.
            selectContactInfo(dataConnection, workingARTSCustomer);
            // set Addresses to reflect input search parameters
            workingARTSCustomer.getPosCustomer().setAddressList(posCustomer.getAddressList());
            selectAddressInfo(dataConnection, workingARTSCustomer);
            selectEmailInfo(dataConnection, workingARTSCustomer);
            selectPhoneInfo(dataConnection, workingARTSCustomer);
            selectGroupInfo(dataConnection, workingARTSCustomer);
            selectBusinessInfo(dataConnection, workingARTSCustomer, localeRequestor);
            
            ArrayList list = new ArrayList();
            list.add(workingARTSCustomer.getPosCustomer());
            
            ResultList resultList = new ResultList(list, 1);
            
            dataTransaction.setResult(resultList);
        }
        catch (SQLException e)
        {
            ((JdbcDataConnection)dataConnection).logSQLException(e, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR,
                    "An SQL Error occurred proccessing the result set from selecting a customer in JdbcReadCustomer.",
                    e);
        }
    }
    
    /**
     * 
     */
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
    
    //TODO: remove method when JPA in place
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
        
    }
    

    /**
     * Read from the contact table.
     * 
     * @param dataConnection The data connection on which to execute.
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection
     * @exception SQLException thrown when an error occurs with the ResultSet
     */
    public void selectContactInfo(DataConnectionIfc dataConnection, ARTSCustomer artsCustomer) throws DataException,
            SQLException
    {
        String sqlString = ReadARTSCustomerSQL.selectContactInfoSQL(artsCustomer.getPartyId());
        dataConnection.execute(sqlString, false);
        ResultSet rs = (ResultSet)dataConnection.getResult();
        ReadARTSCustomerSQL.readContactResultsForCustomer(rs, artsCustomer.getPosCustomer());
        rs.close();
    }

    /**
     * Select all addresses from the address table.
     * 
     * @param dataConnection The data connection on which to execute.
     * @param customer The Customer with input parameters and retrieved data
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection
     * @exception SQLException thrown when an error occurs with the ResultSet
     */
    public void selectAddressInfo(DataConnectionIfc dataConnection, ARTSCustomer customer) throws DataException,
            SQLException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomer.selectAddressInfo");

        String sqlString = ReadARTSCustomerSQL.selectAddressInfoSQL(customer.getPartyId(), customer.getPosCustomer());
        dataConnection.execute(sqlString, false);
        ResultSet rs = (ResultSet)dataConnection.getResult();
        ReadARTSCustomerSQL.readAddressResultsForCustomer(rs, customer.getPosCustomer());
        rs.close();

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomer.selectAddressInfo");
    }

    /**
     * Select all email address from the email address table.
     * 
     * @param dataConnection The data connection on which to execute.
     * @param customer The Customer with input parameters and retrieved data
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection
     * @exception SQLException thrown when an error occurs with the ResultSet
     */
    public void selectEmailInfo(DataConnectionIfc dataConnection, ARTSCustomer customer) throws DataException,
            SQLException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomer.selectEmailAddressInfo");

        String sqlString = ReadARTSCustomerSQL.selectEmailInfoSQL(customer.getPartyId());
        dataConnection.execute(sqlString, false);
        ResultSet rs = (ResultSet)dataConnection.getResult();
        ReadARTSCustomerSQL.readEmailResultsForCustomer(rs, customer.getPosCustomer());
        rs.close();

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomer.selectEmailAddressInfo");
    }

    /**
     * Select all phone numbers from the phone table.
     * 
     * @param dataConnection The data connection on which to execute.
     * @param customer The output Customer with retrieved data
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection
     * @exception SQLException thrown when an error occurs with the ResultSet
     */
    public void selectPhoneInfo(DataConnectionIfc dataConnection, ARTSCustomer customer) throws DataException,
            SQLException
    {
        // Need to figure out what to do with telephone numbers in address table.
        String sqlString = ReadARTSCustomerSQL.selectPhoneInfoSQL(customer.getPartyId());
        dataConnection.execute(sqlString, false);
        ResultSet rs = (ResultSet)dataConnection.getResult();
        ReadARTSCustomerSQL.readPhoneResultsForCustomer(rs, customer.getPosCustomer());
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
    public void selectGroupInfo(DataConnectionIfc dataConnection, ARTSCustomer customer) throws DataException,
            SQLException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadCustomer.selectGroupInfo");
        // build SQL for groups
        SQLSelectStatement sql = ReadARTSCustomerSQL.buildCustomerGroupSQL(customer.getPosCustomer().getCustomerID(), customer.getPosCustomer().getLocaleRequestor());

        // execute and retrieve results
        dataConnection.execute(sql.getSQLString());
        ResultSet rs = (ResultSet) dataConnection.getResult();

        // parse result set
        CustomerGroupIfc[] groups = ReadARTSCustomerSQL.readCustomerGroupResultSet(rs);

        rs.close();

        if (groups != null)
        {
            customer.getPosCustomer().setCustomerGroups(groups);
            selectDiscountInfo(dataConnection, customer);
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadCustomer.selectGroupInfo");
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
    public static void selectDiscountInfo(DataConnectionIfc dataConnection, ARTSCustomer customer) throws DataException,
            SQLException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomer.selectDiscountInfo");

        ResultSet rs = null;
        DiscountRuleIfc[] discounts = null;
        DiscountRuleIfc discount = null;
        SQLSelectStatement sql = null;
        // loop through groups for discount rules
        CustomerGroupIfc[] groups = customer.getPosCustomer().getCustomerGroups();
        if (groups != null)
        {
            int numGroups = groups.length;
            for (int i = 0; i < numGroups; i++)
            {
                sql = ReadARTSCustomerSQL.buildCustomerDiscountSQL(groups[i].getGroupID());

                dataConnection.execute(sql.getSQLString());
                rs = (ResultSet)dataConnection.getResult();
                discounts = ReadARTSCustomerSQL.readCustomerDiscountResultSet(rs);
                LocalizedCodeIfc reasonCode = null;
                if (discounts != null)
                {
                    for (int j = 0; j < discounts.length; j++)
                    {
                        discount = discounts[j];
                        sql = ReadARTSCustomerSQL.buildLocalizedRuleSQL(discount.getRuleID(), customer.getPosCustomer()
                                .getLocaleRequestor());
                        dataConnection.execute(sql.getSQLString());
                        rs = (ResultSet)dataConnection.getResult();
                        discount = ReadARTSCustomerSQL.readLocalizedRule(rs, discount);
                        reasonCode = DomainGateway.getFactory().getLocalizedCode();
                        reasonCode.setCode(groups[i].getGroupID());
                        reasonCode.setText(groups[i].getLocalizedNames());
                        discount.setReason(reasonCode);
                        groups[i].addDiscountRule(discount);
                    }

                }
                rs.close();
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCustomer.selectDiscountInfo");
    }

    /**
     * Attempt to retrieve a customer. The customerSearchCriteria needs to
     * have the customerId and the customerIDPrefix populated.  From this, a query
     * is made for a customerID that is a concatenation of the customerIDPrefix and
     * the customerID.  If no such customer is found, a search is performed on the
     * customerID with the prefix pre-pended.
     *
     * @param dataConnection Connection to the database
     * @param customerSearchCriteria Customer object containing search critera
     * @return Customer object with partyID, houseAccountNumber, employeeID, customerStatus,
     * and customerID filled in.
     * @throws DataException
     * @throws SQLException
     * @since NEP67
     */
    public ARTSCustomer selectCustomer(DataConnectionIfc dataConnection, CustomerIfc customerSearchCriteria)
            throws DataException, SQLException
    {
        ARTSCustomer artsCustomer = new ARTSCustomer();
        CustomerIfc customer = null;

        String customerID = customerSearchCriteria.getCustomerID();
        SQLSelectStatement sql = new SQLSelectStatement();
        sql.addTable(TABLE_CUSTOMER);

        sql.addColumn(FIELD_PARTY_ID);
        sql.addColumn(FIELD_EMPLOYEE_ID);
        sql.addColumn(FIELD_CUSTOMER_STATUS);
        sql.addColumn(FIELD_CUSTOMER_PRICING_GROUP_ID);
        sql.addColumn(FIELD_MASKED_CUSTOMER_TAX_ID);
        sql.addColumn(FIELD_ENCRYPTED_CUSTOMER_TAX_ID);
        sql.addColumn(FIELD_HOUSE_ACCOUNT_NUM);
        sql.addColumn(FIELD_MASKED_HOUSE_ACCOUNT_NUM);
        sql.addColumn(FIELD_LOCALE);
        sql.addQualifier(FIELD_CUSTOMER_ID, makeSafeString(customerID));
        if (!customerSearchCriteria.isCustomerLinkedWithLayawayAttributeEnabled())
        {
            sql.addNotQualifier(FIELD_CUSTOMER_STATUS, CustomerConstantsIfc.CUSTOMER_STATUS_DELETED);
        }

        dataConnection.execute(sql.getSQLString(), false);

        ResultSet resultSet = (ResultSet)dataConnection.getResult();
        if (resultSet.next())
        {
            int index=0;
            int partyID = Integer.parseInt(getSafeString(resultSet,++index));
            customer = DomainGateway.getFactory().getCustomerInstance();
            customer.setCustomerID(customerID);
            customer.setRecordID(String.valueOf(partyID));
            customer.setEmployeeID(getSafeString(resultSet,++index));
            customer.setStatus(Integer.parseInt(getSafeString(resultSet,++index)));
            int pricingGroupID = resultSet.getInt(++index);
            if (pricingGroupID > 0)
                customer.setPricingGroupID(pricingGroupID);
            String maskedNumber = resultSet.getString(++index);
            String encryptedNumber = resultSet.getString(++index);
            EncipheredDataIfc customerTaxID = FoundationObjectFactory.getFactory()
                    .createEncipheredDataInstance(encryptedNumber, maskedNumber);
            customer.setEncipheredTaxID(customerTaxID);
            String houseAccount = resultSet.getString(++index);
            String maskedHouseAccount = resultSet.getString(++index);
            String locale = getSafeString(resultSet,++index);
            if (!Util.isEmpty(locale))
            {
                // convert string representation of the locale into a
                // locale object
            	Locale customerLocale = LocaleUtilities.getLocaleFromString(locale);
            	customer.setPreferredLocale(customerLocale);
            }

            EncipheredCardDataIfc cardData =
                FoundationObjectFactory.getFactory().createEncipheredCardDataInstance(
                                       houseAccount,
                                       maskedHouseAccount,
                                       null);
            customer.setHouseCardData(cardData);

            artsCustomer.setPartyId(partyID);
            artsCustomer.setPosCustomer(customer);
            resultSet.close();
        }
        else
        {
            artsCustomer = selectCustomer(dataConnection, customerSearchCriteria.getCustomerID());
        }


        return artsCustomer;
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
    public ARTSCustomer selectCustomer(DataConnectionIfc dataConnection,
                                       String customerID)
        throws DataException, SQLException
    {
        ARTSCustomer artsCustomer = new ARTSCustomer();
        CustomerIfc     customer     = null;

        // Select the party id, employee id, and status code from the
        // customer table
        SQLSelectStatement sql = new SQLSelectStatement();
        sql.addTable(TABLE_CUSTOMER);

        sql.addColumn(FIELD_PARTY_ID);
        sql.addColumn(FIELD_TILL_PAYMENT_EMPLOYEE_ID);
        sql.addColumn(FIELD_CUSTOMER_STATUS);
        sql.addColumn(FIELD_CUSTOMER_PRICING_GROUP_ID);
        sql.addColumn(FIELD_MASKED_CUSTOMER_TAX_ID);
        sql.addColumn(FIELD_ENCRYPTED_CUSTOMER_TAX_ID);
        sql.addColumn(FIELD_HOUSE_ACCOUNT_NUM);
        sql.addColumn(FIELD_MASKED_HOUSE_ACCOUNT_NUM);
        sql.addColumn(FIELD_LOCALE);
        
        sql.addQualifier(FIELD_CUSTOMER_ID, makeSafeString(customerID));
        sql.addNotQualifier(FIELD_CUSTOMER_STATUS, CustomerConstantsIfc.CUSTOMER_STATUS_DELETED);

        dataConnection.execute(sql.getSQLString(), false);

        ResultSet resultSet = (ResultSet)dataConnection.getResult();
        if (resultSet.next())
        {
            int partyID = Integer.parseInt(getSafeString(resultSet,1));

            if (partyID != 0)
            {
                customer = DomainGateway.getFactory().getCustomerInstance();
                customer.setCustomerID(customerID);
                customer.setRecordID(String.valueOf(partyID));
                customer.setEmployeeID(getSafeString(resultSet,2));
                customer.setStatus(Integer.parseInt(getSafeString(resultSet,3)));
                int pricingGroupID = resultSet.getInt(4);
                if (pricingGroupID > 0)
                    customer.setPricingGroupID(pricingGroupID);
                String maskedNumber = resultSet.getString(5);
                String encryptedNumber = resultSet.getString(6);
                EncipheredDataIfc customerTaxID = FoundationObjectFactory.getFactory()
                        .createEncipheredDataInstance(encryptedNumber, maskedNumber);
                customer.setEncipheredTaxID(customerTaxID);
                String houseAccount = resultSet.getString(7);
                String maskedHouseAccount = resultSet.getString(8);
                String locale = getSafeString(resultSet,9);
                if (!Util.isEmpty(locale))
                {
                    // convert string representation of the locale into a
                    // locale object
                	Locale customerLocale = LocaleUtilities.getLocaleFromString(locale);
                	customer.setPreferredLocale(customerLocale);
                }

                EncipheredCardDataIfc cardData =
                    FoundationObjectFactory.getFactory().createEncipheredCardDataInstance(
                                           houseAccount,
                                           maskedHouseAccount,
                                           null);
                customer.setHouseCardData(cardData);

                artsCustomer.setPartyId(partyID);
                artsCustomer.setPosCustomer(customer);
            }
        }

        resultSet.close();

        return artsCustomer;
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
    public void readCustomerLocale(DataConnectionIfc dataConnection,
                                   ARTSCustomer customer)
        throws DataException, SQLException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadCustomer.readCustomerLocale");

        ResultSet rs = null;
        Locale customerLocale = customer.getPosCustomer().getPreferredLocale();

        SQLSelectStatement sql = new SQLSelectStatement();

        // build sql query
        sql.addTable(TABLE_CUSTOMER, ALIAS_CUSTOMER);
        sql.addColumn(FIELD_LOCALE);
        sql.addQualifier(FIELD_CUSTOMER_ID + " = '" + customer.getPosCustomer().getCustomerID() + "'");

        dataConnection.execute(sql.getSQLString());
        rs = (ResultSet) dataConnection.getResult();

        // read locale from database result set
        if (rs.next())
        {
            String localeString = getSafeString(rs, 1);

            if (!Util.isEmpty(localeString))
            {
                // convert string representation of the locale into a
                // locale object
                customerLocale = LocaleUtilities.getLocaleFromString(localeString);
                customer.getPosCustomer().setPreferredLocale(customerLocale);
            }
        }
        rs.close();

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadCustomer.readCustomerLocale");
    }

    /**
     * Attempt to resolve the party id from the customer table.
     * 
     * @param dataConnection The data connection on which to execute.
     * @param customer
     * @param localeRequestor
     * @return an integer denoting the party_id
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection
     * @exception SQLException thrown when an error occurs with the ResultSet
     */
    public void selectBusinessInfo(DataConnectionIfc dataConnection,
                                   ARTSCustomer customer,
                                   LocaleRequestor localeRequestor)
        throws DataException, SQLException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcReadCustomer.selectBusinessInfo");

        SQLSelectStatement sql =
            ReadARTSCustomerSQL.buildBusinessInfoSQL
            (customer.getPartyId());


        dataConnection.execute(sql.getSQLString());
        ResultSet rs = (ResultSet) dataConnection.getResult();

        ReadARTSCustomerSQL.readBusinessInfoResultsForCustomer(rs,
                                                               customer.getPosCustomer(),
                                                               dataConnection,
                                                               localeRequestor);

        rs.close();

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcReadCustomer.selectBusinessInfo");
    }

    /**
     * Set all data members should be set to their initial state.
     * <P>
     * No action taken here.
     */
    @Override
    public void initialize() throws DataException
    {
    }
}
