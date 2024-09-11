/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSelectBusiness.java /main/32 2013/07/30 16:13:16 rgour Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rgour     07/30/13 - replacing * to % for DB search
 *    abondala  12/14/12 - enhancements to the customer search
 *    abondala  12/13/12 - customer search criteria fields are all optional.
 *    acadar    08/05/12 - refactoring
 *    hyin      05/23/12 - remove areacode, add empty check for phone
 *    cgreene   04/03/12 - removed deprecated methods
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    mkutiana  03/06/12 - XbranchMerge
 *                         mkutiana_hpqc1463_13_4_1_taxcertificate_not_retrieved
 *                         from rgbustores_13.4x_generic_branch
 *    mkutiana  03/05/12 - fixes to taxid and tax certificate saving -
 *                         encrypt/decrypt issues
 *    cgreene   08/22/11 - removed deprecated methods
 *    tksharma  08/22/11 - Encryption CR code cleaning
 *    mkutiana  08/17/11 - Removed deprecated Customer.ID_HSH_ACNT from DB and
 *                         all using classes
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    acadar    01/26/10 - read email info for business customer
 *    abondala  01/03/10 - update header date
 *    mahising  12/04/08 - JUnit fix and SQL fix
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *    ranojha   11/05/08 - Fixed Tax Exempt Reason Code for Customer
 *
 * ===========================================================================
 * $Log:
 |  10   360Commerce 1.9         4/15/2008 12:53:53 PM  Charles D. Baker CR
 |       31088 - Corrected retrieval of customer account information to
 |       preserve null status in the database. This allows consistent
 |       persistence of updated customer information. Code reviewed by Leona
 |       and Dwight.
 |  9    360Commerce 1.8         3/5/2008 10:22:04 PM   Manikandan Chellapan
 |       CR#29874 Removed hardcoded select statement
 |  8    360Commerce 1.7         1/17/2008 5:24:06 PM   Alan N. Sinton  CR
 |       29954: Refactor of EncipheredCardData to implement interface and be
 |       instantiated using a factory.
 |  7    360Commerce 1.6         1/9/2008 10:54:51 AM   Alan N. Sinton  CR
 |       29849: Updated SQL to use new columns on customer table.
 |  6    360Commerce 1.5         12/18/2007 5:47:48 PM  Alan N. Sinton  CR
 |       29661: Changes per code review.
 |  5    360Commerce 1.4         9/20/2007 11:29:19 AM  Rohit Sachdeva  28813:
 |       Initial Bulk Migration for Java 5 Source/Binary Compatibility of All
 |       Products
 |  4    360Commerce 1.3         6/15/2006 5:57:54 PM   Brett J. Larsen CR
 |       18490 - UDM - PA_CT.ST_CT renamed to STS_CT
 |  3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse
 |  2    360Commerce 1.1         3/10/2005 10:22:51 AM  Robert Pearse
 |  1    360Commerce 1.0         2/11/2005 12:12:05 PM  Robert Pearse
 | $
 | Revision 1.7  2004/07/28 19:54:28  dcobb
 | @scr 6355 Can still search on original business name after it was changed
 | Modified JdbcSelectBusiness to search for name from pa_cnct table.
 |
 | Revision 1.6  2004/04/09 16:55:46  cdb
 | @scr 4302 Removed double semicolon warnings.
 |
 | Revision 1.5  2004/02/17 17:57:37  bwf
 | @scr 0 Organize imports.
 |
 | Revision 1.4  2004/02/17 16:18:47  rhafernik
 | @scr 0 log4j conversion
 |
 | Revision 1.3  2004/02/12 17:13:19  mcs
 | Forcing head revision
 |
 | Revision 1.2  2004/02/11 23:25:24  bwf
 | @scr 0 Organize imports.
 |
 | Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 | updating to pvcs 360store-current
 |
 |
 |
 |    Rev 1.0   Aug 29 2003 15:33:08   CSchellenger
 | Initial revision.
 |
 |    Rev 1.2   May 06 2003 15:58:28   baa
 | modify sql query to retrieve business name from the customer name column
 | Resolution for POS SCR-2203: Business Customer- unable to Find previous entered Busn Customer
 |
 |    Rev 1.1   Apr 28 2003 08:50:54   baa
 | fix buisiness customer
 | Resolution for POS SCR-2217: System crashes if new business customer is created and Return is selected
 |
 |    Rev 1.0   Apr 03 2003 15:19:16   baa
 | Initial revision.
 | Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 |
 |    Rev 1.4   Mar 26 2003 16:36:30   baa
 | customer refactoring
 | Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 |
 |    Rev 1.3   Mar 20 2003 18:12:22   baa
 | Refactoring of customer screens
 | Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 |
 |    Rev 1.2   Oct 09 2002 11:07:12   kmorneau
 | fixed header and changed out a deprecated call
 | Resolution for 1814: Customer find by BusinessInfo crashes POS
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.ResultList;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerConstantsIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.CustomerSort;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.common.utility.Util;

import org.apache.log4j.Logger;

/**
 * This operation selects business Customer data from the database based on
 * name, postal code, and phone number.
 */
public class JdbcSelectBusiness extends JdbcSelectCustomers
{
    private static final long serialVersionUID = -565824199702212392L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSelectBusiness.class);

    /**
     * Class constructor.
     */
    public JdbcSelectBusiness()
    {
        setName("JdbcSelectBusiness");
    }

    /**
     * Execute the SQL statements against the database.
     * 
     * @param dataTransaction
     * @param dataConnection
     * @param action
     * @exception DataException
     */
    @Override
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)  throws DataException
    {

        CustomerSearchCriteriaIfc criteria =
            (CustomerSearchCriteriaIfc) action.getDataObject();
        LocaleRequestor locale = criteria.getLocaleRequestor();
        CustomerIfc posCustomer = configureCustomer(criteria);

        ArrayList<CustomerIfc> customers = new ArrayList<CustomerIfc>();

        try
        {
            String sqlString = buildBusinessQuery(posCustomer);
            dataConnection.execute(sqlString);

            ResultSet rs = (ResultSet)dataConnection.getResult();
            String encryptedHouseAccount = null;
            String maskedHouseAccount = null;
            while (rs.next())
            {
                CustomerIfc customer =
                    DomainGateway.getFactory().getCustomerInstance();
                customer.setCustomerID(getSafeString(rs,1));
                customer.setRecordID(getSafeString(rs,2));
                customer.setEmployeeID(getSafeString(rs,3));
                customer.setStatus(rs.getInt(4));
                // CR 31088. The enciphered card data account number fields are null
                // by default. Do not use getSafeString to get them or the data values
                // in the database will be changed, affecting the POSLog formatting.
                encryptedHouseAccount = rs.getString(5);
                maskedHouseAccount = rs.getString(6);
                EncipheredCardDataIfc cardData =
                    FoundationObjectFactory.getFactory().createEncipheredCardDataInstance(
                                           encryptedHouseAccount,
                                           maskedHouseAccount,
                                           null);
                customer.setHouseCardData(cardData);
                customers.add(customer);

                // Adding tax ID and pricing group id in customer.
                String taxID =  rs.getString(7);
                if (!Util.isEmpty(taxID))
                {
                    EncipheredDataIfc encipheredTaxID = FoundationObjectFactory.getFactory().createEncipheredDataInstance(taxID);
                    customer.setEncipheredTaxID(encipheredTaxID);
                }
                
                int pricingGroupId = rs.getInt(8);
                if (pricingGroupId > 0)
                    customer.setPricingGroupID(pricingGroupId);
            }
            rs.close();

            if (customers.size() == 0)
            {
                throw new DataException(DataException.NO_DATA,
                                        "No customers were found proccessing the result set in JdbcSelectBusiness.");
            }

            for (CustomerIfc customer : customers)
            {
                customer.setLocaleRequestor(locale);
                int partyID = Integer.parseInt(customer.getRecordID());

                selectContactInfo(dataConnection, customer, partyID);
                selectAddressInfo(dataConnection, customer, partyID);
                selectPhoneInfo(dataConnection, customer, partyID);
                selectEmailInfo(dataConnection, customer, partyID);
                selectGroupInfo(dataConnection, customer);
                selectBusinessInfo(dataConnection, customer, partyID, locale);
            }

            CustomerSort.sort(customers);
            
            ResultList resultList = new ResultList(customers, 1);
            
            dataTransaction.setResult(resultList);
        }
        catch(SQLException e)
        {
            ((JdbcDataConnection)dataConnection).logSQLException(e, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR,
                                    "An SQL Error occurred proccessing the result set from selecting business customers in JdbcSelectBusCustomers.", e);
        }

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
    public void selectBusinessInfo(DataConnectionIfc dataConnection, CustomerIfc customer, int partyID,
            LocaleRequestor localeRequestor) throws DataException, SQLException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcSelectBusiness.selectBusinessInfo");

        SQLSelectStatement sql =
            ReadARTSCustomerSQL.buildBusinessInfoSQL(partyID);

        dataConnection.execute(sql.getSQLString());
        ResultSet rs = (ResultSet) dataConnection.getResult();

        ReadARTSCustomerSQL.readBusinessInfoResultsForCustomer(rs, customer, dataConnection, localeRequestor);

        rs.close();

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcSelectBusiness.selectBusinessInfo");
    }

    /**
     * Builds a business customer query string.
     * 
     * @param pos customer object
     * @throws SQLException
     */
    protected String buildBusinessQuery(CustomerIfc posCustomer) throws SQLException, DataException
    {
        String orgName = null;
        String postalCode = null;
        String phoneNumber = null;

        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcSelectBusiness.buildBusinessQuery");

        SQLSelectStatement sql = new SQLSelectStatement();

        // Add tables
        sql.addTable(TABLE_CUSTOMER, ALIAS_CUSTOMER);
        sql.addTable(TABLE_CONTACT, ALIAS_CONTACT);
        sql.addTable(TABLE_BUS_CUSTOMER, ALIAS_BUS_CUSTOMER);
        sql.addTable(TABLE_ADDRESS, ALIAS_ADDRESS);
        sql.addTable(TABLE_PHONE, ALIAS_PHONE);

        // Set Distinct flag to true
        sql.setDistinctFlag(true);

        // Add coloumns
        sql.addColumn(ALIAS_CUSTOMER,FIELD_CUSTOMER_ID);
        sql.addColumn(ALIAS_CUSTOMER,FIELD_PARTY_ID);
        sql.addColumn(ALIAS_CUSTOMER,FIELD_EMPLOYEE_ID);
        sql.addColumn(ALIAS_CUSTOMER,FIELD_CUSTOMER_STATUS);
        sql.addColumn(ALIAS_CUSTOMER,FIELD_HOUSE_ACCOUNT_NUM);
        sql.addColumn(ALIAS_CUSTOMER,FIELD_MASKED_HOUSE_ACCOUNT_NUM);

        // Adding two column for tax id and pricing group
        sql.addColumn(ALIAS_CUSTOMER,FIELD_ENCRYPTED_CUSTOMER_TAX_ID);
        sql.addColumn(ALIAS_CUSTOMER,FIELD_CUSTOMER_PRICING_GROUP_ID);
        // Add qualifiers
        sql.addJoinQualifier(ALIAS_CUSTOMER, FIELD_PARTY_ID, ALIAS_CONTACT, FIELD_PARTY_ID);
        sql.addJoinQualifier(ALIAS_CUSTOMER, FIELD_PARTY_ID, ALIAS_BUS_CUSTOMER, FIELD_PARTY_ID);
        sql.addQualifier(ALIAS_CUSTOMER + "." + FIELD_CUSTOMER_STATUS + " <> " + CustomerConstantsIfc.CUSTOMER_STATUS_DELETED);

        orgName = posCustomer.getCustomerName();
        if ((orgName != null) && (orgName.length() > 0))
        {
            String tempOrgName = makeSafeString(orgName.replaceAll("\\*", "\\%") + "%");
            sql.addQualifier("UPPER(" + ALIAS_CONTACT + "." + FIELD_CONTACT_LAST_NAME + ") LIKE UPPER(" + tempOrgName + ")");
        }

        List<AddressIfc> addressList = posCustomer.getAddressList();
        if (addressList.size() >= 1)
        {
            AddressIfc addr = addressList.get(0);
            postalCode = addr.getPostalCode();
            String postalCodeExt = addr.getPostalCodeExtension();

            if ((postalCode != null) && (postalCode.length() > 0))
            {
                if ((postalCodeExt != null) && (postalCodeExt.length() > 0))
                {
                    postalCode = postalCode.concat("-" + postalCodeExt);
                }

                String tempPostalCode = makeSafeString(postalCode + "%");

                sql.addJoinQualifier(ALIAS_CUSTOMER, FIELD_PARTY_ID, ALIAS_ADDRESS, FIELD_PARTY_ID);
                sql.addQualifier(ALIAS_ADDRESS + "." + FIELD_CONTACT_POSTAL_CODE + " LIKE " + tempPostalCode);
            }
        }

        PhoneIfc phone =  posCustomer.getPhoneByType(PhoneConstantsIfc.PHONE_TYPE_WORK);
        if(phone != null && !Util.isEmpty(phone.getPhoneNumber()))
        {
                phoneNumber = phone.getPhoneNumber();

                sql.addJoinQualifier(ALIAS_CUSTOMER, FIELD_PARTY_ID,ALIAS_PHONE,FIELD_PARTY_ID);
                sql.addQualifier(ALIAS_PHONE,FIELD_PHONE_TYPE, makeSafeString(String.valueOf(phone.getPhoneType())));

                if ((phoneNumber != null) && (phoneNumber.trim().length()>0))
                {
                    String tempPhoneNumber = makeSafeString(phoneNumber);

                    sql.addQualifier(ALIAS_PHONE + "." + FIELD_CONTACT_LOCAL_TELEPHONE_NUMBER + " LIKE " + tempPhoneNumber);
                }
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcSelectBusiness.buildBusinessQuery");

        /* Return Sql String
         *  SELECT DISTINCT CU.ID_CT, CU.ID_PRTY, CU.ID_EM, CU.STS_CT, CU.ID_NCRPT_ACTN_CRD,
         *  CU.ID_MSK_ACNT_CRD FROM PA_PHN PH, ORGN_CT BCUST, PA_CNCT CNT,
         *  LO_ADS AD, PA_CT CU WHERE CU.ID_PRTY = CNT.ID_PRTY AND CU.ID_PRTY = BCUST.ID_PRTY
         *  AND CU.STS_CT <> 2 AND UPPER(CNT.LN_CNCT) LIKE UPPER('BusinessName%') AND CU.ID_PRTY = AD.ID_PRTY
         *  AND AD.PC_CNCT LIKE 'PostalCode%'
         */
        return sql.getSQLString();
    }
}
