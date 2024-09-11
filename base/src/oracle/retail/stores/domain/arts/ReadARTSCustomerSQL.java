/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ReadARTSCustomerSQL.java /main/34 2012/11/30 15:36:53 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  11/23/12 - Added customer receipt preference column
 *    asinton   10/05/12 - fixed sql indexing error on selecting phone details
 *    cgreene   04/03/12 - removed deprecated methods
 *    rabhawsa  10/26/12 - tax certificate should be encrypted in db
 *    asinton   03/28/12 - fix customer lookup query
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    mkutiana  03/06/12 - XbranchMerge
 *                         mkutiana_hpqc1463_13_4_1_taxcertificate_not_retrieved
 *                         from rgbustores_13.4x_generic_branch
 *    mkutiana  03/05/12 - fixes to taxid and tax certificate saving -
 *                         encrypt/decrypt issues
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   08/22/11 - removed deprecated methods
 *    tksharma  08/02/11 - Customer Search fails if Address line1 is in
 *                         different case
 *    tksharma  08/02/11 - Forward Port: 906-BOSE-SEARCH CUSTOMER INFO
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    cgreene   09/29/09 - XbranchMerge cgreene_bug-8931245 from
 *                         rgbustores_13.1x_branch
 *    cgreene   09/29/09 - remove time char column from promo eligibility table
 *    mpbarnet  09/21/09 - In selectAddressInfoSQL(), escape apostrophes in the
 *                         address lines.
 *    cgreene   04/14/09 - convert pricingGroupID to integer instead of string
 *    mkochumm  02/18/09 - retrieve customer preferred locale
 *    mkochumm  02/16/09 - set country
 *    mahising  01/14/09 - fixed QA issue
 *    ranojha   11/05/08 - Fixed Tax Exempt Reason Code for Customer
 *    mkochumm  11/04/08 - i18n changes for phone and postalcode fields
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *    acadar    11/02/08 - changes to read the localized reason codes for
 *                         customer groups and store coupons
 *    acadar    10/30/08 - use localized reason codes for item and transaction
 *                         discounts
 * ===========================================================================
 |   $Log:
 |    10   360Commerce 1.9         4/17/2008 10:56:49 AM  Charles D. Baker CR
 |         31202 - Updated to consistently store empty email addresses as Null
 |          between DB2 and Oracle DB. Also updated to avoid loading empty
 |         email addresses when working with a customer object. Code reviewed
 |         by Owen Horne.
 |    9    360Commerce 1.8         12/12/2006 4:39:52 PM  Brendan W. Farrell
 |         Only add email addresses that contain an address.
 |    8    360Commerce 1.7         7/12/2006 5:13:46 PM   Brendan W. Farrell
 |         Fix for UDM
 |    7    360Commerce 1.6         6/9/2006 2:38:36 PM    Brett J. Larsen CR
 |         18490 - UDM
 |         FL_NM_.* changed to NM_.*
 |         timestamp fields in DO_EMSG renamed to ts_..._emsg for consistency
 |    6    360Commerce 1.5         6/8/2006 3:54:25 PM    Brett J. Larsen CR
 |         18490 - UDM - columns CD_MTH_PRDV, CD_SCP_PRDV and CD_BAS_PRDV's
 |         type was changed to INTEGER
 |    5    360Commerce 1.4         6/6/2006 6:03:44 PM    Brett J. Larsen CR
 |         18490 - UDM - TimeDatePriceDerivationRuleEligibility
 |         (CO_EL_TM_PRDV) - Effective/Expiration Dates changed to type:
 |         TIMESTAMP
 |    4    360Commerce 1.3         1/25/2006 4:11:41 PM   Brett J. Larsen merge
 |          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 |    3    360Commerce 1.2         3/31/2005 4:29:33 PM   Robert Pearse
 |    2    360Commerce 1.1         3/10/2005 10:24:30 AM  Robert Pearse
 |    1    360Commerce 1.0         2/11/2005 12:13:32 PM  Robert Pearse
 |   $:
 |    4    .v700     1.2.1.0     11/16/2005 16:26:53    Jason L. DeLeau 4215:
 |         Get rid of redundant ArtsDatabaseifc class
 |    3    360Commerce1.2         3/31/2005 15:29:33     Robert Pearse
 |    2    360Commerce1.1         3/10/2005 10:24:30     Robert Pearse
 |    1    360Commerce1.0         2/11/2005 12:13:32     Robert Pearse
 |   $
 |   Revision 1.6  2004/04/09 16:55:46  cdb
 |   @scr 4302 Removed double semicolon warnings.
 |
 |   Revision 1.5  2004/02/17 17:57:37  bwf
 |   @scr 0 Organize imports.
 |
 |   Revision 1.4  2004/02/17 16:18:46  rhafernik
 |   @scr 0 log4j conversion
 |
 |   Revision 1.3  2004/02/12 17:13:19  mcs
 |   Forcing head revision
 |
 |   Revision 1.2  2004/02/11 23:25:23  bwf
 |   @scr 0 Organize imports.
 |
 |   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 |   updating to pvcs 360store-current
 |
 |
 |
 |    Rev 1.0   Aug 29 2003 15:33:56   CSchellenger
 | Initial revision.
 |
 |    Rev 1.3   Mar 20 2003 18:12:24   baa
 | Refactoring of customer screens
 | Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 |
 |    Rev 1.2   Mar 07 2003 17:04:58   baa
 | code review changes for I18n
 | Resolution for POS SCR-1740: Code base Conversions
 |
 |    Rev 1.1   03 Sep 2002 14:12:52   djefferson
 | added support for Business Customer
 | Resolution for POS SCR-1605: Business Customer
 |
 |    Rev 1.0   Jun 03 2002 16:42:16   msg
 | Initial revision.
 |
 |    Rev 1.3   May 14 2002 21:05:42   mpm
 | Made corrections for DB2.
 | Resolution for Domain SCR-50: db2 port fixes
 |
 |    Rev 1.2   May 12 2002 23:40:10   mhr
 | db2 quote fixes.  chars/varchars must be quoted and ints/decimals must not be quoted.
 | Resolution for Domain SCR-50: db2 port fixes
 |
 |    Rev 1.1   Mar 18 2002 22:50:56   msg
 | - updated copyright
 |
 |    Rev 1.0   Mar 18 2002 12:10:20   msg
 | Initial revision.
 |
 |    Rev 1.3   06 Feb 2002 18:19:12   sfl
 | Use work around to avoid using getInt because
 | Postgresql database doesn't support getInt.
 | Resolution for Domain SCR-28: Porting POS 5.0 to Postgresql
 |
 |    Rev 1.2   11 Jan 2002 17:58:24   baa
 | sort phone list by ascending order
 | Resolution for POS SCR-567: Customer Select Add, Find, Delete display telephone type as Home/Work
 |
 |    Rev 1.1   07 Jan 2002 11:49:46   jbp
 | updates for modification of email address.
 | Resolution for POS SCR-544: Email Address Updates
 |
 |    Rev 1.0   Sep 20 2001 15:59:12   msg
 | Initial revision.
 |
 |    Rev 1.1   Sep 17 2001 12:33:24   msg
 | header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.PricingGroupIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.domain.utility.EmailAddressIfc;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This is a utility class that generates the SQL needed to read the customer
 * information from the ARTS database, as well as the methods that know how to
 * process the ResultSet of these queries.
 * 
 */
public class ReadARTSCustomerSQL implements ARTSDatabaseIfc, DiscountRuleConstantsIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(ReadARTSCustomerSQL.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/34 $";

    /**
     * Format the SQL to select from the Contact table.
     * 
     * @param partyId the party id key value
     * @return The SQL String
     */
    public static String selectContactInfoSQL(int partyId)
    {
        String sqlString = "\n" + "select \n" + "ln_cnct, fn_cnct, md_cnct, lu_cnct_sln, nm_cnct_sfx, dc_cnct, \n"
                + "gndr_cnct, co_nm_cnct, no_mail_cnct, no_phn_cnct, no_eml_cnct, cd_prf_rcpt_cnct, nm_cnct \n" + "from pa_cnct \n"
                + "where \n" + "id_prty = " + partyId + " and " + "ty_cnct = '" + ARTSDatabaseIfc.CUSTOMER_CONTACT_TYPE
                + "'";
        return sqlString;
    }

    /**
     * Format the SQL to select from the customer table.
     * 
     * @param partyId the party id key value
     * @return The SQL String
     */
    public static String selectCustomerLocaleSQL(int partyId)
    {
        String sqlString = "\n" + "select \n" + "lcl \n" + "from pa_ct \n" + "where \n" + "id_prty = " + partyId;
        return sqlString;
    }

    /**
     * Read the data from the ResultSet following execution of the SQL generated
     * by selectCustomerLocaleSQL and backfill the Customer with the data.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI>rs is a valid ResultSet with results pending from the SQL
     * selectContactInfoSQL
     * <LI>customer is not null
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI>The customer is backfilled with the read data
     * </UL>
     * 
     * @param rs The ResultSet to read
     * @param retrievedCustomer The Customer to populate with the data
     * @exception SQLException when an error occurs processing the ResultSet
     */
    public static void readCustomerLocaleForCustomer(ResultSet rs, CustomerIfc retrievedCustomer) throws SQLException
    {
        Locale customerLocale = null;
        if (rs.next())
        {
            String localeString = JdbcDataOperation.getSafeString(rs, 1);
            if (!Util.isEmpty(localeString))
            {
                // convert string representation of the locale into a
                // locale object
                customerLocale = LocaleUtilities.getLocaleFromString(localeString);
                retrievedCustomer.setPreferredLocale(customerLocale);
            }
        }

    }

    /**
     * Read the data from the ResultSet following execution of the SQL generated
     * by selectContactInfoSQL and backfill the Customer with the data.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI>rs is a valid ResultSet with results pending from the SQL
     * selectContactInfoSQL
     * <LI>customer is not null
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI>The customer is backfilled with the read data
     * </UL>
     * 
     * @param rs The ResultSet to read
     * @param retrievedCustomer The Customer to populate with the data
     * @exception SQLException when an error occurs processing the ResultSet
     */
    public static void readContactResultsForCustomer(ResultSet rs, CustomerIfc retrievedCustomer) throws SQLException
    {

        while (rs.next())
        {
            // Set the fields in the domain.

            retrievedCustomer.setLastName(JdbcDataOperation.getSafeString(rs, 1));
            retrievedCustomer.setFirstName(JdbcDataOperation.getSafeString(rs, 2));
            retrievedCustomer.setMiddleName(JdbcDataOperation.getSafeString(rs, 3));
            retrievedCustomer.setSalutation(JdbcDataOperation.getSafeString(rs, 4));
            retrievedCustomer.setNameSuffix(JdbcDataOperation.getSafeString(rs, 5));

            retrievedCustomer.parseBirthDate(JdbcDataOperation.getSafeString(rs, 6));
            retrievedCustomer.setGenderCode(Integer.parseInt(JdbcDataOperation.getSafeString(rs, 7)));

            retrievedCustomer.setCompanyName(JdbcDataOperation.getSafeString(rs, 8));
            retrievedCustomer.setMailPrivacy(JdbcDataOperation.getBooleanFromString(rs, 9));
            retrievedCustomer.setTelephonePrivacy(JdbcDataOperation.getBooleanFromString(rs, 10));
            retrievedCustomer.setEMailPrivacy(JdbcDataOperation.getBooleanFromString(rs, 11));
            retrievedCustomer.setReceiptPreference(rs.getInt(12));

            retrievedCustomer.setCustomerName(JdbcDataOperation.getSafeString(rs, 13));

        }

    }

    /**
     * Format the SQL to select from the Address table.
     * 
     * @param partyID The unique identifier for the Customer in the database
     * @param customer The Customer object with search parameters
     * @return The SQL String
     */
    public static String selectAddressInfoSQL(int partyID, CustomerIfc customer) throws DataException
    {

        AddressIfc addr;
        String line1 = "";
        String line2 = "";
        String line3 = "";
        String city;
        String state;
        String postalCode;
        String postalCodeExt;
        Vector<String> lines;
        List<AddressIfc> addressList;

        SQLSelectStatement sql = new SQLSelectStatement();
        // set table
        sql.addTable(TABLE_ADDRESS);
        // set columns
        sql.addColumn(FIELD_ADDRESS_TYPE_CODE);
        sql.addColumn(FIELD_CONTACT_ADDRESS_LINE_1);
        sql.addColumn(FIELD_CONTACT_ADDRESS_LINE_2);
        sql.addColumn(FIELD_CONTACT_ADDRESS_LINE_3);
        sql.addColumn(FIELD_CONTACT_CITY);
        sql.addColumn(FIELD_CONTACT_STATE);
        sql.addColumn(FIELD_CONTACT_POSTAL_CODE);
        sql.addColumn(FIELD_CONTACT_COUNTRY);
        // set qualifier
        sql.addQualifier(FIELD_PARTY_ID, partyID);

        // if any address line parameters are given, include them in the lookup
        addressList = customer.getAddressList();

        // if there is at least one Address object in the Vector
        if (addressList.size() >= 1)
        {

            addr = addressList.get(0);

            lines = addr.getLines(); // get Vector of address lines

            // if there is at least one address line in the Vector
            if (lines.size() >= 1)
            {

                line1 = lines.get(0);

                // if there is a second address line in the Vector
                if (lines.size() >= 2)
                {

                    line2 = lines.get(1);

                }
                if (lines.size() >= 3)
                {

                    line2 = lines.get(2);

                }

            }

            // get the other search parameters from the Address object
            city = addr.getCity();
            state = addr.getState();
            postalCode = addr.getPostalCode();
            postalCodeExt = addr.getPostalCodeExtension();

            // append the postal code extension to the postal code
            if (postalCodeExt != null && postalCodeExt.length() > 0)
                postalCode = postalCode.concat("-" + postalCodeExt);

            // if there was a search parameter for Address Line 1
            if (line1 != null && line1.length() > 0)
            {
                sql.addQualifier(createAddressLineQualifier(FIELD_CONTACT_ADDRESS_LINE_1, line1));
            }

            // if there was a search parameter for Address Line 2
            if (line2 != null && line2.length() > 0)
            {
                sql.addQualifier(createAddressLineQualifier(FIELD_CONTACT_ADDRESS_LINE_2, line2));
            }

            // if there was a search parameter for Address Line 2
            if (line3.length() > 0)
            {
                sql.addQualifier(createAddressLineQualifier(FIELD_CONTACT_ADDRESS_LINE_3, line3));
            }
            // if there was a search parameter for city
            if (city != null && city.length() > 0)
            {
                StringBuilder qualifier = new StringBuilder(FIELD_CONTACT_CITY);
                qualifier.append(" like ");
                qualifier.append(makeSafeString(city + "%"));
                sql.addQualifier(qualifier.toString());
            }

            // if there was a search parameter for state
            if (state != null && state.length() > 0)
            {
                StringBuilder qualifier = new StringBuilder(FIELD_CONTACT_STATE);
                qualifier.append(" like ");
                qualifier.append(makeSafeString(state + "%"));
                sql.addQualifier(qualifier.toString());
            }

            // if there was a search parameter for postal code
            if (postalCode != null && postalCode.length() > 0)
            {
                StringBuilder qualifier = new StringBuilder(FIELD_CONTACT_POSTAL_CODE);
                qualifier.append(" like ");
                qualifier.append(makeSafeString(postalCode + "%"));
                sql.addQualifier(qualifier.toString());
            }

        }

        return sql.getSQLString();
    }

    /**
     * Returns the formatted qualifier string for the given column and value. 
     * @param column
     * @param sql
     */
    protected static String createAddressLineQualifier(String column, String value)
    {
        StringBuilder qualifier = new StringBuilder("UPPER(");
        qualifier.append(column);
        qualifier.append(") like UPPER(");
        qualifier.append(makeSafeString(value + "%"));
        qualifier.append(")");
        return qualifier.toString();
    }

    /**
     * Format the SQL to select from the Email Address table.
     * 
     * @param partyID The unique identifier for the Customer in the database
     * @return The SQL String
     * @throws SQLException
     */
    public static String selectEmailInfoSQL(int partyID) throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_EMAIL_ADDRESS);

        sql.addColumn(FIELD_EMAIL_ADDRESS);
        sql.addColumn(FIELD_EMAIL_ADDRESS_TYPE_CODE);

        sql.addQualifier(FIELD_PARTY_ID, String.valueOf(partyID));

        return sql.getSQLString();
    }

    /**
     * Read the data from the ResultSet following execution of the SQL generated
     * by selectAddressInfoSQL and backfill the Customer with the data.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI>rs is a valid ResultSet with results pending from the SQL
     * selectAddressInfoSQL
     * <LI>customer is not null
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI>The customer is backfilled with the read data
     * </UL>
     * 
     * @param rs The ResultSet to read
     * @param customer The Customer to populate with the data
     * @exception SQLException when an error occurs processing the ResultSet
     */
    public static void readEmailResultsForCustomer(ResultSet rs, CustomerIfc customer) throws SQLException
    {
        ArrayList<EmailAddressIfc> emailAddresses = new ArrayList<EmailAddressIfc>();

        EmailAddressIfc email = null;

        // Set the fields in the domain.

        while (rs.next())
        {
            if (logger.isInfoEnabled())
                logger.info("Reading address");
            email = DomainGateway.getFactory().getEmailAddressInstance();
            int index = 0;

            email.setEmailAddress(JdbcDataOperation.getSafeString(rs, ++index));
            email.setEmailAddressType(Integer.parseInt(JdbcDataOperation.getSafeString(rs, ++index)));
            if (!Util.isEmpty(email.getEmailAddress()))
            {
                emailAddresses.add(email);
            }
        }

        customer.setEmailAddresses(emailAddresses);
    }

    /**
     * Read the data from the ResultSet following execution of the SQL generated
     * by selectAddressInfoSQL and backfill the Customer with the data.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI>rs is a valid ResultSet with results pending from the SQL
     * selectAddressInfoSQL
     * <LI>customer is not null
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI>The customer is backfilled with the read data
     * </UL>
     * 
     * @param rs The ResultSet to read
     * @param customer The Customer to populate with the data
     * @exception SQLException when an error occurs processing the ResultSet
     */
    public static void readAddressResultsForCustomer(ResultSet rs, CustomerIfc customer) throws SQLException
    {
        List<AddressIfc> customerAddresses = new ArrayList<AddressIfc>();
        Vector<String> addressLines = null;
        AddressIfc address = null;

        String postalCode = null;
        String postalCodeExt = new String("");

        // Set the fields in the domain.

        while (rs.next())
        {
            if (logger.isInfoEnabled())
            {
                logger.info("Reading address");
            }
            address = DomainGateway.getFactory().getAddressInstance();
            addressLines = new Vector<String>();

            address.setAddressType(Integer.parseInt(JdbcDataOperation.getSafeString(rs, 1)));

            addressLines.addElement(JdbcDataOperation.getSafeString(rs, 2));
            addressLines.addElement(JdbcDataOperation.getSafeString(rs, 3));
            addressLines.addElement(JdbcDataOperation.getSafeString(rs, 4));
            address.setLines(addressLines);

            address.setCity(JdbcDataOperation.getSafeString(rs, 5));
            address.setState(JdbcDataOperation.getSafeString(rs, 6));

            // Zip codes are saved in the form nnnnn-xxxx if an extension is
            // known, otherwise it's in the form nnnnn
            postalCode = JdbcDataOperation.getSafeString(rs, 7);

            address.setPostalCode(postalCode);
            address.setPostalCodeExtension(postalCodeExt);
            address.setCountry(JdbcDataOperation.getSafeString(rs, 8));

            customerAddresses.add(address);
        }

        customer.setAddressList(customerAddresses);
    }

    /**
     * Format the SQL to select from the Phone table.
     * 
     * @param partyId the party id key value
     * @return The SQL String
     */
    public static String selectPhoneInfoSQL(int partyId)
    {

        String sqlString = "\n" + "select \n" + "ty_phn, tl_cnct, ext_cnct \n" + "from pa_phn \n" + "where \n"
                + "id_prty = " + partyId + " order by ty_phn \n";
        return sqlString;
    }

    /**
     * Read the data from the ResultSet following execution of the SQL generated
     * by selectPhoneInfoSQL and backfill the Customer with the data.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI>rs is a valid ResultSet with results pending from the SQL
     * selectPhoneInfoSQL
     * <LI>customer is not null
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI>The customer is backfilled with the read data
     * </UL>
     * 
     * @param rs The ResultSet to read
     * @param customer The Customer to populate with the data
     * @exception SQLException when an error occurs processing the ResultSet
     */
    public static void readPhoneResultsForCustomer(ResultSet rs, CustomerIfc customer) throws SQLException
    {
        List<PhoneIfc> customerPhones = new ArrayList<PhoneIfc>();
        PhoneIfc phone = null;

        while (rs.next())
        {

            // Set the fields in the domain. For each string field, replace
            // a null reference with an empty String. This makes the Customer
            // attributes display properly.

            phone = DomainGateway.getFactory().getPhoneInstance();
            phone.setPhoneType(Integer.parseInt(JdbcDataOperation.getSafeString(rs, 1)));

            phone.setPhoneNumber(JdbcDataOperation.getSafeString(rs, 2));

            phone.setExtension(JdbcDataOperation.getSafeString(rs, 3));
            if (customer.getAddressList() != null && customer.getAddressList().size() > 0
                    && customer.getAddressList().get(0) != null && customer.getAddressList().get(0).getCountry() != null)
            {
                phone.setCountry(customer.getAddressList().get(0).getCountry());
            }

            customerPhones.add(phone);
        }

        customer.setPhoneList(customerPhones);
    }

    /**
     * Builds SQL string to read customer name based on customer ID. To do this,
     * we must join from customer table PA_CT to contact table PA_CNCT using
     * party ID.
     * 
     * @param customerID customer identifier
     * @return SQLSelectStatement object
     */
    public static SQLSelectStatement buildReadCustomerNameSQL(String customerID)
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // add tables
        sql.addTable(TABLE_CUSTOMER, ALIAS_CUSTOMER);
        sql.addTable(TABLE_CONTACT, ALIAS_CONTACT);

        // add columns
        sql.addColumn(FIELD_CONTACT_LAST_NAME);
        sql.addColumn(FIELD_CONTACT_FIRST_NAME);
        sql.addColumn(FIELD_CONTACT_MIDDLE_INITIAL);
        sql.addColumn(FIELD_CONTACT_FULL_NAME);
        sql.addColumn(FIELD_CONTACT_SALUTATION);
        sql.addColumn(FIELD_CONTACT_SUFFIX);

        // add join qualifer
        sql.addQualifier(ALIAS_CUSTOMER + "." + FIELD_PARTY_ID + " = " + ALIAS_CONTACT + "." + FIELD_PARTY_ID);
        sql.addQualifier(FIELD_CONTACT_TYPE_CODE + " = '" + CUSTOMER_CONTACT_TYPE + "'");

        // add key
        sql.addQualifier(FIELD_CUSTOMER_ID + " = '" + customerID + "'");

        return (sql);
    }

    /**
     * Pulls PersonNameIfc object from result set. If result set is empty, null
     * object is returned.
     * 
     * @param rs ResultSet
     * @return PersonNameIfc object
     * @exception SQLException if error pulling from result set
     */
    public static PersonNameIfc readCustomerNameResults(ResultSet rs) throws SQLException
    {
        PersonNameIfc personName = null;

        if (rs.next())
        {
            personName = instantiatePersonNameIfc();
            personName.setLastName(JdbcDataOperation.getSafeString(rs, 1));
            personName.setFirstName(JdbcDataOperation.getSafeString(rs, 2));
            personName.setMiddleName(JdbcDataOperation.getSafeString(rs, 3));
            personName.setFullName(JdbcDataOperation.getSafeString(rs, 4));
            personName.setSalutation(JdbcDataOperation.getSafeString(rs, 5));
            personName.setNameSuffix(JdbcDataOperation.getSafeString(rs, 6));

        }
        rs.close();

        return (personName);
    }

    /**
     * Builds SQL for reading group data for customer.
     * 
     * @param customerID customer identifier
     * @return SQLSelectStatement to be used to read customer group
     */
    public static SQLSelectStatement buildCustomerGroupSQL(String customerID, LocaleRequestor locale)
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_CUSTOMER_GROUP, ALIAS_CUSTOMER_GROUP);
        sql.addTable(TABLE_CUSTOMER_GROUP_I8, ALIAS_CUSTOMER_GROUP_I8);
        sql.addTable(TABLE_CUSTOMER_AFFILIATION, ALIAS_CUSTOMER_AFFILIATION);
        // add columns
        sql.addColumn(ALIAS_CUSTOMER_GROUP, FIELD_CUSTOMER_GROUP_ID);
        sql.addColumn(ALIAS_CUSTOMER_GROUP_I8, FIELD_LOCALE);
        sql.addColumn(ALIAS_CUSTOMER_GROUP_I8, FIELD_CUSTOMER_GROUP_NAME);
        sql.addColumn(ALIAS_CUSTOMER_GROUP_I8, FIELD_CUSTOMER_GROUP_DESCRIPTION);
        // add qualifiers
        sql.addQualifier(ALIAS_CUSTOMER_AFFILIATION, FIELD_CUSTOMER_ID, JdbcDataOperation.makeSafeString(customerID));
        // add locale qualifier
        Set<Locale> bestMatches = LocaleMap.getBestMatch("", locale.getLocales());
        sql.addQualifier(ALIAS_CUSTOMER_GROUP_I8 + "." + FIELD_LOCALE + " "
                + JdbcDataOperation.buildINClauseString(bestMatches));

        sql.addJoinQualifier(ALIAS_CUSTOMER_GROUP, FIELD_CUSTOMER_GROUP_ID, ALIAS_CUSTOMER_AFFILIATION,
                FIELD_CUSTOMER_GROUP_ID);
        sql.addJoinQualifier(ALIAS_CUSTOMER_GROUP, FIELD_CUSTOMER_GROUP_ID, ALIAS_CUSTOMER_GROUP_I8,
                FIELD_CUSTOMER_GROUP_ID);
        return (sql);
    }

    /**
     * Reads customer group result set and converts to array of CustomerGroupIfc
     * objects. If result set is empty, NO_DATA exception is not thrown. Rather,
     * customer group reference is null.
     * 
     * @param rs ResultSet
     * @return array of discount rules
     * @exception SQLException is thrown if an error occurs parsing result set
     */
    public static CustomerGroupIfc[] readCustomerGroupResultSet(ResultSet rs) throws SQLException
    {
        Vector<CustomerGroupIfc> groupVector = new Vector<CustomerGroupIfc>();
        CustomerGroupIfc group = null;
        String groupId = null;
        Locale locale = null;
        String localeString = null;

        while (rs.next())
        {
            int index = 0;
            groupId = JdbcDataOperation.getSafeString(rs, ++index);
            group = getGroup(groupId, groupVector);
            if (group == null)
            {
                group = instantiateCustomerGroupIfc();
                group.setGroupID(groupId);
                groupVector.addElement(group);
            }

            localeString = JdbcDataOperation.getSafeString(rs, ++index);
            locale = LocaleUtilities.getLocaleFromString(localeString);
            group.setName(locale, JdbcDataOperation.getSafeString(rs, ++index));
            group.setDescription(locale, JdbcDataOperation.getSafeString(rs, ++index));

        }

        CustomerGroupIfc[] groups = null;
        // build array of groups
        if (groupVector.size() > 0)
        {
            groups = new CustomerGroupIfc[groupVector.size()];
            groupVector.copyInto(groups);
        }
        return (groups);
    }

    /**
     * Builds SQL for reading discount data for group.
     * 
     * @param groupID group identifier
     * @return SQLSelectStatement to be used to read customer group and discount
     *         data
     */
    public static SQLSelectStatement buildCustomerDiscountSQL(String groupID)
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_PRICE_DERIVATION_RULE, ALIAS_PRICE_DERIVATION_RULE);
        sql.addTable(TABLE_ITEM_PRICE_DERIVATION, ALIAS_ITEM_PRICE_DERIVATION);
        sql.addTable(TABLE_CUSTOMER_AFFILIATION_PRICE_DERIVATION_RULE_ELIGIBILITY,
                ALIAS_CUSTOMER_AFFILIATION_PRICE_DERIVATION_RULE_ELIGIBILITY);
        sql.addTable(TABLE_TIME_DATE_PRICE_DERIVATION_RULE_ELIGIBILITY,
                ALIAS_TIME_DATE_PRICE_DERIVATION_RULE_ELIGIBILITY);
        // add columns
        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_TRANSACTION_CONTROL_BREAK_CODE);
        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_STATUS_CODE);
        sql.addColumn(ALIAS_TIME_DATE_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_PRICE_DERIVATION_RULE_EFFECTIVE_DATE);
        sql.addColumn(ALIAS_TIME_DATE_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_PRICE_DERIVATION_RULE_EXPIRATION_DATE);
        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_DESCRIPTION);

        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_ID);
        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_REASON_CODE);
        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_INCLUDED_IN_BEST_DEAL_FLAG);
        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_SCOPE_CODE);
        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_METHOD_CODE);
        sql.addColumn(ALIAS_ITEM_PRICE_DERIVATION, FIELD_ITEM_PRICE_DERIVATION_SALE_UNIT_MONETARY_AMOUNT);
        sql.addColumn(ALIAS_ITEM_PRICE_DERIVATION, FIELD_ITEM_PRICE_DERIVATION_SALE_UNIT_PERCENT);
        // add qualifiers
        sql.addQualifier(ALIAS_CUSTOMER_AFFILIATION_PRICE_DERIVATION_RULE_ELIGIBILITY + "." + FIELD_CUSTOMER_GROUP_ID
                + " = " + groupID);
        sql.addJoinQualifier(ALIAS_TIME_DATE_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_PRICE_DERIVATION_RULE_ID,
                ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_ID);
        sql.addJoinQualifier(ALIAS_CUSTOMER_AFFILIATION_PRICE_DERIVATION_RULE_ELIGIBILITY,
                FIELD_PRICE_DERIVATION_RULE_ID, ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_ID);
        sql.addJoinQualifier(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_ID, ALIAS_ITEM_PRICE_DERIVATION,
                FIELD_PRICE_DERIVATION_RULE_ID);
        sql.addQualifier(JdbcDataOperation.currentTimestampRangeCheckingString(
                ALIAS_TIME_DATE_PRICE_DERIVATION_RULE_ELIGIBILITY + "." + FIELD_PRICE_DERIVATION_RULE_EFFECTIVE_DATE,
                ALIAS_TIME_DATE_PRICE_DERIVATION_RULE_ELIGIBILITY + "." + FIELD_PRICE_DERIVATION_RULE_EXPIRATION_DATE));
        // limit to customer triggered
        sql.addQualifier(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE + " = " + ASSIGNMENT_CUSTOMER);
        return (sql);
    }

    /**
     * Reads discount rule result set and converts to array of DiscountRuleIfc
     * objects. If result set is empty, NO_DATA exception is not thrown. Rather,
     * null is returned.
     * 
     * @param rs ResultSet
     * @return array of discount rules
     * @exception SQLException is thrown if an error occurs parsing result set
     */
    public static DiscountRuleIfc[] readCustomerDiscountResultSet(ResultSet rs) throws SQLException
    {
        Vector<DiscountRuleIfc> discountsVector = new Vector<DiscountRuleIfc>();
        DiscountRuleIfc rule = null;
        int recordsRead = 0;
        int index = 0;
        String appliedWhen = null;
        String status = null;
        String includedInBestDealFlag = null;

        while (rs.next())
        {
            index = 0;
            rule = instantiateDiscountRuleIfc();
            appliedWhen = JdbcDataOperation.getSafeString(rs, ++index);
            status = JdbcDataOperation.getSafeString(rs, ++index);
            rule.setEffectiveDate(JdbcDataOperation.timestampToEYSDate(rs, ++index));
            rule.setEffectiveTime(new EYSTime(rule.getEffectiveDate()));
            rule.setExpirationDate(JdbcDataOperation.timestampToEYSDate(rs, ++index));
            rule.setExpirationTime(new EYSTime(rule.getExpirationDate()));
            rule.setDescription(JdbcDataOperation.getSafeString(rs, ++index));
            rule.setRuleID(JdbcDataOperation.getSafeString(rs, ++index));

            int reasonCode = rs.getInt(++index);

            String reasonCodeString = CodeConstantsIfc.CODE_UNDEFINED;
            try
            {
                reasonCodeString = Integer.toString(reasonCode);
            }
            catch (Exception e)
            {
                // do nothing, use CODE_UNDEFINED
            }
            rule.getReason().setCode(reasonCodeString);

            includedInBestDealFlag = JdbcDataOperation.getSafeString(rs, ++index);
            Float discountScopeF = new Float(rs.getFloat(++index));
            int discountScopeI = discountScopeF.intValue();
            rule.setDiscountScope(discountScopeI);
            Float discountMethodF = new Float(rs.getFloat(++index));
            int discountMethodI = discountMethodF.intValue();
            rule.setDiscountMethod(discountMethodI);
            rule.setDiscountAmount(JdbcDataOperation.getCurrencyFromDecimal(rs, ++index));
            rule.setDiscountRate(JdbcDataOperation.getPercentage(rs, ++index));

            rule.setDiscountScope(DISCOUNT_SCOPE_TRANSACTION);
            rule.setAssignmentBasis(ASSIGNMENT_CUSTOMER);
            // translate values
            setDiscountRuleValues(rule, appliedWhen, status, includedInBestDealFlag);
            discountsVector.addElement(rule);
            recordsRead++;
        }

        DiscountRuleIfc[] discounts = null;
        // build array of discounts
        if (recordsRead > 0)
        {
            discounts = new DiscountRuleIfc[recordsRead];
            discountsVector.copyInto(discounts);
        }
        return (discounts);
    }

    /**
     * Builds the SQL for reading the localization information for the rule
     * 
     * @param ruleId
     * @param locale
     * @return
     */
    public static SQLSelectStatement buildLocalizedRuleSQL(String ruleId, LocaleRequestor locale)
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_PRICE_DERIVATION_RULE, ALIAS_PRICE_DERIVATION_RULE);
        sql.addTable(TABLE_PRICE_DERIVATION_RULE_I8, ALIAS_PRICE_DERIVATION_RULE_I8);

        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE_I8, FIELD_LOCALE);
        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE_I8, FIELD_PRICE_DERIVATION_RULE_NAME);

        sql.addQualifier(ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_PRICE_DERIVATION_RULE_ID + " = " + ruleId);

        Set<Locale> bestMatches = LocaleMap.getBestMatch("", locale.getLocales());
        sql.addQualifier(ALIAS_PRICE_DERIVATION_RULE_I8 + "." + FIELD_LOCALE + " "
                + JdbcDataOperation.buildINClauseString(bestMatches));

        sql.addJoinQualifier(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_ID, ALIAS_PRICE_DERIVATION_RULE,
                FIELD_PRICE_DERIVATION_RULE_ID);

        return sql;

    }

    /**
     * Reads the result set and populates the localized name for the discount
     * rule
     * 
     * @param dc
     * @param rule
     * @return
     * @throws DataException
     * @throws SQLException
     */
    public static DiscountRuleIfc readLocalizedRule(ResultSet rs, DiscountRuleIfc rule) throws DataException,
            SQLException
    {

        while (rs.next())
        {
            int index = 0;
            String localeString = JdbcDataOperation.getSafeString(rs, ++index);
            String localizedName = JdbcDataOperation.getSafeString(rs, ++index);
            Locale lcl = LocaleUtilities.getLocaleFromString(localeString);
            rule.setName(lcl, localizedName);
        }

        return rule;
    }

    /**
     * Sets values in discount rule object.
     * 
     * @param rule DiscountRuleIfc object already created
     * @param appliedWhen string value of applied when attribute
     * @param status string value of status
     * @param includedInBestDealFlag string value of Included In Best Deal flag
     */
    public static void setDiscountRuleValues(DiscountRuleIfc rule, String appliedWhen, String status,
            String includedInBestDealFlag)
    {
        // set applied when value
        if (appliedWhen.equals("DT"))
        {
            rule.setAppliedWhen(APPLIED_DETAIL);
        }
        else if (appliedWhen.equals("MT"))
        {
            rule.setAppliedWhen(APPLIED_MERCHANDISE_SUBTOTAL);
        }
        else
        {
            rule.setAppliedWhen(APPLIED_UNDEFINED);
        }

        // set status
        rule.setStatus(STATUS_PENDING);
        for (int i = 0; i < STATUS_DESCRIPTORS.length; i++)
        {
            if (status.equals(STATUS_DESCRIPTORS[i]))
            {
                rule.setStatus(i);
                i = STATUS_DESCRIPTORS.length;
            }
        }

        if (includedInBestDealFlag.equals("1"))
        {
            rule.setIncludedInBestDeal(true);
        }
        else
        {
            rule.setIncludedInBestDeal(false);
        }

    }

    /**
     * @param groupId
     * @param groups
     * @return
     */
    public static CustomerGroupIfc getGroup(String groupId, Collection<CustomerGroupIfc> groups)
    {
        CustomerGroupIfc newGroup = null;

        for (CustomerGroupIfc group : groups)
        {
            if (group.getGroupID().equals(groupId))
            {
                newGroup = group;
                break;
            }

        }
        return newGroup;
    }

    /**
     * @param groupId
     * @param groups
     * @return
     */
    public static PricingGroupIfc getPricingGroup(Collection<PricingGroupIfc> groups, int pricingGroupID)
    {
        PricingGroupIfc newGroup = null;
        for (PricingGroupIfc group : groups)
        {
            if (group.getPricingGroupID() == pricingGroupID)
            {
                newGroup = group;
                break;
            }

        }
        return newGroup;
    }

    /**
     * Instantiates a PersonName object.
     * 
     * @return new PersonName object
     */
    public static PersonNameIfc instantiatePersonNameIfc()
    {
        return (DomainGateway.getFactory().getPersonNameInstance());
    }

    /**
     * Instantiates a DiscountRule object.
     * 
     * @return new DiscountRule object
     */
    public static DiscountRuleIfc instantiateDiscountRuleIfc()
    {
        return (DomainGateway.getFactory().getDiscountRuleInstance());
    }

    /**
     * Instantiates a CustomerGroup object.
     * 
     * @return new CustomerGroup object
     */
    public static CustomerGroupIfc instantiateCustomerGroupIfc()
    {
        return (DomainGateway.getFactory().getCustomerGroupInstance());
    }

    /**
     * Builds SQL string to read customer business info based on customer ID. To
     * do this, we must join from customer table PA_CT to business customer
     * table ORGN_CT using party ID.
     * 
     * @param partyID party identifier
     * @return SQLSelectStatement object
     */
    public static SQLSelectStatement buildBusinessInfoSQL(int partyID)
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_BUS_CUSTOMER, ALIAS_BUS_CUSTOMER);

        // add columns
        sql.addColumn(FIELD_BUS_CUSTOMER_NAME);
        sql.addColumn(FIELD_MSK_TAX_EXEMPT_CERTIFICATE);
        sql.addColumn(FIELD_EXEMPTION_REASON);

        // add qualifier
        sql.addQualifier(FIELD_PARTY_ID + " = " + partyID);

        return (sql);
    }

    /**
     * Read the data from the ResultSet following execution of the SQL generated
     * by buildBusinessInfoSQL and backfill the Customer with the data.
     * 
     * @param rs The ResultSet to read
     * @param retrievedCustomer The Customer to populate with the data
     * @param localeRequestor
     * @exception SQLException when an error occurs processing the ResultSet
     */
    public static void readBusinessInfoResultsForCustomer(ResultSet rs, CustomerIfc retrievedCustomer,
            DataConnectionIfc dataConnection, LocaleRequestor localeRequestor) throws SQLException
    {
        while (rs.next())
        {
            retrievedCustomer.setCustomerName(JdbcDataOperation.getSafeString(rs, 1));
            
            String taxCertificate =  JdbcDataOperation.getSafeString(rs, 2);
            if (!Util.isEmpty(taxCertificate))
            {
                EncipheredDataIfc encipheredTaxCertificate = FoundationObjectFactory.getFactory().createEncipheredDataInstance(taxCertificate);
                retrievedCustomer.setEncipheredTaxCertificate(encipheredTaxCertificate);
            }
            
            String reason = JdbcDataOperation.getSafeString(rs, 3);
            String storeID = retrievedCustomer.getCustomerIDPrefix();
            retrievedCustomer.setTaxExemptionReason(getLocalizedReasonCode((JdbcDataConnection)dataConnection, storeID,
                    reason, CodeConstantsIfc.CODE_LIST_TAX_EXEMPT_REASON_CODES, localeRequestor));
            retrievedCustomer.setBusinessCustomer(true);
        }
    }

    /**
     * Invokes the utility makeSafeString() method.
     * 
     * @param value the string to make safe
     * @return a database safe string
     */
    public static String makeSafeString(String value)
    {

        return JdbcUtilities.makeSafeString(value);

    }

    /**
     * Method to default display string function.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // build result string
        StringBuffer strResult = new StringBuffer("Class:  ReadARTSCustomerSQL ");
        strResult.append(hashCode());
        // pass back result
        return (strResult.toString());
    }

    /**
     * Gets the localized reason code for a transaction
     * 
     * @param connection
     * @param storeId
     * @param reasonCode
     * @param codeListType
     * @param locale
     * @return LocalizedCodeIfc
     */
    protected static LocalizedCodeIfc getLocalizedReasonCode(JdbcDataConnection connection, String storeId,
            String reasonCode, String codeListType, LocaleRequestor locale)
    {
        LocalizedCodeIfc localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();

        if (!reasonCode.equals(CodeConstantsIfc.CODE_UNDEFINED))
        {

            // Read Localized Reason Code
            CodeSearchCriteriaIfc criteria = DomainGateway.getFactory().getCodeSearchCriteriaInstance();
            criteria.setStoreID(storeId);
            criteria.setListID(codeListType);
            criteria.setLocaleRequestor(locale);
            criteria.setCode(reasonCode);
            try
            {
                localizedReasonCode = new JdbcReadCodeList().readCode(connection, criteria);
            }
            catch (DataException e)
            {
                logger.warn(
                        "An error occured retrieving the localized descriptions for reason code: " + criteria.getCode(),
                        e);
                localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();
                localizedReasonCode.setCode(criteria.getCode());
            }
        }
        else
        {
            localizedReasonCode.setCode(reasonCode);
        }
        return localizedReasonCode;
    }

}
