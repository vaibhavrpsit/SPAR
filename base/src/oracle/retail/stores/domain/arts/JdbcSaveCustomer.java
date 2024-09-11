/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveCustomer.java /main/35 2014/01/09 16:23:23 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    mjwall 01/09/14 - fix null dereferences
 *    mchell 11/23/12 - Updates for receipt preference
 *    cgreen 04/03/12 - removed deprecated methods
 *    rabhaw 09/12/12 - tax certificate should be encrypted in db.
 *    vtemke 03/29/12 - Merged conflicts
 *    vtemke 03/29/12 - Sensitive data from getDecryptedData() of
 *                      EncipheredData class fetched into byte array and later,
 *                      deleted
 *    asinto 03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                      List<AddressIfc>) and remove old deprecated methods and
 *                      references to them
 *    asinto 03/13/12 - removed references to customer hashed tax ID.
 *                      deprecated getHashedCustomerTaxID method.
 *    mkutia 03/06/12 - XbranchMerge
 *                      mkutiana_hpqc1463_13_4_1_taxcertificate_not_retrieved
 *                      from rgbustores_13.4x_generic_branch
 *    mkutia 03/05/12 - fixes to taxid and tax certificate saving -
 *                      encrypt/decrypt issues
 *    cgreen 09/02/11 - update deprecated tax methods
 *    cgreen 09/02/11 - refactored method names around enciphered objects
 *    cgreen 08/22/11 - removed deprecated methods
 *    mkutia 08/16/11 - Removed depricated hashAccount field EncipheredCardData
 *                      and related classes
 *    tkshar 07/29/11 - getAddressLin2(AddressIfc) changed to use
 *                      makeSafeString(value,boolean)
 *    rrkohl 07/25/11 - encryption cr issues fix
 *    masahu 07/07/11 - FORTIFY FIX: The sensitive SQLs get logged
 *    asinto 12/20/10 - XbranchMerge asinton_bug-10407292 from
 *                      rgbustores_13.3x_generic_branch
 *    asinto 12/17/10 - deprecated hashed account ID.
 *    npoola 08/25/10 - passed the connection object to the IdentifierService
 *                      getNextID method to use right connection
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/28/10 - updating deprecated names
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech75 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                      SQLException to DataException
 *    abonda 01/03/10 - update header date
 *    mahisi 01/20/09 - fix ejournal issue when only customer added
 *    mahisi 01/09/09 - fix QA issue
 *    mahisi 12/04/08 - JUnit fix and SQL fix
 *    mahisi 11/19/08 - Updated for review comments
 *    mahisi 11/18/08 - Update for Customer
 *    mahisi 11/14/08 - added for customer module
 *    mahisi 11/13/08 - Added for Customer module for both ORPOS and ORCO
 *    ranojh 11/05/08 - Fixed Tax Exempt Reason Code for Customer
 *    mkochu 11/04/08 - i18n changes for phone and postalcode fields
 *    acadar 10/23/08 - updates from code review
 * =========================================================================== |
 |   $Log:
 |    12   360Commerce 1.11        4/18/2008 11:03:20 AM  Charles D. Baker CR
 |         31202 - Corrected problem with making string safe when not null or
 |         empty.
 |    11   360Commerce 1.10        4/17/2008 10:56:49 AM  Charles D. Baker CR
 |         31202 - Updated to consistently store empty email addresses as Null
 |          between DB2 and Oracle DB. Also updated to avoid loading empty
 |         email addresses when working with a customer object. Code reviewed
 |         by Owen Horne.
 |    10   360Commerce 1.9         1/4/2008 4:46:41 PM    Alan N. Sinton  CR
 |         29849: Refactor of customer table to use encrypted, hashed and
 |         masked fields.
 |    9    360Commerce 1.8         12/18/2007 5:47:48 PM  Alan N. Sinton  CR
 |         29661: Changes per code review.
 |    8    360Commerce 1.7         11/29/2007 11:25:31 PM Robinson Joseph Fixed
 |          the code to update the customers first and last name in the
 |         appropriate table
 |    7    360Commerce 1.6         6/1/2006 12:28:42 PM   Brendan W. Farrell
 |         Update comments.
 |    6    360Commerce 1.5         5/31/2006 5:04:01 PM   Brendan W. Farrell
 |         Move from party to id gen.
 |
 |    5    360Commerce 1.4         1/25/2006 4:11:21 PM   Brett J. Larsen merge
 |          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 |    4    360Commerce 1.3         12/13/2005 4:43:45 PM  Barry A. Pape
 |         Base-lining of 7.1_LA
 |    3    360Commerce 1.2         3/31/2005 4:28:43 PM   Robert Pearse
 |    2    360Commerce 1.1         3/10/2005 10:22:48 AM  Robert Pearse
 |    1    360Commerce 1.0         2/11/2005 12:12:01 PM  Robert Pearse
 |   $: JdbcSaveCustomer.java,v $
 |    6    .v710     1.2.2.0     9/21/2005 13:39:47     Brendan W. Farrell
 |         Initial Check in merge 67.
 |    5    .v700     1.2.3.1     12/23/2005 17:18:10    Rohit Sachdeva
 |         8203:Null Pointer Fix for Business Customer Info
 |    4    .v700     1.2.3.0     11/16/2005 16:26:11    Jason L. DeLeau 4215:
 |         Get rid of redundant ArtsDatabaseifc class
 |    3    360Commerce1.2         3/31/2005 15:28:43     Robert Pearse
 |    2    360Commerce1.1         3/10/2005 10:22:48     Robert Pearse
 |    1    360Commerce1.0         2/11/2005 12:12:01     Robert Pearse
 |   $
 |   Revision 1.6  2004/04/09 16:55:46  cdb
 |   @scr 4302 Removed double semicolon warnings.
 |
 |   Revision 1.5  2004/02/17 17:57:36  bwf
 |   @scr 0 Organize imports.
 |
 |   Revision 1.4  2004/02/17 16:18:45  rhafernik
 |   @scr 0 log4j conversion
 |
 |   Revision 1.3  2004/02/12 17:13:18  mcs
 |   Forcing head revision
 |
 |   Revision 1.2  2004/02/11 23:25:23  bwf
 |   @scr 0 Organize imports.
 |
 |   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 |   updating to pvcs 360store-current
 |
 |
 |
 |    Rev 1.0   Aug 29 2003 15:32:40   CSchellenger
 | Initial revision.
 |
 |    Rev 1.7   16 Jul 2003 21:46:52   baa
 | prevent null exemptions if cutomer phone list is not fully populated
 |
 |    Rev 1.6   May 11 2003 22:41:36   baa
 | fix business customer
 |
 |    Rev 1.5   Apr 28 2003 08:49:04   baa
 | fix business customer
 | Resolution for POS SCR-2217: System crashes if new business customer is created and Return is selected
 |
 |    Rev 1.4   Mar 24 2003 10:05:26   baa
 | remove reference to foundation's  .util.EMPTY_STRING
 | Resolution for POS SCR-2101: Remove uses of  foundation constant  EMPTY_STRING
 |
 |    Rev 1.3   Mar 20 2003 18:12:22   baa
 | Refactoring of customer screens
 | Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 |
 |    Rev 1.2   Mar 07 2003 17:04:56   baa
 | code review changes for I18n
 | Resolution for POS SCR-1740: Code base Conversions
 |
 |    Rev 1.1   03 Sep 2002 14:12:52   djefferson
 | added support for Business Customer
 | Resolution for POS SCR-1605: Business Customer
 |
 |    Rev 1.0   Jun 03 2002 16:39:28   msg
 | Initial revision.
 |
 |    Rev 1.2   May 12 2002 23:20:16   mhr
 | db2 quoting fix
 | Resolution for Domain SCR-50: db2 port fixes
 |
 |    Rev 1.1   Mar 18 2002 22:48:10   msg
 | - updated copyright
 |
 |    Rev 1.0   Mar 18 2002 12:08:04   msg
 | Initial revision.
 |
 |    Rev 1.1   07 Jan 2002 11:49:44   jbp
 | updates for modification of email address.
 | Resolution for POS SCR-544: Email Address Updates
 |
 |    Rev 1.0   Sep 20 2001 15:59:20   msg
 | Initial revision.
 |
 |    Rev 1.1   Sep 17 2001 12:34:08   msg
 | header update
 ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.identifier.IdentifierConstantsIfc;
import oracle.retail.stores.common.identifier.IdentifierServiceIfc;
import oracle.retail.stores.common.identifier.IdentifierServiceLocator;
import oracle.retail.stores.common.sql.SQLDeleteStatement;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.EmailAddressIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This operation takes a POS domain Customer and creates a new entry in the
 * database.
 * 
 * @version $Revision: /main/35 $
 */
public class JdbcSaveCustomer extends JdbcDataOperation implements ARTSDatabaseIfc, CodeConstantsIfc
{
    private static final long serialVersionUID = 491296793326204649L;

    /** The logger to which log messages will be sent. */
    private static final Logger logger = Logger.getLogger(JdbcSaveCustomer.class);

    public final int ID_CT_BATCH_DEFAULT = -1;

    /**
     * Class constructor.
     */
    public JdbcSaveCustomer()
    {
        setName("JdbcSaveCustomer");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    @Override
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveCustomer.execute");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        ARTSCustomer artsCustomer = (ARTSCustomer)action.getDataObject();

        saveCustomer(connection, artsCustomer);
        dataTransaction.setResult(artsCustomer.getPosCustomer());
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveCustomer.execute");
    }

    /**
     * Saves the customer information to the database.
     * <p>
     *
     * @param dataConnection The connection to the data source
     * @param artsCustomer The customer information to save
     * @exception DataException upon error
     */
    public void saveCustomer(JdbcDataConnection dataConnection, ARTSCustomer artsCustomer) throws DataException
    {
        try
        {
            int partyID = getPartyIDFromCustomer(dataConnection, artsCustomer);
            artsCustomer.setPartyId(partyID);

            if (partyID == 0)
            {
                artsCustomer.setPartyId(generatePartyID(dataConnection));
                // get customer id
                artsCustomer.getPosCustomer().setCustomerID(artsCustomer.getPosCustomer().getCustomerID());

                insertParty(dataConnection, artsCustomer);
                insertCustomer(dataConnection, artsCustomer);
                insertContact(dataConnection, artsCustomer);
                insertAddress(dataConnection, artsCustomer);
                insertEmailAddress(dataConnection, artsCustomer);
                insertPhone(dataConnection, artsCustomer);
                insertGroups(dataConnection, artsCustomer);
                insertBusinessInfo(dataConnection, artsCustomer);
            }
            else
            {
                updateCustomer(dataConnection, artsCustomer);
                updateContact(dataConnection, artsCustomer);
                updateAddress(dataConnection, artsCustomer);
                updateEmailAddress(dataConnection, artsCustomer);
                updatePhone(dataConnection, artsCustomer);
                updateGroups(dataConnection, artsCustomer);
                updateBusinessInfo(dataConnection, artsCustomer);
            }
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, e.toString());
        }
    }

    /**
     * Generates a unique party id. Uses {@link IdentifierServiceIfc}.
     * 
     * @param dataConnection The connection to the data source
     * @return the party_id
     * @exception DataException upon error
     */
    protected int generatePartyID(JdbcDataConnection dataConnection) throws DataException
    {
        return IdentifierServiceLocator.getIdentifierService().getNextID(dataConnection.getConnection(), IdentifierConstantsIfc.COUNTER_PARTY);
    }

    /**
     * Inserts into the party table.
     * <P>
     * 
     * @param dataConnection The connection to the data source
     * @param artsCustomer The customer information to save
     * @exception DataException upon error
     */
    protected void insertParty(JdbcDataConnection dataConnection,
                               ARTSCustomer artsCustomer)
        throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_PARTY);

        // Fields
        sql.addColumn(FIELD_PARTY_ID, getPartyID(artsCustomer));
        sql.addColumn(FIELD_PARTY_TYPE_CODE, getPartyType(CUSTOMER_PARTY_TYPE));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "insertParty", e);
        }
    }

    /**
     * Inserts into the customer table.
     * <P>
     * 
     * @param dataConnection The connection to the data source
     * @param artsCustomer The customer information to save
     * @exception DataException upon error
     */
    protected void insertCustomer(JdbcDataConnection dataConnection,
                                  ARTSCustomer artsCustomer)
        throws DataException
    {
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
        sql.addColumn(FIELD_MASKED_HOUSE_ACCOUNT_NUM, getMaskedHouseAccountNumber(artsCustomer));
        if (getCustomerLocale(artsCustomer) != null)
        {
          sql.addColumn(FIELD_LOCALE, getCustomerLocale(artsCustomer));
        }
        sql.addColumn(FIELD_ENCRYPTED_CUSTOMER_TAX_ID, getEncryptedCustomerTaxID(artsCustomer));
        sql.addColumn(FIELD_MASKED_CUSTOMER_TAX_ID, getMaskedCustomerTaxID(artsCustomer));
        // if pricing group selected is not None
        if (artsCustomer.getPosCustomer().getPricingGroupID() != null
                && artsCustomer.getPosCustomer().getPricingGroupID() > 0)
        {
            sql.addColumn(FIELD_CUSTOMER_PRICING_GROUP_ID, artsCustomer.getPosCustomer().getPricingGroupID());
        }
        else
        {
            sql.addColumn(FIELD_CUSTOMER_PRICING_GROUP_ID, null);
        }
        
        
        try
        {
            dataConnection.execute(sql.getSQLString(), false);
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "insertCustomer", e);
        }
    }

    /**
     * Inserts into the contact table.
     * <P>
     * 
     * @param dataConnection The connection to the data source
     * @param artsCustomer The customer information to save
     * @exception DataException upon error
     */
    protected void insertContact(JdbcDataConnection dataConnection,
                                 ARTSCustomer artsCustomer)
        throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_CONTACT);

        // Fields
        sql.addColumn(FIELD_CONTACT_ID, getContactID(artsCustomer));
        sql.addColumn(FIELD_CONTACT_TYPE_CODE, getContactType(CUSTOMER_CONTACT_TYPE));
        sql.addColumn(FIELD_PARTY_ID, getPartyID(artsCustomer));
        sql.addColumn(FIELD_CONTACT_LAST_NAME, getLastName(artsCustomer));
        sql.addColumn(FIELD_CONTACT_FIRST_NAME, getFirstName(artsCustomer));

        //same column is used to store full name form regular customers and
        sql.addColumn(FIELD_CONTACT_FULL_NAME,getCustomerName(artsCustomer));

        sql.addColumn(FIELD_CONTACT_MIDDLE_INITIAL, getMiddleName(artsCustomer));
        sql.addColumn(FIELD_CONTACT_SALUTATION, getSalutation(artsCustomer));
        sql.addColumn(FIELD_CONTACT_SUFFIX, getSuffix(artsCustomer));
        sql.addColumn(FIELD_CONTACT_BIRTH_DATE, getBirthDate(artsCustomer));
        sql.addColumn(FIELD_CONTACT_GENDER, getGender(artsCustomer));
        sql.addColumn(FIELD_CONTACT_COMPANY_NAME, getCompanyName(artsCustomer));
        sql.addColumn(FIELD_CONTACT_MAIL_FLAG, getMailPrivacy(artsCustomer));
        sql.addColumn(FIELD_CONTACT_PHONE_FLAG, getPhonePrivacy(artsCustomer));
        sql.addColumn(FIELD_CONTACT_EMAIL_FLAG, getEmailPrivacy(artsCustomer));
        sql.addColumn(FIELD_CONTACT_RECEIPT_PREFERENCE, artsCustomer.getPosCustomer().getReceiptPreference());

        try
        {
            dataConnection.execute(sql.getSQLString(), false);
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "insertContact", e);
        }
    }

    /**
     * Inserts all addresses into the address table.
     * 
     * @param dataConnection The data connection on which to execute.
     * @param artsCustomer Customer object with address information
     * @throws DataException thrown when an error occurs
     */
    protected void insertAddress(JdbcDataConnection dataConnection,
                                 ARTSCustomer artsCustomer)
        throws DataException
    {
        for (AddressIfc address : artsCustomer.getPosCustomer().getAddressList())
        {
            insertAddress(dataConnection, address, artsCustomer);
        }
    }

    /**
     * Inserts all email addresses into the email address table.
     * 
     * @param dataConnection The data connection on which to execute.
     * @param artsCustomer Customer object with email address information
     * @throws DataException thrown when an error occurs
     */
    protected void insertEmailAddress(JdbcDataConnection dataConnection,
                                 ARTSCustomer artsCustomer)
        throws DataException
    {
        Iterator<EmailAddressIfc> emailAddresses = artsCustomer.getPosCustomer().getEmailAddresses();

        while (emailAddresses.hasNext())
        {
            EmailAddressIfc emailAddress = emailAddresses.next();
            insertEmailAddress(dataConnection, emailAddress, artsCustomer);
        }
    }

    /**
     * Reads the party id from the customer table.
     * <P>
     * 
     * @param dataConnection The connection to the data source
     * @param artsCustomer The customer information
     * @return the party_id
     * @exception DataException upon error
     */
    protected int getPartyIDFromCustomer(JdbcDataConnection dataConnection,
                                         ARTSCustomer artsCustomer)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // Table
        sql.setTable(TABLE_CUSTOMER);

        // Fields
        sql.addColumn(FIELD_PARTY_ID);

        // Qualifiers
        sql.addQualifier(FIELD_CUSTOMER_ID + " = " + getCustomerID(artsCustomer));

        int partyID = 0;
        try
        {
            dataConnection.execute(sql.getSQLString(), false);
            ResultSet rs = (ResultSet)dataConnection.getResult();
            if (rs.next())
            {
                partyID = rs.getInt(1);
            }

            rs.close();
        }
        catch (SQLException e)
        {
            throw new DataException(DataException.SQL_ERROR, "Can't read customer table", e);
        }

        return(partyID);
    }

    /**
     * Updates the customer table.
     *
     * @param dataConnection The connection to the data source
     * @param artsCustomer The customer information to save
     * @exception DataException upon error
     */
    protected void updateCustomer(JdbcDataConnection dataConnection, ARTSCustomer artsCustomer) throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_CUSTOMER);

        // Fields
        sql.addColumn(FIELD_CUSTOMER_STATUS, getCustomerStatus(artsCustomer));
        sql.addColumn(FIELD_EMPLOYEE_ID, getEmployeeID(artsCustomer));
        sql.addColumn(FIELD_LOCALE, getCustomerLocale(artsCustomer));
        sql.addColumn(FIELD_CUSTOMER_NAME, getCustomerName(artsCustomer));
        sql.addColumn(FIELD_HOUSE_ACCOUNT_NUM, getHouseAccountNumber(artsCustomer));
        sql.addColumn(FIELD_MASKED_HOUSE_ACCOUNT_NUM, getMaskedHouseAccountNumber(artsCustomer));
        sql.addColumn(FIELD_ENCRYPTED_CUSTOMER_TAX_ID, getEncryptedCustomerTaxID(artsCustomer));
        sql.addColumn(FIELD_MASKED_CUSTOMER_TAX_ID, getMaskedCustomerTaxID(artsCustomer));
        // if pricing group selected is not None
        if (artsCustomer.getPosCustomer().getPricingGroupID() != null
                && artsCustomer.getPosCustomer().getPricingGroupID() > 0)
        {
            sql.addColumn(FIELD_CUSTOMER_PRICING_GROUP_ID, artsCustomer.getPosCustomer().getPricingGroupID());
        }
        else
        {
            sql.addColumn(FIELD_CUSTOMER_PRICING_GROUP_ID, null);
        }
        sql.addColumn(FIELD_CUSTOMER_TLOG_BATCH_IDENTIFIER, ID_CT_BATCH_DEFAULT);
        
        // Qualifiers
        sql.addQualifier(FIELD_CUSTOMER_ID + " = " + getCustomerID(artsCustomer));
        sql.addQualifier(FIELD_PARTY_ID + " = " + getPartyID(artsCustomer));

        try
        {
            dataConnection.execute(sql.getSQLString(), false);
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + e + "");
            throw new DataException(DataException.UNKNOWN, "updateCustomer", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Unable to update customer.");
        }
    }

    /**
     * Updates the contact table.
     * <P>
     * 
     * @param dataConnection The connection to the data source
     * @param artsCustomer The customer information to save
     * @exception DataException upon error
     */
    protected void updateContact(JdbcDataConnection dataConnection,
                                 ARTSCustomer artsCustomer)
        throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_CONTACT);

        // Fields
        sql.addColumn(FIELD_CONTACT_LAST_NAME, getLastName(artsCustomer));
        sql.addColumn(FIELD_CONTACT_FIRST_NAME, getFirstName(artsCustomer));
        sql.addColumn(FIELD_CONTACT_MIDDLE_INITIAL, getMiddleName(artsCustomer));
        sql.addColumn(FIELD_CONTACT_FULL_NAME, getCustomerName(artsCustomer));
        sql.addColumn(FIELD_CONTACT_SALUTATION, getSalutation(artsCustomer));
        sql.addColumn(FIELD_CONTACT_SUFFIX, getSuffix(artsCustomer));
        sql.addColumn(FIELD_CONTACT_BIRTH_DATE, getBirthDate(artsCustomer));
        sql.addColumn(FIELD_CONTACT_GENDER, getGender(artsCustomer));
        sql.addColumn(FIELD_CONTACT_COMPANY_NAME, getCompanyName(artsCustomer));
        sql.addColumn(FIELD_CONTACT_MAIL_FLAG, getMailPrivacy(artsCustomer));
        sql.addColumn(FIELD_CONTACT_PHONE_FLAG, getPhonePrivacy(artsCustomer));
        sql.addColumn(FIELD_CONTACT_EMAIL_FLAG, getEmailPrivacy(artsCustomer));
        sql.addColumn(FIELD_CONTACT_RECEIPT_PREFERENCE, artsCustomer.getPosCustomer().getReceiptPreference());

        // Qualifiers
        sql.addQualifier(FIELD_CONTACT_ID + " = " + getContactID(artsCustomer));
        sql.addQualifier(FIELD_CONTACT_TYPE_CODE + " = " + getContactType(CUSTOMER_CONTACT_TYPE));
        sql.addQualifier(FIELD_PARTY_ID + " = " + getPartyID(artsCustomer));

        try
        {
            dataConnection.execute(sql.getSQLString(), false);
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "updateContact", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            insertContact(dataConnection, artsCustomer);
        }
    }

    /**
     * Updates all email addresses.
     *
     * @param dataConnection The connection to the data source
     * @param artsCustomer The customer information to save
     * @exception DataException upon error
     */
    protected void updateEmailAddress(JdbcDataConnection dataConnection, ARTSCustomer artsCustomer)
            throws DataException
    {
        Iterator<EmailAddressIfc> emailAddresses = artsCustomer.getPosCustomer().getEmailAddresses();

        while (emailAddresses.hasNext())
        {
            EmailAddressIfc email = emailAddresses.next();
            updateEmailAddress(dataConnection, email, artsCustomer);
        }
    }

    /**
     * Updates all addresses.
     *
     * @param dataConnection The connection to the data source
     * @param artsCustomer The customer information to save
     * @exception DataException upon error
     */
    protected void updateAddress(JdbcDataConnection dataConnection, ARTSCustomer artsCustomer)
        throws DataException
    {
        for (AddressIfc address : artsCustomer.getPosCustomer().getAddressList())
        {
            updateAddress(dataConnection, address, artsCustomer);
        }
    }

    /**
     * Updates an address.
     *
     * @param dataConnection The connection to the data source
     * @param address The address entry to update
     * @param artsCustomer The customer information
     * @exception DataException upon error
     */
    protected void updateAddress(JdbcDataConnection dataConnection,
                                 AddressIfc address,
                                 ARTSCustomer artsCustomer)
        throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_ADDRESS);

        // Fields
        sql.addColumn(FIELD_CONTACT_ADDRESS_LINE_1, getAddressLine1(address));
        sql.addColumn(FIELD_CONTACT_ADDRESS_LINE_2, getAddressLine2(address));
        sql.addColumn(FIELD_CONTACT_ADDRESS_LINE_3, getAddressLine3(address));
        sql.addColumn(FIELD_CONTACT_CITY, getCity(address));
        sql.addColumn(FIELD_CONTACT_STATE, getState(address));
        sql.addColumn(FIELD_CONTACT_POSTAL_CODE, getPostalCode(address));
        sql.addColumn(FIELD_CONTACT_COUNTRY, getCountry(address));
        sql.addColumn(FIELD_CONTACT_LOCAL_TELEPHONE_NUMBER, getPhoneNumber(artsCustomer));

        // Qualifiers
        sql.addQualifier(FIELD_ADDRESS_ID + " = " + getAddressID(address));
        sql.addQualifier(FIELD_ADDRESS_TYPE_CODE + " = " + getAddressType(address));
        sql.addQualifier(FIELD_PARTY_ID + " = " + getPartyID(artsCustomer));

        try
        {
            dataConnection.execute(sql.getSQLString(), false);
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "updateAddress", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            insertAddress(dataConnection, address, artsCustomer);
        }
    }

    /**
     * Updates an email address.
     *
     * @param dataConnection The connection to the data source
     * @param email The email address entry to update
     * @param artsCustomer The customer information
     * @exception DataException upon error
     */
    protected void updateEmailAddress(JdbcDataConnection dataConnection,
                                      EmailAddressIfc email,
                                      ARTSCustomer artsCustomer)
        throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_EMAIL_ADDRESS);

        // Fields
        sql.addColumn(FIELD_EMAIL_ADDRESS, getEmailAddress(email));

        // Qualifiers
        sql.addQualifier(FIELD_PARTY_ID + " = " + getPartyID(artsCustomer));
        sql.addQualifier(FIELD_EMAIL_ADDRESS_TYPE_CODE, getEmailAddressType(email));
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "updateEmailAddress", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            insertEmailAddress(dataConnection, email, artsCustomer);
        }
    }

    /**
     * Updates all phone numbers.
     *
     * @param dataConnection The connection to the data source
     * @param artsCustomer The customer information to save
     * @exception DataException upon error
     */
    protected void updatePhone(JdbcDataConnection dataConnection,
                               ARTSCustomer artsCustomer)
        throws DataException
    {

        List<PhoneIfc> phoneList = artsCustomer.getPosCustomer().getPhoneList();
        if (phoneList != null && phoneList.size() > 0)
        {
            for (PhoneIfc telephone : phoneList)
            {
                if (telephone != null)
                {
                   updatePhone(dataConnection, telephone, artsCustomer);
                }
            }
        }
    }

    /**
     * Updates a phone number.
     *
     * @param dataConnection The connection to the data source
     * @param phone The phone entry to update
     * @param artsCustomer The customer information
     * @exception DataException upon error
     */
    protected void updatePhone(JdbcDataConnection dataConnection,
                               PhoneIfc phone,
                               ARTSCustomer artsCustomer)
        throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_PHONE);

        // Fields
        sql.addColumn(FIELD_CONTACT_LOCAL_TELEPHONE_NUMBER, getPhoneNumber(phone));
        sql.addColumn(FIELD_CONTACT_EXTENSION, getExtension(phone));

        // Qualifiers
        sql.addQualifier(FIELD_PHONE_ID + " = " + getPhoneID(phone));
        sql.addQualifier(FIELD_PARTY_ID + " = " + getPartyID(artsCustomer));
        sql.addQualifier(FIELD_PHONE_TYPE + " = " + getPhoneType(phone));

        try
        {
            dataConnection.execute(sql.getSQLString(), false);
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "updatePhone", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            insertPhone(dataConnection, phone, artsCustomer);
        }
    }

    /**
     * Inserts the specified email address object into the email address table.
     *
     * @param dataConnection The connection to the data source
     * @param address The email address to insert
     * @param artsCustomer The customer
     * @exception DataException upon error
     */
    public void insertEmailAddress(JdbcDataConnection dataConnection,
                                   EmailAddressIfc address,
                                   ARTSCustomer artsCustomer)
        throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_EMAIL_ADDRESS);

        // Fields
        sql.addColumn(FIELD_PARTY_ID, getPartyID(artsCustomer));
        sql.addColumn(FIELD_EMAIL_ADDRESS_TYPE_CODE, getEmailAddressType(address));
        sql.addColumn(FIELD_EMAIL_ADDRESS, getEmailAddress(address));

        try
        {
            dataConnection.execute(sql.getSQLString(), false);
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "insertEmailAddress", e);
        }
    }

    /**
     * Inserts the specified address object into the address table.
     *
     * @param dataConnection The connection to the data source
     * @param address The address to insert
     * @param artsCustomer The customer
     * @exception DataException upon error
     */
    public void insertAddress(JdbcDataConnection dataConnection, AddressIfc address,
            ARTSCustomer artsCustomer)
        throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_ADDRESS);

        // Fields
        sql.addColumn(FIELD_ADDRESS_ID, getAddressID(address));
        sql.addColumn(FIELD_PARTY_ID, getPartyID(artsCustomer));
        sql.addColumn(FIELD_ADDRESS_TYPE_CODE, getAddressType(address));
        sql.addColumn(FIELD_CONTACT_ADDRESS_LINE_1, getAddressLine1(address));
        if (getAddressLine2(address) !=null)
        {
           sql.addColumn(FIELD_CONTACT_ADDRESS_LINE_2, getAddressLine2(address));
        }
        if (getAddressLine3(address) !=null)
        {
           sql.addColumn(FIELD_CONTACT_ADDRESS_LINE_3, getAddressLine3(address));
        }
        sql.addColumn(FIELD_CONTACT_CITY, getCity(address));
        sql.addColumn(FIELD_CONTACT_STATE, getState(address));
        sql.addColumn(FIELD_CONTACT_POSTAL_CODE, getPostalCode(address));
        sql.addColumn(FIELD_CONTACT_COUNTRY, getCountry(address));
        sql.addColumn(FIELD_CONTACT_LOCAL_TELEPHONE_NUMBER, getPhoneNumber(artsCustomer));

        try
        {
            dataConnection.execute(sql.getSQLString(), false);
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "insertAddress", e);
        }
    }

    /**
     * Inserts all phone numbers into the phone number table.
     *
     * @param dataConnection The connection to the data source
     * @param artsCustomer The customer information to save
     * @exception DataException upon error
     */
    protected void insertPhone(JdbcDataConnection dataConnection,
                               ARTSCustomer artsCustomer)
        throws DataException
    {
        List<PhoneIfc> phoneList = artsCustomer.getPosCustomer().getPhoneList();
        // convert data to map to ensure there is only one phone per type.
        TreeMap<Integer,PhoneIfc> phoneMap = new TreeMap<Integer,PhoneIfc>();
        if (phoneList != null)
        {
            for (PhoneIfc phone : phoneList)
            {
                if (phone != null)
                {
                    phoneMap.put(phone.getPhoneType(), phone);
                }
            }
        }

        // parse the map and insert the phones
        Iterator<Integer> phoneTypes = phoneMap.keySet().iterator();
        while (phoneTypes.hasNext())
        {
            insertPhone(dataConnection, phoneMap.get(phoneTypes.next()), artsCustomer);
        }
    }

    /**
     * Inserts a phone entry into the phone table.
     *
     * @param dataConnection The connection to the data source
     * @param phone The phone entry to insert
     * @param artsCustomer The customer
     * @exception DataException upon error
     */
    protected void insertPhone(JdbcDataConnection dataConnection,
                               PhoneIfc phone,
                               ARTSCustomer artsCustomer)
        throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_PHONE);

        // Fields
        sql.addColumn(FIELD_PHONE_ID, getPhoneID(phone));
        sql.addColumn(FIELD_PARTY_ID, getPartyID(artsCustomer));
        sql.addColumn(FIELD_PHONE_TYPE, getPhoneType(phone));
        sql.addColumn(FIELD_CONTACT_LOCAL_TELEPHONE_NUMBER, getPhoneNumber(phone));
        sql.addColumn(FIELD_CONTACT_EXTENSION, getExtension(phone));

        try
        {
            dataConnection.execute(sql.getSQLString(), false);
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "insertPhone", e);
        }
    }

    /**
     * Inserts customer groups.
     *
     * @param dataConnection The connection to the data source
     * @param customer The customer
     * @exception DataException thrown if error occurs
     */
    protected void insertGroups(JdbcDataConnection dataConnection,
                                ARTSCustomer customer)
        throws DataException
    {
        CustomerGroupIfc[] customerGroups =
            customer.getPosCustomer().getCustomerGroups();
        int numRules = 0;
        if (customerGroups != null)
        {
            numRules = customerGroups.length;
        }
        for (int i = 0; i < numRules; i++)
        {
            insertGroup(dataConnection,
                        getCustomerID(customer),
                        customerGroups[i]);
        }
    }

    /**
     * Inserts a CustomerGroup record. This puts a row in the
     * CustomerAffiliation table.
     *
     * @param dataConnection The connection to the data source
     * @param customerIDString SQL-ready customer identifier string
     * @param customerGroup CustomerGroupIfc object
     * @exception DataException thrown if error occurs
     */
    protected void insertGroup(JdbcDataConnection dataConnection,
                               String customerIDString,
                               CustomerGroupIfc customerGroup)
        throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_CUSTOMER_AFFILIATION);

        // Fields
        sql.addColumn(FIELD_CUSTOMER_ID, customerIDString);
        sql.addColumn(FIELD_CUSTOMER_GROUP_ID, customerGroup.getGroupID());
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "insertGroup", e);
        }

    }

    /**
     * Updates customer group record by deleting matching records and then
     * inserting new records.
     *
     * @param dataConnection The connection to the data source
     * @param customer The customer
     * @exception DataException thrown if error occurs
     */
    protected void updateGroups(JdbcDataConnection dataConnection,
            ARTSCustomer customer)
        throws DataException
    {
        try
        {
            deleteCustomerGroups(dataConnection, customer);
        }
        // if data not found, it's OK
        catch (DataException de)
        {
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                logger.error(de);
                throw de;
            }
        }

        // insert groups
        insertGroups(dataConnection, customer);
    }

    /**
     * Deletes all customer group records matching customer ID.
     *
     * @param dataConnection The connection to the data source
     * @param customer The customer
     * @exception DataException thrown if error occurs
     */
    protected void deleteCustomerGroups(JdbcDataConnection dataConnection,
                                        ARTSCustomer customer)
        throws DataException
    {
        SQLDeleteStatement sql = new SQLDeleteStatement();

        // table
        sql.setTable(TABLE_CUSTOMER_AFFILIATION);

        // qualifier
        sql.addQualifier
            (FIELD_CUSTOMER_ID + " = " + getCustomerID(customer));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "deleteCustomerGroups", e);
        }
    }

    /**
     * Inserts discount rules.
     *
     * @param dataConnection The connection to the data source
     * @param customer The customer
     * @exception DataException thrown if error occurs
     */
    protected void insertDiscountRules(JdbcDataConnection dataConnection,
                                       ARTSCustomer customer)
        throws DataException
    {
        CustomerGroupIfc[] groups = customer.getPosCustomer().getCustomerGroups();
        DiscountRuleIfc[] discountRules = null;

        if (groups != null && groups.length > 0)
        {
            discountRules = groups[0].getDiscountRules();
        }

        int numRules = 0;
        if (discountRules != null)
        {
            numRules = discountRules.length;
            for (int i = 0; i < numRules; i++)
            {
                insertDiscountRule(dataConnection,
                                   getCustomerID(customer),
                                   discountRules[i]);
            }
        }

    }

    /**
     * Inserts a DiscountRule record. This puts a row in the
     * CustomerAffiliationPriceDerivationRuleEligibility table.
     *
     * @param dataConnection The connection to the data source
     * @param customerIDString SQL-ready customer identifier string
     * @param discountRule DiscountRuleIfc object
     * @exception DataException thrown if error occurs
     */
    protected void insertDiscountRule(JdbcDataConnection dataConnection,
                                      String customerIDString,
                                      DiscountRuleIfc discountRule)
        throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_CUSTOMER_AFFILIATION_PRICE_DERIVATION_RULE_ELIGIBILITY);

        // Fields
        sql.addColumn(FIELD_CUSTOMER_ID, customerIDString);
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ID, discountRule.getRuleID());
        sql.addColumn(FIELD_RETAIL_STORE_ID, "' + STORE_ID_CORPORATE + '");
        sql.addColumn(FIELD_CUSTOMER_GROUP_ID, "0");
        // these columns aren't really needed; they are also found in RU_PRDV
        sql.addColumn(FIELD_CUSTOMER_AFFILIATION_PRICE_DERIVATION_ELIGIBLE_EFFECTIVE_DATE_TIMESTAMP,
                      dateToSQLTimestampString(discountRule.getEffectiveDate()));
        sql.addColumn(FIELD_CUSTOMER_AFFILIATION_PRICE_DERIVATION_ELIGIBLE_EXPIRATION_DATE_TIMESTAMP,
                      dateToSQLTimestampString(discountRule.getExpirationDate()));
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "insertDiscountRule", e);
        }
    }

    /**
     * Updates discount rule record by deleting matching records and then
     * inserting new records.
     *
     * @param dataConnection The connection to the data source
     * @param customer The customer
     * @exception DataException thrown if error occurs
     */
    protected void updateDiscountRules(JdbcDataConnection dataConnection,
                                       ARTSCustomer customer)
        throws DataException
    {
        CustomerGroupIfc[] groups = customer.getPosCustomer().getCustomerGroups();
        DiscountRuleIfc[] discountRules = null;

        if (groups != null && groups.length > 0)
        {
            discountRules = groups[0].getDiscountRules();
        }

        try
        {
            deleteDiscountRules(dataConnection,
                                customer);
        }
        // if data not found, it's OK
        catch (DataException de)
        {
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                logger.error(de);
                throw de;
            }
        }

        if (discountRules != null)
        {
            insertDiscountRules(dataConnection,customer);
        }
    }

    /**
     * Deletes all discount rule records matching customer ID.
     *
     * @param dataConnection The connection to the data source
     * @param customer The customer
     * @exception DataException thrown if error occurs
     */
    protected void deleteDiscountRules(JdbcDataConnection dataConnection,
                                       ARTSCustomer customer)
        throws DataException
    {
        SQLDeleteStatement sql = new SQLDeleteStatement();

        // table
        sql.setTable(TABLE_CUSTOMER_AFFILIATION_PRICE_DERIVATION_RULE_ELIGIBILITY);

        // qualifier
        sql.addQualifier
            (FIELD_CUSTOMER_ID + " = " + getCustomerID(customer));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "deleteDiscountRules", e);
        }

    }

    /**
     * Inserts into the business customer table.
     *
     * @param dataConnection The connection to the data source
     * @param artsCustomer The customer information to save
     * @exception DataException upon error
     */
    protected void insertBusinessInfo(JdbcDataConnection dataConnection,
                                      ARTSCustomer artsCustomer)
        throws DataException
    {
        if (artsCustomer.getPosCustomer().isBusinessCustomer())
        {
            SQLInsertStatement sql = new SQLInsertStatement();

            // Table
            sql.setTable(TABLE_BUS_CUSTOMER);

            // Fields
            sql.addColumn(FIELD_BUS_CUSTOMER_ID, getContactID(artsCustomer));
            sql.addColumn(FIELD_PARTY_ID, getPartyID(artsCustomer));
            sql.addColumn(FIELD_BUS_CUSTOMER_NAME, getCustomerName(artsCustomer));
            sql.addColumn(FIELD_MSK_TAX_EXEMPT_CERTIFICATE,
                          getTaxCertificate(artsCustomer));
            sql.addColumn(FIELD_EXEMPTION_REASON,
                          getExemptionReason(artsCustomer));

            try
            {
                dataConnection.execute(sql.getSQLString());
            }
            catch (DataException de)
            {
                logger.error(de);
                throw de;
            }
            catch (Exception e)
            {
                logger.error(
                            e);
                throw new DataException(DataException.UNKNOWN,
                                        "insertBusinessInfo", e);
            }
        }
    }

    /**
     * Updates the business customer table.
     *
     * @param dataConnection The connection to the data source
     * @param artsCustomer The customer information to save
     * @exception DataException upon error
     */
    protected void updateBusinessInfo(JdbcDataConnection dataConnection,
                                      ARTSCustomer artsCustomer)
        throws DataException
    {
        if (artsCustomer.getPosCustomer().isBusinessCustomer())
        {
            SQLUpdateStatement sql = new SQLUpdateStatement();

            // Table
            sql.setTable(TABLE_BUS_CUSTOMER);

            // Fields
            sql.addColumn(FIELD_BUS_CUSTOMER_NAME, getCustomerName(artsCustomer));
            sql.addColumn(FIELD_MSK_TAX_EXEMPT_CERTIFICATE,
                          getTaxCertificate(artsCustomer));
            sql.addColumn(FIELD_EXEMPTION_REASON,
                          getExemptionReason(artsCustomer));

            // Qualifiers
            sql.addQualifier(FIELD_BUS_CUSTOMER_ID + " = " +
                             getContactID(artsCustomer));
            sql.addQualifier(FIELD_PARTY_ID + " = " +
                             getPartyID(artsCustomer));

            try
            {
                dataConnection.execute(sql.getSQLString());
            }
            catch (DataException de)
            {
                logger.error(
                            de);
                throw de;
            }
            catch (Exception e)
            {
                logger.error(
                            e);
                throw new DataException(DataException.UNKNOWN,
                                        "updateContact", e);
            }
        }
    }

    /**
     * Returns the party ID.
     *
     * @param customer The customer
     * @return the party ID.
     */
    public String getPartyID(ARTSCustomer customer)
    {
        return (String.valueOf(customer.getPartyId()));
    }

    /**
     * Returns the party type.
     *
     * @param partyType The party type
     * @return the party type.
     */
    public String getPartyType(String partyType)
    {
        return (makeSafeString(partyType));
    }

    /**
     * Returns the customer ID.
     *
     * @param customer The customer
     * @return the customer ID.
     */
    public String getCustomerID(ARTSCustomer customer)
    {
        return (makeSafeString(customer.getPosCustomer().getCustomerID()));
    }

    /**
     * For new customers, customerID is generated by a combination of
     * customerIDPrefix and partyID. This method does that concatenation and
     * returns the result in a database safe format. CustomerIDPrefix is
     * generally storeID.
     *
     * @param customer ARTSCustomer object
     * @return new customerID.
     * @since NEP67
     */
    public String generateCustomerID(ARTSCustomer customer)
    {
        String storeID = customer.getPosCustomer().getCustomerIDPrefix();
        String partyID = String.valueOf(customer.getPartyId());
        return storeID + partyID;

    }

    /**
     * Returns the customer Locale.
     *
     * @param customer The customer
     * @return the customer ID.
     */
    public String getCustomerLocale(ARTSCustomer customer)
    {
        String locale = null;
        if ( customer.getPosCustomer().getPreferredLocale() != null)
        {
            locale = makeSafeString(customer.getPosCustomer().getPreferredLocale().toString());
        }
        return (locale);
    }

    /**
     * Returns the group ID.
     *
     * @param customer The customer
     * @return the group ID.
     */
    public String getGroupID(ARTSCustomer customer)
    {
        String returnString = "";
        if (customer.getPosCustomer().getCustomerGroups() != null)
        {
            CustomerGroupIfc[] groups = customer.getPosCustomer().getCustomerGroups();
            if (groups != null &&   groups.length > 0)
            {
              returnString =groups[0].getGroupID();
            }
        }
        return(returnString);
    }

    /**
     * Returns the customer full name.
     *
     * @param customer The customer
     * @return the customer full name.
     */
    public String getCustomerName(ARTSCustomer customer)
    { // CustomerIfc should extend or contain PersonNameIfc
        return makeSafeString(customer.getPosCustomer().getFirstLastName());
    }

    /**
     * Returns the customer status.
     *
     * @param customer The customer
     * @return the customer status.
     */
    public String getCustomerStatus(ARTSCustomer customer)
    {
        return(String.valueOf(customer.getPosCustomer().getStatus()));
    }

    /**
     * Returns the employee ID.
     *
     * @param customer The customer
     * @return the employee ID.
     */
    public String getEmployeeID(ARTSCustomer customer)
    {
        return(makeSafeString(customer.getPosCustomer().getEmployeeID()));
    }

    /**
     * Returns the encrypted house account number.
     *
     * @param customer The customer
     * @return the encrypted house account number.
     */
    public String getHouseAccountNumber(ARTSCustomer customer)
    {
        return(makeSafeString(customer.getPosCustomer().getHouseCardData().getEncryptedAcctNumber()));
    }

    /**
     * Returns the masked house account number.
     *
     * @param customer The customer
     * @return the masked house account number.
     */
    public String getMaskedHouseAccountNumber(ARTSCustomer customer)
    {
        return (makeSafeString(customer.getPosCustomer().getHouseCardData().getMaskedAcctNumber()));
    }
    
    /**
     * Returns the masked house account number.
     *
     * @param customer The customer
     * @return the masked house account number.
     */
    public int getCustomerPreferredReceiptMode(ARTSCustomer customer)
    {
        return customer.getPosCustomer().getReceiptPreference();
    }

    /**
     * Returns the contact ID.
     *
     * @param customer The customer
     * @return the contact ID.
     */
    public String getContactID(ARTSCustomer customer)
    {
        return (String.valueOf(customer.getPartyId()));
    }

    /**
     * Returns the contact type.
     *
     * @param type The contact type
     * @return the contact type.
     */
    public String getContactType(String type)
    {
        return (makeSafeString(type));
    }

    /**
     * Returns the customer last name.
     *
     * @param customer The customer
     * @return the customer last name.
     */
    public String getLastName(ARTSCustomer customer)
    {
        return (makeSafeString(customer.getPosCustomer().getLastName()));
    }

    /**
     * Returns the customer first name.
     *
     * @param customer The customer
     * @return the customer first name.
     */
    public String getFirstName(ARTSCustomer customer)
    {
        return (makeSafeString(customer.getPosCustomer().getFirstName()));
    }

    /**
     * Returns the customer middle name.
     *
     * @param customer The customer
     * @return the customer middle name.
     */
    public String getMiddleName(ARTSCustomer customer)
    {
        return (makeSafeString(customer.getPosCustomer().getMiddleName()));
    }

    /**
     * Returns the customer salutation.
     *
     * @param customer The customer
     * @return the customer salutation.
     */
    public String getSalutation(ARTSCustomer customer)
    {
        return (makeSafeString(customer.getPosCustomer().getSalutation()));
    }

    /**
     * Returns the customer suffix.
     *
     * @param customer The customer
     * @return the customer suffix.
     */
    public String getSuffix(ARTSCustomer customer)
    {
        return (makeSafeString(customer.getPosCustomer().getNameSuffix()));
    }

    /**
     * Returns the customer birth date.
     *
     * @param customer The customer
     * @return the customer birth date.
     */
    public String getBirthDate(ARTSCustomer customer)
    {
        return (makeSafeString(customer.getPosCustomer().getBirthDateAsString()));
    }

    /**
     * Returns the customer gender.
     *
     * @param customer The customer
     * @return the customer gender.
     */
    public String getGender(ARTSCustomer customer)
    {
        return (String.valueOf(customer.getPosCustomer().getGenderCode()));
    }

    /**
     * Returns the customer company name.
     *
     * @param customer The customer
     * @return the customer company name.
     */
    public String getCompanyName(ARTSCustomer customer)
    {
        return (makeSafeString(customer.getPosCustomer().getCompanyName()));
    }

    /**
     * Returns the customer mail privacy.
     *
     * @param customer The customer
     * @return the customer mail privacy.
     */
    public String getMailPrivacy(ARTSCustomer customer)
    {
        return (makeStringFromBoolean(customer.getPosCustomer().getMailPrivacy()));
    }

    /**
     * Returns the customer phone privacy.
     *
     * @param customer The customer
     * @return the customer phone privacy.
     */
    public String getPhonePrivacy(ARTSCustomer customer)
    {
        return (makeStringFromBoolean(customer.getPosCustomer().getTelephonePrivacy()));
    }

    /**
     * Returns the customer email privacy.
     *
     * @param customer The customer
     * @return the customer email privacy.
     */
    public String getEmailPrivacy(ARTSCustomer customer)
    {
        return (makeStringFromBoolean(customer.getPosCustomer().getEMailPrivacy()));
    }

    /**
     * Returns the address ID.
     *
     * @param address The address
     * @return the address ID.
     */
    public String getAddressID(AddressIfc address)
    {
        return (String.valueOf(address.getAddressType()));
    }

    /**
     * Returns the address type.
     *
     * @param address The address
     * @return the address type.
     */
    public String getAddressType(AddressIfc address)
    {
        return (makeSafeString(String.valueOf(address.getAddressType())));
    }

    /**
     * Returns the email address
     *
     * @param email The address
     * @return the first address line.
     */
    public String getEmailAddress(EmailAddressIfc email)
    {
        String value = null;
        if (email != null && !Util.isEmpty(email.getEmailAddress()))
        {
            value = makeSafeString(email.getEmailAddress());
        }
        return (value);
    }
    

    /**
     * Returns the first address line.
     *
     * @param address The address
     * @return the first address line.
     */
    public String getAddressLine1(AddressIfc address)
    {
        String value = null;
        Vector<String> addressLines = address.getLines();
        if (addressLines != null && addressLines.size() > 0)
        {
            value = addressLines.get(0);
        }
        return (makeSafeString(value));
    }

    /**
     * Returns the second address line.
     *
     * @param address The address
     * @return the second address line.
     */
    public String getAddressLine2(AddressIfc address)
    {
        String value = null;
        Vector<String> addressLines = address.getLines();
        if (addressLines != null && addressLines.size() > 1)
        {
            value = addressLines.get(1);
        }
        return (makeSafeString(value, true));
    }

    /**
     * Returns the third address line.
     *
     * @param address The address
     * @return the second address line.
     */
    public String getAddressLine3(AddressIfc address)
    {
        String value = null;
        Vector<String> addressLines = address.getLines();
        if (addressLines != null && addressLines.size() > 2)
        {
            value = addressLines.get(2);
        }
        return (makeSafeString(value));
    }

    /**
     * Returns the address city.
     *
     * @param address The address
     * @return the address city.
     */
    public String getCity(AddressIfc address)
    {
        return (makeSafeString(address.getCity()));
    }

    /**
     * Returns the address state.
     *
     * @param address The address
     * @return the address state.
     */
    public String getState(AddressIfc address)
    {
        return (makeSafeString(address.getState()));
    }

    /**
     * Returns the address postal code.
     *
     * @param address The address
     * @return the address postal code.
     */
    public String getPostalCode(AddressIfc address)
    {
        StringBuffer postalCodeString = new StringBuffer(address.getPostalCode());

        return (makeSafeString(postalCodeString.toString()));
    }

    /**
     * Returns the address country.
     *
     * @param address The address
     * @return the address country.
     */
    public String getCountry(AddressIfc address)
    {
        return (makeSafeString(address.getCountry()));
    }

    /**
     * Returns the local portion of the phone number.
     *
     * @param customer The customer
     * @return the phone number.
     */
    public String getPhoneNumber(ARTSCustomer customer)
    {
        String value = null;
        int type = PhoneConstantsIfc.PHONE_TYPE_HOME;

        if (customer.getPosCustomer().isBusinessCustomer())
        {
            type = PhoneConstantsIfc.PHONE_TYPE_WORK;
        }
        PhoneIfc phone = customer.getPosCustomer().getPhoneByType(type);
        if (phone != null)
        {
            value = phone.getPhoneNumber();
        }

        return (makeSafeString(value));
    }

    /**
     * Returns the email address type.
     *
     * @param email email address
     * @return the email type.
     */
    public String getEmailAddressType(EmailAddressIfc email)
    {
        return (String.valueOf(email.getEmailAddressType()));
    }

    /**
     * Returns the phone ID.
     *
     * @param phone The phone
     * @return the phone ID.
     */
    public String getPhoneID(PhoneIfc phone)
    {
        return (String.valueOf(phone.getPhoneType()));
    }

    /**
     * Returns the phone type.
     *
     * @param phone The phone
     * @return the phone type.
     */
    public String getPhoneType(PhoneIfc phone)
    {
        return (makeSafeString(String.valueOf(phone.getPhoneType())));
    }

    /**
     * Returns the phone number.
     *
     * @param phone The phone
     * @return the phone number.
     */
    public String getPhoneNumber(PhoneIfc phone)
    {
        return (makeSafeString(phone.getPhoneNumber()));
    }

    /**
     * Returns the phone extension.
     *
     * @param phone The phone
     * @return the phone extension.
     */
    public String getExtension(PhoneIfc phone)
    {
        return (makeSafeString(phone.getExtension()));
    }

    /**
     * Returns the tax certificate.
     *
     * @param customer The customer
     * @return the tax certificate.
     */
    public String getTaxCertificate(ARTSCustomer customer)
    {
        return (makeSafeString(customer.getPosCustomer().getEncipheredTaxCertificate().getEncryptedNumber()));
    }
    
    /**
     * Returns the masked Tax Certificate.
     * @param customer
     * @return masked tax certificate
     */
    public String getMaskedTaxCertificate(ARTSCustomer customer)
    {
        return (makeSafeString(customer.getPosCustomer().getEncipheredTaxCertificate().getMaskedNumber()));
    }

    /**
     * Returns the exemption reason.
     *
     * @param customer The customer
     * @return the exemption reason.
     */
    public String getExemptionReason(ARTSCustomer customer)
    {
        return (makeSafeString(customer.getPosCustomer().getTaxExemptionReason().getCode()));
    }

    /**
     * This method return Tax ID
     * @deprecated as of 13.4. No callers.
     */
    public String getEncipheredTaxID(ARTSCustomer customer)
    {
        return (makeSafeString(""));
    }
    
    /**
     * This method return encrypted Tax ID
     */
    public String getEncryptedCustomerTaxID(ARTSCustomer customer)
    {
        String acctNo = customer.getPosCustomer().getEncipheredTaxID().getEncryptedNumber();
        if (StringUtils.isNotEmpty(acctNo))
        {
            return makeSafeString(acctNo);
        }
        return makeSafeString("");
    }
    
    /**
     * This method return hashed Tax ID
     * @deprecated As of 14.0, do not use.
     */
    public String getHashedCustomerTaxID(ARTSCustomer customer)
    {
        String hashedTaxId = "";
        byte[] taxID = null;
        if ((StringUtils.isNotEmpty(customer.getPosCustomer().getEncipheredTaxID().getEncryptedNumber())))
        {
            try
            {
                
                KeyStoreEncryptionManagerIfc encryptionManager = (KeyStoreEncryptionManagerIfc) Gateway.getDispatcher()
                        .getManager(KeyStoreEncryptionManagerIfc.TYPE);
                taxID = customer.getPosCustomer().getEncipheredTaxID().getDecryptedNumber();
                hashedTaxId = new String(JdbcUtilities.base64encode(encryptionManager.hash(taxID)));
            }
            catch (Exception e)
            {
                logger.error("Couldn't create hash tax id", e);
            }
            finally
            {
                Util.flushByteArray(taxID);
            }
            return makeSafeString(hashedTaxId);
        }

        return makeSafeString("");
    }
    
    /**
     * This method return masked Tax ID
     */
    public String getMaskedCustomerTaxID(ARTSCustomer customer)
    {
        String maskedNo = customer.getPosCustomer().getEncipheredTaxID().getMaskedNumber();
        if (StringUtils.isNotEmpty(maskedNo))
            return makeSafeString(maskedNo);
        
        return makeSafeString("");
    }
}
