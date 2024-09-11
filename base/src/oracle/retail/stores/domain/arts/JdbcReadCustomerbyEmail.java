/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadCustomerbyEmail.java /main/17 2013/12/20 10:27:42 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  12/11/13 - fix null dereferences
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    masahu    07/07/11 - FORTIFY FIX: The sensitive SQLs get logged
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         11/2/2006 9:41:24 AM   Christian Greene
 *         change enum to enumerator
 *    5    360Commerce 1.4         6/15/2006 5:57:53 PM   Brett J. Larsen CR
 *         18490 - UDM - PA_CT.ST_CT renamed to STS_CT
 *    4    360Commerce 1.3         1/25/2006 4:11:14 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:40 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:43 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:58 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:19    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:40     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:43     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:58     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
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
 *    Rev 1.1   Mar 07 2003 17:04:54   baa
 * code review changes for I18n
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Jun 03 2002 16:37:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:47:18   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:20   msg
 * Initial revision.
 *
 *    Rev 1.0   12 Mar 2002 16:34:42   adp
 * Initial revision.
 * Resolution for Domain SCR-43: Read customer by email
 *
 *    Rev 1.0   12 Mar 2002 14:40:14   adp
 * Initial revision.
 * Resolution for mybikestore SCR-1213: POS 5.0 Integration
 *
 *    Rev 1.0   Sep 20 2001 15:55:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// java imports
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.CustomerSort;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
    This operation takes a POS domain Customer email address and retrieves
    the cutomer from the database.
    <P>
    @version $Revision: /main/17 $
**/
//-------------------------------------------------------------------------
public class JdbcReadCustomerbyEmail
    extends JdbcDataOperation
    implements ARTSDatabaseIfc
{
    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcReadCustomerbyEmail.class);

    //---------------------------------------------------------------------
    /**
       Class constructor. <P>
    **/
    //---------------------------------------------------------------------
    public JdbcReadCustomerbyEmail()
    {
        super();
        setName("JdbcReadCustomerbyEmail");
    }

     /**
      * Execute the SQL statements against the database. <P>
      * @param  dataTransaction
      * @param  dataConnection
      * @param  action
      * @exception  DataException
    */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection,DataActionIfc action) throws DataException
    {
        // get the input Customer object that contains search criteria
        CustomerIfc posCustomer = (CustomerIfc) action.getDataObject();
        LocaleRequestor locale = posCustomer.getLocaleRequestor();

        // Remove elements from previous searches.
        ArrayList<CustomerIfc> customers = new ArrayList<CustomerIfc>();

        // attempt the database access
        try
        {
            // build an SQL query String
            String sqlString = buildQuery(posCustomer);

            // execute the SQL query
            dataConnection.execute(sqlString, false);

            // get the result set
            ResultSet rs = (ResultSet) dataConnection.getResult();

            // loop through the result set, building the returned set of Customers
            while(rs.next())
            {
                // create a new Customer object
                CustomerIfc customer =
                  DomainGateway.getFactory().getCustomerInstance();
                // set the Customer ID and Record ID (party ID) that met the search criteria
                customer.setRecordID(getSafeString(rs,1));
                customer.setCustomerID(getSafeString(rs,2));
                customer.setStatus(rs.getInt(3));
                //set the employee id for each customer
                customer.setEmployeeID(posCustomer.getEmployeeID());

                // add the Customer object to the returned set
                customers.add(customer);
            }

            if (customers.size() == 0)
            {
                throw new DataException(DataException.NO_DATA,
                                        "No customers were found proccessing the result set in JdbcSelectCustomers.");
            }


            // Get contact, address, and phone info for the Customers
            for (CustomerIfc customer : customers) 
            {
                // We're going to issue three statements:  first to retrieve the
                // contact information, second to retrieve the addresses, and third
                // to retrieve the phone numbers.
                int partyID = Integer.parseInt(customer.getRecordID());
                selectContactInfo(dataConnection, customer, partyID);

                // set Addresses to reflect input search parameters
                customer.setAddressList(posCustomer.getAddressList());
                selectAddressInfo(dataConnection, customer, partyID);

                selectEmailInfo(dataConnection, customer, partyID);

                selectPhoneInfo(dataConnection, customer, partyID);

                selectGroupInfo(dataConnection, customer);
            }                           // End get additional info

            // sort the returned set of Customers
            CustomerSort.sort(customers);

            dataTransaction.setResult(customers);
        }
        catch(SQLException e)
        {
            ((JdbcDataConnection)dataConnection).logSQLException(e, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR,
                                    "An SQL Error occurred proccessing the result set from selecting customers in JdbcSelectCustomers.", e);
        }

    }

    //---------------------------------------------------------------------
    /**
       Read from the contact table. <P>
       @param  the data connection on which to execute.
       @param  cusomer to hold the data
       @param  unique identifier for a party
       @exception  DataException thrown when an error occurs executing the
       against the DataConnection
       @exception  SQLException thrown when an error occurs with the
       ResultSet
    **/
    //---------------------------------------------------------------------
    public void selectContactInfo
    (
     DataConnectionIfc dataConnection,
     CustomerIfc          customer,
     int               partyID
     )   throws DataException, SQLException
    {
        String sqlString = ReadARTSCustomerSQL.selectContactInfoSQL(partyID);

        dataConnection.execute(sqlString, false);

        ResultSet rs = (ResultSet)dataConnection.getResult();

        ReadARTSCustomerSQL.readContactResultsForCustomer(rs, customer);

        rs.close();
    }

    //---------------------------------------------------------------------
    /**
       Select all addresses from the address table. <P>
       @param  the data connection on which to execute.
       @param  cusomer to hold the data
       @param  unique identifier for a party
       @exception  DataException thrown when an error occurs executing the
       against the DataConnection
       @exception  SQLException thrown when an error occurs with the
       ResultSet
    **/
    //---------------------------------------------------------------------
    public void selectAddressInfo
    (
     DataConnectionIfc dataConnection,
     CustomerIfc       customer,
     int               partyID
     )   throws DataException, SQLException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadCustomerbyEmail.selectAddressInfo");

        String sqlString = ReadARTSCustomerSQL.selectAddressInfoSQL(partyID, customer);

        dataConnection.execute(sqlString, false);

        ResultSet rs = (ResultSet)dataConnection.getResult();

        ReadARTSCustomerSQL.readAddressResultsForCustomer(rs, customer);

        rs.close();

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadCustomerbyEmail.selectAddressInfo");
    }

    //---------------------------------------------------------------------
    /**
       Select all phone numbers from the phone table. <P>
       @param  the data connection on which to execute.
       @param  cusomer to hold the data
       @param  unique identifier for a party
       @exception  DataException thrown when an error occurs executing the
       against the DataConnection
       @exception  SQLException thrown when an error occurs with the
       ResultSet
    **/
    //---------------------------------------------------------------------
    public void selectPhoneInfo
    (
     DataConnectionIfc dataConnection,
     CustomerIfc       customer,
     int               partyID
     )   throws DataException, SQLException
    {
        // Need to figure out what to do with telephone numbers in
        // address table.
        String sqlString = ReadARTSCustomerSQL.selectPhoneInfoSQL(partyID);

        dataConnection.execute(sqlString, false);

        ResultSet rs = (ResultSet)dataConnection.getResult();

        ReadARTSCustomerSQL.readPhoneResultsForCustomer(rs, customer);

        rs.close();
    }

    //---------------------------------------------------------------------
    /**
       Select all email address from the email address table. <P>
       @param  dataConnection The data connection on which to execute.
       @param  customer  The Customer with input parameters and retrieved data
       @exception  DataException thrown when an error occurs executing the
       against the DataConnection
       @exception  SQLException thrown when an error occurs with the
       ResultSet
    **/
    //---------------------------------------------------------------------
    public void selectEmailInfo
    (
     DataConnectionIfc dataConnection,
     CustomerIfc       customer,
     int               partyID
     )   throws DataException, SQLException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadCustomer.selectEmailAddressInfo");

        String sqlString = ReadARTSCustomerSQL.selectEmailInfoSQL(partyID);

        dataConnection.execute(sqlString, false);

        ResultSet rs = (ResultSet)dataConnection.getResult();

        ReadARTSCustomerSQL.readEmailResultsForCustomer(rs, customer);

        rs.close();

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadCustomer.selectEmailAddressInfo");
    }



     /**
      * Select all the groups assigned to this customer. <P>
      * @param  dataConnection The data connection on which to execute.
      * @param  customer  The output Customer with retrieved data
      * @exception  DataException thrown when an error occurs executing the
      * against the DataConnection
      * @exception  SQLException thrown when an error occurs with the
      * ResultSet
    */
     public void selectGroupInfo(DataConnectionIfc dataConnection, CustomerIfc customer)
        throws DataException, SQLException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadCustomerbyEmail.selectGroupInfo");
        // build SQL for groups
        SQLSelectStatement sql = ReadARTSCustomerSQL.buildCustomerGroupSQL(customer.getCustomerID(), customer.getLocaleRequestor());

        // execute and retrieve results
        dataConnection.execute(sql.getSQLString());
        ResultSet rs = (ResultSet) dataConnection.getResult();

        // parse result set
        CustomerGroupIfc[] groups = ReadARTSCustomerSQL.readCustomerGroupResultSet(rs);

        rs.close();

        if (groups != null)
        {
            customer.setCustomerGroups(groups);
            selectDiscountInfo(dataConnection, customer);
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadCustomerbyEmail.selectGroupInfo");
    }



     /**
       *Select all the discount rules available to this customer. <P>
       *@param  dataConnection The data connection on which to execute.
       *@param  customer  The output Customer with retrieved data
       *@param locale the LocaleRequestor object

       *@exception  DataException thrown when an error occurs executing the
       *against the DataConnection
       *@exception  SQLException thrown when an error occurs with the
       *ResultSet
    */
     public void selectDiscountInfo(DataConnectionIfc dataConnection, CustomerIfc customer)
        throws DataException, SQLException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadCustomerbyEmail.selectDiscountInfo");

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
        LocalizedCodeIfc reasonCode = null;
        for (int i = 0; i < numGroups; i++)
        {
            sql = ReadARTSCustomerSQL.buildCustomerDiscountSQL(groups[i].getGroupID());

            dataConnection.execute(sql.getSQLString());
            rs = (ResultSet) dataConnection.getResult();
            discounts = ReadARTSCustomerSQL.readCustomerDiscountResultSet(rs);

            if (discounts != null)
            {
                for (int j = 0; j < discounts.length; j++ )
                {
                    discount = discounts[j];
                    sql = ReadARTSCustomerSQL.buildLocalizedRuleSQL(discount.getRuleID(), customer.getLocaleRequestor());
                    dataConnection.execute(sql.getSQLString());
                    rs = (ResultSet) dataConnection.getResult();
                    discount = ReadARTSCustomerSQL.readLocalizedRule(rs, discount);
                    reasonCode = DomainGateway.getFactory().getLocalizedCode();
                    reasonCode.setCode(groups[i].getGroupID());
                    reasonCode.setText(groups[i].getLocalizedNames());
                    discount.setReason(reasonCode);
                    groups[i].addDiscountRule(discount);
                }

            }
        }
        if (rs != null)
        {
            rs.close();
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadCustomerbyEmail.selectDiscountInfo");
    }

    //---------------------------------------------------------------------
    /**
       @exception  Exception thrown when an error occurs
    **/
    //---------------------------------------------------------------------
    protected String buildQuery(CustomerIfc customer)
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcSelectCustomers.buildQuery()");

        String emailAddress = customer.getEMailAddress();

        String sqlString = "\n"
            + "SELECT pa_ct.id_prty, pa_ct.id_ct, pa_ct.sts_ct\n"
            + "FROM pa_ct, lo_eml_ads\n"
            + "WHERE pa_ct.id_prty = lo_eml_ads.id_prty\n"
            + "AND lo_eml_ads.em_ads = '" + emailAddress + "'";


        if (logger.isInfoEnabled()) logger.info( "SQL is " + sqlString + "");

        if (logger.isDebugEnabled()) logger.debug( "JdbcSelectCustomers.buildQuery()");

        return sqlString;
    }

    //---------------------------------------------------------------------
    /**
       Set all data members should be set to their initial state. <P>
       <B>Pre-Condition</B>
       <UL>
       <LI>
       All processing must be complete
       <LI>
       </UL>
       <B>Post-Condition</B>
       <UL>
       <LI>
       All data member have been returned to the initial state.
       </UL>
    **/
    //---------------------------------------------------------------------
    public void initialize() throws DataException
    {                                   // begin initialize()
    }                                   // end initialize()
} // end JdbcReadCustomerbyEmail
